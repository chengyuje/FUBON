package com.systex.jbranch.platform.common.platformdao.table;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.dao.DaoIF;

public interface TbsysmultilangDaoIF extends DaoIF<TbsysmultilangVO, TbsysmultilangPK>{

	
	public List<TbsysmultilangVO> findByMultiFields(TbsysmultilangVO tbsysmultilangVO);
		
	public boolean deleteMultiFields(TbsysmultilangVO tbsysmultilangVO);
	
	public List<String> distinctFields(String field,String condition);
	
	public List<TbsysmultilangVO> findByGroup(TbsysmultilangVO tbsysmultilangVO);
	
	public List<TbsysmultilangVO> findByProperties(String type, String typeSeq,String locale);

}
