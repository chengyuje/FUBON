package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBINS_HOSPITAL_PARAVO extends VOBase {

    /** identifier field */
    private BigDecimal H_KEYNO;

    /** persistent field */
    private String HOSPITAL_TYPE;

    /** persistent field */
    private String WARD_TYPE;

    /** persistent field */
    private BigDecimal DAY_AMT;

    /** persistent field */
    private String STATUS;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBINS_HOSPITAL_PARA";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBINS_HOSPITAL_PARAVO(BigDecimal H_KEYNO, String HOSPITAL_TYPE, String WARD_TYPE, BigDecimal DAY_AMT, String STATUS, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.H_KEYNO = H_KEYNO;
        this.HOSPITAL_TYPE = HOSPITAL_TYPE;
        this.WARD_TYPE = WARD_TYPE;
        this.DAY_AMT = DAY_AMT;
        this.STATUS = STATUS;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBINS_HOSPITAL_PARAVO() {
    }

    /** minimal constructor */
    public TBINS_HOSPITAL_PARAVO(BigDecimal H_KEYNO, String HOSPITAL_TYPE, String WARD_TYPE, BigDecimal DAY_AMT, String STATUS) {
        this.H_KEYNO = H_KEYNO;
        this.HOSPITAL_TYPE = HOSPITAL_TYPE;
        this.WARD_TYPE = WARD_TYPE;
        this.DAY_AMT = DAY_AMT;
        this.STATUS = STATUS;
    }

    public BigDecimal getH_KEYNO() {
        return this.H_KEYNO;
    }

    public void setH_KEYNO(BigDecimal H_KEYNO) {
        this.H_KEYNO = H_KEYNO;
    }

    public String getHOSPITAL_TYPE() {
        return this.HOSPITAL_TYPE;
    }

    public void setHOSPITAL_TYPE(String HOSPITAL_TYPE) {
        this.HOSPITAL_TYPE = HOSPITAL_TYPE;
    }

    public String getWARD_TYPE() {
        return this.WARD_TYPE;
    }

    public void setWARD_TYPE(String WARD_TYPE) {
        this.WARD_TYPE = WARD_TYPE;
    }

    public BigDecimal getDAY_AMT() {
        return this.DAY_AMT;
    }

    public void setDAY_AMT(BigDecimal DAY_AMT) {
        this.DAY_AMT = DAY_AMT;
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
