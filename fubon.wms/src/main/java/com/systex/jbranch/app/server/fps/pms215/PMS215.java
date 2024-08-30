package com.systex.jbranch.app.server.fps.pms215;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.pms215.PMS215OutputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

import java.text.ParseException;
/**
 * 
 * Copy Right Information : Forms Syntron <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : FC理專財務非財務報表Controller<br>
 * Comments Name : PMS215.java<br>
 * Author :zhouyiqiong<br>
 * Date :2016年11月7日 <br>
 * Version : 1.01 <br>
 * Editor : zhouyiqiong<br>
 * Editor Date : 2016年11月7日<br>
 */
@Component("pms215")
@Scope("request")
public class PMS215 extends FubonWmsBizLogic
{
	private DataAccessManager dam = null;
	private PMS215InputVO inputVO = null;
	private Logger logger = LoggerFactory.getLogger(PMS215.class);
	
	/**
	 * 版本下拉選單
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void version(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS215OutputVO return_VO = new PMS215OutputVO();
		PMS215InputVO inputVO = (PMS215InputVO) body;
		dam = this.getDataAccessManager();
		String role = inputVO.getRole();
		QueryConditionIF queryCondition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		try {
			sql.append(" select * from table(FN_EXAM_REPORT('"+inputVO.getsTime()+"','"+role+"','PMS215'))");
			queryCondition.setQueryString(sql.toString());
			// result
			List<Map<String, Object>> list = dam.executeQuery(queryCondition);
			return_VO.setVerList(list);
			this.sendRtnObject(return_VO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
	/**
	 * 查詢檔案
	 * 
	 * @param body
	 * @param header
	 * @throws  
	 * @throws JBranchException
	 * @throws ParseException 
	 */
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException
	{	
		PMS215InputVO inputVO = (PMS215InputVO) body;
		PMS215OutputVO outputVO = new PMS215OutputVO();
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		List roleList = new ArrayList();
		roleList.add(0, inputVO.getRole());
		StringBuffer sql = new StringBuffer();
		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			sql.append("  SELECT YEARMON,									  					 ");
			sql.append("         EXEC_DATE,									  					 ");
			sql.append("         OPEN_FLAG,									  					 ");
			sql.append("         REPORT_TYPE,								 					 ");
			sql.append("  	     REGION_CENTER_ID,                            					 ");
			sql.append("  	     REGION_CENTER_NAME,                          					 ");
			sql.append("  	     BRANCH_AREA_ID,                              					 ");
			sql.append("  	     BRANCH_AREA_NAME,                            					 ");
			sql.append("  	     BRANCH_NBR,                                  					 ");
			sql.append("  	     BRANCH_NAME,                                 					 ");
			sql.append("  	     '' || EMP_ID AS EMP_ID,                      					 ");
			sql.append("  	     EMP_NAME,                                    					 ");
			sql.append("  	     AO_CODE,                                     					 ");
			sql.append("  	     JOB_TITLE_ID,                                					 ");
			sql.append("  	     AOVER_GAINS,                                 					 ");
			sql.append("  	     AOVER_GAINS_GOAL,                            					 ");
			sql.append("  	     ROUND(FEE_RATE_REACHED*100,2)||'%' AS FEE_RATE_REACHED,         ");
			sql.append("  	     SCORE_FEE_INCOME,                            					 ");
			sql.append("  	     DEP_AUM_IN,                                  					 ");
			sql.append("  	     DEP_AUM_IN_GOAL,                             					 ");
			sql.append("  	     DEP_AUM_IN_YTD,                              					 ");
			sql.append("  	     DEP_AUM_IN_YTD_GOAL,                         					 ");
			sql.append("  	     ROUND(DEP_AUM_IN_YTD_RATE*100,2)||'%' AS DEP_AUM_IN_YTD_RATE,   ");
			sql.append("  	     DEP_AUM_SC,                                  					 ");
			sql.append("  	     INC_INSU_AUM,                                					 ");
			sql.append("  	     INC_INSU_AUM_GOAL,                           					 ");
			sql.append("  	     INC_INSU_AUM_YTD,                            					 ");
			sql.append("  	     INC_INSU_AUM_YTD_GOAL,                       					 ");
			sql.append("  	     ROUND(INC_INSU_AUM_YTD_RATE*100,2)||'%' AS INC_INSU_AUM_YTD_RATE,");
			sql.append("  	     INC_AUM_SC,                                  					 ");
			sql.append("  	     AUM_SC,                                      					 ");
			sql.append("  	     E_CL,                                        					 ");
			sql.append("  	     E_CL_YTD,                                    					 ");
			sql.append("  	     I_CL,                                        					 ");
			sql.append("  	     I_CL_YTD,                                    					 ");
			sql.append("  	     P_CL,                                        					 ");
			sql.append("  	     P_CL_YTD,                                    					 ");
			sql.append("  	     EIP_ALL_CL,                                  					 ");
			sql.append("  	     EIP_ALL_CL_GOAL,                             					 ");
			sql.append("  	     EIP_ALL_CL_YTD,                              					 ");
			sql.append("  	     EIP_ALL_CL_YTD_GOAL,                         					 ");
			sql.append("  	     ROUND(EIP_ALL_CL_RATE_YTD*100,2)||'%' AS EIP_ALL_CL_RATE_YTD,   ");
			sql.append("  	     EIP_ALL_CL_SC,                               					 ");
			sql.append("  	     AUM_ICMT_ACH_SCORE,                          					 ");
			sql.append("  	     FIN_IND_SC,                                  					 ");
			sql.append("  	     ROUND(UN_FIN_IND_BONUS_RATE*100,2)||'%' AS UN_FIN_IND_BONUS_RATE,");
			sql.append("  	     ROUND(LACK_IND_RATE*100,2)||'%' AS LACK_IND_RATE,               ");
			sql.append("  	     LOST_CONTENT                                 					 ");
			sql.append("  FROM TBPMS_FC_FIN_RPT                               					 ");
			sql.append("  WHERE 1=1                                           					 ");
			if (!StringUtils.isBlank(inputVO.getsTime())) {
				sql.append(" AND TRIM(YEARMON) = :YEARMON                     ");
				queryCondition.setObject("YEARMON", inputVO.getsTime());
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
			if (!StringUtils.isBlank(inputVO.getAo_code())){
				sql.append(" AND AO_CODE = :ao_code 					    	  ");
				queryCondition.setObject("ao_code", inputVO.getAo_code());
			}
			if(StringUtils.isNotBlank(inputVO.getRptVersion())){
				if(inputVO.getRptVersion().equals("上簽版")){
					sql.append(" AND REPORT_TYPE = '1' 			              ");
					
				}else{
					sql.append(" AND REPORT_TYPE = '0' 			              ");
					sql.append(" AND EXEC_DATE = :rptVersion 	              ");
					queryCondition.setObject("rptVersion", inputVO.getRptVersion().substring(3,11));
				}
			}
			sql.append("  order by REGION_CENTER_ID, BRANCH_NBR,AO_CODE ");
			queryCondition.setQueryString(sql.toString());
			ResultIF list = dam.executePaging(queryCondition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
			List<Map<String, Object>> list1 = dam.exeQuery(queryCondition);
			int totalPage_i = list.getTotalPage(); // 分頁用
			int totalRecord_i = list.getTotalRecord(); // 分頁用
			outputVO.setResultList(list); // data
			outputVO.setCsvList(list1);
			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
			outputVO.setTotalPage(totalPage_i);// 總頁次
			outputVO.setTotalRecord(totalRecord_i);// 總筆數
			outputVO.setRoleList(roleList);
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
		PMS215OutputVO return_VO2 = (PMS215OutputVO) body;
		List<Map<String, Object>> list = return_VO2.getCsvList();
		List<String> roleList = return_VO2.getRoleList();
		if(list.size() > 0){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH:mm:ss");
			String fileName = "FC理專財務非財務報表_" + sdf.format(new Date()) + ".csv"; 
			List listCSV =  new ArrayList();
			String[] csvHeader = null;
			if(roleList.get(0).equals("0"))
			{
				for(Map<String, Object> map : list){
					String[] records = new String[36];
					int i = 0;
					records[i] = checkIsNull(map, "YEARMON");  						//資料年月
					records[++i] = checkIsNull(map, "REGION_CENTER_ID");  		    //區域中心
					records[++i] = checkIsNull(map, "REGION_CENTER_NAME");  		//區域中心name
					records[++i] = checkIsNull(map, "BRANCH_AREA_ID");			    //營運區
					records[++i] = checkIsNull(map, "BRANCH_AREA_NAME");			//營運區	name
					records[++i] = checkIsNull(map, "BRANCH_NBR");					//分行代碼
					records[++i] = checkIsNull(map, "BRANCH_NAME");					//分行別		
					records[++i] = checkIsNullAndTrans(map, "EMP_ID");  					//員工編號
					records[++i] = checkIsNull(map, "EMP_NAME");					//專員姓名
					records[++i] = checkIsNullAndTrans(map, "AO_CODE");						//AO_CODE
					records[++i] = checkIsNull(map, "JOB_TITLE_ID");				//專員職級
					//財務指標-手續費收入
					records[++i] = checkIsNull(map, "AOVER_GAINS");					//加碼後計績收益						
					records[++i] = checkIsNull(map, "FEE_RATE_REACHED");				//達成率		
					records[++i] = checkIsNull(map, "SCORE_FEE_INCOME");			//得分
					//財務指標-存款AUM（不含台定)	
					records[++i] = checkIsNull(map, "DEP_AUM_IN_YTD");				//增量YTD(含台定減量加回)			
					records[++i] = checkIsNull(map, "DEP_AUM_IN_YTD_GOAL");			//增量目標YTD			
					records[++i] = checkIsNull(map, "DEP_AUM_IN_YTD_RATE");			//達成率YTD				
					records[++i] = checkIsNull(map, "DEP_AUM_SC");					//得分				
					//財務指標-投保AUM
					records[++i] = checkIsNull(map, "INC_INSU_AUM_YTD");			//增量YTD
					records[++i] = checkIsNull(map, "INC_INSU_AUM_YTD_GOAL");		//增量目標YTD			
					records[++i] = checkIsNull(map, "INC_INSU_AUM_YTD_RATE");		//達成率YTD				
					records[++i] = checkIsNull(map, "INC_AUM_SC");					//得分		
					//AUM得分
					records[++i] = checkIsNull(map, "AUM_SC");						//AUM得分	
					//財務指標-EIP客戶數
					records[++i] = checkIsNull(map, "E_CL_YTD");					//E級客戶增量YTD
					records[++i] = checkIsNull(map, "I_CL_YTD");					//I級客戶增量YTD
					records[++i] = checkIsNull(map, "P_CL_YTD");					//P級客戶增量YTD			
					records[++i] = checkIsNull(map, "EIP_ALL_CL_YTD");				//EIP合計增量YTD
					records[++i] = checkIsNull(map, "EIP_ALL_CL_YTD_GOAL");			//EIP合計增量目標YTD
					records[++i] = checkIsNull(map, "EIP_ALL_CL_RATE_YTD");			//EIP合計達成率
					records[++i] = checkIsNull(map, "EIP_ALL_CL_SC");				//EIP合計得分
					records[++i] = checkIsNull(map, "AUM_ICMT_ACH_SCORE");			//AUM+客戶數得分
					records[++i] = checkIsNull(map, "FIN_IND_SC");					//財務指標得分
					records[++i] = checkIsNull(map, "UN_FIN_IND_BONUS_RATE");		//非財務指標扣減百分比%
					records[++i] = checkIsNull(map, "LACK_IND_RATE");				//獨立列示重大缺失扣減百分比%
					records[++i] = checkIsNull(map, "LOST_CONTENT");				//缺失內容
					listCSV.add(records);
				}
				//header
				csvHeader = new String[36];
				int j = 0;
				csvHeader[j] = "資料年月";
				csvHeader[++j] = "業務處代碼";
				csvHeader[++j] = "業務處";
				csvHeader[++j] = "營運區代碼";
				csvHeader[++j] = "營運區";
				csvHeader[++j] = "分行代碼";
				csvHeader[++j] = "分行別	";
				csvHeader[++j] = "員工編號";
				csvHeader[++j] = "專員姓名";
				csvHeader[++j] = "AO_CODE";
				csvHeader[++j] = "專員職級";
				//財務指標-手續費收入
				csvHeader[++j] = "手續費收入-加碼後計績收益";
				csvHeader[++j] = "手續費收入-達成率";
				csvHeader[++j] = "手續費收入-得分";
				//財務指標-存款AUM（不含台定)
				csvHeader[++j] = "存款AUM（不含台定)-增量YTD(含台定減量加回)";
				csvHeader[++j] = "存款AUM（不含台定)-增量目標YTD";
				csvHeader[++j] = "存款AUM（不含台定)-達成率YTD";
				csvHeader[++j] = "存款AUM（不含台定)-得分";
				//財務指標-投保AUM
				csvHeader[++j] = "投保AUM-增量YTD";
				csvHeader[++j] = "投保AUM-增量目標YTD";
				csvHeader[++j] = "投保AUM-達成率YTD";
				csvHeader[++j] = "投保AUM-得分";
				csvHeader[++j] = "AUM得分";
				//財務指標-EIP客戶數
				csvHeader[++j] = "EIP客戶數-E級客戶增量YTD";
				csvHeader[++j] = "EIP客戶數-I級客戶增量YTD";
				csvHeader[++j] = "EIP客戶數-P級客戶增量YTD";
				csvHeader[++j] = "EIP客戶數-EIP合計增量YTD";
				csvHeader[++j] = "EIP客戶數-EIP合計增量目標YTD";
				csvHeader[++j] = "EIP客戶數-EIP合計達成率";
				csvHeader[++j] = "EIP客戶數-EIP合計得分";
				csvHeader[++j] = "AUM+客戶數得分";
				csvHeader[++j] = "財務指標得分";
				csvHeader[++j] = "非財務指標扣減百分比%";
				csvHeader[++j] = "獨立列示重大缺失扣減百分比%";
				csvHeader[++j] = "缺失內容";
			}else
			{   
				for(Map<String, Object> map : list){
					String[] records = new String[47];
					int i = 0;
					records[i] = checkIsNull(map, "YEARMON");  					//資料年月
					records[++i] = checkIsNull(map, "REGION_CENTER_ID");  	    //區域中心
					records[++i] = checkIsNull(map, "REGION_CENTER_NAME");  	//區域中心name
					records[++i] = checkIsNull(map, "BRANCH_AREA_ID");		    //營運區
					records[++i] = checkIsNull(map, "BRANCH_AREA_NAME");		//營運區	name
					records[++i] = checkIsNull(map, "BRANCH_NBR");				//分行代碼
					records[++i] = checkIsNull(map, "BRANCH_NAME");				//分行別		
					records[++i] = checkIsNullAndTrans(map, "EMP_ID");  				//員工編號
					records[++i] = checkIsNull(map, "EMP_NAME");				//專員姓名
					records[++i] = checkIsNullAndTrans(map, "AO_CODE");					//AO_CODE
					records[++i] = checkIsNull(map, "JOB_TITLE_ID");			//專員職級
					//財務指標-手續費收入
					records[++i] = checkIsNull(map, "AOVER_GAINS");				//加碼後計績收益		
					records[++i] = checkIsNull(map, "AOVER_GAINS_GOAL");		//目標	
					records[++i] = checkIsNull(map, "FEE_RATE_REACHED");			//達成率		
					records[++i] = checkIsNull(map, "SCORE_FEE_INCOME");		//得分
					//財務指標-存款AUM（不含台定)
					records[++i] = checkIsNull(map, "DEP_AUM_IN");				//當月增量(含台定減量加回)			
					records[++i] = checkIsNull(map, "DEP_AUM_IN_GOAL");			//當月增量目標		
					records[++i] = checkIsNull(map, "DEP_AUM_IN_YTD");			//增量YTD(含台定減量加回)			
					records[++i] = checkIsNull(map, "DEP_AUM_IN_YTD_GOAL");		//增量目標YTD			
					records[++i] = checkIsNull(map, "DEP_AUM_IN_YTD_RATE");		//達成率YTD				
					records[++i] = checkIsNull(map, "DEP_AUM_SC");				//得分				
					//財務指標-投保AUM
					records[++i] = checkIsNull(map, "INC_INSU_AUM");			//當月增量
					records[++i] = checkIsNull(map, "INC_INSU_AUM_GOAL");		//當月增量目標		
					records[++i] = checkIsNull(map, "INC_INSU_AUM_YTD");		//增量YTD
					records[++i] = checkIsNull(map, "INC_INSU_AUM_YTD_GOAL");	//增量目標YTD			
					records[++i] = checkIsNull(map, "INC_INSU_AUM_YTD_RATE");	//達成率YTD				
					records[++i] = checkIsNull(map, "INC_AUM_SC");				//得分		
					//AUM得分
					records[++i] = checkIsNull(map, "AUM_SC");					//AUM得分	
					//財務指標-EIP客戶數
					records[++i] = checkIsNull(map, "E_CL");					//E級客戶增量YTD
					records[++i] = checkIsNull(map, "E_CL_YTD");				//E級客戶當月增量
					records[++i] = checkIsNull(map, "I_CL");					//I級客戶當月增量
					records[++i] = checkIsNull(map, "I_CL_YTD");				//I級客戶增量YTD
					records[++i] = checkIsNull(map, "P_CL");					//P級客戶當月增量
					records[++i] = checkIsNull(map, "P_CL_YTD");				//P級客戶增量YTD	
					records[++i] = checkIsNull(map, "EIP_ALL_CL");				//EIP合計當月增量
					records[++i] = checkIsNull(map, "EIP_ALL_CL_GOAL");			//EIP合計當月目標
					records[++i] = checkIsNull(map, "EIP_ALL_CL_YTD");			//EIP合計增量YTD
					records[++i] = checkIsNull(map, "EIP_ALL_CL_YTD_GOAL");		//EIP合計增量目標YTD
					records[++i] = checkIsNull(map, "EIP_ALL_CL_RATE_YTD");		//EIP合計達成率
					records[++i] = checkIsNull(map, "EIP_ALL_CL_SC");			//EIP合計得分
					records[++i] = checkIsNull(map, "AUM_ICMT_ACH_SCORE");		//AUM+客戶數得分
					records[++i] = checkIsNull(map, "FIN_IND_SC");				//財務指標得分
					records[++i] = checkIsNull(map, "UN_FIN_IND_BONUS_RATE");	//非財務指標扣減百分比%
					records[++i] = checkIsNull(map, "LACK_IND_RATE");			//獨立列示重大缺失扣減百分比%
					records[++i] = checkIsNull(map, "LOST_CONTENT");			//缺失內容
					listCSV.add(records);
				}
				//header
				csvHeader = new String[47];
				int j = 0;
				csvHeader[j] = "資料年月";
				csvHeader[++j] = "業務處代碼";
				csvHeader[++j] = "業務處";
				csvHeader[++j] = "營運區代碼";
				csvHeader[++j] = "營運區";
				csvHeader[++j] = "分行代碼";
				csvHeader[++j] = "分行別	";
				csvHeader[++j] = "員工編號";
				csvHeader[++j] = "專員姓名";
				csvHeader[++j] = "AO_CODE";
				csvHeader[++j] = "專員職級";
				//財務指標-手續費收入
				csvHeader[++j] = "手續費收入-加碼後計績收益";
				csvHeader[++j] = "手續費收入-目標";
				csvHeader[++j] = "手續費收入-達成率";
				csvHeader[++j] = "手續費收入-得分";
				//財務指標-存款AUM（不含台定)
				csvHeader[++j] = "存款AUM（不含台定)-當月增量(含台定減量加回)";
				csvHeader[++j] = "存款AUM（不含台定)-當月增量目標";
				csvHeader[++j] = "存款AUM（不含台定)-增量YTD(含台定減量加回)	";
				csvHeader[++j] = "存款AUM（不含台定)-增量目標YTD";
				csvHeader[++j] = "存款AUM（不含台定)-達成率YTD";
				csvHeader[++j] = "存款AUM（不含台定)-得分";
				//財務指標-投保AUM
				csvHeader[++j] = "投保AUM-當月增量";
				csvHeader[++j] = "投保AUM-當月增量目標";
				csvHeader[++j] = "投保AUM-增量YTD";
				csvHeader[++j] = "投保AUM-增量目標YTD";
				csvHeader[++j] = "投保AUM-達成率YTD";
				csvHeader[++j] = "投保AUM-得分";
				csvHeader[++j] = "AUM得分";
				//財務指標-EIP客戶數
				csvHeader[++j] = "EIP客戶數-E級客戶當月增量";
				csvHeader[++j] = "EIP客戶數-E級客戶增量YTD";
				csvHeader[++j] = "EIP客戶數-I級客戶當月增量";
				csvHeader[++j] = "EIP客戶數-I級客戶增量YTD";
				csvHeader[++j] = "EIP客戶數-P級客戶當月增量";
				csvHeader[++j] = "EIP客戶數-P級客戶增量YTD";
				csvHeader[++j] = "EIP客戶數-EIP合計當月增量";
				csvHeader[++j] = "EIP客戶數-EIP合計當月目標";
				csvHeader[++j] = "EIP客戶數-EIP合計增量YTD";
				csvHeader[++j] = "EIP客戶數-EIP合計增量目標YTD";
				csvHeader[++j] = "EIP客戶數-EIP合計達成率";
				csvHeader[++j] = "EIP客戶數-EIP合計得分";
				csvHeader[++j] = "AUM+客戶數得分";
				csvHeader[++j] = "財務指標得分";
				csvHeader[++j] = "非財務指標扣減百分比%";
				csvHeader[++j] = "獨立列示重大缺失扣減百分比%";
				csvHeader[++j] = "缺失內容";
			}
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
	* 檢查Map取出欄位是否為Null  用於理專以及員編
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