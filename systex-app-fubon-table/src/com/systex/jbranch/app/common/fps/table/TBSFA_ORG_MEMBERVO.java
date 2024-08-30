package com.systex.jbranch.app.common.fps.table;

import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBSFA_ORG_MEMBERVO extends VOBase {

    /** identifier field */
    private String EMP_ID;

    /** nullable persistent field */
    private String EMP_NAME;

    /** persistent field */
    private String EMP_PID;

    /** nullable persistent field */
    private String E_MAIL;

    /** persistent field */
    private String TERRITORY_ID;

    /** nullable persistent field */
    private String SA_JOB_TITLE_ID;

    /** nullable persistent field */
    private String ON_BOARD_DATE;

    /** nullable persistent field */
    private Byte MARK_4_DEL;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBSFA_ORG_MEMBER";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBSFA_ORG_MEMBERVO(String EMP_ID, String EMP_NAME, String EMP_PID, String E_MAIL, String TERRITORY_ID, String SA_JOB_TITLE_ID, String ON_BOARD_DATE, Byte MARK_4_DEL, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.EMP_ID = EMP_ID;
        this.EMP_NAME = EMP_NAME;
        this.EMP_PID = EMP_PID;
        this.E_MAIL = E_MAIL;
        this.TERRITORY_ID = TERRITORY_ID;
        this.SA_JOB_TITLE_ID = SA_JOB_TITLE_ID;
        this.ON_BOARD_DATE = ON_BOARD_DATE;
        this.MARK_4_DEL = MARK_4_DEL;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBSFA_ORG_MEMBERVO() {
    }

    /** minimal constructor */
    public TBSFA_ORG_MEMBERVO(String EMP_ID, String EMP_PID, String TERRITORY_ID) {
        this.EMP_ID = EMP_ID;
        this.EMP_PID = EMP_PID;
        this.TERRITORY_ID = TERRITORY_ID;
    }

    public String getEMP_ID() {
        return this.EMP_ID;
    }

    public void setEMP_ID(String EMP_ID) {
        this.EMP_ID = EMP_ID;
    }

    public String getEMP_NAME() {
        return this.EMP_NAME;
    }

    public void setEMP_NAME(String EMP_NAME) {
        this.EMP_NAME = EMP_NAME;
    }

    public String getEMP_PID() {
        return this.EMP_PID;
    }

    public void setEMP_PID(String EMP_PID) {
        this.EMP_PID = EMP_PID;
    }

    public String getE_MAIL() {
        return this.E_MAIL;
    }

    public void setE_MAIL(String E_MAIL) {
        this.E_MAIL = E_MAIL;
    }

    public String getTERRITORY_ID() {
        return this.TERRITORY_ID;
    }

    public void setTERRITORY_ID(String TERRITORY_ID) {
        this.TERRITORY_ID = TERRITORY_ID;
    }

    public String getSA_JOB_TITLE_ID() {
        return this.SA_JOB_TITLE_ID;
    }

    public void setSA_JOB_TITLE_ID(String SA_JOB_TITLE_ID) {
        this.SA_JOB_TITLE_ID = SA_JOB_TITLE_ID;
    }

    public String getON_BOARD_DATE() {
        return this.ON_BOARD_DATE;
    }

    public void setON_BOARD_DATE(String ON_BOARD_DATE) {
        this.ON_BOARD_DATE = ON_BOARD_DATE;
    }

    public Byte getMARK_4_DEL() {
        return this.MARK_4_DEL;
    }

    public void setMARK_4_DEL(Byte MARK_4_DEL) {
        this.MARK_4_DEL = MARK_4_DEL;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("EMP_ID", getEMP_ID())
            .toString();
    }

}
