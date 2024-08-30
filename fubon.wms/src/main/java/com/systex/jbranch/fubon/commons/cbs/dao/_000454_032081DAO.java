package com.systex.jbranch.fubon.commons.cbs.dao;

import com.systex.jbranch.fubon.commons.cbs.vo._000454_032081.CBS000454InputVO;
import com.systex.jbranch.fubon.commons.cbs.vo.basic.CBSUtilOutputVO;
import com.systex.jbranch.fubon.commons.tx.stuff.Worker;
import com.systex.jbranch.fubon.commons.tx.traffic.Cbs;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * 電文 000454 發送至 CBS 回傳 032081
 * 一本萬利資料
 */
@Repository
public class _000454_032081DAO {
    /**
     * @param acct      帳號
     * @param startDate 起始日
     * @param endDate   起訖日
     * @return
     * @throws Exception
     */
    public List<CBSUtilOutputVO> search(String acct, String startDate, String endDate, String txType) throws Exception {
        List<CBSUtilOutputVO> list = new ArrayList();
        Worker.call()
                .assign(Worker.CBS, "000454")
                .setRequest(Cbs.createRequestVO()
                        .setPickUpId("032081")
                        .setModule("_000454_032081DAO.search")
                        .setTxHeadVO(Cbs.createTxHeadVO()
                                .setSubsysChannel("F"))
                        .setCbs000454InputVO(cbs000454(acct, startDate, endDate, txType)))
                .work()
                .getResponse(list);
        return list;
    }

    /**
     * 電文 InputVO
     **/
    private CBS000454InputVO cbs000454(String acct, String startDate, String endDate, String txType) {
        CBS000454InputVO inputVO = new CBS000454InputVO();
        inputVO.setSavingsAcct(acct);
        inputVO.setFromDate(startDate);
        inputVO.setEndDate(endDate);
        inputVO.setTXType(txType);
        return inputVO;
    }
}
