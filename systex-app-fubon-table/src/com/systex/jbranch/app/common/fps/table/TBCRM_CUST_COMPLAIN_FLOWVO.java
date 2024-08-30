package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBCRM_CUST_COMPLAIN_FLOWVO extends VOBase {

    /** identifier field */
    private BigDecimal SEQ;

    /** persistent field */
    private String COMPLAIN_LIST_ID;

    /** nullable persistent field */
    private String EMP_ID;

    /** nullable persistent field */
    private String NEXT_EMP_ID;

    /** nullable persistent field */
    private String STATUS;

    /** nullable persistent field */
    private String REASON;

    /** nullable persistent field */
    private Timestamp SUBMIT_DATE;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBCRM_CUST_COMPLAIN_FLOW";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBCRM_CUST_COMPLAIN_FLOWVO(BigDecimal SEQ, String COMPLAIN_LIST_ID, String EMP_ID, String NEXT_EMP_ID, String STATUS, String REASON, Timestamp SUBMIT_DATE, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.SEQ = SEQ;
        this.COMPLAIN_LIST_ID = COMPLAIN_LIST_ID;
        this.EMP_ID = EMP_ID;
        this.NEXT_EMP_ID = NEXT_EMP_ID;
        this.STATUS = STATUS;
        this.REASON = REASON;
        this.SUBMIT_DATE = SUBMIT_DATE;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBCRM_CUST_COMPLAIN_FLOWVO() {
    }

    /** minimal constructor */
    public TBCRM_CUST_COMPLAIN_FLOWVO(BigDecimal SEQ, String COMPLAIN_LIST_ID) {
        this.SEQ = SEQ;
        this.COMPLAIN_LIST_ID = COMPLAIN_LIST_ID;
    }

    public BigDecimal getSEQ() {
        return this.SEQ;
    }

    public void setSEQ(BigDecimal SEQ) {
        this.SEQ = SEQ;
    }

    public String getCOMPLAIN_LIST_ID() {
        return this.COMPLAIN_LIST_ID;
    }

    public void setCOMPLAIN_LIST_ID(String COMPLAIN_LIST_ID) {
        this.COMPLAIN_LIST_ID = COMPLAIN_LIST_ID;
    }

    public String getEMP_ID() {
        return this.EMP_ID;
    }

    public void setEMP_ID(String EMP_ID) {
        this.EMP_ID = EMP_ID;
    }

    public String getNEXT_EMP_ID() {
        return this.NEXT_EMP_ID;
    }

    public void setNEXT_EMP_ID(String NEXT_EMP_ID) {
        this.NEXT_EMP_ID = NEXT_EMP_ID;
    }

    public String getSTATUS() {
        return this.STATUS;
    }

    public void setSTATUS(String STATUS) {
        this.STATUS = STATUS;
    }

    public String getREASON() {
        return this.REASON;
    }

    public void setREASON(String REASON) {
        this.REASON = REASON;
    }

    public Timestamp getSUBMIT_DATE() {
        return this.SUBMIT_DATE;
    }

    public void setSUBMIT_DATE(Timestamp SUBMIT_DATE) {
        this.SUBMIT_DATE = SUBMIT_DATE;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("SEQ", getSEQ())
            .toString();
    }

}
