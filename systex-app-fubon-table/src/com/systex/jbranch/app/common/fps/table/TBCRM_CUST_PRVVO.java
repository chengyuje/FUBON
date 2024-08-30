package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBCRM_CUST_PRVVO extends VOBase {

    /** identifier field */
    private String SEQ;

    /** nullable persistent field */
    private String CUST_ID_M;

    /** nullable persistent field */
    private String CUST_ID_S;

    /** nullable persistent field */
    private String REL_TYPE;

    /** nullable persistent field */
    private String REL_TYPE_OTH;

    /** nullable persistent field */
    private String PRV_MBR_MAST_YN;

    /** nullable persistent field */
    private String PRV_MBR_YN;

    /** nullable persistent field */
    private BigDecimal PRV_MBR_NO;

    /** nullable persistent field */
    private BigDecimal PRV_MBR_NO_NEW;

    /** nullable persistent field */
    private String PRV_APL_TYPE;

    /** nullable persistent field */
    private String PRV_STATUS;

    /** nullable persistent field */
    private Timestamp APL_DATE;

    /** nullable persistent field */
    private Timestamp ACT_DATE;

    /** nullable persistent field */
    private String BRA_MGR_EMP_ID;

    /** nullable persistent field */
    private Timestamp BRA_MGR_RPL_DATE;

    /** nullable persistent field */
    private String OP_MGR_EMP_ID;

    /** nullable persistent field */
    private Timestamp OP_MGR_RPL_DATE;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBCRM_CUST_PRV";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBCRM_CUST_PRVVO(String SEQ, String CUST_ID_M, String CUST_ID_S, String REL_TYPE, String REL_TYPE_OTH, String PRV_MBR_MAST_YN, String PRV_MBR_YN, BigDecimal PRV_MBR_NO, BigDecimal PRV_MBR_NO_NEW, String PRV_APL_TYPE, String PRV_STATUS, Timestamp APL_DATE, Timestamp ACT_DATE, String BRA_MGR_EMP_ID, Timestamp BRA_MGR_RPL_DATE, String OP_MGR_EMP_ID, Timestamp OP_MGR_RPL_DATE, String creator, Timestamp createtime, String modifier, Timestamp lastupdate, Long version) {
        this.SEQ = SEQ;
        this.CUST_ID_M = CUST_ID_M;
        this.CUST_ID_S = CUST_ID_S;
        this.REL_TYPE = REL_TYPE;
        this.REL_TYPE_OTH = REL_TYPE_OTH;
        this.PRV_MBR_MAST_YN = PRV_MBR_MAST_YN;
        this.PRV_MBR_YN = PRV_MBR_YN;
        this.PRV_MBR_NO = PRV_MBR_NO;
        this.PRV_MBR_NO_NEW = PRV_MBR_NO_NEW;
        this.PRV_APL_TYPE = PRV_APL_TYPE;
        this.PRV_STATUS = PRV_STATUS;
        this.APL_DATE = APL_DATE;
        this.ACT_DATE = ACT_DATE;
        this.BRA_MGR_EMP_ID = BRA_MGR_EMP_ID;
        this.BRA_MGR_RPL_DATE = BRA_MGR_RPL_DATE;
        this.OP_MGR_EMP_ID = OP_MGR_EMP_ID;
        this.OP_MGR_RPL_DATE = OP_MGR_RPL_DATE;
        this.creator = creator;
        this.createtime = createtime;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBCRM_CUST_PRVVO() {
    }

    /** minimal constructor */
    public TBCRM_CUST_PRVVO(String SEQ) {
        this.SEQ = SEQ;
    }

    public String getSEQ() {
        return this.SEQ;
    }

    public void setSEQ(String SEQ) {
        this.SEQ = SEQ;
    }

    public String getCUST_ID_M() {
        return this.CUST_ID_M;
    }

    public void setCUST_ID_M(String CUST_ID_M) {
        this.CUST_ID_M = CUST_ID_M;
    }

    public String getCUST_ID_S() {
        return this.CUST_ID_S;
    }

    public void setCUST_ID_S(String CUST_ID_S) {
        this.CUST_ID_S = CUST_ID_S;
    }

    public String getREL_TYPE() {
        return this.REL_TYPE;
    }

    public void setREL_TYPE(String REL_TYPE) {
        this.REL_TYPE = REL_TYPE;
    }

    public String getREL_TYPE_OTH() {
        return this.REL_TYPE_OTH;
    }

    public void setREL_TYPE_OTH(String REL_TYPE_OTH) {
        this.REL_TYPE_OTH = REL_TYPE_OTH;
    }

    public String getPRV_MBR_MAST_YN() {
        return this.PRV_MBR_MAST_YN;
    }

    public void setPRV_MBR_MAST_YN(String PRV_MBR_MAST_YN) {
        this.PRV_MBR_MAST_YN = PRV_MBR_MAST_YN;
    }

    public String getPRV_MBR_YN() {
        return this.PRV_MBR_YN;
    }

    public void setPRV_MBR_YN(String PRV_MBR_YN) {
        this.PRV_MBR_YN = PRV_MBR_YN;
    }

    public BigDecimal getPRV_MBR_NO() {
        return this.PRV_MBR_NO;
    }

    public void setPRV_MBR_NO(BigDecimal PRV_MBR_NO) {
        this.PRV_MBR_NO = PRV_MBR_NO;
    }

    public BigDecimal getPRV_MBR_NO_NEW() {
        return this.PRV_MBR_NO_NEW;
    }

    public void setPRV_MBR_NO_NEW(BigDecimal PRV_MBR_NO_NEW) {
        this.PRV_MBR_NO_NEW = PRV_MBR_NO_NEW;
    }

    public String getPRV_APL_TYPE() {
        return this.PRV_APL_TYPE;
    }

    public void setPRV_APL_TYPE(String PRV_APL_TYPE) {
        this.PRV_APL_TYPE = PRV_APL_TYPE;
    }

    public String getPRV_STATUS() {
        return this.PRV_STATUS;
    }

    public void setPRV_STATUS(String PRV_STATUS) {
        this.PRV_STATUS = PRV_STATUS;
    }

    public Timestamp getAPL_DATE() {
        return this.APL_DATE;
    }

    public void setAPL_DATE(Timestamp APL_DATE) {
        this.APL_DATE = APL_DATE;
    }

    public Timestamp getACT_DATE() {
        return this.ACT_DATE;
    }

    public void setACT_DATE(Timestamp ACT_DATE) {
        this.ACT_DATE = ACT_DATE;
    }

    public String getBRA_MGR_EMP_ID() {
        return this.BRA_MGR_EMP_ID;
    }

    public void setBRA_MGR_EMP_ID(String BRA_MGR_EMP_ID) {
        this.BRA_MGR_EMP_ID = BRA_MGR_EMP_ID;
    }

    public Timestamp getBRA_MGR_RPL_DATE() {
        return this.BRA_MGR_RPL_DATE;
    }

    public void setBRA_MGR_RPL_DATE(Timestamp BRA_MGR_RPL_DATE) {
        this.BRA_MGR_RPL_DATE = BRA_MGR_RPL_DATE;
    }

    public String getOP_MGR_EMP_ID() {
        return this.OP_MGR_EMP_ID;
    }

    public void setOP_MGR_EMP_ID(String OP_MGR_EMP_ID) {
        this.OP_MGR_EMP_ID = OP_MGR_EMP_ID;
    }

    public Timestamp getOP_MGR_RPL_DATE() {
        return this.OP_MGR_RPL_DATE;
    }

    public void setOP_MGR_RPL_DATE(Timestamp OP_MGR_RPL_DATE) {
        this.OP_MGR_RPL_DATE = OP_MGR_RPL_DATE;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("SEQ", getSEQ())
            .toString();
    }

}
