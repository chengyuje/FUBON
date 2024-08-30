package com.systex.jbranch.app.server.fps.pms225;
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
import com.systex.jbranch.app.server.fps.pms225.PMS225;
import com.systex.jbranch.app.server.fps.pms225.PMS225InputVO;
import com.systex.jbranch.app.server.fps.pms225.PMS225OutputVO;
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
 * Description : <br>
 * Comments Name : PMS225.java<br>
 * Author :zhouyiqiong<br>
 * Date :2016年11月11日 <br>
 * Version : 1.01 <br>
 * Editor : zhouyiqiong<br>
 * Editor Date : 2016年11月11日<br>
 */
@Component("pms225")
@Scope("request")
public class PMS225 extends FubonWmsBizLogic
{
	private DataAccessManager dam = null;
	private PMS225InputVO inputVO = null;
	private Logger logger = LoggerFactory.getLogger(PMS225.class);
	/**
	 * 查詢檔案
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException
	{	
		PMS225InputVO inputVO = (PMS225InputVO) body;
		PMS225OutputVO outputVO = new PMS225OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			if(inputVO.getRptType() == 2)
			{
				sql.append("  SELECT YEARMON,                                        ");
				sql.append("  	     REGION_CENTER_ID,                               ");
				sql.append("  	     REGION_CENTER_NAME,                             ");
				sql.append("  	     BRANCH_AREA_ID,                                 ");
				sql.append("  	     BRANCH_AREA_NAME,                               ");
				sql.append("  	     BRANCH_NBR,                                     ");
				sql.append("  	     BRANCH_NAME,                                    ");
				sql.append("  	     PS_CUST_ID,                                     ");
				sql.append("  	     PS_EMP_ID,                                      ");
				sql.append("  	     PS_EMP_NAME,                                    ");
				sql.append("  	     ROW_INDEX,                                      ");
				sql.append("  	     ACC,                                            ");
				sql.append("  	     ACCT_TYPE,                                      ");
				sql.append("  	     LOAN_TYPE,                                      ");
				sql.append("  	     IS_REF,                                         ");
				sql.append("  	     REF_TYPE,                                       ");
				sql.append("  	     CUST_ID,                                        ");
				sql.append("  	     CUST_NAME,                                      ");
				sql.append("  	     APP_AMT,                                        ");
				sql.append("  	     AGR_AMT,                                        ");
				sql.append("  	     CUR_BAL,                                        ");
				sql.append("  	     REF_CUST_ID,                                    ");
				sql.append("  	     REF_EMP_ID,                                     ");
				sql.append("  	     REF_EMP_NAME,                                   ");
				sql.append("  	     REF_EMP_UNI_ADD1,                               ");
				sql.append("  	     REF_EMP_UNI_ADD2,                               ");
				sql.append("  	     REF_EMP_UNI_ADD3,                               ");
				sql.append("  	     CASE_ID,                                        ");
				sql.append("  	     CASE_SOURCE,                                    ");
				sql.append("  	     IS_RATE_PROM,                                   ");
				sql.append("  	     PRO_CODE,                                       ");
				sql.append("  	     APP_DATE,                                       ");
				sql.append("  	     AGR_DATE,                                       ");
				sql.append("  	     PRO_DATE,                                       ");
				sql.append("  	     COM_NM,                                         ");
				sql.append("  	     IS_EXTEND,                                      ");
				sql.append("  	     BOUNS,                                          ");
				sql.append("  	     VERSION,                                        ");
				sql.append("  	     CREATETIME,                                     ");
				sql.append("  	     CREATOR,                                        ");
				sql.append("  	     MODIFIER,                                       ");
				sql.append("  	     LASTUPDATE                                      ");
				sql.append("  FROM TBPMS_LOAN_BONUS_DETAIL                           ");
				sql.append("  WHERE 1=1                                              ");
			}else if(inputVO.getRptType() == 3)
			 {
				sql.append("  SELECT YEARMON,                                       ");            
				sql.append("  	     REGION_CENTER_ID,                              ");
				sql.append("  	     REGION_CENTER_NAME,                            ");
				sql.append("  	     BRANCH_AREA_ID,                                ");
				sql.append("  	     BRANCH_AREA_NAME,                              ");
				sql.append("  	     BRANCH_NBR,                                    ");
				sql.append("  	     BRANCH_NAME,                                   ");
				sql.append("  	     PS_CUST_ID,                                    ");
				sql.append("  	     EMP_ID,                                        ");
				sql.append("  	     EMP_NAME,                                      ");
				sql.append("  	     CARD_ID,                                       ");
				sql.append("  	     OPEN_DATE,                                     ");
				sql.append("  	     CUST_ID,                                       ");
				sql.append("  	     CUST_NAME,                                     ");
				sql.append("  	     BONUS,                                         ");
				sql.append("  	     REASON,                                        ");
				sql.append("  	     BONUS_MINUS,                                   ");
				sql.append("  	     BEL_ID,                                        ");
				sql.append("  	     VERSION,                                       ");
				sql.append("  	     CREATETIME,                                    ");
				sql.append("  	     CREATOR,                                       ");
				sql.append("  	     MODIFIER,                                      ");
				sql.append("  	     LASTUPDATE                                     ");
				sql.append("  FROM TBPMS_EISS_BONUS_DETAIL                          ");
				sql.append("  WHERE 1=1                                             ");
			} else if(inputVO.getRptType() == 1)
			 {
				sql.append("  SELECT YEARMON,                                       ");
				sql.append("  	     REGION_CENTER_ID,                              ");
				sql.append("  	     REGION_CENTER_NAME,                            ");
				sql.append("  	     BRANCH_AREA_ID,                                ");
				sql.append("  	     BRANCH_AREA_NAME,                              ");
				sql.append("  	     BRANCH_NBR,                                    ");
				sql.append("  	     BRANCH_NAME,                                   ");
				sql.append("  	     AUX_PIN,                                       ");
				sql.append("  	     PS_EMP_ID,                                     ");
				sql.append("  	     PS_EMP_NAME,                                   ");
				sql.append("  	     JOB_RANK,                                      ");
				sql.append("  	     JOB_TITLE_NAME,                                ");
				sql.append("  	     BONUS_MRTG,                                    ");
				sql.append("  	     BONUS_CREDIT,                                  ");
				sql.append("  	     BONUS_CARD,                                    ");
				sql.append("  	     BONUS_TOTAL_1,                                 ");
				sql.append("  	     BONUS_TOTAL_2,                                 ");
				sql.append("  	     BOUNS_AJD,                                     ");
				sql.append("  	     NON_FIN_IND_AMT,                               ");
				sql.append("  	     DEDUC_ORGI_BOUNS,                              ");
				sql.append("  	     DEF_DEF_BOUNS,                                 ");
				sql.append("  	     BOUNS,                                         ");
				sql.append("  	     BOUNS_TOTAL3,                                  ");
				sql.append("  	     DEDUC_IND_BOUNS,                               ");
				sql.append("  	     REDUC_DEF_BOUNS,                               ");
				sql.append("  	     TOTAL_BOUNS,                                   ");
				sql.append("  	     DEF_DEF_BOUNS1,                                ");
				sql.append("  	     DEF_DEF_MONTH,                                 ");
				sql.append("  	     VERSION,                                       ");
				sql.append("  	     CREATETIME,                                    ");
				sql.append("  	     CREATOR,                                       ");
				sql.append("  	     MODIFIER,                                      ");
				sql.append("  	     LASTUPDATE                                     ");
				sql.append("  FROM TBPMS_LOAN_BONUS_TOTAL                           ");
				sql.append("  WHERE 1=1                                             ");
			}
			if (!StringUtils.isBlank(inputVO.getRegion_center_id())) {
				sql.append(" AND REGION_CENTER_ID = :region_center_id         ");
				queryCondition.setObject("region_center_id", inputVO.getRegion_center_id());
			}
			if (!StringUtils.isBlank(inputVO.getBranch_area_id())) {
				sql.append(" AND BRANCH_AREA_ID = :branch_area_id             ");
				queryCondition.setObject("branch_area_id", inputVO.getBranch_area_id());
			}
			if (!StringUtils.isBlank(inputVO.getBranch_nbr())) {
				sql.append(" AND BRANCH_NBR = :branch_nbr                     ");
				queryCondition.setObject("branch_nbr", inputVO.getBranch_nbr());
			}
			if(StringUtils.isNotBlank(inputVO.getsTime())){
				sql.append(" AND TRIM(YEARMON) = :sTime 					        ");
				queryCondition.setObject("sTime", inputVO.getsTime());
			}
			if(StringUtils.isNotBlank(inputVO.getEmp_id())){
				if(inputVO.getRptType() == 3) {
					sql.append(" AND EMP_ID = :EMP_ID 						        ");
				}else {
					sql.append(" AND PS_EMP_ID = :EMP_ID 						    ");
				}
				queryCondition.setObject("EMP_ID", inputVO.getEmp_id());
			}
			sql.append(" order by REGION_CENTER_ID, BRANCH_AREA_ID, BRANCH_NBR      ");
			queryCondition.setQueryString(sql.toString());
			ResultIF list = dam.executePaging(queryCondition, inputVO
					.getCurrentPageIndex() + 1, inputVO.getPageCount());
			List<Map<String, Object>> list1 = dam.exeQuery(queryCondition);
			int totalPage_i = list.getTotalPage();
			int totalRecord_i = list.getTotalRecord();
			outputVO.setResultList(list);
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
		PMS225OutputVO return_VO2 = (PMS225OutputVO) body;
		List<Map<String, Object>> list = return_VO2.getList();
		int rptType = return_VO2.getRptType();
		if(list.size() > 0){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH:mm:ss");
			List listCSV =  new ArrayList();
			String[] csvHeader = null;
			if(return_VO2.getRptType() == 2)
			{	
				String fileName = "消金專員獎勵金查詢房信貸明細_" + sdf.format(new Date()) + ".csv"; 
				for(Map<String, Object> map : list){
					String[] records = new String[38];
					int i = 0;
					records[i] = checkIsNull(map, "YEARMON");
					records[++i] = checkIsNull(map, "REGION_CENTER_ID");
					records[++i] = checkIsNull(map, "REGION_CENTER_NAME");
					records[++i] = checkIsNull(map, "BRANCH_AREA_ID");
					records[++i] = checkIsNull(map, "BRANCH_AREA_NAME");
					records[++i] = checkIsNull(map, "BRANCH_NBR");
					records[++i] = checkIsNull(map, "BRANCH_NAME");
					records[++i] = checkIsNull(map, "PS_CUST_ID");
					records[++i] = checkIsNull(map, "PS_EMP_ID");
					records[++i] = checkIsNull(map, "PS_EMP_NAME");
					records[++i] = checkIsNull(map, "ROW_INDEX");
					records[++i] = checkIsNull(map, "ACC");
					records[++i] = checkIsNull(map, "ACCT_TYPE");
					records[++i] = checkIsNull(map, "LOAN_TYPE");
					records[++i] = checkIsNull(map, "IS_REF");
					records[++i] = checkIsNull(map, "REF_TYPE");
					records[++i] = checkIsNull(map, "CUST_ID");
					records[++i] = checkIsNull(map, "CUST_NAME");
					records[++i] = checkIsNull(map, "APP_AMT");
					records[++i] = checkIsNull(map, "AGR_AMT");
					records[++i] = checkIsNull(map, "CUR_BAL");
					records[++i] = checkIsNull(map, "REF_CUST_ID");
					records[++i] = checkIsNull(map, "REF_EMP_ID");
					records[++i] = checkIsNull(map, "REF_EMP_NAME");
					records[++i] = checkIsNull(map, "REF_EMP_UNI_ADD1");
					records[++i] = checkIsNull(map, "REF_EMP_UNI_ADD2");
					records[++i] = checkIsNull(map, "REF_EMP_UNI_ADD3");
					records[++i] = checkIsNull(map, "CASE_ID");
					records[++i] = checkIsNull(map, "CASE_SOURCE");
					records[++i] = checkIsNull(map, "IS_RATE_PROM");
					records[++i] = checkIsNull(map, "PRO_CODE");
					records[++i] = checkIsNull(map, "APP_DATE");
					records[++i] = checkIsNull(map, "AGR_DATE");
					records[++i] = checkIsNull(map, "PRO_DATE");
					records[++i] = checkIsNull(map, "COM_NM");
					records[++i] = checkIsNull(map, "IS_EXTEND");
					records[++i] = checkIsNull(map, "BOUNS");
					listCSV.add(records);
				}
				//header
				csvHeader = new String[38];
				int j = 0;
				csvHeader[j] = "資料年月";
				csvHeader[++j] = "業務處代碼";
				csvHeader[++j] = "業務處名稱";
				csvHeader[++j] = "營運區代碼";
				csvHeader[++j] = "營運區名稱";
				csvHeader[++j] = "分行代碼";
				csvHeader[++j] = "分行名稱";
				csvHeader[++j] = "PS 統編";
				csvHeader[++j] = "PS 員工編號";
				csvHeader[++j] = "PS 姓名";
				csvHeader[++j] = "ROW_INDEX";
				csvHeader[++j] = "帳號";
				csvHeader[++j] = "類別";
				csvHeader[++j] = "房/信貸";
				csvHeader[++j] = "轉介案件(Y/N)";
				csvHeader[++j] = "專案別";
				csvHeader[++j] = "客戶統編";
				csvHeader[++j] = "客戶名稱";
				csvHeader[++j] = "初撥金額";
				csvHeader[++j] = "核准額度";
				csvHeader[++j] = "動支餘額";
				csvHeader[++j] = "介紹人統編";
				csvHeader[++j] = "介紹人員工編號";
				csvHeader[++j] = "介紹人員工姓名";
				csvHeader[++j] = "介紹人單位代號一";
				csvHeader[++j] = "介紹人單位代號二";
				csvHeader[++j] = "介紹人單位代號三";
				csvHeader[++j] = "案件編號";
				csvHeader[++j] = "業務來源";
				csvHeader[++j] = "有無利率請示(Y/N)";
				csvHeader[++j] = "行銷活動";
				csvHeader[++j] = "申請日期";
				csvHeader[++j] = "核准日期";
				csvHeader[++j] = "起息日";
				csvHeader[++j] = "COM_NM";
				csvHeader[++j] = "發佣註記(Y/N)";
				csvHeader[++j] = "獎金";
				CSVUtil csv = new CSVUtil();
				csv.setHeader(csvHeader);
				csv.addRecordList(listCSV);
				String url = csv.generateCSV();
				notifyClientToDownloadFile(url, fileName); //download
			}else if(return_VO2.getRptType() == 3)
			{
				String fileName = "消金專員獎勵金查詢信用卡獎金明細_" + sdf.format(new Date()) + ".csv"; 
				for(Map<String, Object> map : list){
					String[] records = new String[18];
					int i = 0;
					records[i] = checkIsNull(map, "YEARMON");
					records[++i] = checkIsNull(map, "REGION_CENTER_ID");
					records[++i] = checkIsNull(map, "REGION_CENTER_NAME");
					records[++i] = checkIsNull(map, "BRANCH_AREA_ID");
					records[++i] = checkIsNull(map, "BRANCH_AREA_NAME");
					records[++i] = checkIsNull(map, "BRANCH_NBR");
					records[++i] = checkIsNull(map, "BRANCH_NAME");
					records[++i] = checkIsNull(map, "PS_CUST_ID");
					records[++i] = checkIsNull(map, "EMP_ID");
					records[++i] = checkIsNull(map, "EMP_NAME");
					records[++i] = checkIsNull(map, "CARD_ID");
					records[++i] = checkIsNull(map, "OPEN_DATE");
					records[++i] = checkIsNull(map, "CUST_ID");
					records[++i] = checkIsNull(map, "CUST_NAME");
					records[++i] = checkIsNull(map, "BONUS");
					records[++i] = checkIsNull(map, "REASON");
					records[++i] = checkIsNull(map, "BONUS_MINUS");
					records[++i] = checkIsNull(map, "BEL_ID");
					listCSV.add(records);
				}
				//header
				csvHeader = new String[18];
				int j = 0;
				csvHeader[j] = "資料年月";
				csvHeader[++j] = "業務處代碼";
				csvHeader[++j] = "業務處名稱";
				csvHeader[++j] = "營運區代碼";
				csvHeader[++j] = "營運區名稱";
				csvHeader[++j] = "分行代碼";
				csvHeader[++j] = "分行名稱";
				csvHeader[++j] = "PS ID";
				csvHeader[++j] = "PS 員工編號";
				csvHeader[++j] = "PS 姓名";
				csvHeader[++j] = "卡別";
				csvHeader[++j] = "核卡日期";
				csvHeader[++j] = "客戶ID";
				csvHeader[++j] = "客戶名稱";
				csvHeader[++j] = "信用卡獎金(元)";
				csvHeader[++j] = "加減項原因";
				csvHeader[++j] = "信用卡加減項(元)";
				csvHeader[++j] = "歸戶ID";
				CSVUtil csv = new CSVUtil();
				csv.setHeader(csvHeader);
				csv.addRecordList(listCSV);
				String url = csv.generateCSV();
				notifyClientToDownloadFile(url, fileName); //download
			}else if(return_VO2.getRptType() == 1)
			{
				String fileName = "消金專員獎勵金查詢總表_" + sdf.format(new Date()) + ".csv"; 
				for(Map<String, Object> map : list){
					String[] records = new String[28];
					int i = 0;
					records[i] = checkIsNull(map, "YEARMON");
					records[++i] = checkIsNull(map, "REGION_CENTER_ID");
					records[++i] = checkIsNull(map, "REGION_CENTER_NAME");
					records[++i] = checkIsNull(map, "BRANCH_AREA_ID");
					records[++i] = checkIsNull(map, "BRANCH_AREA_NAME");
					records[++i] = checkIsNull(map, "BRANCH_NBR");
					records[++i] = checkIsNull(map, "BRANCH_NAME");
					records[++i] = checkIsNull(map, "AUX_PIN");
					records[++i] = checkIsNull(map, "PS_EMP_ID");
					records[++i] = checkIsNull(map, "PS_EMP_NAME");
					records[++i] = checkIsNull(map, "JOB_RANK");
					records[++i] = checkIsNull(map, "JOB_TITLE_NAME");
					records[++i] = checkIsNull(map, "BONUS_MRTG");
					records[++i] = checkIsNull(map, "BONUS_CREDIT");
					records[++i] = checkIsNull(map, "BONUS_CARD");
					records[++i] = checkIsNull(map, "BONUS_TOTAL_1");
					records[++i] = checkIsNull(map, "BONUS_TOTAL_2");
					records[++i] = checkIsNull(map, "BOUNS_AJD");
					records[++i] = checkIsNull(map, "NON_FIN_IND_AMT");
					records[++i] = checkIsNull(map, "DEDUC_ORGI_BOUNS");
					records[++i] = checkIsNull(map, "DEF_DEF_BOUNS");
					records[++i] = checkIsNull(map, "BOUNS");
					records[++i] = checkIsNull(map, "BOUNS_TOTAL3");
					records[++i] = checkIsNull(map, "DEDUC_IND_BOUNS");
					records[++i] = checkIsNull(map, "REDUC_DEF_BOUNS");
					records[++i] = checkIsNull(map, "TOTAL_BOUNS");
					records[++i] = checkIsNull(map, "DEF_DEF_BOUNS1");
					records[++i] = checkIsNull(map, "DEF_DEF_MONTH");
					listCSV.add(records);
				}
				//header
				csvHeader = new String[28];
				int j = 0;
				csvHeader[j] = "資料年月";
				csvHeader[++j] = "業務處代碼";
				csvHeader[++j] = "業務處名稱";
				csvHeader[++j] = "營運區代碼";
				csvHeader[++j] = "營運區名稱";
				csvHeader[++j] = "分行代碼";
				csvHeader[++j] = "分行名稱";
				csvHeader[++j] = "輔銷";
				csvHeader[++j] = "員工編號";
				csvHeader[++j] = "專員姓名";
				csvHeader[++j] = "職等";
				csvHeader[++j] = "職稱";
				csvHeader[++j] = "房貸獎金";
				csvHeader[++j] = "信貸獎金";
				csvHeader[++j] = "信用卡獎金";
				csvHeader[++j] = "合計加3萬(未減本薪)";
				csvHeader[++j] = "當月原始獎金100%";
				csvHeader[++j] = "獎勵金調整數";
				csvHeader[++j] = "當月扣減非財務指標金額";
				csvHeader[++j] = "當月扣減後原始獎金";
				csvHeader[++j] = "當月保留遞延獎金";
				csvHeader[++j] = "當月發放獎金";
				csvHeader[++j] = "前三個月遞延至當月發放獎金";
				csvHeader[++j] = "續扣減非財務指標金額";
				csvHeader[++j] = "扣減後當月遞延獎金";
				csvHeader[++j] = "合計發放獎金";
				csvHeader[++j] = "當月保留遞延獎金";
				csvHeader[++j] = "當月保留遞延獎金預計發放年月(資料年月+3個月)";
				CSVUtil csv = new CSVUtil();
				csv.setHeader(csvHeader);
				csv.addRecordList(listCSV);
				String url = csv.generateCSV();
				notifyClientToDownloadFile(url, fileName); //download
			}
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
	private String checkIsNull(Map map, String key) 
	{
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
	private String currencyFormat(Map map, String key)
	{		
		if(StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null){
			NumberFormat nf = NumberFormat.getCurrencyInstance();
			return nf.format(map.get(key));										
		}else
			return "$0.00";		
	}
}