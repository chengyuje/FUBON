package com.systex.jbranch.platform.common.dataManager;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.errHandle.NotFoundException;
import com.systex.jbranch.platform.common.platformdao.table.TbsystxnVO;

public class TransactionDefinitionHashTable {
	
	static private   Hashtable   transDefHashTable=new Hashtable();

	public static Hashtable getTransDefHashTable() {
		return transDefHashTable;
	}

	public static void setTransDefHashTable(Hashtable transDefHashTable) {
		TransactionDefinitionHashTable.transDefHashTable = transDefHashTable;
	}
	
	public static TransactionDefinition getTransactionDefinition(String txnCode)throws JBranchException{
		TransactionDefinition transDef=(TransactionDefinition)transDefHashTable.get(txnCode);
		if(transDef!=null){

			return transDef;
		}else{
			
			transDef=getTransactionDefinitionFromDB(txnCode);
			transDefHashTable.put(txnCode,transDef);

			return transDef;
		}
			
	}
	
	private static TransactionDefinition getTransactionDefinitionFromDB(String txnCode)throws JBranchException{
		TransactionDefinition td=new TransactionDefinition();
		DataAccessManager dam = new DataAccessManager();
		TbsystxnVO  txnVO = null;
		
		if (txnCode.trim()==""){
			txnVO.setTxncode("");
			txnVO.setTxnname("");
			txnVO.setSystype("");
			txnVO.setJrntype("");
			txnVO.setCrdb("");
		}else{
				
			txnVO = (TbsystxnVO) dam.findByPKey(TbsystxnVO.TABLE_UID,txnCode);
			if (txnVO == null){
				List<String> errMsgList = new ArrayList<String>();
				errMsgList.add(txnCode);
				throw new JBranchException("ehl_01_pipeline_001", errMsgList);
			}
		}
		td.setTxnCode(txnVO.getTxncode().trim());
		td.setTxnName(txnVO.getTxnname().trim());
		td.setSysType(txnVO.getSystype().trim());
		td.setJrnType(txnVO.getJrntype().trim());
		td.setCrdb(txnVO.getCrdb().trim());
		return td;
	}

}
