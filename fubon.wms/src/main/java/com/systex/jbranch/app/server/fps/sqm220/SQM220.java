package com.systex.jbranch.app.server.fps.sqm220;

import com.systex.jbranch.fubon.commons.ExcelUtil;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.*;
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
 * Description :績效評核_分行成績<br>
 * Comments Name : SQM220.java<br>
 * Author : Sam<br>
 * Date :2018/04/16 <br>
 * Version : 1.0 <br>
 */

@Component("sqm220")
@Scope("request")
public class SQM220 extends FubonWmsBizLogic {
	
	private DataAccessManager dam = null;
	private Logger logger = Logger.getLogger(SQM220.class);

	public void inquire (Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		
		SQM220InputVO inputVO = (SQM220InputVO) body;
		SQM220OutputVO outputVO = new SQM220OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		queryData(condition, inputVO);
		List<Map<String,Object>> list = (ResultIF) dam.exeQuery(condition);
		
//		outputVO.setTotalPage(list.getTotalPage());
		outputVO.setResultList(list);
//		outputVO.setTotalRecord(list.getTotalRecord());
		outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());
		sendRtnObject(outputVO);
	}
	
	public void getYearMon (Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		
//		SQM220InputVO inputVO = (SQM220InputVO) body;
		SQM220OutputVO outputVO = new SQM220OutputVO();
		StringBuffer sql = new StringBuffer();
		dam = this.getDataAccessManager();
		
		sql.append(" select DISTINCT SUBSTR(YEARMON,0,4)||'/'||SUBSTR(YEARMON,5,6) as LABEL, YEARMON as DATA from TBSQM_CSM_BR_SCORE" );
		sql.append(" ORDER BY YEARMON DESC" );
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			
		condition.setQueryString(sql.toString());
		List<Map<String,Object>> list = (ResultIF) dam.exeQuery(condition);
		
//		outputVO.setTotalPage(list.getTotalPage());
		outputVO.setResultList(list);
//		outputVO.setTotalRecord(list.getTotalRecord());
//		outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());
		sendRtnObject(outputVO);
	}
	

	public void queryData (QueryConditionIF condition, SQM220InputVO inputVO) throws JBranchException, ParseException {
		String yearMon 			= inputVO.getYearMon();
		String branchNbr 		= inputVO.getBranchNbr();
		String qtnType 			= inputVO.getQtnType();
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT QTN_TYPE ,BRANCH_NBR, YEARMON ,	" );
		sql.append("    	           VS_CNT , S_CNT , OS_CNT , ");
		sql.append("                  NS_CNT , VD_CNT , NC_CNT, TOT_CNT ");
		sql.append("   FROM TBSQM_CSM_BR_SCORE  A"); 
		sql.append("  WHERE YEARMON = :yearMon");
		condition.setObject("yearMon", yearMon);
		if (StringUtils.isNotBlank(branchNbr)) {
			sql.append(" AND BRANCH_NBR =:branchNbr ");
			condition.setObject("branchNbr", branchNbr);
		}
		if (StringUtils.isNotBlank(qtnType)) {
			sql.append(" AND QTN_TYPE =:qtnType ");
			condition.setObject("qtnType", qtnType);
		}
		sql.append(" ORDER BY BRANCH_NBR , QTN_TYPE");
		condition.setQueryString(sql.toString());
//		return dam.exeQuery(condition);
	}

	public void queryDataForRpt (QueryConditionIF condition, SQM220InputVO inputVO) throws JBranchException, ParseException {
		String yearMon 			= inputVO.getYearMon();
		String branchNbr 		= inputVO.getBranchNbr();
		String qtnType 			= inputVO.getQtnType();
		StringBuffer sql = new StringBuffer();
		sql.append("	SELECT * FROM (");
		sql.append("	SELECT '合計' QTN_TYPE , BRANCH_NBR , YEARMON,	");
		sql.append("					SUM(VS_CNT)	VS_CNT, SUM(S_CNT)	S_CNT	, SUM(OS_CNT)	OS_CNT	,	");
		sql.append("					SUM(NS_CNT)	NS_CNT	, SUM(VD_CNT)	VD_CNT	, SUM(NC_CNT)	NC_CNT ,	SUM(TOT_CNT)	TOT_CNT	");
		sql.append("	FROM TBSQM_CSM_BR_SCORE	");
		sql.append("  WHERE YEARMON = :yearMon");
		condition.setObject("yearMon", yearMon);
		if (StringUtils.isNotBlank(branchNbr)) {
			sql.append(" AND BRANCH_NBR =:branchNbr ");
			condition.setObject("branchNbr", branchNbr);
		}
		sql.append("	GROUP BY BRANCH_NBR , YEARMON");
//		if (StringUtils.isNotBlank(qtnType)) {
//			sql.append(" AND QTN_TYPE =:qtnType ");
//			condition.setObject("qtnType", qtnType);
//		}
		sql.append("	UNION ALL	");
		sql.append(" SELECT QTN_TYPE , BRANCH_NBR, YEARMON ,	" );
		sql.append("    	           VS_CNT , S_CNT , OS_CNT , ");
		sql.append("                  NS_CNT , VD_CNT , NC_CNT, TOT_CNT ");
		sql.append("   FROM TBSQM_CSM_BR_SCORE "); 
		sql.append("  WHERE YEARMON = :yearMon");
		condition.setObject("yearMon", yearMon);
		if (StringUtils.isNotBlank(branchNbr)) {
			sql.append(" AND BRANCH_NBR =:branchNbr ");
			condition.setObject("branchNbr", branchNbr);
		}
		sql.append("	) ");
		sql.append("	ORDER BY QTN_TYPE,BRANCH_NBR");
		condition.setQueryString(sql.toString());
	}
	/* ==== 【儲存】更新資料 ======== */
	public void save (Object body, IPrimitiveMap header) throws JBranchException {
		SQM220InputVO inputVO = (SQM220InputVO) body;
		SQM220OutputVO outputVO = new SQM220OutputVO();
		List<Map<String,String>> paramList = inputVO.getParamList();
		dam = this.getDataAccessManager();
		try{
			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
			sb.append("  UPDATE TBSQM_CSM_BR_SCORE	");
			sb.append("     	    SET VS_CNT = :vsCnt ,	S_CNT =:sCnt ,	");
			sb.append("                     OS_CNT = :osCnt , NS_CNT =:nsCnt , VD_CNT =:vdCnt , NC_CNT =:ncCnt ,	");
			sb.append("                     TOT_CNT =:vsCnt + :sCnt + :osCnt + :nsCnt + :vdCnt + :ncCnt,	");
			sb.append("                     LASTUPDATE = SYSDATE , MODIFIER =:empId");
			sb.append("    WHERE YEARMON = :yearMon AND QTN_TYPE = :qtnType AND");
			sb.append("			              BRANCH_NBR =:braNbr");
			for (Map inputMap : paramList) {
				qc.setObject("vsCnt",	inputMap.get("VS_CNT")==null?0:inputMap.get("VS_CNT"));
				qc.setObject("sCnt",	inputMap.get("S_CNT")==null?0:inputMap.get("S_CNT"));
				qc.setObject("osCnt",	inputMap.get("OS_CNT")==null?0:inputMap.get("OS_CNT"));
				qc.setObject("nsCnt",	inputMap.get("NS_CNT")==null?0:inputMap.get("NS_CNT"));
				qc.setObject("vdCnt",	inputMap.get("VD_CNT")==null?0:inputMap.get("VD_CNT"));
				qc.setObject("ncCnt",	inputMap.get("NC_CNT")==null?0:inputMap.get("NC_CNT"));
				qc.setObject("empId",	getUserVariable(FubonSystemVariableConsts.LOGINID));
				qc.setObject("yearMon", inputMap.get("YEARMON"));
				qc.setObject("qtnType", inputMap.get("QTN_TYPE"));
				qc.setObject("braNbr", inputMap.get("BRANCH_NBR"));
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
	public void export (Object body, IPrimitiveMap header) throws JBranchException, ParseException, IOException {
		SQM220InputVO inputVO = (SQM220InputVO) body;
		dam = this.getDataAccessManager();
		//滿意度
		Map satisMap = new HashMap<String , String>();
		satisMap.put("1", "非常滿意");
		satisMap.put("2", "滿意");
		satisMap.put("3", "普通");
		satisMap.put("4", "不滿意");
		satisMap.put("5", "非常不滿意");
		satisMap.put("6", "未聯繫");
		
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		this.queryDataForRpt(condition, inputVO);

		List<Map<String, Object>> list = dam.exeQuery(condition);

        Map<String, String> order = new LinkedHashMap<String, String>();
//        order.put("QTN_TYPE"		, "問卷別");
        order.put("BRANCH_NBR"		, "分行別");
        order.put("VS_CNT"			, "非常滿意");
        order.put("S_CNT"				, "滿意");
        order.put("OS_CNT"			, "普通");
        order.put("NS_CNT"			, "不滿意");
        order.put("VD_CNT"			, "非常不滿意");
        order.put("NC_CNT"			, "未聯繫");
        order.put("TOT_CNT"			, "TOTAL");
        
		String fileName = "滿意度分行成績";

		Map<String, String> file = new HashMap<String, String>();
		ExcelUtil excel = new ExcelUtil();
		file = this.exportxlsx_cname(fileName , list , order);
		this.sendRtnObject("downloadFile", file);
	}
	
	private Map<String,List<Map<String,Object>>> packQtnTypeMap(List<Map<String,Object>> list) throws Exception{
		Map<String,List<Map<String,Object>>> result = new HashMap<String,List<Map<String,Object>>>();
		for(Map map:list) {
			String qtnType = (String)map.get("QTN_TYPE");
			if (result.containsKey(qtnType)) {
//				result.
			}
		}
		
		return result;
	}
	
	private List<Map<String,Object>> packRequireList(List<Map<String,Object>> list , String qtnType) {
		List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
		for (Map map:list) {
			String qtnTypeInList = (String) map.get("QTN_TYPE");
			if (qtnTypeInList.equals(qtnType))
				result.add(map);
		}
		
		return result;
	}
	//匯出EXCEL BY 排序 AND 欄位名稱設定
	public Map<String, String> exportxlsx_cname(String name, List<Map<String, Object>> list, Map<String, String> order) throws JBranchException, IOException{
		//滿意度
		Map satisMap = new HashMap<String , String>();
		satisMap.put("1", "非常滿意");
		satisMap.put("2", "滿意");
		satisMap.put("3", "普通");
		satisMap.put("4", "不滿意");
		satisMap.put("5", "非常不滿意");
		satisMap.put("6", "未聯繫");
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> qtnMap = xmlInfo.doGetVariable("SQM.QTN_TYPE", FormatHelper.FORMAT_3); // 問卷類型
		qtnMap.put("合計", "合計");

		List qtnLinkedList = new LinkedList<String>();
		qtnLinkedList.add("合計");
		qtnLinkedList.add("WMS01");
		qtnLinkedList.add("WMS02");
		qtnLinkedList.add("WMS03");
		qtnLinkedList.add("WMS04");
		qtnLinkedList.add("WMS05");
		
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = null;
		Map<String, String> params = new HashMap<String, String>();
		List<Map<String,Object>> sheetList = new ArrayList<Map<String,Object>>();

		Iterator<String> it = qtnLinkedList.iterator();
		while(it.hasNext()) {
			String qtnType = it.next();
			sheetList = packRequireList(list , qtnType);
			if (sheetList.size()>0)
				sheet = workbook.createSheet((String)qtnMap.get(qtnType).replace("/", ""));
			
		CellStyle cellStyleFont = workbook.createCellStyle();
		XSSFFont font = workbook.createFont();
		font.setFontName("Serif");
		cellStyleFont.setFont(font);
		
//		sheet = doMergeCell(sheet, list);
		CellStyle cellStyleDate = workbook.createCellStyle();
		CreationHelper createHelper = workbook.getCreationHelper();
		cellStyleDate.setDataFormat(createHelper.createDataFormat().getFormat("yyyy/MM/dd h:mm:ss"));

		Row row_head = sheet.createRow(0);
		int cell_head_num = 0;
		for (Entry<String, String> strs : order.entrySet()) {
			Cell cell_head = row_head.createCell(cell_head_num++);
			String str = strs.getValue();
			cell_head.setCellStyle(cellStyleFont);
			cell_head.setCellValue(str);
		}

		List<String> orderkey = new ArrayList<String>();
		for ( String key : order.keySet() ) {
			orderkey.add(key);
		}
		
		int rownum = sheet.getLastRowNum();

		for (Map<String, Object> objs : sheetList) {
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
		}
		
		String Path = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		String uuid = UUID.randomUUID().toString();
		String fileName = name + ".xlsx";
		String filePath = Path + uuid;

		workbook.write(new FileOutputStream(filePath));
		
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