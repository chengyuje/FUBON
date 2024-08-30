package com.systex.jbranch.app.server.fps.pms717;

import java.io.File;
import java.sql.Types;
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

import com.systex.jbranch.app.server.fps.pms341.PMS341;
import com.systex.jbranch.app.server.fps.pms341.PMS341InputVO;
import com.systex.jbranch.app.server.fps.pms341.PMS341OutputVO;
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
 * Project : fubon_1.wms<br>
 * JDK version used : JDK1.6.0 <br>
 * Description : <br>
 * Comments Name : PMS717.java<br>
 * Author :WKK<br>
 * Date :2017年3月30日 <br>
 * Version : 1.01 <br>
 * Editor : WKK<br>
 * Editor Date : 2017年3月30日<br>
 */
@Component("pms717")
@Scope("request")
public class PMS717 extends FubonWmsBizLogic 
{
	private DataAccessManager dam = null;	
	private Logger logger = LoggerFactory.getLogger(PMS341.class);
	/**
	 * 查詢
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void queryData(Object body, IPrimitiveMap header) throws JBranchException 
	{		
		PMS717InputVO inputVO = (PMS717InputVO) body;
		PMS717OutputVO outputVO = new PMS717OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		String prodType = inputVO.getProdType();
		String tableName = "";
		String PRD_CNAME = "";
		switch (prodType)
		{
		case "2":
			tableName = "TBPMS_SIINFO_M";					//SI
			PRD_CNAME = "SI_CNAME";
			break;
		case "3":
			tableName = "TBPMS_BONDINFO_M";					//海外債
			PRD_CNAME = "BOND_CNAME";
			break;
		case "4":
			tableName = "TBPMS_SNINFO_M";					//SN
			PRD_CNAME = "SI_CNAME";
			break;
		case "5":
			tableName = "TBPMS_ETF_M";						//海外ETF
			PRD_CNAME = "ETF_CNAME";
			break;
		case "6":
			tableName = "TBPMS_STOCK_M";					//海外股票
			PRD_CNAME = "STOCK_CNAME";
			break;
		}
		try 
		{
			if(inputVO.getProdType().equals("1")){
				sql.append("	SELECT                                       ");
				sql.append("		YEARMON,                                 ");
				sql.append("		PRD_ID,                                  ");
				sql.append("		FUND_ENAME,                              ");
				sql.append("		FUND_CNAME,                              ");
				sql.append("		MAIN_PRD,                                ");
				sql.append("		FUND_TYPE,                               ");
				sql.append("		NVL(CNR_YIELD,0) AS CNR_YIELD,                               ");
				sql.append("		ALLOTMENT_RATIO,                         ");
				sql.append("		NVL(CNR_MULTIPLE,0) AS CNR_MULTIPLE,                            ");
				sql.append("		TO_CHAR(RAISE_FUND_SDATE,'YYYYMMDD') RAISE_FUND_SDATE,    ");
				sql.append("		TO_CHAR(RAISE_FUND_EDATE,'YYYYMMDD') RAISE_FUND_EDATE,    ");
				sql.append("		CNR_DISCOUNT,                            ");
				sql.append("		RATE_DISCOUNT,                           ");
				//問題單:0003040 已解決問題
//				sql.append("		CNR_COMT_RATE,                           ");
				sql.append("		CNR_FEE,                                 ");				
				sql.append("		TO_CHAR(CNR_TARGET_SDATE,'YYYYMMDD') CNR_TARGET_SDATE,    ");
				sql.append("		TO_CHAR(CNR_TARGET_EDATE,'YYYYMMDD') CNR_TARGET_EDATE,    ");				
				sql.append("		FUS20,                                   ");
				sql.append("		VERSION,                                 ");
				sql.append("		CREATETIME,                              ");
				sql.append("		CREATOR,                                 ");
				sql.append("		LASTUPDATE,                              ");
				sql.append("		MODIFIER ,                               ");
				sql.append("		TO_CHAR(MAIN_PRD_SDATE,'YYYYMMDD') MAIN_PRD_SDATE,                          ");
				sql.append("		TO_CHAR(MAIN_PRD_EDATE,'YYYYMMDD') MAIN_PRD_EDATE                           ");
				sql.append("	FROM WMSUSER.TBPMS_FUNDINFO_M                ");
				sql.append("	WHERE YEARMON = :YEARMON                     ");
			}else if(inputVO.getProdType().equals("7")){
				sql.append("	SELECT                           			 ");
				sql.append("		YEARMON,                                 ");
				sql.append("		INSPRD_ID,                               ");
				sql.append("		INSPRD_NAME,                             ");
				sql.append("		INSPRD_ANNUAL,                           ");
				sql.append("		ANNUAL,                                  ");
				sql.append("		NVL(CNR_YIELD,0) AS CNR_YIELD,                               ");
				sql.append("		TO_CHAR(MULTIPLE_SDATE,'YYYYMMDD') MULTIPLE_SDATE,      ");
				sql.append("		TO_CHAR(MULTIPLE_EDATE,'YYYYMMDD') MULTIPLE_EDATE,      ");
				sql.append("		NVL(CNR_MULTIPLE,0) AS CNR_MULTIPLE,                            ");
				sql.append("		COMM_RATE,                               ");
				sql.append("		VERSION,                                 ");
				sql.append("		CREATETIME,                              ");
				sql.append("		CREATOR,                                 ");
				sql.append("		LASTUPDATE,                              ");
				sql.append("		MODIFIER                                 ");
				sql.append("	FROM WMSUSER.TBPMS_INS_M                     ");
				sql.append("	WHERE YEARMON = :YEARMON                     ");
			}else if(inputVO.getProdType().equals("2")||inputVO.getProdType().equals("4")){
				sql.append("	SELECT                       			     ");
				sql.append("		YEARMON,                                 ");
				sql.append("		PRD_ID,                                  ");
				sql.append("		"+PRD_CNAME+" AS PRD_CNAME,              ");
				sql.append("		NVL(CNR_YIELD,0) AS CNR_YIELD,                               ");
				if(inputVO.getProdType().equals("2")){
					sql.append("		PROFIT_RATE,                             ");   //PROFIT_RATE  si 
				}else{
					sql.append("		RATE_OF_CHANNEL,                         ");   //通路服務費率
				}	
				sql.append("		PERFORMANCE_REVIEW,                      "); //20170613 新增欄位[ 計績檔次 ]
				sql.append("		VERSION,                                 ");
				sql.append("		CREATETIME,                              ");
				sql.append("		CREATOR,                                 ");
				sql.append("		LASTUPDATE,                              ");
				sql.append("		MODIFIER                                 ");
				sql.append("	FROM WMSUSER." + tableName                	  );
				sql.append("	WHERE YEARMON = :YEARMON                     ");
			}else{
				sql.append("	SELECT                       			     ");
				sql.append("		YEARMON,                                 ");
				sql.append("		PRD_ID,                                  ");
				sql.append("		"+PRD_CNAME+" AS PRD_CNAME,              ");
				sql.append("		NVL(CNR_YIELD,0) AS CNR_YIELD,                               ");
				sql.append("		NVL(CNR_MULTIPLE,0) AS CNR_MULTIPLE,                            ");
				sql.append("		TO_CHAR(RAISE_FUND_SDATE,'YYYYMMDD') RAISE_FUND_SDATE,    ");
				sql.append("		TO_CHAR(RAISE_FUND_EDATE,'YYYYMMDD') RAISE_FUND_EDATE,    ");
				if(inputVO.getProdType().equals("6")){
					sql.append("		STOCK_CODE, ");
					sql.append("		OLD_PROD_FLG, ");
				}
				sql.append("		VERSION,                                 ");
				sql.append("		CREATETIME,                              ");
				sql.append("		CREATOR,                                 ");
				sql.append("		LASTUPDATE,                              ");
				sql.append("		MODIFIER                                 ");
				sql.append("	FROM WMSUSER." + tableName                	  );
				sql.append("	WHERE YEARMON = :YEARMON                     ");
			}
			if(!StringUtils.isBlank(inputVO.getProdId()) &&!prodType.equals("7")){
				sql.append(" AND PRD_ID = :PRD_ID ");
				condition.setObject("PRD_ID", inputVO.getProdId());
			}
			if(!StringUtils.isBlank(inputVO.getProdId()) &&prodType.equals("7")){
				sql.append(" AND INSPRD_ID = :INSPRD_ID ");
				condition.setObject("INSPRD_ID", inputVO.getProdId());
			}
			sql.append("	ORDER BY YEARMON	");
			condition.setObject("YEARMON", inputVO.getYearMon());
			condition.setQueryString(sql.toString());
			//分頁結果集
			ResultIF list = dam.executePaging(condition, inputVO
					.getCurrentPageIndex() + 1, inputVO.getPageCount());
			//查詢所有結果集
			List<Map<String, Object>> list1 = dam.exeQuery(condition);
			int totalPage_i = list.getTotalPage();
			int totalRecord_i = list.getTotalRecord();
			outputVO.setResultList(list);
			outputVO.setCsvList(list1);
			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());  // 當前頁次
			outputVO.setTotalPage(totalPage_i);                           // 總頁次
			outputVO.setTotalRecord(totalRecord_i);                       // 總筆數
			// result
			this.sendRtnObject(outputVO);
			
		} catch (Exception e) 
		{
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
	
	//從excel表格中新增數據
	@SuppressWarnings({ "rawtypes", "unused" })
	public void addData(Object body, IPrimitiveMap header) throws APException
	{
		int flag = 0;
		try 
		{
			PMS717InputVO inputVO = (PMS717InputVO) body;
			PMS717OutputVO outputVO = new PMS717OutputVO();
			//清空臨時表
			dam = this.getDataAccessManager();
			QueryConditionIF dcon = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			String dsql = "";
			if(inputVO.getProdType().equals("1")){
				 dsql = " TRUNCATE TABLE TBPMS_FUNDINFO_M_U ";				
			}else if(inputVO.getProdType().equals("7")){
				 dsql = " TRUNCATE TABLE TBPMS_INS_M_U ";								
			}else{
				 dsql = " TRUNCATE TABLE TBPMS_OTHER_M_U ";												
			}
			dcon.setQueryString(dsql.toString());
			dam.exeUpdate(dcon);
			//獲取上傳數據
			List<String> import_file = new ArrayList<String>();
			List<String> list =  new ArrayList<String>();
			String joinedPath = new File((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), inputVO.getFileName()).toString();
			Workbook workbook = Workbook.getWorkbook(new File(joinedPath));
			Sheet sheet[] = workbook.getSheets();
			//有表頭.xls文檔
			String lab = null;
			for(int a=0;a<sheet.length;a++)
			{
				for(int i=1;i<sheet[a].getRows();i++)
				{
					for(int j=0;j<sheet[a].getColumns();j++)
					{
						lab = sheet[a].getCell(j, i).getContents();
						list.add(lab);
					}
					
					//excel表格記行數
					flag++;
					
					//判斷當前上傳數據欄位個數是否一致
					if(inputVO.getProdType().equals("1") && list.size()!=19){
						throw new APException("上傳數據欄位個數不一致");
					}else if(inputVO.getProdType().equals("7") && list.size()!=10){
						throw new APException("上傳數據欄位個數不一致");
					}else if(inputVO.getProdType().equals("2") && list.size()!=6 
							|| inputVO.getProdType().equals("4") && list.size()!=6){
						throw new APException("上傳數據欄位個數不一致");
					}else if(inputVO.getProdType().equals("3")
							||inputVO.getProdType().equals("5")
							){
						if(list.size()!=7){
							throw new APException("上傳數據欄位個數不一致");													
						}
					}else if(inputVO.getProdType().equals("6")){
						if(list.size()!=9){
							throw new APException("上傳數據欄位個數不一致");													
						}
					}
					
					//SQL指令
					dam = this.getDataAccessManager();
					StringBuffer sb = new StringBuffer();
					QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					if(inputVO.getProdType().equals("1")){
						sb.append("		INSERT INTO WMSUSER.TBPMS_FUNDINFO_M_U(           ");
						sb.append("			RNUM,                                         ");
						sb.append("			YEARMON,                                      ");
						sb.append("			PRD_ID,                                       ");
						sb.append("			FUND_ENAME,                                   ");
						sb.append("			FUND_CNAME,                                   ");
						sb.append("			MAIN_PRD,                                     ");
						//MAIN_PRD_SDATE
						sb.append("			MAIN_PRD_SDATE,                               ");
						sb.append("			MAIN_PRD_EDATE,                               ");
						sb.append("			FUND_TYPE,                                    ");
						sb.append("			CNR_YIELD,                                    ");
						sb.append("			ALLOTMENT_RATIO,                              ");
						sb.append("			CNR_MULTIPLE,                                 ");
						sb.append("			RAISE_FUND_SDATE,                             ");
						sb.append("			RAISE_FUND_EDATE,                             ");
						sb.append("			CNR_DISCOUNT,                                 ");
						sb.append("			RATE_DISCOUNT,                                ");
//						sb.append("			CNR_COMT_RATE,                                ");
						sb.append("			CNR_FEE,                                      ");						
						sb.append("			CNR_TARGET_SDATE,                             ");
						sb.append("			CNR_TARGET_EDATE,                             ");						
						sb.append("			FUS20,                                        ");
						sb.append("			VERSION,                                      ");
						sb.append("			CREATETIME,                                   ");
						sb.append("			CREATOR,                                      ");
						sb.append("			LASTUPDATE,                                   ");
						sb.append("			MODIFIER)                                     ");
						sb.append("		VALUES (                                          ");
						sb.append("			:RNUM,                                        ");
						sb.append("			:YEARMON,                                     ");
						sb.append("			:PRD_ID,                                      ");
						sb.append("			:FUND_ENAME,                                  ");
						sb.append("			:FUND_CNAME,                                  ");
						sb.append("			:MAIN_PRD,                                    ");
						sb.append("			:MAIN_PRD_SDATE,                              ");
						sb.append("			:MAIN_PRD_EDATE,                              ");
						sb.append("			:FUND_TYPE,                                   ");
						sb.append("			:CNR_YIELD,                                   ");
						sb.append("			:ALLOTMENT_RATIO,                             ");
						sb.append("			:CNR_MULTIPLE,                                ");
						sb.append("			:RAISE_FUND_SDATE,                            ");
						sb.append("			:RAISE_FUND_EDATE,                            ");
						sb.append("			:CNR_DISCOUNT,                                ");
						sb.append("			:RATE_DISCOUNT,                               ");
						//問題單:0003040 
//						sb.append("			:CNR_COMT_RATE,                               ");
						sb.append("			:CNR_FEE,                                     ");
						sb.append("			:CNR_TARGET_SDATE,                            ");
						sb.append("			:CNR_TARGET_EDATE,                            ");
						sb.append("			:FUS20,                                       ");
						sb.append("			:VERSION,                                     ");
						sb.append("			SYSDATE,                                  	  ");
						sb.append("			:CREATOR,                                     ");
						sb.append("			SYSDATE,                                  	  ");
						sb.append("			:MODIFIER)                                    ");
						qc.setObject("YEARMON",list.get(0).trim()                 		   );
						qc.setObject("PRD_ID",list.get(1).trim()                           );
						qc.setObject("FUND_ENAME",list.get(2).trim()                       );
						qc.setObject("FUND_CNAME",list.get(3).trim()                       );
						qc.setObject("MAIN_PRD",list.get(4).trim()                         );
						//註記	MAIN_PRD_SDATE
						qc.setObject("MAIN_PRD_SDATE",list.get(5).trim()                        );
						qc.setObject("MAIN_PRD_EDATE",list.get(6).trim()                        );
						qc.setObject("FUND_TYPE",list.get(7).trim()                        );
						qc.setObject("CNR_YIELD",list.get(8).trim()                        );
						qc.setObject("ALLOTMENT_RATIO",list.get(9).trim()                  );
						qc.setObject("CNR_MULTIPLE",list.get(10).trim()                     );
						qc.setObject("RAISE_FUND_SDATE",list.get(11).trim()                 );
						qc.setObject("RAISE_FUND_EDATE",list.get(12).trim()                );
						qc.setObject("CNR_DISCOUNT",list.get(13).trim()                    );
						qc.setObject("RATE_DISCOUNT",list.get(14).trim()                   );
						//問題單:0003040 已註解項目
//						qc.setObject("CNR_COMT_RATE",list.get(15).trim()                   );
						qc.setObject("CNR_FEE",list.get(15).trim()                         );	
						qc.setObject("CNR_TARGET_SDATE",list.get(16).trim()                );
						qc.setObject("CNR_TARGET_EDATE",list.get(17).trim()                );						
						qc.setObject("FUS20",list.get(18).trim()                           );
						qc.setObject("RNUM",flag                                           );
						qc.setObject("VERSION","0"                                         );
					}else if(inputVO.getProdType().equals("7")){
						sb.append("	INSERT INTO	 WMSUSER.TBPMS_INS_M_U(   ");
						sb.append("			RNUM,                                         ");
						sb.append("			YEARMON,                                      ");
						sb.append("			INSPRD_ID,                                    ");
						sb.append("			INSPRD_NAME,                                  ");
						sb.append("			INSPRD_ANNUAL,                                ");
						sb.append("			ANNUAL,                                       ");
						sb.append("			CNR_YIELD,                                    ");
						sb.append("			MULTIPLE_SDATE,                               ");
						sb.append("			MULTIPLE_EDATE,                               ");
						sb.append("			CNR_MULTIPLE,                                 ");
						sb.append("			COMM_RATE,                                    ");
						sb.append("			VERSION,                                      ");
						sb.append("			CREATETIME,                                   ");
						sb.append("			CREATOR,                                      ");
						sb.append("			LASTUPDATE,                                   ");
						sb.append("			MODIFIER)                                     ");
						sb.append("		VALUES (                                          ");
						sb.append("			:RNUM,                                        ");
						sb.append("			:YEARMON,                                     ");
						sb.append("			:INSPRD_ID,                                   ");
						sb.append("			:INSPRD_NAME,                                 ");
						sb.append("			:INSPRD_ANNUAL,                               ");
						sb.append("			:ANNUAL,                                      ");
						sb.append("			:CNR_YIELD,                                   ");
						sb.append("			:MULTIPLE_SDATE,                              ");
						sb.append("			:MULTIPLE_EDATE,                              ");
						sb.append("			:CNR_MULTIPLE,                                ");
						sb.append("			:COMM_RATE,                                   ");
						sb.append("			:VERSION,                                     ");
						sb.append("			SYSDATE,                                      ");
						sb.append("			:CREATOR,                                     ");
						sb.append("			SYSDATE,                                      ");
						sb.append("			:MODIFIER)                                    ");
						qc.setObject("YEARMON",list.get(0).trim()                		   );
						qc.setObject("INSPRD_ID",list.get(1).trim()                    );
						qc.setObject("INSPRD_NAME",list.get(2).trim()                    );
						qc.setObject("INSPRD_ANNUAL",list.get(3).trim()                    );
						qc.setObject("ANNUAL",list.get(4).trim()                           );
						qc.setObject("CNR_YIELD",list.get(5).trim()                        );
						qc.setObject("MULTIPLE_SDATE",list.get(6).trim()                   );
						qc.setObject("MULTIPLE_EDATE",list.get(7).trim()                   );
						qc.setObject("CNR_MULTIPLE",list.get(8).trim()                     );
						qc.setObject("COMM_RATE",list.get(9).trim()                        );
						qc.setObject("RNUM",flag                                           );
						qc.setObject("VERSION","0"                                         );
					}else if(inputVO.getProdType().equals("2")||inputVO.getProdType().equals("4")){
						sb.append("	INSERT INTO WMSUSER.TBPMS_OTHER_M_U(	  ");
						sb.append("			RNUM,                                         ");
						sb.append("			YEARMON,                                      ");
						sb.append("			PRD_ID,                                       ");
						sb.append("			PRD_CNAME,                                    ");
						sb.append("			CNR_YIELD,                                    ");
						sb.append("			CNR_RATE,                                     ");   //2017/0612增加第五欄位
						sb.append("			PERFORMANCE_REVIEW,                           ");   //2017/0613增加第六欄位  計績檔次
						sb.append("			VERSION,                                      ");
						sb.append("			CREATETIME,                                   ");
						sb.append("			CREATOR,                                      ");
						sb.append("			MODIFIER,                                     ");
						sb.append("			LASTUPDATE)                                   ");
						sb.append("	VALUES (                                              ");
						sb.append("			:RNUM,                                        ");
						sb.append("			:YEARMON,                                     ");
						sb.append("			:PRD_ID,                                      ");
						sb.append("			:PRD_CNAME,                                   ");
						sb.append("			:CNR_YIELD,                                   ");
						sb.append("			:CNR_RATE,                                    ");   //2017/0612增加第五欄位
						sb.append("			:PERFORMANCE_REVIEW,                          ");   //2017/0613增加第五欄位
						sb.append("			:VERSION,                                     ");
						sb.append("			SYSDATE,                                      ");
						sb.append("			:CREATOR,                                     ");
						sb.append("			:MODIFIER,                                    ");
						sb.append("			SYSDATE)                                      ");
						qc.setObject("YEARMON",list.get(0).trim()                		   );
						qc.setObject("PRD_ID",list.get(1).trim()                           );
						qc.setObject("PRD_CNAME",list.get(2).trim()                        );
						qc.setObject("CNR_YIELD",list.get(3).trim()                        );
						qc.setObject("CNR_RATE",list.get(4).trim()                         );
						qc.setObject("PERFORMANCE_REVIEW",list.get(5).trim()               );
						qc.setObject("RNUM",flag                                           );
						qc.setObject("VERSION","0"                                         );
					}else{
						sb.append("	INSERT INTO WMSUSER.TBPMS_OTHER_M_U(	  ");
						sb.append("			RNUM,                                         ");
						sb.append("			YEARMON,                                      ");
						sb.append("			PRD_ID,                                       ");
						sb.append("			PRD_CNAME,                                    ");
						sb.append("			CNR_YIELD,                                    ");
						sb.append("			CNR_MULTIPLE,                                 ");
						sb.append("			RAISE_FUND_SDATE,                             ");
						sb.append("			RAISE_FUND_EDATE,                             ");
						if(inputVO.getProdType().equals("6")){
							sb.append("			STOCK_CODE,                             ");
							sb.append("			OLD_PROD_FLG,                             ");
						}		
						sb.append("			VERSION,                                      ");
						sb.append("			CREATETIME,                                   ");
						sb.append("			CREATOR,                                      ");
						sb.append("			MODIFIER,                                     ");
						sb.append("			LASTUPDATE)                                   ");
						sb.append("	VALUES (                                              ");
						sb.append("			:RNUM,                                        ");
						sb.append("			:YEARMON,                                     ");
						sb.append("			:PRD_ID,                                      ");
						sb.append("			:PRD_CNAME,                                    ");
						sb.append("			:CNR_YIELD,                                   ");
						sb.append("			:CNR_MULTIPLE,                                ");
						sb.append("			:RAISE_FUND_SDATE,                            ");
						sb.append("			:RAISE_FUND_EDATE,                            ");
						if(inputVO.getProdType().equals("6")){
							sb.append("			:STOCK_CODE,                             ");
							sb.append("			:OLD_PROD_FLG,                             ");
						}	
						sb.append("			:VERSION,                                     ");
						sb.append("			SYSDATE,                                      ");
						sb.append("			:CREATOR,                                     ");
						sb.append("			:MODIFIER,                                    ");
						sb.append("			SYSDATE)                                      ");
						qc.setObject("YEARMON",list.get(0).trim()                		   );
						qc.setObject("PRD_ID",list.get(1).trim()                           );
						qc.setObject("PRD_CNAME",list.get(2).trim()                         );
						qc.setObject("CNR_YIELD",list.get(3).trim()                        );
						qc.setObject("CNR_MULTIPLE",list.get(4).trim()                     );
						qc.setObject("RAISE_FUND_SDATE",list.get(5).trim()                 );
						qc.setObject("RAISE_FUND_EDATE",list.get(6).trim()                 );
						if(inputVO.getProdType().equals("6")){
							qc.setObject("STOCK_CODE",list.get(7).trim()                 );
							qc.setObject("OLD_PROD_FLG",list.get(8).trim()                 );							
						}
						qc.setObject("RNUM",flag                                           );
						qc.setObject("VERSION","0"                                         );
					}
					qc.setObject("CREATOR", (String) getUserVariable(FubonSystemVariableConsts.LOGINID)                     );
					qc.setObject("MODIFIER", (String) getUserVariable(FubonSystemVariableConsts.LOGINID)                     );
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
	@SuppressWarnings({ "unused", "rawtypes" })
	public void callStored(Object body, IPrimitiveMap header) throws APException
	{
		try
		{
			PMS717InputVO inputVO = (PMS717InputVO) body;
			PMS717OutputVO outputVO = new PMS717OutputVO();
			//執行存儲過程
			dam = this.getDataAccessManager();
			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
			if(inputVO.getProdType().equals("1")){
				sb.append(" CALL PABTH_BTPMS733.SP_TBPMS_FUNDINFO_M(? ,?, ? ) ");
			}else if(inputVO.getProdType().equals("7")){
				sb.append(" CALL PABTH_BTPMS733.SP_TBPMS_INS_M(? ,?, ? ) ");				
			}else{
				sb.append(" CALL PABTH_BTPMS733.SP_TBPMS_OTHER_M(? ,?, ? ) ");												
			}
			qc.setString(1, inputVO.getYearMon());
			qc.setString(2, inputVO.getProdType());
			qc.registerOutParameter(3, Types.VARCHAR);
			qc.setQueryString(sb.toString());
			Map<Integer, Object> resultMap = dam.executeCallable(qc);
			String str = (String) resultMap.get(3);
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
		PMS717OutputVO outputVO = (PMS717OutputVO) body;
		List<Map<String, Object>> list = outputVO.getCsvList();
		if(list.size() > 0)
		{
			String prodType = outputVO.getProdType();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH:mm:ss");
			String fileName = ""; 
			String PRD_CNAME = "";
			switch (prodType)
			{
			case "1":
				fileName = "歷史月份基金商品主檔_" + sdf.format(new Date()) + ".csv";
				break;
			case "2":
				PRD_CNAME = "SI_CNAME";							//SI
				fileName = "歷史月份SI商品主檔_" + sdf.format(new Date()) + ".csv";
				break;
			case "3":
				PRD_CNAME = "BOND_CNAME";						//海外債
				fileName = "歷史月份海外債商品主檔_" + sdf.format(new Date()) + ".csv";
				break;
			case "4":
				PRD_CNAME = "SI_CNAME";							//SN
				fileName = "歷史月份SN商品主檔_" + sdf.format(new Date()) + ".csv";
				break;
			case "5":
				PRD_CNAME = "ETF_CNAME";						//海外ETF
				fileName = "歷史月份海外ETF商品主檔_" + sdf.format(new Date()) + ".csv";
				break;
			case "6":
				PRD_CNAME = "STOCK_CNAME";						//海外股票
				fileName = "歷史月份海外股票商品主檔_" + sdf.format(new Date()) + ".csv";
				break;
			case "7":
				fileName = "歷史月份保險商品主檔_" + sdf.format(new Date()) + ".csv";
				break;
			}
			
			List listCSV =  new ArrayList();
			for(Map<String, Object> map : list){
				String[] records = new String[list.get(0).size()];
				int i = 0;
				if(outputVO.getProdType().equals("1")){
					records[i] = checkIsNull(map, "YEARMON");                    //數據月份
					records[++i] = checkIsNull(map, "PRD_ID");                   //商品代碼
					records[++i] = checkIsNull(map, "FUND_ENAME");               //基金英文名稱
					records[++i] = checkIsNull(map, "FUND_CNAME");         		 //基金中文名稱
					records[++i] = checkIsNull(map, "MAIN_PRD");                 //主推
					records[++i] = checkIsNull(map, "MAIN_PRD_SDATE");           //主推時間起日
					records[++i] = checkIsNull(map, "MAIN_PRD_EDATE");           //主推時間起日
					records[++i] = checkIsNull(map, "FUND_TYPE");                //基金類型
					records[++i] = checkIsNull(map, "CNR_YIELD");                //CNR分配率
					records[++i] = checkIsNull(map, "ALLOTMENT_RATIO");          //拆帳比率
					records[++i] = checkIsNull(map, "CNR_MULTIPLE");             //CNR加減碼
					records[++i] = checkIsNull(map, "RAISE_FUND_SDATE");         //加碼區間起日
					records[++i] = checkIsNull(map, "RAISE_FUND_EDATE");         //加碼區間迄日
					records[++i] = checkIsNull(map, "CNR_DISCOUNT");             //CNR收益扣減率
					records[++i] = checkIsNull(map, "RATE_DISCOUNT");            //實際收益扣減率
					//問題單:0003040
//					records[++i] = checkIsNull(map, "CNR_COMT_RATE");            //基金實際收益拆分比
					records[++i] = checkIsNull(map, "CNR_FEE");                  //CNR基金管理費回饋					
					records[++i] = checkIsNull(map, "CNR_TARGET_SDATE");         //基金管理費標的計績追溯起日
					records[++i] = checkIsNull(map, "CNR_TARGET_EDATE");         //基金管理費標的計績追溯迄日					
					records[++i] = checkIsNull(map, "FUS20");                    //是否為國內基金,若為國內基金，則此欄位為'C'，否則為空值
				}else if(outputVO.getProdType().equals("7")){
					records[i] = checkIsNull(map, "YEARMON");                    //數據月份
					records[++i] = checkIsNull(map, "INSPRD_ID");                //險種代碼
					records[++i] = checkIsNull(map, "INSPRD_NAME");              //險種名稱
					records[++i] = checkIsNull(map, "INSPRD_ANNUAL");            //繳費年期
					records[++i] = checkIsNull(map, "ANNUAL");                   //保單年度
					records[++i] = checkIsNull(map, "CNR_YIELD");         		 //CNR分配率
					records[++i] = checkIsNull(map, "MULTIPLE_SDATE");           //加碼區間起日
					records[++i] = checkIsNull(map, "MULTIPLE_EDATE");           //加碼區間迄日
					records[++i] = checkIsNull(map, "CNR_MULTIPLE");             //CNR加減碼
					records[++i] = checkIsNull(map, "COMM_RATE");                //佣金率
				}else if(outputVO.getProdType().equals("2")||outputVO.getProdType().equals("4")){
					records[i] = checkIsNull(map, "YEARMON");                    //數據月份
					records[++i] = checkIsNull(map, "PRD_ID");                   //產品ID
					records[++i] = checkIsNull(map, "PRD_CNAME");                //產品名稱
					records[++i] = checkIsNull(map, "CNR_YIELD");         		 //CNR分配率
					if(outputVO.getProdType().equals("2"))
						records[++i] = checkIsNull(map, "PROFIT_RATE");          //銀行收益率  2017/06/12
					else
						records[++i] = checkIsNull(map, "RATE_OF_CHANNEL");      //通路服務費率   2017/06/12
					records[++i] = checkIsNull(map, "PERFORMANCE_REVIEW");       //計積檔次
				}else{
					
					records[i] = checkIsNull(map, 	"YEARMON");                    //數據月份
					records[++i] = checkIsNull(map, "PRD_ID");                   //產品ID
					records[++i] = checkIsNull(map, "PRD_CNAME");                //產品名稱
					records[++i] = checkIsNull(map, "CNR_YIELD");         		 //CNR分配率
					records[++i] = checkIsNull(map, "CNR_MULTIPLE");             //CNR加減碼
					records[++i] = checkIsNull(map, "RAISE_FUND_SDATE");         //加碼區間起日
					records[++i] = checkIsNull(map, "RAISE_FUND_EDATE");         //加碼區間迄日
					if(outputVO.getProdType().equals("6")){
						//海外股票
						//交易所代碼
						//舊商品註記
						records[++i] = checkIsNull(map, "STOCK_CODE");         //交易所代碼
						records[++i] = checkIsNull(map, "OLD_PROD_FLG");         //舊商品註記日
					}
						
				}
				listCSV.add(records);
				
			}
			//header
			String [] csvHeader = new String[list.get(0).size()];
			int j = 0;
			if(outputVO.getProdType().equals("1")){
				csvHeader[j] = "數據月份";
				csvHeader[++j] = "商品代碼";
				csvHeader[++j] = "基金英文名稱";
				csvHeader[++j] = "基金中文名稱";
				csvHeader[++j] = "主推";
				csvHeader[++j] = "主推時間起日";
				csvHeader[++j] = "主推時間訖日";
				csvHeader[++j] = "基金類型";
				csvHeader[++j] = "CNR分配率";
				csvHeader[++j] = "拆帳比率";
				csvHeader[++j] = "CNR加減碼";
				csvHeader[++j] = "加碼區間起日";
				csvHeader[++j] = "加碼區間迄日";
				csvHeader[++j] = "CNR收益扣減率";
				csvHeader[++j] = "實際收益扣減率";
				//問題單: 0003040 註解項目
//				csvHeader[++j] = "基金實際收益拆分比";
				csvHeader[++j] = "CNR基金管理費回饋";				
				csvHeader[++j] = "基金管理費標的計績追溯起日";
				csvHeader[++j] = "基金管理費標的計績追溯迄日";	
				csvHeader[++j] = "是否為國內基金";

			}else if(outputVO.getProdType().equals("7")){
				csvHeader[j] = "數據月份";
				csvHeader[++j] = "險種代碼";
				csvHeader[++j] = "險種名稱";
				csvHeader[++j] = "繳費年期";
				csvHeader[++j] = "保單年度";
				csvHeader[++j] = "CNR分配率";
				csvHeader[++j] = "加碼區間起日";
				csvHeader[++j] = "加碼區間迄日";
				csvHeader[++j] = "CNR加減碼";
				csvHeader[++j] = "佣金率";
			}else if(outputVO.getProdType().equals("2")||outputVO.getProdType().equals("4")){
				csvHeader[j] = "數據月份";
				csvHeader[++j] = "產品ID";
				csvHeader[++j] = "產品名稱";
				csvHeader[++j] = "CNR分配率";
				if(outputVO.getProdType().equals("2"))
					csvHeader[++j] = "銀行收益率";           //銀行收益率   2017/06/12
				else
					csvHeader[++j] = "通路服務費率";          //通路服務費率   2017/06/12	
				csvHeader[++j] = "計績檔次";         		  //計績檔次   2017/06/13		
			}else{
				csvHeader[j] = "數據月份";
				csvHeader[++j] = "產品ID";
				csvHeader[++j] = "產品名稱";
				csvHeader[++j] = "CNR分配率";
				csvHeader[++j] = "CNR加減碼";
				csvHeader[++j] = "加碼區間起日";
				csvHeader[++j] = "加碼區間迄日";
				if(outputVO.getProdType().equals("6")){
					//海外股票
					//交易所代碼
					//舊商品註記
					csvHeader[++j] ="交易所代碼";
					csvHeader[++j] ="舊商品註記日";
				}			
			}
			
			CSVUtil csv = new CSVUtil();
			csv.setHeader(csvHeader);
			csv.addRecordList(listCSV);
			String url = csv.generateCSV();
			notifyClientToDownloadFile(url, fileName);
			this.sendRtnObject(null);
		} else 
		{
			outputVO.setResultList(list);
			this.sendRtnObject(outputVO);
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
			return String.valueOf("=\""+map.get(key)+"\"");
		}else{
			return "";
		}
	}
	
	
}
