package com.systex.jbranch.platform.common.dataManager;

import com.systex.jbranch.platform.common.errHandle.JBranchException;

/**
 * 作為Client發出多個Request時（一個TxnID)，可存取相關的資料的暫存區。<br>
 * @author Administrator
 *
 */

public class ClientTransaction {
 //private TransactionDefinition txnDefinition=null;
 //private String txnCode;		//2009-11-18移到ServerTransaction
 private BizlogicVO bizlogicVO;
 private PlatFormVO platformVO;

 //public void setTxnCode(String txnCode){
//	this.txnCode=txnCode; 
 //}
 //public String getTxnCode(){
//	 return this.txnCode;
 //}
//public TransactionDefinition getTxnDefinition() throws JBranchException {
	
//	if (txnDefinition==null)
//		txnDefinition=new TransactionDefinition();
	
	//return txnDefinition;
//	return getTxnDefinition(this.txnCode);
//}

//public void setTxnDefinition(TransactionDefinition txnDefinition) {
//	this.txnDefinition = txnDefinition;
//}

//public TransactionDefinition getTxnDefinition(String txnCode)throws JBranchException {
	
//	return TransactionDefinitionHashTable.getTransactionDefinition(txnCode);
	
//}



/**
 * 取得BizLogicVO<br>
 * 若不存在，則new 一個instance回傳。<br>
 * @return the bizlogic<br>
 */
public BizlogicVO getBizLogicVO() {
	if(bizlogicVO==null){
		bizlogicVO=new BizlogicVO();
	}
	return bizlogicVO;
}
/**
 * 設定BizlogicVO物件<br>
 * @param bizlogic :BizlogicVO物件<br>
 */
public void setBizlogicVO(BizlogicVO bizlogicVO) {
	this.bizlogicVO = bizlogicVO;
}
/**
 * 取得BizLogicVO<br>
 * 若不存在，則new 一個instance回傳。<br>
 * @return the bizlogic<br>
 */
public PlatFormVO getPlatFormVO() {
	if(platformVO==null){
		platformVO=new PlatFormVO();
	}
	return platformVO;
}
/**
 * 設定BizLogicVO物件<br>
 * @param platform :BizLogicVO物件<br>
 */
public void setPlatFormVO(PlatFormVO platformVO) {
	this.platformVO = platformVO;
}

}
