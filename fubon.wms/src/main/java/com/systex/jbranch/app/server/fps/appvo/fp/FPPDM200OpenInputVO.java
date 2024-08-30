package com.systex.jbranch.app.server.fps.appvo.fp;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class FPPDM200OpenInputVO extends PagingInputVO{

	private String prdID;
	private String mfPrdID;
	private String ptype;
	private String pname;
	private String sortByPerf;

	public String getPrdID() {
		return prdID;
	}

	public void setPrdID(String prdID) {
		this.prdID = prdID;
	}

	public String getMfPrdID() {
		return mfPrdID;
	}

	public void setMfPrdID(String mfPrdID) {
		this.mfPrdID = mfPrdID;
	}

	public String getPtype() {
		return ptype;
	}

	public void setPtype(String ptype) {
		this.ptype = ptype;
	}

	public String getPname() {
		return pname;
	}

	public void setPname(String pname) {
		this.pname = pname;
	}

	public String getSortByPerf() {
		return sortByPerf;
	}

	public void setSortByPerf(String sortByPerf) {
		this.sortByPerf = sortByPerf;
	}



	
}
