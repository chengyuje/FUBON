package com.systex.jbranch.fubon.commons.cbs.dao;

import com.systex.jbranch.fubon.commons.cbs.vo._000481_000482.CBS000481InputVO;
import com.systex.jbranch.fubon.commons.cbs.vo.basic.CBSUtilOutputVO;
import com.systex.jbranch.fubon.commons.tx.stuff.Worker;
import com.systex.jbranch.fubon.commons.tx.traffic.Cbs;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * 電文 000481 發送至 CBS 回傳 000482
 * 帳號相關資訊
 */
@Repository
public class _000481_000482DAO {
    /**
     * @param acct 帳號
     * @return
     */
    public List<CBSUtilOutputVO> search(String acct) throws Exception {
        List<CBSUtilOutputVO> list = new ArrayList();
        Worker.call()
                .assign(Worker.CBS, "000481")
                .setRequest(Cbs.createRequestVO()
                        .setPickUpId("000482")
                        .setModule("_000481_000482DAO.search")
                        .setTxHeadVO(Cbs.createTxHeadVO()
                                .setSubsysChannel("F")
                                .setUUIDSuffix("00001"))
                        .setCbs000481InputVO(cbs000481(acct)))
                .work()
                .getResponse(list);
        return list;
    }

    /**
     * 電文 inputVO
     */
    private CBS000481InputVO cbs000481(String acct) {
        CBS000481InputVO inputVO = new CBS000481InputVO();
        inputVO.setAccntNumber1(acct);
        return inputVO;
    }
}
