package com.systex.jbranch.app.server.fps.it0123;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("request")
public class IT0123InputVO {
    private String custId;  // 欲生效KYC之客戶ID
    private String prdId;   // 商品ID
    private String prdType; // 商品種類
    private ELI_MUST_UPDATE_RECORD mustUpdateRecord; // 上線必要更新 VO

    /**
     * 自動新增基金
     **/
    private String fileName;// 上傳基金檔案名稱
    
    private String cValue; //C值

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    public String getPrdId() {
        return prdId;
    }

    public void setPrdId(String prdId) {
        this.prdId = prdId;
    }

    public String getPrdType() {
        return prdType;
    }

    public void setPrdType(String prdType) {
        this.prdType = prdType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public ELI_MUST_UPDATE_RECORD getMustUpdateRecord() {
        return mustUpdateRecord;
    }

    public void setMustUpdateRecord(ELI_MUST_UPDATE_RECORD mustUpdateRecord) {
        this.mustUpdateRecord = mustUpdateRecord;
    }

	public String getcValue() {
		return cValue;
	}

	public void setcValue(String cValue) {
		this.cValue = cValue;
	}
    
    
}

