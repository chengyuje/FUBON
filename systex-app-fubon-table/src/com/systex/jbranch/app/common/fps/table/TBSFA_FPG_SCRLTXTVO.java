package com.systex.jbranch.app.common.fps.table;

import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBSFA_FPG_SCRLTXTVO extends VOBase {

    /** identifier field */
    private Long SCRL_ID;

    /** nullable persistent field */
    private String SCRL_TITLE;

    /** nullable persistent field */
    private String SCRL_DESC;

    /** nullable persistent field */
    private Timestamp SCRL_START_DT;

    /** nullable persistent field */
    private Timestamp SCRL_END_DT;

    /** nullable persistent field */
    private String MSG_TYPE;

    /** nullable persistent field */
    private String MSG_NEW;

    /** nullable persistent field */
    private String SCRL_STOP;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBSFA_FPG_SCRLTXT";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBSFA_FPG_SCRLTXTVO(String SCRL_TITLE, String SCRL_DESC, Timestamp SCRL_START_DT, Timestamp SCRL_END_DT, String MSG_TYPE, String MSG_NEW, String SCRL_STOP, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.SCRL_TITLE = SCRL_TITLE;
        this.SCRL_DESC = SCRL_DESC;
        this.SCRL_START_DT = SCRL_START_DT;
        this.SCRL_END_DT = SCRL_END_DT;
        this.MSG_TYPE = MSG_TYPE;
        this.MSG_NEW = MSG_NEW;
        this.SCRL_STOP = SCRL_STOP;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBSFA_FPG_SCRLTXTVO() {
    }

    public Long getSCRL_ID() {
        return this.SCRL_ID;
    }

    public void setSCRL_ID(Long SCRL_ID) {
        this.SCRL_ID = SCRL_ID;
    }

    public String getSCRL_TITLE() {
        return this.SCRL_TITLE;
    }

    public void setSCRL_TITLE(String SCRL_TITLE) {
        this.SCRL_TITLE = SCRL_TITLE;
    }

    public String getSCRL_DESC() {
        return this.SCRL_DESC;
    }

    public void setSCRL_DESC(String SCRL_DESC) {
        this.SCRL_DESC = SCRL_DESC;
    }

    public Timestamp getSCRL_START_DT() {
        return this.SCRL_START_DT;
    }

    public void setSCRL_START_DT(Timestamp SCRL_START_DT) {
        this.SCRL_START_DT = SCRL_START_DT;
    }

    public Timestamp getSCRL_END_DT() {
        return this.SCRL_END_DT;
    }

    public void setSCRL_END_DT(Timestamp SCRL_END_DT) {
        this.SCRL_END_DT = SCRL_END_DT;
    }

    public String getMSG_TYPE() {
        return this.MSG_TYPE;
    }

    public void setMSG_TYPE(String MSG_TYPE) {
        this.MSG_TYPE = MSG_TYPE;
    }

    public String getMSG_NEW() {
        return this.MSG_NEW;
    }

    public void setMSG_NEW(String MSG_NEW) {
        this.MSG_NEW = MSG_NEW;
    }

    public String getSCRL_STOP() {
        return this.SCRL_STOP;
    }

    public void setSCRL_STOP(String SCRL_STOP) {
        this.SCRL_STOP = SCRL_STOP;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("SCRL_ID", getSCRL_ID())
            .toString();
    }

}
