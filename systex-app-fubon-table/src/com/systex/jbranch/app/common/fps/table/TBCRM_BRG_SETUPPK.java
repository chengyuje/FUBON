package com.systex.jbranch.app.common.fps.table;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBCRM_BRG_SETUPPK  implements Serializable  {

    /** identifier field */
    private String CON_DEGREE;

    /** identifier field */
    private String LEVEL_NO;

    /** identifier field */
    private String PROD_TYPE;
    
    /** identifier field */
    private String SETUP_TYPE;

    /** full constructor */
    public TBCRM_BRG_SETUPPK(String CON_DEGREE, String LEVEL_NO, String PROD_TYPE, String SETUP_TYPE) {
        this.CON_DEGREE = CON_DEGREE;
        this.LEVEL_NO = LEVEL_NO;
        this.PROD_TYPE = PROD_TYPE;
        this.SETUP_TYPE = SETUP_TYPE;
    }

    /** default constructor */
    public TBCRM_BRG_SETUPPK() {
    }

    public String getCON_DEGREE() {
        return this.CON_DEGREE;
    }

    public void setCON_DEGREE(String CON_DEGREE) {
        this.CON_DEGREE = CON_DEGREE;
    }

    public String getLEVEL_NO() {
        return this.LEVEL_NO;
    }

    public void setLEVEL_NO(String LEVEL_NO) {
        this.LEVEL_NO = LEVEL_NO;
    }

    public String getPROD_TYPE() {
        return this.PROD_TYPE;
    }

    public void setPROD_TYPE(String PROD_TYPE) {
        this.PROD_TYPE = PROD_TYPE;
    }

    public String getSETUP_TYPE() {
		return SETUP_TYPE;
	}

	public void setSETUP_TYPE(String sETUP_TYPE) {
		SETUP_TYPE = sETUP_TYPE;
	}

	public String toString() {
        return new ToStringBuilder(this)
            .append("CON_DEGREE", getCON_DEGREE())
            .append("LEVEL_NO", getLEVEL_NO())
            .append("PROD_TYPE", getPROD_TYPE())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBCRM_BRG_SETUPPK) ) return false;
        TBCRM_BRG_SETUPPK castOther = (TBCRM_BRG_SETUPPK) other;
        return new EqualsBuilder()
            .append(this.getCON_DEGREE(), castOther.getCON_DEGREE())
            .append(this.getLEVEL_NO(), castOther.getLEVEL_NO())
            .append(this.getPROD_TYPE(), castOther.getPROD_TYPE())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getCON_DEGREE())
            .append(getLEVEL_NO())
            .append(getPROD_TYPE())
            .toHashCode();
    }

}
