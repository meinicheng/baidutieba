package com.lshapp.baidutieba.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lshapp.baidutieba.CustomApplcation;
import com.lshapp.baidutieba.R;
import com.lshapp.baidutieba.activity.SetMyInfoActivity;
import com.lshapp.baidutieba.base.Comment;
import com.lshapp.baidutieba.base.User;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class HuifulistAdapter extends BaseAdapter {
	private Context mContext;
	private List<Comment> dataList;
	private LayoutInflater mInflater;
	private Comment comment;
	private String louzhu;
	private View contentView;
	private LinearLayout pshouchang;
	private LinearLayout phuifu;
	private ImageView pdelete;

	public HuifulistAdapter(Context context, List<Comment> comments,
			String louzhu) {
		mContext = context;
		dataList = comments;
		mInflater = LayoutInflater.from(mContext);
		this.louzhu = louzhu;
	}

	@Override
	public int getCount() {
		return dataList.size();
	}

	@Override
	public Object getItem(int position) {
		return dataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.item_huifulist, null);

			holder.comment_louzhu = (ImageView) convertView
					.findViewById(R.id.hlcomment_louzhu);
			holder.comment_name = (TextView) convertView
					.findViewById(R.id.hlcomment_name);

			holder.comment_text = (TextView) convertView
					.findViewById(R.id.hlcomment_text);
			
			holder.comment_logo = (ImageView) convertView
					.findViewById(R.id.hlcomment_logo);

			holder.hltime = (TextView) convertView.findViewById(R.id.hltime);

			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();

		}
		comment = dataList.get(position);
		final User user = comment.getUser();
		if (comment.getUser().getUsername().equals(louzhu)) {
			holder.comment_louzhu.setVisibility(View.VISIBLE);
		} else {
			holder.comment_louzhu.setVisibility(View.GONE);
		}
		String a = dataList.get(position).getUser().getAvatar();
		holder.comment_logo.setImageResource(R.drawable.photo);
		if (a == null || a.isEmpty() == true) {

			holder.comment_logo.setImageResource(R.drawable.photo);
		} else {

			ImageLoader.getInstance()
					.displayImage(
							dataList.get(position).getUser().getAvatar(),
							holder.comment_logo,
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

		final String uidname = comment.getUser().getUsername();
		holder.comment_name.setText(uidname);
		holder.comment_text.setText(comment.getCommentContent());

		holder.hltime.setText(comment.getCreatedAt());
		holder.comment_logo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, SetMyInfoActivity.class);
				intent.putExtra("userdata", user);
				mContext.startActivity(intent);
				((Activity) mContext).overridePendingTransition(R.anim.in_anim,
						R.anim.out_anim);
			}
		});

		return convertView;
	}

	class ViewHolder {
		ImageView comment_logo;
		TextView comment_name;
		ImageView comment_louzhu;
		TextView comment_text;
		TextView hltime;

	}

}
