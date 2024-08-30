package com.systex.jbranch.app.server.fps.fps814;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class FPS814InputVO extends PagingInputVO {

    public FPS814InputVO() {}
    
    private String roleType;
    private String type;
    private String regionCenterID;
    private String branchAreaID;
    private String branchNBR;
    
	public String getRegionCenterID() {
		return regionCenterID;
	}
	
	public void setRegionCenterID(String regionCenterID) {
		this.regionCenterID = regionCenterID;
	}
	
	public String getBranchAreaID() {
		return branchAreaID;
	}
	
	public void setBranchAreaID(String branchAreaID) {
		this.branchAreaID = branchAreaID;
	}

	public String getBranchNBR() {
		return branchNBR;
	}

	public void setBranchNBR(String branchNBR) {
		this.branchNBR = branchNBR;
	}

	public String getRoleType() {
		return roleType;
	}

	public void setRoleType(String roleType) {
		this.roleType = roleType;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
