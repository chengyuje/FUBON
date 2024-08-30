package com.systex.jbranch.platform.common.dataManager;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.errHandle.JBranchException;

public interface BranchFactoryIF {

	public static String DEFAULT_BRANCH_FACTORY = "def_branch_factory";
	
	public List<Branch> getBranchList() throws JBranchException;
	
	public List<Map<String, String>> getBranchLabelList() throws JBranchException;
	
	public Branch getBranch(String branchId) throws JBranchException;
}
