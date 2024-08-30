package com.systex.jbranch.platform.common.dataaccess.serialnumber;

import java.sql.Timestamp;


import com.systex.jbranch.platform.common.dataaccess.helper.DataAccessHelper;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;

import com.systex.jbranch.platform.common.util.PlatformContext;



public class SerialNumberUtil {
	
	SerialNumberIF sn;
	public SerialNumberUtil() throws DAOException, JBranchException{

		sn = (SerialNumberIF) PlatformContext.getBean(DataAccessHelper.SERIAL_NUMBER_ID);
	}
	
	/**
	 * 取得指定ID的下一個序號
	 * @param snid
	 * @return
	 * @throws JBranchException
	 */
	public String getNextSerialNumber(String snid) throws JBranchException{
		
		return sn.getNextSerialNumber(snid);
	}
	
	public void createNewSerial(String snid, String pattern, Integer period, String periodunit, 
			     Timestamp periodupdatetime, Integer min, Long max, String increase, Long value, 
			     String user) throws JBranchException{
		
		sn.createNewSerial(snid, pattern, period, periodunit, periodupdatetime, min, max, increase, value, user);
	}
	
	/**
	 * 刪除指定ID的下一個序號
	 * @param snid
	 * @throws JBranchException
	 */
	public void deleteSerialNumber(String snid) throws JBranchException{
		sn.deleteSerialNumber(snid);
	}
}
