package com.example.isweixin.tc1;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.example.isweixin.R;
import com.example.isweixin.tc3.Utils;
import com.example.isweixin.tc3.Utils.Person_Sms;
import com.example.isweixin.tc3.Utils.SMSs;



import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChatActivity extends Activity implements OnClickListener{
	//�������������
	private TextView m_PersonName;
	//������Ϣ�İ�ť
	private Button m_SendBtn;
	//���ذ�ť
	private Button m_BackBtn;
	//���а�ť
	private Button m_DialBtn;
	//�༭��Ϣ��
	private EditText m_MsgEditText;
	//�����¼�б�
	private ListView m_ChatLogList;
	//�����¼�б��������
	private ChatLogAdapter m_ChatLogAdapter;
	//�������
	Person_Sms m_chat_person;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);  
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		setContentView(R.layout.chat_activtity);
		
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(mReceiver , filter); 
		
		m_chat_person = (Person_Sms)getIntent().getSerializableExtra("chatperson");
		Collections.reverse(m_chat_person.person_smss);
		
		m_PersonName = (TextView)findViewById(R.id.text_chat_name);
		m_PersonName.setText(m_chat_person.Name);
		m_SendBtn = (Button) findViewById(R.id.btn_chat_send);
		m_SendBtn.setOnClickListener(this);
		m_BackBtn = (Button) findViewById(R.id.btn_chat_back);
		m_BackBtn.setOnClickListener(this);
		m_DialBtn = (Button) findViewById(R.id.btn_chat_dial);
		m_DialBtn.setOnClickListener(this);
		m_MsgEditText = (EditText) findViewById(R.id.et_chat_msg);
		
		m_ChatLogList = (ListView) findViewById(R.id.chat_list);
		m_ChatLogAdapter = new ChatLogAdapter();
		m_ChatLogList.setAdapter(m_ChatLogAdapter);
		m_ChatLogList.setSelection(m_ChatLogAdapter.getCount() - 1);
	}
	
	BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			//�����Ϣ��ж������ͨѶ¼������²��յ���,������ʱ�����о���
			if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED"))
			{
				StringBuilder sb = new StringBuilder();
				Bundle bundle = intent.getExtras();
					 
				 SMSs new_sm =  new SMSs();
				 new_sm.SMSContent = "";
	             if (bundle != null) 
	             {
	                  Object[] pdus = (Object[]) bundle.get("pdus");
	                  SmsMessage[] msg = new SmsMessage[pdus.length];
	                  for (int i = 0; i < pdus.length; i++) 
	                  {
	                      msg[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
	                  }
	                  for (SmsMessage currMsg : msg) 
	                  {
	                	  m_chat_person.Number = currMsg.getDisplayOriginatingAddress();
	                	  new_sm.SMSDate = currMsg.getTimestampMillis();
	                	  new_sm.SMSContent = new_sm.SMSContent + currMsg.getDisplayMessageBody();
	                	  new_sm.SMSType = 1;
	               	  }
	                  
	                  	m_chat_person.person_smss.add(new_sm);
	                  	m_ChatLogAdapter.notifyDataSetChanged();
						m_ChatLogList.setSelection(m_ChatLogList.getCount() - 1);
	                
//						ContentValues values = new ContentValues();
//						values.put("address", m_chat_person.Number);
//						values.put("body", new_sm.SMSContent);
//						values.put("date", new_sm.SMSDate);
//						values.put("type", new_sm.SMSType );
//						Uri result = context.getContentResolver().insert(Uri
//								.parse("content://sms/"), values);
	             }
			}
		}
    };

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.btn_chat_send)
		{
				if(!"".equals(m_MsgEditText.getText().toString().trim()))
				{  
					SmsManager smsManager = SmsManager.getDefault();
		             if(m_MsgEditText.getText().toString().length() > 70) 
		             {
		                 List<String> contents = smsManager.divideMessage(m_MsgEditText.getText().toString());
		                 for(String sms : contents) 
		                 {
		                     smsManager.sendTextMessage(m_chat_person.Number, null, sms, null, null);
		                 }
		             } 
		             else 
		             {
		              smsManager.sendTextMessage(m_chat_person.Number, null, m_MsgEditText.getText().toString(), null, null);
		             }
		             
		             SMSs new_sm =  new SMSs();
		             new_sm.SMSContent = m_MsgEditText.getText().toString();
		             new_sm.SMSDate = System.currentTimeMillis();
		             new_sm.SMSType = 2;
		             
		             m_chat_person.person_smss.add(new_sm);
		             
		             Utils.AddSms(m_chat_person.Number, new_sm.SMSContent, new_sm.SMSDate, new_sm.SMSType);
		             
					m_ChatLogAdapter.notifyDataSetChanged();

					m_MsgEditText.setText("");// ��ձ༭������

					m_ChatLogList.setSelection(m_ChatLogList.getCount() - 1);
				}
		}
		else if(v.getId() == R.id.btn_chat_back)
		{
			finish();
		}
		else if(v.getId() == R.id.btn_chat_dial)
		{
			Intent  intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel://" + m_chat_person.Number));
			startActivity(intent);
		}
	}
	
	private class ChatLogAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return m_chat_person.person_smss.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return m_chat_person.person_smss.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub		

			if (m_chat_person.person_smss.get(position).SMSType == 1) 
			{
				convertView = LayoutInflater.from(ChatActivity.this).inflate(
						R.layout.chat_list_item_receive, null);
			} 
			else if(m_chat_person.person_smss.get(position).SMSType == 2)
			{
				convertView = LayoutInflater.from(ChatActivity.this).inflate(
						R.layout.chat_list_item_send, null);
			}

			TextView tvSendTime = (TextView) convertView
					.findViewById(R.id.tv_sendtime);
			TextView tvContent = (TextView) convertView
					.findViewById(R.id.tv_chatcontent);

			Date date = new Date(m_chat_person.person_smss.get(position).SMSDate);
			SimpleDateFormat sfd = new SimpleDateFormat("yyyy/MM/dd");
			String time = sfd.format(date);
			tvSendTime.setText(time);
			tvContent.setText(m_chat_person.person_smss.get(position).SMSContent);
			return convertView;
		}
	}

}
