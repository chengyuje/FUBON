package com.systex.jbranch.app.server.fps.sqm230;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

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

import com.systex.jbranch.app.common.fps.table.TBSQM_CSM_NFI_SCOREPK;
import com.systex.jbranch.app.common.fps.table.TBSQM_CSM_NFI_SCOREVO;
import com.systex.jbranch.app.common.fps.table.TBSQM_CSM_NFI_SCORE_PARVO;
import com.systex.jbranch.app.server.fps.sqm220.SQM220OutputVO;
import com.systex.jbranch.fubon.commons.ExcelUtil;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * Copy Right Information :<br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description :非財務指標扣分<br>
 * Comments Name : SQM230.java<br>
 * Author : Carley<br>
 * Date :2018/05/02 <br>
 * Version : 1.0 <br>
 */

@Component("sqm230")
@Scope("request")
public class SQM230 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = Logger.getLogger(SQM230.class);
	
	public void getYearMon (Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		
		SQM230OutputVO outputVO = new SQM230OutputVO();
		StringBuffer sql = new StringBuffer();
		dam = this.getDataAccessManager();
		
		sql.append(" select DISTINCT SUBSTR(YEARMON,0,4)||'/'||SUBSTR(YEARMON,5,6) as LABEL, YEARMON as DATA from TBSQM_CSM_NFI_SCORE" );
		sql.append(" ORDER BY YEARMON DESC" );
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			
		condition.setQueryString(sql.toString());
		List<Map<String,Object>> list = (ResultIF) dam.exeQuery(condition);
		
		outputVO.setResultList(list);
		sendRtnObject(outputVO);
	}

	public void inquire (Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		SQM230InputVO inputVO = (SQM230InputVO) body;
		SQM230OutputVO outputVO = new SQM230OutputVO();
		
		String yearMon 			= inputVO.getYearMon();
		String branchNbr 		= inputVO.getBranchNbr();
		String empId 			= inputVO.getEmpId();
		String caseNo 			= inputVO.getCaseNo();
		String deductionInitial = inputVO.getDeductionInitial();
		String deductionFinal 	= inputVO.getDeductionFinal();
		
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
//		sql.append("SELECT * FROM TBSQM_CSM_NFI_SCORE WHERE YEARMON = :yearMon ");
		
		sql.append("SELECT DEFN.BRANCH_NAME, SQM.*, '' AS A, '' AS B, '' AS C, '' AS D, '' AS E, ");
		sql.append("'' AS F, '' AS G, '' AS H, '' AS I, '' AS J, SQM.YEARMON AS HAPPEN_YEARMON, ");
		sql.append("'6' AS REVIEW_MON FROM TBSQM_CSM_NFI_SCORE SQM ");
		sql.append("LEFT JOIN VWORG_DEFN_INFO DEFN ON SQM.BRANCH_NBR = DEFN.BRANCH_NBR ");
		sql.append("WHERE SQM.YEARMON = :yearMon ");
		
		condition.setObject("yearMon", yearMon);
		
		if (StringUtils.isNotBlank(branchNbr)) {
			sql.append("AND SQM.BRANCH_NBR = :branchNbr ");
			condition.setObject("branchNbr", branchNbr);
		}
		
		if (StringUtils.isNotBlank(empId)) {
			sql.append("AND SQM.EMP_ID LIKE :empId ");
			condition.setObject("empId", empId + "%");
		}
		
		if (StringUtils.isNotBlank(caseNo)) {
			sql.append("AND SQM.CASE_NO = :caseNo ");
			condition.setObject("caseNo", caseNo);
		}
		
		//處長裁示：「扣分」、「不扣分」
		if (StringUtils.isNotBlank(deductionInitial)) {
			if("1".equals(deductionInitial)){				//處長「扣分」：查詢條件為DEDUCTION_INITIAL < 0
				sql.append("AND SQM.DEDUCTION_INITIAL < 0 ");
			} else if ("2".equals(deductionInitial)){		//處長「不扣分」：查詢條件為DEDUCTION_INITIAL = 0
				sql.append("AND SQM.DEDUCTION_INITIAL = 0 ");
			}
		}
		
		//總行裁示：「未裁示」、「扣分」、「不扣分」
		if (StringUtils.isNotBlank(deductionFinal)) {
			if("0".equals(deductionFinal)){					//總行「未裁示」：查詢條件為DEDUCTION_FINAL IS NULL
				sql.append("AND SQM.DEDUCTION_FINAL IS NULL ");
			} else if ("1".equals(deductionFinal)){			//總行「扣分」：查詢條件為DEDUCTION_FINAL < 0
				sql.append("AND SQM.DEDUCTION_FINAL < 0 ");
			} else if ("2".equals(deductionFinal)){			//總行「不扣分」：查詢條件為DEDUCTION_FINAL = 0
				sql.append("AND SQM.DEDUCTION_FINAL = 0 ");
			}
		}
		
		sql.append("ORDER BY SQM.CASE_NO, SQM.EMP_TYPE ");
		
		condition.setQueryString(sql.toString());
		outputVO.setResultList(dam.exeQuery(condition));
		
		this.sendRtnObject(outputVO);
	}
	
	//查詢滿意度扣分設定
	public void inquireScore (Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		SQM230InputVO inputVO = (SQM230InputVO) body;
		SQM230OutputVO outputVO = new SQM230OutputVO();
		
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT * FROM TBSQM_CSM_NFI_SCORE_PAR WHERE CS_TYPE IN ('4', '5') ORDER BY CS_TYPE ");
		condition.setQueryString(sql.toString());
		outputVO.setResultList(dam.exeQuery(condition));
		
		this.sendRtnObject(outputVO);
	}
	
	//滿意度扣分設定
	public void saveScore (Object body, IPrimitiveMap header) throws JBranchException {
		SQM230InputVO inputVO = (SQM230InputVO) body;
		dam = this.getDataAccessManager();
		
		TBSQM_CSM_NFI_SCORE_PARVO vo_four = (TBSQM_CSM_NFI_SCORE_PARVO) dam.findByPKey(TBSQM_CSM_NFI_SCORE_PARVO.TABLE_UID, "4");
		if(null != vo_four){
			//修改
			vo_four.setEMP_DP(new BigDecimal(inputVO.getEmp_dp_4()));
			vo_four.setIM_SUP_DP(new BigDecimal(inputVO.getIm_sup_dp_4()));
			vo_four.setSO_SUP_DP(new BigDecimal(inputVO.getSo_sup_dp_4()));
			dam.update(vo_four);
			
		} else {
			//新增
			TBSQM_CSM_NFI_SCORE_PARVO vo = new TBSQM_CSM_NFI_SCORE_PARVO();
			vo.setCS_TYPE("4");
			vo.setEMP_DP(new BigDecimal(inputVO.getEmp_dp_4()));
			vo.setIM_SUP_DP(new BigDecimal(inputVO.getIm_sup_dp_4()));
			vo.setSO_SUP_DP(new BigDecimal(inputVO.getSo_sup_dp_4()));
			dam.create(vo);
		}
		
		TBSQM_CSM_NFI_SCORE_PARVO vo_five = (TBSQM_CSM_NFI_SCORE_PARVO) dam.findByPKey(TBSQM_CSM_NFI_SCORE_PARVO.TABLE_UID, "5");
		if(null != vo_five){
			//修改
			vo_five.setEMP_DP(new BigDecimal(inputVO.getEmp_dp_5()));
			vo_five.setIM_SUP_DP(new BigDecimal(inputVO.getIm_sup_dp_5()));
			vo_five.setSO_SUP_DP(new BigDecimal(inputVO.getSo_sup_dp_5()));
			dam.update(vo_five);
			
		} else {
			//新增
			TBSQM_CSM_NFI_SCORE_PARVO vo = new TBSQM_CSM_NFI_SCORE_PARVO();
			vo.setCS_TYPE("5");
			vo.setEMP_DP(new BigDecimal(inputVO.getEmp_dp_5()));
			vo.setIM_SUP_DP(new BigDecimal(inputVO.getIm_sup_dp_5()));
			vo.setSO_SUP_DP(new BigDecimal(inputVO.getSo_sup_dp_5()));
			dam.create(vo);
		}
		
		this.sendRtnObject(null);
	}
	
	/* ==== 【儲存】更新資料 ======== */
	public void save (Object body, IPrimitiveMap header) throws JBranchException {
		SQM230InputVO inputVO = (SQM230InputVO) body;
		List<Map<String,String>> resultList = inputVO.getResultList();
		
		if(resultList.size() > 0){
			for(Map<String,String> map : resultList){
				dam = this.getDataAccessManager();
				TBSQM_CSM_NFI_SCOREPK pk = new TBSQM_CSM_NFI_SCOREPK();
				pk.setCASE_NO(map.get("CASE_NO"));
				pk.setYEARMON(map.get("YEARMON"));
				pk.setEMP_TYPE(map.get("EMP_TYPE"));
				
				TBSQM_CSM_NFI_SCOREVO vo = (TBSQM_CSM_NFI_SCOREVO) dam.findByPKey(TBSQM_CSM_NFI_SCOREVO.TABLE_UID, pk);
				vo.setEMP_NAME(map.get("EMP_NAME"));
				vo.setEMP_ID(map.get("EMP_ID"));
				if(map.get("DEDUCTION_INITIAL") != null){
					vo.setDEDUCTION_INITIAL(new BigDecimal(map.get("DEDUCTION_INITIAL")));
				}
				if(map.get("DEDUCTION_FINAL") != null){
					vo.setDEDUCTION_FINAL(new BigDecimal(map.get("DEDUCTION_FINAL")));
				}
				dam.update(vo);
			}
		} else {
			throw new APException("系統發生錯誤請洽系統管理員");
		}
		
		this.sendRtnObject(null);
	}

	/* === 產出Excel==== */
	public void exportRPT (Object body, IPrimitiveMap header) throws JBranchException, ParseException, IOException {

		SQM230InputVO inputVO = (SQM230InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		List<Map<String, String>> list = inputVO.getResultList();

        Map<String, String> order = new LinkedHashMap<String, String>();
        order.put("CASE_NO"				, "案件編號");
        order.put("YEARMON"				, "計績月份");
        order.put("BRANCH_NBR"			, "分行代號");
        order.put("BRANCH_NAME"			, "分行名稱");
        order.put("SATISFACTION_W"		, "缺失內容");
        order.put("A"					, "人員類別");
        order.put("EMP_NAME"			, "姓名");
        order.put("EMP_ID"				, "員工編號");
        order.put("B"					, "客戶紛爭");
        order.put("C"					, "法令遵循/其他內外部規範");
        order.put("D"					, "稽核缺失");
        order.put("E"					, "證照取得");
        order.put("F"					, "認識客戶作業落實(KYC)");
        order.put("G"					, "非常態交易(銷售行為)");
        order.put("DEDUCTION_INITIAL"	, "滿意度(處/副主管裁示)");
        order.put("DEDUCTION_FINAL"		, "滿意度(總行裁示)");
        order.put("H"					, "短TRADE");
        order.put("I"					, "教育訓練");
        order.put("J"					, "獨立列示扣分");
        order.put("HAPPEN_YEARMON"		, "發生缺失月份/釐清責任月份");
        order.put("REVIEW_MON"			, "發生缺失/責任歸屬往前追朔月份");
        order.put("EMP_TYPE"			, "專員/一階/二階主管");
        
		String fileName = "非財務指標扣分";

		Map<String, String> file = new HashMap<String, String>();
		ExcelUtil excel = new ExcelUtil();
		file = this.exportxlsx_cname(fileName , list , order);
		
		this.sendRtnObject("downloadFile", file);
	}

	//匯出EXCEL BY 排序 AND 欄位名稱設定
	public Map<String, String> exportxlsx_cname(String name, List<Map<String, String>> list, Map<String, String> order) throws JBranchException, IOException{
		
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

		//滿意度
		Map satisMap = new HashMap<String , String>();
		satisMap.put("1", "非常滿意");
		satisMap.put("2", "滿意");
		satisMap.put("3", "普通");
		satisMap.put("4", "滿意度扣分(不滿意)");
		satisMap.put("5", "滿意度扣分(非常不滿意)");
		
		for (Map<String, String> objs : list) {
			Row row = sheet.createRow(++rownum);
			
			for (Entry<String, String> en : objs.entrySet()) {
				Object obj  = en.getValue();
				
				if (en.getKey().equals("SATISFACTION_W")) {
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