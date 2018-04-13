package org.foxconn.sendPcs;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.foxconn.dao.PcsDao;
import org.foxconn.entity.PcsResult;
import org.foxconn.util.ContextUtil;
import org.foxconn.util.LogUtil;
import org.foxconn.util.StringArrayUtil;
import org.foxconn.util.UploadFile;
import org.springframework.dao.DataAccessException;


	public class Run {
		public static void main(String[] args)  {
			Run run = new Run();
			try {
				run.createHP();
				run.createOther();
			} catch (Exception e) {
				LogUtil.writeXmlToLocalDisk("GetDataError:"+e.getMessage()+System.getProperty("line.separator"));
				e.printStackTrace();
			}
		}
		private void  createHP() throws Exception{
			PcsDao pcsDao= ContextUtil.getContext().getBean("pcsDao",PcsDao.class);
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("strFactoryID","GHGG");
			map.put("BAuto","1");
			List<List<?>> list =null;
			try {
				list =pcsDao.findHPAll(map);
			} catch (DataAccessException e) {
				throw e;
			}
			writeFile(list);
		}
		private void  createOther() throws Exception{
			PcsDao pcsDao= ContextUtil.getContext().getBean("pcsDao",PcsDao.class);
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("strFactoryID","GHGG");
			map.put("BAuto","1");
			List<List<?>> list =null;
			try {
				list =pcsDao.findOtherAll(map);
			} catch (DataAccessException e) {
				throw e;
			}
			writeFile(list);
		}
		
		private  void writeFile(List<List<?>> list) throws Exception{
			List<PcsResult> resultList = (List<PcsResult>)list.get(1);
			String  filename = list.get(0).toString().replaceAll("[\\[|\\]]", "");
			//System.out.println(filename);
			//System.out.println(resultList);
			System.out.println("has collected data, total  lines:"+String.valueOf(resultList.size()));
			if ("".equals(filename)){
				LogUtil.writeXmlToLocalDisk("file name is null"+System.getProperty("line.separator"));
			}
			Map<String,String> map = new HashMap<String, String>();
			StringBuilder msg=new StringBuilder("");
			long index =0l;
			for(PcsResult PcsResult:resultList){
				msg=msg.append(PcsResult.getWriteline()+System.getProperty("line.separator"));
				if(index++%500==0){
					System.out.println("append string ...current line:"+String.valueOf(index));
				}
				if(null==map.get(PcsResult.getSsn())){
					map.put(PcsResult.getSsn(), PcsResult.getSsn());
				}
			}
//			String ftp1Msg =msg.toString();
//			String ftp2Msg = ftp1Msg;
			if(!"".equals(msg.toString())){
				LogUtil.writeString("D:\\seagatePcs\\backup\\",filename,msg.toString());
				LogUtil.writeXmlToLocalDisk("has backuped to local disk"+System.getProperty("line.separator"));
				System.out.println("backup to local disk:"+"D:\\seagatePcs\\backup"+filename);
				InputStream  inputStream=  new ByteArrayInputStream(msg.toString().getBytes("UTF-8"));
				String uploadMsg="false";
				String uploadMsgBack="false";
				String strSSNS ="";
				try {
					uploadMsg = UploadFile.uploadFile("134.204.240.44", 21,"foxconn_tianjin","2496w29286456597198T", "/",filename,inputStream );
//					uploadMsg = UploadFile.uploadFile("10.67.70.95", 21,"it","8293584", "/pcs1/",filename,inputStream );
				} catch (Exception e) {
					uploadMsg = uploadMsg+"-->"+e.getMessage();
				}
				InputStream  inputStream2=  new ByteArrayInputStream(msg.toString().getBytes("UTF-8"));
				try {
					uploadMsgBack = UploadFile.uploadFile("10.67.70.95", 21,"it","8293584", "/pcs/",filename,inputStream2 );
				} catch (Exception e) {
					uploadMsgBack = uploadMsgBack+"-->"+e.getMessage();
				}
				if(uploadMsg.indexOf("false")!=-1){//如果上传ftp失败，改回状态
					PcsDao pcsDao= ContextUtil.getContext().getBean("pcsDao",PcsDao.class);
					strSSNS = StringArrayUtil.addDHAndFh(map.values());
					Map<String,String> ssnMap = new HashMap<String, String>();
					ssnMap.put("sns", strSSNS);
					pcsDao.updateSSNStatus(ssnMap);
					LogUtil.writeXmlToLocalDisk("upload ftp failed,rollback pcs status,It will be upload again tomorrow"+System.getProperty("line.separator"));
					String deleteFlag = UploadFile.deleteFile("134.204.240.44", 21,"foxconn_tianjin","2496w29286456597198T", "/",filename );
					System.out.println(deleteFlag);
					LogUtil.writeXmlToLocalDisk("134.204.240.44:delete,"+deleteFlag+System.getProperty("line.separator"));
				}
				
				if(uploadMsgBack.indexOf("false")!=-1){//如果上传ftp失败，改回状态
					String deleteFlagBackup=UploadFile.deleteFile("10.67.70.95", 21,"it","8293584", "/pcs/",filename );
					LogUtil.writeXmlToLocalDisk("10.67.70.95:delete,"+deleteFlagBackup+System.getProperty("line.separator"));
				}
				LogUtil.writeXmlToLocalDisk("upload to ftp:134.204.240.44"+uploadMsg+System.getProperty("line.separator"));
				LogUtil.writeXmlToLocalDisk("upload to ftp:10.67.70.95"+uploadMsgBack+System.getProperty("line.separator"));
				System.out.println("upload to ftp:134.204.240.44"+uploadMsg);
				System.out.println("upload to ftp:10.67.70.95"+uploadMsgBack);
			}
			LogUtil.writeXmlToLocalDisk("success-->create,back up  and upload pcs file ok:size"+resultList.size()+",filename:"+filename+System.getProperty("line.separator")+System.getProperty("line.separator"));
		}
	}

