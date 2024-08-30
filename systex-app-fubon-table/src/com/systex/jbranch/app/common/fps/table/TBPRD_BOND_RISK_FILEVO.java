package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Timestamp;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPRD_BOND_RISK_FILEVO extends VOBase {

    /** nullable persistent field */
    private String PRD_ID;

    /** nullable persistent field */
    private Blob RISK_FILE;
    

public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBPRD_BOND_RISK_FILE";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBPRD_BOND_RISK_FILEVO(String PRD_ID,Blob RISK_FILE, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.PRD_ID = PRD_ID;
        this.RISK_FILE = RISK_FILE;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;

    }

    /** default constructor */
    public TBPRD_BOND_RISK_FILEVO() {
    }

    /** minimal constructor */
    public TBPRD_BOND_RISK_FILEVO(String PRD_ID) {
        this.PRD_ID = PRD_ID;
    }
    
    public String getPRD_ID() {
		return this.PRD_ID;
	}

	public void setPRD_ID(String pRD_ID) {
		this.PRD_ID = pRD_ID;
	}

	public Blob getRISK_FILE() {
		return RISK_FILE;
	}

	public void setRISK_FILE(Blob rISK_FILE) {
		RISK_FILE = rISK_FILE;
	}
}
