package com.systex.jbranch.app.server.fps.pms211;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * 
 * Copy Right Information : Forms Syntron <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.6.0 <br>
 * Description : <br>
 * Comments Name : PMS211.java<br>
 * Author :WKK<br>
 * Date :2016年12月1日 <br>
 * Version : 1.01 <br>
 * Editor : WKK<br>
 * Editor Date : 2016年12月1日<br>
 */
@Component("pms211")
@Scope("request")
public class PMS211 extends FubonWmsBizLogic
{
	public DataAccessManager dam = null;
	
	private Logger logger = LoggerFactory.getLogger(PMS211.class);
	private static Map<String, String> descMap ;
	
	/**
	 * 查詢功能，若查詢結果為空，返回null
	 * @param body
	 * @param header
	 * @return
	 * @throws JBranchException
	 */
	public void queryData(Object body, IPrimitiveMap header) throws JBranchException
	{
		PMS211QueryInputVO inputVO = (PMS211QueryInputVO) body;
		PMS211QueryOutputVO outputVO = new PMS211QueryOutputVO();
		try{
			dam = this.getDataAccessManager();
			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
			sb.append(" 	SELECT                                       ");
			sb.append(" 	       OPEN_SELECT                           ");
			sb.append(" 	      ,VERSION_CHOOSE                        ");
			sb.append(" 	      ,DATA_MONTH                            ");
			sb.append(" 	      ,JOB_BEGIN_TIME                        ");
			sb.append(" 	      ,JOB_END_TIME                          ");
			sb.append(" 	      ,JOB_STATE                             ");
			sb.append(" 	      ,FC_IND_REPORT_FLAG                    ");
			sb.append(" 	      ,FCH_IND_REPORT_FLAG                   ");
			sb.append(" 	      ,ZG_IND_REPORT_FLAG                    ");
			sb.append(" 	      ,FC_FLAG                               ");
			sb.append(" 	      ,FCH_FLAG                              ");
			sb.append(" 	      ,ZG_FLAG                               ");
			sb.append(" 	      ,LZJX_FLAG                             ");
			sb.append(" 	      ,FHSY_FLAG                             ");
			sb.append(" 	FROM TBPMS_PROD_CNR_JOB T                    ");
			sb.append(" 	WHERE T.DATA_MONTH = :DATA_MONTH             ");
			sb.append(" 	      AND T.VERSION_CHOOSE = '1'             ");
			sb.append(" 	UNION ALL                                    ");
			sb.append(" 	SELECT                                       ");
			sb.append(" 	       OPEN_SELECT                           ");
			sb.append(" 	      ,VERSION_CHOOSE                        ");
			sb.append(" 	      ,DATA_MONTH                            ");
			sb.append(" 	      ,JOB_BEGIN_TIME                        ");
			sb.append(" 	      ,JOB_END_TIME                          ");
			sb.append(" 	      ,JOB_STATE                             ");
			sb.append(" 	      ,FC_IND_REPORT_FLAG                    ");
			sb.append(" 	      ,FCH_IND_REPORT_FLAG                   ");
			sb.append(" 	      ,ZG_IND_REPORT_FLAG                    ");
			sb.append(" 	      ,FC_FLAG                               ");
			sb.append(" 	      ,FCH_FLAG                              ");
			sb.append(" 	      ,ZG_FLAG                               ");
			sb.append(" 	      ,LZJX_FLAG                             ");
			sb.append(" 	      ,FHSY_FLAG                             ");
			sb.append(" 	FROM(SELECT                                  ");
			sb.append(" 	           OPEN_SELECT                       ");
			sb.append(" 	          ,VERSION_CHOOSE                    ");
			sb.append(" 	          ,DATA_MONTH                        ");
			sb.append(" 	          ,JOB_BEGIN_TIME                    ");
			sb.append(" 	          ,JOB_END_TIME                      ");
			sb.append(" 	          ,JOB_STATE                         ");
			sb.append(" 	          ,FC_IND_REPORT_FLAG                ");
			sb.append(" 	          ,FCH_IND_REPORT_FLAG               ");
			sb.append(" 	          ,ZG_IND_REPORT_FLAG                ");
			sb.append(" 	          ,FC_FLAG                           ");
			sb.append(" 	          ,FCH_FLAG                          ");
			sb.append(" 	          ,ZG_FLAG                           ");
			sb.append(" 	          ,LZJX_FLAG                         ");
			sb.append(" 	          ,FHSY_FLAG                         ");
			sb.append(" 	    FROM TBPMS_PROD_CNR_JOB T                ");
			sb.append(" 	    WHERE T.DATA_MONTH = :DATA_MONTH         ");
			sb.append(" 	      AND T.VERSION_CHOOSE = '0'             ");
			sb.append(" 	    ORDER BY T.JOB_BEGIN_TIME DESC           ");
			sb.append(" 	    FETCH FIRST 1 ROWS ONLY)                 ");
			qc.setObject("DATA_MONTH", inputVO.getInputDataMonth());
		//	qc.setObject("VERSION_CHOOSE", inputVO.getInputVersionChoose()+"%");
		//	qc.setObject("JOB_BEGIN_TIME", new Date());
	
			qc.setQueryString(sb.toString());
			List<Map<String, Object>> result = dam.exeQuery(qc);
			if(null == result || result.size() <= 0 ){
				sendRtnObject(null);	
			}
			else{
				outputVO.setOutputLargeAgrList(result);
				sendRtnObject(outputVO);	
			}
		}catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
		
	}
	
	/**
	 * 執行存儲過程 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void procedureData(Object body, IPrimitiveMap header) throws JBranchException
	{
		final PMS211InputVO inputVO = (PMS211InputVO) body;
		final PMS211QueryOutputVO outputVO = new PMS211QueryOutputVO();
		try{
			dam = this.getDataAccessManager();
		//	if("0".equals(inputVO.getVersionFlag())){		//最新版 執行存儲過程
			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
			sb.append(" CALL PABTH_BTPMS215.SP_EXEC_EXAM_BATCH(? ,? ,? ,? ,? ,? ) ");
			qc.setString(1, inputVO.getExecDate());
			qc.setString(2, inputVO.getYearMon());
			qc.setString(3, inputVO.getVersionFlag());
			qc.setString(4, inputVO.getSelectFlag());
			qc.setString(5, inputVO.getUserId());
			qc.registerOutParameter(6, Types.VARCHAR);
			qc.setQueryString(sb.toString());
			Map<Integer, Object> resultMap = dam.executeCallable(qc);
			
			/*****************啟動一個線程，執行8個報表修改的存儲過程**************************************/
			new Thread() {
			//	FPTXN670InputVO inputVO = (FPTXN670InputVO) body;
				QueryConditionIF qc = dam.getQueryCondition((byte) 3); 
				StringBuffer sb = new StringBuffer();
					
					@Override
					public void run() {
						sb.append("CALL PABTH_BTPMS215.SP_EXEC_BATCH(? ,? ,? ,?)");
						qc.setString(1, inputVO.getExecDate());
						qc.setString(2, inputVO.getYearMon());
						qc.setString(3, inputVO.getVersionFlag());
						qc.registerOutParameter(4, Types.VARCHAR);
						qc.setQueryString(sb.toString());
						try {
							Map<Integer, Object> resultMap = dam.executeCallable(qc);
							String resultStr = (String) resultMap.get(4);
							/*outputVO.setErrorMsg(resultStr);
							sendRtnObject(outputVO);*/
						} catch (DAOException e) {
							e.printStackTrace();
						} catch (JBranchException e) {
							e.printStackTrace();
						}
					}
					
			}.start();
			/************************************************************************************/
			outputVO.setErrorMsg((String)resultMap.get(6));
			sendRtnObject(outputVO);
			/*}else if("1".equals(inputVO.getVersionFlag())){			//上簽版 執行存儲過程
				//執行存儲過程
				QueryConditionIF qc1 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				StringBuffer sb1 = new StringBuffer();
				sb1.append(" CALL PABTH_BTPMS215.SP_EXEC_EXAM_BATCH(? ,? ,? ,? ,? ,? ) ");
				qc1.setString(1, inputVO.getExecDate());
				qc1.setString(2, inputVO.getYearMon());
				qc1.setString(3, inputVO.getVersionFlag());
				qc1.setString(4, inputVO.getSelectFlag());
				qc1.setString(5, inputVO.getUserId());
				qc1.registerOutParameter(6, Types.VARCHAR);
				qc1.setQueryString(sb1.toString());
				Map<Integer, Object> resultMap = dam.executeCallable(qc1);
				
				*//*****************啟動一個線程，執行8個報表修改的存儲過程**************************************//*
				new Thread() {
				//	FPTXN670InputVO inputVO = (FPTXN670InputVO) body;
					QueryConditionIF qc = dam.getQueryCondition((byte) 3); 
					StringBuffer sb = new StringBuffer();
						
						@Override
						public void run() {
							sb.append("CALL PABTH_BTPMS215.SP_EXEC_BATCH(? ,? ,? ,?)");
							qc.setString(1, inputVO.getExecDate());
							qc.setString(2, inputVO.getYearMon());
							qc.setString(3, inputVO.getVersionFlag());
							qc.registerOutParameter(4, Types.VARCHAR);
							qc.setQueryString(sb.toString());
							try {
								Map<Integer, Object> resultMap = dam.executeCallable(qc);
								String resultStr = (String) resultMap.get(4);
								outputVO.setErrorMsg(resultStr);
								sendRtnObject(outputVO);
							} catch (DAOException e) {
								e.printStackTrace();
							} catch (JBranchException e) {
								e.printStackTrace();
							}
						}
						
				}.start();
				*//************************************************************************************//*
				outputVO.setErrorMsg((String)resultMap.get(6));
				sendRtnObject(outputVO);
			}*/
		}catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
		
	}
	/**
	 * 計算獎勵金
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void caculatePri(Object body, IPrimitiveMap header) throws JBranchException
	{
		PMS211QueryInputVO inputVO = (PMS211QueryInputVO) body;
		PMS211QueryOutputVO outputVO = new PMS211QueryOutputVO();
		try{
			dam = this.getDataAccessManager();
			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
			sb.append("		SELECT PABTH_BTPMS215.FN_EXEC_EXAM_PRE(:YEARMON)			");
			sb.append("			FROM dual												");
			qc.setObject("YEARMON", inputVO.getInputDataMonth());
			qc.setQueryString(sb.toString());
			List<Map<String,Object>> resultList = dam.exeQuery(qc);
			
			//獲取查詢結果中的錯誤信息
			String errorMsg = null;
			Set<Entry<String,Object>> entry = resultList.get(0).entrySet();
			Iterator<Entry<String,Object>> iterator = entry.iterator();
			while(iterator.hasNext()){
				Map.Entry<String, Object> it = iterator.next();
				if(null != it.getValue()){
					errorMsg = (String)it.getValue();
				}
			}
			if(null != errorMsg){
				outputVO.setErrorMsg(errorMsg);
				sendRtnObject(outputVO);
				return;
			}
			
			//若為空，表示没有错误，則繼續執行
			List<Map<String,Object>> result = null;
			String message = null;
			if("0".equals(inputVO.getInputVersionChoose())){
				sb.setLength(0);
				QueryConditionIF qcNew = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb.append(" 	SELECT                                                ");
				sb.append(" 	       COUNT(1)     AS N                              ");
				sb.append(" 	      ,COUNT(CASE WHEN T.OPEN_SELECT IS NOT NULL      ");
				sb.append(" 	                  THEN 1                              ");
				sb.append(" 	             END)   AS M                              ");
				sb.append(" 	FROM TBPMS_PROD_CNR_JOB T                             ");
				sb.append(" 	WHERE T.DATA_MONTH = :YEARMON                         ");
				sb.append(" 	      AND T.VERSION_CHOOSE = '0'                       ");
				qcNew.setObject("YEARMON", inputVO.getInputDataMonth());
				qcNew.setQueryString(sb.toString());
				result = dam.exeQuery(qcNew);
				
				message = "當月最新版已執行"+result.get(0).get("N")+"個次，有"+result.get(0).get("M")+"個版本開放，是否確認執行";
				outputVO.setMessage(message);
				sendRtnObject(outputVO);
			}else if("1".equals(inputVO.getInputVersionChoose())){
				sb.setLength(0);
				QueryConditionIF qcUp = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb.append("  SELECT                                    ");
				sb.append(" 	       COUNT(1)     AS N               ");
				sb.append(" 	FROM TBPMS_PROD_CNR_JOB T              ");
				sb.append(" 	WHERE T.DATA_MONTH = :YEARMON         ");
				sb.append(" 	      AND T.VERSION_CHOOSE = '1'         ");
				qcUp.setObject("YEARMON", inputVO.getInputDataMonth());
				qcUp.setQueryString(sb.toString());
				result = dam.exeQuery(qcUp);
				String upStr = ((BigDecimal)result.get(0).get("N")).toString(); 
				if("0".equals(upStr))
					message = "當月上簽版首次執行知否，是否確認執行";
				else if("1".equals(upStr))
					message = "當月上簽版已執行，是否確認覆蓋執行";
				outputVO.setMessage(message);
				sendRtnObject(outputVO);
			}
		}catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
	
	/**
	 * 保存最新版版本
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void updateData(Object body, IPrimitiveMap header) throws JBranchException
	{
		PMS211InputVO inputVO = (PMS211InputVO) body;
		PMS211QueryOutputVO outputVO = new PMS211QueryOutputVO();
		try{
			dam = this.getDataAccessManager();
			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			String startTime = inputVO.getExecDate();
			String execStr = startTime.substring(0,10).replace("-", "");
			StringBuffer sb = new StringBuffer();
			sb.append(" CALL PABTH_BTPMS215.SP_EXEC_EXAM_BATCH(? ,? ,? ,? ,? ,? ) ");
			qc.setString(1, execStr);
			qc.setString(2, inputVO.getYearMon());
			qc.setString(3, inputVO.getVersionFlag());
			qc.setString(4, inputVO.getSelectFlag());
			qc.setString(5, inputVO.getUserId());
			qc.registerOutParameter(6, Types.VARCHAR);
			qc.setQueryString(sb.toString());
			Map<Integer, Object> resultMap = dam.executeCallable(qc);
			outputVO.setErrorMsg((String)resultMap.get(6));
			sendRtnObject(outputVO);
			
		}catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
	
	public void downloadData(Object body, IPrimitiveMap header) throws JBranchException
	{
		PMS211InputVO inputVO = (PMS211InputVO) body;
		PMS211QueryOutputVO outputVO = new PMS211QueryOutputVO();
		String startTime = inputVO.getExecDate();
		String execStr = startTime.substring(0,10).replace("-", "");	//日期格式为：yyyyMMdd
		List<Map<String,Object>> resultList = null;						//报表查询的结果集
		try{
			if("0".equals(inputVO.getVersionFlag())){			//最新版，傳入3個查詢條件
				resultList = queryReport(inputVO.getTableName(), inputVO.getYearMon(),"0",execStr);
			}else if("1".equals(inputVO.getVersionFlag())){		//上簽版，傳入2個查詢條件
				resultList = queryReport(inputVO.getTableName(), inputVO.getYearMon(),"1");
			}else{
				sendRtnObject(outputVO);
				return;
			}
			if(null == resultList || resultList.size() == 0){
				outputVO.setErrorMsg("查詢報表為空");
				sendRtnObject(outputVO);
				return;
			}
			/************************報表匯出*************************/
			switch (inputVO.getTableName())
			{
			case "TBPMS_FC_FIN_RPT":	
				download_TBPMS_FC_FIN_RPT(resultList);
				break;
			case "TBPMS_FCH_FIN_RPT":
				download_TBPMS_FCH_FIN_RPT(resultList);
				break;
			case "TBPMS_MNGR_FIN_RPT":
				download_TBPMS_MNGR_FIN_RPT(resultList);
				break;
			case "TBPMS_FC_BONUS_RPT":
				download_TBPMS_FC_BONUS_RPT(resultList);
				break;
			case "TBPMS_FCH_BONUS_RPT":
				download_TBPMS_FCH_BONUS_RPT(resultList);
				break;
			case "TBPMS_MNGR_BONUS_RPT":
				download_TBPMS_MNGR_BONUS_RPT(resultList);
				break;
			case "TBPMS_BUY_PROD_RPT":
				download_TBPMS_BUY_PROD_RPT(resultList);
				break;
			case "TBPMS_BARNCH_PROFIT_RPT":
				download_TBPMS_BARNCH_PROFIT_RPT(resultList);
				break;
			}
			
			
			
		}catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
	/**
	 * 查詢個報表的內容
	 * @param strings 傳入若干個查詢條件參數
	 * @return	結果封裝在List<Map<Stirng,Object>>中
	 * @throws JBranchException
	 */
	public List<Map<String,Object>> queryReport(String tableName,String...strings) throws JBranchException
	{
		List<Map<String,Object>> resultList = null;
		try{
			dam = this.getDataAccessManager();
			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
			sb.append(" SELECT * FROM   ");
			sb.append(	tableName	);
			//最新版查詢條件有3個，上簽版查詢條件有2個
			if(strings.length == 2){
				sb.append("	WHERE	YEARMON =  	  ");
				sb.append(strings[0]);
				sb.append(" AND     REPORT_TYPE = ");
				sb.append(strings[1]);
			}else if(strings.length == 3){
				sb.append("	WHERE	YEARMON = 	  ");
				sb.append(strings[0]);
				sb.append(" AND     REPORT_TYPE = ");
				sb.append(strings[1]);
				sb.append(" AND     EXEC_DATE =   ");			
				sb.append(strings[2] );			//测试用数据20161103   
			}
			qc.setQueryString(sb.toString());
			resultList = dam.exeQuery(qc);
			return resultList;
		}catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
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
	 * @throws JBranchException **************************************/
	/*8個方法，對應8個報表的下載*/
	private void download_TBPMS_FC_FIN_RPT(List<Map<String,Object>> resultList) throws JBranchException{		//1. 下載FC理專財務非財務報表
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH:mm:ss");
		String fileName = "FC理專財務非財務報表_" + sdf.format(new Date()) + ".csv"; 
		List listCSV =  new ArrayList();
		String[] csvHeader = null;
				for(Map<String, Object> map : resultList){
					String[] records = new String[47];
					int i = 0;
					records[i] = checkIsNull(map, "YEARMON");  					//資料年月
					records[++i] = checkIsNullAndTrans(map, "REGION_CENTER_ID");  	    //區域中心
					records[++i] = checkIsNull(map, "REGION_CENTER_NAME");  	//區域中心name
					records[++i] = checkIsNullAndTrans(map, "BRANCH_AREA_ID");		    //營運區
					records[++i] = checkIsNull(map, "BRANCH_AREA_NAME");		//營運區	name
					records[++i] = checkIsNullAndTrans(map, "BRANCH_NBR");				//分行代碼
					records[++i] = checkIsNull(map, "BRANCH_NAME");				//分行別		
					records[++i] = checkIsNullAndTrans(map, "EMP_ID");  				//員工編號
					records[++i] = checkIsNull(map, "EMP_NAME");				//專員姓名
					records[++i] = checkIsNullAndTrans(map, "AO_CODE");					//AO_CODE
					records[++i] = checkIsNullAndTrans(map, "JOB_TITLE_ID");			//專員職級
					//財務指標-手續費收入
					records[++i] = checkIsNull(map, "AOVER_GAINS");				//加碼後計績收益		
					records[++i] = checkIsNull(map, "AOVER_GAINS_GOAL");		//目標	
					records[++i] = pcntFormat(map, "FEE_RATE_REACHED");			//達成率		
					records[++i] = checkIsNull(map, "SCORE_FEE_INCOME");		//得分
					//財務指標-存款AUM（不含台定)
					records[++i] = checkIsNull(map, "DEP_AUM_IN");				//當月增量(含台定減量加回)			
					records[++i] = checkIsNull(map, "DEP_AUM_IN_GOAL");			//當月增量目標		
					records[++i] = checkIsNull(map, "DEP_AUM_IN_YTD");			//增量YTD(含台定減量加回)			
					records[++i] = checkIsNull(map, "DEP_AUM_IN_YTD_GOAL");		//增量目標YTD			
					records[++i] = pcntFormat(map, "DEP_AUM_IN_YTD_RATE");		//達成率YTD				
					records[++i] = checkIsNull(map, "DEP_AUM_SC");				//得分				
					//財務指標-投保AUM
					records[++i] = checkIsNull(map, "INC_INSU_AUM");			//當月增量
					records[++i] = checkIsNull(map, "INC_INSU_AUM_GOAL");		//當月增量目標		
					records[++i] = checkIsNull(map, "INC_INSU_AUM_YTD");		//增量YTD
					records[++i] = checkIsNull(map, "INC_INSU_AUM_YTD_GOAL");	//增量目標YTD			
					records[++i] = pcntFormat(map, "INC_INSU_AUM_YTD_RATE");	//達成率YTD				
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
					records[++i] = pcntFormat(map, "EIP_ALL_CL_RATE_YTD");		//EIP合計達成率
					records[++i] = checkIsNull(map, "EIP_ALL_CL_SC");			//EIP合計得分
					records[++i] = checkIsNull(map, "AUM_ICMT_ACH_SCORE");		//AUM+客戶數得分
					records[++i] = checkIsNull(map, "FIN_IND_SC");				//財務指標得分
					records[++i] = pcntFormat(map, "UN_FIN_IND_BONUS_RATE");	//非財務指標扣減百分比%
					records[++i] = pcntFormat(map, "LACK_IND_RATE");			//獨立列示重大缺失扣減百分比%
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
		
		CSVUtil csv = new CSVUtil();
		csv.setHeader(csvHeader);
		csv.addRecordList(listCSV);
		String url = csv.generateCSV();
		notifyClientToDownloadFile(url, fileName); //download
		
	}
	private void download_TBPMS_FCH_FIN_RPT(List<Map<String,Object>> resultList) throws JBranchException{			//2. 下載FCH理專財務非財務報表
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH:mm:ss");
		String fileName = "FCH理專財務非財務報表_" + sdf.format(new Date()) + ".csv"; 
		List listCSV = new ArrayList();
		String[] csvHeader = null;
		for (Map<String, Object> map : resultList) 
		{
			String[] records = new String[41];
			int i = 0;
			records[i] = checkIsNull(map, "YEARMON");
			records[++i] = checkIsNullAndTrans(map, "REGION_CENTER_ID");
			records[++i] = checkIsNull(map, "REGION_CENTER_NAME");
			records[++i] = checkIsNullAndTrans(map, "BRANCH_AREA_ID");
			records[++i] = checkIsNull(map, "BRANCH_AREA_NAME");
			records[++i] = checkIsNullAndTrans(map, "BRANCH_NBR");
			records[++i] = checkIsNull(map, "BRANCH_NAME");
			records[++i] = checkIsNullAndTrans(map, "EMP_ID");
			records[++i] = checkIsNull(map, "EMP_NAME");
			records[++i] = checkIsNullAndTrans(map, "AO_CODE");
			records[++i] = checkIsNull(map, "JOB_TITLE_ID");
			records[++i] = checkIsNull(map, "AOVER_GAINS");
			records[++i] = checkIsNull(map, "AOVER_GAINS_GOAL_MUL");
			records[++i] = checkIsNull(map, "AOVER_GAINS_GOAL");
			records[++i] = pcntFormat(map, "FEE_RATE_REACHED_GOAL");
			records[++i] = pcntFormat(map, "FEE_RATE_REACHED");
			records[++i] = checkIsNull(map, "SCORE_FEE_INCOME");
			records[++i] = checkIsNull(map, "DEP_AUM_IN");
			records[++i] = checkIsNull(map, "DEP_AUM_IN_GOAL");
			records[++i] = checkIsNull(map, "DEP_AUM_IN_YTD");
			records[++i] = checkIsNull(map, "DEP_AUM_IN_YTD_GOAL");
			records[++i] = pcntFormat(map, "DEP_AUM_IN_YTD_RATE");
			records[++i] = checkIsNull(map, "DEP_AUM_SC");
			records[++i] = checkIsNull(map, "INC_INSU_AUM");
			records[++i] = checkIsNull(map, "INC_INSU_AUM_GOAL");
			records[++i] = checkIsNull(map, "INC_INSU_AUM_YTD");
			records[++i] = checkIsNull(map, "INC_INSU_AUM_YTD_GOAL");
			records[++i] = pcntFormat(map, "INC_AUM_IN_YTD_RATE");
			records[++i] = checkIsNull(map, "INC_AUM_SC");
			records[++i] = checkIsNull(map, "AUM_SC");
			records[++i] = checkIsNull(map, "NEW_CUST_CL");
			records[++i] = checkIsNull(map, "NEW_CUST_CL_GOAL");
			records[++i] = checkIsNull(map, "NEW_CUST_CL_YTD");
			records[++i] = checkIsNull(map, "NEW_CUST_CL_YTD_GOAL");
			records[++i] = pcntFormat(map, "NEW_CUST_CL_RATE_YTD");
			records[++i] = checkIsNull(map, "NEW_CUST_CL_SC");
			records[++i] = checkIsNull(map, "AUM_ICMT_ACH_SCORE");
			records[++i] = checkIsNull(map, "FIN_IND_SC");
			records[++i] = pcntFormat(map, "UN_FIN_IND_BONUS_RATE");
			records[++i] = pcntFormat(map, "LACK_IND_RATE");
			records[++i] = checkIsNull(map, "LOST_CONTENT");
			listCSV.add(records);
		}
		csvHeader = new String[41];
		int j = 0;
		csvHeader[j] = "資料年月";
		csvHeader[++j] = "區域中心代碼";
		csvHeader[++j] = "區域中心名稱";
		csvHeader[++j] = "營運區代碼";
		csvHeader[++j] = "營運區名稱";
		csvHeader[++j] = "分行代碼";
		csvHeader[++j] = "分行別";
		csvHeader[++j] = "員工編號";
		csvHeader[++j] = "專員姓名";
		csvHeader[++j] = "AO_CODE";
		csvHeader[++j] = "專員職級";
		csvHeader[++j] = "財務指標-手續費收入-加碼後計績收益";
		csvHeader[++j] = "財務指標-手續費收入-目標(本薪倍數)";
		csvHeader[++j] = "財務指標-手續費收入-目標";
		csvHeader[++j] = "財務指標-手續費收入-達成率(本薪倍數)";
		csvHeader[++j] = "財務指標-手續費收入-達成率";
		csvHeader[++j] = "財務指標-手續費收入-得分";
		csvHeader[++j] = "財務指標-存款AUM（不含台定)-當月增量(含台定減量加回)";
		csvHeader[++j] = "財務指標-存款AUM（不含台定)-當月增量目標";
		csvHeader[++j] = "財務指標-存款AUM（不含台定)-增量YTD(含台定減量加回)";
		csvHeader[++j] = "財務指標-存款AUM（不含台定)-增量目標YTD";
		csvHeader[++j] = "財務指標-存款AUM（不含台定)-達成率YTD";
		csvHeader[++j] = "財務指標-存款AUM（不含台定)-得分";
		csvHeader[++j] = "財務指標-投保AUM-當月增量";
		csvHeader[++j] = "財務指標-投保AUM-當月增量目標";
		csvHeader[++j] = "財務指標-投保AUM-增量YTD";
		csvHeader[++j] = "財務指標-投保AUM-增量目標YTD";
		csvHeader[++j] = "財務指標-投保AUM-達成率YTD";
		csvHeader[++j] = "財務指標-投保AUM-得分";
		csvHeader[++j] = "AUM得分";
		csvHeader[++j] = "新客戶數-當月增量";
		csvHeader[++j] = "新客戶數-當月目標";
		csvHeader[++j] = "新客戶數-增量YTD";
		csvHeader[++j] = "新客戶數-增量目標YTD";
		csvHeader[++j] = "新客戶數-達成率YTD";
		csvHeader[++j] = "新客戶數-得分";
		csvHeader[++j] = "AUM+客戶數得分";
		csvHeader[++j] = "財務指標得分";
		csvHeader[++j] = "非財務指標扣減百分比%";
		csvHeader[++j] = "獨立列示重大缺失扣減百分比%";
		csvHeader[++j] = "缺失內容";
		CSVUtil csv = new CSVUtil();
		csv.setHeader(csvHeader);
		csv.addRecordList(listCSV);
		String url = csv.generateCSV();
		notifyClientToDownloadFile(url, fileName); //download
	}
	private void download_TBPMS_MNGR_FIN_RPT(List<Map<String,Object>> resultList) throws JBranchException{					//3. 下載主管財務非財務報表
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH:mm:ss");
		String fileName = "主管財務非財務報表_" + sdf.format(new Date()) + ".csv"; 
		List listCSV =  new ArrayList();
		String[] csvHeader = null;
		for(Map<String, Object> map : resultList){
				String[] records = new String[58];
				int i = 0;
				records[i] = checkIsNull(map, "YEARMON");                         //資料年月
				records[++i] = checkIsNullAndTrans(map, "REGION_CENTER_ID");            //區域中心
				records[++i] = checkIsNull(map, "REGION_CENTER_NAME");            //區域中心
				records[++i] = checkIsNullAndTrans(map, "BRANCH_AREA_ID");              //營運區
				records[++i] = checkIsNull(map, "BRANCH_AREA_NAME");              //營運區
				records[++i] = checkIsNullAndTrans(map, "BRANCH_NBR");                    //分行代碼/母行分行代碼
				records[++i] = checkIsNull(map, "BRANCH_NAME");                   //分別行/母行別
				records[++i] = checkIsNull(map, "GROUP_ID");                      //分行組別/區組別
				records[++i] = checkIsNull(map, "MANAGER_GROUP");                 //業務主管組別
				records[++i] = descFormat(map, "PERSON_TYPE_NOTE");                 //人員類別註記(0:主角色1:兼任/2:暫代)
				records[++i] = checkIsNull(map, "PERSON_TYPE");                   //人員類別
				records[++i] = checkIsNullAndTrans(map, "EMP_ID");                        //員工編號
				records[++i] = checkIsNull(map, "EMP_NAME");                      //員工姓名
				records[++i] = checkIsNull(map, "INS_INCOME");                    //投保收益(a)
				records[++i] = checkIsNull(map, "INS_GOAL");                      //投保收益目標(b)
				records[++i] = checkIsNull(map, "DEP_DIFF");                      //存款利差(c)
				records[++i] = checkIsNull(map, "DEP_DIFF_GOAL");                 //存款利差目標(d)
				records[++i] = pcntFormat(map, "INS_YIELD_RATE");                //存投保達成率=(a+c)/(b+d)
				records[++i] = checkIsNull(map, "INS_SCORE");                     //存投保得分
				records[++i] = checkIsNull(map, "DEP_AUM_IN");                    //當月增量(含台定減量加回)
				records[++i] = checkIsNull(map, "DEP_AUM_IN_GOAL");               //當月增量目標
				records[++i] = checkIsNull(map, "DEP_AUM_IN_YTD");                //增量YTD(含台定減量加回)
				records[++i] = checkIsNull(map, "DEP_AUM_IN_YTD_GOAL");           //增量目標YTD
				records[++i] = pcntFormat(map, "DEP_AUM_IN_YTD_RATE");           //達成率YTD
				records[++i] = checkIsNull(map, "DEP_AUM_SC");                    //得分
				records[++i] = checkIsNull(map, "INC_INSU_AUM");                  //當月增量
				records[++i] = checkIsNull(map, "INC_INSU_AUM_GOAL");             //當月增量目標
				records[++i] = checkIsNull(map, "INC_INSU_AUM_YTD");              //增量YTD
				records[++i] = checkIsNull(map, "INC_INSU_AUM_YTD_GOAL");         //增量目標YTD
				records[++i] = pcntFormat(map, "INC_INSU_AUM_YTD_RATE");         //達成率YTD
				records[++i] = checkIsNull(map, "INC_AUM_SC");                    //得分
				records[++i] = checkIsNull(map, "AUM_SC");                        //AUM得分
				records[++i] = checkIsNull(map, "E_CL");                          //E級客戶當月增量
				records[++i] = checkIsNull(map, "E_CL_YTD");                      //E級客戶增量YTD
				records[++i] = checkIsNull(map, "I_CL");                          //I級客戶當月增量
				records[++i] = checkIsNull(map, "I_CL_YTD");                      //I級客戶增量YTD
				records[++i] = checkIsNull(map, "P_CL");                          //P級客戶當月增量
				records[++i] = checkIsNull(map, "P_CL_YTD");                      //P級客戶增量YTD
				records[++i] = checkIsNull(map, "EIP_ALL_CL");                    //EIP合計當月增量
				records[++i] = checkIsNull(map, "EIP_ALL_CL_GOAL");               //EIP合計當月目標
				records[++i] = checkIsNull(map, "EIP_ALL_CL_YTD");                //EIP合計增量YTD
				records[++i] = checkIsNull(map, "EIP_ALL_CL_YTD_GOAL");           //EIP合計增量目標YTD
				records[++i] = checkIsNull(map, "NEW_CUST_CL");                   //當月增量
				records[++i] = checkIsNull(map, "NEW_CUST_CL_GOAL");              //當月增量目標
				records[++i] = checkIsNull(map, "NEW_CUST_CL_YTD");               //增量YTD
				records[++i] = checkIsNull(map, "NEW_CUST_CL_YTD_GOAL");         //增量目標YTD
				records[++i] = pcntFormat(map, "CUST_ACH");                      //客戶數達成率
				records[++i] = checkIsNull(map, "CUST_SCORE");                    //客戶數得分
				records[++i] = checkIsNull(map, "AUM_ICMT_ACH_SCORE");            //AUM+客戶數得分
				records[++i] = checkIsNull(map, "FIN_IND_SC");                    //財務指標得分
				records[++i] = pcntFormat(map, "UN_FIN_IND_BONUS_RATE");         //非財務指標扣減百分比%
				records[++i] = pcntFormat(map, "LACK_IND_RATE");                 //獨立列示重大缺失扣減百分比
				records[++i] = checkIsNull(map, "LOST_CONTENT");                  //缺失內容
				listCSV.add(records);
			}
			//header
			csvHeader = new String[57];
			int j = 0;
			csvHeader[j] = "資料年月";
			csvHeader[++j] = "業務處代碼";
			csvHeader[++j] = "業務處名稱";
			csvHeader[++j] = "營運區代碼";
			csvHeader[++j] = "營運區名稱";
			csvHeader[++j] = "分行代碼/母行分行代碼";
			csvHeader[++j] = "分行別/母行別";
			csvHeader[++j] = "分行組別/區組別";
			csvHeader[++j] = "業務主管組別";
			csvHeader[++j] = "人員類別";
			csvHeader[++j] = "類別註記";
			csvHeader[++j] = "員工編號";
			csvHeader[++j] = "員工姓名";
			
			csvHeader[++j] = "存投保-投保收益(a)";
			csvHeader[++j] = "存投保-投保收益目標(b)";
			csvHeader[++j] = "存投保-存款利差(c)";
			csvHeader[++j] = "存投保-存款利差目標(d)";
			csvHeader[++j] = "存投保-存投保達成率=(a+c)/(b+d)";
			csvHeader[++j] = "存投保-存投保得分";
			csvHeader[++j] = "存款AUM-當月增量(含台定減量加回)";
			csvHeader[++j] = "存款AUM-當月增量目標";
			csvHeader[++j] = "存款AUM-增量YTD(含台定減量加回)";
			csvHeader[++j] = "存款AUM-增量目標YTD";
			csvHeader[++j] = "存款AUM-達成率YTD";
			csvHeader[++j] = "存款AUM-得分";
			csvHeader[++j] = "投保AUM-當月增量";
			csvHeader[++j] = "投保AUM-當月增量目標";
			csvHeader[++j] = "投保AUM-增量YTD";
			csvHeader[++j] = "投保AUM-增量目標YTD";
			csvHeader[++j] = "投保AUM-達成率YTD";
			csvHeader[++j] = "投保AUM-得分";
			csvHeader[++j] = "AUM得分";
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
			csvHeader[++j] = "新客戶數-當月增量";
			csvHeader[++j] = "新客戶數-當月增量目標";
			csvHeader[++j] = "新客戶數-增量YTD";
			csvHeader[++j] = "新客戶數-增量目標YTD";
			csvHeader[++j] = "客戶數達成率";
			csvHeader[++j] = "客戶數得分";
			csvHeader[++j] = "AUM+客戶數得分";
			csvHeader[++j] = "財務指標得分";
			csvHeader[++j] = "非財務指標扣減百分比%";
			csvHeader[++j] = "獨立列示重大缺失扣減百分比";
			csvHeader[++j] = "缺失內容";
		CSVUtil csv = new CSVUtil();
		csv.setHeader(csvHeader);
		csv.addRecordList(listCSV);
		String url = csv.generateCSV();
		notifyClientToDownloadFile(url, fileName); //download
	}
	private void download_TBPMS_FC_BONUS_RPT(List<Map<String,Object>> resultList) throws JBranchException{					//4.  下載FC獎勵金報表
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH:mm:ss");
		String fileName = "FC獎勵金查詢_" + sdf.format(new Date()) + ".csv"; 
		List listCSV = new ArrayList();
		String[] csvHeader = null;
		for (Map<String, Object> map : resultList)
		{
			String[] records = new String[53];
			int i = 0;
			records[i] = checkIsNull(map, "YEARMON");
			records[++i] = checkIsNullAndTrans(map, "REGION_CENTER_ID");
			records[++i] = checkIsNull(map, "REGION_CENTER_NAME");
			records[++i] = checkIsNullAndTrans(map, "BRANCH_AREA_ID");
			records[++i] = checkIsNull(map, "BRANCH_AREA_NAME");
			records[++i] = checkIsNullAndTrans(map, "BRANCH_NBR");
			records[++i] = checkIsNull(map, "BRANCH_NAME");
			records[++i] = checkIsNullAndTrans(map, "EMP_ID");
			records[++i] = checkIsNull(map, "EMP_NAME");
			records[++i] = checkIsNullAndTrans(map, "AO_CODE");
			records[++i] = checkIsNullAndTrans(map, "JOB_TITLE_ID");
			records[++i] = checkIsNull(map, "FC_DEPOSIT");
			records[++i] = checkIsNull(map, "FC_LFUND_SIN");
			records[++i] = checkIsNull(map, "FC_LFUND_SMA");
			records[++i] = checkIsNull(map, "FC_LFUND_STR");
			records[++i] = checkIsNull(map, "FC_OLUND_SIN");
			records[++i] = checkIsNull(map, "FC_OLUND_SMA");
			records[++i] = checkIsNull(map, "FC_OLUND_STR");
			records[++i] = checkIsNull(map, "FC_FUND_MNG_FEE");
			records[++i] = checkIsNull(map, "FC_DCI");
			records[++i] = checkIsNull(map, "FC_SI");
			records[++i] = checkIsNull(map, "FC_ETF");
			records[++i] = checkIsNull(map, "FC_TPCC");
			records[++i] = checkIsNull(map, "FC_OSEA");
			records[++i] = checkIsNull(map, "FC_SN");
			records[++i] = checkIsNull(map, "FC_INS_SG");
			records[++i] = checkIsNull(map, "FC_INS_SQ_FIR");
			records[++i] = checkIsNull(map, "FC_INS_SQ_CON");
			records[++i] = checkIsNull(map, "FC_TRUST_FEE");
			records[++i] = checkIsNull(map, "FC_GOLD");
			records[++i] = checkIsNull(map, "COMBINED");
			records[++i] = checkIsNull(map, "REAL_INCOME");
			records[++i] = checkIsNull(map, "STAND_GOAL");
			records[++i] = checkIsNull(map, "PRFT_GOAL");
			records[++i] = checkIsNull(map, "FC_FC_TATOL");
			records[++i] = checkIsNull(map, "FC_INDCT");
			records[++i] = checkIsNull(map, "FIN_DEDUC_AMT");
			records[++i] = checkIsNull(map, "UN_FIN_DEDUCAMT");
			records[++i] = checkIsNull(map, "LACK_IND_AMT");
			records[++i] = checkIsNull(map, "NEW_REF_DEDUCT_AMT");
			records[++i] = checkIsNull(map, "PREVIOUS_DEDUC_AMT");
			records[++i] = checkIsNull(map, "REAL_DEDUC_AMT");
			records[++i] = checkIsNull(map, "FC_BONUS_CNR100");
			records[++i] = checkIsNull(map, "FC_BONUS_CNR80");
			records[++i] = checkIsNull(map, "FC_BONUS_CNR20");
			records[++i] = checkIsNull(map, "FC_BONUS_CNR");
			records[++i] = checkIsNull(map, "FC_BONUS_CNRADJ");
			records[++i] = checkIsNull(map, "FC_BONUS_CNR_REAL");
			records[++i] = checkIsNull(map, "RANKING");
			records[++i] = checkIsNull(map, "THRESHOLD_NUM_100");
			records[++i] = checkIsNull(map, "FC_BONUS_CNRADJ_NOTE");
			records[++i] = checkIsNull(map, "LEAVE");
			listCSV.add(records);
		}
		csvHeader = new String[53];
		int j = 0;
		csvHeader[j] = "資料統計月份";
		csvHeader[++j] = "業務處代碼";
		csvHeader[++j] = "業務處名稱";
		csvHeader[++j] = "營運區代碼";
		csvHeader[++j] = "營運區名稱";
		csvHeader[++j] = "分行ID";
		csvHeader[++j] = "分行名稱";
		csvHeader[++j] = "理專員編";
		csvHeader[++j] = "理專姓名";
		csvHeader[++j] = "AOCODE";
		csvHeader[++j] = "專員職級";
		csvHeader[++j] = "計績收益(加碼後)-存匯類商品";
		csvHeader[++j] = "計績收益(加碼後)-國內基金-單筆";
		csvHeader[++j] = "計績收益(加碼後)-國內基金-小額";
		csvHeader[++j] = "計績收益(加碼後)-國內基金短Trade(減項)";
		csvHeader[++j] = "計績收益(加碼後)-國外基金-單筆(含轉換)";
		csvHeader[++j] = "計績收益(加碼後)-國外基金-小額";
		csvHeader[++j] = "計績收益(加碼後)-國外基金短Trade(減項)";
		csvHeader[++j] = "計績收益(加碼後)-基金管理費";
		csvHeader[++j] = "計績收益(加碼後)-DCI";
		csvHeader[++j] = "計績收益(加碼後)-SI";
		csvHeader[++j] = "計績收益(加碼後)-ETF";
		csvHeader[++j] = "計績收益(加碼後)-海外股票";
		csvHeader[++j] = "計績收益(加碼後)-海外債券";
		csvHeader[++j] = "計績收益(加碼後)-SN";
		csvHeader[++j] = "計績收益(加碼後)-保險-躉繳";
		csvHeader[++j] = "計績收益(加碼後)-保險-分期繳首年";
		csvHeader[++j] = "計績收益(加碼後)-保險-分期繳續年";
		csvHeader[++j] = "計績收益(加碼後)-信託商品";
		csvHeader[++j] = "計績收益(加碼後)-黃金存摺";
		csvHeader[++j] = "計績收益(加碼後)-合計";
		csvHeader[++j] = "實際收益";
		csvHeader[++j] = "標準生產力目標";
		csvHeader[++j] = "收益目標";
		csvHeader[++j] = "加碼後計績收益達成率";
		csvHeader[++j] = "個人淨貢獻";
		csvHeader[++j] = "財務指標扣減金額";
		csvHeader[++j] = "非財務指標扣減金額";
		csvHeader[++j] = "獨立列示重大缺失扣減金額";
		csvHeader[++j] = "新戶轉介扣減金額";
		csvHeader[++j] = "上期遞延扣減金額";
		csvHeader[++j] = "實際扣減金額(本月+上期遞延)";
		csvHeader[++j] = "業務獎勵金100%";
		csvHeader[++j] = "業務獎勵金80%";
		csvHeader[++j] = "業務獎勵金20%";
		csvHeader[++j] = "獎金率";
		csvHeader[++j] = "業務獎勵金應調整數";
		csvHeader[++j] = "業務獎勵金實發";
		csvHeader[++j] = "名次(依據業務獎勵金實發)";
		csvHeader[++j] = "達100%門檻人數";
		csvHeader[++j] = "業務獎勵金調整說明";
		csvHeader[++j] = "離職日";
	
		CSVUtil csv = new CSVUtil();
		csv.setHeader(csvHeader);
		csv.addRecordList(listCSV);
		String url = csv.generateCSV();
		notifyClientToDownloadFile(url, fileName); //download
	}
	private void download_TBPMS_FCH_BONUS_RPT(List<Map<String,Object>> resultList) throws JBranchException{					//5.  下載FCH獎勵金報表
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH:mm:ss");
		String fileName = "FCH獎勵金查詢_" + sdf.format(new Date()) + ".csv";
		List listCSV = new ArrayList();
		String[] csvHeader = null;
		for (Map<String, Object> map : resultList) {
			String[] records = new String[61];
			int i = 0;
			records[i] = checkIsNull(map, "YEARMON");
			records[++i] = checkIsNullAndTrans(map, "REGION_CENTER_ID");
			records[++i] = checkIsNull(map, "REGION_CENTER_NAME");
			records[++i] = checkIsNullAndTrans(map, "BRANCH_AREA_ID");
			records[++i] = checkIsNull(map, "BRANCH_AREA_NAME");
			records[++i] = checkIsNullAndTrans(map, "BRANCH_NBR");
			records[++i] = checkIsNull(map, "BRANCH_NAME");
			records[++i] = checkIsNullAndTrans(map, "EMP_ID");
			records[++i] = checkIsNull(map, "EMP_NAME");
			records[++i] = checkIsNullAndTrans(map, "AO_CODE");
			records[++i] = checkIsNull(map, "JOB_TITLE_ID");
			records[++i] = checkIsNull(map, "FC_DEPOSIT");
			records[++i] = checkIsNull(map, "FC_LFUND_SIN");
			records[++i] = checkIsNull(map, "FC_LFUND_SMA");
			records[++i] = checkIsNull(map, "FC_LFUND_STR");
			records[++i] = checkIsNull(map, "FC_OLUND_SIN");
			records[++i] = checkIsNull(map, "FC_OLUND_SMA");
			records[++i] = checkIsNull(map, "FC_OLUND_STR");
			records[++i] = checkIsNull(map, "FC_FUND_MNG_FEE");
			records[++i] = checkIsNull(map, "FC_DCI");
			records[++i] = checkIsNull(map, "FC_SI");
			records[++i] = checkIsNull(map, "FC_ETF");
			records[++i] = checkIsNull(map, "FC_TPCC");
			records[++i] = checkIsNull(map, "FC_OSEA");
			records[++i] = checkIsNull(map, "FC_SN");
			records[++i] = checkIsNull(map, "FC_INS_SG");
			records[++i] = checkIsNull(map, "FC_INS_SQ_FIR");
			records[++i] = checkIsNull(map, "FC_INS_SQ_CON");
			records[++i] = checkIsNull(map, "FC_TRUST_FEE");
			records[++i] = checkIsNull(map, "FC_GOLD");
			records[++i] = checkIsNull(map, "COMBINED");
			records[++i] = checkIsNull(map, "REAL_INCOME");
			records[++i] = checkIsNull(map, "NOTIONAL_AMT");
			records[++i] = checkIsNull(map, "EARN_GOAL");
			records[++i] = checkIsNull(map, "XPAD_EARN_GOAL");
			records[++i] = checkIsNull(map, "INCOME_OW_RATE");
			records[++i] = checkIsNull(map, "XPAD_INCOME_GOAL");
			records[++i] = checkIsNull(map, "INS_LEVEL1");
			records[++i] = checkIsNull(map, "INS_LEVEL2");
			records[++i] = checkIsNull(map, "INS_LEVEL3");
			records[++i] = checkIsNull(map, "INS_LEVEL4");
			records[++i] = checkIsNull(map, "NEW_CUST_DEV_BONUS");
			records[++i] = checkIsNull(map, "REFER_PERFOR_GAIN");
			records[++i] = checkIsNull(map, "REFER_BONUS");
			records[++i] = checkIsNull(map, "PERFOR_BONUS");
			records[++i] = checkIsNull(map, "FIN_DEDUC_AMT");
			records[++i] = checkIsNull(map, "UN_FIN_DEDUCAMT");
			records[++i] = checkIsNull(map, "LACK_IND_AMT");
			records[++i] = checkIsNull(map, "NEW_REF_DEDUCT_AMT");
			records[++i] = checkIsNull(map, "PREVIOUS_DEDUC_AMT");
			records[++i] = checkIsNull(map, "REAL_DEDUC_AMT");
			records[++i] = checkIsNull(map, "FC_BONUS_CNR100");
			records[++i] = checkIsNull(map, "FC_BONUS_CNR80");
			records[++i] = checkIsNull(map, "FC_BONUS_CNR20");
			records[++i] = checkIsNull(map, "FC_BONUS_CNR");
			records[++i] = checkIsNull(map, "FC_BONUS_CNRADJ");
			records[++i] = checkIsNull(map, "FC_BONUS_CNR_REAL");
			records[++i] = checkIsNull(map, "RANKING");
			records[++i] = checkIsNull(map, "THRESHOLD_NUM_100");
			records[++i] = checkIsNull(map, "FC_BONUS_CNRADJ_NOTE");
			listCSV.add(records);
		}
		csvHeader = new String[61];
		int j = 0;
		csvHeader[j] = "資料年月";
		csvHeader[++j] = "業務處代碼";
		csvHeader[++j] = "業務處名稱";
		csvHeader[++j] = "營運區代碼";
		csvHeader[++j] = "營運區名稱";
		csvHeader[++j] = "分行代碼";
		csvHeader[++j] = "分行名稱";
		csvHeader[++j] = "專員員編";
		csvHeader[++j] = "專員姓名";
		csvHeader[++j] = "AO_CODE";
		csvHeader[++j] = "理專職級";
		csvHeader[++j] = "計績收益(加碼後)-存匯類商品";
		csvHeader[++j] = "計績收益(加碼後)-國內基金-單筆";
		csvHeader[++j] = "計績收益(加碼後)-國內基金-小額";
		csvHeader[++j] = "計績收益(加碼後)-國內基金-短Trade(減項)";
		csvHeader[++j] = "計績收益(加碼後)-國外基金-單筆(含轉換)";
		csvHeader[++j] = "計績收益(加碼後)-國外基金-小額";
		csvHeader[++j] = "計績收益(加碼後)-國外基金-短Trade(減項)";
		csvHeader[++j] = "計績收益(加碼後)-基金管理費";
		csvHeader[++j] = "計績收益(加碼後)-DCI";
		csvHeader[++j] = "計績收益(加碼後)-SI";
		csvHeader[++j] = "計績收益(加碼後)-ETF";
		csvHeader[++j] = "計績收益(加碼後)-海外股票";
		csvHeader[++j] = "計績收益(加碼後)-海外債券";
		csvHeader[++j] = "計績收益(加碼後)-SN";
		csvHeader[++j] = "計績收益(加碼後)-保險-躉繳";
		csvHeader[++j] = "計績收益(加碼後)-保險-分期繳首年";
		csvHeader[++j] = "計績收益(加碼後)-保險-分期繳續年";
		csvHeader[++j] = "計績收益(加碼後)-信託商品";
		csvHeader[++j] = "計績收益(加碼後)-黃金存摺";
		csvHeader[++j] = "計績收益(加碼後)-加總";
		csvHeader[++j] = "實際收益";
		csvHeader[++j] = "承作量";
		csvHeader[++j] = "收益目標";
		csvHeader[++j] = "理財收益目標";
		csvHeader[++j] = "加碼後計績收益達成率";
		csvHeader[++j] = "理財收益達GOAL獎金(1)";
		csvHeader[++j] = "新戶數-50萬~100萬含投保(每戶1000元)";
		csvHeader[++j] = "新戶數-100萬~300萬(每戶1500元)";
		csvHeader[++j] = "新戶數-300萬~1500萬(每戶3000元)";
		csvHeader[++j] = "新戶數-1500萬以上(每戶5000元)";
		csvHeader[++j] = "新戶開發獎金(2)";
		csvHeader[++j] = "轉介計績收益";
		csvHeader[++j] = "轉介獎金(3)[轉介計績收益* 4%]";
		csvHeader[++j] = "績效獎勵金(4)=(1)+(2)+(3)";
		csvHeader[++j] = "財務指標扣減金額";
		csvHeader[++j] = "非財務指標扣減金額";
		csvHeader[++j] = "獨立列示重大缺失扣減金額";
		csvHeader[++j] = "新戶轉介扣減金額";
		csvHeader[++j] = "上期遞延扣減金額";
		csvHeader[++j] = "實際扣減金額(本月+上期遞延)";
		csvHeader[++j] = "業務獎勵金100%";
		csvHeader[++j] = "業務獎勵金80%";
		csvHeader[++j] = "業務獎勵金20%";
		csvHeader[++j] = "獎金率";
		csvHeader[++j] = "業務獎勵金應調整數";
		csvHeader[++j] = "業務獎勵金實發";
		csvHeader[++j] = "名次(依據業務獎勵金實發)";
		csvHeader[++j] = "達100%門檻人數";
		csvHeader[++j] = "業務獎勵金調整說明";
	
		CSVUtil csv = new CSVUtil();
		csv.setHeader(csvHeader);
		csv.addRecordList(listCSV);
		String url = csv.generateCSV();
		notifyClientToDownloadFile(url, fileName); //download
	}
	private void download_TBPMS_MNGR_BONUS_RPT(List<Map<String,Object>> resultList) throws JBranchException{					//6.  下載主管獎勵金報表
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH:mm:ss");
		String fileName = "主管獎勵金報表_" + sdf.format(new Date()) + ".csv"; 
		List listCSV =  new ArrayList();
		String[] csvHeader = null;
		for(Map<String, Object> map : resultList){
			String[] records = new String[37];
			int i = 0;
			records[i] = checkIsNull(map, "YEARMON");                           //資料年月
			records[++i] = checkIsNullAndTrans(map, "REGION_CENTER_ID");                //區域中心代碼
			records[++i] = checkIsNull(map, "REGION_CENTER_NAME");              //區域中心
			records[++i] = checkIsNullAndTrans(map, "BRANCH_AREA_ID");                  //營運區代碼
			records[++i] = checkIsNull(map, "BRANCH_AREA_NAME");                //營運區
			records[++i] = checkIsNullAndTrans(map, "BRANCH_NBR");                      //分行代碼母行分行代碼
			records[++i] = checkIsNull(map, "BRANCH_NAME");             	    //分別行母行別
			records[++i] = checkIsNull(map, "PERSON_TYPE");             		//人員類別
			records[++i] = checkIsNullAndTrans(map, "EMP_ID");             			    //員工編號
			records[++i] = checkIsNull(map, "EMP_NAME");            		    //員工姓名
			records[++i] = checkIsNull(map, "SCORE_FIN_INDEX");                 //財務指標得分
			records[++i] = checkIsNull(map, "BONUS_FIN_INDEX");                 //財務指標績效獎金
			records[++i] = checkIsNull(map, "FC_ALL_NUM");             		    //應有轄下專員總人數
			records[++i] = checkIsNull(map, "FC_ACH_PROD_NUM");                 //達標準生產力專員人數
			records[++i] = checkIsNull(map, "FC_ACH_PROD_RATE");                //專員達標準生產力占比
			records[++i] = checkIsNull(map, "FC_ACH_GOAL_BONUS_RATE");          //專員達GOAL率對應獎勵金成數
			records[++i] = checkIsNull(map, "NEW_OR_TRANS_YR_MN");              //新、調任年月
			records[++i] = checkIsNull(map, "NEW_OR_TRANS_MN_NUM");             //新、調任分行任職月份數
			records[++i] = checkIsNull(map, "ORIG_BRANCH_NBR");                 //原分行代碼
			records[++i] = checkIsNull(map, "NEW_BRANCH_RATE");                 //新分行成數
			records[++i] = checkIsNull(map, "ORIG_BRANCH_RATE");                //原分行成數
			records[++i] = checkIsNull(map, "BUS_BONUS");             		    //業務獎勵金(過新、調任成數、達goal率獎金折數)
			records[++i] = checkIsNull(map, "UN_FIN_DEDUCAMT");                 //非財務指標扣減金額
			records[++i] = checkIsNull(map, "LACK_IND_AMT");              		//獨立列示重大缺失扣減金額
			records[++i] = checkIsNull(map, "PREVIOUS_DEDUC_AMT");              //上期遞延扣減金額
			records[++i] = checkIsNull(map, "REAL_DEDUC_AMT");                  //實際扣減金額(本月+上期遞延)
			records[++i] = checkIsNull(map, "BUSI_BONUS_100_PERTG");            //業務獎勵金100%
			records[++i] = checkIsNull(map, "BUSI_BONUS_80_PERTG");             //業務獎勵金80%
			records[++i] = checkIsNull(map, "BUSI_BONUS_CNRADJ");               //業務獎勵金應調整數
			records[++i] = checkIsNull(map, "BUSI_BONUS_CNR_REAL");             //業務獎勵金實發
			records[++i] = checkIsNull(map, "BONUS_CNRADJ_NOTE");               //業務獎勵金調整說明
			listCSV.add(records);
		}
		//header
		csvHeader = new String[37];
		int j = 0;
		csvHeader[j] = "資料年月";
		csvHeader[++j] = "業務處代碼";
		csvHeader[++j] = "業務處名稱";
		csvHeader[++j] = "營運區代碼";
		csvHeader[++j] = "營運區名稱";
		csvHeader[++j] = "分行代碼/母行分行代碼";
		csvHeader[++j] = "分行別/母行別";
		csvHeader[++j] = "人員類別";
		csvHeader[++j] = "員工編號";
		csvHeader[++j] = "員工姓名";
		
		csvHeader[++j] = "財務指標得分";
		csvHeader[++j] = "財務指標績效獎金";
		csvHeader[++j] = "應有轄下專員總人數";
		csvHeader[++j] = "達標準生產力專員人數";
		csvHeader[++j] = "專員達標準生產力占比";
		csvHeader[++j] = "專員達GOAL率對應獎勵金成數";
		
		csvHeader[++j] = "新、調任年月";			
		csvHeader[++j] = "新、調任分行任職月份數";
		csvHeader[++j] = "原分行代碼";
		csvHeader[++j] = "新分行成數";
		
		csvHeader[++j] = "原分行成數";			
		csvHeader[++j] = "業務獎勵金(過新、調任成數、達goal率獎金折數)";
		csvHeader[++j] = "非財務指標扣減金額";
		csvHeader[++j] = "獨立列示重大缺失扣減金額";
		
		csvHeader[++j] = "上期遞延扣減金額";			
		csvHeader[++j] = "實際扣減金額(本月+上期遞延)";
		csvHeader[++j] = "業務獎勵金100%";
		csvHeader[++j] = "業務獎勵金80%";
		csvHeader[++j] = "業務獎勵金應調整數";			
		csvHeader[++j] = "業務獎勵金實發";
		csvHeader[++j] = "業務獎勵金調整說明";
	
		CSVUtil csv = new CSVUtil();
		csv.setHeader(csvHeader);
		csv.addRecordList(listCSV);
		String url = csv.generateCSV();
		notifyClientToDownloadFile(url, fileName); //download
	}
	private void download_TBPMS_BUY_PROD_RPT(List<Map<String,Object>> resultList) throws JBranchException{					//7.  下載理專計績商品報表
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH:mm:ss");
		String fileName = "理專計績商品報表_" + sdf.format(new Date()) + ".csv"; 
		List listCSV =  new ArrayList();
		for(Map<String, Object> map : resultList){
			String[] records = new String[17];
			int i = 0;
			records[i] = checkIsNull(map, "NUM");                        //項次
			records[++i] = checkIsNull(map, "YEARMON");  				 //成績年月
			records[++i] = checkIsNullAndTrans(map, "REGION_CENTER_ID");	     //營運區	name
			records[++i] = checkIsNull(map, "REGION_CENTER_NAME");	     //營運區	name
			records[++i] = checkIsNullAndTrans(map, "BRANCH_AREA_ID");	     //營運區	name
			records[++i] = checkIsNull(map, "BRANCH_AREA_NAME");	     //營運區	name
			records[++i] = checkIsNullAndTrans(map, "BRANCH_NBR");			     //分行別	
			records[++i] = checkIsNull(map, "BRANCH_NAME");			     //分行別	
			records[++i] = checkIsNullAndTrans(map, "AO_CODE");				     //AO_CODE
			records[++i] = checkIsNullAndTrans(map, "EMP_ID");  			     //員工編號
			records[++i] = checkIsNull(map, "EMP_NAME");			     //專員姓名
			records[++i] = checkIsNull(map, "PROD_TYPE");		         //商品類別					
			records[++i] = checkIsNullAndTrans(map, "PROD_ID");		         //商品名稱
			records[++i] = checkIsNull(map, "PROD_NAME");		         //商品名稱
			records[++i] = checkIsNull(map, "SELL_NUM");		         //實際收益
			records[++i] = checkIsNull(map, "ACT_PRFT");		         //實際收益
			records[++i] = checkIsNull(map, "CNR_PRFT");		         //CNR收益	
			listCSV.add(records);
		}
		//header
		String [] csvHeader = new String[17];
		int j = 0;
		csvHeader[j] = "項次";
		csvHeader[++j] = "成績年月";
		csvHeader[++j] = "業務處代碼";
		csvHeader[++j] = "業務處名稱";
		csvHeader[++j] = "營運區代碼";
		csvHeader[++j] = "營運區名稱";
		csvHeader[++j] = "分行別代碼";
		csvHeader[++j] = "分行別名稱";
		csvHeader[++j] = "AO_CODE";
		csvHeader[++j] = "員工編號";
		csvHeader[++j] = "專員姓名";
		csvHeader[++j] = "商品類別";
		csvHeader[++j] = "商品代碼";
		csvHeader[++j] = "商品名稱";
		csvHeader[++j] = "銷量";
		csvHeader[++j] = "實際收益";
		csvHeader[++j] = "CNR收益";
		CSVUtil csv = new CSVUtil();
		csv.setHeader(csvHeader);
		csv.addRecordList(listCSV);
		String url = csv.generateCSV();
		notifyClientToDownloadFile(url, fileName); //download
	}
	private void download_TBPMS_BARNCH_PROFIT_RPT(List<Map<String,Object>> resultList) throws JBranchException{					//8.  下載分行收益報表
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH:mm:ss");
		String fileName = "分行收益報表_" + sdf.format(new Date()) + ".csv"; 
		List listCSV =  new ArrayList();
		for(Map<String, Object> map : resultList){
			String[] records = new String[18];
			int i = 0;
			records[i] = checkIsNull(map, "YEARMON");                      //資料年月
			records[++i] = checkIsNullAndTrans(map, "REGION_CENTER_ID");         //區域中心
			records[++i] = checkIsNull(map, "REGION_CENTER_NAME");         //區域中心
			records[++i] = checkIsNullAndTrans(map, "BRANCH_AREA_ID");           //營運區
			records[++i] = checkIsNull(map, "BRANCH_AREA_NAME");           //營運區
			records[++i] = checkIsNullAndTrans(map, "BRANCH_NBR");                 //分別代碼
			records[++i] = checkIsNull(map, "BRANCH_NAME");                //分行別
			records[++i] = checkIsNull(map, "PRFT_AMT");                   //分行收益
			records[++i] = checkIsNull(map, "PRFT_GOAL");                  //分行收益目標
			records[++i] = checkIsNull(map, "PRFT_GOAL_ACH");              //收益達成率
			records[++i] = checkIsNull(map, "INV_DAY_RCEV");               //投資日收
			records[++i] = checkIsNull(map, "RCEV_FUND");                  //持收_基金小額
			records[++i] = checkIsNull(map, "NUM_MNGR_FEE");               //持收_管理費
			records[++i] = checkIsNull(map, "EXCHG_PL");                   //兌換損益
			records[++i] = checkIsNull(map, "INV_PL");                     //投資收益
			records[++i] = checkIsNull(map, "INS_NEW");                    //保險首期
			records[++i] = checkIsNull(map, "RCEV_INS_RENEW");             //持收_保險績期
			records[++i] = checkIsNull(map, "INS_PL");                     //保險收益
			listCSV.add(records);
		}
		//header
		String [] csvHeader = new String[18];
		int j = 0;
		csvHeader[j] = "資料年月";
		csvHeader[++j] = "業務處代碼";
		csvHeader[++j] = "業務處名稱";
		csvHeader[++j] = "營運區代碼";
		csvHeader[++j] = "營運區名稱";
		csvHeader[++j] = "分別代碼";
		csvHeader[++j] = "分行別";
		csvHeader[++j] = "分行收益";
		csvHeader[++j] = "分行收益目標";
		csvHeader[++j] = "收益達成率";
		csvHeader[++j] = "投資日收";
		csvHeader[++j] = "持收_基金小額";
		csvHeader[++j] = "持收_管理費";
		csvHeader[++j] = "兌換損益";
		csvHeader[++j] = "投資收益";
		csvHeader[++j] = "保險首期";
		csvHeader[++j] = "持收_保險績期";
		csvHeader[++j] = "保險收益";
		CSVUtil csv = new CSVUtil();
		csv.setHeader(csvHeader);
		csv.addRecordList(listCSV);
		String url = csv.generateCSV();
		notifyClientToDownloadFile(url, fileName); //download
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

	/**
	 * 達成率格式
	 * @param map
	 * @param key
	 * @return
	 */
	private String pcntFormat(Map map, String key){		
		if(StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null){
			BigDecimal getValue = new BigDecimal(String.valueOf(map.get(key))); 
			BigDecimal muValue = new BigDecimal(100);
			return getValue.multiply(muValue).setScale(2,RoundingMode.HALF_EVEN) + "%";
		}else
			return "";		
	}
	
	/**
	* 身份類別註記格式轉換
	* PERSON_TYPE_NOTE  
	* @param map
	* @return String
	 * @throws JBranchException 
	*/
	private String descFormat(Map map, String key) throws JBranchException {		
		if(descMap==null){
			XmlInfo xmlInfo = new XmlInfo();
			descMap = xmlInfo.doGetVariable("PMS.PERSON_TYPE_NOTE", FormatHelper.FORMAT_3);     //軌跡類型	
		}	
		if (StringUtils.isNotBlank(String.valueOf(map.get(key)))
				&& map.get(key) != null) {
			String type = ""; 
			if(map.get(key)!=null)
				type=descMap.get(map.get(key)+"");		
			return type;
		} else
			return "";
	}
}
