package com.lshapp.baidutieba.fragment;

import com.lshapp.baidutieba.R;
import com.lshapp.baidutieba.activity.ChatActivity;
import com.lshapp.baidutieba.adapter.MessageRecentAdapter;
import com.lshapp.baidutieba.view.ClearEditText;
import com.lshapp.baidutieba.view.DialogTips;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobRecent;
import cn.bmob.im.db.BmobDB;

/**
 * ����Ự
 * 
 * @ClassName: ConversationFragment
 * @Description: TODO
 * @author smile
 * @date 2014-6-7 ����1:01:37
 */
public class RecentFragment extends FragmentBase implements
		OnItemClickListener, OnItemLongClickListener {

	ClearEditText mClearEditText;

	ListView listview;

	MessageRecentAdapter adapter;

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.tabb_fragment, container, false);
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		initView();
	}

	private void initView() {
		listview = (ListView) findViewById(R.id.list);
		listview.setOnItemClickListener(this);
		listview.setOnItemLongClickListener(this);
		adapter = new MessageRecentAdapter(getActivity(),
				R.layout.item_conversation, BmobDB.create(getActivity())
						.queryRecents());
		listview.setAdapter(adapter);

		mClearEditText = (ClearEditText) findViewById(R.id.et_msg_search);
		mClearEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				adapter.getFilter().filter(s);
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
	 * ɾ���Ự deleteRecent
	 * 
	 * @param @param recent
	 * @return void
	 * @throws
	 */
	private void deleteRecent(BmobRecent recent) {
		adapter.remove(recent);
		BmobDB.create(getActivity()).deleteRecent(recent.getTargetid());
		BmobDB.create(getActivity()).deleteMessages(recent.getTargetid());
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
			int position, long arg3) {
		// TODO Auto-generated method stub
		BmobRecent recent = adapter.getItem(position);
		showDeleteDialog(recent);
		return true;
	}

	public void showDeleteDialog(final BmobRecent recent) {
		DialogTips dialog = new DialogTips(getActivity(), recent.getUserName(),
				"ɾ���Ự", "ȷ��", true, true);
		// ���óɹ��¼�
		dialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogInterface, int userId) {
				deleteRecent(recent);
			}
		});
		// ��ʾȷ�϶Ի���
		dialog.show();
		dialog = null;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		// TODO Auto-generated method stub
		BmobRecent recent = adapter.getItem(position);
		// ����δ����Ϣ
		BmobDB.create(getActivity()).resetUnread(recent.getTargetid());
		// ��װ�������
		BmobChatUser user = new BmobChatUser();
		user.setAvatar(recent.getAvatar());
		user.setNick(recent.getNick());
		user.setUsername(recent.getUserName());
		user.setObjectId(recent.getTargetid());
		Intent intent = new Intent(getActivity(), ChatActivity.class);
		intent.putExtra("user", user);
		startAnimActivity(intent);
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

	public void refresh() {
		try {
			getActivity().runOnUiThread(new Runnable() {
				public void run() {
					adapter = new MessageRecentAdapter(getActivity(),
							R.layout.item_conversation, BmobDB.create(
									getActivity()).queryRecents());
					listview.setAdapter(adapter);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (!hidden) {
			refresh();
		}
	}

}
