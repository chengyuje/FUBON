package com.systex.jbranch.office.formula;


public interface DataProcessFormula<T> {

	public String process(T data, String args) throws Exception;
}
