package com.systex.jbranch.app.server.fps.prd100;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.sot630.SOT630;
import com.systex.jbranch.app.server.fps.sot630.SOT630InputVO;
import com.systex.jbranch.fubon.commons.CheckBossAdmin;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * prd100
 */
@Component("prd100")
@Scope("request")
public class PRD100 extends FubonWmsBizLogic {
//	private DataAccessManager dam = null;
//	private Logger logger = LoggerFactory.getLogger(PRD100.class);

	/***
	 * 檢核覆核主管是否正確
	 * (1)發送LDAP驗證主管帳密
	 * (2)檢核轄下關係
	 * return： "Y":正確 "N":有誤
	 * @param body
	 * @param header
	 * @throws JBranchException
	 * @throws Exception
	 */
	public void checkBossAdmin(Object body, IPrimitiveMap header) throws JBranchException, Exception {
		PRD100InputVO inputVO = (PRD100InputVO) body;
		PRD100OutputVO outputVO = new PRD100OutputVO();
		SimpleDateFormat ft = new SimpleDateFormat("yyyy/MM/dd");
		
		String loginEmpID = (String) getUserVariable(FubonSystemVariableConsts.LOGINID);
		// (1)發送LDAP驗證主管帳密
		// (2)檢核轄下關係
		CheckBossAdmin cba = (CheckBossAdmin) PlatformContext.getBean("checkBossAdmin");
		Boolean checkResult = cba.checkBossAdmin(loginEmpID, inputVO.getBossEmpID(), inputVO.getBossEmpPWD());
		outputVO.setValidBossAdmin(checkResult ? "Y" : "N");
		
		if(checkResult) {
			//檢核通過，寫入主管確認紀錄檔
			SOT630InputVO inputVO630 = new SOT630InputVO();
			inputVO630.setCUST_ID(inputVO.getCustID());
			inputVO630.setCUST_NAME(getCustName(inputVO.getCustID()));
			inputVO630.setAUTH_TYPE(inputVO.getSeniorAuthType());
			inputVO630.setPROD_TYPE(inputVO.getProdType());
			inputVO630.setTRADE_TYPE(inputVO.getTradeType());
			inputVO630.setEVALUATE_VALID_DATE(ft.parse(inputVO.getMatchDate()));
			inputVO630.setABILITY_RESULT(inputVO.getInvalidMsgC());
			inputVO630.setFINACIAL_COGNITION_RESULT(inputVO.getInvalidMsgF());
			inputVO630.setTRUST_TRADE_TYPE(inputVO.getTrustTS());
			inputVO630.setAUTH_BRANCH_NBR((String) getUserVariable(FubonSystemVariableConsts.LOGINBRH));
			inputVO630.setAUTH_DIRECTOR_EMP_ID(inputVO.getBossEmpID());
			inputVO630.setAUTH_DATE(new Date());
			
			SOT630 sot630 = (SOT630) PlatformContext.getBean("sot630");
			sot630.save(inputVO630);
		}
		
		sendRtnObject(outputVO);
	}
	
	//取得客戶姓名
	private String getCustName(String custId) throws DAOException, JBranchException {
		DataAccessManager dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("select CUST_NAME from TBCRM_CUST_MAST where CUST_ID = :custId ");
		queryCondition.setObject("custId", custId);
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		return CollectionUtils.isNotEmpty(list) ? ObjectUtils.toString(list.get(0).get("CUST_NAME")) : "";
	}
}
