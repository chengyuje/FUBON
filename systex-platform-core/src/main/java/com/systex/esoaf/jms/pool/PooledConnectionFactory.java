package com.systex.esoaf.jms.pool;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import org.apache.commons.pool.ObjectPoolFactory;
import org.apache.commons.pool.impl.GenericObjectPoolFactory;

import com.systex.esoaf.jms.IService;

public class PooledConnectionFactory implements ConnectionFactory,IService
{
    private ConnectionFactory targetConnectionFactory;
    private Map<ConnectionKey, LinkedList<ConnectionPool>> cache = new HashMap<ConnectionKey, LinkedList<ConnectionPool>>();
    private ObjectPoolFactory poolFactory;
    private int maximumActive = 500;
    private int maxConnections = 1;
    private int idleTimeout = 30 * 1000;
    private AtomicBoolean stopped = new AtomicBoolean(false);

    public ConnectionFactory getTargetConnectionFactory() {
        return targetConnectionFactory;
    }

    public void setTargetConnectionFactory(ConnectionFactory targetConnectionFactory) {
        this.targetConnectionFactory = targetConnectionFactory;
    }

    public Connection createConnection() throws JMSException 
    {
        return createConnection(null, null);
    }

    public synchronized Connection createConnection(String userName, String password) throws JMSException {
        if (stopped.get()) {
            //LOG.debug("PooledConnectionFactory is stopped, skip create new connection.");
            return null;
        }
        
        ConnectionKey key = new ConnectionKey(userName, password);
        LinkedList<ConnectionPool> pools = cache.get(key);

        if (pools == null) {
            pools = new LinkedList<ConnectionPool>();
            cache.put(key, pools);
        }

        ConnectionPool connectionPool = null;
        if (pools.size() == maxConnections)
        {
        	connectionPool = pools.removeFirst();
        }

        // Now.. we might get a connection, but it might be that we need to
        // dump it..
        if (connectionPool != null && connectionPool.expiredCheck()) {
        	connectionPool = null;
        }

        if (connectionPool == null) {
            Connection delegate = createConnection(key);
            connectionPool = createConnectionPool(delegate);
        }
        pools.add(connectionPool);
        return PooledConnectionProxy.newProxyInstance(connectionPool);
    }

    protected ConnectionPool createConnectionPool(Connection connection) throws JMSException {
        ConnectionPool result =  new ConnectionPool(connection, getPoolFactory());
        result.setIdleTimeout(getIdleTimeout());
        return result;
    }

    protected Connection createConnection(ConnectionKey key) throws JMSException {
        if (key.getUserName() == null && key.getPassword() == null) {
            return targetConnectionFactory.createConnection();
        } else {
            return targetConnectionFactory.createConnection(key.getUserName(), key.getPassword());
        }
    }

    /**
     * @see org.apache.activemq.service.Service#start()
     */
    public void start() {
        try {
            stopped.set(false);
            createConnection();
        } catch (JMSException e) {
        	//TODO
            //LOG.warn("Create pooled connection during start failed.", e);
            //IOExceptionSupport.create(e);
        }
    }

    public void stop() {
    	//TODO
        //LOG.debug("Stop the PooledConnectionFactory, number of connections in cache: "+cache.size());
        stopped.set(true);
        for (Iterator<LinkedList<ConnectionPool>> iter = cache.values().iterator(); iter.hasNext();) {
            LinkedList list = iter.next();
            for (Iterator i = list.iterator(); i.hasNext();) {
                ConnectionPool connectionPool = (ConnectionPool) i.next();
                try {
                	connectionPool.close();
                }catch(Exception e) {
                    //TODO
                	//LOG.warn("Close connection failed",e);
                }
            }
        }
        cache.clear();
    }

    public ObjectPoolFactory getPoolFactory() {
        if (poolFactory == null) {
            poolFactory = createPoolFactory();
        }
        return poolFactory;
    }

    /**
     * Sets the object pool factory used to create individual session pools for
     * each connection
     */
    public void setPoolFactory(ObjectPoolFactory poolFactory) {
        this.poolFactory = poolFactory;
    }

    public int getMaximumActive() {
        return maximumActive;
    }

    /**
     * Sets the maximum number of active sessions per connection
     */
    public void setMaximumActive(int maximumActive) {
        this.maximumActive = maximumActive;
    }

    /**
     * @return the maxConnections
     */
    public int getMaxConnections() {
        return maxConnections;
    }

    /**
     * @param maxConnections the maxConnections to set
     */
    public void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }

    protected ObjectPoolFactory createPoolFactory() {
        return new GenericObjectPoolFactory(null, maximumActive);
    }

    public int getIdleTimeout() {
        return idleTimeout;
    }

    public void setIdleTimeout(int idleTimeout) {
        this.idleTimeout = idleTimeout;
    }
}
