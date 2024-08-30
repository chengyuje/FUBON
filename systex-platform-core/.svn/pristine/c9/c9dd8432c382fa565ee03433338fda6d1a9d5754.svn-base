package com.systex.jbranch.platform.common.platformdao.table;

import java.util.List;

import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.springframework.transaction.annotation.Transactional;

import com.systex.jbranch.platform.common.dataaccess.dao.impl.hibernate.DaoImpl;
import com.systex.jbranch.platform.common.errHandle.DAOException;

@Transactional
public class TbsysworkstationHibernateDAO
		extends
		DaoImpl<TbsysworkstationVO, String>
		implements TbsysworkstationDaoIF {

	

	public List<TbsysworkstationVO> findByBranchWsID(String starBrchID,
			String endBrchID, String startWsID, String endWsID)
			throws com.systex.jbranch.platform.common.errHandle.NotFoundException,
			DAOException {
		return null;
//		List<TbsysworkstationVO> list = null;
//
//		try {
//			list = getSession().createCriteria(getPersistentClass()).add(
//					Expression.between("brchid", starBrchID, endBrchID)).add(
//					Expression.between("wsid", startWsID, endWsID)).addOrder(
//					Order.asc("brchid")).addOrder(Order.asc("wsid")).list();
//
//			if (list.size() == 0) {
//				throw new com.systex.jbranch.platform.common.errHandle.NotFoundException("");
//			} else {
//				return list;
//			}
//		} catch (com.systex.jbranch.platform.common.errHandle.NotFoundException e) {
//			e.printStackTrace();
//			throw e;
//		} catch (Exception e) {
//			e.printStackTrace();
//			DAOException daoEx = new DAOException("");
//			daoEx.setException(e);
//			throw daoEx;
//
//		}
	}
}
