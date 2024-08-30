package com.systex.jbranch.platform.common.platformdao.table;

import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;

/** @author Hibernate CodeGenerator */
public class TbsysmultilangVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.platform.common.platformdao.table.TbsysmultilangPK comp_id;

    /** nullable persistent field */
    private String content;

    /** nullable persistent field */
    private String memo;

    public static final String TABLE_UID = "com.systex.jbranch.platform.common.platformdao.table.Tbsysmultilang";
    public String getTableuid(){
    	return TABLE_UID;
    }
   
    /** full constructor */
    public TbsysmultilangVO(com.systex.jbranch.platform.common.platformdao.table.TbsysmultilangPK comp_id, String content, String memo, String creator, Timestamp createtime, String modifier, Timestamp lastupdate, Long version) {
        this.comp_id = comp_id;
        this.content = content;
        this.memo = memo;
        this.creator = creator;
        this.createtime = createtime;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TbsysmultilangVO() {
    }

    /** minimal constructor */
    public TbsysmultilangVO(com.systex.jbranch.platform.common.platformdao.table.TbsysmultilangPK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.platform.common.platformdao.table.TbsysmultilangPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(com.systex.jbranch.platform.common.platformdao.table.TbsysmultilangPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMemo() {
        return this.memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

  

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TbsysmultilangVO) ) return false;
        TbsysmultilangVO castOther = (TbsysmultilangVO) other;
        return new EqualsBuilder()
            .append(this.getComp_id(), castOther.getComp_id())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getComp_id())
            .toHashCode();
    }

}
