package com.systex.jbranch.comutil.io;

import java.util.List;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataManager.UUID;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;

public class JoinDifferentSysBizLogic extends FubonWmsBizLogic{
	public String getLoginBranch() throws JBranchException{
		return (String) getUserVariable(SystemVariableConsts.LOGINBRH);
	}
	
	public List<String> getLoginAoCode(){
		try {
			return (List<String>)getUserVariable(FubonSystemVariableConsts.LOGIN_AOCODE_LIST);
		} catch (JBranchException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void setUuid(UUID uuid){
		this.uuid = uuid;
	}
	
	
}
