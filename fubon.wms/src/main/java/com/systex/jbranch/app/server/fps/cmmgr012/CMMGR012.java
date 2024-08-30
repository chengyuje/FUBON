package com.systex.jbranch.app.server.fps.cmmgr012;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * 軌跡紀錄查詢-權限異動	Notics： 修改歷程及說明
 * @author 施陽欽
 * @date 2009-10-29
 * @modifyDate 2009-10-29
 * @since Version
 * @spec 修改註記 	V0.1	2009/10/19	初版	徐禮光
 *
 */
@Component("cmmgr012")
@Scope("request")
public class CMMGR012 extends BizLogic {
	private CMMGR012InputVO cMMGR012InputVO_obj = null;
	private DataAccessManager dam_obj = null;

	/**
	 * 初始化查詢畫面
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void init(Object body, IPrimitiveMap header)
			throws JBranchException {
		// 取得畫面資料
		cMMGR012InputVO_obj = (CMMGR012InputVO) body;

		// 商業邏輯
		dam_obj = this.getDataAccessManager();
		QueryConditionIF condition = dam_obj.getQueryCondition();
		condition.setQueryString("select FUNCTIONID, NAME from TBSYSSECUITEMFUN where 1=1");
		List<Map<String, Object>> function_list = new ArrayList<Map<String,Object>>();
		function_list = dam_obj.executeQuery(condition);
		condition.setQueryString("select ROLEID, NAME from TBSYSSECUROLE where 1=1");
		List<Map<String, Object>> role_list = new ArrayList<Map<String,Object>>();
		role_list = dam_obj.executeQuery(condition);
		if (function_list.size() > 0 && role_list.size() > 0){
			CMMGR012OutputVO resultVO = new CMMGR012OutputVO();
			resultVO.setFunctionList(function_list);
			resultVO.setRoleList(role_list);
			sendRtnObject(resultVO);
		} else {
			throw new APException("ehl_01_common_001");
		}
	}

	/**
	 * 查询功能
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void inquire(Object body, IPrimitiveMap header)
			throws JBranchException {
		// 取得畫面資料
		cMMGR012InputVO_obj = (CMMGR012InputVO) body;

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
	public void export(Object body, IPrimitiveMap header)
			throws JBranchException {
		// 取得畫面資料
		cMMGR012InputVO_obj = (CMMGR012InputVO) body;

		// 從數據庫中查找所需的數據
		dam_obj = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam_obj.getQueryCondition();

		// 設置查詢條件
		setQuerySQL(queryCondition);

		List<Map<String, Object>> query_list = dam_obj.executeQuery(queryCondition);

		// 創建存放明細的List
		List<Object[]> csv_list = new ArrayList<Object[]>();

		if (query_list.size() > 0) {

			for (int index_i = 0; index_i < query_list.size(); index_i++) {
				Map<String, Object> map = query_list.get(index_i);
				String branchId_s = map.get("BRANCHID") == null ? "" : map.get("BRANCHID").toString();
				String workStationId_s = map.get("WORKSTATIONID") == null ? "" : map.get("WORKSTATIONID").toString();
				String teller_s = map.get("TELLER") == null ? "" : map.get("TELLER").toString();
				String tellerName_s = map.get("TELLERNAME") == null ? "" : map.get("TELLERNAME").toString();
				String txnCode_s = map.get("DATA1") == null ? "" : map.get("DATA1").toString();
				String txnName_s = map.get("TXNNAME") == null ? "" : map.get("TXNNAME").toString();
				String functionId_s = map.get("FUNCTIONNAME") == null ? "" : map.get("FUNCTIONNAME").toString();
				String roleId_s = map.get("ROLENAME") == null ? "" : map.get("ROLENAME").toString();
				String lastUpdate_s = map.get("LASTUPDATE") == null ? "" : map.get("LASTUPDATE").toString();

				Object[] csv_obj = { "=\"" + branchId_s + "\"","=\"" + workStationId_s + "\"",teller_s,tellerName_s,txnCode_s,txnName_s,functionId_s,roleId_s,setDate(lastUpdate_s) };

				csv_list.add(csv_obj);
			}

			CSVUtil csv = new CSVUtil();

			// 設定表頭
			csv.setHeader(new String[] { "分行別", "工作站代號", "櫃員代號", "櫃員名稱", "角色名稱", "交易代號", "交易名稱", "功能", "角色名稱", "最後變更日" });
			// 添加明細的List
			csv.addRecordList(csv_list);

			// 執行產生csv并收到該檔的url
			String url = csv.generateCSV();

			// 將url送回FlexClient存檔
			notifyClientToDownloadFile(url, "CMMGR012.csv");
		}
	}

	/**
	 * 設置查詢SQL
	 * 
	 * @param queryCondition
	 * @return queryCondition
	 */
	private QueryConditionIF setQuerySQL(QueryConditionIF queryCondition) {

		SimpleDateFormat sDateF = new SimpleDateFormat("yyyy-MM-dd");
		String tipBranchId = cMMGR012InputVO_obj.getTipBranchId().trim();
		String tipWorkStationId = cMMGR012InputVO_obj.getTipWorkStationId().trim();
		String tipTeller = cMMGR012InputVO_obj.getTipTeller().trim();
		String tipTxnCode = cMMGR012InputVO_obj.getTipTxnCode().trim();
		String cmbFunctionId = cMMGR012InputVO_obj.getCmbFunctionId().trim();
		String cmbRoleId = cMMGR012InputVO_obj.getCmbRoleId().trim();
		Date dtfStartDate = cMMGR012InputVO_obj.getDtfStartDate();
		Date dtfLastUpdate = cMMGR012InputVO_obj.getDtfLastUpdate();

		ArrayList<String> sql_list = new ArrayList<String>();
		StringBuffer sql_sb = new StringBuffer();
		sql_sb
				.append("select IT01.*, IT02.TXNNAME, IT03.NAME as ROLENAME, IT04.NAME as FUNCTIONNAME, IT05.EMP_NAME as TELLERNAME"
						+ " from  TBSYSSECULOG IT01"
						+ " left join TBSYSTXN IT02 on IT02.TXNCODE = IT01.DATA1"
						+ " left join TBSYSSECUROLE IT03 on IT03.ROLEID = IT01.DATA3"
						+ " left join TBSYSSECUITEMFUN IT04 on IT04.FUNCTIONID = IT01.DATA2"
						+ " left join TBORG_MEMBER IT05 on IT05.EMP_ID = IT01.TELLER where 1=1 ");

		if (tipBranchId != null && !tipBranchId.equals("")) {
			sql_sb.append(" and IT01.BRANCHID  like ? ");
			sql_list.add("%" + tipBranchId + "%");
		}
		if (tipWorkStationId != null && !tipWorkStationId.equals("")) {
			sql_sb.append(" and IT01.WORKSTATIONID  like ? ");
			sql_list.add("%" + tipWorkStationId + "%");
		}
		if (tipTeller != null && !tipTeller.equals("")) {
			sql_sb.append(" and IT01.TELLER  like ? ");
			sql_list.add("%" + tipTeller + "%");
		}
		if (tipTxnCode != null && !tipTxnCode.equals("")) {
			sql_sb.append(" and IT01.DATA1 like ? ");
			sql_list.add("%" + tipTxnCode + "%");
		}
		if (cmbFunctionId != null && !cmbFunctionId.equals("")) {
			sql_sb.append(" and IT01.DATA2 like ? ");
			sql_list.add("%" + cmbFunctionId + "%");
		}
		if (cmbRoleId != null && !cmbRoleId.equals("")) {
			sql_sb.append(" and IT01.DATA3 like ? ");
			sql_list.add("%" + cmbRoleId + "%");
		}
		if (dtfLastUpdate != null && !"".equals(dtfLastUpdate)) {
			sql_sb.append(" and IT01.LASTUPDATE >= ?  ");
			sql_list.add(sDateF.format(dtfStartDate) + " 00:00:00.0");
		}
		if (dtfLastUpdate != null && !"".equals(dtfLastUpdate)) {
			sql_sb.append(" and IT01.LASTUPDATE <= ? ");
			sql_list.add(sDateF.format(dtfLastUpdate) + " 23:59:59.9");
		}
		sql_sb.append(" order by IT01.LOGINDEX");
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
				cMMGR012InputVO_obj.getCurrentPageIndex() + 1,
				cMMGR012InputVO_obj.getPageCount());

		// 將資料傳回client端
		if (data_list.size() != 0) {
			CMMGR012OutputVO outputVO = new CMMGR012OutputVO();
			outputVO.setCurrentPageIndex(cMMGR012InputVO_obj
					.getCurrentPageIndex());
			outputVO.setDataList(data_list);
			outputVO.setTotalPage(data_list.getTotalPage());
			outputVO.setTotalRecord(data_list.getTotalRecord());
			sendRtnObject(outputVO);
		} else {
			throw new APException("ehl_01_common_001");
		}
	}

	/**
	 * 西元年轉民國年(時間)
	 * @param date_s
	 * @return
	 */
	private String setDate(String date_s) {
		String year_s = "";
		String month_s = "";
		String day_s = "";
		if (date_s.length() >= 10){
			year_s = String.valueOf((Integer.valueOf(date_s.substring(0, 4)) - 1911));
			month_s = date_s.substring(5, 7);
			day_s = date_s.substring(8, 10);
			return year_s + "/" + month_s + "/" + day_s;
		}else{
			return date_s;
		}
	}
}
