package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBKYC_INVESTOREXAM_D_EXPVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBKYC_INVESTOREXAM_D_EXPPK comp_id;

    /** persistent field */
    private BigDecimal QSEQUENCE;

    /** persistent field */
    private String ANSWER;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBKYC_INVESTOREXAM_D_EXP";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBKYC_INVESTOREXAM_D_EXPVO(com.systex.jbranch.app.common.fps.table.TBKYC_INVESTOREXAM_D_EXPPK comp_id, BigDecimal QSEQUENCE, String ANSWER) {
        this.comp_id = comp_id;
        this.QSEQUENCE = QSEQUENCE;
        this.ANSWER = ANSWER;
    }

    /** default constructor */
    public TBKYC_INVESTOREXAM_D_EXPVO() {
    }

    public com.systex.jbranch.app.common.fps.table.TBKYC_INVESTOREXAM_D_EXPPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBKYC_INVESTOREXAM_D_EXPPK comp_id) {
        this.comp_id = comp_id;
    }

    public BigDecimal getQSEQUENCE() {
        return this.QSEQUENCE;
    }

    public void setQSEQUENCE(BigDecimal QSEQUENCE) {
        this.QSEQUENCE = QSEQUENCE;
    }

    public String getANSWER() {
        return this.ANSWER;
    }

    public void setANSWER(String ANSWER) {
        this.ANSWER = ANSWER;
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
        if ( !(other instanceof TBKYC_INVESTOREXAM_D_EXPVO) ) return false;
        TBKYC_INVESTOREXAM_D_EXPVO castOther = (TBKYC_INVESTOREXAM_D_EXPVO) other;
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
