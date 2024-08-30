package com.systex.jbranch.app.server.fps.iot960;

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
 * 保險進件_高資產客戶投組適配
 * @date 2023/12/13
 * 
 */
@Component("iot960")
@Scope("request")
public class IOT960 extends FubonWmsBizLogic {

	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(IOT960.class);
	private IOT960InputVO iot960InputVO;
	private IOT960OutputVO iot960OutputVO;
	
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
		iot960InputVO = (IOT960InputVO) body;
		iot960OutputVO = new IOT960OutputVO();
		XmlInfo xmlInfo = new XmlInfo();
		
		SOT701OutputVO output701 = getCustInfo(iot960InputVO);
		//取得客戶註記資料
		iot960OutputVO.setFp032675Data(output701.getFp032675DataVO());
		//客戶風險檢核值
		iot960OutputVO.setCustRiskChkVal((String) xmlInfo.getVariable("SOT.CUST_RISK_CHECK_SUM", output701.getCustKYCDataVO().getKycLevel(), "F3"));
		//取得目前風險檢核值
		iot960OutputVO.setWmshaiaData(this.getByPassRiskData(iot960InputVO, output701));
		//取得險種幣別折台匯率
		iot960OutputVO.setProdCurrRate(getProdCurrRate(iot960InputVO.getCURR_CD()));
		
		sendRtnObject(iot960OutputVO);
	}
	
	/***
	 * 電文取得客戶資訊
	 * @param inputVO
	 * @return
	 * @throws Exception
	 */
	private SOT701OutputVO getCustInfo(IOT960InputVO inputVO) throws Exception {
		SOT701InputVO inputVO701 = new SOT701InputVO();
		SOT701OutputVO outputVO701 = new SOT701OutputVO();

		inputVO701.setCustID(inputVO.getCUST_ID());
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
	private WMSHAIADataVO getByPassRiskData(IOT960InputVO inputVO, SOT701OutputVO sot701Data) throws Exception {
		SOT714 sot714 = (SOT714) PlatformContext.getBean("sot714");
		SOT714InputVO inputVO714 = new SOT714InputVO();
		inputVO714.setCustID(inputVO.getCUST_ID());
		inputVO714.setCUST_KYC(inputVO.getCUST_RISK());
		inputVO714.setSP_YN(sot701Data.getFp032675DataVO().getCustRemarks());
		inputVO714.setPROD_RISK("P1"); //取得現在風險檢核值，設最低風險值
		
		//查詢客戶風險檢核值資料
		WMSHAIADataVO riskValData = sot714.getByPassRiskData(inputVO714);
		return riskValData;
	}
	
	/***
	 * 投組越級適配試算
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void trialCalculate(Object body, IPrimitiveMap header) throws Exception {
		iot960InputVO = (IOT960InputVO) body;
		iot960OutputVO = new IOT960OutputVO();
		
		SOT714 sot714 = (SOT714) PlatformContext.getBean("sot714");
		SOT714InputVO inputVO714 = new SOT714InputVO();
		inputVO714.setCustID(iot960InputVO.getCUST_ID());
		inputVO714.setCUST_KYC(iot960InputVO.getCUST_RISK()); //客戶風險等級
		inputVO714.setSP_YN(iot960InputVO.getCustRemarks()); //特定客戶
		inputVO714.setPROD_RISK(iot960InputVO.getSENIOR_OVER_PVAL()); //取得選擇越級適配的風險檢核值
		//依越級風險檢核值，傳入投入金額
		String overPval = iot960InputVO.getSENIOR_OVER_PVAL().substring(1);
		if(StringUtils.equals("1", overPval)) inputVO714.setAMT_BUY_1(iot960InputVO.getOverPvalAmt());
		if(StringUtils.equals("2", overPval)) inputVO714.setAMT_BUY_2(iot960InputVO.getOverPvalAmt());
		if(StringUtils.equals("3", overPval)) inputVO714.setAMT_BUY_3(iot960InputVO.getOverPvalAmt());
		if(StringUtils.equals("4", overPval)) inputVO714.setAMT_BUY_4(iot960InputVO.getOverPvalAmt());
		
		//查詢客戶風險檢核值資料
		WMSHAIADataVO riskValData = sot714.getByPassRiskData(inputVO714);
		iot960OutputVO.setWmshaiaData(riskValData);

		sendRtnObject(iot960OutputVO);
	}
		
	/***
	 * 取得險種幣別匯率
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void getProdCurrRate(Object body, IPrimitiveMap header) throws Exception {
		iot960InputVO = (IOT960InputVO) body;
		iot960OutputVO = new IOT960OutputVO();
		
		iot960OutputVO.setProdCurrRate(getProdCurrRate(iot960InputVO.getCURR_CD()));
		sendRtnObject(iot960OutputVO);
	}
	
	/*** 
	 * 取得險種幣別匯率
	 * @param currCode 幣別
	 * @return
	 * @throws DAOException
	 * @throws JBranchException
	 */
	private BigDecimal getProdCurrRate(String currCode) throws DAOException, JBranchException {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append("select NVL(BUY_RATE, 1) AS PROD_CURR_RATE ");
		sb.append("  from TBPMS_IQ053 ");
		sb.append(" where CUR_COD = :currCode AND MTN_DATE = (SELECT MAX(MTN_DATE) FROM TBPMS_IQ053) ");
		queryCondition.setObject("currCode", currCode);
		queryCondition.setQueryString(sb.toString());
		List<Map<String, Object>> cList = dam.exeQuery(queryCondition);

		return CollectionUtils.isNotEmpty(cList) ? (BigDecimal) cList.get(0).get("PROD_CURR_RATE") : new BigDecimal("1");
	}
	
		
}
