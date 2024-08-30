package com.systex.jbranch.app.server.fps.prd232;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PRD232InputVO extends PagingInputVO {
	private String prd_id;
	private String edit_col;
	private String col;
	public String getPassParams() {
		return passParams;
	}
	public void setPassParams(String passParams) {
		this.passParams = passParams;
	}
	private List<String> id_map;
	private String status;
	
	private String fileName;
	private String fileRealName;
	
	private String passParams;   //首頁接參數
	
	private List<Map<String, Object>> review_list;
	
	
	public String getPrd_id() {
		return prd_id;
	}
	public void setPrd_id(String prd_id) {
		this.prd_id = prd_id;
	}
	public String getEdit_col() {
		return edit_col;
	}
	public void setEdit_col(String edit_col) {
		this.edit_col = edit_col;
	}
	public String getCol() {
		return col;
	}
	public void setCol(String col) {
		this.col = col;
	}
	public List<String> getId_map() {
		return id_map;
	}
	public void setId_map(List<String> id_map) {
		this.id_map = id_map;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
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
	public List<Map<String, Object>> getReview_list() {
		return review_list;
	}
	public void setReview_list(List<Map<String, Object>> review_list) {
		this.review_list = review_list;
	}
}
