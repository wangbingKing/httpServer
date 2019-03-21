package com.wb.socket;

import java.net.Socket;

public class SocketMsg {
	
	public static int MSG_ACCEPT = 9999;
	public static int Msg_g2d_setUser = 1;
	public static int Msg_d2c_setUser = 2;
	public static int Msg_g2d_setScore = 3;
	public static int Msg_d2g_setScore = 4;
	public static int Msg_g2d_getScore = 5;
	public static int Msg_d2g_getScore = 6;
	public static int Msg_g2d_heartbeat = 99;
	public static int Msg_d2g_heartbeat = 100;
	
	private int msgId = 0;
	private Socket socket = null;
	private String body = "";
	public SocketMsg(int mId,Socket st,String bd)
	{
		msgId = mId;
		socket = st;
		body = bd;
	}
	public int getMsgId()
	{
		return msgId;
	}
	public Socket getMsgSocket()
	{
		return socket;
	}
	public String getBody()
	{
		return body;
	}
}
