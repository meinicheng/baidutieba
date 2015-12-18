package com.lshapp.baidutieba.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import cn.bmob.v3.datatype.BmobGeoPoint;

import com.lshapp.baidutieba.CustomApplcation;
import com.lshapp.baidutieba.R;
import com.lshapp.baidutieba.activity.SetMyInfoActivity;
import com.lshapp.baidutieba.base.User;
import com.lshapp.baidutieba.base.ViewHolder;
import com.lshapp.baidutieba.utils.ImageLoadOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 附近的人
 * 
 * @ClassName: BlackListAdapter
 * @Description: TODO
 * @author smile
 * @date 2014-6-24 下午5:27:14
 */
public class NearPeopleAdapter extends BaseListAdapter<User> {

	public NearPeopleAdapter(Context context, List<User> list) {
		super(context, list);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View bindView(int arg0, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_near_people, null);
		}
		final User contract = getList().get(arg0);
		TextView tv_name = ViewHolder.get(convertView, R.id.tv_name);
		TextView tv_distance = ViewHolder.get(convertView, R.id.tv_distance);
		TextView tv_logintime = ViewHolder.get(convertView, R.id.tv_logintime);
		ImageView iv_avatar = ViewHolder.get(convertView, R.id.iv_avatar);
		String avatar = contract.getAvatar();
		if (avatar != null && !avatar.equals("")) {
			ImageLoader.getInstance().displayImage(avatar, iv_avatar,
					ImageLoadOptions.getOptions());
		} else {
			iv_avatar.setImageResource(R.drawable.photo);
		}
		BmobGeoPoint location = contract.getLocation();
		String currentLat = CustomApplcation.getInstance().getLatitude();
		String currentLong = CustomApplcation.getInstance().getLongtitude();
		if (location != null && !currentLat.equals("")
				&& !currentLong.equals("")) {
			double distance = DistanceOfTwoPoints(
					Double.parseDouble(currentLat),
					Double.parseDouble(currentLong), contract.getLocation()
							.getLatitude(), contract.getLocation()
							.getLongitude());
			tv_distance.setText(String.valueOf(distance) + "米");
		} else {
			tv_distance.setText("未知");
		}
		tv_name.setText(contract.getUsername());
		tv_logintime.setText("最近登录时间:" + contract.getUpdatedAt());
		iv_avatar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, SetMyInfoActivity.class);
				intent.putExtra("userdata", contract);
				mContext.startActivity(intent);
				((Activity) mContext).overridePendingTransition(R.anim.in_anim,
						R.anim.out_anim);
			}
		});

		return convertView;
	}

	private static final double EARTH_RADIUS = 6378137;

	private static double rad(double d) {
		return d * Math.PI / 180.0;
	}

	/**
	 * 根据两点间经纬度坐标（double值），计算两点间距离，
	 * 
	 * @param lat1
	 * @param lng1
	 * @param lat2
	 * @param lng2
	 * @return 距离：单位为米
	 */
	public static double DistanceOfTwoPoints(double lat1, double lng1,
			double lat2, double lng2) {
		double radLat1 = rad(lat1);
		double radLat2 = rad(lat2);
		double a = radLat1 - radLat2;
		double b = rad(lng1) - rad(lng2);
		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
				+ Math.cos(radLat1) * Math.cos(radLat2)
				* Math.pow(Math.sin(b / 2), 2)));
		s = s * EARTH_RADIUS;
		s = Math.round(s * 10000) / 10000;
		return s;
	}

}
