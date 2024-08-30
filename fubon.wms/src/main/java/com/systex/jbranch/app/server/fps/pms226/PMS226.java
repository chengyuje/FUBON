package com.systex.jbranch.app.server.fps.pms226;
import java.io.File;
import java.sql.Types;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import jxl.Sheet;
import jxl.Workbook;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.pms226.PMS226InputVO;
import com.systex.jbranch.app.server.fps.pms226.PMS226OutputVO;
import com.systex.jbranch.app.server.fps.pms226.PMS226InputVO;
import com.systex.jbranch.app.server.fps.pms226.PMS226OutputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;
/**
 * 
 * Copy Right Information : Forms Syntron <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : 新戶轉介查詢Controller<br>
 * Comments Name : PMS226.java<br>
 * Author :zhouyiqiong<br>
 * Date :2016年11月12日 <br>
 * Version : 1.01 <br>
 * Editor : zhouyiqiong<br>
 * Editor Date : 2016年11月12日<br>
 */
@Component("pms226")
@Scope("request")
public class PMS226 extends FubonWmsBizLogic
{
	private DataAccessManager dam = null;
	private PMS226InputVO inputVO = null;
	private Logger logger = LoggerFactory.getLogger(PMS226.class);
	/**
	 * 查詢檔案
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException
	{	
		PMS226InputVO inputVO = (PMS226InputVO) body;
		PMS226OutputVO outputVO = new PMS226OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			sql.append("  SELECT RPT_YM,										");
			sql.append("  	     REF_YM,                                        ");
			sql.append("  	     REF_DATE,                                      ");
			sql.append("  	     CASE_SEQ,                                      ");
			sql.append("  	     REGION_CENTER_ID,                              ");
			sql.append("  	     REGION_CENTER_NAME,                            ");
			sql.append("  	     BRANCH_AREA_ID,                                ");
			sql.append("  	     BRANCH_AREA_NAME,                              ");
			sql.append("  	     BRANCH_NBR,                                    ");
			sql.append("  	     BRANCH_NAME,                                   ");
			sql.append("  	     REF_SR,                                        ");
			sql.append("  	     D.PARAM_NAME AS REF_SR_NAME,                   ");
			sql.append("  	     REF_ID,                                        ");
			sql.append("  	     REF_NAME,                                      ");
			sql.append("  	     REF_PROD,                                      ");
			sql.append("  	     S.PARAM_NAME AS REF_PROD_NAME,                 ");
			sql.append("  	     CUST_ID,                                       ");
			sql.append("  	     CUST_NAME,                                     ");
			sql.append("  	     USER_ID,                                       ");
			sql.append("  	     USER_NAME,                                     ");
			sql.append("  	     TP_ID,                                         ");
			sql.append("  	     TP_AO_CODE,                                    ");
			sql.append("  	     TP_YM,                                         ");
			sql.append("  	     PNOTE,                                         ");
			sql.append("  	     ACUM_FEE,                                      ");
			sql.append("  	     BONUS,                                         ");
			sql.append("  	     ADJ_TYPE,                                      ");
			sql.append("  	     GRO_GAR,                                       ");
			sql.append("  	     RM_CODE,                                       ");
			sql.append("  	     WHE_SENI,                                      ");
			sql.append("  	     T.VERSION,                                     ");
			sql.append("  	     T.CREATETIME,                                  ");
			sql.append("  	     T.CREATOR,                                     ");
			sql.append("  	     T.MODIFIER,                                    ");
			sql.append("  	     T.LASTUPDATE                                   ");
			sql.append("  FROM TBPMS_NEW_CUST_REC T                             ");
			sql.append("  LEFT JOIN TBSYSPARAMETER S                            ");
			sql.append("       ON S.PARAM_TYPE = 'CAM.REF_PROD'                 ");
			sql.append("       AND S.PARAM_CODE = T.REF_PROD                 	");
			sql.append("  LEFT JOIN TBSYSPARAMETER D                            ");
			sql.append("       ON D.PARAM_TYPE = 'CAM.REF_SALES_ROLE'           ");
			sql.append("       AND D.PARAM_CODE = T.REF_SR                 	    ");
			sql.append("  WHERE 1 = 1                                           ");
			sql.append("  AND BONUS > 0                                         ");
			sql.append("  AND PNOTE IN ('T+3','T+6')                            ");
						
			if (!StringUtils.isBlank(inputVO.getRegion())) {
				sql.append(" AND REGION_CENTER_ID = :regionCenter               ");
				condition.setObject("regionCenter", inputVO.getRegion());
			}
			if (!StringUtils.isBlank(inputVO.getOp())) {
				sql.append(" AND BRANCH_AREA_ID = :branchArea                   ");
				condition.setObject("branchArea", inputVO.getOp());
			}
			if (!StringUtils.isBlank(inputVO.getBranch())) {
				sql.append(" AND BRANCH_NBR = :branchNbr                        ");
				condition.setObject("branchNbr", inputVO.getBranch());
			}
			if (!StringUtils.isBlank(inputVO.getREF_ID())) {
				sql.append(" AND REF_ID = :refid                                ");
				condition.setObject("refid", inputVO.getREF_ID());
			}
			if (!StringUtils.isBlank(inputVO.getTP_ID())) {
				sql.append(" AND TP_ID = :tpid                                 ");
				condition.setObject("tpid", inputVO.getTP_ID());
			}
			if (!StringUtils.isBlank(inputVO.getsTime())) {
				sql.append(" AND TRIM(RPT_YM) = :sTime                         ");
				condition.setObject("sTime", inputVO.getsTime().trim());
			}

			sql.append("  ORDER BY T.REF_YM,T.CUST_ID,T.BRANCH_NBR,T.TP_ID,T.PNOTE  ");

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
		PMS226OutputVO return_VO2 = (PMS226OutputVO) body;
		List<Map<String, Object>> list = return_VO2.getList();
		if(list.size() > 0){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH:mm:ss");
			String fileName = "新戶轉介查詢_" + sdf.format(new Date()) + ".csv"; 
			List listCSV =  new ArrayList();
			for(Map<String, Object> map : list){
				String[] records = new String[28];
				int i = 0;
				
				records[i] = checkIsNull(map, "REF_YM");                 //轉介年月
				records[++i] = checkIsNull(map, "REF_DATE");             //轉介日期
				records[++i] = checkIsNullAndTrans(map, "CASE_SEQ");             //案件編號
				records[++i] = checkIsNullAndTrans(map, "REGION_CENTER_ID");   //區域中心
				records[++i] = checkIsNull(map, "REGION_CENTER_NAME");   //區域中心
				records[++i] = checkIsNullAndTrans(map, "BRANCH_AREA_ID");     //營運區
				records[++i] = checkIsNull(map, "BRANCH_AREA_NAME");     //營運區
				records[++i] = checkIsNullAndTrans(map, "BRANCH_NBR");          //分行代碼
				records[++i] = checkIsNull(map, "BRANCH_NAME");          //轉介單位名稱
				records[++i] = checkIsNull(map, "REF_SR_NAME");          //轉介人身份
				records[++i] = checkIsNullAndTrans(map, "REF_ID");               //轉介人員編
				records[++i] = checkIsNull(map, "REF_NAME");             //轉介人姓名
				records[++i] = checkIsNull(map, "REF_PROD_NAME");        //轉介商品
				records[++i] = checkIsNullAndTrans(map, "CUST_ID");              //客戶ID
				records[++i] = checkIsNull(map, "CUST_NAME");            //客戶姓名
				records[++i] = checkIsNullAndTrans(map, "USER_ID");              //受轉介人員編
				records[++i] = checkIsNull(map, "USER_NAME");            //受轉介人姓名
				records[++i] = checkIsNullAndTrans(map, "TP_ID");                //計績理專員編
				records[++i] = checkIsNullAndTrans(map, "TP_AO_CODE");           //計績AO_CODE
				records[++i] = checkIsNull(map, "TP_YM");                //計績年月
				records[++i] = checkIsNull(map, "PNOTE");                //時點註記
				records[++i] = checkIsNull(map, "ACUM_FEE");             //累計收益
				records[++i] = checkIsNull(map, "GRO_GAR");              //當月客戶之客群等級	
				records[++i] = checkIsNull(map, "RM_CODE");              //當月客戶是否有RM CODE	
				records[++i] = checkIsNull(map, "WHE_SENI");             //是否符合領獎資格
				records[++i] = checkIsNull(map, "BONUS");                //領獎金額
				records[++i] = checkIsNullType(map, "ADJ_TYPE");             //調整類型說明
				listCSV.add(records);
			}
			//header
			String [] csvHeader = new String[28];
			int j = 0;
			//業務處代碼,營運區代碼,分行代碼
			csvHeader[j] = "轉介年月";
			csvHeader[++j] = "轉介日期";
			csvHeader[++j] = "案件編號";
			csvHeader[++j] = "業務處代碼";
			csvHeader[++j] = "業務處";
			csvHeader[++j] = "營運區代碼";
			csvHeader[++j] = "營運區";
			csvHeader[++j] = "分行代碼";
			csvHeader[++j] = "分行";
			csvHeader[++j] = "轉介人身份";
			csvHeader[++j] = "轉介人員編";
			csvHeader[++j] = "轉介人姓名";
			csvHeader[++j] = "轉介商品";
			csvHeader[++j] = "客戶ID";
			csvHeader[++j] = "客戶姓名";
			csvHeader[++j] = "受轉介人員編";
			csvHeader[++j] = "受轉介人姓名";
			csvHeader[++j] = "計績理專員編";
			csvHeader[++j] = "計績AO_CODE";
			csvHeader[++j] = "計績年月";
			csvHeader[++j] = "時點註記";
			csvHeader[++j] = "累計收益";
			csvHeader[++j] = "當月客戶之客群等級";	
			csvHeader[++j] = "當月客戶是否有RM CODE";		
			csvHeader[++j] = "是否符合領獎資格";
			csvHeader[++j] = "領獎金額";
			csvHeader[++j] = "調整類型說明";
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
	* @param map
	* @return String
	*/
	private String checkIsNull(Map map, String key) 
	{
		if(StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null)
		{
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
		if(StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null)
		{
			NumberFormat nf = NumberFormat.getCurrencyInstance();
			return nf.format(map.get(key));										
		}else
			return "$0.00";		
	}
	
	//人工上傳調整
		@SuppressWarnings({ "rawtypes", "unused" })
		public void addData(Object body, IPrimitiveMap header) throws APException
		{	
			int flag = 0;
			try {
				PMS226InputVO inputVO = (PMS226InputVO) body;
				PMS226OutputVO outputVO = new PMS226OutputVO();
				List<String> list =  new ArrayList<String>();
				String joinedPath = new File((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), inputVO.getFileName()).toString();
				Workbook workbook = Workbook.getWorkbook(new File(joinedPath));
				Sheet sheet[] = workbook.getSheets();
				//有表頭.xls文檔
				//清空臨時表
				dam = this.getDataAccessManager();
				QueryConditionIF dcon = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);	
				String dsql = " TRUNCATE TABLE TBPMS_NEW_CUST_REC_U ";
				dcon.setQueryString(dsql.toString());
				dam.exeUpdate(dcon);
				String lab = null;
				for(int a=0;a<sheet.length;a++){
					for(int i=1;i<sheet[a].getRows();i++){
						for(int j=0;j<sheet[a].getColumns();j++){
							lab = sheet[a].getCell(j, i).getContents();
							list.add(lab);
						}
						
						//excel表格記行數
						flag++;
						
						//判斷當前上傳數據欄位個數是否一致
						if(list.size()!=6){
							throw new APException("上傳數據欄位個數不一致");
						}
						
						//SQL指令
						StringBuffer sb = new StringBuffer();
						dam = this.getDataAccessManager();
						QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
						sb.append("   INSERT INTO TBPMS_NEW_CUST_REC_U (RPT_YM,	         ");
						sb.append("  		REF_YM,            				             ");
						sb.append("  		CUST_ID,            				         ");
						sb.append("  		TP_YM,           					         ");
						sb.append("  		PNOTE,            					         ");
						sb.append("  		ACUM_FEE,            					     ");
						sb.append("  		BONUS,            					         ");
						sb.append("  		ADJ_TYPE,            					     ");
						sb.append("  		RNUM,            					         ");
						sb.append("  		VERSION,            						 ");
						sb.append("  		CREATETIME,             					 ");
						sb.append("  		CREATOR,             						 ");
						sb.append("  		MODIFIER,         						     ");
						sb.append("  		LASTUPDATE )             					 ");
						sb.append("  	VALUES(:RPT_YM,            				         ");
						sb.append("  		:REF_YM,             				         ");
						sb.append("  		:CUST_ID,             				         ");
						sb.append("  		:TP_YM,             					     ");
						sb.append("  		:PNOTE,             					     ");
						sb.append("  		:ACUM_FEE,             					     ");
						sb.append("  		:BONUS,             					     ");
						sb.append("  		1,             					             ");
						sb.append("  		:RNUM,             					         ");
						sb.append("  		:VERSION,           					     ");
						sb.append("  		SYSDATE,           				             ");
						sb.append("  		:CREATOR,            					     ");
						sb.append("  		:MODIFIER,         					         ");
						sb.append("  		SYSDATE)          				             ");
						qc.setObject("RPT_YM",inputVO.getsTime().trim()                   );
						qc.setObject("REF_YM",list.get(0).trim()                          );
						qc.setObject("CUST_ID",list.get(1).trim()                         );
						qc.setObject("TP_YM",list.get(2).trim()                           );
						qc.setObject("PNOTE",list.get(3).trim()                           );
						qc.setObject("ACUM_FEE",list.get(4).trim()                        );
						qc.setObject("BONUS",list.get(5).trim()                           );
						qc.setObject("RNUM",flag                                          );
						qc.setObject("VERSION","1"                                        );
						qc.setObject("CREATOR", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
						qc.setObject("MODIFIER", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
						qc.setQueryString(sb.toString());
						dam.exeUpdate(qc);
						list.clear();
					}
				}
				//文檔上傳成功
				outputVO.setFlag(flag);
				this.sendRtnObject(outputVO);
			} catch (Exception e) {
				logger.error("文檔上傳失敗");
				throw new APException("資料上傳失敗,錯誤發生在第"+flag+"筆,"+e.getMessage());
			}
		}
		
		/**
		 * 調用存儲過程
		 * @param body
		 * @param header
		 * @throws APException
		 */
		@SuppressWarnings({ "rawtypes" })
		public void callStored(Object body, IPrimitiveMap header) throws APException
		{
			try
			{
				PMS226InputVO inputVO = (PMS226InputVO) body;
				PMS226OutputVO outputVO = new PMS226OutputVO();
				//執行存儲過程
				dam = this.getDataAccessManager();
				QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				StringBuffer sb = new StringBuffer();
				sb.append(" CALL PABTH_BTPMS725.SP_TBPMS_NEW_CUST_REC(? ,? ) ");
				qc.setString(1, inputVO.getsTime());
				qc.registerOutParameter(2, Types.VARCHAR);
				qc.setQueryString(sb.toString());
				Map<Integer, Object> resultMap = dam.executeCallable(qc);
				String str = (String) resultMap.get(2);
				String[] strs = null;
				if(str!=null){
					strs = str.split("；");
					if(strs!=null&&strs.length>5){
						str = strs[0]+"；"+strs[1]+"；"+strs[2]+"；"+strs[3]+"；"+strs[4]+"...等";
					}
				}
				outputVO.setErrorMessage(str);
				this.sendRtnObject(outputVO);
			}
			catch (Exception e)
			{
				logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
				throw new APException(e.getMessage());
			}
		
		}
		public void detail(Object body, IPrimitiveMap header) throws JBranchException
		{	
			PMS226InputVO inputVO = (PMS226InputVO) body;
			PMS226OutputVO outputVO = new PMS226OutputVO();
			dam = this.getDataAccessManager();
			QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			try
			{
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				sql.append("  SELECT RPT_YM,                                                        ");
				sql.append("         SNAP_YYYYMM,													");
				sql.append("  	     BRH_COD,                                                       ");
				sql.append("  	     CUST_ID,                                                       ");
				sql.append("  	     PROD_SOURCE,                                                   ");
				sql.append("  	     TYPE1,                                                         ");
				sql.append("  	     TYPE2,                                                         ");
				sql.append("  	     TYPE3,                                                         ");
				sql.append("  	     TYPE4,                                                         ");
				sql.append("  	     AO_CODE,                                                       ");
				sql.append("  	     TXN_AMT,                                                       ");
				sql.append("  	     CNR_PROFIT_PLUS,                                               ");
				sql.append("  	     PROD_ID,                                                       ");
				sql.append("  	     PROD_NAME,                                                     ");
				sql.append("  	     TXN_DATE,                                                      ");
				sql.append("  	     VERSION,                                                       ");
				sql.append("  	     CREATETIME,                                                    ");
				sql.append("  	     CREATOR,                                                       ");
				sql.append("  	     MODIFIER,                                                      ");
				sql.append("  	     LASTUPDATE                                                     ");
				sql.append("  FROM TBPMS_NEW_CUST_REC_DETAIL                                        ");
				sql.append("  WHERE 1 = 1                                                           ");
				if (!StringUtils.isBlank(inputVO.getsTime())) 
				{
					sql.append(" AND RPT_YM = :sTime                           ");
					condition.setObject("sTime", inputVO.getsTime().trim());
				}
				if (!StringUtils.isBlank(inputVO.getCustId())) 
				{
					sql.append(" AND CUST_ID = :custId                         ");
					condition.setObject("custId", inputVO.getCustId());
				}
				sql.append("  ORDER BY SNAP_YYYYMM  ");

				condition.setQueryString(sql.toString());
				ResultIF list = dam.executePaging(condition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
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
		* 檢查Map取出欄位是否為Null
		* @param map
		* @return String
		*/
		private String checkIsNullType(Map map, String key) 
		{
			if(StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null){
				String type=String.valueOf(map.get(key));
				if(String.valueOf(map.get(key)).equals("0"))
					type="系統資料";
				else if(String.valueOf(map.get(key)).equals("1"))
					type="上傳資料";				
				return type;
					
			}else{
				return "";
			}
		}
	
		
		/**
		 * 下載
		 * @param body
		 * @param header
		 * @throws Exception
		 */
		public void downLoad2(Object body, IPrimitiveMap header) throws Exception {
			notifyClientToDownloadFile("doc//PMS//PMS226_EXAMPLE.xls", "PMS226_新戶轉介查詢_上傳範例.xls"); //download
			this.sendRtnObject(null);
		}
		
}
