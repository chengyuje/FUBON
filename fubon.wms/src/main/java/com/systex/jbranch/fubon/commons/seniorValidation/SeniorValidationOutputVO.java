package com.systex.jbranch.fubon.commons.seniorValidation;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

public class SeniorValidationOutputVO {
	private List<Map<String, String>> seniorCustEvalResult;
	
	public List<Map<String, String>> getSeniorCustEvalResult() {
		return seniorCustEvalResult;
	}

	public void setSeniorCustEvalResult(
			List<Map<String, String>> seniorCustEvalResult) {
		this.seniorCustEvalResult = seniorCustEvalResult;
	}

}