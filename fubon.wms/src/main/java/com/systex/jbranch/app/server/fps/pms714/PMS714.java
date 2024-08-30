package com.systex.jbranch.app.server.fps.pms714;
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
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.util.IPrimitiveMap;
/**
 * 
 * Copy Right Information : Forms Syntron <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : 每月保險同意書補簽報表 Controller<br>
 * Comments Name : PMS714.java<br>
 * Author :zhouyiqiong<br>
 * Date :2017年1月11日 <br>
 * Version : 1.01 <br>
 * Editor : zhouyiqiong<br>
 * Editor Date : 2017年1月11日<br>
 */
@Component("pms714")
@Scope("request")
public class PMS714 extends FubonWmsBizLogic
{
	private DataAccessManager dam = null;
	private PMS714InputVO inputVO = null;
	private Logger logger = LoggerFactory.getLogger(PMS714.class);
	/**
	 * 查詢檔案
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException
	{	
		PMS714InputVO inputVO = (PMS714InputVO) body;
		PMS714OutputVO outputVO = new PMS714OutputVO();
		dam = this.getDataAccessManager();
		List roleList = new ArrayList();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			sql.append("  SELECT                                                 ");
			sql.append("         ROW_NUMBER() OVER(ORDER BY BRANCH_AREA_ID, BRANCH_NBR,EMP_ID) AS NUM,");
			sql.append("         T.REGION_CENTER_ID,                             ");
			sql.append("         T.REGION_CENTER_NAME,                           ");
			sql.append("         T.BRANCH_AREA_ID,                               ");
			sql.append("         T.BRANCH_AREA_NAME,                             ");
			sql.append("         T.BRANCH_NBR,                                   ");
			sql.append("         T.BRANCH_NAME,                                  ");
			sql.append("         T.AO_CODE,                                      ");
			sql.append("         T.EMP_ID,                                       ");
			sql.append("         T.EMP_NAME,                                     ");
			sql.append("         T.ACH_YR_MN,                                    ");
			sql.append("         T.POLICY_NO,                                    ");
			sql.append("         T.POLICY_SEQ,                                   ");
			sql.append("         T.APPL_ID,                                      ");
			sql.append("         T.ID_DUP,                                       ");
			sql.append("         T.APPL_NAME,                                    ");
			sql.append("         T.EFF_YY,                                       ");
			sql.append("         T.EFF_MM,                                       ");
			sql.append("         T.EFF_DD,                                       ");
			sql.append("         T.INS_TYPE_CODE,                                ");
			sql.append("         T.INS_TYPE_NOTE,                                ");
			sql.append("         T.PREM,                                         ");
			sql.append("         T.COMMISSION,                                   ");
			sql.append("         T.RAISE_FINAL,                                  ");
			sql.append("         T.ACH_PRFT,                                      ");
			sql.append("         T.RESOURCE1                                     ");   //新增 [新件/續期] 2017/07/17
			sql.append("  FROM (SELECT YEARMON,                                  ");
			sql.append("             REGION_CENTER_ID,                           ");
			sql.append("             REGION_CENTER_NAME,                         ");
			sql.append("             BRANCH_AREA_ID,                             ");
			sql.append("             BRANCH_AREA_NAME,                           ");
			sql.append("             BRANCH_NBR,                                 ");
			sql.append("             BRANCH_NAME,                                ");
			sql.append("             AO_CODE,                                    ");
			sql.append("             EMP_ID,                                     ");
			sql.append("             EMP_NAME,                                   ");
			sql.append("             ACH_YR_MN,                                  ");
			sql.append("             POLICY_NO,                                  ");
			sql.append("             TO_CHAR(POLICY_SEQ) AS POLICY_SEQ,          ");
			sql.append("             APPL_ID,                                    ");
			sql.append("             ID_DUP,                                     ");
			sql.append("             APPL_NAME,                                  ");
			sql.append("         	 EFF_YY,                                     ");
			sql.append("         	 EFF_MM,                                     ");
			sql.append("         	 EFF_DD,                                     ");
			sql.append("         	 INS_TYPE_CODE,                              ");
			sql.append("         	 INS_TYPE_NOTE,                              ");
			sql.append("         	 PREM,                                       ");
			sql.append("         	 COMMISSION,                                 ");
			sql.append("         	 RAISE_FINAL,                                ");
			sql.append("         	 ACH_PRFT,                                   ");
			sql.append("         	 RESOURCE1                                   ");   //新增 [新件/續期]  2017/07/17
			sql.append("        FROM TBPMS_MON_INSU_AGR_REIS) T                  ");
			sql.append("  WHERE 1 = 1                                            ");
			if (!StringUtils.isBlank(inputVO.getRegion_center_id())) {
				sql.append(" AND T.REGION_CENTER_ID = :regionCenter              ");
				condition.setObject("regionCenter", inputVO.getRegion_center_id());
			}
			if (!StringUtils.isBlank(inputVO.getBranch_area_id())) {
				sql.append(" AND T.BRANCH_AREA_ID = :branchArea                  ");
				condition.setObject("branchArea", inputVO.getBranch_area_id());
			}
			if (!StringUtils.isBlank(inputVO.getBranch_nbr())) {
				sql.append(" AND T.BRANCH_NBR = :branchNbr                       ");
				condition.setObject("branchNbr", inputVO.getBranch_nbr());
			}
			if (!StringUtils.isBlank(inputVO.getAo_code())) {
				sql.append(" AND T.AO_CODE = :ao_Code                            ");
				condition.setObject("ao_Code", inputVO.getAo_code());
			}
			if (!StringUtils.isBlank(inputVO.getsTime())) {
				sql.append(" AND TRIM(T.YEARMON) = :sTime                        ");
				condition.setObject("sTime", inputVO.getsTime().trim());
			}
			sql.append("  order by BRANCH_AREA_ID, BRANCH_NBR,EMP_ID ");
			condition.setQueryString(sql.toString());
			ResultIF list = dam.executePaging(condition, inputVO
					.getCurrentPageIndex() + 1, inputVO.getPageCount());
			List<Map<String, Object>> list1 = dam.exeQuery(condition);
			int totalPage_i = list.getTotalPage(); // 分頁用
			int totalRecord_i = list.getTotalRecord(); // 分頁用
			outputVO.setResultList(list); // data
			outputVO.setCsvList(list1);
			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
			outputVO.setTotalPage(totalPage_i);// 總頁次
			outputVO.setTotalRecord(totalRecord_i);// 總筆數
			this.sendRtnObject(outputVO);
		}
		catch (Exception e)
		{
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
	/**
	 * 匯出EXCLE
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void export(Object body, IPrimitiveMap header) throws JBranchException {
		PMS714OutputVO return_VO2 = (PMS714OutputVO) body;
		List<Map<String, Object>> list = return_VO2.getCsvList();
		if(list.size() > 0){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH:mm:ss");
			String fileName = "每月保險同意書補簽報表_" + sdf.format(new Date()) + ".csv"; 
			List listCSV =  new ArrayList();
			for(Map<String, Object> map : list){
				String[] records = new String[26];
				int i = 0;
				records[i] = checkIsNull(map, "NUM"); 
				records[++i] = checkIsNullAndTrans(map, "REGION_CENTER_ID"); 
				records[++i] = checkIsNull(map, "REGION_CENTER_NAME"); 
				records[++i] = checkIsNullAndTrans(map, "BRANCH_AREA_ID"); 
				records[++i] = checkIsNull(map, "BRANCH_AREA_NAME"); 
				records[++i] = checkIsNullAndTrans(map, "BRANCH_NBR"); 
				records[++i] = checkIsNull(map, "BRANCH_NAME"); 
				records[++i] = checkIsNullAndTrans(map, "AO_CODE"); 
				records[++i] = checkIsNullAndTrans(map, "EMP_ID"); 
				records[++i] = checkIsNull(map, "EMP_NAME");
				records[++i] = checkIsNull(map, "ACH_YR_MN"); 
				records[++i] = checkIsNullAndTrans(map, "POLICY_NO"); 
				records[++i] = checkIsNullAndTrans(map, "POLICY_SEQ");
				records[++i] = checkIsNullAndTrans(map, "APPL_ID");
				records[++i] = checkIsNullAndTrans(map, "ID_DUP");
				records[++i] = checkIsNull(map, "APPL_NAME");
				records[++i] = checkIsNull(map, "EFF_YY");
				records[++i] = checkIsNull(map, "EFF_MM");
				records[++i] = checkIsNull(map, "EFF_DD");
				records[++i] = checkIsNullAndTrans(map, "INS_TYPE_CODE");
				records[++i] = checkIsNull(map, "INS_TYPE_NOTE");
				records[++i] = checkIsNull(map, "PREM");
				records[++i] = checkIsNull(map, "COMMISSION");
				records[++i] = checkIsNull(map, "RAISE_FINAL");
				records[++i] = checkIsNull(map, "ACH_PRFT");		         //CNR收益	
				records[++i] = checkIsNull(map, "RESOURCE1");
				listCSV.add(records);
			}
			//header
			String [] csvHeader = new String[26];
			int j = 0;
			csvHeader[j] = "項次";
			csvHeader[++j] = "區域中心代碼";
			csvHeader[++j] = "區域中心";
			csvHeader[++j] = "營運區代碼";
			csvHeader[++j] = "營運區";
			csvHeader[++j] = "分行代碼";
			csvHeader[++j] = "分別行";
			csvHeader[++j] = "AO_CODE";
			csvHeader[++j] = "專員員工代碼";
			csvHeader[++j] = "專員姓名";
			csvHeader[++j] = "成績年月";
			csvHeader[++j] = "保單號碼";
			csvHeader[++j] = "保單序號";
			csvHeader[++j] = "要保人ID";
			csvHeader[++j] = "身分證重覆別";
			csvHeader[++j] = "要保人姓名";
			csvHeader[++j] = "生效日-年";
			csvHeader[++j] = "生效日-月";
			csvHeader[++j] = "生效日-日";
			csvHeader[++j] = "險種代號";
			csvHeader[++j] = "險種說明";
			csvHeader[++j] = "保費";
			csvHeader[++j] = "佣金";
			csvHeader[++j] = "加碼FINAL";
			csvHeader[++j] = "計績收益";
			csvHeader[++j] = "新件/續期";
			
			CSVUtil csv = new CSVUtil();
			csv.setHeader(csvHeader);
			csv.addRecordList(listCSV);
			String url = csv.generateCSV();
			notifyClientToDownloadFile(url, fileName); //download
		} else {
			return_VO2.setResultList(list);
			this.sendRtnObject(return_VO2);
	    }
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
	/**
	 * 處理貨幣格式
	 * @param map
	 * @param key
	 * @return
	 */
	private String currencyFormat(Map map, String key){		
		if(StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null){
			NumberFormat nf = NumberFormat.getCurrencyInstance();
			return nf.format(map.get(key));										
		}else
			return "$0.00";		
	}
	
	
	/**
	* 檢查Map取出欄位是否為Null
	* @param map
	* @return String
	*/
	private String checkIsNullAndTrans(Map map, String key) 
	{
		if(StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null){
			return String.valueOf("=\""+map.get(key)+"\"");
		}else{
			return "";
		}
	}
}
