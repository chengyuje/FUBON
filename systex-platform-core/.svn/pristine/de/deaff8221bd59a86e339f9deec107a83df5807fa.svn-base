package com.systex.jbranch.platform.server.pipeline.flex;

import javax.servlet.http.HttpServletRequest;

import com.systex.jbranch.platform.common.dataManager.UUID;
import com.systex.jbranch.platform.common.util.ThreadDataPool;
import com.systex.jbranch.platform.server.conversation.TiaIF;
import com.systex.jbranch.platform.server.conversation.message.EnumTiaHeader;

// import flex.messaging.FlexContext;

public class PipelineUtil {

	final public static String HTTP_SERVLET_REQUEST = "HttpServletRequest";
	final public static String HTTP_SERVLET_RESPONSE = "HttpServletResponse";

	private PipelineUtil()
	{}
	
    public static  String getTellerId(TiaIF tia){
    	String tlrId;
		try	{
			tlrId=tia.Headers().getStr(EnumTiaHeader.TlrID);
		}
		catch(Exception e){
			tlrId="";
		}
    	return tlrId;
    }
	
	public static UUID getUUID()
	{
		return (UUID)ThreadDataPool.getData(ThreadDataPool.KEY_UUID);
	}
	
	public static void setUUID(UUID uuid)
	{
		ThreadDataPool.setData(ThreadDataPool.KEY_UUID, uuid);
	}	
	
	public static String getRemoteAddr()
	{
		
		HttpServletRequest request= (HttpServletRequest) ThreadDataPool.getData(HTTP_SERVLET_REQUEST);
		if (request == null) {
			request = (HttpServletRequest) ThreadDataPool.getData("HttpServletRequest");
		}
		String ip = request.getHeader("x-forwarded-for");

		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) 
		{ 
			ip = request.getHeader("Proxy-Client-IP");
		} 
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
		{ 
			ip = request.getHeader("WL-Proxy-Client-IP"); 
		} 
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) 
		{ 
			ip = request.getRemoteAddr(); 
		}

		int endIndex=ip.indexOf(",");
		if(endIndex>=0)
			ip=ip.substring(0, endIndex).trim();
		
		return ip;
	}
}
