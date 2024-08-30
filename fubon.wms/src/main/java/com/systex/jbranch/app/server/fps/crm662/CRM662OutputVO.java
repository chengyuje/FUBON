package com.systex.jbranch.app.server.fps.crm662;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CRM662OutputVO extends PagingOutputVO {

	private List resultList;
	private List resultList_main; //只有主戶的時候
	private List resultList_prv;
	private List resultList_prv_add;
	private List resultList_rel_add;
	private String prv_add_err;
	private String prv_add_err_type; //申請家庭戶-錯誤類型
	private List resultList_prv_sort;
	private List resultList_prv_rpy;
	private String prv_delete;

	//
	private List resultList_prv_rpy_dc;

	public List getResultList_prv_rpy_dc() {
		return resultList_prv_rpy_dc;
	}

	public void setResultList_prv_rpy_dc(List resultList_prv_rpy_dc) {
		this.resultList_prv_rpy_dc = resultList_prv_rpy_dc;
	}

	public List getResultList_main() {
		return resultList_main;
	}

	public void setResultList_main(List resultList_main) {
		this.resultList_main = resultList_main;
	}

	public List getResultList() {
		return resultList;
	}

	public void setResultList(List resultList) {
		this.resultList = resultList;
	}

	public List getResultList_prv() {
		return resultList_prv;
	}

	public void setResultList_prv(List resultList_prv) {
		this.resultList_prv = resultList_prv;
	}

	public List getResultList_prv_add() {
		return resultList_prv_add;
	}

	public void setResultList_prv_add(List resultList_prv_add) {
		this.resultList_prv_add = resultList_prv_add;
	}

	public List getResultList_prv_sort() {
		return resultList_prv_sort;
	}

	public void setResultList_prv_sort(List resultList_prv_sort) {
		this.resultList_prv_sort = resultList_prv_sort;
	}

	public List getResultList_prv_rpy() {
		return resultList_prv_rpy;
	}

	public void setResultList_prv_rpy(List resultList_prv_rpy) {
		this.resultList_prv_rpy = resultList_prv_rpy;
	}

	public List getResultList_rel_add() {
		return resultList_rel_add;
	}

	public void setResultList_rel_add(List resultList_rel_add) {
		this.resultList_rel_add = resultList_rel_add;
	}

	public String getPrv_add_err() {
		return prv_add_err;
	}

	public void setPrv_add_err(String prv_add_err) {
		this.prv_add_err = prv_add_err;
	}

	public String getPrv_add_err_type() {
		return prv_add_err_type;
	}

	public void setPrv_add_err_type(String prv_add_err_type) {
		this.prv_add_err_type = prv_add_err_type;
	}

	public String getPrv_delete() {
		return prv_delete;
	}

	public void setPrv_delete(String prv_delete) {
		this.prv_delete = prv_delete;
	}

}
