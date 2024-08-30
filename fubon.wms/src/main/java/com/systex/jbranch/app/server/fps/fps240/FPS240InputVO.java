package com.systex.jbranch.app.server.fps.fps240;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Blob;
import java.util.List;

import com.systex.jbranch.app.server.fps.fps200.FPS200InputVO;

public class FPS240InputVO extends FPS200InputVO {
	public FPS240InputVO(){}
	
	private String commRsYn;
	private String fileName;
	private String tempFileName;
	private BigDecimal printSEQ;
	private Boolean hasIns;
  
	public String getCommRsYn() {
		return commRsYn;
	}

	public void setCommRsYn(String commRsYn) {
		this.commRsYn = commRsYn;
	}
  
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getTempFileName() {
		return tempFileName;
	}

	public void setTempFileName(String tempFileName) {
	    this.tempFileName = tempFileName;
	}
	
	public BigDecimal getPrintSEQ() {
		return printSEQ;
	}

	public void setPrintSEQ(BigDecimal printSEQ) {
		this.printSEQ = printSEQ;
	}

	public Boolean getHasIns() {
		return hasIns;
	}
	
	public void setHasIns(Boolean hasIns) {
		this.hasIns = hasIns;
	}
}