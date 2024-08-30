package com.systex.jbranch.common.sorterinfo;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataSortManager;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Repository("sorterinfo")
@Scope("prototype")
public class SorterInfo extends FubonWmsBizLogic {
	
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * @author ArthurKO
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void getVO(Object body, IPrimitiveMap<Object> header) throws JBranchException {
//		String randRortId = UUID.randomUUID().toString();
//		SorterInputVO inputVO = (SorterInputVO)body;
//		Map<DataSortManager.Type , Object> paramMap = null;
//		
//		if(inputVO.isCheckin()){
//			paramMap = new HashMap<DataSortManager.Type , Object>();
//			paramMap.put(DataSortManager.Type.COLUMN , inputVO.getColumn());
//			paramMap.put(DataSortManager.Type.ASC , inputVO.isAsc());
//			DataSortManager.setSortPool(paramMap);
//		}
//		
//		this.sendRtnObject(randRortId);
	}
}