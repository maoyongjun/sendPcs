package org.foxconn.sendPcs;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.foxconn.config.Config;
import org.foxconn.dao.PcsDao;
import org.foxconn.entity.PcsResult;
import org.foxconn.util.ContextUtil;
import org.foxconn.util.LogUtil;
import org.foxconn.util.StringArrayUtil;
import org.foxconn.util.UploadFile;
import org.springframework.dao.DataAccessException;

public class Run {
	Config config = new Config();
	
	private void initProp() throws IOException{
		File file = new File("");
		Properties prop = new Properties();  
        FileInputStream fis = new FileInputStream(file.getAbsolutePath()+"/"+"config.properties");  
        prop.load(fis); 
    	System.out.println(file.getAbsolutePath());
		System.out.println(file.getCanonicalPath());
        config.setIp((String)(prop.get("ip")));
        config.setPort(Integer.parseInt((String)prop.get("port")));
        config.setPath((String)(prop.get("path")));
        config.setUsername((String)(prop.get("username")));
        config.setPassword((String)(prop.get("password")));
        
        config.setBackupIP((String)(prop.get("backupIP")));
        config.setBackupPort(Integer.parseInt((String)prop.get("backupPort")));
        config.setBackuppath((String)(prop.get("backuppath")));
        config.setBackupusername((String)(prop.get("backupusername")));
        config.setBackuppassword((String)(prop.get("backuppassword")));
        config.setBackupLocalPath((String)(prop.get("backupLocalPath")));
        
        
        String ip=config.getIp();
		int port = config.getPort();
		String username =config.getUsername();
		String password=config.getPassword();
		String path=config.getPath();
		
		String backupIP=config.getBackupIP();
		int backupPort=config.getBackupPort();
		String backupusername=config.getBackupusername();
		String backuppassword=config.getBackuppassword();
		String backuppath=config.getBackuppath();
		String backupLocalPath = config.getBackupLocalPath();
		System.out.println("ip"+ip+"port:"+port+",username:"+username+",password:"+password+",path:"+path);
		System.out.println("ip"+backupIP+"port:"+backupPort+",username:"+backupusername+",password:"+backuppassword+",path:"+backuppath);
		System.out.println("backupLocalPath:"+ backupLocalPath);
	}
	public static void main(String[] args) {
		Run run = new Run();
		try {
			run.initProp();
			run.createHP();
			run.createOther();
		} catch (Exception e) {
			LogUtil.writeXmlToLocalDisk("GetDataError:" + e.getMessage() + System.getProperty("line.separator"));
			e.printStackTrace();
		}
	}

	private void createHP() throws Exception {
		PcsDao pcsDao = ContextUtil.getContext().getBean("pcsDao", PcsDao.class);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("strFactoryID", "GHGG");
		map.put("BAuto", "1");
		List<List<?>> list = null;
		try {
			list = pcsDao.findHPAll(map);
		} catch (DataAccessException e) {
			throw e;
		}
		writeFile(list);
	}

	private void createOther() throws Exception {
		PcsDao pcsDao = ContextUtil.getContext().getBean("pcsDao", PcsDao.class);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("strFactoryID", "GHGG");
		map.put("BAuto", "1");
		List<List<?>> list = null;
		try {
			list = pcsDao.findOtherAll(map);
		} catch (DataAccessException e) {
			throw e;
		}
		writeFile(list);
	}

	private void writeFile(List<List<?>> list) throws Exception {
		List<PcsResult> resultList = (List<PcsResult>) list.get(1);
		String filename = list.get(0).toString().replaceAll("[\\[|\\]]", "");
		// System.out.println(filename);
		// System.out.println(resultList);
		System.out.println("has collected data, total  lines:" + String.valueOf(resultList.size()));
		if ("".equals(filename)) {
			LogUtil.writeXmlToLocalDisk("file name is null" + System.getProperty("line.separator"));
		}
		Map<String, String> map = new HashMap<String, String>();
		StringBuilder msg = new StringBuilder("");
		long index = 0l;
		for (PcsResult PcsResult : resultList) {
			msg = msg.append(PcsResult.getWriteline() + System.getProperty("line.separator"));
			if (index++ % 500 == 0) {
				System.out.println("append string ...current line:" + String.valueOf(index));
			}
			if (null == map.get(PcsResult.getSsn())) {
				map.put(PcsResult.getSsn(), PcsResult.getSsn());
			}
		}
		// String ftp1Msg =msg.toString();
		// String ftp2Msg = ftp1Msg;
		if (!"".equals(msg.toString())) {
			String backupLocalPath = config.getBackupLocalPath();
			LogUtil.writeString(backupLocalPath, filename, msg.toString());
			LogUtil.writeXmlToLocalDisk("has backuped to local disk" + System.getProperty("line.separator"));
			System.out.println("backup to local disk:" + backupLocalPath + filename);
			InputStream inputStream = new ByteArrayInputStream(msg.toString().getBytes("UTF-8"));
			String uploadMsg = "false";
			String uploadMsgBack = "false";
			String strSSNS = "";
			String ip=config.getIp();
			int port = config.getPort();
			String username =config.getUsername();
			String password=config.getPassword();
			String path=config.getPath();
			
			String backupIP=config.getBackupIP();
			int backupPort=config.getBackupPort();
			String backupusername=config.getBackupusername();
			String backuppassword=config.getBackuppassword();
			String backuppath=config.getBackuppath();
			try {
				uploadMsg = UploadFile.uploadFile(ip, port, username, password
						,path,
						filename, inputStream);
			} catch (Exception e) {
				uploadMsg = uploadMsg + "-->" + e.getMessage();
			}
			InputStream inputStream2 = new ByteArrayInputStream(msg.toString().getBytes("UTF-8"));
			try {
				uploadMsgBack = UploadFile.uploadFile(backupIP, backupPort, backupusername, backuppassword, backuppath, filename,
						inputStream2);
			} catch (Exception e) {
				uploadMsgBack = uploadMsgBack + "-->" + e.getMessage();
			}
			if (uploadMsg.indexOf("false") != -1) {// 如果上传ftp失败，改回状态
				PcsDao pcsDao = ContextUtil.getContext().getBean("pcsDao", PcsDao.class);
				strSSNS = StringArrayUtil.addDHAndFh(map.values());
				Map<String, String> ssnMap = new HashMap<String, String>();
				ssnMap.put("sns", strSSNS);
				pcsDao.updateSSNStatus(ssnMap);
				LogUtil.writeXmlToLocalDisk("upload ftp failed,rollback pcs status,It will be upload again tomorrow"
						+ System.getProperty("line.separator"));
				String deleteFlag = UploadFile.deleteFile(ip, 21, username,
						password, "/", filename);
				System.out.println(deleteFlag);
				LogUtil.writeXmlToLocalDisk(
						ip+":delete," + deleteFlag + System.getProperty("line.separator"));
			}

			if (uploadMsgBack.indexOf("false") != -1) {// 如果上传ftp失败，改回状态
				String deleteFlagBackup = UploadFile.deleteFile(backupIP, backupPort, backupusername, backuppassword, backuppath, filename);
				LogUtil.writeXmlToLocalDisk(
						backupIP+":delete," + deleteFlagBackup + System.getProperty("line.separator"));
			}
			LogUtil.writeXmlToLocalDisk(
					"upload to ftp:"+ip + uploadMsg + System.getProperty("line.separator"));
			LogUtil.writeXmlToLocalDisk(
					"upload to ftp:"+backupIP + uploadMsgBack + System.getProperty("line.separator"));
			System.out.println("upload to ftp:"+ip + uploadMsg);
			System.out.println("upload to ftp:"+backupIP + uploadMsgBack);
		}
		LogUtil.writeXmlToLocalDisk(
				"success-->create,back up  and upload pcs file ok:size" + resultList.size() + ",filename:" + filename
						+ System.getProperty("line.separator") + System.getProperty("line.separator"));
	}
}
