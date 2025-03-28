package com.systex.jbranch.fubon.jlb;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.errHandle.JBranchException;

/**
 * 提供JAVA端轉換資料套用遮罩之共用模組
 *
 * @author ArthurKO
 * @date 2016/01/11
 * @spec FPS系統TDS-遮罩規則.doc
 * @Modifier:
 * @Lastupdate:
 * @Description:
 * Modifier:
 * LastUpdate:
 * Description:
 *
 **/

public class DataFormat extends FubonWmsBizLogic {

	/**
	 * 高風險個資遮蔽原則 =>  身份證字號 5~8 碼為 * 符號
	 * @param custId
	 * @return
	 */
	public static String getCustIdMaskForHighRisk(String custId) {
		return custId.replaceAll("(.{4})(.{4})(.*)", "$1" + "****" + "$3");
	}

	/**
	 * 高風險個資遮蔽原則 =>  姓名第 2 碼為 * 符號
	 * @param name
	 * @return
	 */
	public static String getNameForHighRisk(String name) {
		int count = StringUtils.length(name);
		if (count < 2)
			return name;
		if (count < 5)
			return name.replaceAll("(.{1})(.{1})(.*)", "$1" + "*" + "$3");
		if (count < 9)
			return StringUtils.substring(name, 0, 2) +
					StringUtils.repeat("*", name.length()-4) +
					StringUtils.substring(name, -2);
		return StringUtils.repeat("*", 6) + StringUtils.substring(name, 6);
	}

	/**
     * Method Name: getMarking to string
     *
     * Preferences:
     * [caseI = 1  身份證字號 (10)]
     * [caseI = 2  統一編號(8)]
     * [caseI = 3  外國人編號(10)]
     * [caseI = 4  地址(x)]
     * [caseI = 5  市話(x)]
     * [caseI = 6  手機欄位(10)]
     * [caseI = 7  姓名欄位(x)]
     *
     * 2016/01/11 by ArthurKO
     * @param caseI
     * @return
     */
	public String getMarking(int caseI, String item) throws Exception, JBranchException {
		/* initialize */
		String strTmp="";
		try{
			/* CHECK PARAM */
			if (caseI != 0 && item != null && item != "") {
				int lenI = item.trim().length();
				/* Select Case */
				switch(caseI) {
					/* 身份證字號 */
					//2016/05/12 Allen 因欄位可能有統編資料故多判斷統編條件。
					/* ID */
		            case 1:
		            	if (lenI >= 10) {
		            		CharSequence strTmp_a = item.subSequence(0, 5);
		            		CharSequence strTmp_b = item.subSequence(9,lenI);
		            		//compose string
		            		strTmp = strTmp_a + "****" + strTmp_b;
		            	} else if (lenI == 8){
		            		CharSequence strTmp_a = item.subSequence(0, 5);
		            		//compose string
		            		strTmp = strTmp_a + "***";
		            	}
		            	else{
		            		logger.debug("ID: The number of strings do not match.");
		            	}
		                break;
		            /* 統一編號 */
	                /* Uniform number */
		            case 2:
		            	if (lenI == 8) {
		            		CharSequence strTmp_a = item.subSequence(0, 5);
		            		//compose string
		            		strTmp = strTmp_a + "***";
		            	}else{
		            		logger.debug("Uniform number: the number of strings do not match.");
		            	}
		                break;
		            /* 外國人編號 */
	                /* Foreigners ID */
		            case 3:
		            	if (lenI == 10) {
		            		CharSequence strTmp_a = item.subSequence(0, 5);
		            		char strTmp_b = item.charAt(9);
		            		//compose string
		            		strTmp = strTmp_a + "****" + strTmp_b;
		            	}else{
		            		logger.debug("Foreigners ID: Quantity strings do not match.");
		            	}
		                break;
		            /* 地址 */
	                /* Address */
		            //2016/01/14 ArthurKO 若地址欄位輸入的字符數量少於要遮罩的數量則不需遮罩回傳原參數。
		            case 4:
		            	if (lenI > 6) {
		            		CharSequence strTmp_a = item.subSequence(0, 5);
		            		String strTmp_b = "";
		            		for (int i = lenI; i > 6; i--) {
		            			strTmp_b = strTmp_b + "*";
		            		}
		            		//compose string
		            		strTmp = strTmp_a + strTmp_b;
		            	}else if (lenI > 0 && lenI <= 6) {
		            		//compose string
		            		strTmp = item;
		            	}else{
		            		logger.debug("Address: the number of strings do not match.");
		            	}
		                break;
		            /* 市話 */
	                /* Local phone number */
		            //condition: 輸入的參數大於5碼，顯示前5碼，後面遮罩。
	                //2016/01/14 ArthurKO 若市話欄位輸入的字符數量少於要遮罩的數量則不需遮罩回傳原參數。
		            case 5:
		            	if (lenI > 5) {
		            		CharSequence strTmp_a = item.subSequence(0, 5);
		            		String strTmp_b = "";
		            		for (int i = lenI; i > 5; i--) {
		            			strTmp_b = strTmp_b + "*";
		            		}
		            		//compose string
		            		strTmp = strTmp_a + strTmp_b;
		            	}else if (lenI > 0 && lenI <= 5) {
		            		//compose string
		            		strTmp = item;
		            	}else{
		            		logger.debug("Local phone number: the number of strings do not match.");
		            	}
		                break;
		            /* 手機 */
	                //condition: 輸入的參數大於等於9碼，顯示前5碼，後面遮罩。
		            case 6:
		            	if (lenI > 5) {
		            		CharSequence strTmp_a = item.subSequence(0, 5);
		            		String strTmp_b = "";
		            		for (int i = lenI; i > 5; i--) {
		            			strTmp_b = strTmp_b + "*";
		            		}
		            		//compose string
		            		strTmp = strTmp_a + strTmp_b;
		            	}else if (lenI > 0 && lenI <= 5) {
		            		//compose string
		            		strTmp = item;
		            	}else{
		            		logger.debug("Uniform number: the number of strings do not match.");
		            	}
		                break;
		            /* 姓名 */
		            case 7:
		            	if (lenI <= 3) {
		            		char strTmp_a = item.charAt(0);
		            		String strTmp_b = "";
		            		for (int i = lenI; i > 1; i--) {
		            			strTmp_b = strTmp_b + "*";
		            		}
		            		//compose string
		            		strTmp = strTmp_a + strTmp_b;
		            	}else if (lenI == 4) {
		            		CharSequence strTmp_a = item.subSequence(0, 2);
		            		String strTmp_b = "**";
		            		//compose string
		            		strTmp = strTmp_a + strTmp_b;
		            	}else if (lenI > 4) {
		            		CharSequence strTmp_a = item.subSequence(0, 3);
		            		String strTmp_b = "";
		            		for (int i = lenI; i > 3; i--) {
		            			strTmp_b = strTmp_b + "*";
		            		}
		            		//compose string
		            		strTmp = strTmp_a + strTmp_b;
		            	}else{
		            		logger.debug("Uniform number: the number of strings do not match.");
		            	}
		                break;
		            /* Others */
		            default:
		            	logger.debug("Case type does not exist!");
				}
			}else if(caseI > 7 && item != null && item != ""){
				logger.debug("Case type out of group!");
			}else{
				logger.debug("Case type or string are empty!");
			}
		}catch(Exception e){
			logger.debug(e.getMessage(),e);
		}
		/* return result */
		return strTmp;
	}

	/*
	 * 客戶ID遮罩
	 */
	public String maskID(String CustID){
		String custID = "";

		if(StringUtils.length(CustID) == 10){ //本國人ID
			custID = StringUtils.substring(CustID, 0, 5) + "****" + StringUtils.substring(CustID, -1);
		}else if(StringUtils.length(CustID) == 8){ //統一編號
			custID  = StringUtils.substring(CustID, 0, 5) + "***";
		}else{
			if(StringUtils.length(CustID) >= 7){ //特殊ID
				custID = StringUtils.substring(CustID, 0, 5) + "**" + StringUtils.substring(CustID, 7, StringUtils.length(CustID));
			}else{
				custID = CustID; //不遮
			}
		}
		return custID;
	}
	
	/*
	 * 電文客戶ID遮罩
	 */
	public String maskXml(String xml, String tag) {
        Pattern pattern = Pattern.compile("<" + tag + ">(.+?)</" + tag + ">", Pattern.CASE_INSENSITIVE);
        Matcher mat = pattern.matcher(xml);
        if (mat.find()) {
            String tagVal = mat.group(1).trim();
            String maskVal = maskID(tagVal);
            xml = xml.replaceFirst(tagVal, maskVal);
        }
        return xml;
    }
	
	/*
	 * API 客戶ID遮罩
	 */
	public String maskJson(String orig, String tag) {
        String origPat = "\"(" + tag + ")\" *?: *?\"(.+?)\"";
        Pattern pat = Pattern.compile(origPat, Pattern.CASE_INSENSITIVE);
        Matcher mat = pat.matcher(orig);
        if (mat.find()) {
        	String tagKey = mat.group(1);
            String val = mat.group(2);
            if (val != null && val.length() > 9) {
                String hidCode = maskID(val);
                orig = orig.replaceAll(origPat, "\"" + tagKey + "\":\"" + hidCode + "\"");
            }
        }
        return orig;
    }
	
	/*
	 * 地址遮罩
	 */
	public static String maskAddr(String Addr){
		String addr = "";
		int lenI = StringUtils.length(Addr);
		if (lenI > 6) {
    		String strTmp_a = StringUtils.substring(Addr, 0, 5);
    		String strTmp_b = "";
    		for (int i = lenI; i > 6; i--) {
    			strTmp_b = strTmp_b + "*";
    		}
    		//compose string
    		addr = strTmp_a + strTmp_b;
    	}else if (lenI > 0 && lenI <= 6) {
    		//compose string
    		addr = Addr;
    	}
		return addr;
	}
	/*
	 * 手機遮罩
	 */
	public static String maskCellPhone(String CellPhone){
		String cellphone = "";
		int lenI = StringUtils.length(CellPhone);
		if (lenI > 5) {
    		String strTmp_a = StringUtils.substring(CellPhone, 0, 5);
    		String strTmp_b = "";
    		for (int i = lenI; i > 5; i--) {
    			strTmp_b = strTmp_b + "*";
    		}
    		//compose string
    		cellphone = strTmp_a + strTmp_b;
    	}else if (lenI > 0 && lenI <= 5) {
    		//compose string
    		cellphone = CellPhone;
    	}
		return cellphone;
	}
	/*
	 * 市話遮罩
	 */
	public static String maskTelPhone(String TelPhone){
		String telphone = "";
		int lenI = StringUtils.length(TelPhone);
		if (lenI > 5) {
    		String strTmp_a = StringUtils.substring(TelPhone, 0, 5);
    		String strTmp_b = "";
    		for (int i = lenI; i > 5; i--) {
    			strTmp_b = strTmp_b + "*";
    		}
    		//compose string
    		telphone = strTmp_a + strTmp_b;
    	}else if (lenI > 0 && lenI <= 5) {
    		//compose string
    		telphone = TelPhone;
    	}
		return telphone;
	}

	/*
	 * 姓名遮罩
	 */
	public static String maskName(String Name){
		String name = "";
		int lenI = StringUtils.length(Name);
		if (lenI < 3) {
    		String strTmp_a = StringUtils.substring(Name, 0, 1);
    		//compose string
    		name = strTmp_a +"*";
    	}else{
    		String strTmp_a = StringUtils.substring(Name, 0, 1);
    		String strTmp_b = StringUtils.substring(Name, (lenI -2) * -1);
    		name = strTmp_a +"*"+ strTmp_b;
    	}
		return name;
	}
}
