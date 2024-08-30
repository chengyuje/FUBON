package com.systex.jbranch.app.server.fps.insjlb.callBack;

import com.systex.jbranch.app.server.fps.insjlb.InsjlbParamInf;
import com.systex.jbranch.comutil.callBack.CallBackExcute;
import com.systex.jbranch.comutil.collection.GenericMap;

public class DoSetIsDetailCb implements CallBackExcute{	
	//非B0013的埋IS_DETAIL
	public <T> T callBack(GenericMap genericMap) {
		//將INSCOMPANY是合計的設定0，不是的埋1給IS_DETAIL
		genericMap.put("IS_DETAIL" , genericMap.equals("INSCOMPANY", InsjlbParamInf.SUM_DESC) ? "0" : "1");
		return null;
	}
}
