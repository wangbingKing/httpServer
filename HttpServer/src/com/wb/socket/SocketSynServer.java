package com.wb.socket;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;




public class SocketSynServer implements Runnable{
	private static int NUM_THREADS = 50;
	List<SocketRunnable> readList = null;
	ExecutorService  pool = null;//服务器工作线程池
	SocketManager socketManager = null;
	public SocketSynServer()
	{
		pool = Executors.newFixedThreadPool(NUM_THREADS); //服务器工作线程池
		socketManager = new SocketManager();
		readList = new ArrayList<SocketRunnable>();
	}
	public void pushSocket(SocketMsg msg)
	{
		socketManager.push(msg);
	}
	public void addRunnable(SocketMsg msg)
	{
		Runnable r = new SocketRunnable(this,msg.getMsgSocket());
		pool.submit(r);
	}
	
	@Override
	public void run() {
		while(true)
		{
			List<SocketMsg> list = socketManager.getReadList();
			for(int i = 0;i < list.size();i++)
			{
				ServerHandler.msgRecvHandler(this, list.get(i));
			}
			list.clear();
			if(socketManager.getWriteListSize() > 0)
			{
				socketManager.swap();
				List<SocketMsg> list2 = socketManager.getReadList();
				for(int i = 0;i < list2.size();i++)
				{
					ServerHandler.msgRecvHandler(this, list2.get(i));
				}
				list2.clear();
			}
		}
	}

}
