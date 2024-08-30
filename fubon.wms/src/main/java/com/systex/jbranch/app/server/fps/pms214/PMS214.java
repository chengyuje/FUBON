package com.systex.jbranch.app.server.fps.pms214;

import java.io.File;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import jxl.Sheet;
import jxl.Workbook;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.pms213.PMS213QueryInputVO;
import com.systex.jbranch.app.server.fps.pms213.PMS213QueryOutputVO;
import com.systex.jbranch.app.server.fps.pms214.PMS214InputVO;
import com.systex.jbranch.app.server.fps.pms214.PMS214OutputVO;
import com.systex.jbranch.app.server.fps.pms214.PMS214UpdateInputVO;
import com.systex.jbranch.app.server.fps.pms341.PMS341OutputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * Copy Right Information : Forms Syntron <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description :調整客戶收益(整批)Controller<br>
 * Comments Name : PMS214.java<br>
 * Author : cty<br>
 * Date :2016年11月23日 <br>
 * Version : 1.0 <br>
 * Editor : cty<br>
 * Editor Date : 2016年11月23日<br>
 */
@Component("pms214")
@Scope("request")
public class PMS214 extends FubonWmsBizLogic
{
	public DataAccessManager dam = null;

	private Logger logger = LoggerFactory.getLogger(PMS214.class);

	private static Map<String, String> typeMap ;
	private static Map<String, String> descMap ;
	
	
	public void queryData (Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		
		PMS214QueryInputVO inputVO = (PMS214QueryInputVO) body;
		PMS214OutputVO outputVO = new PMS214OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		int total=0;
		this.queryData(condition, inputVO);
		
		ResultIF list = dam.executePaging(condition,inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
	
	
		if (list.size() > 0) {
			outputVO.setTotalPage(list.getTotalPage());
			outputVO.setOutputLargeAgrList(list);
			outputVO.setTotalRecord(list.getTotalRecord());
			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());
			outputVO.setErrorMessage("");
			total=list.getTotalRecord();
			if(outputVO.getTotalRecord()>200000)
			{
//				logger.error(String.format("查詢筆數過多, 請增加查詢條件"));
//			 	throw new APException(e.getMessage());
				outputVO.setOutputLargeAgrList( new ArrayList());
				outputVO.setErrorMessage("查詢筆數過多, 請增加查詢條件");
				sendRtnObject(outputVO);
			}
			else
			{
				sendRtnObject(outputVO);
			}
		}
		else 
		{
				throw new APException("ehl_01_common_009");
		}
		
	}

	public void queryData (QueryConditionIF qc, PMS214QueryInputVO inputVO) throws JBranchException, ParseException {
		
			StringBuffer sb = new StringBuffer();
			
			sb.append("   SELECT BOU.seq_no,                  ");
			sb.append("          BOU.yearmon,                 ");
			sb.append("          BOU.custid,                  ");
			sb.append("          BOU.aocode,                  ");
			sb.append("          BOU.last_aocode,             ");
			sb.append("          BOU.LAST_ASSIS_ID,           ");
			sb.append("          BOU.assis_id,                ");
			sb.append("          BOU.save_aum_bal,            ");
			sb.append("          BOU.last_save_aum_bal,       ");
			sb.append("          BOU.save_increment,          ");
			sb.append("          BOU.set_aum_bal,             ");
			sb.append("          BOU.last_set_aum_bal,        ");
			sb.append("          BOU.set_increment,           ");
			sb.append("          BOU.cover_aum_bal,           ");
			sb.append("          BOU.last_cover_aum_bal,      ");
			sb.append("          BOU.cover_increment,         ");
			sb.append("          BOU.last_eip_level,          ");
			sb.append("          BOU.eip_level,               ");
			sb.append("          BOU.adj,                     ");
			sb.append("          BOU.adj_type,                ");
			sb.append("          BOU.adj_desc,                ");
			sb.append("          TE.EMP_NAME AS TEMP_NAME,     ");
			sb.append("          LE.EMP_NAME AS LEMP_NAME,     ");
			sb.append("          LO.BRANCH_NAME AS LBRANCH_NAME, ");
			sb.append("          TOO.BRANCH_NAME AS TBRANCH_NAME, ");
			sb.append("          BOU.DEPT_ID,                 ");
			sb.append("          BOU.LAST_DEPT_ID             ");
			sb.append("   FROM   tbpms_prod_aum_adj BOU     ");
			sb.append("   LEFT JOIN TBPMS_EMPLOYEE_REC_N TE                                                      ");
			sb.append("     ON TE.EMP_ID=BOU.ASSIS_ID                                                            ");
			sb.append("     AND LAST_DAY(TO_DATE(:YEARMON ,'YYYYMM')) BETWEEN TE.START_TIME AND TE.END_TIME      ");
			sb.append("   LEFT JOIN TBPMS_EMPLOYEE_REC_N LE                                                      ");
			sb.append("     ON LE.EMP_ID=BOU.LAST_ASSIS_ID                                                       ");
			sb.append("     AND LAST_DAY(TO_DATE(:YEARMON ,'YYYYMM')) BETWEEN LE.START_TIME AND LE.END_TIME      ");
			sb.append("   LEFT JOIN TBPMS_ORG_REC_N TOO                                                          ");
			sb.append("     ON TOO.DEPT_ID=BOU.DEPT_ID                                                           ");
			sb.append("     AND LAST_DAY(TO_DATE(:YEARMON ,'YYYYMM')) BETWEEN TOO.START_TIME AND TOO.END_TIME    ");
			sb.append("   LEFT JOIN TBPMS_ORG_REC_N LO                                                           ");
			sb.append("     ON LO.DEPT_ID=BOU.LAST_DEPT_ID                                                       ");
			sb.append("     AND LAST_DAY(TO_DATE(:YEARMON ,'YYYYMM')) BETWEEN LO.START_TIME AND LO.END_TIME      ");
			sb.append("   WHERE  yearmon = :YEARMON                                                              ");

			if(null!=inputVO.getCustId() && !("".equals(inputVO.getCustId())) ){
				sb.append("		AND  CUSTID = :CUSTID               ");
				qc.setObject("CUSTID", inputVO.getCustId());
			}
			if(null!=inputVO.getAOCODE() && !("".equals(inputVO.getAOCODE())) ){
				sb.append("    AND AOCODE = :AOCODE");
				qc.setObject("AOCODE", inputVO.getAOCODE());
			}
			if(null!=inputVO.getAdjType() && !("".equals(inputVO.getAdjType())) ){
				sb.append("    AND ADJ_TYPE = :ADJ_TYPE");
				qc.setObject("ADJ_TYPE", inputVO.getAdjType());
			}
			if(null!=inputVO.getAssisId() && !("".equals(inputVO.getAssisId())) ){
				sb.append("    AND ASSIS_ID = :ASSIS_ID");
				qc.setObject("ASSIS_ID", inputVO.getAssisId());
			}
			if(null!=inputVO.getAdjDesc() && !("".equals(inputVO.getAdjDesc())) ){
				sb.append("    AND ADJ_DESC = :ADJ_DESC");
				qc.setObject("ADJ_DESC", inputVO.getAdjDesc());
			}
			//branch_nbr
			if(null!=inputVO.getBranch_nbr() && !("".equals(inputVO.getBranch_nbr())) ){
				sb.append("    AND BOU.DEPT_ID = :DEPT_ID");
				qc.setObject("DEPT_ID", inputVO.getBranch_nbr());
			}
			sb.append("		ORDER BY YEARMON, CUSTID , ADJ_TYPE             		");
			qc.setObject("YEARMON", inputVO.getYearMon());
			qc.setQueryString(sb.toString());
	}
//	/**
//	 * 查詢
//	 * @param body
//	 * @param header
//	 * @throws APException
//	 */
//	@SuppressWarnings({ "unchecked", "rawtypes" })
//	public void queryData(Object body, IPrimitiveMap header) throws APException
//	{
//		try
//		{
//			PMS214QueryInputVO inputVO = (PMS214QueryInputVO) body;
//			PMS214OutputVO outputVO = new PMS214OutputVO();
//			dam = this.getDataAccessManager();
//			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
//	
//			StringBuffer sb = new StringBuffer();
//			
//			sb.append(" 	SELECT                                                               ");
//			sb.append(" 		SEQ_NO,YEARMON,CUSTID,AOCODE,LAST_AOCODE,                        ");
//			sb.append(" 		ASSIS_ID,SAVE_AUM_BAL,LAST_SAVE_AUM_BAL,SAVE_INCREMENT,          ");
//			sb.append(" 		SET_AUM_BAL,LAST_SET_AUM_BAL,SET_INCREMENT,COVER_AUM_BAL,        ");
//			sb.append(" 		LAST_COVER_AUM_BAL,COVER_INCREMENT,LAST_EIP_LEVEL,EIP_LEVEL,     ");
//			sb.append(" 		ADJ,ADJ_TYPE,ADJ_DESC                                            ");
//			sb.append(" 	FROM TBPMS_PROD_AUM_ADJ                                              ");
//			sb.append(" 	WHERE YEARMON = :YEARMON                                             ");
//			if(null!=inputVO.getCustId() && !("".equals(inputVO.getCustId())) ){
//				sb.append("		AND  CUSTID = :CUSTID               ");
//				qc.setObject("CUSTID", inputVO.getCustId());
//			}
//			if(null!=inputVO.getAOCODE() && !("".equals(inputVO.getAOCODE())) ){
//				sb.append("    AND AOCODE = :AOCODE");
//				qc.setObject("AOCODE", inputVO.getAOCODE());
//			}
//			if(null!=inputVO.getAdjType() && !("".equals(inputVO.getAdjType())) ){
//				sb.append("    AND ADJ_TYPE = :ADJ_TYPE");
//				qc.setObject("ADJ_TYPE", inputVO.getAdjType());
//			}
//			if(null!=inputVO.getAssisId() && !("".equals(inputVO.getAssisId())) ){
//				sb.append("    AND ASSIS_ID = :ASSIS_ID");
//				qc.setObject("ASSIS_ID", inputVO.getAssisId());
//			}
//			if(null!=inputVO.getAdjDesc() && !("".equals(inputVO.getAdjDesc())) ){
//				sb.append("    AND ADJ_DESC = :ADJ_DESC");
//				qc.setObject("ADJ_DESC", inputVO.getAdjDesc());
//			}
//			sb.append("		ORDER BY YEARMON, CUSTID , ADJ_TYPE             		");
//			qc.setObject("YEARMON", inputVO.getYearMon());
//			qc.setQueryString(sb.toString());
//			ResultIF largeAgrList = dam.executePaging(qc, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
//			List<Map<String, Object>> resultList = dam.exeQuery(qc);  //查詢CSV用途
//			outputVO.setOutputLargeAgrList(largeAgrList); // data
//			outputVO.setCsvList(resultList);
//			int totalPage_i = largeAgrList.getTotalPage(); // 分頁用
//			int totalRecord_i = largeAgrList.getTotalRecord(); // 分頁用
//			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
//			outputVO.setTotalPage(totalPage_i);// 總頁次
//			outputVO.setTotalRecord(totalRecord_i);// 總筆數
//			this.sendRtnObject(outputVO);
//		}
//		catch (Exception e)
//		{
//			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
//			throw new APException(e.getMessage());
//		}
//	
//	}
	
	/**
	 * 從excel表格中新增數據
	 * 上傳
	 * @param body
	 * @param header
	 * @throws APException
	 */
	@SuppressWarnings({ "rawtypes", "unused" })
	public void importData(Object body, IPrimitiveMap header) throws APException
	{
		int flag = 0;
		try {
			PMS214InputVO inputVO = (PMS214InputVO) body;
			PMS214OutputVO outputVO = new PMS214OutputVO();
			List<String> import_file = new ArrayList<String>();
			List<String> list =  new ArrayList<String>();
			String joinedPath = new File((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), inputVO.getFileName()).toString();
			Workbook workbook = Workbook.getWorkbook(new File(joinedPath));
			Sheet sheet[] = workbook.getSheets();
			String lab = null;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			//有表頭.xls文檔
			//清空臨時表
			dam = this.getDataAccessManager();
			QueryConditionIF dcon = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);	
			String dsql = " TRUNCATE TABLE TBPMS_PROD_AUM_ADJ_U ";
			dcon.setQueryString(dsql.toString());
			dam.exeUpdate(dcon);
			//清空異常數據表
			String delT = " DELETE FROM TBPMS_PROD_AUM_ADJ_T WHERE YEARMON = :YEARMON";
			QueryConditionIF dcon1 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			dcon1.setObject("YEARMON", inputVO.getYearMon());
			dcon1.setQueryString(delT);
			dam.exeUpdate(dcon1);
			for(int a=0;a<sheet.length;a++){
				for(int i=1;i<sheet[a].getRows();i++){
					for(int j=0;j<sheet[a].getColumns();j++){
						lab = sheet[a].getCell(j, i).getContents();
						list.add(lab);
					}
					
						//excel表格記行數
						flag++;
						
						//判斷當前上傳數據月份是否一致
						if(!list.get(0).equals(inputVO.getYearMon())){
							throw new APException("上傳數據選擇月份不一致");
						}
						
						//判斷當前上傳數據欄位個數是否一致
						if(list.size()!=8){
							throw new APException("上傳數據欄位個數不一致");
						}
						/*String custId = (String) list.get(2);
						for(int i1=custId.length(); i1<20; i1++)
							custId  = '0' + custId;
						String timeNow = sdf.format(new Date());
						String end = "30"+i;
						String SEQ_NO =  custId +  "000000"+ timeNow + end;*/
						StringBuffer sb = new StringBuffer();
						dam = this.getDataAccessManager();
						QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
						sb.append("   INSERT INTO TBPMS_PROD_AUM_ADJ_U (SEQ_NO,	         ");
						sb.append("  		YEARMON,            				         ");
						sb.append("  		CUSTID,           					         ");
						sb.append("  		AOCODE,            					         ");
						sb.append("  		ASSIS_ID,            					     ");
						sb.append("  		SAVE_INCREMENT,            				     ");
						sb.append("  		SET_INCREMENT,            					 ");
						sb.append("  		COVER_INCREMENT,            			     ");
						sb.append("  		ADJ_DESC,            					     ");
						sb.append("  		RNUM,            					         ");
						sb.append("  		VERSION,            						 ");
						sb.append("  		CREATETIME,             					 ");
						sb.append("  		CREATOR,             						 ");
						sb.append("  		MODIFIER,         						     ");
						sb.append("  		LASTUPDATE )             					 ");
						sb.append("  	VALUES(:SEQ_NO,            				         ");
						sb.append("  		:YEARMON,             				         ");
						sb.append("  		:CUSTID,             				         ");
						sb.append("  		:AOCODE,             				         ");
						sb.append("  		:ASSIS_ID,             					     ");
						sb.append("  		:SAVE_INCREMENT,             				 ");
						sb.append("  		:SET_INCREMENT,             				 ");
						sb.append("  		:COVER_INCREMENT,             				 ");
						sb.append("  		:ADJ_DESC,             					     ");
						sb.append("  		:RNUM,             					         ");
						sb.append("  		:VERSION,           					     ");
						sb.append("  		SYSDATE,           				             ");
						sb.append("  		:CREATOR,            					     ");
						sb.append("  		:MODIFIER,         					         ");
						sb.append("  		SYSDATE)          				             ");
						qc.setObject("SEQ_NO",""                             			  );
						qc.setObject("YEARMON",list.get(0).trim()                         );
						qc.setObject("CUSTID",list.get(1).trim()                          );
						qc.setObject("AOCODE",list.get(2).trim()                          );
						qc.setObject("ASSIS_ID",list.get(3).trim()                        );
						qc.setObject("SAVE_INCREMENT",list.get(4).trim()                  );
						qc.setObject("SET_INCREMENT",list.get(5).trim()                  );
						qc.setObject("COVER_INCREMENT",list.get(6).trim()                );
						qc.setObject("ADJ_DESC",list.get(7).trim()                       );
						qc.setObject("RNUM",flag                                          );
						qc.setObject("VERSION","0"                                        );
						qc.setObject("CREATOR", inputVO.getUserId()                       );
						qc.setObject("MODIFIER", inputVO.getUserId()                      );
						qc.setQueryString(sb.toString());
						dam.exeUpdate(qc);
						list.clear();
					}
				
		}
			//資料上傳成功
			outputVO.setFlag(flag);
			this.sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error("發生錯誤:%s",StringUtil.getStackTraceAsString(e));
			throw new APException("資料上傳失敗,錯誤發生在第"+flag+"筆,"+e.getMessage());
		}
		
	}
	
	/**
	 * 調用存儲過程
	 * @param body
	 * @param header
	 * @throws APException
	 */
	@SuppressWarnings({ "unused", "rawtypes" })
	public void callStored(Object body, IPrimitiveMap header) throws APException
	{
		try
		{
			PMS214InputVO inputVO = (PMS214InputVO) body;
			PMS214OutputVO outputVO = new PMS214OutputVO();
			//執行存儲過程
			dam = this.getDataAccessManager();
			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
			sb.append(" CALL PABTH_BTPMS717.SP_TBPMS_PROD_AUM_ADJ(? ,? ) ");
			qc.setString(1, inputVO.getYearMon());
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
	
	/**
	 * 匯出EXCLE
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void export(Object body, IPrimitiveMap header) throws JBranchException 
	{
		PMS214InputVO inputVO = (PMS214InputVO) body;
		PMS214OutputVO outputVO = new PMS214OutputVO();
		//查詢匯出數據
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append("	 SELECT	YEARMON,                  ");
		sb.append("			CUSTID,                   ");
		sb.append("			AOCODE,                   ");
		sb.append("			ASSIS_ID,                 ");
		sb.append("			SAVE_INCREMENT,           ");
		sb.append("			SET_INCREMENT,            ");
		sb.append("			COVER_INCREMENT,          ");
		sb.append("			ADJ_DESC                  ");
		sb.append("	 FROM TBPMS_PROD_AUM_ADJ_T		  ");	
		sb.append("	 WHERE YEARMON = :YEARMON         ");
		qc.setObject("YEARMON",(String)inputVO.getYearMon() );
		qc.setQueryString(sb.toString());
		List<Map<String, Object>> list = dam.exeQuery(qc);
		//List<Map<String, Object>> list = return_VO2.getCsvList();
		if(list.size() > 0)
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH:mm:ss");
			String fileName = "調整理專AUM&&客戶數資料異常數據表_" + sdf.format(new Date()) + ".csv"; 
			List listCSV =  new ArrayList();
			for(Map<String, Object> map : list){
				String[] records = new String[8];
				int i = 0;
				records[i] = checkIsNull(map, "YEARMON");                     //計績年月
				records[++i] = checkIsNull(map, "CUSTID");                 	  //客戶ID
				records[++i] = checkIsNull(map, "AOCODE");                    //本月-AOCODE
				records[++i] = checkIsNull(map, "ASSIS_ID");         		  //本月-專員員編	
				records[++i] = checkIsNull(map, "SAVE_INCREMENT");            //本月存款增量
				records[++i] = checkIsNull(map, "SET_INCREMENT");             //本月台定增量
				records[++i] = checkIsNull(map, "COVER_INCREMENT");           //本月投保增量
				records[++i] = checkIsNull(map, "ADJ_DESC");                  //人工調整說明
				listCSV.add(records);
			}
			//header
			String [] csvHeader = new String[8];
			int j = 0;
			csvHeader[j] = "計績年月";
			csvHeader[++j] = "客戶ID";
			csvHeader[++j] = "本月-AOCODE";
			csvHeader[++j] = "本月-專員員編";
			csvHeader[++j] = "本月存款增量";
			csvHeader[++j] = "本月台定增量";
			csvHeader[++j] = "本月投保增量";
			csvHeader[++j] = "人工調整說明";
			CSVUtil csv = new CSVUtil();
			csv.setHeader(csvHeader);
			csv.addRecordList(listCSV);
			String url = csv.generateCSV();
			notifyClientToDownloadFile(url, fileName);
			this.sendRtnObject(null);
		} else 
		{
			outputVO.setOutputLargeAgrList(list);
			this.sendRtnObject(outputVO);
	    }
	}
	
	
	/**
	 * 匯出主檔EXCLE
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	
	/* === 產出Excel==== */
	public void exportMain (Object body, IPrimitiveMap header) throws JBranchException, ParseException {

		PMS214QueryInputVO inputVO = (PMS214QueryInputVO) body;
		PMS214OutputVO outputVO = new PMS214OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		this.queryData(condition, inputVO);

		List<Map<String, Object>> list = dam.exeQuery(condition);
		if(list.size()>200000)
		{	outputVO.setOutputLargeAgrList( new ArrayList());
			outputVO.setErrorMessage("匯出筆數過多, 請增加查詢條件");
			sendRtnObject(outputVO);
//		
		}
		else
		{	

			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String fileName = "調整理專AUM&客戶數_" + sdf.format(new Date()) + "-" + getUserVariable(FubonSystemVariableConsts.LOGINID) + ".csv";
			List listCSV = new ArrayList();
			int a = 1;
			for (Map<String, Object> map : list) {
				String[] records = new String[27];
				int i = 0;
				records[i]=checkIsNullAndTrans(map,"SEQ_NO");                     // 序列號
				records[++i]=checkIsNull(map,"YEARMON");                    // 計積年月
				records[++i]=checkIsNullAndTrans(map,"CUSTID");                     // 客戶ID
				records[++i]=checkIsNullAndTrans(map,"AOCODE");                     // 本月-AOCODE
				records[++i]=checkIsNullAndTrans(map,"LAST_AOCODE");                // 上月-AOCODE
				records[++i]=checkIsNullAndTrans(map,"ASSIS_ID");                   // 本月-專員員編
				records[++i]=checkIsNull(map,"TEMP_NAME");                   // 本月-專員姓名
				records[++i]=checkIsNullAndTrans(map,"LAST_ASSIS_ID");              // 上月-專員員編
				records[++i]=checkIsNull(map,"LEMP_NAME");                   // 上月-專員姓名
				records[++i]=checkIsNullAndTrans(map,"DEPT_ID");                    // 本月-部門編號
				records[++i]=checkIsNull(map,"TBRANCH_NAME");               // 本月-分行名稱
				records[++i]=checkIsNullAndTrans(map,"LAST_DEPT_ID");               // 上月-部門編號
				records[++i]=checkIsNull(map,"LBRANCH_NAME");               // 上月-分行名稱
				records[++i]=checkIsNull(map,"SAVE_AUM_BAL");               // 本月-存款AUM餘額
				records[++i]=checkIsNull(map,"LAST_SAVE_AUM_BAL");          // 上月-存款AUM餘額
				records[++i]=checkIsNull(map,"SAVE_INCREMENT");             // 本月存款增量
				records[++i]=checkIsNull(map,"SET_AUM_BAL");                // 本月-台定AUM餘額
				records[++i]=checkIsNull(map,"LAST_SET_AUM_BAL");           // 上月-台定AUM餘額
				records[++i]=checkIsNull(map,"SET_INCREMENT");              // 本月台定增量
				records[++i]=checkIsNull(map,"COVER_AUM_BAL");              // 本月-投保AUM餘額
				records[++i]=checkIsNull(map,"LAST_COVER_AUM_BAL");         // 上月-投保AUM餘額
				records[++i]=checkIsNull(map,"COVER_INCREMENT");            // 本月投保增量
				records[++i]=checkIsNull(map,"LAST_EIP_LEVEL");             // 上月EIP等級
				records[++i]=checkIsNull(map,"EIP_LEVEL");                  // 本月EIP等級
				records[++i]=checkIsNull(map,"ADJ");                        // 調整
				records[++i]=typeFormat(map,"ADJ_TYPE");                   // 軌跡類型
				records[++i]=descFormat(map,"ADJ_DESC");                   // 人工調整說明/系統調整類別

			
				listCSV.add(records);
			}
			
			// header
			String[] csvHeader = new String[27];
			int j = 0;
			csvHeader[j]="序列號";
			csvHeader[++j]="計積年月";
			csvHeader[++j]="客戶ID";
			csvHeader[++j]="本月-AOCODE";
			csvHeader[++j]="上月-AOCODE";
			csvHeader[++j]="本月-專員員編";
			csvHeader[++j]="本月-專員姓名";
			csvHeader[++j]="上月-專員員編";
			csvHeader[++j]="上月-專員姓名";
			csvHeader[++j]="本月-部門編號";
			csvHeader[++j]="本月-分行名稱";
			csvHeader[++j]="上月-部門編號";
			csvHeader[++j]="上月-分行名稱";
			csvHeader[++j]="本月-存款AUM餘額";
			csvHeader[++j]="上月-存款AUM餘額";
			csvHeader[++j]="本月存款增量";
			csvHeader[++j]="本月-台定AUM餘額";
			csvHeader[++j]="上月-台定AUM餘額";
			csvHeader[++j]="本月台定增量";
			csvHeader[++j]="本月-投保AUM餘額";
			csvHeader[++j]="上月-投保AUM餘額";
			csvHeader[++j]="本月投保增量";
			csvHeader[++j]="上月EIP等級";
			csvHeader[++j]="本月EIP等級";
			csvHeader[++j]="調整";
			csvHeader[++j]="軌跡類型";
			csvHeader[++j]="人工調整說明/系統調整類別";
	
			CSVUtil csv = new CSVUtil();
			csv.setHeader(csvHeader);
			csv.addRecordList(listCSV);
			String url = csv.generateCSV();
			notifyClientToDownloadFile(url, fileName);
			this.sendRtnObject(null);
		}
	}
	
	/**
	 * 由人工調整修改的數據作更新操作
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void updateData(Object body, IPrimitiveMap header) throws JBranchException
	{
		PMS214UpdateInputVO inputVO = (PMS214UpdateInputVO) body;
		try{
			dam = this.getDataAccessManager();
			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
			sb.append("		UPDATE TBPMS_PROD_AUM_ADJ M                           ");
			sb.append("		SET 	M.ADJ               =  ''        ,      	  ");
			sb.append("     	    M.LASTUPDATE = sysdate    ,     			  ");
			sb.append("     	    M.VERSION =  M.VERSION + 1 ,                  ");
			sb.append("     	    M.MODIFIER = :userId            			  ");
			sb.append("     WHERE M.SEQ_NO = :SEQ_NO             				  ");
			
			//插入操作
			qc.setObject("SEQ_NO", inputVO.getInputList().get(0).get("SEQ_NO")	);
			qc.setObject("userId", inputVO.getUserId());
			qc.setQueryString(sb.toString());
			int result = dam.exeUpdate(qc);
			
			inputVO.getInputList().get(0).put("ADJ", "V");
			insertData(body, header);
			//sendRtnObject(result);	
		}catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
	
	/**
	 * 由原始資料新增的數據作插入操作
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void insertData(Object body, IPrimitiveMap header) throws JBranchException
	{
		PMS214UpdateInputVO inputVO = (PMS214UpdateInputVO) body;
		try{
			dam = this.getDataAccessManager();
			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
			sb.append("		INSERT INTO TBPMS_PROD_AUM_ADJ                                  ");
			sb.append("			(SEQ_NO,YEARMON,CUSTID,AOCODE,LAST_AOCODE,                  ");
			sb.append("			ASSIS_ID,SAVE_AUM_BAL,LAST_SAVE_AUM_BAL,SAVE_INCREMENT,     ");
			sb.append("			SET_AUM_BAL,LAST_SET_AUM_BAL,SET_INCREMENT,COVER_AUM_BAL,   ");
			sb.append("			LAST_COVER_AUM_BAL,COVER_INCREMENT,LAST_EIP_LEVEL,EIP_LEVEL,");
			sb.append("			ADJ,ADJ_TYPE,ADJ_DESC,                                      ");
			sb.append("			VERSION,CREATETIME,CREATOR,MODIFIER,LASTUPDATE              ");
			sb.append("			)                                                           ");
			sb.append("		VALUES (                                                        ");
			sb.append("			  PABTH_BTPMS717.FN_GET_TZKHCPSY_NEXT_AUM_SEQ( :SEQ_NO )  , ");
			sb.append("			  :YEARMON           ,                                      ");
			sb.append("			  :CUSTID            ,                                      ");
			sb.append("			  :AOCODE            ,                                      ");
			sb.append("			  :LAST_AOCODE       ,                                      ");
			sb.append("			  :ASSIS_ID          ,                                      ");
			sb.append("			  :SAVE_AUM_BAL      ,                                      ");
			sb.append("			  :LAST_SAVE_AUM_BAL ,                                      ");
			sb.append("			  :SAVE_INCREMENT    ,                                      ");
			sb.append("			  :SET_AUM_BAL       ,                                      ");
			sb.append("			  :LAST_SET_AUM_BAL  ,                                      ");
			sb.append("			  :SET_INCREMENT     ,                                      ");
			sb.append("			  :COVER_AUM_BAL     ,                                      ");
			sb.append("			  :LAST_COVER_AUM_BAL,                                      ");
			sb.append("			  :COVER_INCREMENT   ,                                      ");
			sb.append("			  :LAST_EIP_LEVEL    ,                                      ");
			sb.append("			  :EIP_LEVEL         ,                                      ");
			sb.append("			  :ADJ               ,                                      ");
			sb.append("			  :ADJ_TYPE          ,                                      ");
			sb.append("			  :ADJ_DESC          ,                                      ");
			sb.append("			  0           ,                                     	    ");
			sb.append("			  sysdate        ,                                    	    ");
			sb.append("			  :userId           ,                                       ");
			sb.append("			  :userId          ,                                        ");
			sb.append("		  	  sysdate                                              	    ");
			sb.append("			)                                                           ");
			
			Set<Entry<String, Object>> set = inputVO.getInputList().get(0).entrySet();
			Iterator<Entry<String, Object>> iterator = set.iterator();
			while(iterator.hasNext()){
				Entry<String, Object> en = iterator.next();
				if(!en.getKey().contains("hashKey"))
					qc.setObject(en.getKey(), en.getValue()	);
			}
			//插入操作
			/*qc.setObject("YEARMON", inputVO.getInputList().get(0).get("YEARMON"));
			qc.setObject("TRANDATE", inputVO.getInputList().get(0).get("TRANDATE"));
			qc.setObject("CUSTID", inputVO.getInputList().get(0).get("CUSTID"));
			qc.setObject("CUSTNAME", inputVO.getInputList().get(0).get("CUSTNAME"));
			qc.setObject("AOCODE", inputVO.getInputList().get(0).get("AOCODE"));
			qc.setObject("TRANAOCODE", inputVO.getInputList().get(0).get("TRANAOCODE"));
			qc.setObject("PRODCODE", inputVO.getInputList().get(0).get("PRODCODE"));
			qc.setObject("PRODTYPE", inputVO.getInputList().get(0).get("PRODTYPE"));
			qc.setObject("PRODNAME", inputVO.getInputList().get(0).get("PRODNAME"));
			qc.setObject("PRODBEN", inputVO.getInputList().get(0).get("PRODBEN"));
			qc.setObject("PRODREALBEN", inputVO.getInputList().get(0).get("PRODREALBEN"));
			qc.setObject("PRODCNRBEN", inputVO.getInputList().get(0).get("PRODCNRBEN"));
			qc.setObject("ADJ", inputVO.getInputList().get(0).get("ADJ"));
			qc.setObject("ADJ_TYPE", inputVO.getInputList().get(0).get("ADJ_TYPE"));
			qc.setObject("ADJ_DESC", inputVO.getInputList().get(0).get("ADJ_DESC"));*/
			qc.setObject("userId", inputVO.getUserId());
	
			qc.setQueryString(sb.toString());
			int result = dam.exeUpdate(qc);
			sendRtnObject(result);	
		}catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
	
	/**
	 * 刪除數據
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void delData(Object body, IPrimitiveMap header) throws JBranchException
	{
		PMS214UpdateInputVO inputVO = (PMS214UpdateInputVO) body;
		try{
			dam = this.getDataAccessManager();
			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
			sb.append("  DELETE FROM TBPMS_PROD_AUM_ADJ    ");
			sb.append("  WHERE SEQ_NO = :SEQ_NO    ");
			
			qc.setObject("SEQ_NO", inputVO.getSeqNo());
			qc.setQueryString(sb.toString());
			int result = dam.exeUpdate(qc);
			sendRtnObject(result);
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
	* 軌跡類型下拉框格式轉換
	*  ADJ_TYPE 
	* @param map
	* @return String
	 * @throws JBranchException 
	*/
	
	private String typeFormat(Map map, String key) throws JBranchException {		
		if(typeMap==null){
			XmlInfo xmlInfo = new XmlInfo();
			typeMap = xmlInfo.doGetVariable("PMS.CNR_ADJ_TYPE", FormatHelper.FORMAT_3);     //軌跡類型	
		}			
		if (StringUtils.isNotBlank(String.valueOf(map.get(key)))
				&& map.get(key) != null) {
			String type = ""; 
			if(map.get(key)!=null)
				type=typeMap.get(map.get(key)+"");		
			return type;
		} else
			return "";
	}	
	/**
	* 軌跡類型下拉框格式轉換
	*  ADJ_DESC 
	* @param map
	* @return String
	 * @throws JBranchException 
	*/
	private String descFormat(Map map, String key) throws JBranchException {    		
		if(descMap==null){
//			XmlInfo xmlInfo = new XmlInfo();
//			descMap = xmlInfo.doGetVariable("PMS.CNR_SYS_ADJ_DESC", FormatHelper.FORMAT_3);     //軌跡類型	
			descMap=new HashMap();
			descMap.put("1", "歷史AO CODE調整客戶");
			descMap.put("2", "AC版塊調整客戶");
			descMap.put("3", "個金轉介法金客戶");
			descMap.put("4", "保險AUM遞延");
			descMap.put("5", "RM TABLE 客戶");
		
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
