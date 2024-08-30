package com.systex.jbranch.platform.common.dataaccess.dao;

import java.io.Serializable;
import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;


import com.systex.jbranch.platform.common.dataManager.UUID;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.DuplicateException;

/**
 * Generic的Dao interface
 * https://www.hibernate.org/328.html
 * @param <T>
 * @param <PKey>
 */
public interface DaoIF<T , PKey extends Serializable> {
	
	    /**
	     * 利用PKey搜尋資料
	     * @param pKey
	     * @param lock 是否要row lock
	     * @return
	     * @throws DAOException
	     */
        T findByPKey(PKey pKey, boolean lock) throws  DAOException;

        /**
         * 利用PKey搜尋資料
         * @param pKey
         * @return
         * @throws DAOException
         */
        T findByPKey(PKey pKey) throws  DAOException;
        
        /**
         * 利用指定欄位名稱，與條件值來搜尋資料，若找不到，則回傳size為0的List
         * @param entity vo，塞入搜尋條件值
         * @param fields 需要搜尋的欄位名稱
         * @return VO List
         * @throws DAOException
         */
        List<T> findByFields(T entity, String[] fields) throws DAOException;
        
		/**
		 * @param criterions
		 * @param orders
		 * @param firstResult
		 * @param maxResults
		 * @return
		 * @throws DAOException
		 */
		List<T> findByCriteria(List<Criterion> criterions, List<Order> orders,
				int firstResult, int maxResults) throws DAOException;  
        
        /**
         * select出table中所有的資料
         * @return VO List
         * @throws DAOException
         */
        List<T> findAll() throws DAOException;

        /**
         * insert傳入的資料
         * @param entity
         * @param uuid
         * @return
         * @throws DuplicateException
         * @throws DAOException
         */
        T create(T entity,UUID uuid) throws DuplicateException, DAOException;
        
        /**
         * insert傳入的資料
         * @param entity
         * @return
         * @throws DuplicateException
         * @throws DAOException
         */
        T create(T entity)throws DuplicateException, DAOException;

        /**
         * 更新傳入的資料
         * @param entity
         * @param uuid
         * @return
         * @throws DAOException
         */
        T update(T entity,UUID uuid)throws DAOException;
       
        /**
         * 更新傳入的資料
         * @param entity
         * @return
         * @throws DAOException
         */
        T update(T entity)throws DAOException;

        /**
         * 刪除傳入的資料
         * @param entity
         */
        void delete(T entity);


}
