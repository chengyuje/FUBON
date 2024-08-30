package com.systex.jbranch.app.common.fps.table;

import java.sql.Timestamp;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBKYC_INVESTOREXAM_D_HISVO extends VOBase {

    /** identifier field */
    private String SEQ;

    /** nullable persistent field */
    private String CUST_ID;

    /** nullable persistent field */
    private String CUST_EDUCTION_BEFORE;

    /** nullable persistent field */
    private String CUST_CAREER_BEFORE;

    /** nullable persistent field */
    private String CUST_MARRIAGE_BEFORE;

    /** nullable persistent field */
    private String CUST_CHILDREN_BEFORE;

    /** nullable persistent field */
    private String CUST_HEALTH_BEFORE;

    /** nullable persistent field */
    private String CUST_EDUCTION_AFTER;

    /** nullable persistent field */
    private String CUST_CAREER_AFTER;

    /** nullable persistent field */
    private String CUST_MARRIAGE_AFTER;

    /** nullable persistent field */
    private String CUST_CHILDREN_AFTER;

    /** nullable persistent field */
    private String CUST_HEALTH_AFTER;

    /** nullable persistent field */
    private String ANSWER_2;

    /** nullable persistent field */
    private String CUST_TEL;

    /** nullable persistent field */
    private String CUST_EMAIL;

    /** nullable persistent field */
    private String CUST_ADDRESS;

    /** nullable persistent field */
    private String UPDATE_YN;

    /** nullable persistent field */
    private String CUST_DEGRADE;

    /** nullable persistent field */
    private String CUST_SCHOOL;

    /** nullable persistent field */
    private String CUST_EDU_CHANGE;

    /** nullable persistent field */
    private String CUST_HEALTH_CHANGE;

    /** nullable persistent field */
    private Timestamp DEGRADE_DUE_DATE;
    
    /** nullable persistent field */
    private Timestamp PRO_DUE_DATE;
    
    /** nullable persistent field */
    private String CUST_EMAIL_BEFORE;
    
    /** nullable persistent field */
    private String SAMEEMAIL_REASON;
    
    /** nullable persistent field */
    private String SAMEEMAIL_CHOOSE;

    /** nullable persistent field */
    private Timestamp HNWC_DUE_DATE;
    
    /** nullable persistent field */
    private Timestamp HNWC_INVALID_DATE;
    
public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBKYC_INVESTOREXAM_D_HIS";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBKYC_INVESTOREXAM_D_HISVO(String SEQ, String CUST_ID, String CUST_EDUCTION_BEFORE, String CUST_CAREER_BEFORE, String CUST_MARRIAGE_BEFORE, String CUST_CHILDREN_BEFORE, String CUST_HEALTH_BEFORE, String CUST_EDUCTION_AFTER, String CUST_CAREER_AFTER, String CUST_MARRIAGE_AFTER, String CUST_CHILDREN_AFTER, String CUST_HEALTH_AFTER, String ANSWER_2, Timestamp createtime, String creator, Timestamp lastupdate, String modifier, String CUST_TEL, String CUST_EMAIL, String CUST_ADDRESS, String UPDATE_YN, String CUST_DEGRADE, String CUST_SCHOOL, String CUST_EDU_CHANGE, String CUST_HEALTH_CHANGE, Long version, String CUST_EMAIL_BEFORE, String SAMEEMAIL_REASON, String SAMEEMAIL_CHOOSE) {
        this.SEQ = SEQ;
        this.CUST_ID = CUST_ID;
        this.CUST_EDUCTION_BEFORE = CUST_EDUCTION_BEFORE;
        this.CUST_CAREER_BEFORE = CUST_CAREER_BEFORE;
        this.CUST_MARRIAGE_BEFORE = CUST_MARRIAGE_BEFORE;
        this.CUST_CHILDREN_BEFORE = CUST_CHILDREN_BEFORE;
        this.CUST_HEALTH_BEFORE = CUST_HEALTH_BEFORE;
        this.CUST_EDUCTION_AFTER = CUST_EDUCTION_AFTER;
        this.CUST_CAREER_AFTER = CUST_CAREER_AFTER;
        this.CUST_MARRIAGE_AFTER = CUST_MARRIAGE_AFTER;
        this.CUST_CHILDREN_AFTER = CUST_CHILDREN_AFTER;
        this.CUST_HEALTH_AFTER = CUST_HEALTH_AFTER;
        this.ANSWER_2 = ANSWER_2;
        this.createtime = createtime;
        this.creator = creator;
        this.lastupdate = lastupdate;
        this.modifier = modifier;
        this.CUST_TEL = CUST_TEL;
        this.CUST_EMAIL = CUST_EMAIL;
        this.CUST_ADDRESS = CUST_ADDRESS;
        this.UPDATE_YN = UPDATE_YN;
        this.CUST_DEGRADE = CUST_DEGRADE;
        this.CUST_SCHOOL = CUST_SCHOOL;
        this.CUST_EDU_CHANGE = CUST_EDU_CHANGE;
        this.CUST_HEALTH_CHANGE = CUST_HEALTH_CHANGE;
        this.version = version;
        this.CUST_EMAIL_BEFORE = CUST_EMAIL_BEFORE;
        this.SAMEEMAIL_REASON = SAMEEMAIL_REASON;
        this.SAMEEMAIL_CHOOSE = SAMEEMAIL_CHOOSE;
    }

    /** default constructor */
    public TBKYC_INVESTOREXAM_D_HISVO() {
    }

    /** minimal constructor */
    public TBKYC_INVESTOREXAM_D_HISVO(String SEQ) {
        this.SEQ = SEQ;
    }

    public String getSEQ() {
        return this.SEQ;
    }

    public void setSEQ(String SEQ) {
        this.SEQ = SEQ;
    }

    public String getCUST_ID() {
        return this.CUST_ID;
    }

    public void setCUST_ID(String CUST_ID) {
        this.CUST_ID = CUST_ID;
    }

    public String getCUST_EDUCTION_BEFORE() {
        return this.CUST_EDUCTION_BEFORE;
    }

    public void setCUST_EDUCTION_BEFORE(String CUST_EDUCTION_BEFORE) {
        this.CUST_EDUCTION_BEFORE = CUST_EDUCTION_BEFORE;
    }

    public String getCUST_CAREER_BEFORE() {
        return this.CUST_CAREER_BEFORE;
    }

    public void setCUST_CAREER_BEFORE(String CUST_CAREER_BEFORE) {
        this.CUST_CAREER_BEFORE = CUST_CAREER_BEFORE;
    }

    public String getCUST_MARRIAGE_BEFORE() {
        return this.CUST_MARRIAGE_BEFORE;
    }

    public void setCUST_MARRIAGE_BEFORE(String CUST_MARRIAGE_BEFORE) {
        this.CUST_MARRIAGE_BEFORE = CUST_MARRIAGE_BEFORE;
    }

    public String getCUST_CHILDREN_BEFORE() {
        return this.CUST_CHILDREN_BEFORE;
    }

    public void setCUST_CHILDREN_BEFORE(String CUST_CHILDREN_BEFORE) {
        this.CUST_CHILDREN_BEFORE = CUST_CHILDREN_BEFORE;
    }

    public String getCUST_HEALTH_BEFORE() {
        return this.CUST_HEALTH_BEFORE;
    }

    public void setCUST_HEALTH_BEFORE(String CUST_HEALTH_BEFORE) {
        this.CUST_HEALTH_BEFORE = CUST_HEALTH_BEFORE;
    }

    public String getCUST_EDUCTION_AFTER() {
        return this.CUST_EDUCTION_AFTER;
    }

    public void setCUST_EDUCTION_AFTER(String CUST_EDUCTION_AFTER) {
        this.CUST_EDUCTION_AFTER = CUST_EDUCTION_AFTER;
    }

    public String getCUST_CAREER_AFTER() {
        return this.CUST_CAREER_AFTER;
    }

    public void setCUST_CAREER_AFTER(String CUST_CAREER_AFTER) {
        this.CUST_CAREER_AFTER = CUST_CAREER_AFTER;
    }

    public String getCUST_MARRIAGE_AFTER() {
        return this.CUST_MARRIAGE_AFTER;
    }

    public void setCUST_MARRIAGE_AFTER(String CUST_MARRIAGE_AFTER) {
        this.CUST_MARRIAGE_AFTER = CUST_MARRIAGE_AFTER;
    }

    public String getCUST_CHILDREN_AFTER() {
        return this.CUST_CHILDREN_AFTER;
    }

    public void setCUST_CHILDREN_AFTER(String CUST_CHILDREN_AFTER) {
        this.CUST_CHILDREN_AFTER = CUST_CHILDREN_AFTER;
    }

    public String getCUST_HEALTH_AFTER() {
        return this.CUST_HEALTH_AFTER;
    }

    public void setCUST_HEALTH_AFTER(String CUST_HEALTH_AFTER) {
        this.CUST_HEALTH_AFTER = CUST_HEALTH_AFTER;
    }

    public String getANSWER_2() {
        return this.ANSWER_2;
    }

    public void setANSWER_2(String ANSWER_2) {
        this.ANSWER_2 = ANSWER_2;
    }

    public String getCUST_TEL() {
        return this.CUST_TEL;
    }

    public void setCUST_TEL(String CUST_TEL) {
        this.CUST_TEL = CUST_TEL;
    }

    public String getCUST_EMAIL() {
        return this.CUST_EMAIL;
    }

    public void setCUST_EMAIL(String CUST_EMAIL) {
        this.CUST_EMAIL = CUST_EMAIL;
    }

    public String getCUST_ADDRESS() {
        return this.CUST_ADDRESS;
    }

    public void setCUST_ADDRESS(String CUST_ADDRESS) {
        this.CUST_ADDRESS = CUST_ADDRESS;
    }

    public String getUPDATE_YN() {
        return this.UPDATE_YN;
    }

    public void setUPDATE_YN(String UPDATE_YN) {
        this.UPDATE_YN = UPDATE_YN;
    }

    public String getCUST_DEGRADE() {
        return this.CUST_DEGRADE;
    }

    public void setCUST_DEGRADE(String CUST_DEGRADE) {
        this.CUST_DEGRADE = CUST_DEGRADE;
    }

    public String getCUST_SCHOOL() {
        return this.CUST_SCHOOL;
    }

    public void setCUST_SCHOOL(String CUST_SCHOOL) {
        this.CUST_SCHOOL = CUST_SCHOOL;
    }

    public String getCUST_EDU_CHANGE() {
        return this.CUST_EDU_CHANGE;
    }

    public void setCUST_EDU_CHANGE(String CUST_EDU_CHANGE) {
        this.CUST_EDU_CHANGE = CUST_EDU_CHANGE;
    }

    public String getCUST_HEALTH_CHANGE() {
        return this.CUST_HEALTH_CHANGE;
    }

    public void setCUST_HEALTH_CHANGE(String CUST_HEALTH_CHANGE) {
        this.CUST_HEALTH_CHANGE = CUST_HEALTH_CHANGE;
    }

    public Timestamp getDEGRADE_DUE_DATE() {
		return DEGRADE_DUE_DATE;
	}

	public void setDEGRADE_DUE_DATE(Timestamp dEGRADE_DUE_DATE) {
		DEGRADE_DUE_DATE = dEGRADE_DUE_DATE;
	}

	public Timestamp getPRO_DUE_DATE() {
		return PRO_DUE_DATE;
	}

	public void setPRO_DUE_DATE(Timestamp pRO_DUE_DATE) {
		PRO_DUE_DATE = pRO_DUE_DATE;
	}

	public String getCUST_EMAIL_BEFORE() {
		return CUST_EMAIL_BEFORE;
	}

	public void setCUST_EMAIL_BEFORE(String cUST_EMAIL_BEFORE) {
		CUST_EMAIL_BEFORE = cUST_EMAIL_BEFORE;
	}

	public String getSAMEEMAIL_REASON() {
		return SAMEEMAIL_REASON;
	}

	public void setSAMEEMAIL_REASON(String sAMEEMAIL_REASON) {
		SAMEEMAIL_REASON = sAMEEMAIL_REASON;
	}
	
	public String getSAMEEMAIL_CHOOSE() {
		return SAMEEMAIL_CHOOSE;
	}

	public void setSAMEEMAIL_CHOOSE(String sAMEEMAIL_CHOOSE) {
		SAMEEMAIL_CHOOSE = sAMEEMAIL_CHOOSE;
	}

	public Timestamp getHNWC_DUE_DATE() {
		return HNWC_DUE_DATE;
	}

	public void setHNWC_DUE_DATE(Timestamp hNWC_DUE_DATE) {
		HNWC_DUE_DATE = hNWC_DUE_DATE;
	}

	public Timestamp getHNWC_INVALID_DATE() {
		return HNWC_INVALID_DATE;
	}

	public void setHNWC_INVALID_DATE(Timestamp hNWC_INVALID_DATE) {
		HNWC_INVALID_DATE = hNWC_INVALID_DATE;
	}

	public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("SEQ", getSEQ())
            .toString();
    }

}
