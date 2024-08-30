package com.systex.jbranch.platform.common.log;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.systex.jbranch.platform.common.dataManager.Branch;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.PlatformVOHelper;
import com.systex.jbranch.platform.common.dataManager.ServerTransaction;
import com.systex.jbranch.platform.common.dataManager.TransactionDefinition;
import com.systex.jbranch.platform.common.dataManager.UUID;
import com.systex.jbranch.platform.common.dataManager.User;
import com.systex.jbranch.platform.common.dataManager.WorkStation;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.conversation.ToaHelperIF;
import com.systex.jbranch.platform.server.conversation.message.Tia;

public class InfoCollector {

	public void infoCollect(Tia tia, ToaHelperIF toaHelper, UUID uuid, Map workflowParams) throws JBranchException {
//		MDC.put(key, val);
//		Branch branch = DataManager.getBranch(uuid);
//		WorkStation ws = DataManager.getWorkStation(uuid);
//		User user = DataManager.getUser(uuid);
//		ServerTransaction st = DataManager.getServerTransaction(uuid);
//		TransactionDefinition td = DataManager.getTransactionDefinition(uuid);
//        if(ws == null){
//        	return;
//        }
//        Map<String, Object> headers = tia.getHeaders();
//        Set<Entry<String, Object>> sets = headers.entrySet();
        
//		//從DataManager取得資訊
//		String brchID = branch != null ? branch.getBrchID() : null;
//		String wsID = ws != null ? ws.getWsID() : null;
//		String txnCode = td != null ? td.getTxnCode() : null;
//		String tellerID = user != null ? user.getUserID() : null;
//		String depID = ""; //尚未實作
//		String roleID = user != null ? user.getUserAuth() : null; 
//		String bizCodeName = st != null ? (String) st.getPlatFormVO().getVar(PlatformVOHelper._BIZCODENAME) : null;
//		String memo = st != null ? (String) st.getPlatFormVO().getVar(PlatformVOHelper._EJMEMO) : null;
//		MDC.put("UUID", uuid);
//		logger.debug("infoCollector==============================");
//		MDC.put(LogHelper.BRANCH_ID, brchID);
//		MDC.put(LogHelper.WS_ID, wsID);
//		MDC.put(LogHelper.TXN_CODE, txnCode);
//		MDC.put(LogHelper.TLR_ID, tellerID);
//		MDC.put(LogHelper.ROLE_ID, roleID);
//		MDC.put(LogHelper.BIZ_CODE_NAME, bizCodeName);
//		MDC.put(LogHelper.MEMO, memo);
//		logger.debug("brchID="+brchID+",wsID="+wsID+",txnCode="+txnCode+",tellerID="+roleID+","+bizCodeName+",memo="+memo);
//        Iterator<Entry<String, Object>> it = sets.iterator();
//        while(it.hasNext()){
//        	Entry<String, Object> entry = it.next();
//        	String key = entry.getKey();
//        	if(key != null){
//        		key = key.toUpperCase();
//        	}
//        	MDC.put(key, entry.getValue());
//        	logger.debug("MDC key=" + key + ", value=" + entry.getValue());
//        }
	}
}
