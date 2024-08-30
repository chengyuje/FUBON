package com.systex.jbranch.platform.common.platformdao.table;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.systex.jbranch.platform.common.dataaccess.dao.impl.hibernate.DaoImpl;

@Transactional
public class TbsyssecupriattassHibernateDAO extends DaoImpl<TbsyssecupriattassVO,String> implements TbsyssecupriattassDaoIF {
	
	public List<TbsyssecupriattassVO> findByHql(String hql){
		return null;
		//return getSession().createQuery(hql).list();
	}
	public void deleteByPriID_PAttID_(String hql){
		//getSession().delete(hql);
	}
}
