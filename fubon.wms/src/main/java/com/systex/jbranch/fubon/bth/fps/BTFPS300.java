package com.systex.jbranch.fubon.bth.fps;

import com.systex.jbranch.app.common.fps.table.TBFPS_SPP_PRD_RETURN_HEADVO;
import com.systex.jbranch.app.server.fps.fpsutils.FPSUtils;
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.util.IPrimitiveMap;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component("btfps300")
@Scope("prototype")
public class BTFPS300 extends BizLogic {
	private DataAccessManager dam = null;
	
	public void execute(Object body, IPrimitiveMap<?> header) throws Exception {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT PLAN_ID, CUST_ID FROM TBFPS_SPP_PRD_RETURN_HEAD");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		if(list.size() > 0){
			for (Map<String, Object> map : list) {
				// 更新TBFPS_SPP_PRD_RETURN_HEAD.INV_AMT_CURRENT (目前投入資金(元)(台幣))
				sql = new StringBuffer();
				sql.append("SELECT TPPS.PLAN_ID, SUM(VAAD.INV_AMT_TWD) INV_AMT_CURRENT ");
				sql.append("FROM MVFPS_AST_ALLPRD_DETAIL VAAD ");
				sql.append("LEFT JOIN ( ");
				sql.append("    SELECT PLAN_ID, (POLICY_NO ||'-'|| TRIM(TO_CHAR(POLICY_SEQ  ,'00')) || CASE WHEN ID_DUP <> ' ' THEN '-' || ID_DUP END ) AS CERT_NBR ");
				sql.append("    FROM TBFPS_PORTFOLIO_PLAN_SPP WHERE PLAN_ID IN (SELECT PLAN_ID FROM TBFPS_SPP_PRD_RETURN_HEAD WHERE CUST_ID = :custId) AND POLICY_NO IS NOT NULL ");
				sql.append("    UNION ");
				sql.append("    SELECT PLAN_ID, CERTIFICATE_ID AS CERT_NBR FROM TBFPS_PLANID_MAPPING WHERE PLAN_ID IN (SELECT PLAN_ID FROM TBFPS_SPP_PRD_RETURN_HEAD WHERE CUST_ID = :custId) ");
				sql.append(") TPPS ON VAAD.CERT_NBR = TPPS.CERT_NBR ");
				sql.append("WHERE 1 = 1 ");
				sql.append("AND CUST_ID = :custId ");
				sql.append("AND PLAN_ID = :planId ");
				sql.append("AND AST_TYPE IN ('07', '08', '09', '14') ");
				sql.append("GROUP BY TPPS.PLAN_ID ");
				
				GenericMap resultGMap = new GenericMap(map);
				String custId = resultGMap.getNotNullStr("CUST_ID");
				String planId = resultGMap.getNotNullStr("PLAN_ID");
				
				queryCondition.setObject("custId", custId);
				queryCondition.setObject("planId", planId);
				queryCondition.setQueryString(sql.toString());
				
				List<Map<String, Object>> invAmtList = dam.exeQuery(queryCondition);
				
				BigDecimal invAmtCurrent = new BigDecimal(0);
				if (invAmtList.size() > 0) {
					String pk = invAmtList.get(0).get("PLAN_ID").toString();
					TBFPS_SPP_PRD_RETURN_HEADVO vo = (TBFPS_SPP_PRD_RETURN_HEADVO) dam.findByPKey(TBFPS_SPP_PRD_RETURN_HEADVO.TABLE_UID, pk); 
					if (null != vo) {
						invAmtCurrent = new BigDecimal(invAmtList.get(0).get("INV_AMT_CURRENT").toString());
//						System.out.println("=====================");
//						System.out.println(invAmtCurrent);
//						vo.setINV_AMT_CURRENT(invAmtCurrent);
//						dam.update(vo);						
					}
				}
				
//				GenericMap resultGMap = new GenericMap(map);
//				String planId = resultGMap.getNotNullStr("PLAN_ID");
				TBFPS_SPP_PRD_RETURN_HEADVO vo = (TBFPS_SPP_PRD_RETURN_HEADVO) dam.findByPKey(TBFPS_SPP_PRD_RETURN_HEADVO.TABLE_UID, planId);
				if (null != vo) {
//					System.out.println("*********************");
//					System.out.println(invAmtCurrent);
					vo.setINV_AMT_CURRENT(invAmtCurrent);
//					String custId = vo.getCUST_ID();
					
					// 1. 報酬率重算(RETURN_RATE)
					BigDecimal returnRate = FPSUtils.getInterestReturnRate(vo.getMARKET_VALUE(), vo.getINV_AMT_CURRENT());
//					System.out.println(planId);
//					System.out.println(returnRate.setScale(2, BigDecimal.ROUND_UP));
					vo.setRETURN_RATE(returnRate.setScale(2, BigDecimal.ROUND_UP));	//四捨五入至小數第二位
					
					// 目標年期
					int targetYear = FPSUtils.getInvestmentYear(dam, planId);
					
					// 未來剩餘月份
			        int futureMonth = FPSUtils.getRemainingMonth(vo.getCreatetime(), targetYear);
			        
			        // 已購入
			        BigDecimal purchaseFutureValue = FPSUtils.getPurchaseFutureValue(dam, custId, FPSUtils.getCertificateIdList(dam, planId), futureMonth);
			        
			        // 未來投入金額 (未購入 + 已購入)
			        // 不含未購入 FPSUtils.getHistory(dam, resultGMap.getNotNullStr("PLAN_ID"), inputVO.getCustID(), "N")
			        BigDecimal futureQuota = FPSUtils.getFutureQuota(new ArrayList<Map<String, Object>>(), futureMonth, purchaseFutureValue);
			        
					// 2. 應達目標重算(AMT_TARGET)
			        BigDecimal amtTarget = FPSUtils.getAchievementTarget(vo.getCreatetime(), vo.getINV_AMT_TARGET(), vo.getINV_AMT_CURRENT(), futureQuota, targetYear);
//			        System.out.println(amtTarget.setScale(0, BigDecimal.ROUND_UP));
			        vo.setAMT_TARGET(amtTarget.setScale(0, BigDecimal.ROUND_UP));
					
					// 3. 達成率重算(HIT_RATE)
					BigDecimal hitRate = FPSUtils.getAchievementRate(vo.getMARKET_VALUE(), vo.getAMT_TARGET());
//					System.out.println(hitRate.setScale(0, BigDecimal.ROUND_UP));
					vo.setHIT_RATE(hitRate.setScale(0, BigDecimal.ROUND_UP));
					
					dam.update(vo);
				}
			}
		}
	}
}