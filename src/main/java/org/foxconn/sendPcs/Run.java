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
import org.foxconn.util.BasicFtpClient;
import org.foxconn.util.ContextUtil;
import org.foxconn.util.FileLog;
import org.foxconn.util.StringArrayUtil;
import org.foxconn.util.UploadFile;

public class Run extends FileLog{
	public static Config config = new Config();
	private BasicFtpClient client;
	private BasicFtpClient backupClient;
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
        config.setLoggerType((String)(prop.get("loggerType")));
  }
	public static void main(String[] args) {
		Run run = new Run();
		try {
			run.initProp();
			run.client = new BasicFtpClient(config.getIp(), config.getPort(), config.getUsername(), config.getPassword(), config.getPath());
			run.backupClient = new BasicFtpClient(config.getBackupIP(), config.getBackupPort(), config.getBackupusername(), config.getBackuppassword(), config.getBackuppath());
			if(run.client.getConnectStatus()){
				run.runPcs();
			}else{
				run.logError("connect ftp server Error!");
			}
		} catch (Exception e) {
			run.logError("RunPcsError:" + e.getMessage());
		}finally {
			if(run.client!=null){
				run.client.closeServer();
			}
			if(run.backupClient!=null){
				run.backupClient.closeServer();
			}
		}
	}

	private void runPcs() throws Exception {
		PcsDao pcsDao = ContextUtil.getContext().getBean("pcsDao", PcsDao.class);
		String types = config.getTypes();
		String makeFile = config.getMakeFile();
		List<List<?>> singleFilePcsList = null;
		for(String type :types.split(",")){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("transType", type);
			logInfo("get pcs Data transType:" +type );
			List<List<?>> thisList = null;
			try {
				//thisList= pcsDao.findAll(map);
			} catch (Exception e) {
				if(e.getMessage()!=null&&e.getMessage().indexOf("99999")!=-1){
					logError("GetDataError:" + "today has no data " );
				}else{
					logError("GetDataError:" + e.getMessage() );
				}
			}
			if("each".equals(makeFile)){
				writeFile(thisList);//单独分开发送
			}else if("single".equals(makeFile)){
				singleFilePcsList = addPcsList(singleFilePcsList,thisList);
			}
		}
		//single
		if(singleFilePcsList!=null){
			writeFile(singleFilePcsList);//合成一个文件发送
		}
	}
	//多个pcs结果，进行汇总
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

	//发送pcs和备份pcs
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
			boolean uploadMsg =false;
			boolean uploadMsgBack = false;
			String strSSNS = "";
			client.uploadFile(filename, inputStream);
			InputStream inputStream2 = new ByteArrayInputStream(msg.toString().getBytes("UTF-8"));
			backupClient.uploadFile(filename, inputStream2);
			if (!uploadMsg) {// 如果上传ftp失败，改回状态
				PcsDao pcsDao = ContextUtil.getContext().getBean("pcsDao", PcsDao.class);
				strSSNS = StringArrayUtil.addDHAndFh(map.values());
				Map<String, String> ssnMap = new HashMap<String, String>();
				ssnMap.put("sns", strSSNS);
				pcsDao.updateSSNStatus(ssnMap);
				logInfo("upload ftp failed,rollback pcs status,It will be upload again tomorrow");
				logInfo(config.getIp()+":delete," + client.deleteFile(filename) );
			}

			if (!uploadMsgBack) {// 如果上传ftp失败，改回状态
				logInfo(config.getBackupIP()+":delete," + backupClient.deleteFile(filename));
			}
			logInfo("upload to ftp:"+config.getIp() + uploadMsg );
			logInfo("upload to ftp:"+config.getBackupIP() + uploadMsgBack);
		}
		logInfo("success-->create,back up  pcs file :size" + resultList.size() + ",filename:" + filename
						+ System.getProperty("line.separator"));
	}
}
