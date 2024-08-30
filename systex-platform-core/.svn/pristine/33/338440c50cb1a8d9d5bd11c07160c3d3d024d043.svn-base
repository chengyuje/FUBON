package com.systex.jbranch.platform.common.platformdao.table;

import java.sql.Timestamp;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;


/** @author Hibernate CodeGenerator */
public class TbsysserialnumberVO extends VOBase {
	  /** identifier field */
    private String snid;

    /** nullable persistent field */
    private String pattern;

    /** nullable persistent field */
    private Integer period;

    /** nullable persistent field */
    private String periodunit;

    /** nullable persistent field */
    private Timestamp periodupdatetime;

    /** nullable persistent field */
    private Integer min;

    /** nullable persistent field */
    private Long max;

    /** nullable persistent field */
    private String increase;

    /** nullable persistent field */
    private Long value;

    /** nullable persistent field */
    private String creator;
    
    /** nullable persistent field */
    private String modifier;

    public String getModifier() {
		return modifier;
	}

	public void setModifier(String modifier) {
		this.modifier = modifier;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	/** nullable persistent field */
    private Timestamp createtime;

    /** nullable persistent field */
    private Timestamp lastupdate;
    
    public static final String TABLE_UID = "com.systex.jbranch.platform.common.platformdao.table.Tbsysserialnumber";
    public String getTableuid(){
    	return TABLE_UID;
    }
    

    /** full constructor */
    public TbsysserialnumberVO(String snid, String pattern, Integer period, String periodunit, Timestamp periodupdatetime, Integer min, Long max, String increase, Long value, String creator, Timestamp createtime, Timestamp lastupdate) {
        this.snid = snid;
        this.pattern = pattern;
       
        this.period = period;
        this.periodunit = periodunit;
        this.periodupdatetime = periodupdatetime;
        this.min = min;
        this.max = max;
        this.increase = increase;
        this.value = value;
        this.creator = creator;
        this.createtime = createtime;
        this.lastupdate = lastupdate;
    }

    /** default constructor */
    public TbsysserialnumberVO() {
    }

    /** minimal constructor */
    public TbsysserialnumberVO(String snid) {
        this.snid = snid;
    }

    public String getSnid() {
        return this.snid;
    }

    public void setSnid(String snid) {
        this.snid = snid;
    }

    public String getPattern() {
        return this.pattern;
    }

    
    public Integer getPeriod() {
        return this.period;
    }

    public void setPeriod(Integer period) {
        this.period = period;
    }

    public String getPeriodunit() {
        return this.periodunit;
    }

    public void setPeriodunit(String periodunit) {
        this.periodunit = periodunit;
    }

    public Date getPeriodupdatetime() {
        return this.periodupdatetime;
    }

    public void setPeriodupdatetime(Timestamp periodupdatetime) {
        this.periodupdatetime = periodupdatetime;
    }

    public Integer getMin() {
        return this.min;
    }

    public void setMin(Integer min) {
        this.min = min;
    }

    public Long getMax() {
        return this.max;
    }

    public void setMax(Long max) {
        this.max = max;
    }

    public String getIncrease() {
        return this.increase;
    }

    public void setIncrease(String increase) {
        this.increase = increase;
    }

    public Long getValue() {
        return this.value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    public String getCreator() {
        return this.creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Timestamp getCreatetime() {
        return this.createtime;
    }

    public void setCreatetime(Timestamp createtime) {
        this.createtime = createtime;
    }

    public Timestamp getLastupdate() {
        return this.lastupdate;
    }

    public void setLastupdate(Timestamp lastupdate) {
        this.lastupdate = lastupdate;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("snid", getSnid())
            .toString();
    }

}
