package com.systex.jbranch.platform.server.bizLogic;

import com.systex.jbranch.platform.common.errHandle.JBranchException;
import java.util.Map;

public interface IServiceProvider {

	/**
	 * 
	 * @param value 
	 * @param params 與平台相關的參數
	 * @return
	 * @throws JBranchException
	 */
	public Object invoke(Object value,Map params) throws JBranchException;
}