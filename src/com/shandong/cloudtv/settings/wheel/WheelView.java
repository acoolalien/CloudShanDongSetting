/**
 * @Title WheelView.java
 * @Package com.hiveview.cloudscreen.videolive.view.wheelview
 * @author haozening
 * @date 2014年10月1日 上午11:35:57
 * @Description 
 * @version V1.0
 */
package com.shandong.cloudtv.settings.wheel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnLayoutChangeListener;
import android.widget.AdapterView;
import android.widget.Scroller;

/**
 * 在适配器getView中，必须给View设置LayoutParams
 * 
 * @ClassName WheelView
 * @Description
 * @author haozening
 * @date 2014年10月1日 上午11:35:57
 * 
 */
@SuppressLint("NewApi")
public class WheelView extends AdapterView<BaseWheelAdapter> implements
		OnLayoutChangeListener {

	private static final String TAG = "WheelView";
	private int duration = 400;
	private BaseWheelAdapter adapter;
	private Scroller scroller;
	private TypeInterpolator interpolator;
	private int currentItem = 0;
	/**
	 * 向上滚动触发的位置
	 */
	private int startScrollCoord = 0;
	/**
	 * 向下滚动触发的位置
	 */
	private int endScrollCoord = 0;
	/**
	 * 列表第一个Item在列表的初始位置
	 */
	private int startCoord = 0;
	/**
	 * 列表最后一个Item在列表的最终位置
	 */
	private int endCoord = 0;
	/**
	 * 被选中的Item所在的位置
	 */
	private int selectionCoord = 0;

	private View selectedView;
	private View preView;
	private View nextView;
	private int preItemPosition = 0;
	private int nextItemPosition = 0;
	/**
	 * 开始发生滚动的位置
	 */
	private float startScrollPercent = 0.5f;
	/**
	 * 结束滚动的位置
	 */
	private float endScrollPercent = 0.5f;
	/**
	 * 第一个显示的View的位置
	 */
	private float startPercent = 0.5f;
	/**
	 * 最后一个显示的View的位置
	 */
	private float endPercent = 0.5f;
	/**
	 * 发生setSelection后，View需要滚动到的位置
	 */
	private float selectionPercent = 0.5f;

	private boolean blockLastItem = true;
	private boolean blockFirstItem = true;

	/**
	 * 是否是横向滚动
	 */
	private boolean isHorizontal = false;

	private final DataSetObserver dataSetObserver = new DataSetObserver() {
		@Override
		public void onChanged() {
			Log.d(TAG, "onChanged");
			// 当数据发生变化后，重新填充数据并且重新布局
			removeAllViewsInLayout();
			setScrollY(0);
			setScrollX(0);
			requestLayout();
		}

		@Override
		public void onInvalidated() {
			Log.d(TAG, "onInvalidated");
		}
	};

	public WheelView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public WheelView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public WheelView(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		interpolator = new TypeInterpolator();
		scroller = new Scroller(context, interpolator);
	}

	@Override
	public BaseWheelAdapter getAdapter() {
		return adapter;
	}

	@Override
	public void setAdapter(BaseWheelAdapter adapter) {
		this.adapter = adapter;
		adapter.registerDataSetObserver(dataSetObserver);
	}

	@Override
	public View getSelectedView() {
		return selectedView;
	}

	/**
	 * @return the currentItem
	 */
	public int getCurrentItem() {
		return currentItem;
	}

	public void setInterpolatorType(int type) {
		interpolator.setInterpolatorType(type);
	}

	/**
	 * 设置向上滚动的时候，开始滚动的位置
	 * 
	 * @Title setStartScrollYPosition
	 * @author haozening
	 * @Description
	 * @param percent
	 */
	public void setStartScrollPosition(float percent) {
		startScrollPercent = percent;
	}

	/**
	 * 设置向下滚动的时候，开始滚动的位置
	 * 
	 * @Title setEndScrollYPosition
	 * @author haozening
	 * @Description
	 * @param percent
	 */
	public void setEndScrollPosition(float percent) {
		endScrollPercent = percent;
	}

	/**
	 * 重新设置开始滚动的位置
	 * 
	 * @Title resetStartScrollPosition
	 * @author haozening
	 * @Description
	 * @param percent
	 */
	public void resetStartScrollPosition(float percent) {
		int measuredSize = 0;
		if (isHorizontal) {
			measuredSize = getMeasuredWidth();
		} else {
			measuredSize = getMeasuredHeight();
		}
		startScrollPercent = percent;
		startScrollCoord = (int) (measuredSize * startScrollPercent);
	}

	/**
	 * 重新设置结束滚动的位置
	 * 
	 * @Title resetEndScrollPosition
	 * @author haozening
	 * @Description
	 * @param percent
	 */
	public void resetEndScrollPosition(float percent) {
		int measuredSize = 0;
		if (isHorizontal) {
			measuredSize = getMeasuredWidth();
		} else {
			measuredSize = getMeasuredHeight();
		}
		endScrollPercent = percent;
		endScrollCoord = (int) (measuredSize * endScrollPercent);
	}

	public void resetStartPosition(float percent) {
		int measuredSize = 0;
		if (isHorizontal) {
			measuredSize = getMeasuredWidth();
		} else {
			measuredSize = getMeasuredHeight();
		}
		startPercent = percent;
		startCoord = (int) (measuredSize * startPercent);
	}

	public void resetEndPosition(float percent) {
		int measuredSize = 0;
		if (isHorizontal) {
			measuredSize = getMeasuredWidth();
		} else {
			measuredSize = getMeasuredHeight();
		}
		endPercent = percent;
		endCoord = (int) (measuredSize * endPercent);
	}

	/**
	 * 设置第一个View初始化的位置
	 * 
	 * @Title setStartPosition
	 * @author haozening
	 * @Description
	 * @param percent
	 */
	public void setStartPosition(float percent) {
		startPercent = percent;
	}

	/**
	 * 设置最后一个View初始化的位置
	 * 
	 * @Title setEndPosition
	 * @author haozening
	 * @Description
	 * @param percent
	 */
	public void setEndPosition(float percent) {
		endPercent = percent;
	}

	/**
	 * 设置选中的Item自动滚动到的位置
	 * 
	 * @Title setSelectionPosition
	 * @author haozening
	 * @Description
	 * @param percent
	 */
	public void setSelectionPosition(float percent) {
		selectionPercent = percent;
	}

	/**
	 * 设置是否横向滚动（默认是false，是纵向滚动）
	 * 
	 * @Title setHorizontal
	 * @author haozening
	 * @Description
	 * @param isHorizontal
	 */
	public void setHorizontal(boolean isHorizontal) {
		this.isHorizontal = isHorizontal;
	}

	/**
	 * 设置是否让最后一个Item停止在底部
	 * 
	 * @Title setBlockLastItem
	 * @author haozening
	 * @Description
	 * @param blockLastItem
	 */
	public void setBlockLastItem(boolean blockLastItem) {
		this.blockLastItem = blockLastItem;
	}

	/**
	 * 设置是否让第一个Item停止在顶部
	 * 
	 * @Title setBlockFirstItem
	 * @author haozening
	 * @Description
	 * @param blockFirstItem
	 */
	public void setBlockFirstItem(boolean blockFirstItem) {
		this.blockFirstItem = blockFirstItem;
	}

	public void setDuration(int scrollDuration) {
		duration = scrollDuration;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if (getChildCount() <= 0) {
			fillList();
		}
	}

	/**
	 * 当数据发生改变后，设置被选中的位置
	 */
	private int selectionPositionAfterDataSetChanged;

	/**
	 * 设置当数据发生改变后重新设置被选中的Item
	 * 
	 * @Title setSelectionAfterDataSetChanged
	 * @author haozening
	 * @Description
	 * @param position
	 */
	public void setSelectionAfterDataSetChanged(int position) {
		selectionPositionAfterDataSetChanged = position;
	}

	/**
	 * 设置被选中的View
	 */
	@Override
	public void setSelection(int position) {
		if (null == adapter) {
			return;
		}
		// 设置Item的被选中状态
		if (null != onSelectionChangedListener) {
			onSelectionChangedListener.unSelected(selectedView);
		}
		// 设置Item的被选中状态
		if (null != onSelectedChangedListener) {
			onSelectedChangedListener.onChanged(this, selectedView, position,
					false);
		}
		// 获取指定位置的View
		View view = getChildAt(position);
		// 当前View的坐标位置
		int currentViewPosition = 0;
		if (isHorizontal) { // 横向滚动的话，当前View坐标为横向坐标，在当前View中心点
			if (null != selectedView) {
				currentViewPosition = selectedView.getLeft()
						+ selectedView.getMeasuredWidth() / 2;
			}
		} else {
			if (null != selectedView) { // 纵向滚动的话，当前View坐标为纵向坐标，在当前View中心点
				currentViewPosition = selectedView.getTop()
						+ selectedView.getMeasuredHeight() / 2;
			}
		}
		if (view != null) {
			// 目标View的中心点
			int targetViewCenter = 0;
			if (isHorizontal) {
				targetViewCenter = view.getLeft() + view.getMeasuredWidth() / 2;
			} else {
				targetViewCenter = view.getTop() + view.getMeasuredHeight() / 2;
			}
			adapter.preView(selectedView);
			adapter.currentView(view);

			// 上一个当前Item
			int lastCurrentItem = currentItem;
			// 设置位置
			currentItem = position;
			nextItemPosition = position + 1;
			preItemPosition = position - 1;
			getView();

			int selectedViewCenterPosition = 0;
			if (isHorizontal) {
				int selectViewEnd = selectedView.getMeasuredWidth();
				selectedViewCenterPosition = selectedView.getLeft()
						+ selectViewEnd / 2 - getScrollX();
			} else {
				int selectViewEnd = selectedView.getMeasuredHeight();
				selectedViewCenterPosition = selectedView.getTop()
						+ selectViewEnd / 2 - getScrollY();
			}
			Log.d(TAG, "selectedViewCenterPosition : "
					+ selectedViewCenterPosition + " startCoord : "
					+ startCoord);
			// 判断是要向前滚动和向后滚动
			if (currentItem > lastCurrentItem
					|| selectedViewCenterPosition > startCoord) {
				moveToEnd(targetViewCenter - currentViewPosition);
			} else if (currentItem < lastCurrentItem
					|| selectedViewCenterPosition < startCoord) {
				Log.d(TAG, "currentViewPosition - targetViewCenter = "
						+ (currentViewPosition - targetViewCenter));
				Log.d(TAG, "startScrollCoord = " + startScrollCoord);
				moveToStart(currentViewPosition - targetViewCenter);
			} else {

			}
			selectionPositionAfterDataSetChanged = 0;
			if (null != onSelectionChangedListener) {
				onSelectionChangedListener.selected(selectedView);
			}
			if (null != onSelectedChangedListener) {
				onSelectedChangedListener.onChanged(this, selectedView,
						position, true);
			}
		}

	}

	private void moveToStart(int distance) {
		if (null == adapter) {
			return;
		}
		int firstItemStart = 0;
		int selectedViewCenterPosition = 0;
		int selectViewEnd = 0;
		if (isHorizontal) {
			selectViewEnd = selectedView.getMeasuredWidth();
			firstItemStart = getChildAt(0).getLeft() - getScrollX()
					+ getChildAt(0).getMeasuredWidth() / 2; // 计算第一个Item位置
			Log.d(TAG, "getLeft : " + getChildAt(0).getLeft()
					+ " getScrollX : " + getScrollX() + " firstItemStart : "
					+ firstItemStart + " startCoord : " + startCoord);
			selectedViewCenterPosition = selectedView.getLeft() + selectViewEnd
					/ 2 - getScrollX();
		} else {
			selectViewEnd = selectedView.getMeasuredHeight();
			firstItemStart = getChildAt(0).getTop() - getScrollY()
					+ getChildAt(0).getMeasuredHeight() / 2;
			selectedViewCenterPosition = selectedView.getTop() + selectViewEnd
					/ 2 - getScrollY();
		}
		if (firstItemStart < startCoord) {
			// 当前View的顶部在中间线下边，向上按键，不进行滚动，确保当前View顶部和中间线对齐
			if (selectedViewCenterPosition >= startScrollCoord) {
				if (null != onScrollListener) {
					Log.d(TAG, "-distance = " + (-distance));
					onScrollListener.beforeScroll(this, selectedView,
							-distance, SCROLL_STATE_IDLE);
				}
				return;
			} else if (selectedViewCenterPosition < startScrollCoord
					&& startScrollCoord - selectedViewCenterPosition < selectViewEnd) {
				if (null != onScrollListener) {
					onScrollListener.beforeScroll(this, selectedView,
							startScrollCoord - selectedViewCenterPosition,
							SCROLL_STATE_SCROLLING);
				}
				Log.d(TAG, "startScrollCoord - selectedViewCenterPosition = "
						+ (startScrollCoord - selectedViewCenterPosition));
				// 当前View的顶部在中间线之上，并且中间线和View顶部的差小于当前View的高度，滚动一部分，确保当前View顶部和中间线对齐
				smoothScroll(startScrollCoord - selectedViewCenterPosition);
			} else {
				if (startCoord - firstItemStart < distance) {
					if (null != onScrollListener) {
						onScrollListener.beforeScroll(this, selectedView,
								startCoord - firstItemStart,
								SCROLL_STATE_SCROLLING);
					}
					Log.d(TAG, "startCoord - firstItemTop = "
							+ (startCoord - firstItemStart));
					smoothScroll(startCoord - firstItemStart);
				} else {
					int realDistance = distance;
					Log.d(TAG,
							"startScrollCoord - selectedViewCenterPosition = "
									+ (startScrollCoord - selectedViewCenterPosition)
									+ " realDistance : " + realDistance);
					if (startScrollCoord - selectedViewCenterPosition < distance) {
						realDistance = startScrollCoord
								- selectedViewCenterPosition;
						Log.d(TAG, "startCoord - firstItemStart = "
								+ (startCoord - firstItemStart));
						if (startCoord - firstItemStart < realDistance) {
							realDistance = startCoord - firstItemStart;
							Log.d(TAG, "realDistance : " + realDistance);
						}
					}
					if (null != onScrollListener) {
						onScrollListener.beforeScroll(this, selectedView,
								realDistance, SCROLL_STATE_SCROLLING);
					}
					Log.d(TAG, "realDistance = " + realDistance);
					Log.d(TAG, "distance = " + distance);
					smoothScroll(realDistance);
				}
			}
		} else {
			if (null != onScrollListener) {
				onScrollListener.beforeScroll(this, selectedView, -distance,
						SCROLL_STATE_IDLE);
			}
		}
	}

	private void moveToEnd(int distance) {
		if (null == adapter) {
			return;
		}
		int lastItemEnd = 0;
		int selectedViewCenterPosition = 0;
		int selectViewEnd = 0;
		measureView(selectedView);
		if (isHorizontal) {
			selectViewEnd = selectedView.getMeasuredWidth();
			Log.d(TAG,
					"selectedView.getMeasuredWidth() : "
							+ selectedView.getMeasuredWidth());
			lastItemEnd = getChildAt(adapter.getCount() - 1).getRight()
					- getScrollX()
					- getChildAt(adapter.getCount() - 1).getMeasuredWidth() / 2;
			selectedViewCenterPosition = selectedView.getLeft() + selectViewEnd
					/ 2 - getScrollX();
		} else {
			selectViewEnd = selectedView.getMeasuredHeight();
			lastItemEnd = getChildAt(adapter.getCount() - 1).getBottom()
					- getScrollY()
					- getChildAt(adapter.getCount() - 1).getMeasuredHeight()
					/ 2;
			selectedViewCenterPosition = selectedView.getTop() + selectViewEnd
					/ 2 - getScrollY();
		}
		if (lastItemEnd > endCoord) {
			// 如果当前View的顶部在中间线之上，就不进行滚动，确保焦点View到中间线位置后再滚动
			Log.d(TAG, "selectedViewCenterPosition : "
					+ selectedViewCenterPosition + "  endScrollCoord : "
					+ endScrollCoord);
			if (selectedViewCenterPosition <= endScrollCoord) {
				Log.d(TAG, "selectedViewCenterPosition : "
						+ selectedViewCenterPosition + "endScrollCoord : "
						+ endScrollCoord);
				if (null != onScrollListener) {
					onScrollListener.beforeScroll(this, selectedView, distance,
							SCROLL_STATE_IDLE);
				}
				return;
			} else if (selectedViewCenterPosition > endScrollCoord
					&& selectedViewCenterPosition - endScrollCoord < selectViewEnd) {
				Log.d(TAG, "selectedViewCenterPosition : "
						+ selectedViewCenterPosition + "endScrollCoord : "
						+ endScrollCoord + " selectViewEnd : " + selectViewEnd);
				if (null != onScrollListener) {
					onScrollListener.beforeScroll(this, selectedView,
							endScrollCoord - selectedViewCenterPosition,
							SCROLL_STATE_SCROLLING);
				}
				// 如果当前View顶部在中间线以下，并且顶部和中间线的距离小于一个View高度，就滚动一部分，确保焦点View到达中间线位置
				smoothScroll(endScrollCoord - selectedViewCenterPosition);
			} else { // 当前的View的顶部和中间线相等，就滚动
				if (lastItemEnd - endCoord < distance) { // 比较最后一个Item和结束显示View的位置的差与滚动距离的大小
					Log.d(TAG, "lastItemEnd : " + lastItemEnd + " endCoord : "
							+ endCoord + " distance : " + distance);
					if (null != onScrollListener) {
						onScrollListener.beforeScroll(this, selectedView,
								endCoord - lastItemEnd, SCROLL_STATE_SCROLLING);
					}
					smoothScroll(endCoord - lastItemEnd);
				} else { // 如果最后一个View距离结束位置大于需要滚动的距离
					Log.d(TAG, "lastItemEnd : " + lastItemEnd + " endCoord : "
							+ endCoord + " distance : " + distance);
					int realDistance = distance;
					if (selectedViewCenterPosition - endScrollCoord > distance) {
						realDistance = selectedViewCenterPosition
								- endScrollCoord;
						if (lastItemEnd - endCoord < realDistance) {
							realDistance = lastItemEnd - endCoord;
						}
					}
					if (null != onScrollListener) {
						onScrollListener.beforeScroll(this, selectedView,
								-realDistance, SCROLL_STATE_SCROLLING);
					}
					smoothScroll(-realDistance);
				}
			}
		} else {
			if (null != onScrollListener) {
				onScrollListener.beforeScroll(this, selectedView, distance,
						SCROLL_STATE_IDLE);
			}
		}
	}

	/**
	 * 填充列表
	 * 
	 * @Title fillList
	 * @author haozening
	 * @Description
	 */
	private void fillList() {
		if (null == adapter) {
			return;
		}
		int count = adapter.getCount();
		int measuredSize = 0;
		if (isHorizontal) {
			measuredSize = getMeasuredWidth();
		} else {
			measuredSize = getMeasuredHeight();
		}
		startScrollCoord = (int) (measuredSize * startScrollPercent);
		endScrollCoord = (int) (measuredSize * endScrollPercent);
		startCoord = (int) (measuredSize * startPercent);
		endCoord = (int) (measuredSize * endPercent);
		selectionCoord = (int) (measuredSize * selectionPercent);
		int totalDistance = 0;
		int selectedViewStart = 0;
		for (int i = 0; i < count; i++) {
			View view = adapter.getView(i, null, this);
			measureView(view);
			if (i == selectionPositionAfterDataSetChanged) {
				selectedView = view;
				currentItem = i;
				if (isHorizontal) {
					selectedViewStart = totalDistance + startCoord
							- view.getMeasuredWidth() / 2;
				} else {
					selectedViewStart = totalDistance + startCoord
							- view.getMeasuredHeight() / 2;
				}
				selectedView.addOnLayoutChangeListener(this);
			}
			LayoutParams params = view.getLayoutParams();
			if (null == params) {
				params = new LayoutParams(LayoutParams.MATCH_PARENT,
						LayoutParams.WRAP_CONTENT);
			}
			addViewInLayout(view, i, params);
			if (isHorizontal) {
				int itemStart = totalDistance + startCoord
						- view.getMeasuredWidth() / 2;
				view.layout(itemStart, 0, itemStart + view.getMeasuredWidth(),
						view.getMeasuredHeight());
				totalDistance += view.getMeasuredWidth();
			} else {
				int itemStart = totalDistance + startCoord
						- view.getMeasuredHeight() / 2;
				view.layout(0, itemStart, view.getMeasuredWidth(), itemStart
						+ view.getMeasuredHeight());
				totalDistance += view.getMeasuredHeight();
			}
		}

		if (null != selectedView) {
			int selectViewCenter = 0;
			int scrollDistance = 0;
			int lastItemEnd = 0;
			int firstItemStart = 0;
			if (isHorizontal) {
				selectViewCenter = selectedView.getMeasuredWidth() / 2;
				scrollDistance = getScrollX();
				lastItemEnd = getChildAt(adapter.getCount() - 1).getRight();
				firstItemStart = getChildAt(0).getLeft();
			} else {
				selectViewCenter = selectedView.getMeasuredHeight() / 2;
				scrollDistance = getScrollY();
				lastItemEnd = getChildAt(adapter.getCount() - 1).getBottom();
				firstItemStart = getChildAt(0).getTop();
			}
			// 目标View的中心位置
			int targetViewCenter = selectedViewStart + selectViewCenter;
			// 将要滚动的距离
			int distance = scrollDistance + selectionCoord - targetViewCenter;
			Log.d(TAG, "distance = " + distance + " scrollDistance = "
					+ scrollDistance + " selectionCoord = " + selectionCoord
					+ " targetViewCenter = " + targetViewCenter + " " + this);

			if (targetViewCenter - selectionCoord > 0) { // 向前滚动，焦点后移
				// 目标视图距离大于 （最后一个Item到View底部的距离）重新设置滚动距离，
				// 保证当最后一个Item刚出来时，不向上滚动，而是保持在View底部
				if (targetViewCenter - selectionCoord > lastItemEnd
						- measuredSize
						&& !blockFirstItem) {
					Log.d(TAG, "distance = " + distance + " lastItemEnd = "
							+ lastItemEnd + " measuredSize = " + measuredSize);
					if (lastItemEnd - measuredSize > 0) {
						distance = -(lastItemEnd - measuredSize);
					} else {
						distance = 0;
					}
				}
				if (null != onScrollListener) {
					selectedView.removeOnLayoutChangeListener(this);
					onScrollListener.beforeScroll(this, selectedView, distance,
							SCROLL_STATE_SCROLLING);
				}
				Log.d(TAG, "distance = " + distance);
				smoothScroll(distance);
			}
			if (targetViewCenter - selectionCoord < 0) { // 向后滚动，焦点前移
				if (selectionCoord - targetViewCenter > 0 - firstItemStart
						&& !blockLastItem) {
					distance = -firstItemStart;
				}
				if (null != onScrollListener) {
					selectedView.removeOnLayoutChangeListener(this);
					onScrollListener.beforeScroll(this, selectedView, distance,
							SCROLL_STATE_SCROLLING);
				}
				smoothScroll(distance);
			}

		}
		nextItemPosition = currentItem + 1;
		preItemPosition = currentItem - 1;
		getView();
	}

	/**
	 * 测量View的方法，根据布局参数测量View的宽高
	 * 
	 * @Title measureView
	 * @author haozening
	 * @Description
	 * @param view
	 */
	private void measureView(View view) {
		if (null == view) {
			return;
		}
		LayoutParams params = view.getLayoutParams();
		if (null == params) {
			return;
		} else {
			int widthMeasureSpec = 0;
			int heightMeasureSpec = 0;
			if (params.width == LayoutParams.WRAP_CONTENT) {
				Log.d(TAG, "params.width == LayoutParams.WRAP_CONTENT");
				widthMeasureSpec = MeasureSpec.makeMeasureSpec(
						LayoutParams.WRAP_CONTENT, MeasureSpec.UNSPECIFIED);
			} else if (params.width == LayoutParams.MATCH_PARENT) {
				Log.d(TAG, "params.width == LayoutParams.MATCH_PARENT");
				widthMeasureSpec = MeasureSpec.makeMeasureSpec(
						getMeasuredWidth(), MeasureSpec.EXACTLY);
			} else {
				Log.d(TAG, "params.width=" + params.width);
				widthMeasureSpec = MeasureSpec.makeMeasureSpec(params.width,
						MeasureSpec.EXACTLY);
			}
			if (params.height == LayoutParams.WRAP_CONTENT) {
				heightMeasureSpec = MeasureSpec.makeMeasureSpec(
						LayoutParams.WRAP_CONTENT, MeasureSpec.UNSPECIFIED);
			} else if (params.height == LayoutParams.MATCH_PARENT) {
				heightMeasureSpec = MeasureSpec.makeMeasureSpec(
						getMeasuredHeight(), MeasureSpec.EXACTLY);
			} else {
				heightMeasureSpec = MeasureSpec.makeMeasureSpec(params.height,
						MeasureSpec.EXACTLY);
			}
			view.measure(widthMeasureSpec, heightMeasureSpec);
		}
	}

	/**
	 * 上一次按键发生的时间，用于判断是否执行完移动
	 */
	private long lastTime = 0;

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			// 当滚动计算还没有完成或者还没达到指定时间，就不处理按键事件
			if (scroller.computeScrollOffset()
					|| System.currentTimeMillis() - lastTime < duration) {
				return true;
			} else {
				lastTime = System.currentTimeMillis();
			}
			switch (event.getKeyCode()) {
			case KeyEvent.KEYCODE_DPAD_DOWN:
				if (!isHorizontal) {
					moveToEnd();
				}
				break;
			case KeyEvent.KEYCODE_DPAD_UP:
				if (!isHorizontal) {
					moveToStart();
				}
				break;
			case KeyEvent.KEYCODE_DPAD_LEFT:
				if (isHorizontal) {
					moveToStart();
				}
				break;
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				if (isHorizontal) {
					moveToEnd();
				}
				break;
			case KeyEvent.KEYCODE_DPAD_CENTER:
				performItemClick(selectedView, currentItem,
						selectedView.getId());
				break;
			}
		}
		return super.dispatchKeyEvent(event);
	}

	/**
	 * 向上按键触发的移动
	 * 
	 * @Title moveUp
	 * @author haozening
	 * @Description
	 */
	private void moveToStart() {
		if (null == adapter) {
			return;
		}
		// 如果当前Item已经到达第一个，就不进行移动
		if (currentItem <= 0 || null == selectedView) {
			return;
		}
		if (null != onSelectedChangedListener) {
			onSelectedChangedListener.onChanged(this, selectedView,
					currentItem, false);
		}
		// 计算当前、前一个、后一个Item的位置
		currentItem--;
		nextItemPosition = currentItem + 1;
		preItemPosition = currentItem - 1;
		getView();
		if (null != onSelectedChangedListener) {
			onSelectedChangedListener.onChanged(this, selectedView,
					currentItem, true);
		}
		int scrollDy = 0;
		// 如果前一个View不为空，测量前一个View，获取要移动的距离 // TODO 应该移动当前View的距离
		if (null != preView) {
			measureView(preView);
			if (isHorizontal) {
				scrollDy = preView.getMeasuredWidth();
			} else {
				scrollDy = preView.getMeasuredHeight();
			}
		} else {
			// 如果前一个View为空，就测量当前的View，移动当前View的距离
			measureView(selectedView);
			if (isHorizontal) {
				scrollDy = selectedView.getMeasuredWidth();
			} else {
				scrollDy = selectedView.getMeasuredHeight();
			}
		}
		moveToStart(scrollDy);
	}

	/**
	 * 向下按键，触发的移动
	 * 
	 * @Title moveDown
	 * @author haozening
	 * @Description
	 */
	private void moveToEnd() {
		if (null == adapter) {
			return;
		}
		// 如果当前的Item大于数据总数的情况，就不进行移动
		if (currentItem >= adapter.getCount() - 1 || null == selectedView) {
			return;
		}

		if (null != onSelectedChangedListener) {
			onSelectedChangedListener.onChanged(this, selectedView,
					currentItem, false);
		}
		// 计算当前、前一个、后一个Item的位置
		currentItem++;
		nextItemPosition = currentItem + 1;
		preItemPosition = currentItem - 1;
		getView();
		if (null != onSelectedChangedListener) {
			onSelectedChangedListener.onChanged(this, selectedView,
					currentItem, true);
		}
		int scrollDy = 0;
		if (null != nextView) {
			measureView(nextView);
			if (isHorizontal) {
				scrollDy = nextView.getMeasuredWidth();
			} else {
				scrollDy = nextView.getMeasuredHeight();
			}
		} else {
			measureView(selectedView);
			if (isHorizontal) {
				scrollDy = selectedView.getMeasuredWidth();
			} else {
				scrollDy = selectedView.getMeasuredHeight();
			}
		}
		moveToEnd(scrollDy);
	}

	/**
	 * 获取当前、上一个、后一个View
	 * 
	 * @Title getView
	 * @author haozening
	 * @Description
	 */
	private void getView() {
		if (null == adapter) {
			return;
		}
		if (currentItem >= 0 && currentItem < adapter.getCount()) {
			selectedView = getChildAt(currentItem);
			adapter.currentView(selectedView);
		} else {
			selectedView = null;
		}
		if (nextItemPosition >= 0 && nextItemPosition < adapter.getCount()) {
			nextView = getChildAt(nextItemPosition);
			adapter.nextView(nextView);
		} else {
			nextView = null;
		}
		if (preItemPosition >= 0 && preItemPosition < adapter.getCount()) {
			preView = getChildAt(preItemPosition);
			adapter.preView(preView);
		} else {
			preView = null;
		}

	}

	/**
	 * 滚动距离的计算
	 */
	@Override
	public void computeScroll() {
		if (scroller.computeScrollOffset()) {
			scrollTo(scroller.getCurrX(), scroller.getCurrY());
			invalidate();
			if (null != onScrollListener) {
				onScrollListener.onScrollChanged(this, selectedView,
						SCROLL_STATE_SCROLLING);
			}
		} else {
			Log.d(TAG, "!computeScrollOffset");
			if (null != onScrollListener) {
				onScrollListener.onScrollChanged(this, selectedView,
						SCROLL_STATE_IDLE);
			}
		}
		super.computeScroll();
	}

	/**
	 * 平滑滚动方法
	 * 
	 * @Title smoothScroll
	 * @author haozening
	 * @Description
	 * @param dy
	 */
	private void smoothScroll(int d) {
		if (isHorizontal) {
			scroller.startScroll(getScrollX(), 0, -d, 0, duration);
		} else {
			scroller.startScroll(0, getScrollY(), 0, -d, duration);
		}
		invalidate();
	}

	public static final int SCROLL_STATE_IDLE = 100921;
	public static final int SCROLL_STATE_SCROLLING = 100922;

	private OnScrollListener onScrollListener;

	/**
	 * 设置滚动监听器
	 * 
	 * @Title setOnScrollListener
	 * @author haozening
	 * @Description
	 * @param listener
	 */
	public void setOnScrollListener(OnScrollListener listener) {
		onScrollListener = listener;
	}

	/**
	 * 滚动监听器
	 * 
	 * @ClassName OnScrollListener
	 * @Description
	 * @author haozening
	 * @date 2014年10月9日 下午9:59:12
	 * 
	 */
	public static interface OnScrollListener {
		/**
		 * 开始滚动之前调用的方法
		 * 
		 * @Title beforeScroll
		 * @author haozening
		 * @Description
		 * @param parent
		 * @param view
		 * @param scrollDistance
		 *            滚动的距离
		 * @param nextState
		 *            下一个滚动状态
		 */
		public void beforeScroll(View parent, View view, int scrollDistance,
				int nextState);

		public void onScrollChanged(View parent, View view, int scrollState);
	}

	private OnSelectionChangedListener onSelectionChangedListener;

	public void setOnSelectionChangedListener(
			OnSelectionChangedListener listener) {
		onSelectionChangedListener = listener;
	}

	/**
	 * 当发生setSelection后的回调，当被选中的View被添加到视图上并且测量好后的回调
	 * 
	 * @ClassName OnSelectedViewAttachListener
	 * @Description
	 * @author haozening
	 * @date 2014年10月10日 下午12:11:08
	 * 
	 */
	public static interface OnSelectionChangedListener {
		/**
		 * 当setSelection设置后，被取消选中的View
		 * 
		 * @Title unSelected
		 * @author haozening
		 * @Description
		 * @param view
		 */
		public void unSelected(View view);

		/**
		 * 当setSelection设置后，被选中的View
		 * 
		 * @Title selected
		 * @author haozening
		 * @Description
		 * @param view
		 */
		public void selected(View view);
	}

	private OnSelectedChangedListener onSelectedChangedListener;

	public void setOnSelectedChangedListener(OnSelectedChangedListener listener) {
		onSelectedChangedListener = listener;
	}

	/**
	 * 当Item的选中状态发生改变的监听器
	 * 
	 * @ClassName OnSelectedChangedListener
	 * @Description
	 * @author haozening
	 * @date 2014年11月3日 下午10:18:19
	 *
	 */
	public static interface OnSelectedChangedListener {
		/**
		 * 列表中被选中的View发生改变后的回调方法
		 * 
		 * @Title onChanged
		 * @author haozening
		 * @Description
		 * @param parent
		 *            列表
		 * @param view
		 *            当前Item
		 * @param position
		 *            当前Item的位置
		 * @param selected
		 *            选中状态
		 */
		public void onChanged(View parent, View view, int position,
				boolean selected);
	}

	private OnViewLayoutChangeListener onViewLayoutChangeListener;

	public void setOnViewLayoutChangeListener(
			OnViewLayoutChangeListener listener) {
		onViewLayoutChangeListener = listener;
	}

	/**
	 * 当布局改变后发生的回调
	 * 
	 * @ClassName OnViewLayoutChangeListener
	 * @Description
	 * @author haozening
	 * @date 2014年10月12日 下午3:34:23
	 * 
	 */
	public static interface OnViewLayoutChangeListener {
		public void onLayoutChange(View view);
	}

	@Override
	public void onLayoutChange(View v, int left, int top, int right,
			int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
		if (null != onViewLayoutChangeListener) {
			onViewLayoutChangeListener.onLayoutChange(selectedView);
		}
	}

}
