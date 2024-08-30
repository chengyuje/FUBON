package com.systex.jbranch.app.server.fps.pms332;

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
 * Description :百大貢獻度客戶報表Controller <br>
 * Comments Name : PMS332.java<br>
 * Author :Kevin<br>
 * Date :2016年06月03日 <br>
 * Version : 1.01 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年01月31日<br>
 */

@Component("pms332")
@Scope("request")
public class PMS332 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PMS332.class);

	/**
	 * 匯出檔案確認欄位NULL
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	private String checkIsNull(Map map, String key) {
		
		if(key=="VIP_DEGREE"){
			switch(String.valueOf(map.get(key))){
				case "H":return "恆富理財會員";
				case "T":return "智富理財會員";
				case "K":return "穩富理財會員";
				case "C":return "一般存戶-跨優";
				case "V":return "私人銀行理財會員";
				case "A":return "白金理財會員";
				case "B":return "個人理財會員";
				default: return "非理財會員";	
			}	
		}
		if(key=="YTD_FEE"){
			if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key)!=null) {
				return String.valueOf(map.get(key) );
			} else {
				return "0";
			}
		}
		if(key=="ACUM_FEE"){
			if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key)!=null) {
				return String.valueOf(map.get(key) );
			} else {
				return "0";
			}
		}
		if(key=="MON_FEE"){
			if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key)!=null) {
				return String.valueOf(map.get(key) );
			} else {
				return "0";
			}
		}
		if(key=="FUND"){
			if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key)!=null) {
				return String.valueOf(map.get(key) );
			} else {
				return "0";
			}
		}
		if(key=="ETF"){
			if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key)!=null) {
				return String.valueOf(map.get(key) );
			} else {
				return "0";
			}
		}
		if(key=="BOND"){
			if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key)!=null) {
				return String.valueOf(map.get(key) );
			} else {
				return "0";
			}
		}
		if(key=="SI"){
			if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key)!=null) {
				return String.valueOf(map.get(key) );
			} else {
				return "0";
			}
		}
		if(key=="SN"){
			if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key)!=null) {
				return String.valueOf(map.get(key) );
			} else {
				return "0";
			}
		}
		if(key=="DCI"){
			if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key)!=null) {
				return String.valueOf(map.get(key) );
			} else {
				return "0";
			}
		}
		if(key=="OTHER"){
			if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key)!=null) {
				return String.valueOf(map.get(key) );
			} else {
				return "0";
			}
		}
		if(key=="INS_SPP"){
			if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key)!=null) {
				return String.valueOf(map.get(key) );
			} else {
				return "0";
			}
		}
		if(key=="INS_WSP"){
			if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key)!=null) {
				return String.valueOf(map.get(key) );
			} else {
				return "0";
			}
		}
		if(key=="INS_LPP"){
			if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key)!=null) {
				return String.valueOf(map.get(key) );
			} else {
				return "0";
			}
		}
		if(key=="AUM"){
			if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key)!=null) {
				return String.valueOf(map.get(key) );
			} else {
				return "0";
			}
		}
		if(key=="CONTRIB_RATE"){
			if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key)!=null) {
				return String.valueOf(map.get(key) );
			} else {
				return "0";
			}
		}
		if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key)!=null) {
			return String.valueOf(map.get(key) );
		} else {
			return "";
		}
	   
	}

	/**
	 * 匯出檔案
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void export(Object body, IPrimitiveMap header)
			throws JBranchException {
		// 取得畫面資料 輸入vo
		PMS332OutputVO return_VO = (PMS332OutputVO) body;

		List<Map<String, Object>> list = return_VO.getList();
		try {
			if (list.size() > 0) {
				// gen csv
				String.format("%1$,09d", -3123);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				String fileName = "分行百大貢獻度客戶報表" + sdf.format(new Date())+"_"
						+ getUserVariable(FubonSystemVariableConsts.LOGINID)
						+ ".csv";
				List listCSV = new ArrayList();
				for (Map<String, Object> map : list) {
					// 30 column
					String[] records = new String[29];
					int i = 0;
//					records[i] = checkIsNull(map, "ROWNUM").substring(0, 1); // 項次
//					records[i] =((int)Double.parseDouble(checkIsNull(map, "ROWNUM").toString()))+""; // 序號 - 去小數點
					records[i] = checkIsNull(map, "YEARMON"); // 資料統計年月
					records[++i] = checkIsNull(map, "REGION_CENTER_ID"); // 區域中心ID
					records[++i] = checkIsNull(map, "REGION_CENTER_NAME"); // 區域中心名稱
					records[++i] = checkIsNull(map, "BRANCH_AREA_ID"); // 營運區ID
					records[++i] = checkIsNull(map, "BRANCH_AREA_NAME"); // 營運區名稱
					records[++i] = checkIsNull(map, "BRANCH_NBR"); // 分行代碼
					records[++i] = checkIsNull(map, "BRANCH_NAME"); // 分行名稱
					records[++i] = checkIsNull(map, "AO_CODE"); // AO Code
					records[++i] = checkIsNull(map, "EMP_ID"); // 理專代碼
					records[++i] = checkIsNull(map, "EMP_NAME"); // 理專姓名
					records[++i] = checkIsNull(map, "RANK"); // 職級
					records[++i] = checkIsNull(map, "CUST_ID"); // 客戶ID
					records[++i] = checkIsNull(map, "CUST_NAME"); // 客戶姓名
					records[++i] = checkIsNull(map, "VIP_DEGREE"); // 客戶等級
					records[++i] = checkIsNull(map, "YTD_FEE"); // 當年度累計手收(R)
					records[++i] = checkIsNull(map, "ACUM_FEE"); // 前12個月累計手收
					records[++i] = checkIsNull(map, "MON_FEE"); // 當月手收P =
					// A1+A2+A3+B1+B2+B3+B4+E1+E2+E3)
					records[++i] = checkIsNull(map, "FUND"); // 單筆基金(A1)
					records[++i] = checkIsNull(map, "ETF"); // ETF(A2)
					records[++i] = checkIsNull(map, "BOND"); // 海外債(A3)
					records[++i] = checkIsNull(map, "SI"); // SI(B1)
					records[++i] = checkIsNull(map, "SN"); // SN(B2)
					records[++i] = checkIsNull(map, "DCI"); // DCI(B3)
					records[++i] = checkIsNull(map, "OTHER"); // 其他 (B4)
					records[++i] = checkIsNull(map, "INS_WSP"); // 躉繳保險(E1)
					records[++i] = checkIsNull(map, "INS_SPP"); // 短年繳保險(E2)
					records[++i] = checkIsNull(map, "INS_LPP"); // 長年繳保險(E2)
					records[++i] = checkIsNull(map, "AUM"); // AUM月平均餘額(T)
					records[++i] = checkIsNull(map, "CONTRIB_RATE"); // 手收貢獻率(C=R/T)

					listCSV.add(records);
				}
				// header
				String[] csvHeader = new String[29];
				int j = 0;
//				csvHeader[j] = "項次";
				csvHeader[j] = "資料統計年月";
				csvHeader[++j] = "業務處ID";
				csvHeader[++j] = "業務處名稱";
				csvHeader[++j] = "營運區ID";
				csvHeader[++j] = "營運區名稱";
				csvHeader[++j] = "分行代碼";
				csvHeader[++j] = "分行名稱";
				csvHeader[++j] = "AO Code";
				csvHeader[++j] = "理專代碼";
				csvHeader[++j] = "理專姓名";
				csvHeader[++j] = "職級";
				csvHeader[++j] = "客戶ID";
				csvHeader[++j] = "客戶姓名";
				csvHeader[++j] = "客戶等級";
				csvHeader[++j] = "當年度累計手收(R)";
				csvHeader[++j] = "前12個月累計手收";
				csvHeader[++j] = "當月手收(P = A1+A2+A3+B1+B2+B3+B4+E1+E2+E3)";
				csvHeader[++j] = "單筆基金(A1)";
				csvHeader[++j] = "ETF(A2)";
				csvHeader[++j] = "海外債(A3)";
				csvHeader[++j] = "SI(B1)";
				csvHeader[++j] = "SN(B2)";
				csvHeader[++j] = "DCI(B3)";
				csvHeader[++j] = "其他 (B4)";
				csvHeader[++j] = "躉繳保險(E1)";
				csvHeader[++j] = "短年繳保險(E2)";
				csvHeader[++j] = "長年繳保險(E2)";
				csvHeader[++j] = "AUM月平均餘額(T)";
				csvHeader[++j] = "手收貢獻率(C=R/T)";

				CSVUtil csv = new CSVUtil();
				csv.setHeader(csvHeader);
				csv.addRecordList(listCSV);
				String url = csv.generateCSV();
				// download
				notifyClientToDownloadFile(url, fileName);
			} else
				return_VO.setResultList(list);
			this.sendRtnObject(return_VO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	/**
	 * 查詢
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		// 輸入vo
		PMS332InputVO inputVO = (PMS332InputVO) body;
		// 輸出vo
		PMS332OutputVO outputVO = new PMS332OutputVO();
		
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
		pms000InputVO.setReportDate(inputVO.getsCreDate());
		PMS000OutputVO pms000outputVO = pms000.getOrg(pms000InputVO);
		
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		try {
			//==主查詢==
			sql.append(" SELECT  ROWNUM,T.* ,  ");
			sql.append(" ((CASE WHEN T.FUND is null THEN 0 ELSE T.FUND END) +  ");
			sql.append(" (CASE WHEN T.ETF is null THEN 0 ELSE T.ETF END) +  ");
			sql.append(" (CASE WHEN T.BOND is null THEN 0 ELSE T.BOND END) +  ");
			sql.append(" (CASE WHEN T.SI is null THEN 0 ELSE T.SI END) +  ");
			sql.append(" (CASE WHEN T.SN is null THEN 0 ELSE T.SN END) +  ");
			sql.append(" (CASE WHEN T.DCI is null THEN 0 ELSE T.DCI END) +  ");
			sql.append(" (CASE WHEN T.INS_OT is null THEN 0 ELSE T.INS_OT END) +  ");
			sql.append(" (CASE WHEN T.INS_SY is null THEN 0 ELSE T.INS_SY END) +  ");
			sql.append(" (CASE WHEN T.INS_LY is null THEN 0 ELSE T.INS_LY END)  ");
			sql.append(" )AS NEW_MON_FEE FROM    ");
			sql.append(" (SELECT * FROM TBPMS_100_CONTRIB_CUST   ");
			sql.append(" WHERE 1=1   ");
			
			//==主查詢條件==
			//區域中心
			if (StringUtils.isNotBlank(inputVO.getRegion_center_id()) && !"".equals(inputVO.getRegion_center_id())) {
				sql.append(" and REGION_CENTER_ID = :region_center_id ");
				condition.setObject("region_center_id", inputVO.getRegion_center_id());
			}else{
				//登入非總行人員強制加區域中心
				if(!headmgrMap.containsKey(roleID)) {
					sql.append("and REGION_CENTER_ID IN (:region_center_id) ");
					condition.setObject("region_center_id", pms000outputVO.getV_regionList());
				}
			}
			//營運區
			if (StringUtils.isNotBlank(inputVO.getBranch_area_id()) && !"".equals(inputVO.getBranch_area_id())) {
				sql.append(" and BRANCH_AREA_ID = :branch_area_id ");
				condition.setObject("branch_area_id", inputVO.getBranch_area_id());
			}else{
				//登入非總行人員強制加營運區
				if(!headmgrMap.containsKey(roleID)) {
					sql.append("  and BRANCH_AREA_ID IN (:branch_area_id) ");
					condition.setObject("branch_area_id", pms000outputVO.getV_areaList());
				}
			}
			//分行
			if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
				sql.append(" and BRANCH_NBR = :branch_nbr ");
				condition.setObject("branch_nbr", inputVO.getBranch_nbr());
			}else{
				//登入非總行人員強制加分行
				if(!headmgrMap.containsKey(roleID)) {		
					sql.append("  and BRANCH_NBR IN (:branch_nbr) ");
					condition.setObject("branch_nbr", pms000outputVO.getV_branchList());
				}
			}
			//員編
			if (StringUtils.isNotBlank(inputVO.getAo_code())) {
				sql.append(" and AO_CODE = :ao_code ");
				condition.setObject("ao_code", inputVO.getAo_code());
			}else{
				//登入為銷售人員強制加AO_CODE
				if(fcMap.containsKey(roleID) || psopMap.containsKey(roleID)) {
					sql.append(" and AO_CODE IN (:ao_code) ");
					condition.setObject("ao_code", pms000outputVO.getV_aoList());
				}
			}
			//日期年月
			if (!StringUtils.isBlank(inputVO.getsCreDate())) {
				sql.append(" and YEARMON LIKE :YEARMONN");
			}
			if (!StringUtils.isBlank(inputVO.getAojob())) {
				sql.append(" and RANK LIKE :RANKKK");

			}
			if (!StringUtils.isBlank(inputVO.getVIP())) {
				sql.append(" and VIP_DEGREE LIKE :VIP_DEGREEEE");
			}
			//排序
			if (!StringUtils.isBlank(inputVO.getNNUM())) {
				sql.append(" ORDER BY CONTRIB_RATE desc ");
			}
			sql.append("   ) T");
			
			if (!StringUtils.isBlank(inputVO.getNNUM())) {
				sql.append("   WHERE ROWNUM<=" + inputVO.getNNUM());
			}
			
			//分類排序
			if (!StringUtils.isBlank(inputVO.getBT())) {
				if ("01".equals(inputVO.getBT())) {
					sql.append("   ORDER BY T.YTD_FEE DESC , NEW_MON_FEE DESC , ACUM_FEE DESC ");
				}
				if ("02".equals(inputVO.getBT())) {
					sql.append("   ORDER BY T.ACUM_FEE DESC , NEW_MON_FEE DESC , ACUM_FEE DESC ");
				}
				if ("03".equals(inputVO.getBT())) {
					sql.append("   ORDER BY NEW_MON_FEE DESC , YTD_FEE DESC , ACUM_FEE DESC ");
				}
				if ("04".equals(inputVO.getBT())) {
					sql.append("   ORDER BY T.CONTRIB_RATE DESC");
				}

			}

			condition.setQueryString(sql.toString());
			//==主查詢條件設定==
			//年月
			if (!StringUtils.isBlank(inputVO.getsCreDate())) {
				condition.setObject("YEARMONN", "%" + inputVO.getsCreDate() + "%");
			}
			if (!StringUtils.isBlank(inputVO.getAojob())) {
				condition.setObject("RANKKK", "%" + inputVO.getAojob() + "%");
			}
			if (!StringUtils.isBlank(inputVO.getVIP())) {
				condition.setObject("VIP_DEGREEEE", "%" + inputVO.getVIP() + "%");
			}
			//分頁查詢結果
			ResultIF list = dam.executePaging(condition,
					inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
			//csv查詢結果
			List<Map<String, Object>> csvList = dam.exeQuery(condition);
			int totalPage_i = list.getTotalPage(); // 分頁用
			int totalRecord_i = list.getTotalRecord(); // 分頁用
			outputVO.setResultList(list); // data
			outputVO.setCsvList(csvList);
			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
			outputVO.setTotalPage(totalPage_i);// 總頁次
			outputVO.setTotalRecord(totalRecord_i);// 總筆數
			this.sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

}