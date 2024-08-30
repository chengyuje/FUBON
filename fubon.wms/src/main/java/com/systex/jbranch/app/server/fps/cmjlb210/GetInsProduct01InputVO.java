package com.systex.jbranch.app.server.fps.cmjlb210;

import java.util.Date;


/**
 * Method getInsProduct01 之 inputVO
 */
public class GetInsProduct01InputVO {

	private String allProdctsID;// 產品ID
	private String strfind;		// 關鍵字
	private String cid;			// 保險公司代碼
	private String qid;			// 商品屬性編號
	private int m0;				// 主約
	private int m1;				// 附約
	private int m2;				// 現售
	private int m3;				// 停售
	private Date p_strLastUPdate;// 更新日期
	private String ifChs;		// 是否可挑選(0→不可挑選;1→可挑選;空白→全部)

	public String getAllProdctsID() {
		return allProdctsID;
	}
	public void setAllProdctsID(String allProdctsID) {
		this.allProdctsID = allProdctsID;
	}
	public String getStrfind() {
		return strfind;
	}
	public void setStrfind(String strfind) {
		this.strfind = strfind;
	}
	public String getCid() {
		return cid;
	}
	public void setCid(String cid) {
		this.cid = cid;
	}
	public String getQid() {
		return qid;
	}
	public void setQid(String qid) {
		this.qid = qid;
	}
	public int getM0() {
		return m0;
	}
	public void setM0(int m0) {
		this.m0 = m0;
	}
	public int getM1() {
		return m1;
	}
	public void setM1(int m1) {
		this.m1 = m1;
	}
	public int getM2() {
		return m2;
	}
	public void setM2(int m2) {
		this.m2 = m2;
	}
	public int getM3() {
		return m3;
	}
	public void setM3(int m3) {
		this.m3 = m3;
	}
	public Date getP_strLastUPdate() {
		return p_strLastUPdate;
	}
	public void setP_strLastUPdate(Date lastUPdate) {
		p_strLastUPdate = lastUPdate;
	}
	public String getIfChs() {
		return ifChs;
	}
	public void setIfChs(String ifChs) {
		this.ifChs = ifChs;
	}
}
