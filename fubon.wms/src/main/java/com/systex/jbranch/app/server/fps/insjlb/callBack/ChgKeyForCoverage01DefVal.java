package com.systex.jbranch.app.server.fps.insjlb.callBack;

import com.systex.jbranch.comutil.callBack.CallBackExcute;
import com.systex.jbranch.comutil.collection.GenericMap;

public class ChgKeyForCoverage01DefVal implements CallBackExcute{
	private String [] reKeyNames = new String [] {
		"CountType"		, "IPremium"	, "RAllProductsID"	, "InsuredAge"		, "Sex"		, 
		"PayType"		, "AccuTerm"	, "PremTerm"		, "InsuredObject"	, "Kind"	, 
		"JobGrade"		, "Unit"		, "Plan"			, "Quantity"		, "IDay"	, 
		"ICount"		, "IObject"		, "SocialSecurity"	, "FieldG"			, "FieldX"	, 
		"PolicyDesc"
	};
	
	public <T> T callBack(GenericMap genericMap) {
		for(String eName : reKeyNames){
			if(genericMap.get(eName) == null){
				genericMap.put(eName , ""); 
			}
		}
		
		return null;
	}
}
