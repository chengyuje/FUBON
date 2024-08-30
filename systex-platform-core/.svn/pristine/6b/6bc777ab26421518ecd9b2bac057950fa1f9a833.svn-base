package com.systex.jbranch.platform.common.platformdao.table;

import java.sql.Timestamp;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBSYSFTPVO extends VOBase {

    /** identifier field */
    private String FTPSETTINGID;

    /** nullable persistent field */
    private String SRCDIRECTORY;

    /** nullable persistent field */
    private String SRCFILENAME;

    /** nullable persistent field */
    private String CHECKFILE;

    /** nullable persistent field */
    private String DESDIRECTORY;

    /** nullable persistent field */
    private String DESFILENAME;

    /** nullable persistent field */
    private Integer REPEAT;

    /** nullable persistent field */
    private Integer REPEATINTERVAL;

    /** nullable persistent field */
    private String HOSTID;


public static final String TABLE_UID = "com.systex.jbranch.platform.common.platformdao.table.TBSYSFTP";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBSYSFTPVO(String FTPSETTINGID, String SRCDIRECTORY, String SRCFILENAME, String CHECKFILE, String DESDIRECTORY, String DESFILENAME, Integer REPEAT, Integer REPEATINTERVAL, String HOSTID, String creator, Timestamp createtime, String modifier, Timestamp lastupdate, Long version) {
        this.FTPSETTINGID = FTPSETTINGID;
        this.SRCDIRECTORY = SRCDIRECTORY;
        this.SRCFILENAME = SRCFILENAME;
        this.CHECKFILE = CHECKFILE;
        this.DESDIRECTORY = DESDIRECTORY;
        this.DESFILENAME = DESFILENAME;
        this.REPEAT = REPEAT;
        this.REPEATINTERVAL = REPEATINTERVAL;
        this.HOSTID = HOSTID;
        this.creator = creator;
        this.createtime = createtime;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBSYSFTPVO() {
    }

    /** minimal constructor */
    public TBSYSFTPVO(String FTPSETTINGID) {
        this.FTPSETTINGID = FTPSETTINGID;
    }

    public String getFTPSETTINGID() {
        return this.FTPSETTINGID;
    }

    public void setFTPSETTINGID(String FTPSETTINGID) {
        this.FTPSETTINGID = FTPSETTINGID;
    }

    public String getSRCDIRECTORY() {
        return this.SRCDIRECTORY;
    }

    public void setSRCDIRECTORY(String SRCDIRECTORY) {
        this.SRCDIRECTORY = SRCDIRECTORY;
    }

    public String getSRCFILENAME() {
        return this.SRCFILENAME;
    }

    public void setSRCFILENAME(String SRCFILENAME) {
        this.SRCFILENAME = SRCFILENAME;
    }

    public String getCHECKFILE() {
        return this.CHECKFILE;
    }

    public void setCHECKFILE(String CHECKFILE) {
        this.CHECKFILE = CHECKFILE;
    }

    public String getDESDIRECTORY() {
        return this.DESDIRECTORY;
    }

    public void setDESDIRECTORY(String DESDIRECTORY) {
        this.DESDIRECTORY = DESDIRECTORY;
    }

    public String getDESFILENAME() {
        return this.DESFILENAME;
    }

    public void setDESFILENAME(String DESFILENAME) {
        this.DESFILENAME = DESFILENAME;
    }

	public Integer getREPEAT() {
		return REPEAT;
	}

	public void setREPEAT(Integer rEPEAT) {
		REPEAT = rEPEAT;
	}

	public Integer getREPEATINTERVAL() {
		return REPEATINTERVAL;
	}

	public void setREPEATINTERVAL(Integer rEPEATINTERVAL) {
		REPEATINTERVAL = rEPEATINTERVAL;
	}

	public String getHOSTID() {
        return this.HOSTID;
    }

    public void setHOSTID(String HOSTID) {
        this.HOSTID = HOSTID;
    }

    public void checkDefaultValue() {
         if (REPEAT == null) {
             this.REPEAT= 0;
         }
         if (REPEATINTERVAL == null) {
             this.REPEATINTERVAL= 0;
         }
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("FTPSETTINGID", getFTPSETTINGID())
            .toString();
    }

}
