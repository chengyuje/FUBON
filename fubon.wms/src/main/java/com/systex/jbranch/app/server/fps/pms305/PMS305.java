package com.systex.jbranch.app.server.fps.pms305;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBPMS_DAILY_FIPETLPK;
import com.systex.jbranch.app.common.fps.table.TBPMS_DAILY_FIPETLVO;
import com.systex.jbranch.app.server.fps.pms000.PMS000;
import com.systex.jbranch.app.server.fps.pms000.PMS000InputVO;
import com.systex.jbranch.app.server.fps.pms000.PMS000OutputVO;
import com.systex.jbranch.app.server.fps.pms304.PMS304InputVO;
import com.systex.jbranch.app.server.fps.pms304.PMS304OutputVO;
import com.systex.jbranch.app.server.fps.pms305.PMS305InputVO;
import com.systex.jbranch.app.server.fps.pms305.PMS305OutputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.report.reportdata.ConfigAdapterIF;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;
/**
 * 
 * Copy Right Information : <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description :險種別統計Controller <br>
 * Comments Name : PMS305.java<br>
 * Author :Kevin<br>
 * Date :2016年07月28日 <br>
 * Version : 1.01 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年02月01日<br>
 */

@Component("pms305")
@Scope("request")
public class PMS305 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PMS305.class);
	SimpleDateFormat sdfYYYYMMDD = new SimpleDateFormat("yyyyMMdd");

	/****
	 * 
	 * 主查詢
	 * @param body
	 * @param header
	 * @throws Exception
	 *             
	 */
	public void queryData(Object body, IPrimitiveMap header)
			throws JBranchException {

		PMS305InputVO inputVO = (PMS305InputVO) body;
		PMS305OutputVO outputVO = new PMS305OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
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
			
			//==主查詢==
			sql.append("WITH INSID_BASE AS (SELECT DISTINCT INS_ID, INS_NAME FROM TBPMS_AO_DAY_INS_PRD ");
			sql.append("	WHERE YEARMON = (SELECT DISTINCT YEARMON FROM TBPMS_AO_DAY_INS_PRD WHERE DATA_DATE = :DATA_DATEE) ) ");
			sql.append("SELECT BASE.INS_ID, BASE.INS_NAME, ");
			sql.append("SUM(NVL(PRD.IV_MON_CNT, 0)) as 			IV_MON_CNT, ");
			sql.append("SUM(NVL(PRD.IV_MON_PREM_FULL, 0)) as 	IV_MON_PREM_FULL, ");
			sql.append("SUM(NVL(PRD.IV_MON_PREM_DIS, 0)) as 	IV_MON_PREM_DIS, ");
			sql.append("SUM(NVL(PRD.IV_MON_FEE_FULL, 0)) as　	IV_MON_FEE_FULL, ");
			sql.append("SUM(NVL(PRD.IV_MON_FEE_DIS, 0)) as 		IV_MON_FEE_DIS, ");
			sql.append("SUM(NVL(PRD.OT_MON_CNT, 0)) as 			OT_MON_CNT, ");
			sql.append("SUM(NVL(PRD.OT_MON_PREM_FULL, 0)) as 	OT_MON_PREM_FULL, ");
			sql.append("SUM(NVL(PRD.OT_MON_PREM_DIS, 0)) as 	OT_MON_PREM_DIS, ");
			sql.append("SUM(NVL(PRD.OT_MON_FEE_FULL, 0)) as 	OT_MON_FEE_FULL, ");
			sql.append("SUM(NVL(PRD.OT_MON_FEE_DIS, 0)) as 		OT_MON_FEE_DIS, ");
			sql.append("SUM(NVL(PRD.SY_MON_CNT, 0)) as 			SY_MON_CNT, ");
			sql.append("SUM(NVL(PRD.SY_MON_PREM_FULL, 0)) as 	SY_MON_PREM_FULL, ");
			sql.append("SUM(NVL(PRD.SY_MON_PREM_DIS, 0)) as 	SY_MON_PREM_DIS, ");
			sql.append("SUM(NVL(PRD.SY_MON_FEE_FULL, 0)) as 	SY_MON_FEE_FULL, ");
			sql.append("SUM(NVL(PRD.SY_MON_FEE_DIS, 0)) as 		SY_MON_FEE_DIS, ");
			sql.append("SUM(NVL(PRD.LY_MON_CNT, 0)) as 			LY_MON_CNT, ");
			sql.append("SUM(NVL(PRD.LY_MON_PREM_FULL, 0)) as 	LY_MON_PREM_FULL, ");
			sql.append("SUM(NVL(PRD.LY_MON_PREM_DIS, 0)) as 	LY_MON_PREM_DIS, ");
			sql.append("SUM(NVL(PRD.LY_MON_FEE_FULL, 0)) as 	LY_MON_FEE_FULL, ");
			sql.append("SUM(NVL(PRD.LY_MON_FEE_DIS, 0)) as 		LY_MON_FEE_DIS, ");
			sql.append("SUM(NVL(PRD.SUM_MON_CNT, 0)) as 		SUM_MON_CNT, ");
			sql.append("SUM(NVL(PRD.SUM_MON_PREM_FULL, 0)) as 	SUM_MON_PREM_FULL, ");
			sql.append("SUM(NVL(PRD.SUM_MON_PREM_DIS, 0)) as 	SUM_MON_PREM_DIS, ");
			sql.append("SUM(NVL(PRD.SUM_MON_FEE_FULL, 0)) as 	SUM_MON_FEE_FULL, ");
			sql.append("SUM(NVL(PRD.SUM_MON_FEE_DIS, 0)) as 	SUM_MON_FEE_DIS, ");
			sql.append("SUM(NVL(PRD.IV_DAY_CNT, 0)) as 			IV_DAY_CNT, ");
			sql.append("SUM(NVL(PRD.IV_DAY_PREM_FULL, 0)) as 	IV_DAY_PREM_FULL, ");
			sql.append("SUM(NVL(PRD.IV_DAY_PREM_DIS, 0)) as 	IV_DAY_PREM_DIS, ");
			sql.append("SUM(NVL(PRD.IV_DAY_FEE_FULL, 0)) as 	IV_DAY_FEE_FULL, ");
			sql.append("SUM(NVL(PRD.IV_DAY_FEE_DIS, 0)) as 		IV_DAY_FEE_DIS, ");
			sql.append("SUM(NVL(PRD.OT_DAY_CNT, 0)) as 			OT_DAY_CNT, ");
			sql.append("SUM(NVL(PRD.OT_DAY_PREM_FULL, 0)) as 	OT_DAY_PREM_FULL, ");
			sql.append("SUM(NVL(PRD.OT_DAY_PREM_DIS, 0)) as 	OT_DAY_PREM_DIS, ");
			sql.append("SUM(NVL(PRD.OT_DAY_FEE_FULL, 0)) as 	OT_DAY_FEE_FULL, ");
			sql.append("SUM(NVL(PRD.OT_DAY_FEE_DIS, 0)) as 		OT_DAY_FEE_DIS, ");
			sql.append("SUM(NVL(PRD.SY_DAY_CNT, 0)) as 			SY_DAY_CNT, ");
			sql.append("SUM(NVL(PRD.SY_DAY_PREM_FULL, 0)) as 	SY_DAY_PREM_FULL, ");
			sql.append("SUM(NVL(PRD.SY_DAY_PREM_DIS, 0)) as 	SY_DAY_PREM_DIS, ");
			sql.append("SUM(NVL(PRD.SY_DAY_FEE_FULL, 0)) as 	SY_DAY_FEE_FULL, ");
			sql.append("SUM(NVL(PRD.SY_DAY_FEE_DIS, 0)) as 		SY_DAY_FEE_DIS, ");
			sql.append("SUM(NVL(PRD.LY_DAY_CNT, 0)) as 			LY_DAY_CNT, ");
			sql.append("SUM(NVL(PRD.LY_DAY_PREM_FULL, 0)) as 	LY_DAY_PREM_FULL, ");
			sql.append("SUM(NVL(PRD.LY_DAY_PREM_DIS, 0)) as 	LY_DAY_PREM_DIS, ");
			sql.append("SUM(NVL(PRD.LY_DAY_FEE_FULL, 0)) as 	LY_DAY_FEE_FULL, ");
			sql.append("SUM(NVL(PRD.LY_DAY_FEE_DIS, 0)) as 		LY_DAY_FEE_DIS, ");
			sql.append("SUM(NVL(PRD.SUM_DAY_CNT, 0)) as 		SUM_DAY_CNT, ");
			sql.append("SUM(NVL(PRD.SUM_DAY_PREM_FULL, 0)) as 	SUM_DAY_PREM_FULL, ");
			sql.append("SUM(NVL(PRD.SUM_DAY_PREM_DIS, 0)) as 	SUM_DAY_PREM_DIS, ");
			sql.append("SUM(NVL(PRD.SUM_DAY_FEE_FULL, 0)) as 	SUM_DAY_FEE_FULL, ");
			sql.append("SUM(NVL(PRD.SUM_DAY_FEE_DIS, 0)) as 	SUM_DAY_FEE_DIS ");
			sql.append("FROM INSID_BASE BASE ");
			sql.append("LEFT OUTER JOIN TBPMS_AO_DAY_INS_PRD PRD ON PRD.INS_ID = BASE.INS_ID ");
			
			
			//==主查詢條件==
			//區域中心
			if (!StringUtils.isBlank(inputVO.getRegion_center_id())) {
				sql.append(" and REGION_CENTER_ID LIKE :REGION_CENTER_IDDD");
				condition.setObject("REGION_CENTER_IDDD", "%" + inputVO.getRegion_center_id() + "%");
			}else {
				// 登入非總行人員強制加區域中心
				if (!headmgrMap.containsKey(roleID)) {
					sql.append(" and REGION_CENTER_ID IN (:REGION_CENTER_IDDD) ");
					condition.setObject("REGION_CENTER_IDDD",pms000outputVO.getV_regionList());
				}
			}
			
			//營運區
			if (!StringUtils.isBlank(inputVO.getBranch_area_id())) {
				sql.append(" and BRANCH_AREA_ID LIKE :OP_AREA_IDDD");
				condition.setObject("OP_AREA_IDDD", "%" + inputVO.getBranch_area_id() + "%");
			}else {
				// 登入非總行人員強制加營運區
				if (!headmgrMap.containsKey(roleID)) {
					sql.append(" and BRANCH_AREA_ID IN (:OP_AREA_IDDD) ");
					condition.setObject("OP_AREA_IDDD",pms000outputVO.getV_areaList());
				}
			}
			
			//分行
			if (!StringUtils.isBlank(inputVO.getBranch_nbr())) {
				sql.append(" and BRANCH_NBR LIKE :BRANCH_NBRR");
				condition.setObject("BRANCH_NBRR", "%" + inputVO.getBranch_nbr() + "%");
			}else {
				// 登入非總行人員強制加分行
				if (!headmgrMap.containsKey(roleID)) {
					sql.append(" and BRANCH_NBR IN (:BRANCH_NBRR) ");
					condition.setObject("BRANCH_NBRR",pms000outputVO.getV_branchList());
				}
			}
			if (!StringUtils.isBlank(inputVO.getAo_code())) {
				sql.append(" and AO_CODE LIKE :EMP_IDEE");
				condition.setObject("EMP_IDEE", "%" + inputVO.getAo_code() + "%");
			}else {
				// 登入為銷售人員強制加AO_CODE
				if (fcMap.containsKey(roleID) || psopMap.containsKey(roleID)) {
					sql.append(" and AO_CODE IN (:EMP_IDEE) ");
					condition.setObject("EMP_IDEE", pms000outputVO.getV_aoList());
				}
			}
			
			//年月
			if (inputVO.getsCreDate() != null) {
				sql.append(" and DATA_DATE LIKE :DATA_DATEE");
				// 進行轉換
				String dateString = sdf.format(inputVO.getsCreDate());
				condition.setObject("DATA_DATEE", dateString);
			}
			
			sql.append(" WHERE 1=1 ");
			//險種
			if (!StringUtils.isBlank(inputVO.getINS_ID())) {
				sql.append(" and BASE.INS_ID LIKE :INS_IDD");
				condition.setObject("INS_IDD", "%" + inputVO.getINS_ID().toUpperCase() + "%");
			}
			//險種名稱
			if (!StringUtils.isBlank(inputVO.getINS_NAME())) {
				sql.append(" and BASE.INS_NAME LIKE :INS_NAMEE");
				condition.setObject("INS_NAMEE", "%" + inputVO.getINS_NAME() + "%");
			}
			//
			sql.append(" GROUP BY BASE.INS_ID, BASE.INS_NAME ");
			sql.append(" ORDER BY BASE.INS_ID ");
			
			condition.setQueryString(sql.toString());
			
						
			//分頁結果查詢
			ResultIF list = dam.executePaging(condition,
					inputVO.getCurrentPageIndex() + 1, 20); // 分頁用
			List<Map<String, Object>> csvList = dam.exeQuery(condition); // 匯出全部用
			
			QueryConditionIF condition2 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql2 = new StringBuffer();	
			//全行合計
			sql2.append(" WITH INSID_BASE AS (SELECT DISTINCT INS_ID, INS_NAME FROM TBPMS_AO_DAY_INS_PRD ");
			sql2.append(" 	WHERE YEARMON = (SELECT DISTINCT YEARMON FROM TBPMS_AO_DAY_INS_PRD WHERE DATA_DATE = :DATA_DATEE) ) ");
			sql2.append(" SELECT '' AS TOTAL, ");
			sql2.append("        SUM(NVL(PRD.IV_MON_CNT, 0))        AS 	IV_MON_CNT, ");
			sql2.append("        SUM(NVL(PRD.IV_MON_PREM_FULL, 0))  AS 	IV_MON_PREM_FULL, ");
			sql2.append("        SUM(NVL(PRD.IV_MON_PREM_DIS, 0))   AS 	IV_MON_PREM_DIS, ");
			sql2.append("        SUM(NVL(PRD.IV_MON_FEE_FULL, 0))   AS　	IV_MON_FEE_FULL, ");
			sql2.append("        SUM(NVL(PRD.IV_MON_FEE_DIS, 0))    AS 	IV_MON_FEE_DIS, ");
			sql2.append("        SUM(NVL(PRD.OT_MON_CNT, 0))        AS 	OT_MON_CNT, ");
			sql2.append("        SUM(NVL(PRD.OT_MON_PREM_FULL, 0))  AS 	OT_MON_PREM_FULL, ");
			sql2.append("        SUM(NVL(PRD.OT_MON_PREM_DIS, 0))   AS 	OT_MON_PREM_DIS, ");
			sql2.append("        SUM(NVL(PRD.OT_MON_FEE_FULL, 0))   AS 	OT_MON_FEE_FULL, ");
			sql2.append("        SUM(NVL(PRD.OT_MON_FEE_DIS, 0))    AS 	OT_MON_FEE_DIS, ");
			sql2.append("        SUM(NVL(PRD.SY_MON_CNT, 0))        AS 	SY_MON_CNT, ");
			sql2.append("        SUM(NVL(PRD.SY_MON_PREM_FULL, 0))  AS 	SY_MON_PREM_FULL, ");
			sql2.append("        SUM(NVL(PRD.SY_MON_PREM_DIS, 0))   AS 	SY_MON_PREM_DIS, ");
			sql2.append("        SUM(NVL(PRD.SY_MON_FEE_FULL, 0))   AS 	SY_MON_FEE_FULL, ");
			sql2.append("        SUM(NVL(PRD.SY_MON_FEE_DIS, 0))    AS 	SY_MON_FEE_DIS, ");
			sql2.append("        SUM(NVL(PRD.LY_MON_CNT, 0))        AS 	LY_MON_CNT, ");
			sql2.append("        SUM(NVL(PRD.LY_MON_PREM_FULL, 0))  AS 	LY_MON_PREM_FULL, ");
			sql2.append("        SUM(NVL(PRD.LY_MON_PREM_DIS, 0))   AS 	LY_MON_PREM_DIS, ");
			sql2.append("        SUM(NVL(PRD.LY_MON_FEE_FULL, 0))   AS 	LY_MON_FEE_FULL, ");
			sql2.append("        SUM(NVL(PRD.LY_MON_FEE_DIS, 0))    AS 	LY_MON_FEE_DIS, ");
			sql2.append("        SUM(NVL(PRD.SUM_MON_CNT, 0))       AS 	SUM_MON_CNT, ");
			sql2.append("        SUM(NVL(PRD.SUM_MON_PREM_FULL, 0)) AS 	SUM_MON_PREM_FULL, ");
			sql2.append("        SUM(NVL(PRD.SUM_MON_PREM_DIS, 0))  AS 	SUM_MON_PREM_DIS, ");
			sql2.append("        SUM(NVL(PRD.SUM_MON_FEE_FULL, 0))  AS 	SUM_MON_FEE_FULL, ");
			sql2.append("        SUM(NVL(PRD.SUM_MON_FEE_DIS, 0))   AS 	SUM_MON_FEE_DIS, ");
			sql2.append("        SUM(NVL(PRD.IV_DAY_CNT, 0))        AS 	IV_DAY_CNT, ");
			sql2.append("        SUM(NVL(PRD.IV_DAY_PREM_FULL, 0))  AS 	IV_DAY_PREM_FULL, ");
			sql2.append("        SUM(NVL(PRD.IV_DAY_PREM_DIS, 0))   AS 	IV_DAY_PREM_DIS, ");
			sql2.append("        SUM(NVL(PRD.IV_DAY_FEE_FULL, 0))   AS 	IV_DAY_FEE_FULL, ");
			sql2.append("        SUM(NVL(PRD.IV_DAY_FEE_DIS, 0))    AS 	IV_DAY_FEE_DIS, ");
			sql2.append("        SUM(NVL(PRD.OT_DAY_CNT, 0))        AS 	OT_DAY_CNT, ");
			sql2.append("        SUM(NVL(PRD.OT_DAY_PREM_FULL, 0))  AS 	OT_DAY_PREM_FULL, ");
			sql2.append("        SUM(NVL(PRD.OT_DAY_PREM_DIS, 0))   AS 	OT_DAY_PREM_DIS, ");
			sql2.append("        SUM(NVL(PRD.OT_DAY_FEE_FULL, 0))   AS 	OT_DAY_FEE_FULL, ");
			sql2.append("        SUM(NVL(PRD.OT_DAY_FEE_DIS, 0))    AS	OT_DAY_FEE_DIS, ");
			sql2.append("        SUM(NVL(PRD.SY_DAY_CNT, 0))        AS 	SY_DAY_CNT, ");
			sql2.append("        SUM(NVL(PRD.SY_DAY_PREM_FULL, 0))  AS 	SY_DAY_PREM_FULL, ");
			sql2.append("        SUM(NVL(PRD.SY_DAY_PREM_DIS, 0))   AS 	SY_DAY_PREM_DIS, ");
			sql2.append("        SUM(NVL(PRD.SY_DAY_FEE_FULL, 0))   AS 	SY_DAY_FEE_FULL, ");
			sql2.append("        SUM(NVL(PRD.SY_DAY_FEE_DIS, 0))    AS 	SY_DAY_FEE_DIS, ");
			sql2.append("        SUM(NVL(PRD.LY_DAY_CNT, 0))        AS 	LY_DAY_CNT, ");
			sql2.append("        SUM(NVL(PRD.LY_DAY_PREM_FULL, 0))  AS 	LY_DAY_PREM_FULL, ");
			sql2.append("        SUM(NVL(PRD.LY_DAY_PREM_DIS, 0))   AS 	LY_DAY_PREM_DIS, ");
			sql2.append("        SUM(NVL(PRD.LY_DAY_FEE_FULL, 0))   AS 	LY_DAY_FEE_FULL, ");
			sql2.append("        SUM(NVL(PRD.LY_DAY_FEE_DIS, 0))    AS 	LY_DAY_FEE_DIS, ");
			sql2.append("        SUM(NVL(PRD.SUM_DAY_CNT, 0))       AS 	SUM_DAY_CNT, ");
			sql2.append("        SUM(NVL(PRD.SUM_DAY_PREM_FULL, 0)) AS 	SUM_DAY_PREM_FULL, ");
			sql2.append("        SUM(NVL(PRD.SUM_DAY_PREM_DIS, 0))  AS 	SUM_DAY_PREM_DIS, ");
			sql2.append("        SUM(NVL(PRD.SUM_DAY_FEE_FULL, 0))  AS 	SUM_DAY_FEE_FULL, ");
			sql2.append("        SUM(NVL(PRD.SUM_DAY_FEE_DIS, 0))   AS 	SUM_DAY_FEE_DIS ");
			sql2.append(" FROM INSID_BASE BASE ");
			sql2.append(" LEFT OUTER JOIN TBPMS_AO_DAY_INS_PRD PRD ON PRD.INS_ID = BASE.INS_ID ");
			
			if (inputVO.getsCreDate() != null) {
				// 進行轉換
				String dateString = sdf.format(inputVO.getsCreDate());
				condition2.setObject("DATA_DATEE", dateString);
			}
			
			//年月
			if (inputVO.getsCreDate() != null) {
				sql2.append(" AND PRD.DATA_DATE LIKE :DATA_DATEE");
				// 進行轉換
				String dateString = sdf.format(inputVO.getsCreDate());
				condition2.setObject("DATA_DATEE", dateString);
			}
			
			condition2.setQueryString(sql2.toString());
			List<Map<String, Object>> totalList = dam.exeQuery(condition2);
			
			
			outputVO.setCsvList(csvList);
			outputVO.setTotalList(totalList);
			int totalPage_i = list.getTotalPage(); // 分頁用
			int totalRecord_i = list.getTotalRecord(); // 分頁用
			outputVO.setResultList(list); // data
			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
			outputVO.setTotalPage(totalPage_i);// 總頁次
			outputVO.setTotalRecord(totalRecord_i);// 總筆數
			this.sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}

	}
	
	/** ==手收折數查詢== 
	 * @throws ParseException **/
	public void queryDiscount(Object body, IPrimitiveMap header)
			throws JBranchException, ParseException{
		PMS305InputVO inputVO = (PMS305InputVO) body;
		PMS305OutputVO outputVO = new PMS305OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		try{
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT PARAM_NAME_EDIT,PARAM_DESC ");
			sql.append(" FROM TBSYSPARAMETER ");
			sql.append(" WHERE PARAM_TYPE='PMS.INS_DISCOUNT' ");
			condition.setQueryString(sql.toString());
			
			List<Map<String, Object>> list = dam.exeQuery(condition);
			outputVO.setDiscountList(list);
			sendRtnObject(outputVO);
						
		}catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	/* ==== 查詢代碼資料 ======== */
	
	public void queryINS(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS305detailInputVO inputVO=(PMS305detailInputVO)body;
	    DataAccessManager dam=this.getDataAccessManager();
	    QueryConditionIF condition=dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
	    StringBuffer sql=new StringBuffer();
	    try {
			sql.append("SELECT ROWNUM AS NUM,T.* FROM(");
			sql.append("SELECT distinct  INSPRD_ID,INSPRD_NAME FROM TBPRD_INS_MAIN WHERE 1=1");
			
			if(StringUtils.isNotBlank(inputVO.getINS_ID()))
			{
				sql.append(" and INSPRD_ID LIKE :INS_IDD");
			}
			sql.append(" ORDER BY  INSPRD_ID)T");
			condition.setQueryString(sql.toString());
			if(StringUtils.isNotBlank(inputVO.getINS_ID()))
			{
				condition.setObject("INS_IDD","%"+inputVO.getINS_ID() +"%");
			}
			List<Map<String,Object>> list=dam.exeQuery(condition);
			PMS305OutputVO outputVO = new PMS305OutputVO();
			outputVO.setResultList(list);
			this.sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	    
	
	  }
	/* ==== 【儲存】更新資料 ======== */
	public void save(Object body, IPrimitiveMap header) throws JBranchException {
		Timestamp stamp = new Timestamp(System.currentTimeMillis());
		PMS305InputVO inputVO = (PMS305InputVO) body;
		DataAccessManager dam = this.getDataAccessManager();
		try {

			for (Map<String, Object> map : inputVO.getList()) {
				for (Map<String, Object> map2 : inputVO.getList2()) {
					if (map.get("DATA_DATE").equals(map2.get("DATA_DATE"))
							&& map.get("TXN_NO").equals(map2.get("TXN_NO"))
							&& (!map.get("NOTE").equals(map2.get("NOTE")))) {

						StringBuffer Sb = new StringBuffer(map.get("DATA_DATE")
								.toString());
						Sb.insert(4, "-");
						Sb.insert(7, "-");
						Sb.append("  00:00:00");
						// Timestamp ts = Timestamp.valueOf(Sb);
						TBPMS_DAILY_FIPETLPK PK = new TBPMS_DAILY_FIPETLPK();

						PK.setTXN_NO(map.get("TXN_NO").toString());
						PK.setDATA_DATE(map.get("DATA_DATE").toString());

						TBPMS_DAILY_FIPETLVO PARAMVO = (TBPMS_DAILY_FIPETLVO) dam
								.findByPKey(TBPMS_DAILY_FIPETLVO.TABLE_UID, PK);
						PARAMVO.setNOTE(map.get("NOTE").toString());
						PARAMVO.setModifier("test");
						PARAMVO.setLastupdate(stamp);

						dam.update(PARAMVO);
					}

				}
			}
			sendRtnObject(null);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
	/* === 產出Excel==== */
	public void export(Object body, IPrimitiveMap header) throws JBranchException {
		// 取得畫面資料
		PMS305OutputVO return_VO = (PMS305OutputVO) body;
		try {
			List<Map<String, Object>> list = return_VO.getList();
				
			Calendar busiday = Calendar.getInstance();  
	        busiday.setTime(new Date());  
	        int day = busiday.get(Calendar.DATE);  
	        
	        List<String> item = new ArrayList<String>();
			
	        item.add("INS_ID"); // 險種代碼
			item.add("INS_NAME"); // 險種名稱
			item.add("OT_DAY_CNT"); // 躉繳(當日)-件數
			item.add("OT_DAY_PREM_FULL"); // 躉繳(當日)-保費
			item.add("OT_DAY_FEE_FULL"); // 躉繳(當日)-手收100%
			item.add("OT_DAY_FEE_DIS"); // 躉繳(當日)-手收85%
			item.add("IV_DAY_CNT"); // 投資型(當日)-件數
			item.add("IV_DAY_PREM_FULL"); // 投資型(當日)-保費
			item.add("IV_DAY_FEE_FULL"); // 投資型(當日)-手收100%
			item.add("IV_DAY_FEE_DIS"); // 投資型(當日)-手收85%
			item.add("SY_DAY_CNT"); // 短年期繳(當日)-件數
			item.add("SY_DAY_PREM_FULL"); // 短年期繳(當日)-保費
			item.add("SY_DAY_FEE_FULL"); // 短年期繳(當日)-手收100%
			item.add("SY_DAY_FEE_DIS"); // 短年期繳(當日)-手收85%
			item.add("LY_DAY_CNT"); // 長年期繳(當日)-件數
			item.add("LY_DAY_PREM_FULL"); // 長年期繳(當日)-保費
			item.add("LY_DAY_FEE_FULL"); // 長年期繳(當日)-手收100%
			item.add("LY_DAY_FEE_DIS"); // 長年期繳(當日)-手收85%
			item.add("SUM_DAY_CNT"); // 小計(當日)-件數
			item.add("SUM_DAY_PREM_FULL"); // 小計(當日)-保費100%
			item.add("SUM_DAY_PREM_DIS"); // 小計(當日)-保費85%
			item.add("SUM_DAY_FEE_FULL"); // 小計(當日)-手收100%
			item.add("SUM_DAY_FEE_DIS"); // 小計(當日)-手收85%
			item.add("OT_MON_CNT"); // 躉繳(當月)-件數
			item.add("OT_MON_PREM_FULL"); // 躉繳(當月)-保費
			item.add("OT_MON_FEE_FULL"); // 躉繳(當月)-手收100%
			item.add("OT_MON_FEE_DIS"); // 躉繳(當月)-手收85%
			item.add("IV_MON_CNT"); // 投資型(當月)-件數
			item.add("IV_MON_PREM_FULL"); // 投資型(當月)-保費
			item.add("IV_MON_FEE_FULL"); // 投資型(當月)-手收100%
			item.add("IV_MON_FEE_DIS"); // 投資型(當月)-手收85%
			item.add("SY_MON_CNT"); // 短年期繳(當月)-件數
			item.add("SY_MON_PREM_FULL"); // 短年期繳(當月)-保費
			item.add("SY_MON_FEE_FULL"); // 短年期繳(當月)-手收100%
			item.add("SY_MON_FEE_DIS"); // 短年期繳(當月)-手收85%
			item.add("LY_MON_CNT"); // 長年期繳(當月)-件數
			item.add("LY_MON_PREM_FULL"); // 長年期繳(當月)-保費
			item.add("LY_MON_FEE_FULL"); // 長年期繳(當月)-手收100%
			item.add("LY_MON_FEE_DIS"); // 長年期繳(當月)-手收85%
			item.add("SUM_MON_CNT"); // 小計(當月)-件數
			item.add("SUM_MON_PREM_FULL"); // 小計(當月)-保費100%
			item.add("SUM_MON_PREM_DIS"); // 小計(當月)-保費85%
			item.add("SUM_MON_FEE_FULL"); // 小計(當月)-手收100%
			item.add("SUM_MON_FEE_DIS"); // 小計(當月)-手收85%
			

	    	String[] headerLine1 = {  
					"險種代號", 
					"險種名稱", 
					"躉繳(當日)",
					"躉繳(當日)", 
					"躉繳(當日)",
					"躉繳(當日)",
					"投資型(當日)",
					"投資型(當日)",
					"投資型(當日)",
					"投資型(當日)",
					"短年期繳(當日)", 
					"短年期繳(當日)", 
					"短年期繳(當日)",
					"短年期繳(當日)",
					"長年期繳(當日)", 
					"長年期繳(當日)", 
					"長年期繳(當日)", 
					"長年期繳(當日)",
					"小計(當日)", 
					"小計(當日)",
					"小計(當日)",
					"小計(當日)",
					"小計(當日)",
					"躉繳(當月)" ,
					"躉繳(當月)" ,
					"躉繳(當月)" ,
					"躉繳(當月)" ,
					"投資型(當月)" ,
					"投資型(當月)" ,
					"投資型(當月)" ,
					"投資型(當月)" ,
					"短年期繳(當月)" ,
					"短年期繳(當月)" ,
					"短年期繳(當月)" ,
					"短年期繳(當月)" ,
					"長年期繳(當月)" ,
					"長年期繳(當月)" ,
					"長年期繳(當月)" ,
					"長年期繳(當月)" ,
					"小計(當月)" ,
					"小計(當月)" ,
					"小計(當月)" ,
					"小計(當月)" ,
					"小計(當月)" 
			};
			
	    	dam = this.getDataAccessManager();
			QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			sql.append("select PARAM_CODE, PARAM_NAME_EDIT from TBSYSPARAMETER WHERE PARAM_TYPE = 'PMS.INS_DISCOUNT' ");
			condition.setQueryString(sql.toString());       
			List<Map<String,Object>> disList = dam.exeQuery(condition);
			
			int insDisInt = 0;
			int feeDisInt = 0;
			for (Map<String,Object> map : disList) {
				if (map.get("PARAM_CODE") != null && "1".equals(map.get("PARAM_CODE")) && map.get("PARAM_NAME_EDIT") != null)
					insDisInt = new BigDecimal(map.get("PARAM_NAME_EDIT").toString()).multiply(new BigDecimal(100)).intValue();				
				
				if (map.get("PARAM_CODE") != null && "2".equals(map.get("PARAM_CODE")) && map.get("PARAM_NAME_EDIT") != null)
					feeDisInt = new BigDecimal(map.get("PARAM_NAME_EDIT").toString()).multiply(new BigDecimal(100)).intValue();				
			}
			
			String insDis = Integer.toString(insDisInt) + "%";
			String feeDis = Integer.toString(feeDisInt) + "%"; 		
			
			String[] headerLine2 = { 
					"", "",
					"件數", "保費", "手收 100%", "手收  " + feeDis,
					"件數", "保費", "手收 100%", "手收  " + feeDis,
					"件數", "保費", "手收 100%", "手收  " + feeDis,
					"件數", "保費", "手收 100%", "手收  " + feeDis,
					"件數", "保費 100%", "保費 " + insDis, "手收 100%", "手收  " + feeDis,
					"件數", "保費", "手收 100%", "手收  " + feeDis,
					"件數", "保費", "手收 100%", "手收  " + feeDis,
					"件數", "保費", "手收 100%", "手收  " + feeDis,
					"件數", "保費", "手收 100%", "手收  " + feeDis, 
					"件數", "保費 100%", "保費 " + insDis, "手收 100%", "手收  " + feeDis
			};
			
			ConfigAdapterIF config = (ConfigAdapterIF) PlatformContext.getBean("configAdapter");	
			String fileName = "險種別統計_" + sdfYYYYMMDD.format(new Date())+ "-" + getUserVariable(FubonSystemVariableConsts.LOGINID) + ".xlsx";

			

				XSSFWorkbook workbook = new XSSFWorkbook();
				XSSFSheet sheet = workbook.createSheet("險種別統計_"+ sdfYYYYMMDD.format(new Date()));
				sheet.setDefaultColumnWidth(30);
				
				XSSFCellStyle style = workbook.createCellStyle();
				XSSFCellStyle STYLE = workbook.createCellStyle();

				style.setAlignment(XSSFCellStyle.ALIGN_CENTER); 				// 水平置中
				style.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER); 		// 垂直置中

				STYLE.setAlignment(XSSFCellStyle.ALIGN_CENTER); 				// 水平置中
				STYLE.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER); 		// 垂直置中
				STYLE.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
				STYLE.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
				STYLE.setBorderBottom((short) 1);
				STYLE.setBorderTop((short) 1);
				STYLE.setBorderLeft((short) 1);
				STYLE.setBorderRight((short) 1);
				Integer index = 0; // first row
				Integer startFlag = 0;
				Integer endFlag = 0;
				ArrayList<String> tempList = new ArrayList<String>(); // 比對用

				XSSFRow row = sheet.createRow(index);
				for (int i = 0; i < headerLine1.length; i++) {
					String headerLine = headerLine1[i];
					if (tempList.indexOf(headerLine) < 0) {
						tempList.add(headerLine);
						XSSFCell cell = row.createCell(i);
						cell.setCellStyle(STYLE);
						cell.setCellValue(headerLine1[i]);

						if (endFlag != 0) {
							sheet.addMergedRegion(new CellRangeAddress(0, 0, startFlag, endFlag)); // firstRow, endRow, firstColumn, endColumn
						}
						startFlag = i;
						endFlag = 0;
					} else {
						endFlag = i;
					}
				}
				if (endFlag != 0) { // 最後的CELL若需要合併儲存格，則在這裡做
					sheet.addMergedRegion(new CellRangeAddress(0, 0, startFlag, endFlag)); // firstRow, endRow, firstColumn, endColumn
				}

				index++; // next row
				row = sheet.createRow(index);
				for (int i = 0; i < headerLine2.length; i++) {
					XSSFCell cell = row.createCell(i);
					cell.setCellStyle(STYLE);
					cell.setCellValue(headerLine2[i]);

					if ("".equals(headerLine2[i])) {
						sheet.addMergedRegion(new CellRangeAddress(0, 1, i, i)); // firstRow, endRow, firstColumn, endColumn
					}
				}

				index++;
				List<Map<String, Object>> totalList = return_VO.getTotalList();
				
				int listCnt = 0;
				for (Map<String, Object> map : list) {
					listCnt++;
					
					Boolean changeArea = false;
										
					row = sheet.createRow(index);
					int j = 0;
					XSSFCell cell = row.createCell(j);
					
					
					for(String key : item) {
						cell = row.createCell(j++);
						cell.setCellStyle(style);
										
						cell.setCellValue(checkIsNull(map, key));					
					}
					
					index++;
					
					//最後一筆
					if (listCnt == list.size()) {
						row = sheet.createRow(index);
						int i = 0;
						XSSFCell centerTalCell = row.createCell(i);
						centerTalCell = row.createCell(i++);
						centerTalCell.setCellStyle(style);
						centerTalCell.setCellValue( "全行 合計");
						centerTalCell = row.createCell(i++);
						
						for (String key : item){
							if( key == "INS_ID" || key == "INS_NAME" ){
								continue;
							}else{
								centerTalCell = row.createCell(i++);
								centerTalCell.setCellStyle(style);
								
								BigDecimal value = new BigDecimal(0);
								if (totalList.get(0).get(key) != null ) {
									value = new BigDecimal(totalList.get(0).get(key).toString());
									centerTalCell.setCellValue(value.toPlainString());	
								}
									
							}						
						}
						sheet.addMergedRegion(new CellRangeAddress(index, index, 0, 1)); // firstRow, endRow, firstColumn, endColumn
					
					}
				}
				
		
				String tempName = UUID.randomUUID().toString();
				File f = new File(config.getServerHome(), config.getReportTemp() + tempName);
				workbook.write(new FileOutputStream(f)); //絕對路徑建檔
				notifyClientToDownloadFile(config.getReportTemp().substring(1) + tempName, fileName); //相對路徑取檔

		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("EXCIL無資料無法匯出!");
		}
	}

	/**
	 * 檢查Map取出欄位是否為Null
	 * 類型:數字
	 * @param map
	 * @return String
	 */
	private String checkIsNullI(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key)))
				&& map.get(key) != null) {
			return String.valueOf(map.get(key));
		} else {
			return "0";
		}
	}
	private String checkIsNull(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key)))) {
			if(map.get(key)==null)
			return "";
				return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}
	/**
	 * 檢查Map取出欄位是否為Null
	 * 類型:字串
	 * @param map
	 * @return String
	 */
	private String checkIsNullS(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key)))
				&& map.get(key) != null) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}

}