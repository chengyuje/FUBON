package com.systex.jbranch.app.server.fps.pms716;
import java.io.File;
import java.sql.Types;
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

import com.systex.jbranch.app.server.fps.pms716.PMS716InputVO;
import com.systex.jbranch.app.server.fps.pms716.PMS716OutputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
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
 * Description : <br>
 * Comments Name : pms716InputVO.java<br>
 * Author :CTY<br>
 * Date :2016年12月22日 <br>
 * Version : 1.01 <br>
 * Editor : CTY<br>
 * Editor Date : 2016年11月14日<br>
 */
@Component("pms716")
@Scope("request")
public class PMS716 extends FubonWmsBizLogic
{
	public DataAccessManager dam = null;
	@SuppressWarnings("unused")
	private Logger logger = LoggerFactory.getLogger(PMS716.class);
	/**
	 * 查詢當月份參數維護（主表）
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void queryData(Object body, IPrimitiveMap header) throws JBranchException
	{	try
		{
		PMS716InputVO inputVO = (PMS716InputVO) body;
		PMS716OutputVO outputVO = new PMS716OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append("  SELECT YEARMON,                     ");
		sb.append("         MINOR_ENT_EMP,               ");
		sb.append("         MINOR_ENT_BRANCH,            ");
		sb.append("         DC_EMP,                      ");
		sb.append("         DC_BRANCH,                   ");
		sb.append("         RESTS                        ");
		sb.append("  FROM   TBPMS_KPI_NEW_ADD_KPI        ");
		sb.append("    WHERE YEARMON = :YEARMON"          );
		qc.setObject("YEARMON", inputVO.getsTime()        );
		qc.setQueryString(sb.toString()                   );
		List<Map<String, Object>> result = dam.exeQuery(qc);
		outputVO.setResultList(result);
		sendRtnObject(outputVO);
		}
		catch (Exception e)
		{
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			throw new APException(e.getMessage());
		}
	}
	
	/**
	 * 保險產品明細表查詢
	 * @param body
	 * @param header
	 * @throws APException
	 */
	public void queryInsureDetail(Object body, IPrimitiveMap header) throws JBranchException
	{
		try
		{
			PMS716InputVO inputVO = (PMS716InputVO) body;
			PMS716OutputVO outputVO = new PMS716OutputVO();
			dam = this.getDataAccessManager();
			QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			sql.append("   SELECT 								              ");
			sql.append("          PRD_ID,                                     ");
			sql.append("          BOND_NAME,                                  ");
			sql.append("          PAY_TYPE,                                   ");
			sql.append("          COIN_TYPE,                                  ");
			sql.append("          FOUR_SORT,                                  ");
			sql.append("          SHAPE,                                      ");
			sql.append("          PAY_TYPE_E,                                 ");
			sql.append("          PRI_ADD_PACT,                               ");
			sql.append("          REMARK,                                     ");
			sql.append("          UPDATE_DATE                                 ");
			sql.append("  FROM TBPMS_KPI_NEW_ADD_INSURE_DET                   ");
			sql.append("  ORDER BY PRD_ID                                     ");
			condition.setQueryString(sql.toString());
			ResultIF largeAgrList = dam.executePaging(condition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
			int totalPage_i = largeAgrList.getTotalPage(); // 分頁用
			int totalRecord_i = largeAgrList.getTotalRecord(); // 分頁用
			outputVO.setResultList(largeAgrList); // data
			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
			outputVO.setTotalPage(totalPage_i);// 總頁次
			outputVO.setTotalRecord(totalRecord_i);// 總筆數
			this.sendRtnObject(outputVO);
		}
		catch (Exception e)
		{
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			throw new APException(e.getMessage());
		}
	}
	
	/**
	 * 保險產品明細表 上傳
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	@SuppressWarnings("rawtypes")
	public void addInsureDetail(Object body, IPrimitiveMap header) throws JBranchException
	{
		PMS716InputVO inputVO = (PMS716InputVO) body;
		PMS716OutputVO outputVO = new PMS716OutputVO();
		int flag = 0;
		try{
			List<String> list =  new ArrayList<String>();
			String joinedPath = new File((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), inputVO.getFileName()).toString();
			Workbook workbook = Workbook.getWorkbook(new File(joinedPath));
			Sheet sheet[] = workbook.getSheets();
			//有表頭.xls文檔
			//清空臨時表
			dam = this.getDataAccessManager();
			QueryConditionIF dcon = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);	
			String dsql = " TRUNCATE TABLE TBPMS_KPI_NEW_ADD_INSURE_DET_U ";
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
					if(list.size()!=9){
						throw new APException("上傳數據欄位個數不一致");
					}
					//SQL指令
					StringBuffer sb = new StringBuffer();
					dam = this.getDataAccessManager();
					QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sb.append("   INSERT INTO TBPMS_KPI_NEW_ADD_INSURE_DET_U (               ");
					sb.append("          PRD_ID,                                             ");
					sb.append("          BOND_NAME,                                          ");
					sb.append("          PAY_TYPE,                                           ");
					sb.append("          COIN_TYPE,                                          ");
					sb.append("          FOUR_SORT,                                          ");
					sb.append("          SHAPE,                                              ");
					sb.append("          PAY_TYPE_E,                                         ");
					sb.append("          PRI_ADD_PACT,                                       ");
					sb.append("          REMARK,                                             ");
					sb.append("          UPDATE_DATE,                                        ");
					sb.append("  		 RNUM,            					                 ");		
					sb.append("  		 VERSION,            						         ");
					sb.append("  		 CREATETIME,             					         ");
					sb.append("  		 CREATOR,             						         ");
					sb.append("  		 MODIFIER,         						             ");
					sb.append("  		 LASTUPDATE )             					         ");
					sb.append("  	VALUES(:PRD_ID,            				                 ");
					sb.append("  		:BOND_NAME,             					         ");
					sb.append("  		:PAY_TYPE,             				                 ");
					sb.append("  		:COIN_TYPE,             				             ");
					sb.append("  		:FOUR_SORT,             					         ");
					sb.append("  		:SHAPE,             					             ");
					sb.append("  		:PAY_TYPE_E,             					         ");
					sb.append("  		:PRI_ADD_PACT,             					         ");
					sb.append("  		:REMARK,             					             ");
					sb.append("  		SYSDATE,             					             ");
					sb.append("  		:RNUM,             					                 ");
					sb.append("  		:VERSION,           					             ");
					sb.append("  		SYSDATE,           				                     ");
					sb.append("  		:CREATOR,            					             ");
					sb.append("  		:MODIFIER,         					                 ");
					sb.append("  		SYSDATE)          				                     ");
					qc.setObject("PRD_ID",list.get(0).trim()                                  );
					qc.setObject("BOND_NAME",list.get(1).trim()                               );
					qc.setObject("PAY_TYPE",list.get(2).trim()                                );
					qc.setObject("COIN_TYPE",list.get(3).trim()                               );
					qc.setObject("FOUR_SORT",list.get(4).trim()                               );
					qc.setObject("SHAPE",list.get(5).trim()                                   );
					qc.setObject("PAY_TYPE_E",list.get(6).trim()                              );
					qc.setObject("PRI_ADD_PACT",list.get(7).trim()                            );
					qc.setObject("REMARK",list.get(8).trim()                                  );
					qc.setObject("RNUM",flag                                                  );
					qc.setObject("VERSION","0"                                                );
					qc.setObject("CREATOR", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
					qc.setObject("MODIFIER",(String) getUserVariable(FubonSystemVariableConsts.LOGINID));
					qc.setQueryString(sb.toString());
					dam.exeUpdate(qc);
					list.clear();
				}
			}	
			sendRtnObject(null);
		}catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			throw new APException("資料上傳失敗,錯誤發生在第"+flag+"筆,"+e.getMessage());
		}
	}
	
	
	/**
	 * 保險產品明細表查詢
	 * @param body
	 * @param header
	 * @throws APException
	 */
	public void queryInsureYe(Object body, IPrimitiveMap header) throws JBranchException
	{
		try
		{
			PMS716InputVO inputVO = (PMS716InputVO) body;
			PMS716OutputVO outputVO = new PMS716OutputVO();
			dam = this.getDataAccessManager();
			QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			sql.append("   SELECT 								              ");
			sql.append("          INSURE_ID,                                  ");
			sql.append("          PAY_TIME,                                   ");
			sql.append("          KY,                                         ");
			sql.append("          PAY_TYPE,                                   ");
			sql.append("          COIN_TYPE,                                  ");
			sql.append("          FOUR_SORT,                                  ");
			sql.append("          SHAPE,                                      ");
			sql.append("          PAY_TYPE_E,                                 ");
			sql.append("          PRI_ADD_PACT,                               ");
			sql.append("          REMARK,                                     ");
			sql.append("          UPDATE_DATE                                ");
			sql.append("  FROM TBPMS_KPI_NEW_ADD_INSURE_YE                    ");
			sql.append("  ORDER BY INSURE_ID                                  ");
			condition.setQueryString(sql.toString());
			ResultIF largeAgrList = dam.executePaging(condition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
			int totalPage_i = largeAgrList.getTotalPage(); // 分頁用
			int totalRecord_i = largeAgrList.getTotalRecord(); // 分頁用
			outputVO.setResultList(largeAgrList); // data
			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
			outputVO.setTotalPage(totalPage_i);// 總頁次
			outputVO.setTotalRecord(totalRecord_i);// 總筆數
			this.sendRtnObject(outputVO);
		}
		catch (Exception e)
		{
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			throw new APException(e.getMessage());
		}
	}
	
	/**
	 * 保險產品明細表 上傳
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	@SuppressWarnings("rawtypes")
	public void addInsureYe(Object body, IPrimitiveMap header) throws JBranchException
	{
		PMS716InputVO inputVO = (PMS716InputVO) body;
		PMS716OutputVO outputVO = new PMS716OutputVO();
		int flag = 0;
		try{
			List<String> list =  new ArrayList<String>();
			String joinedPath = new File((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), inputVO.getFileName()).toString();
			Workbook workbook = Workbook.getWorkbook(new File(joinedPath));
			Sheet sheet[] = workbook.getSheets();
			//有表頭.xls文檔
			//清空臨時表
			dam = this.getDataAccessManager();
			QueryConditionIF dcon = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);	
			String dsql = " TRUNCATE TABLE TBPMS_KPI_NEW_ADD_INSURE_YE_U ";
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
					if(list.size()!=10){
						throw new APException("上傳數據欄位個數不一致");
					}
					//SQL指令
					StringBuffer sb = new StringBuffer();
					dam = this.getDataAccessManager();
					QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sb.append("   INSERT INTO TBPMS_KPI_NEW_ADD_INSURE_YE_U (                ");
					sb.append("          INSURE_ID,                                          ");
					sb.append("          PAY_TIME,                                           ");
					sb.append("          KY,                                                 ");
					sb.append("          PAY_TYPE,                                           ");
					sb.append("          COIN_TYPE,                                          ");
					sb.append("          FOUR_SORT,                                          ");
					sb.append("          SHAPE,                                              ");
					sb.append("          PAY_TYPE_E,                                         ");
					sb.append("          PRI_ADD_PACT,                                       ");
					sb.append("          REMARK,                                             ");
					sb.append("          UPDATE_DATE,                                        ");
					sb.append("  		 RNUM,            					                 ");		
					sb.append("  		 VERSION,            						         ");
					sb.append("  		 CREATETIME,             					         ");
					sb.append("  		 CREATOR,             						         ");
					sb.append("  		 MODIFIER,         						             ");
					sb.append("  		 LASTUPDATE )             					         ");
					sb.append("  	VALUES(:INSURE_ID,            				             ");
					sb.append("  		:PAY_TIME,             					             ");
					sb.append("  		:KY,             					                 ");
					sb.append("  		:PAY_TYPE,             				                 ");
					sb.append("  		:COIN_TYPE,             				             ");
					sb.append("  		:FOUR_SORT,             					         ");
					sb.append("  		:SHAPE,             					             ");
					sb.append("  		:PAY_TYPE_E,             					         ");
					sb.append("  		:PRI_ADD_PACT,             					         ");
					sb.append("  		:REMARK,             					             ");
					sb.append("  		SYSDATE,             					             ");
					sb.append("  		:RNUM,             					                 ");
					sb.append("  		:VERSION,           					             ");
					sb.append("  		SYSDATE,           				                     ");
					sb.append("  		:CREATOR,            					             ");
					sb.append("  		:MODIFIER,         					                 ");
					sb.append("  		SYSDATE)          				                     ");
					qc.setObject("INSURE_ID",list.get(0).trim()                               );
					qc.setObject("PAY_TIME",list.get(1).trim()                                );
					qc.setObject("KY",list.get(2).trim()                                      );
					qc.setObject("PAY_TYPE",list.get(3).trim()                                );
					qc.setObject("COIN_TYPE",list.get(4).trim()                               );
					qc.setObject("FOUR_SORT",list.get(5).trim()                               );
					qc.setObject("SHAPE",list.get(6).trim()                                   );
					qc.setObject("PAY_TYPE_E",list.get(7).trim()                              );
					qc.setObject("PRI_ADD_PACT",list.get(8).trim()                            );
					qc.setObject("REMARK",list.get(9).trim()                                  );
					qc.setObject("RNUM",flag                                                  );
					qc.setObject("VERSION","0"                                                );
					qc.setObject("CREATOR", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
					qc.setObject("MODIFIER",(String) getUserVariable(FubonSystemVariableConsts.LOGINID));
					qc.setQueryString(sb.toString());
					dam.exeUpdate(qc);
					list.clear();
				}
			}	
			sendRtnObject(null);
		}catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			throw new APException("資料上傳失敗,錯誤發生在第"+flag+"筆,"+e.getMessage());
		}
	}
	
	
	
	/**
	 * 中小企業實績明細-專員查詢
	 * @param body
	 * @param header
	 * @throws APException
	 */
	public void queryMinEmp(Object body, IPrimitiveMap header) throws JBranchException
	{
		try
		{
			PMS716InputVO inputVO = (PMS716InputVO) body;
			PMS716OutputVO outputVO = new PMS716OutputVO();
			dam = this.getDataAccessManager();
			QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			sql.append("   SELECT YEARMON,									   ");
			sql.append("          CUST_NAME,                                   ");
			sql.append("          CUST_ID,                                     ");
			sql.append("          EMP_ID,                                      ");
			sql.append("          EMP_NAME,                                    ");
			sql.append("          BRANCH_NBR,                                  ");
			sql.append("          BRANCH_NAME,                                 ");
			sql.append("          FJ_UNIT,                                     ");
			sql.append("          ZJFSJ_NUM,                                   ");
			sql.append("          DATA_SOURCE                                  ");
			sql.append("  FROM TBPMS_KPI_NEW_ADD_MIN_EMP                       ");
			sql.append("  WHERE 1=1                                            ");
			if(StringUtils.isNotBlank(inputVO.getsTime())){
				sql.append("  AND YEARMON = :YEARMON                           ");
				condition.setObject("YEARMON", inputVO.getsTime());
			}
			sql.append("  ORDER BY YEARMON, BRANCH_NBR,CUST_ID                 ");
			condition.setQueryString(sql.toString());
			ResultIF largeAgrList = dam.executePaging(condition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
			int totalPage_i = largeAgrList.getTotalPage(); // 分頁用
			int totalRecord_i = largeAgrList.getTotalRecord(); // 分頁用
			outputVO.setResultList(largeAgrList); // data
			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
			outputVO.setTotalPage(totalPage_i);// 總頁次
			outputVO.setTotalRecord(totalRecord_i);// 總筆數
			this.sendRtnObject(outputVO);
		}
		catch (Exception e)
		{
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			throw new APException(e.getMessage());
		}
	}
	
	/**
	 * 中小企業實績明細-專員查詢 上傳
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	@SuppressWarnings("rawtypes")
	public void addMinEmp(Object body, IPrimitiveMap header) throws JBranchException
	{
		PMS716InputVO inputVO = (PMS716InputVO) body;
		PMS716OutputVO outputVO = new PMS716OutputVO();
		int flag = 0;
		try{
			List<String> list =  new ArrayList<String>();
			String joinedPath = new File((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), inputVO.getFileName()).toString();
			Workbook workbook = Workbook.getWorkbook(new File(joinedPath));
			Sheet sheet[] = workbook.getSheets();
			//有表頭.xls文檔
			//清空臨時表
			dam = this.getDataAccessManager();
			QueryConditionIF dcon = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);	
			String dsql = " TRUNCATE TABLE TBPMS_KPI_NEW_ADD_MIN_EMP_U ";
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
					if(list.size()!=7){
						throw new APException("上傳數據欄位個數不一致");
					}
					//SQL指令
					StringBuffer sb = new StringBuffer();
					dam = this.getDataAccessManager();
					QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sb.append("   INSERT INTO TBPMS_KPI_NEW_ADD_MIN_EMP_U (YEARMON,	         ");
					sb.append("          CUST_NAME,                                          ");
					sb.append("          CUST_ID,                                            ");
					sb.append("          EMP_ID,                                             ");
					sb.append("          FJ_UNIT,                                            ");
					sb.append("          ZJFSJ_NUM,                                          ");
					sb.append("          DATA_SOURCE,                                        ");
					sb.append("  		 RNUM,            					                 ");		
					sb.append("  		 VERSION,            						         ");
					sb.append("  		 CREATETIME,             					         ");
					sb.append("  		 CREATOR,             						         ");
					sb.append("  		 MODIFIER,         						             ");
					sb.append("  		 LASTUPDATE )             					         ");
					sb.append("  	VALUES(:YEARMON,            				             ");
					sb.append("  		:CUST_NAME,             					         ");
					sb.append("  		:CUST_ID,             				                 ");
					sb.append("  		:EMP_ID,             				                 ");
					sb.append("  		:FJ_UNIT,             					             ");
					sb.append("  		:ZJFSJ_NUM,             					         ");
					sb.append("  		:DATA_SOURCE,             					         ");
					sb.append("  		:RNUM,             					                 ");
					sb.append("  		:VERSION,           					             ");
					sb.append("  		SYSDATE,           				                     ");
					sb.append("  		:CREATOR,            					             ");
					sb.append("  		:MODIFIER,         					                 ");
					sb.append("  		SYSDATE)          				                     ");
					qc.setObject("YEARMON",list.get(0).trim()                                 );
					qc.setObject("CUST_NAME",list.get(1).trim()                               );
					qc.setObject("CUST_ID",list.get(2).trim()                                 );
					qc.setObject("EMP_ID",list.get(3).trim()                                  );
					qc.setObject("FJ_UNIT",list.get(4).trim()                                 );
					qc.setObject("ZJFSJ_NUM",list.get(5).trim()                               );
					qc.setObject("DATA_SOURCE",list.get(6).trim()                             );
					qc.setObject("RNUM",flag                                                  );
					qc.setObject("VERSION","0"                                                );
					qc.setObject("CREATOR", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
					qc.setObject("MODIFIER",(String) getUserVariable(FubonSystemVariableConsts.LOGINID));
					qc.setQueryString(sb.toString());
					dam.exeUpdate(qc);
					list.clear();
				}
			}	
			sendRtnObject(null);
		}catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			throw new APException("資料上傳失敗,錯誤發生在第"+flag+"筆,"+e.getMessage());
		}
	}
	/**
	 * 中小企業實績明細-分行查詢
	 * @param body
	 * @param header
	 * @throws APException
	 */
	public void queryMinBran(Object body, IPrimitiveMap header) throws JBranchException
	{
		try
		{
			PMS716InputVO inputVO = (PMS716InputVO) body;
			PMS716OutputVO outputVO = new PMS716OutputVO();
			dam = this.getDataAccessManager();
			QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			sql.append("   SELECT YEARMON,									   ");
			sql.append("          BRANCH_NBR,                                  ");
			sql.append("          BRANCH_NAME,                                 ");
			sql.append("          EFFE_NUM,                                    ");
			sql.append("          ZJFJ_NUM,                                    ");
			sql.append("          ZJSJ_NUM                                     ");
			sql.append("  FROM TBPMS_KPI_NEW_ADD_MIN_BRAN                      ");
			sql.append("  WHERE 1=1                                            ");
			if(StringUtils.isNotBlank(inputVO.getsTime())){
				sql.append("  AND YEARMON = :YEARMON                           ");
				condition.setObject("YEARMON", inputVO.getsTime());
			}
			sql.append("  ORDER BY YEARMON, BRANCH_NBR                         ");
			condition.setQueryString(sql.toString());
			ResultIF largeAgrList = dam.executePaging(condition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
			int totalPage_i = largeAgrList.getTotalPage(); // 分頁用
			int totalRecord_i = largeAgrList.getTotalRecord(); // 分頁用
			outputVO.setResultList(largeAgrList); // data
			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
			outputVO.setTotalPage(totalPage_i);// 總頁次
			outputVO.setTotalRecord(totalRecord_i);// 總筆數
			this.sendRtnObject(outputVO);
		}
		catch (Exception e)
		{
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			throw new APException(e.getMessage());
		}
	}
	
	/**
	 * 中小企業實績明細-分行 上傳
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	@SuppressWarnings("rawtypes")
	public void addMinBran(Object body, IPrimitiveMap header) throws JBranchException
	{
		PMS716InputVO inputVO = (PMS716InputVO) body;
		PMS716OutputVO outputVO = new PMS716OutputVO();
		int flag = 0;
		try{
			List<String> list =  new ArrayList<String>();
			String joinedPath = new File((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), inputVO.getFileName()).toString();
			Workbook workbook = Workbook.getWorkbook(new File(joinedPath));
			Sheet sheet[] = workbook.getSheets();
			//有表頭.xls文檔
			//清空臨時表
			dam = this.getDataAccessManager();
			QueryConditionIF dcon = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);	
			String dsql = " TRUNCATE TABLE TBPMS_KPI_NEW_ADD_MIN_BRAN_U ";
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
					if(list.size()!=5){
						throw new APException("上傳數據欄位個數不一致");
					}
					//SQL指令
					StringBuffer sb = new StringBuffer();
					dam = this.getDataAccessManager();
					QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sb.append("   INSERT INTO TBPMS_KPI_NEW_ADD_MIN_BRAN_U (YEARMON,	     ");
					sb.append("          BRANCH_NBR,                                         ");
					sb.append("          EFFE_NUM,                                           ");
					sb.append("          ZJFJ_NUM,                                           ");
					sb.append("          ZJSJ_NUM,                                           ");
					sb.append("  		 RNUM,            					                 ");		
					sb.append("  		 VERSION,            						         ");
					sb.append("  		 CREATETIME,             					         ");
					sb.append("  		 CREATOR,             						         ");
					sb.append("  		 MODIFIER,         						             ");
					sb.append("  		 LASTUPDATE )             					         ");
					sb.append("  	VALUES(:YEARMON,            				             ");
					sb.append("  		:BRANCH_NBR,             					         ");
					sb.append("  		:EFFE_NUM,             					             ");
					sb.append("  		:ZJFJ_NUM,             					             ");
					sb.append("  		:ZJSJ_NUM,             					             ");
					sb.append("  		:RNUM,             					                 ");
					sb.append("  		:VERSION,           					             ");
					sb.append("  		SYSDATE,           				                     ");
					sb.append("  		:CREATOR,            					             ");
					sb.append("  		:MODIFIER,         					                 ");
					sb.append("  		SYSDATE)          				                     ");
					qc.setObject("YEARMON",list.get(0).trim()                                 );
					qc.setObject("BRANCH_NBR", list.get(1).trim()                             );
					qc.setObject("EFFE_NUM",list.get(2).trim()                                );
					qc.setObject("ZJFJ_NUM",list.get(3).trim()                                );
					qc.setObject("ZJSJ_NUM",list.get(4).trim()                                );
					qc.setObject("RNUM",flag                                                  );
					qc.setObject("VERSION","0"                                                );
					qc.setObject("CREATOR", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
					qc.setObject("MODIFIER",(String) getUserVariable(FubonSystemVariableConsts.LOGINID));
					qc.setQueryString(sb.toString());
					dam.exeUpdate(qc);
					list.clear();
				}
			}	
			sendRtnObject(null);
		}catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			throw new APException("資料上傳失敗,錯誤發生在第"+flag+"筆,"+e.getMessage());
		}
	}
	/**
	 * 信用卡實績明細-專員查詢
	 * @param body
	 * @param header
	 * @throws APException
	 */
	public void queryDcEmp(Object body, IPrimitiveMap header) throws JBranchException
	{
		try
		{
			PMS716InputVO inputVO = (PMS716InputVO) body;
			PMS716OutputVO outputVO = new PMS716OutputVO();
			dam = this.getDataAccessManager();
			QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			sql.append("   SELECT YEARMON,									   ");
			sql.append("          BRANCH_NBR,                                  ");
			sql.append("          BRANCH_NAME,                                 ");
			sql.append("          PERSON_ID,                                   ");
			sql.append("          PERSON_NAME,                                 ");
			sql.append("          CARD_NUM                                     ");
			sql.append("  FROM TBPMS_KPI_NEW_ADD_DC_EMP                        ");
			sql.append("  WHERE 1=1                                            ");
			if(StringUtils.isNotBlank(inputVO.getsTime())){
				sql.append("  AND YEARMON = :YEARMON                           ");
				condition.setObject("YEARMON", inputVO.getsTime());
			}
			sql.append("  ORDER BY YEARMON, BRANCH_NBR                         ");
			condition.setQueryString(sql.toString());
			ResultIF largeAgrList = dam.executePaging(condition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
			int totalPage_i = largeAgrList.getTotalPage(); // 分頁用
			int totalRecord_i = largeAgrList.getTotalRecord(); // 分頁用
			outputVO.setResultList(largeAgrList); // data
			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
			outputVO.setTotalPage(totalPage_i);// 總頁次
			outputVO.setTotalRecord(totalRecord_i);// 總筆數
			this.sendRtnObject(outputVO);
		}
		catch (Exception e)
		{
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			throw new APException(e.getMessage());
		}
	}
	/**
	 * 信用卡實績明細-專員 上傳
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	@SuppressWarnings("rawtypes")
	public void addDcEmp(Object body, IPrimitiveMap header) throws JBranchException
	{
		PMS716InputVO inputVO = (PMS716InputVO) body;
		PMS716OutputVO outputVO = new PMS716OutputVO();
		int flag = 0;
		try{
			List<String> list =  new ArrayList<String>();
			String joinedPath = new File((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), inputVO.getFileName()).toString();
			Workbook workbook = Workbook.getWorkbook(new File(joinedPath));
			Sheet sheet[] = workbook.getSheets();
			//有表頭.xls文檔
			//清空臨時表
			dam = this.getDataAccessManager();
			QueryConditionIF dcon = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);	
			String dsql = " TRUNCATE TABLE TBPMS_KPI_NEW_ADD_DC_EMP_U ";
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
					if(list.size()!=3){
						throw new APException("上傳數據欄位個數不一致");
					}
					//SQL指令
					StringBuffer sb = new StringBuffer();
					dam = this.getDataAccessManager();
					QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sb.append("   INSERT INTO TBPMS_KPI_NEW_ADD_DC_EMP_U (YEARMON,	         ");
					sb.append("          PERSON_ID,                                          ");
					sb.append("          CARD_NUM,                                           ");
					sb.append("  		 RNUM,            					                 ");		
					sb.append("  		 VERSION,            						         ");
					sb.append("  		 CREATETIME,             					         ");
					sb.append("  		 CREATOR,             						         ");
					sb.append("  		 MODIFIER,         						             ");
					sb.append("  		 LASTUPDATE )             					         ");
					sb.append("  	VALUES(:YEARMON,            				             ");
					sb.append("  		:PERSON_ID,             					         ");
					sb.append("  		:CARD_NUM,             					             ");
					sb.append("  		:RNUM,             					                 ");
					sb.append("  		:VERSION,           					             ");
					sb.append("  		SYSDATE,           				                     ");
					sb.append("  		:CREATOR,            					             ");
					sb.append("  		:MODIFIER,         					                 ");
					sb.append("  		SYSDATE)          				                     ");
					qc.setObject("YEARMON",list.get(0).trim()                                 );
					qc.setObject("PERSON_ID",list.get(1).trim()                               );
					qc.setObject("CARD_NUM",list.get(2).trim()                                );
					qc.setObject("RNUM",flag                                                  );
					qc.setObject("VERSION","0"                                                );
					qc.setObject("CREATOR", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
					qc.setObject("MODIFIER",(String) getUserVariable(FubonSystemVariableConsts.LOGINID));
					qc.setQueryString(sb.toString());
					dam.exeUpdate(qc);
					list.clear();
				}
			}	
			sendRtnObject(null);
		}catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			throw new APException("資料上傳失敗,錯誤發生在第"+flag+"筆,"+e.getMessage());
		}
	}
	/**
	 * 信用卡實績明細-分行查詢
	 * @param body
	 * @param header
	 * @throws APException
	 */
	public void queryDcBran(Object body, IPrimitiveMap header) throws JBranchException
	{
		try
		{
			PMS716InputVO inputVO = (PMS716InputVO) body;
			PMS716OutputVO outputVO = new PMS716OutputVO();
			dam = this.getDataAccessManager();
			QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			sql.append("   SELECT YEARMON,									   ");
			sql.append("          BRANCH_NBR,                                  ");
			sql.append("          BRANCH_NAME,                                 ");
			sql.append("          CARD_NUM                                     ");
			sql.append("  FROM TBPMS_KPI_NEW_ADD_DC_BRAN                       ");
			sql.append("  WHERE 1=1                                            ");
			if(StringUtils.isNotBlank(inputVO.getsTime())){
				sql.append("  AND YEARMON = :YEARMON                           ");
				condition.setObject("YEARMON", inputVO.getsTime());
			}
			sql.append("  ORDER BY YEARMON, BRANCH_NBR                         ");
			condition.setQueryString(sql.toString());
			ResultIF largeAgrList = dam.executePaging(condition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
			int totalPage_i = largeAgrList.getTotalPage(); // 分頁用
			int totalRecord_i = largeAgrList.getTotalRecord(); // 分頁用
			outputVO.setResultList(largeAgrList); // data
			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
			outputVO.setTotalPage(totalPage_i);// 總頁次
			outputVO.setTotalRecord(totalRecord_i);// 總筆數
			this.sendRtnObject(outputVO);
		}
		catch (Exception e)
		{
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			throw new APException(e.getMessage());
		}
	}
	/**
	 * 信用卡實績明細-分行 上傳
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	@SuppressWarnings("rawtypes")
	public void addDcBran(Object body, IPrimitiveMap header) throws JBranchException
	{
		PMS716InputVO inputVO = (PMS716InputVO) body;
		PMS716OutputVO outputVO = new PMS716OutputVO();
		int flag = 0;
		try{
			List<String> list =  new ArrayList<String>();
			String joinedPath = new File((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), inputVO.getFileName()).toString();
			Workbook workbook = Workbook.getWorkbook(new File(joinedPath));
			Sheet sheet[] = workbook.getSheets();
			//有表頭.xls文檔
			//清空臨時表
			dam = this.getDataAccessManager();
			QueryConditionIF dcon = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);	
			String dsql = " TRUNCATE TABLE TBPMS_KPI_NEW_ADD_DC_BRAN_U ";
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
					if(list.size()!=3){
						throw new APException("上傳數據欄位個數不一致");
					}
					//SQL指令
					StringBuffer sb = new StringBuffer();
					dam = this.getDataAccessManager();
					QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sb.append("   INSERT INTO TBPMS_KPI_NEW_ADD_DC_BRAN_U (YEARMON,	         ");
					sb.append("          BRANCH_NBR,                                         ");
					sb.append("          CARD_NUM,                                           ");
					sb.append("  		 RNUM,            					                 ");		
					sb.append("  		 VERSION,            						         ");
					sb.append("  		 CREATETIME,             					         ");
					sb.append("  		 CREATOR,             						         ");
					sb.append("  		 MODIFIER,         						             ");
					sb.append("  		 LASTUPDATE )             					         ");
					sb.append("  	VALUES(:YEARMON,            				             ");
					sb.append("  		:BRANCH_NBR,             					         ");
					sb.append("  		:CARD_NUM,             					             ");
					sb.append("  		:RNUM,             					                 ");
					sb.append("  		:VERSION,           					             ");
					sb.append("  		SYSDATE,           				                     ");
					sb.append("  		:CREATOR,            					             ");
					sb.append("  		:MODIFIER,         					                 ");
					sb.append("  		SYSDATE)          				                     ");
					qc.setObject("YEARMON",list.get(0).trim()                                 );
					qc.setObject("BRANCH_NBR", list.get(1).trim()                             );
					qc.setObject("CARD_NUM",list.get(2).trim()                                );
					qc.setObject("RNUM",flag                                                  );
					qc.setObject("VERSION","0"                                                );
					qc.setObject("CREATOR", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
					qc.setObject("MODIFIER",(String) getUserVariable(FubonSystemVariableConsts.LOGINID));
					qc.setQueryString(sb.toString());
					dam.exeUpdate(qc);
					list.clear();
				}
			}	
			sendRtnObject(null);
		}catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			throw new APException("資料上傳失敗,錯誤發生在第"+flag+"筆,"+e.getMessage());
		}
	}
	/**
	 * 其他加扣分 查詢
	 * @param body
	 * @param header
	 * @throws APException
	 */
	public void queryRests(Object body, IPrimitiveMap header) throws JBranchException
	{
		try
		{
			PMS716InputVO inputVO = (PMS716InputVO) body;
			PMS716OutputVO outputVO = new PMS716OutputVO();
			dam = this.getDataAccessManager();
			QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			sql.append("   SELECT YEARMON,									   ");
			sql.append("          BRANCH_NBR,                                  ");
			sql.append("          BRANCH_NAME,                                 ");
			sql.append("          PERSON_ID,                                   ");
			sql.append("          PERSON_NAME,                                 ");
			sql.append("          ADD_DED                                      ");
			sql.append("  FROM TBPMS_KPI_NEW_ADD_RESTS                         ");
			sql.append("  WHERE 1=1                                            ");
			if(StringUtils.isNotBlank(inputVO.getsTime())){
				sql.append("  AND YEARMON = :YEARMON                           ");
				condition.setObject("YEARMON", inputVO.getsTime());
			}
			sql.append("  ORDER BY YEARMON, BRANCH_NBR                         ");
			condition.setQueryString(sql.toString());
			ResultIF largeAgrList = dam.executePaging(condition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
			int totalPage_i = largeAgrList.getTotalPage(); // 分頁用
			int totalRecord_i = largeAgrList.getTotalRecord(); // 分頁用
			outputVO.setResultList(largeAgrList); // data
			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
			outputVO.setTotalPage(totalPage_i);// 總頁次
			outputVO.setTotalRecord(totalRecord_i);// 總筆數
			this.sendRtnObject(outputVO);
		}
		catch (Exception e)
		{
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			throw new APException(e.getMessage());
		}
	}
	/**
	 * 其他加扣分 上傳
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	@SuppressWarnings("rawtypes")
	public void addRests(Object body, IPrimitiveMap header) throws JBranchException
	{
		PMS716InputVO inputVO = (PMS716InputVO) body;
		PMS716OutputVO outputVO = new PMS716OutputVO();
		int flag = 0;
		try{
			List<String> list =  new ArrayList<String>();
			String joinedPath = new File((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), inputVO.getFileName()).toString();
			Workbook workbook = Workbook.getWorkbook(new File(joinedPath));
			Sheet sheet[] = workbook.getSheets();
			//有表頭.xls文檔
			//清空臨時表
			dam = this.getDataAccessManager();
			QueryConditionIF dcon = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);	
			String dsql = " TRUNCATE TABLE TBPMS_KPI_NEW_ADD_RESTS_U ";
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
					if(list.size()!=3){
						throw new APException("上傳數據欄位個數不一致");
					}
					//SQL指令
					StringBuffer sb = new StringBuffer();
					dam = this.getDataAccessManager();
					QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sb.append("   INSERT INTO TBPMS_KPI_NEW_ADD_RESTS_U (YEARMON,	         ");
					sb.append("          PERSON_ID,                                          ");
					sb.append("          ADD_DED,                                            ");
					sb.append("  		 RNUM,            					                 ");		
					sb.append("  		 VERSION,            						         ");
					sb.append("  		 CREATETIME,             					         ");
					sb.append("  		 CREATOR,             						         ");
					sb.append("  		 MODIFIER,         						             ");
					sb.append("  		 LASTUPDATE )             					         ");
					sb.append("  	VALUES(:YEARMON,            				             ");
					sb.append("  		:PERSON_ID,             					         ");
					sb.append("  		:ADD_DED,             					             ");
					sb.append("  		:RNUM,             					                 ");
					sb.append("  		:VERSION,           					             ");
					sb.append("  		SYSDATE,           				                     ");
					sb.append("  		:CREATOR,            					             ");
					sb.append("  		:MODIFIER,         					                 ");
					sb.append("  		SYSDATE)          				                     ");
					qc.setObject("YEARMON",list.get(0).trim()                                 );
					qc.setObject("PERSON_ID",list.get(1).trim()                               );
					qc.setObject("ADD_DED",list.get(2).trim()                                 );
					qc.setObject("RNUM",flag                                                  );
					qc.setObject("VERSION","0"                                                );
					qc.setObject("CREATOR", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
					qc.setObject("MODIFIER",(String) getUserVariable(FubonSystemVariableConsts.LOGINID));
					qc.setQueryString(sb.toString());
					dam.exeUpdate(qc);
					list.clear();
				}
			}	
			sendRtnObject(null);
		}catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			throw new APException("資料上傳失敗,錯誤發生在第"+flag+"筆,"+e.getMessage());
		}
	}
	// 修改主表中为已设定
	public void changeMastSet(String column,String yearMon) throws JBranchException
	{
			dam = this.getDataAccessManager();
			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
			sb.append("		UPDATE TBPMS_KPI_NEW_ADD_KPI T 					");
			sb.append("		SET T."+column+" = '1',                         ");
			sb.append("			T.VERSION = 1,                              ");
			sb.append("         MODIFIER = :MODIFIER,                       ");
			sb.append("         LASTUPDATE = SYSDATE                        ");
			sb.append("		WHERE T.YEARMON = :YEARMON 					    ");
			qc.setObject("YEARMON", yearMon);
			qc.setObject("MODIFIER", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
			qc.setQueryString(sb.toString());
			dam.exeUpdate(qc);
	}
	/**
	 * 調用存儲過程,參數不同，執行的TRY,CATCH不同
	 * @param body
	 * @param header
	 * @throws JBranchException 
	 * @throws DAOException 
	 */
	@SuppressWarnings({ "unused", "rawtypes" })
	public void callStored(Object body, IPrimitiveMap header) throws DAOException, JBranchException
	{
		PMS716InputVO inputVO = (PMS716InputVO) body;
		PMS716OutputVO outputVO = new PMS716OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		//執行存儲過程
		if(inputVO.getStoredFlag().equals("addInsureDetail")){
			try
			{
				sb.append(" CALL PABTH_BTPMS727.SP_TBPMS_KPI_NEW_ADD_INSU_DET(? ) ");
				qc.registerOutParameter(1, Types.VARCHAR);
				qc.setQueryString(sb.toString());
				Map<Integer, Object> resultMap = dam.executeCallable(qc);
				String str = (String) resultMap.get(1);
				String[] strs = null;
				if(str!=null){
					strs = str.split("；");
					if(strs!=null&&strs.length>5){
						str = strs[0]+"；"+strs[1]+"；"+strs[2]+"；"+strs[3]+"；"+strs[4]+"...等";
					}
				}else{
					//文檔上傳成功
					changeMastSet("DC_BRANCH",inputVO.getsTime());
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
		if(inputVO.getStoredFlag().equals("addInsureYe")){
			try
			{
				sb.append(" CALL PABTH_BTPMS727.SP_TBPMS_KPI_NEW_ADD_INSU_YE(? ) ");
				qc.registerOutParameter(1, Types.VARCHAR);
				qc.setQueryString(sb.toString());
				Map<Integer, Object> resultMap = dam.executeCallable(qc);
				String str = (String) resultMap.get(1);
				String[] strs = null;
				if(str!=null){
					strs = str.split("；");
					if(strs!=null&&strs.length>5){
						str = strs[0]+"；"+strs[1]+"；"+strs[2]+"；"+strs[3]+"；"+strs[4]+"...等";
					}
				}else{
					//文檔上傳成功
					changeMastSet("DC_BRANCH",inputVO.getsTime());
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
		if(inputVO.getStoredFlag().equals("addMinEmp")){
			try
			{
				sb.append(" CALL PABTH_BTPMS727.SP_TBPMS_KPI_NEW_ADD_MIN_EMP(?,? ) ");
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
				}else{
					//文檔上傳成功
					changeMastSet("MINOR_ENT_EMP",inputVO.getsTime());
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
		//執行存儲過程
		if(inputVO.getStoredFlag().equals("addMinBran")){
			try
			{
				sb.append(" CALL PABTH_BTPMS727.SP_TBPMS_KPI_NEW_ADD_MIN_BRAN(?,? ) ");
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
				}else{
					//文檔上傳成功
					changeMastSet("MINOR_ENT_BRANCH",inputVO.getsTime());
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
		if(inputVO.getStoredFlag().equals("addDcEmp")){
			try
			{
				sb.append(" CALL PABTH_BTPMS727.SP_TBPMS_KPI_NEW_ADD_DC_EMP(?,? ) ");
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
				}else{
					//文檔上傳成功
					changeMastSet("DC_EMP",inputVO.getsTime());
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
		if(inputVO.getStoredFlag().equals("addDcBran")){
			try
			{
				sb.append(" CALL PABTH_BTPMS727.SP_TBPMS_KPI_NEW_ADD_DC_BRAN(?,? ) ");
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
				}else{
					//文檔上傳成功
					changeMastSet("DC_BRANCH",inputVO.getsTime());
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
		if(inputVO.getStoredFlag().equals("addRests")){
			try
			{
				sb.append(" CALL PABTH_BTPMS727.SP_TBPMS_KPI_NEW_ADD_RESTS(?,? ) ");
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
				}else{
					//文檔上傳成功
					changeMastSet("RESTS",inputVO.getsTime());
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
	}
	
	/** 下載範例檔 **/
	public void downloadSample(Object body, IPrimitiveMap header) throws Exception {
		PMS716InputVO inputVO = (PMS716InputVO) body;
		switch (inputVO.getDownloadSample().toString())
		{
		case "dcBran":
			notifyClientToDownloadFile("doc"+File.separator+"PMS"+File.separator+"PMS716_dcBran.xls", "信用卡實績明細-分行_上傳範例.xls");
			break;
		case "dcEmp":
			notifyClientToDownloadFile("doc"+File.separator+"PMS"+File.separator+"PMS716_dcEmp.xls", "信用卡實績明細-專員_上傳範例.xls");
			break;
		case "insureDetail":
			notifyClientToDownloadFile("doc"+File.separator+"PMS"+File.separator+"PMS716_insureDetail.xls", "保險產品明細表_上傳範例.xls");
			break;
		case "insureYe":
			notifyClientToDownloadFile("doc"+File.separator+"PMS"+File.separator+"PMS716_insureYe.xls", "保險產品業況表_上傳範例.xls");
			break;
		case "minBran":
			notifyClientToDownloadFile("doc"+File.separator+"PMS"+File.separator+"PMS716_minBran.xls", "中小企業實績明細-分行_上傳範例.xls");
			break;
		case "minEmp":
			notifyClientToDownloadFile("doc"+File.separator+"PMS"+File.separator+"PMS716_minEmp.xls", "中小企業實績明細-專員_上傳範例.xls");
			break;
		case "rests":
			notifyClientToDownloadFile("doc"+File.separator+"PMS"+File.separator+"PMS716_rests.xls", "其他加扣分_上傳範例.xls");
			break;
		}
//		return change_name;
//		inputVO.getDownloadSample().toString().equals("")
//		dcBran
//		dcEmp
//		insureDetail
//		insureYe
//		minBran
//		minEmp
//		rests
		
		
	    this.sendRtnObject(null);
	}
}