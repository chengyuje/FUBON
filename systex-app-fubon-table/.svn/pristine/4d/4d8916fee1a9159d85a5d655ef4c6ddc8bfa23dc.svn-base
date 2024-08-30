package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBINS_SPPEDU_DETAILVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBINS_SPPEDU_DETAILPK comp_id;

    /** nullable persistent field */
    private Timestamp CHILD_BIRTHDATE;

    /** nullable persistent field */
    private BigDecimal END_EDU_AGE;

    /** nullable persistent field */
    private BigDecimal EDU_AMT;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBINS_SPPEDU_DETAIL";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBINS_SPPEDU_DETAILVO(com.systex.jbranch.app.common.fps.table.TBINS_SPPEDU_DETAILPK comp_id, Timestamp CHILD_BIRTHDATE, BigDecimal END_EDU_AGE, BigDecimal EDU_AMT, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.comp_id = comp_id;
        this.CHILD_BIRTHDATE = CHILD_BIRTHDATE;
        this.END_EDU_AGE = END_EDU_AGE;
        this.EDU_AMT = EDU_AMT;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBINS_SPPEDU_DETAILVO() {
    }

    /** minimal constructor */
    public TBINS_SPPEDU_DETAILVO(com.systex.jbranch.app.common.fps.table.TBINS_SPPEDU_DETAILPK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.app.common.fps.table.TBINS_SPPEDU_DETAILPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBINS_SPPEDU_DETAILPK comp_id) {
        this.comp_id = comp_id;
    }

    public Timestamp getCHILD_BIRTHDATE() {
        return this.CHILD_BIRTHDATE;
    }

    public void setCHILD_BIRTHDATE(Timestamp CHILD_BIRTHDATE) {
        this.CHILD_BIRTHDATE = CHILD_BIRTHDATE;
    }

    public BigDecimal getEND_EDU_AGE() {
        return this.END_EDU_AGE;
    }

    public void setEND_EDU_AGE(BigDecimal END_EDU_AGE) {
        this.END_EDU_AGE = END_EDU_AGE;
    }

    public BigDecimal getEDU_AMT() {
        return this.EDU_AMT;
    }

    public void setEDU_AMT(BigDecimal EDU_AMT) {
        this.EDU_AMT = EDU_AMT;
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
        if ( !(other instanceof TBINS_SPPEDU_DETAILVO) ) return false;
        TBINS_SPPEDU_DETAILVO castOther = (TBINS_SPPEDU_DETAILVO) other;
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
