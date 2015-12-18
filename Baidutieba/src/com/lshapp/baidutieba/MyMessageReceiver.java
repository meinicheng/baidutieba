package com.lshapp.baidutieba;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobNotifyManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.config.BmobConstant;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.inteface.EventListener;
import cn.bmob.im.inteface.OnReceiveListener;
import cn.bmob.im.util.BmobJsonUtil;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.listener.FindListener;

import com.lshapp.baidutieba.activity.MainActivity;
import com.lshapp.baidutieba.activity.MyhuifuActivity;
import com.lshapp.baidutieba.activity.NewFriendActivity;
import com.lshapp.baidutieba.utils.CollectionUtils;
import com.lshapp.baidutieba.utils.CommonUtils;

/**
 * ������Ϣ������
 * 
 * @ClassName: MyMessageReceiver
 * @Description: TODO
 * @author smile
 * @date 2014-5-30 ����4:01:13
 */
public class MyMessageReceiver extends BroadcastReceiver {

	// �¼�����
	public static ArrayList<EventListener> ehList = new ArrayList<EventListener>();

	public static final int NOTIFY_ID = 0x000;
	public static int mNewNum = 0;//
	BmobUserManager userManager;
	BmobChatUser currentUser;
	public static Context mcontext;

	// ������뷢���Զ����ʽ����Ϣ����ʹ��sendJsonMessage����������Json��ʽ���ַ�����Ȼ���㰴�ո�ʽ�Լ�����������

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub

		String json = intent.getStringExtra("msg");
		userManager = BmobUserManager.getInstance(context);
		currentUser = userManager.getCurrentUser();
		boolean isNetConnected = CommonUtils.isNetworkAvailable(context);
		if (isNetConnected) {
			String a ="{\"alert\":\"hflist\"}";
			if(json.equals(a)){
				
				ashowOtherNotify(mcontext, "a", "a", "a", MyhuifuActivity.class);
				
			}else {
				parseMessage(context, json);
			}
		} else {
			for (int i = 0; i < ehList.size(); i++)
				((EventListener) ehList.get(i)).onNetChange(isNetConnected);
		}
	}

	/**
	 * ����Json�ַ���
	 * 
	 * @Title: parseMessage
	 * @Description: TODO
	 * @param @param context
	 * @param @param json
	 * @return void
	 * @throws
	 */
	private void parseMessage(final Context context, final String json) {
		JSONObject jo;
		try {
			jo = new JSONObject(json);
			String tag = BmobJsonUtil.getString(jo, BmobConstant.PUSH_KEY_TAG);
			if (tag.equals(BmobConfig.TAG_OFFLINE)) {// ����֪ͨ
				if (currentUser != null) {
					if (ehList.size() > 0) {// �м�����ʱ�򣬴�����ȥ
						for (EventListener handler : ehList)
							handler.onOffline();
					} else {
						// �������
						CustomApplcation.getInstance().logout();
					}
				}
			} else {
				String fromId = BmobJsonUtil.getString(jo,
						BmobConstant.PUSH_KEY_TARGETID);
				// ������Ϣ���շ���ObjectId--Ŀ���ǽ�����˻���½ͬһ�豸ʱ���޷����յ��ǵ�ǰ��½�û�����Ϣ��
				final String toId = BmobJsonUtil.getString(jo,
						BmobConstant.PUSH_KEY_TOID);
				String msgTime = BmobJsonUtil.getString(jo,
						BmobConstant.PUSH_READED_MSGTIME);
				if (fromId != null
						&& !BmobDB.create(context, toId).isBlackUser(fromId)) {// ����Ϣ���ͷ���Ϊ�������û�
					if (TextUtils.isEmpty(tag)) {// ��Я��tag��ǩ--�˿ɽ���İ���˵���Ϣ
						BmobChatManager.getInstance(context).createReceiveMsg(
								json, new OnReceiveListener() {

									@Override
									public void onSuccess(BmobMsg msg) {
										// TODO Auto-generated method stub
										if (ehList.size() > 0) {// �м�����ʱ�򣬴�����ȥ
											for (int i = 0; i < ehList.size(); i++) {
												((EventListener) ehList.get(i))
														.onMessage(msg);
											}
										} else {
											boolean isAllow = CustomApplcation
													.getInstance().getSpUtil()
													.isAllowPushNotify();
											if (isAllow
													&& currentUser != null
													&& currentUser
															.getObjectId()
															.equals(toId)) {// ��ǰ��½�û����ڲ���Ҳ���ڽ��շ�id
												mNewNum++;
												showMsgNotify(context, msg);
											}
										}
									}

									@Override
									public void onFailure(int code, String arg1) {
										// TODO Auto-generated method stub
										BmobLog.i("��ȡ���յ���Ϣʧ�ܣ�" + arg1);
									}
								});

					} else {// ��tag��ǩ
						if (tag.equals(BmobConfig.TAG_ADD_CONTACT)) {
							// ���������������أ������º�̨��δ���ֶ�
							BmobInvitation message = BmobChatManager
									.getInstance(context).saveReceiveInvite(
											json, toId);

							if (currentUser != null) {// �е�½�û�
								if (toId.equals(currentUser.getObjectId())) {
									if (ehList.size() > 0) {// �м�����ʱ�򣬴�����ȥ
										for (EventListener handler : ehList)
											handler.onAddUser(message);
									} else {
										showOtherNotify(context,
												message.getFromname(), toId,
												message.getFromname()
														+ "������Ӻ���",
												NewFriendActivity.class);

									}
								}

							}
						} else if (tag.equals(BmobConfig.TAG_ADD_AGREE)) {

							String username = BmobJsonUtil.getString(jo,
									BmobConstant.PUSH_KEY_TARGETUSERNAME);
							Log.d("IM", username);
							showOtherNotify(context, username, toId, username
									+ "ͬ�������Ϊ����", MainActivity.class);
							// �յ��Է���ͬ������֮�󣬾͵���ӶԷ�Ϊ����--��Ĭ�����ͬ�ⷽΪ���ѣ������浽���غ������ݿ�
							BmobUserManager.getInstance(context)
									.addContactAfterAgree(username,
											new FindListener<BmobChatUser>() {

												@Override
												public void onError(int arg0,
														final String arg1) {
													// TODO Auto-generated
													// method stub
												}

												@Override
												public void onSuccess(
														List<BmobChatUser> arg0) {
													// TODO Auto-generated
													// method stub
													// ���浽�ڴ���
													CustomApplcation
															.getInstance()
															.setContactList(
																	CollectionUtils
																			.list2map(BmobDB
																					.create(context)
																					.getContactList()));
												}
											});

						} else if (tag.equals(BmobConfig.TAG_READED)) {// �Ѷ���ִ
							String conversionId = BmobJsonUtil.getString(jo,
									BmobConstant.PUSH_READED_CONVERSIONID);
							if (currentUser != null) {
								// ����ĳ����Ϣ��״̬
								BmobChatManager.getInstance(context)
										.updateMsgStatus(conversionId, msgTime);
								if (toId.equals(currentUser.getObjectId())) {
									if (ehList.size() > 0) {// �м�����ʱ�򣬴�����ȥ--�����޸Ľ���
										for (EventListener handler : ehList)
											handler.onReaded(conversionId,
													msgTime);
									}
								}
							}
						}
					}
				} else {// �ں������ڼ����е���Ϣ��Ӧ����Ϊ�Ѷ�����Ȼ��ȡ��������֮���ֿ��Բ�ѯ�ĵ�
					BmobChatManager.getInstance(context).updateMsgReaded(true,
							fromId, msgTime);
					BmobLog.i("����Ϣ���ͷ�Ϊ�������û�");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			// �����ȡ�����п�����web��̨���͸��ͻ��˵���Ϣ��Ҳ�п����ǿ������Զ��巢�͵���Ϣ����Ҫ���������н����ʹ���
			BmobLog.i("parseMessage����" + e.getMessage());
		}
	}

	/**
	 * ��ʾ��������Ϣ��֪ͨ
	 * 
	 * @Title: showNotify
	 * @return void
	 * @throws
	 */
	public void showMsgNotify(Context context, BmobMsg msg) {
		// ����֪ͨ��
		int icon = R.drawable.ic_launcher;
		String trueMsg = "";
		if (msg.getMsgType() == BmobConfig.TYPE_TEXT
				&& msg.getContent().contains("\\ue")) {
			trueMsg = "[����]";
		} else if (msg.getMsgType() == BmobConfig.TYPE_IMAGE) {
			trueMsg = "[ͼƬ]";
		} else if (msg.getMsgType() == BmobConfig.TYPE_VOICE) {
			trueMsg = "[����]";
		} else if (msg.getMsgType() == BmobConfig.TYPE_LOCATION) {
			trueMsg = "[λ��]";
		} else {
			trueMsg = msg.getContent();
		}
		CharSequence tickerText = msg.getBelongUsername() + ":" + trueMsg;
		String contentTitle = msg.getBelongUsername() + " (" + mNewNum
				+ "������Ϣ)";

		Intent intent = new Intent(context, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

		boolean isAllowVoice = CustomApplcation.getInstance().getSpUtil()
				.isAllowVoice();
		boolean isAllowVibrate = CustomApplcation.getInstance().getSpUtil()
				.isAllowVibrate();

		BmobNotifyManager.getInstance(context).showNotifyWithExtras(
				isAllowVoice, isAllowVibrate, icon, tickerText.toString(),
				contentTitle, tickerText.toString(), intent);
	}

	/**
	 * ��ʾ����Tag��֪ͨ showOtherNotify
	 */
	public void showOtherNotify(Context context, String username, String toId,
			String ticker, Class<?> cls) {
		boolean isAllow = CustomApplcation.getInstance().getSpUtil()
				.isAllowPushNotify();
		boolean isAllowVoice = CustomApplcation.getInstance().getSpUtil()
				.isAllowVoice();
		boolean isAllowVibrate = CustomApplcation.getInstance().getSpUtil()
				.isAllowVibrate();
		if (isAllow && currentUser != null
				&& currentUser.getObjectId().equals(toId)) {
			// ͬʱ����֪ͨ
			BmobNotifyManager.getInstance(context).showNotify(isAllowVoice,
					isAllowVibrate, R.drawable.ic_launcher, ticker, username,
					ticker.toString(), NewFriendActivity.class);
		}
	}
	//�ظ�����
	public void ashowOtherNotify(Context context, String username, String toId,
			String ticker, Class<?> cls) {
		boolean isAllow = CustomApplcation.getInstance().getSpUtil()
				.isAllowPushNotify();
		boolean isAllowVoice = CustomApplcation.getInstance().getSpUtil()
				.isAllowVoice();
		boolean isAllowVibrate = CustomApplcation.getInstance().getSpUtil()
				.isAllowVibrate();
		if (isAllow && currentUser != null) {
			// ͬʱ����֪ͨ
			BmobNotifyManager.getInstance(context).showNotify(isAllowVoice,
					isAllowVibrate, R.drawable.ic_launcher, ticker, username,
					ticker.toString(), MyhuifuActivity.class);
		}
	}

}
