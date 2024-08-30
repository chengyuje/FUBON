package com.systex.jbranch.app.common.fps.table;

import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;

;

/** @Author : SystexDBTool CodeGenerator By LeoLin */
public class TBCRM_CUST_VISIT_RECORDVO extends VOBase {

	/** identifier field */
	private String VISIT_SEQ;

	/** nullable persistent field */
	private String VISITOR_ROLE;

	/** nullable persistent field */
	private String CUST_ID;

	/** nullable persistent field */
	private String CMU_TYPE;

	/** nullable persistent field */
	private String VISIT_MEMO;
	
	private Timestamp VISIT_DT;
	
	private String VISIT_CREPLY;

	public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBCRM_CUST_VISIT_RECORD";

	public String getTableuid() {
		return TABLE_UID;
	}

	/** full constructor */
	public TBCRM_CUST_VISIT_RECORDVO(String VISIT_SEQ, String VISITOR_ROLE, String CUST_ID, String CMU_TYPE, String VISIT_MEMO, Timestamp VISIT_DT, String VISIT_CREPLY, String creator, Timestamp createtime, String modifier, Timestamp lastupdate, Long version) {
		this.VISIT_SEQ = VISIT_SEQ;
		this.VISITOR_ROLE = VISITOR_ROLE;
		this.CUST_ID = CUST_ID;
		this.CMU_TYPE = CMU_TYPE;
		this.VISIT_MEMO = VISIT_MEMO;
		this.VISIT_DT = VISIT_DT;
		this.VISIT_CREPLY = VISIT_CREPLY;
		this.creator = creator;
		this.createtime = createtime;
		this.modifier = modifier;
		this.lastupdate = lastupdate;
		this.version = version;
	}

	/** default constructor */
	public TBCRM_CUST_VISIT_RECORDVO() {
	}

	public Timestamp getVISIT_DT() {
		return VISIT_DT;
	}

	public void setVISIT_DT(Timestamp vISIT_DT) {
		VISIT_DT = vISIT_DT;
	}

	public String getVISIT_CREPLY() {
		return VISIT_CREPLY;
	}

	public void setVISIT_CREPLY(String vISIT_CREPLY) {
		VISIT_CREPLY = vISIT_CREPLY;
	}

	/** minimal constructor */
	public TBCRM_CUST_VISIT_RECORDVO(String VISIT_SEQ) {
		this.VISIT_SEQ = VISIT_SEQ;
	}

	public String getVISIT_SEQ() {
		return this.VISIT_SEQ;
	}

	public void setVISIT_SEQ(String VISIT_SEQ) {
		this.VISIT_SEQ = VISIT_SEQ;
	}

	public String getVISITOR_ROLE() {
		return this.VISITOR_ROLE;
	}

	public void setVISITOR_ROLE(String VISITOR_ROLE) {
		this.VISITOR_ROLE = VISITOR_ROLE;
	}

	public String getCUST_ID() {
		return this.CUST_ID;
	}

	public void setCUST_ID(String CUST_ID) {
		this.CUST_ID = CUST_ID;
	}

	public String getCMU_TYPE() {
		return this.CMU_TYPE;
	}

	public void setCMU_TYPE(String CMU_TYPE) {
		this.CMU_TYPE = CMU_TYPE;
	}

	public String getVISIT_MEMO() {
		return this.VISIT_MEMO;
	}

	public void setVISIT_MEMO(String VISIT_MEMO) {
		this.VISIT_MEMO = VISIT_MEMO;
	}

	public void checkDefaultValue() {
	}

	public String toString() {
		return new ToStringBuilder(this).append("VISIT_SEQ", getVISIT_SEQ()).toString();
	}

}
