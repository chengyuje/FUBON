package com.systex.jbranch.platform.common.errHandle;

import java.sql.SQLException;
import java.util.List;


/**
 *此類別的功能描述：JDataAccessException的Exception物件，繼承JBranchException
 *此程式的注意事項：此物件為與資料存取有關的上層物件
 *
 *@author  Benson Chen
 *@since   此Class 從哪個版本開始加入  (外部的version)
 *@see	JBranchException	
 **/
public class DAOException extends PlatFormException {

    private SQLException _sqlException;

//    public DAOException() {
//    }
    
    
    
    public DAOException(EnumErrInputType errInputType,String value){
    	super(errInputType,value); 	
    }

    public DAOException(EnumErrInputType errInputType, String value,
			Throwable cause) {
		super(errInputType, value, cause);
		// TODO Auto-generated constructor stub
	}

	public DAOException(String ml_idGroup_s) {
        super(ml_idGroup_s);
    }
    
    /**
     *此類別的功能描述：建立JDataAccessException物件
     *此程式的注意事項：傳入多國語系群組編號並call super constructor
     *
     *@param  傳入多國語系群組編號 
     *@author  Benson Chen
     *@since   此Class 從哪個版本開始加入  (外部的version)	
     *@see
     **/
    public DAOException(String ml_idGroup_s,List<String> interMsgList) {
        super(ml_idGroup_s,interMsgList);
    }

//    /**
//     *此類別的功能描述：建立JDataAccessException物件
//     *此程式的注意事項：傳入多國語系群組編號及使用者權限物件並call super constructor
//     * 
//     *@param  傳入使用者權限物件(包含該使用者的ExceptionLevel等級)
//     *@param  傳入多國語系群組編號
//     *@author  Benson Chen
//     *@since   此Class 從哪個版本開始加入  (外部的version)	
//     *@see
//     **/
//    public DAOException(UUID userPrivilege_s, String ml_idGroup_s,List<String> interMsgList) {
//        super(userPrivilege_s, ml_idGroup_s,interMsgList);
//    }

    /**
     *此類別的功能描述：建立JDataAccessException物件
     *此程式的注意事項：傳入多國語系群組編號並call super constructor
     *
     *@param  傳入多國語系群組編號 
     *@author  Benson Chen
     *@since   此Class 從哪個版本開始加入  (外部的version)	
     *@see
     **/
    public DAOException(String ml_idGroup_s,List<String> interMsgList, SQLException ex) {
        super(ml_idGroup_s,interMsgList);
        this.setSqlException(ex);
    }

//    /**
//     *此類別的功能描述：建立JDataAccessException物件
//     *此程式的注意事項：傳入多國語系群組編號及使用者權限物件並call super constructor
//     * 
//     *@param  傳入使用者權限物件(包含該使用者的ExceptionLevel等級)
//     *@param  傳入多國語系群組編號
//     *@author  Benson Chen
//     *@since   此Class 從哪個版本開始加入  (外部的version)	
//     *@see
//     **/
//    public DAOException(UUID userPrivilege_s, String ml_idGroup_s,List<String> interMsgList, SQLException ex) {
//        super(userPrivilege_s, ml_idGroup_s,interMsgList);
//        this.setSqlException(ex);
//    }

    public SQLException getSqlException() {
        return _sqlException;
    }

    public void setSqlException(SQLException sqlException) {
        this._sqlException = sqlException;
    }

}
