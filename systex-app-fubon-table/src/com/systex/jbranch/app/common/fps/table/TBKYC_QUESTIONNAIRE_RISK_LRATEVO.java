package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;


/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBKYC_QUESTIONNAIRE_RISK_LRATEVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBKYC_QUESTIONNAIRE_RISK_LRATEPK comp_id;

    /** nullable persistent field */
    private String RL_NAME;
    
    /** nullable persistent field */
    private BigDecimal RL_UP_RATE;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBKYC_QUESTIONNAIRE_RISK_LRATE";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBKYC_QUESTIONNAIRE_RISK_LRATEVO(com.systex.jbranch.app.common.fps.table.TBKYC_QUESTIONNAIRE_RISK_LRATEPK comp_id, String RL_NAME, BigDecimal RL_UP_RATE, Timestamp createtime, String creator, Timestamp lastupdate, String modifier, Long version) {
        this.comp_id = comp_id;
        this.RL_NAME = RL_NAME;
        this.RL_UP_RATE = RL_UP_RATE;
        this.createtime = createtime;
        this.creator = creator;
        this.lastupdate = lastupdate;
        this.modifier = modifier;
        this.version = version;
    }

    /** default constructor */
    public TBKYC_QUESTIONNAIRE_RISK_LRATEVO() {
    }

    /** minimal constructor */
    public TBKYC_QUESTIONNAIRE_RISK_LRATEVO(com.systex.jbranch.app.common.fps.table.TBKYC_QUESTIONNAIRE_RISK_LRATEPK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.app.common.fps.table.TBKYC_QUESTIONNAIRE_RISK_LRATEPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBKYC_QUESTIONNAIRE_RISK_LRATEPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getRL_NAME() {
		return RL_NAME;
	}

	public void setRL_NAME(String rL_NAME) {
		RL_NAME = rL_NAME;
	}

	public BigDecimal getRL_UP_RATE() {
		return RL_UP_RATE;
	}

	public void setRL_UP_RATE(BigDecimal rL_UP_RATE) {
		RL_UP_RATE = rL_UP_RATE;
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
        if ( !(other instanceof TBKYC_QUESTIONNAIRE_RISK_LRATEVO) ) return false;
        TBKYC_QUESTIONNAIRE_RISK_LRATEVO castOther = (TBKYC_QUESTIONNAIRE_RISK_LRATEVO) other;
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
