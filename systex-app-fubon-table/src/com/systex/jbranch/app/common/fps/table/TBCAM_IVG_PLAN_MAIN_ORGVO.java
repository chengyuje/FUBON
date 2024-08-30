package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBCAM_IVG_PLAN_MAIN_ORGVO extends VOBase {

    /** identifier field */
    private BigDecimal IVG_ORG_SEQ;

    /** nullable persistent field */
    private BigDecimal IVG_PLAN_SEQ;

    /** nullable persistent field */
    private String ALL_BRANCH;

    /** nullable persistent field */
    private String REGION_CENTER_ID;

    /** nullable persistent field */
    private String BRANCH_AREA_ID;

    /** nullable persistent field */
    private String BRANCH_NBR;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBCAM_IVG_PLAN_MAIN_ORG";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBCAM_IVG_PLAN_MAIN_ORGVO(BigDecimal IVG_ORG_SEQ, BigDecimal IVG_PLAN_SEQ, String ALL_BRANCH, String REGION_CENTER_ID, String BRANCH_AREA_ID, String BRANCH_NBR, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.IVG_ORG_SEQ = IVG_ORG_SEQ;
        this.IVG_PLAN_SEQ = IVG_PLAN_SEQ;
        this.ALL_BRANCH = ALL_BRANCH;
        this.REGION_CENTER_ID = REGION_CENTER_ID;
        this.BRANCH_AREA_ID = BRANCH_AREA_ID;
        this.BRANCH_NBR = BRANCH_NBR;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBCAM_IVG_PLAN_MAIN_ORGVO() {
    }

    /** minimal constructor */
    public TBCAM_IVG_PLAN_MAIN_ORGVO(BigDecimal IVG_ORG_SEQ) {
        this.IVG_ORG_SEQ = IVG_ORG_SEQ;
    }

    public BigDecimal getIVG_ORG_SEQ() {
        return this.IVG_ORG_SEQ;
    }

    public void setIVG_ORG_SEQ(BigDecimal IVG_ORG_SEQ) {
        this.IVG_ORG_SEQ = IVG_ORG_SEQ;
    }

    public BigDecimal getIVG_PLAN_SEQ() {
        return this.IVG_PLAN_SEQ;
    }

    public void setIVG_PLAN_SEQ(BigDecimal IVG_PLAN_SEQ) {
        this.IVG_PLAN_SEQ = IVG_PLAN_SEQ;
    }

    public String getALL_BRANCH() {
        return this.ALL_BRANCH;
    }

    public void setALL_BRANCH(String ALL_BRANCH) {
        this.ALL_BRANCH = ALL_BRANCH;
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

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("IVG_ORG_SEQ", getIVG_ORG_SEQ())
            .toString();
    }

}
