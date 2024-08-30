package com.systex.jbranch.platform.server.info;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataManager.Branch;
import com.systex.jbranch.platform.common.dataManager.BranchFactoryIF;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.PlatformVOHelper;
import com.systex.jbranch.platform.common.dataManager.UUID;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.common.util.ThreadDataPool;

public class SysInfo
{

	private static UUID getUUID()
	{
		return (UUID)ThreadDataPool.getData(ThreadDataPool.KEY_UUID);
	}

    public static Object getInfoValue(String key) throws JBranchException{

    	if(key.equals(SystemVariableConsts.LOGINID)){

    		return DataManager.getUser(getUUID()).getUserID();

    	}else if (key.equals(SystemVariableConsts.LOGINNAME)){

    		return DataManager.getUser(getUUID()).getUserName();

    	}
    	else if (key.equals(SystemVariableConsts.LOGINROLE))
    	{
    		return DataManager.getUser(getUUID()).getUserAuth();
    	}
    	else if (key.equals(SystemVariableConsts.LOGINROLENAME))
    	{
    		return DataManager.getUser(getUUID()).getPlatFormVO().getVar(PlatformVOHelper._FPS_USER_ROLENAME);
    	}
    	else if (key.equals(SystemVariableConsts.LOGIN_AOCODE_LIST))
    	{
    		return DataManager.getUser(getUUID()).getBizlogicVO().getVar(key);
    	}
    	else if (key.equals(SystemVariableConsts.LOGINLEVEL)){

    		return DataManager.getUser(getUUID()).getLevel();

    	}else if (key.equals(SystemVariableConsts.LOGINBRH)){

            return DataManager.getWorkStation(getUUID()).getSignOnBranchID();

    	}
    	else if (key.equals(SystemVariableConsts.LOGINBRHNAME))
    	{
    		String branchId = (String)getInfoValue(SystemVariableConsts.LOGINBRH);
    		Branch branch = DataManager.getBranch(branchId);
    		if(branch == null){
    			throw new JBranchException("查無此分行[" + branchId + "]");
    		}
	 		return DataManager.getBranch((String)getInfoValue(SystemVariableConsts.LOGINBRH)).getName();

//    		if(brhid!=null)
//    		{
//    			DataAccessManager dam= new DataAccessManager();
//    			TbsysbranchVO branch=(TbsysbranchVO)dam.findByPKey(TbsysbranchVO.TABLE_UID,brhid);
//    			if(branch!=null)
//    			{
//    				return branch.getName();
//    			}
//    		}
//    		return "";
    	}
    	else if (key.equals(SystemVariableConsts.NOWFUNCTION)){

            return DataManager.getTransactionDefinition(getUUID()).getTxnCode();

    	}else if (key.equals(SystemVariableConsts.HOSTDATE)){

            return getCurrentTime("yyyyMMdd");

    	}else if (key.equals(SystemVariableConsts.HOSTTIME)){

    		return getCurrentTime("HHmmss");

       	}else if (key.equals(SystemVariableConsts.FILETRANSIN_PATH)){
        	String path=DataManager.getSystem().getRoot() +
		       			System.getProperties().getProperty("file.separator")+
		       			DataManager.getSystem().getPath().get("fileTransferIN").toString();

        	File filePath = new File(path);
        	if(! filePath.exists()){
        		filePath.mkdirs();
        	}
       		return path;

       	}else if (key.equals(SystemVariableConsts.FILETRANSOUT_PATH)){
       		String path=DataManager.getSystem().getRoot() +
     		       		System.getProperties().getProperty("file.separator")+
     		       			DataManager.getSystem().getPath().get("fileTransferOUT").toString();
       		File filePath = new File(path);
        	if(! filePath.exists()){
        		filePath.mkdirs();
        	}
       		return path;

       	}
       	else if (key.equals(SystemVariableConsts.TEMP_PATH)){
			File filePath = new File(DataManager.getRealPath(), DataManager
					.getSystem().getPath().get("temp").toString());
        	if(! filePath.exists()){
        		filePath.mkdirs();
        	}
       		return filePath.toString() + System.getProperty("file.separator");
       	}
       	else if (key.equals(SystemVariableConsts.SERVER_PATH)){
       		return DataManager.getRealPath();
       	}
       	else if (key.equals(SystemVariableConsts.TEMP_PATH_RELATIVE)){
       		return DataManager.getSystem().getPath().get("temp").toString();
       	}
    	else if (key.equals(SystemVariableConsts.RACF_ID))
       	{
       		return DataManager.getUser(getUUID()).getPlatFormVO().getVar(PlatformVOHelper._FPS_USER_RACFID);
    	}
    	else if (SystemVariableConsts.TXNINFO.equals(key))
    	{
    		DataAccessManager dam= new DataAccessManager();
    		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_SQL);

    		qc.setQueryString("SELECT TXNCODE \"txncode\", TXNNAME \"txnname\", MODULEID \"moduleid\" FROM TBSYSTXN");
    		List list=dam.executeQuery(qc);

    		Map result=new HashMap();
    		for(int i=0;i<list.size();i++)
    		{
    			Map record=(Map)list.get(i);
    			result.put(record.get("txncode"), record);
    		}
    		return result;
    	} else if (key.equals(SystemVariableConsts.ACCOUNT_ID)){	
    		return DataManager.getUser(getUUID()).getPlatFormVO().getVar(PlatformVOHelper._ACCOUNT_ID);    
    		
    	} else if (key.equals(SystemVariableConsts.FULLBRANCHLIST))
       	{
    		BranchFactoryIF branchFactory = (BranchFactoryIF) PlatformContext.getBean(BranchFactoryIF.DEFAULT_BRANCH_FACTORY);
    		return branchFactory.getBranchLabelList();
    	}
    	else{
    		Object rtn =DataManager.getSystem().getVars(key);
    		if (rtn == null)
    		{
    			setInfoValue(key);
    			rtn =DataManager.getSystem().getVars(key);
    		}
    		return rtn;

    	}

	}

    public static void setInfoValue(String key) throws JBranchException{
    	/*
    	if (key.equals(SystemVariableConsts.AVAILBRANCHLIST)){
    		//目前邏輯為查詢所有分行,待SA確認
    		//doc內標明scope為server端all
    		//若到時需要by user get,再調整
    		Map<String,String> data  = new HashMap<String,String>();

    		DataAccessManager dam= new DataAccessManager();
    		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_SQL);

    		qc.setQueryString("SELECT BRCHID,NAME FROM TBSYSBRANCH ORDER BY BRCHID");
    		List result=dam.executeQuery(qc);
    		Map recordMap;
    		for(int i=0 ; i<result.size() ; i++){
    			recordMap = (Map) result.get(i);

    			data.put((String)recordMap.get("BRCHID"), (String)recordMap.get("NAME"));
    		}
    		DataManager.getSystem().setVars(key, data);
    	}
    	*/
//    	if (key.equals(SystemVariableConsts.SYS_REF_XRATE)){
//			Map<String,Double> data = new HashMap<String, Double>();
//
//		 	DataAccessManager dam = new DataAccessManager();
//			QueryConditionIF qc = dam.getQueryCondition(dam.QUERY_LANGUAGE_TYPE_SQL);
//			//qc.setQueryString("Select CUR_ID, RATE FROM TBFPS_PRODUCT_RATE  WHERE RATE_DT = (SELECT MAX(RATE_DT) FROM TBFPS_PRODUCT_RATE)");
//			qc.setQueryString("SELECT A.CUR_ID, A.RATE FROM TBFPS_PRODUCT_RATE A JOIN (SELECT B.CUR_ID,MAX(B.RATE_DT) RATE_DT FROM TBFPS_PRODUCT_RATE B GROUP BY B.CUR_ID) AS C ON C.CUR_ID=A.CUR_ID AND C.RATE_DT=A.RATE_DT");
//			List result = dam.executeQuery(qc);
//			Map recordMap;
//			for(int i=0;i<result.size();i++){
//
//				recordMap = (Map) result.get(i);
//
//				BigDecimal rateBDC = (BigDecimal) recordMap.get("RATE");
//				double rate = rateBDC.doubleValue();
//				data.put((String)recordMap.get("CUR_ID"), rate);
//
//			}
//			DataManager.getSystem().setVars(key, data);
//		}
//    	else if (key.equals(SystemVariableConsts.SYS_XCHG_XRATE)){
//			Map<String,Double> data = new HashMap<String, Double>();
//
//		 	DataAccessManager dam = new DataAccessManager();
//			QueryConditionIF qc = dam.getQueryCondition(dam.QUERY_LANGUAGE_TYPE_SQL);
//			qc.setQueryString("SELECT A.CUR_ID, A.RATE FROM TBFPS_XCHG_RATE A JOIN (SELECT B.CUR_ID,MAX(B.RATE_DT) RATE_DT FROM TBFPS_XCHG_RATE B GROUP BY B.CUR_ID) AS C ON C.CUR_ID=A.CUR_ID AND C.RATE_DT=A.RATE_DT");
//			List result = dam.executeQuery(qc);
//			Map recordMap;
//			for(int i=0;i<result.size();i++){
//
//				recordMap = (Map) result.get(i);
//
//				BigDecimal rateBDC = (BigDecimal) recordMap.get("RATE");
//				double rate = rateBDC.doubleValue();
//				data.put((String)recordMap.get("CUR_ID"), rate);
//
//			}
//			DataManager.getSystem().setVars(key, data);
//		}
    }

    public static void setInfoValue(String key,Object value) throws JBranchException{
	  	DataManager.getSystem().setVars(key, value);
	}

    public static Object getServerValue(String key){

    	return DataManager.getClientTransaction(getUUID()).getBizLogicVO().getVar(key);
    }

    public static void setServerValue(String key , Object value){

    	DataManager.getClientTransaction(getUUID()).getBizLogicVO().setVar(key, value);

    }

    private static String getCurrentTime(String format){
		SimpleDateFormat sdFormat = new SimpleDateFormat(format);
		Date currentTime = new Date();
		String time = sdFormat.format(currentTime);
		return time;
	}
}
