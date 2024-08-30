package com.systex.jbranch.ws.external.service.domain.insurance_status;

public class InsuranceStatusInputVO {
    private String docNum;          //	文件編號
    private String policyNum;       //	保單號碼
    private String queryDateStart;  //	查詢日期起（最小值：今日往前推三個月。最大值：今日。）民國年 yyy/MM/dd
    private String queryDateEnd;    //	查詢日期迄（最小值：今日往前推三個月。最大值：今日。）民國年 yyy/MM/dd
    private String salesEmployeeId; //	業務員員編
    private String holderName;      //  被保險人

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

    public String getQueryDateStart() {
        return queryDateStart;
    }

    public void setQueryDateStart(String queryDateStart) {
        this.queryDateStart = queryDateStart;
    }

    public String getQueryDateEnd() {
        return queryDateEnd;
    }

    public void setQueryDateEnd(String queryDateEnd) {
        this.queryDateEnd = queryDateEnd;
    }

    public String getSalesEmployeeId() {
        return salesEmployeeId;
    }

    public void setSalesEmployeeId(String salesEmployeeId) {
        this.salesEmployeeId = salesEmployeeId;
    }

    public String getHolderName() {
        return holderName;
    }

    public void setHolderName(String holderName) {
        this.holderName = holderName;
    }

    @Override
    public String toString() {
        return "InsuranceStatusInputVO{" +
                "docNum='" + docNum + '\'' +
                ", policyNum='" + policyNum + '\'' +
                ", queryDateStart='" + queryDateStart + '\'' +
                ", queryDateEnd='" + queryDateEnd + '\'' +
                ", salesEmployeeId='" + salesEmployeeId + '\'' +
                ", holderName='" + holderName + '\'' +
                '}';
    }
}
