package com.systex.jbranch.app.server.fps.sot660;

import com.systex.jbranch.app.server.fps.sot701.CustHighNetWorthDataVO;
import com.systex.jbranch.app.server.fps.sot701.SOT701;
import com.systex.jbranch.app.server.fps.sot701.SOT701InputVO;
import com.systex.jbranch.app.server.fps.sot701.SOT701OutputVO;
import com.systex.jbranch.app.server.fps.sot707.ProdRefValVO;
import com.systex.jbranch.app.server.fps.sot707.SOT707;
import com.systex.jbranch.app.server.fps.sot707.SOT707InputVO;
import com.systex.jbranch.app.server.fps.sot707.SOT707OutputVO;
import com.systex.jbranch.app.server.fps.sot712.PRDFitInputVO;
import com.systex.jbranch.app.server.fps.sot712.SOT712;
import com.systex.jbranch.app.server.fps.sot712.SotPdf;
import com.systex.jbranch.app.server.fps.sot712.SotPdfContext;
import com.systex.jbranch.app.server.fps.sot714.CentInvDataVO;
import com.systex.jbranch.app.server.fps.sot714.SOT714;
import com.systex.jbranch.app.server.fps.sot714.SOT714InputVO;
import com.systex.jbranch.app.server.fps.sot714.SOT714OutputVO;
import com.systex.jbranch.app.server.fps.sot714.WMSHACRDataVO;
import com.systex.jbranch.app.server.fps.sot714.WMSHAIADataVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.commons.PdfUtil;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 高資產客戶投組適配試算
 * @date 2023/11/07
 * 
 */
@Component("sot660")
@Scope("request")
public class SOT660 extends FubonWmsBizLogic {

	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(SOT660.class);
	private SOT660InputVO sot660InputVO;
	private SOT660OutputVO sot660OutputVO;
	
	/***
	 * 取得客戶資料
	 * 1. 檢核是否為高資產客戶
	 * 2. 取得目前風險檢核值
	 * 3. 取得客戶基本資料
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void getCustData(Object body, IPrimitiveMap header) throws Exception {
		sot660InputVO = (SOT660InputVO) body;
		sot660OutputVO = new SOT660OutputVO();
		XmlInfo xmlInfo = new XmlInfo();
		
		//檢核是否為高資產客戶
		SOT714 sot714 = (SOT714) PlatformContext.getBean("sot714");
		CustHighNetWorthDataVO hnwcData = sot714.getHNWCData(sot660InputVO.getCustID());
//		if(hnwcData == null || !StringUtils.equals("Y", hnwcData.getValidHnwcYN()) || !StringUtils.equals("Y", hnwcData.getHnwcService())) {
//			throw new APException("非高資產客戶無法執行試算");
//		}
//		if(hnwcData != null && StringUtils.equals("Y", hnwcData.getSpFlag())) {
//			throw new APException("特定客戶不可越級適配，無法執行適算");
//		}
		
		SOT701OutputVO output701 = getCustInfo(sot660InputVO);
		//取得客戶註記資料
		sot660OutputVO.setFp032675Data(output701.getFp032675DataVO());
		//取得客戶KYC資料
		sot660OutputVO.setCustKYCData(output701.getCustKYCDataVO());
		if(StringUtils.isBlank(sot660OutputVO.getCustKYCData().getKycLevel())) {
			throw new APException("客戶無風險屬性，無法執行試算");
		}
		sot660OutputVO.setKYCExpiredYN("N");
		if(sot660OutputVO.getCustKYCData().getKycDueDate() == null || 
				sot660OutputVO.getCustKYCData().getKycDueDate().before(new Date())) {
			sot660OutputVO.setKYCExpiredYN("Y"); //KYC是否已過期 Y:已過期
		}
		//客戶風險檢核值
		sot660OutputVO.setCustRiskChkVal((String) xmlInfo.getVariable("SOT.CUST_RISK_CHECK_SUM", output701.getCustKYCDataVO().getKycLevel(), "F3"));
		//高資產客戶資料
		sot660OutputVO.setHnwcData(hnwcData);		
		//取得目前風險檢核值
		sot660InputVO.setCustRemarks(hnwcData.getSpFlag());
		sot660OutputVO.setWmshaiaData(this.getByPassRiskData(sot660InputVO, output701));
				
		sendRtnObject(sot660OutputVO);
	}
	
	/***
	 * 電文取得客戶資訊
	 * @param inputVO
	 * @return
	 * @throws Exception
	 */
	private SOT701OutputVO getCustInfo(SOT660InputVO inputVO) throws Exception {
		SOT701InputVO inputVO701 = new SOT701InputVO();
		SOT701OutputVO outputVO701 = new SOT701OutputVO();

		inputVO701.setCustID(sot660InputVO.getCustID());
		SOT701 sot701 = (SOT701) PlatformContext.getBean("sot701");
		// 查詢客戶註記資料
		outputVO701.setFp032675DataVO(sot701.getFP032675Data(inputVO701));
		// 查詢：KYC等級、KYC效期
		outputVO701.setCustKYCDataVO(sot701.getCustKycData(inputVO701));
		
		return outputVO701;
	}
	
	/***
	 * 取得越級適配風險檢核值
	 * @param inputVO
	 * @return
	 * @throws Exception
	 */
	private WMSHAIADataVO getByPassRiskData(SOT660InputVO inputVO, SOT701OutputVO sot701Data) throws Exception {
		SOT714 sot714 = (SOT714) PlatformContext.getBean("sot714");
		SOT714InputVO inputVO714 = new SOT714InputVO();
		inputVO714.setCustID(inputVO.getCustID());
		inputVO714.setCUST_KYC(sot701Data.getCustKYCDataVO().getKycLevel());
		inputVO714.setSP_YN(inputVO.getCustRemarks()); //hnwcData.spFlag 高資產特定客戶
		inputVO714.setPROD_RISK("P1"); //取得現在風險檢核值，設最低風險值
		
		//查詢客戶風險檢核值資料
		WMSHAIADataVO riskValData = sot714.getByPassRiskData(inputVO714);
		return riskValData;
	}
	
	/***
	 * 投組適配試算
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void trialCalculate(Object body, IPrimitiveMap header) throws Exception {
		sot660InputVO = (SOT660InputVO) body;
		sot660OutputVO = new SOT660OutputVO();
		
		SOT714 sot714 = (SOT714) PlatformContext.getBean("sot714");
		SOT714InputVO inputVO714 = new SOT714InputVO();
		inputVO714.setCustID(sot660InputVO.getCustID());
		inputVO714.setCUST_KYC(sot660InputVO.getKycLevel());
		inputVO714.setSP_YN(sot660InputVO.getCustRemarks()); //hnwcData.spFlag 高資產特定客戶
		inputVO714.setPROD_RISK(sot660InputVO.getProdRisk()); //取得現在風險檢核值，設最低風險值
		
		BigDecimal amtBuy1 = getAmtTwd(sot660InputVO.getCUR12(),sot660InputVO.getAMT12()).add(getAmtTwd(sot660InputVO.getCUR14(),sot660InputVO.getAMT14()));
		BigDecimal amtBuy2 = getAmtTwd(sot660InputVO.getCUR22(),sot660InputVO.getAMT22()).add(getAmtTwd(sot660InputVO.getCUR24(),sot660InputVO.getAMT24()));
		BigDecimal amtBuy3 = getAmtTwd(sot660InputVO.getCUR32(),sot660InputVO.getAMT32()).add(getAmtTwd(sot660InputVO.getCUR34(),sot660InputVO.getAMT34()));
		BigDecimal amtBuy4 = getAmtTwd(sot660InputVO.getCUR42(),sot660InputVO.getAMT42()).add(getAmtTwd(sot660InputVO.getCUR44(),sot660InputVO.getAMT44()));
		BigDecimal amtSell1 = getAmtTwd(sot660InputVO.getCUR13(),sot660InputVO.getAMT13()).add(getAmtTwd(sot660InputVO.getCUR15(),sot660InputVO.getAMT15()));
		BigDecimal amtSell2 = getAmtTwd(sot660InputVO.getCUR23(),sot660InputVO.getAMT23()).add(getAmtTwd(sot660InputVO.getCUR25(),sot660InputVO.getAMT25()));
		BigDecimal amtSell3 = getAmtTwd(sot660InputVO.getCUR33(),sot660InputVO.getAMT33()).add(getAmtTwd(sot660InputVO.getCUR35(),sot660InputVO.getAMT35()));
		BigDecimal amtSell4 = getAmtTwd(sot660InputVO.getCUR43(),sot660InputVO.getAMT43()).add(getAmtTwd(sot660InputVO.getCUR45(),sot660InputVO.getAMT45()));
		
		//贖回金額不可大於投資餘額
		if(amtSell1.compareTo(sot660InputVO.getWmshaiaData().getAMT_1()) > 0) throw new APException("P1贖回金額不可大於投資餘額");
		if(amtSell2.compareTo(sot660InputVO.getWmshaiaData().getAMT_2()) > 0) throw new APException("P2贖回金額不可大於投資餘額");
		if(amtSell3.compareTo(sot660InputVO.getWmshaiaData().getAMT_3()) > 0) throw new APException("P3贖回金額不可大於投資餘額");
		if(amtSell4.compareTo(sot660InputVO.getWmshaiaData().getAMT_4()) > 0) throw new APException("P4贖回金額不可大於投資餘額");
		
		inputVO714.setAMT_BUY_1(amtBuy1);
		inputVO714.setAMT_BUY_2(amtBuy2);
		inputVO714.setAMT_BUY_3(amtBuy3);
		inputVO714.setAMT_BUY_4(amtBuy4);
		inputVO714.setAMT_SELL_1(amtSell1);
		inputVO714.setAMT_SELL_2(amtSell2);
		inputVO714.setAMT_SELL_3(amtSell3);
		inputVO714.setAMT_SELL_4(amtSell4);
		
		//查詢客戶風險檢核值資料
		WMSHAIADataVO riskValData = sot714.getByPassRiskData(inputVO714);
		sot660OutputVO.setWmshaiaData(riskValData);

		sendRtnObject(sot660OutputVO);
	}
		
	/*** 取得折台金額
	 * 
	 * @param currCode 幣別
	 * @param oriAmt 原幣金額
	 * @return
	 * @throws DAOException
	 * @throws JBranchException
	 */
	private BigDecimal getAmtTwd(String currCode, BigDecimal oriAmt) throws DAOException, JBranchException {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append("select (NVL(BUY_RATE, 1) * :oriAmt) AS AMT_TWD ");
		sb.append("  from TBPMS_IQ053 ");
		sb.append(" where CUR_COD = :currCode AND MTN_DATE = (SELECT MAX(MTN_DATE) FROM TBPMS_IQ053) ");
		queryCondition.setObject("currCode", currCode);
		queryCondition.setObject("oriAmt", oriAmt == null ? BigDecimal.ZERO : oriAmt);
		queryCondition.setQueryString(sb.toString());
		List<Map<String, Object>> cList = dam.exeQuery(queryCondition);

		return CollectionUtils.isNotEmpty(cList) ? (BigDecimal) cList.get(0).get("AMT_TWD") : BigDecimal.ZERO;
	}
	
	/***
	 * 取得幣別相對應金額
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void getCurAmt(Object body, IPrimitiveMap header) throws Exception {
		sot660InputVO = (SOT660InputVO) body;
		sot660OutputVO = new SOT660OutputVO();
		
		sot660OutputVO.setCurAmt(getCurAmt(sot660InputVO.getCUR12(), sot660InputVO.getAMT12()));
		sendRtnObject(sot660OutputVO);
	}

	/*** 取得幣別相對應金額(最大可申購金額)
	 * 
	 * @param currCode 幣別
	 * @param oriAmt 原幣金額
	 * @return
	 * @throws DAOException
	 * @throws JBranchException
	 */
	private BigDecimal getCurAmt(String currCode, BigDecimal oriAmt) throws DAOException, JBranchException {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append("select TRUNC((:oriAmt / NVL(BUY_RATE, 1)), 2) AS CUR_AMT ");
		sb.append("  from TBPMS_IQ053 ");
		sb.append(" where CUR_COD = :currCode AND MTN_DATE = (SELECT MAX(MTN_DATE) FROM TBPMS_IQ053) ");
		queryCondition.setObject("currCode", currCode);
		queryCondition.setObject("oriAmt", oriAmt == null ? BigDecimal.ZERO : oriAmt);
		queryCondition.setQueryString(sb.toString());
		List<Map<String, Object>> cList = dam.exeQuery(queryCondition);

		return CollectionUtils.isNotEmpty(cList) ? (BigDecimal) cList.get(0).get("CUR_AMT") : BigDecimal.ZERO;
	}
	
}
