package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBINS_PARA_DISEASEVO extends VOBase {

    /** identifier field */
    private BigDecimal D_KEYNO;

    /** persistent field */
    private BigDecimal PARA_NO;

    /** persistent field */
    private String DIS_NAME;

    /** nullable persistent field */
    private String TYPE_CANCER;

    /** nullable persistent field */
    private String TYPE_MAJOR;

    /** nullable persistent field */
    private String TYPE_LT;

    /** nullable persistent field */
    private String DIS_DESC;

    /** nullable persistent field */
    private BigDecimal SEQ_NUM;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBINS_PARA_DISEASE";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBINS_PARA_DISEASEVO(BigDecimal D_KEYNO, BigDecimal PARA_NO, String DIS_NAME, String TYPE_CANCER, String TYPE_MAJOR, String TYPE_LT, String DIS_DESC, BigDecimal SEQ_NUM, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.D_KEYNO = D_KEYNO;
        this.PARA_NO = PARA_NO;
        this.DIS_NAME = DIS_NAME;
        this.TYPE_CANCER = TYPE_CANCER;
        this.TYPE_MAJOR = TYPE_MAJOR;
        this.TYPE_LT = TYPE_LT;
        this.DIS_DESC = DIS_DESC;
        this.SEQ_NUM = SEQ_NUM;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBINS_PARA_DISEASEVO() {
    }

    /** minimal constructor */
    public TBINS_PARA_DISEASEVO(BigDecimal D_KEYNO, BigDecimal PARA_NO, String DIS_NAME) {
        this.D_KEYNO = D_KEYNO;
        this.PARA_NO = PARA_NO;
        this.DIS_NAME = DIS_NAME;
    }

    public BigDecimal getD_KEYNO() {
        return this.D_KEYNO;
    }

    public void setD_KEYNO(BigDecimal D_KEYNO) {
        this.D_KEYNO = D_KEYNO;
    }

    public BigDecimal getPARA_NO() {
        return this.PARA_NO;
    }

    public void setPARA_NO(BigDecimal PARA_NO) {
        this.PARA_NO = PARA_NO;
    }

    public String getDIS_NAME() {
        return this.DIS_NAME;
    }

    public void setDIS_NAME(String DIS_NAME) {
        this.DIS_NAME = DIS_NAME;
    }

    public String getTYPE_CANCER() {
        return this.TYPE_CANCER;
    }

    public void setTYPE_CANCER(String TYPE_CANCER) {
        this.TYPE_CANCER = TYPE_CANCER;
    }

    public String getTYPE_MAJOR() {
        return this.TYPE_MAJOR;
    }

    public void setTYPE_MAJOR(String TYPE_MAJOR) {
        this.TYPE_MAJOR = TYPE_MAJOR;
    }

    public String getTYPE_LT() {
        return this.TYPE_LT;
    }

    public void setTYPE_LT(String TYPE_LT) {
        this.TYPE_LT = TYPE_LT;
    }

    public String getDIS_DESC() {
        return this.DIS_DESC;
    }

    public void setDIS_DESC(String DIS_DESC) {
        this.DIS_DESC = DIS_DESC;
    }

    public BigDecimal getSEQ_NUM() {
        return this.SEQ_NUM;
    }

    public void setSEQ_NUM(BigDecimal SEQ_NUM) {
        this.SEQ_NUM = SEQ_NUM;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("D_KEYNO", getD_KEYNO())
            .toString();
    }

}
