package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPMS_DAILY_FIPETLVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBPMS_DAILY_FIPETLPK comp_id;

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
    private String EMP_NAME;

    /** nullable persistent field */
    private String CUST_ID;

    /** nullable persistent field */
    private String CUST_NAME;

    /** nullable persistent field */
    private String PRD_ID;

    /** nullable persistent field */
    private String PRD_NAME;

    /** nullable persistent field */
    private BigDecimal S_TXN_AMT;

    /** nullable persistent field */
    private BigDecimal INTEREST;

    /** nullable persistent field */
    private BigDecimal S_TXN_FEE;

    /** nullable persistent field */
    private BigDecimal B_TXN_AMT;

    /** nullable persistent field */
    private BigDecimal B_TXN_FEE;

    /** nullable persistent field */
    private BigDecimal S_TXN_LOSS;

    /** nullable persistent field */
    private String NOTE;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBPMS_DAILY_FIPETL";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBPMS_DAILY_FIPETLVO(com.systex.jbranch.app.common.fps.table.TBPMS_DAILY_FIPETLPK comp_id, String REGION_CENTER_ID, String REGION_CENTER_NAME, String BRANCH_AREA_ID, String BRANCH_AREA_NAME, String BRANCH_NBR, String BRANCH_NAME, String AO_CODE, String EMP_ID, String EMP_NAME, String CUST_ID, String CUST_NAME, String PRD_ID, String PRD_NAME, BigDecimal S_TXN_AMT, BigDecimal INTEREST, BigDecimal S_TXN_FEE, BigDecimal B_TXN_AMT, BigDecimal B_TXN_FEE, BigDecimal S_TXN_LOSS, String NOTE, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.comp_id = comp_id;
        this.REGION_CENTER_ID = REGION_CENTER_ID;
        this.REGION_CENTER_NAME = REGION_CENTER_NAME;
        this.BRANCH_AREA_ID = BRANCH_AREA_ID;
        this.BRANCH_AREA_NAME = BRANCH_AREA_NAME;
        this.BRANCH_NBR = BRANCH_NBR;
        this.BRANCH_NAME = BRANCH_NAME;
        this.AO_CODE = AO_CODE;
        this.EMP_ID = EMP_ID;
        this.EMP_NAME = EMP_NAME;
        this.CUST_ID = CUST_ID;
        this.CUST_NAME = CUST_NAME;
        this.PRD_ID = PRD_ID;
        this.PRD_NAME = PRD_NAME;
        this.S_TXN_AMT = S_TXN_AMT;
        this.INTEREST = INTEREST;
        this.S_TXN_FEE = S_TXN_FEE;
        this.B_TXN_AMT = B_TXN_AMT;
        this.B_TXN_FEE = B_TXN_FEE;
        this.S_TXN_LOSS = S_TXN_LOSS;
        this.NOTE = NOTE;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBPMS_DAILY_FIPETLVO() {
    }

    /** minimal constructor */
    public TBPMS_DAILY_FIPETLVO(com.systex.jbranch.app.common.fps.table.TBPMS_DAILY_FIPETLPK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.app.common.fps.table.TBPMS_DAILY_FIPETLPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBPMS_DAILY_FIPETLPK comp_id) {
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

    public String getEMP_NAME() {
        return this.EMP_NAME;
    }

    public void setEMP_NAME(String EMP_NAME) {
        this.EMP_NAME = EMP_NAME;
    }

    public String getCUST_ID() {
        return this.CUST_ID;
    }

    public void setCUST_ID(String CUST_ID) {
        this.CUST_ID = CUST_ID;
    }

    public String getCUST_NAME() {
        return this.CUST_NAME;
    }

    public void setCUST_NAME(String CUST_NAME) {
        this.CUST_NAME = CUST_NAME;
    }

    public String getPRD_ID() {
        return this.PRD_ID;
    }

    public void setPRD_ID(String PRD_ID) {
        this.PRD_ID = PRD_ID;
    }

    public String getPRD_NAME() {
        return this.PRD_NAME;
    }

    public void setPRD_NAME(String PRD_NAME) {
        this.PRD_NAME = PRD_NAME;
    }

    public BigDecimal getS_TXN_AMT() {
        return this.S_TXN_AMT;
    }

    public void setS_TXN_AMT(BigDecimal S_TXN_AMT) {
        this.S_TXN_AMT = S_TXN_AMT;
    }

    public BigDecimal getINTEREST() {
        return this.INTEREST;
    }

    public void setINTEREST(BigDecimal INTEREST) {
        this.INTEREST = INTEREST;
    }

    public BigDecimal getS_TXN_FEE() {
        return this.S_TXN_FEE;
    }

    public void setS_TXN_FEE(BigDecimal S_TXN_FEE) {
        this.S_TXN_FEE = S_TXN_FEE;
    }

    public BigDecimal getB_TXN_AMT() {
        return this.B_TXN_AMT;
    }

    public void setB_TXN_AMT(BigDecimal B_TXN_AMT) {
        this.B_TXN_AMT = B_TXN_AMT;
    }

    public BigDecimal getB_TXN_FEE() {
        return this.B_TXN_FEE;
    }

    public void setB_TXN_FEE(BigDecimal B_TXN_FEE) {
        this.B_TXN_FEE = B_TXN_FEE;
    }

    public BigDecimal getS_TXN_LOSS() {
        return this.S_TXN_LOSS;
    }

    public void setS_TXN_LOSS(BigDecimal S_TXN_LOSS) {
        this.S_TXN_LOSS = S_TXN_LOSS;
    }

    public String getNOTE() {
        return this.NOTE;
    }

    public void setNOTE(String NOTE) {
        this.NOTE = NOTE;
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
        if ( !(other instanceof TBPMS_DAILY_FIPETLVO) ) return false;
        TBPMS_DAILY_FIPETLVO castOther = (TBPMS_DAILY_FIPETLVO) other;
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
