package com.systex.jbranch.app.server.fps.iot131;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.util.IPrimitiveMap;



/**
 * IOT131
 * 
 * @author Jimmy
 * @date 2016/09/10
 * @spec null
 */

@Component("iot131")
@Scope("request")
public class IOT131 extends FubonWmsBizLogic{
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	DataAccessManager dam_obj;
	
	public void Initial(Object body, IPrimitiveMap<Object> header) throws JBranchException{
		IOT131InputVO inputVO = (IOT131InputVO) body;
		IOT131OutputVO outputVO = new IOT131OutputVO();
		dam_obj = this.getDataAccessManager();
		QueryConditionIF qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		try {
			if("UPDATE".equals(inputVO.getIn_OPRSTATUS())){
				qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuilder();
				sb.append(" select A.LINK_PCT,B.TARGET_ID,B.LINKED_NAME,B.PRD_RISK,B.KEY_NO ");
				sb.append(" from TBIOT_FUND_LINK A,TBPRD_INS_LINKING B ");
				sb.append(" where A.PRD_LK_KEYNO = B.KEY_NO AND A.INS_KEYNO = :in_INSKEYNO ");
				qc.setObject("in_INSKEYNO", inputVO.getUp_INSKEYNO());
				qc.setQueryString(sb.toString());
				outputVO.setMatchList(dam_obj.exeQuery(qc));
			}
			if("Read".equals(inputVO.getIn_OPRSTATUS())){
				qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuilder();
				sb.append(" select A.LINK_PCT,B.TARGET_ID,B.LINKED_NAME,B.PRD_RISK,B.KEY_NO ");
				sb.append(" from TBIOT_FUND_LINK A,TBPRD_INS_LINKING B ");
				sb.append(" where A.PRD_LK_KEYNO = B.KEY_NO AND A.INS_KEYNO = :in_INSKEYNO ");
				qc.setObject("in_INSKEYNO", inputVO.getUp_INSKEYNO());
				qc.setQueryString(sb.toString());
				outputVO.setMatchList(dam_obj.exeQuery(qc));
			}
			if("new".equals(inputVO.getIn_OPRSTATUS())){
				qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuilder();
				sb.append(" select TARGET_ID,LINKED_NAME,PRD_RISK,KEY_NO ");
				sb.append(" from TBPRD_INS_LINKING ");
				sb.append(" where INSPRD_ID = :in_INSKEYNO and substr(PRD_RISK,2) <= :in_RISK ");
				//要保人高齡限制P值
				if(StringUtils.isNotBlank(inputVO.getC_SENIOR_PVAL())) {
					sb.append(" AND substrb(PRD_RISK, 2, 1) <= substrb(:seniorPVal, 2, 1)");
					qc.setObject("seniorPVal", inputVO.getC_SENIOR_PVAL());
				}
				qc.setObject("in_INSKEYNO", inputVO.getIn_INSKEYNO());
				qc.setObject("in_RISK", inputVO.getIn_RISK().substring(1));
				qc.setQueryString(sb.toString());
				outputVO.setMatchList(dam_obj.exeQuery(qc));
			}
			sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
	

}
