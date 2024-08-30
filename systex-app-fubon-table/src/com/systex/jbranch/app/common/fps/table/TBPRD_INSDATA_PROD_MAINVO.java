package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPRD_INSDATA_PROD_MAINVO extends VOBase {

    /** identifier field */
    private BigDecimal KEY_NO;

    /** nullable persistent field */
    private String PRD_ID;

    /** nullable persistent field */
    private String PRD_NAME;

    /** nullable persistent field */
    private String COM_ID;

    /** nullable persistent field */
    private String IS_MAIN;

    /** nullable persistent field */
    private String IS_SALE;

    /** nullable persistent field */
    private String IS_WL;

    /** nullable persistent field */
    private String WL_TERM;

    /** nullable persistent field */
    private String CONTENT_FILENAME;

    /** nullable persistent field */
    private String CLAUSE_FILENAME;

    /** nullable persistent field */
    private String INVEST_TYPE;

    /** nullable persistent field */
    private String ITEM_Y;

    /** nullable persistent field */
    private String ITEM_A;

    /** nullable persistent field */
    private String ITEM_X;

    /** nullable persistent field */
    private String ITEM_K;

    /** nullable persistent field */
    private String ITEM_P;

    /** nullable persistent field */
    private String ITEM_U;

    /** nullable persistent field */
    private String LIST_Y;

    /** nullable persistent field */
    private String LIST_A;

    /** nullable persistent field */
    private String LIST_X;

    /** nullable persistent field */
    private String LIST_K;

    /** nullable persistent field */
    private String LIST_P;

    /** nullable persistent field */
    private String LIST_U;

    /** nullable persistent field */
    private String LIST_E;

    /** nullable persistent field */
    private String LIST_O;

    /** nullable persistent field */
    private String IS_OVERSEA;

    /** nullable persistent field */
    private String CURR_CD;

    /** nullable persistent field */
    private String QUANTITY_STYLE;

    /** nullable persistent field */
    private String MENU_TYPE;

    /** nullable persistent field */
    private String MENU_ITEM;

    /** nullable persistent field */
    private String MENU_LIST;

    /** nullable persistent field */
    private BigDecimal TYPE_VALUE;

    /** nullable persistent field */
    private BigDecimal TYPE_VALUE1;

    /** nullable persistent field */
    private BigDecimal TYPE_VALUE2;

    /** nullable persistent field */
    private BigDecimal COVERCACULUNIT;

    /** nullable persistent field */
    private String COVERCACULUNITDESC;

    /** nullable persistent field */
    private String IFCHS;

    /** nullable persistent field */
    private Timestamp INSDATA_LASTUPDATE;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBPRD_INSDATA_PROD_MAIN";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBPRD_INSDATA_PROD_MAINVO(BigDecimal KEY_NO, String PRD_ID, String PRD_NAME, String COM_ID, String IS_MAIN, String IS_SALE, String IS_WL, String WL_TERM, String CONTENT_FILENAME, String CLAUSE_FILENAME, String INVEST_TYPE, String ITEM_Y, String ITEM_A, String ITEM_X, String ITEM_K, String ITEM_P, String ITEM_U, String LIST_Y, String LIST_A, String LIST_X, String LIST_K, String LIST_P, String LIST_U, String LIST_E, String LIST_O, String IS_OVERSEA, String CURR_CD, String QUANTITY_STYLE, String MENU_TYPE, String MENU_ITEM, String MENU_LIST, BigDecimal TYPE_VALUE, BigDecimal TYPE_VALUE1, BigDecimal TYPE_VALUE2, BigDecimal COVERCACULUNIT, String COVERCACULUNITDESC, String IFCHS, Timestamp INSDATA_LASTUPDATE, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.KEY_NO = KEY_NO;
        this.PRD_ID = PRD_ID;
        this.PRD_NAME = PRD_NAME;
        this.COM_ID = COM_ID;
        this.IS_MAIN = IS_MAIN;
        this.IS_SALE = IS_SALE;
        this.IS_WL = IS_WL;
        this.WL_TERM = WL_TERM;
        this.CONTENT_FILENAME = CONTENT_FILENAME;
        this.CLAUSE_FILENAME = CLAUSE_FILENAME;
        this.INVEST_TYPE = INVEST_TYPE;
        this.ITEM_Y = ITEM_Y;
        this.ITEM_A = ITEM_A;
        this.ITEM_X = ITEM_X;
        this.ITEM_K = ITEM_K;
        this.ITEM_P = ITEM_P;
        this.ITEM_U = ITEM_U;
        this.LIST_Y = LIST_Y;
        this.LIST_A = LIST_A;
        this.LIST_X = LIST_X;
        this.LIST_K = LIST_K;
        this.LIST_P = LIST_P;
        this.LIST_U = LIST_U;
        this.LIST_E = LIST_E;
        this.LIST_O = LIST_O;
        this.IS_OVERSEA = IS_OVERSEA;
        this.CURR_CD = CURR_CD;
        this.QUANTITY_STYLE = QUANTITY_STYLE;
        this.MENU_TYPE = MENU_TYPE;
        this.MENU_ITEM = MENU_ITEM;
        this.MENU_LIST = MENU_LIST;
        this.TYPE_VALUE = TYPE_VALUE;
        this.TYPE_VALUE1 = TYPE_VALUE1;
        this.TYPE_VALUE2 = TYPE_VALUE2;
        this.COVERCACULUNIT = COVERCACULUNIT;
        this.COVERCACULUNITDESC = COVERCACULUNITDESC;
        this.IFCHS = IFCHS;
        this.INSDATA_LASTUPDATE = INSDATA_LASTUPDATE;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBPRD_INSDATA_PROD_MAINVO() {
    }

    /** minimal constructor */
    public TBPRD_INSDATA_PROD_MAINVO(BigDecimal KEY_NO) {
        this.KEY_NO = KEY_NO;
    }

    public BigDecimal getKEY_NO() {
        return this.KEY_NO;
    }

    public void setKEY_NO(BigDecimal KEY_NO) {
        this.KEY_NO = KEY_NO;
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

    public String getCOM_ID() {
        return this.COM_ID;
    }

    public void setCOM_ID(String COM_ID) {
        this.COM_ID = COM_ID;
    }

    public String getIS_MAIN() {
        return this.IS_MAIN;
    }

    public void setIS_MAIN(String IS_MAIN) {
        this.IS_MAIN = IS_MAIN;
    }

    public String getIS_SALE() {
        return this.IS_SALE;
    }

    public void setIS_SALE(String IS_SALE) {
        this.IS_SALE = IS_SALE;
    }

    public String getIS_WL() {
        return this.IS_WL;
    }

    public void setIS_WL(String IS_WL) {
        this.IS_WL = IS_WL;
    }

    public String getWL_TERM() {
        return this.WL_TERM;
    }

    public void setWL_TERM(String WL_TERM) {
        this.WL_TERM = WL_TERM;
    }

    public String getCONTENT_FILENAME() {
        return this.CONTENT_FILENAME;
    }

    public void setCONTENT_FILENAME(String CONTENT_FILENAME) {
        this.CONTENT_FILENAME = CONTENT_FILENAME;
    }

    public String getCLAUSE_FILENAME() {
        return this.CLAUSE_FILENAME;
    }

    public void setCLAUSE_FILENAME(String CLAUSE_FILENAME) {
        this.CLAUSE_FILENAME = CLAUSE_FILENAME;
    }

    public String getINVEST_TYPE() {
        return this.INVEST_TYPE;
    }

    public void setINVEST_TYPE(String INVEST_TYPE) {
        this.INVEST_TYPE = INVEST_TYPE;
    }

    public String getITEM_Y() {
        return this.ITEM_Y;
    }

    public void setITEM_Y(String ITEM_Y) {
        this.ITEM_Y = ITEM_Y;
    }

    public String getITEM_A() {
        return this.ITEM_A;
    }

    public void setITEM_A(String ITEM_A) {
        this.ITEM_A = ITEM_A;
    }

    public String getITEM_X() {
        return this.ITEM_X;
    }

    public void setITEM_X(String ITEM_X) {
        this.ITEM_X = ITEM_X;
    }

    public String getITEM_K() {
        return this.ITEM_K;
    }

    public void setITEM_K(String ITEM_K) {
        this.ITEM_K = ITEM_K;
    }

    public String getITEM_P() {
        return this.ITEM_P;
    }

    public void setITEM_P(String ITEM_P) {
        this.ITEM_P = ITEM_P;
    }

    public String getITEM_U() {
        return this.ITEM_U;
    }

    public void setITEM_U(String ITEM_U) {
        this.ITEM_U = ITEM_U;
    }

    public String getLIST_Y() {
        return this.LIST_Y;
    }

    public void setLIST_Y(String LIST_Y) {
        this.LIST_Y = LIST_Y;
    }

    public String getLIST_A() {
        return this.LIST_A;
    }

    public void setLIST_A(String LIST_A) {
        this.LIST_A = LIST_A;
    }

    public String getLIST_X() {
        return this.LIST_X;
    }

    public void setLIST_X(String LIST_X) {
        this.LIST_X = LIST_X;
    }

    public String getLIST_K() {
        return this.LIST_K;
    }

    public void setLIST_K(String LIST_K) {
        this.LIST_K = LIST_K;
    }

    public String getLIST_P() {
        return this.LIST_P;
    }

    public void setLIST_P(String LIST_P) {
        this.LIST_P = LIST_P;
    }

    public String getLIST_U() {
        return this.LIST_U;
    }

    public void setLIST_U(String LIST_U) {
        this.LIST_U = LIST_U;
    }

    public String getLIST_E() {
        return this.LIST_E;
    }

    public void setLIST_E(String LIST_E) {
        this.LIST_E = LIST_E;
    }

    public String getLIST_O() {
        return this.LIST_O;
    }

    public void setLIST_O(String LIST_O) {
        this.LIST_O = LIST_O;
    }

    public String getIS_OVERSEA() {
        return this.IS_OVERSEA;
    }

    public void setIS_OVERSEA(String IS_OVERSEA) {
        this.IS_OVERSEA = IS_OVERSEA;
    }

    public String getCURR_CD() {
        return this.CURR_CD;
    }

    public void setCURR_CD(String CURR_CD) {
        this.CURR_CD = CURR_CD;
    }

    public String getQUANTITY_STYLE() {
        return this.QUANTITY_STYLE;
    }

    public void setQUANTITY_STYLE(String QUANTITY_STYLE) {
        this.QUANTITY_STYLE = QUANTITY_STYLE;
    }

    public String getMENU_TYPE() {
        return this.MENU_TYPE;
    }

    public void setMENU_TYPE(String MENU_TYPE) {
        this.MENU_TYPE = MENU_TYPE;
    }

    public String getMENU_ITEM() {
        return this.MENU_ITEM;
    }

    public void setMENU_ITEM(String MENU_ITEM) {
        this.MENU_ITEM = MENU_ITEM;
    }

    public String getMENU_LIST() {
        return this.MENU_LIST;
    }

    public void setMENU_LIST(String MENU_LIST) {
        this.MENU_LIST = MENU_LIST;
    }

    public BigDecimal getTYPE_VALUE() {
        return this.TYPE_VALUE;
    }

    public void setTYPE_VALUE(BigDecimal TYPE_VALUE) {
        this.TYPE_VALUE = TYPE_VALUE;
    }

    public BigDecimal getTYPE_VALUE1() {
        return this.TYPE_VALUE1;
    }

    public void setTYPE_VALUE1(BigDecimal TYPE_VALUE1) {
        this.TYPE_VALUE1 = TYPE_VALUE1;
    }

    public BigDecimal getTYPE_VALUE2() {
        return this.TYPE_VALUE2;
    }

    public void setTYPE_VALUE2(BigDecimal TYPE_VALUE2) {
        this.TYPE_VALUE2 = TYPE_VALUE2;
    }

    public BigDecimal getCOVERCACULUNIT() {
        return this.COVERCACULUNIT;
    }

    public void setCOVERCACULUNIT(BigDecimal COVERCACULUNIT) {
        this.COVERCACULUNIT = COVERCACULUNIT;
    }

    public String getCOVERCACULUNITDESC() {
        return this.COVERCACULUNITDESC;
    }

    public void setCOVERCACULUNITDESC(String COVERCACULUNITDESC) {
        this.COVERCACULUNITDESC = COVERCACULUNITDESC;
    }

    public String getIFCHS() {
        return this.IFCHS;
    }

    public void setIFCHS(String IFCHS) {
        this.IFCHS = IFCHS;
    }

    public Timestamp getINSDATA_LASTUPDATE() {
        return this.INSDATA_LASTUPDATE;
    }

    public void setINSDATA_LASTUPDATE(Timestamp INSDATA_LASTUPDATE) {
        this.INSDATA_LASTUPDATE = INSDATA_LASTUPDATE;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("KEY_NO", getKEY_NO())
            .toString();
    }

}
