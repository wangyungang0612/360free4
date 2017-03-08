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
	// ��ϵ���б�Ľ���
	private View ContactsView;
	// ��ĸ����ͼView
	private AlphabetScrollBar m_asb;
	// ��ʾѡ�е���ĸ
	private TextView m_letterNotice;
	// ��ϵ�˵��б�
	private ListView m_contactslist;
	// ��ϵ���б��������
	public static ContactsAdapter m_contactsadapter;
	// ����������ϵ��EditText
	private EditText m_FilterEditText;
	// û��ƥ����ϵ��ʱ��ʾ��TextView
	private TextView m_listEmptyText;
	// ������ϵ�˰�ť
	private Button m_AddContactBtn;
	// ȥ����ϵ�˰�ť
	private Button m_RemoveSameContactBtn;
	// �������layout
	private FrameLayout m_topcontactslayout;
	// ��ϵ�����ݹ۲���
	private ContactsContentObserver ContactsCO;
	// ѡ�е���ϵ������
	private String ChooseContactName;
	// ѡ�е���ϵ�˺���
	private String ChooseContactNumber;
	// ѡ�е���ϵ��ID
	private String ChooseContactID;
	// ���ضԻ���
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

		// �õ���ĸ�еĶ���,�����ô�����Ӧ������
		m_asb = (AlphabetScrollBar) ContactsView
				.findViewById(R.id.alphabetscrollbar);
		m_asb.setOnTouchBarListener(new ScrollBarListener());
		m_letterNotice = (TextView) ContactsView
				.findViewById(R.id.pb_letter_notice);
		m_asb.setTextView(m_letterNotice);

		// �õ���ϵ���б�,������������
		m_contactslist = (ListView) ContactsView.findViewById(R.id.pb_listvew);
		m_contactsadapter = new ContactsAdapter(getActivity(), Utils.persons);
		m_contactslist.setAdapter(m_contactsadapter);
		// ��ϵ���б�������
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
										new String[] { "����", "����", "ɾ����ϵ��",
												"�༭��ϵ��" },
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
															.setTitle("ɾ��")
															.setMessage(
																	"ɾ����ϵ��"
																			+ ChooseContactName
																			+ "?")
															.setPositiveButton(
																	"ȷ��",
																	new DialogInterface.OnClickListener() {

																		@Override
																		public void onClick(
																				DialogInterface dialog,
																				int which) {
																			// ɾ����ϵ�˲���,�����߳��д���
																			new DeleteContactTask()
																					.execute();
																		}
																	})
															.setNegativeButton(
																	"ȡ��",
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

		// ������ϵ��
		m_AddContactBtn = (Button) ContactsView.findViewById(R.id.add_contacts);
		m_AddContactBtn.setOnClickListener(new BtnClick());
		// ȥ����ϵ��
		m_RemoveSameContactBtn = (Button) ContactsView
				.findViewById(R.id.remove_same_contacts);
		m_RemoveSameContactBtn.setOnClickListener(new BtnClick());

		m_topcontactslayout = (FrameLayout) ContactsView
				.findViewById(R.id.top_contacts_layout);

		// ��ʼ�������༭��,�����ı��ı�ʱ�ļ�����
		m_FilterEditText = (EditText) ContactsView
				.findViewById(R.id.pb_search_edit);
		m_FilterEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

				if (!"".equals(s.toString().trim())) {
					// ���ݱ༭��ֵ������ϵ�˲�������ϵ�б�
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
			m_dialogLoading.setProgressStyle(ProgressDialog.STYLE_SPINNER);// ���÷��ΪԲ�ν�����
			m_dialogLoading.setMessage("����ɾ��");
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
					// ���û��ƥ�����ϵ��
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
						.setTitle("��ϵ��ȥ��")
						.setMessage("���ֺͺ��붼��ͬ����ϵ��ֻ����һ��?")
						.setPositiveButton("ȷ��",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// ɾ����ϵ�˲���,�����߳��д���
										new DeleteSameContactsTask().execute();
									}
								})
						.setNegativeButton("ȡ��",
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
			m_dialogLoading.setProgressStyle(ProgressDialog.STYLE_SPINNER);// ���÷��ΪԲ�ν�����
			m_dialogLoading.setMessage("����ɾ���ظ���ϵ��");
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

		// ����ϵ���б����ݷ����仯ʱ,�ô˷����������б�
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
				Log.i("huahua", "Ϊ��");
				return convertView;
			}

			TextView name = (TextView) convertView
					.findViewById(R.id.contacts_name);
			name.setText(persons.get(position).Name);

			TextView number = (TextView) convertView
					.findViewById(R.id.contacts_number);
			number.setText(persons.get(position).Number);

			// ��ĸ��ʾtextview����ʾ
			TextView letterTag = (TextView) convertView
					.findViewById(R.id.pb_item_LetterTag);
			// ��õ�ǰ������ƴ������ĸ
			String firstLetter = persons.get(position).PY.substring(0, 1)
					.toUpperCase();

			// ����ǵ�1����ϵ�� ��ôletterTagʼ��Ҫ��ʾ
			if (position == 0) {
				letterTag.setVisibility(View.VISIBLE);
				letterTag.setText(firstLetter);
			} else {
				// �����һ��������ƴ������ĸ
				String firstLetterPre = persons.get(position - 1).PY.substring(
						0, 1).toUpperCase();
				// �Ƚ�һ�������Ƿ���ͬ
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

	// ��ĸ�д����ļ�����
	private class ScrollBarListener implements
			AlphabetScrollBar.OnTouchBarListener {

		@Override
		public void onTouch(String letter) {

			// ������ĸ��ʱ,����ϵ���б���µ�����ĸ���ֵ�λ��
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
