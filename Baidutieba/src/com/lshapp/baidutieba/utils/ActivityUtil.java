package com.lshapp.baidutieba.utils;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.lshapp.baidutieba.CustomApplcation;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * @author adison Activity��������
 */
public final class ActivityUtil {

    /**
     * ��ȡ��Ļ���
     * @param activity
     * @return
     */
    public static int[] getScreenSize() {
        int[] screens;
        // if (Constants.screenWidth > 0) {
        // return screens;
        // }
        DisplayMetrics dm=new DisplayMetrics();
        dm=CustomApplcation.getInstance().getResources().getDisplayMetrics();
        screens=new int[]{dm.widthPixels, dm.heightPixels};
        return screens;
    }

    public static float[] getBitmapConfiguration(Bitmap bitmap, ImageView imageView, float screenRadio) {
        int screenWidth=ActivityUtil.getScreenSize()[0];
        float rawWidth=0;
        float rawHeight=0;
        float width=0;
        float height=0;
        if(bitmap == null) {
            // rawWidth = sourceWidth;
            // rawHeight = sourceHeigth;
            width=(float)(screenWidth / screenRadio);
            height=(float)width;
            imageView.setScaleType(ScaleType.FIT_XY);
        } else {
            rawWidth=bitmap.getWidth();
            rawHeight=bitmap.getHeight();
            if(rawHeight > 10 * rawWidth) {
                imageView.setScaleType(ScaleType.CENTER);
            } else {
                imageView.setScaleType(ScaleType.FIT_XY);
            }
            float radio=rawHeight / rawWidth;

            width=(float)(screenWidth / screenRadio);
            height=(float)(radio * width);
        }
        return new float[]{width, height};
    }

    /**
     * ��ȡӦ�ð汾��
     * @param context
     * @return
     */
    public static String getVersionName(Context context) {
        PackageManager pm=context.getPackageManager();
        try {
            PackageInfo info=pm.getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch(NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;

    }

    //
    // /**
    // * @author adison ����ͼƬ����
    // * @param bitmap
    // * @param left
    // * @param top
    // * @param right
    // * @param bottom
    // * @param sourceWidth
    // * @param sourceHeigth
    // * @param screenRadio
    // * @return
    // */
    // public static LayoutParams setImageViewParams(Bitmap bitmap, ImageView imageView, int left, int top, int right, int bottom,
    // float sourceWidth, float sourceHeigth, float screenRadio, boolean fitSource) {
    // float[] cons=getBitmapConfiguration(bitmap, imageView, sourceWidth, sourceHeigth, screenRadio, fitSource);
    // LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams((int)cons[0], (int)cons[1]);
    // layoutParams.gravity=Gravity.CENTER;
    // layoutParams.setMargins(left, top, right, bottom);
    // return layoutParams;
    // }

    /**
     * ͨ���ⲿ�������ҳ��
     * @param context
     * @param url
     */
    public static void openBrowser(Context context, String urlText) {
        Intent intent=new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri url=Uri.parse(urlText);
        intent.setData(url);
        context.startActivity(intent);
    }

    /**
     * �л�ȫ��״̬��
     * @param activity Activity
     * @param isFull ����Ϊtrue��ȫ���������ȫ��
     */
    public static void toggleFullScreen(Activity activity, boolean isFull) {
        hideTitleBar(activity);
        Window window=activity.getWindow();
        WindowManager.LayoutParams params=window.getAttributes();
        if(isFull) {
            params.flags|=WindowManager.LayoutParams.FLAG_FULLSCREEN;
            window.setAttributes(params);
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            params.flags&=(~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            window.setAttributes(params);
            window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    /**
     * ����Ϊȫ��
     * @param activity Activity
     */
    public static void setFullScreen(Activity activity) {
        toggleFullScreen(activity, true);
    }

    /**
     * ��ȡϵͳ״̬���߶�
     * @param activity Activity
     * @return ״̬���߶�
     */
    public static int getStatusBarHeight(Activity activity) {
        try {
            Class<?> clazz=Class.forName("com.android.internal.R$dimen");
            Object object=clazz.newInstance();
            Field field=clazz.getField("status_bar_height");
            int dpHeight=Integer.parseInt(field.get(object).toString());
            return activity.getResources().getDimensionPixelSize(dpHeight);
        } catch(Exception e1) {
            e1.printStackTrace();
            return 0;
        }
    }

    /**
     * ����Activity��ϵͳĬ�ϱ�����
     * @param activity Activity
     */
    public static void hideTitleBar(Activity activity) {
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    /**
     * ǿ������Actiity����ʾ����Ϊ��ֱ����
     * @param activity Activity
     */
    public static void setScreenVertical(Activity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /**
     * ǿ������Activity����ʾ����Ϊ����
     * @param activity Activity
     */
    public static void setScreenHorizontal(Activity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    /**
     * ����������뷨
     * @param activity Activity
     */
    public static void hideSoftInput(Activity activity) {
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    /**
     * �ر��Ѿ���ʾ�����뷨���ڡ�
     * @param context �����Ķ���һ��ΪActivity
     * @param focusingView ���뷨���ڽ����View
     */
    public static void closeSoftInput(Context context, View focusingView) {
        InputMethodManager imm=(InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(focusingView.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

    /**
     * ʹUI�������뷨
     * @param activity Activity
     */
    public static void adjustSoftInput(Activity activity) {
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    public interface MessageFilter {

        String filter(String msg);
    }

    public static MessageFilter msgFilter;

    /**
     * ��ʱ����ʾToast��Ϣ������֤������UI�߳���
     * @param activity Activity
     * @param message ��Ϣ����
     */
//    public static void show(final Activity activity, final String message) {
//        final String msg=msgFilter != null ? msgFilter.filter(message) : message;
//        activity.runOnUiThread(new Runnable() {
//
//            public void run() {
//                // Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
//                Toast toast=ToastFactory.getToast(activity, message);
//                toast.setGravity(Gravity.CENTER, 0, 0);
//                toast.show();
//            }
//        });
//    }
    /**
     * ��ʱ����ʾToast��Ϣ������֤������UI�߳���
     * @param activity Activity
     * @param message ��Ϣ����
     */
//    public static void show(final Context context, final String message) {
//        final String msg=msgFilter != null ? msgFilter.filter(message) : message;
//        MyApplication.getInstance().getTopActivity().runOnUiThread(new Runnable() {
//
//            public void run() {
//                // Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
//                Toast toast=ToastFactory.getToast(MyApplication.getInstance().getTopActivity(), message);
//                toast.setGravity(Gravity.BOTTOM, 0, 0);
//                toast.show();
//            }
//        });
//    }
    /**
     * ��ʱ����ʾToast��Ϣ������֤������UI�߳���
     * @param activity Activity
     * @param message ��Ϣ����
     */
//    public static void showL(final Activity activity, final String message) {
//        final String msg=msgFilter != null ? msgFilter.filter(message) : message;
//        activity.runOnUiThread(new Runnable() {
//
//            public void run() {
//                // Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();
//                Toast toast=ToastFactory.getToast(activity, message);
//                toast.setGravity(Gravity.CENTER, 0, 0);
//                toast.show();
//            }
//        });
//    }

    /**
     * �����ֻ��ķֱ��ʴ� dp �ĵ�λ ת��Ϊ px(����)
     * @param context �����ģ�һ��ΪActivity
     * @param dpValue dp����ֵ
     * @return px����ֵ
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale=context.getResources().getDisplayMetrics().density;
        return (int)(dpValue * scale + 0.5f);
    }

    /**
     * �����ֻ��ķֱ��ʴ� px(����) �ĵ�λ ת��Ϊ dp
     * @param context �����ģ�һ��ΪActivity
     * @param pxValue px����ֵ
     * @return dp����ֵ
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale=context.getResources().getDisplayMetrics().density;
        return (int)(pxValue / scale + 0.5f);
    }

    /**
     * activity�л�����
     * @param m
     * @param isEnd
     */
//    @SuppressLint("NewApi")
//    public static void runActivityAnim(Activity m, boolean isEnd) {
//        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.DONUT) {
//            if(isEnd) {
//                m.overridePendingTransition(R.anim.activity_close_enter, R.anim.activity_close_exit);
//            } else {
//                m.overridePendingTransition(R.anim.activity_open_enter, R.anim.activity_open_exit);
//            }
//        }
//    }

//    /**
//     * ��ݷ�ʽ�Ƿ����
//     * @return
//     */
//    public static boolean ifAddShortCut(Context context) {
//        boolean isInstallShortCut=false;
//        ContentResolver cr=context.getContentResolver();
//        String authority="com.android.launcher2.settings";
//        Uri uri=Uri.parse("content://" + authority + "/favorites?notify=true");
//        Cursor c=
//            cr.query(uri, new String[]{"title", "iconResource"}, "title=?", new String[]{context.getString(R.string.app_name)},
//                null);
//        if(null != c && c.getCount() > 0) {
//            isInstallShortCut=true;
//        }
//        return isInstallShortCut;
//    }

//    /**
//     * ������ݷ�ʽ
//     */
//    public static void addShortCut(Context context) {
//        Intent shortcut=new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
//        // ��������
//        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, context.getString(R.string.app_name));
//        ShortcutIconResource resource=Intent.ShortcutIconResource.fromContext(context, R.drawable.ic_launcher);
//        shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, resource);
//        // �Ƿ������ظ�����
//        shortcut.putExtra("duplicate", false);
//        Intent intent=new Intent(Intent.ACTION_MAIN);// ��ʶActivityΪһ������Ŀ�ʼ
//        intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
//        intent.addFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
//        intent.addCategory(Intent.CATEGORY_LAUNCHER);
//        intent.setClass(context, SplashActivity.class);
//        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
//        context.sendBroadcast(shortcut);
//    }

    /**
     * �õ�view�߶�
     * @param w
     * @param bmw
     * @param bmh
     * @return
     */
    public static int getViewHeight(int w, int bmw, int bmh) {
        return w * bmh / bmw;
    }

    /**
     * ��ȡ��Ļ���
     * @param activity
     * @return
     */
    public static int[] getScreenSize(Activity activity) {
        int[] screens;
        // if (Constants.screenWidth > 0) {
        // return screens;
        // }
        DisplayMetrics dm=new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        screens=new int[]{dm.widthPixels, dm.heightPixels};
        return screens;
    }

    // MD5��Q
    public static String Md5(String str) {
        if(str != null && !str.equals("")) {
            try {
                MessageDigest md5=MessageDigest.getInstance("MD5");
                char[] HEX={'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
                byte[] md5Byte=md5.digest(str.getBytes("UTF8"));
                StringBuffer sb=new StringBuffer();
                for(int i=0; i < md5Byte.length; i++) {
                    sb.append(HEX[(int)(md5Byte[i] & 0xff) / 16]);
                    sb.append(HEX[(int)(md5Byte[i] & 0xff) % 16]);
                }
                str=sb.toString();
            } catch(NoSuchAlgorithmException e) {

            } catch(Exception e) {
            }
        }
        return str;
    }

    /**
     * �õ��ֻ�IMEI
     * @param context
     * @return
     */
    public static String getImei(Context context) {
        TelephonyManager tm=(TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        LogUtils.i("IMEI", tm.getDeviceId());
        return tm.getDeviceId();
    }

    /**
     * �ж����������Ƿ����
     * @param context
     * @return
     */
    public static boolean hasNetwork(Context context) {
        // ��ȡ�ֻ��������ӹ�����󣨰���wifi��net�����ӵĹ���
        ConnectivityManager connectivity=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivity != null) {
            // ��ȡ�������ӹ���Ķ���
            NetworkInfo info=connectivity.getActiveNetworkInfo();
            if(info != null && info.isConnected()) {
                // �жϵ�ǰ�����Ƿ��Ѿ�����
                if(info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;

    }

    /**
     * ��װһ��APK�ļ�
     * @param file
     */
    public static void installApk(Context context, File file) {
        Intent intent=new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /**
     * ��ָ��byte����ת����16���ƴ�д�ַ���
     * @param b
     * @return
     */
    public static String byteToHexString(byte[] b) {
        StringBuffer hexString=new StringBuffer();
        for(int i=0; i < b.length; i++) {
            String hex=Integer.toHexString(b[i] & 0xFF);
            if(hex.length() == 1) {
                hex='0' + hex;
            }
            hexString.append(hex.toUpperCase());
        }
        return hexString.toString();
    }

//    /**
//     * չʾtoast
//     * @param context
//     * @param str
//     */
//    public static void showToast(Context context, String str) {
//        // Toast toast = Toast.makeText(context, str, Toast.LENGTH_SHORT);
//
//        Toast toast=ToastFactory.getToast(context, str);
//        toast.setGravity(Gravity.CENTER, 0, 0);
//        toast.show();
//    }

    /**
     * ���������е�����ȫ����ȡ����, һ���Է���
     * @param is
     * @return
     * @throws IOException
     */
    public static byte[] load(InputStream is) throws IOException {
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        byte[] buffer=new byte[1024];
        int len;
        while((len=is.read(buffer)) != -1)
            baos.write(buffer, 0, len);
        baos.close();
        is.close();
        return baos.toByteArray();
    }

    /**
     * �����ļ��õ��ֽ���
     * @param file
     * @return
     */
    public static byte[] getFileByte(File file) {
        if(!file.exists()) {
            return null;
        }
        try {
            FileInputStream fis=new FileInputStream(file);
            int len=fis.available();
            byte[] bytes=new byte[len];
            fis.read(bytes);
            fis.close();
            return bytes;
        } catch(Exception e) {

        }

        return null;
    }

    /**
     * ����ͼƬ��С�õ����ʵ�������
     * @param value
     * @return
     */
    public static int getSimpleNumber(int value) {
        if(value > 30) {
            return 1 + getSimpleNumber(value / 4);
        } else {
            return 1;
        }
    }

    /**
     * �����飨String����ȡ��ͬԪ�� ����˼·��:1.���Ƚ���������A��B����(����)<br>
     * 2.�ֱ��A��B�и�ȡ��һԪ��a,b����a��b���б� �ϣ�<br>
     * 1) ���a��b��ȣ���a��b����һָ��������<br>
     * 2)���aС��b�������ȡA����һԪ�أ�����b�� ��<br>
     * 3) ���a����b����ȡB����һ��Ԫ�أ���a���бȽ�<br>
     * 3.�������в���2��֪��A��B��Ԫ�ض��Ƚ���<br>
     * 4.���ؼ���(������ͬ��Ԫ��)<br>
     * @param strArr1
     * @param strArr2
     * @return
     */
    public static List<String> getAllSameElement2(String[] strArr1, String[] strArr2) {
        if(strArr1 == null || strArr2 == null) {
            return null;
        }
        Arrays.sort(strArr1);
        Arrays.sort(strArr2);
        List<String> list=new ArrayList<String>();
        int k=0;
        int j=0;
        while(k < strArr1.length && j < strArr2.length) {
            if(strArr1[k].compareTo(strArr2[j]) == 0) {
                if(strArr1[k].equals(strArr2[j])) {
                    list.add(strArr1[k]);
                    k++;
                    j++;
                }
                continue;
            } else if(strArr1[k].compareTo(strArr2[j]) < 0) {
                k++;
            } else {
                j++;
            }
        }
        return list;
    }

    /**
     * ��ȡ��ǰ���ꡢ�¡��� ��Ӧ��ʱ��
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static long getTime() {
        Date d=new Date();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        String dateNowStr=sdf.format(d);
        // System.out.println("��ʽ��������ڣ�" + dateNowStr);
        Date d2=null;
        try {
            d2=sdf.parse(dateNowStr);
            // System.out.println(d2);
            // System.out.println(d2.getTime());
            return d2.getTime();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    // I added a generic return type to reduce the casting noise in client code
    @SuppressWarnings("unchecked")
    public static <T extends View> T get(View view, int id) {
        SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
        if (viewHolder == null) {
            viewHolder = new SparseArray<View>();
            view.setTag(viewHolder);
        }
        View childView = viewHolder.get(id);
        if (childView == null) {
            childView = view.findViewById(id);
            viewHolder.put(id, childView);
        }
        return (T) childView;
    }

}
