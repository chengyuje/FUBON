package com.systex.jbranch.app.common.fps.table;

import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBSQM_RSA_DTLVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBSQM_RSA_DTLPK comp_id;

    /** nullable persistent field */
    private String CAMPAIGN_NAME;

    /** nullable persistent field */
    private String LEAD_SOURCE_ID;

    /** nullable persistent field */
    private String LEAD_TYPE;

    /** persistent field */
    private Timestamp CONTACT_DATE;

    /** persistent field */
    private String VISITOR;

    /** nullable persistent field */
    private String VISIT_MEMO;

    /** nullable persistent field */
    private String MEET_RULE;

    /** nullable persistent field */
    private String MEET_RULE_CUST;

    /** nullable persistent field */
    private String AUDIT_REMARK;

    /** nullable persistent field */
    private String MEET_TM_RULE;
    
    /** nullable persistent field */
    private String CMU_TYPE;
    
    /** nullable persistent field */
    private String MEET_RULE_CUST1;
    
    /** nullable persistent field */
    private String MEET_RULE_CUST2;
    
    /** nullable persistent field */
    private String MEET_RULE_CUST3;
    
    /** nullable persistent field */
    private String MEET_RULE_CUST4;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBSQM_RSA_DTL";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBSQM_RSA_DTLVO(com.systex.jbranch.app.common.fps.table.TBSQM_RSA_DTLPK comp_id, String CAMPAIGN_NAME, String LEAD_SOURCE_ID, String LEAD_TYPE, Timestamp CONTACT_DATE, String VISITOR, String VISIT_MEMO, String MEET_RULE, String MEET_RULE_CUST,
    		String MEET_RULE_CUST1, String MEET_RULE_CUST2, String MEET_RULE_CUST3, String MEET_RULE_CUST4, String CMU_TYPE, String AUDIT_REMARK, String MEET_TM_RULE, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.comp_id = comp_id;
        this.CAMPAIGN_NAME = CAMPAIGN_NAME;
        this.LEAD_SOURCE_ID = LEAD_SOURCE_ID;
        this.LEAD_TYPE = LEAD_TYPE;
        this.CONTACT_DATE = CONTACT_DATE;
        this.VISITOR = VISITOR;
        this.VISIT_MEMO = VISIT_MEMO;
        this.MEET_RULE = MEET_RULE;
        this.MEET_RULE_CUST = MEET_RULE_CUST;
        this.MEET_RULE_CUST1 = MEET_RULE_CUST1;
        this.MEET_RULE_CUST2 = MEET_RULE_CUST2;
        this.MEET_RULE_CUST3 = MEET_RULE_CUST3;
        this.MEET_RULE_CUST4 = MEET_RULE_CUST4;
        this.CMU_TYPE = CMU_TYPE;
        this.AUDIT_REMARK = AUDIT_REMARK;
        this.MEET_TM_RULE = MEET_TM_RULE;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBSQM_RSA_DTLVO() {
    }

    /** minimal constructor */
    public TBSQM_RSA_DTLVO(com.systex.jbranch.app.common.fps.table.TBSQM_RSA_DTLPK comp_id, Timestamp CONTACT_DATE, String VISITOR) {
        this.comp_id = comp_id;
        this.CONTACT_DATE = CONTACT_DATE;
        this.VISITOR = VISITOR;
    }

    public com.systex.jbranch.app.common.fps.table.TBSQM_RSA_DTLPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBSQM_RSA_DTLPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getCAMPAIGN_NAME() {
        return this.CAMPAIGN_NAME;
    }

    public void setCAMPAIGN_NAME(String CAMPAIGN_NAME) {
        this.CAMPAIGN_NAME = CAMPAIGN_NAME;
    }

    public String getLEAD_SOURCE_ID() {
        return this.LEAD_SOURCE_ID;
    }

    public void setLEAD_SOURCE_ID(String LEAD_SOURCE_ID) {
        this.LEAD_SOURCE_ID = LEAD_SOURCE_ID;
    }

    public String getLEAD_TYPE() {
        return this.LEAD_TYPE;
    }

    public void setLEAD_TYPE(String LEAD_TYPE) {
        this.LEAD_TYPE = LEAD_TYPE;
    }

    public Timestamp getCONTACT_DATE() {
        return this.CONTACT_DATE;
    }

    public void setCONTACT_DATE(Timestamp CONTACT_DATE) {
        this.CONTACT_DATE = CONTACT_DATE;
    }

    public String getVISITOR() {
        return this.VISITOR;
    }

    public void setVISITOR(String VISITOR) {
        this.VISITOR = VISITOR;
    }

    public String getVISIT_MEMO() {
        return this.VISIT_MEMO;
    }

    public void setVISIT_MEMO(String VISIT_MEMO) {
        this.VISIT_MEMO = VISIT_MEMO;
    }

    public String getMEET_RULE() {
        return this.MEET_RULE;
    }

    public void setMEET_RULE(String MEET_RULE) {
        this.MEET_RULE = MEET_RULE;
    }

    public String getMEET_RULE_CUST() {
        return this.MEET_RULE_CUST;
    }

    public void setMEET_RULE_CUST(String MEET_RULE_CUST) {
        this.MEET_RULE_CUST = MEET_RULE_CUST;
    }
    
    public String getMEET_RULE_CUST1() {
        return this.MEET_RULE_CUST1;
    }

    public void setMEET_RULE_CUST1(String MEET_RULE_CUST1) {
        this.MEET_RULE_CUST1 = MEET_RULE_CUST1;
    }
    
    public String getMEET_RULE_CUST2() {
        return this.MEET_RULE_CUST2;
    }

    public void setMEET_RULE_CUST2(String MEET_RULE_CUST2) {
        this.MEET_RULE_CUST2 = MEET_RULE_CUST2;
    }
    
    public String getMEET_RULE_CUST3() {
        return this.MEET_RULE_CUST3;
    }

    public void setMEET_RULE_CUST3(String MEET_RULE_CUST3) {
        this.MEET_RULE_CUST3 = MEET_RULE_CUST3;
    }
    
    public String getMEET_RULE_CUST4() {
        return this.MEET_RULE_CUST4;
    }

    public void setMEET_RULE_CUST4(String MEET_RULE_CUST4) {
        this.MEET_RULE_CUST4 = MEET_RULE_CUST4;
    }
    
    public String getCMU_TYPE() {
    	return this.CMU_TYPE;
    }
    
    public void setCMU_TYPE(String CMU_TYPE) {
    	this.CMU_TYPE = CMU_TYPE;
    }

    public String getAUDIT_REMARK() {
        return this.AUDIT_REMARK;
    }

    public void setAUDIT_REMARK(String AUDIT_REMARK) {
        this.AUDIT_REMARK = AUDIT_REMARK;
    }

    public String getMEET_TM_RULE() {
        return this.MEET_TM_RULE;
    }

    public void setMEET_TM_RULE(String MEET_TM_RULE) {
        this.MEET_TM_RULE = MEET_TM_RULE;
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
        if ( !(other instanceof TBSQM_RSA_DTLVO) ) return false;
        TBSQM_RSA_DTLVO castOther = (TBSQM_RSA_DTLVO) other;
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
