package com.systex.jbranch.app.common.fps.table;

import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBCRM_CUST_SP_CONTACTVO extends VOBase {

    /** identifier field */
    private String SP_CONTACT_ID;

    /** nullable persistent field */
    private String CUST_ID;

    /** nullable persistent field */
    private String VALID_TYPE;

    /** nullable persistent field */
    private Timestamp VALID_BGN_DATE;

    /** nullable persistent field */
    private Timestamp VALID_END_DATE;

    /** nullable persistent field */
    private String CONTENT;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBCRM_CUST_SP_CONTACT";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBCRM_CUST_SP_CONTACTVO(String SP_CONTACT_ID, String CUST_ID, String VALID_TYPE, Timestamp VALID_BGN_DATE, Timestamp VALID_END_DATE, String CONTENT, String creator, Timestamp createtime, String modifier, Timestamp lastupdate, Long version) {
        this.SP_CONTACT_ID = SP_CONTACT_ID;
        this.CUST_ID = CUST_ID;
        this.VALID_TYPE = VALID_TYPE;
        this.VALID_BGN_DATE = VALID_BGN_DATE;
        this.VALID_END_DATE = VALID_END_DATE;
        this.CONTENT = CONTENT;
        this.creator = creator;
        this.createtime = createtime;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBCRM_CUST_SP_CONTACTVO() {
    }

    /** minimal constructor */
    public TBCRM_CUST_SP_CONTACTVO(String SP_CONTACT_ID) {
        this.SP_CONTACT_ID = SP_CONTACT_ID;
    }

    public String getSP_CONTACT_ID() {
        return this.SP_CONTACT_ID;
    }

    public void setSP_CONTACT_ID(String SP_CONTACT_ID) {
        this.SP_CONTACT_ID = SP_CONTACT_ID;
    }

    public String getCUST_ID() {
        return this.CUST_ID;
    }

    public void setCUST_ID(String CUST_ID) {
        this.CUST_ID = CUST_ID;
    }

    public String getVALID_TYPE() {
        return this.VALID_TYPE;
    }

    public void setVALID_TYPE(String VALID_TYPE) {
        this.VALID_TYPE = VALID_TYPE;
    }

    public Timestamp getVALID_BGN_DATE() {
        return this.VALID_BGN_DATE;
    }

    public void setVALID_BGN_DATE(Timestamp VALID_BGN_DATE) {
        this.VALID_BGN_DATE = VALID_BGN_DATE;
    }

    public Timestamp getVALID_END_DATE() {
        return this.VALID_END_DATE;
    }

    public void setVALID_END_DATE(Timestamp VALID_END_DATE) {
        this.VALID_END_DATE = VALID_END_DATE;
    }

    public String getCONTENT() {
        return this.CONTENT;
    }

    public void setCONTENT(String CONTENT) {
        this.CONTENT = CONTENT;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("SP_CONTACT_ID", getSP_CONTACT_ID())
            .toString();
    }

}
