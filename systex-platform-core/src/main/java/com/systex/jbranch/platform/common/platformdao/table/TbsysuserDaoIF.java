package com.systex.jbranch.platform.common.platformdao.table;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.dao.DaoIF;
import com.systex.jbranch.platform.common.errHandle.DAOException ;
import com.systex.jbranch.platform.common.errHandle.NotFoundException ;



public interface TbsysuserDaoIF extends DaoIF<TbsysuserVO, String>{
	
	public List<TbsysuserVO> findByBranchID(String branchID,String startID,String endID) throws NotFoundException , DAOException ;
}
