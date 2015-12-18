package com.lshapp.baidutieba.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

import com.lshapp.baidutieba.R;
import com.lshapp.baidutieba.base.QiangYu;
import com.lshapp.baidutieba.base.User;
import com.lshapp.baidutieba.utils.CacheUtils;

/**
 * @author kingofglory email: kingofglory@yeah.net blog: http:www.google.com
 * @date 2014-3-19 TODO
 */

public class EditActivity extends BasePageActivity implements OnClickListener {

	private static final int REQUEST_CODE_ALBUM = 1;
	private static final int REQUEST_CODE_CAMERA = 2;
	EditText content;

	LinearLayout openLayout;
	LinearLayout takeLayout;

	ImageView albumPic;
	ImageView takePic;
	// Button commitButton;
	String dateTime, tbname;

	@Override
	protected void setLayoutView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_edit);
		tbname = getIntent().getStringExtra("tiebaname");
	}

	@Override
	protected void findViews() {
		content = (EditText) findViewById(R.id.edit_content);
		eimgxie = (ImageView) findViewById(R.id.eimgxie);
		openLayout = (LinearLayout) findViewById(R.id.open_layout);
		takeLayout = (LinearLayout) findViewById(R.id.take_layout);
		sstitle = (EditText) findViewById(R.id.sstitle);
		albumPic = (ImageView) findViewById(R.id.open_pic);
		takePic = (ImageView) findViewById(R.id.take_pic);
		// commitButton = (Button)findViewById(R.id.commit_edit);
	}

	@Override
	protected void setupViews(Bundle bundle) {
		// TODO Auto-generated method stub
		this.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE
						| WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		eimgxie.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String commitContent = content.getText().toString().trim();
				String committitle = sstitle.getText().toString().trim();
				if (TextUtils.isEmpty(commitContent)) {
					// ActivityUtil.show(mContext, "内容不能为空");
					return;
				}
				if (targeturl == null) {
					publishWithoutFigure(committitle, commitContent, null);
				} else {
					publish(committitle, commitContent);
				}
			}
		});

	}

	@Override
	protected void setListener() {
		// TODO Auto-generated method stub
		openLayout.setOnClickListener(this);
		takeLayout.setOnClickListener(this);

		albumPic.setOnClickListener(this);
		takePic.setOnClickListener(this);
		// commitButton.setOnClickListener(this);
	}

	@Override
	protected void fetchData() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		// case R.id.commit_edit:
		// String commitContent = content.getText().toString().trim();
		// if(TextUtils.isEmpty(commitContent)){
		// ActivityUtil.show(this, "内容不能为空");
		// return;
		// }
		// if(targeturl == null){
		// publishWithoutFigure(commitContent, null);
		// }else{
		// publish(commitContent);
		// }
		// break;
		case R.id.open_layout:
			Date date1 = new Date(System.currentTimeMillis());
			dateTime = date1.getTime() + "";
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI,
					"image/*");
			startActivityForResult(intent, REQUEST_CODE_ALBUM);
			break;
		case R.id.take_layout:
			Date date = new Date(System.currentTimeMillis());
			dateTime = date.getTime() + "";
			File f = new File(CacheUtils.getCacheDirectory(mContext, true,
					"pic") + dateTime);
			if (f.exists()) {
				f.delete();
			}
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Uri uri = Uri.fromFile(f);
			Log.e("uri", uri + "");

			Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			camera.putExtra(MediaStore.EXTRA_OUTPUT, uri);
			startActivityForResult(camera, REQUEST_CODE_CAMERA);
			break;
		default:
			break;
		}
	}

	/*
	 * 发表带图片
	 */
	private void publish(final String committitle, final String commitContent) {

		// final BmobFile figureFile = new BmobFile(QiangYu.class, new
		// File(targeturl));

		final BmobFile figureFile = new BmobFile(new File(targeturl));

		figureFile.upload(mContext, new UploadFileListener() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				// LogUtils.i(TAG,
				// "上传文件成功。" + figureFile.getFileUrl(EditActivity.this));
				publishWithoutFigure(committitle, commitContent, figureFile);

			}

			@Override
			public void onProgress(Integer arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				// LogUtils.i(TAG, "上传文件失败。" + arg1);
			}
		});

	}

	private void publishWithoutFigure(final String committitle,
			final String commitContent,
			final BmobFile figureFile) {
		User user = BmobUser.getCurrentUser(mContext, User.class);

		final QiangYu qiangYu = new QiangYu();
		qiangYu.setAuthor(user);
		qiangYu.setContent(commitContent);
		qiangYu.setTblisttitle(committitle);
		qiangYu.setGltbname(tbname);
		if (figureFile != null) {
			qiangYu.setContentfigureurl(figureFile);
		}
		qiangYu.setLove(0);
		qiangYu.setHate(0);
		qiangYu.setShare(0);
		qiangYu.setComment(0);
		qiangYu.setPass(true);
		qiangYu.save(mContext, new SaveListener() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				// ActivityUtil.show(EditActivity.this, "发表成功！");
				// LogUtils.i(TAG, "创建成功。");
				setResult(RESULT_OK);
				finish();
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				// ActivityUtil.show(EditActivity.this, "发表失败！yg" + arg1);
				// LogUtils.i(TAG, "创建失败。" + arg1);
			}
		});
	}

	String targeturl = null;
	private ImageView eimgxie;
	private EditText sstitle;

	@SuppressLint("NewApi")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		// LogUtils.i(TAG, "get album:");
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUEST_CODE_ALBUM:
				String fileName = null;
				if (data != null) {
					Uri originalUri = data.getData();
					ContentResolver cr = getContentResolver();
					Cursor cursor = cr.query(originalUri, null, null, null,
							null);
					if (cursor.moveToFirst()) {
						do {
							fileName = cursor.getString(cursor
									.getColumnIndex("_data"));
							// LogUtils.i(TAG, "get album:" + fileName);
						} while (cursor.moveToNext());
					}
					Bitmap bitmap = compressImageFromFile(fileName);
					targeturl = saveToSdCard(bitmap);
					albumPic.setBackgroundDrawable(new BitmapDrawable(bitmap));
					takeLayout.setVisibility(View.GONE);
				}
				break;
			case REQUEST_CODE_CAMERA:
				String files = CacheUtils.getCacheDirectory(mContext, true,
						"pic") + dateTime;
				File file = new File(files);
				if (file.exists()) {
					Bitmap bitmap = compressImageFromFile(files);
					targeturl = saveToSdCard(bitmap);
					takePic.setBackgroundDrawable(new BitmapDrawable(bitmap));
					openLayout.setVisibility(View.GONE);
				} else {

				}
				break;
			default:
				break;
			}
		}
	}

	private Bitmap compressImageFromFile(String srcPath) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		newOpts.inJustDecodeBounds = true;// 只读边,不读内容
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);

		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		float hh = 800f;//
		float ww = 480f;//
		int be = 1;
		if (w > h && w > ww) {
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置采样率

		newOpts.inPreferredConfig = Config.ARGB_8888;// 该模式是默认的,可不设
		newOpts.inPurgeable = true;// 同时设置才会有效
		newOpts.inInputShareable = true;// 。当系统内存不够时候图片自动被回收

		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		// return compressBmpFromBmp(bitmap);//原来的方法调用了这个方法企图进行二次压缩
		// 其实是无效的,大家尽管尝试
		return bitmap;
	}

	public String saveToSdCard(Bitmap bitmap) {
		String files = CacheUtils.getCacheDirectory(mContext, true, "pic")
				+ dateTime + "_11.jpg";
		File file = new File(files);
		try {
			FileOutputStream out = new FileOutputStream(file);
			if (bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out)) {
				out.flush();
				out.close();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// LogUtils.i(TAG, file.getAbsolutePath());
		return file.getAbsolutePath();
	}

}
