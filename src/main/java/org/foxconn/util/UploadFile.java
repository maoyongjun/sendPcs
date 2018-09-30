package org.foxconn.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

public class UploadFile {

	public static String uploadFile(String url, int port, String username, String password, String path,
			String filename, InputStream input) throws Exception {
		String storeFileSuccess = "false";
		FTPClient ftp = new FTPClient();
		ftp.setControlEncoding("utf-8");
		try {
			int reply;
			ftp.connect(url, port);

			ftp.login(username, password);
			reply = ftp.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftp.disconnect();
				return "connect false" + reply;
			}
			ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftp.makeDirectory(path);
			ftp.changeWorkingDirectory(path);
			ftp.enterLocalPassiveMode();// 设置为被动模式
			ftp.setFileTransferMode(FTP.STREAM_TRANSFER_MODE);
			storeFileSuccess = ftp.storeFile(filename, input) + "";
			storeFileSuccess += ftp.getReplyCode();// 获取状态码
			input.close();
			ftp.logout();

		} catch (IOException e) {
			// e.printStackTrace();
			return "store File:" + storeFileSuccess + "--->" + e.getMessage();
		} finally {
			if (ftp.isConnected()) {
				try {
					ftp.disconnect();
				} catch (IOException ioe) {
				}
			}
		}
		return "store File:" + storeFileSuccess;
	}

	public static String deleteFile(String url, int port, String username, String password, String path,
			String filename) {
		boolean success = false;
		FTPClient ftp = new FTPClient();
		ftp.setControlEncoding("utf-8");
		int reply = 0;
		try {
			ftp.connect(url, port);
			ftp.login(username, password);
			reply = ftp.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftp.disconnect();
				return "delete file -->" + success + ":" + reply;
			}
			ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftp.changeWorkingDirectory(path);
			ftp.enterLocalPassiveMode();// 设置为被动模式
			ftp.setFileTransferMode(FTP.STREAM_TRANSFER_MODE);
			success = ftp.deleteFile(filename);
			reply = ftp.getReplyCode();
			ftp.logout();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (ftp.isConnected()) {
				try {
					ftp.disconnect();
				} catch (IOException ioe) {
				}
			}
		}
		return "delete file -->" + success + ":" + reply;
	}

	public static void upLoadFromProduction(String url, int port, String username, String password, String path,
			String filename, String orginfilename) {
		try {
			FileInputStream in = new FileInputStream(new File(orginfilename));
			String msg = uploadFile(url, port, username, password, path, filename, in);
			System.out.println(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String deleteFromProduction(String url, int port, String username, String password, String path,
			String filename) {
		try {
			String flag = deleteFile(url, port, username, password, path, filename);
			System.out.println(flag);
			return flag;
		} catch (Exception e) {
			e.printStackTrace();
			return "false:" + e.getMessage();
		}
	}

	public static void main(String[] args) throws UnsupportedEncodingException {

		String msg = "中文字符测试 abc";
		String filename = "testFile.txt";
		InputStream inputStream = new ByteArrayInputStream(msg.getBytes("UTF-8"));
		try {
			// UploadFile.uploadFile("134.204.240.44",
			// 21,"foxconn_tianjin","2496w29286456597198T",
			// "/test",filename,inputStream );
			String result = UploadFile.uploadFile("10.67.68.65", 21, "fcadmin", "Foxcon99", "/test", filename,
					inputStream);
			// if(result.indexOf("true")!=-1){
			String deleteresult = deleteFile("10.67.68.65", 21, "fcadmin", "Foxcon99", "/test", filename);
			System.out.println(deleteresult);
			// }
			// System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
			//LogUtil.writeXmlToLocalDisk(e.getMessage() + "\n");
		}
	}
}
