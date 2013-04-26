package com.t3hh4xx0r.tweezee.billing;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BillingReceiver extends BroadcastReceiver {

	private static final String TAG = "BillingService";

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Log.i(TAG, "Received action: " + action);
		if (C.ACTION_PURCHASE_STATE_CHANGED.equals(action)) {
			String signedData = intent.getStringExtra(C.INAPP_SIGNED_DATA);
			String signature = intent.getStringExtra(C.INAPP_SIGNATURE);
			purchaseStateChanged(context, signedData, signature);
		} else if (C.ACTION_NOTIFY.equals(action)) {
			String notifyId = intent.getStringExtra(C.NOTIFICATION_ID);
			notify(context, notifyId);
		} else if (C.ACTION_RESPONSE_CODE.equals(action)) {
			long requestId = intent.getLongExtra(C.INAPP_REQUEST_ID, -1);
			int responseCodeIndex = intent.getIntExtra(C.INAPP_RESPONSE_CODE,
					C.ResponseCode.RESULT_ERROR.ordinal());
			checkResponseCode(context, requestId, responseCodeIndex);
		} else {
			Log.e(TAG, "unexpected action: " + action);
		}
	}

	private void purchaseStateChanged(Context context, String signedData,
			String signature) {
		Log.i(TAG, "purchaseStateChanged got signedData: " + signedData);
		Log.i(TAG, "purchaseStateChanged got signature: " + signature);
		BillingHelper.verifyPurchase(signedData, signature);
	}

	private void notify(Context context, String notifyId) {
		Log.i(TAG, "notify got id: " + notifyId);
		String[] notifyIds = { notifyId };
		BillingHelper.getPurchaseInformation(notifyIds);
	}

	private void checkResponseCode(Context context, long requestId,
			int responseCodeIndex) {
		Log.i(TAG, "checkResponseCode got requestId: " + requestId);
		Log.i(TAG,
				"checkResponseCode got responseCode: "
						+ C.ResponseCode.valueOf(responseCodeIndex));
	}
}