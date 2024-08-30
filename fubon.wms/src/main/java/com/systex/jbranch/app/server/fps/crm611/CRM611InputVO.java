package com.systex.jbranch.app.server.fps.crm611;

import com.systex.jbranch.fubon.commons.cbs.vo.basic.CBSUtilOutputVO;
import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

import java.util.List;

public class CRM611InputVO extends PagingInputVO{
	private String cust_id;
	private String infrom_way;

	private List<CBSUtilOutputVO> data067050_067101_2;// 電文資料 自然人 _067050_067101、法人: _067050_067102
	private List<CBSUtilOutputVO> data067050_067000;  // 電文資料_067050_067000
	private List<CBSUtilOutputVO> data067050_067112;  // 電文資料_067050_067112
	
	public String getCust_id() {
		return cust_id;
	}
	public void setCust_id(String cust_id) {
		this.cust_id = cust_id;
	}
	public String getInfrom_way() {
		return infrom_way;
	}
	public void setInfrom_way(String infrom_way) {
		this.infrom_way = infrom_way;
	}


	public List<CBSUtilOutputVO> getData067050_067101_2() {
		return data067050_067101_2;
	}

	public void setData067050_067101_2(List<CBSUtilOutputVO> data067050_067101_2) {
		this.data067050_067101_2 = data067050_067101_2;
	}

	public List<CBSUtilOutputVO> getData067050_067000() {
		return data067050_067000;
	}

	public void setData067050_067000(List<CBSUtilOutputVO> data067050_067000) {
		this.data067050_067000 = data067050_067000;
	}

	public List<CBSUtilOutputVO> getData067050_067112() {
		return data067050_067112;
	}

	public void setData067050_067112(List<CBSUtilOutputVO> data067050_067112) {
		this.data067050_067112 = data067050_067112;
	}
}
