package com.systex.jbranch.platform.common.platformdao.table;

import org.springframework.transaction.annotation.Transactional;

import com.systex.jbranch.platform.common.dataaccess.dao.impl.hibernate.DaoImpl;

@Transactional
public class TbsyssecupriHibernateDAO extends DaoImpl<TbsyssecupriVO, String> implements TbsyssecupriDaoIF {

}
