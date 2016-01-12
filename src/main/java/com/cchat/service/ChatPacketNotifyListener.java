package com.cchat.service;
import com.cchat.common.base.data.ChatMessage;
import com.cchat.common.base.data.DataTalk;
import com.cchat.common.base.data.XmlData.DataChatMessage;
import com.cchat.common.base.data.XmlParse.XmlParseChatMessage;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
public class ChatPacketNotifyListener implements PacketListener {
	@Override
		public void processPacket(Packet packet) {
			if (packet instanceof Message) {
				Message message = (Message) packet;
				if (message.getBody() != null) {
					try {
						String body = URLDecoder.decode(message.getBody(), "utf-8"); //TODO:hhool will be remove;
						XmlParseChatMessage parser = new XmlParseChatMessage();
						InputStream in = new ByteArrayInputStream(body.getBytes("UTF-8"));
						DataChatMessage chatMessage = (DataChatMessage) parser.parse(in);
						String from = StringUtils.parseName(message.getFrom());
						String to = StringUtils.parseName(message.getTo());
						ChatMessage chatMsg = new ChatMessage(from, to, chatMessage.getTalk());

						DataTalk.Func func = chatMsg.getDataTalk().getFun();
						if (func.equals(DataTalk.Func.OnReceipt)) {
//							TextMessage msg = new TextMessage(0, message.getBody(), message.getFrom());
							if (ConnectionService.mConnectionManager != null) {
								ConnectionService.mConnectionManager.handleReciveTextMessage(chatMsg);
							}
//							processChatPacketResult(message.getFrom(), chatMsg, null);
						} else if (func.equals(DataTalk.Func.OnMsg)) {
//							TextMessage msg = new TextMessage(0, message.getBody(), message.getFrom());
							if (ConnectionService.mConnectionManager != null) {
								ConnectionService.mConnectionManager.handleReciveTextMessage(chatMsg);
							}
//							processChatPacket(message.getFrom(), chatMsg);
						} else {
//							SLogger.w(TAG, "hhool will done later");
						}
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				} else {
//					SLogger.w(TAG, "message body is null");
				}
			} else {
//				SLogger.v(TAG, "can't run here" + " " + packet.getXmlns());
			}
		}
	}