package com.systex.jbranch.app.server.fps.sot712;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.errHandle.JBranchException;

@SuppressWarnings({"unchecked", "rawtypes"})
public abstract class SotPdf extends FubonWmsBizLogic{
	private PRDFitInputVO inputVO;
	
	public PRDFitInputVO getInputVO() {
		return inputVO;
	}

	public void setInputVO(PRDFitInputVO inputVO) {
		this.inputVO = inputVO;
	}

	/**
	 * abstract - print report
	 * @return List : report urlList
	 * @throws JBranchException 
	 * @throws Exception 
	 */
	public abstract List<String> printReport() throws JBranchException, Exception;
	
	protected List checkList (List<Map<?,?>> list) throws Exception {
		List newList = new ArrayList<>();
		for (Map m : list) {
			Map newMap = new HashMap();
			Iterator it = m.keySet().iterator();
			while (it.hasNext()) {
				Object o = it.next();
				if (m.get(o) != null) {
					newMap.put(o, m.get(o));
				} else {
					newMap.put(o, "");
				}
			}
			newList.add(newMap);
		}
		return newList;
	}
}
