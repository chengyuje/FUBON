package com.systex.esoaf.jms.pool;

import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.Connection;

import com.systex.esoaf.jms.AdvancedJMSException;
import com.systex.esoaf.jms.AlreadyClosedException;
import com.systex.esoaf.jms.pool.ConnectionPool;
import com.systex.esoaf.jms.pool.SessionKey;

import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.PoolableObjectFactory;

public class SessionPool implements PoolableObjectFactory
{
    private ConnectionPool connectionPool;
    private SessionKey key;
    private ObjectPool sessionPool;

    public SessionPool(ConnectionPool connectionPool, SessionKey key, ObjectPool sessionPool)
    {
        this.connectionPool = connectionPool;
        this.key = key;
        this.sessionPool = sessionPool;
        sessionPool.setFactory(this);
    }

    public void close() throws Exception {
        if (sessionPool != null) {
            sessionPool.close();
        }
        sessionPool = null;
    }

    public ISessionProxy borrowSession() throws JMSException {
        try {
            Object object = getSessionPool().borrowObject();
            return (ISessionProxy)object;
        } catch (JMSException e) {
            throw e;
        } catch (Exception e) {
            throw new AdvancedJMSException("Failed to borrow session from pool: " + e, e);
        }
    }

    public void returnSession(ISessionProxy session) throws JMSException {
        // lets check if we are already closed
        getConnection();
        try {
            getSessionPool().returnObject(session);
        } catch (Exception e) {
            throw new AdvancedJMSException("Failed to return session to pool: " + e, e);
        }
    }

    // PoolableObjectFactory methods
    // -------------------------------------------------------------------------
    public Object makeObject() throws Exception {
    	    	
    	return PooledSessionProxy.newProxyInstance(createSession(), this);
    }

    public void destroyObject(Object o) throws Exception {
        ISessionProxy session = (ISessionProxy)o;
        session.getInternalSession().close();
    }

    public boolean validateObject(Object o) 
    {
        return true;
    }

    public void activateObject(Object o) throws Exception 
    {
    }

    public void passivateObject(Object o) throws Exception 
    {    	
    }

    private ObjectPool getSessionPool() throws AlreadyClosedException {
        if (sessionPool == null) {
            throw new AlreadyClosedException();
        }
        return sessionPool;
    }

    private Connection getConnection() throws JMSException 
    {
        return connectionPool.getConnection();
    }

    private Session createSession() throws JMSException 
    {
        return (Session)getConnection().createSession(key.isTransacted(), key.getAckMode());
    }

}
