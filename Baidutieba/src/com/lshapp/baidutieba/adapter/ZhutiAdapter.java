package com.lshapp.baidutieba.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lshapp.baidutieba.CustomApplcation;
import com.lshapp.baidutieba.R;
import com.lshapp.baidutieba.base.QiangYu;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class ZhutiAdapter extends BaseAdapter {
	private Context context;
	private List<QiangYu> litstdata;
	private LayoutInflater inflater;
	private QiangYu qy;
	private String date;

	public ZhutiAdapter(Context context, List<QiangYu> mListItems) {
		this.context = context;
		litstdata = mListItems;
		this.inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return litstdata.size();
	}

	@Override
	public Object getItem(int position) {
		return litstdata.get(position);
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
			convertView = inflater.inflate(R.layout.zhutiadapteritem, null);
			holder.zhuti_logo = (ImageView) convertView
					.findViewById(R.id.zhuti_logo);
			holder.zhuti_name = (TextView) convertView
					.findViewById(R.id.zhuti_name);
			holder.zhuti_size = (TextView) convertView
					.findViewById(R.id.zhuti_size);
			holder.zhuti_time = (TextView) convertView
					.findViewById(R.id.zhuti_time);
			holder.title_zhuti = (TextView) convertView
					.findViewById(R.id.title_zhuti);
			holder.content_zhuti = (TextView) convertView
					.findViewById(R.id.content_zhuti);
			holder.zhutibaname = (TextView) convertView
					.findViewById(R.id.zhutibaname);
			holder.zhuti_image = (ImageView) convertView
					.findViewById(R.id.zhuti_image);
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		qy = litstdata.get(position);
		holder.zhuti_name.setText(qy.getAuthor().getUsername());
		if (qy.getScore() == null) {
			holder.zhuti_size.setText(" 0");
		} else {
			holder.zhuti_size.setText(" " + qy.getScore());
		}
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		Date d;
		try {
			d = formatter.parse(qy.getCreatedAt());
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			date = format.format(d);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		holder.zhuti_time.setText(date);
		holder.title_zhuti.setText(qy.getTblisttitle());
		holder.content_zhuti.setText(qy.getContent());
		holder.zhutibaname.setText(qy.getGltbname() + "°É");
		String avatarUrl = null;
		if (qy.getAuthor().getAvatar() != null) {
			avatarUrl = qy.getAuthor().getAvatar();
		}
		ImageLoader.getInstance().displayImage(avatarUrl, holder.zhuti_logo,
				CustomApplcation.getInstance().getOptions(R.drawable.photo),
				new SimpleImageLoadingListener() {

					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						// TODO Auto-generated method stub
						super.onLoadingComplete(imageUri, view, loadedImage);
					}

				});

		if (null == qy.getContentfigureurl()) {
			holder.zhuti_image.setVisibility(View.GONE);
		} else {
			holder.zhuti_image.setVisibility(View.VISIBLE);
			ImageLoader.getInstance().displayImage(
					qy.getContentfigureurl().getFileUrl(context) == null ? ""
							: qy.getContentfigureurl().getFileUrl(context),
					holder.zhuti_image,
					CustomApplcation.getInstance().getOptions(
							R.drawable.bg_pic_loading),
					new SimpleImageLoadingListener() {

						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
							super.onLoadingComplete(imageUri, view, loadedImage);
						}

					});
		}
		return convertView;
	}

	class ViewHolder {

		ImageView zhuti_logo;
		TextView zhuti_name;
		TextView zhuti_size;
		TextView zhuti_time;
		TextView title_zhuti;
		TextView content_zhuti;
		ImageView zhuti_image;
		TextView zhutibaname;

	}

}
