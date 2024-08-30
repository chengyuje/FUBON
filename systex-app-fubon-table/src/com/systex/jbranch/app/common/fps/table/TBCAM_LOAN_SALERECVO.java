package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBCAM_LOAN_SALERECVO extends VOBase {

    /** identifier field */
    private String SEQ;

    /** nullable persistent field */
    private String REF_CON_ID;

    /** nullable persistent field */
    private String TERRITORY_ID;

    /** nullable persistent field */
    private Timestamp TXN_DATE;

    /** nullable persistent field */
    private String REF_SRC;

    /** nullable persistent field */
    private String REF_ORG_ID;

    /** nullable persistent field */
    private String CUST_ID;

    /** nullable persistent field */
    private String CUST_NAME;

    /** nullable persistent field */
    private String SALES_PERSON;

    /** nullable persistent field */
    private String SALES_NAME;

    /** nullable persistent field */
    private String REF_PROD;

    /** nullable persistent field */
    private String CASE_PROPERTY;

    /** nullable persistent field */
    private String LOAN_PRJ_ID;

    /** nullable persistent field */
    private Timestamp CONTACT_DTTM;

    /** nullable persistent field */
    private String CONT_RSLT;

    /** nullable persistent field */
    private String COMMENTS;

    /** nullable persistent field */
    private Timestamp ROW_LASTMANT_DTTM;

    /** nullable persistent field */
    private String ROW_LASTMANT_OPRID;

    /** nullable persistent field */
    private String CASEID;

    /** nullable persistent field */
    private String CANCEL_FLAG;

    /** nullable persistent field */
    private String SALES_ROLE;

    /** nullable persistent field */
    private String USERID;

    /** nullable persistent field */
    private String USERNAME;

    /** nullable persistent field */
    private String USERROLE;

    /** nullable persistent field */
    private String BUY_PROD_TYPE;

    /** nullable persistent field */
    private String MEMO;

    /** nullable persistent field */
    private String NON_GRANT_REASON;

    /** nullable persistent field */
    private BigDecimal REF_FEE;

    /** nullable persistent field */
    private String IF_APPROVAL_FLAG;

    /** nullable persistent field */
    private String BUY_PROD_TYPE_D;

    /** nullable persistent field */
    private String NEW_CUST_FLAG;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBCAM_LOAN_SALEREC";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBCAM_LOAN_SALERECVO(String SEQ, String REF_CON_ID, String TERRITORY_ID, Timestamp TXN_DATE, String REF_SRC, String REF_ORG_ID, String CUST_ID, String CUST_NAME, String SALES_PERSON, String SALES_NAME, String REF_PROD, String CASE_PROPERTY, String LOAN_PRJ_ID, Timestamp CONTACT_DTTM, String CONT_RSLT, String COMMENTS, Timestamp ROW_LASTMANT_DTTM, String ROW_LASTMANT_OPRID, String CASEID, String CANCEL_FLAG, String SALES_ROLE, String USERID, String USERNAME, String USERROLE, String BUY_PROD_TYPE, String MEMO, String NON_GRANT_REASON, BigDecimal REF_FEE, String IF_APPROVAL_FLAG, String BUY_PROD_TYPE_D, String NEW_CUST_FLAG, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.SEQ = SEQ;
        this.REF_CON_ID = REF_CON_ID;
        this.TERRITORY_ID = TERRITORY_ID;
        this.TXN_DATE = TXN_DATE;
        this.REF_SRC = REF_SRC;
        this.REF_ORG_ID = REF_ORG_ID;
        this.CUST_ID = CUST_ID;
        this.CUST_NAME = CUST_NAME;
        this.SALES_PERSON = SALES_PERSON;
        this.SALES_NAME = SALES_NAME;
        this.REF_PROD = REF_PROD;
        this.CASE_PROPERTY = CASE_PROPERTY;
        this.LOAN_PRJ_ID = LOAN_PRJ_ID;
        this.CONTACT_DTTM = CONTACT_DTTM;
        this.CONT_RSLT = CONT_RSLT;
        this.COMMENTS = COMMENTS;
        this.ROW_LASTMANT_DTTM = ROW_LASTMANT_DTTM;
        this.ROW_LASTMANT_OPRID = ROW_LASTMANT_OPRID;
        this.CASEID = CASEID;
        this.CANCEL_FLAG = CANCEL_FLAG;
        this.SALES_ROLE = SALES_ROLE;
        this.USERID = USERID;
        this.USERNAME = USERNAME;
        this.USERROLE = USERROLE;
        this.BUY_PROD_TYPE = BUY_PROD_TYPE;
        this.MEMO = MEMO;
        this.NON_GRANT_REASON = NON_GRANT_REASON;
        this.REF_FEE = REF_FEE;
        this.IF_APPROVAL_FLAG = IF_APPROVAL_FLAG;
        this.BUY_PROD_TYPE_D = BUY_PROD_TYPE_D;
        this.NEW_CUST_FLAG = NEW_CUST_FLAG;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBCAM_LOAN_SALERECVO() {
    }

    /** minimal constructor */
    public TBCAM_LOAN_SALERECVO(String SEQ) {
        this.SEQ = SEQ;
    }

    public String getSEQ() {
        return this.SEQ;
    }

    public void setSEQ(String SEQ) {
        this.SEQ = SEQ;
    }

    public String getREF_CON_ID() {
        return this.REF_CON_ID;
    }

    public void setREF_CON_ID(String REF_CON_ID) {
        this.REF_CON_ID = REF_CON_ID;
    }

    public String getTERRITORY_ID() {
        return this.TERRITORY_ID;
    }

    public void setTERRITORY_ID(String TERRITORY_ID) {
        this.TERRITORY_ID = TERRITORY_ID;
    }

    public Timestamp getTXN_DATE() {
        return this.TXN_DATE;
    }

    public void setTXN_DATE(Timestamp TXN_DATE) {
        this.TXN_DATE = TXN_DATE;
    }

    public String getREF_SRC() {
        return this.REF_SRC;
    }

    public void setREF_SRC(String REF_SRC) {
        this.REF_SRC = REF_SRC;
    }

    public String getREF_ORG_ID() {
        return this.REF_ORG_ID;
    }

    public void setREF_ORG_ID(String REF_ORG_ID) {
        this.REF_ORG_ID = REF_ORG_ID;
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

    public String getSALES_PERSON() {
        return this.SALES_PERSON;
    }

    public void setSALES_PERSON(String SALES_PERSON) {
        this.SALES_PERSON = SALES_PERSON;
    }

    public String getSALES_NAME() {
        return this.SALES_NAME;
    }

    public void setSALES_NAME(String SALES_NAME) {
        this.SALES_NAME = SALES_NAME;
    }

    public String getREF_PROD() {
        return this.REF_PROD;
    }

    public void setREF_PROD(String REF_PROD) {
        this.REF_PROD = REF_PROD;
    }

    public String getCASE_PROPERTY() {
        return this.CASE_PROPERTY;
    }

    public void setCASE_PROPERTY(String CASE_PROPERTY) {
        this.CASE_PROPERTY = CASE_PROPERTY;
    }

    public String getLOAN_PRJ_ID() {
        return this.LOAN_PRJ_ID;
    }

    public void setLOAN_PRJ_ID(String LOAN_PRJ_ID) {
        this.LOAN_PRJ_ID = LOAN_PRJ_ID;
    }

    public Timestamp getCONTACT_DTTM() {
        return this.CONTACT_DTTM;
    }

    public void setCONTACT_DTTM(Timestamp CONTACT_DTTM) {
        this.CONTACT_DTTM = CONTACT_DTTM;
    }

    public String getCONT_RSLT() {
        return this.CONT_RSLT;
    }

    public void setCONT_RSLT(String CONT_RSLT) {
        this.CONT_RSLT = CONT_RSLT;
    }

    public String getCOMMENTS() {
        return this.COMMENTS;
    }

    public void setCOMMENTS(String COMMENTS) {
        this.COMMENTS = COMMENTS;
    }

    public Timestamp getROW_LASTMANT_DTTM() {
        return this.ROW_LASTMANT_DTTM;
    }

    public void setROW_LASTMANT_DTTM(Timestamp ROW_LASTMANT_DTTM) {
        this.ROW_LASTMANT_DTTM = ROW_LASTMANT_DTTM;
    }

    public String getROW_LASTMANT_OPRID() {
        return this.ROW_LASTMANT_OPRID;
    }

    public void setROW_LASTMANT_OPRID(String ROW_LASTMANT_OPRID) {
        this.ROW_LASTMANT_OPRID = ROW_LASTMANT_OPRID;
    }

    public String getCASEID() {
        return this.CASEID;
    }

    public void setCASEID(String CASEID) {
        this.CASEID = CASEID;
    }

    public String getCANCEL_FLAG() {
        return this.CANCEL_FLAG;
    }

    public void setCANCEL_FLAG(String CANCEL_FLAG) {
        this.CANCEL_FLAG = CANCEL_FLAG;
    }

    public String getSALES_ROLE() {
        return this.SALES_ROLE;
    }

    public void setSALES_ROLE(String SALES_ROLE) {
        this.SALES_ROLE = SALES_ROLE;
    }

    public String getUSERID() {
        return this.USERID;
    }

    public void setUSERID(String USERID) {
        this.USERID = USERID;
    }

    public String getUSERNAME() {
        return this.USERNAME;
    }

    public void setUSERNAME(String USERNAME) {
        this.USERNAME = USERNAME;
    }

    public String getUSERROLE() {
        return this.USERROLE;
    }

    public void setUSERROLE(String USERROLE) {
        this.USERROLE = USERROLE;
    }

    public String getBUY_PROD_TYPE() {
        return this.BUY_PROD_TYPE;
    }

    public void setBUY_PROD_TYPE(String BUY_PROD_TYPE) {
        this.BUY_PROD_TYPE = BUY_PROD_TYPE;
    }

    public String getMEMO() {
        return this.MEMO;
    }

    public void setMEMO(String MEMO) {
        this.MEMO = MEMO;
    }

    public String getNON_GRANT_REASON() {
        return this.NON_GRANT_REASON;
    }

    public void setNON_GRANT_REASON(String NON_GRANT_REASON) {
        this.NON_GRANT_REASON = NON_GRANT_REASON;
    }

    public BigDecimal getREF_FEE() {
        return this.REF_FEE;
    }

    public void setREF_FEE(BigDecimal REF_FEE) {
        this.REF_FEE = REF_FEE;
    }

    public String getIF_APPROVAL_FLAG() {
        return this.IF_APPROVAL_FLAG;
    }

    public void setIF_APPROVAL_FLAG(String IF_APPROVAL_FLAG) {
        this.IF_APPROVAL_FLAG = IF_APPROVAL_FLAG;
    }

    public String getBUY_PROD_TYPE_D() {
        return this.BUY_PROD_TYPE_D;
    }

    public void setBUY_PROD_TYPE_D(String BUY_PROD_TYPE_D) {
        this.BUY_PROD_TYPE_D = BUY_PROD_TYPE_D;
    }

    public String getNEW_CUST_FLAG() {
        return this.NEW_CUST_FLAG;
    }

    public void setNEW_CUST_FLAG(String NEW_CUST_FLAG) {
        this.NEW_CUST_FLAG = NEW_CUST_FLAG;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("SEQ", getSEQ())
            .toString();
    }

}
