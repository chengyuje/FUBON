package com.systex.jbranch.app.common.fps.table;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;
import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class TBSQM_CSM_IMPROVE_MASTVO extends VOBase {
  private TBSQM_CSM_IMPROVE_MASTPK comp_id;
  
  private String CASE_NO;
  
  private String REGION_CENTER_ID;
  
  private String BRANCH_AREA_ID;
  
  private String BRANCH_NBR;
  
  private String EMP_ID;
  
  private String AO_CODE;
  
  private String QUESTION_DESC;
  
  private String CUST_NAME;
  
  private String MOBILE_NO;
  
  private String ANSWER;
  
  private String RESP_NOTE;
  
  private String TRADE_DATE;
  
  private String RESP_DATE;
  
  private String SEND_DATE;
  
  private String CAMPAIGN_ID;
  
  private String STEP_ID;
  
  private String HO_CHECK;
  
  private String DEDUCTION_FINAL;
  
  private String CASE_STATUS;
  
  private String SATISFACTION_O;
  
  private String SATISFACTION_W;
  
  private String DELETE_FLAG;
  
  private String OWNER_EMP_ID;
  
  private BigDecimal QST_VERSION;
  
  private String UHRM_YN;
  
  public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBSQM_CSM_IMPROVE_MAST";
  
  public String getTableuid() {
    return "com.systex.jbranch.app.common.fps.table.TBSQM_CSM_IMPROVE_MAST";
  }
  
  public TBSQM_CSM_IMPROVE_MASTVO(TBSQM_CSM_IMPROVE_MASTPK comp_id, String CASE_NO, String REGION_CENTER_ID, String BRANCH_AREA_ID, String BRANCH_NBR, String EMP_ID, String AO_CODE, String QUESTION_DESC, String CUST_NAME, String MOBILE_NO, String ANSWER, String RESP_NOTE, String TRADE_DATE, String RESP_DATE, String SEND_DATE, String CAMPAIGN_ID, String STEP_ID, String HO_CHECK, String DEDUCTION_FINAL, String CASE_STATUS, String SATISFACTION_O, String SATISFACTION_W, String DELETE_FLAG, String OWNER_EMP_ID, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, BigDecimal QST_VERSION, Long version) {
    this.comp_id = comp_id;
    this.CASE_NO = CASE_NO;
    this.REGION_CENTER_ID = REGION_CENTER_ID;
    this.BRANCH_AREA_ID = BRANCH_AREA_ID;
    this.BRANCH_NBR = BRANCH_NBR;
    this.EMP_ID = EMP_ID;
    this.AO_CODE = AO_CODE;
    this.QUESTION_DESC = QUESTION_DESC;
    this.CUST_NAME = CUST_NAME;
    this.MOBILE_NO = MOBILE_NO;
    this.ANSWER = ANSWER;
    this.RESP_NOTE = RESP_NOTE;
    this.TRADE_DATE = TRADE_DATE;
    this.RESP_DATE = RESP_DATE;
    this.SEND_DATE = SEND_DATE;
    this.CAMPAIGN_ID = CAMPAIGN_ID;
    this.STEP_ID = STEP_ID;
    this.HO_CHECK = HO_CHECK;
    this.DEDUCTION_FINAL = DEDUCTION_FINAL;
    this.CASE_STATUS = CASE_STATUS;
    this.SATISFACTION_O = SATISFACTION_O;
    this.SATISFACTION_W = SATISFACTION_W;
    this.DELETE_FLAG = DELETE_FLAG;
    this.OWNER_EMP_ID = OWNER_EMP_ID;
    this.createtime = createtime;
    this.creator = creator;
    this.modifier = modifier;
    this.lastupdate = lastupdate;
    this.QST_VERSION = QST_VERSION;
    this.version = version;
  }
  
  public TBSQM_CSM_IMPROVE_MASTVO() {}
  
  public TBSQM_CSM_IMPROVE_MASTVO(TBSQM_CSM_IMPROVE_MASTPK comp_id) {
    this.comp_id = comp_id;
  }
  
  public TBSQM_CSM_IMPROVE_MASTPK getcomp_id() {
    return this.comp_id;
  }
  
  public void setcomp_id(TBSQM_CSM_IMPROVE_MASTPK comp_id) {
    this.comp_id = comp_id;
  }
  
  public String getCASE_NO() {
    return this.CASE_NO;
  }
  
  public void setCASE_NO(String CASE_NO) {
    this.CASE_NO = CASE_NO;
  }
  
  public String getREGION_CENTER_ID() {
    return this.REGION_CENTER_ID;
  }
  
  public void setREGION_CENTER_ID(String REGION_CENTER_ID) {
    this.REGION_CENTER_ID = REGION_CENTER_ID;
  }
  
  public String getBRANCH_AREA_ID() {
    return this.BRANCH_AREA_ID;
  }
  
  public void setBRANCH_AREA_ID(String BRANCH_AREA_ID) {
    this.BRANCH_AREA_ID = BRANCH_AREA_ID;
  }
  
  public String getBRANCH_NBR() {
    return this.BRANCH_NBR;
  }
  
  public void setBRANCH_NBR(String BRANCH_NBR) {
    this.BRANCH_NBR = BRANCH_NBR;
  }
  
  public String getEMP_ID() {
    return this.EMP_ID;
  }
  
  public void setEMP_ID(String EMP_ID) {
    this.EMP_ID = EMP_ID;
  }
  
  public String getAO_CODE() {
    return this.AO_CODE;
  }
  
  public void setAO_CODE(String AO_CODE) {
    this.AO_CODE = AO_CODE;
  }
  
  public String getQUESTION_DESC() {
    return this.QUESTION_DESC;
  }
  
  public void setQUESTION_DESC(String QUESTION_DESC) {
    this.QUESTION_DESC = QUESTION_DESC;
  }
  
  public String getCUST_NAME() {
    return this.CUST_NAME;
  }
  
  public void setCUST_NAME(String CUST_NAME) {
    this.CUST_NAME = CUST_NAME;
  }
  
  public String getMOBILE_NO() {
    return this.MOBILE_NO;
  }
  
  public void setMOBILE_NO(String MOBILE_NO) {
    this.MOBILE_NO = MOBILE_NO;
  }
  
  public String getANSWER() {
    return this.ANSWER;
  }
  
  public void setANSWER(String ANSWER) {
    this.ANSWER = ANSWER;
  }
  
  public String getRESP_NOTE() {
    return this.RESP_NOTE;
  }
  
  public void setRESP_NOTE(String RESP_NOTE) {
    this.RESP_NOTE = RESP_NOTE;
  }
  
  public String getTRADE_DATE() {
    return this.TRADE_DATE;
  }
  
  public void setTRADE_DATE(String TRADE_DATE) {
    this.TRADE_DATE = TRADE_DATE;
  }
  
  public String getRESP_DATE() {
    return this.RESP_DATE;
  }
  
  public void setRESP_DATE(String RESP_DATE) {
    this.RESP_DATE = RESP_DATE;
  }
  
  public String getSEND_DATE() {
    return this.SEND_DATE;
  }
  
  public void setSEND_DATE(String SEND_DATE) {
    this.SEND_DATE = SEND_DATE;
  }
  
  public String getCAMPAIGN_ID() {
    return this.CAMPAIGN_ID;
  }
  
  public void setCAMPAIGN_ID(String CAMPAIGN_ID) {
    this.CAMPAIGN_ID = CAMPAIGN_ID;
  }
  
  public String getSTEP_ID() {
    return this.STEP_ID;
  }
  
  public void setSTEP_ID(String STEP_ID) {
    this.STEP_ID = STEP_ID;
  }
  
  public String getHO_CHECK() {
    return this.HO_CHECK;
  }
  
  public void setHO_CHECK(String HO_CHECK) {
    this.HO_CHECK = HO_CHECK;
  }
  
  public String getDEDUCTION_FINAL() {
    return this.DEDUCTION_FINAL;
  }
  
  public void setDEDUCTION_FINAL(String DEDUCTION_FINAL) {
    this.DEDUCTION_FINAL = DEDUCTION_FINAL;
  }
  
  public String getCASE_STATUS() {
    return this.CASE_STATUS;
  }
  
  public void setCASE_STATUS(String CASE_STATUS) {
    this.CASE_STATUS = CASE_STATUS;
  }
  
  public String getSATISFACTION_O() {
    return this.SATISFACTION_O;
  }
  
  public void setSATISFACTION_O(String SATISFACTION_O) {
    this.SATISFACTION_O = SATISFACTION_O;
  }
  
  public String getSATISFACTION_W() {
    return this.SATISFACTION_W;
  }
  
  public void setSATISFACTION_W(String SATISFACTION_W) {
    this.SATISFACTION_W = SATISFACTION_W;
  }
  
  public String getDELETE_FLAG() {
    return this.DELETE_FLAG;
  }
  
  public void setDELETE_FLAG(String DELETE_FLAG) {
    this.DELETE_FLAG = DELETE_FLAG;
  }
  
  public String getOWNER_EMP_ID() {
    return this.OWNER_EMP_ID;
  }
  
  public void setOWNER_EMP_ID(String OWNER_EMP_ID) {
    this.OWNER_EMP_ID = OWNER_EMP_ID;
  }
  
  public BigDecimal getQST_VERSION() {
    return this.QST_VERSION;
  }
  
  public void setQST_VERSION(BigDecimal QST_VERSION) {
    this.QST_VERSION = QST_VERSION;
  }
  
  public String getUHRM_YN() {
    return this.UHRM_YN;
  }
  
  public void setUHRM_YN(String uHRM_YN) {
    this.UHRM_YN = uHRM_YN;
  }
  
  public void checkDefaultValue() {}
  
  public String toString() {
    return (new ToStringBuilder(this)).append("comp_id", getcomp_id()).toString();
  }
  
  public boolean equals(Object other) {
    if (this == other)
      return true; 
    if (!(other instanceof TBSQM_CSM_IMPROVE_MASTVO))
      return false; 
    TBSQM_CSM_IMPROVE_MASTVO castOther = (TBSQM_CSM_IMPROVE_MASTVO)other;
    return (new EqualsBuilder()).append(getcomp_id(), castOther.getcomp_id()).isEquals();
  }
  
  public int hashCode() {
    return (new HashCodeBuilder()).append(getcomp_id()).toHashCode();
  }
}
