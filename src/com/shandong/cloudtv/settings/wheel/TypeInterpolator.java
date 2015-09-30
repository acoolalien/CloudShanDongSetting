/**
 * @Title TypeInterpolator.java
 * @Package com.hiveview.cloudscreen.player.view
 * @author haozening
 * @date 2014年10月19日 下午2:24:06
 * @Description 
 * @version V1.0
 */
package com.shandong.cloudtv.settings.wheel;

import android.view.animation.Interpolator;

/**
 * 根据类型来选择是加速、减速还是线性插值器
 * 
 * @ClassName TypeInterpolator
 * @Description
 * @author haozening
 * @date 2014年10月19日 下午2:24:06
 * 
 */
public class TypeInterpolator implements Interpolator {
	private final float mFactor;
	private final double mDoubleFactor;
	public static final int TYPE_ACCELERATE = 10191218;
	public static final int TYPE_DECELERATE = 10191219;
	public static final int TYPE_ACCELERATE_DECELERATE = 10191220;
	public static final int TYPE_LINEAR = 0;
	private int type = TYPE_LINEAR;

	public TypeInterpolator() {
		mFactor = 1.0f;
		mDoubleFactor = 2.0;
	}

	public void setInterpolatorType(int type) {
		this.type = type;
	}

	public float getInterpolation(float input) {
		if (type == TYPE_ACCELERATE) {
			if (mFactor == 1.0f) {
				return input * input;
			} else {
				return (float) Math.pow(input, mDoubleFactor);
			}
		} else if (type == TYPE_DECELERATE) {
			float result;
			if (mFactor == 1.0f) {
				result = (float) (1.0f - (1.0f - input) * (1.0f - input));
			} else {
				result = (float) (1.0f - Math.pow((1.0f - input), 2 * mFactor));
			}
			return result;
		} else if (type == TYPE_ACCELERATE_DECELERATE) {
			return (float) (Math.cos((input + 1) * Math.PI) / 2.0f) + 0.5f;
		} else {
			return input;
		}
	}
}
