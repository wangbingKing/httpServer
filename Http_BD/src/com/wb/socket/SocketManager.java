package com.wb.socket;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SocketManager {
	Map<InetAddress,SocketMsg> socket_map = null;
	List<SocketMsg> readList = null;
	List<SocketMsg> writeList = null;
	public SocketManager()
	{
		socket_map = new HashMap<InetAddress, SocketMsg>();
		readList = new ArrayList<SocketMsg>();
		writeList = new ArrayList<SocketMsg>();
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
}
