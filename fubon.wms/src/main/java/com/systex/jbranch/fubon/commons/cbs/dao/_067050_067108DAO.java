package com.systex.jbranch.fubon.commons.cbs.dao;

import com.systex.jbranch.fubon.commons.cbs.vo._067050_067115.CBS067050InputVO;
import com.systex.jbranch.fubon.commons.cbs.vo.basic.CBSUtilOutputVO;
import com.systex.jbranch.fubon.commons.tx.stuff.Worker;
import com.systex.jbranch.fubon.commons.tx.traffic.Cbs;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * 電文 067050 發送至 CBS 回傳 067108
 * 查詢：網銀取得戶名電文 電文FC032659
 * @author sam
 *
 */
@Repository
public class _067050_067108DAO {

	  public List<CBSUtilOutputVO> search(String custId, String idType) throws Exception {
	        List<CBSUtilOutputVO> list = new ArrayList();
	        Worker.call()
	                .assign(Worker.CBS, "067050")
	                .setRequest(Cbs.createRequestVO()
	                        .setPickUpId("067108")
	                        .setModule("_067050_067108DAO.search")
	                        .setTxHeadVO(Cbs.createTxHeadVO()
								.setSubsysChannel("F"))
	                        .setCbs067050InputVO(cbs067050(custId, idType)))
	                .work()
	                .getResponse(list);
	        return list;
	    }

	  /**
	     * 電文 InputVO
	     **/
	    private CBS067050InputVO cbs067050(String custId, String idType) {
	    	CBS067050InputVO inputVO = new CBS067050InputVO();
	    	inputVO.setDefInteger1("09");
	        inputVO.setId_type(idType); // TYPE
	        inputVO.setDefaultString2(custId); // ID
	        return inputVO;
	    }

}
