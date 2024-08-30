package com.systex.jbranch.platform.common.platformdao.table;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.dao.DaoIF;
import com.systex.jbranch.platform.common.errHandle.DAOException;

public interface TbsysworkstationDaoIF extends DaoIF<TbsysworkstationVO, String>{
	public static final String [] FINDBY_BRANCHID_WORKSTATIONID = { "brchid", "wsid" };
	public enum FindByFields{
		
	}
	//public static int findBy_brchID_wsID=1;
	public List<TbsysworkstationVO> findByBranchWsID(String starBrchID,String endBrchID,String startWsID,String endWsID) throws com.systex.jbranch.platform.common.
	errHandle.NotFoundException ,DAOException;
}
