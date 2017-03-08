package com.example.isweixin.tc4;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.example.isweixin.R;
import com.example.isweixin.tc1.ChatActivity;
import com.example.isweixin.tc1.ContactsContentObserver;

import com.example.isweixin.tc3.Utils;
import com.example.isweixin.tc3.Utils.Person_Sms;
import com.example.isweixin.tc3.Utils.Persons;
import com.example.isweixin.tc4.AddContactsActivity.SaveContactTask;
import com.example.tctc1002.AlphabetScrollBar;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.webkit.WebView.FindListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ContactsFragment extends Fragment {
	// 联系人列表的界面
	private View ContactsView;
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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		LayoutInflater inflater = getActivity().getLayoutInflater();
		ContactsView = inflater.inflate(R.layout.b, null);

		IntentFilter filter = new IntentFilter();
		filter.addAction("huahua.action.UpdataContactsView");
		getActivity().registerReceiver(mReceiver, filter);

		ContactsCO = new ContactsContentObserver(new Handler());
		getActivity().getContentResolver().registerContentObserver(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, false,
				ContactsCO);

		// 得到字母列的对象,并设置触摸响应监听器
		m_asb = (AlphabetScrollBar) ContactsView
				.findViewById(R.id.alphabetscrollbar);
		m_asb.setOnTouchBarListener(new ScrollBarListener());
		m_letterNotice = (TextView) ContactsView
				.findViewById(R.id.pb_letter_notice);
		m_asb.setTextView(m_letterNotice);

		// 得到联系人列表,并设置适配器
		m_contactslist = (ListView) ContactsView.findViewById(R.id.pb_listvew);
		m_contactsadapter = new ContactsAdapter(getActivity(), Utils.persons);
		m_contactslist.setAdapter(m_contactsadapter);
		// 联系人列表长按监听
		m_contactslist
				.setOnItemLongClickListener(new OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> arg0,
							View arg1, int arg2, long arg3) {

						Vibrator vib = (Vibrator) getActivity()
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
								getActivity())
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
															getActivity(),
															ChatActivity.class);
													Bundle mBundle = new Bundle();
													mBundle.putSerializable(
															"chatperson",
															personsms);
													intent.putExtras(mBundle);
													startActivity(intent);
												} else if (which == 2) {
													AlertDialog DeleteDialog = new AlertDialog.Builder(
															getActivity())
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
															getActivity(),
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

		m_listEmptyText = (TextView) ContactsView
				.findViewById(R.id.nocontacts_notice);

		// 新增联系人
		m_AddContactBtn = (Button) ContactsView.findViewById(R.id.add_contacts);
		m_AddContactBtn.setOnClickListener(new BtnClick());
		// 去重联系人
		m_RemoveSameContactBtn = (Button) ContactsView
				.findViewById(R.id.remove_same_contacts);
		m_RemoveSameContactBtn.setOnClickListener(new BtnClick());

		m_topcontactslayout = (FrameLayout) ContactsView
				.findViewById(R.id.top_contacts_layout);

		// 初始化搜索编辑框,设置文本改变时的监听器
		m_FilterEditText = (EditText) ContactsView
				.findViewById(R.id.pb_search_edit);
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
			m_dialogLoading = new ProgressDialog(getActivity());
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

				Intent intent = new Intent(getActivity(),
						AddContactsActivity.class);
				intent.putExtra("person", bundle);
				startActivity(intent);
			} else if (v == m_RemoveSameContactBtn) {
				AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
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
			m_dialogLoading = new ProgressDialog(getActivity());
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
		getActivity().unregisterReceiver(mReceiver);
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

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		ViewGroup p = (ViewGroup) ContactsView.getParent();
		if (p != null) {
			p.removeAllViewsInLayout();
		}

		return ContactsView;
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

}
