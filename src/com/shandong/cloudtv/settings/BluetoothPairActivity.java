package com.shandong.cloudtv.settings;

import android.app.Activity;
import android.os.Bundle;

public class BluetoothPairActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*
		 * BluetoothPairDialog dialog = new BluetoothPairDialog(this); EditText
		 * editText = (EditText) dialog.findViewById(R.id.editview1);
		 * editText.setVisibility(View.GONE); dialog.show();
		 */
		this.setContentView(R.layout.bluetooth_pair_require);
	}

}
