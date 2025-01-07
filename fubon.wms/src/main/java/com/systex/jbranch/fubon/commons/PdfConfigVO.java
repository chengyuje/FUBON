package com.systex.jbranch.fubon.commons;

import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
/*import edu.emory.mathcs.backport.java.util.Arrays;*/
import java.util.List;
import java.util.Arrays;

public class PdfConfigVO {
    private final List<String> files;
    private boolean isAddBlank = false; // 預設 PDF 不需要空白頁
    private boolean encrypt = false;     // 預設 PDF 需加密
    private String userPassword;
    private String ownerPassword;
    private DataAccessManager dam;

    /**
     * PDF 案例：
     * 兩組密碼：
     * - 使用者密碼：使用登入 ID
     * - 擁有者密碼：資料庫查詢
     * 不需添加空白頁
     */
    public PdfConfigVO(DataAccessManager dam, List<String> files) {
        this.dam = dam;
        this.files = files;
    }

    /**
     * PDF 案例：
     * 兩組密碼：
     * - 使用者密碼：使用登入 ID
     * - 擁有者密碼：資料庫查詢
     * 不需添加空白頁
     */
    public PdfConfigVO(DataAccessManager dam, String... files) {
        this.dam = dam;
        this.files = Arrays.asList(files);
    }

    /**
     * PDF 案例：
     * 兩組密碼：
     * - 使用者密碼：使用登入 ID
     * - 擁有者密碼：資料庫查詢
     * 可設定是否添加空白頁
     */
    public PdfConfigVO(DataAccessManager dam, boolean isAddBlank, List files) {
        this.dam = dam;
        this.isAddBlank = isAddBlank;
        this.files = files;
    }

    public List<String> getFiles() {
        return files;
    }

    public boolean isAddBlank() {
        return isAddBlank;
    }

    public boolean isEncrypt() {
        return encrypt;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public String getOwnerPassword() {
        return ownerPassword;
    }

    public DataAccessManager getDam() {
        return dam;
    }
}
