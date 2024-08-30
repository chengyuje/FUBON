package com.systex.jbranch.platform.common.platformdao.table;

import java.sql.Blob;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBSYSREPORTMASTERVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.platform.common.platformdao.table.TBSYSREPORTMASTERPK comp_id;

    /** persistent field */
    private String TXNCODE;

    /** nullable persistent field */
    private String REPORTNAME;

    /** nullable persistent field */
    private String DEVICE;

    /** nullable persistent field */
    private String REQUEST_ID;

    /** nullable persistent field */
    private String TRMSEQ;

    /** nullable persistent field */
    private String REPRINT;

    /** nullable persistent field */
    private String FINISHED;

    /** nullable persistent field */
    private String PASSBOOK_YN;

    /** nullable persistent field */
    private Blob OTHERS;


public static final String TABLE_UID = "com.systex.jbranch.platform.common.platformdao.table.TBSYSREPORTMASTER";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBSYSREPORTMASTERVO(com.systex.jbranch.platform.common.platformdao.table.TBSYSREPORTMASTERPK comp_id, String TXNCODE, String REPORTNAME, String DEVICE, Timestamp createtime, String creator, Timestamp lastupdate, String modifier, String REQUEST_ID, String TRMSEQ, String REPRINT, String FINISHED, String PASSBOOK_YN, Blob OTHERS) {
        this.comp_id = comp_id;
        this.TXNCODE = TXNCODE;
        this.REPORTNAME = REPORTNAME;
        this.DEVICE = DEVICE;
        this.createtime = createtime;
        this.creator = creator;
        this.lastupdate = lastupdate;
        this.modifier = modifier;
        this.REQUEST_ID = REQUEST_ID;
        this.TRMSEQ = TRMSEQ;
        this.REPRINT = REPRINT;
        this.FINISHED = FINISHED;
        this.PASSBOOK_YN = PASSBOOK_YN;
        this.OTHERS = OTHERS;
    }

    /** default constructor */
    public TBSYSREPORTMASTERVO() {
    }

    /** minimal constructor */
    public TBSYSREPORTMASTERVO(com.systex.jbranch.platform.common.platformdao.table.TBSYSREPORTMASTERPK comp_id, String TXNCODE) {
        this.comp_id = comp_id;
        this.TXNCODE = TXNCODE;
    }

    public com.systex.jbranch.platform.common.platformdao.table.TBSYSREPORTMASTERPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.platform.common.platformdao.table.TBSYSREPORTMASTERPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getTXNCODE() {
        return this.TXNCODE;
    }

    public void setTXNCODE(String TXNCODE) {
        this.TXNCODE = TXNCODE;
    }

    public String getREPORTNAME() {
        return this.REPORTNAME;
    }

    public void setREPORTNAME(String REPORTNAME) {
        this.REPORTNAME = REPORTNAME;
    }

    public String getDEVICE() {
        return this.DEVICE;
    }

    public void setDEVICE(String DEVICE) {
        this.DEVICE = DEVICE;
    }

    public String getREQUEST_ID() {
        return this.REQUEST_ID;
    }

    public void setREQUEST_ID(String REQUEST_ID) {
        this.REQUEST_ID = REQUEST_ID;
    }

    public String getTRMSEQ() {
        return this.TRMSEQ;
    }

    public void setTRMSEQ(String TRMSEQ) {
        this.TRMSEQ = TRMSEQ;
    }

    public String getREPRINT() {
        return this.REPRINT;
    }

    public void setREPRINT(String REPRINT) {
        this.REPRINT = REPRINT;
    }

    public String getFINISHED() {
        return this.FINISHED;
    }

    public void setFINISHED(String FINISHED) {
        this.FINISHED = FINISHED;
    }

    public String getPASSBOOK_YN() {
        return this.PASSBOOK_YN;
    }

    public void setPASSBOOK_YN(String PASSBOOK_YN) {
        this.PASSBOOK_YN = PASSBOOK_YN;
    }

    public Blob getOTHERS() {
        return this.OTHERS;
    }

    public void setOTHERS(Blob OTHERS) {
        this.OTHERS = OTHERS;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getcomp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBSYSREPORTMASTERVO) ) return false;
        TBSYSREPORTMASTERVO castOther = (TBSYSREPORTMASTERVO) other;
        return new EqualsBuilder()
            .append(this.getcomp_id(), castOther.getcomp_id())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getcomp_id())
            .toHashCode();
    }

}
