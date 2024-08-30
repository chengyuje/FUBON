package com.systex.jbranch.app.common.fps.table;

import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPRD_NANOVO extends VOBase {

    /** identifier field */
    private String PRD_ID;

    /** nullable persistent field */
    private String PRD_NAME;

    /** nullable persistent field */
    private String RISKCATE_ID;

    /** nullable persistent field */
    private String STOCK_BOND_TYPE;

    /** nullable persistent field */
    private String CORE_TYPE;

    /** persistent field */
    private String CURRENCY_STD_ID;

    /** nullable persistent field */
    private String IS_SALE;

    /** nullable persistent field */
    private String INV_LEVEL;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBPRD_NANO";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBPRD_NANOVO(String PRD_ID, String PRD_NAME, String RISKCATE_ID, String STOCK_BOND_TYPE, String CORE_TYPE, String CURRENCY_STD_ID, String IS_SALE, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, String INV_LEVEL, Long version) {
        this.PRD_ID = PRD_ID;
        this.PRD_NAME = PRD_NAME;
        this.RISKCATE_ID = RISKCATE_ID;
        this.STOCK_BOND_TYPE = STOCK_BOND_TYPE;
        this.CORE_TYPE = CORE_TYPE;
        this.CURRENCY_STD_ID = CURRENCY_STD_ID;
        this.IS_SALE = IS_SALE;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.INV_LEVEL = INV_LEVEL;
        this.version = version;
    }

    /** default constructor */
    public TBPRD_NANOVO() {
    }

    /** minimal constructor */
    public TBPRD_NANOVO(String PRD_ID, String CURRENCY_STD_ID) {
        this.PRD_ID = PRD_ID;
        this.CURRENCY_STD_ID = CURRENCY_STD_ID;
    }

    public String getPRD_ID() {
        return this.PRD_ID;
    }

    public void setPRD_ID(String PRD_ID) {
        this.PRD_ID = PRD_ID;
    }

    public String getPRD_NAME() {
        return this.PRD_NAME;
    }

    public void setPRD_NAME(String PRD_NAME) {
        this.PRD_NAME = PRD_NAME;
    }

    public String getRISKCATE_ID() {
        return this.RISKCATE_ID;
    }

    public void setRISKCATE_ID(String RISKCATE_ID) {
        this.RISKCATE_ID = RISKCATE_ID;
    }

    public String getSTOCK_BOND_TYPE() {
        return this.STOCK_BOND_TYPE;
    }

    public void setSTOCK_BOND_TYPE(String STOCK_BOND_TYPE) {
        this.STOCK_BOND_TYPE = STOCK_BOND_TYPE;
    }

    public String getCORE_TYPE() {
        return this.CORE_TYPE;
    }

    public void setCORE_TYPE(String CORE_TYPE) {
        this.CORE_TYPE = CORE_TYPE;
    }

    public String getCURRENCY_STD_ID() {
        return this.CURRENCY_STD_ID;
    }

    public void setCURRENCY_STD_ID(String CURRENCY_STD_ID) {
        this.CURRENCY_STD_ID = CURRENCY_STD_ID;
    }

    public String getIS_SALE() {
        return this.IS_SALE;
    }

    public void setIS_SALE(String IS_SALE) {
        this.IS_SALE = IS_SALE;
    }

    public String getINV_LEVEL() {
        return this.INV_LEVEL;
    }

    public void setINV_LEVEL(String INV_LEVEL) {
        this.INV_LEVEL = INV_LEVEL;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("PRD_ID", getPRD_ID())
            .toString();
    }

}
