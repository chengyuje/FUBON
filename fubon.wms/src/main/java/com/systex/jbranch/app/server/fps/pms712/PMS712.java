package com.systex.jbranch.app.server.fps.pms712;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.report.reportdata.ConfigAdapterIF;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * Copy Right Information : Forms Syntron <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : 專員信用卡業績上傳Controller<br>
 * Comments Name : PMS712.java<br>
 * Author : cty<br>
 * Date :2016年11月17日 <br>
 * Version : 1.0 <br>
 * Editor : cty<br>
 * Editor Date : 2016年11月17日<br>
 */
@Component("pms712")
@Scope("request")
public class PMS712 extends FubonWmsBizLogic
{
	public DataAccessManager dam = null;

	private Logger logger = LoggerFactory.getLogger(PMS712.class);

	/**
	 * 上傳文件模板初始化
	 * @param body
	 * @param header
	 * @throws APException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void getModel(Object body, IPrimitiveMap header) throws APException
	{
		try
		{
			PMS712OutputVO outputVO = new PMS712OutputVO();
			dam = this.getDataAccessManager();
			QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT UPLOAD_ID          ");
			sql.append("FROM TBPMS_UPLOAD_MAST    ");
			sql.append("ORDER BY NUM              ");
			condition.setQueryString(sql.toString());
			// result
			List<Map<String, Object>> resultList = dam.exeQuery(condition);
			outputVO.setResultList(resultList);
			this.sendRtnObject(outputVO);
		}
		catch (Exception e)
		{
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			throw new APException(e.getMessage());
		}
	
	}
	
	/**
	 * 查看上傳文件模板
	 * @param body
	 * @param header
	 * @throws APException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void queryModel(Object body, IPrimitiveMap header) throws APException
	{
		try
		{
			PMS712InputVO inputVO = (PMS712InputVO) body;
			PMS712OutputVO outputVO = new PMS712OutputVO();
			dam = this.getDataAccessManager();
			QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT *                      ");
			sql.append("FROM TBPMS_UPLOAD_MAST        ");
			sql.append("WHERE UPLOAD_ID = :UPLOAD_ID  ");
			condition.setObject("UPLOAD_ID", inputVO.getModelName());
			condition.setQueryString(sql.toString());
			List<Map<String, Object>> resultList = dam.exeQuery(condition);
			outputVO.setResultList(resultList);; 
			this.sendRtnObject(outputVO);
		}
		catch (Exception e)
		{
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			throw new APException(e.getMessage());
		}
	
	}
	
	/**
	 * 下載上傳文件模板
	 * @param body
	 * @param header
	 * @throws JBranchException
	 * @throws IOException 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes", "resource", "unused" })
	public void downloadModel(Object body, IPrimitiveMap header) throws JBranchException, IOException {
		try
		{
			PMS712InputVO inputVO = (PMS712InputVO) body;
			dam = this.getDataAccessManager();
			QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT *                      ");
			sql.append("FROM TBPMS_UPLOAD_MAST        ");
			sql.append("WHERE UPLOAD_ID = :UPLOAD_ID  ");
			condition.setObject("UPLOAD_ID", inputVO.getModelName());
			condition.setQueryString(sql.toString());
			List<Map<String, Object>> list = dam.exeQuery(condition);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			String fileName = inputVO.getModelName()+"模板" + sdf.format(new Date()) + ".xls"; 
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet("Sample sheet");
			//第一行數據
			//cell00.setCellType(XSSFCell.CELL_TYPE_STRING);
			HSSFRow row0 = sheet.createRow(0);
			if(list.get(0).get("C1NAME")!=null){
				HSSFCell cell00 = row0.createCell(0);
				cell00.setCellValue(new HSSFRichTextString((String) list.get(0).get("C1NAME")));
			}
			
			if(list.get(0).get("C2NAME")!=null){
				HSSFCell cell01 = row0.createCell(1);
				cell01.setCellValue(new HSSFRichTextString((String) list.get(0).get("C2NAME")));
			}
			
			if(list.get(0).get("C3NAME")!=null){
				HSSFCell cell02 = row0.createCell(2);
				cell02.setCellValue(new HSSFRichTextString((String) list.get(0).get("C3NAME")));
			}
			
			if(list.get(0).get("C4NAME")!=null){
				HSSFCell cell03 = row0.createCell(3);
				cell03.setCellValue(new HSSFRichTextString((String) list.get(0).get("C4NAME")));
			}
			
			if(list.get(0).get("C5NAME")!=null){
				HSSFCell cell04 = row0.createCell(4);
				cell04.setCellValue(new HSSFRichTextString((String) list.get(0).get("C5NAME")));
			}
			
			if(list.get(0).get("C6NAME")!=null){
				HSSFCell cell05 = row0.createCell(5);
				cell05.setCellValue(new HSSFRichTextString((String) list.get(0).get("C6NAME")));
			}
			
			if(list.get(0).get("C7NAME")!=null){
				HSSFCell cell06 = row0.createCell(6);
				cell06.setCellValue(new HSSFRichTextString((String) list.get(0).get("C7NAME")));
			}
			
			if(list.get(0).get("C8NAME")!=null){
				HSSFCell cell07 = row0.createCell(7);
				cell07.setCellValue(new HSSFRichTextString((String) list.get(0).get("C8NAME")));
			}
			
			if(list.get(0).get("C9NAME")!=null){
				HSSFCell cell08 = row0.createCell(8);
				cell08.setCellValue(new HSSFRichTextString((String) list.get(0).get("C9NAME")));
			}
			
			if(list.get(0).get("C10NAME")!=null){
				HSSFCell cell09 = row0.createCell(9);
				cell09.setCellValue(new HSSFRichTextString((String) list.get(0).get("C10NAME")));
			}
			
			if(list.get(0).get("C11NAME")!=null){
				HSSFCell cell010 = row0.createCell(10);
				cell010.setCellValue(new HSSFRichTextString((String) list.get(0).get("C11NAME")));
			}
			
			if(list.get(0).get("C12NAME")!=null){
				HSSFCell cell011 = row0.createCell(11);
				cell011.setCellValue(new HSSFRichTextString((String) list.get(0).get("C12NAME")));
			}
			
			if(list.get(0).get("C13NAME")!=null){
				HSSFCell cell012 = row0.createCell(12);
				cell012.setCellValue(new HSSFRichTextString((String) list.get(0).get("C13NAME")));
			}
			
			if(list.get(0).get("C14NAME")!=null){
				HSSFCell cell013 = row0.createCell(13);
				cell013.setCellValue(new HSSFRichTextString((String) list.get(0).get("C14NAME")));
			}
			
			if(list.get(0).get("C15NAME")!=null){
				HSSFCell cell014 = row0.createCell(14);
				cell014.setCellValue(new HSSFRichTextString((String) list.get(0).get("C15NAME")));
			}
			
			if(list.get(0).get("C16NAME")!=null){
				HSSFCell cell015 = row0.createCell(15);
				cell015.setCellValue(new HSSFRichTextString((String) list.get(0).get("C16NAME")));
			}
			
			if(list.get(0).get("C17NAME")!=null){
				HSSFCell cell016 = row0.createCell(16);
				cell016.setCellValue(new HSSFRichTextString((String) list.get(0).get("C17NAME")));
			}
			
			if(list.get(0).get("C18NAME")!=null){
				HSSFCell cell017 = row0.createCell(17);
				cell017.setCellValue(new HSSFRichTextString((String) list.get(0).get("C18NAME")));
			}
			
			if(list.get(0).get("C19NAME")!=null){
				HSSFCell cell018 = row0.createCell(18);
				cell018.setCellValue(new HSSFRichTextString((String) list.get(0).get("C19NAME")));
			}
			
			if(list.get(0).get("C20NAME")!=null&&list.get(0).get("C4NAME")!=""){
				HSSFCell cell019 = row0.createCell(19);
				cell019.setCellValue(new HSSFRichTextString((String) list.get(0).get("C20NAME")));
			}
			
			//第二行數據
			HSSFRow row1 = sheet.createRow(1);
			if(list.get(0).get("C1TYPE")!=null){
				HSSFCell cell10 = row1.createCell(0);
				cell10.setCellValue(new HSSFRichTextString((String) list.get(0).get("C1TYPE")));
			}
			
			if(list.get(0).get("C2TYPE")!=null){
				HSSFCell cell11 = row1.createCell(1);
				cell11.setCellValue(new HSSFRichTextString((String) list.get(0).get("C2TYPE")));
			}
			
			if(list.get(0).get("C3TYPE")!=null){
				HSSFCell cell12 = row1.createCell(2);
				cell12.setCellValue(new HSSFRichTextString((String) list.get(0).get("C3TYPE")));
			}
			
			if(list.get(0).get("C4TYPE")!=null){
				HSSFCell cell13 = row1.createCell(3);
				cell13.setCellValue(new HSSFRichTextString((String) list.get(0).get("C4TYPE")));
			}
			
			if(list.get(0).get("C5TYPE")!=null){
				HSSFCell cell14 = row1.createCell(4);
				cell14.setCellValue(new HSSFRichTextString((String) list.get(0).get("C5TYPE")));
			}
			
			if(list.get(0).get("C6TYPE")!=null){
				HSSFCell cell15 = row1.createCell(5);
				cell15.setCellValue(new HSSFRichTextString((String) list.get(0).get("C6TYPE")));
			}
			
			if(list.get(0).get("C7TYPE")!=null){
				HSSFCell cell16 = row1.createCell(6);
				cell16.setCellValue(new HSSFRichTextString((String) list.get(0).get("C7TYPE")));
			}
			
			if(list.get(0).get("C8TYPE")!=null){
				HSSFCell cell17 = row1.createCell(7);
				cell17.setCellValue(new HSSFRichTextString((String) list.get(0).get("C8TYPE")));
			}
			
			if(list.get(0).get("C9TYPE")!=null){
				HSSFCell cell18 = row1.createCell(8);
				cell18.setCellValue(new HSSFRichTextString((String) list.get(0).get("C9TYPE")));
			}
			
			if(list.get(0).get("C10TYPE")!=null){
				HSSFCell cell19 = row1.createCell(9);
				cell19.setCellValue(new HSSFRichTextString((String) list.get(0).get("C10TYPE")));
			}
			
			if(list.get(0).get("C11TYPE")!=null){
				HSSFCell cell110 = row1.createCell(10);
				cell110.setCellValue(new HSSFRichTextString((String) list.get(0).get("C11TYPE")));
			}
			
			if(list.get(0).get("C12TYPE")!=null){
				HSSFCell cell111 = row1.createCell(11);
				cell111.setCellValue(new HSSFRichTextString((String) list.get(0).get("C12TYPE")));
			}
			
			if(list.get(0).get("C13TYPE")!=null){
				HSSFCell cell112 = row1.createCell(12);
				cell112.setCellValue(new HSSFRichTextString((String) list.get(0).get("C13TYPE")));
			}
			
			if(list.get(0).get("C14TYPE")!=null){
				HSSFCell cell113 = row1.createCell(13);
				cell113.setCellValue(new HSSFRichTextString((String) list.get(0).get("C14TYPE")));
			}
			
			if(list.get(0).get("C15TYPE")!=null){
				HSSFCell cell114 = row1.createCell(14);
				cell114.setCellValue(new HSSFRichTextString((String) list.get(0).get("C15TYPE")));
			}
			
			if(list.get(0).get("C16TYPE")!=null){
				HSSFCell cell115 = row1.createCell(15);
				cell115.setCellValue(new HSSFRichTextString((String) list.get(0).get("C16TYPE")));
			}
			
			if(list.get(0).get("C17TYPE")!=null){
				HSSFCell cell116 = row1.createCell(16);
				cell116.setCellValue(new HSSFRichTextString((String) list.get(0).get("C17TYPE")));
			}
			
			if(list.get(0).get("C18TYPE")!=null){
				HSSFCell cell117 = row1.createCell(17);
				cell117.setCellValue(new HSSFRichTextString((String) list.get(0).get("C18TYPE")));
			}
			
			if(list.get(0).get("C19TYPE")!=null){
				HSSFCell cell118 = row1.createCell(18);
				cell118.setCellValue(new HSSFRichTextString((String) list.get(0).get("C19TYPE")));
			}
			
			if(list.get(0).get("C20TYPE")!=null){
				HSSFCell cell119 = row1.createCell(19);
				cell119.setCellValue(new HSSFRichTextString((String) list.get(0).get("C20TYPE")));
			}
			
			ConfigAdapterIF config = (ConfigAdapterIF)PlatformContext.getBean("configAdapter");
			final String path = (new StringBuilder()).append(config.getServerHome()).append(config.getReportTemp()).append(System.getProperties().getProperty("file.separator")).toString()+fileName;
			FileOutputStream fos = new FileOutputStream(path);
			workbook.write(fos);
			fos.flush();
			notifyClientToDownloadFile((new StringBuilder()).append(config.getReportTemp().substring(1)).append(fileName).toString(), fileName);
		}
		catch (Exception e)
		{
			logger.error("上傳文件模板下載失敗");
			throw new APException(e.getMessage());
		}
		
	}
}
