package lv.bizapps.lvsecttest;

import java.util.*;

import lv.bizapps.lvsecttest.Main.CustomListViewAdapter.TranslatedPos;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

class Category {
	public String title;

	public List<Forum> forums = new ArrayList<Forum>();
}

class Forum {
	public String title;
}

public class Main extends ActionBarActivity {
	protected List<Category> items = new ArrayList<Category>();

	protected ListView listView;
	protected ProgressBar pb;

	protected CustomListViewAdapter clva;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.e("AA", "ON CREATE EVENT");

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		clva = new CustomListViewAdapter(getApplicationContext());

		pb = (ProgressBar)findViewById(R.id.progressBar1);
		pb.setVisibility(View.VISIBLE);

		listView = (ListView)findViewById(R.id.listView1);
		listView.setAdapter(clva);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> av, View view, int id1, long id2) {
				TranslatedPos pos = clva.translateIndex(id1);

				Log.e("AAA", "TP CLICK: "+pos.categoryIdx+":"+pos.forumIdx);
			}
		});

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(3000);
				}
				catch(Exception e) {
					e.printStackTrace();
				}

				for(int i=0; i<3; i++) {
					Category c = new Category();

					c.title = "Category item #"+(i+1)+" title";

					for(int j=0; j<Utils.randInt(1, 10); j++) {
						Forum f = new Forum();

						f.title = "Cat #"+(i+1)+". Forum item #"+(j+1)+" title";

						c.forums.add(f);
					}

					items.add(c);
				}

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						pb.setVisibility(View.GONE);
						clva.notifyDataSetChanged();
					}
				});
			}
		}).start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater mi = getMenuInflater();
		mi.inflate(R.menu.main, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case R.id.action_menu_1:Log.e("AAA", "MENU 1");
									return true;

			case R.id.action_quit:	System.exit(1);
									return true;

			default: 				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		Log.e("AAA", "CONF CHANGED");
	}

	class CustomListViewAdapter extends BaseAdapter {
		protected Context ctx;
		protected LayoutInflater li;

		public CustomListViewAdapter(Context ctx) {
			this.ctx = ctx;
			this.li = (LayoutInflater)this.ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			int count = 0;

			if(items != null) {
				for(int i=0; i<items.size(); i++) {
					count += 1;

					if(items.get(i).forums != null && items.get(i).forums.size() > 0) {
						count += items.get(i).forums.size();
					}
				}
			}

			return count;
		}

		@Override
		public boolean isEnabled(int position) {
			TranslatedPos pos = this.translateIndex(position);

			if(pos.categoryIdx > -1) {
				if(pos.forumIdx > -1) return true;
				else return false;
			}

			return false;
		}

		@Override
		public Object getItem(int id) {
			return id;
		}

		@Override
		public long getItemId(int id) {
			//Log.e("AAA", "GET_ITEM_ID: "+id);

			return id;
		}

		public TranslatedPos translateIndex(int idx) {
			TranslatedPos pos = new TranslatedPos();
			int categoryIdx = -1;
			int forumIdx = -1;
			int _pos = -1;

			for(int ci=0; ci<items.size(); ci++) {
				_pos += 1;
				if(_pos == idx) {
					pos.categoryIdx = ci;
					pos.forumIdx = -1;

					return pos;
				}

				for(int fi=0; fi<items.get(ci).forums.size(); fi++) {
					_pos += 1;
					if(_pos == idx) {
						pos.categoryIdx = ci;
						pos.forumIdx = fi;

						return pos;
					}
				}
			}

			return pos;
		}

		@Override
		public View getView(int position, View view, ViewGroup root) {
			TranslatedPos pos = this.translateIndex(position);

			if(pos.categoryIdx > -1) {
				if(pos.forumIdx > -1) {//render list item
					view = li.inflate(R.layout.list_item, root, false);

					TextView textTv = (TextView)view.findViewById(R.id.textView1);
					textTv.setText(items.get(pos.categoryIdx).forums.get(pos.forumIdx).title);
				}
				else {//render hsection header
					view = li.inflate(R.layout.header, root, false);

					TextView headerTv = (TextView)view.findViewById(R.id.header);
					headerTv.setText(items.get(pos.categoryIdx).title);
				}
			}

			return view;
		}

		class TranslatedPos {
			public int categoryIdx = -1;
			public int forumIdx = -1;
		}
	}
}