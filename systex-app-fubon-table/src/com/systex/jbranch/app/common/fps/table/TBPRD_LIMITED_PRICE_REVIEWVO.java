package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPRD_LIMITED_PRICE_REVIEWVO extends VOBase {

    /** identifier field */
    private BigDecimal SEQ;

    /** nullable persistent field */
    private String PRD_ID;

    /** nullable persistent field */
    private String CUST_ID;

    /** nullable persistent field */
    private BigDecimal LIMITED_PRICE;

    /** nullable persistent field */
    private BigDecimal CHANNEL_FEE;

    /** nullable persistent field */
    private String ACT_TYPE;

    /** nullable persistent field */
    private String REVIEW_STATUS;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBPRD_LIMITED_PRICE_REVIEW";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBPRD_LIMITED_PRICE_REVIEWVO(BigDecimal SEQ, String PRD_ID, String CUST_ID, BigDecimal LIMITED_PRICE, BigDecimal CHANNEL_FEE, String ACT_TYPE, String REVIEW_STATUS, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.SEQ = SEQ;
        this.PRD_ID = PRD_ID;
        this.CUST_ID = CUST_ID;
        this.LIMITED_PRICE = LIMITED_PRICE;
        this.CHANNEL_FEE = CHANNEL_FEE;
        this.ACT_TYPE = ACT_TYPE;
        this.REVIEW_STATUS = REVIEW_STATUS;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBPRD_LIMITED_PRICE_REVIEWVO() {
    }

    /** minimal constructor */
    public TBPRD_LIMITED_PRICE_REVIEWVO(BigDecimal SEQ) {
        this.SEQ = SEQ;
    }

    public BigDecimal getSEQ() {
        return this.SEQ;
    }

    public void setSEQ(BigDecimal SEQ) {
        this.SEQ = SEQ;
    }

    public String getPRD_ID() {
        return this.PRD_ID;
    }

    public void setPRD_ID(String PRD_ID) {
        this.PRD_ID = PRD_ID;
    }

    public String getCUST_ID() {
        return this.CUST_ID;
    }

    public void setCUST_ID(String CUST_ID) {
        this.CUST_ID = CUST_ID;
    }

    public BigDecimal getLIMITED_PRICE() {
        return this.LIMITED_PRICE;
    }

    public void setLIMITED_PRICE(BigDecimal LIMITED_PRICE) {
        this.LIMITED_PRICE = LIMITED_PRICE;
    }

    public BigDecimal getCHANNEL_FEE() {
        return this.CHANNEL_FEE;
    }

    public void setCHANNEL_FEE(BigDecimal CHANNEL_FEE) {
        this.CHANNEL_FEE = CHANNEL_FEE;
    }

    public String getACT_TYPE() {
        return this.ACT_TYPE;
    }

    public void setACT_TYPE(String ACT_TYPE) {
        this.ACT_TYPE = ACT_TYPE;
    }

    public String getREVIEW_STATUS() {
        return this.REVIEW_STATUS;
    }

    public void setREVIEW_STATUS(String REVIEW_STATUS) {
        this.REVIEW_STATUS = REVIEW_STATUS;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("SEQ", getSEQ())
            .toString();
    }

}
