package com.systex.jbranch.fubon.commons.cbs.dao;

import com.systex.jbranch.fubon.commons.cbs.vo._067050_067115.CBS067050InputVO;
import com.systex.jbranch.fubon.commons.cbs.vo.basic.CBSUtilOutputVO;
import com.systex.jbranch.fubon.commons.tx.stuff.Worker;
import com.systex.jbranch.fubon.commons.tx.traffic.Cbs;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * 電文 067050 發送至 CBS 回傳 067102
 *
 *
 */
@Repository
public class _067050_067102DAO {
    /**
     * 查詢
     * @param id     客戶 ID
     * @param idType 客戶 ID TYPE
     * @return
     */
    public List<CBSUtilOutputVO> search(String id, String idType) throws Exception {
        List<CBSUtilOutputVO> list = new ArrayList();
        Worker.call()
                .assign(Worker.CBS, "067050")
                .setRequest(Cbs.createRequestVO()
                        .setPickUpId("067102")
                        .setModule("_067050_067102DAO.search")
                        .setTxHeadVO(Cbs.createTxHeadVO()
                                .setSubsysChannel("F")
                                .setUUIDSuffix("00001"))
                        .setCbs067050InputVO(cbs067050inputVO(id, idType)))
                .work()
                .getResponse(list);
        return list;
    }

    /**
     * 電文 InputVO
     */
    private CBS067050InputVO cbs067050inputVO(String id, String idType) {
        CBS067050InputVO inputVO = new CBS067050InputVO();
        inputVO.setDefInteger1("06");
        inputVO.setDefaultString2(id);
        inputVO.setId_type(idType);
        return inputVO;
    }
}
