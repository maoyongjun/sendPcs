package org.foxconn.ftpupload;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import org.foxconn.util.BasicFtpClient;
import org.foxconn.util.UploadFile;
import org.junit.Test;

public class TestFileUpload {
	private int count =50;
	
	@Test
	public void test2(){
		BasicFtpClient client = new BasicFtpClient();
		List<String> files = client.getFileList("");
		String msg ="";
//		System.out.println(files);
		for(String file :files){
			 String result = client.readFile(file);
			 if(msg.equals("")){
				 msg = result;
			 }else{
				 System.out.println(msg.equals(result));
			 }
			 
		}
//		System.out.println(msg);
//		System.out.println(client.readFile("abc.txt"));
		client.closeServer();
	}
	
	@Test
	public void test() throws Exception{
		
		CountDownLatch cdl = new CountDownLatch(count);
		CountDownLatch cd2 = new CountDownLatch(count);
		List<Runnable> list = new ArrayList<Runnable>();
		for(int i=0;i<count;i++){
			
			Runnable run = new Runnable() {
				
				@Override
				public void run() {
					try {
						cdl.countDown();
						cdl.await();
						long i = new Date().getTime();
						String index = String.valueOf(i);
						uploadFile(index);
						cd2.countDown();
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}
			};
			list.add(run);
		}
		List<Thread> threads = new ArrayList<Thread>();
		for(int i=0;i<count;i++){
			Thread t = new Thread(list.get(i));
			threads.add(t);
		}
		for(int i=0;i<count;i++){
			Thread t = threads.get(i);
			t.start();
		}
		cd2.await();
	}
	private void uploadFile(String index) throws Exception{
		UUID  uuid = UUID.randomUUID();
		String url ="192.168.43.72";
		int port =21;
		String username ="share";
		String password="1234";
		String path="/";
		SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-DD HH:mm:ss SSS");
		String filename="测试"+sdf.format(new Date())+"_"+uuid.toString()+".txt";
		System.out.println(sdf.format(new Date())+"---->"+filename);
		
		//并发上传100个文件。
		File file = new File("测试文件.txt");
		FileInputStream fis = new FileInputStream(file);
		byte[] bytes = new byte[fis.available()];
		fis.read(bytes);
		String msg = new String(bytes,"UTF-8");
		InputStream input = new ByteArrayInputStream(msg.toString().getBytes("UTF-8"));
//		InputStream input = new InflaterInputStream(fis);
		BasicFtpClient client = new BasicFtpClient();
		 client.uploadFile(filename, input);
		//校验文件内容是否完整
		
	}
}
