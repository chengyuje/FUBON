package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPRD_CUSTOMIZED_REVIEWVO extends VOBase {

    /** identifier field */
    private BigDecimal SEQ;

    /** nullable persistent field */
    private String PTYPE;

    /** nullable persistent field */
    private String PRD_ID;

    /** nullable persistent field */
    private String REGION_CENTER_ID;

    /** nullable persistent field */
    private String BRANCH_AREA_ID;

    /** nullable persistent field */
    private String BRANCH_NBR;

    /** nullable persistent field */
    private String CUST_ID;

    /** nullable persistent field */
    private String ACT_TYPE;

    /** nullable persistent field */
    private String REVIEW_STATUS;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBPRD_CUSTOMIZED_REVIEW";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBPRD_CUSTOMIZED_REVIEWVO(BigDecimal SEQ, String PTYPE, String PRD_ID, String REGION_CENTER_ID, String BRANCH_AREA_ID, String BRANCH_NBR, String CUST_ID, String ACT_TYPE, String REVIEW_STATUS, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.SEQ = SEQ;
        this.PTYPE = PTYPE;
        this.PRD_ID = PRD_ID;
        this.REGION_CENTER_ID = REGION_CENTER_ID;
        this.BRANCH_AREA_ID = BRANCH_AREA_ID;
        this.BRANCH_NBR = BRANCH_NBR;
        this.CUST_ID = CUST_ID;
        this.ACT_TYPE = ACT_TYPE;
        this.REVIEW_STATUS = REVIEW_STATUS;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBPRD_CUSTOMIZED_REVIEWVO() {
    }

    /** minimal constructor */
    public TBPRD_CUSTOMIZED_REVIEWVO(BigDecimal SEQ) {
        this.SEQ = SEQ;
    }

    public BigDecimal getSEQ() {
        return this.SEQ;
    }

    public void setSEQ(BigDecimal SEQ) {
        this.SEQ = SEQ;
    }

    public String getPTYPE() {
        return this.PTYPE;
    }

    public void setPTYPE(String PTYPE) {
        this.PTYPE = PTYPE;
    }

    public String getPRD_ID() {
        return this.PRD_ID;
    }

    public void setPRD_ID(String PRD_ID) {
        this.PRD_ID = PRD_ID;
    }

    public String getREGION_CENTER_ID() {
        return this.REGION_CENTER_ID;
    }

    public void setREGION_CENTER_ID(String REGION_CENTER_ID) {
        this.REGION_CENTER_ID = REGION_CENTER_ID;
    }

    public String getBRANCH_AREA_ID() {
        return this.BRANCH_AREA_ID;
    }

    public void setBRANCH_AREA_ID(String BRANCH_AREA_ID) {
        this.BRANCH_AREA_ID = BRANCH_AREA_ID;
    }

    public String getBRANCH_NBR() {
        return this.BRANCH_NBR;
    }

    public void setBRANCH_NBR(String BRANCH_NBR) {
        this.BRANCH_NBR = BRANCH_NBR;
    }

    public String getCUST_ID() {
        return this.CUST_ID;
    }

    public void setCUST_ID(String CUST_ID) {
        this.CUST_ID = CUST_ID;
    }

    public String getACT_TYPE() {
        return this.ACT_TYPE;
    }

    public void setACT_TYPE(String ACT_TYPE) {
        this.ACT_TYPE = ACT_TYPE;
    }

    public String getREVIEW_STATUS() {
        return this.REVIEW_STATUS;
    }

    public void setREVIEW_STATUS(String REVIEW_STATUS) {
        this.REVIEW_STATUS = REVIEW_STATUS;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("SEQ", getSEQ())
            .toString();
    }

}
