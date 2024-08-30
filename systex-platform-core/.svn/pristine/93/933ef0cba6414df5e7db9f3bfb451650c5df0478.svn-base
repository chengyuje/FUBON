package com.systex.jbranch.platform.common.dataaccess.dao.impl.hibernate;

import java.io.Serializable;
import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.systex.jbranch.platform.common.dataManager.UUID;
import com.systex.jbranch.platform.common.dataaccess.dao.DaoIF;
import com.systex.jbranch.platform.common.dataaccess.dao.DaoProxyIF;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.DuplicateException;
import com.systex.jbranch.platform.common.errHandle.NotFoundException;

/**
 * 此類別主要是做Dao控制Transaction用，
 * 此類別中對應了DaoIF所有的method，只作轉呼叫DaoIF用，
 * 另多出newTransaction開頭的，為使用新的獨立Transaction來呼叫
 * 
 */
public class DaoProxy implements DaoProxyIF{
	
	/**
	 * 請參照DaoIF
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public Object create(DaoIF dao, Object vo) throws DuplicateException, DAOException{
			return dao.create(vo);
	}
	
	/**
	 * 請參照DaoIF
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public Object create(DaoIF dao, Object vo, UUID uuid) throws DuplicateException, DAOException{
			return dao.create(vo, uuid);
	}
	
	/**
	 * 請參照DaoIF
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public Object update(DaoIF dao, Object vo) throws DAOException{
		return dao.update(vo);
	}
	
	/**
	 * 請參照DaoIF
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public Object update(DaoIF dao, Object vo,UUID uuid) throws DAOException{
		return dao.update(vo, uuid);
	}
	
	/**
	 * 請參照DaoIF
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(DaoIF dao, Object vo){
		dao.delete(vo);
	}
	
	/**
	 * 使用獨立Transaction新增資料
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Object newTransactionCreate(DaoIF dao, Object vo) throws DuplicateException, DAOException{
		return dao.create(vo);
	}
	
	/**
	 * 使用獨立Transaction新增資料
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Object newTransactionCreate(DaoIF dao, Object vo, UUID uuid) throws DuplicateException, DAOException{
		return dao.create(vo, uuid);
	}
	
	/**
	 * 使用獨立Transaction更新資料
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Object newTransactionUpdate(DaoIF dao, Object vo) throws DAOException{
		return dao.update(vo);
	}
	
	/**
	 * 使用獨立Transaction更新資料
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Object newTransactionUpdate(DaoIF dao, Object vo,UUID uuid) throws DAOException{
		return dao.update(vo, uuid);
	}
	
	/**
	 * 使用獨立Transaction刪除資料
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void newTransactionDelete(DaoIF dao, Object vo){
		dao.delete(vo);
	}
	
	/**
	 * 請參照DaoIF
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public Object findByPKey(DaoIF dao, Serializable pKey, boolean lock) throws 
					DAOException {
		return dao.findByPKey(pKey, lock);		
	}
	
	/**
	 * 請參照DaoIF
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Object newTransactionFindByPKey(DaoIF dao, Serializable pKey, boolean lock) throws 
					DAOException {
		return dao.findByPKey(pKey, lock);		
	}
	
	/**
	 * 請參照DaoIF
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public Object findByPKey(DaoIF dao, Serializable pKey) throws  DAOException {
		return dao.findByPKey(pKey);
	}
	
	/**
	 * 請參照DaoIF
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Object newTransactionFindByPKey(DaoIF dao, Serializable pKey) throws  DAOException {
		return dao.findByPKey(pKey);
	}
	
	/**
	 * 請參照DaoIF
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public List findAll(DaoIF dao) throws  DAOException {
		return dao.findAll();
	}
	
	/**
	 * 請參照DaoIF
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public List newTransactionFindAll(DaoIF dao) throws  DAOException {
		return dao.findAll();
	}
	
	/**
	 * 請參照DaoIF
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public List findByFields(DaoIF dao, Object vo, String [] fields) throws NotFoundException, DAOException {
		return dao.findByFields(vo, fields);
	}
	
	/**
	 * 請參照DaoIF
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public List newTransactionFindByFields(DaoIF dao, Object vo, String [] fields) throws NotFoundException, DAOException {
		return dao.findByFields(vo, fields);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List findByCriteria(DaoIF dao, List<Criterion> criterions,
			List<Order> orders) throws DAOException {
		
		return dao.findByCriteria(criterions, orders, -1, -1);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public List newTransactionFindByCriteria(DaoIF dao,
			List<Criterion> criterions, List<Order> orders) throws DAOException {
		return dao.findByCriteria(criterions, orders, -1, -1);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List findByCriteria(DaoIF dao, List<Criterion> criterions,
			List<Order> orders, int firstResult, int maxResults)
			throws DAOException {
		
		return dao.findByCriteria(criterions, orders, firstResult, maxResults);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public List newTransactionFindByCriteria(DaoIF dao,
			List<Criterion> criterions, List<Order> orders, int firstResult, int maxResults) throws DAOException {
		return dao.findByCriteria(criterions, orders, firstResult, maxResults);
	}
}
