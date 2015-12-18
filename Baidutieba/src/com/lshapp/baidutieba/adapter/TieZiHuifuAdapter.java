package com.lshapp.baidutieba.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lshapp.baidutieba.CustomApplcation;
import com.lshapp.baidutieba.R;
import com.lshapp.baidutieba.base.Comment;
import com.lshapp.baidutieba.utils.TimeUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class TieZiHuifuAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater inflater;
	List<Comment> listdata;
	private Comment comment;

	public TieZiHuifuAdapter(Context context, List<Comment> hfListItems) {
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
			// ʵ����ViewHolder
			holder = new ViewHolder();
			// ���listview��item��Ŀxml�����ļ�
			convertView = inflater.inflate(R.layout.zhutihuifulayout, null);
			// Ȼ����item��Ŀxml�����ļ���Ŀؼ�
			holder.logo = (ImageView) convertView.findViewById(R.id.hf_logo);
			holder.name = (TextView) convertView.findViewById(R.id.hf_name);
			holder.time = (TextView) convertView.findViewById(R.id.hf_time);
			holder.content = (TextView) convertView.findViewById(R.id.hf_text);
			holder.ytcontent = (TextView) convertView
					.findViewById(R.id.hf_ytcontent);
			holder.hftbname = (TextView) convertView
					.findViewById(R.id.hf_tbname);
			// ���setTag,Ҳ�������Ʊ���convertView��Ҳ���Ǳ�����item
			convertView.setTag(holder);

		} else {
			// ���convertViewǰ�汣���������Ͳ�Ϊ�գ���ô����֮���item�ù�����
			holder = (ViewHolder) convertView.getTag();
		}
		comment = listdata.get(position);
		String strname = comment.getUser().getUsername();
		Log.d("CommentActivity", strname + comment.getUser().getUsername()
				+ "++++name");

		holder.name.setText(strname);
		holder.time.setText(comment.getCreatedAt());
		long stringToLong = TimeUtil.stringToLong(comment.getCreatedAt(),
				"yyyy-MM-dd HH:mm:ss");
		String ztime = TimeUtil.getDescriptionTimeFromTimestamp(stringToLong);
		holder.time.setText(ztime);
		holder.content.setText(comment.getCommentContent());
		holder.ytcontent.setText(" ԭ����" + comment.getYtcontent());
		holder.hftbname.setText(comment.getCtbname() + "��");

		String avatarUrl;
		String a = comment.getUser().getAvatar();
		if (a == null || a.isEmpty()) {
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
