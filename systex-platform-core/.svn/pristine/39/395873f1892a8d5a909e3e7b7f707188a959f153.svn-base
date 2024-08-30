package com.systex.jbranch.platform.server.pipeline.flex;

import com.systex.jbranch.platform.common.dataManager.UUID;
import com.systex.jbranch.platform.common.dataaccess.serialnumber.SerialNumberUtil;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.log.appender.JBranchDBAppender;
import com.systex.jbranch.platform.common.util.JsonUtil;
import com.systex.jbranch.platform.common.util.NullTK;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.common.util.ThreadDataPool;
import com.systex.jbranch.platform.server.conversation.ToaHelperIF;
import com.systex.jbranch.platform.server.conversation.ToaIF;
import com.systex.jbranch.platform.server.conversation.message.EnumToaHeader;
import com.systex.jbranch.platform.server.conversation.message.Tia;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.net.InetAddress;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.systex.jbranch.platform.server.conversation.message.EnumTiaHeader;

public class RemotingService {
// ------------------------------ FIELDS ------------------------------

	private static Map<String, AtomicInteger> requestIDMap = new ConcurrentHashMap<String, AtomicInteger>();
	private Pipeline pipeline;
	private Logger logger = LoggerFactory.getLogger(RemotingService.class);
    
    private long currentTimeMillis=System.currentTimeMillis();
// -------------------------- OTHER METHODS --------------------------

    
    public List invoke(Tia tia) throws Exception {
        try {
        	MDC.put(ToaHelperIF.AP_HOST_NAME, InetAddress.getLocalHost().getHostName());
        	
        	String ip=PipelineUtil.getRemoteAddr();
        	generMDC(tia, ip);
        	
//        	if(logger.isDebugEnabled()){
//	    		String headerJson = JsonUtil.toJson(tia.getHeaders());
//	    		String bodyJson = JsonUtil.toJson(tia.getBody());
//	    		logger.debug("{\"header\":" + headerJson + ", \r\n\"body\":" + bodyJson + "\r\n}");
//	    	}
        	
        	UUIDGeneratorIF uuidGenerator = PlatformContext.getBean(UUIDGeneratorIF.class);
            final UUID uuid = uuidGenerator.generateUUID(tia);
            PipelineUtil.setUUID(uuid);
            
            if (logger.isDebugEnabled()){
        		String txnCode=tia.Headers().getStr(EnumTiaHeader.TxnCode);
        		String bizCode=tia.Headers().getStr(EnumTiaHeader.BizCode);
        		diff("server in %s %s %s %s",ip,uuid.getWsId(),
        				txnCode,bizCode);
            }

            final List toaList = new ArrayList();

            final ToaHelperIF toaHelper = new ToaHelper() {
                public void sendTOA(ToaIF toa, boolean endBracket) {
                    toa.Headers().setBool(EnumToaHeader.EndBracket, endBracket);
                    toaList.add(toa);
                }
            };

            setPipeline(PlatformContext.getBean(Pipeline.class));

            pipeline.invoke(tia, toaHelper, uuid, null);

            if (logger.isDebugEnabled()){
        		String txnCode=tia.Headers().getStr(EnumTiaHeader.TxnCode);
        		String bizCode=tia.Headers().getStr(EnumTiaHeader.BizCode);
        		diff("server out %s %s %s %s",ip,uuid.getWsId(),
        				txnCode,bizCode);
            }
            
//            if (logger.isDebugEnabled()) {
//            	int len = toaList.size();
//            	StringBuffer sb = new StringBuffer();
//            	sb.append("\r\n[");
//            	for(int i = 0; i < len; i++){
//            		String headerJson = JsonUtil.toJson(((com.systex.jbranch.platform.server.conversation.message.Toa) toaList.get(i)).getHeaders());
//            		String bodyJson = JsonUtil.toJson(((com.systex.jbranch.platform.server.conversation.message.Toa) toaList.get(i)).getBody());
//            		sb.append("\r\n{\"header\":"+headerJson+", \"body\":"+bodyJson+"}");
//            		if(i < len - 1){
//            			sb.append(",");
//            		}
//            	}
//            	sb.append("\r\n]");
//            	logger.debug(sb.toString());
//			}
            return toaList;
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }finally{
			Map<String, Object> params = (Map<String, Object>) ThreadDataPool.getData(JBranchDBAppender.JBRANCH_DB_APPENDER_PARAMS);
			if(params != null){
				params.clear();				
			}

			MDC.clear();
        }
    }

	private void generMDC(Tia tia, String key) {
		
		AtomicInteger aInt = requestIDMap.get(key);
		if(aInt == null){
			aInt = new AtomicInteger(0);
			requestIDMap.put(key, aInt);
		}
		int requestID = aInt.addAndGet(1);
		
		String branchID = tia.Headers().getStr(EnumTiaHeader.BranchID);
		String wsID = tia.Headers().getStr(EnumTiaHeader.WsID);
		String TlrID = tia.Headers().getStr(EnumTiaHeader.TlrID);
		String txnCode = tia.Headers().getStr(EnumTiaHeader.TxnCode);
		
		MDC.put(CommonConstantHelper.BRANCH_ID, branchID);
		if("".equals(wsID) == false){
			MDC.put(CommonConstantHelper.WS_ID, wsID);			
		}
		MDC.put(CommonConstantHelper.TLR_ID, TlrID);
		MDC.put(CommonConstantHelper.IP, key);
		MDC.put(CommonConstantHelper.REQUEST_ID, key + "_" + String.valueOf(requestID));
		MDC.put(CommonConstantHelper.TXN_CODE, txnCode);
		
		//for fubon poc臨時作法
//		try {
//			String actionId = getActionID();
//			MDC.put(CommonConstantHelper.ACTION_ID, actionId);
//		} catch (JBranchException e) {
//			logger.error(e.getMessage(), e);
//		}
	}

	private String getActionID() throws JBranchException {
		
		SerialNumberUtil serialNO =  new SerialNumberUtil();
		String snid = "ACTION_ID";
		String actionId = null;
		try{
			actionId = serialNO.getNextSerialNumber(snid);
		}catch(Exception e){
			//ignore
		}
		if(actionId == null){
			serialNO.createNewSerial(snid, "0", (Integer) null, (String) null, (Timestamp) null, new Integer(1), new Long(Long.MAX_VALUE), "Y", new Long(1), "SYSTEM");
			actionId = "1";
		}
		return actionId;
	}

// --------------------- GETTER / SETTER METHODS ---------------------

    public void setPipeline(Pipeline pipeline) {
        this.pipeline = pipeline;
    }
    private void diff(String format,Object... args)
	{
		try
		{
			long time=System.currentTimeMillis();
			long diff=time-currentTimeMillis;
			currentTimeMillis=time;			
			String value=String.format(format, args);
			logger.debug(String.format("%d %s",diff,value));
		}
		catch(Exception e){}
	}
}