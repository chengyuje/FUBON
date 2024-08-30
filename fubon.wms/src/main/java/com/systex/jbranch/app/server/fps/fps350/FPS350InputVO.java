package com.systex.jbranch.app.server.fps.fps350;

import java.util.Date;
import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class FPS350InputVO extends PagingInputVO {

    public FPS350InputVO() {
    }

    private String planID;
    private String custID;
    private String planStatus;
    private Date SD;
    private Date ED;
    private String isDisable;

    // print 
    private String SEQNO;

    public String getPlanID() {
        return planID;
    }

    public void setPlanID(String planID) {
        this.planID = planID;
    }

    public String getCustID() {
        return custID;
    }

    public void setCustID(String custID) {
        this.custID = custID;
    }

    public Date getSD() {
        return SD;
    }

    public void setSD(Date sD) {
        SD = sD;
    }

    public Date getED() {
        return ED;
    }

    public void setED(Date eD) {
        ED = eD;
    }

    public String getIsDisable() {
        return isDisable;
    }

    public void setIsDisable(String isDisable) {
        this.isDisable = isDisable;
    }

    public String getPlanStatus() {
        return planStatus;
    }

    public void setPlanStatus(String planStatus) {
        this.planStatus = planStatus;
    }

	public String getSEQNO() {
		return SEQNO;
	}

	public void setSEQNO(String sEQNO) {
		SEQNO = sEQNO;
	}
    
    

}
