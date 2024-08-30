package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBFPS_SPP_PLAN_HELP_RVO extends VOBase {

    /** identifier field */
    private String PLAN_ID;

    /** nullable persistent field */
    private BigDecimal RETIREMENT_AGE;

    /** nullable persistent field */
    private BigDecimal RETIRE_FEE;

    /** nullable persistent field */
    private BigDecimal PREPARE_FEE;

    /** nullable persistent field */
    private BigDecimal SOCIAL_INS_FEE_1;

    /** nullable persistent field */
    private BigDecimal SOCIAL_INS_FEE_2;

    /** nullable persistent field */
    private BigDecimal SOCIAL_WELFARE_FEE_1;

    /** nullable persistent field */
    private BigDecimal SOCIAL_WELFARE_FEE_2;

    /** nullable persistent field */
    private BigDecimal COMM_INS_FEE_1;

    /** nullable persistent field */
    private BigDecimal COMM_INS_FEE_2;

    /** nullable persistent field */
    private BigDecimal OTHER_FEE_1;

    /** nullable persistent field */
    private BigDecimal OTHER_FEE_2;

    /** nullable persistent field */
    private BigDecimal HERITAGE;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBFPS_SPP_PLAN_HELP_R";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBFPS_SPP_PLAN_HELP_RVO(String PLAN_ID, BigDecimal RETIREMENT_AGE, BigDecimal RETIRE_FEE, BigDecimal PREPARE_FEE, BigDecimal SOCIAL_INS_FEE_1, BigDecimal SOCIAL_INS_FEE_2, BigDecimal SOCIAL_WELFARE_FEE_1, BigDecimal SOCIAL_WELFARE_FEE_2, BigDecimal COMM_INS_FEE_1, BigDecimal COMM_INS_FEE_2, BigDecimal OTHER_FEE_1, BigDecimal OTHER_FEE_2, BigDecimal HERITAGE, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.PLAN_ID = PLAN_ID;
        this.RETIREMENT_AGE = RETIREMENT_AGE;
        this.RETIRE_FEE = RETIRE_FEE;
        this.PREPARE_FEE = PREPARE_FEE;
        this.SOCIAL_INS_FEE_1 = SOCIAL_INS_FEE_1;
        this.SOCIAL_INS_FEE_2 = SOCIAL_INS_FEE_2;
        this.SOCIAL_WELFARE_FEE_1 = SOCIAL_WELFARE_FEE_1;
        this.SOCIAL_WELFARE_FEE_2 = SOCIAL_WELFARE_FEE_2;
        this.COMM_INS_FEE_1 = COMM_INS_FEE_1;
        this.COMM_INS_FEE_2 = COMM_INS_FEE_2;
        this.OTHER_FEE_1 = OTHER_FEE_1;
        this.OTHER_FEE_2 = OTHER_FEE_2;
        this.HERITAGE = HERITAGE;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBFPS_SPP_PLAN_HELP_RVO() {
    }

    /** minimal constructor */
    public TBFPS_SPP_PLAN_HELP_RVO(String PLAN_ID) {
        this.PLAN_ID = PLAN_ID;
    }

    public String getPLAN_ID() {
        return this.PLAN_ID;
    }

    public void setPLAN_ID(String PLAN_ID) {
        this.PLAN_ID = PLAN_ID;
    }

    public BigDecimal getRETIREMENT_AGE() {
        return this.RETIREMENT_AGE;
    }

    public void setRETIREMENT_AGE(BigDecimal RETIREMENT_AGE) {
        this.RETIREMENT_AGE = RETIREMENT_AGE;
    }

    public BigDecimal getRETIRE_FEE() {
        return this.RETIRE_FEE;
    }

    public void setRETIRE_FEE(BigDecimal RETIRE_FEE) {
        this.RETIRE_FEE = RETIRE_FEE;
    }

    public BigDecimal getPREPARE_FEE() {
        return this.PREPARE_FEE;
    }

    public void setPREPARE_FEE(BigDecimal PREPARE_FEE) {
        this.PREPARE_FEE = PREPARE_FEE;
    }

    public BigDecimal getSOCIAL_INS_FEE_1() {
        return this.SOCIAL_INS_FEE_1;
    }

    public void setSOCIAL_INS_FEE_1(BigDecimal SOCIAL_INS_FEE_1) {
        this.SOCIAL_INS_FEE_1 = SOCIAL_INS_FEE_1;
    }

    public BigDecimal getSOCIAL_INS_FEE_2() {
        return this.SOCIAL_INS_FEE_2;
    }

    public void setSOCIAL_INS_FEE_2(BigDecimal SOCIAL_INS_FEE_2) {
        this.SOCIAL_INS_FEE_2 = SOCIAL_INS_FEE_2;
    }

    public BigDecimal getSOCIAL_WELFARE_FEE_1() {
        return this.SOCIAL_WELFARE_FEE_1;
    }

    public void setSOCIAL_WELFARE_FEE_1(BigDecimal SOCIAL_WELFARE_FEE_1) {
        this.SOCIAL_WELFARE_FEE_1 = SOCIAL_WELFARE_FEE_1;
    }

    public BigDecimal getSOCIAL_WELFARE_FEE_2() {
        return this.SOCIAL_WELFARE_FEE_2;
    }

    public void setSOCIAL_WELFARE_FEE_2(BigDecimal SOCIAL_WELFARE_FEE_2) {
        this.SOCIAL_WELFARE_FEE_2 = SOCIAL_WELFARE_FEE_2;
    }

    public BigDecimal getCOMM_INS_FEE_1() {
        return this.COMM_INS_FEE_1;
    }

    public void setCOMM_INS_FEE_1(BigDecimal COMM_INS_FEE_1) {
        this.COMM_INS_FEE_1 = COMM_INS_FEE_1;
    }

    public BigDecimal getCOMM_INS_FEE_2() {
        return this.COMM_INS_FEE_2;
    }

    public void setCOMM_INS_FEE_2(BigDecimal COMM_INS_FEE_2) {
        this.COMM_INS_FEE_2 = COMM_INS_FEE_2;
    }

    public BigDecimal getOTHER_FEE_1() {
        return this.OTHER_FEE_1;
    }

    public void setOTHER_FEE_1(BigDecimal OTHER_FEE_1) {
        this.OTHER_FEE_1 = OTHER_FEE_1;
    }

    public BigDecimal getOTHER_FEE_2() {
        return this.OTHER_FEE_2;
    }

    public void setOTHER_FEE_2(BigDecimal OTHER_FEE_2) {
        this.OTHER_FEE_2 = OTHER_FEE_2;
    }

    public BigDecimal getHERITAGE() {
        return this.HERITAGE;
    }

    public void setHERITAGE(BigDecimal HERITAGE) {
        this.HERITAGE = HERITAGE;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("PLAN_ID", getPLAN_ID())
            .toString();
    }

}
