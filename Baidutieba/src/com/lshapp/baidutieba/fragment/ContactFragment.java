package com.lshapp.baidutieba.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.db.BmobDB;
import cn.bmob.v3.listener.UpdateListener;

import com.lshapp.baidutieba.CustomApplcation;
import com.lshapp.baidutieba.R;
import com.lshapp.baidutieba.activity.AddFriendActivity;
import com.lshapp.baidutieba.activity.NearPeopleActivity;
import com.lshapp.baidutieba.activity.NewFriendActivity;
import com.lshapp.baidutieba.activity.SetMyInfoActivity;
import com.lshapp.baidutieba.adapter.UserFriendAdapter;
import com.lshapp.baidutieba.base.User;
import com.lshapp.baidutieba.utils.CharacterParser;
import com.lshapp.baidutieba.utils.CollectionUtils;
import com.lshapp.baidutieba.utils.PinyinComparator;
import com.lshapp.baidutieba.view.ClearEditText;
import com.lshapp.baidutieba.view.DialogTips;
import com.lshapp.baidutieba.view.MyLetterView;
import com.lshapp.baidutieba.view.MyLetterView.OnTouchingLetterChangedListener;

/**
 * ��ϵ��
 * 
 * @ClassName: ContactFragment
 * @Description: TODO
 * @author smile
 * @date 2014-6-7 ����1:02:05
 */
@SuppressLint("DefaultLocale")
public class ContactFragment extends FragmentBase implements
		OnItemClickListener, OnItemLongClickListener {

	ClearEditText mClearEditText;

	TextView dialog;

	ListView list_friends;
	MyLetterView right_letter;

	private UserFriendAdapter userAdapter;// ����

	List<User> friends = new ArrayList<User>();
	// ������ж�ʹ��
	private InputMethodManager inputMethodManager;

	/**
	 * ����ת����ƴ������
	 */
	private CharacterParser characterParser;
	/**
	 * ����ƴ��������ListView�����������
	 */
	private PinyinComparator pinyinComparator;

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.tabc_fragment, container, false);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		inputMethodManager = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		init();
	}

	private void init() {
		characterParser = CharacterParser.getInstance();
		pinyinComparator = new PinyinComparator();
		initListView();
		initRightLetterView();
		initEditText();
	}

	private void initEditText() {
		mClearEditText = (ClearEditText) findViewById(R.id.et_msg_search);
		// �������������ֵ�ĸı�����������
		mClearEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// ������������ֵΪ�գ�����Ϊԭ�����б�����Ϊ���������б�
				filterData(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
	}

	/**
	 * ����������е�ֵ���������ݲ�����ListView
	 * 
	 * @param filterStr
	 */
	private void filterData(String filterStr) {
		List<User> filterDateList = new ArrayList<User>();
		if (TextUtils.isEmpty(filterStr)) {
			filterDateList = friends;
		} else {
			filterDateList.clear();
			for (User sortModel : friends) {
				String name = sortModel.getUsername();
				if (name != null) {
					if (name.indexOf(filterStr.toString()) != -1
							|| characterParser.getSelling(name).startsWith(
									filterStr.toString())) {
						filterDateList.add(sortModel);
					}
				}
			}
		}
		// ����a-z��������
		Collections.sort(filterDateList, pinyinComparator);
		userAdapter.updateListView(filterDateList);
	}

	/**
	 * ΪListView�������
	 * 
	 * @param date
	 * @return
	 */
	private void filledData(List<BmobChatUser> datas) {
		friends.clear();
		int total = datas.size();
		for (int i = 0; i < total; i++) {
			BmobChatUser user = datas.get(i);
			User sortModel = new User();
			sortModel.setAvatar(user.getAvatar());
			sortModel.setNick(user.getNick());
			sortModel.setUsername(user.getUsername());
			sortModel.setObjectId(user.getObjectId());
			sortModel.setContacts(user.getContacts());
			// ����ת����ƴ��
			String username = sortModel.getUsername();
			// ��û��username
			if (username != null) {
				String pinyin = characterParser.getSelling(sortModel
						.getUsername());
				String sortString = pinyin.substring(0, 1).toUpperCase();
				// ������ʽ���ж�����ĸ�Ƿ���Ӣ����ĸ
				if (sortString.matches("[A-Z]")) {
					sortModel.setSortLetters(sortString.toUpperCase());
				} else {
					sortModel.setSortLetters("#");
				}
			} else {
				sortModel.setSortLetters("#");
			}
			friends.add(sortModel);
		}
		// ����a-z��������
		Collections.sort(friends, pinyinComparator);
	}

	ImageView iv_msg_tips;
	TextView tv_new_name;
	LinearLayout layout_new;// ������
	LinearLayout layout_near;// ���Һ���
	LinearLayout layout_nearb;// ��������

	private void initListView() {
		list_friends= (ListView)findViewById(R.id.list_friends);
		RelativeLayout headView = (RelativeLayout) mInflater.inflate(R.layout.include_new_friend, null);
		iv_msg_tips = (ImageView)headView.findViewById(R.id.iv_msg_tips);
		layout_new =(LinearLayout)headView.findViewById(R.id.layout_new);
		layout_near =(LinearLayout)headView.findViewById(R.id.layout_near);
		layout_nearb = (LinearLayout)headView.findViewById(R.id.layout_nearb);
		//�����ѵĵ���¼�
		layout_new.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(), NewFriendActivity.class);
				intent.putExtra("from", "contact");
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.in_anim, R.anim.out_anim);
			}
		});
		
		//���Һ���
		layout_near.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getActivity(), AddFriendActivity.class);
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.in_anim, R.anim.out_anim);
			}
		});
		layout_nearb.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(), NearPeopleActivity.class);
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.in_anim, R.anim.out_anim);
			}
		});
		
		list_friends.addHeaderView(headView);
	    userAdapter = new UserFriendAdapter(getActivity(), friends);
	    
		list_friends.setAdapter(userAdapter);
		list_friends.setOnItemClickListener(this);
		list_friends.setOnItemLongClickListener(this);
		
		list_friends.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// ���������
				if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
					if (getActivity().getCurrentFocus() != null)
						inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
				}
				return false;
			}
		});
		
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		// TODO Auto-generated method stub
		if (isVisibleToUser) {
			queryMyfriends();
		}
		super.setUserVisibleHint(isVisibleToUser);
	}

	private void initRightLetterView() {
		right_letter = (MyLetterView) findViewById(R.id.right_letter);
		dialog = (TextView) findViewById(R.id.dialog);
		right_letter.setTextView(dialog);
		right_letter
				.setOnTouchingLetterChangedListener(new LetterListViewListener());
	}

	private class LetterListViewListener implements
			OnTouchingLetterChangedListener {

		@Override
		public void onTouchingLetterChanged(String s) {
			// ����ĸ�״γ��ֵ�λ��
			int position = userAdapter.getPositionForSection(s.charAt(0));
			if (position != -1) {
				list_friends.setSelection(position);
			}
		}
	}
	
	/**
	 * ��ȡ�����б� queryMyfriends
	 * 
	 * @return void
	 * @throws
	 */
	private void queryMyfriends() {
		// �Ƿ����µĺ�������
		if (BmobDB.create(getActivity()).hasNewInvite()) {
			iv_msg_tips.setVisibility(View.VISIBLE);
		} else {
			iv_msg_tips.setVisibility(View.GONE);
		}
		// ����������һ�α��صĺ������ݿ�ļ�飬��Ϊ�˱��غ������ݿ����Ѿ�����˶Է������ǽ���ȴû����ʾ����������
		// �����������ڴ��б���ĺ����б�
		CustomApplcation.getInstance().setContactList(
				CollectionUtils.list2map(BmobDB.create(getActivity())
						.getContactList()));

		Map<String, BmobChatUser> users = CustomApplcation.getInstance()
				.getContactList();
		// ��װ�µ�User
		filledData(CollectionUtils.map2list(users));
		if (userAdapter == null) {
			userAdapter = new UserFriendAdapter(getActivity(), friends);
			list_friends.setAdapter(userAdapter);
		} else {
			userAdapter.notifyDataSetChanged();
		}

	}

	private boolean hidden;

	private View view;

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		this.hidden = hidden;
		if (!hidden) {
			refresh();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (!hidden) {
			refresh();
		}
	}

	public void refresh() {
		try {
			getActivity().runOnUiThread(new Runnable() {
				public void run() {
					queryMyfriends();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		// TODO Auto-generated method stub
		User user = (User) userAdapter.getItem(position - 1);
		// �Ƚ�����ѵ���ϸ����ҳ��
		Intent intent = new Intent(getActivity(), SetMyInfoActivity.class);
//		intent.putExtra("from", "other");
		intent.putExtra("userdata", user);
		startActivity(intent);
		getActivity().overridePendingTransition(R.anim.in_anim, R.anim.out_anim);

	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
			int position, long arg3) {
		// TODO Auto-generated method stub
		User user = (User) userAdapter.getItem(position - 1);
		showDeleteDialog(user);
		return true;
	}

	public void showDeleteDialog(final User user) {
		DialogTips dialog = new DialogTips(getActivity(), user.getUsername(),
				"ɾ����ϵ��", "ȷ��", true, true);
		// ���óɹ��¼�
		dialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogInterface, int userId) {
				deleteContact(user);
			}
		});
		// ��ʾȷ�϶Ի���
		dialog.show();
		dialog = null;
	}

	/**
	 * ɾ����ϵ�� deleteContact
	 * 
	 * @return void
	 * @throws
	 */
	private void deleteContact(final User user) {
		final ProgressDialog progress = new ProgressDialog(getActivity());
		progress.setMessage("����ɾ��...");
		progress.setCanceledOnTouchOutside(false);
		progress.show();
		userManager.deleteContact(user.getObjectId(), new UpdateListener() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				ShowToast("ɾ���ɹ�");
				// ɾ���ڴ�
				CustomApplcation.getInstance().getContactList()
						.remove(user.getUsername());
				// ���½���
				getActivity().runOnUiThread(new Runnable() {
					public void run() {
						progress.dismiss();
						userAdapter.remove(user);
					}
				});
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				ShowToast("ɾ��ʧ�ܣ�" + arg1);
				progress.dismiss();
			}
		});
	}

}
