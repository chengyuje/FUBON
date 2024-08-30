package com.systex.jbranch.app.server.fps.prd176;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

import java.math.BigDecimal;
import java.sql.Date;

public class PRD176InputVO extends PagingInputVO {
    private BigDecimal serialNum;            // 序號
    private String name;                     // 保險公司名稱
    private Date contractSDate;              // 簽約日期（起）
    private Date contractEDate;              // 簽約日期（迄）
    private Date renewSDate;                 // 續約日期（起）
    private Date renewEDate;                 // 續約日期（迄）
    private boolean isSampleExport = false;  // 是否為下載範例檔案
    private String fileName;                 // 上傳 Temp 檔名
    private String fileRealName;             // 上傳檔名

    public BigDecimal getSerialNum() {
        return serialNum;
    }

    public void setSerialNum(BigDecimal serialNum) {
        this.serialNum = serialNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getContractSDate() {
        return contractSDate;
    }

    public void setContractSDate(Date contractSDate) {
        this.contractSDate = contractSDate;
    }

    public Date getContractEDate() {
        return contractEDate;
    }

    public void setContractEDate(Date contractEDate) {
        this.contractEDate = contractEDate;
    }

    public Date getRenewSDate() {
        return renewSDate;
    }

    public void setRenewSDate(Date renewSDate) {
        this.renewSDate = renewSDate;
    }

    public Date getRenewEDate() {
        return renewEDate;
    }

    public void setRenewEDate(Date renewEDate) {
        this.renewEDate = renewEDate;
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
}
