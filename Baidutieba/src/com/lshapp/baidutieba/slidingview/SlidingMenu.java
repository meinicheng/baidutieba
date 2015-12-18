package com.lshapp.baidutieba.slidingview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Scroller;

import com.lshapp.baidutieba.R;
import com.nineoldandroids.view.ViewHelper;

public class SlidingMenu extends HorizontalScrollView {
	private LinearLayout mWapper;
	private ViewGroup mMenu;
	private ViewGroup mContent;
	private int mScreenWidth;

	private int mMenuWidth;
	// dp
	private int mMenuRightPadding = 50;

	private boolean once;

	private boolean isOpen;
	private ImageView bt;

	private int mLastXIntercept = 0;
	private int mLastYIntercept = 0;

	private int mLastX = 0;
	private int mLastY = 0;


	/**
	 * δʹ���Զ�������ʱ������
	 * 
	 * @param context
	 * @param attrs
	 */
	public SlidingMenu(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	/**
	 * ��ʹ�����Զ�������ʱ������ô˹��췽��
	 * 
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public SlidingMenu(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		// ��ȡ���Ƕ��������
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.SlidingMenu, defStyle, 0);

		int n = a.getIndexCount();
		for (int i = 0; i < n; i++) {
			int attr = a.getIndex(i);
			switch (attr) {
			case R.styleable.SlidingMenu_rightPadding:
				//dpת����
				mMenuRightPadding = a.getDimensionPixelSize(attr,
						(int) TypedValue.applyDimension(
								TypedValue.COMPLEX_UNIT_DIP, 50, context
										.getResources().getDisplayMetrics()));
				break;
			}
		}
		a.recycle();

		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		mScreenWidth = outMetrics.widthPixels;

	}

	public SlidingMenu(Context context) {
		this(context, null);
	}

	/**
	 * ������View�Ŀ�͸� �����Լ��Ŀ�͸�
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (!once) {
			mWapper = (LinearLayout) getChildAt(0);
			mMenu = (ViewGroup) mWapper.getChildAt(0);
			mContent = (ViewGroup) mWapper.getChildAt(1);

			bt = (ImageView) mContent.findViewById(R.id.img_ch);

			mMenuWidth = mMenu.getLayoutParams().width = mScreenWidth
					- mMenuRightPadding;
			mContent.getLayoutParams().width = mScreenWidth;
			once = true;
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	/**
	 * ͨ������ƫ��������menu����
	 */
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (changed) {
			this.scrollTo(mMenuWidth, 0);
		}
	}

//	@Override
//	public boolean onInterceptTouchEvent(MotionEvent event) {
//		boolean intercepted = false;
//		int x = (int) event.getX();
//		int y = (int) event.getY();
//
//		switch (event.getAction()) {
//		case MotionEvent.ACTION_DOWN:
//			intercepted = false;
////			if (!mScroller.isFinished()) {
////				mScroller.abortAnimation();
////				intercepted = true;
////			}
//			break;
//
//		case MotionEvent.ACTION_MOVE:
//			int deltaX = x - mLastXIntercept;
//			int deltaY = y - mLastYIntercept;
//			if (Math.abs(deltaX) > Math.abs(deltaY)) {
//				intercepted = true;
//
//			} else {
//				intercepted = false;
//			}
//			break;
//
//		case MotionEvent.ACTION_UP:
//			intercepted = false;
//
//			break;
//
//		}
//
//		mLastX = x;
//		mLastY = y;
//		mLastXIntercept = x;
//		mLastYIntercept = y;
//
//		return intercepted;
//	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		int action = ev.getAction();

		switch (action) {
		case MotionEvent.ACTION_UP:
			// ��������ߵĿ��
			//getScrollX()Ϊ̧����ʹ�ƶ��ľ��룬���ұ߻�Ϊ+
			int scrollX = getScrollX();
			if (scrollX >= mMenuWidth / 2) {
				//�ƶ�����this.smoothScrollTo()
				this.smoothScrollTo(mMenuWidth, 0);
				isOpen = false;
			} else {
				this.smoothScrollTo(0, 0);
				isOpen = true;
			}

			return true;
		}
		return super.onTouchEvent(ev);
	}

	/**
	 * �򿪲˵�
	 */
	public void openMenu() {
		if (isOpen)
			return;
		this.smoothScrollTo(0, 0);

		isOpen = true;
	}

	public void closeMenu() {
		if (!isOpen)
			return;
		this.smoothScrollTo(mMenuWidth, 0);
		isOpen = false;
	}

	/**
	 * �л��˵�
	 */
	public void toggle() {
		if (isOpen) {
			closeMenu();
		} else {
			openMenu();
		}
	}

	/**
	 * ��������ʱ
	 */
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		float scale = l * 1.0f / mMenuWidth; // 1 ~ 0

		/**
		 * ����1����������1.0~0.7 ���ŵ�Ч�� scale : 1.0~0.0 0.7 + 0.3 * scale
		 * 
		 * ����2���˵���ƫ������Ҫ�޸�
		 * 
		 * ����3���˵�����ʾʱ�������Լ�͸���ȱ仯 ���ţ�0.7 ~1.0 1.0 - scale * 0.3 ͸���� 0.6 ~ 1.0 0.6+
		 * 0.4 * (1- scale) ;
		 * 
		 */
		float rightScale = 0.8f + 0.2f * scale;
		float leftScale = 1.0f - scale * 0.5f;
		float leftAlpha = 0.3f + 0.7f * (1 - scale);

		float lAlpha = 0.0f + 1.0f * (scale);
		// �������Զ���������TranslationX
		ViewHelper.setTranslationX(mMenu, mMenuWidth * scale * 0.5f);

		ViewHelper.setScaleX(mMenu, leftScale);
		ViewHelper.setScaleY(mMenu, leftScale);
		ViewHelper.setAlpha(mMenu, leftAlpha);
		ViewHelper.setAlpha(bt, lAlpha);
		// ����content�����ŵ����ĵ�
		ViewHelper.setPivotX(mContent, 0);
		ViewHelper.setPivotY(mContent, mContent.getHeight() / 2);
		ViewHelper.setScaleX(mContent, rightScale);
		ViewHelper.setScaleY(mContent, rightScale);

	}

}
