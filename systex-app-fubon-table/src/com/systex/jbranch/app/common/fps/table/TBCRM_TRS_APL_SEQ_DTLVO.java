package com.systex.jbranch.app.common.fps.table;

import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBCRM_TRS_APL_SEQ_DTLVO extends VOBase {

    /** identifier field */
    private String APL_SEQ;

    /** persistent field */
    private String CUST_ID;

    /** nullable persistent field */
    private Timestamp APL_DATE;

    /** nullable persistent field */
    private String NEW_AO_CODE;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBCRM_TRS_APL_SEQ_DTL";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBCRM_TRS_APL_SEQ_DTLVO(String APL_SEQ, String CUST_ID, Timestamp APL_DATE, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, String NEW_AO_CODE, Long version) {
        this.APL_SEQ = APL_SEQ;
        this.CUST_ID = CUST_ID;
        this.APL_DATE = APL_DATE;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.NEW_AO_CODE = NEW_AO_CODE;
        this.version = version;
    }

    /** default constructor */
    public TBCRM_TRS_APL_SEQ_DTLVO() {
    }

    /** minimal constructor */
    public TBCRM_TRS_APL_SEQ_DTLVO(String APL_SEQ, String CUST_ID) {
        this.APL_SEQ = APL_SEQ;
        this.CUST_ID = CUST_ID;
    }

    public String getAPL_SEQ() {
        return this.APL_SEQ;
    }

    public void setAPL_SEQ(String APL_SEQ) {
        this.APL_SEQ = APL_SEQ;
    }

    public String getCUST_ID() {
        return this.CUST_ID;
    }

    public void setCUST_ID(String CUST_ID) {
        this.CUST_ID = CUST_ID;
    }

    public Timestamp getAPL_DATE() {
        return this.APL_DATE;
    }

    public void setAPL_DATE(Timestamp APL_DATE) {
        this.APL_DATE = APL_DATE;
    }

    public String getNEW_AO_CODE() {
        return this.NEW_AO_CODE;
    }

    public void setNEW_AO_CODE(String NEW_AO_CODE) {
        this.NEW_AO_CODE = NEW_AO_CODE;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("APL_SEQ", getAPL_SEQ())
            .toString();
    }

}
