package com.systex.jbranch.app.server.fps.pms429;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.app.server.fps.crm3102.CRM3102InputVO;
import com.systex.jbranch.app.server.fps.crm3102.CRM3102OutputVO;
import com.systex.jbranch.app.server.fps.oth001.OTH001;
import com.systex.jbranch.platform.common.util.PlatformContext;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBPMS_ROTATION_MAINVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("pms429")
@Scope("request")
public class PMS429 extends FubonWmsBizLogic {

	/**下拉式選單:加強管控專案**/
	public void getAllPRJ(Object body, IPrimitiveMap header) throws JBranchException {
		PMS429InputVO inputVO = (PMS429InputVO) body;
		PMS429OutputVO return_VO = new PMS429OutputVO();
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT PRJ_ID, PRJ_NAME, PRJ_STATUS, PRJ_TYPE FROM TBCRM_TRS_PRJ_MAST ");
		sql.append(" WHERE PRJ_TYPE = '2' "); 
		//分行管理科可看到所有加強管控專案
		if(StringUtils.isBlank(inputVO.getPrivilegeId()) || !inputVO.getPrivilegeId().matches("043|044")) {
			if((StringUtils.isNotBlank(inputVO.getMemLoginFlag()) && inputVO.getMemLoginFlag().matches("UHRM")) ||
					StringUtils.equals("Y", inputVO.getUHRMMGR_FLAG())) {
				//高端專案
				sql.append(" AND PRJ_SUB_TYPE = '2' ");
			} else {
				//一般理專專案
				sql.append(" AND PRJ_SUB_TYPE = '1' ");
			}
		}
		sql.append("ORDER BY PRJ_ID DESC ");
		queryCondition.setQueryString(sql.toString());
		List list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		sendRtnObject(return_VO);
	}
	
	public void queryData(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		PMS429OutputVO outputVO = new PMS429OutputVO();
		outputVO = this.queryData(body);

		sendRtnObject(outputVO);
	}

	private PMS429OutputVO queryData(Object body) throws JBranchException, ParseException {
		initUUID();

		PMS429InputVO inputVO = (PMS429InputVO) body;
		PMS429OutputVO outputVO = new PMS429OutputVO();
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		String roleID = inputVO.getLoginRole();
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> fcMap = xmlInfo.doGetVariable("FUBONSYS.FC_ROLE", FormatHelper.FORMAT_2); // 理專
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

		if (StringUtils.isBlank(inputVO.getPRJ_ID())) {//專案代碼為必輸欄位
			throw new APException("請先選擇專案");
		}
		
//		try {
			sql.append(" SELECT N.DEATH_YN, N.COMPLAIN_YN, N.COMM_RS_YN, N.COMM_NS_YN ");
			sql.append(" , A.STATEMENT_DATE "); // 帳單基準日
			sql.append(" , ORG.REGION_CENTER_ID "); // 業務處代碼
			sql.append(" , ORG.REGION_CENTER_NAME "); // 業務處名稱
			sql.append(" , ORG.BRANCH_AREA_ID "); // 營運區代碼
			sql.append(" , ORG.BRANCH_AREA_NAME "); // 營運區名稱
			sql.append(" , ORG.BRANCH_NBR "); // 歸屬行代碼
			sql.append(" , ORG.BRANCH_NAME "); // 歸屬行名稱
			sql.append(" , A.SM_BRANCH_NBR"); // 對帳單寄送分行代碼
			sql.append(" , ORG2.BRANCH_NAME AS SM_BRANCH_NAME  "); // 對帳單寄送分行名稱
			sql.append(" , A.CUST_ID "); // 客戶ID
			sql.append(" , CUST.CUST_NAME "); // 客戶姓名
			sql.append(" , AO.EMP_NAME "); // 服務理專
			sql.append(" , CUST.AO_CODE "); // AO CODE
			sql.append(" , A.STATEMENT_SEND_TYPE "); // 對帳單寄送方式 PARAM_TYPE =
														// 'PMS.STATEMENT_SEND_TYPE'
			sql.append(" , A.SEND_DATE "); // 電子/實體確認單寄送日期
			sql.append(" , A.RECEIVE_DATE "); // 回函日期
			sql.append(" , A.RTN_STATUS_AST "); // 回函結果帳務相符
			sql.append(" , A.RTN_STATUS_NP "); // 回函結果理專是否有不當情事
			sql.append(" , A.MAIL_CONTACT_DATE "); // 實體確認單聯絡單日期
			sql.append(" , A.MAIL_REPLY_DATE "); // 實體確認單聯絡單回覆日期
			sql.append(" , (CASE WHEN A.AO_CONTACT_DATE IS NULL THEN 'N' ELSE 'Y' END) AS AO_CONTACT_YN "); // 自取確認單理專是否已連繫
																											// Y:已連繫
																											// N:未聯繫
			sql.append(" , A.AO_CONTACT_DATE "); // 自取確認單理專聯繫日期
			sql.append(" , A.AO_CONTACT_EMP_ID "); // 自取確認單理專聯繫員編
			sql.append(" , A.BRN_MGM_YN "); // 異常通報(分行主管回覆是否正常 Y:正常 N:異常)
			sql.append(" , A.CONTACT_STATUS "); // 錄音訪談紀錄
			sql.append(" , A.REC_DATE "); // 錄音日期
			sql.append(" , A.REC_SEQ "); // 錄音序號
			sql.append(" , A.CONTACT_MEMO "); // 分行主管聯繫內容
			sql.append(" , A.CONTACT_EMP_ID "); // 分行主管聯繫員編
			sql.append(" , A.RM_DIFF_YN "); // 確認單寄送前後RM有差異
			sql.append(" , TO_CHAR(A.KEY_NO) AS KEY_NO "); // 主鍵序號
			sql.append(" , A.PROCESS_STATUS ");
			sql.append(" , A.CUST_PROCE_STATUS, ");
			sql.append(" CASE WHEN M.PRJ_ID IS NOT NULL THEN ( ");
			sql.append(" CASE WHEN TRUNC(SYSDATE) BETWEEN TRUNC(M.PRJ_DATE_BGN) AND TRUNC(M.PRJ_DATE_END) THEN 'Y' ELSE 'N' END ");			
			sql.append(" ) ELSE 'N' END AS EDIT_YN ");
			sql.append(" , Y.EMP_ID || ' - ' || NVL(YM.EMP_NAME, '') AS EMP_5Y ");
			sql.append(" , Y.PRFT_LAST_YEAR_NOTE "); //前一年度貢獻度
			sql.append(" , FLOOR(ROUND(MONTHS_BETWEEN(SYSDATE, CUST.BIRTH_DATE) / 12, 4)) AS CUST_AGE ");
			sql.append(" , Y.CON_DEGREE_YN ");
			sql.append(" , CASE WHEN Y.CON_DEGREE_YN = 'Y' THEN '' ELSE Y.EMP_ID || ' - ' || NVL(YM.EMP_NAME, '') END AS EMP_5Y_SHOW ");
			sql.append(" FROM TBPMS_ROTATION_MAIN A ");
			sql.append(" LEFT JOIN TBCRM_CUST_MAST CUST ON CUST.CUST_ID = A.CUST_ID ");
			sql.append(" LEFT JOIN TBCRM_CUST_NOTE N ON A.CUST_ID = N.CUST_ID ");
			sql.append(" LEFT JOIN VWORG_AO_INFO AO ON AO.AO_CODE = CUST.AO_CODE ");
			sql.append(" LEFT JOIN VWORG_DEFN_INFO ORG ON ORG.BRANCH_NBR = CUST.BRA_NBR ");
			sql.append(" LEFT JOIN VWORG_DEFN_INFO ORG2 ON ORG2.BRANCH_NBR = A.SM_BRANCH_NBR ");
			sql.append(" LEFT JOIN TBCRM_TRS_PRJ_MAST M ON M.PRJ_ID = A.PRJ_ID ");
			sql.append(" LEFT JOIN TBPMS_ROTATION_5YCUST Y ON Y.PRJ_ID = A.PRJ_ID AND Y.CUST_ID = A.CUST_ID ");
			sql.append(" LEFT JOIN TBORG_MEMBER YM ON YM.EMP_ID = Y.EMP_ID ");	
			sql.append(" WHERE A.PRJ_ID = :prjId ");
			condition.setObject("prjId", inputVO.getPRJ_ID());

			// 確認單回函日期
			if (null != inputVO.getsCreDate() && null != inputVO.getEndDate()) {
				sql.append(" AND TRUNC(A.RECEIVE_DATE) BETWEEN TRUNC(:sDATE) AND TRUNC(:eDATE) ");
				condition.setObject("sDATE", inputVO.getsCreDate());
				condition.setObject("eDATE", inputVO.getEndDate());
			}
			// 對帳單寄送方式
			if (StringUtils.isNotBlank(inputVO.getStatement_send_type())) {
				sql.append(" AND A.STATEMENT_SEND_TYPE = :STATEMENT_SEND_TYPE ");
				condition.setObject("STATEMENT_SEND_TYPE", inputVO.getStatement_send_type());
			}

			// 客戶ID
			if (StringUtils.isNotBlank(inputVO.getCustId())) {
				sql.append(" AND A.CUST_ID = :CUST_ID ");
				condition.setObject("CUST_ID", inputVO.getCustId());
			}

			// 確認單處理狀態
			if (StringUtils.isNotBlank(inputVO.getProcess_status())) {
				if(StringUtils.equals("6", inputVO.getProcess_status())) {
					//通報內控品管科
					sql.append(" AND A.PROCESS_STATUS = '6' ");
					sql.append(" AND A.MODIFIER = 'BTPMS429_SEND175BMAIL.java' ");
				} else if(StringUtils.equals("7", inputVO.getProcess_status())) {
					//通報分行人員管理科
					sql.append(" AND A.PROCESS_STATUS = '6' ");
					sql.append(" AND A.MODIFIER = 'BTPMS429_SEND175CMAIL.java' ");
				} else {
					sql.append(" AND A.PROCESS_STATUS = :PROCESS_STATUS ");
					condition.setObject("PROCESS_STATUS", inputVO.getProcess_status());
				}
			}
			
			// 客戶ID
			if (StringUtils.isNotBlank(inputVO.getCust_proce_status())) {
				sql.append(" AND A.CUST_PROCE_STATUS = :CUST_PROCE_STATUS ");
				condition.setObject("CUST_PROCE_STATUS", inputVO.getCust_proce_status());
			}

			//從首頁過來
			//2022年調換換手系統管控需求(未輪調) 分行主管異常通報為"正常"，但電子/實體回函有異常
			if (StringUtils.isNotBlank(inputVO.getRotationBRMsg())) {
				String[] CUST_ARRAY = inputVO.getRotationBRMsg().split(", ");
				List<String> cList = new ArrayList();
				for(int i = 0; i < CUST_ARRAY.length; i++) {
					if(StringUtils.isNotBlank(CUST_ARRAY[i])) cList.add(CUST_ARRAY[i].trim());
				}
				sql.append(" AND A.CUST_ID IN (:CUST_ARRAY) ");
				condition.setObject("CUST_ARRAY", cList);
			}

			/*
			 * 可視範圍四個層級 業務處>營運區>分行>理專
			 * 1. 理專身分: 只考慮有無選擇理專來決定限制條件
			 * 2. 其他身分: 從理專往上看,有選擇的當限制條件
			 * 3. 其他身分若四個層級都沒有,則用業務處清單當限制條件
			 */
			//理專
			boolean conditionEnd = false;
			if (fcMap.containsKey(roleID)) {
				if (StringUtils.isNotBlank(inputVO.getAo_code())) {
					sql.append(" AND CUST.AO_CODE = :AO_CODE ");
					condition.setObject("AO_CODE", inputVO.getAo_code());
				} else {
					List<String> aoCodeList = new ArrayList();
					for (Map map : inputVO.getAoCodeList()) {
						if (!checkIsNull(map, "DATA").equals("")) {
							aoCodeList.add(checkIsNull(map, "DATA"));
						}
					}
					sql.append(" AND CUST.AO_CODE IN (:AO_CODE) ");
					condition.setObject("AO_CODE", aoCodeList);
				}
				conditionEnd = true;
			} else { //其他身分
				//理專
				if (!conditionEnd && StringUtils.isNotBlank(inputVO.getAo_code())) {
					sql.append(" AND CUST.AO_CODE = :AO_CODE ");
					condition.setObject("AO_CODE", inputVO.getAo_code());
					conditionEnd = true;
				}
				// 分行
				if (!conditionEnd && StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
					sql.append(" and CUST.BRA_NBR = :branch_nbr ");
					condition.setObject("branch_nbr", inputVO.getBranch_nbr());
					conditionEnd = true;
				}
				// 區
				if (!conditionEnd && StringUtils.isNotBlank(inputVO.getBranch_area_id())) {
					sql.append(" and ORG.BRANCH_AREA_ID = :branch_area_id ");
					condition.setObject("branch_area_id", inputVO.getBranch_area_id());
					conditionEnd = true;
				}
				// 處
				if (!conditionEnd && StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
					sql.append(" and ORG.REGION_CENTER_ID = :region_center_id ");
					condition.setObject("region_center_id", inputVO.getRegion_center_id());
					conditionEnd = true;
				}
			}
			//非理專且四項都沒選就用業務處清單
			if(!conditionEnd && inputVO.getRegionList() != null) {
				List<String> regionList = new ArrayList();
				for (Map map : inputVO.getRegionList()) {
					if (!checkIsNull(map, "DATA").equals("")) {
						regionList.add(checkIsNull(map, "DATA"));
					}
				}
				sql.append("and ORG.REGION_CENTER_ID IN (:region_center_list) ");
				condition.setObject("region_center_list", regionList);
			}

			sql.append("order by ORG.REGION_CENTER_ID, ORG.BRANCH_AREA_ID, ORG.BRANCH_NBR ");
			condition.setQueryString(sql.toString());
			List<Map<String, Object>> list = dam.exeQuery(condition);

			// 給前端所需的參數&型態轉換
			if (list.size() > 0) {
				for (Map map : list) {
					map.put("isOpen", false);
					if (StringUtils.isNotBlank(checkIsNull(map, "REC_DATE"))) {
						Date date = (Date) map.get("REC_DATE");
						map.put("REC_DATE", date.getTime());
					}
				}
			}
			outputVO.setResultList(list);
			return outputVO;

//		} catch (Exception e) {
//			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
//			throw new APException("系統發生錯誤請洽系統管理員");
//		}
	}

	/**
	 * 【儲存】更新資料，在前端篩選編輯過的資料。
	 *
	 * @throws ParseException
	 */
	public void save(Object body, IPrimitiveMap header) throws JBranchException, ParseException {

		PMS429InputVO inputVO = (PMS429InputVO) body;
		DataAccessManager dam = this.getDataAccessManager();
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> bmmgrMap = xmlInfo.doGetVariable("FUBONSYS.BMMGR_ROLE", FormatHelper.FORMAT_2);// 個金主管
		Map<String, String> fcMap = xmlInfo.doGetVariable("FUBONSYS.FC_ROLE", FormatHelper.FORMAT_2); // 理專
		String loginID = (String) SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINID);

//		try {
			// 理專
			if (fcMap.containsKey(SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINROLE))) {
				for (Map each : inputVO.getList()) {
					//自取對帳單已連繫才需要更新
					if(StringUtils.equals("Y", each.get("AO_CONTACT_YN").toString())) {
						TBPMS_ROTATION_MAINVO mainVO = (TBPMS_ROTATION_MAINVO) dam.findByPKey(TBPMS_ROTATION_MAINVO.TABLE_UID, new BigDecimal(each.get("KEY_NO").toString()));

						mainVO.setAO_CONTACT_DATE(new Timestamp(new Date().getTime()));
						mainVO.setAO_CONTACT_EMP_ID(loginID);
						mainVO.setPROCESS_STATUS("2");
						dam.update(mainVO);
					}
				}
				// 主管
			} else if (bmmgrMap.containsKey(SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINROLE))) {
				for (Map each : inputVO.getList()) {
					TBPMS_ROTATION_MAINVO mainVO = (TBPMS_ROTATION_MAINVO) dam.findByPKey(TBPMS_ROTATION_MAINVO.TABLE_UID, new BigDecimal(each.get("KEY_NO").toString()));

					//自取對帳單，AO為空，可由主管聯繫客戶
					if(StringUtils.equals("3", ObjectUtils.toString(each.get("STATEMENT_SEND_TYPE"))) && StringUtils.isBlank(ObjectUtils.toString(each.get("PROCESS_STATUS")))) {
						//自取對帳單已連繫才需要更新
						if(StringUtils.equals("Y", each.get("AO_CONTACT_YN").toString())) {
							mainVO.setAO_CONTACT_DATE(new Timestamp(new Date().getTime()));
							mainVO.setAO_CONTACT_EMP_ID(loginID);
							mainVO.setPROCESS_STATUS("2");
							dam.update(mainVO);
						}
					} else {
						//主管正常流程
						mainVO.setBRN_MGM_YN(checkIsNull(each, "BRN_MGM_YN"));
						mainVO.setCONTACT_STATUS(checkIsNull(each, "CONTACT_STATUS"));

						/** 
						 * CUST_PROCE_STATUS 客戶處理情形：
						 * null    1-客戶已回函但未有訪談(含錄音)紀錄
						 * 1~4     2-客戶已完成錄音訪談紀錄
						 * 5       3-客戶不便來行，主管陪同外訪確認完成
						 * 6       4-客戶臨櫃填寫「資況表申請書」及「客戶指定個金RM自主聲明書」等表單確認完成
						 * 7~10    5-客戶人在國外或特殊原因(專簽經處長同意)，並已完成錄音訪談紀錄
						 * **/
						if (StringUtils.isNotBlank(checkIsNull(each, "CUST_PROCE_STATUS"))) {
							mainVO.setCUST_PROCE_STATUS(checkIsNull(each, "CUST_PROCE_STATUS"));
						} else {
							mainVO.setCUST_PROCE_STATUS("1");
						}

						if (StringUtils.isNotBlank(checkIsNull(each, "REC_DATE"))) {
							mainVO.setREC_DATE(new Timestamp(changeStringToLong(checkIsNull(each, "REC_DATE"))));
						} else {
							mainVO.setREC_DATE(null);
						}

						mainVO.setREC_SEQ(checkIsNull(each, "REC_SEQ"));
						mainVO.setCONTACT_MEMO(checkIsNull(each, "CONTACT_MEMO"));
						mainVO.setCONTACT_EMP_ID(loginID);
						mainVO.setPROCESS_STATUS("3");
						dam.update(mainVO);
					}
				}
			}
//		} catch (Exception e) {
//			logger.error(String.format("PMS429 save error", StringUtil.getStackTraceAsString(e)));
//			throw new APException("系統發生錯誤請洽系統管理員 " + e.getMessage());
//		}

		sendRtnObject(null);
	}

	// 匯出
	public void export(Object body, IPrimitiveMap header) throws Exception {

		PMS429InputVO inputVO = (PMS429InputVO) body;

		if (StringUtils.isBlank(inputVO.getPRJ_ID())) {//專案代碼為必輸欄位
			throw new APException("請先選擇專案");
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
		String fileName = "帳務確認管理報表(輪調/換手)_" + sdf.format(new Date()) + ".csv";
		XmlInfo xmlInfo = new XmlInfo();

		Map<String, String> comm_yn = xmlInfo.doGetVariable("COMMON.YES_NO", FormatHelper.FORMAT_3);
		Map<String, String> STATEMENT_SEND_TYPE = xmlInfo.doGetVariable("PMS.STATEMENT_SEND_TYPE", FormatHelper.FORMAT_3);
		Map<String, String> ROTATION_BRMGR_STATUS = xmlInfo.doGetVariable("PMS.ROTATION_BRMGR_STATUS", FormatHelper.FORMAT_3);
		Map<String, String> CUST_PROCE_STATUS = xmlInfo.doGetVariable("PMS.CUST_PROCE_STATUS", FormatHelper.FORMAT_3);
		Map<String, String> BRN_MGM_YN = new HashMap();
		BRN_MGM_YN.put("Y", "正常");
		BRN_MGM_YN.put("N", "異常");
		Map<String, String> RTN_STATUS_AST = new HashMap();
		RTN_STATUS_AST.put("Y", "相符");
		RTN_STATUS_AST.put("N", "不符");
		RTN_STATUS_AST.put("E", "未填寫");
		Map<String, String> RTN_STATUS_NP = new HashMap();
		RTN_STATUS_NP.put("Y", "有");
		RTN_STATUS_NP.put("N", "無");
		RTN_STATUS_NP.put("E", "未填寫");

		String[] csvHeader = getCsvHeader();
		String[] csvMain = getCsvMain();
		List<Object[]> csvData = new ArrayList<Object[]>();

		for (Map<String, Object> map : inputVO.isPrintAllData() ? getExportAllData(inputVO.getPRJ_ID()) : inputVO.getList()) {

			String[] records = new String[csvHeader.length];
			map.put("BRANCH", checkIsNull(map, "BRANCH_NBR") + " - " + checkIsNull(map, "BRANCH_NAME"));
			map.put("SM_BRANCH", checkIsNull(map, "SM_BRANCH_NBR") + " - " + checkIsNull(map, "SM_BRANCH_NAME"));

			for (int i = 0; i < csvHeader.length; i++) {
				switch (csvMain[i]) {
				case "STATEMENT_SEND_TYPE":
					records[i] = STATEMENT_SEND_TYPE.get(checkIsNull(map, csvMain[i]));
					break;
				case "RTN_STATUS_AST":
					records[i] = RTN_STATUS_AST.get(checkIsNull(map, csvMain[i]));
					break;
				case "RTN_STATUS_NP":
					records[i] = RTN_STATUS_NP.get(checkIsNull(map, csvMain[i]));
					break;
				case "AO_CONTACT_YN":
					records[i] = comm_yn.get(checkIsNull(map, csvMain[i]));
					break;
				case "BRN_MGM_YN":
					records[i] = BRN_MGM_YN.get(checkIsNull(map, csvMain[i]));
					break;
				case "CUST_PROCE_STATUS":
					records[i] = CUST_PROCE_STATUS.get(checkIsNull(map, csvMain[i]));
					break;
				case "CONTACT_STATUS":
					records[i] = ROTATION_BRMGR_STATUS.get(checkIsNull(map, csvMain[i]));
					break;
				case "REC_DATE":
					if (!inputVO.isPrintAllData() && StringUtils.isNotBlank(checkIsNull(map, csvMain[i]))) {
						records[i] = sdf2.format(new Date(changeStringToLong(checkIsNull(map, csvMain[i]))));
					} else {
						records[i] = checkIsNull(map, csvMain[i]);
					}
					break;
				case "CUST_FLAG":
					// N.DEATH_YN, N.COMPLAIN_YN, N.COMM_RS_YN, N.COMM_NS_YN
					String CUST_FLAG = "";
					
					String COMPLAIN_YN = checkIsNull(map, "COMPLAIN_YN").equals("Y") ? "客訴戶" : "";
					CUST_FLAG += COMPLAIN_YN;
					
					String COMM_NS_YN = checkIsNull(map, "COMM_NS_YN").equals("Y") ? "NS禁銷戶" : "";
					if (CUST_FLAG.length() > 0 && COMM_NS_YN.length() > 0) CUST_FLAG += "、";
					CUST_FLAG += COMM_NS_YN;
					
					String COMM_RS_YN = checkIsNull(map, "COMM_RS_YN").equals("Y") ? "RS拒銷戶" : "";
					if (CUST_FLAG.length() > 0 && COMM_RS_YN.length() > 0) CUST_FLAG += "、";
					CUST_FLAG += COMM_RS_YN;
					
					String DEATH_YN = checkIsNull(map, "DEATH_YN").equals("Y") ? "死亡戶" : "";
					if (CUST_FLAG.length() > 0 && DEATH_YN.length() > 0) CUST_FLAG += "、";
					CUST_FLAG += DEATH_YN;
					
					records[i] = CUST_FLAG;
					break;
				default:
					records[i] = checkIsNull(map, csvMain[i]);
					break;
				}
			}
			csvData.add(records);
		}

		CSVUtil csv = new CSVUtil();
		csv.setHeader(csvHeader);
		csv.addRecordList(csvData);

		String url = csv.generateCSV();

		notifyClientToDownloadFile(url, fileName);
	}

	private String checkIsNull(Map map, String key) {

		if (StringUtils.isNotBlank(ObjectUtils.toString(map.get(key)))) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}

	private List<Map<String, Object>> getExportAllData(String prjId) throws JBranchException, ParseException {

		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		StringBuffer sql = new StringBuffer();
		
		try {

			sql.append(" SELECT  N.DEATH_YN, N.COMPLAIN_YN, N.COMM_RS_YN, N.COMM_NS_YN ");
			sql.append(" , A.STATEMENT_DATE "); // 帳單基準日
			sql.append(" , ORG.REGION_CENTER_ID "); // 業務處代碼
			sql.append(" , ORG.REGION_CENTER_NAME "); // 業務處名稱
			sql.append(" , ORG.BRANCH_AREA_ID "); // 營運區代碼
			sql.append(" , ORG.BRANCH_AREA_NAME "); // 營運區名稱
			sql.append(" , ORG.BRANCH_NBR "); // 歸屬行代碼
			sql.append(" , ORG.BRANCH_NAME "); // 歸屬行名稱
			sql.append(" , A.SM_BRANCH_NBR"); // 對帳單寄送分行代碼
			sql.append(" , ORG2.BRANCH_NAME AS SM_BRANCH_NAME  "); // 對帳單寄送分行名稱
			sql.append(" , A.CUST_ID "); // 客戶ID
			sql.append(" , CUST.CUST_NAME "); // 客戶姓名
			sql.append(" , AO.EMP_NAME "); // 服務理專
			sql.append(" , CUST.AO_CODE "); // AO CODE
			sql.append(" , A.STATEMENT_SEND_TYPE "); // 對帳單寄送方式 PARAM_TYPE =
														// 'PMS.STATEMENT_SEND_TYPE'
			sql.append(" , A.SEND_DATE "); // 電子/實體確認單寄送日期
			sql.append(" , A.RECEIVE_DATE "); // 回函日期
			sql.append(" , A.RTN_STATUS_AST "); // 回函結果帳務相符
			sql.append(" , A.RTN_STATUS_NP "); // 回函結果理專是否有不當情事
			sql.append(" , A.MAIL_CONTACT_DATE "); // 實體確認單聯絡單日期
			sql.append(" , A.MAIL_REPLY_DATE "); // 實體確認單聯絡單回覆日期
			sql.append(" , (CASE WHEN A.AO_CONTACT_DATE IS NULL THEN 'N' ELSE 'Y' END) AS AO_CONTACT_YN "); // 自取確認單理專是否已連繫
																											// Y:已連繫
																											// N:未聯繫
			sql.append(" , A.AO_CONTACT_DATE "); // 自取確認單理專聯繫日期
			sql.append(" , A.AO_CONTACT_EMP_ID "); // 自取確認單理專聯繫員編
			sql.append(" , A.BRN_MGM_YN "); // 異常通報(分行主管回覆是否正常 Y:正常 N:異常)
			sql.append(" , A.CONTACT_STATUS "); // 錄音訪談紀錄
			sql.append(" , A.REC_DATE "); // 錄音日期
			sql.append(" , A.REC_SEQ "); // 錄音序號
			sql.append(" , A.CONTACT_MEMO "); // 分行主管聯繫內容
			sql.append(" , A.CONTACT_EMP_ID "); // 分行主管聯繫員編
			sql.append(" , A.RM_DIFF_YN "); // 確認單寄送前後RM有差異
			sql.append(" , A.KEY_NO "); // 主鍵序號
			sql.append(" , A.PROCESS_STATUS ");
			sql.append(" , A.CUST_PROCE_STATUS ");
			sql.append(" , Y.EMP_ID || ' - ' || NVL(YM.EMP_NAME, '') AS EMP_5Y "); //經營滿五年理專
			sql.append(" , Y.PRFT_LAST_YEAR_NOTE "); //前一年度貢獻度
			sql.append(" , FLOOR(ROUND(MONTHS_BETWEEN(SYSDATE, CUST.BIRTH_DATE) / 12, 4)) AS CUST_AGE ");
			sql.append(" , Y.CON_DEGREE_YN ");
			sql.append(" , CASE WHEN Y.CON_DEGREE_YN = 'Y' THEN '' ELSE Y.EMP_ID || ' - ' || NVL(YM.EMP_NAME, '') END AS EMP_5Y_SHOW ");
			sql.append(" FROM TBPMS_ROTATION_MAIN A ");
			sql.append(" LEFT JOIN TBCRM_CUST_MAST CUST ON CUST.CUST_ID = A.CUST_ID ");
			sql.append(" LEFT JOIN TBCRM_CUST_NOTE N ON A.CUST_ID = N.CUST_ID ");
			sql.append(" LEFT JOIN VWORG_AO_INFO AO ON AO.AO_CODE = CUST.AO_CODE ");
			sql.append(" LEFT JOIN VWORG_DEFN_INFO ORG ON ORG.BRANCH_NBR = CUST.BRA_NBR ");
			sql.append(" LEFT JOIN VWORG_DEFN_INFO ORG2 ON ORG2.BRANCH_NBR = A.SM_BRANCH_NBR ");
			sql.append(" LEFT JOIN TBPMS_ROTATION_5YCUST Y ON Y.PRJ_ID = A.PRJ_ID AND Y.CUST_ID = A.CUST_ID ");
			sql.append(" LEFT JOIN TBORG_MEMBER YM ON YM.EMP_ID = Y.EMP_ID ");
			sql.append(" WHERE A.PRJ_ID = :prjId ");
			condition.setObject("prjId", prjId);

			sql.append("order by ORG.REGION_CENTER_ID, ORG.BRANCH_AREA_ID, ORG.BRANCH_NBR ");
			condition.setQueryString(sql.toString());
			List<Map<String, Object>> list = dam.exeQuery(condition);

			return list;

		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	private long changeStringToLong(String str) {
		BigDecimal bd = new BigDecimal(str);
		return bd.longValue();
	}

	private String[] getCsvHeader() {
		String[] str = { "對帳單基準日", "業務處", "區別", "歸屬行", "對帳單寄送分行", "客戶註記", "前一年度貢獻度等級", "客戶姓名", "身分證號", "年齡", "現任理專", "經營滿五年理專", "AOCODE", "對帳單寄送方式", "電子/實體確認單寄送日期", "回函日期", "回函結果帳務相符", "回函結果理專是否有不當情事", "實體確認單聯絡單日期", "實體確認單聯絡單回覆日期", "自取確認單是否已連繫", "自取確認單聯繫日期", "自取確認單聯繫員編", "客戶處理情形", "異常通報(正常/異常)", "客戶處理情形", "錄音訪談紀錄", "錄音日期", "錄音序號", "聯繫內容", "聯繫員編", "確認單寄送前後RM有差異" };
		return str;
	}

	private String[] getCsvMain() {
		String[] str = { "STATEMENT_DATE", "REGION_CENTER_NAME", "BRANCH_AREA_NAME", "BRANCH", "SM_BRANCH", "CUST_FLAG", "PRFT_LAST_YEAR_NOTE", "CUST_NAME", "CUST_ID", "CUST_AGE", "EMP_NAME", "EMP_5Y_SHOW", "AO_CODE", "STATEMENT_SEND_TYPE", "SEND_DATE", "RECEIVE_DATE", "RTN_STATUS_AST", "RTN_STATUS_NP", "MAIL_CONTACT_DATE", "MAIL_REPLY_DATE", "AO_CONTACT_YN", "AO_CONTACT_DATE", "AO_CONTACT_EMP_ID", "CUST_PROCE_STATUS", "BRN_MGM_YN", "CUST_PROCE_STATUS", "CONTACT_STATUS", "REC_DATE", "REC_SEQ", "CONTACT_MEMO", "CONTACT_EMP_ID", "RM_DIFF_YN" };
		return str;
	}

	/***
	 * 檢核錄音序號正確性
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void validateRecseq(Object body, IPrimitiveMap header) throws Exception {
		PMS429InputVO inputVO = (PMS429InputVO) body;
		PMS429OutputVO outputVO = validateRecseq(inputVO);
		sendRtnObject(outputVO); 
	}
	
	/***
	 * 檢核錄音序號正確性
	 * @param inputVO
	 * @return
	 * @throws Exception
	 */
	public PMS429OutputVO validateRecseq(PMS429InputVO inputVO) throws Exception {
		PMS429OutputVO outputVO = new PMS429OutputVO();
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		String rtnMsg = "";

		if(StringUtils.isBlank(inputVO.getRecSeq()) || StringUtils.isBlank(inputVO.getCustId()) || inputVO.getRecDate() == null) {
			rtnMsg = "錄音序號檢核遺漏必要欄位";
        } else {
			if (PlatformContext.getBean(OTH001.class).voiceRecordTestModePass(inputVO.getRecSeq())) {
				rtnMsg = "";
		 	} else if(inputVO.getRecSeq().length() != 12) { //錄音序號規則為：YYMMDD45XXXX
        		rtnMsg = inputVO.getCustId() + "錄音序號長度錯誤，應為12碼";
        	} else if(!StringUtils.equals("45", inputVO.getRecSeq().substring(6, 8))) {
        		rtnMsg = inputVO.getCustId() + "錄音序號格式錯誤";
        	} else {
        		StringBuilder sb = new StringBuilder();
        		sb.append(" call P_VOICERECORDING_QRY(?,?,?,?,?,?) ");
        		queryCondition.setString(1, inputVO.getRecSeq()); //錄音序號
        		queryCondition.setString(2, inputVO.getCustId()); //客戶ID
        		queryCondition.setString(3, ""); //分行代碼，沒有檢核
        		queryCondition.setString(4, "CRM"); //PRODTYPE
        		queryCondition.setString(5, "ROTATION"); //PRODID
          		queryCondition.registerOutParameter(6,Types.VARCHAR);

        		queryCondition.setQueryString(sb.toString());

        		Map<Integer, Object> RecList = dam.executeCallable(queryCondition);
        		if("0000".equals(RecList.get(6).toString())){
        			//錄音序號正確，檢核錄音日期
        			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        			sb = new StringBuilder();
        			sb.append("SELECT 1 FROM TBSYS_REC_LOG WHERE TRANSSEQ = :recSeq AND TRANSDATE = TO_CHAR(:recDate, 'YYYYMMDD') ");
        			queryCondition.setObject("recSeq", inputVO.getRecSeq());
        			queryCondition.setObject("recDate", inputVO.getRecDate());
        			queryCondition.setQueryString(sb.toString());
        			if(CollectionUtils.isEmpty(dam.exeQuery(queryCondition))) {
        				rtnMsg = inputVO.getCustId() + "錄音日期錯誤";
        			}
        		} else {
        			rtnMsg = inputVO.getCustId() + "查無此錄音序號" + inputVO.getRecSeq();
        		}
        	}
        }

		outputVO.setValidateRecseqMsg(rtnMsg);
		return outputVO;
	}
}
