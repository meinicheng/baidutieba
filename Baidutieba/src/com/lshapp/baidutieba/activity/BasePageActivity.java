package com.lshapp.baidutieba.activity;

import android.os.Bundle;

/**
 * @author kingofglory email: kingofglory@yeah.net blog: http:www.google.com
 * @date 2014-2-21 TODO
 */

public abstract class BasePageActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle bundle) {
		// TODO Auto-generated method stub
		super.onCreate(bundle);
		setLayoutView();
		init(bundle);
	}

	private void init(Bundle bundle) {
		// TODO Auto-generated method stub
		findViews();
		setupViews(bundle);
		setListener();
		fetchData();
	}

	protected abstract void setLayoutView();

	protected abstract void findViews();

	protected abstract void setupViews(Bundle bundle);

	protected abstract void setListener();

	protected abstract void fetchData();

}
