package com.systex.jbranch.app.server.fps.insjlb.callBack;

import com.systex.jbranch.comutil.callBack.CallBackExcute;

public enum InsjlbCallBackFactory {
	DO_SET_IS_DETAIL(DoSetIsDetailCb.class),
	CHG_KEY_FOR_COV01_DEF_VAL(ChgKeyForCoverage01DefVal.class);
	
	private CallBackExcute callBack;
	
	private InsjlbCallBackFactory(Class cls){
		try {
			this.callBack = (CallBackExcute)cls.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	public CallBackExcute getCallBack(){
		return this.callBack;
	}
}
