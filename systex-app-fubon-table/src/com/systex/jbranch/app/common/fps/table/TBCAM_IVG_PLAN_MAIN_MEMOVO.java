package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBCAM_IVG_PLAN_MAIN_MEMOVO extends VOBase {

    /** identifier field */
    private BigDecimal IVG_MEMO_SEQ;

    /** nullable persistent field */
    private BigDecimal IVG_PLAN_SEQ;

    /** nullable persistent field */
    private String EMP_ID;

    /** nullable persistent field */
    private String EMP_NAME;

    /** nullable persistent field */
    private String MEMO_DESC;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBCAM_IVG_PLAN_MAIN_MEMO";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBCAM_IVG_PLAN_MAIN_MEMOVO(BigDecimal IVG_MEMO_SEQ, BigDecimal IVG_PLAN_SEQ, String EMP_ID, String EMP_NAME, String MEMO_DESC, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.IVG_MEMO_SEQ = IVG_MEMO_SEQ;
        this.IVG_PLAN_SEQ = IVG_PLAN_SEQ;
        this.EMP_ID = EMP_ID;
        this.EMP_NAME = EMP_NAME;
        this.MEMO_DESC = MEMO_DESC;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBCAM_IVG_PLAN_MAIN_MEMOVO() {
    }

    /** minimal constructor */
    public TBCAM_IVG_PLAN_MAIN_MEMOVO(BigDecimal IVG_MEMO_SEQ) {
        this.IVG_MEMO_SEQ = IVG_MEMO_SEQ;
    }

    public BigDecimal getIVG_MEMO_SEQ() {
        return this.IVG_MEMO_SEQ;
    }

    public void setIVG_MEMO_SEQ(BigDecimal IVG_MEMO_SEQ) {
        this.IVG_MEMO_SEQ = IVG_MEMO_SEQ;
    }

    public BigDecimal getIVG_PLAN_SEQ() {
        return this.IVG_PLAN_SEQ;
    }

    public void setIVG_PLAN_SEQ(BigDecimal IVG_PLAN_SEQ) {
        this.IVG_PLAN_SEQ = IVG_PLAN_SEQ;
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

    public String getMEMO_DESC() {
        return this.MEMO_DESC;
    }

    public void setMEMO_DESC(String MEMO_DESC) {
        this.MEMO_DESC = MEMO_DESC;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("IVG_MEMO_SEQ", getIVG_MEMO_SEQ())
            .toString();
    }

}
