package com.systex.jbranch.fubon.commons.cbs.dao;

import com.systex.jbranch.fubon.commons.cbs.vo._000400_032041.CBS000400InputVO;
import com.systex.jbranch.fubon.commons.cbs.vo.basic.CBSUtilOutputVO;
import com.systex.jbranch.fubon.commons.tx.stuff.Worker;
import com.systex.jbranch.fubon.commons.tx.traffic.Cbs;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * 電文 000400 發送至 CBS 回傳 032041
 * 查詢台幣、外幣定存資料
 */
@Repository
public class _000400_032041DAO {
    /**
     * 查詢
     */
    public List<CBSUtilOutputVO> search(CBS000400InputVO inputVO) throws Exception {
        List<CBSUtilOutputVO> list = new ArrayList();
        Worker.call()
                .assign(Worker.CBS, "000400")
                .setRequest(Cbs.createRequestVO()
                        .setPickUpId("032041")
                        .setModule("_000400_032401DAO.search")
                        .setTxHeadVO(Cbs.createTxHeadVO()
                                .setSubsysChannel("F")
                                .setUUIDSuffix("00001"))
                        .setCbs000400Input(inputVO))
                .work()
                .getResponse(list);
        return list;
    }
}
