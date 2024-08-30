package com.systex.jbranch.app.server.fps.mgm410;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class MGM410InputVO extends PagingInputVO{
	
	private String act_seq;							//活動代碼
	private String cust_id;							//客戶ID
	private String apply_seq;						//申請書序號
	private String delivery_status;					//申請狀態
	private Date s_createtime;						//申請日期(起)
	private Date e_createtime;						//申請日期(迄)
	private String gift_kind;						//贈品性質
	private List<Map<String,Object>> editList;		//整批放行
	
	public String getAct_seq() {
		return act_seq;
	}
	
	public void setAct_seq(String act_seq) {
		this.act_seq = act_seq;
	}
	
	public String getCust_id() {
		return cust_id;
	}
	
	public void setCust_id(String cust_id) {
		this.cust_id = cust_id;
	}
	
	public String getApply_seq() {
		return apply_seq;
	}
	
	public void setApply_seq(String apply_seq) {
		this.apply_seq = apply_seq;
	}
	
	public String getDelivery_status() {
		return delivery_status;
	}
	
	public void setDelivery_status(String delivery_status) {
		this.delivery_status = delivery_status;
	}
	
	public Date getS_createtime() {
		return s_createtime;
	}
	
	public void setS_createtime(Date s_createtime) {
		this.s_createtime = s_createtime;
	}
	
	public Date getE_createtime() {
		return e_createtime;
	}
	
	public void setE_createtime(Date e_createtime) {
		this.e_createtime = e_createtime;
	}
	
	public String getGift_kind() {
		return gift_kind;
	}
	
	public void setGift_kind(String gift_kind) {
		this.gift_kind = gift_kind;
	}

	public List<Map<String, Object>> getEditList() {
		return editList;
	}

	public void setEditList(List<Map<String, Object>> editList) {
		this.editList = editList;
	}
	
}
