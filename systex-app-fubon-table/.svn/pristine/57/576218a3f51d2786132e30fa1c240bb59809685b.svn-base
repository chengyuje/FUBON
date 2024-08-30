package com.systex.jbranch.app.common.fps.table;

import java.sql.Blob;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;

;

/** @Author : SystexDBTool CodeGenerator By LeoLin */
public class TBFPS_PORTFOLIO_PLAN_RPTVO extends VOBase {

	/** identifier field */
	private com.systex.jbranch.app.common.fps.table.TBFPS_PORTFOLIO_PLAN_RPTPK comp_id;

	/** persistent field */
	private String CUST_ID;
	private Date REPORT_DATE;
	private String REMINDER;
	private Blob PLAN_PDF_FILE;
	private Date LAST_RPT_DATE;


	public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBFPS_PORTFOLIO_PLAN_RPT";

	public String getTableuid() {
		return TABLE_UID;
	}

	/** full constructor */
	public TBFPS_PORTFOLIO_PLAN_RPTVO(
			com.systex.jbranch.app.common.fps.table.TBFPS_PORTFOLIO_PLAN_RPTPK comp_id,
			String CUST_ID, Date REPORT_DATE, String REMINDER, Blob PLAN_PDF_FILE, Date LAST_RPT_DATE, Timestamp createtime,
			String creator, String modifier, Timestamp lastupdate, Long version) {
		this.comp_id = comp_id;
		this.CUST_ID                      = CUST_ID;
		this.REPORT_DATE                  = REPORT_DATE;
		this.REMINDER                     = REMINDER;
		this.PLAN_PDF_FILE                = PLAN_PDF_FILE;
		this.LAST_RPT_DATE                = LAST_RPT_DATE;
		this.createtime = createtime;
		this.creator = creator;
		this.modifier = modifier;
		this.lastupdate = lastupdate;
		this.version = version;
	}

	/** default constructor */
	public TBFPS_PORTFOLIO_PLAN_RPTVO() {
	}

	/** minimal constructor */
	public TBFPS_PORTFOLIO_PLAN_RPTVO(
			com.systex.jbranch.app.common.fps.table.TBFPS_PORTFOLIO_PLAN_RPTPK comp_id,
			String CUST_ID, Date REPORT_DATE, String REMINDER, Blob PLAN_PDF_FILE, Date LAST_RPT_DATE) {
		this.comp_id = comp_id;
		this.CUST_ID                      = CUST_ID;
		this.REPORT_DATE                  = REPORT_DATE;
		this.REMINDER                     = REMINDER;
		this.PLAN_PDF_FILE                = PLAN_PDF_FILE;
		this.LAST_RPT_DATE                = LAST_RPT_DATE;
	}

	public com.systex.jbranch.app.common.fps.table.TBFPS_PORTFOLIO_PLAN_RPTPK getcomp_id() {
		return this.comp_id;
	}

	public void setcomp_id(
			com.systex.jbranch.app.common.fps.table.TBFPS_PORTFOLIO_PLAN_RPTPK comp_id) {
		this.comp_id = comp_id;
	}

	public com.systex.jbranch.app.common.fps.table.TBFPS_PORTFOLIO_PLAN_RPTPK getComp_id() {
		return comp_id;
	}

	public void setComp_id(
			com.systex.jbranch.app.common.fps.table.TBFPS_PORTFOLIO_PLAN_RPTPK comp_id) {
		this.comp_id = comp_id;
	}

	public String getCUST_ID() {
		return CUST_ID;
	}

	public void setCUST_ID(String cUST_ID) {
		CUST_ID = cUST_ID;
	}

	public Date getREPORT_DATE() {
		return REPORT_DATE;
	}

	public void setREPORT_DATE(Date rEPORT_DATE) {
		REPORT_DATE = rEPORT_DATE;
	}

	public String getREMINDER() {
		return REMINDER;
	}

	public void setREMINDER(String rEMINDER) {
		REMINDER = rEMINDER;
	}

	public Blob getPLAN_PDF_FILE() {
		return PLAN_PDF_FILE;
	}

	public void setPLAN_PDF_FILE(Blob pLAN_PDF_FILE) {
		PLAN_PDF_FILE = pLAN_PDF_FILE;
	}

	public Date getLAST_RPT_DATE() {
		return LAST_RPT_DATE;
	}

	public void setLAST_RPT_DATE(Date lAST_RPT_DATE) {
		LAST_RPT_DATE = lAST_RPT_DATE;
	}

	public static String getTableUid() {
		return TABLE_UID;
	}

	public void checkDefaultValue() {
	}

	public String toString() {
		return new ToStringBuilder(this).append("comp_id", getcomp_id())
				.toString();
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if (!(other instanceof TBFPS_PORTFOLIO_PLAN_RPTVO))
			return false;
		TBFPS_PORTFOLIO_PLAN_RPTVO castOther = (TBFPS_PORTFOLIO_PLAN_RPTVO) other;
		return new EqualsBuilder().append(this.getcomp_id(),
				castOther.getcomp_id()).isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder().append(getcomp_id()).toHashCode();
	}

}
