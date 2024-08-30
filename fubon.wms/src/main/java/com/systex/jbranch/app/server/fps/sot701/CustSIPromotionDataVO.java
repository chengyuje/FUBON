package com.systex.jbranch.app.server.fps.sot701;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Cathy Tang on 2018/09/05.
 *
 * 客戶結構型商品推介同意註記資料VO
 */
public class CustSIPromotionDataVO {

    private String custID;  	//客戶ID
    private String isSign;    	//是否簽署推介同意書 Y/N
    private Date signDate;      //登錄日期
    private Date effectiveDate;	//有效日期
    private Date endDate;		//終止日期
    private String status;		//狀態註記 Y:有效 C:已終止 E:已失效
    
	public String getCustID() {
		return custID;
	}
	
	public void setCustID(String custID) {
		this.custID = custID;
	}
	
	public String getIsSign() {
		return isSign;
	}
	
	public void setIsSign(String isSign) {
		this.isSign = isSign;
	}
	
	public Date getSignDate() {
		return signDate;
	}
	
	public void setSignDate(Date signDate) {
		this.signDate = signDate;
	}
	
	public Date getEffectiveDate() {
		return effectiveDate;
	}
	
	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}
	
	public Date getEndDate() {
		return endDate;
	}
	
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
        
}
