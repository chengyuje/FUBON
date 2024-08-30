package com.systex.jbranch.app.server.fps.crm813;

import com.systex.jbranch.common.xmlInfo.XMLInfo;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.commons.cbs.CBSService;
import com.systex.jbranch.fubon.commons.cbs.service.EB12020002Service;
import com.systex.jbranch.fubon.commons.esb.vo.eb12020002.EB12020002OutputDetailsVO;
import com.systex.jbranch.fubon.commons.esb.vo.eb12020002.EB12020002OutputVO;
import com.systex.jbranch.platform.util.IPrimitiveMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;

/**
 * 存款分布_台外幣定存
 *
 * @author walalala
 * @date 2016/12/06
 */
@Component("crm813")
@Scope("request")
public class CRM813 extends FubonWmsBizLogic {
    @Autowired
    private CBSService cbsservice;
    @Autowired
    private EB12020002Service eb12020002Service;

    /**
     * 查詢台幣定存資料
     *
     * @param body
     * @param header
     * @throws Exception
     */
    public void inquire(Object body, IPrimitiveMap header) throws Exception {
        List results = new ArrayList();
        for (EB12020002OutputVO vo : eb12020002Service.search(((CRM813InputVO) body).getCust_id(), "1")) {
            if (isEmpty(vo.getDetails())) continue;

            for (EB12020002OutputDetailsVO data : vo.getDetails()) {
                /** 帳號為空不處理 **/
                if (isBlank(data.getACNO())) continue;

                formatNumber(data);
                results.add(data);
            }
        }
        CRM813OutputVO return_VO = new CRM813OutputVO();
        return_VO.setResultList(results);
        this.sendRtnObject(return_VO);
    }

    /**
     * 查詢外幣定存資料
     *
     * @param body
     * @param header
     * @throws Exception
     */
    public void inquire2(Object body, IPrimitiveMap header) throws Exception {
        /** 匯率 **/
        HashMap<String, BigDecimal> ex_map = new XMLInfo().getExchangeRate();

        List results = new ArrayList();
        for (EB12020002OutputVO vo : eb12020002Service.search(((CRM813InputVO) body).getCust_id(), "2")) {
            if (isEmpty(vo.getDetails())) continue;

            for (EB12020002OutputDetailsVO data : vo.getDetails()) {
                /** 帳號為空不處理 **/
                if (isBlank(data.getACNO())) continue;

                formatNumber(data);
                transferTWD(ex_map, data);
                results.add(data);
            }
        }
        CRM813OutputVO return_VO = new CRM813OutputVO();
        return_VO.setResultList2(results);
        this.sendRtnObject(return_VO);
    }

    /**
     * 折合台幣
     **/
    private void transferTWD(HashMap<String, BigDecimal> ex_map, EB12020002OutputDetailsVO data) {
        if ("XXX".equals(data.getCUR())) return;

        BigDecimal curr = ex_map.get(data.getCUR());
        BigDecimal CURRENT_AMT_BAL = new BigDecimal(data.getOPN_DPR_AMT());
        data.setOPN_DPR_AMT_TWD(CURRENT_AMT_BAL.multiply(curr).setScale(0, BigDecimal.ROUND_HALF_UP).toString());
    }

    /**
     * 數值處理
     **/
    private void formatNumber(EB12020002OutputDetailsVO data) {
        if (isNotBlank(data.getOPN_DPR_AMT()))
            data.setOPN_DPR_AMT(new BigDecimal(cbsservice.amountFormat(data.getOPN_DPR_AMT())).toString());

        if (isNotBlank(data.getINT_RATE()))
            data.setINT_RATE(cbsservice.amountFormat(data.getINT_RATE(), 7));

        if (isNotBlank(data.getDUE_DTE()))
            data.setDUE_DTE(cbsservice.changeDateView(data.getDUE_DTE(),"3"));

        if (isNotBlank(data.getBK_VALUE()))
            data.setBK_VALUE(cbsservice.changeDateView(data.getBK_VALUE(),"3"));

        if (!"0".equals(data.getCRLN_UTL()))
            data.setCRLN_UTL(cbsservice.amountFormat(data.getCRLN_UTL()));

        if (!"0".equals(data.getLOAN_BAL()))
            data.setLOAN_BAL(cbsservice.amountFormat(data.getLOAN_BAL()));
    }
}