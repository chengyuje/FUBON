package com.systex.jbranch.app.server.fps.prd177;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

import java.math.BigDecimal;
import java.sql.Date;

public class PRD177InputVO extends PagingInputVO {
    private BigDecimal productSerialNum;    // 保險產品序號
    private BigDecimal insuranceCoSerialNum;// 保險公司序號
    private String shortName;               // 保險產品簡稱
    private String prdId;                   // 英文代碼
    private String currency;                // 幣別
    private Date productValidFromSDate;     // 開始銷售日（起）
    private Date productValidFromEDate;     // 開始銷售日（迄）

    private boolean isSampleExport = false;  // 是否為下載範例檔案
    private String fileName;                 // 上傳 Temp 檔名
    private String fileRealName;             // 上傳檔名

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getPrdId() {
        return prdId;
    }

    public void setPrdId(String prdId) {
        this.prdId = prdId;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Date getProductValidFromSDate() {
        return productValidFromSDate;
    }

    public void setProductValidFromSDate(Date productValidFromSDate) {
        this.productValidFromSDate = productValidFromSDate;
    }

    public Date getProductValidFromEDate() {
        return productValidFromEDate;
    }

    public void setProductValidFromEDate(Date productValidFromEDate) {
        this.productValidFromEDate = productValidFromEDate;
    }

    public boolean isSampleExport() {
        return isSampleExport;
    }

    public void setSampleExport(boolean sampleExport) {
        isSampleExport = sampleExport;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileRealName() {
        return fileRealName;
    }

    public void setFileRealName(String fileRealName) {
        this.fileRealName = fileRealName;
    }

    public BigDecimal getProductSerialNum() {
        return productSerialNum;
    }

    public void setProductSerialNum(BigDecimal productSerialNum) {
        this.productSerialNum = productSerialNum;
    }

    public BigDecimal getInsuranceCoSerialNum() {
        return insuranceCoSerialNum;
    }

    public void setInsuranceCoSerialNum(BigDecimal insuranceCoSerialNum) {
        this.insuranceCoSerialNum = insuranceCoSerialNum;
    }
}
