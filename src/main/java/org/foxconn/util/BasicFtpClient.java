package org.foxconn.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.http.ParseException;

public class BasicFtpClient extends FileLog{
	private FTPClient ftpClient; 
	private String fileName, strencoding; 
	private String ip = "192.168.43.72";        // 服务器IP地址 
	private String userName = "share";        // 用户名 
	private String userPwd = "1234";        // 密码 
	private int port = 21;      // 端口号 
	private String path = "/test";        // 读取文件的存放目录 
	 
	/** 
	  * init ftp servere 
	  */ 
	public BasicFtpClient() { 
	  init(); 
	} 
	
	public BasicFtpClient(String ip,int port,String userName,String userPwd,String path){
		strencoding = "UTF-8"; 
		this.connectServer(ip, port, userName, userPwd, path); 
	}
	public void init() { 
	  // 以当前系统时间拼接文件名 
	  fileName = "20131112114850793835861000010161141169.txt"; 
	  strencoding = "UTF-8"; 
	  this.connectServer(ip, port, userName, userPwd, path); 
	} 
	 
	/** 
	  * @param ip 
	  * @param port 
	  * @param userName 
	  * @param userPwd 
	  * @param path 
	  * @throws SocketException 
	  * @throws IOException function:连接到服务器 
	  */ 
	public void connectServer(String ip, int port, String userName, String userPwd, String path) {
		ftpClient = new FTPClient();
		try {
			ftpClient.setControlEncoding(strencoding);
			
			// 连接
			ftpClient.connect(ip, port);
			// 登录
			ftpClient.login(userName, userPwd);
			
			
			int reply = ftpClient.getReplyCode();
			
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftpClient.disconnect();
				System.out.println("login fail:"+reply);
				return;
			}else{
				System.out.println("login success:"+reply);
			}
			
			ftpClient.enterLocalPassiveMode();
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftpClient.setFileTransferMode(FTP.STREAM_TRANSFER_MODE);
			
		
			if (path != null && path.length() > 0) {
				// 跳转到指定目录
				ftpClient.changeWorkingDirectory(path);
			}
		} catch (SocketException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	/** 
	  * @throws IOException function:关闭连接 
	  */ 
	public void closeServer() { 
	  if (ftpClient.isConnected()) { 
	   try { 
	    ftpClient.logout(); 
	    ftpClient.disconnect(); 
	   } catch (IOException e) { 
		   System.out.println(e.getMessage());
	   } 
	  } 
	} 
	 
	/** 
	  * @param path 
	  * @return function:读取指定目录下的文件名 
	  * @throws IOException 
	  */ 
	public List<String> getFileList(String path) { 
	  List<String> fileLists = new ArrayList<String>(); 
	  // 获得指定目录下所有文件名 
	  FTPFile[] ftpFiles = null; 
	  try { 
	   ftpFiles = ftpClient.listFiles(path); 
	  } catch (IOException e) { 
	   e.printStackTrace(); 
	  } 
	  for (int i = 0; ftpFiles != null && i < ftpFiles.length; i++) { 
	   FTPFile file = ftpFiles[i]; 
	   if (file.isFile()) { 
	    fileLists.add(file.getName()); 
	   } 
	  } 
	  return fileLists; 
	} 
	 
	/** 
	  * @param fileName 
	  * @return function:从服务器上读取指定的文件 
	  * @throws ParseException 
	  * @throws IOException 
	  */ 
	public String readFile(String fileName) throws ParseException { 
	  InputStream ins = null; 
	  StringBuilder builder = null; 
	  try { 
	   // 从服务器上读取指定的文件 
	   ins = ftpClient.retrieveFileStream(fileName); 
	   BufferedReader reader = new BufferedReader(new InputStreamReader(ins, strencoding)); 
	   String line; 
	   builder = new StringBuilder(150); 
	   while ((line = reader.readLine()) != null) { 
	    builder.append(line); 
	   } 
	   reader.close(); 
	   if (ins != null) { 
	    ins.close(); 
	   } 
	   // 主动调用一次getReply()把接下来的226消费掉. 这样做是可以解决这个返回null问题 
	   ftpClient.getReply(); 
	  } catch (IOException e) { 
		  System.out.println(e.getMessage());
	  } 
	  return builder.toString(); 
	} 
	 
	/** 
	  * @param fileName function:删除文件 
	  */ 
	public void deleteFile(String fileName) { 
	  try { 
	   ftpClient.deleteFile(fileName); 
	  } catch (IOException e) { 
	   e.printStackTrace(); 
	  } 
	} 
	
	public void uploadFile(String filename, InputStream input) {
		String storeFileSuccess = "false";
		try {
			storeFileSuccess = ftpClient.storeFile(filename, input) + "";
		} catch (IOException e) {
			//e.printStackTrace();
			storeFileSuccess+=e.getMessage();
		}
		System.out.println("store:"+storeFileSuccess);

	}
}
