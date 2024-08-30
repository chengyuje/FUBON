package com.systex.jbranch.app.server.fps.exportfile;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.birt.report.model.api.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * @author walalala
 * @date 2016/08/02
 * 
 */
@Component("exportfile")
@Scope("request")
public class EXPORTFILE extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(EXPORTFILE.class);
	
	public void test(Object body, IPrimitiveMap header) throws JBranchException{
		EXPORTFILEInputVO inputVO = (EXPORTFILEInputVO) body;
		EXPORTFILEOutputVO outputVO = new EXPORTFILEOutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
	
		sql.append(inputVO.getSql());
		queryCondition.setQueryString(sql.toString());
		
		ResultIF list = dam.executePaging(queryCondition, inputVO
				.getCurrentPageIndex() + 1, inputVO.getPageCount());
		int totalPage_i = list.getTotalPage();
		int totalRecord_i = list.getTotalRecord();
		outputVO.setResultList(list);
		outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
		outputVO.setTotalPage(totalPage_i);// 總頁次
		outputVO.setTotalRecord(totalRecord_i);// 總筆數
		sendRtnObject(outputVO);
	}
	
	public void export(Object body, IPrimitiveMap header) throws JBranchException, IOException{
		EXPORTFILEInputVO inputVO = (EXPORTFILEInputVO) body;
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
	
		sql.append(inputVO.getSql());
		queryCondition.setQueryString(sql.toString());
		
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);

		if (list.size() == 0) {
			throw new JBranchException("ehl_01_common_009");
		}
		
		Map<String, String> file = new HashMap<String, String>();
		
		if (inputVO.getType().equals("1")) {
			file = exportxlsx("File", list);
		}else if (inputVO.getType().equals("2")){
			file = exportcsv("File", list, "N");
		}else {
			file = exportcsv("File", list, "Y");
		}
		
		this.sendRtnObject("downloadFile", file);
		
	}
	
	//匯出EXCEL
	public Map<String, String> exportxlsx(String name, List<Map<String, Object>> list) throws JBranchException, IOException{
		
		List<Collection<Object>> data = new ArrayList<>();
		for(int i = 0; i < list.size(); i++){
			data.add(list.get(i).values());
		}
		
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Sample sheet");
		
		CellStyle cellStyleFont = workbook.createCellStyle();
		XSSFFont font = workbook.createFont();
		font.setFontName("Serif");
		cellStyleFont.setFont(font);
		
		CellStyle cellStyleDate = workbook.createCellStyle();
		CreationHelper createHelper = workbook.getCreationHelper();
		cellStyleDate.setDataFormat(createHelper.createDataFormat().getFormat("yyyy/MM/dd h:mm:ss"));
		
		Row row_head = sheet.createRow(0);
		int cell_head_num = 0;
		for (String str : list.get(0).keySet()) {
			Cell cell_head = row_head.createCell(cell_head_num++);
			cell_head.setCellValue(str);
		}
		
		int rownum = 1;
		for (Collection<Object> objs : data) {
			Row row = sheet.createRow(rownum++);
			int cellnum = 0;
			for (Object obj : objs) {
				Cell cell = row.createCell(cellnum++);
				cell.setCellStyle(cellStyleFont);
				//sql String
				if (obj instanceof String)
					cell.setCellValue((String)obj);
				//sql Date
				else if(obj instanceof Date) {
					cell.setCellValue((Date)obj);
					cell.setCellStyle(cellStyleDate);
				}
				//sql Number
				else if(obj instanceof Number) {
					cell.setCellValue((getBigDecimal(obj)).doubleValue());
				}
				//sql Double
				else if(obj instanceof Double) {
					cell.setCellValue((Double)obj);
				}
				//sql Boolean
				else if(obj instanceof Boolean) {
					cell.setCellValue((Boolean)obj);
				}
				else if(obj instanceof Blob) {
					cell.setCellValue("檔案");
				}
				//sql Null
				else if(obj == null) {
					cell.setCellValue("(null)");
				}
				//sql undefined
				else {
					cell.setCellValue("ERROR");
				}
			}
		}

		//autoSizeColumn
		for (int i = 0; i < row_head.getPhysicalNumberOfCells(); i++) {
			sheet.autoSizeColumn(i);
		}
		
		String Path = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		String uuid = UUID.randomUUID().toString();
		String fileName = name + ".xlsx";
		String filePath = Path + uuid;

		workbook.write(new FileOutputStream(filePath));
		
		Map<String, String> params = new HashMap<String, String>();
        params.put("fileUrl", DataManager.getSystem().getPath().get("temp").toString() + uuid);
        params.put("defaultFileName", fileName);

		workbook.close();
		return (params);
	}
	
	//匯出EXCEL BY 排序
	public Map<String, String> exportxlsx(String name, List<Map<String, Object>> list, List<String> order) throws JBranchException, IOException{
		
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Sheet");
		
		CellStyle cellStyleFont = workbook.createCellStyle();
		XSSFFont font = workbook.createFont();
		font.setFontName("Serif");
		cellStyleFont.setFont(font);
		
		CellStyle cellStyleDate = workbook.createCellStyle();
		CreationHelper createHelper = workbook.getCreationHelper();
		cellStyleDate.setDataFormat(createHelper.createDataFormat().getFormat("yyyy/MM/dd h:mm:ss"));
		
		Row row_head = sheet.createRow(0);
		int cell_head_num = 0;
		for (String str : order) {
			Cell cell_head = row_head.createCell(cell_head_num++);
			cell_head.setCellValue(str);
		}
		
		int rownum = sheet.getLastRowNum();
		
		for (Map<String, Object> objs : list) {
			Row row = sheet.createRow(++rownum);
			
			for (Entry<String, Object> en : objs.entrySet()) {
				Object obj  = en.getValue();
				int idx = order.indexOf(en.getKey());
				
				if (idx >= 0) {
					Cell cell = row.createCell(idx);
					cell.setCellStyle(cellStyleFont);
					
					//sql String
					if (obj instanceof String)
						cell.setCellValue((String)obj);
					//sql Date
					else if(obj instanceof Date) {
						cell.setCellValue((Date)obj);
						cell.setCellStyle(cellStyleDate);
					}
					//sql Number
					else if(obj instanceof Number) {
						cell.setCellValue((getBigDecimal(obj)).doubleValue());
					}
					//sql Double
					else if(obj instanceof Double) {
						cell.setCellValue((Double)obj);
					}
					//sql Boolean
					else if(obj instanceof Boolean) {
						cell.setCellValue((Boolean)obj);
					}
					else if(obj instanceof Blob) {
						cell.setCellValue("檔案");
					}
					//sql Null
					else if(obj == null) {
						cell.setCellValue("(null)");
					}
					//sql undefined
					else {
						cell.setCellValue("ERROR");
					}
				}
			}
		}

		//autoSizeColumn
		for (int i = 0; i < row_head.getPhysicalNumberOfCells(); i++) {
			sheet.autoSizeColumn(i);
		}
		
		String Path = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		String uuid = UUID.randomUUID().toString();
		String fileName = name + ".xlsx";
		String filePath = Path + uuid;

		workbook.write(new FileOutputStream(filePath));
		
		Map<String, String> params = new HashMap<String, String>();
        params.put("fileUrl", DataManager.getSystem().getPath().get("temp").toString() + uuid);
        params.put("defaultFileName", fileName);

		workbook.close();
		return (params);
	}
	
	//匯出EXCEL BY 排序 AND 欄位名稱設定
	public Map<String, String> exportxlsx_cname(String name, List<Map<String, Object>> list, Map<String, String> order) throws JBranchException, IOException{
		
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Sheet");
		
		CellStyle cellStyleFont = workbook.createCellStyle();
		XSSFFont font = workbook.createFont();
		font.setFontName("Serif");
		cellStyleFont.setFont(font);
		
		CellStyle cellStyleDate = workbook.createCellStyle();
		CreationHelper createHelper = workbook.getCreationHelper();
		cellStyleDate.setDataFormat(createHelper.createDataFormat().getFormat("yyyy/MM/dd h:mm:ss"));
		
		Row row_head = sheet.createRow(0);
		int cell_head_num = 0;
		
		for (Entry<String, String> strs : order.entrySet()) {
			String str = strs.getValue();
			Cell cell_head = row_head.createCell(cell_head_num++);
			cell_head.setCellStyle(cellStyleFont);
			cell_head.setCellValue(str);
		}

		List<String> orderkey = new ArrayList<String>();
		for ( String key : order.keySet() ) {
			orderkey.add(key);
		}
		
		int rownum = sheet.getLastRowNum();
		
		for (Map<String, Object> objs : list) {
			Row row = sheet.createRow(++rownum);
			
			for (Entry<String, Object> en : objs.entrySet()) {
				Object obj  = en.getValue();
				int idx = orderkey.indexOf(en.getKey());
				
				if (idx >= 0) {
					Cell cell = row.createCell(idx);
					cell.setCellStyle(cellStyleFont);
					
					//sql String
					if (obj instanceof String) {
						cell.setCellValue((String)obj);
					//sql Date
					} else if(obj instanceof Date) {
						cell.setCellStyle(cellStyleDate);
						cell.setCellValue((Date)obj);
					}
					//sql Number
					else if(obj instanceof Number) {
						cell.setCellValue((getBigDecimal(obj)).doubleValue());
					}
					//sql Double
					else if(obj instanceof Double) {
						cell.setCellValue((Double)obj);
					}
					//sql Boolean
					else if(obj instanceof Boolean) {
						cell.setCellValue((Boolean)obj);
					}
					else if(obj instanceof Blob) {
						cell.setCellValue("檔案");
					}
					//sql Null
					else if(obj == null) {
						cell.setCellValue("(null)");
					}
					//sql undefined
					else {
						cell.setCellValue("ERROR");
					}
				}
			}
		}

		//autoSizeColumn
		for (int i = 0; i < row_head.getPhysicalNumberOfCells(); i++) {
			sheet.autoSizeColumn(i);
		}
		
		String Path = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		String uuid = UUID.randomUUID().toString();
		String fileName = name + ".xlsx";
		String filePath = Path + uuid;

		workbook.write(new FileOutputStream(filePath));
		
		Map<String, String> params = new HashMap<String, String>();
        params.put("fileUrl", DataManager.getSystem().getPath().get("temp").toString() + uuid);
        params.put("defaultFileName", fileName);
  
		workbook.close();
		return (params);
	}
	
	//匯出CSV
	public Map<String, String> exportcsv(String name, List<Map<String, Object>> list, String title) throws JBranchException, IOException{
		
		String Path = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		String uuid = UUID.randomUUID().toString();
		
		String fileName = name + ".csv";
		String filePath = Path + uuid;
		
		FileWriter writer = new FileWriter(filePath);

		if (StringUtil.isEqual(title, "Y")) {
			int i = 1;
			for ( String key : list.get(0).keySet() ) {
				writer.append(key);
				if (i++ == list.get(0).size()){
				}else{
					writer.append(',');
				}
				
			}
			writer.append("\r\n");
		}
		
		for(Map<String, Object> datas : list) {
			int i = 1;
			for(Object obj : datas.values()) {
				writer.append(obj == null ? "" : obj.toString());
				if (i++ == datas.values().size()){
				}else{
					writer.append(',');
				}
			}
			writer.append("\r\n");
		}

		Map<String, String> params = new HashMap<String, String>();
        params.put("fileUrl", DataManager.getSystem().getPath().get("temp").toString() + uuid);
        params.put("defaultFileName", fileName);
        
		writer.flush();
		writer.close();
		
		return (params);
	}
	
	//匯出CSV by order
	public Map<String, String> exportcsv(String name, List<Map<String, Object>> list, String title, List<String> order) throws JBranchException, IOException{
		
		String Path = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		String uuid = UUID.randomUUID().toString();
		
		String fileName = name + ".csv";
		String filePath = Path + uuid;
		
		FileWriter writer = new FileWriter(filePath);
		
		if (StringUtil.isEqual(title, "Y")) {
			int k = 1;
			for (String str : order) {
				writer.append(str);
				if (k++ == order.size()){
				}else{
					writer.append(',');
				}
			}
			writer.append("\r\n");
		}
		
		for (Map<String, Object> objs : list) {
			int i = 1;
			for (String key : order) {
				for (Entry<String, Object> en : objs.entrySet()) {
					if (key.equals(en.getKey())){
						Object obj  = en.getValue();
						writer.append(obj == null ? "" : obj.toString());
					}
				}
				if (i++ == order.size()){
				}else{
					writer.append(',');
				}
			}
			writer.append("\r\n");
		}

		Map<String, String> params = new HashMap<String, String>();
        params.put("fileUrl", DataManager.getSystem().getPath().get("temp").toString() + uuid);
        params.put("defaultFileName", fileName);
        
		writer.flush();
		writer.close();
		
		return (params);
	}
	
	//轉Decimal
	public BigDecimal getBigDecimal( Object value ) {
        BigDecimal ret = null;
        if( value != null ) {
            if( value instanceof BigDecimal ) {
                ret = (BigDecimal) value;
            } else if( value instanceof String ) {
                ret = new BigDecimal( (String) value );
            } else if( value instanceof BigInteger ) {
                ret = new BigDecimal( (BigInteger) value );
            } else if( value instanceof Number ) {
                ret = new BigDecimal( ((Number)value).doubleValue() );
            } else {
                throw new ClassCastException("Not possible to coerce ["+value+"] from class "+value.getClass()+" into a BigDecimal.");
            }
        }
        return ret;
    }
	
}