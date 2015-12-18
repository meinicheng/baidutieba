package com.lshapp.baidutieba.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lshapp.baidutieba.CustomApplcation;
import com.lshapp.baidutieba.R;
import com.lshapp.baidutieba.activity.SetMyInfoActivity;
import com.lshapp.baidutieba.base.QiangYu;
import com.lshapp.baidutieba.base.User;
import com.lshapp.baidutieba.utils.TimeUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * @author kingofglory email: kingofglory@yeah.net blog: http:www.google.com
 * @date 2014-2-24 TODO
 */

public class AIContentAdapter extends BaseContentAdapter<QiangYu> {

	public static final String TAG = "AIContentAdapter";
	public static final int SAVE_FAVOURITE = 2;

	public AIContentAdapter(Context context, List<QiangYu> list) {
		super(context, list);
	}

	@Override
	public View getConvertView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.ai_item, null);
			viewHolder.userName = (TextView) convertView
					.findViewById(R.id.user_name);
			viewHolder.userLogo = (ImageView) convertView
					.findViewById(R.id.user_logo);
			// viewHolder.favMark = (ImageView) convertView
			// .findViewById(R.id.item_action_fav);
			viewHolder.titleText = (TextView) convertView
					.findViewById(R.id.title_text);
			viewHolder.contentText = (TextView) convertView
					.findViewById(R.id.content_text);
			viewHolder.listtime = (TextView) convertView
					.findViewById(R.id.user_time);

			viewHolder.contentImage = (ImageView) convertView
					.findViewById(R.id.content_image);

			viewHolder.user_size = (TextView) convertView
					.findViewById(R.id.user_size);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		final QiangYu entity = dataList.get(position);
		// LogUtils.i("user", entity.toString());
		final User user = entity.getAuthor();
		if (user == null) {
			// LogUtils.i("user", "USER IS NULL");
		}
		if (user.getAvatar() == null) {
			// LogUtils.i("user", "USER avatar IS NULL");
		}
		String avatarUrl = null;
		if (user.getAvatar() != null) {
			avatarUrl = user.getAvatar();
		}
		ImageLoader.getInstance().displayImage(avatarUrl, viewHolder.userLogo,
				CustomApplcation.getInstance().getOptions(R.drawable.photo),
				new SimpleImageLoadingListener() {

					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						// TODO Auto-generated method stub
						super.onLoadingComplete(imageUri, view, loadedImage);
					}

				});
		viewHolder.userLogo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, SetMyInfoActivity.class);
				intent.putExtra("userdata", user);
				mContext.startActivity(intent);
				((Activity) mContext).overridePendingTransition(R.anim.in_anim,
						R.anim.out_anim);
			}
		});

		viewHolder.userName.setText(entity.getAuthor().getUsername());
		viewHolder.titleText.setText(entity.getTblisttitle());
		if (entity.getScore() == null) {
			viewHolder.user_size.setText("  " + "0");
		} else {
			viewHolder.user_size.setText("  " + entity.getScore() + "");
		}
		viewHolder.contentText.setText(entity.getContent());
		long stringToLong = TimeUtil.stringToLong(entity.getUpdatedAt(),
				"yyyy-MM-dd HH:mm:ss");
		String ztime = TimeUtil.getDescriptionTimeFromTimestamp(stringToLong);
		viewHolder.listtime.setText(ztime);

		if (null == entity.getContentfigureurl()) {
			viewHolder.contentImage.setVisibility(View.GONE);
		} else {
			viewHolder.contentImage.setVisibility(View.VISIBLE);
			ImageLoader
					.getInstance()
					.displayImage(
							entity.getContentfigureurl().getFileUrl(mContext) == null ? ""
									: entity.getContentfigureurl().getFileUrl(
											mContext),
							viewHolder.contentImage,
							CustomApplcation.getInstance().getOptions(
									R.drawable.bg_pic_loading),
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
		return convertView;
	}

	public static class ViewHolder {
		public ImageView userLogo;
		public TextView userName;
		public TextView contentText;
		public TextView titleText;
		private TextView listtime;
		public ImageView contentImage;
		public TextView user_size;

	}

}