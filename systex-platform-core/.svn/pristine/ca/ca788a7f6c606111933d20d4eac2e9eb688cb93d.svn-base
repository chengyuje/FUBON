package com.systex.jbranch.office.formula;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LimitLength implements DataProcessFormula<String> {

	private Logger logger = LoggerFactory.getLogger(LimitLength.class);
	private String docCharSet;

	public String process(String data, String args) throws Exception {
		String strLen = args;
		if(strLen == null){
			throw new IllegalArgumentException(
			"未設置DataProcessFormula[LimitLength.length]參數");
		}
		Integer length = null;
		try {
			length = Integer.parseInt(strLen);
		} catch (Exception e) {
			logger.warn("DataProcessFormula[LimitLength.length]參數需為數字[" + strLen + "]");
			throw new IllegalArgumentException(
			"DataProcessFormula[LimitLength.length]參數需為數字[" + strLen + "]");
		}
		if(docCharSet == null){
			if (data.length() > length) {
				data = data.substring(0, length);
			}
		}else{
			byte[] bytes = data.getBytes(docCharSet);
			if (bytes.length > length) {
				data = new String(bytes, 0, length, docCharSet);
			}
		}
		return data;
	}

	public String getDocCharSet() {
		return docCharSet;
	}

	public void setDocCharSet(String docCharSet) {
		this.docCharSet = docCharSet;
	}
}
