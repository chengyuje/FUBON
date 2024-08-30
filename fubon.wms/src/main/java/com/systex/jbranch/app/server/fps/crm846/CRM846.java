package com.systex.jbranch.app.server.fps.crm846;

import static com.systex.jbranch.fubon.commons.esb.cons.EsbCrmCons.CCM002_CUST_DATA;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbCrmCons.CCM7818_CUST_DATA;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbCrmCons.CE6220R_CUST_DATA;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.birt.report.model.api.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.esb.EsbFmpJRunConfiguer;
import com.systex.jbranch.fubon.commons.esb.EsbUtil;
import com.systex.jbranch.fubon.commons.esb.vo.ESBUtilInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ESBUtilOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.TxHeadVO;
import com.systex.jbranch.fubon.commons.esb.vo.ccm002.CCM002InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ccm002.CCM002OutputDetailsVO;
import com.systex.jbranch.fubon.commons.esb.vo.ccm002.CCM002OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ccm7818.CCM7818InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ccm7818.CCM7818OutputDetailsVO;
import com.systex.jbranch.fubon.commons.esb.vo.ccm7818.CCM7818OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ce6200r.CE6220RInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ce6200r.CE6220ROutputVO;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.util.IPrimitiveMap;


/**
 * @author Stella, Walalala
 * @date 2016/06/27, 2016/12/07
 * 
 */

@Component("crm846")
@Scope("request")
public class CRM846 extends EsbUtil {

	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(CRM846.class);
	/* const */
    private String ESB_TYPE = EsbFmpJRunConfiguer.ESB_TYPE;
    private String thisClaz = this.getClass().getSimpleName() + ".";
	
	//信用卡帳務資料
	public void initial (Object body, IPrimitiveMap header) throws Exception {
		CRM846InputVO inputVO = (CRM846InputVO) body;
		CRM846OutputVO return_VO = new CRM846OutputVO();
		
		String custID = inputVO.getCust_id();
		
        String htxtid = CCM7818_CUST_DATA;

        //init util
        ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, htxtid);
        esbUtilInputVO.setModule(thisClaz+new Object(){}.getClass().getEnclosingMethod().getName());

        //head
        TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
        txHead.setDefaultTxHead();
        esbUtilInputVO.setTxHeadVO(txHead);

        //body
        CCM7818InputVO ccm7818InputVO = new CCM7818InputVO();
        ccm7818InputVO.setFUNCTION("C2");
        ccm7818InputVO.setCUSID(custID);        //客戶ID
        esbUtilInputVO.setCcm7818InputVO(ccm7818InputVO);

        //發送電文
        List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

        CCM7818OutputVO ccm7818OutputVO = new CCM7818OutputVO();
        List<CCM7818OutputDetailsVO> results = new ArrayList<>();
        
        for(ESBUtilOutputVO esbUtilOutputVO : vos) {
        	ccm7818OutputVO = esbUtilOutputVO.getCcm7818OutputVO();
            results = ccm7818OutputVO.getDetails();
        }

        return_VO.setResultList(results);
		
		this.sendRtnObject(return_VO);
	}

	//信用卡卡片清單
	public void inquire (Object body, IPrimitiveMap header) throws Exception {
		CRM846InputVO inputVO = (CRM846InputVO) body;
		CRM846OutputVO return_VO = new CRM846OutputVO();
		
		String custID = inputVO.getCust_id();
		
        String htxtid = CE6220R_CUST_DATA;

        //init util
        ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, htxtid);
        esbUtilInputVO.setModule(thisClaz+new Object(){}.getClass().getEnclosingMethod().getName());

        //head
        TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
        txHead.setDefaultTxHead();
        esbUtilInputVO.setTxHeadVO(txHead);

        //body
        CE6220RInputVO ce6220rInputVO = new CE6220RInputVO();
        ce6220rInputVO.setTYPE("1");     
        ce6220rInputVO.setPIN(custID); //客戶ID
        esbUtilInputVO.setCe6220rInputVO(ce6220rInputVO);

        //發送電文
        List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

        CE6220ROutputVO ce6220rOutputVO = new CE6220ROutputVO();
        List<CE6220ROutputVO> results = new ArrayList<>();
        
        for(ESBUtilOutputVO esbUtilOutputVO : vos) {
        	ce6220rOutputVO = esbUtilOutputVO.getCe6220rOutputVO();
        	if(StringUtils.isNotBlank(ce6220rOutputVO.getCARD_NO())) {
        		ce6220rOutputVO.setISSUE_DAY(ChangeDateFormat(ce6220rOutputVO.getISSUE_DAY()));
        		ce6220rOutputVO.setLAST_ACCICENT_DAY(ChangeDateFormat(ce6220rOutputVO.getLAST_ACCICENT_DAY()));
        		ce6220rOutputVO.setCARD_TYPE(ce6220rOutputVO.getCARD_TYPE().trim());
        		
                results.add(ce6220rOutputVO);
        	}

        }

        return_VO.setResultList(results);
        
        String htxtid2 = CCM002_CUST_DATA;

        //init util
        ESBUtilInputVO esbUtilInputVO2 = getTxInstance(ESB_TYPE, htxtid2);
        esbUtilInputVO2.setModule(thisClaz+new Object(){}.getClass().getEnclosingMethod().getName());

        //head
        TxHeadVO txHead2 = esbUtilInputVO2.getTxHeadVO();
        txHead2.setDefaultTxHead();
        esbUtilInputVO2.setTxHeadVO(txHead2);

        //body
        CCM002InputVO ccm002InputVO = new CCM002InputVO();
        ccm002InputVO.setFUNCTION("C3");
        ccm002InputVO.setCUSID(custID);        //客戶ID
        esbUtilInputVO2.setCcm002InputVO(ccm002InputVO);

        //發送電文
        List<ESBUtilOutputVO> vos2 = send(esbUtilInputVO2);

        CCM002OutputVO ccm002OutputVO = new CCM002OutputVO();
        List<CCM002OutputDetailsVO> results2 = new ArrayList<>();
        
        for(ESBUtilOutputVO esbUtilOutputVO : vos2) {
        	ccm002OutputVO = esbUtilOutputVO.getCcm002OutputVO();
        	// 2017/6/21
        	if(ccm002OutputVO.getDetails() != null){
        		results2.addAll(ccm002OutputVO.getDetails());
        	}
        	
        }

        return_VO.setResultList2(results2);
        
        
        //1976取CARD_TYPE對應mapping
        dam = getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_SQL);

		StringBuffer sql = new StringBuffer()
		.append("select CARD_TYPE as DATA, CARD_NAME as LABEL from TBCRM_CREDITCARD_NAME ");

		condition.setQueryString(sql.toString());
		return_VO.setResultList3(dam.exeQuery(condition));
		
		this.sendRtnObject(return_VO);
	}
	
	//民國的yyyMMdd轉成西元yyyyMMdd
	private String ChangeDateFormat(String date) {
		if(StringUtil.isBlank(date)) {
			return "00000000";
		}
		if("0000000".equals(date)) {
			return "00000000";
		}
		String newDate = "";
		int yyyyInt = 1911 + Integer.parseInt(date.substring(0,3));
		String yyyy = String.valueOf(yyyyInt);
		newDate = yyyy + date.substring(3,7);
		return newDate;
		
	}
}
