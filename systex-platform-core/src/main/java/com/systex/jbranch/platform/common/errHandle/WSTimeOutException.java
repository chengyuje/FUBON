/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.systex.jbranch.platform.common.errHandle;

import java.util.List;

/**
 *
 * @author Benson
 */
public class WSTimeOutException extends JBranchException {

	public WSTimeOutException(EnumErrInputType errInputType,String value){
    	super(errInputType,value); 	
    }
//    public PlatFormException(){
//        
//    }
    public WSTimeOutException(String ml_idGroup_s) {
        super(ml_idGroup_s);
    }

    /**
     *此類別的功能描述：建立PlatFormException物件
     *此程式的注意事項：傳入多國語系群組編號並call super constructor
     *
     *@param  傳入多國語系群組編號 
     *@author  Benson Chen
     *@since   此Class 從哪個版本開始加入  (外部的version)	
     *@see
     **/
    public WSTimeOutException(String ml_idGroup_s, List<String> interMsgList) {
        super(ml_idGroup_s, interMsgList);
    }//    /**
//     *此類別的功能描述：建立PlatFormException物件
//     *此程式的注意事項：傳入多國語系群組編號及使用者權限物件並call super constructor
//     * 
//     *@param  傳入使用者權限物件(包含該使用者的ExceptionLevel等級)
//     *@param  傳入多國語系群組編號
//     *@author  Benson Chen
//     *@since   此Class 從哪個版本開始加入  (外部的version)	
//     *@see
//     **/
//    public PlatFormException(UUID userPrivilege_s, String ml_idGroup_s, List<String> interMsgList) {
//        super(userPrivilege_s, ml_idGroup_s, interMsgList);
//    }
}
