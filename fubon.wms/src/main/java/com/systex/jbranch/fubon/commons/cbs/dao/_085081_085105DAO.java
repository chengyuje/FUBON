package com.systex.jbranch.fubon.commons.cbs.dao;

import com.systex.jbranch.fubon.commons.cbs.vo._085081_085105.CBS085081InputVO;
import com.systex.jbranch.fubon.commons.cbs.vo.basic.CBSUtilOutputVO;
import com.systex.jbranch.fubon.commons.tx.stuff.Worker;
import com.systex.jbranch.fubon.commons.tx.traffic.Cbs;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * 電文 085081 發送至 CBS 回傳 085105
 * 客戶帳號、戶名資料
 */
@Repository
public class _085081_085105DAO {
    /**
     * @param custId 客戶統一編號
     * @param idType 統一編號種類代碼
     * @return
     */
    public List<CBSUtilOutputVO> search(String custId, String idType) throws Exception {
        List<CBSUtilOutputVO> list = new ArrayList();
        // 使用 Worker（工人） 電文工作流來設置電文相關參數
        Worker.call()
                // 設定（指派）目的地為 CBS數字電文，原有的則為 Worker.ESB，並且要送的資料（貨物）為 085081
                // 這裡等同於 ESB 使用 EsbUtil.getTxInstance 方法初始化
                .assign(Worker.CBS, "085081")
                // 設定（契約單） TxHead、TxBody，setPickUpId 指定下行（回程）需要什麼樣的資料（貨物）
                // 這裡指定下行需要 085105 的電文資料
                .setRequest(Cbs.createRequestVO() // 工廠方法
                        .setPickUpId("085105") // 下行電文代號
                        .setModule("_085081_085105DAO.search") // 等同於 ESB 使用 setModule 方法
                        .setTxHeadVO(Cbs.createTxHeadVO()) // 利用工廠方法設置預設 TxHead
                        .setCbs085081Input(cbs085081(custId, idType))) // 設置 TxBody
                // 發送（叫工人去工作），等同於 EsbUtil.send
                .work()
                // 將回傳電文資料封裝到 List<CBSUtilOutputVO>（看看工人運回來什麼貨物）
                .getResponse(list);
        return list;
    }

    /**
     * 電文 InputVO
     **/
    private CBS085081InputVO cbs085081(String custId, String idType) {
        CBS085081InputVO inputVO = new CBS085081InputVO();
        inputVO.setTib_ID1(idType); // TYPE
        inputVO.setDefaultString1(custId); // ID
        return inputVO;
    }
}
