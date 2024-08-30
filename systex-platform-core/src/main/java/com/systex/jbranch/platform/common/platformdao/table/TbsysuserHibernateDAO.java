package com.systex.jbranch.platform.common.platformdao.table;

import java.util.List;

import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.springframework.transaction.annotation.Transactional;

import com.systex.jbranch.platform.common.dataaccess.dao.impl.hibernate.DaoImpl;
import com.systex.jbranch.platform.common.errHandle.DAOException;


@Transactional
public class TbsysuserHibernateDAO extends DaoImpl<TbsysuserVO, String> implements TbsysuserDaoIF {
	
	// TODO ?��SQLUtil?��???
	public List<TbsysuserVO> findByBranchID(String branchID,String startID,String endID) throws com.systex.jbranch.platform.common.
	errHandle.NotFoundException ,DAOException {
		return null;
		
//		List<TbsysuserVO> list = null;
//		try{
//		getSession().createCriteria(getPersistentClass())
//		.add(Expression.eq("brchid", branchID))
//		.add(Expression.between("tellerid", startID, endID))
//		.addOrder(Order.asc("tellerid"))
//		.list();
//		
//		if(list.size()==0){
//			throw new com.systex.jbranch.platform.common.
//			errHandle.NotFoundException ("");
//		}else{
//			return list;
//		 }
//		}catch(com.systex.jbranch.platform.common.
//				errHandle.NotFoundException e){
//			e.printStackTrace();
//        	throw e;      	
//		} catch (Exception e) {
//			e.printStackTrace();
//			e.printStackTrace();
//			DAOException daoEx = new DAOException("");
//			daoEx.setException(e);
//			throw daoEx;
//		}	
	}
}