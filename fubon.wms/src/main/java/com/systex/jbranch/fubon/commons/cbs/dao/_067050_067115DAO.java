package com.systex.jbranch.fubon.commons.cbs.dao;

import com.systex.jbranch.fubon.commons.cbs.vo._067050_067115.CBS067000OutputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._067050_067115.CBS067050InputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._067050_067115.CBS067101OutputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._067050_067115.CBS067115OutputVO;
import com.systex.jbranch.fubon.commons.cbs.vo.basic.CBSUtilOutputVO;
import com.systex.jbranch.fubon.commons.tx.stuff.Worker;
import com.systex.jbranch.fubon.commons.tx.traffic.Cbs;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * 電文 067050 發送至 CBS 回傳 067115（查詢客戶 Q 值相關資訊）
 * 電文 067115 發送至 CBS 回傳 067115（更新客戶 Q 值相關資訊）
 * 電文 067000 發送至 CBS 回傳 067000 (更新聯絡方式)
 * 電文 067101 發送至 CBS 回傳 067101 (更新客戶婚姻 職業 子女數 教育 電話...etc)
 *
 */
@Repository
public class _067050_067115DAO {
    /**
     * 客戶 Q 值與投資經驗等資訊
     *
     * @param id     客戶 ID
     * @param idType 客戶 ID TYPE
     * @return
     */
    public List<CBSUtilOutputVO> search(String id, String idType) throws Exception {
        List<CBSUtilOutputVO> list = new ArrayList();
        Worker.call()
                .assign(Worker.CBS, "067050")
                .setRequest(Cbs.createRequestVO()
                        .setPickUpId("067115")
                        .setModule("_067050_067115DAO.search")
                        .setTxHeadVO(Cbs.createTxHeadVO()
                                .setSubsysChannel("F")
                                .setUUIDSuffix("90001"))
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
        inputVO.setDefInteger1("21");
        inputVO.setDefaultString2(id);
        inputVO.setId_type(idType);
        return inputVO;
    }

    /**
     * 更新 067115
     * 客戶 Q 值相關資訊
     * @param updateVO CBS067115OutputVO
     */
    public void update067115(CBS067115OutputVO updateVO) throws Exception {
        Worker.call()
                .assign(Worker.CBS, "067115")
                .setRequest(Cbs.createRequestVO()
                        .setPickUpId("067115")
                        .setModule("_067050_067115DAO.update067115")
                        .setTxHeadVO(Cbs.createTxHeadVO())
                        .setCbs067115UpdateVO(updateVO))
                .work();
    }

    /**
     * 更新 067000
     * 聯絡方式
     * @param updateVO CBS067000OutputVO
     */
    public void update067000(CBS067000OutputVO updateVO) throws Exception {
        Worker.call()
                .assign(Worker.CBS, "067000")
                .setRequest(Cbs.createRequestVO()
                        .setPickUpId("067000")
                        .setModule("_067050_067115DAO.update067000")
                        .setTxHeadVO(Cbs.createTxHeadVO()
                                .setFlagX("")
                                .setUUIDSuffix("00001"))
                        .setCbs067000updateVO(updateVO))
                .work();
    }

    /**
     * 更新 067101
     * 客戶婚姻 職業 子女數 教育 電話...etc
     * @param updateVO CBS067101OutputVO
     */
    public void update067101(CBS067101OutputVO updateVO) throws Exception {
        Worker.call()
                .assign(Worker.CBS, "067101")
                .setRequest(Cbs.createRequestVO()
                        .setPickUpId("067101")
                        .setModule("_067050_067115DAO.update067101")
                        .setTxHeadVO(Cbs.createTxHeadVO()
                                .setFlagX("")
                                .setUUIDSuffix("00002"))
                        .setCbs067101updateVO(updateVO))
                .work();
    }
}
