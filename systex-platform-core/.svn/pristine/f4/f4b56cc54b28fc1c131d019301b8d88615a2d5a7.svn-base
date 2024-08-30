package com.systex.jbranch.platform.common.platformdao.table;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBSYSWSONLINESTATUSPK  implements Serializable  {

    /** identifier field */
    private String TELLERID;

    /** identifier field */
    private String WSID;

    /** identifier field */
    private String APSVRNAME;

    /** full constructor */
    public TBSYSWSONLINESTATUSPK(String TELLERID, String WSID, String APSVRNAME) {
        this.TELLERID = TELLERID;
        this.WSID = WSID;
        this.APSVRNAME = APSVRNAME;
    }

    /** default constructor */
    public TBSYSWSONLINESTATUSPK() {
    }

    public String getTELLERID() {
        return this.TELLERID;
    }

    public void setTELLERID(String TELLERID) {
        this.TELLERID = TELLERID;
    }

    public String getWSID() {
        return this.WSID;
    }

    public void setWSID(String WSID) {
        this.WSID = WSID;
    }

    public String getAPSVRNAME() {
        return this.APSVRNAME;
    }

    public void setAPSVRNAME(String APSVRNAME) {
        this.APSVRNAME = APSVRNAME;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("TELLERID", getTELLERID())
            .append("WSID", getWSID())
            .append("APSVRNAME", getAPSVRNAME())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBSYSWSONLINESTATUSPK) ) return false;
        TBSYSWSONLINESTATUSPK castOther = (TBSYSWSONLINESTATUSPK) other;
        return new EqualsBuilder()
            .append(this.getTELLERID(), castOther.getTELLERID())
            .append(this.getWSID(), castOther.getWSID())
            .append(this.getAPSVRNAME(), castOther.getAPSVRNAME())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getTELLERID())
            .append(getWSID())
            .append(getAPSVRNAME())
            .toHashCode();
    }

}
