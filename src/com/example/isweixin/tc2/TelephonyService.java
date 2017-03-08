package com.example.isweixin.tc2;

import java.util.Collections;

import com.example.isweixin.tc4.ContactsFragment;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.provider.CallLog;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class TelephonyService extends Service{
	 private TelephonyManager telephonyManager;  
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		//取得TELEPHONY服务  
        telephonyManager=(TelephonyManager)super.getSystemService(Context.TELEPHONY_SERVICE);  
        
        telephonyManager.listen(new MyPhoneStateListener(),  
                PhoneStateListener.LISTEN_CALL_STATE);  
		super.onCreate();
	}
	
	private class MyPhoneStateListener extends PhoneStateListener{

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			if(state == TelephonyManager.CALL_STATE_IDLE)//空闲
			{
				Log.i("huahua", "CALL_STATE_IDLE");
//				Utils.getCallLogs();
			}
			else if(state == TelephonyManager.CALL_STATE_RINGING)//来电
			{
				Log.i("huahua", "CALL_STATE_RINGING");
			}
			else if(state == TelephonyManager.CALL_STATE_OFFHOOK)//去电,通话中
			{
				Log.i("huahua", "CALL_STATE_OFFHOOK");
			}
		}
		
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}

}
