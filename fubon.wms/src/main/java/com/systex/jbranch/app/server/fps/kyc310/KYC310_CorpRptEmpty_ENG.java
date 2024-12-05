package com.systex.jbranch.app.server.fps.kyc310;


import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.systex.jbranch.common.xmlInfo.XMLInfo;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.util.PlatformContext;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.file.Paths;
import java.sql.Blob;
import java.util.List;
import java.util.*;

import static com.systex.jbranch.fubon.bth.job.context.AccessContext.tempReportPath;

/**
 * KYC法人空白問卷英文版
 */
@Component("kyc310CorpRptEmptyENG")
@Scope("prototype")
public class KYC310_CorpRptEmpty_ENG extends FubonWmsBizLogic {
    /**
     * KYC 相關資料
     **/
    private KYC310InputVO inputVO;

    /**
     * input setter
     *
     * @param inputVO
     */
    public void setInputVO(KYC310InputVO inputVO) {
        this.inputVO = inputVO;
    }

	/** 資源路徑 **/
	private File source;

    /**
     * 報表容器
     **/
    private Document doc;

    /**
     * 報表物件
     **/
    private PdfWriter pdfWriter;

    /**
     * 報表路徑
     **/
    private String reportPath;

    /**
     * 報表名稱
     */
    private String reportName;
    private String year;
    private String month;
    private String day;

	private Font nFont9;
	private Font nFont9White;
	private Font bFont10;
	private Font nFont10;
	private Font uFont10;
	private Font uFont10eudc;
	private Font nFont13;
	private Font bFont14;
	private Font nFont16;
	private Font bFont16;
	private Font nFont20;
	private Color qstColor;

    /**
     * 空白KYC
     */
    public String genBlankPDF() throws Exception {
        initialize();
        createDocument();
        createPdfWriter();

        doc.open();
        genPDF();
        doc.newPage();
        doc.close();
        return Paths.get("temp", "reports", reportName).toString();
    }

    /**
     * 建立報表容器
     **/
    private void createDocument() {
        Document.compress = false;
        Rectangle rectPageSize = new Rectangle(PageSize.A4);// 設定版面
        doc = new Document(rectPageSize, 20, 10, 20, 75);// 設定邊距(左,右,上,下)
    }

    /**
     * 建立 PdfWriter
     **/
    private void createPdfWriter() throws FileNotFoundException, DocumentException {
        PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(reportPath));
        writer.setPageEvent(new MyPageEvent());
    }

    /**
     * 初始字體物件，報表路徑
     **/
    private void initialize() throws IOException, DocumentException {
        /** 資源路徑 **/
        source = new File(DataManager.getRealPath(), "doc//FPS");

		/** 載入字型 **/
		BaseFont kaiu = BaseFont.createFont(source + "//kaiu.ttf", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
		BaseFont eudc = BaseFont.createFont(source + "/EUDC.TTF", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);

		nFont9 = new Font(kaiu, 9, Font.NORMAL);
		nFont9White = new Font(kaiu, 9, Font.NORMAL);
		nFont9White.setColor(Color.WHITE);

		nFont10 = new Font(kaiu, 10, Font.NORMAL);
		bFont10 = new Font(kaiu, 10, Font.BOLD);
		uFont10 = new Font(kaiu, 10, Font.UNDERLINE);
		uFont10eudc = new Font(eudc, 10, Font.UNDERLINE);

		nFont13 = new Font(kaiu, 13, Font.NORMAL);

		bFont14 = new Font(kaiu, 14, Font.BOLD);
		
		nFont16 = new Font(kaiu, 16, Font.NORMAL);
		bFont16 = new Font(kaiu, 16, Font.BOLD);

		nFont20 = new Font(kaiu, 20, Font.NORMAL);

		qstColor = new Color(55, 169, 203);

        /** 製表相關時間資訊 **/
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        year = String.valueOf(calendar.get(Calendar.YEAR));//年
        month = String.valueOf(calendar.get(Calendar.MONTH) + 1);//月
        day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));//日

        /** 報表路徑 **/
        reportName = UUID.randomUUID().toString();
        reportPath = tempReportPath + reportName;
    }

    /**
     * 產出PDF
     *
     * @throws IOException
     * @throws
     */
    private Document genPDF() throws Exception {
		genTopContent();
//		doc.add(buildPartTable());

		PdfPTable tbTextTmp = buildTable(new int[]{100});
		tbTextTmp.addCell(buildBlankCell(5, 0));
		doc.add(tbTextTmp);
		doc.add(buildPart1Table());

		PdfPTable tbTextLine = buildTable(new int[]{100});
		tbTextLine.addCell(buildBlankCell(10, 0));
		doc.add(tbTextLine);
        doc.add(buildRemindTable());
        doc.add(tbTextLine);
        doc.add(buildRemindTable2());
        genDownContent();
        return doc;
    }

    /** 上半部相關資訊 **/
    private void genTopContent() throws Exception {
        int lineLength = 26;
        char addStr = ' ';

        PdfPCell blank = buildBlankCell(18, 0);
        PdfPTable blankTb = buildTable(new int[]{84});
        blankTb.addCell(blank);

        PdfPTable tbTitleTable = buildTable(new int[]{84});
        PdfPCell cell = buildTitle("\nCustomer Risk Attribute Assessment Questionnaire(For Corporate or Institution)", bFont14, Paragraph.ALIGN_CENTER);
        cell.setPaddingTop(10);
        cell.setPaddingBottom(10);
        tbTitleTable.addCell(cell);

        PdfPTable tbText1 = buildTable(new int[]{17, 82});
        tbText1.addCell(buildTitle("Test Date：", nFont10, Paragraph.ALIGN_LEFT));
        tbText1.addCell(buildTitle("□□□□/□□/□□", nFont16, Paragraph.ALIGN_LEFT));

        PdfPTable tbText2 = buildTable(new int[]{17, 82});
        tbText2.addCell(buildTitle("Print Date：", nFont10, Paragraph.ALIGN_LEFT));
        tbText2.addCell(buildTitle(addStrBehind("", addStr, lineLength), nFont10, Paragraph.ALIGN_LEFT));


        PdfPTable tbText3 = buildTable(new int[]{17, 30, 3, 20, 30});
        tbText3.addCell(buildTitle("Company Name：", nFont10, Paragraph.ALIGN_LEFT));
        tbText3.addCell(buildCellhardWordTitle(addStrBehind("", addStr, lineLength), uFont10, uFont10eudc, Paragraph.ALIGN_LEFT));

        tbText3.addCell(blank);
        tbText3.addCell(buildTitle("Responsible Person：", nFont10, Paragraph.ALIGN_LEFT));
        tbText3.addCell(buildCellhardWordTitle(addStrBehind("", addStr, lineLength), uFont10, uFont10eudc, Paragraph.ALIGN_LEFT));

        PdfPTable tbText4 = buildTable(new int[]{17, 30, 3, 20, 30});
        tbText4.addCell(buildTitle("Company ID：", nFont10, Paragraph.ALIGN_LEFT));
        tbText4.addCell(buildTitle(addStrBehind("", addStr, lineLength), uFont10, Paragraph.ALIGN_LEFT));

        tbText4.addCell(blank);
        tbText4.addCell(buildTitle("Contact Number：", nFont10, Paragraph.ALIGN_LEFT));
        tbText4.addCell(buildTitle(addStrBehind("", addStr, lineLength), uFont10, Paragraph.ALIGN_LEFT));

        PdfPTable tbText5 = buildTable(new int[]{17, 82});
        tbText5.addCell(buildTitle("Mailing Address：", nFont10, Paragraph.ALIGN_LEFT));
        tbText5.addCell(buildTitle(addStrBehind("", addStr, 84), uFont10, Paragraph.ALIGN_LEFT));

        PdfPTable tbText6 = buildTable(new int[]{100});
        tbText6.addCell(buildTitle("This questionnaire can help assess the company's risk attribute, and to protect the company's interests, please fill the answers based on actual situations of the company. The analysis results of this questionnaire is valid for one year.", bFont10, Paragraph.ALIGN_LEFT));
//        tbText6.addCell(blank);
//        
        PdfPTable tbText7 = buildTable(new int[]{17, 30, 23, 30});
        tbText7.addCell(buildTitle("E-mail Address：", nFont10, Paragraph.ALIGN_LEFT));
        tbText7.addCell(buildTitle(addStrBehind("", addStr, lineLength), uFont10, Paragraph.ALIGN_LEFT));
        tbText7.addCell(buildTitle("Updated E-mail Address：", nFont10, Paragraph.ALIGN_LEFT));
        tbText7.addCell(buildTitle(addStrBehind("", addStr, lineLength), uFont10, Paragraph.ALIGN_LEFT));

        doc.add(tbTitleTable);//客戶風險屬性評估問卷（適用於法人或機構）
//        doc.add(blankTb);//客戶風險屬性評估問卷（適用於法人或機構）
        doc.add(tbText1);//問卷測驗日期
        doc.add(tbText2);//問卷列印日期
        doc.add(tbText3);//公司/機構名稱、負責人姓名
        doc.add(tbText4);//統 一 編 號、電話
        doc.add(tbText5);//通訊地址
        doc.add(tbText7);//電子郵件
        doc.add(tbText6);//通訊地址
    }

    private PdfPTable buildPartTable() throws Exception {
        PdfPTable tbPart1 = buildTable(new int[]{74, 23});
        tbPart1.addCell(buildBlankCell(10, 0));
        tbPart1.addCell(buildSection("", nFont10, Paragraph.ALIGN_BOTTOM));//本問卷分析結果有效期限一年
        return tbPart1;
    }

    /** 試題部分 **/
    private PdfPTable buildPart1Table() throws Exception {
        PdfPTable tbPart1 = buildTable(new int[]{8, 5, 87});
        PdfPCell blank = buildBlankCell(18, 0);

        int Q = 1;//題號
        for (Map<String, Object> questionDesc : inputVO.getQuest_list()) {
        	Object ansList = questionDesc.get("ANSWER_LIST");
            List<Map<String, Object>> ansList_change = (List<Map<String, Object>>) ansList;

            PdfPCell cPart1 = buildCell(questionDesc.get("QUESTION_DESC_ENG"), nFont9White, Paragraph.ALIGN_LEFT, -1, 0);
            cPart1.setBackgroundColor(qstColor);
            //複選
            String elsecheckbox = "";
            if(Q == 3) {
            	//第三題不用在前面顯示CheckBox
            	tbPart1.addCell(buildCell(" ", nFont13, Paragraph.ALIGN_RIGHT, -1, 0));
            } else {
	            if (!"S".equals(questionDesc.get("QUESTION_TYPE").toString())) {
	                String checkbox = "";
	                for (int i = 0; i < ansList_change.size(); i++) {
	                    if (i < 3) {
	                        checkbox += "□";
	                    } else {
	                        elsecheckbox += "□";
	                    }
	
	                }
	                tbPart1.addCell(buildCell(checkbox, nFont13, Paragraph.ALIGN_RIGHT, -1, 0));
	
	            } else {
	                tbPart1.addCell(buildCell("□", nFont13, Paragraph.ALIGN_RIGHT, -1, 0));
	            }
            }

            tbPart1.addCell(buildCell("Q" + Q, bFont10, Paragraph.ALIGN_CENTER, -1, 0));
            tbPart1.addCell(cPart1);
            
            //答案選項
            if(Q == 3) {
        		//第3題特殊處理
        		tbPart1.addCell(blank);
                tbPart1.addCell(blank);
                tbPart1.addCell(buildCell(getQ3Ans(), nFont9, Paragraph.ALIGN_LEFT, -1, 0));
        	} else if(Q == 9) {
        		//第9題特殊處理
        		tbPart1.addCell(blank);
                tbPart1.addCell(blank);
                tbPart1.addCell(buildCell(getQ9Ans(questionDesc), nFont9, Paragraph.ALIGN_LEFT, -1, 0));
        	} else {
                if (Q == 1 || Q == 8) {
                    String answerSD1 = "";
                    for (Map<String, Object> map : ansList_change) {
                        tbPart1.addCell(blank);
                        tbPart1.addCell(blank);
                        answerSD1 = map.get("ANSWER_DESC_ENG").toString();
                        tbPart1.addCell(buildCell(answerSD1, nFont9, Paragraph.ALIGN_LEFT, -1, 0));

                    }
                } else {
                    String answerSD1 = "";
                    String answerSD2 = "";
                    for (int j = 0; j < ansList_change.size(); j++) {
                        if (j < 3) {
                            answerSD1 += ansList_change.get(j).get("ANSWER_DESC_ENG").toString() + "　　　";
                        } else {
                            answerSD2 += ansList_change.get(j).get("ANSWER_DESC_ENG").toString() + "　　　";
                        }
                    }
                    tbPart1.addCell(buildCell(elsecheckbox, nFont13, Paragraph.ALIGN_RIGHT, -1, 0));
                    tbPart1.addCell(blank);
                    tbPart1.addCell(buildCell(answerSD1, nFont9, Paragraph.ALIGN_LEFT, -1, 0));
                    if(StringUtils.isNotBlank(answerSD2)) {
                        tbPart1.addCell(blank);
                        tbPart1.addCell(blank);
                        tbPart1.addCell(buildCell(answerSD2, nFont9, Paragraph.ALIGN_LEFT, -1, 0));
                    }
                }
        	}       
            Q++;
        }
        
        return tbPart1;
    }

    private PdfPTable getQ3Ans() throws Exception {
    	XMLInfo xmlinfo = new XMLInfo();
    	List<Map<String, Object>> prodExpList = xmlinfo.getXMLInfo("KYC.Q3_PROD_EXP_ENG");
    	List<Map<String, Object>> prodTypeList = xmlinfo.getXMLInfo("KYC.Q3_PROD_TYPE_ENG");
    	
    	//第3題特殊處理
    	PdfPTable q3table = buildTable(new int[]{11,7,7,8,8,7});
    	for(int i = -1; i < prodTypeList.size(); i++) {
    		for(int j = -1; j < prodExpList.size(); j++) {
    			if(i == -1 && j == -1) {
    				q3table.addCell(setMiddle(buildCellQ3("", nFont9, Paragraph.ALIGN_CENTER, 12, (float)0.1)));
    			} else if(i == -1 && j > -1) {
    				q3table.addCell(setMiddle(buildCellQ3(prodExpList.get(j).get("PARAM_NAME").toString(), nFont9, Paragraph.ALIGN_CENTER, 24, (float)0.1)));
    			} else if(i > -1 && j == -1) {
    				if(i == prodTypeList.size() - 1) {
    					int pLen = StringUtils.length(prodTypeList.get(i).get("PARAM_NAME").toString());
    					int pIdx = StringUtils.indexOf(prodTypeList.get(i).get("PARAM_NAME").toString(), "(");
    					String pName = StringUtils.substring(prodTypeList.get(i).get("PARAM_NAME").toString(), 0, pIdx) + "\n" 
    								 + StringUtils.substring(prodTypeList.get(i).get("PARAM_NAME").toString(), pIdx, pLen);
    					int height = (i == prodTypeList.size() - 1 ? 32 : 22);//衍生性商品字數較多需要高一點
    					q3table.addCell(setMiddle(buildCellQ3(pName, nFont9, Paragraph.ALIGN_CENTER, height, (float)0.1)));
    				} else {
    					q3table.addCell(setMiddle(buildCellQ3(prodTypeList.get(i).get("PARAM_NAME").toString(), nFont9, Paragraph.ALIGN_CENTER, 12, (float)0.1)));
    				}
    			} else  if(i > -1 && j > -1) {
    				q3table.addCell(setMiddle(buildCellQ3("□", nFont9, Paragraph.ALIGN_CENTER, 12, (float)0.1)));
    			}
    		}
    	}
		
		return q3table;
    }

    private PdfPCell buildCellQ3(Object value, Font font, int alignment, int height, float br) {
    	PdfPCell cell = buildCell(value, font, alignment, height, (int)br);
        
    	cell.setBorderWidth(br);
    	cell.setBorderColor(new Color(224,224,224));
    	
        return cell;
    }
    
    /**
     * 設定垂直置中
     **/
    private PdfPCell setMiddle(PdfPCell cell) {
        cell.setVerticalAlignment(Paragraph.ALIGN_MIDDLE);
        return cell;
    }

    /**
     * 取得答案選項
     * @throws Exception 
     **/
    private PdfPTable getQ9Ans(Map map) throws Exception {
    	PdfPTable q9table = buildTable(new int[]{83});
    	q9table.getDefaultCell().setBorderWidth(0);
    	
    	//第9題答案
        PdfPTable anstable = buildTable(new int[]{60});
        anstable.getDefaultCell().setBorderWidth(0);
        for (Map ansMap : (List<Map>)map.get("ANSWER_LIST")) {
        	Font q9Font = new Font(nFont9);
        	q9Font.setColor(new Color(0, 0, 0));
    		PdfPCell cell = setMiddle(buildCell(ansMap.get("ANSWER_DESC_ENG"), q9Font, Paragraph.ALIGN_LEFT, 35, 0));
    		cell.setFixedHeight(24);
    		anstable.addCell(cell);
        }
        
        q9table.addCell(anstable);
        
        return q9table;
    }
    
    /** 提醒區域第一部分 **/
    private PdfPTable buildRemindTable() throws Exception {
        PdfPTable table = buildTable(new int[]{84});
        StringBuffer sb = new StringBuffer();
        sb		.append("Notes：1. The questionnaire filled by the company and the analysis result are only for products suitability\n")
                .append("  　　　  assessment in transaction instruction of the company to Taipei Fubon Bank ('the Bank').\n")
                .append("  　　　  The company understands and confirms that the Bank does not guarantee or promise the investment\n")
                .append("  　　　  result, investment profits and no losses in investment principals.\n")
                .append("　　　 2. The analysis result of this questionnaire is valid for one year. The Bank will take the last valid\n")
                .append("  　　　  result as the accordance for products suitability assessment in every the company's transaction\n")
                .append("  　　　  instruction except periodic recurring transactions (i.e., regular savings plan for mutual funds).\n")
                .append("  　　　  However, if the analysis result is adjusted which leads to themismatch of products suitability\n")
                .append("  　　　  assessment for such periodic recurring transactions, the company can only continue the initial\n")
                .append("  　　　  investment agreement but not to increase the investment amount, the frequency of investment or\n")
                .append("  　　　  resumption in investment. In that situation, the company is advised to evaluate the terms of the\n")
                .append("  　　　  initial investment agreement.\n")
                .append("　　　 3. The company's risk attribute assessment shall be viewed regularly, and the company may apply at\n")
                .append("  　　　  business units of the Bank or via Internet Banking or Mobile Banking. * Applying at business units\n")
                .append("  　　　  only If the company have valid pre-settlement risk credit.\n")
                .append("　　　 4. In order to protect the right and interests for the company, the Bank may confirm the questionnaire\n")
                .append("  　　　  contents by phone.\n");

        table.addCell(buildCell(sb.toString(), nFont10, Paragraph.ALIGN_LEFT, -1, 0));

        return table;
    }

	/** 提醒區域第二部分 **/
    private PdfPTable buildRemindTable2() throws Exception {
        PdfPTable table = buildTable(new int[]{84});
        table.addCell(buildCell("Confirmations and Declarations：", bFont10, Paragraph.ALIGN_LEFT, -1, 0));
        table.addCell(buildCell("The company declares that the company fully understands the contents of this questionnaire and all the filled contents accurately and correctly reflect the company's current financial status and investment objectives."
                , bFont10, Paragraph.ALIGN_LEFT, -1, 0));
        return table;
    }

    /**
     * 製作下方簽名印鑑、來往帳號，銀行註記等內容
     **/
    private void genDownContent() throws Exception {
        PdfPCell blank = buildBlankCell(18, 0);
        PdfPTable tbText9 = buildTable(new int[]{100});
        tbText9.addCell(blank);

        doc.add(tbText9);
        doc.add(genUserSignatureColumn());//簽名印鑑
        doc.add(tbText9);
//        doc.add(tbText9);
        doc.add(genBankNoteColumn());//銀行註記欄
    }

    /**
     * 製作下方區塊客戶相關需要填寫區塊
     **/
    private PdfPTable genUserSignatureColumn() throws Exception {
        PdfPTable table = buildTable(new int[]{24, 43, 33});
        table.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.setWidthPercentage(95);

        table.addCell(buildCell(" Company Name：", nFont10, Paragraph.ALIGN_LEFT, -1, 0));
        table.addCell(buildCell("　　　　　　　　　　　　　　　　　　　", uFont10, Paragraph.ALIGN_LEFT, -1, 0));

        PdfPCell cell = buildCell("", bFont10, Paragraph.ALIGN_LEFT, -1, 2);
        cell.setBorder(0);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setRowspan(7);
        table.addCell(cell);

        int lineLength = 38;
        char addStr = ' ';
        table.addCell(buildTitle(" Representative：", nFont10, Paragraph.ALIGN_LEFT));
        table.addCell(buildTitle(addStrFront("(Please sign)", addStr, lineLength), uFont10, Paragraph.ALIGN_LEFT));
        table.addCell(buildTitle(" Company ID：", nFont10, Paragraph.ALIGN_LEFT));
        table.addCell(buildTitle(addStrBehind("", addStr, lineLength), uFont10, Paragraph.ALIGN_LEFT));
        table.addCell(buildTitle( " Account Number：", nFont10, Paragraph.ALIGN_LEFT));
        table.addCell(buildTitle("□□□□□□□□□□□□", nFont16, Paragraph.ALIGN_LEFT));

        PdfPCell tipCell = buildTitle( new StringBuilder()
                .append("(The Representative please sign in person and affix one of the following specimen seals: please take the priority of the retained specimen seal for trust account, otherwise the specimen seal for other account or the authorized seals retained on \"Client Information Form for Bond Business\")")
                .toString(), nFont10, Paragraph.ALIGN_LEFT);
        tipCell.setColspan(3);
		table.addCell(tipCell);

        table.addCell(buildTitle(" Authorized Person：", nFont10, Paragraph.ALIGN_LEFT));
        table.addCell(buildTitle(addStrFront("(Please sign)", addStr, lineLength), uFont10, Paragraph.ALIGN_LEFT));
        table.addCell(buildTitle(" ID Number：", nFont10, Paragraph.ALIGN_LEFT));
        table.addCell(buildTitle(addStrBehind("", addStr, lineLength), uFont10, Paragraph.ALIGN_LEFT));
        return table;
    }

    /**
     * 製作銀行註記欄
     **/
    private PdfPTable genBankNoteColumn() throws Exception {
    	PdfPTable table = buildTable(new int[]{84}, 100);
        PdfPTable inner = buildTable(new int[]{17, 16, 21, 13, 17});

        inner.addCell(buildCell("For Bank Use Only：", nFont9, Paragraph.ALIGN_LEFT, -1, 0));
        inner.addCell(buildBlankCell(18, 0));
        inner.addCell(buildBlankCell(18, 0));
        inner.addCell(buildBlankCell(18, 0));
        inner.addCell(buildBlankCell(18, 0));
        inner.addCell(buildCell("Business Unit：", nFont9, Paragraph.ALIGN_LEFT, -1, 0));
        inner.addCell(buildBlankCell(18, 0));
        inner.addCell(buildBlankCell(18, 0));
        inner.addCell(buildBlankCell(18, 0));
        inner.addCell(buildBlankCell(18, 0));

        inner.addCell(buildCell("Explanation：_________", nFont9, Paragraph.ALIGN_LEFT, -1, 0));
        inner.addCell(buildCell("Witness by：_________", nFont9, Paragraph.ALIGN_LEFT, -1, 0));
        inner.addCell(buildCell("Seal Verification：_________", nFont9, Paragraph.ALIGN_LEFT, -1, 0));
        inner.addCell(buildCell("Key-in：_________", nFont9, Paragraph.ALIGN_LEFT, -1, 0));
        inner.addCell(buildCell("Supervisor：_________", nFont9, Paragraph.ALIGN_LEFT, -1, 0));

        table.addCell(inner);
        return table;
    }

    private PdfPCell buildBlankCell(int height, int br) {
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

    private PdfPTable buildTable(int[] colLen) throws Exception {
        return buildTable(colLen, 98);
    }

    private PdfPTable buildTable(int[] colLen, int percent) throws Exception {
        PdfPTable table = new PdfPTable(colLen.length);
        table.setWidthPercentage(percent);
        table.setWidths(colLen);
        return table;
    }

    private PdfPCell buildTitle(String value, Font font, int alignment) {
        PdfPCell cell = new PdfPCell();
        cell.setVerticalAlignment(Paragraph.ALIGN_MIDDLE);
        cell.setBorder(0);

        if (value != null) {
            value = value.replaceFirst("  $", "　");
            Chunk chunk = new Chunk(value, font);
            Paragraph text = new Paragraph();
            text.add(chunk);
            text.setAlignment(alignment);
            cell.addElement(text);
        }

        return cell;
    }

    private PdfPCell buildCellhardWordTitle(String value, Font font1, Font font2, int aligment) {
        PdfPCell cell = new PdfPCell();
        cell.setVerticalAlignment(Paragraph.ALIGN_MIDDLE);
        cell.setBorder(0);

        if (value != null) {
            value = value.replaceFirst("  $", "　");

            Paragraph text = new Paragraph();
            text.setAlignment(aligment);

            CharsetEncoder enc = Charset.forName("Big5").newEncoder();

            String inputStr = (String) value;
            for (int i = 0; i < inputStr.length(); i++) {
                char c = inputStr.charAt(i);
                String tmpstr = String.valueOf(c);
                if (!enc.canEncode(c)) {
                    Chunk chunk = new Chunk(tmpstr, font2);
                    chunk.setTextRise(2);
                    text.add(chunk);
                } else {
                    Chunk chunk = new Chunk(tmpstr, font1);
                    chunk.setTextRise(2);
                    //System.out.println("tmpstr : "+tmpstr);
                    text.add(chunk);
                }
            }
            cell.addElement(text);
        }
        return cell;
    }

    private PdfPCell buildSection(Object value, Font font, int alignment) {
        PdfPCell cell = new PdfPCell();
        cell.setFixedHeight(18);
        cell.setPadding(0);
        cell.setBorder(0);

        if (value != null) {
            Chunk chunk = new Chunk(value.toString(), font);
            chunk.setTextRise(4);
            Paragraph text = new Paragraph();
            text.add(chunk);
            text.setAlignment(alignment);
            cell.addElement(text);
        }

        return cell;
    }

    private PdfPCell buildCell(Object value, Font font, int alignment, int height, int br) {
        PdfPCell cell = new PdfPCell();

        if (height != -1) {
            cell.setFixedHeight(height);
            cell.setPadding(0);
        }

        if (br != -1) {
            cell.setBorderWidth(br);
        }

        if (value != null) {
            if (value instanceof String) {
                Chunk chunk = new Chunk(value.toString(), font);
                chunk.setTextRise(2);
                Paragraph text = new Paragraph(10);
                text.add(chunk);
                text.setAlignment(alignment);
                cell.addElement(text);
            } else if (value instanceof PdfPTable) {
                cell.addElement((PdfPTable) value);
            }
        }
        return cell;
    }

    //頁尾
    private class MyPageEvent extends PdfPageEventHelper {

        private PdfTemplate total;

        /**
         * Creates the PdfTemplate that will hold the total number of pages.
         */
        public void onOpenDocument(PdfWriter writer, Document document) {
            total = writer.getDirectContent().createTemplate(50, 20);
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            try {
                appendPageFourCorners(writer);
                appendBarCodeForEndPage(writer);//barCode
                appendPageNumForEndPage(writer);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }//頁碼
        }

        //四角加號
        public void appendPageFourCorners(PdfWriter writer) throws Exception {
            PdfPTable tmpTable = new PdfPTable(2);
            tmpTable.setTotalWidth(585);
            tmpTable.setLockedWidth(true);

            PdfPCell cell3 = buildCell("＋", nFont20, Paragraph.ALIGN_LEFT, -1, 0);
            cell3.setFixedHeight(30);
            cell3.setPadding(0);
            cell3.setBorderWidth(0);
            tmpTable.addCell(cell3);

            cell3 = buildCell("＋", nFont20, Paragraph.ALIGN_RIGHT, -1, 0);
            cell3.setFixedHeight(30);
            cell3.setPadding(0);
            cell3.setBorderWidth(0);
            tmpTable.addCell(cell3);

            tmpTable.writeSelectedRows(0, -1, 5, 828, writer.getDirectContent());
            tmpTable.writeSelectedRows(0, -1, 5, 76, writer.getDirectContent());

            if (writer.getPageNumber() == 1) {
                tmpTable = new PdfPTable(1);
                tmpTable.setTotalWidth(585);
                tmpTable.setLockedWidth(true);

                //logo
                Image img = Image.getInstance(source + "//logo.png");
                img.setWidthPercentage(70);
                img.scaleToFit(150, 150);//大小

                PdfPCell cLogo = new PdfPCell();
                cLogo.addElement(img);
                cLogo.setBorder(0);
                tmpTable.addCell(cLogo);

                tmpTable.writeSelectedRows(0, 5, 25, 835, writer.getDirectContent());
            }
            tmpTable.flushContent();
        }

        //產生barCode
        public void appendBarCodeForEndPage(PdfWriter writer) {
            PdfPTable tBarCodeTable = new PdfPTable(6);//表格
            tBarCodeTable.setTotalWidth(545);

            PdfContentByte pdfContentByte = writer.getDirectContent();

            PdfPCell blank = new PdfPCell();//空白列
            blank.setBorderWidth(0);//邊框寬度
            blank.setFixedHeight(10);//儲存格高度

            //barcode - tw date
            Barcode39 code39 = new Barcode39();

            PdfPCell cell = new PdfPCell();//產生列物件
            cell.setFixedHeight(30);//儲存格高度
            cell.setPadding(0);    //間格
            cell.setBorderWidth(0); //邊框的寬度

            tBarCodeTable.addCell(cell);//添加到表中
            tBarCodeTable.addCell(blank);

            //barcode - 客戶ID
//            code39 = new Barcode39();
//            code39.setCode(inputVO.getCUST_ID());
//            code39.setBarHeight(18);//設定高度
//
//            cell = new PdfPCell();
//            cell.setFixedHeight(30);//儲存格高度
//            cell.setPadding(0);    //間格
//            cell.setBorderWidth(0); //邊框的寬度
//            cell.addElement(code39.createImageWithBarcode(pdfContentByte, null, null));//產生一張barCode圖片並添加到列中
//
//            tBarCodeTable.addCell(cell);//添加到表中
            tBarCodeTable.addCell(blank);
            tBarCodeTable.addCell(blank);

            //barcode - page
            code39 = new Barcode39();
            code39.setCode("88-0601-" + (writer.getPageNumber() <= 9 ? "0" + String.valueOf(writer.getPageNumber()) : String.valueOf(writer.getPageNumber())));
            code39.setBarHeight(12);
            code39.setSize(6);
            code39.setBaseline(6);

            Image image = code39.createImageWithBarcode(pdfContentByte, null, null);
            image.setWidthPercentage(90);

            cell = new PdfPCell();
            cell.setColspan(2);
            cell.setFixedHeight(30);
            cell.setPadding(0);
            cell.setBorderWidth(0);
            cell.addElement(image);

            tBarCodeTable.addCell(cell);//添加到表中
            tBarCodeTable.writeSelectedRows(0, -1, 25, 70, writer.getDirectContent());
        }

        //產生頁碼
        public void appendPageNumForEndPage(PdfWriter writer) throws DocumentException {
            PdfPTable tPageNum = null;
            //page num
            tPageNum = new PdfPTable(3);
            tPageNum.setTotalWidth(545);
            int[] data = new int[3];
            data = new int[]{273, 45, 227};
			tPageNum.setWidths(data);

            PdfPCell curPage = new PdfPCell();
            curPage.setBorderWidth(0);

            Chunk chunk = new Chunk(String.format("Page %d ", writer.getPageNumber()), nFont9);
            Paragraph text = new Paragraph();
            text.setAlignment(Phrase.ALIGN_RIGHT);
            text.add(chunk);
            curPage.addElement(text);

            PdfPCell totalPage = new PdfPCell(Image.getInstance(total));
            totalPage.setBorderWidth(0);
            totalPage.setHorizontalAlignment(Image.ALIGN_LEFT);
            
            PdfPCell version = new PdfPCell();
            version.setBorderWidth(0);
            
            Chunk chunkVer = new Chunk("Version：2024.08", nFont9);
//            Chunk chunkVer = new Chunk(" ", nFont9);
            Paragraph textVer = new Paragraph();
            textVer.setAlignment(Phrase.ALIGN_RIGHT);
            textVer.add(chunkVer);
            version.addElement(textVer);
            
            tPageNum.addCell(curPage);
            tPageNum.addCell(totalPage);
            tPageNum.addCell(version);

            tPageNum.writeSelectedRows(0, -1, 25, 50, writer.getDirectContent());
        }

        /**
         * Fills out the total number of pages before the document is closed.
         */
        public void onCloseDocument(PdfWriter writer, Document document) {
            Chunk chunk = new Chunk("of " + (writer.getPageNumber() - 1) + " pages", nFont9);
            Phrase text = new Phrase();
            text.add(chunk);
            ColumnText.showTextAligned(total, Element.ALIGN_LEFT, text, 3, 2, 0);
        }
    }

    public static String addStrFront(String content, char addStr, int lineLength) {
        return addStr(content, addStr, lineLength, true);
    }

    public static String addStrBehind(String content, char addStr, int lineLength) {
        return addStr(content, addStr, lineLength, false);
    }

    public static String addStr(String content, char addStr, int lineLength, boolean isFront) {
        content = content == null ? "" : content.replaceAll("　*", "").trim();
        StringBuffer sbr = new StringBuffer(content);
        int size = getStringLength(content);

        for (int i = 0; i < lineLength - size; i++) {
            if (isFront)
                sbr.insert(0, addStr);
            else
                sbr.append(addStr);
        }
        return sbr.toString();
    }

    public static int getStringLength(String str) {
        int length = 0;
        for (char chr : str.toCharArray()) {
            length += String.valueOf(chr).getBytes().length >= 2 ? 2 : 1;
        }
        return length;
    }
}
