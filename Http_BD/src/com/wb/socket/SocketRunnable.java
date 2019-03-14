package com.wb.socket;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Arrays;

import com.wb.msg.Msg.MsgBase;
import com.wb.msg.Msg.MsgHead;

public class SocketRunnable implements Runnable{
	private final int MAX_SIZE = 1024;
	SocketSynServer synServer = null;
	Socket socket = null;
	boolean isClose = false;
	SocketRunnable(SocketSynServer ser,Socket st)
	{
		synServer = ser;
		socket = st;
	}
	public void setClose(boolean close)
	{
		isClose = close;
	}
	@Override
	public void run() {
		while(!isClose)
		{
			try {
				//从客户端程序接收数据
				InputStream is = socket.getInputStream();
				MsgBase msg = MsgBase.parseDelimitedFrom(is);
				MsgHead head = MsgHead.parseFrom(msg.getMsgHeadBytes());
				
				System.out.println("从客户端程序接收数据:"+head.getMsgId());
				//这里做一下消息的检验
				SocketMsg sockMsg = new SocketMsg(head.getMsgId(),socket,msg.getMsgBody());
				synServer.pushSocket(sockMsg);
				
				//得到socket读写流,向服务端程序发送数据 
//				socket.getOutputStream().write(msg.getBytes());
//				socket.shutdownOutput();
			} catch (Exception e) {
				e.printStackTrace();
			} finally{
				
			}
		}
		
		try 
		{
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
