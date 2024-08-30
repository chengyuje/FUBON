package com.systex.jbranch.platform.common.errHandle;

public enum EnumErrInputType {

	/*
     char(2)
     00表示使用Multilang_id
     01表示回傳msg     
    */
    
	MUTILANG_ID("00"),MSG("01");
            
     public final String code;
    
    private EnumErrInputType(String code){
        this.code = code;
    }
    
    public String getErrInputTypeCode(){
        return this.code;
    }
}
