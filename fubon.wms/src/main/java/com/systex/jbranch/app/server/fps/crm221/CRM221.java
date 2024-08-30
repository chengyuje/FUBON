package com.systex.jbranch.app.server.fps.crm221;


import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.crm210.CRM210;
import com.systex.jbranch.app.server.fps.crm210.CRM210OutputVO;
import com.systex.jbranch.app.server.fps.crm210.CRM210_ALLInputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.WorkStation;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * MENU
 * 
 * @author walalala
 * @date 2016/06/15
 * @spec null
 */
@Component("crm221")
@Scope("request")
public class CRM221 extends FubonWmsBizLogic {

	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		WorkStation ws = DataManager.getWorkStation(uuid);
		CRM221InputVO inputVO = (CRM221InputVO) body;
		
		CRM210_ALLInputVO inputVO_all = new CRM210_ALLInputVO();
		inputVO_all.setCrm221inputVO(inputVO);
		inputVO_all.setCrm210inputVO(inputVO);
		inputVO_all.setAvailRegionList(getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
		inputVO_all.setAvailAreaList(getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
		inputVO_all.setAvailBranchList(getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		inputVO_all.setLoginEmpID(ws.getUser().getUserID());
		inputVO_all.setLoginRole(getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		
		CRM210 crm210 = (CRM210) PlatformContext.getBean("crm210");
		CRM210OutputVO outputVO_crm210 = new CRM210OutputVO();
		
		String loginToken = "";
		Object loginAoCode = null;
		
		outputVO_crm210 = crm210.inquire_common(inputVO_all, "CRM221", loginToken, loginAoCode);

		this.sendRtnObject(outputVO_crm210);
	}

	
}