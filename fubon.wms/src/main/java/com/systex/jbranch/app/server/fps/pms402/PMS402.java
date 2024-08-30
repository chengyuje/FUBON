package com.systex.jbranch.app.server.fps.pms402;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBPMS_MONTHLY_SALES_DEPPK;
import com.systex.jbranch.app.common.fps.table.TBPMS_MONTHLY_SALES_DEPVO;
import com.systex.jbranch.app.server.fps.pms000.PMS000;
import com.systex.jbranch.app.server.fps.pms000.PMS000InputVO;
import com.systex.jbranch.app.server.fps.pms000.PMS000OutputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * Copy Right Information :<br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description :業務人員當月存款異動明細表<br>
 * Comments Name : PMS402.java<br>
 * Author : Frank<br>
 * Date :2016/05/17 <br>
 * Version : 1.0 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年01月30日<br>
 */

@Component("pms402")
@Scope("request")
public class PMS402 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PMS402.class);

	/** 查詢資料 
	 * @throws ParseException **/
	public void queryData(Object body, IPrimitiveMap header)
			throws JBranchException, ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
		PMS402InputVO inputVO = (PMS402InputVO) body;
		PMS402OutputVO outputVO = new PMS402OutputVO();
		DataAccessManager dam = this.getDataAccessManager();
		String roleID = (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> fcMap = xmlInfo.doGetVariable("FUBONSYS.FC_ROLE",FormatHelper.FORMAT_2); // 理專
		Map<String, String> psopMap = xmlInfo.doGetVariable("FUBONSYS.PSOP_ROLE", FormatHelper.FORMAT_2); // OP
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); // 總行人員

		// 取得查詢資料可視範圍
		PMS000 pms000 = (PMS000) PlatformContext.getBean("pms000");
		PMS000InputVO pms000InputVO = new PMS000InputVO();
		pms000InputVO.setReportDate(inputVO.getsCreDate());
		PMS000OutputVO pms000outputVO = pms000.getOrg(pms000InputVO);
		
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		ArrayList<String> sql_list = new ArrayList<String>();
		StringBuffer sql = new StringBuffer();
		// 主查詢
		// 20170912:前端不需卡30W~50W
		// 20170921:移除Disable_FLAG處理
		sql.append(" select ROWNUM, t.* from (  select dep.DATA_YEARMON, to_char(dep.TRADE_DATE, 'YYYY/MM/DD') as TRADE_DATES,dep.TRADE_DATE,     ");
		sql.append("   dep.BRANCH_NBR,b.BRANCH_NAME, dep.ACC_NBR, dep.ID, dep.NAME,                          ");
		sql.append("   dep.EMP_ID, dep.AO_CODE, dep.CRCY_TYPE, dep.AMT_ORGD, dep.AMT_NTD,                      ");
		sql.append("   dep.MODIFIER, dep.SUPERVISOR_FLAG, dep.NOTE,                                                                   ");
		sql.append("   case when role.PARAM_NAME IN('001','004') then 'OP'         ");
		sql.append("        when role.PARAM_NAME IN('002','003','005','006','007','008') then 'BR' end as ROLE_FLAG ,         ");
		sql.append("   to_char(dep.LASTUPDATE, 'YYYY/MM/DD HH:MI:SS AM') as LASTUPDATE, ");
		//sql.append("   case when dep.DATA_YEARMON is not null then 'Y' else 'N' end as Disable_FLAG , ");
		sql.append("   dep.CREATETIME AS CDATE               ");
		sql.append("   from TBPMS_MONTHLY_SALES_DEP dep                                                ");
		sql.append("   left join TBPMS_ORG_REC_N b                                                     ");
		sql.append("     on dep.BRANCH_NBR = b.DEPT_ID                                                 ");
		sql.append("       and dep.TRADE_DATE between b.START_TIME and b.END_TIME                      ");
		sql.append(" left join TBPMS_EMPLOYEE_REC_N emp       ");
		sql.append(" ON dep.ID =emp.CUST_ID and dep.BRANCH_NBR = emp.DEPT_ID         ");
		sql.append(" and dep.TRADE_DATE between emp.START_TIME and emp.END_TIME        ");
		sql.append(" and (emp.PS_FLAG = 'Y' OR emp.AO_JOB_RANK LIKE ('%F%')OR emp.ROLE_ID IN (select PARAM_CODE from TBSYSPARAMETER where PARAM_TYPE ='BTPMS401.PERSON_ROLE'))        ");
		sql.append(" left join (select * from TBSYSPARAMETER where PARAM_TYPE ='BTPMS401.PERSON_ROLE') role     ");
		sql.append(" ON emp.role_id =role.PARAM_CODE          ");
		
		//sql.append("   where  dep.AMT_NTD BETWEEN  300000 AND 500000 AND  1=1                                                                    ");
		sql.append("     WHERE 1=1 ");
//		if (inputVO.getsCreDate() != null && !"".equals(inputVO.getsCreDate())) {
//			sql.append("and dep.DATA_YEARMON = ? ");
//			sql_list.add(inputVO.getsCreDate());
//		}
		if (inputVO.getsCreDate() != null && !"".equals(inputVO.getsCreDate())) {
			sql.append("and dep.DATA_YEARMON = :yearmon ");
			condition.setObject("yearmon", inputVO.getsCreDate());
		}
		
		// by Willis 20171019 此條件因為發現組織換區有異動(例如:東寧分行在正式環境10/1從西台南區換至東台南區)，跟之前組織對應會有問題，改為對應目前最新組織分行別
		// 分行
		if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
			sql.append("and dep.BRANCH_NBR = :BRNCH_NBRR ");
			condition.setObject("BRNCH_NBRR", inputVO.getBranch_nbr());
		// 營運區	
		}else if (StringUtils.isNotBlank(inputVO.getBranch_area_id())){
			sql.append("and dep.BRANCH_NBR in ( ");
			sql.append("select BRANCH_NBR from VWORG_DEFN_BRH where DEPT_ID = :BRANCH_AREA_IDD ) ");
			condition.setObject("BRANCH_AREA_IDD", inputVO.getBranch_area_id());
	    // 區域中心	
		}else if (StringUtils.isNotBlank(inputVO.getRegion_center_id())){
			sql.append("and dep.BRANCH_NBR in ( ");
			sql.append("select BRANCH_NBR from VWORG_DEFN_BRH where DEPT_ID = :REGION_CENTER_IDD ) ");
			condition.setObject("REGION_CENTER_IDD", inputVO.getRegion_center_id());
		}
		// 需求變更-新稱職務欄位 by 2017/11/27-Willis
		if (StringUtils.isNotBlank(inputVO.getPerson_role())) {
			sql.append("and role.PARAM_NAME = :PERSON_ROLE ");
			condition.setObject("PERSON_ROLE", inputVO.getPerson_role());
		}
		
/*	   	by willis 20171019 註解 改為對應目前最新組織分行別
		// 區域中心
		if (inputVO.getRegion_center_id() != null && !inputVO.getRegion_center_id().equals("")) {
			sql.append("and b.REGION_CENTER_ID = ? ");
			sql_list.add(inputVO.getRegion_center_id());
		}
		if (StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
			sql.append(" AND b.REGION_CENTER_ID = :REGION_CENTER_IDD ");
			condition.setObject("REGION_CENTER_IDD",inputVO.getRegion_center_id());
		} else {
			// 登入非總行人員強制加區域中心
			if (!headmgrMap.containsKey(roleID)) {
				sql.append("and b.REGION_CENTER_ID IN (:REGION_CENTER_IDD) ");
				condition.setObject("REGION_CENTER_IDD",pms000outputVO.getV_regionList());
			}
		}
		
		// 營運區
		if (inputVO.getBranch_area_id() != null && !inputVO.getBranch_area_id().equals("")) {
			sql.append("and b.BRANCH_AREA_ID = ? ");
			sql_list.add(inputVO.getBranch_area_id());
		}
		if (StringUtils.isNotBlank(inputVO.getBranch_area_id())) {
			sql.append(" AND b.BRANCH_AREA_ID = :BRANCH_AREA_IDD ");
			condition.setObject("BRANCH_AREA_IDD", inputVO.getBranch_area_id());
		} else {
			// 登入非總行人員強制加營運區
			if (!headmgrMap.containsKey(roleID)) {
				sql.append("and b.BRANCH_AREA_ID IN (:BRANCH_AREA_IDD) ");
				condition.setObject("BRANCH_AREA_IDD",pms000outputVO.getV_areaList());
			}
		}
		
		// 分行
		if (inputVO.getBranch_nbr() != null && !inputVO.getBranch_nbr().equals("")) {
			sql.append("and dep.BRANCH_NBR = ? ");
			sql_list.add(inputVO.getBranch_nbr());
		}
		if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
			sql.append(" AND dep.BRANCH_NBR LIKE :BRNCH_NBRR ");
			condition.setObject("BRNCH_NBRR", inputVO.getBranch_nbr());
		} else {
			// 登入非總行人員強制加分行
			if (!headmgrMap.containsKey(roleID)) {
				sql.append("and dep.BRANCH_NBR IN (:BRNCH_NBRR) ");
				condition.setObject("BRNCH_NBRR",pms000outputVO.getV_branchList());
			}
		}
*/		
		
/* 因前面畫面已拿掉此欄位，故後面不須此條件 by willis 20171019 註解 			
//		// 理專員編
//		if (inputVO.getAo_code() != null && !inputVO.getAo_code().equals("")) {
//			sql.append("and dep.AO_CODE = ? ");
//			sql_list.add(inputVO.getAo_code());
//		}
	
		if (StringUtils.isNotBlank(inputVO.getAo_code())) {
			sql.append(" AND dep.AO_CODE LIKE :AO_CODEE ");
			condition.setObject("AO_CODEE", inputVO.getAo_code());
		} else {
			// 登入為銷售人員強制加AO_CODE
			if(!"A157".equals(roleID)){
				if (fcMap.containsKey(roleID) || psopMap.containsKey(roleID)) {
					sql.append(" and dep.AO_CODE IN (:AO_CODEE) ");
					condition.setObject("AO_CODEE", pms000outputVO.getV_aoList());
				}
			}
		}
*/		
		// 排序
		sql.append(" order by dep.TRADE_DATE, dep.BRANCH_NBR, dep.EMP_ID, dep.ACC_NBR) t ");

		condition.setQueryString(sql.toString());
		for (int sql_i = 0; sql_i < sql_list.size(); sql_i++) {
			condition.setString(sql_i + 1, sql_list.get(sql_i));
		}
		ResultIF list = dam.executePaging(condition,inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
		outputVO.setTotalList(dam.exeQuery(condition));
		if (list.size() > 0) {
			int totalPage = list.getTotalPage();
			outputVO.setTotalPage(totalPage);
			outputVO.setResultList(list); // 主查詢資訊
			outputVO.setTotalRecord(list.getTotalRecord());
			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());
			sendRtnObject(outputVO);
		} else {
			throw new APException("ehl_01_common_009");
		}
	}

	/* ==== 【儲存】更新資料 ========修正by Kevin */
	public void save(Object body, IPrimitiveMap header) throws JBranchException {
		Timestamp stamp = new Timestamp(System.currentTimeMillis());
		PMS402InputVO inputVO = (PMS402InputVO) body;
		DataAccessManager dam = this.getDataAccessManager();
		for (Map<String, Object> map : inputVO.getList()) { // 資料修改後
			if (map.get("NOTE") == null)
				map.put("NOTE", "");
			if (map.get("SUPERVISOR_FLAG") == null)   //2/14外加
				map.put("SUPERVISOR_FLAG", "");
			for (Map<String, Object> map2 : inputVO.getList2()) { // 資料修改前
				if (map2.get("NOTE") == null)
					map2.put("NOTE", "");
				if (map2.get("SUPERVISOR_FLAG") == null)
					map2.put("SUPERVISOR_FLAG", "");
				if (map.get("TRADE_DATE").equals(map2.get("TRADE_DATE"))
						&& map.get("ID").equals(map2.get("ID"))
						&& map.get("DATA_YEARMON").equals(
								map2.get("DATA_YEARMON"))
						&& map.get("CRCY_TYPE").equals(map2.get("CRCY_TYPE"))
						&& map.get("BRANCH_NBR").equals(map2.get("BRANCH_NBR"))
						&& map.get("ACC_NBR").equals(map2.get("ACC_NBR"))
						&& (!map.get("SUPERVISOR_FLAG").equals(
								map2.get("SUPERVISOR_FLAG")) || !map
								.get("NOTE").equals(map2.get("NOTE")))) {

					TBPMS_MONTHLY_SALES_DEPPK pk = new TBPMS_MONTHLY_SALES_DEPPK();
					pk.setACC_NBR(map.get("ACC_NBR") + "");
					pk.setBRANCH_NBR(map.get("BRANCH_NBR") + "");
					pk.setCRCY_TYPE(map.get("CRCY_TYPE") + "");
					pk.setDATA_YEARMON(map.get("DATA_YEARMON") + "");
					pk.setID(map.get("ID") + "");
					pk.setTRADE_DATE(Timestamp.valueOf(map.get("TRADE_DATE")
							+ ""));
					TBPMS_MONTHLY_SALES_DEPVO paramVO = (TBPMS_MONTHLY_SALES_DEPVO) dam
							.findByPKey(TBPMS_MONTHLY_SALES_DEPVO.TABLE_UID, pk);
					if (map.get("SUPERVISOR_FLAG") != null)
						paramVO.setSUPERVISOR_FLAG(map.get("SUPERVISOR_FLAG")
								+ "");
					paramVO.setNOTE(map.get("NOTE") + "");
					paramVO.setModifier("test");
					paramVO.setLastupdate(stamp);
					dam.update(paramVO);
				}
			}
		}
		sendRtnObject(null);
	}

	/* === 產出Excel==== */
	public void export(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS402OutputVO outputVO = (PMS402OutputVO) body;
		List<Map<String, Object>> list = outputVO.getTotalList();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String fileName = "分行人員當月存款異動明細表_" + sdf.format(new Date()) + ".csv";
		List listCSV = new ArrayList();
		if (list.size() == 0) {
			String[] records = new String[2];
			records[0] = "查無資料";
			listCSV.add(records);
		} else {
			for (Map<String, Object> map : list) {
				String[] records = new String[17];
				int i = 0;
//				records[i] = checkIsNull(map, "ROWNUM").substring(0, 1); // 序號
				records[i] =((int)Double.parseDouble(checkIsNull(map, "ROWNUM").toString()))+""; // 序號 - 去小數點
				records[++i] = checkIsNull(map, "DATA_YEARMON"); // 資料年月
				records[++i] = checkIsNull(map, "TRADE_DATE"); // 交易日期
				records[++i] = checkIsNull(map, "BRANCH_NBR"); // 專員所屬分行代碼
				records[++i] = checkIsNull(map, "BRANCH_NAME"); // 專員所屬分行
				records[++i] = "=\""+checkIsNull(map, "ACC_NBR")+"\""; // 帳號   by 20180123-willis 轉成文字格式  問題單:4155
				records[++i] = checkIsNull(map, "ID"); // 行員身分證字號
				records[++i] = checkIsNull(map, "NAME"); // 行員姓名
				records[++i] = checkIsNull(map, "EMP_ID"); // 理專員編
				records[++i] = checkIsNull(map, "AO_CODE"); // AO Code
				records[++i] = checkIsNull(map, "CRCY_TYPE"); // 幣別
				records[++i] = currencyFormat(map, "AMT_ORGD"); // 原幣金額
				records[++i] = currencyFormat(map, "AMT_NTD"); // 交易金額(台幣)
				records[++i] = checkIsNull(map, "SUPERVISOR_FLAG"); // 主管確認
				records[++i] = checkIsNull(map, "NOTE"); // 主管備註欄
				records[++i] = checkIsNull(map, "MODIFIER"); // 最新異動人員
				records[++i] = checkIsNull(map, "LASTUPDATE"); // 最新異動日期
				listCSV.add(records);
			}
		}

		// header
		String[] csvHeader = new String[17];
		int j = 0;
		csvHeader[j] = "序號";
		csvHeader[++j] = "資料年月";
		csvHeader[++j] = "交易日期";
		csvHeader[++j] = "所屬分行ID";
		csvHeader[++j] = "所屬分行名稱";
		csvHeader[++j] = "帳號";
		csvHeader[++j] = "行員身分證字號";
		csvHeader[++j] = "行員姓名";
		csvHeader[++j] = "理專員編";
		csvHeader[++j] = "AO Code";
		csvHeader[++j] = "幣別";
		csvHeader[++j] = "原幣金額";
		csvHeader[++j] = "交易金額(台幣)";
		csvHeader[++j] = "主管確認";
		csvHeader[++j] = "主管備註欄";
		csvHeader[++j] = "最新異動人員";
		csvHeader[++j] = "最新異動日期";

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

	// 處理貨幣格式
	private String currencyFormat(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key)))
				&& map.get(key) != null) {
			DecimalFormat df = new DecimalFormat("#,##0.00");
			return df.format(map.get(key));
		} else
			return "0.00";
	}

}