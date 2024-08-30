package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBCRM_TRS_PRJ_DTL_HISTVO extends VOBase {

    /** identifier field */
    private BigDecimal SEQ;

    /** persistent field */
    private String PRJ_ID;

    /** persistent field */
    private String CUST_ID;

    /** nullable persistent field */
    private String ORG_AO_CODE;

    /** nullable persistent field */
    private String NEW_AO_CODE;

    /** nullable persistent field */
    private String ORG_AO_BRH;

    /** nullable persistent field */
    private String NEW_AO_BRH;

    /** nullable persistent field */
    private String DATA_01;

    /** nullable persistent field */
    private String DATA_02;

    /** nullable persistent field */
    private String DATA_03;

    /** nullable persistent field */
    private String DATA_04;

    /** nullable persistent field */
    private String DATA_05;

    /** nullable persistent field */
    private String DATA_06;

    /** nullable persistent field */
    private String DATA_07;

    /** nullable persistent field */
    private String DATA_08;

    /** nullable persistent field */
    private String DATA_09;

    /** nullable persistent field */
    private String DATA_10;

    /** nullable persistent field */
    private String DATA_11;

    /** nullable persistent field */
    private String DATA_12;

    /** nullable persistent field */
    private String DATA_13;

    /** nullable persistent field */
    private String DATA_14;

    /** nullable persistent field */
    private String DATA_15;

    /** nullable persistent field */
    private String IMP_SUCCESS_YN;

    /** nullable persistent field */
    private String IMP_STATUS;

    /** nullable persistent field */
    private String TRS_SUCCESS_YN;

    /** nullable persistent field */
    private String IMP_FILE_NAME;

    /** nullable persistent field */
    private String IMP_EMP_ID;

    /** nullable persistent field */
    private Timestamp IMP_DATETIME;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBCRM_TRS_PRJ_DTL_HIST";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBCRM_TRS_PRJ_DTL_HISTVO(BigDecimal SEQ, String PRJ_ID, String CUST_ID, String ORG_AO_CODE, String NEW_AO_CODE, String ORG_AO_BRH, String NEW_AO_BRH, String DATA_01, String DATA_02, String DATA_03, String DATA_04, String DATA_05, String DATA_06, String DATA_07, String DATA_08, String DATA_09, String DATA_10, String DATA_11, String DATA_12, String DATA_13, String DATA_14, String DATA_15, String IMP_SUCCESS_YN, String IMP_STATUS, String TRS_SUCCESS_YN, String IMP_FILE_NAME, String IMP_EMP_ID, Timestamp IMP_DATETIME, String creator, Timestamp createtime, String modifier, Timestamp lastupdate, Long version) {
        this.SEQ = SEQ;
        this.PRJ_ID = PRJ_ID;
        this.CUST_ID = CUST_ID;
        this.ORG_AO_CODE = ORG_AO_CODE;
        this.NEW_AO_CODE = NEW_AO_CODE;
        this.ORG_AO_BRH = ORG_AO_BRH;
        this.NEW_AO_BRH = NEW_AO_BRH;
        this.DATA_01 = DATA_01;
        this.DATA_02 = DATA_02;
        this.DATA_03 = DATA_03;
        this.DATA_04 = DATA_04;
        this.DATA_05 = DATA_05;
        this.DATA_06 = DATA_06;
        this.DATA_07 = DATA_07;
        this.DATA_08 = DATA_08;
        this.DATA_09 = DATA_09;
        this.DATA_10 = DATA_10;
        this.DATA_11 = DATA_11;
        this.DATA_12 = DATA_12;
        this.DATA_13 = DATA_13;
        this.DATA_14 = DATA_14;
        this.DATA_15 = DATA_15;
        this.IMP_SUCCESS_YN = IMP_SUCCESS_YN;
        this.IMP_STATUS = IMP_STATUS;
        this.TRS_SUCCESS_YN = TRS_SUCCESS_YN;
        this.IMP_FILE_NAME = IMP_FILE_NAME;
        this.IMP_EMP_ID = IMP_EMP_ID;
        this.IMP_DATETIME = IMP_DATETIME;
        this.creator = creator;
        this.createtime = createtime;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBCRM_TRS_PRJ_DTL_HISTVO() {
    }

    /** minimal constructor */
    public TBCRM_TRS_PRJ_DTL_HISTVO(BigDecimal SEQ, String PRJ_ID, String CUST_ID) {
        this.SEQ = SEQ;
        this.PRJ_ID = PRJ_ID;
        this.CUST_ID = CUST_ID;
    }

    public BigDecimal getSEQ() {
        return this.SEQ;
    }

    public void setSEQ(BigDecimal SEQ) {
        this.SEQ = SEQ;
    }

    public String getPRJ_ID() {
        return this.PRJ_ID;
    }

    public void setPRJ_ID(String PRJ_ID) {
        this.PRJ_ID = PRJ_ID;
    }

    public String getCUST_ID() {
        return this.CUST_ID;
    }

    public void setCUST_ID(String CUST_ID) {
        this.CUST_ID = CUST_ID;
    }

    public String getORG_AO_CODE() {
        return this.ORG_AO_CODE;
    }

    public void setORG_AO_CODE(String ORG_AO_CODE) {
        this.ORG_AO_CODE = ORG_AO_CODE;
    }

    public String getNEW_AO_CODE() {
        return this.NEW_AO_CODE;
    }

    public void setNEW_AO_CODE(String NEW_AO_CODE) {
        this.NEW_AO_CODE = NEW_AO_CODE;
    }

    public String getORG_AO_BRH() {
        return this.ORG_AO_BRH;
    }

    public void setORG_AO_BRH(String ORG_AO_BRH) {
        this.ORG_AO_BRH = ORG_AO_BRH;
    }

    public String getNEW_AO_BRH() {
        return this.NEW_AO_BRH;
    }

    public void setNEW_AO_BRH(String NEW_AO_BRH) {
        this.NEW_AO_BRH = NEW_AO_BRH;
    }

    public String getDATA_01() {
        return this.DATA_01;
    }

    public void setDATA_01(String DATA_01) {
        this.DATA_01 = DATA_01;
    }

    public String getDATA_02() {
        return this.DATA_02;
    }

    public void setDATA_02(String DATA_02) {
        this.DATA_02 = DATA_02;
    }

    public String getDATA_03() {
        return this.DATA_03;
    }

    public void setDATA_03(String DATA_03) {
        this.DATA_03 = DATA_03;
    }

    public String getDATA_04() {
        return this.DATA_04;
    }

    public void setDATA_04(String DATA_04) {
        this.DATA_04 = DATA_04;
    }

    public String getDATA_05() {
        return this.DATA_05;
    }

    public void setDATA_05(String DATA_05) {
        this.DATA_05 = DATA_05;
    }

    public String getDATA_06() {
        return this.DATA_06;
    }

    public void setDATA_06(String DATA_06) {
        this.DATA_06 = DATA_06;
    }

    public String getDATA_07() {
        return this.DATA_07;
    }

    public void setDATA_07(String DATA_07) {
        this.DATA_07 = DATA_07;
    }

    public String getDATA_08() {
        return this.DATA_08;
    }

    public void setDATA_08(String DATA_08) {
        this.DATA_08 = DATA_08;
    }

    public String getDATA_09() {
        return this.DATA_09;
    }

    public void setDATA_09(String DATA_09) {
        this.DATA_09 = DATA_09;
    }

    public String getDATA_10() {
        return this.DATA_10;
    }

    public void setDATA_10(String DATA_10) {
        this.DATA_10 = DATA_10;
    }

    public String getDATA_11() {
        return this.DATA_11;
    }

    public void setDATA_11(String DATA_11) {
        this.DATA_11 = DATA_11;
    }

    public String getDATA_12() {
        return this.DATA_12;
    }

    public void setDATA_12(String DATA_12) {
        this.DATA_12 = DATA_12;
    }

    public String getDATA_13() {
        return this.DATA_13;
    }

    public void setDATA_13(String DATA_13) {
        this.DATA_13 = DATA_13;
    }

    public String getDATA_14() {
        return this.DATA_14;
    }

    public void setDATA_14(String DATA_14) {
        this.DATA_14 = DATA_14;
    }

    public String getDATA_15() {
        return this.DATA_15;
    }

    public void setDATA_15(String DATA_15) {
        this.DATA_15 = DATA_15;
    }

    public String getIMP_SUCCESS_YN() {
        return this.IMP_SUCCESS_YN;
    }

    public void setIMP_SUCCESS_YN(String IMP_SUCCESS_YN) {
        this.IMP_SUCCESS_YN = IMP_SUCCESS_YN;
    }

    public String getIMP_STATUS() {
        return this.IMP_STATUS;
    }

    public void setIMP_STATUS(String IMP_STATUS) {
        this.IMP_STATUS = IMP_STATUS;
    }

    public String getTRS_SUCCESS_YN() {
        return this.TRS_SUCCESS_YN;
    }

    public void setTRS_SUCCESS_YN(String TRS_SUCCESS_YN) {
        this.TRS_SUCCESS_YN = TRS_SUCCESS_YN;
    }

    public String getIMP_FILE_NAME() {
        return this.IMP_FILE_NAME;
    }

    public void setIMP_FILE_NAME(String IMP_FILE_NAME) {
        this.IMP_FILE_NAME = IMP_FILE_NAME;
    }

    public String getIMP_EMP_ID() {
        return this.IMP_EMP_ID;
    }

    public void setIMP_EMP_ID(String IMP_EMP_ID) {
        this.IMP_EMP_ID = IMP_EMP_ID;
    }

    public Timestamp getIMP_DATETIME() {
        return this.IMP_DATETIME;
    }

    public void setIMP_DATETIME(Timestamp IMP_DATETIME) {
        this.IMP_DATETIME = IMP_DATETIME;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("SEQ", getSEQ())
            .toString();
    }

}
