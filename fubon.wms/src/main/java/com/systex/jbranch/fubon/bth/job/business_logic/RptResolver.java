package com.systex.jbranch.fubon.bth.job.business_logic;

import com.systex.jbranch.fubon.bth.ftp.BthFtpJobUtil;
import com.systex.jbranch.fubon.bth.job.context.AccessContext;
import com.systex.jbranch.fubon.bth.job.context.ProcessContext;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.systex.jbranch.fubon.bth.job.context.AccessContext.*;

/**
 * @author Eli
 * @date 2018/07/30 產出報表服務
 * @date 2018/10/30 添加double quotation modifier and choose file charset
 * @date 2019/04/03 增加取得報表檔案的編碼方法，以及讀取報表內容方法
 * @date 2019/06/03 為提高產檔效率，直接使用 ResultSet 將報表資料寫入檔案裡
 */
@Component
@Scope("prototype")
@SuppressWarnings({"rawtypes", "unchecked"})
public class RptResolver extends FubonWmsBizLogic {
    /**
     * txt副檔名
     */
    private final String TXT_EXT = ".txt";
    /**
     * csv副檔名
     */
    private final String CSV_EXT = ".csv";
    /**
     * 報表配置
     */
    private ReportConfig config;

    private BthFtpJobUtil util = new BthFtpJobUtil();

    /**
     * 計算筆數SQL (SQL_SERVER)
     **/
    public static final String COUNT_SQLSERVER_SQL = "select count(*) CNT from ( #sql ) as a ";

    /**
     * 分組SQL (SQL_SERVER)
     **/
    public static final String GROUP_SQLSERVER_SQL
            = new StringBuffer()
            .append("select * from (                                       ")
            .append("    select ROW_NUMBER() over (order by a.OKEY) as RN, ")
            .append("    a.* from (#sql) a                                 ")
            .append(") as b                                                ")
            .append("where RN >= #db and RN <= #ub                         ")
            .toString();

    /**
     * 儲存格式化後的檔案名稱
     **/
    private String formattedFileName;

    public ReportConfig getConfig() {
        return config;
    }

    public void setConfig(ReportConfig config) {
        this.config = config;
    }

    /**
     * 依照給定的 config 報表參數，產出報表
     *
     * @return
     * @throws Exception
     */
    public List<String> generate() throws Exception {
        /** 產出資料檔 **/
        formatFileName();
        calWidthArray();
        qryDelimeter();
        qryQuotes();
        qryCharset();

        int dataCount = genFile();
        List<String> files = new ArrayList();
        files.add("temp/reports/" + config.getFormattedFileName());
        /** 若有驗證檔則產生 **/
        if (StringUtils.isNotBlank(config.getChkFileName()))
            files.add(generateChkFile(dataCount));
        return files;
    }

    /**
     * 直接使用 ResultSet 物件將資料寫入檔案中，此方法目前最有效率
     **/
    private int genFile() throws SQLException, IOException {
        int count = 0;
        try (Connection conn = ProcessContext.getConnection(config.getSqlType());
             PreparedStatement ps = conn.prepareStatement(config.getSql(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {
            ps.setFetchSize(3000);
            try (ResultSet rs = ps.executeQuery();
                 BufferedWriter bw = getFileToWrite(new File(tempReportPath, config.getFormattedFileName()), config.getCharset())
            ) {
                ResultSetMetaData rsmd = rs.getMetaData();
                /** header **/
                if (needHeader()) {
                    for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                        if (config.isDot())
                            bw.append(checkDoubleQuotes(nullIsSpace(rsmd.getColumnName(i)), config.isExistDoubleQuotes()))
                                    .append(i < rsmd.getColumnCount() ? "," : "");
                        else
                            bw.append(lenString(checkDoubleQuotes(rsmd.getColumnName(i), config.isExistDoubleQuotes()), config.getWidthArray()[i - 1]));
                    }
                    bw.newLine();
                }
                /** data **/
                while (rs.next()) {
                    for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                        if (config.isDot())
                            bw.append(checkDoubleQuotes(nullIsSpace(rs.getObject(rsmd.getColumnName(i))), config.isExistDoubleQuotes()))
                                    .append(i < rsmd.getColumnCount() ? "," : "");
                        else
                            bw.append(lenString(checkDoubleQuotes(nullIsSpace(rs.getObject(rsmd.getColumnName(i))), config.isExistDoubleQuotes()), config.getWidthArray()[i - 1]));
                    }
                    bw.newLine();
                    count++;
                }
                bw.flush();
            }
        }
        return count;
    }

    /**
     * null 則為 ""
     **/
    private String nullIsSpace(Object o) {
        if (null != o) return o.toString();
        else return "";
    }

    /**
     * 產生驗證檔
     **/
    private String generateChkFile(int dataCount) throws Exception {
        String[] widthArr = config.getChkWidth().split(",");
        String content = new StringBuilder()
                .append(StringUtils.rightPad(config.getChkName(), Integer.parseInt(widthArr[0]), ' '))
                .append(StringUtils.leftPad(String.valueOf(dataCount), Integer.parseInt(widthArr[1]), '0'))
                .append(StringUtils.rightPad(getChkFormattedDate(config.getChkDateFormat()), Integer.parseInt(widthArr[2]), ' '))
                .toString();

        formatCheckFileName();
        try (BufferedWriter bw = AccessContext.getFileToWrite(new File(tempReportPath, config.getFormattedCheckFileName()), config.getCharset())) {
            bw.append(content);
            bw.flush();
        }
        return "temp/reports/" + config.getFormattedCheckFileName();
    }

    /**
     * 取得驗證檔的日期內容
     **/
    private String getChkFormattedDate(String chkDateFormat) throws Exception {
        return util.getRealFileName(chkDateFormat);
    }

    /**
     * 查詢是否需要雙引號修飾資料
     **/
    private void qryQuotes() {
        config.setExistDoubleQuotes(config.getNeedDoubleQuotes().equals("Y"));
    }

    /**
     * 查詢使用的檔案編碼
     **/
    private void qryCharset() {
        config.setCharset(config.getFileCoding().equals("U") ? Charset.forName("UTF-8") : Charset.forName("MS950"));
    }

    /**
     * 將指定SQL 轉換成 GROPU SQL (分次撈取資料)
     **/
    public static String getGroupSQL(int index, int limit, String sql, String groupSql) {
        return groupSql.replace("#sql", sql)
                .replace("#ub", Integer.toString(index + limit))
                .replace("#db", Integer.toString(index + 1));
    }

    /**
     * 查詢是否需要','分隔
     **/
    private void qryDelimeter() {
        config.setDot(isCSV() || config.getDelimiter().equals("DOT"));
    }

    /**
     * 是否需要Header
     **/
    private boolean needHeader() {
        return config.getNeedHeader().equals("Y");
    }

    /**
     * 取得格式化後的檔案名稱
     *
     * @return
     */
    public String getFormattedFileName() {
        return formattedFileName;
    }

    /**
     * 格式化報表檔案名稱
     **/
    private void formatFileName() throws Exception {
        this.setFormattedFileName(util.getRealFileName(config.getName() + getExtName()));
    }

    /**
     * 格式化驗證檔檔名
     */
    private void formatCheckFileName() throws Exception {
        config.setFormattedCheckFileName(util.getRealFileName(config.getChkFileName()));
    }

    private String getExtName() {
        return isCSV() ? CSV_EXT : isTXT() ? TXT_EXT : "";
    }

    /**
     * 取得固定寬度
     **/
    private void calWidthArray() {
        if (isFIX()) config.setWidthArray(calWidthArray(config.getWidth().split(",")));
        else config.setWidthArray(null);
    }

    /**
     * 是否需要固定寬度
     **/
    private boolean isFIX() {
        return !isCSV() && config.getDelimiter().equals("FIX");
    }

    /**
     * 是否為CSV檔案
     **/
    private boolean isCSV() {
        return config.getType().equals("C");
    }

    /**
     * 是否為CSV檔案
     **/
    private boolean isTXT() {
        return config.getType().equals("T");
    }

    /**
     * 得到寬度陣列
     **/
    private int[] calWidthArray(String[] widthArray) {
        int[] widthIntegerArr = new int[widthArray.length];
        for (int i = 0; i < widthIntegerArr.length; i++) {
            widthIntegerArr[i] = Integer.valueOf(widthArray[i].trim());
        }
        return widthIntegerArr;
    }

    /**
     * 設定格式化報表名稱
     **/
    public void setFormattedFileName(String formattedFileName) {
        this.formattedFileName = formattedFileName;
        config.setFormattedFileName(formattedFileName);
    }

    /**
     * 取得檔案編碼
     **/
    public static Charset getFileCharset(Path file) throws IOException {
        byte[] b = new byte[3];
        try (InputStream is = Files.newInputStream(file)) {
            is.read(b);
        }
        /** 根據檔案前三位元碼來判斷檔案編碼 **/
        if (b[0] == -17 && b[1] == -69 && b[2] == -65) return StandardCharsets.UTF_8;
        /** 使用 UTF-8 編碼讀取檔案，若出錯則為 MS950 **/
        try (BufferedReader br = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            br.readLine();
            return StandardCharsets.UTF_8;
        } catch (IOException e) {
            return Charset.forName("MS950");
        }
    }

    /**
     * 取得報表內容
     **/
    public static void readReport(String filePath, StringBuilder builder) {
        Path file = Paths.get(AccessContext.tempReportPath + filePath);

        try (BufferedReader bd = Files.newBufferedReader(file, getFileCharset(file))) {
            String line;
            while ((line = bd.readLine()) != null)
                builder.append(line + "\n");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
