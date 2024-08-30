package com.systex.jbranch.platform.common.multiLang;

/**
 *此類別的功能描述：多國語系的編號型態
 * Notices: 此程式的注意事項：無
 *
 *@author  Benson Chen
 *@since   此Class 從哪個版本開始加入  (外部的version)
 *@see     			
 **/
public class MultiLangIdType {

    private String type_s;
    private String typeSeq_s;
    private String groupname_s;
    private String groupSeq_s;
    private String attribute_s;
    private String seq_s;

    /**
     *此Method的功能描述：取出多國語系的存檔型別
     * 
     *@param  
     *@return  傳回存檔別
     *@throws 若有丟出Exception 的說明     (Required)
     *@see   
     *@since   此Method 從哪個版本開始加入(外部的version)
     **/
    public String getType_s() {
        return type_s;
    }

    /**
     *此Method的功能描述：設定多國語系的存檔型別
     * 
     *@param 存檔型別 
     *@return  
     *@throws 若有丟出Exception 的說明     (Required)
     *@see   
     *@since   此Method 從哪個版本開始加入(外部的version)
     **/
    void setType_s(String type_s) {
        this.type_s = type_s;
    }

    /**
     *此Method的功能描述：取出多國語系存檔型別序號
     * 
     *@param  
     *@return  傳出存檔型別序號
     *@throws 若有丟出Exception 的說明     (Required)
     *@see   
     *@since   此Method 從哪個版本開始加入(外部的version)
     **/
    public String getTypeSeq_s() {
        return typeSeq_s;
    }

    /**
     *此Method的功能描述：設定多國語系存檔型別序號
     * 
     *@param  傳入存檔型別序號
     *@return  
     *@throws 若有丟出Exception 的說明     (Required)
     *@see   
     *@since   此Method 從哪個版本開始加入(外部的version)
     **/
    void setTypeSeq_s(String typeSeq_s) {
        this.typeSeq_s = typeSeq_s;
    }
    
    /**
     *此Method的功能描述：取出多國語系的群組型別中的群組名稱
     * 
     *@param  
     *@return  傳回群組名稱
     *@throws 若有丟出Exception 的說明     (Required)
     *@see   
     *@since   此Method 從哪個版本開始加入(外部的version)
     **/
    public String getGroupname_s() {
        return groupname_s;
    }

    /**
     *此Method的功能描述：設定多國語系的群組型別中的群組名稱
     * 
     *@param  傳入群組名稱
     *@return  
     *@throws 若有丟出Exception 的說明     (Required)
     *@see   
     *@since   此Method 從哪個版本開始加入(外部的version)
     **/
    void setGroupname_s(String group_s) {
        this.groupname_s = group_s;
    }

    /**
     *此Method的功能描述：取出多國語系的群組型別中的群組序號
     * 
     *@param  
     *@return  傳回群組序號
     *@throws 若有丟出Exception 的說明     (Required)
     *@see   
     *@since   此Method 從哪個版本開始加入(外部的version)
     **/
    public String getGroupSeq_s() {
        return groupSeq_s;
    }

    /**
     *此Method的功能描述：設定多國語系的群組型別中的群組序號
     * 
     *@param  傳入群組序號
     *@return  
     *@throws 若有丟出Exception 的說明     (Required)
     *@see   
     *@since   此Method 從哪個版本開始加入(外部的version)
     **/
    void setGroupSeq_s(String groupSeq_s) {
        this.groupSeq_s = groupSeq_s;
    }

    /**
     *此Method的功能描述：取出多國語系的群組型別中的屬性名稱
     * 
     *@param  
     *@return  傳回屬性名稱
     *@throws 若有丟出Exception 的說明     (Required)
     *@see   
     *@since   此Method 從哪個版本開始加入(外部的version)
     **/
    public String getAttribute_s() {
        return attribute_s;
    }

    /**
     *此Method的功能描述：設定多國語系的群組型別中的屬性名稱
     * 
     *@param  傳入屬性名稱
     *@return  
     *@throws 若有丟出Exception 的說明     (Required)
     *@see   
     *@since   此Method 從哪個版本開始加入(外部的version)
     **/
    void setAttribute_s(String attribute_s) {
        this.attribute_s = attribute_s;
    }

    /**
     *此Method的功能描述：取出多國語系的序號
     * 
     *@param  
     *@return  傳回序號
     *@throws 若有丟出Exception 的說明     (Required)
     *@see   
     *@since   此Method 從哪個版本開始加入(外部的version)
     **/
    public String getSeq_s() {
        return seq_s;
    }

    /**
     *此Method的功能描述：設定多國語系的序號
     * 
     *@param  傳入序號
     *@return  
     *@throws 若有丟出Exception 的說明     (Required)
     *@see   
     *@since   此Method 從哪個版本開始加入(外部的version)
     **/
    void setSeq_s(String seq_s) {
        this.seq_s = seq_s;
    }    
}
