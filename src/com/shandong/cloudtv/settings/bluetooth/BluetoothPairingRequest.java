/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.shandong.cloudtv.settings.bluetooth;

import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.PowerManager;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.shandong.cloudtv.settings.BluetoothPairActivity;
import com.shandong.cloudtv.settings.R;
import com.shandong.cloudtv.settings.widget.BluetoothPairDialog;

/**
 * BluetoothPairingRequest is a receiver for any Bluetooth pairing request. It
 * checks if the Bluetooth Settings is currently visible and brings up the PIN,
 * the passkey or a confirmation entry dialog. Otherwise it puts a Notification
 * in the status bar, which can be clicked to bring up the Pairing entry dialog.
 */
public final class BluetoothPairingRequest extends BroadcastReceiver {

	private static final int NOTIFICATION_ID = android.R.drawable.stat_sys_data_bluetooth;
	private static BluetoothPairDialog dialog = null;
	private String TAG = "BluetoothPairingRequest";
	private EditText editText = null;
	private String mPairingKey;

	@SuppressLint("NewApi")
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (action.equals(BluetoothDevice.ACTION_PAIRING_REQUEST)) {
			// convert broadcast intent into activity intent (same action
			// string)
			if (dialog == null) {
				dialog = new BluetoothPairDialog(context);
			}
			String test;
			editText = (EditText) dialog.findViewById(R.id.editview1);
			TextView textView1 = (TextView) dialog.findViewById(R.id.message1);
			TextView textView2 = (TextView) dialog.findViewById(R.id.message2);
			final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			final int type = intent.getIntExtra(BluetoothDevice.EXTRA_PAIRING_VARIANT,
					BluetoothDevice.ERROR);
			switch (type) {
			case BluetoothDevice.PAIRING_VARIANT_PIN:
			case BluetoothDevice.PAIRING_VARIANT_PASSKEY:
				String text = context.getString(R.string.bluetooth_enter_passkey_msg,
						device.getName());
				textView1.setText(Html.fromHtml(text));
				text = context.getString(R.string.bluttooth_enter_the_same);
				textView2.setText(text);
				editText.setText(null);
				editText.setVisibility(View.VISIBLE);
				editText.setInputType(InputType.TYPE_CLASS_NUMBER);
				editText.setFilters(new InputFilter[] { new LengthFilter(6) });
				break;

			case BluetoothDevice.PAIRING_VARIANT_PASSKEY_CONFIRMATION:
				int passkey = intent.getIntExtra(BluetoothDevice.EXTRA_PAIRING_KEY,
						BluetoothDevice.ERROR);
				if (passkey == BluetoothDevice.ERROR) {
					Log.e(TAG, "Invalid Confirmation Passkey received, not showing any dialog");
					return;
				}
				editText.setVisibility(View.GONE);
				mPairingKey = String.format(Locale.US, "%06d", passkey);
				test = context.getString(R.string.bluetooth_confirm_passkey_msg, device.getName(),
						mPairingKey);
				textView1.setText(Html.fromHtml(test));
				textView2.setVisibility(View.GONE);
				break;

			case BluetoothDevice.PAIRING_VARIANT_CONSENT:
			case BluetoothDevice.PAIRING_VARIANT_OOB_CONSENT:
				test = context.getString(R.string.bluetooth_incoming_pairing_msg, device.getName());
				textView1.setText(Html.fromHtml(test));
				textView2.setVisibility(View.GONE);
				editText.setVisibility(View.GONE);
				break;

			case BluetoothDevice.PAIRING_VARIANT_DISPLAY_PASSKEY:
			case BluetoothDevice.PAIRING_VARIANT_DISPLAY_PIN:
				int pairingKey = intent.getIntExtra(BluetoothDevice.EXTRA_PAIRING_KEY,
						BluetoothDevice.ERROR);
				if (pairingKey == BluetoothDevice.ERROR) {
					Log.e(TAG,
							"Invalid Confirmation Passkey or PIN received, not showing any dialog");
					return;
				}
				if (type == BluetoothDevice.PAIRING_VARIANT_DISPLAY_PASSKEY) {
					mPairingKey = String.format("%06d", pairingKey);
				} else {
					mPairingKey = String.format("%04d", pairingKey);
				}
				test = context.getString(R.string.bluetooth_display_passkey_pin_msg,
						device.getName(), mPairingKey);
				textView1.setText(Html.fromHtml(test));
				textView2.setVisibility(View.GONE);
				editText.setVisibility(View.GONE);

				if (type == BluetoothDevice.PAIRING_VARIANT_DISPLAY_PASSKEY) {
					device.setPairingConfirmation(true);
				} else if (type == BluetoothDevice.PAIRING_VARIANT_DISPLAY_PIN) {
					byte[] pinBytes = BluetoothDevice.convertPinToBytes(mPairingKey);
					device.setPin(pinBytes);
				}
				break;

			default:
				Log.e(TAG, "Incorrect pairing type received, not showing any dialog");

			}

			Intent pairingIntent = new Intent();
			pairingIntent.setClass(context, BluetoothPairActivity.class);
			pairingIntent.putExtra(BluetoothDevice.EXTRA_DEVICE, device);
			pairingIntent.putExtra(BluetoothDevice.EXTRA_PAIRING_VARIANT, type);
			if (type == BluetoothDevice.PAIRING_VARIANT_PASSKEY_CONFIRMATION
					|| type == BluetoothDevice.PAIRING_VARIANT_DISPLAY_PASSKEY
					|| type == BluetoothDevice.PAIRING_VARIANT_DISPLAY_PIN) {
				int pairingKey = intent.getIntExtra(BluetoothDevice.EXTRA_PAIRING_KEY,
						BluetoothDevice.ERROR);
				pairingIntent.putExtra(BluetoothDevice.EXTRA_PAIRING_KEY, pairingKey);
			}
			pairingIntent.setAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
			pairingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			PowerManager powerManager = (PowerManager) context
					.getSystemService(Context.POWER_SERVICE);
			String deviceAddress = device != null ? device.getAddress() : null;
			if (powerManager.isScreenOn()) {
				// Since the screen is on and the BT-related activity is in the
				// foreground,
				// just open the dialog
				// context.startActivity(pairingIntent);

				Button button1 = (Button) dialog.findViewById(R.id.button1);
				button1.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View paramView) {
						// TODO Auto-generated method stub
						switch (type) {
						case BluetoothDevice.PAIRING_VARIANT_PIN:
							/*
							 * byte[] pinBytes = BluetoothDevice
							 * .convertPinToBytes(value); if (pinBytes == null)
							 * { return; } mDevice.setPin(pinBytes);
							 */
							String temp = null;
							if (editText != null) {
								temp = editText.getText().toString();
							}
							byte[] pinBytes = BluetoothDevice.convertPinToBytes(temp);
							if (pinBytes == null) {
								return;
							}
							device.setPin(pinBytes);
							break;

						case BluetoothDevice.PAIRING_VARIANT_PASSKEY:
							/*
							 * int passkey = Integer.parseInt(value);
							 * mDevice.setPasskey(passkey);
							 */
							String temp1 = null;
							if (editText != null) {
								temp1 = editText.getText().toString();
							}
							int passkey = Integer.parseInt(temp1);
							device.setPasskey(passkey);
							break;

						case BluetoothDevice.PAIRING_VARIANT_PASSKEY_CONFIRMATION:
						case BluetoothDevice.PAIRING_VARIANT_CONSENT:
							device.setPairingConfirmation(true);
							break;

						case BluetoothDevice.PAIRING_VARIANT_DISPLAY_PASSKEY:
						case BluetoothDevice.PAIRING_VARIANT_DISPLAY_PIN:
							// Do nothing.
							break;

						case BluetoothDevice.PAIRING_VARIANT_OOB_CONSENT:
							device.setRemoteOutOfBandData();
							break;

						default:
							Log.e(TAG, "Incorrect pairing type received");
						}
						dialog.dismiss();
					}

				});
				Button button2 = (Button) dialog.findViewById(R.id.button2);
				button2.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View paramView) {
						// TODO Auto-generated method stub
						device.cancelPairingUserInput();
						// dialog.dismiss();
					}

				});
				dialog.getWindow().setType((WindowManager.LayoutParams.TYPE_SYSTEM_ALERT));
				Window dialogWindow = dialog.getWindow();
				WindowManager.LayoutParams lp = dialogWindow.getAttributes();
				lp.width = 1025;
				lp.height = 338;
				dialogWindow.setAttributes(lp);
				dialog.show();
			} else {
				// Put up a notification that leads to the dialog
				Resources res = context.getResources();
				Notification.Builder builder = new Notification.Builder(context).setSmallIcon(
						android.R.drawable.stat_sys_data_bluetooth).setTicker(
						res.getString(R.string.bluetooth_notif_ticker));

				PendingIntent pending = PendingIntent.getActivity(context, 0, pairingIntent,
						PendingIntent.FLAG_ONE_SHOT);

				String name = intent.getStringExtra(BluetoothDevice.EXTRA_NAME);
				if (TextUtils.isEmpty(name)) {
					name = device != null ? device.getAliasName() : context
							.getString(android.R.string.unknownName);
				}

				builder.setContentTitle(res.getString(R.string.bluetooth_notif_title))
						.setContentText(res.getString(R.string.bluetooth_notif_message, name))
						.setContentIntent(pending).setAutoCancel(true)
						.setDefaults(Notification.DEFAULT_SOUND);

				NotificationManager manager = (NotificationManager) context
						.getSystemService(Context.NOTIFICATION_SERVICE);
				manager.notify(NOTIFICATION_ID, builder.getNotification());
			}

		} else if (action.equals(BluetoothDevice.ACTION_PAIRING_CANCEL)) {
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
				dialog = null;
			}
			// Remove the notification
			NotificationManager manager = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
			manager.cancel(NOTIFICATION_ID);
		} else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
			int bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE,
					BluetoothDevice.ERROR);
			if (bondState == BluetoothDevice.BOND_BONDED || bondState == BluetoothDevice.BOND_NONE) {
				if (dialog != null && dialog.isShowing()) {
					dialog.dismiss();
					dialog = null;
				}
			}
		}
	}
}
