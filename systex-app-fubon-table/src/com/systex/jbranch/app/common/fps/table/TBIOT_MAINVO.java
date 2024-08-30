package com.systex.jbranch.app.common.fps.table;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;
import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

public class TBIOT_MAINVO
  extends VOBase
{
  private BigDecimal INS_KEYNO;
  private String INS_KIND;
  private String INS_ID;
  private String POLICY_NO1;
  private String POLICY_NO2;
  private String POLICY_NO3;
  private String REG_TYPE;
  private String OTH_TYPE;
  private String BRANCH_NBR;
  private String CUST_ID;
  private String PROPOSER_NAME;
  private Timestamp PROPOSER_BIRTH;
  private String INSURED_ID;
  private String INSURED_NAME;
  private String REPRESET_ID;
  private String RLT_BT_PROREP;
  private String AO_CODE;
  private String PROPOSER_FATCA_FLAG;
  private String PROPOSER_CM_FLAG;
  private String INSURED_CM_FLAG;
  private String REPRESET_CM_FLAG;
  private BigDecimal INSPRD_KEYNO;
  private BigDecimal FXD_KEYNO;
  private Timestamp APPLY_DATE;
  private Timestamp KEYIN_DATE;
  private Timestamp MATCH_DATE;
  private String PROPOSER_RISK;
  private Timestamp OP_DATE;
  private BigDecimal BATCH_INFO_KEYNO;
  private BigDecimal REAL_PREMIUM;
  private BigDecimal BASE_PREMIUM;
  private String MOP2;
  private String FIRST_PAY_WAY;
  private String RECRUIT_ID;
  private String REF_CON_ID;
  private String IS_EMP;
  private String WRITE_REASON;
  private String WRITE_REASON_OTH;
  private String REJ_REASON;
  private String REJ_REASON_OTH;
  private BigDecimal STATUS;
  private String TERMINATED_INC;
  private String QC_ADD;
  private String QC_ERASER;
  private String QC_ANC_DOC;
  private String QC_STAMP;
  private String AB_TRANSSEQ;
  private String PROPOSER_TRANSSEQ;
  private String INSURED_TRANSSEQ;
  private String SIGN_INC;
  private String DELETE_OPRID;
  private Timestamp DELETE_DATE;
  private String BEF_SIGN_NO;
  private String BEF_SIGN_OPRID;
  private Timestamp BEF_SIGN_DATE;
  private String SIGN_NO;
  private String SIGN_OPRID;
  private Timestamp SIGN_DATE;
  private String AFT_SIGN_OPRID;
  private Timestamp AFT_SIGN_DATE;
  private String REMARK_BANK;
  private String REMARK_INS;
  private String CASE_ID;
  private Timestamp INS_SUBMIT_DATE;
  private String PREMATCH_SEQ;
  private Timestamp GUILD_RPT_DATE;
  private String LOAN_PRD_YN;
  private String QC_IMMI;
  private String NOT_PASS_REASON;
  private String PREMIUM_TRANSSEQ;
  private String I_PREMIUM_TRANSSEQ;
  private String P_PREMIUM_TRANSSEQ;
  private String QC_PROPOSER_CHG;
  private String PREMIUM_USAGE;
  private String PAY_WAY;
  private String PAYER_ID;
  private String RLT_BT_PROPAY;
  private Timestamp DOC_KEYIN_DATE;
  private String LOAN_SOURCE_YN;
  private String VALID_CHG_CUST_YN;
  private String SALE_SENIOR_TRANSSEQ;	//高齡銷售過程錄音序號
  private String INS_SIGN_OPRID; 		//人壽系統簽署員編
  private Timestamp INS_SIGN_DATE;		//人壽系統簽署日期
  private String INS_SIGN_YN;			//人壽系統簽署結果
  private String QC_APEC;
  private String QC_LOAN_CHK;
  private String QC_SIGNATURE;

  private String NOT_FB_BATCH_SEQ;
  private Timestamp NOT_FB_BATCH_DATE;
  private Timestamp NOT_FB_SIGN_DATE;
  private String NOT_FB_OP_NAME;

  private String FB_COM_YN;
  private BigDecimal COMPANY_NUM;
  private String REVISE_CONFIRM_YN;

  public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBIOT_MAIN";

  public String getTableuid()
  {
    return "com.systex.jbranch.app.common.fps.table.TBIOT_MAIN";
  }

  public TBIOT_MAINVO(BigDecimal INS_KEYNO, String INS_KIND, String INS_ID, String POLICY_NO1, String POLICY_NO2, String POLICY_NO3, String REG_TYPE, String OTH_TYPE, String BRANCH_NBR, String CUST_ID, String PROPOSER_NAME, Timestamp PROPOSER_BIRTH, String INSURED_ID, String INSURED_NAME, String REPRESET_ID, String RLT_BT_PROREP, String AO_CODE, String PROPOSER_FATCA_FLAG, String PROPOSER_CM_FLAG, String INSURED_CM_FLAG, String REPRESET_CM_FLAG, BigDecimal INSPRD_KEYNO, BigDecimal FXD_KEYNO, Timestamp APPLY_DATE, Timestamp KEYIN_DATE, Timestamp MATCH_DATE, String PROPOSER_RISK, Timestamp OP_DATE, BigDecimal BATCH_INFO_KEYNO, BigDecimal REAL_PREMIUM, BigDecimal BASE_PREMIUM, String MOP2, String FIRST_PAY_WAY, String RECRUIT_ID, String REF_CON_ID, String IS_EMP, String WRITE_REASON, String WRITE_REASON_OTH, String REJ_REASON, String REJ_REASON_OTH, BigDecimal STATUS, String TERMINATED_INC, String QC_ADD, String QC_ERASER, String QC_ANC_DOC, String QC_STAMP, String AB_TRANSSEQ, String PROPOSER_TRANSSEQ, String INSURED_TRANSSEQ, String SIGN_INC, String DELETE_OPRID, Timestamp DELETE_DATE, String BEF_SIGN_NO, String BEF_SIGN_OPRID, Timestamp BEF_SIGN_DATE, String SIGN_NO, String SIGN_OPRID, Timestamp SIGN_DATE, String AFT_SIGN_OPRID, Timestamp AFT_SIGN_DATE, String REMARK_BANK, String REMARK_INS, String CASE_ID, Timestamp INS_SUBMIT_DATE, String PREMATCH_SEQ, Timestamp GUILD_RPT_DATE, String LOAN_PRD_YN, String QC_IMMI, String NOT_PASS_REASON, String PREMIUM_TRANSSEQ, String i_PREMIUM_TRANSSEQ, String p_PREMIUM_TRANSSEQ, String QC_PROPOSER_CHG, String PREMIUM_USAGE, String PAY_WAY, String PAYER_ID, String RLT_BT_PROPAY, Timestamp DOC_KEYIN_DATE, String LOAN_SOURCE_YN, String VALID_CHG_CUST_YN, String SALE_SENIOR_TRANSSEQ, String INS_SIGN_OPRID, Timestamp INS_SIGN_DATE, String INS_SIGN_YN, String QC_APEC, String QC_LOAN_CHK, String QC_SIGNATURE, String NOT_FB_BATCH_SEQ, Timestamp NOT_FB_BATCH_DATE, Timestamp NOT_FB_SIGN_DATE, String NOT_FB_OP_NAME, String FB_COM_YN, BigDecimal COMPANY_NUM) {
    this.INS_KEYNO = INS_KEYNO;
    this.INS_KIND = INS_KIND;
    this.INS_ID = INS_ID;
    this.POLICY_NO1 = POLICY_NO1;
    this.POLICY_NO2 = POLICY_NO2;
    this.POLICY_NO3 = POLICY_NO3;
    this.REG_TYPE = REG_TYPE;
    this.OTH_TYPE = OTH_TYPE;
    this.BRANCH_NBR = BRANCH_NBR;
    this.CUST_ID = CUST_ID;
    this.PROPOSER_NAME = PROPOSER_NAME;
    this.PROPOSER_BIRTH = PROPOSER_BIRTH;
    this.INSURED_ID = INSURED_ID;
    this.INSURED_NAME = INSURED_NAME;
    this.REPRESET_ID = REPRESET_ID;
    this.RLT_BT_PROREP = RLT_BT_PROREP;
    this.AO_CODE = AO_CODE;
    this.PROPOSER_FATCA_FLAG = PROPOSER_FATCA_FLAG;
    this.PROPOSER_CM_FLAG = PROPOSER_CM_FLAG;
    this.INSURED_CM_FLAG = INSURED_CM_FLAG;
    this.REPRESET_CM_FLAG = REPRESET_CM_FLAG;
    this.INSPRD_KEYNO = INSPRD_KEYNO;
    this.FXD_KEYNO = FXD_KEYNO;
    this.APPLY_DATE = APPLY_DATE;
    this.KEYIN_DATE = KEYIN_DATE;
    this.MATCH_DATE = MATCH_DATE;
    this.PROPOSER_RISK = PROPOSER_RISK;
    this.OP_DATE = OP_DATE;
    this.BATCH_INFO_KEYNO = BATCH_INFO_KEYNO;
    this.REAL_PREMIUM = REAL_PREMIUM;
    this.BASE_PREMIUM = BASE_PREMIUM;
    this.MOP2 = MOP2;
    this.FIRST_PAY_WAY = FIRST_PAY_WAY;
    this.RECRUIT_ID = RECRUIT_ID;
    this.REF_CON_ID = REF_CON_ID;
    this.IS_EMP = IS_EMP;
    this.WRITE_REASON = WRITE_REASON;
    this.WRITE_REASON_OTH = WRITE_REASON_OTH;
    this.REJ_REASON = REJ_REASON;
    this.REJ_REASON_OTH = REJ_REASON_OTH;
    this.STATUS = STATUS;
    this.TERMINATED_INC = TERMINATED_INC;
    this.QC_ADD = QC_ADD;
    this.QC_ERASER = QC_ERASER;
    this.QC_ANC_DOC = QC_ANC_DOC;
    this.QC_STAMP = QC_STAMP;
    this.AB_TRANSSEQ = AB_TRANSSEQ;
    this.PROPOSER_TRANSSEQ = PROPOSER_TRANSSEQ;
    this.INSURED_TRANSSEQ = INSURED_TRANSSEQ;
    this.SIGN_INC = SIGN_INC;
    this.DELETE_OPRID = DELETE_OPRID;
    this.DELETE_DATE = DELETE_DATE;
    this.BEF_SIGN_NO = BEF_SIGN_NO;
    this.BEF_SIGN_OPRID = BEF_SIGN_OPRID;
    this.BEF_SIGN_DATE = BEF_SIGN_DATE;
    this.SIGN_NO = SIGN_NO;
    this.SIGN_OPRID = SIGN_OPRID;
    this.SIGN_DATE = SIGN_DATE;
    this.AFT_SIGN_OPRID = AFT_SIGN_OPRID;
    this.AFT_SIGN_DATE = AFT_SIGN_DATE;
    this.REMARK_BANK = REMARK_BANK;
    this.REMARK_INS = REMARK_INS;
    this.CASE_ID = CASE_ID;
    this.INS_SUBMIT_DATE = INS_SUBMIT_DATE;
    this.PREMATCH_SEQ = PREMATCH_SEQ;
    this.GUILD_RPT_DATE = GUILD_RPT_DATE;
    this.LOAN_PRD_YN = LOAN_PRD_YN;
    this.QC_IMMI = QC_IMMI;
    this.NOT_PASS_REASON = NOT_PASS_REASON;
    this.PREMIUM_TRANSSEQ = PREMIUM_TRANSSEQ;
    this.I_PREMIUM_TRANSSEQ = i_PREMIUM_TRANSSEQ;
    this.P_PREMIUM_TRANSSEQ = p_PREMIUM_TRANSSEQ;
    this.QC_PROPOSER_CHG = QC_PROPOSER_CHG;
    this.PREMIUM_USAGE = PREMIUM_USAGE;
    this.PAY_WAY = PAY_WAY;
    this.PAYER_ID = PAYER_ID;
    this.RLT_BT_PROPAY = RLT_BT_PROPAY;
    this.DOC_KEYIN_DATE = DOC_KEYIN_DATE;
    this.LOAN_SOURCE_YN = LOAN_SOURCE_YN;
    this.VALID_CHG_CUST_YN = VALID_CHG_CUST_YN;
    this.SALE_SENIOR_TRANSSEQ = SALE_SENIOR_TRANSSEQ;
    this.INS_SIGN_OPRID = INS_SIGN_OPRID;
    this.INS_SIGN_DATE = INS_SIGN_DATE;
    this.INS_SIGN_YN = INS_SIGN_YN;
    this.QC_APEC = QC_APEC;
    this.QC_LOAN_CHK = QC_LOAN_CHK;
    this.QC_SIGNATURE = QC_SIGNATURE;
    this.NOT_FB_BATCH_SEQ = NOT_FB_BATCH_SEQ;
    this.NOT_FB_BATCH_DATE = NOT_FB_BATCH_DATE;
    this.NOT_FB_SIGN_DATE = NOT_FB_SIGN_DATE;
    this.NOT_FB_OP_NAME = NOT_FB_OP_NAME;
    this.FB_COM_YN = FB_COM_YN;
    this.COMPANY_NUM = COMPANY_NUM;
  }

  public TBIOT_MAINVO() {}

  public TBIOT_MAINVO(BigDecimal paramBigDecimal, String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, BigDecimal paramBigDecimal1, Timestamp paramTimestamp, String paramString6)
  {
    this.INS_KEYNO = paramBigDecimal;
    this.INS_KIND = paramString1;
    this.REG_TYPE = paramString2;
    this.BRANCH_NBR = paramString3;
    this.CUST_ID = paramString4;
    this.RECRUIT_ID = paramString5;
    this.STATUS = paramBigDecimal1;
    this.createtime = paramTimestamp;
    this.creator = paramString6;
  }

  public BigDecimal getINS_KEYNO()
  {
    return this.INS_KEYNO;
  }

  public void setINS_KEYNO(BigDecimal paramBigDecimal)
  {
    this.INS_KEYNO = paramBigDecimal;
  }

  public String getINS_KIND()
  {
    return this.INS_KIND;
  }

  public void setINS_KIND(String paramString)
  {
    this.INS_KIND = paramString;
  }

  public String getINS_ID()
  {
    return this.INS_ID;
  }

  public void setINS_ID(String paramString)
  {
    this.INS_ID = paramString;
  }

  public String getPOLICY_NO1()
  {
    return this.POLICY_NO1;
  }

  public void setPOLICY_NO1(String paramString)
  {
    this.POLICY_NO1 = paramString;
  }

  public String getPOLICY_NO2()
  {
    return this.POLICY_NO2;
  }

  public void setPOLICY_NO2(String paramString)
  {
    this.POLICY_NO2 = paramString;
  }

  public String getPOLICY_NO3()
  {
    return this.POLICY_NO3;
  }

  public void setPOLICY_NO3(String paramString)
  {
    this.POLICY_NO3 = paramString;
  }

  public String getREG_TYPE()
  {
    return this.REG_TYPE;
  }

  public void setREG_TYPE(String paramString)
  {
    this.REG_TYPE = paramString;
  }

  public String getOTH_TYPE()
  {
    return this.OTH_TYPE;
  }

  public void setOTH_TYPE(String paramString)
  {
    this.OTH_TYPE = paramString;
  }

  public String getBRANCH_NBR()
  {
    return this.BRANCH_NBR;
  }

  public void setBRANCH_NBR(String paramString)
  {
    this.BRANCH_NBR = paramString;
  }

  public String getCUST_ID()
  {
    return this.CUST_ID;
  }

  public void setCUST_ID(String paramString)
  {
    this.CUST_ID = paramString;
  }

  public String getPROPOSER_NAME()
  {
    return this.PROPOSER_NAME;
  }

  public void setPROPOSER_NAME(String paramString)
  {
    this.PROPOSER_NAME = paramString;
  }

  public Timestamp getPROPOSER_BIRTH()
  {
    return this.PROPOSER_BIRTH;
  }

  public void setPROPOSER_BIRTH(Timestamp paramTimestamp)
  {
    this.PROPOSER_BIRTH = paramTimestamp;
  }

  public String getINSURED_ID()
  {
    return this.INSURED_ID;
  }

  public void setINSURED_ID(String paramString)
  {
    this.INSURED_ID = paramString;
  }

  public String getINSURED_NAME()
  {
    return this.INSURED_NAME;
  }

  public void setINSURED_NAME(String paramString)
  {
    this.INSURED_NAME = paramString;
  }

  public String getREPRESET_ID()
  {
    return this.REPRESET_ID;
  }

  public void setREPRESET_ID(String paramString)
  {
    this.REPRESET_ID = paramString;
  }

  public String getRLT_BT_PROREP()
  {
    return this.RLT_BT_PROREP;
  }

  public void setRLT_BT_PROREP(String paramString)
  {
    this.RLT_BT_PROREP = paramString;
  }

  public String getAO_CODE()
  {
    return this.AO_CODE;
  }

  public void setAO_CODE(String paramString)
  {
    this.AO_CODE = paramString;
  }

  public String getPROPOSER_FATCA_FLAG()
  {
    return this.PROPOSER_FATCA_FLAG;
  }

  public void setPROPOSER_FATCA_FLAG(String paramString)
  {
    this.PROPOSER_FATCA_FLAG = paramString;
  }

  public String getPROPOSER_CM_FLAG()
  {
    return this.PROPOSER_CM_FLAG;
  }

  public void setPROPOSER_CM_FLAG(String paramString)
  {
    this.PROPOSER_CM_FLAG = paramString;
  }

  public String getINSURED_CM_FLAG()
  {
    return this.INSURED_CM_FLAG;
  }

  public void setINSURED_CM_FLAG(String paramString)
  {
    this.INSURED_CM_FLAG = paramString;
  }

  public String getREPRESET_CM_FLAG()
  {
    return this.REPRESET_CM_FLAG;
  }

  public void setREPRESET_CM_FLAG(String paramString)
  {
    this.REPRESET_CM_FLAG = paramString;
  }

  public BigDecimal getINSPRD_KEYNO()
  {
    return this.INSPRD_KEYNO;
  }

  public void setINSPRD_KEYNO(BigDecimal paramBigDecimal)
  {
    this.INSPRD_KEYNO = paramBigDecimal;
  }

  public BigDecimal getFXD_KEYNO()
  {
    return this.FXD_KEYNO;
  }

  public void setFXD_KEYNO(BigDecimal paramBigDecimal)
  {
    this.FXD_KEYNO = paramBigDecimal;
  }

  public Timestamp getAPPLY_DATE()
  {
    return this.APPLY_DATE;
  }

  public void setAPPLY_DATE(Timestamp paramTimestamp)
  {
    this.APPLY_DATE = paramTimestamp;
  }

  public Timestamp getKEYIN_DATE()
  {
    return this.KEYIN_DATE;
  }

  public void setKEYIN_DATE(Timestamp paramTimestamp)
  {
    this.KEYIN_DATE = paramTimestamp;
  }

  public Timestamp getMATCH_DATE()
  {
    return this.MATCH_DATE;
  }

  public void setMATCH_DATE(Timestamp paramTimestamp)
  {
    this.MATCH_DATE = paramTimestamp;
  }

  public String getPROPOSER_RISK()
  {
    return this.PROPOSER_RISK;
  }

  public void setPROPOSER_RISK(String paramString)
  {
    this.PROPOSER_RISK = paramString;
  }

  public Timestamp getOP_DATE()
  {
    return this.OP_DATE;
  }

  public void setOP_DATE(Timestamp paramTimestamp)
  {
    this.OP_DATE = paramTimestamp;
  }

  public BigDecimal getBATCH_INFO_KEYNO()
  {
    return this.BATCH_INFO_KEYNO;
  }

  public void setBATCH_INFO_KEYNO(BigDecimal paramBigDecimal)
  {
    this.BATCH_INFO_KEYNO = paramBigDecimal;
  }

  public BigDecimal getREAL_PREMIUM()
  {
    return this.REAL_PREMIUM;
  }

  public void setREAL_PREMIUM(BigDecimal paramBigDecimal)
  {
    this.REAL_PREMIUM = paramBigDecimal;
  }

  public BigDecimal getBASE_PREMIUM()
  {
    return this.BASE_PREMIUM;
  }

  public void setBASE_PREMIUM(BigDecimal paramBigDecimal)
  {
    this.BASE_PREMIUM = paramBigDecimal;
  }

  public String getMOP2()
  {
    return this.MOP2;
  }

  public void setMOP2(String paramString)
  {
    this.MOP2 = paramString;
  }

  public String getFIRST_PAY_WAY()
  {
    return this.FIRST_PAY_WAY;
  }

  public void setFIRST_PAY_WAY(String paramString)
  {
    this.FIRST_PAY_WAY = paramString;
  }

  public String getRECRUIT_ID()
  {
    return this.RECRUIT_ID;
  }

  public void setRECRUIT_ID(String paramString)
  {
    this.RECRUIT_ID = paramString;
  }

  public String getREF_CON_ID()
  {
    return this.REF_CON_ID;
  }

  public void setREF_CON_ID(String paramString)
  {
    this.REF_CON_ID = paramString;
  }

  public String getIS_EMP()
  {
    return this.IS_EMP;
  }

  public void setIS_EMP(String paramString)
  {
    this.IS_EMP = paramString;
  }

  public String getWRITE_REASON()
  {
    return this.WRITE_REASON;
  }

  public void setWRITE_REASON(String paramString)
  {
    this.WRITE_REASON = paramString;
  }

  public String getWRITE_REASON_OTH()
  {
    return this.WRITE_REASON_OTH;
  }

  public void setWRITE_REASON_OTH(String paramString)
  {
    this.WRITE_REASON_OTH = paramString;
  }

  public String getREJ_REASON()
  {
    return this.REJ_REASON;
  }

  public void setREJ_REASON(String paramString)
  {
    this.REJ_REASON = paramString;
  }

  public String getREJ_REASON_OTH()
  {
    return this.REJ_REASON_OTH;
  }

  public void setREJ_REASON_OTH(String paramString)
  {
    this.REJ_REASON_OTH = paramString;
  }

  public BigDecimal getSTATUS()
  {
    return this.STATUS;
  }

  public void setSTATUS(BigDecimal paramBigDecimal)
  {
    this.STATUS = paramBigDecimal;
  }

  public String getTERMINATED_INC()
  {
    return this.TERMINATED_INC;
  }

  public void setTERMINATED_INC(String paramString)
  {
    this.TERMINATED_INC = paramString;
  }

  public String getQC_ADD()
  {
    return this.QC_ADD;
  }

  public void setQC_ADD(String paramString)
  {
    this.QC_ADD = paramString;
  }

  public String getQC_ERASER()
  {
    return this.QC_ERASER;
  }

  public void setQC_ERASER(String paramString)
  {
    this.QC_ERASER = paramString;
  }

  public String getQC_ANC_DOC()
  {
    return this.QC_ANC_DOC;
  }

  public void setQC_ANC_DOC(String paramString)
  {
    this.QC_ANC_DOC = paramString;
  }

  public String getQC_STAMP()
  {
    return this.QC_STAMP;
  }

  public void setQC_STAMP(String paramString)
  {
    this.QC_STAMP = paramString;
  }

  public String getAB_TRANSSEQ()
  {
    return this.AB_TRANSSEQ;
  }

  public void setAB_TRANSSEQ(String paramString)
  {
    this.AB_TRANSSEQ = paramString;
  }

  public String getPROPOSER_TRANSSEQ()
  {
    return this.PROPOSER_TRANSSEQ;
  }

  public void setPROPOSER_TRANSSEQ(String paramString)
  {
    this.PROPOSER_TRANSSEQ = paramString;
  }

  public String getINSURED_TRANSSEQ()
  {
    return this.INSURED_TRANSSEQ;
  }

  public void setINSURED_TRANSSEQ(String paramString)
  {
    this.INSURED_TRANSSEQ = paramString;
  }

  public String getSIGN_INC()
  {
    return this.SIGN_INC;
  }

  public void setSIGN_INC(String paramString)
  {
    this.SIGN_INC = paramString;
  }

  public String getDELETE_OPRID()
  {
    return this.DELETE_OPRID;
  }

  public void setDELETE_OPRID(String paramString)
  {
    this.DELETE_OPRID = paramString;
  }

  public Timestamp getDELETE_DATE()
  {
    return this.DELETE_DATE;
  }

  public void setDELETE_DATE(Timestamp paramTimestamp)
  {
    this.DELETE_DATE = paramTimestamp;
  }

  public String getBEF_SIGN_NO()
  {
    return this.BEF_SIGN_NO;
  }

  public void setBEF_SIGN_NO(String paramString)
  {
    this.BEF_SIGN_NO = paramString;
  }

  public String getBEF_SIGN_OPRID()
  {
    return this.BEF_SIGN_OPRID;
  }

  public void setBEF_SIGN_OPRID(String paramString)
  {
    this.BEF_SIGN_OPRID = paramString;
  }

  public Timestamp getBEF_SIGN_DATE()
  {
    return this.BEF_SIGN_DATE;
  }

  public void setBEF_SIGN_DATE(Timestamp paramTimestamp)
  {
    this.BEF_SIGN_DATE = paramTimestamp;
  }

  public String getSIGN_NO()
  {
    return this.SIGN_NO;
  }

  public void setSIGN_NO(String paramString)
  {
    this.SIGN_NO = paramString;
  }

  public String getSIGN_OPRID()
  {
    return this.SIGN_OPRID;
  }

  public void setSIGN_OPRID(String paramString)
  {
    this.SIGN_OPRID = paramString;
  }

  public Timestamp getSIGN_DATE()
  {
    return this.SIGN_DATE;
  }

  public void setSIGN_DATE(Timestamp paramTimestamp)
  {
    this.SIGN_DATE = paramTimestamp;
  }

  public String getAFT_SIGN_OPRID()
  {
    return this.AFT_SIGN_OPRID;
  }

  public void setAFT_SIGN_OPRID(String paramString)
  {
    this.AFT_SIGN_OPRID = paramString;
  }

  public Timestamp getAFT_SIGN_DATE()
  {
    return this.AFT_SIGN_DATE;
  }

  public void setAFT_SIGN_DATE(Timestamp paramTimestamp)
  {
    this.AFT_SIGN_DATE = paramTimestamp;
  }

  public String getREMARK_BANK()
  {
    return this.REMARK_BANK;
  }

  public void setREMARK_BANK(String paramString)
  {
    this.REMARK_BANK = paramString;
  }

  public String getREMARK_INS()
  {
    return this.REMARK_INS;
  }

  public void setREMARK_INS(String paramString)
  {
    this.REMARK_INS = paramString;
  }

  public String getCASE_ID()
  {
    return this.CASE_ID;
  }

  public void setCASE_ID(String paramString)
  {
    this.CASE_ID = paramString;
  }

  public Timestamp getINS_SUBMIT_DATE()
  {
    return this.INS_SUBMIT_DATE;
  }

  public void setINS_SUBMIT_DATE(Timestamp paramTimestamp)
  {
    this.INS_SUBMIT_DATE = paramTimestamp;
  }

  public String getPREMATCH_SEQ()
  {
    return this.PREMATCH_SEQ;
  }

  public void setPREMATCH_SEQ(String paramString)
  {
    this.PREMATCH_SEQ = paramString;
  }

  public Timestamp getGUILD_RPT_DATE()
  {
    return this.GUILD_RPT_DATE;
  }

  public void setGUILD_RPT_DATE(Timestamp paramTimestamp)
  {
    this.GUILD_RPT_DATE = paramTimestamp;
  }

  public String getLOAN_PRD_YN()
  {
    return this.LOAN_PRD_YN;
  }

  public void setLOAN_PRD_YN(String paramString)
  {
    this.LOAN_PRD_YN = paramString;
  }

  public String getQC_IMMI()
  {
    return this.QC_IMMI;
  }

  public void setQC_IMMI(String paramString)
  {
    this.QC_IMMI = paramString;
  }

  public String getNOT_PASS_REASON()
  {
    return this.NOT_PASS_REASON;
  }

  public void setNOT_PASS_REASON(String paramString)
  {
    this.NOT_PASS_REASON = paramString;
  }

  public String getPREMIUM_TRANSSEQ()
  {
    return this.PREMIUM_TRANSSEQ;
  }

  public void setPREMIUM_TRANSSEQ(String paramString)
  {
    this.PREMIUM_TRANSSEQ = paramString;
  }

  public String getI_PREMIUM_TRANSSEQ() {
	return I_PREMIUM_TRANSSEQ;
  }

	public void setI_PREMIUM_TRANSSEQ(String i_PREMIUM_TRANSSEQ) {
		I_PREMIUM_TRANSSEQ = i_PREMIUM_TRANSSEQ;
	}

	public String getP_PREMIUM_TRANSSEQ() {
		return P_PREMIUM_TRANSSEQ;
	}

	public void setP_PREMIUM_TRANSSEQ(String p_PREMIUM_TRANSSEQ) {
		P_PREMIUM_TRANSSEQ = p_PREMIUM_TRANSSEQ;
	}

	public String getQC_PROPOSER_CHG()
  {
    return this.QC_PROPOSER_CHG;
  }

  public void setQC_PROPOSER_CHG(String paramString)
  {
    this.QC_PROPOSER_CHG = paramString;
  }

  public String getPREMIUM_USAGE()
  {
    return this.PREMIUM_USAGE;
  }

  public void setPREMIUM_USAGE(String paramString)
  {
    this.PREMIUM_USAGE = paramString;
  }

  public String getPAY_WAY()
  {
    return this.PAY_WAY;
  }

  public void setPAY_WAY(String paramString)
  {
    this.PAY_WAY = paramString;
  }

  public String getPAYER_ID()
  {
    return this.PAYER_ID;
  }

  public void setPAYER_ID(String paramString)
  {
    this.PAYER_ID = paramString;
  }

  public String getRLT_BT_PROPAY()
  {
    return this.RLT_BT_PROPAY;
  }

  public void setRLT_BT_PROPAY(String paramString)
  {
    this.RLT_BT_PROPAY = paramString;
  }

  public void checkDefaultValue() {}

  public String toString()
  {
    return new ToStringBuilder(this).append("INS_KEYNO", getINS_KEYNO()).toString();
  }

	public Timestamp getDOC_KEYIN_DATE() {
		return DOC_KEYIN_DATE;
	}

	public void setDOC_KEYIN_DATE(Timestamp dOC_KEYIN_DATE) {
		DOC_KEYIN_DATE = dOC_KEYIN_DATE;
	}

	public String getLOAN_SOURCE_YN() {
		return LOAN_SOURCE_YN;
	}

	public void setLOAN_SOURCE_YN(String lOAN_SOURCE_YN) {
		LOAN_SOURCE_YN = lOAN_SOURCE_YN;
	}

	public String getVALID_CHG_CUST_YN() {
		return VALID_CHG_CUST_YN;
	}

	public void setVALID_CHG_CUST_YN(String vALID_CHG_CUST_YN) {
		VALID_CHG_CUST_YN = vALID_CHG_CUST_YN;
	}

	public String getSALE_SENIOR_TRANSSEQ() {
		return SALE_SENIOR_TRANSSEQ;
	}

	public void setSALE_SENIOR_TRANSSEQ(String sALE_SENIOR_TRANSSEQ) {
		SALE_SENIOR_TRANSSEQ = sALE_SENIOR_TRANSSEQ;
	}

	public String getINS_SIGN_OPRID() {
		return INS_SIGN_OPRID;
	}

	public void setINS_SIGN_OPRID(String iNS_SIGN_OPRID) {
		INS_SIGN_OPRID = iNS_SIGN_OPRID;
	}

	public Timestamp getINS_SIGN_DATE() {
		return INS_SIGN_DATE;
	}

	public void setINS_SIGN_DATE(Timestamp iNS_SIGN_DATE) {
		INS_SIGN_DATE = iNS_SIGN_DATE;
	}

	public String getINS_SIGN_YN() {
		return INS_SIGN_YN;
	}

	public void setINS_SIGN_YN(String iNS_SIGN_YN) {
		INS_SIGN_YN = iNS_SIGN_YN;
	}

	public String getQC_APEC() {
		return QC_APEC;
	}

	public void setQC_APEC(String qC_APEC) {
		QC_APEC = qC_APEC;
	}

	public String getQC_LOAN_CHK() {
		return QC_LOAN_CHK;
	}

	public void setQC_LOAN_CHK(String qC_LOAN_CHK) {
		QC_LOAN_CHK = qC_LOAN_CHK;
	}

	public String getQC_SIGNATURE() {
		return QC_SIGNATURE;
	}

	public void setQC_SIGNATURE(String qC_SIGNATURE) {
		QC_SIGNATURE = qC_SIGNATURE;
	}

    public String getNOT_FB_BATCH_SEQ() {
      return NOT_FB_BATCH_SEQ;
    }

    public void setNOT_FB_BATCH_SEQ(String NOT_FB_BATCH_SEQ) {
      this.NOT_FB_BATCH_SEQ = NOT_FB_BATCH_SEQ;
    }

    public Timestamp getNOT_FB_BATCH_DATE() {
      return NOT_FB_BATCH_DATE;
    }

    public void setNOT_FB_BATCH_DATE(Timestamp NOT_FB_BATCH_DATE) {
      this.NOT_FB_BATCH_DATE = NOT_FB_BATCH_DATE;
    }

    public Timestamp getNOT_FB_SIGN_DATE() {
      return NOT_FB_SIGN_DATE;
    }

    public void setNOT_FB_SIGN_DATE(Timestamp NOT_FB_SIGN_DATE) {
      this.NOT_FB_SIGN_DATE = NOT_FB_SIGN_DATE;
    }

    public String getNOT_FB_OP_NAME() {
      return NOT_FB_OP_NAME;
    }

    public void setNOT_FB_OP_NAME(String NOT_FB_OP_NAME) {
      this.NOT_FB_OP_NAME = NOT_FB_OP_NAME;
    }

  public String getFB_COM_YN() {
    return FB_COM_YN;
  }

  public void setFB_COM_YN(String FB_COM_YN) {
    this.FB_COM_YN = FB_COM_YN;
  }

  public BigDecimal getCOMPANY_NUM() {
    return COMPANY_NUM;
  }

  public void setCOMPANY_NUM(BigDecimal COMPANY_NUM) {
    this.COMPANY_NUM = COMPANY_NUM;
  }

public String getREVISE_CONFIRM_YN() {
	return REVISE_CONFIRM_YN;
}

public void setREVISE_CONFIRM_YN(String rEVISE_CONFIRM_YN) {
	REVISE_CONFIRM_YN = rEVISE_CONFIRM_YN;
}
}
