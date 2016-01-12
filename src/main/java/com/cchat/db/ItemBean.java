package com.cchat.db;

public class ItemBean {
	public static final String ID = "_id";
	public static final String USER = "user";
	public static final String SENDTEXT = "sendtext";
	public static final String RECEIVETEXT = "receivetext";
	public static final String TIME = "time";
	public static final String WITH = "with";
	public static final String MESSAGETYPE = "messagetype";
	public static final String FILEPATH = "filepath";
	public static final String READ = "read";
	public static final String TASKID = "taskid";
	public static final String TRANSMISSIONSTATE = "transmissionstate";
	
	private String id;
	private String user;
	private String sendtext;
	private String receivetext;
	private String time;
	private String with;
	private int messagetype;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	
	public String getsendText() {
		return sendtext;
	}
	public void setSendText(String sendtext) {
		this.sendtext = sendtext;
	}
	
	public String getReceiveText() {
		return receivetext;
	}
	public void setReceiveText(String receivetext) {
		this.receivetext = receivetext;
	}
	
	public void setTime(String time) {
		this.time = time;
	}
	public String getTime() {
		return time;
	}
	
	public void setWith(String with) {
		this.with = with;
	}
	public String getWith() {
		return with;
	}
	
	public int getMessageType() {
		return messagetype;
	}
	public void setMessageType(int messagetype) {
		this.messagetype = messagetype;
	}
	
}
