package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBJSB_INS_PROD_LIFEITEMVO extends VOBase {

    /** identifier field */
    private TBJSB_INS_PROD_LIFEITEMPK comp_id;

    /** persistent field */
    private String PRODUCTPUDTYPE;

    /** persistent field */
    private BigDecimal PRODUCTPUD;

    /** persistent field */
    private String PRODUCTEDTYPE;

    /** persistent field */
    private BigDecimal PRODUCTED;

    /** nullable persistent field */
    private BigDecimal PRODUCTEXPDATE;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBJSB_INS_PROD_LIFEITEM";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBJSB_INS_PROD_LIFEITEMVO(TBJSB_INS_PROD_LIFEITEMPK comp_id, String PRODUCTPUDTYPE, BigDecimal PRODUCTPUD, String PRODUCTEDTYPE, BigDecimal PRODUCTED, BigDecimal PRODUCTEXPDATE, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.comp_id = comp_id;
        this.PRODUCTPUDTYPE = PRODUCTPUDTYPE;
        this.PRODUCTPUD = PRODUCTPUD;
        this.PRODUCTEDTYPE = PRODUCTEDTYPE;
        this.PRODUCTED = PRODUCTED;
        this.PRODUCTEXPDATE = PRODUCTEXPDATE;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBJSB_INS_PROD_LIFEITEMVO() {
    }

    /** minimal constructor */
    public TBJSB_INS_PROD_LIFEITEMVO(TBJSB_INS_PROD_LIFEITEMPK comp_id, String PRODUCTPUDTYPE, BigDecimal PRODUCTPUD, String PRODUCTEDTYPE, BigDecimal PRODUCTED) {
        this.comp_id = comp_id;
        this.PRODUCTPUDTYPE = PRODUCTPUDTYPE;
        this.PRODUCTPUD = PRODUCTPUD;
        this.PRODUCTEDTYPE = PRODUCTEDTYPE;
        this.PRODUCTED = PRODUCTED;
    }

    public TBJSB_INS_PROD_LIFEITEMPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(TBJSB_INS_PROD_LIFEITEMPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getPRODUCTPUDTYPE() {
        return this.PRODUCTPUDTYPE;
    }

    public void setPRODUCTPUDTYPE(String PRODUCTPUDTYPE) {
        this.PRODUCTPUDTYPE = PRODUCTPUDTYPE;
    }

    public BigDecimal getPRODUCTPUD() {
        return this.PRODUCTPUD;
    }

    public void setPRODUCTPUD(BigDecimal PRODUCTPUD) {
        this.PRODUCTPUD = PRODUCTPUD;
    }

    public String getPRODUCTEDTYPE() {
        return this.PRODUCTEDTYPE;
    }

    public void setPRODUCTEDTYPE(String PRODUCTEDTYPE) {
        this.PRODUCTEDTYPE = PRODUCTEDTYPE;
    }

    public BigDecimal getPRODUCTED() {
        return this.PRODUCTED;
    }

    public void setPRODUCTED(BigDecimal PRODUCTED) {
        this.PRODUCTED = PRODUCTED;
    }

    public BigDecimal getPRODUCTEXPDATE() {
        return this.PRODUCTEXPDATE;
    }

    public void setPRODUCTEXPDATE(BigDecimal PRODUCTEXPDATE) {
        this.PRODUCTEXPDATE = PRODUCTEXPDATE;
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
        if ( !(other instanceof TBJSB_INS_PROD_LIFEITEMVO) ) return false;
        TBJSB_INS_PROD_LIFEITEMVO castOther = (TBJSB_INS_PROD_LIFEITEMVO) other;
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
