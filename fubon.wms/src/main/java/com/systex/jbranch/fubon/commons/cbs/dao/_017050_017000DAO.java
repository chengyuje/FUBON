package com.systex.jbranch.fubon.commons.cbs.dao;

import com.systex.jbranch.fubon.commons.cbs.vo._017050_017000.CBS017050InputVO;
import com.systex.jbranch.fubon.commons.cbs.vo.basic.CBSUtilOutputVO;
import com.systex.jbranch.fubon.commons.tx.stuff.Worker;
import com.systex.jbranch.fubon.commons.tx.traffic.Cbs;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * 電文 017050 發送至 CBS 回傳 017000
 */
@Repository
public class _017050_017000DAO {
    /**
     * 查詢貸款詳細資料
     * 測試抓訂約額度
     *
     * @param loanAcct 貸款帳號
     */
    public List<CBSUtilOutputVO> search(String loanAcct) throws Exception {
        List<CBSUtilOutputVO> list = new ArrayList();
        Worker.call()
                .assign(Worker.CBS, "017050")
                .setRequest(Cbs.createRequestVO()
                        .setPickUpId("017000")
                        .setModule("_017050_017000DAO.search")
                        .setTxHeadVO(Cbs.createTxHeadVO()
                                .setUUIDSuffix("90001"))
                        .setCbs017050InputVO(cbs017050(loanAcct)))
                .work()
                .getResponse(list);
        return list;
    }

    /**
     * 電文 InputVO
     **/
    private CBS017050InputVO cbs017050(String loanAcct) {
        CBS017050InputVO inputVO = new CBS017050InputVO();
        inputVO.setAccntNumber1(loanAcct);
        return inputVO;
    }
}
