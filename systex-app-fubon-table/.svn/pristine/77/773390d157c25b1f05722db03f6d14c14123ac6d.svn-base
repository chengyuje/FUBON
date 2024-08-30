package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBSQM_CSM_OPTIONSPK  implements Serializable  {

    /** identifier field */
    private String ITEM_MAPPING_ID;

    /** identifier field */
    private String QUESTION_MAPPING_ID;

    /** full constructor */
    public TBSQM_CSM_OPTIONSPK(String ITEM_MAPPING_ID, String QUESTION_MAPPING_ID) {
        this.ITEM_MAPPING_ID = ITEM_MAPPING_ID;
        this.QUESTION_MAPPING_ID = QUESTION_MAPPING_ID;
    }

    /** default constructor */
    public TBSQM_CSM_OPTIONSPK() {
    }

    public String getITEM_MAPPING_ID() {
        return this.ITEM_MAPPING_ID;
    }

    public void setITEM_MAPPING_ID(String ITEM_MAPPING_ID) {
        this.ITEM_MAPPING_ID = ITEM_MAPPING_ID;
    }

    public String getQUESTION_MAPPING_ID() {
        return this.QUESTION_MAPPING_ID;
    }

    public void setQUESTION_MAPPING_ID(String QUESTION_MAPPING_ID) {
        this.QUESTION_MAPPING_ID = QUESTION_MAPPING_ID;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("ITEM_MAPPING_ID", getITEM_MAPPING_ID())
            .append("QUESTION_MAPPING_ID", getQUESTION_MAPPING_ID())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBSQM_CSM_OPTIONSPK) ) return false;
        TBSQM_CSM_OPTIONSPK castOther = (TBSQM_CSM_OPTIONSPK) other;
        return new EqualsBuilder()
            .append(this.getITEM_MAPPING_ID(), castOther.getITEM_MAPPING_ID())
            .append(this.getQUESTION_MAPPING_ID(), castOther.getQUESTION_MAPPING_ID())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getITEM_MAPPING_ID())
            .append(getQUESTION_MAPPING_ID())
            .toHashCode();
    }

}
