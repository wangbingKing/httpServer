package com.wb.socket;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.wb.msg.Msg.MsgBase;
import com.wb.msg.Msg.MsgHead;

public class SocketRunnable implements Runnable{
	SocketSynServer synServer = null;
	Socket socket = null;
	boolean isClose = false;
	List<SocketMsg> writeList = null;
	SocketRunnable(SocketSynServer ser,Socket st)
	{
		synServer = ser;
		socket = st;
		writeList = new ArrayList<SocketMsg>();
	}
	public void push(SocketMsg socket)
	{
		synchronized (writeList) {
			writeList.add(socket);
		}
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

				byte len[] = new byte[1024];
				int count = is.read(len);  
			
				byte[] temp = new byte[count];
				for (int i = 0; i < count; i++) {   
					temp[i] = len[i];                              
				}
				System.out.println(temp.toString());
				MsgBase msg = MsgBase.parseFrom(is);
				// MsgBase msg = MsgBase.parseDelimitedFrom(is);
				is.close();
				MsgHead head = MsgHead.parseFrom(msg.getMsgHeadBytes());
				
				System.out.println("从客户端程序接收数据:"+head.getMsgId());
				//这里做一下消息的检验
				SocketMsg sockMsg = new SocketMsg(head.getMsgId(),socket,msg.getMsgBody());
				synServer.pushSocket(sockMsg);
				
				// push(sockMsg);
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
