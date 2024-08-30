package com.systex.jbranch.platform.common.platformdao.table;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class TbsysmultilangPK implements Serializable {

    /** identifier field */
    private String type;

    /** identifier field */
    private String typeSeq;

    /** identifier field */
    private String groupname;

    /** identifier field */
    private String groupSeq;

    /** identifier field */
    private String attribute;

    /** identifier field */
    private String seq;

    /** identifier field */
    private String locale;

    /** full constructor */
    public TbsysmultilangPK(String type, String typeSeq, String groupname, String groupSeq, String attribute, String seq, String locale) {
        this.type = type;
        this.typeSeq = typeSeq;
        this.groupname = groupname;
        this.groupSeq = groupSeq;
        this.attribute = attribute;
        this.seq = seq;
        this.locale = locale;
    }

    /** default constructor */
    public TbsysmultilangPK() {
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeSeq() {
        return this.typeSeq;
    }

    public void setTypeSeq(String typeSeq) {
        this.typeSeq = typeSeq;
    }

    public String getGroupname() {
        return this.groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    public String getGroupSeq() {
        return this.groupSeq;
    }

    public void setGroupSeq(String groupSeq) {
        this.groupSeq = groupSeq;
    }

    public String getAttribute() {
        return this.attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getSeq() {
        return this.seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }

    public String getLocale() {
        return this.locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("type", getType())
            .append("typeSeq", getTypeSeq())
            .append("groupname", getGroupname())
            .append("groupSeq", getGroupSeq())
            .append("attribute", getAttribute())
            .append("seq", getSeq())
            .append("locale", getLocale())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TbsysmultilangPK) ) return false;
        TbsysmultilangPK castOther = (TbsysmultilangPK) other;
        return new EqualsBuilder()
            .append(this.getType(), castOther.getType())
            .append(this.getTypeSeq(), castOther.getTypeSeq())
            .append(this.getGroupname(), castOther.getGroupname())
            .append(this.getGroupSeq(), castOther.getGroupSeq())
            .append(this.getAttribute(), castOther.getAttribute())
            .append(this.getSeq(), castOther.getSeq())
            .append(this.getLocale(), castOther.getLocale())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getType())
            .append(getTypeSeq())
            .append(getGroupname())
            .append(getGroupSeq())
            .append(getAttribute())
            .append(getSeq())
            .append(getLocale())
            .toHashCode();
    }

}
