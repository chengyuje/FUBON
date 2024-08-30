package com.systex.esoaf.jms.pool;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.jms.Connection;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Session;
import org.apache.commons.pool.ObjectPoolFactory;

public class ConnectionPool {

    private Connection connection;
    private Map<SessionKey, SessionPool> cache;
    private AtomicBoolean started = new AtomicBoolean(false);
    private int referenceCount;
    private ObjectPoolFactory poolFactory;
    private long lastUsed = System.currentTimeMillis();
    private boolean hasFailed;
    private boolean hasExpired;
    private int idleTimeout = 30 * 1000;

    public ConnectionPool(Connection connection, ObjectPoolFactory poolFactory) throws JMSException {
    	
        this(connection, new HashMap<SessionKey, SessionPool>(), poolFactory);

		connection.setExceptionListener(
				new ExceptionListener() 
				{
					public void onException(JMSException arg0) 
					{
						synchronized (ConnectionPool.this) 
						{
                			hasFailed = true;
                		}
					}
				}
			);

    }

    public ConnectionPool(Connection connection, Map<SessionKey, SessionPool> cache, ObjectPoolFactory poolFactory) {
        this.connection = connection;
        this.cache = cache;
        this.poolFactory = poolFactory;
    }

    public void start() throws JMSException {
        if (started.compareAndSet(false, true)) {
            connection.start();
        }
    }

    public synchronized Connection getConnection() {
        return connection;
    }

    public Session createSession(boolean transacted, int ackMode) throws JMSException {
        SessionKey key = new SessionKey(transacted, ackMode);
        SessionPool pool = cache.get(key);
        if (pool == null) {
            pool = createSessionPool(key);
            cache.put(key, pool);
        }
        ISessionProxy session = pool.borrowSession();
        return session;
    }

    public synchronized void close() {
        if (connection != null) {
            try {
                Iterator<SessionPool> i = cache.values().iterator();
                while (i.hasNext()) {
                    SessionPool pool = i.next();
                    i.remove();
                    try {
                        pool.close();
                    } catch (Exception e) {
                    }
                }
            } finally {
                try {
                    connection.close();
                } catch (Exception e) {
                } finally {
                    connection = null;
                }
            }
        }
    }

    public synchronized void incrementReferenceCount() {
        referenceCount++;
        lastUsed = System.currentTimeMillis();
    }

    public synchronized void decrementReferenceCount() {
        referenceCount--;
        lastUsed = System.currentTimeMillis();
        if (referenceCount == 0) {
            expiredCheck();
        }
    }

    /**
     * @return true if this connection has expired.
     */
    public synchronized boolean expiredCheck() {
        if (connection == null) {
            return true;
        }
        if (hasExpired) {
            if (referenceCount == 0) {
                close();
            }
            return true;
        }
        if (hasFailed || (idleTimeout > 0 && System.currentTimeMillis() > lastUsed + idleTimeout)) {
            hasExpired = true;
            if (referenceCount == 0) {
                close();
            }
            return true;
        }
        return false;
    }

    public int getIdleTimeout() {
        return idleTimeout;
    }

    public void setIdleTimeout(int idleTimeout) {
        this.idleTimeout = idleTimeout;
    }

    protected SessionPool createSessionPool(SessionKey key) {
    	return new SessionPool(this, key, poolFactory.createPool());
    }
}