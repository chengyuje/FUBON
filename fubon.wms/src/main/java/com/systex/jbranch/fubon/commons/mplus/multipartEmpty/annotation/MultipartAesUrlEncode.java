package com.systex.jbranch.fubon.commons.mplus.multipartEmpty.annotation;

import java.net.URLEncoder;

public class MultipartAesUrlEncode extends MultipartAes{
	@Override
	protected String encryptAES(Object value, String sKey) throws Exception {
		return URLEncoder.encode(super.encryptAES(value, sKey) , "utf-8");
	}
}

