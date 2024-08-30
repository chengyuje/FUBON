package com.systex.jbranch.app.server.fps.pms306;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

/**
 * 
 * Copy Right Information : <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description :保險速報作業 OutputVO<br>
 * Comments Name : PMS306OutputVO.java<br>
 * Author :Kevin<br>
 * Date :2016年08月09日 <br>
 * Version : 1.01 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年01月31日<br>
 */
public class PMS306OutputVO extends PagingOutputVO {

	private List notelist; // 註解查詢的LIST
	private List timelist; // 時間list
	private List pislist; // 批次排程時間
	private List mothlist; // 月目標list
	private List personlist; // 人壽list
	private List csvlist; //匯出list
	private String state;// 狀態
	private List totalList;
	private List Discountlist;
	private String NBR_state;
	private String BRANCH_NBR;
	private List recruitList;
	

	public List getCsvlist() {
		return csvlist;
	}

	public void setCsvlist(List csvlist) {
		this.csvlist = csvlist;
	}

	public String getBRANCH_NBR() {
		return BRANCH_NBR;
	}

	public void setBRANCH_NBR(String bRANCH_NBR) {
		BRANCH_NBR = bRANCH_NBR;
	}

	public String getNBR_state() {
		return NBR_state;
	}

	public void setNBR_state(String nBR_state) {
		this.NBR_state = nBR_state;
	}

	public List getDiscountlist() {
		return Discountlist;
	}

	public void setDiscountlist(List discountlist) {
		Discountlist = discountlist;
	}

	public List getTotalList() {
		return totalList;
	}

	public void setTotalList(List totalList) {
		this.totalList = totalList;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public List getPislist() {
		return pislist;
	}

	public void setPislist(List pislist) {
		this.pislist = pislist;
	}

	public List getNotelist() {
		return notelist;
	}

	public void setNotelist(List notelist) {
		this.notelist = notelist;
	}

	public List getTimelist() {
		return timelist;
	}

	public void setTimelist(List timelist) {
		this.timelist = timelist;
	}

	public List getMothlist() {
		return mothlist;
	}

	public void setMothlist(List mothlist) {
		this.mothlist = mothlist;
	}

	public List getPersonlist() {
		return personlist;
	}

	public void setPersonlist(List personlist) {
		this.personlist = personlist;
	}

	public List getRecruitList() {
		return recruitList;
	}

	public void setRecruitList(List recruitList) {
		this.recruitList = recruitList;
	}

}
