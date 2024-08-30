package com.systex.jbranch.platform.common.platformdao.table;

import org.springframework.transaction.annotation.Transactional;

import com.systex.jbranch.platform.common.dataaccess.dao.impl.hibernate.DaoImpl;

@Transactional
public class TbsyspflogHibernateDAO extends DaoImpl<TbsyspflogVO, String> implements TbsyspflogDaoIF {

}
