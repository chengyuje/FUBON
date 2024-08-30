package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBFPS_OTHER_PARA_MANUALVO extends VOBase {

    /** identifier field */
    private BigDecimal SEQ;

    /** nullable persistent field */
    private String PARAM_NO;

    /** nullable persistent field */
    private String FPS_TYPE;

    /** nullable persistent field */
    private String DESC_TYPE;

    /** nullable persistent field */
    private BigDecimal RANK;

    /** nullable persistent field */
    private String CONTENT;

    /** nullable persistent field */
    private String FONT;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBFPS_OTHER_PARA_MANUAL";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBFPS_OTHER_PARA_MANUALVO(BigDecimal SEQ, String PARAM_NO, String FPS_TYPE, String DESC_TYPE, BigDecimal RANK, String CONTENT, String FONT, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.SEQ = SEQ;
        this.PARAM_NO = PARAM_NO;
        this.FPS_TYPE = FPS_TYPE;
        this.DESC_TYPE = DESC_TYPE;
        this.RANK = RANK;
        this.CONTENT = CONTENT;
        this.FONT = FONT;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBFPS_OTHER_PARA_MANUALVO() {
    }

    /** minimal constructor */
    public TBFPS_OTHER_PARA_MANUALVO(BigDecimal SEQ) {
        this.SEQ = SEQ;
    }

    public BigDecimal getSEQ() {
        return this.SEQ;
    }

    public void setSEQ(BigDecimal SEQ) {
        this.SEQ = SEQ;
    }

    public String getPARAM_NO() {
        return this.PARAM_NO;
    }

    public void setPARAM_NO(String PARAM_NO) {
        this.PARAM_NO = PARAM_NO;
    }

    public String getFPS_TYPE() {
        return this.FPS_TYPE;
    }

    public void setFPS_TYPE(String FPS_TYPE) {
        this.FPS_TYPE = FPS_TYPE;
    }

    public String getDESC_TYPE() {
        return this.DESC_TYPE;
    }

    public void setDESC_TYPE(String DESC_TYPE) {
        this.DESC_TYPE = DESC_TYPE;
    }

    public BigDecimal getRANK() {
        return this.RANK;
    }

    public void setRANK(BigDecimal RANK) {
        this.RANK = RANK;
    }

    public String getCONTENT() {
        return this.CONTENT;
    }

    public void setCONTENT(String CONTENT) {
        this.CONTENT = CONTENT;
    }

    public String getFONT() {
        return this.FONT;
    }

    public void setFONT(String FONT) {
        this.FONT = FONT;
    }

    public void checkDefaultValue() {
         if (FONT == null) {
             this.FONT="1 ";
         }
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("SEQ", getSEQ())
            .toString();
    }

}
