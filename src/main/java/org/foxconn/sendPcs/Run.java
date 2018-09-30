package org.foxconn.sendPcs;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.foxconn.config.Config;
import org.foxconn.dao.PcsDao;
import org.foxconn.entity.PcsResult;
import org.foxconn.util.ContextUtil;
import org.foxconn.util.FileLog;
import org.foxconn.util.StringArrayUtil;
import org.foxconn.util.UploadFile;

public class Run extends FileLog{
	public static Config config = new Config();
	private void initProp() throws IOException{
		File file = new File("");
		Properties prop = new Properties();  
        FileInputStream fis = new FileInputStream(file.getAbsolutePath()+"/"+"config.properties");  
        prop.load(fis); 
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
        config.setTypes((String)(prop.get("types")));
        config.setMakeFile((String)(prop.get("makeFile")));
	}
	public static void main(String[] args) {
		Run run = new Run();
		try {
			run.initProp();
			run.findAll();
		} catch (Exception e) {
			run.logError("GetDataError:" + e.getMessage());
			e.printStackTrace();
		}
	}

	private void findAll() throws Exception {
		PcsDao pcsDao = ContextUtil.getContext().getBean("pcsDao", PcsDao.class);
		String types = config.getTypes();
		String makeFile = config.getMakeFile();
		List<List<?>> list = null;
		for(String type :types.split(",")){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("transType", type);
			logInfo("get pcs Data transType:" +type );
			List<List<?>> thisList = null;
			try {
				thisList= pcsDao.findAll(map);
			} catch (Exception e) {
				if(e.getMessage()!=null&&e.getMessage().indexOf("99999")!=-1){
					logError("GetDataError:" + "today has no data " );
				}else{
					logError("GetDataError:" + e.getMessage() );
				}
			}
			if("each".equals(makeFile)){
				writeFile(thisList);
			}else if("single".equals(makeFile)){
				list = addPcsList(list,thisList);
			}
		}
		//single
		if(list!=null){
			writeFile(list);
		}
	}
	private List<List<?>>  addPcsList(List<List<?>> totalList,List<List<?>> thisList){
		List<String> fileName=null;
		List<PcsResult> pcsResults=null;
		if(totalList==null){
			totalList= new ArrayList<List<?>>();
		}
		if(totalList.size()==0){
			fileName = new ArrayList<String>();
			totalList.add(fileName);
		}
		if(totalList.size()==1){
			pcsResults = new ArrayList<PcsResult>();
			totalList.add(pcsResults);
		}
		if(totalList.size()>=2){
			//如果没有文件名
			if(totalList.get(0).size()==0){
				((List<String>)totalList.get(0)).addAll((List<String>) thisList.get(0));
			}
			//如果有pcs的结果
			if(thisList.size()>=2){
				List<PcsResult> resultList = (List<PcsResult>) thisList.get(1);
				((List<PcsResult>)totalList.get(1)).addAll(resultList);
			}
		}
		return totalList;
	}

	private void writeFile(List<List<?>> list) throws Exception {
		String filename ="no file Name";
		
		if(list!=null&&list.size()>0){
			try {
				filename = list.get(0).toString().replaceAll("[\\[|\\]]", "");
			} catch (Exception e) {
				filename = list.toString().replaceAll("[\\[|\\]]", "");
			}
			
		}
		if(null==list||list.size()<2){
			logInfo("this transtype has no data,fileName:" +filename+System.getProperty("line.separator"));
			return ;
		}
		List<PcsResult> resultList = (List<PcsResult>) list.get(1);
		
		System.out.println("has collected data, total  lines:" + String.valueOf(resultList.size()));
		if ("".equals(filename)) {
			logInfo("file name is null" );
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
		if (!"".equals(msg.toString())) {
			String backupLocalPath = config.getBackupLocalPath()+"backup\\";
			writeString(backupLocalPath, filename, msg.toString());
			logInfo("has backuped to local disk");
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
				logInfo("upload ftp failed,rollback pcs status,It will be upload again tomorrow");
				String deleteFlag = UploadFile.deleteFile(ip, 21, username,
						password, "/", filename);
				logInfo(ip+":delete," + deleteFlag );
			}

			if (uploadMsgBack.indexOf("false") != -1) {// 如果上传ftp失败，改回状态
				String deleteFlagBackup = UploadFile.deleteFile(backupIP, backupPort, backupusername, backuppassword, backuppath, filename);
				logInfo(backupIP+":delete," + deleteFlagBackup);
			}
			logInfo("upload to ftp:"+ip + uploadMsg );
			logInfo("upload to ftp:"+backupIP + uploadMsgBack);
		}
		logInfo("success-->create,back up  pcs file :size" + resultList.size() + ",filename:" + filename
						+ System.getProperty("line.separator"));
	}
}
