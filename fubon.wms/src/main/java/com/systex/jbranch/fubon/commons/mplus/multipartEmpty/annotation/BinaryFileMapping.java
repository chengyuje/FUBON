package com.systex.jbranch.fubon.commons.mplus.multipartEmpty.annotation;

import java.lang.reflect.Field;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import com.systex.jbranch.fubon.commons.mplus.MPlusInputVO;

public class BinaryFileMapping extends MultipartAes{

	@Override
	public boolean addTextBody(MultipartEntityBuilder mebuilder, Object inputVO, Field field, Object fieldInstance, String secretKey) throws Exception {
		boolean isSetTextBodyNotOk = fieldInstance == null;
		isSetTextBodyNotOk = fieldInstance == null
							 || super.addTextBody(mebuilder, inputVO, field, fieldInstance, secretKey) 
							 || !MPlusInputVO.BinaryFile.class.equals(fieldInstance.getClass());
		
		if(isSetTextBodyNotOk)
			return false;
		
		MPlusInputVO.BinaryFile binaryFileObj = (MPlusInputVO.BinaryFile) fieldInstance;
		mebuilder.addBinaryBody(field.getName() , binaryFileObj.getFileCxt(), ContentType.APPLICATION_OCTET_STREAM, binaryFileObj.getFileName());
		return true;
	}

}
