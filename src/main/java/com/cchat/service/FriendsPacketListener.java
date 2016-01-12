package com.cchat.service;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;

import android.content.Context;
import android.util.Log;

import com.cchat.utils.XmppTool;

public class FriendsPacketListener implements PacketListener{
	private Context context;
	public FriendsPacketListener( ){
//		this.context=context;
	}

	@Override
	public void processPacket(Packet packet) {
		if(packet.getFrom().equals(packet.getTo())){
			return;
		}
		 if (packet instanceof Presence) {  
             Presence presence = (Presence) packet;  
             String from = presence.getFrom();//发送方  
             String to = presence.getTo();//接收方
			 int type = 0;

             if (presence.getType().equals(Presence.Type.subscribe)) {//好友申请  
                   Log.e("jj", "好友申请");
				 type = 0;
             } else if (presence.getType().equals(Presence.Type.subscribed)) {//同意添加好友  
            	 Log.e("jj", "同意添加好友");

				 /*if (ConnectionService.mConnectionManager != null) {

					 Presence presence1 = new Presence( Presence.Type.subscribed);
					 //同意是 subscribed   拒绝是unsubscribe
					 presence.setTo(from);//接收方jid
					 presence.setFrom(XmppTool.getConnection().getUser());//发送方jid
					 XmppTool.getConnection().sendPacket(presence);//connection是你自己的XMPPConnection链接

//						 ConnectionService.mConnectionManager.handleReciveTextMessage(chatMsg);
				 }*/

				 type = 1;
             } else if (presence.getType().equals(Presence.Type.unsubscribe)) {//拒绝添加好友  和  删除好友  
            	 Log.e("jj", "拒绝添加好友");
				 type = 2;
             } else if (presence.getType().equals(Presence.Type.unsubscribed)){
				 type = 3;
             } /*else if (presence.getType().equals(Presence.Type.unavailable)) {//好友下线   要更新好友列表，可以在这收到包后，发广播到指定页面   更新列表
            	 Log.e("jj", "好友下线");
             } else if(presence.getType().equals(Presence.Type.available)){//好友上线  
            	 Log.e("jj", "好友上线");
             } */ else{
//            	 Log.e("jj", "error");
				 return;
             }
			 if (ConnectionService.mConnectionManager != null) {
				 /*available,
					unavailable,
					subscribe,
					subscribed,
					unsubscribe,
					unsubscribed,
					error;*/

				 ConnectionService.mConnectionManager.handleRosterStateChange(new Person(0, from, "from"), type);
			 }
         }  
       };  
	}

