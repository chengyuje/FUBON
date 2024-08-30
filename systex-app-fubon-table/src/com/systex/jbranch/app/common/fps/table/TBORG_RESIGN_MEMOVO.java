package com.systex.jbranch.app.common.fps.table;

import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBORG_RESIGN_MEMOVO extends VOBase {

    /** identifier field */
    private String EMP_ID;

    /** nullable persistent field */
    private Timestamp RESIGN_DATE;

    /** persistent field */
    private String RESIGN_HANDOVER;

    /** nullable persistent field */
    private String RESIGN_REASON;

    /** nullable persistent field */
    private String RESIGN_DESTINATION;

    /** nullable persistent field */
    private String DESTINATION_BANK_ID;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBORG_RESIGN_MEMO";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBORG_RESIGN_MEMOVO(String EMP_ID, Timestamp RESIGN_DATE, String RESIGN_HANDOVER, String RESIGN_REASON, String RESIGN_DESTINATION, String DESTINATION_BANK_ID, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.EMP_ID = EMP_ID;
        this.RESIGN_DATE = RESIGN_DATE;
        this.RESIGN_HANDOVER = RESIGN_HANDOVER;
        this.RESIGN_REASON = RESIGN_REASON;
        this.RESIGN_DESTINATION = RESIGN_DESTINATION;
        this.DESTINATION_BANK_ID = DESTINATION_BANK_ID;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBORG_RESIGN_MEMOVO() {
    }

    /** minimal constructor */
    public TBORG_RESIGN_MEMOVO(String EMP_ID, String RESIGN_HANDOVER) {
        this.EMP_ID = EMP_ID;
        this.RESIGN_HANDOVER = RESIGN_HANDOVER;
    }

    public String getEMP_ID() {
        return this.EMP_ID;
    }

    public void setEMP_ID(String EMP_ID) {
        this.EMP_ID = EMP_ID;
    }

    public Timestamp getRESIGN_DATE() {
        return this.RESIGN_DATE;
    }

    public void setRESIGN_DATE(Timestamp RESIGN_DATE) {
        this.RESIGN_DATE = RESIGN_DATE;
    }

    public String getRESIGN_HANDOVER() {
        return this.RESIGN_HANDOVER;
    }

    public void setRESIGN_HANDOVER(String RESIGN_HANDOVER) {
        this.RESIGN_HANDOVER = RESIGN_HANDOVER;
    }

    public String getRESIGN_REASON() {
        return this.RESIGN_REASON;
    }

    public void setRESIGN_REASON(String RESIGN_REASON) {
        this.RESIGN_REASON = RESIGN_REASON;
    }

    public String getRESIGN_DESTINATION() {
        return this.RESIGN_DESTINATION;
    }

    public void setRESIGN_DESTINATION(String RESIGN_DESTINATION) {
        this.RESIGN_DESTINATION = RESIGN_DESTINATION;
    }

    public String getDESTINATION_BANK_ID() {
        return this.DESTINATION_BANK_ID;
    }

    public void setDESTINATION_BANK_ID(String DESTINATION_BANK_ID) {
        this.DESTINATION_BANK_ID = DESTINATION_BANK_ID;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("EMP_ID", getEMP_ID())
            .toString();
    }

}
