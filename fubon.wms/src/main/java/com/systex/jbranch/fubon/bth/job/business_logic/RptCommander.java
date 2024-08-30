package com.systex.jbranch.fubon.bth.job.business_logic;

import com.systex.jbranch.fubon.bth.job.context.AccessContext;
import com.systex.jbranch.fubon.bth.job.context.StoredContext;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.sql.Clob;
import java.util.*;

/**
 * @author Eli
 * @date 2018/08/24 負責指揮報表處理者{@link RptResolver}與報表寄送者 {@link RptSender}
 * @date 2018/11/02 RPT_SQL DB資料型態改為CLOB，增加對CLOB的處理
 * @date 2019/04/03 增加報表參數 NOT_ATTACHED（報表內容以附檔或內文呈現）
 */
@Component
@Scope("prototype")
public class RptCommander extends FubonWmsBizLogic {

    /**
     * 執行寄送報表
     *
     * @param mailType 報表代碼
     */
    public void mailReport(String mailType) throws Exception {
        RptSender sender = RptSender.getInstance();
        setSenderInfo(sender, getMailMap(mailType));
        sender.send();
    }

    /**
     * 從DB取得特定Mail參數
     **/
    private Map getMailMap(String arg) throws JBranchException {
        String sql = "select PARAM_CODE, PARAM_NAME from tbsysparameter where PARAM_TYPE = :rpt ";
        Map params = new HashMap();
        params.put("rpt", arg);
        List<Map<String, String>> result = exeQueryForMap(sql, params);

        Map mailMap = new HashMap();
        for (Map<String, String> map : result)
            mailMap.put(map.get("PARAM_CODE"), map.get("PARAM_NAME"));

        return mailMap;
    }

    /**
     * 設置sender相關資訊
     */
    private void setSenderInfo(RptSender sender, Map<String, String> mailMap) throws Exception {
        sender.setReports(generateReport(mailMap.get("RPT")));
        sender.setReceivers(toList(mailMap.get("RECEIVER")));
        sender.setPassword(procPassword(mailMap.get("ZIP_PASSWORD")));
        sender.setZipMailSubject(procValue(mailMap.get("ZIP_MAIL_SUBJECT")));
        sender.setZipMailContent(procValue(mailMap.get("ZIP_MAIL_CONTENT")));
        sender.setPasswordMailSubject(procValue(mailMap.get("PS_MAIL_SUBJECT")));
        sender.setPasswordMailContent(procValue(mailMap.get("PS_MAIL_CONTENT")));
        sender.setNotAttached(Boolean.valueOf(mailMap.get("NOT_ATTACHED")));
    }

    /**
     * 密碼邏輯處理
     */
    private String procPassword(Object password) {
        if ("default".equals(password)) return AccessContext.sdf.format(new Date());
        if (StringUtils.isBlank((String) password)) return null;
        return (String) password;
    }

    /**
     * 處理參數分隔符號，使成為List型態
     */
    private List toList(Object str) {
        return Arrays.asList(((String) str).split(";"));
    }

    /**
     * 將參數去空白，否則為 ""
     */
    private String procValue(Object obj) {
        try {
            return obj.toString().trim();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 根據參數reportCodes產出相關報表，並回傳有產出之報表名稱
     *
     * @param reportCodes 報表代碼
     * @return
     * @throws Exception
     */
    public List<String> generateReport(String reportCodes) throws Exception {
        String sql = "select * from TBSYSRPTSENDER where RPT_CODE = :rptCode ";
        List<String> reportList = new ArrayList();

        for (String rptCode : reportCodes.split(",")) {
            Map params = new HashMap();
            params.put("rptCode", rptCode);
            List<Map> report = exeQueryForMap(sql, params);

            RptResolver resolver = (RptResolver) PlatformContext.getBean("rptResolver");
            resolver.setConfig(CollectionUtils.isNotEmpty(report) ?
                    getReportConfigFromDB(report.get(0)) :  // 先從DB取得報表參數
                    getReportConfigFromClass(rptCode)); // 若DB沒資料，則從 CLASS 取得

            List<String> rpts = resolver.generate();
            if (CollectionUtils.isNotEmpty(rpts))
                for (String rpt : rpts)
                    reportList.add(new File(rpt).getName());
        }
        return reportList;
    }

    /**
     * 從 StoredContext 取得報表config參數
     **/
    private ReportConfig getReportConfigFromClass(String rptCode) throws Exception {
        StoredContext context = new StoredContext(rptCode);
        ReportConfig config = (ReportConfig) PlatformContext.getBean("reportConfig");
        config.setCode(rptCode);

        try {
            config.setName(context.getCsvFileName());
            config.setType("C");  // CSV
            config.setDelimiter(null);
            config.setNeedHeader("Y");
            config.setWidth(null);
        } catch (Exception e) {
            config.setName(context.getFileName());
            config.setType("T");  // TXT
            config.setDelimiter(context.getFileColWidth() != null ? "FIX" : "DOT");
            config.setNeedHeader("N");
            config.setWidth(config.getDelimiter().equals("FIX") ?
                    Arrays.toString(context.getFileColWidth()).replace("[", "").replace("]", "") : null);
        }
        config.setSql(context.getSQL());
        config.setSqlType("WMS");
        config.setNeedDoubleQuotes("N");
        config.setFileCoding("U"); // UTF-8
        return config;
    }

    /**
     * 從DB取得報表Config參數
     **/
    public ReportConfig getReportConfigFromDB(Map rptMap) throws Exception {
        ReportConfig config = (ReportConfig) PlatformContext.getBean("reportConfig");
        config.setCode((String) rptMap.get("RPT_CODE"));
        config.setDelimiter((String) rptMap.get("DELIMITER"));
        config.setName((String) rptMap.get("RPT_NAME"));
        config.setNeedHeader((String) rptMap.get("NEED_HEADER"));
        config.setSql(getSqlFromClob((Clob) rptMap.get("RPT_SQL")));
        config.setSqlType((String) rptMap.get("SQL_TYPE"));
        config.setType((String) rptMap.get("RPT_TYPE"));
        config.setWidth((String) rptMap.get("FIX_WIDTH"));
        config.setNeedDoubleQuotes((String) rptMap.get("NEED_DOUBLE_QUOTES"));
        config.setFileCoding((String) rptMap.get("FILE_CODING"));
        /** 驗證檔欄位 **/
        config.setChkFileName((String) rptMap.get("CHK_FILE_NAME"));
        config.setChkName((String) rptMap.get("CHK_NAME"));
        config.setChkDateFormat((String) rptMap.get("CHK_DATE_FORMAT"));
        config.setChkWidth((String) rptMap.get("CHK_WIDTH"));
        return config;
    }

    /**
     * 將Clob轉換為String
     *
     * @param sqlClob
     * @return
     * @throws Exception
     */
    public String getSqlFromClob(Clob sqlClob) throws Exception {
        StringBuffer sb = new StringBuffer();
        try (BufferedReader sqlReader = new BufferedReader(sqlClob.getCharacterStream())) {
            String line;
            while ((line = sqlReader.readLine()) != null) {
                sb.append(line + "\n");
            }
        }
        return sb.toString();
    }

}
