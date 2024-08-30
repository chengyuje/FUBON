package com.systex.jbranch.app.server.fps.mgm510;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.itextpdf.text.Font;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * @author Carley
 * @date 2018/05/23
 * 
 */
@Component("mgm510")
@Scope("request")
public class MGM510 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	
	//分行管理報表查詢
	public void inquire (Object body, IPrimitiveMap header) throws JBranchException {
		MGM510InputVO inputVO = (MGM510InputVO) body;
		MGM510OutputVO outputVO = new MGM510OutputVO();
		
		if(StringUtils.isNotBlank(inputVO.getAct_seq())){
			List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
			dam = this.getDataAccessManager();
			
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
			
			sb.append("SELECT DEFN.REGION_CENTER_ID, DEFN.REGION_CENTER_NAME, DEFN.BRANCH_AREA_ID, ");
			sb.append("DEFN.BRANCH_AREA_NAME, DEFN.BRANCH_NBR, DEFN.BRANCH_NAME, ORG.EMP_NAME, ");
			sb.append("ORG.ROLE_ID, PRI.PRIVILEGEID, ");
			sb.append("(CASE WHEN PRI.PRIVILEGEID = '003' THEN 'FCH' ELSE ");
			sb.append("(CASE ORG.ROLE_ID WHEN 'A145' THEN 'FC1' WHEN 'ABRH' THEN 'FC2' ");
			sb.append("WHEN 'A148' THEN 'FC3' WHEN 'ABRE' THEN 'FC4' ");
			sb.append("WHEN 'A8A7' THEN 'FC5' END) END) AS FC_LEVEL, MAIN.* FROM ( ");
			sb.append("SELECT A.BRA_NBR, A.AO_CODE, A.MGM_NBR, NVL(B.UN_APPR,0) AS UN_APPR, ");
			sb.append("NVL(C.UN_ABLE_EXH,0) AS UN_ABLE_EXH, NVL(D.ABLE_EXH,0) AS ABLE_EXH, ");
			sb.append("NVL(F.TOTAL_POINTS,0) AS TOTAL_POINTS, NVL(E.TOTAL_EXH,0) AS TOTAL_EXH, ");
			sb.append("NVL(F.TOTAL_POINTS,0) - NVL(E.TOTAL_EXH,0) AS UN_EXH, ");
			sb.append("ROUND((NVL(D.ABLE_EXH,0) / A.MGM_NBR * 100), 2) AS PASS_RATE, ");
			sb.append("ROUND((CASE WHEN NVL(E.TOTAL_EXH,0) = 0 THEN 0 ");
			sb.append("ELSE E.TOTAL_EXH / NVL(F.TOTAL_POINTS,0) END), 2) * 100 AS EXH_RATE ");
			sb.append("FROM ( ");
			
			//查詢：AO Code、分行代碼、鍵機案件數
			sb.append("SELECT NVL(AO_CODE, 'NO_AO') AS AO_CODE, BRA_NBR, COUNT(*) AS MGM_NBR FROM ( ");
			sb.append("SELECT CUST.AO_CODE, CUST.BRA_NBR, MGM.* FROM ( ");
			sb.append("SELECT * FROM TBMGM_MGM WHERE ACT_SEQ = :act_seq ) MGM ");
			sb.append("LEFT JOIN TBCRM_CUST_MAST CUST ON MGM.MGM_CUST_ID = CUST.CUST_ID ) ");
			sb.append("GROUP BY AO_CODE, BRA_NBR ");
			sb.append(") A ");
			sb.append("LEFT JOIN ( ");
			
			//查詢：AO Code、分行代碼、未核點案件數(尚未放行之案件)
			sb.append("SELECT NVL(AO_CODE, 'NO_AO') AS AO_CODE, BRA_NBR, COUNT(*) AS UN_APPR FROM ( ");
			sb.append("SELECT CUST.AO_CODE, CUST.BRA_NBR, MGM.* FROM ( ");
			sb.append("SELECT * FROM TBMGM_MGM WHERE ACT_SEQ = :act_seq AND RELEASE_DATE IS NULL ) MGM ");
			sb.append("LEFT JOIN TBCRM_CUST_MAST CUST ON MGM.MGM_CUST_ID = CUST.CUST_ID ) ");
			sb.append("GROUP BY AO_CODE, BRA_NBR ");
			sb.append(") B ON A.AO_CODE = B.AO_CODE AND A.BRA_NBR = B.BRA_NBR ");
			sb.append("LEFT JOIN ( ");
			
			//查詢：AO Code、分行代碼、未達門檻案件數(已放行且未達門檻)
			sb.append("SELECT NVL(AO_CODE, 'NO_AO') AS AO_CODE, BRA_NBR, COUNT(*) AS UN_ABLE_EXH FROM ( ");
			sb.append("SELECT CUST.AO_CODE, CUST.BRA_NBR, MGM.* FROM ( ");
			sb.append("SELECT * FROM TBMGM_MGM WHERE ACT_SEQ = :act_seq ");
			sb.append("AND RELEASE_DATE IS NOT NULL AND MGM_APPR_STATUS = 'N' ) MGM  ");
			sb.append("LEFT JOIN TBCRM_CUST_MAST CUST ON MGM.MGM_CUST_ID = CUST.CUST_ID ) ");
			sb.append("GROUP BY AO_CODE, BRA_NBR ");
			sb.append(") C ON A.AO_CODE = C.AO_CODE AND A.BRA_NBR = C.BRA_NBR ");
			sb.append("LEFT JOIN ( ");
			
			//查詢：AO Code、分行代碼、已核點案件數(已放行且已達門檻)
			sb.append("SELECT NVL(AO_CODE, 'NO_AO') AS AO_CODE, BRA_NBR, COUNT(*) AS ABLE_EXH FROM ( ");
			sb.append("SELECT CUST.AO_CODE, CUST.BRA_NBR, MGM.* FROM ( ");
			sb.append("SELECT * FROM TBMGM_MGM WHERE ACT_SEQ = :act_seq ");
			sb.append("AND RELEASE_DATE IS NOT NULL AND MGM_APPR_STATUS = 'Y' ) MGM ");
			sb.append("LEFT JOIN TBCRM_CUST_MAST CUST ON MGM.MGM_CUST_ID = CUST.CUST_ID ) ");
			sb.append("GROUP BY AO_CODE, BRA_NBR ");
			sb.append(") D ON A.AO_CODE = D.AO_CODE AND A.BRA_NBR = D.BRA_NBR ");
			sb.append("LEFT JOIN ( ");
			
			//查詢：AO Code、分行代碼、已兌換點數
			sb.append("SELECT NVL(CUST.AO_CODE, 'NO_AO') AS AO_CODE, CUST.BRA_NBR, ");
			sb.append("SUM(EXCHANGE_POINTS) AS TOTAL_EXH FROM ( ");
			sb.append("SELECT * FROM TBMGM_APPLY_MAIN WHERE ACT_SEQ = :act_seq ) APP ");
			sb.append("LEFT JOIN TBCRM_CUST_MAST CUST ON APP.CUST_ID = CUST.CUST_ID ");
			sb.append("GROUP BY CUST.AO_CODE, CUST.BRA_NBR ");
			sb.append(") E ON A.AO_CODE = E.AO_CODE AND A.BRA_NBR = E.BRA_NBR ");
			
			//查詢：AO Code、分行代碼、可兌換總點數
			sb.append("LEFT JOIN ( ");
			sb.append("SELECT NVL(AO_CODE, 'NO_AO') AS AO_CODE, BRA_NBR, SUM(APPR_POINTS) AS TOTAL_POINTS FROM ( ");
			sb.append("SELECT CUST.AO_CODE, CUST.BRA_NBR, MGM.* FROM ( ");
			sb.append("SELECT * FROM TBMGM_MGM WHERE ACT_SEQ = :act_seq AND RELEASE_DATE IS NOT NULL ) MGM ");
			sb.append("LEFT JOIN TBCRM_CUST_MAST CUST ON MGM.MGM_CUST_ID = CUST.CUST_ID ) ");
			sb.append("GROUP BY AO_CODE, BRA_NBR ");
			sb.append(") F ON A.AO_CODE = F.AO_CODE AND A.BRA_NBR = F.BRA_NBR ) MAIN ");
			
			sb.append("LEFT JOIN TBORG_SALES_AOCODE AO ON MAIN.AO_CODE = AO.AO_CODE ");
			sb.append("LEFT JOIN VWORG_BRANCH_EMP_DETAIL_INFO ORG ON MAIN.AO_CODE = ORG.AO_CODE ");
			sb.append("LEFT JOIN TBSYSSECUROLPRIASS PRI ON ORG.ROLE_ID = PRI.ROLEID ");
			sb.append("LEFT JOIN VWORG_DEFN_INFO DEFN ON MAIN.BRA_NBR = DEFN.BRANCH_NBR ");
			sb.append("WHERE 1 = 1 ");
			
			//活動代碼
			queryCondition.setObject("act_seq", inputVO.getAct_seq());
			
			//理專
			if(StringUtils.isNotBlank(inputVO.getAo_code()) && !"0".equals(inputVO.getAo_code())){
				sb.append("AND MAIN.AO_CODE = :ao_code ");
				queryCondition.setObject("ao_code", inputVO.getAo_code());					
				
			} else {
				//查空Code
				if("0".equals(inputVO.getAo_code())){
					sb.append("AND MAIN.AO_CODE = 'NO_AO' ");
				}
				
				//分行
				if(StringUtils.isNotBlank(inputVO.getBranch_nbr())){
					sb.append("AND DEFN.BRANCH_NBR = :branch_nbr ");
					queryCondition.setObject("branch_nbr", inputVO.getBranch_nbr());
				} else {
					//營運區
					if(StringUtils.isNotBlank(inputVO.getBranch_area_id())){
						sb.append("AND DEFN.BRANCH_AREA_ID = :branch_area_id ");
						queryCondition.setObject("branch_area_id", inputVO.getBranch_area_id());
					} else {
						//業務處
						if(StringUtils.isNotBlank(inputVO.getRegion_center_id())){
							sb.append("AND DEFN.REGION_CENTER_ID = :region_center_id ");
							queryCondition.setObject("region_center_id", inputVO.getRegion_center_id());
						}
					}
				}
			}
			
			//理專職級
			if(StringUtils.isNotBlank(inputVO.getFc_level())){
				sb.append("AND (CASE WHEN PRI.PRIVILEGEID = '003' THEN 'FCH' ELSE ");
				sb.append("(CASE ORG.ROLE_ID WHEN 'A145' THEN 'FC1' WHEN 'ABRH' THEN 'FC2' ");
				sb.append("WHEN 'A148' THEN 'FC3' WHEN 'ABRE' THEN 'FC4' ");
				sb.append("WHEN 'A8A7' THEN 'FC5' END) END) = :fc_level ");
				queryCondition.setObject("fc_level", inputVO.getFc_level());
			}
			
			queryCondition.setQueryString(sb.toString());
			resultList = dam.exeQuery(queryCondition);
			outputVO.setResultList(resultList);
			
			this.sendRtnObject(outputVO);
			
		} else {
			throw new APException("ehl_01_common_022");		//欄位檢核錯誤：*為必要輸入欄位,請輸入後重試
		}
	}
	
	//分行管理報表下載
	public void download (Object body, IPrimitiveMap header) throws Exception {
		MGM510InputVO inputVO = (MGM510InputVO) body;
		List<Map<String,Object>> resultList = inputVO.getResultList();
		if(resultList.size() > 0){
			String act_seq = resultList.get(0).get("ACT_SEQ").toString();
			String act_name = "";
			if(StringUtils.isNotBlank(act_seq)){
				//查詢活動名稱
				dam = this.getDataAccessManager();
				QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				StringBuffer sb = new StringBuffer();
				sb.append("SELECT ACT_NAME FROM TBMGM_ACTIVITY_MAIN WHERE ACT_SEQ = :act_seq ");
				queryCondition.setObject("act_seq", act_seq);
				queryCondition.setQueryString(sb.toString());
				List<Map<String,Object>> actList = dam.exeQuery(queryCondition);
				if(actList.size() > 0){
					act_name = actList.get(0).get("ACT_NAME").toString();					
				}
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			SimpleDateFormat sdfSlash = new SimpleDateFormat("yyyy/MM/dd");
			
			//相關資訊設定
			String fileName = String.format("MGM活動分行管理報表%s.xlsx", sdf.format(new Date()));
			String uuid = UUID.randomUUID().toString();
			
			//建置Excel
			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet("MGM活動分行管理報表" + sdf.format(new Date()));
			sheet.setDefaultColumnWidth(20);
			sheet.setDefaultRowHeightInPoints(20);
			
			//Subject font型式
			XSSFFont font = workbook.createFont();
			font.setColor(HSSFColor.BLACK.index);
			font.setBoldweight((short)Font.NORMAL);
			font.setFontHeight((short)300);

			// Subject cell型式
			XSSFCellStyle SubjectStyle = workbook.createCellStyle();
			SubjectStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
			SubjectStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
//			SubjectStyle.setBorderBottom((short) 1);
//			SubjectStyle.setBorderTop((short) 1);
//			SubjectStyle.setBorderLeft((short) 1);
//			SubjectStyle.setBorderRight((short) 1);
			SubjectStyle.setWrapText(true);
			SubjectStyle.setFont(font);

			// 表頭 CELL型式
			XSSFCellStyle headingStyle = workbook.createCellStyle();
			headingStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
			headingStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
			headingStyle.setFillForegroundColor(HSSFColor.LIGHT_ORANGE.index);	//填滿顏色
			headingStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			headingStyle.setBorderBottom((short) 1);
			headingStyle.setBorderTop((short) 1);
			headingStyle.setBorderLeft((short) 1);
			headingStyle.setBorderRight((short) 1);
			headingStyle.setWrapText(true);

			Integer index = 0; // first row
			
			//Subject
			XSSFRow row = sheet.createRow(index);
			row.setHeight((short)500);
			XSSFCell sCell = row.createCell(0);
			sCell.setCellStyle(SubjectStyle);
			sCell.setCellValue(act_name + "_分行管理報表　" + sdfSlash.format(new Date()));
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 15));
			
//			XSSFCell sCell2 = row.createCell(6);
//			sCell2.setCellStyle(SubjectStyle);
//			sheet.addMergedRegion(new CellRangeAddress(0,0,6,10));
//			sCell2.setCellValue("日期：" + sdfSlash.format(new Date()));
			
			index++;
			
			//header
			String[] headerLine1 = {"活動代碼","業務處","營運區","分行",
									"理專職級","AO Code","理專",
									"鍵機案件數","未核點案件數","未達門檻案件數","已核點案件數",
									"可兌換總點數","已兌換點數","未兌換點數","案件合格率","點數兌換率"};
			
			String[] mainLine    = {"ACT_SEQ","REGION_CENTER_NAME","BRANCH_AREA_NAME","BRANCH_NAME",
									"FC_LEVEL","AO_CODE","EMP_NAME",
									"MGM_NBR","UN_APPR","UN_ABLE_EXH","ABLE_EXH",
									"TOTAL_POINTS","TOTAL_EXH","UN_EXH","PASS_RATE","EXH_RATE"};
			
			Integer startFlag = 0;
			Integer endFlag = 0;
			ArrayList<String> tempList = new ArrayList<String>(); //比對用
			
		    row = sheet.createRow(index);
		    
			for (int i = 0; i < headerLine1.length; i++) {
				String headerLine = headerLine1[i];
				if (tempList.indexOf(headerLine) < 0) {
					tempList.add(headerLine);
					XSSFCell cell = row.createCell(i);
					cell.setCellStyle(headingStyle);
					cell.setCellValue(headerLine1[i]);

					if (endFlag != 0) {
						sheet.addMergedRegion(new CellRangeAddress(0, 0, startFlag, endFlag)); // firstRow, endRow, firstColumn, endColumn
					}
					startFlag = i;
					endFlag = 0;
				} else {
					endFlag = i;
				}
			}
			if (endFlag != 0) { //最後的CELL若需要合併儲存格，則在這裡做
				sheet.addMergedRegion(new CellRangeAddress(0, 0, startFlag, endFlag)); // firstRow, endRow, firstColumn, endColumn
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
			
			if (resultList.size() > 0) {
				
				for (Map<String, Object> map : resultList) {
					row = sheet.createRow(index);

					if (map.size() > 0) {
						for (int j = 0; j < mainLine.length; j++) {
							XSSFCell cell = row.createCell(j);
							cell.setCellStyle(mainStyle);
							if (mainLine[j].indexOf("AO_CODE") >= 0){
								if (map.containsKey("AO_CODE") && (map.get("AO_CODE") != null)) {
									String ao_code = map.get("AO_CODE").toString();
									if("NO_AO".equals(ao_code)){
										cell.setCellValue("");
									} else {
										cell.setCellValue(ao_code);
									}
								}
							} else if (mainLine[j].indexOf("PASS_RATE") >= 0){
								//案件合格率
								String pass_rate = "";
								if (map.containsKey("PASS_RATE") && (map.get("PASS_RATE") != null)) {
									pass_rate = map.get("PASS_RATE").toString() + "%";
								}
								cell.setCellValue(pass_rate);
								
							} else if (mainLine[j].indexOf("EXH_RATE") >= 0){
								//點數兌換率
								String exh_rate = "";
								if (map.containsKey("EXH_RATE") && (map.get("EXH_RATE") != null)) {
									exh_rate = map.get("EXH_RATE").toString() + "%";
								}
								cell.setCellValue(exh_rate);
								
							} else {
								//其餘欄位
								cell.setCellValue(checkMap(map, mainLine[j]));
							}
						}
						index++;
					} 
				}
			} else { 
				row = sheet.createRow(index);
				XSSFCell cell = row.createCell(0);
				cell.setCellStyle(mainStyle);
				cell.setCellValue("查無資料！");
			}
			String filePath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
			File targetFile = new File(filePath, uuid);
			FileOutputStream fos = new FileOutputStream(targetFile);
			
			workbook.write(fos);
			workbook.close();
			notifyClientToDownloadFile("temp//" + uuid, fileName);
		} else {
			throw new APException("無查詢結果可供下載。");
		}
	}
	
	/**
	* 檢查Map取出欄位是否為Null
	*/
	private String checkMap (Map map, String key) {
		if (null != map && null != map.get(key)) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}
	
}