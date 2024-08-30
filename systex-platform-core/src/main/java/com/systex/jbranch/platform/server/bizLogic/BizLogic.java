package com.systex.jbranch.platform.server.bizLogic;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.workflow.FunctionProvider;
import com.opensymphony.workflow.WorkflowException;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.DataManagerIF;
import com.systex.jbranch.platform.common.dataManager.PlatformVOHelper;
import com.systex.jbranch.platform.common.dataManager.UUID;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataSortManager;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataSortManager.Type;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.EnumErrInputType;
import com.systex.jbranch.platform.common.errHandle.ErrHandleUtils;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.errHandle.SupvConfirmException;
import com.systex.jbranch.platform.common.multiLang.MultiLangUtil;
import com.systex.jbranch.platform.common.security.util.CryptoUtil;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.common.util.ThreadDataPool;
import com.systex.jbranch.platform.server.conversation.ConversationIF;
import com.systex.jbranch.platform.server.conversation.MapTiaIF;
import com.systex.jbranch.platform.server.conversation.ObjectTiaIF;
import com.systex.jbranch.platform.server.conversation.ObjectToaIF;
import com.systex.jbranch.platform.server.conversation.message.EnumMessageType;
import com.systex.jbranch.platform.server.conversation.message.EnumOutputType;
import com.systex.jbranch.platform.server.conversation.message.EnumShowType;
import com.systex.jbranch.platform.server.conversation.message.EnumTiaHeader;
import com.systex.jbranch.platform.server.conversation.message.EnumToaHeader;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/*
*  @author Eric.Lin＜br>
*/

public class BizLogic implements FunctionProvider , IServiceFacade {
// ------------------------------ FIELDS ------------------------------
	
    public static final String BRANCH_ROLLBACK = "To RollBack...";
    public static final String BRANCH_SUPVCONFIRM = "branch_supvconfirm";
    public static final String VAR_CONVERSATION = "Conversation";
    public static final String VAR_UUID = "UUID";
    public static final String LPT_REPORT = "lptReport";
    protected static final String rtnCode = "Z999";
    protected UUID uuid;
    protected ConversationIF conversation;
    private Map args;
    private Logger logger = LoggerFactory.getLogger(BizLogic.class);
    @Autowired
    private DataAccessManager dataAccessManager;
    
    @Autowired
    private DataManagerIF dataManager;

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface FunctionProvider ---------------------

    /** 實作了OSWorkFlow的FunctionProvider的execute Method */
    public void execute(Map transientVars, Map args, com.opensymphony.module.propertyset.PropertySet ps) throws WorkflowException {
        uuid = (UUID) transientVars.get(VAR_UUID);
        conversation = (ConversationIF) transientVars.get(VAR_CONVERSATION);

        //主管帳號密碼
        IPrimitiveMap header = conversation.getTiaHelper().getTia().Headers();

        String custId = (String) header.get(EnumTiaHeader.CustomerID);
        
        if (header.containsKey("SupvID")
                && !"".equals(header.get("SupvID"))
                && header.get("SupvID") != null) {
            String supvid = (String) header.get("SupvID");
            String supvpwd = (String) header.get("SupvPwd");
            DataManager.getServerTransaction(uuid).getPlatFormVO().setVar(PlatformVOHelper._SUPVID, supvid);
            DataManager.getServerTransaction(uuid).getPlatFormVO().setVar(PlatformVOHelper._SUPVPWD, supvpwd);
        }

        this.args = args;
        String method = (String) args.get("method");

        if (logger.isInfoEnabled()) {
            String className = (String) args.get("class.name");
            String beanName = (String) args.get("bean.name");
            boolean isWriteEJ = Boolean.parseBoolean((String) args.get("writeEJ"));
            logger.info(String.format("method:%s,class.name:%s,bean.name:%s,method:%s,isWriteEJ:%s", method, className, beanName, method, isWriteEJ));
            if(isWriteEJ){
            	Logger ejLogger = LoggerFactory.getLogger("EJ_LOG");
            	ejLogger.info("writeEJ");
            }
        }
        try {
            try {
                executeMethods(method);
            }
            catch (InvocationTargetException e) {
                if (e.getCause() == null) {
                    throw new JBranchException(EnumErrInputType.MSG, "InvocationTargetException");
                }
                else {
                    throw e.getCause();
                }
            }
        }
        catch (Rollback e) {
            throw e;
        }
        catch(SupvConfirmException e){
        	sendSupvConfirmMsg(e);
        }
        catch(APException e){
			sendErrMsg_rollback(e);
	    }
        catch (JBranchException e) {
            sendBranchErrMsg(e);
        }
        catch (Throwable e) {
        	logger.error(e.getMessage(), e);
            String message = String.format("執行交易時發生錯誤,模組代號:[%s] 功能代號:[%s] 訊息:[%s]",
                    getTxnCode(), method, ErrHandleUtils.getMessage(e));
            sendSysErrMsg(message, e);
        }
    }

// --------------------- Interface IServiceFacade ---------------------
    /**
     * 傳送主管授權訊息
     *
     * @param message
     */
    protected void sendSupvConfirmMsg(SupvConfirmException e) {
 		conversation.getToaHelper().sendTOA(EnumShowType.Show, EnumMessageType.SupvConfirm, null, e.getMessage());
     	throw new RuntimeException(BRANCH_SUPVCONFIRM);
    }
    
    /**
     * 傳送警告訊息
     *
     * @param message
     */
    public void sendWarningMsg(String ml_idGroup_s) {
        sendWarningMsg(ml_idGroup_s, EnumShowType.Show);
    }

    public void sendWarningMsg(String ml_idGroup_s, EnumShowType showType) {
        ml_idGroup_s = MultiLangUtil.getMessage(uuid, ml_idGroup_s);
        conversation.getToaHelper().sendTOA(showType, EnumMessageType.Warning, rtnCode, ml_idGroup_s);
    }
 
    /**
     * 傳送回應訊息
     *
     * @param message
     */
    public void sendRtnMsg(String ml_idGroup_s) {
        sendRtnMsg(ml_idGroup_s, EnumShowType.Show);
    }

    public void sendRtnMsg(String ml_idGroup_s, EnumShowType showType) {
        ml_idGroup_s = MultiLangUtil.getMessage(uuid, ml_idGroup_s);
        conversation.getToaHelper().sendTOA(showType, EnumMessageType.Ok, rtnCode, ml_idGroup_s);
    }

    /**
     * 回傳報表URL至Client端
     *
     * @param obj
     */
    public void sendRtnReport(Object obj) {
        ObjectToaIF toa = conversation.getToaHelper().createObjectToa();
        toa.Headers().setStr(EnumToaHeader.OutputType, EnumOutputType.Report.toString());
        toa.setBody(obj);
        conversation.getToaHelper().sendTOA(toa);
    }

    /**
     * 回傳物件至Client端
     *
     * @param obj
     */
    public void sendRtnObject(Object obj) {
        ObjectToaIF toa = conversation.getToaHelper().createObjectToa();
        toa.Headers().setStr(EnumToaHeader.OutputType, EnumOutputType.Screen.toString());
        toa.setBody(obj);
        conversation.getToaHelper().sendTOA(toa);
    }

// -------------------------- OTHER METHODS --------------------------

    /**
     * 解密字串
     *
     * @param encryptedData Hex形式的被加密的字串
     * @return 解密後的字串
     */
    public String decrypt(String encryptedData) throws JBranchException {
        try {
            return new String(CryptoUtil.getInstance().symmetricDecrypt(StringUtil.fromHex(encryptedData)));
        }
        catch (DecoderException e) {
            JBranchException je = new JBranchException(EnumErrInputType.MSG, "Hex Decode Exception", e);
            throw je;
        }
    }

    /**
     * 加密字串
     *
     * @param plainText 需要被加密的字串
     * @return 以Hex形式回傳被加密後的字串
     */
    public String encrypt(String plainText) throws JBranchException {
        return StringUtil.toHex(CryptoUtil.getInstance().symmetricEncrypt(plainText.getBytes()));
    }

    //取得公用變數
    public Object getCommonVariable(String key) throws JBranchException {
        return SysInfo.getInfoValue(key);
    }
    //取得使用者變數
    public Object getUserVariable(String key) throws JBranchException{
    	return DataManager.getUser(uuid).getBizlogicVO().getVar(key);
    }
    
    /**
     * 取得工作流程參數
     *
     * @param key
     * @return
     */
    public Object getWorkFlowParameter(String key) {
        if (key == null || key.trim().length() == 0) {
            return null;
        }
        return ThreadDataPool.getData(key);
    }

    public void notifyClientToDownloadFile(String fileUrl, String defaultFileName) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("fileUrl", fileUrl);
        params.put("defaultFileName", defaultFileName);
        this.sendRtnObject("downloadFile", params);
    }

    /**
     * 回傳物件至Client端
     *
     * @param clientMethod
     * @param obj
     */
    public void sendRtnObject(String clientMethod, Object obj) {
        ObjectToaIF toa = conversation.getToaHelper().createObjectToa();
        toa.Headers().setStr(EnumToaHeader.OutputType, EnumOutputType.NextProcess.toString());
        toa.Headers().setStr(EnumToaHeader.NextProc, clientMethod);
        toa.setBody(obj);
        conversation.getToaHelper().sendTOA(toa);
    }
    
		/**
		 * 傳送業務邏輯錯誤回應訊息，呼叫此訊息後，所有對資料庫所做的變更將被取消並結束交易
	
		 * @param message
		 */
		public void sendErrMsg_rollback(JBranchException e){
			sendErrMsg(e);
			throw new RuntimeException(BRANCH_ROLLBACK, e);
		}
	
    /**
     * 傳送錯誤訊息
     *
     * @param message
     */
		public void sendErrMsg(JBranchException e){
			sendErrMsg(e,EnumShowType.Show);
		}     
		public void sendErrMsg(JBranchException e,EnumShowType showType){
	
			String message = ErrHandleUtils.getMessage(e);
			conversation.getToaHelper().sendTOA(EnumShowType.Show,EnumMessageType.Error,rtnCode,message);
			
      if (logger.isErrorEnabled()) {
            logger.error(StringUtil.getStackTraceAsString(e));
      }			
			
		}
		public void sendErrMsg(String ml_idGroup_s){
			sendErrMsg(ml_idGroup_s,EnumShowType.Show);
		}
		public void sendErrMsg(String ml_idGroup_s,EnumShowType showType){
			ml_idGroup_s = MultiLangUtil.getMessage(uuid, ml_idGroup_s);
			conversation.getToaHelper().sendTOA(showType,EnumMessageType.Error,rtnCode,ml_idGroup_s);
      if (logger.isErrorEnabled()) {
            logger.error(ml_idGroup_s);
      }			
		}
    //存入公用變數
    public void setCommonVariable(String key, Object value) throws JBranchException {
        SysInfo.setInfoValue(key, value);
    }
    //存入使用者變數
    public void setUserVariable(String key,Object value) throws JBranchException{
    	DataManager.getUser(uuid).getBizlogicVO().setVar(key, value);
    }
    /**
     * 設定工作流程參數
     * key值不可為null、空白字串、空字串，否則會丟出JBranchException
     *
     * @param key
     * @param value
     * @throws JBranchException
     */
    public void setWorkFlowParameter(String key, Object value) throws JBranchException {
        if (key == null || key.trim().length() == 0) {
            throw new JBranchException(EnumErrInputType.MSG, "workflow parameter key error");
        }
        ThreadDataPool.setData(key, value);
    }

    /**
     * 各AP 須OverWrite的Method.
     *
     * @param name
     * @throws JBranchException
     * @throws InvocationTargetException
     */
    protected void executeMethods(String name) throws JBranchException, InvocationTargetException {
        try {
            IPrimitiveMap<EnumTiaHeader> header = conversation.getTiaHelper().getTia().Headers();
            Object body = null;

            if (conversation.getTiaHelper().getTia() instanceof MapTiaIF) {
                body = ((MapTiaIF) conversation.getTiaHelper().getTia()).Body();
            }
            else if (conversation.getTiaHelper().getTia() instanceof ObjectTiaIF) {
                body = ((ObjectTiaIF) conversation.getTiaHelper().getTia()).getBody();
            }

            Method method;
            
            if(body != null && body instanceof PagingInputVO){
            	doSetSort((PagingInputVO)body);
            }
            	
            try {
                method = getMethod(name, Object.class, IPrimitiveMap.class);
                method.invoke(this, body, header);
                return;
            }
            catch (NoSuchMethodException e) {
            }
            try {
                method = getMethod(name, Object.class);
                method.invoke(this, body);
                return;
            }
            catch (NoSuchMethodException e) {
            }

            method = getMethod(name);
            method.invoke(this);
        }
        catch (InvocationTargetException e) {
            throw e;
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            String message = String.format("執行交易時發生錯誤,模組代號:[%s] 功能代號:[%s]", this.getClass().getName(), name);
            throw new JBranchException(EnumErrInputType.MSG, message, e);
        }
        finally{
        	DataSortManager.setSortPool(null);
        }
    }

    private Method getMethod(String name, Class... paramsType) throws NoSuchMethodException {
        return this.getClass().getMethod(name, paramsType);
    }

    private void sendBranchErrMsg(JBranchException e) {
    	
    	
        String message = ErrHandleUtils.getMessage(e);
        sendSysErrMsg(message, e);
    	
    }

    /**
     * 傳送系統層面錯誤訊息
     *
     * @param message message
     * @param cause   cause
     */
    private void sendSysErrMsg(String message, Throwable cause) {
        conversation.getToaHelper().sendTOA(EnumShowType.Show, EnumMessageType.Error, rtnCode, message);

        logger.error(cause.getMessage(), cause);

        throw new RuntimeException(BRANCH_ROLLBACK, cause);
    }
    
    public void notifyClientAutoPrint(String[] fileUrl){
		Map<String,String[]> params=new HashMap<String,String[]>();
		params.put("docs", fileUrl);
		this.sendRtnObject("docAutoPrint",params);
	}
    
    public void doSetSort(PagingInputVO inVo){
    	if(StringUtils.isNotBlank(inVo.getColumn())){
    		Map<Type , Object> sortParam = new HashMap<Type , Object>();
    		sortParam.put(Type.COLUMN , inVo.getColumn());
    		sortParam.put(Type.ASC , inVo.isAsc());
        	DataSortManager.setSortPool(sortParam);
    	}
    }


// --------------------- GETTER / SETTER METHODS ---------------------

    private String getTxnCode() {
        String txnCode = null;
        try {
            txnCode = DataManager.getTransactionDefinition(uuid).getTxnCode();
        }
        catch (JBranchException e) {
            logger.error(e.getMessage(), e);
        }
        return txnCode;
    }

    protected Map getArgs() {
        return this.args;
    }

    /**
	 * @return the dataAccessManager
	 */
	public DataAccessManager getDataAccessManager() {
		return dataAccessManager;
	}

	/**
	 * @param dataAccessManager the dataAccessManager to set
	 */
	public void setDataAccessManager(DataAccessManager dataAccessManager) {
		this.dataAccessManager = dataAccessManager;
	}

	public DataManagerIF getDataManager() {
        return dataManager;
    }

    public void setDataManager(DataManagerIF dataManager) {
        this.dataManager = dataManager;
    }

    public DataAccessManager getDataAccessManager(String dbID) throws DAOException,JBranchException
    {  
    	return new DataAccessManager(dbID,uuid);
    }
    
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void exeUpdateForMap(String qeuryStr , Map param) throws DAOException, JBranchException{
		DataAccessManager dam = getDataAccessManager();
		dam.exeUpdate(initQueryConditionIF(dam , qeuryStr ,  param));
	}
	
	public void exeUpdateForQcf(QueryConditionIF qf) throws DAOException, JBranchException{
		getDataAccessManager().exeUpdate(qf);
	}
	
	public List exeQueryForMap(String qeuryStr , Map param) throws DAOException, JBranchException{
		DataAccessManager dam = getDataAccessManager();
		return dam.exeQuery(initQueryConditionIF(dam , qeuryStr ,  param));
	}
	
	public List exeQueryForQcf(QueryConditionIF qf) throws DAOException, JBranchException{
		return getDataAccessManager().exeQuery(qf);
	}
	
	public List exeQueryWithoutSortForMap(String qeuryStr , Map param) throws DAOException, JBranchException{
		DataAccessManager dam = getDataAccessManager();
		return dam.exeQueryWithoutSort(initQueryConditionIF(dam , qeuryStr ,  param));
	}
	
	public List exeQueryWithoutSortForQcf(QueryConditionIF qf) throws DAOException, JBranchException {
		DataAccessManager dam = getDataAccessManager();
		return dam.exeQueryWithoutSort(qf);
	}
	
	public QueryConditionIF genDefaultQueryConditionIF() throws DAOException, JBranchException {
		return getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
	}
	
	private QueryConditionIF initQueryConditionIF(DataAccessManager dam , String qeuryStr , Map<String , Object> param ) throws DAOException, JBranchException{
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		qc.setQueryString(qeuryStr);
		
		for(String key : param.keySet())
			qc.setObject(key , param.get(key));
		
		return qc;
	}
}