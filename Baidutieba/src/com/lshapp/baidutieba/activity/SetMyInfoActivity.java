package com.lshapp.baidutieba.activity;

import java.util.List;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.PushListener;

import com.lshapp.baidutieba.CustomApplcation;
import com.lshapp.baidutieba.R;
import com.lshapp.baidutieba.base.TieBaName;
import com.lshapp.baidutieba.base.User;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class SetMyInfoActivity extends ActivityBase implements OnClickListener {
	private User user;
	private TextView unametv;
	private ImageView usertouxiang;
	private TextView uxsname;
	private TextView batv1;
	private TextView batv2;
	private TextView batv3;
	private TextView batv4;
	private LinearLayout balayout;
	private TextView basize;
	private LinearLayout faxiaoxi;
	private LinearLayout addhaoyou;
	private LinearLayout uxiabiao;
	private LinearLayout gotiezi;
	private ImageView uuimgxie;
	private TextView readdata;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setuserlayout);
		user = (User) getIntent().getSerializableExtra("userdata");
		User geUser = BmobUser.getCurrentUser(SetMyInfoActivity.this,
				User.class);
		initview();
		unametv.setText(user.getUsername());
		uxsname.setText(user.getUsername());
		if (TextUtils.isEmpty(user.getSetread())) {
			readdata.setText("他什么也没有写哦");
		} else {
			readdata.setText(user.getSetread());
		}
		String avatarUrl;
		String a = user.getAvatar();
		if (a == null || a.isEmpty()) {
			usertouxiang.setImageResource(R.drawable.photo);
		} else {

			avatarUrl = user.getAvatar();
			ImageLoader.getInstance()
					.displayImage(
							avatarUrl,
							usertouxiang,
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
		BmobQuery<TieBaName> query = new BmobQuery<TieBaName>();
		query.addWhereRelatedTo("usertbname", new BmobPointer(user));
		query.findObjects(SetMyInfoActivity.this,
				new FindListener<TieBaName>() {

					@Override
					public void onSuccess(List<TieBaName> arg0) {
						basize.setText(arg0.size() + "");
						if (arg0.size() == 1) {
							batv1.setText(arg0.get(0).getName());
						}
						if (arg0.size() == 2) {
							batv1.setText(arg0.get(0).getName());
							batv2.setText(arg0.get(1).getName());
						}
						if (arg0.size() == 3) {
							batv1.setText(arg0.get(0).getName());
							batv2.setText(arg0.get(1).getName());
							batv3.setText(arg0.get(2).getName());
						}
						if (arg0.size() >= 4) {
							batv1.setText(arg0.get(0).getName());
							batv2.setText(arg0.get(1).getName());
							batv3.setText(arg0.get(2).getName());
							batv4.setText(arg0.get(3).getName());
						}
					}

					@Override
					public void onError(int arg0, String arg1) {

					}
				});
		if (geUser.getUsername().equals(user.getUsername())) {
			// 如果是自己的话
			uxiabiao.setVisibility(View.GONE);
			uuimgxie.setImageResource(R.drawable.icon_pop_edit);
			uuimgxie.setOnClickListener(this);

		} else {
			if (mApplication.getContactList().containsKey(user.getUsername())) {// 是好友
				faxiaoxi.setVisibility(View.VISIBLE);
			} else {
				addhaoyou.setVisibility(View.VISIBLE);
			}

		}

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 2) {
			if (resultCode == RESULT_OK || resultCode == 3) {

				BmobQuery<User> query = new BmobQuery<User>();
				query.addWhereEqualTo("username", user.getUsername());
				query.findObjects(SetMyInfoActivity.this,
						new FindListener<User>() {

							@Override
							public void onSuccess(List<User> arg0) {
								String read = arg0.get(0).getSetread();
								if (TextUtils.isEmpty(read)) {
									readdata.setText("他什么也没有写哦");
								} else {
									readdata.setText(read);
								}
								String ab = arg0.get(0).getAvatar();
								if (ab == null || ab.isEmpty()) {
									usertouxiang
											.setImageResource(R.drawable.photo);
								} else {

									ImageLoader.getInstance().displayImage(
											ab,
											usertouxiang,
											CustomApplcation.getInstance()
													.getOptions(
															R.drawable.photo),
											new SimpleImageLoadingListener() {

												@Override
												public void onLoadingComplete(
														String imageUri,
														View view,
														Bitmap loadedImage) {
													super.onLoadingComplete(
															imageUri, view,
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

		}
	}

	private void initview() {
		uuimgxie = (ImageView) findViewById(R.id.uuimgxie);

		readdata = (TextView) findViewById(R.id.readdata);
		unametv = (TextView) findViewById(R.id.unametv);
		usertouxiang = (ImageView) findViewById(R.id.usertouxiang);
		uxsname = (TextView) findViewById(R.id.uxsname);
		batv1 = (TextView) findViewById(R.id.batv1);
		batv2 = (TextView) findViewById(R.id.batv2);
		batv3 = (TextView) findViewById(R.id.batv3);
		batv4 = (TextView) findViewById(R.id.batv4);
		balayout = (LinearLayout) findViewById(R.id.balayout);
		basize = (TextView) findViewById(R.id.basize);
		faxiaoxi = (LinearLayout) findViewById(R.id.faxiaoxi);
		addhaoyou = (LinearLayout) findViewById(R.id.addhaoyou);
		uxiabiao = (LinearLayout) findViewById(R.id.uxiabiao);
		gotiezi = (LinearLayout) findViewById(R.id.gotiezi);

		gotiezi.setOnClickListener(this);
		balayout.setOnClickListener(this);
		addhaoyou.setOnClickListener(this);
		faxiaoxi.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.balayout:
			Intent intent = new Intent(SetMyInfoActivity.this,
					BanamelistActivity.class);
			intent.putExtra("listdata", user);
			startActivity(intent);
			overridePendingTransition(R.anim.in_anim, R.anim.out_anim);
			break;
		case R.id.gotiezi:
			Intent intent2 = new Intent(SetMyInfoActivity.this,
					ReadTieziActivity.class);
			intent2.putExtra("listdata", user);
			startActivity(intent2);
			overridePendingTransition(R.anim.in_anim, R.anim.out_anim);

			break;
		case R.id.addhaoyou:
			addFriend();
			break;
		case R.id.faxiaoxi:
			Intent intent1 = new Intent(this, ChatActivity.class);
			intent1.putExtra("user", user);
			startAnimActivity(intent1);
			overridePendingTransition(R.anim.in_anim, R.anim.out_anim);
			break;
		case R.id.uuimgxie:
			Intent intent3 = new Intent(SetMyInfoActivity.this,
					SetMydataActivity.class);
			startActivityForResult(intent3, 2);
			overridePendingTransition(R.anim.in_anim, R.anim.out_anim);
			break;
		}

	}

	private void addFriend() {
		final ProgressDialog progress = new ProgressDialog(this);
		progress.setMessage("正在添加...");
		progress.setCanceledOnTouchOutside(false);
		progress.show();
		// 发送tag请求
		BmobChatManager.getInstance(this).sendTagMessage(
				BmobConfig.TAG_ADD_CONTACT, user.getObjectId(),
				new PushListener() {

					@Override
					public void onSuccess() {
						progress.dismiss();
						ShowToast("发送请求成功，等待对方验证！");
					}

					@Override
					public void onFailure(int arg0, final String arg1) {
						// TODO Auto-generated method stub
						progress.dismiss();
						ShowToast("发送请求成功，等待对方验证！");
						ShowLog("发送请求失败:" + arg1);
					}
				});
	}

}
