package com.systex.jbranch.fubon.commons.cbs.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.systex.jbranch.fubon.commons.cbs.vo._062171_062171.CBS062171InputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._062171_062171.CBS062171OutputDetailsVO;
import com.systex.jbranch.fubon.commons.cbs.vo._062171_062171.CBS062171OutputVO;
import com.systex.jbranch.fubon.commons.cbs.vo.basic.CBSUtilOutputVO;
import com.systex.jbranch.fubon.commons.tx.stuff.Worker;
import com.systex.jbranch.fubon.commons.tx.traffic.Cbs;

@Repository
public class _062171_062171DAO {
	
	public List<CBSUtilOutputVO> search(String id, String idType) throws Exception {
		List<CBSUtilOutputVO> list = new ArrayList<>();
		Worker.call()
			  .assign(Worker.CBS, "062171")
			  .setRequest(
				  Cbs.createRequestVO()
				  	 .setPickUpId("062171")
				  	 .setModule("_062171_062171DAO.search")
				  	 .setTxHeadVO(Cbs.createTxHeadVO())
				  	 .setCbs062171InputVO(getCbs062171InputVO(id, idType))
			  )
			  .work()
			  .getResponse(list);
		return list;
	}
	
	private CBS062171InputVO getCbs062171InputVO (String id, String idType) {
		CBS062171InputVO inputVO = new CBS062171InputVO();
		inputVO.setIDNo(id);
		inputVO.setIDType(idType);
		inputVO.setDayNumber("90");
		return inputVO;
	}
 
}
