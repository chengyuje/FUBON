package com.systex.jbranch.app.server.fps.cmmgr008;

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
 * 軌跡紀錄查詢-執行重要功能 Notics： 修改歷程及說明
 * @author 施陽欽
 * @date 2009-10-23
 * @modifyDate 2009-10-26
 * @since Version
 * @spec 修改註記 	V0.1	2009/10/19	初版	徐禮光
 *
 */
@Component("cmmgr008")
@Scope("request")
public class CMMGR008 extends BizLogic {
	private CMMGR008InputVO cMMGR008InputVO_obj = null;
	private DataAccessManager dam = null;

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
		cMMGR008InputVO_obj = (CMMGR008InputVO) body;

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
		cMMGR008InputVO_obj = (CMMGR008InputVO) body;

		// 從數據庫中查找所需的數據
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition();

		// 設置查詢條件
		setQuerySQL(queryCondition);

		List<Map<String, Object>> query_list = dam.executeQuery(queryCondition);

		// 創建存放明細的List
		List<Object[]> csv_list = new ArrayList<Object[]>();

		if (query_list.size() > 0) {

			for (int index_i = 0; index_i < query_list.size(); index_i++) {
				Map<String, Object> map = query_list.get(index_i);
				String txnDateTime_s = map.get("TXNDATETIME") == null ? "" : map.get("TXNDATETIME").toString();
				String brchId_s = map.get("BRCHID") == null ? "" : map.get("BRCHID").toString();
				String wsId_s = map.get("WSID") == null ? "" : map.get("WSID").toString();
				String txnCode_s = map.get("TXNCODE") == null ? "" : map.get("TXNCODE").toString();
				String txnName_s = map.get("TXNNAME") == null ? "" : map.get("TXNNAME").toString();
				String tellerId_s = map.get("TELLERID") == null ? "" : map.get("TELLERID").toString();
				String tellerName_s = map.get("TELLERNAME") == null ? "" : map.get("TELLERNAME").toString();
				String roleId_s = map.get("ROLENAME") == null ? "" : map.get("ROLENAME").toString();
				String customerId_s = map.get("CUSTOMERID") == null ? "" : map.get("CUSTOMERID").toString();
				String customerName_s = map.get("CUSTOMERNAME") == null ? "" : map.get("CUSTOMERNAME").toString();
				String bizcodeName_s = map.get("BIZCODENAME") == null ? "" : map.get("BIZCODENAME").toString();
				String memo_s = map.get("MEMO") == null ? "" : map.get("MEMO").toString();

				Object[] csv_obj = { setDate(txnDateTime_s),"=\"" + brchId_s + "\"","=\"" + wsId_s + "\"","=\"" + txnCode_s + "\"",txnName_s,tellerId_s,tellerName_s,roleId_s,customerId_s,customerName_s,bizcodeName_s,memo_s };

				csv_list.add(csv_obj);
			}

			CSVUtil csv = new CSVUtil();

			// 設定表頭
			csv.setHeader(new String[] { "交易日期時間", "分行別", "工作站代號", "交易代號", "交易名稱",
					"櫃員代號", "櫃員名稱", "角色名稱", "客戶ID", "客戶名稱", "功能名稱", "說明" });
			// 添加明細的List
			csv.addRecordList(csv_list);

			// 執行產生csv并收到該檔的url
			String url = csv.generateCSV();

			// 將url送回FlexClient存檔
			notifyClientToDownloadFile(url, "CMMGR008.csv");
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
		Date dtfStartDate = cMMGR008InputVO_obj.getDtfStartDate();
		Date dtfEndDate = cMMGR008InputVO_obj.getDtfEndDate();
		String tipBrchId = cMMGR008InputVO_obj.getTipBrchId().trim();
		String tipWsId = cMMGR008InputVO_obj.getTipWsId().trim();
		String tipTxnCode = cMMGR008InputVO_obj.getTipTxnCode().trim();
		String tipTellerId = cMMGR008InputVO_obj.getTipTellerId().trim();
		String tipRoleId = cMMGR008InputVO_obj.getTipRoleId().trim();
		String tipCustomerId = cMMGR008InputVO_obj.getTipCustomerId().trim();
		String tipCustomerName = cMMGR008InputVO_obj.getTipCustomerName()
				.trim();
		String tipBizcodeName = cMMGR008InputVO_obj.getTipBizcodeName().trim();
		String tipMemo = cMMGR008InputVO_obj.getTipMemo().trim();

		ArrayList<String> sql_list = new ArrayList<String>();;
		StringBuffer sql_sb = new StringBuffer();
		sql_sb
				.append("select IT01.*, IT02.NAME as ROLENAME, IT03.TXNNAME, IT04.NAME as TELLERNAME"
						+ " from  TBSYSFPSEJ IT01"
						+ " left join TBSYSSECUROLE IT02 on IT02.ROLEID = IT01.ROLEID"
						+ " left join TBSYSTXN IT03 on IT03.TXNCODE = IT01.TXNCODE"
						+ " left join TBSYSUSER IT04 on IT04.TELLERID = IT01.TELLERID where 1=1 ");

		if (dtfStartDate != null && !"".equals(dtfStartDate)) {
			sql_sb.append(" and IT01.TXNDATETIME >= ? ");
			sql_list.add(sDateF.format(dtfStartDate) + " 00:00:00.0");
		}
		if (dtfEndDate != null && !"".equals(dtfEndDate)) {
			sql_sb.append(" and IT01.TXNDATETIME <= ? ");
			sql_list.add(sDateF.format(dtfEndDate) + " 23:59:59.9");
		}
		if (tipBrchId != null && !tipBrchId.equals("")) {
			sql_sb.append(" and IT01.BRCHID like ? ");
			sql_list.add("%" + tipBrchId + "%");
		}
		if (tipWsId != null && !tipWsId.equals("")) {
			sql_sb.append(" and IT01.WSID like ? ");
			sql_list.add("%" + tipWsId + "%");
		}
		if (tipTxnCode != null && !tipTxnCode.equals("")) {
			sql_sb.append(" and IT01.TXNCODE like ? ");
			sql_list.add("%" + tipTxnCode + "%");
		}
		if (tipTellerId != null && !tipTellerId.equals("")) {
			sql_sb.append(" and IT01.TELLERID like ? ");
			sql_list.add("%" + tipTellerId + "%");
		}
		if (tipRoleId != null && !tipRoleId.equals("")) {
			sql_sb.append(" and IT01.ROLEID like ? ");
			sql_list.add("%" + tipRoleId + "%");
		}
		if (tipCustomerId != null && !tipCustomerId.equals("")) {
			sql_sb.append(" and IT01.CUSTOMERID like ? ");
			sql_list.add("%" + tipCustomerId + "%");
		}
		if (tipCustomerName != null && !tipCustomerName.equals("")) {
			sql_sb.append(" and IT01.CUSTOMERNAME like ? ");
			sql_list.add("%" + tipCustomerName + "%");
		}
		if (tipBizcodeName != null && !tipBizcodeName.equals("")) {
			sql_sb.append(" and IT01.BIZCODENAME like ? ");
			sql_list.add("%" + tipBizcodeName + "%");
		}
		if (tipMemo != null && !tipMemo.equals("")) {
			sql_sb.append(" and IT01.MEMO like ? ");
			sql_list.add("%" + tipMemo + "%");
		}

		sql_sb.append(" order by IT01.RECID");

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
				cMMGR008InputVO_obj.getCurrentPageIndex() + 1,
				cMMGR008InputVO_obj.getPageCount());

		// 將資料傳回client端
		if (data_list.size() != 0) {
			CMMGR008OutputVO outputVO = new CMMGR008OutputVO();
			outputVO.setCurrentPageIndex(cMMGR008InputVO_obj
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
		String time_s = "";
		if (date_s.length() >= 19){
			year_s = String.valueOf((Integer.valueOf(date_s.substring(0, 4)) - 1911));
			month_s = date_s.substring(5, 7);
			day_s = date_s.substring(8, 10);
			time_s = date_s.substring(11, 19);
			return year_s + "/" + month_s + "/" + day_s + "-" + time_s;
		}else{
			return date_s;
		}
	}
	
	// 取得角色清單
	public void getRoleList(Object body, IPrimitiveMap header)
			throws JBranchException {
		CMMGR008OutputVO outputVO = new CMMGR008OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF condition = dam.getQueryCondition();
		StringBuffer sql = new StringBuffer();
		sql.append("select ROLE_ID as DATA, ROLE_NAME as LABEL from TBORG_ROLE");
		condition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.executeQuery(condition);
		outputVO.setRoleList(list);
		sendRtnObject(outputVO);
	}
}
