package com.systex.jbranch.app.server.fps.crm617;

import static com.systex.jbranch.fubon.commons.esb.cons.EsbCrmCons.TP552697_CUST_DATA;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbCrmCons.WMS552697_CUST_DATA;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.crm617.CRM617InputVO;
import com.systex.jbranch.app.server.fps.crm617.CRM617OutputVO;
import com.systex.jbranch.app.server.fps.sot701.FC032675DataVO;
import com.systex.jbranch.fubon.commons.esb.EsbFmpJRunConfiguer;
import com.systex.jbranch.fubon.commons.esb.EsbUtil;
import com.systex.jbranch.fubon.commons.esb.vo.ESBUtilInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ESBUtilOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.TxHeadVO;
import com.systex.jbranch.fubon.commons.esb.vo.fc032675.FC032675OutputDetailsVO;
import com.systex.jbranch.fubon.commons.esb.vo.fc032675.FC032675OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.tp552697.TP552697OutputDetailsVO;
import com.systex.jbranch.fubon.commons.esb.vo.tp552697.TP552697OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.tp552697.TP552697InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.wms552697.WMS552697InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.wms552697.WMS552697OutputDetailsVO;
import com.systex.jbranch.fubon.commons.esb.vo.wms552697.WMS552697OutputVO;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * @author Stella
 * @date 2016/06/22
 * 
 */
@Component("crm617")
@Scope("request")
public class CRM617 extends EsbUtil {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(CRM617.class);
	/* const */
    private String ESB_TYPE = EsbFmpJRunConfiguer.ESB_TYPE;
    private String thisClaz = this.getClass().getSimpleName() + ".";
	
	public void initial(Object body, IPrimitiveMap header) throws JBranchException {
		CRM617InputVO inputVO = (CRM617InputVO) body;
		CRM617OutputVO return_VO = new CRM617OutputVO();
		
		dam = this.getDataAccessManager();
		/* 客戶常用基本資料 */
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT A.CUST_ID, A.CUST_NAME, A.BRA_NBR, A.VIP_DEGREE, A.AO_CODE, B.EMP_NAME "
				+ " FROM TBCRM_CUST_MAST A "
				+ " LEFT JOIN VWORG_BRANCH_EMP_DETAIL_INFO B ON A.AO_CODE = B.AO_CODE "
				+ " where 1=1 ");
		sql.append("and A.CUST_ID = :cust_id ");
		queryCondition.setQueryString(sql.toString());
		queryCondition.setObject("cust_id", inputVO.getCust_id());
		
		List result = dam.exeQuery(queryCondition);
		return_VO.setResultList(result);
		sendRtnObject(return_VO);
	}
	
	
	public void inquire (Object body, IPrimitiveMap header) throws Exception {
		CRM617InputVO inputVO = (CRM617InputVO) body;
		CRM617OutputVO return_VO = new CRM617OutputVO();
		dam = this.getDataAccessManager();
		
		/**Mark :20170105 更改共用電文  BY Stella */
		String custID = inputVO.getCust_id();
		String htxtid = WMS552697_CUST_DATA;

        //init util
        ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, htxtid);
        esbUtilInputVO.setModule(thisClaz+new Object(){}.getClass().getEnclosingMethod().getName());

        //head
        TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
        txHead.setDefaultTxHead();
        txHead.setHTLID("2004011");
        esbUtilInputVO.setTxHeadVO(txHead);

        //body
        WMS552697InputVO wms552697InputVO = new WMS552697InputVO();
       // wms552697InputVO.setFUNCTION("EB");
        wms552697InputVO.setCUSID(custID);        //客戶ID
        esbUtilInputVO.setWms552697InputVO(wms552697InputVO);

        //發送電文
        List<ESBUtilOutputVO> vos = send(esbUtilInputVO);
       
        WMS552697OutputVO wms552697OutputVO = new WMS552697OutputVO();
         List<WMS552697OutputDetailsVO> results = new ArrayList<>();
        for(ESBUtilOutputVO esbUtilOutputVO : vos) {
        	wms552697OutputVO = esbUtilOutputVO.getWms552697OutputVO();
        	results.addAll(wms552697OutputVO.getDetails());
        	
        }
        return_VO.setResultList3(results);  //原本的TP552697是ResultList2
		this.sendRtnObject(return_VO);
		
	}
}
