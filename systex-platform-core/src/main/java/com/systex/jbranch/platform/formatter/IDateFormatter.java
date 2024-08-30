package com.systex.jbranch.platform.formatter;

import java.sql.Timestamp;
import java.util.Date;

public interface IDateFormatter {
	
	public String format(Date date);
	
	public Timestamp parse(String value)throws ParseException;
		
}
