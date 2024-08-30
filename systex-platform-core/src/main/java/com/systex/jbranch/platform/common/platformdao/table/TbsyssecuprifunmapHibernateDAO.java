package com.systex.jbranch.platform.common.platformdao.table;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.systex.jbranch.platform.common.dataaccess.dao.impl.hibernate.DaoImpl;

@Transactional
public class TbsyssecuprifunmapHibernateDAO extends DaoImpl<TbsyssecuprifunmapVO, Long> implements TbsyssecuprifunmapDaoIF {
	
	

	public List<TbsyssecuprifunmapVO> findByHql(String hql){
		return null;
		//return getSession().createQuery(hql).list();
	}
	
}
