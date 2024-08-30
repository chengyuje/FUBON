package com.systex.jbranch.app.common.fps.table;

import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBJSB_INS_PROD_TARGET_MAPVO extends VOBase {

    /** identifier field */
    private TBJSB_INS_PROD_TARGET_MAPPK comp_id;

    /** nullable persistent field */
    private String PRODUCTID_S;

    /** nullable persistent field */
    private String PRODUCTID_E;

    /** nullable persistent field */
    private String CURR_TYPE;

    /** nullable persistent field */
    private String PREMIUMTABLE;

    /** nullable persistent field */
    private Timestamp CREATEDT;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBJSB_INS_PROD_TARGET_MAP";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBJSB_INS_PROD_TARGET_MAPVO(TBJSB_INS_PROD_TARGET_MAPPK comp_id, String PRODUCTID_S, String PRODUCTID_E, String CURR_TYPE, String PREMIUMTABLE, Timestamp CREATEDT, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.comp_id = comp_id;
        this.PRODUCTID_S = PRODUCTID_S;
        this.PRODUCTID_E = PRODUCTID_E;
        this.CURR_TYPE = CURR_TYPE;
        this.PREMIUMTABLE = PREMIUMTABLE;
        this.CREATEDT = CREATEDT;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBJSB_INS_PROD_TARGET_MAPVO() {
    }

    /** minimal constructor */
    public TBJSB_INS_PROD_TARGET_MAPVO(TBJSB_INS_PROD_TARGET_MAPPK comp_id) {
        this.comp_id = comp_id;
    }

    public TBJSB_INS_PROD_TARGET_MAPPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(TBJSB_INS_PROD_TARGET_MAPPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getPRODUCTID_S() {
        return this.PRODUCTID_S;
    }

    public void setPRODUCTID_S(String PRODUCTID_S) {
        this.PRODUCTID_S = PRODUCTID_S;
    }

    public String getPRODUCTID_E() {
        return this.PRODUCTID_E;
    }

    public void setPRODUCTID_E(String PRODUCTID_E) {
        this.PRODUCTID_E = PRODUCTID_E;
    }

    public String getCURR_TYPE() {
        return this.CURR_TYPE;
    }

    public void setCURR_TYPE(String CURR_TYPE) {
        this.CURR_TYPE = CURR_TYPE;
    }

    public String getPREMIUMTABLE() {
        return this.PREMIUMTABLE;
    }

    public void setPREMIUMTABLE(String PREMIUMTABLE) {
        this.PREMIUMTABLE = PREMIUMTABLE;
    }

    public Timestamp getCREATEDT() {
        return this.CREATEDT;
    }

    public void setCREATEDT(Timestamp CREATEDT) {
        this.CREATEDT = CREATEDT;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getcomp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBJSB_INS_PROD_TARGET_MAPVO) ) return false;
        TBJSB_INS_PROD_TARGET_MAPVO castOther = (TBJSB_INS_PROD_TARGET_MAPVO) other;
        return new EqualsBuilder()
            .append(this.getcomp_id(), castOther.getcomp_id())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getcomp_id())
            .toHashCode();
    }

}
