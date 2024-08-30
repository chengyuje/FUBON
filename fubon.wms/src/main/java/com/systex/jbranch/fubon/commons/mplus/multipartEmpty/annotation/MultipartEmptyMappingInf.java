package com.systex.jbranch.fubon.commons.mplus.multipartEmpty.annotation;

import java.lang.reflect.Field;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.http.entity.mime.MultipartEntityBuilder;

public interface MultipartEmptyMappingInf {
	public boolean addTextBody(MultipartEntityBuilder mebuilder , Object vo , Field field , Object fieldInstance , String sKey)throws Exception;
	public NameValuePair genNameValuePair(Object vo , Field field , Object fieldInstance , String sKey)throws Exception;
}
