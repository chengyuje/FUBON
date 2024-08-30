package com.systex.jbranch.common.sorterinfo;

public class SorterInputVO {
	
	/** 
	 * @author ArthurKO
	 * @depiction Data Sorting by DataSortManager
	 */
	private boolean checkin;
	private boolean asc;
	private String column;
	private String sortId;
	
	public boolean isCheckin() {
		return checkin;
	}

	public void setCheckin(boolean checkin) {
		this.checkin = checkin;
	}

	public boolean isAsc() {
		return asc;
	}

	public void setAsc(boolean asc) {
		this.asc = asc;
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public String getSortId() {
		return sortId;
	}

	public void setSortId(String sortId) {
		this.sortId = sortId;
	}

		
}
