package com.systex.jbranch.app.common.fps.table;

import java.sql.Blob;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBMGM_GIFT_IMGVO extends VOBase {

    /** identifier field */
    private String GIFT_SEQ;

    /** nullable persistent field */
    private String GIFT_PHOTO_NAME;

    /** nullable persistent field */
    private Blob GIFT_PHOTO;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBMGM_GIFT_IMG";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBMGM_GIFT_IMGVO(String GIFT_SEQ, String GIFT_PHOTO_NAME, Blob GIFT_PHOTO, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.GIFT_SEQ = GIFT_SEQ;
        this.GIFT_PHOTO_NAME = GIFT_PHOTO_NAME;
        this.GIFT_PHOTO = GIFT_PHOTO;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBMGM_GIFT_IMGVO() {
    }

    /** minimal constructor */
    public TBMGM_GIFT_IMGVO(String GIFT_SEQ) {
        this.GIFT_SEQ = GIFT_SEQ;
    }

    public String getGIFT_SEQ() {
        return this.GIFT_SEQ;
    }

    public void setGIFT_SEQ(String GIFT_SEQ) {
        this.GIFT_SEQ = GIFT_SEQ;
    }

    public String getGIFT_PHOTO_NAME() {
        return this.GIFT_PHOTO_NAME;
    }

    public void setGIFT_PHOTO_NAME(String GIFT_PHOTO_NAME) {
        this.GIFT_PHOTO_NAME = GIFT_PHOTO_NAME;
    }

    public Blob getGIFT_PHOTO() {
        return this.GIFT_PHOTO;
    }

    public void setGIFT_PHOTO(Blob GIFT_PHOTO) {
        this.GIFT_PHOTO = GIFT_PHOTO;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("GIFT_SEQ", getGIFT_SEQ())
            .toString();
    }

}
