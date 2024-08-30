package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPMS_DAILY_PUBCOM_CHECKVO extends VOBase {

    /** identifier field */
    private BigDecimal SEQ;

    /** nullable persistent field */
    private Timestamp DATA_DATE;

    /** nullable persistent field */
    private String REGION_CENTER_ID;

    /** nullable persistent field */
    private String REGION_CENTER_NAME;

    /** nullable persistent field */
    private String OP_AREA_ID;

    /** nullable persistent field */
    private String OP_AREA_NAME;

    /** nullable persistent field */
    private String BRANCH_NBR;

    /** nullable persistent field */
    private String BRANCH_NAME;

    /** nullable persistent field */
    private String CUST_ID;

    /** nullable persistent field */
    private String CUST_NAME;

    /** nullable persistent field */
    private String AO_CODE;

    /** nullable persistent field */
    private String SPECIFIC_FLAG;

    /** nullable persistent field */
    private Timestamp TRADE_DATE;

    /** nullable persistent field */
    private String TRADE_ITEM;

    /** nullable persistent field */
    private String STAFF_THERE_FLAG;

    /** nullable persistent field */
    private String MEETING;

    /** nullable persistent field */
    private BigDecimal MEETING_HOUR;

    /** nullable persistent field */
    private BigDecimal MEETING_MIN;

    /** nullable persistent field */
    private String OBO_CUST_FLAG;

    /** nullable persistent field */
    private String AVOID_FLAG;

    /** nullable persistent field */
    private String NEARBY_CUST_FLAG;

    /** nullable persistent field */
    private String EXPLANATION;

    /** nullable persistent field */
    private String VIOLATION_FLAG;

    /** nullable persistent field */
    private String JOB_TITLE;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBPMS_DAILY_PUBCOM_CHECK";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBPMS_DAILY_PUBCOM_CHECKVO(BigDecimal SEQ, Timestamp DATA_DATE, String REGION_CENTER_ID, String REGION_CENTER_NAME, String OP_AREA_ID, String OP_AREA_NAME, String BRANCH_NBR, String BRANCH_NAME, String CUST_ID, String CUST_NAME, String AO_CODE, String SPECIFIC_FLAG, Timestamp TRADE_DATE, String TRADE_ITEM, String STAFF_THERE_FLAG, String MEETING, BigDecimal MEETING_HOUR, BigDecimal MEETING_MIN, String OBO_CUST_FLAG, String AVOID_FLAG, String NEARBY_CUST_FLAG, String EXPLANATION, String VIOLATION_FLAG, String JOB_TITLE, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.SEQ = SEQ;
        this.DATA_DATE = DATA_DATE;
        this.REGION_CENTER_ID = REGION_CENTER_ID;
        this.REGION_CENTER_NAME = REGION_CENTER_NAME;
        this.OP_AREA_ID = OP_AREA_ID;
        this.OP_AREA_NAME = OP_AREA_NAME;
        this.BRANCH_NBR = BRANCH_NBR;
        this.BRANCH_NAME = BRANCH_NAME;
        this.CUST_ID = CUST_ID;
        this.CUST_NAME = CUST_NAME;
        this.AO_CODE = AO_CODE;
        this.SPECIFIC_FLAG = SPECIFIC_FLAG;
        this.TRADE_DATE = TRADE_DATE;
        this.TRADE_ITEM = TRADE_ITEM;
        this.STAFF_THERE_FLAG = STAFF_THERE_FLAG;
        this.MEETING = MEETING;
        this.MEETING_HOUR = MEETING_HOUR;
        this.MEETING_MIN = MEETING_MIN;
        this.OBO_CUST_FLAG = OBO_CUST_FLAG;
        this.AVOID_FLAG = AVOID_FLAG;
        this.NEARBY_CUST_FLAG = NEARBY_CUST_FLAG;
        this.EXPLANATION = EXPLANATION;
        this.VIOLATION_FLAG = VIOLATION_FLAG;
        this.JOB_TITLE = JOB_TITLE;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBPMS_DAILY_PUBCOM_CHECKVO() {
    }

    /** minimal constructor */
    public TBPMS_DAILY_PUBCOM_CHECKVO(BigDecimal SEQ) {
        this.SEQ = SEQ;
    }

    public BigDecimal getSEQ() {
        return this.SEQ;
    }

    public void setSEQ(BigDecimal SEQ) {
        this.SEQ = SEQ;
    }

    public Timestamp getDATA_DATE() {
        return this.DATA_DATE;
    }

    public void setDATA_DATE(Timestamp DATA_DATE) {
        this.DATA_DATE = DATA_DATE;
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

    public String getOP_AREA_ID() {
        return this.OP_AREA_ID;
    }

    public void setOP_AREA_ID(String OP_AREA_ID) {
        this.OP_AREA_ID = OP_AREA_ID;
    }

    public String getOP_AREA_NAME() {
        return this.OP_AREA_NAME;
    }

    public void setOP_AREA_NAME(String OP_AREA_NAME) {
        this.OP_AREA_NAME = OP_AREA_NAME;
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

    public String getAO_CODE() {
        return this.AO_CODE;
    }

    public void setAO_CODE(String AO_CODE) {
        this.AO_CODE = AO_CODE;
    }

    public String getSPECIFIC_FLAG() {
        return this.SPECIFIC_FLAG;
    }

    public void setSPECIFIC_FLAG(String SPECIFIC_FLAG) {
        this.SPECIFIC_FLAG = SPECIFIC_FLAG;
    }

    public Timestamp getTRADE_DATE() {
        return this.TRADE_DATE;
    }

    public void setTRADE_DATE(Timestamp TRADE_DATE) {
        this.TRADE_DATE = TRADE_DATE;
    }

    public String getTRADE_ITEM() {
        return this.TRADE_ITEM;
    }

    public void setTRADE_ITEM(String TRADE_ITEM) {
        this.TRADE_ITEM = TRADE_ITEM;
    }

    public String getSTAFF_THERE_FLAG() {
        return this.STAFF_THERE_FLAG;
    }

    public void setSTAFF_THERE_FLAG(String STAFF_THERE_FLAG) {
        this.STAFF_THERE_FLAG = STAFF_THERE_FLAG;
    }

    public String getMEETING() {
        return this.MEETING;
    }

    public void setMEETING(String MEETING) {
        this.MEETING = MEETING;
    }

    public BigDecimal getMEETING_HOUR() {
        return this.MEETING_HOUR;
    }

    public void setMEETING_HOUR(BigDecimal MEETING_HOUR) {
        this.MEETING_HOUR = MEETING_HOUR;
    }

    public BigDecimal getMEETING_MIN() {
        return this.MEETING_MIN;
    }

    public void setMEETING_MIN(BigDecimal MEETING_MIN) {
        this.MEETING_MIN = MEETING_MIN;
    }

    public String getOBO_CUST_FLAG() {
        return this.OBO_CUST_FLAG;
    }

    public void setOBO_CUST_FLAG(String OBO_CUST_FLAG) {
        this.OBO_CUST_FLAG = OBO_CUST_FLAG;
    }

    public String getAVOID_FLAG() {
        return this.AVOID_FLAG;
    }

    public void setAVOID_FLAG(String AVOID_FLAG) {
        this.AVOID_FLAG = AVOID_FLAG;
    }

    public String getNEARBY_CUST_FLAG() {
        return this.NEARBY_CUST_FLAG;
    }

    public void setNEARBY_CUST_FLAG(String NEARBY_CUST_FLAG) {
        this.NEARBY_CUST_FLAG = NEARBY_CUST_FLAG;
    }

    public String getEXPLANATION() {
        return this.EXPLANATION;
    }

    public void setEXPLANATION(String EXPLANATION) {
        this.EXPLANATION = EXPLANATION;
    }

    public String getVIOLATION_FLAG() {
        return this.VIOLATION_FLAG;
    }

    public void setVIOLATION_FLAG(String VIOLATION_FLAG) {
        this.VIOLATION_FLAG = VIOLATION_FLAG;
    }

    public String getJOB_TITLE() {
        return this.JOB_TITLE;
    }

    public void setJOB_TITLE(String JOB_TITLE) {
        this.JOB_TITLE = JOB_TITLE;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("SEQ", getSEQ())
            .toString();
    }

}
