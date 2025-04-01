package com.systex.jbranch.fubon.commons.fitness;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.prd110.PRD110InputVO;
import com.systex.jbranch.app.server.fps.prd120.PRD120InputVO;
import com.systex.jbranch.app.server.fps.prd130.PRD130InputVO;
import com.systex.jbranch.app.server.fps.prd140.PRD140InputVO;
import com.systex.jbranch.app.server.fps.sot701.CustHighNetWorthDataVO;
import com.systex.jbranch.app.server.fps.sot701.FP032675DataVO;
import com.systex.jbranch.common.xmlInfo.XMLInfo;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.commons.cbs.CBSService;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.XmlInfo;

@Component("ProdFitness")
@Scope("request")
public class ProdFitness extends FubonWmsBizLogic {
	private Logger logger = LoggerFactory.getLogger(ProdFitness.class);
	private DataAccessManager dam = null;
	private XMLInfo xmlinfo = new XMLInfo();
	
	private ProdFitnessCustVO custVO;
	
	@Autowired
	private CBSService cbsservice;
	
	private String custID;
	private String piBuy;					//商品專投申購
	private String riskCateID;				//商品風險屬性
	private String prodCountryID;			//商品國籍別
	private String prodQValue;				//商品Q值
	private String prodStakeholder;			//商品利害關係人是否可申購
	private BigDecimal prodMaturityDate;	//商品年限
	private BigDecimal prodParamAgeLimit;	//商品年限XML設定值
	private String custProdRiskMapping;		//客戶與商品風險屬性對應XML值
	private String prodOBU;					//是否有OBU商品註記
	private String prodOBUBuy;				//限OBU申購
	private String prodOBUPro;				//OBU專投註記
	private String prodOBUAge;				//OBU客戶年齡限制
	private String prodBuyTWD;				//商品申購台幣信託註記
	private String trustCurrType;			//信託類型
	private BigDecimal prodQuotas;			//商品申購額度控管
	private String prodExpoFlag;			//基金報酬揭露註記
	private String prodHighYield;			//高收益基金檢核
	private String prodSugSelling;			//基金建議賣出註記
	private String prodFus40;				//商品未核備註記
	private String prodId;
	private String sameSerialYN;			//是否為同系列基金
	private String sameSerialProdId;		//同系列基金商品代碼
	private String NFS100_YN;				//是否為新興市場之非投資等級債券型基金
	private String HNWC_BUY;				//限高資產客戶申購
	private String OVS_PRIVATE_YN;			//境外私募基金註記
	private String prdID;					//商品代碼
	private String trustTS;					//金錢信託下單
	private String dynamicType;				//動態鎖利類別 M:母基金 C:子基金
	private String dynamicM;				//動態鎖利母基金註記
	private String dynamicC;				//動態鎖利子基金註記
	private String prodCurr;				//商品計價幣別
	private String dynamicProdCurrM; 		//動態鎖利母基金計價幣別
	private String inProdIdM;				//動態鎖利轉入母基金ID(轉換)
	private String fromPRD111YN; 				//從動態鎖利適配過來
	
	//回傳值
	private ProdFitnessOutputVO outputVO = new ProdFitnessOutputVO();
	
	public enum CustType { PERSON, CORPORATION }	
	
	/**
	 * Constructor
	 */
	public ProdFitness() {}
	
	/**
	 * Constructor
	 * @param custId：客戶ID
	 */
	public ProdFitness(String custId) {
		this.setCustID(custId);		
	}
	
	public ProdFitnessCustVO getCustVO() throws DAOException, JBranchException {
		if(this.custVO == null) {
			dam = this.getDataAccessManager();
			custVO = new ProdFitnessCustVO(dam, this.getCustID());
		}
		
		return custVO;
	}

	public void setCustVO(ProdFitnessCustVO custVO) {
		this.custVO = custVO;
	}

	public String getCustID() {
		return custID;
	}

	public void setCustID(String custID) {
		this.custID = custID;
	}

	public String getPiBuy() {
		return piBuy;
	}

	public void setPiBuy(String piBuy) {
		this.piBuy = piBuy;
	}

	public String getRiskCateID() {
		return riskCateID;
	}

	public void setRiskCateID(String riskCateID) {
		this.riskCateID = riskCateID;
	}

	public String getProdCountryID() {
		return prodCountryID;
	}

	public void setProdCountryID(String prodCountryID) {
		this.prodCountryID = prodCountryID;
	}
	
	public String getProdQValue() {
		return prodQValue;
	}

	public void setProdQValue(String prodQValue) {
		this.prodQValue = prodQValue;
	}

	public String getProdStakeholder() {
		return prodStakeholder;
	}

	public void setProdStakeholder(String prodStakeholder) {
		this.prodStakeholder = prodStakeholder;
	}

	public BigDecimal getProdMaturityDate() {
		return prodMaturityDate;
	}

	public void setProdMaturityDate(BigDecimal prodMaturityDate) {
		this.prodMaturityDate = prodMaturityDate;
	}

	public BigDecimal getProdParamAgeLimit() throws DAOException, JBranchException {
		if(this.prodParamAgeLimit == null) {
			XmlInfo xmlInfo = new XmlInfo();
			String ageLimit = (String) xmlInfo.getVariable("SOT.BN_AGE_LIMIT", "1", "F3");
			
			if(StringUtils.isNotBlank(ageLimit)) {
				this.prodParamAgeLimit = new BigDecimal(ageLimit);
			} else {
				this.prodParamAgeLimit = BigDecimal.ZERO;
			}
		}
		
		return prodParamAgeLimit;
	}

	public void setProdParamAgeLimit(BigDecimal prodParamAgeLimit) {
		this.prodParamAgeLimit = prodParamAgeLimit;
	}

	public String getCustProdRiskMapping(String custRiskId) throws DAOException, JBranchException {
		if(StringUtils.isBlank(this.custProdRiskMapping) && !StringUtils.isWhitespace(this.custProdRiskMapping)) {
			XmlInfo xmlInfo = new XmlInfo();
			String riskFit = (String) xmlInfo.getVariable("SOT.RISK_FIT_CONFIG", custRiskId, "F3");
			
			this.custProdRiskMapping = (StringUtils.isBlank(riskFit) ? "" : riskFit.trim());
		}
				
		return custProdRiskMapping;
	}

	public void setCustProdRiskMapping(String custProdRiskMapping) {
		this.custProdRiskMapping = custProdRiskMapping;
	}

	public String getProdOBU() {
		return prodOBU;
	}

	public void setProdOBU(String prodOBU) {
		this.prodOBU = prodOBU;
	}

	public String getProdOBUBuy() {
		return prodOBUBuy;
	}

	public void setProdOBUBuy(String prodOBUBuy) {
		this.prodOBUBuy = prodOBUBuy;
	}

	public String getProdOBUPro() {
		return prodOBUPro;
	}

	public void setProdOBUPro(String prodOBUPro) {
		this.prodOBUPro = prodOBUPro;
	}

	public String getProdOBUAge() {
		return prodOBUAge;
	}

	public void setProdOBUAge(String prodOBUAge) {
		this.prodOBUAge = prodOBUAge;
	}

	public String getProdBuyTWD() {
		return prodBuyTWD;
	}

	public void setProdBuyTWD(String prodBuyTWD) {
		this.prodBuyTWD = prodBuyTWD;
	}

	public String getTrustCurrType() {
		return trustCurrType;
	}

	public void setTrustCurrType(String trustCurrType) {
		this.trustCurrType = trustCurrType;
	}

	public BigDecimal getProdQuotas() {
		return prodQuotas;
	}

	public void setProdQuotas(BigDecimal prodQuotas) {
		this.prodQuotas = prodQuotas;
	}

	public String getProdExpoFlag() {
		return prodExpoFlag;
	}

	public void setProdExpoFlag(String prodExpoFlag) {
		this.prodExpoFlag = prodExpoFlag;
	}

	public String getProdHighYield() {
		return prodHighYield;
	}

	public void setProdHighYield(String prodHighYield) {
		this.prodHighYield = prodHighYield;
	}

	public String getProdSugSelling() {
		return prodSugSelling;
	}

	public void setProdSugSelling(String prodSugSelling) {
		this.prodSugSelling = prodSugSelling;
	}

	public String getProdFus40() {
		return prodFus40;
	}

	public void setProdFus40(String prodFus40) {
		this.prodFus40 = prodFus40;
	}

	public String getProdId() {
		return prodId;
	}

	public void setProdId(String prodId) {
		this.prodId = prodId;
	}

	public String getSameSerialYN() {
		return sameSerialYN;
	}

	public void setSameSerialYN(String sameSerialYN) {
		this.sameSerialYN = sameSerialYN;
	}

	public String getSameSerialProdId() {
		return sameSerialProdId;
	}

	public void setSameSerialProdId(String sameSerialProdId) {
		this.sameSerialProdId = sameSerialProdId;
	}

	public String getNFS100_YN() {
		return NFS100_YN;
	}

	public void setNFS100_YN(String nFS100_YN) {
		NFS100_YN = nFS100_YN;
	}

	public String getHNWC_BUY() {
		return HNWC_BUY;
	}

	public void setHNWC_BUY(String hNWC_BUY) {
		HNWC_BUY = hNWC_BUY;
	}
	
	public String getOVS_PRIVATE_YN() {
		return OVS_PRIVATE_YN;
	}

	public void setOVS_PRIVATE_YN(String oVS_PRIVATE_YN) {
		OVS_PRIVATE_YN = oVS_PRIVATE_YN;
	}

	public String getPrdID() {
		return prdID;
	}

	public void setPrdID(String prdID) {
		this.prdID = prdID;
	}

	public String getTrustTS() {
		return trustTS;
	}

	public void setTrustTS(String trustTS) {
		this.trustTS = trustTS;
	}

	public String getDynamicType() {
		return dynamicType;
	}

	public void setDynamicType(String dynamicType) {
		this.dynamicType = dynamicType;
	}

	public String getDynamicM() {
		return dynamicM;
	}

	public void setDynamicM(String dynamicM) {
		this.dynamicM = dynamicM;
	}

	public String getDynamicC() {
		return dynamicC;
	}

	public void setDynamicC(String dynamicC) {
		this.dynamicC = dynamicC;
	}

	public String getProdCurr() {
		return prodCurr;
	}

	public void setProdCurr(String prodCurr) {
		this.prodCurr = prodCurr;
	}

	public String getDynamicProdCurrM() {
		return dynamicProdCurrM;
	}

	public void setDynamicProdCurrM(String dynamicProdCurrM) {
		this.dynamicProdCurrM = dynamicProdCurrM;
	}

	public String getInProdIdM() {
		return inProdIdM;
	}

	public void setInProdIdM(String inProdIdM) {
		this.inProdIdM = inProdIdM;
	}

	public String getFromPRD111YN() {
		return fromPRD111YN;
	}

	public void setFromPRD111YN(String fromPRD111YN) {
		this.fromPRD111YN = fromPRD111YN;
	}

	/**
	 * 基金、ETF/基金客戶資料適配檢核
	 * validkCustRiskAttr：客戶風險屬性檢核
	 * @param custId：客戶ID
	 * @return
	 * @throws DAOException
	 * @throws JBranchException
	 * @throws Exception
	 */
	public ProdFitnessOutputVO validFundETFCustRiskAttr(String custId) throws DAOException, JBranchException, Exception {
		this.setCustID(custId);
		
		if(BooleanUtils.isTrue(this.validCustRiskAttr()))
			return new ProdFitnessOutputVO();
		else
			return outputVO;
	}
	
	/**
	 * 基金、ETF/基金客戶資料適配檢核
	 * validCustFATCA：客戶FATCA註記檢核
	 * #1981 擴大到海外債 SI SN
	 * @param custId：客戶ID
	 * @return
	 * @throws DAOException
	 * @throws JBranchException
	 * @throws Exception
	 */
	public ProdFitnessOutputVO validFundETFCustFATCA(String custId) throws DAOException, JBranchException, Exception {
		this.setCustID(custId);
		
		if(BooleanUtils.isTrue(this.validCustFATCA()))
			return new ProdFitnessOutputVO();
		else
			return outputVO;
	}
	
	/**
	 * 海外債、SI、SN客戶資料適配檢核
	 * validkCustRiskAttr：客戶風險屬性檢核
	 * @param custId：客戶ID
	 * @return
	 * @throws DAOException
	 * @throws JBranchException
	 * @throws Exception
	 */
	public ProdFitnessOutputVO validCustBondSISN(String custId) throws DAOException, JBranchException, Exception {
		this.setCustID(custId);
		
		if(BooleanUtils.isTrue(this.validCustRiskAttr()))
			return new ProdFitnessOutputVO();
		else
			return outputVO;
	}
	
	/**
	 * 基金商品資料適配檢核
	 * validOBUProd：有OBU_PROD商品註記，只有OBU客戶可申購
	 * validOBUBuy：OBU客戶無法申購此商品
	 * validProdExpoFlag：基金報酬揭露檢核
	 * validHighYield：高收益基金檢核
	 * validOBUPI：非專業投資人不得購買此商品
	 * validOBUAge：OBU客戶年齡限
	 * validSugSelling：此基金為本行建議售出之標的，請提醒客戶並確認其申購意願，謝謝！
	 * validFus40 ：OBU 可以買未核備, DBU不能買未核備商品
	 * validProdRiskCate：商品風險屬性檢核
	 * validProdQuotas：本次交易中有基金申購額度控管，交易是否成功依鍵入作業系統時之剩餘額度而定
	 * validTrustTypeTWD：是否可承作台幣信託
	 * validCustProdRisk：商品風險等級與客戶風險等級檢核
	 * validSameSerial：轉換必需為同系列商品
	 * validNFS100：65歲以上客戶除非有專投資格，否則不得申購新興市場之非投資等級債券基金
	 * validOvsPrivate：境外私募基金在可交易期間中
	 * validPrivatelyOfferedFund：僅限「高資產客戶」、「專業機構投資人」與「高淨值投資法人」申購境外私募基金
	 * validHNWCBuy：客戶非高資產客戶，不得申購僅限高資產客戶申購之商品(境外私募基金) ==> 合併至 validPrivatelyOfferedFund
	 * @param product
	 * @param isChkPC
	 * @return
	 * @throws Exception
	 */
	public ProdFitnessOutputVO validProdFund(Map<String, Object> product, Boolean isChkPC, PRD110InputVO prd110InputVO) throws Exception {
		outputVO = new ProdFitnessOutputVO();
		
		this.setPrdID((String) product.get("PRD_ID"));
		this.setRiskCateID((String) product.get("RISKCATE_ID"));
		this.setProdOBU((String) product.get("OBU_PROD"));
		this.setProdOBUBuy((String) product.get("OBU_BUY"));
		this.setProdOBUPro((String) product.get("OBU_PRO"));
		this.setProdOBUAge((String) product.get("OBU_AGE"));
		this.setProdBuyTWD((String) product.get("BUY_TWD"));
		this.setProdQuotas((BigDecimal) product.get("QUOTAS"));
		this.setProdExpoFlag((String) product.get("FLAG"));
		this.setProdHighYield((String) product.get("HIGH_YIELD"));
		this.setProdSugSelling((String) product.get("SELLING"));
		this.setProdFus40((String) product.get("FUS40"));
		this.setDynamicM((String) product.get("DYNAMIC_M"));
		this.setDynamicC((String) product.get("DYNAMIC_C"));
		this.setCustID(prd110InputVO.getCust_id());
		this.setProdId(prd110InputVO.getFund_id());
		this.setSameSerialProdId(prd110InputVO.getSameSerialProdId());
		this.setSameSerialYN(prd110InputVO.getSameSerialYN());
		this.setTrustTS(prd110InputVO.getTrustTS());
		this.setDynamicType(prd110InputVO.getDynamicType());
		this.setNFS100_YN(ObjectUtils.toString(product.get("NFS100_YN")));
		this.setOVS_PRIVATE_YN(ObjectUtils.toString(product.get("OVS_PRIVATE_YN")));
		this.setHNWC_BUY(this.getOVS_PRIVATE_YN());
		this.setProdCurr((String) product.get("CURRENCY_STD_ID"));
		this.setDynamicProdCurrM(prd110InputVO.getDynamicProdCurrM());
		this.setInProdIdM(prd110InputVO.getInProdIdM());
		this.setFromPRD111YN(prd110InputVO.getFromPRD111YN());
		
		if(BooleanUtils.isTrue(this.validOBUProd()) 
				&& BooleanUtils.isTrue(this.validOBUBuy())
				&& BooleanUtils.isTrue(this.validProdExpoFlag())
				&& BooleanUtils.isTrue(this.validHighYield())
				&& BooleanUtils.isTrue(this.validOBUPI())
				&& BooleanUtils.isTrue(this.validOBUAge())
				&& BooleanUtils.isTrue(this.validSugSelling())
				&& BooleanUtils.isTrue(this.validFus40())
				&& BooleanUtils.isTrue(this.validProdRiskCate())
				&& BooleanUtils.isTrue(this.validProdQuotas())
				&& BooleanUtils.isTrue(this.validNFS100())
				&& BooleanUtils.isTrue(this.validOvsPrivate())
				&& BooleanUtils.isTrue(this.validPrivatelyOfferedFund())
				&& BooleanUtils.isTrue(this.validDynamic())) {
			
			if(BooleanUtils.isTrue(isChkPC)) {	
				this.setTrustCurrType(prd110InputVO.getTrustType());
				
				if(BooleanUtils.isTrue(this.validTrustTypeTWD())) {
					//由商品查詢進入，不需檢查，已於SQL濾掉；由下單進入才需檢查
					if(BooleanUtils.isTrue(this.validCustProdRisk())) {
						this.validSameSerial();
					}
				}
			}				
		}
		
		return outputVO;
	}
	
	/**
	 * ETF商品資料適配檢核
	 * validPIBuyETFSI：特定客戶取得專投身分未滿2週不得購買此商品；特定客戶取得專投身分未滿2週不得購買此商品
	 * validProdRiskCate：商品風險屬性檢核
	 * validCustProdRisk：商品風險等級與客戶風險等級檢核
	 * validPIBuy65：65歲以上客戶不得申購僅限專投客戶申購之ETF (2022/09/19取消控管)
	 * @param piBuy：商品專投申購
	 * @param riskCateId：商品風險屬性
	 * @param isChkPC：是否做商品與客戶風險等級檢核
	 * @return
	 * @throws Exception
	 */
	public ProdFitnessOutputVO validProdETF(Map<String, Object> product, Boolean isChkPC, PRD120InputVO prd120InputVO) throws Exception {
		outputVO = new ProdFitnessOutputVO();
		
		this.setPiBuy(product.get("PI_BUY") != null ? product.get("PI_BUY").toString() : "");
		this.setRiskCateID((String) product.get("RISKCATE_ID"));
		this.setTrustTS(prd120InputVO.getTrustTS());
		
		if(BooleanUtils.isTrue(this.validPIBuyBondSNETF()) 
				&& BooleanUtils.isTrue(this.validProdRiskCate())) {
			if(BooleanUtils.isTrue(isChkPC)) {	
				//由商品查詢進入，不需檢查，已於SQL濾掉；由下單進入才需檢查
				this.validCustProdRisk();
			}				
		}
		
		return outputVO;
	}
	
	/**
	 * 海外債商品資料適配檢核
	 * validProdRiskCate：商品風險屬性檢核
	 * validCustProdRisk：商品風險等級與客戶風險等級檢核
	 * validCustomizedProd：客製化商品無法申購
	 * validPIBuyBondSN：非專業投資人或非高資產客戶不得購買此商品
	 * validBondOBUBuy：OBU客戶無法申購此商品/限OBU客戶申購
	 * validBondCustAge：65歲以上客戶除非有專投資格，否則不得申購客戶年齡+海外債到期年限超過80以上之商品
	 * validHNWCBuy：客戶非高資產客戶，不得申購僅限高資產客戶申購之商品
	 * @param piBuy：商品專投申購
	 * @param riskCateId：商品風險屬性
	 * @param isChkPC：是否做商品與客戶風險等級檢核
	 * @return
	 * @throws Exception
	 */
	public ProdFitnessOutputVO validProdBond(Map<String, Object> product, Boolean isChkPC, PRD130InputVO prd130InputVO) throws Exception {
		outputVO = new ProdFitnessOutputVO();
		
		this.setProdId((String) product.get("PRD_ID"));
 		this.setPiBuy(product.get("PI_BUY") != null ? product.get("PI_BUY").toString() : "");
		this.setRiskCateID((String) product.get("RISKCATE_ID"));
		this.setProdOBUBuy((String) product.get("OBU_BUY_2"));
		this.setProdMaturityDate((BigDecimal) product.get("DATE_OF_MATURITY"));
		this.setHNWC_BUY((String) product.get("HNWC_BUY")); 
		this.setTrustTS(prd130InputVO.getTrustTS());
		
		if(BooleanUtils.isTrue(this.validPIBuyBondSNETF()) 
				&& BooleanUtils.isTrue(this.validHNWCBuy())
				&& BooleanUtils.isTrue(this.validProdRiskCate()) 
				&& BooleanUtils.isTrue(this.validBondOBUBuy())
				&& BooleanUtils.isTrue(this.validBondCustAge())) {
			if(BooleanUtils.isTrue(isChkPC)) {	
				//由商品查詢進入，不需檢查，已於SQL濾掉；由下單進入才需檢查
				if(BooleanUtils.isTrue(this.validCustProdRisk())) {
					this.validCustomizedProd();
				}
			}		
		}
		
		return outputVO;
	}
	
	/**
	 * SN商品資料適配檢核
	 * validProdRiskCate：商品風險屬性檢核
	 * validMaturityDate：年限檢測(N/A)無法判斷，無法適配
	 * validProdQValue：商品與客戶Q值檢核
	 * validCustProdRisk：商品風險等級與客戶風險等級檢核
	 * validCustomizedProd：客製化商品無法申購
	 * validPIBuyBondSN：非專業投資人或非高資產客戶不得購買此商品
	 * validBondOBUBuy：OBU客戶無法申購此商品/限OBU客戶申購
	 * @param piBuy：商品專投申購
	 * @param riskCateId：商品風險屬性
	 * @param isChkPC：是否做商品與客戶風險等級檢核
	 * @return
	 * @throws Exception
	 */
	public ProdFitnessOutputVO validProdSN(Map<String, Object> product, Boolean isChkPC, PRD140InputVO prd140InputVO) throws Exception {
		outputVO = new ProdFitnessOutputVO();
		
		this.setProdId((String) product.get("PRD_ID"));
		this.setRiskCateID((String) product.get("RISKCATE_ID"));
		//this.setProdMaturityDate((Date) product.get("MATURITY_DATE"));
		this.setProdMaturityDate((BigDecimal) product.get("DATE_OF_MATURITY"));
		this.setProdQValue((String) product.get("GLCODE"));
		this.setPiBuy(product.get("PI_BUY") != null ? product.get("PI_BUY").toString() : "");
		this.setProdOBUBuy((String) product.get("OBU_BUY_2"));
		this.setHNWC_BUY((String) product.get("HNWC_BUY")); 
		this.setTrustTS(prd140InputVO.getTrustTS());

		if(BooleanUtils.isTrue(this.validProdRiskCate())
				&& BooleanUtils.isTrue(this.validHNWCBuy())
				&& BooleanUtils.isTrue(this.validMaturityDate())
				&& BooleanUtils.isTrue(needCheckQValue("SN") ? this.validProdQValue() : true )
				&& BooleanUtils.isTrue(this.validPIBuyBondSNETF())
				&& BooleanUtils.isTrue(this.validBondOBUBuy())) {
			if(BooleanUtils.isTrue(isChkPC)) {	
				//由商品查詢進入，不需檢查，已於SQL濾掉；由下單進入才需檢查
				if(BooleanUtils.isTrue(this.validCustProdRisk())) {
					this.validCustomizedProd();
				}
			}				
		}
		return outputVO;
	}
	
	/**
	 * SI商品資料適配檢核
	 * validProdRiskCate：商品風險屬性檢核
	 * validPIBuyETFSI：非專業投資人不得購買此商品
	 * validPIBuyCorp：專投法人不可申購此商品
	 * validProdQValue：商品與客戶Q值檢核
	 * validCustAge：債券年期&客戶年齡檢核
	 * validStackholder：利害關係人檢核
	 * validCustProdRisk：商品風險等級與客戶風險等級檢核
	 * validCustomizedProd：客製化商品無法申購
	 * validBondOBUBuy：OBU客戶無法申購此商品/限OBU客戶申購
	 * @param piBuy：商品專投申購
	 * @param riskCateId：商品風險屬性
	 * @param isChkPC：是否做商品與客戶風險等級檢核
	 * @return
	 * @throws Exception
	 */
	public ProdFitnessOutputVO validProdSI(Map<String, Object> product, Boolean isChkPC) throws Exception {
		outputVO = new ProdFitnessOutputVO();
		
		this.setProdId((String) product.get("PRD_ID"));
		this.setRiskCateID((String) product.get("RISKCATE_ID"));
		//this.setProdMaturityDate((Date) product.get("MATURITY_DATE"));
		this.setProdMaturityDate((BigDecimal) product.get("DATE_OF_MATURITY"));
		this.setPiBuy(ObjectUtils.toString(product.get("PI_BUY")));
		this.setProdQValue((String) product.get("GLCODE"));
		this.setProdStakeholder((String) product.get("STAKEHOLDER"));
		this.setProdOBUBuy((String) product.get("OBU_BUY_2"));
		this.setHNWC_BUY((String) product.get("HNWC_BUY")); 
		
		if(BooleanUtils.isTrue(this.validProdRiskCate())
//				&& BooleanUtils.isTrue(this.validCustAge())
				&& BooleanUtils.isTrue(this.validHNWCBuy())
				&& BooleanUtils.isTrue(this.validPIBuySI())
				&& BooleanUtils.isTrue(this.validPIBuyCorp())
				&& BooleanUtils.isTrue(needCheckQValue("SI") ? this.validProdQValue() : true)
				&& BooleanUtils.isTrue(this.validStackholder())
				&& BooleanUtils.isTrue(this.validBondOBUBuy())) {
			if(BooleanUtils.isTrue(isChkPC)) {	
				//由商品查詢進入，不需檢查，已於SQL濾掉；由下單進入才需檢查
				if(BooleanUtils.isTrue(this.validCustProdRisk())) {
					this.validCustomizedProd();
				}
			}				
		}
		return outputVO;
	}
	
	/*
	 * QVALUE是否需要檢核
	 */
	private Boolean needCheckQValue(String type) throws DAOException, JBranchException {
		
		List<Map<String,Object>> resultList = null;
		QueryConditionIF qc = null;
		StringBuilder sb = new StringBuilder();
		dam = this.getDataAccessManager();
		qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		sb.append("select PARAM_NAME from TBSYSPARAMETER ");
		sb.append("where PARAM_TYPE = :PARAM_TYPE and PARAM_CODE = :PARAM_CODE ");
		qc.setObject("PARAM_TYPE", "QVALUE_NEEDCHECK");
		qc.setObject("PARAM_CODE", type);
		qc.setQueryString(sb.toString());
		resultList = dam.exeQuery(qc);
		String QVALUE_NEEDCHECK = "";
		try {
			QVALUE_NEEDCHECK = resultList.get(0).get("PARAM_NAME").toString();
		} catch (Exception e) {
			QVALUE_NEEDCHECK = "";
		}
		return StringUtils.equals("Y", QVALUE_NEEDCHECK);
	}

	/**
	 * 商品風險等級與客戶風險等級檢核
	 * @return
	 * @throws Exception 
	 */
	private Boolean validCustProdRisk() throws Exception {
		String custRiskID = this.getCustVO().getRiskAtr();
		boolean hnwcCust = false; //高資產客戶
		boolean hnwcSPCust = false; //高資產客戶且為特定客戶
		
		//越級適配檢核
		if(!StringUtils.equals("M", this.getTrustTS()) && //非金錢信託下單
				this.getCustVO().getHNWCDataVO() != null && 
				this.getCustVO().getHNWCDataVO().getValidHnwcYN().matches("Y") && 
				this.getCustVO().getHNWCDataVO().getHnwcService().matches("Y") && //高資產客戶可越級適配
				(StringUtils.isBlank(this.getDynamicType()) || StringUtils.equals("Y", this.getFromPRD111YN())) ) { //動態鎖利下單不可越級適配；動態鎖利適配可越級
			hnwcCust = true; //高資產客戶
			custRiskID = this.getCustVO().getRiskAtr(); 
			
			if(!StringUtils.equals("Y", this.getCustVO().getHNWCDataVO().getSpFlag())) {
				//高資產客戶且非特定客戶且非C4只能越一級
				if(!StringUtils.equals("C4", this.getCustVO().getRiskAtr())) {
					custRiskID = "C" + ObjectUtils.toString((Integer.parseInt(this.getCustVO().getRiskAtr().substring(1)) + 1));
				}
			} else {
				hnwcSPCust = true;
			}
		}
				
		if(StringUtils.isNotBlank(this.getRiskCateID()) && !this.getCustProdRiskMapping(custRiskID).contains(this.getRiskCateID())) {
			if(hnwcSPCust) { 
				//高資產客戶且為特定客戶
				outputVO.setErrorID("高資產弱勢客戶不得申購越級商品");
			} else if(hnwcCust) {
				//高資產客戶
				outputVO.setErrorID("高資產客戶申購越級商品限越一級");
			} else {
				//商品風險等級與客戶風險等級不符
				outputVO.setErrorID("ehl_01_SOT_010");
			}
		}
		
		return (outputVO.getIsError() ? Boolean.FALSE : Boolean.TRUE);
	}
	
	/**
	 * 客戶風險屬性檢核
	 * 無客戶風險屬性，無法適配
	 * @return
	 * @throws JBranchException 
	 * @throws DAOException 
	 * @throws ParseException 
	 */
	private Boolean validCustRiskAttr() throws DAOException, JBranchException, ParseException {
		SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat ft = new SimpleDateFormat("yyyyMMdd");
		Date nowDate = ft.parse(cbsservice.getCBSTestDate().substring(0, 8));
		String kycDueDate = null;
		if (this.getCustVO().getKycDueDate() != null) {
			kycDueDate = sdFormat.format(this.getCustVO().getKycDueDate()); 
		}		
		
		if (StringUtils.isBlank(this.getCustVO().getRiskAtr()) 
				|| this.getCustVO().getKycDueDate() == null
				|| nowDate.after(java.sql.Date.valueOf(kycDueDate))) {
			//無客戶風險屬性或已逾期，無法適配
			outputVO.setErrorID("ehl_01_SOT702_009");
		}

		return (outputVO.getIsError() ? Boolean.FALSE : Boolean.TRUE);
	}
	
	/***
	 * 客戶FATCA註記檢核
	 * N 已有不合作帳戶註記，不得承作新商品
	 * Y 屬受限制之外國金融機構，不得新承作商品
	 * X 未簽署協議之外國金融機構，不得承作新商品
	 * 
	 * #1981 不合作例外帳戶例外管理  2024.11.28  Sam Tu
	 * @return
	 * @throws Exception 
	 */
	private Boolean validCustFATCA() throws Exception {
		String facaType = this.getCustVO().getW8BenDataVO().getFatcaType();
		
		if (StringUtils.equals("N", facaType)) {			
			//已有不合作帳戶註記，不得承作新商品
			outputVO.setErrorID("ehl_01_SOT702_011");
		} else if (StringUtils.equals("Y", facaType)) {		
			//屬受限制之外國金融機構，不得新承作商品
			outputVO.setErrorID("ehl_01_SOT702_025");
		} else if (StringUtils.equals("X", facaType)) {		
			//未簽署協議之外國金融機構，不得承作新商品
			outputVO.setErrorID("ehl_01_SOT702_013");
		}
		
		return (outputVO.getIsError() ? Boolean.FALSE : Boolean.TRUE);
	}
	
	/***
	 * 特定客戶取得專投身分未滿2週不得購買此商品
	 * 非專業投資人不得購買此商品
	 * @return
	 * @throws Exception 
	 */
	private Boolean validPIBuySI() throws Exception {
		FP032675DataVO fp032675 = this.getCustVO().getFp032675DataVO();
		
		if (StringUtils.equals("Y", this.getPiBuy()) 
				&& StringUtils.equals("Y", fp032675.getCustProFlag()) 
				&& StringUtils.equals("Y", fp032675.getInvestType()) 
				&& !StringUtils.equals("Y", fp032675.getInvestDue())) {
			//特定客戶取得專投身分未滿2週不得購買此商品
			outputVO.setErrorID("ehl_01_SOT704_001");
		} else if (StringUtils.equals("Y", this.getPiBuy()) 
				&& !StringUtils.equals("Y", fp032675.getCustProFlag())) {
			//非專業投資人不得購買此商品
			outputVO.setErrorID("ehl_01_SOT704_002");
		}

		return (outputVO.getIsError() ? Boolean.FALSE : Boolean.TRUE);
	}	
	
	/***
	 * 特定客戶取得專投身分未滿2週不得購買此商品
	 * 非專業投資人且非高資產客戶不得購買此商品
	 * @return
	 * @throws Exception 
	 */
	private Boolean validPIBuyBondSNETF() throws Exception {
		FP032675DataVO fp032675 = this.getCustVO().getFp032675DataVO();
		
		if (!StringUtils.equals("Y", this.getCustVO().getHNWCDataVO().getValidHnwcYN()) //高資產客戶可免兩週控管
				&& StringUtils.equals("Y", this.getPiBuy()) 
				&& StringUtils.equals("Y", fp032675.getCustProFlag()) 
				&& StringUtils.equals("Y", fp032675.getInvestType()) 
				&& !StringUtils.equals("Y", fp032675.getInvestDue())) {
			//特定客戶取得專投身分未滿2週不得購買此商品
			outputVO.setErrorID("ehl_01_SOT704_001");
		} else if (StringUtils.equals("Y", this.getPiBuy()) 
				&& !StringUtils.equals("Y", fp032675.getCustProFlag())
				&& !StringUtils.equals("Y", this.getCustVO().getHNWCDataVO().getValidHnwcYN())) {
			//非專業投資人不得購買此商品
			outputVO.setErrorID("ehl_01_SOT704_002");
		}

		return (outputVO.getIsError() ? Boolean.FALSE : Boolean.TRUE);
	}	
	
	/**
	 * 專投法人且註記為31,32，不可申購限專投商品
	 * 專投法人不可申購此商品
	 * @return
	 * @throws Exception
	 */
	private Boolean validPIBuyCorp() throws Exception {
		FP032675DataVO fp032675 = this.getCustVO().getFp032675DataVO();
		//是否為法人
		boolean isCorp = ((this.getCustID().length() >= 8 && this.getCustID().length() < 10) ? true : false);
				
		if (isCorp && StringUtils.equals("Y", fp032675.getCustProFlag())
				&& StringUtils.equals("Y", this.getPiBuy()) 				 
				&& StringUtils.equals("Y", fp032675.getTrustProCorp())) {
			//專投法人註記為31,32，不可申購此商品
			outputVO.setErrorID("ehl_01_SOT706_010");
		}

		return (outputVO.getIsError() ? Boolean.FALSE : Boolean.TRUE);
	}	
	
	/**
	 * 商品風險屬性檢核
	 * 商品風險 {0} 不符
	 * @return
	 */
	private Boolean validProdRiskCate() {
		if (StringUtils.isBlank(this.getRiskCateID())) {
			//該商品無風險屬性，無法適配
			outputVO.setErrorID("ehl_02_common_008");
		} else if (!this.getRiskCateID().matches("P1|P2|P3|P4")) {
			//商品風險 {0} 不符
			outputVO.setErrorID("ehl_01_SOT702_015");
		}

		return (outputVO.getIsError() ? Boolean.FALSE : Boolean.TRUE);
	}

	/**
	 * 商品與客戶Q值檢核
	 * @return
	 * @throws Exception
	 */
	private Boolean validProdQValue() throws Exception {
		BigDecimal prodQ = new BigDecimal("0");
		BigDecimal custQ = new BigDecimal("0");
		
		if(StringUtils.isNotBlank(this.getProdQValue()) && StringUtils.length(this.getProdQValue().trim()) >= 2) {
			prodQ = new BigDecimal(StringUtils.substring(this.getProdQValue(), 1));
		}
		
		if(StringUtils.isNotBlank(this.getCustVO().getqValue()) && StringUtils.length(this.getCustVO().getqValue().trim()) >= 2) {
			custQ = new BigDecimal(StringUtils.substring(this.getCustVO().getqValue(), 1));
		}
		
		if(!StringUtils.equals("Y", this.getCustVO().getFp032675DataVO().getCustProFlag()) 
				&& prodQ.compareTo(custQ) > 0) {
			//商品Q值不可大於客戶Q值
			outputVO.setErrorID("ehl_01_SOT706_003");
		}
		
		return (outputVO.getIsError() ? Boolean.FALSE : Boolean.TRUE);
	}
	
	/**
	 * 利害關係人檢核
	 * 本商品未開放利害關係人交易
	 * 法人戶未開放利害關係人交易
	 * @return
	 * @throws Exception
	 */
	private Boolean validStackholder() throws Exception {
		if(this.getCustVO().getIsStakeholder() 
				&& !StringUtils.equals("Y", this.getProdStakeholder())) {
			if(this.getCustVO().getCustType() == CustType.PERSON) {
				//本商品未開放利害關係人交易
				outputVO.setErrorID("ehl_01_SOT706_004");
			} else {
				//法人戶未開放利害關係人交易
				outputVO.setErrorID("ehl_01_SOT706_005");
			}
		}
			
		return (outputVO.getIsError() ? Boolean.FALSE : Boolean.TRUE);
	}
	
	/**
	 * 債券年期&客戶年齡檢核
	 * 專業投資人客戶年齡大於等於80歲，需特案核可購買境外結構型商品、組合式商品
	 * 一般客戶年齡大於等於65歲，需特案核可購買境外結構型商品、組合式商品
	 * @return
	 * @throws Exception
	 */
	public ProdFitnessOutputVO validCustAge() throws Exception  {
		outputVO = new ProdFitnessOutputVO();
		
		if(this.getProdMaturityDate()==null) return outputVO;
		if(this.getProdMaturityDate().compareTo(BigDecimal.ONE) >= 1
			&& this.getCustVO().getAge().compareTo(new BigDecimal(80)) >= 0
			&& StringUtils.equals("Y", this.getCustVO().getFp032675DataVO().getCustProFlag())) {
			//專業投資人客戶年齡大於等於80歲，需特案核可購買境外結構型商品、組合式商品
			outputVO.setWarningMsg(xmlinfo.getErrorMsg("ehl_01_SOT706_001"));
		} else if(this.getProdMaturityDate().compareTo(BigDecimal.ONE) >= 1
				&& this.getCustVO().getAge().compareTo(new BigDecimal(65)) >= 0
				&& !StringUtils.equals("Y", this.getCustVO().getFp032675DataVO().getCustProFlag())) {
			//客戶年齡大於等於65歲，需特案核可購買境外結構型商品、組合式商品
			outputVO.setWarningMsg(xmlinfo.getErrorMsg("ehl_01_SOT706_002"));
		}
		
		return outputVO;
	}
	
	/**
	 * 金錢信託債券年期&客戶年齡檢核
	 * 滿80歲(含)以上之專投自然人不得申購一年期以上SN商品
	 * 65歲(含)以上非專投自然人不得申購一年期以上SN商品
	 * @return
	 * @throws Exception
	 */
	public ProdFitnessOutputVO validSNCustAgeM() throws Exception  {
		outputVO = new ProdFitnessOutputVO();
		
		if(this.getProdMaturityDate().compareTo(BigDecimal.ONE) >= 1
			&& this.getCustVO().getAge().compareTo(new BigDecimal(80)) >= 0
			&& StringUtils.equals("Y", this.getCustVO().getFp032675DataVO().getCustProFlag())) {
			//滿80歲(含)以上之專投自然人不得申購一年期以上SN商品
			outputVO.setErrorID("ehl_01_SOT706_011");
		} else if(this.getProdMaturityDate().compareTo(BigDecimal.ONE) >= 1
				&& this.getCustVO().getAge().compareTo(new BigDecimal(65)) >= 0
				&& !StringUtils.equals("Y", this.getCustVO().getFp032675DataVO().getCustProFlag())) {
			//65歲(含)以上非專投自然人不得申購一年期以上SN商品
			outputVO.setErrorID("ehl_01_SOT706_012");
		}
		
		return outputVO;
	}
	
	/**
	 * 年限檢測(N/A)無法判斷，無法適配
	 * @return
	 * @throws Exception
	 */
	private Boolean validMaturityDate() throws Exception  {
		if(this.getProdMaturityDate() == null) {
			//年限檢測(N/A)無法判斷，無法適配
			outputVO.setErrorID("ehl_01_SOT706_007");
		}
		
		return (outputVO.getIsError() ? Boolean.FALSE : Boolean.TRUE);
	}
	
	/**
	 * 是否可申購
	 * 有OBU_PROD商品註記，只有OBU客戶可申購
	 * @return
	 * @throws Exception
	 */
	private Boolean validOBUProd() throws Exception  {
		if(StringUtils.equals("Y", this.getProdOBU()) && !StringUtils.equals("Y", this.getCustVO().getFp032675DataVO().getObuFlag())) {
			//未核備或停止申購
			outputVO.setErrorID("ehl_01_SOT702_001");
		}
		
		return (outputVO.getIsError() ? Boolean.FALSE : Boolean.TRUE);
	}
	
	/**
	 * 商品OBU申購註記檢核
	 * OBU客戶無法申購此商品
	 * @return
	 * @throws Exception
	 */
	private Boolean validOBUBuy() throws Exception  {
		if(StringUtils.equals("Y", this.getCustVO().getFp032675DataVO().getObuFlag()) && StringUtils.equals("N", this.getProdOBUBuy())) {
			//OBU客戶無法申購此商品
			outputVO.setErrorID("ehl_01_SOT702_003");
		}
		
		return (outputVO.getIsError() ? Boolean.FALSE : Boolean.TRUE);
	}
	
	/**
	 * 海外債商品OBU申購註記檢核
	 * OBU客戶無法申購此商品/限OBU客戶申購
	 * @return
	 * @throws Exception
	 */
	private Boolean validBondOBUBuy() throws Exception  {
		if(StringUtils.equals("Y", this.getCustVO().getFp032675DataVO().getObuFlag()) 
				&& StringUtils.equals("D", this.getProdOBUBuy())) {
			//OBU客戶無法申購此商品
			outputVO.setErrorID("ehl_01_SOT702_003");
		} else if(!StringUtils.equals("Y", this.getCustVO().getFp032675DataVO().getObuFlag()) 
				&& StringUtils.equals("O", this.getProdOBUBuy())) {
			//限OBU客戶申購
			outputVO.setErrorID("ehl_01_SOT706_009");
		}
		
		return (outputVO.getIsError() ? Boolean.FALSE : Boolean.TRUE);
	}
	
	/**
	 * 是否可承作台幣信託 (由下單進入才需檢核)
	 * @return
	 * @throws Exception
	 */
	private Boolean validTrustTypeTWD() throws Exception  {
		//若為台幣信託，且商品申購台幣信託註記為"N"(不可申購)
		if(StringUtils.equals("N", this.getTrustCurrType()) && StringUtils.equals("N", this.getProdBuyTWD())) {
			//不可承作台幣信託
			outputVO.setErrorID("ehl_01_SOT702_004");
		}
		
		return (outputVO.getIsError() ? Boolean.FALSE : Boolean.TRUE);
	}
	
	/**
	 * 商品申購額度控管檢核 (Warning)
	 * 本次交易中有基金申購額度控管，交易是否成功依鍵入作業系統時之剩餘額度而定。
	 * @return
	 * @throws Exception
	 */
	private Boolean validProdQuotas() throws Exception  {
		if(this.getProdQuotas().compareTo(BigDecimal.ZERO) == 1) {
			//商品申購額度控管金額需>0
			outputVO.setWarningMsg(xmlinfo.getErrorMsg("ehl_01_SOT702_005"));
		}
		
		return Boolean.TRUE;
	}
	
	/**
	 * 基金報酬揭露檢核
	 * @return
	 * @throws Exception
	 */
	private Boolean validProdExpoFlag() throws Exception  {
		//通路報酬揭露覆核過後，狀態為2
		if(!StringUtils.equals("2", this.getProdExpoFlag())) {
			//基金報酬揭露資訊尚未覆核
			outputVO.setErrorID("ehl_01_SOT702_006");
		}
		
		return (outputVO.getIsError() ? Boolean.FALSE : Boolean.TRUE);
	}
	
	/**
	 * 高收益基金檢核 (Warning)
	 * 請提醒客戶: 1.本類基金主要係投資於非投資等級之高風險債券且配息來源可能為本金，投資本類基金不宜占投資組合過高的比重。
	 * 2.屬退休人士之委託人需確認已充分了解本類基金之各項投資風險。
	 * 3.本類基金可能投資於符合美國Rule 144A規定具有私募性質之債券，雖其投資總金額不得超過基金淨資產價值之百分之十，惟本類基金之相關風險，均由委託人自行承擔。
	 * @return
	 * @throws Exception
	 */
	private Boolean validHighYield() throws Exception  {
		if(StringUtils.equals("Y", this.getProdHighYield())) {
			outputVO.setWarningMsg(xmlinfo.getErrorMsg("ehl_01_SOT702_007"));
		}
		
		return Boolean.TRUE;
	}
	
	/**
	 * OBU客戶註記檢核
	 * 非專業投資人不得購買此商品
	 * @return
	 * @throws Exception
	 */
	private Boolean validOBUPI() throws Exception  {
		if(StringUtils.equals("Y", this.getProdOBUPro()) && 
				StringUtils.equals("Y", this.getCustVO().getFp032675DataVO().getObuFlag()) &&
				!StringUtils.equals("Y", this.getCustVO().getFp032675DataVO().getCustProFlag())) {
			//非專業投資人不得購買此商品
			outputVO.setErrorID("ehl_01_SOT702_010");
		}
		
		return (outputVO.getIsError() ? Boolean.FALSE : Boolean.TRUE);
	}
	
	/**
	 * OBU商品年限檢核
	 * OBU客戶年齡限制
	 * @return
	 * @throws Exception
	 */
	private Boolean validOBUAge() throws Exception  {
		if(StringUtils.equals("Y", this.getProdOBUAge()) &&
				StringUtils.equals("Y", this.getCustVO().getFp032675DataVO().getObuFlag()) &&
				!StringUtils.equals("Y", this.getCustVO().getFp032675DataVO().getAgeUnder70Flag())) { //AgeUnder70Flag內容已改為65歲以下
			//年限檢測不適;年限檢測:客戶年齡超過65歲，不可購買此商品
			outputVO.setErrorID("ehl_01_SOT702_014");
		}
		
		return (outputVO.getIsError() ? Boolean.FALSE : Boolean.TRUE);
	}
	
	/**
	 * 商品建議售出註記檢核 (Warning)
	 * 此基金為本行建議售出之標的，請提醒客戶並確認其申購意願，謝謝！
	 * @return
	 * @throws Exception
	 */
	private Boolean validSugSelling() throws Exception  {
		if(StringUtils.equals("Y", this.getProdSugSelling())) {
			//此基金為本行建議售出之標的，請提醒客戶並確認其申購意願，謝謝！
			outputVO.setWarningMsg(xmlinfo.getErrorMsg("ehl_01_SOT702_016"));
		}
		
		return Boolean.TRUE;
	}
	
	/**
	 * 商品未核備檢核
	 * OBU 可以買未核備, DBU不能買未核備商品
	 * 境外私募基金不檢核
	 * @return
	 * @throws Exception
	 */
	private Boolean validFus40() throws Exception  {
		if(!StringUtils.equals("Y", this.getOVS_PRIVATE_YN()) && //不為境外私募基金
				StringUtils.equals("Y", this.getProdFus40()) && 
				!StringUtils.equals("Y", this.getCustVO().getFp032675DataVO().getObuFlag())) {
			//OBU 可以買未核備, DBU不能買未核備商品
			outputVO.setErrorID("ehl_01_SOT702_017");
		}
		
		return (outputVO.getIsError() ? Boolean.FALSE : Boolean.TRUE);
	}
	
	/**
	 * 轉換必需為同系列商品
	 * @return
	 * @throws Exception
	 */
	private Boolean validSameSerial() throws Exception  {
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> sameSerialProdId_map = xmlInfo.doGetVariable("SOT.SAME_SERIAL_FUND", FormatHelper.FORMAT_3);
		String sameSerialProdId = null;
		if (StringUtils.isNotBlank(StringUtils.substring(this.getSameSerialProdId(), 0, 2))) {
			sameSerialProdId = sameSerialProdId_map.get(StringUtils.substring(this.getSameSerialProdId(), 0, 2));//帶入轉出標的前兩碼
		}		
		
		if(StringUtils.isNotBlank(this.getDynamicType())) {
			//動態鎖利
			if(StringUtils.equals("Y", this.getSameSerialYN()) 
					&& !(StringUtils.equals(StringUtils.substring(this.getProdId(), 0, 2), StringUtils.substring(this.getSameSerialProdId(), 0, 2))
							|| StringUtils.equals(StringUtils.substring(this.getProdId(), 0, 2), sameSerialProdId))) {
				//動態鎖利子基金須為母基金同系列商品
				outputVO.setErrorID("動態鎖利子基金須為母基金同系列商品");
			}
			if(StringUtils.equals("C", this.getDynamicType()) && !StringUtils.equals(this.getProdCurr(), this.getDynamicProdCurrM())) {
				//動態鎖利子基金須為母基金同計價幣別
				outputVO.setErrorID("動態鎖利子基金須與母基金相同計價幣別");
			}
			if(StringUtils.isNotBlank(this.getInProdIdM()) && !StringUtils.equals(this.getProdCurr(), this.getDynamicProdCurrM())) {
				//動態鎖利子基金須為母基金同計價幣別
				outputVO.setErrorID("轉入母基金須與轉出母基金相同計價幣別");
			}
		} else {
			//一般基金轉換
			if(StringUtils.equals("Y", this.getSameSerialYN()) 
					&& !(StringUtils.equals(StringUtils.substring(this.getProdId(), 0, 2), StringUtils.substring(this.getSameSerialProdId(), 0, 2))
							|| StringUtils.equals(StringUtils.substring(this.getProdId(), 0, 2), sameSerialProdId))) {
				//轉換必需為同系列商品
				outputVO.setErrorID("ehl_01_SOT702_018");
			}
		}
		
		return (outputVO.getIsError() ? Boolean.FALSE : Boolean.TRUE);
	}
	
	/**
	 * 客製化商品檢核
	 * 客製化商品無法申購
	 * @return
	 * @throws Exception
	 */
	private Boolean validCustomizedProd() throws Exception {
		
		//客製化商品不可申購
		if(!isInCustomized()) outputVO.setErrorID("ehl_01_SOT706_008");
		
		return (outputVO.getIsError() ? Boolean.FALSE : Boolean.TRUE);
	}
	
	/**
	 * 若PM有設定設客製化商品，需檢核：
	 * 若有設定分行：須檢核該商品，客戶所屬分行有在設定分行內
	 * 若有設定客戶ID：須檢核該商品，客戶ID為設定客戶ID
	 * @return
	 */
	private boolean isInCustomized(){
		List<Map<String,Object>> recList = null;
		QueryConditionIF qc = null;
		StringBuilder sb = new StringBuilder();
		try{
//			String loginbrh = (String) SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINBRH);
			dam = this.getDataAccessManager();
			qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			
			sb.append("select * from TBPRD_CUSTOMIZED ");
			sb.append("where PRD_ID = :prodID and REVIEW_STATUS = 'Y' ");
			qc.setObject("prodID", this.getProdId());
			qc.setQueryString(sb.toString());
			recList = dam.exeQuery(qc);
			
			if(CollectionUtils.isEmpty(recList)) {
				return true;
			} else {
				for(Map<String,Object> map : recList){
					
					if( StringUtils.isBlank(map.get("BRANCH_NBR").toString()) 
							&& StringUtils.isBlank(map.get("CUST_ID").toString()))
						 return true;
					
					if( StringUtils.isBlank(map.get("BRANCH_NBR").toString()) 
							&& (StringUtils.equals(map.get("CUST_ID").toString(), this.getCustID()) 
									|| StringUtils.equals("ALL", map.get("CUST_ID").toString().trim())))
						 return true;
					
					if( StringUtils.equals(map.get("BRANCH_NBR").toString(), this.getCustVO().getBranchNbr()) 
							&& StringUtils.isBlank(map.get("CUST_ID").toString()))
						 return true;
					
					if( StringUtils.equals(map.get("BRANCH_NBR").toString(), this.getCustVO().getBranchNbr()) 
							&& (StringUtils.equals(map.get("CUST_ID").toString(), this.getCustID()) 
									|| StringUtils.equals("ALL", map.get("CUST_ID").toString().trim())))
						 return true;
					
				}
			}
		} catch(Exception  e) {
			logger.debug(e.getMessage(),e);
		}
		return false;
	}
	
	/**
	 * 新興市場之非投資等級債券基金檢核
	 * 65歲以上客戶除非有專投資格，否則不得申購新興市場之非投資等級債券基金
	 * @return
	 * @throws Exception
	 */
	private Boolean validNFS100() throws Exception  {
		if(StringUtils.equals("Y", this.getNFS100_YN()) &&
				!StringUtils.equals("Y", this.getCustVO().getFp032675DataVO().getAgeUnder70Flag()) && //AgeUnder70Flag內容已改為65歲以下
				!StringUtils.equals("Y", this.getCustVO().getFp032675DataVO().getCustProFlag())) {
			//65歲以上客戶除非有專投資格，否則不得申購新興市場之非投資等級債券基金
			outputVO.setErrorID("ehl_01_SOT702_019");
		}
		
		return (outputVO.getIsError() ? Boolean.FALSE : Boolean.TRUE);
	}
	
	/**
	 * 65歲以上客戶限專投ETF商品檢核
	 * 65歲以上客戶不得申購僅限專投客戶申購之ETF
	 * @return
	 * @throws Exception
	 */
	private Boolean validPIBuy65() throws Exception  {
		if(!StringUtils.equals("Y", this.getCustVO().getFp032675DataVO().getAgeUnder70Flag()) && //AgeUnder70Flag內容已改為65歲以下
				StringUtils.equals("Y", this.getPiBuy())) {
			//65歲以上客戶不得申購僅限專投客戶申購之ETF
			outputVO.setErrorID("ehl_01_SOT702_020");
		}
		
		return (outputVO.getIsError() ? Boolean.FALSE : Boolean.TRUE);
	}
	
	/**
	 * 債券年期&客戶年齡檢核
	 * 65歲以上客戶除非有專投資格，否則不得申購客戶年齡+海外債到期年限超過80以上之商品(取消)
	 * 65歲以上非專業投資人客戶禁止申購客戶年齡+海外債商品剩餘年限>=80，要出文字提醒”需有主管特案簽核”
	 * @return
	 * @throws Exception
	 */
	public Boolean validBondCustAge() throws Exception  {
		outputVO = new ProdFitnessOutputVO();
		
		//65歲以上客戶沒有專投資格
		if(!StringUtils.equals("Y", this.getCustVO().getFp032675DataVO().getAgeUnder70Flag()) && // AgeUnder70Flag內容已改為65歲以下
		   !StringUtils.equals("Y", this.getCustVO().getFp032675DataVO().getCustProFlag()) &&
		   !StringUtils.equals("Y", this.getCustVO().getHNWCDataVO().getValidHnwcYN())) {		 // 非高資產客戶
			if((this.getCustVO().getAge().add(this.getProdMaturityDate())).compareTo(new BigDecimal(80)) >= 0) {
				//客戶年齡+海外債到期年限超過80以上
				//65歲以上客戶除非有專投資格，否則不得申購客戶年齡+海外債到期年限超過80以上之商品(取消)
				outputVO.setWarningMsg(xmlinfo.getErrorMsg("ehl_01_SOT702_022")); //需有主管特案簽核
			}
		}
		
		return Boolean.TRUE;
	}
	
	/***
	 * 限高資產客戶申購
	 * 客戶非高資產客戶，不得申購僅限高資產客戶申購之商品
	 * 高資產客戶未過期中評估
	 * @return
	 * @throws Exception
	 */
	private Boolean validHNWCBuy() throws Exception {
		CustHighNetWorthDataVO hnwcData = this.getCustVO().getHNWCDataVO();
		
		if (StringUtils.equals("Y", this.getHNWC_BUY())) { //限高資產申購
			if(!StringUtils.equals("Y", hnwcData.getValidHnwcYN())) { //沒有高資產註記客戶
				//客戶非高資產客戶，不得申購僅限高資產客戶申購之商品
				outputVO.setErrorID("ehl_01_SOT702_023");
			} else if(!StringUtils.equals("Y", hnwcData.getHnwcService())) { //沒有提供高資產商品或服務
				//高資產客戶未過期中評估
				outputVO.setErrorID("ehl_01_SOT702_024");
			} else if(StringUtils.equals("M", this.getTrustTS())) { //金錢信託不可申購高資產商品
				outputVO.setErrorID("金錢信託不受理高資產商品投資");
			}
		}

		return (outputVO.getIsError() ? Boolean.FALSE : Boolean.TRUE);
	}
	
	/***
	 * 境外私募基金不在可交易期間中
	 * @return
	 * @throws Exception
	 */
	private Boolean validOvsPrivate() throws Exception  {
		if (StringUtils.equals("Y", this.getOVS_PRIVATE_YN()) && !isInOvsPriDate(this.getPrdID())) {
			//境外私募基金不在可交易期間中
			outputVO.setErrorID("境外私募基金不在可交易期間中");
		}
		
		return (outputVO.getIsError() ? Boolean.FALSE : Boolean.TRUE);
	}
	
	/***
	 * 境外私募基金是否在可交易期間中
	 * ture: YES
	 * false: NO
	 * @return
	 * @throws DAOException
	 * @throws JBranchException
	 */
	private Boolean isInOvsPriDate(String prdId) throws DAOException, JBranchException {
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		
		sb.append("SELECT 1 FROM TBPRD_FUND_OVS_PRIVATE ");
		sb.append(" WHERE PRD_ID = :prdId AND TRADE_TYPE = '1' "); //申購
		sb.append(" AND TRUNC(SYSDATE) BETWEEN TRUNC(START_DATE) AND TRUNC(END_DATE) ");
		qc.setObject("prdId", prdId);
		qc.setQueryString(sb.toString());
		List<Map<String,Object>> list = dam.exeQuery(qc);
		
		return CollectionUtils.isNotEmpty(list) ? true : false;
	}
	
	/***
	 * #2039：WMS-CR-20240606-02_境外私募基金新增銷售客群：
	 * 僅限「高資產客戶」、「專業機構投資人」與「高淨值投資法人」申購境外私募基金。
	 * 若非上述三客群申購則顯示錯誤訊息「客戶非高資產客戶、專業機構投資人或高淨值投資法人，不得申購境外私募基金」
	 * 
	 * (基金只有境外私募基金，HNWC_BUY 會放Y)
	 * @return
	 * @throws Exception
	 */
	private Boolean validPrivatelyOfferedFund() throws Exception  {
		// 私募基金
		if (StringUtils.equals("Y", this.getOVS_PRIVATE_YN())) {
			if (!StringUtils.equals("Y", this.getCustVO().getFp032675DataVO().getProCorpInv()) &&
				!StringUtils.equals("Y", this.getCustVO().getFp032675DataVO().getProCorpInv2()) &&
			    !StringUtils.equals("Y", this.getCustVO().getFp032675DataVO().getHighYieldCorp()) &&
			    !(this.getCustVO().getHNWCDataVO() != null && 
			    this.getCustVO().getHNWCDataVO().getValidHnwcYN().matches("Y") && 
			    this.getCustVO().getHNWCDataVO().getHnwcService().matches("Y"))
			) {
				outputVO.setErrorID("客戶非高資產客戶、專業機構投資人或高淨值投資法人，不得申購境外私募基金");
			}
			
			// 限高資產申購
			if (StringUtils.equals("Y", this.getHNWC_BUY())) {
				// 金錢信託不可申購高資產商品
				if(StringUtils.equals("M", this.getTrustTS())) { 
					outputVO.setErrorID("金錢信託不受理高資產商品投資");
				}			
			}
		}
		return (outputVO.getIsError() ? Boolean.FALSE : Boolean.TRUE);
	} 
	
	/***
	 * 動態鎖利母子基金檢核
	 * @return
	 * @throws Exception
	 */
	private Boolean validDynamic() throws Exception  {
		if(StringUtils.isNotBlank(this.getDynamicType())) {
			if(StringUtils.equals("M", this.getDynamicType()) && !StringUtils.equals("Y", this.getDynamicM())) {
				//沒有動態鎖利母基金註記
				outputVO.setErrorID("此基金非動態鎖利母基金");
			} else if(StringUtils.equals("C", this.getDynamicType()) && !StringUtils.equals("Y", this.getDynamicC())) {
				//沒有動態鎖利子基金註記
				outputVO.setErrorID("此基金非動態鎖利子基金");
			}
		}
		
		return (outputVO.getIsError() ? Boolean.FALSE : Boolean.TRUE);
	}

	public ProdFitnessOutputVO validUSACustAndProd(String cust_id) throws DAOException, JBranchException, Exception {
		this.setCustID(cust_id);
		
		if(BooleanUtils.isTrue(this.validUSACustAndProd()))
			return new ProdFitnessOutputVO();
		else
			return outputVO;
	}

	private Boolean validUSACustAndProd()  throws Exception {
				String facaType = this.getCustVO().getFatcaDataVO().getFatcaType();
				
				if (StringUtils.equals("Z", facaType)) {			
					//客戶屬美國國籍或稅籍不受理交易申請
					outputVO.setErrorID("ehl_01_SOT702_012");
				}				
				return (outputVO.getIsError() ? Boolean.FALSE : Boolean.TRUE);
	}
	
}
