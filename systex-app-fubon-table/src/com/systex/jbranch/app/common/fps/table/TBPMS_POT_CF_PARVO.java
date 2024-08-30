package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPMS_POT_CF_PARVO extends VOBase {

    /** identifier field */
    private String TYPE;

    /** nullable persistent field */
    private BigDecimal ROI;

    /** nullable persistent field */
    private BigDecimal AMT_TWD;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBPMS_POT_CF_PAR";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBPMS_POT_CF_PARVO(String TYPE, BigDecimal ROI, BigDecimal AMT_TWD, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.TYPE = TYPE;
        this.ROI = ROI;
        this.AMT_TWD = AMT_TWD;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBPMS_POT_CF_PARVO() {
    }

    /** minimal constructor */
    public TBPMS_POT_CF_PARVO(String TYPE) {
        this.TYPE = TYPE;
    }

    public String getTYPE() {
        return this.TYPE;
    }

    public void setTYPE(String TYPE) {
        this.TYPE = TYPE;
    }

    public BigDecimal getROI() {
        return this.ROI;
    }

    public void setROI(BigDecimal ROI) {
        this.ROI = ROI;
    }

    public BigDecimal getAMT_TWD() {
        return this.AMT_TWD;
    }

    public void setAMT_TWD(BigDecimal AMT_TWD) {
        this.AMT_TWD = AMT_TWD;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("TYPE", getTYPE())
            .toString();
    }

}
