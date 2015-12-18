package com.lshapp.baidutieba.activity;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.FindListener;

import com.lshapp.baidutieba.R;
import com.lshapp.baidutieba.base.TieBaName;
import com.lshapp.baidutieba.base.User;

public class BanamelistActivity extends Activity {
	private User user;
	private ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.balistactivity);
		user = (User) getIntent().getSerializableExtra("listdata");
		listView = (ListView) findViewById(R.id.balistview);
		
		BmobQuery<TieBaName> query = new BmobQuery<TieBaName>();
		query.addWhereRelatedTo("usertbname", new BmobPointer(user));
		query.findObjects(BanamelistActivity.this,
				new FindListener<TieBaName>() {

					@Override
					public void onSuccess(List<TieBaName> arg0) {
						String[] arrays = new String[arg0.size()];
						for (int i = 0; i < arg0.size(); i++) {
							arrays[i] = arg0.get(i).getName();
						}
						listView.setAdapter(new ArrayAdapter<String>(
								BanamelistActivity.this,
								android.R.layout.simple_list_item_1, arrays));
					}

					@Override
					public void onError(int arg0, String arg1) {

					}
				});

	}
}
