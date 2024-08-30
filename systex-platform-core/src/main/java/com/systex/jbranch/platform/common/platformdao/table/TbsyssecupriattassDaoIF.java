package com.systex.jbranch.platform.common.platformdao.table;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.dao.DaoIF;

public interface TbsyssecupriattassDaoIF extends DaoIF<TbsyssecupriattassVO, String>{
	public static final String [] FINDBY_PRIID_PATID = {"privilegeid", "pattributeid"};
	public static final String [] FINDBY_PRIID = {"privilegeid"};
	
	public List<TbsyssecupriattassVO> findByHql(String hql);
}
