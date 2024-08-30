package com.systex.jbranch.fubon.bth.job.business_logic;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

/**
 * 報表內容設定
 *
 * @author Eli
 * @date 20180727
 * @date 20181026 添加檔案編碼與雙引號欄位
 */
@Component
@Scope("prototype")
public class ReportConfig {
    private String code; // 報表代號
    private String name; // 報表名稱
    private String type; // 報表種類
    private String desc; // 報表說明
    private String sql;  // SQL語法
    private String delimiter; // 分隔方式
    private String needHeader; // 是否需要標題
    private String sqlType; // SQL種類
    private String width;    // 固定寬度
    private String fileCoding; // 檔案編碼
    private String needDoubleQuotes; // 資料是否使用雙引號
    //====== 驗證檔欄位 ======
    private String chkFileName; // 驗證檔檔案名稱
    private String chkName;     // 驗證檔名稱
    private String chkWidth;    // 驗證檔固定寬度
    private String chkDateFormat;  // 驗證檔日期格式

    //====== 印製報表儲存參數 ======
    private String formattedFileName; // 格式化後的報表名稱
    private String[] headerCols;      // 報表Header陣列
    private int[] widthArray;         // 固定寬度陣列 (報表分隔方式採用固定)
    private List<Map<String, Object>> data; // 報表資料
    private boolean isDot;            // 資料是否採用,分隔
    private boolean existHeader;      // Header 是否存在
    private boolean existDoubleQuotes;// 是否需要雙引號修飾資料
    private boolean append;           // 是否寫入已存在檔案
    private Charset charset;          // 檔案編碼
    private String formattedCheckFileName; // 格式化後的驗證檔名稱


    /**
     * 預設報表儲存參數，不需要header 、 雙引號 ， 覆寫檔案 ， UTF-8 編碼
     **/
    public ReportConfig configureDefaultArg(String formattedFileName,
                                            String[] headerCols,
                                            int[] widthArray,
                                            List<Map<String, Object>> data,
                                            boolean isDot) {
        this.formattedFileName = formattedFileName;
        this.headerCols = headerCols;
        this.widthArray = widthArray;
        this.data = data;
        this.isDot = isDot;
        this.existHeader = false;
        this.existDoubleQuotes = false;
        this.append = false;
        this.charset = Charset.forName("UTF-8");
        return this;
    }
    //===========================

    public String getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public String getNeedHeader() {
        return needHeader;
    }

    public void setNeedHeader(String needHeader) {
        this.needHeader = needHeader;
    }

    public String getSqlType() {
        return sqlType;
    }

    public void setSqlType(String sqlType) {
        this.sqlType = sqlType;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getNeedDoubleQuotes() {
        return needDoubleQuotes;
    }

    public void setNeedDoubleQuotes(String needDoubleQuotes) {
        this.needDoubleQuotes = needDoubleQuotes;
    }

    public String getFileCoding() {
        return fileCoding;
    }

    public void setFileCoding(String fileCoding) {
        this.fileCoding = fileCoding;
    }

    public String getChkFileName() {
        return chkFileName;
    }

    public void setChkFileName(String chkFileName) {
        this.chkFileName = chkFileName;
    }

    public String getChkName() {
        return chkName;
    }

    public void setChkName(String chkName) {
        this.chkName = chkName;
    }

    public String getChkWidth() {
        return chkWidth;
    }

    public void setChkWidth(String chkWidth) {
        this.chkWidth = chkWidth;
    }

    public String getChkDateFormat() {
        return chkDateFormat;
    }

    public void setChkDateFormat(String chkDateFormat) {
        this.chkDateFormat = chkDateFormat;
    }

    public String getFormattedFileName() {
        return formattedFileName;
    }

    public void setFormattedFileName(String formattedFileName) {
        this.formattedFileName = formattedFileName;
    }

    public String[] getHeaderCols() {
        return headerCols;
    }

    public void setHeaderCols(String[] headerCols) {
        this.headerCols = headerCols;
    }

    public int[] getWidthArray() {
        return widthArray;
    }

    public void setWidthArray(int[] widthArray) {
        this.widthArray = widthArray;
    }

    public List<Map<String, Object>> getData() {
        return data;
    }

    public void setData(List<Map<String, Object>> data) {
        this.data = data;
    }

    public boolean isDot() {
        return isDot;
    }

    public void setDot(boolean dot) {
        isDot = dot;
    }

    public boolean isExistHeader() {
        return existHeader;
    }

    public void setExistHeader(boolean existHeader) {
        this.existHeader = existHeader;
    }

    public boolean isAppend() {
        return append;
    }

    public void setAppend(boolean append) {
        this.append = append;
    }

    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public boolean isExistDoubleQuotes() {
        return existDoubleQuotes;
    }

    public void setExistDoubleQuotes(boolean existDoubleQuotes) {
        this.existDoubleQuotes = existDoubleQuotes;
    }

    public String getFormattedCheckFileName() {
        return formattedCheckFileName;
    }

    public void setFormattedCheckFileName(String formattedCheckFileName) {
        this.formattedCheckFileName = formattedCheckFileName;
    }
}
