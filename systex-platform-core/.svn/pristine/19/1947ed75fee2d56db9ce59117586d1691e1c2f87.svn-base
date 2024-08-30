package com.systex.jbranch.platform.common.platformdao.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBSYSOVERPRINTFORMULAVO extends VOBase {

    /** identifier field */
    private BigDecimal FID;

    /** persistent field */
    private String DESCRIPTION;

    /** persistent field */
    private String BEAN_ID;

    /** nullable persistent field */
    private String ARGS_DESC;


public static final String TABLE_UID = "com.systex.jbranch.platform.common.platformdao.table.TBSYSOVERPRINTFORMULA";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBSYSOVERPRINTFORMULAVO(String DESCRIPTION, String BEAN_ID, String creator, Timestamp createtime, String modifier, Timestamp lastupdate, String ARGS_DESC, Long version) {
        this.DESCRIPTION = DESCRIPTION;
        this.BEAN_ID = BEAN_ID;
        this.creator = creator;
        this.createtime = createtime;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.ARGS_DESC = ARGS_DESC;
        this.version = version;
    }

    /** default constructor */
    public TBSYSOVERPRINTFORMULAVO() {
    }

    /** minimal constructor */
    public TBSYSOVERPRINTFORMULAVO(String DESCRIPTION, String BEAN_ID) {
        this.DESCRIPTION = DESCRIPTION;
        this.BEAN_ID = BEAN_ID;
    }

    public BigDecimal getFID() {
        return this.FID;
    }

    public void setFID(BigDecimal FID) {
        this.FID = FID;
    }

    public String getDESCRIPTION() {
        return this.DESCRIPTION;
    }

    public void setDESCRIPTION(String DESCRIPTION) {
        this.DESCRIPTION = DESCRIPTION;
    }

    public String getBEAN_ID() {
        return this.BEAN_ID;
    }

    public void setBEAN_ID(String BEAN_ID) {
        this.BEAN_ID = BEAN_ID;
    }

    public String getARGS_DESC() {
        return this.ARGS_DESC;
    }

    public void setARGS_DESC(String ARGS_DESC) {
        this.ARGS_DESC = ARGS_DESC;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("FID", getFID())
            .toString();
    }

}
