package com.systex.jbranch.platform.common.multiLang;

import com.systex.jbranch.platform.common.errHandle.MultiLangException;
import com.systex.jbranch.platform.common.platformdao.table.TbsysmultilangVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

/**
 * 此類別的功能描述：多國語系的存取，使用properties的方式
 * Notices: 此程式的注意事項：無
 *
 * @author Benson Chen
 * @see
 * @since 此Class 從哪個版本開始加入  (外部的version)
 */
public class MultiLangDaoProperties implements MultiLangDaoFacadeIF {
// ------------------------------ FIELDS ------------------------------

    private ResourceBundle resourceBundle;
    private Properties properties;
    private MultiLangIdType multiLangIdType;
    private String fileName;
    private Locale locale;
	private Logger logger = LoggerFactory.getLogger(MultiLangDaoProperties.class);

// --------------------------- CONSTRUCTORS ---------------------------

    public MultiLangDaoProperties(Locale locale) {
        this.locale = locale;
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface MultiLangDaoFacadeIF ---------------------

    /**
     * 此Method的功能描述：建立一個多國語系的資料
     *
     * @param 傳入多國語系編號、欲儲存的內容
     * @return 是否執行成功
     * @throws 若有丟出Exception 的說明     (Required)
     * @see
     * @since 此Method 從哪個版本開始加入(外部的version)
     */
    public void create(String id_s, String content_s) throws MultiLangException {
        //error Msg combination
//        boolean errFlag = false;
        List<String> errMsgList = new ArrayList<String>();
        StringBuffer errMsg_sb = new StringBuffer();
        errMsgList.add(id_s);

        try {
            //get MULTILANG_FILE_PATH
            String path = MultiLangUtil.getMultiLangConfigFilePath();

            //conbine file name, e.g.,ml_table_01_zh_TW.properties
            multiLangIdType = MultiLangUtil.parsingId(id_s);
            fileName = "ml_" + multiLangIdType.getType_s() + "_" + multiLangIdType.getTypeSeq_s() + "_" + locale + ".txt";
            if (!MultiLangUtil.checkPropertiesFileIsExist(id_s, this.locale.toString())) {
                MultiLangUtil.createPropertiesFile(id_s, this.locale.toString());
            }

            //reading multi language properties file
            InputStream is = new FileInputStream(path + System.getProperty("file.separator") + fileName);

            //create element
            properties = new Properties();
            properties.load(is);
            if (!properties.containsKey(id_s)) {
                properties.put(id_s, content_s);
            }
            else {
                errMsg_sb.append(fileName);
                errMsgList.add(errMsg_sb.toString());
                throw new MultiLangException("", errMsgList);
            }

            //writing multi language properties file
            OutputStream os = new FileOutputStream(path + System.getProperty("file.separator") + fileName);
            properties.store(os, fileName);
            os.flush();
            os.close();
        }
        catch (IOException ex) {
            throw new MultiLangException("", errMsgList);
        }
    }

    /**
     * 此Method的功能描述：建立一組多國語系的資料
     *
     * @param 傳入多國語系編號、欲儲存的語系與內容 Map<語系,內容>
     * @return 是否執行成功
     * @throws 若有丟出Exception 的說明     (Required)
     * @see
     * @since 此Method 從哪個版本開始加入(外部的version)
     */
    public void create(String id_s, Map<String, String> locale_contents) throws MultiLangException {
        //error Msg combination
        boolean errFlag = false;
        List<String> errMsgList = new ArrayList<String>();
        StringBuffer errMsg_sb = new StringBuffer();
        errMsgList.add(id_s);

        try {
            //get MULTILANG_FILE_PATH
            String path = MultiLangUtil.getMultiLangConfigFilePath();

            //conbine file name, e.g.,ml_table_01_zh_TW.properties
            if (!MultiLangUtil.checkPropertiesFileIsExist(id_s, this.locale.toString())) {
                MultiLangUtil.createPropertiesFile(id_s, this.locale.toString());
            }

            multiLangIdType = MultiLangUtil.parsingId(id_s);

            InputStream is;
            OutputStream os;

            String content = "";
            Set<String> set = locale_contents.keySet();
            String[] locs = set.toArray(new String[0]);
            for (String locale_s : locs) {
                content = locale_contents.get(locale_s);
                fileName = "ml_" + multiLangIdType.getType_s() + "_" + multiLangIdType.getTypeSeq_s() + "_" + locale + ".txt";
                //reading multi language properties file
                is = new FileInputStream(path + System.getProperty("file.separator") + fileName);

                //create element
                properties = new Properties();
                properties.load(is);
                if (!properties.containsKey(id_s)) {
                    properties.put(id_s, content);
                }
                else {
                    errFlag = true;
                    errMsg_sb.append(fileName).append(",");
                }

                //writing multi language properties file
                os = new FileOutputStream(path + System.getProperty("file.separator") + fileName);
                properties.store(os, fileName);
                os.flush();
                os.close();
            }

            if (errFlag) {
                errMsgList.add(errMsg_sb.deleteCharAt(errMsg_sb.lastIndexOf(",")).toString());
                throw new MultiLangException("", errMsgList);
            }
        }
        catch (IOException ex) {
            errMsgList.add(errMsg_sb.toString());
            throw new MultiLangException("", errMsgList);
        }
    }

    /**
     * 此Method的功能描述：刪除一個多國語系的資料
     *
     * @param 傳入多國語系編號
     * @return 是否執行成功
     * @throws 若有丟出Exception 的說明     (Required)
     * @see
     * @since 此Method 從哪個版本開始加入(外部的version)
     */
    public void destroy(String id_s) throws MultiLangException {
        //error Msg combination
        boolean errFlag = false;
        List<String> errMsgList = new ArrayList<String>();
        StringBuffer errMsg_sb = new StringBuffer();
        errMsgList.add(id_s);

        try {
            //get MULTILANG_FILE_PATH
            String path = MultiLangUtil.getMultiLangConfigFilePath();

            //conbine file name, e.g.,ml_table_01_zh_TW.properties
            multiLangIdType = MultiLangUtil.parsingId(id_s);
            fileName = "ml_" + multiLangIdType.getType_s() + "_" + multiLangIdType.getTypeSeq_s() + "_" + locale + ".txt";

            if (!MultiLangUtil.checkPropertiesFileIsExist(id_s, this.locale.toString())) {
                MultiLangUtil.createPropertiesFile(id_s, this.locale.toString());
            }

            //reading multi language properties file
            InputStream is = new FileInputStream(path + System.getProperty("file.separator") + fileName);

            //create element
            properties = new Properties();
            properties.load(is);
            if (properties.containsKey(id_s)) {
                properties.remove(id_s);
            }
            else {
                errMsg_sb.append(fileName);
                errMsgList.add(errMsg_sb.toString());
                throw new MultiLangException("", errMsgList);
            }
            //writing multi language properties file
            OutputStream os = new FileOutputStream(path + System.getProperty("file.separator") + fileName);

            properties.store(os, fileName);
            os.flush();
            os.close();
        }
        catch (IOException ex) {
            throw new MultiLangException("", errMsgList);
        }
    }

    /**
     * 此Method的功能描述：刪除一組多國語系的資料(所有語系)
     *
     * @param 傳入多國語系編號
     * @return 是否執行成功
     * @throws 若有丟出Exception 的說明     (Required)
     * @see
     * @since 此Method 從哪個版本開始加入(外部的version)
     */
    public void destroyAll(String id_s) throws MultiLangException {
    }

    /**
     * 此Method的功能描述：修改一個多國語系的資料
     *
     * @param 傳入多國語系編號、欲儲存的檔案
     * @return 是否執行成功
     * @throws 若有丟出Exception 的說明     (Required)
     * @see
     * @since 此Method 從哪個版本開始加入(外部的version)
     */
    public void edit(String id_s, String content_s) throws MultiLangException {
        //error Msg combination
        boolean errFlag = false;
        List<String> errMsgList = new ArrayList<String>();
        StringBuffer errMsg_sb = new StringBuffer();
        errMsgList.add(id_s);

        try {
            //get MULTILANG_FILE_PATH
            String path = MultiLangUtil.getMultiLangConfigFilePath();

            //conbine file name, e.g.,ml_table_01_zh_TW.properties
            multiLangIdType = MultiLangUtil.parsingId(id_s);
            fileName = "ml_" + multiLangIdType.getType_s() + "_" + multiLangIdType.getTypeSeq_s() + "_" + locale + ".txt";

            if (!MultiLangUtil.checkPropertiesFileIsExist(id_s, this.locale.toString())) {
                MultiLangUtil.createPropertiesFile(id_s, this.locale.toString());
            }

            //reading multi language properties file
            InputStream is = new FileInputStream(path + System.getProperty("file.separator") + fileName);
            if (logger.isDebugEnabled()) {
                logger.debug("path + fileName = " + path + "/" + fileName);
            }
            //            InputStreamReader isr = new InputStreamReader(is);

            //create element
            properties = new Properties();
            properties.load(is);
            if (properties.containsKey(id_s)) {
                properties.remove(id_s);
                properties.put(id_s, content_s);
            }
            else {
                errMsg_sb.append(fileName);
                errMsgList.add(errMsg_sb.toString());
                throw new MultiLangException("", errMsgList);
            }

            //writing multi language properties file
            OutputStream os = new FileOutputStream(path + System.getProperty("file.separator") + fileName);

            properties.store(os, fileName);
            os.flush();
            os.close();
        }
        catch (IOException ex) {
            MultiLangException mex = new MultiLangException("", errMsgList);
            mex.setException(ex);
            throw mex;
        }
    }

    /**
     * 此Method的功能描述：修改一組多國語系的資料
     *
     * @param 傳入多國語系編號、欲修改的語系與內容 Map<語系,內容>
     * @return 是否執行成功
     * @throws 若有丟出Exception 的說明     (Required)
     * @see
     * @since 此Method 從哪個版本開始加入(外部的version)
     */
    public void edit(String id_s, Map<String, String> locale_contents) throws MultiLangException {
        //error Msg combination
        boolean errFlag = false;
        List<String> errMsgList = new ArrayList<String>();
        StringBuffer errMsg_sb = new StringBuffer();
        errMsgList.add(id_s);

        try {
            //get MULTILANG_FILE_PATH
            String path = MultiLangUtil.getMultiLangConfigFilePath();

            //conbine file name, e.g.,ml_table_01_zh_TW.properties
            if (!MultiLangUtil.checkPropertiesFileIsExist(id_s, this.locale.toString())) {
                MultiLangUtil.createPropertiesFile(id_s, this.locale.toString());
            }

            multiLangIdType = MultiLangUtil.parsingId(id_s);

            InputStream is;
            OutputStream os;
            Set<String> set = locale_contents.keySet();
            String content = "";
            String[] locs = set.toArray(new String[0]);
            for (String locale_s : locs) {
                content = locale_contents.get(locale_s);
                fileName = "ml_" + multiLangIdType.getType_s() + "_" + multiLangIdType.getTypeSeq_s() + "_" + locale + ".txt";
                //reading multi language properties file
                is = new FileInputStream(path + System.getProperty("file.separator") + fileName);
                if (logger.isDebugEnabled()) {
                    logger.debug("path + fileName = " + path + "/" + fileName);
                }

                //create element
                properties = new Properties();
                properties.load(is);
                if (properties.containsKey(id_s)) {
                    properties.remove(id_s);
                    properties.put(id_s, content);
                }
                else {
                    errFlag = true;
                    errMsg_sb.append(fileName).append(",");
                }

                //writing multi language properties file
                os = new FileOutputStream(path + System.getProperty("file.separator") + fileName);
                properties.store(os, fileName);
                os.flush();
                os.close();
            }

            if (errFlag) {
                errMsgList.add(errMsg_sb.deleteCharAt(errMsg_sb.lastIndexOf(",")).toString());
                throw new MultiLangException("", errMsgList);
            }
        }
        catch (IOException ex) {
            errMsgList.add(errMsg_sb.toString());
            MultiLangException mex = new MultiLangException("", errMsgList);
            mex.setException(ex);
            throw mex;
        }
    }

    /**
     * 此Method的功能描述：找出多國語系的所有檔案型別
     *
     * @param 傳入BaseName的類別
     * @return 一系列多國語系型別名稱
     * @throws 若有丟出Exception 的說明     (Required)
     * @see
     * @since 此Method 從哪個版本開始加入(外部的version)
     */
    public List<String> findAllType() {
        List<String> typeList = new ArrayList<String>();
        try {
            //String path = MultiLangUtil.getMultiLangConfigFilePath();
            File fileDir = new File(MultiLangUtil.getMultiLangConfigFilePath());
            File[] fileList = fileDir.listFiles();
            for (File fName : fileList) {
                //properties file name ==> ml_msg_01_en_US.properties
                if (fName.getName().startsWith("ml_")) {
                    String[] fileNameSplit = fName.getName().split("_");
                    if (!typeList.contains(fileNameSplit[1])) {
                        typeList.add(fileNameSplit[1]);
                    }
                }
            }
            return typeList;
        }
        catch (IOException ex) {
            return typeList;
        }
    }

    /**
     * 此Method的功能描述：找出該多國語系檔的群組資料(substring搜尋)
     *
     * @param subName_s 傳入多國語系的群組編號子字串,ex: msg_01_group_001
     * @return List<IdGroupType> 該群組下分組的屬性及序號(ex.attribute_001)
     * @throws 若有丟出Exception 的說明     (Required)
     * @see
     * @since 此Method 從哪個版本開始加入(外部的version)
     */
    public List<IdGroupType> findGroup(String subName_s) {
        if (subName_s == null) {
            subName_s = "";
        }
        multiLangIdType = MultiLangUtil.parsingId(subName_s);
        List<IdGroupType> idGrouplist = new ArrayList<IdGroupType>();
        if (subName_s.trim().equals("")) {
            return idGrouplist;
        }

        String groupName = multiLangIdType.getType_s() + "_" + multiLangIdType.getTypeSeq_s() + "_" + multiLangIdType.getGroupname_s() + "_" + multiLangIdType.getGroupSeq_s();

        try {
            this.IsChangeBundle(MultiLangUtil.parsingId(subName_s));
            TbsysmultilangVO Tbsysmultilang_tmp = new TbsysmultilangVO();
            List<TbsysmultilangVO> allList = new ArrayList<TbsysmultilangVO>();

            Enumeration<String> keyEnum = resourceBundle.getKeys();
            String tmpKey = "";
            while (keyEnum.hasMoreElements()) {
                tmpKey = keyEnum.nextElement();
                if (tmpKey.toLowerCase().contains(groupName.toLowerCase())) {
                    Tbsysmultilang_tmp = new TbsysmultilangVO();
                    Tbsysmultilang_tmp = MultiLangUtil.parsingMultiLangIdType(MultiLangUtil.parsingId(tmpKey));
                    Tbsysmultilang_tmp.getComp_id().setLocale(this.locale.toString());
                    Tbsysmultilang_tmp.setContent(resourceBundle.getString(tmpKey));
                    allList.add(Tbsysmultilang_tmp);
                }
            }

            //separating the attribute and get contents
            if (!allList.isEmpty()) {
                // msg_01_group_001_attribute_001 ==> preAttribute = msg_01_group_001_attribute
                //set value to IdGroupType

                IdGroupType idGroup = new IdGroupType();
                List<TbsysmultilangVO> inList = new ArrayList<TbsysmultilangVO>();
                String cmp = "";
                for (int i = 0; i < allList.size(); i++) {
                    TbsysmultilangVO Tbsysmultilang = (TbsysmultilangVO) allList.get(i);
                    if (i == 0) {
                        cmp = Tbsysmultilang.getComp_id().getAttribute();
                    }
                    if (Tbsysmultilang.getComp_id().getAttribute().equals(cmp)) {
                        inList.add(allList.get(i));
                    }
                    else {
                        idGroup = new IdGroupType();
                        idGroup.setAttributeName(cmp);
                        idGroup.setIdAttributes(inList);
                        idGrouplist.add(idGroup);
                        cmp = Tbsysmultilang.getComp_id().getAttribute();
                        inList = new ArrayList<TbsysmultilangVO>();
                        inList.add(allList.get(i));
                    }
                    if (i == allList.size() - 1) {
                        idGroup = new IdGroupType();
                        idGroup.setAttributeName(cmp);
                        idGroup.setIdAttributes(inList);
                        idGrouplist.add(idGroup);
                    }
                }

                return idGrouplist;
            }
            else {
                return idGrouplist;
            }
        }
        catch (RuntimeException e) {
            return idGrouplist;
        }
        catch (IOException ioe) {
            return idGrouplist;
        }
    }

    public String findtById(String id_s) {
        if (id_s.trim().equals("")) {
            return "";
        }
        try {
            this.IsChangeBundle(MultiLangUtil.parsingId(id_s));
            return resourceBundle.getString(id_s);
        }
        catch (RuntimeException e) {
            return "";
        }
        catch (IOException ioe) {
            return "";
        }
    }

    /**
     * 此Method的功能描述：將ResourceBundle所使用的property檔案關閉，並釋放資源
     *
     * @param
     * @return
     * @throws 若有丟出Exception 的說明     (Required)
     * @see
     * @since 此Method 從哪個版本開始加入(外部的version)
     */
    public void changeLocale(Locale locale) {
        if (multiLangIdType != null) {
            try {
                String name = "ml_" + multiLangIdType.getType_s() + "_" + multiLangIdType.getTypeSeq_s();
                String source = MultiLangUtil.getMultiLangConfigPropertiesClassPath() + locale.toString() + "/" + name;
                if (source.length() <= 100) {
                    resourceBundle = ResourceBundle.getBundle(source, locale);
                }
            }
            catch (IOException ex) {
            }
        }
        else {
            this.locale = locale;
        }
    }

    private void IsChangeBundle(MultiLangIdType idNew) throws RuntimeException, IOException {
        String name = "ml_" + multiLangIdType.getType_s() + "_" + multiLangIdType.getTypeSeq_s();
        String baseName = MultiLangUtil.getMultiLangConfigPropertiesClassPath() + "." + this.locale.toString() + "." + name;
        if (logger.isDebugEnabled()) {
            logger.debug("baseName = " + baseName);
        }
        if (resourceBundle != null) {
            if (!(multiLangIdType.getType_s().equals(idNew.getType_s())) || !(multiLangIdType.getTypeSeq_s().equals(idNew.getTypeSeq_s()))) {
                multiLangIdType.setType_s(idNew.getType_s());
                multiLangIdType.setTypeSeq_s(idNew.getTypeSeq_s());
                multiLangIdType.setGroupname_s(idNew.getGroupname_s());
                multiLangIdType.setGroupSeq_s(idNew.getGroupSeq_s());
                multiLangIdType.setAttribute_s(idNew.getAttribute_s());
                multiLangIdType.setSeq_s(idNew.getSeq_s());
            }
        }
        else {
            multiLangIdType = new MultiLangIdType();
            multiLangIdType.setType_s(idNew.getType_s());
            multiLangIdType.setTypeSeq_s(idNew.getTypeSeq_s());
            multiLangIdType.setGroupname_s(idNew.getGroupname_s());
            multiLangIdType.setGroupSeq_s(idNew.getGroupSeq_s());
            multiLangIdType.setAttribute_s(idNew.getAttribute_s());
            multiLangIdType.setSeq_s(idNew.getSeq_s());
        }
        if (baseName.length() <= 100) {
            resourceBundle = ResourceBundle.getBundle(baseName, locale);
        }
    }
}
