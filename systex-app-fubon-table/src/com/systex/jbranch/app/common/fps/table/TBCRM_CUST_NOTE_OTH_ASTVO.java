package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBCRM_CUST_NOTE_OTH_ASTVO extends VOBase {

    /** identifier field */
    private String ASSETS_ID;

    /** persistent field */
    private String CUST_ID;

    /** nullable persistent field */
    private String ASSETS_NAME;

    /** nullable persistent field */
    private BigDecimal ASSETS_AMT;

    /** nullable persistent field */
    private String ASSETS_NOTE;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBCRM_CUST_NOTE_OTH_AST";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBCRM_CUST_NOTE_OTH_ASTVO(String ASSETS_ID, String CUST_ID, String ASSETS_NAME, BigDecimal ASSETS_AMT, String ASSETS_NOTE, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.ASSETS_ID = ASSETS_ID;
        this.CUST_ID = CUST_ID;
        this.ASSETS_NAME = ASSETS_NAME;
        this.ASSETS_AMT = ASSETS_AMT;
        this.ASSETS_NOTE = ASSETS_NOTE;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBCRM_CUST_NOTE_OTH_ASTVO() {
    }

    /** minimal constructor */
    public TBCRM_CUST_NOTE_OTH_ASTVO(String ASSETS_ID, String CUST_ID) {
        this.ASSETS_ID = ASSETS_ID;
        this.CUST_ID = CUST_ID;
    }

    public String getASSETS_ID() {
        return this.ASSETS_ID;
    }

    public void setASSETS_ID(String ASSETS_ID) {
        this.ASSETS_ID = ASSETS_ID;
    }

    public String getCUST_ID() {
        return this.CUST_ID;
    }

    public void setCUST_ID(String CUST_ID) {
        this.CUST_ID = CUST_ID;
    }

    public String getASSETS_NAME() {
        return this.ASSETS_NAME;
    }

    public void setASSETS_NAME(String ASSETS_NAME) {
        this.ASSETS_NAME = ASSETS_NAME;
    }

    public BigDecimal getASSETS_AMT() {
        return this.ASSETS_AMT;
    }

    public void setASSETS_AMT(BigDecimal ASSETS_AMT) {
        this.ASSETS_AMT = ASSETS_AMT;
    }

    public String getASSETS_NOTE() {
        return this.ASSETS_NOTE;
    }

    public void setASSETS_NOTE(String ASSETS_NOTE) {
        this.ASSETS_NOTE = ASSETS_NOTE;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("ASSETS_ID", getASSETS_ID())
            .toString();
    }

}
