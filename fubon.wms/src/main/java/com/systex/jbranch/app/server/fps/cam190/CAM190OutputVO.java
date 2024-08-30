package com.systex.jbranch.app.server.fps.cam190;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CAM190OutputVO extends PagingOutputVO {
	
	private List customList;
	private List campExpiredList;
	private List campNExpiredList;
	private List custExpiredList;
	private List custNExpiredList;
	private List resultList;
	private Integer rowUpdate;		//CONTACT CONTACT_DATE 更新筆數

	public List getCustExpiredList() {
		return custExpiredList;
	}

	public void setCustExpiredList(List custExpiredList) {
		this.custExpiredList = custExpiredList;
	}

	public List getCustNExpiredList() {
		return custNExpiredList;
	}

	public void setCustNExpiredList(List custNExpiredList) {
		this.custNExpiredList = custNExpiredList;
	}

	public List getCampNExpiredList() {
		return campNExpiredList;
	}

	public void setCampNExpiredList(List campNExpiredList) {
		this.campNExpiredList = campNExpiredList;
	}

	public List getCampExpiredList() {
		return campExpiredList;
	}

	public void setCampExpiredList(List campExpiredList) {
		this.campExpiredList = campExpiredList;
	}

	public List getResultList() {
		return resultList;
	}

	public void setResultList(List resultList) {
		this.resultList = resultList;
	}

	public List getCustomList() {
		return customList;
	}

	public void setCustomList(List customList) {
		this.customList = customList;
	}

	public Integer getRowUpdate() {
		return rowUpdate;
	}

	public void setRowUpdate(Integer rowUpdate) {
		this.rowUpdate = rowUpdate;
	}

}
