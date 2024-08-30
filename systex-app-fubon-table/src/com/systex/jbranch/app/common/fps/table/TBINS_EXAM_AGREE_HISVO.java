package com.systex.jbranch.app.common.fps.table;

import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBINS_EXAM_AGREE_HISVO extends VOBase {

    /** identifier field */
    private String AGREE_KEYNO;

    /** nullable persistent field */
    private String AO_CODE;

    /** nullable persistent field */
    private String CUST_ID;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBINS_EXAM_AGREE_HIS";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBINS_EXAM_AGREE_HISVO(String AGREE_KEYNO, String AO_CODE, String CUST_ID, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.AGREE_KEYNO = AGREE_KEYNO;
        this.AO_CODE = AO_CODE;
        this.CUST_ID = CUST_ID;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBINS_EXAM_AGREE_HISVO() {
    }

    /** minimal constructor */
    public TBINS_EXAM_AGREE_HISVO(String AGREE_KEYNO) {
        this.AGREE_KEYNO = AGREE_KEYNO;
    }

    public String getAGREE_KEYNO() {
        return this.AGREE_KEYNO;
    }

    public void setAGREE_KEYNO(String AGREE_KEYNO) {
        this.AGREE_KEYNO = AGREE_KEYNO;
    }

    public String getAO_CODE() {
        return this.AO_CODE;
    }

    public void setAO_CODE(String AO_CODE) {
        this.AO_CODE = AO_CODE;
    }

    public String getCUST_ID() {
        return this.CUST_ID;
    }

    public void setCUST_ID(String CUST_ID) {
        this.CUST_ID = CUST_ID;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("AGREE_KEYNO", getAGREE_KEYNO())
            .toString();
    }

}
