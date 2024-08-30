package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBCRM_CUST_DKYC_DATAVO extends VOBase {

    /** identifier field */
    private BigDecimal SEQ;

    /** nullable persistent field */
    private String CUST_ID;

    /** nullable persistent field */
    private String QSTN_ID;

    /** nullable persistent field */
    private String ANS_IDS;

    /** nullable persistent field */
    private String ANS_CONTENT;

    /** nullable persistent field */
    private String NOTE_CONTENT;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBCRM_CUST_DKYC_DATA";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBCRM_CUST_DKYC_DATAVO(BigDecimal SEQ, String CUST_ID, String QSTN_ID, String ANS_IDS, String ANS_CONTENT, String NOTE_CONTENT, String creator, Timestamp createtime, String modifier, Timestamp lastupdate, Long version) {
        this.SEQ = SEQ;
        this.CUST_ID = CUST_ID;
        this.QSTN_ID = QSTN_ID;
        this.ANS_IDS = ANS_IDS;
        this.ANS_CONTENT = ANS_CONTENT;
        this.NOTE_CONTENT = NOTE_CONTENT;
        this.creator = creator;
        this.createtime = createtime;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBCRM_CUST_DKYC_DATAVO() {
    }

    /** minimal constructor */
    public TBCRM_CUST_DKYC_DATAVO(BigDecimal SEQ) {
        this.SEQ = SEQ;
    }

    public BigDecimal getSEQ() {
        return this.SEQ;
    }

    public void setSEQ(BigDecimal SEQ) {
        this.SEQ = SEQ;
    }

    public String getCUST_ID() {
        return this.CUST_ID;
    }

    public void setCUST_ID(String CUST_ID) {
        this.CUST_ID = CUST_ID;
    }

    public String getQSTN_ID() {
        return this.QSTN_ID;
    }

    public void setQSTN_ID(String QSTN_ID) {
        this.QSTN_ID = QSTN_ID;
    }

    public String getANS_IDS() {
        return this.ANS_IDS;
    }

    public void setANS_IDS(String ANS_IDS) {
        this.ANS_IDS = ANS_IDS;
    }

    public String getANS_CONTENT() {
        return this.ANS_CONTENT;
    }

    public void setANS_CONTENT(String ANS_CONTENT) {
        this.ANS_CONTENT = ANS_CONTENT;
    }

    public String getNOTE_CONTENT() {
        return this.NOTE_CONTENT;
    }

    public void setNOTE_CONTENT(String NOTE_CONTENT) {
        this.NOTE_CONTENT = NOTE_CONTENT;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("SEQ", getSEQ())
            .toString();
    }

}
