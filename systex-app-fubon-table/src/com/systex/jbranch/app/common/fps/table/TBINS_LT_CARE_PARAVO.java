package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBINS_LT_CARE_PARAVO extends VOBase {

    /** identifier field */
    private BigDecimal H_KEYNO;

    /** persistent field */
    private String CARE_WAY;

    /** persistent field */
    private String CARE_STYLE;

    /** persistent field */
    private BigDecimal MONTH_AMT;

    /** persistent field */
    private String STATUS;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBINS_LT_CARE_PARA";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBINS_LT_CARE_PARAVO(BigDecimal H_KEYNO, String CARE_WAY, String CARE_STYLE, BigDecimal MONTH_AMT, String STATUS, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.H_KEYNO = H_KEYNO;
        this.CARE_WAY = CARE_WAY;
        this.CARE_STYLE = CARE_STYLE;
        this.MONTH_AMT = MONTH_AMT;
        this.STATUS = STATUS;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBINS_LT_CARE_PARAVO() {
    }

    /** minimal constructor */
    public TBINS_LT_CARE_PARAVO(BigDecimal H_KEYNO, String CARE_WAY, String CARE_STYLE, BigDecimal MONTH_AMT, String STATUS) {
        this.H_KEYNO = H_KEYNO;
        this.CARE_WAY = CARE_WAY;
        this.CARE_STYLE = CARE_STYLE;
        this.MONTH_AMT = MONTH_AMT;
        this.STATUS = STATUS;
    }

    public BigDecimal getH_KEYNO() {
        return this.H_KEYNO;
    }

    public void setH_KEYNO(BigDecimal H_KEYNO) {
        this.H_KEYNO = H_KEYNO;
    }

    public String getCARE_WAY() {
        return this.CARE_WAY;
    }

    public void setCARE_WAY(String CARE_WAY) {
        this.CARE_WAY = CARE_WAY;
    }

    public String getCARE_STYLE() {
        return this.CARE_STYLE;
    }

    public void setCARE_STYLE(String CARE_STYLE) {
        this.CARE_STYLE = CARE_STYLE;
    }

    public BigDecimal getMONTH_AMT() {
        return this.MONTH_AMT;
    }

    public void setMONTH_AMT(BigDecimal MONTH_AMT) {
        this.MONTH_AMT = MONTH_AMT;
    }

    public String getSTATUS() {
        return this.STATUS;
    }

    public void setSTATUS(String STATUS) {
        this.STATUS = STATUS;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("H_KEYNO", getH_KEYNO())
            .toString();
    }

}
