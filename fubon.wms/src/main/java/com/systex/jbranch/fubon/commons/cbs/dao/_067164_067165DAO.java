package com.systex.jbranch.fubon.commons.cbs.dao;

import com.systex.jbranch.fubon.commons.cbs.vo._067164_067165.CBS067164InputVO;
import com.systex.jbranch.fubon.commons.cbs.vo.basic.CBSUtilOutputVO;
import com.systex.jbranch.fubon.commons.tx.stuff.Worker;
import com.systex.jbranch.fubon.commons.tx.traffic.Cbs;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * 電文 067164 發送至 CBS 回傳 067165
 */
@Repository
public class _067164_067165DAO {
    /**
     * 查詢
     * @param id     客戶 ID
     * @param idType 客戶 ID TYPE
     * @return
     */
    public List<CBSUtilOutputVO> search(String id, String idType) throws Exception {
        List<CBSUtilOutputVO> list = new ArrayList();
        Worker.call()
                .assign(Worker.CBS, "067164")
                .setRequest(Cbs.createRequestVO()
                        .setPickUpId("067165")
                        .setModule("_067164_067165DAO.search")
                        .setTxHeadVO(Cbs.createTxHeadVO())
                        .setCbs067164InputVO(cbs067164(id, idType)))
                .work()
                .getResponse(list);
        return list;
    }

    /** 電文 InputVO **/
    private CBS067164InputVO cbs067164(String id, String idType) {
        CBS067164InputVO inputVO = new CBS067164InputVO();
        inputVO.setIDNo(id);
        inputVO.setIDType(idType);
        return inputVO;
    }
}
