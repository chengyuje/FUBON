package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;

/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBIOT_BATCH_INFOVO extends VOBase {

    /** identifier field */
    private BigDecimal BATCH_INFO_KEYNO;

    /** persistent field */
    private String OP_BATCH_NO;

    /** persistent field */
    private Timestamp OP_BATCH_DATE;

    /** persistent field */
    private String OP_BATCH_OPRID;

    /** nullable persistent field */
    private String SUBMIT_WAY;

    /** nullable persistent field */
    private Timestamp SUBMIT_DATE;
    
    /** nullable persistent field */
    private String BATCH_SETUP_EMPID;

    /** nullable persistent field */
    private Timestamp BATCH_SETUP_DATE;
    
    /** nullable persistent field */
    private String BATCH_SEQ;

    /** nullable persistent field */
    private String INS_RCV_OPRID;

    /** nullable persistent field */
    private Timestamp INS_RCV_DATE;

    /** nullable persistent field */
    private Timestamp INS_SUBMIT_DATE;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBIOT_BATCH_INFO";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBIOT_BATCH_INFOVO(BigDecimal BATCH_INFO_KEYNO, String OP_BATCH_NO, Timestamp OP_BATCH_DATE, String OP_BATCH_OPRID, String SUBMIT_WAY, Timestamp SUBMIT_DATE, String BATCH_SEQ, Timestamp createtime, String creator, Timestamp lastupdate, String modifier, String INS_RCV_OPRID, Timestamp INS_RCV_DATE, Timestamp INS_SUBMIT_DATE, Long version) {
        this.BATCH_INFO_KEYNO = BATCH_INFO_KEYNO;
        this.OP_BATCH_NO = OP_BATCH_NO;
        this.OP_BATCH_DATE = OP_BATCH_DATE;
        this.OP_BATCH_OPRID = OP_BATCH_OPRID;
        this.SUBMIT_WAY = SUBMIT_WAY;
        this.SUBMIT_DATE = SUBMIT_DATE;
        this.BATCH_SEQ = BATCH_SEQ;
        this.createtime = createtime;
        this.creator = creator;
        this.lastupdate = lastupdate;
        this.modifier = modifier;
        this.INS_RCV_OPRID = INS_RCV_OPRID;
        this.INS_RCV_DATE = INS_RCV_DATE;
        this.INS_SUBMIT_DATE = INS_SUBMIT_DATE;
        this.version = version;
    }

    /** default constructor */
    public TBIOT_BATCH_INFOVO() {
    }

    /** minimal constructor */
    public TBIOT_BATCH_INFOVO(BigDecimal BATCH_INFO_KEYNO, String OP_BATCH_NO, Timestamp OP_BATCH_DATE, String OP_BATCH_OPRID, Timestamp createtime, String creator) {
        this.BATCH_INFO_KEYNO = BATCH_INFO_KEYNO;
        this.OP_BATCH_NO = OP_BATCH_NO;
        this.OP_BATCH_DATE = OP_BATCH_DATE;
        this.OP_BATCH_OPRID = OP_BATCH_OPRID;
        this.createtime = createtime;
        this.creator = creator;
    }

    public BigDecimal getBATCH_INFO_KEYNO() {
        return this.BATCH_INFO_KEYNO;
    }

    public void setBATCH_INFO_KEYNO(BigDecimal BATCH_INFO_KEYNO) {
        this.BATCH_INFO_KEYNO = BATCH_INFO_KEYNO;
    }

    public String getOP_BATCH_NO() {
        return this.OP_BATCH_NO;
    }

    public void setOP_BATCH_NO(String OP_BATCH_NO) {
        this.OP_BATCH_NO = OP_BATCH_NO;
    }

    public Timestamp getOP_BATCH_DATE() {
        return this.OP_BATCH_DATE;
    }

    public void setOP_BATCH_DATE(Timestamp OP_BATCH_DATE) {
        this.OP_BATCH_DATE = OP_BATCH_DATE;
    }

    public String getOP_BATCH_OPRID() {
        return this.OP_BATCH_OPRID;
    }

    public void setOP_BATCH_OPRID(String OP_BATCH_OPRID) {
        this.OP_BATCH_OPRID = OP_BATCH_OPRID;
    }

    public String getSUBMIT_WAY() {
        return this.SUBMIT_WAY;
    }

    public void setSUBMIT_WAY(String SUBMIT_WAY) {
        this.SUBMIT_WAY = SUBMIT_WAY;
    }

    public Timestamp getSUBMIT_DATE() {
        return this.SUBMIT_DATE;
    }

    public void setSUBMIT_DATE(Timestamp SUBMIT_DATE) {
        this.SUBMIT_DATE = SUBMIT_DATE;
    }

	public String getBATCH_SETUP_EMPID() {
		return BATCH_SETUP_EMPID;
	}

	public void setBATCH_SETUP_EMPID(String bATCH_SETUP_EMPID) {
		BATCH_SETUP_EMPID = bATCH_SETUP_EMPID;
	}

	public Timestamp getBATCH_SETUP_DATE() {
		return BATCH_SETUP_DATE;
	}

	public void setBATCH_SETUP_DATE(Timestamp bATCH_SETUP_DATE) {
		BATCH_SETUP_DATE = bATCH_SETUP_DATE;
	}

	public String getBATCH_SEQ() {
        return this.BATCH_SEQ;
    }

    public void setBATCH_SEQ(String BATCH_SEQ) {
        this.BATCH_SEQ = BATCH_SEQ;
    }

    public String getINS_RCV_OPRID() {
        return this.INS_RCV_OPRID;
    }

    public void setINS_RCV_OPRID(String INS_RCV_OPRID) {
        this.INS_RCV_OPRID = INS_RCV_OPRID;
    }

    public Timestamp getINS_RCV_DATE() {
        return this.INS_RCV_DATE;
    }

    public void setINS_RCV_DATE(Timestamp INS_RCV_DATE) {
        this.INS_RCV_DATE = INS_RCV_DATE;
    }

    public Timestamp getINS_SUBMIT_DATE() {
        return this.INS_SUBMIT_DATE;
    }

    public void setINS_SUBMIT_DATE(Timestamp INS_SUBMIT_DATE) {
        this.INS_SUBMIT_DATE = INS_SUBMIT_DATE;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("BATCH_INFO_KEYNO", getBATCH_INFO_KEYNO())
            .toString();
    }

}
