package com.systex.jbranch.app.common.fps.table;

import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBCRM_CUST_AGR_MKT_NOTEVO extends VOBase {

    /** identifier field */
    private String CUST_ID;

    /** nullable persistent field */
    private Timestamp TXN_MTN_DATE;

    /** nullable persistent field */
    private String TXN_MTN_EMP_ID;

    /** nullable persistent field */
    private String BASE_OTHER_FLAG_P;

    /** nullable persistent field */
    private String BASE_OTHER_FLAG_L;

    /** nullable persistent field */
    private String BASE_OTHER_FLAG_S;

    /** nullable persistent field */
    private String BASE_OTHER_FLAG_I;

    /** nullable persistent field */
    private String BASE_OTHER_FLAG_U;

    /** nullable persistent field */
    private String BASE_OTHER_FLAG_M;

    /** nullable persistent field */
    private String BASE_OTHER_FLAG_N;

    /** nullable persistent field */
    private String BASE_OTHER_FLAG_F;

    /** nullable persistent field */
    private String BASE_OTHER_FLAG_D;

    /** nullable persistent field */
    private String TRAN_FLAG_P;

    /** nullable persistent field */
    private String TRAN_FLAG_L;

    /** nullable persistent field */
    private String TRAN_FLAG_S;

    /** nullable persistent field */
    private String TRAN_FLAG_I;

    /** nullable persistent field */
    private String TRAN_FLAG_U;

    /** nullable persistent field */
    private String TRAN_FLAG_M;

    /** nullable persistent field */
    private String TRAN_FLAG_N;

    /** nullable persistent field */
    private String TRAN_FLAG_F;

    /** nullable persistent field */
    private String TRAN_FLAG_D;

    /** nullable persistent field */
    private String TRAN_FLAG_T;

    /** nullable persistent field */
    private String NEW_AGMT_FLG;

    /** nullable persistent field */
    private String BIG_CO_REFUSE_PROM_YN;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBCRM_CUST_AGR_MKT_NOTE";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBCRM_CUST_AGR_MKT_NOTEVO(String CUST_ID, Timestamp TXN_MTN_DATE, String TXN_MTN_EMP_ID, String BASE_OTHER_FLAG_P, String BASE_OTHER_FLAG_L, String BASE_OTHER_FLAG_S, String BASE_OTHER_FLAG_I, String BASE_OTHER_FLAG_U, String BASE_OTHER_FLAG_M, String BASE_OTHER_FLAG_N, String BASE_OTHER_FLAG_F, String BASE_OTHER_FLAG_D, String TRAN_FLAG_P, String TRAN_FLAG_L, String TRAN_FLAG_S, String TRAN_FLAG_I, String TRAN_FLAG_U, String TRAN_FLAG_M, String TRAN_FLAG_N, String TRAN_FLAG_F, String TRAN_FLAG_D, String TRAN_FLAG_T, String NEW_AGMT_FLG, String BIG_CO_REFUSE_PROM_YN, String creator, Timestamp createtime, String modifier, Timestamp lastupdate, Long version) {
        this.CUST_ID = CUST_ID;
        this.TXN_MTN_DATE = TXN_MTN_DATE;
        this.TXN_MTN_EMP_ID = TXN_MTN_EMP_ID;
        this.BASE_OTHER_FLAG_P = BASE_OTHER_FLAG_P;
        this.BASE_OTHER_FLAG_L = BASE_OTHER_FLAG_L;
        this.BASE_OTHER_FLAG_S = BASE_OTHER_FLAG_S;
        this.BASE_OTHER_FLAG_I = BASE_OTHER_FLAG_I;
        this.BASE_OTHER_FLAG_U = BASE_OTHER_FLAG_U;
        this.BASE_OTHER_FLAG_M = BASE_OTHER_FLAG_M;
        this.BASE_OTHER_FLAG_N = BASE_OTHER_FLAG_N;
        this.BASE_OTHER_FLAG_F = BASE_OTHER_FLAG_F;
        this.BASE_OTHER_FLAG_D = BASE_OTHER_FLAG_D;
        this.TRAN_FLAG_P = TRAN_FLAG_P;
        this.TRAN_FLAG_L = TRAN_FLAG_L;
        this.TRAN_FLAG_S = TRAN_FLAG_S;
        this.TRAN_FLAG_I = TRAN_FLAG_I;
        this.TRAN_FLAG_U = TRAN_FLAG_U;
        this.TRAN_FLAG_M = TRAN_FLAG_M;
        this.TRAN_FLAG_N = TRAN_FLAG_N;
        this.TRAN_FLAG_F = TRAN_FLAG_F;
        this.TRAN_FLAG_D = TRAN_FLAG_D;
        this.TRAN_FLAG_T = TRAN_FLAG_T;
        this.NEW_AGMT_FLG = NEW_AGMT_FLG;
        this.BIG_CO_REFUSE_PROM_YN = BIG_CO_REFUSE_PROM_YN;
        this.creator = creator;
        this.createtime = createtime;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBCRM_CUST_AGR_MKT_NOTEVO() {
    }

    /** minimal constructor */
    public TBCRM_CUST_AGR_MKT_NOTEVO(String CUST_ID) {
        this.CUST_ID = CUST_ID;
    }

    public String getCUST_ID() {
        return this.CUST_ID;
    }

    public void setCUST_ID(String CUST_ID) {
        this.CUST_ID = CUST_ID;
    }

    public Timestamp getTXN_MTN_DATE() {
        return this.TXN_MTN_DATE;
    }

    public void setTXN_MTN_DATE(Timestamp TXN_MTN_DATE) {
        this.TXN_MTN_DATE = TXN_MTN_DATE;
    }

    public String getTXN_MTN_EMP_ID() {
        return this.TXN_MTN_EMP_ID;
    }

    public void setTXN_MTN_EMP_ID(String TXN_MTN_EMP_ID) {
        this.TXN_MTN_EMP_ID = TXN_MTN_EMP_ID;
    }

    public String getBASE_OTHER_FLAG_P() {
        return this.BASE_OTHER_FLAG_P;
    }

    public void setBASE_OTHER_FLAG_P(String BASE_OTHER_FLAG_P) {
        this.BASE_OTHER_FLAG_P = BASE_OTHER_FLAG_P;
    }

    public String getBASE_OTHER_FLAG_L() {
        return this.BASE_OTHER_FLAG_L;
    }

    public void setBASE_OTHER_FLAG_L(String BASE_OTHER_FLAG_L) {
        this.BASE_OTHER_FLAG_L = BASE_OTHER_FLAG_L;
    }

    public String getBASE_OTHER_FLAG_S() {
        return this.BASE_OTHER_FLAG_S;
    }

    public void setBASE_OTHER_FLAG_S(String BASE_OTHER_FLAG_S) {
        this.BASE_OTHER_FLAG_S = BASE_OTHER_FLAG_S;
    }

    public String getBASE_OTHER_FLAG_I() {
        return this.BASE_OTHER_FLAG_I;
    }

    public void setBASE_OTHER_FLAG_I(String BASE_OTHER_FLAG_I) {
        this.BASE_OTHER_FLAG_I = BASE_OTHER_FLAG_I;
    }

    public String getBASE_OTHER_FLAG_U() {
        return this.BASE_OTHER_FLAG_U;
    }

    public void setBASE_OTHER_FLAG_U(String BASE_OTHER_FLAG_U) {
        this.BASE_OTHER_FLAG_U = BASE_OTHER_FLAG_U;
    }

    public String getBASE_OTHER_FLAG_M() {
        return this.BASE_OTHER_FLAG_M;
    }

    public void setBASE_OTHER_FLAG_M(String BASE_OTHER_FLAG_M) {
        this.BASE_OTHER_FLAG_M = BASE_OTHER_FLAG_M;
    }

    public String getBASE_OTHER_FLAG_N() {
        return this.BASE_OTHER_FLAG_N;
    }

    public void setBASE_OTHER_FLAG_N(String BASE_OTHER_FLAG_N) {
        this.BASE_OTHER_FLAG_N = BASE_OTHER_FLAG_N;
    }

    public String getBASE_OTHER_FLAG_F() {
        return this.BASE_OTHER_FLAG_F;
    }

    public void setBASE_OTHER_FLAG_F(String BASE_OTHER_FLAG_F) {
        this.BASE_OTHER_FLAG_F = BASE_OTHER_FLAG_F;
    }

    public String getBASE_OTHER_FLAG_D() {
        return this.BASE_OTHER_FLAG_D;
    }

    public void setBASE_OTHER_FLAG_D(String BASE_OTHER_FLAG_D) {
        this.BASE_OTHER_FLAG_D = BASE_OTHER_FLAG_D;
    }

    public String getTRAN_FLAG_P() {
        return this.TRAN_FLAG_P;
    }

    public void setTRAN_FLAG_P(String TRAN_FLAG_P) {
        this.TRAN_FLAG_P = TRAN_FLAG_P;
    }

    public String getTRAN_FLAG_L() {
        return this.TRAN_FLAG_L;
    }

    public void setTRAN_FLAG_L(String TRAN_FLAG_L) {
        this.TRAN_FLAG_L = TRAN_FLAG_L;
    }

    public String getTRAN_FLAG_S() {
        return this.TRAN_FLAG_S;
    }

    public void setTRAN_FLAG_S(String TRAN_FLAG_S) {
        this.TRAN_FLAG_S = TRAN_FLAG_S;
    }

    public String getTRAN_FLAG_I() {
        return this.TRAN_FLAG_I;
    }

    public void setTRAN_FLAG_I(String TRAN_FLAG_I) {
        this.TRAN_FLAG_I = TRAN_FLAG_I;
    }

    public String getTRAN_FLAG_U() {
        return this.TRAN_FLAG_U;
    }

    public void setTRAN_FLAG_U(String TRAN_FLAG_U) {
        this.TRAN_FLAG_U = TRAN_FLAG_U;
    }

    public String getTRAN_FLAG_M() {
        return this.TRAN_FLAG_M;
    }

    public void setTRAN_FLAG_M(String TRAN_FLAG_M) {
        this.TRAN_FLAG_M = TRAN_FLAG_M;
    }

    public String getTRAN_FLAG_N() {
        return this.TRAN_FLAG_N;
    }

    public void setTRAN_FLAG_N(String TRAN_FLAG_N) {
        this.TRAN_FLAG_N = TRAN_FLAG_N;
    }

    public String getTRAN_FLAG_F() {
        return this.TRAN_FLAG_F;
    }

    public void setTRAN_FLAG_F(String TRAN_FLAG_F) {
        this.TRAN_FLAG_F = TRAN_FLAG_F;
    }

    public String getTRAN_FLAG_D() {
        return this.TRAN_FLAG_D;
    }

    public void setTRAN_FLAG_D(String TRAN_FLAG_D) {
        this.TRAN_FLAG_D = TRAN_FLAG_D;
    }

    public String getTRAN_FLAG_T() {
        return this.TRAN_FLAG_T;
    }

    public void setTRAN_FLAG_T(String TRAN_FLAG_T) {
        this.TRAN_FLAG_T = TRAN_FLAG_T;
    }

    public String getNEW_AGMT_FLG() {
        return this.NEW_AGMT_FLG;
    }

    public void setNEW_AGMT_FLG(String NEW_AGMT_FLG) {
        this.NEW_AGMT_FLG = NEW_AGMT_FLG;
    }

    public String getBIG_CO_REFUSE_PROM_YN() {
        return this.BIG_CO_REFUSE_PROM_YN;
    }

    public void setBIG_CO_REFUSE_PROM_YN(String BIG_CO_REFUSE_PROM_YN) {
        this.BIG_CO_REFUSE_PROM_YN = BIG_CO_REFUSE_PROM_YN;
    }

    public void checkDefaultValue() {
         if (TRAN_FLAG_T == null) {
             this.TRAN_FLAG_T="Y";
         }
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("CUST_ID", getCUST_ID())
            .toString();
    }

}
