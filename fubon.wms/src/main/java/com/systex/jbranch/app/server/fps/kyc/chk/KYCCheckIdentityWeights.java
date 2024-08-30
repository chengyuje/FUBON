package com.systex.jbranch.app.server.fps.kyc.chk;

import static com.systex.jbranch.app.server.fps.kyccons.KYCCons.ARM;
import static com.systex.jbranch.app.server.fps.kyccons.KYCCons.TRADER;
import static com.systex.jbranch.app.server.fps.kyccons.KYCCons.LEGAL_PERSON_FINANCE_BOSS;
import static com.systex.jbranch.app.server.fps.kyccons.KYCCons.LEGAL_PERSON_FINANCE_OP;
import static com.systex.jbranch.app.server.fps.kyccons.KYCCons.PERSONAL_FINANCE_BOSS;
import static com.systex.jbranch.app.server.fps.kyccons.KYCCons.PERSONAL_FINANCE_OP;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.sot701.SOT701;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;

@Component("KYCCheckIdentityWeights")
@Scope("singleton")
public class KYCCheckIdentityWeights extends BizLogic{
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	//檢查信託證照
	public boolean chk_cert(String in_emp_id) throws JBranchException{
		try {
			SOT701 sot701 = (SOT701) PlatformContext.getBean("sot701");
			return sot701.chkLicense(in_emp_id);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
	
	//檢查衍商推介證照
	public boolean chk_cert_derive(String in_emp_id) throws JBranchException{
		try {
			SOT701 sot701 = (SOT701) PlatformContext.getBean("sot701");
			return sot701.chkLicense_derive(in_emp_id);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}	
	
	//只要一個為true，回傳true
	public boolean chkRoleOneOfThemIsTrue(String in_emp_id , String...in_privilegeids) throws JBranchException{
		boolean success = false;
		
		for(String in_PRIVILEGEID : in_privilegeids){
			if(success = success || chkRole(in_emp_id , in_PRIVILEGEID))
				return success;
		}
		return success;
	}
	
	//其中一個為false就回傳false
	public boolean chkRoleAllTrue(String in_emp_id , String...in_privilegeids) throws JBranchException{
		boolean success = true;
		
		for(String in_PRIVILEGEID : in_privilegeids){
			if(!(success = success && chkRole(in_emp_id , in_PRIVILEGEID)))
				return success;
		}
		return success;
	}
	
	
	//檢查角色 20170330
	public boolean chkRole(String in_emp_id , String in_PRIVILEGEID) throws JBranchException{
		try {
			Integer cnt = null;
			//檢核在職且為ARM組織人員
			if(ARM.equals(in_PRIVILEGEID))
				return (cnt = queryArmCnt(in_emp_id , in_PRIVILEGEID)) != null && cnt > 0;
			
			//檢核在職且為交易員
			if(TRADER.equals(in_PRIVILEGEID))
				return (cnt = queryTraderCnt(in_emp_id , in_PRIVILEGEID)) != null && cnt > 0;
					
			//其他權限
			return (cnt = queryOtherRoleCnt(in_emp_id , in_PRIVILEGEID)) != null && cnt > 0;
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
	
	
	/**判斷是否為企金板塊
	 * @param empId
	 * @return boolean
	 * @throws APException
	 */
	@SuppressWarnings("unchecked")
	public boolean chkIsBusBlock(String empId) throws APException {
		try{
			DataAccessManager dam_obj = this.getDataAccessManager();
			String queryStr = " select count(1)CNT from TBORG_MEMBER_PLATE_SG WHERE PLATE_REAL <> 'A' AND EMP_ID = :empid ";
			List<Map<String, BigDecimal>> check_Role = dam_obj.exeQueryWithoutSort(dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL)
				.setQueryString(queryStr)
				.setObject("empid" , empId)
			);
			
			return CollectionUtils.isNotEmpty(check_Role) && check_Role.get(0).get("CNT").intValue() > 0;
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
	
	/**
	 * 探斷是否為ARM
	 * @param in_emp_id
	 * @param in_PRIVILEGEID
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public Integer queryArmCnt(String in_emp_id,String in_PRIVILEGEID) throws Exception{
		//判斷是否為仍在職的ARM組織人員
		DataAccessManager dam_obj = this.getDataAccessManager();
		QueryConditionIF qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		List<Map<String, BigDecimal>> check_Role = dam_obj.exeQueryWithoutSort(
			qc.setQueryString(new StringBuilder()
				.append(" select COUNT(1)cnt FROM TBORG_MEMBER ")
				.append(" WHERE 1 = 1 ")
				.append(" AND SERVICE_FLAG = 'A' ") //在職註記：I-無效 A-有效 (含正常、試用及留停)
				.append(" AND CHANGE_FLAG IN ('A', 'M', 'P') ")//異動註記：Ａ：新增 Ｍ：修改 Ｄ：刪除 Ｓ：停用 Ｐ：復職
				.append(" AND JOB_TITLE_NAME IN ( SELECT PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'ORG.ARM' ) ")//人資職稱為ARM的相關人資職稱
				.append(" AND EMP_ID = :empid ")
				.toString()
			).setObject("empid" , in_emp_id)
		);
		
		return CollectionUtils.isEmpty(check_Role) ? 0 : check_Role.get(0).get("CNT").intValue(); 
	}
	
	/**
	 * 探斷是否為交易員
	 * @param in_emp_id
	 * @param in_PRIVILEGEID
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public Integer queryTraderCnt(String in_emp_id,String in_PRIVILEGEID) throws Exception{
		//判斷是否為仍在職的ARM組織人員
		DataAccessManager dam_obj = this.getDataAccessManager();
		QueryConditionIF qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		List<Map<String, BigDecimal>> check_Role = dam_obj.exeQueryWithoutSort(
			qc.setQueryString(new StringBuilder()
				.append(" select COUNT(1)cnt FROM TBORG_MEMBER ")
				.append(" WHERE 1 = 1 ")
				.append(" AND SERVICE_FLAG = 'A' ") //在職註記：I-無效 A-有效 (含正常、試用及留停)
				.append(" AND CHANGE_FLAG IN ('A', 'M', 'P') ")//異動註記：Ａ：新增 Ｍ：修改 Ｄ：刪除 Ｓ：停用 Ｐ：復職
				.append(" AND JOB_TITLE_NAME IN ( SELECT PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'ORG.TRADER' ) ")//人資職稱為交易員的相關人資職稱
				.append(" AND EMP_ID = :empid ")
				.toString()
			).setObject("empid" , in_emp_id)
		);
		
		return CollectionUtils.isEmpty(check_Role) ? 0 : check_Role.get(0).get("CNT").intValue(); 
	}
	
	/**
	 * 判斷其他身分
	 * @param in_emp_id
	 * @param in_PRIVILEGEID
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public Integer queryOtherRoleCnt(String in_emp_id , String...inPrivilegeids) throws Exception{
		DataAccessManager dam_obj = this.getDataAccessManager();
		Map<String , String> paramMap = new Hashtable<String , String>();
		
		StringBuilder queryStr = new StringBuilder();
		queryStr.append(" SELECT COUNT(1)cnt FROM TBORG_MEMBER_ROLE ");
		queryStr.append(" WHERE 1 = 1 ");
		queryStr.append(" AND EMP_ID = :in_emp_id ");
		queryStr.append(" AND ROLE_ID IN ( ");
		queryStr.append(" 	SELECT ROLEID FROM TBSYSSECUROLPRIASS WHERE PRIVILEGEID in( ");
		paramMap.put("in_emp_id", in_emp_id);
		
		for(int i = 0 ; i < inPrivilegeids.length ; i++){
			String inPrivilegeid = inPrivilegeids[i];
			String key = "in_PRIVILEGEID" + inPrivilegeid;
			queryStr.append(":" + key).append(i == inPrivilegeids.length-1 ? ")" : " , ");
			paramMap.put(key , inPrivilegeid);
		}
		queryStr.append(" ) ");
		
		QueryConditionIF qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL).setQueryString(queryStr.toString());

		for(String key : paramMap.keySet())
			qc.setObject(key , paramMap.get(key));
		
		List<Map<String, BigDecimal>> check_Role = dam_obj.exeQueryWithoutSort(qc);
		return CollectionUtils.isEmpty(check_Role) ? 0 : check_Role.get(0).get("CNT").intValue(); 
	}
	
	//判斷鑑機人員是否為主管轄下
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean checkInBossUnderJurisdiction(String bossEmpId , String empId) throws Exception{
		if(StringUtils.isBlank(bossEmpId) || StringUtils.isBlank(empId) || bossEmpId.equals(empId))
			return false;
		
		DataAccessManager dam_obj = this.getDataAccessManager();
		StringBuffer sbr = new StringBuffer();
		sbr.append(" select * from( ");
		sbr.append(" 	SELECT  ");
		sbr.append(" 		M.EMP_ID AS BOSS_EMP_ID, ");
		sbr.append(" 		:keyEmpID AS KEYIN_EMP_ID, ");
		sbr.append(" 		CASE WHEN INSTR( ");
		sbr.append(" 			( ");
		sbr.append(" 				SELECT  ");
		sbr.append(" 					LISTAGG(DEFN2.DEPT_ID, ',') WITHIN GROUP (ORDER BY 1) AS DEPT_LIST ");
		sbr.append(" 				FROM TBORG_DEFN DEFN ");
		sbr.append(" 				LEFT JOIN TBORG_DEFN DEFN2 ON DEFN.DEPT_ID  = DEFN2.PARENT_DEPT_ID ");
		sbr.append(" 				START WITH DEFN2.DEPT_ID = M.DEPT_ID ");
		sbr.append(" 				CONNECT BY PRIOR DEFN2.DEPT_ID  = DEFN2.PARENT_DEPT_ID ");
		sbr.append(" 			), ");
		sbr.append(" 			( ");
		sbr.append(" 			 SELECT DEPT_ID ");
		sbr.append(" 			 FROM ( ");
		sbr.append(" 			   SELECT DEPT_ID, FLAG, LASTUPDATE ");
		sbr.append(" 			   FROM ( ");
		sbr.append(" 			     SELECT DEPT_ID, 'Y' AS FLAG, TRUNC(LASTUPDATE) AS LASTUPDATE ");
		sbr.append(" 			     FROM TBORG_MEMBER ");
		sbr.append(" 			     WHERE EMP_ID = :keyEmpID ");
		sbr.append(" 			     AND SUBSTR(DEPT_ID, 1, 3) >= 200 AND SUBSTR(DEPT_ID, 1, 3) <= 900 ");
		sbr.append(" 			     AND SUBSTR(DEPT_ID, 1, 3) <> 806 ");
		sbr.append(" 			     AND SUBSTR(DEPT_ID, 1, 3) <> 810 ");
		sbr.append(" 			     ORDER BY LASTUPDATE ");
		sbr.append(" 			   ) ");
		sbr.append(" 			   UNION ");
		sbr.append(" 			   SELECT DEPT_ID, FLAG, LASTUPDATE ");
		sbr.append(" 			   FROM ( ");
		sbr.append(" 			     SELECT DEPT_ID, 'N' AS FLAG, TRUNC(LASTUPDATE) AS LASTUPDATE ");
		sbr.append(" 			     FROM TBORG_MEMBER_PLURALISM ");
		sbr.append(" 			     WHERE EMP_ID = :keyEmpID ");
		sbr.append(" 			     AND SUBSTR(DEPT_ID, 1, 3) >= 200 AND SUBSTR(DEPT_ID, 1, 3) <= 900 ");
		sbr.append(" 			     AND SUBSTR(DEPT_ID, 1, 3) <> 806 ");
		sbr.append(" 			     AND SUBSTR(DEPT_ID, 1, 3) <> 810 ");
		sbr.append(" 			     ORDER BY LASTUPDATE DESC ");
		sbr.append(" 			     FETCH FIRST 1 ROWS ONLY ");
		sbr.append(" 			   ) ");
		sbr.append(" 			 ) ");
		sbr.append(" 			 ORDER BY LASTUPDATE, DECODE(FLAG, 'Y', 0, 99) ");
		sbr.append(" 			 FETCH FIRST 1 ROWS ONLY ");
		sbr.append(" 			),  ");
		sbr.append(" 			1 ");
		sbr.append(" 		) > 0 THEN 'Y' ELSE 'N' END AS UNDER_THE_RULE ");
		sbr.append(" 	FROM TBORG_MEMBER M ");
		sbr.append(" 	INNER JOIN TBORG_MEMBER_ROLE MR ON M.EMP_ID = MR.EMP_ID AND MR.IS_PRIMARY_ROLE = 'Y' ");
		sbr.append(" 	WHERE SERVICE_FLAG = 'A' ");
		sbr.append(" 	AND CHANGE_FLAG IN ('A', 'M', 'P') ");
		sbr.append(" 	AND M.EMP_ID = :bossEmpID ");
		sbr.append(" 	  ");
		sbr.append(" 	UNION ");
		sbr.append(" 	  ");
		sbr.append(" 	SELECT  ");
		sbr.append(" 		PT.EMP_ID AS BOSS_EMP_ID, ");
		sbr.append(" 		:keyEmpID AS KEYIN_EMP_ID, ");
		sbr.append(" 		CASE WHEN INSTR( ");
		sbr.append(" 			( ");
		sbr.append(" 				SELECT LISTAGG(DEFN2.DEPT_ID, ',') WITHIN GROUP (ORDER BY 1) AS DEPT_LIST ");
		sbr.append(" 				FROM TBORG_DEFN DEFN ");
		sbr.append(" 				LEFT JOIN TBORG_DEFN DEFN2 ON DEFN.DEPT_ID  = DEFN2.PARENT_DEPT_ID ");
		sbr.append(" 				START WITH DEFN2.DEPT_ID = PT.DEPT_ID ");
		sbr.append(" 				CONNECT BY PRIOR DEFN2.DEPT_ID  = DEFN2.PARENT_DEPT_ID ");
		sbr.append(" 			), ");
		sbr.append(" 			( ");
		sbr.append(" 			 SELECT DEPT_ID ");
		sbr.append(" 			 FROM ( ");
		sbr.append(" 			   SELECT DEPT_ID, FLAG, LASTUPDATE ");
		sbr.append(" 			   FROM ( ");
		sbr.append(" 			     SELECT DEPT_ID, 'Y' AS FLAG, TRUNC(LASTUPDATE) AS LASTUPDATE ");
		sbr.append(" 			     FROM TBORG_MEMBER ");
		sbr.append(" 			     WHERE EMP_ID = :keyEmpID ");
		sbr.append(" 			     AND SUBSTR(DEPT_ID, 1, 3) >= 200 AND SUBSTR(DEPT_ID, 1, 3) <= 900 ");
		sbr.append(" 			     AND SUBSTR(DEPT_ID, 1, 3) <> 806 ");
		sbr.append(" 			     AND SUBSTR(DEPT_ID, 1, 3) <> 810 ");
		sbr.append(" 			     ORDER BY LASTUPDATE ");
		sbr.append(" 			   ) ");
		sbr.append(" 			   UNION ");
		sbr.append(" 			   SELECT DEPT_ID, FLAG, LASTUPDATE ");
		sbr.append(" 			   FROM ( ");
		sbr.append(" 			     SELECT DEPT_ID, 'N' AS FLAG, TRUNC(LASTUPDATE) AS LASTUPDATE ");
		sbr.append(" 			     FROM TBORG_MEMBER_PLURALISM ");
		sbr.append(" 			     WHERE EMP_ID = :keyEmpID ");
		sbr.append(" 			     AND SUBSTR(DEPT_ID, 1, 3) >= 200 AND SUBSTR(DEPT_ID, 1, 3) <= 900 ");
		sbr.append(" 			     AND SUBSTR(DEPT_ID, 1, 3) <> 806 ");
		sbr.append(" 			     AND SUBSTR(DEPT_ID, 1, 3) <> 810 ");
		sbr.append(" 			     ORDER BY LASTUPDATE DESC ");
		sbr.append(" 			     FETCH FIRST 1 ROWS ONLY ");
		sbr.append(" 			   ) ");
		sbr.append(" 			 ) ");
		sbr.append(" 			 ORDER BY LASTUPDATE, DECODE(FLAG, 'Y', 0, 99) ");
		sbr.append(" 			 FETCH FIRST 1 ROWS ONLY ");
		sbr.append(" 			),  ");
		sbr.append(" 			1 ");
		sbr.append(" 		) > 0 THEN 'Y' ELSE 'N' END AS UNDER_THE_RULE ");
		sbr.append(" 	FROM TBORG_MEMBER_PLURALISM PT ");
		sbr.append(" 	INNER JOIN TBORG_MEMBER_ROLE MR ON PT.EMP_ID = MR.EMP_ID AND MR.IS_PRIMARY_ROLE = 'N' ");
		sbr.append(" 	WHERE (TRUNC(PT.TERDTE) >= TRUNC(SYSDATE) OR PT.TERDTE IS NULL) ");
		sbr.append(" 	AND PT.EMP_ID = :bossEmpID ");
		sbr.append(" )WHERE UNDER_THE_RULE = 'Y' ");
		
		QueryConditionIF qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL).setQueryString(sbr.toString());
		qc.setObject("bossEmpID" , bossEmpId)
		  .setObject("keyEmpID", empId);
		List result = ((List<Map>)dam_obj.exeQueryWithoutSort(qc));

		return CollectionUtils.isNotEmpty(result);
	}
	
	public List<String> checkBossBossJurisdiction(String creator , String empId) throws Exception{
		List<String> supervisoList = new ArrayList<String>();
		//個金OP
		if(chkRole(creator , PERSONAL_FINANCE_OP)){
			//個金主管
			Integer cnt = queryOtherRoleCnt(empId , PERSONAL_FINANCE_BOSS);
			if(cnt == null || cnt < 1){
				supervisoList.add("個金");
				supervisoList.add("個金");
			}
				
		}
		
//		chkRole(creator , LEGAL_PERSON_FINANCE_OP) && !chkRole(empId, LEGAL_PERSON_FINANCE_BOSS)
		//鑑機人員為企金OP，因此檢查主管是否為企金主管
		else if(chkRole(creator , LEGAL_PERSON_FINANCE_OP) && !chkRole(empId, LEGAL_PERSON_FINANCE_BOSS)){
			supervisoList.add("企金");
			supervisoList.add("企金");
		}
		
		return supervisoList;
	}
	
	public boolean checkIsLegalPerson(String emp_id) throws Exception{
		Integer cnt = queryOtherRoleCnt(emp_id , LEGAL_PERSON_FINANCE_OP , LEGAL_PERSON_FINANCE_BOSS);
		return cnt != null && cnt > 0;
	}
	
	/**判斷是否為銀行員工**/
	public boolean checkIsMember(String empId) throws DAOException, JBranchException{
		Map<String , Object> queryParamMap = new Hashtable<String , Object>();
		queryParamMap.put("empId", empId);
		return CollectionUtils.isNotEmpty(exeQueryWithoutSortForMap("select 1 from TBORG_MEMBER where emp_id = :empId ", queryParamMap));
	}
}
