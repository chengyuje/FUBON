package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBKYC_INVESTOREXAM_M_HISVO extends VOBase {

    /** identifier field */
    private String SEQ;

    /** nullable persistent field */
    private String EXAM_VERSION;

    /** nullable persistent field */
    private String CUST_ID;

    /** nullable persistent field */
    private String CUST_NAME;

    /** nullable persistent field */
    private String AO_CODE;

    /** nullable persistent field */
    private String CUST_BRANCH_NBR;

    /** nullable persistent field */
    private String INVEST_BRANCH_NBR;

    /** nullable persistent field */
    private String INVEST_SOURCE;

    /** nullable persistent field */
    private String INVEST_IP;

    /** nullable persistent field */
    private Timestamp TEST_BEF_DATE;

    /** nullable persistent field */
    private String CUST_RISK_BEF;

    /** nullable persistent field */
    private String CUST_RISK_AFR;

    /** nullable persistent field */
    private BigDecimal RISKRANGE;

    /** nullable persistent field */
    private BigDecimal DIFF_DAYS;

    /** nullable persistent field */
    private String EMP_ID;

    /** nullable persistent field */
    private String OUT_ACCESS;

    /** nullable persistent field */
    private String STATUS;

    /** nullable persistent field */
    private String SIGNOFF_ID;

    /** nullable persistent field */
    private Timestamp SIGNOFF_DATE;

    /** nullable persistent field */
    private Timestamp CREATE_DATE;

    /** nullable persistent field */
    private Timestamp EXPIRY_DATE;

    /** nullable persistent field */
    private BigDecimal REC_TYPE;

    /** nullable persistent field */
    private String CUST_RISK_MDF;

    /** nullable persistent field */
    private String CHECKED_APPR_ID;

    /** nullable persistent field */
    private Timestamp CHECKED_APPR_DATE;

    /** nullable persistent field */
    private String CIB_FLAG;
    
    private String DEL_TYPE;

    /** nullable persistent field */
    private String REC_SEQ;
    
    /** nullable persistent field */
    private String CRR_ORI;
    
    /** nullable persistent field */
    private String CRR_MATRIX;
    
    /** nullable persistent field */
    private BigDecimal SCORE_C;
    
    /** nullable persistent field */
    private BigDecimal SCORE_W;
    
    /** nullable persistent field */
    private BigDecimal SCORE_CW_TOT;
    
    /** nullable persistent field */
    private BigDecimal RISK_LOSS_RATE;
    
    /** nullable persistent field */
    private String RISK_LOSS_LEVEL;
    
    /** nullable persistent field */
    private BigDecimal SCORE_ORI_TOT;

    public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBKYC_INVESTOREXAM_M_HIS";


	public String getTableuid () {
	    return TABLE_UID;
	}

	public TBKYC_INVESTOREXAM_M_HISVO(String SEQ, String EXAM_VERSION, String CUST_ID, String CUST_NAME, String AO_CODE, String CUST_BRANCH_NBR, String INVEST_BRANCH_NBR, String INVEST_SOURCE, String INVEST_IP, Timestamp TEST_BEF_DATE, String CUST_RISK_BEF, String CUST_RISK_AFR, BigDecimal RISKRANGE, BigDecimal DIFF_DAYS, String EMP_ID, String OUT_ACCESS, String STATUS, String SIGNOFF_ID, Timestamp SIGNOFF_DATE, Timestamp CREATE_DATE, Timestamp EXPIRY_DATE, BigDecimal REC_TYPE, Timestamp createtime, String creator, Timestamp lastupdate, String modifier, String CUST_RISK_MDF, String CHECKED_APPR_ID, Timestamp CHECKED_APPR_DATE, String CIB_FLAG, Long version , String DEL_TYPE,
			String REC_SEQ, String CRR_ORI, String CRR_MATRIX, BigDecimal SCORE_C, BigDecimal SCORE_W, BigDecimal SCORE_CW_TOT, BigDecimal RISK_LOSS_RATE, String RISK_LOSS_LEVEL, BigDecimal SCORE_ORI_TOT){
		this(SEQ, EXAM_VERSION, CUST_ID, CUST_NAME, AO_CODE, CUST_BRANCH_NBR, INVEST_BRANCH_NBR, INVEST_SOURCE, INVEST_IP, TEST_BEF_DATE, CUST_RISK_BEF, CUST_RISK_AFR, RISKRANGE, DIFF_DAYS, EMP_ID, OUT_ACCESS, STATUS, SIGNOFF_ID, SIGNOFF_DATE, CREATE_DATE, EXPIRY_DATE, REC_TYPE, createtime, creator, lastupdate, modifier, CUST_RISK_MDF, CHECKED_APPR_ID, CHECKED_APPR_DATE, CIB_FLAG, version);
		this.DEL_TYPE = DEL_TYPE;
		this.DEL_TYPE = DEL_TYPE;
		this.REC_SEQ = REC_SEQ;
		this.CRR_ORI = CRR_ORI;
		this.CRR_MATRIX = CRR_MATRIX;
		this.SCORE_C = SCORE_C;
		this.SCORE_W = SCORE_W;
		this.SCORE_CW_TOT = SCORE_CW_TOT;
		this.RISK_LOSS_RATE = RISK_LOSS_RATE;
		this.RISK_LOSS_LEVEL = RISK_LOSS_LEVEL;
		this.SCORE_ORI_TOT = SCORE_ORI_TOT;
	}
	
    /** full constructor */
    public TBKYC_INVESTOREXAM_M_HISVO(String SEQ, String EXAM_VERSION, String CUST_ID, String CUST_NAME, String AO_CODE, String CUST_BRANCH_NBR, String INVEST_BRANCH_NBR, String INVEST_SOURCE, String INVEST_IP, Timestamp TEST_BEF_DATE, String CUST_RISK_BEF, String CUST_RISK_AFR, BigDecimal RISKRANGE, BigDecimal DIFF_DAYS, String EMP_ID, String OUT_ACCESS, String STATUS, String SIGNOFF_ID, Timestamp SIGNOFF_DATE, Timestamp CREATE_DATE, Timestamp EXPIRY_DATE, BigDecimal REC_TYPE, Timestamp createtime, String creator, Timestamp lastupdate, String modifier, String CUST_RISK_MDF, String CHECKED_APPR_ID, Timestamp CHECKED_APPR_DATE, String CIB_FLAG, Long version) {
        this.SEQ = SEQ;
        this.EXAM_VERSION = EXAM_VERSION;
        this.CUST_ID = CUST_ID;
        this.CUST_NAME = CUST_NAME;
        this.AO_CODE = AO_CODE;
        this.CUST_BRANCH_NBR = CUST_BRANCH_NBR;
        this.INVEST_BRANCH_NBR = INVEST_BRANCH_NBR;
        this.INVEST_SOURCE = INVEST_SOURCE;
        this.INVEST_IP = INVEST_IP;
        this.TEST_BEF_DATE = TEST_BEF_DATE;
        this.CUST_RISK_BEF = CUST_RISK_BEF;
        this.CUST_RISK_AFR = CUST_RISK_AFR;
        this.RISKRANGE = RISKRANGE;
        this.DIFF_DAYS = DIFF_DAYS;
        this.EMP_ID = EMP_ID;
        this.OUT_ACCESS = OUT_ACCESS;
        this.STATUS = STATUS;
        this.SIGNOFF_ID = SIGNOFF_ID;
        this.SIGNOFF_DATE = SIGNOFF_DATE;
        this.CREATE_DATE = CREATE_DATE;
        this.EXPIRY_DATE = EXPIRY_DATE;
        this.REC_TYPE = REC_TYPE;
        this.createtime = createtime;
        this.creator = creator;
        this.lastupdate = lastupdate;
        this.modifier = modifier;
        this.CUST_RISK_MDF = CUST_RISK_MDF;
        this.CHECKED_APPR_ID = CHECKED_APPR_ID;
        this.CHECKED_APPR_DATE = CHECKED_APPR_DATE;
        this.CIB_FLAG = CIB_FLAG;
        this.version = version;
    }

    /** default constructor */
    public TBKYC_INVESTOREXAM_M_HISVO() {
    }

    /** minimal constructor */
    public TBKYC_INVESTOREXAM_M_HISVO(String SEQ) {
        this.SEQ = SEQ;
    }

    public String getSEQ() {
        return this.SEQ;
    }

    public void setSEQ(String SEQ) {
        this.SEQ = SEQ;
    }

    public String getEXAM_VERSION() {
        return this.EXAM_VERSION;
    }

    public void setEXAM_VERSION(String EXAM_VERSION) {
        this.EXAM_VERSION = EXAM_VERSION;
    }

    public String getCUST_ID() {
        return this.CUST_ID;
    }

    public void setCUST_ID(String CUST_ID) {
        this.CUST_ID = CUST_ID;
    }

    public String getCUST_NAME() {
        return this.CUST_NAME;
    }

    public void setCUST_NAME(String CUST_NAME) {
        this.CUST_NAME = CUST_NAME;
    }

    public String getAO_CODE() {
        return this.AO_CODE;
    }

    public void setAO_CODE(String AO_CODE) {
        this.AO_CODE = AO_CODE;
    }

    public String getCUST_BRANCH_NBR() {
        return this.CUST_BRANCH_NBR;
    }

    public void setCUST_BRANCH_NBR(String CUST_BRANCH_NBR) {
        this.CUST_BRANCH_NBR = CUST_BRANCH_NBR;
    }

    public String getINVEST_BRANCH_NBR() {
        return this.INVEST_BRANCH_NBR;
    }

    public void setINVEST_BRANCH_NBR(String INVEST_BRANCH_NBR) {
        this.INVEST_BRANCH_NBR = INVEST_BRANCH_NBR;
    }

    public String getINVEST_SOURCE() {
        return this.INVEST_SOURCE;
    }

    public void setINVEST_SOURCE(String INVEST_SOURCE) {
        this.INVEST_SOURCE = INVEST_SOURCE;
    }

    public String getINVEST_IP() {
        return this.INVEST_IP;
    }

    public void setINVEST_IP(String INVEST_IP) {
        this.INVEST_IP = INVEST_IP;
    }

    public Timestamp getTEST_BEF_DATE() {
        return this.TEST_BEF_DATE;
    }

    public void setTEST_BEF_DATE(Timestamp TEST_BEF_DATE) {
        this.TEST_BEF_DATE = TEST_BEF_DATE;
    }

    public String getCUST_RISK_BEF() {
        return this.CUST_RISK_BEF;
    }

    public void setCUST_RISK_BEF(String CUST_RISK_BEF) {
        this.CUST_RISK_BEF = CUST_RISK_BEF;
    }

    public String getCUST_RISK_AFR() {
        return this.CUST_RISK_AFR;
    }

    public void setCUST_RISK_AFR(String CUST_RISK_AFR) {
        this.CUST_RISK_AFR = CUST_RISK_AFR;
    }

    public BigDecimal getRISKRANGE() {
        return this.RISKRANGE;
    }

    public void setRISKRANGE(BigDecimal RISKRANGE) {
        this.RISKRANGE = RISKRANGE;
    }

    public BigDecimal getDIFF_DAYS() {
        return this.DIFF_DAYS;
    }

    public void setDIFF_DAYS(BigDecimal DIFF_DAYS) {
        this.DIFF_DAYS = DIFF_DAYS;
    }

    public String getEMP_ID() {
        return this.EMP_ID;
    }

    public void setEMP_ID(String EMP_ID) {
        this.EMP_ID = EMP_ID;
    }

    public String getOUT_ACCESS() {
        return this.OUT_ACCESS;
    }

    public void setOUT_ACCESS(String OUT_ACCESS) {
        this.OUT_ACCESS = OUT_ACCESS;
    }

    public String getSTATUS() {
        return this.STATUS;
    }

    public void setSTATUS(String STATUS) {
        this.STATUS = STATUS;
    }

    public String getSIGNOFF_ID() {
        return this.SIGNOFF_ID;
    }

    public void setSIGNOFF_ID(String SIGNOFF_ID) {
        this.SIGNOFF_ID = SIGNOFF_ID;
    }

    public Timestamp getSIGNOFF_DATE() {
        return this.SIGNOFF_DATE;
    }

    public void setSIGNOFF_DATE(Timestamp SIGNOFF_DATE) {
        this.SIGNOFF_DATE = SIGNOFF_DATE;
    }

    public Timestamp getCREATE_DATE() {
        return this.CREATE_DATE;
    }

    public void setCREATE_DATE(Timestamp CREATE_DATE) {
        this.CREATE_DATE = CREATE_DATE;
    }

    public Timestamp getEXPIRY_DATE() {
        return this.EXPIRY_DATE;
    }

    public void setEXPIRY_DATE(Timestamp EXPIRY_DATE) {
        this.EXPIRY_DATE = EXPIRY_DATE;
    }

    public BigDecimal getREC_TYPE() {
        return this.REC_TYPE;
    }

    public void setREC_TYPE(BigDecimal REC_TYPE) {
        this.REC_TYPE = REC_TYPE;
    }

    public String getCUST_RISK_MDF() {
        return this.CUST_RISK_MDF;
    }

    public void setCUST_RISK_MDF(String CUST_RISK_MDF) {
        this.CUST_RISK_MDF = CUST_RISK_MDF;
    }

    public String getCHECKED_APPR_ID() {
        return this.CHECKED_APPR_ID;
    }

    public void setCHECKED_APPR_ID(String CHECKED_APPR_ID) {
        this.CHECKED_APPR_ID = CHECKED_APPR_ID;
    }

    public Timestamp getCHECKED_APPR_DATE() {
        return this.CHECKED_APPR_DATE;
    }

    public void setCHECKED_APPR_DATE(Timestamp CHECKED_APPR_DATE) {
        this.CHECKED_APPR_DATE = CHECKED_APPR_DATE;
    }

    public String getCIB_FLAG() {
        return this.CIB_FLAG;
    }

    public void setCIB_FLAG(String CIB_FLAG) {
        this.CIB_FLAG = CIB_FLAG;
    }

    public String getDEL_TYPE() {
		return DEL_TYPE;
	}

	public void setDEL_TYPE(String dEL_TYPE) {
		DEL_TYPE = dEL_TYPE;
	}

	public String getREC_SEQ() {
		return REC_SEQ;
	}

	public void setREC_SEQ(String rEC_SEQ) {
		REC_SEQ = rEC_SEQ;
	}

	public String getCRR_ORI() {
		return CRR_ORI;
	}

	public void setCRR_ORI(String cRR_ORI) {
		CRR_ORI = cRR_ORI;
	}

	public String getCRR_MATRIX() {
		return CRR_MATRIX;
	}

	public void setCRR_MATRIX(String cRR_MATRIX) {
		CRR_MATRIX = cRR_MATRIX;
	}

	public BigDecimal getSCORE_C() {
		return SCORE_C;
	}

	public void setSCORE_C(BigDecimal sCORE_C) {
		SCORE_C = sCORE_C;
	}

	public BigDecimal getSCORE_W() {
		return SCORE_W;
	}

	public void setSCORE_W(BigDecimal sCORE_W) {
		SCORE_W = sCORE_W;
	}

	public BigDecimal getSCORE_CW_TOT() {
		return SCORE_CW_TOT;
	}

	public void setSCORE_CW_TOT(BigDecimal sCORE_CW_TOT) {
		SCORE_CW_TOT = sCORE_CW_TOT;
	}

	public BigDecimal getRISK_LOSS_RATE() {
		return RISK_LOSS_RATE;
	}

	public void setRISK_LOSS_RATE(BigDecimal rISK_LOSS_RATE) {
		RISK_LOSS_RATE = rISK_LOSS_RATE;
	}

	public String getRISK_LOSS_LEVEL() {
		return RISK_LOSS_LEVEL;
	}

	public void setRISK_LOSS_LEVEL(String rISK_LOSS_LEVEL) {
		RISK_LOSS_LEVEL = rISK_LOSS_LEVEL;
	}

	public BigDecimal getSCORE_ORI_TOT() {
		return SCORE_ORI_TOT;
	}

	public void setSCORE_ORI_TOT(BigDecimal sCORE_ORI_TOT) {
		SCORE_ORI_TOT = sCORE_ORI_TOT;
	}

	public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("SEQ", getSEQ())
            .toString();
    }

}
