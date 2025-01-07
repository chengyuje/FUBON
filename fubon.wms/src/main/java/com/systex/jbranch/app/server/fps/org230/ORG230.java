package com.systex.jbranch.app.server.fps.org230;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBORG_BRH_MBR_QUOTAVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

import de.schlichtherle.io.FileInputStream;

@Component("org230")
@Scope("request")
public class ORG230 extends FubonWmsBizLogic {
	public DataAccessManager dam = null;
	LinkedHashMap<String, String> headColumnMap = new LinkedHashMap<String, String>();

	public ORG230() {
		headColumnMap.put("分行", "DEPT_ID");
		headColumnMap.put("FC1員額數", "FC1_CNT");
		headColumnMap.put("FC2員額數", "FC2_CNT");
		headColumnMap.put("FC3員額數", "FC3_CNT");
		headColumnMap.put("FC4員額數", "FC4_CNT");
		headColumnMap.put("FC5員額數", "FC5_CNT");
		headColumnMap.put("作業人員員額數", "OP_CNT");
		headColumnMap.put("作業主管員額數", "OPH_CNT");
		headColumnMap.put("消金PS專員員額數", "PS_CNT");
		headColumnMap.put("JRM員額數", "JRM_CNT");
	}

	public void getBranchMbrQuotaLst(Object body, IPrimitiveMap header) throws Exception {

		ORG230InputVO inputVO = (ORG230InputVO) body;
		ORG230OutputVO outputVO = new ORG230OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT INFO.REGION_CENTER_ID, ");
		sql.append("       INFO.REGION_CENTER_NAME, ");
		sql.append("       INFO.BRANCH_AREA_ID, ");
		sql.append("       INFO.BRANCH_AREA_NAME, ");
		sql.append("       INFO.BRANCH_NBR, ");
		sql.append("       INFO.BRANCH_NAME, ");
		sql.append("       QUOTA.DEPT_ID, ");
		sql.append("       QUOTA.FC1_CNT, ");
		sql.append("       QUOTA.FC2_CNT, ");
		sql.append("       QUOTA.FC3_CNT, ");
		sql.append("       QUOTA.FC4_CNT, ");
		sql.append("       QUOTA.FC5_CNT, ");
		sql.append("       QUOTA.FCH_CNT, ");
		sql.append("       QUOTA.OP_CNT, ");
		sql.append("       QUOTA.OPH_CNT, ");
		sql.append("       QUOTA.PS_CNT, ");
		sql.append("       QUOTA.PS_CNT_REMARK, ");
		sql.append("       QUOTA.CREATETIME, ");
		sql.append("       QUOTA.CREATOR, ");
		sql.append("       QUOTA.MODIFIER, ");
		sql.append("       QUOTA.LASTUPDATE, ");
		//WMS-CR-20180205-02_「組織人員管理」分行業務人員進用流程管理 20180619新增
		sql.append("	   QUOTA.AO_CNT, ");
		sql.append("       QUOTA.CAO_CNT, ");
		sql.append("       QUOTA.JRM_CNT ");
		sql.append("FROM TBORG_BRH_MBR_QUOTA QUOTA ");
		sql.append("LEFT JOIN VWORG_DEFN_INFO INFO ON QUOTA.DEPT_ID = INFO.BRANCH_NBR ");
		sql.append("WHERE 1 = 1 ");

		if (StringUtils.isNotBlank(inputVO.getBranch_nbr()) && Integer.valueOf(inputVO.getBranch_nbr()) > 0) {
			sql.append("AND INFO.BRANCH_NBR = :branchID "); //分行代碼
			condition.setObject("branchID", inputVO.getBranch_nbr());
		} else if (StringUtils.isNotBlank(inputVO.getBranch_area_id()) && !"null".equals(inputVO.getBranch_area_id())) {
			sql.append("AND INFO.BRANCH_AREA_ID = :branchAreaID "); //營運區代碼
			condition.setObject("branchAreaID", inputVO.getBranch_area_id());
		} else if (StringUtils.isNotBlank(inputVO.getRegion_center_id()) && !"null".equals(inputVO.getRegion_center_id())) {
			sql.append("AND INFO.REGION_CENTER_ID = :regionCenterID "); //區域代碼
			condition.setObject("regionCenterID", inputVO.getRegion_center_id());
		} else {
			sql.append("AND INFO.BRANCH_NBR IN (:branchIDList) ");
			condition.setObject("branchIDList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		}

		condition.setQueryString(sql.toString());
		
		ResultIF branchMbrQuotaLst = dam.executePaging(condition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());

		outputVO.setBranchMbrQuotaLst(branchMbrQuotaLst); // data
		outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
		outputVO.setTotalPage(branchMbrQuotaLst.getTotalPage());// 總頁次
		outputVO.setTotalRecord(branchMbrQuotaLst.getTotalRecord());// 總筆數

		sendRtnObject(outputVO);
	}

	public void getSampleLst(Object body, IPrimitiveMap header) throws Exception {
		
		CSVUtil csv = new CSVUtil();

		// 設定表頭
		csv.setHeader(headColumnMap.keySet().toArray(new String[headColumnMap.keySet().size()]));
		String url = csv.generateCSV();
		notifyClientToDownloadFile(url, "員額上傳.csv");
		
		sendRtnObject(null);
	}

	public void uploadBranchMbrQuotaLst(Object body, IPrimitiveMap header) throws Exception {

		ORG230InputVO inputVO = (ORG230InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		if (!StringUtils.isBlank(inputVO.getFILE_NAME())) {
			File csvFile = new File((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), inputVO.getFILE_NAME());

			FileInputStream fi = new FileInputStream(csvFile);
			BufferedReader br = new BufferedReader(new InputStreamReader(fi, "big5"));

			// 將輸入資料轉型成List<Map<[資料庫欄位名稱], [欄位值]>> 方便下一步驟執行
			String[] head = br.readLine().split(",");
			String line = null;
			List<Map<String, String>> inputLst = new ArrayList<Map<String, String>>();
			
			while ((line = br.readLine()) != null) {
				String[] data = line.split(",");
				HashMap<String, String> dataMap = new HashMap<String, String>();

				for (int i = 0; i < data.length; i++) {
					dataMap.put(headColumnMap.get(head[i]), data[i]); //data[i]
				}
				
				inputLst.add(dataMap);
			}

			for (Map<String, String> dataMap : inputLst) {
				sb = new StringBuffer();
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				
				sb.append("SELECT DEPT_ID, ");
				sb.append("       DEPT_NAME, ");
				sb.append("       ORG_TYPE, ");
				sb.append("       PARENT_DEPT_ID ");
				sb.append("FROM TBORG_DEFN ");
				sb.append("WHERE DEPT_ID = :deptID ");
				sb.append("AND ORG_TYPE = 50 ");

				queryCondition.setQueryString(sb.toString());

				queryCondition.setObject("deptID", dataMap.get("DEPT_ID"));

				List<Map<String, Object>> list = dam.exeQuery(queryCondition);

				if (list.size() > 0) {
					sb = new StringBuffer();
					queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					
					sb.append("SELECT * ");
					sb.append("FROM TBORG_BRH_MBR_QUOTA ");
					sb.append("WHERE DEPT_ID = :deptID	");
					
					queryCondition.setObject("deptID", dataMap.get("DEPT_ID"));

					queryCondition.setQueryString(sb.toString());

					List<Map<String, Object>> quotaList = dam.exeQuery(queryCondition);

					sb = new StringBuffer();
					queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					if (quotaList.isEmpty()) {
						sb.append("INSERT INTO TBORG_BRH_MBR_QUOTA ( ");
						sb.append("  DEPT_ID, ");
						sb.append("  FC1_CNT, ");
						sb.append("  FC2_CNT, ");
						sb.append("  FC3_CNT, ");
						sb.append("  FC4_CNT, ");
						sb.append("  FC5_CNT, ");
						sb.append("  OP_CNT, ");
						sb.append("  PS_CNT, ");
						sb.append("  OPH_CNT, "); 
						sb.append("  JRM_CNT, ");
						sb.append("  VERSION, CREATETIME, CREATOR, MODIFIER, LASTUPDATE ");
						sb.append(") ");
						sb.append("VALUES ( ");
						sb.append("  :deptId, ");
						sb.append("  :fc1, ");
						sb.append("  :fc2, ");
						sb.append("  :fc3, ");
						sb.append("  :fc4, ");
						sb.append("  :fc5, ");
						sb.append("  :op, ");
						sb.append("  :ps, ");
						sb.append("  :oph, "); 
						sb.append("  :jrm, ");
						sb.append("  0, sysdate, :empId, :empId, sysdate ");
						sb.append(") ");

					} else {
						sb.append("UPDATE TBORG_BRH_MBR_QUOTA ");
						sb.append("SET FC1_CNT = :fc1, ");
						sb.append("    FC2_CNT = :fc2, ");
						sb.append("    FC3_CNT = :fc3, ");
						sb.append("    FC4_CNT = :fc4, ");
						sb.append("    FC5_CNT = :fc5, ");
						sb.append("    OP_CNT = :op, ");
						sb.append("    PS_CNT = :ps, ");
						sb.append("    OPH_CNT = :oph, "); 
						sb.append("    JRM_CNT = :jrm, ");
						sb.append("    VERSION = VERSION + 1, ");
						sb.append("    MODIFIER = :empId, ");
						sb.append("    LASTUPDATE = sysdate ");
						sb.append("WHERE DEPT_ID = :deptId");
					}
					
					queryCondition.setObject("deptId", dataMap.get("DEPT_ID"));
					queryCondition.setObject("fc1", StringUtils.isNotBlank(dataMap.get("FC1_CNT")) ? new BigDecimal(dataMap.get("FC1_CNT")) : BigDecimal.ZERO);
					queryCondition.setObject("fc2", StringUtils.isNotBlank(dataMap.get("FC2_CNT")) ? new BigDecimal(dataMap.get("FC2_CNT")) : BigDecimal.ZERO);
					queryCondition.setObject("fc3", StringUtils.isNotBlank(dataMap.get("FC3_CNT")) ? new BigDecimal(dataMap.get("FC3_CNT")) : BigDecimal.ZERO);
					queryCondition.setObject("fc4", StringUtils.isNotBlank(dataMap.get("FC4_CNT")) ? new BigDecimal(dataMap.get("FC4_CNT")) : BigDecimal.ZERO);
					queryCondition.setObject("fc5", StringUtils.isNotBlank(dataMap.get("FC5_CNT")) ? new BigDecimal(dataMap.get("FC5_CNT")) : BigDecimal.ZERO);
					queryCondition.setObject("op", StringUtils.isNotBlank(dataMap.get("OP_CNT")) ? new BigDecimal(dataMap.get("OP_CNT")) : BigDecimal.ZERO);
					queryCondition.setObject("ps", StringUtils.isNotBlank(dataMap.get("PS_CNT")) ? new BigDecimal(dataMap.get("PS_CNT")) : BigDecimal.ZERO);
					queryCondition.setObject("oph", StringUtils.isNotBlank(dataMap.get("OPH_CNT")) ? new BigDecimal(dataMap.get("OPH_CNT")) : BigDecimal.ZERO);
					queryCondition.setObject("jrm", StringUtils.isNotBlank(dataMap.get("JRM_CNT")) ? new BigDecimal(dataMap.get("JRM_CNT")) : BigDecimal.ZERO);
					queryCondition.setObject("empId", getUserVariable(FubonSystemVariableConsts.LOGINID));
					
					queryCondition.setQueryString(sb.toString());
					
					dam.exeUpdate(queryCondition);
				}
			}
		}
		
		sendRtnObject(null);
	}

	public void delBranchMbrQuota(Object body, IPrimitiveMap header) throws Exception {

		ORG230InputVO inputVO = (ORG230InputVO) body;
		dam = this.getDataAccessManager();

		TBORG_BRH_MBR_QUOTAVO quotaVO = (TBORG_BRH_MBR_QUOTAVO) dam.findByPKey(TBORG_BRH_MBR_QUOTAVO.TABLE_UID, inputVO.getBranch_nbr());
		dam.delete(quotaVO);

		sendRtnObject(null);
	}
}
