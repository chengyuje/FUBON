package com.systex.jbranch.app.server.fps.importfile;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.supercsv.io.CsvListReader;
import org.supercsv.prefs.CsvPreference;

import com.systex.jbranch.app.common.fps.table.TBSYS_XLS_IMP_MAPPK;
import com.systex.jbranch.app.common.fps.table.TBSYS_XLS_IMP_MAPVO;
import com.systex.jbranch.app.common.fps.table.TBSYS_XLS_IMP_SETVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * @author walalala
 * @date 2016/07/05
 * @version 2017/01/16 1500617
 * 
 */
@Component("importfile")
@Scope("request")
public class IMPORTFILE extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(IMPORTFILE.class);
	
	private String setCode;  // new Object(){}.getClass().getEnclosingMethod().getName();
	private String fileName;
	private boolean isPlatformTable = true;
	
	public void importfile(Object body, IPrimitiveMap<?> header) throws JBranchException {
		IMPORTFILEInputVO inputVO = (IMPORTFILEInputVO) body;
		importFileImpl(inputVO.getSet_code(), inputVO.getFileName());
		this.sendRtnObject(null);
	}
	
	public void callImportFileImpl(StackTraceElement[] stackAry, String fileName) throws JBranchException {
		String fullClassName = stackAry[1].getClassName();
		String simpleClassName = fullClassName.substring(fullClassName.lastIndexOf('.') + 1);
		String code = simpleClassName + "." + stackAry[1].getMethodName();
		
		importFileImpl(code, fileName);
	}
	
	/**
	 * 
	 * @param code [className].[methodName] from producer 
	 * @param fileName
	 * @throws JBranchException
	 */
	public void importFileImpl(String code, String fileName) throws JBranchException {
		this.setCode = code;
		this.fileName = fileName;
		
		// 取得設定資料
		List<Map<String, Object>> list = findSetting(code);
		
		// 解析檔案
		Map<Integer, List<String>> dataMap = new LinkedHashMap<Integer, List<String>>();
		String ext = fileName.substring(fileName.lastIndexOf(46) + 1, fileName.length());
		
		if (ext.indexOf("xls") != -1) {
			dataMap = parseExcel(list);
		} else if (ext.indexOf("csv") != -1) {
			dataMap = parseCsv(list); // FIXME
		}
		
		// 上傳 db
		importData(dataMap);
	}
	
	/**
	 * 從 TBSYS_XLS_IMP_SET 取得設定
	 * @param name
	 * @return
	 * @throws JBranchException
	 */
	public List<Map<String, Object>> findSetting(String setCode) throws JBranchException {
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT * FROM TBSYS_XLS_IMP_SET WHERE code = :code");
		condition.setObject("code", setCode);	
		condition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(condition);
		
		if (list.size() == 0) {
			throw new JBranchException("無設定檔案");
		}
		
		return list;
	}
	
	/**
	 * 從 TBSYS_XLS_IMP_SET 取得設定 ID
	 * @param name
	 * @return
	 * @throws JBranchException
	 */
	public List<String> findSettingID(String setCode) throws JBranchException {
		List<String> list = new ArrayList<String>();
		
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sql = new StringBuffer();
		sql.append("select ID from TBSYS_XLS_IMP_SET where CODE = :code");
		condition.setObject("code", setCode);	
		condition.setQueryString(sql.toString());
		List<Map<String, Object>> listData = dam.exeQuery(condition);
		
		for (Map<String, Object> map : listData) {
			if (map.get("ID") != null) {
				list.add((String)map.get("ID"));
			}
		}
		
		return list;
	}
	
	/**
	 * 解析 excel 檔案
	 * @param list
	 * @return
	 * @throws JBranchException
	 */
	public Map<Integer, List<String>> parseExcel(List<Map<String, Object>> list)
			throws JBranchException {
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		Workbook wb = null;
		int sheet_idx = 0;
		int count = 0;
		
		Map<Integer, List<String>> dataMap = new LinkedHashMap<Integer, List<String>>();
		
		try {
			File joinedFile = new File(
					(String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), fileName);
			wb = WorkbookFactory.create(joinedFile);
			DataFormatter formatter = new DataFormatter();
			
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT * FROM TBSYS_XLS_IMP_MAP WHERE ID = (SELECT ID FROM TBSYS_XLS_IMP_SET WHERE CODE = :code AND SHEET_IDX = :sheet ) ");
			condition.setQueryString(sql.toString());
			
			for (int i = 0; i < list.size(); i++) {
				List<String> import_file = new ArrayList<String>();
				
				String table_name = list.get(i).get("TABLE_NAME").toString();
				sheet_idx = Integer.parseInt(list.get(i).get("SHEET_IDX").toString());
				String has_header = list.get(i).get("HAS_HEAD").toString();
				
				condition.setObject("code", setCode);
				condition.setObject("sheet", sheet_idx);
				List<Map<String, Object>> list_map = dam.exeQuery(condition);

				Sheet sheet = wb.getSheetAt(sheet_idx - 1);
				Iterator<Row> itr = sheet.iterator();
				count = 0;
				
				while (itr.hasNext()) {
					String userID = (String)SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINID);
					count++;
					Row row = itr.next();
					
					// 有無表頭判定
					if (has_header.equals("1") && row.getRowNum() == 0) {
						continue;
					}
					
					List<Object> rowList = new ArrayList<Object>();
					Iterator<Cell> cellIterator = row.cellIterator();
					
					while (cellIterator.hasNext()) {
						Cell cell = cellIterator.next();
						rowList.add(formatter.formatCellValue(cell));
					}

					List<Object> col_name = new ArrayList<Object>();
					List<Object> value = new ArrayList<Object>();

					for (int j = 0; j < list_map.size(); j++) {
						int src_col_idx = Integer.parseInt(list_map.get(j).get("SRC_COL_IDX").toString()) - 1;
						String tar_col_name = list_map.get(j).get("TAR_COL_NAME").toString();
						int tar_data_type = Integer.parseInt(list_map.get(j).get("TAR_DATA_TYPE").toString());

						col_name.add(tar_col_name);
						Object data = rowList.get(src_col_idx);
						
						// 轉換並檢查型態
						if (tar_data_type == 1) { // varchar2
							data = "'" + data + "'";
						} else if (tar_data_type == 2) { // number(整數)
							try {
								if (data instanceof Double) {
									data = new Double((double) data).intValue();
								}
								
								Integer.parseInt(data + "");
							} catch (NumberFormatException e) {
								throw new JBranchException("數字格式不符");
							}
						} else if (tar_data_type == 4) { // number(浮點數)
							try {
								Float.parseFloat(data + "");
							} catch (NumberFormatException e) {
								throw new JBranchException("數字格式不符");
							}
						} else if (tar_data_type == 3) { // date
							try {
								DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
								df.setLenient(false);
								df.parse((String)data);

								data = "TO_DATE('" + data.toString() + "','yyyy/MM/dd')";
							} catch (ParseException e) {
								throw new JBranchException("日期格式不符");
							}
						} else {
							throw new JBranchException("沒設定欄位型態");
						}
						value.add(data);
					}
					
					// add platform column
					if (isPlatformTable) {
						col_name.add("VERSION");
						col_name.add("CREATETIME");
						col_name.add("CREATOR");
						col_name.add("MODIFIER");
						col_name.add("LASTUPDATE");
						
						value.add(0);
						value.add("SYSDATE");
						value.add("'" + userID + "'");
						value.add("'" + userID + "'");
						value.add("SYSDATE");
					}
					
					// 移除 "[", "]"
					String col_nameStr = col_name.toString();
					col_nameStr = col_nameStr.substring(1, col_nameStr.length() - 1);
					String valueStr = value.toString();
					valueStr = valueStr.substring(1, valueStr.length() - 1);
					
					// SQL指令
					String import_sql = String.format("insert into %S (%S) values (%S)",
							new Object[] {table_name, col_nameStr, valueStr});
					import_file.add(import_sql);
				}
				
				dataMap.put(sheet_idx, import_file);
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new JBranchException(e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new JBranchException("第 " + sheet_idx + " 工作表第 " + count + " 筆資料發生如下錯誤: " + e.getMessage());
		} finally {
			if (wb != null) {
				try {
					wb.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
				}
			}
		}
		
		return dataMap;
	}
	
	public Map<Integer, List<String>> parseCsv(List<Map<String, Object>> list) throws JBranchException {
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		CsvListReader csvReader = null;
		int sheet_idx = 0;
		int count = 0;
		Map<Integer, List<String>> dataMap = new LinkedHashMap<Integer, List<String>>();
		
		try {
			File joinedFile = new File(
					(String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), fileName);
			
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT * FROM TBSYS_XLS_IMP_MAP WHERE ID = (SELECT ID FROM TBSYS_XLS_IMP_SET WHERE CODE = :code AND SHEET_IDX = :sheet ) ");
			condition.setQueryString(sql.toString());
			
			for (int i = 0; i < list.size(); i++) {
				List<String> import_file = new ArrayList<String>();
				
				String table_name = list.get(i).get("TABLE_NAME").toString();
				sheet_idx = Integer.parseInt(list.get(i).get("SHEET_IDX").toString());
				String has_header = list.get(i).get("HAS_HEAD").toString();
				
				condition.setObject("code", setCode);
				condition.setObject("sheet", sheet_idx);
				List<Map<String, Object>> list_map = dam.exeQuery(condition);

				csvReader = new CsvListReader(new FileReader(joinedFile), CsvPreference.STANDARD_PREFERENCE);
				    
				//Read CSV Header
				if ("1".equals(has_header)) {
					List<String> headers = new ArrayList<String>(csvReader.read());
				}
				
				List<String> rowList;

				while ((rowList = csvReader.read()) != null) {

					List<String> col_name = new ArrayList<String>();
					List<String> value = new ArrayList<String>();
						
					for (int j = 0; j < list_map.size(); j++) {
						int src_col_idx = Integer.parseInt(list_map.get(j).get("SRC_COL_IDX").toString()) - 1;

						String tar_col_name = list_map.get(j).get("TAR_COL_NAME").toString();
						String data = "";
						if (data != null) {
							data = "'" + data + "'";
							col_name.add(tar_col_name);
							value.add(data);
						}
					}
					
					//SQL指令
					String import_sql = String.format("insert into %S (%S) values (%S)",new Object[]{table_name, col_name.toString().replace("[", "").replace("]", ""), value.toString().replace("[", "").replace("]", "")});
					import_file.add(import_sql);
					System.out.println(import_sql);
				}
				
				dataMap.put(sheet_idx, import_file);
			}
		} catch(Exception e) {
        	logger.error(e.getMessage(), e);
        	throw new JBranchException(e.getMessage());
		} finally {
			if (csvReader != null) {
				try {
					csvReader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
				}
			}
		}
		
		return dataMap;
	}
	
	/**
	 * 匯入資料
	 * @param dataMap
	 * @throws JBranchException
	 */
	public void importData(Map<Integer, List<String>> dataMap) throws JBranchException {
		for (Entry<Integer, List<String>> en : dataMap.entrySet()) {
			int sheetIdx = en.getKey();
			List<String> import_file = en.getValue();
			
			for (int i = 0; i < import_file.size(); i++) {
				try {
					xlsImport(import_file.get(i));
				} catch (Exception e) {
					throw new JBranchException("在第" + sheetIdx + "工作表, 第" + (i+1) + "筆資料, 匯入資料庫錯誤 ");
				}
			}
		}
	}
	
	//IMPORT
	public void xlsImport(String sql) throws JBranchException {
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		condition.setQueryString(sql.toString());
		dam.exeUpdate(condition);
	}
	
	//抓取方法名稱
	public void initial(Object body, IPrimitiveMap<?> header) throws JBranchException{
		IMPORTFILEOutputVO return_VO = new IMPORTFILEOutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT distinct NAME, CODE FROM TBSYS_XLS_IMP_SET order by NAME ");
		condition.setQueryString(sql.toString());
		// result
		List<Map<String, Object>> list = dam.exeQuery(condition);
		return_VO.setNameList(list);
		this.sendRtnObject(return_VO);
	}
	
	//設定
	public void detail(Object body, IPrimitiveMap<?> header) throws JBranchException{
		IMPORTFILEInputVO inputVO = (IMPORTFILEInputVO) body;
		IMPORTFILEOutputVO return_VO = new IMPORTFILEOutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT ID, NAME, CODE, SHEET_IDX, TABLE_NAME, HAS_HEAD ");
		sql.append("FROM TBSYS_XLS_IMP_SET WHERE CODE = :code ORDER BY SHEET_IDX");
		
		QueryConditionIF condition2 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql2 = new StringBuffer();
		sql2.append("SELECT A.ID, SRC_COL_IDX, TAR_COL_NAME, TAR_DATA_TYPE FROM TBSYS_XLS_IMP_MAP A ");
		sql2.append("LEFT JOIN TBSYS_XLS_IMP_SET B ON A.ID = B.ID WHERE CODE = :code ");
		
		condition.setQueryString(sql.toString());
		condition.setObject("code", inputVO.getSet_code());	
		condition2.setQueryString(sql2.toString());
		condition2.setObject("code", inputVO.getSet_code());
		
		// result
		List<Map<String, Object>> ResultList = dam.exeQuery(condition);
		List<Map<String, Object>> ResultList2 = dam.exeQuery(condition2);

		for (int i = 0; i < ResultList.size(); i++) {
			List<Map<String, Object>> temp = new ArrayList<Map<String,Object>>();
			Map<String, Object> map = new HashMap<String, Object>();
			
			map.put("HAS_HEAD", ResultList.get(i).get("HAS_HEAD"));
			map.put("TABLE_NAME", ResultList.get(i).get("TABLE_NAME"));
			temp.add(map);
			
			ResultList.get(i).put("settings",temp);
			ResultList.get(i).remove("HAS_HEAD");
			ResultList.get(i).remove("TABLE_NAME");
			
			List<Map<String, Object>> temp2 = new ArrayList<Map<String,Object>>();
			for (int j= 0; j < ResultList2.size(); j++) {
				if (ResultList.get(i).get("ID").equals(ResultList2.get(j).get("ID"))) {
					temp2.add(ResultList2.get(j));
					ResultList.get(i).put("content", temp2);
				}
			}
		}
		
		return_VO.setResultList(ResultList);
		this.sendRtnObject(return_VO);
	}
	
	// save setting
	public void save(Object body, IPrimitiveMap<?> header) throws JBranchException{
		IMPORTFILEInputVO inputVO = (IMPORTFILEInputVO) body;
		
		dam = this.getDataAccessManager();
		String id = "";
		String code = inputVO.getSet_code();
		String name = inputVO.getSet_name();
		
		List<String> delIdList = findSettingID(code);
		
		for (int i = 0; i < inputVO.getList().size(); i++) {
			Map<String, Object> inputListMap = inputVO.getList().get(i);
			
			List<Map<String, Object>> settingMap = new ArrayList<Map<String,Object>>();
			settingMap = (List<Map<String, Object>>) inputListMap.get("settings");
			
			BigDecimal sheetIdx = getBigDecimal(inputListMap.get("SHEET_IDX"));
			String table = (String)settingMap.get(0).get("TABLE_NAME");
			BigDecimal hasHead = getBigDecimal(settingMap.get(0).get("HAS_HEAD"));
			
			List<Map<String, Object>> contentMap = new ArrayList<Map<String,Object>>();
			contentMap = (List<Map<String, Object>>) inputListMap.get("content");
			
			TBSYS_XLS_IMP_SETVO vos_set = new TBSYS_XLS_IMP_SETVO();
			
			// 修改
			if (inputListMap.containsKey("ID")) {
				id = (String)inputListMap.get("ID");
				delIdList.remove(id);
				
				vos_set = (TBSYS_XLS_IMP_SETVO)dam.findByPKey(TBSYS_XLS_IMP_SETVO.TABLE_UID, id);
				vos_set.setCODE(code);
				vos_set.setNAME(name);
				vos_set.setSHEET_IDX(sheetIdx);
				vos_set.setTABLE_NAME(table);
				vos_set.setHAS_HEAD(hasHead);
				dam.update(vos_set);
				
				// 先刪除再新增
				deleteMapData(Arrays.asList(id));
			// 新增	
			} else {
				id = UUID.randomUUID().toString();
				
				vos_set = new TBSYS_XLS_IMP_SETVO();
				vos_set.setID(id);
				vos_set.setCODE(code);
				vos_set.setNAME(name);
				vos_set.setSHEET_IDX(sheetIdx);
				vos_set.setTABLE_NAME(table);
				vos_set.setHAS_HEAD(hasHead);
			    dam.create(vos_set);
			}
			
		    for (int j = 0; j < contentMap.size(); j++) {
		    	TBSYS_XLS_IMP_MAPVO vos_map = new TBSYS_XLS_IMP_MAPVO();
		    	TBSYS_XLS_IMP_MAPPK pk_map = new TBSYS_XLS_IMP_MAPPK();
		    	
		    	pk_map.setID(id);
		    	pk_map.setSRC_COL_IDX(getBigDecimal(contentMap.get(j).get("SRC_COL_IDX")));
		    	vos_map.setTAR_COL_NAME(contentMap.get(j).get("TAR_COL_NAME").toString());
		    	vos_map.setTAR_DATA_TYPE(getBigDecimal(contentMap.get(j).get("TAR_DATA_TYPE")));
		    	vos_map.setcomp_id(pk_map);
		    	dam.create(vos_map);
			}
		}
		
		// 刪除未在頁面tab上的資料
		if (delIdList.size() > 0) {
			deleteSetData(delIdList);
			deleteMapData(delIdList);
		}
		
		this.sendRtnObject(null);
	}
	
	// delete
	public void delete(Object body, IPrimitiveMap<?> header) throws JBranchException{
		IMPORTFILEInputVO inputVO = (IMPORTFILEInputVO) body;
		
		List<String> list = findSettingID(inputVO.getSet_code());
		deleteData(list);
		
		this.sendRtnObject(null);
	}
	
	/**
	 * 刪除 TBSYS_XLS_IMP_SET, TBSYS_XLS_IMP_MAP 資料
	 * @param code
	 * @throws JBranchException
	 */
	public void deleteData(List<String> list) throws JBranchException{
		deleteSetData(list);
		deleteMapData(list);
	}
	
	/**
	 * 刪除 TBSYS_XLS_IMP_SET 資料
	 * @param code
	 * @throws JBranchException
	 */
	public void deleteSetData(List<String> list) throws JBranchException{
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		condition.setObject("id", list);	
		condition.setQueryString("delete TBSYS_XLS_IMP_SET where ID in :id");
		dam.exeUpdate(condition);
	}
	
	/**
	 * 刪除 TBSYS_XLS_IMP_MAP 資料
	 * @param code
	 * @throws JBranchException
	 */
	public void deleteMapData(List<String> list) throws JBranchException{
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		condition.setObject("id", list);	
		condition.setQueryString("delete TBSYS_XLS_IMP_MAP where ID in :id");
		dam.exeUpdate(condition);
	}
	
	// 轉Decimal
	public BigDecimal getBigDecimal(Object value) {
		BigDecimal ret = null;
		if (value != null) {
			if (value instanceof BigDecimal) {
				ret = (BigDecimal) value;
			} else if (value instanceof String) {
				ret = new BigDecimal((String) value);
			} else if (value instanceof BigInteger) {
				ret = new BigDecimal((BigInteger) value);
			} else if (value instanceof Number) {
				ret = new BigDecimal(((Number) value).doubleValue());
			} else {
				throw new ClassCastException("Not possible to coerce [" + value
						+ "] from class " + value.getClass()
						+ " into a BigDecimal.");
			}
		}
		return ret;
	}
	
}