package com.cchat.common.base.data;

import org.jivesoftware.smack.packet.IQ;

public class WeChatPacket extends IQ {

	public static final String ELEMENT_NAME = "wechat";
	public static final String NAMESPACE = "wechat";

	private String urlStr;
	private String json;

	public String getUrlStr() {
		return urlStr;
	}

	public void setUrlStr(String urlStr) {
		this.urlStr = urlStr;
	}

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}

	@Override
	public String getChildElementXML() {
		StringBuffer buf = new StringBuffer();
		buf.append("<" + ELEMENT_NAME + " xmlns=\"" + NAMESPACE + "\">");
		if (getType() == IQ.Type.GET) {
			buf.append("<urlStr>").append(urlStr).append("</urlStr>");
			buf.append("<json>").append(json).append("</json>");
			buf.append(getExtensionsXML());
		}
		buf.append("</" + ELEMENT_NAME + ">");
		return buf.toString();
	}

}
