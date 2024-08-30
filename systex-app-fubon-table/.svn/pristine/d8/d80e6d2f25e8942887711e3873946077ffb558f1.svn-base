package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Timestamp;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;


public class TBSYS_SSO_INFOVO extends VOBase {
    private String SYS_CODE;
    private String EMP_ID;
    private String ROLE_ID;
    private String VN_SYS_CODE;
    private String VN_SYS_ADDR;
    private Long KEY_NO;
    private BigDecimal RANDOM_NO;
    private Blob SECRET_KEY;
    
    public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBSYS_SSO_INFOVO";

	public String getTableuid () {
	    return TABLE_UID;
	}
	

    /** full constructor */
	
    public TBSYS_SSO_INFOVO(String SYS_CODE , String eMP_ID , String rOLE_ID,
			String vN_SYS_CODE , String vN_SYS_ADDR , Long kEY_NO,
			BigDecimal rANDOM_NO ,Blob sECRET_KEY, Long version , String creator , 
			Timestamp createtime , Timestamp lastupdate , String modifier) {
		this(SYS_CODE, eMP_ID, rOLE_ID, vN_SYS_CODE, vN_SYS_ADDR, kEY_NO, rANDOM_NO, sECRET_KEY);
		this.version = version;
		this.creator = creator;
		this.createtime = createtime;
		this.lastupdate = lastupdate;
		this.modifier = modifier;
	}
    
    public TBSYS_SSO_INFOVO(String SYS_CODE, String eMP_ID, String rOLE_ID,
			String vN_SYS_CODE, String vN_SYS_ADDR, Long kEY_NO,
			BigDecimal rANDOM_NO, Blob sECRET_KEY) {
		super();
		this.SYS_CODE = SYS_CODE;
		this.EMP_ID = eMP_ID;
		this.ROLE_ID = rOLE_ID;
		this.VN_SYS_CODE = vN_SYS_CODE;
		this.VN_SYS_ADDR = vN_SYS_ADDR;
		this.KEY_NO = kEY_NO;
		this.RANDOM_NO = rANDOM_NO;
		this.SECRET_KEY = sECRET_KEY;
	}

    /** default constructor */
    public TBSYS_SSO_INFOVO() {
    }

	/** getter & setter **/
    public String getSYS_CODE() {
		return SYS_CODE;
	}

	public void setSYS_CODE(String SYS_CODE) {
		this.SYS_CODE = SYS_CODE;
	}

	public String getEMP_ID() {
		return EMP_ID;
	}

	public void setEMP_ID(String eMP_ID) {
		this.EMP_ID = eMP_ID;
	}

	public String getROLE_ID() {
		return ROLE_ID;
	}

	public void setROLE_ID(String rOLE_ID) {
		this.ROLE_ID = rOLE_ID;
	}

	public String getVN_SYS_CODE() {
		return VN_SYS_CODE;
	}

	public void setVN_SYS_CODE(String vN_SYS_CODE) {
		this.VN_SYS_CODE = vN_SYS_CODE;
	}

	public String getVN_SYS_ADDR() {
		return VN_SYS_ADDR;
	}

	public void setVN_SYS_ADDR(String vN_SYS_ADDR) {
		this.VN_SYS_ADDR = vN_SYS_ADDR;
	}

	public Long getKEY_NO() {
		return KEY_NO;
	}

	public void setKEY_NO(Long kEY_NO) {
		this.KEY_NO = kEY_NO;
	}

	public BigDecimal getRANDOM_NO() {
		return RANDOM_NO;
	}

	public void setRANDOM_NO(BigDecimal rANDOM_NO) {
		this.RANDOM_NO = rANDOM_NO;
	}

	public Blob getSECRET_KEY() {
		return SECRET_KEY;
	}


	public void setSECRET_KEY(Blob sECRET_KEY) {
		SECRET_KEY = sECRET_KEY;
	}

	public String toString() {
        return new ToStringBuilder(this)
            .append("KEY_NO" , getKEY_NO())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) 
        	return true;        
        if ( !(other instanceof TBSYS_SSO_INFOVO) ) 
        	return false;
        
        TBSYS_SSO_INFOVO castOther = (TBSYS_SSO_INFOVO) other;
        
        return new EqualsBuilder()
            .append(this.getKEY_NO() , castOther.getKEY_NO())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getKEY_NO())
            .toHashCode();
    }

}
