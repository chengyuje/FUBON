package com.systex.jbranch.app.server.fps.pms333;

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
 * Description :未掛Code客戶報表Controller <br>
 * Comments Name : PMS333.java<br>
 * Author :Kevin<br>
 * Date :2016年09月29日 <br>
 * Version : 1.01 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年01月31日<br>
 */
@Component("pms333")
@Scope("request")
public class PMS333 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PMS333.class);

	/**
	 * 彈跳視窗查詢
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void getDetail(Object body, IPrimitiveMap header) throws JBranchException {
		PMS333InputVO inputVO = (PMS333InputVO) body;
		PMS333OutputVO return_VO = new PMS333OutputVO();
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		try {
			// ==主查詢==
		    sql.append(" SELECT ");
			sql.append(" CM.CUST_ID,CM.CUST_NAME,CM.VIP_DEGREE,CM.CON_DEGREE,AUM.AVG_AUM_AMT ");
			sql.append(" FROM TBCRM_CUST_MAST CM INNER JOIN TBCRM_CUST_NOTE CN ON CM.CUST_ID = CN.CUST_ID ");
			sql.append(" LEFT JOIN TBCRM_CUST_AUM_MONTHLY AUM ON CM.CUST_ID = AUM.CUST_ID ");
			sql.append(" WHERE NVL(CM.AO_CODE,'000') = '000' AND CM.VIP_DEGREE IN ('H','T','K') AND CM.CON_DEGREE IN ('E','I','P','O','S') ");
//			 --排RS、NS、客訴戶、企金RM、死亡戶 
			sql.append("AND CN.DEATH_YN <> 'Y' AND NVL(CN.RM_ID,'000000') = '000000' AND CN.COMM_RS_YN <> 'Y' AND CN.COMM_NS_YN <> 'Y' AND  CN.COMPLAIN_YN <> 'Y' ");
						
			// ==主查詢條件==
			//分行
			if (!StringUtils.isBlank(inputVO.getBranch())) {
				sql.append(" and CM.BRA_NBR = :BRANCH_NBRR ");
				queryCondition.setObject("BRANCH_NBRR",inputVO.getBranch());
			}			
						
			// 客戶等級
			if (!StringUtils.isBlank(inputVO.getCUST_DEGREE()) && 
					(StringUtils.equals(inputVO.getCUST_DEGREE(), "E") ||
					 StringUtils.equals(inputVO.getCUST_DEGREE(), "I") ||
					 StringUtils.equals(inputVO.getCUST_DEGREE(), "P") ||
					 StringUtils.equals(inputVO.getCUST_DEGREE(), "O") ||
					 StringUtils.equals(inputVO.getCUST_DEGREE(), "S"))) {
				sql.append(" and CM.CON_DEGREE = :CUSTDEGREE ");
				queryCondition.setObject("CUSTDEGREE", inputVO.getCUST_DEGREE());
			}

			if (!StringUtils.isBlank(inputVO.getIND()) && 
				(StringUtils.equals(inputVO.getIND(), "H") ||
				 StringUtils.equals(inputVO.getIND(), "T") ||
				 StringUtils.equals(inputVO.getIND(), "K"))) {
				sql.append(" and CM.VIP_DEGREE = :VIPDEGREE ");
				queryCondition.setObject("VIPDEGREE", inputVO.getIND());
			}
		
			queryCondition.setQueryString(sql.toString());
			return_VO.setResultList(dam.exeQuery(queryCondition));
//			
//			ResultIF list = dam.executePaging(queryCondition,
//			inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
//			int totalPage_i = list.getTotalPage();
//			int totalRecord_i = list.getTotalRecord();
//			return_VO.setResultList(list);
//			return_VO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
//			return_VO.setTotalPage(totalPage_i);// 總頁次
//			return_VO.setTotalRecord(totalRecord_i);// 總筆數
			this.sendRtnObject(return_VO);
			
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	/**
	 * 匯出確認NULL
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	private String checkIsNull(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null) {
			return String.valueOf(map.get(key));
		} else {
			return "0";
		}
	}

	/**
	 * 匯出
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void export(Object body, IPrimitiveMap header) throws JBranchException {
		// 取得畫面資料
		PMS333OutputVO return_VO = (PMS333OutputVO) body;

		List<Map<String, Object>> list = return_VO.getList();
		if (list.size() > 0) {
			// gen csv
			String.format("%1$,09d", -3123);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String fileName = "未掛Code客戶報表" + sdf.format(new Date()) + "_" + getUserVariable(FubonSystemVariableConsts.LOGINID)+".csv";
			List listCSV = new ArrayList();
			for (Map<String, Object> map : list) {
				// 21 column
				String[] records = new String[16];
				int i = 0;
//				records[i] = checkIsNull(map, "REGION_CENTER_ID");
				records[i] = checkIsNull(map, "REGION_CENTER_NAME");
//				records[++i] = checkIsNull(map, "BRANCH_AREA_ID"); //將OP_AREA_ID修改為BRANCH_AREA_ID
				records[++i] = checkIsNull(map, "BRANCH_AREA_NAME"); //將OP_AREA_NAME修改為BRANCH_AREA_NAME
				records[++i] = checkIsNull(map, "BRANCH_NBR");
				records[++i] = checkIsNull(map, "BRANCH_NAME");
				records[++i] = checkIsNull(map, "CON_DEGREE");
				records[++i] = checkIsNull(map, "H_CNT");	//H
				records[++i] = checkIsNull(map, "T_CNT");	//T
				records[++i] = checkIsNull(map, "K_CNT");	//K
//				records[++i] = checkIsNull(map, "V_CNT");	//V
//				records[++i] = checkIsNull(map, "A_CNT");	//A
//				records[++i] = checkIsNull(map, "B_CNT");	//B
				Double intValue = Double.parseDouble(checkIsNull(map, "H_CNT")) +
								  Double.parseDouble(checkIsNull(map, "T_CNT")) +
								  Double.parseDouble(checkIsNull(map, "K_CNT"));
//				Double intValue = Double.parseDouble(checkIsNull(map, "V_CNT")) +
//								  Double.parseDouble(checkIsNull(map, "A_CNT")) +
//								  Double.parseDouble(checkIsNull(map, "B_CNT"));
				records[++i] = intValue + "";
				listCSV.add(records);
			}
			// header
			String[] csvHeader = new String[16];
			int j = 0;
			csvHeader[j]   = "業務處名稱";
			csvHeader[++j] = "營運區名稱";
			csvHeader[++j] = "分行代碼";
			csvHeader[++j] = "分行名稱";
			csvHeader[++j] = "客戶分級";
			csvHeader[++j] = "未掛code客戶數-恆富理財會員";
			csvHeader[++j] = "未掛code客戶數-智富理財會員";
			csvHeader[++j] = "未掛code客戶數-穩富理財會員";
			csvHeader[++j] = "未掛code客戶數合計";
			CSVUtil csv = new CSVUtil();
			csv.setHeader(csvHeader);
			csv.addRecordList(listCSV);
			String url = csv.generateCSV();
			// download
			notifyClientToDownloadFile(url, fileName);
		
		} 
		this.sendRtnObject(null);
	}
	
	/**
	 * 主查詢
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		//輸入VO
		PMS333InputVO inputVO = (PMS333InputVO) body;
		//輸出VO
		PMS333OutputVO outputVO = new PMS333OutputVO();
		
		String roleType = "";
		String roleID = (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> fcMap = xmlInfo.doGetVariable("FUBONSYS.FC_ROLE", FormatHelper.FORMAT_2);			// 理專
		Map<String, String> psopMap = xmlInfo.doGetVariable("FUBONSYS.PSOP_ROLE", FormatHelper.FORMAT_2);		// OP
		Map<String, String> bmmgrMap = xmlInfo.doGetVariable("FUBONSYS.BMMGR_ROLE", FormatHelper.FORMAT_2);		// 個金主管
		Map<String, String> mbrmgrMap = xmlInfo.doGetVariable("FUBONSYS.MBRMGR_ROLE", FormatHelper.FORMAT_2);	// 營運督導
		Map<String, String> armgrMap = xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2);		// 區域中心主管
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2);	// 總行人員
			
		//取得查詢資料可視範圍
		PMS000 pms000 = (PMS000) PlatformContext.getBean("pms000");
		PMS000InputVO pms000InputVO = new PMS000InputVO();
		pms000InputVO.setReportDate(inputVO.getReportDate());
		PMS000OutputVO pms000outputVO = pms000.getOrg(pms000InputVO);
		
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		StringBuffer sqlview = new StringBuffer();
		try {
			// ==主查詢==
//			sql.append("WITH NOAO_CUST(BRANCH_NBR,CON_DEGREE,V_CNT,A_CNT,B_CNT) AS ( ");
			sql.append("WITH NOAO_CUST(BRANCH_NBR, CON_DEGREE, H_CNT, T_CNT, K_CNT, C_CNT) AS ( ");
			sql.append("SELECT * FROM ( ");
			sql.append(" SELECT CM.BRA_NBR,VIP_DEGREE,CON_DEGREE,CM.CUST_ID ");
			sql.append(" FROM TBCRM_CUST_MAST CM INNER JOIN TBCRM_CUST_NOTE CN ON CM.CUST_ID = CN.CUST_ID ");
//			sql.append(" WHERE NVL(AO_CODE,'000') = '000' AND VIP_DEGREE IN ('V','A','B') AND CON_DEGREE IN ('E','I','P','O','S') ");
			sql.append(" WHERE NVL(AO_CODE,'000') = '000' AND VIP_DEGREE IN ('H','T','K','C') AND CON_DEGREE IN ('E','I','P','O','S') ");
//			 --排RS、NS、客訴戶、企金RM、死亡戶 
			sql.append("AND DEATH_YN <> 'Y' AND NVL(RM_ID,'000000') = '000000' AND COMM_RS_YN <> 'Y' AND COMM_NS_YN <> 'Y' AND  COMPLAIN_YN <> 'Y' ");
//			sql.append(") PIVOT (COUNT(CUST_ID) FOR VIP_DEGREE IN ('V' V_CNT,'A' A_CNT,'B' B_CNT))),ORG AS ( ");
			sql.append(") PIVOT (COUNT(CUST_ID) FOR VIP_DEGREE IN ('H' H_CNT,'T' T_CNT,'K' K_CNT, 'C' C_CNT))),ORG AS ( ");
			sql.append("select REGION_CENTER_ID,REGION_CENTER_NAME,BRANCH_AREA_ID,BRANCH_AREA_NAME,O.BRANCH_NBR,O.BRANCH_NAME,PARAM_CODE CON_DEGREE ");
//			sql.append(",NVL(NOAO_CUST.V_CNT,0) V_CNT, NVL(NOAO_CUST.A_CNT,0) A_CNT,NVL(NOAO_CUST.B_CNT,0) B_CNT ");
			sql.append(",NVL(NOAO_CUST.H_CNT,0) H_CNT, NVL(NOAO_CUST.T_CNT,0) T_CNT, NVL(NOAO_CUST.K_CNT,0) K_CNT, NVL(NOAO_CUST.C_CNT,0) C_CNT ");
			sql.append("FROM TBPMS_ORG_REC_N o INNER JOIN TBSYSPARAMETER P ON P.PARAM_TYPE = 'CRM.CON_DEGREE' AND P.PARAM_CODE IN ('E','I','P','O','S') ");
			sql.append("LEFT JOIN NOAO_CUST ON O.BRANCH_NBR = NOAO_CUST.BRANCH_NBR AND P.PARAM_CODE = NOAO_CUST.CON_DEGREE ");
			sql.append("WHERE sysdate between o.START_TIME and o.END_TIME ");
			sql.append("and o.ORG_TYPE = '50' ");/* 20以下:總行 30:區域中心 40:營運區 50:分行 60:組別 */
			sql.append("and o.BRANCH_NBR BETWEEN '200' AND '900' ");
			//區域中心
			if (!StringUtils.isBlank(inputVO.getRegion_center_id())) {
				sql.append(" and o.REGION_CENTER_ID = :REGION_CENTER_IDDD ");
				condition.setObject("REGION_CENTER_IDDD",inputVO.getRegion_center_id());
			} else {
				//登入非總行人員強制加區域中心
				if (!headmgrMap.containsKey(roleID)) {
					sql.append("and o.REGION_CENTER_ID IN (:region_center_id) ");
					condition.setObject("region_center_id", pms000outputVO.getV_regionList());
				}
			}
			//營運區
			if (!StringUtils.isBlank(inputVO.getBranch_area_id())) {
				sql.append(" and o.BRANCH_AREA_ID = :BRANCH_AREA_IDDD ");
				condition.setObject("BRANCH_AREA_IDDD",inputVO.getBranch_area_id());
			} else {
				//登入非總行人員強制加營運區
				if (!headmgrMap.containsKey(roleID)) {
					sql.append("  and o.BRANCH_AREA_ID IN (:branch_area_id) ");
					condition.setObject("branch_area_id", pms000outputVO.getV_areaList());
				}
			}
			//分行
			if (!StringUtils.isBlank(inputVO.getBranch_nbr())) {
				sql.append(" and o.BRANCH_NBR = :BRANCH_NBRR ");
				condition.setObject("BRANCH_NBRR",inputVO.getBranch_nbr());
			} else {
				//登入非總行人員強制加分行
				if (!headmgrMap.containsKey(roleID)) {		
					sql.append("  and o.BRANCH_NBR IN (:branch_nbr) ");
					condition.setObject("branch_nbr", pms000outputVO.getV_branchList());
				}
			}
			sql.append(") ");
			sql.append("SELECT * FROM (");
//			sql.append("	SELECT REGION_CENTER_ID,REGION_CENTER_NAME,BRANCH_AREA_ID,BRANCH_AREA_NAME,BRANCH_NBR,BRANCH_NAME,CON_DEGREE,V_CNT,A_CNT,B_CNT FROM ORG ");
			sql.append("	SELECT REGION_CENTER_ID,REGION_CENTER_NAME,BRANCH_AREA_ID,BRANCH_AREA_NAME,BRANCH_NBR,BRANCH_NAME,CON_DEGREE,H_CNT,T_CNT,K_CNT,C_CNT FROM ORG ");
			sql.append("	UNION ");
//			sql.append("	SELECT REGION_CENTER_ID,REGION_CENTER_NAME,BRANCH_AREA_ID,BRANCH_AREA_NAME,BRANCH_NBR,BRANCH_NAME,'分行小計',SUM(V_CNT),SUM(A_CNT),SUM(B_CNT) FROM ORG ");
			sql.append("	SELECT REGION_CENTER_ID,REGION_CENTER_NAME,BRANCH_AREA_ID,BRANCH_AREA_NAME,BRANCH_NBR,BRANCH_NAME,'分行小計',SUM(H_CNT),SUM(T_CNT),SUM(K_CNT),SUM(C_CNT) FROM ORG ");
			sql.append("	GROUP BY REGION_CENTER_ID,REGION_CENTER_NAME,BRANCH_AREA_ID,BRANCH_AREA_NAME,BRANCH_NBR,BRANCH_NAME ");
			sql.append("    ) DA WHERE 1=1 ");						
			sql.append("ORDER BY REGION_CENTER_ID,BRANCH_AREA_ID,BRANCH_NBR,CON_DEGREE ");
			
			condition.setQueryString(sql.toString());
			//匯出用list
			List<Map<String, Object>> csvLsit = dam.exeQuery(condition);
			outputVO.setResultList(csvLsit); // 查詢結果頁面data
			outputVO.setCsvList(csvLsit);
			this.sendRtnObject(outputVO);
			
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

}