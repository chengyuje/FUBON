package com.systex.jbranch.app.server.fps.crm512;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.Barcode39;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.comutil.parse.JsonUtil;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.webservice.rs.SeniorCitizenClientRS;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.XmlInfo;

@Component("crm512_report")
@Scope("prototype")
public class CRM512_Report extends FubonWmsBizLogic {

	public DataAccessManager dam = null;
	private String apiParam = "SYS.SENIOR_CITIZEN_URL";
	
	private File nowpath = new File(DataManager.getRealPath(), "doc//FPS");
	private File tempPath = new File(DataManager.getRealPath(), "temp//reports");
	private BaseFont bfChinese1;
	private BaseFont bfChinese2;
	private Font tableTitleFont1;
	private Font tableTitleFont2;
	private Font tableContentFont1;
	private Font tableContentFont2;

	public void genCRM512PDF(String loginID, String custID, String fileName) {

		try {
			Document.compress = false;
			Rectangle rectPageSize = new Rectangle(PageSize.A4);// 設定版面
			Document document = new Document(rectPageSize, 10, 10, 15, 80);// 設定邊距(左,右,上,下)

			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(tempPath + "//" + fileName));
			writer.setPageEvent(new MyPageEvent());
			document.open();

			genPDF(writer, loginID, custID, document);
			document.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Document genPDF(PdfWriter writer, String loginID, String custID, Document document) throws Exception {

		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		// start ===
		sb.append("SELECT MEM.EMP_ID, ");
		sb.append("       MEM.EMP_NAME, ");
		sb.append("       MEM.DEPT_ID, ");
		sb.append("       DEFN.DEPT_NAME, ");
		sb.append("       TO_CHAR(SYSDATE, 'YYYY') AS YYYY, ");
		sb.append("       TO_CHAR(SYSDATE, 'MM') AS MM,  ");
		sb.append("       TO_CHAR(SYSDATE, 'DD') AS DD  ");
		sb.append("FROM TBORG_MEMBER MEM ");
		sb.append("LEFT JOIN TBORG_DEFN DEFN ON MEM.DEPT_ID = DEFN.DEPT_ID ");
		sb.append("WHERE MEM.EMP_ID = :loginID ");
		
		queryCondition.setObject("loginID", loginID);
		
		queryCondition.setQueryString(sb.toString());

		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		// end ===
		
		bfChinese1 = BaseFont.createFont(nowpath + "//kaiu.ttf", "Identity-H", BaseFont.NOT_EMBEDDED);
		bfChinese2 = BaseFont.createFont(nowpath + "/EUDC.TTF", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);

		tableTitleFont1 = new Font(bfChinese1, 10, Font.NORMAL);
		tableTitleFont2 = new Font(bfChinese1, 10, Font.NORMAL);
		tableContentFont1 = new Font(bfChinese1, 10, Font.NORMAL);
		tableContentFont2 = new Font(bfChinese2, 10, Font.NORMAL);

		PdfPCell blankCell = buildBlankCell(18, 0);

		// bar code start ===
		PdfContentByte pdfContentByte = writer.getDirectContent();
		
		Barcode39 code39Report = new Barcode39();
		code39Report.setCode("00-1037-01");
		code39Report.setBarHeight(12);
        
        PdfPCell cellReport = new PdfPCell();
        cellReport.setFixedHeight(30);
        cellReport.setPadding(5);	
        cellReport.setBorderWidth(0);
        cellReport.addElement(code39Report.createImageWithBarcode(pdfContentByte, null, null));
        
        Barcode39 code39Cust = new Barcode39();
        code39Cust.setCode(custID);
        code39Cust.setBarHeight(12);
        
        PdfPCell cellCust = new PdfPCell();
        cellCust.setFixedHeight(30);
        cellCust.setPadding(5);	
        cellCust.setBorderWidth(0);
        cellCust.addElement(code39Cust.createImageWithBarcode(pdfContentByte, null, null));
		// bar code end ===
		
		// fubon logo start ===
		Image img = Image.getInstance(nowpath + "//logo.png");
        img.setWidthPercentage(70);
        img.scaleToFit(150 , 150);//大小
        
        PdfPCell cLogo = new PdfPCell();
        cLogo.addElement(img);
        cLogo.setBorder(0);
		// fubon logo end ===
        
        PdfPTable tbTop = buildTable(new int[] { 28, 28, 28 }, 98);
        tbTop.addCell(cLogo); 
        tbTop.addCell(cellCust);
        tbTop.addCell(cellReport);

        document.add(tbTop);
        
		PdfPTable tbHeader = buildTable(new int[] { 28, 28, 28 }, 98);
		
		tbHeader.addCell(blankCell);
		tbHeader.addCell(buildHeaderTitle("高齡客戶資訊觀察表", new Font(bfChinese1, 16, Font.UNDERLINE), Paragraph.ALIGN_CENTER));
		tbHeader.addCell(buildHeaderOther("印表日期：\t" + list.get(0).get("YYYY") + "年\t" + list.get(0).get("MM") + "月\t" + list.get(0).get("DD") + "日", new Font(bfChinese1, 12, Font.NORMAL), Paragraph.ALIGN_RIGHT));

		
		//取異動日期CHG_CREATOR_TIME
		XmlInfo xmlinfo = new XmlInfo();
		Gson gson = JsonUtil.genDefaultGson();
		
		String apiName = "getOldCust_DTL";
		String url = xmlinfo.getVariable(apiParam, apiName, "F3");

		logger.info(apiName + " url:" + url);

		GenericMap inputGmap = new GenericMap();
		if (StringUtils.isNotEmpty(custID)) {
			inputGmap.put("CUST_ID", custID);
		}
		
		logger.info(apiName + " inputVO:" + gson.toJson(inputGmap.getParamMap()));

		List<Map<String, Object>> list2 = new SeniorCitizenClientRS().getList(url, inputGmap);
		String chg_creator_time = "00000000";
		if(list2.size() > 0) {
			for(Map map : list2){
				if(StringUtils.equals("評估結果", (String) map.get("QUESTION_NAME_NAME")))
				chg_creator_time = dealCHG_CREATOR_TIME((String) map.get("CHG_CREATOR_TIME"));
			}
			
		} 

		tbHeader.addCell(blankCell); //空白TABLE
		tbHeader.addCell(blankCell); //空白TABLE
		tbHeader.addCell(buildHeaderOther("異動日期：\t" + chg_creator_time.substring(0,4) + "年\t" + chg_creator_time.substring(4,6) + "月\t" + chg_creator_time.substring(6,8) + "日", new Font(bfChinese1, 12, Font.NORMAL), Paragraph.ALIGN_RIGHT)); //空白TABLE
		
		tbHeader.addCell(blankCell); //空白TABLE
		tbHeader.addCell(blankCell); //空白TABLE
		tbHeader.addCell(blankCell); //空白TABLE
		
		document.add(tbHeader);
		
		// 客戶資訊
		document.add(custINFO(custID));
		
		// 健康及能力-健康情況
		// 健康及能力-能力表現
		// 客戶自述有其化服務需求
		// 認知類-金融認知
		// 評估結果
		document.add(questionINFO(custID));
		
		// 印表資訊
		// bottom
		PdfPTable tbBottom = buildTable(new int[] { 28, 28, 28 }, 98);

		tbBottom.addCell(buildCellWithBorder("印表分行/單位\n\n" + list.get(0).get("DEPT_NAME") + "\n\n", tableTitleFont1, Paragraph.ALIGN_CENTER, -1, -1));
		tbBottom.addCell(buildCellWithBorder("印表人員姓名\n\n" + list.get(0).get("EMP_NAME") + "\n\n", tableTitleFont1, Paragraph.ALIGN_CENTER, -1, -1));
		tbBottom.addCell(buildCellWithBorder("印表人員員編\n\n" + list.get(0).get("EMP_ID") + "\n\n", tableTitleFont1, Paragraph.ALIGN_CENTER, -1, -1));

		document.add(tbBottom);
		
		return document;
	}

	// 新增table
	public PdfPTable buildTable(int[] colLen, int percent) throws Exception {

		PdfPTable table = new PdfPTable(colLen.length);
		table.setWidthPercentage(percent);
		table.setWidths(colLen);

		return table;
	}

	// 空白欄位
	public PdfPCell buildBlankCell(int height, int br) {

		PdfPCell cell = new PdfPCell();

		if (height != -1) {
			cell.setFixedHeight(height);
			cell.setPadding(0);
		}

		if (br != -1) {
			cell.setBorderWidth(br);
		}

		return cell;
	}

	// 標題：高齡客戶資訊觀察表
	private PdfPCell buildHeaderTitle(String value, Font fontStyle, int alignStyle) {

		PdfPCell cell = new PdfPCell();
		cell.setVerticalAlignment(Paragraph.ALIGN_MIDDLE);
		cell.setBorder(0);

		if (value != null) {
			Chunk chunk = new Chunk(value, fontStyle);

			Paragraph text = new Paragraph();
			text.add(chunk);
			text.setAlignment(alignStyle);
			cell.addElement(text);
		}

		return cell;
	}

	// 標題：填表日期
	private PdfPCell buildHeaderOther(String value, Font fontStyle, int alignStyle) {

		PdfPCell cell = new PdfPCell();
		cell.setVerticalAlignment(Paragraph.ALIGN_MIDDLE);
		cell.setBorder(0);

		if (value != null) {
			Chunk chunk = new Chunk(value, fontStyle);

			Paragraph text = new Paragraph();
			text.add(chunk);
			text.setAlignment(alignStyle);
			cell.addElement(text);
		}

		return cell;
	}

	// 內文-table標題
	private PdfPCell buildCellWithBorder(Object value, Font fontStyle, int alignStyle, int height, int border) {
		
		PdfPCell cell = new PdfPCell();

		if (height != -1) {
			cell.setFixedHeight(height);
			cell.setPadding(0);
		}

		if (border != -1) {
			cell.setBorderWidth(border);
		}

		if (value != null) {
			if (value instanceof String) {
				Chunk chunk = new Chunk(value.toString(), fontStyle);
				chunk.setTextRise(2);

				Paragraph text = new Paragraph(10);
				text.add(chunk);
				text.setAlignment(alignStyle);
				cell.addElement(text);
			} else if (value instanceof PdfPTable) {
				cell.addElement((PdfPTable) value);
			}
		}

		return cell;
	}

	public PdfPCell buildCellhardWord(Object value, Font fontStyle1, Font fontStyle2, int alignStyle, int height, int border) {

		PdfPCell cell = new PdfPCell();

		if (height != -1) {
			cell.setFixedHeight(height);
			cell.setPadding(0);
		}

		if (border != -1) {
			cell.setBorderWidth(border);
		}

		if (value != null) {
			if (value instanceof String) {

				Paragraph text = new Paragraph(10);
				text.setAlignment(alignStyle);

				CharsetEncoder enc = Charset.forName("Big5").newEncoder();

				String inputStr = (String) value;
				for (int i = 0; i < inputStr.length(); i++) {

					char c = inputStr.charAt(i);
					String tmpstr = String.valueOf(c);
					if (!enc.canEncode(c)) {
						Chunk chunk = new Chunk(tmpstr, fontStyle2);
						chunk.setTextRise(2);

						text.add(chunk);

					} else {
						Chunk chunk = new Chunk(tmpstr, fontStyle1);
						chunk.setTextRise(2);

						text.add(chunk);
					}
				}

				cell.addElement(text);
			} else if (value instanceof PdfPTable) {
				cell.addElement((PdfPTable) value);
			}
		}

		return cell;
	}

	// 內文-客戶資訊
	private PdfPTable custINFO(String custID) throws Exception {

		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		// 取得客戶資訊 start ===
		sb.append("SELECT CUST_NAME FROM TBCRM_CUST_MAST WHERE CUST_ID = :custID ");
		queryCondition.setObject("custID", custID);

		queryCondition.setQueryString(sb.toString());

		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		// 取得客戶資訊 end ===

		PdfPTable table = buildTable(new int[] { 84 }, 98);
		PdfPCell cell;

		// 客戶姓名
		PdfPTable sub1 = buildTable(new int[] { 8, 74 }, 98);
		sub1.addCell(buildCellWithBorder("客戶姓名", tableTitleFont1, Paragraph.ALIGN_CENTER, -1, -1));
		if (list.size() > 0) {
			sub1.addCell(buildCellhardWord(list.get(0).get("CUST_NAME"), tableContentFont1, tableContentFont2, Paragraph.ALIGN_LEFT, -1, -1));
		} else {
			sub1.addCell(buildCellhardWord("", tableContentFont1, tableContentFont2, Paragraph.ALIGN_LEFT, -1, -1));
		}
		
		cell = new PdfPCell(sub1);
		table.addCell(cell);

		// 身份證字號
		PdfPTable sub2 = buildTable(new int[] { 8, 74 }, 98);
		sub2.addCell(buildCellWithBorder("身份證字號", tableTitleFont1, Paragraph.ALIGN_CENTER, -1, -1));
		sub2.addCell(buildCellhardWord(custID, tableContentFont1, tableContentFont2, Paragraph.ALIGN_LEFT, -1, -1));
		
		cell = new PdfPCell(sub2);
		table.addCell(cell);

		return table;
	}

	// 內文-題目
	private PdfPTable questionINFO(String custID) throws Exception {

		// 取得題目 start ===
		XmlInfo xmlinfo = new XmlInfo();
		Gson gson = JsonUtil.genDefaultGson();
		
		String apiName = "questionINFO";
		String url = xmlinfo.getVariable(apiParam, apiName, "F3");

		logger.info(apiName + " url:" + url);

		GenericMap inputGmap = new GenericMap();
		
		logger.info(apiName + " inputVO:" + gson.toJson(inputGmap.getParamMap()));

		List<Map<String, Object>> list = new SeniorCitizenClientRS().getList(url, inputGmap);
		// 取得題目 end ===

		PdfPTable table = buildTable(new int[] { 84 }, 98);
		PdfPCell cell;

		boolean title01flag = false;
		boolean title02flag = false;

		for (Map<String, Object> map : list) {
			logger.info(apiName + " return:" + map);
			switch ((String) map.get("QUESTION_CLASS")) {
				case "01":
					// 大標題
					if (!title01flag) {
						PdfPTable sub = buildTable(new int[] { 8 }, 98);
						sub.addCell(buildCellWithBorder("【" + (String) map.get("QUESTION_CLASS_NAME") + "】", tableTitleFont1, Paragraph.ALIGN_CENTER, -1, -1));
						cell = new PdfPCell(sub);
						cell.setVerticalAlignment(Paragraph.ALIGN_MIDDLE);
						cell.setBackgroundColor(new Color(243, 243, 243));
						table.addCell(cell);
	
						title01flag = true;
					}
	
					// 小標題&答案
					switch ((String) map.get("QUESTION_NAME")) {
						case "01_REMARK": // 備註欄
							String remark = getRemark((String) map.get("QUESTION_VERSION"), (String) map.get("EXAM_VERSION"), custID);
							if (StringUtils.equals("null", remark) || null == remark) {
								remark = "\n \n ";
							} else {
								remark = remark + "\n ";
							}
							
							PdfPTable subRemark = buildTable(new int[] { 100 }, 98);
							subRemark.addCell(buildCellhardWord((String) map.get("QUESTION_DESCR") + "：\n" + remark, tableContentFont1, tableContentFont2, Paragraph.ALIGN_LEFT, -1, -1));
							
							cell = new PdfPCell(subRemark);
							cell.setVerticalAlignment(Paragraph.ALIGN_MIDDLE);
							table.addCell(cell);
							
							
							break;
						default: // 健康情況/能力表現
							PdfPTable sub = buildTable(new int[] { 8, 74 }, 98);
							sub.addCell(setMiddle(buildCellWithBorder_rowspan(map.get("QUESTION_NAME_NAME"), tableTitleFont1, Paragraph.ALIGN_CENTER, 3, -1, -1)));
							sub.addCell(buildCellhardWord((String) map.get("QUESTION_DESCR"), tableContentFont1, tableContentFont2, Paragraph.ALIGN_LEFT, -1, 0));
							sub.addCell(buildCellhardWord(getOption((String) map.get("QUESTION_VERSION"), (String) map.get("EXAM_VERSION"), custID), tableContentFont1, tableContentFont2, Paragraph.ALIGN_LEFT, -1, 0));
							if (null != (String) map.get("QUESTION_REMARK")) {
								sub.addCell(buildCellhardWord((String) map.get("QUESTION_REMARK"), new Font(bfChinese1, 10, Font.BOLD), tableContentFont2, Paragraph.ALIGN_LEFT, -1, 0));
							}
							
							cell = new PdfPCell(sub);
							cell.setVerticalAlignment(Paragraph.ALIGN_MIDDLE);
							table.addCell(cell);
							
							break;
					}
					
					break;
				case "02":
					if (!title02flag) {
						PdfPTable sub = buildTable(new int[] { 8 }, 98);
						sub.addCell(buildCellWithBorder("【" + (String) map.get("QUESTION_CLASS_NAME") + "】", tableTitleFont1, Paragraph.ALIGN_CENTER, -1, -1));
						
						cell = new PdfPCell(sub);
						cell.setVerticalAlignment(Paragraph.ALIGN_MIDDLE);
						cell.setBackgroundColor(new Color(243, 243, 243));
						
						table.addCell(cell);
	
						title02flag = true;
					}
					
					PdfPTable sub = buildTable(new int[] { 8, 74 }, 98);
					sub.addCell(setMiddle(buildCellWithBorder_rowspan(map.get("QUESTION_NAME_NAME"), tableTitleFont1, Paragraph.ALIGN_CENTER, 3, -1, -1)));
					sub.addCell(buildCellhardWord((String) map.get("QUESTION_DESCR"), tableContentFont1, tableContentFont2, Paragraph.ALIGN_LEFT, -1, 0));
					sub.addCell(buildCellhardWord(getOption((String) map.get("QUESTION_VERSION"), (String) map.get("EXAM_VERSION"), custID), tableContentFont1, tableContentFont2, Paragraph.ALIGN_LEFT, -1, 0));
					if (null != (String) map.get("QUESTION_REMARK")) {
						sub.addCell(buildCellhardWord((String) map.get("QUESTION_REMARK"), new Font(bfChinese1, 10, Font.BOLD), tableContentFont2, Paragraph.ALIGN_LEFT, -1, 0));
					}
					
					cell = new PdfPCell(sub);
					cell.setVerticalAlignment(Paragraph.ALIGN_MIDDLE);
					table.addCell(cell);
	
					break;
				case "99":
					PdfPTable sub99 = buildTable(new int[] { 8, 74 }, 98);
					sub99.addCell(setMiddle(buildCellWithBorder_rowspan((String) map.get("QUESTION_CLASS_NAME"), tableTitleFont1, Paragraph.ALIGN_CENTER, 4, -1, -1)));
					sub99.addCell(buildCellhardWord(getOption99((String) map.get("QUESTION_VERSION"), (String) map.get("EXAM_VERSION"), custID, "NO_CHECK"), tableContentFont1, tableContentFont2, Paragraph.ALIGN_LEFT, -1, 0));
					sub99.addCell(buildCellhardWord(getOption99((String) map.get("QUESTION_VERSION"), (String) map.get("EXAM_VERSION"), custID, "HAVE_CHECK"), tableContentFont1, tableContentFont2, Paragraph.ALIGN_LEFT, -1, 2));

					cell = new PdfPCell(sub99);
					cell.setVerticalAlignment(Paragraph.ALIGN_MIDDLE);
					table.addCell(cell);
					
					break;
			}
		}
		
		return table;
	}

	private String dealCHG_CREATOR_TIME(String date1) throws ParseException {
		if(null == date1) {
			return "00000000";
		}
		try {
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");		
			return sdf2.format(sdf1.parse(date1));
		} catch (Exception e) {
			logger.error("CRM512_Report dealCHG_CREATOR_TIME()異動日期轉換錯誤");
			return "00000000";
		}
		
	}

	// cell with rowspan
	private PdfPCell buildCellWithBorder_rowspan(Object value, Font fontStyle, int alignStyle, int row, int height, int border) {
		
		PdfPCell cell = new PdfPCell();

        if (height != -1) {
            cell.setFixedHeight(height);
            cell.setPadding(0);
        }

        if (border != -1) {
            cell.setBorderWidth(border);
        }

        if (value != null) {
            if (value instanceof String) {
                Chunk chunk = new Chunk(value.toString(), fontStyle);
                chunk.setTextRise(2);
                Paragraph text = new Paragraph(10);
                text.add(chunk);
                text.setAlignment(alignStyle);
                cell.addElement(text);
            } else if (value instanceof PdfPTable) {
                cell.addElement((PdfPTable) value);
            }
        }
        
        cell.setRowspan(row);
        
        return cell;
    }
	
	public static String addStrBehind(String content, char addStr, int lineLength, boolean isFront) {

		int length = 0;
		for (char chr : content.toCharArray()) {
			length += String.valueOf(chr).getBytes().length >= 2 ? 2 : 1;
		}

		content = content == null ? "" : content.replaceAll("　*", "").trim();
		StringBuffer sbr = new StringBuffer(content);

		for (int i = 0; i < lineLength - length; i++) {
			if (isFront)
				sbr.insert(0, addStr);
			else
				sbr.append(addStr);
		}

		return sbr.toString();
	}

	// 垂直置中
	private PdfPCell setMiddle(PdfPCell cell) {
		
        cell.setVerticalAlignment(Paragraph.ALIGN_MIDDLE);
        
        return cell;
    }
	
	// 一般題目選項
	private String getOption(String questionVersion, String examVersion, String custID) throws JBranchException, Exception {

		// 取得答案 start ===
		XmlInfo xmlinfo = new XmlInfo();
		Gson gson = JsonUtil.genDefaultGson();
		
		String apiName = "getOption";
		String url = xmlinfo.getVariable(apiParam, apiName, "F3");
		
		logger.info(apiName + " url:" + url);

		GenericMap inputGmap = new GenericMap();
		inputGmap.put("CUST_ID", custID);
		inputGmap.put("EXAM_VERSION", examVersion);
		inputGmap.put("QUESTION_VERSION", questionVersion);
		
		logger.info(apiName + " inputVO:" + gson.toJson(inputGmap.getParamMap()));

		List<Map<String, Object>> list = new SeniorCitizenClientRS().getList(url, inputGmap);
		// 取得答案 end ===
		
		StringBuilder options = new StringBuilder();
		
		for (int i = 0; i < list.size(); i++) {
			logger.info(apiName + " return:" + list.get(i));

			options.append(list.get(i).get("CUST_ANSWER") + "\n");
		}
		
		return options.toString();
	}
	
	// 備註
	private String getRemark(String questionVersion, String examVersion, String custID) throws JBranchException, Exception {
		
		// 取得答案 start ===
		XmlInfo xmlinfo = new XmlInfo();
		Gson gson = JsonUtil.genDefaultGson();
		
		String apiName = "getRemark";
		String url = xmlinfo.getVariable(apiParam, apiName, "F3");

		logger.info(apiName + " url:" + url);

		GenericMap inputGmap = new GenericMap();
		inputGmap.put("CUST_ID", custID);
		inputGmap.put("EXAM_VERSION", examVersion);
		inputGmap.put("QUESTION_VERSION", questionVersion);
		
		logger.info(apiName + " inputVO:" + gson.toJson(inputGmap.getParamMap()));

		List<Map<String, Object>> list = new SeniorCitizenClientRS().getList(url, inputGmap);
		// 取得答案 end ===
		
		StringBuilder options = new StringBuilder();
		
		for (int i = 0; i < list.size(); i++) {
			logger.info(apiName + " return:" + list.get(i));

			options.append(list.get(i).get("ANSWER_REMARK"));
		}
		
		return options.toString();
	}
	
	// 評估結果選項
	private String getOption99(String questionVersion, String examVersion, String custID, String block) throws JBranchException, Exception {

		// 取得答案 start ===
		XmlInfo xmlinfo = new XmlInfo();
		Gson gson = JsonUtil.genDefaultGson();
		
		String apiName = "getOption99";
		String url = xmlinfo.getVariable(apiParam, apiName, "F3");

		logger.info(apiName + " url:" + url);

		GenericMap inputGmap = new GenericMap();
		inputGmap.put("CUST_ID", custID);
		inputGmap.put("EXAM_VERSION", examVersion);
		inputGmap.put("QUESTION_VERSION", questionVersion);
		
		logger.info(apiName + " inputVO:" + gson.toJson(inputGmap.getParamMap()));

		List<Map<String, Object>> list = new SeniorCitizenClientRS().getList(url, inputGmap);
		// 取得答案 end ===
		
		StringBuilder options = new StringBuilder();
		
		for (Map<String, Object> map : list) {
			logger.info(apiName + " return:" + map);

			if (StringUtils.equals(block, (String) map.get("SEQ_TYPE"))) {
				options.append(map.get("ANSWER_DESC"));
			}
		}
		
		return options.toString();
	}
	
	class MyPageEvent extends PdfPageEventHelper {

		private PdfTemplate total;

		/** Creates the PdfTemplate that will hold the total number of pages. **/
		public void onOpenDocument(PdfWriter writer, Document document) {
			total = writer.getDirectContent().createTemplate(150, 20);
		}
		
		@Override
	    public void onStartPage(PdfWriter writer, Document document) {
			
		}
	}

}
