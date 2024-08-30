package com.systex.jbranch.app.server.fps.sot714;

import com.systex.jbranch.app.server.fps.sot701.CustHighNetWorthDataVO;
import com.systex.jbranch.fubon.commons.cbs.CBSService;
import com.systex.jbranch.fubon.commons.esb.EsbFmpJRunConfiguer;
import com.systex.jbranch.fubon.commons.esb.EsbUtil;
import com.systex.jbranch.fubon.commons.esb.vo.ESBUtilInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ESBUtilOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.TxHeadVO;
import com.systex.jbranch.fubon.commons.esb.vo.cm061435cr.CM061435CRInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.cm061435cr.CM061435CROutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.njbrvh3.NJBRVH3InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.njbrvh3.NJBRVH3OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.njbrvh3.NJBRVH3OutputVODetails;
import com.systex.jbranch.fubon.commons.esb.vo.wmshacr.WMSHACRInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.wmshacr.WMSHACROutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.wmshaia.WMSHAIAVO;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.util.IPrimitiveMap;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.systex.jbranch.fubon.commons.esb.cons.EsbCrmCons.*;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.*;
import static org.apache.commons.lang.StringUtils.*;

/**
 * Created on 2023/4/26.
 * 高資產客戶 & 集中度 & 投組適配 相關電文
 * 
 */
@Component("sot714")
@Scope("request")
public class SOT714 extends EsbUtil {
	@Autowired
	private CBSService cbsservice;
	
	private String thisClaz = this.getClass().getSimpleName() + ".";
	private SOT714InputVO sot714InputVO;
	private SOT714OutputVO sot714OutputVO;

	/* const */
	private String ESB_TYPE = EsbFmpJRunConfiguer.ESB_TYPE;

	 	
	/***
	 * 取得客戶高資產註記資料(High Net Worth Client Data)
 	 * 下單集中度檢核用
 	 * -- 高資產註記到期日
 	 * -- 高資產註記註銷日
 	 * -- 高資產客戶註記
 	 * -- 可提供高資產商品或服務
	 * @param body
	 * @param header
	 * @throws Exception
	 */
 	public void getHNWCData(Object body, IPrimitiveMap header) throws Exception {
 		sot714InputVO = (SOT714InputVO) body;
 		sot714OutputVO = new SOT714OutputVO();
 		sot714OutputVO.setHnwcDataVO(this.getHNWCData(sot714InputVO.getCustID()));
		sendRtnObject(sot714OutputVO);
	}
 	
 	/***
 	 * 取得客戶高資產註記資料(High Net Worth Client Data)
 	 * 下單集中度檢核用
 	 * -- 高資產註記到期日
 	 * -- 高資產註記註銷日
 	 * -- 高資產客戶註記
 	 * -- 可提供高資產商品或服務
 	 * @param custID
 	 * @return
 	 * @throws Exception
 	 */
 	public CustHighNetWorthDataVO getHNWCData(String custID) throws Exception {
 		CustHighNetWorthDataVO hnwcVO = new CustHighNetWorthDataVO();
 		hnwcVO.setValidHnwcYN("N"); //非高資產客戶或已失效
 		Date dueDate = null;
 		Date invalidDate = null;	
 		 		
 		//init util
 		ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, CM061435CR);
 		esbUtilInputVO.setModule(thisClaz + new Object(){}.getClass().getEnclosingMethod().getName());

 		//head
 		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
 		txHead.setDefaultTxHead();
 		txHead.setHTLID("99940119");
		esbUtilInputVO.setTxHeadVO(txHead);

 		//body
		CM061435CRInputVO cm061435crInputVO = new CM061435CRInputVO();
		cm061435crInputVO.setID1(custID);
		cm061435crInputVO.setIDTYPE1(cbsservice.getCBSIDCode(custID));
        esbUtilInputVO.setCm061435crInputVO(cm061435crInputVO);

        //發送電文
        List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

        for (ESBUtilOutputVO esbUtilOutputVO : vos) {
        	CM061435CROutputVO cm061435crOutputVO = esbUtilOutputVO.getCm061435crOutputVO();
        	if (cm061435crOutputVO != null) {
        		String dueDateStr = ObjectUtils.toString(cm061435crOutputVO.getHNWC_DUE_DATE());
    			String invalidDateStr = ObjectUtils.toString(cm061435crOutputVO.getHNWC_INVALID_DATE());
    			
    			//到期日
    			if(isNotBlank(dueDateStr)) {
    				try {
    					dueDate = new SimpleDateFormat("yyyyMMdd").parse(dueDateStr);
    					hnwcVO.setDueDate(dueDate); 
    				} catch(Exception e) {}
    			}
    			//註銷日
    			if(isNotBlank(invalidDateStr)) {
    				try {
    					invalidDate = new SimpleDateFormat("yyyyMMdd").parse(invalidDateStr);
    					hnwcVO.setInvalidDate(invalidDate); 
    				} catch(Exception e) {}
    			}
    			
    			hnwcVO.setValidHnwcYN(ObjectUtils.toString(cm061435crOutputVO.getHNWC_FLG())); //高資產客戶註記
    			hnwcVO.setHnwcService(ObjectUtils.toString(cm061435crOutputVO.getHNWC_SERV())); //可提供高資產商品或服務
    			hnwcVO.setSpFlag(ObjectUtils.toString(cm061435crOutputVO.getSP_FLG())); //是否為高資產特定客戶註記 (1)年齡70歲(含)以上。(2)教育程度為國中(含)以下。(3)有全民健康保險重大傷病證明。
        	}
        }
        
		return hnwcVO;
	}
 	
 	/***
 	 * 取得集中度資訊
 	 * 電文代碼：WMSHACR
 	 * 
 	 * @param body
 	 * @param header
 	 * @throws Exception
 	 */
 	public void getCentRateData(Object body, IPrimitiveMap header) throws Exception {
 		sot714InputVO = (SOT714InputVO) body;
 		sot714OutputVO = new SOT714OutputVO();
 		sot714OutputVO.setWmshacrDataVO(this.getCentRateData(sot714InputVO));
		sendRtnObject(sot714OutputVO);
	}
	 	 	
 	/***
 	 * 取得集中度資訊
 	 * 電文代碼：WMSHACR
 	 * 
 	 * @param custID
 	 * @return
 	 * @throws Exception
 	 */
 	public WMSHACRDataVO getCentRateData(SOT714InputVO inputVO) throws Exception {
 		WMSHACRDataVO wmshacrDataVO = new WMSHACRDataVO();

 		//init util
 		ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, WMSHACR);
 		esbUtilInputVO.setModule(thisClaz + new Object(){}.getClass().getEnclosingMethod().getName());

 		//head
 		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
 		txHead.setDefaultTxHead();
		esbUtilInputVO.setTxHeadVO(txHead);

 		//body
		WMSHACRInputVO wmshacrInputVO = new WMSHACRInputVO();
		wmshacrInputVO.setCUST_ID(inputVO.getCustID());
		wmshacrInputVO.setPROD_TYPE(inputVO.getProdType());
		wmshacrInputVO.setAMT(decimalPadding(inputVO.getBuyAmt(), 16, 0));
        esbUtilInputVO.setWmshacrInputVO(wmshacrInputVO);

        //發送電文
        List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

        for (ESBUtilOutputVO esbUtilOutputVO : vos) {
        	WMSHACROutputVO wmshacrOutputVO = esbUtilOutputVO.getWmshacrOutputVO();
        	if (wmshacrOutputVO != null) {
        		if(StringUtils.isNotBlank(wmshacrOutputVO.getEMSGTXT())) {
        			throw new APException(wmshacrOutputVO.getEMSGID() + ":" + wmshacrOutputVO.getEMSGTXT());
        		}
        		//分母總金額(折台)
        		wmshacrDataVO.setDENO_AMT(decimalPoint(wmshacrOutputVO.getDENO_AMT(), 0));
        		//有不保本註記之SI、SN、DCI
        		wmshacrDataVO.setBASE_PERC_1(decimalPoint(wmshacrOutputVO.getBASE_PERC_1(), 2));
        		wmshacrDataVO.setPERCENTAGE_1(decimalPoint(wmshacrOutputVO.getPERCENTAGE_1(), 2));
        		wmshacrDataVO.setAMT_1(decimalPoint(wmshacrOutputVO.getAMT_1(), 0));
        		//有僅限高資產客戶申購註記之海外債券
        		wmshacrDataVO.setBASE_PERC_2(decimalPoint(wmshacrOutputVO.getBASE_PERC_2(), 2));
        		wmshacrDataVO.setPERCENTAGE_2(decimalPoint(wmshacrOutputVO.getPERCENTAGE_2(), 2));
        		wmshacrDataVO.setAMT_2(decimalPoint(wmshacrOutputVO.getAMT_2(), 0));
        		//未具證投信基金性質境外基金
        		wmshacrDataVO.setBASE_PERC_3(decimalPoint(wmshacrOutputVO.getBASE_PERC_3(), 2));
        		wmshacrDataVO.setPERCENTAGE_3(decimalPoint(wmshacrOutputVO.getPERCENTAGE_3(), 2));
        		wmshacrDataVO.setAMT_3(decimalPoint(wmshacrOutputVO.getAMT_3(), 0));
        		//加總
        		wmshacrDataVO.setBASE_PERC_4(decimalPoint(wmshacrOutputVO.getBASE_PERC_4(), 2)); //限額基準百分比
        		wmshacrDataVO.setPERCENTAGE_4(decimalPoint(wmshacrOutputVO.getPERCENTAGE_4(), 2)); //計算出的百分比
        		wmshacrDataVO.setAMT_4(decimalPoint(wmshacrOutputVO.getAMT_4(), 0)); //所有分子加總金額，不包含當下申購金額(折台)
        		//上限比例基準百分比
        		wmshacrDataVO.setLIMIT_PERC_2(decimalPoint(wmshacrOutputVO.getLIMIT_PERC_2(), 2)); //（有僅限高資產客戶申購註記之海外債券）上限比例基準百分比
        		wmshacrDataVO.setLIMIT_PERC_3(decimalPoint(wmshacrOutputVO.getLIMIT_PERC_3(), 2)); //（未具證投信基金性質境外基金）上限比例基準百分比
        		//可否交易
        		wmshacrDataVO.setVALIDATE_YN(wmshacrOutputVO.getVALIDATE_YN()); //Y:可交易 W:超過通知門檻 N:不可交易
        	}
        }
		
		return wmshacrDataVO;
	}
 	
 	/***
 	 * 業管發查各交易系統，取得客戶已委託高風險投資明細資訊
 	 * @param body
 	 * @param header
 	 * @throws Exception
 	 */
 	public void getCentInvData(Object body, IPrimitiveMap header) throws Exception {
 		sot714InputVO = (SOT714InputVO) body;
 		sot714OutputVO = new SOT714OutputVO();
 		sot714OutputVO.setCentInvDataList(this.getCentInvData(sot714InputVO));
		sendRtnObject(sot714OutputVO);
	}
 	
 	/***
 	 * 業管發查各交易系統，取得客戶已委託高風險投資明細資訊
 	 * @param inputVO
 	 * @return
 	 * @throws Exception
 	 */
 	public List<CentInvDataVO> getCentInvData(SOT714InputVO inputVO) throws Exception {
 		List<CentInvDataVO> CenInvDataList = new ArrayList<CentInvDataVO>();
 		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

 		//init util
 		String esbSno = getESB_NO(inputVO.getProdType());
 		if(StringUtils.isBlank(esbSno)) { //找不到電文代碼，回傳空
 			return CenInvDataList;
 		}
 		
 		ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, esbSno);
 		esbUtilInputVO.setModule(thisClaz + new Object(){}.getClass().getEnclosingMethod().getName());

 		//head
 		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
 		txHead.setDefaultTxHead();
		esbUtilInputVO.setTxHeadVO(txHead);

 		//body
		NJBRVH3InputVO njbrvh3InputVO = new NJBRVH3InputVO();
		njbrvh3InputVO.setCUST_ID(inputVO.getCustID());
        esbUtilInputVO.setNjbrvh3InputVO(njbrvh3InputVO);

        //發送電文
        List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

        for (ESBUtilOutputVO esbUtilOutputVO : vos) {
        	NJBRVH3OutputVO njbrvh3OutputVO = getEsbUtilOuputVOBySNO(esbUtilOutputVO, esbSno);
        	if (njbrvh3OutputVO != null && njbrvh3OutputVO.getDetails() != null) {
        		for(NJBRVH3OutputVODetails details : njbrvh3OutputVO.getDetails()) {
        			CentInvDataVO invData = new CentInvDataVO();
        			if(inputVO.getProdType().matches("3|4|6|7")) {
        				//信託平台海外債券/SN/境外私募基金委託日期為民國年，須改為西元年
        				invData.setBUY_DATE(StringUtils.isNotBlank(details.getBUY_DATE()) ? toAdYearMMdd(details.getBUY_DATE()) : null);
        			} else {
        				invData.setBUY_DATE(StringUtils.isNotBlank(details.getBUY_DATE()) ? sdf.parse(details.getBUY_DATE()) : null);
        			}
        			invData.setSTATUS(details.getSTATUS());
        			invData.setPROD_CAT(details.getPROD_CAT());
        			invData.setTRANS_TYPE(details.getTRANS_TYPE());
        			invData.setPROD_ID(details.getPROD_ID());
        			invData.setPROD_NAME(details.getPROD_NAME());
        			invData.setCURR_ID(details.getCURR_ID());
        			try {
        				invData.setAMT_ORG(decimalPoint(details.getAMT_ORG(), 2));
        			} catch(Exception e) {
        				invData.setAMT_ORG(new BigDecimal(details.getAMT_ORG()));
        			}
        			invData.setAMT_TWD(decimalPoint(details.getAMT_TWD(), 0));
        			//只顯示 4:特金海外債 5: 金市海外債 6: 境外私募基金
        			//移除境內外結構型商品，不顯示
        			if(details.getPROD_CAT().matches("4|5|6")) {
        				CenInvDataList.add(invData);
        			}
        		}
        	}
        }
		
		return CenInvDataList;
	}
 	
 	/***
 	 * 依商品類別取得電文代碼
 	 * @param prodType
 	 * @return
 	 */
 	private String getESB_NO(String prodType) {
 		switch (prodType) {
 		case "1": //SI
 			return "WMSHAD001";
 		case "2": //DCI
 			return ""; 		//目前沒有
// 			return "WMSHAD003";
 		case "3": //特金海外債&SN (DBU)
 			return "NJBRVH3";
 		case "4": //特金海外債&SN (OBU)
 			return "AJBRVH3";
 		case "5": //金市海外債
 			return "WMSHAD005";
 		case "6": //境外私募基金(DBU)
 			return "NFBRND";
 		case "7": //境外私募基金(OBU)
 			return "AFBRND";
 		default:
 			return "";
 		}
 	}
 	
 	/***
 	 * 依電文代碼取得電文回傳VO
 	 * 這幾個電文的上下行皆相同
 	 * @param esbVO
 	 * @param sno
 	 * @return
 	 */
 	private NJBRVH3OutputVO getEsbUtilOuputVOBySNO(ESBUtilOutputVO esbVO, String sno) {
 		switch (sno) {
 		case "WMSHAD001":
 			return esbVO.getWmshad001OutputVO();
 		case "WMSHAD003":
 			return esbVO.getWmshad003OutputVO();
 		case "NJBRVH3":
 			return esbVO.getNjbrvh3OutputVO();
 		case "AJBRVH3":
 			return esbVO.getAjbrvh3OutputVO();
 		case "WMSHAD005":
 			return esbVO.getWmshad005OutputVO();
 		case "NFBRND":
 			return esbVO.getNfbrndOutputVO();
 		case "AFBRND":
 			return esbVO.getAfbrndOutputVO();
 		default:
 			return null;
 		}
 	}
 	
 	/***
 	 * 高資產客戶投組適配承作，取得風險檢核資訊
 	 * 電文代碼：WMSHAIA
 	 * @param body
 	 * @param header
 	 * @throws Exception
 	 */
 	public void getByPassRiskData(Object body, IPrimitiveMap header) throws Exception {
 		sot714InputVO = (SOT714InputVO) body;
 		sot714OutputVO = new SOT714OutputVO();
 		sot714OutputVO.setWmshaiaDataVO(this.getByPassRiskData(sot714InputVO));
		sendRtnObject(sot714OutputVO);
	}
 	
 	/***
 	 * 高資產客戶投組適配承作，取得風險檢核資訊
 	 * 電文代碼：WMSHAIA
 	 * @param inputVO
 	 * @return
 	 * @throws Exception
 	 */
 	public WMSHAIADataVO getByPassRiskData(SOT714InputVO inputVO) throws Exception {
 		WMSHAIADataVO wmshaiaDataVO = new WMSHAIADataVO();

 		//init util
 		ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, WMSHAIA);
 		esbUtilInputVO.setModule(thisClaz + new Object(){}.getClass().getEnclosingMethod().getName());

 		//head
 		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
 		txHead.setDefaultTxHead();
		esbUtilInputVO.setTxHeadVO(txHead);

 		//body
		WMSHAIAVO wmshaiaInputVO = new WMSHAIAVO();
		wmshaiaInputVO.setCUST_ID(inputVO.getCustID());
		wmshaiaInputVO.setCUST_KYC(inputVO.getCUST_KYC());	
		wmshaiaInputVO.setSP_YN(inputVO.getSP_YN());	
		wmshaiaInputVO.setPROD_RISK(inputVO.getPROD_RISK());	
		wmshaiaInputVO.setAMT_BUY_1(decimalPadding(inputVO.getAMT_BUY_1(), 16, 4));	
		wmshaiaInputVO.setAMT_SELL_1(decimalPadding(inputVO.getAMT_SELL_1(), 16, 4));	
		wmshaiaInputVO.setAMT_BUY_2(decimalPadding(inputVO.getAMT_BUY_2(), 16, 4));	
		wmshaiaInputVO.setAMT_SELL_2(decimalPadding(inputVO.getAMT_SELL_2(), 16, 4));	
		wmshaiaInputVO.setAMT_BUY_3(decimalPadding(inputVO.getAMT_BUY_3(), 16, 4));	
		wmshaiaInputVO.setAMT_SELL_3(decimalPadding(inputVO.getAMT_SELL_3(), 16, 4));	
		wmshaiaInputVO.setAMT_BUY_4(decimalPadding(inputVO.getAMT_BUY_4(), 16, 4));	
		wmshaiaInputVO.setAMT_SELL_4(decimalPadding(inputVO.getAMT_SELL_4(), 16, 4));	
        esbUtilInputVO.setWmshaiaInputVO(wmshaiaInputVO);

        //發送電文
        List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

        for (ESBUtilOutputVO esbUtilOutputVO : vos) {
        	WMSHAIAVO wmshaiaOutputVO = esbUtilOutputVO.getWmshaiaOutputVO();
        	if (wmshaiaOutputVO != null) {
        		if(StringUtils.isNotBlank(wmshaiaOutputVO.getEMSGTXT())) {
        			throw new APException(wmshaiaOutputVO.getEMSGID() + ":" + wmshaiaOutputVO.getEMSGTXT());
        		}
        		wmshaiaDataVO.setSEQ(wmshaiaOutputVO.getSEQ());
        		wmshaiaDataVO.setDENO_AMT(decimalPoint(wmshaiaOutputVO.getDENO_AMT(), 4));
        		wmshaiaDataVO.setBASE_RISK_1(decimalPoint(wmshaiaOutputVO.getBASE_RISK_1(), 2));
        		wmshaiaDataVO.setAMT_1(decimalPoint(wmshaiaOutputVO.getAMT_1(), 4));
        		wmshaiaDataVO.setBASE_RISK_2(decimalPoint(wmshaiaOutputVO.getBASE_RISK_2(), 2));
        		wmshaiaDataVO.setAMT_2(decimalPoint(wmshaiaOutputVO.getAMT_2(), 4));
        		wmshaiaDataVO.setAMT_LEFT_2(decimalPoint(wmshaiaOutputVO.getAMT_LEFT_2(), 0));
        		wmshaiaDataVO.setBASE_RISK_3(decimalPoint(wmshaiaOutputVO.getBASE_RISK_3(), 2));
        		wmshaiaDataVO.setAMT_3(decimalPoint(wmshaiaOutputVO.getAMT_3(), 4));
        		wmshaiaDataVO.setAMT_LEFT_3(decimalPoint(wmshaiaOutputVO.getAMT_LEFT_3(), 0));
        		wmshaiaDataVO.setBASE_RISK_4(decimalPoint(wmshaiaOutputVO.getBASE_RISK_4(), 2));
        		wmshaiaDataVO.setAMT_4(decimalPoint(wmshaiaOutputVO.getAMT_4(), 4));
        		wmshaiaDataVO.setAMT_LEFT_4(decimalPoint(wmshaiaOutputVO.getAMT_LEFT_4(), 0));
        		wmshaiaDataVO.setRISK_1(decimalPoint(wmshaiaOutputVO.getRISK_1(), 2));
        		wmshaiaDataVO.setRISK_2(decimalPoint(wmshaiaOutputVO.getRISK_2(), 2));
        		wmshaiaDataVO.setRISK_3(decimalPoint(wmshaiaOutputVO.getRISK_3(), 2));
        		wmshaiaDataVO.setRISK_SUM(decimalPoint(wmshaiaOutputVO.getRISK_SUM(), 2));
        		wmshaiaDataVO.setVALIDATE_YN(wmshaiaOutputVO.getVALIDATE_YN());
        	}
        }
		
		return wmshaiaDataVO;
 	}
 	
 	/* 發送電文功能 - end */
}
