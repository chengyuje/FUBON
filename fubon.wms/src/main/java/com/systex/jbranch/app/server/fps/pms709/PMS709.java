package com.systex.jbranch.app.server.fps.pms709;

import java.io.File;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jxl.Sheet;
import jxl.Workbook;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.pms708.PMS708InputVO;
import com.systex.jbranch.app.server.fps.pms708.PMS708OutputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * Copy Right Information : Forms Syntron <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : 新增/編輯FCH轉介客戶Controller<br>
 * Comments Name : PMS709.java<br>
 * Author : cty<br>
 * Date :2016年11月22日 <br>
 * Version : 1.0 <br>
 * Editor : cty<br>
 * Editor Date : 2016年11月22日<br>
 */
@Component("pms709")
@Scope("request")
public class PMS709 extends FubonWmsBizLogic
{
	public DataAccessManager dam = null;

	private Logger logger = LoggerFactory.getLogger(PMS709.class);

	/**
	 * 查詢
	 * @param body
	 * @param header
	 * @throws APException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void queryData(Object body, IPrimitiveMap header) throws APException
	{
		try
		{
			PMS709InputVO inputVO = (PMS709InputVO) body;
			PMS709OutputVO outputVO = new PMS709OutputVO();
			dam = this.getDataAccessManager();
			QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
	
			StringBuffer sql = new StringBuffer();
			
			sql.append("SELECT FCH_REF_YEARMON,                              ");
			sql.append("       CUST_ID,                                      ");
			sql.append("       FCH_AO_CODE,                                  ");
			sql.append("       FCH_EMP_ID,                                   ");
			sql.append("       FCH_EMP_NAME,                                 ");
			sql.append("       REF_BRANCH_NO,                                ");
			sql.append("       REF_AO_CODE,                                  ");
			sql.append("       REF_EMP_ID,                                 ");
			sql.append("       REF_EMP_NAME,                                 ");
			sql.append("       MODIFIER,                                     ");
			sql.append("       LASTUPDATE                                    ");
			sql.append("FROM TBPMS_FCH_REF_INPUT                             ");
			
			if(!StringUtils.isBlank(inputVO.getYearMon()))
			{
				sql.append("WHERE TRIM(FCH_REF_YEARMON) = :yearMon           ");
				condition.setObject("yearMon",inputVO.getYearMon().trim());
			}
			
			if(!StringUtils.isBlank(inputVO.getCust_ID()))
			{
				sql.append("AND TRIM(CUST_ID) = :cust_ID                     ");
				condition.setObject("cust_ID",inputVO.getCust_ID().trim());
			}
			
			sql.append("ORDER BY CUST_ID                                     ");
			condition.setQueryString(sql.toString());

			ResultIF largeAgrList = dam.executePaging(condition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
			
			int totalPage_i = largeAgrList.getTotalPage(); // 分頁用
			int totalRecord_i = largeAgrList.getTotalRecord(); // 分頁用
			outputVO.setLargeAgrList(largeAgrList); // data
			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
			outputVO.setTotalPage(totalPage_i);// 總頁次
			outputVO.setTotalRecord(totalRecord_i);// 總筆數
			this.sendRtnObject(outputVO);
		}
		catch (Exception e)
		{
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			throw new APException(e.getMessage());
		}
	
	}
	
	/**
	 * 新增
	 * @param body
	 * @param header
	 * @throws APException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
	public void addData(Object body, IPrimitiveMap header) throws APException
	{	
		int flag = 0;
		try {
			PMS709InputVO inputVO = (PMS709InputVO) body;
			PMS709OutputVO outputVO = new PMS709OutputVO();
			
			//校驗主鍵
			StringBuffer sb1 = new StringBuffer();
			dam = this.getDataAccessManager();
			QueryConditionIF qc1 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb1.append("   SELECT COUNT(*) AS NUM FROM TBPMS_FCH_REF_INPUT ");
			sb1.append("     WHERE FCH_REF_YEARMON = :YEARMON              ");
			sb1.append("  	 AND CUST_ID  = :CUST_ID                       ");
			qc1.setObject("YEARMON",inputVO.getYearMon()                    );
			qc1.setObject("CUST_ID",inputVO.getCust_ID()                    );
			qc1.setQueryString(sb1.toString());
			List<Map<String, Object>> result = dam.exeQuery(qc1);
			int pk = Integer.parseInt((String)result.get(0).get("NUM").toString());
			if(pk!=0){
				throw new APException("已存在當前轉介年月和客戶ID");
			}
//			
//			//查詢FCH_AO_DATE
////			sb1.setLength(0);
////			QueryConditionIF qcAoDate = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
////			sb1.append("	SELECT TO_CHAR(MAX(NVL(T.CHG_DATE,T.LETGO_DATETIME)),'YYYYMMDD')  AS fch_ao_date                				   ");
////			sb1.append("	FROM TBCRM_CUST_AOCODE_CHGLOG T                                                                ");
////			sb1.append("	WHERE T.CUST_ID = :CUST_ID                                                                     ");
////			sb1.append("	      AND NVL(T.CHG_DATE,T.LETGO_DATETIME) < LAST_DAY(TO_DATE(:FCH_REF_YEARMON,'YYYYMM'))      ");
////			qcAoDate.setObject("CUST_ID", inputVO.getCust_ID());
////			qcAoDate.setObject("FCH_REF_YEARMON", inputVO.getYearMon());
////			qcAoDate.setQueryString(sb1.toString());
////			List<Map<String, Object>> resultAoDate = dam.exeQuery(qcAoDate);
////			String fch_ao_date = "";
////			if (resultAoDate!=null && resultAoDate.size()>0 && resultAoDate.get(0).get("FCH_AO_DATE")!=null){
////				fch_ao_date = (String) resultAoDate.get(0).get("FCH_AO_DATE");
////			}else{
////				throw new APException("輸入的FCH AO_CODE轉介資料不存在!");
////			}
//			sb1.setLength(0);
//			QueryConditionIF qcAoDate = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
//			sb1.append("  SELECT TO_CHAR(ADD_MONTHS(TO_DATE(YEARMONTH,'YYYYMM'),1),'YYYYMMDD') YEARMONTH ");
//			sb1.append("  FROM	TBPMS_CUST_AO_ME ");
//			sb1.append("  WHERE  CUST_ID = :CUST_ID ");
//			sb1.append("  AND	YEARMONTH = TO_CHAR(ADD_MONTHS(TO_DATE(:YEARMON,'YYYYMM'),-1),'YYYYMM') ");
//			sb1.append("  AND	AO_CODE = :AO_CODE ");
//			qcAoDate.setObject("CUST_ID", inputVO.getCust_ID().trim());
//			qcAoDate.setObject("YEARMON",inputVO.getYearMon().trim());
//			qcAoDate.setObject("AO_CODE",inputVO.getFch_AO_CODE());
//			qcAoDate.setQueryString(sb1.toString());
//			List<Map<String, Object>> resultAoDate = dam.exeQuery(qcAoDate);
//			String fch_ao_date = "";
//			if (resultAoDate!=null && resultAoDate.size()>0){
//				fch_ao_date = (String) resultAoDate.get(0).get("YEARMONTH");
//			}else{
//				throw new APException("該客戶轉介前實際FCH AO_CODE不符合!");
//			}
//			
//			//查詢FCH_EMP_ID
//			sb1.setLength(0);
//			QueryConditionIF qcFCH = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
//			sb1.append("	SELECT T.EMP_ID  AS fch_emp_id                                           ");
//			sb1.append("	FROM TBPMS_SALES_AOCODE_REC T                                            ");
//			sb1.append("	WHERE T.AO_CODE = :FCH_AO_CODE                                           ");
//			sb1.append("	   AND LAST_DAY(TO_DATE(:FCH_REF_YEARMON,'YYYYMM')) BETWEEN T.START_TIME AND T.END_TIME  ");
//			qcFCH.setObject("FCH_AO_CODE", inputVO.getFch_AO_CODE());
//			qcFCH.setObject("FCH_REF_YEARMON", inputVO.getYearMon());
//			qcFCH.setQueryString(sb1.toString());
//			List<Map<String, Object>> resultFCH = dam.exeQuery(qcFCH);
//			String fch_emp_id = "";
//			if (resultFCH!=null && resultFCH.size()>0){
//				fch_emp_id = (String) resultFCH.get(0).get("FCH_EMP_ID");
//			}else{
//				throw new APException("輸入的FCH AO_CODE資料不存在!");
//			}
//			
//			//查詢FCH 分行別
//			sb1.setLength(0);
//			qcFCH = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
//			sb1.append("	SELECT DISTINCT T.DEPT_ID  AS FCH_DEPT_ID                              ");
//			sb1.append("	FROM TBPMS_EMPLOYEE_REC_N T                                            ");
//			sb1.append("	WHERE T.EMP_ID = :FCH_EMP_ID                                           ");
//			sb1.append("	   AND LAST_DAY(TO_DATE(:FCH_REF_YEARMON,'YYYYMM')) BETWEEN T.START_TIME AND T.END_TIME  ");
//			qcFCH.setObject("FCH_EMP_ID", fch_emp_id);
//			qcFCH.setObject("FCH_REF_YEARMON", inputVO.getYearMon());
//			qcFCH.setQueryString(sb1.toString());
//			List<Map<String, Object>> resultFchDept = dam.exeQuery(qcFCH);
//			String fch_dept_id = "";
//			if (resultFchDept!=null && resultFchDept.size()>0){
//				fch_dept_id = (String) resultFchDept.get(0).get("FCH_DEPT_ID");
//			}else{
//				throw new APException("輸入的FCH 員工資料不存在!");
//			}			
//
//			//查詢REF_EMP_ID
//			sb1.setLength(0);
//			QueryConditionIF qcREF = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
//			sb1.append("	SELECT T.EMP_ID            AS ref_emp_id                                 ");
//			sb1.append("	FROM TBPMS_SALES_AOCODE_REC T                                            ");
//			sb1.append("	WHERE T.AO_CODE = :REF_AO_CODE                                           ");
//			sb1.append("	   AND LAST_DAY(TO_DATE(:FCH_REF_YEARMON,'YYYYMM')) BETWEEN T.START_TIME AND T.END_TIME  ");
//			qcREF.setObject("REF_AO_CODE", inputVO.getRef_AO_CODE());
//			qcREF.setObject("FCH_REF_YEARMON", inputVO.getYearMon());
//			qcREF.setQueryString(sb1.toString());
//			List<Map<String, Object>> resultREF = dam.exeQuery(qcREF);
//			String ref_emp_id = "";
//			if (resultREF!=null && resultREF.size()>0){
//				ref_emp_id = (String) resultREF.get(0).get("REF_EMP_ID");
//			}else{
//				throw new APException("輸入的轉介後 AO_CODE不存在!");
//			}	
			
			//SQL指令
			StringBuffer sb = new StringBuffer();
			dam = this.getDataAccessManager();
			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb.append("   INSERT INTO TBPMS_FCH_REF_INPUT (FCH_REF_YEARMON,	 ");
			sb.append("  		CUST_ID,            				         ");
			sb.append("  		FCH_AO_CODE,           					     ");
			sb.append("  		FCH_EMP_ID,            					     ");
			sb.append("  		FCH_EMP_NAME,            					 ");
			sb.append("  		FCH_AO_DATE,            					 ");
			sb.append("  		FCH_BRANCH_NBR,            					 ");
			sb.append("  		REF_BRANCH_NO,            				     ");
			sb.append("  		REF_AO_CODE,            					 ");
			sb.append("  		REF_EMP_ID,            						 ");
			sb.append("  		REF_EMP_NAME,            					 ");
			sb.append("  		VERSION,            						 ");
			sb.append("  		CREATETIME,             					 ");
			sb.append("  		CREATOR,             						 ");
			sb.append("  		MODIFIER,         						     ");
			sb.append("  		LASTUPDATE )             					 ");
			sb.append("  	VALUES(:YEARMON,            				     ");
			sb.append("  		:CUST_ID,             				         ");
			sb.append("  		:FCH_AO_CODE,             					 ");
			sb.append("  		:FCH_EMP_ID,             					 ");
			sb.append("  		:FCH_EMP_NAME,             					 ");
			sb.append("  		TO_DATE(:FCH_AO_DATE,'YYYYMMDD'),      		 ");
			sb.append("  		:FCH_BRANCH_NBR,      		                 ");
			sb.append("  		:REF_BRANCH_NO,             			     ");
			sb.append("  		:REF_AO_CODE,             					 ");
			sb.append("  		:REF_EMP_ID,             					 ");
			sb.append("  		:REF_EMP_NAME,             					 ");
			sb.append("  		0,           					             ");
			sb.append("  		SYSDATE,           				             ");
			sb.append("  		:CREATOR,            					     ");
			sb.append("  		:MODIFIER,         					         ");
			sb.append("  		SYSDATE)          				             ");
			qc.setObject("YEARMON",inputVO.getYearMon().trim()                );
			qc.setObject("CUST_ID",inputVO.getCust_ID().trim()                );
			qc.setObject("FCH_AO_CODE",inputVO.getFch_AO_CODE().trim()        );
			qc.setObject("FCH_EMP_ID",inputVO.getFCH_EMP_ID().trim()   		  );
			qc.setObject("FCH_EMP_NAME",inputVO.getFch_EMP_name().trim()      );
			qc.setObject("FCH_AO_DATE",inputVO.getFCH_AO_DATE() 	          );
			qc.setObject("FCH_BRANCH_NBR",inputVO.getFCH_BRANCH_NBR()         );
			qc.setObject("REF_BRANCH_NO",inputVO.getRef_BRANCH_NO().trim()    );
			qc.setObject("REF_AO_CODE",inputVO.getRef_AO_CODE().trim()        );
			qc.setObject("REF_EMP_ID",inputVO.getREF_EMP_ID().trim()          );
			qc.setObject("REF_EMP_NAME",inputVO.getRef_EMP_NAME().trim()      );
			qc.setObject("CREATOR", inputVO.getUserId().trim()                );
			qc.setObject("MODIFIER", inputVO.getUserId().trim()               );
			qc.setQueryString(sb.toString());
			dam.exeUpdate(qc);
			flag++;
			outputVO.setFlag(flag);
			this.sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			throw new APException(e.getMessage());
		}
		
	}
	
	/**
	 * 刪除
	 * @param body
	 * @param header
	 * @throws APException
	 */
	@SuppressWarnings("rawtypes")
	public void deleteData(Object body, IPrimitiveMap header) throws APException
	{	
		int flag = 0;
		try {
			
		
			PMS709InputVO inputVO = (PMS709InputVO) body;
			PMS709OutputVO outputVO = new PMS709OutputVO();
			dam = this.getDataAccessManager();
			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
			//SQL指令
			sb.append("	DELETE FROM TBPMS_FCH_REF_INPUT    ");
			sb.append("		WHERE CUST_ID = :CUST_ID       ");
			sb.append("		AND FCH_REF_YEARMON = :YEARMON ");
			qc.setObject("CUST_ID", inputVO.getCust_ID().trim());
			qc.setObject("YEARMON",inputVO.getYearMon().trim());
			qc.setQueryString(sb.toString());
			dam.exeUpdate(qc);
			flag++;
			outputVO.setFlag(flag);
			inputVO.setCust_ID("");
			this.sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			throw new APException(e.getMessage());
		}
		
	}
	
	/**
	 * 修改
	 * @param body
	 * @param header
	 * @throws APException
	 */
	@SuppressWarnings("rawtypes")
	public void editData(Object body, IPrimitiveMap header) throws APException
	{	
		int flag = 0;
		try {
			PMS709InputVO inputVO = (PMS709InputVO) body;
			PMS709OutputVO outputVO = new PMS709OutputVO();
			
			//SQL指令
			StringBuffer sb = new StringBuffer();
			dam = this.getDataAccessManager();
			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		    sb.append("  UPDATE TBPMS_FCH_REF_INPUT                        ");
			sb.append("  	SET	FCH_AO_CODE = :FCH_AO_CODE,                ");
			sb.append("  		FCH_EMP_NAME = :FCH_EMP_NAME,                  ");
			sb.append("  		FCH_BRANCH_NBR = :FCH_BRANCH_NBR,        ");
			sb.append("  		FCH_AO_DATE = TO_DATE(:FCH_AO_DATE,'YYYYMMDD'),     ");
			sb.append("  		REF_BRANCH_NO = :REF_BRANCH_NO,            ");
			sb.append("  		REF_AO_CODE = :REF_AO_CODE,                ");
			sb.append("  		REF_EMP_NAME = :REF_EMP_NAME,              ");
			sb.append("  		MODIFIER = :MODIFIER,                      ");
			sb.append("  		LASTUPDATE = SYSDATE                       ");
			sb.append("  	WHERE CUST_ID = :CUST_ID                       ");
			sb.append("  	AND	FCH_REF_YEARMON = :YEARMON                 ");
			qc.setObject("FCH_AO_CODE",inputVO.getFch_AO_CODE().trim()      );
			qc.setObject("FCH_EMP_NAME",inputVO.getFch_EMP_name().trim()    );
			qc.setObject("FCH_BRANCH_NBR",inputVO.getFCH_BRANCH_NBR().trim()    );
			qc.setObject("FCH_AO_DATE",inputVO.getFCH_AO_DATE().trim()    );
			qc.setObject("REF_BRANCH_NO",inputVO.getRef_BRANCH_NO().trim()  );
			qc.setObject("REF_AO_CODE",inputVO.getRef_AO_CODE().trim()      );
			qc.setObject("REF_EMP_NAME",inputVO.getRef_EMP_NAME().trim()    );
			qc.setObject("MODIFIER", inputVO.getUserId().trim()             );
			qc.setObject("CUST_ID", inputVO.getCust_ID().trim()             );
			qc.setObject("YEARMON",inputVO.getYearMon().trim()              );
			qc.setQueryString(sb.toString());
			dam.exeUpdate(qc);
			flag++;
			outputVO.setFlag(flag);
			this.sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			throw new APException(e.getMessage());
		}
		
	}
	
	/**
	 * 判斷年月 各資訊檢核
	 * @param body
	 * @param header
	 * @throws JBranchException 
	 */
	@SuppressWarnings("rawtypes")
	public void checkData(Object body, IPrimitiveMap header) throws APException
	{
		try {
			PMS709InputVO inputVO = (PMS709InputVO) body;
			PMS709OutputVO outputVO = new PMS709OutputVO();
			StringBuffer sb1 = new StringBuffer();
			dam = this.getDataAccessManager();
			//error msg增加訊息
			String errorMessage="";
			
			//SQL指令
			StringBuffer sb = new StringBuffer();
			QueryConditionIF qcCust = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		    sb.append("  SELECT CUST_ID TBPMS_FCH_REF_INPUT ");
			sb.append("  FROM	TBPMS_CUST_AO_ME ");
			sb.append("  WHERE  CUST_ID = :CUST_ID ");
			sb.append("  AND	YEARMONTH = :YEARMON ");
			qcCust.setObject("CUST_ID", inputVO.getCust_ID().trim());
			qcCust.setObject("YEARMON",inputVO.getYearMon().trim() );
			qcCust.setQueryString(sb.toString());
			//set flag list
			String custId="";
			List<Map<String, Object>> resultCust = dam.exeQuery(qcCust);
			if (resultCust!=null && resultCust.size()>0){
				custId = (String) resultCust.get(0).get("CUST_ID");
			}else{
				if(!errorMessage.equals(""))
					errorMessage+=",";
				errorMessage+="輸入的[年月該客戶ID]不存在! \n";
			}	
			
			//查詢FCH_AO_DATE
			sb1.setLength(0);
			QueryConditionIF qcAoDate = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb1.append("  SELECT TO_CHAR(TO_DATE(YEARMONTH,'YYYYMM'),'YYYYMMDD') YEARMONTH ");
			sb1.append("  FROM	TBPMS_CUST_AO_ME ");
			sb1.append("  WHERE  CUST_ID = :CUST_ID ");
			sb1.append("  AND	YEARMONTH = :YEARMON ");
			sb1.append("  AND	AO_CODE = :AO_CODE ");
			qcAoDate.setObject("CUST_ID", inputVO.getCust_ID().trim());
			qcAoDate.setObject("YEARMON",inputVO.getYearMon().trim());
			qcAoDate.setObject("AO_CODE",inputVO.getFch_AO_CODE());
			qcAoDate.setQueryString(sb1.toString());
			List<Map<String, Object>> resultAoDate = dam.exeQuery(qcAoDate);
			String fch_ao_date = "";
			if (resultAoDate!=null && resultAoDate.size()>0) {
				fch_ao_date = (String) resultAoDate.get(0).get("YEARMONTH");
				outputVO.setFCH_AO_DATE(fch_ao_date);
			}else{
				if(!errorMessage.equals(""))
					errorMessage+=",";
				errorMessage+="該客戶轉介前實際[FCH AO_CODE]不符合! \n";
			}
			//查詢FCH_EMP_ID  分行 
			sb1.setLength(0);
			QueryConditionIF qcFCH = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb1.append("    select DISTINCt T.EMP_ID,e.ROLE_ID,e.DEPT_ID                  ");
			sb1.append("    from TBPMS_SALES_AOCODE_REC T                       ");
			sb1.append("    LEFT JOIN                                           ");
			sb1.append("    (                                                   ");
			sb1.append("    select EMP_ID, ROLE_ID,DEPT_ID                              ");
			sb1.append("    from TBPMS_EMPLOYEE_REC_N emp                       ");
			sb1.append("    where LAST_DAY(TO_DATE(:FCH_REF_YEARMON,'YYYYMM'))  ");
			sb1.append("    BETWEEN emp.START_TIME AND emp.END_TIME             ");
			sb1.append("    ) e                                                 ");
			sb1.append("    on e.emp_id = t.emp_id                              ");
			sb1.append("    and e.role_id                                       ");
			sb1.append("    in                                                  ");
			sb1.append("    (                                                   ");
			sb1.append("    SELECT PARAM_CODE                                   ");
			sb1.append("    FROM TBSYSPARAMETER                                 ");
			sb1.append("    WHERE PARAM_TYPE IN ('FUBONSYS.FCH_ROLE')            ");
			sb1.append("    )                                                   ");
			sb1.append("    where AO_CODE =:FCH_AO_CODE                         ");
			sb1.append("    AND LAST_DAY(TO_DATE(:FCH_REF_YEARMON,'YYYYMM'))    ");
			sb1.append("    BETWEEN T.START_TIME AND T.END_TIME                 ");
			qcFCH.setObject("FCH_AO_CODE", inputVO.getFch_AO_CODE());
			qcFCH.setObject("FCH_REF_YEARMON", inputVO.getYearMon());
			qcFCH.setQueryString(sb1.toString());
			List<Map<String, Object>> resultFchDept = dam.exeQuery(qcFCH);
			String fch_dept_id = "";
			String fch_emp_id = "";
			if (resultFchDept!=null && resultFchDept.size()>0){
				fch_dept_id = (String) resultFchDept.get(0).get("DEPT_ID");
				fch_emp_id = (String) resultFchDept.get(0).get("EMP_ID");
				if(resultFchDept.get(0).get("ROLE_ID")==null){
					if(!errorMessage.equals(""))
						errorMessage+=",";
					errorMessage+="輸入的[FCH AO_CODE] 對應的理專不是FCH! \n";
				}else{
					outputVO.setFCH_BRANCH_NBR(fch_dept_id);
					outputVO.setFCH_EMP_ID(fch_emp_id);
				}
			}else{
				if(!errorMessage.equals(""))
					errorMessage+=",";
				errorMessage+="輸入的[FCH_AOCODE 員工資料]不存在! \n";
//				throw new APException("輸入的FCH 員工資料不存在!");
			}		
			
			
			//查詢BRANCH_NO
			sb1.setLength(0);
			QueryConditionIF qcREFB = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb1.append("	SELECT T.BRANCH_NBR  ");
			sb1.append("	FROM TBPMS_ORG_REC_N T          ");
			sb1.append("	WHERE T.BRANCH_NBR  = :BRANCH_NBR    ");
			sb1.append("	   AND LAST_DAY(TO_DATE(:YEARMON,'YYYYMM')) BETWEEN T.START_TIME AND T.END_TIME  ");
			qcREFB.setObject("BRANCH_NBR", inputVO.getRef_BRANCH_NO());
			qcREFB.setObject("YEARMON", inputVO.getYearMon());
			qcREFB.setQueryString(sb1.toString());
			List<Map<String, Object>> resultREFB = dam.exeQuery(qcREFB);
			String ref_B = "";
			if (resultREFB!=null && resultREFB.size()>0){
				ref_B = (String) resultREFB.get(0).get("BRANCH_NBR");
			}else{
				if(!errorMessage.equals(""))
					errorMessage+=",";
				errorMessage+="輸入的[轉介後分行代碼]不存在! \n";
			}	
			

			//查詢REF_EMP_ID
			sb1.setLength(0);
			QueryConditionIF qcREF = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb1.append("    select DISTINCt T.EMP_ID,e.ROLE_ID                  ");
			sb1.append("    from TBPMS_SALES_AOCODE_REC T                       ");
			sb1.append("    LEFT JOIN                                           ");
			sb1.append("    (                                                   ");
			sb1.append("    select EMP_ID, ROLE_ID                              ");
			sb1.append("    from TBPMS_EMPLOYEE_REC_N emp                       ");
			sb1.append("    where LAST_DAY(TO_DATE(:FCH_REF_YEARMON,'YYYYMM'))  ");
			sb1.append("    BETWEEN emp.START_TIME AND emp.END_TIME             ");
			sb1.append("    ) e                                                 ");
			sb1.append("    on e.emp_id = t.emp_id                              ");
			sb1.append("    and e.role_id                                       ");
			sb1.append("    in                                                  ");
			sb1.append("    (                                                   ");
			sb1.append("    SELECT PARAM_CODE                                   ");
			sb1.append("    FROM TBSYSPARAMETER                                 ");
			sb1.append("    WHERE PARAM_TYPE IN ('FUBONSYS.FC_ROLE')            ");
			sb1.append("    )                                                   ");
			sb1.append("    where AO_CODE =:REF_AO_CODE                         ");
			sb1.append("    AND LAST_DAY(TO_DATE(:FCH_REF_YEARMON,'YYYYMM'))    ");
			sb1.append("    BETWEEN T.START_TIME AND T.END_TIME                 ");
			qcREF.setObject("REF_AO_CODE", inputVO.getRef_AO_CODE());
			qcREF.setObject("FCH_REF_YEARMON", inputVO.getYearMon());
			qcREF.setQueryString(sb1.toString());
			List<Map<String, Object>> resultREF = dam.exeQuery(qcREF);
			String ref_emp_id = "";
			if (resultREF!=null && resultREF.size()>0 && resultREF.get(0).get("ROLE_ID")!=null){
				ref_emp_id = (String) resultREF.get(0).get("EMP_ID");
				outputVO.setREF_EMP_ID(ref_emp_id);
			}else{
				if(!errorMessage.equals(""))
					errorMessage+=",";
				if( resultREF.size()>0 && resultREF.get(0).get("ROLE_ID")==null)
					errorMessage+="輸入的[轉介後 AO_CODE], 其角色不是FC! \n";
				else
					errorMessage+="輸入的[轉介後 AO_CODE]不存在! \n";
			}	
			

		
		
			outputVO.setErrorMessage(errorMessage);
			this.sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			throw new APException(e.getMessage());
		}
		
	}
	
	
}
