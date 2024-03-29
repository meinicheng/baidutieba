package com.lshapp.baidutieba.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lshapp.baidutieba.CustomApplcation;
import com.lshapp.baidutieba.R;
import com.lshapp.baidutieba.activity.SetMyInfoActivity;
import com.lshapp.baidutieba.adapter.MyGridAdapter.ViewHolder;
import com.lshapp.baidutieba.base.Comment;
import com.lshapp.baidutieba.base.User;
import com.lshapp.baidutieba.utils.TimeUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class HfAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater inflater;
	List<Comment> listdata;
	private Comment comment;

	public HfAdapter(Context context, List<Comment> hfListItems) {
		this.context = context;
		listdata = hfListItems;
		this.inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return listdata.size();
	}

	@Override
	public Object getItem(int position) {
		return listdata.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			//实例化ViewHolder
			holder = new ViewHolder();
			//获得listview的item条目xml布局文件
			convertView = inflater.inflate(R.layout.item_hf, null);
			//然后获得item条目xml布局文件里的控件
			holder.logo = (ImageView) convertView.findViewById(R.id.hf_logo);
			holder.name = (TextView) convertView.findViewById(R.id.hf_name);
			holder.time = (TextView) convertView.findViewById(R.id.hf_time);
			holder.content = (TextView) convertView.findViewById(R.id.hf_text);
			holder.ytcontent = (TextView) convertView.findViewById(R.id.hf_ytcontent);
			holder.hftbname = (TextView) convertView.findViewById(R.id.hf_tbname);
			//最后setTag,也就是类似保存convertView，也就是保存了item
			convertView.setTag(holder);

		} else {
			//如果convertView前面保存过，这里就不为空，那么我们之间把item拿过来用
			holder = (ViewHolder) convertView.getTag();
		}
		comment = listdata.get(position);
		final User user = comment.getUser();
		String name = comment.getUser().getUsername();
		
		holder.name.setText(name);
		holder.time.setText(comment.getCreatedAt());
		long stringToLong = TimeUtil.stringToLong(comment.getCreatedAt(), "yyyy-MM-dd HH:mm:ss");
		String ztime = TimeUtil.getDescriptionTimeFromTimestamp(stringToLong);
		holder.time.setText(ztime);
		holder.content.setText(comment.getCommentContent());
		holder.ytcontent.setText(" 原帖："+comment.getYtcontent());
		holder.hftbname.setText(comment.getCtbname()+"吧");
		
		String avatarUrl;
		String a = comment.getUser().getAvatar();
		if (a ==null || a.isEmpty()) {
			holder.logo.setImageResource(R.drawable.photo);
		} else {

			avatarUrl = comment.getUser().getAvatar();
			ImageLoader.getInstance()
					.displayImage(
							avatarUrl,
							holder.logo,
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
		holder.logo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, SetMyInfoActivity.class);
				intent.putExtra("userdata", user);
				context.startActivity(intent);
				((Activity) context).overridePendingTransition(R.anim.in_anim,
						R.anim.out_anim);
			}
		});
		
		
		return convertView;
	}

	class ViewHolder {
		ImageView logo;
		TextView name;
		TextView time;
		TextView content;
		TextView ytcontent;
		TextView hftbname;
		
	}

}
