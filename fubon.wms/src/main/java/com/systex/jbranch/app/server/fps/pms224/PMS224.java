package com.systex.jbranch.app.server.fps.pms224;

import java.text.NumberFormat;
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

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.util.IPrimitiveMap;
/**
 * 
 * Copy Right Information : <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description :保險新承做明細表 Controller <br>
 * Comments Name : PMS224.java<br>
 * Author :frank<br>
 * Date :2016年07月11日 <br>
 * Version : 1.01 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年02月01日<br>
 */

@Component("pms224")
@Scope("request")
public class PMS224 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;	
	private Logger logger = LoggerFactory.getLogger(PMS224.class);
	
	public void queryData(Object body, IPrimitiveMap header) throws JBranchException {		
		PMS224InputVO inputVO = (PMS224InputVO) body;
		PMS224OutputVO outputVO = new PMS224OutputVO();		
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition();
		ArrayList<String> sql_list = new ArrayList<String>();
		StringBuffer sql = new StringBuffer("SELECT ROWNUM, T.* FROM ( ");
		sql.append("SELECT BRANCH_AREA_NAME, BRANCH_NAME, ");
		sql.append("EMP_ID, EMP_NAME, ACH_YR_MN, ");
		sql.append("POLICY_NO, POLICY_SEQ, APPL_NAME, ");
		sql.append("EFF_YY, EFF_MM, EFF_DD, ");
		sql.append("INS_TYPE_CODE, INS_TYPE_NOTE, ");
		sql.append("PREM, RAISE_FINAL ");		
		sql.append("FROM TBPMS_INS_NEW_RPT ");
		sql.append("WHERE 1=1 ");
		//資料統計日期
		if (inputVO.getDataMonth() != null && !inputVO.getDataMonth().equals("")){
			sql.append("and ACH_YR_MN = ? ");
			sql_list.add(inputVO.getDataMonth());
		}		
		//營運區
		if(inputVO.getOp_id() != null && !inputVO.getOp_id().equals("")){
			sql.append("and BRANCH_AREA_ID = ? ");
			sql_list.add(inputVO.getOp_id());
		}
		//分行
		if(inputVO.getBr_id() != null && !inputVO.getBr_id().equals("")){
			sql.append("and BRANCH_NBR = ? ");
			sql_list.add(inputVO.getBr_id());
		}
		//理專員編
		if(inputVO.getEmp_id() != null && !inputVO.getEmp_id().equals("")){
			sql.append("and EMP_ID = ? ");
			sql_list.add(inputVO.getEmp_id());
		}
		sql.append("order by BRANCH_AREA_ID, BRANCH_NBR ) T ");
		
		condition.setQueryString(sql.toString());
		for (int sql_i = 0; sql_i < sql_list.size(); sql_i ++) {
			condition.setString(sql_i + 1, sql_list.get(sql_i));
		}
		outputVO.setTotalList(dam.exeQuery(condition));
		ResultIF list = dam.executePaging(condition, inputVO
				.getCurrentPageIndex() + 1, inputVO.getPageCount());
		if (list.size() > 0) {
			int totalPage = list.getTotalPage();
			outputVO.setTotalPage(totalPage);
			outputVO.setResultList(list);
			outputVO.setTotalRecord(list.getTotalRecord());

			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());
			sendRtnObject(outputVO);
		} else {
			throw new APException("ehl_01_common_009");
		}
	}
			
	
	/*  === 產出CSV==== */
	public void export(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS224OutputVO outputVO = (PMS224OutputVO) body;		
		
		List<Map<String, Object>> list = outputVO.getTotalList();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String fileName = "保險新承做明細表_" + sdf.format(new Date()) + ".csv"; 
		List listCSV =  new ArrayList();
		for(Map<String, Object> map : list){
			String[] records = new String[16];
			int i = 0;
			records[i]   = checkIsNull(map, "ROWNUM");
			records[++i] = checkIsNull(map, "BRANCH_AREA_NAME");
			records[++i] = checkIsNull(map, "BRANCH_NAME");			
			records[++i] = checkIsNull(map, "EMP_ID");
			records[++i] = checkIsNull(map, "EMP_NAME");
			records[++i] = checkIsNull(map, "ACH_YR_MN");
			records[++i] = checkIsNull(map, "POLICY_NO");
			records[++i] = checkIsNull(map, "POLICY_SEQ");
			records[++i] = checkIsNull(map, "APPL_NAME");
			records[++i] = checkIsNull(map, "EFF_YY");
			records[++i] = checkIsNull(map, "EFF_MM");
			records[++i] = checkIsNull(map, "EFF_DD");
			records[++i] = checkIsNull(map, "INS_TYPE_CODE");
			records[++i] = checkIsNull(map, "INS_TYPE_NOTE");				
			records[++i] = currencyFormat(map, "PREM");
			records[++i] = currencyFormat(map, "RAISE_FINAL");
			
			listCSV.add(records);
		}
		//header
		String [] csvHeader = new String[16];
		int j = 0;
		csvHeader[j]   = "項次";
		csvHeader[++j] = "營運區";
		csvHeader[++j] = "分行別";
		csvHeader[++j] = "專員員工代碼";
		csvHeader[++j] = "專員姓名";
		csvHeader[++j] = "成績年月";
		csvHeader[++j] = "保單號碼";
		csvHeader[++j] = "保單序號";
		csvHeader[++j] = "要保人姓名";
		csvHeader[++j] = "生效日-年";
		csvHeader[++j] = "生效日-月";
		csvHeader[++j] = "生效日-日";
		csvHeader[++j] = "險種代號";
		csvHeader[++j] = "險種說明";			
		csvHeader[++j] = "保費";
		csvHeader[++j] = "加碼FINAL";
		
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
		if(StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null){
			return String.valueOf(map.get(key));
		}else{
			return "";
		}
	}
	//處理貨幣格式
	private String currencyFormat(Map map, String key){		
		if(StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null){
			NumberFormat nf = NumberFormat.getCurrencyInstance();
			return nf.format(map.get(key));										
		}else
			return "0.00";		
	}
}