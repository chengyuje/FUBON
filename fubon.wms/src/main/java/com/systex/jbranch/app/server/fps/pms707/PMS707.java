package com.systex.jbranch.app.server.fps.pms707;


import java.io.File;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jxl.Sheet;
import jxl.Workbook;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.pms707.PMS707InputVO;
import com.systex.jbranch.app.server.fps.pms707.PMS707OutputVO;
import com.systex.jbranch.app.server.fps.pms709.PMS709InputVO;
import com.systex.jbranch.app.server.fps.pms709.PMS709OutputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * 
 * Copy Right Information : Forms Syntron <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : <br>
 * Comments Name : pms707InputVO.java<br>
 * Author :CTY<br>
 * Date :2016年12月22日 <br>
 * Version : 1.01 <br>
 * Editor : CTY<br>
 * Editor Date : 2016年11月14日<br>
 */
@Component("pms707")
@Scope("request")
public class PMS707 extends FubonWmsBizLogic
{
	public DataAccessManager dam = null;
	@SuppressWarnings("unused")
	private Logger logger = LoggerFactory.getLogger(PMS707.class);
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
		PMS707InputVO inputVO = (PMS707InputVO) body;
		PMS707OutputVO outputVO = new PMS707OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sb = new StringBuffer();
		sb.append("  SELECT PAR_LIMIT,                   ");
		sb.append("         PAR_RATE,                    ");
		sb.append("         PAR_BOUNTY,                  ");
		sb.append("         PAR_PROJ,                    ");
		sb.append("         PAR_BONUS_ADJ,               ");
		sb.append("         PAR_PS_LIST,                 ");
		sb.append("         YEARMON                      ");
		sb.append("  FROM   TBPMS_LOAN_BONUS_MAST        ");
		sb.append("    WHERE YEARMON = :YEARMON"          );
		qc.setObject("YEARMON", inputVO.getYearMon()      );
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
	 * 儲存主頁面的修改（主表）
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	@SuppressWarnings("rawtypes")
	public void saveChange(Object body, IPrimitiveMap header) throws JBranchException
	{
		try
		{
			PMS707InputVO inputVO = (PMS707InputVO) body;
			PMS707OutputVO outputVO = new PMS707OutputVO();
			dam = this.getDataAccessManager();
			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

			StringBuffer sb = new StringBuffer();
			sb.append("  UPDATE TBPMS_LOAN_BONUS_MAST SET          ");
			sb.append("         PAR_LIMIT = :PAR_LIMIT,            ");
			sb.append("         PAR_RATE = :PAR_RATE,              ");
			sb.append("         PAR_BOUNTY = :PAR_BOUNTY,          ");
			sb.append("         PAR_PROJ = :PAR_PROJ,              ");
			sb.append("         PAR_BONUS_ADJ = :PAR_BONUS_ADJ,    ");
			sb.append("         PAR_PS_LIST = :PAR_PS_LIST,        ");
			sb.append("         VERSION = 1,                       ");
			sb.append("         MODIFIER = :MODIFIER,              ");
			sb.append("         LASTUPDATE = SYSDATE               ");
			sb.append("    WHERE YEARMON = :YEARMON                ");
			qc.setObject("PAR_LIMIT", inputVO.getPAR_LIMIT()        );
			qc.setObject("PAR_RATE", inputVO.getPAR_RATE()          );
			qc.setObject("PAR_BOUNTY", inputVO.getPAR_BOUNTY()      );
			qc.setObject("PAR_PROJ", inputVO.getPAR_PROJ()    	    );
			qc.setObject("PAR_BONUS_ADJ", inputVO.getPAR_BONUS_ADJ());
			qc.setObject("PAR_PS_LIST", inputVO.getPAR_PS_LIST()    );
			qc.setObject("MODIFIER", inputVO.getUserId()            );
			qc.setObject("YEARMON", inputVO.getYearMon()            );
			qc.setQueryString(sb.toString()                         );
			int result = dam.exeUpdate(qc);
			outputVO.setBackResult(result);
			sendRtnObject(outputVO);
			
		}
		catch (Exception e)
		{
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			throw new APException(e.getMessage());
		}
	}
	
	/**
	 * 查詢參數4撥款額度定額獎金表
	 * @param body
	 * @param header
	 * @throws APException
	 */
	public void queryLimitReward(Object body, IPrimitiveMap header) throws JBranchException
	{
		try
		{
			PMS707InputVO inputVO = (PMS707InputVO) body;
			PMS707OutputVO outputVO = new PMS707OutputVO();
			dam = this.getDataAccessManager();
			QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			sql.append("  SELECT YEARMON,                               ");
			sql.append("         CASE_SOURCE,                           ");
			sql.append("         PROD_TYPE_1,                           ");
			sql.append("         PROD_TYPE_2,                           ");
			sql.append("         BEGIN_MON,                             ");
			sql.append("         END_MON,                               ");
			sql.append("         BONUS,                                 ");
			sql.append("         BONUS_DIS                              ");
			sql.append("  FROM TBPMS_LOAN_BONUS_PARA                    ");
			sql.append("  WHERE 1=1                                     ");
			if(StringUtils.isNotBlank(inputVO.getYearMon())){
				sql.append("  AND YEARMON = :YEARMON                    ");
				condition.setObject("YEARMON", inputVO.getYearMon());
			}
			condition.setQueryString(sql.toString());
			List<Map<String, Object>> result = dam.exeQuery(condition);
			outputVO.setResultList(result);
			sendRtnObject(outputVO);
		}
		catch (Exception e)
		{
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			throw new APException(e.getMessage());
		}
	
	}
	
	//參數4維護數據
		@SuppressWarnings({ "rawtypes", "unused" })
		public void addLimitReward(Object body, IPrimitiveMap header) throws APException
		{
			int flag = 0;
			try {
				PMS707InputVO inputVO = (PMS707InputVO) body;
				PMS707OutputVO outputVO = new PMS707OutputVO();
				//刪除當月已存在數據
				dam = this.getDataAccessManager();
				QueryConditionIF dqc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);	
				StringBuffer dsb = new StringBuffer();
				dsb.append("  DELETE TBPMS_LOAN_BONUS_PARA");
				dsb.append("  WHERE YEARMON = :YEARMON");
				dqc.setObject("YEARMON", inputVO.getYearMon());
				dqc.setQueryString(dsb.toString());
				dam.exeUpdate(dqc);
				
				//SQL指令
				StringBuffer sb = new StringBuffer();
				dam = this.getDataAccessManager();
				QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb.append("   INSERT INTO TBPMS_LOAN_BONUS_PARA (YEARMON,	     ");
				sb.append("  		CASE_SOURCE,           					     ");
				sb.append("  		PROD_TYPE_1,            				     ");
				sb.append("  		PROD_TYPE_2,            				     ");
				sb.append("  		BEGIN_MON,            					     ");
				sb.append("  		END_MON,            					     ");
				sb.append("  		BONUS,            					         ");
				sb.append("  		BONUS_DIS,            					     ");
				sb.append("  		VERSION,            						 ");
				sb.append("  		CREATETIME,             					 ");
				sb.append("  		CREATOR,             						 ");
				sb.append("  		MODIFIER,         						     ");
				sb.append("  		LASTUPDATE )             					 ");
				sb.append("  	VALUES(:YEARMON,            				     ");
				sb.append("  		:CASE_SOURCE,             					 ");
				sb.append("  		:PROD_TYPE_1,             				     ");
				sb.append("  		:PROD_TYPE_2,             				     ");
				sb.append("  		:BEGIN_MON,             					 ");
				sb.append("  		:END_MON,             					     ");
				sb.append("  		:BONUS,             					     ");
				sb.append("  		:BONUS_DIS,             					 ");
				sb.append("  		:VERSION,           					     ");
				sb.append("  		SYSDATE,           				             ");
				sb.append("  		:CREATOR,            					     ");
				sb.append("  		:MODIFIER,         					         ");
				sb.append("  		SYSDATE)          				             ");
				for (int i = 0; i < inputVO.getInputList().size(); i++)
				{
					qc.setObject("YEARMON", inputVO.getYearMon());
					qc.setObject("CASE_SOURCE", inputVO.getInputList().get(i).get("CASE_SOURCE"));
					qc.setObject("PROD_TYPE_1", inputVO.getInputList().get(i).get("PROD_TYPE_1"));
					qc.setObject("PROD_TYPE_2", inputVO.getInputList().get(i).get("PROD_TYPE_2"));
					qc.setObject("BEGIN_MON", inputVO.getInputList().get(i).get("BEGIN_MON"));
					qc.setObject("END_MON", inputVO.getInputList().get(i).get("END_MON"));
					qc.setObject("BONUS", inputVO.getInputList().get(i).get("BONUS"));
					qc.setObject("BONUS_DIS", inputVO.getInputList().get(i).get("BONUS_DIS"));
					qc.setObject("VERSION","0"                                        );
					qc.setObject("CREATOR", inputVO.getUserId()                       );
					qc.setObject("MODIFIER", inputVO.getUserId()                      );
					qc.setQueryString(sb.toString());
					dam.exeUpdate(qc);
				}
				//更新成功
				changeMastSet("PAR_LIMIT",inputVO.getYearMon(),inputVO.getUserId());
				this.sendRtnObject(outputVO);
			} catch (Exception e) {
				logger.error("保存數據失敗");
				throw new APException("保存數據失敗,"+e.getMessage());
			}
		}
	
	/**
	 * 查詢參數2
	 * @param body
	 * @param header
	 * @throws APException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void queryDeRate(Object body, IPrimitiveMap header) throws APException
	{
		try
		{
			PMS707InputVO inputVO = (PMS707InputVO) body;
			PMS707OutputVO outputVO = new PMS707OutputVO();
			dam = this.getDataAccessManager();
	
			QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
	
			StringBuffer sql = new StringBuffer();
			
			sql.append("SELECT YEARMON,                            ");
			sql.append("    REQUE_DATE,   CASE_ID ,                ");
			sql.append("    REGION_CENTER_CODE,                    ");
			sql.append("    PS_CUST_ID, IN_BRANCH_ID ,             ");
			sql.append("    BORR_ID                                ");
			sql.append("FROM TBPMS_LOAN_BONUS_DERATE               ");
			sql.append("WHERE TRIM(YEARMON) = :YEARMON             ");
			condition.setObject("YEARMON",inputVO.getYearMon().trim());
			condition.setQueryString(sql.toString());
			ResultIF largeAgrList = dam.executePaging(condition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
			int totalPage_i = largeAgrList.getTotalPage(); // 分頁用
			int totalRecord_i = largeAgrList.getTotalRecord(); // 分頁用
			outputVO.setResultList(largeAgrList); // data
			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
			outputVO.setTotalPage(totalPage_i);// 總頁次
			outputVO.setTotalRecord(totalRecord_i);// 總筆數
			this.sendRtnObject(outputVO);
			/*List<Map<String,Object>> resultList = dam.exeQuery(condition);
			outputVO.setResultList(resultList); // data
			this.sendRtnObject(outputVO);*/
		}
		catch (Exception e)
		{
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			throw new APException(e.getMessage());
		}
	}
	/**
	 * 參數2上传功能
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	@SuppressWarnings("rawtypes")
	public void uploadDeRate(Object body, IPrimitiveMap header) throws JBranchException
	{
		PMS707InputVO inputVO = (PMS707InputVO) body;
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
			String dsql = " TRUNCATE TABLE TBPMS_LOAN_BONUS_DERATE_U ";
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
					sb.append("   INSERT INTO TBPMS_LOAN_BONUS_DERATE_U (YEARMON,	 ");
					sb.append("  		REQUE_DATE,            				         ");
					sb.append("  		CASE_ID,           					         ");
					sb.append("  		REGION_CENTER_CODE,            				 ");
					sb.append("  		PS_CUST_ID,            					     ");
					sb.append("  		IN_BRANCH_ID,            					 ");
					sb.append("  		BORR_ID,            					     ");
					sb.append("  		RNUM,            					         ");
					sb.append("  		VERSION,            						 ");
					sb.append("  		CREATETIME,             					 ");
					sb.append("  		CREATOR,             						 ");
					sb.append("  		MODIFIER,         						     ");
					sb.append("  		LASTUPDATE )             					 ");
					sb.append("  	VALUES(:YEARMON,            				     ");
					sb.append("  		:REQUE_DATE,             				     ");
					sb.append("  		:CASE_ID,             					     ");
					sb.append("  		:REGION_CENTER_CODE,             	         ");
					sb.append("  		:PS_CUST_ID,             					 ");
					sb.append("  		:IN_BRANCH_ID,             					 ");
					sb.append("  		:BORR_ID,             					     ");
					sb.append("  		:RNUM,             					         ");
					sb.append("  		:VERSION,           					     ");
					sb.append("  		SYSDATE,           				             ");
					sb.append("  		:CREATOR,            					     ");
					sb.append("  		:MODIFIER,         					         ");
					sb.append("  		SYSDATE)          				             ");
					qc.setObject("YEARMON",inputVO.getYearMon().trim()                );
					qc.setObject("REQUE_DATE",list.get(0).trim()                      );
					qc.setObject("CASE_ID",list.get(1).trim()                         );
					qc.setObject("REGION_CENTER_CODE",list.get(2).trim()              );
					qc.setObject("PS_CUST_ID",list.get(3).trim()                      );
					qc.setObject("IN_BRANCH_ID",list.get(4).trim()                    );
					qc.setObject("BORR_ID",list.get(5).trim()                         );
					qc.setObject("RNUM",flag                                          );
					qc.setObject("VERSION","0"                                        );
					qc.setObject("CREATOR", inputVO.getUserId()                       );
					qc.setObject("MODIFIER", inputVO.getUserId()                      );
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
	 * 參數3查詢扣減獎金之非財務指標及違失情事扣減數明細
	 * @param body
	 * @param header
	 * @throws APException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void queryDedReward(Object body, IPrimitiveMap header) throws JBranchException
	{
		try
		{
			PMS707InputVO inputVO = (PMS707InputVO) body;
			PMS707OutputVO outputVO = new PMS707OutputVO();
			dam = this.getDataAccessManager();
			QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			sql.append("  SELECT YEARMON,                                 ");
			sql.append("         OCCUR_YM,                                ");
			sql.append("         BRANCH_NBR,                              ");
			sql.append("         BRANCH_NAME,                             ");
			sql.append("         PS_EMP_ID,                               ");
			sql.append("         PS_EMP_NAME,                             ");
			sql.append("         WRONG_TYPE,                              ");
			sql.append("         WRONG_DETAIL_1,                          ");
			sql.append("         WRONG_DETAIL_2,                          ");
			sql.append("         DED_BONUS,                               ");
			sql.append("         DED_FLAG,                                ");
			sql.append("         DED_YM,                                  ");
			sql.append("         NO_DED                                   ");
			sql.append("  FROM TBPMS_LOAN_BOUNTY_DETAIL                   ");
			if(StringUtils.isNotBlank(inputVO.getYearMon())){
				sql.append("  WHERE YEARMON = :YEARMON                    ");
				condition.setObject("YEARMON", inputVO.getYearMon());
			}
			condition.setQueryString(sql.toString());
			ResultIF largeAgrList = dam.executePaging(condition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
			int totalPage_i = largeAgrList.getTotalPage(); // 分頁用
			int totalRecord_i = largeAgrList.getTotalRecord(); // 分頁用
			outputVO.setResultList(largeAgrList); // data
			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
			outputVO.setTotalPage(totalPage_i);// 總頁次
			outputVO.setTotalRecord(totalRecord_i);// 總筆數
			this.sendRtnObject(outputVO);
			/*List<Map<String, Object>> result = dam.exeQuery(condition);
			outputVO.setResultList(result);
			sendRtnObject(outputVO);*/
		}
		catch (Exception e)
		{
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			throw new APException(e.getMessage());
		}
	
	}
	
	//參數3從excel表格中新增數據
		@SuppressWarnings({ "rawtypes", "unused" })
		public void addDedReward(Object body, IPrimitiveMap header) throws APException
		{
			int flag = 0;
			try {
				PMS707InputVO inputVO = (PMS707InputVO) body;
				PMS707OutputVO outputVO = new PMS707OutputVO();
				//清空臨時表
				dam = this.getDataAccessManager();
				QueryConditionIF dcon = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);	
				String dsql = " TRUNCATE TABLE TBPMS_LOAN_BOUNTY_DETAIL_U ";
				dcon.setQueryString(dsql.toString());
				dam.exeUpdate(dcon);
				List<String> import_file = new ArrayList<String>();
				List<String> list =  new ArrayList<String>();
				String joinedPath = new File((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), inputVO.getFileName()).toString();
				Workbook workbook = Workbook.getWorkbook(new File(joinedPath));
				Sheet sheet[] = workbook.getSheets();
				//有表頭.xls文檔
				String lab = null;
				for(int a=0;a<sheet.length;a++){
					for(int i=1;i<sheet[a].getRows();i++){
						for(int j=0;j<sheet[a].getColumns();j++){
							lab = sheet[a].getCell(j, i).getContents();
							list.add(lab);
						}
						
						//excel表格記行數
						flag++;
						
						/*//判斷當前上傳數據月份是否一致
						if(!list.get(0).equals(inputVO.getYearMon())){
							throw new APException("上傳數據選擇月份不一致");
						}
						*/
						//判斷當前上傳數據欄位個數是否一致
						if(list.size()!=8){
							throw new APException("上傳數據欄位個數不一致");
						}
						
						//SQL指令
						StringBuffer sb = new StringBuffer();
						dam = this.getDataAccessManager();
						QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
						sb.append("   INSERT INTO TBPMS_LOAN_BOUNTY_DETAIL_U (YEARMON,	 ");
						sb.append("  		OCCUR_YM,            				         ");
						sb.append("  		BRANCH_NBR,           			             ");
						sb.append("  		PS_EMP_ID,            					     ");
						sb.append("  		WRONG_TYPE,            					     ");
						sb.append("  		WRONG_DETAIL_1,            					 ");
						sb.append("  		WRONG_DETAIL_2,            					 ");
						sb.append("  		DED_BONUS,            					     ");
						sb.append("  		RNUM,            					         ");
						sb.append("  		VERSION,            						 ");
						sb.append("  		CREATETIME,             					 ");
						sb.append("  		CREATOR,             						 ");
						sb.append("  		MODIFIER,         						     ");
						sb.append("  		LASTUPDATE )             					 ");
						sb.append("  	VALUES(:YEARMON,            				     ");
						sb.append("  		:OCCUR_YM,             				         ");
						sb.append("  		:BRANCH_NBR,             				     ");
						sb.append("  		:PS_EMP_ID,             					 ");
						sb.append("  		:WRONG_TYPE,             					 ");
						sb.append("  		:WRONG_DETAIL_1,             			     ");
						sb.append("  		:WRONG_DETAIL_2,             		         ");
						sb.append("  		:DED_BONUS,             					 ");
						sb.append("  		:RNUM,             					         ");
						sb.append("  		:VERSION,           					     ");
						sb.append("  		SYSDATE,           				             ");
						sb.append("  		:CREATOR,            					     ");
						sb.append("  		:MODIFIER,         					         ");
						sb.append("  		SYSDATE)          				             ");
						qc.setObject("YEARMON",list.get(0).trim()                         );
						qc.setObject("OCCUR_YM",list.get(1).trim()                        );
						qc.setObject("BRANCH_NBR",list.get(2).trim()                      );
						qc.setObject("PS_EMP_ID",list.get(3).trim()                       );
						qc.setObject("WRONG_TYPE",list.get(4).trim()                      );
						qc.setObject("WRONG_DETAIL_1",list.get(5).trim()                  );
						qc.setObject("WRONG_DETAIL_2",list.get(6).trim()                  );
						qc.setObject("DED_BONUS",list.get(7).trim()                       );
						qc.setObject("RNUM",flag                                          );
						qc.setObject("VERSION","0"                                        );
						qc.setObject("CREATOR", inputVO.getUserId()                       );
						qc.setObject("MODIFIER", inputVO.getUserId()                      );
						qc.setQueryString(sb.toString());
						dam.exeUpdate(qc);
						list.clear();
					}
				}		
				outputVO.setFlag(flag);
				this.sendRtnObject(outputVO);
			} catch (Exception e) {
				logger.error("文檔上傳失敗");
				throw new APException("資料上傳失敗,錯誤發生在第"+flag+"筆,"+e.getMessage());
			}
		}
		
		/**
		 * 參數2查詢房信貸及留貸專案別
		 * @param body
		 * @param header
		 * @throws APException
		 */
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public void queryParProj(Object body, IPrimitiveMap header) throws JBranchException
		{
			try
			{
				PMS707InputVO inputVO = (PMS707InputVO) body;
				PMS707OutputVO outputVO = new PMS707OutputVO();
				dam = this.getDataAccessManager();
				QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				StringBuffer sql = new StringBuffer();
				sql.append("  SELECT YEARMON,                            ");
				sql.append("         PROJ_CODE,                          ");
				sql.append("         PROJ_NAME                           ");
				sql.append("  FROM TBPMS_LOAN_BONUS_PROJ                 ");
				if(StringUtils.isNotBlank(inputVO.getYearMon())){
					sql.append("  WHERE YEARMON = :YEARMON                ");
					condition.setObject("YEARMON", inputVO.getYearMon());
				}
				condition.setQueryString(sql.toString());
				ResultIF largeAgrList = dam.executePaging(condition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
				int totalPage_i = largeAgrList.getTotalPage(); // 分頁用
				int totalRecord_i = largeAgrList.getTotalRecord(); // 分頁用
				outputVO.setResultList(largeAgrList); // data
				outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
				outputVO.setTotalPage(totalPage_i);// 總頁次
				outputVO.setTotalRecord(totalRecord_i);// 總筆數
				this.sendRtnObject(outputVO);
				/*List<Map<String, Object>> result = dam.exeQuery(condition);
				outputVO.setResultList(result);
				sendRtnObject(outputVO);*/
			}
			catch (Exception e)
			{
				logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
				throw new APException(e.getMessage());
			}
		
		}
		
		//參數2從excel表格中新增數據
			@SuppressWarnings({ "rawtypes", "unused" })
			public void addParProj(Object body, IPrimitiveMap header) throws APException
			{
				int flag = 0;
				try {
					PMS707InputVO inputVO = (PMS707InputVO) body;
					PMS707OutputVO outputVO = new PMS707OutputVO();
					//清空臨時表
					dam = this.getDataAccessManager();
					QueryConditionIF dcon = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);	
					String dsql = " TRUNCATE TABLE TBPMS_LOAN_BONUS_PROJ_U ";
					dcon.setQueryString(dsql.toString());
					dam.exeUpdate(dcon);
					List<String> import_file = new ArrayList<String>();
					List<String> list =  new ArrayList<String>();
					String joinedPath = new File((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), inputVO.getFileName()).toString();
					Workbook workbook = Workbook.getWorkbook(new File(joinedPath));
					Sheet sheet[] = workbook.getSheets();
					//有表頭.xls文檔
					String lab = null;
					for(int a=0;a<sheet.length;a++){
						for(int i=1;i<sheet[a].getRows();i++){
							for(int j=0;j<sheet[a].getColumns();j++){
								lab = sheet[a].getCell(j, i).getContents();
								list.add(lab);
							}
							
							//excel表格記行數
							flag++;
							
							//判斷當前上傳數據月份是否一致
							/*if(!list.get(0).equals(inputVO.getYearMon())){
								throw new APException("上傳數據選擇月份不一致");
							}*/
							
							//判斷當前上傳數據欄位個數是否一致
							if(list.size()!=3){
								throw new APException("上傳數據欄位個數不一致");
							}
							
							//SQL指令
							StringBuffer sb = new StringBuffer();
							dam = this.getDataAccessManager();
							QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
							sb.append("   INSERT INTO TBPMS_LOAN_BONUS_PROJ_U (YEARMON,	     ");
							sb.append("  		PROJ_CODE,            				         ");
							sb.append("  		PROJ_NAME,           			             ");
							sb.append("  		RNUM,            					         ");
							sb.append("  		VERSION,            						 ");
							sb.append("  		CREATETIME,             					 ");
							sb.append("  		CREATOR,             						 ");
							sb.append("  		MODIFIER,         						     ");
							sb.append("  		LASTUPDATE )             					 ");
							sb.append("  	VALUES(:YEARMON,            				     ");
							sb.append("  		:PROJ_CODE,             				     ");
							sb.append("  		:PROJ_NAME,             				     ");
							sb.append("  		:RNUM,             					         ");
							sb.append("  		:VERSION,           					     ");
							sb.append("  		SYSDATE,           				             ");
							sb.append("  		:CREATOR,            					     ");
							sb.append("  		:MODIFIER,         					         ");
							sb.append("  		SYSDATE)          				             ");
							qc.setObject("YEARMON",list.get(0).trim()                         );
							qc.setObject("PROJ_CODE",list.get(1).trim()                       );
							qc.setObject("PROJ_NAME",list.get(2).trim()                		  );
							qc.setObject("RNUM",flag                                          );
							qc.setObject("VERSION","0"                                        );
							qc.setObject("CREATOR", inputVO.getUserId()                       );
							qc.setObject("MODIFIER", inputVO.getUserId()                      );
							qc.setQueryString(sb.toString());
							dam.exeUpdate(qc);
							list.clear();
						}
					}		
					outputVO.setFlag(flag);
					this.sendRtnObject(outputVO);
				} catch (Exception e) {
					logger.error("文檔上傳失敗");
					throw new APException("資料上傳失敗,錯誤發生在第"+flag+"筆,"+e.getMessage());
				}
			}
			
			/**
			 * 參數5查詢PS獎勵金調整數
			 * @param body
			 * @param header
			 * @throws APException
			 */
			@SuppressWarnings({ "unchecked", "rawtypes" })
			public void queryParBonusAdj(Object body, IPrimitiveMap header) throws JBranchException
			{
				try
				{
					PMS707InputVO inputVO = (PMS707InputVO) body;
					PMS707OutputVO outputVO = new PMS707OutputVO();
					dam = this.getDataAccessManager();
					QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					StringBuffer sql = new StringBuffer();
					sql.append("  SELECT YEARMON,                               ");
					sql.append("         PS_EMP_ID,                             ");
					sql.append("         BONUS_ADJ,                             ");
					sql.append("         BONUS_ADJ_REASON                       ");
					sql.append("  FROM TBPMS_LOAN_BONUS_ADJ                     ");
					if(StringUtils.isNotBlank(inputVO.getYearMon())){
						sql.append("  WHERE YEARMON = :YEARMON                  ");
						condition.setObject("YEARMON", inputVO.getYearMon());
					}
					condition.setQueryString(sql.toString());
					ResultIF largeAgrList = dam.executePaging(condition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
					int totalPage_i = largeAgrList.getTotalPage(); // 分頁用
					int totalRecord_i = largeAgrList.getTotalRecord(); // 分頁用
					outputVO.setResultList(largeAgrList); // data
					outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
					outputVO.setTotalPage(totalPage_i);// 總頁次
					outputVO.setTotalRecord(totalRecord_i);// 總筆數
					this.sendRtnObject(outputVO);
					/*List<Map<String, Object>> result = dam.exeQuery(condition);
					outputVO.setResultList(result);
					sendRtnObject(outputVO);*/
				}
				catch (Exception e)
				{
					logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
					throw new APException(e.getMessage());
				}
			
			}
			
			//參數5從excel表格中新增數據
				@SuppressWarnings({ "rawtypes", "unused" })
				public void addParBonusAdj(Object body, IPrimitiveMap header) throws APException
				{
					int flag = 0;
					try {
						PMS707InputVO inputVO = (PMS707InputVO) body;
						PMS707OutputVO outputVO = new PMS707OutputVO();
						//清空臨時表
						dam = this.getDataAccessManager();
						QueryConditionIF dcon = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);	
						String dsql = " TRUNCATE TABLE TBPMS_LOAN_BONUS_ADJ_U ";
						dcon.setQueryString(dsql.toString());
						dam.exeUpdate(dcon);
						List<String> import_file = new ArrayList<String>();
						List<String> list =  new ArrayList<String>();
						String joinedPath = new File((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), inputVO.getFileName()).toString();
						Workbook workbook = Workbook.getWorkbook(new File(joinedPath));
						Sheet sheet[] = workbook.getSheets();
						//有表頭.xls文檔
						String lab = null;
						for(int a=0;a<sheet.length;a++){
							for(int i=1;i<sheet[a].getRows();i++){
								for(int j=0;j<sheet[a].getColumns();j++){
									lab = sheet[a].getCell(j, i).getContents();
									list.add(lab);
								}
								
								//excel表格記行數
								flag++;
								
								/*//判斷當前上傳數據月份是否一致
								if(!list.get(0).equals(inputVO.getYearMon())){
									throw new APException("上傳數據選擇月份不一致");
								}*/
								
								//判斷當前上傳數據欄位個數是否一致
								if(list.size()!=4){
									throw new APException("上傳數據欄位個數不一致");
								}
								
								//SQL指令
								StringBuffer sb = new StringBuffer();
								dam = this.getDataAccessManager();
								QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
								sb.append("   INSERT INTO TBPMS_LOAN_BONUS_ADJ_U (YEARMON,	     ");
								sb.append("  		PS_EMP_ID,            				         ");
								sb.append("  		BONUS_ADJ,           			             ");
								sb.append("  		BONUS_ADJ_REASON,            				 ");
								sb.append("  		RNUM,            					         ");
								sb.append("  		VERSION,            						 ");
								sb.append("  		CREATETIME,             					 ");
								sb.append("  		CREATOR,             						 ");
								sb.append("  		MODIFIER,         						     ");
								sb.append("  		LASTUPDATE )             					 ");
								sb.append("  	VALUES(:YEARMON,            				     ");
								sb.append("  		:PS_EMP_ID,             				     ");
								sb.append("  		:BONUS_ADJ,             				     ");
								sb.append("  		:BONUS_ADJ_REASON,             	             ");
								sb.append("  		:RNUM,             					         ");
								sb.append("  		:VERSION,           					     ");
								sb.append("  		SYSDATE,           				             ");
								sb.append("  		:CREATOR,            					     ");
								sb.append("  		:MODIFIER,         					         ");
								sb.append("  		SYSDATE)          				             ");
								qc.setObject("YEARMON",list.get(0).trim()                         );
								qc.setObject("PS_EMP_ID",list.get(1).trim()                       );
								qc.setObject("BONUS_ADJ",list.get(2).trim()                       );
								qc.setObject("BONUS_ADJ_REASON",list.get(3).trim()                );
								qc.setObject("RNUM",flag                                          );
								qc.setObject("VERSION","0"                                        );
								qc.setObject("CREATOR", inputVO.getUserId()                       );
								qc.setObject("MODIFIER", inputVO.getUserId()                      );
								qc.setQueryString(sb.toString());
								dam.exeUpdate(qc);
								list.clear();
							}
						}		
						outputVO.setFlag(flag);
						this.sendRtnObject(outputVO);
					} catch (Exception e) {
						logger.error("文檔上傳失敗");
						throw new APException("資料上傳失敗,錯誤發生在第"+flag+"筆,"+e.getMessage());
					}
				}
				
	/**
	 * 參數6查詢計績PS專員名單
	 * @param body
	 * @param header
	 * @throws APException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void queryParPsList(Object body, IPrimitiveMap header) throws JBranchException
	{
		try
		{
			PMS707InputVO inputVO = (PMS707InputVO) body;
			PMS707OutputVO outputVO = new PMS707OutputVO();
			dam = this.getDataAccessManager();
			QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			sql.append("  SELECT YEARMON,                               ");
			sql.append("         REGION_CENTER_ID,                      ");
			sql.append("         REGION_CENTER_NAME,                    ");
			sql.append("         BRANCH_AREA_ID,                        ");
			sql.append("         BRANCH_AREA_NAME,                      ");
			sql.append("         BRANCH_NBR,                            ");
			sql.append("         BRANCH_NAME,                           ");
			sql.append("         REVOKE_PERSON,                         ");
			sql.append("         EMP_NAME,                              ");
			sql.append("         EMP_ID,                                ");
			sql.append("         CUST_ID,                               ");
			sql.append("         JOB_RANK,                              ");
			sql.append("         JOB_TITLE_NAME,                        ");
			sql.append("         LEAVE_DATE,                            ");
			sql.append("         BEN_SAL,                               ");
			sql.append("         JOB_ADD                                ");
			sql.append("  FROM TBPMS_PS_LIST                            ");
			if(StringUtils.isNotBlank(inputVO.getYearMon())){
				sql.append("  WHERE YEARMON = :YEARMON                  ");
				condition.setObject("YEARMON", inputVO.getYearMon());
			}
			condition.setQueryString(sql.toString());
			List<Map<String, Object>> result = dam.exeQuery(condition);
			outputVO.setResultList(result);
			sendRtnObject(outputVO);
		}
		catch (Exception e)
		{
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			throw new APException(e.getMessage());
		}
	
	}
	
	// 修改主表中为已设定
	public void changeMastSet(String column,String yearMon,String userId) throws JBranchException
	{
			dam = this.getDataAccessManager();
			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
			sb.append("		UPDATE TBPMS_LOAN_BONUS_MAST T 					");
			sb.append("		SET T."+column+" = '1'||SUBSTR(T."+column+",2), ");
			sb.append("			T.VERSION = 1,                              ");
			sb.append("         MODIFIER = :MODIFIER,                       ");
			sb.append("         LASTUPDATE = SYSDATE                        ");
			sb.append("		WHERE T.YEARMON = :YEARMON 					    ");
			qc.setObject("YEARMON", yearMon);
			qc.setObject("MODIFIER", userId);
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
		PMS707InputVO inputVO = (PMS707InputVO) body;
		PMS707OutputVO outputVO = new PMS707OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		//執行存儲過程
		if(inputVO.getStoredFlag().equals("uploadDeRate")){
			try
			{
				sb.append(" CALL PABTH_BTPMS720.SP_TBPMS_LOAN_BONUS_DERATE(? ,? ) ");
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
				}else{
					//文檔上傳成功
					changeMastSet("PAR_RATE",inputVO.getYearMon(),inputVO.getUserId());
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
		
		if(inputVO.getStoredFlag().equals("addDedReward")){
			try
			{
				sb.append(" CALL PABTH_BTPMS720.SP_TBPMS_LOAN_BOUNTY_DETAIL(? ,? ) ");
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
				}else{
					//文檔上傳成功
					changeMastSet("PAR_BOUNTY",inputVO.getYearMon(),inputVO.getUserId());
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
		
		if(inputVO.getStoredFlag().equals("addParProj")){
			try
			{
				sb.append(" CALL PABTH_BTPMS720.SP_TBPMS_LOAN_BONUS_PROJ(? ,? ) ");
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
				}else{
					//文檔上傳成功
					changeMastSet("PAR_PROJ",inputVO.getYearMon(),inputVO.getUserId());
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
		
		if(inputVO.getStoredFlag().equals("addParBonusAdj")){
			try
			{
				sb.append(" CALL PABTH_BTPMS720.SP_TBPMS_LOAN_BONUS_ADJ(? ,? ) ");
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
				}else{
					//文檔上傳成功
					changeMastSet("PAR_BONUS_ADJ",inputVO.getYearMon(),inputVO.getUserId());
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
}