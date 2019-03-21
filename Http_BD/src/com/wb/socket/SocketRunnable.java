package com.wb.socket;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.google.protobuf.TextFormat;
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
				if(!socket.isConnected())
				{
					break;
				}
				//从客户端程序接收数据
				InputStream is = socket.getInputStream();

				byte len[] = new byte[1024];
                int count = is.read(len);  
            
                byte[] temp = new byte[count];
                for (int i = 0; i < count; i++) {   
                    temp[i] = len[i];                              
                }
				MsgBase msgBase = MsgBase.parseFrom(temp);              
                String headStr = msgBase.getMsgHead();
                String bodyStr = msgBase.getMsgBody();
                InputStream isHeadStrem = new ByteArrayInputStream(headStr.getBytes());
                InputStreamReader readerHead = new InputStreamReader(isHeadStrem, "ASCII");
                MsgHead.Builder headBuild = MsgHead.newBuilder();
                TextFormat.merge(readerHead, headBuild);
                MsgHead head = headBuild.build();
                
				System.out.println("get client msg is :"+head.getMsgId());
				//这里做一下消息的检验
				SocketMsg sockMsg = new SocketMsg(head.getMsgId(),socket,bodyStr);
				synServer.pushSocket(sockMsg);
				
				// push(sockMsg);
			} catch (Exception e) {
				e.printStackTrace();
				isClose = true;
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
