package com.systex.jbranch.platform.common.platformdao.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TbsysschdVO extends VOBase {

    /** identifier field */
    private String scheduleid;

    /** nullable persistent field */
    private String schedulename;

    /** nullable persistent field */
    private String description;

    /** nullable persistent field */
    private String processor;

    /** nullable persistent field */
    private String cronexpression;

    /** nullable persistent field */
    private BigDecimal repeat;

    /** nullable persistent field */
    private BigDecimal repeatinterval;

    /** nullable persistent field */
    private Timestamp lasttry;

    /** nullable persistent field */
    private String isuse;
    
    /** nullable persistent field */
    private String isscheduled;
    
    /** nullable persistent field */
    private String istriggered;
    
    /** nullable persistent field */
    private String onetime;
    
    /** nullable persistent field */
    private BigDecimal executetime;

    public String getIstriggered() {
		return istriggered;
	}

	public void setIstriggered(String istriggered) {
		this.istriggered = istriggered;
	}

	/** nullable persistent field */
    private String scheduleparameter;
    
    /** persistent field */
    private BigDecimal jobstart;


    public BigDecimal getJobstart() {
		return jobstart;
	}

	public void setJobstart(BigDecimal order) {
		this.jobstart = order;
	}

public static final String TABLE_UID = "com.systex.jbranch.platform.common.platformdao.table.Tbsysschd";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TbsysschdVO(String scheduleid, String schedulename, String description, String processor, String sync, String cronexpression, BigDecimal repeat, BigDecimal repeatinterval, Timestamp lasttry, String isscheduled, String scheduleparameter, String istriggered, Timestamp createtime, String creator, Timestamp lastupdate, String modifier, BigDecimal executetime, Long version) {
        this.scheduleid = scheduleid;
        this.schedulename = schedulename;
        this.description = description;
        this.processor = processor;
        this.cronexpression = cronexpression;
        this.repeat = repeat;
        this.repeatinterval = repeatinterval;
        this.lasttry = lasttry;
        this.isscheduled = isscheduled;
        this.scheduleparameter = scheduleparameter;
        this.istriggered = istriggered;
        this.createtime = createtime;
        this.creator = creator;
        this.lastupdate = lastupdate;
        this.modifier = modifier;
        this.executetime = executetime;
        this.version = version;
    }

    /** default constructor */
    public TbsysschdVO() {
    }

    /** minimal constructor */
    public TbsysschdVO(String scheduleid) {
        this.scheduleid = scheduleid;
    }

    public String getscheduleid() {
        return this.scheduleid;
    }

    public void setscheduleid(String scheduleid) {
        this.scheduleid = scheduleid;
    }

    public String getschedulename() {
        return this.schedulename;
    }

    public void setschedulename(String schedulename) {
        this.schedulename = schedulename;
    }

    public String getdescription() {
        return this.description;
    }

    public void setdescription(String description) {
        this.description = description;
    }

    public String getprocessor() {
        return this.processor;
    }

    public void setprocessor(String processor) {
        this.processor = processor;
    }

    public String getcronexpression() {
        return this.cronexpression;
    }

    public void setcronexpression(String cronexpression) {
        this.cronexpression = cronexpression;
    }

    public BigDecimal getrepeat() {
        return this.repeat;
    }

    public void setrepeat(BigDecimal repeat) {
        this.repeat = repeat;
    }

    public BigDecimal getrepeatinterval() {
        return this.repeatinterval;
    }

    public void setrepeatinterval(BigDecimal repeatinterval) {
        this.repeatinterval = repeatinterval;
    }

    public Timestamp getlasttry() {
        return this.lasttry;
    }

    public void setlasttry(Timestamp lasttry) {
        this.lasttry = lasttry;
    }

    public String getisscheduled() {
        return this.isscheduled;
    }

    public void setisscheduled(String isscheduled) {
        this.isscheduled = isscheduled;
    }

    public String getscheduleparameter() {
        return this.scheduleparameter;
    }

    public void setscheduleparameter(String scheduleparameter) {
        this.scheduleparameter = scheduleparameter;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("scheduleid", getscheduleid())
            .toString();
    }

	public String getIsuse() {
		return isuse;
	}

	public void setIsuse(String isuse) {
		this.isuse = isuse;
	}

	public String getOnetime() {
		return onetime;
	}

	public void setOnetime(String onetime) {
		this.onetime = onetime;
	}

	public BigDecimal getExecutetime() {
		return executetime;
	}

	public void setExecutetime(BigDecimal executetime) {
		this.executetime = executetime;
	}
}
