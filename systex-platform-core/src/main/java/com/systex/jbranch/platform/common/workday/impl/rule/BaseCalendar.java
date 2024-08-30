package com.systex.jbranch.platform.common.workday.impl.rule;

import java.util.Date;

import org.quartz.Calendar;
import org.quartz.impl.calendar.HolidayCalendar;

import com.systex.jbranch.platform.common.errHandle.JBranchException;

public class BaseCalendar extends HolidayCalendar{

	private static final long serialVersionUID = 4900675981150184434L;
	
	protected String name;
	private Date[] dates = null;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void addExcludedDates(Date[] dates){
		if(dates == null){
			return;
		}
		this.dates = dates;
		for (int i = 0; i < dates.length; i++) {
			this.addExcludedDate(dates[i]);
		}
	}
	
	public void rollNextDay() throws JBranchException{
		throw new JBranchException("未支援rollNextDay功能");
	}
	
	public void rollNextWorkDay() throws JBranchException{
		throw new JBranchException("未支援rollNextWorkDay功能");
	}
	
	public String getDateFormat(String format) throws JBranchException{
		Date today = getToday();
		int index = format.indexOf('.');
		if(index != -1){
			String ruleId = format.substring(0, index);
			String pattern = format.substring(index + 1);
			
		}
		return null;
	}

	public Date getToday() throws JBranchException{
		
		return getStartOfDayJavaCalendar(System.currentTimeMillis()).getTime();
	}
	
	public Calendar getQuartzCalendar(){
		HolidayCalendar cal = new HolidayCalendar();
		for (int i = 0; i < dates.length; i++) {
			cal.addExcludedDate(dates[i]);
		}
		return cal;
	}
	
	public boolean isHoliday() throws JBranchException{
		return !isTimeIncluded(getToday().getTime());
	}
	
	public boolean isHoliday(Date date) throws JBranchException{
		return !isTimeIncluded(date.getTime());
	}
	
	public boolean isHoliday(long timeStamp) throws JBranchException{
		return !isTimeIncluded(timeStamp);
	}
}
