package com.systex.jbranch.platform.server.info.branchcatalog;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.UUID;
import com.systex.jbranch.platform.common.dataManager.User;
import com.systex.jbranch.platform.common.dataManager.WorkStation;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.ThreadDataPool;

public abstract class AbstractBranchCataloger implements IBranchCataloger 
{
	
	public Map<String, String> getBranchCatalog() throws JBranchException 
	{
		HashMap<String,String> map=new HashMap<String,String>();
		Iterator<Map<String,String>> i=this.getBranchList().iterator();
		while(i.hasNext())
		{
			Map<String,String> item=i.next();
			String data=item.get("data");
			String label=item.get("label");
			map.put(data, label);
		}
		return map;
	}
	
	protected UUID getUUID()
	{
		return (UUID)ThreadDataPool.getData(ThreadDataPool.KEY_UUID);
	}
	
	protected User getUser()
	{
		return DataManager.getUser(getUUID());
	}
	
	protected WorkStation getWorkStation()
	{
		return DataManager.getWorkStation(getUUID());
	}
}
