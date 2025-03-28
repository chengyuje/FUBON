package com.systex.jbranch.app.server.fps.sot701;

import java.util.Date;

/**
 * Created by SebastianWu on 2016/9/9.
 */
public class CustKYCDataVO {
    private String kycLevel;    //KYC等級
    private Date kycDueDate;    //KYC最後有效日
    private String RISK_FIT;
    
  //FOR CBS測試日期修改
    private boolean isKycDueDateUseful;  //如果Y 代表過期 如果N代表沒過期;
    
    //#2304_商品適配及申購新增KYC快到期提醒
    private boolean kycDueDateLessOneMonth; //KYC校期小於1個月    
    
    public boolean isKycDueDateUseful() {
		return isKycDueDateUseful;
	}

	public void setKycDueDateUseful(boolean isKycDueDateUseful) {
		this.isKycDueDateUseful = isKycDueDateUseful;
	}

	public String getRISK_FIT() {
		return RISK_FIT;
	}

	public void setRISK_FIT(String rISK_FIT) {
		RISK_FIT = rISK_FIT;
	}

	public String getKycLevel() {
        return kycLevel;
    }

    public void setKycLevel(String kycLevel) {
        this.kycLevel = kycLevel;
    }

    public Date getKycDueDate() {
        return kycDueDate;
    }

    public void setKycDueDate(Date kycDueDate) {
        this.kycDueDate = kycDueDate;
    }
    

    public boolean isKycDueDateLessOneMonth() {
		return kycDueDateLessOneMonth;
	}

	public void setKycDueDateLessOneMonth(boolean kycDueDateLessOneMonth) {
		this.kycDueDateLessOneMonth = kycDueDateLessOneMonth;
	}

	@Override
    public String toString() {
        return "CustKYCDataVO{" +
                "kycLevel='" + kycLevel + '\'' +
                ", kycDueDate=" + kycDueDate +
                '}';
    }
}
