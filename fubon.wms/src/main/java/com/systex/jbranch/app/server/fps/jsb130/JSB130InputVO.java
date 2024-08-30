package com.systex.jbranch.app.server.fps.jsb130;

import java.sql.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class JSB130InputVO extends PagingInputVO{
	private String fileName;
	private String fileRealName;
	private String ins_com_id;
	private String data_month;
	private Date data_date;
	private Date acc_value_date;
	private String export_type;
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileRealName() {
		return fileRealName;
	}

	public void setFileRealName(String fileRealName) {
		this.fileRealName = fileRealName;
	}

	public String getIns_com_id() {
		return ins_com_id;
	}

	public void setIns_com_id(String ins_com_id) {
		this.ins_com_id = ins_com_id;
	}

	public String getData_month() {
		return data_month;
	}

	public void setData_month(String data_month) {
		this.data_month = data_month;
	}

	public Date getData_date() {
		return data_date;
	}

	public void setData_date(Date data_date) {
		this.data_date = data_date;
	}
	
	public Date getAcc_value_date() {
		return acc_value_date;
	}

	public void setAcc_value_date(Date acc_value_date) {
		this.acc_value_date = acc_value_date;
	}

	public String getExport_type() {
		return export_type;
	}

	public void setExport_type(String export_type) {
		this.export_type = export_type;
	}
	
}
