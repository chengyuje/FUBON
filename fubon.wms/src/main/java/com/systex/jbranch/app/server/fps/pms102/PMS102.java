package com.systex.jbranch.app.server.fps.pms102;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.pms000.PMS000;
import com.systex.jbranch.app.server.fps.pms000.PMS000InputVO;
import com.systex.jbranch.app.server.fps.pms000.PMS000OutputVO;
import com.systex.jbranch.app.server.fps.pms104.PMS104InputVO;
import com.systex.jbranch.app.server.fps.pms104.PMS104OutputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;
/**
 * Copy Right Information : <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description :潛力金流名單<br>
 * Comments Name : PMS102.java<br>
 * Author : KevinHsu<br>
 * Date :2016年06月17日 <br>
 * Version : 1.0 <br>
 * Editor : KevinHsu<br>
 * Editor Date : 2017年01月12日<br>
 */
@Component("pms102")
@Scope("request")
public class PMS102 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PMS102.class);

	/**
	 * 主查詢
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void queryData(Object body, IPrimitiveMap header)
			throws JBranchException {
		DataAccessManager dam = this.getDataAccessManager();
		// 輸入vo
		PMS102InputVO inputVO = (PMS102InputVO) body;
		// 輸出vo
		PMS102OutputVO outputVO = new PMS102OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		try {
			
			String roleID = (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);
			XmlInfo xmlInfo = new XmlInfo();
			Map<String, String> fcMap = xmlInfo.doGetVariable("FUBONSYS.FC_ROLE",FormatHelper.FORMAT_2); // 理專
			Map<String, String> psopMap = xmlInfo.doGetVariable("FUBONSYS.PSOP_ROLE", FormatHelper.FORMAT_2); // OP
			Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); // 總行人員
			
			// 筆數限制
			Map<String, String> qry_max_limit_xml = xmlInfo.doGetVariable("SYS.MAX_QRY_ROWS", FormatHelper.FORMAT_2);
			
			
			// 取得查詢資料可視範圍
			PMS000 pms000 = (PMS000) PlatformContext.getBean("pms000");
			PMS000InputVO pms000InputVO = new PMS000InputVO();
			pms000InputVO.setReportDate(inputVO.getsCreDate());
			PMS000OutputVO pms000outputVO = pms000.getOrg(pms000InputVO);
			// ==查詢==
			//2017/06/04 新增ALR_PLAN_AMT
			sb.append("  WITH ESTAMT AS ( ");
			sb.append("  SELECT ");
			sb.append("    SUM(EST_AMT) AS ALR_PLAN_AMT, ");
			sb.append("    CUST_ID ");
			sb.append("  FROM TBPMS_SALES_PLAN ");
			sb.append("  GROUP BY CUST_ID ");
			sb.append("), ");
			sb.append("INF AS ");
			sb.append("( ");
			sb.append("   	SELECT   C.cust_id, ");
			sb.append("   			 C.branch_nbr, ");
			sb.append("   			 C.ao_code, ");
			sb.append("   			 O.region_center_id,  ");
			sb.append("   			 O.region_center_name,  ");
			sb.append("   			 O.branch_area_id,  ");
			sb.append("   			 O.branch_area_name,  ");
			sb.append("   			 O.branch_name,  ");
			sb.append("     row_number() over (partition by C.CUST_ID order by C.START_TIME desc) RN");
			sb.append("   	FROM   tbpms_cust_rec_n C "); 
			sb.append("   	left join tbpms_org_rec_n O  ");
			sb.append("   	ON C.branch_nbr = O.branch_nbr ");
			sb.append("   	AND Last_day(To_date(:DATA_DATEE, 'YYYYMM')) BETWEEN O.start_time AND O.end_time ");
			sb.append("   	WHERE Last_day(To_date(:DATA_DATEE, 'YYYYMM')) BETWEEN C.start_time AND C.end_time ");
			sb.append(") ");

			sb.append("SELECT SUBSTR(A.DATA_DATE,0,6) AS PLAN_YEARMON,A.CUST_ID, A.CUST_NAME, NVL(A.TOTAL_AMT,0) AS TOTAL_AMT, NVL(EAMT.ALR_PLAN_AMT,0) AS ALR_PLAN_AMT,NVL(A.TOTAL_AMT,0)-NVL(EAMT.ALR_PLAN_AMT,0) AS UNPLAN_AMT, ");
			sb.append("INF.AO_CODE,INF.BRANCH_NBR,INF.BRANCH_NAME, ");
			sb.append("INF.BRANCH_AREA_ID,INF.BRANCH_AREA_NAME,INF.REGION_CENTER_ID, ");

			sb.append("INF.REGION_CENTER_NAME,Trunc(A.CREATETIME) as CREATETIME FROM  TBPMS_POT_CF_MAST A ");
			//原本的 未調校sql 
//			sb.append("LEFT JOIN (SELECT A.* , B.REGION_CENTER_ID , B.REGION_CENTER_NAME , B.BRANCH_AREA_ID,B.BRANCH_AREA_NAME, B.BRANCH_NAME FROM (SELECT DISTINCT CUST_ID,BRANCH_NBR,AO_CODE FROM TBPMS_CUST_REC_N ) A   ");
//			sb.append("LEFT JOIN TBPMS_ORG_REC_N B ON A.BRANCH_NBR=B.BRANCH_NBR AND TO_DATE(:DATA_DATEE || '01','YYYYMMDD') BETWEEN B.START_TIME AND B.END_TIME"
//					//補上金額錯誤問題  多串三倍  因時間區間無多塞選
////					+ " and To_date(:DATA_DATEE "                    
////					+" || '01', 'YYYYMMDD' )BETWEEN a.start_time and a.end_time "					
//					+ ") INF  ON A.CUST_ID = INF.CUST_ID   ");
			sb.append("LEFT JOIN INF ON A.CUST_ID = INF.CUST_ID ");
			sb.append("  LEFT JOIN ESTAMT EAMT         ");
			sb.append("   ON EAMT.CUST_ID=A.CUST_ID  ");
			sb.append("WHERE ROWNUM BETWEEN 1 AND :qry_max_limit  ");
			sb.append("AND INF.RN = 1 "); //#20200302_CBS_麗文_#78672 潛力金流名單重覆
			sb.append("AND TOTAL_AMT > 0 ");//#0002166 : 潛力金流金額為0不顯示  (TOTAL_AMT!='0')
			
			// ==查詢條件==
			// 統計日期
			if (!StringUtils.isBlank(inputVO.getsCreDate())){
				sb.append("AND SUBSTR(A.DATA_DATE,0,6) = :DATA_DATEE ");
				queryCondition.setObject("DATA_DATEE",inputVO.getsCreDate());
			}
			// 營運區代號
			if (!StringUtils.isBlank(inputVO.getBranch_area_id())) {
				//sb.append("AND BRANCH_AREA_ID = :BRANCH_AREA_IDD ");
				sb.append("  and BRANCH_NBR IN ( ");
				sb.append("    SELECT BRANCH_NBR ");
				sb.append("    FROM VWORG_DEFN_BRH ");
				sb.append("    WHERE DEPT_ID = :BRANCH_AREA_IDD ");
				sb.append("  ) ");
				queryCondition.setObject("BRANCH_AREA_IDD",inputVO.getBranch_area_id());
			}else {
				// 登入非總行人員強制加營運區
				if (!headmgrMap.containsKey(roleID)) {
					//sb.append("and BRANCH_AREA_ID IN (:BRANCH_AREA_IDD) ");
					sb.append("  and BRANCH_NBR IN ( ");
					sb.append("    SELECT BRANCH_NBR ");
					sb.append("    FROM VWORG_DEFN_BRH ");
					sb.append("    WHERE DEPT_ID IN (:BRANCH_AREA_IDD) ");
					sb.append("  ) ");
					queryCondition.setObject("BRANCH_AREA_IDD",pms000outputVO.getV_areaList());
				}
			}
			// 分行代號
			if (!StringUtils.isBlank(inputVO.getBranch_nbr())) {
				sb.append("AND BRANCH_NBR = :BRANCH_NBRR ");
				queryCondition.setObject("BRANCH_NBRR", inputVO.getBranch_nbr());
			}else {
				// 登入非總行人員強制加分行
				if (!headmgrMap.containsKey(roleID)) {
					sb.append("and BRANCH_NBR IN (:BRANCH_NBRR) ");
					queryCondition.setObject("BRANCH_NBRR",pms000outputVO.getV_branchList());
				}
			}
			// 理專ao_code
			if (!StringUtils.isBlank(inputVO.getAo_code())) {
				sb.append("AND AO_CODE = :AO_CODEE ");
				queryCondition.setObject("AO_CODEE",inputVO.getAo_code());
			}else {
				// 登入為銷售人員強制加AO_CODE
				if (fcMap.containsKey(roleID) || psopMap.containsKey(roleID)) {
					sb.append(" and AO_CODE IN (:AO_CODEE) ");
					queryCondition.setObject("AO_CODEE", pms000outputVO.getV_aoList());
				}
			}
			// 客戶id
			if (!StringUtils.isBlank(inputVO.getCUST_ID())) {
				sb.append("AND A.CUST_ID =:CUST_IDD ");
				queryCondition.setObject("CUST_IDD",inputVO.getCUST_ID().toUpperCase().trim());
			}
			
			
			// 商品類型
			//保險
			if(!StringUtils.isBlank(inputVO.getPROD_TYPE()) && inputVO.getPROD_TYPE().equals("05")){
				sb.append("and exists  (select * from TBPMS_POT_CF_DTL_INS INS where substr(INS.DATA_DATE,0,6)=:DATA_DATEE and A.CUST_ID=INS.CUST_ID) ");
			}
			//投資商品
			if(!StringUtils.isBlank(inputVO.getPROD_TYPE()) && !(inputVO.getPROD_TYPE().equals("05"))){
				sb.append("and exists  (select * from TBPMS_POT_CF_DTL_INV INV where substr(INV.DATA_DATE,0,6)=:DATA_DATEE and A.CUST_ID=INV.CUST_ID ");
				if(!StringUtils.isBlank(inputVO.getPROD_TYPE()) && inputVO.getPROD_TYPE().equals("01")){ //基金		
					sb.append("AND INV.PRD_TYPE = 'MFD') ");				
				}else if(!StringUtils.isBlank(inputVO.getPROD_TYPE()) && inputVO.getPROD_TYPE().equals("02")){ //ETD/海外股票
					sb.append("AND INV.PRD_TYPE in ('ETF','STOCK')) ");	
				}else if(!StringUtils.isBlank(inputVO.getPROD_TYPE()) && inputVO.getPROD_TYPE().equals("03")){ //SI/SN
					sb.append("AND INV.PRD_TYPE in ('SI','SN')) ");	
				}else if(!StringUtils.isBlank(inputVO.getPROD_TYPE()) && inputVO.getPROD_TYPE().equals("04")){ //SI/SN
					sb.append("AND INV.PRD_TYPE = 'BND') ");	
				}
			}
			
			// 是否已規劃銷售計劃
			if (!StringUtils.isBlank(inputVO.getTYPE())) {
				if ("Y".equals(inputVO.getTYPE())) {
					sb.append("AND A.TOTAL_AMT>0 ");
				}
				if ("N".equals(inputVO.getTYPE())) {
					sb.append("AND A.UNPLAN_AMT=0 ");
				}
			}
			
		
//			sb.append(" GROUP BY A.CUST_ID, A.CUST_NAME,TRUNC(A.CREATETIME), ");
//			sb.append(" INF.AO_CODE,INF.BRANCH_NBR,INF.BRANCH_NAME,  ");
//			sb.append(" INF.BRANCH_AREA_ID,INF.BRANCH_AREA_NAME,INF.REGION_CENTER_ID,EAMT.ALR_PLAN_AMT, ");
//			if(!StringUtils.isBlank(inputVO.getPROD_TYPE()) && !(inputVO.getPROD_TYPE().equals("05")))
//			{
//				sb.append("C.PRD_TYPE,");
//			}else if(!StringUtils.isBlank(inputVO.getPROD_TYPE())){
//				sb.append("'INS',");
//			}
//			sb.append(" INF.REGION_CENTER_NAME,SUBSTR(A.DATA_DATE,0,6)  ");
			//效能影響註解調
//			sb.append(" ORDER BY UNPLAN_AMT "); 
			queryCondition.setQueryString(sb.toString());
			queryCondition.setObject("qry_max_limit", qry_max_limit_xml.get("2000"));

			// 查詢結果
			ResultIF list = dam.executePaging(queryCondition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
			int totalPage_i = list.getTotalPage(); // 分頁用
			int totalRecord_i = list.getTotalRecord(); // 分頁用
			outputVO.setResultList(list); // data
			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
			outputVO.setTotalPage(totalPage_i);// 總頁次
			outputVO.setTotalRecord(totalRecord_i);// 總筆數
			this.sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
	
	
	
	
	
	/** ==主初始查詢== **/
	public void bePlan(Object body, IPrimitiveMap header) throws JBranchException {

		PMS102InputVO inputVO = (PMS102InputVO) body;
		PMS102OutputVO OutputVO = new PMS102OutputVO();
		dam = this.getDataAccessManager();
		List<Map<String, Object>> List=new ArrayList<Map<String, Object>>();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		try {		
				
				//==查詢==
				StringBuffer sb = new StringBuffer();
				sb.append("  SELECT                           ");
				sb.append("  NVL(SUM(EST_AMT),0) as  PLAN_AMT                    ");
				sb.append("  FROM tbpms_sales_plan            ");
				sb.append("  WHERE 1=1           ");
				sb.append("  AND SRC_TYPE='2'                 ");
//				sb.append("  AND CLOSE_DATE>CREATETIME        ");
				sb.append("  AND CUST_ID=:CUST_ID             ");
				sb.append("  AND plan_yearmon>=:ym        ");
				//==查詢條件==			
				queryCondition.setQueryString(sb.toString());
				if (!StringUtils.isBlank(inputVO.getCUST_ID())) {
					queryCondition.setObject("CUST_ID", inputVO.getCUST_ID());
				}
				if (!StringUtils.isBlank(inputVO.getReportDate())) {
					queryCondition.setObject("ym", inputVO.getReportDate());
				}
				List = dam.exeQuery(queryCondition);
				OutputVO.setBePlanList(List);
				QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				StringBuffer sb_Ao = new StringBuffer();
				//==查詢==				
				sb_Ao.append("  SELECT                           ");
				sb_Ao.append("  NVL(SUM(EST_AMT),0) as  TOT_PLAN_AMT                    ");
				sb_Ao.append("  FROM tbpms_sales_plan            ");
				sb_Ao.append("  WHERE 1=1          ");
				sb_Ao.append("  AND SRC_TYPE='2'                 ");
//				sb_Ao.append("  AND CLOSE_DATE>CREATETIME        ");
				sb_Ao.append("  AND plan_yearmon>=:ym        ");
			
				//==查詢條件==			
				condition.setQueryString(sb_Ao.toString());
			
				if (!StringUtils.isBlank(inputVO.getReportDate())) {
					condition.setObject("ym", inputVO.getReportDate());
				}
				List = dam.exeQuery(condition);
				OutputVO.setTotallist(List);
				this.sendRtnObject(OutputVO);
				
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
	
	/**
	 * 拿總計
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */

	public void inquire2(Object body, IPrimitiveMap header) throws JBranchException {
		DataAccessManager dam = this.getDataAccessManager();
		// 輸入vo
		PMS102InputVO inputVO = (PMS102InputVO) body;
		// 輸出vo
		PMS102OutputVO outputVO = new PMS102OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		try {
			
			String roleID = (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);
			XmlInfo xmlInfo = new XmlInfo();
			Map<String, String> fcMap = xmlInfo.doGetVariable("FUBONSYS.FC_ROLE",FormatHelper.FORMAT_2); // 理專
			Map<String, String> psopMap = xmlInfo.doGetVariable("FUBONSYS.PSOP_ROLE", FormatHelper.FORMAT_2); // OP
			Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); // 總行人員
			
			// 筆數限制
			Map<String, String> qry_max_limit_xml = xmlInfo.doGetVariable("SYS.MAX_QRY_ROWS", FormatHelper.FORMAT_2);
			
			
			// 取得查詢資料可視範圍
			PMS000 pms000 = (PMS000) PlatformContext.getBean("pms000");
			PMS000InputVO pms000InputVO = new PMS000InputVO();
			pms000InputVO.setReportDate(inputVO.getsCreDate());
			PMS000OutputVO pms000outputVO = pms000.getOrg(pms000InputVO);
			// ==查詢==
			//2017/06/04 新增ALR_PLAN_AMT
			sb.append("  WITH ESTAMT AS (                  ");
			sb.append("  SELECT                            ");
			sb.append("    SUM(EST_AMT) AS ALR_PLAN_AMT,   ");
			sb.append("    CUST_ID                         ");
			sb.append("  FROM TBPMS_SALES_PLAN             ");
			sb.append("  GROUP BY CUST_ID)                 ");
			
			sb.append("SELECT SUM(A.TOTAL_AMT) AS TOTAL_AMT, NVL(SUM(EAMT.ALR_PLAN_AMT),0) AS ALR_PLAN_AMT,SUM(A.TOTAL_AMT)-NVL(SUM(EAMT.ALR_PLAN_AMT),0) AS UNPLAN_AMT ");
			sb.append(" FROM  TBPMS_POT_CF_MAST A   ");
			sb.append("LEFT JOIN (SELECT A.* , B.REGION_CENTER_ID , B.REGION_CENTER_NAME , B.BRANCH_AREA_ID,B.BRANCH_AREA_NAME, B.BRANCH_NAME FROM (SELECT DISTINCT CUST_ID,BRANCH_NBR,AO_CODE FROM  TBPMS_CUST_REC_N where Last_day(To_date(:DATA_DATEE, 'YYYYMM')) BETWEEN START_TIME AND END_TIME) A   ");
			sb.append("LEFT JOIN TBPMS_ORG_REC_N B ON A.BRANCH_NBR=B.BRANCH_NBR AND Last_day(To_date(:DATA_DATEE, 'YYYYMM')) BETWEEN B.START_TIME AND B.END_TIME  "
					//補上金額錯誤問題  多串三倍  因時間區間無多塞選
								
					+ ") INF  ON A.CUST_ID = INF.CUST_ID         ");
//			if(!StringUtils.isBlank(inputVO.getPROD_TYPE())){
//				sb.append("LEFT JOIN C ON A.CUST_ID = C.CUST_ID ");
//			}
			sb.append("  LEFT JOIN ESTAMT EAMT         ");
			sb.append("   ON EAMT.CUST_ID=INF.CUST_ID     ");
			sb.append("WHERE ROWNUM<= :qry_max_limit ");
			sb.append("AND TOTAL_AMT != '0' ");//#0002166 : 潛力金流金額為0不顯示
			
			// ==查詢條件==
			// 統計日期
			if (!StringUtils.isBlank(inputVO.getsCreDate())){
				sb.append("AND SUBSTR(A.DATA_DATE,0,6) = :DATA_DATEE ");
				queryCondition.setObject("DATA_DATEE",inputVO.getsCreDate());
			}
			// 營運區代號
			if (!StringUtils.isBlank(inputVO.getBranch_area_id())) {
				//sb.append("AND BRANCH_AREA_ID like :BRANCH_AREA_IDD ");
				sb.append("  and BRANCH_NBR IN ( ");
				sb.append("    SELECT BRANCH_NBR ");
				sb.append("    FROM VWORG_DEFN_BRH ");
				sb.append("    WHERE DEPT_ID like :BRANCH_AREA_IDD ");
				sb.append("  ) ");
				queryCondition.setObject("BRANCH_AREA_IDD","%" + inputVO.getBranch_area_id()+ "%");
			}else {
				// 登入非總行人員強制加營運區
				if (!headmgrMap.containsKey(roleID)) {
					//sb.append("and BRANCH_AREA_ID IN (:BRANCH_AREA_IDD) ");
					sb.append("  and BRANCH_NBR IN ( ");
					sb.append("    SELECT BRANCH_NBR ");
					sb.append("    FROM VWORG_DEFN_BRH ");
					sb.append("    WHERE DEPT_ID IN (:BRANCH_AREA_IDD) ");
					sb.append("  ) ");
					queryCondition.setObject("BRANCH_AREA_IDD",pms000outputVO.getV_areaList());
				}
			}
			// 分行代號
			if (!StringUtils.isBlank(inputVO.getBranch_nbr())) {
				sb.append("AND BRANCH_NBR like :BRANCH_NBRR ");
				queryCondition.setObject("BRANCH_NBRR","%" + inputVO.getBranch_nbr()+ "%");
			}else {
				// 登入非總行人員強制加分行
				if (!headmgrMap.containsKey(roleID)) {
					sb.append("and BRANCH_NBR IN (:BRANCH_NBRR) ");
					queryCondition.setObject("BRANCH_NBRR",pms000outputVO.getV_branchList());
				}
			}
			// 理專ao_code
			if (!StringUtils.isBlank(inputVO.getAo_code())) {
				sb.append("AND AO_CODE like :AO_CODEE ");
				queryCondition.setObject("AO_CODEE","%" + inputVO.getAo_code()+ "%");
			}else {
				// 登入為銷售人員強制加AO_CODE
				if (fcMap.containsKey(roleID) || psopMap.containsKey(roleID)) {
					sb.append(" and AO_CODE IN (:AO_CODEE) ");
					queryCondition.setObject("AO_CODEE", pms000outputVO.getV_aoList());
				}
			}
			// 客戶id
			if (!StringUtils.isBlank(inputVO.getCUST_ID())) {
				sb.append("AND A.CUST_ID =:CUST_IDD ");
				queryCondition.setObject("CUST_IDD",inputVO.getCUST_ID().toUpperCase().trim());
			}
//			// 商品類型
			if(!StringUtils.isBlank(inputVO.getPROD_TYPE()) && inputVO.getPROD_TYPE().equals("05")){
//				sb.append("AND C.PRD_TYPE ='INS' ");
				sb.append("and exists  (select * from TBPMS_POT_CF_DTL_INS INS where substr(INS.DATA_DATE,0,6)=:DATA_DATEE and A.CUST_ID=INS.CUST_ID) ");
			}
			//投資商品
			if(!StringUtils.isBlank(inputVO.getPROD_TYPE()) && !(inputVO.getPROD_TYPE().equals("05"))){
				sb.append("and exists  (select * from TBPMS_POT_CF_DTL_INV INV where substr(INV.DATA_DATE,0,6)=:DATA_DATEE and A.CUST_ID=INV.CUST_ID ");
				if(!StringUtils.isBlank(inputVO.getPROD_TYPE()) && inputVO.getPROD_TYPE().equals("01")){ //基金		
					sb.append("AND INV.PRD_TYPE = 'MFD') ");				
				}else if(!StringUtils.isBlank(inputVO.getPROD_TYPE()) && inputVO.getPROD_TYPE().equals("02")){ //ETD/海外股票
					sb.append("AND INV.PRD_TYPE in ('ETF','STOCK')) ");	
				}else if(!StringUtils.isBlank(inputVO.getPROD_TYPE()) && inputVO.getPROD_TYPE().equals("03")){ //SI/SN
					sb.append("AND INV.PRD_TYPE in ('SI','SN')) ");	
				}else if(!StringUtils.isBlank(inputVO.getPROD_TYPE()) && inputVO.getPROD_TYPE().equals("04")){ //SI/SN
					sb.append("AND INV.PRD_TYPE = 'BND') ");	
				}
			}
			
			// 是否已規劃銷售計劃
			if (!StringUtils.isBlank(inputVO.getTYPE())) {
				if ("Y".equals(inputVO.getTYPE())) {
					sb.append("AND A.TOTAL_AMT>0 ");
				}
				if ("N".equals(inputVO.getTYPE())) {
					sb.append("AND A.UNPLAN_AMT=0 ");
				}
			}
		
//			sb.append(" GROUP BY A.CUST_ID, A.CUST_NAME,TRUNC(A.CREATETIME), ");
//			sb.append(" INF.AO_CODE,INF.BRANCH_NBR,INF.BRANCH_NAME,  ");
//			sb.append(" INF.BRANCH_AREA_ID,INF.BRANCH_AREA_NAME,INF.REGION_CENTER_ID,EAMT.ALR_PLAN_AMT, ");
//			if(!StringUtils.isBlank(inputVO.getPROD_TYPE()) && !(inputVO.getPROD_TYPE().equals("05")))
//			{
//				sb.append("C.PRD_TYPE,");
//			}else if(!StringUtils.isBlank(inputVO.getPROD_TYPE())){
//				sb.append("'INS',");
//			}
//			sb.append(" INF.REGION_CENTER_NAME,SUBSTR(A.DATA_DATE,0,6)  ");
			sb.append(" ORDER BY UNPLAN_AMT "); 
			queryCondition.setQueryString(sb.toString());
			queryCondition.setObject("qry_max_limit", qry_max_limit_xml.get("2000"));

			// 查詢結果
			ResultIF list = dam.executePaging(queryCondition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
			int totalPage_i = list.getTotalPage(); // 分頁用
			int totalRecord_i = list.getTotalRecord(); // 分頁用
			outputVO.setResultList2(list); // data
			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
			outputVO.setTotalPage(totalPage_i);// 總頁次
			outputVO.setTotalRecord(totalRecord_i);// 總筆數
			this.sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
	
	

	/**
	 * 查詢明細
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void queryMod(Object body, IPrimitiveMap header) throws JBranchException {
		DataAccessManager dam = this.getDataAccessManager();
		// 輸入vo
		PMS102InputVO inputVO = (PMS102InputVO) body;
		// 輸出vo
		PMS102OutputVO outputVO = new PMS102OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		try {
			//==查詢==
			sb.append("SELECT * from  TBPMS_POT_CF_DTL_INS where 1=1 ");
			//==查詢條件==
			//客戶ID
			if (!StringUtils.isBlank(inputVO.getCUST_ID()))
				sb.append("  AND CUST_ID like :CUST_IDDD "); // 客戶ID
			
			// 統計日期
			if (!StringUtils.isBlank(inputVO.getsCreDate())){
				sb.append(" AND SUBSTR(DATA_DATE,0,6) = :DATA_DATEE ");
				queryCondition.setObject("DATA_DATEE",inputVO.getsCreDate());
			}
			queryCondition.setQueryString(sb.toString());
			//==查詢條件設定==
			//客戶ID
			if (!StringUtils.isBlank(inputVO.getCUST_ID()))
				queryCondition.setObject("CUST_IDDD", "%" + inputVO.getCUST_ID() + "%");
			
			//==查詢結果==
			List<Map<String, Object>> list1 = dam.exeQuery(queryCondition);
			outputVO.setResultList2(list1); // data
			this.sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
	/**
	 * 查詢明細2
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void getLeadsList(Object body, IPrimitiveMap header) throws JBranchException {
		DataAccessManager dam = this.getDataAccessManager();
		PMS102InputVO inputVO = (PMS102InputVO) body;
		PMS102OutputVO outputVO = new PMS102OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sb = new StringBuffer();
		try {
			//==查詢==
			sb.append("SELECT * from TBPMS_POT_CF_DTL_INV where 1=1  ");
			//==查詢條件==
			//客戶ID
			if (!StringUtils.isBlank(inputVO.getCUST_ID()))
				sb.append(" AND CUST_ID like :CUST_IDDD "); // 客戶ID
			
			// 統計日期
			if (!StringUtils.isBlank(inputVO.getsCreDate())){
				sb.append(" AND SUBSTR(DATA_DATE,0,6) = :DATA_DATEE ");
				queryCondition.setObject("DATA_DATEE",inputVO.getsCreDate());
			}
			if(!StringUtils.isBlank(inputVO.getPROD_TYPE())){
				sb.append("AND PRD_TYPE= :PROD_TYPE ");
				queryCondition.setObject("PROD_TYPE",inputVO.getPROD_TYPE());
			}
			queryCondition.setQueryString(sb.toString());
			
			
			//==查詢條件設定==
			//客戶ID
			if (!StringUtils.isBlank(inputVO.getCUST_ID()))
				queryCondition.setObject("CUST_IDDD", "%" + inputVO.getCUST_ID() + "%");
			
			//==查詢結果==
			List<Map<String, Object>> list1 = dam.exeQuery(queryCondition);
			outputVO.setResultList(list1);
			this.sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
	
	

	
}
