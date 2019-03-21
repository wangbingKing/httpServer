package com.wb.server;


import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.wb.socket.SocketClient;
 
public class MyHttpServer {
	
		
	private static final int NUM_THREADS = 50 ; //线程池线程数量
	private int port = 8888 ;  //服务器端口号   默认设置为80
	public MyHttpServer(int port) throws Exception{
		this.port = port ;
	}
	
	public void start() throws IOException{  //启动服务器
		ExecutorService  pool = Executors.newFixedThreadPool(NUM_THREADS); //服务器工作线程池
		try(ServerSocket server = new ServerSocket(port)){
			System.out.println(" create server success");
			while(true){
				try{					
					Socket request = server.accept();  //接受请求，后提交线程池处理
					Runnable r = new ProcessorRequest(request);
					pool.submit(r);
				}catch(IOException ex){
					System.out.println(" Error accepting connect"+ ex);
				}
			}
								
		}
		
	}
	public static void main(String[] args) {   //服务器主函数，
		
		// try{
		// 	MyHttpServer httpServer = new MyHttpServer(8888);
		// 	httpServer.start();
		// }catch(Exception e){
		// 	System.out.println("Can not start Server"+ e);
		// }

		System.out.println(" client start connect");
		try{					
			SocketClient client = SocketClient.getInstance();
			client.connect("127.0.0.1", 8889);
			System.out.println(" client connect");
			while(true)
			{	
				long startTime=System.nanoTime();   //获取开始时间
				
				try{					
					Thread.sleep(100);
				}catch(Exception ex){
					System.out.println(" Error accepting connect"+ ex);
				}
				long endTime=System.nanoTime(); //获取结束时间
				// System.out.println(endTime - startTime);
				client.update(0.1f);
			}
		}catch(Exception ex){
			System.out.println(" Error accepting connect"+ ex);
		}
	}
	
}
