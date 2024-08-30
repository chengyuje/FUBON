package com.systex.jbranch.app.common.fps.table;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;

public class TBIOT_ADDRTELMAIL_CHKVO extends VOBase {

	/** identifier field */
	private String SEQ;

	/** nullable persistent field */
	private String YYYYMM;

	/** nullable persistent field */
	private String POLICY_NO;

	/** nullable persistent field */
	private String CUST_ID;

	/** nullable persistent field */
	private String BRANCH_ID;

	/** nullable persistent field */
	private String EMP_ID;

	/** nullable persistent field */
	private String EMP_CUST_ID;

	/** nullable persistent field */
	private String EMP_NAME;

	/** nullable persistent field */
	private String SOURCE_TYPE;

	/** nullable persistent field */
	private String CHK_TYPE;

	/** nullable persistent field */
	private String MATCH_YN;

	/** nullable persistent field */
	private String SOURCE_CONTENT;

	/** nullable persistent field */
	private String CHECKED_RESULT;

	public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBIOT_ADDRTELMAIL_CHKVO";

	public String getTableuid() {
		return TABLE_UID;
	}

	/** default constructor */
	public TBIOT_ADDRTELMAIL_CHKVO() {

	}

	/** minimal constructor */
	public TBIOT_ADDRTELMAIL_CHKVO(String SEQ) {
		this.SEQ = SEQ;
	}

	/** full constructor */
	public TBIOT_ADDRTELMAIL_CHKVO(String SEQ, String YYYYMM, String POLICY_NO, String CUST_ID, String BRANCH_ID,
			String EMP_ID, String EMP_CUST_ID, String EMP_NAME, String SOURCE_TYPE, String CHK_TYPE, String MATCH_YN,
			String SOURCE_CONTENT, String CHECKED_RESULT) {
		super();
		this.SEQ = SEQ;
		this.YYYYMM = YYYYMM;
		this.POLICY_NO = POLICY_NO;
		this.CUST_ID = CUST_ID;
		this.BRANCH_ID = BRANCH_ID;
		this.EMP_ID = EMP_ID;
		this.EMP_CUST_ID = EMP_CUST_ID;
		this.EMP_NAME = EMP_NAME;
		this.SOURCE_TYPE = SOURCE_TYPE;
		this.CHK_TYPE = CHK_TYPE;
		this.MATCH_YN = MATCH_YN;
		this.SOURCE_CONTENT = SOURCE_CONTENT;
		this.CHECKED_RESULT = CHECKED_RESULT;
	}

	public String getSEQ() {
		return SEQ;
	}

	public void setSEQ(String SEQ) {
		this.SEQ = SEQ;
	}

	public String getYYYYMM() {
		return YYYYMM;
	}

	public void setYYYYMM(String YYYYMM) {
		this.YYYYMM = YYYYMM;
	}

	public String getPOLICY_NO() {
		return POLICY_NO;
	}

	public void setPOLICY_NO(String POLICY_NO) {
		this.POLICY_NO = POLICY_NO;
	}

	public String getCUST_ID() {
		return CUST_ID;
	}

	public void setCUST_ID(String CUST_ID) {
		this.CUST_ID = CUST_ID;
	}

	public String getBRANCH_ID() {
		return BRANCH_ID;
	}

	public void setBRANCH_ID(String BRANCH_ID) {
		this.BRANCH_ID = BRANCH_ID;
	}

	public String getEMP_ID() {
		return EMP_ID;
	}

	public void setEMP_ID(String EMP_ID) {
		this.EMP_ID = EMP_ID;
	}

	public String getEMP_CUST_ID() {
		return EMP_CUST_ID;
	}

	public void setEMP_CUST_ID(String EMP_CUST_ID) {
		this.EMP_CUST_ID = EMP_CUST_ID;
	}

	public String getEMP_NAME() {
		return EMP_NAME;
	}

	public void setEMP_NAME(String EMP_NAME) {
		this.EMP_NAME = EMP_NAME;
	}

	public String getSOURCE_TYPE() {
		return SOURCE_TYPE;
	}

	public void setSOURCE_TYPE(String SOURCE_TYPE) {
		this.SOURCE_TYPE = SOURCE_TYPE;
	}

	public String getCHK_TYPE() {
		return CHK_TYPE;
	}

	public void setCHK_TYPE(String CHK_TYPE) {
		this.CHK_TYPE = CHK_TYPE;
	}

	public String getMATCH_YN() {
		return MATCH_YN;
	}

	public void setMATCH_YN(String MATCH_YN) {
		this.MATCH_YN = MATCH_YN;
	}

	public String getSOURCE_CONTENT() {
		return SOURCE_CONTENT;
	}

	public void setSOURCE_CONTENT(String SOURCE_CONTENT) {
		this.SOURCE_CONTENT = SOURCE_CONTENT;
	}

	public String getCHECKED_RESULT() {
		return CHECKED_RESULT;
	}

	public void setCHECKED_RESULT(String CHECKED_RESULT) {
		this.CHECKED_RESULT = CHECKED_RESULT;
	}
	
	public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("SEQ", getSEQ())
            .toString();
    }
}
