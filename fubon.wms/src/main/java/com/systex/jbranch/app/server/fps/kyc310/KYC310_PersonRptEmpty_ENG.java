package com.systex.jbranch.app.server.fps.kyc310;

import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.systex.jbranch.common.xmlInfo.XMLInfo;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataManager.DataManager;
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
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;

import static com.systex.jbranch.fubon.bth.job.context.AccessContext.tempReportPath;


/**
 * KYC自然人空白問卷英文版
 */
@Component("kyc310PersonRptEmptyENG")
@Scope("prototype")
public class KYC310_PersonRptEmpty_ENG extends FubonWmsBizLogic {
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

    /**
     * 參數物件
     */
    private XmlInfo xmlInfo;

    private String year;
    private String month;
    private String day;
    private File source;
    private Font nFont;
    private Font bFont;
    private Font bFont10;
    private Font font10;
    private Font bFont16;
    private Font bFontWhite;
    private Font nFontBlue;
    private Font nFont_a;
    private BaseFont kaiuFont;
    private BaseFont educFont;//Ray	201305070205-00	pdf檔案可顯示難字
    private Color partHeaderColor;

    /**
     * 空白KYC
     */
    public String genBlankPDF() throws Exception {
        initialize();
        createDocument();
        createPdfWriter();

        doc.open();
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
        bFont16 = new Font(kaiuFont, 16, Font.BOLD);
//        bFont16.setStyle(Font.UNDERLINE);
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
        year = time[0];
        month = time[1];
        day = time[2];

        /** xmlInfo **/
        xmlInfo = new XmlInfo();
    }

    /**
     * 建立 Document 容器
     **/
    private void createDocument() {
        Document.compress = false;
        Rectangle rectPageSize = new Rectangle(PageSize.A4);// 設定版面
        doc = new Document(rectPageSize, 10, 10, 10, 70);// 設定邊距(左,右,上,下)
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
    	doc.add(buildLogo());
        doc.add(buildHeader());
        doc.add(buildPart1Table());
        doc.add(buildPart2Preface());
        doc.add(buildPart2Table());
        doc.add(buildRemindTable());
        doc.add(buildSignTable());
        doc.add(buildNoteTable());
    }

    /**
     * 建立 LOGO 區塊
     **/
    private PdfPTable buildLogo() throws Exception {
        PdfPTable table = buildTable(new int[]{28, 28, 28});
        PdfPCell blank = buildBlankCell(12, 0);
        table.addCell(logo());
        table.addCell(blank);
        table.addCell(blank);
        return table;
    }
    
	/**
     * 建立 Header 區塊
     **/
    private PdfPTable buildHeader() throws Exception {
        PdfPTable table = buildTable(new int[]{18, 66});
        PdfPCell dateCell = buildCell(getPrintDate(), nFont, Paragraph.ALIGN_LEFT, -1, 0);
        dateCell.setVerticalAlignment(Element.ALIGN_BOTTOM);
        table.addCell(dateCell);
        table.addCell(buildTitle("Customer Risk Attribute Assessment Questionnaire", bFont16, Paragraph.ALIGN_LEFT));
        return table;
    }

    /**
     * 取得列印日期
     **/
    private String getPrintDate() {
        return String.format("Print Date：%s/%s/%s", year, month, day);
    }

    /**
     * 最上面的富邦 LOGO
     **/
    private PdfPCell logo() throws IOException, BadElementException {
        Image img = Image.getInstance(source + "//logo2.png");
        img.setWidthPercentage(85);
        PdfPCell logo = new PdfPCell();
        logo.addElement(img);
        logo.setBorder(0);
        return logo;
    }

    /**
     * 第一部分基本資料
     **/
    private PdfPTable buildPart1Table() throws Exception {
        PdfPTable table = buildTable(new int[]{84});
        table.addCell(getPart1Header());
        table.addCell(basicInfoRow());
        table.addCell(checkUpdateRow());
        table.addCell(educationRow());
        table.addCell(careerRow());
        table.addCell(marriageRow());
        table.addCell(kidRow());
        table.addCell(phoneRow());
        table.addCell(addressRow());
        table.addCell(emailRow());
        table.addCell(sickRow());
        return table;
    }

    /**
     * 第二部分前導文
     **/
    private PdfPTable buildPart2Preface() throws Exception {
        PdfPTable table = buildTable(new int[]{84});
        StringBuilder preface = new StringBuilder()
                .append("\nThe following questions can help assess your risk attribute, and to protect your interests, please fill the answers based on your actual situatuons.\n")
                .append("[Legal representatives, guardians or assistants should fill out the questionnaire for the customer (if the individual has no or limited legal capacity,\n")
                .append("is under guardianship or declared to be under assistance) based on actual situatuons and assume related responsibilities.]");
        table.addCell(buildCell(preface.toString(), bFont, Paragraph.ALIGN_LEFT, 35, 0));
        return table;
    }

    /**
     * 取得第一部分標題
     **/
    private PdfPCell getPart1Header() {
        PdfPCell header = buildSection("Part I: Personal Information", bFontWhite, Paragraph.ALIGN_CENTER);
        header.setBackgroundColor(partHeaderColor);
        return header;
    }

    /**
     * 基本資料列
     **/
    private PdfPCell basicInfoRow() throws Exception {
        PdfPTable table = buildTable(new int[]{10, 8, 12, 14, 12, 15, 5, 8});
        table.addCell(buildCellWithBorder("Name", bFont, Paragraph.ALIGN_CENTER));
        table.addCell(buildCellhardWord(StringEscapeUtils.unescapeHtml(inputVO.getCust_name()), nFont, nFont_a, Paragraph.ALIGN_CENTER));
        table.addCell(buildCellWithBorder("ID Number", bFont, Paragraph.ALIGN_CENTER));
        table.addCell(buildCellWithBorder(inputVO.getCUST_ID(), nFont, Paragraph.ALIGN_CENTER));
        table.addCell(buildCellWithBorder("Date of Birth", bFont, Paragraph.ALIGN_CENTER));
        table.addCell(buildCellWithBorder(getCustBirthday(), nFont, Paragraph.ALIGN_CENTER));
        table.addCell(buildCellWithBorder("Gender", bFont, Paragraph.ALIGN_CENTER));
        table.addCell(buildCellWithBorder(getCustGender(), nFont, Paragraph.ALIGN_CENTER));
        return new PdfPCell(table);
    }

    /**
     * 教育程度列
     **/
    private PdfPCell educationRow() throws Exception {
        PdfPTable table = buildTable(new int[]{10, 15, 59});
        table.addCell(setMiddle(buildCellWithBorder("Education", bFont, Paragraph.ALIGN_CENTER)));
        table.addCell(setMiddle(buildCellWithBorder(getCustEducation(), nFont, Paragraph.ALIGN_CENTER)));
        table.addCell(buildCellWithBorder(getEducationOptions(), nFont, Paragraph.ALIGN_LEFT));
        return new PdfPCell(table);
    }

    /**
     * 職業列
     **/
    private PdfPCell careerRow() throws Exception {
        PdfPTable table = buildTable(new int[]{10, 15, 59});
        table.addCell(setMiddle(buildCellWithBorder("Occupation", bFont, Paragraph.ALIGN_CENTER)));
        table.addCell(setMiddle(buildCellWithBorder(getCustCareer(), nFont, Paragraph.ALIGN_CENTER)));
        table.addCell(buildCellWithBorder(getCareerOptions(), nFont, Paragraph.ALIGN_LEFT));
        return new PdfPCell(table);
    }

    /**
     * 婚姻列
     **/
    private PdfPCell marriageRow() throws Exception {
        PdfPTable table = buildTable(new int[]{10, 15, 59});
        table.addCell(buildCellWithBorder("Marital Status", bFont, Paragraph.ALIGN_CENTER));
        table.addCell(buildCellWithBorder(getCustMarriage(), nFont, Paragraph.ALIGN_CENTER));
        table.addCell(buildCellWithBorder(getMarriageOptions(), nFont, Paragraph.ALIGN_LEFT));
        return new PdfPCell(table);
    }

    /**
     * 子女人數列
     **/
    private PdfPCell kidRow() throws Exception {
        PdfPTable table = buildTable(new int[]{10, 15, 59});
        table.addCell(buildCellWithBorder("Children", bFont, Paragraph.ALIGN_CENTER));
        table.addCell(buildCellWithBorder(getCustKids(), nFont, Paragraph.ALIGN_CENTER));
        table.addCell(buildCellWithBorder(getKidsOptions(), nFont, Paragraph.ALIGN_LEFT));
        return new PdfPCell(table);
    }

    /**
     * 連絡電話列
     *
     * @return
     */
    private PdfPCell phoneRow() throws Exception {
        PdfPTable table = buildTable(new int[]{10, 15, 59});
        table.addCell(setMiddle(buildCellWithBorder("Contact Number", bFont, Paragraph.ALIGN_CENTER)));
        table.addCell(buildCellWithBorder(getCustPhone(), nFont, Paragraph.ALIGN_LEFT));
        table.addCell(buildCellWithBorder(getPhoneOptions(), nFont, Paragraph.ALIGN_LEFT));
        return new PdfPCell(table);
    }

    /**
     * 通訊地址列
     **/
    private PdfPCell addressRow() throws Exception {
        PdfPTable table = buildTable(new int[]{10, 15, 59});
        table.addCell(setMiddle(buildCellWithBorder("Mailing Address", bFont, Paragraph.ALIGN_CENTER)));
        table.addCell(buildCellWithBorder(inputVO.getCUST_ADDR_1().trim().replace("　", ""), nFont, Paragraph.ALIGN_LEFT));
        table.addCell(buildCellWithBorder("□□□\n     ", nFont, Paragraph.ALIGN_LEFT));
        return new PdfPCell(table);
    }

    /**
     * 電子郵件列
     **/
    private PdfPCell emailRow() throws Exception {
        PdfPTable table = buildTable(new int[]{10, 15, 59});
        table.addCell(buildCellWithBorder("E-mail Address", bFont, Paragraph.ALIGN_CENTER));
        table.addCell(buildCellWithBorder(inputVO.getEMAIL_ADDR(), nFont, Paragraph.ALIGN_LEFT));
        table.addCell(buildCellWithBorder("", nFont, Paragraph.ALIGN_LEFT));
        return new PdfPCell(table);
    }

    /**
     * 重大傷病證明列
     **/
    private PdfPCell sickRow() throws Exception {
        PdfPTable table = buildTable(new int[]{25, 59});
        table.addCell(buildCellWithBorder("With Proof of Major Illness and Injury of National Health Insurance", bFont, Paragraph.ALIGN_CENTER));
        table.addCell(buildCellWithBorder_rowspan(getSickOptions(), nFont, Paragraph.ALIGN_LEFT, 2));
        table.addCell(setMiddle(buildCellWithBorder(getCustSick(), nFont, Paragraph.ALIGN_CENTER)));
        return new PdfPCell(table);
    }

    /**
     * 取得客戶性別
     **/
    private String getCustGender() {
        if (BooleanUtils.isTrue(inputVO.getGender_M())) return "Male";
        if (BooleanUtils.isTrue(inputVO.getGender_W())) return "Female";
        return " ";
    }

    /**
     * 取得客戶生日，客戶主檔為優先，其次則是傳入 Input 的生日資料
     **/
    private String getCustBirthday() throws JBranchException {
        String birthdayCustMast = "";
        List<Map<String, String>> clist = exeQueryForQcf(genDefaultQueryConditionIF()
                .setQueryString("select to_char(trunc(BIRTH_DATE), 'yyyy-MM-dd') BIRTHDAY from TBCRM_CUST_MAST where CUST_ID = :custId ")
                .setObject("custId", StringUtils.defaultString(inputVO.getCUST_ID())));

        if (CollectionUtils.isNotEmpty(clist))
            birthdayCustMast = StringUtils.defaultString(clist.get(0).get("BIRTHDAY"));

        String birthdayInput = "";
        if (StringUtils.isNotBlank(inputVO.getBirthday())) {
            birthdayInput = new SimpleDateFormat("yyyy/MM/dd")
                    .format(Long.parseLong(inputVO.getBirthday()));
        }
        return StringUtils.defaultIfEmpty(birthdayCustMast, birthdayInput);
    }

    /**
     * 確認是否更新資料列
     **/
    private PdfPCell checkUpdateRow() throws Exception {
        PdfPTable table = buildTable(new int[]{30, 54});
        table.addCell(buildCellWithBorder("□ No need to update", bFont, Paragraph.ALIGN_CENTER));
        table.addCell(buildCellWithBorder("□ Update as following", bFont, Paragraph.ALIGN_CENTER));
        return new PdfPCell(table);
    }

    /**
     * 設定垂直置中
     **/
    private PdfPCell setMiddle(PdfPCell cell) {
        cell.setVerticalAlignment(Paragraph.ALIGN_MIDDLE);
        return cell;
    }

    /**
     * 取得客戶教育程度
     **/
    private String getCustEducation() throws JBranchException {
        Map<String, String> ref = xmlInfo.getVariable("KYC.EDUCATION_ENG", "F3");
        String degreeName = StringUtils.defaultString(ref.get(inputVO.getEDUCATION()));
        String degreeCode = "";
        if (StringUtils.isNotBlank(degreeName)) {
//            if (degreeName.contains("illiterate")) degreeCode = "(7)";
//            else {
                List<Map<String, String>> mapping = exeQueryForQcf(genDefaultQueryConditionIF()
                        .setQueryString("select PARAM_CODE from TBSYSPARAMETER where PARAM_TYPE ='KYC.EDUCATION_UP_ENG' and PARAM_NAME like :degreeName ")
                        .setObject("degreeName", "%" + degreeName + "%"));
                if (CollectionUtils.isNotEmpty(mapping))
                    degreeCode = String.format("(%s)", StringUtils.defaultString(mapping.get(0).get("PARAM_CODE")));
//            }
        }
        return degreeCode + degreeName;
    }

    /**
     * 取得教育程度選項
     **/
    private String getEducationOptions() {
        StringBuilder options = new StringBuilder();
        if (CollectionUtils.isNotEmpty(inputVO.getEDUCATIONList())) {
            for (int i = 0; i < inputVO.getEDUCATIONList().size(); i++) {
                options.append(String.format("□(%s)%s%s ", i + 1,
                        StringUtils.defaultString((String) inputVO.getEDUCATIONList().get(i).get("LABEL_ENG")),
                        /** 第一行強制斷行，比較好看 **///先不用
                        i == 0 ? "" : ""));
            }
        }
        return options.toString();
    }

    /**
     * 取得客戶職業
     **/
    private String getCustCareer() throws JBranchException {
        String careerName = xmlInfo.getVariable("KYC.CAREER_ENG", inputVO.getCAREER(), "F3");
        String careerCode = "";
        if (StringUtils.isNotBlank(inputVO.getCAREER())) {
            careerCode = String.format("(%s)", inputVO.getCAREER());
        }
        return careerCode + careerName;
    }

    /**
     * 取得客戶重大傷病證明
     **/
    private String getCustSick() throws JBranchException {
        String type = "";
        if (StringUtils.isNotBlank(inputVO.getSICK_TYPE()))
            type = xmlInfo.getVariable("KYC.HEALTH_FLAG_ENG", inputVO.getSICK_TYPE(), "F3");
        return type;
    }

    /**
     * 取得職業選項
     **/
    private String getCareerOptions() {
        StringBuilder options = new StringBuilder();
        if (CollectionUtils.isNotEmpty(inputVO.getCAREERList())) {
            for (Map map : inputVO.getCAREERList()) {
                options.append(String.format("□(%s)%s ",
                        StringUtils.defaultString((String) map.get("DATA")),
                        StringUtils.defaultString((String) map.get("LABEL_ENG"))));
            }
        }
        return options.toString();
    }

    /**
     * 取得客戶婚姻狀態
     **/
    private String getCustMarriage() throws JBranchException {
        String marriageName = xmlInfo.getVariable("KYC.MARRAGE_ENG", inputVO.getMARRAGE(), "F3");
        String marriageCode = "";
        if (StringUtils.isNotBlank(inputVO.getMARRAGE())) {
            marriageCode = String.format("(%s)", inputVO.getMARRAGE());
        }
        return marriageCode + marriageName;
    }

    /**
     * 取得客戶子女人數
     **/
    private String getCustKids() throws JBranchException {
        String kidCode = "";
        String kidName = xmlInfo.getVariable("KYC.CHILD_NO_ENG", inputVO.getCHILD_NO(), "F3");
        if (StringUtils.isNotBlank(kidName)) {
            List<Map<String, String>> mapping = exeQueryForQcf(genDefaultQueryConditionIF()
                    .setQueryString("select PARAM_CODE from TBSYSPARAMETER where PARAM_TYPE ='KYC.CHILD_NO_UP' and PARAM_NAME like :kidName ")
                    .setObject("kidName", "%" + kidName + "%"));
            if (CollectionUtils.isNotEmpty(mapping))
                kidCode = String.format("(%s)", StringUtils.defaultString(mapping.get(0).get("PARAM_CODE")));
        }
        return kidCode + kidName;
    }

    /**
     * 取得客戶聯絡電話
     **/
    private String getCustPhone() {
        StringBuilder phone = new StringBuilder();
        if (StringUtils.isNotBlank(inputVO.getDAY()))
            phone.append(String.format("(Day)%s%n", inputVO.getDAY()));
        if (StringUtils.isNotBlank(inputVO.getNIGHT()))
            phone.append(String.format("(Night)%s%n", inputVO.getNIGHT()));
        if (StringUtils.isNotBlank(inputVO.getTEL_NO()))
            phone.append(String.format("(Mobile)%s%n", inputVO.getTEL_NO()));
        if (StringUtils.isNotBlank(inputVO.getFAX()))
            phone.append(String.format("(Fax)%s%n", inputVO.getFAX()));
        return phone.toString();
    }

    /**
     * 取得子女人數選項
     **/
    private String getKidsOptions() {
        StringBuilder options = new StringBuilder();
        if (CollectionUtils.isNotEmpty(inputVO.getCHILD_NOList())) {
            for (int i = 0; i < inputVO.getCHILD_NOList().size(); i++) {
                options.append(String.format("□(%d)%s　", i + 1,
                        StringUtils.defaultString((String) inputVO.getCHILD_NOList().get(i).get("LABEL_ENG"))));
            }
        }
        return options.toString();
    }

    /**
     * 取得婚姻選項
     **/
    private String getMarriageOptions() {
        StringBuilder options = new StringBuilder();
        if (CollectionUtils.isNotEmpty(inputVO.getMARRAGEList())) {
            for (int i = 0; i < inputVO.getMARRAGEList().size(); i++) {
            	if(!inputVO.getMARRAGEList().get(i).get("LABEL").equals("Other")){
            		 options.append(String.format("□(%d)%s　", i + 1,
                             StringUtils.defaultString((String) inputVO.getMARRAGEList().get(i).get("LABEL_ENG"))));
            	}
               
            }
        }
        return options.toString();
    }

    /**
     * 取得連絡電話選項
     **/
    private String getPhoneOptions() {
        return new StringBuilder()
                .append("□Day:                     □Mobile:\n")
                .append("□Night:                   □Fax:\n")
                .toString();
    }

    /**
     * 取得重大傷病證明選項
     **/
    private String getSickOptions() {
        StringBuilder options = new StringBuilder();
        if (CollectionUtils.isNotEmpty(inputVO.getSICK_TYPEList())) {
            for (int i = 0; i < inputVO.getSICK_TYPEList().size(); i++) {
                options.append(String.format("□%s%n", StringUtils.defaultString((String) inputVO.getSICK_TYPEList().get(i).get("LABEL_ENG"))));
            }
        }
        return options.toString();
    }

    /**
     * 第二部分：題目與答案選項
     **/
    private PdfPTable buildPart2Table() throws Exception {
        PdfPTable table = buildTable(new int[]{84});
        table.addCell(getPart2Header());

        int index = 1;
        for (Map map : inputVO.getQuest_list()) {
            PdfPTable couple = buildTable(new int[]{28, 56});
            /** 題目 **/
            if(new Double(11).equals((Double)map.get("QST_NO"))) {
            	//第11題特殊處理
            	PdfPTable q11table = getQ11Question(map);            	
            	couple.addCell(q11table);
            } else {
            	couple.addCell(setMiddle(buildCellWithBorder(String.format("%d、%s", index++, getQst(map)), nFont, Paragraph.ALIGN_LEFT)));
            }
            
            /** 答案選項是否以圖片代為顯示 **/
            if (StringUtils.isNotBlank((String) map.get("PICTURE")) && !new Double(11).equals((Double)map.get("QST_NO"))) {
                couple.addCell(createImageCell((Blob) map.get("PICTUREObject")));
            } else {
            	if(new Double(3).equals((Double)map.get("QST_NO"))) {
            		//第3題答案顯示特殊處理
            		couple.addCell(getQ3Ans());
            	} else if(new Double(11).equals((Double)map.get("QST_NO"))) {
            		//第11題答案顯示特殊處理
            		couple.addCell(getAns(map, true));
            	} else {
            		couple.addCell(getAns(map, false));
            	}
            }
            
            table.addCell(new PdfPCell(couple));
        }
        return table;
    }

    /**
     * 取得第二部分標題
     **/
    private PdfPCell getPart2Header() {
        PdfPCell header = buildSection("Part II: Financial Status, Investment Experience, Investment Objective and Risk Appetite",
                bFontWhite, Paragraph.ALIGN_CENTER);
        header.setBackgroundColor(partHeaderColor);
        return header;
    }

    /**
     * 取得題目
     **/
    private String getQst(Map map) {
        return StringUtils.defaultString((String) map.get("QUESTION_DESC_ENG"));
    }

    /**
     * 取得答案選項
     **/
    private PdfPTable getAns(Map map, boolean isQ11) {
        List options = new ArrayList();
        List<Map> ansList = (List) map.get("ANSWER_LIST");
        for (Map ansMap : ansList)
            options.add(String.format("□%s", ansMap.get("ANSWER_DESC_ENG")));
        
        return genInnerTable(options, nFont, isQ11);
    }
    
    /**
     * 建立以傳入圖片為背景的 Cell
     **/
    private PdfPCell createImageCell(Blob raw) {
        PdfPCell cell = new PdfPCell();
        try {
            Image image = Image.getInstance(raw.getBytes(1, (int) raw.length()));
            /** 設定圖片尺寸，公式為： 欄寬 / 圖片寬度 * 百分比(100%) * 倍率**/
            image.scalePercent(56 / image.getWidth() * 100 * 3);
            image.setAlignment(Image.ALIGN_MIDDLE);
            cell.addElement(image);
            raw.free();
        } catch (BadElementException | IOException | SQLException e) {
            logger.info(e.getMessage());
        }
        return cell;
    }

    /**
     * 約定事項
     **/
    private PdfPTable buildRemindTable() throws Exception {
        PdfPTable table = buildTable(new int[]{84});
        StringBuffer sb = new StringBuffer();
        sb.append("\nTerms and Conditions：\n")
                .append("1.The questionnaire filled by the you and the analysis result are bases only for products suitability assessment upon\n")
                .append("  transaction instructions given by you to Taipei Fubon Bank ('the Bank'). You understand and confirm that the Bank\n")
                .append("  does not guarantee or promise the investment result, investment profits and  no losses in investment principals.\n")
                .append("2.The analysis result of this questionnaire is valid for one year. The Bank will take the last valid result as the\n")
                .append("  accordance for products suitability assessment in every transaction instruction given by you except periodic recurring\n")
                .append("  transactions (i.e., regular savings plan for mutual funds). However, if the analysis result is adjusted which leads to\n")
                .append("  the mismatch of products suitability assessment for such periodic recurringtransactions, you may only continue the initial\n")
                .append("  investment agreement but not to increase the investment amount, the frequency of investment or resumption in investment.\n")
                .append("  In that situation, you are advised to evaluate the terms of the initial investment agreement.\n")
                .append("3.If you are under 18 years old, please follow the provisions of the existing consent for minor account opening to\n")
                .append("  undertake this questionnaire assessment.\n")
                .append("4.Your risk attribute assessment shall be reviewed regularly, which may be done at business units of the Bank or via\n")
                .append("  Internet Banking /Mobile Banking or on Nano Investment Platform.\n")
                .append("5.In order to protect your right and interests, the Bank may confirm the questionnaire contents with you by phone.\n")
                .append("6.If you have any question about the content or the analysis result of the questionnaire, please contact your relationship\n")
        		.append("  manager or call the following number for our customer service: (02)8751-6665.");

        table.addCell(buildCell(sb.toString(), bFont, Paragraph.ALIGN_LEFT, -1, 0));
        return table;
    }

    /**
     * 客戶簽署區域
     **/
    private PdfPTable buildSignTable() throws Exception {
        PdfPTable table = buildTable(new int[]{84});
        table.addCell(buildBlankCell(20, 0));
        table.addCell(signParagraph(
                new Chunk("Applicant：", bFont10),
                new Chunk("__________________________  ", font10),
                new Chunk("(Please sign or affix the retained specimen seal of the following account", font10)
        ));
        table.addCell(signParagraph(
    		  	new Chunk("or contract seal and signature retained on \"Client Information Form for Bond Business\")", font10)
    	));
//        table.addCell(signParagraph(
//                new Chunk("立約印鑑或簽章樣式)", font10)
//        ));
        table.addCell(signParagraph(
                new Chunk("Account Number：□□□□□□□□□□□□ ", font10)
        ));
        table.addCell(signParagraph(
                new Chunk("Legal Representatives/Guardians/Assistants：", bFont10),
                new Chunk("__________________(Please sign)  ", font10),
                new Chunk("ID Number：", bFont10),
                new Chunk("__________________", font10)
        ));
        table.addCell(signParagraph(
                new Chunk("Date：", bFont10)
        ));
        table.addCell(buildBlankCell(18, 0));
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

    /** 註記區 **/
    private PdfPTable buildNoteTable() throws Exception {
        PdfPTable table = buildTable(new int[]{84}, 100);
        PdfPTable inner = buildTable(new int[]{17, 16, 21, 13, 17});

        inner.addCell(buildCell("For Bank Use Only：", nFont, Paragraph.ALIGN_LEFT, -1, 0));
        inner.addCell(buildBlankCell(18, 0));
        inner.addCell(buildBlankCell(18, 0));
        inner.addCell(buildBlankCell(18, 0));
        inner.addCell(buildBlankCell(18, 0));
        inner.addCell(buildCell("Business Unit：", nFont, Paragraph.ALIGN_LEFT, -1, 0));
        inner.addCell(buildBlankCell(18, 0));
        inner.addCell(buildBlankCell(18, 0));
        inner.addCell(buildBlankCell(18, 0));
        inner.addCell(buildBlankCell(18, 0));

        inner.addCell(buildCell("Explanation：_________", nFont, Paragraph.ALIGN_LEFT, -1, 0));
        inner.addCell(buildCell("Witness by：_________", nFont, Paragraph.ALIGN_LEFT, -1, 0));
        inner.addCell(buildCell("Seal Verification：_________", nFont, Paragraph.ALIGN_LEFT, -1, 0));
        inner.addCell(buildCell("Key-in：_________", nFont, Paragraph.ALIGN_LEFT, -1, 0));
        inner.addCell(buildCell("Supervisor：_________", nFont, Paragraph.ALIGN_LEFT, -1, 0));

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

    private PdfPCell buildCellWithBorder_rowspan(String value, Font font, int aligment, int row) {
        return buildCell_rowspan(value, font, aligment, -1, -1, row);
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
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.addElement(text);
            cell.setPaddingBottom(5);
            cell.setColspan(2);
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

    private PdfPCell buildCellQ3(Object value, Font font, int alignment, int height, float br) {
    	PdfPCell cell = buildCell(value, font, alignment, height, (int)br);
        
    	cell.setBorderWidth(br);
    	cell.setBorderColor(new Color(224,224,224));
    	
        return cell;
    }
    
    //cell with rowspan
    private PdfPCell buildCell_rowspan(Object value, Font font, int alignment, int height, int br, int row) {
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
        cell.setRowspan(row);
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

    private int getFontWidth(String text, Font font) {
        BaseFont bFont = font.getCalculatedBaseFont(true);
        return (int) bFont.getWidthPoint(text, font.getCalculatedSize());
    }

    private PdfPTable genInnerTable(List<String> list, Font font, boolean isQ11) {
        PdfPTable table = null;
        int totalLen = 372;
        int halfLen = totalLen / 2;
        int thirdLen = totalLen / 3;
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
            for (int i = 0; i < table.getNumberOfColumns() - mod; i++) {
                list.add("");
            }
        }
        
        for (String str : list) {
        	if(isQ11) {
        		Font q11Font = new Font(nFont);
        		q11Font.setColor(new Color(0, 0, 0));
        		PdfPCell cell = setMiddle(buildCell(str, q11Font, Paragraph.ALIGN_LEFT, 35, 0));
        		cell.setFixedHeight(24);
        		table.addCell(cell);
        	} else { 
        		table.addCell(setMiddle(buildCell(str, nFont, Paragraph.ALIGN_LEFT, 10, 0)));
        	}
        }
        
        return table;
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
                tBarCode = new PdfPTable(4);
                tBarCode.setTotalWidth(545);
                PdfContentByte cb = writer.getDirectContent();
                
				// #2252_調整KYC問卷條碼及版次
                if(page == 1) {
                	// barcode - page
                    Barcode39 code39 = new Barcode39();
                    code39.setCode("88-0402-99");
                    code39.setBarHeight(17);
                    code39.setSize(7);

                    PdfPCell cell = new PdfPCell();
                    cell.setFixedHeight(30);
                    cell.setPadding(0);
                    cell.setBorderWidth(0);
                    cell.addElement(code39.createImageWithBarcode(cb, null, null));

                    PdfPCell blank = new PdfPCell();
                    blank.setBorderWidth(0);
                    blank.setFixedHeight(10);

                    tBarCode.addCell(cell);
					for (int i = 0; i < tBarCode.getNumberOfColumns(); i++) {
						tBarCode.addCell(blank);
					}
                }
                
                // page num
                tPageNum = new PdfPTable(3);
                tPageNum.setTotalWidth(545);
                int[] data = new int[3];
				data = new int[]{273, 45, 227};
				tPageNum.setWidths(data);

                PdfPCell curPage = new PdfPCell();
                curPage.setBorderWidth(0);

                Chunk chunk = new Chunk(String.format("Page %d", writer.getPageNumber()), nFont);
                Paragraph text = new Paragraph();
                text.setAlignment(Phrase.ALIGN_RIGHT);
                text.add(chunk);
                curPage.addElement(text);

                PdfPCell totalPage = new PdfPCell(Image.getInstance(total));
                totalPage.setBorderWidth(0);
                totalPage.setHorizontalAlignment(Image.ALIGN_LEFT);
                
                PdfPCell version = new PdfPCell();
                version.setBorderWidth(0);
                
                Chunk chunkVer = new Chunk("Version：2024.12", nFont);
                Paragraph textVer = new Paragraph();
                textVer.setAlignment(Phrase.ALIGN_RIGHT);
                textVer.add(chunkVer);
                version.addElement(textVer);

                tPageNum.addCell(curPage);
                tPageNum.addCell(totalPage);
                tPageNum.addCell(version);

                // write
                tBarCode.writeSelectedRows(0, -1, 25, 60, writer.getDirectContent());
                tPageNum.writeSelectedRows(0, -1, 25, 40, writer.getDirectContent());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * Fills out the total number of pages before the document is closed.
         */
        public void onCloseDocument(PdfWriter writer, Document document) {
            Chunk chunk = new Chunk("of " + (writer.getPageNumber() - 1) + " pages", nFont);
            Phrase text = new Phrase();
            text.add(chunk);
            ColumnText.showTextAligned(total, Element.ALIGN_LEFT, text, 3, 2, 0);
        }
    }
    
    public PdfPTable getQ3Ans() throws Exception {
    	XMLInfo xmlinfo = new XMLInfo();
    	List<Map<String, Object>> prodExpList = xmlinfo.getXMLInfo("KYC.Q3_PROD_EXP_ENG");
    	List<Map<String, Object>> prodTypeList = xmlinfo.getXMLInfo("KYC.Q3_PROD_TYPE_ENG");
    	
    	//第3題特殊處理
    	PdfPTable q3table = buildTable(new int[]{13,7,7,7,7,7});
    	for(int i = -1; i < prodTypeList.size(); i++) {
    		for(int j = -1; j < prodExpList.size(); j++) {
    			if(i == -1 && j == -1) {
    				q3table.addCell(setMiddle(buildCellQ3("", nFont, Paragraph.ALIGN_CENTER, 12, (float)0.1)));
    			} else if(i == -1 && j > -1) {
    				q3table.addCell(setMiddle(buildCellQ3(prodExpList.get(j).get("PARAM_NAME").toString(), nFont, Paragraph.ALIGN_CENTER, 24, (float)0.1)));
    			} else if(i > -1 && j == -1) {
    				if(i == prodTypeList.size() - 1) {
    					int pLen = StringUtils.length(prodTypeList.get(i).get("PARAM_NAME").toString());
    					int pIdx = StringUtils.indexOf(prodTypeList.get(i).get("PARAM_NAME").toString(), "(");
    					String pName = StringUtils.substring(prodTypeList.get(i).get("PARAM_NAME").toString(), 0, pIdx) + "\n" 
    								 + StringUtils.substring(prodTypeList.get(i).get("PARAM_NAME").toString(), pIdx, pLen);
    					int height = (i == prodTypeList.size() - 1 ? 32 : 22);//衍生性商品字數較多需要高一點
    					q3table.addCell(setMiddle(buildCellQ3(pName, nFont, Paragraph.ALIGN_CENTER, height, (float)0.1)));
    				} else {
    					q3table.addCell(setMiddle(buildCellQ3(prodTypeList.get(i).get("PARAM_NAME").toString(), nFont, Paragraph.ALIGN_CENTER, 12, (float)0.1)));
    				}
    			} else  if(i > -1 && j > -1) {
    				q3table.addCell(setMiddle(buildCellQ3("□", nFont, Paragraph.ALIGN_CENTER, 12, (float)0.1)));
    			}
    		}
    	}
		
		return q3table;
    }
    
    private PdfPTable getQ11Question(Map map) throws Exception {
    	PdfPTable q11table = buildTable(new int[]{28});
    	PdfPCell cell = new PdfPCell();
    	cell = setMiddle(buildCellWithBorder(String.format("%d、%s", 11, getQst(map)), nFont, Paragraph.ALIGN_LEFT));
    	cell.setBorderWidth(0);
    	q11table.addCell(cell);
		return q11table;
    }
}
