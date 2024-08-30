/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.systex.jbranch.platform.common.errHandle;


/**
 *
 * @author Benson
 */
public enum EnumErrLevel {
    /*
    tbsysteller.errlevel  char(2)
     00表示終端使用者
     01表示應用系統開發者
     02表示系統管理者
    */
    
    EndUser("00"),Developer("01"),SysAdmin("02");
            
     public final String errLevel;
    
    private EnumErrLevel(String errLevel){
        this.errLevel = errLevel;
    }
    
    public String getErrLevelCode(){
        return this.errLevel;
    }
}
