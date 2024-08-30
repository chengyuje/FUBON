package com.systex.jbranch.platform.common.multiLang;

import com.systex.jbranch.platform.common.errHandle.MultiLangException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 *此類別的功能描述：多國語系的存取介面，依實作應用
 * Notices: 此程式的注意事項：無
 *
 *@author  Benson Chen
 *@since   此Class 從哪個版本開始加入  (外部的version)
 *@see     			
 **/
public interface MultiLangDaoFacadeIF {

    /**
     * 此Method的功能描述：建立一個多國語系的資料
     *
     * @param  傳入多國語系編號、欲儲存的內容
     * @return  是否執行成功
     * @throws 若有丟出Exception 的說明     (Required)
     * @see
     * @since   此Method 從哪個版本開始加入(外部的version)
     */
    public void create(String id_s, String content_s) throws MultiLangException;

    /**
     * 此Method的功能描述：建立一組多國語系的資料
     *
     * @param  傳入多國語系編號、欲儲存的語系與內容 Map<語系,內容>
     * @return  是否執行成功
     * @throws 若有丟出Exception 的說明     (Required)
     * @see
     * @since   此Method 從哪個版本開始加入(外部的version)
     */
    public void create(String id_s, Map<String, String> locale_contents) throws MultiLangException;

    /**
     * 此Method的功能描述：刪除一個多國語系的資料
     *
     * @param  傳入多國語系編號
     * @return  是否執行成功
     * @throws 若有丟出Exception 的說明     (Required)
     * @see
     * @since   此Method 從哪個版本開始加入(外部的version)
     */
    public void destroy(String id_s) throws MultiLangException;

    /**
     * 此Method的功能描述：刪除一組多國語系的資料(所有語系)
     *
     * @param  傳入多國語系編號
     * @return  是否執行成功
     * @throws 若有丟出Exception 的說明     (Required)
     * @see
     * @since   此Method 從哪個版本開始加入(外部的version)
     */
    public void destroyAll(String id_s) throws MultiLangException;

    /**
     * 此Method的功能描述：修改一個多國語系的資料
     *
     * @param  傳入多國語系編號、欲儲存的檔案
     * @return  是否執行成功
     * @throws 若有丟出Exception 的說明     (Required)
     * @see
     * @since   此Method 從哪個版本開始加入(外部的version)
     */
    public void edit(String id_s, String content_s) throws MultiLangException;

    /**
     * 此Method的功能描述：修改一組多國語系的資料
     *
     * @param  傳入多國語系編號、欲修改的語系與內容 Map<語系,內容>
     * @return  是否執行成功
     * @throws 若有丟出Exception 的說明     (Required)
     * @see
     * @since   此Method 從哪個版本開始加入(外部的version)
     */
    public void edit(String id_s, Map<String, String> locale_contents) throws MultiLangException;

    /**
     * 此Method的功能描述：找出多國語系的所有檔案型別
     *
     * @param  傳入BaseName的類別
     * @return  一系列多國語系型別名稱
     * @throws 若有丟出Exception 的說明     (Required)
     * @see
     * @since   此Method 從哪個版本開始加入(外部的version)
     */
    public List<String> findAllType();

    /**
     * 此Method的功能描述：找出該多國語系檔的群組資料(substring搜尋)
     *
     * @param  傳入多國語系的群組編號子字串
     * @return  一系列群組名稱
     * @throws 若有丟出Exception 的說明     (Required)
     * @see
     * @since   此Method 從哪個版本開始加入(外部的version)
     */
    public List<IdGroupType> findGroup(String subName_s) ;

    /**
     *此Method的功能描述：將多國語系編號轉換成設定語系的文字
     * 
     *@param  傳入多國語系編號
     *@return  設定語系文字
     *@throws 若有丟出Exception 的說明     (Required)
     *@see   
     *@since   此Method 從哪個版本開始加入(外部的version)
     **/
    public String findtById(String id_s) ;

    public void changeLocale(Locale locale) ;
}
