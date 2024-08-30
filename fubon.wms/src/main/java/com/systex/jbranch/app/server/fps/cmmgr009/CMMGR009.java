package com.systex.jbranch.app.server.fps.cmmgr009;

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
 * 軌跡紀錄查詢-參數異動	Notics： 修改歷程及說明
 * @author 施陽欽
 * @date 2009-10-27
 * @modifyDate 2009-10-28
 * @since Version
 * @spec 修改註記 	V0.1	2009/10/19	初版	徐禮光
 *
 */
@Component("cmmgr009")
@Scope("request")
public class CMMGR009 extends BizLogic {
	private CMMGR009InputVO cMMGR009InputVO_obj = null;
	private DataAccessManager dam_obj = null;

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
		cMMGR009InputVO_obj = (CMMGR009InputVO) body;

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
		cMMGR009InputVO_obj = (CMMGR009InputVO) body;

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
				String paramType_s = map.get("DATA1") == null ? "" : map.get("DATA1").toString();
				String ptypeName_s = map.get("DATA2") == null ? "" : map.get("DATA2").toString();
				String paramCode_s = map.get("DATA3") == null ? "" : map.get("DATA3").toString();
				String paramName_s = map.get("DATA4") == null ? "" : map.get("DATA4").toString();
				String effectDate_s = map.get("DATA5") == null ? "" : map.get("DATA5").toString();
				String lastUpdate_s = map.get("LASTUPDATE") == null ? "" : map.get("LASTUPDATE").toString();

				Object[] csv_obj = { "=\"" + branchId_s + "\"","=\"" + workStationId_s + "\"",teller_s,tellerName_s,paramType_s,ptypeName_s,paramCode_s,paramName_s,setDateS(effectDate_s),setDate(lastUpdate_s) };

				csv_list.add(csv_obj);
			}

			CSVUtil csv = new CSVUtil();

			// 設定表頭
			csv.setHeader(new String[] { "分行別", "工作站代號",	"櫃員代號", "櫃員名稱", "參數ID", "參數名稱", "CODE ID", "CODE值", "生效日", "最後變更日" });
			// 添加明細的List
			csv.addRecordList(csv_list);

			// 執行產生csv并收到該檔的url
			String url = csv.generateCSV();

			// 將url送回FlexClient存檔
			notifyClientToDownloadFile(url, "CMMGR009.csv");
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
		SimpleDateFormat sDateS = new SimpleDateFormat("yyyyMMdd");
		String tipBranchId = cMMGR009InputVO_obj.getTipBranchId().trim();
		String tipWorkStationId = cMMGR009InputVO_obj.getTipWorkStationId().trim();
		String tipTeller = cMMGR009InputVO_obj.getTipTeller().trim();
		String tipParamType = cMMGR009InputVO_obj.getTipParamType().trim();
		String tipPtypeName = cMMGR009InputVO_obj.getTipPtypeName().trim();
		String tipParamCode = cMMGR009InputVO_obj.getTipParamCode().trim();
		String tipParamName = cMMGR009InputVO_obj.getTipParamName().trim();
		Date dtfEffectDate = cMMGR009InputVO_obj.getDtfEffectDate();
		Date dtfLastUpdate = cMMGR009InputVO_obj.getDtfLastUpdate();
		
		ArrayList<String> sql_list = new ArrayList<String>();;
		StringBuffer sql_sb = new StringBuffer();
		sql_sb
				.append("select IT01.*, IT02.NAME as TELLERNAME"
					+ " from TBSYSPARALOG IT01"
					+ " left join TBSYSUSER IT02 on IT02.TELLERID = IT01.TELLER where 1=1 ");

		if (tipBranchId != null && !tipBranchId.equals("")) {
			sql_sb.append(" and IT01.BRANCHID like ? ");
			sql_list.add("%" + tipBranchId + "%");
		}
		if (tipWorkStationId != null && !tipWorkStationId.equals("")) {
			sql_sb.append(" and IT01.WORKSTATIONID like ? ");
			sql_list.add("%" + tipWorkStationId + "%");
		}
		if (tipTeller != null && !tipTeller.equals("")) {
			sql_sb.append(" and IT01.TELLER like ? ");
			sql_list.add("%" + tipTeller + "%");
		}
		if (tipParamType != null && !tipParamType.equals("")) {
			sql_sb.append(" and IT01.DATA1 like ? ");
			sql_list.add("%" + tipParamType + "%");
		}
		if (tipPtypeName != null && !tipPtypeName.equals("")) {
			sql_sb.append(" and IT01.DATA2 like ? ");
			sql_list.add("%" + tipPtypeName + "%");
		}
		if (tipParamCode != null && !tipParamCode.equals("")) {
			sql_sb.append(" and IT01.DATA3 like ? ");
			sql_list.add("%" + tipParamCode + "%");
		}
		if (tipParamName != null && !tipParamName.equals("")) {
			sql_sb.append(" and IT01.DATA4 like ? ");
			sql_list.add("%" + tipParamName + "%");
		}
		if (dtfEffectDate != null && !"".equals(dtfEffectDate)) {
			sql_sb.append(" and IT01.DATA5 like ? ");
			sql_list.add(sDateS.format(dtfEffectDate));
		}
		if (dtfLastUpdate != null && !"".equals(dtfLastUpdate)) {
			sql_sb.append(" and IT01.LASTUPDATE >= ? and IT01.LASTUPDATE <= ? ");
			sql_list.add(sDateF.format(dtfLastUpdate) + " 00:00:00.0");
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
				cMMGR009InputVO_obj.getCurrentPageIndex() + 1,
				cMMGR009InputVO_obj.getPageCount());

		// 將資料傳回client端
		if (data_list.size() != 0) {
			CMMGR009OutputVO outputVO = new CMMGR009OutputVO();
			outputVO.setCurrentPageIndex(cMMGR009InputVO_obj
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
	 * 西元年轉民國年(D2)
	 * @param date_s
	 * @return
	 */
	private String setDateS(String date_s) {
		String year_s = "";
		String month_s = "";
		String day_s = "";
		if (date_s.length() >= 8){
			year_s = String.valueOf((Integer.valueOf(date_s.substring(0, 4)) - 1911));
			month_s = date_s.substring(4, 6);
			day_s = date_s.substring(6, 8);
			return year_s + "/" + month_s + "/" + day_s;
		}else{
			return date_s;
		}
	}

	/**
	 * 西元年轉民國年(D1)
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
