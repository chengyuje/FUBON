package com.systex.jbranch.app.server.fps.pms403;

import java.sql.Timestamp;
import java.text.NumberFormat;
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

import com.systex.jbranch.app.common.fps.table.TBPMS_NS_MONTHLY_TRADEPK;
import com.systex.jbranch.app.common.fps.table.TBPMS_NS_MONTHLY_TRADEVO;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * Copy Right Information :<br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description :禁銷戶交易明細月報<br>
 * Comments Name : PMS403.java<br>
 * Author : Frank<br>
 * Date :2016/05/17 <br>
 * Version : 1.0 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年01月30日<br>
 */

@Component("pms403")
@Scope("request")
public class PMS403 extends BizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PMS403.class);

	public void queryData(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS403InputVO inputVO = (PMS403InputVO) body;
		PMS403OutputVO outputVO = new PMS403OutputVO();
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition();
		ArrayList<String> sql_list = new ArrayList<String>();
		StringBuffer sql = new StringBuffer("select ROWNUM, t.* from (");
		sql.append("select a.CREATETIME,a.YEARMON, a.BRANCH_NBR, a.CUST_NAME, a.CUST_ID, a.AO_CODE, ");
		sql.append("a.EMP_ID, a.EMP_NAME, a.PRD_TYPE, a.CERT_NBR, a.PRD_ID, ");
		sql.append("a.PRD_NAME, TO_CHAR(a.MAKE_DATE, 'yyyy-MM-dd HH24:MI:SS') AS MAKE_DATE, a.CRCY_TYPE, a.COST_ORGD, ");
		sql.append("a.STATUS, a.ROR, a.RISK_ATTR, a.NOTE ");
		sql.append("from TBPMS_NS_MONTHLY_TRADE a ");
		sql.append("where ROWNUM<=2000 ");
		if (inputVO.getDataMonth() != null
				&& !"".equals(inputVO.getDataMonth())) {
			sql.append("and a.YEARMON = ? ");
			sql_list.add(inputVO.getDataMonth());
		}
		// 通路別
//		if (inputVO.getChannel() != null && !"".equals(inputVO.getChannel())) {
//			sql.append("and a.ACCESS_TYPE = ? ");
//			sql_list.add(inputVO.getChannel());
//		}
//		// 投資方式
//		if (inputVO.getInvType() != null && !"".equals(inputVO.getInvType())) {
//			sql.append("and a.INVESTMENT = ? ");
//			sql_list.add(inputVO.getInvType());
//		}
//		// 區域中心
//		if (inputVO.getRegion_center_id() != null && !inputVO.getRegion_center_id().equals("")) {
//			sql.append("and a.REGION_CENTER_ID = ? ");
//			sql_list.add(inputVO.getRegion_center_id());
//		}
//		// 營運區
//		if (inputVO.getBranch_area_id() != null && !inputVO.getBranch_area_id().equals("")) {
//			sql.append("and a.BRANCH_AREA_ID = ? ");
//			sql_list.add(inputVO.getBranch_area_id());
//		}
//		// 分行
//		if (inputVO.getBranch_nbr() != null && !inputVO.getBranch_nbr().equals("")) {
//			sql.append("and a.BRANCH_NBR = ? ");
//			sql_list.add(inputVO.getBranch_nbr());
//		}
		
		// by Willis 20171024 此條件因為發現組織換區有異動(例如:東寧分行在正式環境10/1從西台南區換至東台南區)，跟之前組織對應會有問題，改為對應目前最新組織分行別
		// 分行
		if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
			sql.append("and a.BRANCH_NBR = ? ");
			sql_list.add(inputVO.getBranch_nbr());
		// 營運區	
		}else if (StringUtils.isNotBlank(inputVO.getBranch_area_id())){
			sql.append("and a.BRANCH_NBR in ( ");
			sql.append("select BRANCH_NBR from VWORG_DEFN_BRH where DEPT_ID = ? ) ");
			sql_list.add(inputVO.getBranch_area_id());
	    // 區域中心	
		}else if (StringUtils.isNotBlank(inputVO.getRegion_center_id())){
			sql.append("and a.BRANCH_NBR in ( ");
			sql.append("select BRANCH_NBR from VWORG_DEFN_BRH where DEPT_ID = ? ) ");
			sql_list.add(inputVO.getRegion_center_id());
		}
		
		// 理專(FOR 可視範圍)
		if (inputVO.getEmp_id() != null && !"".equals(inputVO.getEmp_id())) {
			sql.append("and a.EMP_ID = ? ");
			sql_list.add(inputVO.getEmp_id());
		}
		sql.append(" order by a.BRANCH_NBR, a.CUST_ID, a.CERT_NBR, a.PRD_ID) t ");

		condition.setQueryString(sql.toString());
		for (int sql_i = 0; sql_i < sql_list.size(); sql_i++) {
			condition.setString(sql_i + 1, sql_list.get(sql_i));
		}
		outputVO.setTotalList(dam.exeQuery(condition));
		ResultIF list = dam.executePaging(condition,
				inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
		if (list.size() > 0) {
			int totalPage = list.getTotalPage();
			outputVO.setTotalPage(totalPage);
			outputVO.setResultList(list); 
			outputVO.setTotalRecord(list.getTotalRecord());
			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());
			System.err.print("getOp_id="+inputVO.getOp_id());
			sendRtnObject(outputVO);
		} else {
			throw new APException("ehl_01_common_009");
		}
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		
		
	}

	/* ==== 【儲存】更新資料 ======== */
	public void save(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		
		Timestamp stamp = new Timestamp(System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		PMS403InputVO inputVO = (PMS403InputVO) body;
		DataAccessManager dam = this.getDataAccessManager();

		for (Map<String, Object> map : inputVO.getList()) {
			for (Map<String, Object> map2 : inputVO.getList2()) {
				
				 boolean one=map.get("NOTE")==null;
				 boolean two=map2.get("NOTE")==null;
				
				 if (map.get("YEARMON").equals(map2.get("YEARMON")) && map.get("BRANCH_NBR").equals(map2.get("BRANCH_NBR"))
						&& map.get("CERT_NBR").equals(map2.get("CERT_NBR")) && map.get("MAKE_DATE").equals(map2.get("MAKE_DATE")) ) {
					 
					boolean three= one&&two;
				    boolean four=false; 	
					if(one==false&&two==false){
						
						four=map.get("NOTE").equals(map2.get("NOTE"));
					}     
					   
					if(three==false||four==false){
						
						Date sdfMakeDate = sdf.parse(map.get("MAKE_DATE").toString());
						Timestamp tsMakeDate = new Timestamp(sdfMakeDate.getTime());
						
						TBPMS_NS_MONTHLY_TRADEPK pk = new TBPMS_NS_MONTHLY_TRADEPK();
						pk.setYEARMON(map.get("YEARMON").toString());
						pk.setCERT_NBR(map.get("CERT_NBR").toString());
						pk.setBRANCH_NBR(map.get("BRANCH_NBR").toString());
						pk.setMAKE_DATE(tsMakeDate);
						TBPMS_NS_MONTHLY_TRADEVO paramVO = (TBPMS_NS_MONTHLY_TRADEVO) dam.findByPKey(TBPMS_NS_MONTHLY_TRADEVO.TABLE_UID, pk);
					
						if(four==false){
							paramVO.setLastupdate(stamp);
						}
					
						if(three==true){
							
							paramVO.setNOTE("null");
						}else if(!map.get("NOTE").equals(map2.get("NOTE"))){
							paramVO.setNOTE(map.get("NOTE").toString());
							dam.update(paramVO);
						}	
						
					 }
				 }

			}
		}
		sendRtnObject(null);
	}

	/* === 產出CSV==== */
	public void export(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS403OutputVO outputVO = (PMS403OutputVO) body;

		List<Map<String, Object>> list = outputVO.getTotalList();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String fileName = "禁銷戶交易明細月報_" + sdf.format(new Date()) + ".csv";
		List listCSV = new ArrayList();
		for (Map<String, Object> map : list) {
			String[] records = new String[18];
			int i = 0;
//			records[i] = checkIsNull(map, "ROWNUM").substring(0, 1); // 序號
			records[i] =((int)Double.parseDouble(checkIsNull(map, "ROWNUM").toString()))+""; // 序號 - 去小數點
			records[++i] = checkIsNull(map, "BRANCH_NBR"); // 分行代碼
			records[++i] = checkIsNull(map, "CUST_NAME"); // 客戶姓名
			records[++i] = checkIsNull(map, "CUST_ID"); // 客戶ID
			records[++i] = checkIsNull(map, "AO_CODE"); // AO Code
			records[++i] = checkIsNull(map, "EMP_ID"); // 理專員編
			records[++i] = checkIsNull(map, "EMP_NAME"); // 員工姓名
			records[++i] = checkIsNull(map, "PRD_TYPE"); // 承做商品類型
			records[++i] = checkIsNull(map, "CERT_NBR"); // 憑證編號
			records[++i] = checkIsNull(map, "PRD_ID"); // 商品代碼
			records[++i] = checkIsNull(map, "PRD_NAME"); // 商品名稱
			records[++i] = checkIsNull(map, "MAKE_DATE"); // 承做日
			records[++i] = checkIsNull(map, "CRCY_TYPE"); // 投資幣別
			records[++i] = currencyFormat(map, "COST_ORGD"); // 原幣投資成本
			records[++i] = checkIsNull(map, "STATUS"); // 持有狀態
			records[++i] = checkIsNull(map, "ROR"); // 報酬率%
			records[++i] = checkIsNull(map, "RISK_ATTR"); // 風險屬性
			records[++i] = checkIsNull(map, "NOTE"); // 備註
			listCSV.add(records);
		}
		// header
		String[] csvHeader = new String[18];
		int j = 0;
		csvHeader[j] = "序號";
		csvHeader[++j] = "分行代碼";
		csvHeader[++j] = "客戶姓名";
		csvHeader[++j] = "客戶ID";
		csvHeader[++j] = "AO Code";
		csvHeader[++j] = "理專員編";
		csvHeader[++j] = "員工姓名";
		csvHeader[++j] = "承做商品類型";
		csvHeader[++j] = "憑證編號";
		csvHeader[++j] = "商品代碼";
		csvHeader[++j] = "商品名稱";
		csvHeader[++j] = "承做日";
		csvHeader[++j] = "投資幣別";
		csvHeader[++j] = "原幣投資成本";
		csvHeader[++j] = "持有狀態";
		csvHeader[++j] = "報酬率%";
		csvHeader[++j] = "風險屬性";
		csvHeader[++j] = "備註";

		CSVUtil csv = new CSVUtil();
		csv.setHeader(csvHeader);
		csv.addRecordList(listCSV);
		String url = csv.generateCSV();
		notifyClientToDownloadFile(url, fileName);
		this.sendRtnObject(null);
	}

	/**
	 * 檢查Map取出欄位是否為Null
	 * 
	 * @param map
	 * @return String
	 */
	private String checkIsNull(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key)))
				&& map.get(key) != null) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}

	// 處理貨幣格式
	private String currencyFormat(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key)))
				&& map.get(key) != null) {
			NumberFormat nf = NumberFormat.getCurrencyInstance();
			return nf.format(map.get(key));
		} else
			return "$0.00";
	}

}