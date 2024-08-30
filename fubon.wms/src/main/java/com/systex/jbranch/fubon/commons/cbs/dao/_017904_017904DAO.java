package com.systex.jbranch.fubon.commons.cbs.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.systex.jbranch.fubon.commons.cbs.vo._017904_017904.CBS017904InputVO;
import com.systex.jbranch.fubon.commons.cbs.vo.basic.CBSUtilOutputVO;
import com.systex.jbranch.fubon.commons.tx.stuff.Worker;
import com.systex.jbranch.fubon.commons.tx.traffic.Cbs;


/**
 * 電文 017904 發送至 CBS 回傳 
 */
@Repository
public class _017904_017904DAO {

	
	
	/**
     * 查詢就學貸款資料
     *
     * @param loanAcct 貸款帳號
     * @param yearTerm [optional] 就學貸款上送學年學期別用 應該是四位數
     */
    public List<CBSUtilOutputVO> search(String loanAcct) throws Exception {
        List<CBSUtilOutputVO> list = new ArrayList();
        Worker.call()
                .assign(Worker.CBS, "017904")
                .setRequest(Cbs.createRequestVO()
                        .setPickUpId("017904")
                        .setModule("_017904_017904DAO.search")
                        .setTxHeadVO(Cbs.createTxHeadVO()
                                .setUUIDSuffix("90001"))
                        .setCbs017904InputVO(cbs017904(loanAcct)))
                .work()
                .getResponse(list);
        return list;
    }

    /**
     * 電文 InputVO
     **/
    private CBS017904InputVO cbs017904(String loanAcct) {
    	CBS017904InputVO inputVO = new CBS017904InputVO();
        inputVO.setAcctNO(loanAcct);
        return inputVO;
    }

}
