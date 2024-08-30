package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;

/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPMS_INS_OFFSETVO extends VOBase {

    /** identifier field */
    private String INSFNO;

    /** nullable persistent field */
    private BigDecimal AIF_ORGD;

    /** nullable persistent field */
    private String INS_NBR;

    /** nullable persistent field */
    private String CRCY_TYPE;

    /** nullable persistent field */
    private String PP_TYPE;

    /** nullable persistent field */
    private String AP_EMP_ID;

    /** nullable persistent field */
    private BigDecimal AF_NTD;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBPMS_INS_OFFSET";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBPMS_INS_OFFSETVO(String INSFNO, BigDecimal AIF_ORGD, String INS_NBR, String CRCY_TYPE, String PP_TYPE, String AP_EMP_ID, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, BigDecimal AF_NTD, Long version) {
        this.INSFNO = INSFNO;
        this.AIF_ORGD = AIF_ORGD;
        this.INS_NBR = INS_NBR;
        this.CRCY_TYPE = CRCY_TYPE;
        this.PP_TYPE = PP_TYPE;
        this.AP_EMP_ID = AP_EMP_ID;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.AF_NTD = AF_NTD;
        this.version = version;
    }

    /** default constructor */
    public TBPMS_INS_OFFSETVO() {
    }

    /** minimal constructor */
    public TBPMS_INS_OFFSETVO(String INSFNO) {
        this.INSFNO = INSFNO;
    }

    public String getINSFNO() {
        return this.INSFNO;
    }

    public void setINSFNO(String INSFNO) {
        this.INSFNO = INSFNO;
    }

    public BigDecimal getAIF_ORGD() {
        return this.AIF_ORGD;
    }

    public void setAIF_ORGD(BigDecimal AIF_ORGD) {
        this.AIF_ORGD = AIF_ORGD;
    }

    public String getINS_NBR() {
        return this.INS_NBR;
    }

    public void setINS_NBR(String INS_NBR) {
        this.INS_NBR = INS_NBR;
    }

    public String getCRCY_TYPE() {
        return this.CRCY_TYPE;
    }

    public void setCRCY_TYPE(String CRCY_TYPE) {
        this.CRCY_TYPE = CRCY_TYPE;
    }

    public String getPP_TYPE() {
        return this.PP_TYPE;
    }

    public void setPP_TYPE(String PP_TYPE) {
        this.PP_TYPE = PP_TYPE;
    }

    public String getAP_EMP_ID() {
        return this.AP_EMP_ID;
    }

    public void setAP_EMP_ID(String AP_EMP_ID) {
        this.AP_EMP_ID = AP_EMP_ID;
    }

    public BigDecimal getAF_NTD() {
        return this.AF_NTD;
    }

    public void setAF_NTD(BigDecimal AF_NTD) {
        this.AF_NTD = AF_NTD;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("INSFNO", getINSFNO())
            .toString();
    }

}
