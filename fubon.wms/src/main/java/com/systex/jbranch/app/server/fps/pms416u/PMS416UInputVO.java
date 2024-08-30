package com.systex.jbranch.app.server.fps.pms416u;

import java.util.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;
 
public class PMS416UInputVO extends PagingInputVO {
	
	private String dataMon;
	private String aoCode;          // 理專
	private String freqType;       	 // 進出頻率
	
	private Date sCreDate;
	private String importSDate;
	
	private String uhrmRC;
	private String uhrmOP;
	
	public String getUhrmRC() {
		return uhrmRC;
	}

	public void setUhrmRC(String uhrmRC) {
		this.uhrmRC = uhrmRC;
	}

	public String getUhrmOP() {
		return uhrmOP;
	}

	public void setUhrmOP(String uhrmOP) {
		this.uhrmOP = uhrmOP;
	}

	public String getImportSDate() {
		return importSDate;
	}

	public void setImportSDate(String importSDate) {
		this.importSDate = importSDate;
	}

	public Date getsCreDate() {
		return sCreDate;
	}

	public void setsCreDate(Date sCreDate) {
		this.sCreDate = sCreDate;
	}

	public String getAoCode() {
		return aoCode;
	}
	
	public void setAoCode(String aoCode) {
		this.aoCode = aoCode;
	}
	
	public String getDataMon() {
		return dataMon;
	}
	
	public void setDataMon(String dataMon) {
		this.dataMon = dataMon;
	}
	
	public String getFreqType() {
		return freqType;
	}
	
	public void setFreqType(String freqType) {
		this.freqType = freqType;
	}
	
}
