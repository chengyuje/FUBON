package com.systex.jbranch.app.server.fps.crm233;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.crm230.CRM230;
import com.systex.jbranch.app.server.fps.crm230.CRM230OutputVO;
import com.systex.jbranch.app.server.fps.crm230.CRM230_ALLInputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.WorkStation;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * MENU
 * 
 * @author walalala
 * @date 2016/11/03
 * @spec null
 */
@Component("crm233")
@Scope("request")
public class CRM233 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(CRM233.class);

	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		
		WorkStation ws = DataManager.getWorkStation(uuid);
		CRM233InputVO inputVO = (CRM233InputVO) body;
		
		CRM230_ALLInputVO inputVO_all = new CRM230_ALLInputVO();
		inputVO_all.setCrm233inputVO(inputVO);
		inputVO_all.setCrm230inputVO(inputVO);
		inputVO_all.setAvailRegionList(getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
		inputVO_all.setAvailAreaList(getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
		inputVO_all.setAvailBranchList(getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		inputVO_all.setLoginEmpID(ws.getUser().getUserID());
		inputVO_all.setLoginRole(getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		
		CRM230 crm230 = (CRM230) PlatformContext.getBean("crm230");
		CRM230OutputVO outputVO_crm230 = new CRM230OutputVO();
		
		outputVO_crm230 = crm230.inquire_common(inputVO_all, "CRM233");

		this.sendRtnObject(outputVO_crm230);
	}
	
}