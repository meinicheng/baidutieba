package com.lshapp.baidutieba.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
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
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import com.lshapp.baidutieba.CustomApplcation;
import com.lshapp.baidutieba.R;
import com.lshapp.baidutieba.adapter.CommentAdapter;
import com.lshapp.baidutieba.base.Comment;
import com.lshapp.baidutieba.base.QiangYu;
import com.lshapp.baidutieba.base.User;
import com.lshapp.baidutieba.utils.BmobConstants;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class CommentActivity extends Activity implements OnScrollListener,
		OnClickListener {
	private QiangYu qiangYu;
	private String tbname;
	private TextView cnametv;
	private ListView clist;
	private RelativeLayout headlayout;
	private ImageView cuser_logo;
	private TextView cuser_name;
	private TextView cuser_time;
	private TextView ctitle_text;
	private TextView ccontent_text;
	private ImageView ccontent_image;
	private LinearLayout footerlayout;
	private TextView loadButton;
	private LinearLayout loading;
	private List<Comment> comments = new ArrayList<Comment>();
	private CommentAdapter adapter;
	private Button commentbtn;
	private EditText commentedit;
	private String scommentEdit;
	private int pageNum = 0;
	private int ifswitch;
	private LinearLayout tibu;
	private String qyname;
	BmobPushManager bmobPushManager;
	private ImageView cdelete;
	private User user;;
	private AlertDialog albumDialog;
	private ProgressDialog progress;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comment);
		qiangYu = (QiangYu) getIntent().getSerializableExtra("data");
		tbname = getIntent().getStringExtra("tbname");
		user = BmobUser.getCurrentUser(CommentActivity.this, User.class);

		progress = new ProgressDialog(CommentActivity.this);
		progress.setMessage("加载中...");
		progress.setCanceledOnTouchOutside(false);
		progress.show();
		
		InitView();
		cnametv.setText(tbname);
		clist.addHeaderView(headlayout);
		clist.addFooterView(footerlayout);
		qyname = qiangYu.getAuthor().getUsername();
		adapter = new CommentAdapter(CommentActivity.this, comments, qyname,
				qiangYu);
		clist.setAdapter(adapter);
		setData();
		setHeadView(qiangYu);
		clist.setOnScrollListener(this);
	}

	private void setData() {
		BmobQuery<Comment> query = new BmobQuery<Comment>();
		query.addWhereRelatedTo("crelation", new BmobPointer(qiangYu));
		query.include("user");
		query.order("createdAt");
		query.setSkip(BmobConstants.NUMBERS_PER_PAGE * pageNum);
		query.setLimit(BmobConstants.NUMBERS_PER_PAGE);
		query.findObjects(CommentActivity.this, new FindListener<Comment>() {

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

	private void setHeadView(QiangYu qy) {
		if (qy == null) {
			return;
		}
//		if (qyname.equals(user.getUsername())) {
//			cdelete.setVisibility(View.VISIBLE);
//		} else {
//			cdelete.setVisibility(View.GONE);
//		}

		cuser_name.setText(qy.getAuthor().getUsername());
		ctitle_text.setText(qy.getTblisttitle());
		cuser_time.setText(qy.getCreatedAt());
		ccontent_text.setText(qy.getContent());
		if (qy.getContentfigureurl() == null) {
			ccontent_image.setVisibility(View.GONE);
		} else {
			ImageLoader.getInstance()
					.displayImage(
							qy.getContentfigureurl().getFileUrl(
									CommentActivity.this) == null ? "" : qy
									.getContentfigureurl().getFileUrl(
											CommentActivity.this),
							ccontent_image,
							CustomApplcation.getInstance().getOptions(
									R.drawable.bg_pic_loading),
							new SimpleImageLoadingListener() {

								@Override
								public void onLoadingComplete(String imageUri,
										View view, Bitmap loadedImage) {
									super.onLoadingComplete(imageUri, view,
											loadedImage);
								}

							});
		}
		String b = qy.getAuthor().getAvatar();
		if (b == null || b.isEmpty()) {
			cuser_logo.setImageResource(R.drawable.photo);
		} else {
			ImageLoader.getInstance()
					.displayImage(
							qy.getAuthor().getAvatar(),
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
				R.layout.clisthead, null);
		cuser_logo = (ImageView) headlayout.findViewById(R.id.cuser_logo);
		cuser_name = (TextView) headlayout.findViewById(R.id.cuser_name);
		cuser_time = (TextView) headlayout.findViewById(R.id.cuser_time);
		ctitle_text = (TextView) headlayout.findViewById(R.id.ctitle_text);
		ccontent_text = (TextView) headlayout.findViewById(R.id.ccontent_text);
		ccontent_image = (ImageView) headlayout
				.findViewById(R.id.ccontent_image);


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
		cuser_logo.setOnClickListener(this);
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		onrestar();

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
			Log.d("CommentActivity", "00000000000000开始");
			// 自动加载,可以在这里放置异步加载数据的代码
			if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
				Log.d("CommentActivity", "2222222222222222");
				// 自动加载,可以在这里放置异步加载数据的代码
				if (ifswitch != 1 && loading.getVisibility() == 0) {
					Log.d("CommentActivity", "333333333333333333");
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.commentbtn:
			commentbtn.setClickable(false);
			
			scommentEdit = commentedit.getText().toString();
			if (TextUtils.isEmpty(scommentEdit)) {
				progress.dismiss();
				commentbtn.setClickable(true);
				Toast.makeText(CommentActivity.this, "请输入评论的内容", Toast.LENGTH_SHORT).show();
				return;
			}
			progress.show();	
			publishComment(user, scommentEdit);
			break;
		case R.id.loadButton:
			loadButton.setVisibility(View.GONE);
			tibu.setVisibility(View.VISIBLE);

			publishData();
			break;
		case R.id.cuser_logo:
			Intent intent = new Intent(CommentActivity.this,
					SetMyInfoActivity.class);
			intent.putExtra("userdata", qiangYu.getAuthor());
			startActivity(intent);
			overridePendingTransition(R.anim.in_anim, R.anim.out_anim);

			break;

//		case R.id.cdelete:
//			showAlbumDialog();
//
//			break;
		}

	}

	public void showAlbumDialog() {
		albumDialog = new AlertDialog.Builder(CommentActivity.this).create();
		albumDialog.setCanceledOnTouchOutside(true);
		View v = LayoutInflater.from(CommentActivity.this).inflate(
				R.layout.digbargridview, null);
		albumDialog.show();
		albumDialog.setContentView(v);
		albumDialog.getWindow().setGravity(Gravity.CENTER);

		TextView delete = (TextView) v.findViewById(R.id.digdelete);
		TextView back = (TextView) v.findViewById(R.id.digback);

		delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				albumDialog.dismiss();
				QiangYu qydeete = new QiangYu();
				qydeete.setObjectId(qiangYu.getObjectId());
				qydeete.delete(CommentActivity.this, new DeleteListener() {

					@Override
					public void onSuccess() {
						setResult(RESULT_OK);
						finish();
					}

					@Override
					public void onFailure(int arg0, String arg1) {

					}
				});

			}
		});
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				albumDialog.dismiss();

			}
		});
	}

	private void hideSoftInput() {
		InputMethodManager imm = (InputMethodManager) this
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(commentedit.getWindowToken(), 0);
	}

	private void onrestar() {
		BmobQuery<Comment> query = new BmobQuery<Comment>();
		query.addWhereRelatedTo("crelation", new BmobPointer(qiangYu));
		query.include("user");
		query.order("createdAt");
		query.setLimit(adapter.getCount());
		query.findObjects(CommentActivity.this, new FindListener<Comment>() {

			@Override
			public void onSuccess(List<Comment> arg0) {
				if (arg0.size() != 0) {
					comments.clear();
					comments.addAll(arg0);
					adapter.notifyDataSetChanged();
				}
			}

			@Override
			public void onError(int arg0, String arg1) {
			}
		});
	}

	private void publishData() {
		BmobQuery<Comment> query = new BmobQuery<Comment>();
		query.addWhereRelatedTo("crelation", new BmobPointer(qiangYu));
		query.include("user");
		query.order("createdAt");
		query.setSkip(adapter.getCount());
		query.setLimit(BmobConstants.NUMBERS_PER_PAGE);
		query.findObjects(CommentActivity.this, new FindListener<Comment>() {

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

	private void publishComment(final User user, String scommentEdit2) {
		final Comment comment = new Comment();
		comment.setUser(user);
		comment.setCommentContent(scommentEdit2);
		if (!user.getUsername().equals(qyname)) {
			comment.setZhuhfname(qyname);
		}
		comment.setYtcontent(qiangYu.getTblisttitle());
		comment.setCtbname(tbname);
		comment.setGozhuti(qiangYu);
		comment.save(CommentActivity.this, new SaveListener() {

			@Override
			public void onSuccess() {
				commentedit.setText("");
				hideSoftInput();

				BmobRelation relation = new BmobRelation();
				relation.add(comment);
				qiangYu.setCrelation(relation);
				qiangYu.increment("score");
				qiangYu.update(CommentActivity.this, new UpdateListener() {

					@Override
					public void onSuccess() {
						if (user.getUsername().equals(qyname)) {

						} else {
							bmobPushManager = new BmobPushManager(
									CommentActivity.this);

							BmobQuery<BmobInstallation> query = BmobInstallation
									.getQuery();
							// 并添加条件为设备类型属于android
							query.addWhereEqualTo("uid", qyname);
							// 设置推送条件给bmobPushManager对象。
							bmobPushManager.setQuery(query);
							// 设置推送消息，服务端会根据上面的查询条件，来进行推送这条消息
							bmobPushManager.pushMessage("hflist");
						}
						if (loadButton.getVisibility() == 0) {
							publishData();
						}
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
