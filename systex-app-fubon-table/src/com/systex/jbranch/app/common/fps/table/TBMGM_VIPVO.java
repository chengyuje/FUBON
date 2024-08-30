package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBMGM_VIPVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBMGM_VIPPK comp_id;

    /** persistent field */
    private String SEQ;

    /** nullable persistent field */
    private BigDecimal TOTAL;

    /** nullable persistent field */
    private BigDecimal EXCHANGE;

    /** nullable persistent field */
    private BigDecimal REMAIN;

    /** nullable persistent field */
    private String GIFT01;

    /** nullable persistent field */
    private String GIFT02;

    /** nullable persistent field */
    private String GIFT03;

    /** nullable persistent field */
    private String GIFT04;

    /** nullable persistent field */
    private String GIFT05;

    /** nullable persistent field */
    private String GIFT06;

    /** nullable persistent field */
    private String GIFT07;

    /** nullable persistent field */
    private String GIFT08;

    /** nullable persistent field */
    private String GIFT09;

    /** nullable persistent field */
    private String GIFT10;

    /** nullable persistent field */
    private String RELEASE_YN;

    /** nullable persistent field */
    private Timestamp RELEASE_DATE;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBMGM_VIP";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBMGM_VIPVO(com.systex.jbranch.app.common.fps.table.TBMGM_VIPPK comp_id, String SEQ, BigDecimal TOTAL, BigDecimal EXCHANGE, BigDecimal REMAIN, String GIFT01, String GIFT02, String GIFT03, String GIFT04, String GIFT05, String GIFT06, String GIFT07, String GIFT08, String GIFT09, String GIFT10, String RELEASE_YN, Timestamp RELEASE_DATE, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.comp_id = comp_id;
        this.SEQ = SEQ;
        this.TOTAL = TOTAL;
        this.EXCHANGE = EXCHANGE;
        this.REMAIN = REMAIN;
        this.GIFT01 = GIFT01;
        this.GIFT02 = GIFT02;
        this.GIFT03 = GIFT03;
        this.GIFT04 = GIFT04;
        this.GIFT05 = GIFT05;
        this.GIFT06 = GIFT06;
        this.GIFT07 = GIFT07;
        this.GIFT08 = GIFT08;
        this.GIFT09 = GIFT09;
        this.GIFT10 = GIFT10;
        this.RELEASE_YN = RELEASE_YN;
        this.RELEASE_DATE = RELEASE_DATE;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBMGM_VIPVO() {
    }

    /** minimal constructor */
    public TBMGM_VIPVO(com.systex.jbranch.app.common.fps.table.TBMGM_VIPPK comp_id, String SEQ) {
        this.comp_id = comp_id;
        this.SEQ = SEQ;
    }

    public com.systex.jbranch.app.common.fps.table.TBMGM_VIPPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBMGM_VIPPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getSEQ() {
        return this.SEQ;
    }

    public void setSEQ(String SEQ) {
        this.SEQ = SEQ;
    }

    public BigDecimal getTOTAL() {
        return this.TOTAL;
    }

    public void setTOTAL(BigDecimal TOTAL) {
        this.TOTAL = TOTAL;
    }

    public BigDecimal getEXCHANGE() {
        return this.EXCHANGE;
    }

    public void setEXCHANGE(BigDecimal EXCHANGE) {
        this.EXCHANGE = EXCHANGE;
    }

    public BigDecimal getREMAIN() {
        return this.REMAIN;
    }

    public void setREMAIN(BigDecimal REMAIN) {
        this.REMAIN = REMAIN;
    }

    public String getGIFT01() {
        return this.GIFT01;
    }

    public void setGIFT01(String GIFT01) {
        this.GIFT01 = GIFT01;
    }

    public String getGIFT02() {
        return this.GIFT02;
    }

    public void setGIFT02(String GIFT02) {
        this.GIFT02 = GIFT02;
    }

    public String getGIFT03() {
        return this.GIFT03;
    }

    public void setGIFT03(String GIFT03) {
        this.GIFT03 = GIFT03;
    }

    public String getGIFT04() {
        return this.GIFT04;
    }

    public void setGIFT04(String GIFT04) {
        this.GIFT04 = GIFT04;
    }

    public String getGIFT05() {
        return this.GIFT05;
    }

    public void setGIFT05(String GIFT05) {
        this.GIFT05 = GIFT05;
    }

    public String getGIFT06() {
        return this.GIFT06;
    }

    public void setGIFT06(String GIFT06) {
        this.GIFT06 = GIFT06;
    }

    public String getGIFT07() {
        return this.GIFT07;
    }

    public void setGIFT07(String GIFT07) {
        this.GIFT07 = GIFT07;
    }

    public String getGIFT08() {
        return this.GIFT08;
    }

    public void setGIFT08(String GIFT08) {
        this.GIFT08 = GIFT08;
    }

    public String getGIFT09() {
        return this.GIFT09;
    }

    public void setGIFT09(String GIFT09) {
        this.GIFT09 = GIFT09;
    }

    public String getGIFT10() {
        return this.GIFT10;
    }

    public void setGIFT10(String GIFT10) {
        this.GIFT10 = GIFT10;
    }

    public String getRELEASE_YN() {
        return this.RELEASE_YN;
    }

    public void setRELEASE_YN(String RELEASE_YN) {
        this.RELEASE_YN = RELEASE_YN;
    }

    public Timestamp getRELEASE_DATE() {
        return this.RELEASE_DATE;
    }

    public void setRELEASE_DATE(Timestamp RELEASE_DATE) {
        this.RELEASE_DATE = RELEASE_DATE;
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
        if ( !(other instanceof TBMGM_VIPVO) ) return false;
        TBMGM_VIPVO castOther = (TBMGM_VIPVO) other;
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
