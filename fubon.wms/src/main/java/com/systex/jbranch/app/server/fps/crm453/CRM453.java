package com.systex.jbranch.app.server.fps.crm453;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.comutil.encrypt.aes.AesEncryptDecryptUtils;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * crm452
 * 
 * @author
 * @date
 * @spec null
 */
@Component("crm453")
@Scope("request")
public class CRM453 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	// 接到M+傳來auserid解碼後用來查詢EMP_ID
	public void getEmpIdByAuserId (Object body, IPrimitiveMap header) throws Exception {
		logger.info("#1585埋log追蹤getEmpIdByAuserId(): 進入getEmpIdByAuserId() ");
		System.out.println("#1585埋log追蹤getEmpIdByAuserId(): 進入getEmpIdByAuserId() ");
		CRM453InputVO inputVO = (CRM453InputVO) body ;
		CRM453OutputVO outputVO = new CRM453OutputVO();
		List<Map<String, Object>> resultList = new ArrayList<Map<String,Object>>();
		// 抓參數
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("MPLUS.API_PASSWORD", FormatHelper.FORMAT_3);
		String aesKey = headmgrMap.get("AesKey");
		// 取得M+傳來的加密ID
		String auserID = inputVO.getAuserid();
		String mplus_uid = "";
		if (StringUtils.isNotBlank(auserID)) {
			// AES解密
			mplus_uid = AesEncryptDecryptUtils.decryptAesEcbPkcs7Padding(aesKey, auserID);
			logger.info("#1585埋log追蹤getEmpIdByAuserId(): plus " + mplus_uid);
			System.out.println("#1585埋log追蹤getEmpIdByAuserId(): plus " + mplus_uid);
//			System.out.println("==================");
//			System.out.println(aesKey);
//			System.out.println(auserID);
//			System.out.println(mplus_uid);
			dam = this.getDataAccessManager();
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT MP.EMP_NUMBER, P.PRIVILEGEID FROM ( ");
			sql.append("    SELECT EMP_NUMBER FROM TBAPI_MPLUS WHERE MPLUS_UID = :mplus_uid ");
			sql.append(") MP ");
			sql.append("LEFT JOIN TBORG_MEMBER_ROLE R ON MP.EMP_NUMBER = R.EMP_ID ");
			sql.append("LEFT JOIN TBSYSSECUROLPRIASS P ON R.ROLE_ID = P.ROLEID ");
			queryCondition.setObject("mplus_uid", mplus_uid);
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
			logger.info("#1585埋log追蹤getEmpIdByAuserId(): list撈出幾筆? " + list.size() + " 筆");
			System.out.println("#1585埋log追蹤getEmpIdByAuserId(): list撈出幾筆? " + list.size() + " 筆");
			// 有可能一個 MPLUS_UID 會對到多個 EMP_NUMBER，此時需取最大角色。
			String emp_number = "";
			int pri = 0;
			String privilegeID = "";
			for (Map<String, Object> map : list) {
				if (map.get("PRIVILEGEID") != null && map.get("EMP_NUMBER") != null) {
					String priID = map.get("PRIVILEGEID").toString();
					privilegeID = StringUtils.isBlank(privilegeID) ? map.get("PRIVILEGEID").toString() : privilegeID;
					if (priID.length() > 3 && "UHRM".equals(priID.substring(0, 4)) || 
						priID.length() > 3 && "BS".equals(priID.substring(0, 2))) {
						emp_number = map.get("EMP_NUMBER").toString();
						break;
					} else {
						if (Integer.parseInt(priID) > pri) {
							pri = Integer.parseInt(map.get("PRIVILEGEID").toString());
							privilegeID = map.get("PRIVILEGEID").toString();
							emp_number = map.get("EMP_NUMBER").toString();
						}
					}
				}
			}
			Map<String, Object> resultMap = new HashMap<String, Object>(); 
			resultMap.put("EMP_NUMBER", emp_number);
			resultMap.put("PRIVILEGEID", privilegeID);
			logger.info("#1585埋log追蹤getEmpIdByAuserId(): EMP_NUMBER? " + resultMap.get("EMP_NUMBER"));
			logger.info("#1585埋log追蹤getEmpIdByAuserId(): PRIVILEGEID? " + resultMap.get("PRIVILEGEID"));
			System.out.println("#1585埋log追蹤getEmpIdByAuserId(): EMP_NUMBER? " + resultMap.get("EMP_NUMBER"));
			System.out.println("#1585埋log追蹤getEmpIdByAuserId(): PRIVILEGEID? " + resultMap.get("PRIVILEGEID"));
			resultList.add(resultMap);
			outputVO.setResultList(resultList);
		}
		logger.info("#1585埋log追蹤getEmpIdByAuserId(): 結束getEmpIdByAuserId()");
		System.out.println("#1585埋log追蹤getEmpIdByAuserId(): 結束getEmpIdByAuserId()");
		this.sendRtnObject(outputVO);
	}
	
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		logger.info("#1585埋log追蹤inquire(): 進入inquire()");
		System.out.println("#1585埋log追蹤inquire(): 進入inquire()");
		CRM453InputVO inputVO = (CRM453InputVO) body ;
		CRM453OutputVO outputVO = new CRM453OutputVO();
		List<Map<String, Object>> resultList = new ArrayList<Map<String,Object>>();
		dam = this.getDataAccessManager();
		
		// 查詢是否有代理其他EMP_ID
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT EMP_ID, ROLE_ID ");
		sb.append("FROM VWORG_EMP_INFO ");
		sb.append("WHERE EMP_ID = :empID ");
		sb.append("UNION ");
		sb.append("SELECT EMP_ID, ROLE_ID ");
		sb.append("FROM VWORG_EMP_INFO INFO ");
		sb.append("WHERE EXISTS ( ");
		sb.append("  SELECT EMP_ID ");
		sb.append("  FROM TBORG_AGENT AG ");
		sb.append("  WHERE AG.AGENT_ID = :empID ");
		sb.append("  AND AG.AGENT_STATUS = 'S' ");
		sb.append("  AND AG.EMP_ID = INFO.EMP_ID ");
		sb.append(") ");
		logger.info("#1585埋log追蹤inquire(): empID? " + inputVO.getEmpID());
		queryCondition.setObject("empID", inputVO.getEmpID());
		queryCondition.setQueryString(sb.toString());
		
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		logger.info("#1585埋log追蹤inquire(): list幾筆? " + list.size() + " 筆");
		System.out.println("#1585埋log追蹤inquire(): list幾筆? " + list.size() + " 筆");
		if (list.size() > 0) {
			for(Map<String, Object> agentMap : list){
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuffer();
				
				sb.append("SELECT A.*, C.CUST_NAME, E.EMP_NAME, E.DEPT_ID, E.DEPT_ID || D.DEPT_NAME AS BRANCH FROM ( ");
				sb.append("SELECT CUST_ID, CREATOR, '期間' AS APPLY_CAT, '1' AS APPLY_TYPE, MPLUS_BATCH FROM TBCRM_BRG_APPLY_PERIOD ");
				sb.append("WHERE APPLY_STATUS = '1' "); 
				sb.append("AND ( ");  
				sb.append("(INSTR(MGR_EMP_ID_1, :emp_id ) > 0 AND AUTH_STATUS = '0') OR "); 
				sb.append("(INSTR(MGR_EMP_ID_2, :emp_id ) > 0 AND AUTH_STATUS = '1') OR "); 
				sb.append("(INSTR(MGR_EMP_ID_3, :emp_id ) > 0 AND AUTH_STATUS = '2') OR "); 
				sb.append("(INSTR(MGR_EMP_ID_4, :emp_id ) > 0 AND AUTH_STATUS = '3') ) "); 
				sb.append("UNION ");
				sb.append("SELECT CUST_ID, CREATOR, '單次' AS APPLY_CAT, '2' AS APPLY_TYPE, MPLUS_BATCH FROM TBCRM_BRG_APPLY_SINGLE ");
				sb.append("WHERE APPLY_STATUS = '1' "); 
				sb.append("AND ( ");
				sb.append("(INSTR(MGR_EMP_ID_1, :emp_id ) > 0 AND AUTH_STATUS = '0') OR "); 
				sb.append("(INSTR(MGR_EMP_ID_2, :emp_id ) > 0 AND AUTH_STATUS = '1') OR "); 
				sb.append("(INSTR(MGR_EMP_ID_3, :emp_id ) > 0 AND AUTH_STATUS = '2') OR "); 
				sb.append("(INSTR(MGR_EMP_ID_4, :emp_id ) > 0 AND AUTH_STATUS = '3') ) "); 
				sb.append(") A "); 
				sb.append("LEFT JOIN TBCRM_CUST_MAST C ON A.CUST_ID = C.CUST_ID ");
				sb.append("LEFT JOIN TBORG_MEMBER E ON A.CREATOR = E.EMP_ID ");
				sb.append("LEFT JOIN TBORG_DEFN D ON E.DEPT_ID = D.DEPT_ID ");
				
				queryCondition.setObject("emp_id", agentMap.get("EMP_ID").toString());
				queryCondition.setQueryString(sb.toString());
				logger.info("#1585埋log追蹤inquire(): for loop empID? " + agentMap.get("EMP_ID").toString());
				System.out.println("#1585埋log追蹤inquire(): for loop empID? " + agentMap.get("EMP_ID").toString());
				List<Map<String, Object>> tempList = dam.exeQuery(queryCondition);
				logger.info("#1585埋log追蹤inquire(): for loop list幾筆? " + tempList.size() + " 筆");
				System.out.println("#1585埋log追蹤inquire(): for loop list幾筆? " + tempList.size() + " 筆");
				for (Map<String, Object> map : tempList) {
					// 將分行兩個字拿掉
					if (map.get("BRANCH") != null && String.valueOf(map.get("BRANCH")).indexOf("分行") >= 0) {
						String branchOri = String.valueOf(map.get("BRANCH"));
						int i = branchOri.indexOf("分行");
						String branch = branchOri.substring(0, i);
						map.put("BRANCH", branch);
					}
					
					// 批號相同（代表客戶ID＆議價類型一定相同，但單次議價的申請理專與申請分行有可能不同）畫面上僅顯示一筆
					// 批號為null，但相同客戶ID且相同議價類型僅顯示一筆
					if (map.get("MPLUS_BATCH") == null) {
						if (resultList.size() == 0) {
							// 第一筆都無條件先放進resultList
							resultList.add(map);
						} else {
							Boolean sameCustAndType = false;
							for (Map<String, Object> resultMap : resultList) {
								if (resultMap.get("CUST_ID") != null && resultMap.get("CUST_ID").equals(map.get("CUST_ID")) && 
									resultMap.get("APPLY_TYPE") != null && resultMap.get("APPLY_TYPE").equals(map.get("APPLY_TYPE"))) {
									sameCustAndType = true;
									// 處理申請分行、申請理專
									resultMap = checkResultMap(resultMap, map);
								}
							}
							if(!sameCustAndType) {
								resultList.add(map);
							}
						}
					} else {
						if (resultList.size() == 0) {
							// 第一筆都無條件先放進resultList
							resultList.add(map);
						} else {
							Boolean sameBatch = false;
							for (Map<String, Object> resultMap : resultList) {
								if (resultMap.get("MPLUS_BATCH") != null && resultMap.get("MPLUS_BATCH").equals(map.get("MPLUS_BATCH"))) {
									sameBatch = true;
									// 處理申請分行、申請理專
									resultMap = checkResultMap(resultMap, map);
								}
							}	
							if(!sameBatch) {
								resultList.add(map);
							}
						}
					}
				}
			}
			
			for (Map<String, Object> map : resultList) {
				// 處理申請分行
				if (map.get("tempBranch") != null) {
					Set<String> branchSet = (Set<String>) map.get("tempBranch");
					int i = 0;
					String branchs = "";
					for(String branch : branchSet) {
						i++;
						if (i == branchSet.size()) {
							branchs += branch;				
						} else {
							branchs += (branch + "\n");
						}
					}
					map.put("BRANCH", branchs);
				}
				
				// 處理申請理專姓名
				if (map.get("tempEmpName") != null) {
					Set<String> empNameSet = (Set<String>) map.get("tempEmpName");
					int i = 0;
					String empNames = "";
					for(String empName : empNameSet) {
						i++;
						if (i == empNameSet.size()) {
							empNames += empName;				
						} else {
							empNames += (empName + "\n");
						}
					}
					map.put("EMP_NAME", empNames);
				}
			}
		}
		outputVO.setResultList(resultList);
		logger.info("#1585埋log追蹤inquire(): 最後回送resultList有幾筆? " + resultList.size() + " 筆");
		logger.info("#1585埋log追蹤inquire(): 結束inquire()");
		System.out.println("#1585埋log追蹤inquire(): 最後回送resultList有幾筆? " + resultList.size() + " 筆");
		System.out.println("#1585埋log追蹤inquire(): 結束inquire()");
		this.sendRtnObject(outputVO);
	}
	
	private Map<String, Object> checkResultMap(Map<String, Object> resultMap, Map<String, Object> map) throws JBranchException {
		// 處理申請分行
		Set<String> branchSet = new HashSet<>();
		if (!resultMap.get("DEPT_ID").equals(map.get("DEPT_ID"))) {
			if (resultMap.get("tempBranch") != null) {
				branchSet = (Set) resultMap.get("tempBranch");
			}
			
			if (resultMap.get("BRANCH") != null)
				branchSet.add(String.valueOf(resultMap.get("BRANCH")));
			
			if (map.get("BRANCH") != null)
				branchSet.add(String.valueOf(map.get("BRANCH")));
			
			resultMap.put("tempBranch", branchSet);
		}
		
		// 處理申請理專
		Set<String> empNameSet = new HashSet<>();
		if (!resultMap.get("CREATOR").equals(map.get("CREATOR"))) {
			if (resultMap.get("tempEmpName") != null) {
				empNameSet = (Set) resultMap.get("tempEmpName");
			}
			
			if (resultMap.get("EMP_NAME") != null)
				empNameSet.add(String.valueOf(resultMap.get("EMP_NAME")));
			
			if (map.get("EMP_NAME") != null)
				empNameSet.add(String.valueOf(map.get("EMP_NAME")));
			
			resultMap.put("tempEmpName", empNameSet);
		}
		return resultMap;
	}
}