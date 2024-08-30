package com.systex.jbranch.app.server.fps.pms212;

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

import com.ibm.icu.math.BigDecimal;
import com.systex.jbranch.app.server.fps.pms212.PMS212InputVO;
import com.systex.jbranch.app.server.fps.pms212.PMS212OutputVO;
import com.systex.jbranch.app.server.fps.pms708.PMS708InputVO;
import com.systex.jbranch.app.server.fps.pms708.PMS708OutputVO;
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
 * Comments Name : PMS212.java<br>
 * Author : cty<br>
 * Date :2016年11月23日 <br>
 * Version : 1.0 <br>
 * Editor : cty<br>
 * Editor Date : 2016年11月23日<br>
 */
@Component("pms212")
@Scope("request")
public class PMS212 extends FubonWmsBizLogic
{
	public DataAccessManager dam = null;

	private Logger logger = LoggerFactory.getLogger(PMS212.class);

	/**
	 * 查詢
	 * @param body
	 * @param header
	 * @throws APException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
	public void queryData(Object body, IPrimitiveMap header) throws APException
	{
		try
		{
			PMS212InputVO inputVO = (PMS212InputVO) body;
			PMS212OutputVO outputVO = new PMS212OutputVO();
			dam = this.getDataAccessManager();
			QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
	
			StringBuffer sql = new StringBuffer();
			
			sql.append("SELECT ROWNUM,T.*                            ");
			sql.append("FROM TBPMS_PROD_AOCODE_ADJ T                 ");
			sql.append("WHERE T.CUSTID = :CUSTID                     ");
			sql.append("ORDER BY T.ADJDATAMONTH,T.CUSTID             ");
			condition.setObject("CUSTID",inputVO.getCustID().trim()   );
			condition.setQueryString(sql.toString()                   );
			ResultIF largeAgrList = dam.executePaging(condition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
			List<Map<String, Object>> resultList = dam.exeQuery(condition);
			int totalPage_i = largeAgrList.getTotalPage(); // 分頁用
			int totalRecord_i = largeAgrList.getTotalRecord(); // 分頁用
			outputVO.setLargeAgrList(largeAgrList); // data
			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
			outputVO.setTotalPage(totalPage_i);// 總頁次
			outputVO.setTotalRecord(totalRecord_i);// 總筆數
			outputVO.setResultList(resultList);// 查詢結果集
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
	 * 上傳
	 * @param body
	 * @param header
	 * @throws APException
	 */
	@SuppressWarnings({ "rawtypes", "unused" })
	public void importData(Object body, IPrimitiveMap header) throws APException
	{
		int flag = 0;
		String str = null;
		String []strs = null;
		try {
			PMS212InputVO inputVO = (PMS212InputVO) body;
			PMS212OutputVO outputVO = new PMS212OutputVO();
			List<String> import_file = new ArrayList<String>();
			List<String> list =  new ArrayList<String>();
			String joinedPath = new File((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), inputVO.getFileName()).toString();
			Workbook workbook = Workbook.getWorkbook(new File(joinedPath));
			Sheet sheet[] = workbook.getSheets();
			String lab = null;
			
			//有表頭.xls文檔
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
					
					//校驗計績年月和客戶ID
					StringBuffer sb1 = new StringBuffer();
					dam = this.getDataAccessManager();
					QueryConditionIF qc1 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sb1.append("   SELECT COUNT(*) AS NUM FROM TBPMS_PROD_AOCODE_ADJ ");
					sb1.append("     WHERE ADJDATAMONTH = :ADJDATAMONTH              ");
					sb1.append("  	 AND CUSTID  = :CUST_ID                          ");
					qc1.setObject("ADJDATAMONTH",list.get(0).trim()                   );
					qc1.setObject("CUST_ID",list.get(1).trim()                        );
					qc1.setQueryString(sb1.toString());
					List<Map<String, Object>> result1 = dam.exeQuery(qc1);
					if(Integer.parseInt((String)result1.get(0).get("NUM").toString())==0){
						throw new APException("請上傳正確的計績年月和客戶ID");
					}
					
					//校驗調整後AO_CODE
					StringBuffer sb2 = new StringBuffer();
					dam = this.getDataAccessManager();
					QueryConditionIF qc2 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sb2.append("   SELECT AOCODE FROM TBPMS_PROD_AOCODE_ADJ ");
					sb2.append("     WHERE ADJDATAMONTH = :ADJDATAMONTH              ");
					sb2.append("  	 AND CUSTID  = :CUST_ID                          ");
					qc2.setObject("ADJDATAMONTH",list.get(0).trim()                   );
					qc2.setObject("CUST_ID",list.get(1).trim()                        );
					qc2.setQueryString(sb2.toString());
					List<Map<String, Object>> result2 = dam.exeQuery(qc2);
					if(list.get(2).trim().equals("")){
						throw new APException("請輸入調整後AOCODE");
					}
					if(result2.get(0).get("AOCODE").toString().equals(list.get(2).trim())){
						throw new APException("調整後AOCODE不可等於原始AO_CODE");
					}
					
					//校驗調整後AO_CODE
					StringBuffer sb3 = new StringBuffer();
					dam = this.getDataAccessManager();
					QueryConditionIF qc3 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sb3.append("   SELECT COUNT(1) AS CNT FROM TBPMS_SALES_AOCODE_M R                                      ");
					sb3.append("   WHERE LAST_DAY(TO_DATE(:ADJDATAMONTH,'YYYYMM')) BETWEEN R.START_TIME AND R.END_TIME     ");
					sb3.append("  	 AND R.TYPE IN('1','2')                          									   ");
					sb3.append("  	 AND R.YEARMON = :ADJDATAMONTH                    									   ");
					sb3.append("  	 AND R.AO_CODE = :AOCODE                          									   ");
					qc3.setObject("ADJDATAMONTH",list.get(0).trim()                   );
					qc3.setObject("AOCODE",list.get(2).trim()                        );
					qc3.setQueryString(sb3.toString());
					List<Map<String, Object>> result3 = dam.exeQuery(qc3);
					int total = Integer.parseInt((String)result3.get(0).get("CNT").toString());
					if (total == 0){
						throw new APException(result3.get(0).get("CNT") + "非法的AO_CODE");						
					}
					
					//SQL指令
					StringBuffer sb = new StringBuffer();
					dam = this.getDataAccessManager();
					QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sb.append("  UPDATE TBPMS_PROD_AOCODE_ADJ                        ");
					sb.append("    SET  ADJ = :ADJ,                                  ");
					sb.append("  	   	ADJAOCODE = :ADJAOCODE,                      ");
					sb.append("  		VERSION = 1,                                 ");
					sb.append("  		MODIFIER = :MODIFIER,                        ");
					sb.append("  		LASTUPDATE = SYSDATE                         ");
					sb.append("   WHERE CUSTID = :CUSTID                             ");
					sb.append("   AND	ADJDATAMONTH = :ADJDATAMONTH                 ");
					qc.setObject("ADJ","1"                                            );
					qc.setObject("ADJAOCODE", list.get(2).trim()                      );
					qc.setObject("MODIFIER", inputVO.getUserId()                      );
					qc.setObject("CUSTID", list.get(1).trim()                         );
					qc.setObject("ADJDATAMONTH", list.get(0).trim()                   );
					qc.setQueryString(sb.toString());
					dam.exeUpdate(qc);
					list.clear();
					}
				
			}
			//資料上傳結束
			this.sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error("發生錯誤:%s",StringUtil.getStackTraceAsString(e));
			throw new APException("資料上傳失敗,錯誤發生在第"+flag+"筆:"+e.getMessage());
		}
		
	}

	/**
	 * 更新
	 * @param body
	 * @param header
	 * @throws APException
	 */
	@SuppressWarnings("rawtypes")
	public void updateData(Object body, IPrimitiveMap header) throws APException
	{	
		int flag = 0;
		try {
			PMS212InputVO inputVO = (PMS212InputVO) body;
			PMS212OutputVO outputVO = new PMS212OutputVO();
			String errorMessage = new String(); 
				for(int i = 0;i<inputVO.getInputList().size();i++){
					
					if(!(inputVO.getInputList().get(i)==null) && inputVO.getInputList().get(i).get("ADJAOCODE") !=null){
						//校驗調整後AO_CODE
						StringBuffer sb3 = new StringBuffer();
						dam = this.getDataAccessManager();
						QueryConditionIF qc3 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
						sb3.append("   SELECT COUNT(1) AS CNT FROM TBPMS_SALES_AOCODE_REC R                                    ");
						sb3.append("   WHERE LAST_DAY(TO_DATE(:ADJDATAMONTH,'YYYYMM')) BETWEEN R.START_TIME AND R.END_TIME     ");
						sb3.append("  	 AND R.TYPE IN('1','2')                          									   ");
						sb3.append("  	 AND R.AO_CODE = :AOCODE                          									   ");
						qc3.setObject("ADJDATAMONTH",inputVO.getInputList().get(i).get("ADJDATAMONTH"));
						qc3.setObject("AOCODE",inputVO.getInputList().get(i).get("ADJAOCODE"));
						qc3.setQueryString(sb3.toString());
						List<Map<String, Object>> result3 = dam.exeQuery(qc3);
						int total = Integer.parseInt((String)result3.get(0).get("CNT").toString());
						if (total == 0){
							if(errorMessage.length()<1000){
								errorMessage = errorMessage + (inputVO.getInputList().get(i).get("ADJDATAMONTH") + "月对应的 AO_CODE:"
										+ inputVO.getInputList().get(i).get("ADJAOCODE") + "不存在;");
							}
						}else{
							//SQL指令
							dam = this.getDataAccessManager();
							QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
							StringBuffer sb = new StringBuffer();
							sb.append("  UPDATE TBPMS_PROD_AOCODE_ADJ                                          ");
							sb.append("    	SET ADJ = :ADJ,                                                    ");
							sb.append("  	   	ADJAOCODE = :ADJAOCODE,                                        ");
							sb.append("  		VERSION = 1,                                                   ");
							sb.append("  		MODIFIER = :MODIFIER,                                          ");
							sb.append("  		LASTUPDATE = SYSDATE                                           ");
							sb.append("  	WHERE CUSTID = :CUSTID                                             ");
							sb.append("  	AND	ADJDATAMONTH = :ADJDATAMONTH                                   ");
							qc.setObject("ADJ",inputVO.getInputList().get(i).get("ADJ")                         );
							qc.setObject("ADJAOCODE", inputVO.getInputList().get(i).get("ADJAOCODE")            );
							qc.setObject("MODIFIER", inputVO.getUserId()                                        );
							qc.setObject("CUSTID", inputVO.getInputList().get(i).get("CUSTID")                  );
							qc.setObject("ADJDATAMONTH", inputVO.getInputList().get(i).get("ADJDATAMONTH")      );
							qc.setQueryString(sb.toString());
							dam.exeUpdate(qc);
							flag++;
						}
					}
					
				}
				outputVO.setFlag(flag);
				outputVO.setErrorMessage(errorMessage);
				this.sendRtnObject(outputVO);
			
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			throw new APException(e.getMessage());
		}
		
	}
	
}
