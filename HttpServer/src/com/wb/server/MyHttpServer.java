package com.wb.server;


import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
 
public class MyHttpServer {
	
		
	private static final int NUM_THREADS = 50 ; //线程池线程数量
	private static final  String INDEX_FILE = "index.html" ;  //服务器首页索引文件名
 
	
	private final File  rootDirectory ;  //服务器根目录，服务器资源都放在该目录下
	private int port = 8088 ;  //服务器端口号   默认设置为80
		
	
	public MyHttpServer(File rootDirectory , int port) throws IOException{
		
		if(!rootDirectory.isDirectory()){
			throw new IOException(rootDirectory + "is not a directory");
		}
		this.rootDirectory = rootDirectory ;
		this.port = port ;
		
	}
	
	public void start() throws IOException{  //启动服务器
		ExecutorService  pool = Executors.newFixedThreadPool(NUM_THREADS); //服务器工作线程池
		try(ServerSocket server = new ServerSocket(port)){
			while(true){
				try{					
					Socket request = server.accept();  //接受请求，后提交线程池处理
					Runnable r = new ProcessorRequest(rootDirectory,INDEX_FILE,request);
					pool.submit(r);
				}catch(IOException ex){
					System.out.println(" Error accepting connect"+ ex);
				}
			}
								
		}
		
	}
	public static void main(String[] args) {   //服务器主函数，
		File  docRoot ;
		
		try{
			docRoot = new File(args[0]);  //解析参数，确定服务器根目录
			if(!docRoot.isDirectory()){
				System.out.println("Error , docRoot is not a directory");
				return ;
			}
		}catch(ArrayIndexOutOfBoundsException ex){
			System.out.println("Please input docRoot name");
			return;
		}
		int port ;
		try{
			port = Integer.parseInt(args[1]); //解析参数 ，获取端口号
			
		}catch(RuntimeException e){
			port = 80 ;
		}
		try{
			MyHttpServer httpServer = new MyHttpServer(docRoot, port);
			httpServer.start();
		}catch(IOException e){
			System.out.println("Can not start Server"+ e);
		}
	}
	
}
