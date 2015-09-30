package com.shandong.cloudtv.settings;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;



import com.shandong.cloudtv.common.CommonItemList;

import android.text.method.HideReturnsTransformationMethod;  
import android.text.method.PasswordTransformationMethod;  



import android.content.ContentResolver;
import android.util.Log;


public class SettingCode extends Activity {
	private final static String SUPUR_PASSWORD = "600804";
	private final static String DEFAULT_PASSWORD = "201508";
	private Gallery gallery;
	private ImageView ivViewFocus;
	private TextView textView;
	private String[] myImageIds = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" };
	private TextView textViewwc;
	private TextView textViewsc;
	private TextView textViewyc;
	private boolean isLongClick = false;// 回删长按事件删除线程的标志位
	private Context mContext = this;
	private int mailionoffSelectId = 0;
	int mode=MODE_WORLD_READABLE+MODE_WORLD_WRITEABLE;
	SharedPreferences perfer;
	SharedPreferences.Editor editor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settingcode);

		ivViewFocus = (ImageView) findViewById(R.id.focus);
		textView = (TextView) findViewById(R.id.mm);
		textViewsc = (TextView) findViewById(R.id.sc);
		textViewwc = (TextView) findViewById(R.id.wc);
		textViewyc = (TextView) findViewById(R.id.yc);
		
		perfer=getSharedPreferences("code000",mode);		
		
//		textView.setTransformationMethod(PasswordTransformationMethod.getInstance());
		/*	editor=perfer.edit();
		editor.putString("password", "mm");
		editor.commit();                 */
		
		textViewsc.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String text = (String) textView.getText();
		            if (text.length() > 0) {     				    
					text = text.substring(0, text.length() - 1);
					textView.setText(text);
				    }
			}
		});
		
		//发送广播的方式传输密码
		textViewwc.setOnClickListener(new OnClickListener() {
			
			@SuppressLint("NewApi")
			@Override
			public void onClick(View arg0) {
			
				
				
				String text = (String) textView.getText();
				if(text.length() < 6){
					Toast.makeText(mContext, mContext.getResources().getString(R.string.inputless6), Toast.LENGTH_SHORT).show();
				} 
				String k = textView.getText().toString();
				String dbpassword = perfer.getString("password",null);  
				if(dbpassword == null)
				dbpassword = DEFAULT_PASSWORD;
				
				if(k.equals(SUPUR_PASSWORD) || k.equals(dbpassword)){
					//Intent intent1 = new Intent(SettingCode.this , SettingActivity.class);
					//startActivity(intent1);	
					finish();
				}	else {
					Toast.makeText(mContext, mContext.getResources().getString(R.string.wrongcode), Toast.LENGTH_SHORT).show();
					textView.setText("");
				}
				
				
			}
			
		});
			
    
		
		textViewyc.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				startActivity(new Intent().setClass(mContext, ModifyCode.class));
			}
		});
		
		

		textViewsc.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				isLongClick = true;
				new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						while (isLongClick) {
							handler.sendEmptyMessage(1);// 开启一个线程循环，每隔0.1毫秒搜索关键字长度减1
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}).start();
				return false;
			}
		});
		textViewsc.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER && isLongClick) {
					isLongClick = false;
				}
				return false;
			}
		});
		

		gallery = (Gallery) findViewById(R.id.gallery);
		gallery.setAdapter(new TextAdapter(this));
		gallery.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				String aString = (String) textView.getText();
				String bString = Integer.toString(position);
				if (bString.length() > 1) {
					bString = bString.substring(bString.length() - 1, bString.length());
				}
				textView.setText(aString + bString);
				textView.setTransformationMethod(PasswordTransformationMethod.getInstance());
			}
		});

		// 2147483647 / 2 = 1073741820 - 1
		// 设置gallery的当前项为一个比较大的数，以便一开始就可以左右循环滑动
		int n = Integer.MAX_VALUE / 2 % myImageIds.length;
		int itemPosition = Integer.MAX_VALUE / 2 - n;
		gallery.setSelection(itemPosition);
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.what == 1) {
				String text = textView.getText().toString();
				if (text.length() > 0) {
					text = text.substring(0, text.length() - 1);
					textView.setText(text);
				} else {
					isLongClick = false;
				}
			}
		}
	};

	public class TextAdapter extends BaseAdapter {
		private Context mContext;

		public TextAdapter(Context c) {
			mContext = c;
		}

		public int getCount() /* 一定要重写的方法getCount,传回图片数目总数 */
		{
			return Integer.MAX_VALUE;
		}

		public Object getItem(int position) /* 一定要重写的方法getItem,传回position */
		{
			return position;
		}

		public long getItemId(int position) /* 一定要重写的方法getItemId,传回position */
		{
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			TextView i = new TextView(mContext);
			i.setTextSize(60);
			i.setTextColor(android.graphics.Color.WHITE);
			i.setText(myImageIds[position % myImageIds.length]);
			i.setLayoutParams(new Gallery.LayoutParams((int) getResources().getDimension(R.dimen.text_key_width), (int) getResources().getDimension(
					R.dimen.text_key_height))); /* 重新设定Layout的宽高 */
			i.setGravity(Gravity.CENTER);
			i.setOnFocusChangeListener(new OnFocusChangeListener() {
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					// TODO Auto-generated method stub
					if (hasFocus) {
						ivViewFocus.setVisibility(View.VISIBLE);
					} else {
						ivViewFocus.setVisibility(View.INVISIBLE);
					}
				}
			});
			return i;
		}
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		Log.e("huxing", "------------onDestroy");
		super.onDestroy();	
	}
	
	@Override
	public boolean onKeyDown(int arg0, KeyEvent arg1) {
		// TODO Auto-generated method stub
		if(arg0 == KeyEvent.KEYCODE_BACK) {
			Intent intent=new Intent();
		     intent.putExtra("pass", 1);
		     setResult(1024, intent);
		     Log.e("huxing", "------dessss");
		}
		return super.onKeyDown(arg0, arg1);
	}
}
