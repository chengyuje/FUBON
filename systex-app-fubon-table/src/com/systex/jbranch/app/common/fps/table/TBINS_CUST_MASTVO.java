package com.systex.jbranch.app.common.fps.table;

import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBINS_CUST_MASTVO extends VOBase {

    /** identifier field */
    private String CUST_ID;

    /** nullable persistent field */
    private String CUST_NAME;

    /** nullable persistent field */
    private String GENDER;

    /** nullable persistent field */
    private Timestamp BIRTH_DATE;

    /** nullable persistent field */
    private String MARRIAGE_STAT;

    /** nullable persistent field */
    private String FB_CUST;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBINS_CUST_MAST";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBINS_CUST_MASTVO(String CUST_ID, String CUST_NAME, String GENDER, Timestamp BIRTH_DATE, String MARRIAGE_STAT, String FB_CUST, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.CUST_ID = CUST_ID;
        this.CUST_NAME = CUST_NAME;
        this.GENDER = GENDER;
        this.BIRTH_DATE = BIRTH_DATE;
        this.MARRIAGE_STAT = MARRIAGE_STAT;
        this.FB_CUST = FB_CUST;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBINS_CUST_MASTVO() {
    }

    /** minimal constructor */
    public TBINS_CUST_MASTVO(String CUST_ID) {
        this.CUST_ID = CUST_ID;
    }

    public String getCUST_ID() {
        return this.CUST_ID;
    }

    public void setCUST_ID(String CUST_ID) {
        this.CUST_ID = CUST_ID;
    }

    public String getCUST_NAME() {
        return this.CUST_NAME;
    }

    public void setCUST_NAME(String CUST_NAME) {
        this.CUST_NAME = CUST_NAME;
    }

    public String getGENDER() {
        return this.GENDER;
    }

    public void setGENDER(String GENDER) {
        this.GENDER = GENDER;
    }

    public Timestamp getBIRTH_DATE() {
        return this.BIRTH_DATE;
    }

    public void setBIRTH_DATE(Timestamp BIRTH_DATE) {
        this.BIRTH_DATE = BIRTH_DATE;
    }

    public String getMARRIAGE_STAT() {
        return this.MARRIAGE_STAT;
    }

    public void setMARRIAGE_STAT(String MARRIAGE_STAT) {
        this.MARRIAGE_STAT = MARRIAGE_STAT;
    }

    public String getFB_CUST() {
        return this.FB_CUST;
    }

    public void setFB_CUST(String FB_CUST) {
        this.FB_CUST = FB_CUST;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("CUST_ID", getCUST_ID())
            .toString();
    }

}
