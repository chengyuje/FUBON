package com.systex.jbranch.app.common.fps.table;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBCRM_FWLDETA00_DATAVO extends VOBase {

    /** identifier field */
    private String CASE_NO;

    /** nullable persistent field */
    private String POLICY_NO;

    /** nullable persistent field */
    private String POLICY_SEQ;

    /** nullable persistent field */
    private String ID_DUP;

    /** nullable persistent field */
    private String ITEM;

    /** nullable persistent field */
    private String PREM_YEAR;

    /** nullable persistent field */
    private String PASS_TEAR;

    /** nullable persistent field */
    private String POLI_YY;

    /** nullable persistent field */
    private String POLI_MM;

    /** nullable persistent field */
    private String POLI_DD;

    /** nullable persistent field */
    private String UNITS;

    /** nullable persistent field */
    private String INVT_BONUS;

    /** nullable persistent field */
    private String INVT_INT;

    /** nullable persistent field */
    private String DVD_TYPE;

    /** nullable persistent field */
    private String PROD_JOB;

    /** nullable persistent field */
    private String PROD_ID;

    /** nullable persistent field */
    private String PROD_YY;

    /** nullable persistent field */
    private String PROD_MM;

    /** nullable persistent field */
    private String PROD_DD;

    /** nullable persistent field */
    private String NOTF_YY;

    /** nullable persistent field */
    private String NOTF_MM;

    /** nullable persistent field */
    private String NOTF_DD;

    /** nullable persistent field */
    private String DPAW_OP;

    /** nullable persistent field */
    private String DPAW_NO;

    /** nullable persistent field */
    private String DRAW_YY;

    /** nullable persistent field */
    private String DRAW_MM;

    /** nullable persistent field */
    private String DRAW_DD;

    /** nullable persistent field */
    private String CANCEL_YY;

    /** nullable persistent field */
    private String CANCEL_MM;

    /** nullable persistent field */
    private String CANCEL_DD;

    /** nullable persistent field */
    private String FUND_CODE;

    /** nullable persistent field */
    private String PRICE_YY;

    /** nullable persistent field */
    private String PRICE_MM;

    /** nullable persistent field */
    private String PRICE_DD;

    /** nullable persistent field */
    private String EXCH_YY;

    /** nullable persistent field */
    private String EXCH_MM;

    /** nullable persistent field */
    private String EXCH_DD;

    /** nullable persistent field */
    private String ECHG_BRT;

    /** nullable persistent field */
    private String FC_AMT;

    /** nullable persistent field */
    private String DLY_INT;

    /** nullable persistent field */
    private String UPD_ID;

    /** nullable persistent field */
    private String UPD_YY;

    /** nullable persistent field */
    private String UPD_MM;

    /** nullable persistent field */
    private String UPD_DD;

    /** nullable persistent field */
    private String FILLER_10;

    /** nullable persistent field */
    private String APPLID;

    /** nullable persistent field */
    private String ACPTNO;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBCRM_FWLDETA00_DATA";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBCRM_FWLDETA00_DATAVO(String CASE_NO, String POLICY_NO, String POLICY_SEQ, String ID_DUP, String ITEM, String PREM_YEAR, String PASS_TEAR, String POLI_YY, String POLI_MM, String POLI_DD, String UNITS, String INVT_BONUS, String INVT_INT, String DVD_TYPE, String PROD_JOB, String PROD_ID, String PROD_YY, String PROD_MM, String PROD_DD, String NOTF_YY, String NOTF_MM, String NOTF_DD, String DPAW_OP, String DPAW_NO, String DRAW_YY, String DRAW_MM, String DRAW_DD, String CANCEL_YY, String CANCEL_MM, String CANCEL_DD, String FUND_CODE, String PRICE_YY, String PRICE_MM, String PRICE_DD, String EXCH_YY, String EXCH_MM, String EXCH_DD, String ECHG_BRT, String FC_AMT, String DLY_INT, String UPD_ID, String UPD_YY, String UPD_MM, String UPD_DD, String FILLER_10, String APPLID, String ACPTNO) {
        this.CASE_NO = CASE_NO;
        this.POLICY_NO = POLICY_NO;
        this.POLICY_SEQ = POLICY_SEQ;
        this.ID_DUP = ID_DUP;
        this.ITEM = ITEM;
        this.PREM_YEAR = PREM_YEAR;
        this.PASS_TEAR = PASS_TEAR;
        this.POLI_YY = POLI_YY;
        this.POLI_MM = POLI_MM;
        this.POLI_DD = POLI_DD;
        this.UNITS = UNITS;
        this.INVT_BONUS = INVT_BONUS;
        this.INVT_INT = INVT_INT;
        this.DVD_TYPE = DVD_TYPE;
        this.PROD_JOB = PROD_JOB;
        this.PROD_ID = PROD_ID;
        this.PROD_YY = PROD_YY;
        this.PROD_MM = PROD_MM;
        this.PROD_DD = PROD_DD;
        this.NOTF_YY = NOTF_YY;
        this.NOTF_MM = NOTF_MM;
        this.NOTF_DD = NOTF_DD;
        this.DPAW_OP = DPAW_OP;
        this.DPAW_NO = DPAW_NO;
        this.DRAW_YY = DRAW_YY;
        this.DRAW_MM = DRAW_MM;
        this.DRAW_DD = DRAW_DD;
        this.CANCEL_YY = CANCEL_YY;
        this.CANCEL_MM = CANCEL_MM;
        this.CANCEL_DD = CANCEL_DD;
        this.FUND_CODE = FUND_CODE;
        this.PRICE_YY = PRICE_YY;
        this.PRICE_MM = PRICE_MM;
        this.PRICE_DD = PRICE_DD;
        this.EXCH_YY = EXCH_YY;
        this.EXCH_MM = EXCH_MM;
        this.EXCH_DD = EXCH_DD;
        this.ECHG_BRT = ECHG_BRT;
        this.FC_AMT = FC_AMT;
        this.DLY_INT = DLY_INT;
        this.UPD_ID = UPD_ID;
        this.UPD_YY = UPD_YY;
        this.UPD_MM = UPD_MM;
        this.UPD_DD = UPD_DD;
        this.FILLER_10 = FILLER_10;
        this.APPLID = APPLID;
        this.ACPTNO = ACPTNO;
    }

    /** default constructor */
    public TBCRM_FWLDETA00_DATAVO() {
    }

    /** minimal constructor */
    public TBCRM_FWLDETA00_DATAVO(String CASE_NO) {
        this.CASE_NO = CASE_NO;
    }

    public String getCASE_NO() {
        return this.CASE_NO;
    }

    public void setCASE_NO(String CASE_NO) {
        this.CASE_NO = CASE_NO;
    }

    public String getPOLICY_NO() {
        return this.POLICY_NO;
    }

    public void setPOLICY_NO(String POLICY_NO) {
        this.POLICY_NO = POLICY_NO;
    }

    public String getPOLICY_SEQ() {
        return this.POLICY_SEQ;
    }

    public void setPOLICY_SEQ(String POLICY_SEQ) {
        this.POLICY_SEQ = POLICY_SEQ;
    }

    public String getID_DUP() {
        return this.ID_DUP;
    }

    public void setID_DUP(String ID_DUP) {
        this.ID_DUP = ID_DUP;
    }

    public String getITEM() {
        return this.ITEM;
    }

    public void setITEM(String ITEM) {
        this.ITEM = ITEM;
    }

    public String getPREM_YEAR() {
        return this.PREM_YEAR;
    }

    public void setPREM_YEAR(String PREM_YEAR) {
        this.PREM_YEAR = PREM_YEAR;
    }

    public String getPASS_TEAR() {
        return this.PASS_TEAR;
    }

    public void setPASS_TEAR(String PASS_TEAR) {
        this.PASS_TEAR = PASS_TEAR;
    }

    public String getPOLI_YY() {
        return this.POLI_YY;
    }

    public void setPOLI_YY(String POLI_YY) {
        this.POLI_YY = POLI_YY;
    }

    public String getPOLI_MM() {
        return this.POLI_MM;
    }

    public void setPOLI_MM(String POLI_MM) {
        this.POLI_MM = POLI_MM;
    }

    public String getPOLI_DD() {
        return this.POLI_DD;
    }

    public void setPOLI_DD(String POLI_DD) {
        this.POLI_DD = POLI_DD;
    }

    public String getUNITS() {
        return this.UNITS;
    }

    public void setUNITS(String UNITS) {
        this.UNITS = UNITS;
    }

    public String getINVT_BONUS() {
        return this.INVT_BONUS;
    }

    public void setINVT_BONUS(String INVT_BONUS) {
        this.INVT_BONUS = INVT_BONUS;
    }

    public String getINVT_INT() {
        return this.INVT_INT;
    }

    public void setINVT_INT(String INVT_INT) {
        this.INVT_INT = INVT_INT;
    }

    public String getDVD_TYPE() {
        return this.DVD_TYPE;
    }

    public void setDVD_TYPE(String DVD_TYPE) {
        this.DVD_TYPE = DVD_TYPE;
    }

    public String getPROD_JOB() {
        return this.PROD_JOB;
    }

    public void setPROD_JOB(String PROD_JOB) {
        this.PROD_JOB = PROD_JOB;
    }

    public String getPROD_ID() {
        return this.PROD_ID;
    }

    public void setPROD_ID(String PROD_ID) {
        this.PROD_ID = PROD_ID;
    }

    public String getPROD_YY() {
        return this.PROD_YY;
    }

    public void setPROD_YY(String PROD_YY) {
        this.PROD_YY = PROD_YY;
    }

    public String getPROD_MM() {
        return this.PROD_MM;
    }

    public void setPROD_MM(String PROD_MM) {
        this.PROD_MM = PROD_MM;
    }

    public String getPROD_DD() {
        return this.PROD_DD;
    }

    public void setPROD_DD(String PROD_DD) {
        this.PROD_DD = PROD_DD;
    }

    public String getNOTF_YY() {
        return this.NOTF_YY;
    }

    public void setNOTF_YY(String NOTF_YY) {
        this.NOTF_YY = NOTF_YY;
    }

    public String getNOTF_MM() {
        return this.NOTF_MM;
    }

    public void setNOTF_MM(String NOTF_MM) {
        this.NOTF_MM = NOTF_MM;
    }

    public String getNOTF_DD() {
        return this.NOTF_DD;
    }

    public void setNOTF_DD(String NOTF_DD) {
        this.NOTF_DD = NOTF_DD;
    }

    public String getDPAW_OP() {
        return this.DPAW_OP;
    }

    public void setDPAW_OP(String DPAW_OP) {
        this.DPAW_OP = DPAW_OP;
    }

    public String getDPAW_NO() {
        return this.DPAW_NO;
    }

    public void setDPAW_NO(String DPAW_NO) {
        this.DPAW_NO = DPAW_NO;
    }

    public String getDRAW_YY() {
        return this.DRAW_YY;
    }

    public void setDRAW_YY(String DRAW_YY) {
        this.DRAW_YY = DRAW_YY;
    }

    public String getDRAW_MM() {
        return this.DRAW_MM;
    }

    public void setDRAW_MM(String DRAW_MM) {
        this.DRAW_MM = DRAW_MM;
    }

    public String getDRAW_DD() {
        return this.DRAW_DD;
    }

    public void setDRAW_DD(String DRAW_DD) {
        this.DRAW_DD = DRAW_DD;
    }

    public String getCANCEL_YY() {
        return this.CANCEL_YY;
    }

    public void setCANCEL_YY(String CANCEL_YY) {
        this.CANCEL_YY = CANCEL_YY;
    }

    public String getCANCEL_MM() {
        return this.CANCEL_MM;
    }

    public void setCANCEL_MM(String CANCEL_MM) {
        this.CANCEL_MM = CANCEL_MM;
    }

    public String getCANCEL_DD() {
        return this.CANCEL_DD;
    }

    public void setCANCEL_DD(String CANCEL_DD) {
        this.CANCEL_DD = CANCEL_DD;
    }

    public String getFUND_CODE() {
        return this.FUND_CODE;
    }

    public void setFUND_CODE(String FUND_CODE) {
        this.FUND_CODE = FUND_CODE;
    }

    public String getPRICE_YY() {
        return this.PRICE_YY;
    }

    public void setPRICE_YY(String PRICE_YY) {
        this.PRICE_YY = PRICE_YY;
    }

    public String getPRICE_MM() {
        return this.PRICE_MM;
    }

    public void setPRICE_MM(String PRICE_MM) {
        this.PRICE_MM = PRICE_MM;
    }

    public String getPRICE_DD() {
        return this.PRICE_DD;
    }

    public void setPRICE_DD(String PRICE_DD) {
        this.PRICE_DD = PRICE_DD;
    }

    public String getEXCH_YY() {
        return this.EXCH_YY;
    }

    public void setEXCH_YY(String EXCH_YY) {
        this.EXCH_YY = EXCH_YY;
    }

    public String getEXCH_MM() {
        return this.EXCH_MM;
    }

    public void setEXCH_MM(String EXCH_MM) {
        this.EXCH_MM = EXCH_MM;
    }

    public String getEXCH_DD() {
        return this.EXCH_DD;
    }

    public void setEXCH_DD(String EXCH_DD) {
        this.EXCH_DD = EXCH_DD;
    }

    public String getECHG_BRT() {
        return this.ECHG_BRT;
    }

    public void setECHG_BRT(String ECHG_BRT) {
        this.ECHG_BRT = ECHG_BRT;
    }

    public String getFC_AMT() {
        return this.FC_AMT;
    }

    public void setFC_AMT(String FC_AMT) {
        this.FC_AMT = FC_AMT;
    }

    public String getDLY_INT() {
        return this.DLY_INT;
    }

    public void setDLY_INT(String DLY_INT) {
        this.DLY_INT = DLY_INT;
    }

    public String getUPD_ID() {
        return this.UPD_ID;
    }

    public void setUPD_ID(String UPD_ID) {
        this.UPD_ID = UPD_ID;
    }

    public String getUPD_YY() {
        return this.UPD_YY;
    }

    public void setUPD_YY(String UPD_YY) {
        this.UPD_YY = UPD_YY;
    }

    public String getUPD_MM() {
        return this.UPD_MM;
    }

    public void setUPD_MM(String UPD_MM) {
        this.UPD_MM = UPD_MM;
    }

    public String getUPD_DD() {
        return this.UPD_DD;
    }

    public void setUPD_DD(String UPD_DD) {
        this.UPD_DD = UPD_DD;
    }

    public String getFILLER_10() {
        return this.FILLER_10;
    }

    public void setFILLER_10(String FILLER_10) {
        this.FILLER_10 = FILLER_10;
    }

    public String getAPPLID() {
        return this.APPLID;
    }

    public void setAPPLID(String APPLID) {
        this.APPLID = APPLID;
    }

    public String getACPTNO() {
        return this.ACPTNO;
    }

    public void setACPTNO(String ACPTNO) {
        this.ACPTNO = ACPTNO;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("CASE_NO", getCASE_NO())
            .toString();
    }

}
