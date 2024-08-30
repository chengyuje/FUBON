package com.systex.jbranch.app.server.fps.kycoperation;

import com.systex.jbranch.fubon.commons.Manager;
import com.systex.jbranch.fubon.commons.mail.MailConfig;
import com.systex.jbranch.fubon.commons.mail.MailService;
import com.systex.jbranch.fubon.webservice.rs.KycOperation;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.util.IPrimitiveMap;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.apache.commons.lang.StringUtils.defaultIfEmpty;
import static org.apache.commons.lang.StringUtils.join;

@Component
@Scope("prototype")
public class KycMonitor extends BizLogic {
    /**
     * 利用 KycOperation.java 的 testForm 功能（選擇 recordRisk 試算）模擬發送
     * 當發生異常時，立即發信通知
     */
    public void monitorKycWS(Object body, IPrimitiveMap<?> header) throws Exception {
        // get Monitor information with Map
        Map<String, String> monitorMap = getMonitorMap();
        // configure questionnaire information
        Map<String, String> questionnaireMap = getQuestionnairMap(monitorMap.get("QST_TYPE"));

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");

        // get monitor urls and use to connect kyc api
        for (String url : getKycWSUrls(monitorMap)) {
            String layout = monitorMap.get("MAIL_LAYOUT");
            String finalSendingUrl = getFinalSendingUrl(url, questionnaireMap);

            layout = layout.replaceAll("#url", url)
                    .replaceAll("#sendUrl", finalSendingUrl)
                    .replaceAll("#start", dateFormat.format(new Date()));
            String xml = "";
            String err = "";
            try {
                xml = callApiByUrl(finalSendingUrl);
            } catch (Exception e) {
                err = e.getMessage();
            }

            // 模擬呼叫過程中發生的錯誤也一併通知
            if (hasError(xml) || StringUtils.isNotBlank(err)) {
                layout = layout.replaceAll("#errorTime", dateFormat.format(new Date()))
                        .replaceAll("#errorMsg", defaultIfEmpty(xml, err));
                try {
                    mail(monitorMap, layout);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private List<String> getKycWSUrls(Map<String, String> monitorMap) {
        List<String> urls = new ArrayList();
        for (Map.Entry<String, String> entry : monitorMap.entrySet()) {
            // monitor url starts with URL
            if (entry.getKey().startsWith("URL"))
                urls.add(entry.getValue());
        }
        return urls;
    }

    private Map<String, String> getQuestionnairMap(String qstType) throws JBranchException {
        List<String> questionList = new ArrayList();
        List<String> answerList = new ArrayList();
        Map<String, String> map = new HashMap();
        // 問卷類型會依照客戶ID的長度來判斷，如果長度>=10，代表是自然人（02），否則是法人（03）。這邊依照設定的問卷類型設定傳入參數 custId 的長度
        map.put("examID", configureQA(StringUtils.rightPad("#", qstType.equals("02") ? 10 : 8, "#"), questionList, answerList));
        map.put("question", join(questionList, "^"));
        map.put("ans", codeAnswer(join(answerList, "^")));
        return map;
    }

    private Map<String, String> getMonitorMap() throws JBranchException {
        List<Map<String, String>> data = Manager.manage(this.getDataAccessManager())
                .append("select PARAM_CODE, PARAM_NAME from TBSYSPARAMETER ")
                .append("where PARAM_TYPE = 'KYC.WS_MONITOR' ")
                .query();
        Map<String, String> monitorMap = new HashMap();
        for (Map<String, String> map : data)
            monitorMap.put(map.get("PARAM_CODE"), map.get("PARAM_NAME"));

        return monitorMap;
    }

    /**
     * 將參數設定的 URL 置換成可以得到最高風險屬性的問卷相關參數，如果該 URL 沒有設定相關參數可替換，則使用該 URL 發送
     **/
    private String getFinalSendingUrl(String url, Map<String, String> questionnaireMap) {
        return url.replaceAll("#examID", questionnaireMap.get("examID"))
                .replaceAll("#generalQuestion", questionnaireMap.get("question"))
                .replaceAll("#generalAns", questionnaireMap.get("ans"));
    }

    private void mail(Map<String, String> monitorMap, String xml) throws Exception {
        MailConfig cfg = new MailConfig();
        cfg.setMailToMap(getReceiverMap(monitorMap.get("RECEIVERS")));
        cfg.setSubject(monitorMap.get("MAIL_SUBJECT"));
        cfg.setContent(xml);
        PlatformContext.getBean(MailService.class)
                .send(cfg);
    }

    private Map getReceiverMap(String receivers) {
        String[] group = receivers.split(";"); // 多筆 email 以 ';' 分隔
        Map map = new HashMap();
        for (String email : group) {
            map.put(email, "");
        }
        return map;
    }

    /**
     * KycOperation.java 錯誤時會呼叫 errorXmlContent 方法，取得裡面的關鍵字判斷此次呼叫是否有發生錯誤
     **/
    private boolean hasError(String xml) {
        return xml.contains("<soapenv:Fault>");
    }

    /**
     * 排程呼叫不是透過 Web Request，因此不能直接呼叫 KycOperation.testForm，會產生 Spring Scope 的相關錯誤
     * 因此利用連線物件來取得回傳的 XML
     **/
    public String callApiByUrl(String url) throws IOException, JBranchException {
        HttpURLConnection conn = getConn(url);

        try {
            conn.connect();
            return getResponseXml(conn.getInputStream());
        } finally {
            conn.disconnect();
        }
    }

    private String getResponseXml(InputStream inputStream) throws IOException {
        try (InputStream stream = inputStream) {
            return IOUtils.toString(stream, StandardCharsets.UTF_8.name());
        }
    }

    /**
     * 取得 KYC WS 連線物件
     **/
    private HttpURLConnection getConn(String targetIP) throws IOException {
        URL fileUrl = new URL(targetIP);
        HttpURLConnection conn = (HttpURLConnection) fileUrl.openConnection();
        conn.setDoInput(true);
        conn.setDoOutput(true);
        return conn;
    }

    /**
     * 直接呼叫程式
     **/
    public String callApi(String custId, String confirm, String ip) throws Exception {
        List<String> questionList = new ArrayList();
        List<String> answerList = new ArrayList();

        return new KycOperation().testForm("999", "999999", custId,
                configureQA(custId, questionList, answerList),
                join(questionList, "^"),
                codeAnswer(join(answerList, "^")), confirm, "recordRisk", ip);
    }

    /**
     * 配置問卷版本、題目序號集合以及最高分答案序號集合
     **/
    private String configureQA(String custId, List<String> questionList, List<String> answerList) throws JBranchException {
        int scoreTmp = 0; // 暫存分數，記錄前次最高答案分數 (為了*處使用)
        String ans = ""; // 紀錄答案序號
        String examId = ""; // 問卷版本

        for (Map<String, Object> map : queryQuestionAndAnswer(custId)) {
            if (StringUtils.isBlank(examId))
                examId = map.get("EXAM_VERSION").toString();

            // 如果題目序號集合內未包含此題目序號，則添加
            if (!questionList.contains(map.get("QUESTION_VERSION").toString())) {
                questionList.add(map.get("QUESTION_VERSION").toString());
                ans = processAns(ans, answerList); // 如果ans有值，代表已處理完一題，將答案處理後加到答案序號集合
                scoreTmp = 0; // 新的一題複選題其暫存分數歸零
            }
            // 答案分數如果為負數則忽略
            if (Integer.valueOf(map.get("FRACTION").toString()) < 0)
                continue;

            // 複選題為了取得最高分，將所有非負數答案序號相加
            if (map.get("QUESTION_TYPE").toString().equals("M"))
                ans += codePoint(map.get("ANSWER_SEQ").toString()) + "-";
                // *與前次分數scoreTmp比較，在一道題目的所有答案中找出分數最高的答案序號
            else if (Integer.valueOf(map.get("FRACTION").toString()) >= scoreTmp) {
                scoreTmp = Integer.valueOf(map.get("FRACTION").toString()); // 新的最高分
                ans = codePoint(map.get("ANSWER_SEQ").toString());
            }

        }
        // 當迴圈結束後，最後一題答案尚未處理，在此處理
        processAns(ans, answerList);
        return examId;
    }

    /**
     * 加入答案集合的處理邏輯
     **/
    private String processAns(String ans, List<String> answerList) {
        if (StringUtils.isNotBlank(ans)) {
            if (ans.contains("-"))
                ans = ans.substring(0, ans.length() - 1); // 複選題去掉尾端多餘符號
            answerList.add(ans);
            ans = "";
        }
        return ans;
    }

    /**
     * 轉為 Unicode 兩位以上在每一位轉 Unicode 後，以 "." 串接
     **/
    private String codePoint(String str) {
        List<String> result = new ArrayList<>();
        for (int index = 0; index < str.length(); index++) {
            result.add(String.valueOf(str.codePointAt(index)));
        }
        return join(result, ".");
    }


    /**
     * 將answerList組合成網銀可接受的參數格式 KycOperation#testForm
     **/
    private String codeAnswer(String answerStr) {
        return answerStr.replaceAll("\\^", ".94.").replaceAll("-", ".45.");
    }

    /**
     * 依客戶類型取得問卷題目與答案
     **/
    private List<Map<String, Object>> queryQuestionAndAnswer(String custId) throws JBranchException {
        return exeQueryWithoutSortForQcf(genDefaultQueryConditionIF().setQueryString(new StringBuilder().append("select QUES.EXAM_VERSION, ") // 問卷版本
                .append("       QUES.QUESTION_VERSION,") // 題目序號
                .append("       QT.QUESTION_TYPE, ") // 題目類型：S：單選選單、M：複選答案、N：數字格式、T：文字格式
                .append("       ANS.ANSWER_SEQ, ") // 答案序號
                .append("       WEIGHT.FRACTION ") // 分數
                // 問卷資料檔、題庫題目檔、題庫題目答案列表檔、答案權重檔
                .append("from TBSYS_QUESTIONNAIRE QUES, TBSYS_QST_QUESTION QT, TBSYS_QST_ANSWER ANS, TBKYC_QUESTIONNAIRE_ANS_WEIGHT WEIGHT ")
                // 表格鍵值對應
                .append("where QUES.QUESTION_VERSION = QT.QUESTION_VERSION ").append("and QUES.QUESTION_VERSION = ANS.QUESTION_VERSION ").append("and ANS.QUESTION_VERSION = WEIGHT.QUESTION_VERSION ").append("and ANS.ANSWER_SEQ = WEIGHT.ANSWER_SEQ ").append("and QUES.EXAM_VERSION = WEIGHT.EXAM_VERSION ")
                // 待啟用
                .append("and QUES.STATUS = '02' ")
                // 問卷類型
                .append("and QUES.QUEST_TYPE = :guestType ")
                // 找出待啟用日其小於等於系統日且最接近的那筆
                .append("and QUES.ACTIVE_DATE = ( ").append("    select max(ACTIVE_DATE) from TBSYS_QUESTIONNAIRE where STATUS = '02' ").append("    and QUEST_TYPE = :guestType and ACTIVE_DATE <= sysdate ").append(") order by QUES.QST_NO, ANS.ANSWER_SEQ ").toString()).setObject("guestType", custId.length() >= 10 ? "02" : "03"));
    }
}
