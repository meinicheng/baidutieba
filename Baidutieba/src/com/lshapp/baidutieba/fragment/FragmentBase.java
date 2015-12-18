package com.lshapp.baidutieba.fragment;


import com.lshapp.baidutieba.CustomApplcation;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.util.BmobLog;

/** Fragmenet ����
  * @ClassName: FragmentBase
  * @Description: TODO
  * @author smile
  * @date 2014-5-22 ����2:43:50
  */
public abstract class FragmentBase extends Fragment {
	
	public BmobUserManager userManager;
	public BmobChatManager manager;
	
	/**
	 * ���õ�Header����
	 */

	protected View contentView;
	
	public LayoutInflater mInflater;
	
	private Handler handler = new Handler();
	
	public void runOnWorkThread(Runnable action) {
		new Thread(action).start();
	}

	public void runOnUiThread(Runnable action) {
		handler.post(action);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		mApplication = CustomApplcation.getInstance();
		userManager = BmobUserManager.getInstance(getActivity());
		manager = BmobChatManager.getInstance(getActivity());
		mInflater = LayoutInflater.from(getActivity());
	}

	
	public FragmentBase() {
		
	}

	Toast mToast;

	public void ShowToast(String text) {
		if (mToast == null) {
			mToast = Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT);
		} else {
			mToast.setText(text);
		}
		mToast.show();
	}

	public void ShowToast(int text) {
		if (mToast == null) {
			mToast = Toast.makeText(getActivity(), text, Toast.LENGTH_LONG);
		} else {
			mToast.setText(text);
		}
		mToast.show();
	}

	
	/** ��Log
	  * ShowLog
	  * @return void
	  * @throws
	  */
	public void ShowLog(String msg){
		BmobLog.i(msg);
	}
	
	public View findViewById(int paramInt) {
		return getView().findViewById(paramInt);
	}

	public CustomApplcation mApplication;



	
	/**
	 * ��������ҳ�� startAnimActivity
	 * @throws
	 */
	public void startAnimActivity(Intent intent) {
		this.startActivity(intent);
	}
	
	public void startAnimActivity(Class<?> cla) {
		getActivity().startActivity(new Intent(getActivity(), cla));
	}
	
}
