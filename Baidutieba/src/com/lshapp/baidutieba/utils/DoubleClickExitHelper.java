package com.lshapp.baidutieba.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.widget.Toast;

public class DoubleClickExitHelper {
	private final Activity mActivity;
	private boolean isOnKeyBacking;
	private Handler mHandler;
	private Toast mBackToast;
	
	public DoubleClickExitHelper(Activity activity) {
		mActivity = activity;
		mHandler = new Handler(Looper.getMainLooper());
	}
	
	/**
	 * Activity onKeyDown�¼�
	 * */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode != KeyEvent.KEYCODE_BACK) {
			return false;
		}
		if(isOnKeyBacking) {
			mHandler.removeCallbacks(onBackTimeRunnable);
			if(mBackToast != null){
				mBackToast.cancel();
			}
			isOnKeyBacking = false;
			Intent setIntent = new Intent(Intent.ACTION_MAIN);
	        setIntent.addCategory(Intent.CATEGORY_HOME);
	        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        mActivity.startActivity(setIntent);
			return true;
		} else {
			isOnKeyBacking = true;
			if(mBackToast == null) {
				mBackToast = Toast.makeText(mActivity, "�ٰ�һ�η���", 2000);
			}
			mBackToast.show();
			//�ӳ������ʱ�䣬��Runable����ȥ
			mHandler.postDelayed(onBackTimeRunnable, 2000);
			return true;
		}
	}
	
	private Runnable onBackTimeRunnable = new Runnable() {
		
		@Override
		public void run() {
			isOnKeyBacking = false;
			if(mBackToast != null){
				mBackToast.cancel();
			}
		}
	};
}
