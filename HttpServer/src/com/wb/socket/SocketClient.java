package com.wb.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.wb.msg.Msg.MsgBase;
import com.wb.msg.Msg.MsgHead;

public class SocketClient implements Runnable {

    private static SocketClient _instance = null;

    List<SocketMsg> readList = null;
    List<SocketMsg> writeList = null;
    List<MsgBase> sendList = null;
    private String m_ip = "";
    private int m_port = 0;
    Socket socket = null;
    private final float HEADBEAT_TIME = 10.0f;
    private float headBeatTime = 0.0f;

    private SocketClient()
    {
        readList = new ArrayList<SocketMsg>();
        writeList = new ArrayList<SocketMsg>();
        sendList = new ArrayList<MsgBase>();
    }
    public static synchronized SocketClient getInstance()
    {
        if(null == _instance)
        {
            _instance = new SocketClient();
        }
        return _instance;
    }
    public void connect(String ip,int port)
    {
        m_ip = ip;
        m_port = port;
        try{
            socket = new Socket(m_ip,m_port);
            sendMessage(SocketMsg.Msg_d2g_heartbeat, "");
            headBeatTime = HEADBEAT_TIME;
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public boolean sendMessage(int msgId, String body)
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
            sendList.add(msgBuilder.build());
        }
        catch(Exception e) {
            e.printStackTrace();
            return false;
        }
       
        return true;
    }

    public void update(float fps)
    {
        headBeatTime = headBeatTime - fps;
        if(headBeatTime <= 0.0f)
        {
            sendMessage(SocketMsg.Msg_d2g_heartbeat, "");
            headBeatTime = HEADBEAT_TIME;
        }
        for(int i = 0;i < sendList.size();i++)
        {
            try{
                sendList.get(i).writeTo(socket.getOutputStream());
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
        sendList.clear();
    }

    public void push(SocketMsg socket)
	{
		synchronized (writeList) {
			writeList.add(socket);
		}
	}
	
	public int getWriteListSize() {
		synchronized (writeList) {			
			return writeList.size();
		}
	}
	public List<SocketMsg> getReadList() {
		return readList;
	}
	public void swap()
	{
		//这里加锁
		synchronized(writeList) {
			List<SocketMsg> temp = readList;
			readList = writeList;
			writeList = temp;
			writeList.clear();	
		}

	}

    @Override
    public void run() {
        try{
            InputStream is = socket.getInputStream();
            MsgBase msg = MsgBase.parseDelimitedFrom(is);
            MsgHead head = MsgHead.parseFrom(msg.getMsgHeadBytes());
            
            System.out.println("从客户端程序接收数据:"+head.getMsgId());
            //这里做一下消息的检验
            SocketMsg sockMsg = new SocketMsg(head.getMsgId(),socket,msg.getMsgBody());
            push(sockMsg);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

}