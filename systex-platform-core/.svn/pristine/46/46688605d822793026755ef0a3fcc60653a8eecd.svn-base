package com.systex.jbranch.platform.common.platformdao.table;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.math.BigDecimal;
import java.sql.Timestamp;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBSYSTABLECLEARVO extends VOBase {

    /** identifier field */
    private Long seq;

    /** persistent field */
    private String type;

    /** persistent field */
    private String tableName;

    /** persistent field */
    private String hisTableName;

    /** nullable persistent field */
    private String expireColumn;

    /** nullable persistent field */
    private String expireColumnType;

    /** nullable persistent field */
    private BigDecimal expire;

    /** nullable persistent field */
    private String extraCondition;

    /** nullable persistent field */
    private String desc;


public static final String TABLE_UID = "com.systex.jbranch.platform.common.platformdao.table.TBSYSTABLECLEAR";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBSYSTABLECLEARVO(String type, String tableName, String hisTableName, String expireColumn, String expireColumnType, BigDecimal expire, String extraCondition, String desc, String creator, Timestamp createtime, Timestamp lastupdate, String modifier, Long version) {
        this.type = type;
        this.tableName = tableName;
        this.hisTableName = hisTableName;
        this.expireColumn = expireColumn;
        this.expireColumnType = expireColumnType;
        this.expire = expire;
        this.extraCondition = extraCondition;
        this.desc = desc;
        this.creator = creator;
        this.createtime = createtime;
        this.lastupdate = lastupdate;
        this.modifier = modifier;
        this.version = version;
    }

    /** default constructor */
    public TBSYSTABLECLEARVO() {
    }

    /** minimal constructor */
    public TBSYSTABLECLEARVO(String tableName) {
        this.tableName = tableName;
    }

    public Long getSeq() {
        return this.seq;
    }

    public void setSeq(Long seq) {
        this.seq = seq;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTableName() {
        return this.tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getHisTableName() {
        return hisTableName;
    }

    public void setHisTableName(String hisTableName) {
        this.hisTableName = hisTableName;
    }

    public String getExpireColumn() {
        return this.expireColumn;
    }

    public void setExpireColumn(String expireColumn) {
        this.expireColumn = expireColumn;
    }

    public String getExpireColumnType() {
        return this.expireColumnType;
    }

    public void setExpireColumnType(String expireColumnType) {
        this.expireColumnType = expireColumnType;
    }

    public BigDecimal getExpire() {
        return this.expire;
    }

    public void setExpire(BigDecimal expire) {
        this.expire = expire;
    }

    public String getExtraCondition() {
        return this.extraCondition;
    }

    public void setExtraCondition(String extraCondition) {
        this.extraCondition = extraCondition;
    }

    public String getDesc() {
        return this.desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void checkDefaultValue() {
         if (expire == null) {
             this.expire= new BigDecimal(0);
         }
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("SEQ", getSeq())
            .toString();
    }

}
