package com.systex.jbranch.fubon.commons.esb;

import com.systex.jbranch.app.server.fps.crm621.CRM621InputVO;
import com.systex.jbranch.app.server.fps.crm621.CRM621OutputVO;
import com.systex.jbranch.app.server.fps.crm811.CRM811InputVO;
import com.systex.jbranch.app.server.fps.crm811.CRM811OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ESBUtilInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ESBUtilOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.TxHeadVO;
import com.systex.jbranch.fubon.commons.esb.vo.tp032675.TP032675InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.tp032675.TP032675OutputDetailsVO;
import com.systex.jbranch.fubon.commons.esb.vo.tp032675.TP032675OutputVO;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.util.IPrimitiveMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.systex.jbranch.fubon.commons.esb.cons.EsbCrm390Cons.CRM621_CUST_CONTECT_INFO;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbCrm390Cons.CRM811_CUST_TWD_DATA;

/**
 * Created by SebastianWu on 2016/8/29.
 */
@Component("esbcrm390")
@Scope("request")
public class EsbCrm390 extends EsbUtil{
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private DataAccessManager dam = null;

    /* const */
    private String ESB_TYPE = EsbFmpJRunConfiguer.ESB_TYPE;

    /**
     * CRM621 客戶即時聯絡方式
     *
     * 使用電文: TP032675
     *
     * @param body
     * @param header
     * @return List
     */
    public void getCRM621_CustContectInfo(Object body, IPrimitiveMap header) throws Exception {
        CRM621InputVO inputVO = (CRM621InputVO) body;
        CRM621OutputVO outputVO = new CRM621OutputVO();

        String custID = inputVO.getCust_id();

        // 電文代號
        String htxtid = CRM621_CUST_CONTECT_INFO;

        //init util
        ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, htxtid);
        esbUtilInputVO.setModule(new Object(){}.getClass().getEnclosingMethod().getName());

        //head
        TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
        txHead.setDefaultTxHead();


        //body
        TP032675InputVO tp032675VO = new TP032675InputVO();
        tp032675VO.setFUNCTION("CRRAMS");
        tp032675VO.setCUSID(custID);        //客戶ID
        esbUtilInputVO.setTp032675InputVO(tp032675VO);

        //發送電文
        List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

        List<TP032675OutputVO> results = new ArrayList<>();

        for(ESBUtilOutputVO esbUtilOutputVO : vos) {
            TP032675OutputVO tp032675OutputVO = esbUtilOutputVO.getTp032675OutputVO();
            results.add(tp032675OutputVO);
        }

        outputVO.setResultList(results);
		
		this.sendRtnObject(outputVO);
    }
}