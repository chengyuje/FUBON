package com.systex.jbranch.platform.common.dataaccess.dao.impl.hibernate;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.LockOptions;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.UUID;
import com.systex.jbranch.platform.common.dataManager.User;
import com.systex.jbranch.platform.common.dataManager.WorkStation;
import com.systex.jbranch.platform.common.dataaccess.dao.DaoIF;
import com.systex.jbranch.platform.common.dataaccess.helper.DataAccessHelper;
import com.systex.jbranch.platform.common.dataaccess.util.ExceptionMessageUtil;
import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.DuplicateException;
import com.systex.jbranch.platform.common.errHandle.EnumErrInputType;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.ObjectUtil;
import com.systex.jbranch.platform.common.util.ThreadDataPool;

/**
 * 此類別為所有table DaoIF採用Hibernate的實作為一個抽象類別，
 * 詳細規格請參考https://www.hibernate.org/328.html
 */
public abstract class DaoImpl<T, PKey extends Serializable> implements DaoIF<T, PKey> {
// ------------------------------ FIELDS ------------------------------

    private static final String DEFAULT_USER = "N/A";

    private Class<T> persistentClass;
    private SessionFactory sessionFactory;
    
    final private static ExceptionMessageUtil exceptionMessageUtil = new ExceptionMessageUtil();
    
	private Logger logger = LoggerFactory.getLogger(DaoImpl.class);
    private boolean isCacheable = true;
    
// --------------------------- CONSTRUCTORS ---------------------------

    /**
     * default construstor
     */
    public DaoImpl() {
        this.persistentClass = (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface DaoIF ---------------------



    /**
     * 請參考DaoIF
     */
    public T findByPKey(PKey pKey, boolean lock) throws
            DAOException {
        try {
            if (pKey == null) {
                throw new DAOException(ErrMsgHelper.PRIMAVLY_KEY_COULD_NOT_BE_NULL);
            }

            T entity;
            if (lock) {
                entity = (T) getSessionFactory().getCurrentSession().load(getPersistentClass(), pKey, LockOptions.UPGRADE);
            } else {
                entity = (T) getSessionFactory().getCurrentSession().load(getPersistentClass(), pKey);
            }

            return entity;
        }
        catch (org.hibernate.ObjectNotFoundException e) {
            return null;
        }
        catch (DAOException e) {
        	voPrint(pKey);
            throw e;
        }
        catch (Exception e) {
        	voPrint(pKey);
            DAOException daoE = new DAOException(EnumErrInputType.MSG, e.getMessage());
            daoE.setException(e);
            throw daoE;
        }
    }

    /**
     * 請參考DaoIF
     */
    public T findByPKey(PKey pKey) throws DAOException {
        try {
            if (pKey == null) {
                throw new DAOException(ErrMsgHelper.PRIMAVLY_KEY_COULD_NOT_BE_NULL);
            }

            T entity;

            entity = (T) getSessionFactory().getCurrentSession().load(getPersistentClass(), pKey);

            return entity;
        }
        catch (org.hibernate.ObjectNotFoundException e) {
            return null;
        }
        catch (DAOException e) {
        	voPrint(pKey);
            throw e;
        }
        catch (Exception e) {
        	voPrint(pKey);
            DAOException daoE = new DAOException(EnumErrInputType.MSG, e.getMessage());
            daoE.setException(e);
            throw daoE;
        }
    }

    /**
     * 請參考DaoIF
     */
    public List<T> findByFields(T entity, String... fields) throws DAOException {
        if (fields == null) {
            throw new DAOException(ErrMsgHelper.FIELD_MAP_ERROR);
        }
        if (entity == null) {
            throw new DAOException(ErrMsgHelper.ENTITY_COULD_NOT_BE_NULL);
        }
        if (fields.length == 0) {
            return findAll();
        }

        Object value;
        Criteria criteria = getSessionFactory().getCurrentSession().createCriteria(getPersistentClass());
        for (int i = 0; i < fields.length; i++) {
            try {
                value = PropertyUtils.getProperty(entity, fields[i]);
            }
            catch (IllegalAccessException e) {
                DAOException daoE = new DAOException(EnumErrInputType.MSG, e.getMessage());
                daoE.setException(e);
                throw daoE;
            }
            catch (InvocationTargetException e) {
                DAOException daoE = new DAOException(EnumErrInputType.MSG, e.getMessage());
                daoE.setException(e);
                throw daoE;
            }
            catch (NoSuchMethodException e) {
                DAOException daoE = new DAOException(EnumErrInputType.MSG, e.getMessage());
                daoE.setException(e);
                throw daoE;
            }

            if (value != null) {
                criteria.add(Restrictions.eq(fields[i], value));
            }
            else {
                criteria.add(Restrictions.isNull(fields[i]));
            }
        }

        //回傳結果
        try {
        	criteria.setCacheable(isCacheable);
        	
            return criteria.list();
        }
        catch (Exception e) {
        	logger.error("criteria={}",criteria);
            DAOException daoE = new DAOException(EnumErrInputType.MSG, e.getMessage());
            daoE.setException(e);
            throw daoE;
        }
    }
    
    @Override
    public List<T> findByCriteria(List<Criterion> criterions, List<Order> orders, int firstResult, int maxResults) throws DAOException {
    	Criteria criteria = getSessionFactory().getCurrentSession().createCriteria(getPersistentClass());
    	if(criterions != null){
    	   	for (int i = 0; i < criterions.size(); i++) {
        		criteria.add(criterions.get(i));
    		}
    	}
    	
    	if(orders != null){
    	   	for (int i = 0; i < orders.size(); i++) {
        		criteria.addOrder(orders.get(i));
    		}
    	}
    	if(firstResult != -1){
    		criteria.setFirstResult(firstResult);
    	}
    	if(maxResults != -1){
    		criteria.setMaxResults(maxResults);
    	}
    	try {
			return criteria.list();
		} catch (HibernateException e) {
			logger.error("criteria={}",criteria);
            DAOException daoE = new DAOException(EnumErrInputType.MSG, e.getMessage());
            daoE.setException(e);
            throw daoE;
		}
    }

    /**
     * 請參考DaoIF
     */
    public List<T> findAll() throws DAOException {
        try {
            String name = this.getPersistentClass().getName();
            Query query = getSessionFactory().getCurrentSession().createQuery("from " + name);
            query.setCacheable(isCacheable);
            return query.list();
        }
        catch (Exception e) {
            DAOException daoE = new DAOException(EnumErrInputType.MSG, e.getMessage());
            daoE.setException(e);
            throw daoE;
        }
    }

    /**
     * 請參考DaoIF
     */
    public T create(T entity, UUID uuid) throws DuplicateException, DAOException {
        VOBase entityVB = (VOBase) entity;
        entityVB.checkDefault();
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        try {
        	String userId = getUserId(uuid);
            entityVB.setCreator(userId);
            entityVB.setModifier(userId);
        }
        catch (Exception e) {
            entityVB.setCreator(DEFAULT_USER);
            entityVB.setModifier(DEFAULT_USER);
        }

        entityVB.setCreatetime(currentTime);
        entityVB.setLastupdate(currentTime);

        try {
            getSessionFactory().getCurrentSession().save(entity);
            flush();
        }
        catch (org.hibernate.exception.ConstraintViolationException e) {
        	throw exceptionMessageUtil.processException(e);
        }catch (Exception e) {
        	voPrint(entityVB);
            DAOException de = new DAOException(EnumErrInputType.MSG, e.getMessage());
            de.setException(e);
            throw de;
        }
        return entity;
    }


    /**
     * 請參考DaoIF
     */
    public T create(T entity) throws DuplicateException, DAOException {
        String userId = getUserId();
        VOBase entityVB = (VOBase) entity;
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        entityVB.setCreator(userId);
        entityVB.setModifier(userId);
        entityVB.setCreatetime(currentTime);
        entityVB.setLastupdate(currentTime);
        try {
            getSessionFactory().getCurrentSession().save(entity);
            flush();
        }
        catch (org.hibernate.exception.ConstraintViolationException e) {
        	throw exceptionMessageUtil.processException(e);
        }catch (Exception e) {
        	voPrint(entityVB);
            DAOException de = new DAOException(EnumErrInputType.MSG, e.getMessage());
            de.setException(e);
            throw de;
        }
        return entity;
    }

	private void voPrint(Object vo) {
		if(vo == null){
			logger.warn("vo is null");
		}else{
			try {
				Map<String, Object> voMap = ObjectUtil.voToMap(vo);
				logger.error("vo={}", voMap);
			} catch (JBranchException e1) {
				logger.warn(e1.getMessage(), e1);
			}
		}
	}

    /**
     * 請參考DaoIF
     */
    public T update(T entity, UUID uuid) throws DAOException {
        VOBase entityVB = (VOBase) entity;

        try {
            entityVB.setModifier(getUserId(uuid));
        }
        catch (Exception e) {
            entityVB.setModifier(DEFAULT_USER);
        }
        entityVB.setLastupdate(new Timestamp(System.currentTimeMillis()));

        try {

            getSessionFactory().getCurrentSession().saveOrUpdate(entity);
            flush();
        }catch (org.hibernate.exception.ConstraintViolationException e) {
        	throw exceptionMessageUtil.processException(e);
        }
        catch (org.hibernate.PropertyValueException e) {
        	voPrint(entity);
            DAOException de = new DAOException(EnumErrInputType.MSG, e.getMessage());
            de.setException(e);
            throw de;
        }
        catch (Exception e) {
        	voPrint(entity);
            DAOException de = new DAOException(EnumErrInputType.MSG, e.getMessage());
            de.setException(e);
            throw de;
        }
        return entity;
    }

    /**
     * 請參考DaoIF
     */
    public T update(T entity) throws DAOException {
        String userId = getUserId();
        VOBase entityVB = (VOBase) entity;
        entityVB.setModifier(userId);
        entityVB.setLastupdate(new Timestamp(System.currentTimeMillis()));

        try {
            getSessionFactory().getCurrentSession().saveOrUpdate(entity);
            flush();
        }catch (org.hibernate.exception.ConstraintViolationException e) {
        	throw exceptionMessageUtil.processException(e);
        }catch (org.hibernate.PropertyValueException e) {
        	voPrint(entity);
            DAOException de = new DAOException(EnumErrInputType.MSG, e.getMessage());
            de.setException(e);
            throw de;
        }catch (Exception e) {
        	voPrint(entity);
            DAOException de = new DAOException(EnumErrInputType.MSG, e.getMessage());
            de.setException(e);
            throw de;
        }
        return entity;
    }

    /**
     * 請參考DaoIF
     */
    public void delete(T entity) {
        try {
			getSessionFactory().getCurrentSession().delete(entity);
			flush();
		} catch (Exception e) {
			voPrint(entity);
			throw new RuntimeException(e.getMessage(), e);
		}
    }

// -------------------------- OTHER METHODS --------------------------

    public void clear() {
        getSessionFactory().getCurrentSession().clear();
    }

    public void flush() {
        getSessionFactory().getCurrentSession().flush();
    }

    /**
     * 利用Hibernate的Criteria來做為底層查詢
     *
     * @param criterion
     * @return
     * @throws DAOException
     */
    protected List<T> findByCriteria(Criterion... criterion) throws DAOException {
        Criteria crit = getSessionFactory().getCurrentSession().createCriteria(getPersistentClass());
        for (Criterion c : criterion) {
            crit.add(c);
        }

        //回傳結果
        try {
        	crit.setCacheable(isCacheable);
            List<T> result = crit.list();
            return result;
        }catch (Exception e) {
        	logger.error("criteria={}", crit);
            DAOException daoE = new DAOException(EnumErrInputType.MSG, e.getMessage());
            daoE.setException(e);
            throw daoE;
        }
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    /**
     * 取得User ID
     *
     * @return
     */
    private String getUserId() {
        String userId = null;
        UUID uuid = (UUID) ThreadDataPool.getData(ThreadDataPool.KEY_UUID);
        if (uuid != null) {
            try {
            	userId = DataManager.getWorkStation(uuid).getUser().getCurrentUserId();
            	
            	if(StringUtils.isBlank(userId))
            		userId = DataManager.getWorkStation(uuid).getUser().getUserID();
            	
                if (userId == null) {
                    userId = "N/A";
                }
            }
            catch (Exception e) {
                String wsId = uuid.getWsId();
                if (wsId != null) {
                    userId = wsId;
                }
                else {
                    userId = "N/A";
                }
            }
        }
        else {
            userId = DataAccessHelper.DEFAULT_USER;
        }
        logger.info("#user id :" + userId);
        return userId;
    }
    
    private String getUserId(UUID uuid) {
        String userId = null;
        
        WorkStation workStation = DataManager.getWorkStation(uuid);
        
        if(workStation != null){
        	User user = workStation.getUser();
        	
        	if(user != null){
        		userId = DataManager.getWorkStation(uuid).getUser().getCurrentUserId();
            	
            	if(StringUtils.isBlank(userId))
            		userId = DataManager.getWorkStation(uuid).getUser().getUserID();
        	}
        }
        
        return userId;
    }

    /**
     * 取得此table連線的SessionFactory
     *
     * @return
     */
    protected SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            throw new IllegalStateException(
                    "SessionFactory has not been set on DAO before usage");
        }
        return sessionFactory;
    }

    /**
     * 設定此table連線的SessionFactory
     *
     * @param sessionFactory
     */
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * 取得此DAO的Class
     *
     * @return
     */
    public Class<T> getPersistentClass() {
        return persistentClass;
    }
}

