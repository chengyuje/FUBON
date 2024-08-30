package com.systex.jbranch.platform.common.workday.impl.rule;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeSet;

/**
 * 
 * @author Angus Luo
 * @date 2010-05-07
 * 
 */
public class WorkZeroTimeCalendar extends BaseCalendar{

	private static final long serialVersionUID = -5482278210776695240L;

	private TreeSet dates = new TreeSet();
	
	//換日點
	private int zeroTime = 0;

	public int getZeroTime() {
		return zeroTime;
	}

	public void setZeroTime(int zeroTime) {
		this.zeroTime = zeroTime;
	}
	
	@Override
	public boolean isTimeIncluded(long timeStamp) {
		Date lookFor = getZeroTimeStartOfDayJavaCalendar(timeStamp).getTime();
        return !(dates.contains(lookFor));
	}
	
	protected Calendar getZeroTimeStartOfDayJavaCalendar(long timeInMillis) {
        Calendar startOfDay = createJavaCalendar(timeInMillis);
        startOfDay.add(Calendar.HOUR_OF_DAY, zeroTime * -1);
        startOfDay.set(java.util.Calendar.HOUR_OF_DAY, 0);
        startOfDay.set(java.util.Calendar.MINUTE, 0);
        startOfDay.set(java.util.Calendar.SECOND, 0);
        startOfDay.set(java.util.Calendar.MILLISECOND, 0);
        return startOfDay;
    }
	
	@Override
	public Date getToday(){
		
		return getZeroTimeStartOfDayJavaCalendar(System.currentTimeMillis()).getTime();
	}
}
