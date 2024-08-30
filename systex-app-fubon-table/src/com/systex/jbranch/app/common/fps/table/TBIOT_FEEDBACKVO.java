package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBIOT_FEEDBACKVO extends VOBase {

    /** identifier field */
    private BigDecimal KEY_NO;

    /** nullable persistent field */
    private BigDecimal INS_KEYNO;

    /** nullable persistent field */
    private String CUST_ID;

    /** nullable persistent field */
    private String POLICY_STATUS;

    /** nullable persistent field */
    private String INS_STATUS;

    /** nullable persistent field */
    private String PMS_STATUS;

    /** nullable persistent field */
    private String POLICY_NO1;

    /** nullable persistent field */
    private String POLICY_NO2;

    /** nullable persistent field */
    private String POLICY_NO3;

    /** nullable persistent field */
    private String INS_NO;

    /** nullable persistent field */
    private String INS_ITEM;

    /** nullable persistent field */
    private String PREM_YEAR_STR;

    /** nullable persistent field */
    private BigDecimal INS_YEAR_STR;

    /** nullable persistent field */
    private String PAY_TYPE;

    /** nullable persistent field */
    private String EMPY_YN;

    /** nullable persistent field */
    private String DEPT_CODE;

    /** nullable persistent field */
    private String NB_NOTE_DT;

    /** nullable persistent field */
    private String NB_CONTENT;

    /** nullable persistent field */
    private String RN_NOTE_DT;

    /** nullable persistent field */
    private String RM_CONTENT;

    /** nullable persistent field */
    private Timestamp INSPECT_DATE;

    /** nullable persistent field */
    private BigDecimal PREM_PAYABLE;

    /** nullable persistent field */
    private Timestamp APPL_DATE;

    /** nullable persistent field */
    private String DOWN_PAY_TYPE;

    /** nullable persistent field */
    private Timestamp SEND_DATE;

    /** nullable persistent field */
    private String SEND_TYPE;

    /** nullable persistent field */
    private Timestamp SIGN_DATE;

    /** nullable persistent field */
    private String REGISTER_NO;

    /** nullable persistent field */
    private String POLI_YEAR;

    /** nullable persistent field */
    private BigDecimal POLI_PERD;

    /** nullable persistent field */
    private String POLICY_CUR;

    /** nullable persistent field */
    private String BRANCH_NBR;

    /** nullable persistent field */
    private String ITEM_REMRK;

    /** nullable persistent field */
    private BigDecimal COMU_RATE;

    /** nullable persistent field */
    private BigDecimal POLICY_EXCH;

    /** nullable persistent field */
    private Timestamp POLICY_EXCH_DATE;

    /** nullable persistent field */
    private String GET_YYMMDD;

    /** nullable persistent field */
    private String COMU_YYMMDD;

    /** nullable persistent field */
    private BigDecimal EST_PREM;

    /** nullable persistent field */
    private BigDecimal EST_COMIS;

    /** nullable persistent field */
    private BigDecimal EST_DECL_ECHG;

    /** nullable persistent field */
    private Timestamp REAL_COMU_DATE;

    /** nullable persistent field */
    private BigDecimal REAL_PREM;

    /** nullable persistent field */
    private BigDecimal REAL_COMIS;

    /** nullable persistent field */
    private BigDecimal DECL_ECHG;

    /** nullable persistent field */
    private String INSURED_ID;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBIOT_FEEDBACK";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBIOT_FEEDBACKVO(BigDecimal KEY_NO, BigDecimal INS_KEYNO, String CUST_ID, String POLICY_STATUS, String INS_STATUS, String PMS_STATUS, String POLICY_NO1, String POLICY_NO2, String POLICY_NO3, String INS_NO, String INS_ITEM, String PREM_YEAR_STR, BigDecimal INS_YEAR_STR, String PAY_TYPE, String EMPY_YN, String DEPT_CODE, String NB_NOTE_DT, String NB_CONTENT, String RN_NOTE_DT, String RM_CONTENT, Timestamp INSPECT_DATE, BigDecimal PREM_PAYABLE, Timestamp APPL_DATE, String DOWN_PAY_TYPE, Timestamp SEND_DATE, String SEND_TYPE, Timestamp SIGN_DATE, String REGISTER_NO, String POLI_YEAR, BigDecimal POLI_PERD, String POLICY_CUR, String BRANCH_NBR, String ITEM_REMRK, BigDecimal COMU_RATE, BigDecimal POLICY_EXCH, Timestamp POLICY_EXCH_DATE, String GET_YYMMDD, String COMU_YYMMDD, BigDecimal EST_PREM, BigDecimal EST_COMIS, BigDecimal EST_DECL_ECHG, Timestamp REAL_COMU_DATE, BigDecimal REAL_PREM, BigDecimal REAL_COMIS, BigDecimal DECL_ECHG, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, String INSURED_ID, Long version) {
        this.KEY_NO = KEY_NO;
        this.INS_KEYNO = INS_KEYNO;
        this.CUST_ID = CUST_ID;
        this.POLICY_STATUS = POLICY_STATUS;
        this.INS_STATUS = INS_STATUS;
        this.PMS_STATUS = PMS_STATUS;
        this.POLICY_NO1 = POLICY_NO1;
        this.POLICY_NO2 = POLICY_NO2;
        this.POLICY_NO3 = POLICY_NO3;
        this.INS_NO = INS_NO;
        this.INS_ITEM = INS_ITEM;
        this.PREM_YEAR_STR = PREM_YEAR_STR;
        this.INS_YEAR_STR = INS_YEAR_STR;
        this.PAY_TYPE = PAY_TYPE;
        this.EMPY_YN = EMPY_YN;
        this.DEPT_CODE = DEPT_CODE;
        this.NB_NOTE_DT = NB_NOTE_DT;
        this.NB_CONTENT = NB_CONTENT;
        this.RN_NOTE_DT = RN_NOTE_DT;
        this.RM_CONTENT = RM_CONTENT;
        this.INSPECT_DATE = INSPECT_DATE;
        this.PREM_PAYABLE = PREM_PAYABLE;
        this.APPL_DATE = APPL_DATE;
        this.DOWN_PAY_TYPE = DOWN_PAY_TYPE;
        this.SEND_DATE = SEND_DATE;
        this.SEND_TYPE = SEND_TYPE;
        this.SIGN_DATE = SIGN_DATE;
        this.REGISTER_NO = REGISTER_NO;
        this.POLI_YEAR = POLI_YEAR;
        this.POLI_PERD = POLI_PERD;
        this.POLICY_CUR = POLICY_CUR;
        this.BRANCH_NBR = BRANCH_NBR;
        this.ITEM_REMRK = ITEM_REMRK;
        this.COMU_RATE = COMU_RATE;
        this.POLICY_EXCH = POLICY_EXCH;
        this.POLICY_EXCH_DATE = POLICY_EXCH_DATE;
        this.GET_YYMMDD = GET_YYMMDD;
        this.COMU_YYMMDD = COMU_YYMMDD;
        this.EST_PREM = EST_PREM;
        this.EST_COMIS = EST_COMIS;
        this.EST_DECL_ECHG = EST_DECL_ECHG;
        this.REAL_COMU_DATE = REAL_COMU_DATE;
        this.REAL_PREM = REAL_PREM;
        this.REAL_COMIS = REAL_COMIS;
        this.DECL_ECHG = DECL_ECHG;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.INSURED_ID = INSURED_ID;
        this.version = version;
    }

    /** default constructor */
    public TBIOT_FEEDBACKVO() {
    }

    /** minimal constructor */
    public TBIOT_FEEDBACKVO(BigDecimal KEY_NO) {
        this.KEY_NO = KEY_NO;
    }

    public BigDecimal getKEY_NO() {
        return this.KEY_NO;
    }

    public void setKEY_NO(BigDecimal KEY_NO) {
        this.KEY_NO = KEY_NO;
    }

    public BigDecimal getINS_KEYNO() {
        return this.INS_KEYNO;
    }

    public void setINS_KEYNO(BigDecimal INS_KEYNO) {
        this.INS_KEYNO = INS_KEYNO;
    }

    public String getCUST_ID() {
        return this.CUST_ID;
    }

    public void setCUST_ID(String CUST_ID) {
        this.CUST_ID = CUST_ID;
    }

    public String getPOLICY_STATUS() {
        return this.POLICY_STATUS;
    }

    public void setPOLICY_STATUS(String POLICY_STATUS) {
        this.POLICY_STATUS = POLICY_STATUS;
    }

    public String getINS_STATUS() {
        return this.INS_STATUS;
    }

    public void setINS_STATUS(String INS_STATUS) {
        this.INS_STATUS = INS_STATUS;
    }

    public String getPMS_STATUS() {
        return this.PMS_STATUS;
    }

    public void setPMS_STATUS(String PMS_STATUS) {
        this.PMS_STATUS = PMS_STATUS;
    }

    public String getPOLICY_NO1() {
        return this.POLICY_NO1;
    }

    public void setPOLICY_NO1(String POLICY_NO1) {
        this.POLICY_NO1 = POLICY_NO1;
    }

    public String getPOLICY_NO2() {
        return this.POLICY_NO2;
    }

    public void setPOLICY_NO2(String POLICY_NO2) {
        this.POLICY_NO2 = POLICY_NO2;
    }

    public String getPOLICY_NO3() {
        return this.POLICY_NO3;
    }

    public void setPOLICY_NO3(String POLICY_NO3) {
        this.POLICY_NO3 = POLICY_NO3;
    }

    public String getINS_NO() {
        return this.INS_NO;
    }

    public void setINS_NO(String INS_NO) {
        this.INS_NO = INS_NO;
    }

    public String getINS_ITEM() {
        return this.INS_ITEM;
    }

    public void setINS_ITEM(String INS_ITEM) {
        this.INS_ITEM = INS_ITEM;
    }

    public String getPREM_YEAR_STR() {
        return this.PREM_YEAR_STR;
    }

    public void setPREM_YEAR_STR(String PREM_YEAR_STR) {
        this.PREM_YEAR_STR = PREM_YEAR_STR;
    }

    public BigDecimal getINS_YEAR_STR() {
        return this.INS_YEAR_STR;
    }

    public void setINS_YEAR_STR(BigDecimal INS_YEAR_STR) {
        this.INS_YEAR_STR = INS_YEAR_STR;
    }

    public String getPAY_TYPE() {
        return this.PAY_TYPE;
    }

    public void setPAY_TYPE(String PAY_TYPE) {
        this.PAY_TYPE = PAY_TYPE;
    }

    public String getEMPY_YN() {
        return this.EMPY_YN;
    }

    public void setEMPY_YN(String EMPY_YN) {
        this.EMPY_YN = EMPY_YN;
    }

    public String getDEPT_CODE() {
        return this.DEPT_CODE;
    }

    public void setDEPT_CODE(String DEPT_CODE) {
        this.DEPT_CODE = DEPT_CODE;
    }

    public String getNB_NOTE_DT() {
        return this.NB_NOTE_DT;
    }

    public void setNB_NOTE_DT(String NB_NOTE_DT) {
        this.NB_NOTE_DT = NB_NOTE_DT;
    }

    public String getNB_CONTENT() {
        return this.NB_CONTENT;
    }

    public void setNB_CONTENT(String NB_CONTENT) {
        this.NB_CONTENT = NB_CONTENT;
    }

    public String getRN_NOTE_DT() {
        return this.RN_NOTE_DT;
    }

    public void setRN_NOTE_DT(String RN_NOTE_DT) {
        this.RN_NOTE_DT = RN_NOTE_DT;
    }

    public String getRM_CONTENT() {
        return this.RM_CONTENT;
    }

    public void setRM_CONTENT(String RM_CONTENT) {
        this.RM_CONTENT = RM_CONTENT;
    }

    public Timestamp getINSPECT_DATE() {
        return this.INSPECT_DATE;
    }

    public void setINSPECT_DATE(Timestamp INSPECT_DATE) {
        this.INSPECT_DATE = INSPECT_DATE;
    }

    public BigDecimal getPREM_PAYABLE() {
        return this.PREM_PAYABLE;
    }

    public void setPREM_PAYABLE(BigDecimal PREM_PAYABLE) {
        this.PREM_PAYABLE = PREM_PAYABLE;
    }

    public Timestamp getAPPL_DATE() {
        return this.APPL_DATE;
    }

    public void setAPPL_DATE(Timestamp APPL_DATE) {
        this.APPL_DATE = APPL_DATE;
    }

    public String getDOWN_PAY_TYPE() {
        return this.DOWN_PAY_TYPE;
    }

    public void setDOWN_PAY_TYPE(String DOWN_PAY_TYPE) {
        this.DOWN_PAY_TYPE = DOWN_PAY_TYPE;
    }

    public Timestamp getSEND_DATE() {
        return this.SEND_DATE;
    }

    public void setSEND_DATE(Timestamp SEND_DATE) {
        this.SEND_DATE = SEND_DATE;
    }

    public String getSEND_TYPE() {
        return this.SEND_TYPE;
    }

    public void setSEND_TYPE(String SEND_TYPE) {
        this.SEND_TYPE = SEND_TYPE;
    }

    public Timestamp getSIGN_DATE() {
        return this.SIGN_DATE;
    }

    public void setSIGN_DATE(Timestamp SIGN_DATE) {
        this.SIGN_DATE = SIGN_DATE;
    }

    public String getREGISTER_NO() {
        return this.REGISTER_NO;
    }

    public void setREGISTER_NO(String REGISTER_NO) {
        this.REGISTER_NO = REGISTER_NO;
    }

    public String getPOLI_YEAR() {
        return this.POLI_YEAR;
    }

    public void setPOLI_YEAR(String POLI_YEAR) {
        this.POLI_YEAR = POLI_YEAR;
    }

    public BigDecimal getPOLI_PERD() {
        return this.POLI_PERD;
    }

    public void setPOLI_PERD(BigDecimal POLI_PERD) {
        this.POLI_PERD = POLI_PERD;
    }

    public String getPOLICY_CUR() {
        return this.POLICY_CUR;
    }

    public void setPOLICY_CUR(String POLICY_CUR) {
        this.POLICY_CUR = POLICY_CUR;
    }

    public String getBRANCH_NBR() {
        return this.BRANCH_NBR;
    }

    public void setBRANCH_NBR(String BRANCH_NBR) {
        this.BRANCH_NBR = BRANCH_NBR;
    }

    public String getITEM_REMRK() {
        return this.ITEM_REMRK;
    }

    public void setITEM_REMRK(String ITEM_REMRK) {
        this.ITEM_REMRK = ITEM_REMRK;
    }

    public BigDecimal getCOMU_RATE() {
        return this.COMU_RATE;
    }

    public void setCOMU_RATE(BigDecimal COMU_RATE) {
        this.COMU_RATE = COMU_RATE;
    }

    public BigDecimal getPOLICY_EXCH() {
        return this.POLICY_EXCH;
    }

    public void setPOLICY_EXCH(BigDecimal POLICY_EXCH) {
        this.POLICY_EXCH = POLICY_EXCH;
    }

    public Timestamp getPOLICY_EXCH_DATE() {
        return this.POLICY_EXCH_DATE;
    }

    public void setPOLICY_EXCH_DATE(Timestamp POLICY_EXCH_DATE) {
        this.POLICY_EXCH_DATE = POLICY_EXCH_DATE;
    }

    public String getGET_YYMMDD() {
        return this.GET_YYMMDD;
    }

    public void setGET_YYMMDD(String GET_YYMMDD) {
        this.GET_YYMMDD = GET_YYMMDD;
    }

    public String getCOMU_YYMMDD() {
        return this.COMU_YYMMDD;
    }

    public void setCOMU_YYMMDD(String COMU_YYMMDD) {
        this.COMU_YYMMDD = COMU_YYMMDD;
    }

    public BigDecimal getEST_PREM() {
        return this.EST_PREM;
    }

    public void setEST_PREM(BigDecimal EST_PREM) {
        this.EST_PREM = EST_PREM;
    }

    public BigDecimal getEST_COMIS() {
        return this.EST_COMIS;
    }

    public void setEST_COMIS(BigDecimal EST_COMIS) {
        this.EST_COMIS = EST_COMIS;
    }

    public BigDecimal getEST_DECL_ECHG() {
        return this.EST_DECL_ECHG;
    }

    public void setEST_DECL_ECHG(BigDecimal EST_DECL_ECHG) {
        this.EST_DECL_ECHG = EST_DECL_ECHG;
    }

    public Timestamp getREAL_COMU_DATE() {
        return this.REAL_COMU_DATE;
    }

    public void setREAL_COMU_DATE(Timestamp REAL_COMU_DATE) {
        this.REAL_COMU_DATE = REAL_COMU_DATE;
    }

    public BigDecimal getREAL_PREM() {
        return this.REAL_PREM;
    }

    public void setREAL_PREM(BigDecimal REAL_PREM) {
        this.REAL_PREM = REAL_PREM;
    }

    public BigDecimal getREAL_COMIS() {
        return this.REAL_COMIS;
    }

    public void setREAL_COMIS(BigDecimal REAL_COMIS) {
        this.REAL_COMIS = REAL_COMIS;
    }

    public BigDecimal getDECL_ECHG() {
        return this.DECL_ECHG;
    }

    public void setDECL_ECHG(BigDecimal DECL_ECHG) {
        this.DECL_ECHG = DECL_ECHG;
    }

    public String getINSURED_ID() {
        return this.INSURED_ID;
    }

    public void setINSURED_ID(String INSURED_ID) {
        this.INSURED_ID = INSURED_ID;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("KEY_NO", getKEY_NO())
            .toString();
    }

}
