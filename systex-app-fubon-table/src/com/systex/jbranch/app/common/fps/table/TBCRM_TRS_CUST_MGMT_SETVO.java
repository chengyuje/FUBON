package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBCRM_TRS_CUST_MGMT_SETVO extends VOBase {

    /** identifier field */
    private String AO_JOB_RANK;

    /** nullable persistent field */
    private String LIMIT_BY_AUM_YN;

    /** nullable persistent field */
    private BigDecimal AUM_LIMIT_UP;

    /** nullable persistent field */
    private BigDecimal TTL_CUST_NO_LIMIT_UP;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBCRM_TRS_CUST_MGMT_SET";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBCRM_TRS_CUST_MGMT_SETVO(String AO_JOB_RANK, String LIMIT_BY_AUM_YN, BigDecimal AUM_LIMIT_UP, BigDecimal TTL_CUST_NO_LIMIT_UP) {
        this.AO_JOB_RANK = AO_JOB_RANK;
        this.LIMIT_BY_AUM_YN = LIMIT_BY_AUM_YN;
        this.AUM_LIMIT_UP = AUM_LIMIT_UP;
        this.TTL_CUST_NO_LIMIT_UP = TTL_CUST_NO_LIMIT_UP;
    }

    /** default constructor */
    public TBCRM_TRS_CUST_MGMT_SETVO() {
    }

    /** minimal constructor */
    public TBCRM_TRS_CUST_MGMT_SETVO(String AO_JOB_RANK) {
        this.AO_JOB_RANK = AO_JOB_RANK;
    }

    public String getAO_JOB_RANK() {
        return this.AO_JOB_RANK;
    }

    public void setAO_JOB_RANK(String AO_JOB_RANK) {
        this.AO_JOB_RANK = AO_JOB_RANK;
    }

    public String getLIMIT_BY_AUM_YN() {
        return this.LIMIT_BY_AUM_YN;
    }

    public void setLIMIT_BY_AUM_YN(String LIMIT_BY_AUM_YN) {
        this.LIMIT_BY_AUM_YN = LIMIT_BY_AUM_YN;
    }

    public BigDecimal getAUM_LIMIT_UP() {
        return this.AUM_LIMIT_UP;
    }

    public void setAUM_LIMIT_UP(BigDecimal AUM_LIMIT_UP) {
        this.AUM_LIMIT_UP = AUM_LIMIT_UP;
    }

    public BigDecimal getTTL_CUST_NO_LIMIT_UP() {
        return this.TTL_CUST_NO_LIMIT_UP;
    }

    public void setTTL_CUST_NO_LIMIT_UP(BigDecimal TTL_CUST_NO_LIMIT_UP) {
        this.TTL_CUST_NO_LIMIT_UP = TTL_CUST_NO_LIMIT_UP;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("AO_JOB_RANK", getAO_JOB_RANK())
            .toString();
    }

}
