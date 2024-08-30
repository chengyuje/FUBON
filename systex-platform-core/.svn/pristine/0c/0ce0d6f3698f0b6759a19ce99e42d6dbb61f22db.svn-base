package com.systex.jbranch.platform.common.errHandle;

import com.systex.jbranch.platform.common.dataManager.UUID;
import com.systex.jbranch.platform.common.util.ThreadDataPool;

public class ErrHandleUtils {

	private ErrHandleUtils(){}
	
	public static String getMessage(Throwable e)
	{
		if(e.getMessage()!=null)
		{
			return e.getMessage(); 
		}
		else
		{
			return e.getClass().getName();
		}
	}
	
	public static String getMessage(JBranchException e)
	{
		UUID uuid=(UUID)ThreadDataPool.getData(ThreadDataPool.KEY_UUID);
		
		if(uuid!=null && e.getMessage(uuid)!=null)
		{
			return e.getMessage(uuid); 
		}
		else
		{
			return e.getClass().getName();
		}
	}
}
