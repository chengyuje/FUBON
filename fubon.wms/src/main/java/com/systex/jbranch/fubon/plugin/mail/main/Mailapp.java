package com.systex.jbranch.fubon.plugin.mail.main;

import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.scheduler.SchedulerHelper;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.*;
import java.io.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 201505250161-00 Ray 修改郵件寄送時軌跡留存之規則, 並增加定期監控機制
 */
@Component("mailapp")
@Scope("prototype")
@SuppressWarnings({"unchecked", "rawtypes"})
public class Mailapp extends BizLogic {
    private XmlInfo xmlInfo = new XmlInfo();
    private Map<String, String> mailInfo;

    private String SMTP_AUTH_NAME;
    private String SMTP_HOST;

    private String PATH;
    private String MAIL_CONTENT_PATH;
    private String BACK_FILE_PATH;

    private String DEFAULT_FORM;

    /**
     * 寄送成功狀態
     **/
    private final String TYPE_OK = "O";
    /**
     * 寄送失敗狀態
     **/
    private final String TYPE_FAIL = "F";
    /**
     * 寄送前發生錯誤狀態
     **/
    private final String TYPE_ERROR = "E";
    /**
     * LOG字數限制
     **/
    private final int LOG_LENGTH = 2000;


    /**
     * 排程參數可設定指定email，mailApp將會寄送此email。預設不指定。
     **/
    private boolean isAssigned = false;
    private String assignedEmail = "";

    /**
     * 初始化參數
     *
     * @throws JBranchException
     */
    private void initParameter() throws Exception {
        mailInfo = xmlInfo.doGetVariable("MAILSERVER.SETTING", FormatHelper.FORMAT_3);
        PATH = checkPath(mailInfo.get("SOURCE_FILE_PATH"));
        MAIL_CONTENT_PATH = checkPath(mailInfo.get("MAIL_CONTENT_PATH"));
        BACK_FILE_PATH = checkPath(mailInfo.get("BACK_FILE_PATH"));

        SMTP_AUTH_NAME = (String) mailInfo.get("SMTP_AUTH_NAME");
        SMTP_HOST = (String) mailInfo.get("SMTP_HOST");

        DEFAULT_FORM = (String) mailInfo.get("DEFAULT_FORM");
    }

    /**
     * 確認設置在資料庫的路徑是否存在
     **/
    private String checkPath(String path) throws Exception {
        if (new File(path).exists()) return path;
        else throw new Exception(path + " is not exists ! ");
    }

    /**
     * 檢查temp folder是否有資料，有資料則先處理
     *
     * @throws JBranchException
     */
    public void procTempFile(Object body, IPrimitiveMap<?> header) throws Exception {
        try {
            checkTest((Map) body);
            initParameter();
            File file = new File(PATH);

            List<String> chkList = new ArrayList<String>();
            List<String> filelList = filelList(file, chkList);

            // 檢查temp folder是否有資料，有資料則先處理
            if (chekTempFolder(filelList)) {
                this.moveFile(file, BACK_FILE_PATH);
                this.deleteAll(file, PATH);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        }
    }

    /**
     * 處理FTP指定目錄 File
     *
     * @throws JBranchException
     */
    public void procFtpFile(Object body, IPrimitiveMap<?> header) throws Exception {
        try {
            checkTest((Map) body);
            initParameter();

            File file = new File(PATH);

            List<String> chkList = new ArrayList<String>();
            List<String> filelList = filelList(file, chkList);
            sendMail(filelList, null);
            this.moveFile(file, BACK_FILE_PATH);
            this.deleteAll(file, PATH);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        }
    }

    /** 判斷排程是否有提供參數(指定email)，如果有的話，將會寄送給指定email **/
    private void checkTest(Map body) {
        Object arg = ((Map) body.get(SchedulerHelper.JOB_PARAMETER_KEY)).get("arg");
        if (StringUtils.isNotBlank((String) arg)) {
            isAssigned = true;
            assignedEmail = arg.toString();
        }
    }

    private HashMap<String, String> adsInfo = null;

    private void getAdsInfo() {
        adsInfo = new HashMap<String, String>();
        try {
            File file = new File(MAIL_CONTENT_PATH + "/url.txt");
            BufferedReader br = new BufferedReader(new FileReader(file));
            String s = "";
            while ((s = br.readLine()) != null) {
                adsInfo.put(s.split(":", 2)[0], s.split(":", 2)[1]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendMail(List<String> filelList, List<String> addrList) {
        getAdsInfo();

        List<String> emailList = new ArrayList<String>();
        List<String> custList = new ArrayList<String>();
        List<Map> tempList = new ArrayList<Map>();
        List<Map> contentList = new ArrayList<Map>();
        try {
            for (int i = 0; i < filelList.size(); i++) {

                File fileN = new File(filelList.get(i));


                List<String> alist = new ArrayList<String>();

                BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileN), "Big5_HKSCS"));

                String str = "";

                while ((str = in.readLine()) != null) {
                    alist.add(str);
                }
                in.close();

                // 處理資料
                Map dataMap = dataOutPut(alist, addrList, false);

                emailList = (List) dataMap.get("emailList");

                custList = (List) dataMap.get("custList");

                // String firstName = fileN.getName().substring(0,1);

                tempList = (List) dataMap.get("tempList");

                contentList = (List) dataMap.get("contentList");

                mailapps(emailList, custList, contentList, tempList, fileN);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * alist 資料來源為X檔案or X.result 檔案， if X.result check=true
     *
     * @param alist
     * @param check
     * @return Map<String       ,       List>
     * @author Camel
     * @date 2013/10/7 下午2:33:03
     */
    private Map<String, List> dataOutPut(List<String> alist, List<String> addrList, boolean check) {
        Map<String, List> dataMap = new HashMap<String, List>();
        List<String> emailList = new ArrayList<String>();
        List<String> custList = new ArrayList<String>();
        List<Map> contentList = new ArrayList<Map>();
        List<Map> tempList = new ArrayList<Map>();
        List<String> emailResultList = new ArrayList<String>();
        List<String> cmList = new ArrayList<String>();
        boolean continuePoint = false;

        try {
            Calendar cc = Calendar.getInstance();
            // example 20131014
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

            String formtype = null;
            for (int a = 0; a < alist.size(); a++) {
                String tmpstr = alist.get(a);
                if (StringUtils.isNotBlank(tmpstr)) {

                    String[] tmparr = cutStringByByte(tmpstr, 1);

                    if ("1".equals(tmparr[0])) {
                        tmparr = cutStringByByte(tmparr[1], 2);
                        formtype = tmparr[0];

                        // email size is not same
                        if ("02".equals(formtype)) {
                            tmparr = cutStringByByte(tmparr[1], 50);
                        } else {
                            tmparr = cutStringByByte(tmparr[1], 60);
                        }
                        cmList.add(tmparr[0]);
                    }
                }
            }

            // email 相同，則將資料合併為同一筆, formtype = 01
            if (!"01".equals(formtype)) {
                cmList = dataList(cmList);
            }

            for (int a = 0; a < alist.size(); a++) {
                String tmpstr = alist.get(a);

                if (StringUtils.isNotBlank(tmpstr)) {
                    String[] tmparr = cutStringByByte(tmpstr, 1);


                    String firstWord = tmparr[0];
                    Map conMap = new HashMap();

                    if ("0".equals(firstWord)) {
                        // 資料產生日期
                        tmparr = cutStringByByte(tmparr[1], 8);
                        conMap.put("dataDate", tmparr[0]);

                    } else if ("1".equals(firstWord)) {
                        String email = null;
                        String custId = null;

                        tmparr = cutStringByByte(tmparr[1], 2);
                        if ("01".equals(tmparr[0])) {

                            // FORM_TYP 通知訊息類別 len 2
                            conMap.put("form_typ", tmparr[0]);

                            // PGT07 EMAIL len 60
                            tmparr = cutStringByByte(tmparr[1], 60);
                            email = tmparr[0];

                            // PGT01 身分證號 len 11
                            tmparr = cutStringByByte(tmparr[1], 11);
                            conMap.put("dataPgt01", tmparr[0]);
                            custId = tmparr[0];

                            // PGT01A 姓名 len 62
                            tmparr = cutStringByByte(tmparr[1], 62);
                            conMap.put("dataPgt01a", tmparr[0]);

                            // PGT02 商品 len 10
                            tmparr = cutStringByByte(tmparr[1], 10);
                            conMap.put("dataPgt02", tmparr[0]);

                            // PGT02A 商品名稱 len 32
                            tmparr = cutStringByByte(tmparr[1], 32);
                            conMap.put("dataPgt02a", tmparr[0]);

                            // PGT03 設定方式 len 1
                            tmparr = cutStringByByte(tmparr[1], 1);
                            conMap.put("dataPgt03", tmparr[0]);

                            // PGT04 指定價格 len 13
                            tmparr = cutStringByByte(tmparr[1], 13);
                            conMap.put("dataPgt04", moneyTransfer(tmparr[0]));

                            // PGT05 收盤價 len 13
                            tmparr = cutStringByByte(tmparr[1], 13);
                            conMap.put("dataPgt05", moneyTransfer(tmparr[0]));

                            // PGT06 收盤價日 len 8
                            tmparr = cutStringByByte(tmparr[1], 8);
                            conMap.put("dataPgt06", dateTransfer(tmparr[0]));

                            // PGT08 計價幣別 len 10
                            tmparr = cutStringByByte(tmparr[1], 10);
                            conMap.put("dataPgt08", tmparr[0]);

                            // PGT09 特別說明 len 1
                            tmparr = cutStringByByte(tmparr[1], 1);
                            conMap.put("dataPgt09", tmparr[0]);

                        } else if ("02".equals(tmparr[0])) {

                            // FORM_TYP 通知訊息類別 len 2
                            conMap.put("form_typ", tmparr[0]);

                            // email
                            tmparr = cutStringByByte(tmparr[1], 50);
                            email = tmparr[0];

                            // MSG_TYP 通知訊息類別 len 5
                            tmparr = cutStringByByte(tmparr[1], 5);
                            conMap.put("dataMsg_typ", tmparr[0]);

                            // YYY len 3
                            tmparr = cutStringByByte(tmparr[1], 3);
                            conMap.put("dataYYY", tmparr[0]);

                            // MM len 2
                            tmparr = cutStringByByte(tmparr[1], 2);
                            conMap.put("dataMM", tmparr[0]);

                            // DD len 2
                            tmparr = cutStringByByte(tmparr[1], 2);
                            conMap.put("dataDD", tmparr[0]);

                            // ACNO 帳號 len 12
                            tmparr = cutStringByByte(tmparr[1], 12);
                            conMap.put("dataAcno", tmparr[0]);

                            // CUR 幣別 len 12
                            tmparr = cutStringByByte(tmparr[1], 12);
                            conMap.put("dataCur", tmparr[0]);

                            // OD_AMT 透支金額 len 20
                            tmparr = cutStringByByte(tmparr[1], 20);
                            conMap.put("dataOd_amt", tmparr[0]);
                        } else if ("03".equals(tmparr[0]) || "09".equals(tmparr[0])) {
                            // 201405050264-08 Ray 新增海外股票/ETF通知信
                            // FORM_TYP 通知訊息類別 len 3
                            conMap.put("form_typ", tmparr[0]);
                            formtype = tmparr[0];

                            // email
                            tmparr = cutStringByByte(tmparr[1], 60);
                            email = tmparr[0];

                            // TRS03 len 11
                            tmparr = cutStringByByte(tmparr[1], 11);
                            custId = tmparr[0];

                            // TRS04 62
                            tmparr = cutStringByByte(tmparr[1], 62);

                            // TRS05 10
                            tmparr = cutStringByByte(tmparr[1], 10);
                            conMap.put("dataTRS05", tmparr[0]);

                            // TRS06 8
                            tmparr = cutStringByByte(tmparr[1], 8);

                            SimpleDateFormat sd1 = new SimpleDateFormat("yyyyMMdd");
                            SimpleDateFormat sd2 = new SimpleDateFormat("yyyy/MM/dd");

                            Date datetsr06 = sd1.parse(tmparr[0]);
                            conMap.put("dataTRS06", sd2.format(datetsr06));

                            // TRS07 10
                            tmparr = cutStringByByte(tmparr[1], 10);
                            conMap.put("dataTRS07", tmparr[0]);
                            System.out.println(tmparr[0]);

                            // TRS08 32
                            tmparr = cutStringByByte(tmparr[1], 32);
                            conMap.put("dataTRS08", tmparr[0]);

                            // TRS09 1
                            tmparr = cutStringByByte(tmparr[1], 1);
                            conMap.put("dataTRS09", tmparr[0]);

                            // TRS10 3
                            tmparr = cutStringByByte(tmparr[1], 3);
                            conMap.put("dataTRS10", tmparr[0]);

                            // TRS11 11
                            tmparr = cutStringByByte(tmparr[1], 11);
                            conMap.put("dataTRS11", moneyTransfer(tmparr[0], 11, 0));

                            // TRS12 13
                            tmparr = cutStringByByte(tmparr[1], 13);
                            conMap.put("dataTRS12", moneyTransfer(tmparr[0], 13, 7, 6));

                            // TRS13 11
                            tmparr = cutStringByByte(tmparr[1], 11);
                            conMap.put("dataTRS13", moneyTransfer(tmparr[0], 11, 0));

                            // TRS14 13
                            tmparr = cutStringByByte(tmparr[1], 13);
                            conMap.put("dataTRS14", moneyTransfer(tmparr[0], 13, 7, 6));

                            // TRS15 13
                            tmparr = cutStringByByte(tmparr[1], 13);
                            conMap.put("dataTRS15", moneyTransfer(tmparr[0], 13, 11));

                            // TRS16 1
                            tmparr = cutStringByByte(tmparr[1], 1);
                            conMap.put("dataTRS16", tmparr[0]);

                            // TRS17 1
                            tmparr = cutStringByByte(tmparr[1], 1);
                            conMap.put("dataTRS17", tmparr[0]);

                            // TRS18 8
                            tmparr = cutStringByByte(tmparr[1], 8);
                            Date datetsr18 = sd1.parse(tmparr[0]);
                            conMap.put("dataTRS18", sd2.format(datetsr18));

                            // TRS19 4
                            tmparr = cutStringByByte(tmparr[1], 4);
                            conMap.put("dataTRS19", tmparr[0].substring(0, 2) + ":" + tmparr[0].substring(2));

                            // TRS20 8
                            tmparr = cutStringByByte(tmparr[1], 8);
                            if ("00000000".equals(tmparr[0])) {
                                conMap.put("dataTRS20", "---");
                            } else {
                                Date datetsr20 = sd1.parse(tmparr[0]);
                                conMap.put("dataTRS20", sd2.format(datetsr20));
                            }

                            // TRS21 8
                            tmparr = cutStringByByte(tmparr[1], 8);
                            if ("00000000".equals(tmparr[0])) {
                                conMap.put("dataTRS21", "---");
                            } else {
                                Date datetsr21 = sd1.parse(tmparr[0]);
                                conMap.put("dataTRS21", sd2.format(datetsr21));
                            }

                            // TRS22 2
                            tmparr = cutStringByByte(tmparr[1], 2);
                            conMap.put("dataTRS22", tmparr[0]);

                        }

                        // formtype is 1 or 2 in
                        if (formtype != null) {

                            // check used
                            if (addrList != null && addrList.size() > 0) {

                                for (int k = 0; k < addrList.size(); k++) {
                                    if (addrList.get(k).equals(email)) {
                                        break;
                                    }
                                }
                            } else {
                                emailList.add(email);
                                custList.add(custId);
                            }
                        } else {
                            // formtype is 3
                            emailList.add(email);
                            custList.add(custId);

                        }

                        if (cmList.size() > 0) {
                            for (int v = 0; v < cmList.size(); v++) {
                                if (cmList.contains(email)) {
                                    continuePoint = true;
                                    break;
                                } else {
                                    continuePoint = false;
                                }
                            }
                        }

                        // 塞資料
                        conMap.put("form", subStringbyByte(tmpstr, 2, 1));

                        if (continuePoint) {
                            conMap.put("mail", email);
                            tempList.add(conMap);
                        }

                        contentList.add(conMap);

                        if (continuePoint) {
                            continue;
                        }
                        // check used
                    } else if ("#".equals(firstWord)) {
                        String email = subStringbyByte(tmpstr, 50, 3);
                        emailResultList.add(email);
                    }

                    if (!check) {
                        if (a == alist.size() - 1) {
                            // 要有2表示為完整資料
                            if (!"2".equals(firstWord)) {
                                emailList = new ArrayList<String>();
                                custList = new ArrayList<String>();
                                contentList = new ArrayList<Map>();
                            } else {
                                // CNT 資料筆數 len 6
                                conMap.put("dataCnt", subStringbyByte(tmpstr, 6, 9));
                            }
                        }
                    }

                }
            }

            dataMap.put("emailList", emailList);
            dataMap.put("custList", custList);
            dataMap.put("contentList", contentList);
            dataMap.put("emailResultList", emailResultList);
            dataMap.put("tempList", tempList);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataMap;
    }

    /**
     * 處理符合條件 email相同的部分，回傳重覆的部分
     *
     * @param dataList
     * @return List<String>
     * @author Camel
     * @date 2013/10/9 下午2:43:39
     */
    private List<String> dataList(List<String> dataList) {
        List<String> neList = new ArrayList<String>();
        List<String> newList = new ArrayList<String>();
        try {
            for (int i = 0; i < dataList.size(); i++) {
                if (!neList.contains(dataList.get(i))) {
                    neList.add(dataList.get(i));
                } else {
                    newList.add(dataList.get(i));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return neList;
    }

    private void mailapps(List<String> emailList, List<String> custList, List<Map> contentList,
                          List<Map> tempList, File file) {
        try {
            Properties props = new Properties();

            Session sendMailSession;
            sendMailSession = Session.getInstance(props, null);
            props.put("mail.smtp.host", SMTP_HOST);
            props.put("mail.smtp.timeout", "10000");

            boolean continuePoint = false;
            Map<String, String> conMap = new HashMap<String, String>();
            Map<String, List> tempMap = new HashMap<String, List>();
            Map<String, String> WasSendEmailMap = new HashMap<String, String>();

            for (int x = 0; x < emailList.size(); x++) {
                conMap = contentList.get(x);
                List alist = new ArrayList();

                if ("01".equals(conMap.get("form_typ"))) {
                    for (int k = 0; k < tempList.size(); k++) {
                        Map mm = tempList.get(k);

                        // 將相同email 的內容合併
                        if (emailList.get(x).equals(mm.get("mail")) && custList.get(x).equals(mm.get("dataPgt01"))) {
                            alist.add(mm);
                            tempMap.put(emailList.get(x), alist);
                        }
                    }
                }

                if (tempMap != null) {
                    if (WasSendEmailMap.get(emailList.get(x) + custList.get(x)) != null
                            && "01".equals(conMap.get("form_typ"))) {
                        continuePoint = true;
                        // break;
                    } else {
                        continuePoint = false;
                    }
                } else {
                    continuePoint = true;
                    // break;
                }

                if (continuePoint) {
                    continue;
                }

                MimeMessage message = new MimeMessage(sendMailSession);
                try {
                    message.setFrom(new InternetAddress(mailFrom(conMap.get("form_typ")), SMTP_AUTH_NAME, "Big5"));

                    if (StringUtils.isEmpty(emailList.get(x))) {
                        throw new Exception("NoReceiverError");
                    }
                    if (!emailList.get(x).matches("^[\\w-]+(\\.[\\w-]+)*@[\\w-]+(\\.[\\w-]+)+$")) {
                        throw new Exception("email Address Error format!");
                    }
                } catch (Exception ee) {
                    // 紀錄Email狀態為錯誤的資訊
                    record(getEmail(emailList.get(x)), TYPE_ERROR, checkLogLength(ee.getMessage()));
                    rwFile(file, TYPE_ERROR, emailList.get(x));
                    continue;
                }

                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(getEmail(emailList.get(x))));

                File titleFile = new File(MAIL_CONTENT_PATH + "/" + conMap.get("form_typ") + "/title.txt");

                StringBuffer sbu = new StringBuffer();

                InputStreamReader reader = new InputStreamReader(new FileInputStream(titleFile), "Big5");
                char[] ch = new char[1];
                int readLength = reader.read(ch);
                while (readLength >= 0) {
                    sbu.append(new String(ch));
                    readLength = reader.read(ch);
                }
                reader.close();

                // 設定主旨
                // 201501130105-02 Ray ETF交易新增委託失敗事件通知
                if ("03".equals(conMap.get("form_typ"))) {
                    sbu.setLength(0);
                    if ("4".equals(conMap.get("dataTRS16")) || "5".equals(conMap.get("dataTRS16"))) {
                        sbu.append("台北富邦商業銀行「海外ETF/海外股票」交易通知");
                    } else {
                        sbu.append("台北富邦商業銀行「海外ETF/海外股票」交易通知");
                    }
                }

                message.setSubject(MimeUtility.encodeWord(sbu.toString() + (getEmail("")), "Big5", "B"));

                // 文字部份，注意 img src 部份要用 cid:接下面附檔的header
                MimeBodyPart textPart = new MimeBodyPart();

                // 指定內文位置
                // System.getProperty("XXXXX")
                File inFile = new File(MAIL_CONTENT_PATH + "/" + conMap.get("form_typ") + "/formTest.html");

                /*
                 * BufferedReader buff = new BufferedReader(new FileReader(inFile));
                 *
                 * String strs = null; while ((strs = buff.readLine()) != null) {
                 * html.append(strs); }
                 */

                StringBuffer html = new StringBuffer();

                reader = new InputStreamReader(new FileInputStream(inFile), "Big5");
                readLength = reader.read(ch);
                while (readLength >= 0) {
                    html.append(new String(ch));
                    readLength = reader.read(ch);
                }
                reader.close();

                boolean remarkType = false;
                Calendar cc = Calendar.getInstance();

                String tmpStr = "";
                if (tempMap.get(emailList.get(x)) != null) {

                    StringBuffer newLine = new StringBuffer();

                    List<Map> tlist = tempMap.get(emailList.get(x));

                    remarkType = checkRemarkType(tlist);

                    for (int k = 0; k < tlist.size(); k++) {
                        Map mm = tlist.get(k);

                        // html tag
                        if (k % 2 == 0) {
                            newLine.append("<tr bgcolor=\"#CCCCCC\" class=\"a1\">");
                        } else {
                            newLine.append("<tr class=\"a1\">");
                        }
                        newLine.append("<td class=\"style1\" align=\"center\">" + mm.get("dataPgt02") + "</td>");
                        newLine.append("<td class=\"style1\" align=\"center\">" + mm.get("dataPgt02a") + "</td>");
                        newLine.append("<td class=\"style1\" align=\"center\">" + mm.get("dataPgt08") + "</td>");
                        newLine.append("<td class=\"style1\" align=\"center\">" + mm.get("dataPgt05") + "</td>");
                        newLine.append("<td class=\"style1\" align=\"center\">" + mm.get("dataPgt06") + "</td>");
                        if ("1".equals(mm.get("dataPgt03"))) {
                            newLine.append("<td class=\"style1\" align=\"center\">高於(含)指定價格</td>");
                        } else if ("2".equals(mm.get("dataPgt03"))) {
                            newLine.append("<td class=\"style1\" align=\"center\">低於(含)指定價格</td>");
                        } else {
                            newLine.append("<td class=\"style1\" align=\"center\"></td>");
                        }
                        newLine.append("<td class=\"style1\" align=\"center\">" + mm.get("dataPgt04") + "</td>");

                        if ("1".equals(mm.get("dataPgt09"))) {
                            newLine.append(
                                    "<td class=\"style1\" align=\"left\">追蹤標的目前暫停交易，最新報價為參考報價日期所示價格；詳情請洽所屬專員或洽本行客服 </td>");
                        } else {
                            if (remarkType) {
                                newLine.append("<td class=\"style1\" align=\"left\">　" + "</td>");
                            }
                        }
                        newLine.append("</tr>");
                    }

                    tmpStr = html.toString().replace("$newLine", newLine);

                } else {
                    tmpStr = html.toString().replace("$newLine", "");
                }

                if (remarkType) {
                    tmpStr = tmpStr.replace("$tbWidth", "900px");
                    tmpStr = tmpStr.replace("$remarkLine",
                            "<td width=\"20%\" align=\"center\" class=\"main-title\">備註</td>");
                    tmpStr = tmpStr.replace("$colspan", "8");
                } else {
                    tmpStr = tmpStr.replace("$tbWidth", "720px");
                    tmpStr = tmpStr.replace("$remarkLine", "");
                    tmpStr = tmpStr.replace("$colspan", "7");

                }

                tmpStr = tmpStr.replace("$T_YYY", String.valueOf(cc.get(Calendar.YEAR) - 1911));
                tmpStr = tmpStr.replace("$T_MM", String.valueOf(cc.get(Calendar.MONTH) + 1));
                tmpStr = tmpStr.replace("$T_DD", String.valueOf(cc.get(Calendar.DATE)));
                // 201405050264-08 Ray 信件圖片參數化
                tmpStr = tmpStr.replace("$ad1_img", adsInfo.get("ad1_img"));
                tmpStr = tmpStr.replace("$ad1_lnk", adsInfo.get("ad1_lnk"));
                tmpStr = tmpStr.replace("$ad2_img", adsInfo.get("ad2_img"));
                tmpStr = tmpStr.replace("$ad2_lnk", adsInfo.get("ad2_lnk"));
                tmpStr = tmpStr.replace("$ad3_img", adsInfo.get("ad3_img"));
                tmpStr = tmpStr.replace("$ad3_lnk", adsInfo.get("ad3_lnk"));
                tmpStr = tmpStr.replace("$ad4_img", adsInfo.get("ad4_img"));
                tmpStr = tmpStr.replace("$ad4_lnk", adsInfo.get("ad4_lnk"));

                html = new StringBuffer(tmpStr);

                Multipart multiPart = new MimeMultipart("related");

                // 圖檔部份，注意 html 用 cid:image，則header要設<image>
                MimeBodyPart picturePart1 = new MimeBodyPart();

                // 01 multi data
                if ("01".equals(conMap.get("form_typ"))) {
                    textPart.setContent(html.toString(), "text/html;charset=Big5");
                    multiPart.addBodyPart(textPart);

                    if (html.toString().indexOf("image4") != -1) {
                        picturePart1 = new MimeBodyPart();
                        File picFile = new File(MAIL_CONTENT_PATH + "/" + conMap.get("form_typ") + "/AcctBalInq.jpg");
                        FileDataSource fds = new FileDataSource(picFile);
                        picturePart1.setDataHandler(new DataHandler(fds));
                        picturePart1.addHeader("Content-ID", "<image4>");
                        multiPart.addBodyPart(picturePart1);
                    }

                    if (html.toString().indexOf("image5") != -1) {
                        picturePart1 = new MimeBodyPart();
                        File picFile = new File(MAIL_CONTENT_PATH + "/" + conMap.get("form_typ") + "/PriceNotice.jpg");
                        FileDataSource fds = new FileDataSource(picFile);
                        picturePart1.setDataHandler(new DataHandler(fds));
                        picturePart1.addHeader("Content-ID", "<image5>");
                        multiPart.addBodyPart(picturePart1);
                    }
                } else if ("02".equals(conMap.get("form_typ"))) {
                    // 替換內容
                    tmpStr = html.toString().replace("$YYY", conMap.get("dataYYY"));
                    tmpStr = tmpStr.replace("$MM", conMap.get("dataMM"));
                    tmpStr = tmpStr.replace("$DD", conMap.get("dataDD"));
                    tmpStr = tmpStr.replace("$ACNO", conMap.get("dataAcno"));
                    tmpStr = tmpStr.replace("$CUR", conMap.get("dataCur"));
                    tmpStr = tmpStr.replace("$OD_AMT", conMap.get("dataOd_amt"));

                    html = new StringBuffer(tmpStr);
                    textPart.setContent(html.toString(), "text/html;charset=Big5");
                    multiPart.addBodyPart(textPart);

                    if (html.toString().indexOf("image1") != -1) {
                        picturePart1 = new MimeBodyPart();
                        File picFile = new File(MAIL_CONTENT_PATH + "/" + conMap.get("form_typ") + "/logo.JPG");

                        FileDataSource fds = new FileDataSource(picFile);
                        picturePart1.setDataHandler(new DataHandler(fds));
                        // picturePart3.setFileName(fds.getName());
                        picturePart1.addHeader("Content-ID", "<image1>");

                        multiPart.addBodyPart(picturePart1);
                    }
                } else if ("03".equals(conMap.get("form_typ")) || "09".equals(conMap.get("form_typ"))) {
                    tmpStr = html.toString();

                    tmpStr = tmpStr.replace("$DATA01", conMap.get("dataTRS08") + "(" + conMap.get("dataTRS07") + ")");
                    String strtsr09 = null;
                    String data9 = null;
                    if ("B".equals(conMap.get("dataTRS09").toUpperCase())) {
                        strtsr09 = "買進";
                        data9 = "扣款";
                    } else {
                        strtsr09 = "賣出";
                        data9 = "入帳";
                    }
                    String titleStr = "您於$DATETIME委託本行買賣海外ETF/海外股票的結果如下：";
                    String data13Str = conMap.get("dataTRS13") + "股";
                    String data7Str = conMap.get("dataTRS10") + " " + conMap.get("dataTRS14");
                    String data8Str = conMap.get("dataTRS10") + " " + conMap.get("dataTRS15");
                    String trs16Str = "<tr><td class=\"style1\">成交日期</td>	<td class=\"style1\">$DATA21</td></tr><tr><td class=\"style1\">成交股數</td>	<td class=\"style1\">$DATA13</td></tr><tr><td class=\"style1\">成交價格</td>	<td class=\"style1\">$DATA07</td></tr><tr><td class=\"style1\">預計$DATA09金額</td><td class=\"style1\">$DATA08</td></tr>";
                    String noticeStr = "請注意：以上為系統發出之交易結果，本交易之預計$DATA09金額非實際$DATA09金額，實際結果以證券商回報之成交確認資料為準；惟因各交易市場交易回報時間不同，您可於次一營業日至本行網路銀行查詢或電洽客服詢問實際結果。";
                    if ("1".equals(conMap.get("dataTRS16"))) {
                        // ~
                    } else if ("2".equals(conMap.get("dataTRS16"))) {
                        // ~
                    } else if ("3".equals(conMap.get("dataTRS16"))) {
                        data13Str = "---";
                        data7Str = "---";
                        data8Str = "---";
                    } else if ("4".equals(conMap.get("dataTRS16"))) {
                        trs16Str = "";
                        noticeStr = "很抱歉通知您，本次交易委託並未完成，如您欲再進行交易，請重新進行交易指示。如因資訊系統故障或線路中斷而無法立即修復時，本行可能暫停受理委託服務，造成不便，尚祈見諒。";
                    } else if ("5".equals(conMap.get("dataTRS16"))) {
                        trs16Str = "";
                        noticeStr = "很抱歉通知您，本次交易委託並未完成，如您欲再進行交易，請重新進行交易指示。如因資訊系統故障或線路中斷而無法立即修復時，本行可能暫停受理委託服務，造成不便，尚祈見諒。";
                    } else if ("6".equals(conMap.get("dataTRS16"))) {
                        trs16Str = "";
                        noticeStr = "請注意：以上為系統發出之交易結果，您可至行動銀行、網路銀行查詢或電洽客服詢問實際結果。";
                    } else if ("9".equals(conMap.get("dataTRS16"))) {
                        titleStr = "您於$DATETIME委託台北富邦銀行買賣海外ETF/海外股票交易指示中止，係<br>因發行公司活動─$DATA22，交易結果如下。";
                        trs16Str = "";
                        noticeStr = "如您欲再進行交易，請於本行完成公司活動處理後，重新進行交易指示。";
                    }

                    String strtsr16 = "";
                    String data16 = conMap.get("dataTRS16");
                    if ("1".equals(data16)) {
                        strtsr16 = "  全部成交";
                    } else if ("2".equals(data16)) {
                        strtsr16 = "  部分成交";
                    } else if ("3".equals(data16)) {
                        strtsr16 = "  不成交";
                    } else if ("4".equals(data16)) {
                        strtsr16 = "  委託失敗";
                    } else if ("5".equals(data16)) {
                        strtsr16 = "取消  失敗";
                    } else if ("6".equals(data16)) {
                        strtsr16 = "取消  成功";
                    } else if ("9".equals(data16)) {
                        strtsr16 = "  委託中止";
                    }

                    String strtsr17 = null;
                    if ("0".equals(conMap.get("dataTRS17"))) {
                        strtsr17 = "約定限價";
                    } else if ("1".equals(conMap.get("dataTRS17"))) {
                        strtsr17 = "市價";
                    } else {
                        strtsr17 = "限價";
                    }

                    String data22 = "";
                    String data22Str = "";
                    try {
                        data22 = conMap.get("dataTRS22").trim();
                    } catch (Exception e) {
                    }
                    if ("1".equals(data22) || "3".equals(data22)) {
                        data22Str = "配股";
                    } else if ("7".equals(data22)) {
                        data22Str = "減資";
                    } else if ("5".equals(data22) || "61".equals(data22)) {
                        data22Str = "股票合併(反分割)";
                    } else if ("62".equals(data22)) {
                        data22Str = "股票分割";
                    }

                    tmpStr = tmpStr.replace("$TITLE", titleStr);
                    tmpStr = tmpStr.replace("$DATETIME", conMap.get("dataTRS18") + "  " + conMap.get("dataTRS19"));
                    tmpStr = tmpStr.replace("$DATA02", strtsr09 + strtsr16);
                    tmpStr = tmpStr.replace("$DATA03", conMap.get("dataTRS05"));
                    tmpStr = tmpStr.replace("$DATA04", conMap.get("dataTRS11") + "股");
                    tmpStr = tmpStr.replace("$trs16Str", trs16Str);
                    tmpStr = tmpStr.replace("$noticeStr", noticeStr);
                    tmpStr = tmpStr.replace("$DATA05",
                            strtsr17 + "，" + conMap.get("dataTRS10") + " " + conMap.get("dataTRS12"));
                    tmpStr = tmpStr.replace("$DATA06", conMap.get("dataTRS06"));
                    tmpStr = tmpStr.replace("$DATA07", data7Str);
                    tmpStr = tmpStr.replace("$DATA08", data8Str);
                    tmpStr = tmpStr.replace("$DATA09", data9);
                    tmpStr = tmpStr.replace("$DATA13", data13Str);
                    tmpStr = tmpStr.replace("$DATA20", conMap.get("dataTRS20"));
                    tmpStr = tmpStr.replace("$DATA21", conMap.get("dataTRS21"));
                    tmpStr = tmpStr.replace("$DATA22", data22Str);

                    textPart.setContent(tmpStr, "text/html;charset=Big5");
                    multiPart.addBodyPart(textPart);
                }

                message.setContent(multiPart, "text/html;charset=Big5");

                // 驗證
                // System.setProperty("javax.net.ssl.trustStore","C:/jdk1.6.0_14/jre/lib/security/jssecacerts");//

                postMail(message, file, emailList.get(x), conMap);

                // same eamil to empty
                tempMap = new HashMap<String, List>();
                WasSendEmailMap.put(emailList.get(x) + custList.get(x), emailList.get(x));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** 如果job參數有指定email，返回指定email，反之使用檔案中的email **/
    private String getEmail(String email) {
        return isAssigned ? assignedEmail : email;
    }

    /**
     * 確保LOG能在限制字數以內
     **/
    private String checkLogLength(String message) {
        if (message.length() > LOG_LENGTH) {
            return message.substring(0, LOG_LENGTH);
        }
        return message;
    }

    /**
     * 紀錄寄送資訊
     **/
    private void record(String email, String status, String log) {
        String sql = new StringBuffer()
                .append("insert into TBOTH_MAILAPP_LOG(EMAIL, STATUS, LOG, VERSION, CREATOR, CREATETIME, MODIFIER, LASTUPDATE)")
                .append("values (:email, :status, :log, 0, 'SYSTEM', sysdate, 'SYSTEM', sysdate)")
                .toString();
        Map params = new HashMap();
        params.put("email", email);
        params.put("status", status);
        params.put("log", log);
        try {
            exeUpdateForMap(sql, params);
        } catch (JBranchException e) {
            e.printStackTrace();
        }
    }

    /**
     * 遞迴將檔案列出
     *
     * @param file
     * @return List<String>
     * @author Camel
     * @date 2013/10/3 下午4:01:33
     */
    private List<String> filelList(File file, List<String> chkList) {

        File target = new File(file.getPath());
        if (target.isDirectory()) {
            File[] files = target.listFiles();
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    filelList(files[i], chkList);
                }
            }

        } else {
            chkList.add(target.getPath());
        }

        return chkList;
    }

    private void postMail(MimeMessage message, File fileName, String to, Map<String, String> conMap) {
        try {
            Transport.send(message);
            // 紀錄Email狀態為寄送成功的資訊
            record(getEmail(to), TYPE_OK, "寄送成功");
            rwFile(fileName, TYPE_OK, to);
        } catch (Exception e) {
            // 紀錄Email狀態為寄送失敗的資訊
            record(getEmail(to), TYPE_FAIL, checkLogLength(e.getMessage()));
        }
    }

    private void rwFile(File file, String msg, String address) {  // TODO 看起來是產生result檔，紀錄成功寄送或寄送失敗，但其他地方邏輯有用到result檔，先不刪除
        try {
            // String fileName = file.getName().substring(0,file.getName().indexOf("."));
            String fileName = file.getName();
            String date = "_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            File filechk = new File(file.getParent() + "/" + fileName + date + ".result");

            if (!filechk.exists()) {
                filechk.createNewFile();
            }
            BufferedReader buff = new BufferedReader(new FileReader(filechk));

            StringBuffer sb = new StringBuffer();
            String str = "";
            while ((str = buff.readLine()) != null) {
                sb.append(str + "\n");
            }
            Calendar cc = Calendar.getInstance();

            SimpleDateFormat sd = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

            if (msg.equals(TYPE_ERROR)) {
                sb.append(StringUtils.rightPad("!", 3, "") + StringUtils.rightPad(address, 50, "")
                        + sd.format(cc.getTime()) + "....is " + msg + "\n");
            } else {
                sb.append(StringUtils.rightPad("#", 3, "") + StringUtils.rightPad(address, 50, "")
                        + sd.format(cc.getTime()) + "....is " + msg + "\n");

            }

            buff.close();

            BufferedWriter bw = new BufferedWriter(new FileWriter(filechk));

            bw.write(sb.toString());
            bw.flush();
            bw.close();

        } catch (Exception ee) {
            ee.printStackTrace();
        }
    }

    /**
     * 2種情況，有result檔跟沒有result檔，有的話先讀取資料判斷，沒有則將跑eamil送發流程
     *
     * @param fileList
     * @return List<String>
     * @author Camel
     * @date 2013/10/4 下午2:58:21
     */
    private boolean chekTempFolder(List<String> fileList) {
        List<String> tresList = new ArrayList<String>();
        List<String> nList = new ArrayList<String>();
        List<String> newFileList = new ArrayList<String>();
        List<String> addrList = new ArrayList<String>();
        List<String> emailList = new ArrayList<String>();
        List<String> emailResultList = new ArrayList<String>();
        boolean checkType = false;
        boolean checkFolder = false;
        try {

            // 先過濾檔案，有.result與沒有.result的檔區隔開來
            for (int s = 0; s < fileList.size(); s++) {
                File filee = new File(fileList.get(s));

                String fileName = filee.getName();
                if (fileName.indexOf(".result") != -1) {
                    tresList.add(fileList.get(s));
                } else {
                    nList.add(fileList.get(s));
                }
            }

            String[] fileArray = new String[fileList.size()];
            fileList.toArray(fileArray);

            for (int s = 0; s < fileArray.length; s++) {
                for (int i = 0; i < tresList.size(); i++) {
                    File fileN = new File(tresList.get(i));
                    BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileN), "Big5"));

                    String str = "";

                    List<String> alist = new ArrayList<String>();

                    while ((str = in.readLine()) != null) {
                        alist.add(str);
                    }
                    in.close();

                    // 先處理.result 資料
                    Map dataMap = dataOutPut(alist, null, true);

                    emailResultList = (List) dataMap.get("emailResultList");

                    // 原檔案
                    String fileName = fileN.getName().substring(0, fileN.getName().indexOf(".result"));

                    String fileArrayName = fileArray[s].substring(fileArray[s].lastIndexOf("\\") + 1);

                    if (fileArray[s].indexOf(".result") == -1) {
                        File checkFile = new File(fileArray[s] + ".result");
                        if (!checkFile.exists()) {
                            checkFolder = true;
                        }
                    }

                    if (!fileName.equals(fileArrayName)) {
                        continue;
                    }

                    File filechk = new File(fileN.getParent() + "/" + fileName);

                    in = new BufferedReader(new InputStreamReader(new FileInputStream(filechk), "Big5"));

                    str = "";

                    alist = new ArrayList<String>();

                    while ((str = in.readLine()) != null) {
                        alist.add(str);
                    }
                    in.close();

                    // 處理資料
                    dataMap = dataOutPut(alist, null, false);
                    emailList = (List) dataMap.get("emailList");

                    for (int m = 0; m < emailList.size(); m++) {
                        String email = emailList.get(m);
                        for (int k = 0; k < emailResultList.size(); k++) {
                            if (StringUtils.isNotEmpty(email) && emailResultList.contains(email)) {
                                addrList.add(email);
                            }
                        }
                    }

                    if (fileArray[s].equals(tresList.get(i).substring(0, tresList.get(i).indexOf(".result")))) {
                        continue;
                    } else {
                        if (fileArray[s].equals(tresList.get(i))) {
                            if (!emailList.equals(emailResultList)) {
                                sendMail(fileList, addrList);
                            }
                        } else {
                            sendMail(fileList, null);
                        }
                    }
                }
            }

            if (checkFolder) {
                for (int i = 0; i < nList.size(); i++) {
                    File checkFile = new File(nList.get(i) + ".result");
                    if (!checkFile.exists()) {
                        newFileList.add(nList.get(i));
                    }
                }

                if (newFileList.size() > 0) {
                    sendMail(newFileList, null);
                }
            }

            if (tresList.size() == 0) {
                sendMail(fileList, null);
            }

            checkType = true;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return checkType;
    }

    private void copyfile(String srFile, String dtFile) {
        try {
            File f1 = new File(srFile);
            File f2 = new File(dtFile);
            InputStream in = new FileInputStream(f1);

            // For Append the file.
            // OutputStream out = new FileOutputStream(f2,true);

            // For Overwrite the file.
            OutputStream out = new FileOutputStream(f2);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteAll(File file, String sourPath) {
        File[] deleteFiles = file.listFiles();

        if (deleteFiles.length == 0) {
            return;
        } else {
            for (File souFil : deleteFiles) {
                if (souFil.isDirectory()) {
                    deleteAll(souFil.getAbsoluteFile(), sourPath);
                    souFil.delete();
                } else {
                    souFil.delete();
                }
            }
        }
    }

    private void moveFile(File file, String backFilePath) {

        File[] pfileName = file.listFiles();

        // String backFilePath = "C:/Users/camel/Desktop/backFile/";

        if (pfileName.length > 0) {
            File backFile = new File(backFilePath);

            if (!backFile.exists()) {
                backFile.mkdir();
            }

            for (File sourceFile : pfileName) {
                if (sourceFile.isFile()) {

                    this.copyfile(sourceFile.getPath(), backFilePath + "/" + sourceFile.getName());
                } else {
                    this.moveFile(sourceFile, backFilePath + "/" + sourceFile.getName());
                }
            }
        }
    }

    /**
     * @param orgStr     source String
     * @param byteLength
     * @param startPoint
     * @return
     */

    private String subStringbyByte(String orgStr, int byteLength, int startPoint) {
        try {
            byte[] strBytes = orgStr.getBytes();
            byte[] bytes = new byte[byteLength];

            /*
             * if((orgStr.substring(startPoint,startPoint+byteLength)).getBytes().length/
             * byteLength==1){ System.arraycopy(strBytes, startPoint, bytes, 0, byteLength);
             * }else{ byteLength=byteLength*2; bytes = new byte[byteLength];
             * System.arraycopy(strBytes, startPoint, bytes, 0, byteLength); }
             */
            System.arraycopy(strBytes, startPoint, bytes, 0, byteLength);

            return new String(bytes).trim();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";

    }

    /**
     * 照傳入長度依byte切割字串.
     *
     * @param in 傳入字串
     * @param cut 切割長度
     * @param b  true,遇中文少兩位; false,遇中文不少兩位直接切割
     * @return 長度2的字串陣列，元素0是結果，元素1是後半字串
     * @author malo
     * @see #cutStringByByte(String, int)
     */
    private String[] cutStringByByte(String in, int cut) {
        int total = 0;
        if (in == null) {
            return null;
        }
        String[] out = new String[2];

        for (int i = 0; i < in.length(); i++) {
            boolean isDBCS = isDoubleByte(in.charAt(i));
            total = isDBCS ? total + 2 : total + 1;
            if (total > cut) {
                out[0] = in.substring(0, i).trim();
                out[1] = in.substring(i);
                return out;
            }
        }
        out[0] = in;
        out[1] = null;
        return out;
    }

    /**
     * 判斷傳入的 char 是否為 DoubleByte 字元.
     *
     * @param c 傳入 char 字元
     * @return 是否為 DoubleByte 字元
     * @author Michelle Chiang
     */
    private boolean isDoubleByte(char ch) {
        if (ch < 127) {
            return false;
        } else if (ch > 256) {
            return true;
        } else {
            try {
                return (ch + "").getBytes().length == 2;
            } catch (Exception ex) {
            }
        }
        return false;
    }

    private String mailFrom(String formType) {

        String mailfrom = (String) mailInfo.get("form" + formType);
        try {
            // if error , get defaultform
            mailfrom = mailfrom == null ? DEFAULT_FORM : mailfrom;
            if (!mailfrom.matches("^[\\w-]+(\\.[\\w-]+)*@[\\w-]+(\\.[\\w-]+)+$")) {
                mailfrom = DEFAULT_FORM;

                if (!mailfrom.matches("^[\\w-]+(\\.[\\w-]+)*@[\\w-]+(\\.[\\w-]+)+$")) {
                    throw new Exception("email From Address Error format!");
                }
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
        return mailfrom;
    }

    private String dateTransfer(String strDate) {
        try {
            // len 8 01021031
            strDate = Integer.valueOf(strDate.substring(0, 4)) + "/" + strDate.substring(4, 6) + "/"
                    + strDate.substring(6, 8);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return strDate;
    }

    private String moneyTransfer(String strMoney) {
        return moneyTransfer(strMoney, 13, 7);
    }

    private String moneyTransfer(String strMoney, int length, int subLength) {
        return moneyTransfer(strMoney, length, subLength, 2);
    }

    private String moneyTransfer(String strMoney, int length, int subLength, int digiLength) {
        String format = "##,###.";
        for (int i = 0; i < digiLength; i++) {
            format += "#";
        }
        DecimalFormat df = new DecimalFormat(format);
        BigDecimal bd = null;
        try {
            if (subLength > 0) {
                strMoney = strMoney.substring(0, subLength) + "." + strMoney.substring(subLength, length);
            }
            bd = new BigDecimal(strMoney);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return df.format(bd);
    }

    /**
     * 檢查 dataPgt09資料是否有 值
     *
     * @param alist
     * @return boolean
     * @author Camel
     * @date 2013/11/6 下午6:36:17
     */
    private boolean checkRemarkType(List<Map> alist) {

        boolean remarkType = false;
        try {
            for (int k = 0; k < alist.size(); k++) {
                Map mm = alist.get(k);
                if ("1".equals(mm.get("dataPgt09"))) {
                    remarkType = true;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return remarkType;
    }
}