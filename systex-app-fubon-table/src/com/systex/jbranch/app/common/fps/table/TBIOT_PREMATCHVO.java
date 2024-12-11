package com.systex.jbranch.app.common.fps.table;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;
import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

public class TBIOT_PREMATCHVO extends VOBase {
  private String PREMATCH_SEQ;
  
  private String INS_KIND;
  
  private String INS_ID;
  
  private String CASE_ID;
  
  private String POLICY_NO1;
  
  private String POLICY_NO2;
  
  private String POLICY_NO3;
  
  private String REG_TYPE;
  
  private String OTH_TYPE;
  
  private String BRANCH_NBR;
  
  private String CUST_ID;
  
  private String CUST_RISK;
  
  private Timestamp CUST_RISK_DUE;
  
  private String PROPOSER_NAME;
  
  private Timestamp PROPOSER_BIRTH;
  
  private String INSURED_ID;
  
  private String INSURED_NAME;
  
  private String REPRESET_ID;
  
  private String REPRESET_NAME;
  
  private String RLT_BT_PROREP;
  
  private String PAYER_ID;
  
  private String PAYER_NAME;
  
  private String RLT_BT_PROPAY;
  
  private BigDecimal INSPRD_KEYNO;
  
  private String INSPRD_ID;
  
  private BigDecimal REAL_PREMIUM;
  
  private BigDecimal BASE_PREMIUM;
  
  private String MOP2;
  
  private String LOAN_SOURCE_YN;
  
  private Timestamp APPLY_DATE;
  
  private Timestamp MATCH_DATE;
  
  private String RECRUIT_ID;
  
  private String PROPOSER_CM_FLAG;
  
  private String INSURED_CM_FLAG;
  
  private String PAYER_CM_FLAG;
  
  private String AML;
  
  private String PRECHECK;
  
  private BigDecimal PROPOSER_INCOME1;
  
  private BigDecimal PROPOSER_INCOME2;
  
  private BigDecimal PROPOSER_INCOME3;
  
  private BigDecimal INSURED_INCOME1;
  
  private BigDecimal INSURED_INCOME2;
  
  private BigDecimal INSURED_INCOME3;
  
  private String LOAN_CHK1_YN;
  
  private String LOAN_CHK2_YN;
  
  private String LOAN_CHK3_YN;
  
  private String CD_CHK_YN;
  
  private String C_LOAN_CHK1_YN;
  
  private String C_LOAN_CHK2_YN;
  
  private String C_LOAN_CHK3_YN;
  
  private String C_CD_CHK_YN;
  
  private String I_LOAN_CHK1_YN;
  
  private String I_LOAN_CHK2_YN;
  
  private String I_LOAN_CHK3_YN;
  
  private String I_CD_CHK_YN;
  
  private String INCOME_REMARK;
  
  private String LOAN_SOURCE_REMARK;
  
  private String STATUS;
  
  private String SIGNOFF_ID;
  
  private Timestamp SIGNOFF_DATE;
  
  private String CHG_CUST_ID;
  
  private String CHG_PROPOSER_NAME;
  
  private Timestamp CHG_PROPOSER_BIRTH;
  
  private String AO_CODE;
  
  private String REPRESET_CM_FLAG;
  
  private String CONTRACT_END_YN;
  
  private String S_INFITEM_LOAN_YN;
  
  private String PROPOSER_WORK;
  
  private String INSURED_WORK;
  
  private Timestamp C_LOAN_APPLY_DATE;
  
  private Timestamp C_PREM_DATE;
  
  private Timestamp I_LOAN_APPLY_DATE;
  
  private Timestamp P_LOAN_APPLY_DATE;
  
  private String C_LOAN_APPLY_YN;
  
  private String I_LOAN_APPLY_YN;
  
  private String P_LOAN_APPLY_YN;
  
  private String LOAN_SOURCE2_YN;
  
  private String AB_SENIOR_YN;
  
  private String C_SALE_SENIOR_YN;
  
  private String I_SALE_SENIOR_YN;
  
  private String P_SALE_SENIOR_YN;
  
  private String LOAN_CHK4_YN;
  
  private String C_LOAN_CHK4_YN;
  
  private String I_LOAN_CHK4_YN;
  
  private Timestamp C_LOAN_CHK2_DATE;
  
  private Timestamp I_LOAN_CHK2_DATE;
  
  private Timestamp LOAN_CHK2_DATE;
  
  private String MAPPVIDEO_YN;
  
  private String AGE_UNDER20_YN;
  
  private Timestamp INSURED_BIRTH;
  
  private Timestamp PAYER_BIRTH;
  
  private String INV_SCORE;
  
  private String C_KYC_INCOME;
  
  private String I_KYC_INCOME;
  
  private String SENIOR_AUTH_OPT;
  
  private String SENIOR_AUTH_OPT2;
  
  private String SENIOR_AUTH_OPT3;
  
  private String SENIOR_AUTH_OPT4;
  
  private String C_SALE_SENIOR_TRANSSEQ;
  
  private String I_SALE_SENIOR_TRANSSEQ;
  
  private String P_SALE_SENIOR_TRANSSEQ;
  
  private String INV_SCORE_CHK;
  
  private BigDecimal CUST_DEBIT;
  
  private BigDecimal INSURED_DEBIT;
  
  private String MAPPVIDEO_AGENTMEMO;
  
  private String C_SENIOR_PVAL;
  
  private String DIGITAL_AGREESIGN_YN;
  
  private String FB_COM_YN;
  
  private BigDecimal COMPANY_NUM;
  
  private String AB_YN;
  
  private Timestamp C_CD_DUE_DATE;
  
  private Timestamp I_CD_DUE_DATE;
  
  private Timestamp P_CD_DUE_DATE;
  
  private String CANCEL_CONTRACT_YN;
  
  private String DATA_SHR_YN;
  
  private String SENIOR_OVER_PVAL;
  
  private String WMSHAIA_SEQ;
  
  private BigDecimal OVER_PVAL_AMT;
  
  private BigDecimal OVER_PVAL_MAX_AMT;
  
  private String SENIOR_AUTH_REMARKS;
  
  private String SENIOR_AUTH_ID;
  
  private String C_PREMIUM_TRANSSEQ_YN;
  
  private String I_PREMIUM_TRANSSEQ_YN;
  
  private String P_PREMIUM_TRANSSEQ_YN;
  
  private String C_REVOLVING_LOAN_YN;
  
  private String I_REVOLVING_LOAN_YN;
  
  private String P_REVOLVING_LOAN_YN;
  
  private String BUSINESS_REL;
  
  public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBIOT_PREMATCH";
  
  public String getTableuid() {
    return "com.systex.jbranch.app.common.fps.table.TBIOT_PREMATCH";
  }
  
  public TBIOT_PREMATCHVO(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7, String paramString8, String paramString9, String paramString10, String paramString11, String paramString12, String paramString13, Timestamp paramTimestamp1, String paramString14, String paramString15, String paramString16, String paramString17, String paramString18, String paramString19, String paramString20, String paramString21, BigDecimal paramBigDecimal1, String paramString22, BigDecimal paramBigDecimal2, BigDecimal paramBigDecimal3, String paramString23, String paramString24, Timestamp paramTimestamp2, Timestamp paramTimestamp3, String paramString25, String paramString26, String paramString27, String paramString28, String paramString29, String paramString30, BigDecimal paramBigDecimal4, BigDecimal paramBigDecimal5, BigDecimal paramBigDecimal6, BigDecimal paramBigDecimal7, BigDecimal paramBigDecimal8, BigDecimal paramBigDecimal9, String paramString31, String paramString32, String paramString33, String paramString34, String paramString35, String paramString36, String paramString37, Timestamp paramTimestamp4, String paramString38, String paramString39, Timestamp paramTimestamp5, Timestamp paramTimestamp6, String paramString40, String paramString41, Timestamp paramTimestamp7, Long paramLong, BigDecimal companyNum, String abYN) {
    this.PREMATCH_SEQ = paramString1;
    this.INS_KIND = paramString2;
    this.INS_ID = paramString3;
    this.CASE_ID = paramString4;
    this.POLICY_NO1 = paramString5;
    this.POLICY_NO2 = paramString6;
    this.POLICY_NO3 = paramString7;
    this.REG_TYPE = paramString8;
    this.OTH_TYPE = paramString9;
    this.BRANCH_NBR = paramString10;
    this.CUST_ID = paramString11;
    this.CUST_RISK = paramString12;
    this.PROPOSER_NAME = paramString13;
    this.PROPOSER_BIRTH = paramTimestamp1;
    this.INSURED_ID = paramString14;
    this.INSURED_NAME = paramString15;
    this.REPRESET_ID = paramString16;
    this.REPRESET_NAME = paramString17;
    this.RLT_BT_PROREP = paramString18;
    this.PAYER_ID = paramString19;
    this.PAYER_NAME = paramString20;
    this.RLT_BT_PROPAY = paramString21;
    this.INSPRD_KEYNO = paramBigDecimal1;
    this.INSPRD_ID = paramString22;
    this.REAL_PREMIUM = paramBigDecimal2;
    this.BASE_PREMIUM = paramBigDecimal3;
    this.MOP2 = paramString23;
    this.LOAN_SOURCE_YN = paramString24;
    this.APPLY_DATE = paramTimestamp2;
    this.MATCH_DATE = paramTimestamp3;
    this.RECRUIT_ID = paramString25;
    this.PROPOSER_CM_FLAG = paramString26;
    this.INSURED_CM_FLAG = paramString27;
    this.PAYER_CM_FLAG = paramString28;
    this.AML = paramString29;
    this.PRECHECK = paramString30;
    this.PROPOSER_INCOME1 = paramBigDecimal4;
    this.PROPOSER_INCOME2 = paramBigDecimal5;
    this.PROPOSER_INCOME3 = paramBigDecimal6;
    this.INSURED_INCOME1 = paramBigDecimal7;
    this.INSURED_INCOME2 = paramBigDecimal8;
    this.INSURED_INCOME3 = paramBigDecimal9;
    this.LOAN_CHK1_YN = paramString31;
    this.LOAN_CHK2_YN = paramString32;
    this.CD_CHK_YN = paramString33;
    this.INCOME_REMARK = paramString34;
    this.LOAN_SOURCE_REMARK = paramString35;
    this.STATUS = paramString36;
    this.SIGNOFF_ID = paramString37;
    this.SIGNOFF_DATE = paramTimestamp4;
    this.CHG_CUST_ID = paramString38;
    this.CHG_PROPOSER_NAME = paramString39;
    this.CHG_PROPOSER_BIRTH = paramTimestamp5;
    this.createtime = paramTimestamp6;
    this.creator = paramString40;
    this.modifier = paramString41;
    this.lastupdate = paramTimestamp7;
    this.version = paramLong;
    this.COMPANY_NUM = companyNum;
    this.AB_YN = abYN;
  }
  
  public TBIOT_PREMATCHVO() {}
  
  public TBIOT_PREMATCHVO(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, Timestamp paramTimestamp, String paramString6) {
    this.PREMATCH_SEQ = paramString1;
    this.INS_KIND = paramString2;
    this.REG_TYPE = paramString3;
    this.BRANCH_NBR = paramString4;
    this.CUST_ID = paramString5;
    this.createtime = paramTimestamp;
    this.creator = paramString6;
  }
  
  public String getPREMATCH_SEQ() {
    return this.PREMATCH_SEQ;
  }
  
  public void setPREMATCH_SEQ(String paramString) {
    this.PREMATCH_SEQ = paramString;
  }
  
  public String getINS_KIND() {
    return this.INS_KIND;
  }
  
  public void setINS_KIND(String paramString) {
    this.INS_KIND = paramString;
  }
  
  public String getINS_ID() {
    return this.INS_ID;
  }
  
  public void setINS_ID(String paramString) {
    this.INS_ID = paramString;
  }
  
  public String getCASE_ID() {
    return this.CASE_ID;
  }
  
  public void setCASE_ID(String paramString) {
    this.CASE_ID = paramString;
  }
  
  public String getPOLICY_NO1() {
    return this.POLICY_NO1;
  }
  
  public void setPOLICY_NO1(String paramString) {
    this.POLICY_NO1 = paramString;
  }
  
  public String getPOLICY_NO2() {
    return this.POLICY_NO2;
  }
  
  public void setPOLICY_NO2(String paramString) {
    this.POLICY_NO2 = paramString;
  }
  
  public String getPOLICY_NO3() {
    return this.POLICY_NO3;
  }
  
  public void setPOLICY_NO3(String paramString) {
    this.POLICY_NO3 = paramString;
  }
  
  public String getREG_TYPE() {
    return this.REG_TYPE;
  }
  
  public void setREG_TYPE(String paramString) {
    this.REG_TYPE = paramString;
  }
  
  public String getOTH_TYPE() {
    return this.OTH_TYPE;
  }
  
  public void setOTH_TYPE(String paramString) {
    this.OTH_TYPE = paramString;
  }
  
  public String getBRANCH_NBR() {
    return this.BRANCH_NBR;
  }
  
  public void setBRANCH_NBR(String paramString) {
    this.BRANCH_NBR = paramString;
  }
  
  public String getCUST_ID() {
    return this.CUST_ID;
  }
  
  public void setCUST_ID(String paramString) {
    this.CUST_ID = paramString;
  }
  
  public String getCUST_RISK() {
    return this.CUST_RISK;
  }
  
  public void setCUST_RISK(String paramString) {
    this.CUST_RISK = paramString;
  }
  
  public Timestamp getCUST_RISK_DUE() {
    return this.CUST_RISK_DUE;
  }
  
  public void setCUST_RISK_DUE(Timestamp cUST_RISK_DUE) {
    this.CUST_RISK_DUE = cUST_RISK_DUE;
  }
  
  public String getPROPOSER_NAME() {
    return this.PROPOSER_NAME;
  }
  
  public void setPROPOSER_NAME(String paramString) {
    this.PROPOSER_NAME = paramString;
  }
  
  public Timestamp getPROPOSER_BIRTH() {
    return this.PROPOSER_BIRTH;
  }
  
  public void setPROPOSER_BIRTH(Timestamp paramTimestamp) {
    this.PROPOSER_BIRTH = paramTimestamp;
  }
  
  public String getINSURED_ID() {
    return this.INSURED_ID;
  }
  
  public void setINSURED_ID(String paramString) {
    this.INSURED_ID = paramString;
  }
  
  public String getINSURED_NAME() {
    return this.INSURED_NAME;
  }
  
  public void setINSURED_NAME(String paramString) {
    this.INSURED_NAME = paramString;
  }
  
  public String getREPRESET_ID() {
    return this.REPRESET_ID;
  }
  
  public void setREPRESET_ID(String paramString) {
    this.REPRESET_ID = paramString;
  }
  
  public String getREPRESET_NAME() {
    return this.REPRESET_NAME;
  }
  
  public void setREPRESET_NAME(String paramString) {
    this.REPRESET_NAME = paramString;
  }
  
  public String getRLT_BT_PROREP() {
    return this.RLT_BT_PROREP;
  }
  
  public void setRLT_BT_PROREP(String paramString) {
    this.RLT_BT_PROREP = paramString;
  }
  
  public String getPAYER_ID() {
    return this.PAYER_ID;
  }
  
  public void setPAYER_ID(String paramString) {
    this.PAYER_ID = paramString;
  }
  
  public String getPAYER_NAME() {
    return this.PAYER_NAME;
  }
  
  public void setPAYER_NAME(String paramString) {
    this.PAYER_NAME = paramString;
  }
  
  public String getRLT_BT_PROPAY() {
    return this.RLT_BT_PROPAY;
  }
  
  public void setRLT_BT_PROPAY(String paramString) {
    this.RLT_BT_PROPAY = paramString;
  }
  
  public BigDecimal getINSPRD_KEYNO() {
    return this.INSPRD_KEYNO;
  }
  
  public void setINSPRD_KEYNO(BigDecimal paramBigDecimal) {
    this.INSPRD_KEYNO = paramBigDecimal;
  }
  
  public String getINSPRD_ID() {
    return this.INSPRD_ID;
  }
  
  public void setINSPRD_ID(String paramString) {
    this.INSPRD_ID = paramString;
  }
  
  public BigDecimal getREAL_PREMIUM() {
    return this.REAL_PREMIUM;
  }
  
  public void setREAL_PREMIUM(BigDecimal paramBigDecimal) {
    this.REAL_PREMIUM = paramBigDecimal;
  }
  
  public BigDecimal getBASE_PREMIUM() {
    return this.BASE_PREMIUM;
  }
  
  public void setBASE_PREMIUM(BigDecimal paramBigDecimal) {
    this.BASE_PREMIUM = paramBigDecimal;
  }
  
  public String getMOP2() {
    return this.MOP2;
  }
  
  public void setMOP2(String paramString) {
    this.MOP2 = paramString;
  }
  
  public String getLOAN_SOURCE_YN() {
    return this.LOAN_SOURCE_YN;
  }
  
  public void setLOAN_SOURCE_YN(String paramString) {
    this.LOAN_SOURCE_YN = paramString;
  }
  
  public Timestamp getAPPLY_DATE() {
    return this.APPLY_DATE;
  }
  
  public void setAPPLY_DATE(Timestamp paramTimestamp) {
    this.APPLY_DATE = paramTimestamp;
  }
  
  public Timestamp getMATCH_DATE() {
    return this.MATCH_DATE;
  }
  
  public void setMATCH_DATE(Timestamp paramTimestamp) {
    this.MATCH_DATE = paramTimestamp;
  }
  
  public String getRECRUIT_ID() {
    return this.RECRUIT_ID;
  }
  
  public void setRECRUIT_ID(String paramString) {
    this.RECRUIT_ID = paramString;
  }
  
  public String getPROPOSER_CM_FLAG() {
    return this.PROPOSER_CM_FLAG;
  }
  
  public void setPROPOSER_CM_FLAG(String paramString) {
    this.PROPOSER_CM_FLAG = paramString;
  }
  
  public String getINSURED_CM_FLAG() {
    return this.INSURED_CM_FLAG;
  }
  
  public void setINSURED_CM_FLAG(String paramString) {
    this.INSURED_CM_FLAG = paramString;
  }
  
  public String getPAYER_CM_FLAG() {
    return this.PAYER_CM_FLAG;
  }
  
  public void setPAYER_CM_FLAG(String paramString) {
    this.PAYER_CM_FLAG = paramString;
  }
  
  public String getAML() {
    return this.AML;
  }
  
  public void setAML(String paramString) {
    this.AML = paramString;
  }
  
  public String getPRECHECK() {
    return this.PRECHECK;
  }
  
  public void setPRECHECK(String pRECHECK) {
    this.PRECHECK = pRECHECK;
  }
  
  public BigDecimal getPROPOSER_INCOME1() {
    return this.PROPOSER_INCOME1;
  }
  
  public void setPROPOSER_INCOME1(BigDecimal paramBigDecimal) {
    this.PROPOSER_INCOME1 = paramBigDecimal;
  }
  
  public BigDecimal getPROPOSER_INCOME2() {
    return this.PROPOSER_INCOME2;
  }
  
  public void setPROPOSER_INCOME2(BigDecimal paramBigDecimal) {
    this.PROPOSER_INCOME2 = paramBigDecimal;
  }
  
  public BigDecimal getPROPOSER_INCOME3() {
    return this.PROPOSER_INCOME3;
  }
  
  public void setPROPOSER_INCOME3(BigDecimal paramBigDecimal) {
    this.PROPOSER_INCOME3 = paramBigDecimal;
  }
  
  public BigDecimal getINSURED_INCOME1() {
    return this.INSURED_INCOME1;
  }
  
  public void setINSURED_INCOME1(BigDecimal paramBigDecimal) {
    this.INSURED_INCOME1 = paramBigDecimal;
  }
  
  public BigDecimal getINSURED_INCOME2() {
    return this.INSURED_INCOME2;
  }
  
  public void setINSURED_INCOME2(BigDecimal paramBigDecimal) {
    this.INSURED_INCOME2 = paramBigDecimal;
  }
  
  public BigDecimal getINSURED_INCOME3() {
    return this.INSURED_INCOME3;
  }
  
  public void setINSURED_INCOME3(BigDecimal paramBigDecimal) {
    this.INSURED_INCOME3 = paramBigDecimal;
  }
  
  public String getLOAN_CHK1_YN() {
    return this.LOAN_CHK1_YN;
  }
  
  public void setLOAN_CHK1_YN(String paramString) {
    this.LOAN_CHK1_YN = paramString;
  }
  
  public String getLOAN_CHK2_YN() {
    return this.LOAN_CHK2_YN;
  }
  
  public void setLOAN_CHK2_YN(String paramString) {
    this.LOAN_CHK2_YN = paramString;
  }
  
  public String getCD_CHK_YN() {
    return this.CD_CHK_YN;
  }
  
  public void setCD_CHK_YN(String paramString) {
    this.CD_CHK_YN = paramString;
  }
  
  public String getLOAN_CHK3_YN() {
    return this.LOAN_CHK3_YN;
  }
  
  public void setLOAN_CHK3_YN(String lOAN_CHK3_YN) {
    this.LOAN_CHK3_YN = lOAN_CHK3_YN;
  }
  
  public String getC_LOAN_CHK1_YN() {
    return this.C_LOAN_CHK1_YN;
  }
  
  public void setC_LOAN_CHK1_YN(String c_LOAN_CHK1_YN) {
    this.C_LOAN_CHK1_YN = c_LOAN_CHK1_YN;
  }
  
  public String getC_LOAN_CHK2_YN() {
    return this.C_LOAN_CHK2_YN;
  }
  
  public void setC_LOAN_CHK2_YN(String c_LOAN_CHK2_YN) {
    this.C_LOAN_CHK2_YN = c_LOAN_CHK2_YN;
  }
  
  public String getC_LOAN_CHK3_YN() {
    return this.C_LOAN_CHK3_YN;
  }
  
  public void setC_LOAN_CHK3_YN(String c_LOAN_CHK3_YN) {
    this.C_LOAN_CHK3_YN = c_LOAN_CHK3_YN;
  }
  
  public String getC_CD_CHK_YN() {
    return this.C_CD_CHK_YN;
  }
  
  public void setC_CD_CHK_YN(String c_CD_CHK_YN) {
    this.C_CD_CHK_YN = c_CD_CHK_YN;
  }
  
  public String getI_LOAN_CHK1_YN() {
    return this.I_LOAN_CHK1_YN;
  }
  
  public void setI_LOAN_CHK1_YN(String i_LOAN_CHK1_YN) {
    this.I_LOAN_CHK1_YN = i_LOAN_CHK1_YN;
  }
  
  public String getI_LOAN_CHK2_YN() {
    return this.I_LOAN_CHK2_YN;
  }
  
  public void setI_LOAN_CHK2_YN(String i_LOAN_CHK2_YN) {
    this.I_LOAN_CHK2_YN = i_LOAN_CHK2_YN;
  }
  
  public String getI_LOAN_CHK3_YN() {
    return this.I_LOAN_CHK3_YN;
  }
  
  public void setI_LOAN_CHK3_YN(String i_LOAN_CHK3_YN) {
    this.I_LOAN_CHK3_YN = i_LOAN_CHK3_YN;
  }
  
  public String getI_CD_CHK_YN() {
    return this.I_CD_CHK_YN;
  }
  
  public void setI_CD_CHK_YN(String i_CD_CHK_YN) {
    this.I_CD_CHK_YN = i_CD_CHK_YN;
  }
  
  public String getINCOME_REMARK() {
    return this.INCOME_REMARK;
  }
  
  public void setINCOME_REMARK(String paramString) {
    this.INCOME_REMARK = paramString;
  }
  
  public String getLOAN_SOURCE_REMARK() {
    return this.LOAN_SOURCE_REMARK;
  }
  
  public void setLOAN_SOURCE_REMARK(String paramString) {
    this.LOAN_SOURCE_REMARK = paramString;
  }
  
  public String getSTATUS() {
    return this.STATUS;
  }
  
  public void setSTATUS(String paramString) {
    this.STATUS = paramString;
  }
  
  public String getSIGNOFF_ID() {
    return this.SIGNOFF_ID;
  }
  
  public void setSIGNOFF_ID(String paramString) {
    this.SIGNOFF_ID = paramString;
  }
  
  public Timestamp getSIGNOFF_DATE() {
    return this.SIGNOFF_DATE;
  }
  
  public void setSIGNOFF_DATE(Timestamp paramTimestamp) {
    this.SIGNOFF_DATE = paramTimestamp;
  }
  
  public String getCHG_CUST_ID() {
    return this.CHG_CUST_ID;
  }
  
  public void setCHG_CUST_ID(String paramString) {
    this.CHG_CUST_ID = paramString;
  }
  
  public String getCHG_PROPOSER_NAME() {
    return this.CHG_PROPOSER_NAME;
  }
  
  public void setCHG_PROPOSER_NAME(String paramString) {
    this.CHG_PROPOSER_NAME = paramString;
  }
  
  public Timestamp getCHG_PROPOSER_BIRTH() {
    return this.CHG_PROPOSER_BIRTH;
  }
  
  public void setCHG_PROPOSER_BIRTH(Timestamp paramTimestamp) {
    this.CHG_PROPOSER_BIRTH = paramTimestamp;
  }
  
  public void checkDefaultValue() {}
  
  public String toString() {
    return (new ToStringBuilder(this)).append("PREMATCH_SEQ", getPREMATCH_SEQ()).toString();
  }
  
  public String getAO_CODE() {
    return this.AO_CODE;
  }
  
  public void setAO_CODE(String aO_CODE) {
    this.AO_CODE = aO_CODE;
  }
  
  public String getREPRESET_CM_FLAG() {
    return this.REPRESET_CM_FLAG;
  }
  
  public void setREPRESET_CM_FLAG(String rEPRESET_CM_FLAG) {
    this.REPRESET_CM_FLAG = rEPRESET_CM_FLAG;
  }
  
  public String getCONTRACT_END_YN() {
    return this.CONTRACT_END_YN;
  }
  
  public void setCONTRACT_END_YN(String cONTRACT_END_YN) {
    this.CONTRACT_END_YN = cONTRACT_END_YN;
  }
  
  public String getS_INFITEM_LOAN_YN() {
    return this.S_INFITEM_LOAN_YN;
  }
  
  public void setS_INFITEM_LOAN_YN(String s_INFITEM_LOAN_YN) {
    this.S_INFITEM_LOAN_YN = s_INFITEM_LOAN_YN;
  }
  
  public String getPROPOSER_WORK() {
    return this.PROPOSER_WORK;
  }
  
  public void setPROPOSER_WORK(String pROPOSER_WORK) {
    this.PROPOSER_WORK = pROPOSER_WORK;
  }
  
  public String getINSURED_WORK() {
    return this.INSURED_WORK;
  }
  
  public void setINSURED_WORK(String iNSURED_WORK) {
    this.INSURED_WORK = iNSURED_WORK;
  }
  
  public Timestamp getC_LOAN_APPLY_DATE() {
    return this.C_LOAN_APPLY_DATE;
  }
  
  public void setC_LOAN_APPLY_DATE(Timestamp c_LOAN_APPLY_DATE) {
    this.C_LOAN_APPLY_DATE = c_LOAN_APPLY_DATE;
  }
  
  public Timestamp getC_PREM_DATE() {
    return this.C_PREM_DATE;
  }
  
  public void setC_PREM_DATE(Timestamp c_PREM_DATE) {
    this.C_PREM_DATE = c_PREM_DATE;
  }
  
  public Timestamp getI_LOAN_APPLY_DATE() {
    return this.I_LOAN_APPLY_DATE;
  }
  
  public void setI_LOAN_APPLY_DATE(Timestamp i_LOAN_APPLY_DATE) {
    this.I_LOAN_APPLY_DATE = i_LOAN_APPLY_DATE;
  }
  
  public Timestamp getP_LOAN_APPLY_DATE() {
    return this.P_LOAN_APPLY_DATE;
  }
  
  public void setP_LOAN_APPLY_DATE(Timestamp p_LOAN_APPLY_DATE) {
    this.P_LOAN_APPLY_DATE = p_LOAN_APPLY_DATE;
  }
  
  public String getC_LOAN_APPLY_YN() {
    return this.C_LOAN_APPLY_YN;
  }
  
  public void setC_LOAN_APPLY_YN(String c_LOAN_APPLY_YN) {
    this.C_LOAN_APPLY_YN = c_LOAN_APPLY_YN;
  }
  
  public String getI_LOAN_APPLY_YN() {
    return this.I_LOAN_APPLY_YN;
  }
  
  public void setI_LOAN_APPLY_YN(String i_LOAN_APPLY_YN) {
    this.I_LOAN_APPLY_YN = i_LOAN_APPLY_YN;
  }
  
  public String getP_LOAN_APPLY_YN() {
    return this.P_LOAN_APPLY_YN;
  }
  
  public void setP_LOAN_APPLY_YN(String p_LOAN_APPLY_YN) {
    this.P_LOAN_APPLY_YN = p_LOAN_APPLY_YN;
  }
  
  public String getLOAN_SOURCE2_YN() {
    return this.LOAN_SOURCE2_YN;
  }
  
  public void setLOAN_SOURCE2_YN(String lOAN_SOURCE2_YN) {
    this.LOAN_SOURCE2_YN = lOAN_SOURCE2_YN;
  }
  
  public String getAB_SENIOR_YN() {
    return this.AB_SENIOR_YN;
  }
  
  public void setAB_SENIOR_YN(String aB_SENIOR_YN) {
    this.AB_SENIOR_YN = aB_SENIOR_YN;
  }
  
  public String getC_SALE_SENIOR_YN() {
    return this.C_SALE_SENIOR_YN;
  }
  
  public void setC_SALE_SENIOR_YN(String c_SALE_SENIOR_YN) {
    this.C_SALE_SENIOR_YN = c_SALE_SENIOR_YN;
  }
  
  public String getI_SALE_SENIOR_YN() {
    return this.I_SALE_SENIOR_YN;
  }
  
  public void setI_SALE_SENIOR_YN(String i_SALE_SENIOR_YN) {
    this.I_SALE_SENIOR_YN = i_SALE_SENIOR_YN;
  }
  
  public String getP_SALE_SENIOR_YN() {
    return this.P_SALE_SENIOR_YN;
  }
  
  public void setP_SALE_SENIOR_YN(String p_SALE_SENIOR_YN) {
    this.P_SALE_SENIOR_YN = p_SALE_SENIOR_YN;
  }
  
  public String getLOAN_CHK4_YN() {
    return this.LOAN_CHK4_YN;
  }
  
  public void setLOAN_CHK4_YN(String lOAN_CHK4_YN) {
    this.LOAN_CHK4_YN = lOAN_CHK4_YN;
  }
  
  public String getC_LOAN_CHK4_YN() {
    return this.C_LOAN_CHK4_YN;
  }
  
  public void setC_LOAN_CHK4_YN(String c_LOAN_CHK4_YN) {
    this.C_LOAN_CHK4_YN = c_LOAN_CHK4_YN;
  }
  
  public String getI_LOAN_CHK4_YN() {
    return this.I_LOAN_CHK4_YN;
  }
  
  public void setI_LOAN_CHK4_YN(String i_LOAN_CHK4_YN) {
    this.I_LOAN_CHK4_YN = i_LOAN_CHK4_YN;
  }
  
  public Timestamp getC_LOAN_CHK2_DATE() {
    return this.C_LOAN_CHK2_DATE;
  }
  
  public void setC_LOAN_CHK2_DATE(Timestamp c_LOAN_CHK2_DATE) {
    this.C_LOAN_CHK2_DATE = c_LOAN_CHK2_DATE;
  }
  
  public Timestamp getI_LOAN_CHK2_DATE() {
    return this.I_LOAN_CHK2_DATE;
  }
  
  public void setI_LOAN_CHK2_DATE(Timestamp i_LOAN_CHK2_DATE) {
    this.I_LOAN_CHK2_DATE = i_LOAN_CHK2_DATE;
  }
  
  public Timestamp getLOAN_CHK2_DATE() {
    return this.LOAN_CHK2_DATE;
  }
  
  public void setLOAN_CHK2_DATE(Timestamp lOAN_CHK2_DATE) {
    this.LOAN_CHK2_DATE = lOAN_CHK2_DATE;
  }
  
  public String getMAPPVIDEO_YN() {
    return this.MAPPVIDEO_YN;
  }
  
  public void setMAPPVIDEO_YN(String mAPPVIDEO_YN) {
    this.MAPPVIDEO_YN = mAPPVIDEO_YN;
  }
  
  public String getAGE_UNDER20_YN() {
    return this.AGE_UNDER20_YN;
  }
  
  public void setAGE_UNDER20_YN(String aGE_UNDER20_YN) {
    this.AGE_UNDER20_YN = aGE_UNDER20_YN;
  }
  
  public Timestamp getINSURED_BIRTH() {
    return this.INSURED_BIRTH;
  }
  
  public void setINSURED_BIRTH(Timestamp iNSURED_BIRTH) {
    this.INSURED_BIRTH = iNSURED_BIRTH;
  }
  
  public Timestamp getPAYER_BIRTH() {
    return this.PAYER_BIRTH;
  }
  
  public void setPAYER_BIRTH(Timestamp pAYER_BIRTH) {
    this.PAYER_BIRTH = pAYER_BIRTH;
  }
  
  public String getINV_SCORE() {
    return this.INV_SCORE;
  }
  
  public void setINV_SCORE(String iNV_SCORE) {
    this.INV_SCORE = iNV_SCORE;
  }
  
  public String getC_KYC_INCOME() {
    return this.C_KYC_INCOME;
  }
  
  public void setC_KYC_INCOME(String c_KYC_INCOME) {
    this.C_KYC_INCOME = c_KYC_INCOME;
  }
  
  public String getI_KYC_INCOME() {
    return this.I_KYC_INCOME;
  }
  
  public void setI_KYC_INCOME(String i_KYC_INCOME) {
    this.I_KYC_INCOME = i_KYC_INCOME;
  }
  
  public String getSENIOR_AUTH_OPT() {
    return this.SENIOR_AUTH_OPT;
  }
  
  public void setSENIOR_AUTH_OPT(String sENIOR_AUTH_OPT) {
    this.SENIOR_AUTH_OPT = sENIOR_AUTH_OPT;
  }
  
  public String getSENIOR_AUTH_OPT2() {
    return this.SENIOR_AUTH_OPT2;
  }
  
  public void setSENIOR_AUTH_OPT2(String sENIOR_AUTH_OPT2) {
    this.SENIOR_AUTH_OPT2 = sENIOR_AUTH_OPT2;
  }
  
  public String getSENIOR_AUTH_OPT3() {
    return this.SENIOR_AUTH_OPT3;
  }
  
  public void setSENIOR_AUTH_OPT3(String sENIOR_AUTH_OPT3) {
    this.SENIOR_AUTH_OPT3 = sENIOR_AUTH_OPT3;
  }
  
  public String getSENIOR_AUTH_OPT4() {
    return this.SENIOR_AUTH_OPT4;
  }
  
  public void setSENIOR_AUTH_OPT4(String sENIOR_AUTH_OPT4) {
    this.SENIOR_AUTH_OPT4 = sENIOR_AUTH_OPT4;
  }
  
  public String getC_SALE_SENIOR_TRANSSEQ() {
    return this.C_SALE_SENIOR_TRANSSEQ;
  }
  
  public void setC_SALE_SENIOR_TRANSSEQ(String c_SALE_SENIOR_TRANSSEQ) {
    this.C_SALE_SENIOR_TRANSSEQ = c_SALE_SENIOR_TRANSSEQ;
  }
  
  public String getI_SALE_SENIOR_TRANSSEQ() {
    return this.I_SALE_SENIOR_TRANSSEQ;
  }
  
  public void setI_SALE_SENIOR_TRANSSEQ(String i_SALE_SENIOR_TRANSSEQ) {
    this.I_SALE_SENIOR_TRANSSEQ = i_SALE_SENIOR_TRANSSEQ;
  }
  
  public String getP_SALE_SENIOR_TRANSSEQ() {
    return this.P_SALE_SENIOR_TRANSSEQ;
  }
  
  public void setP_SALE_SENIOR_TRANSSEQ(String p_SALE_SENIOR_TRANSSEQ) {
    this.P_SALE_SENIOR_TRANSSEQ = p_SALE_SENIOR_TRANSSEQ;
  }
  
  public String getINV_SCORE_CHK() {
    return this.INV_SCORE_CHK;
  }
  
  public void setINV_SCORE_CHK(String iNV_SCORE_CHK) {
    this.INV_SCORE_CHK = iNV_SCORE_CHK;
  }
  
  public BigDecimal getCUST_DEBIT() {
    return this.CUST_DEBIT;
  }
  
  public void setCUST_DEBIT(BigDecimal cUST_DEBIT) {
    this.CUST_DEBIT = cUST_DEBIT;
  }
  
  public BigDecimal getINSURED_DEBIT() {
    return this.INSURED_DEBIT;
  }
  
  public void setINSURED_DEBIT(BigDecimal iNSURED_DEBIT) {
    this.INSURED_DEBIT = iNSURED_DEBIT;
  }
  
  public String getMAPPVIDEO_AGENTMEMO() {
    return this.MAPPVIDEO_AGENTMEMO;
  }
  
  public void setMAPPVIDEO_AGENTMEMO(String mAPPVIDEO_AGENTMEMO) {
    this.MAPPVIDEO_AGENTMEMO = mAPPVIDEO_AGENTMEMO;
  }
  
  public String getC_SENIOR_PVAL() {
    return this.C_SENIOR_PVAL;
  }
  
  public void setC_SENIOR_PVAL(String c_SENIOR_PVAL) {
    this.C_SENIOR_PVAL = c_SENIOR_PVAL;
  }
  
  public String getDIGITAL_AGREESIGN_YN() {
    return this.DIGITAL_AGREESIGN_YN;
  }
  
  public void setDIGITAL_AGREESIGN_YN(String dIGITAL_AGREESIGN_YN) {
    this.DIGITAL_AGREESIGN_YN = dIGITAL_AGREESIGN_YN;
  }
  
  public String getFB_COM_YN() {
    return this.FB_COM_YN;
  }
  
  public void setFB_COM_YN(String fB_COM_YN) {
    this.FB_COM_YN = fB_COM_YN;
  }
  
  public BigDecimal getCOMPANY_NUM() {
    return this.COMPANY_NUM;
  }
  
  public void setCOMPANY_NUM(BigDecimal COMPANY_NUM) {
    this.COMPANY_NUM = COMPANY_NUM;
  }
  
  public String getAB_YN() {
    return this.AB_YN;
  }
  
  public void setAB_YN(String aB_YN) {
    this.AB_YN = aB_YN;
  }
  
  public Timestamp getC_CD_DUE_DATE() {
    return this.C_CD_DUE_DATE;
  }
  
  public void setC_CD_DUE_DATE(Timestamp c_CD_DUE_DATE) {
    this.C_CD_DUE_DATE = c_CD_DUE_DATE;
  }
  
  public Timestamp getI_CD_DUE_DATE() {
    return this.I_CD_DUE_DATE;
  }
  
  public void setI_CD_DUE_DATE(Timestamp i_CD_DUE_DATE) {
    this.I_CD_DUE_DATE = i_CD_DUE_DATE;
  }
  
  public Timestamp getP_CD_DUE_DATE() {
    return this.P_CD_DUE_DATE;
  }
  
  public void setP_CD_DUE_DATE(Timestamp p_CD_DUE_DATE) {
    this.P_CD_DUE_DATE = p_CD_DUE_DATE;
  }
  
  public String getCANCEL_CONTRACT_YN() {
    return this.CANCEL_CONTRACT_YN;
  }
  
  public void setCANCEL_CONTRACT_YN(String cANCEL_CONTRACT_YN) {
    this.CANCEL_CONTRACT_YN = cANCEL_CONTRACT_YN;
  }
  
  public String getDATA_SHR_YN() {
    return this.DATA_SHR_YN;
  }
  
  public void setDATA_SHR_YN(String dATA_SHR_YN) {
    this.DATA_SHR_YN = dATA_SHR_YN;
  }
  
  public String getSENIOR_OVER_PVAL() {
    return this.SENIOR_OVER_PVAL;
  }
  
  public void setSENIOR_OVER_PVAL(String sENIOR_OVER_PVAL) {
    this.SENIOR_OVER_PVAL = sENIOR_OVER_PVAL;
  }
  
  public String getWMSHAIA_SEQ() {
    return this.WMSHAIA_SEQ;
  }
  
  public void setWMSHAIA_SEQ(String wMSHAIA_SEQ) {
    this.WMSHAIA_SEQ = wMSHAIA_SEQ;
  }
  
  public BigDecimal getOVER_PVAL_AMT() {
    return this.OVER_PVAL_AMT;
  }
  
  public void setOVER_PVAL_AMT(BigDecimal oVER_PVAL_AMT) {
    this.OVER_PVAL_AMT = oVER_PVAL_AMT;
  }
  
  public BigDecimal getOVER_PVAL_MAX_AMT() {
    return this.OVER_PVAL_MAX_AMT;
  }
  
  public void setOVER_PVAL_MAX_AMT(BigDecimal oVER_PVAL_MAX_AMT) {
    this.OVER_PVAL_MAX_AMT = oVER_PVAL_MAX_AMT;
  }
  
  public String getSENIOR_AUTH_REMARKS() {
    return this.SENIOR_AUTH_REMARKS;
  }
  
  public void setSENIOR_AUTH_REMARKS(String sENIOR_AUTH_REMARKS) {
    this.SENIOR_AUTH_REMARKS = sENIOR_AUTH_REMARKS;
  }
  
  public String getSENIOR_AUTH_ID() {
    return this.SENIOR_AUTH_ID;
  }
  
  public void setSENIOR_AUTH_ID(String sENIOR_AUTH_ID) {
    this.SENIOR_AUTH_ID = sENIOR_AUTH_ID;
  }
  
  public String getC_PREMIUM_TRANSSEQ_YN() {
    return this.C_PREMIUM_TRANSSEQ_YN;
  }
  
  public void setC_PREMIUM_TRANSSEQ_YN(String c_PREMIUM_TRANSSEQ_YN) {
    this.C_PREMIUM_TRANSSEQ_YN = c_PREMIUM_TRANSSEQ_YN;
  }
  
  public String getI_PREMIUM_TRANSSEQ_YN() {
    return this.I_PREMIUM_TRANSSEQ_YN;
  }
  
  public void setI_PREMIUM_TRANSSEQ_YN(String i_PREMIUM_TRANSSEQ_YN) {
    this.I_PREMIUM_TRANSSEQ_YN = i_PREMIUM_TRANSSEQ_YN;
  }
  
  public String getP_PREMIUM_TRANSSEQ_YN() {
    return this.P_PREMIUM_TRANSSEQ_YN;
  }
  
  public void setP_PREMIUM_TRANSSEQ_YN(String p_PREMIUM_TRANSSEQ_YN) {
    this.P_PREMIUM_TRANSSEQ_YN = p_PREMIUM_TRANSSEQ_YN;
  }
  
  public String getC_REVOLVING_LOAN_YN() {
    return this.C_REVOLVING_LOAN_YN;
  }
  
  public void setC_REVOLVING_LOAN_YN(String c_REVOLVING_LOAN_YN) {
    this.C_REVOLVING_LOAN_YN = c_REVOLVING_LOAN_YN;
  }
  
  public String getI_REVOLVING_LOAN_YN() {
    return this.I_REVOLVING_LOAN_YN;
  }
  
  public void setI_REVOLVING_LOAN_YN(String i_REVOLVING_LOAN_YN) {
    this.I_REVOLVING_LOAN_YN = i_REVOLVING_LOAN_YN;
  }
  
  public String getP_REVOLVING_LOAN_YN() {
    return this.P_REVOLVING_LOAN_YN;
  }
  
  public void setP_REVOLVING_LOAN_YN(String p_REVOLVING_LOAN_YN) {
    this.P_REVOLVING_LOAN_YN = p_REVOLVING_LOAN_YN;
  }
  
  public String getBUSINESS_REL() {
    return this.BUSINESS_REL;
  }
  
  public void setBUSINESS_REL(String bUSINESS_REL) {
    this.BUSINESS_REL = bUSINESS_REL;
  }
}
