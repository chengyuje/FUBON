package com.systex.jbranch.platform.common.security.privilege;

import java.util.Map;

import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.security.privilege.vo.ItemDTO;

public interface ItemMapFilterIF
{
	void filter(Map<String, ItemDTO> itemMap) throws JBranchException;
}