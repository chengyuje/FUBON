package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPRD_SNVO extends VOBase {

    /** identifier field */
    private String PRD_ID;

    /** nullable persistent field */
    private String SN_CNAME;

    /** nullable persistent field */
    private String SN_CNAME_A;

    /** nullable persistent field */
    private String SN_TYPE;

    /** nullable persistent field */
    private String CURRENCY_STD_ID;

    /** nullable persistent field */
    private Timestamp DATE_OF_MATURITY;

    /** nullable persistent field */
    private BigDecimal RATE_GUARANTEEPAY;

    /** nullable persistent field */
    private String RISKCATE_ID;

    /** nullable persistent field */
    private String PI_BUY;

    /** nullable persistent field */
    private String HNWC_BUY;
    
    /** nullable persistent field */
    private String OBU_BUY;

    /** nullable persistent field */
    private String IS_SALE;

    /** nullable persistent field */
    private String W8BEN_MARK;

    /** nullable persistent field */
    private String GLCODE;

    /** nullable persistent field */
    private BigDecimal UNIT_OF_PURCHASE;

    /** nullable persistent field */
    private BigDecimal BOND_VALUE;

    /** nullable persistent field */
    private String RECORD_FLAG;

    /** nullable persistent field */
    private String PROJECT;

    /** nullable persistent field */
    private String CUSTOMER_LEVEL;
    
    /** nullable persistent field */
    private BigDecimal YEAR_OF_MATURITY;

public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBPRD_SN";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBPRD_SNVO(String PRD_ID, String SN_CNAME, String SN_CNAME_A, String SN_TYPE, String CURRENCY_STD_ID, Timestamp DATE_OF_MATURITY, BigDecimal RATE_GUARANTEEPAY, String RISKCATE_ID, String PI_BUY, String OBU_BUY, String IS_SALE, String W8BEN_MARK, String GLCODE, BigDecimal UNIT_OF_PURCHASE, BigDecimal BOND_VALUE, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, String RECORD_FLAG, Long version, String PROJECT, String CUSTOMER_LEVEL, BigDecimal YEAR_OF_MATURITY) {
        this.PRD_ID = PRD_ID;
        this.SN_CNAME = SN_CNAME;
        this.SN_CNAME_A = SN_CNAME_A;
        this.SN_TYPE = SN_TYPE;
        this.CURRENCY_STD_ID = CURRENCY_STD_ID;
        this.DATE_OF_MATURITY = DATE_OF_MATURITY;
        this.RATE_GUARANTEEPAY = RATE_GUARANTEEPAY;
        this.RISKCATE_ID = RISKCATE_ID;
        this.PI_BUY = PI_BUY;
        this.OBU_BUY = OBU_BUY;
        this.IS_SALE = IS_SALE;
        this.W8BEN_MARK = W8BEN_MARK;
        this.GLCODE = GLCODE;
        this.UNIT_OF_PURCHASE = UNIT_OF_PURCHASE;
        this.BOND_VALUE = BOND_VALUE;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.RECORD_FLAG = RECORD_FLAG;
        this.version = version;
        this.PROJECT = PROJECT;
        this.CUSTOMER_LEVEL = CUSTOMER_LEVEL;
        this.YEAR_OF_MATURITY = YEAR_OF_MATURITY;
    }

    /** default constructor */
    public TBPRD_SNVO() {
    }

    /** minimal constructor */
    public TBPRD_SNVO(String PRD_ID) {
        this.PRD_ID = PRD_ID;
    }

    public String getPRD_ID() {
        return this.PRD_ID;
    }

    public void setPRD_ID(String PRD_ID) {
        this.PRD_ID = PRD_ID;
    }

    public String getSN_CNAME() {
        return this.SN_CNAME;
    }

    public void setSN_CNAME(String SN_CNAME) {
        this.SN_CNAME = SN_CNAME;
    }

    public String getSN_CNAME_A() {
        return this.SN_CNAME_A;
    }

    public void setSN_CNAME_A(String SN_CNAME_A) {
        this.SN_CNAME_A = SN_CNAME_A;
    }

    public String getSN_TYPE() {
        return this.SN_TYPE;
    }

    public void setSN_TYPE(String SN_TYPE) {
        this.SN_TYPE = SN_TYPE;
    }

    public String getCURRENCY_STD_ID() {
        return this.CURRENCY_STD_ID;
    }

    public void setCURRENCY_STD_ID(String CURRENCY_STD_ID) {
        this.CURRENCY_STD_ID = CURRENCY_STD_ID;
    }

    public Timestamp getDATE_OF_MATURITY() {
        return this.DATE_OF_MATURITY;
    }

    public void setDATE_OF_MATURITY(Timestamp DATE_OF_MATURITY) {
        this.DATE_OF_MATURITY = DATE_OF_MATURITY;
    }

    public BigDecimal getRATE_GUARANTEEPAY() {
        return this.RATE_GUARANTEEPAY;
    }

    public void setRATE_GUARANTEEPAY(BigDecimal RATE_GUARANTEEPAY) {
        this.RATE_GUARANTEEPAY = RATE_GUARANTEEPAY;
    }

    public String getRISKCATE_ID() {
        return this.RISKCATE_ID;
    }

    public void setRISKCATE_ID(String RISKCATE_ID) {
        this.RISKCATE_ID = RISKCATE_ID;
    }

    public String getPI_BUY() {
        return this.PI_BUY;
    }

    public void setPI_BUY(String PI_BUY) {
        this.PI_BUY = PI_BUY;
    }

    public String getHNWC_BUY() {
		return HNWC_BUY;
	}

	public void setHNWC_BUY(String hNWC_BUY) {
		HNWC_BUY = hNWC_BUY;
	}

	public String getOBU_BUY() {
        return this.OBU_BUY;
    }

    public void setOBU_BUY(String OBU_BUY) {
        this.OBU_BUY = OBU_BUY;
    }

    public String getIS_SALE() {
        return this.IS_SALE;
    }

    public void setIS_SALE(String IS_SALE) {
        this.IS_SALE = IS_SALE;
    }

    public String getW8BEN_MARK() {
        return this.W8BEN_MARK;
    }

    public void setW8BEN_MARK(String W8BEN_MARK) {
        this.W8BEN_MARK = W8BEN_MARK;
    }

    public String getGLCODE() {
        return this.GLCODE;
    }

    public void setGLCODE(String GLCODE) {
        this.GLCODE = GLCODE;
    }

    public BigDecimal getUNIT_OF_PURCHASE() {
        return this.UNIT_OF_PURCHASE;
    }

    public void setUNIT_OF_PURCHASE(BigDecimal UNIT_OF_PURCHASE) {
        this.UNIT_OF_PURCHASE = UNIT_OF_PURCHASE;
    }

    public BigDecimal getBOND_VALUE() {
        return this.BOND_VALUE;
    }

    public void setBOND_VALUE(BigDecimal BOND_VALUE) {
        this.BOND_VALUE = BOND_VALUE;
    }

    public String getRECORD_FLAG() {
        return this.RECORD_FLAG;
    }

    public void setRECORD_FLAG(String RECORD_FLAG) {
        this.RECORD_FLAG = RECORD_FLAG;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("PRD_ID", getPRD_ID())
            .toString();
    }

	public String getPROJECT() {
		return PROJECT;
	}

	public void setPROJECT(String pROJECT) {
		PROJECT = pROJECT;
	}

	public String getCUSTOMER_LEVEL() {
		return CUSTOMER_LEVEL;
	}

	public void setCUSTOMER_LEVEL(String cUSTOMER_LEVEL) {
		CUSTOMER_LEVEL = cUSTOMER_LEVEL;
	}

	public BigDecimal getYEAR_OF_MATURITY() {
		return YEAR_OF_MATURITY;
	}

	public void setYEAR_OF_MATURITY(BigDecimal yEAR_OF_MATURITY) {
		YEAR_OF_MATURITY = yEAR_OF_MATURITY;
	}

}
