package com.systex.jbranch.office.formula;

import java.math.BigDecimal;

import com.systex.jbranch.platform.common.util.AmtUtil;

public class NumberToWord implements DataProcessFormula<Object> {

	@Override
	public String process(Object data, String args) throws Exception {

		if(data != null){
			return AmtUtil.transferNum(new BigDecimal(data.toString()));
		}
		return "";
	}

}
