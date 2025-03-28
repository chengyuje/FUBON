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
 * KYC法人空白問卷中文版
 */
@Component("kyc310CorpRptEmpty")
@Scope("prototype")
public class KYC310_CorpRptEmpty extends FubonWmsBizLogic {
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
        doc = new Document(rectPageSize, 20, 10, 30, 80);// 設定邊距(左,右,上,下)
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

		nFont16 = new Font(kaiu, 16, Font.NORMAL);
		bFont16 = new Font(kaiu, 16, Font.BOLD);

		nFont20 = new Font(kaiu, 20, Font.NORMAL);

		qstColor = new Color(55, 169, 203);

        /** 製表相關時間資訊 **/
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        year = String.valueOf(calendar.get(Calendar.YEAR) - 1911);//年
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
		doc.add(buildPartTable());

		PdfPTable tbTextTmp = buildTable(new int[]{100});
		tbTextTmp.addCell(buildBlankCell(5, 0));
		doc.add(tbTextTmp);
		doc.add(buildPart1Table());

		PdfPTable tbTextLine = buildTable(new int[]{100});
		tbTextLine.addCell(buildBlankCell(18, 0));
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
        tbTitleTable.addCell(buildTitle("\n客戶風險屬性評估問卷（適用於法人或機構）", bFont16, Paragraph.ALIGN_CENTER));

        PdfPTable tbText1 = buildTable(new int[]{15, 84});
        tbText1.addCell(buildTitle("問卷測驗日期 ：", nFont10, Paragraph.ALIGN_LEFT));
        tbText1.addCell(buildTitle("□□□□/□□/□□", nFont16, Paragraph.ALIGN_LEFT));

        PdfPTable tbText2 = buildTable(new int[]{15, 84});
        tbText2.addCell(buildTitle("問卷列印日期 ：", nFont10, Paragraph.ALIGN_LEFT));
        tbText2.addCell(buildTitle(year + "/" + (month.matches("\\d{2}") ? "" : "0") + month + "/" + (day.matches("\\d{2}") ? "" : "0") + day, nFont10, Paragraph.ALIGN_LEFT));


        PdfPTable tbText3 = buildTable(new int[]{15, 25, 20, 15, 25});
        tbText3.addCell(buildTitle("公司/機構名稱：", nFont10, Paragraph.ALIGN_LEFT));
        tbText3.addCell(buildCellhardWordTitle(addStrBehind(inputVO.getCust_name(), addStr, lineLength), uFont10, uFont10eudc, Paragraph.ALIGN_LEFT));

        tbText3.addCell(blank);
        tbText3.addCell(buildTitle("負責人姓名：", nFont10, Paragraph.ALIGN_LEFT));
        tbText3.addCell(buildCellhardWordTitle(addStrBehind(inputVO.getRPRS_NAME(), addStr, lineLength), uFont10, uFont10eudc, Paragraph.ALIGN_LEFT));

        PdfPTable tbText4 = buildTable(new int[]{15, 25, 20, 15, 25});
        tbText4.addCell(buildTitle("統 一 編 號　：", nFont10, Paragraph.ALIGN_LEFT));
        tbText4.addCell(buildTitle(addStrBehind(inputVO.getCUST_ID(), addStr, lineLength), uFont10, Paragraph.ALIGN_LEFT));

        tbText4.addCell(blank);
        tbText4.addCell(buildTitle("電　　　話：", nFont10, Paragraph.ALIGN_LEFT));
        tbText4.addCell(buildTitle(addStrBehind(inputVO.getDAY(), addStr, lineLength), uFont10, Paragraph.ALIGN_LEFT));

        PdfPTable tbText5 = buildTable(new int[]{15, 85});
        tbText5.addCell(buildTitle("通 訊 地 址　：", nFont10, Paragraph.ALIGN_LEFT));
        tbText5.addCell(buildTitle(addStrBehind(inputVO.getCUST_ADDR_1(), addStr, 92), uFont10, Paragraph.ALIGN_LEFT));

        PdfPTable tbText6 = buildTable(new int[]{100});
        tbText6.addCell(buildTitle("以下的問卷可幫助評估投資人的風險屬性，為保護投資人權益，請詳實填答下列問題：", bFont10, Paragraph.ALIGN_LEFT));
        tbText6.addCell(blank);
        
        PdfPTable tbText7 = buildTable(new int[]{15, 35, 15, 35});
        tbText7.addCell(buildTitle("電 子 郵 件　：", nFont10, Paragraph.ALIGN_LEFT));
        tbText7.addCell(buildTitle(addStrBehind(inputVO.getCUST_EMAIL_BEFORE(), addStr, lineLength + 11), uFont10, Paragraph.ALIGN_LEFT));
        tbText7.addCell(buildTitle("更新電子郵件：", nFont10, Paragraph.ALIGN_LEFT));
        tbText7.addCell(buildTitle(addStrBehind("", addStr, lineLength + 11), uFont10, Paragraph.ALIGN_LEFT));

        doc.add(tbTitleTable);//客戶風險屬性評估問卷（適用於法人或機構）
        doc.add(blankTb);//客戶風險屬性評估問卷（適用於法人或機構）
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
        tbPart1.addCell(buildSection("本問卷分析結果有效期限一年", nFont10, Paragraph.ALIGN_BOTTOM));
        return tbPart1;
    }

    /** 試題部分 **/
    private PdfPTable buildPart1Table() throws Exception {
        PdfPTable tbPart1 = buildTable(new int[]{12, 5, 83});
        PdfPCell blank = buildBlankCell(18, 0);

        int Q = 1;//題號
        for (Map<String, Object> questionDesc : inputVO.getQuest_list()) {
        	Object ansList = questionDesc.get("ANSWER_LIST");
            List<Map<String, Object>> ansList_change = (List<Map<String, Object>>) ansList;

            PdfPCell cPart1 = buildCell(questionDesc.get("QUESTION_DESC"), nFont9White, Paragraph.ALIGN_LEFT, -1, 0);
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
	                    if (i < 4) {
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
                if (ansList_change.get(0).get("ANSWER_DESC").toString().length() > 15) {
                    String answerSD1 = "";
                    for (Map<String, Object> map : ansList_change) {
                        tbPart1.addCell(blank);
                        tbPart1.addCell(blank);
                        answerSD1 = map.get("ANSWER_DESC").toString();
                        tbPart1.addCell(buildCell(answerSD1, nFont9, Paragraph.ALIGN_LEFT, -1, 0));

                    }
                } else {
                    String answerSD1 = "";
                    String answerSD2 = "";
                    if (!"S".equals(questionDesc.get("QUESTION_TYPE").toString())) {
                        for (int j = 0; j < ansList_change.size(); j++) {
                            if (j < 4) {
                                answerSD1 += ansList_change.get(j).get("ANSWER_DESC").toString() + "　　　";
                            } else {
                                answerSD2 += ansList_change.get(j).get("ANSWER_DESC").toString() + "　　　";
                            }
                        }
                        tbPart1.addCell(buildCell(elsecheckbox, nFont13, Paragraph.ALIGN_RIGHT, -1, 0));
                        tbPart1.addCell(blank);
                        tbPart1.addCell(buildCell(answerSD1, nFont9, Paragraph.ALIGN_LEFT, -1, 0));
                        tbPart1.addCell(blank);
                        tbPart1.addCell(blank);
                        tbPart1.addCell(buildCell(answerSD2, nFont9, Paragraph.ALIGN_LEFT, -1, 0));
                    } else {
                        for (int j = 0; j < ansList_change.size(); j++) {
                            if (j < 3) {
                                answerSD1 += ansList_change.get(j).get("ANSWER_DESC").toString() + "　　　";
                            } else {
                                answerSD2 += ansList_change.get(j).get("ANSWER_DESC").toString() + "　　　";
                            }
                        }
                        tbPart1.addCell(blank);
                        tbPart1.addCell(blank);
                        tbPart1.addCell(buildCell(answerSD1, nFont9, Paragraph.ALIGN_LEFT, -1, 0));
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
    	List<Map<String, Object>> prodExpList = xmlinfo.getXMLInfo("KYC.Q3_PROD_EXP");
    	List<Map<String, Object>> prodTypeList = xmlinfo.getXMLInfo("KYC.Q3_PROD_TYPE");
    	
    	//第3題特殊處理
    	PdfPTable q3table = buildTable(new int[]{11,7,7,8,8,7});
    	for(int i = -1; i < prodTypeList.size(); i++) {
    		for(int j = -1; j < prodExpList.size(); j++) {
    			if(i == -1 && j == -1) {
    				q3table.addCell(setMiddle(buildCellQ3("", nFont9, Paragraph.ALIGN_CENTER, 12, (float)0.1)));
    			} else if(i == -1 && j > -1) {
    				q3table.addCell(setMiddle(buildCellQ3(prodExpList.get(j).get("PARAM_NAME").toString(), nFont9, Paragraph.ALIGN_CENTER, 12, (float)0.1)));
    			} else if(i > -1 && j == -1) {
    				if(i == prodTypeList.size() - 1) {
    					int pLen = StringUtils.length(prodTypeList.get(i).get("PARAM_NAME").toString());
    					int pIdx = StringUtils.indexOf(prodTypeList.get(i).get("PARAM_NAME").toString(), "(");
    					String pName = StringUtils.substring(prodTypeList.get(i).get("PARAM_NAME").toString(), 0, pIdx) + "\n" 
    								 + StringUtils.substring(prodTypeList.get(i).get("PARAM_NAME").toString(), pIdx, pLen);
    					q3table.addCell(setMiddle(buildCellQ3(pName, nFont9, Paragraph.ALIGN_CENTER, 22, (float)0.1)));
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
    		PdfPCell cell = setMiddle(buildCell(ansMap.get("ANSWER_DESC"), q9Font, Paragraph.ALIGN_LEFT, 35, 0));
    		cell.setFixedHeight(20);
    		anstable.addCell(cell);
        }
        
        q9table.addCell(anstable);
        
        return q9table;
    }
    
    /** 提醒區域第一部分 **/
    private PdfPTable buildRemindTable() throws Exception {
        PdfPTable table = buildTable(new int[]{84});
        StringBuffer sb = new StringBuffer();
        sb.append("備註：1. 貴公司／機構所勾選／填寫之問卷內容及分析結果，僅供本行作為日後進行交易時之商品適合度評估依據。本\n")
                .append("  　　　 行不保證／承諾最後「投資之結果」、「投資本金之不虧損」或「投資獲利」，提醒貴公司／機構務必瞭解並\n")
                .append("　　　　 確認。\n")
                .append("　　　2. 客戶風險屬性分析結果有效期限為一年。本行於每筆下單交易前，將以當時貴公司／機構最後有效之分析結果\n")
                .append("　　　　 作為商品適合度評估依據，但具定期重覆投資性質之交易（例如：基金定期定額／不定額投資），不在此限。\n")
                .append("　　　　 惟當貴公司／機構的風險屬性分析結果變更，導致原定期重覆投資性質交易之商品適合度評估未能符合時，依\n")
                .append("　　　　 規定僅可依原約定條件繼續扣款，不得增加扣款金額、扣款次數及停扣後恢復扣款，並請貴公司／機構自行評\n")
                .append("　　　　 估是否依原約定條件持續扣款。\n")
                .append("　　　3. 客戶風險屬性評估之後續定期檢視，可透過本行臨櫃、網路銀行或行動銀行方式辦理。 若核有衍商交易(PSR)\n")
    	        .append("　　　　 額度之客戶，則限以臨櫃方式辦理。\n")
                .append("　　　4. 為維護貴公司／機構的權益，貴公司／機構所填寫之問卷內容，本行可能會以電話方式照會確認。\n");

        table.addCell(buildCell(sb.toString(), nFont10, Paragraph.ALIGN_LEFT, -1, 0));

        return table;
    }

	/** 提醒區域第二部分 **/
    private PdfPTable buildRemindTable2() throws Exception {
        PdfPTable table = buildTable(new int[]{11, 73});
        table.addCell(buildCell("確認與聲明：", bFont10, Paragraph.ALIGN_LEFT, -1, 0));
        table.addCell(buildCell("本公司／機構茲聲明已充份瞭解本問卷的問題及選項內容，且本問卷填答內容已正確及真實地反應本公司／機構現時的狀況及投資目標。"
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
        doc.add(tbText9);
        doc.add(genBankNoteColumn());//銀行註記欄
    }

    /**
     * 製作下方區塊客戶相關需要填寫區塊
     **/
    private PdfPTable genUserSignatureColumn() throws Exception {
        PdfPTable table = buildTable(new int[]{24, 43, 33});
        table.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.setWidthPercentage(95);

        table.addCell(buildCell(" 公司/機構名稱：", nFont10, Paragraph.ALIGN_LEFT, -1, 0));
        table.addCell(buildCell("　　　　　　　　　　　　　　　　　　　", uFont10, Paragraph.ALIGN_LEFT, -1, 0));

        PdfPCell cell = buildCell("", bFont10, Paragraph.ALIGN_LEFT, -1, 2);
        cell.setBorder(1);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setRowspan(7);
        table.addCell(cell);

        int lineLength = 38;
        char addStr = ' ';
        table.addCell(buildTitle(" 負  責  人  ：", nFont10, Paragraph.ALIGN_LEFT));
        table.addCell(buildTitle(addStrFront("(負責人親簽)", addStr, lineLength), uFont10, Paragraph.ALIGN_LEFT));
        table.addCell(buildTitle(" 統 一 編 號　：", nFont10, Paragraph.ALIGN_LEFT));
        table.addCell(buildTitle(addStrBehind("", addStr, lineLength), uFont10, Paragraph.ALIGN_LEFT));
        table.addCell(buildTitle( " 往 來 帳 號　：", nFont10, Paragraph.ALIGN_LEFT));
        table.addCell(buildTitle("□□□□□□□□□□□□", nFont16, Paragraph.ALIGN_LEFT));

        PdfPCell tipCell = buildTitle( new StringBuilder()
                .append("(請負責人親簽並擇一蓋用以下印鑑：以信託帳戶印鑑為優先，若無，再蓋\n其他帳戶印鑑或「債票券客戶資料表」所留存之立約印鑑或簽章樣式)")
                .toString(), nFont10, Paragraph.ALIGN_LEFT);
        tipCell.setColspan(2);
		table.addCell(tipCell);

        table.addCell(buildTitle(" 公司/機構代理人 ：", nFont10, Paragraph.ALIGN_LEFT));
        table.addCell(buildTitle(addStrFront("(被授權人親簽)", addStr, lineLength), uFont10, Paragraph.ALIGN_LEFT));
        table.addCell(buildTitle(" 身分證統一編號  ：", nFont10, Paragraph.ALIGN_LEFT));
        table.addCell(buildTitle(addStrBehind("", addStr, lineLength), uFont10, Paragraph.ALIGN_LEFT));
        return table;
    }

    /**
     * 製作銀行註記欄
     **/
    private PdfPTable genBankNoteColumn() throws Exception {
        //String format = formatter.format(kyc311.getBasic_information().get("EXPIRY_DATE"));

        PdfPTable table2 = buildTable(new int[]{84}, 100);
        PdfPCell blank = buildBlankCell(18, 0);
        PdfPTable table = new PdfPTable(20);
        table.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.setWidthPercentage(100);

        PdfPCell cell = buildCell("銀行註記欄", nFont9, Paragraph.ALIGN_LEFT, -1, 0);
        cell.setColspan(3);
        table.addCell(cell);

        PdfPCell cell1 = buildCell("", nFont9, Paragraph.ALIGN_LEFT, -1, 0);
        cell1.setColspan(20);
        table.addCell(cell1);
        table.addCell(cell1);

        PdfPCell cell2 = null;
        cell2 = buildCell("營業單位：", nFont9, Paragraph.ALIGN_LEFT, -1, 0);
        cell2.setColspan(2);
        table.addCell(cell2);

        PdfPCell cell10 = buildCell("", nFont9, Paragraph.ALIGN_LEFT, -1, 0);
        cell10.setColspan(18);
        table.addCell(cell10);

        table.addCell(cell1);

        PdfPCell cell3 = buildCell("對保人(核對本人親簽無誤)：_________", nFont9, Paragraph.ALIGN_LEFT, -1, 0);
        cell3.setColspan(6);
        table.addCell(cell3);

        PdfPCell cell5 = buildCell("鍵機／解說人員：", nFont9, Paragraph.ALIGN_LEFT, -1, 0);
        cell5.setColspan(3);
        table.addCell(cell5);
        PdfPCell cell9 = buildCell(" _________ ／ _________ ", nFont9, Paragraph.ALIGN_LEFT, -1, 0);
        cell9.setColspan(4);
        table.addCell(cell9);

        PdfPCell cell6 = buildCell("驗印：_________", nFont9, Paragraph.ALIGN_LEFT, -1, 0);
        cell6.setColspan(3);
        table.addCell(cell6);

        PdfPCell cell7 = null;
        cell7 = buildCell("覆核主管：_________ ", nFont9, Paragraph.ALIGN_LEFT, -1, 0);
        cell7.setColspan(5);
        table.addCell(cell7);
        table2.addCell(table);
        return table2;
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

            if (writer.getPageNumber() % 2 == 1) {
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
        	// #2252_調整KYC問卷條碼及版次
        	if (writer.getPageNumber() != 1) return;
        	
            PdfPTable tBarCodeTable = new PdfPTable(5);//表格
            tBarCodeTable.setTotalWidth(545);
            
            PdfContentByte pdfContentByte = writer.getDirectContent();

            PdfPCell blank = new PdfPCell();//空白列
            blank.setBorderWidth(0);//邊框寬度
            blank.setFixedHeight(10);//儲存格高度
            
            Barcode39 code39;
            PdfPCell cell;
            //Barcode 99
			code39 = new Barcode39();
			code39.setCode("88-0601-99");
            code39.setBarHeight(18);
            code39.setSize(8);
			cell = getPdfCell(code39, pdfContentByte);
			tBarCodeTable.addCell(cell);
			tBarCodeTable.addCell(blank);
			
			//Barcode CustID
			code39 = new Barcode39();
			code39.setCode(inputVO.getCUST_ID());
            code39.setBarHeight(15);
            code39.setSize(7);
			cell = getPdfCell(code39, pdfContentByte);
			tBarCodeTable.addCell(cell);
			tBarCodeTable.addCell(blank);
			
			//Barcode Date
			code39 = new Barcode39();
			code39.setCode(year + "0" + month + day);
			code39.setBarHeight(13.6f);
            code39.setSize(6.4f);
            cell = getPdfCell(code39, pdfContentByte);
            tBarCodeTable.addCell(cell);
			
            tBarCodeTable.writeSelectedRows(0, -1, 25, 80, writer.getDirectContent());
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

            Chunk chunk = new Chunk(String.format("第 %d 頁 /", writer.getPageNumber()), nFont10);
            Paragraph text = new Paragraph();
            text.setAlignment(Phrase.ALIGN_RIGHT);
            text.add(chunk);
            curPage.addElement(text);

            PdfPCell totalPage = new PdfPCell(Image.getInstance(total));
            totalPage.setBorderWidth(0);
            totalPage.setHorizontalAlignment(Image.ALIGN_LEFT);
            
            PdfPCell version = new PdfPCell();
            version.setBorderWidth(0);
            
            Chunk chunkVer = new Chunk("2024年12月版", nFont10);
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
            Chunk chunk = new Chunk("共 " + (writer.getPageNumber() - 1) + " 頁", nFont10);
            Phrase text = new Phrase();
            text.add(chunk);
            ColumnText.showTextAligned(total, Element.ALIGN_LEFT, text, 3, 2, 0);
        }
        
        public PdfPCell getPdfCell(Barcode39 code39, PdfContentByte cb) {
        	PdfPCell cell = new PdfPCell();
            cell.setFixedHeight(30);
            cell.setPadding(0);
            cell.setBorderWidth(0);
            cell.addElement(code39.createImageWithBarcode(cb, null, null));
            return cell;
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
