package com.systex.jbranch.fubon.commons.cbs.service;

import com.systex.jbranch.fubon.commons.cbs.CBSService;
import com.systex.jbranch.fubon.commons.cbs.dao._062410_062411DAO;
import com.systex.jbranch.fubon.commons.cbs.vo._062410_062411.CBS062411OutputDetailsVO;
import com.systex.jbranch.fubon.commons.cbs.vo._062410_062411.CBS062411OutputVO;
import com.systex.jbranch.fubon.commons.cbs.vo.basic.CBSUtilOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.wms032275.WMS032275OutputVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.collections.CollectionUtils.isEmpty;

/**
 * 此服務將代替原本的 WMS032275 ESB 電文
 * （Note: 原本的 WMS032275 ESB 電文改走 CBS 路線）
 */
@Service
public class WMS032275Service {
    @Autowired
    private _062410_062411DAO _062410_062411dao;
    @Autowired
    private CBSService cbsService;

    /**
     * 查詢客戶 W-8BEN 和 FATCA 客戶註記檢核
     *
     * @param custId 客戶 ID
     */
    public WMS032275OutputVO search(String custId) throws Exception {
        List<CBSUtilOutputVO> data = _062410_062411dao.search(custId, cbsService.getCBSIDCode(custId));
        if (isEmpty(data)) return null;

        return _062410_062411Transfer(data);
    }

    /**
     * 規格書轉換 _062410_062411 to WMS032275
     */
    private WMS032275OutputVO _062410_062411Transfer(List<CBSUtilOutputVO> data) {
        WMS032275OutputVO esbVO = new WMS032275OutputVO();
        CBS062411OutputVO cbsVO = data.get(0).getCbs062411OutputVO();

        processDtl(esbVO, cbsVO);
        processOutput(esbVO, cbsVO);
        return esbVO;
    }

    /** 存 OutputVO **/
    private void processOutput(WMS032275OutputVO esbVO, CBS062411OutputVO cbsVO) {
        esbVO.setIDX_1(cbsVO.getUSIndex1());
        esbVO.setIDX_2(cbsVO.getUSIndex2());
        esbVO.setIDX_3(cbsVO.getUSIndex3());
        esbVO.setIDX_4(cbsVO.getUSIndex4());
        esbVO.setIDX_5(cbsVO.getUSIndex5());
        esbVO.setIDX_6(cbsVO.getUSIndex6());
        esbVO.setIDX_7(cbsVO.getUSIndex7());
        esbVO.setIDX_8(cbsVO.getUSIndex8());
        esbVO.setIDF_N(cbsVO.getIdentityNo());
        esbVO.setIDF_S(cbsVO.getCurrIdentityDesc());
        esbVO.setIDF_P(cbsVO.getPrevIdentityNo());
        esbVO.setTIN(cbsVO.getLastYrIdentityNo());
        esbVO.setIDF_Y(cbsVO.getLastYrIdentityNo());
        esbVO.setSEH_DATE_E(cbsVO.getSrchDtElectronic());
        esbVO.setSHE_DATE_P(cbsVO.getSrchDtManual());
        esbVO.setBRH_COD(cbsVO.getMaintBranch());
        esbVO.setSEH_DATE_C(cbsVO.getMandIdentificatnDt());
        esbVO.setDetails(new ArrayList());
    }

    /**
     * 對應 SOT701 的值取法直接硬存在 OutputVO
     **/
    private void processDtl(WMS032275OutputVO esbVO, CBS062411OutputVO cbsVO) {
        List<CBS062411OutputDetailsVO> cbsDtls = cbsVO.getDetails();
        esbVO.setDOC_NO1(cbsDtls.get(0).getDocNo());
        esbVO.setEFF_DATE1(cbsDtls.get(0).getEffDate());
        esbVO.setSIG_DATE1(cbsDtls.get(0).getSignDate());

        esbVO.setDOC_NO2(cbsDtls.get(1).getDocNo());
        esbVO.setEFF_DATE2(cbsDtls.get(1).getEffDate());
        esbVO.setSIG_DATE2(cbsDtls.get(1).getSignDate());

        esbVO.setDOC_NO3(cbsDtls.get(2).getDocNo());
        esbVO.setEFF_DATE3(cbsDtls.get(2).getEffDate());
        esbVO.setSIG_DATE3(cbsDtls.get(2).getSignDate());
    }
}
