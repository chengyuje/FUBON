package com.systex.jbranch.fubon.commons.cbs.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.systex.jbranch.fubon.commons.cbs.vo._017079_017079.CBS017079InputVO;
import com.systex.jbranch.fubon.commons.cbs.vo.basic.CBSUtilOutputVO;
import com.systex.jbranch.fubon.commons.tx.stuff.Worker;
import com.systex.jbranch.fubon.commons.tx.traffic.Cbs;



/**
 * 電文 017079 發送至 CBS 回傳 017079
 * 分期型房貸取每月應繳月付金
 */
@Repository
public class _017079_017079DAO {

    /**
     * 查詢
     */
    public List<CBSUtilOutputVO> search(String acct, String term) throws Exception {
        List<CBSUtilOutputVO> list = new ArrayList();
        Worker.call()
                .assign(Worker.CBS, "017079")
                .setRequest(Cbs.createRequestVO()
                        .setPickUpId("017079")
                        .setModule("_017079_017079DAO.search")
                        .setTxHeadVO(Cbs.createTxHeadVO()
//                                .setFLAG4("N")
//                                .setChannel_ID("01")
                                .setUUIDSuffix("00001"))
                        .setCbs017079InputVO(cbs017079(acct, term)))
                .work()
                .getResponse(list);
        return list;
    }
    
    /**
     * 電文 InputVO
     **/
    private CBS017079InputVO cbs017079(String acct, String term) {
        CBS017079InputVO inputVO = new CBS017079InputVO();
        inputVO.setAccntNumber1(acct);
        inputVO.setOpt("A");
        inputVO.setTerm1(term);
        inputVO.setTerm2(term);
        return inputVO;
    }
    
    /**
     * 就學貸款查詢用
     */
    public List<CBSUtilOutputVO> search(String acct, String term, String DOC_NO) throws Exception {
        List<CBSUtilOutputVO> list = new ArrayList();
        Worker.call()
                .assign(Worker.CBS, "017079")
                .setRequest(Cbs.createRequestVO()
                        .setPickUpId("017079")
                        .setModule("_017079_017079DAO.search")
                        .setTxHeadVO(Cbs.createTxHeadVO()
//                                .setFLAG4("N")
//                                .setChannel_ID("01")
                                .setUUIDSuffix("00001"))
                        .setCbs017079InputVO(cbs017079(acct, term, DOC_NO)))
                .work()
                .getResponse(list);
        return list;
    }
    
    /**
     * 電文 InputVO
     **/
    private CBS017079InputVO cbs017079(String acct, String term, String DOC_NO) {
        CBS017079InputVO inputVO = new CBS017079InputVO();
        inputVO.setAccntNumber1(acct);
        inputVO.setOpt("A");
        inputVO.setTerm1(term);
        inputVO.setTerm2(term);
        inputVO.setYrTerm(DOC_NO);
        return inputVO;
    }

}
