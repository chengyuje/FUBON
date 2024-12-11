package com.systex.jbranch.app.server.fps.sot630;

import java.sql.Timestamp;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * @author Ocean
 * @date 2023/08/27
 * 
 */
@Component("sot630")
@Scope("request")
public class SOT630 extends FubonWmsBizLogic {

	private DataAccessManager dam = null;

	public void query(Object body, IPrimitiveMap header) throws JBranchException {

		SOT630InputVO inputVO = (SOT630InputVO) body;
		SOT630OutputVO outputVO = new SOT630OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sb = new StringBuffer();
		sb.append("SELECT SEQNO, ");
		sb.append("       CUST_ID, ");
		sb.append("       CUST_NAME, ");
		sb.append("       AUTH_TYPE, ");
        sb.append("       PROD_TYPE, ");
        sb.append("       TRADE_TYPE, ");
        sb.append("       EVALUATE_VALID_DATE, ");
        sb.append("       ABILITY_RESULT, ");
        sb.append("       TRUST_TRADE_TYPE, ");
        sb.append("       AUTH_BRANCH_NBR, ");
        sb.append("       AUTH_DIRECTOR_EMP_ID, ");
        sb.append("       AUTH_DATE, ");
        sb.append("       CREATETIME, ");
        sb.append("       CREATOR, ");
        sb.append("       MODIFIER, ");
        sb.append("       LASTUPDATE, ");
        sb.append("       FINACIAL_COGNITION_RESULT, ");
        sb.append("       AUTH_REASON ");
		sb.append("FROM ( ");
		sb.append("  SELECT  SEQNO, ");
		sb.append("          CUST_ID, ");
		sb.append("          CUST_NAME, ");
		sb.append("          AUTH_TYPE, ");
		sb.append("          PROD_TYPE, ");
		sb.append("          TRADE_TYPE, ");
		sb.append("          EVALUATE_VALID_DATE, ");
		sb.append("          ABILITY_RESULT, ");
		sb.append("          TRUST_TRADE_TYPE, ");
		sb.append("          AUTH_BRANCH_NBR, ");
		sb.append("          AUTH_DIRECTOR_EMP_ID, ");
		sb.append("          AUTH_DATE, ");
		sb.append("          CREATETIME, ");
		sb.append("          CREATOR, ");
		sb.append("          MODIFIER, ");
		sb.append("          LASTUPDATE, ");
		sb.append("          FINACIAL_COGNITION_RESULT, ");	// 20240109 : add by ocean #0001858_WMS-CR-20231226-01_高齡客戶資訊觀察表控管
		sb.append("          AUTH_REASON ");	// 20241204 : add by ocean #0002219_WMS-CR-20241108-02_為強化高齡客戶評估作業擬優化高齡評估相關功能
		sb.append("  FROM TBSOT_OLD_AGE_CHECK_LIST ");
		
		sb.append("  UNION ");
		
		sb.append("  SELECT  NULL AS SEQNO, ");
		sb.append("          BASE.CUST_ID, ");
		sb.append("         BASE.CUST_NAME, ");
		sb.append("         'C' AS AUTH_TYPE, ");
		sb.append("         NULL AS PROD_TYPE, ");
		sb.append("         NULL AS TRADE_TYPE, ");
		sb.append("         BASE.EVALUATE_VALID_DATE, ");
		sb.append("         BASE.ABILITY_RESULT, ");
		sb.append("         NULL AS TRUST_TRADE_TYPE, ");
		sb.append("         MEM.DEPT_ID AS AUTH_BRANCH_NBR, ");
		sb.append("         BASE.AUTH_DIRECTOR_EMP_ID, ");
		sb.append("         BASE.AUTH_DATE, ");
		sb.append("         BASE.CREATETIME, ");
		sb.append("         BASE.CREATOR, ");
		sb.append("         BASE.MODIFIER, ");
		sb.append("         BASE.LASTUPDATE, ");
		sb.append("         BASE.FINACIAL_COGNITION_RESULT, ");
		sb.append("         BASE.AUTH_REASON ");
		sb.append("  FROM ( ");
		sb.append("    SELECT  CUST_ID, ");
		sb.append("            CUST_NAME, ");
		sb.append("            EVALUATE_VALID_DATE, ");
		sb.append("            AUTH_DIRECTOR_EMP_ID, ");
		sb.append("            AUTH_DATE, ");
		sb.append("            CREATETIME, ");
		sb.append("            CREATOR, ");
		sb.append("            MODIFIER, ");
		sb.append("            LASTUPDATE,");
		sb.append("            HEALTHY_RESULT, ");
		sb.append("            ABILITY_RESULT, ");
		sb.append("            REMARK_RESULT, ");
		sb.append("            FINACIAL_COGNITION_RESULT, ");
		sb.append("            RESULT_RESULT, ");
		sb.append("            AUTH_REASON ");
		sb.append("    FROM ( ");
		sb.append("      SELECT CUST_ID, ");
		sb.append("             CUST_NAME, ");
		sb.append("             CREATETIME AS EVALUATE_VALID_DATE, ");
		sb.append("             QUESTION_VERSION, ");
		sb.append("             AFT_ANSWER_SEQ_REMARK, ");
		sb.append("             AUTH_DIRECTOR_EMP_ID, ");
		sb.append("             AUTH_DATE, ");
		sb.append("             AUTH_REASON, ");
		sb.append("             CREATETIME, ");
		sb.append("             CREATOR, ");
		sb.append("             MODIFIER, ");
		sb.append("             LASTUPDATE ");
		sb.append("      FROM TBCRM_OLD_AGE_CHECK_LIST ");
		sb.append("    ) ");
		sb.append("    PIVOT (MAX(AFT_ANSWER_SEQ_REMARK) FOR QUESTION_VERSION IN ('202210010001' AS HEALTHY_RESULT, ");
		sb.append("                                                               '202210010002' AS ABILITY_RESULT, ");
		sb.append("                                                               '202210010003' AS REMARK_RESULT, ");
		sb.append("                                                               '202210010004' AS FINACIAL_COGNITION_RESULT, ");
		sb.append("                                                               '202210010005' AS RESULT_RESULT)) ");
		sb.append("  ) BASE ");
		sb.append("  LEFT JOIN TBPMS_EMPLOYEE_REC_N MEM ON BASE.AUTH_DIRECTOR_EMP_ID = MEM.EMP_ID AND BASE.CREATETIME BETWEEN MEM.START_TIME AND MEM.END_TIME AND (LENGTH(MEM.DEPT_ID) = 3 AND MEM.DEPT_ID >= '200' AND MEM.DEPT_ID <= '900') ");

		sb.append(") ");
		
		sb.append("WHERE 1 = 1 ");

		if (StringUtils.isNotEmpty(inputVO.getCustID())) {
			sb.append("AND CUST_ID = :custID ");
			queryCondition.setObject("custID", inputVO.getCustID());
		}
		
		if (StringUtils.isNotEmpty(inputVO.getAuthType())) {
			sb.append("AND AUTH_TYPE = :authType ");
			queryCondition.setObject("authType", inputVO.getAuthType());
		}

		if (StringUtils.isNotEmpty(inputVO.getProdType())) {
			sb.append("AND PROD_TYPE = :prodType ");
			queryCondition.setObject("prodType", inputVO.getProdType());
		}

		if (StringUtils.isNotEmpty(inputVO.getTradeType())) {
			sb.append("AND TRADE_TYPE = :tradeType ");
			queryCondition.setObject("tradeType", inputVO.getTradeType());
		}

		if (StringUtils.isNotEmpty(inputVO.getTrustTradeType())) {
			sb.append("AND TRUST_TRADE_TYPE = :trustTradeType ");
			queryCondition.setObject("trustTradeType", inputVO.getTrustTradeType());
		}
		
		if (null != inputVO.getsDate()) {
			sb.append("AND AUTH_DATE >= TRUNC(:startDate) ");
			queryCondition.setObject("startDate", inputVO.getsDate());
		}

		if (null == inputVO.getsDate() && null != inputVO.geteDate()) {
			sb.append("AND AUTH_DATE <= TRUNC(:endDate) ");
			queryCondition.setObject("endDate", inputVO.geteDate());
		}
		
		sb.append("ORDER BY AUTH_DATE DESC ");

		queryCondition.setQueryString(sb.toString());	
		
		outputVO.setTradeList(dam.exeQuery(queryCondition));

		this.sendRtnObject(outputVO);
	}
	
	public void save(Object body, IPrimitiveMap header) throws JBranchException {
		
		SOT630InputVO inputVO = (SOT630InputVO) body;
		
		save(inputVO);
		
		sendRtnObject(null);
	}

	public void save(SOT630InputVO inputVO) throws JBranchException {
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb = new StringBuffer();
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		sb.append("INSERT INTO TBSOT_OLD_AGE_CHECK_LIST ( ");
		sb.append("  SEQNO, ");
		sb.append("  CUST_ID, ");
		sb.append("  CUST_NAME, ");
		sb.append("  AUTH_TYPE, ");
		sb.append("  PROD_TYPE, ");
		sb.append("  TRADE_TYPE, ");
		sb.append("  EVALUATE_VALID_DATE, ");
		sb.append("  ABILITY_RESULT, ");
		sb.append("  TRUST_TRADE_TYPE, ");
		sb.append("  AUTH_BRANCH_NBR, ");
		sb.append("  AUTH_DIRECTOR_EMP_ID, ");
		sb.append("  AUTH_DATE, ");
		sb.append("  VERSION, ");
		sb.append("  CREATETIME, ");
		sb.append("  CREATOR, ");
		sb.append("  MODIFIER, ");
		sb.append("  LASTUPDATE,  ");
		sb.append("  FINACIAL_COGNITION_RESULT, ");	// 20240109 : add by ocean #0001858_WMS-CR-20231226-01_高齡客戶資訊觀察表控管
		sb.append("  AUTH_REASON "); // 20241204 : add by ocean #0002219_WMS-CR-20241108-02_為強化高齡客戶評估作業擬優化高齡評估相關功能
		
		sb.append(") ");
		sb.append("VALUES ( ");
		sb.append("  TBSOT_OLD_AGE_CHECK_LIST_SEQ.NEXTVAL, ");
		sb.append("  :CUST_ID, ");
		sb.append("  :CUST_NAME, ");
		sb.append("  :AUTH_TYPE, ");
		sb.append("  :PROD_TYPE, ");
		sb.append("  :TRADE_TYPE, ");
		sb.append("  :EVALUATE_VALID_DATE, ");
		sb.append("  :ABILITY_RESULT, ");
		sb.append("  :TRUST_TRADE_TYPE, ");
		sb.append("  :AUTH_BRANCH_NBR, ");
		sb.append("  :AUTH_DIRECTOR_EMP_ID, ");
		sb.append("  :AUTH_DATE, ");
		sb.append("  :VERSION, SYSDATE, :CREATOR, :MODIFIER, SYSDATE, ");
		sb.append("  :FINACIAL_COGNITION_RESULT, "); // 20240109 : add by ocean #0001858_WMS-CR-20231226-01_高齡客戶資訊觀察表控管
		sb.append("  :AUTH_REASON "); // 20241204 : add by ocean #0002219_WMS-CR-20241108-02_為強化高齡客戶評估作業擬優化高齡評估相關功能
		sb.append(") ");
		
		queryCondition.setObject("CUST_ID", inputVO.getCUST_ID());
		queryCondition.setObject("CUST_NAME", inputVO.getCUST_NAME());
		queryCondition.setObject("AUTH_TYPE", inputVO.getAUTH_TYPE());
		queryCondition.setObject("PROD_TYPE", inputVO.getPROD_TYPE());
		queryCondition.setObject("TRADE_TYPE", inputVO.getTRADE_TYPE());
		queryCondition.setObject("EVALUATE_VALID_DATE", new Timestamp(inputVO.getEVALUATE_VALID_DATE().getTime()));
		queryCondition.setObject("ABILITY_RESULT", inputVO.getABILITY_RESULT());
		queryCondition.setObject("TRUST_TRADE_TYPE", inputVO.getTRUST_TRADE_TYPE());
		queryCondition.setObject("AUTH_BRANCH_NBR", inputVO.getAUTH_BRANCH_NBR());
		queryCondition.setObject("AUTH_DIRECTOR_EMP_ID", inputVO.getAUTH_DIRECTOR_EMP_ID());
		queryCondition.setObject("AUTH_DATE", new Timestamp(inputVO.getAUTH_DATE().getTime()));
		queryCondition.setObject("VERSION", 0);
		queryCondition.setObject("CREATOR", (String) SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINID));
		queryCondition.setObject("MODIFIER", (String) SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINID));
		queryCondition.setObject("FINACIAL_COGNITION_RESULT", inputVO.getFINACIAL_COGNITION_RESULT()); // 20240109 : add by ocean #0001858_WMS-CR-20231226-01_高齡客戶資訊觀察表控管
		queryCondition.setObject("AUTH_REASON", inputVO.getAUTH_REASON()); // 20241204 : add by ocean #0002219_WMS-CR-20241108-02_為強化高齡客戶評估作業擬優化高齡評估相關功能
		
		queryCondition.setQueryString(sb.toString());
		
		dam.exeUpdate(queryCondition);
	}
}
