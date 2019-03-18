package com.wb.socket;
import com.wb.msg.Msg.MsgBase;
import com.wb.msg.Msg.MsgHead;

public class ServerHandler {
	public static void msgRecvHandler(SocketSynServer server, SocketMsg msg)
	{
		if(msg.getMsgId() == SocketMsg.MSG_ACCEPT)
		{
			server.addRunnable(msg);
		}
		else if(msg.getMsgId() == SocketMsg.Msg_g2d_setUser)
		{
			
		}
		else if(msg.getMsgId() == SocketMsg.Msg_g2d_getScore)
		{

		}
		else if(msg.getMsgId() == SocketMsg.Msg_g2d_heartbeat)
		{
			sendMessage(SocketMsg.Msg_d2g_heartbeat, "", msg);
		}
		else if(msg.getMsgId() == SocketMsg.Msg_d2g_setScore)
		{

		}
	}
	public static void sendMessage(int msgId,String body,SocketMsg msg)
	{
		try{
			MsgHead.Builder h = MsgHead.newBuilder();
			h.setMsgId(msgId);
			h.setGameId(10001);
			h.setMsgLen(2222);
			h.setMsgSign(11111111);
			MsgHead head = h.build();
			MsgBase.Builder msgBuilder = MsgBase.newBuilder();
			msgBuilder.setMsgHead(head.toString());
			msgBuilder.setMsgBody(body);
			MsgBase msgData = msgBuilder.build();
			msgData.writeTo(msg.getMsgSocket().getOutputStream());
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
	}
}
