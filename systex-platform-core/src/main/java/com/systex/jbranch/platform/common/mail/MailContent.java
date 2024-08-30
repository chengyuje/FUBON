package com.systex.jbranch.platform.common.mail;

import com.systex.jbranch.platform.common.errHandle.JBranchException;

import java.util.Properties;

/**
 * Mail的內容
 *
 * @author Alex Lin
 * @version 2010/08/27 2:06:47 PM
 */
public interface MailContent {
// -------------------------- OTHER METHODS --------------------------

    /**
     * 設定mail template變數
     *
     * @param key   變數key
     * @param value 變數value
     */
    void addParameter(String key, Object value);

    /**
     * 取得mail內容
     *
     * @return mail內容
     * @throws JBranchException jbranch exception
     */
    String getContent() throws JBranchException;

    /**
     * 取得自訂的mail header
     *
     * @return mail header
     */
    Properties getHeaders();

    /**
     * 取得Importance header值
     *
     * @return Importance header值
     */
    Importance getImportance();

    /**
     * 取得X-Priority header值
     *
     * @return X-Priority header值
     */
    Priority getPriority();

    /**
     * 取得主旨
     *
     * @return 主旨
     */
    String getSubject();

    /**
     * 取得template名稱
     *
     * @return template名稱
     */
    String getTemplate();

    /**
     * 新增附檔
     *
     * @param attachment Attachment
     */
    void addAttachment(Attachment attachment);

    /**
     * 取得附檔
     *
     * @return 附檔
     */
    Attachment[] getAttachments();

// -------------------------- ENUMERATIONS --------------------------

    public enum Priority {
        PRIORITY_1(1), PRIORITY_2(2), PRIORITY_3(3), PRIORITY_4(4), PRIORITY_5(5);
        private int value;

        Priority(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public enum Importance {
        HIGH("High"), LOW("Low");
        private String value;

        Importance(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
