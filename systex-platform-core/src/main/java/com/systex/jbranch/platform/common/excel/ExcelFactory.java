package com.systex.jbranch.platform.common.excel;

public class ExcelFactory {

	/**
	 * 取得ExcelGenerator實體
	 * @return ExcelGenerator實體
	 */
	public static ExcelGenerator getGenerator(){
		return new ExcelGenerator();
	}
}
