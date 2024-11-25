package com.systex.jbranch.app.server.fps.kyc610;



import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.Barcode39;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;
import com.systex.jbranch.app.common.fps.table.TBORG_MEMBERVO;
import com.systex.jbranch.app.server.fps.kyc.chk.KYCCheckIdentityWeights;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;


//金融(含債卷)專案知識問卷
public class KYC610_Qst extends FubonWmsBizLogic{
	private String	custId	=	"";
	private String	custName	=	"";
	private String	deptNbr	=	"";
	private String	empId	=	"";
	private String	qstSeq = "";
	private String evaSeq;
	private boolean isLegalPerson;
	private File nowpath = new File (DataManager.getRealPath() , "doc//FPS");
	private File tempPath = new File (DataManager.getRealPath() ,"temp//reports");
	private Font nFont;
	private Font nFontB;
	private Font nFontPink;
	private Font bFontWhite;
	private Font bFontWhiteBo;
	private Font nFonts;
	private Font uFonts;
    private Font bFont;
    private Font bFont16;
    private Font bFontBlue;
    private Font nFontBlue;
    private Font uFont;
	private BaseFont bfChinese;
	private Boolean run= true;
	private DataAccessManager dam = null;
	private List<Map<String, Object>> list;

    /**這個PdfTemplate實例用於保存總頁數 */
    private PdfTemplate tpl;

	@Autowired@Qualifier("KYCCheckIdentityWeights")
	private KYCCheckIdentityWeights KYCCheck;
	
    public KYC610_Qst() throws Exception{
    	bfChinese = BaseFont.createFont(nowpath + "//kaiu.ttf",	"Identity-H", BaseFont.NOT_EMBEDDED);
    	BaseFont.createFont(nowpath +"//kaiu.ttf", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
    	bFont16 = new Font(bfChinese, 16, Font.NORMAL);
    	nFont = new Font(bfChinese, 10, Font.NORMAL);
    	nFontB = new Font (bfChinese, 13, Font.NORMAL);
    	nFonts = new Font(bfChinese, 9, Font.NORMAL);
    	new Font(bfChinese, 10, Font.UNDERLINE);
    	uFonts = new Font(bfChinese, 8, Font.UNDERLINE);
    	bFont = new Font(bfChinese, 10, Font.BOLD);
		nFontPink = new Font(bfChinese, 9, Font.NORMAL);
		nFontPink.setColor(new Color(233, 56, 151));
		bFontWhite = new Font(bfChinese, 9, Font.NORMAL);
		bFontWhite.setColor(Color.WHITE);
		bFontWhiteBo = new Font(bfChinese, 9, Font.BOLD);
		bFontWhiteBo.setColor(Color.WHITE);
		bFontBlue = new Font(bfChinese, 9, Font.NORMAL);
		bFontBlue.setColor(Color.BLUE);
		nFontBlue = new Font(bfChinese, 9, Font.NORMAL);
        nFontBlue.setColor(new Color(233, 56, 151));
        uFont = new Font(bfChinese, 10, Font.UNDERLINE);
        
        /**
         *         金融(含債卷)專案知識評估編號：
         *		 【A+分行別+員編+日期(YYYYMMDD)+時間(HHMM)】
         *			結構型專案知識評估編號
         *			B+分行別+員編+日期(YYYYMMDD)+時間(HHMM)】
         */
        SimpleDateFormat simple = new SimpleDateFormat("YYYYMMDDHHmm");
        evaSeq = qstSeq+deptNbr+empId+simple.format(new Date());
    }

   
    /**
     * 空白KYC
     */
    public void genBlankPDF(KYC610InputVO kyc610 , String tempPath) {
		Document.compress = false;
		Rectangle rectPageSize = new Rectangle(PageSize.A4);// 設定版面
		Document document = new Document(rectPageSize, 20, 10, 30, 60);// 設定邊距(左,右,上,下)
    	try {
    		PdfWriter writer = PdfWriter.getInstance(document,new FileOutputStream(tempPath));
//    		writer.setPageEvent(new MyPageEvent());
    		document.open();
    		
    		genPDF(kyc610,document , kyc610.getQUEST_TYPE() , false);//題目
    		document.newPage();
    		genPDF(kyc610,document , kyc610.getQUEST_TYPE() , true);//答案
    		document.newPage();
    		run = false;
    		//genPDF(kyc610,document);
    
    		document.close();
    		
		} catch (Exception e) {
			logger.error("genBlankPDF error:" + e.getMessage() , e);
			e.printStackTrace();
		} finally {
    		document.close();
		}
    }
    
	/**
	 * 產出PDF 
	 * @throws IOException 
	 * @throws  
	 */
    private Document genPDF(KYC610InputVO kyc610,Document document , String qstType,boolean containAns) throws Exception {		
        PdfPCell blank = buildBlankCell(18, 0);
        PdfPTable tbTextLine = buildTable(new int[] {100});
        tbTextLine.addCell(blank);
               
        
        genTopContent(kyc610, document,qstType);
        
//        document.add(buildPartTable());
        PdfPTable tbTextTmp = buildTable(new int[] {100});
        PdfPCell blank12 = buildTitle("答題說明：請判斷題目敘述並作正確選項", bFont, Paragraph.ALIGN_LEFT);
        tbTextTmp.addCell(blank12);
        document.add(tbTextTmp);

        document.add(tbTextLine);
        document.add(buildPart1Table(kyc610,qstType,containAns));
        document.add(tbTextLine);
//        document.add(buildRemindTable());
//        document.add(tbTextLine);
        if (containAns)
        	document.add(buildRemindTable(kyc610));
        else
        	genDownContent(kyc610 , document);

        genBottomSeq(kyc610,document);
//        if (run == true){
//        	document.add(createPdf2(kyc310));
//        }

		return document;
	}
    private void genBottomSeq(KYC610InputVO kyc610 , Document document) throws Exception {
    	String empId = (String) getCommonVariable(SystemVariableConsts.LOGINID);

    	String branchNbr = kyc610.getResultMap().get("inputBranch")==null?"":(String)kyc610.getResultMap().get("inputBranch");//客戶承作問卷分行
    	String printDate = new SimpleDateFormat("yyyyMMddHHmm").format(new Date());
    	String examVersion = kyc610.getEXAM_VERSION();

        PdfPTable tb = buildTable(new int[] {50});
        tb.addCell(buildTitle(branchNbr+empId+printDate+examVersion, bFont, Paragraph.ALIGN_RIGHT));
    	document.add(tb);
    }
	public String queuryInvestBranchNbr(String empId) throws Exception{
		//判斷當下鑑機人員是否為企金OP或主管
		if(KYCCheck.checkIsLegalPerson(empId)){
			TBORG_MEMBERVO memberVo = (TBORG_MEMBERVO) getDataAccessManager().findByPKey(TBORG_MEMBERVO.TABLE_UID, empId);
			return memberVo.getDEPT_ID();
		}
		return (String) getCommonVariable(SystemVariableConsts.LOGINBRH);
	}
    private void genTopContent(KYC610InputVO kyc610 , Document document,String qstType) throws Exception{
    	String kycCustName = kyc610.getCUST_NAME();    
        String kycCustID = kyc610.getCUST_ID();
        String compName = kyc610.getCOMP_NAME();
        String compNbr = kyc610.getCOMP_NBR();
        custId = kyc610.getCUST_ID();//客戶ID
        int lineLength = 26;
        char addStr = ' ';
        
    	PdfPCell blank = buildBlankCell(18, 0);
        PdfPTable blankTb = buildTable(new int[] {84});
        blankTb.addCell(blank);

        PdfPTable tbTitleTable = buildTable(new int[] {84});
        
        if (qstType.equals("04")) {
        	tbTitleTable.addCell(buildTitle("\n台北富邦銀行專業投資人金融(含債券)專業知識評估問卷", bFont16, Paragraph.ALIGN_CENTER));
        	tbTitleTable.addCell(buildTitle("\n(本問卷為正反二頁)", bFont16, Paragraph.ALIGN_CENTER));
        }
        if (qstType.equals("05"))
        	tbTitleTable.addCell(buildTitle("\n台北富邦銀行結構型商品專業知識評估", bFont16, Paragraph.ALIGN_CENTER));
    	
        //申請人姓名、身分證字號/公司名稱、統一編號
        PdfPTable tbText1 = buildTable(new int[] {15,94});
        tbText1.addCell(buildTitle("申請人姓名 ：" , nFont, Paragraph.ALIGN_LEFT));
        tbText1.addCell(buildTitle(addStrBehind(kycCustName , addStr , lineLength), uFont, Paragraph.ALIGN_LEFT));
        tbText1.addCell(buildTitle(addStrBehind("身分證字號 ：" , addStr , lineLength), nFont, Paragraph.ALIGN_LEFT));
        tbText1.addCell(buildTitle(addStrBehind(kycCustID , addStr , lineLength), uFont, Paragraph.ALIGN_LEFT));
        // (如為法人之授權辦理交易人員請加註法人名稱及統一編號)
        if	(qstType.equals("04")) {
            tbText1.addCell(buildTitle("公司名稱 ：", nFont, Paragraph.ALIGN_LEFT));
            tbText1.addCell(buildTitle(addStrBehind(compName , addStr , lineLength), uFont, Paragraph.ALIGN_LEFT));
            tbText1.addCell(buildTitle("統一編號 ：", nFont, Paragraph.ALIGN_LEFT));
            tbText1.addCell(buildTitle(addStrBehind(compNbr , addStr , lineLength), uFont, Paragraph.ALIGN_LEFT));
        }

        document.add(tbTitleTable);//問卷表頭
        document.add(blankTb);
        document.add(tbText1);//表頭基本
    }
    
    private PdfPTable buildPart1Table(KYC610InputVO kyc610, String qstType, boolean containAns) throws Exception {
        PdfPTable tbPart1 = buildTable(new int[] {12,5,83});
        PdfPCell blank = buildBlankCell(18, 0);
        
        int Q = 1;//題號
        for(Map<String,Object> questionDesc:kyc610.getQuest_list()){
        	String corrAns = questionDesc.get("CORR_ANS")==null?"":(String)questionDesc.get("CORR_ANS");
        	corrAns = corrAns.trim().replace(" ", ",");
        	Object ansList = questionDesc.get("OPTION_LIST")==null?"":questionDesc.get("OPTION_LIST");
        	List<Map<String, Object>> ansList_change = (List<Map<String, Object>>) ansList;
        	
        	PdfPCell cPart1 = buildCell(questionDesc.get("QUESTION_DESC"), bFont, Paragraph.ALIGN_LEFT,-1,0);
//        	cPart1.setBackgroundColor(new Color(150,34,100));
        	//複選
        	String elsecheckbox = "";
//        	if(!"S".equals(questionDesc.get("QUESTION_TYPE").toString())){
//        		String checkbox = "";
//        		for(int i = 0;i<ansList_change.size();i++){
//        			if(i<4){
//        				checkbox+="(		)";
//        			}else{
//        				elsecheckbox+="(		)";
//        			}
//        			
//        		}
//            	tbPart1.addCell(buildCell(checkbox, nFontB, Paragraph.ALIGN_RIGHT,-1,0));
//
//        	}else{
        		if (containAns)
        			tbPart1.addCell(buildCell("("+corrAns+")", nFontB, Paragraph.ALIGN_RIGHT,-1,0));
        		else 
                	tbPart1.addCell(buildCell("(	)", nFontB, Paragraph.ALIGN_RIGHT,-1,0));
//        	}
        	
        	tbPart1.addCell(buildCell("Q"+Q, nFontPink, Paragraph.ALIGN_CENTER,-1,0));
        	tbPart1.addCell(cPart1);
        	Q++;
        	//題目選項
        	if(ansList_change.get(0).get("ANSWER_DESC").toString().length() > 15){
               String answerSD1 = "";
		        for(Map<String, Object> map:ansList_change){
		              tbPart1.addCell(blank);
		              tbPart1.addCell(blank);
		              answerSD1 = map.get("ANSWER_DESC").toString();
		              PdfPCell cell = buildCell(answerSD1, nFonts, Paragraph.ALIGN_LEFT,-1,0);
		              tbPart1.addCell(cell);
		              
		      	}
        	}else{
                String answerSD1 = "";
        		String answerSD2 = "";
        		if(!"S".equals(questionDesc.get("QUESTION_TYPE").toString())){
            		for(int j = 0;j<ansList_change.size();j++){
            			if(j<4){
            				answerSD1+=ansList_change.get(j).get("ANSWER_DESC").toString()+"　　　";
            			}else{
            				answerSD2+=ansList_change.get(j).get("ANSWER_DESC").toString()+"　　　";
            			}
            		}
            		
            		tbPart1.addCell(buildCell(elsecheckbox, nFontB, Paragraph.ALIGN_RIGHT,-1,0));
                    tbPart1.addCell(blank);
                    tbPart1.addCell(buildCell(answerSD1, nFonts, Paragraph.ALIGN_LEFT,-1,0));
                    tbPart1.addCell(blank);
                    tbPart1.addCell(blank);
                    tbPart1.addCell(buildCell(answerSD2, nFonts, Paragraph.ALIGN_LEFT,-1,0));
            	}else{
            		for(int j = 0;j<ansList_change.size();j++){
            			if(j<3){
            				answerSD1+=ansList_change.get(j).get("ANSWER_DESC").toString()+"　　　";
            			}else{
            				answerSD2+=ansList_change.get(j).get("ANSWER_DESC").toString()+"　　　";
            			}
            		}
            		tbPart1.addCell(blank);
                    tbPart1.addCell(blank);
                    tbPart1.addCell(buildCell(answerSD1, nFonts, Paragraph.ALIGN_LEFT,-1,0));
                    tbPart1.addCell(blank);
                    tbPart1.addCell(blank);
                    tbPart1.addCell(buildCell(answerSD2, nFonts, Paragraph.ALIGN_LEFT,-1,0));
            	}
        	}
        }
		return tbPart1;
    }

    private PdfPTable buildRemindTable(KYC610InputVO inputVO) throws Exception {
    	PdfPTable table = buildTable(new int[] {84});
    	StringBuffer sb = new StringBuffer();
    	if (inputVO.getQUEST_TYPE().equals("04"))
    		sb.append("通過標準為答對12題(含)以上(可錯3題)");
    	if (inputVO.getQUEST_TYPE().equals("05"))
    		sb.append("通過標準為答對7題(含)以上(可錯3題)");
    	table.addCell(buildCell(sb.toString(), bFont, Paragraph.ALIGN_LEFT, -1, 0));
        
	    return table;
	}

	public PdfPTable createPdf() throws Exception {
		
		PdfPCell blank = buildBlankCell(18, 0);
		PdfPTable table = new PdfPTable(3);
		table.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.setWidthPercentage(95);

		table.addCell(buildCell("公司/機構名稱：", bFont, Paragraph.ALIGN_LEFT,-1,0));
		table.addCell(buildCell("　　　　　　　　　　　　　　　　　　　", uFonts, Paragraph.ALIGN_LEFT,-1,0));
		
		PdfPCell cell = buildCell("", bFont, Paragraph.ALIGN_LEFT,-1,2); 
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setRowspan(5);
        table.addCell(cell);
        
		table.addCell(buildCell("負  責  人  ：", bFont, Paragraph.ALIGN_LEFT,-1,0));
		table.addCell(buildCell("　　　　　　　　　　　　　(負責人親簽)", uFonts, Paragraph.ALIGN_LEFT,-1,0));
		table.addCell(buildCell("統一編號  ：", bFont, Paragraph.ALIGN_LEFT,-1,0));
		table.addCell(buildCell("　　　　　　　　　　　　　　　　　　　", uFonts, Paragraph.ALIGN_LEFT,-1,0));
		table.addCell(buildCell("公司/機構代理人：", bFont, Paragraph.ALIGN_LEFT,-1,0));
		table.addCell(buildCell("　　　　　　　　　　　　(被授權人親簽)", uFonts, Paragraph.ALIGN_LEFT,-1,0));
		table.addCell(buildCell("身分證統一編號：", bFont, Paragraph.ALIGN_LEFT,-1,0));
		table.addCell(buildCell("　　　　　　　　　　　　　　　　　　　", uFonts, Paragraph.ALIGN_LEFT,-1,0));

        table.addCell(buildCell(blank, uFonts, Paragraph.ALIGN_LEFT,-1,0));
        table.addCell(buildCell(blank, uFonts, Paragraph.ALIGN_LEFT,-1,0));
        table.addCell(buildCell("（公司/機構往來帳號原留印鑑）", nFont, Paragraph.ALIGN_CENTER,-1,0));

        return table;
    }
	
	
    /**製作下方簽名印鑑、來往帳號，銀行註記等內容**/
    private void genDownContent(KYC610InputVO kyc610 , Document document) throws Exception{
    	PdfPCell blank = buildBlankCell(18, 0);
    	PdfPTable tbText9 = buildTable(new int[] {200});
		tbText9.addCell(blank);
		
		document.add(tbText9);
		if (kyc610.getQUEST_TYPE().equals("04"))
			document.add(genNotificationColumn());
		document.add(genUserSignatureColumn());

		document.add(genUserSignatureColumn1());
    }
	private PdfPTable genNotificationColumn() throws Exception {
		Font bFont = new Font(bfChinese, 9, Font.BOLD);
		Font uFonts = new Font(bfChinese, 9, Font.UNDERLINE);
		
		PdfPCell blank = buildBlankCell(18, 0);
		PdfPTable table =buildTable(new int[]{333});
		table.setHorizontalAlignment(Element.ALIGN_LEFT);
		table.setWidthPercentage(95);

		table.addCell(buildCell("申請人瞭解並同意  貴行及「台北富邦商業銀行履行個人資料保護法第八條第一項告知義務內容」(以下簡稱告知事項)所列對象於告知事項所列特定目的或法令許可範圍內，對申請人之資料為蒐集、處理、國際傳輸及相互交付利用。", bFont, Paragraph.ALIGN_LEFT , -1 , 0 ));
		table.addCell(buildCell("　　　　　　　　　　　　　　　　　　　", bFont, Paragraph.ALIGN_LEFT , -1 , 0 ));
		table.addCell(buildCell("　　　　　　　　　　　　　　　　　　　", bFont, Paragraph.ALIGN_LEFT , -1 , 0 ));
		return table;
    }
    
    /**製作下方區塊客戶相關需要填寫區塊**/
	public PdfPTable genUserSignatureColumn() throws Exception {
		Font bFont = new Font(bfChinese, 9, Font.BOLD);
		Font uFonts = new Font(bfChinese, 9, Font.UNDERLINE);
		
		PdfPCell blank = buildBlankCell(18, 0);
		PdfPTable table =buildTable(new int[]{24 , 43 , 33});
		table.setHorizontalAlignment(Element.ALIGN_LEFT);
		table.setWidthPercentage(95);

		table.addCell(buildCell(" 申請人親簽：", bFont, Paragraph.ALIGN_LEFT , -1 , 0 ));
		table.addCell(buildCell("　　　　　　　　　　　　　　　　　　　", uFonts, Paragraph.ALIGN_LEFT , -1 , 0 ));
		table.addCell(buildCell(" 日期：中華民國					年					月					日", bFont, Paragraph.ALIGN_LEFT , -1 , 0 ));

//        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//        cell.setRowspan(5);
        
          return table;
    }
	public PdfPTable genUserSignatureColumn1() throws Exception {
		Font bFont = new Font(bfChinese, 9, Font.BOLD);
		Font uFonts = new Font(bfChinese, 9, Font.UNDERLINE);
		
		PdfPCell blank = buildBlankCell(18, 0);
		PdfPTable table =buildTable(new int[]{24 , 43 , 33});
		table.setHorizontalAlignment(Element.ALIGN_LEFT);
		table.setWidthPercentage(95);
        table.addCell(buildTitle(" 銀行註記欄位 " , bFont, Paragraph.ALIGN_LEFT));
        table.addCell(blank);
        table.addCell(blank);
        table.addCell(buildTitle(" □外訪案件  " , bFont, Paragraph.ALIGN_LEFT));
        table.addCell(blank);
        table.addCell(blank);
        table.addCell(buildTitle(" 主管 ：" , bFont, Paragraph.ALIGN_LEFT));
        table.addCell(buildTitle(" 作業人員/未涉及銷售之業務人員(核對親簽)  ：" , bFont, Paragraph.ALIGN_LEFT));
        table.addCell(blank);
        table.addCell(buildTitle(" □通過" , bFont, Paragraph.ALIGN_LEFT));
        table.addCell(blank);
        table.addCell(blank);
        table.addCell(buildTitle(" □不通過" , bFont, Paragraph.ALIGN_LEFT));
        table.addCell(blank);
        table.addCell(blank);

        table.addCell(buildTitle(" 編號  ：" , bFont, Paragraph.ALIGN_LEFT));
        return table;
    }
        
	/**製作銀行註記欄**/
	public PdfPTable genBankNoteColumn(KYC610InputVO kyc610) throws Exception {
        //String format = formatter.format(kyc311.getBasic_information().get("EXPIRY_DATE"));
        
		PdfPTable table2 = buildTable(new int[]{84}, 100);
		PdfPCell blank = buildBlankCell(18, 0);
		PdfPTable table = new PdfPTable(20);
		table.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.setWidthPercentage(100);		

		PdfPCell cell = buildCell("銀行註記欄", nFonts, Paragraph.ALIGN_LEFT , -1 , 0 ); 
        cell.setColspan(3);
        table.addCell(cell);
		
        PdfPCell cell1 = buildCell("", nFonts, Paragraph.ALIGN_LEFT , -1 , 0 ); 
		cell1.setColspan(20);
        table.addCell(cell1);
        table.addCell(cell1);
        
		PdfPCell cell2 = null;
		cell2 = buildCell("營業單位：", nFonts, Paragraph.ALIGN_LEFT , -1 , 0 ); 
		cell2.setColspan(2);
        table.addCell(cell2);
        
		PdfPCell cell10 = buildCell("", nFonts, Paragraph.ALIGN_LEFT , -1 , 0 ); 
		cell10.setColspan(18);
        table.addCell(cell10);
      
        table.addCell(cell1);
        
		PdfPCell cell3 = buildCell("對保人(核對本人親簽無誤)：_________", nFonts, Paragraph.ALIGN_LEFT , -1 , 0 ); 
		cell3.setColspan(6);
        table.addCell(cell3);
        
		PdfPCell cell5 = buildCell("鍵機／解說人員：", nFonts, Paragraph.ALIGN_LEFT , -1 , 0 ); 
		cell5.setColspan(3);
        table.addCell(cell5);
		PdfPCell cell9= buildCell(" _________ ／ _________ ", nFonts, Paragraph.ALIGN_LEFT , -1 , 0 ); 
		cell9.setColspan(4);
        table.addCell(cell9);
        
		PdfPCell cell6= buildCell("驗印：_________", nFonts, Paragraph.ALIGN_LEFT , -1 , 0 ); 
		cell6.setColspan(3);
        table.addCell(cell6);
        
        PdfPCell cell7= null;
		cell7= buildCell("覆核主管：_________ " , nFonts, Paragraph.ALIGN_LEFT , -1 , 0 );
		cell7.setColspan(5);
        table.addCell(cell7);
        table2.addCell(table);
        return table2;
    }
    
    
    
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public PdfPTable createPdf2(KYC610InputVO kyc610) throws Exception {
		String loginID = (String) getCommonVariable(SystemVariableConsts.LOGINID);
		String loginBra = (String) getCommonVariable(SystemVariableConsts.LOGINBRHNAME);
		Map param = null;
		
		PdfPTable table2 = buildTable(new int[]{84}, 100);
		PdfPTable table = buildTable(new int[]{32,35,15,18});
		table.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.setWidthPercentage(100);

		PdfPCell cell = buildCell("銀行註記欄", nFonts, Paragraph.ALIGN_LEFT,-1,0); 
        cell.setColspan(3);
        table.addCell(cell);
		PdfPCell cell1 = buildCell("", nFonts, Paragraph.ALIGN_LEFT,-1,0); 
		cell1.setColspan(20);
        table.addCell(cell1);

        PdfPCell cell2 = null;
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT  ");
		sb.append("   DECODE(DEPT_ID , '999', '網銀', DEPT_NAME) DEPT_NAME ");
		sb.append(" FROM( ");
		sb.append("   SELECT  ");
		sb.append("     DEPT_ID, ");
		sb.append("     (SELECT DEPT_NAME FROM TBORG_DEFN WHERE DEPT_ID = a.DEPT_ID) DEPT_NAME ");
		sb.append("   FROM TBORG_MEMBER a WHERE emp_id = :empId ");
		sb.append(" ) ");
		param = new Hashtable();
		param.put("empId", loginID);
		list = exeQueryForMap(sb.toString() , param);

		if (list.size() > 0){
			cell2 = buildCell("營業單位："+list.get(0).get("DEPT_NAME"), nFonts, Paragraph.ALIGN_LEFT,-1,0);
		}else{
			cell2 = buildCell("營業單位："+loginBra, nFonts, Paragraph.ALIGN_LEFT,-1,0);
		}

		cell2.setColspan(5);
        table.addCell(cell2);
        

		PdfPCell cell3 = buildCell("對保人（核對本人親簽無誤）：_________", nFonts, Paragraph.ALIGN_LEFT,-1,0); 
        table.addCell(cell3);
        
        String LogInName = "";
        String keyinName = "";
        
        sb = new StringBuffer();
		sb.append("SELECT EMP_NAME ");
		sb.append("FROM TBORG_MEMBER where EMP_ID = :loginID");
		param = new Hashtable();
		param.put("loginID", loginID);
		list = exeQueryForMap(sb.toString() , param);

        if(list.size() > 0)
        	LogInName = list.get(0).get("EMP_NAME").toString();
        
        sb = new StringBuffer();
		sb.append("SELECT EMP_NAME ");
		sb.append("FROM TBORG_MEMBER where EMP_ID = :keyinID");
		param = new Hashtable();
		param.put("keyinID", kyc610.getCOMMENTARY_STAFF());
		list = exeQueryForMap(sb.toString() , param);

        if(list.size() > 0)
        	keyinName = list.get(0).get("EMP_NAME").toString();
        
		PdfPCell cell5 = buildCell("鍵機／解說人員："+LogInName+"／"+keyinName, nFonts, Paragraph.ALIGN_LEFT,-1,0); 
        table.addCell(cell5);
		PdfPCell cell6= buildCell("驗印：_________", nFonts, Paragraph.ALIGN_LEFT,-1,0); 
        table.addCell(cell6);
        PdfPCell cell7= buildCell("覆核主管： _________", nFonts, Paragraph.ALIGN_LEFT,-1,0); 
		cell7.setColspan(3);
		
		
        table.addCell(cell7);
        table2.addCell(table);
        return table2;
    }
	
	
    public PdfPCell buildBlankCell() {
		return buildBlankCell(18, -1);
	}
    
	public PdfPCell buildBlankCell(int height) {
		return buildBlankCell(height, -1);
	}
	
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

	public PdfPCell buildCell(PdfPTable table) {
		return buildCell(table, nFont, Paragraph.ALIGN_LEFT, -1, -1);
	}
	
	public PdfPCell buildCell(String value, Font font, int aligment) {
	    return buildCell(value, font, aligment, 9);
	}
	
	public PdfPCell buildCell(String value, Font font, int aligment, int height) {
		return buildCell(value, font, aligment, height, -1);
	}
	
	public PdfPCell buildCellhardWord(String value, Font font1, Font font2, int aligment) {
	    return buildCellhardWord(value, font1 ,font2, aligment, -1);
	}
	
	public PdfPCell buildCellhardWord(String value, Font font1,Font font2, int aligment, int height) {
		return buildCellhardWord(value, font1 ,font2, aligment, height, 0);
	}
	
	private PdfPCell buildCellWithBorder(String value, Font font, int aligment) {
		return buildCellWithBorder(value, font, aligment, -1, -1);
	}
	
	private PdfPCell buildCellWithBorder(String value, Font font, int aligment, int height) {
	    return buildCell(value, font, aligment, height, -1);
	}
	
	public PdfPCell buildCellWithBorder(String value, Font font, int aligment, int height, int br) {
	    return buildCell(value, font, aligment, height, br);
	}
	
	public PdfPCell buildCellWithBorder_rowspan(String value, Font font, int aligment, int row) {
	    return buildCell_rowspan(value, font, aligment, -1, -1, row);
	}
	
	/**
	 * 難字使用
	 * 	@param value
	 * 	@param font1
	 * 	@param font2
	 * 	@param aligment
	 * 	@return
	 *	@author Camel
	 *	@date    2013/5/10 上午10:12:23
	 *	@return PdfPCell
	 */
	private PdfPCell buildCellWithBorderHardWord(String value, Font font1 , Font font2 , int aligment) {
		return buildCellWithBorder(value, font1, font2 , aligment, -1, -1);
	}
	
	public PdfPCell buildCellWithBorder(String value, Font font1 ,Font font2 , int aligment, int height, int br) {
	    return buildCellhardWord(value, font1 , font2,  aligment, height, br);
	}
    
	public PdfPTable buildTable(int[] colLen) throws Exception {
        return buildTable(colLen, 98);
    }
    
	public PdfPTable buildTable(int[] colLen, int percent) throws Exception {
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
			Chunk chunk = new Chunk(value , font);
			Paragraph text = new Paragraph();
			text.add(chunk);
			text.setAlignment(alignment);
			cell.addElement(text);
		}
    	
        return cell;
	}
	
	public PdfPCell buildSection(Object value, Font font, int alignment) {
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
	
    public PdfPCell buildCell(Object value, Font font, int alignment, int height, int br) {
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
    		} else if (value instanceof PdfPTable){
    			cell.addElement((PdfPTable) value);
    		}
    	}
        return cell;
    }
    //cell with rowspan
    public PdfPCell buildCell_rowspan(Object value, Font font, int alignment, int height, int br, int row) {
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
    		} else if (value instanceof PdfPTable){
    			cell.addElement((PdfPTable) value);
    		}
    	}
    	cell.setRowspan(row);
        return cell;
    }

    
    public PdfPCell buildCellhardWord(Object value, Font font1 ,Font font2, int aligment, int height, int br) {
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

    			String inputStr = (String)value;
			    for(int i=0; i<inputStr.length(); i++){
			    	
			        char c = inputStr.charAt(i);
			        String tmpstr = String.valueOf(c);
			        if(!enc.canEncode(c)){
			        	Chunk chunk = new Chunk(tmpstr, font2);
			        	chunk.setTextRise(2);
		                text.add(chunk);
		                
			        }else{
			        	Chunk chunk = new Chunk(tmpstr, font1);
			        	chunk.setTextRise(2);
			        	//System.out.println("tmpstr : "+tmpstr);
		                text.add(chunk);
			        }
			    }
			    cell.addElement(text);
    				
    		} else if (value instanceof PdfPTable){
    			cell.addElement((PdfPTable) value);
    		}
    	}
        return cell;
    }

    
    /**
     * 取得專員姓名
     * 
     * @param empId
     * @return
     */
    public String getEmpName(String empId) {
    	return empId;
    }
    
    private int getFontWidth(String text, Font font) {
    	BaseFont bFont = nFont.getCalculatedBaseFont(true);
    	return (int)bFont.getWidthPoint(text, nFont.getCalculatedSize());
    }
    
    private PdfPTable genInnerTable(List<String> list, Font font) {
    	PdfPTable table = null;
    	int totalLen = 372;
    	int halfLen = totalLen / 2;
    	int thirdLen = totalLen / 3 ;
    	int qrLen = totalLen / 4;
    	int maxStrLen = 0;
    	
    	for (String str : list) {
    		int len = getFontWidth(str, font);
    		if (len > maxStrLen) {
    			maxStrLen = len;
    		}
    	}
    	
    	try {
    		if (maxStrLen > halfLen) {
        		table = new PdfPTable(1);
        		table.setWidths(new int[]{60});
        	} else if (maxStrLen > thirdLen) {
        		table = new PdfPTable(2);
        		table.setWidths(new int[]{30, 30});
        	} else if (maxStrLen > qrLen) {
        		table = new PdfPTable(3);
        		table.setWidths(new int[]{20, 20, 20});
        	} else {
        		table = new PdfPTable(4);
        		table.setWidths(new int[]{15, 15, 15, 15});
        	}
		} catch (DocumentException e) {
			e.printStackTrace();
		}
    	
    	// fill table cell
    	int mod = list.size() % table.getNumberOfColumns();
    	if (mod > 0) {
    		for (int i = 0; i < table.getNumberOfColumns()-mod; i++) {
    			list.add("");
    		}
    	}
    	
    	for (String str : list) {
    		table.addCell(buildCell(str, nFont, Paragraph.ALIGN_LEFT, 10, 0));
    	}
    	
    	return table;
    }
    
    
    //頁尾
	class MyPageEvent extends PdfPageEventHelper {
		
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
		public void appendPageFourCorners(PdfWriter writer) throws Exception{
			PdfPTable tmpTable = new PdfPTable(2);
			tmpTable.setTotalWidth(585);
			tmpTable.setLockedWidth(true);
			
			Font anyfont = new Font (bfChinese, 20, Font.NORMAL);
			PdfPCell cell3 = buildCell("＋", anyfont, Paragraph.ALIGN_LEFT , -1 , 0);
			cell3.setFixedHeight(30);
			cell3.setPadding(0);	
			cell3.setBorderWidth(0);
			tmpTable.addCell(cell3);
			
			cell3 = buildCell("＋", anyfont, Paragraph.ALIGN_RIGHT , -1 , 0);
			cell3.setFixedHeight(30);
			cell3.setPadding(0);	
			cell3.setBorderWidth(0);
			tmpTable.addCell(cell3);
			
			tmpTable.writeSelectedRows(0 , -1 , 5 , 828 , writer.getDirectContent());
			tmpTable.writeSelectedRows(0 , -1 , 5 , 76 , writer.getDirectContent());

			if(writer.getPageNumber() % 2 == 1){
				tmpTable = new PdfPTable(1);
				tmpTable.setTotalWidth(585);
				tmpTable.setLockedWidth(true);
				
		        //logo
		        Image img = Image.getInstance(nowpath + "//logo.png");
		        img.setWidthPercentage(70);
		        img.scaleToFit(150 , 150);//大小
		        
		        PdfPCell cLogo = new PdfPCell();
		        cLogo.addElement(img);
		        cLogo.setBorder(0);
		        tmpTable.addCell(cLogo);
		        
		        tmpTable.writeSelectedRows(0 , 5 , 25 , 835 , writer.getDirectContent());
			}
			tmpTable.flushContent();
		}
		
		//產生barCode
		public void appendBarCodeForEndPage(PdfWriter writer){
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
        	cell.setPadding(0);	//間格
    		cell.setBorderWidth(0); //邊框的寬度
    		
    		tBarCodeTable.addCell(cell);//添加到表中
    		tBarCodeTable.addCell(blank);
			
			//barcode - 客戶ID
			code39 = new Barcode39();
			code39.setCode(custId);
			code39.setBarHeight(18);//設定高度
			
			cell = new PdfPCell();
    		cell.setFixedHeight(30);//儲存格高度
        	cell.setPadding(0);	//間格
    		cell.setBorderWidth(0); //邊框的寬度
    		cell.addElement(code39.createImageWithBarcode(pdfContentByte, null, null));//產生一張barCode圖片並添加到列中
			
    		tBarCodeTable.addCell(cell);//添加到表中
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
    		tBarCodeTable.writeSelectedRows(0, -1, 25, 80, writer.getDirectContent());
		}
		
		//產生頁碼
		public void appendPageNumForEndPage(PdfWriter writer) throws BadElementException{			
			PdfPTable tPageNum = null;
			//page num
			tPageNum = new PdfPTable(2);
			tPageNum.setTotalWidth(545);
			
			PdfPCell curPage = new PdfPCell();
			curPage.setBorderWidth(0);
			
			Chunk chunk = new Chunk(String.format("第 %d 頁 /", writer.getPageNumber()), nFont);
			Paragraph text = new Paragraph();
            text.setAlignment(Phrase.ALIGN_RIGHT);
            text.add(chunk);
            curPage.addElement(text);	
			
			PdfPCell totalPage = new PdfPCell(Image.getInstance(total));
			totalPage.setBorderWidth(0);
			totalPage.setHorizontalAlignment(Image.ALIGN_LEFT);
			
			tPageNum.addCell(curPage);
			tPageNum.addCell(totalPage);
			
			tPageNum.writeSelectedRows(0, -1, 25, 50, writer.getDirectContent());
		}
		
		 /**
         * Fills out the total number of pages before the document is closed.
         */
        public void onCloseDocument(PdfWriter writer, Document document) {
        	Chunk chunk = new Chunk("共 "+ (writer.getPageNumber() - 1) + " 頁", nFont);
        	Phrase text = new Phrase();
            text.add(chunk);
			ColumnText.showTextAligned(total, Element.ALIGN_LEFT, text, 3, 2, 0);
        }
	}
    
	public static String addStrFront(String content , char addStr , int lineLength){
		return addStr(content , addStr , lineLength , true);
    }
	
	public static String addStrBehind(String content , char addStr , int lineLength){
    	return addStr(content , addStr , lineLength , false);
    }
	
	public static String addStr(String content , char addStr , int lineLength , boolean isFront){
		content = content == null ? "" : content.replaceAll("　*", "").trim();
		StringBuffer sbr = new StringBuffer(content);
    	int size = getStringLength(content);
    	
    	for(int i = 0 ; i < lineLength - size; i++){
    		if(isFront)
    			sbr.insert(0, addStr);
    		else
    			sbr.append(addStr);
    	}
    	return sbr.toString();
    }
	
    public static int getStringLength(String str){
    	int length = 0;
    	for(char chr : str.toCharArray()){
    		length += String.valueOf(chr).getBytes().length >= 2 ? 2 : 1;
    	}
    	return length;
    }


	public String getCustId() {
		return custId;
	}


	public void setCustId(String custId) {
		this.custId = custId;
	}


	public String getCustName() {
		return custName;
	}


	public void setCustName(String custName) {
		this.custName = custName;
	}


	public String getDeptNbr() {
		return deptNbr;
	}


	public void setDeptNbr(String deptNbr) {
		this.deptNbr = deptNbr;
	}


	public String getEmpId() {
		return empId;
	}


	public void setEmpId(String empId) {
		this.empId = empId;
	}


	public String getEvaSeq() {
		return evaSeq;
	}


	public void setEvaSeq(String evaSeq) {
		this.evaSeq = evaSeq;
	}
}
