package com.systex.jbranch.platform.common.datasource;

import com.systex.jbranch.platform.common.dataManager.UUID;
import com.systex.jbranch.platform.common.dataaccess.datasource.DataSourceRuleIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;

public class DefaultRuleImpl implements DataSourceRuleIF{
	
	private static final String DEFAULT_SESSION_FACTORY = "defaultSessionFactory";
	public static final String DB_ID_NOT_FOUND = "pf_defaultrule_error_001";
	
	public String getDataSource(String dbID, UUID uuid) throws JBranchException {
		if(dbID != null && dbID.equalsIgnoreCase("pps")){
			return "ds_pps";
		}else if(dbID != null && dbID.equalsIgnoreCase("iweb")){
			return "ds_iweb";
		}else{
			throw new JBranchException(DB_ID_NOT_FOUND);
		}
	}

	public String getDefaultDBDataSource(UUID uuid) throws JBranchException {
		return DEFAULT_SESSION_FACTORY;
	}

	public String getDefaultDataSource() throws JBranchException {
		return DEFAULT_SESSION_FACTORY;
	}
}
