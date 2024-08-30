package com.systex.jbranch.app.common.fps.table;

import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBSFA_FUNC_MENUVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBSFA_FUNC_MENUPK comp_id;

    /** nullable persistent field */
    private Integer SEQ_NUM;

    /** nullable persistent field */
    private String MENU_TYPE;

    /** nullable persistent field */
    private String MENU_NAME;

    /** nullable persistent field */
    private String PREV_MENU;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBSFA_FUNC_MENU";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBSFA_FUNC_MENUVO(com.systex.jbranch.app.common.fps.table.TBSFA_FUNC_MENUPK comp_id, Integer SEQ_NUM, String MENU_TYPE, String MENU_NAME, String PREV_MENU, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.comp_id = comp_id;
        this.SEQ_NUM = SEQ_NUM;
        this.MENU_TYPE = MENU_TYPE;
        this.MENU_NAME = MENU_NAME;
        this.PREV_MENU = PREV_MENU;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBSFA_FUNC_MENUVO() {
    }

    /** minimal constructor */
    public TBSFA_FUNC_MENUVO(com.systex.jbranch.app.common.fps.table.TBSFA_FUNC_MENUPK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.app.common.fps.table.TBSFA_FUNC_MENUPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBSFA_FUNC_MENUPK comp_id) {
        this.comp_id = comp_id;
    }

    public Integer getSEQ_NUM() {
        return this.SEQ_NUM;
    }

    public void setSEQ_NUM(Integer SEQ_NUM) {
        this.SEQ_NUM = SEQ_NUM;
    }

    public String getMENU_TYPE() {
        return this.MENU_TYPE;
    }

    public void setMENU_TYPE(String MENU_TYPE) {
        this.MENU_TYPE = MENU_TYPE;
    }

    public String getMENU_NAME() {
        return this.MENU_NAME;
    }

    public void setMENU_NAME(String MENU_NAME) {
        this.MENU_NAME = MENU_NAME;
    }

    public String getPREV_MENU() {
        return this.PREV_MENU;
    }

    public void setPREV_MENU(String PREV_MENU) {
        this.PREV_MENU = PREV_MENU;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getcomp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBSFA_FUNC_MENUVO) ) return false;
        TBSFA_FUNC_MENUVO castOther = (TBSFA_FUNC_MENUVO) other;
        return new EqualsBuilder()
            .append(this.getcomp_id(), castOther.getcomp_id())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getcomp_id())
            .toHashCode();
    }

}
