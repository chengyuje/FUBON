package com.systex.jbranch.ws.external.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.workflow.Workflow;
import com.opensymphony.workflow.loader.ActionDescriptor;
import com.opensymphony.workflow.loader.WorkflowDescriptor;
import com.systex.jbranch.platform.common.dataManager.Branch;
import com.systex.jbranch.platform.common.dataManager.BranchFactoryIF;
import com.systex.jbranch.platform.common.dataManager.ClientTransaction;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.DataManagerIF;
import com.systex.jbranch.platform.common.dataManager.PlatFormVO;
import com.systex.jbranch.platform.common.dataManager.PlatformVOHelper;
import com.systex.jbranch.platform.common.dataManager.Section;
import com.systex.jbranch.platform.common.dataManager.ServerTransaction;
import com.systex.jbranch.platform.common.dataManager.TransactionDefinition;
import com.systex.jbranch.platform.common.dataManager.UUID;
import com.systex.jbranch.platform.common.dataManager.User;
import com.systex.jbranch.platform.common.dataManager.WorkStation;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.errHandle.SessionException;
import com.systex.jbranch.platform.common.errHandle.UnLoginException;
import com.systex.jbranch.platform.common.errHandle.WSTimeOutException;
import com.systex.jbranch.platform.common.util.NullTK;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.common.util.ThreadDataPool;
import com.systex.jbranch.platform.server.PlatformStartupService;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.server.conversation.ConversationIF;
import com.systex.jbranch.platform.server.conversation.ObjectToaIF;
import com.systex.jbranch.platform.server.conversation.TiaHelperIF;
import com.systex.jbranch.platform.server.conversation.TiaIF;
import com.systex.jbranch.platform.server.conversation.ToaHelperIF;
import com.systex.jbranch.platform.server.conversation.message.EnumMessageType;
import com.systex.jbranch.platform.server.conversation.message.EnumOutputType;
import com.systex.jbranch.platform.server.conversation.message.EnumShowType;
import com.systex.jbranch.platform.server.conversation.message.EnumTiaHeader;
import com.systex.jbranch.platform.server.conversation.message.EnumToaHeader;
import com.systex.jbranch.platform.server.conversation.message.payload.EndBracket;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.pipeline.flex.Pipeline;
import com.systex.jbranch.platform.server.pipeline.flex.PipelineUtil;
import com.systex.jbranch.platform.server.sessionmanager.APServerManager;
import com.systex.jbranch.platform.server.sessionmanager.WSManagerIF;
import com.systex.jbranch.platform.util.IPrimitiveMap;

public class WSPipelineImpl {
// ------------------------------ FIELDS ------------------------------

    private static final String LOGIN_STATUS_CHECK_IGNORE = "loginStatusCheckIgnore";
    //private static final String apSvrName=APServerManager.getApSvrName();
    public static final String WSKEEPER_LOG = "WSKeeper_LOG";
    private static final String BEAN_WORKFLOW = "workflow";
    private static final String BEAN_WORKFLOW_CONFIGURATION = "osworkflowConfiguration";
    private static final String BEAN_WORKFLOW_FACTORY = "xmlWorkflowFactory";
    private static final String FILE_SEPARATOR = System.getProperties().getProperty("file.separator");
    private static final String TLRON_BIZCODE = "tlronFPS";                //櫃員簽到
    private static final String TLRON_OUTER_BIZCODE = "tlronSSO";        //櫃員簽到(透過外部系統)

    private static final String TLRON_TXNCODE = "CMFPG000";                //櫃員簽到
    private static final String WSON_BIZCODE = "wson";                //工作站簽到

    private static final String WSON_TXNCODE = "CMFPG000";                    //工作站簽到
    
    
    private APServerManager apServerManager;
    
    private WSManagerIF wsManager;
    
    private DataManagerIF dataManager;
    
    private Map<String, Set> entryPoints;
    
    
//    <bean id="pipeline" class="com.systex.jbranch.platform.server.pipeline.flex.PipelineImpl">
//    <property name="wsManager" ref="wsManager"/>
//    <property name="apServerManager" ref="apServerManager"/>
//    <property name="dataManager" ref="dataManager"/>
//    <property name="entryPoints">
//        <util:map>
//            <!-- txnCode -->
//            <entry key="CMFPG000">
//                <!-- bizCode -->
//                <util:set>
//                    <value>authenticate</value>
//                    <value>getBranchList</value>
//                    <value>wson</value>
//                    <value>tlronFPS</value>
//                    <value>tlronSSO</value>
//                    <value>tlronBTS</value>
//                    <value>tlron</value>
//                </util:set>
//            </entry>
//        </util:map>
//    </property>
//</bean>

	private Logger logger = LoggerFactory.getLogger(WSPipelineImpl.class);
	
	@Autowired
	private Pipeline Pipeline;

// --------------------------- CONSTRUCTORS ---------------------------

    public WSPipelineImpl() {
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface Pipeline ---------------------

    public void invoke(final TiaIF tia, final ToaHelperIF toaHelper, final UUID uuid, Map workflowParams) {
        EndBracket endBracket = EndBracket.DEFAULT_ERROR;
        if (workflowParams == null) {
            workflowParams = new HashMap();
        }

        try {
            if (PlatformStartupService.getServiceStatus() == 1) {
                throw new RuntimeException("親愛的用戶，因系統尚未Ready,請稍後再上線!");
            }

            if (checkLoginState(tia, uuid) == false) {
                throw new UnLoginException("pf_sessionmanager_error_007_00_001");
            }
                
            final TiaHelperIF tiaHelper = new TiaHelperIF() {
                public TiaIF getTia() {
                    return tia;
                }
            };

            ConversationIF conversation = new ConversationIF() {
                public TiaHelperIF getTiaHelper() {
                    return tiaHelper;
                }

                public ToaHelperIF getToaHelper() {
                    return toaHelper;
                }

                public UUID getUUID() {
                    return uuid;
                }
            };

            wson(conversation);
       
            workflowParams.put(BizLogic.VAR_CONVERSATION, conversation);
            workflowParams.put(BizLogic.VAR_UUID, uuid);
            endBracket = action(conversation, workflowParams, tia);
        }catch (UnLoginException err) {
            logger.error(err.getMessage(), err);
            toaHelper.sendTOA(EnumShowType.Popup, EnumMessageType.ForceLogOut, "0001", err.getMessage(PipelineUtil.getUUID()));
        }
        catch (WSTimeOutException err) {
            logger.error(err.getMessage(), err);
            toaHelper.sendTOA(EnumShowType.Popup, EnumMessageType.ForceLogOut, "0001", err.getMessage(PipelineUtil.getUUID()));
            if (logger.isInfoEnabled()) {
                logger.info("WSTimeOutException");
            }
        }
        catch (JBranchException err) {
            logger.error(err.getMessage(), err);
            UUID uid = PipelineUtil.getUUID();
            
            if (uid != null && err.getMessage(uid) != null) {
                toaHelper.sendTOA(EnumShowType.Show, EnumMessageType.Error, "Z999", err.getMessage(uid));
            }
            else {
                toaHelper.sendTOA(EnumShowType.Show, EnumMessageType.Error, "Z999", err.getClass().toString());
            }
        }
        catch (Exception err) {
            logger.error(err.getMessage(), err);
            if (err.getMessage() == null) {
                toaHelper.sendTOA(EnumShowType.Show, EnumMessageType.Error, "Z999", err.getClass().toString());
            }
            else {
                toaHelper.sendTOA(EnumShowType.Show, EnumMessageType.Error, "Z999", err.getMessage());
            }
        }
        finally {
            boolean stampTime = true;
            if (tia.Headers().containsKey(EnumTiaHeader.StampTime.toString())
                    && !tia.Headers().getBool(EnumTiaHeader.StampTime)) {
                stampTime = false;
            }
            if (logger.isDebugEnabled()) {
                logger.debug("stampTime = " + String.format("stampTime=%s.", stampTime));
            }

            if (stampTime) {
                addWsTouchedTime(PipelineUtil.getUUID());
            }


            ObjectToaIF toa = toaHelper.createObjectToa();
            toa.Headers().setStr(EnumToaHeader.OutputType, EnumOutputType.EndBracket.toString());
            
            //20120529 Angus append
            toa.Headers().setStr(EnumToaHeader.AP_HOST_NAME, NullTK.checkNull(MDC.get(ToaHelperIF.AP_HOST_NAME), ""));
            toa.Headers().setStr(EnumToaHeader.GW_HOST_NAME, NullTK.checkNull(MDC.get(ToaHelperIF.GW_HOST_NAME), ""));
            //20120824 Angus append
            Map totaTxnData = (Map) NullTK.checkNull(ThreadDataPool.getData(ToaHelperIF.TXN_DATA), Collections.EMPTY_MAP);
            toa.Headers().setMap(EnumToaHeader.TXN_DATA, totaTxnData);
            
            toa.setBody(endBracket);

            toaHelper.sendTOA(toa);
        	logger.debug("txnCode:{},totaTxnData={}",tia.Headers().getStr(EnumTiaHeader.TxnCode), totaTxnData);
            ThreadDataPool.setData(ToaHelperIF.TXN_DATA, new HashMap());//clear TXN_DATA
            try {
                UUID uid = PipelineUtil.getUUID();
                DataManager.deleteSection(uid);
            }
            catch (Throwable e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private boolean checkLoginState(TiaIF tia, UUID uuid) {
        
        String txnCode=tia.Headers().getStr(EnumTiaHeader.TxnCode);
        String bizCode=tia.Headers().getStr(EnumTiaHeader.BizCode);
        Set<String> loginStatusCheckIgnoreSet = null;
        try {
            loginStatusCheckIgnoreSet = PlatformContext.getBean(LOGIN_STATUS_CHECK_IGNORE, Set.class);
            Iterator<String> it = loginStatusCheckIgnoreSet.iterator();
            while (it.hasNext()) {
				String ignoreString = it.next();
				if(ignoreString.contains(".")){
					String[] express = ignoreString.split("\\.", 2);
					String ignoreTxnCode = express[0];
					String ignoreBizCode = express[1];
					
					if(ignoreTxnCode.equals(txnCode) && ignoreBizCode.equals(bizCode)){
						return true;
					}
				}
				if(ignoreString.equals(txnCode)){
					return true;
				}
			}
            
        } catch (JBranchException e) {
            //未設定登入忽略清單或其它ERROR，全部忽略
            return true;
        }
        
        if (uuid == null) {
            return false;
        }
        User user = DataManager.getUser(uuid);
        if (user == null) {
            return false;
        }
        try {
            Boolean isLogin = (Boolean) user.getBizlogicVO().getVar(SystemVariableConsts.LOGIN_FLAG);
            return isLogin != null && isLogin;
        } catch (Exception e) {
            //取登入資訊時發生exception，即判定為無登入資訊
            logger.error("取登入資訊時發生exception:" +e.getMessage(), e);
            return false;
        }
    }
    
	private EndBracket action(ConversationIF conversation, Map workflowParams, TiaIF tia) throws JBranchException {
        UUID uuid = conversation.getUUID();

        EndBracket endBracket = EndBracket.DEFAULT_ERROR;

        TransactionDefinition td = DataManager.getTransactionDefinition(uuid);
        ServerTransaction st = DataManager.getServerTransaction(uuid);

        int initActionID;
        String initActionName = null;
        long workflowId;

        try {
            Workflow webWf = (Workflow) PlatformContext.getBean(BEAN_WORKFLOW);
            initActionID = 100;
            String workflowName = (new StringBuilder()).append(st.getTxnCode()).append(".").append(st.getBizCode()).toString();
            workflowId = webWf.initialize(workflowName, initActionID, workflowParams);

            //========取得init action name========
            try {
                WorkflowDescriptor wfDescriptor = webWf.getWorkflowDescriptor(webWf.getWorkflowName(workflowId));
                ActionDescriptor actionDescriptor = wfDescriptor.getAction(initActionID);
                initActionName = actionDescriptor.getName();
            }
            catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            IPrimitiveMap header = tia.Headers();
            PlatFormVO platformVO = DataManager.getServerTransaction(uuid).getPlatFormVO();
            platformVO.setVar(PlatformVOHelper._BIZCODENAME, initActionName);
            platformVO.setVar(PlatformVOHelper.TRMSEQ, header.getStr(EnumTiaHeader.TRMSEQ));
            platformVO.setVar(PlatformVOHelper.REQUEST_ID, header.getStr(EnumTiaHeader.REQUEST_ID));
            platformVO.setVar(PlatformVOHelper._SUPVID, header.getStr(EnumTiaHeader.SupvID));
            
//            mdcInfo(tia);
            
            //執行
            webWf.doAction(workflowId, 1, workflowParams);

            endBracket = EndBracket.SUCCESS;
        }
        catch (RuntimeException e) {
            if (BizLogic.BRANCH_ROLLBACK.equals(e.getMessage())) {
                // 此訊息是為了Rollback 資料庫而產生,不傳回Flex 前端
            }
            else if (BizLogic.BRANCH_SUPVCONFIRM.equals(e.getMessage())) {
            	//主管授權視為交易執行成功
                endBracket = EndBracket.SUCCESS;
            }
            else {
                logger.error(e.getMessage(), e);
                String logMsg = td.getTxnCode() + "." + st.getBizCode() + "的交易，執行workflow時，產生Exception!!" + e.getMessage();
                conversation.getToaHelper().sendTOA(EnumShowType.Popup, EnumMessageType.Error, "Z999", logMsg);
            }
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            String logMsg = td.getTxnCode() + "." + st.getBizCode() + "的交易，執行workflow時，產生Exception!!" + e.getMessage();
            conversation.getToaHelper().sendTOA(EnumShowType.Popup, EnumMessageType.Error, "Z999", logMsg);
        }
        return endBracket;
    }
	
    private void addWsTouchedTime(UUID uuid) {
        try {
            WorkStation ws = DataManager.getWorkStation(uuid);
            if(ws != null){
            	ws.setTouchedTime(System.currentTimeMillis());
            }
        } catch (Throwable e) {//wsoff已deleteWorkStation
            logger.error(e.getMessage(), e);
        }
    }

    private void wson(ConversationIF conversation) throws JBranchException {

        UUID uuid = conversation.getUUID();
        TiaIF tia = conversation.getTiaHelper().getTia();
        String bizCode = tia.Headers().getStr(EnumTiaHeader.BizCode);
        boolean changeTxn = tia.Headers().getBool(EnumTiaHeader.ClientTransaction);
        String txnCode = tia.Headers().getStr(EnumTiaHeader.TxnCode);
        String macAddress = tia.Headers().getStr(EnumTiaHeader.MACAddress);
        WorkStation ws;
        
        if (dataManager.getBranch(uuid) == null) {
        	
        	BranchFactoryIF branchFactory = (BranchFactoryIF) PlatformContext.getBean(BranchFactoryIF.DEFAULT_BRANCH_FACTORY);
        	Branch branch = branchFactory.getBranch(uuid.getBranchID());
        	DataManager.setBranch(uuid, branch);
            
        }

        String applicationID = tia.Headers().getStr(EnumTiaHeader.ApplicationID);

        //new dataManager.WorkStation
        if (entryPoints.containsKey(txnCode) && (entryPoints.get(txnCode)).contains(bizCode)) {
            try {
                dataManager.deleteWorkStation(uuid);    //刪除之前登入資訊
            }
            catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            ws = new WorkStation();
            ws.setBrchID(uuid.getBranchID());
            ws.setWsID(uuid.getWsId());
            ws.setWsIP(PipelineUtil.getRemoteAddr());
            ws.setApplicationID(applicationID);
            ws.setMacAddress(macAddress);
            dataManager.setWorkStation(uuid, ws);
            if (logger.isDebugEnabled()) {
                logger.debug("txnCode=" + txnCode + ",bizCode=" + bizCode + ", setWorkStation");
            }
        }
        else {
        	try{
        	    wsManager.whenTransaction(uuid.getWsId(), uuid.getTellerID(), apServerManager.getApSvrName(), uuid.getBranchID());
    	   }catch (SessionException sex){
    	    throw sex;
    	   }catch (Exception ex){
    	    logger.error(String.format("使用者狀態管理錯誤:%s", ex.getMessage()));
    	    conversation.getToaHelper().sendTOA(EnumShowType.Popup, EnumMessageType.Warning, "Z999", "使用者狀態管理錯誤");
    	   }
        }

        if (dataManager.getSection(uuid) == null) {
            Section section = new Section();
            section.setSectionID(uuid.getSectionID());
            dataManager.setSection(uuid, section);
        }
        
        Section section = dataManager.getSection(uuid);
        if(section == null){
        	throw new JBranchException("資訊已不存在，請重新登入");
        }

        //每個 txn 都有獨立的 serverTransaction
        section.setServerTransaction(null);

        dataManager.getServerTransaction(uuid).setBizCode(bizCode);
        dataManager.getServerTransaction(uuid).setTxnCode(txnCode);    //2009-11-18 add

        dataManager.getConversationVO(uuid).setTia(tia);

        if (changeTxn) {
            changeClientTransaction(conversation, txnCode);
        }
    }

    /**
     * 更替ClientTransaction<br>
     *
     * @param uuid
     * @param txnId
     */
    private void changeClientTransaction(ConversationIF conversation, String txnId) throws JBranchException {
        UUID uuid = conversation.getUUID();

        ClientTransaction ct = new ClientTransaction();

        dataManager.setClientTransaction(uuid, ct);
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public void setApServerManager(APServerManager apServerManager) {
        this.apServerManager = apServerManager;
    }

    public void setDataManager(DataManagerIF dataManager) {
        this.dataManager = dataManager;
    }

    public void setEntryPoints(Map<String, Set> entryPoints) {
        this.entryPoints = entryPoints;
    }

    public void setWsManager(WSManagerIF wsManager) {
        this.wsManager = wsManager;
    }
}
