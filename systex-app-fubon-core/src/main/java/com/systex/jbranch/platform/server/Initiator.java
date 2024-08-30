package com.systex.jbranch.platform.server;

import org.springframework.stereotype.Repository;

import com.systex.jbranch.platform.common.initiation.InitiatorIF;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;

@Repository("initiator")
public class Initiator extends BizLogic implements InitiatorIF{

	@Override
	public void execute() throws Exception {
		// TODO Auto-generated method stub
		
	}
	
}
