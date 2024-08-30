package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Timestamp;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBCRM_TRS_CUST_EXCLUDEVO extends VOBase {

	/** identifier field */
    private BigDecimal SEQ_KEY_NO;

    /** nullable persistent field */
    private String CUST_ID;
    
    /** nullable persistent field */
    private Blob ATT_FILE;
    
    /** nullable persistent field */
    private String DEL_YN;
    

public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBCRM_TRS_CUST_EXCLUDE";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBCRM_TRS_CUST_EXCLUDEVO(String CUST_ID, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.CUST_ID = CUST_ID;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBCRM_TRS_CUST_EXCLUDEVO() {
    }

    /** minimal constructor */
    public TBCRM_TRS_CUST_EXCLUDEVO(BigDecimal SEQ_KEY_NO) {
        this.SEQ_KEY_NO = SEQ_KEY_NO;
    }

    public BigDecimal getSEQ_KEY_NO() {
		return SEQ_KEY_NO;
	}

	public void setSEQ_KEY_NO(BigDecimal sEQ_KEY_NO) {
		SEQ_KEY_NO = sEQ_KEY_NO;
	}

    public String getCUST_ID() {
		return CUST_ID;
	}

	public void setCUST_ID(String cUST_ID) {
		CUST_ID = cUST_ID;
	}

	public Blob getATT_FILE() {
		return ATT_FILE;
	}

	public void setATT_FILE(Blob aTT_FILE) {
		ATT_FILE = aTT_FILE;
	}

	public String getDEL_YN() {
		return DEL_YN;
	}

	public void setDEL_YN(String dEL_YN) {
		DEL_YN = dEL_YN;
	}

	public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("SEQ_KEY_NO", getSEQ_KEY_NO())
            .toString();
    }

}
