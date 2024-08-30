package com.systex.jbranch.app.server.fps.pms425;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.jlb.DataFormat;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("pms425")
@Scope("request")
public class PMS425 extends FubonWmsBizLogic {

	private DataAccessManager dam = null;
	SimpleDateFormat sdfYYYYMMDDHHMMSS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//	SimpleDateFormat sdfYYYYMMDD = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat sdfYYYYMMDD = new SimpleDateFormat("yyyyMMdd");

	public void query(Object body, IPrimitiveMap header) throws Exception {

		initUUID();

		PMS425InputVO inputVO = (PMS425InputVO) body;
		PMS425OutputVO outputVO = new PMS425OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		sb.append("SELECT DISTINCT CD.INQ_DATE_TIME, ");
		sb.append("       ORG.REGION_CENTER_ID, ");
		sb.append("       ORG.REGION_CENTER_NAME, ");
		sb.append("       ORG.BRANCH_AREA_ID, ");
		sb.append("       ORG.BRANCH_AREA_NAME, ");
		sb.append("       CD.BRANCH_NBR, ");
		sb.append("       ORG.BRANCH_NAME, ");
		sb.append("       CD.INQ_EMP_NAME, ");
		sb.append("       CD.INQ_EMP_ID, ");
		sb.append("       CD.INQ_EMP_ROLE_NAME, ");
		sb.append("       CD.CUST_ID, ");
		sb.append("       CD.CUST_NAME, ");
		sb.append("       CD.CUST_AO || '-' || EMP.EMP_NAME || CASE WHEN AO.TYPE = '1' THEN '(計績)' WHEN AO.TYPE = '2' THEN '(兼)' WHEN AO.TYPE = '3' THEN '(維護)' ELSE NULL END AS CUST_AO ");
		sb.append("FROM TBPMS_INQUIRE_CUST_DTL CD ");
		sb.append("LEFT JOIN VWORG_DEFN_INFO ORG ON CD.BRANCH_NBR = ORG.BRANCH_NBR ");
		sb.append("LEFT JOIN TBPMS_SALES_AOCODE_REC AO ON AO.AO_CODE = CD.CUST_AO AND CD.INQ_DATE_TIME BETWEEN AO.START_TIME AND AO.END_TIME ");
		sb.append("LEFT JOIN TBPMS_EMPLOYEE_REC_N EMP ON AO.EMP_ID = EMP.EMP_ID AND CD.INQ_DATE_TIME BETWEEN EMP.START_TIME AND EMP.END_TIME ");
		
		sb.append("WHERE 1 = 1 ");
		
		// 資料統計日期-起
		if (inputVO.getsDate() != null) {
			sb.append("AND TRUNC(CD.INQ_DATE_TIME) >= TRUNC(:start) ");
			queryCondition.setObject("start", inputVO.getsDate());
		}

		// 資料統計日期-迄
		if (inputVO.geteDate() != null) {
			sb.append("AND TRUNC(CD.INQ_DATE_TIME) <= TRUNC(:end) ");
			queryCondition.setObject("end", inputVO.geteDate());
		}

		// 區域中心
		if (StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
			sb.append("AND ORG.REGION_CENTER_ID = :regionCenterID ");
			queryCondition.setObject("regionCenterID", inputVO.getRegion_center_id());
		}

		// 營運區	
		if (StringUtils.isNotBlank(inputVO.getBranch_area_id())) {
			sb.append("AND ORG.BRANCH_AREA_ID = :branchAreaID ");
			queryCondition.setObject("branchAreaID", inputVO.getBranch_area_id());
		}

		// 分行
		if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
			sb.append("AND ORG.BRANCH_NBR = :branchNbr ");
			queryCondition.setObject("branchNbr", inputVO.getBranch_nbr());
		}

		sb.append("ORDER BY CD.INQ_DATE_TIME DESC, ");
		sb.append("         DECODE(REPLACE(REPLACE(REPLACE(ORG.REGION_CENTER_NAME, '分行業務', ''), '處', ''), ' 合計', ''), '一', 1, '二', 2, '三', 3, '四', 4, '五', 5, '六', 6, '七', 7, '八', 8, '九', 9, '十', 10, 99), ");
		sb.append("         ORG.REGION_CENTER_NAME, ");
		sb.append("         ORG.BRANCH_AREA_ID, ");
		sb.append("         ORG.BRANCH_AREA_NAME, ");
		sb.append("         CD.BRANCH_NBR, ");
		sb.append("         ORG.BRANCH_NAME ");

		queryCondition.setQueryString(sb.toString());

		outputVO.setResultList(dam.exeQuery(queryCondition));

		sendRtnObject(outputVO);
	}

	public void export(Object body, IPrimitiveMap header) throws JBranchException, ParseException, FileNotFoundException, IOException {

		XmlInfo xmlInfo = new XmlInfo();

		PMS425InputVO inputVO = (PMS425InputVO) body;

		List<Map<String, Object>> list = inputVO.getExportList();

		String fileName = "分行理財客戶查詢紀錄日報_" + sdfYYYYMMDD.format(inputVO.getsDate()) + "-" + sdfYYYYMMDD.format(inputVO.geteDate()) + ".xlsx";
		String uuid = UUID.randomUUID().toString();
		String Path = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);

		String filePath = Path + uuid;

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("分行理財客戶查詢紀錄日報");
		sheet.setDefaultColumnWidth(20);
		sheet.setDefaultRowHeightInPoints(20);

		XSSFCellStyle headingStyle = workbook.createCellStyle();
		headingStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		headingStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
		headingStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);// 填滿顏色
		headingStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		headingStyle.setBorderBottom((short) 1);
		headingStyle.setBorderTop((short) 1);
		headingStyle.setBorderLeft((short) 1);
		headingStyle.setBorderRight((short) 1);
		headingStyle.setWrapText(true);

		String[] headerLine = { "查詢時間", "分行代號", "分行名稱", "員工姓名", "員工編號", "使用者角色", "客戶ID", "客戶姓名", "所屬理專" };
		String[] mainLine = { "INQ_DATE_TIME", "BRANCH_NBR", "BRANCH_NAME", "INQ_EMP_NAME", "INQ_EMP_ID", "INQ_EMP_ROLE_NAME", "CUST_ID", "CUST_NAME", "CUST_AO" };

		Integer index = 0;

		XSSFRow row = sheet.createRow(index);
		for (int i = 0; i < headerLine.length; i++) {
			XSSFCell cell = row.createCell(i);
			cell.setCellStyle(headingStyle);
			cell.setCellValue(headerLine[i]);
		}

		index++;

		// 資料 CELL型式
		XSSFCellStyle mainStyle = workbook.createCellStyle();
		mainStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		mainStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
		mainStyle.setBorderBottom((short) 1);
		mainStyle.setBorderTop((short) 1);
		mainStyle.setBorderLeft((short) 1);
		mainStyle.setBorderRight((short) 1);

		for (Map<String, Object> map : list) {
			row = sheet.createRow(index);

			for (int i = 0; i < mainLine.length; i++) {
				XSSFCell cell = row.createCell(i);
				cell.setCellStyle(mainStyle);

				switch (mainLine[i]) {
					case "INQ_DATE_TIME":
						cell.setCellValue(sdfYYYYMMDDHHMMSS.format(sdfYYYYMMDDHHMMSS.parse((String) map.get(mainLine[i]))));
						break;
					case "CUST_ID":
						cell.setCellValue(DataFormat.getCustIdMaskForHighRisk((String) map.get("CUST_ID")));
						break;
					default:
						cell.setCellValue(checkIsNull(map, mainLine[i]));
						break;
				}
			}

			index++;
		}

		workbook.write(new FileOutputStream(filePath));

		notifyClientToDownloadFile(DataManager.getSystem().getPath().get("temp").toString() + uuid, fileName);

		sendRtnObject(null);
	}

	// 空值檢查
	private String checkIsNull(Map map, String key) {

		if (null != map && null != map.get(key)) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}

}