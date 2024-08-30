package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBJSB_INS_PROD_COMPANYVO extends VOBase {

    /** identifier field */
    private BigDecimal SERIALNUM;

    /** persistent field */
    private String CNAME;

    /** nullable persistent field */
    private String ENAME;

    /** nullable persistent field */
    private String ZIPCODE;

    /** nullable persistent field */
    private String ADDRESS;

    /** nullable persistent field */
    private String CONTACTPERSONNAME;

    /** nullable persistent field */
    private String CONTACTPERSONPHONE;

    /** nullable persistent field */
    private String IDNUM;

    /** nullable persistent field */
    private String CONTACTPERSONEMAIL;

    /** nullable persistent field */
    private String ADMCONTACTPERSONNAME;

    /** nullable persistent field */
    private String ADMCONTACTPERSONPHONE;

    /** nullable persistent field */
    private String ADMCONTACTPERSONEMAIL;

    /** nullable persistent field */
    private String INDCONTACTPERSONNAME;

    /** nullable persistent field */
    private String INDCONTACTPERSONPHONE;

    /** nullable persistent field */
    private String INDCONTACTPERSONEMAIL;

    /** nullable persistent field */
    private Timestamp CONTRACTDATE;

    /** nullable persistent field */
    private BigDecimal CALCTYPE;

    /** nullable persistent field */
    private String HASH;

    /** nullable persistent field */
    private String INSCOTYPE;

    /** nullable persistent field */
    private String SHORTNAME;

    /** nullable persistent field */
    private String FILENAME;

    /** nullable persistent field */
    private Timestamp RENEWDATE;

    /** nullable persistent field */
    private Timestamp TERMDATE;

    /** nullable persistent field */
    private String COM_CODE;

    public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBJSB_INS_PROD_COMPANY";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBJSB_INS_PROD_COMPANYVO(BigDecimal SERIALNUM, String CNAME, String ENAME, String ZIPCODE, String ADDRESS, String CONTACTPERSONNAME, String CONTACTPERSONPHONE, String IDNUM, String CONTACTPERSONEMAIL, String ADMCONTACTPERSONNAME, String ADMCONTACTPERSONPHONE, String ADMCONTACTPERSONEMAIL, String INDCONTACTPERSONNAME, String INDCONTACTPERSONPHONE, String INDCONTACTPERSONEMAIL, Timestamp CONTRACTDATE, BigDecimal CALCTYPE, String HASH, String INSCOTYPE, String SHORTNAME, String FILENAME, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Timestamp RENEWDATE, Timestamp TERMDATE, Long version, String COM_CODE) {
        this.SERIALNUM = SERIALNUM;
        this.CNAME = CNAME;
        this.ENAME = ENAME;
        this.ZIPCODE = ZIPCODE;
        this.ADDRESS = ADDRESS;
        this.CONTACTPERSONNAME = CONTACTPERSONNAME;
        this.CONTACTPERSONPHONE = CONTACTPERSONPHONE;
        this.IDNUM = IDNUM;
        this.CONTACTPERSONEMAIL = CONTACTPERSONEMAIL;
        this.ADMCONTACTPERSONNAME = ADMCONTACTPERSONNAME;
        this.ADMCONTACTPERSONPHONE = ADMCONTACTPERSONPHONE;
        this.ADMCONTACTPERSONEMAIL = ADMCONTACTPERSONEMAIL;
        this.INDCONTACTPERSONNAME = INDCONTACTPERSONNAME;
        this.INDCONTACTPERSONPHONE = INDCONTACTPERSONPHONE;
        this.INDCONTACTPERSONEMAIL = INDCONTACTPERSONEMAIL;
        this.CONTRACTDATE = CONTRACTDATE;
        this.CALCTYPE = CALCTYPE;
        this.HASH = HASH;
        this.INSCOTYPE = INSCOTYPE;
        this.SHORTNAME = SHORTNAME;
        this.FILENAME = FILENAME;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.RENEWDATE = RENEWDATE;
        this.TERMDATE = TERMDATE;
        this.version = version;
        this.COM_CODE = COM_CODE;
    }

    /** default constructor */
    public TBJSB_INS_PROD_COMPANYVO() {
    }

    /** minimal constructor */
    public TBJSB_INS_PROD_COMPANYVO(BigDecimal SERIALNUM, String CNAME) {
        this.SERIALNUM = SERIALNUM;
        this.CNAME = CNAME;
    }

    public BigDecimal getSERIALNUM() {
        return this.SERIALNUM;
    }

    public void setSERIALNUM(BigDecimal SERIALNUM) {
        this.SERIALNUM = SERIALNUM;
    }

    public String getCNAME() {
        return this.CNAME;
    }

    public void setCNAME(String CNAME) {
        this.CNAME = CNAME;
    }

    public String getENAME() {
        return this.ENAME;
    }

    public void setENAME(String ENAME) {
        this.ENAME = ENAME;
    }

    public String getZIPCODE() {
        return this.ZIPCODE;
    }

    public void setZIPCODE(String ZIPCODE) {
        this.ZIPCODE = ZIPCODE;
    }

    public String getADDRESS() {
        return this.ADDRESS;
    }

    public void setADDRESS(String ADDRESS) {
        this.ADDRESS = ADDRESS;
    }

    public String getCONTACTPERSONNAME() {
        return this.CONTACTPERSONNAME;
    }

    public void setCONTACTPERSONNAME(String CONTACTPERSONNAME) {
        this.CONTACTPERSONNAME = CONTACTPERSONNAME;
    }

    public String getCONTACTPERSONPHONE() {
        return this.CONTACTPERSONPHONE;
    }

    public void setCONTACTPERSONPHONE(String CONTACTPERSONPHONE) {
        this.CONTACTPERSONPHONE = CONTACTPERSONPHONE;
    }

    public String getIDNUM() {
        return this.IDNUM;
    }

    public void setIDNUM(String IDNUM) {
        this.IDNUM = IDNUM;
    }

    public String getCONTACTPERSONEMAIL() {
        return this.CONTACTPERSONEMAIL;
    }

    public void setCONTACTPERSONEMAIL(String CONTACTPERSONEMAIL) {
        this.CONTACTPERSONEMAIL = CONTACTPERSONEMAIL;
    }

    public String getADMCONTACTPERSONNAME() {
        return this.ADMCONTACTPERSONNAME;
    }

    public void setADMCONTACTPERSONNAME(String ADMCONTACTPERSONNAME) {
        this.ADMCONTACTPERSONNAME = ADMCONTACTPERSONNAME;
    }

    public String getADMCONTACTPERSONPHONE() {
        return this.ADMCONTACTPERSONPHONE;
    }

    public void setADMCONTACTPERSONPHONE(String ADMCONTACTPERSONPHONE) {
        this.ADMCONTACTPERSONPHONE = ADMCONTACTPERSONPHONE;
    }

    public String getADMCONTACTPERSONEMAIL() {
        return this.ADMCONTACTPERSONEMAIL;
    }

    public void setADMCONTACTPERSONEMAIL(String ADMCONTACTPERSONEMAIL) {
        this.ADMCONTACTPERSONEMAIL = ADMCONTACTPERSONEMAIL;
    }

    public String getINDCONTACTPERSONNAME() {
        return this.INDCONTACTPERSONNAME;
    }

    public void setINDCONTACTPERSONNAME(String INDCONTACTPERSONNAME) {
        this.INDCONTACTPERSONNAME = INDCONTACTPERSONNAME;
    }

    public String getINDCONTACTPERSONPHONE() {
        return this.INDCONTACTPERSONPHONE;
    }

    public void setINDCONTACTPERSONPHONE(String INDCONTACTPERSONPHONE) {
        this.INDCONTACTPERSONPHONE = INDCONTACTPERSONPHONE;
    }

    public String getINDCONTACTPERSONEMAIL() {
        return this.INDCONTACTPERSONEMAIL;
    }

    public void setINDCONTACTPERSONEMAIL(String INDCONTACTPERSONEMAIL) {
        this.INDCONTACTPERSONEMAIL = INDCONTACTPERSONEMAIL;
    }

    public Timestamp getCONTRACTDATE() {
        return this.CONTRACTDATE;
    }

    public void setCONTRACTDATE(Timestamp CONTRACTDATE) {
        this.CONTRACTDATE = CONTRACTDATE;
    }

    public BigDecimal getCALCTYPE() {
        return this.CALCTYPE;
    }

    public void setCALCTYPE(BigDecimal CALCTYPE) {
        this.CALCTYPE = CALCTYPE;
    }

    public String getHASH() {
        return this.HASH;
    }

    public void setHASH(String HASH) {
        this.HASH = HASH;
    }

    public String getINSCOTYPE() {
        return this.INSCOTYPE;
    }

    public void setINSCOTYPE(String INSCOTYPE) {
        this.INSCOTYPE = INSCOTYPE;
    }

    public String getSHORTNAME() {
        return this.SHORTNAME;
    }

    public void setSHORTNAME(String SHORTNAME) {
        this.SHORTNAME = SHORTNAME;
    }

    public String getFILENAME() {
        return this.FILENAME;
    }

    public void setFILENAME(String FILENAME) {
        this.FILENAME = FILENAME;
    }

    public Timestamp getRENEWDATE() {
        return this.RENEWDATE;
    }

    public void setRENEWDATE(Timestamp RENEWDATE) {
        this.RENEWDATE = RENEWDATE;
    }

    public Timestamp getTERMDATE() {
        return this.TERMDATE;
    }

    public void setTERMDATE(Timestamp TERMDATE) {
        this.TERMDATE = TERMDATE;
    }

    public String getCOM_CODE() {
        return COM_CODE;
    }

    public void setCOM_CODE(String COM_CODE) {
        this.COM_CODE = COM_CODE;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("SERIALNUM", getSERIALNUM())
            .toString();
    }

}
