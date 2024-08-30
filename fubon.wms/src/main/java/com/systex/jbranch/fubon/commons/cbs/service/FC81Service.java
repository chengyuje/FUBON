package com.systex.jbranch.fubon.commons.cbs.service;

import com.systex.jbranch.fubon.commons.cbs.CBSService;
import com.systex.jbranch.fubon.commons.cbs.dao._067050_067115DAO;
import com.systex.jbranch.fubon.commons.cbs.vo._067050_067115.CBS067115OutputVO;
import com.systex.jbranch.fubon.commons.cbs.vo.basic.CBSUtilOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.fc81.FC81InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.fc81.FC81OutputVO;
import com.systex.jbranch.platform.common.errHandle.APException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.*;

/**
 * 此服務將代替原本的 FC81 ESB 電文
 * （Note: 原本的 FC81 ESB 電文改走 CBS 路線）
 */
@Service
public class FC81Service {
    @Autowired
    private _067050_067115DAO _067050_067115dao;
    @Autowired
    private CBSService cbsService;
    /**
     * 查詢客戶的 Q 值與投資經驗
     */
    public FC81OutputVO search(String custId) throws Exception {
        List<CBSUtilOutputVO> data = _067050_067115dao.search(custId, cbsService.getCBSIDCode(custId));

        if (isEmpty(data)) return null;
        return _067050_067115Transfer(data.get(0).getCbs067115OutputVO());
    }

    /**
     * 規格書轉換 _067050_067115 to FC81
     **/
    private FC81OutputVO _067050_067115Transfer(CBS067115OutputVO cbs) {
        FC81OutputVO esb = new FC81OutputVO();

        esb.setTX_RSN(substring(checkReligiousRef1(cbs.getReligiousRef1()), 66, 72));
        esb.setTX_DESC("Y".equals(cbs.getAnntationFlag15()) ? "Q6" : "Q0");

        return esb;
    }

    /**
     * 判斷 ReligiousRef1 欄位，並回傳處理結果
     **/
    private String checkReligiousRef1(String ref) {
        return length(ref) >= 72 ? ref : rightPad(ref, 72);
    }

    /**
     * 更新客戶的 Q 值與投資經驗
     */
    public void update(FC81InputVO esbVO) throws Exception {
        List<CBSUtilOutputVO> data = _067050_067115dao.search(esbVO.getCUST_NO(), cbsService.getCBSIDCode(esbVO.getCUST_NO()));
        if (isEmpty(data))
            throw new APException("客戶資料有誤，無法更新！");

        CBS067115OutputVO cbsVO = data.get(0).getCbs067115OutputVO();
        processReligiousRef1(esbVO, cbsVO);
        cbsVO.setAnntationFlag15(isBlank(esbVO.getTX_RSN())? "": "Y");
        _067050_067115dao.update067115(cbsVO);
    }

    /** ReligiousRef1 欄位邏輯處理 **/
    private void processReligiousRef1(FC81InputVO esbVO, CBS067115OutputVO cbsVO) {
        String ref = cbsVO.getReligiousRef1();
        String mixed = esbVO.getMTN_BRH() + esbVO.getMTN_EMP() + esbVO.getTX_DATE() + esbVO.getTX_TIME() + esbVO.getTX_RSN();
        if (length(ref) < 42)
            cbsVO.setReligiousRef1(rightPad(ref, 42) + mixed);
        else {
            StringBuilder sb = new StringBuilder(cbsVO.getReligiousRef1());
            sb.replace(42, 46, esbVO.getMTN_BRH());
            sb.replace(46, 52, esbVO.getMTN_EMP());
            sb.replace(52, 60, esbVO.getTX_DATE());
            sb.replace(60, 66, esbVO.getTX_TIME());
            sb.replace(66, 72, esbVO.getTX_RSN());
            cbsVO.setReligiousRef1(sb.toString());
        }
    }
}
