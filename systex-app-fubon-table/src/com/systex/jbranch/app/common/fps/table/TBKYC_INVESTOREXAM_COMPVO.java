package com.systex.jbranch.app.common.fps.table;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;


/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBKYC_INVESTOREXAM_COMPVO extends VOBase {

    /** identifier field */
    private String SEQ;

    /** persistent field */
    private String EXAM_VERSION;

    /** persistent field */
    private String LAST_SEQ;

    /** nullable persistent field */
    private String ANSWER_2;

    /** nullable persistent field */
    private String LAST_ANSWER_2;

    /** nullable persistent field */
    private String ANSWER_COMP;
    
	public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBKYC_INVESTOREXAM_COMP";
	
	
	public String getTableuid () {
	    return TABLE_UID;
	}

    /** default constructor */
    public TBKYC_INVESTOREXAM_COMPVO() {
    }

    /** minimal constructor */
    public TBKYC_INVESTOREXAM_COMPVO(String SEQ, String EXAM_VERSIONE) {
        this.SEQ = SEQ;
        this.EXAM_VERSION = EXAM_VERSION;
    }

	public String getSEQ() {
		return SEQ;
	}

	public void setSEQ(String sEQ) {
		SEQ = sEQ;
	}

	public String getEXAM_VERSION() {
		return EXAM_VERSION;
	}

	public void setEXAM_VERSION(String eXAM_VERSION) {
		EXAM_VERSION = eXAM_VERSION;
	}

	public String getLAST_SEQ() {
		return LAST_SEQ;
	}

	public void setLAST_SEQ(String lAST_SEQ) {
		LAST_SEQ = lAST_SEQ;
	}

	public String getANSWER_2() {
		return ANSWER_2;
	}

	public void setANSWER_2(String aNSWER_2) {
		ANSWER_2 = aNSWER_2;
	}

	public String getLAST_ANSWER_2() {
		return LAST_ANSWER_2;
	}

	public void setLAST_ANSWER_2(String lAST_ANSWER_2) {
		LAST_ANSWER_2 = lAST_ANSWER_2;
	}

	public String getANSWER_COMP() {
		return ANSWER_COMP;
	}

	public void setANSWER_COMP(String aNSWER_COMP) {
		ANSWER_COMP = aNSWER_COMP;
	}

	public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("SEQ", getSEQ())
            .toString();
    }

}
