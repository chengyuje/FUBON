package com.systex.jbranch.fubon.commons.mplus.check;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.lang.StringUtils;

public class AbstractMPlusCheckUtils implements MPlusCheckInf{
	
	//找到符合VO的判斷檢核方法
	@Override
	public StringBuffer checkVoIsSuccess(Object inputVO) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		return (StringBuffer)getClass().getMethod("checker", new Class[]{inputVO.getClass()}).invoke(this , inputVO);
	}

	//判斷傳入字串是否全部都是null或空白字元或是全形空白
	public boolean chkAllEmpty(String...vals){
		boolean isEmpty = true;
		if(vals == null || vals.length == 0)
			return true;
		
		for(String val : vals)
			if(!(isEmpty = isEmpty && chkEmpty(val)))
				return isEmpty;
		return true;
	}

	//判斷傳入字串是否為null或空白字元或是含全形空白，若是回傳預設參數defaultStr
	public String chkEmptyReturnNextLine(String val , String defaultStr){
		return chkEmpty(val , defaultStr + "\r\n");
	}
		
	//判斷傳入字串是否為null或空白字元或是含全形空白，若是回傳預設參數defaultStr
	public String chkEmpty(String val , String defaultStr){
		return chkEmpty(val) ? defaultStr : "";
	}
	
	//判斷傳入字串是否為null或空白字元或是含全形空白
	public boolean chkEmpty(String val){
		return StringUtils.isBlank(val == null ? "" : val.replaceAll("\\s|　", ""));
	}
	
	
}
