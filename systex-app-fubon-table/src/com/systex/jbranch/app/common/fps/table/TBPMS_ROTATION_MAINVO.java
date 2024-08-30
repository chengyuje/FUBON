package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;

/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPMS_ROTATION_MAINVO extends VOBase {

	private BigDecimal KEY_NO;
	private String STATEMENT_DATE;	
	private String CUST_ID;	
	private String BRANCH_NBR;	
	private String SM_BRANCH_NBR;
	private String STATEMENT_SEND_TYPE;
	private String AO_CODE;				
	private String AO_EMP_ID;			
	private String AO_BRANCH_NBR;		
	private String AO_TYPE;
	private Timestamp SEND_DATE;			
	private Timestamp RECEIVE_DATE;		
	private String RTN_STATUS_AST;	
	private String RTN_STATUS_NP;
	private Timestamp MAIL_CONTACT_DATE;
	private Timestamp MAIL_REPLY_DATE;	
	private String MAIL_REPLY_STATUS;
	private BigDecimal MAIL_KEY;	
	private String MAIL_VALID_SAMPLE;	
	private Timestamp AO_CONTACT_DATE;	
	private String AO_CONTACT_EMP_ID;
	private String RTN_BRANCH_NBR;		
	private String RTN_AO_CODE;	
	private String RTN_AO_BRANCH_NBR;	
	private String REC_SEQ;	
	private Timestamp REC_DATE;
	private String CONTACT_STATUS;
	private String CONTACT_MEMO;
	private String CONTACT_EMP_ID;	
	private String BRN_MGM_YN;
	private String PROCESS_STATUS;		
	private String RM_DIFF_YN;
	private String CUST_PROCE_STATUS;
	private String CBILL_MATCH_YN;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBPMS_ROTATION_MAIN";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBPMS_ROTATION_MAINVO(BigDecimal KEY_NO, String STATEMENT_DATE, String CUST_ID, String SM_BRANCH_NBR, String STATEMENT_SEND_TYPE,
    		String BRANCH_NBR, String AO_CODE, String AO_EMP_ID, String AO_BRANCH_NBR, 
    		String AO_TYPE, Timestamp SEND_DATE, Timestamp RECEIVE_DATE, 
    		String RTN_STATUS_AST, String RTN_STATUS_NP, Timestamp MAIL_CONTACT_DATE, Timestamp MAIL_REPLY_DATE, String MAIL_REPLY_STATUS,
    		BigDecimal MAIL_KEY, String MAIL_VALID_SAMPLE, Timestamp AO_CONTACT_DATE, String AO_CONTACT_EMP_ID, String RTN_BRANCH_NBR, String RTN_AO_CODE,
    		String RTN_AO_BRANCH_NBR, String REC_SEQ, Timestamp REC_DATE, String CONTACT_STATUS, String CONTACT_MEMO, String CONTACT_EMP_ID, String BRN_MGM_YN,  
    		String PROCESS_STATUS, String RM_DIFF_YN, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
    	this.KEY_NO				= KEY_NO;
    	this.STATEMENT_DATE		= STATEMENT_DATE;
    	this.CUST_ID			= CUST_ID;
    	this.SM_BRANCH_NBR		= SM_BRANCH_NBR;
    	this.STATEMENT_SEND_TYPE= STATEMENT_SEND_TYPE;
		this.BRANCH_NBR			= BRANCH_NBR;			
		this.AO_CODE			= AO_CODE;				
		this.AO_EMP_ID			= AO_EMP_ID;			
		this.AO_BRANCH_NBR		= AO_BRANCH_NBR;		
		this.AO_TYPE			= AO_TYPE;
		this.SEND_DATE			= SEND_DATE;			
		this.RECEIVE_DATE		= RECEIVE_DATE;		
		this.RTN_STATUS_AST		= RTN_STATUS_AST;	
		this.RTN_STATUS_NP		= RTN_STATUS_NP;
		this.MAIL_CONTACT_DATE	= MAIL_CONTACT_DATE;	
		this.MAIL_REPLY_DATE	= MAIL_REPLY_DATE;	
		this.MAIL_REPLY_STATUS	= MAIL_REPLY_STATUS;
		this.MAIL_KEY			= MAIL_KEY;		
		this.MAIL_VALID_SAMPLE	= MAIL_VALID_SAMPLE;
		this.AO_CONTACT_DATE	= AO_CONTACT_DATE;	
		this.AO_CONTACT_EMP_ID	= AO_CONTACT_EMP_ID;
		this.RTN_BRANCH_NBR		= RTN_BRANCH_NBR;		
		this.RTN_AO_CODE		= RTN_AO_CODE;	
		this.RTN_AO_BRANCH_NBR	= RTN_AO_BRANCH_NBR;
		this.REC_SEQ			= REC_SEQ;
		this.REC_DATE			= REC_DATE;
		this.CONTACT_STATUS		= CONTACT_STATUS;
		this.CONTACT_MEMO		= CONTACT_MEMO;		
		this.CONTACT_EMP_ID		= CONTACT_EMP_ID;
		this.BRN_MGM_YN			= BRN_MGM_YN;
		this.PROCESS_STATUS		= PROCESS_STATUS;
		this.RM_DIFF_YN			= RM_DIFF_YN;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBPMS_ROTATION_MAINVO() {
    }

	public BigDecimal getKEY_NO() {
		return KEY_NO;
	}

	public void setKEY_NO(BigDecimal kEY_NO) {
		KEY_NO = kEY_NO;
	}

	public String getSTATEMENT_DATE() {
		return STATEMENT_DATE;
	}

	public void setSTATEMENT_DATE(String sTATEMENT_DATE) {
		STATEMENT_DATE = sTATEMENT_DATE;
	}

	public String getCUST_ID() {
		return CUST_ID;
	}

	public void setCUST_ID(String cUST_ID) {
		CUST_ID = cUST_ID;
	}

	public String getSM_BRANCH_NBR() {
		return SM_BRANCH_NBR;
	}

	public void setSM_BRANCH_NBR(String sM_BRANCH_NBR) {
		SM_BRANCH_NBR = sM_BRANCH_NBR;
	}

	public String getSTATEMENT_SEND_TYPE() {
		return STATEMENT_SEND_TYPE;
	}

	public void setSTATEMENT_SEND_TYPE(String sTATEMENT_SEND_TYPE) {
		STATEMENT_SEND_TYPE = sTATEMENT_SEND_TYPE;
	}

	public String getBRANCH_NBR() {
		return BRANCH_NBR;
	}

	public void setBRANCH_NBR(String bRANCH_NBR) {
		BRANCH_NBR = bRANCH_NBR;
	}

	public String getAO_CODE() {
		return AO_CODE;
	}

	public void setAO_CODE(String aO_CODE) {
		AO_CODE = aO_CODE;
	}

	public String getAO_EMP_ID() {
		return AO_EMP_ID;
	}

	public void setAO_EMP_ID(String aO_EMP_ID) {
		AO_EMP_ID = aO_EMP_ID;
	}

	public String getAO_BRANCH_NBR() {
		return AO_BRANCH_NBR;
	}

	public void setAO_BRANCH_NBR(String aO_BRANCH_NBR) {
		AO_BRANCH_NBR = aO_BRANCH_NBR;
	}

	public String getAO_TYPE() {
		return AO_TYPE;
	}

	public void setAO_TYPE(String aO_TYPE) {
		AO_TYPE = aO_TYPE;
	}

	public Timestamp getSEND_DATE() {
		return SEND_DATE;
	}

	public void setSEND_DATE(Timestamp sEND_DATE) {
		SEND_DATE = sEND_DATE;
	}

	public Timestamp getRECEIVE_DATE() {
		return RECEIVE_DATE;
	}

	public void setRECEIVE_DATE(Timestamp rECEIVE_DATE) {
		RECEIVE_DATE = rECEIVE_DATE;
	}

	public String getRTN_STATUS_AST() {
		return RTN_STATUS_AST;
	}

	public void setRTN_STATUS_AST(String rTN_STATUS_AST) {
		RTN_STATUS_AST = rTN_STATUS_AST;
	}

	public String getRTN_STATUS_NP() {
		return RTN_STATUS_NP;
	}

	public void setRTN_STATUS_NP(String rTN_STATUS_NP) {
		RTN_STATUS_NP = rTN_STATUS_NP;
	}

	public Timestamp getMAIL_CONTACT_DATE() {
		return MAIL_CONTACT_DATE;
	}

	public void setMAIL_CONTACT_DATE(Timestamp mAIL_CONTACT_DATE) {
		MAIL_CONTACT_DATE = mAIL_CONTACT_DATE;
	}

	public Timestamp getMAIL_REPLY_DATE() {
		return MAIL_REPLY_DATE;
	}

	public void setMAIL_REPLY_DATE(Timestamp mAIL_REPLY_DATE) {
		MAIL_REPLY_DATE = mAIL_REPLY_DATE;
	}

	public String getMAIL_REPLY_STATUS() {
		return MAIL_REPLY_STATUS;
	}

	public void setMAIL_REPLY_STATUS(String mAIL_REPLY_STATUS) {
		MAIL_REPLY_STATUS = mAIL_REPLY_STATUS;
	}

	public BigDecimal getMAIL_KEY() {
		return MAIL_KEY;
	}

	public void setMAIL_KEY(BigDecimal mAIL_KEY) {
		MAIL_KEY = mAIL_KEY;
	}

	public Timestamp getAO_CONTACT_DATE() {
		return AO_CONTACT_DATE;
	}

	public void setAO_CONTACT_DATE(Timestamp aO_CONTACT_DATE) {
		AO_CONTACT_DATE = aO_CONTACT_DATE;
	}

	public String getRTN_BRANCH_NBR() {
		return RTN_BRANCH_NBR;
	}

	public void setRTN_BRANCH_NBR(String rTN_BRANCH_NBR) {
		RTN_BRANCH_NBR = rTN_BRANCH_NBR;
	}

	public String getRTN_AO_CODE() {
		return RTN_AO_CODE;
	}

	public void setRTN_AO_CODE(String rTN_AO_CODE) {
		RTN_AO_CODE = rTN_AO_CODE;
	}

	public String getREC_SEQ() {
		return REC_SEQ;
	}

	public void setREC_SEQ(String rEC_SEQ) {
		REC_SEQ = rEC_SEQ;
	}

	public Timestamp getREC_DATE() {
		return REC_DATE;
	}

	public void setREC_DATE(Timestamp rEC_DATE) {
		REC_DATE = rEC_DATE;
	} 

	public String getCONTACT_MEMO() {
		return CONTACT_MEMO;
	}

	public String getCONTACT_STATUS() {
		return CONTACT_STATUS;
	}

	public void setCONTACT_STATUS(String cONTACT_STATUS) {
		CONTACT_STATUS = cONTACT_STATUS;
	}

	public void setCONTACT_MEMO(String cONTACT_MEMO) {
		CONTACT_MEMO = cONTACT_MEMO;
	}

	public String getPROCESS_STATUS() {
		return PROCESS_STATUS;
	} 

	public void setPROCESS_STATUS(String pROCESS_STATUS) {
		PROCESS_STATUS = pROCESS_STATUS;
	} 

	public String getRM_DIFF_YN() {
		return RM_DIFF_YN;
	}

	public void setRM_DIFF_YN(String rM_DIFF_YN) {
		RM_DIFF_YN = rM_DIFF_YN;
	}

	public String getMAIL_VALID_SAMPLE() {
		return MAIL_VALID_SAMPLE;
	}

	public void setMAIL_VALID_SAMPLE(String mAIL_VALID_SAMPLE) {
		MAIL_VALID_SAMPLE = mAIL_VALID_SAMPLE;
	}

	public String getAO_CONTACT_EMP_ID() {
		return AO_CONTACT_EMP_ID;
	}

	public void setAO_CONTACT_EMP_ID(String aO_CONTACT_EMP_ID) {
		AO_CONTACT_EMP_ID = aO_CONTACT_EMP_ID;
	}

	public String getRTN_AO_BRANCH_NBR() {
		return RTN_AO_BRANCH_NBR;
	}

	public void setRTN_AO_BRANCH_NBR(String rTN_AO_BRANCH_NBR) {
		RTN_AO_BRANCH_NBR = rTN_AO_BRANCH_NBR;
	}

	public String getCONTACT_EMP_ID() {
		return CONTACT_EMP_ID;
	}

	public void setCONTACT_EMP_ID(String cONTACT_EMP_ID) {
		CONTACT_EMP_ID = cONTACT_EMP_ID;
	}

	public String getBRN_MGM_YN() {
		return BRN_MGM_YN;
	}

	public void setBRN_MGM_YN(String bRN_MGM_YN) {
		BRN_MGM_YN = bRN_MGM_YN;
	}

	public void checkDefaultValue() {
    
	}

	public String getCUST_PROCE_STATUS() {
		return CUST_PROCE_STATUS;
	}

	public void setCUST_PROCE_STATUS(String cUST_PROCE_STATUS) {
		CUST_PROCE_STATUS = cUST_PROCE_STATUS;
	}

	public String getCBILL_MATCH_YN() {
		return CBILL_MATCH_YN;
	}

	public void setCBILL_MATCH_YN(String cBILL_MATCH_YN) {
		CBILL_MATCH_YN = cBILL_MATCH_YN;
	}
	
}
