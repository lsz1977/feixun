package com.cchat.utils;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.provider.PrivacyProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.GroupChatInvitation;
import org.jivesoftware.smackx.OfflineMessageManager;
import org.jivesoftware.smackx.PrivateDataManager;
import org.jivesoftware.smackx.bytestreams.socks5.provider.BytestreamsProvider;
import org.jivesoftware.smackx.packet.ChatStateExtension;
import org.jivesoftware.smackx.packet.LastActivity;
import org.jivesoftware.smackx.packet.OfflineMessageInfo;
import org.jivesoftware.smackx.packet.OfflineMessageRequest;
import org.jivesoftware.smackx.packet.SharedGroupsInfo;
import org.jivesoftware.smackx.provider.AdHocCommandDataProvider;
import org.jivesoftware.smackx.provider.DataFormProvider;
import org.jivesoftware.smackx.provider.DelayInformationProvider;
import org.jivesoftware.smackx.provider.DiscoverInfoProvider;
import org.jivesoftware.smackx.provider.DiscoverItemsProvider;
import org.jivesoftware.smackx.provider.MUCAdminProvider;
import org.jivesoftware.smackx.provider.MUCOwnerProvider;
import org.jivesoftware.smackx.provider.MUCUserProvider;
import org.jivesoftware.smackx.provider.MessageEventProvider;
import org.jivesoftware.smackx.provider.MultipleAddressesProvider;
import org.jivesoftware.smackx.provider.RosterExchangeProvider;
import org.jivesoftware.smackx.provider.StreamInitiationProvider;
import org.jivesoftware.smackx.provider.VCardProvider;
import org.jivesoftware.smackx.provider.XHTMLExtensionProvider;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.OfflineMessageManager;
import org.jivesoftware.smackx.receipts.DeliveryReceipt;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager;
import org.jivesoftware.smackx.receipts.DeliveryReceiptRequest;

import org.jivesoftware.smackx.search.UserSearch;

import android.util.Log;

import com.cchat.service.ConnectMethod;

/**
 * asmack加载的静态方法
 * 
 */
public class XmppTool {
	private static ConnectionConfiguration connConfig;
	private static XMPPConnection con;
	private static OfflineMessageManager offlineManager;
	public static final String g_Domain = "@124.205.165.170";//"@yuling-pc";

	private static boolean isLogined = false;

	// 静态加载ReconnectionManager ,重连后正常工作
	static {
		try {
			Class.forName("org.jivesoftware.smack.ReconnectionManager");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static OfflineMessageManager getOffLineMessageManager() {
		if (offlineManager == null) {
			offlineManager = new OfflineMessageManager(con);
		}
		return offlineManager;
	}

	private static void openConnection() {
		try {
			configure(ProviderManager.getInstance());
			connConfig = new ConnectionConfiguration("124.205.165.170", 5222);

			// 设置登录状态为离线
			connConfig.setSendPresence(false);
			// 断网重连
			connConfig.setReconnectionAllowed(true);
			connConfig.setSecurityMode(ConnectionConfiguration.SecurityMode.enabled);
			connConfig.setSASLAuthenticationEnabled(true);
			connConfig.setTruststorePath("/system/etc/security/cacerts.bks");
			connConfig.setTruststorePassword("changeit");
			connConfig.setTruststoreType("bks");

			con = new XMPPConnection(connConfig);

			// 自动回复回执方法，如果对方的消息要求回执。
			ProviderManager pm = ProviderManager.getInstance();
			pm.addExtensionProvider(DeliveryReceipt.ELEMENT, DeliveryReceipt.NAMESPACE, new DeliveryReceipt.Provider());
			pm.addExtensionProvider(DeliveryReceiptRequest.ELEMENT, DeliveryReceipt.NAMESPACE, new DeliveryReceiptRequest.Provider());
			DeliveryReceiptManager.getInstanceFor(con).enableAutoReceipts();

			con.connect();
			if (con.isConnected()) {
				con.addConnectionListener(connectionListener);
			}

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("XmppTool.openConnection()");
		}
	}

	public static void removeConnectionListener() {
		if (con != null && connectionListener != null) {
			con.removeConnectionListener(connectionListener);
		}

	}

	public static ConnectionListener connectionListener = new ConnectionListener() {
		@Override
		public void reconnectionSuccessful() {
			Log.i("connection", "reconnectionSuccessful");
//			ConnectMethod.login("3","3");
			ConnectMethod.getOnLine();
			ConnectMethod.getOffLine();
		}

		@Override
		public void reconnectionFailed(Exception arg0) {
			Log.i("connection", "reconnectionFailed");
		}

		@Override
		public void reconnectingIn(int arg0) {
			Log.i("connection", "reconnectingIn");
		}

		@Override
		public void connectionClosedOnError(Exception arg0) {
			Log.i("connection", "connectionClosedOnError");
		}

		@Override
		public void connectionClosed() {
			Log.i("connection", "connectionClosed");
			isLogined = false;

		}
	};

	public static XMPPConnection getConnection() {
		if (con == null || !con.isConnected()) {
			openConnection();
		}
		return con;
	}

	public static void closeConnection() {
//		con.disconnect();
		if (offlineManager != null)
			offlineManager = null;
		try {
			if (con != null) {
				con.disconnect();
				con = null;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void setLoginState(boolean beLogined) {

		isLogined = beLogined;
	}

	public static void configure(ProviderManager pm) {

//  Private Data Storage
		pm.addIQProvider("query", "jabber:iq:private", new PrivateDataManager.PrivateDataIQProvider());

//  Time
		try {
			pm.addIQProvider("query", "jabber:iq:time", Class.forName("org.jivesoftware.smackx.packet.Time"));
		} catch (ClassNotFoundException e) {
			Log.w("TestClient", "Can't load class for org.jivesoftware.smackx.packet.Time");
		}

//  Roster Exchange
		pm.addExtensionProvider("x", "jabber:x:roster", new RosterExchangeProvider());

//  Message Events
		pm.addExtensionProvider("x", "jabber:x:event", new MessageEventProvider());

//  Chat State
		pm.addExtensionProvider("active", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
		pm.addExtensionProvider("composing", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
		pm.addExtensionProvider("paused", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
		pm.addExtensionProvider("inactive", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
		pm.addExtensionProvider("gone", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());

//  XHTML
		pm.addExtensionProvider("html", "http://jabber.org/protocol/xhtml-im", new XHTMLExtensionProvider());

//  Group Chat Invitations
		pm.addExtensionProvider("x", "jabber:x:conference", new GroupChatInvitation.Provider());

//  Service Discovery # Items
		pm.addIQProvider("query", "http://jabber.org/protocol/disco#items", new DiscoverItemsProvider());

//  Service Discovery # Info
		pm.addIQProvider("query", "http://jabber.org/protocol/disco#info", new DiscoverInfoProvider());

//  Data Forms
		pm.addExtensionProvider("x", "jabber:x:data", new DataFormProvider());

//  MUC User
		pm.addExtensionProvider("x", "http://jabber.org/protocol/muc#user", new MUCUserProvider());

//  MUC Admin
		pm.addIQProvider("query", "http://jabber.org/protocol/muc#admin", new MUCAdminProvider());

//  MUC Owner
		pm.addIQProvider("query", "http://jabber.org/protocol/muc#owner", new MUCOwnerProvider());

//  Delayed Delivery
		pm.addExtensionProvider("x", "jabber:x:delay", new DelayInformationProvider());

//  Version
		try {
			pm.addIQProvider("query", "jabber:iq:version", Class.forName("org.jivesoftware.smackx.packet.Version"));
		} catch (ClassNotFoundException e) {
			//  Not sure what's happening here.
		}

//  VCard
		pm.addIQProvider("vCard", "vcard-temp", new VCardProvider());

//  Offline Message Requests
		pm.addIQProvider("offline", "http://jabber.org/protocol/offline", new OfflineMessageRequest.Provider());

//  Offline Message Indicator
		pm.addExtensionProvider("offline", "http://jabber.org/protocol/offline", new OfflineMessageInfo.Provider());

//  Last Activity
		pm.addIQProvider("query", "jabber:iq:last", new LastActivity.Provider());

//  User Search
		pm.addIQProvider("query", "jabber:iq:search", new UserSearch.Provider());

//  SharedGroupsInfo
		pm.addIQProvider("sharedgroup", "http://www.jivesoftware.org/protocol/sharedgroup", new SharedGroupsInfo.Provider());

//  JEP-33: Extended Stanza Addressing
		pm.addExtensionProvider("addresses", "http://jabber.org/protocol/address", new MultipleAddressesProvider());

//   FileTransfer
		pm.addIQProvider("si", "http://jabber.org/protocol/si", new StreamInitiationProvider());

		pm.addIQProvider("query", "http://jabber.org/protocol/bytestreams", new BytestreamsProvider());

//  Privacy
		pm.addIQProvider("query", "jabber:iq:privacy", new PrivacyProvider());
		pm.addIQProvider("command", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider());
		pm.addExtensionProvider("malformed-action", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.MalformedActionError());
		pm.addExtensionProvider("bad-locale", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.BadLocaleError());
		pm.addExtensionProvider("bad-payload", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.BadPayloadError());
		pm.addExtensionProvider("bad-sessionid", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.BadSessionIDError());
		pm.addExtensionProvider("session-expired", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.SessionExpiredError());
	}

}
