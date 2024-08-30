package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBSYS_MKT_LINKVO extends VOBase {

    /** identifier field */
    private String DOC_ID;

    /** nullable persistent field */
    private BigDecimal SEQ;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBSYS_MKT_LINK";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBSYS_MKT_LINKVO(String DOC_ID, BigDecimal SEQ, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.DOC_ID = DOC_ID;
        this.SEQ = SEQ;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBSYS_MKT_LINKVO() {
    }

    /** minimal constructor */
    public TBSYS_MKT_LINKVO(String DOC_ID) {
        this.DOC_ID = DOC_ID;
    }

    public String getDOC_ID() {
        return this.DOC_ID;
    }

    public void setDOC_ID(String DOC_ID) {
        this.DOC_ID = DOC_ID;
    }

    public BigDecimal getSEQ() {
        return this.SEQ;
    }

    public void setSEQ(BigDecimal SEQ) {
        this.SEQ = SEQ;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("DOC_ID", getDOC_ID())
            .toString();
    }

}
