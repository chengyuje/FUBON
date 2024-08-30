package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBCRM_EMP_SCROLLING_TEXTVO extends VOBase {

    /** identifier field */
    private BigDecimal SEQ;

    /** nullable persistent field */
    private String TITLE;

    /** nullable persistent field */
    private String CONTENT;

    /** nullable persistent field */
    private Timestamp START_DATE;

    /** nullable persistent field */
    private Timestamp END_DATE;

    /** nullable persistent field */
    private String MSG_LEVEL;

    /** nullable persistent field */
    private String DISPLAY;

    /** nullable persistent field */
    private String NEW_MSG_YN;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBCRM_EMP_SCROLLING_TEXT";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBCRM_EMP_SCROLLING_TEXTVO(BigDecimal SEQ, String TITLE, String CONTENT, Timestamp START_DATE, Timestamp END_DATE, String MSG_LEVEL, String DISPLAY, String creator, String NEW_MSG_YN, Timestamp createtime, String modifier, Timestamp lastupdate, Long version) {
        this.SEQ = SEQ;
        this.TITLE = TITLE;
        this.CONTENT = CONTENT;
        this.START_DATE = START_DATE;
        this.END_DATE = END_DATE;
        this.MSG_LEVEL = MSG_LEVEL;
        this.DISPLAY = DISPLAY;
        this.creator = creator;
        this.NEW_MSG_YN = NEW_MSG_YN;
        this.createtime = createtime;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBCRM_EMP_SCROLLING_TEXTVO() {
    }

    /** minimal constructor */
    public TBCRM_EMP_SCROLLING_TEXTVO(BigDecimal SEQ) {
        this.SEQ = SEQ;
    }

    public BigDecimal getSEQ() {
        return this.SEQ;
    }

    public void setSEQ(BigDecimal SEQ) {
        this.SEQ = SEQ;
    }

    public String getTITLE() {
        return this.TITLE;
    }

    public void setTITLE(String TITLE) {
        this.TITLE = TITLE;
    }

    public String getCONTENT() {
        return this.CONTENT;
    }

    public void setCONTENT(String CONTENT) {
        this.CONTENT = CONTENT;
    }

    public Timestamp getSTART_DATE() {
        return this.START_DATE;
    }

    public void setSTART_DATE(Timestamp START_DATE) {
        this.START_DATE = START_DATE;
    }

    public Timestamp getEND_DATE() {
        return this.END_DATE;
    }

    public void setEND_DATE(Timestamp END_DATE) {
        this.END_DATE = END_DATE;
    }

    public String getMSG_LEVEL() {
        return this.MSG_LEVEL;
    }

    public void setMSG_LEVEL(String MSG_LEVEL) {
        this.MSG_LEVEL = MSG_LEVEL;
    }

    public String getDISPLAY() {
        return this.DISPLAY;
    }

    public void setDISPLAY(String DISPLAY) {
        this.DISPLAY = DISPLAY;
    }

    public String getNEW_MSG_YN() {
        return this.NEW_MSG_YN;
    }

    public void setNEW_MSG_YN(String NEW_MSG_YN) {
        this.NEW_MSG_YN = NEW_MSG_YN;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("SEQ", getSEQ())
            .toString();
    }

}
