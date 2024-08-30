package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBSYS_QUESTIONNAIREVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBSYS_QUESTIONNAIREPK comp_id;

    /** nullable persistent field */
    private String EXAM_NAME;

    /** nullable persistent field */
    private String QUEST_TYPE;

    /** nullable persistent field */
    private BigDecimal QST_NO;

    /** nullable persistent field */
    private String ESSENTIAL_FLAG;

    /** nullable persistent field */
    private String RL_VERSION;
    
    /** nullable persistent field */
    private String RLR_VERSION;

    /** nullable persistent field */
    private String STATUS;

    /** nullable persistent field */
    private Timestamp ACTIVE_DATE;

    /** nullable persistent field */
    private Timestamp EXPIRY_DATE;

    /** nullable persistent field */
    private String RS_VERSION;
    
    /** nullable persistent field */
    private BigDecimal QST_WEIGHT;
    
    /** nullable persistent field */
    private String SCORE_TYPE;
    
    /** nullable persistent field */
    private BigDecimal INT_SCORE;

public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBSYS_QUESTIONNAIRE";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBSYS_QUESTIONNAIREVO(com.systex.jbranch.app.common.fps.table.TBSYS_QUESTIONNAIREPK comp_id, String EXAM_NAME, String QUEST_TYPE, BigDecimal QST_NO, String ESSENTIAL_FLAG, String RL_VERSION, String STATUS, Timestamp ACTIVE_DATE, Timestamp EXPIRY_DATE, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version, String RS_VERSION, String RLR_VERSION, BigDecimal QST_WEIGHT, String SCORE_TYPE, BigDecimal INT_SCORE) {
        this.comp_id = comp_id;
        this.EXAM_NAME = EXAM_NAME;
        this.QUEST_TYPE = QUEST_TYPE;
        this.QST_NO = QST_NO;
        this.ESSENTIAL_FLAG = ESSENTIAL_FLAG;
        this.RL_VERSION = RL_VERSION;
        this.RS_VERSION = RS_VERSION;
        this.RLR_VERSION = RLR_VERSION;
        this.QST_WEIGHT = QST_WEIGHT;
        this.SCORE_TYPE = SCORE_TYPE;
        this.INT_SCORE = INT_SCORE;
        this.STATUS = STATUS;
        this.ACTIVE_DATE = ACTIVE_DATE;
        this.EXPIRY_DATE = EXPIRY_DATE;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBSYS_QUESTIONNAIREVO() {
    }

    /** minimal constructor */
    public TBSYS_QUESTIONNAIREVO(com.systex.jbranch.app.common.fps.table.TBSYS_QUESTIONNAIREPK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.app.common.fps.table.TBSYS_QUESTIONNAIREPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBSYS_QUESTIONNAIREPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getEXAM_NAME() {
        return this.EXAM_NAME;
    }

    public void setEXAM_NAME(String EXAM_NAME) {
        this.EXAM_NAME = EXAM_NAME;
    }

    public String getQUEST_TYPE() {
        return this.QUEST_TYPE;
    }

    public void setQUEST_TYPE(String QUEST_TYPE) {
        this.QUEST_TYPE = QUEST_TYPE;
    }

    public BigDecimal getQST_NO() {
        return this.QST_NO;
    }

    public void setQST_NO(BigDecimal QST_NO) {
        this.QST_NO = QST_NO;
    }

    public String getESSENTIAL_FLAG() {
        return this.ESSENTIAL_FLAG;
    }

    public void setESSENTIAL_FLAG(String ESSENTIAL_FLAG) {
        this.ESSENTIAL_FLAG = ESSENTIAL_FLAG;
    }

    public String getRL_VERSION() {
        return this.RL_VERSION;
    }

    public void setRL_VERSION(String RL_VERSION) {
        this.RL_VERSION = RL_VERSION;
    }
    
    public String getRLR_VERSION() {
        return this.RLR_VERSION;
    }

    public void setRLR_VERSION(String RLR_VERSION) {
        this.RLR_VERSION = RLR_VERSION;
    }
    
    public String getRS_VERSION() {
        return this.RS_VERSION;
    }

    public void setRS_VERSION(String RS_VERSION) {
        this.RS_VERSION = RS_VERSION;
    }
    
    public BigDecimal getQST_WEIGHT() {
		return QST_WEIGHT;
	}

	public void setQST_WEIGHT(BigDecimal qST_WEIGHT) {
		QST_WEIGHT = qST_WEIGHT;
	}

	public String getSCORE_TYPE() {
		return SCORE_TYPE;
	}

	public void setSCORE_TYPE(String sCORE_TYPE) {
		SCORE_TYPE = sCORE_TYPE;
	}

	public BigDecimal getINT_SCORE() {
		return INT_SCORE;
	}

	public void setINT_SCORE(BigDecimal iNT_SCORE) {
		INT_SCORE = iNT_SCORE;
	}

	public String getSTATUS() {
        return this.STATUS;
    }

    public void setSTATUS(String STATUS) {
        this.STATUS = STATUS;
    }

    public Timestamp getACTIVE_DATE() {
        return this.ACTIVE_DATE;
    }

    public void setACTIVE_DATE(Timestamp ACTIVE_DATE) {
        this.ACTIVE_DATE = ACTIVE_DATE;
    }

    public Timestamp getEXPIRY_DATE() {
        return this.EXPIRY_DATE;
    }

    public void setEXPIRY_DATE(Timestamp EXPIRY_DATE) {
        this.EXPIRY_DATE = EXPIRY_DATE;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getcomp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBSYS_QUESTIONNAIREVO) ) return false;
        TBSYS_QUESTIONNAIREVO castOther = (TBSYS_QUESTIONNAIREVO) other;
        return new EqualsBuilder()
            .append(this.getcomp_id(), castOther.getcomp_id())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getcomp_id())
            .toHashCode();
    }

}
