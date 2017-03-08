package com.example.isweixin.tc;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.example.isweixin.R;
import com.example.isweixin.tc1.ChatActivity;
import com.example.isweixin.tc1.SmssContentObserver;
import com.example.isweixin.tc3.Utils;
import com.example.isweixin.tc3.Utils.Person_Sms;



import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class SmsFragment extends Fragment{
	//通话记录的界面
	private View SmsView;
	//信息的列表
	private ListView m_smsslist;
	//信息列表的适配器
	public static SmsAdapter m_smsadapter;
	//短信息内容观察者
	private SmssContentObserver SmssCO;  
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		LayoutInflater inflater = getActivity().getLayoutInflater();
		SmsView = inflater.inflate(R.layout.c, null);
		
        IntentFilter filter = new IntentFilter();
        filter.addAction("huahua.action.UpdataSmssView");
        getActivity().registerReceiver(mReceiver , filter); 
        
        SmssCO = new SmssContentObserver(new Handler());  
        getActivity().getContentResolver().registerContentObserver(Uri.parse("content://sms/") , false, SmssCO);
		
		m_smsslist = (ListView)SmsView.findViewById(R.id.sms_list);
		m_smsadapter = new SmsAdapter(getActivity(), Utils.persons_sms);
		m_smsslist.setAdapter(m_smsadapter);
		m_smsslist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent(getActivity(),ChatActivity.class);
				
		        Bundle mBundle = new Bundle();  
		        
		        mBundle.putSerializable("chatperson", Utils.persons_sms.get(arg2));
		        intent.putExtras(mBundle); 
				startActivity(intent);
				
			}
		});
		
	}
	
	BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals("huahua.action.UpdataSmssView"))
			{
				m_smsadapter.updateListView(Utils.persons_sms);
			}
		}
    };
	
	public class SmsAdapter extends BaseAdapter{
		private LayoutInflater m_inflater;
		private ArrayList<Person_Sms> persons_sms;
    	private Context context;
		
        public SmsAdapter(Context context,
        		ArrayList<Person_Sms> persons_sms) {
    	    this.m_inflater = LayoutInflater.from(context);
    	    this.persons_sms = persons_sms;
    	    this.context = context;
        }
        
    	public void updateListView(ArrayList<Person_Sms> persons_sms){
    		this.persons_sms = persons_sms;
    		notifyDataSetChanged();
    	}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return persons_sms.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return persons_sms.get(arg0);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null)
			{
				 convertView = m_inflater.inflate(R.layout.sms_list_item, null);
			}
			
			 TextView sms_name = (TextView)convertView.findViewById(R.id.sms_name);
			 sms_name.setText(persons_sms.get(position).Name + 
					 "("+persons_sms.get(position).person_smss.size() + ")");
			 
			 TextView sms_content = (TextView)convertView.findViewById(R.id.sms_content);
			 sms_content.setText(persons_sms.get(position).person_smss.get(0).SMSContent);
			 
			 TextView sms_data = (TextView)convertView.findViewById(R.id.sms_date);
			 Date date = new Date(persons_sms.get(position).person_smss.get(0).SMSDate);
			 SimpleDateFormat sfd = new SimpleDateFormat("yyyy/MM/dd");
			 String time = sfd.format(date);
			 sms_data.setText(time);
			 
			 return convertView;
		}
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		ViewGroup p = (ViewGroup) SmsView.getParent(); 
        if (p != null) { 
            p.removeAllViewsInLayout(); 
        } 
        
		return SmsView;
	}

	@Override
	public void onDestroy() {
		getActivity().unregisterReceiver(mReceiver);
		super.onDestroy();
	}
}
