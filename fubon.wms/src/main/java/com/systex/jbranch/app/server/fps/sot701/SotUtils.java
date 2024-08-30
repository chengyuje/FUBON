package com.systex.jbranch.app.server.fps.sot701;

import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;

/**
 * sotUtils
 * 
 * @author Jeff
 * @date 2024/06/03
 * @spec
 */
@Component("sotUtils")
@Scope("request")
public class SotUtils extends FubonWmsBizLogic {
	
	private DataAccessManager dam = null;
	
	/**
	 * #2030
	 * 金錢信託下單時，員編是250分行上送電文會帶000分行，
	 * 錯誤訊息:E122受理分行不可為空白。
	 * 修改:
	 * 寫入TBSOT_TRADE_MAIN前
	 * 判斷BRANCH_NBR是否是250分行
	 * 是 => 取TBORG_MEMBER.DEPT_ID
	 * 否 => getCommonVariable(SystemVariableConsts.LOGINBRH)
	 * 目前只開放BRANCH_NBR = 250
	 */
	public String getBranchNbr(String trustTS, String sysLoginBrh) throws Exception {
		String branchNbr = null;
		if ("M".equals(trustTS)) {
			dam = this.getDataAccessManager();
			QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			String loginID = (String) getCommonVariable(SystemVariableConsts.LOGINID);
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT DEPT_ID FROM TBORG_MEMBER WHERE EMP_ID = :empID");
			condition.setObject("empID", loginID);
			condition.setQueryString(sb.toString());
			List<Map<String, Object>> list = dam.exeQuery(condition);
			if ("250".equals(list.get(0).get("DEPT_ID").toString())) {
				branchNbr = list.get(0).get("DEPT_ID").toString();
			} else {
				branchNbr = sysLoginBrh;
			}
		} else {
			branchNbr = sysLoginBrh;
		}
		return branchNbr;
	}
}
