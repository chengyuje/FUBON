package com.systex.jbranch.platform.common.platformdao.table;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.dao.DaoIF;




public interface TbsyssecuprifunmapDaoIF extends DaoIF<TbsyssecuprifunmapVO, Long>{
	public static final String [] FINDBY_MOD_ITM_PRI = {"moduleid", "itemid", "privilegeid"};
	public static final String [] FINDBY_MOD_PRI = {"moduleid", "privilegeid"};
	public static final String [] FINDBY_PRI = {"privilegeid"};
	
	public List<TbsyssecuprifunmapVO> findByHql(String hql);
	
}
