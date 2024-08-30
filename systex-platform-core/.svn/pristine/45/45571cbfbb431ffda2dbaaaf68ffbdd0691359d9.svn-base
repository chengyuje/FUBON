package com.systex.jbranch.platform.common.util;

import java.util.Arrays;
import java.util.Map;

import com.systex.jbranch.platform.common.errHandle.JBranchException;

public class FormulaUtil {

	/**
	 * parse statement中指定的key，並將值substring後回傳
	 * @param map 來源map
	 * @param statement 支援格式 XXX(0-1)
	 * @return substring後的值
	 * @throws JBranchException
	 */
	public static String substring(Map<String, String> map, String statement) throws JBranchException{
		int startIndex = statement.indexOf("(");
		int endIndex = statement.lastIndexOf(")");
		int dsIndex = statement.lastIndexOf("-");
		if(startIndex == -1 || endIndex == -1 || dsIndex == -1){
			throw new JBranchException("FormulaUtil.substring格式需符合[XXX(0-1)]");
		}
		String key = statement.substring(0, startIndex);
		String value = map.get(key);
		if(value == null){
			return null;
		}
		String strRange = statement.substring(startIndex + 1, endIndex);
		String[] temps = strRange.split("-", 2);
		startIndex = Integer.parseInt(temps[0].trim());
		endIndex = Integer.parseInt(temps[1].trim());
		
		
		return value.substring(startIndex, endIndex);
	}
	
	/**
	 * 身分證字號與居留證(統一證)編號檢核
	 * 
	 * @param str
	 * @return boolean
	 * @throws JBranchException
	 */
	public static boolean isValidIDorRCNumber(String str) throws JBranchException {
		if (str == null || "".equals(str)) {
			return false;
		}

		final char[] pidCharArray = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
				'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
				'U', 'V', 'W', 'X', 'Y', 'Z' };

		// 原身分證英文字應轉換為10~33，這裡直接作個位數*9+10
		final int[] pidIDInt = { 1, 10, 19, 28, 37, 46, 55, 64, 39, 73, 82, 2,
				11, 20, 48, 29, 38, 47, 56, 65, 74, 83, 21, 3, 12, 30 };

		// 原居留證第一碼英文字應轉換為10~33，十位數*1，個位數*9，這裡直接作[(十位數*1) mod 10] + [(個位數*9) mod
		// 10]
		final int[] pidResidentFirstInt = { 1, 10, 9, 8, 7, 6, 5, 4, 9, 3, 2,
				2, 11, 10, 8, 9, 8, 7, 6, 5, 4, 3, 11, 3, 12, 10 };

		// 原居留證第二碼英文字應轉換為10~33，並僅取個位數*6，這裡直接取[(個位數*6) mod 10]
		final int[] pidResidentSecondInt = { 0, 8, 6, 4, 2, 0, 8, 6, 2, 4, 2,
				0, 8, 6, 0, 4, 2, 0, 8, 6, 4, 2, 6, 0, 8, 4 };

		str = str.toUpperCase();// 轉換大寫
		final char[] strArr = str.toCharArray();// 字串轉成char陣列
		int verifyNum = 0;

		/* 檢查身分證字號 */
		if (str.matches("[A-Z]{1}[1-2]{1}[0-9]{8}")) {
			// 第一碼
			verifyNum = verifyNum
					+ pidIDInt[Arrays.binarySearch(pidCharArray, strArr[0])];
			// 第二~九碼
			for (int i = 1, j = 8; i < 9; i++, j--) {
				verifyNum += Character.digit(strArr[i], 10) * j;
			}
			// 檢查碼
			verifyNum = (10 - (verifyNum % 10)) % 10;

			return verifyNum == Character.digit(strArr[9], 10);
		}

		/* 檢查統一證(居留證)編號 */
		verifyNum = 0;
		if (str.matches("[A-Z]{1}[A-D]{1}[0-9]{8}")) {
			// 第一碼
			verifyNum += pidResidentFirstInt[Arrays.binarySearch(pidCharArray,
					strArr[0])];
			// 第二碼
			verifyNum += pidResidentSecondInt[Arrays.binarySearch(pidCharArray,
					strArr[1])];
			// 第三~八碼
			for (int i = 2, j = 7; i < 9; i++, j--) {
				verifyNum += Character.digit(strArr[i], 10) * j;
			}
			// 檢查碼
			verifyNum = (10 - (verifyNum % 10)) % 10;

			return verifyNum == Character.digit(strArr[9], 10);
		}

		return false;
	}
	
}
