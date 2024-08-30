package com.systex.jbranch.app.common.fps.table;

import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBSYS_QST_QUESTIONVO extends VOBase {

    /** identifier field */
    private String QUESTION_VERSION;

    /** nullable persistent field */
    private String QUESTION_DESC;

    /** nullable persistent field */
    private String MODULE_CATEGORY;

    /** nullable persistent field */
    private String QUESTION_TYPE;

    /** nullable persistent field */
    private String CORR_ANS;
    
    /** nullable persistent field */
    private String ANS_OTHER_FLAG;

    /** nullable persistent field */
    private String ANS_MEMO_FLAG;

    /** nullable persistent field */
    private String PICTURE;

    /** nullable persistent field */
    private String STATUS;

    /** nullable persistent field */
    private String DEFINITION;
    
    /** nullable persistent field */
    private String QUESTION_DESC_ENG;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBSYS_QST_QUESTION";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBSYS_QST_QUESTIONVO(String QUESTION_VERSION, String QUESTION_DESC, String MODULE_CATEGORY, String QUESTION_TYPE, String ANS_OTHER_FLAG, String ANS_MEMO_FLAG, String PICTURE, String STATUS, String DEFINITION, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version,String CORR_ANS, String QUESTION_DESC_ENG) {
        this.QUESTION_VERSION = QUESTION_VERSION;
        this.QUESTION_DESC = QUESTION_DESC;
        this.MODULE_CATEGORY = MODULE_CATEGORY;
        this.CORR_ANS = CORR_ANS;
        this.QUESTION_TYPE = QUESTION_TYPE;
        this.ANS_OTHER_FLAG = ANS_OTHER_FLAG;
        this.ANS_MEMO_FLAG = ANS_MEMO_FLAG;
        this.PICTURE = PICTURE;
        this.STATUS = STATUS;
        this.DEFINITION = DEFINITION;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
        this.QUESTION_DESC_ENG = QUESTION_DESC_ENG;
    }

    /** default constructor */
    public TBSYS_QST_QUESTIONVO() {
    }

    /** minimal constructor */
    public TBSYS_QST_QUESTIONVO(String QUESTION_VERSION) {
        this.QUESTION_VERSION = QUESTION_VERSION;
    }

    public String getQUESTION_VERSION() {
        return this.QUESTION_VERSION;
    }

    public void setQUESTION_VERSION(String QUESTION_VERSION) {
        this.QUESTION_VERSION = QUESTION_VERSION;
    }

    public String getQUESTION_DESC() {
        return this.QUESTION_DESC;
    }

    public void setQUESTION_DESC(String QUESTION_DESC) {
        this.QUESTION_DESC = QUESTION_DESC;
    }

    public String getMODULE_CATEGORY() {
        return this.MODULE_CATEGORY;
    }

    public void setMODULE_CATEGORY(String MODULE_CATEGORY) {
        this.MODULE_CATEGORY = MODULE_CATEGORY;
    }

    public String getQUESTION_TYPE() {
        return this.QUESTION_TYPE;
    }

    public void setQUESTION_TYPE(String QUESTION_TYPE) {
        this.QUESTION_TYPE = QUESTION_TYPE;
    }

    public String getANS_OTHER_FLAG() {
        return this.ANS_OTHER_FLAG;
    }

    public void setANS_OTHER_FLAG(String ANS_OTHER_FLAG) {
        this.ANS_OTHER_FLAG = ANS_OTHER_FLAG;
    }

    public String getANS_MEMO_FLAG() {
        return this.ANS_MEMO_FLAG;
    }

    public void setANS_MEMO_FLAG(String ANS_MEMO_FLAG) {
        this.ANS_MEMO_FLAG = ANS_MEMO_FLAG;
    }

    public String getPICTURE() {
        return this.PICTURE;
    }

    public void setPICTURE(String PICTURE) {
        this.PICTURE = PICTURE;
    }

    public String getSTATUS() {
        return this.STATUS;
    }

    public void setSTATUS(String STATUS) {
        this.STATUS = STATUS;
    }

    public String getDEFINITION() {
        return this.DEFINITION;
    }

    public void setDEFINITION(String DEFINITION) {
        this.DEFINITION = DEFINITION;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("QUESTION_VERSION", getQUESTION_VERSION())
            .toString();
    }

	public String getCORR_ANS() {
		return CORR_ANS;
	}

	public void setCORR_ANS(String cORR_ANS) {
		CORR_ANS = cORR_ANS;
	}

	public String getQUESTION_DESC_ENG() {
		return QUESTION_DESC_ENG;
	}

	public void setQUESTION_DESC_ENG(String qUESTION_DESC_ENG) {
		QUESTION_DESC_ENG = qUESTION_DESC_ENG;
	}

}
