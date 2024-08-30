package com.systex.jbranch.fubon.commons.cbs.dao;

import java.util.ArrayList;
import java.util.List;






import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.systex.jbranch.fubon.commons.cbs.CBSService;
import com.systex.jbranch.fubon.commons.cbs.vo._062141_062144.CBS062141InputVO;
import com.systex.jbranch.fubon.commons.cbs.vo.basic.CBSUtilOutputVO;
import com.systex.jbranch.fubon.commons.tx.stuff.Worker;
import com.systex.jbranch.fubon.commons.tx.traffic.Cbs;

@Repository
public class _062141_062144DAO {
	
	@Autowired
	CBSService cbsService;

    public List<CBSUtilOutputVO> search(String custID, String colNo) throws Exception {
        List<CBSUtilOutputVO> list = new ArrayList();
        Worker.call()
                .assign(Worker.CBS, "062141")
                .setRequest(Cbs.createRequestVO()
                        .setPickUpId("062144")
                        .setModule("_062141_062144DAO.search")
                        .setTxHeadVO(Cbs.createTxHeadVO())
                        .setCbs062141InputVO(cbs062141(custID,colNo)))
                .work()
                .getResponse(list);
        return list;
    }
    
    /**
     * 電文 InputVO
     **/
    private CBS062141InputVO cbs062141(String custID, String colNo) {
    	CBS062141InputVO inputVO = new CBS062141InputVO();
        inputVO.setAction("A");
        inputVO.setIdNo(custID);
        inputVO.setIdType(cbsService.getCBSIDCode(custID));
        inputVO.setColNo(colNo);
        inputVO.setColType("1");//1. 有價證券 2. 不動產
        return inputVO;
    }

}
