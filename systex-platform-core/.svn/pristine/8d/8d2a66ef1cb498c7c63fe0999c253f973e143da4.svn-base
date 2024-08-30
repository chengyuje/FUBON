package com.systex.jbranch.platform.common.excel;

import java.util.ArrayList;
import java.util.List;

import jxl.Cell;
import jxl.write.WritableCell;

public class DataSetDetail {
	
	private String name;
	private WritableCell begin;
	private WritableCell end;
	private List<WritableCell> varableCells = new ArrayList<WritableCell>();
	private WritableCell hasTempRowCell;

	void appendVarableCell(WritableCell cell){
		varableCells.add(cell);
		if(varableCells.size() == 1){
			begin = cell;
		}else{
			end = cell;
		}
	}
	
	List<WritableCell> getVarableCells(){
		
		return varableCells;
	}
	
	String getName() {
		return name;
	}
	void setName(String name) {
		this.name = name;
	}
	Location getBegin() {
		return new Location(begin.getColumn(), begin.getRow());
	}
	void setBegin(WritableCell begin) {
		this.begin = begin;
	}
	Location getEnd() {
		return new Location(end.getColumn(), end.getRow());
	}
	void setEnd(WritableCell end) {
		this.end = end;
	}
	RowAndColumn getRowAndColumnCount() {
		int rowMin = Integer.MAX_VALUE;
		int rowMax = Integer.MIN_VALUE;
		int columnMin = Integer.MAX_VALUE;
		int columnMax = Integer.MIN_VALUE;
		for (int i = 0; i < varableCells.size(); i++) {
			Cell cell = varableCells.get(i);
			int row = cell.getRow();
			int column = cell.getColumn();
			rowMin = Math.min(rowMin, row);
			rowMax = Math.max(rowMax, row);
			columnMin = Math.min(columnMin, column);
			columnMax = Math.max(columnMax, column);
		}
		RowAndColumn rc = new RowAndColumn(rowMax - rowMin + 1, columnMax - columnMin + 1);
		return rc;
	}
	

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		if(varableCells.size() == 0){
			return "name=" + name;
		}
		return "name=" + name + 
		" (" + varableCells.get(0).getRow() + "," + varableCells.get(0).getColumn() + ")" +
		"~(" + varableCells.get(varableCells.size() - 1).getRow() + "," + varableCells.get(varableCells.size() - 1).getColumn() + ")";
	}

	void setTempRowCell(WritableCell cell) {
		hasTempRowCell = cell;
	}
	
	WritableCell getTempRowCell(){
		return hasTempRowCell;
	}
}
