package com.systex.jbranch.app.server.fps.pms338;
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
import com.systex.jbranch.app.server.fps.pms338.PMS338InputVO;
import com.systex.jbranch.app.server.fps.pms338.PMS338OutputVO;
import com.systex.jbranch.app.server.fps.pms338.PMS338;
import com.systex.jbranch.app.server.fps.pms338.PMS338;
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
 * Description :房貸壽險佣金報表Controller <br>
 * Comments Name : PMS338.java<br>
 * Author :zhouyiqiong<br>
 * Date :2016年11月15日 <br>
 * Version : 1.01 <br>
 * Editor : zhouyiqiong<br>
 * Editor Date : 2016年11月15日<br>
 */
@Component("pms338")
@Scope("request")
public class PMS338 extends FubonWmsBizLogic 
{
	private DataAccessManager dam = null;	
	private Logger logger = LoggerFactory.getLogger(PMS338.class);
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException 
	{		
		PMS338InputVO inputVO = (PMS338InputVO) body;
		PMS338OutputVO outputVO = new PMS338OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			sql.append("  SELECT DATE_YEAR,											");
			sql.append("  	     LOAN_TYPE,                                         ");
			sql.append("  	     MONTH01,                                           ");
			sql.append("  	     MONTH02,                                           ");
			sql.append("  	     MONTH03,                                           ");
			sql.append("  	     MONTH04,                                           ");
			sql.append("  	     MONTH05,                                           ");
			sql.append("  	     MONTH06,                                           ");
			sql.append("  	     MONTH07,                                           ");
			sql.append("  	     MONTH08,                                           ");
			sql.append("  	     MONTH09,                                           ");
			sql.append("  	     MONTH10,                                           ");
			sql.append("  	     MONTH11,                                           ");
			sql.append("  	     MONTH12,                                           ");
			sql.append("  	     MONTH00,                                           ");
			sql.append("  	     VERSION,                                           ");
			sql.append("  	     CREATETIME,                                        ");
			sql.append("  	     CREATOR,                                           ");
			sql.append("  	     MODIFIER,                                          ");
			sql.append("  	     LASTUPDATE                                         ");
			sql.append("  FROM TBPMS_LIFE_INS_COMM                                  ");                                                                   
			sql.append("  WHERE 1=1                                                 ");
			//資料統計日期
			if(StringUtils.isNotBlank(inputVO.getsTime()))
			{
				sql.append("  AND TRIM(DATE_YEAR) = :sTime 					        ");
				condition.setObject("sTime", inputVO.getsTime());
			}
			sql.append("  ORDER BY LOAN_TYPE                                        ");
			condition.setQueryString(sql.toString());
			ResultIF list = dam.executePaging(condition, inputVO
					.getCurrentPageIndex() + 1, inputVO.getPageCount());
			List<Map<String, Object>> list1 = dam.exeQuery(condition);
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
	public void export(Object body, IPrimitiveMap header) throws JBranchException 
	{
		PMS338OutputVO return_VO2 = (PMS338OutputVO) body;
		List<Map<String, Object>> list = return_VO2.getCsvList();
		int dataDate = 0;
		if(list.size() > 0)
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH:mm:ss");
			String fileName = "房貸壽險佣金報表_" + sdf.format(new Date()) + ".csv"; 
			List listCSV =  new ArrayList();
			for(int j=0;j<list.size();j++)
			{
				String[] records = new String[15];
				int i=0;
				if("1".equals(list.get(j).get("LOAN_TYPE").toString()))
				{
					dataDate  = Integer.valueOf(list.get(i).get("DATE_YEAR").toString().substring(0,4));
					records[i] = "房款壽險"; 
					
				}else if("2".equals(list.get(j).get("LOAN_TYPE").toString()))
				{
					dataDate  = Integer.valueOf(list.get(i).get("DATE_YEAR").toString().substring(0,4));
					records[i] = "非房款壽險";  
				}
				else if("3".equals(list.get(j).get("LOAN_TYPE").toString()))
				{
					dataDate  = Integer.valueOf(list.get(i).get("DATE_YEAR").toString().substring(0,4));
					records[i] = "單位:元";  
				}
				records[++i] = checkIsNull(list.get(j), "MONTH01");  
				records[++i] = checkIsNull(list.get(j), "MONTH02");  			
				records[++i] = checkIsNull(list.get(j), "MONTH03");	
				records[++i] = checkIsNull(list.get(j), "MONTH04");			
				records[++i] = checkIsNull(list.get(j), "MONTH05");				
				records[++i] = checkIsNull(list.get(j), "MONTH06");  		
				records[++i] = checkIsNull(list.get(j), "MONTH07");			
				records[++i] = checkIsNull(list.get(j), "MONTH08");					
				records[++i] = checkIsNull(list.get(j), "MONTH09");	
				records[++i] = checkIsNull(list.get(j), "MONTH10");		
				records[++i] = checkIsNull(list.get(j), "MONTH11");		
				records[++i] = checkIsNull(list.get(j), "MONTH12");		
				records[++i] = checkIsNull(list.get(j), "MONTH00");		
				listCSV.add(records);
			}
			//header
			String [] csvHeader = new String[15];
			int j = 0;
			csvHeader[j] = "年月";
			csvHeader[++j] = dataDate+"01";
			csvHeader[++j] = dataDate+"02";
			csvHeader[++j] = dataDate+"03";
			csvHeader[++j] = dataDate+"04";
			csvHeader[++j] = dataDate+"05";
			csvHeader[++j] = dataDate+"06";
			csvHeader[++j] = dataDate+"07";
			csvHeader[++j] = dataDate+"08";
			csvHeader[++j] = dataDate+"09";
			csvHeader[++j] = dataDate+"10";
			csvHeader[++j] = dataDate+"11";
			csvHeader[++j] = dataDate+"12";
			csvHeader[++j] = dataDate + "合計";
			CSVUtil csv = new CSVUtil();
			csv.setHeader(csvHeader);
			csv.addRecordList(listCSV);
			String url = csv.generateCSV();
			notifyClientToDownloadFile(url, fileName); //download
		} else 
		{
			return_VO2.setResultList(list);
			this.sendRtnObject(return_VO2);
	    }
	}
	public void detail(Object body, IPrimitiveMap header) throws JBranchException
	{	
		PMS338InputVO inputVO = (PMS338InputVO) body;
		PMS338OutputVO outputVO = new PMS338OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			sql.append("  SELECT YEARMON,										");
			sql.append("  	     LOAN_TYPE,                                     ");
			sql.append("  	     TXN_DATE,                                      ");
			sql.append("  	     CESS_NO,                                       ");
			sql.append("  	     WORK_YY,                                       ");
			sql.append("  	     WORK_MM,                                       ");
			sql.append("  	     UNIT_CODE,                                     ");
			sql.append("  	     POLICY_NO,                                     ");
			sql.append("  	     ID_DUP,                                        ");
			sql.append("  	     POLICY_SEQ,                                    ");
			sql.append("  	     POLICY_NUM,                                    ");
			sql.append("  	     ID_NO,                                         ");
			sql.append("  	     INS_ID,                                        ");
			sql.append("  	     INS_NAME,                                      ");
			sql.append("  	     APPL_ID,                                       ");
			sql.append("  	     APPL_NAME,                                     ");
			sql.append("  	     EFF_YY,                                        ");
			sql.append("  	     EFF_MM,                                        ");
			sql.append("  	     EFF_DD,                                        ");
			sql.append("  	     RECEIPT_TP,                                    ");
			sql.append("  	     RECEIPT_NO,                                    ");
			sql.append("  	     ITEM,                                          ");
			sql.append("  	     PREM_YEAR,                                     ");
			sql.append("  	     POLI_YEAR,                                     ");
			sql.append("  	     POLI_PERD,                                     ");
			sql.append("  	     PREM,                                          ");
			sql.append("  	     COMIS,                                         ");
			sql.append("  	     PREM_Y,                                        ");
			sql.append("  	     COMIS_Y,                                       ");
			sql.append("  	     REL_PREM,                                      ");
			sql.append("  	     AMOUNT,                                        ");
			sql.append("  	     PRFM_COMU,                                     ");
			sql.append("  	     PRFM_RATE,                                     ");
			sql.append("  	     SAVE_COMIS,                                    ");
			sql.append("  	     SERV_RATE,                                     ");
			sql.append("  	     EMPY_ID,                                       ");
			sql.append("  	     ORIG_ID,                                       ");
			sql.append("  	     COMU_YY,                                       ");
			sql.append("  	     COMU_MM,                                       ");
			sql.append("  	     COMU_DD,                                       ");
			sql.append("  	     AGNT_NAME,                                     ");
			sql.append("  	     ORIG_NAME,                                     ");
			sql.append("  	     MOP,                                           ");
			sql.append("  	     SA,                                            ");
			sql.append("  	     END_YY,                                        ");
			sql.append("  	     END_MM,                                        ");
			sql.append("  	     END_DD,                                        ");
			sql.append("  	     CHECK_YY,                                      ");
			sql.append("  	     CHECK_MM,                                      ");
			sql.append("  	     CHECK_DD,                                      ");
			sql.append("  	     RESOURCES,                                     ");
			sql.append("  	     COMU_RATE,                                     ");
			sql.append("  	     CUR_PERD,                                      ");
			sql.append("  	     PREMF,                                         ");
			sql.append("  	     CURRENCY,                                      ");
			sql.append("  	     DECL_ECHG,                                     ");
			sql.append("  	     EMPY_YN,                                       ");
			sql.append("  	     DEPT_CODE,                                     ");
			sql.append("  	     CNCT_STAT,                                     ");
			sql.append("  	     DISC_HISA,                                     ");
			sql.append("  	     PREM_KIND,                                     ");
			sql.append("  	     PRIZE,                                         ");
			sql.append("  	     BT_AMT,                                        ");
			sql.append("  	     VERSION,                                       ");
			sql.append("  	     CREATETIME,                                    ");
			sql.append("  	     CREATOR,                                       ");
			sql.append("  	     MODIFIER,                                      ");
			sql.append("  	     LASTUPDATE                                     ");
			sql.append("  FROM TBPMS_LIFE_INS_COMM_DETAIL                       ");
			sql.append("  WHERE 1 = 1                                           ");
			if (!StringUtils.isBlank(inputVO.getYearMon())) 
			{
				sql.append(" AND TRIM(YEARMON) = :yearMon                       ");
				condition.setObject("yearMon", inputVO.getYearMon().trim());
			}
			if (!StringUtils.isBlank(inputVO.getLoanType())) 
			{
				sql.append(" AND LOAN_TYPE = :type                              ");
				condition.setObject("type", inputVO.getLoanType());
			}
			sql.append("  ORDER BY APPL_ID, POLICY_NO                           ");
			condition.setQueryString(sql.toString());
			ResultIF list = dam.executePaging(condition, inputVO
					.getCurrentPageIndex() + 1, inputVO.getPageCount());
			int totalPage_i = list.getTotalPage();
			int totalRecord_i = list.getTotalRecord();
			outputVO.setDetailList(list);
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
	* 檢查Map取出欄位是否為Null
	* 
	* @param map
	* @return String
	*/
	private String checkIsNull(Map map, String key) 
	{
		if(StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null)
		{
			return String.valueOf(map.get(key));
		}else
		{
			return "";
		}
	}
	//處理貨幣格式
	private String currencyFormat(Map map, String key)
	{		
		if(StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null)
		{
			NumberFormat nf = NumberFormat.getCurrencyInstance();
			return nf.format(map.get(key));										
		}else
			return "$0.00";		
	}

	//達成率格式
	private String pcntFormat(Map map, String key)
	{		
		if(StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null)
		{				
			return (int)(Float.parseFloat(map.get(key)+"")+0.5)+"%";										
		}else
			return "";		
	}
}