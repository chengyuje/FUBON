package com.systex.jbranch.fubon.commons.mplus.multipartEmpty.annotation;

import java.lang.reflect.Field;

import org.apache.commons.codec.CharEncoding;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.lang.StringUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.json.JSONObject;

import com.systex.jbranch.fubon.commons.mplus.AbstractMPlus;

public class MultipartAes implements MultipartEmptyMappingInf {

    /**
     * 塞入TextBody的實作內容，若成功塞入回傳true否則false
     *
     * @param mebuilder
     * @param inputVO
     * @param field
     * @param fieldInstance
     * @param secretKey
     * 
     * @return boolean
     * @throws Exception
     */
	@SuppressWarnings("rawtypes")
	@Override
    public boolean addTextBody(MultipartEntityBuilder mebuilder , Object inputVO , Field field , Object fieldInstance , String secretKey)throws Exception{
		if(fieldInstance == null)
			return false;
		
		String fidleName = field.getName();
		Class fieldClass = fieldInstance.getClass();
		
		//純字串
		if(String.class.equals(fieldClass)){
			mebuilder.addTextBody(fidleName , encryptAES(fieldInstance , secretKey) , ContentType.TEXT_PLAIN.withCharset(CharEncoding.UTF_8));
		}
		//JSONObject物件格式
		else if(JSONObject.class.equals(fieldClass)){
            if(StringUtils.equals(fidleName, "text"))
            	mebuilder.addTextBody(fidleName, encryptAES(fieldInstance , secretKey) , ContentType.TEXT_PLAIN.withCharset(CharEncoding.UTF_8));
            else
            	mebuilder.addTextBody(fidleName, encryptAES(fieldInstance , secretKey) , ContentType.TEXT_PLAIN.withCharset(CharEncoding.UTF_8));
        }
		else{
			return false;
		}
		return true;
    } 
	
	protected String encryptAES(Object value , String sKey) throws Exception{
		return value == null ? null : StringUtils.isBlank(sKey) ? value.toString() : AbstractMPlus.encryptAES(value.toString() , sKey);
	}

	protected static String decryptAES(String value , String sKey) throws Exception {
    	return value == null ? null : StringUtils.isBlank(sKey) ? value.toString() : AbstractMPlus.decryptAES(value.toString() , sKey);
    }

	@Override
	public NameValuePair genNameValuePair(Object vo, Field field, Object fieldInstance, String secretKey) throws Exception {
		return fieldInstance == null ? null : new NameValuePair(field.getName() , encryptAES(fieldInstance , secretKey));
	}
}