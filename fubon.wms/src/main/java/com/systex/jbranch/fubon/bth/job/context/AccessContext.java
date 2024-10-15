package com.systex.jbranch.fubon.bth.job.context;

import com.systex.jbranch.fubon.bth.ftp.FtpUtilInf;
import com.systex.jbranch.fubon.bth.ftp.SystexFtpUtil;
import com.systex.jbranch.fubon.bth.job.business_logic.ReportConfig;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.platformdao.table.TBSYSREMOTEHOSTVO;
import com.systex.jbranch.platform.common.security.impl.JBranchCryptology;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.w3c.dom.NamedNodeMap;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * AccessContext 存取工具服務器
 *
 * @author Eli
 * @date 2017/10/04
 * @spec <p>該服務器提供靜態存取工具。</p>
 * @since V1.1版
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Scope("prototype")
public class AccessContext extends FubonWmsBizLogic {
    //log
    private static Logger logger = LoggerFactory.getLogger(AccessContext.class);

    //路徑
    public static String tempPath;
    public static String tempReportPath;

    //格式
    public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    //file檔案 delimiter (DOT : 逗點分隔 、 FIX : 固定寬度)
    public static final boolean DOT = true;
    public static final boolean FIX = false;

    //BOM-UTF-8
    public static final byte[] BOM_UTF8 = {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};

    //封閉建構子
    private AccessContext() {
    }

    //static initialize
    static {
        //init path
        try {
            tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
            tempReportPath = SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH) + "reports/";
        } catch (JBranchException e) {
            logger.debug(e.getMessage());
        }
    }

    /**
     * 產生CSV檔案
     *
     * @param fileName  : String : CSV欲命名檔案名稱
     * @param csvHeader : String[] : CSV表頭
     * @param csvColumn : String[] : CSV內容
     * @param listData  : List<Map<String, Object>> : CSV內容來源
     * @return String : CSV檔案路徑
     * @throws Exception
     */
    public static String generateCSV
    (
            String fileName,
            String[] csvHeader,
            String[] csvColumn,
            List<Map<String, Object>> listData
    )
            throws Exception {
        //檢核參數陣列個數是否一致
        if (csvHeader.length != csvColumn.length) {
            throw new APException("參數設置有問題，再請確認");
        }

        List listCSV = new ArrayList();
        for (Map<String, Object> map : listData) {
            String[] records = new String[csvColumn.length];
            int index = 0;
            for (String key : csvColumn) {
                records[index++] = checkIsNull(map, key);
            }
            listCSV.add(records);
        }

        CSVUtil csv = new CSVUtil();
        csv.setFileName(fileName);
        csv.setHeader(csvHeader);
        csv.addRecordList(listCSV);
        return csv.generateCSV();
    }

    /**
     * 產生File檔案
     *
     * @param cfg 報表config設定檔
     * @return String 檔案路徑
     * @throws Exception
     */
    public static String generateFile(ReportConfig cfg) throws Exception {
        try (BufferedWriter bw = getFileToWrite(new File(tempReportPath, cfg.getFormattedFileName()), cfg.getCharset())) {
            if (cfg.isExistHeader()) { // 符合則寫入header
                printLine(cfg, bw, null);
            }

            for (Map<String, Object> map : cfg.getData()) { // 寫Data
                printLine(cfg, bw, map);
            }
            bw.flush();
        }
        return "temp/reports/" + cfg.getFormattedFileName();
    }

    /**
     * 寫入一行，map null 時，代表寫header，反之寫value
     **/
    private static void printLine(ReportConfig cfg, BufferedWriter bw, Map<String, Object> map) throws IOException {
        int index = 0;
        for (String key : cfg.getHeaderCols()) {
            if (cfg.isDot()) {
                bw.append(checkDoubleQuotes(checkIsNull(map, key), cfg.isExistDoubleQuotes()))
                        .append(++index < cfg.getHeaderCols().length ? "," : "");
            } else {
                bw.append(lenString(checkDoubleQuotes(checkIsNull(map, key), cfg.isExistDoubleQuotes()), cfg.getWidthArray()[index++]));
            }
        }
        bw.newLine();
    }

    /**
     * Map處理邏輯
     *
     * @param map 欲處理的map物件
     * @param key 欲處理的map物件其key值
     * @return String : 處理過後的字串
     */
    public static String checkIsNull(Map map, String key) {
        if (map == null) return key;
        else if (map.get(key) == null) return "";
        else return String.valueOf(map.get(key));
    }

    /**
     * 判斷是否需要雙引號修飾資料
     **/
    public static String checkDoubleQuotes(String value, boolean existDoubleQuotes) {
        return existDoubleQuotes ? String.format("%s%s%s", "\"", value, "\"") : value;
    }

    /**
     * 取得指定 Charset 的 BufferedWriter
     *
     * @param file
     * @param charset
     * @return
     * @throws IOException
     */
    public static BufferedWriter getFileToWrite(File file, Charset charset) throws IOException {
        return new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(
                                file), charset));
    }

    /**
     * 字元處理
     *
     * @param inputStr : String : 欲處理的字元
     * @param strLen   : int : 欲處理的字元顯示的長度
     * @return String : 處理過後的字串
     */
    public static String lenString(String inputStr, int strLen) {
        int inputStrLen;
        if (StringUtils.isBlank(inputStr)) {
            inputStrLen = 0;
            inputStr = "";
        } else inputStrLen = inputStr.length();

        int lenCtrl = 0;
        int index = 0;
        String returnString = "";
        while (inputStrLen > index) {
            //判斷該字元是否為雙位元資料
            if (Integer.parseInt(Integer.toString(inputStr.charAt(index), 16), 16) >= 128) {
                if ((strLen - lenCtrl) >= 2) {
                    returnString += inputStr.substring(index, index + 1);
                    lenCtrl += 2;
                }
            } else if ((strLen - lenCtrl) >= 1) {
                returnString += inputStr.substring(index, index + 1);
                lenCtrl++;
            }
            index++;
        }

        // 現有字串以後補空白
        while (lenCtrl < strLen) {
            returnString += " ";
            lenCtrl++;
        }
        // 表示該字串中含有雙位元資料
        if (returnString.length() < strLen)
            strLen = returnString.length();
        return returnString.substring(0, strLen);
    }

    /**
     * 類別成員解密字串，提供解密connection相關隱密資訊
     *
     * @param text : encryptedData Hex形式的被加密的字串
     * @return 解密後的字串
     */
    public static String dec(String text) throws JBranchException {
        return JBranchCryptology.decodePassword(text);
    }

    /**
     * 取得遠端檔案
     * (舊程式有參考到此Method，暫不刪)
     * @param srcdirectory : String 檔案路徑
     * @param srcfilename  : String 檔案名稱
     * @return 遠端檔案source (封裝在List)
     * @throws JBranchException
     */
    public static List<String> getRemoteFile(String srcdirectory, String srcfilename) throws JBranchException {
        DataAccessManager dam = (DataAccessManager) PlatformContext.getBean("dataAccessManager");
        TBSYSREMOTEHOSTVO tbsysremotehostvo = (TBSYSREMOTEHOSTVO) dam.findByPKey(TBSYSREMOTEHOSTVO.TABLE_UID, "FTP");
        if (null == tbsysremotehostvo) throw new APException("遠端連線設定錯誤");
        FtpUtilInf ftpUtil = new SystexFtpUtil(tbsysremotehostvo);
        return ftpUtil.readFile(srcdirectory, srcfilename);
    }

    /**
     * 將 List 規範元素 (Map) (通常為<b>query<b>後返回的物件型態)<br>
     * 轉換為 <b>List<b>並規範其內元素為<b>String<b>
     *
     * @param list        : 欲轉換source
     * @param paramColumn : 轉換source的mapkey值
     * @return 轉換過的source (封裝在List其規範值為String)
     */
    public static List<String> dataTransformedToStringOfList(List<Map<String, Object>> list, String[] paramColumn) throws Exception {
        List<String> paramList = new ArrayList<>();
        StringBuffer paramline = new StringBuffer();

        for (Map<String, Object> m : list) {
            for (String col : paramColumn) {
                paramline.append(m.get(col));
                if (!paramColumn[paramColumn.length - 1].equals(col)) paramline.append(",");
            }

            paramList.add(paramline.toString());
            paramline.setLength(0);
        }
        return paramList;
    }

    /**
     * 將 List 規範元素 (String) (通常為<b>getRemoteFile<b>後返回的物件型態)<br>
     * 轉換為 <b>List<b>並規範其內元素為<b>Map<b>
     *
     * @param list   : 欲轉換source
     * @param mapKey : 轉換source所需的mapkey值
     * @return 轉換過的source (封裝在List其規範值為String)
     */
    public static List<Map<String, Object>> dataTransformedToMapOfList(List<String> list, String[] mapKey) throws Exception {
        List<Map<String, Object>> paramList = new ArrayList<>();
        HashMap dataMap = new HashMap();

        for (String mapStr : list) {
            String[] mapData = mapStr.split(",");
            if (mapData.length != mapKey.length) {
                throw new APException("資料源的個數與Map Key值個數不盡相同，無法轉換！");
            }
            for (int i = 0; i < mapKey.length; i++) {
                dataMap.put(mapKey[i], mapData[i]);
            }
            paramList.add((HashMap) dataMap.clone()); //must clone and next step will clear
            dataMap.clear();
        }
        return paramList;
    }


    /**
     * 取得目標xml檔案標籤裡的屬性
     *
     * @param xmlFilePath 檔案位置
     * @param xmlTagName  標籤
     * @param attrs       標籤屬性
     * @return HashMap 屬性Map
     * @throws Exception
     */
    public static HashMap<String, String> getXmlAttribute(String xmlFilePath, String xmlTagName, String[] attrs) throws Exception {
        return parseXmlElement(getXmlElement(xmlFilePath, xmlTagName), attrs);
    }

    private static HashMap parseXmlElement(NamedNodeMap xmlAttrMap, String[] attrs) {
        HashMap attrMap = new HashMap();
        for (String attr : attrs) {
            attrMap.put(attr, parseXmlAttribute(xmlAttrMap.getNamedItem(attr).toString()));
        }
        return attrMap;
    }

    private static NamedNodeMap getXmlElement(String xmlFilePath, String xmlTagName) throws Exception {
        File context = new File(xmlFilePath);
        return DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(context)
                .getElementsByTagName(xmlTagName)
                .item(0)
                .getAttributes();
    }

    private static String parseXmlAttribute(String attr) {
        return attr.substring(attr.indexOf("\"") + 1, attr.lastIndexOf("\""));
    }

    /**
     * 取得包有BOM檔首的 BufferedWriter
     *
     * @param path
     * @return
     * @throws IOException
     */
    public static BufferedWriter getBomBufferedWriter(Path path) throws IOException {
        FileOutputStream stream = new FileOutputStream(path.toFile());
        stream.write(BOM_UTF8);
        return new BufferedWriter(new OutputStreamWriter(stream, StandardCharsets.UTF_8));
    }
}
