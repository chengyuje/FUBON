package com.systex.jbranch.fubon.commons.cbs.dao;

import com.systex.jbranch.fubon.commons.cbs.vo._010400_032105.CBS010400InputVO;
import com.systex.jbranch.fubon.commons.cbs.vo.basic.CBSUtilOutputVO;
import com.systex.jbranch.fubon.commons.tx.stuff.Worker;
import com.systex.jbranch.fubon.commons.tx.traffic.Cbs;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * 電文 010400 發送至 CBS 回傳 032105
 */
@Repository
public class _010400_032105DAO {
    /**
     * 查詢貸款詳細資料
     *
     * @param loanAcct 貸款帳號
     * @param yearTerm [optional] 就學貸款上送學年學期別用 應該是四位數
     */
    public List<CBSUtilOutputVO> search(String loanAcct, String yearTerm) throws Exception {
        List<CBSUtilOutputVO> list = new ArrayList();
        Worker.call()
                .assign(Worker.CBS, "010400")
                .setRequest(Cbs.createRequestVO()
                        .setPickUpId("032105")
                        .setModule("_010400_032105DAO.search")
                        .setTxHeadVO(Cbs.createTxHeadVO()
                                .setUUIDSuffix("90001"))
                        .setCbs010400InputVO(cbs010400(loanAcct, yearTerm)))
                .work()
                .getResponse(list);
        return list;
    }

    /**
     * 電文 InputVO
     **/
    private CBS010400InputVO cbs010400(String loanAcct, String yearTerm) {
        CBS010400InputVO inputVO = new CBS010400InputVO();
        inputVO.setAccntNumber1(loanAcct);
        inputVO.setYearterm(yearTerm);
        return inputVO;
    }
    
    
    /**
     * 查詢貸款詳細資料
     *
     * @param loanAcct 貸款帳號
     * 
     */
    public List<CBSUtilOutputVO> search(String loanAcct) throws Exception {
        List<CBSUtilOutputVO> list = new ArrayList();
        Worker.call()
                .assign(Worker.CBS, "010400")
                .setRequest(Cbs.createRequestVO()
                        .setPickUpId("032105")
                        .setModule("_010400_032105DAO.search")
                        .setTxHeadVO(Cbs.createTxHeadVO()
                                .setUUIDSuffix("90001"))
                        .setCbs010400InputVO(cbs010400(loanAcct)))
                .work()
                .getResponse(list);
        return list;
    }
    
    /**
     * 電文 InputVO
     **/
    private CBS010400InputVO cbs010400(String loanAcct) {
        CBS010400InputVO inputVO = new CBS010400InputVO();
        inputVO.setAccntNumber1(loanAcct);
        return inputVO;
    }
}
