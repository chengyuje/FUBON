package com.systex.jbranch.app.server.fps.pms701;

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

import com.systex.jbranch.app.server.fps.pms701.PMS701InputVO;
import com.systex.jbranch.app.server.fps.pms701.PMS701OutputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * Copy Right Information : Forms Syntron <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description :調整客戶收益(整批)Controller<br>
 * Comments Name : PMS701.java<br>
 * Author : cty<br>
 * Date :2016年11月23日 <br>
 * Version : 1.0 <br>
 * Editor : cty<br>
 * Editor Date : 2016年11月23日<br>
 */
@Component("pms701")
@Scope("request")
public class PMS701 extends FubonWmsBizLogic
{
	public DataAccessManager dam = null;

	private Logger logger = LoggerFactory.getLogger(PMS701.class);

	/**
	 * 查詢
	 * @param body
	 * @param header
	 * @throws APException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void queryData(Object body, IPrimitiveMap header) throws APException
	{
		try
		{
			PMS701InputVO inputVO = (PMS701InputVO) body;
			PMS701OutputVO outputVO = new PMS701OutputVO();
			dam = this.getDataAccessManager();
			QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
	
			StringBuffer sql = new StringBuffer();
			
			sql.append("SELECT ROWNUM,                                     ");
			sql.append("       T.*                                         ");
			sql.append("FROM TBPMS_CNR_CUST_ADJ T                          ");
			
			if(!StringUtils.isBlank(inputVO.getYearMon()))
			{
				sql.append("WHERE T.YEARMON = :yearMon                     ");
				condition.setObject("yearMon",inputVO.getYearMon().trim()   );
			}
			
			if(!StringUtils.isBlank(inputVO.getCust_Type()))
			{
				sql.append("AND T.CUST_TYPE = :cust_Type                    ");
				condition.setObject("cust_Type",inputVO.getCust_Type().trim());
			}
			
			sql.append("ORDER BY T.CUST_ID                                  ");
			condition.setQueryString(sql.toString());
			ResultIF largeAgrList = dam.executePaging(condition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
			int totalPage_i = largeAgrList.getTotalPage(); // 分頁用
			int totalRecord_i = largeAgrList.getTotalRecord(); // 分頁用
			outputVO.setLargeAgrList(largeAgrList); // data
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
	 * 從excel表格中新增數據
	 * 查詢
	 * @param body
	 * @param header
	 * @throws APException
	 */
	@SuppressWarnings({ "rawtypes", "unused" })
	public void addData(Object body, IPrimitiveMap header) throws APException
	{
		int flag = 0;
		try {
			PMS701InputVO inputVO = (PMS701InputVO) body;
			PMS701OutputVO outputVO = new PMS701OutputVO();
			List<String> import_file = new ArrayList<String>();
			List<String> list =  new ArrayList<String>();
			String joinedPath = new File((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), inputVO.getFileName()).toString();
			Workbook workbook = Workbook.getWorkbook(new File(joinedPath));
			Sheet sheet[] = workbook.getSheets();
			String lab = null;
			
			//清空臨時表
			dam = this.getDataAccessManager();
			QueryConditionIF dcon = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);	
			String dsql = " TRUNCATE TABLE TBPMS_CNR_CUST_ADJ_U ";
			dcon.setQueryString(dsql.toString());
			dam.exeUpdate(dcon);
			
			//有表頭.xls文檔
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
					
					//SQL指令
					//RM轉介客戶
					if(inputVO.getCust_Type().equals("1")){
						if(list.size()!=3)
						{
							throw new APException("上傳數據欄位個數不一致");
						}
						StringBuffer sb = new StringBuffer();
						dam = this.getDataAccessManager();
						QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
						sb.append("INSERT INTO TBPMS_CNR_CUST_ADJ_U(YEARMON,	  ");
						sb.append("  							  CUST_TYPE,      ");
						sb.append("  							  CUST_ID,        ");
						sb.append("  		                      COL1,           ");
						sb.append("  		                      COL2,           ");
						sb.append("  		                      COL3,           ");
						sb.append("  		                      COL4,           ");
						sb.append("  		                      RNUM,           ");
						sb.append("  		                      VERSION,        ");
						sb.append("  		                      CREATETIME,     ");
						sb.append("  		                      CREATOR,        ");
						sb.append("  		                      MODIFIER,       ");
						sb.append("  		                      LASTUPDATE)     ");
						sb.append("  	                          VALUES(:YEARMON,");
						sb.append("  		                      :CUST_TYPE,     ");
						sb.append("  		                      :CUST_ID,       ");
						sb.append("  		                      :COL1,          ");
						sb.append("  		                      :COL2,          ");
						sb.append("  		                      :COL3,          ");
						sb.append("  		                      :COL4,          ");
						sb.append("  		                      :RNUM,          ");
						sb.append("  		                      :VERSION,       ");
						sb.append("  		                      SYSDATE,        ");
						sb.append("  		                      :CREATOR,       ");
						sb.append("  		                      :MODIFIER,      ");
						sb.append("  		                      SYSDATE)        ");
						qc.setObject("YEARMON", inputVO.getYearMon().trim()        );
						qc.setObject("CUST_TYPE", inputVO.getCust_Type().trim()    );
						qc.setObject("CUST_ID", list.get(1).trim()                 );
						qc.setObject("COL1", list.get(2).trim()                    );
						qc.setObject("COL2", ""                                    );
						qc.setObject("COL3", ""                                    );
						qc.setObject("COL4", ""                                    );
						qc.setObject("RNUM",flag                                   );
						qc.setObject("VERSION", "0"                                );
						qc.setObject("CREATOR", inputVO.getUserId().trim()         );
						qc.setObject("MODIFIER", inputVO.getUserId().trim()        );
						qc.setQueryString(sb.toString());
						dam.exeUpdate(qc);
					}
					
					//個金轉介法金客戶
					if(inputVO.getCust_Type().equals("2")){
						if(list.size()!=6)
						{
							throw new APException("上傳數據欄位個數不一致");
						}
						StringBuffer sb = new StringBuffer();
						dam = this.getDataAccessManager();
						QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
						sb.append("INSERT INTO TBPMS_CNR_CUST_ADJ_U(YEARMON,	  ");
						sb.append("  							  CUST_TYPE,      ");
						sb.append("  							  CUST_ID,        ");
						sb.append("  		                      COL1,           ");
						sb.append("  		                      COL2,           ");
						sb.append("  		                      COL3,           ");
						sb.append("  		                      COL4,           ");
						sb.append("  		                      RNUM,           ");
						sb.append("  		                      VERSION,        ");
						sb.append("  		                      CREATETIME,     ");
						sb.append("  		                      CREATOR,        ");
						sb.append("  		                      MODIFIER,       ");
						sb.append("  		                      LASTUPDATE)     ");
						sb.append("  	                          VALUES(:YEARMON,");
						sb.append("  		                      :CUST_TYPE,     ");
						sb.append("  		                      :CUST_ID,       ");
						sb.append("  		                      :COL1,          ");
						sb.append("  		                      :COL2,          ");
						sb.append("  		                      :COL3,          ");
						sb.append("  		                      :COL4,          ");
						sb.append("  		                      :RNUM,          ");
						sb.append("  		                      :VERSION,       ");
						sb.append("  		                      SYSDATE,        ");
						sb.append("  		                      :CREATOR,       ");
						sb.append("  		                      :MODIFIER,      ");
						sb.append("  		                      SYSDATE)        ");
						qc.setObject("YEARMON", inputVO.getYearMon().trim()        );
						qc.setObject("CUST_TYPE", inputVO.getCust_Type().trim()    );
						qc.setObject("CUST_ID", list.get(1).trim()                 );
						qc.setObject("COL1", list.get(2).trim()                    );
						qc.setObject("COL2", list.get(3).trim()                    );
						qc.setObject("COL3", list.get(4).trim()                    );
						qc.setObject("COL4", list.get(4).trim()                    );
						qc.setObject("RNUM",flag                                   );
						qc.setObject("VERSION", "0"                                );
						qc.setObject("CREATOR", inputVO.getUserId().trim()         );
						qc.setObject("MODIFIER", inputVO.getUserId().trim()        );
						qc.setQueryString(sb.toString());
						dam.exeUpdate(qc);
					}
					
					//加碼專案客戶
					if(inputVO.getCust_Type().equals("3")){
						if(list.size()!=3)
						{
							throw new APException("上傳數據欄位個數不一致");
						}
						StringBuffer sb = new StringBuffer();
						dam = this.getDataAccessManager();
						QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
						sb.append("INSERT INTO TBPMS_CNR_CUST_ADJ_U(YEARMON,	  ");
						sb.append("  							  CUST_TYPE,      ");
						sb.append("  							  CUST_ID,        ");
						sb.append("  		                      COL1,           ");
						sb.append("  		                      COL2,           ");
						sb.append("  		                      COL3,           ");
						sb.append("  		                      COL4,           ");
						sb.append("  		                      RNUM,           ");
						sb.append("  		                      VERSION,        ");
						sb.append("  		                      CREATETIME,     ");
						sb.append("  		                      CREATOR,        ");
						sb.append("  		                      MODIFIER,       ");
						sb.append("  		                      LASTUPDATE)     ");
						sb.append("  	                          VALUES(:YEARMON,");
						sb.append("  		                      :CUST_TYPE,     ");
						sb.append("  		                      :CUST_ID,       ");
						sb.append("  		                      :COL1,          ");
						sb.append("  		                      :COL2,          ");
						sb.append("  		                      :COL3,          ");
						sb.append("  		                      :COL4,          ");
						sb.append("  		                      :RNUM,          ");
						sb.append("  		                      :VERSION,       ");
						sb.append("  		                      SYSDATE,        ");
						sb.append("  		                      :CREATOR,       ");
						sb.append("  		                      :MODIFIER,      ");
						sb.append("  		                      SYSDATE)        ");
						qc.setObject("YEARMON", inputVO.getYearMon().trim()        );
						qc.setObject("CUST_TYPE", inputVO.getCust_Type().trim()    );
						qc.setObject("CUST_ID", list.get(1).trim()                 );
						qc.setObject("COL1", list.get(2).trim()                    );
						qc.setObject("COL2", ""                                    );
						qc.setObject("COL3", ""                                    );
						qc.setObject("COL4", ""                                    );
						qc.setObject("RNUM",flag                                   );
						qc.setObject("VERSION", "0"                                );
						qc.setObject("CREATOR", inputVO.getUserId().trim()         );
						qc.setObject("MODIFIER", inputVO.getUserId().trim()        );
						qc.setQueryString(sb.toString());
						dam.exeUpdate(qc);
					}
					
					/*//保險遞延客戶
					if(inputVO.getCust_Type().equals("4")){
						String sql = "INSERT INTO TBPMS_CNR_CUST_ADJ VALUES('"
								+list.get(0) +"','"
								+inputVO.getCust_Type() +"','"
								+list.get(2) +"','"
								+Integer.parseInt(list.get(3)) +"','"
								+"','"
								+"','111'"
								+",0,SYSDATE,'"
								+inputVO.getUserId()+"','"
								+inputVO.getUserId()+"',SYSDATE"
								+")";
						dam = this.getDataAccessManager();
						QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
						condition.setQueryString(sql.toString());
						flag++;
						dam.exeUpdate(condition);
					}*/
					
					//特定排除客戶
					if(inputVO.getCust_Type().equals("4")){
						if(list.size()!=2)
						{
							throw new APException("上傳數據欄位個數不一致");
						}
						StringBuffer sb = new StringBuffer();
						dam = this.getDataAccessManager();
						QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
						sb.append("INSERT INTO TBPMS_CNR_CUST_ADJ_U(YEARMON,	  ");
						sb.append("  							  CUST_TYPE,      ");
						sb.append("  							  CUST_ID,        ");
						sb.append("  		                      COL1,           ");
						sb.append("  		                      COL2,           ");
						sb.append("  		                      COL3,           ");
						sb.append("  		                      COL4,           ");
						sb.append("  		                      RNUM,       	  ");
						sb.append("  		                      VERSION,        ");
						sb.append("  		                      CREATETIME,     ");
						sb.append("  		                      CREATOR,        ");
						sb.append("  		                      MODIFIER,       ");
						sb.append("  		                      LASTUPDATE)     ");
						sb.append("  	                          VALUES(:YEARMON,");
						sb.append("  		                      :CUST_TYPE,     ");
						sb.append("  		                      :CUST_ID,       ");
						sb.append("  		                      :COL1,          ");
						sb.append("  		                      :COL2,          ");
						sb.append("  		                      :COL3,          ");
						sb.append("  		                      :COL4,          ");
						sb.append("  		                      :RNUM,      	  ");
						sb.append("  		                      :VERSION,       ");
						sb.append("  		                      SYSDATE,        ");
						sb.append("  		                      :CREATOR,       ");
						sb.append("  		                      :MODIFIER,      ");
						sb.append("  		                      SYSDATE)        ");
						qc.setObject("YEARMON", inputVO.getYearMon().trim()        );
						qc.setObject("CUST_TYPE", inputVO.getCust_Type().trim()    );
						qc.setObject("CUST_ID", list.get(1).trim()                 );
						qc.setObject("COL1", ""                                    );
						qc.setObject("COL2", ""                                    );
						qc.setObject("COL3", ""                                    );
						qc.setObject("COL4", ""                                    );
						qc.setObject("RNUM",flag                                   );
						qc.setObject("VERSION", "0"                                );
						qc.setObject("CREATOR", inputVO.getUserId().trim()         );
						qc.setObject("MODIFIER", inputVO.getUserId().trim()        );
						qc.setQueryString(sb.toString());
						dam.exeUpdate(qc);
					}
					list.clear();
				}
			}		
					//資料上傳成功
					outputVO.setFlag(flag);
					this.sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
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
			PMS701InputVO inputVO = (PMS701InputVO) body;
			PMS701OutputVO outputVO = new PMS701OutputVO();
			//執行存儲過程
			dam = this.getDataAccessManager();
			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
			sb.append(" CALL PABTH_BTPMS722.SP_TBPMS_CNR_CUST_ADJ(? ,? ) ");
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
	
}
