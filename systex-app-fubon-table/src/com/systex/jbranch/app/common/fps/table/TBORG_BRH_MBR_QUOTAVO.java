package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBORG_BRH_MBR_QUOTAVO extends VOBase {

    /** identifier field */
    private String DEPT_ID;

    /** nullable persistent field */
    private BigDecimal FC1_CNT;

    /** nullable persistent field */
    private BigDecimal FC2_CNT;

    /** nullable persistent field */
    private BigDecimal FC3_CNT;

    /** nullable persistent field */
    private BigDecimal FC4_CNT;

    /** nullable persistent field */
    private BigDecimal FC5_CNT;

    /** nullable persistent field */
    private BigDecimal FCH_CNT;

    /** nullable persistent field */
    private BigDecimal OP_CNT;

    /** nullable persistent field */
    private BigDecimal PS_CNT;

    /** nullable persistent field */
    private String PS_CNT_REMARK;

    /** nullable persistent field */
    private BigDecimal OPH_CNT;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBORG_BRH_MBR_QUOTA";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBORG_BRH_MBR_QUOTAVO(String DEPT_ID, BigDecimal FC1_CNT, BigDecimal FC2_CNT, BigDecimal FC3_CNT, BigDecimal FC4_CNT, BigDecimal FC5_CNT, BigDecimal FCH_CNT, BigDecimal OP_CNT, BigDecimal PS_CNT, String PS_CNT_REMARK, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, BigDecimal OPH_CNT, Long version) {
        this.DEPT_ID = DEPT_ID;
        this.FC1_CNT = FC1_CNT;
        this.FC2_CNT = FC2_CNT;
        this.FC3_CNT = FC3_CNT;
        this.FC4_CNT = FC4_CNT;
        this.FC5_CNT = FC5_CNT;
        this.FCH_CNT = FCH_CNT;
        this.OP_CNT = OP_CNT;
        this.PS_CNT = PS_CNT;
        this.PS_CNT_REMARK = PS_CNT_REMARK;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.OPH_CNT = OPH_CNT;
        this.version = version;
    }

    /** default constructor */
    public TBORG_BRH_MBR_QUOTAVO() {
    }

    /** minimal constructor */
    public TBORG_BRH_MBR_QUOTAVO(String DEPT_ID) {
        this.DEPT_ID = DEPT_ID;
    }

    public String getDEPT_ID() {
        return this.DEPT_ID;
    }

    public void setDEPT_ID(String DEPT_ID) {
        this.DEPT_ID = DEPT_ID;
    }

    public BigDecimal getFC1_CNT() {
        return this.FC1_CNT;
    }

    public void setFC1_CNT(BigDecimal FC1_CNT) {
        this.FC1_CNT = FC1_CNT;
    }

    public BigDecimal getFC2_CNT() {
        return this.FC2_CNT;
    }

    public void setFC2_CNT(BigDecimal FC2_CNT) {
        this.FC2_CNT = FC2_CNT;
    }

    public BigDecimal getFC3_CNT() {
        return this.FC3_CNT;
    }

    public void setFC3_CNT(BigDecimal FC3_CNT) {
        this.FC3_CNT = FC3_CNT;
    }

    public BigDecimal getFC4_CNT() {
        return this.FC4_CNT;
    }

    public void setFC4_CNT(BigDecimal FC4_CNT) {
        this.FC4_CNT = FC4_CNT;
    }

    public BigDecimal getFC5_CNT() {
        return this.FC5_CNT;
    }

    public void setFC5_CNT(BigDecimal FC5_CNT) {
        this.FC5_CNT = FC5_CNT;
    }

    public BigDecimal getFCH_CNT() {
        return this.FCH_CNT;
    }

    public void setFCH_CNT(BigDecimal FCH_CNT) {
        this.FCH_CNT = FCH_CNT;
    }

    public BigDecimal getOP_CNT() {
        return this.OP_CNT;
    }

    public void setOP_CNT(BigDecimal OP_CNT) {
        this.OP_CNT = OP_CNT;
    }

    public BigDecimal getPS_CNT() {
        return this.PS_CNT;
    }

    public void setPS_CNT(BigDecimal PS_CNT) {
        this.PS_CNT = PS_CNT;
    }

    public String getPS_CNT_REMARK() {
        return this.PS_CNT_REMARK;
    }

    public void setPS_CNT_REMARK(String PS_CNT_REMARK) {
        this.PS_CNT_REMARK = PS_CNT_REMARK;
    }

    public BigDecimal getOPH_CNT() {
        return this.OPH_CNT;
    }

    public void setOPH_CNT(BigDecimal OPH_CNT) {
        this.OPH_CNT = OPH_CNT;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("DEPT_ID", getDEPT_ID())
            .toString();
    }

}
