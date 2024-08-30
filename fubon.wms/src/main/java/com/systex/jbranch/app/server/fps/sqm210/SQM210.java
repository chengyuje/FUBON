package com.systex.jbranch.app.server.fps.sqm210;

import com.systex.jbranch.fubon.commons.ExcelUtil;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

/**
 * Copy Right Information :<br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description :績效評核-個人扣分<br>
 * Comments Name : SQM210.java<br>
 * Author : Sam<br>
 * Date :2018/03/9 <br>
 * Version : 1.0 <br>
 */

@Component("sqm210")
@Scope("request")
public class SQM210 extends FubonWmsBizLogic {
	
	private DataAccessManager dam = null;
	private Logger logger = Logger.getLogger(SQM210.class);

	public void inquire (Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		
		SQM210InputVO inputVO = (SQM210InputVO) body;
		SQM210OutputVO outputVO = new SQM210OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		queryData(condition, inputVO);
//		ResultIF list = dam.executePaging(condition,inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
		List<Map<String,Object>> list = (ResultIF) dam.exeQuery(condition);

//		if (list.size() > 0) {
//			outputVO.setTotalPage(list.getTotalPage());
			outputVO.setResultList(list);
//			outputVO.setTotalRecord(list.getTotalRecord());
			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());
			sendRtnObject(outputVO);
//		} else {
//			throw new APException("ehl_01_common_009");
//		}
	}
	
	public void getYearMon (Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		
		SQM210OutputVO outputVO = new SQM210OutputVO();
		StringBuffer sql = new StringBuffer();
		dam = this.getDataAccessManager();
		
		sql.append(" select DISTINCT SUBSTR(YEARMON,0,4)||'/'||SUBSTR(YEARMON,5,6) as LABEL, YEARMON as DATA from TBSQM_CSM_EMP_SCORE" );
		sql.append(" ORDER BY YEARMON DESC" );
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			
		condition.setQueryString(sql.toString());
		List<Map<String,Object>> list = (ResultIF) dam.exeQuery(condition);
		outputVO.setResultList(list);

		sendRtnObject(outputVO);
	}

	public void queryData (QueryConditionIF condition, SQM210InputVO inputVO) throws JBranchException, ParseException {
		String yearMon 			= inputVO.getYearMon();
		String branchNbr 		= inputVO.getBranchNbr();
		String jobTitleName 	= inputVO.getJobTitleName();
		String qtnType 			= inputVO.getQtnType();
		String satisfactionO 	= inputVO.getSatisfactionO();
		String empId 			= inputVO.getEmpId();
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT YEARMON , QTN_TYPE ," );
		sql.append("    	BRANCH_NBR , EMP_NAME , EMP_ID , ");
		sql.append("		CUR_JOB , SATISFACTION_O , RESP_NO , CUST_ID , ");
		sql.append("		RESP_DATE , MOBILE_NO , DEDUCTION_FINAL , SEND_DATE ");
		sql.append("   FROM TBSQM_CSM_EMP_SCORE ");
		sql.append("  WHERE YEARMON = :yearMon");
		condition.setObject("yearMon", yearMon);
		if (StringUtils.isNotBlank(qtnType)) {
			sql.append(" AND QTN_TYPE =:qtnType ");
			condition.setObject("qtnType", qtnType);
		}
		if (StringUtils.isNotBlank(branchNbr)) {
			sql.append(" AND BRANCH_NBR =:branchNbr ");
			condition.setObject("branchNbr", branchNbr);
		}
		if (StringUtils.isNotBlank(empId)) {
			sql.append(" AND EMP_ID LIKE :empId ");
			condition.setObject("empId", empId + "%");
		}
		if (StringUtils.isNotBlank(satisfactionO)) {
			sql.append(" AND SATISFACTION_O =:satisfactionO ");
			condition.setObject("satisfactionO", satisfactionO);
		}
		if (StringUtils.isNotBlank(jobTitleName)) {
			sql.append(" AND CUR_JOB =:jobTitleName ");
			condition.setObject("jobTitleName", jobTitleName);
		}
		condition.setQueryString(sql.toString());
//		return dam.exeQuery(condition);
	}

	/* ==== 【儲存】更新資料 ======== */
	public void save (Object body, IPrimitiveMap header) throws JBranchException {
		
		SQM210InputVO inputVO = (SQM210InputVO) body;
		SQM210OutputVO outputVO = new SQM210OutputVO();
		List<Map<String,String>> paramList = inputVO.getParamList();
		dam = this.getDataAccessManager();

		try{
			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
			sb.append("  UPDATE TBSQM_CSM_EMP_SCORE                     ");
			sb.append("     SET SATISFACTION_O = :satisfactionO ,       ");
			sb.append("         RESP_NO    = :respNo ,                  ");
			sb.append("         DEDUCTION_FINAL    = :deductionFinal	");
			sb.append("    WHERE YEARMON = :yearMon AND QTN_TYPE = :qtnType AND");
			sb.append("			 EMP_ID = :empId AND CUST_ID = :custId ");
			for (Map inputMap : paramList) {
				qc.setObject("satisfactionO", 	inputMap.get("SATISFACTION_O"));
				qc.setObject("respNo", 			inputMap.get("RESP_NO"));
				qc.setObject("deductionFinal",	inputMap.get("DEDUCTION_FINAL"));
				qc.setObject("yearMon", 		inputMap.get("YEARMON"));
				qc.setObject("qtnType", 		inputMap.get("QTN_TYPE"));
				qc.setObject("empId", 			inputMap.get("EMP_ID"));
				qc.setObject("custId", 			inputMap.get("CUST_ID"));
				qc.setQueryString(sb.toString());
				dam.exeUpdate(qc);
			}
			sendRtnObject(outputVO);
		} catch (Exception e) {
//			throw new JBranchException("ehl_01_sacommon_007", e); // 更新失敗
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	/* === 產出Excel==== */
	public void exportRPT (Object body, IPrimitiveMap header) throws JBranchException, ParseException, IOException {

		SQM210InputVO inputVO = (SQM210InputVO) body;
		dam = this.getDataAccessManager();
		//滿意度
		Map satisMap = new HashMap<String , String>();
		satisMap.put("1", "非常滿意");
		satisMap.put("2", "滿意");
		satisMap.put("3", "普通");
		satisMap.put("4", "不滿意");
		satisMap.put("5", "非常不滿意");
		
		
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		this.queryData(condition, inputVO);

		List<Map<String, Object>> list = dam.exeQuery(condition);

        Map<String, String> order = new LinkedHashMap<String, String>();
        order.put("YEARMON"			, "計績年月");
        order.put("QTN_TYPE"		, "問卷別");
        order.put("BRANCH_NBR"		, "分行");
        order.put("EMP_NAME"		, "姓名");
        order.put("EMP_ID"			, "員編");
        order.put("CUR_JOB"			, "職務");
        order.put("SATISFACTION_O"	, "滿意度");
        order.put("RESP_NO"			, "回覆值");
        order.put("CUST_ID"			, "客戶ID");
        order.put("SEND_DATE"		, "接收日期");
        order.put("RESP_DATE"		, "回覆日期");
        order.put("MOBILE_NO"		, "手機號碼");
        order.put("DEDUCTION_FINAL"	, "扣分");
        
		String fileName = "滿意度個人扣分";

		Map<String, String> file = new HashMap<String, String>();
		ExcelUtil excel = new ExcelUtil();
		file = this.exportxlsx_cname(fileName , list , order);
//		List listCSV ?ds[++i] = checkIsNull(map, "DEDUCTION_FINAL"); // 扣分
//			if(checkIsNull(map, "CUST_ID").length()>=10){
//				records[++i] = checkIsNull(map, "CUST_ID").substring(0,4) + "****" + checkIsNull(map, "CUST_ID").substring(8,10); // 身分證字號					
//			}else{
//				records[++i] = checkIsNull(map, "CUST_ID");
//			}
//			records[++i] = checkIsNull(map, "CUST_NAME"); // 客戶姓名
//			records[++i] = checkIsNull(map, "AO_CODE");// AO Code	
//			records[++i] = checkIsNull(map, "EMP_NAME"); // 理專姓名
//			records[++i] = checkIsNull(map, "CUST_RISK_BEF"); // 風險承受度前
//			records[++i] = checkIsNull(map, "CUST_RISK_AFR"); // 風險承受度後
//			records[++i] = checkIsNull(map, "SIGNOFF_DATE"); // 測試/簽置日期
//			records[++i] = checkIsNull(map, "CREATEOR"); // 建立人
//			records[++i] = checkIsNull(map, "EMP_NAME"); // 建立人姓名
//			records[++i] = checkIsNull(map, "SUPERVISOR_FLAG"); // 主管確認欄
//			records[++i] = checkIsNull(map, "HR_ATTR"); // 專員有無勸誘客戶提高風險屬性
//			records[++i] = checkIsNull(map, "NOTE"); // 主管備註欄
//			records[++i] = checkIsNull(map, "MODIFIER"); // 最後修改人
//			records[++i] = dateFormat(map, "LASTUPDATE"); // 最後修改時間
//			System.out.println(dateFormat(map, "LASTUPDATE"));
//			a++;
//			listCSV.add(records);
//		}
		
		// header
//		String[] csvHeader = new String[20];
//		int j = 0;
//		csvHeader[j] = "計劃年月";
//		csvHeader[++j] = "問卷別";
//		csvHeader[++j] = "分行";
//		csvHeader[++j] = "姓名";
//		csvHeader[++j] = "員編";
//		csvHeader[++j] = "職務";
//		csvHeader[++j] = "滿意度";
//		csvHeader[++j] = "回覆值";
//		csvHeader[++j] = "客戶id";
//		csvHeader[++j] = "接收日期";
//		csvHeader[++j] = "回覆日期";
//		csvHeader[++j] = "手機號碼";
//		csvHeader[++j] = "扣分";
//		CSVUtil csv = new CSVUtil();
//		csv.setHeader(csvHeader);
//		csv.addRecordList(listCSV);
//		String url = csv.generateCSV();
//		notifyClientToDownloadFile(url, fileName);
		this.sendRtnObject("downloadFile", file);
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
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> qtnMap = xmlInfo.doGetVariable("SQM.QTN_TYPE", FormatHelper.FORMAT_3); // 問卷類型
		Map<String, String> satisMap = xmlInfo.doGetVariable("SQM.ANS_TYPE", FormatHelper.FORMAT_3); // 滿意度
		
//		Map qtnMap = new HashMap<String , String>();
//		qtnMap.put("WMS01", "投資/保險");
//		qtnMap.put("WMS02", "理專");
//		qtnMap.put("WMS03", "開戶");
//		qtnMap.put("WMS04", "櫃檯");
//		qtnMap.put("WMS05", "簡訊");
		
		for (Map<String, Object> objs : list) {
			Row row = sheet.createRow(++rownum);
			
			for (Entry<String, Object> en : objs.entrySet()) {
				Object obj  = en.getValue();
				
				if (en.getKey().equals("QTN_TYPE")) {
					obj =  qtnMap.get(obj);
				}
				
				if (en.getKey().equals("SATISFACTION_O")) {
					obj =  satisMap.get(obj);
				}
								
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
						cell.setCellValue("");
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
	/**
	 * 檢查Map取出欄位是否為Null
	 * 
	 * @param map
	 * @return String
	 */
	private String checkIsNull(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
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
	//日期格式
	private String dateFormat(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key)))
				&& map.get(key) != null) {
			SimpleDateFormat sdfd = new SimpleDateFormat("yyyyMMdd HH:mm");
			return sdfd.format(map.get(key));
		} else
			return "";
	}
	


}