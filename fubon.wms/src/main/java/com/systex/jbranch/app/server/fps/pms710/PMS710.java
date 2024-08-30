package com.systex.jbranch.app.server.fps.pms710;

import java.io.File;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jxl.Sheet;
import jxl.Workbook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.pms710.PMS710InputVO;
import com.systex.jbranch.app.server.fps.pms710.PMS710OutputVO;
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
 * Comments Name : PMS710.java<br>
 * Author : cty<br>
 * Date :2016年11月23日 <br>
 * Version : 1.0 <br>
 * Editor : cty<br>
 * Editor Date : 2016年11月23日<br>
 */
@Component("pms710")
@Scope("request")
public class PMS710 extends FubonWmsBizLogic
{
	public DataAccessManager dam = null;

	private Logger logger = LoggerFactory.getLogger(PMS710.class);

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
			PMS710InputVO inputVO = (PMS710InputVO) body;
			PMS710OutputVO outputVO = new PMS710OutputVO();
			dam = this.getDataAccessManager();
			QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT ROWNUM,                                      ");
			sql.append("       T.*                                          ");
			sql.append("FROM TBPMS_CNR_BOUNTY_ADJ T                         ");
			sql.append("WHERE T.YEARMON = :YEARMON                          ");
			sql.append("AND T.BOUNTY_TYPE = :BOUNTY_TYPE                    ");
			sql.append("ORDER BY T.EMP_ID,ROWNUM                            ");
			condition.setObject("YEARMON",inputVO.getYearMon().trim()        );
			condition.setObject("BOUNTY_TYPE",inputVO.getBounty_Type().trim());	
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
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	
	}
	
	/**
	 * 從excel表格中新增數據
	 * 上傳
	 * @param body
	 * @param header
	 * @throws APException
	 */
	@SuppressWarnings({ "rawtypes", "unused" })
	public void addData(Object body, IPrimitiveMap header) throws APException
	{
		int flag = 0;
		try {
			PMS710InputVO inputVO = (PMS710InputVO) body;
			PMS710OutputVO outputVO = new PMS710OutputVO();
			List<String> import_file = new ArrayList<String>();
			List<String> list =  new ArrayList<String>();
			String joinedPath = new File((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), inputVO.getFileName()).toString();
			Workbook workbook = Workbook.getWorkbook(new File(joinedPath));
			Sheet sheet[] = workbook.getSheets();
			String lab = null;
			
			//清空臨時表
			dam = this.getDataAccessManager();
			QueryConditionIF dcon = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);	
			String dsql = " TRUNCATE TABLE TBPMS_CNR_BOUNTY_ADJ_U ";
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
					
					//判斷所讀取到的數據
					if(!list.get(0).equals(inputVO.getYearMon())){
						throw new APException("上傳數據選擇月份不一致");
					}
					
					//SQL指令
					//獎勵金調整
					if(inputVO.getBounty_Type().equals("1")){
						if(list.size()!=4)
						{
							throw new APException("上傳數據欄位個數不一致");
						}
						StringBuffer sb = new StringBuffer();
						dam = this.getDataAccessManager();
						QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
						sb.append("INSERT INTO TBPMS_CNR_BOUNTY_ADJ_U(YEARMON,	  ");
						sb.append("  							  BOUNTY_TYPE,    ");
						sb.append("  							  EMP_ID,         ");
						sb.append("  		                      ADJSUM,         ");
						sb.append("  		                      ADJDETAIL,      ");
						sb.append("  		                      RNUM,           ");
						sb.append("  		                      VERSION,        ");
						sb.append("  		                      CREATETIME,     ");
						sb.append("  		                      CREATOR,        ");
						sb.append("  		                      MODIFIER,       ");
						sb.append("  		                      LASTUPDATE)     ");
						sb.append("  	                          VALUES(:YEARMON,");
						sb.append("  		                      :BOUNTY_TYPE,   ");
						sb.append("  		                      :EMP_ID,        ");
						sb.append("  		                      :ADJSUM,        ");
						sb.append("  		                      :ADJDETAIL,     ");
						sb.append("  		                       :RNUM,         ");
						sb.append("  		                       0,             ");
						sb.append("  		                       SYSDATE,       ");
						sb.append("  		                       :CREATOR,      ");
						sb.append("  		                       :MODIFIER,     ");
						sb.append("  		                       SYSDATE)       ");
						qc.setObject("YEARMON",inputVO.getYearMon().trim()         );
						qc.setObject("BOUNTY_TYPE",inputVO.getBounty_Type().trim() );
						qc.setObject("EMP_ID",list.get(1).trim()                   );
						qc.setObject("ADJSUM",list.get(2).trim()                   );
						qc.setObject("ADJDETAIL",list.get(3).trim()                );
						qc.setObject("RNUM",flag                                   );
						qc.setObject("CREATOR",inputVO.getUserId().trim()          );
						qc.setObject("MODIFIER",inputVO.getUserId().trim()         );
						qc.setQueryString(sb.toString());
						dam.exeUpdate(qc);
					}
					
					//歷史月份獎勵金
					if(inputVO.getBounty_Type().equals("2")){
						if(list.size()!=3)
						{
							throw new APException("上傳數據欄位個數不一致");
						}
						StringBuffer sb = new StringBuffer();
						dam = this.getDataAccessManager();
						QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
						sb.append("INSERT INTO TBPMS_CNR_BOUNTY_ADJ_U(YEARMON,	  ");
						sb.append("  							  BOUNTY_TYPE,    ");
						sb.append("  							  EMP_ID,         ");
						sb.append("  		                      SUM,            ");
						sb.append("  		                      RNUM,           ");
						sb.append("  		                      VERSION,        ");
						sb.append("  		                      CREATETIME,     ");
						sb.append("  		                      CREATOR,        ");
						sb.append("  		                      MODIFIER,       ");
						sb.append("  		                      LASTUPDATE)     ");
						sb.append("  	                          VALUES(:YEARMON,");
						sb.append("  		                      :BOUNTY_TYPE,   ");
						sb.append("  		                      :EMP_ID,        ");
						sb.append("  		                      :SUM,           ");
						sb.append("  		                      :RNUM,          ");
						sb.append("  		                       0,             ");
						sb.append("  		                       SYSDATE,       ");
						sb.append("  		                       :CREATOR,      ");
						sb.append("  		                       :MODIFIER,     ");
						sb.append("  		                       SYSDATE)       ");
						qc.setObject("YEARMON",inputVO.getYearMon().trim()         );
						qc.setObject("BOUNTY_TYPE",inputVO.getBounty_Type().trim() );
						qc.setObject("EMP_ID",list.get(1).trim()                   );
						qc.setObject("SUM",list.get(2).trim()                      );
						qc.setObject("RNUM",flag                                   );
						qc.setObject("CREATOR",inputVO.getUserId().trim()          );
						qc.setObject("MODIFIER",inputVO.getUserId().trim()         );
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
			logger.error("資料上傳失敗");
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
			PMS710InputVO inputVO = (PMS710InputVO) body;
			PMS710OutputVO outputVO = new PMS710OutputVO();
			//執行存儲過程
			dam = this.getDataAccessManager();
			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
			sb.append(" CALL PABTH_BTPMS723.SP_TBPMS_CNR_BOUNTY_ADJ(? ,? ) ");
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
