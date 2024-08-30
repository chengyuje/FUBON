package com.systex.jbranch.app.common.fps.table;

import java.sql.Timestamp;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;

/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPRD_STOCK_NREXGFLVO extends VOBase {

    /** identifier field */
    private String STOCK_CODE;

    /** nullable persistent field */
    private String EXCHANGE_NAME;

    /** nullable persistent field */
    private String COUNTRY_CODE;

    /** nullable persistent field */
    private String EXG14;

    /** nullable persistent field */
    private String EXG15;

    /** nullable persistent field */
    private String EXG16;

    /** nullable persistent field */
    private String EXG17;

    /** nullable persistent field */
    private String EXG18;

    /** nullable persistent field */
    private String EXG19;

    /** nullable persistent field */
    private String EXG20;

    /** nullable persistent field */
    private String EXG21;

    /** nullable persistent field */
    private String EXG22;

    /** nullable persistent field */
    private String EXG23;

    /** nullable persistent field */
    private String EXG24;

    /** nullable persistent field */
    private String EXG25;

    /** nullable persistent field */
    private String EXG26;

    /** nullable persistent field */
    private String EXG27;

    /** nullable persistent field */
    private String EXG28;

    /** nullable persistent field */
    private String EXG29;

    /** nullable persistent field */
    private String EXG30;

    /** nullable persistent field */
    private String EXG31;

    /** nullable persistent field */
    private String EXG32;

    /** nullable persistent field */
    private String EXG33;

    /** nullable persistent field */
    private String EXG331;

    /** nullable persistent field */
    private String EXG332;

    /** nullable persistent field */
    private String EXG34;

    /** nullable persistent field */
    private String EXG35;

    /** nullable persistent field */
    private String EXG36;

    /** nullable persistent field */
    private String EXG37;

    /** nullable persistent field */
    private String EXG38;

    /** nullable persistent field */
    private String EXG39;

    /** nullable persistent field */
    private String EXG40;

    /** nullable persistent field */
    private String EXG41;

    /** nullable persistent field */
    private String EXG42;

    /** nullable persistent field */
    private String EXG43;

    /** nullable persistent field */
    private String EXG44;

    /** nullable persistent field */
    private String EXG45;

    /** nullable persistent field */
    private String EXG991;

    /** nullable persistent field */
    private String EXG992;

    /** nullable persistent field */
    private String EXG993;

    /** nullable persistent field */
    private String EXG994;

    /** nullable persistent field */
    private String EXG995;

    /** nullable persistent field */
    private String EXG996;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBPRD_STOCK_NREXGFL";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBPRD_STOCK_NREXGFLVO(String STOCK_CODE, String EXCHANGE_NAME, String COUNTRY_CODE, String EXG14, String EXG15, String EXG16, String EXG17, String EXG18, String EXG19, String EXG20, String EXG21, String EXG22, String EXG23, String EXG24, String EXG25, String EXG26, String EXG27, String EXG28, String EXG29, String EXG30, String EXG31, String EXG32, String EXG33, String EXG331, String EXG332, String EXG34, String EXG35, String EXG36, String EXG37, String EXG38, String EXG39, String EXG40, String EXG41, String EXG42, String EXG43, String EXG44, String EXG45, String EXG991, String EXG992, String EXG993, String EXG994, String EXG995, String EXG996, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.STOCK_CODE = STOCK_CODE;
        this.EXCHANGE_NAME = EXCHANGE_NAME;
        this.COUNTRY_CODE = COUNTRY_CODE;
        this.EXG14 = EXG14;
        this.EXG15 = EXG15;
        this.EXG16 = EXG16;
        this.EXG17 = EXG17;
        this.EXG18 = EXG18;
        this.EXG19 = EXG19;
        this.EXG20 = EXG20;
        this.EXG21 = EXG21;
        this.EXG22 = EXG22;
        this.EXG23 = EXG23;
        this.EXG24 = EXG24;
        this.EXG25 = EXG25;
        this.EXG26 = EXG26;
        this.EXG27 = EXG27;
        this.EXG28 = EXG28;
        this.EXG29 = EXG29;
        this.EXG30 = EXG30;
        this.EXG31 = EXG31;
        this.EXG32 = EXG32;
        this.EXG33 = EXG33;
        this.EXG331 = EXG331;
        this.EXG332 = EXG332;
        this.EXG34 = EXG34;
        this.EXG35 = EXG35;
        this.EXG36 = EXG36;
        this.EXG37 = EXG37;
        this.EXG38 = EXG38;
        this.EXG39 = EXG39;
        this.EXG40 = EXG40;
        this.EXG41 = EXG41;
        this.EXG42 = EXG42;
        this.EXG43 = EXG43;
        this.EXG44 = EXG44;
        this.EXG45 = EXG45;
        this.EXG991 = EXG991;
        this.EXG992 = EXG992;
        this.EXG993 = EXG993;
        this.EXG994 = EXG994;
        this.EXG995 = EXG995;
        this.EXG996 = EXG996;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBPRD_STOCK_NREXGFLVO() {
    }

    /** minimal constructor */
    public TBPRD_STOCK_NREXGFLVO(String STOCK_CODE) {
        this.STOCK_CODE = STOCK_CODE;
    }

    public String getSTOCK_CODE() {
        return this.STOCK_CODE;
    }

    public void setSTOCK_CODE(String STOCK_CODE) {
        this.STOCK_CODE = STOCK_CODE;
    }

    public String getEXCHANGE_NAME() {
        return this.EXCHANGE_NAME;
    }

    public void setEXCHANGE_NAME(String EXCHANGE_NAME) {
        this.EXCHANGE_NAME = EXCHANGE_NAME;
    }

    public String getCOUNTRY_CODE() {
        return this.COUNTRY_CODE;
    }

    public void setCOUNTRY_CODE(String COUNTRY_CODE) {
        this.COUNTRY_CODE = COUNTRY_CODE;
    }

    public String getEXG14() {
        return this.EXG14;
    }

    public void setEXG14(String EXG14) {
        this.EXG14 = EXG14;
    }

    public String getEXG15() {
        return this.EXG15;
    }

    public void setEXG15(String EXG15) {
        this.EXG15 = EXG15;
    }

    public String getEXG16() {
        return this.EXG16;
    }

    public void setEXG16(String EXG16) {
        this.EXG16 = EXG16;
    }

    public String getEXG17() {
        return this.EXG17;
    }

    public void setEXG17(String EXG17) {
        this.EXG17 = EXG17;
    }

    public String getEXG18() {
        return this.EXG18;
    }

    public void setEXG18(String EXG18) {
        this.EXG18 = EXG18;
    }

    public String getEXG19() {
        return this.EXG19;
    }

    public void setEXG19(String EXG19) {
        this.EXG19 = EXG19;
    }

    public String getEXG20() {
        return this.EXG20;
    }

    public void setEXG20(String EXG20) {
        this.EXG20 = EXG20;
    }

    public String getEXG21() {
        return this.EXG21;
    }

    public void setEXG21(String EXG21) {
        this.EXG21 = EXG21;
    }

    public String getEXG22() {
        return this.EXG22;
    }

    public void setEXG22(String EXG22) {
        this.EXG22 = EXG22;
    }

    public String getEXG23() {
        return this.EXG23;
    }

    public void setEXG23(String EXG23) {
        this.EXG23 = EXG23;
    }

    public String getEXG24() {
        return this.EXG24;
    }

    public void setEXG24(String EXG24) {
        this.EXG24 = EXG24;
    }

    public String getEXG25() {
        return this.EXG25;
    }

    public void setEXG25(String EXG25) {
        this.EXG25 = EXG25;
    }

    public String getEXG26() {
        return this.EXG26;
    }

    public void setEXG26(String EXG26) {
        this.EXG26 = EXG26;
    }

    public String getEXG27() {
        return this.EXG27;
    }

    public void setEXG27(String EXG27) {
        this.EXG27 = EXG27;
    }

    public String getEXG28() {
        return this.EXG28;
    }

    public void setEXG28(String EXG28) {
        this.EXG28 = EXG28;
    }

    public String getEXG29() {
        return this.EXG29;
    }

    public void setEXG29(String EXG29) {
        this.EXG29 = EXG29;
    }

    public String getEXG30() {
        return this.EXG30;
    }

    public void setEXG30(String EXG30) {
        this.EXG30 = EXG30;
    }

    public String getEXG31() {
        return this.EXG31;
    }

    public void setEXG31(String EXG31) {
        this.EXG31 = EXG31;
    }

    public String getEXG32() {
        return this.EXG32;
    }

    public void setEXG32(String EXG32) {
        this.EXG32 = EXG32;
    }

    public String getEXG33() {
        return this.EXG33;
    }

    public void setEXG33(String EXG33) {
        this.EXG33 = EXG33;
    }

    public String getEXG331() {
        return this.EXG331;
    }

    public void setEXG331(String EXG331) {
        this.EXG331 = EXG331;
    }

    public String getEXG332() {
        return this.EXG332;
    }

    public void setEXG332(String EXG332) {
        this.EXG332 = EXG332;
    }

    public String getEXG34() {
        return this.EXG34;
    }

    public void setEXG34(String EXG34) {
        this.EXG34 = EXG34;
    }

    public String getEXG35() {
        return this.EXG35;
    }

    public void setEXG35(String EXG35) {
        this.EXG35 = EXG35;
    }

    public String getEXG36() {
        return this.EXG36;
    }

    public void setEXG36(String EXG36) {
        this.EXG36 = EXG36;
    }

    public String getEXG37() {
        return this.EXG37;
    }

    public void setEXG37(String EXG37) {
        this.EXG37 = EXG37;
    }

    public String getEXG38() {
        return this.EXG38;
    }

    public void setEXG38(String EXG38) {
        this.EXG38 = EXG38;
    }

    public String getEXG39() {
        return this.EXG39;
    }

    public void setEXG39(String EXG39) {
        this.EXG39 = EXG39;
    }

    public String getEXG40() {
        return this.EXG40;
    }

    public void setEXG40(String EXG40) {
        this.EXG40 = EXG40;
    }

    public String getEXG41() {
        return this.EXG41;
    }

    public void setEXG41(String EXG41) {
        this.EXG41 = EXG41;
    }

    public String getEXG42() {
        return this.EXG42;
    }

    public void setEXG42(String EXG42) {
        this.EXG42 = EXG42;
    }

    public String getEXG43() {
        return this.EXG43;
    }

    public void setEXG43(String EXG43) {
        this.EXG43 = EXG43;
    }

    public String getEXG44() {
        return this.EXG44;
    }

    public void setEXG44(String EXG44) {
        this.EXG44 = EXG44;
    }

    public String getEXG45() {
        return this.EXG45;
    }

    public void setEXG45(String EXG45) {
        this.EXG45 = EXG45;
    }

    public String getEXG991() {
        return this.EXG991;
    }

    public void setEXG991(String EXG991) {
        this.EXG991 = EXG991;
    }

    public String getEXG992() {
        return this.EXG992;
    }

    public void setEXG992(String EXG992) {
        this.EXG992 = EXG992;
    }

    public String getEXG993() {
        return this.EXG993;
    }

    public void setEXG993(String EXG993) {
        this.EXG993 = EXG993;
    }

    public String getEXG994() {
        return this.EXG994;
    }

    public void setEXG994(String EXG994) {
        this.EXG994 = EXG994;
    }

    public String getEXG995() {
        return this.EXG995;
    }

    public void setEXG995(String EXG995) {
        this.EXG995 = EXG995;
    }

    public String getEXG996() {
        return this.EXG996;
    }

    public void setEXG996(String EXG996) {
        this.EXG996 = EXG996;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("STOCK_CODE", getSTOCK_CODE())
            .toString();
    }

}
