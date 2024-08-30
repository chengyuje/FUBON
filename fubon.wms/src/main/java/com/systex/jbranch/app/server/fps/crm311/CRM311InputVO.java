package com.systex.jbranch.app.server.fps.crm311;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CRM311InputVO extends PagingInputVO {

	private String ao_ao_job_rank,    	/* AO理專級別  */
	limit_by_aum_yn, 					/* 是否控制AUM上限 */
	aum_limit_up,						/* 日餘額(AUM)上限  */
	ttl_cust_no_limit_up,				/* 總客戶上限  */
	cust_ao_job_rank,					/* CUST理專級別  */
	vip_degree,							/* 個人/理財會員等級  */
	cust_no_flex_prcnt,					/* 客戶數彈性%  */
	cust_no_limit_up;					/* 等級客戶數上限   */

	public String getAo_ao_job_rank() {
		return ao_ao_job_rank;
	}

	public void setAo_ao_job_rank(String ao_ao_job_rank) {
		this.ao_ao_job_rank = ao_ao_job_rank;
	}

	public String getLimit_by_aum_yn() {
		return limit_by_aum_yn;
	}

	public void setLimit_by_aum_yn(String limit_by_aum_yn) {
		this.limit_by_aum_yn = limit_by_aum_yn;
	}

	public String getAum_limit_up() {
		return aum_limit_up;
	}

	public void setAum_limit_up(String aum_limit_up) {
		this.aum_limit_up = aum_limit_up;
	}

	public String getTtl_cust_no_limit_up() {
		return ttl_cust_no_limit_up;
	}

	public void setTtl_cust_no_limit_up(String ttl_cust_no_limit_up) {
		this.ttl_cust_no_limit_up = ttl_cust_no_limit_up;
	}

	public String getCust_ao_job_rank() {
		return cust_ao_job_rank;
	}

	public void setCust_ao_job_rank(String cust_ao_job_rank) {
		this.cust_ao_job_rank = cust_ao_job_rank;
	}

	public String getVip_degree() {
		return vip_degree;
	}

	public void setVip_degree(String vip_degree) {
		this.vip_degree = vip_degree;
	}

	public String getCust_no_flex_prcnt() {
		return cust_no_flex_prcnt;
	}

	public void setCust_no_flex_prcnt(String cust_no_flex_prcnt) {
		this.cust_no_flex_prcnt = cust_no_flex_prcnt;
	}

	public String getCust_no_limit_up() {
		return cust_no_limit_up;
	}

	public void setCust_no_limit_up(String cust_no_limit_up) {
		this.cust_no_limit_up = cust_no_limit_up;
	}
}
