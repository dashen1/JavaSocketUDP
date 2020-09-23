package com.java.socket.modal;

import java.sql.Date;
import java.sql.Timestamp;

public class ChatUser {
	private long id;
	private int flag;
	private String ChatMsg;
	private Timestamp ChatTime;
	private String IpAddress;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public int getFlag() {
		return flag;
	}
	public void setFlag(int flag) {
		this.flag = flag;
	}
	public String getChatMsg() {
		return ChatMsg;
	}
	public void setChatMsg(String chatMsg) {
		ChatMsg = chatMsg;
	}
	public Timestamp getChatTime() {
		return ChatTime;
	}
	public void setChatTime(Timestamp chatTime) {
		ChatTime = chatTime;
	}
	public String getIpAddress() {
		return IpAddress;
	}
	public void setIpAddress(String ipAddress) {
		IpAddress = ipAddress;
	}
	public String getUserName() {
		return UserName;
	}
	public void setUserName(String userName) {
		UserName = userName;
	}
	private String UserName;
}
