package com.systex.jbranch.app.server.fps.cmmgr013;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.cmmgr008.CMMGR008OutputVO;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.report.ReportIF;
import com.systex.jbranch.platform.common.report.factory.ReportFactory;
import com.systex.jbranch.platform.common.report.generator.ReportGeneratorIF;
import com.systex.jbranch.platform.common.report.reportdata.ReportData;
import com.systex.jbranch.platform.common.report.reportdata.ReportDataIF;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * 使用者角色權限查詢	Notics： 修改歷程及說明
 * @author 施陽欽
 * @date 2009-10-29
 * @modifyDate 2009-10-30
 * @since Version
 * @spec 修改註記 	V0.1	2009/10/24	初版	余嘉雯
 *
 */
@Component("cmmgr013")
@Scope("request")
public class CMMGR013 extends BizLogic {
	private CMMGR013InputVO inVO = null;
	private DataAccessManager dam_obj = null;

	/**
	 * 查询功能
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void inquire(Object body, IPrimitiveMap<?> header)
			throws JBranchException {
		// 取得畫面資料
		inVO = (CMMGR013InputVO) body;

		// 商業邏輯
		findFromDB();
	}

	/**
	 * 匯出所有DataGrid顯示資料
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void export(Object body, IPrimitiveMap<?> header)
			throws JBranchException {
		// 取得畫面資料
		inVO = (CMMGR013InputVO) body;

		// 從數據庫中查找所需的數據
		dam_obj = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam_obj.getQueryCondition();

		// 設置查詢條件
		queryCondition = setQuerySQL(queryCondition);

		List<Map<String, Object>> query_list = dam_obj.exeQuery(queryCondition);
		query_list = joinFunctionId(query_list);
		// 創建存放明細的List
		List<Object[]> csv_list = new ArrayList<Object[]>();

		if (query_list.size() > 0) {

			for (int index_i = 0; index_i < query_list.size(); index_i++) {
				Map<String, Object> map = query_list.get(index_i);
				String deptID = map.get("DEPT_ID") == null ? "" : map.get("DEPT_ID").toString();
				String empID = map.get("EMP_ID") == null ? "" : map.get("EMP_ID").toString();
				String empName = map.get("EMP_NAME") == null ? "" : map.get("EMP_NAME").toString();
				String roleID = map.get("ROLE_ID") == null ? "" : map.get("ROLE_ID").toString();
				String roleName = map.get("ROLE_NAME") == null ? "" : map.get("ROLE_NAME").toString();
				String priID = map.get("PRI_ID") == null ? "" : map.get("PRI_ID").toString();
				String priName = map.get("PRI_NAME") == null ? "" : map.get("PRI_NAME").toString();
				String txnCode = map.get("ITEM_ID") == null ? "" : map.get("ITEM_ID").toString();
				String txnName = map.get("TXN_NAME") == null ? "" : map.get("TXN_NAME").toString();
				String extend3 = map.get("EXTEND3") == null ? "" : map.get("EXTEND3").toString();
//				XmlInfo xmlInfo = new XmlInfo();
//				Hashtable<?, ?> hashEXTEND3 = xmlInfo.getVariable("TBSYSSECUROLE.EXTEND3", FormatHelper.FORMAT_3);
//				extend3 = hashEXTEND3.get(extend3).toString();

				String maintenance = map.get("maintenance") == null ? "" : map.get("maintenance").toString();
				String query = map.get("query") == null ? "" : map.get("query").toString();
				String print = map.get("print") == null ? "" : map.get("print").toString();
				String export = map.get("export") == null ? "" : map.get("export").toString();
				if (Boolean.valueOf(maintenance)) {
					maintenance = "V";
				}else{
					maintenance = "";
				}
				if (Boolean.valueOf(query)) {
					query = "V";
				}else{
					query = "";
				}
				if (Boolean.valueOf(print)) {
					print = "V";
				}else{
					print = "";
				}
				if (Boolean.valueOf(export)) {
					export = "V";
				}else{
					export = "";
				}
				
				Object[] csv_obj = { "=\"" + deptID + "\"", empID, empName,
						roleID, roleName, priID, priName, txnCode, txnName,
						maintenance, query, print, export };

				csv_list.add(csv_obj);
			}

			CSVUtil csv = new CSVUtil();

			// 設定表頭
			csv.setHeader(new String[] { "分行代號", "員工編號", "員工姓名", "角色代號",
					"角色名稱", "群組代號", "群組名稱", "功能代碼", "功能名稱", "維護", "查詢", "列印",
					"匯出" });
			// 添加明細的List
			csv.addRecordList(csv_list);

			// 執行產生csv并收到該檔的url
			String url = csv.generateCSV();

			// 將url送回FlexClient存檔
			notifyClientToDownloadFile(url, "CMMGR013.csv");
		}
	}
	
	/**
	 * 接收參數，列出報表
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void report(Object body, IPrimitiveMap<?> header)
			throws JBranchException {
		inVO = (CMMGR013InputVO) body;
		String cmbBRCHID = inVO.getCmbBRCHID().trim();
		String empID = inVO.getEmpID().trim();
		int queryMode = inVO.getRadioChange();
		// 取得畫面資料
		inVO = (CMMGR013InputVO) body;

		// 從數據庫中查找所需的數據
		dam_obj = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam_obj.getQueryCondition();

		// 設置查詢條件
		queryCondition = setQuerySQL(queryCondition);

		List<Map<String, Object>> query_list = dam_obj.exeQuery(queryCondition);
		query_list = joinFunctionId(query_list);

        ReportIF report = null;
        String reportID_s = "R1";
		if (queryMode == 1) {
			reportID_s = "R1";
		} else if (queryMode == 2) {
			reportID_s = "R2";
		}
		String txnCode_s = "CMMGR013";
		
		ReportDataIF reportData = new ReportData();//取得傳輸資料給report模組的instance
		ReportGeneratorIF generator = ReportFactory.getGenerator();//取得產生PDF檔的instance

		// 創建存放明細的List
		List<Map> pdfList = new ArrayList<Map>();
		if (query_list.size() > 0) {
			for (int index_i = 0; index_i < query_list.size(); index_i++) {
				Map<String, Object> map = query_list.get(index_i);
				Map<String, Object> dataMap = new HashMap<String, Object>();
				dataMap.put("BRCHID", map.get("BRCHID") == null ? "" : map.get("BRCHID").toString());
				dataMap.put("EMP_ID", map.get("EMP_ID") == null ? "" : map.get("EMP_ID").toString());
				dataMap.put("NAME", map.get("NAME") == null ? "" : map.get("NAME").toString());
				dataMap.put("ROLEID", map.get("ROLEID") == null ? "" : map.get("ROLEID").toString());
				dataMap.put("PRI_ID", map.get("PRIVILEGEID") == null ? "" : map.get("PRIVILEGEID").toString());
				dataMap.put("PRI_NAME", map.get("PRINAME") == null ? "" : map.get("PRINAME").toString());
				dataMap.put("ITEMID", map.get("ITEMID") == null ? "" : map.get("ITEMID").toString());
				dataMap.put("TXNNAME", map.get("TXNNAME") == null ? "" : map.get("TXNNAME").toString());
				
				String extend3_s = map.get("EXTEND3") == null ? "" : map.get("EXTEND3").toString();
				XmlInfo xmlInfo = new XmlInfo();
				Hashtable<?, ?> hashEXTEND3 = xmlInfo.getVariable("TBSYSSECUROLE.EXTEND3", FormatHelper.FORMAT_3);
				extend3_s = hashEXTEND3.get(extend3_s).toString();
				dataMap.put("EXTEND3", extend3_s);
				
				String maintenance_s = map.get("maintenance") == null ? "" : map.get("maintenance").toString();
				String query_s = map.get("query") == null ? "" : map.get("query").toString();
				String print_s = map.get("print") == null ? "" : map.get("print").toString();
				String export_s = map.get("export") == null ? "" : map.get("export").toString();
				if (Boolean.valueOf(maintenance_s)) {
					maintenance_s = "V";
				}else{
					maintenance_s = "";
				}
				dataMap.put("maintenance", maintenance_s);
				if (Boolean.valueOf(query_s)) {
					query_s = "V";
				}else{
					query_s = "";
				}
				dataMap.put("query", query_s);
				if (Boolean.valueOf(print_s)) {
					print_s = "V";
				}else{
					print_s = "";
				}
				dataMap.put("print", print_s);
				if (Boolean.valueOf(export_s)) {
					export_s = "V";
				}else{
					export_s = "";
				}
				dataMap.put("export", export_s);

				pdfList.add(dataMap);
			}

			// 設定表頭
			//csv.setHeader(new String[] { "分行代號", "員工編號", "員工姓名", "角色代號", "角色名稱","形態", "功能代碼", "功能名稱", "維護", "查詢", "列印", "匯出" });
		}

		SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		Date currentTime = new Date(); 
		
//		reportData.addParameter("ID", DataAdapterUtil.getUserID());
		reportData.addParameter("DATE_TIME", formatDate.format(currentTime));
		reportData.addParameter("BRANCH_ID", inVO.getCmbBRCHID().trim());
		reportData.addParameter("TELLER_ID", inVO.getEmpID().trim());
		reportData.addRecordList("Script Mult Data Set",pdfList);
		
		//將資料送給REPORT模組產生PDF，並得到給Flex Client載入其PDF的URL
		report = generator.generateReport(txnCode_s, reportID_s, reportData);
		sendRtnReport(report.getLocation());
	}
	
	/**
	 * 設置查詢SQL
	 * 
	 * @param queryCondition
	 * @return queryCondition
	 */
	private QueryConditionIF setQuerySQL(QueryConditionIF queryCondition) {

		String cmbBRCHID = inVO.getCmbBRCHID().trim();
		String empID = inVO.getEmpID().trim();
		int queryMode = inVO.getRadioChange();
		String cmbRoleID = inVO.getCmbRoleID().trim();

		ArrayList<String> sql_list = new ArrayList<String>();
		StringBuffer sql_sb = new StringBuffer();
		
		sql_sb.append("select distinct U.DEPT_ID, U.EMP_ID, U.EMP_NAME, U.ROLEID, U.ROLENAME,"
				+ " U.PRIVILEGEID, U.PRINAME, U.ITEMID, U.EXTEND3, U.TXNNAME"
				+ " from (select m.DEPT_ID, m.EMP_ID, m.EMP_NAME,"
				+ " sr.ROLEID, sr.NAME as ROLENAME, sr.EXTEND3,"
				+ " sp.PRIVILEGEID, sp.NAME as PRINAME,"
				+ " spf.ITEMID, spf.FUNCTIONID,"
				+ " t.TXNNAME"
				+ " from TBORG_MEMBER m"
				+ " left outer join TBORG_MEMBER_ROLE mr on mr.EMP_ID = m.EMP_ID"
				+ " left outer join TBSYSSECUROLE sr on sr.ROLEID = mr.ROLE_ID"
				+ " left outer join TBSYSSECUROLPRIASS srp on srp.ROLEID = sr.ROLEID"
				+ " left outer join TBSYSSECUPRI sp on sp.PRIVILEGEID = srp.PRIVILEGEID"
				+ " left outer join TBSYSSECUPRIFUNMAP spf on spf.PRIVILEGEID =  srp.PRIVILEGEID"
				+ " left outer join TBSYSTXN t on t.TXNCODE = spf.ITEMID"
				+ " where mr.ROLE_ID IS NOT NULL ");
		
		if (queryMode == 1 && empID != null && !empID.equals("")) {
			sql_sb.append(" and m.EMP_ID like ? ");
			sql_list.add("%" + empID + "%");
		} else if (queryMode == 2 && cmbRoleID != null && !cmbRoleID.equals("")) {
			sql_sb.append(" and sr.ROLEID like ? ");
			sql_list.add("%" + cmbRoleID + "%");
		}
		
		if (cmbBRCHID != null && !cmbBRCHID.equals("")) {
			sql_sb.append(" and m.DEPT_ID like ? ");
			sql_list.add("%" + cmbBRCHID + "%");
		}
		
		sql_sb.append(") U order by U.DEPT_ID, U.EMP_ID, U.ROLEID, U.PRIVILEGEID, U.ITEMID");
		queryCondition.setQueryString(sql_sb.toString());
		for (int sql_i = 0; sql_i < sql_list.size(); sql_i ++) {
			queryCondition.setString(sql_i + 1, sql_list.get(sql_i));
		}

		return queryCondition;
	}

	/**
	 * 查詢DB
	 * 
	 * @throws JBranchException
	 */
	private void findFromDB() throws JBranchException {
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition();

		queryCondition = setQuerySQL(queryCondition);

		ResultIF data_list = dam.executePaging(queryCondition,
				inVO.getCurrentPageIndex() + 1,
				inVO.getPageCount());
		
		// 將資料傳回client端
		if (data_list.size() != 0) {
			CMMGR013OutputVO outputVO = new CMMGR013OutputVO();
			outputVO.setCurrentPageIndex(inVO
					.getCurrentPageIndex());
			outputVO.setDataList(joinFunctionId(data_list));
			outputVO.setTotalPage(data_list.getTotalPage());
			outputVO.setTotalRecord(data_list.getTotalRecord());
			sendRtnObject(outputVO);
		} else {
			throw new APException("ehl_01_common_009");
		}
	}

	/**
	 * 取得FUNCTIONID並合併至同一筆資料中
	 * @param list
	 * @return
	 * @throws JBranchException
	 */
	private List<Map<String, Object>> joinFunctionId(List<Map<String,Object>> list) throws JBranchException {
		dam_obj = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam_obj.getQueryCondition();
		
		List<Map<String, Object>> ret_list = new ArrayList<Map<String,Object>>();
		for (int index_i = 0; index_i < list.size(); index_i ++){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("DEPT_ID", list.get(index_i).get("DEPT_ID"));
			map.put("EMP_ID", list.get(index_i).get("EMP_ID"));
			map.put("EMP_NAME", list.get(index_i).get("EMP_NAME"));
			map.put("ROLE_ID", list.get(index_i).get("ROLEID"));
			map.put("ROLE_NAME", list.get(index_i).get("ROLENAME"));
			map.put("PRI_ID", list.get(index_i).get("PRIVILEGEID"));
			map.put("PRI_NAME", list.get(index_i).get("PRINAME"));
			map.put("ITEM_ID", list.get(index_i).get("ITEMID"));
			map.put("TXN_NAME", list.get(index_i).get("TXNNAME"));
			map.put("EXTEND3", list.get(index_i).get("EXTEND3"));

			map.put("query", false);
			map.put("export", false);
			map.put("print", false);
			map.put("maintenance", false);
			map.put("watermark", false);
			map.put("security", false);
			map.put("confirm", false);

			queryCondition.setQueryString("select FUNCTIONID"
					+ " from TBSYSSECUPRIFUNMAP"
					+ " where PRIVILEGEID = ? and ITEMID = ? ");
			if (list.get(index_i).get("PRIVILEGEID") != null) {
				queryCondition.setString(1, list.get(index_i)
						.get("PRIVILEGEID").toString());
			} else {
				queryCondition.setString(1, "");
			}
			if (list.get(index_i).get("ITEMID") != null) {
				queryCondition.setString(2, list.get(index_i).get("ITEMID")
						.toString());
			} else {
				queryCondition.setString(2, "");
			}

			List<Map<String, Object>> functionId_list = new ArrayList<Map<String,Object>>();
			functionId_list = dam_obj.exeQuery(queryCondition);
			for (int funId_i = 0; funId_i < functionId_list.size(); funId_i ++ ){
				String functionId_s = functionId_list.get(funId_i).get("FUNCTIONID").toString();
				map.put(functionId_s, true);
			}
			ret_list.add(map);
		}
		return ret_list;
	}

	// 取得角色清單
	public void getRoleList(Object body, IPrimitiveMap<?> header)
			throws JBranchException {
		dam_obj = this.getDataAccessManager();
		QueryConditionIF condition = dam_obj.getQueryCondition();
		StringBuffer sql = new StringBuffer();

		sql.append("select ROLEID as DATA, NAME as LABEL from TBSYSSECUROLE");
		condition.setQueryString(sql.toString());

		List<Map<String, Object>> list = dam_obj.exeQuery(condition);

		CMMGR008OutputVO outputVO = new CMMGR008OutputVO();
		outputVO.setRoleList(list);
		sendRtnObject(outputVO);
	}
	
}
