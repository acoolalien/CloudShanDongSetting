package com.shandong.cloudtv.common;

import android.content.Context;
import android.os.Build;
import android.support.v4.view.KeyEventCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.shandong.cloudtv.settings.R;
import com.shandong.cloudtv.settings.UpdateActivity;

public class MyViewPager extends ViewPager {

	private ImageView mRight;
	private ImageView mLeft;

	public MyViewPager(Context context) {
		// TODO Auto-generated constructor stub
		super(context);

	}

	public MyViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean executeKeyEvent(KeyEvent event) {
		boolean handled = false;
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			switch (event.getKeyCode()) {
			case 21:

				mLeft.setImageDrawable(getContext().getResources().getDrawable(
						R.drawable.update_left_selected));
				handled = arrowScroll(17);
				break;
			case 22:

				mRight.setImageDrawable(getContext().getResources()
						.getDrawable(R.drawable.update_right_selected));
				handled = arrowScroll(66);
				break;
			case 61:
				if (Build.VERSION.SDK_INT < 11) {
					break;
				}
				if (KeyEventCompat.hasNoModifiers(event)) {
					handled = arrowScroll(2);
				} else {
					if (!KeyEventCompat.hasModifiers(event, 1))
						break;
					handled = arrowScroll(1);
				}
			}

		} else if (event.getAction() == KeyEvent.ACTION_UP) {
			switch (event.getKeyCode()) {
			case KeyEvent.KEYCODE_DPAD_RIGHT: {
				mRight.setImageDrawable(getContext().getResources()
						.getDrawable(R.drawable.update_right));
				if (UpdateActivity.mPosition == 0) {
					mLeft.setVisibility(View.INVISIBLE);
				} else if (UpdateActivity.mPosition != 0) {
					mLeft.setVisibility(View.VISIBLE);
				}

				if (UpdateActivity.mPosition == (UpdateActivity.mListSize - 1)) {
					mRight.setVisibility(View.INVISIBLE);
				} else if (UpdateActivity.mPosition != (UpdateActivity.mListSize - 1)) {
					mRight.setVisibility(View.VISIBLE);
				}

				break;
			}
			case KeyEvent.KEYCODE_DPAD_LEFT: {
				mLeft.setImageDrawable(getContext().getResources().getDrawable(
						R.drawable.update_left));
				if (UpdateActivity.mPosition == 0) {
					mLeft.setVisibility(View.INVISIBLE);
				} else if (UpdateActivity.mPosition != 0) {
					mLeft.setVisibility(View.VISIBLE);
				}

				if (UpdateActivity.mPosition == (UpdateActivity.mListSize - 1)) {
					mRight.setVisibility(View.INVISIBLE);
				} else if (UpdateActivity.mPosition != (UpdateActivity.mListSize - 1)) {
					mRight.setVisibility(View.VISIBLE);
				}
				break;
			}
			}
		}

		return handled;
	}

	public ImageView getmRight() {
		return mRight;
	}

	public void setmRight(ImageView mRight) {
		this.mRight = mRight;
	}

	public ImageView getmLeft() {
		return mLeft;
	}

	public void setmLeft(ImageView mLeft) {
		this.mLeft = mLeft;
	}

}
