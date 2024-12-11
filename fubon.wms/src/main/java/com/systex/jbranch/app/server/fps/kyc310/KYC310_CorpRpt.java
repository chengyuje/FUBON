package com.systex.jbranch.app.server.fps.kyc310;


import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.systex.jbranch.app.server.fps.kyc311.KYC311;
import com.systex.jbranch.app.server.fps.kyc311.KYC311InputVO;
import com.systex.jbranch.common.xmlInfo.XMLInfo;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;



/** KYC法人問卷中文版 **/
public class KYC310_CorpRpt extends FubonWmsBizLogic{

	private String custId ="";
	private String year;
	private int twYear;
	private String month;
	private String day;
	private String SEQ="";
	private File nowpath = new File (DataManager.getRealPath() , "doc//FPS");
	private File tempPath = new File (DataManager.getRealPath() ,"temp//reports");
//	private String nowpath = DataManager.getRealPath()+"//doc//FPS//" ;
	private Font nFont10;
	private Font nFontB;
	private Font uFont10;
	private Font nFontPink;
	private Font bFontWhite;
	private Font bFontWhiteBo;
	private Font nFonts;
	private Font uFonts;
	private Font uFont_a;
	
    private Font bFont;
    private Font nFont16;
    private Font bFontBlue;
    private Font nFontBlue;
	private BaseFont bfChinese;
	private BaseFont bfChinese2;//Ray	201305070205-00	pdf檔案可顯示難字

	private Color qstColor;
	private DataAccessManager dam = null;
	private List<Map<String, Object>> list;
	private SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
	private boolean isBank = true;
    /**這個PdfTemplate實例用於保存總頁數 */
    private PdfTemplate tpl;

    /**建構式初始化參數**/
    public KYC310_CorpRpt(){
    	try {
			bfChinese = BaseFont.createFont(nowpath + "//kaiu.ttf",	"Identity-H", BaseFont.NOT_EMBEDDED);
			bfChinese2 = BaseFont.createFont(nowpath +"/EUDC.TTF", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
			//bfChinese2 = BaseFont.createFont(nowpath +"//kaiu.ttf", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
			nFont16 = new Font(bfChinese, 16, Font.NORMAL);
			nFont10 = new Font(bfChinese, 10, Font.NORMAL);
			nFontB = new Font (bfChinese, 13, Font.NORMAL);
			nFonts = new Font(bfChinese, 9, Font.NORMAL);
			uFont10 = new Font(bfChinese, 10, Font.UNDERLINE);
			uFont_a = new Font(bfChinese2, 10, Font.UNDERLINE);
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
			qstColor = new Color(55, 169, 203);

	        //列印日期
	    	Calendar calendar = Calendar.getInstance();
	    	calendar.setTime(new Date());
	    	year = String.valueOf(calendar.get(Calendar.YEAR));//年
	        month = String.valueOf(calendar.get(Calendar.MONTH) + 1);//月
	        day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));//日
	        
    	}catch (Exception e) {
			System.out.println("genBlankPDF error:" + e.getMessage());
			e.printStackTrace();
		}
    }
    
    
    /**製作空白KYC pdf文件內容**/
    public void genBlankPDF(KYC311InputVO kyc311 , String fileName) {
    	try {
    		//設定所有產生的pdf檔案均不會壓縮
    		Document.compress = false;
    		//設定版面
    		Rectangle rectPageSize = new Rectangle(PageSize.A4);
    		//設定邊距(左,右,上,下)
    		Document document = new Document(rectPageSize, 10, 10, 15, 80);
    		//用於輸出pdf內容的物件
    		PdfWriter writer = PdfWriter.getInstance(document,new FileOutputStream(tempPath + "//" + fileName));
    		//添加事件，
    		writer.setPageEvent(new MyPageEvent(this , kyc311));
    		//開啟文件
    		document.open();
    		//開始製作第一段內容()
    		this.setBank(true);
    		genPDF(kyc311 , document , writer);
    		//添加新page
    		document.newPage();
    		//開始製作第二段內容
    		this.setBank(false);
    		genPDF(kyc311 , document , writer);
    		//關閉文件
    		document.close();
		} catch (Exception e) {
			System.out.println("genBlankPDF error:" + e.getMessage());
			e.printStackTrace();
		}
    }
    
	/**
	 * 產出PDF 
	 * @throws IOException 
	 * @throws  
	 */
    private Document genPDF(KYC311InputVO kyc311 , Document document , PdfWriter writer) throws Exception {
		//高18沒邊框的空白元件
    	PdfPCell blank = buildBlankCell(10, 0);
		//建立表格
		PdfPTable tbTextTmp = buildTable(new int[] {100});
		tbTextTmp.addCell(buildBlankCell(5, 0));
		//建立表格
		PdfPTable tbTextLine = buildTable(new int[] {100});
		tbTextLine.addCell(blank);
		//空白單據於問卷之前的上方區塊
		genTopContent(kyc311 , document , writer);
		//題目及選項
		buildPart1Table(kyc311 , document);
		document.add(tbTextTmp);        
		//風險屬性分析結果
		buildPart3Table(kyc311 , document);
		document.add(tbTextLine);
		//備註
		document.add(buildRemindTable());
		//document.add(tbText9);
		//確認與聲明
		document.add(buildRemindTable2(kyc311));
		//另確認以下事項
		document.add(buildRemindTable3(kyc311));
		//下方簽名區塊
		if(isBank())
			genDownContent(kyc311 , document);

		return document;
	}
    
    /**空白單據於問卷之前的上方區塊**/
    private void genTopContent(KYC311InputVO kyc311 , Document document , PdfWriter writer) throws Exception{
        custId = kyc311.getCUST_ID();//客戶id
        String createDate = formatter.format((Date)kyc311.getBasic_information().get("CREATE_DATE"));//鑑機日期
        SEQ = kyc311.getSEQ();//測試序號
        String kycCustName = kyc311.getCust_name();//客戶姓名
        String kycDAY = kyc311.getDAY();//電話(日)
        String kycCustAddr = kyc311.getCUST_ADDR_1();//通訊地址
        String kycRprsName = kyc311.getRPRS_NAME();//負責人姓名
        PdfPCell blank = buildBlankCell(18, 0);//空白
        int lineLength = 27;
        char addStr = ' ';

        PdfPTable tbTitleTable = buildTable(new int[] {84});
        tbTitleTable.addCell(buildTitle("\n\n客戶風險屬性評估問卷（適用於法人或機構）", nFont16, Paragraph.ALIGN_CENTER));
        
        PdfPTable tbText1 = buildTable(new int[] {15,85});
        tbText1.addCell(buildTitle("問卷測驗日期 ：", nFont10, Paragraph.ALIGN_LEFT));
        tbText1.addCell(buildTitle(createDate, nFont10, Paragraph.ALIGN_LEFT));
        
        PdfPTable tbText2 = buildTable(new int[] {15,85});
        tbText2.addCell(buildTitle("問卷列印日期 ：", nFont10, Paragraph.ALIGN_LEFT));
        tbText2.addCell(buildTitle(year + "/" + (month.matches("\\d{2}") ? "" : "0") + month + "/" + (day.matches("\\d{2}") ? "" : "0") + day , nFont10, Paragraph.ALIGN_LEFT));
        
        PdfPTable tbText3 = buildTable(new int[] {15,25,20,15,25});
        tbText3.addCell(buildTitle("公司/機構名稱：", nFont10, Paragraph.ALIGN_LEFT));
        tbText3.addCell(buildCellhardWordTitle(addStrBehind(kycCustName , addStr , lineLength), uFont10, uFont_a, Paragraph.ALIGN_LEFT));
        tbText3.addCell(blank);
        tbText3.addCell(buildTitle("負責人姓名：", nFont10, Paragraph.ALIGN_LEFT));
        tbText3.addCell(buildCellhardWordTitle(addStrBehind(kycRprsName , addStr , lineLength), uFont10, uFont_a, Paragraph.ALIGN_LEFT));
        
        PdfPTable tbText4 = buildTable(new int[] {15,25,20,15,25});
        tbText4.addCell(buildTitle("統 一 編 號　：", nFont10, Paragraph.ALIGN_LEFT));
        tbText4.addCell(buildTitle(addStrBehind(custId , addStr , lineLength), uFont10, Paragraph.ALIGN_LEFT));
        tbText4.addCell(blank);
        tbText4.addCell(buildTitle("電　　　話：", nFont10, Paragraph.ALIGN_LEFT));
        tbText4.addCell(buildTitle(addStrBehind(kycDAY , addStr , lineLength), uFont10, Paragraph.ALIGN_LEFT));
        
        PdfPTable tbText5 = buildTable(new int[] {15, 85});
        tbText5.addCell(buildTitle("通 訊 地 址　：", nFont10, Paragraph.ALIGN_LEFT));
        tbText5.addCell(buildTitle(addStrBehind(kycCustAddr , addStr , 94) , uFont10, Paragraph.ALIGN_LEFT));
        
        PdfPTable tbText6 = buildTable(new int[]{15,25,20,15,25});
        tbText6.addCell(buildTitle("電 子 郵 件　：", nFont10, Paragraph.ALIGN_LEFT));
        tbText6.addCell(buildTitle(addStrBehind(kyc311.getEMAIL_ADDR(), addStr, lineLength), uFont10, Paragraph.ALIGN_LEFT));
        tbText6.addCell(blank);
        tbText6.addCell(blank);
        tbText6.addCell(blank);
        
        document.add(tbTitleTable);//客戶風險屬性評估問卷（適用於法人或機構）
        document.add(tbText1);//問卷測驗日期
        document.add(tbText2);//問卷列印日期
        document.add(tbText3);//公司/機構名稱、負責人姓名
        document.add(tbText4);//統 一 編 號、電話
        document.add(tbText5);//通訊地址
        document.add(tbText6);//電子郵件
    }
    
    /**有效期間為一年**/
    private PdfPTable buildPartTable() throws Exception {
    	PdfPCell blank = buildBlankCell(18, 0);
    	PdfPTable tbPart1 = buildTable(new int[] {18,18,10,18,10,23});
    	
    	for(int i = 0 ; i < 5 ; i++)
    		tbPart1.addCell(blank);

        PdfPCell cPart1 = buildSection("本問卷分析結果有效期限一年", nFont10, Paragraph.ALIGN_BOTTOM);
        tbPart1.addCell(cPart1);
    	return tbPart1;
    }
    
    
    /**題目及選項**/
    private void buildPart1Table(KYC311InputVO kyc311 , Document document) throws Exception {
        PdfPTable tbPart1 = buildTable(new int[] {5,80,15});
        PdfPCell blank = buildBlankCell(10, 0);
        int j = 1;
        String answerAll = "";
        String answer_Seq = "";
        String answerSD = "";
        
        PdfPTable tbText6 = buildTable(new int[] {100});
        tbText6.addCell(blank);
        tbText6.addCell(blank);
        tbText6.addCell(buildTitle("以下的問卷可幫助評估投資人的風險屬性，為保護投資人權益，請詳實填答下列問題：", bFont, Paragraph.ALIGN_LEFT));
        
        for(Map<String,Object> questionDesc : kyc311.getQuestionList()){
        	int lineFinalSize = 91;
        	int lineSize = lineFinalSize;
        	StringBuffer lineContent = new StringBuffer();
        	//題目
        	PdfPCell cPart1 = buildCell(questionDesc.get("QUESTION_DESC"), bFontWhite, Paragraph.ALIGN_LEFT , -1 , 0 );
        	cPart1.setBackgroundColor(qstColor);
        	tbPart1.addCell(buildCell("Q" + j, bFont, Paragraph.ALIGN_CENTER , -1 , 0 ));
        	tbPart1.addCell(cPart1);
        	
        	//答案
        	List<Map<String, Object>> ansList_change = (List<Map<String, Object>>) questionDesc.get("ANSWER_LIST");
        	
        	if(j == 3) {
        		//第3題特殊處理
        		//答案及中間的空行
        		PdfPCell answerCell = buildCellWithBorder("", bFontWhite, Paragraph.ALIGN_CENTER);
	            tbPart1.addCell(answerCell);
        		cPart1 = buildCell("", bFontWhite, Paragraph.ALIGN_LEFT , -1 , 0 );
	            cPart1.setColspan(3);
	            cPart1.setFixedHeight(8);
	            tbPart1.addCell(cPart1);
	            tbPart1.addCell(blank);
	            
	            //第3題答案矩陣
	            //第3題選的答案
	            StringBuffer Q3Ans = new StringBuffer();
        		for(Map<String, Object> map1:ansList_change){
	        		if(map1.get("select") != null && map1.get("select").toString()=="true"){
	        			Q3Ans.append(map1.get("ANSWER_SEQ").toString()+";");
	        		}
	        	}
        		
        		PdfPCell cell3 = new PdfPCell(getQ3Ans(Q3Ans.toString()));
        		cell3.setBorderWidth(0);
        		tbPart1.addCell(cell3);
        		tbPart1.addCell(blank);
        	} else {
	        	//答案
	        	StringBuffer answer = new StringBuffer(); //取答案
	        	
	        	for(Map<String, Object> map1 : ansList_change){
	        		if(map1.get("select") != null && map1.get("select").toString() == "true"){
	        			answer_Seq = map1.get("ANSWER_SEQ").toString().substring(0 , map1.get("ANSWER_SEQ").toString().length() - 2);
	        			answer.append(answer_Seq + "|");
	        		}
	        	}
	        	
	        	if (answer.indexOf("|") != -1 && answer.substring(answer.length()-1).equals("|")){
	        		answerAll = answer.substring(0,answer.length()-1);
	        	}
	        	
	        	Font blueFont = new Font(bfChinese, 10, Font.NORMAL);
	        	blueFont.setColor(Color.BLUE);
	        	PdfPCell answerCell = buildCellWithBorder(answerAll, blueFont, Paragraph.ALIGN_CENTER);
	            tbPart1.addCell(answerCell);
	        	
	            cPart1 = buildCell("", bFontWhite, Paragraph.ALIGN_LEFT , -1 , 0 );
	            cPart1.setColspan(3);
	            cPart1.setFixedHeight(8);
	            tbPart1.addCell(cPart1);
	            
	        	//題目選項(一行約99字元)
	        	for(Map<String, Object> map : ansList_change){
	                answerSD = map.get("ANSWER_DESC").toString();
	                
	                //題目沒帶題號才需要加
	//                if(!answerSD.matches("^\\d+\\..*") && !answerSD.matches("^\\(\\d+\\).*"))
	//                	answerSD = map.get("ANSWER_SEQ").toString().substring(0 , map.get("ANSWER_SEQ").toString().length() - 2) + ". " + answerSD;
	                
	                //每行答案的總字元長度
	                int size = getStringLength(answerSD);
	                
	                //單一答案超過字數
	                if(size > lineFinalSize){
	                	if(lineContent.length() > 0){
	                		tbPart1.addCell(blank);
	                		tbPart1.addCell(buildCell(lineContent.toString(), nFonts, Paragraph.ALIGN_LEFT , -1 , 0));
	                        tbPart1.addCell(blank);
	                        lineContent = new StringBuffer();
	                        lineSize = lineFinalSize;
	                	}
	                	tbPart1.addCell(blank);
	                	tbPart1.addCell(buildCell(answerSD, nFonts, Paragraph.ALIGN_LEFT , -1 , 0));
	                    tbPart1.addCell(blank);
	                    continue;
	                }
	                //與上個答案長度相加包涵間隔長是足夠的
	                else if(lineSize - size >= 1 ){
	                	lineContent.append(lineContent.length() == 0 ? "" : " ").append(answerSD);                	
	                	lineSize = lineSize - size;
	                	continue;
	                }
	                //單一答案沒超過總長度，但與上一題相加會超過
	                else{
	            		tbPart1.addCell(blank);
	            		tbPart1.addCell(buildCell(lineContent.toString(), nFonts, Paragraph.ALIGN_LEFT , -1 , 0));
	                    tbPart1.addCell(blank);
	
	                    lineContent.delete(0, lineContent.length()).append(answerSD);
	                    lineSize = lineFinalSize - answerSD.length();
	                }
	                
	        	}
	        	//最後未添加的答案
	    		tbPart1.addCell(blank);
	    		tbPart1.addCell(buildCell(lineContent.toString(), nFonts, Paragraph.ALIGN_LEFT , -1 , 0));
	            tbPart1.addCell(blank);
        	}
            
            cPart1 = buildCell("", bFontWhite, Paragraph.ALIGN_LEFT , -1 , 0 );
            cPart1.setColspan(3);
            cPart1.setFixedHeight(10);
            tbPart1.addCell(cPart1);        	
            
            j++;
        }
        
        document.add(tbText6);//以下的問卷可幫助評估投資人的風險屬性，為保護投資人權益，請詳實填答下列問題
        document.add(buildPartTable());//有效期間為一年
        document.add(tbPart1);//問卷內容
    }
    
    private PdfPTable getQ3Ans(String ansStr) throws Exception {
    	XMLInfo xmlinfo = new XMLInfo();
    	List<Map<String, Object>> prodExpList = xmlinfo.getXMLInfo("KYC.Q3_PROD_EXP");
    	List<Map<String, Object>> prodTypeList = xmlinfo.getXMLInfo("KYC.Q3_PROD_TYPE");
    	
    	//第3題特殊處理
    	PdfPTable bigtable = buildTable(new int[]{5,90,5});
    	bigtable.getDefaultCell().setBorderWidth(0);
    	bigtable.addCell(buildCellQ3("  ", nFonts, Paragraph.ALIGN_CENTER, 12, 0));
    	
    	PdfPTable q3table = buildTable(new int[]{8,8,8,8,8,8});
    	for(int i = -1; i < prodTypeList.size(); i++) {
    		for(int j = -1; j < prodExpList.size(); j++) {
    			if(i == -1 && j == -1) {
    				q3table.addCell(setMiddle(buildCellQ3("", nFonts, Paragraph.ALIGN_CENTER, 12, (float)0.1)));
    			} else if(i == -1 && j > -1) {
    				q3table.addCell(setMiddle(buildCellQ3(prodExpList.get(j).get("PARAM_NAME").toString(), nFonts, Paragraph.ALIGN_CENTER, 12, (float)0.1)));
    			} else if(i > -1 && j == -1) {
    				q3table.addCell(setMiddle(buildCellQ3(prodTypeList.get(i).get("PARAM_NAME").toString(), nFonts, Paragraph.ALIGN_CENTER, 12, (float)0.1)));
    			} else  if(i > -1 && j > -1) {
    				String ansSeq = ObjectUtils.toString(i+1) + ObjectUtils.toString(j+1);
    				if(ansStr.indexOf(ansSeq) >= 0) {
    					q3table.addCell(setMiddle(buildCellQ3("■", nFonts, Paragraph.ALIGN_CENTER, 12, (float)0.1)));
    				} else {
    					q3table.addCell(setMiddle(buildCellQ3("□", nFonts, Paragraph.ALIGN_CENTER, 12, (float)0.1)));
    				}
    			}
    		}
    	}
    	bigtable.addCell(q3table);
    	bigtable.addCell(buildCellQ3("  ", nFonts, Paragraph.ALIGN_CENTER, 12, 0));
    	
		return bigtable;
    }
    
    /**製作下方簽名印鑑、來往帳號，銀行註記等內容**/
    private void genDownContent(KYC311InputVO kyc311 , Document document) throws Exception{
    	PdfPCell blank = buildBlankCell(18, 0);
    	PdfPTable tbText9 = buildTable(new int[] {100});
		tbText9.addCell(blank);
		
		document.add(tbText9);
		document.add(genUserSignatureColumn());//簽名印鑑
		document.add(tbText9);
		document.add(tbText9);
		document.add(genBankNoteColumn(kyc311));//銀行註記欄
    }
    
    public static int getStringLength(String str){
    	int length = 0;
    	for(char chr : str.toCharArray()){
    		length += String.valueOf(chr).getBytes().length >= 2 ? 2 : 1;
    	}
    	return length;
    }
    
    /**製作風險屬性分析結果**/
    private void buildPart3Table(KYC311InputVO kyc311 , Document document) throws Exception {
    	PdfPCell blank = buildBlankCell(18, 0);
    	
        PdfPTable tbText7 = buildTable(new int[] {5,80,15});
        PdfPCell tbText7b = buildCell("客戶風險屬性分析結果", bFontWhiteBo, Paragraph.ALIGN_CENTER,-1,1);
        tbText7b.setBackgroundColor(new Color(55,169,203));
        tbText7.addCell(buildCell(blank, nFont10, Paragraph.ALIGN_CENTER , -1 , 0 ));
        tbText7.addCell(tbText7b);
        tbText7.addCell(buildCell(blank, nFont10, Paragraph.ALIGN_CENTER , -1 , 0 ));
        
        XmlInfo xmlInfo = new XmlInfo();
        String custRiskAfr = (String) xmlInfo.getVariable("KYC.03_RESULT_DESC", kyc311.getCUST_RISK_AFR(), "F3");
        
        PdfPTable tbText8 = buildTable(new int[] {5,80,15});
        tbText8.addCell(buildCell(blank, nFont10, Paragraph.ALIGN_CENTER , -1 , 0 ));
        tbText8.addCell(buildCell("貴公司/機構屬於"+kyc311.getCUST_RISK_AFR()+"的"+kyc311.getRISK_TYPE()+"客戶。"+custRiskAfr, nFonts, Paragraph.ALIGN_LEFT,-1,1));
        tbText8.addCell(buildCell(blank, nFonts, Paragraph.ALIGN_CENTER , -1 , 0 ));
        
    	PdfPTable tbText9 = buildTable(new int[] {5,16,16,16,16,16,15});
        tbText9.addCell(buildCell(blank, nFont10, Paragraph.ALIGN_CENTER , -1 , 0 ));
        tbText9.addCell(buildCell("可承作投資型\n金融商品風險等級 ", nFonts, Paragraph.ALIGN_CENTER,-1,1));
        PdfPCell tbText9g = buildCell("P1等級\n（低度風險）", bFontWhiteBo, Paragraph.ALIGN_CENTER,-1,1);
        tbText9g.setBackgroundColor(new Color(0,175,0));
        tbText9.addCell(tbText9g);
        PdfPCell tbText9b = buildCell("P2等級\n（一般風險）", bFontWhiteBo, Paragraph.ALIGN_CENTER,-1,1);
        tbText9b.setBackgroundColor(new Color(0,102,255));
        tbText9.addCell(tbText9b);
        PdfPCell tbText9o = buildCell("P3等級\n（中度風險）", bFontWhiteBo, Paragraph.ALIGN_CENTER,-1,1);
        tbText9o.setBackgroundColor(new Color(255,153,0));
        tbText9.addCell(tbText9o);
        PdfPCell tbText9r = buildCell("P4等級\n（高度風險）", bFontWhiteBo, Paragraph.ALIGN_CENTER,-1,1);
        tbText9r.setBackgroundColor(new Color(255,0,0));
        tbText9.addCell(tbText9r);
        tbText9.addCell(blank);
        
		for(int i =0; i < kyc311.getRISKList().size(); i++){
			PdfPCell tbText9blb = buildCell(kyc311.getRISKList().get(i).get("RISKLIST"), bFontWhiteBo, Paragraph.ALIGN_CENTER,-1,1);
	    	tbText9blb.setBackgroundColor(Color.BLACK);
			switch(kyc311.getRISKList().get(i).get("CUST_RL_ID").toString()){
				case "C1":
					tbText9.addCell(buildCell(blank, nFont10, Paragraph.ALIGN_CENTER , -1 , 0 ));
					if (kyc311.getRISKList().get(i).get("CUST_RL_ID").equals(kyc311.getCUST_RISK_AFR()) == true){
						tbText9.addCell(tbText9blb);
					}else{
						tbText9.addCell(buildCell(kyc311.getRISKList().get(i).get("RISKLIST"), nFonts, Paragraph.ALIGN_CENTER,-1,1));
					}
			        tbText9.addCell(buildCell("V", nFonts, Paragraph.ALIGN_CENTER,-1,1));
			        tbText9.addCell(buildCell("不適合", nFonts, Paragraph.ALIGN_CENTER,-1,1));
			        tbText9.addCell(buildCell("不適合", nFonts, Paragraph.ALIGN_CENTER,-1,1));
			        tbText9.addCell(buildCell("不適合", nFonts, Paragraph.ALIGN_CENTER,-1,1));
			        tbText9.addCell(blank);
			        continue;
				case "C2":
					tbText9.addCell(buildCell(blank, nFont10, Paragraph.ALIGN_CENTER , -1 , 0 ));
					if (kyc311.getRISKList().get(i).get("CUST_RL_ID").equals(kyc311.getCUST_RISK_AFR()) == true){
						tbText9.addCell(tbText9blb);
					}else{
						tbText9.addCell(buildCell(kyc311.getRISKList().get(i).get("RISKLIST"), nFonts, Paragraph.ALIGN_CENTER,-1,1));
					}
			        tbText9.addCell(buildCell("V", nFonts, Paragraph.ALIGN_CENTER,-1,1));
			        tbText9.addCell(buildCell("V", nFonts, Paragraph.ALIGN_CENTER,-1,1));
			        tbText9.addCell(buildCell("不適合", nFonts, Paragraph.ALIGN_CENTER,-1,1));
			        tbText9.addCell(buildCell("不適合", nFonts, Paragraph.ALIGN_CENTER,-1,1));
			        tbText9.addCell(blank);
			        continue;
				case "C3":
					tbText9.addCell(buildCell(blank, nFont10, Paragraph.ALIGN_CENTER , -1 , 0 ));
					if (kyc311.getRISKList().get(i).get("CUST_RL_ID").equals(kyc311.getCUST_RISK_AFR()) == true){
						tbText9.addCell(tbText9blb);
					}else{
						tbText9.addCell(buildCell(kyc311.getRISKList().get(i).get("RISKLIST"), nFonts, Paragraph.ALIGN_CENTER,-1,1));
					}
			        tbText9.addCell(buildCell("V", nFonts, Paragraph.ALIGN_CENTER,-1,1));
			        tbText9.addCell(buildCell("V", nFonts, Paragraph.ALIGN_CENTER,-1,1));
			        tbText9.addCell(buildCell("V", nFonts, Paragraph.ALIGN_CENTER,-1,1));
			        tbText9.addCell(buildCell("不適合", nFonts, Paragraph.ALIGN_CENTER,-1,1));
			        tbText9.addCell(blank);
			        continue;
				case "C4":
					tbText9.addCell(buildCell(blank, nFont10, Paragraph.ALIGN_CENTER , -1 , 0 ));
					if (kyc311.getRISKList().get(i).get("CUST_RL_ID").equals(kyc311.getCUST_RISK_AFR()) == true){
						tbText9.addCell(tbText9blb);
					}else{
						tbText9.addCell(buildCell(kyc311.getRISKList().get(i).get("RISKLIST"), nFonts, Paragraph.ALIGN_CENTER,-1,1));
					}	        
			        tbText9.addCell(buildCell("V", nFonts, Paragraph.ALIGN_CENTER,-1,1));
			        tbText9.addCell(buildCell("V", nFonts, Paragraph.ALIGN_CENTER,-1,1));
			        tbText9.addCell(buildCell("V", nFonts, Paragraph.ALIGN_CENTER,-1,1));
			        tbText9.addCell(buildCell("V", nFonts, Paragraph.ALIGN_CENTER,-1,1));
			        tbText9.addCell(blank);
			        continue;
				case "C5":
					tbText9.addCell(buildCell(blank, nFont10, Paragraph.ALIGN_CENTER , -1 , 0 ));
					if (kyc311.getRISKList().get(i).get("CUST_RL_ID").equals(kyc311.getCUST_RISK_AFR()) == true){
						tbText9.addCell(tbText9blb);
					}else{
						tbText9.addCell(buildCell(kyc311.getRISKList().get(i).get("RISKLIST"), nFonts, Paragraph.ALIGN_CENTER,-1,1));
					}	        
			        tbText9.addCell(buildCell("V", nFonts, Paragraph.ALIGN_CENTER,-1,1));
			        tbText9.addCell(buildCell("V", nFonts, Paragraph.ALIGN_CENTER,-1,1));
			        tbText9.addCell(buildCell("V", nFonts, Paragraph.ALIGN_CENTER,-1,1));
			        tbText9.addCell(buildCell("V", nFonts, Paragraph.ALIGN_CENTER,-1,1));
			        tbText9.addCell(blank);
			        continue;
			}
		}

        //風險屬性分析結果
        document.add(tbText7);
        document.add(tbText8);
        document.add(tbText9);
    }

    /**製作備註區塊**/
    private PdfPTable buildRemindTable() throws Exception {
    	PdfPTable table = buildTable(new int[] {84});
    	StringBuffer sb = new StringBuffer();
    	sb.append("\n\n備註：1. 貴公司／機構所勾選／填寫之問卷內容及分析結果，僅供本行作為日後進行交易時之商品適合度評估依據。本\n")
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

    	table.addCell(buildCell(sb.toString(), bFont, Paragraph.ALIGN_LEFT, -1, 0));
        
	    return table;
	}
    
    /**確認與聲明**/
    private PdfPTable buildRemindTable2(KYC311InputVO kyc311) throws Exception {
    	KYC311 kyccooling = (KYC311) PlatformContext.getBean("kyc311");
		kyccooling.getCoolingPeriodRiskVal(kyc311);
		
    	PdfPTable table = buildTable(new int[] {84});
    	StringBuffer sb = new StringBuffer();
    	sb.append("確認與聲明：\n")
    	  .append("1. 本公司／機構確認已收執本問卷分析結果副本乙份【與本行正本內容無誤】。\n")
    	  .append("2. 以上問卷內容及分析結果經本公司／機構確認無誤。");
    	
    	//若本次評估結果較上次結果為高，顯示訊息
    	if(StringUtils.isNotBlank(kyc311.getCUST_RISK_BEF())) {
    		try {
	    		int riskBef = Integer.parseInt(kyc311.getCUST_RISK_BEF().substring(1));
	    		int riskAfr = Integer.parseInt(kyc311.getCUST_RISK_AFR().substring(1));
	    		
	    		if(riskAfr > riskBef) {
	    			if(kyc311.isCOOLING()){
	    				sb.append("\n3. 貴公司本次的風險屬性評估結果為" + kyc311.getCUST_RISK_AFR() + "等級(" + kyc311.getRISK_TYPE() + ")，較上次評估結果")
		    			  .append(kyc311.getCUST_RISK_BEF() + "等級(" + kyc311.getRISK_TYPE_BEF() + ")提高，為維護貴公司的權益，測試結果將自今日起算二日後生效，請")
		    			  .append("   貴公司再次確認所填問卷內容的正確性。\n")
		    	    	  .append("4. 本次評估結果係貴公司依貴公司的實際情況自主填寫，未受本行任何人員影響。");
	    			}else{
	    				sb.append("\n3. 貴公司本次的風險屬性評估結果為" + kyc311.getCUST_RISK_AFR() + "等級(" + kyc311.getRISK_TYPE() + ")，較上次評估結果")
		    			  .append(kyc311.getCUST_RISK_BEF() + "等級(" + kyc311.getRISK_TYPE_BEF() + ")提高，為維護貴公司的權益，請")
		    			  .append("   貴公司再次確認所填問卷內容的正確性。\n")
		    	    	  .append("4. 本次評估結果係貴公司依貴公司的實際情況自主填寫，未受本行任何人員影響。");
	    			}
	    		}
    		} catch(Exception e) {
    			
    		}
    	}
    	
    	table.addCell(buildCell(sb.toString(), bFont, Paragraph.ALIGN_LEFT, -1, 0));
        
	    return table;
	}
    /**另確認以下項目**/
    private PdfPTable buildRemindTable3(KYC311InputVO kyc311) throws Exception {
    	PdfPTable table = buildTable(new int[] {84});
    	StringBuffer sb = new StringBuffer();
    	XmlInfo xmlInfo = new XmlInfo();    	

    	//前一次信箱
    	String EMAIL_BEFORE = kyc311.getCUST_EMAIL_BEFORE();
    	//本次輸入信箱
    	String EMAIL_AFTER = kyc311.getEMAIL_ADDR();
    	//信箱重覆原因
    	String SAMEEMAIL_CHOOSE = (String)xmlInfo.getVariable("KYC.EMAIL_CHANGE", kyc311.getSAMEEMAIL_CHOOSE(), "F3");   
    	//信箱重覆理由
    	String SAMEEMAIL_REASON = kyc311.getSAMEEMAIL_REASON();
    	
    	//顯示文字調整
    	if(StringUtils.isBlank(EMAIL_BEFORE)) EMAIL_BEFORE = "無";
    	if(StringUtils.isBlank(EMAIL_AFTER)) EMAIL_AFTER = "無";
		String REASON_SHOW = SAMEEMAIL_REASON;
		if(StringUtils.isNotBlank(SAMEEMAIL_CHOOSE) && SAMEEMAIL_CHOOSE.indexOf("其他") < 0){
			REASON_SHOW = SAMEEMAIL_CHOOSE;
		}

		if(StringUtils.equals(EMAIL_BEFORE, EMAIL_AFTER)){
			sb.append("\n\n貴公司/機構留存的電子郵件為：【" + EMAIL_AFTER + "】，請再次確認留存資料正確性。");
		}else if(!StringUtils.equals(EMAIL_BEFORE, EMAIL_AFTER) && StringUtils.isNotBlank(SAMEEMAIL_CHOOSE)){
			sb.append("\n\n貴公司/機構留存的電子郵件由【" + EMAIL_BEFORE + "】更新為【" + EMAIL_AFTER +
					"】，本行已發送確認連結至貴公司/機構新留存信箱，本次變更將於點選確認連結後生效，請再次確認留存資料正確性。");
			sb.append("\n\n經比對，貴公司/機構留存的電子郵件與本行其他客戶有重覆情形，原因為：【" +  REASON_SHOW + "】，請再次確認。");
		}else if(!StringUtils.equals(EMAIL_BEFORE, EMAIL_AFTER) && !StringUtils.isNotBlank(SAMEEMAIL_CHOOSE)){
			if(StringUtils.equals("無", EMAIL_AFTER)){
				sb.append("\n\n貴公司/機構留存的電子郵件由【" + EMAIL_BEFORE + "】更新為【" + EMAIL_AFTER +
						"】，請再次確認留存資料正確性。");
			} else {
				sb.append("\n\n貴公司/機構留存的電子郵件由【" + EMAIL_BEFORE + "】更新為【" + EMAIL_AFTER +
						"】，本行已發送確認連結至貴公司/機構新留存信箱，本次變更將於點選確認連結後生效，請再次確認留存資料正確性。");
			}
			
		}
    	//有內容時增加標題
    	if(sb.length()>0){
    		sb.insert(0, "\n\n另確認以下項目：");
    	}
    	table.addCell(buildCell(sb.toString(), bFont, Paragraph.ALIGN_LEFT, -1, 0));
    	return table;
    }
	
    /**製作下方區塊客戶相關需要填寫區塊**/
	public PdfPTable genUserSignatureColumn() throws Exception {
		PdfPTable table = buildTable(new int[]{24, 43, 33});
		table.setHorizontalAlignment(Element.ALIGN_LEFT);
		table.setWidthPercentage(95);

		table.addCell(buildCell(" 公司/機構名稱：", nFont10, Paragraph.ALIGN_LEFT, -1, 0));
		table.addCell(buildCell("　　　　　　　　　　　　　　　　　　　", uFont10, Paragraph.ALIGN_LEFT, -1, 0));

		PdfPCell cell = buildCell("", nFont10, Paragraph.ALIGN_LEFT, -1, 2);
		cell.setBorder(0);
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
	
	
	/**製作銀行註記欄**/
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public PdfPTable genBankNoteColumn(KYC311InputVO kyc311) throws Exception {
        String format = formatter.format(kyc311.getBasic_information().get("EXPIRY_DATE"));
        String loginID = (String) getCommonVariable(SystemVariableConsts.LOGINID);
        Map param = null;
        
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
		list = exeQueryForMap(sb.toString(), param);
		
		if (CollectionUtils.isNotEmpty(list)){
			String deptName =  list.get(0).get("DEPT_NAME") == null ? "" : list.get(0).get("DEPT_NAME").toString();
			cell2 = buildCell("營業單位： " + deptName , nFonts, Paragraph.ALIGN_LEFT , -1 , 0 ); 
		}
		else
			cell2 = buildCell("營業單位：", nFonts, Paragraph.ALIGN_LEFT , -1 , 0 ); 
		
		cell2.setColspan(5);
        table.addCell(cell2);
        
		PdfPCell cell10 = buildCell("", nFonts, Paragraph.ALIGN_LEFT , -1 , 0 ); 
		cell10.setColspan(1);
        table.addCell(cell10);
        
		PdfPCell cell11 = buildCell("測試序號：", nFonts, Paragraph.ALIGN_LEFT , -1 , 0 ); 
		cell11.setColspan(2);
        table.addCell(cell11);

		PdfPCell cell12 = buildCell(kyc311.getBasic_information().get("SEQ"), nFonts, Paragraph.ALIGN_LEFT , -1 , 0 ); 
		cell12.setColspan(4);
        table.addCell(cell12);

        table.addCell(buildCell(blank, nFonts, Paragraph.ALIGN_LEFT , -1 , 0 ));
		PdfPCell cell13 = buildCell("本次測試有效期限：", nFonts, Paragraph.ALIGN_LEFT , -1 , 0 ); 
		cell13.setColspan(3);
        table.addCell(cell13);
		PdfPCell cell14 = buildCell(format, nFonts, Paragraph.ALIGN_LEFT , -1 , 0 ); 
		cell14.setColspan(3);
        table.addCell(cell14);
        
        table.addCell(cell1);
        table.addCell(cell1);
        
		PdfPCell cell3 = buildCell("對保人(核對本人親簽無誤)：_________", nFonts, Paragraph.ALIGN_LEFT , -1 , 0 ); 
		cell3.setColspan(6);
        table.addCell(cell3);
        
		PdfPCell cell5 = buildCell("鍵機／解說人員：", nFonts, Paragraph.ALIGN_LEFT , -1 , 0 ); 
		cell5.setColspan(3);
        table.addCell(cell5);
		PdfPCell cell9= buildCell(kyc311.getBasic_information().get("KEYIN")+"／"+kyc311.getBasic_information().get("COMMENTATOR"), nFonts, Paragraph.ALIGN_LEFT , -1 , 0 ); 
		cell9.setColspan(4);
        table.addCell(cell9);
        
		PdfPCell cell6= buildCell("驗印：_________", nFonts, Paragraph.ALIGN_LEFT , -1 , 0 ); 
		cell6.setColspan(3);
        table.addCell(cell6);
        
        PdfPCell cell7= null;
		sb = new StringBuffer();
		sb.append("SELECT EMP_NAME ");
		sb.append("FROM TBORG_MEMBER where EMP_ID = :empId ");
		sb.append("and '02' = (select STATUS from TBKYC_INVESTOREXAM_M where cust_id = :custId ) ");
		param = new TreeMap();
		param.put("empId", kyc311.getEMP_ID());
		param.put("custId", kyc311.getCUST_ID());		
		list = exeQueryForMap(sb.toString(), param);	
		
		if (list.size() > 0){
			cell7= buildCell("覆核主管： " + list.get(0).get("EMP_NAME") , nFonts, Paragraph.ALIGN_LEFT , -1 , 0 ); 
		}else{
			cell7= buildCell("覆核主管： ", nFonts, Paragraph.ALIGN_LEFT , -1 , 0 ); 
		}
		
		cell7.setColspan(3);
        table.addCell(cell7);
		PdfPCell cell8= buildCell(kyc311.getBasic_information().get("SUPERVISOR"), nFonts, Paragraph.ALIGN_LEFT , -1 , 0 ); 
		cell8.setColspan(2);
        table.addCell(cell8);
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
		return buildCell(table, nFont10, Paragraph.ALIGN_LEFT, -1, -1);
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
	
	public PdfPCell buildCellhardWordTitle(String value, Font font1 ,Font font2, int aligment) {
    	PdfPCell cell = new PdfPCell();
    	cell.setVerticalAlignment(Paragraph.ALIGN_MIDDLE);
		cell.setBorder(0);
    	
    	if (value != null) {
    		value = value.replaceFirst("  $", "　");
    		
    		Paragraph text = new Paragraph();
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
	
	public PdfPCell buildSection(Object value, Font font, int alignment) {
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
    	BaseFont bFont = nFont10.getCalculatedBaseFont(true);
    	return (int)bFont.getWidthPoint(text, nFont10.getCalculatedSize());
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
    	
    	//fill table cell
    	int mod = list.size() % table.getNumberOfColumns();
    	if (mod > 0) {
    		for (int i = 0; i < table.getNumberOfColumns()-mod; i++) {
    			list.add("");
    		}
    	}
    	
    	for (String str : list) {
    		table.addCell(buildCell(str, nFont10, Paragraph.ALIGN_LEFT, 10, 0));
    	}
    	
    	return table;
    }
    
    
    /* pdf事件
     * onOpenDocument() — 當打開一個檔案時觸發，可以用於初始化檔案的全域變數。
     * onStartPage() — 當一個頁面初始化時觸發，可用於初始化頁面的設置參數，這個函數觸發時，該頁面並沒有建立好，此時不可利用這個函數添加內容，最好利用onEndPage()處理頁面的初始化。
     * onEndPage() — 在創建一個新頁面完成但寫入內容之前觸發，是添加頁眉、頁腳、水印等最佳時機。
     * onCloseDocument() — 在文檔關閉之前觸發，可以用於釋放一些資源。
     */
	class MyPageEvent extends PdfPageEventHelper {
		private PdfTemplate total;
		private KYC310_CorpRpt kyc310Gen;
		private KYC311InputVO inputVO;
		
		private MyPageEvent(KYC310_CorpRpt kyc310Gen){
			this.kyc310Gen = kyc310Gen;
		}
		
		public MyPageEvent(KYC310_CorpRpt kyc310Gen , KYC311InputVO inputVO){
			this(kyc310Gen);
			this.inputVO = inputVO;
		}
		
		/**
         * Creates the PdfTemplate that will hold the total number of pages.
         */
        public void onOpenDocument(PdfWriter writer, Document document) {
            total = writer.getDirectContent().createTemplate(50, 20);
        }
		
        //頁尾
		@Override
		public void onEndPage(PdfWriter writer, Document document) {
			try {
				appendPageFourCorners(writer);//四角加號
				//一共四頁，後三頁不顯示barcode
				if(getKyc310Gen().isBank())
					appendBarCodeForEndPage(writer);//barCode
				
				appendPageNumForEndPage(writer);//頁碼
			} catch (Exception e) {
				e.printStackTrace();
			}
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
			
//			if(writer.getPageNumber() % 2 == 1)
//				tmpTable.writeSelectedRows(0 , -1 , 5 , 814 , writer.getDirectContent());
//			else
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
			// #2252_調整KYC問卷條碼及版次
			if (writer.getPageNumber() != 1) return; 
			
			PdfPTable tBarCodeTable = new PdfPTable(6);//表格
			tBarCodeTable.setTotalWidth(545);
			
			PdfContentByte pdfContentByte = writer.getDirectContent();
			
			Date createDate = (Date)getInputVO().getBasic_information().get("CREATE_DATE");//鑑機日期
			String codeDate = "";
			if(createDate != null){
				Calendar calCreateDate = Calendar.getInstance();
				calCreateDate.setTime(createDate);
				String twYear = String.valueOf(calCreateDate.get(Calendar.YEAR) - 1911);
				String month =  (calCreateDate.get(Calendar.MONTH) + 1) < 10 ? "0" : "";
				month += (calCreateDate.get(Calendar.MONTH) + 1);
				String day = String.valueOf(calCreateDate.get(Calendar.DAY_OF_MONTH));
				codeDate = twYear + month + day;
			}
			else{
				twYear = Integer.parseInt(year) - 1911;//民國年
				codeDate = String.valueOf(twYear) + (month.matches("\\d{2}") ? "" : "0") + month + (day.matches("\\d{2}") ? "" : "0") + day;
			}
			
			PdfPCell blank = new PdfPCell();//空白列
    		blank.setBorderWidth(0);//邊框寬度
    		blank.setFixedHeight(10);//儲存格高度

			// barcode - tw date
			Barcode39 code39 = new Barcode39();
			code39.setCode(codeDate);// 設定內容為民國年月日
			code39.setBarHeight(14);// 設定高度

			PdfPCell cell = new PdfPCell();// 產生列物件
			cell.setFixedHeight(30);// 儲存格高度
			cell.setPadding(0); // 間格
			cell.setBorderWidth(0); // 邊框的寬度
			cell.addElement(code39.createImageWithBarcode(pdfContentByte, null, null));// 產生一張barCode圖片並添加到列中

			tBarCodeTable.addCell(cell);// 添加到表中
			tBarCodeTable.addCell(blank);

			// barcode - 客戶ID
			code39 = new Barcode39();
			code39.setCode(custId);
			code39.setBarHeight(18);// 設定高度

			cell = new PdfPCell();
			cell.setFixedHeight(30);// 儲存格高度
			cell.setPadding(0); // 間格
			cell.setBorderWidth(0); // 邊框的寬度
			cell.addElement(code39.createImageWithBarcode(pdfContentByte, null, null));// 產生一張barCode圖片並添加到列中

			tBarCodeTable.addCell(cell);// 添加到表中
			tBarCodeTable.addCell(blank);

			// barcode - page
			code39 = new Barcode39();
			code39.setCode("88-0601-99");
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

			tBarCodeTable.addCell(cell);// 添加到表中
    		tBarCodeTable.writeSelectedRows(0, -1, 25, 80, writer.getDirectContent());
		}
		
		//產生頁碼
		public void appendPageNumForEndPage(PdfWriter writer) throws DocumentException{			
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
		
        public void onCloseDocument(PdfWriter writer, Document document) {
        	Chunk chunk = new Chunk("共 "+ (writer.getPageNumber() - 1) + " 頁", nFont10);
        	Phrase text = new Phrase();
            text.add(chunk);
			ColumnText.showTextAligned(total, Element.ALIGN_LEFT, text, 3, 2, 0);
        }
        
		public KYC310_CorpRpt getKyc310Gen() {
			return kyc310Gen;
		}
		
		public void setKyc310Gen(KYC310_CorpRpt kyc310Gen) {
			this.kyc310Gen = kyc310Gen;
		}
		public KYC311InputVO getInputVO() {
			return inputVO;
		}
		public void setInputVO(KYC311InputVO inputVO) {
			this.inputVO = inputVO;
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
	
	
	
	public boolean isBank() {
		return isBank;
	}


	public void setBank(boolean isBank) {
		this.isBank = isBank;
	}
}
