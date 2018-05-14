package org.foxconn.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class StringArrayUtil {
	public static List<String> splitToArray(String sysserialnos) {
		return Arrays.asList(sysserialnos.split("\\s"));
	}

	public static String addDHAndFh(Collection<String> list) {
		StringBuilder strbuild = new StringBuilder("");
		for (String str : list) {
			if (!"".equals(str)) {
				strbuild.append("\'" + str.trim() + "\',");
			}
		}
		strbuild.deleteCharAt(strbuild.length() - 1);
		// 为了使得单个SN的时候支持模糊查询 将前后的 单引号去掉 在动态sql中增加
		strbuild.deleteCharAt(0);
		strbuild.deleteCharAt(strbuild.length() - 1);
		return strbuild.toString();
	}

	public static String addDHAndFh(String sysserialnos) {
		return addDHAndFh(splitToArray(sysserialnos));
	}

	public static String addDH(List<String> list) {
		StringBuilder strbuild = new StringBuilder("");
		for (String str : list) {
			if (!"".equals(str)) {
				strbuild.append(str.trim() + ",");
			}
		}
		strbuild.deleteCharAt(strbuild.length() - 1);
		return strbuild.toString();
	}

	public static String addDH(String sysserialnos) {
		return addDH(splitToArray(sysserialnos));
	}

	public static List<String> addDHS(String sysserialnos) {
		return addDHS(splitToArray(sysserialnos));
	}

	public static List<String> addDHS(List<String> list) {
		List<String> allResult = new ArrayList<String>();
		int i = 1;
		int size = list.size();
		List<String> sublist = new ArrayList<String>();
		do {
			sublist = new ArrayList<String>();
			if (500 * i - 1 < size) {
				sublist = list.subList(500 * (i - 1), 500 * i);
			} else {
				sublist = list.subList(500 * (i - 1), size);
			}
			i++;
			allResult.add(addDH(sublist));
		} while (size > 500 * (i - 1));

		return allResult;
	}

	public static void main(String[] args) {
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < 600; i++) {
			list.add(String.valueOf(i));
		}
		List<String> result = addDHS(list);
		System.out.println(result);
		result = addDHS(result.toString().replaceAll(",", " "));
		System.out.println(result);
	}
}
