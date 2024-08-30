package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPMS_MRTG_PS_WEEKPK  implements Serializable  {

    /** identifier field */
    private String DATA_DATE;

    /** identifier field */
    private String EMP_ID;

    /** full constructor */
    public TBPMS_MRTG_PS_WEEKPK(String DATA_DATE, String EMP_ID) {
        this.DATA_DATE = DATA_DATE;
        this.EMP_ID = EMP_ID;
    }

    /** default constructor */
    public TBPMS_MRTG_PS_WEEKPK() {
    }

    public String getDATA_DATE() {
        return this.DATA_DATE;
    }

    public void setDATA_DATE(String DATA_DATE) {
        this.DATA_DATE = DATA_DATE;
    }

    public String getEMP_ID() {
        return this.EMP_ID;
    }

    public void setEMP_ID(String EMP_ID) {
        this.EMP_ID = EMP_ID;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("DATA_DATE", getDATA_DATE())
            .append("EMP_ID", getEMP_ID())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBPMS_MRTG_PS_WEEKPK) ) return false;
        TBPMS_MRTG_PS_WEEKPK castOther = (TBPMS_MRTG_PS_WEEKPK) other;
        return new EqualsBuilder()
            .append(this.getDATA_DATE(), castOther.getDATA_DATE())
            .append(this.getEMP_ID(), castOther.getEMP_ID())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getDATA_DATE())
            .append(getEMP_ID())
            .toHashCode();
    }

}
