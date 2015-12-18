package com.lshapp.baidutieba.activity;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.widget.TextView;
import cn.bmob.v3.BmobUser;

import com.lshapp.baidutieba.R;
import com.lshapp.baidutieba.adapter.ReadtieziViewPager;
import com.lshapp.baidutieba.base.User;
import com.lshapp.baidutieba.fragment.TieZiHuiFufragment;
import com.lshapp.baidutieba.fragment.TieZiZhuTifragment;
import com.viewpagerindicator.TabPageIndicator;

public class ReadTieziActivity extends FragmentActivity {
	private TabPageIndicator titleIndicator;
	private List<Fragment> listfragment;
	private ReadtieziViewPager adapter;
	private ViewPager pager;
	private TextView rhuifu;
	private User user;
	private String[] titlestrs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.readtiezilayout);
		user = (User) getIntent().getSerializableExtra("listdata");
		initView();
		User cUser = BmobUser
				.getCurrentUser(ReadTieziActivity.this, User.class);
		if (user.getUsername().equals(cUser.getUsername())) {
			titlestrs = new String[] { "我的主题", "我的回复" };
			rhuifu.setText("我的帖子");
		} else {
			rhuifu.setText("他的帖子");
			titlestrs = new String[] { "他的主题", "他的回复" };
		}

		adapter = new ReadtieziViewPager(getSupportFragmentManager(),
				listfragment, titlestrs);
		pager.setAdapter(adapter);
		titleIndicator.setViewPager(pager);

	}

	private void initView() {

		rhuifu = (TextView) findViewById(R.id.rhuifu);
		titleIndicator = (TabPageIndicator) findViewById(R.id.titles);
		pager = (ViewPager) findViewById(R.id.tzviewpager);
		listfragment = new ArrayList<Fragment>();
		TieZiZhuTifragment zhuTifragment = new TieZiZhuTifragment(user);
		TieZiHuiFufragment huiFufragment = new TieZiHuiFufragment(user);
		listfragment.add(zhuTifragment);
		listfragment.add(huiFufragment);

	}

}
