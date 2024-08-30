package com.systex.jbranch.fubon.commons.cbs.dao;

import com.systex.jbranch.fubon.commons.cbs.vo._067157_067157.CBS067157InputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._067157_067157.CBS067157OutputVO;
import com.systex.jbranch.fubon.commons.cbs.vo.basic.CBSUtilOutputVO;
import com.systex.jbranch.fubon.commons.tx.stuff.Worker;
import com.systex.jbranch.fubon.commons.tx.traffic.Cbs;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * 電文 067157 發送至 CBS 回傳 067157 (查詢 / 更新)
 *
 */
@Repository
public class _067157_067157DAO {
    /**
     * 查風險等級用 (C值)
     *
     * @param id     客戶 ID
     * @param idType 客戶 ID TYPE
     * @return
     */
    public List<CBSUtilOutputVO> search(String id, String idType) throws Exception {
        List<CBSUtilOutputVO> list = new ArrayList();
        Worker.call()
                .assign(Worker.CBS, "067157")
                .setRequest(Cbs.createRequestVO()
                        .setPickUpId("067157")
                        .setModule("_067157_067157DAO.search")
                        .setTxHeadVO(Cbs.createTxHeadVO())
                        .setCbs067157InputVO(cbs067157inputVO(id, idType)))
                .work()
                .getResponse(list);
        return list;
    }

    /** 電文 InputVO **/
    private CBS067157InputVO cbs067157inputVO(String id, String idType) {
        CBS067157InputVO inputVO = new CBS067157InputVO();
        inputVO.setIdno(id);
        inputVO.setIdtype(idType);
        return inputVO;
    }

    /**
     * 更新 067157
     * 風險等級用 (C值)
     * @param updateVO CBS067157OutputVO
     */
    public void update067157(CBS067157OutputVO updateVO) throws Exception {
        Worker.call()
                .assign(Worker.CBS, "067157")
                .setRequest(Cbs.createRequestVO()
                        .setPickUpId("067157")
                        .setModule("_067157_067157DAO.update067157")
                        .setTxHeadVO(Cbs.createTxHeadVO()
                                .setUUIDSuffix("9001"))
                        .setCbs067157updateVO(updateVO))
                .work();
    }
}
