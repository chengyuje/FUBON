package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBINS_PARA_LTCAREVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBINS_PARA_LTCAREPK comp_id;

    /** persistent field */
    private String CARE_WAY;

    /** persistent field */
    private String CARE_STYLE;

    /** persistent field */
    private BigDecimal MONTH_AMT;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBINS_PARA_LTCARE";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBINS_PARA_LTCAREVO(com.systex.jbranch.app.common.fps.table.TBINS_PARA_LTCAREPK comp_id, String CARE_WAY, String CARE_STYLE, BigDecimal MONTH_AMT, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.comp_id = comp_id;
        this.CARE_WAY = CARE_WAY;
        this.CARE_STYLE = CARE_STYLE;
        this.MONTH_AMT = MONTH_AMT;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBINS_PARA_LTCAREVO() {
    }

    /** minimal constructor */
    public TBINS_PARA_LTCAREVO(com.systex.jbranch.app.common.fps.table.TBINS_PARA_LTCAREPK comp_id, String CARE_WAY, String CARE_STYLE, BigDecimal MONTH_AMT) {
        this.comp_id = comp_id;
        this.CARE_WAY = CARE_WAY;
        this.CARE_STYLE = CARE_STYLE;
        this.MONTH_AMT = MONTH_AMT;
    }

    public com.systex.jbranch.app.common.fps.table.TBINS_PARA_LTCAREPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBINS_PARA_LTCAREPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getCARE_WAY() {
        return this.CARE_WAY;
    }

    public void setCARE_WAY(String CARE_WAY) {
        this.CARE_WAY = CARE_WAY;
    }

    public String getCARE_STYLE() {
        return this.CARE_STYLE;
    }

    public void setCARE_STYLE(String CARE_STYLE) {
        this.CARE_STYLE = CARE_STYLE;
    }

    public BigDecimal getMONTH_AMT() {
        return this.MONTH_AMT;
    }

    public void setMONTH_AMT(BigDecimal MONTH_AMT) {
        this.MONTH_AMT = MONTH_AMT;
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
        if ( !(other instanceof TBINS_PARA_LTCAREVO) ) return false;
        TBINS_PARA_LTCAREVO castOther = (TBINS_PARA_LTCAREVO) other;
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
