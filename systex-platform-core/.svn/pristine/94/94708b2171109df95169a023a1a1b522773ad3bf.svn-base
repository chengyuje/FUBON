package com.systex.jbranch.platform.common.multiLang;

import com.systex.jbranch.platform.common.errHandle.MultiLangException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 *此類別的功能描述：多國語系的一般共用介面實作(使用property的方式)
 * Notices: 此程式的注意事項：無
 *
 *@author  Benson Chen
 *@since   此Class 從哪個版本開始加入  (外部的version)
 *@see     MultiLangFacadeIF			
 **/
public class MultiLangBaseProperty implements MultiLangFacadeIF {

    private Locale locale;
    private MultiLangDaoFacadeIF daoProperties;

    public MultiLangBaseProperty() {
        this.locale = Locale.getDefault();
        daoProperties = new MultiLangDaoProperties(locale);
    }

    public MultiLangBaseProperty(Locale locale) {
        this.locale = locale;
        daoProperties = new MultiLangDaoProperties(locale);
    }

    /**
     *此Method的功能描述：將多國語系編號轉換成設定語系的文字
     * 
     *@param  傳入多國語系編號
     *@return  設定語系文字
     *@throws 若有丟出Exception 的說明     (Required)
     *@see   
     *@since   此Method 從哪個版本開始加入(外部的version)
     **/
    public String getById(String id_s) {

        return daoProperties.findtById(id_s);
    }

    /**
     *此Method的功能描述：將一組多國語系編號轉換成一組設定語系的文字
     * 
     *@param 傳入一組多國語系編號
     *@return  回傳一組設定語系文字
     *@throws 若有丟出Exception 的說明     (Required)
     *@see   
     *@since   此Method 從哪個版本開始加入(外部的version)
     **/
    public List<String> getByIds(List<String> list) {
        List<String> ids = new ArrayList<String>();
        for (String id : list) {
            ids.add(daoProperties.findtById(id));
        }
        return ids;
    }

    /**
     *此Method的功能描述：將多國語系群組編號轉換成以該群組為一組設定語系的文字
     * 
     *@param 傳入群組多國語系編號
     *@return  回傳一組設定語系文字(以屬性為主的HashMap)
     *@throws 若有丟出Exception 的說明     (Required)
     *@see   
     *@since   此Method 從哪個版本開始加入(外部的version)
     **/
    public List<IdGroupType> getByGroup(String gruop_s) {
        //msg_01_validation_001
        List<IdGroupType> list = new ArrayList<IdGroupType>();
        list = daoProperties.findGroup(gruop_s);
        return list;

    }

    /**
     *此Method的功能描述：取得設定的語系與國別
     * 
     *@param 
     *@return  回傳Locale物件
     *@throws 若有丟出Exception 的說明     (Required)
     *@see   
     *@since   此Method 從哪個版本開始加入(外部的version)
     **/
    public Locale getLocale() {
        return locale;
    }

    /**
     *此Method的功能描述：設定語系與國別
     * 
     *@param 傳入Locale物件
     *@return  
     *@throws 若有丟出Exception 的說明     (Required)
     *@see   
     *@since   此Method 從哪個版本開始加入(外部的version)
     **/
    public void setLocale(Locale locale) {
        this.locale = locale;
        daoProperties.changeLocale(locale);
    }
}
