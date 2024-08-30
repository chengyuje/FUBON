package com.systex.jbranch.app.server.fps.crm710;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("crm710")
@Scope("request")
public class CRM710 extends FubonWmsBizLogic {

	private DataAccessManager dam = null;
	SimpleDateFormat sdfYYYYMM = new SimpleDateFormat("yyyyMM");

	private StringBuffer tradeSQL(CRM710InputVO inputVO, QueryConditionIF queryCondition, Map<String, String> fcMap) throws JBranchException {

		StringBuffer sb = new StringBuffer();
		sb.append("WITH BASE AS ( ");
		sb.append("  SELECT TRADE_SEQ, TRADE_DATE, NARRATOR_ID, PROD_ID, PROD_NAME ");
		sb.append("  FROM ( ");
		sb.append("    SELECT TRADE_SEQ, TRADE_DATE, NARRATOR_ID, PROD_ID, PROD_NAME FROM TBSOT_NF_PURCHASE_D ");
		sb.append("    UNION ");
		sb.append("    SELECT TRADE_SEQ, TRADE_DATE, NARRATOR_ID, RDM_PROD_ID AS PROD_ID, RDM_PROD_NAME AS PROD_NAME FROM TBSOT_NF_REDEEM_D ");
		sb.append("    UNION ");
		sb.append("    SELECT TRADE_SEQ, TRADE_DATE, NARRATOR_ID, OUT_PROD_ID AS PROD_ID, OUT_PROD_NAME AS PROD_NAME FROM TBSOT_NF_TRANSFER_D ");
		sb.append("    UNION ");
		sb.append("    SELECT TRADE_SEQ, TRADE_DATE, NARRATOR_ID, B_PROD_ID AS PROD_ID, B_PROD_NAME AS PROD_NAME FROM TBSOT_NF_CHANGE_D ");
		sb.append("    UNION ");
		sb.append("    SELECT TRADE_SEQ, TRADE_DATE, NARRATOR_ID, PROD_ID, PROD_NAME FROM TBSOT_ETF_TRADE_D ");
		sb.append("    UNION ");
		sb.append("    SELECT TRADE_SEQ, TRADE_DATE, NARRATOR_ID, PROD_ID, PROD_NAME FROM TBSOT_BN_TRADE_D ");
		sb.append("    UNION ");
		sb.append("    SELECT TRADE_SEQ, TRADE_DATE, NARRATOR_ID, PROD_ID, PROD_NAME FROM TBSOT_SI_TRADE_D ");
		sb.append("    UNION ");
		sb.append("    SELECT TRADE_SEQ, TRADE_DATE, NARRATOR_ID, PROD_ID, PROD_NAME FROM TBSOT_SN_TRADE_D ");
		sb.append("    UNION ");
		sb.append("    SELECT TRADE_SEQ, TRADE_DATE, NARRATOR_ID, PROD_ID, PROD_NAME FROM TBSOT_FCI_TRADE_D ");
		sb.append("  ) T ");
		sb.append("  WHERE 1 = 1 ");
		
		if (StringUtils.isNotBlank(inputVO.getImportSDate())) {
			sb.append("  AND TO_CHAR(T.TRADE_DATE, 'YYYYMM') >= :yearMonS ");
			queryCondition.setObject("yearMonS", sdfYYYYMM.format(new Date(Long.parseLong(inputVO.getImportSDate()))));
		}
		
		if (StringUtils.isNotBlank(inputVO.getImportEDate())) {
			sb.append("  AND TO_CHAR(T.TRADE_DATE, 'YYYYMM') <= :yearMonE ");
			queryCondition.setObject("yearMonE", sdfYYYYMM.format(new Date(Long.parseLong(inputVO.getImportEDate()))));
		}
		
		if (fcMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			switch ((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE)) {
				case "JRM":
					sb.append("  AND T.NARRATOR_ID = :loginID ");
					queryCondition.setObject("loginID", (String) getCommonVariable(SystemVariableConsts.LOGINID));
					break;
				default:
					break;
			}
		}

		sb.append(") ");

		return sb;
	}
	
	public void query(Object body, IPrimitiveMap header) throws JBranchException {

		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> fcMap = xmlInfo.doGetVariable("FUBONSYS.FC_ROLE", FormatHelper.FORMAT_2);
		
		CRM710InputVO inputVO = (CRM710InputVO) body;
		CRM710OutputVO outputVO = new CRM710OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sb = new StringBuffer();
		sb.append(tradeSQL(inputVO, queryCondition, fcMap));
		sb.append("SELECT DISTINCT ALL_TRADE.* ");
		sb.append("FROM ( ");
		sb.append("  SELECT T_MAIN.TRADE_SEQ, ");
		sb.append("         NULL AS TRADE_DATE, ");
		sb.append("         T_MAIN.CUST_ID, ");
		sb.append("         T_MAIN.CUST_NAME, ");
		sb.append("         CM.BRA_NBR AS BRANCH_NBR, ");
		sb.append("         DEFN.BRANCH_NAME, ");
		sb.append("         CM.AO_CODE, ");
		sb.append("         MEM.EMP_NAME, ");
		sb.append("         T_MAIN.PROD_TYPE, ");
		sb.append("         T_MAIN.TRADE_TYPE, ");
		sb.append("         NULL AS PROD_ID_LIST, ");
		sb.append("         NULL AS PROD_NAME_LIST ");
		sb.append("  FROM TBSOT_TRADE_MAIN T_MAIN ");
		sb.append("  LEFT JOIN TBCRM_CUST_MAST CM ON T_MAIN.CUST_ID = CM.CUST_ID ");
		sb.append("  LEFT JOIN TBORG_SALES_AOCODE AO ON CM.AO_CODE = AO.AO_CODE ");
		sb.append("  LEFT JOIN TBORG_MEMBER MEM ON AO.EMP_ID = MEM.EMP_ID ");
		sb.append("  LEFT JOIN VWORG_DEFN_INFO DEFN ON CM.BRA_NBR = DEFN.BRANCH_NBR ");
		sb.append("  WHERE 1 = 1 ");
		sb.append("  AND T_MAIN.TRADE_STATUS IN ('3', '4') ");
		sb.append("  AND EXISTS(SELECT B.PROD_ID FROM BASE B WHERE B.TRADE_SEQ = T_MAIN.TRADE_SEQ) ");
		
		sb.append("  UNION ");
				     
		sb.append("  SELECT IOT.CASE_ID AS TRADE_SEQ, ");
		sb.append("         IOT.CREATETIME, ");
		sb.append("         IOT.CUST_ID, ");
		sb.append("         IOT.PROPOSER_NAME AS CUST_NAME, ");
		sb.append("         CM.BRA_NBR AS BRANCH_NBR, ");
		sb.append("         DEFN.BRANCH_NAME, ");
		sb.append("         CM.AO_CODE, ");
		sb.append("         MEM.EMP_NAME, ");
		sb.append("         '7' AS PROD_TYPE, ");
		sb.append("         REG_TYPE AS TRADE_TYPE, ");
		sb.append("         IOT.INSPRD_ID, ");
		sb.append("         M.INSPRD_NAME AS PROD_NAME ");
		sb.append("  FROM TBIOT_PREMATCH IOT ");
		sb.append("  LEFT OUTER JOIN TBPRD_INS_MAIN M ON M.INSPRD_KEYNO = IOT.INSPRD_KEYNO ");
		sb.append("  LEFT JOIN TBCRM_CUST_MAST CM ON IOT.CUST_ID = CM.CUST_ID ");
		sb.append("  LEFT JOIN TBORG_SALES_AOCODE AO ON CM.AO_CODE = AO.AO_CODE ");
		sb.append("  LEFT JOIN TBORG_MEMBER MEM ON AO.EMP_ID = MEM.EMP_ID ");
		sb.append("  LEFT JOIN VWORG_DEFN_INFO DEFN ON CM.BRA_NBR = DEFN.BRANCH_NBR ");
		sb.append("  WHERE IOT.STATUS IN ('2', '3', '4') ");
		
		if (StringUtils.isNotBlank(inputVO.getImportSDate())) {
			sb.append("  AND TO_CHAR(IOT.CREATETIME, 'YYYYMM') >= :yearMonS ");
			queryCondition.setObject("yearMonS", sdfYYYYMM.format(new Date(Long.parseLong(inputVO.getImportSDate()))));
		}
		
		if (StringUtils.isNotBlank(inputVO.getImportEDate())) {
			sb.append("  AND TO_CHAR(IOT.CREATETIME, 'YYYYMM') <= :yearMonE ");
			queryCondition.setObject("yearMonE", sdfYYYYMM.format(new Date(Long.parseLong(inputVO.getImportEDate()))));
		}
		
		if (fcMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			switch ((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE)) {
				case "JRM":
					sb.append("  AND IOT.RECRUIT_ID = :loginID ");
					queryCondition.setObject("loginID", (String) getCommonVariable(SystemVariableConsts.LOGINID));
					break;
				default:
					break;
			}
		}
		
		sb.append(") ALL_TRADE ");
		sb.append("WHERE 1 = 1 ");
		
		if (StringUtils.isNotBlank(inputVO.getCustID())) {
			sb.append("AND ALL_TRADE.CUST_ID LIKE :custID ");
			queryCondition.setObject("custID", "%" + inputVO.getCustID().toUpperCase() + "%");
		}
		
		if (StringUtils.isNotBlank(inputVO.getCustName())) {
			sb.append("AND ALL_TRADE.CUST_NAME LIKE :custName ");
			queryCondition.setObject("custName", "%" + inputVO.getCustName() + "%");
		}
		
		if (StringUtils.isNotBlank(inputVO.getProdType())) {
			sb.append("AND ALL_TRADE.PROD_TYPE = :prodType ");
			System.out.println("prodType:"+ inputVO.getProdType());
			queryCondition.setObject("prodType", inputVO.getProdType());
		}
		
		if (StringUtils.isNotBlank(inputVO.getTradeType())) {
			sb.append("AND ALL_TRADE.TRADE_TYPE = :tradeType ");
			queryCondition.setObject("tradeType", inputVO.getTradeType());
		}

		sb.append("ORDER BY ALL_TRADE.TRADE_SEQ DESC ");

		queryCondition.setQueryString(sb.toString());

		List<Map<String, Object>> list = dam.exeQuery(queryCondition);

		for (Map<String, Object> map : list) {
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuffer();
			
			if (!StringUtils.equals(map.get("PROD_TYPE").toString(), "7")) {
				sb.append(tradeSQL(inputVO, queryCondition, fcMap));
				sb.append("SELECT TRADE_SEQ, TRADE_DATE, PROD_ID, PROD_NAME ");
				sb.append("FROM BASE ");
				sb.append("WHERE TRADE_SEQ = :tradeSEQ ");
				
				queryCondition.setObject("tradeSEQ", map.get("TRADE_SEQ"));
	
				queryCondition.setQueryString(sb.toString());
	
				List<Map<String, Object>> dtlList = dam.exeQuery(queryCondition);
	
				if (dtlList.size() > 0) {
					String prodID = "";
					String prodName = "";
					for (Map<String, Object> dtlMap : dtlList) {
						prodID = prodID + dtlMap.get("PROD_ID") + " \n";
						prodName = prodName + dtlMap.get("PROD_NAME") + " \n";
					}
	
					map.put("PROD_ID_LIST", prodID);
					map.put("PROD_NAME_LIST", prodName);
					map.put("TRADE_DATE", dtlList.get(0).get("TRADE_DATE"));
				}
			}
		}
		
		outputVO.setTradeList(list);

		this.sendRtnObject(outputVO);
	}

}
