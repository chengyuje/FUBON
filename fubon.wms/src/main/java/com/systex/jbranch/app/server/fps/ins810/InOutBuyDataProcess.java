package com.systex.jbranch.app.server.fps.ins810;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

import com.systex.jbranch.app.server.fps.insjlb.InsjlbUtils;
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.platform.common.errHandle.JBranchException;

public enum InOutBuyDataProcess {
	PAYMENTYEAR_SEL_NAME(true , true);
	
	private boolean isIn = false;
	private boolean isOut = false;
	
	private InOutBuyDataProcess(boolean isIn , boolean isOut){
		this.isIn = isIn;
		this.isOut = isOut;
	}
	
	public static void main(String...args){
		String name = "PAYMENTYEAR_SEL_NAME";
		StringBuffer methodName = new StringBuffer("doSet");
		
		for(String mName : name.toLowerCase().split("_")){
			methodName.append(mName.substring(0 , 1).toUpperCase() + mName.substring(1));
		}
	}
	
	public void excute(List<Map<String , Object>> dataList , GenericMap genMap) throws JBranchException{
		StringBuffer methodName = new StringBuffer("doSet");
		
		for(String mName : this.name().toLowerCase().split("_")){
			methodName.append(mName.substring(0 , 1).toUpperCase() + mName.substring(1));
		}
		
		try{
			for(Map dataMap : dataList){
				this.getClass()
					.getMethod(methodName.toString() , new Class[]{Map.class , GenericMap.class})
					.invoke(this , new Object[]{dataMap , genMap});
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
			throw new JBranchException(ex);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void doSetPaymentyearSelName(Map inOutBuyData , GenericMap genMap){
		GenericMap inOutBuyGmap = new GenericMap(inOutBuyData);
		
		// 增加取得 繳費年期資料
		String item = inOutBuyGmap.getNotNullStr("ITEM_Y");
		String list = inOutBuyGmap.getNotNullStr("LIST_Y");
		String paymentyearSel = inOutBuyGmap.getNotNullStr("PAYMENTYEAR_SEL");
		int index = -1;
		
		if(StringUtils.isBlank(list) || StringUtils.isBlank(paymentyearSel)){
			inOutBuyData.put("PAYMENTYEAR_SEL_NAME", "");
			return;
		}
		
		if(inOutBuyGmap.getNotNullStr("INOUT").equals("1") && "99".equals(paymentyearSel)) {
			inOutBuyData.put("PAYMENTYEAR_SEL_NAME", paymentyearSel);
			return;
		}
		
		GenericMap itemResult = new GenericMap(
			InsjlbUtils.findSimilarIntVal(item , paymentyearSel , "," , inOutBuyGmap.getNotNullStr("INOUT").equals("1")));
		
		if(MapUtils.isNotEmpty(itemResult.getParamMap())) {
			inOutBuyData.put("PAYMENTYEAR_SEL_NAME", 
				list.split(",")[itemResult.getBigDecimal("idx").intValue()]
			);
		}
		else{
			inOutBuyData.put("PAYMENTYEAR_SEL_NAME" , list.split(",")[0]);
		}		
	}

	public boolean isIn() {
		return isIn;
	}

	public void setIn(boolean isIn) {
		this.isIn = isIn;
	}

	public boolean isOut() {
		return isOut;
	}

	public void setOut(boolean isOut) {
		this.isOut = isOut;
	}
}
