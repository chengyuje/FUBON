package com.systex.jbranch.app.server.fps.ins810;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.systex.jbranch.app.server.fps.ins.parse.WSMappingParserUtils;
import com.systex.jbranch.app.server.fps.insjlb.InsjlbUtils;
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

import java.util.Arrays;

@Service("ins810")
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class FubonIns810 extends INS810 implements PolicySuggestServiceInf {
	private Logger logger = LoggerFactory.getLogger(FubonIns810.class);

	/**
	 * 取得行外保單
	 * 
	 * @param body
	 * @return
	 * @return List<Map<String,Object>>
	 * @throws Exception
	 * */
	public List<Map<String, Object>> doGetOutBuy(Object body) {
		INS810InputVO inputVO = (INS810InputVO) body;
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		DataAccessManager dam = null;
		QueryConditionIF qc = null;
		List<String> insseqList = null;

		try {
			dam = getDataAccessManager();
			qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

			// 如果沒有可視保單就跳出
			insseqList = queryInsseqList(QueryType.OUT_BUY, inputVO);

			if (CollectionUtils.isEmpty(insseqList)) {
				return result;
			}

			StringBuffer sb = new StringBuffer();
			sb.append(" SELECT ");
			sb.append(" 	A.POLICY_NBR,  ");
			sb.append(" 	A.PRD_KEYNO,  "); // 行外保單PK
			sb.append(" 	A.INSURED_NAME, "); // 行外保單PK
			sb.append(" 	A.KEYNO, "); // 行外保單PK
			sb.append(" 	A.INSURED_ID, "); // 主約被保險人ID
			sb.append(" 	A.CUST_ID,  "); // 被保險人ID(主附約被保險人ID可能不同)
			sb.append(" 	A.INSSEQ,  "); // 保單號碼
			sb.append(" 	A.COVERYEAR_SEL, "); // 保障年期(對照資訊源)
			sb.append(" 	A.KIND_SEL, "); // 型別(對照資訊源)
			sb.append(" 	A.UPTYPE, "); // 單位或計畫(對照資訊源)
			sb.append(" 	A.INSURED_OBJECT, "); // 對象(對照資訊源)
			sb.append(" 	A.PAYMENTYEAR_SEL,  "); // 繳費年期(對應資訊源)
			sb.append(" 	A.IS_MAIN,  "); // 主附約
			sb.append(" 	A.PAYTYPE,  "); // 繳別(對應資訊源)
			sb.append(" 	A.INSUREDAMT,  "); // 保額
			sb.append(" 	A.UPQTY_SEL,  "); // 單位(對應資訊源)
			sb.append(" 	A.CURR_CD,  "); // 幣別(對應資訊源)
			sb.append(" 	A.INSYEARFEE,  "); // 原幣續期保費待確認
			sb.append(" 	(A.INSYEARFEE * DECODE(F.rate, '0','1',null, '1', F.rate)) LOCAL_INSYEARFEE,  "); // 直接算出台幣
																											// sen
			sb.append(" 	DECODE(F.rate, '0','1',null, '1', F.rate) RATE, "); // 匯率也順便丟出去
																				// sen
			sb.append(" 	A.EFFECTED_DATE,  "); // 保單生效日
			sb.append(" 	A.STATUS,  "); // 保單狀態(永遠有效)
			sb.append(" 	NVL(A.BENEFICIARY_YN, 'N') AS BENEFICIARY_YN,  "); // 要保人是否為身故受益人
			sb.append(" 	NVL2(B.CUST_ID,B.BIRTH_DATE,C.BIRTH_DATE) BIRTH_DATE,  "); // 出生日期
			sb.append(" 	NVL2(B.CUST_ID,B.GENDER,C.GENDER) GENDER, "); // 性別
			sb.append(" 	NULL MARRIAGE_STAT,  "); // 婚姻狀況
			sb.append(" 	NVL2(B.CUST_ID,B.FB_CUST, 'Y') FB_CUST,  "); // 是否為富邦客戶
			sb.append(" 	D.COM_ID,  "); // 資訊源保險公司
			sb.append(" 	D.PRD_NAME,  "); // 資訊源商品名稱
			sb.append(" 	D.PRD_ID,  "); // 資訊源險種代碼
			sb.append(" 	D.ITEM_A, "); // 檢查保障年期
			sb.append(" 	D.ITEM_Y, "); // 檢查繳費年期
			sb.append(" 	D.ITEM_K, "); // 檢查型別
			sb.append(" 	D.ITEM_P, "); // 檢查計畫
			sb.append(" 	D.ITEM_U, "); // 檢查單位
			sb.append(" 	D.ITEM_X, ");
			sb.append(" 	D.LIST_Y, ");
			sb.append(" 	D.LIST_A, ");
			sb.append(" 	D.LIST_X, ");
			sb.append(" 	D.LIST_K, ");
			sb.append(" 	D.LIST_P, ");
			sb.append(" 	D.LIST_U, ");
			sb.append(" 	D.LIST_E, ");
			sb.append(" 	D.LIST_O, ");// 檢查對象
			sb.append(" 	D.COVERCACULUNITDESC, "); // 單位
			sb.append(" 	E.COM_NAME, ");
			sb.append(" 	'2' INOUT  "); // 資料來源
			sb.append(" 	FROM TBINS_OUTBUY_MAST A ");
			sb.append(" 	left join TBINS_CUST_MAST B on A.CUST_ID = B.CUST_ID ");
			sb.append(" 	left join TBCRM_CUST_MAST C on A.CUST_ID = C.CUST_ID ");
			sb.append(" 	LEFT JOIN (SELECT CUR_COD, (BUY_RATE + SEL_RATE)/2 rate "); // 串上匯率
			sb.append(" 	FROM TBPMS_IQ053 WHERE mtn_date = (SELECT max(mtn_date) FROM tbpms_iq053)) F ");
			sb.append(" 	ON F.CUR_COD = A.CURR_CD, ");
			sb.append(" 	 TBPRD_INSDATA_PROD_MAIN D ");
			sb.append(" left join  ");
			sb.append(" 	 TBPRD_INSDATA_COMPANY E  ");
			sb.append(" on E.COM_ID = D.COM_ID  ");
			sb.append(" WHERE INSSEQ in(:INSSEQ) ");
			sb.append(" AND A.PRD_KEYNO = D.KEY_NO ");
			sb.append(" ORDER BY A.POLICY_NBR, A.IS_MAIN DESC, A.EFFECTED_DATE ");

			qc.setObject("INSSEQ", insseqList);
			qc.setQueryString(sb.toString());
			result = dam.exeQuery(qc);
		} catch (DAOException e) {
			e.printStackTrace();
		} catch (JBranchException e) {
			e.printStackTrace();
		}

		return result;
	}

	/** 行內可視保單 */
	public List queryInBuyPolicyList(String custId, List<String> aoCode,
			String loginBraNbr) throws JBranchException {
		List<Map<String, Object>> resultList = null;
		List<Map<String, Object>> insseqList = new ArrayList<Map<String, Object>>();
		DataAccessManager dam = this.getDataAccessManager();

		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT ");
		sb.append(" 	DISTINCT ");
		sb.append(" 	A.POLICY_NBR, ");
		sb.append(" 	A.POLICY_SEQ, ");
		sb.append(" 	A.ID_DUP ");
		sb.append(" FROM TBCRM_AST_INS_MAST A");
		sb.append("      ,TBCRM_CUST_MAST B");
		sb.append("      ,TBCRM_CUST_MAST C");
		sb.append(" WHERE A.CUST_ID = B.CUST_ID");
		sb.append(" AND A.INS_ID = C.CUST_ID");
		sb.append(" AND (( (B.AO_CODE IS NULL AND B.BRA_NBR = :loginBraNbr) or (B.AO_CODE IS NOT NULL AND B.AO_CODE in(:loginAO)) ) ");
		sb.append(" 	 OR ( (C.AO_CODE IS NULL AND C.BRA_NBR = :loginBraNbr) or (C.AO_CODE IS NOT NULL AND C.AO_CODE in(:loginAO)) )) ");
		sb.append(" AND A.INS_ID = :custId ");

		QueryConditionIF queryCondition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setObject("loginAO", aoCode);
		queryCondition.setObject("custId", custId);
		queryCondition.setObject("loginBraNbr", loginBraNbr);
		queryCondition.setQueryString(sb.toString());

		resultList = dam.exeQuery(queryCondition);

		return resultList;
	}

	/**
	 * 取得行內保單
	 * 
	 * @param body
	 * @return
	 * @return List<Map<String,Object>>
	 * @throws Exception
	 * */
	public List<Map<String, Object>> doGetInBuy(Object body) {
		INS810InputVO inputVO = (INS810InputVO) body;
		DataAccessManager dam = null;
		QueryConditionIF qc = null;
		List<Map<String, Object>> inBuyPolicyList = null;
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		int idx = 0;

		try {
			dam = getDataAccessManager();
			qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

			StringBuffer sb = new StringBuffer();

			// 如果沒有可視保單就跳出
			inBuyPolicyList = queryInsseqList(QueryType.IN_BUY, inputVO);

			if (CollectionUtils.isEmpty(inBuyPolicyList)) {
				return result;
			}
			sb.append(" SELECT distinct ");
			sb.append(" 	(A.POLICY_NBR||'-'||A.POLICY_SEQ||'-'||A.ID_DUP) AS POLICY_NO,  ");
			sb.append(" 	'富邦人壽' COM_NAME ,  ");
			sb.append(" 	TNS.PROD_NAME_S AS POLICY_SIMP_NAME,  ");
			sb.append(" 	TNS.INS_ITEM AS INSPRD_ID,  ");
			sb.append(" 	TNS.BELONG_ID AS  INSURED_ID,  "); // INSURED_ID 被保人ID
			sb.append(" 	B.GUARANTEE_ANNUAL COVERYEAR_SEL,  "); // 保障年期
			sb.append(" 	D.CUST_NAME INSURED_NAME,  "); // 被保人NAME
			sb.append(" 	D.GENDER INSURED_GENDER,  "); // 被保險人性別
			sb.append(" 	B.INS_TYPE INS_TYPE ,  ");	// FPS 理規要的 ins_type
			sb.append(" 	TFS.PREM_YEAR AS PAYMENTYEAR_SEL ,  "); //20180918 改為商品年期
			sb.append(" 	(CASE WHEN TNS.INS_NO = '00' THEN 'Y' ELSE 'N' END) AS MAST_SLA_TYPE,  ");
			sb.append(" 	TNS.EX_INS_AMOUNT AS POLICY_ASSURE_AMT,  ");
			sb.append(" 	TNS.EX_UNIT AS UNIT,  ");
			sb.append(" 	A.CRCY_TYPE,  ");
			sb.append(" 	TNS.PREM_PAYABLE AS INSYEARFEE,  ");
			sb.append(" 	(TNS.PREM_PAYABLE * (DECODE(F.rate, '0','1',null, '1', F.rate))) LOCAL_INSYEARFEE,  "); // 直接算出台幣// sen
			sb.append(" 	DECODE(F.rate, '0','1',null, '1', F.rate) RATE,  ");// 匯率也順便丟出去// sen
			sb.append(" 	TNS.EFF_DATE AS POLICY_ACTIVE_DATE,  ");
			sb.append(" 	A.CONTRACT_STATUS,  ");
			sb.append(" 	NVL(TNM.BENEFICIARY_YN, 'N') AS BENEFICIARY_YN,  "); // 要保人是否為身故受益人
			sb.append(" 	'1' INOUT,  ");
			sb.append(" 	DECODE(TNS.PAY_TYPE, 'D', '0', 'M', '12', 'A', '1', 'Q', '4', 'S', '2', TNS.PAY_TYPE) PAY_TYPE, ");
			sb.append(" 	D.BIRTH_DATE　INSURED_BIRTHDAY　 , C.* ");
			sb.append(" 	FROM TBCRM_AST_INS_MAST A ");
			sb.append(" 	INNER JOIN TBCRM_CUST_MAST D ON D.CUST_ID = A.INS_ID ");
			sb.append(" 	INNER JOIN TBCRM_NPOLD TNS ON A.POLICY_NBR = TNS.POLICY_NO AND A.POLICY_SEQ = TNS.POLICY_SEQ AND A.ID_DUP = TNS.ID_DUP ");
			sb.append(" 		AND TNS.INS_STATUS in (SELECT DISTINCT TRIM(REGEXP_SUBSTR(STR, '[^,]+', 1, LEVEL)) STR ");
			sb.append("					 FROM (SELECT PARAM_CODE AS STR FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'INS.NORMAL_POLICY_STATUS') T ");
			sb.append("							 CONNECT BY INSTR(STR, ',', 1, LEVEL - 1) > 0) ");
			sb.append(" 	LEFT JOIN (SELECT CUR_COD, (BUY_RATE + SEL_RATE)/2 rate FROM TBPMS_IQ053 WHERE mtn_date = (SELECT max(mtn_date) FROM tbpms_iq053)) F ON F.CUR_COD = A.CRCY_TYPE ");
			sb.append(" 	LEFT JOIN TBCRM_NPOLM TNM ON A.POLICY_NBR = TNM.POLICY_NO AND A.POLICY_SEQ = TNM.POLICY_SEQ AND A.ID_DUP = TNM.ID_DUP ");
			sb.append(" 	LEFT JOIN (SELECT T.*, ROW_NUMBER () OVER (PARTITION BY POLICY_NO, POLICY_SEQ, ID_DUP, ITEM, ID_NO ORDER BY CONF_DATE DESC) RN ");
			sb.append(" 	   FROM TBCRM_FBRNY8A0 T WHERE T.SNAP_DATE = (SELECT MAX(SNAP_DATE) FROM TBCRM_FBRNY8A0)) TFS ");
			sb.append(" 	   		ON tns.POLICY_No = TFS.POLICY_NO AND tns.POLICY_SEQ = TFS.POLICY_SEQ AND tns.ID_DUP = TFS.ID_DUP ");
			sb.append(" 	   			AND TNS.INS_ITEM = TFS.ITEM AND TNS.BELONG_ID = TFS.ID_NO AND TFS.RN = 1 ");
			sb.append(" 	INNER JOIN TBPRD_INS B ON TNS.INS_ITEM = B.PRD_ID AND TFS.PREM_YEAR = B.INSPRD_ANNUAL ");
			sb.append(" 	INNER JOIN TBPRD_INS_COMPARED E ON E.KEY_NO = B.KEY_NO ");
			sb.append(" 	INNER JOIN TBPRD_INSDATA_PROD_MAIN C ON C.KEY_NO = E.INSDATA_KEYNO ");
			sb.append(" WHERE A.CONTRACT_STATUS IN (:POLICY_STATUS) ");
			sb.append(" and ( ");

			for (Map<String, Object> inButPolicyMap : inBuyPolicyList) {
				String policyNbrValName = "POLICY_NBR" + String.valueOf(idx);
				String policySeqValName = "POLICY_SEQ" + String.valueOf(idx);
				String idDupValName = "ID_DUP" + String.valueOf(idx);

				sb.append(idx == 0 ? "" : " OR ");
				sb.append(" ( ");
				sb.append(" 	A.POLICY_NBR = :" + policyNbrValName + " AND ");
				sb.append(" 	A.POLICY_SEQ = :" + policySeqValName + " AND ");
				sb.append(" 	A.ID_DUP = :" + idDupValName);
				sb.append(" ) ");

				qc.setObject(policyNbrValName, inButPolicyMap.get("POLICY_NBR"));
				qc.setObject(policySeqValName, inButPolicyMap.get("POLICY_SEQ"));
				qc.setObject(idDupValName, inButPolicyMap.get("ID_DUP"));

				idx++;
			}

			sb.append(" ) ");
			sb.append(" ORDER BY POLICY_NO, MAST_SLA_TYPE DESC, TNS.EFF_DATE ");
			qc.setObject("POLICY_STATUS", getNormalPolicyStatus());
			qc.setQueryString(sb.toString());
			result = dam.exeQuery(qc);
			
			// 針對行內保單單位進行轉換
			// 如果單位一致(庫存 跟 F25B比)就不轉換了  如果單位不一致但不是什麼什麼元 (庫存)也不轉換
			// 庫存顯示的 UNIT
			for(Map<String, Object> resultMap : result) {
				if(ObjectUtils.toString(resultMap.get("UNIT")).equals(resultMap.get("COVERCACULUNITDESC"))) {
					// 單位一致 & 不是金額欄位
				} else {
					if(ObjectUtils.toString(resultMap.get("UNIT")).indexOf("元") == -1) {
						
					} else {
						BigDecimal policyAssureAmt = new GenericMap(resultMap).getBigDecimal("POLICY_ASSURE_AMT");
						String npoldUnit = ObjectUtils.toString(resultMap.get("UNIT"));
						BigDecimal value = BigDecimal.ONE;
						switch(npoldUnit) {
						case "元" : {
							value = BigDecimal.ONE;
							break;
						}
						case "百元" : {
							value = new BigDecimal(100);
						}
						case "佰元" : {
							value = new BigDecimal(100);
							break;
						}
						case "千元" : {
							value = new BigDecimal(1000);
						}
						case "仟元" : {
							value = new BigDecimal(1000);
							break;
						}
						case "萬元" : {
							value = new BigDecimal(10000);
							break;
						}
						default : {
							value = BigDecimal.ONE;
							break;
						}
						}
						BigDecimal covercaculunitdesc = new GenericMap(resultMap).getBigDecimal("COVERCACULUNIT");
						BigDecimal f25bUnit =  covercaculunitdesc.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ONE : covercaculunitdesc;
						resultMap.put("POLICY_ASSURE_AMT", policyAssureAmt.multiply(value).divide(f25bUnit));
						resultMap.put("UNIT", resultMap.get("COVERCACULUNITDESC"));						
					}
				}
			}

			return result;

		} catch (JBranchException e) {
			e.printStackTrace();
		}

		return null;
	}

	public INS810OutputVO getFamailyLst(Object body) {
		INS810InputVO inputVO = (INS810InputVO) body;
		INS810OutputVO outputVO = new INS810OutputVO();
		List<Map<String, Object>> queryList = new ArrayList<Map<String, Object>>();

		Boolean isCallCoverage = inputVO.getIsCallCoverage();

		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF qc = null;
		StringBuffer sb = new StringBuffer();

		// TBSYSPARAMETER 暫時先註解 起來，應該透過 js 取得參數檔 show name mark by sen
		try {
			qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb.append(" SELECT ").append(" 	A.CUST_ID_M CUST_ID, ")
					.append(" 	A.CUST_ID_S RELATION_ID, ")
					.append(" 	C.CUST_NAME RELATION_NAME, ")
					.append(" 	C.BIRTH_DATE RELATION_BIRTHDAY, ");

			if (isCallCoverage) {
				sb.append(" 	DECODE(C.GENDER, '1', 'M', '2', 'F', C.GENDER) RELATION_GENDER, ");
			} else {
				sb.append(" 	C.GENDER RELATION_GENDER, ");
			}

			sb.append(" 	A.REL_TYPE RELATION_TYPE, ")
					.append(" 	A.REL_TYPE , ")
					.append(" 	C.AO_CODE, ")
					.append(" 	C.BRA_NBR, ")
					.append(" 	( ")
					.append(" 		SELECT CREATETIME FROM TBINS_EXAM_AGREE_HIS ")
					.append("   	WHERE CUST_ID = A. CUST_ID_S ")
					.append("   	AND AO_CODE in (:loginAO) ")
					.append("   	ORDER BY CREATETIME DESC FETCH FIRST ROWS ONLY ")
					.append("	) REPORT_DATE ")
					.append(" FROM TBCRM_CUST_REL A, ")
					.append(" TBCRM_CUST_MAST B, ")
					.append(" TBCRM_CUST_MAST C ")
					.append(" WHERE A.CUST_ID_M = B.CUST_ID ")
					.append(" AND A.CUST_ID_S = C.CUST_ID ")
					.append(" AND A.CUST_ID_M = :custId ")
					.append(" AND ( (B.AO_CODE IS NULL AND B.BRA_NBR = :loginBraNbr) or ")
					.append(" (B.AO_CODE IS NOT NULL AND B.AO_CODE in (:loginAO)) ) ")
					.append(" AND ( (C.AO_CODE IS NULL AND C.BRA_NBR = :loginBraNbr) or ")
					.append(" (C.AO_CODE IS NOT NULL AND C.AO_CODE in (:loginAO)) ) ")
					.append(" ORDER BY A.REL_TYPE ");

			// qc.setObject("loginAOCode", inputVO.getLoginAOCode().get(0));
			qc.setObject("custId", inputVO.getCUST_ID());
			qc.setObject("loginBraNbr", inputVO.getLoginBranch());
			qc.setObject("loginAO", inputVO.getLoginAOCode());
			qc.setQueryString(sb.toString());
			queryList = dam.exeQuery(qc);

			outputVO.setGenealogyList(getGenealogyList(queryList));
		} catch (DAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JBranchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return outputVO;
	}

	/** 親屬關係代碼轉 */
	public List<Map<String, Object>> getGenealogyList(
			List<Map<String, Object>> famailyList) {
		int idx = 2;

		for (Map<String, Object> map : famailyList) {
			switch (String.valueOf(map.get("REL_TYPE"))) {
			case "00":// 本人
				map.put("RELATIONCODE", "0");
				break;
			case "07":// 配偶
				map.put("RELATIONCODE", "1");
				break;
			default:
				map.put("RELATIONCODE", String.valueOf(idx));
				idx++;
				break;
			}
		}

		return famailyList;
	}

	public Map<String, String> getGenealogyMap(
			List<Map<String, Object>> famailyList) {
		Map<String, String> relMap = new HashMap<String, String>();

		// 篩選親屬關係代碼
		if (CollectionUtils.isNotEmpty(famailyList)) {
			int idx = 2;

			for (Map<String, Object> famaily : famailyList) {
				GenericMap familyGmap = new GenericMap(famaily);
				String rCustId = familyGmap.getNotNullStr("REL_CUST_ID");
				String relType = familyGmap.getNotNullStr("REL_TYPE");

				switch (relType) {
				case "00":// 本人
					relMap.put(rCustId, "0");
					break;
				case "07":// 配偶
					relMap.put(rCustId, "1");
					break;
				default:
					relMap.put(rCustId, String.valueOf(idx));
					idx++;
					break;
				}
			}
		}

		return relMap;
	}

	/** 行外保單轉換欄位同時對資料做加工 */
	public List<Map<String, Object>> reNameOutBuyForPolicyMap(
			List<Map<String, Object>> outBuyList,
			InOutBuyDataProcess[] inOutBuyDataProcess) throws JBranchException {

		List<Map<String, Object>> reOutBuyList = new ArrayList<Map<String, Object>>();

		if (CollectionUtils.isEmpty(outBuyList)) {
			return reOutBuyList;
		}

		for (Map<String, Object> map : outBuyList) {
			logger.debug("befor change for ins810 : " + map);
			Map<String, Object> outputmap = new HashMap<String, Object>();
			Set<String> keys = map.keySet();

			for (String key : keys) {
				Object value = map.get(key);

				// 繳別
				if ("PAYTYPE".equals(key)) {
					outputmap.put("PAY_TYPE", value);
					outputmap.put("PAYTYPE", value);
				}
				// 性別
				else if ("GENDER".equals(key)) {
					outputmap.put(
							"INSURED_GENDER",
							"1".equals(map.get("GENDER")) ? "M" : "2"
									.equals(map.get("GENDER")) ? "F" : "");
				}
				// 生日
				else if ("BIRTH_DATE".equals(key)) {
					outputmap.put("BIRTH_DATE", value);
					outputmap.put("INSURED_BIRTHDAY", value);
				} else {
					outputmap.put(key, value);
				}
			}

			logger.debug("after change for ins810 : " + outputmap);
			reOutBuyList.add(outputmap);
		}

		// 資料加工
		if (!ArrayUtils.isEmpty(inOutBuyDataProcess)) {
			doOutBuyDataProcess(inOutBuyDataProcess, reOutBuyList);
		}

		return reOutBuyList;
	}

	/** 行內保單轉換欄位同時對資料做加工 */
	@Override
	public List<Map<String, Object>> reNameInBuyForPolicyMap(
			List<Map<String, Object>> inBuyList,
			InOutBuyDataProcess[] inOutBuyDataProcess) throws JBranchException {

		List<Map<String, Object>> reInBuyList = new ArrayList<Map<String, Object>>();

		if (CollectionUtils.isEmpty(inBuyList)) {
			return reInBuyList;
		}

		// 不做 "COVERYEAR_SEL","POLICY_ASSURE_AMT" 這兩個額外加工
		String[] sameKey = new String[] { "INSURED_ID", "INSURED_NAME",
				"PAY_TYPE", "COM_NAME", "INOUT", "INSYEARFEE",
				"LOCAL_INSYEARFEE", "INSURED_BIRTHDAY", "ITEM_A", "ITEM_Y",
				"ITEM_K", "ITEM_P", "ITEM_U", "ITEM_X", "LIST_Y", "LIST_A",
				"LIST_X", "LIST_K", "LIST_P", "LIST_U", "LIST_E", "LIST_O",
				"PAYMENTYEAR_SEL", "INS_TYPE", "RATE", "BENEFICIARY_YN"
		// ,"COVERYEAR_SEL"
		};

		for (Map<String, Object> map : inBuyList) {
			Map<String, Object> tmpMap = new HashMap<String, Object>();
			tmpMap.put("PRD_KEYNO", map.get("KEY_NO"));
			tmpMap.put("INSSEQ", map.get("POLICY_NO"));// 行內保單無保單自訂序號，故抓POLICY_NO
			tmpMap.put("PRD_NAME", map.get("POLICY_SIMP_NAME"));// 商品名稱(簡稱)
			tmpMap.put("PRD_ID", map.get("INSPRD_ID"));// 險種代號
			tmpMap.put("IS_MAIN",
					ObjectUtils.toString(map.get("MAST_SLA_TYPE")));// 主約 = Y ,
																	// 附約 = N
			tmpMap.put("COVERCACULUNITDESC", map.get("UNIT"));// 換算單位
			tmpMap.put("CURR_CD", map.get("CRCY_TYPE"));// 幣別
			tmpMap.put("EFFECTED_DATE", map.get("POLICY_ACTIVE_DATE"));// 險種保障生效日
			tmpMap.put("STATUS", map.get("CONTRACT_STATUS"));// 契約狀態
			tmpMap.put("POLICY_NBR", map.get("POLICY_NO"));// 保單號碼
			// tmpMap.put("INSUREDAMT",
			// Double.valueOf(map.get("POLICY_ASSURE_AMT").toString()));//換算保額
			tmpMap.put("COM_ID", "209");// 公司代號
			tmpMap.put(
					"INSURED_GENDER",
					"1".equals(map.get("INSURED_GENDER")) ? "M" : "2"
							.equals(map.get("INSURED_GENDER")) ? "F" : "");

			for (String key : sameKey) {
				tmpMap.put(key, map.get(key));// 繳別
			}
			processingItems(map, tmpMap);
			reInBuyList.add(tmpMap);
		}

		// 資料加工
		if (!ArrayUtils.isEmpty(inOutBuyDataProcess)) {
			doInBuyDataProcess(inOutBuyDataProcess, reInBuyList);
		}

		processingPaymentyearSel(reInBuyList);

		return reInBuyList;
	}

	@Override
	public Map<String, BigDecimal> queryRefExcRate() throws JBranchException {
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF qc = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		String queryStr = "select CUR_COD CUR , BUY_RATE  BUY_RATE , SEL_RATE SELL_RATE from TBPMS_IQ053 ";
		queryStr += " WHERE mtn_date = (select max(mtn_date) from tbpms_iq053)";

		List<Map<String, Object>> result = dam.exeQuery(qc
				.setQueryString(queryStr));
		Map<String, BigDecimal> rateMap = new HashMap<String, BigDecimal>();

		if (CollectionUtils.isEmpty(result)) {
			return rateMap;
		}

		for (Map refExc : result) {
			BigDecimal buyRate = refExc.get("BUY_RATE") == null ? BigDecimal.ZERO
					: (BigDecimal) refExc.get("BUY_RATE");
			BigDecimal selRate = refExc.get("SELL_RATE") == null ? BigDecimal.ZERO
					: (BigDecimal) refExc.get("SELL_RATE");
			String cur = ObjectUtils.toString(refExc.get("CUR"));

			if (StringUtils.isBlank(cur)) {
				return rateMap;
			}

			buyRate = buyRate.add(selRate);

			if (buyRate.doubleValue() != 0) {
				buyRate = buyRate.divide(BigDecimal.valueOf(2));
			} else {
				buyRate = BigDecimal.valueOf(1);
			}

			rateMap.put(ObjectUtils.toString(refExc.get("CUR")), buyRate);
		}

		return rateMap;
	}

	@Override
	public List queryInBuyPolicyList(List<Map<String, Object>> inOutBuyFamilyList) throws JBranchException {
		List<Map<String, Object>> resultList = null;
		List<Map<String, Object>> insseqList = new ArrayList<Map<String, Object>>();
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		int idx = 0;

		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT DISTINCT ");
		sb.append(" 	A.POLICY_NBR, ");
		sb.append(" 	A.POLICY_SEQ, ");
		sb.append(" 	A.ID_DUP ");
		sb.append(" FROM TBCRM_AST_INS_MAST A , ");
		sb.append("      TBCRM_CUST_MAST B , ");
		sb.append("      TBCRM_CUST_MAST C ");
		sb.append(" WHERE A.CUST_ID = B.CUST_ID ");
		sb.append(" AND A.INS_ID = C.CUST_ID ");
		sb.append(" AND ( ");

		for (Map<String, Object> inOutBuyFamily : inOutBuyFamilyList) {
			if (idx > 0) {
				sb.append(" OR ");
			}

			sb.append(" ( ");

			// 客戶id
			sb.append(" A.INS_ID = :CUST_ID" + idx);
			queryCondition.setObject("CUST_ID" + idx, inOutBuyFamily.get("CUST_ID"));

			if (inOutBuyFamily.get("AO") instanceof List) {
				sb.append(" AND (( ");
				sb.append(" 	(B.AO_CODE IS NULL AND B.BRA_NBR = :BRANCH" + idx + ") or ");
				sb.append(" 	(B.AO_CODE IS NOT NULL AND B.AO_CODE in(:AO" + idx + ")) ");
				sb.append(" ) ");
				sb.append(" OR ( ");
				sb.append(" 	(C.AO_CODE IS NULL AND C.BRA_NBR = :BRANCH" + idx + ") or ");
				sb.append(" 	(C.AO_CODE IS NOT NULL AND C.AO_CODE in(:AO" + idx + ")) ");
				sb.append(" ))) ");
			} else {
				sb.append(" AND ( ");
				sb.append(" 	(B.AO_CODE IS NULL AND B.BRA_NBR = :BRANCH" + idx + ") or ");
				sb.append(" 	(B.AO_CODE IS NOT NULL AND B.AO_CODE =:AO" + idx + ") ");
				sb.append(" ) ");
				sb.append(" OR ( ");
				sb.append(" 	(C.AO_CODE IS NULL AND C.BRA_NBR = :BRANCH" + idx + ") or ");
				sb.append(" 	(C.AO_CODE IS NOT NULL AND C.AO_CODE =:AO" + idx + ") ");
				sb.append(" )) ");
			}

			queryCondition.setObject("AO" + idx, inOutBuyFamily.get("AO"));
			queryCondition.setObject("BRANCH" + idx, inOutBuyFamily.get("BRANCH"));

			idx++;
		}
		sb.append(" ) ");
		queryCondition.setQueryString(sb.toString());
		resultList = dam.exeQuery(queryCondition);

		return resultList;
	}

	/**
	 * 富邦行內保單狀態定義參數
	 * 
	 * @return
	 * @throws DAOException
	 * @throws JBranchException
	 */
	private List<String> getNormalPolicyStatus() throws DAOException,
			JBranchException {
		DataAccessManager dam = null;
		QueryConditionIF qc = null;
		List<String> policyStatus = new ArrayList<String>();
		StringBuffer sbf = new StringBuffer(
				"SELECT PARAM_CODE FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'INS.NORMAL_POLICY_STATUS'");

		dam = getDataAccessManager();
		qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		qc.setQueryString(sbf.toString());
		List<Map<String, Object>> result = dam.exeQuery(qc);
		Map<String, Object> resultMap = result.get(0);
		policyStatus = Arrays.asList(((String) resultMap.get("PARAM_CODE"))
				.split(","));
		return policyStatus;
	}

	/**
	 * 加工 保障年期、型別、保額、對象
	 * 
	 * @param map
	 * @param tmpMap
	 */
	private void processingItems(Map<String, Object> map,
			Map<String, Object> tmpMap) {
		Map<String, String> itemMap = new HashMap<String, String>();
		itemMap.put("ITEM_A", "COVERYEAR_SEL");

		for (Entry<String, String> entry : itemMap.entrySet()) {
			if (org.springframework.util.StringUtils.isEmpty(map.get(entry
					.getKey()))) {
				tmpMap.put(entry.getValue(), null);
			} else {
				String yearSel = null;
				if (ObjectUtils.toString(map.get(entry.getKey())).split(",").length == 1) {
					yearSel = ObjectUtils.toString(map.get(entry.getKey()))
							.split(",")[0];
				} else {
					yearSel = (String) InsjlbUtils.findSimilarIntVal(
							ObjectUtils.toString(map.get(entry.getKey())),
							ObjectUtils.toString(map.get(entry.getValue())),
							",", true).get("item");
				}
				tmpMap.put(entry.getValue(), yearSel);
			}
		}

		// 4.檢查型別 (ITEM_K) 6.檢查對象 (ITEM_O)
		itemMap.clear();
		itemMap.put("ITEM_K", "KIND_SEL");
		itemMap.put("LIST_O", "INSURED_OBJECT");

		for (Entry<String, String> entry : itemMap.entrySet()) {
			if (org.springframework.util.StringUtils.isEmpty(map.get(entry
					.getKey()))) {
				tmpMap.put(entry.getValue(), null);
			} else {
				String kindSel = null;
				if (ObjectUtils.toString(map.get(entry.getKey())).split(",").length > 0) {
					kindSel = ObjectUtils.toString(map.get(entry.getKey()))
							.split(",")[0];
				}
				tmpMap.put(entry.getValue(), kindSel);
			}
		}

		// 5.檢查保額 (ITEM_U/ITEM_P)
		String useWhich = !org.springframework.util.StringUtils.isEmpty(map
				.get("ITEM_P")) ? "ITEM_P"
				: (!org.springframework.util.StringUtils.isEmpty(map
						.get("ITEM_U")) ? "ITEM_U" : null);
		if (StringUtils.isNotEmpty(useWhich)) {
			// 屬於 useWhich
			String upqtySel = null;
			
			StringBuffer itemPsSBF = new StringBuffer();
//			String test = "1,2,3,4,5,6,7";
//			String[] firstParser = test.split(",");
			String[] firstParser = ObjectUtils.toString(map.get(useWhich)).split(",");
			for(String firstValue:firstParser) {
				String[] secondParser = firstValue.split("-");
				if(secondParser.length == 1) {
					itemPsSBF.append((itemPsSBF.length() == 0 ? "" : ",") + secondParser[0]);
				} else {
					for(int value = Integer.parseInt(secondParser[0]); value<=Integer.parseInt(secondParser[1]); value++)
						itemPsSBF.append((itemPsSBF.length() == 0 ? "" : ",") + value);
				}
			}
			String[] itemPs = itemPsSBF.toString().split(",");
			
			
			if (itemPs.length == 1) {
				upqtySel = itemPs[0];
			} else if (Arrays.asList(itemPs).contains(
					ObjectUtils.toString(map.get("POLICY_ASSURE_AMT")))) {
				upqtySel = ObjectUtils.toString(map.get("POLICY_ASSURE_AMT"));
			} else {
				upqtySel = (String) InsjlbUtils.findSimilarIntVal(
						ObjectUtils.toString(map.get(useWhich)),
						ObjectUtils.toString(map.get("POLICY_ASSURE_AMT")),
						",", true).get("item");
			}
			tmpMap.put("UPQTY_SEL", upqtySel);// 換算保額
			
			if("ITEM_U".equals(useWhich)) {
				tmpMap.put("UPTYPE", "01"); // ITEM_U(單位) 有值，UPTYPE 就存 01
			}else if("ITEM_P".equals(useWhich)){
				tmpMap.put("UPTYPE", "02"); // ITEM_P(單位) 有值，UPTYPE 就存 02
			}
		} else {
			tmpMap.put("INSUREDAMT",
					Double.valueOf(map.get("POLICY_ASSURE_AMT").toString()));// 換算保額
		}
	}

	private void processingPaymentyearSel(List<Map<String, Object>> reInBuyList) {

		// IF 如果 PAYMENTYEAR_SEL = 99 或 PAY_TYPE = 0
		// 直接給 01
		// ELSE 如果不是 99
		// 就跑正常邏輯
		for (Map<String, Object> reInBuyMap : reInBuyList) {
			if (org.springframework.util.StringUtils.isEmpty(reInBuyMap
					.get("ITEM_Y"))) {
				reInBuyMap.put("PAYMENTYEAR_SEL", null);
			} else {
				String yearSel = null;
				if ("99".equals(ObjectUtils.toString(reInBuyMap.get("PAYMENTYEAR_SEL"))) || 
						"0".equals(ObjectUtils.toString(reInBuyMap.get("PAY_TYPE")))) {
					yearSel = "01";
				} else {
					if (ObjectUtils.toString(reInBuyMap.get("ITEM_Y")).split(
							",").length == 1) {
						yearSel = ObjectUtils
								.toString(reInBuyMap.get("ITEM_Y")).split(",")[0];
					} else {
						yearSel = (String) InsjlbUtils.findSimilarIntVal(
								ObjectUtils.toString(reInBuyMap.get("ITEM_Y")),
								ObjectUtils.toString(reInBuyMap
										.get("PAYMENTYEAR_SEL")), ",", true)
								.get("item");
					}
				}
				reInBuyMap.put("PAYMENTYEAR_SEL", yearSel);
			}
		}
	}

	/**
	 * ===================================== 商品建議 實作
	 * =================================================
	 */
	// for web 調用服務 - 商品建議
	@Override
	public void getSuggestPrd(Object body, IPrimitiveMap<Object> header)
			throws DAOException, JBranchException {
		DataAccessManager dam = getDataAccessManager();
		PolicySuggestInputVO inputVO = (PolicySuggestInputVO) body;
		PolicySuggestOutputVO outputVO = new PolicySuggestOutputVO();
		outputVO.setSuggestPrdList(getSuggestDataParser(inputVO, dam));
		sendRtnObject(outputVO);
	}

	// for web 調用服務 - 取費率 與 匯率
	@Override
	public void getPremAndExchangeRate(Object body, IPrimitiveMap<Object> header)
			throws DAOException, JBranchException {
		DataAccessManager dam = this.getDataAccessManager();
		PolicySuggestInputVO inputVO = (PolicySuggestInputVO) body;
		this.sendRtnObject(new PolicySuggestOutputVO(getPremRate(inputVO, dam),
				queryRefExcRate()));
	}

	// for web 資料處理
	@Override
	public List<Map<String, Object>> getSuggestDataParser(
			PolicySuggestInputVO inputVO, DataAccessManager dam)
			throws DAOException, JBranchException {
		List<Map<String, Object>> suggestPrdList = getSuggestListDatas(
				getSuggestPrdSql(ObjectUtils.toString(inputVO.getCurrCD()),
						inputVO.getInsAge(),inputVO.getEstate()), inputVO, dam);
		for (Map<String, Object> currentMap : suggestPrdList) {
			List<Map<String, Object>> insprdAnnualList = new ArrayList<Map<String, Object>>();
			reCombianMap(currentMap, insprdAnnualList);
			currentMap.put("choic", "N");// checkbox
			currentMap.put("insprd_annualList", insprdAnnualList); // 整理過的繳費年前 &
																	// 上下限相關資料
		}
		return suggestPrdList;
	}

	// for web 資料處理 2
	@Override
	public void reCombianMap(Map<String, Object> currentMap,
			List<Map<String, Object>> insprdAnnualList) {
		String[] keyNo = String.valueOf(currentMap.get("KEY_NO")).split(",");
		String[] insprdAnnual = String.valueOf(currentMap.get("INSPRD_ANNUAL")).split(",");
		String[] cvrgRatio = String.valueOf(currentMap.get("CVRG_RATIO")).split(",");
		String[] policyAmtDistance = String.valueOf(currentMap.get("POLICY_AMT_DISTANCE")).split(",");
		String[] policyAmtMin = String.valueOf(currentMap.get("POLICY_AMT_MIN")).split(",");
		String[] policyAmtMax = String.valueOf(currentMap.get("POLICY_AMT_MAX")).split(",");

		String[] earnedYear = ObjectUtils.toString(currentMap.get("EARNED_YEAR")).split(",");
		String[] earnedRatio = ObjectUtils.toString(currentMap.get("EARNED_RATIO")).split(",");
		String[] earnedCalWay = ObjectUtils.toString(currentMap.get("EARNED_CAL_WAY")).split(",");
		
		String[] guaranteeAnnual = ObjectUtils.toString(currentMap.get("GUARANTEE_ANNUAL")).split(",");
		String[] insdataKeyno = ObjectUtils.toString(currentMap.get("INSDATA_KEYNO")).split(",");
		for (int i = 0; i < insprdAnnual.length; i++) {
			Map<String, Object> insprdAnnualMap = new HashMap<String, Object>();
			insprdAnnualMap.put("LABEL", insprdAnnual[i]);
			insprdAnnualMap.put("DATA", insprdAnnual[i]);
			insprdAnnualMap.put("KEY_NO", keyNo[i]); // 保險主檔主鍵
			insprdAnnualMap.put("CVRG_RATIO", cvrgRatio[i]); // 保障比
			insprdAnnualMap.put("POLICY_AMT_DISTANCE", policyAmtDistance[i]); // 保額累加級距
			insprdAnnualMap.put("POLICY_AMT_MIN", policyAmtMin[i]); // 保額下限
			insprdAnnualMap.put("POLICY_AMT_MAX", policyAmtMax[i]); // 保額上限

			insprdAnnualMap.put("EARNED_YEAR", earnedYear != null && (earnedYear.length-1 >= i) ?earnedYear[i] : null); // 滿期金計算類型
			insprdAnnualMap.put("EARNED_RATIO", earnedRatio != null && (earnedRatio.length-1 >= i)  ?earnedRatio[i] : null); // 滿期金比
			insprdAnnualMap.put("EARNED_CAL_WAY", earnedCalWay != null && (earnedCalWay.length-1 >= i)  ?earnedCalWay[i] : null); // 滿期金年度
			insprdAnnualMap.put("GUARANTEE_ANNUAL", guaranteeAnnual != null && (guaranteeAnnual.length-1 >= i)  ?guaranteeAnnual[i] : null); // 保障年期
			
			insprdAnnualMap.put("INSDATA_KEYNO", insdataKeyno[i]); // 保險主檔主鍵
			insprdAnnualList.add(insprdAnnualMap);
		}
	}

	// 共用查出推薦商品
	@Override
	public List<Map<String, Object>> getSuggestListDatas(String sql,
			PolicySuggestInputVO inputVO, DataAccessManager dam)
			throws DAOException, JBranchException {
		// 取得商品建議前先取得 planNo
		inputVO.setParaNo(inputVO.getParaNo() != null ? inputVO.getParaNo()
				: getSingleMapValue(getPlanNo(inputVO, dam), "PARA_NO"));
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		qc.setQueryString(getSuggestPrdSql(ObjectUtils.toString(inputVO.getCurrCD()), inputVO.getInsAge(),inputVO.getEstate()));
		qc.setObject("para_no", inputVO.getParaNo());
		// qc.setObject("age", inputVO.getInsAge());
		return dam.exeQuery(qc);
	}

	// 共用查出 planNo
	@Override
	public List<Map<String, Object>> getPlanNo(PolicySuggestInputVO inputVO,
			DataAccessManager dam) throws DAOException, JBranchException {
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		qc.setQueryString(getPlanNoSql());
		qc.setObject("PLAN_TYPE", inputVO.getParaType()); // 傳入參數
		return dam.exeQuery(qc);
	}

	// 共用查出 premRate(費率)
	@Override
	public BigDecimal getPremRate(PolicySuggestInputVO inputVO,
			DataAccessManager dam) throws DAOException, JBranchException {
		QueryConditionIF qc = dam.getQueryCondition(dam.QUERY_LANGUAGE_TYPE_VAR_SQL);
		qc.setObject("gender", (StringUtils.equals("1", inputVO.getGender()) ? "M" : "F"));
		qc.setObject("age", inputVO.getAge());
		qc.setObject("prdID", inputVO.getInsPrdId());
		qc.setObject("annual", inputVO.getAnnual());
		qc.setObject("currCD", inputVO.getCurrCD());
		qc.setQueryString(getPremRateSql());
		List<Map<String, Object>> rate = dam.exeQuery(qc);
		BigDecimal premRate = null;
		return rate.size() > 0 ? premRate = (BigDecimal) rate.get(0).get("PREM_RATE") : premRate;
	}

	// 統一 SQL 查推薦商品
	@Override
	public String getSuggestPrdSql(String currCD, String insage, String estate) {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT PRD_ID, INSPRD_NAME,  TTL_FLAG, CURR_CD, SUGGEST_TYPE, PRD_UNIT, ");
		sb.append("LISTAGG(KEY_NO, ',') WITHIN GROUP (ORDER BY INSPRD_ANNUAL) AS KEY_NO, ");
		sb.append("LISTAGG(INSPRD_ANNUAL,',') WITHIN GROUP (ORDER BY INSPRD_ANNUAL) as INSPRD_ANNUAL, ");
		sb.append("LISTAGG(CVRG_RATIO, ',') WITHIN GROUP (ORDER BY INSPRD_ANNUAL) AS CVRG_RATIO, ");
		sb.append("LISTAGG(POLICY_AMT_DISTANCE, ',') WITHIN  GROUP (ORDER BY INSPRD_ANNUAL) AS POLICY_AMT_DISTANCE, ");
		sb.append("LISTAGG(POLICY_AMT_MIN, ',') WITHIN GROUP (ORDER BY INSPRD_ANNUAL) AS POLICY_AMT_MIN, ");
		sb.append("LISTAGG(POLICY_AMT_MAX, ',') WITHIN GROUP (ORDER BY INSPRD_ANNUAL) AS POLICY_AMT_MAX, ");
		sb.append("LISTAGG(ESTATE_PLAN, ',') WITHIN GROUP (ORDER BY INSPRD_ANNUAL) AS ESTATE_PLAN, ");
		sb.append("LISTAGG(EARNED_YEAR, ',') WITHIN GROUP (ORDER BY INSPRD_ANNUAL) AS EARNED_YEAR, ");
		sb.append("LISTAGG(EARNED_RATIO, ',') WITHIN GROUP (ORDER BY INSPRD_ANNUAL) AS EARNED_RATIO, ");
		sb.append("LISTAGG(EARNED_CAL_WAY, ',') WITHIN GROUP (ORDER BY INSPRD_ANNUAL) AS EARNED_CAL_WAY, ");
		sb.append("LISTAGG(INSDATA_KEYNO, ',') WITHIN GROUP (ORDER BY INSPRD_ANNUAL) AS INSDATA_KEYNO, ");
		sb.append("LISTAGG(GUARANTEE_ANNUAL, ',') WITHIN GROUP (ORDER BY INSPRD_ANNUAL) AS GUARANTEE_ANNUAL ");
		sb.append("FROM VWPRD_INS_SUGGEST a ");
		sb.append("WHERE A.PARA_NO = :para_no ");

		if (StringUtils.isNotBlank(insage)) {
			sb.append("AND NVL(" + insage
					+ ",MIN_AGE) BETWEEN MIN_AGE AND MAX_AGE ");
		}
		// qc.setObject("age", insage);
		// INS400系列 判斷台外幣
		if (StringUtils.isNotBlank(currCD)) {
			if (StringUtils.equals("1", currCD)) {
				sb.append("AND CURR_CD = 'TWD' ");
			} else if (StringUtils.equals("2", currCD)) {
				sb.append("AND CURR_CD <> 'TWD' ");
			}
		}
		
		if(StringUtils.isNotBlank(estate)) {
			if(StringUtils.equals("Y", estate)) {
				sb.append("AND ESTATE_PLAN = 'Y' ");
			} else if(StringUtils.equals("N", estate)) {
				sb.append(" AND ESTATE_PLAN = 'N' ");
			}
		}
		sb.append("group by prd_id, INSPRD_NAME, TTL_FLAG, CURR_CD, SUGGEST_TYPE, PRD_UNIT order by prd_id, INSPRD_ANNUAL ");
		return sb.toString();
	}

	// 統一 SQL 查 planNo
	@Override
	public String getPlanNoSql() {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT * FROM TBINS_PARA_HEADER A WHERE PARA_TYPE = :PLAN_TYPE ");
		sb.append("AND STATUS = 'A' ");
		sb.append("AND trunc(a.EFFECT_DATE) <= trunc(SYSDATE) ");
		sb.append("AND nvl(trunc(a.EXPIRY_DATE), SYSDATE) >= trunc(SYSDATE) ");
		sb.append("ORDER BY EFFECT_DATE DESC ");
		sb.append("FETCH FIRST 1 ROWS ONLY ");
		return sb.toString();
	}

	// 統一 SQL 查 費率表
	@Override
	public String getPremRateSql() {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT RAT.PREM_RATE ");
		sb.append("FROM TBPRD_INS_RATE RAT ");
		sb.append("LEFT JOIN TBPRD_INS	INS ON INS.KEY_NO = RAT.F_KEY_NO ");
		sb.append("WHERE 1 = 1");
		sb.append("AND NVL(RAT.SEX, :gender) = :gender ");
		sb.append("AND RAT.AGE = :age ");
		sb.append("AND INS.PRD_ID = :prdID ");
		sb.append("AND INS.INSPRD_ANNUAL = :annual ");
		sb.append("AND INS.CURR_CD = :currCD ");
		sb.append("ORDER BY RAT.PAY_TYPE ");
		sb.append("FETCH FIRST 1 ROWS ONLY ");
		return sb.toString();
	}
	
	//共用查出table資訊的list
	public List<Map<String, Object>> getDBList(DataAccessManager dam, String tableName, String[] columns, Map<String, Object> conditions) throws DAOException, JBranchException {
		QueryConditionIF queryCondition = null;
		StringBuilder sb = new StringBuilder();
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb.append(" SELECT ");
		int i = 0;
		for(String column : columns) {
			if(i == 0) {
				sb.append(column + " ");
			} else {
				sb.append(", " + column + " ");
			}
			i+=1;
		  }
		sb.append(" FROM ").append(tableName);
		sb.append(" WHERE 1=1 ");
		  
		for(Entry<String, Object> entry : conditions.entrySet()) {
			sb.append(" AND " + entry.getKey() + " = :" +entry.getKey() + " ");
			queryCondition.setObject(entry.getKey(), entry.getValue());
		  }
		queryCondition.setQueryString(sb.toString());
		
		return dam.exeQuery(queryCondition);
	}

	// ======================== 小方法 ========================
	/**
	 * 取得 回傳的 list map 中單一一筆的回傳資料
	 * 
	 * @param result
	 *            SQL 回來後的結果
	 * @param getPropertyName
	 *            要取的欄位名稱
	 * @return 取到的值
	 */
	public String getSingleMapValue(List<Map<String, Object>> result,
			String getPropertyName) {
		String returnValue = "";
		if (CollectionUtils.isNotEmpty(result)) {
			returnValue = ObjectUtils.toString(result.get(0).get(
					getPropertyName));
		}
		return returnValue;
	}
	
}
