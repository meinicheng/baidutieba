package com.lshapp.baidutieba.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobPushManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import com.lshapp.baidutieba.CustomApplcation;
import com.lshapp.baidutieba.R;
import com.lshapp.baidutieba.adapter.HuifulistAdapter;
import com.lshapp.baidutieba.base.Comment;
import com.lshapp.baidutieba.base.QiangYu;
import com.lshapp.baidutieba.base.User;
import com.lshapp.baidutieba.utils.BmobConstants;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class HuifulistActivity extends Activity implements OnClickListener,
		OnScrollListener {
	private int pageNum = 0;
	BmobPushManager bmobPushManager;

	private String scommentEdit;
	private int ifswitch;
	private Comment comment;
	private String lzname;
	private ImageView cuser_logo;
	private TextView cuser_name;
	private TextView cuser_time;
	private TextView ctitle_text;
	private TextView ccontent_text;
	private ImageView ccontent_image;
	private LinearLayout footerlayout;
	private TextView loadButton;
	private LinearLayout loading;
	private LinearLayout tibu;
	private EditText commentedit;
	private Button commentbtn;
	private ListView clist;
	private TextView cnametv;
	private RelativeLayout headlayout;
	private List<Comment> comments = new ArrayList<Comment>();
	private HuifulistAdapter adapter;
	private String hfdename;
	private int huifuswitch;
	private String cengzhuname;
	int pinglun = 1;
	int huifu = 1;
	private QiangYu qiangYu;
	private ProgressDialog progress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comment);
		comment = (Comment) getIntent().getSerializableExtra("data");
		lzname = getIntent().getStringExtra("lzname");
		qiangYu = (QiangYu) getIntent().getSerializableExtra("lzqy");
		cengzhuname = comment.getUser().getUsername();
		hfdename = cengzhuname;
		
		
		progress = new ProgressDialog(HuifulistActivity.this);
		progress.setMessage("加载中...");
		progress.setCanceledOnTouchOutside(false);
		progress.show();
		
		InitView();
		clist.addHeaderView(headlayout);
		clist.addFooterView(footerlayout);
		adapter = new HuifulistAdapter(this, comments, lzname);
		clist.setAdapter(adapter);

		setHeadView(comment);
		setData();
		clist.setOnScrollListener(this);
		clist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == 0) {

					hfdename = comment.getUser().getUsername();
					commentedit.setHint("回复层主");
				} else {
					TextView text = (TextView) view
							.findViewById(R.id.hlcomment_name);
					hfdename = text.getText().toString();
					commentedit.setHint("回复" + hfdename + "的评论...");
				}
			}
		});

	}

	private void setData() {
		BmobQuery<Comment> query = new BmobQuery<Comment>();
		query.addWhereRelatedTo("xiacengcomment", new BmobPointer(comment));
		query.include("user");
		query.order("createdAt");
		query.setSkip(BmobConstants.NUMBERS_PER_PAGE * pageNum);
		query.setLimit(BmobConstants.NUMBERS_PER_PAGE);
		query.findObjects(HuifulistActivity.this, new FindListener<Comment>() {

			@Override
			public void onSuccess(List<Comment> data) {
				if (data.size() != 0) {
					if (data.size() < BmobConstants.NUMBERS_PER_PAGE) {
						loadButton.setVisibility(View.VISIBLE);
						loading.setVisibility(View.GONE);

					} else {
						loadButton.setVisibility(View.GONE);
						loading.setVisibility(View.VISIBLE);
					}
					comments.addAll(data);
					adapter.notifyDataSetChanged();
					pageNum = pageNum + 1;
					ifswitch = 0;

				} else {
					loadButton.setVisibility(View.VISIBLE);
					loading.setVisibility(View.GONE);
					ifswitch = 0;
				}
				progress.dismiss();

			}

			@Override
			public void onError(int arg0, String arg1) {
				ifswitch = 0;
				progress.dismiss();
			}
		});

	}

	private void setHeadView(Comment qy) {
		if (qy == null) {
			return;
		}
		if (qy.getUser().getUsername().equals(lzname)) {
			ccontent_image.setVisibility(View.VISIBLE);
		} else {
			ccontent_image.setVisibility(View.GONE);
		}
		cuser_name.setText(qy.getUser().getUsername());
		ctitle_text.setText(qy.getCommentContent());
		cuser_time.setText(qy.getCreatedAt());

		String b = qy.getUser().getAvatar();
		if (b == null || b.isEmpty()) {
			cuser_logo.setImageResource(R.drawable.photo);
		} else {
			ImageLoader.getInstance()
					.displayImage(
							qy.getUser().getAvatar(),
							cuser_logo,
							CustomApplcation.getInstance().getOptions(
									R.drawable.photo),
							new SimpleImageLoadingListener() {

								@Override
								public void onLoadingComplete(String imageUri,
										View view, Bitmap loadedImage) {
									// TODO Auto-generated method stub
									super.onLoadingComplete(imageUri, view,
											loadedImage);
								}
							});
		}

	}

	private void InitView() {
		headlayout = (RelativeLayout) LayoutInflater.from(this).inflate(
				R.layout.headhuifulist, null);
		cuser_logo = (ImageView) headlayout.findViewById(R.id.hcuser_logo);
		cuser_name = (TextView) headlayout.findViewById(R.id.hcuser_name);
		cuser_time = (TextView) headlayout.findViewById(R.id.hcuser_time);
		ctitle_text = (TextView) headlayout.findViewById(R.id.hctitle_text);
		ccontent_image = (ImageView) headlayout.findViewById(R.id.hcuser_size);

		footerlayout = (LinearLayout) getLayoutInflater().inflate(
				R.layout.clistfooter, null);
		loadButton = (TextView) footerlayout.findViewById(R.id.loadButton);
		loading = (LinearLayout) footerlayout.findViewById(R.id.loading);
		tibu = (LinearLayout) footerlayout.findViewById(R.id.tibu);

		cnametv = (TextView) findViewById(R.id.cnametv);
		clist = (ListView) findViewById(R.id.clist);
		commentbtn = (Button) findViewById(R.id.commentbtn);

		commentedit = (EditText) findViewById(R.id.commentedit);
		commentbtn.setOnClickListener(this);
		
		loadButton.setOnClickListener(this);
		// ctitle_text.setOnClickListener(this);
		cuser_logo.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.commentbtn:
			
			scommentEdit = commentedit.getText().toString();
			if (TextUtils.isEmpty(scommentEdit)) {
				Toast.makeText(HuifulistActivity.this, "请输入评论的内容", Toast.LENGTH_SHORT).show();
				return;
			}
			commentbtn.setClickable(false);
			progress.show();
			User user = BmobUser.getCurrentUser(HuifulistActivity.this,
					User.class);

			publishComment(user, scommentEdit);
			break;
		case R.id.loadButton:
			loadButton.setVisibility(View.GONE);
			tibu.setVisibility(View.VISIBLE);

			publishData();
			break;
		// case R.id.hctitle_text:
		// hfdename = comment.getUser().getUsername();
		// commentedit.setHint("回复层主");
		// break;
		case R.id.hcuser_logo:

			Intent intent = new Intent(HuifulistActivity.this,
					SetMyInfoActivity.class);
			intent.putExtra("userdata", comment.getUser());
			startActivity(intent);
			overridePendingTransition(R.anim.in_anim, R.anim.out_anim);
			break;

		}

	}

	private void publishData() {
		BmobQuery<Comment> query = new BmobQuery<Comment>();
		query.addWhereRelatedTo("xiacengcomment", new BmobPointer(comment));
		query.include("user");
		query.order("createdAt");
		query.setSkip(adapter.getCount());
		query.setLimit(BmobConstants.NUMBERS_PER_PAGE);
		query.findObjects(HuifulistActivity.this, new FindListener<Comment>() {

			@Override
			public void onSuccess(List<Comment> arg0) {
				Log.d("CommentActivity", adapter.getCount()
						+ "****************");
				if (arg0.size() != 0) {
					comments.addAll(arg0);
					adapter.notifyDataSetChanged();
				}
				loadButton.setVisibility(View.VISIBLE);
				tibu.setVisibility(View.GONE);
				progress.dismiss();
			}

			@Override
			public void onError(int arg0, String arg1) {
				Log.d("CommentActivity", arg1);
				progress.dismiss();
			}
		});
	}

	private void hideSoftInput() {
		InputMethodManager imm = (InputMethodManager) this
				.getSystemService(Context.INPUT_METHOD_SERVICE);

		imm.hideSoftInputFromWindow(commentedit.getWindowToken(), 0);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
			Log.d("CommentActivity", "huifulistActivity开始");
			// 自动加载,可以在这里放置异步加载数据的代码
			if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
				Log.d("CommentActivity", "???huifulistActivity????????");
				// 自动加载,可以在这里放置异步加载数据的代码
				if (ifswitch != 1 && loading.getVisibility() == 0) {
					Log.d("CommentActivity", "huifulistActivity");
					ifswitch = 1;
					setData();
				}
			}
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {

	}

	private void publishComment(final User user, String scommentEdit2) {
		final Comment commentb = new Comment();
		commentb.setUser(user);
		// 如果被评论的用户等于层主
		if (hfdename.equals(cengzhuname)) {
			commentb.setCommentContent(scommentEdit2);
			// 判断是否是楼主评论层主
			if (user.getUsername().equals(lzname)) {
				huifuswitch = 0;
				if (!lzname.equals(cengzhuname)) {
					commentb.setCihfname(cengzhuname);
					huifuswitch = 1;
					// c
				}
			} else {
				// 不是
				// 判断是否是层主评论层主
				if (user.getUsername().equals(cengzhuname)) {
					commentb.setZhuhfname(lzname);
					huifuswitch = 2;
					// z
				} else {
					commentb.setZhuhfname(lzname);
					commentb.setCihfname(cengzhuname);
					huifuswitch = 3;
					// z+c
				}
			}
		} else {
			commentb.setCommentContent("回复 " + hfdename + " : " + scommentEdit2);
			// 如果被评论的用户等于楼主
			if (hfdename.equals(lzname)) {
				if (user.getUsername().equals(lzname)) {
					huifuswitch = 0;
					if (!lzname.equals(cengzhuname)) {
						commentb.setCihfname(cengzhuname);
						huifuswitch = 1;
						// c
					}
				} else {
					if (user.getUsername().equals(cengzhuname)) {
						commentb.setZhuhfname(lzname);
						huifuswitch = 2;
						// z
					} else {
						commentb.setCihfname(cengzhuname);
						commentb.setZhuhfname(lzname);
						huifuswitch = 3;
						// z+c
					}
				}
			} else {
				if (user.getUsername().equals(lzname)) {
					commentb.setCicihfname(hfdename);
					huifuswitch = 7;
					// cc
					if (!lzname.equals(cengzhuname)) {
						commentb.setCihfname(cengzhuname);
						huifuswitch = 8;
						// cc+c
					}
				} else {
					if (user.getUsername().equals(cengzhuname)) {
						commentb.setCicihfname(hfdename);
						huifuswitch = 7;
						// cc
						if (!cengzhuname.equals(lzname)) {
							commentb.setZhuhfname(lzname);
							huifuswitch = 9;
							// cc+z
						}
					} else {
						// 第三方回复第三方
						if (user.getUsername().equals(hfdename)) {
							commentb.setZhuhfname(lzname);
							commentb.setCihfname(cengzhuname);
							huifuswitch = 3;
							// z+c
						} else {
							commentb.setZhuhfname(lzname);
							commentb.setCihfname(cengzhuname);
							commentb.setCicihfname(hfdename);
							huifuswitch = 10;
							// z+c+cc
						}
					}
				}

			}

		}

		commentb.setYtcontent(comment.getCommentContent());
		commentb.setCtbname(comment.getCtbname());
		commentb.setGozhuti(qiangYu);
		commentb.save(HuifulistActivity.this, new SaveListener() {

			@Override
			public void onSuccess() {
				commentedit.setText("");
				hideSoftInput();

				BmobRelation relation = new BmobRelation();
				relation.add(commentb);
				if (TextUtils.isEmpty(comment.getDiyitext()) && pinglun == 1) {
					SimpleDateFormat dateFormat24 = new SimpleDateFormat(
							"yyyy-MM-dd");
					String time = dateFormat24.format(Calendar.getInstance()
							.getTime());
					comment.setDiyitext(user.getUsername() + " : "
							+ scommentEdit + "  " + time);
				} else {
					if (TextUtils.isEmpty(comment.getDiertext()) && huifu == 1) {
						SimpleDateFormat dateFormat24 = new SimpleDateFormat(
								"yyyy-MM-dd");
						String time = dateFormat24.format(Calendar
								.getInstance().getTime());
						comment.setDiertext(user.getUsername() + " : "
								+ scommentEdit + "  " + time);
						huifu = huifu + 1;
					}
				}
				comment.setXiacengcomment(relation);
				comment.update(HuifulistActivity.this, new UpdateListener() {
					@Override
					public void onSuccess() {
						Log.d("CommentActivity", "走的switch=" + huifuswitch);
						switch (huifuswitch) {
						case 0:

							break;
						case 1:
							bmobPushManager = new BmobPushManager(
									HuifulistActivity.this);
							BmobQuery<BmobInstallation> query = BmobInstallation
									.getQuery();
							// 并添加条件为设备类型属于android
							query.addWhereEqualTo("uid", cengzhuname);
							// 设置推送条件给bmobPushManager对象。
							bmobPushManager.setQuery(query);
							// 设置推送消息，服务端会根据上面的查询条件，来进行推送这条消息
							bmobPushManager.pushMessage("hflist");

							break;
						case 2:
							bmobPushManager = new BmobPushManager(
									HuifulistActivity.this);
							BmobQuery<BmobInstallation> query2 = BmobInstallation
									.getQuery();
							// 并添加条件为设备类型属于android
							query2.addWhereEqualTo("uid", lzname);
							// 设置推送条件给bmobPushManager对象。
							bmobPushManager.setQuery(query2);
							// 设置推送消息，服务端会根据上面的查询条件，来进行推送这条消息
							bmobPushManager.pushMessage("hflist");
							break;
						case 3:
							bmobPushManager = new BmobPushManager(
									HuifulistActivity.this);
							BmobQuery<BmobInstallation> query3 = BmobInstallation
									.getQuery();
							query3.addWhereEqualTo("uid", cengzhuname);
							bmobPushManager.setQuery(query3);
							bmobPushManager.pushMessage("hflist");

							bmobPushManager = new BmobPushManager(
									HuifulistActivity.this);
							BmobQuery<BmobInstallation> query4 = BmobInstallation
									.getQuery();
							query4.addWhereEqualTo("uid", lzname);
							bmobPushManager.setQuery(query4);
							bmobPushManager.pushMessage("hflist");

							break;
						case 7:
							bmobPushManager = new BmobPushManager(
									HuifulistActivity.this);
							BmobQuery<BmobInstallation> query5 = BmobInstallation
									.getQuery();
							query5.addWhereEqualTo("uid", hfdename);
							bmobPushManager.setQuery(query5);
							bmobPushManager.pushMessage("hflist");

							break;
						case 8:
							bmobPushManager = new BmobPushManager(
									HuifulistActivity.this);
							BmobQuery<BmobInstallation> query8 = BmobInstallation
									.getQuery();
							query8.addWhereEqualTo("uid", cengzhuname);
							bmobPushManager.setQuery(query8);
							bmobPushManager.pushMessage("hflist");

							bmobPushManager = new BmobPushManager(
									HuifulistActivity.this);
							BmobQuery<BmobInstallation> query6 = BmobInstallation
									.getQuery();
							query6.addWhereEqualTo("uid", hfdename);
							bmobPushManager.setQuery(query6);
							bmobPushManager.pushMessage("hflist");

							break;
						case 9:
							bmobPushManager = new BmobPushManager(
									HuifulistActivity.this);
							BmobQuery<BmobInstallation> query9 = BmobInstallation
									.getQuery();
							query9.addWhereEqualTo("uid", hfdename);
							bmobPushManager.setQuery(query9);
							bmobPushManager.pushMessage("hflist");

							bmobPushManager = new BmobPushManager(
									HuifulistActivity.this);
							BmobQuery<BmobInstallation> query10 = BmobInstallation
									.getQuery();
							query10.addWhereEqualTo("uid", lzname);
							bmobPushManager.setQuery(query10);
							bmobPushManager.pushMessage("hflist");
							break;
						case 10:
							bmobPushManager = new BmobPushManager(
									HuifulistActivity.this);
							BmobQuery<BmobInstallation> query11 = BmobInstallation
									.getQuery();
							query11.addWhereEqualTo("uid", lzname);
							bmobPushManager.setQuery(query11);
							bmobPushManager.pushMessage("hflist");

							bmobPushManager = new BmobPushManager(
									HuifulistActivity.this);
							BmobQuery<BmobInstallation> query12 = BmobInstallation
									.getQuery();
							query12.addWhereEqualTo("uid", hfdename);
							bmobPushManager.setQuery(query12);
							bmobPushManager.pushMessage("hflist");

							bmobPushManager = new BmobPushManager(
									HuifulistActivity.this);
							BmobQuery<BmobInstallation> query13 = BmobInstallation
									.getQuery();
							query13.addWhereEqualTo("uid", cengzhuname);
							bmobPushManager.setQuery(query13);
							bmobPushManager.pushMessage("hflist");
							break;

						}
						qiangYu.increment("score");
						qiangYu.update(HuifulistActivity.this);

						pinglun = pinglun + 1;
						if (loadButton.getVisibility() == 0) {
							publishData();
						}
						progress.dismiss();
						commentbtn.setClickable(true);
					}

					@Override
					public void onFailure(int arg0, String arg1) {
						progress.dismiss();
						commentbtn.setClickable(true);
					}
				});
			}

			@Override
			public void onFailure(int arg0, String arg1) {

				progress.dismiss();
				commentbtn.setClickable(true);
			}
		});
	}

}
