package com.lshapp.baidutieba.adapter;

import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

import com.lshapp.baidutieba.CustomApplcation;
import com.lshapp.baidutieba.R;
import com.lshapp.baidutieba.activity.SetMyInfoActivity;
import com.lshapp.baidutieba.base.User;
import com.lshapp.baidutieba.base.ViewHolder;
import com.lshapp.baidutieba.utils.CollectionUtils;
import com.lshapp.baidutieba.utils.ImageLoadOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * �µĺ�������
 * 
 * @ClassName: NewFriendAdapter
 * @Description: TODO
 * @author smile
 * @date 2014-6-9 ����1:26:12
 */
public class NewFriendAdapter extends BaseListAdapter<BmobInvitation> {

	public NewFriendAdapter(Context context, List<BmobInvitation> list) {
		super(context, list);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View bindView(int arg0, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_add_friend, null);
		}
		final BmobInvitation msg = getList().get(arg0);
		final String usname = msg.getFromname();
		TextView name = ViewHolder.get(convertView, R.id.name);
		ImageView iv_avatar = ViewHolder.get(convertView, R.id.avatar);

		final Button btn_add = ViewHolder.get(convertView, R.id.btn_add);

		String avatar = msg.getAvatar();

		if (avatar != null && !avatar.equals("")) {
			ImageLoader.getInstance().displayImage(avatar, iv_avatar,
					ImageLoadOptions.getOptions());
		} else {
			iv_avatar.setImageResource(R.drawable.photo);
		}

		int status = msg.getStatus();
		if (status == BmobConfig.INVITE_ADD_NO_VALIDATION
				|| status == BmobConfig.INVITE_ADD_NO_VALI_RECEIVED) {
			// btn_add.setText("ͬ��");
			// btn_add.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.btn_login_selector));
			// btn_add.setTextColor(mContext.getResources().getColor(R.color.base_color_text_white));
			btn_add.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					BmobLog.i("���ͬ�ⰴť:" + msg.getFromid());
					agressAdd(btn_add, msg);
				}
			});
		} else if (status == BmobConfig.INVITE_ADD_AGREE) {
			btn_add.setText("��ͬ��");
			btn_add.setBackgroundDrawable(null);
			btn_add.setTextColor(mContext.getResources().getColor(
					R.color.base_color_text_black));
			btn_add.setEnabled(false);
		}
		name.setText(msg.getFromname());
		iv_avatar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				BmobQuery<User> query = new BmobQuery<User>();
				query.addWhereEqualTo("username", usname);
				query.findObjects(mContext, new FindListener<User>() {

					@Override
					public void onSuccess(List<User> arg0) {
						Intent intent = new Intent(mContext,
								SetMyInfoActivity.class);
						intent.putExtra("userdata", arg0.get(0));
						mContext.startActivity(intent);
						((Activity) mContext).overridePendingTransition(
								R.anim.in_anim, R.anim.out_anim);
					}

					@Override
					public void onError(int arg0, String arg1) {

					}
				});

			}
		});
		return convertView;
	}

	/**
	 * ��Ӻ��� agressAdd
	 * 
	 * @Title: agressAdd
	 * @Description: TODO
	 * @param @param btn_add
	 * @param @param msg
	 * @return void
	 * @throws
	 */
	private void agressAdd(final Button btn_add, final BmobInvitation msg) {
		final ProgressDialog progress = new ProgressDialog(mContext);
		progress.setMessage("�������...");
		progress.setCanceledOnTouchOutside(false);
		progress.show();
		try {
			// ͬ����Ӻ���
			BmobUserManager.getInstance(mContext).agreeAddContact(msg,
					new UpdateListener() {

						@Override
						public void onSuccess() {
							// TODO Auto-generated method stub
							progress.dismiss();
							btn_add.setText("��ͬ��");
							btn_add.setBackgroundDrawable(null);
							btn_add.setTextColor(mContext.getResources()
									.getColor(R.color.base_color_text_black));
							btn_add.setEnabled(false);
							// ���浽application�з���Ƚ�
							CustomApplcation.getInstance().setContactList(
									CollectionUtils.list2map(BmobDB.create(
											mContext).getContactList()));

						}

						@Override
						public void onFailure(int arg0, final String arg1) {
							// TODO Auto-generated method stub
							progress.dismiss();
							ShowToast("���ʧ��: " + arg1);
						}
					});
		} catch (final Exception e) {
			progress.dismiss();
			ShowToast("���ʧ��: " + e.getMessage());
		}
	}
}
