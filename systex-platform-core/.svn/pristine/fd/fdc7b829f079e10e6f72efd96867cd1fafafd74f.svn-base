package com.systex.jbranch.platform.common.dataManager;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * 系統組態設定。<br>
 * 請參考System.xml,及Spring IOC。<br>
 *
 * @author Eric.Lin<br>
 */

public class System {
// ------------------------------ FIELDS ------------------------------

    static private boolean status = true;
    String FILE_SEPARATOR = java.lang.System.getProperties().getProperty("file.separator");
    char[] charArray = FILE_SEPARATOR.toCharArray();
    private HashMap path = null;
    private HashMap configFileName;
    private HashMap defaultValue;
    private HashMap info;
    private HashMap vars = new HashMap();
    private PlatFormVO platformVO;
    private BizlogicVO bizlogicVO;

// -------------------------- STATIC METHODS --------------------------

    /**
     * 系統狀態。<br>
     *
     * @return true:正常; false:不正常。<br>
     */
    public static boolean isStatus() {
        return status;
    }

    /**
     * 設定系統狀態。<br>
     *
     * @param status true:正常;false:不正常。<br>
     */
    public static void setStatus(boolean status) {
        System.status = status;
    }

// -------------------------- OTHER METHODS --------------------------

    public Object getVars(String key) {
        return vars.get(key);
    }

    public void setVars(String key, Object value) {
        vars.put(key, value);
    }
    
    public Map getVars(){
    	return vars;
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public BizlogicVO getBizlogicVO() {
        return bizlogicVO;
    }

    public void setBizlogicVO(BizlogicVO bizlogicVO) {
        this.bizlogicVO = bizlogicVO;
    }

    /**
     * 取得各組組態檔名. <br>
     *
     * @return
     */
    public HashMap getConfigFileName() {
        return configFileName;
    }

    /**
     * 設定所有組態檔名<br>
     *
     * @param configFileName<br>
     */
    public void setConfigFileName(HashMap configFileName) {
        this.configFileName = configFileName;
    }

    /**
     * 取得系統各預設值。<br>
     *
     * @return
     */
    public HashMap getDefaultValue() {
        return defaultValue;
    }

    /**
     * 設定所有系統預設值<br>
     *
     * @param defaultValue<br>
     */
    public void setDefaultValue(HashMap defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * 取得系統資訊。<br>r
     *
     * @return<br>
     */
    public HashMap getInfo() {
        return info;
    }

    /**
     * 設定系統所有資訊。<br>
     *
     * @param info<br>
     */
    public void setInfo(HashMap info) {
        this.info = info;
    }

    /**
     * 取得各組路徑設定。
     *
     * @return<br>
     */
    public HashMap getPath() {
        return path;
    }

    /**
     * 設定系統各組路徑。<br>
     *
     * @param path<br>
     */
    public void setPath(HashMap path) {
        if (path != null) {
            for (Object o : path.keySet()) {
                Object value = path.get(o.toString());
                if (value instanceof String) {
                    value = StringUtils.replace(value.toString(), FILE_SEPARATOR, "\\");
                    value = StringUtils.replace(value.toString(), "\\", FILE_SEPARATOR);
                }
                path.put(o, value);
            }
        }
        this.path = path;
    }

    public PlatFormVO getPlatformVO() {
        return platformVO;
    }

    public void setPlatformVO(PlatFormVO platformVO) {
        this.platformVO = platformVO;
    }

    /**
     * 取得系統根目錄。<br>
     *
     * @return<br>
     */
    public String getRoot() {
        String root = DataManager.getRoot();
        root = root.replace('\\', charArray[0]);
        root = root.replace('/', charArray[0]);

        return root;
    }
}
