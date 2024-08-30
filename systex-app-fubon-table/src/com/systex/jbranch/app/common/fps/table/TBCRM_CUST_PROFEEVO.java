package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBCRM_CUST_PROFEEVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBCRM_CUST_PROFEEPK comp_id;

    /** nullable persistent field */
    private String DATA_SOURCE;

    /** nullable persistent field */
    private String BRA_NNBR;

    /** nullable persistent field */
    private String AO_CODE;

    /** nullable persistent field */
    private String PROD_TYPE1;

    /** nullable persistent field */
    private String PROD_TYPE2;

    /** nullable persistent field */
    private String PROD_TYPE3;

    /** nullable persistent field */
    private String PROD_TYPE4;

    /** nullable persistent field */
    private BigDecimal TXN_CNT;

    /** nullable persistent field */
    private BigDecimal TXN_AMT;

    /** nullable persistent field */
    private BigDecimal TXN_FEE;

    /** nullable persistent field */
    private BigDecimal ACT_PRFT;

    /** nullable persistent field */
    private String PARTY_CLASS;

    /** nullable persistent field */
    private String SERVICE_DEGREE;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBCRM_CUST_PROFEE";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBCRM_CUST_PROFEEVO(com.systex.jbranch.app.common.fps.table.TBCRM_CUST_PROFEEPK comp_id, String DATA_SOURCE, String BRA_NNBR, String AO_CODE, String PROD_TYPE1, String PROD_TYPE2, String PROD_TYPE3, String PROD_TYPE4, BigDecimal TXN_CNT, BigDecimal TXN_AMT, BigDecimal TXN_FEE, BigDecimal ACT_PRFT, String PARTY_CLASS, String SERVICE_DEGREE, String creator, Timestamp createtime, String modifier, Timestamp lastupdate, Long version) {
        this.comp_id = comp_id;
        this.DATA_SOURCE = DATA_SOURCE;
        this.BRA_NNBR = BRA_NNBR;
        this.AO_CODE = AO_CODE;
        this.PROD_TYPE1 = PROD_TYPE1;
        this.PROD_TYPE2 = PROD_TYPE2;
        this.PROD_TYPE3 = PROD_TYPE3;
        this.PROD_TYPE4 = PROD_TYPE4;
        this.TXN_CNT = TXN_CNT;
        this.TXN_AMT = TXN_AMT;
        this.TXN_FEE = TXN_FEE;
        this.ACT_PRFT = ACT_PRFT;
        this.PARTY_CLASS = PARTY_CLASS;
        this.SERVICE_DEGREE = SERVICE_DEGREE;
        this.creator = creator;
        this.createtime = createtime;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBCRM_CUST_PROFEEVO() {
    }

    /** minimal constructor */
    public TBCRM_CUST_PROFEEVO(com.systex.jbranch.app.common.fps.table.TBCRM_CUST_PROFEEPK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.app.common.fps.table.TBCRM_CUST_PROFEEPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBCRM_CUST_PROFEEPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getDATA_SOURCE() {
        return this.DATA_SOURCE;
    }

    public void setDATA_SOURCE(String DATA_SOURCE) {
        this.DATA_SOURCE = DATA_SOURCE;
    }

    public String getBRA_NNBR() {
        return this.BRA_NNBR;
    }

    public void setBRA_NNBR(String BRA_NNBR) {
        this.BRA_NNBR = BRA_NNBR;
    }

    public String getAO_CODE() {
        return this.AO_CODE;
    }

    public void setAO_CODE(String AO_CODE) {
        this.AO_CODE = AO_CODE;
    }

    public String getPROD_TYPE1() {
        return this.PROD_TYPE1;
    }

    public void setPROD_TYPE1(String PROD_TYPE1) {
        this.PROD_TYPE1 = PROD_TYPE1;
    }

    public String getPROD_TYPE2() {
        return this.PROD_TYPE2;
    }

    public void setPROD_TYPE2(String PROD_TYPE2) {
        this.PROD_TYPE2 = PROD_TYPE2;
    }

    public String getPROD_TYPE3() {
        return this.PROD_TYPE3;
    }

    public void setPROD_TYPE3(String PROD_TYPE3) {
        this.PROD_TYPE3 = PROD_TYPE3;
    }

    public String getPROD_TYPE4() {
        return this.PROD_TYPE4;
    }

    public void setPROD_TYPE4(String PROD_TYPE4) {
        this.PROD_TYPE4 = PROD_TYPE4;
    }

    public BigDecimal getTXN_CNT() {
        return this.TXN_CNT;
    }

    public void setTXN_CNT(BigDecimal TXN_CNT) {
        this.TXN_CNT = TXN_CNT;
    }

    public BigDecimal getTXN_AMT() {
        return this.TXN_AMT;
    }

    public void setTXN_AMT(BigDecimal TXN_AMT) {
        this.TXN_AMT = TXN_AMT;
    }

    public BigDecimal getTXN_FEE() {
        return this.TXN_FEE;
    }

    public void setTXN_FEE(BigDecimal TXN_FEE) {
        this.TXN_FEE = TXN_FEE;
    }

    public BigDecimal getACT_PRFT() {
        return this.ACT_PRFT;
    }

    public void setACT_PRFT(BigDecimal ACT_PRFT) {
        this.ACT_PRFT = ACT_PRFT;
    }

    public String getPARTY_CLASS() {
        return this.PARTY_CLASS;
    }

    public void setPARTY_CLASS(String PARTY_CLASS) {
        this.PARTY_CLASS = PARTY_CLASS;
    }

    public String getSERVICE_DEGREE() {
        return this.SERVICE_DEGREE;
    }

    public void setSERVICE_DEGREE(String SERVICE_DEGREE) {
        this.SERVICE_DEGREE = SERVICE_DEGREE;
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
        if ( !(other instanceof TBCRM_CUST_PROFEEVO) ) return false;
        TBCRM_CUST_PROFEEVO castOther = (TBCRM_CUST_PROFEEVO) other;
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
