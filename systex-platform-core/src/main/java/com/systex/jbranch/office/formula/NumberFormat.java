package com.systex.jbranch.office.formula;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.systex.jbranch.platform.common.util.NumberUtil;

public class NumberFormat implements DataProcessFormula<Object> {

	private Logger logger = LoggerFactory.getLogger(NumberFormat.class);

	public String process(Object data, String args) throws Exception {
		String pattern = args;
		if (pattern == null) {
			throw new IllegalArgumentException(
					"未設置DataProcessFormula[DecimalFormat.pattern]參數");
		}
		if(data == null){
			return "";
		}
		if(data instanceof BigDecimal){
			return NumberUtil.format((BigDecimal) data, pattern);
		}
		try {
			BigDecimal number = new BigDecimal(data.toString());
			return NumberUtil.format(number, pattern);
		} catch (Exception e) {
			logger.warn("無法格式化數字" + "[" + data + "]");
			return data.toString();
		}
	}
}
