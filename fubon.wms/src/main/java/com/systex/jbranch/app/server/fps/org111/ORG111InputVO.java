package com.systex.jbranch.app.server.fps.org111;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class ORG111InputVO extends PagingInputVO{

	private String EMP_ID;            //員工編號
	private String EMP_CELL_NUM;      //手機
	private String EMP_DEPT_EXT;      //分機
	private String REMARK;            //備註
	private String PHOTO;             //上傳照片
	private String ROLE_ID;           //角色ID
	private String REVIEW_STATUS;
	private String SEQNO;
	private String pictureName;   
	private String realpictureName;
	private String paramType;
	private String ptypeName;
	private String ptypeBuss;
	private String worksType;
	private String roleMaintain;
	private String queryType;
	private String picFlag;
	private String IS_PRIMARY_ROLE;
	
	public String getIS_PRIMARY_ROLE() {
		return IS_PRIMARY_ROLE;
	}

	public void setIS_PRIMARY_ROLE(String iS_PRIMARY_ROLE) {
		IS_PRIMARY_ROLE = iS_PRIMARY_ROLE;
	}

	public String getEMP_ID() {
		return EMP_ID;
	}
	
	public void setEMP_ID(String eMP_ID) {
		EMP_ID = eMP_ID;
	}
	
	public String getEMP_CELL_NUM() {
		return EMP_CELL_NUM;
	}
	
	public void setEMP_CELL_NUM(String eMP_CELL_NUM) {
		EMP_CELL_NUM = eMP_CELL_NUM;
	}
	
	public String getEMP_DEPT_EXT() {
		return EMP_DEPT_EXT;
	}
	
	public void setEMP_DEPT_EXT(String eMP_DEPT_EXT) {
		EMP_DEPT_EXT = eMP_DEPT_EXT;
	}
	
	public String getREMARK() {
		return REMARK;
	}
	
	public void setREMARK(String rEMARK) {
		REMARK = rEMARK;
	}
	
	public String getPHOTO() {
		return PHOTO;
	}
	
	public void setPHOTO(String pHOTO) {
		PHOTO = pHOTO;
	}
	
	public String getROLE_ID() {
		return ROLE_ID;
	}
	
	public void setROLE_ID(String rOLE_ID) {
		ROLE_ID = rOLE_ID;
	}

	public String getREVIEW_STATUS() {
		return REVIEW_STATUS;
	}

	public void setREVIEW_STATUS(String rEVIEW_STATUS) {
		REVIEW_STATUS = rEVIEW_STATUS;
	}

	public String getSEQNO() {
		return SEQNO;
	}

	public void setSEQNO(String sEQNO) {
		SEQNO = sEQNO;
	}

	public String getPictureName() {
		return pictureName;
	}
	
	public void setPictureName(String pictureName) {
		this.pictureName = pictureName;
	}
	
	public String getRealpictureName() {
		return realpictureName;
	}
	
	public void setRealpictureName(String realpictureName) {
		this.realpictureName = realpictureName;
	}
	
	public String getParamType() {
		return paramType;
	}
	
	public void setParamType(String paramType) {
		this.paramType = paramType;
	}
	
	public String getPtypeName() {
		return ptypeName;
	}
	
	public void setPtypeName(String ptypeName) {
		this.ptypeName = ptypeName;
	}
	
	public String getPtypeBuss() {
		return ptypeBuss;
	}
	
	public void setPtypeBuss(String ptypeBuss) {
		this.ptypeBuss = ptypeBuss;
	}
	
	public String getWorksType() {
		return worksType;
	}
	
	public void setWorksType(String worksType) {
		this.worksType = worksType;
	}
	
	public String getRoleMaintain() {
		return roleMaintain;
	}
	
	public void setRoleMaintain(String roleMaintain) {
		this.roleMaintain = roleMaintain;
	}
	
	public String getQueryType() {
		return queryType;
	}
	
	public void setQueryType(String queryType) {
		this.queryType = queryType;
	}

	public String getPicFlag() {
		return picFlag;
	}

	public void setPicFlag(String picFlag) {
		this.picFlag = picFlag;
	}
	
}
