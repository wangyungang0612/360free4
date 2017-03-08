package com.example.isweixin.tc1;


import com.example.isweixin.tc3.Utils;

import android.database.ContentObserver;
import android.os.Handler;
import android.util.Log;

public class ContactsContentObserver extends ContentObserver{

	public ContactsContentObserver(Handler handler) {
		super(handler);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onChange(boolean selfChange) {
		Log.i("huahua", "联系人数据库发生了变化");
		Utils.getContacts();
	}

}
