package com.wb.db;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.wb.socket.SocketMsg;
import com.wb.socket.SocketSynServer;

public class MyserverDB {
	private static int PORT = 8889;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{
			SocketSynServer syn_server = new SocketSynServer();
			new Thread(syn_server).start();
			ServerSocket server = new ServerSocket(PORT);
			System.out.println("Server start");
			while(true){
				try{					
					Socket request = server.accept();  //接受请求，后提交线程池处理
					System.out.println("Server accept success");
					SocketMsg sockMsg = new SocketMsg(SocketMsg.MSG_ACCEPT,request,"");
					syn_server.pushSocket(sockMsg);
				}catch(IOException ex){
					System.out.println(" Error accepting connect"+ ex);
				}
			}
		}
		catch(Exception e){
			System.out.println("Can not start Server"+ e);
		}
					
	}

}
