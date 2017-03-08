package com.example.isweixin;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.example.isweixin.tc.SmsFragment.SmsAdapter;
import com.example.isweixin.tc1.CallLogsContentObserver;
import com.example.isweixin.tc1.ChatActivity;
import com.example.isweixin.tc1.ContactsContentObserver;
import com.example.isweixin.tc1.SmssContentObserver;
import com.example.isweixin.tc3.Utils;
import com.example.isweixin.tc3.CallLogsFragment.CallLogsAdapter;
import com.example.isweixin.tc3.Utils.CallLogs;
import com.example.isweixin.tc3.Utils.Person_Sms;
import com.example.isweixin.tc3.Utils.Persons;
import com.example.isweixin.tc4.AddContactsActivity;
import com.example.isweixin.tc4.ContactsFragment.ContactsAdapter;
import com.example.isweixin.use.CurrentWeatherReportActivity;
import com.example.isweixin.use.FutureWeatherReportActivity;
import com.example.isweixin.use.LocationModeSourceActivity;
import com.example.tctc1002.AlphabetScrollBar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class MainActivity extends Activity implements OnViewChangeListener,
		OnClickListener {
	private MyScrollLayout mScrollLayout;
	private LinearLayout[] mImageViews;
	private int mViewCount;
	private int mCurSel;
	// private ImageView set;
	// private ImageView add;

	private TextView liaotian;
	private TextView faxian;
	private TextView tongxunlu;
	private TextView usecenter;
	private Button button, button2, button3, bohao_btn;

	private boolean isOpen = false;
	// 联系人列表的界面
	// private View ContactsView;
	// 字母列视图View
	private AlphabetScrollBar m_asb;
	// 显示选中的字母
	private TextView m_letterNotice;
	// 联系人的列表
	private ListView m_contactslist;
	// 联系人列表的适配器
	public static ContactsAdapter m_contactsadapter;
	// 搜索过滤联系人EditText
	private EditText m_FilterEditText;
	// 没有匹配联系人时显示的TextView
	private TextView m_listEmptyText;
	// 新增联系人按钮
	private Button m_AddContactBtn;
	// 去重联系人按钮
	private Button m_RemoveSameContactBtn;
	// 最上面的layout
	private FrameLayout m_topcontactslayout;
	// 联系人内容观察者
	private ContactsContentObserver ContactsCO;
	// 选中的联系人名字
	private String ChooseContactName;
	// 选中的联系人号码
	private String ChooseContactNumber;
	// 选中的联系人ID
	private String ChooseContactID;
	// 加载对话框
	ProgressDialog m_dialogLoading;
	// 通话记录的界面
	// private View SmsView;
	// 信息的列表
	private ListView m_smsslist;
	// 信息列表的适配器
	public static SmsAdapter m_smsadapter;
	// 短信息内容观察者
	private SmssContentObserver SmssCO;
	// 通话记录的界面
	// private View CallLogView;
	// 通话记录的列表
	private ListView m_calllogslist;
	// 通话记录列表的适配器
	public static CallLogsAdapter m_calllogsadapter;
	// 通话记录内容观察者
	private CallLogsContentObserver CallLogsCO;
	// 拨号键盘的界面
	// private View DialView;
	// 号码编辑框
	private EditText EditNum;
	// 退格按键
	private Button BtnBackSpace;
	private LinearLayout jianpan;

	// private ListView listview1;
	// private ListView listview2;

	// 自定义的弹出框类
	SelectPicPopupWindow menuWindow; // 弹出框
	SelectAddPopupWindow menuWindow2; // 弹出框

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		init();
		Utils.init(this);
		Utils.getContacts();
		Utils.getCallLogs();
		Utils.getSmss();
		jianpan = (LinearLayout) findViewById(R.id.shuzijianpan);

		button = (Button) findViewById(R.id.button1);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.this,
						LocationModeSourceActivity.class);
				MainActivity.this.startActivity(intent);
			}
		});
		button2 = (Button) findViewById(R.id.button2);
		button2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.this,
						CurrentWeatherReportActivity.class);
				MainActivity.this.startActivity(intent);
			}
		});
		button3 = (Button) findViewById(R.id.button3);
		button3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.this,
						FutureWeatherReportActivity.class);
				MainActivity.this.startActivity(intent);
			}
		});
		bohao_btn = (Button) findViewById(R.id.bohao_btn);
		bohao_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (jianpan.getVisibility() != 0) {
					jianpan.setVisibility(View.VISIBLE);//键盘可见
				} else {

					jianpan.setVisibility(View.GONE);//键盘隐藏
				}
			}
		});
		// LayoutInflater inflater = MainActivity.this.getLayoutInflater();
		// ContactsView = inflater.inflate(R.layout.b, null);

		IntentFilter filter = new IntentFilter();
		filter.addAction("huahua.action.UpdataContactsView");
		MainActivity.this.registerReceiver(mReceiver, filter);

		ContactsCO = new ContactsContentObserver(new Handler());
		MainActivity.this.getContentResolver().registerContentObserver(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, false,
				ContactsCO);

		// 得到字母列的对象,并设置触摸响应监听器
		m_asb = (AlphabetScrollBar) findViewById(R.id.alphabetscrollbar);
		m_asb.setOnTouchBarListener(new ScrollBarListener());
		m_letterNotice = (TextView) findViewById(R.id.pb_letter_notice);
		m_asb.setTextView(m_letterNotice);

		// 得到联系人列表,并设置适配器
		m_contactslist = (ListView) findViewById(R.id.pb_listvew);
		m_contactsadapter = new ContactsAdapter(MainActivity.this,
				Utils.persons);
		m_contactslist.setAdapter(m_contactsadapter);
		// 联系人列表长按监听
		m_contactslist
				.setOnItemLongClickListener(new OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> arg0,
							View arg1, int arg2, long arg3) {

						Vibrator vib = (Vibrator) MainActivity.this
								.getSystemService(Service.VIBRATOR_SERVICE);
						vib.vibrate(50);

						if (m_topcontactslayout.getVisibility() == View.VISIBLE) {
							ChooseContactName = Utils.persons.get(arg2).Name;
							ChooseContactNumber = Utils.persons.get(arg2).Number;
							ChooseContactID = Utils.persons.get(arg2).ID;
						} else {
							ChooseContactName = Utils.filterpersons.get(arg2).Name;
							ChooseContactNumber = Utils.filterpersons.get(arg2).Number;
							ChooseContactID = Utils.filterpersons.get(arg2).ID;
						}

						final Person_Sms personsms = new Person_Sms();
						personsms.Name = ChooseContactName;
						personsms.Number = ChooseContactNumber;
						for (int i = 0; i < Utils.persons_sms.size(); i++) {
							if (personsms.Name.equals(Utils.persons_sms.get(i).Name)) {
								personsms.person_smss = Utils.persons_sms
										.get(i).person_smss;
								break;
							}
						}

						AlertDialog ListDialog = new AlertDialog.Builder(
								MainActivity.this)
								.setTitle(ChooseContactName)
								.setItems(
										new String[] { "拨号", "短信", "删除联系人",
												"编辑联系人" },
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												if (which == 0) {
													Intent intent = new Intent(
															Intent.ACTION_CALL,
															Uri.parse("tel://"
																	+ ChooseContactNumber));
													startActivity(intent);
												} else if (which == 1) {
													Intent intent = new Intent(
															MainActivity.this,
															ChatActivity.class);
													Bundle mBundle = new Bundle();
													mBundle.putSerializable(
															"chatperson",
															personsms);
													intent.putExtras(mBundle);
													startActivity(intent);
												} else if (which == 2) {
													AlertDialog DeleteDialog = new AlertDialog.Builder(
															MainActivity.this)
															.setTitle("删除")
															.setMessage(
																	"删除联系人"
																			+ ChooseContactName
																			+ "?")
															.setPositiveButton(
																	"确定",
																	new DialogInterface.OnClickListener() {

																		@Override
																		public void onClick(
																				DialogInterface dialog,
																				int which) {
																			// 删除联系人操作,放在线程中处理
																			new DeleteContactTask()
																					.execute();
																		}
																	})
															.setNegativeButton(
																	"取消",
																	new DialogInterface.OnClickListener() {

																		@Override
																		public void onClick(
																				DialogInterface dialog,
																				int which) {

																		}
																	}).create();
													DeleteDialog.show();
												} else if (which == 3) {
													Bundle bundle = new Bundle();
													bundle.putInt("tpye", 1);
													bundle.putString("id",
															ChooseContactID);
													bundle.putString("name",
															ChooseContactName);
													bundle.putString("number",
															ChooseContactNumber);

													Intent intent = new Intent(
															MainActivity.this,
															AddContactsActivity.class);
													intent.putExtra("person",
															bundle);
													startActivity(intent);
												}
											}
										}).create();
						ListDialog.show();

						return false;
					}
				});
		m_listEmptyText = (TextView) findViewById(R.id.nocontacts_notice);

		// 新增联系人
		m_AddContactBtn = (Button) findViewById(R.id.add_contacts);
		m_AddContactBtn.setOnClickListener(new BtnClick());
		// 去重联系人
		m_RemoveSameContactBtn = (Button) findViewById(R.id.remove_same_contacts);
		m_RemoveSameContactBtn.setOnClickListener(new BtnClick());

		m_topcontactslayout = (FrameLayout) findViewById(R.id.top_contacts_layout);

		// 初始化搜索编辑框,设置文本改变时的监听器
		m_FilterEditText = (EditText) findViewById(R.id.pb_search_edit);
		m_FilterEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

				if (!"".equals(s.toString().trim())) {
					// 根据编辑框值过滤联系人并更新联系列表
					Utils.filterContacts(s.toString().trim());
					m_asb.setVisibility(View.GONE);
					m_topcontactslayout.setVisibility(View.GONE);
				} else {
					m_topcontactslayout.setVisibility(View.VISIBLE);
					m_asb.setVisibility(View.VISIBLE);
					m_contactsadapter.updateListView(Utils.persons);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
		IntentFilter filter2 = new IntentFilter();
		filter2.addAction("huahua.action.UpdataSmssView");
		MainActivity.this.registerReceiver(mReceiver2, filter2);

		SmssCO = new SmssContentObserver(new Handler());
		MainActivity.this.getContentResolver().registerContentObserver(
				Uri.parse("content://sms/"), false, SmssCO);

		m_smsslist = (ListView) findViewById(R.id.sms_list);
		m_smsadapter = new SmsAdapter(MainActivity.this, Utils.persons_sms);
		m_smsslist.setAdapter(m_smsadapter);
		m_smsslist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent(MainActivity.this,
						ChatActivity.class);

				Bundle mBundle = new Bundle();

				mBundle.putSerializable("chatperson",
						Utils.persons_sms.get(arg2));
				intent.putExtras(mBundle);
				startActivity(intent);

			}
		});
		IntentFilter filter3 = new IntentFilter();
		filter.addAction("huahua.action.UpdataCallLogsView");
		MainActivity.this.registerReceiver(mReceiver3, filter3);

		CallLogsCO = new CallLogsContentObserver(new Handler());
		MainActivity.this.getContentResolver().registerContentObserver(
				CallLog.Calls.CONTENT_URI, false, CallLogsCO);

		// 通话记录列表
		m_calllogslist = (ListView) findViewById(R.id.calllogs_list);
		m_calllogsadapter = new CallLogsAdapter(MainActivity.this,
				Utils.calllogs);
		m_calllogslist.setAdapter(m_calllogsadapter);
		
		EditNum = (EditText) findViewById(R.id.shuzi_edit);
		// EditNum.setInputType(InputType.TYPE_NULL);

		// edittext不显示软键盘,要显示光标
		if (android.os.Build.VERSION.SDK_INT <= 10) {
			EditNum.setInputType(InputType.TYPE_NULL);
		} else {
			MainActivity.this.getWindow().setSoftInputMode(
					WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
			try {
				Class<EditText> cls = EditText.class;
				Method setSoftInputShownOnFocus;
				setSoftInputShownOnFocus = cls.getMethod(
						"setShowSoftInputOnFocus", boolean.class);
				setSoftInputShownOnFocus.setAccessible(true);
				setSoftInputShownOnFocus.invoke(EditNum, false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		findViewById(R.id.shuzi1).setOnClickListener(new BtnClick2());
		findViewById(R.id.shuzi2).setOnClickListener(new BtnClick2());
		findViewById(R.id.shuzi3).setOnClickListener(new BtnClick2());
		findViewById(R.id.shuzi4).setOnClickListener(new BtnClick2());
		findViewById(R.id.shuzi5).setOnClickListener(new BtnClick2());
		findViewById(R.id.shuzi6).setOnClickListener(new BtnClick2());
		findViewById(R.id.shuzi7).setOnClickListener(new BtnClick2());
		findViewById(R.id.shuzi8).setOnClickListener(new BtnClick2());
		findViewById(R.id.shuzi9).setOnClickListener(new BtnClick2());
		findViewById(R.id.shuzi0).setOnClickListener(new BtnClick2());
		findViewById(R.id.shuzi_xing).setOnClickListener(new BtnClick2());
		findViewById(R.id.shuzi_jing).setOnClickListener(new BtnClick2());
		findViewById(R.id.dadianhua).setOnClickListener(new BtnClick2());

		BtnBackSpace = (Button) findViewById(R.id.shuzi_shanchubtn);
		BtnBackSpace.setOnClickListener(new BtnClick2());
		BtnBackSpace.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				Vibrator vib = (Vibrator) MainActivity.this
						.getSystemService(Service.VIBRATOR_SERVICE);
				vib.vibrate(50);

				EditNum.setText("");
				return false;
			}
		});
	}

	private class BtnClick2 implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.shuzi1) {
				keyPressed(KeyEvent.KEYCODE_1);
			} else if (v.getId() == R.id.shuzi2) {
				keyPressed(KeyEvent.KEYCODE_2);
			} else if (v.getId() == R.id.shuzi3) {
				keyPressed(KeyEvent.KEYCODE_3);
			} else if (v.getId() == R.id.shuzi4) {
				keyPressed(KeyEvent.KEYCODE_4);
			} else if (v.getId() == R.id.shuzi5) {
				keyPressed(KeyEvent.KEYCODE_5);
			} else if (v.getId() == R.id.shuzi6) {
				keyPressed(KeyEvent.KEYCODE_6);
			} else if (v.getId() == R.id.shuzi7) {
				keyPressed(KeyEvent.KEYCODE_7);
			} else if (v.getId() == R.id.shuzi8) {
				keyPressed(KeyEvent.KEYCODE_8);
			} else if (v.getId() == R.id.shuzi9) {
				keyPressed(KeyEvent.KEYCODE_9);
			} else if (v.getId() == R.id.shuzi0) {
				keyPressed(KeyEvent.KEYCODE_0);
			} else if (v.getId() == R.id.shuzi_xing) {
				keyPressed(KeyEvent.KEYCODE_STAR);
			} else if (v.getId() == R.id.shuzi_jing) {
				keyPressed(KeyEvent.KEYCODE_POUND);
			} else if (v.getId() == R.id.dadianhua) {
				if (EditNum.length() != 0) {
					Intent intent = new Intent(Intent.ACTION_CALL,
							Uri.parse("tel://" + EditNum.getText().toString()));
					startActivity(intent);
				} else {
					Toast.makeText(MainActivity.this, "请输入号码",
							Toast.LENGTH_SHORT).show();
				}
			} else if (v.getId() == R.id.shuzi_shanchubtn) {
				keyPressed(KeyEvent.KEYCODE_DEL);
			}
		}
	}

	private void keyPressed(int keyCode) {
		KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, keyCode);
		EditNum.onKeyDown(keyCode, event);
	}

	BroadcastReceiver mReceiver3 = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals("huahua.action.UpdataCallLogsView")) {
				m_calllogsadapter.updateListView(Utils.calllogs);
			}
		}
	};

	public class CallLogsAdapter extends BaseAdapter {
		private LayoutInflater m_inflater;
		private ArrayList<CallLogs> calllogs;
		private Context context;

		public CallLogsAdapter(Context context, ArrayList<CallLogs> calllogs) {
			this.m_inflater = LayoutInflater.from(context);
			this.calllogs = calllogs;
			this.context = context;
		}

		// 当联系人列表数据发生变化时,用此方法来更新列表
		public void updateListView(ArrayList<CallLogs> calllogs) {
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
			if (convertView == null) {
				convertView = m_inflater.inflate(R.layout.calllogs_list_item,
						null);
			}

			if (calllogs.isEmpty()) {
				return convertView;
			}

			// 通话记录的姓名
			TextView name = (TextView) convertView
					.findViewById(R.id.calllog_name);
			name.setText(calllogs.get(position).Name);

			if (calllogs.get(position).Name == null) {
				name.setText("未知号码");
			} else {
				name.setText(calllogs.get(position).Name);
			}

			// 通话记录的电话状态
			TextView Type = (TextView) convertView
					.findViewById(R.id.calllog_type);
			if (calllogs.get(position).Type == CallLog.Calls.INCOMING_TYPE) {
				Type.setText("已接来电");
				Type.setTextColor(Color.rgb(0, 0, 255));
			} else if (calllogs.get(position).Type == CallLog.Calls.OUTGOING_TYPE) {
				Type.setText("拨出号码");
				Type.setTextColor(Color.rgb(0, 150, 0));
			} else if (calllogs.get(position).Type == CallLog.Calls.MISSED_TYPE) {
				Type.setText("未接来电");
				Type.setTextColor(Color.rgb(255, 0, 0));
			}

			// 通话记录的号码
			TextView number = (TextView) convertView
					.findViewById(R.id.calllog_number);
			number.setText(calllogs.get(position).Number);

			// 通话记录的日期
			TextView data = (TextView) convertView
					.findViewById(R.id.calllog_data);

			Date date2 = new Date(Long.parseLong(calllogs.get(position).Data));
			SimpleDateFormat sfd = new SimpleDateFormat("yyyy/MM/dd");
			String time = sfd.format(date2);
			data.setText(time);

			Button dialBtn = (Button) convertView
					.findViewById(R.id.calllog_dial);
			dialBtn.setTag(calllogs.get(position).Number);
			dialBtn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(Intent.ACTION_CALL, Uri
							.parse("tel://" + (String) arg0.getTag()));
					startActivity(intent);
				}
			});

			return convertView;
		}

	}

	public void onDestroy3() {
		MainActivity.this.unregisterReceiver(mReceiver3);
		super.onDestroy();
	}

	BroadcastReceiver mReceiver2 = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals("huahua.action.UpdataSmssView")) {
				m_smsadapter.updateListView(Utils.persons_sms);
			}
		}
	};

	public class SmsAdapter extends BaseAdapter {
		private LayoutInflater m_inflater;
		private ArrayList<Person_Sms> persons_sms;
		private Context context;

		public SmsAdapter(Context context, ArrayList<Person_Sms> persons_sms) {
			this.m_inflater = LayoutInflater.from(context);
			this.persons_sms = persons_sms;
			this.context = context;
		}

		public void updateListView(ArrayList<Person_Sms> persons_sms) {
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
			if (convertView == null) {
				convertView = m_inflater.inflate(R.layout.sms_list_item, null);
			}

			TextView sms_name = (TextView) convertView
					.findViewById(R.id.sms_name);
			sms_name.setText(persons_sms.get(position).Name + "("
					+ persons_sms.get(position).person_smss.size() + ")");

			TextView sms_content = (TextView) convertView
					.findViewById(R.id.sms_content);
			sms_content
					.setText(persons_sms.get(position).person_smss.get(0).SMSContent);

			TextView sms_data = (TextView) convertView
					.findViewById(R.id.sms_date);
			Date date = new Date(
					persons_sms.get(position).person_smss.get(0).SMSDate);
			SimpleDateFormat sfd = new SimpleDateFormat("yyyy/MM/dd");
			String time = sfd.format(date);
			sms_data.setText(time);

			return convertView;
		}

	}

	public void onDestroy2() {
		MainActivity.this.unregisterReceiver(mReceiver2);
		super.onDestroy();
	}

	class DeleteContactTask extends AsyncTask<Void, Integer, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			Utils.DeleteContact(ChooseContactID);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (m_dialogLoading != null) {
				m_dialogLoading.dismiss();
			}
		}

		@Override
		protected void onPreExecute() {
			m_dialogLoading = new ProgressDialog(MainActivity.this);
			m_dialogLoading.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置风格为圆形进度条
			m_dialogLoading.setMessage("正在删除");
			m_dialogLoading.setCancelable(false);
			m_dialogLoading.show();
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			Log.i("huahua", "onProgressUpdate");
		}

	}

	BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals("huahua.action.UpdataContactsView")) {
				if (m_topcontactslayout.getVisibility() == View.VISIBLE) {
					m_contactsadapter.updateListView(Utils.persons);
				} else {
					// 如果没有匹配的联系人
					if (Utils.filterpersons.isEmpty()) {
						m_contactslist.setEmptyView(m_listEmptyText);
					}

					m_contactsadapter.updateListView(Utils.filterpersons);
				}

			}
		}
	};

	private class BtnClick implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			if (v == m_AddContactBtn) {
				Bundle bundle = new Bundle();
				bundle.putInt("tpye", 0);
				bundle.putString("name", "");

				bundle.putString("number", "");

				Intent intent = new Intent(MainActivity.this,
						AddContactsActivity.class);
				intent.putExtra("person", bundle);
				startActivity(intent);
			} else if (v == m_RemoveSameContactBtn) {
				AlertDialog alertDialog = new AlertDialog.Builder(
						MainActivity.this)
						.setTitle("联系人去重")
						.setMessage("名字和号码都相同的联系人只保留一个?")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// 删除联系人操作,放在线程中处理
										new DeleteSameContactsTask().execute();
									}
								})
						.setNegativeButton("取消",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {

									}
								}).create();
				alertDialog.show();
			}
			// Intent intent=new Intent(MainActivity.this,MainActivity.class);
			// MainActivity.this.startActivity(intent);
		}
	}

	class DeleteSameContactsTask extends AsyncTask<Void, Integer, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			Utils.DeleteSameContacts();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (m_dialogLoading != null) {
				m_dialogLoading.dismiss();
			}
		}

		@Override
		protected void onPreExecute() {
			m_dialogLoading = new ProgressDialog(MainActivity.this);
			m_dialogLoading.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置风格为圆形进度条
			m_dialogLoading.setMessage("正在删除重复联系人");
			m_dialogLoading.setCancelable(false);
			m_dialogLoading.show();
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			Log.i("huahua", "onProgressUpdate");
		}

	}

	@Override
	public void onDestroy() {
		MainActivity.this.unregisterReceiver(mReceiver);

		super.onDestroy();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	public class ContactsAdapter extends BaseAdapter {
		private LayoutInflater m_inflater;
		private ArrayList<Persons> persons;
		private Context context;

		public ContactsAdapter(Context context, ArrayList<Persons> persons) {
			this.m_inflater = LayoutInflater.from(context);
			this.persons = persons;
			this.context = context;
		}

		// 当联系人列表数据发生变化时,用此方法来更新列表
		public void updateListView(ArrayList<Persons> persons) {
			this.persons = persons;
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return persons.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return persons.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = m_inflater.inflate(R.layout.contacts_list_item,
						null);
			}

			if (persons.isEmpty()) {
				Log.i("huahua", "为空");
				return convertView;
			}

			TextView name = (TextView) convertView
					.findViewById(R.id.contacts_name);
			name.setText(persons.get(position).Name);

			TextView number = (TextView) convertView
					.findViewById(R.id.contacts_number);
			number.setText(persons.get(position).Number);

			// 字母提示textview的显示
			TextView letterTag = (TextView) convertView
					.findViewById(R.id.pb_item_LetterTag);
			// 获得当前姓名的拼音首字母
			String firstLetter = persons.get(position).PY.substring(0, 1)
					.toUpperCase();

			// 如果是第1个联系人 那么letterTag始终要显示
			if (position == 0) {
				letterTag.setVisibility(View.VISIBLE);
				letterTag.setText(firstLetter);
			} else {
				// 获得上一个姓名的拼音首字母
				String firstLetterPre = persons.get(position - 1).PY.substring(
						0, 1).toUpperCase();
				// 比较一下两者是否相同
				if (firstLetter.equals(firstLetterPre)) {
					letterTag.setVisibility(View.GONE);
				} else {
					letterTag.setVisibility(View.VISIBLE);
					letterTag.setText(firstLetter);
				}
			}

			return convertView;
		}

	}

	// 字母列触摸的监听器
	private class ScrollBarListener implements
			AlphabetScrollBar.OnTouchBarListener {

		@Override
		public void onTouch(String letter) {

			// 触摸字母列时,将联系人列表更新到首字母出现的位置
			for (int i = 0; i < Utils.persons.size(); i++) {
				if (Utils.persons.get(i).PY.substring(0, 1)
						.compareToIgnoreCase(letter) == 0) {
					m_contactslist.setSelection(i);
					break;
				}
			}
		}
	}

	private void init() {
		liaotian = (TextView) findViewById(R.id.liaotian);
		faxian = (TextView) findViewById(R.id.faxian);
		tongxunlu = (TextView) findViewById(R.id.tongxunlu);
		usecenter = (TextView) findViewById(R.id.usecenter);

		mScrollLayout = (MyScrollLayout) findViewById(R.id.ScrollLayout);
		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.lllayout);
		mViewCount = mScrollLayout.getChildCount();
		mImageViews = new LinearLayout[mViewCount];
		for (int i = 0; i < mViewCount; i++) {
			mImageViews[i] = (LinearLayout) linearLayout.getChildAt(i);
			mImageViews[i].setEnabled(true);
			mImageViews[i].setOnClickListener(this);
			mImageViews[i].setTag(i);
		}
		mCurSel = 0;
		mImageViews[mCurSel].setEnabled(false);
		mScrollLayout.SetOnViewChangeListener(this);

		// set = (ImageView) findViewById(R.id.set);
		// add = (ImageView) findViewById(R.id.add);

		// set.setOnClickListener(new View.OnClickListener() {
		// @Override
		// public void onClick(View arg0) {
		// uploadImage(MainActivity.this);
		// }
		// });
		// add.setOnClickListener(new View.OnClickListener() {
		// @Override
		// public void onClick(View arg0) {
		// uploadImage2(MainActivity.this);
		// }
		// });
	}

	// public void uploadImage(final Activity context) {
	// menuWindow = new SelectPicPopupWindow(MainActivity.this, itemsOnClick);
	// // 显示窗口
	// // menuWindow.showAtLocation(MainActivity.this.findViewById(R.id.set),
	// // Gravity.TOP | Gravity.RIGHT, 10, 230); // 设置layout在PopupWindow中显示的位置
	// }
	//
	// public void uploadImage2(final Activity context) {
	// menuWindow2 = new SelectAddPopupWindow(MainActivity.this, itemsOnClick2);
	// // 显示窗口
	// // menuWindow2.showAtLocation(MainActivity.this.findViewById(R.id.add),
	// // Gravity.TOP | Gravity.RIGHT, 10, 230); // 设置layout在PopupWindow中显示的位置
	// }

	// // 为弹出窗口实现监听类
	// private OnClickListener itemsOnClick = new OnClickListener() {
	//
	// public void onClick(View v) {
	// menuWindow.dismiss();
	// }
	// };
	//
	// // 为弹出窗口实现监听类
	// private OnClickListener itemsOnClick2 = new OnClickListener() {
	//
	// public void onClick(View v) {
	// menuWindow2.dismiss();
	// }
	// };

	private void setCurPoint(int index) {
		if (index < 0 || index > mViewCount - 1 || mCurSel == index) {
			return;
		}
		mImageViews[mCurSel].setEnabled(true);
		mImageViews[index].setEnabled(false);
		mCurSel = index;

		if (index == 0) {
			liaotian.setTextColor(0xff228B22);
			faxian.setTextColor(Color.BLACK);
			tongxunlu.setTextColor(Color.BLACK);
			usecenter.setTextColor(Color.BLACK);

		} else if (index == 1) {
			liaotian.setTextColor(Color.BLACK);
			faxian.setTextColor(0xff228B22);
			tongxunlu.setTextColor(Color.BLACK);
			usecenter.setTextColor(Color.BLACK);

		} else if (index == 2) {
			liaotian.setTextColor(Color.BLACK);
			faxian.setTextColor(Color.BLACK);
			tongxunlu.setTextColor(0xff228B22);
			usecenter.setTextColor(Color.BLACK);

		} else {
			liaotian.setTextColor(Color.BLACK);
			faxian.setTextColor(Color.BLACK);
			tongxunlu.setTextColor(Color.BLACK);
			usecenter.setTextColor(0xff228B22);
		}
	}

	@Override
	public void OnViewChange(int view) {
		// TODO Auto-generated method stub
		setCurPoint(view);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int pos = (Integer) (v.getTag());
		setCurPoint(pos);
		mScrollLayout.snapToScreen(pos);
	}

}
