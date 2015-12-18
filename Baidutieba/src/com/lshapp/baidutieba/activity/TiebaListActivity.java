package com.lshapp.baidutieba.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshBase.State;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lshapp.baidutieba.R;
import com.lshapp.baidutieba.adapter.AIContentAdapter;
import com.lshapp.baidutieba.base.QiangYu;
import com.lshapp.baidutieba.base.TieBaName;
import com.lshapp.baidutieba.base.User;
import com.lshapp.baidutieba.utils.BmobConstants;

public class TiebaListActivity extends Activity implements OnClickListener {
	private String tbname;
	private TextView lnametv;
	private ImageView limgxie;
	private LinearLayout newtieba;
	private LinearLayout tiebaload;
	private ImageView gifimg;
	private AnimationDrawable animaition;
	private TextView tvtiebaname;
	private Button btnewtieba;
	private PullToRefreshListView mPullRefreshListView;
	private boolean pullFromUser;
	private int pageNum;
	private String lastItemTime;// 当前列表结尾的条目的创建时间，
	private ArrayList<QiangYu> mListItems;
	private ListView actualListView;
	private TieBaName baName;
	private TieBaName tbaName;

	public enum RefreshType {
		REFRESH, LOAD_MORE
	}

	private RefreshType mRefreshType = RefreshType.LOAD_MORE;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tiebalist_activity);
		Intent intent = getIntent();
		tbname = intent.getStringExtra("tiebaname");
		initview();
		lnametv.setText(tbname);
		BmobQuery<TieBaName> query = new BmobQuery<TieBaName>();
		query.addWhereEqualTo("name", tbname);
		query.findObjects(this, new FindListener<TieBaName>() {
			@Override
			public void onSuccess(List<TieBaName> arg0) {
				// Toast.makeText(TiebaListActivity.this, arg0.size() + "成功",
				// Toast.LENGTH_SHORT).show();
				if (arg0.size() == 0) {
					// 显示创建贴吧view
					animaition.stop();
					tiebaload.setVisibility(View.GONE);
					tvtiebaname.setText(tbname);
					newtieba.setVisibility(View.VISIBLE);
				} else {
					// 显示贴吧listview
					animaition.stop();
					User user = BmobUser.getCurrentUser(TiebaListActivity.this,
							User.class);
					if (user != null) {
						BmobQuery<TieBaName> query = new BmobQuery<TieBaName>();
						query.addWhereRelatedTo("usertbname", new BmobPointer(
								user));
						query.findObjects(TiebaListActivity.this,
								new FindListener<TieBaName>() {

									@Override
									public void onSuccess(List<TieBaName> arg0) {
										for (int i = 0; i < arg0.size(); i++) {
											tbaName = new TieBaName();
											tbaName = arg0.get(i);
											String name = tbaName.getName();
											if (name.equals(tbname)) {
												hendweiguanzhu
														.setVisibility(View.GONE);
												hendyiguanzhu
														.setVisibility(View.VISIBLE);
												break;
											}
										}
									}

									@Override
									public void onError(int arg0, String arg1) {

									}
								});

					}

					baName = new TieBaName();
					baName = arg0.get(0);

					mPullRefreshListView.setMode(Mode.BOTH);
					mPullRefreshListView
							.setOnRefreshListener(new OnRefreshListener2<ListView>() {
								// 下拉刷新
								@Override
								public void onPullDownToRefresh(
										PullToRefreshBase<ListView> refreshView) {
									String label = DateUtils.formatDateTime(
											TiebaListActivity.this,
											System.currentTimeMillis(),
											DateUtils.FORMAT_SHOW_TIME
													| DateUtils.FORMAT_SHOW_DATE
													| DateUtils.FORMAT_ABBREV_ALL);
									refreshView.getLoadingLayoutProxy()
											.setLastUpdatedLabel(label);
									mPullRefreshListView.setMode(Mode.BOTH);
									pullFromUser = true;
									mRefreshType = RefreshType.REFRESH;
									pageNum = 0;
									lastItemTime = getCurrentTime();
									fetchData();
								}

								// 上拉加载
								@Override
								public void onPullUpToRefresh(
										PullToRefreshBase<ListView> refreshView) {
									mRefreshType = RefreshType.LOAD_MORE;
									fetchData();
								}
							});

					mPullRefreshListView
							.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

								@Override
								public void onLastItemVisible() {

								}
							});

				}

			}

			@Override
			public void onError(int arg0, String arg1) {
				Toast.makeText(TiebaListActivity.this, arg1 + "失败" + arg0,
						Toast.LENGTH_SHORT).show();
			}
		});
		actualListView = mPullRefreshListView.getRefreshableView();
		actualListView.addHeaderView(hendlayout);
		mListItems = new ArrayList<QiangYu>();
		mAdapter = new AIContentAdapter(this, mListItems);
		actualListView.setAdapter(mAdapter);

		if (mListItems.size() == 0) {
			fetchData();
		}

		hendweiguanzhu.setOnClickListener(this);
		mPullRefreshListView.setState(State.RELEASE_TO_REFRESH, true);
		actualListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				// MyApplication.getInstance().setCurrentQiangYu(mListItems.get(position-1));
				Intent intent = new Intent();
				intent.setClass(TiebaListActivity.this, CommentActivity.class);
				intent.putExtra("data", mListItems.get(position - 2));
				intent.putExtra("tbname", tbname);
				startActivityForResult(intent, 2);
			}
		});
	}

	private void initview() {
		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);

		lnametv = (TextView) findViewById(R.id.lnametv);
		limgxie = (ImageView) findViewById(R.id.limgxie);
		newtieba = (LinearLayout) findViewById(R.id.newtieba);
		tvtiebaname = (TextView) findViewById(R.id.tv_tiebaname);
		tiebaload = (LinearLayout) findViewById(R.id.tiebaload);
		;
		gifimg = (ImageView) findViewById(R.id.gifimg);
		btnewtieba = (Button) findViewById(R.id.bt_newtieba);

		hendlayout = (LinearLayout) LayoutInflater.from(this).inflate(
				R.layout.tiebalisthend, null);
		hendtiebaname = (TextView) hendlayout.findViewById(R.id.hendtiebaname);
		hendsize = (TextView) hendlayout.findViewById(R.id.hendsize);
		hendweiguanzhu = (Button) hendlayout.findViewById(R.id.hendweiguanzhu);
		hendyiguanzhu = (Button) hendlayout.findViewById(R.id.hendyiguanzhu);

		limgxie.setOnClickListener(this);
		btnewtieba.setOnClickListener(this);
		gifimg.setBackgroundResource(R.anim.gifload);
		animaition = (AnimationDrawable) gifimg.getBackground();
		animaition.setOneShot(false);
	}

	public void fetchData() {
		setState(LOADING);
		BmobQuery<QiangYu> query = new BmobQuery<QiangYu>();
		query.order("-updatedAt");
		// query.setCachePolicy(CachePolicy.NETWORK_ONLY);
		// 查询的数据条数
		query.setLimit(BmobConstants.NUMBERS_PER_PAGE);
		// BmobDate date = new BmobDate(new Date(System.currentTimeMillis()));
		// query.addWhereLessThan("createdAt", date);
		query.addWhereEqualTo("gltbname", tbname);
		// 跳过查询的数据NUMBERS_PER_PAGE*(pageNum++)
		query.setSkip(BmobConstants.NUMBERS_PER_PAGE * pageNum);

		query.include("author");
		query.findObjects(this, new FindListener<QiangYu>() {
			@Override
			public void onSuccess(List<QiangYu> list) {
				// TODO Auto-generated method stub
				if (list.size() != 0 && list.get(list.size() - 1) != null) {
					if (mRefreshType == RefreshType.REFRESH) {
						mListItems.clear();
					}
					if (list.size() < BmobConstants.NUMBERS_PER_PAGE) {
						// LogUtils.i(TAG,"已加载完所有数据~");
					}
					hendtiebaname.setText(tbname);
					mListItems.addAll(list);
					mAdapter.notifyDataSetChanged();
					pageNum = pageNum + 1;

					setState(LOADING_COMPLETED);
					mPullRefreshListView.onRefreshComplete();
				} else {
					// ActivityUtil.show(getActivity(), "暂无更多数据~");
					hendtiebaname.setText(tbname);
					setState(LOADING_COMPLETED);
					mPullRefreshListView.onRefreshComplete();
				}
			}

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				// LogUtils.i(TAG,"find failed."+arg1);
				setState(LOADING_FAILED);
				mPullRefreshListView.onRefreshComplete();
			}
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		BmobQuery<QiangYu> query = new BmobQuery<QiangYu>();
		query.addWhereEqualTo("gltbname", tbname);
		query.count(TiebaListActivity.this, QiangYu.class, new CountListener() {

			@Override
			public void onFailure(int arg0, String arg1) {
				Toast.makeText(TiebaListActivity.this, arg1, Toast.LENGTH_SHORT)
						.show();
			}

			@Override
			public void onSuccess(int arg0) {
				hendsize.setText("贴子: " + arg0);

			}
		});
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasFocus);
		animaition.start();
	}

	private String getCurrentTime() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String times = formatter.format(new Date(System.currentTimeMillis()));
		return times;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_newtieba:
			Intent intent = new Intent(TiebaListActivity.this,
					CreadtiebaActivity.class);
			intent.putExtra("tiebaname", tbname);
			startActivity(intent);
			overridePendingTransition(R.anim.in_anim, R.anim.out_anim);
			finish();
			break;
		case R.id.limgxie:
			Intent intent1 = new Intent(TiebaListActivity.this,
					EditActivity.class);
			intent1.putExtra("tiebaname", tbname);
			startActivityForResult(intent1, 2);
			overridePendingTransition(R.anim.in_anim, R.anim.out_anim);
			break;
		case R.id.hendweiguanzhu:
			final User user = BmobUser.getCurrentUser(TiebaListActivity.this,
					User.class);
			if (user != null) {
				BmobRelation usertbname = new BmobRelation();
				usertbname.add(baName);
				user.setUsertbname(usertbname);
				user.update(TiebaListActivity.this, new UpdateListener() {
					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						// LogUtils.i(TAG, "更新评论成功。");
						// fetchData();
						hendweiguanzhu.setVisibility(View.GONE);
						hendyiguanzhu.setVisibility(View.VISIBLE);
					}

					@Override
					public void onFailure(int arg0, String arg1) {
						// TODO Auto-generated method stub
						// LogUtils.i(TAG, "更新评论失败。" + arg1);
					}
				});

			}
			break;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) { // resultCode为回传的标记，我在B中回传的是RESULT_OK
		case RESULT_OK:
			mRefreshType = RefreshType.REFRESH;
			pageNum = 0;
			fetchData();
			break;
		}
	}

	private static final int LOADING = 1;
	private static final int LOADING_COMPLETED = 2;
	private static final int LOADING_FAILED = 3;
	private static final int NORMAL = 4;
	private AIContentAdapter mAdapter;
	private LinearLayout hendlayout;
	private TextView hendtiebaname;
	private TextView hendsize;
	private Button hendweiguanzhu;
	private Button hendyiguanzhu;

	public void setState(int state) {
		switch (state) {
		case LOADING:
			if (mListItems.size() == 0) {
				mPullRefreshListView.setVisibility(View.GONE);
				// progressbar.setVisibility(View.VISIBLE);
				tiebaload.setVisibility(View.VISIBLE);
			}

			break;
		case LOADING_COMPLETED:
			tiebaload.setVisibility(View.GONE);

			mPullRefreshListView.setVisibility(View.VISIBLE);
			mPullRefreshListView.setMode(Mode.BOTH);

			break;
		case LOADING_FAILED:
			if (mListItems.size() == 0) {
				mPullRefreshListView.setVisibility(View.VISIBLE);
				mPullRefreshListView.setMode(Mode.PULL_FROM_START);

			}
			break;
		case NORMAL:

			break;
		default:
			break;
		}
	}

}
