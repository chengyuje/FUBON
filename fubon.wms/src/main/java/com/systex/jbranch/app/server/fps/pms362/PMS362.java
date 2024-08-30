package com.systex.jbranch.app.server.fps.pms362;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * MENU
 * 
 * @author Brian
 * @date 2018/11/05
 * @spec null
 */
@Component("pms362")
@Scope("request")
public class PMS362 extends BizLogic{
	private DataAccessManager dam = null;	
	private Logger logger = LoggerFactory.getLogger(PMS362.class);
	
	public void queryData(Object body, IPrimitiveMap header) throws JBranchException {
		PMS362InputVO inputVO = (PMS362InputVO) body;
		PMS362OutputVO outputVO = new PMS362OutputVO();		
		dam = this.getDataAccessManager();		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		String tableName = "TBPMS_" + inputVO.getSrchType() + "_MTD_CHECK";
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT DATA_DATE, MODIFIER, MAX(LASTUPDATE) AS LASTUPDATE FROM " + tableName);
		sb.append(" WHERE 1 = 1 ");
		/*
		// 日期
		if (inputVO.getsTimes() != null) {
			sb.append(" AND DATA_DATE >= TO_CHAR(:STARTTIME, 'YYYYMMDD') ");
			queryCondition.setObject("STARTTIME", sdf.format(inputVO.getsTimes()));					
		}
		if (inputVO.geteTimes() != null) {
			sb.append(" AND DATA_DATE <= TO_CHAR(:ENDTIME, 'YYYYMMDD') ");
			queryCondition.setObject("ENDTIME", sdf.format(inputVO.geteTimes()));
		}
		*/
		sb.append(" GROUP BY DATA_DATE, MODIFIER ");
		
		queryCondition.setQueryString(sb.toString());
		
		List<Map<String, Object>> resultList = new ArrayList<Map<String,Object>>();
		resultList = dam.exeQuery(queryCondition);
		
		for(Map<String, Object> map : resultList){
			map.put("DATA_TYPE", inputVO.getSrchType());
		}
				
		outputVO.setResultList(resultList);

		sendRtnObject(outputVO);						
	}
	
	//上傳CSV檔案
	public void uploadFile(Object body, IPrimitiveMap header) throws JBranchException {
		PMS362InputVO inputVO = (PMS362InputVO) body;
		PMS362OutputVO outputVO = new PMS362OutputVO();		
		
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//		String datadate = sdf.format(inputVO.getTime());
		
		dam = this.getDataAccessManager();
		//顯示上傳成功的訊息
		String successMsg;
		String tableName = "TBPMS_" + inputVO.getSrchType() + "_MTD_CHECK";
		String[] columns = getColumns(inputVO.getSrchType());
		String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		String loginID = (String) getCommonVariable(SystemVariableConsts.LOGINID);
		List<String[]> dataCsv = CSVUtil.getBig5CSVFile(tempPath, inputVO.getFileName());
				
		if(!dataCsv.isEmpty()){		
			int j = 0;
			truncate(dam, tableName);
			
			for(int i = 1 ; i < dataCsv.size();i++){    //i=1 (不讀標頭)
				
				String[] str = dataCsv.get(i);
				try{
					if("TBPMS_AO_PROFIT_MTD_CHECK".equals(tableName)){
						str[1] = addZero(str[1]);						
					}
					save(dam, tableName, columns, str, loginID);
					j++;
					
				}catch(Exception e){
					throw new APException("案件編號" + i + "有問題，上傳失敗！");
				}
			}
			runJob(inputVO.getSrchType());
			
			successMsg = "資料匯入成功，筆數" + j;
			outputVO.setSuccessMsg(successMsg);	
			
		}
		sendRtnObject(outputVO);
	}
	
	//truncate TABLE
	public void truncate(DataAccessManager dam, String tableName) throws DAOException, JBranchException{
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append(" TRUNCATE TABLE " + tableName);	
		queryCondition.setQueryString(sb.toString());
		dam.exeUpdate(queryCondition);
		return;
	}
	
	
	//使用原生sql新增
	public void save(DataAccessManager dam, String tableName, String[] columns, String[] values, String loginID) throws DAOException, JBranchException{
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append(" INSERT INTO " + tableName);
		for(int i=0;i<columns.length;i++){
			if(i==0){
				sb.append(" ( " + columns[i]);			
			}else{
				sb.append(", " + columns[i]);
			}
		}
		sb.append(" ,CREATETIME, CREATOR, MODIFIER, LASTUPDATE ");
		sb.append(" ) VALUES ");
		for(int i=0;i<columns.length;i++){
			if(i==0){
				sb.append(" ( :" + columns[i]);			
			}else{
				sb.append(", :" + columns[i]);
			}
		}
		sb.append(" ,sysdate, :CREATOR, :MODIFIER, sysdate ");
		sb.append(" ) "); 
		
		for(int i=0; i < values.length; i++){
			values[i] = processValue(values[i]);
			
			if(i<2){
				queryCondition.setObject(columns[i], values[i]);
			}else{
				queryCondition.setObject(columns[i], StringUtils.isBlank(values[i]) != true ? new BigDecimal(values[i]).intValue() : 0);
			}
		}
		queryCondition.setObject("CREATOR", loginID);
		queryCondition.setObject("MODIFIER", loginID);
		queryCondition.setQueryString(sb.toString());
		dam.exeUpdate(queryCondition);		
	}
	
	//使用原生sql修改
	public void update(DataAccessManager dam, String tableName, String[] columns, String[] values, String loginID) throws DAOException, JBranchException{
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append(" UPDATE " + tableName);
		sb.append(" SET ");
		for(int i=0;i<columns.length;i++){
			if(i==columns.length-1){
				sb.append(columns[i] + " = :" + columns[i]);			
			}else{
				sb.append(columns[i] + " = :" + columns[i] + " , ");	
			}
		}
		sb.append(" , MODIFIER = :MODIFIER , LASTUPDATE = sysdate ");
		sb.append(" WHERE 1=1");
		sb.append(" AND " + columns[0] + " = :" + columns[0]);
		sb.append(" AND " + columns[1] + " = :" + columns[1]);
		for(int i=0;i<values.length;i++){
			values[i] = processValue(values[i]);
			
			if(i<2){
				queryCondition.setObject(columns[i], values[i]);
			}else{
				queryCondition.setObject(columns[i], StringUtils.isBlank(values[i]) != true ? new BigDecimal(values[i]).intValue() : 0);
			}
		}	
		queryCondition.setObject("MODIFIER", loginID);
		queryCondition.setQueryString(sb.toString());
		dam.exeUpdate(queryCondition);		
	}
	
	//使用原生sql確認是否有資料
	public List<Map<String,Object>> checkDataExist(DataAccessManager dam, String tableName, String[] columns, String[] values) throws DAOException, JBranchException{
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT*FROM " + tableName);
		sb.append(" WHERE 1=1 ");
		sb.append(" AND " + columns[0] + " = :" + columns[0]);
		sb.append(" AND " + columns[1] + " = :" + columns[1]);
		queryCondition.setObject(columns[0], values[0]);	
		queryCondition.setObject(columns[1], values[1]);	
		queryCondition.setQueryString(sb.toString());
		return dam.exeQuery(queryCondition);		
	}
	
	
	//取得table欄位名稱
	public String[] getColumns(String searchType){
		String[] columns;
		if("BR_PROFIT".equals(searchType)){
			 columns = new String[17];
			 columns[0] = "DATA_DATE";
			 columns[1] = "BRANCH_NBR";
			 columns[2] = "ITEM_01";
			 columns[3] = "ITEM_02";
			 columns[4] = "ITEM_03";
			 columns[5] = "ITEM_04";
			 columns[6] = "ITEM_05";
			 columns[7] = "ITEM_06";
			 columns[8] = "ITEM_17";
			 columns[9] = "ITEM_15";
			 columns[10] = "ITEM_14";
			 columns[11] = "ITEM_09";
			 columns[12] = "ITEM_10";
			 columns[13] = "ITEM_16";
			 columns[14] = "ITEM_11";
			 columns[15] = "ITEM_12";
			 columns[16] = "SUM_FEE";
		}else if("BR_SALES".equals(searchType)){
			 columns = new String[14];
			 columns[0] = "DATA_DATE";
			 columns[1] = "BRANCH_NBR";
			 columns[2] = "ITEM_01";
			 columns[3] = "ITEM_02";
			 columns[4] = "ITEM_03";
			 columns[5] = "ITEM_04";
			 columns[6] = "ITEM_05";
			 columns[7] = "ITEM_06";
			 columns[8] = "ITEM_17";
			 columns[9] = "ITEM_10";
			 columns[10] = "ITEM_16";
			 columns[11] = "ITEM_11";
			 columns[12] = "ITEM_12";
			 columns[13] = "SUM_BAL";
		}else{
			 columns = new String[6];
			 columns[0] = "DATA_DATE";
			 columns[1] = "EMP_ID";
			 columns[2] = "INV_FEE";
			 columns[3] = "EXCHG_FEE";
			 columns[4] = "INS_FEE";
			 columns[5] = "SUM_FEE";
		}
		
		return columns;
	
	}
	
	//將數值處理為需要的格式
	public String processValue(String str){	
		//如果有括號，去掉括號並加上負值
		if(str.contains("(")){
			str = str.replaceAll("\\(", "").replaceAll("\\)", "");
			str = "-" + str;
		}
		//如果有逗號，去掉逗號
		str = str.replaceAll(",", "");
		
		return str.trim();		
	}
	
	//員工編號小於6碼，於前端補0直到滿6碼
	public String addZero(String str){		
		int strLen = str.length();
		if(strLen !=6){
			for(int i=0;i<6-strLen;i++){
				str = "0"+str;
			}			
		}	
		return str;		
	}
	
     //目標維護更新
    public void runJob(String srchType)throws JBranchException {      
        dam = this.getDataAccessManager();
        QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        StringBuffer sql = new StringBuffer();

        try {
            sql.append(" CALL PABTH_BTPMS362.PR_" + srchType + "_CHECK() ");
            queryCondition.setQueryString(sql.toString());
            dam.exeUpdate(queryCondition);

        } catch (Exception e) {
            logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
            throw new APException("系統發生錯誤請洽系統管理員");
        }
    }
		
	//產生CSV範例檔
	public void getExample(Object body, IPrimitiveMap header) throws Exception {
		PMS362InputVO inputVO = (PMS362InputVO) body;
		CSVUtil csv = new CSVUtil();
		String[] headColumnArray;
		String csv_name;
		
		//表頭組成的字串陣列
		if("BR_PROFIT".equals(inputVO.getSrchType())){
			headColumnArray = new String[]{"資料日期","分行代碼", "基金", "SI", "SN", "奈米投", "海外債", "海外ETF",
					"海外股票", "信託", "黃金存摺", "匯兌", "保險_躉繳", "保險_投資型", "保險_短年期繳", "保險_長年期繳", "收益合計"};
			csv_name = "Step1_分行收益核實版.csv";
		}else if("BR_SALES".equals(inputVO.getSrchType())){
			headColumnArray = new String[]{"資料日期","分行代碼", "基金", "SI", "SN", "奈米投", "海外債", "海外ETF",
					"海外股票", "保險_躉繳", "保險_投資型", "保險_短年期繳", "保險_長年期繳", "銷量合計"};
			csv_name = "Step2_分行銷量核實版.csv";
		}else{
			headColumnArray = new String[]{"資料日期","員編", "投資收益", "匯兌收益", "保險收益", "投匯保收益合計"};
			csv_name = "Step3_專員業績戰報資料.csv";
		}

		// 設定表頭
		csv.setHeader(headColumnArray);
		String url = csv.generateCSV();
		notifyClientToDownloadFile(url, csv_name);
		sendRtnObject(null);
	}

}