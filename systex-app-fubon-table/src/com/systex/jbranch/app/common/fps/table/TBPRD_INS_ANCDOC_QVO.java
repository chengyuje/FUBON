package com.systex.jbranch.app.common.fps.table;

import java.sql.Timestamp;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;

/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPRD_INS_ANCDOC_QVO extends VOBase {

    /** identifier field */
    private String Q_ID;

    /** persistent field */
    private String Q_NAME;

    /** nullable persistent field */
    private String Q_TYPE;

    /** nullable persistent field */
    private Timestamp EFFECT_DATE;

    /** nullable persistent field */
    private Timestamp EXPIRY_DATE;

    /** nullable persistent field */
    private String APPROVER;

    /** nullable persistent field */
    private Timestamp APP_DATE;
    
    /** nullable persistent field */
    private String TEXT_STYLE_B;
    
    /** nullable persistent field */
    private String TEXT_STYLE_I;
    
    /** nullable persistent field */
    private String TEXT_STYLE_U;
    
    /** nullable persistent field */
    private String TEXT_STYLE_A;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBPRD_INS_ANCDOC_Q";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBPRD_INS_ANCDOC_QVO(String Q_ID, String Q_NAME, String Q_TYPE, Timestamp EFFECT_DATE, Timestamp EXPIRY_DATE, String APPROVER, Timestamp APP_DATE, Timestamp createtime, String creator, Timestamp lastupdate, String modifier, Long version) {
        this.Q_ID = Q_ID;
        this.Q_NAME = Q_NAME;
        this.Q_TYPE = Q_TYPE;
        this.EFFECT_DATE = EFFECT_DATE;
        this.EXPIRY_DATE = EXPIRY_DATE;
        this.APPROVER = APPROVER;
        this.APP_DATE = APP_DATE;
        this.createtime = createtime;
        this.creator = creator;
        this.lastupdate = lastupdate;
        this.modifier = modifier;
        this.version = version;
    }

    /** default constructor */
    public TBPRD_INS_ANCDOC_QVO() {
    }

    /** minimal constructor */
    public TBPRD_INS_ANCDOC_QVO(String Q_ID, String Q_NAME) {
        this.Q_ID = Q_ID;
        this.Q_NAME = Q_NAME;
    }

    public String getQ_ID() {
        return this.Q_ID;
    }

    public void setQ_ID(String Q_ID) {
        this.Q_ID = Q_ID;
    }

    public String getQ_NAME() {
        return this.Q_NAME;
    }

    public void setQ_NAME(String Q_NAME) {
        this.Q_NAME = Q_NAME;
    }

    public String getQ_TYPE() {
        return this.Q_TYPE;
    }

    public void setQ_TYPE(String Q_TYPE) {
        this.Q_TYPE = Q_TYPE;
    }

    public Timestamp getEFFECT_DATE() {
        return this.EFFECT_DATE;
    }

    public void setEFFECT_DATE(Timestamp EFFECT_DATE) {
        this.EFFECT_DATE = EFFECT_DATE;
    }

    public Timestamp getEXPIRY_DATE() {
        return this.EXPIRY_DATE;
    }

    public void setEXPIRY_DATE(Timestamp EXPIRY_DATE) {
        this.EXPIRY_DATE = EXPIRY_DATE;
    }

    public String getAPPROVER() {
        return this.APPROVER;
    }

    public void setAPPROVER(String APPROVER) {
        this.APPROVER = APPROVER;
    }

    public Timestamp getAPP_DATE() {
        return this.APP_DATE;
    }

    public void setAPP_DATE(Timestamp APP_DATE) {
        this.APP_DATE = APP_DATE;
    }

	public String getTEXT_STYLE_B() {
		return TEXT_STYLE_B;
	}

	public void setTEXT_STYLE_B(String tEXT_STYLE_B) {
		TEXT_STYLE_B = tEXT_STYLE_B;
	}

	public String getTEXT_STYLE_I() {
		return TEXT_STYLE_I;
	}

	public void setTEXT_STYLE_I(String tEXT_STYLE_I) {
		TEXT_STYLE_I = tEXT_STYLE_I;
	}

	public String getTEXT_STYLE_U() {
		return TEXT_STYLE_U;
	}

	public void setTEXT_STYLE_U(String tEXT_STYLE_U) {
		TEXT_STYLE_U = tEXT_STYLE_U;
	}

	public String getTEXT_STYLE_A() {
		return TEXT_STYLE_A;
	}

	public void setTEXT_STYLE_A(String tEXT_STYLE_A) {
		TEXT_STYLE_A = tEXT_STYLE_A;
	}

	public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("Q_ID", getQ_ID())
            .toString();
    }

}
