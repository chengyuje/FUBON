package com.systex.jbranch.fubon.commons.cbs.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.systex.jbranch.fubon.commons.cbs.vo._017050_017000.CBS017050InputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._060440_060441.CBS060440InputVO;
import com.systex.jbranch.fubon.commons.cbs.vo.basic.CBSUtilOutputVO;
import com.systex.jbranch.fubon.commons.tx.stuff.Worker;
import com.systex.jbranch.fubon.commons.tx.traffic.Cbs;


/**
 * 電文 060440 發送至 CBS 回傳 060441
 */
@Repository
public class _060440_060441DAO {

	/**
     * 查詢貸款詳細資料
     * 分期餘額
     *
     * @param loanAcct 貸款帳號
     */
    public List<CBSUtilOutputVO> search(String Acct, String txType) throws Exception {
        List<CBSUtilOutputVO> list = new ArrayList();
        Worker.call()
                .assign(Worker.CBS, "060440")
                .setRequest(Cbs.createRequestVO()
                        .setPickUpId("060441")
                        .setModule("_060440_060441DAO.search")
                        .setTxHeadVO(Cbs.createTxHeadVO()
                                .setUUIDSuffix("90001"))
                        .setCbs060440InputVO(cbs060440(Acct, txType )))
                .work()
                .getResponse(list);
        return list;
    }

    /**
     * 電文 InputVO
     **/
    private CBS060440InputVO cbs060440(String Acct, String txType) {
        CBS060440InputVO inputVO = new CBS060440InputVO();
        inputVO.setAccntNumber1(Acct);
        if(txType.equals("5004")){
        	 inputVO.setDefaultString1("E");
        } else if (txType.equals("5000")) {
        	inputVO.setDefaultString1("U");
        }
        inputVO.setDefaultString3("LON");
        inputVO.setDefInteger1("003");
        return inputVO;
    }

}
