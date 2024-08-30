package com.systex.jbranch.platform.common.errHandle;

import java.util.EnumMap;
import java.util.Map;

/**
 *此類別的功能描述：JavaBranch Exception層級的VO物件
 *此程式的注意事項：ExceptionLevel等級為EndUser,Developer,SysAdmin)
 *其中Coder填入原始exception資訊(此由應用程式面填入)
 *
 *@author  Benson Chen
 *@since   此Class 從哪個版本開始加入  (外部的version)
 *@see	JBranchException	
 **/
public class ErrLevelVO {

    private Map<EnumErrLevel, String> errLevelMap;
    private EnumErrLevel userErrLevel;

    public ErrLevelVO() {
        errLevelMap = new EnumMap<EnumErrLevel, String>(EnumErrLevel.class);
    }

    public ErrLevelVO(EnumErrLevel errLevel) {
        errLevelMap = new EnumMap<EnumErrLevel, String>(EnumErrLevel.class);
        userErrLevel = errLevel;
    }

    public ErrLevelVO(String errLevel) {
        errLevelMap = new EnumMap<EnumErrLevel, String>(EnumErrLevel.class);
        EnumErrLevel[] enumErrLevel = EnumErrLevel.values();
        for (EnumErrLevel enumErrLevel_inner : enumErrLevel) {
            if (enumErrLevel_inner.getErrLevelCode().equals(errLevel)) {
                userErrLevel = enumErrLevel_inner;
            }
        }
    }

//    public String getMessageBySpecialLevel() {
//        if (errLevelMap.containsKey(this.userErrLevel)) {
//            return errLevelMap.get(this.userErrLevel);
//        }
//        return null;
//    }

    public String getMessage(EnumErrLevel errLevel) {
        return errLevelMap.get(errLevel);
    }

    public void setMessage(EnumErrLevel errLevel, String message) {
        if (errLevelMap.containsKey(errLevel)) {
            errLevelMap.remove(errLevel);
            errLevelMap.put(errLevel, message);
        } else {
            errLevelMap.put(errLevel, message);
        }
    }
    
    public Map<EnumErrLevel, String> getAllErrLevel(){
        return errLevelMap;
    }
}
