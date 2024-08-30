package com.systex.jbranch.platform.common.dataaccess.transaction.impl.hibernate;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.systex.jbranch.platform.common.dataaccess.helper.DataAccessHelper;
import com.systex.jbranch.platform.common.dataaccess.transaction.TransactionIF;
import com.systex.jbranch.platform.common.errHandle.DAOException;

public class JDBCTransactionImpl implements TransactionIF{
	
//	public void begin() throws DAOException{	
//		try{
//			begin(DataAccessHelper.DEFAULT_TRANSACTION_ID);
//		}catch(DAOException e){
//    		throw e;
//		}
//	}
//	
//	public void commit() throws DAOException{
//		try{
//			commit(DataAccessHelper.DEFAULT_TRANSACTION_ID);
//		}catch(DAOException e){
//    		throw e;
//		}
//	}
//
//	public void rollback() throws DAOException{
//		try{
//			rollback(DataAccessHelper.DEFAULT_TRANSACTION_ID);
//		}catch(DAOException e){
//    		throw e;
//		}
//	}
//	
//	public void setTimeout(int seconds) throws DAOException{
//		try{
//			setTimeout(DataAccessHelper.DEFAULT_TRANSACTION_ID, seconds);
//		}catch(DAOException e){
//    		throw e;
//		}
//	}
//	
//
//	public void begin(String trxName) throws DAOException{
//		try{
//			HibernateUtil.setCurrentTransactionId(trxName);
//		}catch(Exception e){
//			DAOException je = new DAOException(DataAccessHelper.ERR_MSG_DATA_ACCESS_EXCEPTION);
//    		je.setException(e);
//    		throw je;
//		}
//	}
//	
//	public void commit(String trxName) throws DAOException{
//		
//		Transaction transaction = HibernateUtil.getTransaction(trxName);
//		
//		//若還沒有啟動任何Dao，則沒有Session，故Transaction也尚未啟動
//		if(transaction == null){
//			throw new DAOException(DataAccessHelper.ERR_MSG_CONNECTION_NOT_BEGIN);
//		}
//		
//		Session session = HibernateUtil.getSession(trxName);
//		
//		try{
//			transaction.commit();
//		}catch(Exception e){
//			DAOException je = new DAOException(DataAccessHelper.ERR_MSG_DATA_ACCESS_EXCEPTION);
//    		je.setException(e);
//    		throw je;
//		}finally{
//			try{session.flush();}catch(Exception e){}
//			try{session.close();}catch(Exception e){}
//			try{HibernateUtil.removeSession(trxName);}catch(Exception e){}
//			try{HibernateUtil.removeTransaction(trxName);}catch(Exception e){}
//		}
//	}
//	
//	public void rollback(String trxName) throws DAOException{
//
//		Transaction transaction = HibernateUtil.getTransaction(trxName);
//		
//		//若還沒有啟動任何Dao，則沒有Session，故Transaction也尚未啟動
//		if(transaction == null){
//			throw new DAOException(DataAccessHelper.ERR_MSG_CONNECTION_NOT_BEGIN);
//		}
//		
//		Session session = HibernateUtil.getSession(trxName);
//		
//		try{
//			transaction.rollback();
//		}catch(Exception e){
//			DAOException je = new DAOException(DataAccessHelper.ERR_MSG_DATA_ACCESS_EXCEPTION);
//    		je.setException(e);
//    		throw je;
//		}finally{
//			try{session.flush();}catch(Exception e){}
//			try{session.close();}catch(Exception e){}
//			try{HibernateUtil.removeSession(trxName);}catch(Exception e){}
//			try{HibernateUtil.removeTransaction(trxName);}catch(Exception e){}
//		}
//	}
//	
//	public void setTimeout(String trxName,int seconds) throws DAOException{
//		try{
//			Transaction transaction = HibernateUtil.getTransaction(trxName);
//			transaction.setTimeout(seconds);
//		}catch(Exception e){
//			DAOException je = new DAOException(DataAccessHelper.ERR_MSG_DATA_ACCESS_EXCEPTION);
//    		je.setException(e);
//    		throw je;
//		}
//	}
}
