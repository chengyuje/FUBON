package com.systex.jbranch.platform.common.multiLang;

import com.systex.jbranch.platform.common.errHandle.MultiLangException;
import java.util.Locale;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTree;

/**
 *此類別的功能描述：多國語系的一般共用介面實作(使用property的方式)
 * Notices: 此程式的注意事項：無
 *
 *@author  Benson Chen
 *@since   此Class 從哪個版本開始加入  (外部的version)
 *@see     MultiLangFacade_IF			
 **/
public class MultiLang extends MultiLangBaseProperty implements MultiLangFacadeWithUiIF, MultiLangFacadeWithVOIF {

    public MultiLang(Locale locale) throws MultiLangException {
        super.setLocale(locale);
    }

    /**
     *此Method的功能描述：將多國語系群組編號轉換成設定語系的Jmenu元件文字
     * 
     *@param  傳入多國語系群組編號
     *@return  設定語系後的Jmenu元件
     *@throws 若有丟出Exception 的說明     (Required)
     *@see   
     *@since   此Method 從哪個版本開始加入(外部的version)
     **/
    public JMenu getUiMenu(String group_s) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     *此Method的功能描述：將多國語系群組編號轉換成設定語系的JPopupMenu元件文字
     * 
     *@param  傳入多國語系群組編號
     *@return  設定語系後的JPopupMenu元件
     *@throws 若有丟出Exception 的說明     (Required)
     *@see   
     *@since   此Method 從哪個版本開始加入(外部的version)
     **/
    public JPopupMenu getUiPopupMenu(String group_s) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     *此Method的功能描述：將多國語系群組編號轉換成設定語系的JTable元件文字
     * 
     *@param  傳入多國語系群組編號
     *@return  設定語系後的JTable元件
     *@throws 若有丟出Exception 的說明     (Required)
     *@see   
     *@since   此Method 從哪個版本開始加入(外部的version)
     **/
    public JTable getUiTable(String group_s) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     *此Method的功能描述：將多國語系群組編號轉換成設定語系的JComboBox元件文字
     * 
     *@param  傳入多國語系群組編號
     *@return  設定語系的JComboBox元件
     *@throws 若有丟出Exception 的說明     (Required)
     *@see   
     *@since   此Method 從哪個版本開始加入(外部的version)
     **/
    public JComboBox getUiCombo(String group_s) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     *此Method的功能描述：將多國語系群組編號轉換成設定語系的JList元件文字
     * 
     *@param  傳入多國語系群組編號
     *@return  設定語系後的JList元件
     *@throws 若有丟出Exception 的說明     (Required)
     *@see   
     *@since   此Method 從哪個版本開始加入(外部的version)
     **/
    public JList getUiList(String group_s) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     *此Method的功能描述：將多國語系群組編號轉換成設定語系的JTree元件文字
     * 
     *@param  傳入多國語系群組編號
     *@return  設定語系後的JTree元件
     *@throws 若有丟出Exception 的說明     (Required)
     *@see   
     *@since   此Method 從哪個版本開始加入(外部的version)
     **/
    public JTree getUiTree(String group_s) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
