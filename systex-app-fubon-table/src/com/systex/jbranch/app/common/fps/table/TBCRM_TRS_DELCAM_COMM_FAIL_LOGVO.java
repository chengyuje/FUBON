package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBCRM_TRS_DELCAM_COMM_FAIL_LOGVO extends VOBase {

    /** identifier field */
    private BigDecimal SEQ;

    /** nullable persistent field */
    private String CUSNO;

    /** nullable persistent field */
    private String BANKNO;

    /** nullable persistent field */
    private String ADDRESS;

    /** nullable persistent field */
    private String CUSINDEX1;

    /** nullable persistent field */
    private String CUSINDEX2;

    /** nullable persistent field */
    private String CUSINDEX3;

    /** nullable persistent field */
    private String SNAP_DATE;

    /** nullable persistent field */
    private String CUST_ID;

    /** nullable persistent field */
    private String TXN_CODE;

    /** nullable persistent field */
    private String FAIL_TO_UP_REASON;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBCRM_TRS_DELCAM_COMM_FAIL_LOG";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBCRM_TRS_DELCAM_COMM_FAIL_LOGVO(BigDecimal SEQ, String CUSNO, String BANKNO, String ADDRESS, String CUSINDEX1, String CUSINDEX2, String CUSINDEX3, String SNAP_DATE, String CUST_ID, String TXN_CODE, String FAIL_TO_UP_REASON, String creator, Timestamp createtime, String modifier, Timestamp lastupdate, Long version) {
        this.SEQ = SEQ;
        this.CUSNO = CUSNO;
        this.BANKNO = BANKNO;
        this.ADDRESS = ADDRESS;
        this.CUSINDEX1 = CUSINDEX1;
        this.CUSINDEX2 = CUSINDEX2;
        this.CUSINDEX3 = CUSINDEX3;
        this.SNAP_DATE = SNAP_DATE;
        this.CUST_ID = CUST_ID;
        this.TXN_CODE = TXN_CODE;
        this.FAIL_TO_UP_REASON = FAIL_TO_UP_REASON;
        this.creator = creator;
        this.createtime = createtime;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBCRM_TRS_DELCAM_COMM_FAIL_LOGVO() {
    }

    /** minimal constructor */
    public TBCRM_TRS_DELCAM_COMM_FAIL_LOGVO(BigDecimal SEQ) {
        this.SEQ = SEQ;
    }

    public BigDecimal getSEQ() {
        return this.SEQ;
    }

    public void setSEQ(BigDecimal SEQ) {
        this.SEQ = SEQ;
    }

    public String getCUSNO() {
        return this.CUSNO;
    }

    public void setCUSNO(String CUSNO) {
        this.CUSNO = CUSNO;
    }

    public String getBANKNO() {
        return this.BANKNO;
    }

    public void setBANKNO(String BANKNO) {
        this.BANKNO = BANKNO;
    }

    public String getADDRESS() {
        return this.ADDRESS;
    }

    public void setADDRESS(String ADDRESS) {
        this.ADDRESS = ADDRESS;
    }

    public String getCUSINDEX1() {
        return this.CUSINDEX1;
    }

    public void setCUSINDEX1(String CUSINDEX1) {
        this.CUSINDEX1 = CUSINDEX1;
    }

    public String getCUSINDEX2() {
        return this.CUSINDEX2;
    }

    public void setCUSINDEX2(String CUSINDEX2) {
        this.CUSINDEX2 = CUSINDEX2;
    }

    public String getCUSINDEX3() {
        return this.CUSINDEX3;
    }

    public void setCUSINDEX3(String CUSINDEX3) {
        this.CUSINDEX3 = CUSINDEX3;
    }

    public String getSNAP_DATE() {
        return this.SNAP_DATE;
    }

    public void setSNAP_DATE(String SNAP_DATE) {
        this.SNAP_DATE = SNAP_DATE;
    }

    public String getCUST_ID() {
        return this.CUST_ID;
    }

    public void setCUST_ID(String CUST_ID) {
        this.CUST_ID = CUST_ID;
    }

    public String getTXN_CODE() {
        return this.TXN_CODE;
    }

    public void setTXN_CODE(String TXN_CODE) {
        this.TXN_CODE = TXN_CODE;
    }

    public String getFAIL_TO_UP_REASON() {
        return this.FAIL_TO_UP_REASON;
    }

    public void setFAIL_TO_UP_REASON(String FAIL_TO_UP_REASON) {
        this.FAIL_TO_UP_REASON = FAIL_TO_UP_REASON;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("SEQ", getSEQ())
            .toString();
    }

}
