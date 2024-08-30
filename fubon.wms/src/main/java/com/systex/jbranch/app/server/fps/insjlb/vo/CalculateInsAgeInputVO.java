package com.systex.jbranch.app.server.fps.insjlb.vo;

import java.util.Date;

public class CalculateInsAgeInputVO {
	private Date insCustBirthday;
	private Date insSysDate;
	
	public Date getInsCustBirthday() {
		return insCustBirthday;
	}

	public void setInsCustBirthday(Date insCustBirthday) {
		this.insCustBirthday = insCustBirthday;
	}

	public Date getInsSysDate() {
		return insSysDate;
	}

	public void setInsSysDate(Date insSysDate) {
		this.insSysDate = insSysDate;
	}
}
