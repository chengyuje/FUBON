package com.systex.jbranch.office.formula;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.StringUtil;

public class Padding implements DataProcessFormula<String> {

	private Logger logger = LoggerFactory.getLogger(Padding.class);
	private String docCharSet;
	
	public String process(String data, String args) throws Exception {
		
		if(args == null){
			throw new IllegalArgumentException(
			"未設置DataProcessFormula[Padding]參數");
		}
		
		String direction;
		Integer size;
		String padStr;
		
		String[] arguments = args.split(",");
		direction = arguments[0];
		try {
			size = Integer.parseInt(arguments[1]);
		} catch (Exception e) {
			logger.error("DataProcessFormula Padding第二位參數需為數字[" + args + "]");
			throw new JBranchException("DataProcessFormula Padding第二位參數需為數字[" + args + "]");
		}
		try {
			padStr = arguments[2];
		} catch (Exception e) {
			logger.error("未設置DataProcessFormula Padding第三位參數[" + args + "]");
			throw new JBranchException("未設置DataProcessFormula Padding第三位參數[" + args + "]");
		}
		
		if(direction == null){
			throw new IllegalArgumentException(
			"未設置DataProcessFormula[Padding.direction]參數");
		}
		if(size == null){
			throw new IllegalArgumentException(
			"未設置DataProcessFormula[Padding.size]參數");
		}
		if(padStr == null){
			throw new IllegalArgumentException(
			"未設置DataProcessFormula[Padding.padStr]參數");
		}
		String result;
		if(docCharSet == null){
			if(data.length() > size){
				return data;
			}
			if(direction.equalsIgnoreCase("R")){
				result = StringUtil.rightPad(data, size, padStr);
			}else if(direction.equalsIgnoreCase("L")){
				result = StringUtil.leftPad(data, size, padStr);
			}else{
				throw new IllegalArgumentException("Padding 不合法的參數direction[" + direction + "], 需為[R 或 L]");
			}
			
		}else{
			byte[] bytes = data.getBytes(docCharSet);
			if(bytes.length >= size){
				return data;
			}
			int padStrLen = padStr.getBytes(docCharSet).length - padStr.length() + 1;
			int padStrSize = (size - bytes.length) / padStrLen;
			int mod = (size - bytes.length) % padStrLen;
			int padLen = data.length() + padStrSize;
			
			if(direction.equalsIgnoreCase("R")){
				result = StringUtil.rightPad(data, padLen, padStr);
				result = StringUtil.rightPad(result, padLen + mod, " ");
			}else if(direction.equalsIgnoreCase("L")){
				result = StringUtil.leftPad(data, padLen, padStr);
				result = StringUtil.leftPad(result, padLen + mod, " ");
			}else{
				throw new IllegalArgumentException("Padding 不合法的參數direction[" + direction + "], 需為[R 或 L]");
			}
		}
		return result;
	}

	public String getDocCharSet() {
		return docCharSet;
	}

	public void setDocCharSet(String docCharSet) {
		this.docCharSet = docCharSet;
	}
}
