package com.systex.jbranch.fubon.commons.cbs.dao;

import com.systex.jbranch.fubon.commons.cbs.vo._060425_060433.CBS060425InputVO;
import com.systex.jbranch.fubon.commons.cbs.vo.basic.CBSUtilOutputVO;
import com.systex.jbranch.fubon.commons.tx.stuff.Worker;
import com.systex.jbranch.fubon.commons.tx.traffic.Cbs;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * 電文 060425 發送至 CBS 回傳 060433
 */
@Repository
public class _060425_060433DAO {
    /**
     * 查詢
     * @param id     客戶 ID
     * @param idType 客戶 ID TYPE
     * @return
     */
    public List<CBSUtilOutputVO> search(String id, String idType) throws Exception {
        List<CBSUtilOutputVO> list = new ArrayList();
        Worker.call()
                .assign(Worker.CBS, "060425")
                .setRequest(Cbs.createRequestVO()
                        .setPickUpId("060433")
                        .setModule("_060425_060433DAO.search")
                        .setTxHeadVO(Cbs.createTxHeadVO())
                        .setCbs060425InputVO(cbs060425(id, idType)))
                .work()
                .getResponse(list);
        return list;
    }

    /**
     * 查詢 
     * @param id     客戶 ID
     * @param idType 客戶 ID TYPE
     * @return
     */
    public List<CBSUtilOutputVO> search(String id, String idType, String enquiryType) throws Exception {
        List<CBSUtilOutputVO> list = new ArrayList();
        Worker.call()
                .assign(Worker.CBS, "060425")
                .setRequest(Cbs.createRequestVO()
                        .setPickUpId("060433")
                        .setModule("_060425_060433DAO.search")
                        .setTxHeadVO(Cbs.createTxHeadVO())
                        .setCbs060425InputVO(cbs060425(id, idType, enquiryType)))
                .work()
                .getResponse(list);
        return list;
    }
    
    /** 電文 InputVO **/
    private CBS060425InputVO cbs060425(String id, String idType) {
        CBS060425InputVO inputVO = new CBS060425InputVO();
        inputVO.setIdno(id);
        inputVO.setIdtype(idType);
        return inputVO;
    }
    
    /** 電文 InputVO **/
    private CBS060425InputVO cbs060425(String id, String idType, String enquiryType) {
        CBS060425InputVO inputVO = new CBS060425InputVO();
        inputVO.setIdno(id);
        inputVO.setIdtype(idType);
        inputVO.setEnquiryType(enquiryType);
        return inputVO;
    }
}
