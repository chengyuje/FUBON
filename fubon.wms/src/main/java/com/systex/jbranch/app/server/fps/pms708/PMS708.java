package com.systex.jbranch.app.server.fps.pms708;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import jxl.Sheet;
import jxl.Workbook;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * Copy Right Information : Forms Syntron <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : 專員信用卡業績上傳Controller<br>
 * Comments Name : PMS708.java<br>
 * Author : cty<br>
 * Date :2016年11月17日 <br>
 * Version : 1.0 <br>
 * Editor : cty<br>
 * Editor Date : 2016年11月17日<br>
 */
@Component("pms708")
@Scope("request")
public class PMS708 extends FubonWmsBizLogic
{
	public DataAccessManager dam = null;

	private Logger logger = LoggerFactory.getLogger(PMS708.class);

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
			PMS708InputVO inputVO = (PMS708InputVO) body;
			PMS708OutputVO outputVO = new PMS708OutputVO();
			dam = this.getDataAccessManager();
			QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT T.YEARMON,                                      ");
			sql.append("       T.BRANCH_NBR,                                   ");
			sql.append("       T.EMP_ID,                                       ");
			sql.append("       T.CARD_TYPE,                                    ");
			sql.append("       T.CNT                                           ");
			sql.append("FROM TBPMS_LOAN_BONUS_UPLOAD T                         ");
			sql.append("LEFT JOIN TBORG_DEFN D ON T.BRANCH_NBR = D.DEPT_ID     ");
			sql.append("LEFT JOIN TBORG_DEFN C ON C.DEPT_ID = D.PARENT_DEPT_ID ");
			
			if(!StringUtils.isBlank(inputVO.getYearMon()))
			{
				sql.append("WHERE TRIM(T.YEARMON) = :yearMon                         ");
				condition.setObject("yearMon",inputVO.getYearMon().trim());
				sql.append("ORDER BY C.PARENT_DEPT_ID,C.DEPT_ID,T.BRANCH_NBR,T.EMP_ID");
			}
			
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
	
	//從excel表格中新增數據
	@SuppressWarnings({ "rawtypes", "unused" })
	public void addData(Object body, IPrimitiveMap header) throws APException
	{	
		int flag = 0;
		try {
			PMS708InputVO inputVO = (PMS708InputVO) body;
			PMS708OutputVO outputVO = new PMS708OutputVO();
			List<String> import_file = new ArrayList<String>();
			List<String> list =  new ArrayList<String>();
			String joinedPath = new File((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), inputVO.getFileName()).toString();
			Workbook workbook = Workbook.getWorkbook(new File(joinedPath));
			Sheet sheet[] = workbook.getSheets();
			//有表頭.xls文檔
			//清空臨時表
			dam = this.getDataAccessManager();
			QueryConditionIF dcon = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);	
			String dsql = " TRUNCATE TABLE TBPMS_LOAN_BONUS_UPLOAD_U ";
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
					
					//判斷當前上傳數據月份是否一致
					/*if(!list.get(0).equals(inputVO.getYearMon())){
						throw new APException("上傳數據選擇月份不一致");
					}*/
					
					//判斷當前上傳數據欄位個數是否一致
					if(list.size()!=5){
						throw new APException("上傳數據欄位個數不一致");
					}
					
					//SQL指令
					StringBuffer sb = new StringBuffer();
					dam = this.getDataAccessManager();
					QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sb.append("   INSERT INTO TBPMS_LOAN_BONUS_UPLOAD_U (YEARMON,	 ");
					sb.append("  		BRANCH_NBR,            				         ");
					sb.append("  		EMP_ID,           					         ");
					sb.append("  		CARD_TYPE,            					     ");
					sb.append("  		CNT,            					         ");
					sb.append("  		RNUM,            					         ");
					sb.append("  		VERSION,            						 ");
					sb.append("  		CREATETIME,             					 ");
					sb.append("  		CREATOR,             						 ");
					sb.append("  		MODIFIER,         						     ");
					sb.append("  		LASTUPDATE )             					 ");
					sb.append("  	VALUES(:YEARMON,            				     ");
					sb.append("  		:BRANCH_NBR,             				     ");
					sb.append("  		:EMP_ID,             					     ");
					sb.append("  		:CARD_TYPE,             					 ");
					sb.append("  		:CNT,             					         ");
					sb.append("  		:RNUM,             					         ");
					sb.append("  		:VERSION,           					     ");
					sb.append("  		SYSDATE,           				             ");
					sb.append("  		:CREATOR,            					     ");
					sb.append("  		:MODIFIER,         					         ");
					sb.append("  		SYSDATE)          				             ");
					qc.setObject("YEARMON",list.get(0).trim()                         );
					qc.setObject("BRANCH_NBR",list.get(1).trim()                      );
					qc.setObject("EMP_ID",list.get(2).trim()                          );
					qc.setObject("CARD_TYPE",list.get(3).trim()                       );
					qc.setObject("CNT",list.get(4).trim()                             );
					qc.setObject("RNUM",flag                                          );
					qc.setObject("VERSION","0"                                        );
					qc.setObject("CREATOR", inputVO.getUserId()                       );
					qc.setObject("MODIFIER", inputVO.getUserId()                      );
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
			PMS708InputVO inputVO = (PMS708InputVO) body;
			PMS708OutputVO outputVO = new PMS708OutputVO();
			//執行存儲過程
			dam = this.getDataAccessManager();
			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
			sb.append(" CALL PABTH_BTPMS721.SP_TBPMS_LOAN_BONUS_UPLOAD(? ,? ) ");
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
	 * 下載
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void downLoad(Object body, IPrimitiveMap header) throws Exception {
//		PMS713OutputVO return_VO2 = (PMS713OutputVO) body;
//		File filePath = new File(DataManager.getRealPath(), DataManager.getSystem().getPath().get("temp").toString());

		notifyClientToDownloadFile("doc//PMS//PMS708_EXAMPLE.xls", "專員信用卡業績上傳_上傳範例.xls"); //download
//		System.out.println(DataManager.getRealPath()+"doc\\PMS\\業務主管員額清單_上傳範例.xls");
		
		this.sendRtnObject(null);
	}
}
