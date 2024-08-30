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


/** KYC自然人問卷中文版 **/
public class KYC310_PersonRpt extends FubonWmsBizLogic{

	private File nowpath = new File (DataManager.getRealPath() , "doc//FPS");
	private File tempPath = new File (DataManager.getRealPath() ,"temp//reports");
//	private String nowpath = DataManager.getRealPath()+"//doc//FPS//" ;

	private Font nFont;
	private Font nFont16;
	private Font uFont;
	private Font bFontWhite;
	private Font bFontWhiteBo;
	private Font nFonts;
	private Font nFont_a;
	private Font uFonts;
	private Font bFonts;
	private Font bFont16;
    private Font bFont;
    private String branch_name = "";
    private String intro = "";
    private String cust_id = "";
    private String twYear;
	private String month;
	private String day;
	private DataAccessManager dam = null;
	private List<Map<String, Object>> list;
	private Font nFont1;	
    
//    private Font nFontBlue;
	private BaseFont bfChinese;
	private BaseFont bfChinese2;//Ray	201305070205-00	pdf檔案可顯示難字

	
    /**這個PdfTemplate實例用於保存總頁數 */
    private PdfTemplate tpl;
	
	/**
	 * @param args
	 */
//	public static void main(String[] args) {
//		KYC310_testC pdg = new KYC310_testC();
////		pdg.genBlankPDF();
//	}

    /**
     * 空白KYC
     */
    public void genBlankPDF(KYC311InputVO kyc311 , String fileName) {
    	try {
    		Document.compress = false;
    		Rectangle rectPageSize = new Rectangle(PageSize.A4);// 設定版面
    		Document document = new Document(rectPageSize, 10, 10, 15, 80);// 設定邊距(左,右,上,下)
    		
    		PdfWriter writer = PdfWriter.getInstance(document,new FileOutputStream(tempPath + "//" + fileName));
    		writer.setPageEvent(new MyPageEvent());
    		document.open();
    		
    		genPDF(kyc311,document,"1");
    		document.newPage();
    		genPDF(kyc311,document,"2");
    		document.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
	/**
	 * 產出PDF 
	 * @throws IOException 
	 * @throws  
	 */
    private Document genPDF(KYC311InputVO kyc311,Document document,String type)  throws Exception {
    	
    	bfChinese = BaseFont.createFont(nowpath + "//kaiu.ttf",	"Identity-H", BaseFont.NOT_EMBEDDED);
    	bfChinese2 = BaseFont.createFont(nowpath +"/EUDC.TTF", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
//    	bfChinese2 = BaseFont.createFont(nowpath +"//kaiu.ttf", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
    	bFont16 = new Font(bfChinese, 16, Font.BOLD);//標題字體
    	nFont = new Font(bfChinese, 10, Font.NORMAL);//內文
    	nFont_a = new Font(bfChinese2, 10, Font.NORMAL);
    	nFonts = new Font(bfChinese, 9, Font.NORMAL);//內文
    	uFont = new Font(bfChinese, 10, Font.UNDERLINE);//內文底線
    	uFonts = new Font(bfChinese, 8, Font.UNDERLINE);//內文底線
    	bFont = new Font(bfChinese, 10, Font.BOLD);//內文粗體
    	bFonts= new Font(bfChinese, 9, Font.BOLD);//內文粗體
    	nFont1 = new Font(bfChinese, 2, Font.NORMAL);
    	nFont1.setColor(Color.WHITE);    	

		bFontWhite = new Font(bfChinese, 10, Font.BOLD);
		bFontWhite.setColor(Color.WHITE);
		
		bFontWhiteBo = new Font(bfChinese, 9, Font.BOLD);
		bFontWhiteBo.setColor(Color.WHITE);
		
		PdfPTable tbHeader = buildTable(new int[] {28, 28, 28});
        PdfPCell blank = buildBlankCell(18, 0);
		
        Image img = Image.getInstance(nowpath + "//logo2.png");
        img.setWidthPercentage(70);
        
        
        PdfPCell cLogo = new PdfPCell();
        cLogo.addElement(img);
        cLogo.setBorder(0);
        
        tbHeader.addCell(cLogo);
        tbHeader.addCell(buildTitle("客戶風險屬性評估問卷", bFont16, Paragraph.ALIGN_CENTER));
        tbHeader.addCell(blank);
		
        tbHeader.addCell(blank); //空白TABLE
        tbHeader.addCell(blank);
        tbHeader.addCell(blank);

        
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        String evaluationFormat = formatter.format(kyc311.getBasic_information().get("CREATETIME"));

      
        String getMonth = "";
		String getDate = "";
		java.sql.Timestamp ts = (java.sql.Timestamp) kyc311.getBasic_information().get("CREATETIME");
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
		
		ts = (java.sql.Timestamp) kyc311.getBasic_information().get("EXPIRY_DATE");
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
		tbHeader.addCell(buildCell(kyc311.getSEQ(), nFont, Paragraph.ALIGN_LEFT, -1, 0));
		
        document.add(tbHeader);
        
        document.add(partOne(kyc311));
        document.add(partTwo(kyc311));
        document.add(partThree(kyc311));
        document.add(buildRemindTable());
        document.add(buildRemindTable2());
        document.add(buildRemindTable3(kyc311, type));
        if("1".equals(type)){
        	document.add(buildSignTable());
     		document.add(buildNoteTable(kyc311, type));
        }
//        document.add(endTable(kyc311, type));
        
		return document;
	}

    private PdfPTable partOne(KYC311InputVO kyc311) throws Exception {		
    	DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF qc = null;
		StringBuilder sb = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        long BRITHDAY = Long.parseLong(kyc311.getBirthday());
        cust_id = kyc311.getCUST_ID();
    	
        //取得客戶主檔生日
        String birthdayCustMast = "";
        if(StringUtils.isNotEmpty(cust_id)) {
			qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuilder();
			sb.append(" SELECT TO_CHAR(TRUNC(BIRTH_DATE), 'yyyy-MM-dd') as BIRTHDAY FROM TbCRM_CUST_MAST WHERE CUST_ID = :cust_id");
			qc.setObject("cust_id", cust_id);
			qc.setQueryString(sb.toString());
			List<Map<String , Object>> clist = dam.exeQueryWithoutSort(qc);
			
			if(CollectionUtils.isNotEmpty(clist) && clist.get(0).get("BIRTHDAY") != null && StringUtils.isNotEmpty(clist.get(0).get("BIRTHDAY").toString())) {
				birthdayCustMast = clist.get(0).get("BIRTHDAY").toString();
			}
        }
        
    	PdfPTable tbPart1 = buildTable(new int[] {84});
        PdfPCell cPart1 = buildSection("第一部分 : 個人基本資料", bFontWhite, Paragraph.ALIGN_CENTER);//設定欄位內容
        cPart1.setBackgroundColor(new Color(55, 169, 203));
        tbPart1.addCell(cPart1);
        
        PdfPTable tbPart1Sub = buildTable(new int[]{13,74});
        tbPart1Sub.addCell(buildCellWithBorder("姓名", bFont, Paragraph.ALIGN_CENTER));
        tbPart1Sub.addCell(buildCellhardWord(kyc311.getCust_name() , nFont , nFont_a , Paragraph.ALIGN_CENTER));
		cPart1  = new PdfPCell(tbPart1Sub);
		tbPart1.addCell(cPart1);
		
        PdfPTable tbPart2Sub = buildTable(new int[]{15,15,10,10,10,10,15,15});
        tbPart2Sub.addCell(buildCellWithBorder("身分統一編號", bFont, Paragraph.ALIGN_CENTER));
        tbPart2Sub.addCell(buildCellWithBorder(kyc311.getCUST_ID(), nFont , Paragraph.ALIGN_CENTER));
        tbPart2Sub.addCell(buildCellWithBorder("出生日期" , bFont , Paragraph.ALIGN_CENTER));
        //若客戶主檔有生日資料則抓客戶主檔資料
      	if(StringUtils.isNotEmpty(birthdayCustMast))
      		tbPart2Sub.addCell(buildCellWithBorder(birthdayCustMast, nFont , Paragraph.ALIGN_CENTER));
      	else
      		tbPart2Sub.addCell(buildCellWithBorder(sdf.format(BRITHDAY), nFont , Paragraph.ALIGN_CENTER));
        
        tbPart2Sub.addCell(buildCellWithBorder("性別" , bFont , Paragraph.ALIGN_CENTER));
//		if (kyc311.getGENDER().equals("男")){
		if (kyc311.getGENDER().equals("1")){
			tbPart2Sub.addCell(buildCellWithBorder("男" , nFont , Paragraph.ALIGN_CENTER));
//		}else if(kyc311.getGENDER().equals("女")){
		}else if(kyc311.getGENDER().equals("2")){
			tbPart2Sub.addCell(buildCellWithBorder("女" , nFont , Paragraph.ALIGN_CENTER));
		}else {
			tbPart2Sub.addCell(buildCellWithBorder(" " , nFont , Paragraph.ALIGN_CENTER));
		}
        tbPart2Sub.addCell(buildCellWithBorder("教育程度" , bFont , Paragraph.ALIGN_CENTER));
		XmlInfo xmlInfo_EDUCATION = new XmlInfo();
		Hashtable EDUCATIONLIST_F3 =  xmlInfo_EDUCATION.getVariable("KYC.EDUCATION", "F3");
		String EDUCATION ="";
		if (EDUCATIONLIST_F3.get(kyc311.getEDUCATION()) != null){
			EDUCATION = "("+kyc311.getEDUCATION_Item()+")"+EDUCATIONLIST_F3.get(kyc311.getEDUCATION()).toString();
		}else{
			EDUCATION = "";
		}
        tbPart2Sub.addCell(buildCellWithBorder(EDUCATION , nFont , Paragraph.ALIGN_CENTER));
		cPart1  = new PdfPCell(tbPart2Sub);
		tbPart1.addCell(cPart1);
		
        PdfPTable tbPart3Sub = buildTable(new int[]{15,35,10,10,15,15});
        XmlInfo xmlInfo_CAREER = new XmlInfo();
		String CAREER = "("+(String) xmlInfo_CAREER.getVariable("KYC.CAREER", kyc311.getCAREER(), "F2")+")"+(String) xmlInfo_CAREER.getVariable("KYC.CAREER", kyc311.getCAREER(), "F3");
		
		XmlInfo xmlInfo_MARRAGE = new XmlInfo();
		String MARRAGE = "("+(String) xmlInfo_MARRAGE.getVariable("KYC.MARRAGE", kyc311.getMARRAGE(), "F2")+")"+(String) xmlInfo_MARRAGE.getVariable("KYC.MARRAGE", kyc311.getMARRAGE(), "F3");
		
		XmlInfo xmlInfo_CHILD = new XmlInfo();
		String CHILD_NO = "("+kyc311.getCHILD_NO_Item()+")"+(String) xmlInfo_CHILD.getVariable("KYC.CHILD_NO", kyc311.getCHILD_NO(), "F3");
		
		XmlInfo xmlInfo_SICK = new XmlInfo();
		String SICK_TYPE = "("+(String) xmlInfo_CHILD.getVariable("KYC.HEALTH_FLAG", kyc311.getSICK_TYPE(), "F2")+")"+(String) xmlInfo_CHILD.getVariable("KYC.HEALTH_FLAG", kyc311.getSICK_TYPE(), "F3");
		
        tbPart3Sub.addCell(buildCellWithBorder("職業", bFont, Paragraph.ALIGN_CENTER));
        tbPart3Sub.addCell(buildCellWithBorder(CAREER , nFont , Paragraph.ALIGN_CENTER));
        tbPart3Sub.addCell(buildCellWithBorder("婚姻" , bFont , Paragraph.ALIGN_CENTER));
        tbPart3Sub.addCell(buildCellWithBorder(MARRAGE , nFont , Paragraph.ALIGN_CENTER));
        tbPart3Sub.addCell(buildCellWithBorder("子女人數" , bFont , Paragraph.ALIGN_CENTER));
        tbPart3Sub.addCell(buildCellWithBorder(CHILD_NO , nFont , Paragraph.ALIGN_CENTER));
		cPart1  = new PdfPCell(tbPart3Sub);
		tbPart1.addCell(cPart1);
		
        PdfPTable tbPart4Sub = buildTable(new int[]{15,35,20,30});
        String telphone="";
		if (kyc311.getDAY() != null ){
			telphone =  "(日)"+kyc311.getDAY()+"\n";
		}
		if (kyc311.getNIGHT() != null ){
			telphone = telphone+"(夜)"+kyc311.getNIGHT()+"\n";
		}
		if (kyc311.getTEL_NO() != null ){
			telphone = telphone+"(行動)"+kyc311.getTEL_NO()+"\n";
		}
		if (kyc311.getFAX() != null ){
			telphone = telphone+"(傳真)"+kyc311.getFAX();
		}
		PdfPCell middleCell;
		middleCell = buildCellWithBorder_middle("聯絡電話", bFont, Paragraph.ALIGN_CENTER);
		middleCell.setVerticalAlignment(Paragraph.ALIGN_MIDDLE);
        tbPart4Sub.addCell(middleCell);
        
        middleCell = buildCellWithBorder_middle(telphone, nFont , Paragraph.ALIGN_LEFT);
		middleCell.setVerticalAlignment(Paragraph.ALIGN_MIDDLE);
        tbPart4Sub.addCell(middleCell);
        
        middleCell = buildCellWithBorder_middle("電子郵件" , bFont , Paragraph.ALIGN_CENTER);
		middleCell.setVerticalAlignment(Paragraph.ALIGN_MIDDLE);
        tbPart4Sub.addCell(middleCell);
        
        middleCell = buildCellWithBorder_middle(kyc311.getEMAIL_ADDR() , nFont , Paragraph.ALIGN_LEFT);
		middleCell.setVerticalAlignment(Paragraph.ALIGN_MIDDLE);
        tbPart4Sub.addCell(middleCell);
        
		cPart1  = new PdfPCell(tbPart4Sub);
		tbPart1.addCell(cPart1);
		
        PdfPTable tbPart5Sub = buildTable(new int[]{15,35,20,30});
        
        middleCell = buildCellWithBorder_middle("通訊地址", bFont, Paragraph.ALIGN_CENTER);
		middleCell.setVerticalAlignment(Paragraph.ALIGN_MIDDLE);
        tbPart5Sub.addCell(middleCell);
        
        middleCell = buildCellWithBorder_middle(kyc311.getCUST_ADDR_1() , nFont , Paragraph.ALIGN_LEFT);
		middleCell.setVerticalAlignment(Paragraph.ALIGN_MIDDLE);
        tbPart5Sub.addCell(middleCell);
        
        middleCell = buildCellWithBorder_middle("領有全民健康保險重大傷病證明" , bFont , Paragraph.ALIGN_CENTER);
		middleCell.setVerticalAlignment(Paragraph.ALIGN_MIDDLE);
        tbPart5Sub.addCell(middleCell);
        
        middleCell = buildCellWithBorder_middle(SICK_TYPE, nFont , Paragraph.ALIGN_LEFT);
		middleCell.setVerticalAlignment(Paragraph.ALIGN_MIDDLE);
        tbPart5Sub.addCell(middleCell);
        
		cPart1  = new PdfPCell(tbPart5Sub);
		tbPart1.addCell(cPart1);

		return tbPart1;
    }
    
    private PdfPTable partTwo(KYC311InputVO kyc311) throws Exception {
        PdfPTable tbPart2 = buildTable(new int[] {84});
        PdfPCell cPart2 = buildSection("第二部分 : 財務背景、投資經驗、投資需求與風險偏好", bFontWhite, Paragraph.ALIGN_CENTER);
        cPart2.setBackgroundColor(new Color(55, 169, 203));
        tbPart2.addCell(cPart2);

        PdfPTable tbPart1Sub = buildTable(new int[]{70,30});
        int j = 1;
        String answerAll = "";
        for(Map<String,Object> questionDesc:kyc311.getQuestionList()){
        	Object ansList = questionDesc.get("ANSWER_LIST");
        	List<Map<String, Object>> ansList_change = (List<Map<String, Object>>) ansList;
        	
        	if(j == 3) {
        		//第3題選的答案
        		StringBuffer Q3Ans = new StringBuffer();
        		for(Map<String, Object> map1:ansList_change){
	        		if(map1.get("select") != null && map1.get("select").toString()=="true"){
	        			Q3Ans.append(map1.get("ANSWER_SEQ").toString()+";");
	        		}
	        	}
        		//第3題特殊處理
        		PdfPTable cell3Table = buildTable(new int[]{100});
        		cell3Table.getDefaultCell().setBorderWidth(0);
        		cell3Table.addCell(buildCellQ3(j+"、"+questionDesc.get("QUESTION_DESC").toString(), nFont, Paragraph.ALIGN_LEFT, -1, 0));
        		cell3Table.addCell(getQ3Ans(Q3Ans.toString()));
        		
        		tbPart1Sub.getDefaultCell().setColspan(2);
        		tbPart1Sub.addCell(cell3Table);
        	} else {
        		tbPart1Sub.getDefaultCell().setColspan(1);
	        	tbPart1Sub.addCell(buildCellWithBorder(j+"、"+questionDesc.get("QUESTION_DESC").toString(), nFont, Paragraph.ALIGN_LEFT));
	        	
	        	StringBuffer answer = new StringBuffer(); //取答案
	        	for(Map<String, Object> map1:ansList_change){
	        		if(map1.get("select") != null && map1.get("select").toString()=="true"){
	        			answer.append(map1.get("ANSWER_DESC").toString()+";");
	        		}
	        	}
	        	if (answer.substring(answer.length()-1).equals(";")== true){
	        		answerAll = answer.substring(0,answer.length()-1);
	        	}
	        	tbPart1Sub.addCell(buildCellWithBorder(answerAll, nFont, Paragraph.ALIGN_LEFT));
        	}
        	
    		j++;
        }
    	
		cPart2  = new PdfPCell(tbPart1Sub);
		tbPart2.addCell(cPart2);
        
		return tbPart2;

    }

    private PdfPTable getQ3Ans(String ansStr) throws Exception {
    	XMLInfo xmlinfo = new XMLInfo();
    	List<Map<String, Object>> prodExpList = xmlinfo.getXMLInfo("KYC.Q3_PROD_EXP");
    	List<Map<String, Object>> prodTypeList = xmlinfo.getXMLInfo("KYC.Q3_PROD_TYPE");
    	
    	//第3題特殊處理
    	PdfPTable bigtable = buildTable(new int[]{5,90,5});
    	bigtable.getDefaultCell().setBorderWidth(0);
    	bigtable.addCell(buildCellQ3("  ", nFont, Paragraph.ALIGN_CENTER, 12, 0));
    	
    	PdfPTable q3table = buildTable(new int[]{8,8,8,8,8,8});
    	for(int i = -1; i < prodTypeList.size(); i++) {
    		for(int j = -1; j < prodExpList.size(); j++) {
    			if(i == -1 && j == -1) {
    				q3table.addCell(setMiddle(buildCellQ3("", nFont, Paragraph.ALIGN_CENTER, 12, (float)0.1)));
    			} else if(i == -1 && j > -1) {
    				q3table.addCell(setMiddle(buildCellQ3(prodExpList.get(j).get("PARAM_NAME").toString(), nFont, Paragraph.ALIGN_CENTER, 12, (float)0.1)));
    			} else if(i > -1 && j == -1) {
    				q3table.addCell(setMiddle(buildCellQ3(prodTypeList.get(i).get("PARAM_NAME").toString(), nFont, Paragraph.ALIGN_CENTER, 12, (float)0.1)));
    			} else  if(i > -1 && j > -1) {
    				String ansSeq = ObjectUtils.toString(i+1) + ObjectUtils.toString(j+1);
    				if(ansStr.indexOf(ansSeq) >= 0) {
    					q3table.addCell(setMiddle(buildCellQ3("■", nFont, Paragraph.ALIGN_CENTER, 12, (float)0.1)));
    				} else {
    					q3table.addCell(setMiddle(buildCellQ3("□", nFont, Paragraph.ALIGN_CENTER, 12, (float)0.1)));
    				}
    			}
    		}
    	}
    	bigtable.addCell(q3table);
    	bigtable.addCell(buildCellQ3("  ", nFont, Paragraph.ALIGN_CENTER, 12, 0));
    	
		return bigtable;
    }
    
    private PdfPTable partThree(KYC311InputVO kyc311) throws Exception {
    	
    	PdfPTable tbPart3 = buildTable(new int[] {84});
        PdfPCell cPart3 = buildSection("第三部分 : 風險屬性評估結果", bFontWhite, Paragraph.ALIGN_CENTER);
        cPart3.setBackgroundColor(new Color(55, 169, 203));
        tbPart3.addCell(cPart3);
        
        XmlInfo xmlInfo = new XmlInfo();
        String custRiskAfr = (String) xmlInfo.getVariable("KYC.03_RESULT_DESC", kyc311.getDown_risk_level(), "F3");
        
        cPart3 = buildSection("您屬於"+kyc311.getDown_risk_level()+"等級的"+kyc311.getRISK_TYPE()+"客戶。"+custRiskAfr, nFonts, Paragraph.ALIGN_LEFT);
        tbPart3.addCell(cPart3);
        
    	PdfPCell blank = buildBlankCell(18, 0);
    	PdfPTable tbText9 = buildTable(new int[] {20,20,20,20,20});
        tbText9.addCell(buildCell("可承作投資型\n金融商品風險等級 ", nFonts, Paragraph.ALIGN_CENTER,-1,-1));
        PdfPCell tbText9g = buildCell("P1等級\n（低度風險）", bFontWhiteBo, Paragraph.ALIGN_CENTER,-1,-1);
        tbText9g.setBackgroundColor(new Color(0,175,0));
        tbText9.addCell(tbText9g);
        PdfPCell tbText9b = buildCell("P2等級\n（一般風險）", bFontWhiteBo, Paragraph.ALIGN_CENTER,-1,-1);
        tbText9b.setBackgroundColor(new Color(0,102,255));
        tbText9.addCell(tbText9b);
        PdfPCell tbText9o = buildCell("P3等級\n（中度風險）", bFontWhiteBo, Paragraph.ALIGN_CENTER,-1,-1);
        tbText9o.setBackgroundColor(new Color(255,153,0));
        tbText9.addCell(tbText9o);
        PdfPCell tbText9r = buildCell("P4等級\n（高度風險）", bFontWhiteBo, Paragraph.ALIGN_CENTER,-1,-1);
        tbText9r.setBackgroundColor(new Color(255,0,0));
        tbText9.addCell(tbText9r);
        cPart3  = new PdfPCell(tbText9);
        tbPart3.addCell(cPart3);
        
		for(int i =0; i < kyc311.getRISKList().size(); i++){
			PdfPCell tbText9blb = buildCell(kyc311.getRISKList().get(i).get("RISKLIST"), bFontWhiteBo, Paragraph.ALIGN_CENTER,-1,-1);
	    	tbText9blb.setBackgroundColor(Color.BLACK);
			switch(kyc311.getRISKList().get(i).get("CUST_RL_ID").toString()){
				case "C1":
					if (kyc311.getRISKList().get(i).get("CUST_RL_ID").equals(kyc311.getDown_risk_level()) == true){
						tbText9.addCell(tbText9blb);
					}else{
						tbText9.addCell(buildCell(kyc311.getRISKList().get(i).get("RISKLIST"), nFonts, Paragraph.ALIGN_CENTER,-1,-1));
					}
			        tbText9.addCell(buildCell("V", nFonts, Paragraph.ALIGN_CENTER,-1,-1));
			        tbText9.addCell(buildCell("不適合", nFonts, Paragraph.ALIGN_CENTER,-1,-1));
			        tbText9.addCell(buildCell("不適合", nFonts, Paragraph.ALIGN_CENTER,-1,-1));
			        tbText9.addCell(buildCell("不適合", nFonts, Paragraph.ALIGN_CENTER,-1,-1));
//			        tbText9.addCell(blank);
//			        cPart3  = new PdfPCell(tbText9);
//			        tbPart3.addCell(cPart3);
			        continue;
				case "C2":
//					tbText9.addCell(buildCell(blank, nFont, Paragraph.ALIGN_CENTER,-1,0));
					if (kyc311.getRISKList().get(i).get("CUST_RL_ID").equals(kyc311.getDown_risk_level()) == true){
						tbText9.addCell(tbText9blb);
					}else{
						tbText9.addCell(buildCell(kyc311.getRISKList().get(i).get("RISKLIST"), nFonts, Paragraph.ALIGN_CENTER,-1,-1));
					}
			        tbText9.addCell(buildCell("V", nFonts, Paragraph.ALIGN_CENTER,-1,-1));
			        tbText9.addCell(buildCell("V", nFonts, Paragraph.ALIGN_CENTER,-1,-1));
			        tbText9.addCell(buildCell("不適合", nFonts, Paragraph.ALIGN_CENTER,-1,-1));
			        tbText9.addCell(buildCell("不適合", nFonts, Paragraph.ALIGN_CENTER,-1,-1));
//			        tbText9.addCell(blank);
//			        cPart3  = new PdfPCell(tbText9);
//			        tbPart3.addCell(cPart3);
			        continue;
				case "C3":
//					tbText9.addCell(buildCell(blank, nFont, Paragraph.ALIGN_CENTER,-1,0));
					if (kyc311.getRISKList().get(i).get("CUST_RL_ID").equals(kyc311.getDown_risk_level()) == true){
						tbText9.addCell(tbText9blb);
					}else{
						tbText9.addCell(buildCell(kyc311.getRISKList().get(i).get("RISKLIST"), nFonts, Paragraph.ALIGN_CENTER,-1,-1));
					}
			        tbText9.addCell(buildCell("V", nFonts, Paragraph.ALIGN_CENTER,-1,-1));
			        tbText9.addCell(buildCell("V", nFonts, Paragraph.ALIGN_CENTER,-1,-1));
			        tbText9.addCell(buildCell("V", nFonts, Paragraph.ALIGN_CENTER,-1,-1));
			        tbText9.addCell(buildCell("不適合", nFonts, Paragraph.ALIGN_CENTER,-1,-1));
//			        tbText9.addCell(blank);
//			        cPart3  = new PdfPCell(tbText9);
//			        tbPart3.addCell(cPart3);
			        continue;
				case "C4":
//					tbText9.addCell(buildCell(blank, nFont, Paragraph.ALIGN_CENTER,-1,0));
					if (kyc311.getRISKList().get(i).get("CUST_RL_ID").equals(kyc311.getDown_risk_level()) == true){
						tbText9.addCell(tbText9blb);
					}else{
						tbText9.addCell(buildCell(kyc311.getRISKList().get(i).get("RISKLIST"), nFonts, Paragraph.ALIGN_CENTER,-1,-1));
					}	        
			        tbText9.addCell(buildCell("V", nFonts, Paragraph.ALIGN_CENTER,-1,-1));
			        tbText9.addCell(buildCell("V", nFonts, Paragraph.ALIGN_CENTER,-1,-1));
			        tbText9.addCell(buildCell("V", nFonts, Paragraph.ALIGN_CENTER,-1,-1));
			        tbText9.addCell(buildCell("V", nFonts, Paragraph.ALIGN_CENTER,-1,-1));
//			        tbText9.addCell(blank);
			        
			        continue;
				case "C5":
//					tbText9.addCell(buildCell(blank, nFont, Paragraph.ALIGN_CENTER,-1,0));
					if (kyc311.getRISKList().get(i).get("CUST_RL_ID").equals(kyc311.getDown_risk_level()) == true){
						tbText9.addCell(tbText9blb);
					}else{
						tbText9.addCell(buildCell(kyc311.getRISKList().get(i).get("RISKLIST"), nFonts, Paragraph.ALIGN_CENTER,-1,-1));
					}	        
			        tbText9.addCell(buildCell("V", nFonts, Paragraph.ALIGN_CENTER,-1,-1));
			        tbText9.addCell(buildCell("V", nFonts, Paragraph.ALIGN_CENTER,-1,-1));
			        tbText9.addCell(buildCell("V", nFonts, Paragraph.ALIGN_CENTER,-1,-1));
			        tbText9.addCell(buildCell("V", nFonts, Paragraph.ALIGN_CENTER,-1,-1));
//			        tbText9.addCell(blank);
			        
			        continue;
			}
			cPart3  = new PdfPCell(tbText9);
	        tbPart3.addCell(cPart3);
		}
		return tbPart3;
    }
    
    private PdfPTable buildRemindTable() throws Exception {
    	PdfPTable table = buildTable(new int[] {84});
    	StringBuffer sb = new StringBuffer();
    	sb.append("\n約定事項：\n")
    		.append("1.您所勾選／填寫之問卷內容及分析結果，僅供本行作為日後進行交易時之商品適合度評估依據。本行不保證／承諾最後「投資之結果」\n")
    		.append("  、「投資本金之不虧損」或「投資獲利」，提醒您務必瞭解並確認。\n")
    		.append("2.客戶風險屬性分析結果有效期限為一年。本行於每筆下單交易前，將以當時您最後有效之分析結果作為商品適合度評估依據，但具定期\n")
    		.append("  重覆投資性質之交易（例如：基金定期定額／不定額投資），不在此限。惟當您的風險屬性分析結果變更，導致原定期重覆投資性質交\n")
    		.append("　易之商品適合度評估未能符合時，依規定僅可依原約定條件繼續扣款，不得增加扣款金額、扣款次數及停扣後恢復扣款，並請您自行評\n")
    		.append("　估是否依原約定條件持續扣款。\n")
    		.append("3.委託人如未滿18歲，請依本行「未成年人開戶／往來事項申請法定代理人同意書」之約定辦理。\n")
    		.append("4.客戶風險屬性評估之後續定期檢視，可透過本行臨櫃、網路銀行、行動銀行或奈米投平台方式辦理。\n")
    		.append("5.為維護您的權益，您所填寫之問卷內容，本行可能會以電話方式照會確認。\n")
			.append("6.如您就問卷內容或您風險屬性評估結果有任何疑問，請洽您的客戶關係經理或本行客服專線：(02)8751-6665。");
    	
    	table.addCell(buildCell(sb.toString(), bFonts, Paragraph.ALIGN_LEFT, -1, 0));
	    return table;
	}
    
    private PdfPTable buildRemindTable2() throws Exception {
    	PdfPTable table = buildTable(new int[] {84});
    	StringBuffer sb = new StringBuffer();
    	sb.append("\n\n")
    		.append("茲確認下列事項：\n\n")
    		.append("1.以上問卷內容及分析結果經本人／法定代理人／監護人／輔助人確認無誤，並已審閱完畢。\n\n")
    		.append("2.本人／法定代理人／監護人／輔助人已充分了解本問卷之內容及約定事項。\n\n")
    		.append("3.本人／法定代理人／監護人／輔助人確認已收執本問卷分析結果副本乙份【與本行正本內容無誤】。");
    	table.addCell(buildCell(sb.toString(), bFonts, Paragraph.ALIGN_LEFT, -1, 0));
	    return table;
	}
    
    private PdfPTable buildRemindTable3(KYC311InputVO kyc311, String type) throws Exception {
    	PdfPTable table = buildTable(new int[] {84});
    	StringBuffer sb = new StringBuffer();
    	XmlInfo xmlInfo = new XmlInfo();    	
    	//教育程度
    	String EDUCATION = (String)xmlInfo.getVariable("KYC.EDUCATION", kyc311.getEDUCATION(), "F3");
    	//學校
    	String SCHOOL = kyc311.getSCHOOL();    	
    	//15歲以下選擇原因
    	String EDU_CHOOSE = (String)xmlInfo.getVariable("KYC.EDU_CHOOSE", kyc311.getEDU_CHANGE(), "F3");    	
    	//18歲以上變更原因
    	String EDU_CHANGE = (String)xmlInfo.getVariable("KYC.EDU_CHANGE", kyc311.getEDU_CHANGE(), "F3");    	
    	//重大傷病由有轉無    		
    	String HEALTH_CHANGE = (String)xmlInfo.getVariable("KYC.HEALTH_CHANGE", kyc311.getHEALTH_CHANGE(), "F3");    	
    	//取得上一次教育程度
    	String CUST_EDUCTION_BEFORE = (String)kyc311.getBasic_information().get("CUST_EDUCTION_BEFORE"); 
    	String EDUCTION_BEFORE = (String)xmlInfo.getVariable("KYC.EDUCATION", CUST_EDUCTION_BEFORE, "F3");
    	//取得上一次重大傷病類型
    	String CUST_HEALTH_BEFORE = (String)kyc311.getBasic_information().get("CUST_HEALTH_BEFORE"); 
    	//現在重大傷病類型
    	String SICK_TYPE =  kyc311.getSICK_TYPE();
    	//取得年齡
    	long birthDate = new BigDecimal(ObjectUtils.toString(kyc311.getBasic_information().get("birthday"))).longValue();
    	Integer age = getAge(birthDate);
    	//前一次信箱
    	String EMAIL_BEFORE = kyc311.getCUST_EMAIL_BEFORE();
    	//本次輸入信箱
    	String EMAIL_AFTER = kyc311.getEMAIL_ADDR();
    	//信箱重覆原因
    	String SAMEEMAIL_CHOOSE = (String)xmlInfo.getVariable("KYC.EMAIL_CHANGE", kyc311.getSAMEEMAIL_CHOOSE(), "F3");   
    	//信箱重覆理由
    	String SAMEEMAIL_REASON = kyc311.getSAMEEMAIL_REASON();
    	
    	if(age<=15){   		
    		if(StringUtils.isNotEmpty(SCHOOL)){
    			sb.append("\n\n您的教育程度為【" + EDUCATION + "】，學校名稱【" + SCHOOL + "】，原因為【" + EDU_CHOOSE + "】！");
    		}
    		if("2".equals(CUST_HEALTH_BEFORE)){
    			if("1".equals(SICK_TYPE)){
        			sb.append("\n\n您是否領有全民健康保險重大傷病證明從【有】異動為【無】，變更原因為【" + HEALTH_CHANGE + "】！");
        		}
    		}    		
    	}
    	else if(age>=18){
    		if(StringUtils.isNotEmpty(SCHOOL)){
    			sb.append("\n\n您的教育程度從【" + EDUCTION_BEFORE + "】異動為【" + EDUCATION + "】，學校名稱【" + SCHOOL + "】，原因為【" + EDU_CHANGE + "】！");
    		}
    		if("2".equals(CUST_HEALTH_BEFORE)){
    			if("1".equals(SICK_TYPE)){
        			sb.append("\n\n您是否領有全民健康保險重大傷病證明從【有】異動為【無】，變更原因為【" + HEALTH_CHANGE + "】！");
        		}
    		}    		
    	}
    	else{
    		if("2".equals(CUST_HEALTH_BEFORE)){
    			if("1".equals(SICK_TYPE)){
        			sb.append("\n\n您是否領有全民健康保險重大傷病證明從【有】異動為【無】，變更原因為【" + HEALTH_CHANGE + "】！");
        		}
    		}    
    	}
    	KYC311 kyccooling = (KYC311) PlatformContext.getBean("kyc311");
		kyccooling.getCoolingPeriodRiskVal(kyc311);
		
    	//若本次評估結果較上次結果為高，顯示訊息
    	if(StringUtils.isNotBlank(kyc311.getCUST_RISK_BEF())) {
    		try {
    			int riskBef = Integer.parseInt(kyc311.getCUST_RISK_BEF().substring(1));
    			int riskAfr = Integer.parseInt(kyc311.getCUST_RISK_AFR().substring(1));
    			if(riskAfr > riskBef) {
    				if(kyc311.isCOOLING()){
	        			sb.append("\n\n1.您本次的風險屬性評估結果為" + kyc311.getCUST_RISK_AFR() + "等級(" + kyc311.getRISK_TYPE() + ")，較上次評估結果")
	        			  .append(kyc311.getCUST_RISK_BEF() + "等級(" + kyc311.getRISK_TYPE_BEF() + ")提高，為維護您的權益，測試結果將自今日起算二日後生效，請您再次確認所填問卷內容的")
	        			  .append("正確性。\n\n")
	        	    	  .append("本次評估結果係您依您的實際情況自主填寫，未受本行任何人員影響。");
    				}else{
    					sb.append("\n\n1.您本次的風險屬性評估結果為" + kyc311.getCUST_RISK_AFR() + "等級(" + kyc311.getRISK_TYPE() + ")，較上次評估結果")
	        			  .append(kyc311.getCUST_RISK_BEF() + "等級(" + kyc311.getRISK_TYPE_BEF() + ")提高，為維護您的權益，請您再次確認所填問卷內容的")
	        			  .append("正確性。\n\n")
	        	    	  .append("本次評估結果係您依您的實際情況自主填寫，未受本行任何人員影響。");
    				}
        		}
    		} catch(Exception e) {	
    			
    		}    		
    	}
    	
		if (StringUtils.isNotBlank(kyc311.getPDF_KYC_FLAG())) {
			if("YTX".equals(kyc311.getPDF_KYC_FLAG())) {
				//客戶為專投或高資產客戶且為弱勢客戶且具免降等註記且為C4
				String expiry_date = StringUtils.isNotBlank(kyc311.getEXPIRY_DATE()) ? kyc311.getEXPIRY_DATE() : "無";
				sb.append("\n\n您已申請免適用客戶風險屬性評估結果控管機制，該機制所定風險屬性等級期限至您當時專業投資人資格或高資產客戶資格有效為止(惟最長不超過1年)，故本次風險屬性評估結果到期日為：" + expiry_date);
			} else if("Y".equals(kyc311.getPDF_KYC_FLAG())) {
				//客戶為非專投或高資產客戶且為弱勢客戶且具免降等註記且為C3~C4
				String expiry_date = StringUtils.isNotBlank(kyc311.getEXPIRY_DATE()) ? kyc311.getEXPIRY_DATE() : "無";
				sb.append("\n\n您已申請免適用客戶風險屬性評估結果控管機制，該機制所定風險屬性等級期限至您的免適用客戶風險屬性評估結果資格有效為止，故本次風險屬性評估結果到期日為：" + expiry_date);
			}
		}
		if(StringUtils.isBlank(EMAIL_BEFORE)) EMAIL_BEFORE = "無";
    	if(StringUtils.isBlank(EMAIL_AFTER)) EMAIL_AFTER = "無";
		String REASON_SHOW = SAMEEMAIL_REASON;
		if(StringUtils.isNotBlank(SAMEEMAIL_CHOOSE) && SAMEEMAIL_CHOOSE.indexOf("其他") < 0){
			REASON_SHOW = SAMEEMAIL_CHOOSE;
		}

		if(StringUtils.equals(EMAIL_BEFORE, EMAIL_AFTER)){
			sb.append("\n\n您留存的電子郵件為：【" + EMAIL_AFTER + "】，請再次確認留存資料正確性。");
		}else if(!StringUtils.equals(EMAIL_BEFORE, EMAIL_AFTER) && StringUtils.isNotBlank(SAMEEMAIL_CHOOSE)){
			sb.append("\n\n您留存的電子郵件由【" + EMAIL_BEFORE + "】更新為【" + EMAIL_AFTER +
					"】，本行已發送確認連結至您新留存信箱，本次變更將於點選確認連結後生效，請再次確認留存資料正確性。");
			sb.append("\n\n經比對，您留存的電子郵件與本行其他客戶有重覆情形，原因為：【" +  REASON_SHOW + "】，請再次確認。");
		}else if(!StringUtils.equals(EMAIL_BEFORE, EMAIL_AFTER) && !StringUtils.isNotBlank(SAMEEMAIL_CHOOSE)){
			if(StringUtils.equals("無", EMAIL_AFTER)){
				sb.append("\n\n您留存的電子郵件由【" + EMAIL_BEFORE + "】更新為【" + EMAIL_AFTER +
						"】，請再次確認留存資料正確性。");
			}else {
				sb.append("\n\n您留存的電子郵件由【" + EMAIL_BEFORE + "】更新為【" + EMAIL_AFTER +
						"】，本行已發送確認連結至您新留存信箱，本次變更將於點選確認連結後生效，請再次確認留存資料正確性。");
			}
		}
    	//有內容時增加標題
    	if(sb.length()>0){
    		sb.insert(0, "\n\n另確認以下項目：");
    	}
    	table.addCell(buildCell(sb.toString(), bFonts, Paragraph.ALIGN_LEFT, -1, 0));
    	return table;
    }
    
    //add bt Brian
    private Integer getAge(Object day) {
        if (day == null) 
         return null;
        
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        Calendar calendar2 = new GregorianCalendar();
        calendar2.setTime(new Date((Long)day));
        
        int year1 = calendar.get(Calendar.YEAR);
        int year2 = calendar2.get(Calendar.YEAR);
        int age = year1 - year2;
        int month1 = calendar.get(Calendar.MONTH);
        int month2 = calendar2.get(Calendar.MONTH);
        if (month2 > month1) {
         age--;
        } else if (month1 == month2) {
         int day1 = calendar.get(Calendar.DAY_OF_MONTH);
         int day2 = calendar2.get(Calendar.DAY_OF_MONTH);
         if (day2 > day1)
          age--;
        }
        return age;
       }
    
    private PdfPTable buildSignTable() throws Exception {
		PdfPTable table = buildTable(new int[] { 84 });
		Paragraph text = new Paragraph();

		PdfPCell cell = buildBlankCell(50, 0);
		table.addCell(cell);
		
		cell = buildBlankCell(18, 0);
		text.add(new Chunk("客戶姓名 : ", bFont));
		text.add(new Chunk("__________________________  ", bFont));
		text.add(new Chunk("(本人親簽，或請擇一蓋用本行任一帳戶原留印鑑或「債票券客戶資料表」所留存之", bFont));
		text.setAlignment(Paragraph.ALIGN_LEFT);
		cell.addElement(text);
		table.addCell(cell);

		cell = buildBlankCell(18, 0);
		text = new Paragraph();
		text.add(new Chunk("立約印鑑或簽章樣式)", bFont));
		text.setAlignment(Paragraph.ALIGN_LEFT);
		cell.addElement(text);
		table.addCell(cell);

		cell = buildBlankCell(18, 0);
		text = new Paragraph();
		text.add(new Chunk("帳號 : □□□□□□□□□□□□", bFont));
		text.setAlignment(Paragraph.ALIGN_LEFT);
		cell.addElement(text);
		table.addCell(cell);

		cell = buildBlankCell(18, 0);
		text = new Paragraph();
		text.add(new Chunk("法定代理人/監護人/", bFont));
		text.setAlignment(Paragraph.ALIGN_LEFT);
		cell.addElement(text);
		table.addCell(cell);

		cell = buildBlankCell(18, 0);
		text = new Paragraph();
		text.add(new Chunk("輔助人: __________________________  ", bFont));
		text.add(new Chunk("(請親簽)  身分證統一編號: __________________________", bFont));
		text.setAlignment(Paragraph.ALIGN_LEFT);
		cell.addElement(text);
		table.addCell(cell);
		
		cell = buildBlankCell(18, 0);
		text = new Paragraph();
		text.add(new Chunk("日期 : 民國       年       月       日", bFont));
		text.setAlignment(Paragraph.ALIGN_LEFT);
		cell.addElement(text);
		table.addCell(cell);
		
		table.addCell(buildBlankCell(18, 0));

		return table;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public PdfPTable buildNoteTable(KYC311InputVO kyc311, String type) throws Exception {
		Map param = null;
		String loginID = (String) getCommonVariable(SystemVariableConsts.LOGINID);
		
		PdfPTable table = buildTable(new int[]{84}, 100);
        PdfPTable inner = buildTable(new int[]{22, 28, 14, 14, 14});
        inner.addCell(buildCell("銀行註記欄", nFont, Paragraph.ALIGN_LEFT, -1, 0));
        inner.addCell(buildBlankCell(18, 0));
        inner.addCell(buildBlankCell(18, 0));
        inner.addCell(buildBlankCell(18, 0));
        inner.addCell(buildBlankCell(18, 0));
        
        String expiry_date = StringUtils.isNotBlank(kyc311.getEXPIRY_DATE()) ? kyc311.getEXPIRY_DATE() : "無";
		String cust_pro_date = StringUtils.isNotBlank(kyc311.getCUST_PRO_DATE()) ? kyc311.getCUST_PRO_DATE() : "無";
		String degrade_date = StringUtils.isNotBlank(kyc311.getDEGRADE_DATE()) ? kyc311.getDEGRADE_DATE() : "無";
		String hnwcDueDate = StringUtils.isNotBlank(kyc311.getHNWC_DUE_DATE()) ? kyc311.getHNWC_DUE_DATE() : "無";
    	
		if (type.equals("1")) {
			String info = "客戶本次風險屬性評估結果到期日：" + expiry_date;
			PdfPCell kycCell = buildCell(info, nFont, Paragraph.ALIGN_LEFT, -1, 0);
			kycCell.setColspan(5);	//合併儲存格
			inner.addCell(kycCell);
			
			String info1 = "專業投資人到期日：" + cust_pro_date + "; 高資產客戶到期日：" + hnwcDueDate + "; 免降等到期日：" + degrade_date;
			PdfPCell kycCell1 = buildCell(info1, nFont, Paragraph.ALIGN_LEFT, -1, 0);
			kycCell1.setColspan(5);	//合併儲存格
			inner.addCell(kycCell1);
		}
        
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
		
		if (list.size()>0){
//			if(list.get(0).get("DEPT_NAME")!= null && StringUtils.isNotBlank(list.get(0).get("DEPT_NAME").toString())){
	        	inner.addCell(buildCell("營業單位： "+list.get(0).get("DEPT_NAME") , nFont, Paragraph.ALIGN_LEFT, -1, 0));
//	        }
		}else{
			inner.addCell(buildCell("營業單位： " , nFont, Paragraph.ALIGN_LEFT, -1, 0));
		}
		
//        inner.addCell(buildCell("營業單位： " + branch_name, nFont, Paragraph.ALIGN_LEFT, -1, 0));
        inner.addCell(buildBlankCell(18, 0));
        inner.addCell(buildBlankCell(18, 0));
        inner.addCell(buildBlankCell(18, 0));
        inner.addCell(buildBlankCell(18, 0));
        
        inner.addCell(buildCell("解說： "+kyc311.getBasic_information().get("COMMENTATOR") + intro, nFont, Paragraph.ALIGN_LEFT, -1, 0));
        inner.addCell(buildCell("對保（核對本人親簽無誤）：", nFont, Paragraph.ALIGN_LEFT, -1, 0));
        inner.addCell(buildCell("驗印：", nFont, Paragraph.ALIGN_LEFT, -1, 0));
        inner.addCell(buildCell("鍵機： " +kyc311.getBasic_information().get("KEYIN").toString(), nFont, Paragraph.ALIGN_LEFT, -1, 0));
        
		sb = new StringBuffer();
		sb.append("SELECT EMP_NAME ");
		sb.append("FROM TBORG_MEMBER where EMP_ID = :empId ");
		sb.append("and '02' = (select STATUS from TBKYC_INVESTOREXAM_M where cust_id = :custId ) ");
		param = new TreeMap();
		param.put("empId", kyc311.getEMP_ID());
		param.put("custId", kyc311.getCUST_ID());		
		list = exeQueryForMap(sb.toString(), param);	
		 
		if (list.size() > 0){
	        inner.addCell(buildCell("覆核主管： " + list.get(0).get("EMP_NAME") , nFont, Paragraph.ALIGN_LEFT, -1, 0));
		}
		else{
			inner.addCell(buildCell("覆核主管： " , nFont, Paragraph.ALIGN_LEFT, -1, 0));
		}
        
        table.addCell(inner);
        return table;
	}
	
	//沒用到了
//	private PdfPTable endTable(KYC311InputVO kyc311, String type) throws Exception {
//		PdfPTable table = buildTable(new int[] { 84 });
//		Paragraph text = new Paragraph();
//		StringBuffer sb = new StringBuffer();
//		
//		PdfPCell cell = buildBlankCell(150, 0);
//		table.addCell(cell);
//
//		String expiry_date = StringUtils.isNotBlank(kyc311.getEXPIRY_DATE()) ? kyc311.getEXPIRY_DATE() : "無";
//		String cust_pro_date = StringUtils.isNotBlank(kyc311.getCUST_PRO_DATE()) ? kyc311.getCUST_PRO_DATE() : "無";
//		String degrade_date = StringUtils.isNotBlank(kyc311.getDEGRADE_DATE()) ? kyc311.getDEGRADE_DATE() : "無";
//		String hnwcDueDate = StringUtils.isNotBlank(kyc311.getHNWC_DUE_DATE()) ? kyc311.getHNWC_DUE_DATE() : "無";
//    	
//		if (type.equals("1")) {
//			sb.append("客戶本次風險屬性評估結果到期日：" + expiry_date + "; 專業投資人到期日：" + cust_pro_date + "; 高資產客戶到期日：" + hnwcDueDate+ "; 免降等到期日：" + degrade_date);
//			
//		} else {
//			sb.append("您本次風險屬性評估結果到期日：" + expiry_date);
//		}
//		 
//		cell = buildBlankCell(18, 0);
//		text = new Paragraph();
//		text.add(new Chunk(sb.toString(), bFont));
//		text.setAlignment(Paragraph.ALIGN_LEFT);
//		cell.addElement(text);
//		table.addCell(cell);
//		
//		table.addCell(buildBlankCell(18, 0));
//
//		return table;
//	}

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
	
	private PdfPCell buildCellWithBorder_middle(String value, Font font, int aligment) {
		 return buildCell_middle(value, font, aligment, -1, -1);
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
			Chunk chunk = new Chunk(value, font);
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
    
    public PdfPCell buildCell_middle(Object value, Font font, int alignment, int height, int br) {
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
            total = writer.getDirectContent().createTemplate(150, 20);
        }
		
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
				code39.setCode(cust_id);
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
				if (writer.getPageNumber() > 2) {
					s = "(客戶收執聯)";
				}else{
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
                
                Chunk chunkVer = new Chunk("2023年6月版", nFont);
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
