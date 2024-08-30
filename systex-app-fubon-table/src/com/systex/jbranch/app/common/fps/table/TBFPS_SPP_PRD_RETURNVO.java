package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBFPS_SPP_PRD_RETURNVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBFPS_SPP_PRD_RETURNPK comp_id;

    /** nullable persistent field */
    private String PTYPE;

    /** nullable persistent field */
    private String RISK_TYPE;

    /** nullable persistent field */
    private String TRUST_CURR;

    /** nullable persistent field */
    private BigDecimal PURCHASE_ORG_AMT;

    /** nullable persistent field */
    private BigDecimal PURCHASE_TWD_AMT;

    /** nullable persistent field */
    private BigDecimal PORTFOLIO_RATIO;

    /** nullable persistent field */
    private BigDecimal RETURN_RATE;

    /** nullable persistent field */
    private String PORTFOLIO_TYPE;

    /** nullable persistent field */
    private String STATUS;

    /** nullable persistent field */
    private BigDecimal EX_RATE;

    /** nullable persistent field */
    private BigDecimal AMT_BUY;

    /** nullable persistent field */
    private BigDecimal AMT_BUY_TWD;

    /** nullable persistent field */
    private String TXN_TYPE;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBFPS_SPP_PRD_RETURN";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBFPS_SPP_PRD_RETURNVO(com.systex.jbranch.app.common.fps.table.TBFPS_SPP_PRD_RETURNPK comp_id, String PTYPE, String RISK_TYPE, String TRUST_CURR, BigDecimal PURCHASE_ORG_AMT, BigDecimal PURCHASE_TWD_AMT, BigDecimal PORTFOLIO_RATIO, BigDecimal RETURN_RATE, String PORTFOLIO_TYPE, String STATUS, BigDecimal EX_RATE, BigDecimal AMT_BUY, BigDecimal AMT_BUY_TWD, String TXN_TYPE, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.comp_id = comp_id;
        this.PTYPE = PTYPE;
        this.RISK_TYPE = RISK_TYPE;
        this.TRUST_CURR = TRUST_CURR;
        this.PURCHASE_ORG_AMT = PURCHASE_ORG_AMT;
        this.PURCHASE_TWD_AMT = PURCHASE_TWD_AMT;
        this.PORTFOLIO_RATIO = PORTFOLIO_RATIO;
        this.RETURN_RATE = RETURN_RATE;
        this.PORTFOLIO_TYPE = PORTFOLIO_TYPE;
        this.STATUS = STATUS;
        this.EX_RATE = EX_RATE;
        this.AMT_BUY = AMT_BUY;
        this.AMT_BUY_TWD = AMT_BUY_TWD;
        this.TXN_TYPE = TXN_TYPE;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBFPS_SPP_PRD_RETURNVO() {
    }

    /** minimal constructor */
    public TBFPS_SPP_PRD_RETURNVO(com.systex.jbranch.app.common.fps.table.TBFPS_SPP_PRD_RETURNPK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.app.common.fps.table.TBFPS_SPP_PRD_RETURNPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBFPS_SPP_PRD_RETURNPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getPTYPE() {
        return this.PTYPE;
    }

    public void setPTYPE(String PTYPE) {
        this.PTYPE = PTYPE;
    }

    public String getRISK_TYPE() {
        return this.RISK_TYPE;
    }

    public void setRISK_TYPE(String RISK_TYPE) {
        this.RISK_TYPE = RISK_TYPE;
    }

    public String getTRUST_CURR() {
        return this.TRUST_CURR;
    }

    public void setTRUST_CURR(String TRUST_CURR) {
        this.TRUST_CURR = TRUST_CURR;
    }

    public BigDecimal getPURCHASE_ORG_AMT() {
        return this.PURCHASE_ORG_AMT;
    }

    public void setPURCHASE_ORG_AMT(BigDecimal PURCHASE_ORG_AMT) {
        this.PURCHASE_ORG_AMT = PURCHASE_ORG_AMT;
    }

    public BigDecimal getPURCHASE_TWD_AMT() {
        return this.PURCHASE_TWD_AMT;
    }

    public void setPURCHASE_TWD_AMT(BigDecimal PURCHASE_TWD_AMT) {
        this.PURCHASE_TWD_AMT = PURCHASE_TWD_AMT;
    }

    public BigDecimal getPORTFOLIO_RATIO() {
        return this.PORTFOLIO_RATIO;
    }

    public void setPORTFOLIO_RATIO(BigDecimal PORTFOLIO_RATIO) {
        this.PORTFOLIO_RATIO = PORTFOLIO_RATIO;
    }

    public BigDecimal getRETURN_RATE() {
        return this.RETURN_RATE;
    }

    public void setRETURN_RATE(BigDecimal RETURN_RATE) {
        this.RETURN_RATE = RETURN_RATE;
    }

    public String getPORTFOLIO_TYPE() {
        return this.PORTFOLIO_TYPE;
    }

    public void setPORTFOLIO_TYPE(String PORTFOLIO_TYPE) {
        this.PORTFOLIO_TYPE = PORTFOLIO_TYPE;
    }

    public String getSTATUS() {
        return this.STATUS;
    }

    public void setSTATUS(String STATUS) {
        this.STATUS = STATUS;
    }

    public BigDecimal getEX_RATE() {
        return this.EX_RATE;
    }

    public void setEX_RATE(BigDecimal EX_RATE) {
        this.EX_RATE = EX_RATE;
    }

    public BigDecimal getAMT_BUY() {
        return this.AMT_BUY;
    }

    public void setAMT_BUY(BigDecimal AMT_BUY) {
        this.AMT_BUY = AMT_BUY;
    }

    public BigDecimal getAMT_BUY_TWD() {
        return this.AMT_BUY_TWD;
    }

    public void setAMT_BUY_TWD(BigDecimal AMT_BUY_TWD) {
        this.AMT_BUY_TWD = AMT_BUY_TWD;
    }

    public String getTXN_TYPE() {
        return this.TXN_TYPE;
    }

    public void setTXN_TYPE(String TXN_TYPE) {
        this.TXN_TYPE = TXN_TYPE;
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
        if ( !(other instanceof TBFPS_SPP_PRD_RETURNVO) ) return false;
        TBFPS_SPP_PRD_RETURNVO castOther = (TBFPS_SPP_PRD_RETURNVO) other;
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
