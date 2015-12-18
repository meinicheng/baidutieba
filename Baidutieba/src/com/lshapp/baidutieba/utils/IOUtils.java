package com.lshapp.baidutieba.utils;

import java.io.Closeable;
import java.io.IOException;

import android.util.Log;

//未使用的方法
public class IOUtils {
	public static boolean close(Closeable io) {
		if (io != null) {
			try {
				io.close();
			} catch (IOException e) {
				Log.d("MainActivity", e+"");
			}
		}
		return true;
	}
}
