package com.systex.jbranch.platform.common.excel;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.errHandle.JBranchException;

import jxl.biff.formula.FormulaException;
import jxl.read.biff.BiffException;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class Test {

	/**
	 * @param args
	 * @throws JBranchException 
	 */
	public static void main(String[] args) throws Exception {
		List ds1 = new ArrayList();
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("COL1", 1);
		map.put("COL2", 2);
		map.put("COL3", 3);
		
		Map<String, Object> map2 = new HashMap<String, Object>();
		map2.put("COL1", 4);
		map2.put("COL2", 5);
		map2.put("COL3", 6);
		
		Map<String, Object> map3 = new HashMap<String, Object>();
		map3.put("COL1", 7);
		map3.put("COL2", 8);
		map3.put("COL3", 9);
		
		ds1.add(map);
		ds1.add(map2);
		ds1.add(map3);
		
		List ds2 = new ArrayList();
		
		Map<String, Object> map4 = new HashMap<String, Object>();
		map4.put("COL1", 11);
		map4.put("COL2", 21);
		map4.put("COL3", 31);
		
		Map<String, Object> map5 = new HashMap<String, Object>();
		map5.put("COL1", 41);
		map5.put("COL2", 51);
		map5.put("COL3", 61);
		
		Map<String, Object> map6 = new HashMap<String, Object>();
		map6.put("COL1", 71);
		map6.put("COL2", 81);
		map6.put("COL3", 91);
		
		ds2.add(map4);
		ds2.add(map5);
		ds2.add(map6);
		
		ExcelGenerator generator = ExcelFactory.getGenerator();
		generator.addDataSet("DS1", ds1);
		generator.addDataSet("DS2", ds2);

//		generator.setTemplate("Lab002", "R");	//AP Server上使用
		generator.setTemplatePath("D:\\Book1.xls");	//單機版使用
		generator.setSavePath("D:\\Book2.xls");	//單機版使用
		generator.generateToExcel();
		System.out.println("finshed");
	}

}
