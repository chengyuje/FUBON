package com.systex.jbranch.platform.server.info.branchcatalog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.systex.jbranch.platform.common.errHandle.JBranchException;

public class VoidCataloger extends AbstractBranchCataloger
		implements IBranchCataloger {

	public List<Map<String, String>> getBranchList() throws JBranchException
	{		
		return new ArrayList<Map<String, String>>();
	}

	public List<Map<String, String>> getBranchList(List<Map> list) throws JBranchException
	{
		return getBranchList();
	}
}
