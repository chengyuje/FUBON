package com.systex.jbranch.platform.common.platformdao.table;

import java.util.List;

import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.springframework.transaction.annotation.Transactional;

import com.systex.jbranch.platform.common.dataaccess.dao.impl.hibernate.DaoImpl;
import com.systex.jbranch.platform.common.errHandle.DAOException;

@Transactional
public class TbsyssecuassHibernateDAO extends DaoImpl<TbsyssecuassVO, String> implements TbsyssecuassDaoIF {
	
	public List<TbsyssecuassVO> findByRECEIVERIDDT(String receiverid,String startdate,String enddate) throws com.systex.jbranch.platform.common.
	errHandle.NotFoundException ,DAOException {
		return null;
//		try{
//		return getSession().createCriteria(getPersistentClass())
//		.add(Expression.eq("receiverid", receiverid))
//		.add(Expression.between("startdt", startdate, enddate))
//		.addOrder(Order.asc("receiverid"))
//		.list();
//		}catch(org.hibernate.ObjectNotFoundException e){
//        	throw new com.systex.jbranch.platform.common.errHandle.NotFoundException ("");      	
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new com.systex.jbranch.platform.common.errHandle.DAOException ("");
//		}	
	}
	

}
