package com.systex.jbranch.app.common.fps.table;

import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBCRM_DKYC_QSTN_SETVO extends VOBase {

    /** identifier field */
    private String QSTN_ID;

    /** nullable persistent field */
    private String DISPLAY_LAYER;

    /** nullable persistent field */
    private String DISPLAY_ORDER;

    /** nullable persistent field */
    private String QSTN_CONTENT;

    /** nullable persistent field */
    private String QSTN_TYPE;

    /** nullable persistent field */
    private Timestamp VALID_BGN_DATE;

    /** nullable persistent field */
    private Timestamp VALID_END_DATE;

    /** nullable persistent field */
    private String QSTN_FORMAT;

    /** nullable persistent field */
    private String OTH_OPT_YN;

    /** nullable persistent field */
    private String EXT_MEMO_YN;

    /** nullable persistent field */
    private String VIP_DEGREE;

    /** nullable persistent field */
    private String AUM_DEGREE;

    /** nullable persistent field */
    private String WORD_SURGERY;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBCRM_DKYC_QSTN_SET";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBCRM_DKYC_QSTN_SETVO(String QSTN_ID, String DISPLAY_LAYER, String DISPLAY_ORDER, String QSTN_CONTENT, String QSTN_TYPE, Timestamp VALID_BGN_DATE, Timestamp VALID_END_DATE, String QSTN_FORMAT, String OTH_OPT_YN, String EXT_MEMO_YN, String VIP_DEGREE, String AUM_DEGREE, String creator, Timestamp createtime, String modifier, Timestamp lastupdate, String WORD_SURGERY, Long version) {
        this.QSTN_ID = QSTN_ID;
        this.DISPLAY_LAYER = DISPLAY_LAYER;
        this.DISPLAY_ORDER = DISPLAY_ORDER;
        this.QSTN_CONTENT = QSTN_CONTENT;
        this.QSTN_TYPE = QSTN_TYPE;
        this.VALID_BGN_DATE = VALID_BGN_DATE;
        this.VALID_END_DATE = VALID_END_DATE;
        this.QSTN_FORMAT = QSTN_FORMAT;
        this.OTH_OPT_YN = OTH_OPT_YN;
        this.EXT_MEMO_YN = EXT_MEMO_YN;
        this.VIP_DEGREE = VIP_DEGREE;
        this.AUM_DEGREE = AUM_DEGREE;
        this.creator = creator;
        this.createtime = createtime;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.WORD_SURGERY = WORD_SURGERY;
        this.version = version;
    }

    /** default constructor */
    public TBCRM_DKYC_QSTN_SETVO() {
    }

    /** minimal constructor */
    public TBCRM_DKYC_QSTN_SETVO(String QSTN_ID) {
        this.QSTN_ID = QSTN_ID;
    }

    public String getQSTN_ID() {
        return this.QSTN_ID;
    }

    public void setQSTN_ID(String QSTN_ID) {
        this.QSTN_ID = QSTN_ID;
    }

    public String getDISPLAY_LAYER() {
        return this.DISPLAY_LAYER;
    }

    public void setDISPLAY_LAYER(String DISPLAY_LAYER) {
        this.DISPLAY_LAYER = DISPLAY_LAYER;
    }

    public String getDISPLAY_ORDER() {
        return this.DISPLAY_ORDER;
    }

    public void setDISPLAY_ORDER(String DISPLAY_ORDER) {
        this.DISPLAY_ORDER = DISPLAY_ORDER;
    }

    public String getQSTN_CONTENT() {
        return this.QSTN_CONTENT;
    }

    public void setQSTN_CONTENT(String QSTN_CONTENT) {
        this.QSTN_CONTENT = QSTN_CONTENT;
    }

    public String getQSTN_TYPE() {
        return this.QSTN_TYPE;
    }

    public void setQSTN_TYPE(String QSTN_TYPE) {
        this.QSTN_TYPE = QSTN_TYPE;
    }

    public Timestamp getVALID_BGN_DATE() {
        return this.VALID_BGN_DATE;
    }

    public void setVALID_BGN_DATE(Timestamp VALID_BGN_DATE) {
        this.VALID_BGN_DATE = VALID_BGN_DATE;
    }

    public Timestamp getVALID_END_DATE() {
        return this.VALID_END_DATE;
    }

    public void setVALID_END_DATE(Timestamp VALID_END_DATE) {
        this.VALID_END_DATE = VALID_END_DATE;
    }

    public String getQSTN_FORMAT() {
        return this.QSTN_FORMAT;
    }

    public void setQSTN_FORMAT(String QSTN_FORMAT) {
        this.QSTN_FORMAT = QSTN_FORMAT;
    }

    public String getOTH_OPT_YN() {
        return this.OTH_OPT_YN;
    }

    public void setOTH_OPT_YN(String OTH_OPT_YN) {
        this.OTH_OPT_YN = OTH_OPT_YN;
    }

    public String getEXT_MEMO_YN() {
        return this.EXT_MEMO_YN;
    }

    public void setEXT_MEMO_YN(String EXT_MEMO_YN) {
        this.EXT_MEMO_YN = EXT_MEMO_YN;
    }

    public String getVIP_DEGREE() {
        return this.VIP_DEGREE;
    }

    public void setVIP_DEGREE(String VIP_DEGREE) {
        this.VIP_DEGREE = VIP_DEGREE;
    }

    public String getAUM_DEGREE() {
        return this.AUM_DEGREE;
    }

    public void setAUM_DEGREE(String AUM_DEGREE) {
        this.AUM_DEGREE = AUM_DEGREE;
    }

    public String getWORD_SURGERY() {
        return this.WORD_SURGERY;
    }

    public void setWORD_SURGERY(String WORD_SURGERY) {
        this.WORD_SURGERY = WORD_SURGERY;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("QSTN_ID", getQSTN_ID())
            .toString();
    }

}
