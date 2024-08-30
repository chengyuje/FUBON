package com.systex.jbranch.app.server.fps.pms101;

import java.text.ParseException;
import java.util.HashMap;
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
 * Copy Right Information :  <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description :金流名單<br>
 * Comments Name : PMS101.java<br>
 * Author : KevinHsu<br>
 * Date :2016年07月14日 <br>
 * Version : 1.0 <br>
 * Editor : KevinHsu<br>
 * Editor Date : 2017年01月12日<br>
 */
@Component("pms101")
@Scope("request")
public class PMS101 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PMS101.class);
	/**
	 * 未轉銷售計劃/已轉銷售計劃判斷
	 * @param body
	 * @param header
	 * @throws JBranchException
	 * @throws ParseException 
	 */
	public void queryDataY(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		PMS101InputVO inputVO = (PMS101InputVO) body;
		PMS101OutputVO outputVO = new PMS101OutputVO();
		PMS101Object_OutputVO PMS101Object_OutputVO = new PMS101Object_OutputVO();
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT ROWNUM,  ALLS.* ");
		
		Map<String ,Object> listy=new HashMap();
		listy.put("listy", queryData_List(body,header,"Y"));
		PMS101Object_OutputVO.setAllList(listy);
		this.sendRtnObject(PMS101Object_OutputVO);
	}
	public void queryDataN(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		PMS101InputVO inputVO = (PMS101InputVO) body;
		PMS101OutputVO outputVO = new PMS101OutputVO();
		PMS101Object_OutputVO PMS101Object_OutputVO = new PMS101Object_OutputVO();
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT ROWNUM,  ALLS.* ");
		
		Map<String ,Object> listy=new HashMap();
		listy.put("listn", queryData_List(body,header,"N"));
		PMS101Object_OutputVO.setAllList(listy);
		this.sendRtnObject(PMS101Object_OutputVO);
	}
	
	/**
	 * 主查詢
	 * @param body
	 * @param header
	 * @throws JBranchException
	 * @throws ParseException 
	 */
	public PMS101OutputVO queryData_List(Object body, IPrimitiveMap header,String Y_N) throws JBranchException, ParseException {

		PMS101InputVO inputVO = (PMS101InputVO) body;
		PMS101OutputVO outputVO = new PMS101OutputVO();
		
		String roleID = (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> fcMap = xmlInfo.doGetVariable("FUBONSYS.FC_ROLE", FormatHelper.FORMAT_2);         //理專
		Map<String, String> psopMap = xmlInfo.doGetVariable("FUBONSYS.PSOP_ROLE", FormatHelper.FORMAT_2);     //OP
		Map<String, String> bmmgrMap = xmlInfo.doGetVariable("FUBONSYS.BMMGR_ROLE", FormatHelper.FORMAT_2);    //個金主管
		Map<String, String> mbrmgrMap = xmlInfo.doGetVariable("FUBONSYS.MBRMGR_ROLE", FormatHelper.FORMAT_2);   //營運督導
		Map<String, String> armgrMap = xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2);   //區域中心主管
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); //總行人員
		
		//取得查詢資料可視範圍
		PMS000 pms000 = (PMS000) PlatformContext.getBean("pms000");
		PMS000InputVO pms000InputVO = new PMS000InputVO();
		pms000InputVO.setReportDate(inputVO.getsCreDate());
		PMS000OutputVO pms000outputVO = pms000.getOrg(pms000InputVO);
		
		
		
		// 筆數限制
		Map<String, String> qry_max_limit_xml = xmlInfo.doGetVariable("SYS.MAX_QRY_ROWS", FormatHelper.FORMAT_2);
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		try {
			//==主查詢==
//			sb.append(" SELECT ROWNUM,  ALLS.* ");
//			sb.append(" FROM   ( ");
//			
//			sb.append(" SELECT T.PLAN_YEARMON,T.SEQ, T.SRC_TYPE, T.CUST_ID,  T.CUST_NAME, T.EST_PRD, T.REL_CODE,  T.DATA_DATE,  T.EXP_CF_TOTAL,  T.EXP_CF_PLAN, ");
//			sb.append(" ( T.EXP_CF_TOTAL - T.EXP_CF_PLAN ) AS EXP_CF_BAL, ");
//			sb.append(" T.REC_CF_TOTAL,  T.REC_CF_TXN,  T.REC_CF_PLAN, ");
//			sb.append(" ( T.REC_CF_TOTAL - T.REC_CF_PLAN ) AS REC_CF_BAL, ");
//			sb.append(" T.REC_CF_CONT_FLAG,  T.REC_CF_YTD_DEP, ");
//			sb.append(" ( CASE  WHEN T.REC_CF_YTD_DEP < ( T.REC_CF_TOTAL - T.REC_CF_PLAN )  THEN 'Y'  ELSE 'N'  END ) AS REC_CF_LOS_FLAG, ");
//			sb.append(" T.TSP,  T.M_EMP_ID,  T.M_EMP_NAME,  T.M_DATE,  T.BRANCH_NBR,  T.BRANCH_AREA_ID, ");
//			sb.append(" E.REGION_CENTER_ID,  E.BRANCH_NAME,  E.BRANCH_AREA_NAME,  E.REGION_CENTER_NAME, ");
//			sb.append(" T.AO_CODE,  T.CREATETIME ");
//			sb.append(" FROM ( ");
//			
//			sb.append(" SELECT S.PLAN_YEARMON,S.SEQ, S.SRC_TYPE, C.CUST_ID,  C.CUST_NAME, S.EST_PRD, C.REL_CODE,  C.EXP_CF_TOTAL, ");
//			sb.append(" ( CASE  WHEN NVL(S.EST_AMT, 0) >=  C.REC_CF_TOTAL + C.EXP_CF_TOTAL  THEN  C.EXP_CF_TOTAL ");
//			sb.append(" WHEN NVL(S.EST_AMT, 0) >= C.REC_CF_TOTAL THEN  NVL(S.EST_AMT, 0) - C.REC_CF_TOTAL  ELSE 0  END ) AS EXP_CF_PLAN, ");
//			sb.append(" C.REC_CF_TOTAL,  C.REC_CF_TXN,  C.REC_CF_CONT_FLAG, ");
//			sb.append(" ( CASE  WHEN S.EST_AMT >= C.REC_CF_TOTAL THEN S.EST_AMT  ELSE C.REC_CF_TOTAL  END ) AS REC_CF_PLAN, ");
//			sb.append(" C.REC_CF_YTD_DEP, ");
//			sb.append(" CASE  WHEN S.CUST_ID IS NULL THEN 0  ELSE 1  END AS TSP, ");
//			sb.append(" C.M_EMP_ID,  C.M_EMP_NAME,  C.M_DATE,  C.BRANCH_NBR,  C.BRANCH_AREA_ID,  C.AO_CODE,  C.DATA_DATE,  C.CREATETIME  ");
//			sb.append(" FROM TBPMS_CUS_CF_MAST C ");
//			sb.append(" LEFT JOIN ( ");
//			
//			sb.append(" SELECT PLAN_YEARMON,SEQ, CUST_ID, AO_CODE, SRC_TYPE, EST_PRD, ");
//			sb.append(" SUM(CASE  WHEN SRC_TYPE = '1' THEN EST_AMT  ELSE 0  END) AS EST_AMT ");
//			sb.append(" FROM   TBPMS_SALES_PLANS ");
//			sb.append(" WHERE  CLOSE_DATE >= TRUNC(SYSDATE + 1) ");
//			sb.append(" GROUP  BY SEQ,PLAN_YEARMON, CUST_ID,  AO_CODE, SRC_TYPE, EST_PRD ");
//			
//			sb.append(" )S  ON C.CUST_ID = S.CUST_ID  AND C.AO_CODE = S.AO_CODE ");
//			
//			sb.append(" ) T LEFT JOIN TBPMS_ORG_REC_N E  ON E.BRANCH_NBR = T.BRANCH_NBR ");
//			sb.append(" AND E.BRANCH_AREA_ID = T.BRANCH_AREA_ID ");
//			sb.append(" WHERE  1 = 1 ");
			
			//2017/05/27  金流主檔重新組裝
			sb.append("  WITH turnsell    ");
			sb.append("       AS (SELECT masto.exp_cf_total,                  ");
			sb.append("                  masto.rec_cf_total,                  ");
			sb.append("                  masto.rec_cf_txn,                    ");
			sb.append("                  masto.rec_cf_ytd_dep,                ");
			sb.append("                  masto.cust_id,                       ");
			sb.append("                  masto.cust_name,                     ");
			sb.append("                  masto.data_date,                     ");
			sb.append("                  masto.rel_code,                      ");
			sb.append("                  masto.region_center_id,              ");
			sb.append("                  REC.region_center_name,              ");
			sb.append("                  masto.branch_area_id,                ");
			sb.append("                  REC.branch_area_name,                ");
			sb.append("                  masto.branch_nbr,                    ");
			sb.append("                  REC.branch_name,                     ");
			sb.append("                  masto.ao_code,0 AS REC_CF_CONT_FLAG,                       ");
			sb.append("                  1     AS TSP,                        ");
			sb.append("                  ( nvl(masto.rec_cf_total,0) - nvl(masto.rec_cf_txn,0) ) AS ");
			sb.append("                  REC_CF_BAL,                          ");
			sb.append("                  CASE                                 ");
			sb.append("                    WHEN nvl(SP.est_amt,0) > nvl(masto.rec_cf_total,0) THEN nvl(masto.rec_cf_total,0) ");
			sb.append("                    ELSE nvl(SP.est_amt,0)            ");
			sb.append("                  END   AS                             ");
			sb.append("                  REC_CF_PLAN,                         ");
			sb.append("                  CASE                                 ");
			sb.append("                    WHEN nvl(SP.est_amt,0)-nvl(masto.rec_cf_total,0) > 0 THEN nvl(SP.est_amt,0)-nvl(masto.rec_cf_total,0)  ");
			sb.append("                    ELSE 0            ");
			sb.append("                  END   AS                             ");
			sb.append("                  EXP_CF_PLAN,                         ");
//			sb.append("                  ( nvl(masto.exp_cf_total,0) - CASE          ");
//			sb.append("       WHEN nvl(SP.est_amt,0) > nvl(masto.exp_cf_total,0)            ");
//			sb.append("     THEN          ");
//			sb.append("       nvl(SP.est_amt,0)  ");
//			sb.append("       ELSE nvl(masto.exp_cf_total,0)                         ");
//			sb.append("     END )              AS                             ");
//			sb.append("                  EXP_CF_BAL,                          ");
			sb.append("                  ( nvl(masto.exp_cf_total,0) -  ");
			sb.append("                ( CASE                                 ");
			sb.append("                    WHEN nvl(SP.est_amt,0)-nvl(masto.rec_cf_total,0) > 0 THEN nvl(SP.est_amt,0)-nvl(masto.rec_cf_total,0)  ");
			sb.append("                    ELSE 0            ");
			sb.append("                  END   ) )                            ");
			sb.append("                  EXP_CF_BAL,                          ");
			sb.append("                  CASE                                 ");
			sb.append("                    WHEN ( nvl(masto.rec_cf_ytd_dep,0) -      ");
			sb.append("                           ( nvl(masto.rec_cf_total,0) - nvl(masto.rec_cf_txn,0) ) ) < 0   ");
			sb.append("                   ");
			sb.append("                  THEN 'Y'                             ");
			sb.append("                    ELSE 'N'                           ");
			sb.append("                  END   AS REC_CF_LOS_FLAG             ");
			sb.append("           FROM   (SELECT *                            ");
			sb.append("                   FROM   tbpms_cus_cf_mast　          ");
			sb.append("  WHERE  cust_id IN (SELECT DISTINCT(CUST_ID) FROM TBPMS_SALES_PLANS            ");
			sb.append("  WHERE                                                                        ");
			sb.append("  SRC_TYPE='1' AND                                                             ");
			sb.append("  plan_yearmon BETWEEN :DATA_DATEE                                             ");
			sb.append("  AND To_char(Add_months(To_date(:DATA_DATEE || 01,'YYYYMMDD'), 50),'YYYYMM'))  ");
			
			sb.append("                          AND Substr(data_date, 0, 6) = :DATA_DATEE) MASTO　");
			sb.append("                   ");
			sb.append("                  left join tbpms_org_rec_n REC  ");
			sb.append("                         ON REC.branch_area_id = masto.branch_area_id ");
			sb.append("                            AND REC.branch_nbr = masto.branch_nbr    ");
			sb.append("                            AND REC.region_center_id = masto.region_center_id  ");
			sb.append("                            AND TO_DATE(MASTO.DATA_DATE,'YYYYMMDD') BETWEEN REC.START_TIME AND REC.END_TIME  ");
			sb.append("                  left join (SELECT nvl(SUM(est_amt),0) AS EST_AMT, ");
			sb.append("                                    cust_id          ");		
			sb.append("                             FROM   TBPMS_SALES_PLANS   ");
			//close_date >= Trunc(SYSDATE + 1) <-----拔掉WHERE條件
			sb.append("                             WHERE  src_type='1' AND plan_yearmon BETWEEN :DATA_DATEE AND To_char(Add_months(To_date(:DATA_DATEE || 01,'YYYYMMDD'), 50),'YYYYMM') ");
			sb.append("                             GROUP  BY cust_id    ) SP     ");			  //之後要改
			sb.append("                         ON SP.cust_id = masto.cust_id )");			
			sb.append("  ,   noturnsell  AS (SELECT masto.exp_cf_total,       ");
			sb.append("                  masto.rec_cf_total,                  ");
			sb.append("                  masto.rec_cf_txn,                    ");
			sb.append("                  masto.rec_cf_cont_flag,              ");
			sb.append("                  masto.rec_cf_ytd_dep,                ");
			sb.append("                  masto.cust_id,                       ");
			sb.append("                  masto.cust_name,                     ");
			sb.append("                  masto.data_date,                     ");
			sb.append("                  masto.rel_code,                      ");
			sb.append("                  masto.region_center_id,              ");
			sb.append("                  REC.region_center_name,              ");
			sb.append("                  masto.branch_area_id,                ");
			sb.append("                  REC.branch_area_name,                ");
			sb.append("                  masto.branch_nbr,                    ");
			sb.append("                  REC.branch_name,                     ");
			sb.append("                  masto.ao_code,                       ");
			sb.append("                  0   AS TSP,                          ");
			sb.append("                  CASE                                 ");
			sb.append("                    WHEN ( nvl(masto.rec_cf_ytd_dep,0) -      ");
			sb.append("                           ( nvl(masto.rec_cf_total,0) - nvl(masto.rec_cf_txn,0) ) ) < 0      ");
			sb.append("                  THEN 'Y'                             ");
			sb.append("                    ELSE 'N'                           ");
			sb.append("                  END AS REC_CF_LOS_FLAG               ");
			sb.append("           FROM   (SELECT *                            ");
			sb.append("                   FROM   tbpms_cus_cf_mast　          ");
			sb.append("  WHERE  cust_id not IN (SELECT DISTINCT(CUST_ID) FROM TBPMS_SALES_PLANS            ");
			sb.append("  WHERE                                                                        ");
			sb.append("  SRC_TYPE='1' AND                                                             ");
			sb.append("  plan_yearmon BETWEEN :DATA_DATEE                                             ");
			sb.append("  AND To_char(Add_months(To_date(:DATA_DATEE || 01,'YYYYMMDD'), 50),'YYYYMM'))  ");
			sb.append("  AND Substr(data_date, 0, 6) = :DATA_DATEE) MASTO　　 ");
			sb.append("  left join tbpms_org_rec_n REC                        ");
			sb.append("  ON REC.branch_area_id = masto.branch_area_id         ");
			sb.append("  AND REC.branch_nbr = masto.branch_nbr                ");
			sb.append("  AND REC.region_center_id = masto.region_center_id   ");
			sb.append("  AND TO_DATE(MASTO.DATA_DATE,'YYYYMMDD') BETWEEN REC.START_TIME AND REC.END_TIME)   ");
			
			
			
			//前端查詢標籤
			if (!StringUtils.isBlank(Y_N)) {
				if ("Y".equals(Y_N)) {
					sb.append(" SELECT ROWNUM,ALLS.* FROM (SELECT * FROM TURNSELL T ");
					if(!StringUtils.isBlank(inputVO.getPROD_TYPE())){
						sb.append(" INNER JOIN (SELECT PRD_TYPE,CUST_ID AS CUSTID,DATA_DATE AS DATA_D FROM TBPMS_CUS_CF_DTL  GROUP BY PRD_TYPE,CUST_ID,DATA_DATE) DTL ");
						sb.append(" ON T.CUST_ID=DTL.CUSTID ");
						sb.append(" AND SUBSTR(DTL.DATA_D,0,6) = :DATA_DATEE ");
						sb.append(" AND DTL.PRD_TYPE=:PRD_TYPE ");
						queryCondition.setObject("PRD_TYPE",inputVO.getPROD_TYPE().trim());
					}					
					sb.append(" WHERE 1=1  ");
				}
				if ("N".equals(Y_N)) {
					sb.append(" SELECT ROWNUM,ALLS.* FROM  (SELECT * FROM noturnsell T ");
					if(!StringUtils.isBlank(inputVO.getPROD_TYPE())){
						sb.append(" INNER JOIN (SELECT PRD_TYPE,CUST_ID  AS CUSTID,DATA_DATE AS DATA_D FROM TBPMS_CUS_CF_DTL GROUP BY PRD_TYPE,CUST_ID,DATA_DATE) DTL ");
						sb.append(" ON T.CUST_ID=DTL.CUSTID ");
						sb.append(" AND SUBSTR(DTL.DATA_D,0,6) = :DATA_DATEE ");
						sb.append(" AND DTL.PRD_TYPE=:PRD_TYPE ");
						queryCondition.setObject("PRD_TYPE",inputVO.getPROD_TYPE().trim());
					}					
					sb.append(" WHERE 1=1  ");
				}
			}
			
			/**=================查詢條件=================**/
			//營運區
			if (!StringUtils.isBlank(inputVO.getBranch_area_id())) {
				//sb.append(" AND T.BRANCH_AREA_ID = :branchArea "); // 營運區
				sb.append("  and T.BRANCH_NBR IN ( ");
				sb.append("    SELECT BRANCH_NBR ");
				sb.append("    FROM VWORG_DEFN_BRH ");
				sb.append("    WHERE DEPT_ID = :branchArea ");
				sb.append("  ) ");
				queryCondition.setObject("branchArea", inputVO.getBranch_area_id()); // 營運區
			}else{
				//登入非總行人員強制加營運區
				if(!headmgrMap.containsKey(roleID)) {
					//sb.append(" and T.BRANCH_AREA_ID IN (:branch_area_id) ");
					sb.append("  and T.BRANCH_NBR IN ( ");
					sb.append("    SELECT BRANCH_NBR ");
					sb.append("    FROM VWORG_DEFN_BRH ");
					sb.append("    WHERE DEPT_ID IN (:branch_area_id) ");
					sb.append("  ) ");
					queryCondition.setObject("branch_area_id", pms000outputVO.getV_areaList());
				}
			}
			//分行
			if (!StringUtils.isBlank(inputVO.getBranch_nbr())) {
				sb.append(" AND T.BRANCH_NBR = :branch "); // 分行
				queryCondition.setObject("branch", inputVO.getBranch_nbr()); // 分行
			}else{
				//登入非總行人員強制加分行
				if(!headmgrMap.containsKey(roleID)) {
					sb.append(" and T.BRANCH_NBR IN (:branch_nbr) ");
					queryCondition.setObject("branch_nbr", pms000outputVO.getV_branchList());
				}
			}
			//員編
			if (!StringUtils.isBlank(inputVO.getAo_code())) {
				sb.append(" AND T.AO_CODE = :AO_CODEE "); // 分行
				queryCondition.setObject("AO_CODEE", inputVO.getAo_code()); //員工
			}else{
				//登入為銷售人員強制加AO_CODE
				if(fcMap.containsKey(roleID) || psopMap.containsKey(roleID)) {
					sb.append(" and AO_CODE IN (:ao_code) ");
					queryCondition.setObject("ao_code", pms000outputVO.getV_aoList());
				}
			}
			//客戶ID
			if (!StringUtils.isBlank(inputVO.getCUST_ID())) {
				sb.append(" AND T.CUST_ID = :CUST_IDD "); // 客戶id
				queryCondition.setObject("CUST_IDD",   inputVO.getCUST_ID().toUpperCase().trim());
			}
			

			
			//前端已入帳/未聯繫
			if (!StringUtils.isBlank(inputVO.getTYPE())) {
				if ("Y".equals(inputVO.getTYPE())) {
//					sb.append(" AND  T.REC_CF_TOTAL >0 and T.REC_CF_PLAN=0 ");
					//前端查詢標籤
					if (!StringUtils.isBlank(Y_N)) {
						if ("N".equals(Y_N)) {
							sb.append(" AND  T.REC_CF_CONT_FLAG='Y' ");   //已入帳未聯繫
						}
					}
					
				}
				if ("N".equals(inputVO.getTYPE())) {
//					sb.append(" AND T.REC_CF_CONT_FLAG = 'Y' ");
					sb.append(" AND T.REC_CF_LOS_FLAG ='Y' ");         //已入帳且流失
				}
			}
			//日期
			if (!StringUtils.isBlank(inputVO.getsCreDate())) {
				sb.append(" AND SUBSTR(T.DATA_DATE,0,6) = :DATA_DATEE ");
				queryCondition.setObject("DATA_DATEE", inputVO.getsCreDate());
			}else if(StringUtils.isBlank(inputVO.getFLAG())){
				sb.append(" AND T.DATA_DATE = (SELECT MAX(DATA_DATE) FROM TBPMS_CUS_CF_MAST) ");
			}
			if (StringUtils.equals(inputVO.getFLAG(), "Y")) {
				sb.append(" AND SUBSTR(T.DATA_DATE,0,6) = TO_CHAR(TRUNC(SYSDATE,'MONTH') - 1 , 'YYYYMM')");
			}
			
			sb.append(" ) ALLS ");			
			sb.append(" WHERE ROWNUM <=:QRY_MAX_LIMIT ");
			sb.append(" ORDER BY  ROWNUM ");
			queryCondition.setObject("QRY_MAX_LIMIT", qry_max_limit_xml.get("2000"));

			queryCondition.setQueryString(sb.toString());	
			
			
			
			//查詢結果
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
			
//			ResultIF list = dam.executePaging(queryCondition,
//					inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
//			int totalPage_i = list.getTotalPage(); // 分頁用
//			int totalRecord_i = list.getTotalRecord(); // 分頁用
			outputVO.setResultList(list); // data
//			
//			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
//			outputVO.setTotalPage(totalPage_i);// 總頁次
//			outputVO.setTotalRecord(totalRecord_i);// 總筆數

			return outputVO;
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
	
	/**
	 * 明細查詢
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void getLeadsList(Object body, IPrimitiveMap header) throws JBranchException {

		PMS101InputVO inputVO = (PMS101InputVO) body;
		PMS101OutputVO outputVO = new PMS101OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		try {
			//==查詢==
			sb.append(" SELECT PRD_TYPE, PRD_NAME, CF_TYPE, EXP_AMT, ");
			sb.append(" TO_CHAR(EXP_DT, 'YYYY/MM/DD') AS EXP_DT ");
			sb.append(" from TBPMS_CUS_CF_DTL ");
			sb.append(" where 1=1 ");
			//==查詢條件==
			//客戶ID
			if (!StringUtils.isBlank(inputVO.getCUST_ID()))
				sb.append(" AND CUST_ID = :CUST_IDDD ");
				queryCondition.setObject("CUST_IDDD", inputVO.getCUST_ID());
			//時間
			if (!StringUtils.isBlank(inputVO.getDATA_DATE()))
				sb.append(" AND SUBSTR(DATA_DATE,0,6) = SUBSTR(:DATA_DATE,0,6) ");
				queryCondition.setObject("DATA_DATE", inputVO.getDATA_DATE());
			queryCondition.setQueryString(sb.toString());
			
			//--查詢結果
			List<Map<String, Object>> list1 = dam.exeQuery(queryCondition);
			outputVO.setResultList(list1);
			this.sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

}
