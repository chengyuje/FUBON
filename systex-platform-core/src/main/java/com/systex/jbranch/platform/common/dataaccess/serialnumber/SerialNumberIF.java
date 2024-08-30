package com.systex.jbranch.platform.common.dataaccess.serialnumber;

import java.sql.Timestamp;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;

public interface SerialNumberIF {

	public String getNextSerialNumber(String snid) throws DAOException, JBranchException;

	public void createNewSerial(String snid, String pattern, Integer period, String periodunit, 
		     Timestamp periodupdatetime, Integer min, Long max, String increase, Long value, 
		     String user) throws JBranchException;
	public void deleteSerialNumber(String snid) throws JBranchException;
}
