package com.cchat.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import com.cchat.CChatApplication;
import com.cchat.ChatActivity;
import com.cchat.R;
import com.cchat.common.base.data.ChatMessage;
import com.cchat.common.base.data.Hbutils;
import com.cchat.common.base.service.access.IAccessListener;
import com.cchat.common.base.service.base.android.ShareDataPack;
import com.cchat.service.ConnectMethod;
import com.cchat.service.ConnectionService;
import com.cchat.service.IConnectService;
import com.cchat.service.IConnectServiceCallback;
import com.cchat.service.Person;
import com.cchat.utils.TLog;
import com.cchat.utils.XmppTool;

public class ConnectionManager {

	private static final String TAG = "ConnectionManager";
	protected static final String Tag = "message";
	private Timer mTimer;
	private ConnectionService mContext;
	private List<RosterEntry> rosterEntries = new ArrayList<RosterEntry>();
	public ConnectionManager(ConnectionService aidlService) {
		super();
		mContext = aidlService;
		init();
	}

	private void init() {
		
		/*mTimer = new Timer();
		mTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				int rand = (int) (Math.random() * 3);
				color = colors[rand];
				weight = weights[rand];
				System.out.println("----------" + rand);
				mValue += rand;
				handleGetCount(rand+"");
			}
		}, 0, 800);*/
		
	}
	
	// 有时候我们可能在一个类中有多个Stub对象，它们都是要跟远程交互的类的实例，这个时候可以考虑使用RemoteCallbackList<>：
	private final RemoteCallbackList<IConnectServiceCallback> mCallbacks = new RemoteCallbackList<IConnectServiceCallback>();

	public final IConnectService.Stub mConnectServiceBinder = new IConnectService.Stub() {

		@Override
		public void login(final String user, final String password, boolean autoLogin, final IAccessListener listener) throws RemoteException {

			new Thread() {
				public void run() {
					ConnectMethod.login(user, password, listener);

//					loginState(is);
					/*if (is) {
						getOnLineMsg();
						//getOnLineFile();
						getOffLine();
					}
*/
				};

			}.start();

		}

		@Override
		public void userRegister(final String user, final String password, final IAccessListener listener) throws RemoteException {
			new Thread() {
				public void run() {
					ConnectMethod.register(user, password, listener);
				};
			}.start();
		}

		@Override
		public void resetPasswd(final String password, final IAccessListener listener) throws RemoteException {
			new Thread() {
				public void run() {
					ConnectMethod.changePassword(password, listener);
				};
			}.start();
		}

		@Override
		public void register(IConnectServiceCallback cb) throws RemoteException {
			if (cb != null) {
				mCallbacks.register(cb);
			}

		}

		@Override
		public void unRegister(IConnectServiceCallback cb)
				throws RemoteException {
			if (cb != null) {
				mCallbacks.unregister(cb);
			}

		}

		@Override
		public void logout(final IAccessListener listener) throws RemoteException {
			new Thread(){
				@Override
				public void run() {
					super.run();
					ConnectMethod.logout(listener);

				}
			}.start();
		}

		@Override
		public void agreeAdd(String jid) throws RemoteException {
			Presence presence = new Presence( Presence.Type.subscribed);
			//同意是 subscribed   拒绝是unsubscribe
			presence.setTo(jid);//接收方jid
			presence.setFrom(XmppTool.getConnection().getUser());//发送方jid
			XmppTool.getConnection().sendPacket(presence);//connection是你自己的XMPPConnection链接
		}

		@Override
		public void reqAdd(String jid) throws RemoteException {
			Presence presence = new Presence( Presence.Type.subscribe);
			//同意是 subscribed   拒绝是unsubscribe
			presence.setTo(jid);//接收方jid
			presence.setFrom(XmppTool.getConnection().getUser());//发送方jid
			XmppTool.getConnection().sendPacket(presence);//connection是你自己的XMPPConnection链接
		}

		@Override
		public void getFriensList(final IAccessListener listener) throws RemoteException {
			getFriends(listener);
		}

		@Override
		public void sendMessage(String to, String msg) throws RemoteException {
			ConnectMethod.sendTalkMsg(to, msg);
			((CChatApplication)mContext.getApplication()).saveMessage(null,"123");
		}

		@Override
		public void sendMessageObj(ChatMessage chatMessage) throws RemoteException {

			CChatApplication app = (CChatApplication) mContext.getApplication(); // 获得CustomApplication对象
			app.saveMessage(null,"");
			String jid = chatMessage.getTo();
			Message message = new Message(jid, Message.Type.chat);
			String body = chatMessage.toXml(false, true);
			String strBodyContent = Hbutils.URIEncoder(body, null);
			message.setBody(strBodyContent);
//			TimerMessage timerMessage = mTaskMessage.get(chatMessage.getDataTalk().getTimeId());
//			if (timerMessage != null) {
				/*try {
					if (mChatTransport != null && mConnection != null && mConnection.isLogined()) {
						timerMessage.mbTimer = true;
						mChatTransport.sendPacket(message);
						startTaskQueueTimer();
					} else {
						uploadChatMsg(chatMessage);
					}
				} catch (RemoteException e) {
					uploadChatMsg(chatMessage);
				}*/
			XmppTool.getConnection().sendPacket(message);
			/*} else {
				SLogger.i(TAG, "miss chat message in task");
			}*/

//			XmppTool.getConnection().sendPacket(chatMessage);
		}

	};

	public void handleReciveTextMessage(ChatMessage textMessage) {
		sound();
		showNotification(textMessage);
		final int callbackCount = mCallbacks.beginBroadcast();
		for (int i = 0; i < callbackCount; i++) {
			try {
				mCallbacks.getBroadcastItem(i).receive(textMessage);
			} catch (RemoteException e) {
				// The RemoteCallbackList will take care of removing
				// the dead object for us.
			}
		}
		mCallbacks.finishBroadcast();
	}

	private void handleInComingMessageEvent(ChatMessage chatMessage) {
		/*broadCastLocalInComingMessageEvent(chatMessage);
		final int n = mRemoteListeners.beginBroadcast();

		for (int i = 0; i < n; i++) {
			IChatMessageAccessListener TransferStateListener = mRemoteListeners.getBroadcastItem(i);
			try {
				if (TransferStateListener != null)
					TransferStateListener.onInComingMessageEvent(chatMessage);
			} catch (RemoteException e) {
				SLogger.e(TAG, "Error while triggering remote connection listeners", e);
			}
		}
		mRemoteListeners.finishBroadcast();*/
		sound();
		showNotification(chatMessage);
		final int callbackCount = mCallbacks.beginBroadcast();
		for (int i = 0; i < callbackCount; i++) {
			try {
				mCallbacks.getBroadcastItem(i).receive(chatMessage);
			} catch (RemoteException e) {
				// The RemoteCallbackList will take care of removing
				// the dead object for us.
			}
		}
		mCallbacks.finishBroadcast();
	}

	public void handleRosterStateChange(Person person, int type) {

		final int callbackCount = mCallbacks.beginBroadcast();
		for (int i = 0; i < callbackCount; i++) {
			try {
				mCallbacks.getBroadcastItem(i).rosterStateChange(person, type);
			} catch (RemoteException e) {
				// The RemoteCallbackList will take care of removing
				// the dead object for us.
			}
		}
		mCallbacks.finishBroadcast();
	}

	void sound (){
		MediaPlayer player = null;
		try {
			player = ring();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			player.stop();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			player.stop();
		}
		
	}
	
	void showNotification ( ChatMessage msg){
		NotificationManager nm = (NotificationManager)(mContext.getSystemService(Context.NOTIFICATION_SERVICE));
		
		String[] strArray = msg.getFrom().split("/"); 
		
		Notification n = new Notification(R.mipmap.ic_launcher, strArray[0]+":"+msg.getDataTalk().getContent(), System.currentTimeMillis());
		n.flags |= Notification.FLAG_AUTO_CANCEL;     
		
		Intent i = new Intent(mContext, ChatActivity.class);
		
		i.putExtra("touser", strArray[0]);//消息来自于
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);           
		//PendingIntent
		PendingIntent contentIntent = PendingIntent.getActivity( mContext, R.string.app_name, 
		        i, 
		        PendingIntent.FLAG_UPDATE_CURRENT);
		
		n.setLatestEventInfo(mContext, strArray[0], msg.getDataTalk().getContent(), contentIntent);
		nm.notify(R.string.app_name, n);
		
		/*自定义  通知布局*/
		/*RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.notification_view);
		rv.setImageViewResource(R.id.image, R.drawable.chat);
		rv.setTextViewText(R.id.text, "Hello,there,I'm john.");
		n.contentView = rv;
		n.contentIntent = contentIntent;
		nm.notify(R.string.app_name, n);*/
	}
	public void handlePassPersons(List<Person> persons) {
		Log.d(TAG, "getCount");

		final int callbackCount = mCallbacks.beginBroadcast();
		for (int i = 0; i < callbackCount; i++) {
			try {
				mCallbacks.getBroadcastItem(i).passPersons(persons);
			} catch (RemoteException e) {
				// The RemoteCallbackList will take care of removing
				// the dead object for us.
			}
		}
		mCallbacks.finishBroadcast();
	}
	
	public void loginState(boolean isSuccessLogin) {
		Log.d(TAG, "loginState");

		final int callbackCount = mCallbacks.beginBroadcast();
		for (int i = 0; i < callbackCount; i++) {
			try {
				mCallbacks.getBroadcastItem(i).loginState(isSuccessLogin);
			} catch (RemoteException e) {
				// The RemoteCallbackList will take care of removing
				// the dead object for us.
			}
		}
		mCallbacks.finishBroadcast();
	}
	
	public IBinder onBind()
    {
        return mConnectServiceBinder;
    }

	public void clear() {
		if (mTimer!=null) {
			mTimer.cancel();
			mTimer = null;
		}
		
	}
	
	/**
	 * 文本
	 */
	private void getOnLineMsg() {
		Log.d("getOnLineMsg", "getOnLineMsg");
		ConnectMethod.getOnLine();
	}

	/**
	 * 文件
	 */
	private void getOnLineFile() {
		ConnectMethod.getOnLineFile(mContext);
	}
	
	/**
	 * 搜索用户
	 */
	private void searchUser() {
		/*new Thread() {
			public void run() {
				String usr = ((EditText) findViewById(R.id.et_searchname))
						.getText().toString();
				ArrayList<String> users = ConnectMethod.searchUsers(usr);

				// 跳转页面
				Intent intent = new Intent(MainTabActivity.this, SearchActivity.class);
				intent.putStringArrayListExtra("users", users);
				startActivity(intent);
			};
		}.start();*/
	}

	/**
	 * 获取所有好友的信息
	 */
	private void getFriends(final IAccessListener listener) {
		new Thread() {
			public void run() {
				List<Person> mPersons = new ArrayList<Person>();
				rosterEntries.clear();
//				 获取所有好友，不分组
				for (RosterEntry enty : ConnectMethod.a()) {
					rosterEntries.add(enty);
					TLog.analytics("enty.getUser()->" + enty.getUser() + " entry.getStatus: " + enty.getType().toString());
					if (enty.getType().equals("from")){
/*none,
        to,
        from,
        both,
        remove;*/
					} else if (enty.getType().equals("from")){

					}
					mPersons.add(new Person(0, enty.getUser(), enty.getType().toString()));
				}

				// 获取所有好友，分组
				/*for (RosterGroup group : ConnectMethod.searchAllGroup()) {
					Log.d(Tag, group.getName() + "");
					for (RosterEntry enty : ConnectMethod.getGroupFriends(group.getName())) {
						enty.setName(group.getName()); //标识所在 组名  暂时 不用
						//rosterEntries.add(enty);
						mPersons.add(new Person(0, enty.getUser()));
					}
				}*/
				try {
					if (listener != null) {
						listener.onFinish(true, new ShareDataPack(mPersons));
					}
				} catch (RemoteException e) {
					e.printStackTrace();
				}
//				TransferStateListener.onFinish(true, new ShareDataPack(new String("abc")));
//				handlePassPersons(mPersons);
			};
		}.start();
	}

	/**
	 * 创建新的组好友
	 */
	private void createNewGroup() {
		/*new Thread() {
			public void run() {
				String group = ((EditText) findViewById(R.id.et_searchname))
						.getText().toString();
				String friend = ((EditText) findViewById(R.id.et_groupname))
						.getText().toString();
				ConnectMethod.addGroupFriend(group, friend);
			};
		}.start();*/
	}

	/**
	 * 离线消息
	 */
	private void getOffLine() {
		new Thread() {
			public void run() {
				List<org.jivesoftware.smack.packet.Message> msglist = ConnectMethod
						.getOffLine();
				Log.d(Tag, msglist.size()+"");
				for (org.jivesoftware.smack.packet.Message msg : msglist) {
					Log.d(Tag, msg.getTo() + msg.getBody() + msg.getFrom());
				}
			};
		}.start();
	}
	private MediaPlayer ring() throws Exception, IOException {
		// TODO Auto-generated method stub
		Uri alert = RingtoneManager
				.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		MediaPlayer player = new MediaPlayer();
		player.setDataSource(mContext, alert);
		final AudioManager audioManager = (AudioManager) mContext.getSystemService(mContext.AUDIO_SERVICE);
		if (audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION) != 0) {
			player.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
			// player.setLooping(true);
			player.prepare();
			player.start();
		}
		return player;
	}

}
