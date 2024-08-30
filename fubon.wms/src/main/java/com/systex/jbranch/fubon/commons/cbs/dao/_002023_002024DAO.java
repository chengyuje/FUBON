package com.systex.jbranch.fubon.commons.cbs.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.systex.jbranch.fubon.commons.cbs.vo._002023_002024.CBS002023InputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._010400_032105.CBS010400InputVO;
import com.systex.jbranch.fubon.commons.cbs.vo.basic.CBSUtilOutputVO;
import com.systex.jbranch.fubon.commons.tx.stuff.Worker;
import com.systex.jbranch.fubon.commons.tx.traffic.Cbs;

/**
 * 電文 002023 發送至 CBS 回傳 002024
 */
@Repository
public class _002023_002024DAO {
    /**
     * 查詢循環型貸款詳細資料
     *
     * @param loanAcct 放款透支帳號
     * @param curr 幣別
     */
    public List<CBSUtilOutputVO> search(String loanAcct, String curr) throws Exception {
        List<CBSUtilOutputVO> list = new ArrayList();
        Worker.call()
                .assign(Worker.CBS, "002023")
                .setRequest(Cbs.createRequestVO()
                        .setPickUpId("002024")
                        .setModule("_002023_002024DAO.search")
                        .setTxHeadVO(Cbs.createTxHeadVO())
                        .setCbs002023InputVO(cbs002023(loanAcct, curr)))
                .work()
                .getResponse(list);
        return list;
    }

    /**
     * 電文 InputVO
     **/
    private CBS002023InputVO cbs002023(String loanAcct, String curr) {
    	CBS002023InputVO inputVO = new CBS002023InputVO();
        inputVO.setAccntNumber1(loanAcct);
        inputVO.setOverdraftOption1("09"); //送05會包含結清的帳號 09不含結清的帳戶
        inputVO.setFacNo("00000000000000000");
        inputVO.setCurrency(curr);
        return inputVO;
    }

}
