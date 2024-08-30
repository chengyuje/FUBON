package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBSQM_RSA_SC_PARVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBSQM_RSA_SC_PARPK comp_id;

    /** nullable persistent field */
    private String CAMPAIGN_NAME;

    /** nullable persistent field */
    private String LEAD_SOURCE_ID;

    /** nullable persistent field */
    private String LEAD_TYPE;

    /** persistent field */
    private BigDecimal SC_CNT;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBSQM_RSA_SC_PAR";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBSQM_RSA_SC_PARVO(com.systex.jbranch.app.common.fps.table.TBSQM_RSA_SC_PARPK comp_id, String CAMPAIGN_NAME, String LEAD_SOURCE_ID, String LEAD_TYPE, BigDecimal SC_CNT, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.comp_id = comp_id;
        this.CAMPAIGN_NAME = CAMPAIGN_NAME;
        this.LEAD_SOURCE_ID = LEAD_SOURCE_ID;
        this.LEAD_TYPE = LEAD_TYPE;
        this.SC_CNT = SC_CNT;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBSQM_RSA_SC_PARVO() {
    }

    /** minimal constructor */
    public TBSQM_RSA_SC_PARVO(com.systex.jbranch.app.common.fps.table.TBSQM_RSA_SC_PARPK comp_id, BigDecimal SC_CNT) {
        this.comp_id = comp_id;
        this.SC_CNT = SC_CNT;
    }

    public com.systex.jbranch.app.common.fps.table.TBSQM_RSA_SC_PARPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBSQM_RSA_SC_PARPK comp_id) {
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

    public BigDecimal getSC_CNT() {
        return this.SC_CNT;
    }

    public void setSC_CNT(BigDecimal SC_CNT) {
        this.SC_CNT = SC_CNT;
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
        if ( !(other instanceof TBSQM_RSA_SC_PARVO) ) return false;
        TBSQM_RSA_SC_PARVO castOther = (TBSQM_RSA_SC_PARVO) other;
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
