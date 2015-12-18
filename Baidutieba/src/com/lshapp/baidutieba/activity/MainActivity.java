package com.lshapp.baidutieba.activity;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.bmob.im.BmobChat;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobNotifyManager;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.inteface.EventListener;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;

import com.lshapp.baidutieba.CustomApplcation;
import com.lshapp.baidutieba.MyMessageReceiver;
import com.lshapp.baidutieba.R;
import com.lshapp.baidutieba.base.User;
import com.lshapp.baidutieba.fragment.ContactFragment;
import com.lshapp.baidutieba.fragment.RecentFragment;
import com.lshapp.baidutieba.fragment.SettingsFragment;
import com.lshapp.baidutieba.fragment.TabaFragment;
import com.lshapp.baidutieba.slidingview.SlidingMenu;
import com.lshapp.baidutieba.utils.DoubleClickExitHelper;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class MainActivity extends ActivityBase implements OnClickListener,
		EventListener {

	private SlidingMenu mLeftMenu;
	private LinearLayout taba;
	private LinearLayout tabb;
	private LinearLayout tabc;
	private LinearLayout tabd;
	private ImageView tabaimg;
	private ImageView tabbimg;
	private ImageView tabcimg;
	private ImageView tabdimg;
	private TabaFragment ftaba;
	private RecentFragment ftabb;
	private ContactFragment ftabc;
	private SettingsFragment ftabd;
	private int currentTabIndex = 5;
	private ImageView chatcView;
	private ImageView chatbView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		// 开启定时检测服务（单位为秒）-在这里检测后台是否还有未读的消息，有的话就取出来
		// 如果你觉得检测服务比较耗流量和电量，你也可以去掉这句话-同时还有onDestory方法里面的stopPollService方法
		BmobChat.getInstance(this).startPollService(10);
		// 开启广播接收器

		initNewMessageBroadCast();
		initTagMessageBroadCast();

		IntentFilter filter = new IntentFilter(SetMydataActivity.action);
		registerReceiver(broadcastReceiver, filter);

		doubleClick = new DoubleClickExitHelper(this);
		initview();
		User us = BmobUser.getCurrentUser(MainActivity.this, User.class);
		zyusername.setText(us.getUsername());
		zyduanyu.setText("个人简介：" + us.getSetread());
		String a = us.getAvatar();

		if (a == null || a.isEmpty()) {
			zhutouxiang.setImageResource(R.drawable.photo);
		} else {

			ImageLoader.getInstance()
					.displayImage(
							a,
							zhutouxiang,
							CustomApplcation.getInstance().getOptions(
									R.drawable.photo),
							new SimpleImageLoadingListener() {

								@Override
								public void onLoadingComplete(String imageUri,
										View view, Bitmap loadedImage) {
									super.onLoadingComplete(imageUri, view,
											loadedImage);
								}

							});

			ImageLoader.getInstance()
					.displayImage(
							a,
							img_ch,
							CustomApplcation.getInstance().getOptions(
									R.drawable.photo),
							new SimpleImageLoadingListener() {

								@Override
								public void onLoadingComplete(String imageUri,
										View view, Bitmap loadedImage) {
									super.onLoadingComplete(imageUri, view,
											loadedImage);
								}

							});

		}
		initEvent();
		setSelect(0);

	}

	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// Toast.makeText(MainActivity.this, "?？？？？", Toast.LENGTH_LONG)
			// .show();

//			Log.d("CommentActivity", "server");

			User u = BmobUser.getCurrentUser(MainActivity.this, User.class);
			BmobQuery<User> query = new BmobQuery<User>();
			query.addWhereEqualTo("username", u.getUsername());
			query.findObjects(mContext, new FindListener<User>() {

				@Override
				public void onSuccess(List<User> arg0) {
					Intent intent = new Intent(mContext,
							SetMyInfoActivity.class);
					zyduanyu.setText("个人简介：" + arg0.get(0).getSetread());
					String a = arg0.get(0).getAvatar();
					if (a == null || a.isEmpty()) {
						zhutouxiang.setImageResource(R.drawable.photo);
					} else {

						ImageLoader.getInstance().displayImage(
								a,
								zhutouxiang,
								CustomApplcation.getInstance().getOptions(
										R.drawable.photo),
								new SimpleImageLoadingListener() {

									@Override
									public void onLoadingComplete(
											String imageUri, View view,
											Bitmap loadedImage) {
										super.onLoadingComplete(imageUri, view,
												loadedImage);
									}

								});

						ImageLoader.getInstance().displayImage(
								a,
								img_ch,
								CustomApplcation.getInstance().getOptions(
										R.drawable.photo),
								new SimpleImageLoadingListener() {

									@Override
									public void onLoadingComplete(
											String imageUri, View view,
											Bitmap loadedImage) {
										super.onLoadingComplete(imageUri, view,
												loadedImage);
									}

								});

					}

				}

				@Override
				public void onError(int arg0, String arg1) {

				}
			});

		}

	};

	NewBroadcastReceiver newReceiver;

	private void initNewMessageBroadCast() {
		// 注册接收消息广播
		newReceiver = new NewBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter(
				BmobConfig.BROADCAST_NEW_MESSAGE);
		// 优先级要低于ChatActivity
		intentFilter.setPriority(3);
		registerReceiver(newReceiver, intentFilter);
	}

	/**
	 * 新消息广播接收者
	 * 
	 */
	private class NewBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// 刷新界面
			refreshNewMsg(null);
			// 记得把广播给终结掉
			abortBroadcast();
		}
	}

	/**
	 * 刷新界面
	 * 
	 * @Title: refreshNewMsg
	 * @Description: TODO
	 * @param @param message
	 * @return void
	 * @throws
	 */
	private void refreshNewMsg(BmobMsg message) {
		// 声音提示
		boolean isAllow = CustomApplcation.getInstance().getSpUtil()
				.isAllowVoice();
		if (isAllow) {
			CustomApplcation.getInstance().getMediaPlayer().start();
		}
		// iv_recent_tips.setVisibility(View.VISIBLE);
		// 也要存储起来
		chatbView.setVisibility(View.VISIBLE);
		if (message != null) {
			BmobChatManager.getInstance(MainActivity.this).saveReceiveMessage(
					true, message);
		}
		if (currentTabIndex == 0) {
			// 当前页面如果为会话页面，刷新此页面
			if (ftabb != null) {
				ftabb.refresh();
			}
		}
	}

	TagBroadcastReceiver userReceiver;
	private TextView tabatv;
	private TextView tabbtv;
	private TextView tabctv;
	private TextView tabdtv;
	private ImageView go_hf;
	private ImageView zhutouxiang;
	private ImageView img_ch;
	private DoubleClickExitHelper doubleClick;
	private LinearLayout appset;
	private TextView zyusername;
	private TextView zyduanyu;

	private void initTagMessageBroadCast() {
		// 注册接收消息广播
		userReceiver = new TagBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter(
				BmobConfig.BROADCAST_ADD_USER_MESSAGE);
		// 优先级要低于ChatActivity
		intentFilter.setPriority(3);
		registerReceiver(userReceiver, intentFilter);
	}

	/**
	 * 标签消息广播接收者
	 */
	private class TagBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			BmobInvitation message = (BmobInvitation) intent
					.getSerializableExtra("invite");
			refreshInvite(message);
			// 记得把广播给终结掉
			abortBroadcast();
		}
	}

	private void refreshInvite(BmobInvitation message) {
		boolean isAllow = CustomApplcation.getInstance().getSpUtil()
				.isAllowVoice();
		if (isAllow) {
			CustomApplcation.getInstance().getMediaPlayer().start();
		}
		// iv_contact_tips.setVisibility(View.VISIBLE);
		if (currentTabIndex == 1) {
			if (ftabc != null) {
				ftabc.refresh();
			}
		} else {
			// 同时提醒通知
			String tickerText = message.getFromname() + "请求添加好友";
			boolean isAllowVibrate = CustomApplcation.getInstance().getSpUtil()
					.isAllowVibrate();
			BmobNotifyManager.getInstance(this).showNotify(isAllow,
					isAllowVibrate, R.drawable.ic_launcher, tickerText,
					message.getFromname(), tickerText.toString(),
					NewFriendActivity.class);
		}
	}

	@Override
	public void onOffline() {
		// TODO Auto-generated method stub
		showOfflineDialog(this);
	}

	@Override
	public void onReaded(String conversionId, String msgTime) {
	}

	private void initview() {
		mLeftMenu = (SlidingMenu) findViewById(R.id.id_menu);
		taba = (LinearLayout) findViewById(R.id.tab_a);
		tabb = (LinearLayout) findViewById(R.id.tab_b);
		tabc = (LinearLayout) findViewById(R.id.tab_c);
		tabd = (LinearLayout) findViewById(R.id.tab_d);
		chatbView = (ImageView) findViewById(R.id.tab_b_chat);
		chatcView = (ImageView) findViewById(R.id.tab_c_chat);
		tabaimg = (ImageView) findViewById(R.id.tab_a_img);
		tabbimg = (ImageView) findViewById(R.id.tab_b_img);
		tabcimg = (ImageView) findViewById(R.id.tab_c_img);
		tabdimg = (ImageView) findViewById(R.id.tab_d_img);
		zhutouxiang = (ImageView) findViewById(R.id.zhutouxiang);
		img_ch = (ImageView) findViewById(R.id.img_ch);
		zyusername = (TextView) findViewById(R.id.zyusername);
		zyduanyu = (TextView) findViewById(R.id.zyduanyu);

		tabatv = (TextView) findViewById(R.id.tab_a_tv);
		tabbtv = (TextView) findViewById(R.id.tab_b_tv);
		tabctv = (TextView) findViewById(R.id.tab_c_tv);
		tabdtv = (TextView) findViewById(R.id.tab_d_tv);
		go_hf = (ImageView) findViewById(R.id.go_hf);

		appset = (LinearLayout) findViewById(R.id.appset);
	}

	private void initEvent() {
		taba.setOnClickListener(this);
		tabb.setOnClickListener(this);
		tabc.setOnClickListener(this);
		tabd.setOnClickListener(this);
		go_hf.setOnClickListener(this);
		appset.setOnClickListener(this);
		zhutouxiang.setOnClickListener(this);
		zyusername.setOnClickListener(this);

	}

	private void setSelect(int i) {
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		if (ftaba != null) {
			fragmentTransaction.hide(ftaba);
		}
		if (ftabb != null) {
			fragmentTransaction.hide(ftabb);
		}
		if (ftabc != null) {
			fragmentTransaction.hide(ftabc);
		}
		if (ftabd != null) {
			fragmentTransaction.hide(ftabd);
		}
		switch (i) {
		case 0:
			if (ftaba == null) {
				ftaba = new TabaFragment();
				fragmentTransaction.add(R.id.content, ftaba);
			} else {
				fragmentTransaction.show(ftaba);
			}
			tabaimg.setImageResource(R.drawable.icon_tabbar_jinba_s);
			tabbimg.setImageResource(R.drawable.icon_tabbar_tie_n);
			tabcimg.setImageResource(R.drawable.icon_tabbar_chaticon_n);
			tabdimg.setImageResource(R.drawable.icon_tabbar_finding_n);

			tabatv.setTextColor(Color.parseColor("#3385FF"));
			tabbtv.setTextColor(Color.parseColor("#5E6570"));
			tabctv.setTextColor(Color.parseColor("#5E6570"));
			tabdtv.setTextColor(Color.parseColor("#5E6570"));
			chatbView.setVisibility(View.GONE);
			break;
		case 1:
			if (ftabb == null) {
				ftabb = new RecentFragment();
				fragmentTransaction.add(R.id.content, ftabb);
			} else {
				fragmentTransaction.show(ftabb);
			}
			tabaimg.setImageResource(R.drawable.icon_tabbar_jinba_n);
			tabbimg.setImageResource(R.drawable.icon_tabbar_tie_s);
			tabcimg.setImageResource(R.drawable.icon_tabbar_chaticon_n);
			tabdimg.setImageResource(R.drawable.icon_tabbar_finding_n);

			tabbtv.setTextColor(Color.parseColor("#3385FF"));
			tabatv.setTextColor(Color.parseColor("#5E6570"));
			tabctv.setTextColor(Color.parseColor("#5E6570"));
			tabdtv.setTextColor(Color.parseColor("#5E6570"));

			currentTabIndex = 0;
			break;
		case 2:
			if (ftabc == null) {
				ftabc = new ContactFragment();
				fragmentTransaction.add(R.id.content, ftabc);
			} else {
				fragmentTransaction.show(ftabc);
			}
			tabaimg.setImageResource(R.drawable.icon_tabbar_jinba_n);
			tabbimg.setImageResource(R.drawable.icon_tabbar_tie_n);
			tabcimg.setImageResource(R.drawable.icon_tabbar_chaticon_s);
			tabdimg.setImageResource(R.drawable.icon_tabbar_finding_n);

			tabctv.setTextColor(Color.parseColor("#3385FF"));
			tabbtv.setTextColor(Color.parseColor("#5E6570"));
			tabatv.setTextColor(Color.parseColor("#5E6570"));
			tabdtv.setTextColor(Color.parseColor("#5E6570"));

			currentTabIndex = 1;
			break;
		case 3:
			if (ftabd == null) {
				ftabd = new SettingsFragment();
				fragmentTransaction.add(R.id.content, ftabd);
			} else {
				fragmentTransaction.show(ftabd);
			}
			tabaimg.setImageResource(R.drawable.icon_tabbar_jinba_n);
			tabbimg.setImageResource(R.drawable.icon_tabbar_tie_n);
			tabcimg.setImageResource(R.drawable.icon_tabbar_chaticon_n);
			tabdimg.setImageResource(R.drawable.icon_tabbar_finding_s);

			tabdtv.setTextColor(Color.parseColor("#3385FF"));
			tabbtv.setTextColor(Color.parseColor("#5E6570"));
			tabctv.setTextColor(Color.parseColor("#5E6570"));
			tabatv.setTextColor(Color.parseColor("#5E6570"));

			break;
		}
		fragmentTransaction.commit();
	}

	public void toggleMenu(View view) {
		mLeftMenu.toggle();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
	}

	@Override
	public void onClick(View v) {
		initimg();
		switch (v.getId()) {
		case R.id.tab_a:
			setSelect(0);
			break;
		case R.id.tab_b:
			setSelect(1);
			break;
		case R.id.tab_c:
			setSelect(2);
			break;
		case R.id.tab_d:
			setSelect(3);
			break;
		case R.id.go_hf:
			Intent intent = new Intent(MainActivity.this, MyhuifuActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.in_anim, R.anim.out_anim);
			break;
		case R.id.appset:
			Intent intent4 = new Intent(MainActivity.this, AppSetActivity.class);
			startActivity(intent4);
			overridePendingTransition(R.anim.in_anim, R.anim.out_anim);
			break;
		case R.id.zhutouxiang:
			User user = BmobUser.getCurrentUser(MainActivity.this, User.class);
			Intent intent5 = new Intent(MainActivity.this,
					SetMyInfoActivity.class);
			intent5.putExtra("userdata", user);
			startActivity(intent5);
			overridePendingTransition(R.anim.in_anim, R.anim.out_anim);
			break;

		case R.id.zyusername:
			User usera = BmobUser.getCurrentUser(MainActivity.this, User.class);
			Intent intent6 = new Intent(MainActivity.this,
					SetMyInfoActivity.class);
			intent6.putExtra("userdata", usera);
			startActivity(intent6);
			overridePendingTransition(R.anim.in_anim, R.anim.out_anim);

			break;
		}

	}

	private void initimg() {
		tabaimg.setImageResource(R.drawable.icon_tabbar_jinba_n);
		tabbimg.setImageResource(R.drawable.icon_tabbar_tie_n);
		tabcimg.setImageResource(R.drawable.icon_tabbar_chaticon_n);
		tabdimg.setImageResource(R.drawable.icon_tabbar_finding_n);
	}

	@Override
	public void onAddUser(BmobInvitation message) {
		// TODO Auto-generated method stub
		refreshInvite(message);
	}

	@Override
	public void onMessage(BmobMsg message) {
		refreshNewMsg(message);

	}

	@Override
	public void onNetChange(boolean isNetConnected) {
		// TODO Auto-generated method stub
		if (isNetConnected) {
			ShowToast(R.string.network_tips);
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MyMessageReceiver.ehList.remove(this);// 取消监听推送的消息
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		try {
			unregisterReceiver(newReceiver);
		} catch (Exception e) {
		}
		try {
			unregisterReceiver(userReceiver);
		} catch (Exception e) {
		}

		unregisterReceiver(broadcastReceiver);

		// 取消定时检测服务
		BmobChat.getInstance(this).stopPollService();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// 小圆点提示
		if (BmobDB.create(this).hasUnReadMsg()) {
			chatbView.setVisibility(View.VISIBLE);
		} else {
			chatbView.setVisibility(View.GONE);
		}
		if (BmobDB.create(this).hasNewInvite()) {
			chatcView.setVisibility(View.VISIBLE);
		} else {
			chatcView.setVisibility(View.GONE);
		}
		MyMessageReceiver.mcontext = getApplicationContext();
		MyMessageReceiver.ehList.add(this);// 监听推送的消息
		// 清空
		MyMessageReceiver.mNewNum = 0;

	}

	// 返回键
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			return doubleClick.onKeyDown(keyCode, event);
		}
		return super.onKeyDown(keyCode, event);
	}

}
