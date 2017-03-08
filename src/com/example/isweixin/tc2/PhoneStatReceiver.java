package com.example.isweixin.tc2;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

/*
 * 来电去电挂断通知
 */
public class PhoneStatReceiver  extends BroadcastReceiver{
	private Context context;
	@Override
	public void onReceive(Context context, Intent intent) {
		//去电
		if(intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL))
		{
			Log.i("huahua", "ACTION_NEW_OUTGOING_CALL");
		}
		else 
		{
			Log.i("huahua", "else: ACTION_NEW_OUTGOING_CALL");
		}
		
//		if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED"))
//		{
//			  Log.i("huahua", "android.provider.Telephony.SMS_RECEIVED");
//			StringBuilder sb = new StringBuilder();
//			 Bundle bundle = intent.getExtras();
//			 
//			 String PhoneNumber= null;
//			 String content = null;
//			 long data =0;
//             if (bundle != null) 
//             {
//                  Object[] pdus = (Object[]) bundle.get("pdus");
//                  SmsMessage[] msg = new SmsMessage[pdus.length];
//                  for (int i = 0; i < pdus.length; i++) 
//                  {
//                      msg[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
//                  }
//                  for (SmsMessage currMsg : msg) 
//                  {
//   	                        sb.append("您收到了来自:【");
//   	                       sb.append(currMsg.getDisplayOriginatingAddress());
//   	                        sb.append("】\n的信息，内容：");
//   	                       sb.append(currMsg.getDisplayMessageBody());
//   	                       
//   	                    PhoneNumber = currMsg.getDisplayOriginatingAddress();
//   	                 data = currMsg.getTimestampMillis();
//   	              content = currMsg.getDisplayMessageBody();
//               	  }
//                
//					ContentValues values = new ContentValues();
//					values.put("address", PhoneNumber);
//					values.put("body", content);
//					values.put("date", data);
//					values.put("type", 1);
//					Uri result = context.getContentResolver().insert(Uri
//							.parse("content://sms/"), values);
//             }
//		}

	}

}
