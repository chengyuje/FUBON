package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBCRM_TRS_DELCAM_ULIST_BRHVO extends VOBase {

    /** identifier field */
    private BigDecimal SEQ;

    /** nullable persistent field */
    private BigDecimal LIST_SEQ;

    /** nullable persistent field */
    private String BRH_NBR;

    /** nullable persistent field */
    private String BRH_NAME;

    /** nullable persistent field */
    private String BRH_ADDR;

    /** nullable persistent field */
    private String BRH_TEL;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBCRM_TRS_DELCAM_ULIST_BRH";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBCRM_TRS_DELCAM_ULIST_BRHVO(BigDecimal SEQ, BigDecimal LIST_SEQ, String BRH_NBR, String BRH_NAME, String BRH_ADDR, String BRH_TEL, String creator, Timestamp createtime, String modifier, Timestamp lastupdate, Long version) {
        this.SEQ = SEQ;
        this.LIST_SEQ = LIST_SEQ;
        this.BRH_NBR = BRH_NBR;
        this.BRH_NAME = BRH_NAME;
        this.BRH_ADDR = BRH_ADDR;
        this.BRH_TEL = BRH_TEL;
        this.creator = creator;
        this.createtime = createtime;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBCRM_TRS_DELCAM_ULIST_BRHVO() {
    }

    /** minimal constructor */
    public TBCRM_TRS_DELCAM_ULIST_BRHVO(BigDecimal SEQ) {
        this.SEQ = SEQ;
    }

    public BigDecimal getSEQ() {
        return this.SEQ;
    }

    public void setSEQ(BigDecimal SEQ) {
        this.SEQ = SEQ;
    }

    public BigDecimal getLIST_SEQ() {
        return this.LIST_SEQ;
    }

    public void setLIST_SEQ(BigDecimal LIST_SEQ) {
        this.LIST_SEQ = LIST_SEQ;
    }

    public String getBRH_NBR() {
        return this.BRH_NBR;
    }

    public void setBRH_NBR(String BRH_NBR) {
        this.BRH_NBR = BRH_NBR;
    }

    public String getBRH_NAME() {
        return this.BRH_NAME;
    }

    public void setBRH_NAME(String BRH_NAME) {
        this.BRH_NAME = BRH_NAME;
    }

    public String getBRH_ADDR() {
        return this.BRH_ADDR;
    }

    public void setBRH_ADDR(String BRH_ADDR) {
        this.BRH_ADDR = BRH_ADDR;
    }

    public String getBRH_TEL() {
        return this.BRH_TEL;
    }

    public void setBRH_TEL(String BRH_TEL) {
        this.BRH_TEL = BRH_TEL;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("SEQ", getSEQ())
            .toString();
    }

}
