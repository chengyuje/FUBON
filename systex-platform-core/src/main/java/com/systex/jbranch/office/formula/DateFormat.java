package com.systex.jbranch.office.formula;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.systex.jbranch.platform.common.util.DateUtil;

public class DateFormat implements DataProcessFormula<Date> {

	private Logger logger = LoggerFactory.getLogger(DateFormat.class);

	public String process(Date data, String args) throws Exception {
		String pattern = args;
		if (pattern == null) {
			throw new IllegalArgumentException(
					"未設置DataProcessFormula[DateFormat.pattern]參數");
		}
		try {
			return DateUtil.format(data, pattern);
		} catch (Exception e) {
			logger.warn("無法格式化日期[" + data + "] pattern[" + pattern + "]");
			return data.toString();
		}
	}
}
