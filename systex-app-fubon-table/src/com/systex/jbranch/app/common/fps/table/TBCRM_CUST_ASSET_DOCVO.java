package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBCRM_CUST_ASSET_DOCVO extends VOBase {

    /** identifier field */
    private BigDecimal SEQ;

    /** persistent field */
    private String CUST_ID;

    /** nullable persistent field */
    private Timestamp APPLY_DATE;

    /** nullable persistent field */
    private String APPLY_TYPE;

    /** nullable persistent field */
    private String PROCESS_STATUS;

    /** nullable persistent field */
    private String PRINT_ALL;

    /** nullable persistent field */
    private String PRINT_SAV;

    /** nullable persistent field */
    private String PRINT_INV;

    /** nullable persistent field */
    private String PRINT_INS;

    /** nullable persistent field */
    private String PRINT_LOAN;

    /** nullable persistent field */
    private String PRINT_AUM;

    /** nullable persistent field */
    private String PRINT_IIL;

    /** nullable persistent field */
    private String PRINT_CUR;

    /** nullable persistent field */
    private String PRINT_TYPE;

    /** nullable persistent field */
    private String PRINT_FUND_MKT;

    /** nullable persistent field */
    private String PRINT_PORTFOLIO;

    /** nullable persistent field */
    private String PRINT_BRA_NBR;

    /** nullable persistent field */
    private String EMP_ID;

    /** nullable persistent field */
    private String EMP_NAME;

    /** nullable persistent field */
    private String AO_CODE;

    /** nullable persistent field */
    private String AUTH_ID;

    /** nullable persistent field */
    private String AUTH_NAME;

    /** nullable persistent field */
    private String PRINT_STATUS;

	/** nullable persistent field */
    private Timestamp PRINT_DATE;

    private String PRINT_ALL_CHART;
    
    private String IS_FIRST;
    
    /** nullable persistent field */
    private String FUND_SORT_TYPE;
    
public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBCRM_CUST_ASSET_DOC";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBCRM_CUST_ASSET_DOCVO(BigDecimal SEQ, String CUST_ID, Timestamp APPLY_DATE, String APPLY_TYPE, String PROCESS_STATUS, String PRINT_ALL, String PRINT_SAV, String PRINT_INV, String PRINT_INS, String PRINT_LOAN, String PRINT_AUM, String PRINT_IIL, String PRINT_CUR, String PRINT_TYPE, String PRINT_FUND_MKT, String PRINT_PORTFOLIO, String PRINT_BRA_NBR, String EMP_ID, String EMP_NAME, String AO_CODE, String AUTH_ID, String AUTH_NAME, String PRINT_STATUS, Timestamp PRINT_DATE, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version , String PRINT_ALL_CHART,String IS_FIRST, String FUND_SORT_TYPE) {
        this.SEQ = SEQ;
        this.CUST_ID = CUST_ID;
        this.APPLY_DATE = APPLY_DATE;
        this.APPLY_TYPE = APPLY_TYPE;
        this.PROCESS_STATUS = PROCESS_STATUS;
        this.PRINT_ALL = PRINT_ALL;
        this.PRINT_SAV = PRINT_SAV;
        this.PRINT_INV = PRINT_INV;
        this.PRINT_INS = PRINT_INS;
        this.PRINT_LOAN = PRINT_LOAN;
        this.PRINT_AUM = PRINT_AUM;
        this.PRINT_IIL = PRINT_IIL;
        this.PRINT_CUR = PRINT_CUR;
        this.PRINT_TYPE = PRINT_TYPE;
        this.PRINT_FUND_MKT = PRINT_FUND_MKT;
        this.PRINT_PORTFOLIO = PRINT_PORTFOLIO;
        this.PRINT_BRA_NBR = PRINT_BRA_NBR;
        this.EMP_ID = EMP_ID;
        this.EMP_NAME = EMP_NAME;
        this.AO_CODE = AO_CODE;
        this.AUTH_ID = AUTH_ID;
        this.AUTH_NAME = AUTH_NAME;
        this.PRINT_STATUS = PRINT_STATUS;
        this.PRINT_DATE = PRINT_DATE;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
        this.PRINT_ALL_CHART = PRINT_ALL_CHART;
        this.IS_FIRST = IS_FIRST;
        this.FUND_SORT_TYPE = FUND_SORT_TYPE;       
    }

    /** default constructor */
    public TBCRM_CUST_ASSET_DOCVO() {
    }

    /** minimal constructor */
    public TBCRM_CUST_ASSET_DOCVO(BigDecimal SEQ, String CUST_ID) {
        this.SEQ = SEQ;
        this.CUST_ID = CUST_ID;
    }

    public BigDecimal getSEQ() {
        return this.SEQ;
    }

    public void setSEQ(BigDecimal SEQ) {
        this.SEQ = SEQ;
    }

    public String getCUST_ID() {
        return this.CUST_ID;
    }

    public void setCUST_ID(String CUST_ID) {
        this.CUST_ID = CUST_ID;
    }

    public Timestamp getAPPLY_DATE() {
        return this.APPLY_DATE;
    }

    public void setAPPLY_DATE(Timestamp APPLY_DATE) {
        this.APPLY_DATE = APPLY_DATE;
    }

    public String getAPPLY_TYPE() {
        return this.APPLY_TYPE;
    }

    public void setAPPLY_TYPE(String APPLY_TYPE) {
        this.APPLY_TYPE = APPLY_TYPE;
    }

    public String getPROCESS_STATUS() {
        return this.PROCESS_STATUS;
    }

    public void setPROCESS_STATUS(String PROCESS_STATUS) {
        this.PROCESS_STATUS = PROCESS_STATUS;
    }

    public String getPRINT_ALL() {
        return this.PRINT_ALL;
    }
    
    public void setPRINT_ALL(String PRINT_ALL) {
        this.PRINT_ALL = PRINT_ALL;
    }
    public String getPRINT_ALL_CHART() {
        return this.PRINT_ALL_CHART;
    }

    public void setPRINT_ALL_CHART(String PRINT_ALL_CHART) {
        this.PRINT_ALL_CHART = PRINT_ALL_CHART;
    }

    public String getPRINT_SAV() {
        return this.PRINT_SAV;
    }

    public void setPRINT_SAV(String PRINT_SAV) {
        this.PRINT_SAV = PRINT_SAV;
    }

    public String getPRINT_INV() {
        return this.PRINT_INV;
    }

    public void setPRINT_INV(String PRINT_INV) {
        this.PRINT_INV = PRINT_INV;
    }

    public String getPRINT_INS() {
        return this.PRINT_INS;
    }

    public void setPRINT_INS(String PRINT_INS) {
        this.PRINT_INS = PRINT_INS;
    }

    public String getPRINT_LOAN() {
        return this.PRINT_LOAN;
    }

    public void setPRINT_LOAN(String PRINT_LOAN) {
        this.PRINT_LOAN = PRINT_LOAN;
    }

    public String getPRINT_AUM() {
        return this.PRINT_AUM;
    }

    public void setPRINT_AUM(String PRINT_AUM) {
        this.PRINT_AUM = PRINT_AUM;
    }

    public String getPRINT_IIL() {
        return this.PRINT_IIL;
    }

    public void setPRINT_IIL(String PRINT_IIL) {
        this.PRINT_IIL = PRINT_IIL;
    }

    public String getPRINT_CUR() {
        return this.PRINT_CUR;
    }

    public void setPRINT_CUR(String PRINT_CUR) {
        this.PRINT_CUR = PRINT_CUR;
    }

    public String getPRINT_TYPE() {
        return this.PRINT_TYPE;
    }

    public void setPRINT_TYPE(String PRINT_TYPE) {
        this.PRINT_TYPE = PRINT_TYPE;
    }

    public String getPRINT_FUND_MKT() {
        return this.PRINT_FUND_MKT;
    }

    public void setPRINT_FUND_MKT(String PRINT_FUND_MKT) {
        this.PRINT_FUND_MKT = PRINT_FUND_MKT;
    }

    public String getPRINT_PORTFOLIO() {
        return this.PRINT_PORTFOLIO;
    }

    public void setPRINT_PORTFOLIO(String PRINT_PORTFOLIO) {
        this.PRINT_PORTFOLIO = PRINT_PORTFOLIO;
    }

    public String getPRINT_BRA_NBR() {
        return this.PRINT_BRA_NBR;
    }

    public void setPRINT_BRA_NBR(String PRINT_BRA_NBR) {
        this.PRINT_BRA_NBR = PRINT_BRA_NBR;
    }

    public String getEMP_ID() {
        return this.EMP_ID;
    }

    public void setEMP_ID(String EMP_ID) {
        this.EMP_ID = EMP_ID;
    }

    public String getEMP_NAME() {
        return this.EMP_NAME;
    }

    public void setEMP_NAME(String EMP_NAME) {
        this.EMP_NAME = EMP_NAME;
    }

    public String getAO_CODE() {
        return this.AO_CODE;
    }

    public void setAO_CODE(String AO_CODE) {
        this.AO_CODE = AO_CODE;
    }

    public String getAUTH_ID() {
        return this.AUTH_ID;
    }

    public void setAUTH_ID(String AUTH_ID) {
        this.AUTH_ID = AUTH_ID;
    }

    public String getAUTH_NAME() {
        return this.AUTH_NAME;
    }

    public void setAUTH_NAME(String AUTH_NAME) {
        this.AUTH_NAME = AUTH_NAME;
    }

    public String getPRINT_STATUS() {
        return this.PRINT_STATUS;
    }

    public void setPRINT_STATUS(String PRINT_STATUS) {
        this.PRINT_STATUS = PRINT_STATUS;
    }

    public Timestamp getPRINT_DATE() {
        return this.PRINT_DATE;
    }

    public void setPRINT_DATE(Timestamp PRINT_DATE) {
        this.PRINT_DATE = PRINT_DATE;
    }

    public String getIS_FIRST() {
		return IS_FIRST;
	}

	public void setIS_FIRST(String IS_FIRST) {
		this.IS_FIRST = IS_FIRST;
	}

    public String getFUND_SORT_TYPE() {
		return FUND_SORT_TYPE;
	}

	public void setFUND_SORT_TYPE(String fUND_SORT_TYPE) {
		FUND_SORT_TYPE = fUND_SORT_TYPE;
	}

	public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("SEQ", getSEQ())
            .toString();
    }

}
