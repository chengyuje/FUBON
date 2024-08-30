package com.systex.jbranch.platform.common.dataaccess.dao;

import java.io.Serializable;
import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import com.systex.jbranch.platform.common.dataManager.UUID;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.DuplicateException;

/**
 * 配合DaoIF的Proxy interface
 * 此處的method皆與DaoIF一樣，
 * Proxy指做轉呼叫用，用途為控制DB Transaction，
 * Proxy實作類別定義在Spring的bean file中，
 * 實作類別利用annotation控制 db transaction
 *
 */
public interface DaoProxyIF {
	
	/**
	 * 請參照DaoIF
	 * @param dao
	 * @param vo
	 * @return
	 * @throws DuplicateException
	 * @throws DAOException
	 */
	Object create(DaoIF dao, Object vo)  throws DuplicateException, DAOException;
	
	/**
	 * 請參照DaoIF
	 * @param dao
	 * @param vo
	 * @param uuid
	 * @return
	 * @throws DuplicateException
	 * @throws DAOException
	 */
	Object create(DaoIF dao, Object vo, UUID uuid)  throws DuplicateException, DAOException;
	
	/**
	 * 請參照DaoIF
	 * @param dao
	 * @param vo
	 * @return
	 * @throws DAOException
	 */
	Object update(DaoIF dao, Object vo) throws DAOException;
	
	/**
	 * 請參照DaoIF
	 * @param dao
	 * @param vo
	 * @param uuid
	 * @return
	 * @throws DAOException
	 */
	Object update(DaoIF dao, Object vo,UUID uuid) throws DAOException;
	
	/**
	 * 請參照DaoIF
	 * @param dao
	 * @param vo
	 */
	void delete(DaoIF dao, Object vo);
	
	/**
	 * 請參照DaoIF
	 * @param dao
	 * @param vo
	 * @return
	 * @throws DuplicateException
	 * @throws DAOException
	 */
	Object newTransactionCreate(DaoIF dao, Object vo) throws DuplicateException, DAOException;
	
	/**
	 * 請參照DaoIF
	 * @param dao
	 * @param vo
	 * @param uuid
	 * @return
	 * @throws DuplicateException
	 * @throws DAOException
	 */
	Object newTransactionCreate(DaoIF dao, Object vo, UUID uuid) throws DuplicateException, DAOException;
	
	/**
	 * 請參照DaoIF
	 * @param dao
	 * @param vo
	 * @return
	 * @throws DAOException
	 */
	Object newTransactionUpdate(DaoIF dao, Object vo) throws DAOException;
	
	/**
	 * 請參照DaoIF
	 * @param dao
	 * @param vo
	 * @param uuid
	 * @return
	 * @throws DAOException
	 */
	Object newTransactionUpdate(DaoIF dao, Object vo,UUID uuid) throws DAOException;
	
	/**
	 * 請參照DaoIF
	 * @param dao
	 * @param vo
	 */
	void newTransactionDelete(DaoIF dao, Object vo);
	
	/**
	 * 請參照DaoIF
	 * @param dao
	 * @param pKey
	 * @param lock
	 * @return
	 * @throws DAOException
	 */
	Object findByPKey(DaoIF dao, Serializable pKey, boolean lock) throws 
					DAOException;
	
	/**
	 * 請參照DaoIF
	 * @param dao
	 * @param pKey
	 * @param lock
	 * @return
	 * @throws DAOException
	 */
	Object newTransactionFindByPKey(DaoIF dao, Serializable pKey, boolean lock) throws 
					DAOException;
	
	/**
	 * 請參照DaoIF
	 * @param dao
	 * @param pKey
	 * @return
	 * @throws DAOException
	 */
	Object findByPKey(DaoIF dao, Serializable pKey) throws  DAOException;
	
	/**
	 * 請參照DaoIF
	 * @param dao
	 * @param pKey
	 * @return
	 * @throws DAOException
	 */
	Object newTransactionFindByPKey(DaoIF dao, Serializable pKey) throws  DAOException;
	
	/**
	 * 請參照DaoIF
	 * @param dao
	 * @return
	 * @throws DAOException
	 */
	List findAll(DaoIF dao) throws  DAOException;
	
	/**
	 * 請參照DaoIF
	 * @param dao
	 * @return
	 * @throws DAOException
	 */
	List newTransactionFindAll(DaoIF dao) throws  DAOException;
	
	/**
	 * 請參照DaoIF
	 * @param dao
	 * @param vo
	 * @param fields
	 * @return
	 * @throws DAOException
	 */
	List findByFields(DaoIF dao, Object vo, String [] fields) throws DAOException;
	
	/**
	 * 請參照DaoIF
	 * @param dao
	 * @param vo
	 * @param fields
	 * @return
	 * @throws DAOException
	 */
	List newTransactionFindByFields(DaoIF dao, Object vo, String [] fields) throws DAOException;
	
	/**
	 * @param dao
	 * @param criterions
	 * @param orders
	 * @return
	 * @throws DAOException
	 */
	List findByCriteria(DaoIF dao, List<Criterion> criterions, List<Order> orders) throws DAOException;
	
	/**
	 * @param dao
	 * @param criterions
	 * @param orders
	 * @param firstResult
	 * @param maxResults
	 * @return
	 * @throws DAOException
	 */
	List findByCriteria(DaoIF dao, List<Criterion> criterions, List<Order> orders, int firstResult, int maxResults) throws DAOException;
	
	/**
	 * @param dao
	 * @param criterions
	 * @param orders
	 * @return
	 * @throws DAOException
	 */
	List newTransactionFindByCriteria(DaoIF dao, List<Criterion> criterions, List<Order> orders) throws DAOException;

	/**
	 * @param dao
	 * @param criterions
	 * @param orders
	 * @param firstResult
	 * @param maxResults
	 * @return
	 * @throws DAOException
	 */
	List newTransactionFindByCriteria(DaoIF dao, List<Criterion> criterions,
			List<Order> orders, int firstResult, int maxResults)
			throws DAOException;
}
