package com.systex.jbranch.app.server.fps.pms342;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.pms000.PMS000;
import com.systex.jbranch.app.server.fps.pms000.PMS000InputVO;
import com.systex.jbranch.app.server.fps.pms000.PMS000OutputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.jlb.DataFormat;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * Author : 2016/05/17 Frank Editor : 2017/01/30 Kevin
 */

@Component("pms342")
@Scope("request")
public class PMS342 extends FubonWmsBizLogic {
	
	private DataAccessManager dam = null;

	public void queryData(Object body, IPrimitiveMap header) throws JBranchException, ParseException {

		PMS342InputVO inputVO = (PMS342InputVO) body;
		PMS342OutputVO outputVO = new PMS342OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		XmlInfo xmlInfo = new XmlInfo();
		boolean isFC = xmlInfo.doGetVariable("FUBONSYS.FC_ROLE", FormatHelper.FORMAT_2).containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		boolean isPSOP = xmlInfo.doGetVariable("FUBONSYS.PSOP_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		boolean isHANDMGR = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		boolean isARMGR = xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		boolean isOPMGR = xmlInfo.doGetVariable("FUBONSYS.MBRMGR_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		boolean isUHRMMGR = xmlInfo.doGetVariable("FUBONSYS.UHRMMGR_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		boolean isUHRMBMMGR = xmlInfo.doGetVariable("FUBONSYS.UHRMBMMGR_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE));

		//取得查詢資料可視範圍
		PMS000 pms000 = (PMS000) PlatformContext.getBean("pms000");
		PMS000InputVO pms000InputVO = new PMS000InputVO();
		pms000InputVO.setReportDate(inputVO.getReportDate());
		PMS000OutputVO pms000outputVO = pms000.getOrg(pms000InputVO);

		sql.append("SELECT TO_CHAR(D.TRADE_DATE, 'YYYY/MM/DD') AS TRADE_DATE, ");
		sql.append("       D.CUST_ID, ");
		sql.append("       D.CUST_NAME, ");
		sql.append("       D.ACCOUNT, ");
		sql.append("       D.DEBIT_AMT, ");
		sql.append("       D.CREDIT_AMT, ");
		sql.append("       D.BALANCE_AMT, ");
		sql.append("       D.SUMMARY, ");
		sql.append("       D.CREATETIME, ");
		sql.append("       D.EMP_ID, ");
		sql.append("       D.EMP_NAME, ");
		sql.append("       A.AO_CODE, ");
		sql.append("       D.REGION_CENTER_ID, ");
		sql.append("       D.BRANCH_NBR, ");
		sql.append("       D.CO_ACCT_YN, ");
		sql.append("       EMP.DEPT_ID ");
		sql.append("FROM TBPMS_DAILY_LARGE_CHANGE D  ");
		sql.append("LEFT JOIN VWORG_AO_INFO A ON D.BRANCH_NBR = A.BRA_NBR AND D.EMP_ID = A.EMP_ID AND A.TYPE = '1' ");
		sql.append("LEFT JOIN (SELECT DISTINCT EMP_ID, EMP_DEPT_ID AS DEPT_ID FROM VWORG_EMP_INFO) EMP ON A.EMP_ID = EMP.EMP_ID  ");
		sql.append("WHERE 1 = 1 ");
		
		//資料統計日期
		if (inputVO.getsCreDate() != null) {
			sql.append("AND TRUNC(D.TRADE_DATE) >= TO_DATE( :scredate , 'YYYY-MM-DD') ");
			condition.setObject("scredate", new java.text.SimpleDateFormat("yyyy/MM/dd").format(inputVO.getsCreDate()));
		}
		
		if (inputVO.geteCreDate() != null) {
			sql.append("AND TRUNC(D.TRADE_DATE) <= TO_DATE( :ecredate , 'YYYY-MM-DD') ");
			condition.setObject("ecredate", new java.text.SimpleDateFormat("yyyy/MM/dd").format(inputVO.geteCreDate()));
		}
		
		switch (getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG).toString()) {
			case "UHRM":
				sql.append("AND D.EMP_ID = :loginID ");
				
				condition.setObject("loginID", (String) SysInfo.getInfoValue(SystemVariableConsts.LOGINID));
				
				break;
			case "uhrmMGR":
			case "uhrmBMMGR":
				sql.append("AND ( ");
				sql.append("      D.EMP_ID IS NOT NULL ");
				sql.append("  AND EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO MT WHERE EMP.DEPT_ID = MT.DEPT_ID AND MT.EMP_ID = :loginID AND MT.DEPT_ID = :loginArea) ");
				sql.append(") ");
				
				condition.setObject("loginID", (String) SysInfo.getInfoValue(SystemVariableConsts.LOGINID));
				condition.setObject("loginArea", getUserVariable(FubonSystemVariableConsts.LOGIN_AREA));
				
				break;
			default:
				// 區域中心
				if (inputVO.getRegion_center_id() != null && !inputVO.getRegion_center_id().equals("")) {
					sql.append("AND D.BRANCH_NBR IN (SELECT BRANCH_NBR FROM VWORG_DEFN_BRH WHERE DEPT_ID = :region_center_id) ");
					
					condition.setObject("region_center_id", inputVO.getRegion_center_id());
				} else {
					//登入非總行人員強制加區域中心
					if (!isHANDMGR) {
						sql.append("AND D.BRANCH_NBR IN (SELECT BRANCH_NBR FROM VWORG_DEFN_BRH WHERE DEPT_ID IN (:region_center_id) ) ");
						condition.setObject("region_center_id", pms000outputVO.getV_regionList());
					}
				}
				
				// 營運區
				if (inputVO.getBranch_area_id() != null && !inputVO.getBranch_area_id().equals("")) {
					sql.append("AND D.BRANCH_NBR IN (SELECT BRANCH_NBR FROM VWORG_DEFN_BRH WHERE DEPT_ID = :branch_area_id) ");
					condition.setObject("branch_area_id", inputVO.getBranch_area_id());
				} else {
					if (!isHANDMGR) {
						sql.append("AND D.BRANCH_NBR IN (SELECT BRANCH_NBR FROM VWORG_DEFN_BRH WHERE DEPT_ID IN (:branch_area_id)) ");
						condition.setObject("branch_area_id", pms000outputVO.getV_areaList());
					}
				}
				
				// 分行
				if (inputVO.getBranch_nbr() != null && !inputVO.getBranch_nbr().equals("")) {
					sql.append("AND D.BRANCH_NBR = :branch_nbr ");
					condition.setObject("branch_nbr", inputVO.getBranch_nbr());
				} else {
					if (!isHANDMGR) {
						sql.append("AND D.BRANCH_NBR IN (:branch_nbr) ");
						condition.setObject("branch_nbr", pms000outputVO.getV_branchList());
					}
				}
				
				break;
		}
		
		// AO_CODE
		if (!StringUtils.isBlank(inputVO.getAo_code()) && !"".equals(inputVO.getAo_code())) {
			sql.append("AND A.AO_CODE = :ao_code ");
			condition.setObject("ao_code", inputVO.getAo_code());
		} else if (!StringUtils.isBlank(inputVO.getEmp_id()) && !"".equals(inputVO.getEmp_id())) {
			sql.append("AND D.EMP_ID = :emp_id ");
			condition.setObject("emp_id", inputVO.getEmp_id());
		}
		
		sql.append("GROUP BY TRADE_DATE, ");
		sql.append("         D.CUST_ID, ");
		sql.append("         D.CUST_NAME, ");
		sql.append("         D.ACCOUNT, ");
		sql.append("         D.DEBIT_AMT, ");
		sql.append("         D.CREDIT_AMT, ");
		sql.append("         D.BALANCE_AMT, ");
		sql.append("         D.SUMMARY, ");
		sql.append("         D.CREATETIME, ");
		sql.append("         D.EMP_ID, ");
		sql.append("         D.EMP_NAME, ");
		sql.append("         A.AO_CODE, ");
		sql.append("         D.REGION_CENTER_ID, ");
		sql.append("         D.BRANCH_NBR, ");
		sql.append("         D.CO_ACCT_YN, ");
		sql.append("         EMP.DEPT_ID ");
		
		sql.append("ORDER BY TRADE_DATE, CUST_ID, ACCOUNT ");
		
		condition.setQueryString(sql.toString());
		
		ResultIF list = dam.executePaging(condition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
		
		outputVO.setTotalPage(list.getTotalPage());
		outputVO.setResultList(list);
		outputVO.setTotalRecord(list.getTotalRecord());
		outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());
		outputVO.setTotalList(dam.exeQuery(condition));
		
		sendRtnObject(outputVO);
	}

	/*  === 產出Excel==== */
	public void export(Object body, IPrimitiveMap header) throws JBranchException {
		
		PMS342OutputVO outputVO = (PMS342OutputVO) body;
		
		List<Map<String, Object>> list = outputVO.getTotalList();
		
		String fileName = "大額異動報表_" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + "_" + (String) getUserVariable(FubonSystemVariableConsts.LOGINID) + ".csv";
		
		List listCSV = new ArrayList();
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> conYN = xmlInfo.doGetVariable("COMMON.YES_NO", FormatHelper.FORMAT_3);
		
		for (Map<String, Object> map : list) {
			String[] records = new String[11];
			int i = 0;
			records[i] = checkIsNull(map, "TRADE_DATE"); //交易日期
			records[++i] = DataFormat.getCustIdMaskForHighRisk(checkIsNull(map, "CUST_ID")); //客戶ID
			records[++i] = checkIsNull(map, "CUST_NAME"); //客戶姓名
			records[++i] = checkIsAoNull(map, "ACCOUNT"); //帳號
			records[++i] = currencyFormat(map, "DEBIT_AMT"); //借方金額
			records[++i] = currencyFormat(map, "CREDIT_AMT"); //貸方金額
			records[++i] = currencyFormat(map, "BALANCE_AMT"); //資料日餘額
			records[++i] = checkIsNull(map, "SUMMARY"); //摘要
			records[++i] = checkIsAoNull(map, "EMP_ID"); //理專員編
			records[++i] = checkIsNull(map, "EMP_NAME"); //理專姓名	
			records[++i] = conYN.get(checkIsNull(map, "CO_ACCT_YN")); //是否為法金戶	

			listCSV.add(records);
		}
		
		//header
		String[] csvHeader = new String[11];
		int j = 0;
		csvHeader[j] = "交易日期";
		csvHeader[++j] = "客戶ID";
		csvHeader[++j] = "客戶姓名";
		csvHeader[++j] = "帳號";
		csvHeader[++j] = "借方金額";
		csvHeader[++j] = "貸方金額";
		csvHeader[++j] = "資料日餘額";
		csvHeader[++j] = "摘要";
		csvHeader[++j] = "理專員編";
		csvHeader[++j] = "理專姓名";
		csvHeader[++j] = "是否為法金戶";

		CSVUtil csv = new CSVUtil();
		csv.setHeader(csvHeader);
		csv.addRecordList(listCSV);
		
		String url = csv.generateCSV();
		
		notifyClientToDownloadFile(url, fileName);

		this.sendRtnObject(null);
	}

	/**
	 * 檢查Map取出欄位是否為Null
	 */
	private String checkIsNull(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}

	/**
	 * 檢查Map取出欄位是否為Null
	 */
	private String checkIsAoNull(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null) {
			return String.valueOf("=\"" + map.get(key) + "\"");
		} else {
			return "";
		}
	}

	//處理貨幣格式
	private String currencyFormat(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null) {
			NumberFormat nf = NumberFormat.getInstance();
			return nf.format(map.get(key));
		} else
			return "0.00";
	}
}