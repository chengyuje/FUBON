package com.systex.jbranch.platform.server.common.errMsg;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;

public class MsgGet {
	Logger logger = LoggerFactory.getLogger(this.getClass());

	DataAccessManager dam_obj = null;
	
	public String getMsg(String msgCode, List<String> errMsgList)
			throws JBranchException {
		String errorMsg = "";
		try{
			dam_obj = new DataAccessManager();
			
			QueryConditionIF qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			
			StringBuilder sb = new StringBuilder();			
			sb.append("SELECT TEXT FROM TBSYSI18N WHERE CODE =:code");
			qc.setObject("code", msgCode);
			qc.setQueryString(sb.toString());
			List<Map<String, Object>> result_lst = dam_obj.exeQuery(qc);
			
			if (!result_lst.isEmpty()) {
				errorMsg = (String) result_lst.get(0).get("TEXT");
				int i = 0;
				for (String str : errMsgList) {
					errorMsg = StringUtils
							.replace(errorMsg, "{" + i + "}", str); // 訊息參數為{0},{1}...
					errorMsg = StringUtils.replace(errorMsg, "#{field" + (++i)
							+ "}#", str); // 訊息參數為#{field1}#,#{field2}#...
				}
			}
		}
		catch(JBranchException e){
			logger.debug(e.getMessage(), e);
		}
		
		return errorMsg;
	}
}
