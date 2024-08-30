package com.systex.jbranch.app.common.fps.table;

import java.sql.Blob;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBMGM_MGM_EVIDENCEVO extends VOBase {

    /** identifier field */
    private String SEQ;

    /** nullable persistent field */
    private String EVIDENCE_NAME;

    /** nullable persistent field */
    private Blob EVIDENCE;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBMGM_MGM_EVIDENCE";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBMGM_MGM_EVIDENCEVO(String SEQ, String EVIDENCE_NAME, Blob EVIDENCE, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.SEQ = SEQ;
        this.EVIDENCE_NAME = EVIDENCE_NAME;
        this.EVIDENCE = EVIDENCE;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBMGM_MGM_EVIDENCEVO() {
    }

    /** minimal constructor */
    public TBMGM_MGM_EVIDENCEVO(String SEQ) {
        this.SEQ = SEQ;
    }

    public String getSEQ() {
        return this.SEQ;
    }

    public void setSEQ(String SEQ) {
        this.SEQ = SEQ;
    }

    public String getEVIDENCE_NAME() {
        return this.EVIDENCE_NAME;
    }

    public void setEVIDENCE_NAME(String EVIDENCE_NAME) {
        this.EVIDENCE_NAME = EVIDENCE_NAME;
    }

    public Blob getEVIDENCE() {
        return this.EVIDENCE;
    }

    public void setEVIDENCE(Blob EVIDENCE) {
        this.EVIDENCE = EVIDENCE;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("SEQ", getSEQ())
            .toString();
    }

}
