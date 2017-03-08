package com.example.isweixin.tc3;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import com.example.isweixin.R;
import com.example.isweixin.tc1.CallLogsContentObserver;
import com.example.isweixin.tc3.Utils.CallLogs;




import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class CallLogsFragment extends Fragment{
	//通话记录的界面
	private View CallLogView;
	//通话记录的列表
	private ListView m_calllogslist;
	//通话记录列表的适配器
	public static CallLogsAdapter m_calllogsadapter;
	//通话记录内容观察者
	private CallLogsContentObserver CallLogsCO;  
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		LayoutInflater inflater = getActivity().getLayoutInflater();
		CallLogView = inflater.inflate(R.layout.a, null);
		
        IntentFilter filter = new IntentFilter();
        filter.addAction("huahua.action.UpdataCallLogsView");
        getActivity().registerReceiver(mReceiver , filter); 
        
        CallLogsCO = new CallLogsContentObserver(new Handler());  
        getActivity().getContentResolver().registerContentObserver(CallLog.Calls.CONTENT_URI , false, CallLogsCO); 
		
		//通话记录列表
		m_calllogslist = (ListView)CallLogView.findViewById(R.id.calllogs_list);
		m_calllogsadapter = new CallLogsAdapter(getActivity(), Utils.calllogs);
		m_calllogslist.setAdapter(m_calllogsadapter);

	}
	
	BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals("huahua.action.UpdataCallLogsView"))
			{
				m_calllogsadapter.updateListView(Utils.calllogs);
			}
		}
    };
	
	public class CallLogsAdapter extends BaseAdapter{
		private LayoutInflater m_inflater;
		private ArrayList<CallLogs> calllogs;
    	private Context context;
		
        public CallLogsAdapter(Context context,
        		ArrayList<CallLogs> calllogs) {
    	    this.m_inflater = LayoutInflater.from(context);
    	    this.calllogs = calllogs;
    	    this.context = context;
        }
        
    	//当联系人列表数据发生变化时,用此方法来更新列表
    	public void updateListView(ArrayList<CallLogs> calllogs){
    		this.calllogs = calllogs;
    		notifyDataSetChanged();
    	}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return calllogs.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return calllogs.get(arg0);
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
				convertView = m_inflater.inflate(R.layout.calllogs_list_item, null);
			}
			
			if(calllogs.isEmpty())
			{
				return convertView;
			}
			 
			 //通话记录的姓名
			 TextView name = (TextView)convertView.findViewById(R.id.calllog_name);
			 name.setText(calllogs.get(position).Name);
			 
			if(calllogs.get(position).Name == null){
				name.setText("未知号码");
			}else{
				name.setText(calllogs.get(position).Name);
			}
			 
			 //通话记录的电话状态
			 TextView Type = (TextView)convertView.findViewById(R.id.calllog_type);
			 if(calllogs.get(position).Type == CallLog.Calls.INCOMING_TYPE)
			 {
				 Type.setText("已接来电");
				 Type.setTextColor(Color.rgb(0, 0, 255));
			 }
			 else if(calllogs.get(position).Type == CallLog.Calls.OUTGOING_TYPE)
			 {
				 Type.setText("拨出号码");
				 Type.setTextColor(Color.rgb(0, 150, 0));
			 }
			 else if(calllogs.get(position).Type == CallLog.Calls.MISSED_TYPE)
			 {
				 Type.setText("未接来电");
				 Type.setTextColor(Color.rgb(255, 0, 0));
			 }
			 
			 //通话记录的号码
			 TextView number = (TextView)convertView.findViewById(R.id.calllog_number);
			 number.setText(calllogs.get(position).Number);
			 
			 //通话记录的日期
			 TextView data = (TextView)convertView.findViewById(R.id.calllog_data);
			 
			 Date date2 = new Date(Long.parseLong(calllogs.get(position).Data));
			 SimpleDateFormat sfd = new SimpleDateFormat("yyyy/MM/dd");
			 String time = sfd.format(date2);
			 data.setText(time);
			 
			Button dialBtn = (Button)convertView.findViewById(R.id.calllog_dial);
			dialBtn.setTag(calllogs.get(position).Number);
			dialBtn.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
						Intent  intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel://" + (String)arg0.getTag()));
						startActivity(intent);
				}
			});
			 
			 return convertView;
		}
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		ViewGroup p = (ViewGroup) CallLogView.getParent(); 
        if (p != null) { 
            p.removeAllViewsInLayout(); 
        } 
        
		return CallLogView;
	}

	@Override
	public void onDestroy() {
		getActivity().unregisterReceiver(mReceiver);
		super.onDestroy();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

}
