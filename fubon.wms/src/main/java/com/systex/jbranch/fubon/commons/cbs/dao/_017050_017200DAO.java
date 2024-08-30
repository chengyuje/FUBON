package com.systex.jbranch.fubon.commons.cbs.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.systex.jbranch.fubon.commons.cbs.vo._017050_017000.CBS017050InputVO;
import com.systex.jbranch.fubon.commons.cbs.vo.basic.CBSUtilOutputVO;
import com.systex.jbranch.fubon.commons.tx.stuff.Worker;
import com.systex.jbranch.fubon.commons.tx.traffic.Cbs;


/**
 * 電文 017050 發送至 CBS 回傳 017200
 */
@Repository
public class _017050_017200DAO {

	/**
     * 查詢貸款詳細資料
     * 測試抓訂約額度
     * 輸入循環型放款帳號55061361014172、查出有關聯之分期型放款帳號55011361873784，
     * 再以此55011361873784帳號查詢01040000交易之放款餘額帶入。
     * 若01705000-B查出有多個分期型帳號時，需分別將01040000之放款餘額合計帶入
     *
     * @param loanAcct 貸款帳號
     */
    public List<CBSUtilOutputVO> search(String loanAcct) throws Exception {
        List<CBSUtilOutputVO> list = new ArrayList();
        Worker.call()
                .assign(Worker.CBS, "017050")
                .setRequest(Cbs.createRequestVO()
                        .setPickUpId("017200")
                        .setModule("_017050_017200DAO.search")
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
        inputVO.setDefaultString1("B");
        return inputVO;
    }
}
