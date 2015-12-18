package com.lshapp.baidutieba.utils;


import com.lshapp.baidutieba.BuildConfig;

import android.util.Log;


/**
 * @author kingofglory
 *         email: kingofglory@yeah.net
 *         blog:  http:www.google.com
 * @date 2014-2-21
 * TODO Log�����࣬���ÿ��أ���ֹ�����汾ʱlog��Ϣй¶
 */

public class LogUtils {

		public static void v(String tag, String msg) {
			if (BuildConfig.DEBUG) {
				Log.v(tag, msg);
			}

		}

		public static void d(String tag, String msg) {
			if (BuildConfig.DEBUG) {
				Log.d(tag, msg);
			}

		}

		public static void i(String tag, String msg) {
			if (BuildConfig.DEBUG) {
				Log.i(tag, msg);
			}

		}

		public static void w(String tag, String msg) {
			if (BuildConfig.DEBUG) {
				Log.w(tag, msg);
			}

		}

		public static void e(String tag, String msg) {
			if (BuildConfig.DEBUG) {
				Log.e(tag, msg);
			}
		}

}
