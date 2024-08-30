package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBIOT_FUND_LINKVO extends VOBase {

	/** identifier field */
    private BigDecimal SEQ_KEY_NO;

    /** nullable persistent field */
    private BigDecimal INS_KEYNO;
    
    /** nullable persistent field */
    private BigDecimal PRD_LK_KEYNO;
    
    /** nullable persistent field */
    private String PREMATCH_SEQ;
    
    /** nullable persistent field */
    private String INSPRD_ID;
    
    /** nullable persistent field */
    private BigDecimal LINK_PCT;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBIOT_FUND_LINK";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBIOT_FUND_LINKVO(BigDecimal INS_KEYNO, BigDecimal PRD_LK_KEYNO, String PREMATCH_SEQ, String INSPRD_ID, BigDecimal LINK_PCT, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.INS_KEYNO = INS_KEYNO;
        this.PRD_LK_KEYNO = PRD_LK_KEYNO;
        this.PREMATCH_SEQ = PREMATCH_SEQ;
        this.INSPRD_ID = INSPRD_ID;
        this.LINK_PCT = LINK_PCT;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBIOT_FUND_LINKVO() {
    }

    /** minimal constructor */
    public TBIOT_FUND_LINKVO(BigDecimal SEQ_KEY_NO) {
        this.SEQ_KEY_NO = SEQ_KEY_NO;
    }

    public BigDecimal getSEQ_KEY_NO() {
		return SEQ_KEY_NO;
	}

	public void setSEQ_KEY_NO(BigDecimal sEQ_KEY_NO) {
		SEQ_KEY_NO = sEQ_KEY_NO;
	}

	public BigDecimal getINS_KEYNO() {
		return INS_KEYNO;
	}

	public void setINS_KEYNO(BigDecimal iNS_KEYNO) {
		INS_KEYNO = iNS_KEYNO;
	}

	public BigDecimal getPRD_LK_KEYNO() {
		return PRD_LK_KEYNO;
	}

	public void setPRD_LK_KEYNO(BigDecimal pRD_LK_KEYNO) {
		PRD_LK_KEYNO = pRD_LK_KEYNO;
	}

	public String getPREMATCH_SEQ() {
		return PREMATCH_SEQ;
	}

	public void setPREMATCH_SEQ(String pREMATCH_SEQ) {
		PREMATCH_SEQ = pREMATCH_SEQ;
	}

	public String getINSPRD_ID() {
		return INSPRD_ID;
	}

	public void setINSPRD_ID(String iNSPRD_ID) {
		INSPRD_ID = iNSPRD_ID;
	}

	public BigDecimal getLINK_PCT() {
        return this.LINK_PCT;
    }

    public void setLINK_PCT(BigDecimal LINK_PCT) {
        this.LINK_PCT = LINK_PCT;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("SEQ_KEY_NO", getSEQ_KEY_NO())
            .toString();
    }

}
