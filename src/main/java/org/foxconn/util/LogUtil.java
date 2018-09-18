package org.foxconn.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.foxconn.sendPcs.Run;

public class LogUtil {

	public static boolean writeXmlToLocalDisk(String strs) {
		if (null == strs || "".equals(strs)) {
			return false;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM");
		SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd");
		String baseLocalDir = Run.config.getBackupLocalPath()+"log\\" + sdf2.format(new Date());
		File file = new File(baseLocalDir);
		if (!file.exists()) {
			file.mkdirs();
		}
		return writeString(baseLocalDir, sdf3.format(new Date()) + ".txt", sdf.format(new Date()) + ":" + strs);

	}

	public static boolean writeString(String dir, String filename, String strs) {

		File file = new File(dir);
		if (!file.exists()) {
			file.mkdirs();
		}
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(dir + "\\" + filename, true);
			fileWriter.write(strs);
			fileWriter.flush();
			fileWriter.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fileWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public static void main(String[] args) {
		writeXmlToLocalDisk("12344" + System.getProperty("line.separator"));
		writeString("D:\\vestaPcs\\log", "test.txt", "asdfdfsd");
	}
}
