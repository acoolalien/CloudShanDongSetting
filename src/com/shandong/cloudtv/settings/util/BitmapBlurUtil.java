/**
 * @Title BitmapBlur.java
 * @Package com.hiveview.cloudscreen.video.utils
 * @author haozening
 * @date 2014年9月5日 下午5:02:33
 * @Description 
 * @version V1.0
 */
package com.shandong.cloudtv.settings.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

/**
 * @ClassName BitmapBlur
 * @Description
 * @author haozening
 * @date 2014年9月5日 下午5:02:33
 * 
 */
@SuppressLint("NewApi")
public class BitmapBlurUtil {

	public static class BitmapBlurUtilHolder {
		static final BitmapBlurUtil UTIL = new BitmapBlurUtil();
	}

	private BitmapBlurUtil() {
	}
	
	public static BitmapBlurUtil getInstance() {
		return BitmapBlurUtilHolder.UTIL;
	}
	
	/**
	 * 对bitmap进行高斯模糊
	 * 
	 * @Title blurBitmap
	 * @author haozening
	 * @Description
	 * @param bitmap
	 * @param context
	 * @param radius
	 * @return
	 */
	public Bitmap blurBitmap(Bitmap bitmap, Context context, float radius) {

		Bitmap outBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);

		RenderScript script = RenderScript.create(context);
		ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(script, Element.U8_4(script));

		// 传入输入输出的bitmap，创建配置类
		Allocation allIn = Allocation.createFromBitmap(script, bitmap);
		Allocation allOut = Allocation.createFromBitmap(script, outBitmap);

		// 设置模糊的角度
		blurScript.setRadius(radius);

		// 执行模糊操作，把结果设置到输出中
		blurScript.setInput(allIn);
		blurScript.forEach(allOut);

		// 通过输出配置类获取最终结果
		allOut.copyTo(outBitmap);

		// 回收资源
		bitmap.recycle();
		script.destroy();

		return outBitmap;

	}

	/**
	 * 对bitmap进行高斯模糊
	 * 
	 * @Title blurBitmap
	 * @author haozening
	 * @Description
	 * @param bitmap
	 * @param context
	 * @return
	 */
	public Bitmap blurBitmap(Bitmap bitmap, Context context) {
		return blurBitmap(bitmap, context, 20.f);
	}
}
