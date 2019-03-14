package com.wb.socket;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class SocketSynServer implements Runnable{
	private static int NUM_THREADS = 50;
	ExecutorService  pool = null;//服务器工作线程池
	SocketManager socketManager = null;
	public SocketSynServer()
	{
		pool = Executors.newFixedThreadPool(NUM_THREADS); //服务器工作线程池
		socketManager = new SocketManager();
	}
	public void pushSocket(SocketMsg msg)
	{
		socketManager.push(msg);
	}
	@Override
	public void run() {
		
		
//		
//		Runnable r = new SocketRunnable(request);
//		pool.submit(r);
		// TODO Auto-generated method stub
//		while(true)
//		{
//			this.pushSocket();
//			while(this.getWriteListSize() > 0)
//			{
//				this.pushSocket();
//			}
//			socketManager.update();
//		}
	}

}
