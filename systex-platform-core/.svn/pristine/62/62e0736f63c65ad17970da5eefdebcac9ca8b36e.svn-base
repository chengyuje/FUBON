package com.systex.jbranch.platform.common.security.privilege;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.security.privilege.vo.ItemDTO;

public class BatchItemMapFilter implements ItemMapFilterIF
{
	private List<ItemMapFilterIF> filterlist=new ArrayList<ItemMapFilterIF>();
	
	public BatchItemMapFilter(ItemMapFilterIF...filters)
	{
		this(Arrays.asList(filters));
	}

	public BatchItemMapFilter(List<ItemMapFilterIF> filterlist)
	{
		this.filterlist.addAll(filterlist);
	}
	
	public void filter(Map<String, ItemDTO> itemMap)throws JBranchException
	{
		if(itemMap!=null)
		{
			for(int i=0;i<filterlist.size();i++)
			{
				filterlist.get(i).filter(itemMap);
			}
		}
	}
}