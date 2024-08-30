package com.systex.jbranch.app.server.fps.crm683;

import static com.systex.jbranch.fubon.commons.esb.cons.EsbCrmCons.NMVIPA_CUST_DATA;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.eclipse.birt.report.model.api.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.crm827.CustAssetNMVIPAVO;
import com.systex.jbranch.fubon.commons.esb.EsbFmpJRunConfiguer;
import com.systex.jbranch.fubon.commons.esb.EsbUtil;
import com.systex.jbranch.fubon.commons.esb.vo.ESBUtilInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ESBUtilOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.TxHeadVO;
import com.systex.jbranch.fubon.commons.esb.vo.nmvipa.NMVIPAInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nmvipa.NMVIPAOutputDetailsVO;
import com.systex.jbranch.fubon.commons.esb.vo.nmvipa.NMVIPAOutputVO;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.util.IPrimitiveMap;


@Component("crm683")
@Scope("request")
public class CRM683 extends EsbUtil {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(CRM683.class);
	/* const */
    private String ESB_TYPE = EsbFmpJRunConfiguer.ESB_TYPE;
    private String thisClaz = this.getClass().getSimpleName() + ".";
	
	public void inquire(Object body, IPrimitiveMap header) throws Exception {
		CRM683InputVO inputVO = (CRM683InputVO) body;
		CRM683OutputVO return_VO = new CRM683OutputVO();
		
		String custID = inputVO.getCust_id();
        String htxtid = NMVIPA_CUST_DATA;

        //init util
        ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, htxtid);
        esbUtilInputVO.setModule(thisClaz+new Object(){}.getClass().getEnclosingMethod().getName());

        //head
        TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
        txHead.setDefaultTxHead();
        esbUtilInputVO.setTxHeadVO(txHead);

        //body
        NMVIPAInputVO nmvipaInputVO = new NMVIPAInputVO();
        nmvipaInputVO.setFUNCTION("40");
        nmvipaInputVO.setUNIT("D");
        nmvipaInputVO.setCUSID(custID);        //客戶ID
        esbUtilInputVO.setNmvipaInputVO(nmvipaInputVO);

        //發送電文
        List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

        List<CustAssetNMVIPAVO> list = new ArrayList<>();
        
        for(ESBUtilOutputVO esbUtilOutputVO : vos) {
        	NMVIPAOutputVO nmvipaOutputVO = esbUtilOutputVO.getNmvipaOutputVO();

        	List<NMVIPAOutputDetailsVO> details = nmvipaOutputVO.getDetails();
            details = (CollectionUtils.isEmpty(details)) ? new ArrayList<NMVIPAOutputDetailsVO>() : details;
            
            //取得庫存資料
            for (NMVIPAOutputDetailsVO detail : details) {
                //符合資料放入庫存CustAssetNMVIPAVO
            	CustAssetNMVIPAVO asset = new CustAssetNMVIPAVO();
            	//有價證券只取DA資料
            	if (StringUtil.isEqual(nmvipaOutputVO.getFUNCTION(), "DA")) {            		
            		asset.setDA_ARR00(detail.getARR00());
            		asset.setDA_ARR01(detail.getARR01());
            		asset.setDA_ARR02(detail.getARR02());
            		asset.setDA_ARR03(detail.getARR03());
            		asset.setDA_ARR04(detail.getARR04());
            		//日期格式
            		if(detail.getARR05().length() == 8){
            			java.util.Date DA_ARR05 = new EsbUtil().toAdYearMMdd(detail.getARR05());
            			asset.setDA_ARR05(new java.text.SimpleDateFormat("yyyy/MM/dd").format(DA_ARR05));
            		}else{
            			asset.setDA_ARR05(detail.getARR05());
            		}
            		if(detail.getARR06().length() == 8){
            			java.util.Date DA_ARR06 = new EsbUtil().toAdYearMMdd(detail.getARR06());
            			asset.setDA_ARR06(new java.text.SimpleDateFormat("yyyy/MM/dd").format(DA_ARR06));
            		}else{
            			asset.setDA_ARR06(detail.getARR06());
            		}
            		
            		asset.setDA_ARR07(detail.getARR07());
            		asset.setDA_ARR08(new EsbUtil().decimalPoint(detail.getARR08(), 2).toString());
            		asset.setDA_ARR09(new EsbUtil().decimalPoint(detail.getARR09(), 2).toString());
            		asset.setDA_ARR10(detail.getARR10());
            		asset.setDA_ARR11(detail.getARR11());
            		asset.setDA_ARR12(new EsbUtil().decimalPoint(detail.getARR12(), 4).toString());
            		asset.setDA_ARR13(detail.getARR13());
            		asset.setDA_ARR14(detail.getARR14());
            		asset.setDA_V0015(detail.getV0015());
            		asset.setDA_V0016(detail.getV0016());
            		asset.setDA_V0017(detail.getV0017());
            		asset.setDA_V0018(detail.getV0018());
            		asset.setDA_V0019(detail.getV0019());
            		
            		String v0020Str = "";
            		int v0021int = Integer.valueOf(detail.getV0021());
            		if(detail.getV0020().equals("1")) {
            			v0020Str="每月第"+ String.valueOf(v0021int) + "日";
            		} else if (detail.getV0020().equals("2")) {
            			v0020Str = String.valueOf(v0021int) + "個月扣帳一次";
            		} else if(detail.getV0020().equals("3") ) {
            			v0020Str = "到期扣帳";
            		}
            		asset.setDA_V0020(v0020Str);
            		asset.setDA_V0021(detail.getV0021());
            		
            		asset.setDA_V0022(detail.getV0022());
            		asset.setDA_V0023(detail.getV0023());
            		asset.setDA_V0024(detail.getV0024());
            		asset.setDA_V0025(detail.getV0025());
            		
            		list.add(asset);
            	}
            }
        }  
        return_VO.setResultList(list);
        
        this.sendRtnObject(return_VO);

	}	
	
	//上市櫃股票
	public void inquireListed(Object body, IPrimitiveMap header) throws DAOException,JBranchException {
		CRM683InputVO inputVO = (CRM683InputVO)body;
		CRM683OutputVO outputVO = new CRM683OutputVO();
		
		inputVO.setIsListed(Boolean.TRUE);
		outputVO.setResultList(inquireStock(inputVO));
		
		this.sendRtnObject(outputVO);
	}
	
	//未上市櫃股票
	public void inquireNotListed(Object body, IPrimitiveMap header) throws DAOException,JBranchException {
		CRM683InputVO inputVO = (CRM683InputVO)body;
		CRM683OutputVO outputVO = new CRM683OutputVO();
		
		inputVO.setIsListed(Boolean.FALSE);
		outputVO.setResultList(inquireStock(inputVO));
		
		this.sendRtnObject(outputVO);
	}
	
	//上市櫃股票/未上市櫃股票
	public List inquireStock(CRM683InputVO inputVO) throws DAOException,JBranchException{
		CRM683OutputVO outputVO = new CRM683OutputVO();
		
		dam = this.getDataAccessManager();  
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append(" SELECT DISTINCT B.T03413 AS STOCK_ID");
		sql.append("        ,D.LSE005 AS STOCK_NAME ");
		sql.append(" 		,SUBSTR(E.TMF19A, 3, 3) AS CURRENCY ");
		sql.append(" 		,B.T03405 AS INV_AMT ");
		sql.append(" 		,B.T03422 AS STOCK_NO1 ");
		sql.append(" 		,B.T03423 AS STOCK_NO2 ");
		sql.append(" 		,(NVL(B.T03419, 0) + NVL(B.T03420, 0) + NVL(B.T03421, 0)) AS STOCK_TOTAL_NO ");
		sql.append(" 		,B.T03449 AS REF_VAL_DATE ");
		sql.append(" 		,B.T03448 AS REF_VAL ");
		sql.append(" 		,B.T03412 AS REF_MARKET_VAL ");
		sql.append(" 		,(NVL(B.T03412, 0) - NVL(B.T03411, 0)) AS PROF_LOSS ");
		sql.append(" 		,CASE WHEN NVL(B.T03412, 0) = 0 THEN 0.00 ELSE ROUND((NVL(B.T03412, 0) - NVL(B.T03411, 0)) * 100 / B.T03412, 2) END AS RETURN_RATE ");
		sql.append(" FROM TBCRM_NMS020_DAY A ");
		sql.append(" INNER JOIN TBCRM_NMS034 B ON B.T03404 = A.TMB01 ");
		sql.append(" LEFT OUTER JOIN TBCRM_NMS060_DAY C ON C.TMF01 = A.TMB01 AND C.TMF24 = '03' ");
		sql.append(" LEFT OUTER JOIN TBCRM_NMECM D ON D.LSE001 = B.T03413 ");
		sql.append(" LEFT OUTER JOIN TBCRM_NMS060A E ON E.TMF01A = B.T03404 ");
		sql.append(" WHERE A.TMB02 = :cust_id ");
		sql.append("   AND B.T03404 = :contract_no ");
		sql.append("   AND B.T03401 = '0' AND B.T03403 = '1' ");
		sql.append("   AND B.T03402 = (SELECT MAX(T03402) FROM TBCRM_NMS034 WHERE TRUNC(T03402) <= TRUNC(SYSDATE)) ");
		sql.append("   AND A.SNAP_DATE = (SELECT MAX(SNAP_DATE) FROM TBCRM_NMS020_DAY) ");
		if(inputVO.getIsListed()) {
			sql.append("   AND D.LSE011 <> '0' ");	//上市櫃
		} else {
			sql.append("   AND D.LSE011 = '0' ");	//未上市櫃
		}
				
		queryCondition.setObject("cust_id", inputVO.getCust_id());
		queryCondition.setObject("contract_no", inputVO.getContract_no());
		queryCondition.setQueryString(sql.toString());	
		
		List<Map<String, Object>> list1 = dam.exeQuery(queryCondition);
	
		return list1;
	}
	
	
}