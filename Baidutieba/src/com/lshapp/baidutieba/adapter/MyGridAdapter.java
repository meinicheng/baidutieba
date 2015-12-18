package com.lshapp.baidutieba.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import cn.bmob.v3.datatype.BmobRelation;

import com.lshapp.baidutieba.R;
import com.lshapp.baidutieba.base.TieBaName;

public class MyGridAdapter extends BaseAdapter {
	private TieBaName abc;
	private List<TieBaName> lstImageItem;
	private LayoutInflater inflater;
	public MyGridAdapter(Context mContext, List<TieBaName> arg0) {
		this.lstImageItem = arg0;
		this.inflater = LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return lstImageItem.size()+1;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return lstImageItem.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			//实例化ViewHolder
			holder = new ViewHolder();
			//获得listview的item条目xml布局文件
			convertView = inflater.inflate(R.layout.gridview_item, null);
			//然后获得item条目xml布局文件里的控件
			holder.listimg = (ImageView) convertView.findViewById(R.id.gv_img);
			holder.listimgtv = (TextView) convertView.findViewById(R.id.gv_tv);
			holder.gvadd = (ImageView) convertView.findViewById(R.id.gv_add);
			//最后setTag,也就是类似保存convertView，也就是保存了item
			convertView.setTag(holder);

		} else {
			//如果convertView前面保存过，这里就不为空，那么我们之间把item拿过来用
			holder = (ViewHolder) convertView.getTag();
		}
		if(position<lstImageItem.size()){
		abc = lstImageItem.get(position);
		if (abc != null) {
			holder.listimgtv.setText(abc.getName());
			holder.listimg.setVisibility(View.VISIBLE);
			holder.listimgtv.setVisibility(View.VISIBLE);
			holder.gvadd.setVisibility(View.GONE);
		}}else{
			
			holder.listimg.setVisibility(View.GONE);
			holder.listimgtv.setVisibility(View.GONE);
			holder.gvadd.setVisibility(View.VISIBLE);
		}
		return convertView;
	}

	class ViewHolder {
		ImageView listimg;
		TextView listimgtv;
		ImageView gvadd;
	}
}
