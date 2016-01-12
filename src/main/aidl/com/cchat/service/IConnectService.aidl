package com.cchat.service;
import com.cchat.service.IConnectServiceCallback;
import com.cchat.common.base.service.access.IAccessListener;
import com.cchat.common.base.data.ChatMessage;

interface IConnectService{
	void login(String user, String password, boolean autoLogin, IAccessListener listener);
	void userRegister(String user, String password, IAccessListener listener);
	void resetPasswd(String password, IAccessListener listener);
	void logout(IAccessListener listener);

	void agreeAdd(String jid);

	void reqAdd(String jid);

	void getFriensList(IAccessListener listener);
	void sendMessage(String to, String textMessage);
	void sendMessageObj(in ChatMessage chatMessage);
	void register(IConnectServiceCallback cb);
	void unRegister(IConnectServiceCallback cb);
}