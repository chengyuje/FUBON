package com.systex.jbranch.app.server.fps.iot150;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class IOT150NotFbInputVO extends PagingInputVO {
    private boolean isRePrint;        // 是否補印
    private boolean isNewReg;         // 是否為新契約
    private Date aftSignDateStart;    // （起）總行點收送件(簽署後)日期
    private Date aftSignDateEnd;      // （迄）總行點收送件(簽署後)日期
    private String aftSignOprId;      // 點收經辦員編
    private BigDecimal companyNum;    // 保險公司序號
    private Date notFbBatchDateStart; // （起）送保險公司日期
    private Date notFbBatchDateEnd;   // （迄）送保險公司日期
    private Map<BigDecimal, String> insKeyNoMap;// 「產生送件明細表」所勾選的項目，結構為 Map<TBIOT_MAIN.INS_KEYNO, TBIOT_MAIN.REMARK_BANK>

    public boolean isRePrint() {
        return isRePrint;
    }

    public void setRePrint(boolean rePrint) {
        isRePrint = rePrint;
    }

    public boolean isNewReg() {
        return isNewReg;
    }

    public void setNewReg(boolean newReg) {
        isNewReg = newReg;
    }

    public Date getAftSignDateStart() {
        return aftSignDateStart;
    }

    public void setAftSignDateStart(Date aftSignDateStart) {
        this.aftSignDateStart = aftSignDateStart;
    }

    public Date getAftSignDateEnd() {
        return aftSignDateEnd;
    }

    public void setAftSignDateEnd(Date aftSignDateEnd) {
        this.aftSignDateEnd = aftSignDateEnd;
    }

    public String getAftSignOprId() {
        return aftSignOprId;
    }

    public void setAftSignOprId(String aftSignOprId) {
        this.aftSignOprId = aftSignOprId;
    }

    public BigDecimal getCompanyNum() {
        return companyNum;
    }

    public void setCompanyNum(BigDecimal companyNum) {
        this.companyNum = companyNum;
    }

    public Date getNotFbBatchDateStart() {
        return notFbBatchDateStart;
    }

    public void setNotFbBatchDateStart(Date notFbBatchDateStart) {
        this.notFbBatchDateStart = notFbBatchDateStart;
    }

    public Date getNotFbBatchDateEnd() {
        return notFbBatchDateEnd;
    }

    public void setNotFbBatchDateEnd(Date notFbBatchDateEnd) {
        this.notFbBatchDateEnd = notFbBatchDateEnd;
    }

    public Map<BigDecimal, String> getInsKeyNoMap() {
        return insKeyNoMap;
    }

    public void setInsKeyNoMap(Map<BigDecimal, String> insKeyNoMap) {
        this.insKeyNoMap = insKeyNoMap;
    }
}
