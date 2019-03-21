package com.wb.server;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
 
public class ProcessorRequest  implements Runnable {
 
	private File rootDirectory ;
	private String indexFileNname = "index.html" ;
	private Socket  connection;
	
	
	
	public ProcessorRequest(File directory ,String n ,Socket s){
		
		if(directory != null){
			this.rootDirectory = directory ;
		}
		if(n != null ){
			this.indexFileNname = n ;
		}
		this.connection = s ;
	}

	public ProcessorRequest(Socket s)
	{
		this.connection = s;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub	
		
		try{
			OutputStream rawOut = new BufferedOutputStream(connection.getOutputStream());	
			DataInputStream in = new DataInputStream(connection.getInputStream()) ;				
			StringBuilder requestLine = new StringBuilder();  //获得请求行
			while(true){				
				char c = (char) in.read();
				if(c == '\r' || c == '\n' ||c == -1){
					break;
				}
				requestLine.append(c);
			}
			String reqLine = requestLine.toString();		
			String [] tokens = reqLine.split("\\s+");
			String method = tokens[0];
			
			// if(method.equalsIgnoreCase("GET")){ //GET请求处理
						
			// 	doGet(in , rawOut , reqLine);
				
				
			// }else if(method.equalsIgnoreCase("POST")){ //POST请求处理
				
			// 	doPost(in , rawOut , reqLine);
				
			// }else{                           //其他请求，暂不处理，返回 <span style="font-family: Arial, Helvetica, sans-serif;">501</span>
 
				String fileName = tokens[1];				
				String version = null ;
				if(tokens.length > 2){
					version = tokens[2];
				}
				Writer out = new OutputStreamWriter(rawOut);
				String body = new StringBuilder("HTTP error 501 :Not Implemented \r\n").toString();
				if(version.startsWith("HTTP/")){
					senderHeader(out,"Http/1.0 501 Not Implemented ","text/html;charset=utf-8",body.length());
				}
				out.write(body);
				out.flush();
			// }
			
		}catch(Exception ex){
			System.out.println("Error of "+connection.getRemoteSocketAddress()+ex);
		}finally{
			try{
				connection.close();
			}catch(IOException E){
				
			}
		}
	} //处理Get请求，从服务器根目录寻找资源，并返回。如果提交的get请求有url参数，则转化参数Map对象，有待进一步根据需要处理。
	private void doGet(DataInputStream in , OutputStream out, String reqLine) throws IOException{
		
		String [] tokens = reqLine.split("\\s+");
		String filePath = tokens[1];
		String fileName = filePath;
		if(filePath.indexOf('?') != -1){
			String fileNm = fileName.substring(0,fileName.indexOf('?'));
			String parameters = fileName.substring(fileName.indexOf('?')+1, fileName.length());
			
			String [] pars = parameters.split("&");
			HashMap<String ,ArrayList<String>> parameterMap = new HashMap<String ,ArrayList<String>>();
			for(String s : pars){
				String[] kv = s.split("=");
				String key = null;
				String value = null;
				if(kv.length == 2 ){
					key = kv[0] ;
					value = kv[1];
				}else if(kv.length == 1 ){
					key = kv[0] ;
					value = "";
				}else{
					continue ;
				}
				ArrayList<String> values = parameterMap.get(key);
				if(values == null){
					values = new ArrayList<String>();
					values.add(value);
					parameterMap.put(key, values);
				}else{
					values.add(value);
				}
			}			
				fileName = fileNm;
				doGetWithParameter( in ,  out, fileName , parameterMap);							
			    return ;
		}else{
			if(fileName.endsWith("/")){
				fileName += indexFileNname;
			}
		}		
		String contentTpye = URLConnection.getFileNameMap().getContentTypeFor(fileName);//根据请求资源名，查询资源类型
		String version = null ;
		if(tokens.length > 2){
			version = tokens[2];
		}
		Writer outPut = new OutputStreamWriter(out);
		File theFile = new File(rootDirectory,fileName.substring(1, fileName.length()));
		if(theFile.canRead() && theFile.getCanonicalPath().startsWith(rootDirectory.getPath())&& !theFile.isDirectory()){ //quebao
			byte[] theData = Files.readAllBytes(theFile.toPath());
			if(version.startsWith("HTTP/")){
				senderHeader(outPut,"Http/1.0 200 OK",contentTpye,theData.length);
			}
			out.write(theData);
			out.flush();
		}else{
			String body = new StringBuilder("HTTP error 404 :File Not Found\r\n").toString();
			if(version.startsWith("HTTP/")){
				senderHeader(outPut,"Http/1.0 404 File Not Found","text/html;charset=utf-8",body.length());
			}
			outPut.write(body);
			outPut.flush();
			
		}
	}
	
	void doGetWithParameter( DataInputStream in , OutputStream out, String fileName ,HashMap<String ,ArrayList<String>> parameterMap) throws IOException{
		   
	}
private void doPost(DataInputStream in , OutputStream out, String reqLine) throws IOException{
				
		
		String [] tokens = reqLine.split("\\s+");
		String reqPath = tokens[1] ;
		HashMap<String ,String> headers = new HashMap<String ,String>();
		in.skip(1);	
//		StringBuilder line = new StringBuilder();
//		while(true){				
//			char c = (char) in.read();
//			if(c == '\r' || c == '\n' ){
//				break;
//			}
//			line.append(c);
//		}
//		String theLine = line.toString();
		String theLine = in.readLine();
        while (theLine != null) {
            System.out.println(theLine);         
            if ("".equals(theLine)) {
                break;
            }
            String [] headKV = theLine.split(": ");
            headers.put(headKV[0], headKV[1]);
            theLine = in.readLine();
        }
       Set<Entry<String, String>>entrys = headers.entrySet();
		for(Entry<String, String> h : entrys){
			if(h.getKey().equalsIgnoreCase("Content-Type")){
				if(h.getValue().contains("application/x-www-form-urlencoded")){
					doPostWithformUrlencoded( in ,  out , headers  );
					return ;
				}else if(h.getValue().contains("multipart/form-data")){
					doPoatWithMultiPart( in ,  out , headers );
					return ;
				}
			}			
		}
		Writer outPut = new OutputStreamWriter(out);
		String body = new StringBuilder("HTTP error 501 :Not Implemented \r\n").toString();
		String version = null ;
		if(tokens.length > 2){
			version = tokens[2];
		}
		if(version.startsWith("HTTP/")){
			senderHeader(outPut,"Http/1.0 501 Not Implemented ","text/html;charset=utf-8",body.length());
		}
		outPut.write(body);
		outPut.flush();
		
	}
	void doPostWithformUrlencoded(DataInputStream in , OutputStream out ,HashMap<String ,String> headers ) throws IOException{
		Writer outPut = new OutputStreamWriter(out);		
		int contentLength = 0 ;
		 Set<Entry<String, String>>entrys = headers.entrySet();
			for(Entry<String, String> h : entrys){
				if(h.getKey().equalsIgnoreCase("Content-Length")){
					contentLength =  Integer.parseInt(h.getValue());
					break ;
				}
				
			}
			if(contentLength != 0){
				
				byte []bodyContent = new byte[contentLength];
				int totalRed = 0 ;
				int size = 0 ;
				while(totalRed < contentLength){
				  size = 	in.read(bodyContent, totalRed, contentLength-totalRed) ;
				  totalRed += size;
				}
				String parameters = new String(bodyContent);
				String [] pars = parameters.split("&");
				HashMap<String ,ArrayList<String>> parameterMap = new HashMap<String ,ArrayList<String>>();
				for(String s : pars){
					String[] kv = s.split("=");
					String key = null;
					String value = null;
					if(kv.length == 2 ){
						key = kv[0] ;
						value = kv[1];
					}else if(kv.length == 1 ){
						key = kv[0] ;
						value = "";
					}else{
						continue ;
					}
					ArrayList<String> values = parameterMap.get(key);
					if(values == null){
						values = new ArrayList<String>();
						values.add(value);
						parameterMap.put(key, values);
					}else{
						values.add(value);
					}
				}
				StringBuilder body = new StringBuilder();
				body.append("<html><head><title>Test post with formUrlencoded</title></head><body><p>Post is ok</p></body></html>");			
				senderHeader(outPut,"Http/1.0 200 OK","text/html;charset=utf-8",body.length());
				outPut.write(body.toString());
				outPut.flush();
			}
		
	}
	
    void doPoatWithMultiPart(DataInputStream in , OutputStream outPut,HashMap<String ,String> headers  ) throws IOException{
    	int contentLength = 0 ;
    	String  boundary = null;
		 Set<Entry<String, String>>entrys = headers.entrySet();
			for(Entry<String, String> h : entrys){
				if(h.getKey().equalsIgnoreCase("Content-Length")){
					contentLength =  Integer.parseInt(h.getValue());					
				}
				if(h.getKey().equalsIgnoreCase("Content-Type")){
					boundary = h.getValue().substring(h.getValue().indexOf("boundary=")+9, h.getValue().length());
				}
			}
			
			 if (contentLength != 0) {
		          
		            byte[] buf = new byte[contentLength];
		            int totalRead = 0;
		            int size = 0;
		            while (totalRead < contentLength) {
		                size = in.read(buf, totalRead, contentLength - totalRead);
		                totalRead += size;
		            }
		           
		            String dataString = new String(buf, 0, totalRead);
		            System.out.println("the data user posted:/n" + dataString);
		            int pos = dataString.indexOf(boundary);
		          
		            pos = dataString.indexOf("\n", pos) + 1;
		            pos = dataString.indexOf("\n", pos) + 1;
		            pos = dataString.indexOf("\n", pos) + 1;
		            pos = dataString.indexOf("\n", pos) + 1;
		          
		            int start = dataString.substring(0, pos).getBytes().length;
		            pos = dataString.indexOf(boundary, pos) - 4;
		           
		            int end = dataString.substring(0, pos).getBytes().length;
		            
		            int fileNameBegin = dataString.indexOf("filename") + 10;
		            int fileNameEnd = dataString.indexOf("\n", fileNameBegin);
		            String fileName = dataString.substring(fileNameBegin, fileNameEnd);
		          
		            if(fileName.lastIndexOf("//")!=-1){
		                fileName = fileName.substring(fileName.lastIndexOf("//") + 1);
		            }
		            fileName = fileName.substring(0, fileName.length()-2);
		            OutputStream fileOut = new FileOutputStream(new File(rootDirectory,  fileName));
		            fileOut.write(buf, start, end-start);
		            fileOut.close();		         
		            
		            
					String body ="<html><head><title>Test upload </title></head><body><p>Post upload is ok</p></body></html>";
					Writer writer = new OutputStreamWriter(outPut);	
					senderHeader(writer,"Http/1.0 200 OK","text/html;charset=utf-8",body.length());
					writer.write(body.toString());
					writer.flush();
		        }
		      
	}
    
    
	void senderHeader(Writer out ,String responseCode  ,String contentType ,int length) throws IOException{
		out.write(responseCode+"\r\n");
		Date  now = new Date();
		out.write("Date: " + now +"\r\n");
		out.write("Server: MyHTTP 1.0\r\n");
		out.write("Content-length: "+length+"\r\n");
		out.write("Content-type: "+contentType+"\r\n\r\n");
		out.flush();
	}
	
}

	
