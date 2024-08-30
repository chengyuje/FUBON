package com.systex.jbranch.fubon.commons.cbs.service;

import com.systex.jbranch.fubon.commons.cbs.CBSService;
import com.systex.jbranch.fubon.commons.cbs.dao._085081_085105DAO;
import com.systex.jbranch.fubon.commons.cbs.vo._085081_085105.CBS085105OutputDetailsVO;
import com.systex.jbranch.fubon.commons.cbs.vo.basic.CBSUtilOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.fp032671.FP032671OutputDetailsVO;
import com.systex.jbranch.fubon.commons.esb.vo.fp032671.FP032671OutputVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang.StringUtils.substring;
import static org.apache.commons.lang.StringUtils.trim;

/**
 * 此服務將代替原本的 FP032671 ESB 電文
 * （Note: 原本的 FP032671 ESB 電文改走 CBS 路線）
 */
@Service
public class FP032671Service {
    @Autowired
    private _085081_085105DAO _085081_085105dao;
    @Autowired
    private CBSService cbsService;

    /**
     * 取得客戶帳戶相關資訊
     */
    public List<FP032671OutputVO> searchCustAcct(String custId) throws Exception {
        List result = new ArrayList();
        for (CBSUtilOutputVO each : _085081_085105dao.search(custId, cbsService.getCBSIDCode(custId))) {
            /** Detail **/
            List<FP032671OutputDetailsVO> esbDtlVO = new ArrayList();
            for (CBS085105OutputDetailsVO cbsDtlVO : each.getCbs085105OutputVO().getDetails())
                esbDtlVO.add(_085081_085105Transfer(cbsDtlVO));

            /** FP032671 Output **/
            FP032671OutputVO output = new FP032671OutputVO();
            output.setDetails(esbDtlVO);
            result.add(output);
        }

        return result;
    }

    /**
     * 規格書轉換  _085081_085105 detail to  FP032671 detail
     **/
    private FP032671OutputDetailsVO _085081_085105Transfer(CBS085105OutputDetailsVO cbs) {
        FP032671OutputDetailsVO esb = new FP032671OutputDetailsVO();
        // 參考規格書轉換
        esb.setBRA(cbsService.checkBraLength(cbs.getWA_X_BRANCH_NO().trim()));
        esb.setACNO_CATG_1(cbs.getWA_X_SYS());
        esb.setACNO_1(cbsService.checkAcctLength(cbs.getWA_X_ACCTNO()));
        esb.setOPN_DATE_1(cbs.getWA_X_DATE_OPEN());
        esb.setCUST_NAME_1(cbs.getWA_X_ACCT_NAME());
        esb.setTYPE(trim(cbs.getWA_X_PRODUCT_IND()));
        esb.setCURR(trim(cbs.getWA_X_CURR()));
        esb.setWA_X_ATYPE(cbs.getWA_X_ATYPE());
        esb.setWA_X_ICAT(cbs.getWA_X_ICAT());
        if ("LON".equals(cbs.getWA_X_SYS()))
            esb.setORG_DESC_1(substring(cbs.getOthers(), 75, 77));
        return esb;
    }
}
