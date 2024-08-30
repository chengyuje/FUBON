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
		headColumnMap.put("FCH員額數", "FCH_CNT");
		headColumnMap.put("作業人員員額數", "OP_CNT");
		headColumnMap.put("作業主管員額數", "OPH_CNT");
		headColumnMap.put("消金PS專員員額數", "PS_CNT");
//		headColumnMap.put("消金PS專員員額數備註", "PS_CNT_REMARK"); 20181123 問題單:5914 移除 modify by ocean
		//WMS-CR-20180205-02_「組織人員管理」分行業務人員進用流程管理 20180619新增
		//20181123 問題單:5914 移除 modify by ocean
//		headColumnMap.put("新興AO員額數", "AO_CNT");
//		headColumnMap.put("消金AO員額數", "CAO_CNT");
	}
	
	public void getBranchMbrQuotaLst(Object body, IPrimitiveMap header) throws Exception {

		ORG230InputVO inputVO = (ORG230InputVO) body;
		ORG230OutputVO outputVO = new ORG230OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT INFO.REGION_CENTER_ID, INFO.REGION_CENTER_NAME, INFO.BRANCH_AREA_ID, INFO.BRANCH_AREA_NAME, INFO.BRANCH_NBR, INFO.BRANCH_NAME, ");
		sql.append("       QUOTA.DEPT_ID, QUOTA.FC1_CNT, QUOTA.FC2_CNT, QUOTA.FC3_CNT, QUOTA.FC4_CNT, QUOTA.FC5_CNT, QUOTA.FCH_CNT, QUOTA.OP_CNT, QUOTA.OPH_CNT, QUOTA.PS_CNT, QUOTA.PS_CNT_REMARK, QUOTA.CREATETIME, QUOTA.CREATOR, QUOTA.MODIFIER, QUOTA.LASTUPDATE ,	");
		//WMS-CR-20180205-02_「組織人員管理」分行業務人員進用流程管理 20180619新增
		sql.append("		QUOTA.AO_CNT,QUOTA.CAO_CNT ");
		sql.append("FROM TBORG_BRH_MBR_QUOTA QUOTA ");
		sql.append("LEFT JOIN VWORG_DEFN_INFO INFO ON QUOTA.DEPT_ID = INFO.BRANCH_NBR ");
		sql.append("WHERE 1=1 ");
		
		if (StringUtils.isNotBlank(inputVO.getRegion_center_id()) && !"null".equals(inputVO.getRegion_center_id())) {
			sql.append("AND INFO.REGION_CENTER_ID = :regionCenterID "); //區域代碼
			condition.setObject("regionCenterID", inputVO.getRegion_center_id());
		} else {
			sql.append("AND INFO.REGION_CENTER_ID IN (:regionCenterIDList) ");
			condition.setObject("regionCenterIDList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
		}
	
		if (StringUtils.isNotBlank(inputVO.getBranch_area_id()) && !"null".equals(inputVO.getBranch_area_id())) {
			sql.append("AND INFO.BRANCH_AREA_ID = :branchAreaID "); //營運區代碼
			condition.setObject("branchAreaID", inputVO.getBranch_area_id());
		} else {
			sql.append("AND INFO.BRANCH_AREA_ID IN (:branchAreaIDList) ");
			condition.setObject("branchAreaIDList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
		}
	
		if (StringUtils.isNotBlank(inputVO.getBranch_nbr()) && Integer.valueOf(inputVO.getBranch_nbr()) > 0) {
			sql.append("AND INFO.BRANCH_NBR = :branchID "); //分行代碼
			condition.setObject("branchID", inputVO.getBranch_nbr());
		} else {
			sql.append("AND INFO.BRANCH_NBR IN (:branchIDList) ");
			condition.setObject("branchIDList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		}
		
		condition.setQueryString(sql.toString());
		ResultIF branchMbrQuotaLst = dam.executePaging(condition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
		int totalPage_i = branchMbrQuotaLst.getTotalPage(); // 分頁用
		int totalRecord_i = branchMbrQuotaLst.getTotalRecord(); // 分頁用
		outputVO.setBranchMbrQuotaLst(branchMbrQuotaLst); // data
		outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
		outputVO.setTotalPage(totalPage_i);// 總頁次
		outputVO.setTotalRecord(totalRecord_i);// 總筆數
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
		
		if (!StringUtils.isBlank(inputVO.getFILE_NAME())) {
			File csvFile = new File((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), inputVO.getFILE_NAME());
			
			FileInputStream fi = new FileInputStream(csvFile);
			BufferedReader br = new BufferedReader(new InputStreamReader(fi, "big5"));
			
			// 將輸入資料轉型成List<Map<[資料庫欄位名稱], [欄位值]>> 方便下一步驟執行
			String[] head = br.readLine().split(",");
			String line = null;
			List<Map<String, String>> inputLst = new ArrayList<Map<String,String>>();
			while((line = br.readLine()) != null) {
				String[] data = line.split(",");
				HashMap<String, String> dataMap = new HashMap<String, String>();
				
				for (int i = 0; i < data.length; i++) {
					dataMap.put(headColumnMap.get(head[i]), data[i]); //data[i]
				}
				inputLst.add(dataMap);
			}
			
			StringBuffer sb = new StringBuffer();
						
			for (Map<String, String> dataMap: inputLst) {
				sb = new StringBuffer();
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb.append("SELECT DEPT_ID, DEPT_NAME, ORG_TYPE, PARENT_DEPT_ID ");
				sb.append("FROM TBORG_DEFN ");
				sb.append("WHERE DEPT_ID = :deptID ");
				sb.append("AND ORG_TYPE = 50 ");
				
				queryCondition.setQueryString(sb.toString());
				
				queryCondition.setObject("deptID", dataMap.get("DEPT_ID"));
				
				List<Map<String, Object>> list = dam.exeQuery(queryCondition);

				if (list.size() > 0) {
					StringBuilder sql = new StringBuilder();
					queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					
					sql.append("SELECT * FROM TBORG_BRH_MBR_QUOTA ");
					sql.append("WHERE DEPT_ID = :deptID	");
					queryCondition.setObject("deptID", dataMap.get("DEPT_ID"));
					
					queryCondition.setQueryString(sql.toString());
					
					List<Map<String,Object>> quotaList = dam.exeQuery(queryCondition);
					
					if (quotaList.isEmpty()) {
						sql = new StringBuilder();
						sql.append("INSERT INTO TBORG_BRH_MBR_QUOTA ( ");
						sql.append("  DEPT_ID, FC1_CNT, FC2_CNT, FC3_CNT, FC4_CNT, FC5_CNT, FCH_CNT, ");
						sql.append("  OP_CNT, PS_CNT, OPH_CNT, "); //PS_CNT_REMARK, AO_CNT, CAO_CNT 20181123 問題單:5914 移除 modify by ocean
						sql.append("  VERSION, CREATETIME, CREATOR, MODIFIER, LASTUPDATE ");
						sql.append(") ");
						sql.append("VALUES ( ");
						sql.append("  :deptId, :fc1, :fc2, :fc3, :fc4, :fc5, :fch, ");
						sql.append("  :op, :ps, :oph, "); //:psRmk, :ao, :cao 20181123 問題單:5914 移除 modify by ocean
						sql.append("  0, sysdate, :empId, :empId, sysdate ");
						sql.append(") ");
						queryCondition	=	dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
						queryCondition.setObject("deptId", dataMap.get("DEPT_ID"));
						queryCondition.setObject("fc1", StringUtils.isNotBlank(dataMap.get("FC1_CNT")) ? new BigDecimal(dataMap.get("FC1_CNT")) : BigDecimal.ZERO);
						queryCondition.setObject("fc2", StringUtils.isNotBlank(dataMap.get("FC2_CNT")) ? new BigDecimal(dataMap.get("FC2_CNT")) : BigDecimal.ZERO);
						queryCondition.setObject("fc3", StringUtils.isNotBlank(dataMap.get("FC3_CNT")) ? new BigDecimal(dataMap.get("FC3_CNT")) : BigDecimal.ZERO);
						queryCondition.setObject("fc4", StringUtils.isNotBlank(dataMap.get("FC4_CNT")) ? new BigDecimal(dataMap.get("FC4_CNT")) : BigDecimal.ZERO);
						queryCondition.setObject("fc5", StringUtils.isNotBlank(dataMap.get("FC5_CNT")) ? new BigDecimal(dataMap.get("FC5_CNT")) : BigDecimal.ZERO);
						queryCondition.setObject("fch", StringUtils.isNotBlank(dataMap.get("FCH_CNT")) ? new BigDecimal(dataMap.get("FCH_CNT")) : BigDecimal.ZERO);
						queryCondition.setObject("op", StringUtils.isNotBlank(dataMap.get("OP_CNT")) ? new BigDecimal(dataMap.get("OP_CNT")) : BigDecimal.ZERO);
						queryCondition.setObject("ps", StringUtils.isNotBlank(dataMap.get("PS_CNT")) ? new BigDecimal(dataMap.get("PS_CNT")) : BigDecimal.ZERO);
//						queryCondition.setObject("psRmk", StringUtils.isNotBlank(dataMap.get("PS_CNT_REMARK")) ? dataMap.get("PS_CNT_REMARK") : null);
						queryCondition.setObject("oph", StringUtils.isNotBlank(dataMap.get("OPH_CNT")) ? new BigDecimal(dataMap.get("OPH_CNT")) : BigDecimal.ZERO);
//						queryCondition.setObject("ao", StringUtils.isNotBlank(dataMap.get("AO_CNT"))?new BigDecimal(dataMap.get("AO_CNT")):null);
//						queryCondition.setObject("cao", StringUtils.isNotBlank(dataMap.get("CAO_CNT"))?new BigDecimal(dataMap.get("CAO_CNT")):null);
						queryCondition.setObject("empId", getUserVariable(FubonSystemVariableConsts.LOGINID));
						queryCondition.setQueryString(sql.toString());
						dam.exeUpdate(queryCondition);
					}	else {
						sql = new StringBuilder();
						sql.append("UPDATE TBORG_BRH_MBR_QUOTA ");
						sql.append("SET FC1_CNT = :fc1, FC2_CNT = :fc2, FC3_CNT = :fc3, FC4_CNT = :fc4, FC5_CNT = :fc5, FCH_CNT = :fch, "); 
						sql.append("    OP_CNT = :op, PS_CNT = :ps, OPH_CNT = :oph, "); //PS_CNT_REMARK = :psRmk, AO_CNT = :ao, CAO_CNT = :cao, 20181123 問題單:5914 移除 modify by ocean
						sql.append("    VERSION = VERSION + 1, MODIFIER = :empId, LASTUPDATE = sysdate ");
						sql.append("WHERE DEPT_ID = :deptId");
						queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
						queryCondition.setObject("fc1", StringUtils.isNotBlank(dataMap.get("FC1_CNT")) ? new BigDecimal(dataMap.get("FC1_CNT")) : BigDecimal.ZERO);
						queryCondition.setObject("fc2", StringUtils.isNotBlank(dataMap.get("FC2_CNT")) ? new BigDecimal(dataMap.get("FC2_CNT")) : BigDecimal.ZERO);
						queryCondition.setObject("fc3", StringUtils.isNotBlank(dataMap.get("FC3_CNT")) ? new BigDecimal(dataMap.get("FC3_CNT")) : BigDecimal.ZERO);
						queryCondition.setObject("fc4", StringUtils.isNotBlank(dataMap.get("FC4_CNT")) ? new BigDecimal(dataMap.get("FC4_CNT")) : BigDecimal.ZERO);
						queryCondition.setObject("fc5", StringUtils.isNotBlank(dataMap.get("FC5_CNT")) ? new BigDecimal(dataMap.get("FC5_CNT")) : BigDecimal.ZERO);
						queryCondition.setObject("fch", StringUtils.isNotBlank(dataMap.get("FCH_CNT")) ? new BigDecimal(dataMap.get("FCH_CNT")) : BigDecimal.ZERO);
						queryCondition.setObject("op", StringUtils.isNotBlank(dataMap.get("OP_CNT")) ? new BigDecimal(dataMap.get("OP_CNT")) : BigDecimal.ZERO);
						queryCondition.setObject("ps", StringUtils.isNotBlank(dataMap.get("PS_CNT")) ? new BigDecimal(dataMap.get("PS_CNT")) : BigDecimal.ZERO);
//						queryCondition.setObject("psRmk", StringUtils.isNotBlank(dataMap.get("PS_CNT_REMARK")) ? dataMap.get("PS_CNT_REMARK") : null);
						queryCondition.setObject("oph", StringUtils.isNotBlank(dataMap.get("OPH_CNT")) ? new BigDecimal(dataMap.get("OPH_CNT")) : BigDecimal.ZERO);
//						queryCondition.setObject("ao", StringUtils.isNotBlank(dataMap.get("AO_CNT")) ? new BigDecimal(dataMap.get("AO_CNT")) : BigDecimal.ZERO);
//						queryCondition.setObject("cao", StringUtils.isNotBlank(dataMap.get("CAO_CNT")) ? new BigDecimal(dataMap.get("CAO_CNT")) : BigDecimal.ZERO);
						queryCondition.setObject("empId", getUserVariable(FubonSystemVariableConsts.LOGINID));
						queryCondition.setObject("deptId", dataMap.get("DEPT_ID"));
						queryCondition.setQueryString(sql.toString());
						dam.exeUpdate(queryCondition);
					}
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
