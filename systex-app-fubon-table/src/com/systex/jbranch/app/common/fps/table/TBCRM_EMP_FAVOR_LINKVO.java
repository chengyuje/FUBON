package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBCRM_EMP_FAVOR_LINKVO extends VOBase {

    /** identifier field */
    private BigDecimal SEQ;

    /** nullable persistent field */
    private BigDecimal ORDER_NO;

    /** nullable persistent field */
    private String EMP_ID;

    /** nullable persistent field */
    private String LINK_NAME;

    /** nullable persistent field */
    private String LINK_URL;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBCRM_EMP_FAVOR_LINK";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBCRM_EMP_FAVOR_LINKVO(BigDecimal SEQ, BigDecimal ORDER_NO, String EMP_ID, String LINK_NAME, String LINK_URL, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.SEQ = SEQ;
        this.ORDER_NO = ORDER_NO;
        this.EMP_ID = EMP_ID;
        this.LINK_NAME = LINK_NAME;
        this.LINK_URL = LINK_URL;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBCRM_EMP_FAVOR_LINKVO() {
    }

    /** minimal constructor */
    public TBCRM_EMP_FAVOR_LINKVO(BigDecimal SEQ) {
        this.SEQ = SEQ;
    }

    public BigDecimal getSEQ() {
        return this.SEQ;
    }

    public void setSEQ(BigDecimal SEQ) {
        this.SEQ = SEQ;
    }

    public BigDecimal getORDER_NO() {
        return this.ORDER_NO;
    }

    public void setORDER_NO(BigDecimal ORDER_NO) {
        this.ORDER_NO = ORDER_NO;
    }

    public String getEMP_ID() {
        return this.EMP_ID;
    }

    public void setEMP_ID(String EMP_ID) {
        this.EMP_ID = EMP_ID;
    }

    public String getLINK_NAME() {
        return this.LINK_NAME;
    }

    public void setLINK_NAME(String LINK_NAME) {
        this.LINK_NAME = LINK_NAME;
    }

    public String getLINK_URL() {
        return this.LINK_URL;
    }

    public void setLINK_URL(String LINK_URL) {
        this.LINK_URL = LINK_URL;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("SEQ", getSEQ())
            .toString();
    }

}
