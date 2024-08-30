package com.systex.jbranch.platform.common.errHandle;

import java.util.List;

/**
 *此類別的功能描述：JOtherException的Exception物件，繼承JBranchException
 *此程式的注意事項：此物件為與JBranchException皆無關的exception物件，JBranchException中找不到的錯誤，就throw這個exception
 *
 *@author  Benson Chen
 *@since   此Class 從哪個版本開始加入  (外部的version)
 *@see	JBranchException	
 **/
public class JOtherException extends JBranchException {

    private Exception _Exception;

//    public JOtherException() {
//    }

    public JOtherException(EnumErrInputType errInputType,String value){
    	super(errInputType,value); 	
    }
    
    public JOtherException(String ml_idGroup_s) {
        super(ml_idGroup_s);
    }
    
    /**
     *此類別的功能描述：建立JOtherException物件
     *此程式的注意事項：傳入多國語系群組編號並call super constructor
     *
     *@param  傳入多國語系群組編號 
     *@author  Benson Chen
     *@since   此Class 從哪個版本開始加入  (外部的version)	
     *@see
     **/
    public JOtherException(String ml_idGroup_s, List<String> interMsgList) {
        super(ml_idGroup_s, interMsgList);
    }

//    /**
//     *此類別的功能描述：建立JOtherException物件
//     *此程式的注意事項：傳入多國語系群組編號及使用者權限物件並call super constructor
//     * 
//     *@param  傳入使用者權限物件(包含該使用者的ExceptionLevel等級)
//     *@param  傳入多國語系群組編號
//     *@author  Benson Chen
//     *@since   此Class 從哪個版本開始加入  (外部的version)	
//     *@see
//     **/
//    public JOtherException(UUID userPrivilege_s, String ml_idGroup_s, List<String> interMsgList) {
//        super(userPrivilege_s, ml_idGroup_s, interMsgList);
//    }

    /**
     *此類別的功能描述：建立JOtherException物件
     *此程式的注意事項：傳入多國語系群組編號並call super constructor
     *
     *@param  傳入多國語系群組編號 
     *@author  Benson Chen
     *@since   此Class 從哪個版本開始加入  (外部的version)	
     *@see
     **/
    public JOtherException(String ml_idGroup_s, List<String> interMsgList, Exception ex) {
        super(ml_idGroup_s, interMsgList);
        this.setException(ex);
    }

//    /**
//     *此類別的功能描述：建立JOtherException物件
//     *此程式的注意事項：傳入多國語系群組編號及使用者權限物件並call super constructor
//     * 
//     *@param  傳入使用者權限物件(包含該使用者的ExceptionLevel等級)
//     *@param  傳入多國語系群組編號
//     *@author  Benson Chen
//     *@since   此Class 從哪個版本開始加入  (外部的version)	
//     *@see
//     **/
//    public JOtherException(UUID userPrivilege_s, String ml_idGroup_s, List<String> interMsgList, Exception ex) {
//        super(userPrivilege_s, ml_idGroup_s, interMsgList);
//        this.setException(ex);
//    }

    public Exception getException() {
        return this._Exception;
    }

    public void setException(Exception ex) {
        this._Exception = ex;
    }
}
