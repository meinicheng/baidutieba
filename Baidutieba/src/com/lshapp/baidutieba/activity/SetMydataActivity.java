package com.lshapp.baidutieba.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

import com.lshapp.baidutieba.CustomApplcation;
import com.lshapp.baidutieba.R;
import com.lshapp.baidutieba.base.User;
import com.lshapp.baidutieba.utils.BmobConstants;
import com.lshapp.baidutieba.utils.CacheUtils;
import com.lshapp.baidutieba.utils.PhotoUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class SetMydataActivity extends Activity implements OnClickListener {

	private TextView databaocun;
	private TextView setmyname;
	private EditText editmyread;
	private User user;
	private ImageView setmytp;
	private AlertDialog albumDialog;
	private String dateTime;
	private String iconUrl;
	private Bitmap bitmap;
	private String editstr;
	private ProgressBar dataprobtn;
	public static final String action = "jason.broadcast.action";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setmydatalayout);

		user = BmobUser.getCurrentUser(SetMydataActivity.this, User.class);
		initview();
		setmyname.setText(user.getUsername());
		editmyread.setText(user.getSetread());
		String a = user.getAvatar();

		if (a == null || a.isEmpty()) {
			setmytp.setImageResource(R.drawable.photo);
		} else {

			ImageLoader.getInstance()
					.displayImage(
							a,
							setmytp,
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

	}

	private void initview() {
		setmytp = (ImageView) findViewById(R.id.setmytp);
		databaocun = (TextView) findViewById(R.id.databaocun);
		setmyname = (TextView) findViewById(R.id.setmyname);
		editmyread = (EditText) findViewById(R.id.editmyread);
		dataprobtn = (ProgressBar) findViewById(R.id.dataprobtn);
		databaocun.setOnClickListener(this);
		setmytp.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.setmytp:
			// 设置图片
			showAlbumDialog();

			break;

		case R.id.databaocun:
			// 保存的修改信息
			dataprobtn.setVisibility(View.VISIBLE);
			editstr = editmyread.getText().toString();
			updatedata(editstr);

			break;
		}

	}

	private void updatedata(String editstr2) {
		User currentUser = BmobUser.getCurrentUser(SetMydataActivity.this,
				User.class);
		currentUser.setSetread(editstr2);
		currentUser.update(SetMydataActivity.this, new UpdateListener() {

			@Override
			public void onSuccess() {
				dataprobtn.setVisibility(View.GONE);
				Intent intent = new Intent(action);
				sendBroadcast(intent);
				setResult(3);
				finish();
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				dataprobtn.setVisibility(View.GONE);

			}
		});
	}

	public void showAlbumDialog() {
		albumDialog = new AlertDialog.Builder(SetMydataActivity.this).create();
		albumDialog.setCanceledOnTouchOutside(true);
		View v = LayoutInflater.from(SetMydataActivity.this).inflate(
				R.layout.xiangcedilog, null);
		albumDialog.show();
		albumDialog.setContentView(v);
		albumDialog.getWindow().setGravity(Gravity.CENTER);
		
		TextView albumPic = (TextView) v.findViewById(R.id.album_pic);
		albumPic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				albumDialog.dismiss();
				Intent intent = new Intent(Intent.ACTION_PICK, null);
				intent.setDataAndType(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
				startActivityForResult(intent, 2);
			}
		});
		
		
	}

	boolean isFromCamera = false;// 区分拍照旋转

	private void getAvataFromAlbum() {
		Intent intent2 = new Intent(Intent.ACTION_GET_CONTENT);
		intent2.setType("image/*");
		startActivityForResult(intent2, 2);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case 2:
				Uri uri = null;
				if (data == null) {
					return;
				}
				if (resultCode == RESULT_OK) {
					if (!Environment.getExternalStorageState().equals(
							Environment.MEDIA_MOUNTED)) {
						return;
					}
					isFromCamera = false;
					uri = data.getData();
					startImageAction(uri, 200, 200, 3, true);
				} else {
				}
				break;
			case 3:// 裁剪头像返回
					// TODO sent to crop
				if (data == null) {
					// Toast.makeText(this, "取消选择", Toast.LENGTH_SHORT).show();
					return;
				} else {
					saveCropAvator(data);
				}
				// 初始化文件路径
				filePath = "";
				// 上传头像
				uploadAvatar();
				break;
			}
		}

	}

	public String filePath = "";
	String path;
	int degree = 0;

	private void saveCropAvator(Intent data) {
		Bundle extras = data.getExtras();
		if (extras != null) {
			Bitmap bitmap = extras.getParcelable("data");
			Log.i("life", "avatar - bitmap = " + bitmap);
			if (bitmap != null) {
				bitmap = PhotoUtil.toRoundCorner(bitmap, 10);
				if (isFromCamera && degree != 0) {
					bitmap = PhotoUtil.rotaingImageView(degree, bitmap);
				}
				setmytp.setImageBitmap(bitmap);
				// 保存图片
				String filename = new SimpleDateFormat("yyMMddHHmmss")
						.format(new Date()) + ".png";
				path = BmobConstants.MyAvatarDir + filename;
				PhotoUtil.saveBitmap(BmobConstants.MyAvatarDir, filename,
						bitmap, true);
				// 上传头像
				if (bitmap != null && bitmap.isRecycled()) {
					bitmap.recycle();
				}
			}
		}
	}

	private void uploadAvatar() {
		BmobLog.i("头像地址：" + path);
		final BmobFile bmobFile = new BmobFile(new File(path));
		bmobFile.upload(this, new UploadFileListener() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				String url = bmobFile.getFileUrl(SetMydataActivity.this);
				// 更新BmobUser对象
				updateUserAvatar(url);
			}

			@Override
			public void onProgress(Integer arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onFailure(int arg0, String msg) {
				// TODO Auto-generated method stub
			}
		});
	}

	private void updateUserAvatar(final String url) {
		User user = BmobUser.getCurrentUser(SetMydataActivity.this, User.class);
		user.setAvatar(url);
		user.setObjectId(user.getObjectId());
		user.update(this);

		Intent intent = new Intent(action);

		sendBroadcast(intent);

	}

	private void startImageAction(Uri uri, int outputX, int outputY,
			int requestCode, boolean isCrop) {
		Intent intent = null;
		if (isCrop) {
			intent = new Intent("com.android.camera.action.CROP");
		} else {
			intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		}
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", outputX);
		intent.putExtra("outputY", outputY);
		intent.putExtra("scale", true);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		intent.putExtra("return-data", true);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true); // no face detection
		startActivityForResult(intent, requestCode);
	}

	public String saveToSdCard(Bitmap bitmap) {
		String files = CacheUtils.getCacheDirectory(SetMydataActivity.this,
				true, "icon") + dateTime + "_12.jpg";
		File file = new File(files);
		try {
			FileOutputStream out = new FileOutputStream(file);
			if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
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
		return file.getAbsolutePath();
	}
}
