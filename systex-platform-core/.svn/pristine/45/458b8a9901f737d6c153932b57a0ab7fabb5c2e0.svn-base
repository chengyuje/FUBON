package com.systex.jbranch.platform.configuration;

import java.util.HashMap;
import java.util.Map;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.UUID;
import com.systex.jbranch.platform.common.dataManager.User;
import com.systex.jbranch.platform.common.dataManager.WorkStation;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.common.util.ThreadDataPool;

public class PlatformConfigUtils
{
	private PlatformConfigUtils(){}
	
	public static Map getConfigMap() throws ConfigurationException
	{
		IConfigBrowser configBrowser=null;
		try 
		{
			configBrowser=(IConfigBrowser)PlatformContext.getBean("config.configBrowser");
		}
		catch (JBranchException e) 
		{
			throw new ConfigurationException(e);
		}
		
		Map filter=new HashMap();
		User user=getUser();
		if(user!=null && user.getUserID()!=null)
		{
			filter.put("USERID", user.getUserID());
		}
		
		WorkStation ws=getWorkStation();
		if(ws!=null && ws.getWsIP()!=null)
		{
			filter.put("IP", ws.getWsIP());
		}
		
		return configBrowser.getConfigMap(filter);
	}
	
	private static User getUser()
	{
		return DataManager.getUser(getUUID());
	}
	private static WorkStation getWorkStation()
	{
		return DataManager.getWorkStation(getUUID());
	}
	private static UUID getUUID()
	{
		return (UUID)ThreadDataPool.getData(ThreadDataPool.KEY_UUID);
	}
}