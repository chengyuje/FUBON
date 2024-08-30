package com.systex.jbranch.app.server.fps.jsb110;

import java.sql.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class JSB110InputVO extends PagingInputVO{
	private String fileName;
	private String fileRealName;
	private String ins_com_id;
	private Date data_date;
	private String ins_type;
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

	public Date getData_date() {
		return data_date;
	}

	public void setData_date(Date data_date) {
		this.data_date = data_date;
	}

	public String getIns_type() {
		return ins_type;
	}

	public void setIns_type(String ins_type) {
		this.ins_type = ins_type;
	}

	public String getExport_type() {
		return export_type;
	}

	public void setExport_type(String export_type) {
		this.export_type = export_type;
	}
	
}
