package com.systex.jbranch.platform.common.platformdao.table;
import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.dao.DaoIF;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.NotFoundException;

public interface TbsyssecuassDaoIF extends DaoIF<TbsyssecuassVO, String>{
    public static final String [] FINDBY_RECEIVERID = {"receiverid"};
	
	//TODO:待改為使用QueryUtil
	public List<TbsyssecuassVO> findByRECEIVERIDDT(String receiverid,String startdate,String enddate) throws NotFoundException , DAOException ;
	
}
