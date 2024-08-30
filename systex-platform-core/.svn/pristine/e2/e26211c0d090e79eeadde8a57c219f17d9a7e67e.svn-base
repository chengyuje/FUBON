package com.systex.jbranch.platform.common.platformdao.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TbsysfileclearVO extends VOBase {

    /** identifier field */
    private Long SEQ;

    /** persistent field */
    private String TYPE;

    /** persistent field */
    private String PATH;

    /** persistent field */
    private String FILENAME;

    /** nullable persistent field */
    private BigDecimal EXPIRE;

    /** nullable persistent field */
    private String DESC;


public static final String TABLE_UID = "com.systex.jbranch.platform.common.platformdao.table.Tbsysfileclear";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TbsysfileclearVO(String TYPE, String PATH, String FILENAME, BigDecimal EXPIRE, String DESC, String creator, Timestamp createtime, Timestamp lastupdate, String modifier, Long version) {
        this.TYPE = TYPE;
        this.PATH = PATH;
        this.FILENAME = FILENAME;
        this.EXPIRE = EXPIRE;
        this.DESC = DESC;
        this.creator = creator;
        this.createtime = createtime;
        this.lastupdate = lastupdate;
        this.modifier = modifier;
        this.version = version;
    }

    /** default constructor */
    public TbsysfileclearVO() {
    }

    /** minimal constructor */
    public TbsysfileclearVO(String TYPE, String PATH, String FILENAME) {
        this.TYPE = TYPE;
        this.PATH = PATH;
        this.FILENAME = FILENAME;
    }

    public Long getSEQ() {
        return this.SEQ;
    }

    public void setSEQ(Long SEQ) {
        this.SEQ = SEQ;
    }

    public String getTYPE() {
        return this.TYPE;
    }

    public void setTYPE(String TYPE) {
        this.TYPE = TYPE;
    }

    public String getPATH() {
        return this.PATH;
    }

    public void setPATH(String PATH) {
        this.PATH = PATH;
    }

    public String getFILENAME() {
        return this.FILENAME;
    }

    public void setFILENAME(String FILENAME) {
        this.FILENAME = FILENAME;
    }

    public BigDecimal getEXPIRE() {
        return this.EXPIRE;
    }

    public void setEXPIRE(BigDecimal EXPIRE) {
        this.EXPIRE = EXPIRE;
    }

    public String getDESC() {
        return this.DESC;
    }

    public void setDESC(String DESC) {
        this.DESC = DESC;
    }

    public void checkDefaultValue() {
         if (EXPIRE == null) {
             this.EXPIRE= new BigDecimal(0); 
         }
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("SEQ", getSEQ())
            .toString();
    }

}
