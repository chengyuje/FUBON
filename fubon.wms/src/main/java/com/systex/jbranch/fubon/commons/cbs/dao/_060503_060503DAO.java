package com.systex.jbranch.fubon.commons.cbs.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.systex.jbranch.fubon.commons.cbs.vo._060425_060433.CBS060425InputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._060503_060503.CBS060503InputVO;
import com.systex.jbranch.fubon.commons.cbs.vo.basic.CBSUtilOutputVO;
import com.systex.jbranch.fubon.commons.tx.stuff.Worker;
import com.systex.jbranch.fubon.commons.tx.traffic.Cbs;

/**
 * 電文 060503 發送至 CBS 回傳 060503
 */
@Repository
public class _060503_060503DAO {

	 public List<CBSUtilOutputVO> search(String email) throws Exception {
	        List<CBSUtilOutputVO> list = new ArrayList();
	        Worker.call()
	                .assign(Worker.CBS, "060503")
	                .setRequest(Cbs.createRequestVO()
	                        .setPickUpId("060503")
	                        .setModule("_060503_060503DAO.search")
	                        .setTxHeadVO(Cbs.createTxHeadVO())
	                        .setCbs060503InputVO(cbs060503(email)))
	                .work()
	                .getResponse(list);
	        return list;
	    }

	    /** 電文 InputVO **/
	    private CBS060503InputVO cbs060503(String email) {
	    	CBS060503InputVO inputVO = new CBS060503InputVO();
	        inputVO.setEmail_addr(email);;
//	        inputVO.setStartfr();
	        return inputVO;
	    }

}
