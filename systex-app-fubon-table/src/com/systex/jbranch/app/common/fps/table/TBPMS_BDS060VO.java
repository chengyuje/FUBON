package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPMS_BDS060VO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBPMS_BDS060PK comp_id;

    /** nullable persistent field */
    private String BDF03;

    /** nullable persistent field */
    private BigDecimal BDF07;

    /** nullable persistent field */
    private String BDF09;

    /** nullable persistent field */
    private String BDF0A;

    /** nullable persistent field */
    private String BDF06;

    /** nullable persistent field */
    private BigDecimal BDF0G;

    /** nullable persistent field */
    private BigDecimal BDF08;

    /** nullable persistent field */
    private BigDecimal BDF0B;

    /** nullable persistent field */
    private Timestamp BDF04;

    /** nullable persistent field */
    private String BDF0D;

    /** nullable persistent field */
    private BigDecimal BDF0E;

    /** nullable persistent field */
    private String BDF0F;

    /** nullable persistent field */
    private String BD060;

    /** nullable persistent field */
    private String MTIME;

    /** nullable persistent field */
    private Timestamp MDATE;

    /** nullable persistent field */
    private String MUSR;

    /** nullable persistent field */
    private String MPGM;

    /** nullable persistent field */
    private BigDecimal TRANS_SN;

    /** nullable persistent field */
    private BigDecimal TRANS_ROWS;

    /** nullable persistent field */
    private String TRANS_ACTION;

    /** nullable persistent field */
    private String ERROR_CHECK;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBPMS_BDS060";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBPMS_BDS060VO(com.systex.jbranch.app.common.fps.table.TBPMS_BDS060PK comp_id, String BDF03, BigDecimal BDF07, String BDF09, String BDF0A, String BDF06, BigDecimal BDF0G, BigDecimal BDF08, BigDecimal BDF0B, Timestamp BDF04, String BDF0D, BigDecimal BDF0E, String BDF0F, String BD060, String MTIME, Timestamp MDATE, String MUSR, String MPGM, BigDecimal TRANS_SN, BigDecimal TRANS_ROWS, String TRANS_ACTION, String ERROR_CHECK, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.comp_id = comp_id;
        this.BDF03 = BDF03;
        this.BDF07 = BDF07;
        this.BDF09 = BDF09;
        this.BDF0A = BDF0A;
        this.BDF06 = BDF06;
        this.BDF0G = BDF0G;
        this.BDF08 = BDF08;
        this.BDF0B = BDF0B;
        this.BDF04 = BDF04;
        this.BDF0D = BDF0D;
        this.BDF0E = BDF0E;
        this.BDF0F = BDF0F;
        this.BD060 = BD060;
        this.MTIME = MTIME;
        this.MDATE = MDATE;
        this.MUSR = MUSR;
        this.MPGM = MPGM;
        this.TRANS_SN = TRANS_SN;
        this.TRANS_ROWS = TRANS_ROWS;
        this.TRANS_ACTION = TRANS_ACTION;
        this.ERROR_CHECK = ERROR_CHECK;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBPMS_BDS060VO() {
    }

    /** minimal constructor */
    public TBPMS_BDS060VO(com.systex.jbranch.app.common.fps.table.TBPMS_BDS060PK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.app.common.fps.table.TBPMS_BDS060PK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBPMS_BDS060PK comp_id) {
        this.comp_id = comp_id;
    }

    public String getBDF03() {
        return this.BDF03;
    }

    public void setBDF03(String BDF03) {
        this.BDF03 = BDF03;
    }

    public BigDecimal getBDF07() {
        return this.BDF07;
    }

    public void setBDF07(BigDecimal BDF07) {
        this.BDF07 = BDF07;
    }

    public String getBDF09() {
        return this.BDF09;
    }

    public void setBDF09(String BDF09) {
        this.BDF09 = BDF09;
    }

    public String getBDF0A() {
        return this.BDF0A;
    }

    public void setBDF0A(String BDF0A) {
        this.BDF0A = BDF0A;
    }

    public String getBDF06() {
        return this.BDF06;
    }

    public void setBDF06(String BDF06) {
        this.BDF06 = BDF06;
    }

    public BigDecimal getBDF0G() {
        return this.BDF0G;
    }

    public void setBDF0G(BigDecimal BDF0G) {
        this.BDF0G = BDF0G;
    }

    public BigDecimal getBDF08() {
        return this.BDF08;
    }

    public void setBDF08(BigDecimal BDF08) {
        this.BDF08 = BDF08;
    }

    public BigDecimal getBDF0B() {
        return this.BDF0B;
    }

    public void setBDF0B(BigDecimal BDF0B) {
        this.BDF0B = BDF0B;
    }

    public Timestamp getBDF04() {
        return this.BDF04;
    }

    public void setBDF04(Timestamp BDF04) {
        this.BDF04 = BDF04;
    }

    public String getBDF0D() {
        return this.BDF0D;
    }

    public void setBDF0D(String BDF0D) {
        this.BDF0D = BDF0D;
    }

    public BigDecimal getBDF0E() {
        return this.BDF0E;
    }

    public void setBDF0E(BigDecimal BDF0E) {
        this.BDF0E = BDF0E;
    }

    public String getBDF0F() {
        return this.BDF0F;
    }

    public void setBDF0F(String BDF0F) {
        this.BDF0F = BDF0F;
    }

    public String getBD060() {
        return this.BD060;
    }

    public void setBD060(String BD060) {
        this.BD060 = BD060;
    }

    public String getMTIME() {
        return this.MTIME;
    }

    public void setMTIME(String MTIME) {
        this.MTIME = MTIME;
    }

    public Timestamp getMDATE() {
        return this.MDATE;
    }

    public void setMDATE(Timestamp MDATE) {
        this.MDATE = MDATE;
    }

    public String getMUSR() {
        return this.MUSR;
    }

    public void setMUSR(String MUSR) {
        this.MUSR = MUSR;
    }

    public String getMPGM() {
        return this.MPGM;
    }

    public void setMPGM(String MPGM) {
        this.MPGM = MPGM;
    }

    public BigDecimal getTRANS_SN() {
        return this.TRANS_SN;
    }

    public void setTRANS_SN(BigDecimal TRANS_SN) {
        this.TRANS_SN = TRANS_SN;
    }

    public BigDecimal getTRANS_ROWS() {
        return this.TRANS_ROWS;
    }

    public void setTRANS_ROWS(BigDecimal TRANS_ROWS) {
        this.TRANS_ROWS = TRANS_ROWS;
    }

    public String getTRANS_ACTION() {
        return this.TRANS_ACTION;
    }

    public void setTRANS_ACTION(String TRANS_ACTION) {
        this.TRANS_ACTION = TRANS_ACTION;
    }

    public String getERROR_CHECK() {
        return this.ERROR_CHECK;
    }

    public void setERROR_CHECK(String ERROR_CHECK) {
        this.ERROR_CHECK = ERROR_CHECK;
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
        if ( !(other instanceof TBPMS_BDS060VO) ) return false;
        TBPMS_BDS060VO castOther = (TBPMS_BDS060VO) other;
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
