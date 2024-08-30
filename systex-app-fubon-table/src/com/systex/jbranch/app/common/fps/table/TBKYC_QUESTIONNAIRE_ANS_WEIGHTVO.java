package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBKYC_QUESTIONNAIRE_ANS_WEIGHTVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBKYC_QUESTIONNAIRE_ANS_WEIGHTPK comp_id;

    /** nullable persistent field */
    private BigDecimal FRACTION;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBKYC_QUESTIONNAIRE_ANS_WEIGHT";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBKYC_QUESTIONNAIRE_ANS_WEIGHTVO(com.systex.jbranch.app.common.fps.table.TBKYC_QUESTIONNAIRE_ANS_WEIGHTPK comp_id, BigDecimal FRACTION, Timestamp createtime, String creator, Timestamp lastupdate, String modifier, Long version) {
        this.comp_id = comp_id;
        this.FRACTION = FRACTION;
        this.createtime = createtime;
        this.creator = creator;
        this.lastupdate = lastupdate;
        this.modifier = modifier;
        this.version = version;
    }

    /** default constructor */
    public TBKYC_QUESTIONNAIRE_ANS_WEIGHTVO() {
    }

    /** minimal constructor */
    public TBKYC_QUESTIONNAIRE_ANS_WEIGHTVO(com.systex.jbranch.app.common.fps.table.TBKYC_QUESTIONNAIRE_ANS_WEIGHTPK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.app.common.fps.table.TBKYC_QUESTIONNAIRE_ANS_WEIGHTPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBKYC_QUESTIONNAIRE_ANS_WEIGHTPK comp_id) {
        this.comp_id = comp_id;
    }

    public BigDecimal getFRACTION() {
        return this.FRACTION;
    }

    public void setFRACTION(BigDecimal FRACTION) {
        this.FRACTION = FRACTION;
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
        if ( !(other instanceof TBKYC_QUESTIONNAIRE_ANS_WEIGHTVO) ) return false;
        TBKYC_QUESTIONNAIRE_ANS_WEIGHTVO castOther = (TBKYC_QUESTIONNAIRE_ANS_WEIGHTVO) other;
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
