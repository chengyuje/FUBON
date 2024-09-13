package com.systex.jbranch.app.server.fps.crm821;

import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.CMKTYPE_MK03;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.CUST_ASSET_NF_DBU;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.CUST_ASSET_NF_OBU;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.ETF_ASSETS;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ibm.icu.util.Calendar;
import com.systex.jbranch.fubon.commons.esb.EsbFmpJRunConfiguer;
import com.systex.jbranch.fubon.commons.esb.EsbUtil;
import com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons;
import com.systex.jbranch.fubon.commons.esb.vo.ESBUtilInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ESBUtilOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.TxHeadVO;
import com.systex.jbranch.fubon.commons.esb.vo.afbrn9.AFBRN9InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfbrn9.CustAssetFundVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfbrn9.NFBRN9InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfbrn9.NFBRN9OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfbrn9.NFBRN9OutputVODetailsVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfvipa.NFVIPAInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfvipa.NFVIPAOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfvipa.NFVIPAOutputVODetails;
import com.systex.jbranch.fubon.commons.esb.vo.vn084n.VN084NInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.vn084n.VN084NOutputDetailVO;
import com.systex.jbranch.fubon.commons.esb.vo.vn084n1.VN084N1InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.vn084n1.VN084N1OutputDetailVO;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * crm821
 * 
 * @author moron, Walalala
 * @date 2016/06/14, 2016/12/13
 * @spec null
 */
@Component("crm821")
@Scope("request")
public class CRM821 extends EsbUtil {
	
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(CRM821.class);
	private String ESB_TYPE = EsbFmpJRunConfiguer.ESB_TYPE;
	private String thisClaz = this.getClass().getSimpleName() + ".";
	
	public void inquire(Object body, IPrimitiveMap header) throws Exception {
		CRM821InputVO inputVO = (CRM821InputVO) body;
		CRM821OutputVO return_VO = new CRM821OutputVO();
		
		dam = this.getDataAccessManager();
		QueryConditionIF querycondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT TRUST_COM FROM TBPRD_FUND WHERE PRD_ID = :prod_id ");

		querycondition.setObject("prod_id", inputVO.getProd_id());		

		querycondition.setQueryString(sql.toString());		
		List list = dam.exeQuery(querycondition);
		return_VO.setResultList(list);
		
		this.sendRtnObject(return_VO);
	}

	public void inquire_divid(Object body, IPrimitiveMap header) throws Exception {
		CRM821InputVO inputVO = (CRM821InputVO) body;
		CRM821OutputVO return_VO = new CRM821OutputVO();
		
		dam = this.getDataAccessManager();
		QueryConditionIF querycondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		//20180828改抓TBCRM_AST_INV_FUND_DIVID，MANTIS:5557
		sql.append("	SELECT	TO_CHAR(TXN_DATE,'YYYY-MM-DD')	TXN_DATE	,	CASE	WHEN	TR141		=	'' OR nvl(TR141,0)=0	THEN	'配息'	ELSE	'配權'	END	TYPE	,	");
		sql.append("						CERT_NBR	,	");
		sql.append("						CASE WHEN TR141		=	'' OR nvl(TR141,0)=0 THEN null	ELSE TXN_DIVID_ORGD	END	AS TXN_DIVID_ORGD	,	");
		sql.append("						CASE WHEN TR141		=	'' OR nvl(TR141,0)=0 THEN null	ELSE TR141	END	AS TR141	,	");
		sql.append("						A.FUND_CODE||' '||B.FUND_CNAME	AS	FUND_CNAME	,	B.CURRENCY_STD_ID	AS	CURRENCY	,	TR106	");
		sql.append("	FROM	TBCRM_AST_INV_FUND_DIVID	A	LEFT	JOIN	TBPRD_FUND	B	ON	A.FUND_CODE	=	B.PRD_ID	");
		sql.append("	WHERE	1=1	");
		sql.append("AND A.CUST_ID = :cust_id ");
		querycondition.setObject("cust_id", inputVO.getCust_id());
		
		if (inputVO.getsDate() != null){
			sql.append("and TRUNC(A.TXN_DATE) >= :sDate ");  //交易日
			querycondition.setObject("sDate", new Timestamp(inputVO.getsDate().getTime()));
		}
		if (inputVO.geteDate() != null){
			sql.append("and TRUNC(A.TXN_DATE) <= :eDate ");  //交易日
			querycondition.setObject("eDate", new Timestamp(inputVO.geteDate().getTime()));
		}
		sql.append("ORDER BY A.TXN_DATE	");
		
		querycondition.setQueryString(sql.toString());		
		List list = dam.exeQuery(querycondition);
		return_VO.setResultList(list);
		
		this.sendRtnObject(return_VO);
	}
	
	public void inquire_charge(Object body, IPrimitiveMap header) throws Exception {
		CRM821InputVO inputVO = (CRM821InputVO) body;
		CRM821OutputVO return_VO = new CRM821OutputVO();
		
		dam = this.getDataAccessManager();
		QueryConditionIF querycondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT TR101, CURRENCY, SUM(MONEY) AS MONEY FROM ( ");
		sql.append("SELECT A.TR101, ");
		sql.append("CASE ");
		sql.append("WHEN A.TR147 = 'Y' THEN B.CURRENCY_STD_ID ");
		sql.append("ELSE 'TWD' END AS CURRENCY,  ");
		sql.append("CASE ");
		sql.append("WHEN A.TR147 = 'Y' THEN A.TR140 *  ");
		sql.append("(SELECT SEL_RATE FROM TBPMS_IQ053 WHERE MTN_DATE =  ");
		sql.append("(SELECT MAX(MTN_DATE) FROM TBPMS_IQ053 ) AND B.CURRENCY_STD_ID = CUR_COD ) ");
		sql.append("ELSE A.TR112 END AS MONEY ");
		sql.append("FROM TBPMS_TR1FIL A LEFT JOIN TBPRD_FUND B ON A.TR104 = B.PRD_ID ");
		sql.append("WHERE 1=1 ");
		sql.append("AND A.TR103 = '10' ");
		sql.append("AND A.TR106 = '2' ");
		sql.append("AND A.TR108 = :cust_id ");
		sql.append("AND A.TR101 >= TRUNC( SYSDATE, 'MM' ) AND A.TR101 <= LAST_DAY( SYSDATE ) ");
		sql.append(") GROUP BY TR101 , CURRENCY ORDER BY CURRENCY , TR101 ");
		
		querycondition.setObject("cust_id", inputVO.getCust_id());

		querycondition.setQueryString(sql.toString());		
		List list = dam.exeQuery(querycondition);
		return_VO.setResultList(list);
		
		this.sendRtnObject(return_VO);
	}
	
	//基金型態查詢
	public void inquire_fund(Object body, IPrimitiveMap header) throws Exception {
		CRM821InputVO inputVO = (CRM821InputVO) body;
		CRM821OutputVO return_VO = new CRM821OutputVO();
		
		dam = this.getDataAccessManager();
		QueryConditionIF querycondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append(" SELECT FUS20, VIGILANT FROM TBPRD_FUNDINFO WHERE PRD_ID = :prd_id ");
		
		querycondition.setObject("prd_id", inputVO.getProd_id());
		querycondition.setQueryString(sql.toString());		
		List list = dam.exeQuery(querycondition);
		return_VO.setResultList(list);
		
		this.sendRtnObject(return_VO);
	}
	
	//最新外幣值查詢
	public void inquire_Currency(Object body, IPrimitiveMap header) throws Exception {
		CRM821InputVO inputVO = (CRM821InputVO) body;
		CRM821OutputVO return_VO = new CRM821OutputVO();
		
		dam = this.getDataAccessManager();
		QueryConditionIF querycondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT * FROM TBPMS_IQ053 A ");
		sql.append(" WHERE MTN_DATE = ");
		sql.append(" (SELECT MAX(B.MTN_DATE) FROM TBPMS_IQ053 B WHERE A.CUR_COD = B.CUR_COD) ");
		
		querycondition.setQueryString(sql.toString());		
		List list = dam.exeQuery(querycondition);
		return_VO.setResultList(list);
		
		this.sendRtnObject(return_VO);
	}
	
	public void getNFVIPA(Object body, IPrimitiveMap header) throws Exception {
		CRM821InputVO inputVO = (CRM821InputVO) body;
		CRM821OutputVO return_VO = new CRM821OutputVO();
		String cust_id = inputVO.getCust_id();
		String isOBU = inputVO.getIsOBU();
		
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
				
		List<CustAssetFundVO> D1List = sendNFVIPA("D1", cust_id, isOBU);
		List<CustAssetFundVO> D2List = sendNFVIPA("D2", cust_id, isOBU);
		List<CustAssetFundVO> AssetFunList = new ArrayList();
		getCustAssetNFData(cust_id, AssetFunList, "NF");
		if(StringUtils.equals("Y", isOBU)){
			getCustAssetNFData(cust_id, AssetFunList, "AF");
		}
		
		
		if(D2List.size() > 0){
			for(CustAssetFundVO vo : D2List){
				D1List.add(vo);
			}
		}
		if(AssetFunList.size() > 0){
			for(CustAssetFundVO vo : AssetFunList){
				D1List.add(vo);
			}
		}
		
		return_VO.setResultList(D1List);
		
		this.sendRtnObject(return_VO);
		
	}
	
	/**
	 * #1913
	 * 取得基金類總資產
	 * 電文 => VN084N1(OBU) VN084N(DBU) 
	 */
	public void getFundDeposit(Object body, IPrimitiveMap header) throws Exception {
		CRM821InputVO inputVO = (CRM821InputVO) body;
		CRM821OutputVO outputVO = new CRM821OutputVO();
		String cust_id = inputVO.getCust_id();
		String isOBU = inputVO.getIsOBU();
		
		String constant = null;
		
		if (StringUtils.equals("Y", isOBU)) {
			constant = EsbSotCons.VN084N1;
		} else {
			constant = EsbSotCons.VN084N;
		}
		
		// init util
		ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, constant);
		esbUtilInputVO.setModule(thisClaz + new Object() {
		}.getClass().getEnclosingMethod().getName());

		// head
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
		esbUtilInputVO.setTxHeadVO(txHead);
		txHead.setDefaultTxHead();

		if (StringUtils.equals("Y", isOBU)) {
			VN084N1InputVO txBodyVO = new VN084N1InputVO();
			esbUtilInputVO.setVn084n1InputVO(txBodyVO);
			txBodyVO.setCustId(cust_id);
		} else {
			VN084NInputVO txBodyVO = new VN084NInputVO();
			esbUtilInputVO.setVn084nInputVO(txBodyVO);
			txBodyVO.setCustId(cust_id);
		}

		// 發送電文
		List<ESBUtilOutputVO> esbUtilOutputVO = send(esbUtilInputVO);
		ESBUtilOutputVO esbOutputVO = esbUtilOutputVO.get(0);
		String fundAmount = "0";
		if (StringUtils.equals("Y", isOBU)) {
			List<VN084N1OutputDetailVO> details = esbOutputVO.getVn084n1OutputVO().getDetails();
			if (CollectionUtils.isNotEmpty(details)) {
				fundAmount = details.get(0).getAmount();
			}
		} else {
			List<VN084NOutputDetailVO> details = esbOutputVO.getVn084nOutputVO().getDetails();
			if (CollectionUtils.isNotEmpty(details)) {
				fundAmount = details.get(0).getAmount();
			}
		}
		
		outputVO.setFundAmount(new BigDecimal(fundAmount));
		this.sendRtnObject(outputVO);
	}
	
	
	//日期去時分秒
	private static Date getZeroTimeDate(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		date = calendar.getTime();
		return date;
	}
	
	public void getRedeem(Object body, IPrimitiveMap header) throws Exception {
		CRM821InputVO inputVO = (CRM821InputVO) body;
		CRM821OutputVO returnVO = new CRM821OutputVO();
		String cust_id = inputVO.getCust_id();
		String isOBU = inputVO.getIsOBU();
		
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		
		
		getRedeemData(cust_id, resultList, "NF");
		if(StringUtils.equals("Y", isOBU)){
			getRedeemData(cust_id, resultList, "AF");
		}

		
        returnVO.setResultList(resultList);
		this.sendRtnObject(returnVO);
		
	}
	
	private void getRedeemData(String cust_id, List<Map<String, Object>> resultList, String mark) throws Exception {
		Boolean isAF = StringUtils.equals("AF", mark);
		ESBUtilInputVO esbUtilInputVO;
		
		if (isAF) {
        	esbUtilInputVO = getTxInstance(ESB_TYPE, CUST_ASSET_NF_OBU);
        } else {
        	esbUtilInputVO = getTxInstance(ESB_TYPE, CUST_ASSET_NF_DBU);
        }        
        esbUtilInputVO.setModule(thisClaz+new Object(){}.getClass().getEnclosingMethod().getName());

        //head
        TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
        txHead.setDefaultTxHead();
        txHead.setHFMTID("0001");
        esbUtilInputVO.setTxHeadVO(txHead);

        //body
		if (isAF) {
			AFBRN9InputVO txBodyVO = new AFBRN9InputVO();
			esbUtilInputVO.setAfbrn9InputVO(txBodyVO);
	        txBodyVO.setCustId(cust_id);
		} else {
			NFBRN9InputVO txBodyVO = new NFBRN9InputVO();
	        esbUtilInputVO.setNfbrn9InputVO(txBodyVO);
	        txBodyVO.setCustId(cust_id);
		}
		
        //發送電文
        List<ESBUtilOutputVO> vos = send(esbUtilInputVO);
        
        for(ESBUtilOutputVO esbUtilOutputVO : vos){
        	TxHeadVO headVO = esbUtilOutputVO.getTxHeadVO();

            String hfmtid = StringUtils.isBlank(headVO.getHFMTID()) ? "" : headVO.getHFMTID().trim();
            /**
             * Header.‧HFMTID =1003：贖回在途
             */
            if("1003".equals(hfmtid)) {
            	NFBRN9OutputVO outputVO;
            	if (isAF) {
            		outputVO = esbUtilOutputVO.getAfbrn9OutputVO();
             	} else {
             		outputVO = esbUtilOutputVO.getNfbrn9OutputVO();
             	}
                
            	for (NFBRN9OutputVODetailsVO devo : outputVO.getDetails()) {
            		Map<String, Object> map = new HashMap<>();
            		Date effDate = null;
            		if (!"0000000".equals(devo.getEffDate())) {
            			effDate = new EsbUtil().toAdYearMMdd(devo.getEffDate());
            		}
            		map.put("EffDate", effDate);
            		map.put("EviNum", devo.getEviNum());
            		map.put("FundNO", devo.getFundNO());
            		map.put("FundName", devo.getFundName());
            		map.put("CurFund", devo.getCurFund());
            		map.put("CurUntNum", decimalPoint(devo.getCurUntNum(), 4));
            		
            		BigDecimal redeemNetValue = "0000000000".equals(devo.getRedeemNetValue()) ? null : decimalPoint(devo.getRedeemNetValue(), 4);
            		map.put("RedeemNetValue", redeemNetValue);
            		
            		BigDecimal redeemOrgAmt = "000000000000000".equals(devo.getRedeemOrgAmt()) ? null : decimalPoint(devo.getRedeemOrgAmt(), 2);
            		map.put("RedeemOrgAmt", redeemOrgAmt);
            		
            		Date postingDate = null;
            		if (!"00000000".equals(devo.getPostingDate())) {
            			// 『本行預計入帳日』若已過期則排除
            			postingDate = new EsbUtil().toAdYearMMdd(devo.getPostingDate());
            			Date today = this.getZeroTimeDate(new Date());
            			if (postingDate.before(today))
            				continue;
            		}
            		map.put("PostingDate", postingDate);
            		
            		resultList.add(map);
            	}
            }
        }
		
	}

    /**
     * 檢查可用庫存(憑證,判斷單位數 > 0 )
     * @param devo
     * @return
     */
	private boolean checkCustAssetFundVO(NFBRN9OutputVODetailsVO devo){
		
		//2017-01-18 by Jacky 判斷單位數 > 0 才回傳前端 CurUntNum庫存單位數不能為0
		if (decimalPoint(devo.getCurUntNum(), 4).compareTo(BigDecimal.ZERO) == 1) {
          /*瑞國 事件變更 可用憑證
			if ("1110000097".equals(StringUtils.trim(devo.getEviNum()))
					|| "1110000098".equals(StringUtils.trim(devo.getEviNum()))) {
				logger.error("test SOT150 checkCustAssetFundVO ");
				return true;
			}
			if (devo.getFundNO().matches("5232|5108|5116|5119|5124")) { // TODO
				logger.error("test SOT150 checkCustAssetFundVO 5232|5108|5116|5119|5124");
				return true;
			}
		   */	
			return true;
		}
    	return false;
    }
	
	private List<CustAssetFundVO> sendNFVIPA(String fun, String cust_id, String isOBU) throws Exception {
//		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		List<CustAssetFundVO> total = new ArrayList<CustAssetFundVO>();
		
		try{
			//init util
			ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, ETF_ASSETS);
			esbUtilInputVO.setModule(thisClaz+new Object(){}.getClass().getEnclosingMethod().getName());

			//head
			TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
			esbUtilInputVO.setTxHeadVO(txHead);
			txHead.setDefaultTxHead();
			
			//body
			NFVIPAInputVO txBodyVO = new NFVIPAInputVO();
			esbUtilInputVO.setNfvipaInputVO(txBodyVO);
			txBodyVO.setFUNCTION(fun);
			txBodyVO.setCUSID(cust_id);
			txBodyVO.setUNIT("Y".equals(isOBU) ? "O" : "D");

			//發送電文
			List<ESBUtilOutputVO> esbUtilOutputVO = send(esbUtilInputVO);
			
			for(ESBUtilOutputVO vo : esbUtilOutputVO){
				NFVIPAOutputVO nfvipaOutputVO = vo.getNfvipaOutputVO();
				List<NFVIPAOutputVODetails> details = nfvipaOutputVO.getDetails();
				details = (CollectionUtils.isEmpty(details)) ? new ArrayList<NFVIPAOutputVODetails>() : details;
				
				for(NFVIPAOutputVODetails detail : details){
					CustAssetFundVO retVO = new CustAssetFundVO();
					
					retVO.setFundName(detail.getARR18());
					retVO.setCurCode("TWD");
					retVO.setCurAmt(decimalPoint(detail.getARR10(), 0));
					retVO.setCurBal(decimalPoint(detail.getARR11(), 2));
					retVO.setSignDigit(detail.getARR12());
					retVO.setProfitAndLoss(decimalPoint(detail.getARR17(), 4));
					retVO.setReturn(decimalPoint(detail.getARR13(), 4));
					retVO.setFundType(detail.getARR01());
					retVO.setFundNO(detail.getARR04());
					retVO.setAssetType("0006");
					retVO.setNetValueDate(detail.getARR09());
					retVO.setStrdate(detail.getARR06());
					retVO.setCurUntNum(decimalPoint(detail.getARR07(), 4));
					
					total.add(retVO);
				}
			}
		}catch(Exception e){
			logger.debug("發送電文失敗：客戶ID = "+ cust_id);
			logger.debug("ESB error:NFVIPA="+StringUtil.getStackTraceAsString(e));
		}
		
		return total;
	}
    /**
     * 2021.09.13 DBU身分只打NF電文 OBU身分則打NF+AF電文  
     * @param cust_id
     * @param total
     * @param mark 請輸入NF或AF來指定發送哪道電文
     * @return
     * @throws Exception
     * @author SamTu
     */
	public void getCustAssetNFData(String cust_id, List<CustAssetFundVO> total, String mark) throws Exception {
        Boolean isAF = StringUtils.equals("AF", mark);
        //init util
        ESBUtilInputVO esbUtilInputVO;
        if (isAF) {
        	esbUtilInputVO = getTxInstance(ESB_TYPE, CUST_ASSET_NF_OBU);
        } else {
        	esbUtilInputVO = getTxInstance(ESB_TYPE, CUST_ASSET_NF_DBU);
        }  
        esbUtilInputVO.setModule(thisClaz+new Object(){}.getClass().getEnclosingMethod().getName());

        //head
        TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
        txHead.setDefaultTxHead();
        txHead.setHFMTID("0001");
        esbUtilInputVO.setTxHeadVO(txHead);
        
        //body
      	if (isAF) {
      		AFBRN9InputVO txBodyVO = new AFBRN9InputVO();
      		esbUtilInputVO.setAfbrn9InputVO(txBodyVO);
      	       txBodyVO.setCustId(cust_id);
      	} else {
      		NFBRN9InputVO txBodyVO = new NFBRN9InputVO();
      	       esbUtilInputVO.setNfbrn9InputVO(txBodyVO);
      	       txBodyVO.setCustId(cust_id);
      	}
		
		
        //發送電文
        List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

        //處理資料
		dealAF_NF_Data(vos,total,mark);
	}
	
	private void dealAF_NF_Data(List<ESBUtilOutputVO> vos, List<CustAssetFundVO> total, String mark) {
		Boolean isAF = StringUtils.equals("AF", mark);
        for(ESBUtilOutputVO esbUtilOutputVO : vos){
            TxHeadVO headVO = esbUtilOutputVO.getTxHeadVO();

            String hfmtid = StringUtils.isBlank(headVO.getHFMTID()) ? "" : headVO.getHFMTID().trim();
            /**
             * RN9的下行電文會有5個Format的格式
             * Header.‧HFMTID =0001：單筆
             * Header.‧HFMTID =0002：定期定額
             * Header.‧HFMTID =0003：定期不定額
             * Header.‧HFMTID =0004：定存轉基金
             * Header.‧HFMTID =0005：基金套餐
             */
            if("0001".equals(hfmtid)) {
            	NFBRN9OutputVO outputVO;
            	if (isAF) {
            		outputVO = esbUtilOutputVO.getAfbrn9OutputVO();
            	} else {
            		outputVO = esbUtilOutputVO.getNfbrn9OutputVO();
            	}

                for(NFBRN9OutputVODetailsVO devo : outputVO.getDetails()) {
                	//2017-01-18 by Jacky 判斷單位數 > 0 才回傳前端
                	//動態鎖利單位數為0也需顯示
                	if(checkCustAssetFundVO(devo) || StringUtils.isNotBlank(ObjectUtils.toString(devo.getDynamic()))) {
                		CustAssetFundVO retVO = new CustAssetFundVO();
                        retVO.setAssetType(hfmtid); //信託種類
                        retVO.setSPRefId(devo.getSPRefId());
                        retVO.setAcctId16(devo.getAcctId16());
                        retVO.setOccur(devo.getOccur());
                        retVO.setAcctId02(devo.getAcctId02());
                        retVO.setEviNum(devo.getEviNum());
                        retVO.setFundNO(devo.getFundNO());
                        retVO.setFundName(devo.getFundName());
                        retVO.setCurFund(devo.getCurFund());
                        retVO.setCurCode(devo.getCurCode());
                        retVO.setCurAmt(decimalPoint(devo.getCurAmt(), 2));
                        retVO.setCurAmtNT(decimalPoint(devo.getCurAmtNT(), 2));
                        retVO.setCurBal(decimalPoint(devo.getCurBal(), 2));
                        retVO.setCurBalNT(decimalPoint(devo.getCurBalNT(), 2));
                        retVO.setProfitAndLoss(decimalPoint(devo.getProfitAndLoss(), 2));
                        retVO.setIncrease(decimalPoint(devo.getIncrease(), 2));
                        retVO.setSignDigit(devo.getSignDigit());
                        retVO.setReturn(decimalPoint(devo.getReturn(), 2));
                        retVO.setRewRateDigit(devo.getRewRateDigit());
                        retVO.setAccAllocateRewRate(decimalPoint(devo.getAccAllocateRewRate(), 2));
                        retVO.setCurUntNum(decimalPoint(devo.getCurUntNum(), 4));
                        retVO.setReferenceExchangeRate(decimalPoint(devo.getReferenceExchangeRate(), 4));
                        retVO.setNetValueDate(devo.getNetValueDate());
                        if(isNumeric(devo.getNetValue()))retVO.setNetValue(decimalPoint(devo.getNetValue(), 4));
                        retVO.setStoplossSign(devo.getStoplossSign());
                        if(isNumeric(devo.getNetValue()))retVO.setStoploss(decimalPoint(devo.getStoploss(), 2));
                        retVO.setSatisfiedSign(devo.getSatisfiedSign());
                        retVO.setSatisfied(decimalPoint(devo.getSatisfied(), 2));
                        retVO.setStrdate(devo.getStrdate());
                        retVO.setFundType(devo.getFundType());
                        retVO.setApproveFlag(devo.getApproveFlag());
                        retVO.setProjectCode(devo.getProjectCode());
                        retVO.setGroupCode(devo.getGroupCode());
                        retVO.setPayAcctId(devo.getPayAcctId());
                        retVO.setAccAllocateRew(decimalPoint(devo.getAccAllocateRew(), 2));
                        retVO.setIsPledged(StringUtils.isEmpty(devo.getCmkType()) ? "N" : StringUtils.equals(devo.getCmkType().trim(), CMKTYPE_MK03) ? "Y" : "N");	//質借圈存註記
                        
						if (retVO.getAcctId02() != null)
							retVO.setAcctId02(StringUtils.trim(retVO.getAcctId02()));
						if (retVO.getPayAcctId() != null)
							retVO.setPayAcctId(StringUtils.trim(retVO.getPayAcctId()));
						if (retVO.getPayAccountNo() != null)
							retVO.setPayAccountNo(StringUtils.trim(retVO.getPayAccountNo()));

						retVO.setDynamic(devo.getDynamic());
						retVO.setComboReturnDate(devo.getComboReturnDate());
						retVO.setComboReturnSign(devo.getComboReturnSign());
						retVO.setComboReturn(decimalPoint(devo.getComboReturn(), 2));
						retVO.setSatelliteBuyDate1(devo.getSatelliteBuyDate1());
						retVO.setSatelliteBuyDate2(devo.getSatelliteBuyDate2());
						retVO.setSatelliteBuyDate3(devo.getSatelliteBuyDate3());
						retVO.setSatelliteBuyDate4(devo.getSatelliteBuyDate4());
						retVO.setSatelliteBuyDate5(devo.getSatelliteBuyDate5());
						retVO.setSatelliteBuyDate6(devo.getSatelliteBuyDate6());
						retVO.setBenefitReturnRate1(decimalPoint(devo.getBenefitReturnRate1(), 2));
						retVO.setBenefitReturnRate2(decimalPoint(devo.getBenefitReturnRate2(), 2));
						retVO.setBenefitReturnRate3(decimalPoint(devo.getBenefitReturnRate3(), 2));
						retVO.setTRANSFERAmt(decimalPoint(devo.getTRANSFERAmt(), 0));
						retVO.setEviNumType(devo.getEviNumType());
						
                        total.add(retVO);
                	}
                }
            }
            else if("0002".equals(hfmtid)) {   //定期定額
            	NFBRN9OutputVO outputVO;
            	if (isAF) {
            		outputVO = esbUtilOutputVO.getAfbrn9OutputVO();
            	} else {
            		outputVO = esbUtilOutputVO.getNfbrn9OutputVO();
            	}
                
                for(NFBRN9OutputVODetailsVO devo : outputVO.getDetails()) {
                	//2017-01-18 by Jacky 判斷單位數 > 0 才回傳前端
//                	if(checkCustAssetFundVO(devo)){ 
                	if(true){	//測試用 TODO
                		CustAssetFundVO retVO = new CustAssetFundVO();
                        retVO.setAssetType(hfmtid);
                        retVO.setSPRefId(devo.getSPRefId());
                        retVO.setOccur(devo.getOccur());
                        retVO.setAcctId02(devo.getAcctId02());
                        retVO.setEviNum(devo.getEviNum());
                        retVO.setFundNO(devo.getFundNO());
                        retVO.setFundName(devo.getFundName());
                        retVO.setCurFund(devo.getCurFund());
                        retVO.setCurCode(devo.getCurCode());
                        retVO.setCurAmt(decimalPoint(devo.getCurAmt(), 2));
                        retVO.setCurAmtNT(decimalPoint(devo.getCurAmtNT(), 2));
                        retVO.setCurBal(decimalPoint(devo.getCurBal(), 2));
                        retVO.setCurBalNT(decimalPoint(devo.getCurBalNT(), 2));
                        retVO.setProfitAndLoss(decimalPoint(devo.getProfitAndLoss(), 2));
                        retVO.setIncrease(decimalPoint(devo.getIncrease(), 2));
                        retVO.setSignDigit(devo.getSignDigit());
                        retVO.setReturn(decimalPoint(devo.getReturn(), 2));
                        retVO.setRewRateDigit(devo.getRewRateDigit());
                        retVO.setAccAllocateRewRate(decimalPoint(devo.getAccAllocateRewRate(), 2));
                        retVO.setCurUntNum(decimalPoint(devo.getCurUntNum(), 4));
                        retVO.setReferenceExchangeRate(decimalPoint(devo.getReferenceExchangeRate(), 4));
                        retVO.setNetValueDate(devo.getNetValueDate());
                        retVO.setNetValue(decimalPoint(devo.getNetValue(), 4));
                        retVO.setTransferAmt(decimalPoint(devo.getTransferAmt(), 0));
                        retVO.setTransferDate01(devo.getTransferDate01());
                        retVO.setTransferDate02(devo.getTransferDate02());
                        retVO.setTransferDate03(devo.getTransferDate03());
                        retVO.setTransferDate04(devo.getTransferDate04());
                        retVO.setTransferDate05(devo.getTransferDate05());
                        retVO.setTransferDate06(devo.getTransferDate06());
                        retVO.setTransferCount(devo.getTransferCount());
                        retVO.setPayCount(devo.getPayCount());
                        retVO.setStatus(devo.getStatus());
                        retVO.setStoplossSign(devo.getStoplossSign());
                        if(isNumeric(devo.getStoploss()))retVO.setStoploss(decimalPoint(devo.getStoploss(), 2));
                        retVO.setSatisfiedSign(devo.getSatisfiedSign());
                        retVO.setSatisfied(decimalPoint(devo.getSatisfied(), 2));
                        retVO.setStrdate(devo.getStrdate());
                        retVO.setFundType(devo.getFundType());
                        retVO.setApproveFlag(devo.getApproveFlag());
                        retVO.setProjectCode(devo.getProjectCode());
                        retVO.setGroupCode(devo.getGroupCode());
                        retVO.setPayAcctId(devo.getPayAcctId());
                        retVO.setPayAccountNo(devo.getPayAccountNo());
                        retVO.setTxType(devo.getTxType());
                        retVO.setFrgnPurchaseFlag(devo.getFrgnPurchaseFlag());
                        retVO.setSame(devo.getSame());
                        retVO.setLongDiscount(decimalPoint(devo.getLongDiscount().trim(), 2));
                        retVO.setAccAllocateRew(decimalPoint(devo.getAccAllocateRew(), 2));
                        retVO.setIsPledged(StringUtils.isEmpty(devo.getCmkType()) ? "N" : StringUtils.equals(devo.getCmkType().trim(), CMKTYPE_MK03) ? "Y" : "N");	//質借圈存註記
                        if (retVO.getAcctId02() != null)
							retVO.setAcctId02(StringUtils.trim(retVO.getAcctId02()));
						if (retVO.getPayAcctId() != null)
							retVO.setPayAcctId(StringUtils.trim(retVO.getPayAcctId()));
						if (retVO.getPayAccountNo() != null)
							retVO.setPayAccountNo(StringUtils.trim(retVO.getPayAccountNo()));
                        total.add(retVO);
                	}
                }
            }
            else if("0003".equals(hfmtid)) {//定期不定額
            	NFBRN9OutputVO outputVO;
            	if (isAF) {
            		outputVO = esbUtilOutputVO.getAfbrn9OutputVO();
            	} else {
            		outputVO = esbUtilOutputVO.getNfbrn9OutputVO();
            	}
                
                for(NFBRN9OutputVODetailsVO devo : outputVO.getDetails()) {
                	//2017-01-18 by Jacky 判斷單位數 > 0 才回傳前端
//                	if(checkCustAssetFundVO(devo)){
                	if(true){	//測試用 TODO
                		CustAssetFundVO retVO = new CustAssetFundVO();
                        retVO.setAssetType(hfmtid);
                        retVO.setSPRefId(devo.getSPRefId());
                        retVO.setOccur(devo.getOccur());
                        retVO.setAcctId02(devo.getAcctId02());
                        retVO.setEviNum(devo.getEviNum());
                        retVO.setFundNO(devo.getFundNO());
                        retVO.setFundName(devo.getFundName());
                        retVO.setCurFund(devo.getCurFund());
                        retVO.setCurCode(devo.getCurCode());
                        retVO.setCurAmt(decimalPoint(devo.getCurAmt(), 2));
                        retVO.setCurAmtNT(decimalPoint(devo.getCurAmtNT(), 2));
                        retVO.setCurBal(decimalPoint(devo.getCurBal(), 2));
                        retVO.setCurBalNT(decimalPoint(devo.getCurBalNT(), 2));
                        retVO.setProfitAndLoss(decimalPoint(devo.getProfitAndLoss(), 2));
                        retVO.setIncrease(decimalPoint(devo.getIncrease(), 2));
                        retVO.setSignDigit(devo.getSignDigit());
                        retVO.setReturn(decimalPoint(devo.getReturn(), 2));
                        retVO.setRewRateDigit(devo.getRewRateDigit());
                        retVO.setAccAllocateRewRate(decimalPoint(devo.getAccAllocateRewRate(), 2));
                        retVO.setCurUntNum(decimalPoint(devo.getCurUntNum(), 4));
                        retVO.setReferenceExchangeRate(decimalPoint(devo.getReferenceExchangeRate(), 4));
                        retVO.setNetValueDate(devo.getNetValueDate());
                        retVO.setNetValue(decimalPoint(devo.getNetValue(), 4));
                        retVO.setTransferAmt_H(decimalPoint(devo.getTransferAmt_H(), 0));
                        retVO.setTransferAmt_M(decimalPoint(devo.getTransferAmt_M(), 0));
                        retVO.setTransferAmt_L(decimalPoint(devo.getTransferAmt_L(), 0));
                        retVO.setTransferDate01(devo.getTransferDate01());
                        retVO.setTransferDate02(devo.getTransferDate02());
                        retVO.setTransferDate03(devo.getTransferDate03());
                        retVO.setTransferDate04(devo.getTransferDate04());
                        retVO.setTransferDate05(devo.getTransferDate05());
                        retVO.setTransferDate06(devo.getTransferDate06());
                        retVO.setTransferCount(devo.getTransferCount());
                        retVO.setPayCount(devo.getPayCount());
                        retVO.setStatus(devo.getStatus());
                        retVO.setStoplossSign(devo.getStoplossSign());
                        if(isNumeric(devo.getStoploss())) retVO.setStoploss(decimalPoint(devo.getStoploss(), 2));
                        retVO.setSatisfiedSign(devo.getSatisfiedSign());
                        retVO.setSatisfied(decimalPoint(devo.getSatisfied(), 2));
                        retVO.setStrdate(devo.getStrdate());
                        retVO.setFundType(devo.getFundType());
                        retVO.setApproveFlag(devo.getApproveFlag());
                        retVO.setProjectCode(devo.getProjectCode());
                        retVO.setGroupCode(devo.getGroupCode());
                        retVO.setPayAcctId(devo.getPayAcctId());
                        retVO.setPayAccountNo(devo.getPayAccountNo());
                        retVO.setTxType(devo.getTxType());
                        retVO.setFrgnPurchaseFlag(devo.getFrgnPurchaseFlag());
                        retVO.setSame(devo.getSame());
                        retVO.setAccAllocateRew(decimalPoint(devo.getAccAllocateRew(), 2));
                        retVO.setIsPledged(StringUtils.isEmpty(devo.getCmkType()) ? "N" : StringUtils.equals(devo.getCmkType().trim(), CMKTYPE_MK03) ? "Y" : "N");	//質借圈存註記
                        if (retVO.getAcctId02() != null)
							retVO.setAcctId02(StringUtils.trim(retVO.getAcctId02()));
						if (retVO.getPayAcctId() != null)
							retVO.setPayAcctId(StringUtils.trim(retVO.getPayAcctId()));
						if (retVO.getPayAccountNo() != null)
							retVO.setPayAccountNo(StringUtils.trim(retVO.getPayAccountNo()));
                        total.add(retVO);
                	}                    
                }
            }
            else if("0004".equals(hfmtid)) {
            	NFBRN9OutputVO outputVO;
            	if (isAF) {
            		outputVO = esbUtilOutputVO.getAfbrn9OutputVO();
            	} else {
            		outputVO = esbUtilOutputVO.getNfbrn9OutputVO();
            	}
                
                for(NFBRN9OutputVODetailsVO devo : outputVO.getDetails()) {
                	//2017-01-18 by Jacky 判斷單位數 > 0 才回傳前端
//                	if(checkCustAssetFundVO(devo)){
                	if(true){	//測試用 TODO
                		CustAssetFundVO retVO = new CustAssetFundVO();
                        retVO.setAssetType(hfmtid);
                        retVO.setSPRefId(devo.getSPRefId());
                        retVO.setOccur(devo.getOccur());
                        retVO.setAcctId02(devo.getAcctId02());
                        retVO.setEviNum(devo.getEviNum());
                        retVO.setFundNO(devo.getFundNO());
                        retVO.setFundName(devo.getFundName());
                        retVO.setCurFund(devo.getCurFund());
                        retVO.setCurCode(devo.getCurCode());
                        retVO.setCurAmt(decimalPoint(devo.getCurAmt(), 2));
                        retVO.setCurAmtNT(decimalPoint(devo.getCurAmtNT(), 2));
                        retVO.setCurBal(decimalPoint(devo.getCurBal(), 2));
                        retVO.setCurBalNT(decimalPoint(devo.getCurBalNT(), 2));
                        retVO.setProfitAndLoss(decimalPoint(devo.getProfitAndLoss(), 2));
                        retVO.setIncrease(decimalPoint(devo.getIncrease(), 2));
                        retVO.setSignDigit(devo.getSignDigit());
                        retVO.setReturn(decimalPoint(devo.getReturn(), 2));
                        retVO.setRewRateDigit(devo.getRewRateDigit());
                        retVO.setAccAllocateRewRate(decimalPoint(devo.getAccAllocateRewRate(), 2));
                        retVO.setCurUntNum(decimalPoint(devo.getCurUntNum(), 4));
                        retVO.setReferenceExchangeRate(decimalPoint(devo.getReferenceExchangeRate(), 4));
                        retVO.setNetValueDate(devo.getNetValueDate());
                        retVO.setNetValue(decimalPoint(devo.getNetValue(), 4));
                        retVO.setTransferAmt(decimalPoint(devo.getTransferAmt(), 0));
                        retVO.setTransferDate01(devo.getTransferDate01());
                        retVO.setTransferDate02(devo.getTransferDate02());
                        retVO.setTransferDate03(devo.getTransferDate03());
                        retVO.setTransferDate04(devo.getTransferDate04());
                        retVO.setTransferDate05(devo.getTransferDate05());
                        retVO.setTransferDate06(devo.getTransferDate06());
                        retVO.setTransferCount(devo.getTransferCount());
                        retVO.setPayCount(devo.getPayCount());
                        retVO.setStatus(devo.getStatus());
                        retVO.setStoplossSign(devo.getStoplossSign());
                        retVO.setStoploss(decimalPoint(devo.getStoploss(), 2));
                        retVO.setSatisfiedSign(devo.getSatisfiedSign());
                        retVO.setSatisfied(decimalPoint(devo.getSatisfied(), 2));
                        retVO.setStrdate(devo.getStrdate());
                        retVO.setFundType(devo.getFundType());
                        retVO.setApproveFlag(devo.getApproveFlag());
                        retVO.setGroupCode(devo.getGroupCode());
                        retVO.setPayAccountNo(devo.getPayAccountNo());
                        retVO.setAcctId(devo.getAcctId());
                        retVO.setTimeDepositPrjCd(devo.getTimeDepositPrjCd());
                        retVO.setTotal_Cnt(devo.getTotal_Cnt());
                        retVO.setPay_Cnt(devo.getPay_Cnt());
                        retVO.setEnd_Flg(devo.getEnd_Flg());
                        retVO.setSame(devo.getSame());
                        retVO.setAccAllocateRew(decimalPoint(devo.getAccAllocateRew(), 2));
                        retVO.setIsPledged(StringUtils.isEmpty(devo.getCmkType()) ? "N" : StringUtils.equals(devo.getCmkType().trim(), CMKTYPE_MK03) ? "Y" : "N");	//質借圈存註記
                        if (retVO.getAcctId02() != null)
							retVO.setAcctId02(StringUtils.trim(retVO.getAcctId02()));
						if (retVO.getPayAcctId() != null)
							retVO.setPayAcctId(StringUtils.trim(retVO.getPayAcctId()));
						if (retVO.getPayAccountNo() != null)
							retVO.setPayAccountNo(StringUtils.trim(retVO.getPayAccountNo()));
                        total.add(retVO);
                	}                    
                }
            }
            else if("0005".equals(hfmtid)) {
            	NFBRN9OutputVO outputVO;
            	if (isAF) {
            		outputVO = esbUtilOutputVO.getAfbrn9OutputVO();
            	} else {
            		outputVO = esbUtilOutputVO.getNfbrn9OutputVO();
            	}
                
                for (NFBRN9OutputVODetailsVO devo : outputVO.getDetails()) {
                	//2017-01-18 by Jacky 判斷單位數 > 0 才回傳前端
                	if(true){	//測試用 TODO
//                	if(checkCustAssetFundVO(devo)){
                		CustAssetFundVO retVO = new CustAssetFundVO();
                        retVO.setAssetType(hfmtid);
                        retVO.setSPRefId(devo.getSPRefId());
                        retVO.setAcctId16(devo.getAcctId16());
                        retVO.setOccur(devo.getOccur());
                        retVO.setAcctId02(devo.getAcctId02());
                        retVO.setEviNum(devo.getEviNum());
                        retVO.setFundNO(devo.getFundNO());
                        retVO.setFundName(devo.getFundName());
                        retVO.setCurFund(devo.getCurFund());
                        retVO.setCurCode(devo.getCurCode());
                        retVO.setCurAmt(decimalPoint(devo.getCurAmt(), 2));
                        retVO.setCurAmtNT(decimalPoint(devo.getCurAmtNT(), 2));
                        retVO.setCurBal(decimalPoint(devo.getCurBal(), 2));
                        retVO.setCurBalNT(decimalPoint(devo.getCurBalNT(), 2));
                        retVO.setProfitAndLoss(decimalPoint(devo.getProfitAndLoss(), 2));
                        retVO.setIncrease(decimalPoint(devo.getIncrease(), 2));
                        retVO.setSignDigit(devo.getSignDigit());
                        retVO.setReturn(decimalPoint(devo.getReturn(), 2));
                        retVO.setRewRateDigit(devo.getRewRateDigit());
                        retVO.setAccAllocateRewRate(decimalPoint(devo.getAccAllocateRewRate(), 2));
                        retVO.setCurUntNum(decimalPoint(devo.getCurUntNum(), 4));
                        retVO.setReferenceExchangeRate(decimalPoint(devo.getReferenceExchangeRate(), 4));
                        retVO.setNetValueDate(devo.getNetValueDate());
                        retVO.setNetValue(decimalPoint(devo.getNetValue(), 4));
                        retVO.setTransferAmt(decimalPoint(devo.getTransferAmt(), 0));
                        retVO.setTransferDate01(devo.getTransferDate01());
                        retVO.setTransferDate02(devo.getTransferDate02());
                        retVO.setTransferDate03(devo.getTransferDate03());
                        retVO.setTransferDate04(devo.getTransferDate04());
                        retVO.setTransferDate05(devo.getTransferDate05());
                        retVO.setTransferDate06(devo.getTransferDate06());
                        retVO.setTransferCount(devo.getTransferCount());
                        retVO.setPayCount(devo.getPayCount());
                        retVO.setStatus(devo.getStatus());
                        retVO.setStoplossSign(devo.getStoplossSign());
                        retVO.setStoploss(decimalPoint(devo.getStoploss(), 2));
                        retVO.setSatisfiedSign(devo.getSatisfiedSign());
                        retVO.setSatisfied(decimalPoint(devo.getSatisfied(), 2));
                        retVO.setStrdate(devo.getStrdate());
                        retVO.setFundType(devo.getFundType());
                        retVO.setApproveFlag(devo.getApproveFlag());
                        retVO.setProjectCode(devo.getProjectCode());
                        retVO.setGroupCode(devo.getGroupCode());
                        retVO.setPayAcctId(devo.getPayAcctId());
                        retVO.setPayAccountNo(devo.getPayAccountNo());
                        retVO.setTxType(devo.getTxType());
                        retVO.setSame(devo.getSame());
                        retVO.setFundPackageNo(devo.getFundPackageNo());
                        retVO.setFundPackage(devo.getFundPackage());
                        retVO.setEnd_Flg(devo.getEnd_Flg());
                        retVO.setAccAllocateRew(decimalPoint(devo.getAccAllocateRew(), 2));
                        retVO.setIsPledged(StringUtils.isEmpty(devo.getCmkType()) ? "N" : StringUtils.equals(devo.getCmkType().trim(), CMKTYPE_MK03) ? "Y" : "N");	//質借圈存註記
                        if (retVO.getAcctId02() != null)
							retVO.setAcctId02(StringUtils.trim(retVO.getAcctId02()));
						if (retVO.getPayAcctId() != null)
							retVO.setPayAcctId(StringUtils.trim(retVO.getPayAcctId()));
						if (retVO.getPayAccountNo() != null)
							retVO.setPayAccountNo(StringUtils.trim(retVO.getPayAccountNo()));
                        total.add(retVO);
                	}                    
                }
            }
        }
        
		Collections.sort(total, new Comparator<CustAssetFundVO>() {
		    public int compare( CustAssetFundVO o1,  CustAssetFundVO o2) {
		    	if(StringUtils.isEmpty(o1.getEviNum())){
		    		 return -1;
		    	}
		    	if(StringUtils.isEmpty(o2.getEviNum())){
		    		return 1;
		    	}
		    	return (o1.getEviNum()).compareTo(o2.getEviNum());
		     }
		});
		
	}
	
}