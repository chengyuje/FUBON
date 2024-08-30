package com.systex.jbranch.app.server.fps.pms301;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.pms000.PMS000;
import com.systex.jbranch.app.server.fps.pms000.PMS000InputVO;
import com.systex.jbranch.app.server.fps.pms000.PMS000OutputVO;
import com.systex.jbranch.app.server.fps.sqm110.SQM110DetailInputVO;
import com.systex.jbranch.app.server.fps.sqm110.SQM110OutputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * 
 * Copy Right Information : <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description :分行即時速報Controller <br>
 * Comments Name : PMS301.java<br>
 * Author :Stella<br>
 * Date :2016年06月30日 <br>
 * Version : 1.01 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年02月01日<br>
 */

@Component("pms301")
@Scope("request")
public class PMS301 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PMS301.class);

	public void inquire(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		PMS301InputVO inputVO = (PMS301InputVO) body;
		PMS301OutputVO return_VO = new PMS301OutputVO();
		dam = this.getDataAccessManager();
		

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
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		pms000InputVO.setReportDate(sdf.format(new Date()));
		PMS000OutputVO pms000outputVO = pms000.getOrg(pms000InputVO);
		
		SimpleDateFormat sdfYM = new SimpleDateFormat("yyyyMM");
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		Calendar cal=Calendar.getInstance();
		String tragetTable ="TBPMS_BR_IMM_PROFIT";
		//當月MTD
		if("MTD".equals(inputVO.getSrchType())){
			tragetTable ="TBPMS_BR_IMM_PROFIT_MTD";
			queryCondition.setObject("ym", inputVO.getTx_ym());
//			queryCondition.setObject("dd", String.valueOf(cal.get(Calendar.DATE)));
		}
//		else if("HIS_MON".equals(inputVO.getSrchType())){//歷史月報
//			tragetTable ="TBPMS_BR_IMM_PROFIT_MTD";
//			queryCondition.setObject("ym", inputVO.getTx_ym());
//			queryCondition.setObject("dd", 25);//因為查看歷史月份，保險當月只到25號
//			cal.set(Calendar.DATE, 25);
//		}
		
		
		StringBuffer sql = new StringBuffer();
		sql.append("  WITH  SELECT_ALL  AS ");
		sql.append("  (    ");
		sql.append("  SELECT   BRIP_ALL.REGION_CENTER_ID,    ");
		sql.append("           BRIP_ALL.REGION_CENTER_NAME,  ");
		sql.append("           BRIP_ALL.BRANCH_AREA_ID,      ");
		sql.append("           BRIP_ALL.BRANCH_AREA_NAME,    ");
		sql.append("           BRIP_ALL.BRANCH_NBR,   ");
		sql.append("           BRIP_ALL.BRANCH_NAME,  ");
		sql.append("           BRIP_ALL.BRANCH_GROUP , ");
		sql.append("           BRIP_ALL.INV_FEE AS INV_FEE,     "); //投資實績
		sql.append("           BRIP_ALL.INS_FEE AS INS_FEE,     "); //保險實績
		sql.append("           BRIP_ALL.INV_FEE + BRIP_ALL.INS_FEE AS TOT_FEE,   ");  //合計實績
		sql.append("           NVL(100 * INV_FEE / NULLIF(BRT.INV_DAY_TAR,0),0) AS INV_DAY_RATE,  ");  //投資實際達成率
		sql.append("           NVL(100 * INS_FEE / NULLIF(BRT.INS_DAY_TAR,0),0) AS INS_DAY_RATE,  ");  //保險實際達成率
		sql.append("           NVL(100 * (INV_FEE + INS_FEE) / NULLIF(BRT.INV_DAY_TAR + BRT.INS_DAY_TAR,0),0) AS TOT_DAY_RATE, ");
		sql.append("           INV_DAY_TAR,  ");
		sql.append("           INS_DAY_TAR,  ");
		sql.append("           INV_DAY_TAR + INS_DAY_TAR AS TOT_TAR     ");
//		sql.append("           REPORT_TIME AS REPORT_TIME    ");
		sql.append("  FROM      ( SELECT  NVL(REC.REGION_CENTER_ID,'全行總計') AS REGION_CENTER_ID, ");
		sql.append("                      REC.REGION_CENTER_NAME AS REGION_CENTER_NAME,        ");
		sql.append("                      NVL(REC.BRANCH_AREA_ID,'處合計') AS BRANCH_AREA_ID,  ");
		sql.append("                      REC.BRANCH_AREA_NAME AS BRANCH_AREA_NAME,    ");
		sql.append("                      NVL(REC.BRANCH_NBR,'區合計') AS BRANCH_NBR,  ");
		sql.append("                      REC.BRANCH_NAME AS BRANCH_NAME,      ");
		sql.append("                      REC.BRANCH_GROUP AS BRANCH_GROUP,    ");
		sql.append("                      SUM(NVL(BRIP.INV_FEE,0)) AS INV_FEE, ");
		sql.append("                      SUM(NVL(BRIP.INS_FEE,0)) AS INS_FEE  ");
//		sql.append("                      MAX(report_time) as report_time ");
		sql.append("              FROM VWORG_DEFN_INFO REC                     ");
		sql.append("              LEFT JOIN ( SELECT BRANCH_NBR AS BRANCH_NBR, ");
		sql.append("                                NVL(FEE,0) AS FEE,         ");
		sql.append("                                CASE WHEN ITEM IN ('01','02','03','04','05','06','17') THEN  'INV'    ");  //'09','15',  匯兌  信託  拔掉  畫面拔掉匯兌
		sql.append("                                     WHEN ITEM IN ('10','11','12','16') THEN 'INS'                              ");
		sql.append("                                END AS ITEM                  ");
//		sql.append("                                case when to_char(max(CREATETIME),'HH24:MI') >=  '09:30' AND to_char(max(CREATETIME),'HH24:MI') < '13:30' then TO_DATE(TO_CHAR(max(CREATETIME),'YYYYMMDD')||'09:30','YYYY/MM/DD HH24:MI')               ");
//		sql.append("                                     when to_char(max(CREATETIME),'HH24:MI') >= '13:30'  AND to_char(max(CREATETIME),'HH24:MI') < '16:30' then TO_DATE(TO_CHAR(max(CREATETIME),'YYYYMMDD')||'13:30','YYYY/MM/DD HH24:MI')                ");
//		sql.append("                                     when to_char(max(CREATETIME),'HH24:MI') >= '16:30'  AND to_char(max(CREATETIME),'HH24:MI') < '18:00' then TO_DATE(TO_CHAR(max(CREATETIME),'YYYYMMDD')||'16:30','YYYY/MM/DD HH24:MI')                ");
//		sql.append("                                     when to_char(max(CREATETIME),'HH24:MI') >= '18:00' then TO_DATE(TO_CHAR(max(CREATETIME),'YYYYMMDD')||'18:00','YYYY/MM/DD HH24:MI')  end report_time                ");
		sql.append("                          FROM "+tragetTable+" BRIP      ");
		sql.append("                          WHERE ITEM NOT IN ('90','91','92') ");
		if("NOW".equals(inputVO.getSrchType())){
		sql.append("						    and BRIP.TX_DATE = TO_CHAR(SYSDATE,'YYYYMMDD') ");
		}else{
			    //當月MTD保險25號抓當月資料，歷史也是抓當月資料
//				if(cal.get(Calendar.DATE)<=25){
		sql.append("						    and BRIP.TX_YM = :ym ");			
//				}
//				//26號抓下個月資料
//				else{
//		sql.append("						    and ( "   );
//		sql.append("						    	     (ITEM IN ('01','02','03','04','05','06','17') and BRIP.TX_YM = :ym ) or ");
//	    sql.append("							         (ITEM IN ('10','11','12','16') and BRIP.TX_YM =TO_CHAR(ADD_MONTHS(to_date(:ym,'YYYYMM'), 1),'YYYYMM') )  ");
//		sql.append("                        		)  "  );
//				}	
										
		}
		sql.append("                        GROUP BY BRANCH_NBR,NVL(FEE,0),CASE WHEN ITEM IN ('01','02','03','04','05','06','17') THEN  'INV'  WHEN ITEM IN ('10','11','12','16') THEN 'INS' END ");
		sql.append("                        )  ");
		sql.append("                        PIVOT (SUM(FEE) AS FEE                    ");
		sql.append("                        FOR ITEM IN ('INV' INV, 'INS' INS)) BRIP  ");
		sql.append("              ON REC.BRANCH_NBR = BRIP.BRANCH_NBR                 ");
		sql.append("              GROUP BY ROLLUP((REC.REGION_CENTER_ID,REC.REGION_CENTER_NAME),(REC.BRANCH_AREA_ID,REC.BRANCH_AREA_NAME),(REC.BRANCH_NBR,REC.BRANCH_NAME,REC.BRANCH_GROUP))  ");
		sql.append("            ) BRIP_ALL                                                     ");
		sql.append("  LEFT JOIN ( SELECT NVL(ORG.REGION_CENTER_ID,'全行總計') AS REGION_CENTER_ID, ");
		sql.append("                    NVL(ORG.BRANCH_AREA_ID,'處合計') AS BRANCH_AREA_ID,        ");
		sql.append("                    NVL(ORG.BRANCH_NBR,'區合計') AS BRANCH_NBR,                ");
		
		if("NOW".equals(inputVO.getSrchType())){
		sql.append("                    SUM(ROUND(CASE WHEN DATA_YEARMON = to_char(sysdate,'YYYYMM') then (NVL(TAR.INV_TAR_AMOUNT,0)) else 0 end  / pabth_util.fc_gettwodatediff(last_day(add_months(SYSDATE, -1)) + 1, last_day(SYSDATE)))) AS INV_DAY_TAR,  ");
		sql.append("                    SUM(ROUND(CASE WHEN (to_char(sysdate,'dd') <=25 and DATA_YEARMON = to_char(sysdate,'YYYYMM')) OR                                                          ");
		sql.append("                                        (to_char(sysdate,'dd') >=26 and DATA_YEARMON = to_char(add_months(sysdate,1),'YYYYMM')) then (NVL(TAR.INS_TAR_AMOUNT,0)) else 0 end / ");
		sql.append("                              CASE WHEN SYSDATE < to_date(TO_CHAR(SYSDATE,'YYYYMM')||'26','YYYYMMDD') THEN                                   ");
		sql.append("                              pabth_util.fc_gettwodatediff(last_day(add_months(SYSDATE, -2)) + 26, last_day(add_months(SYSDATE, -1)) + 25) ");
		sql.append("                              ELSE   ");
		sql.append("                              pabth_util.fc_gettwodatediff(last_day(add_months(SYSDATE, -1)) + 26, last_day(SYSDATE) + 25)   ");
		sql.append("                    END)) AS INS_DAY_TAR      ");
		}else{
		sql.append("					SUM(case when DATA_YEARMON = :ym then (NVL(TAR.INV_TAR_AMOUNT,0))                                            ");
		sql.append("                             else 0 end																				                                        ");
		sql.append("                       ) AS INV_DAY_TAR,                                                                                                                  ");
//		sql.append("                    SUM(case when :dd <=25 and DATA_YEARMON = :ym then (NVL(TAR.INS_TAR_AMOUNT,0))             ");
//		sql.append("                             when :dd >=26 and DATA_YEARMON = to_char(add_months(to_date(:ym,'YYYYMM'),1),'YYYYMM') then (NVL(TAR.INS_TAR_AMOUNT,0)) ");
//		sql.append("                             else 0 end                                                                                                                       ");
//		sql.append("                       ) AS INS_DAY_TAR    
		sql.append("                    SUM(NVL(TAR.INS_TAR_AMOUNT,0) ) AS INS_DAY_TAR            ");
		}
		
		sql.append("              FROM VWORG_DEFN_INFO ORG    ");
		sql.append("              LEFT JOIN TBPMS_BR_PRD_TAR_M TAR    ");
		if("NOW".equals(inputVO.getSrchType())){
		sql.append("              ON ORG.BRANCH_NBR = TAR.BRANCH_NBR AND TAR.DATA_YEARMON BETWEEN TO_CHAR(SYSDATE,'YYYYMM') and to_char(add_months(sysdate,1),'YYYYMM')  ");
		}else{
		sql.append("              ON ORG.BRANCH_NBR = TAR.BRANCH_NBR AND TAR.DATA_YEARMON = :ym   ");
		}
		sql.append("              GROUP BY ROLLUP(ORG.REGION_CENTER_ID,ORG.BRANCH_AREA_ID,ORG.BRANCH_NBR)");
		sql.append("            ) BRT      ");
		sql.append("            ON (BRIP_ALL.REGION_CENTER_ID = BRT.REGION_CENTER_ID AND BRIP_ALL.BRANCH_AREA_ID = BRT.BRANCH_AREA_ID AND BRIP_ALL.BRANCH_NBR = BRT.BRANCH_NBR)  ");
		sql.append("  )     ");
		sql.append("  SELECT * FROM SELECT_ALL WHERE 1=1     ");
		
		
		
		//額外多出來串接日期
//		sql.append("and alls.DATA_DATE = :DATES ");
//		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd"   );
//		String aa = sdf1.format(new Date());
//		queryCondition.setObject("DATES", aa);
		if (StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
			sql.append(" AND REGION_CENTER_ID in (:rd_id,'全行總計') ");
			queryCondition.setObject("rd_id", inputVO.getRegion_center_id());
		}else{
			//登入非總行人員強制加區域中心
			if(!headmgrMap.containsKey(roleID)) {
				sql.append(" and REGION_CENTER_ID IN (:region_center_id,'全行總計') ");
				queryCondition.setObject("region_center_id", pms000outputVO.getV_regionList());
			}
		}
		if (StringUtils.isNotBlank(inputVO.getBranch_area_id())) {
			sql.append(" AND BRANCH_AREA_ID in (:op_id,'處合計') ");
			queryCondition.setObject("op_id", inputVO.getBranch_area_id());
		}else{
			//登入非總行人員強制加營運區
			if(!headmgrMap.containsKey(roleID)) {
				sql.append(" and BRANCH_AREA_ID IN (:branch_area_id ,'處合計') ");
				queryCondition.setObject("branch_area_id", pms000outputVO.getV_areaList());
			}
		}
		if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
			sql.append(" AND BRANCH_NBR in (:br_id ,'區合計') ");
			queryCondition.setObject("br_id", inputVO.getBranch_nbr());
		}else{
			//登入非總行人員強制加分行
			if(!headmgrMap.containsKey(roleID)) {
				sql.append(" and BRANCH_NBR IN (:branch_nbr,'區合計') ");
				queryCondition.setObject("branch_nbr", pms000outputVO.getV_branchList());
			}
		}
		sql.append(" ORDER BY REGION_CENTER_ID,BRANCH_AREA_ID,BRANCH_NBR");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		QueryConditionIF condition1 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql1 = new StringBuffer();
		sql1.append("  SELECT  case when to_char(max(CREATETIME),'HH24:MI') >=  '09:30' AND to_char(max(CREATETIME),'HH24:MI') < '13:30' then TO_DATE(TO_CHAR(max(CREATETIME),'YYYYMMDD')||'09:30','YYYY/MM/DD HH24:MI')              ");
		sql1.append("               when to_char(max(CREATETIME),'HH24:MI') >= '13:30'  AND to_char(max(CREATETIME),'HH24:MI') < '16:30' then TO_DATE(TO_CHAR(max(CREATETIME),'YYYYMMDD')||'13:30','YYYY/MM/DD HH24:MI')              ");
		sql1.append("               when to_char(max(CREATETIME),'HH24:MI') >= '16:30'  AND to_char(max(CREATETIME),'HH24:MI') < '18:00' then TO_DATE(TO_CHAR(max(CREATETIME),'YYYYMMDD')||'16:30','YYYY/MM/DD HH24:MI')              ");
		sql1.append("               when to_char(max(CREATETIME),'HH24:MI') >= '18:00' then TO_DATE(TO_CHAR(max(CREATETIME),'YYYYMMDD')||'18:00','YYYY/MM/DD HH24:MI')  end CDATE                  ");
		sql1.append(" FROM "+tragetTable );
		if("NOW".equals(inputVO.getSrchType())){
			sql1.append(" where TX_DATE = TO_CHAR(SYSDATE,'YYYYMMDD') ");		
		}else if ("MTD".equals(inputVO.getSrchType())){
			sql1.append(" where TX_YM = :ym ");		
			condition1.setObject("ym", inputVO.getTx_ym());
		}
//		else if ("HIS_MON".equals(inputVO.getSrchType())){
//			sql1.append(" where TX_YM = :ym ");		
//			condition1.setObject("ym", inputVO.getTx_ym());
//		}
		
		condition1.setQueryString(sql1.toString());
		List<Map<String, Object>> clist =  dam.exeQuery(condition1);
		
		return_VO.setcList(clist);
		return_VO.setResultList(list);
		return_VO.setCsvList(list);
		this.sendRtnObject(return_VO);
	}
	
	/** 查詢MTD資料月份
	 * @throws ParseException **/
	public void getYearMon(Object body, IPrimitiveMap header)
			throws JBranchException, ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
		PMS301OutputVO outputVO = new PMS301OutputVO();
		DataAccessManager dam = this.getDataAccessManager();
		
		QueryConditionIF condition = 
				dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		// 主查詢 sql 
		StringBuffer sql = new StringBuffer();
		sql.append("  SELECT SUBSTR(TX_YM,0,4)||'/'||SUBSTR(TX_YM,5,6) as LABEL, TX_YM as DATA FROM TBPMS_BR_IMM_PROFIT_MTD  ");
		sql.append("  WHERE TO_DATE(TX_YM,'YYYYMM') BETWEEN ADD_MONTHS(TO_DATE((SELECT MAX(TX_YM) FROM TBPMS_BR_IMM_PROFIT_MTD),'YYYYMM'),-6)  AND TO_DATE((SELECT MAX(TX_YM) FROM TBPMS_BR_IMM_PROFIT_MTD),'YYYYMM') ");
		sql.append("  GROUP BY TX_YM ORDER BY TO_DATE(TX_YM,'YYYYMM') DESC ");
		condition.setQueryString(sql.toString());
		
		List<Map<String, Object>> list = dam.exeQuery(condition);
		outputVO.setResultList(list);
		sendRtnObject(outputVO);

	}

	public void queryDetail(Object body, IPrimitiveMap header) throws JBranchException{
		PMS301InputdtatilVO inputVO = (PMS301InputdtatilVO) body;
		PMS301OutputVO return_VO = new PMS301OutputVO();
		dam = this.getDataAccessManager();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat sdfYM = new SimpleDateFormat("yyyyMM");
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		Calendar cal=Calendar.getInstance();
		//當月MTD
		if("MTD".equals(inputVO.getSrchType())){
			
			queryCondition.setObject("ym", inputVO.getTx_ym());
//			queryCondition.setObject("dd", String.valueOf(cr.get(Calendar.DATE)));
		}
//		else if("HIS_MON".equals(inputVO.getSrchType())){//歷史月報
//			queryCondition.setObject("ym", inputVO.getTx_ym());
//			cal.set(Calendar.DATE, 25);
//			queryCondition.setObject("dd", 25);//因為查看歷史月份，保險當月只到25號
//		}
		//2017/3/14 kevin sql
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT ALLS.*,BR.DEPT_NAME AS BRANCH_NAME ");
		sql.append("FROM (");
		sql.append("SELECT a.*, emp.EMP_NAME,b.AO_CODE from TBPMS_SALES_AOCODE_REC b ");
		sql.append("left join   (   ");
		sql.append("     SELECT   item, ");
		sql.append(" SUM(Nvl(fee,0)) AS fee,");
//		sql.append(" tx_date,   ");
//		sql.append(" EMP_ID,    ");
		sql.append(" ao_code as ao_codes,   ");
		sql.append(" branch_nbr ");
		if("NOW".equals(inputVO.getSrchType())){
		sql.append("    FROM  TBPMS_AO_IMM_PROFIT ");
		sql.append("    WHERE TX_DATE=TO_CHAR(SYSDATE,'YYYYMMDD') and branch_nbr = :br_id ");
		}
		else{
		sql.append("    FROM  TBPMS_AO_IMM_PROFIT_MTD where 1=1 and branch_nbr = :br_id ");
			//當月25號前，與歷史月表。
//			if(cal.get(Calendar.DATE)<=25){
			sql.append(" and TX_YM = :ym ");			
//			}//當月26號															
//			else{
//			sql.append(" and ( "   );
//			sql.append("		 (ITEM IN ('01','02','03','04','05','06','17') and TX_YM = :ym ) or ");
//			sql.append("		 (ITEM IN ('10','11','12','16') and TX_YM =TO_CHAR(ADD_MONTHS(to_date(:ym,'YYYYMM'), 1),'YYYYMM') )  ");
//			sql.append("      )  "  );
//			}	
		}
		sql.append("    GROUP BY item,");
		sql.append(" branch_nbr,");
//		sql.append(" EMP_ID,    ");
		sql.append(" ao_Code   ");
//		sql.append(" ,tx_date ) pivot( SUM(nvl(fee,0)) FOR item IN ('01' as 基金,");
		sql.append("  ) pivot( SUM(nvl(fee,0)) FOR item IN ('01' as 基金,");
		sql.append("     '02' as SI,  ");
		sql.append("     '03' AS SN,  ");
		sql.append("     '04' AS DCI, ");
		sql.append("     '05' AS 海外債,    ");
		sql.append("     '06' AS 海外ETF,  ");     //06   海外股票改ETF
		sql.append("     '17' AS 海外股票,   ");	 //17   海外ETF改海外股票
		sql.append("     '09' AS 匯兌損益,  ");
		sql.append("     '10' AS 躉繳,");
		sql.append("     '16' AS 投資期,    ");
		sql.append("     '11' AS 短年期,    ");
		sql.append("     '12' AS 長年期) ) a ");
		sql.append(" on a.AO_CODEs = b.AO_CODE  "); 
		sql.append("left join TBPMS_EMPLOYEE_REC_N emp  ");
		sql.append(" on b.emp_id = emp.emp_id ");
//		sql.append("WHERE a.tx_date = :DATES ");
		sql.append("WHERE  ");
		sql.append(" emp.dept_id = :br_id ");
		if("NOW".equals(inputVO.getSrchType())){
		sql.append(" and trunc(SYSDATE) BETWEEN B.START_TIME AND B.END_TIME ");	
		sql.append(" and trunc(SYSDATE) BETWEEN emp.START_TIME AND emp.END_TIME ");	
		}
		else{//歷史月報
		sql.append(" and trunc(LAST_DAY(TO_DATE(:ym,'YYYYMM'))) BETWEEN B.START_TIME AND B.END_TIME ");
		sql.append(" and trunc(LAST_DAY(TO_DATE(:ym,'YYYYMM'))) BETWEEN emp.START_TIME AND emp.END_TIME ");
		}
		sql.append("union all  ");
		sql.append(" SELECT  a.*, '' as EMP_NAME,a.ao_codes as ao_code from  ");
		sql.append("   (   ");
		sql.append("     SELECT   item, ");
		sql.append(" SUM(Nvl(fee,0)) AS fee,");
//		sql.append(" tx_date,   ");
//		sql.append(" EMP_ID,    ");
		sql.append(" ao_code as ao_codes,   ");
		sql.append(" branch_nbr ");
		if("NOW".equals(inputVO.getSrchType())){
			sql.append("    FROM  TBPMS_AO_IMM_PROFIT ");
			sql.append("    WHERE TX_DATE=TO_CHAR(SYSDATE,'YYYYMMDD') ");
			}
	    else {
			sql.append("    FROM  TBPMS_AO_IMM_PROFIT_MTD where 1=1 ");
				//當月25號前，與歷史月表。
//				if(cal.get(Calendar.DATE)<=25){
				sql.append(" and TX_YM = :ym ");			
//				}//當月26號																
//				else{
//				sql.append(" and ( "   );
//				sql.append("		 (ITEM IN ('01','02','03','04','05','06','17') and TX_YM = :ym ) or ");
//				sql.append("		 (ITEM IN ('10','11','12','16') and TX_YM =TO_CHAR(ADD_MONTHS(to_date(:ym,'YYYYMM'), 1),'YYYYMM') )  ");
//				sql.append("      )  "  );
//				}	
	    }	
		sql.append("    GROUP BY item,");
		sql.append(" branch_nbr,");
//		sql.append(" EMP_ID,    ");
		sql.append(" ao_Code   ");
//		sql.append(" ,tx_date ) pivot( SUM(nvl(fee,0)) FOR item IN ('01' as 基金,");
		sql.append("  ) pivot( SUM(nvl(fee,0)) FOR item IN ('01' as 基金,");
		sql.append("     '02' as SI,  ");
		sql.append("     '03' AS SN,  ");
		sql.append("     '04' AS DCI, ");
		sql.append("     '05' AS 海外債,    ");
		sql.append("     '06' AS 海外ETF,  ");     //06   海外股票改ETF
		sql.append("     '17' AS 海外股票,   ");	 //17   海外ETF改海外股票
		sql.append("     '09' AS 匯兌損益,  ");
		sql.append("     '10' AS 躉繳,");
		sql.append("     '16' AS 投資期,    ");
		sql.append("     '11' AS 短年期,    ");
		sql.append("     '12' AS 長年期) ) a ");
//		sql.append(" on a.BRAnch_NBR = b.BRA_NBR  "); 
//		sql.append("WHERE a.tx_date = :DATES ");
		sql.append("WHERE  ");
		sql.append(" a.BRAnch_NBR = :br_id and a.ao_codes='000' ");
		sql.append(" ) ALLS LEFT JOIN  VWORG_DEPT_BR BR ");
		sql.append(" ON ALLS.BRANCH_NBR=BR.DEPT_ID ");
		sql.append(" ORDER BY ALLS.AO_CODE,ALLS.BRANCH_NBR ");
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//		String aa = sdf.format(inputVO.getsCreDate());
//		queryCondition.setObject("DATES", aa);
		queryCondition.setObject("br_id", inputVO.getBranch_nbr());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}
	
	// 匯出EXCLE
	public void export(Object body, IPrimitiveMap header) throws JBranchException {
		PMS301InputVO inputVO = (PMS301InputVO) body;
		List<Map<String, Object>> list = inputVO.getCsvList();
		if (list != null && list.size() > 0) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH:mm:ss");
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			String fileName ;
			String reportDate;
			fileName = "分行即時速報_" + sdf.format(new Date()) + ".csv";
			reportDate =sdf2.format(new Date());
			
//			switch(inputVO.getSrchType()){
//			case "MTD":
//				fileName = "分行當月MTD即時速報_" + sdf.format(new Date()) + ".csv";
//				reportDate =sdf2.format(new Date());
//				break;
//			case "HIS_MON":
//				fileName = "分行歷史月報_" + inputVO.getTx_ym() + ".csv";
//				reportDate =inputVO.getTx_ym();	
//				break;
//			default	:
//				fileName = "分行即時速報_" + sdf.format(new Date()) + ".csv";
//				reportDate =sdf2.format(new Date());
//				break;
//			}
			
			
			List listCSV = new ArrayList();
			for (Map<String, Object> map : list) {
				String[] records = new String[13];
				int i = 0;
				records[i] = reportDate; // 區域中心name
				records[++i] = checkIsNull(map, "REGION_CENTER_NAME"); // 區域中心name
				records[++i] = checkIsNull(map, "BRANCH_AREA_NAME"); // 營運區 name
				records[++i] = Format2(map, "BRANCH_NBR"); // 分行代碼
				records[++i] = checkIsNull(map, "BRANCH_NAME"); // 分行name
				records[++i] = checkIsNull(map, "BRANCH_GROUP"); // 分行組別
//				// 上一營業日報表
//				records[++i] = currencyFormat(map, "投資實際達成(A)"); // 投資實際達成(A)
//				records[++i] = currencyFormat(map, "投資日目標"); // 投資日目標(B)
//				records[++i] = pcntFormat(map, "投資實際達成率"); // 投資實際達成率(C=A/B)
//				records[++i] = pcntFormat(map, "投資應達成率"); // 投資應達成率(MTD)
//				records[++i] = currencyFormat(map, "保險實際達成(A)"); // 保險實際達成
//				records[++i] = currencyFormat(map, "保險日目標"); // 保險日目標
//				records[++i] = pcntFormat(map, "保險實際達成率"); // 保險實際達成率
//				records[++i] = pcntFormat(map, "保險應達成率"); // 保險應達成率
//				// 合計實際達成
//				NumberFormat nf = NumberFormat.getCurrencyInstance();
//				BigDecimal a1 = new BigDecimal(map.get("投資實際達成(A)") == null ? "0" : ObjectUtils.toString(map.get("投資實際達成(A)")));
//				BigDecimal a2 = new BigDecimal(map.get("保險實際達成(A)") == null ? "0" : ObjectUtils.toString(map.get("保險實際達成(A)")));
//				BigDecimal a3 = a1.add(a2);
//				records[++i] = nf.format(a3);
//				records[++i] = currencyFormat(map, "合計日目標"); // 合計日目標
//				records[++i] = pcntFormat(map, "合計實際達成率"); // 合計實際達成率
//				records[++i] = pcntFormat(map, "合計應達成率"); // 合計應達成
				// 當日日終即時資料
				records[++i] = currencyFormat(map, "INV_FEE"); // 投資實績
				// 投資實際達成率
//				BigDecimal b1 = new BigDecimal(map.get("當日投資實際達成(A)") == null ? "0" : ObjectUtils.toString(map.get("當日投資實際達成(A)")));
//				BigDecimal b2 = new BigDecimal(map.get("inv_tar_d") == null ? "0" : ObjectUtils.toString(map.get("inv_tar_d")));
//				BigDecimal b3 = new BigDecimal(0);
//				if(b2.compareTo(new BigDecimal(0)) == 0)
//					records[++i] = "0%";
//				else {
//					b3 = b1.divide(b2);
//					records[++i] = (int) (Float.parseFloat(b3 + "") + 0.5) + "%";
//				}
				records[++i] =  pcntFormat(map, "INV_DAY_RATE"); // 投資實際達成率
				records[++i] = currencyFormat(map, "INS_FEE"); // 保險實績
				records[++i] =  pcntFormat(map, "INS_DAY_RATE"); // 保險實際達成率
//				BigDecimal c1 = new BigDecimal(map.get("當日保險實際達成") == null ? "0" : ObjectUtils.toString(map.get("當日保險實際達成")));
//				BigDecimal c2 = new BigDecimal(map.get("ins_tar_d") == null ? "0" : ObjectUtils.toString(map.get("ins_tar_d")));
//				BigDecimal c3 = new BigDecimal(0);
//				if(c2.compareTo(new BigDecimal(0)) == 0)
//					records[++i] = "0%";
//				else {
//					c3 = c1.divide(c2);
//					records[++i] = (int) (Float.parseFloat(c3 + "") + 0.5) + "%";
//				}
////				BigDecimal d = b1.add(c1);
//				records[++i] = nf.format(d); // 合計實績
//				BigDecimal e = b3.add(c3);
//				records[++i] = (int) (Float.parseFloat(e + "") + 0.5) + "%"; // 合計實際達成率
				records[++i] = currencyFormat(map, "TOT_FEE"); // 合計實績
				records[++i] =  pcntFormat(map, "TOT_DAY_RATE"); // 合計實際達成率
				listCSV.add(records);
			}
			// header
			String[] csvHeader = new String[12];
			int j = 0;
			csvHeader[j] = "日期";
			csvHeader[++j] = "業務處";
			csvHeader[++j] = "營運區";
			csvHeader[++j] = "分行代碼";
			csvHeader[++j] = "分行";
			csvHeader[++j] = "組別";
			// 上一營業日報表
//			csvHeader[++j] = "投資實際達成(A)";
//			csvHeader[++j] = "投資日目標(B)";
//			csvHeader[++j] = "投資實際達成率(C=A/B)";
//			csvHeader[++j] = "投資應達成率(MTD)";
//			csvHeader[++j] = "保險實際達成";
//			csvHeader[++j] = "保險日目標";
//			csvHeader[++j] = "保險實際達成率";
//			csvHeader[++j] = "保險應達成率";
//			csvHeader[++j] = "合計實際達成";
//			csvHeader[++j] = "合計日目標";
//			csvHeader[++j] = "合計實際達成率";
//			csvHeader[++j] = "合計應達成";
			// 當日日終即時資料
			csvHeader[++j] = "投資實績";
			csvHeader[++j] = "投資實際達成率";
			csvHeader[++j] = "保險實績";
			csvHeader[++j] = "保險實際達成率";
			csvHeader[++j] = "合計實績";
			csvHeader[++j] = "合計實際達成率";

			CSVUtil csv = new CSVUtil();
			csv.setHeader(csvHeader);
			csv.addRecordList(listCSV);
			String url = csv.generateCSV();
			notifyClientToDownloadFile(url, fileName); // download
		} else
			this.sendRtnObject(null);
	}

	/**
	 * 檢查Map取出欄位是否為Null
	 * 
	 * @param map
	 * @return String
	 */
	private String checkIsNull(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key)))
				&& map.get(key) != null) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}

	// 處理貨幣格式
	private String currencyFormat(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key)))
				&& map.get(key) != null) {
			NumberFormat nf = NumberFormat.getInstance();
			return nf.format(map.get(key));
		} else
			return "0.00";
	}

	// 達成率格式
	private String pcntFormat(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key)))
				&& map.get(key) != null) {
			return  (Float.parseFloat(map.get(key) + "") ) + "%";
		} else
			return "";
	}
	
	// 字串多加=""
	private String Format2(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key)))
				&& map.get(key) != null) {
			return String.valueOf("=\""+map.get(key)+"\"");
		} else
			return "";
	}
	
	
	
	
	public void queryAllSum(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		PMS301InputVO inputVO = (PMS301InputVO) body;
		PMS301OutputVO return_VO = new PMS301OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		//2017/3/14 kevin sql
//		sql.append("WITH sum_all as (");
//		sql.append("SELECT alls.*,rec.BRANCH_CLS, rec.region_center_id, rec.region_center_name, rec.BRANCH_AREA_ID, rec.BRANCH_AREA_NAME, rec.branch_name  ");
//		sql.append(" FROM (SELECT * FROM tbpms_org_rec_n WHERE HEAD_OFFICE_ID='105001' AND BRANCH_NBR<>'xxxxxxxxxxxxxxx' AND 　SYSDATE BETWEEN start_time AND end_time ) rec ");
////		sql.append("  SELECT  nvl2(左表.data_date,左表.data_date,右表.data_date)　as    data_date, ");
//		sql.append("  LEFT JOIN ( SELECT   ");
//		sql.append("    nvl2(左表.branch_nbr,左表.branch_nbr,右表.branch_nbr)   AS branch_nbr ,    ");
//		sql.append("    nvl(左表.\"保險實際達成(A)\", 0) as \"保險實際達成(A)\" , ");
//		sql.append("    nvl(左表.\"投資實際達成(A)\", 0) as \"投資實際達成(A)\" , ");
//		sql.append("    左表.合計日目標 ,  ");
//		sql.append("    左表.投資日目標 ,  ");
//		sql.append("    左表.保險日目標 ,  ");
//		sql.append("    左表.投資實際達成率 ,  ");
//		sql.append("    左表.投資應達成率 ,   ");
//		sql.append("    左表.投資總達成率 ,   ");
//		sql.append("    左表.合計實際達成率 , ");
//		sql.append("    左表.合計應達成率 ,");
//		sql.append("    左表.保險實際達成率,");
//		sql.append("    左表.保險應達成率,左表.CDATE,");
//		sql.append("    右表.\"當日投資實際達成(A)\",");
//		sql.append("    右表.當日保險實際達成,");
//		sql.append("    右表.inv_tar_d, ");
//		sql.append("    右表.ins_tar_d, ");
//		sql.append("    nvl(右表.\"當日投資實際達成(A)\", 0) / nvl(右表.inv_tar_d, 1) as inv_act_d, ");
//		sql.append("    nvl(右表.當日保險實際達成 , 0) / nvl(右表.ins_tar_d, 1) as ins_act_d ");
//		sql.append("   FROM (  ");
////		sql.append("   	SELECT    nvl2(戰報投資.data_date, 戰報投資.data_date, 戰報保險.data_date2) AS data_date, ");
//		sql.append("   	SELECT     ");
//		sql.append("   nvl2(戰報投資.branch_nbr, 戰報投資.branch_nbr, 戰報保險.branch_nbr2) AS branch_nbr, ");
//		sql.append("   nvl(戰報保險.保險實際達成, 0) AS \"保險實際達成(A)\",  ");
//		sql.append("   nvl(戰報投資.\"投資實際達成(A)\", 0) AS \"投資實際達成(A)\",  ");
//		sql.append("   戰報投資.合計日目標, ");
//		sql.append("   戰報投資.投資日目標, ");
//		sql.append("   戰報投資.保險日目標, ");
//		sql.append("   戰報投資.投資實際達成率, ");
//		sql.append("   戰報投資.投資應達成率, ");
//		sql.append("   戰報投資.投資總達成率, ");
//		sql.append("   戰報投資.合計實際達成率, ");
//		sql.append("   戰報投資.合計應達成率,  ");
//		sql.append("   戰報投資.保險實際達成率, ");
//		sql.append("   戰報投資.保險應達成率 ,戰報投資.CDATE    ");
//		sql.append("     FROM (");
//		sql.append(" SELECT    profit_l.*, ");
//		sql.append("   nvl(achd.tot_tar_amt_2, 0) AS 合計日目標, ");
//		sql.append("   nvl(achd.inv_tar_amt_2, 0) AS 投資日目標, ");
//		sql.append("   nvl(achd.ins_tar_amt_2, 0) AS 保險日目標, ");
//		sql.append("   nvl(achd.inv_a_rate_1, 0)  AS 投資實際達成率,");
//		sql.append("   nvl(achd.inv_a_rate_2, 0)  AS 投資應達成率,");
//		sql.append("   nvl(achd.inv_a_rate_1, 0)/(case when nvl(achd.inv_a_rate_2, 0) = 0 then 1 else achd.inv_a_rate_2 end)  AS 投資總達成率,");
//		sql.append("   nvl(achd.tot_a_rate_1, 0)  AS 合計實際達成率,");
//		sql.append("   nvl(achd.tot_a_rate_2, 0)  AS 合計應達成率,");
//		sql.append("   nvl(achd.ins_a_rate_1, 0)  AS 保險實際達成率,");
//		sql.append("   nvl(achd.ins_a_rate_2, 0)  AS 保險應達成率 ");
//		sql.append("    FROM ( ");
//		sql.append("SELECT   profit.data_date,");
//		sql.append("  profit.branch_nbr,TRUNC(profit.CREATETIME) AS CDATE,");
//		sql.append("  SUM(nvl(profit.fee, 0)) AS \"投資實際達成(A)\" ");
//		sql.append("  FROM     tbpms_br_day_profit profit");
//		sql.append(" WHERE    profit.item IN ( '01', ");
//		sql.append("'02',");
//		sql.append("'03',");
//		sql.append("'04',");
//		sql.append("'05',");
//		sql.append("'17',");
//		sql.append("'06',");
//		sql.append("'09' ) ");
//		//新增 profit.data_date=:DATES  上一營業日日期
//		sql.append(" and TO_DATE(profit.data_date,'YYYYMMDD')=PABTH_UTIL.FC_getBusiDay(SYSDATE,'TWD',-1) ");
//		
//		sql.append("GROUP BY profit.data_date,TRUNC(profit.CREATETIME),");
//		sql.append("  profit.branch_nbr) profit_l ");
//		sql.append("  left join tbpms_br_achd_rate achd ");
//		sql.append("  ON  achd.data_date = profit_l.data_date    ");
//		sql.append("  AND achd.sum_type = 'M' ");
//		sql.append("  AND achd.branch_nbr = profit_l.branch_nbr) 戰報投資   ");
//		sql.append("  full join ");
//		sql.append("  (");
//		sql.append("   SELECT   day_profit.data_date   AS data_date2, ");
//		sql.append("   day_profit.branch_nbr  AS branch_nbr2,");
//		sql.append("   SUM(nvl(day_profit.fee, 0)) AS 保險實際達成");
//		sql.append("   FROM     tbpms_br_day_profit day_profit ");
//		sql.append("   WHERE    day_profit.item IN ( '10',     ");
//		sql.append("'16',");
//		sql.append("'11',");
//		sql.append("'12' )");
//		//新增 day_profit.data_date=:DATES  上一營業日日期
//		sql.append(" and TO_DATE(day_profit.data_date,'YYYYMMDD')=PABTH_UTIL.FC_getBusiDay(SYSDATE,'TWD',-1) ");
//		
//		sql.append(" GROUP BY day_profit.data_date,");
//		sql.append(" day_profit.branch_nbr) 戰報保險");
//		sql.append(" ON  戰報投資.data_date = 戰報保險.data_date2   ");
//		sql.append(" AND 戰報投資.branch_nbr = 戰報保險.branch_nbr2) 左表     ");
//		sql.append(" full join ");
//		sql.append(" (");
//		sql.append(" SELECT    t.*,   ");
//		sql.append("   rus.inv_tar_d,");
//		sql.append("   rus.ins_tar_d");
//		sql.append(" FROM(  ");
////		sql.append("  SELECT  nvl2(當日投資.tx_date,當日投資.tx_date,當日保險.data_date2) AS data_date, ");
//		sql.append("  SELECT   ");
//		sql.append("    nvl2(當日投資.branch_nbr,當日投資.branch_nbr,當日保險.branch_nbr2)　as    branch_nbr,");
//		sql.append("    當日投資.\"當日投資實際達成(A)\",");
//		sql.append("    當日保險.當日保險實際達成  ");
//		sql.append("    FROM(    ");
//		sql.append("     SELECT profit_l.*     ");
//		sql.append("     FROM   (  ");
////		sql.append("   SELECT   profit.tx_date,");
//		sql.append("   SELECT   ");
//		sql.append("profit.branch_nbr,   ");
//		sql.append("SUM(nvl(profit.fee, 0)) AS \"當日投資實際達成(A)\" ");
//		sql.append("   FROM     tbpms_br_imm_profit profit ");
//		sql.append("   WHERE    profit.item IN ( '01',     ");
//		sql.append("    '02',");
//		sql.append("    '03',");
//		sql.append("    '04',");
//		sql.append("    '05',");
//		sql.append("    '17',");
//		sql.append("    '06',");
//		sql.append("    '09' )     ");
//		//新增 profit.tx_date=:DATES  查詢條件
////		sql.append(" and profit.tx_date=:DATES ");
//		
////		sql.append("   GROUP BY profit.tx_date,");
//		sql.append("   GROUP BY ");
//		sql.append("profit.branch_nbr) profit_l ) 當日投資 ");
//		sql.append("full join");
//		sql.append("    (    ");
////		sql.append(" SELECT   tx_date   AS data_date2,     ");
//		sql.append(" SELECT        ");
//		sql.append("    imm_profit.branch_nbr AS branch_nbr2,    ");
//		sql.append("    SUM(nvl(imm_profit.fee, 0)) AS 當日保險實際達成");
//		sql.append(" FROM     tbpms_br_imm_profit imm_profit     ");
//		sql.append(" WHERE    imm_profit.item IN ( '10',   ");
//		sql.append("'16',    ");
//		sql.append("'11',    ");
//		sql.append("'12' )   ");
//		//新增 imm_profit.tx_date=:DATES  查詢條件
////		sql.append(" and imm_profit.tx_date=:DATES ");
//		
////		sql.append("   GROUP BY tx_date,   ");
//		sql.append("   GROUP BY    ");
//		sql.append("   imm_profit.branch_nbr) 當日保險    ");
////		sql.append("    ON   當日投資.tx_date = 當日保險.data_date2   ");
////		sql.append("    AND  當日投資.branch_nbr = 當日保險.branch_nbr2) t  ");
//		sql.append("    ON  當日投資.branch_nbr = 當日保險.branch_nbr2) t  ");
//		sql.append("    left join  ");
//		sql.append("  (");
//		sql.append("   SELECT to_char(SYSDATE,'YYYYMMDD') AS data_date,     ");
//		sql.append("    branch_nbr,");
//		sql.append("    round(me.inv_tar_amount/ pabth_util.fc_gettwodatediff(last_day(add_months(SYSDATE, -1)) + 1, last_day(SYSDATE))) inv_tar_d, ");
//		sql.append("    round(me.ins_tar_amount/ pabth_util.fc_gettwodatediff(last_day(add_months(SYSDATE, -1)) + 1, last_day(SYSDATE))) ins_tar_d  ");
//		sql.append("   FROM   tbpms_br_prd_tar_m me    ");
//		sql.append("   WHERE  me.data_yearmon = to_char(SYSDATE, 'yyyyMM')) rus     ");
////		sql.append("    ON  rus.data_date=t.data_date");
////		sql.append("    AND t.branch_nbr=rus.branch_nbr ) 右表  ");
//		
//		sql.append("    ON t.branch_nbr=rus.branch_nbr ) 右表  ");
//		sql.append("  ON  左表.branch_nbr=右表.branch_nbr  ");
//		//串接日期條件註解
////		sql.append("  AND 左表.data_date=右表.data_date) alls    ");
//		sql.append("  ) alls    ");
//		sql.append("on alls.branch_nbr=rec.branch_nbr ");
//		sql.append("AND 　SYSDATE BETWEEN rec.start_time AND rec.end_time   ");
//		sql.append(")     ");
//		sql.append("   select                                                                    ");
//		sql.append("   SUM(sum_all.\"保險實際達成(A)\"        ) AS \"保險實際達成(A)\"     ,     ");
//		sql.append("   SUM(sum_all.\"投資實際達成(A)\"        ) AS \"投資實際達成(A)\"     ,     ");
//		sql.append("   SUM(sum_all.\"合計日目標\"             ) AS \"合計日目標\"          ,     ");
//		sql.append("   SUM(sum_all.\"投資日目標\"             ) AS \"投資日目標\"          ,     ");
//		sql.append("   SUM(sum_all.\"保險日目標\"             ) AS \"保險日目標\"          ,     ");
//		sql.append("   AVG(sum_all.\"投資實際達成率\"         ) AS \"投資實際達成率\"      ,     ");
//		sql.append("   AVG(sum_all.\"投資應達成率\"           ) AS \"投資應達成率\"        ,     ");
//		sql.append("   AVG(sum_all.\"投資總達成率\"           ) AS \"投資總達成率\"        ,     ");
//		sql.append("   AVG(sum_all.\"合計實際達成率\"         ) AS \"合計實際達成率\"      ,     ");
//		sql.append("   AVG(sum_all.\"合計應達成率\"           ) AS \"合計應達成率\"        ,     ");
//		sql.append("   AVG(sum_all.\"保險實際達成率\"         ) AS \"保險實際達成率\"      ,     ");
//		sql.append("   AVG(sum_all.\"保險應達成率\"           ) AS \"保險應達成率\"        ,     ");
//		sql.append("   SUM(sum_all.\"當日投資實際達成(A)\"    ) AS \"當日投資實際達成(A)\" ,     ");
//		sql.append("   SUM(sum_all.\"當日保險實際達成\"       ) AS \"當日保險實際達成\"    ,     ");
//		sql.append("   SUM(sum_all.INV_TAR_D                ) AS INV_TAR_D,                      ");
//		sql.append("   SUM(sum_all.INS_TAR_D                ) AS INS_TAR_D,                      ");
//		sql.append("   AVG(sum_all.INV_ACT_D                ) AS INV_ACT_D,                      ");
//		sql.append("   AVG(sum_all.INS_ACT_D                ) AS INS_ACT_D,                      ");
//		sql.append("   sum_all.branch_area_name as DEPT_NAME                                     ");
//		sql.append("   from                                                                      ");
//		sql.append("   sum_all                                                                   ");
//		sql.append("   group by branch_area_name                                                 ");
//		sql.append("   UNION ALL                                                                 ");
//		sql.append("   select                                                                    ");
//		sql.append("   SUM(sum_all.\"保險實際達成(A)\"        ) AS \"保險實際達成(A)\"     ,     ");
//		sql.append("   SUM(sum_all.\"投資實際達成(A)\"        ) AS \"投資實際達成(A)\"     ,     ");
//		sql.append("   SUM(sum_all.\"合計日目標\"             ) AS \"合計日目標\"          ,     ");
//		sql.append("   SUM(sum_all.\"投資日目標\"             ) AS \"投資日目標\"          ,     ");
//		sql.append("   SUM(sum_all.\"保險日目標\"             ) AS \"保險日目標\"          ,     ");
//		sql.append("   AVG(sum_all.\"投資實際達成率\"         ) AS \"投資實際達成率\"      ,     ");
//		sql.append("   AVG(sum_all.\"投資應達成率\"           ) AS \"投資應達成率\"        ,     ");
//		sql.append("   AVG(sum_all.\"投資總達成率\"           ) AS \"投資總達成率\"        ,     ");
//		sql.append("   AVG(sum_all.\"合計實際達成率\"         ) AS \"合計實際達成率\"      ,     ");
//		sql.append("   AVG(sum_all.\"合計應達成率\"           ) AS \"合計應達成率\"        ,     ");
//		sql.append("   AVG(sum_all.\"保險實際達成率\"         ) AS \"保險實際達成率\"      ,     ");
//		sql.append("   AVG(sum_all.\"保險應達成率\"           ) AS \"保險應達成率\"        ,     ");
//		sql.append("   SUM(sum_all.\"當日投資實際達成(A)\"    ) AS \"當日投資實際達成(A)\" ,     ");
//		sql.append("   SUM(sum_all.\"當日保險實際達成\"       ) AS \"當日保險實際達成\"    ,     ");
//		sql.append("   SUM(sum_all.INV_TAR_D                ) AS INV_TAR_D,                      ");
//		sql.append("   SUM(sum_all.INS_TAR_D                ) AS INS_TAR_D,                      ");
//		sql.append("   AVG(sum_all.INV_ACT_D                ) AS INV_ACT_D,                      ");
//		sql.append("   AVG(sum_all.INS_ACT_D                ) AS INS_ACT_D,                      ");
//		sql.append("   sum_all.REGION_CENTER_NAME as DEPT_NAME                                   ");
//		sql.append("   from                                                                      ");
//		sql.append("   sum_all                                                                   ");
//		sql.append("   group by REGION_CENTER_NAME                                               ");
//		sql.append("   UNION ALL                                                                 ");
//		sql.append("   select                                                                    ");
//		sql.append("   SUM(sum_all.\"保險實際達成(A)\"         ) AS \"保險實際達成(A)\"     ,     ");
//		sql.append("   SUM(sum_all.\"投資實際達成(A)\"         ) AS \"投資實際達成(A)\"     ,     ");
//		sql.append("   SUM(sum_all.\"合計日目標\"             ) AS \"合計日目標\"          ,     ");
//		sql.append("   SUM(sum_all.\"投資日目標\"             ) AS \"投資日目標\"          ,     ");
//		sql.append("   SUM(sum_all.\"保險日目標\"             ) AS \"保險日目標\"          ,     ");
//		sql.append("   AVG(sum_all.\"投資實際達成率\"          ) AS \"投資實際達成率\"      ,     ");
//		sql.append("   AVG(sum_all.\"投資應達成率\"           ) AS \"投資應達成率\"        ,     ");
//		sql.append("   AVG(sum_all.\"投資總達成率\"           ) AS \"投資總達成率\"        ,     ");
//		sql.append("   AVG(sum_all.\"合計實際達成率\"          ) AS \"合計實際達成率\"      ,     ");
//		sql.append("   AVG(sum_all.\"合計應達成率\"           ) AS \"合計應達成率\"        ,     ");
//		sql.append("   AVG(sum_all.\"保險實際達成率\"         ) AS \"保險實際達成率\"      ,     ");
//		sql.append("   AVG(sum_all.\"保險應達成率\"           ) AS \"保險應達成率\"        ,     ");
//		sql.append("   SUM(sum_all.\"當日投資實際達成(A)\"      ) AS \"當日投資實際達成(A)\" ,     ");
//		sql.append("   SUM(sum_all.\"當日保險實際達成\"         ) AS \"當日保險實際達成\"    ,     ");
//		sql.append("   SUM(sum_all.INV_TAR_D              ) AS INV_TAR_D,                      ");
//		sql.append("   SUM(sum_all.INS_TAR_D               ) AS INS_TAR_D,                      ");
//		sql.append("   AVG(sum_all.INV_ACT_D                ) AS INV_ACT_D,                      ");
//		sql.append("   AVG(sum_all.INS_ACT_D                ) AS INS_ACT_D,                      ");
//		sql.append("   'ALL' as DEPT_NAME                                                        ");
//		sql.append("   from                                                                      ");
//		sql.append("   sum_all                                                                   ");
//		sql.append("   UNION ALL                                                                 ");
//		sql.append("   select                                                                    ");
//		sql.append("   0 AS \"保險實際達成(A)\"     ,     ");
//		sql.append("   0 AS \"投資實際達成(A)\"     ,     ");
//		sql.append("   0 AS \"合計日目標\"          ,     ");
//		sql.append("   0 AS \"投資日目標\"          ,     ");
//		sql.append("   0 AS \"保險日目標\"          ,     ");
//		sql.append("   0 AS \"投資實際達成率\"      ,     ");
//		sql.append("   0 AS \"投資應達成率\"        ,     ");
//		sql.append("   0 AS \"投資總達成率\"        ,     ");
//		sql.append("   0 AS \"合計實際達成率\"      ,     ");
//		sql.append("   0 AS \"合計應達成率\"        ,     ");
//		sql.append("   0 AS \"保險實際達成率\"      ,     ");
//		sql.append("   0 AS \"保險應達成率\"        ,     ");
//		sql.append("   0 AS \"當日投資實際達成(A)\" ,     ");
//		sql.append("   0 AS \"當日保險實際達成\"    ,     ");
//		sql.append("   0 AS INV_TAR_D,                      ");
//		sql.append("   0 AS INS_TAR_D,                      ");
//		sql.append("   0 AS INV_ACT_D,                      ");
//		sql.append("   to_number(to_char(MAX(CREATETIME),'yyyymmdd')) AS INS_ACT_D,                      ");
//		sql.append("   'TIME' as DEPT_NAME                                                        ");
//		sql.append("   from                                                                      ");
//		sql.append("   TBPMS_BR_IMM_PROFIT                                                                   ");
		
		sql.append("  WITH  SELECT_ALL  AS ");
		sql.append("  (    ");
		sql.append("  SELECT   BRIP_ALL.REGION_CENTER_ID,    ");
		sql.append("           BRIP_ALL.REGION_CENTER_NAME,  ");
		sql.append("           BRIP_ALL.BRANCH_AREA_ID,      ");
		sql.append("           BRIP_ALL.BRANCH_AREA_NAME,    ");
		sql.append("           BRIP_ALL.BRANCH_NBR,   ");
		sql.append("           BRIP_ALL.BRANCH_NAME,  ");
		sql.append("           BRIP_ALL.BRANCH_GROUP , ");
		sql.append("           BRIP_ALL.INV_FEE AS INV_FEE,     "); //投資實績
		sql.append("           BRIP_ALL.INS_FEE AS INS_FEE,     "); //保險實績
		sql.append("           BRIP_ALL.INV_FEE + BRIP_ALL.INS_FEE AS TOT_FEE,   ");  //合計實績
		sql.append("           NVL(100 * INV_FEE / NULLIF(BRT.INV_DAY_TAR,0),0) AS INV_DAY_RATE,  ");  //投資實際達成率
		sql.append("           NVL(100 * INS_FEE / NULLIF(BRT.INS_DAY_TAR,0),0) AS INS_DAY_RATE,  ");  //保險實際達成率
		sql.append("           NVL(100 * (INV_FEE + INS_FEE) / NULLIF(BRT.INV_DAY_TAR + BRT.INS_DAY_TAR,0),0) AS TOT_DAY_RATE, ");
		sql.append("           INV_DAY_TAR,  ");
		sql.append("           INS_DAY_TAR,  ");
		sql.append("           INV_DAY_TAR + INS_DAY_TAR AS TOT_TAR     ");
		sql.append("  FROM      ( SELECT  NVL(REC.REGION_CENTER_ID,'全行總計') AS REGION_CENTER_ID, ");
		sql.append("                      REC.REGION_CENTER_NAME AS REGION_CENTER_NAME,        ");
		sql.append("                      NVL(REC.BRANCH_AREA_ID,'處合計') AS BRANCH_AREA_ID,  ");
		sql.append("                      REC.BRANCH_AREA_NAME AS BRANCH_AREA_NAME,    ");
		sql.append("                      NVL(REC.BRANCH_NBR,'區合計') AS BRANCH_NBR,  ");
		sql.append("                      REC.BRANCH_NAME AS BRANCH_NAME,      ");
		sql.append("                      REC.BRANCH_GROUP AS BRANCH_GROUP,    ");
		sql.append("                      SUM(NVL(BRIP.INV_FEE,0)) AS INV_FEE, ");
		sql.append("                      SUM(NVL(BRIP.INS_FEE,0)) AS INS_FEE  ");
		sql.append("              FROM VWORG_DEFN_INFO REC                     ");
		sql.append("              LEFT JOIN ( SELECT BRANCH_NBR AS BRANCH_NBR, ");
		sql.append("                                NVL(FEE,0) AS FEE,         ");
		sql.append("                                CASE WHEN ITEM IN ('01','02','03','04','05','06','17') THEN  'INV'    ");   //'09','15' 匯兌 信託  拔掉   暫時無用
		sql.append("                                     WHEN ITEM IN ('10','11','12','16') THEN 'INS'                              ");
		sql.append("                                END AS ITEM                  ");
		sql.append("                          FROM TBPMS_BR_IMM_PROFIT BRIP      ");
		sql.append("                          WHERE ITEM NOT IN ('90','91','92') ");
		sql.append("                        )  ");
		sql.append("                        PIVOT (SUM(FEE) AS FEE                    ");
		sql.append("                        FOR ITEM IN ('INV' INV, 'INS' INS)) BRIP  ");
		sql.append("              ON REC.BRANCH_NBR = BRIP.BRANCH_NBR                 ");
		sql.append("              GROUP BY ROLLUP((REC.REGION_CENTER_ID,REC.REGION_CENTER_NAME),(REC.BRANCH_AREA_ID,REC.BRANCH_AREA_NAME),(REC.BRANCH_NBR,REC.BRANCH_NAME,REC.BRANCH_GROUP))  ");
		sql.append("            ) BRIP_ALL                                                     ");
		sql.append("  LEFT JOIN ( SELECT NVL(REGION_CENTER_ID,'全行總計') AS REGION_CENTER_ID, ");
		sql.append("                    NVL(BRANCH_AREA_ID,'處合計') AS BRANCH_AREA_ID,        ");
		sql.append("                    NVL(BRANCH_NBR,'區合計') AS BRANCH_NBR,                ");
		sql.append("                    SUM(ROUND(NVL(INV_TAR_AMOUNT,0) / pabth_util.fc_gettwodatediff(last_day(add_months(SYSDATE, -1)) + 1, last_day(SYSDATE)))) AS INV_DAY_TAR,  ");
		sql.append("                    SUM(ROUND(NVL(INS_TAR_AMOUNT,0) /                                                                                ");
		sql.append("                      CASE WHEN SYSDATE < to_date(TO_CHAR(SYSDATE,'YYYYMM')||'26','YYYYMMDD') THEN                                   ");
		sql.append("                        pabth_util.fc_gettwodatediff(last_day(add_months(SYSDATE, -2)) + 26, last_day(add_months(SYSDATE, -1)) + 25) ");
		sql.append("                      ELSE   ");
		sql.append("                        pabth_util.fc_gettwodatediff(last_day(add_months(SYSDATE, -1)) + 26, last_day(SYSDATE) + 25)   ");
		sql.append("                      END)) AS INS_DAY_TAR      ");
		sql.append("              FROM TBPMS_BR_PRD_TAR_M    ");
		sql.append("              WHERE DATA_YEARMON = TO_CHAR(SYSDATE,'YYYYMM')  ");
		sql.append("              GROUP BY ROLLUP(REGION_CENTER_ID,BRANCH_AREA_ID,BRANCH_NBR) ");
		sql.append("            ) BRT      ");
		sql.append("            ON (BRIP_ALL.REGION_CENTER_ID = BRT.REGION_CENTER_ID AND BRIP_ALL.BRANCH_AREA_ID = BRT.BRANCH_AREA_ID AND BRIP_ALL.BRANCH_NBR = BRT.BRANCH_NBR)  ");
		sql.append("  )     ");
		sql.append("  SELECT * FROM SELECT_ALL WHERE BRANCH_NAME IS  NULL    ");
		sql.append("  UNION ALL                                                        ");
		sql.append("  SELECT                                                           ");
		sql.append("  'TIME' AS REGION_CENTER_ID                                       ");
		sql.append("  ,'TIME' AS REGION_CENTER_NAME                                    ");
		sql.append("  ,'TIME' AS BRANCH_AREA_ID                                        ");
		sql.append("  ,'TIME' AS BRANCH_AREA_NAME                                      ");
		sql.append("  ,'TIME' AS BRANCH_NBR                                            ");
		sql.append("  ,'TIME' AS BRANCH_NAME                                           ");
		sql.append("  ,'TIME' AS BRANCH_GROUP                                          ");
		sql.append("  ,to_number(to_char(MAX(CREATETIME),'yyyymmdd'))  AS INV_FEE      ");
		sql.append("  ,0 AS INS_FEE                                                    ");
		sql.append("  ,0 AS TOT_FEE                                                    ");
		sql.append("  ,0 AS INV_DAY_RATE                                               ");
		sql.append("  ,0 AS INS_DAY_RATE                                               ");
		sql.append("  ,0 AS TOT_DAY_RATE                                               ");
		sql.append("  ,0 AS INV_DAY_TAR                                                ");
		sql.append("  ,0 AS INS_DAY_TAR                                                ");
		sql.append("  ,0 AS TOT_TAR                                                    ");
		sql.append("  FROM TBPMS_BR_IMM_PROFIT                                         ");
		//移除WHERE 1=1
		
		//額外多出來串接日期
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
//		String aa = sdf1.format(new Date());
//		queryCondition.setObject("DATES", aa);
		queryCondition.setQueryString(sql.toString());
		//
		List<Map<String, Object>> list1 = dam.exeQuery(queryCondition);
	
		return_VO.setSumAllList(list1);	
		
		this.sendRtnObject(return_VO);
	}
}
