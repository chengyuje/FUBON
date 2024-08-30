package com.systex.jbranch.fubon.commons.cbs.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.systex.jbranch.fubon.commons.cbs.vo._010400_032105.CBS010400InputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._062167_062167.CBS062167InputVO;
import com.systex.jbranch.fubon.commons.cbs.vo.basic.CBSUtilOutputVO;
import com.systex.jbranch.fubon.commons.tx.stuff.Worker;
import com.systex.jbranch.fubon.commons.tx.traffic.Cbs;


/**
 * 電文 062167 發送至 CBS 回傳 062167
 */
@Repository
public class _062167_062167DAO {

    public List<CBSUtilOutputVO> search(String loanAcct) throws Exception {
        List<CBSUtilOutputVO> list = new ArrayList();
        Worker.call()
                .assign(Worker.CBS, "062167")
                .setRequest(Cbs.createRequestVO()
                        .setPickUpId("062167")
                        .setModule("_062167_062167DAO.search")
                        .setTxHeadVO(Cbs.createTxHeadVO())
                        .setCbs062167InputVO(cbs062167(loanAcct)))
                .work()
                .getResponse(list);
        return list;
    }
    
    /**
     * 電文 InputVO
     **/
    private CBS062167InputVO cbs062167(String loanAcct) {
    	CBS062167InputVO inputVO = new CBS062167InputVO();
        inputVO.setLoan_acct1(loanAcct);
        inputVO.setStatus1("2");
        return inputVO;
    }

}
