package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBCRM_TRS_DELCAM_ULIST_DTLVO extends VOBase {

    /** identifier field */
    private BigDecimal SEQ;

    /** nullable persistent field */
    private BigDecimal LIST_SEQ;

    /** nullable persistent field */
    private String ULISTNO;

    /** nullable persistent field */
    private String SEQ_NO;

    /** nullable persistent field */
    private String BRH_COD;

    /** nullable persistent field */
    private String CONTACT_ADDRESS;

    /** nullable persistent field */
    private String BRH_OTH;

    /** nullable persistent field */
    private String POINTER_1;

    /** nullable persistent field */
    private String POINTER_2;

    /** nullable persistent field */
    private String ADDR_CITY;

    /** nullable persistent field */
    private String ADDR_AREA;

    /** nullable persistent field */
    private BigDecimal KM_BRH_STR;

    /** nullable persistent field */
    private BigDecimal KM_BRH_OTH;

    /** nullable persistent field */
    private String BRH_LST;

    /** nullable persistent field */
    private BigDecimal KM_BRH_LST;

    /** nullable persistent field */
    private String BRH_SEC;

    /** nullable persistent field */
    private BigDecimal KM_BRH_SEC;

    /** nullable persistent field */
    private String DELCAM_BACK_YN;
    
    /** nullable persistent field */
    private String SNAP_DATE;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBCRM_TRS_DELCAM_ULIST_DTL";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBCRM_TRS_DELCAM_ULIST_DTLVO(BigDecimal SEQ, BigDecimal LIST_SEQ, String ULISTNO, String SEQ_NO, String BRH_COD, String CONTACT_ADDRESS, String BRH_OTH, String POINTER_1, String POINTER_2, String ADDR_CITY, String ADDR_AREA, BigDecimal KM_BRH_STR, BigDecimal KM_BRH_OTH, String BRH_LST, BigDecimal KM_BRH_LST, String BRH_SEC, BigDecimal KM_BRH_SEC, String DELCAM_BACK_YN, String creator, Timestamp createtime, String modifier, Timestamp lastupdate, Long version,  String SNAP_DATE) {
        this.SEQ = SEQ;
        this.LIST_SEQ = LIST_SEQ;
        this.ULISTNO = ULISTNO;
        this.SEQ_NO = SEQ_NO;
        this.BRH_COD = BRH_COD;
        this.CONTACT_ADDRESS = CONTACT_ADDRESS;
        this.BRH_OTH = BRH_OTH;
        this.POINTER_1 = POINTER_1;
        this.POINTER_2 = POINTER_2;
        this.ADDR_CITY = ADDR_CITY;
        this.ADDR_AREA = ADDR_AREA;
        this.KM_BRH_STR = KM_BRH_STR;
        this.KM_BRH_OTH = KM_BRH_OTH;
        this.BRH_LST = BRH_LST;
        this.KM_BRH_LST = KM_BRH_LST;
        this.BRH_SEC = BRH_SEC;
        this.KM_BRH_SEC = KM_BRH_SEC;
        this.DELCAM_BACK_YN = DELCAM_BACK_YN;
        this.creator = creator;
        this.createtime = createtime;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
        this.SNAP_DATE = SNAP_DATE;
    }

    /** default constructor */
    public TBCRM_TRS_DELCAM_ULIST_DTLVO() {
    }

    /** minimal constructor */
    public TBCRM_TRS_DELCAM_ULIST_DTLVO(BigDecimal SEQ) {
        this.SEQ = SEQ;
    }

    public BigDecimal getSEQ() {
        return this.SEQ;
    }

    public void setSEQ(BigDecimal SEQ) {
        this.SEQ = SEQ;
    }

    public BigDecimal getLIST_SEQ() {
        return this.LIST_SEQ;
    }

    public void setLIST_SEQ(BigDecimal LIST_SEQ) {
        this.LIST_SEQ = LIST_SEQ;
    }

    public String getULISTNO() {
        return this.ULISTNO;
    }

    public void setULISTNO(String ULISTNO) {
        this.ULISTNO = ULISTNO;
    }

    public String getSEQ_NO() {
        return this.SEQ_NO;
    }

    public void setSEQ_NO(String SEQ_NO) {
        this.SEQ_NO = SEQ_NO;
    }

    public String getBRH_COD() {
        return this.BRH_COD;
    }

    public void setBRH_COD(String BRH_COD) {
        this.BRH_COD = BRH_COD;
    }

    public String getCONTACT_ADDRESS() {
        return this.CONTACT_ADDRESS;
    }

    public void setCONTACT_ADDRESS(String CONTACT_ADDRESS) {
        this.CONTACT_ADDRESS = CONTACT_ADDRESS;
    }

    public String getBRH_OTH() {
        return this.BRH_OTH;
    }

    public void setBRH_OTH(String BRH_OTH) {
        this.BRH_OTH = BRH_OTH;
    }

    public String getPOINTER_1() {
        return this.POINTER_1;
    }

    public void setPOINTER_1(String POINTER_1) {
        this.POINTER_1 = POINTER_1;
    }

    public String getPOINTER_2() {
        return this.POINTER_2;
    }

    public void setPOINTER_2(String POINTER_2) {
        this.POINTER_2 = POINTER_2;
    }

    public String getADDR_CITY() {
        return this.ADDR_CITY;
    }

    public void setADDR_CITY(String ADDR_CITY) {
        this.ADDR_CITY = ADDR_CITY;
    }

    public String getADDR_AREA() {
        return this.ADDR_AREA;
    }

    public void setADDR_AREA(String ADDR_AREA) {
        this.ADDR_AREA = ADDR_AREA;
    }

    public BigDecimal getKM_BRH_STR() {
        return this.KM_BRH_STR;
    }

    public void setKM_BRH_STR(BigDecimal KM_BRH_STR) {
        this.KM_BRH_STR = KM_BRH_STR;
    }

    public BigDecimal getKM_BRH_OTH() {
        return this.KM_BRH_OTH;
    }

    public void setKM_BRH_OTH(BigDecimal KM_BRH_OTH) {
        this.KM_BRH_OTH = KM_BRH_OTH;
    }

    public String getBRH_LST() {
        return this.BRH_LST;
    }

    public void setBRH_LST(String BRH_LST) {
        this.BRH_LST = BRH_LST;
    }

    public BigDecimal getKM_BRH_LST() {
        return this.KM_BRH_LST;
    }

    public void setKM_BRH_LST(BigDecimal KM_BRH_LST) {
        this.KM_BRH_LST = KM_BRH_LST;
    }

    public String getBRH_SEC() {
        return this.BRH_SEC;
    }

    public void setBRH_SEC(String BRH_SEC) {
        this.BRH_SEC = BRH_SEC;
    }

    public BigDecimal getKM_BRH_SEC() {
        return this.KM_BRH_SEC;
    }

    public void setKM_BRH_SEC(BigDecimal KM_BRH_SEC) {
        this.KM_BRH_SEC = KM_BRH_SEC;
    }

    public String getDELCAM_BACK_YN() {
        return this.DELCAM_BACK_YN;
    }

    public void setDELCAM_BACK_YN(String DELCAM_BACK_YN) {
        this.DELCAM_BACK_YN = DELCAM_BACK_YN;
    }

    public String getSNAP_DATE() {
		return SNAP_DATE;
	}

	public void setSNAP_DATE(String SNAP_DATE) {
		this.SNAP_DATE = SNAP_DATE;
	}

	public void checkDefaultValue() {
         if (DELCAM_BACK_YN == null) {
             this.DELCAM_BACK_YN="N";
         }
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("SEQ", getSEQ())
            .toString();
    }

}
