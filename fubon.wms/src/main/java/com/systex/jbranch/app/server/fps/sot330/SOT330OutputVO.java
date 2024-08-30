package com.systex.jbranch.app.server.fps.sot330;

import java.util.List;

import com.systex.jbranch.app.server.fps.sot707.BondGTCDataVO;
import com.systex.jbranch.fubon.commons.esb.vo.njbrvc3.NJBRVC3OutputDetailsVO;
import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

/**
 * SOT330 海外債成交結果查詢
 * 
 * @author SamTu
 * @date 2020.11.05
 */
public class SOT330OutputVO extends PagingOutputVO {

	private List<NJBRVC3OutputDetailsVO> resultList;

	public List<NJBRVC3OutputDetailsVO> getResultList() {
		return resultList;
	}

	public void setResultList(List<NJBRVC3OutputDetailsVO> resultList) {
		this.resultList = resultList;
	}

	
	
	

}
