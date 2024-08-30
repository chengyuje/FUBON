package com.systex.jbranch.app.server.fps.kyc520;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

import java.sql.Date;
import java.util.Calendar;

public class KYC520InputVO extends PagingInputVO{
    private Date approveStartDate;  //主管建立日期(起)
    private Date approveEndDate;    //主管建立日期(迄)
    private Date submitStartDate;   //經辦提交日期(起)
    private Date submitEndDate;     //經辦提交日期(迄)
    private String   aoCode;            //經辦員編
    private String   quesName;          //參數名稱
    private String   approveStatus;     //簽核狀態

    private String   examVersion;       //問卷版本
    private String   questionVersion;   //題目序號
    private String   rlVersion;         //風險級距編號
    private Date     expiryDate;        //失效日
    private String 	 empID;             //當前使用者員編
    private String   empName;           //當前使用者姓名
    private String   branchID;          //當前使用者分行代碼
    private String   remark;            //審核備註

    public Date getApproveStartDate() {
		return approveStartDate;
	}

	public Date getApproveEndDate() {
		return approveEndDate;
	}

	public Date getSubmitStartDate() {
		return submitStartDate;
	}

	public Date getSubmitEndDate() {
		return submitEndDate;
	}

	public void setApproveStartDate(Date approveStartDate) {
		this.approveStartDate = approveStartDate;
	}

	public void setApproveEndDate(Date approveEndDate) {
		this.approveEndDate = approveEndDate;
	}

	public void setSubmitStartDate(Date submitStartDate) {
		this.submitStartDate = submitStartDate;
	}

	public void setSubmitEndDate(Date submitEndDate) {
		this.submitEndDate = submitEndDate;
	}

	public String getAoCode() {
        return aoCode;
    }

    public void setAoCode(String aoCode) {
        this.aoCode = aoCode;
    }

    public String getQuesName() {
        return quesName;
    }

    public void setQuesName(String quesName) {
        this.quesName = quesName;
    }

    public String getApproveStatus() {
        return approveStatus;
    }

    public void setApproveStatus(String approveStatus) {
        this.approveStatus = approveStatus;
    }

    public String getExamVersion() {
        return examVersion;
    }

    public void setExamVersion(String examVersion) {
        this.examVersion = examVersion;
    }

    public String getQuestionVersion() {
        return questionVersion;
    }

    public void setQuestionVersion(String questionVersion) {
        this.questionVersion = questionVersion;
    }

    public String getRlVersion() {
        return rlVersion;
    }

    public void setRlVersion(String rlVersion) {
        this.rlVersion = rlVersion;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getEmpID() {
        return empID;
    }

    public void setEmpID(String empID) {
        this.empID = empID;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public String getBranchID() {
        return branchID;
    }

    public void setBranchID(String branchID) {
        this.branchID = branchID;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getRemark() {
        return remark;
    }

    @Override
    public String toString() {
        return "KYC520InputVO{" +
                "approveStartDate=" + approveStartDate +
                ", approveEndDate=" + approveEndDate +
                ", submitStartDate=" + submitStartDate +
                ", submitEndDate=" + submitEndDate +
                ", aoCode='" + aoCode + '\'' +
                ", quesName='" + quesName + '\'' +
                ", approveStatus='" + approveStatus + '\'' +
                ", examVersion='" + examVersion + '\'' +
                ", questionVersion='" + questionVersion + '\'' +
                ", rlVersion='" + rlVersion + '\'' +
                ", expiryDate=" + expiryDate +
                ", empID='" + empID + '\'' +
                ", empName='" + empName + '\'' +
                ", branchID='" + branchID + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}
