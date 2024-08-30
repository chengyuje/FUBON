package com.systex.jbranch.platform.common.platformdao.table;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBSYSSECULOGVO extends VOBase {

    /** identifier field */
    private BigDecimal LOGINDEX;

    /** nullable persistent field */
    private String BRANCHID;

    /** nullable persistent field */
    private String WORKSTATIONID;

    /** nullable persistent field */
    private String TELLER;

    /** nullable persistent field */
    private String ACTION;

    /** nullable persistent field */
    private String TYPE;

    /** nullable persistent field */
    private String DATA1;

    /** nullable persistent field */
    private String DATA2;
    
    /** nullable persistent field */
    private String DATA3;


public static final String TABLE_UID = "com.systex.jbranch.platform.common.platformdao.table.TBSYSSECULOG";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBSYSSECULOGVO(String BRANCHID, String WORKSTATIONID, String TELLER, Timestamp createtime, String creator, Timestamp lastupdate, String modifier, String ACTION, String TYPE, String DATA1, String DATA2, Long version) {
        this.BRANCHID = BRANCHID;
        this.WORKSTATIONID = WORKSTATIONID;
        this.TELLER = TELLER;
        this.createtime = createtime;
        this.creator = creator;
        this.lastupdate = lastupdate;
        this.modifier = modifier;
        this.ACTION = ACTION;
        this.TYPE = TYPE;
        this.DATA1 = DATA1;
        this.DATA2 = DATA2;
        this.version = version;
    }

    /** default constructor */
    public TBSYSSECULOGVO() {
    }

    public BigDecimal getLOGINDEX() {
        return this.LOGINDEX;
    }

    public void setLOGINDEX(BigDecimal LOGINDEX) {
        this.LOGINDEX = LOGINDEX;
    }

    public String getBRANCHID() {
        return this.BRANCHID;
    }

    public void setBRANCHID(String BRANCHID) {
        this.BRANCHID = BRANCHID;
    }

    public String getWORKSTATIONID() {
        return this.WORKSTATIONID;
    }

    public void setWORKSTATIONID(String WORKSTATIONID) {
        this.WORKSTATIONID = WORKSTATIONID;
    }

    public String getTELLER() {
        return this.TELLER;
    }

    public void setTELLER(String TELLER) {
        this.TELLER = TELLER;
    }

    public String getACTION() {
        return this.ACTION;
    }

    public void setACTION(String ACTION) {
        this.ACTION = ACTION;
    }

    public String getTYPE() {
        return this.TYPE;
    }

    public void setTYPE(String TYPE) {
        this.TYPE = TYPE;
    }

    public String getDATA1() {
        return this.DATA1;
    }

    public void setDATA1(String DATA1) {
        this.DATA1 = DATA1;
    }

    public String getDATA2() {
        return this.DATA2;
    }

    public void setDATA2(String DATA2) {
        this.DATA2 = DATA2;
    }
    
    public String getDATA3() {
        return this.DATA3;
    }

    public void setDATA3(String DATA3) {
        this.DATA3 = DATA3;
    }
    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("LOGINDEX", getLOGINDEX())
            .toString();
    }

}
