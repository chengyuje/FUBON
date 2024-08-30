package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBSQM_RSA_RC_BRVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBSQM_RSA_RC_BRPK comp_id;

    /** persistent field */
    private BigDecimal PREQ;

    /** persistent field */
    private String ROLE_ID;

    /** persistent field */
    private String ROLE_NAME;

    /** persistent field */
    private BigDecimal SC_CNT_TOT;

    /** persistent field */
    private BigDecimal SC_CNT_L;

    /** nullable persistent field */
    private BigDecimal SC_CNT;

    /** persistent field */
    private BigDecimal SP_CUST_TOT;

    /** nullable persistent field */
    private BigDecimal SP_CUST_CNT;
    

    public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBSQM_RSA_RC_BR";


	public String getTableuid () {
	    return TABLE_UID;
	}

    /** full constructor */
    public TBSQM_RSA_RC_BRVO(com.systex.jbranch.app.common.fps.table.TBSQM_RSA_RC_BRPK comp_id, BigDecimal PREQ, String ROLE_ID, String ROLE_NAME, BigDecimal SC_CNT_TOT, BigDecimal SC_CNT_L, BigDecimal SC_CNT, BigDecimal SP_CUST_TOT, BigDecimal SP_CUST_CNT, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.comp_id = comp_id;
        this.PREQ = PREQ;
        this.ROLE_ID = ROLE_ID;
        this.ROLE_NAME = ROLE_NAME;
        this.SC_CNT_TOT = SC_CNT_TOT;
        this.SC_CNT_L = SC_CNT_L;
        this.SC_CNT = SC_CNT;
        this.SP_CUST_TOT = SP_CUST_TOT;
        this.SP_CUST_CNT = SP_CUST_CNT;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBSQM_RSA_RC_BRVO() {
    }

    /** minimal constructor */
    public TBSQM_RSA_RC_BRVO(com.systex.jbranch.app.common.fps.table.TBSQM_RSA_RC_BRPK comp_id, BigDecimal PREQ, String ROLE_ID, String ROLE_NAME, BigDecimal SC_CNT_TOT, BigDecimal SC_CNT_L, BigDecimal SP_CUST_TOT) {
        this.comp_id = comp_id;
        this.PREQ = PREQ;
        this.ROLE_ID = ROLE_ID;
        this.ROLE_NAME = ROLE_NAME;
        this.SC_CNT_TOT = SC_CNT_TOT;
        this.SC_CNT_L = SC_CNT_L;
        this.SP_CUST_TOT = SP_CUST_TOT;
    }

    public com.systex.jbranch.app.common.fps.table.TBSQM_RSA_RC_BRPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBSQM_RSA_RC_BRPK comp_id) {
        this.comp_id = comp_id;
    }

    public BigDecimal getPREQ() {
        return this.PREQ;
    }

    public void setPREQ(BigDecimal PREQ) {
        this.PREQ = PREQ;
    }

    public String getROLE_ID() {
        return this.ROLE_ID;
    }

    public void setROLE_ID(String ROLE_ID) {
        this.ROLE_ID = ROLE_ID;
    }

    public String getROLE_NAME() {
        return this.ROLE_NAME;
    }

    public void setROLE_NAME(String ROLE_NAME) {
        this.ROLE_NAME = ROLE_NAME;
    }

    public BigDecimal getSC_CNT_TOT() {
        return this.SC_CNT_TOT;
    }

    public void setSC_CNT_TOT(BigDecimal SC_CNT_TOT) {
        this.SC_CNT_TOT = SC_CNT_TOT;
    }

    public BigDecimal getSC_CNT_L() {
        return this.SC_CNT_L;
    }

    public void setSC_CNT_L(BigDecimal SC_CNT_L) {
        this.SC_CNT_L = SC_CNT_L;
    }

    public BigDecimal getSC_CNT() {
        return this.SC_CNT;
    }

    public void setSC_CNT(BigDecimal SC_CNT) {
        this.SC_CNT = SC_CNT;
    }

    public BigDecimal getSP_CUST_TOT() {
        return this.SP_CUST_TOT;
    }

    public void setSP_CUST_TOT(BigDecimal SP_CUST_TOT) {
        this.SP_CUST_TOT = SP_CUST_TOT;
    }

    public BigDecimal getSP_CUST_CNT() {
        return this.SP_CUST_CNT;
    }

    public void setSP_CUST_CNT(BigDecimal SP_CUST_CNT) {
        this.SP_CUST_CNT = SP_CUST_CNT;
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
        if ( !(other instanceof TBSQM_RSA_RC_BRVO) ) return false;
        TBSQM_RSA_RC_BRVO castOther = (TBSQM_RSA_RC_BRVO) other;
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
