package com.systex.jbranch.app.server.fps.pms302;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import com.systex.jbranch.app.server.fps.pms301.PMS301OutputVO;
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
 * Description :理專即時速報Controller <br>
 * Comments Name : PMS302.java<br>
 * Author :frank<br>
 * Date :2016年07月01日 <br>
 * Version : 1.01 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年02月01日<br>
 */

@Component("pms302")
@Scope("request")
public class PMS302 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PMS302.class);
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

	public void queryData(Object body, IPrimitiveMap header)
			throws JBranchException, ParseException {
		PMS302InputVO inputVO = (PMS302InputVO) body;
		PMS302OutputVO outputVO = new PMS302OutputVO();
		
		String roleID = (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> fcMap = xmlInfo.doGetVariable("FUBONSYS.FC_ROLE",FormatHelper.FORMAT_2); // 理專
		Map<String, String> psopMap = xmlInfo.doGetVariable("FUBONSYS.PSOP_ROLE", FormatHelper.FORMAT_2); // OP
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); // 總行人員

		// 取得查詢資料可視範圍
		PMS000 pms000 = (PMS000) PlatformContext.getBean("pms000");
		PMS000InputVO pms000InputVO = new PMS000InputVO();
		pms000InputVO.setReportDate(sdf.format(inputVO.getsCreDate()));
		PMS000OutputVO pms000outputVO = pms000.getOrg(pms000InputVO);
		
		
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		Calendar cal=Calendar.getInstance();
		SimpleDateFormat sdfYM = new SimpleDateFormat("yyyyMM");
		
		String tragetTable ="TBPMS_AO_IMM_PROFIT";
		
		//MTD月報
		if("MTD".equals(inputVO.getSrchType())){
			tragetTable ="TBPMS_AO_IMM_PROFIT_MTD";
			condition.setObject("ym", inputVO.getTx_ym());
//			condition.setObject("dd", String.valueOf(cal.get(Calendar.DATE)));
		}
//		else if("HIS_MON".equals(inputVO.getSrchType())){//歷史月報
//			tragetTable ="TBPMS_AO_IMM_PROFIT_MTD";
//			condition.setObject("ym", inputVO.getTx_ym());
//			condition.setObject("dd", 25);//因為查看歷史月份，保險當月只到25號
//			cal.set(Calendar.DATE, 25);
//		}
		
		
		ArrayList<String> sql_list = new ArrayList<String>();
		StringBuffer sql = new StringBuffer(); 
		sql.append("    SELECT   ORG.CENTER_ID,    ");
		sql.append("             ORG.CENTER_NAME,  ");
		sql.append("             ORG.AREA_ID,      ");
		sql.append("             ORG.AREA_NAME,    ");
		sql.append("             ORG.BRA_NBR,      ");
		sql.append("             ORG.BRANCH_NAME,  ");
		sql.append("             ORG.EMP_ID,   ");
		sql.append("             ORG.EMP_NAME, ");
		sql.append("             ORG.AO_CODE,  ");
		sql.append("             NVL(AOIP.INV_FEE,0) AS INV_FEE,  ");
		sql.append("             NVL(AOIP.INS_FEE,0) AS INS_FEE,  ");
		sql.append("             NVL(AOIP.INV_FEE,0) + NVL(AOIP.INS_FEE,0) AS TOT_FEE,   ");
		sql.append("             NVL(100 * NVL(AOIP.INV_FEE,0) / NULLIF(AOT.INV_DAY_TAR,0),0) AS INV_DAY_RATE, ");
		sql.append("             NVL(100 * NVL(AOIP.INS_FEE,0) / NULLIF(AOT.INS_DAY_TAR,0),0) AS INS_DAY_RATE, ");
		sql.append("             NVL(100 * (NVL(AOIP.INV_FEE,0) + NVL(AOIP.INS_FEE,0)) / NULLIF(AOT.INV_DAY_TAR + AOT.INS_DAY_TAR,0),0) AS TOT_DAY_RATE,   ");
		sql.append("             INV_DAY_TAR, ");
		sql.append("             INS_DAY_TAR, ");
		sql.append("             NVL(AOT.INV_DAY_TAR,0) + NVL(AOT.INS_DAY_TAR,0) AS TOT_TAR   ");
		sql.append("    FROM VWORG_AO_INFO ORG    ");
		sql.append("    LEFT JOIN ( SELECT AO_CODE AS AO_CODE,   ");
		sql.append("                       BRANCH_NBR,    ");
		sql.append("                       NVL(FEE,0) AS FEE,    ");
		sql.append("                       CASE WHEN ITEM IN ('01','02','03','04','05','06','17') THEN  'INV'  ");   //'09','15'  匯兌損益  拿掉  9 15
		sql.append("                            WHEN ITEM IN ('10','11','12','16') THEN 'INS'    ");
		sql.append("                       END AS ITEM      ");		
		sql.append("                FROM "+tragetTable+"      ");
		sql.append("                WHERE ITEM NOT IN ('90','91','92') ");
		if("NOW".equals(inputVO.getSrchType())){
		sql.append("						    and TX_DATE = TO_CHAR(SYSDATE,'YYYYMMDD') ");
		}else {
				    //保險跟投資MTD日期切點不一樣，26號後保險資料算下個月的。
//					if(cal.get(Calendar.DATE)<=25){
			sql.append("						    and TX_YM = :ym ");			
//					}															
//					else{
//			sql.append("						    and ( "   );
//			sql.append("						    	     (ITEM IN ('01','02','03','04','05','06','17') and TX_YM = :ym ) or ");
//		    sql.append("							         (ITEM IN ('10','11','12','16') and TX_YM =TO_CHAR(ADD_MONTHS(to_date(:ym,'YYYYMM'), 1),'YYYYMM') )  ");
//			sql.append("                        		)  "  );
//					}	
											
		}
		sql.append("              )      ");
		sql.append("              PIVOT (SUM(FEE) AS FEE   ");
		sql.append("              FOR ITEM IN ('INV' INV, 'INS' INS)) AOIP  ");
		sql.append("    ON ORG.AO_CODE = AOIP.AO_CODE AND ORG.BRA_NBR = AOIP.BRANCH_NBR  ");
		sql.append("    LEFT JOIN (      ");
		sql.append("                SELECT  AO_CODE AS AO_CODE,             ");
//		sql.append("                        CASE WHEN DATA_YEARMON = TO_CHAR(SYSDATE,'YYYYMM') THEN     ");
//		sql.append("                             ROUND(0.32 * NVL(TAR_AMOUNT,0) / pabth_util.fc_gettwodatediff(last_day(add_months(SYSDATE, -1)) + 1, last_day(SYSDATE)))  ");
//		sql.append("                        ELSE   ");
//		sql.append("                            ROUND(0.32 * NVL(TAR_AMOUNT,0) / pabth_util.fc_gettwodatediff(last_day(add_months(SYSDATE, -2)) + 1, last_day(add_months(SYSDATE, -1))))  ");
//		sql.append("                        END AS INV_DAY_TAR,    ");
//		sql.append("                        CASE WHEN DATA_YEARMON = TO_CHAR(SYSDATE,'YYYYMM') THEN    ");
//		sql.append("                            ROUND(0.68 * NVL(TAR_AMOUNT,0) / pabth_util.fc_gettwodatediff(last_day(add_months(SYSDATE, -2)) + 26, last_day(add_months(SYSDATE, -1)) + 25)) ");
//		sql.append("                        ELSE      ");
//		sql.append("                            ROUND(0.68 * NVL(TAR_AMOUNT,0) / pabth_util.fc_gettwodatediff(last_day(add_months(SYSDATE, -1)) + 26, last_day(SYSDATE) + 25))   ");
//		sql.append("                        END AS INS_DAY_TAR   ");
		if("NOW".equals(inputVO.getSrchType())){
			sql.append("                    SUM(ROUND(CASE WHEN DATA_YEARMON = to_char(sysdate,'YYYYMM') then (0.32 * NVL(TAR_AMOUNT,0)) else 0 end  / pabth_util.fc_gettwodatediff(last_day(add_months(SYSDATE, -1)) + 1, last_day(SYSDATE)))) AS INV_DAY_TAR,  ");
			sql.append("                    SUM(ROUND(CASE WHEN (to_char(sysdate,'dd') <=25 and DATA_YEARMON = to_char(sysdate,'YYYYMM')) OR                                                          ");
			sql.append("                                        (to_char(sysdate,'dd') >=26 and DATA_YEARMON = to_char(add_months(sysdate,1),'YYYYMM')) then (0.68 * NVL(TAR_AMOUNT,0)) else 0 end / ");
			sql.append("                              CASE WHEN SYSDATE < to_date(TO_CHAR(SYSDATE,'YYYYMM')||'26','YYYYMMDD') THEN                                   ");
			sql.append("                              pabth_util.fc_gettwodatediff(last_day(add_months(SYSDATE, -2)) + 26, last_day(add_months(SYSDATE, -1)) + 25) ");
			sql.append("                              ELSE   ");
			sql.append("                              pabth_util.fc_gettwodatediff(last_day(add_months(SYSDATE, -1)) + 26, last_day(SYSDATE) + 25)   ");
			sql.append("                    END)) AS INS_DAY_TAR      ");
			sql.append("                FROM TBPMS_AO_PRD_TAR_M      ");
			sql.append("                WHERE DATA_YEARMON IN (to_char(sysdate,'YYYYMM'),TO_CHAR(add_months(sysdate, + 1),'YYYYMM'))  ");
		}else {
			sql.append("					SUM(0.32 * NVL(TAR_AMOUNT,0)) AS INV_DAY_TAR,																			                                        ");
			sql.append("                    SUM(0.68 * NVL(TAR_AMOUNT,0)) AS INS_DAY_TAR                                                          ");                                                                                                              
			sql.append("                FROM TBPMS_AO_PRD_TAR_M      ");
			sql.append("                WHERE DATA_YEARMON = :ym  ");
		}
		
		sql.append("                GROUP BY AO_CODE ");
		sql.append("              ) AOT                     ");
		sql.append("    ON  ORG.AO_CODE = AOT.AO_CODE  ");
//		sql.append("        AOT.DATA_YEARMON = (CASE WHEN SYSDATE < to_date(TO_CHAR(SYSDATE,'YYYYMM')||'26','YYYYMMDD') THEN  TO_CHAR(SYSDATE,'YYYYMM')    ");
//		sql.append("                           ELSE TO_CHAR(add_months(SYSDATE, + 1),'YYYYMM') END)   ");
		sql.append(" WHERE     1=1									 ");
//		// 資料統計日期
//        if(inputVO.getsCreDate() != null){
//        	sql.append(" and alls.tx_date=:DATES ");
//        	condition.setObject("DATES" ,sdf.format(inputVO.getsCreDate()));
//        }
		// 區域中心
        if (inputVO.getRegion_center_id() != null && !inputVO.getRegion_center_id().equals("")) {
        	sql.append(" and org.center_id =:region_center_idd ");
        	condition.setObject("region_center_idd",inputVO.getRegion_center_id());
        }else {
			// 登入非總行人員強制加區域中心
			if (!headmgrMap.containsKey(roleID)) {
				sql.append("and org.center_id IN (:region_center_idd) ");
				condition.setObject("region_center_idd",pms000outputVO.getV_regionList());
			}
		}
        
		// 營運區
        if (inputVO.getBranch_area_id() != null && !inputVO.getBranch_area_id().equals("")) {
        	sql.append(" and org.area_id =:branch_area_idd ");
        	condition.setObject("branch_area_idd" ,inputVO.getBranch_area_id());
        }else {
			// 登入非總行人員強制加營運區
			if (!headmgrMap.containsKey(roleID)) {
				sql.append("and org.area_id IN (:branch_area_idd) ");
				condition.setObject("branch_area_idd",pms000outputVO.getV_areaList());
			}
		}
        
		// 分行
        if (inputVO.getBranch_nbr() != null && !inputVO.getBranch_nbr().equals("")) {
        	sql.append(" and org.bra_nbr =:branch_nbrr ");
        	condition.setObject("branch_nbrr" ,inputVO.getBranch_nbr());
        }else {
			// 登入非總行人員強制加分行
			if (!headmgrMap.containsKey(roleID)) {
				sql.append("and org.bra_nbr IN (:branch_nbrr) ");
				condition.setObject("branch_nbrr",pms000outputVO.getV_branchList());
			}
		}
        
		// 理專員編
        if (inputVO.getAo_code() != null && !inputVO.getAo_code().equals("")) {
        	sql.append(" and org.ao_code =:ao_codeee ");
        	condition.setObject("ao_codeee",inputVO.getAo_code());
        }else {
			// 登入為銷售人員強制加AO_CODE
			if (fcMap.containsKey(roleID) || psopMap.containsKey(roleID)) {
				sql.append("and org.ao_code IN (:ao_codeee) ");
				condition.setObject("ao_codeee", pms000outputVO.getV_aoList());
			}
		}
//        sql.append("  GROUP BY ALLS.TX_DATE,ALLS.AO_CODE,ALLS.BRANCH_NBR,ALLS.EMP_NAME,ALLS.AMOUNT,");
//        sql.append("  ALLS.D_INV_ACT,ALLS.D_INS_ACT,ALLS.D_INSU_ACT,");
//        sql.append("  ALLS.INV_ACT,ALLS.INS_ACT,ALLS.INSU_ACT,ALLS.CREATETIME,");
//        sql.append("  rec.region_center_id,rec.branch_name, rec.region_center_name ,"); 
//        sql.append("  rec.BRANCH_AREA_ID , rec.BRANCH_AREA_NAME");
        sql.append("  order by org.center_id,org.AREA_ID,org.bra_nbr");
		condition.setQueryString(sql.toString());
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
		ResultIF list = dam.executePaging(condition,inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
		int totalPage = list.getTotalPage();
		outputVO.setTotalPage(totalPage);
		outputVO.setcList(clist);
		outputVO.setResultList(list);
		outputVO.setTotalRecord(list.getTotalRecord());

		outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());
		sendRtnObject(outputVO);
		outputVO.setTotalList(dam.exeQuery(condition));
	}
	
	/** 查詢MTD資料月份
	 * @throws ParseException **/
	public void getYearMon(Object body, IPrimitiveMap header)
			throws JBranchException, ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
		PMS302OutputVO outputVO = new PMS302OutputVO();
		DataAccessManager dam = this.getDataAccessManager();
		
		QueryConditionIF condition = 
				dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		// 主查詢 sql 
		StringBuffer sql = new StringBuffer();
		sql.append("  SELECT SUBSTR(TX_YM,0,4)||'/'||SUBSTR(TX_YM,5,6) as LABEL, TX_YM as DATA FROM TBPMS_AO_IMM_PROFIT_MTD  ");
		sql.append("  WHERE TO_DATE(TX_YM,'YYYYMM') BETWEEN ADD_MONTHS(TO_DATE((SELECT MAX(TX_YM) FROM TBPMS_AO_IMM_PROFIT_MTD),'YYYYMM'),-6)  AND TO_DATE((SELECT MAX(TX_YM) FROM TBPMS_AO_IMM_PROFIT_MTD),'YYYYMM') ");
		sql.append("  GROUP BY TX_YM ORDER BY TO_DATE(TX_YM,'YYYYMM') DESC ");
		condition.setQueryString(sql.toString());
		
		List<Map<String, Object>> list = dam.exeQuery(condition);
		outputVO.setResultList(list);
		sendRtnObject(outputVO);

	}

	public void queryDetail(Object body, IPrimitiveMap header) throws JBranchException {
		PMS302InputdetailVO inputVO = (PMS302InputdetailVO) body;
		PMS302OutputVO outputVO = new PMS302OutputVO();
		DataAccessManager dam = this.getDataAccessManager();
		SimpleDateFormat sdfYM = new SimpleDateFormat("yyyyMM");
		Calendar cal=Calendar.getInstance();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		//MTD月報
		String tragetTable ="TBPMS_CUST_IMM_PROFIT";
		if("MTD".equals(inputVO.getSrchType())){
			tragetTable ="TBPMS_CUST_IMM_PROFIT_MTD";
			queryCondition.setObject("ym", inputVO.getTx_ym());
	//					queryCondition.setObject("dd", String.valueOf(cr.get(Calendar.DATE)));
		}
//		else if("HIS_MON".equals(inputVO.getSrchType())){//歷史月報
//			tragetTable ="TBPMS_CUST_IMM_PROFIT_MTD";
//			queryCondition.setObject("ym", inputVO.getTx_ym());
//			cal.set(Calendar.DATE, 25);
//	//					queryCondition.setObject("dd", 25);//因為查看歷史月份，保險當月只到25號
//		}
		
		StringBuffer sql = new StringBuffer();
		if("NOW".equals(inputVO.getSrchType())){
			sql.append(" SELECT * FROM   ( ");
			sql.append(" SELECT item, CUST_NAME, AO_CODE, ");
			sql.append(" Sum(Nvl(fee,0)) AS fee, tx_date, B.CUST_ID  ");
			sql.append(" FROM " + tragetTable + " B  ");
			sql.append(" LEFT JOIN (SELECT CUST_ID, CUST_NAME FROM TBCRM_CUST_MAST ) A ");
			sql.append(" ON A.CUST_ID=B.CUST_ID ");
			sql.append(" AND CUST_NAME=A.CUST_NAME  ");
			sql.append("    WHERE TX_DATE=TO_CHAR(SYSDATE,'YYYYMMDD') ");
			sql.append("    and B.BRANCH_NBR = :branch_nbr ");
			sql.append(" GROUP BY item, B.CUST_ID, tx_date, CUST_NAME ,AO_CODE) ");
		}
		
		else{
			sql.append(" SELECT * FROM   ( ");
			sql.append(" SELECT item, CUST_NAME, AO_CODE, ");
			sql.append(" Sum(Nvl(fee,0)) AS fee, B.CUST_ID  ");
			sql.append(" FROM " + tragetTable + " B  ");
			sql.append(" LEFT JOIN (SELECT CUST_ID, CUST_NAME FROM TBPMS_CUST_REC_N WHERE LAST_DAY(TO_DATE(:ym,'YYYYMM')) BETWEEN START_TIME AND END_TIME ) A ");
			sql.append(" ON A.CUST_ID=B.CUST_ID ");
			sql.append(" AND CUST_NAME=A.CUST_NAME  WHERE 1=1 ");
				//保險跟投資MTD日期切點不一樣，26號後保險資料算下個月的。
//				if(cal.get(Calendar.DATE)<=25){
				sql.append(" and TX_YM = :ym ");
				
//				}															
//				else{
//				sql.append(" and ( "   );
//				sql.append("		 (ITEM IN ('01','02','03','04','05','06','17') and TX_YM = :ym ) or ");
//				sql.append("		 (ITEM IN ('10','11','12','16') and TX_YM =TO_CHAR(ADD_MONTHS(to_date(:ym,'YYYYMM'), 1),'YYYYMM') )  ");
//				sql.append("      )  "  );
//				}
				sql.append(" and B.BRANCH_NBR = :branch_nbr ");
			sql.append(" GROUP BY item, B.CUST_ID,  CUST_NAME ,AO_CODE) ");
		}
		
		sql.append(" PIVOT( sum(nvl(fee,0)) ");
		sql.append(" FOR item IN ('01' AS MFD,'02' AS SI,'03' AS SN ,'04' AS DCI , '05' AS BND ,'17' AS STOCK ,'06' AS ETF , ");   //'09' AS EX_GAIN_LOSS 畫面匯兌拿掉
		sql.append(" '10' AS WHO_PAY , '11' AS SHORT_YEAR , '12' AS LONG_YEAR , '16' AS INVEST) ) ");
		sql.append(" WHERE 1=1 " );
		sql.append(" and AO_CODE = :ao_code ");
		sql.append(" ORDER BY CUST_ID ");
		queryCondition.setQueryString(sql.toString());
		queryCondition.setObject("ao_code", inputVO.getAo_code());
		queryCondition.setObject("branch_nbr" ,inputVO.getBranch_nbr());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		outputVO.setResultList(list);
		
		this.sendRtnObject(outputVO);
	}

	/* === 產出CSV==== */
	public void export(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS302OutputVO outputVO = (PMS302OutputVO) body;
		String type = "";
		if (outputVO.getType().equals("MAST"))
			type = "主檔_";
		else
			type = "明細檔_";
		List<Map<String, Object>> list = outputVO.getTotalList();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String fileName = "理專即時速報" + type + sdf.format(new Date()) + ".csv";
		List listCSV = new ArrayList();
		for (Map<String, Object> map : list) {
			String[] records = null;
			int i = 0;
				records = new String[11];
				records[i] = sdf2.format(new Date());
				records[++i] = checkIsNull(map, "CENTER_NAME");
				records[++i] = checkIsNull(map, "AREA_NAME");
				records[++i] = Format2(map, "BRA_NBR");
				records[++i] = checkIsNull(map, "BRANCH_NAME");
				records[++i] = Format2(map, "AO_CODE");
				
//				records[++i] = currencyFormat(map, "INV_ACT");
//				records[++i] = currencyFormat(map, "INSU_ACT");
//				double INS_ACT = checkIsNulltoDouble(map, "INV_ACT" ) + checkIsNulltoDouble(map, "INSU_ACT");
//				if(checkIsNulltoDouble(map, "INV_ACT" )==0 || checkIsNulltoDouble(map, "INSU_ACT")==0)
//					INS_ACT = 0.00;
//				records[++i] = currencyFormatD(INS_ACT);
//				records[++i] = currencyFormat(map, "INS_TAR");
//				double INS_RATE = INS_ACT/checkIsNulltoDouble(map, "INS_TAR")*100;
//				if(INS_ACT == 0.00)
//					INS_RATE = 0.00;
//				records[++i] = pcntFormat(INS_RATE)+"%";
				
				records[++i] = currencyFormat(map, "INV_FEE");
				records[++i] = currencyFormat(map, "INS_FEE");
//				double D_INS_ACT = checkIsNulltoDouble(map, "D_INV_ACT") + checkIsNulltoDouble(map, "D_INSU_ACT");
//				if(checkIsNulltoDouble(map, "D_INV_ACT")==0 || checkIsNulltoDouble(map, "D_INSU_ACT")==0)
//					 D_INS_ACT = 0.00 ;
				records[++i] = currencyFormat(map, "TOT_FEE");
				records[++i] = currencyFormat(map, "TOT_TAR");
//				double D_INS_RATE = D_INS_ACT/checkIsNulltoDouble(map, "D_INS_TAR")*100;
//				if(D_INS_ACT == 0.00)
//					D_INS_RATE = 0.00;
				records[++i] = pcntFormat(map, "TOT_DAY_RATE");
			listCSV.add(records);
		}
		// header
		String[] csvHeader = null;
		int j = 0;
			csvHeader = new String[11];
			csvHeader[j] = "日期";
			csvHeader[++j] = "業務處";
			csvHeader[++j] = "營運區";
			csvHeader[++j] = "分行代碼";
			csvHeader[++j] = "分行";
			csvHeader[++j] = "AO Code";
//			csvHeader[++j] = "投資實績(E)";
//			csvHeader[++j] = "保險實績(F)";
//			csvHeader[++j] = "投保實績(A)";
//			csvHeader[++j] = "投保目標(B)";
//			csvHeader[++j] = "達成率(A/B)";
			csvHeader[++j] = "日終-投資實績(E)";
			csvHeader[++j] = "日終-保險實績(F)";
			csvHeader[++j] = "日終-投保實績(A)";
			csvHeader[++j] = "日終-投保目標(B)";
			csvHeader[++j] = "日終-達成率(A/B)";

		CSVUtil csv = new CSVUtil();
		csv.setHeader(csvHeader);
		csv.addRecordList(listCSV);
		String url = csv.generateCSV();
		notifyClientToDownloadFile(url, fileName);
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
	private double checkIsNulltoDouble(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key)))
				&& map.get(key) != null) {
			return (double) map.get(key);
		} else {
			return 0.00;
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
	private String currencyFormatD(double value){
		NumberFormat nf = NumberFormat.getCurrencyInstance();
		return nf.format(value);
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
	
	private String pcntFormat(double value) {
		NumberFormat nf = NumberFormat.getInstance();
			return nf.format(value);
	}

	/**
	 * 查詢客戶各項商品銷量
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void queryCustDetail(Object body, IPrimitiveMap header) throws JBranchException {
		PMS302InputdetailVO inputVO = (PMS302InputdetailVO)body;
		String sql =new StringBuffer()
						.append("select * from TBPMS_CUST_IMM_PROFIT_DETAIL where CUST_ID = :custId ")
						.append("#srchCondition")
						.toString();
		StringBuffer srchCondition = new StringBuffer();
		if ( inputVO.getSrchType().equals("NOW")) {
			srchCondition.append("and DATA_DATE = :dataDate ");
		} else {
			srchCondition.append("and ((                                                           ")
		     			 // 投資商品日期範圍為 : 輸入月份月初到月底
						 .append("	  ITEM_TYPE in ('01', '02', '03', '04', '05', '17', '06') and ")
						 .append("	  substr(DATA_DATE, 0, 6) = :dataDate )                       ")                                                   
						 .append("or (                                                            ")
						 // 保險商品日期範圍為 : 輸入月份前一個月26日到 輸入月份 25日
						 .append("    ITEM_TYPE in ('10', '11', '12', '16') and ")
						 .append("    DATA_DATE between to_char(add_months(to_date(:dataDate, 'yyyymm'), -1), 'yyyymm') || '26' and :dataDate || '25')) ");
		}
		
		sql = sql.replace("#srchCondition", srchCondition.toString());
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		condition.setQueryString(sql);
		condition.setObject("custId", inputVO.getCust_id());
		condition.setObject("dataDate", inputVO.getTx_ym());

		PMS302OutputVO outputVO = new PMS302OutputVO();
		outputVO.setResultList(dam.exeQuery(condition));
		this.sendRtnObject(outputVO);
	}
}