package com.systex.jbranch.app.server.fps.cmsub302;
import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;

public class CMSUB302Util 
{
	/**
	 * 判斷頁數長度，不夠6位的往前補0直到滿6位
	 * 
	 * @param page
	 * @return
	 */
	public static String leftAddZero(int page) {
		String rturn_str = "" + page;
		for (int i = rturn_str.length(); i < 7; i++) {
			rturn_str = "0" + rturn_str;
		}
		return rturn_str;
	}

	public static String leftAddZDate(Object date) {
		String rturn_str = "" + date;
		for (int i = rturn_str.length(); i < 9; i++) {
			rturn_str = "0" + rturn_str;
		}
		return rturn_str;
	}

	public static String removeZero(Object date) {
		String rturn_str = "" + date;
		if (!StringUtils.isBlank(rturn_str) && "0".equals(getString(rturn_str.charAt(0)))) {
			return rturn_str.substring(1);
		} else {
			return rturn_str;
		}
	}
	
	/**
	 * String轉化
	 * 
	 * @param data_obj
	 * @return
	 */
	public static String getString(Object data_obj) {
		if (data_obj == null) {
			return "";
		} else {
			return data_obj.toString();
		}
	}

	/**
	 * 判斷“”or null 欄位轉化為0
	 * 
	 * @param data_obj
	 * @return
	 */
	public static BigDecimal getBigDecimal(Object data_obj) {
		if (data_obj == "" || data_obj == null) {
			return BigDecimal.ZERO;
		} else {
			return new BigDecimal(data_obj.toString());
		}
	}
}
