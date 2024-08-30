package com.systex.jbranch.platform.common.multiLang;

import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.errHandle.MultiLangException;
import com.systex.jbranch.platform.common.platformdao.table.TbsysmultilangVO;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

/**
 *此類別的功能描述：多國語系的轉檔介面
 * Notices: 此程式的注意事項：無
 *
 *@author  Benson Chen
 *@since   此Class 從哪個版本開始加入  (外部的version)
 *@see     MultiLangIdType,MultiLangUtil			
 **/
public class MultiLangParser {

    /**
     * 此Method的功能描述：將多國語系從資料庫table(Tbsysmultilang)轉到Property檔案中。
     * 其中將type_typeSeq_group_groupSeq_attribute_seq組合成ml_id(使用MultiLangIdType及MultiLangUtil)
     * 輸出格式：type_typeSeq_group_groupSeq_attribute_seq = content,ex: combo_01_combo_001_item_001 = 開戶
     * 檔案名稱： ml_type_typeSeq_locale.properties,ex: ml_combo_01_zh_TW.properties
     * ☆參考SD文件第四章
     *
     * @return  是否執行成功
     * @throws JBranchException 
     * @throws DAOException 
     * @throws 若有丟出Exception 的說明     (Required)
     * @see    MultiLangIdType,MultiLangUtil
     * @since   此Method 從哪個版本開始加入(外部的version)
     */
    public boolean DbToProperty() throws DAOException, JBranchException {
        boolean flag = false;

        MultiLangDaoDB mulDAO = new MultiLangDaoDB();
        List<String> typeList = mulDAO.findAllType();
        for (String type : typeList) {
            flag = this.DbToProperty(type);
        }
        return flag;
    }

    /**
     * 此Method的功能描述：將多國語系從資料庫table(Tbsysmultilang)轉到Property檔案中，並只轉單一型別檔案
     * 其中將type_typeSeq_group_groupSeq_attribute_seq組合成ml_id(使用MultiLangIdType及MultiLangUtil)
     * 輸出格式：type_typeSeq_group_groupSeq_attribute_seq = content,ex: combo_01_combo_001_item_001 = 開戶
     * 檔案名稱： ml_type_typeSeq_locale.properties,ex: ml_combo_01_zh_TW.properties
     * ☆參考SD文件第四章
     *
     * @param  多國語系型別(type)
     * @return  是否執行成功
     * @throws JBranchException 
     * @throws DAOException 
     * @throws 若有丟出Exception 的說明     (Required)
     * @see    MultiLangIdType,MultiLangUtil
     * @since   此Method 從哪個版本開始加入(外部的version)
     */
    public boolean DbToProperty(String type) throws DAOException, JBranchException {
        boolean flag = false;
        MultiLangDaoDB mulDAO = new MultiLangDaoDB();
        List<String> type_seqList = mulDAO.findTypeSeq(type);
        for (String type_seq : type_seqList) {
            List<String> localeList = mulDAO.findLocale(type);
            flag = this.DbToProperty(type, type_seq, localeList);
        }
        return flag;
    }

    /**
     * 此Method的功能描述：將多國語系從資料庫table(Tbsysmultilang)轉到Property檔案中，並只轉單一多國語系型別的個檔(依型別序號轉檔)
     * 其中將type_typeSeq_group_groupSeq_attribute_seq組合成ml_id(使用MultiLangIdType及MultiLangUtil)
     * 輸出格式：type_typeSeq_group_groupSeq_attribute_seq = content,ex: combo_01_combo_001_item_001 = 開戶
     * 檔案名稱： ml_type_typeSeq_locale.properties,ex: ml_combo_01_zh_TW.properties
     * ☆參考SD文件第四章
     *
     * @param  type 多國語系型別
     * @param  typeSeq 多國語系型別序號
     * @param  localeList 語系(若無指定語系，則轉出所有語系)
     * @return  是否執行成功
     * @throws JBranchException 
     * @throws DAOException 
     * @throws 若有丟出Exception 的說明     (Required)
     * @see    MultiLangIdType,MultiLangUtil
     * @since   此Method 從哪個版本開始加入(外部的version)
     */
    public boolean DbToProperty(String type, String typeSeq, List<String> localeList) throws DAOException, JBranchException {
        boolean flag = true;
        List<TbsysmultilangVO> multiLangList;
        MultiLangDaoDB mulDAO = new MultiLangDaoDB();
        for (String locale : localeList) {
            multiLangList = mulDAO.findByProperties(type, typeSeq, locale);
            TbsysmultilangVO Tbsysmultilang;
            String[] localeSplit = locale.split("_");
            String ids = "";
            MultiLangDaoFacadeIF MultiLangDaoProperties = new MultiLangDaoProperties(new Locale(localeSplit[0], localeSplit[1]));
            for (int i = 0; i < multiLangList.size(); i++) {
                Tbsysmultilang = multiLangList.get(i);
                ids = MultiLangUtil.combineId(Tbsysmultilang);
                try {
                    MultiLangDaoProperties.create(ids, Tbsysmultilang.getContent());
                } catch (MultiLangException ex) {
                    flag = false;
                }
            }
        }

        return flag;
    }

    /**
     * 此Method的功能描述：將多國語系從Property檔案轉到資料庫table(Tbsysmultilang)中。
     * 其中將type_typeSeq_group_groupSeq_attribute_seq組合成ml_id(使用MultiLangIdType及MultiLangUtil)
     * 輸出格式：type_typeSeq_group_groupSeq_attribute_seq = content,ex: combo_01_combo_001_item_001 = 開戶
     * 檔案名稱： ml_type_typeSeq_locale.properties,ex: ml_combo_01_zh_TW.properties
     * ☆參考SD文件第四章
     *
     * @param  Property檔案
     * @return  是否執行成功
     * @throws JBranchException 
     * @throws DAOException 
     * @throws 若有丟出Exception 的說明     (Required)
     * @see  MultiLangIdType,MultiLangUtil
     * @since   此Method 從哪個版本開始加入(外部的version)
     */
    public boolean PropertyToDb(String fileName) throws DAOException, JBranchException {
        boolean flag = false;
        try {
            String path = MultiLangUtil.getMultiLangConfigFilePath();
            String[] fileNameSplits = fileName.substring(0, fileName.lastIndexOf(".")).split("_");

            //reading multi language properties file
            InputStream is;

            is = new FileInputStream(path + System.getProperty("file.separator") + fileName);

            //load file
            Properties properties = new Properties();
            properties.load(is);

            MultiLangDaoDB multiLangDaoDB = new MultiLangDaoDB();
            if (fileNameSplits.length == 5) {
                multiLangDaoDB.changeLocale(new Locale(fileNameSplits[3], fileNameSplits[4]));
            }

            multiLangDaoDB.createByProperties(properties);
            flag = true;
        } catch (MultiLangException ex) {
            flag = false;
        } catch (IOException ex) {
            flag = false;
        }
        return flag;
    }

    /**
     * 此Method的功能描述：將多國語系從多個Property檔案轉到資料庫table(Tbsysmultilang)中
     * 其中將type_typeSeq_group_groupSeq_attribute_seq組合成ml_id(使用MultiLangIdType及MultiLangUtil)
     * 輸出格式：type_typeSeq_group_groupSeq_attribute_seq = content,ex: combo_01_combo_001_item_001 = 開戶
     * 檔案名稱： ml_type_typeSeq_locale.properties,ex: ml_combo_01_zh_TW.properties
     * ☆參考SD文件第四章
     *
     * @param  多個Property檔案
     * @return  是否執行成功
     * @throws JBranchException 
     * @throws DAOException 
     * @throws 若有丟出Exception 的說明     (Required)
     * @see  MultiLangIdType,MultiLangUtil
     * @since   此Method 從哪個版本開始加入(外部的version)
     */
    public boolean PropertyToDb(List<String> fileNames) throws DAOException, JBranchException {
        boolean flag = true;
        for (String filePathName : fileNames) {
            if (!this.PropertyToDb(filePathName)) {
                flag = false;
            }
        }
        return flag;
    }
}
