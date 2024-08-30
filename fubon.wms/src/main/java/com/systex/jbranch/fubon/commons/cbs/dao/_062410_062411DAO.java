package com.systex.jbranch.fubon.commons.cbs.dao;

import com.systex.jbranch.fubon.commons.cbs.vo._062410_062411.CBS062410InputVO;
import com.systex.jbranch.fubon.commons.cbs.vo.basic.CBSUtilOutputVO;
import com.systex.jbranch.fubon.commons.tx.stuff.Worker;
import com.systex.jbranch.fubon.commons.tx.traffic.Cbs;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * 電文 062410 發送至 CBS 回傳 062411
 * 查詢客戶 W-8BEN 和 FATCA 客戶註記檢核
 */
@Repository
public class _062410_062411DAO {
    /**
     * 查詢
     * @param
     * @return
     */
    public List<CBSUtilOutputVO> search(String id, String idType) throws Exception {
        List<CBSUtilOutputVO> list = new ArrayList();
        Worker.call()
                .assign(Worker.CBS, "062410")
                .setRequest(Cbs.createRequestVO()
                        .setPickUpId("062411")
                        .setModule("_062410_062411DAO.search")
                        .setTxHeadVO(Cbs.createTxHeadVO())
                        .setCbs062410InputVO(cbs062410(id, idType)))
                .work()
                .getResponse(list);
        return list;
    }

    /**
     * 電文 inputVO
     */
    private CBS062410InputVO cbs062410(String id, String idType) {
        CBS062410InputVO inputVO = new CBS062410InputVO();
        inputVO.setIdNo(id);
        inputVO.setIdType(idType);
        return inputVO;
    }
}
