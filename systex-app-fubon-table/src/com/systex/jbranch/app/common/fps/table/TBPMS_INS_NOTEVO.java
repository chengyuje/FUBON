package com.systex.jbranch.app.common.fps.table;

import java.sql.Timestamp;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;

/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPMS_INS_NOTEVO extends VOBase {

    /** identifier field */
    private String PKVALUE;

    /** nullable persistent field */
    private String REMARK;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBPMS_INS_NOTE";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBPMS_INS_NOTEVO(String PKVALUE, String REMARK, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.PKVALUE = PKVALUE;
        this.REMARK = REMARK;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBPMS_INS_NOTEVO() {
    }

    /** minimal constructor */
    public TBPMS_INS_NOTEVO(String PKVALUE) {
        this.PKVALUE = PKVALUE;
    }

    public String getPKVALUE() {
        return this.PKVALUE;
    }

    public void setPKVALUE(String PKVALUE) {
        this.PKVALUE = PKVALUE;
    }

    public String getREMARK() {
        return this.REMARK;
    }

    public void setREMARK(String REMARK) {
        this.REMARK = REMARK;
    }

    public void checkDefaultValue() {
         if (PKVALUE == null) {
             this.PKVALUE="1 ";
         }
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("PKVALUE", getPKVALUE())
            .toString();
    }

}
