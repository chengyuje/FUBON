package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPRD_INSINFO_REVIEWVO extends VOBase {

    /** identifier field */
    private BigDecimal SEQ;

    /** nullable persistent field */
    private String PRD_ID;

    /** nullable persistent field */
    private String INS_TYPE;

    /** nullable persistent field */
    private BigDecimal INS_SAGE;

    /** nullable persistent field */
    private BigDecimal INS_EAGE;

    /** nullable persistent field */
    private String IS_SALE;

    /** nullable persistent field */
    private Timestamp SALE_SDATE;

    /** nullable persistent field */
    private Timestamp SALE_EDATE;

    /** nullable persistent field */
    private String OBU_BUY;

    /** nullable persistent field */
    private String GUARANTEE_ANNUAL;

    /** nullable persistent field */
    private String COM01;

    /** nullable persistent field */
    private String COM02;

    /** nullable persistent field */
    private String IPO;

    /** nullable persistent field */
    private Timestamp IPO_SDATE;

    /** nullable persistent field */
    private Timestamp IPO_EDATE;

    /** nullable persistent field */
    private BigDecimal PAYBACK_RATE;

    /** nullable persistent field */
    private BigDecimal IRR;

    /** nullable persistent field */
    private String IS_RETIRED;

    /** nullable persistent field */
    private String IS_EDUCATION;

    /** nullable persistent field */
    private String IS_PURPOSE;

    /** nullable persistent field */
    private String IS_LIFE_INS;

    /** nullable persistent field */
    private String IS_ACCIDENT;

    /** nullable persistent field */
    private String IS_MEDICAL;

    /** nullable persistent field */
    private String IS_DISEASES;

    /** nullable persistent field */
    private String ACT_TYPE;

    /** nullable persistent field */
    private String REVIEW_STATUS;

    /** nullable persistent field */
    private String IS_INV;

    /** nullable persistent field */
    private BigDecimal BASE_AMT_OF_PURCHASE;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBPRD_INSINFO_REVIEW";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBPRD_INSINFO_REVIEWVO(BigDecimal SEQ, String PRD_ID, String INS_TYPE, BigDecimal INS_SAGE, BigDecimal INS_EAGE, String IS_SALE, Timestamp SALE_SDATE, Timestamp SALE_EDATE, String OBU_BUY, String GUARANTEE_ANNUAL, String COM01, String COM02, String IPO, Timestamp IPO_SDATE, Timestamp IPO_EDATE, BigDecimal PAYBACK_RATE, BigDecimal IRR, String IS_RETIRED, String IS_EDUCATION, String IS_PURPOSE, String IS_LIFE_INS, String IS_ACCIDENT, String IS_MEDICAL, String IS_DISEASES, String ACT_TYPE, String REVIEW_STATUS, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, String IS_INV, BigDecimal BASE_AMT_OF_PURCHASE, Long version) {
        this.SEQ = SEQ;
        this.PRD_ID = PRD_ID;
        this.INS_TYPE = INS_TYPE;
        this.INS_SAGE = INS_SAGE;
        this.INS_EAGE = INS_EAGE;
        this.IS_SALE = IS_SALE;
        this.SALE_SDATE = SALE_SDATE;
        this.SALE_EDATE = SALE_EDATE;
        this.OBU_BUY = OBU_BUY;
        this.GUARANTEE_ANNUAL = GUARANTEE_ANNUAL;
        this.COM01 = COM01;
        this.COM02 = COM02;
        this.IPO = IPO;
        this.IPO_SDATE = IPO_SDATE;
        this.IPO_EDATE = IPO_EDATE;
        this.PAYBACK_RATE = PAYBACK_RATE;
        this.IRR = IRR;
        this.IS_RETIRED = IS_RETIRED;
        this.IS_EDUCATION = IS_EDUCATION;
        this.IS_PURPOSE = IS_PURPOSE;
        this.IS_LIFE_INS = IS_LIFE_INS;
        this.IS_ACCIDENT = IS_ACCIDENT;
        this.IS_MEDICAL = IS_MEDICAL;
        this.IS_DISEASES = IS_DISEASES;
        this.ACT_TYPE = ACT_TYPE;
        this.REVIEW_STATUS = REVIEW_STATUS;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.IS_INV = IS_INV;
        this.BASE_AMT_OF_PURCHASE = BASE_AMT_OF_PURCHASE;
        this.version = version;
    }

    /** default constructor */
    public TBPRD_INSINFO_REVIEWVO() {
    }

    /** minimal constructor */
    public TBPRD_INSINFO_REVIEWVO(BigDecimal SEQ) {
        this.SEQ = SEQ;
    }

    public BigDecimal getSEQ() {
        return this.SEQ;
    }

    public void setSEQ(BigDecimal SEQ) {
        this.SEQ = SEQ;
    }

    public String getPRD_ID() {
        return this.PRD_ID;
    }

    public void setPRD_ID(String PRD_ID) {
        this.PRD_ID = PRD_ID;
    }

    public String getINS_TYPE() {
        return this.INS_TYPE;
    }

    public void setINS_TYPE(String INS_TYPE) {
        this.INS_TYPE = INS_TYPE;
    }

    public BigDecimal getINS_SAGE() {
        return this.INS_SAGE;
    }

    public void setINS_SAGE(BigDecimal INS_SAGE) {
        this.INS_SAGE = INS_SAGE;
    }

    public BigDecimal getINS_EAGE() {
        return this.INS_EAGE;
    }

    public void setINS_EAGE(BigDecimal INS_EAGE) {
        this.INS_EAGE = INS_EAGE;
    }

    public String getIS_SALE() {
        return this.IS_SALE;
    }

    public void setIS_SALE(String IS_SALE) {
        this.IS_SALE = IS_SALE;
    }

    public Timestamp getSALE_SDATE() {
        return this.SALE_SDATE;
    }

    public void setSALE_SDATE(Timestamp SALE_SDATE) {
        this.SALE_SDATE = SALE_SDATE;
    }

    public Timestamp getSALE_EDATE() {
        return this.SALE_EDATE;
    }

    public void setSALE_EDATE(Timestamp SALE_EDATE) {
        this.SALE_EDATE = SALE_EDATE;
    }

    public String getOBU_BUY() {
        return this.OBU_BUY;
    }

    public void setOBU_BUY(String OBU_BUY) {
        this.OBU_BUY = OBU_BUY;
    }

    public String getGUARANTEE_ANNUAL() {
        return this.GUARANTEE_ANNUAL;
    }

    public void setGUARANTEE_ANNUAL(String GUARANTEE_ANNUAL) {
        this.GUARANTEE_ANNUAL = GUARANTEE_ANNUAL;
    }

    public String getCOM01() {
        return this.COM01;
    }

    public void setCOM01(String COM01) {
        this.COM01 = COM01;
    }

    public String getCOM02() {
        return this.COM02;
    }

    public void setCOM02(String COM02) {
        this.COM02 = COM02;
    }

    public String getIPO() {
        return this.IPO;
    }

    public void setIPO(String IPO) {
        this.IPO = IPO;
    }

    public Timestamp getIPO_SDATE() {
        return this.IPO_SDATE;
    }

    public void setIPO_SDATE(Timestamp IPO_SDATE) {
        this.IPO_SDATE = IPO_SDATE;
    }

    public Timestamp getIPO_EDATE() {
        return this.IPO_EDATE;
    }

    public void setIPO_EDATE(Timestamp IPO_EDATE) {
        this.IPO_EDATE = IPO_EDATE;
    }

    public BigDecimal getPAYBACK_RATE() {
        return this.PAYBACK_RATE;
    }

    public void setPAYBACK_RATE(BigDecimal PAYBACK_RATE) {
        this.PAYBACK_RATE = PAYBACK_RATE;
    }

    public BigDecimal getIRR() {
        return this.IRR;
    }

    public void setIRR(BigDecimal IRR) {
        this.IRR = IRR;
    }

    public String getIS_RETIRED() {
        return this.IS_RETIRED;
    }

    public void setIS_RETIRED(String IS_RETIRED) {
        this.IS_RETIRED = IS_RETIRED;
    }

    public String getIS_EDUCATION() {
        return this.IS_EDUCATION;
    }

    public void setIS_EDUCATION(String IS_EDUCATION) {
        this.IS_EDUCATION = IS_EDUCATION;
    }

    public String getIS_PURPOSE() {
        return this.IS_PURPOSE;
    }

    public void setIS_PURPOSE(String IS_PURPOSE) {
        this.IS_PURPOSE = IS_PURPOSE;
    }

    public String getIS_LIFE_INS() {
        return this.IS_LIFE_INS;
    }

    public void setIS_LIFE_INS(String IS_LIFE_INS) {
        this.IS_LIFE_INS = IS_LIFE_INS;
    }

    public String getIS_ACCIDENT() {
        return this.IS_ACCIDENT;
    }

    public void setIS_ACCIDENT(String IS_ACCIDENT) {
        this.IS_ACCIDENT = IS_ACCIDENT;
    }

    public String getIS_MEDICAL() {
        return this.IS_MEDICAL;
    }

    public void setIS_MEDICAL(String IS_MEDICAL) {
        this.IS_MEDICAL = IS_MEDICAL;
    }

    public String getIS_DISEASES() {
        return this.IS_DISEASES;
    }

    public void setIS_DISEASES(String IS_DISEASES) {
        this.IS_DISEASES = IS_DISEASES;
    }

    public String getACT_TYPE() {
        return this.ACT_TYPE;
    }

    public void setACT_TYPE(String ACT_TYPE) {
        this.ACT_TYPE = ACT_TYPE;
    }

    public String getREVIEW_STATUS() {
        return this.REVIEW_STATUS;
    }

    public void setREVIEW_STATUS(String REVIEW_STATUS) {
        this.REVIEW_STATUS = REVIEW_STATUS;
    }

    public String getIS_INV() {
        return this.IS_INV;
    }

    public void setIS_INV(String IS_INV) {
        this.IS_INV = IS_INV;
    }

    public BigDecimal getBASE_AMT_OF_PURCHASE() {
        return this.BASE_AMT_OF_PURCHASE;
    }

    public void setBASE_AMT_OF_PURCHASE(BigDecimal BASE_AMT_OF_PURCHASE) {
        this.BASE_AMT_OF_PURCHASE = BASE_AMT_OF_PURCHASE;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("SEQ", getSEQ())
            .toString();
    }

}
