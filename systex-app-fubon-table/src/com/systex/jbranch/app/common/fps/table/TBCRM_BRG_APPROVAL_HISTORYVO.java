package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBCRM_BRG_APPROVAL_HISTORYVO extends VOBase {

    /** identifier field */
    private BigDecimal APPROVAL_SEQ;

    /** nullable persistent field */
    private String APPLY_SEQ;

    /** nullable persistent field */
    private String APPLY_CAT;

    /** nullable persistent field */
    private String AUTH_TYPE;

    /** nullable persistent field */
    private String AUTH_LV;

    /** nullable persistent field */
    private String AUTH_EMP_ID;

    /** nullable persistent field */
    private Timestamp AUTH_DATE;

    /** nullable persistent field */
    private String COMMENTS;

    /** nullable persistent field */
    private String TERMINATE_REASON;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBCRM_BRG_APPROVAL_HISTORY";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBCRM_BRG_APPROVAL_HISTORYVO(BigDecimal APPROVAL_SEQ, String APPLY_SEQ, String APPLY_CAT, String AUTH_TYPE, String AUTH_LV, String AUTH_EMP_ID, Timestamp AUTH_DATE, String COMMENTS, String TERMINATE_REASON, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.APPROVAL_SEQ = APPROVAL_SEQ;
        this.APPLY_SEQ = APPLY_SEQ;
        this.APPLY_CAT = APPLY_CAT;
        this.AUTH_TYPE = AUTH_TYPE;
        this.AUTH_LV = AUTH_LV;
        this.AUTH_EMP_ID = AUTH_EMP_ID;
        this.AUTH_DATE = AUTH_DATE;
        this.COMMENTS = COMMENTS;
        this.TERMINATE_REASON = TERMINATE_REASON;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBCRM_BRG_APPROVAL_HISTORYVO() {
    }

    /** minimal constructor */
    public TBCRM_BRG_APPROVAL_HISTORYVO(BigDecimal APPROVAL_SEQ) {
        this.APPROVAL_SEQ = APPROVAL_SEQ;
    }

    public BigDecimal getAPPROVAL_SEQ() {
        return this.APPROVAL_SEQ;
    }

    public void setAPPROVAL_SEQ(BigDecimal APPROVAL_SEQ) {
        this.APPROVAL_SEQ = APPROVAL_SEQ;
    }

    public String getAPPLY_SEQ() {
        return this.APPLY_SEQ;
    }

    public void setAPPLY_SEQ(String APPLY_SEQ) {
        this.APPLY_SEQ = APPLY_SEQ;
    }

    public String getAPPLY_CAT() {
        return this.APPLY_CAT;
    }

    public void setAPPLY_CAT(String APPLY_CAT) {
        this.APPLY_CAT = APPLY_CAT;
    }

    public String getAUTH_TYPE() {
        return this.AUTH_TYPE;
    }

    public void setAUTH_TYPE(String AUTH_TYPE) {
        this.AUTH_TYPE = AUTH_TYPE;
    }

    public String getAUTH_LV() {
        return this.AUTH_LV;
    }

    public void setAUTH_LV(String AUTH_LV) {
        this.AUTH_LV = AUTH_LV;
    }

    public String getAUTH_EMP_ID() {
        return this.AUTH_EMP_ID;
    }

    public void setAUTH_EMP_ID(String AUTH_EMP_ID) {
        this.AUTH_EMP_ID = AUTH_EMP_ID;
    }

    public Timestamp getAUTH_DATE() {
        return this.AUTH_DATE;
    }

    public void setAUTH_DATE(Timestamp AUTH_DATE) {
        this.AUTH_DATE = AUTH_DATE;
    }

    public String getCOMMENTS() {
        return this.COMMENTS;
    }

    public void setCOMMENTS(String COMMENTS) {
        this.COMMENTS = COMMENTS;
    }

    public String getTERMINATE_REASON() {
        return this.TERMINATE_REASON;
    }

    public void setTERMINATE_REASON(String TERMINATE_REASON) {
        this.TERMINATE_REASON = TERMINATE_REASON;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("APPROVAL_SEQ", getAPPROVAL_SEQ())
            .toString();
    }

}
