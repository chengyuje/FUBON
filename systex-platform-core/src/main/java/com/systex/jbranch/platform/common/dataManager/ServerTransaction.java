package com.systex.jbranch.platform.common.dataManager;

import com.systex.jbranch.platform.common.errHandle.JBranchException;

/**
 * 主要作為當Client發出一個request(一個BizID)時，暫時存放client上傳給各模組的資料。 <br>
 * 
 * @author Eric.Lin <br>
 *
 */

public class ServerTransaction {
private HostVO host;
private ConversationVO conversationVO;
private BizlogicVO bizlogicVO;	//AP 使用的存放區
private PlatFormVO platformVO;	//平台使用的存放區
private String txnCode="";		//client上傳的交易代號	//2009-11-18 add
private String 	bizCode="";		//client上傳的商業邏輯代號

public TransactionDefinition getTxnDefinition(String txnCode)throws JBranchException {	
	return TransactionDefinitionHashTable.getTransactionDefinition(txnCode);
}

public TransactionDefinition getTxnDefinition() throws JBranchException {
	return getTxnDefinition(this.txnCode);
}

public void setTxnCode(String txnCode){
	this.txnCode=txnCode; 
}
public String getTxnCode(){
	return this.txnCode;
}

public String getBizCode() {
	return bizCode;
}

public void setBizCode(String bizCode) {
	this.bizCode = bizCode;
}
/**
 * 取得ConversationVO<br>
 * 若不存在，則new 一個instance回傳。<br>
 * @return the conversation<br>
 */
public ConversationVO getConversationVO() {
	if(conversationVO==null){
		conversationVO=new ConversationVO();
	}
	return conversationVO;
}
/**
 * 取得給主機物件。<br>
 * @return the host<br>
 */
public HostVO getHost() {
	if (host==null)
		host=new HostVO();

	return host;
}
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
 * 設定ConversationVO物件<br>
 * @param conversation :ConversationVO物件<br>
 */
public void setConversationVO(ConversationVO conversationVO) {
	this.conversationVO = conversationVO;
}
/**
 * 設定主機物件。<br>
 * @param host the host to set<br>
 */
public void setHost(HostVO host) {
	this.host = host;
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
