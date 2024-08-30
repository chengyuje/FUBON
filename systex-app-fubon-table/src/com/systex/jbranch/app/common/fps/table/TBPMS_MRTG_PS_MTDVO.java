package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPMS_MRTG_PS_MTDVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBPMS_MRTG_PS_MTDPK comp_id;

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
    private String BRANCH_CLS;

    /** nullable persistent field */
    private String EMP_NAME;

    /** nullable persistent field */
    private BigDecimal S_HB_TAR;

    /** nullable persistent field */
    private BigDecimal S_HB_MTD_IN_AMT;

    /** nullable persistent field */
    private BigDecimal S_HB_MTD_OUT_AMT;

    /** nullable persistent field */
    private BigDecimal S_HB_MTD_RATE;

    /** nullable persistent field */
    private BigDecimal S_NHB_TAR;

    /** nullable persistent field */
    private BigDecimal S_NHB_MTD_INT_AMT;

    /** nullable persistent field */
    private BigDecimal S_NHB_MTD_OUT_AMT;

    /** nullable persistent field */
    private BigDecimal S_NHB_MTD_RATE;

    /** nullable persistent field */
    private BigDecimal S_TAR;

    /** nullable persistent field */
    private BigDecimal S_MTD_IA;

    /** nullable persistent field */
    private BigDecimal S_MTD_OA;

    /** nullable persistent field */
    private BigDecimal S_MTD_RATE;

    /** nullable persistent field */
    private BigDecimal C_NHB_TAR;

    /** nullable persistent field */
    private BigDecimal C_NHB_MTD_IN_AMT;

    /** nullable persistent field */
    private BigDecimal C_NHB_MTD_OUT_AMT;

    /** nullable persistent field */
    private BigDecimal C_NHB_MTD_RATE;

    /** nullable persistent field */
    private BigDecimal MRTG_TAR;

    /** nullable persistent field */
    private BigDecimal MRTG_MTD_IN_AMT;

    /** nullable persistent field */
    private BigDecimal MRTG_MTD_OUT_AMT;

    /** nullable persistent field */
    private BigDecimal MRTG_MTD_RATE;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBPMS_MRTG_PS_MTD";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBPMS_MRTG_PS_MTDVO(com.systex.jbranch.app.common.fps.table.TBPMS_MRTG_PS_MTDPK comp_id, String REGION_CENTER_ID, String REGION_CENTER_NAME, String BRANCH_AREA_ID, String BRANCH_AREA_NAME, String BRANCH_NBR, String BRANCH_NAME, String BRANCH_CLS, String EMP_NAME, BigDecimal S_HB_TAR, BigDecimal S_HB_MTD_IN_AMT, BigDecimal S_HB_MTD_OUT_AMT, BigDecimal S_HB_MTD_RATE, BigDecimal S_NHB_TAR, BigDecimal S_NHB_MTD_INT_AMT, BigDecimal S_NHB_MTD_OUT_AMT, BigDecimal S_NHB_MTD_RATE, BigDecimal S_TAR, BigDecimal S_MTD_IA, BigDecimal S_MTD_OA, BigDecimal S_MTD_RATE, BigDecimal C_NHB_TAR, BigDecimal C_NHB_MTD_IN_AMT, BigDecimal C_NHB_MTD_OUT_AMT, BigDecimal C_NHB_MTD_RATE, BigDecimal MRTG_TAR, BigDecimal MRTG_MTD_IN_AMT, BigDecimal MRTG_MTD_OUT_AMT, BigDecimal MRTG_MTD_RATE, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.comp_id = comp_id;
        this.REGION_CENTER_ID = REGION_CENTER_ID;
        this.REGION_CENTER_NAME = REGION_CENTER_NAME;
        this.BRANCH_AREA_ID = BRANCH_AREA_ID;
        this.BRANCH_AREA_NAME = BRANCH_AREA_NAME;
        this.BRANCH_NBR = BRANCH_NBR;
        this.BRANCH_NAME = BRANCH_NAME;
        this.BRANCH_CLS = BRANCH_CLS;
        this.EMP_NAME = EMP_NAME;
        this.S_HB_TAR = S_HB_TAR;
        this.S_HB_MTD_IN_AMT = S_HB_MTD_IN_AMT;
        this.S_HB_MTD_OUT_AMT = S_HB_MTD_OUT_AMT;
        this.S_HB_MTD_RATE = S_HB_MTD_RATE;
        this.S_NHB_TAR = S_NHB_TAR;
        this.S_NHB_MTD_INT_AMT = S_NHB_MTD_INT_AMT;
        this.S_NHB_MTD_OUT_AMT = S_NHB_MTD_OUT_AMT;
        this.S_NHB_MTD_RATE = S_NHB_MTD_RATE;
        this.S_TAR = S_TAR;
        this.S_MTD_IA = S_MTD_IA;
        this.S_MTD_OA = S_MTD_OA;
        this.S_MTD_RATE = S_MTD_RATE;
        this.C_NHB_TAR = C_NHB_TAR;
        this.C_NHB_MTD_IN_AMT = C_NHB_MTD_IN_AMT;
        this.C_NHB_MTD_OUT_AMT = C_NHB_MTD_OUT_AMT;
        this.C_NHB_MTD_RATE = C_NHB_MTD_RATE;
        this.MRTG_TAR = MRTG_TAR;
        this.MRTG_MTD_IN_AMT = MRTG_MTD_IN_AMT;
        this.MRTG_MTD_OUT_AMT = MRTG_MTD_OUT_AMT;
        this.MRTG_MTD_RATE = MRTG_MTD_RATE;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBPMS_MRTG_PS_MTDVO() {
    }

    /** minimal constructor */
    public TBPMS_MRTG_PS_MTDVO(com.systex.jbranch.app.common.fps.table.TBPMS_MRTG_PS_MTDPK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.app.common.fps.table.TBPMS_MRTG_PS_MTDPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBPMS_MRTG_PS_MTDPK comp_id) {
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

    public String getBRANCH_CLS() {
        return this.BRANCH_CLS;
    }

    public void setBRANCH_CLS(String BRANCH_CLS) {
        this.BRANCH_CLS = BRANCH_CLS;
    }

    public String getEMP_NAME() {
        return this.EMP_NAME;
    }

    public void setEMP_NAME(String EMP_NAME) {
        this.EMP_NAME = EMP_NAME;
    }

    public BigDecimal getS_HB_TAR() {
        return this.S_HB_TAR;
    }

    public void setS_HB_TAR(BigDecimal S_HB_TAR) {
        this.S_HB_TAR = S_HB_TAR;
    }

    public BigDecimal getS_HB_MTD_IN_AMT() {
        return this.S_HB_MTD_IN_AMT;
    }

    public void setS_HB_MTD_IN_AMT(BigDecimal S_HB_MTD_IN_AMT) {
        this.S_HB_MTD_IN_AMT = S_HB_MTD_IN_AMT;
    }

    public BigDecimal getS_HB_MTD_OUT_AMT() {
        return this.S_HB_MTD_OUT_AMT;
    }

    public void setS_HB_MTD_OUT_AMT(BigDecimal S_HB_MTD_OUT_AMT) {
        this.S_HB_MTD_OUT_AMT = S_HB_MTD_OUT_AMT;
    }

    public BigDecimal getS_HB_MTD_RATE() {
        return this.S_HB_MTD_RATE;
    }

    public void setS_HB_MTD_RATE(BigDecimal S_HB_MTD_RATE) {
        this.S_HB_MTD_RATE = S_HB_MTD_RATE;
    }

    public BigDecimal getS_NHB_TAR() {
        return this.S_NHB_TAR;
    }

    public void setS_NHB_TAR(BigDecimal S_NHB_TAR) {
        this.S_NHB_TAR = S_NHB_TAR;
    }

    public BigDecimal getS_NHB_MTD_INT_AMT() {
        return this.S_NHB_MTD_INT_AMT;
    }

    public void setS_NHB_MTD_INT_AMT(BigDecimal S_NHB_MTD_INT_AMT) {
        this.S_NHB_MTD_INT_AMT = S_NHB_MTD_INT_AMT;
    }

    public BigDecimal getS_NHB_MTD_OUT_AMT() {
        return this.S_NHB_MTD_OUT_AMT;
    }

    public void setS_NHB_MTD_OUT_AMT(BigDecimal S_NHB_MTD_OUT_AMT) {
        this.S_NHB_MTD_OUT_AMT = S_NHB_MTD_OUT_AMT;
    }

    public BigDecimal getS_NHB_MTD_RATE() {
        return this.S_NHB_MTD_RATE;
    }

    public void setS_NHB_MTD_RATE(BigDecimal S_NHB_MTD_RATE) {
        this.S_NHB_MTD_RATE = S_NHB_MTD_RATE;
    }

    public BigDecimal getS_TAR() {
        return this.S_TAR;
    }

    public void setS_TAR(BigDecimal S_TAR) {
        this.S_TAR = S_TAR;
    }

    public BigDecimal getS_MTD_IA() {
        return this.S_MTD_IA;
    }

    public void setS_MTD_IA(BigDecimal S_MTD_IA) {
        this.S_MTD_IA = S_MTD_IA;
    }

    public BigDecimal getS_MTD_OA() {
        return this.S_MTD_OA;
    }

    public void setS_MTD_OA(BigDecimal S_MTD_OA) {
        this.S_MTD_OA = S_MTD_OA;
    }

    public BigDecimal getS_MTD_RATE() {
        return this.S_MTD_RATE;
    }

    public void setS_MTD_RATE(BigDecimal S_MTD_RATE) {
        this.S_MTD_RATE = S_MTD_RATE;
    }

    public BigDecimal getC_NHB_TAR() {
        return this.C_NHB_TAR;
    }

    public void setC_NHB_TAR(BigDecimal C_NHB_TAR) {
        this.C_NHB_TAR = C_NHB_TAR;
    }

    public BigDecimal getC_NHB_MTD_IN_AMT() {
        return this.C_NHB_MTD_IN_AMT;
    }

    public void setC_NHB_MTD_IN_AMT(BigDecimal C_NHB_MTD_IN_AMT) {
        this.C_NHB_MTD_IN_AMT = C_NHB_MTD_IN_AMT;
    }

    public BigDecimal getC_NHB_MTD_OUT_AMT() {
        return this.C_NHB_MTD_OUT_AMT;
    }

    public void setC_NHB_MTD_OUT_AMT(BigDecimal C_NHB_MTD_OUT_AMT) {
        this.C_NHB_MTD_OUT_AMT = C_NHB_MTD_OUT_AMT;
    }

    public BigDecimal getC_NHB_MTD_RATE() {
        return this.C_NHB_MTD_RATE;
    }

    public void setC_NHB_MTD_RATE(BigDecimal C_NHB_MTD_RATE) {
        this.C_NHB_MTD_RATE = C_NHB_MTD_RATE;
    }

    public BigDecimal getMRTG_TAR() {
        return this.MRTG_TAR;
    }

    public void setMRTG_TAR(BigDecimal MRTG_TAR) {
        this.MRTG_TAR = MRTG_TAR;
    }

    public BigDecimal getMRTG_MTD_IN_AMT() {
        return this.MRTG_MTD_IN_AMT;
    }

    public void setMRTG_MTD_IN_AMT(BigDecimal MRTG_MTD_IN_AMT) {
        this.MRTG_MTD_IN_AMT = MRTG_MTD_IN_AMT;
    }

    public BigDecimal getMRTG_MTD_OUT_AMT() {
        return this.MRTG_MTD_OUT_AMT;
    }

    public void setMRTG_MTD_OUT_AMT(BigDecimal MRTG_MTD_OUT_AMT) {
        this.MRTG_MTD_OUT_AMT = MRTG_MTD_OUT_AMT;
    }

    public BigDecimal getMRTG_MTD_RATE() {
        return this.MRTG_MTD_RATE;
    }

    public void setMRTG_MTD_RATE(BigDecimal MRTG_MTD_RATE) {
        this.MRTG_MTD_RATE = MRTG_MTD_RATE;
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
        if ( !(other instanceof TBPMS_MRTG_PS_MTDVO) ) return false;
        TBPMS_MRTG_PS_MTDVO castOther = (TBPMS_MRTG_PS_MTDVO) other;
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
