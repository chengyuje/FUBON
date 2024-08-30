package com.systex.jbranch.app.server.fps.pms330;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.pms000.PMS000;
import com.systex.jbranch.app.server.fps.pms000.PMS000InputVO;
import com.systex.jbranch.app.server.fps.pms000.PMS000OutputVO;
import com.systex.jbranch.app.server.fps.pms330.PMS330InputVO;
import com.systex.jbranch.app.server.fps.pms330.PMS330OutputVO;
import com.systex.jbranch.app.server.fps.pms354.PMS354OutputVO;
import com.systex.jbranch.app.server.fps.pms358.PMS358InputVO;
import com.systex.jbranch.app.server.fps.pms358.PMS358OutputVO;
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
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;
/**
 * 
 * Copy Right Information : <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description :理專各級客戶數增減 Controller <br>
 * Comments Name : PMS330.java<br>
 * Author :Kevin<br>
 * Date :2016年05月24日 <br>
 * Version : 1.01 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年02月01日<br>
 */

@Component("pms330")
@Scope("request")
public class PMS330 extends FubonWmsBizLogic {
	public DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PMS330.class);

	/**
	 * 主查詢
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void getBusiDay(Object body, IPrimitiveMap header) 
			throws JBranchException {
		PMS330InputVO inputVO = (PMS330InputVO) body;
		PMS330OutputVO outputVO = new PMS330OutputVO();
		dam = this.getDataAccessManager();
		
		/*********判斷工作天之凾式會強制減一天，因此在此之前先補上一天*********/
        Calendar busiday = Calendar.getInstance();  
        busiday.setTime(inputVO.getsCreDate());  
        int day = busiday.get(Calendar.DATE);  
        busiday.set(Calendar.DATE, day + 1);
        /*****************************************************/
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT PABTH_UTIL.FC_GETBUSIDAY( :Busiday , 'TWD' , -1 ) AS BUSIDAY FROM DUAL");
		condition.setObject("Busiday",busiday.getTime());
		condition.setQueryString(sql.toString());

		List list = dam.exeQuery(condition);
		outputVO.setResultList(list);
		this.sendRtnObject(outputVO);
	}
	public void queryData(Object body, IPrimitiveMap header)
			throws JBranchException, ParseException {
		PMS330InputVO inputVO = (PMS330InputVO) body;
		PMS330OutputVO outputVO = new PMS330OutputVO();
		
		String roleType = "";
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
		pms000InputVO.setReportDate(inputVO.getReportDate());
		PMS000OutputVO pms000outputVO = pms000.getOrg(pms000InputVO);
		
		dam = this.getDataAccessManager();

		QueryConditionIF condition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		try {
			//日期轉字串
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String times = "20160501";
			if (inputVO.getsCreDate() != null) {
				times = sdf.format(inputVO.getsCreDate());
			}
			//==主查詢==
			sql.append("SELECT "
					+ "   A1.* "
					+ "  ,A2.E_PERSON     AS    SE_PERSON "
					+ "  ,A2.I_PERSON     AS    SI_PERSON "
					+ "  ,A2.P_PERSON     AS    SP_PERSON "
					+ "  ,A2.O_PERSON     AS    SO_PERSON "
					+ "  ,A2.S_PERSON     AS    SS_PERSON "
					+ "  ,A2.Z_PERSON     AS    SZ_PERSON "
					+ "  ,A2.E_PLAT       AS    SE_PLAT "
					+ "  ,A2.I_PLAT       AS    SI_PLAT "
					+ "  ,A2.P_PLAT       AS    SP_PLAT "
					+ "  ,A2.O_PLAT       AS    SO_PLAT "
					+ "  ,A2.S_PLAT       AS    SS_PLAT "
					+ "  ,A2.Z_PLAT       AS    SZ_PLAT "
					+ "  ,A2.E_PRI        AS    SE_PRI "
					+ "  ,A2.I_PRI        AS    SI_PRI "
					+ "  ,A2.P_PRI        AS    SP_PRI "
					+ "  ,A2.O_PRI        AS    SO_PRI "
					+ "  ,A2.S_PRI        AS    SS_PRI "
					+ "  ,A2.Z_PRI        AS    SZ_PRI "
					+ "  ,A2.E_PERSON_C   AS    SE_PERSON_C "
					+ "  ,A2.I_PERSON_C   AS    SI_PERSON_C "
					+ "  ,A2.P_PERSON_C   AS    SP_PERSON_C "
					+ "  ,A2.O_PERSON_C   AS    SO_PERSON_C "
					+ "  ,A2.S_PERSON_C   AS    SS_PERSON_C "
					+ "  ,A2.Z_PERSON_C   AS    SZ_PERSON_C "
					+ "  ,A2.E_MASS       AS    SE_MASS "
					+ "  ,A2.I_MASS       AS    SI_MASS "
					+ "  ,A2.P_MASS       AS    SP_MASS "
					+ "  ,A2.O_MASS       AS    SO_MASS "
					+ "  ,A2.S_MASS       AS    SS_MASS "
					+ "  ,A2.Z_MASS       AS    SZ_MASS "
					+ "  ,(A1.E_PERSON-A2.E_PERSON)  AS  ESS_EP "
					+ "  ,(A1.I_PERSON-A2.I_PERSON)  AS  ESS_IP "
					+ "  ,(A1.P_PERSON-A2.P_PERSON)  AS  ESS_PP "
					+ "  ,(A1.O_PERSON-A2.O_PERSON)  AS  ESS_OP "
					+ "  ,(A1.S_PERSON-A2.S_PERSON)  AS  ESS_SP "
					+ "  ,(A1.Z_PERSON-A2.Z_PERSON)  AS  ESS_ZP "
					+ "  ,(A1.E_PLAT-A2.E_PLAT    )  AS  ESS_ET "
					+ "  ,(A1.I_PLAT-A2.I_PLAT    )  AS  ESS_IT "
					+ "  ,(A1.P_PLAT-A2.P_PLAT    )  AS  ESS_PT "
					+ "  ,(A1.O_PLAT-A2.O_PLAT    )  AS  ESS_OT "
					+ "  ,(A1.S_PLAT-A2.S_PLAT    )  AS  ESS_ST "
					+ "  ,(A1.Z_PLAT-A2.Z_PLAT    )  AS  ESS_ZT "
					+ "  ,(A1.E_PRI-A2.E_PRI      )  AS  ESS_EI "
					+ "  ,(A1.I_PRI-A2.I_PRI      )  AS  ESS_II "
					+ "  ,(A1.P_PRI-A2.P_PRI      )  AS  ESS_PI "
					+ "  ,(A1.O_PRI-A2.O_PRI      )  AS  ESS_OI "
					+ "  ,(A1.S_PRI-A2.S_PRI      )  AS  ESS_SI "
					+ "  ,(A1.Z_PRI-A2.Z_PRI      )  AS  ESS_ZI "
					+ "  ,(A1.E_PERSON_C-A2.E_PERSON_C)  AS  ESS_EC "
					+ "  ,(A1.I_PERSON_C-A2.I_PERSON_C)  AS  ESS_IC "
					+ "  ,(A1.P_PERSON_C-A2.P_PERSON_C)  AS  ESS_PC "
					+ "  ,(A1.O_PERSON_C-A2.O_PERSON_C)  AS  ESS_OC "
					+ "  ,(A1.S_PERSON_C-A2.S_PERSON_C)  AS  ESS_SC "
					+ "  ,(A1.Z_PERSON_C-A2.Z_PERSON_C)  AS  ESS_ZC "
					+ "  ,(A1.E_MASS-A2.E_MASS    )  AS  ESS_EM "
					+ "  ,(A1.I_MASS-A2.I_MASS    )  AS  ESS_IM "
					+ "  ,(A1.P_MASS-A2.P_MASS    )  AS  ESS_PM "
					+ "  ,(A1.O_MASS-A2.O_MASS    )  AS  ESS_OM "
					+ "  ,(A1.S_MASS-A2.S_MASS    )  AS  ESS_SM "
					+ "  ,(A1.Z_MASS-A2.Z_MASS    )  AS  ESS_ZM "
					+ "  FROM    "
					+ "  (select * from TBPMS_CUST_LEV where 1=1   and DATA_DATE=(SELECT MAX(DATA_DATE) FROM TBPMS_CUST_LEV)) A1,  "
					+ "  (select * from TBPMS_CUST_LEV where 1=1   and DATA_DATE='" + times + "') A2   "
					+ "  WHERE 1=1 AND A1.EMP_ID=A2.EMP_ID AND A1.AO_CODE=A2.AO_CODE  ");

			// ==查詢條件==
			//區域中心
			if (!StringUtils.isBlank(inputVO.getRegion_center_id())) {
				sql.append(" and A1.REGION_CENTER_ID LIKE :REGION_CENTER_IDDD ");
			}else{
				//登入非總行人員強制加區域中心
				if(!headmgrMap.containsKey(roleID)) {
					sql.append("and A1.REGION_CENTER_ID IN (:region_center_id) ");
					condition.setObject("region_center_id", pms000outputVO.getV_regionList());
				}
			}
			//營運區
			if (!StringUtils.isBlank(inputVO.getBranch_area_id())) {
				sql.append(" and A1.BRANCH_AREA_ID LIKE :BRANCH_AREA_IDDD ");
			}else{
				//登入非總行人員強制加營運區
				if(!headmgrMap.containsKey(roleID)) {
					sql.append("  and A1.BRANCH_AREA_ID IN (:branch_area_id) ");
					condition.setObject("branch_area_id", pms000outputVO.getV_areaList());
				}
			}
			//分行
			if (!StringUtils.isBlank(inputVO.getBranch_nbr())) {
				sql.append(" and A1.BRANCH_NBR LIKE :BRANCH_NBRR ");
			}else{
				//登入非總行人員強制加分行
				if(!headmgrMap.containsKey(roleID)) {		
					sql.append("  and A1.BRANCH_NBR IN (:branch_nbr) ");
					condition.setObject("branch_nbr", pms000outputVO.getV_branchList());
				}
			}
			//員編
			if (!StringUtils.isBlank(inputVO.getEmp_id())) {
				sql.append(" and A1.EMP_ID LIKE :EMP_IDEE ");
			}
			//理專
			if (!StringUtils.isBlank(inputVO.getAo_code())) {
				sql.append(" and A1.AO_CODE LIKE :AO_CODE ");
			}else{
				//登入為銷售人員強制加AO_CODE
				if(fcMap.containsKey(roleID) || psopMap.containsKey(roleID)) {
					sql.append(" and A1.AO_CODE IN (:ao_code) ");
					condition.setObject("ao_code", pms000outputVO.getV_aoList());
				}
			}
			
			sql.append(" order by A1.REGION_CENTER_ID,A1.BRANCH_AREA_NAME,A1.BRANCH_NBR,A1.EMP_ID ");
			
			condition.setQueryString(sql.toString());

			if (!StringUtils.isBlank(inputVO.getRegion_center_id())) {
				condition.setObject("REGION_CENTER_IDDD",
						"%" + inputVO.getRegion_center_id() + "%");
			}
			// ==查詢條件設定==
			//營運區
			if (!StringUtils.isBlank(inputVO.getBranch_area_id())) {
				condition.setObject("BRANCH_AREA_IDDD", "%" + inputVO.getBranch_area_id() + "%");
			}
			//分行
			if (!StringUtils.isBlank(inputVO.getBranch_nbr())) {
				condition.setObject("BRANCH_NBRR", "%" + inputVO.getBranch_nbr()
						+ "%");
			}
			//員編
			if (!StringUtils.isBlank(inputVO.getEmp_id())) {
				condition.setObject("EMP_IDEE", "%" + inputVO.getEmp_id() + "%");
			}
			//理專
			if (!StringUtils.isBlank(inputVO.getAo_code())) {
				condition.setObject("AO_CODE", "%" + inputVO.getAo_code() + "%");
			}
			
			// 分頁查詢結果
			ResultIF list = dam.executePaging(condition,
					inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
			
			//CSV 專用
			List<Map<String, Object>> csvList = dam.exeQuery(condition);
			int totalPage_i = list.getTotalPage(); // 分頁用
			int totalRecord_i = list.getTotalRecord(); // 分頁用
			outputVO.setResultList(list); // data 頁面
			outputVO.setCsvList(csvList);  //csv 專用 
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
	/* ==== 【查詢】建表日期 ======== */
	public void queryCDate(Object body, IPrimitiveMap header) throws JBranchException {
		PMS330InputVO inputVO = (PMS330InputVO) body;
		PMS330OutputVO outputVO = new PMS330OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT MAX(CREATETIME) AS CREATDATE FROM TBPMS_CUST_LEV WHERE 1=1 ");
		sql.append(" AND DATA_DATE LIKE :YEARMONN ");
		
		// 設定時間
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String CDate = sdf.format(inputVO.getsCreDate());
		condition.setObject("YEARMONN",CDate);
		
		condition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(condition);		
		outputVO.setResultList(list);
		this.sendRtnObject(outputVO);
	}
	
//	/**
//	 * 匯出
//	 * 
//	 * @param body
//	 * @param header
//	 * @throws JBranchException
//	 */
//	public void export(Object body, IPrimitiveMap header)
//			throws JBranchException {
//		// 取得畫面資料
//		PMS330OutputVO return_VO = (PMS330OutputVO) body;
//
//		List<Map<String, Object>> list = return_VO.getList();
//		try {
//			if (list.size() > 0) {
//				// gen csv
//				String.format("%1$,09d", -3123);
//				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//				String fileName = "理專各級客戶數增減" + sdf.format(new Date())
//						+ "-"+getUserVariable(FubonSystemVariableConsts.LOGINID)+ ".csv";
//				List listCSV = new ArrayList();
//				for (Map<String, Object> map : list) {
//					// 21 column
//					String[] records = new String[55];
//					int i = 0;
//					records[i] = checkIsNull(map, "DATA_DATE"); // 資料產生日期";
//					records[++i] = checkIsNullAndTrans(map, "REGION_CENTER_ID"); // 區域中心ID";
//					records[++i] = checkIsNull(map, "REGION_CENTER_NAME"); // 區域中心";
//					records[++i] = checkIsNullAndTrans(map, "BRANCH_AREA_ID"); // 營運區ID";
//					records[++i] = checkIsNull(map, "BRANCH_AREA_NAME"); // 營運區";
//					records[++i] = checkIsNullAndTrans(map, "BRANCH_NBR"); // 分行ID";
//					records[++i] = checkIsNull(map, "BRANCH_NAME"); // 分行";
//					records[++i] = checkIsNullAndTrans(map, "EMP_ID"); // 理專員編";
//					records[++i] = checkIsNull(map, "EMP_NAME"); // 理專姓名";
//					records[++i] = checkIsNullAndTrans(map, "AO_CODE"); // AO Code
//					records[++i] = checkIsNull(map, "E_PERSON"); // 個人-E
//					records[++i] = checkIsNull(map, "I_PERSON"); // 個人-I
//					records[++i] = checkIsNull(map, "P_PERSON"); // 個人-P
//					records[++i] = checkIsNull(map, "O_PERSON"); // 個人-O
//					records[++i] = checkIsNull(map, "S_PERSON"); // 個人-S
//					records[++i] = checkIsNull(map, "E_PLAT"); // 白金-E
//					records[++i] = checkIsNull(map, "I_PLAT"); // 白金-I
//					records[++i] = checkIsNull(map, "P_PLAT"); // 白金-P
//					records[++i] = checkIsNull(map, "O_PLAT"); // 白金-O
//					records[++i] = checkIsNull(map, "S_PLAT"); // 白金-S
//					records[++i] = checkIsNull(map, "E_PRI"); // 私人-E
//					records[++i] = checkIsNull(map, "I_PRI"); // 私人-I
//					records[++i] = checkIsNull(map, "P_PRI"); // 私人-P
//					records[++i] = checkIsNull(map, "O_PRI"); // 私人-O
//					records[++i] = checkIsNull(map, "S_PRI"); // 私人-S
//					records[++i] = checkIsNull(map, "SE_PERSON"); // 比較日個人-E
//					records[++i] = checkIsNull(map, "SI_PERSON"); // 比較日個人-I
//					records[++i] = checkIsNull(map, "SP_PERSON"); // 比較日個人-P
//					records[++i] = checkIsNull(map, "SO_PERSON"); // 比較日個人-O
//					records[++i] = checkIsNull(map, "SS_PERSON"); // 比較日個人-S
//					records[++i] = checkIsNull(map, "SE_PLAT"); // 比較日白金-E
//					records[++i] = checkIsNull(map, "SI_PLAT"); // 比較日白金-I
//					records[++i] = checkIsNull(map, "SP_PLAT"); // 比較日白金-P
//					records[++i] = checkIsNull(map, "SO_PLAT"); // 比較日白金-O
//					records[++i] = checkIsNull(map, "SS_PLAT"); // 比較日白金-S
//					records[++i] = checkIsNull(map, "SE_PRI"); // 比較日私人-E
//					records[++i] = checkIsNull(map, "SI_PRI"); // 比較日私人-I
//					records[++i] = checkIsNull(map, "SP_PRI"); // 比較日私人-P
//					records[++i] = checkIsNull(map, "SO_PRI"); // 比較日私人-O
//					records[++i] = checkIsNull(map, "SS_PRI"); // 比較日私人-S
//					records[++i] = checkIsNull(map, "ESS_EP"); // 差異比個人-E
//					records[++i] = checkIsNull(map, "ESS_IP"); // 差異比個人-I
//					records[++i] = checkIsNull(map, "ESS_PP"); // 差異比個人-P
//					records[++i] = checkIsNull(map, "ESS_OP"); // 差異比個人-O
//					records[++i] = checkIsNull(map, "ESS_SP"); // 差異比個人-S
//					records[++i] = checkIsNull(map, "ESS_ET"); // 差異比白金-E
//					records[++i] = checkIsNull(map, "ESS_IT"); // 差異比白金-I
//					records[++i] = checkIsNull(map, "ESS_PT"); // 差異比白金-P
//					records[++i] = checkIsNull(map, "ESS_OT"); // 差異比白金-O
//					records[++i] = checkIsNull(map, "ESS_ST"); // 差異比白金-S
//					records[++i] = checkIsNull(map, "ESS_EI"); // 差異比私人-E
//					records[++i] = checkIsNull(map, "ESS_II"); // 差異比私人-I
//					records[++i] = checkIsNull(map, "ESS_PI"); // 差異比私人-P
//					records[++i] = checkIsNull(map, "ESS_OI"); // 差異比私人-O
//					records[++i] = checkIsNull(map, "ESS_SI"); // 差異比私人-S
//				
//
//					listCSV.add(records);
//				}
//				// header
//				String[] csvHeader = new String[55];
//				int j = 0;
//				csvHeader[j] = "資料產生日期";
//				csvHeader[++j] = "業務處ID";
//				csvHeader[++j] = "業務處";
//				csvHeader[++j] = "營運區ID";
//				csvHeader[++j] = "營運區";
//				csvHeader[++j] = "分行ID";
//				csvHeader[++j] = "分行";
//				csvHeader[++j] = "理專員編";
//				csvHeader[++j] = "理專姓名";
//				csvHeader[++j] = "AO Code";
//				csvHeader[++j] = "個人-E";
//				csvHeader[++j] = "個人-I";
//				csvHeader[++j] = "個人-P";
//				csvHeader[++j] = "個人-O";
//				csvHeader[++j] = "個人-S";
//				csvHeader[++j] = "白金-E";
//				csvHeader[++j] = "白金-I";
//				csvHeader[++j] = "白金-P";
//				csvHeader[++j] = "白金-O";
//				csvHeader[++j] = "白金-S";
//				csvHeader[++j] = "私人-E";
//				csvHeader[++j] = "私人-I";
//				csvHeader[++j] = "私人-P";
//				csvHeader[++j] = "私人-O";
//				csvHeader[++j] = "私人-S";
//				csvHeader[++j] = "比較日個人-E";
//				csvHeader[++j] = "比較日個人-I";
//				csvHeader[++j] = "比較日個人-P";
//				csvHeader[++j] = "比較日個人-O";
//				csvHeader[++j] = "比較日個人-S";
//				csvHeader[++j] = "比較日白金-E";
//				csvHeader[++j] = "比較日白金-I";
//				csvHeader[++j] = "比較日白金-P";
//				csvHeader[++j] = "比較日白金-O";
//				csvHeader[++j] = "比較日白金-S";
//				csvHeader[++j] = "比較日私人-E";
//				csvHeader[++j] = "比較日私人-I";
//				csvHeader[++j] = "比較日私人-P";
//				csvHeader[++j] = "比較日私人-O";
//				csvHeader[++j] = "比較日私人-S";
//				csvHeader[++j] = "差異比個人-E";
//				csvHeader[++j] = "差異比個人-I";
//				csvHeader[++j] = "差異比個人-P";
//				csvHeader[++j] = "差異比個人-O";
//				csvHeader[++j] = "差異比個人-S";
//				csvHeader[++j] = "差異比白金-E";
//				csvHeader[++j] = "差異比白金-I";
//				csvHeader[++j] = "差異比白金-P";
//				csvHeader[++j] = "差異比白金-O";
//				csvHeader[++j] = "差異比白金-S";
//				csvHeader[++j] = "差異比私人-E";
//				csvHeader[++j] = "差異比私人-I";
//				csvHeader[++j] = "差異比私人-P";
//				csvHeader[++j] = "差異比私人-O";
//				csvHeader[++j] = "差異比私人-S";
//			
//
//				CSVUtil csv = new CSVUtil();
//				csv.setHeader(csvHeader);
//				csv.addRecordList(listCSV);
//				String url = csv.generateCSV();
//				// download
//				notifyClientToDownloadFile(url, fileName);
//			} else
//				return_VO.setResultList(list);
//			this.sendRtnObject(return_VO);
//		} catch (Exception e) {
//			logger.error(String.format("發生錯誤:%s",
//					StringUtil.getStackTraceAsString(e)));
//			throw new APException("系統發生錯誤請洽系統管理員");
//		}
//	}

	// ==判斷NULL後回傳空字串==//
	private String checkIsNull(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key)))) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}

	/**
	* 檢查Map取出欄位是否為 用於AO_CODE EMP_ID 
	* @param map
	* @return String
	*/
	private String checkIsNullAndTrans(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null) {
			return String.valueOf("=\""+map.get(key)+"\"");
		} else {
			return "";
		}
	}
	
	/** 匯出EXCEL檔 **/
	public void export(Object body, IPrimitiveMap header) throws JBranchException, Exception {
		
		PMS330OutputVO outputVO = (PMS330OutputVO) body;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String fileName = "理專各級客戶數增減_" + sdf.format(new Date())
				+ "_" + getUserVariable(FubonSystemVariableConsts.LOGINID) + ".xlsx";
		String Path = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		String filePath = Path + fileName;
		System.out.println(filePath);
		String uuid = UUID.randomUUID().toString();
		ConfigAdapterIF config = (ConfigAdapterIF) PlatformContext.getBean("configAdapter");
		
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd");
		String  times="";
		if (outputVO.getTime() != null) {
			times = sdf1.format(outputVO.getTime());
		}	

		//取得查詢結果
		List<Map<String, Object>> list = outputVO.getCsvList();
		//以下撈取全部資訊
		//營運區  區域中心用途

		XSSFWorkbook wb = new XSSFWorkbook();
		//設定SHEET名稱
		XSSFSheet sheet = wb.createSheet("理專各級客戶數增減");
		sheet.setDefaultColumnWidth(20);
		sheet.setDefaultRowHeightInPoints(20);

		// 資料 CELL型式
		XSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		style.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
		style.setBorderBottom((short) 1);
		style.setBorderTop((short) 1);
		style.setBorderLeft((short) 1);
		style.setBorderRight((short) 1);
		BigDecimal totalInvestMoney= new BigDecimal(0); 
		// 資料 CELL型式
		XSSFCellStyle numberStyle = wb.createCellStyle();
		numberStyle.setAlignment(XSSFCellStyle.ALIGN_RIGHT);
		numberStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
		numberStyle.setBorderBottom((short) 1);
		numberStyle.setBorderTop((short) 1);
		numberStyle.setBorderLeft((short) 1);
		numberStyle.setBorderRight((short) 1);

		// 表頭 CELL型式
		XSSFCellStyle headingStyle = wb.createCellStyle();
		headingStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		headingStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
		headingStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);// 填滿顏色
		headingStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		headingStyle.setBorderBottom((short) 1);
		headingStyle.setBorderTop((short) 1);
		headingStyle.setBorderLeft((short) 1);
		headingStyle.setBorderRight((short) 1);
		headingStyle.setWrapText(true);

		Integer index = 0; // 行數
		List<String> headerLineTop = new ArrayList<String>();
		
		headerLineTop.add("業務處");
		headerLineTop.add("營運區");
		headerLineTop.add("分行");
		headerLineTop.add("AO Code");		
		headerLineTop.add("資料年月");		
		headerLineTop.add(times+"(比較日)");
		headerLineTop.add("");
		headerLineTop.add("");
		headerLineTop.add("");
		headerLineTop.add("");
		String DATA_DATE=list.get(0).get("DATA_DATE").toString();
//		sheet.addMergedRegion(new CellRangeAddress(0, 0, 5, 7)); // firstRow, endRow, firstColumn, endColumn
		headerLineTop.add(DATA_DATE.substring(0,4)+"/"+DATA_DATE.substring(4, 6)+"/"+DATA_DATE.substring(6,8)+"(最近一日)");
		headerLineTop.add("");
		headerLineTop.add("");
		headerLineTop.add("");
		headerLineTop.add("");
//		sheet.addMergedRegion(new CellRangeAddress(0, 0, 8, 10)); // firstRow, endRow, firstColumn, endColumn
		headerLineTop.add("差異數");
		headerLineTop.add("");
		headerLineTop.add("");
		headerLineTop.add("");
		headerLineTop.add("");
//		sheet.addMergedRegion(new CellRangeAddress(0, 0, 11, 13)); // firstRow, endRow, firstColumn, endColumn
		
		List<String> headerLineSec = new ArrayList<String>();	
		headerLineSec.add("");
		headerLineSec.add("");
		headerLineSec.add("");
		headerLineSec.add("");
		headerLineSec.add("客戶分級");
//		headerLineSec.add("私人");
//		headerLineSec.add("白金");
//		headerLineSec.add("個人");
//		headerLineSec.add("一般");
		headerLineSec.add("恆富");
		headerLineSec.add("智富");
		headerLineSec.add("穩富");
		headerLineSec.add("一般存戶-跨優");
		headerLineSec.add("一般");
//		headerLineSec.add("私人");
//		headerLineSec.add("白金");
//		headerLineSec.add("個人");
//		headerLineSec.add("一般");
		headerLineSec.add("恆富");
		headerLineSec.add("智富");
		headerLineSec.add("穩富");
		headerLineSec.add("一般存戶-跨優");
		headerLineSec.add("一般");
//		headerLineSec.add("私人");
//		headerLineSec.add("白金");
//		headerLineSec.add("個人");
//		headerLineSec.add("一般");
		headerLineSec.add("恆富");
		headerLineSec.add("智富");
		headerLineSec.add("穩富");
		headerLineSec.add("一般存戶-跨優");
		headerLineSec.add("一般");
//		headerLineSec.add("私人");
//		headerLineSec.add("白金");
//		headerLineSec.add("個人");
//		headerLineSec.add("一般");
		// Heading
		XSSFRow row = sheet.createRow(index);
		
//		row = sheet.createRow(index);
		row.setHeightInPoints(25);
		int j=0;
		for (int i = 0; i < headerLineTop.size(); i++)  {
			if (i < 4) {
				XSSFCell cell = row.createCell(i);
				cell.setCellStyle(headingStyle);
				cell.setCellValue(headerLineTop.get(i));
				sheet.addMergedRegion(new CellRangeAddress(0, 1, i, i)); // firstRow, endRow, firstColumn, endColumn
			} else if (i == 4) {
				XSSFCell cell = row.createCell(i);
				cell.setCellStyle(headingStyle);
				cell.setCellValue(headerLineTop.get(i));
			} else if(i == 5 || i == 10 || i == 15) {
				XSSFCell cell = row.createCell(i);
				cell.setCellStyle(headingStyle);
				cell.setCellValue(headerLineTop.get(i));
				if(i>5)
					sheet.addMergedRegion(new CellRangeAddress(0, 0, i-5, i-1)); // firstRow, endRow, firstColumn, endColumn
			} else {
				XSSFCell cell = row.createCell(i);
				cell.setCellStyle(headingStyle);
				cell.setCellValue(headerLineTop.get(i));
				if(i==19)
					sheet.addMergedRegion(new CellRangeAddress(0, 0, i-4, i)); // firstRow, endRow, firstColumn, endColumn
			}
		}
//		sheet.addMergedRegion(new CellRangeAddress(0, 0, 5, 7)); // firstRow, endRow, firstColumn, endColumn
//		sheet.addMergedRegion(new CellRangeAddress(0, 0, 8, 10)); // firstRow, endRow, firstColumn, endColumn
//		sheet.addMergedRegion(new CellRangeAddress(0, 0, 11, 13)); // firstRow, endRow, firstColumn, endColumn
		index++;
		row = sheet.createRow(index);
		for (int i = 0; i < headerLineSec.size(); i++) {
				XSSFCell cell = row.createCell(i);
				cell.setCellStyle(headingStyle);
				cell.setCellValue(headerLineSec.get(i));
		}

		// Data row
		String[] mainLine = {"REGION_CENTER_NAME", "BRANCH_AREA_NAME", "BRANCH_NAME","AO_CODE"};
//		List<String,String> detailList = new ArrayList<String>();	
		List<Map<String, Object>> detailList = new ArrayList<Map<String, Object>>();	
		Map map = new HashMap();
		map.put("1", "SE_PRI");
		map.put("2", "SE_PLAT");
		map.put("3", "SE_PERSON");
		map.put("4", "SE_PERSON_C");
		map.put("5", "SE_MASS");
		map.put("6", "E_PRI");
		map.put("7", "E_PLAT");
		map.put("8", "E_PERSON");
		map.put("9", "E_PERSON_C");
		map.put("10", "E_MASS");
		map.put("11", "ESS_EI");
		map.put("12", "ESS_ET");
		map.put("13", "ESS_EP");
		map.put("14", "ESS_EC");
		map.put("15", "ESS_EM");
		detailList.add(map);   //E
	    map = new HashMap();
		map.put("1", "SI_PRI");
		map.put("2", "SI_PLAT");
		map.put("3", "SI_PERSON");
		map.put("4", "SI_PERSON_C");
		map.put("5", "SI_MASS");
		map.put("6", "I_PRI");
		map.put("7", "I_PLAT");
		map.put("8", "I_PERSON");
		map.put("9", "I_PERSON_C");
		map.put("10", "I_MASS");
		map.put("11", "ESS_II");
		map.put("12", "ESS_IT");
		map.put("13", "ESS_IP");
		map.put("14", "ESS_IC");
		map.put("15", "ESS_IM");
		detailList.add(map);  //I
		map = new HashMap();
		map.put("1", "SP_PRI");
		map.put("2", "SP_PLAT");
		map.put("3", "SP_PERSON");
		map.put("4", "SP_PERSON_C");
		map.put("5", "SP_MASS");
		map.put("6", "P_PRI");
		map.put("7", "P_PLAT");
		map.put("8", "P_PERSON");
		map.put("9", "P_PERSON_C");
		map.put("10", "P_MASS");
		map.put("11", "ESS_PI");
		map.put("12", "ESS_PT");
		map.put("13", "ESS_PP");
		map.put("14", "ESS_PC");
		map.put("15", "ESS_PM");
		detailList.add(map);  //P
		map = new HashMap();
		map.put("1", "SO_PRI");
		map.put("2", "SO_PLAT");
		map.put("3", "SO_PERSON");
		map.put("4", "SO_PERSON_C");
		map.put("5", "SO_MASS");
		map.put("6", "O_PRI");
		map.put("7", "O_PLAT");
		map.put("8", "O_PERSON");
		map.put("9", "O_PERSON_C");
		map.put("10", "O_MASS");
		map.put("11", "ESS_OI");
		map.put("12", "ESS_OT");
		map.put("13", "ESS_OP");
		map.put("14", "ESS_OC");
		map.put("15", "ESS_OM");
		detailList.add(map);  //O
		map = new HashMap();
		map.put("1", "SS_PRI");
		map.put("2", "SS_PLAT");
		map.put("3", "SS_PERSON");
		map.put("4", "SS_PERSON_C");
		map.put("5", "SS_MASS");
		map.put("6", "S_PRI");
		map.put("7", "S_PLAT");
		map.put("8", "S_PERSON");
		map.put("9", "S_PERSON_C");
		map.put("10", "S_MASS");
		map.put("11", "ESS_SI");
		map.put("12", "ESS_ST");
		map.put("13", "ESS_SP");
		map.put("14", "ESS_SC");
		map.put("15", "ESS_SM");
		detailList.add(map);  //S
		map = new HashMap();
		map.put("1", "SZ_PRI");
		map.put("2", "SZ_PLAT");
		map.put("3", "SZ_PERSON");
		map.put("4", "SZ_PERSON_C");
		map.put("5", "SZ_MASS");
		map.put("6", "Z_PRI");
		map.put("7", "Z_PLAT");
		map.put("8", "Z_PERSON");
		map.put("9", "Z_PERSON_C");
		map.put("10", "Z_MASS");
		map.put("11", "ESS_ZI");
		map.put("12", "ESS_ZT");
		map.put("13", "ESS_ZP");
		map.put("14", "ESS_ZC");
		map.put("15", "ESS_ZM");
		detailList.add(map);  //Z
		//資料開始
		index++;
//		
		ArrayList<String> centerNameTempList = new ArrayList<String>(); //比對用
		ArrayList<String> branchAreaNameTempList = new ArrayList<String>(); //比對用
		ArrayList<String> branchTempList = new ArrayList<String>(); //比對用
		Integer centerStartFlag = 0, branchAreaStartFlag = 0, branchStartFlag = 0; //rowspan用
		Integer centerEndFlag = 0, branchAreaEndFlag = 0, branchEndFlag = 0; //rowspan用
		Integer contectStartIndex = index;
		for (int i = 0; i < list.size(); i++) {			
			for (int x = 0; x < detailList.size(); x++) {
				row = sheet.createRow(index);
				String centerName = (String) list.get(i).get("REGION_CENTER_NAME");
				String branchAreaName = (String) list.get(i).get("BRACH_AREA_NAME");
				String branchName = (String) list.get(i).get("BRANCNH_NAME");
				//區域中心
				XSSFCell cell;
				int detail = 0;
				for (j = 0; j < mainLine.length; j++) {
					cell = row.createCell(j);
					cell.setCellStyle(style);
					if(mainLine[j].equals("AO_CODE"))
						cell.setCellValue((String) list.get(i).get(mainLine[j])+"-"+list.get(i).get("EMP_NAME"));
					else
						cell.setCellValue((String) list.get(i).get(mainLine[j]));
					
//					sheet.addMergedRegion(new CellRangeAddress(index, index + 5, j, j )); // firstRow, endRow, firstColumn, endColumn
					detail++;
				}
				cell = row.createCell(detail);
				cell.setCellStyle(style);
				if(x==0)
					cell.setCellValue("E");
				else if(x==1)
					cell.setCellValue("I");
				else if(x==2)
					cell.setCellValue("P");
				else if(x==3)
					cell.setCellValue("O");
				else if(x==4)
					cell.setCellValue("S");
				else if(x==5)
					cell.setCellValue("一般");
				detail++;
				for (j = 1; j < map.size()+1; j++) {
					cell = row.createCell(detail);
					cell.setCellStyle(style);
					String detaistring=(String)detailList.get(x).get(""+j).toString();
					cell.setCellValue( (int)Double.parseDouble(list.get(i).get(detaistring)+""));
//					sheet.addMergedRegion(new CellRangeAddress(index, index + 5, j, j )); // firstRow, endRow, firstColumn, endColumn
					detail++;
				}
				if ((index-1)%6==0 && index!=2) {
					for (int z = 0; z < mainLine.length; z++) {
						sheet.addMergedRegion(new CellRangeAddress(index-5, index ,z,z )); // firstRow, endRow, firstColumn, endColumn
					}
				}
				index++;
			}
		}

		String tempName = UUID.randomUUID().toString();
//		//路徑結合
		File f = new File(config.getServerHome(), config.getReportTemp() + tempName);
//		//絕對路徑建檔
		wb.write(new FileOutputStream(f)); 

		notifyClientToDownloadFile(config.getReportTemp().substring(1) + tempName, fileName); 

		this.sendRtnObject(null);
	}

}
