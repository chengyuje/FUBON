package com.systex.jbranch.office.formula;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.systex.jbranch.platform.common.errHandle.JBranchException;


public class SubString implements DataProcessFormula<String> {

	private Logger logger = LoggerFactory.getLogger(SubString.class);
	private String docCharSet;
	

	public String process(String data, String args)
			throws Exception {
		
		if(args == null){
			throw new IllegalArgumentException(
			"未設置DataProcessFormula[SubString]參數");
		}
		
		String[] arguments = args.split(",");
		Integer beginIndex;
		Integer endIndex;
		
		try {
			beginIndex = Integer.parseInt(arguments[0]);
		} catch (Exception e) {
			logger.error("DataProcessFormula Substring第一位參數需為數字[" + args + "]");
			throw new JBranchException("DataProcessFormula Substring第一位參數需為數字[" + args + "]");
		}
		
		try {
			endIndex = Integer.parseInt(arguments[1]);
		} catch (Exception e) {
			logger.error("DataProcessFormula Substring第二位參數需為數字[" + args + "]");
			throw new JBranchException("DataProcessFormula Substring第二位參數需為數字[" + args + "]");
		}
		
		if(beginIndex >= endIndex){
			logger.error("DataProcessFormula Substring中beginIndex[" + beginIndex + "], 必需小於endIndex[" + endIndex + "]");
			throw new JBranchException("DataProcessFormula Substring中beginIndex[" + beginIndex + "], 必需小於endIndex[" + endIndex + "]");
		}
		
		if(docCharSet == null){
			if (data.length() > (beginIndex + endIndex)) {
				data = data.substring(beginIndex, endIndex);
			}
		}else{
			byte[] bytes = data.getBytes(docCharSet);
			if (bytes.length >= (beginIndex + endIndex - beginIndex)) {
				data = new String(bytes, beginIndex, endIndex - beginIndex, docCharSet);
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
