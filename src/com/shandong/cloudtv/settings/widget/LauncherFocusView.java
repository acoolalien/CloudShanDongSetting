package com.shandong.cloudtv.settings.widget;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.RelativeLayout;

import com.shandong.cloudtv.settings.R;

/**
 * 
 * @ClassName: FocusView
 * @Description: TODO
 * @author: 陈丽晓
 * @date 2014-8-27 下午9:05:49
 * 
 */
@SuppressLint("NewApi")
public class LauncherFocusView extends RelativeLayout {
	/**
	 * 焦点动画移动的时间
	 */
	private final int duration = 300;

	/**
	 * 属性动画使用的包装层，方便操作宽度和高度的变化
	 */
	private ViewWrapper viewWrapper = null;

	/**
	 * View的x和y的坐标的偏移量,考虑的是焦点图片的光圈
	 */
	public int xyOffset = 25;

	/**
	 * View的width和height的坐标的偏移量,考虑的是焦点图片的光圈
	 */
	private int wOffset = 40;

	private int hOffset = 68;

	/**
	 * 外部调用根据特殊的需要，设置y方向额外的偏移量
	 */
	private int outYOffset = 9;

	/**
	 * 外部调用根据特殊的需要，设置x方向额外的偏移量
	 */
	private int outXOffset = 0;

	/**
	 * 焦点框View的宽度变化的属性值对象
	 */
	PropertyValuesHolder pvhWidth = null;

	/**
	 * 焦点框View的高度变化的属性值对象
	 */
	PropertyValuesHolder pvhHeight = null;

	/**
	 * 焦点框View多属性动画变化的动画对象
	 */
	ObjectAnimator whAnimator = null;

	/**
	 * 高性能的View多属性动画对象
	 */
	ViewPropertyAnimator vpaXY = null;

	/**
	 * 先快后慢的动画差值器
	 */
	private AccelerateDecelerateInterpolator interpolator = null;

	private boolean isInitPositionOnScreen = false;

	/**
	 * 获取View在屏幕中坐标的数组
	 */
	private int[] location = new int[2];

	private View currentFocusView = null;

	private FocusViewAnimatorEndListener animatorEndListener = null;

	private int oldX = 0;
	private int oldY = 0;

	// hyl add
	private final String TAG = "FocusView";

	public LauncherFocusView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public LauncherFocusView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public LauncherFocusView(Context context) {
		super(context);
		init();
	}

	private void init() {
		setBackgroundResource(R.drawable.list_selected_new);
		setAlpha(0f);
		setFocusable(false);
		viewWrapper = new ViewWrapper(this);
		interpolator = new AccelerateDecelerateInterpolator();
		wOffset = (int) getResources().getDimension(R.dimen.focus_view_woffset);
		hOffset = (int) getResources().getDimension(R.dimen.focus_view_hoffset);
		xyOffset = (int) getResources().getDimension(R.dimen.focus_view_xyoffset);
		outYOffset = (int) getResources().getDimension(R.dimen.focus_view_yoffset);
		outXOffset = (int) getResources().getDimension(R.dimen.focus_view_xoffset);
	}

	/**
	 * 设置焦点图片，必须是.9格式的图片
	 * 
	 * @Title: FocusView
	 * @author:陈丽晓
	 * @Description: TODO
	 * @param resid
	 */
	public void setFocusBackgroundResource(int resid) {
		setBackgroundResource(resid);
	}

	/**
	 * 在Activity的onWindowFocusChanged方法中调用此方法
	 * 
	 * @Title: FocusView
	 * @author:陈丽晓
	 * @Description: TODO
	 * @param firstCallRequestFocusOfView
	 *            在某个Activity中指定第一个要获取焦点的View
	 */
	public void initFocusView(View firstCallRequestFocusOfView, boolean isScale, float scale) {

		if(firstCallRequestFocusOfView==null)
			return;
		firstCallRequestFocusOfView.requestFocus();

		if (isInitPositionOnScreen) {// 保证初始化一次
			return;
		}

		// 计算目标焦点View的坐标
		firstCallRequestFocusOfView.getLocationOnScreen(location);
		int targetX = location[0];
		int targetY = location[1];

		// 设置焦点View的在屏幕中的坐标
		setX(targetX - xyOffset - outXOffset);
		setY(targetY - xyOffset - outYOffset);
		// 鐠佸墽鐤嗛悞锔惧仯View閻ㄥ嫬顔旀惔锕�嫲妤傛ê瀹�
		// Log.e("huxing","----------item.width = " +
		// firstCallRequestFocusOfView.getWidth());
		// Log.e("huxing","----------item.width = " +
		// firstCallRequestFocusOfView.getHeight());
		viewWrapper.setWidth(firstCallRequestFocusOfView.getWidth() + wOffset);
		viewWrapper.setHeight(firstCallRequestFocusOfView.getHeight() + hOffset);

		if (isScale) {// 放大到scale倍数
			setScaleX(scale);
			setScaleY(scale);
		}

		setAlpha(1f);

		currentFocusView = firstCallRequestFocusOfView;

		// 初始化多属性的动画对象，用于改变焦点框View的宽和高
		pvhWidth = PropertyValuesHolder.ofInt("width", firstCallRequestFocusOfView.getWidth());
		pvhHeight = PropertyValuesHolder.ofInt("height", firstCallRequestFocusOfView.getHeight());
		whAnimator = ObjectAnimator.ofPropertyValuesHolder(viewWrapper, pvhWidth, pvhHeight);
		// 设置动画时间和速度变化
		whAnimator.setDuration(duration).setInterpolator(interpolator);

		vpaXY = animate().x(targetX - xyOffset - outXOffset).y(targetY - xyOffset - outYOffset)
				.setDuration(duration).setListener(new FocusViewMoveListener())
				.setInterpolator(interpolator);

		// 初始化完成
		isInitPositionOnScreen = true;
	}
	
	
	/**
	 * 在Activity的onWindowFocusChanged方法中调用此方法
	 * 
	 * @Title: FocusView
	 * @author:陈丽晓/易启邦
	 * @Description: 蓝牙页面专用，其他页面不要乱用 BY yiqibang
	 * @param firstCallRequestFocusOfView
	 *            在某个Activity中指定第一个要获取焦点的View
	 */
	public void initFocusViewNew(View firstCallRequestFocusOfView, boolean isScale, float scale) {

		if(firstCallRequestFocusOfView==null)
			return;
		firstCallRequestFocusOfView.requestFocus();

		if (isInitPositionOnScreen) {// 保证初始化一次
			//return;
		}

		// 计算目标焦点View的坐标
		firstCallRequestFocusOfView.getLocationOnScreen(location);
		int targetX = location[0];
		int targetY = location[1];

		// 设置焦点View的在屏幕中的坐标
		setX(targetX - xyOffset - outXOffset);
		setY(targetY - xyOffset - outYOffset);
		viewWrapper.setWidth(firstCallRequestFocusOfView.getWidth() + wOffset);
		viewWrapper.setHeight(firstCallRequestFocusOfView.getHeight() + hOffset);

		if (isScale) {// 放大到scale倍数
			setScaleX(scale);
			setScaleY(scale);
		}

		setAlpha(1f);

		currentFocusView = firstCallRequestFocusOfView;

		// 初始化多属性的动画对象，用于改变焦点框View的宽和高
		pvhWidth = PropertyValuesHolder.ofInt("width", firstCallRequestFocusOfView.getWidth());
		pvhHeight = PropertyValuesHolder.ofInt("height", firstCallRequestFocusOfView.getHeight());
		whAnimator = ObjectAnimator.ofPropertyValuesHolder(viewWrapper, pvhWidth, pvhHeight);
		// 设置动画时间和速度变化
		whAnimator.setDuration(duration).setInterpolator(interpolator);

		vpaXY = animate().x(targetX - xyOffset - outXOffset).y(targetY - xyOffset - outYOffset)
				.setDuration(duration).setListener(new FocusViewMoveListener())
				.setInterpolator(interpolator);

		// 初始化完成
		isInitPositionOnScreen = true;
	}

	/**
	 * 焦点框View动画移动到当前获得焦点的View，调用此方法焦点框View没有放大效果
	 * 
	 * @Title: FocusView
	 * @author:陈丽晓
	 * @Description: TODO
	 * @param tagetView
	 *            目前获得焦点的View
	 */
	public void moveTo(View tagetView) {

		if (!isInitPositionOnScreen) {// 防止Activity中某个View在界面刚刚构建完成时抢焦点，造成的动画效果
			return;
		}

		// 鐠侊紕鐣婚惄顔界垼閻掞拷閻愮瓡iew閻ㄥ嫬顔旈崪宀勭彯
		int targetWidth = tagetView.getWidth() + wOffset;
		int targetHeight = tagetView.getHeight() + hOffset;

		// Log.e("huxing", "huxing------targetWidth = " + targetWidth);
		// Log.e("huxing", "huxing------targetHeight = " + targetHeight);

		tagetView.getLocationOnScreen(location);
		int targetX = location[0];
		int targetY = location[1];

		// 计算焦点框View在屏幕中的坐标
		int x = targetX - xyOffset - outXOffset;
		int y = targetY - xyOffset - outYOffset;

		pvhWidth.setIntValues(viewWrapper.getWidth(), targetWidth);
		pvhHeight.setIntValues(viewWrapper.getHeight(), targetHeight);

		whAnimator.start();

		vpaXY.x(x).y(y);

		currentFocusView = tagetView;

		oldX = targetX;
		oldY = targetY;
	}
	
	//only user pppoe connect button
	public void moveToLayout(View tagetView) {

		if (!isInitPositionOnScreen) {// 防止Activity中某个View在界面刚刚构建完成时抢焦点，造成的动画效果
			return;
		}

		 
		int targetWidth = tagetView.getWidth() + wOffset-10;
		int targetHeight = tagetView.getHeight() + hOffset-2;

		tagetView.getLocationOnScreen(location);
		int targetX = location[0];
		int targetY = location[1];

		// 计算焦点框View在屏幕中的坐标
		int x = targetX - xyOffset - outXOffset + 12;
		int y = targetY - xyOffset - outYOffset-1;

		pvhWidth.setIntValues(viewWrapper.getWidth(), targetWidth);
		pvhHeight.setIntValues(viewWrapper.getHeight(), targetHeight);

		whAnimator.start();

		vpaXY.x(x).y(y);

		currentFocusView = tagetView;

		oldX = targetX;
		oldY = targetY;
	}
	
	
	/**
	 *  仅仅针对WIFI界面UI，偶现焦点错乱的问题  
	 */
	public void moveToWifiTop(View tagetView) {

		if (!isInitPositionOnScreen) {
			return;
		}
		//
		Log.e(TAG, "moveToWifiTop");
		int targetWidth = 1545;
		int targetHeight = 160;
 
		tagetView.getLocationOnScreen(location);
		int targetX = location[0];
		int targetY = location[1];

		// 计算焦点框View在屏幕中的坐标
		int x = targetX - xyOffset - outXOffset;
		int y = targetY - xyOffset - outYOffset;
		
		Log.e(TAG, "x = " + x+"  y=="+y);
		x = 188;
		y = 408; 

		Log.e(TAG, "x = " + x+"  y=="+y);
		pvhWidth.setIntValues(viewWrapper.getWidth(), targetWidth);
		pvhHeight.setIntValues(viewWrapper.getHeight(), targetHeight);

		whAnimator.start();

		vpaXY.x(x).y(y);

		currentFocusView = tagetView;

		oldX = targetX;
		oldY = targetY;
	}

	/**
	 * 返回动画的终点位置的x坐标
	 * 
	 * @Title: FocusView
	 * @author:陈丽晓
	 * @Description: TODO
	 * @return
	 */
	public int getOldX() {
		return oldX;
	}

	/**
	 * 返回动画的终点位置的y坐标
	 * 
	 * @Title: FocusView
	 * @author:陈丽晓
	 * @Description: TODO
	 * @return
	 */
	public int getOldY() {
		return oldY;
	}

	/**
	 * 返回View的x和y的坐标的偏移量,考虑的是焦点图片的光圈
	 * 
	 * @Title: FocusView
	 * @author:陈丽晓
	 * @Description: TODO
	 * @return
	 */
	public int getXYOffset() {
		return xyOffset;
	}

	/**
	 * 瞬间移动焦点框View，并以渐变的方式显示出来
	 * 
	 * @Title: FocusView
	 * @author:陈丽晓
	 * @Description: TODO
	 * @param target
	 */
	public void moveToByAlpha(View target) {

		setAlpha(0f);
		// 计算目标焦点View的坐标
		target.getLocationOnScreen(location);
		int targetX = location[0];
		int targetY = location[1];

		// 设置焦点View的在屏幕中的坐标
		setX(targetX - xyOffset - outXOffset);
		setY(targetY - xyOffset - outYOffset);
		// 设置焦点View的宽度和高度
		viewWrapper.setWidth(target.getWidth() + wOffset);
		viewWrapper.setHeight(target.getHeight() + hOffset);
		setAlpha(1f);

		oldX = targetX;
		oldY = targetY;

	}

	/**
	 * 焦点框移动动画监听器
	 * 
	 * @ClassName: FocusViewMoveListener
	 * @Description: TODO
	 * @author:陈丽晓
	 * @date 2014-9-10 下午9:04:02
	 * 
	 */
	class FocusViewMoveListener implements AnimatorListener {

		@Override
		public void onAnimationStart(Animator animation) {
			if (null != animatorEndListener) {
				animatorEndListener.OnAnimateStart(currentFocusView);
			}
		}

		@Override
		public void onAnimationEnd(Animator animation) {
			if (null != animatorEndListener) {
				animatorEndListener.OnAnimateEnd(currentFocusView);
			}
		}

		@Override
		public void onAnimationCancel(Animator animation) {
		}

		@Override
		public void onAnimationRepeat(Animator animation) {
		}

	}

	public int getOutYOffset() {
		return outYOffset;
	}

	public void setOutYOffset(int outYOffset) {
		this.outYOffset = outYOffset;
	}

	public int getOutXOffset() {
		return outXOffset;
	}

	public void setOutXOffset(int outXOffset) {
		this.outXOffset = outXOffset;
	}

	public void animateWidthAndHeight(View target) {
		// 设置变化过的宽度和高度
		pvhWidth.setIntValues(viewWrapper.getWidth(), target.getWidth());
		pvhHeight.setIntValues(viewWrapper.getHeight(), target.getHeight());
		// 启动焦点View坐标和高度宽度变化
		whAnimator.start();
	}

	public void moveToByParams(int x, int y, int width, int height) {
		// 坐标变化
		if (x > 0 && y > 0) {
			animate().x(x).y(y);
		} else if (x > 0) {
			animate().x(x);
		} else if (y > 0) {
			animate().y(y);
		}

		animate().setListener(new AnimatorListenerAdapter() {

		});

		Log.d("DesktopView", "moveToByParams " + x);
		oldX = x;

		// 宽度和高度变化
		if ((width + xyOffset) != getWidth()) {
			pvhWidth.setIntValues(viewWrapper.getWidth(), width + wOffset);
		}

		if ((height + xyOffset) != getWidth()) {
			pvhHeight.setIntValues(viewWrapper.getHeight(), height + hOffset);
		}
		whAnimator.start();
	}

	/**
	 * 焦点框的View移动结束后回到此接口
	 * 
	 * @ClassName: FocusViewAnimatorEndListener
	 * @Description: TODO
	 * @author: 陈丽晓
	 * @date 2014-9-11 上午12:23:28
	 * 
	 */
	public interface FocusViewAnimatorEndListener {
		public void OnAnimateStart(View currentFocusView);

		public void OnAnimateEnd(View currentFocusView);
	}

	public void setAnimatorEndListener(FocusViewAnimatorEndListener animatorEndListener) {
		this.animatorEndListener = animatorEndListener;
	}
}
