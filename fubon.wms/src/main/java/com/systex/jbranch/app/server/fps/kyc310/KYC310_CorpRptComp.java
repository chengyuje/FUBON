package com.systex.jbranch.app.server.fps.kyc310;

import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.systex.jbranch.app.server.fps.kyc311.KYC311InputVO;
import com.systex.jbranch.common.xmlInfo.XMLInfo;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.XmlInfo;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringEscapeUtils;
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
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;

import static com.systex.jbranch.fubon.bth.job.context.AccessContext.tempReportPath;


/**
 * KYC法人差異表問卷
 */
@Component("kyc310CorpRptComp")
@Scope("prototype")
public class KYC310_CorpRptComp extends FubonWmsBizLogic {
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
    private String twYear;
    private String month;
    private String day;
    private File source;
    private Font nFont;
    private Font bFont;
    private Font bFont10;
    private Font font10;
	private Font uFont10;
    private Font bFont16;
    private Font bFontWhite;
    private Font nFontBlue;
    private Font nFont_a;
    private BaseFont kaiuFont;
    private BaseFont educFont;//Ray	201305070205-00	pdf檔案可顯示難字
    private Color partHeaderColor;

    private String footerType;
    
    /**
     * 法人KYC差異表
     */
    public String getPDFPath() throws Exception {
        initialize();
        createDocument();
        createPdfWriter();

        doc.open();
        //受理單位留存聯
        footerType = "1";
        genPDF();
        //新頁
        doc.newPage();
        //客戶收執聯
        footerType = "2";
        genPDF();
        
        doc.close();
        
        return Paths.get("temp", "reports", reportName).toString();
    }

    /**
     * 初始字體物件，報表路徑
     **/
    private void initialize() throws IOException, DocumentException {
        /** 資源路徑 **/
        source = new File(DataManager.getRealPath(), "doc//FPS");

        /** 載入字型 **/
        kaiuFont = BaseFont.createFont(source + "//kaiu.ttf", "Identity-H", BaseFont.NOT_EMBEDDED);
        educFont = BaseFont.createFont(source + "/EUDC.TTF", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);

        nFont = new Font(kaiuFont, 9, Font.NORMAL);
        bFont = new Font(kaiuFont, 9, Font.BOLD);
        bFont10 = new Font(kaiuFont, 10, Font.BOLD);
        font10 = new Font(kaiuFont, 10, Font.NORMAL);
		uFont10 = new Font(kaiuFont, 10, Font.UNDERLINE);
        bFont16 = new Font(kaiuFont, 16, Font.BOLD);
        bFont16.setStyle(Font.UNDERLINE);
        nFont_a = new Font(educFont, 10, Font.NORMAL);

        /** 題目的字體 **/
        bFontWhite = new Font(kaiuFont, 12, Font.BOLD);
        bFontWhite.setColor(Color.WHITE);

        /** 答案的字體 **/
        nFontBlue = new Font(kaiuFont, 9, Font.NORMAL);
        nFontBlue.setColor(Color.blue);

        /** part header color **/
        partHeaderColor = new Color(55, 169, 203);

        /** 報表路徑 **/
        reportName = UUID.randomUUID().toString();
        reportPath = tempReportPath + reportName;

        /** 製表相關時間資訊 **/
        String[] time = new SimpleDateFormat("yyyy MM dd").format(new Date()).split(" ");
        twYear = Integer.valueOf(time[0]) - 1911 + "";
        month = time[1];
        day = time[2];
    }

    /**
     * 建立 Document 容器
     **/
    private void createDocument() {
        Document.compress = false;
        Rectangle rectPageSize = new Rectangle(PageSize.A4);// 設定版面
        doc = new Document(rectPageSize, 10, 10, 35, 80);// 設定邊距(左,右,上,下)
    }

    /**
     * 建立 PdfWriter
     **/
    private void createPdfWriter() throws FileNotFoundException, DocumentException {
        pdfWriter = PdfWriter.getInstance(doc, new FileOutputStream(reportPath));
        pdfWriter.setPageEvent(new MyPageEvent());
    }

    /**
     * 產出PDF
     *
     * @throws
     * @throws
     */
    private void genPDF() throws Exception {
        doc.add(buildHeader());
        doc.add(partOne());
        doc.add(buildPart2Preface());
        doc.add(buildPart2Table());
        doc.add(buildRemindTable());
        doc.add(buildRemindTable2());
        doc.add(buildSignTable());
        doc.add(buildBlankAreaTable());
        doc.add(buildNoteTable());
    }

    /**
     * 建立 Header 區塊
     **/
    private PdfPTable buildHeader() throws Exception {
    	PdfPTable tbHeader = buildTable(new int[] {28, 36, 20});
        PdfPCell blank = buildBlankCell(18, 0);
		
        Image img = Image.getInstance(source + "//logo2.png");
        img.setWidthPercentage(85);
        PdfPCell logo = new PdfPCell();
        logo.addElement(img);
        logo.setBorder(0);
        
        tbHeader.addCell(logo);
        tbHeader.addCell(buildTitle("客戶風險屬性評估問卷差異說明", bFont16, Paragraph.ALIGN_CENTER));
        tbHeader.addCell(blank);
		
        tbHeader.addCell(blank); //空白TABLE
        tbHeader.addCell(blank);
        tbHeader.addCell(blank);
        
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        String evaluationFormat = formatter.format(new Date());
      
        String getMonth = "";
		String getDate = "";
		Date today = new Date();
		java.sql.Timestamp ts = new Timestamp(today.getTime());
		getMonth = (ts.getMonth()+1)+"";
		getDate = ts.getDate()+"";
		if (getMonth.length() < 2){
			getMonth = "0"+getMonth;
		}
		if (getDate.length() < 2){
			getDate = "0"+getDate;
		}
        twYear = String.valueOf((ts.getYear()-11));
        month = getMonth;
        day = getDate;
		tbHeader.addCell(buildCell("評估日期：民國 "+ (ts.getYear()-11) + " 年 " + getMonth + " 月 " + getDate+" 日", nFont, Paragraph.ALIGN_LEFT, -1, 0));
		tbHeader.addCell(blank);
		tbHeader.addCell(blank);
		
		ts = (java.sql.Timestamp) inputVO.getEXPIRY_DATE();
		getMonth = (ts.getMonth()+1)+"";
		getDate = ts.getDate()+"";
		if (getMonth.length() < 2){
			getMonth = "0"+getMonth;
		}
		if (getDate.length() < 2){
			getDate = "0"+getDate;
		}
		
		tbHeader.addCell(buildCell("有效日期：民國 "+ (ts.getYear()-11) + " 年 " + getMonth + " 月 " + getDate+" 日", nFont, Paragraph.ALIGN_LEFT, -1, 0));
		tbHeader.addCell(buildCell("風險屬性評估問卷編號：", nFont, Paragraph.ALIGN_RIGHT, -1, 0));
		tbHeader.addCell(buildCell(inputVO.getKYC_SEQ(), nFont, Paragraph.ALIGN_LEFT, -1, 0));
		
		return tbHeader;
    }

    /**
     * 取得第一部分標題
     **/
    private PdfPTable partOne() throws Exception {
    	PdfPTable tbPart1 = buildTable(new int[] {84});
        
        PdfPTable tbPart1Sub = buildTable(new int[]{20,30,20,30});
        tbPart1Sub.addCell(buildCellWithBorder("公司/機構名稱", bFont, Paragraph.ALIGN_CENTER));
        tbPart1Sub.addCell(buildCellhardWord(inputVO.getCust_name() , nFont , nFont_a , Paragraph.ALIGN_CENTER));
        tbPart1Sub.addCell(buildCellWithBorder("統一編號", bFont, Paragraph.ALIGN_CENTER));
        tbPart1Sub.addCell(buildCellWithBorder(inputVO.getCUST_ID(), nFont , Paragraph.ALIGN_CENTER));
        PdfPCell cPart1  = new PdfPCell(tbPart1Sub);
		tbPart1.addCell(cPart1);

		return tbPart1;
    }

    /**
     * 設定垂直置中
     **/
    private PdfPCell setMiddle(PdfPCell cell) {
        cell.setVerticalAlignment(Paragraph.ALIGN_MIDDLE);
        return cell;
    }   

    /**
     * 第二部分前導文
     **/
    private PdfPTable buildPart2Preface() throws Exception {
        PdfPTable table = buildTable(new int[]{84});
        
        table.addCell(buildCell("\n確認與聲明：本公司／機構茲聲明已充份瞭解本問卷的問題及選項內容，且本問卷填答內容已正確及真實地反應本公司／機構現時狀況及投資", bFont, Paragraph.ALIGN_LEFT, 20, 0));
        table.addCell(buildCell("目標。", bFont, Paragraph.ALIGN_LEFT, 20, 0));
        
        return table;
    }
    
    /**
     * 取得第二部分標題
     **/
    private PdfPCell getPart2Header(String title) {
        PdfPCell header = buildSection(title, bFontWhite, Paragraph.ALIGN_CENTER);
        header.setBackgroundColor(partHeaderColor);
        return header;
    }
    
    /**
     * 第二部分：題目與答案選項
     **/
    private PdfPTable buildPart2Table() throws Exception {
        PdfPTable table = buildTable(new int[]{16, 8, 20, 20, 20});
        PdfPCell pdfcell = getPart2Header("題目");
        pdfcell.setColspan(2);
        table.addCell(pdfcell);
        table.addCell(getPart2Header("前次填答"));
        table.addCell(getPart2Header("本次填答"));
        table.addCell(getPart2Header("說明"));
       
        for (Map map : inputVO.getCOMP_QUES()) {
        	if((Double) map.get("QST_NO") == 3) {
        		//第三題特殊處理
        		//題目
        		pdfcell = setMiddle(buildCellWithBorder(getQst(map), nFont, Paragraph.ALIGN_LEFT));
        		pdfcell.setRowspan(inputVO.getQ3_PROD_TYPEList().size());
        		table.addCell(pdfcell);
        		for(Map prodType : (List<Map<String, Object>>)map.get("Q3_PROD_TYPEList_COMP")) { //各商品類別分別列出
        			//商品類別
        			table.addCell(setMiddle(buildCellWithBorder((String)prodType.get("LABEL_CH"), nFont, Paragraph.ALIGN_LEFT)));
	        		//前次填答
	        		table.addCell(getLastAnsQ3(map, (String)prodType.get("DATA")));
	        		//本次填答
		        	table.addCell(getAnsQ3(map, (String)prodType.get("DATA")));
		        	//說明
		        	table.addCell(getAnsCompQ3(map, prodType));
        		}
        	} else { //非第3題
	            //題目
	        	pdfcell = setMiddle(buildCellWithBorder(getQst(map), nFont, Paragraph.ALIGN_LEFT));
	        	pdfcell.setColspan(2);
	        	table.addCell(pdfcell);
	            //前次填答
	        	table.addCell(getLastAns(map));
	        	//本次填答
	        	table.addCell(getAns(map));
	        	//說明
	        	table.addCell(getAnsComp(map));
	        }
        }
        return table;
    }

    /**
     * 取得題目
     **/
    private String getQst(Map map) {
        return StringUtils.defaultString(String.format("%.0f",(Double)map.get("QST_NO")) + "、" + (String) map.get("QUESTION_DESC"));
    }

    /**
     * 取得前次填答答案選項 (非第三題)
     * @throws Exception 
     **/
    @SuppressWarnings("unchecked")
	private PdfPTable getLastAns(Map map) throws Exception {
    	PdfPTable table = buildTable(new int[]{20});
        List<Map> ansList = (List) map.get("ANSWER_LIST");
        for (Map ansMap : ansList) {
        	if(ansMap.get("lastAnsSelect") != null && (boolean)ansMap.get("lastAnsSelect")) {
        		table.addCell(setMiddle(buildCell((String) ansMap.get("ANSWER_DESC"), nFont, Paragraph.ALIGN_LEFT, -1, 0)));
        	}
        }
        
        return table;
    }
    
    /***
     * 取得本次填答答案選項 (非第三題)
     * @param map
     * @param isQ11
     * @return
     * @throws Exception 
     */
    @SuppressWarnings("unchecked")
    private PdfPTable getAns(Map map) throws Exception {
    	PdfPTable table = buildTable(new int[]{20});
        List<Map> ansList = (List) map.get("ANSWER_LIST");
        for (Map ansMap : ansList) {
        	if(ansMap.get("select") != null && (boolean)ansMap.get("select")) {
        		table.addCell(setMiddle(buildCell((String) ansMap.get("ANSWER_DESC"), nFont, Paragraph.ALIGN_LEFT, -1, 0)));
        	}
        }
        
        return table;
    }
    
    /***
     * 取得差異表問卷答案選項 (非第三題)
     * @param map
     * @return
     * @throws Exception 
     */
    @SuppressWarnings("unchecked")
    private PdfPTable getAnsComp(Map map) throws Exception {
    	PdfPTable table = buildTable(new int[]{20});
        List<Map> ansList = (List) map.get("ANSWER_LIST_COMP");
        for (Map ansMap : ansList) {
        	if(ansMap.get("select") != null && (boolean)ansMap.get("select")) {
        		table.addCell(setMiddle(buildCell((String) ansMap.get("ANSWER_DESC"), nFont, Paragraph.ALIGN_LEFT, -1, 0)));
        	}
        }
        
        return table;
    }
    
    /**
     * 取得第三題前次填答答案選項
     * @throws Exception 
     **/
    @SuppressWarnings("unchecked")
	private PdfPTable getLastAnsQ3(Map map, String prodType) throws Exception {
    	PdfPTable table = buildTable(new int[]{20});
        List<Map> ansList = (List) map.get("ANSWER_LIST");
        for (Map ansMap : ansList) {
        	for(Map prodExp : inputVO.getQ3_PROD_EXPList()) {
	        	if(ansMap.get("lastAnsSelect") != null && (boolean)ansMap.get("lastAnsSelect")
	        			&& StringUtils.equals(ansMap.get("ANSWER_SEQ").toString().substring(0, 1), prodType)
	        			&& StringUtils.equals(ansMap.get("ANSWER_SEQ").toString().substring(1, 2), (String)prodExp.get("DATA"))) {
	        		table.addCell(setMiddle(buildCell((String) prodExp.get("LABEL_CH"), nFont, Paragraph.ALIGN_LEFT, -1, 0)));
	        	}
        	}
        }
        
        return table;
    }
    
    /***
     * 取得第三題本次填答答案選項
     * @param map
     * @param isQ11
     * @return
     * @throws Exception 
     */
    @SuppressWarnings("unchecked")
    private PdfPTable getAnsQ3(Map map, String prodType) throws Exception {
    	PdfPTable table = buildTable(new int[]{20});
        List<Map> ansList = (List) map.get("ANSWER_LIST");
        for (Map ansMap : ansList) {
        	for(Map prodExp : inputVO.getQ3_PROD_EXPList()) {
	        	if(ansMap.get("select") != null && (boolean)ansMap.get("select")
	        			&& StringUtils.equals(ansMap.get("ANSWER_SEQ").toString().substring(0, 1), prodType)
	        			&& StringUtils.equals(ansMap.get("ANSWER_SEQ").toString().substring(1, 2), (String)prodExp.get("DATA"))) {
	        		table.addCell(setMiddle(buildCell((String) prodExp.get("LABEL_CH"), nFont, Paragraph.ALIGN_LEFT, -1, 0)));
	        	}
        	}
        }
        
        return table;
    }
    
    /***
    * 取得第三題差異表問卷答案選項
    * @param map
    * @return
    * @throws Exception 
    */
   @SuppressWarnings("unchecked")
   private PdfPTable getAnsCompQ3(Map map, Map pTypeMap) throws Exception {
	   PdfPTable table = buildTable(new int[]{20});
       List<Map> ansList = (List) pTypeMap.get("ANSWER_LIST_COMP");
       for (Map ansMap : ansList) {
    	   if(ansMap.get("select") != null && (boolean)ansMap.get("select")) {
    		   table.addCell(setMiddle(buildCell((String) ansMap.get("ANSWER_DESC"), nFont, Paragraph.ALIGN_LEFT, -1, 0)));
    	   }
       }
       
       return table;
   }

    /**
     * 約定事項
     **/
    private PdfPTable buildRemindTable() throws Exception {
        PdfPTable table = buildTable(new int[]{84});
        table.addCell(buildCell("", bFont, Paragraph.ALIGN_LEFT, 20, 0));
        
        StringBuffer sb = new StringBuffer();
        sb.append("\n聲明及同意事項：\n")
                .append("1.貴公司／機構所勾選／填寫之問卷內容及分析結果，僅供本行作為日後進行交易時之商品適合度評估依據。本行不保證／承諾最後「投\n")
                .append("  資之結果」、「投資本金之不虧損」或「投資獲利」，提醒貴公司／機構務必瞭解並確認。\n")
                .append("2.客戶風險屬性分析結果有效期限為一年。本行於每筆下單交易前，將以當時貴公司／機構最後有效之分析結果作為商品適合度評估依據\n")
                .append("  ，但具定期重覆投資性質之交易（例如：基金定期定額／不定額投資），不在此限。惟當貴公司／機構的風險屬性分析結果變更，導致\n")
                .append("  原定期重覆投資性質交易之商品適合度評估未能符合時，依規定僅可依原約定條件繼續扣款，不得增加扣款金額、扣款次數及停扣後恢\n")
                .append("  復扣款，並請貴公司／機構自行評估是否依原約定條件持續扣款。\n")
                .append("3.為維護貴公司／機構的權益，貴公司／機構所填寫之問卷內容，本行可能會以電話方式照會確認。");

        table.addCell(buildCell(sb.toString(), bFont, Paragraph.ALIGN_LEFT, -1, 0));
        return table;
    }

    private PdfPTable buildRemindTable2() throws Exception {
    	PdfPTable table = buildTable(new int[] {84});
    	StringBuffer sb = new StringBuffer();
    	sb.append("\n\n")
    		.append("茲確認下列事項：\n\n")
    		.append("1.以上差異說明經本公司／機構確認無誤，並已審閱完畢。\n\n")
    		.append("2.經本公司／機構確認已收執本差異說明結果副本乙份並確認與【本行正本內容無誤】。\n\n");
    	table.addCell(buildCell(sb.toString(), bFont, Paragraph.ALIGN_LEFT, -1, 0));
	    return table;
	}
    
    /**
     * 客戶簽署區域
     **/
    private PdfPTable buildSignTable() throws Exception {
    	PdfPTable table = buildTable(new int[]{24, 43, 33});
        table.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.setWidthPercentage(95);

        table.addCell(buildCell(" 公司/機構名稱：", font10, Paragraph.ALIGN_LEFT, -1, 0));
        table.addCell(buildCell("　　　　　　　　　　　　　　　　　　　", uFont10, Paragraph.ALIGN_LEFT, -1, 0));

        PdfPCell cell = buildCell("", bFont10, Paragraph.ALIGN_LEFT, -1, 2);
        cell.setBorder(0);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setRowspan(7);
        table.addCell(cell);

        int lineLength = 38;
        char addStr = ' ';
        table.addCell(buildTitle(" 負  責  人  ：", font10, Paragraph.ALIGN_LEFT));
        table.addCell(buildTitle(addStrFront("(負責人親簽)", addStr, lineLength), uFont10, Paragraph.ALIGN_LEFT));
        table.addCell(buildTitle(" 統 一 編 號　：", font10, Paragraph.ALIGN_LEFT));
        table.addCell(buildTitle("______________________________________", font10, Paragraph.ALIGN_LEFT));
        table.addCell(buildTitle( " 往 來 帳 號　：", font10, Paragraph.ALIGN_LEFT));
        table.addCell(buildTitle("□□□□□□□□□□□□", font10, Paragraph.ALIGN_LEFT));

        PdfPCell tipCell = buildTitle( new StringBuilder()
                .append(" (請負責人親簽並擇一蓋用以下印鑑：以信託帳戶印鑑為優先，若無，再蓋其他帳戶印鑑或「債票券客戶資料表」所留存之\n  立約印鑑或簽章樣式)")
                .toString(), font10, Paragraph.ALIGN_LEFT);
        tipCell.setColspan(3);
		table.addCell(tipCell);

        table.addCell(buildTitle(" 公司/機構代理人 ：", font10, Paragraph.ALIGN_LEFT));
        table.addCell(buildTitle(addStrFront("(被授權人親簽)", addStr, lineLength), uFont10, Paragraph.ALIGN_LEFT));
        table.addCell(buildTitle(" 身分證統一編號  ：", font10, Paragraph.ALIGN_LEFT));
        table.addCell(buildTitle("______________________________________", font10, Paragraph.ALIGN_LEFT));
        
        PdfPCell dateCell = buildTitle( new StringBuilder()
        		.append(" 日期：民國       年       月       日")
        		.toString(), font10, Paragraph.ALIGN_LEFT);
        dateCell.setColspan(3);
        table.addCell(dateCell);

        return table;
    }

    /**
     * 客戶簽署區
     **/
    private PdfPCell signParagraph(Chunk... data) {
        Paragraph text = new Paragraph();

        for (Chunk each : data)
            text.add(each);
        text.setAlignment(Paragraph.ALIGN_LEFT);
        PdfPCell cell = buildBlankCell(18, 0);
        cell.addElement(text);
        return cell;
    }
    
    private PdfPTable buildBlankAreaTable() throws Exception {
        PdfPTable table = buildTable(new int[]{84});
        table.addCell(buildCell("", bFont, Paragraph.ALIGN_LEFT, 20, 0));
        
        return table;
    }

    /** 註記區 **/
    private PdfPTable buildNoteTable() throws Exception {
        PdfPTable table = buildTable(new int[]{84}, 100);
        PdfPTable inner = buildTable(new int[]{30, 20, 17, 17});

        inner.addCell(buildCell("銀行註記欄：", nFont, Paragraph.ALIGN_LEFT, -1, 0));
        inner.addCell(buildBlankCell(18, 0));
        inner.addCell(buildBlankCell(18, 0));
        inner.addCell(buildBlankCell(18, 0));
        inner.addCell(buildCell("營業單位：", nFont, Paragraph.ALIGN_LEFT, -1, 0));
        inner.addCell(buildBlankCell(18, 0));
        inner.addCell(buildBlankCell(18, 0));
        inner.addCell(buildBlankCell(18, 0));

        inner.addCell(buildCell("對保人(核對本人親簽無誤)：_________", nFont, Paragraph.ALIGN_LEFT, -1, 0));
        inner.addCell(buildCell("鍵機/解說人員：_________", nFont, Paragraph.ALIGN_LEFT, -1, 0));
        inner.addCell(buildCell("驗印：_________", nFont, Paragraph.ALIGN_LEFT, -1, 0));
        inner.addCell(buildCell("覆核主管：_________", nFont, Paragraph.ALIGN_LEFT, -1, 0));

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
    
    private PdfPCell buildCellhardWord(String value, Font font1, Font font2, int aligment) {
        return buildCellhardWord(value, font1, font2, aligment, -1);
    }

    private PdfPCell buildCellhardWord(String value, Font font1, Font font2, int aligment, int height) {
        return buildCellhardWord(value, font1, font2, aligment, height, 0);
    }

    private PdfPCell buildCellWithBorder(String value, Font font, int aligment) {
        return buildCellWithBorder(value, font, aligment, -1, -1);
    }

    private PdfPCell buildCellWithBorder(String value, Font font, int aligment, int height, int br) {
        return buildCell(value, font, aligment, height, br);
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
            Chunk chunk = new Chunk(value, font);
            Paragraph text = new Paragraph();
            text.add(chunk);
            text.setAlignment(alignment);
            cell.addElement(text);
        }

        return cell;
    }

    private PdfPCell buildSection(Object value, Font font, int alignment) {
        PdfPCell cell = new PdfPCell();
        cell.setFixedHeight(18);
        cell.setPadding(0);

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

    private PdfPCell buildCellhardWord(Object value, Font font1, Font font2, int aligment, int height, int br) {
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

                Paragraph text = new Paragraph(10);
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

    // TODO 研究一下為什麼這邊會使用到
    private class MyPageEvent extends PdfPageEventHelper {

        private PdfTemplate total;

        /**
         * Creates the PdfTemplate that will hold the total number of pages.
         */
        public void onOpenDocument(PdfWriter writer, Document document) {
            total = writer.getDirectContent().createTemplate(50, 20);
        }

        //頁尾
        @Override
		public void onEndPage(PdfWriter writer, Document document) {
			PdfPTable tBarCode;
			PdfPTable tPageNum;
			int page = writer.getPageNumber();
			
			try {
				// barcode - page
				tBarCode = new PdfPTable(5);
				tBarCode.setTotalWidth(545);
				PdfContentByte cb = writer.getDirectContent();
				
				Barcode39 code39 = new Barcode39();
				code39.setCode("88-0402-0" + page);
				code39.setBarHeight(18);
				
				PdfPCell cell = new PdfPCell();
	    		cell.setFixedHeight(30);
	        	cell.setPadding(0);	
	    		cell.setBorderWidth(0);
	    		cell.addElement(code39.createImageWithBarcode(cb, null, null));

	    		PdfPCell blank = new PdfPCell();
	    		blank.setBorderWidth(0);
	    		blank.setFixedHeight(10);
	    		
				tBarCode.addCell(cell);
				tBarCode.addCell(blank);
				
				// barcode - cust id
				code39 = new Barcode39();
				code39.setCode(inputVO.getCUST_ID());
				code39.setBarHeight(18);
				
				cell = new PdfPCell();
	    		cell.setFixedHeight(30);
	        	cell.setPadding(0);	
	    		cell.setBorderWidth(0);
	    		cell.addElement(code39.createImageWithBarcode(cb, null, null));
	    		
	    		tBarCode.addCell(cell);
				tBarCode.addCell(blank);
				
	    		// barcode - tw date
				code39 = new Barcode39();
				code39.setCode(twYear + month + day);
				code39.setSize(6);
				code39.setBarHeight(14);
				
				cell = new PdfPCell();
	    		cell.setFixedHeight(30);
	        	cell.setPadding(0);	
	    		cell.setBorderWidth(0);
	    		cell.addElement(code39.createImageWithBarcode(cb, null, null));
				
				tBarCode.addCell(cell);
				
				// page num
				tPageNum = new PdfPTable(4);
				tPageNum.setTotalWidth(545);
				int[] data = new int[4];
				data = new int[]{250, 45, 125, 125};
				tPageNum.setWidths(data);
				
				PdfPCell curPage = new PdfPCell();
				curPage.setBorderWidth(0);
				
				Chunk chunk = new Chunk(String.format("第 %d 頁/", writer.getPageNumber()), nFont);
				Paragraph text = new Paragraph();
				text.setAlignment(Phrase.ALIGN_RIGHT);
                text.add(chunk);
                curPage.addElement(text);	
				
				PdfPCell totalPage = new PdfPCell(Image.getInstance(total));
				totalPage.setBorderWidth(0);
				totalPage.setHorizontalAlignment(Image.ALIGN_LEFT);
				
				PdfPCell typePage = new PdfPCell();
				typePage.setBorderWidth(0);
				
				String s = "(受理單位留存聯)";
				if(StringUtils.equals("2", footerType)) {
					s = "(客戶收執聯)";
				} else {
					//非客戶收執聯才需要barcode
					tBarCode.writeSelectedRows(0, -1, 25, 80, writer.getDirectContent());
				} 
				Chunk typeChunk = new Chunk(s, nFont);
				Paragraph typeText = new Paragraph();
				typeText.setAlignment(Phrase.ALIGN_LEFT);
				typeText.add(typeChunk);
				typePage.addElement(typeText);	
				
                PdfPCell version = new PdfPCell();
                version.setBorderWidth(0);
                
                Chunk chunkVer = new Chunk("2024年6月版", nFont);
                Paragraph textVer = new Paragraph();
                textVer.setAlignment(Phrase.ALIGN_RIGHT);
                textVer.add(chunkVer);
                version.addElement(textVer);
				
				tPageNum.addCell(curPage);
				tPageNum.addCell(totalPage);
				tPageNum.addCell(typePage);
				tPageNum.addCell(version);
				
				// write
				tPageNum.writeSelectedRows(0, -1, 25, 50, writer.getDirectContent());
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		 /**
         * Fills out the total number of pages before the document is closed.
         */
		@Override
        public void onCloseDocument(PdfWriter writer, Document document) {
        		Chunk chunk = new Chunk("共 "+ (writer.getPageNumber() - 1) + " 頁", nFont);
            	Phrase text = new Phrase();
                text.add(chunk);
    			ColumnText.showTextAligned(total, Element.ALIGN_LEFT, text, 3, 2, 0);

        }
    }
    
}
