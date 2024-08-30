package com.systex.jbranch.platform.common.errHandle;

import java.sql.SQLException;
import java.util.List;

/**
 *此類別的功能描述：DuplicateKeyException的Exception物件，繼承JDataAccessException
 *此程式的注意事項：此物件為資料庫重複鍵值錯誤的物件(record重複)
 *
 *@author  Benson Chen
 *@since   此Class 從哪個版本開始加入  (外部的version)
 *@see	JBranchException,DAOException	
 **/
public class DuplicateException extends DAOException{
//    public DuplicateException() {
//    }

	public DuplicateException(EnumErrInputType errInputType,String value){
    	super(errInputType,value); 	
    }
	
    public DuplicateException(String ml_idGroup_s) {
        super(ml_idGroup_s);
    }
    
    /**
     *此類別的功能描述：建立DuplicateKeyException物件
     *此程式的注意事項：傳入多國語系群組編號並call super constructor
     *
     *@param  傳入多國語系群組編號 
     *@author  Benson Chen
     *@since   此Class 從哪個版本開始加入  (外部的version)	
     *@see
     **/
    public DuplicateException(String ml_idGroup_s,List<String> interMsgList) {
        super(ml_idGroup_s,interMsgList);
    }

//    /**
//     *此類別的功能描述：建立DuplicateKeyException物件
//     *此程式的注意事項：傳入多國語系群組編號及使用者權限物件並call super constructor
//     * 
//     *@param  傳入使用者權限物件(包含該使用者的ExceptionLevel等級)
//     *@param  傳入多國語系群組編號
//     *@author  Benson Chen
//     *@since   此Class 從哪個版本開始加入  (外部的version)	
//     *@see
//     **/
//    public DuplicateException(UUID userPrivilege_s, String ml_idGroup_s,List<String> interMsgList) {
//        super(userPrivilege_s, ml_idGroup_s,interMsgList);
//    }

    /**
     *此類別的功能描述：建立DuplicateKeyException物件
     *此程式的注意事項：傳入多國語系群組編號並call super constructor
     *
     *@param  傳入多國語系群組編號 
     *@author  Benson Chen
     *@since   此Class 從哪個版本開始加入  (外部的version)	
     *@see
     **/
    public DuplicateException(String ml_idGroup_s,List<String> interMsgList,SQLException ex) {
        super(ml_idGroup_s,interMsgList,ex);
    }

//    /**
//     *此類別的功能描述：建立DuplicateKeyException物件
//     *此程式的注意事項：傳入多國語系群組編號及使用者權限物件並call super constructor
//     * 
//     *@param  傳入使用者權限物件(包含該使用者的ExceptionLevel等級)
//     *@param  傳入多國語系群組編號
//     *@author  Benson Chen
//     *@since   此Class 從哪個版本開始加入  (外部的version)	
//     *@see
//     **/
//    public DuplicateException(UUID userPrivilege_s, String ml_idGroup_s,List<String> interMsgList,SQLException ex) {
//        super(userPrivilege_s, ml_idGroup_s,interMsgList,ex);
//    }
}
