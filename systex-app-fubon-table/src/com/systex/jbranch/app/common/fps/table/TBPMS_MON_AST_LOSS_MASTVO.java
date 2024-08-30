package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPMS_MON_AST_LOSS_MASTVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBPMS_MON_AST_LOSS_MASTPK comp_id;

    /** nullable persistent field */
    private String REGION_CENTER_ID;

    /** nullable persistent field */
    private String REGION_CENTER_NAME;

    /** nullable persistent field */
    private String BRANCH_AREA_ID;

    /** nullable persistent field */
    private String BRANCH_AREA_NAME;

    /** nullable persistent field */
    private String BRANCH_NBR;

    /** nullable persistent field */
    private String BRANCH_NAME;

    /** nullable persistent field */
    private String AO_CODE;

    /** nullable persistent field */
    private String EMP_ID;

    /** nullable persistent field */
    private String CUST_NAME;

    /** nullable persistent field */
    private BigDecimal FUND_VALU;

    /** nullable persistent field */
    private BigDecimal FUND_COST;

    /** nullable persistent field */
    private BigDecimal FUND_LOSS_RATE;

    /** nullable persistent field */
    private BigDecimal ETF_VALU;

    /** nullable persistent field */
    private BigDecimal ETF_COST;

    /** nullable persistent field */
    private BigDecimal ETF_LOSS_RATE;

    /** nullable persistent field */
    private BigDecimal STK_VALU;

    /** nullable persistent field */
    private BigDecimal STK_COST;

    /** nullable persistent field */
    private BigDecimal STK_LOSS_RATE;

    /** nullable persistent field */
    private String INTERVIEW_YN;

    /** nullable persistent field */
    private BigDecimal SI_VALU;

    /** nullable persistent field */
    private BigDecimal SI_COST;

    /** nullable persistent field */
    private BigDecimal SI_LOSS_RATE;

    /** nullable persistent field */
    private BigDecimal SN_VALU;

    /** nullable persistent field */
    private BigDecimal SN_COST;

    /** nullable persistent field */
    private BigDecimal SN_LOSS_RATE;

    /** nullable persistent field */
    private BigDecimal BND_VALU;

    /** nullable persistent field */
    private BigDecimal BND_COST;

    /** nullable persistent field */
    private BigDecimal BND_LOSS_RATE;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBPMS_MON_AST_LOSS_MAST";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBPMS_MON_AST_LOSS_MASTVO(com.systex.jbranch.app.common.fps.table.TBPMS_MON_AST_LOSS_MASTPK comp_id, String REGION_CENTER_ID, String REGION_CENTER_NAME, String BRANCH_AREA_ID, String BRANCH_AREA_NAME, String BRANCH_NBR, String BRANCH_NAME, String AO_CODE, String EMP_ID, String CUST_NAME, BigDecimal FUND_VALU, BigDecimal FUND_COST, BigDecimal FUND_LOSS_RATE, BigDecimal ETF_VALU, BigDecimal ETF_COST, BigDecimal ETF_LOSS_RATE, BigDecimal STK_VALU, BigDecimal STK_COST, BigDecimal STK_LOSS_RATE, String INTERVIEW_YN, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, BigDecimal SI_VALU, BigDecimal SI_COST, BigDecimal SI_LOSS_RATE, BigDecimal SN_VALU, BigDecimal SN_COST, BigDecimal SN_LOSS_RATE, BigDecimal BND_VALU, BigDecimal BND_COST, BigDecimal BND_LOSS_RATE, Long version) {
        this.comp_id = comp_id;
        this.REGION_CENTER_ID = REGION_CENTER_ID;
        this.REGION_CENTER_NAME = REGION_CENTER_NAME;
        this.BRANCH_AREA_ID = BRANCH_AREA_ID;
        this.BRANCH_AREA_NAME = BRANCH_AREA_NAME;
        this.BRANCH_NBR = BRANCH_NBR;
        this.BRANCH_NAME = BRANCH_NAME;
        this.AO_CODE = AO_CODE;
        this.EMP_ID = EMP_ID;
        this.CUST_NAME = CUST_NAME;
        this.FUND_VALU = FUND_VALU;
        this.FUND_COST = FUND_COST;
        this.FUND_LOSS_RATE = FUND_LOSS_RATE;
        this.ETF_VALU = ETF_VALU;
        this.ETF_COST = ETF_COST;
        this.ETF_LOSS_RATE = ETF_LOSS_RATE;
        this.STK_VALU = STK_VALU;
        this.STK_COST = STK_COST;
        this.STK_LOSS_RATE = STK_LOSS_RATE;
        this.INTERVIEW_YN = INTERVIEW_YN;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.SI_VALU = SI_VALU;
        this.SI_COST = SI_COST;
        this.SI_LOSS_RATE = SI_LOSS_RATE;
        this.SN_VALU = SN_VALU;
        this.SN_COST = SN_COST;
        this.SN_LOSS_RATE = SN_LOSS_RATE;
        this.BND_VALU = BND_VALU;
        this.BND_COST = BND_COST;
        this.BND_LOSS_RATE = BND_LOSS_RATE;
        this.version = version;
    }

    /** default constructor */
    public TBPMS_MON_AST_LOSS_MASTVO() {
    }

    /** minimal constructor */
    public TBPMS_MON_AST_LOSS_MASTVO(com.systex.jbranch.app.common.fps.table.TBPMS_MON_AST_LOSS_MASTPK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.app.common.fps.table.TBPMS_MON_AST_LOSS_MASTPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBPMS_MON_AST_LOSS_MASTPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getREGION_CENTER_ID() {
        return this.REGION_CENTER_ID;
    }

    public void setREGION_CENTER_ID(String REGION_CENTER_ID) {
        this.REGION_CENTER_ID = REGION_CENTER_ID;
    }

    public String getREGION_CENTER_NAME() {
        return this.REGION_CENTER_NAME;
    }

    public void setREGION_CENTER_NAME(String REGION_CENTER_NAME) {
        this.REGION_CENTER_NAME = REGION_CENTER_NAME;
    }

    public String getBRANCH_AREA_ID() {
        return this.BRANCH_AREA_ID;
    }

    public void setBRANCH_AREA_ID(String BRANCH_AREA_ID) {
        this.BRANCH_AREA_ID = BRANCH_AREA_ID;
    }

    public String getBRANCH_AREA_NAME() {
        return this.BRANCH_AREA_NAME;
    }

    public void setBRANCH_AREA_NAME(String BRANCH_AREA_NAME) {
        this.BRANCH_AREA_NAME = BRANCH_AREA_NAME;
    }

    public String getBRANCH_NBR() {
        return this.BRANCH_NBR;
    }

    public void setBRANCH_NBR(String BRANCH_NBR) {
        this.BRANCH_NBR = BRANCH_NBR;
    }

    public String getBRANCH_NAME() {
        return this.BRANCH_NAME;
    }

    public void setBRANCH_NAME(String BRANCH_NAME) {
        this.BRANCH_NAME = BRANCH_NAME;
    }

    public String getAO_CODE() {
        return this.AO_CODE;
    }

    public void setAO_CODE(String AO_CODE) {
        this.AO_CODE = AO_CODE;
    }

    public String getEMP_ID() {
        return this.EMP_ID;
    }

    public void setEMP_ID(String EMP_ID) {
        this.EMP_ID = EMP_ID;
    }

    public String getCUST_NAME() {
        return this.CUST_NAME;
    }

    public void setCUST_NAME(String CUST_NAME) {
        this.CUST_NAME = CUST_NAME;
    }

    public BigDecimal getFUND_VALU() {
        return this.FUND_VALU;
    }

    public void setFUND_VALU(BigDecimal FUND_VALU) {
        this.FUND_VALU = FUND_VALU;
    }

    public BigDecimal getFUND_COST() {
        return this.FUND_COST;
    }

    public void setFUND_COST(BigDecimal FUND_COST) {
        this.FUND_COST = FUND_COST;
    }

    public BigDecimal getFUND_LOSS_RATE() {
        return this.FUND_LOSS_RATE;
    }

    public void setFUND_LOSS_RATE(BigDecimal FUND_LOSS_RATE) {
        this.FUND_LOSS_RATE = FUND_LOSS_RATE;
    }

    public BigDecimal getETF_VALU() {
        return this.ETF_VALU;
    }

    public void setETF_VALU(BigDecimal ETF_VALU) {
        this.ETF_VALU = ETF_VALU;
    }

    public BigDecimal getETF_COST() {
        return this.ETF_COST;
    }

    public void setETF_COST(BigDecimal ETF_COST) {
        this.ETF_COST = ETF_COST;
    }

    public BigDecimal getETF_LOSS_RATE() {
        return this.ETF_LOSS_RATE;
    }

    public void setETF_LOSS_RATE(BigDecimal ETF_LOSS_RATE) {
        this.ETF_LOSS_RATE = ETF_LOSS_RATE;
    }

    public BigDecimal getSTK_VALU() {
        return this.STK_VALU;
    }

    public void setSTK_VALU(BigDecimal STK_VALU) {
        this.STK_VALU = STK_VALU;
    }

    public BigDecimal getSTK_COST() {
        return this.STK_COST;
    }

    public void setSTK_COST(BigDecimal STK_COST) {
        this.STK_COST = STK_COST;
    }

    public BigDecimal getSTK_LOSS_RATE() {
        return this.STK_LOSS_RATE;
    }

    public void setSTK_LOSS_RATE(BigDecimal STK_LOSS_RATE) {
        this.STK_LOSS_RATE = STK_LOSS_RATE;
    }

    public String getINTERVIEW_YN() {
        return this.INTERVIEW_YN;
    }

    public void setINTERVIEW_YN(String INTERVIEW_YN) {
        this.INTERVIEW_YN = INTERVIEW_YN;
    }

    public BigDecimal getSI_VALU() {
        return this.SI_VALU;
    }

    public void setSI_VALU(BigDecimal SI_VALU) {
        this.SI_VALU = SI_VALU;
    }

    public BigDecimal getSI_COST() {
        return this.SI_COST;
    }

    public void setSI_COST(BigDecimal SI_COST) {
        this.SI_COST = SI_COST;
    }

    public BigDecimal getSI_LOSS_RATE() {
        return this.SI_LOSS_RATE;
    }

    public void setSI_LOSS_RATE(BigDecimal SI_LOSS_RATE) {
        this.SI_LOSS_RATE = SI_LOSS_RATE;
    }

    public BigDecimal getSN_VALU() {
        return this.SN_VALU;
    }

    public void setSN_VALU(BigDecimal SN_VALU) {
        this.SN_VALU = SN_VALU;
    }

    public BigDecimal getSN_COST() {
        return this.SN_COST;
    }

    public void setSN_COST(BigDecimal SN_COST) {
        this.SN_COST = SN_COST;
    }

    public BigDecimal getSN_LOSS_RATE() {
        return this.SN_LOSS_RATE;
    }

    public void setSN_LOSS_RATE(BigDecimal SN_LOSS_RATE) {
        this.SN_LOSS_RATE = SN_LOSS_RATE;
    }

    public BigDecimal getBND_VALU() {
        return this.BND_VALU;
    }

    public void setBND_VALU(BigDecimal BND_VALU) {
        this.BND_VALU = BND_VALU;
    }

    public BigDecimal getBND_COST() {
        return this.BND_COST;
    }

    public void setBND_COST(BigDecimal BND_COST) {
        this.BND_COST = BND_COST;
    }

    public BigDecimal getBND_LOSS_RATE() {
        return this.BND_LOSS_RATE;
    }

    public void setBND_LOSS_RATE(BigDecimal BND_LOSS_RATE) {
        this.BND_LOSS_RATE = BND_LOSS_RATE;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getcomp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBPMS_MON_AST_LOSS_MASTVO) ) return false;
        TBPMS_MON_AST_LOSS_MASTVO castOther = (TBPMS_MON_AST_LOSS_MASTVO) other;
        return new EqualsBuilder()
            .append(this.getcomp_id(), castOther.getcomp_id())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getcomp_id())
            .toHashCode();
    }

}
