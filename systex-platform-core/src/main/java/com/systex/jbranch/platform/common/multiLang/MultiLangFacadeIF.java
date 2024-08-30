package com.systex.jbranch.platform.common.multiLang;

import com.systex.jbranch.platform.common.errHandle.MultiLangException;
import java.util.List;

/**
 *此類別的功能描述：多國語系的一般共用介面
 * Notices: 此程式的注意事項：無
 *
 *@author  Benson Chen
 *@since   此Class 從哪個版本開始加入  (外部的version)
 *@see     			
 **/
public interface MultiLangFacadeIF {

    /**
     *此Method的功能描述：將多國語系編號轉換成設定語系的文字
     * 
     *@param  傳入多國語系編號
     *@return  設定語系文字
     *@throws 若有丟出Exception 的說明     (Required)
     *@see   
     *@since   此Method 從哪個版本開始加入(外部的version)
     **/
    public String getById(String id_s) ;

    /**
     *此Method的功能描述：將一組多國語系編號轉換成一組設定語系的文字
     * 
     *@param 傳入一組多國語系編號
     *@return  回傳一組設定語系文字
     *@throws 若有丟出Exception 的說明     (Required)
     *@see   
     *@since   此Method 從哪個版本開始加入(外部的version)
     **/
    public List<String> getByIds(List<String> list) ;

    /**
     *此Method的功能描述：將多國語系群組編號轉換成以該群組為一組設定語系的文字
     * 
     *@param 傳入群組多國語系編號
     *@return  回傳一組設定語系文字(以屬性為主的HashMap)
     *@throws 若有丟出Exception 的說明     (Required)
     *@see   
     *@since   此Method 從哪個版本開始加入(外部的version)
     **/
    public List<IdGroupType> getByGroup(String gruop_s) ;
}
