package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBINS_PLAN_MAINVO extends VOBase {

    /** identifier field */
    private String PLAN_KEYNO;

    /** persistent field */
    private String CUST_ID;

    /** persistent field */
    private Timestamp PLAN_DATE;

    /** nullable persistent field */
    private String PRINT_YN;

    /** nullable persistent field */
    private Timestamp PRINT_DATE;

    /** nullable persistent field */
    private BigDecimal LIFE_EXPENSE;

    /** nullable persistent field */
    private BigDecimal LIFE_EXPENSE_YEARS;

    /** nullable persistent field */
    private BigDecimal LIFE_EXPENSE_AMT;

    /** nullable persistent field */
    private BigDecimal LOAN_AMT;

    /** nullable persistent field */
    private BigDecimal EDU_FFE;

    /** nullable persistent field */
    private BigDecimal PREPARE_AMT;

    /** nullable persistent field */
    private BigDecimal NURSE_FEE;

    /** nullable persistent field */
    private BigDecimal INS_PREPARE_YEARS;

    /** nullable persistent field */
    private BigDecimal SICKROOM_FEE;

    /** nullable persistent field */
    private String DISEASE;

    /** nullable persistent field */
    private BigDecimal MAJOR_DISEASES_PAY;

    /** nullable persistent field */
    private BigDecimal NURSE_FEE_PAY;

    /** nullable persistent field */
    private String TTL_FLAG;

    /** nullable persistent field */
    private String PRO_LEVEL;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBINS_PLAN_MAIN";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBINS_PLAN_MAINVO(String PLAN_KEYNO, String CUST_ID, Timestamp PLAN_DATE, String PRINT_YN, Timestamp PRINT_DATE, BigDecimal LIFE_EXPENSE, BigDecimal LIFE_EXPENSE_YEARS, BigDecimal LIFE_EXPENSE_AMT, BigDecimal LOAN_AMT, BigDecimal EDU_FFE, BigDecimal PREPARE_AMT, BigDecimal NURSE_FEE, BigDecimal INS_PREPARE_YEARS, BigDecimal SICKROOM_FEE, String DISEASE, BigDecimal MAJOR_DISEASES_PAY, BigDecimal NURSE_FEE_PAY, String TTL_FLAG, String PRO_LEVEL, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.PLAN_KEYNO = PLAN_KEYNO;
        this.CUST_ID = CUST_ID;
        this.PLAN_DATE = PLAN_DATE;
        this.PRINT_YN = PRINT_YN;
        this.PRINT_DATE = PRINT_DATE;
        this.LIFE_EXPENSE = LIFE_EXPENSE;
        this.LIFE_EXPENSE_YEARS = LIFE_EXPENSE_YEARS;
        this.LIFE_EXPENSE_AMT = LIFE_EXPENSE_AMT;
        this.LOAN_AMT = LOAN_AMT;
        this.EDU_FFE = EDU_FFE;
        this.PREPARE_AMT = PREPARE_AMT;
        this.NURSE_FEE = NURSE_FEE;
        this.INS_PREPARE_YEARS = INS_PREPARE_YEARS;
        this.SICKROOM_FEE = SICKROOM_FEE;
        this.DISEASE = DISEASE;
        this.MAJOR_DISEASES_PAY = MAJOR_DISEASES_PAY;
        this.NURSE_FEE_PAY = NURSE_FEE_PAY;
        this.TTL_FLAG = TTL_FLAG;
        this.PRO_LEVEL = PRO_LEVEL;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBINS_PLAN_MAINVO() {
    }

    /** minimal constructor */
    public TBINS_PLAN_MAINVO(String PLAN_KEYNO, String CUST_ID, Timestamp PLAN_DATE) {
        this.PLAN_KEYNO = PLAN_KEYNO;
        this.CUST_ID = CUST_ID;
        this.PLAN_DATE = PLAN_DATE;
    }

    public String getPLAN_KEYNO() {
        return this.PLAN_KEYNO;
    }

    public void setPLAN_KEYNO(String PLAN_KEYNO) {
        this.PLAN_KEYNO = PLAN_KEYNO;
    }

    public String getCUST_ID() {
        return this.CUST_ID;
    }

    public void setCUST_ID(String CUST_ID) {
        this.CUST_ID = CUST_ID;
    }

    public Timestamp getPLAN_DATE() {
        return this.PLAN_DATE;
    }

    public void setPLAN_DATE(Timestamp PLAN_DATE) {
        this.PLAN_DATE = PLAN_DATE;
    }

    public String getPRINT_YN() {
        return this.PRINT_YN;
    }

    public void setPRINT_YN(String PRINT_YN) {
        this.PRINT_YN = PRINT_YN;
    }

    public Timestamp getPRINT_DATE() {
        return this.PRINT_DATE;
    }

    public void setPRINT_DATE(Timestamp PRINT_DATE) {
        this.PRINT_DATE = PRINT_DATE;
    }

    public BigDecimal getLIFE_EXPENSE() {
        return this.LIFE_EXPENSE;
    }

    public void setLIFE_EXPENSE(BigDecimal LIFE_EXPENSE) {
        this.LIFE_EXPENSE = LIFE_EXPENSE;
    }

    public BigDecimal getLIFE_EXPENSE_YEARS() {
        return this.LIFE_EXPENSE_YEARS;
    }

    public void setLIFE_EXPENSE_YEARS(BigDecimal LIFE_EXPENSE_YEARS) {
        this.LIFE_EXPENSE_YEARS = LIFE_EXPENSE_YEARS;
    }

    public BigDecimal getLIFE_EXPENSE_AMT() {
        return this.LIFE_EXPENSE_AMT;
    }

    public void setLIFE_EXPENSE_AMT(BigDecimal LIFE_EXPENSE_AMT) {
        this.LIFE_EXPENSE_AMT = LIFE_EXPENSE_AMT;
    }

    public BigDecimal getLOAN_AMT() {
        return this.LOAN_AMT;
    }

    public void setLOAN_AMT(BigDecimal LOAN_AMT) {
        this.LOAN_AMT = LOAN_AMT;
    }

    public BigDecimal getEDU_FFE() {
        return this.EDU_FFE;
    }

    public void setEDU_FFE(BigDecimal EDU_FFE) {
        this.EDU_FFE = EDU_FFE;
    }

    public BigDecimal getPREPARE_AMT() {
        return this.PREPARE_AMT;
    }

    public void setPREPARE_AMT(BigDecimal PREPARE_AMT) {
        this.PREPARE_AMT = PREPARE_AMT;
    }

    public BigDecimal getNURSE_FEE() {
        return this.NURSE_FEE;
    }

    public void setNURSE_FEE(BigDecimal NURSE_FEE) {
        this.NURSE_FEE = NURSE_FEE;
    }

    public BigDecimal getINS_PREPARE_YEARS() {
        return this.INS_PREPARE_YEARS;
    }

    public void setINS_PREPARE_YEARS(BigDecimal INS_PREPARE_YEARS) {
        this.INS_PREPARE_YEARS = INS_PREPARE_YEARS;
    }

    public BigDecimal getSICKROOM_FEE() {
        return this.SICKROOM_FEE;
    }

    public void setSICKROOM_FEE(BigDecimal SICKROOM_FEE) {
        this.SICKROOM_FEE = SICKROOM_FEE;
    }

    public String getDISEASE() {
        return this.DISEASE;
    }

    public void setDISEASE(String DISEASE) {
        this.DISEASE = DISEASE;
    }

    public BigDecimal getMAJOR_DISEASES_PAY() {
        return this.MAJOR_DISEASES_PAY;
    }

    public void setMAJOR_DISEASES_PAY(BigDecimal MAJOR_DISEASES_PAY) {
        this.MAJOR_DISEASES_PAY = MAJOR_DISEASES_PAY;
    }

    public BigDecimal getNURSE_FEE_PAY() {
        return this.NURSE_FEE_PAY;
    }

    public void setNURSE_FEE_PAY(BigDecimal NURSE_FEE_PAY) {
        this.NURSE_FEE_PAY = NURSE_FEE_PAY;
    }

    public String getTTL_FLAG() {
        return this.TTL_FLAG;
    }

    public void setTTL_FLAG(String TTL_FLAG) {
        this.TTL_FLAG = TTL_FLAG;
    }

    public String getPRO_LEVEL() {
        return this.PRO_LEVEL;
    }

    public void setPRO_LEVEL(String PRO_LEVEL) {
        this.PRO_LEVEL = PRO_LEVEL;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("PLAN_KEYNO", getPLAN_KEYNO())
            .toString();
    }

}
