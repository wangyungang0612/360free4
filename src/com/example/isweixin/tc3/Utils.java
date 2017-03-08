package com.example.isweixin.tc3;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.example.isweixin.tc.SmsFragment;
import com.example.isweixin.tc3.CallLogsFragment;
import com.example.isweixin.tc4.ContactsFragment;
import com.example.isweixin.tc4.PinyinUtils;


import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.RawContacts.Data;

public class Utils {
	public static Context m_context;
	
	/*
	 * 通话记录的数据
	 */
	public static class CallLogs {
		public String Name;      //名字
		public String Number;      //电话号码
		public String Data;  //日期
		public int Type;  //状态:来电,未接,拨出
	} 
	//所有通话记录数组
	public static ArrayList<CallLogs> calllogs = new ArrayList<CallLogs>();
	//根据日期的排序
	public static class ComparatorNum implements Comparator<CallLogs>{
		@Override
		public int compare(CallLogs lhs, CallLogs rhs) {
			
			String str1 = lhs.Data;
			String str2 = rhs.Data;
			return -str1.compareToIgnoreCase(str2);
		}
	}
	
	/*
	 *联系人的数据
	 */
	public static class Persons {
		public String ID;  //数据库中的标识ID
		public String Name;  //姓名
		public String PY;      //姓名拼音 
		public String Number;      //电话号码
		public String FisrtSpell;      //中文名首字母 
	} 
	//所有联系人数组
	public static ArrayList<Persons> persons = new ArrayList<Persons>();
	//过滤的联系人
	public static ArrayList<Persons> filterpersons = new ArrayList<Persons>();
	//根据姓名首字母的排序
	public static class ComparatorPY implements Comparator<Persons>{

		@Override
		public int compare(Persons lhs, Persons rhs) {
			String str1 = lhs.PY;
			String str2 = rhs.PY;
			return str1.compareToIgnoreCase(str2);
		}
	}
	
    
	/*
	 *短信息的数据
	 */
	public static class SMSs implements Serializable{
		public String SMSContent;      //短信内容
		public Long SMSDate;      //短信时间
		public Integer SMSType;      //短信状态
	} 
	
	//单个联系人的信息属性
	public static class Person_Sms implements Serializable {
		public String Name;  //姓名
		public String Number;      //电话号码
		public ArrayList<SMSs> person_smss= new ArrayList<SMSs>();   
	} 
	//单个联系人数组
	public static ArrayList<Person_Sms> persons_sms = new ArrayList<Person_Sms>();
	
	//初始化传入主Activity的上下文
	public static void init(Context context)
	{
		m_context = context;
	}
	
	//从数据库中读取短信息记录
	public static void getSmss()
	{
		new getSMSsTask().execute();
	}

	public static Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			if(msg.what == 0)//更新短信息
			{
				persons_sms.add((Person_Sms)msg.obj);  
    			if(SmsFragment.m_smsadapter != null)
    			{
    				SmsFragment.m_smsadapter.notifyDataSetChanged();
    			}
			}
			else 	if(msg.what == 1)//更新通话记录
			{
                calllogs.add((CallLogs)msg.obj);  
    			if(CallLogsFragment.m_calllogsadapter != null)
    			{
    				CallLogsFragment.m_calllogsadapter.notifyDataSetChanged();
    			}
			}
			else 	if(msg.what == 2)//更新联系人
			{
				persons.add((Persons)msg.obj);  
    			if(ContactsFragment.m_contactsadapter != null)
    			{
    				ContactsFragment.m_contactsadapter.notifyDataSetChanged();
    			}
			}
		}
		
	};
	
	public static class getSMSsTask extends AsyncTask<Void, Integer, Void>{
		@Override
		protected Void doInBackground(Void... params) {
			
	        ContentResolver contentResolver = m_context.getContentResolver();
	        // 获得所有联系人数据集的游标
	        Cursor cursor = contentResolver.query(Uri.parse("content://sms/"),null, null, null, null);
	        // 循环遍历
	        if (cursor.moveToFirst()) {
	                int index_Address = cursor.getColumnIndex("address");
	                int index_Body = cursor.getColumnIndex("body");
	                int index_Date = cursor.getColumnIndex("date");
	                int index_Type = cursor.getColumnIndex("type");
	               
	                while (cursor.moveToNext() ){
	                	boolean AddPersonFlag = true;
                		SMSs Sms = new SMSs();
                		Sms.SMSContent = cursor.getString(index_Body);
                		Sms.SMSDate = cursor.getLong(index_Date);
                        Sms.SMSType = cursor.getInt(index_Type);
                        
                        String Number = cursor.getString(index_Address);
                        if(Number != null)
                        {
                            if(Number.length() > 3 &&Number.substring(0,3).equals("+86"))
                            {
                            	Number = Number.substring(3);
                            }
                            for(int i=0;i<persons_sms.size();i++)
                            {
                                	if(Number.equals(persons_sms.get(i).Number))
                            	{
                            		AddPersonFlag = false;
                            		persons_sms.get(i).person_smss.add(Sms);
                            		break;
                            	}
                            }
                            
                            if(AddPersonFlag)
                            {
                            	Person_Sms  person_sms = new Person_Sms();
                            	person_sms.Number = Number;
                            	person_sms.Name = getPeopleNameFromPerson(cursor.getString(index_Address));
                        		if(person_sms.Name == null || person_sms.Name.equals("null"))
                        		{
                        			person_sms.Name = person_sms.Number;
                        		}
                        		
                            	person_sms.person_smss.add(Sms);
                            	
                              Message msg = mHandler.obtainMessage();
                              msg.what = 0;  
                              msg.obj = person_sms; 
                              mHandler.sendMessage(msg);
                              
    	                          try {
    								Thread.sleep(100);
    							} catch (InterruptedException e) {
    								// TODO Auto-generated catch block
    								e.printStackTrace();
    							}
                              
                            }
                        }
	                }
	                cursor.close();
	                
	        }
	        
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			Intent intent = new Intent("huahua.action.UpdataSmssView");
			m_context.sendBroadcast(intent);
		}
		
	}
	
    // 通过address手机号得到联系人的显示名字  
	public static String getPeopleNameFromPerson(String address){  
        if(address == null || address == ""){  
            return "null";  
        }  
          
        String strPerson = "null";  
        String[] projection = new String[] {Phone.DISPLAY_NAME, Phone.NUMBER};  
          
        Uri uri_Person = Uri.withAppendedPath(ContactsContract.CommonDataKinds.Phone.CONTENT_FILTER_URI, address);  // address 手机号过滤  
        Cursor cursor = m_context.getContentResolver().query(uri_Person, projection, null, null, null);  
          
        if(cursor.moveToFirst()){  
            int index_PeopleName = cursor.getColumnIndex(Phone.DISPLAY_NAME);  
            String strPeopleName = cursor.getString(index_PeopleName);  
            strPerson = strPeopleName;  
        }  
        cursor.close();  
          
        return strPerson;  
    } 
	
	//从数据库中读取通话记录
	public static void getCallLogs()
	{
		new getCallLogsTask().execute();
	}
	
	public static class getCallLogsTask extends AsyncTask<Void, Integer, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			calllogs.clear();
			
	        ContentResolver contentResolver = m_context.getContentResolver();
	        // 获得所有联系人数据集的游标
	        Cursor cursor = contentResolver.query(CallLog.Calls.CONTENT_URI ,null, null, null, null);
	        // 循环遍历
	        if (cursor.moveToFirst()) {
	                
	        		int NameColumn = cursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
	                int NumberColumn = cursor.getColumnIndex(CallLog.Calls.NUMBER);
	                int DataColumn = cursor.getColumnIndex(CallLog.Calls.DATE);
	                int TypeColumn = cursor.getColumnIndex(CallLog.Calls.TYPE);
	               

	                while (cursor.moveToNext()){
	                		CallLogs CallLog = new CallLogs();
	                        // 获得联系人姓名
	                		CallLog.Name = cursor.getString(NameColumn);
	                        CallLog.Number = cursor.getString(NumberColumn);
	                        CallLog.Data = cursor.getString(DataColumn);
	                        CallLog.Type = cursor.getInt(TypeColumn);

	                        Message msg = new Message();  
	                        msg.what = 1;  
	                        msg.obj = CallLog; 
	                        mHandler.sendMessage(msg);
	                }
	                cursor.close();
	        }

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// 根据日期为通话记录数组进行排序
			Collections.sort(calllogs, new ComparatorNum());
			
			Intent intent = new Intent("huahua.action.UpdataCallLogsView");
			m_context.sendBroadcast(intent);
		}
		
	}
	
	//从数据库中读取联系人
	public static void getContacts()
	{
		new getContactsTask().execute();
	}
	
	public static class getContactsTask extends AsyncTask<Void, Integer, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			
			persons.clear();

            ContentResolver contentResolver = m_context.getContentResolver();
            // 获得所有联系人数据集的游标
            Cursor cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI ,null, null, null, null);
            // 循环遍历
            if (cursor.moveToFirst()) {
                    
            		int idColumn = cursor.getColumnIndex(Data.RAW_CONTACT_ID);
                    int displayNameColumn = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                    int NumberColumn = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                    while (cursor.moveToNext())
                    {
                    		Persons person = new Persons();
                    		person.ID = cursor.getString(idColumn);
                            person.Name = cursor.getString(displayNameColumn);
                            person.PY = PinyinUtils.getPingYin(person.Name);
                            person.FisrtSpell = PinyinUtils.getFirstSpell(person.Name);
                            person.Number = cursor.getString(NumberColumn);

                            Message msg = new Message();  
                            msg.what = 2;  
                            msg.obj = person; 
                            mHandler.sendMessage(msg);
                    }
                    cursor.close();
            }
            
			return null;

		}

		@Override
		protected void onPostExecute(Void result) {
    		// 根据拼音为联系人数组进行排序
    		Collections.sort(persons, new ComparatorPY());
			
			Intent intent = new Intent("huahua.action.UpdataContactsView");
			m_context.sendBroadcast(intent);
		}
		
	}
	
	//往数据库中新增联系人
	public static void AddContact(String name, String number)
	{
		ContentValues values = new ContentValues(); 
        //首先向RawContacts.CONTENT_URI执行一个空值插入，目的是获取系统返回的rawContactId  
        Uri rawContactUri = m_context.getContentResolver().insert(RawContacts.CONTENT_URI, values); 
        long rawContactId = ContentUris.parseId(rawContactUri); 
        //往data表插入姓名数据 
        values.clear(); 
        values.put(Data.RAW_CONTACT_ID, rawContactId);  
        values.put(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);//内容类型 
        values.put(StructuredName.GIVEN_NAME, name); 
        m_context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
       
        //往data表插入电话数据 
        values.clear(); 
        values.put(Data.RAW_CONTACT_ID, rawContactId); 
        values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE); 
        values.put(Phone.NUMBER, number); 
        values.put(Phone.TYPE, Phone.TYPE_MOBILE); 
        m_context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values); 
	}
	
	//更改数据库中联系人
	public static void ChangeContact(String name, String number, String ContactId)
	{
		ContentValues values = new ContentValues();
        values.put(StructuredName.GIVEN_NAME, name); 
        values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, number);
        m_context.getContentResolver().update(ContactsContract.Data.CONTENT_URI,
				values, 
				Data.RAW_CONTACT_ID + "=" + ContactId,
				null);
	}
	
	//删除联系人
	public static void DeleteContact(String ContactId)
	{
//        Uri uri = Uri.withAppendedPath(ContactsContract.CommonDataKinds.Phone.CONTENT_URI , ContactId);   
//		m_context.getContentResolver().delete(uri, null, null);
		m_context.getContentResolver().delete(ContactsContract.Data.CONTENT_URI,
				Data.RAW_CONTACT_ID + "=" + ContactId,
				null);
	}
	
	//联系人去重
	public static void DeleteSameContacts()
	{
		for(int i=1;i<persons.size();i++)
		{
			if(persons.get(i).Name.equals(persons.get(i-1).Name)
			&&persons.get(i).Number.equals(persons.get(i-1).Number))
			{
				DeleteContact(persons.get(i).ID);
			}
		}
	}
	
	//根据关键字过滤联系人
	public static void filterContacts(String filterStr){
		new filterContactsTask().execute(filterStr);
	}
	
	public static class filterContactsTask extends AsyncTask<String, Integer, Void>{

		@Override
		protected Void doInBackground(String... params) {
			
			filterpersons.clear();

	        //遍历所有联系人数组,筛选出包含关键字的联系人
	        for (int i = 0; i < persons.size(); i++) {  
	            //过滤的条件
	              if (isStrInString(persons.get(i).Number,params[0])
	            		||isStrInString(persons.get(i).PY,params[0])
	            		||persons.get(i).Name.contains(params[0])
	            		||isStrInString(persons.get(i).FisrtSpell,params[0])){
	                //将筛选出来的联系人重新添加到filterpersons数组中
	            	Persons filterperson = new Persons();
	            	filterperson.ID = persons.get(i).ID;
	            	filterperson.Name = persons.get(i).Name;
	            	filterperson.PY = persons.get(i).PY;
	            	filterperson.Number = persons.get(i).Number;
	            	filterperson.FisrtSpell = persons.get(i).FisrtSpell;
	            	filterpersons.add(filterperson);
	            }  
	        }  
			return null;

		}

		@Override
		protected void onPostExecute(Void result) {
			Intent intent = new Intent("huahua.action.UpdataContactsView");
			m_context.sendBroadcast(intent);
		}

	}
	
	//字符串中是否包含关键字
	public static boolean isStrInString(String bigStr,String smallStr){
		  if(bigStr.toUpperCase().indexOf(smallStr.toUpperCase())>-1){
			  return true;
		  }else{
			  return false;
		  }
	}
	
	//往短信数据库中新增发送的短信
	public static void AddSms(String Number,String Content,Long Date,int Type)
	{
		ContentValues values = new ContentValues();
		values.put("address", Number);
		values.put("body", Content);
		values.put("date", Date);
		values.put("type", Type);
		Uri result = m_context.getContentResolver().insert(Uri.parse("content://sms/"), values);
	}
	
	
}
