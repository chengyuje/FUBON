package com.systex.jbranch.ws.external.service.domain.insurance_status;

import java.util.Date;

public class InsuranceStatusOutputVO {
    private String docNum;              //	文件編號
    private String policyNum;           //	保單號碼
    private String applyDate;           //	申請日期
    private String holderName;          //	被保險人
    private String holderIdentityNum;   //	被保險人ID
    private String insuredName;         //	要保人
    private String insuredIdentityId;   //	要保人ID
    private String processStatus;       //	處理階段
    private String notifyDate;          //	照會日期（民國年 yyy/MM/dd）
    private String closeDate = "";      //	結案日期（民國年 yyy/MM/dd）
    private String deliveryWay = "";    //	保單寄送方式
    private String policyDate = "";     //	核保日期（民國年 yyy/MM/dd）
    private String issueDate;           //	發單日期（民國年 yyy/MM/dd）
    private String contractStartDate = "";   //	契約始期（民國年 yyy/MM/dd）
    private String cancelRsn = "";      //	取消原因
    private String bookingNum;          //	掛號號碼
    private String beTelCheckDate = ""; //	承保前電訪日期（民國年 yyy/MM/dd）
    private String afTelCheckDate = ""; //	承保後電訪日期（民國年 yyy/MM/dd）
    private String beTelCheckResult = "";    //	承保前電訪結果
    private String afTelCheckResult = "";    //	承保後電訪結果
    private String insuranceType;      //	主約險種
    private String payDate = "";       //	(預定)請款日期（民國年 yyy/MM/dd）
    private String payResult = "";     //	請款結果
    private String chargeAccNum = "";  //	扣款帳號/卡號
    private String authBankCode = "";  //	授權銀行
    private String authBankResult = "";//	核印/核卡結果

    public String getDocNum() {
        return docNum;
    }

    public void setDocNum(String docNum) {
        this.docNum = docNum;
    }

    public String getPolicyNum() {
        return policyNum;
    }

    public void setPolicyNum(String policyNum) {
        this.policyNum = policyNum;
    }

    public String getApplyDate() {
        return applyDate;
    }

    public void setApplyDate(String applyDate) {
        this.applyDate = applyDate;
    }

    public String getHolderName() {
        return holderName;
    }

    public void setHolderName(String holderName) {
        this.holderName = holderName;
    }

    public String getHolderIdentityNum() {
        return holderIdentityNum;
    }

    public void setHolderIdentityNum(String holderIdentityNum) {
        this.holderIdentityNum = holderIdentityNum;
    }

    public String getInsuredName() {
        return insuredName;
    }

    public void setInsuredName(String insuredName) {
        this.insuredName = insuredName;
    }

    public String getInsuredIdentityId() {
        return insuredIdentityId;
    }

    public void setInsuredIdentityId(String insuredIdentityId) {
        this.insuredIdentityId = insuredIdentityId;
    }

    public String getProcessStatus() {
        return processStatus;
    }

    public void setProcessStatus(String processStatus) {
        this.processStatus = processStatus;
    }

    public String getNotifyDate() {
        return notifyDate;
    }

    public void setNotifyDate(String notifyDate) {
        this.notifyDate = notifyDate;
    }

    public String getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(String closeDate) {
        this.closeDate = closeDate;
    }

    public String getDeliveryWay() {
        return deliveryWay;
    }

    public void setDeliveryWay(String deliveryWay) {
        this.deliveryWay = deliveryWay;
    }

    public String getPolicyDate() {
        return policyDate;
    }

    public void setPolicyDate(String policyDate) {
        this.policyDate = policyDate;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }

    public String getContractStartDate() {
        return contractStartDate;
    }

    public void setContractStartDate(String contractStartDate) {
        this.contractStartDate = contractStartDate;
    }

    public String getCancelRsn() {
        return cancelRsn;
    }

    public void setCancelRsn(String cancelRsn) {
        this.cancelRsn = cancelRsn;
    }

    public String getBookingNum() {
        return bookingNum;
    }

    public void setBookingNum(String bookingNum) {
        this.bookingNum = bookingNum;
    }

    public String getBeTelCheckDate() {
        return beTelCheckDate;
    }

    public void setBeTelCheckDate(String beTelCheckDate) {
        this.beTelCheckDate = beTelCheckDate;
    }

    public String getAfTelCheckDate() {
        return afTelCheckDate;
    }

    public void setAfTelCheckDate(String afTelCheckDate) {
        this.afTelCheckDate = afTelCheckDate;
    }

    public String getBeTelCheckResult() {
        return beTelCheckResult;
    }

    public void setBeTelCheckResult(String beTelCheckResult) {
        this.beTelCheckResult = beTelCheckResult;
    }

    public String getAfTelCheckResult() {
        return afTelCheckResult;
    }

    public void setAfTelCheckResult(String afTelCheckResult) {
        this.afTelCheckResult = afTelCheckResult;
    }

    public String getInsuranceType() {
        return insuranceType;
    }

    public void setInsuranceType(String insuranceType) {
        this.insuranceType = insuranceType;
    }

    public String getPayDate() {
        return payDate;
    }

    public void setPayDate(String payDate) {
        this.payDate = payDate;
    }

    public String getPayResult() {
        return payResult;
    }

    public void setPayResult(String payResult) {
        this.payResult = payResult;
    }

    public String getChargeAccNum() {
        return chargeAccNum;
    }

    public void setChargeAccNum(String chargeAccNum) {
        this.chargeAccNum = chargeAccNum;
    }

    public String getAuthBankCode() {
        return authBankCode;
    }

    public void setAuthBankCode(String authBankCode) {
        this.authBankCode = authBankCode;
    }

    public String getAuthBankResult() {
        return authBankResult;
    }

    public void setAuthBankResult(String authBankResult) {
        this.authBankResult = authBankResult;
    }
}
