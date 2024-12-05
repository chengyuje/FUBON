package com.systex.jbranch.app.server.fps.crm829;

import static com.systex.jbranch.fubon.commons.esb.cons.EsbCrmCons.NMI001;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbCrmCons.NMI002;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbCrmCons.NMI003;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbCrmCons.NMP8YB;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.ETF_ASSETS;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.esb.EsbFmpJRunConfiguer;
import com.systex.jbranch.fubon.commons.esb.EsbUtil;
import com.systex.jbranch.fubon.commons.esb.EsbUtil3;
import com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons;
import com.systex.jbranch.fubon.commons.esb.vo.ESBUtilInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ESBUtilOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.TxHeadVO;
import com.systex.jbranch.fubon.commons.esb.vo.nmi001.NMI001;
import com.systex.jbranch.fubon.commons.esb.vo.nmi001.NMI001InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nmi001.NMI001OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nmi001.NMI001OutputVODetails;
import com.systex.jbranch.fubon.commons.esb.vo.nmi002.CustAssetPotInfo;
import com.systex.jbranch.fubon.commons.esb.vo.nmi002.NMI002InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nmi002.NMI002OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nmi002.NMI002OutputVODetails;
import com.systex.jbranch.fubon.commons.esb.vo.nmi003.NMI003;
import com.systex.jbranch.fubon.commons.esb.vo.nmi003.NMI003InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nmi003.NMI003OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nmi003.NMI003OutputVODetails;
import com.systex.jbranch.fubon.commons.esb.vo.nmp8yb.NMP8YBInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nmp8yb.NMP8YBOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nmp8yb.NMP8YBOutputVODetails;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;


@Component("crm829")
@Scope("request")
public class CRM829 extends EsbUtil {
	private String ESB_TYPE = EsbFmpJRunConfiguer.ESB_TYPE;
    private String thisClaz = this.getClass().getSimpleName() + ".";
    
    /**
     * 取得電文NMP8YB客戶奈米投資料
     * @param body CRM829InputVO
     * @param header
     * @throws Exception
     */
	public void getNanoAsset(Object body, IPrimitiveMap header) throws Exception {
		CRM829InputVO inputVO = (CRM829InputVO) body;
		CRM829OutputVO return_VO = new CRM829OutputVO();
		BigDecimal totalInvestmentTwd = BigDecimal.ZERO;	//總投資金額(台幣)
		BigDecimal totalMarketValueTwd = BigDecimal.ZERO;	//參考總市值(台幣)
	
		String custID = inputVO.getCust_id();
		//init
		String txtId =NMP8YB;			// 電文代碼
		String module = thisClaz+new Object(){}.getClass().getEnclosingMethod().getName();	// 模組代碼
		EsbUtil3 esbUtil3 = new EsbUtil3(module, this.getEsbUrl(ESB_TYPE), this.getEsbID(ESB_TYPE), this.getEsbSeq());
        //head
		String txHeadXml = esbUtil3.getTxHead(txtId, null); 

        //body
		Map<String, String> txBody = new HashMap<String, String>();
		txBody.put("FUNC", "1");
		txBody.put("CustId", custID);	
		String txBodyXml = esbUtil3.getTxBody(txBody);


        try {
    		//發送電文
    		List<String> dnXmls = esbUtil3.send(txHeadXml + txBodyXml);
	
	        NMP8YBOutputVO nmp8ybOutputVO = new NMP8YBOutputVO();
	        List<CustAssetNano> results = new ArrayList<CustAssetNano>();
	        List<CustAssetNano> sellList = new ArrayList<CustAssetNano>();
	        
	        //發送電文
	        List<NMI001> nmi001 = this.getNMI001(custID);
	        
	        for(String dnXml : dnXmls) {
	        	String txBodyStr = esbUtil3.getTagValue(dnXml, "TxBody");
            	List<String> txRepeats = esbUtil3.getTagValueList(txBodyStr, "TxRepeat");
	        	for(String txRepeat : txRepeats) {
	        		CustAssetNano asset = new CustAssetNano();
	        		
	        		//若契約尚未終止才顯示
	        		String PotId = esbUtil3.getTagClearValue(txRepeat, "PotId");
	        		if(isContractActive(PotId.trim())) {
		        		BigDecimal investmentTwd = esbUtil3.decimalPoint(esbUtil3.getTagClearValue(txRepeat, "IncreaseAmtTwd"), 0);	//投資金額(台幣)
		        		BigDecimal marketValueTwd = esbUtil3.decimalPoint(esbUtil3.getTagClearValue(txRepeat, "MarketValTwd"), 0);	//參考市值(台幣)
		        		totalInvestmentTwd = totalInvestmentTwd.add(investmentTwd);
		        		totalMarketValueTwd = totalMarketValueTwd.add(marketValueTwd);
		        		
		        		asset.setPotId(esbUtil3.getTagClearValue(txRepeat, "PotId").trim());
		        		asset.setPlanName(esbUtil3.getTagClearValue(txRepeat, "PlanName").trim());
		        		asset.setTargetAmt(new EsbUtil().decimalPoint(esbUtil3.getTagClearValue(txRepeat, "TargetAmt"), 2));
		        		asset.setIncreaseAmtBas(new EsbUtil().decimalPoint(esbUtil3.getTagClearValue(txRepeat, "IncreaseAmtBas"), 2));
		        		asset.setIncreaseCharge(new EsbUtil().decimalPoint(esbUtil3.getTagClearValue(txRepeat, "IncreaseCharge"), 2));
		        		asset.setMarketValBas(new EsbUtil().decimalPoint(esbUtil3.getTagClearValue(txRepeat, "MarketValBas"), 2));
		        		asset.setSignDigitBas(esbUtil3.getTagClearValue(txRepeat, "SignDigitBas").trim());
		        		asset.setProfitAndLossBas(new EsbUtil().decimalPoint(esbUtil3.getTagClearValue(txRepeat, "ProfitAndLossBas"), 2));
		        		asset.setRetrunBas(new EsbUtil().decimalPoint(esbUtil3.getTagClearValue(txRepeat, "RetrunBas"), 2));
		        		asset.setTargetRate(new EsbUtil().decimalPoint(esbUtil3.getTagClearValue(txRepeat, "TargetRate"), 2));
		        		asset.setCouponCode(esbUtil3.getTagClearValue(txRepeat, "CouponCode").trim());
		        		asset.setIncreaseAmtTwd(investmentTwd);
		        		asset.setMarketValTwd(marketValueTwd);
		        		asset.setSignDigitTwd(esbUtil3.getTagClearValue(txRepeat, "SignDigitTwd").trim());
		        		asset.setProfitAndLossTwd(new EsbUtil().decimalPoint(esbUtil3.getTagClearValue(txRepeat, "ProfitAndLossTwd"), 0));
		        		asset.setRetrunTwd(new EsbUtil().decimalPoint(esbUtil3.getTagClearValue(txRepeat, "RetrunTwd"), 2));
		        		asset.setTrustClosingDate(esbUtil3.getTagClearValue(txRepeat, "TrustClosingDate").trim());
		        		asset.setChgPurchase(esbUtil3.getTagClearValue(txRepeat, "ChgPurchase").trim());
		        		asset.setDivType(esbUtil3.getTagClearValue(txRepeat, "DivType").trim());
		        		asset.setDividendamount(new EsbUtil().decimalPoint(esbUtil3.getTagClearValue(txRepeat, "Dividendamount"), 2));
		        		asset.setSigncod(esbUtil3.getTagClearValue(txRepeat, "Signcod").trim());
		        		asset.setInterestrateofreturn(new EsbUtil().decimalPoint(esbUtil3.getTagClearValue(txRepeat, "Interestrateofreturn"), 2));
		        		
		        		try {
			        		//取得NMI002電文的TARGET, YEARLIMIT欄位，若有多筆只抓取第一筆資料
		        			if(!StringUtils.equals("Y", inputVO.getGetSumYN())) {	//資產總覽不需要打這個電文，投資分布明細資料才需要
				        		List<CustAssetPotInfo> nmi002 = getNanoAssetDetail(asset.getPotId());
				        		if(CollectionUtils.isNotEmpty(nmi002)) {
				        			asset.setTarget(nmi002.get(0).getTarget());
				        			asset.setYearLimit(nmi002.get(0).getYearLimit());
				        			asset.setStarDate(StringUtils.isEmpty(nmi002.get(0).getStarDate()) ? null: new SimpleDateFormat("yyyyMMdd").parse(nmi002.get(0).getStarDate()));
				        			asset.setType(nmi002.get(0).getType());
				        			asset.setAmt(nmi002.get(0).getAmt());
				        			asset.setRiskPref(nmi002.get(0).getRiskPref());
				        			asset.setChargeDate(nmi002.get(0).getChargeDate());
				        			asset.setStrategy(nmi002.get(0).getStrategy()); //1:奈米1號-全球ETF 2:奈米2號-台灣ETF
				        			asset.setCurrency(StringUtils.equals("2", asset.getStrategy()) ? "TWD" : "USD"); //奈米頭2號為台幣；1號為美金
				        			asset.setDivType(nmi002.get(0).getDivType()); 
				        			asset.setExchange(StringUtils.equals("Y", nmi002.get(0).getExchange()) ? nmi002.get(0).getExchange() : asset.getCurrency());//Y:自動換匯 USD:直接扣美金 TWD:直接扣台幣
				        			asset.setMaximumInvestmentAmount(nmi002.get(0).getMaximumInvestmentAmount());
				        			asset.setSelectedCards(nmi002.get(0).getSelectedCards());
				        			asset.setSelectedCardsName(getCardNames(nmi002.get(0).getSelectedCards()));
				        			asset.setCumulativeMultiple(nmi002.get(0).getCumulativeMultiple());
				        			asset.setIsCumulativeSwitchOn(nmi002.get(0).getIsCumulativeSwitchOn());
				        			asset.setCumulativeAmount(nmi002.get(0).getCumulativeAmount());	
				        			
				        			for (NMI001 vo : nmi001) {
				        				// 取得『贖回中』資料
				        				if (vo.getCardStatus() != null && vo.getCardStatus().equals("1") && 
				     		        		vo.getEviNum() != null && vo.getEviNum().equals(asset.getPotId())) {
				     		        		
				        					try {
							        			List<NMI003> nmi003 = this.getNMI003(asset.getPotId());
							        			CustAssetNano sell = null;
							        			List<String> potIdList = new ArrayList<String>();
							        			
							        			for (NMI003 nmi003VO : nmi003) {
							        				if (nmi003VO.getType() != null && nmi003VO.getType().equals("2")) {
							        					String potID = esbUtil3.getTagClearValue(txRepeat, "PodIt").trim();
							        					String modifyTime = nmi003VO.getModifyTime();
							        					if (potIdList.contains(potID)) {
							        						//【贖回在途】調整顯示判斷：同一契約編號贖回申請日僅抓最新的日期顯示即可。
							        						for (CustAssetNano nano : sellList) {
							        							if (nano.getPotId().equals(potID)) {
							        								String time = nano.getModifyTime();
							        								if (modifyTime.compareTo(time) > 0) {
							        									nano.setModifyTime(modifyTime);
							        									break;
							        								}							        								
							        							}
							        						}
							        					} else {
							        						sell = new CustAssetNano();
							        						sell.setModifyTime(modifyTime);
							        						sell.setPotId(potID);
							        						sell.setPlanName(esbUtil3.getTagClearValue(txRepeat, "PlanName").trim());
							        						sell.setStrategy(nmi002.get(0).getStrategy()); //1:奈米1號-全球ETF 2:奈米2號-台灣ETF
							        						sell.setRiskPref(nmi002.get(0).getRiskPref());
							        						sell.setCurrency(StringUtils.equals("2", asset.getStrategy()) ? "TWD" : "USD"); //奈米頭2號為台幣；1號為美金
							        						sell.setIncreaseAmtBas(new EsbUtil().decimalPoint(esbUtil3.getTagClearValue(txRepeat, "IncreaseAmtBas"), 2));
							        						sell.setIncreaseAmtTwd(investmentTwd);
							        						sell.setMarketValBas(new EsbUtil().decimalPoint(esbUtil3.getTagClearValue(txRepeat, "MarketValBas"), 2));
							        						sell.setMarketValTwd(marketValueTwd);
							        						sell.setSignDigitBas(esbUtil3.getTagClearValue(txRepeat, "SignDigitBas").trim());
							        						sell.setRetrunBas(new EsbUtil().decimalPoint(esbUtil3.getTagClearValue(txRepeat, "RetrunBas"), 2));
							        						sellList.add(sell);
							        						potIdList.add(potID);
							        					}
							        				}
							        			}
							        		} catch(Exception e) {
							        			return_VO.setErrorMsg(e.getMessage());
							        		}
				     		        	}
				        			}
					        	}
		        			}
		        		} catch(Exception e) {
		        			return_VO.setErrorMsg(e.getMessage());
		        		}
		        		results.add(asset);
	        		}
	        	}
	        }
	        return_VO.setTotalPlanNo(results.size());
	        return_VO.setResultList(results);
	        return_VO.setSellList(sellList);
	        
        } catch (Exception e) {
        	return_VO.setErrorMsg(e.getMessage());
        }
        
        return_VO.setTotalInvestmentTwd(totalInvestmentTwd);
        return_VO.setTotalMarketValueTwd(totalMarketValueTwd);
            
		this.sendRtnObject(return_VO);
	}

	public void getNanoAsset_old(Object body, IPrimitiveMap header) throws Exception {
		CRM829InputVO inputVO = (CRM829InputVO) body;
		CRM829OutputVO return_VO = new CRM829OutputVO();
		BigDecimal totalInvestmentTwd = BigDecimal.ZERO;	//總投資金額(台幣)
		BigDecimal totalMarketValueTwd = BigDecimal.ZERO;	//參考總市值(台幣)
	
		String custID = inputVO.getCust_id();
		
        String htxtid = NMP8YB;

        //init util
        ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, htxtid);
        esbUtilInputVO.setModule(thisClaz+new Object(){}.getClass().getEnclosingMethod().getName());

        //head
        TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
        txHead.setDefaultTxHead();
        esbUtilInputVO.setTxHeadVO(txHead);

        //body
        NMP8YBInputVO nmp8ybInputVO = new NMP8YBInputVO();
        nmp8ybInputVO.setFUNC("1");
        
        nmp8ybInputVO.setCustId(custID);        //客戶ID
        esbUtilInputVO.setNmp8ybInputVO(nmp8ybInputVO);

        try {
	        //發送電文
	        List<ESBUtilOutputVO> vos = send(esbUtilInputVO);
	
	        NMP8YBOutputVO nmp8ybOutputVO = new NMP8YBOutputVO();
	        List<CustAssetNano> results = new ArrayList<CustAssetNano>();
	        List<CustAssetNano> sellList = new ArrayList<CustAssetNano>();
	        
	        //發送電文
	        List<NMI001> nmi001 = this.getNMI001(custID);
	        
	        for(ESBUtilOutputVO esbUtilOutputVO : vos) {
	        	nmp8ybOutputVO = esbUtilOutputVO.getNmp8ybOutputVO();
	        	for(NMP8YBOutputVODetails detail : nmp8ybOutputVO.getDetails()) {
	        		CustAssetNano asset = new CustAssetNano();
	        		
	        		//若契約尚未終止才顯示
	        		if(isContractActive(detail.getPotId().trim())) {
		        		BigDecimal investmentTwd = new EsbUtil().decimalPoint(detail.getIncreaseAmtTwd(), 0);	//投資金額(台幣)
		        		BigDecimal marketValueTwd = new EsbUtil().decimalPoint(detail.getMarketValTwd(), 0);	//參考市值(台幣)
		        		totalInvestmentTwd = totalInvestmentTwd.add(investmentTwd);
		        		totalMarketValueTwd = totalMarketValueTwd.add(marketValueTwd);
		        		
		        		asset.setPotId(detail.getPotId().trim());
		        		asset.setPlanName(detail.getPlanName().trim());
		        		asset.setTargetAmt(new EsbUtil().decimalPoint(detail.getTargetAmt(), 2));
		        		asset.setIncreaseAmtBas(new EsbUtil().decimalPoint(detail.getIncreaseAmtBas(), 2));
		        		asset.setIncreaseCharge(new EsbUtil().decimalPoint(detail.getIncreaseCharge(), 2));
		        		asset.setMarketValBas(new EsbUtil().decimalPoint(detail.getMarketValBas(), 2));
		        		asset.setSignDigitBas(detail.getSignDigitBas().trim());
		        		asset.setProfitAndLossBas(new EsbUtil().decimalPoint(detail.getProfitAndLossBas(), 2));
		        		asset.setRetrunBas(new EsbUtil().decimalPoint(detail.getRetrunBas(), 2));
		        		asset.setTargetRate(new EsbUtil().decimalPoint(detail.getTargetRate(), 2));
		        		asset.setCouponCode(detail.getCouponCode().trim());
		        		asset.setIncreaseAmtTwd(investmentTwd);
		        		asset.setMarketValTwd(marketValueTwd);
		        		asset.setSignDigitTwd(detail.getSignDigitTwd().trim());
		        		asset.setProfitAndLossTwd(new EsbUtil().decimalPoint(detail.getProfitAndLossTwd(), 0));
		        		asset.setRetrunTwd(new EsbUtil().decimalPoint(detail.getRetrunTwd(), 2));
		        		asset.setTrustClosingDate(detail.getTrustClosingDate().trim());
		        		asset.setChgPurchase(detail.getChgPurchase().trim());
		        		asset.setDivType(detail.getDivType().trim());
		        		asset.setDividendamount(new EsbUtil().decimalPoint(detail.getDividendamount(), 2));
		        		asset.setSigncod(detail.getSigncod().trim());
		        		asset.setInterestrateofreturn(new EsbUtil().decimalPoint(detail.getInterestrateofreturn(), 2));
		        		
		        		try {
			        		//取得NMI002電文的TARGET, YEARLIMIT欄位，若有多筆只抓取第一筆資料
		        			if(!StringUtils.equals("Y", inputVO.getGetSumYN())) {	//資產總覽不需要打這個電文，投資分布明細資料才需要
				        		List<CustAssetPotInfo> nmi002 = getNanoAssetDetail(asset.getPotId());
				        		if(CollectionUtils.isNotEmpty(nmi002)) {
				        			asset.setTarget(nmi002.get(0).getTarget());
				        			asset.setYearLimit(nmi002.get(0).getYearLimit());
				        			asset.setStarDate(StringUtils.isEmpty(nmi002.get(0).getStarDate()) ? null: new SimpleDateFormat("yyyyMMdd").parse(nmi002.get(0).getStarDate()));
				        			asset.setType(nmi002.get(0).getType());
				        			asset.setAmt(nmi002.get(0).getAmt());
				        			asset.setRiskPref(nmi002.get(0).getRiskPref());
				        			asset.setChargeDate(nmi002.get(0).getChargeDate());
				        			asset.setStrategy(nmi002.get(0).getStrategy()); //1:奈米1號-全球ETF 2:奈米2號-台灣ETF
				        			asset.setCurrency(StringUtils.equals("2", asset.getStrategy()) ? "TWD" : "USD"); //奈米頭2號為台幣；1號為美金
				        			asset.setDivType(nmi002.get(0).getDivType()); 
				        			asset.setExchange(StringUtils.equals("Y", nmi002.get(0).getExchange()) ? nmi002.get(0).getExchange() : asset.getCurrency());//Y:自動換匯 USD:直接扣美金 TWD:直接扣台幣
				        			asset.setMaximumInvestmentAmount(nmi002.get(0).getMaximumInvestmentAmount());
				        			asset.setSelectedCards(nmi002.get(0).getSelectedCards());
				        			asset.setSelectedCardsName(getCardNames(nmi002.get(0).getSelectedCards()));
				        			asset.setCumulativeMultiple(nmi002.get(0).getCumulativeMultiple());
				        			asset.setIsCumulativeSwitchOn(nmi002.get(0).getIsCumulativeSwitchOn());
				        			asset.setCumulativeAmount(nmi002.get(0).getCumulativeAmount());	
				        			
				        			for (NMI001 vo : nmi001) {
				        				// 取得『贖回中』資料
				        				if (vo.getCardStatus() != null && vo.getCardStatus().equals("1") && 
				     		        		vo.getEviNum() != null && vo.getEviNum().equals(asset.getPotId())) {
				     		        		
				        					try {
							        			List<NMI003> nmi003 = this.getNMI003(asset.getPotId());
							        			CustAssetNano sell = null;
							        			List<String> potIdList = new ArrayList<String>();
							        			
							        			for (NMI003 nmi003VO : nmi003) {
							        				if (nmi003VO.getType() != null && nmi003VO.getType().equals("2")) {
							        					String potID = detail.getPotId().trim();
							        					String modifyTime = nmi003VO.getModifyTime();
							        					if (potIdList.contains(potID)) {
							        						//【贖回在途】調整顯示判斷：同一契約編號贖回申請日僅抓最新的日期顯示即可。
							        						for (CustAssetNano nano : sellList) {
							        							if (nano.getPotId().equals(potID)) {
							        								String time = nano.getModifyTime();
							        								if (modifyTime.compareTo(time) > 0) {
							        									nano.setModifyTime(modifyTime);
							        									break;
							        								}							        								
							        							}
							        						}
							        					} else {
							        						sell = new CustAssetNano();
							        						sell.setModifyTime(modifyTime);
							        						sell.setPotId(detail.getPotId().trim());
							        						sell.setPlanName(detail.getPlanName().trim());
							        						sell.setStrategy(nmi002.get(0).getStrategy()); //1:奈米1號-全球ETF 2:奈米2號-台灣ETF
							        						sell.setRiskPref(nmi002.get(0).getRiskPref());
							        						sell.setCurrency(StringUtils.equals("2", asset.getStrategy()) ? "TWD" : "USD"); //奈米頭2號為台幣；1號為美金
							        						sell.setIncreaseAmtBas(new EsbUtil().decimalPoint(detail.getIncreaseAmtBas(), 2));
							        						sell.setIncreaseAmtTwd(investmentTwd);
							        						sell.setMarketValBas(new EsbUtil().decimalPoint(detail.getMarketValBas(), 2));
							        						sell.setMarketValTwd(marketValueTwd);
							        						sell.setSignDigitBas(detail.getSignDigitBas().trim());
							        						sell.setRetrunBas(new EsbUtil().decimalPoint(detail.getRetrunBas(), 2));
							        						sellList.add(sell);
							        						potIdList.add(potID);
							        					}
							        				}
							        			}
							        		} catch(Exception e) {
							        			return_VO.setErrorMsg(e.getMessage());
							        		}
				     		        	}
				        			}
					        	}
		        			}
		        		} catch(Exception e) {
		        			return_VO.setErrorMsg(e.getMessage());
		        		}
		        		results.add(asset);
	        		}
	        	}
	        }
	        return_VO.setTotalPlanNo(results.size());
	        return_VO.setResultList(results);
	        return_VO.setSellList(sellList);
	        
        } catch (Exception e) {
        	return_VO.setErrorMsg(e.getMessage());
        }
        
        return_VO.setTotalInvestmentTwd(totalInvestmentTwd);
        return_VO.setTotalMarketValueTwd(totalMarketValueTwd);
            
		this.sendRtnObject(return_VO);
	}

	/**
	 * Dummy, only for testing NMI002
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void getNanoAsset1(Object body, IPrimitiveMap header) throws Exception {
		List<CustAssetPotInfo> asset = getNanoAssetDetail("250900000008");
	}
	
	/**
	 * 取得電文NMI002奈米投明細資料
	 * @param potID 契約編號
	 * @return
	 * @throws Exception
	 */
	public List<CustAssetPotInfo> getNanoAssetDetail(String potID) throws Exception {
		//init util
		String txtId = NMI002;			// 電文代碼
		String module = thisClaz+new Object(){}.getClass().getEnclosingMethod().getName();	// 模組代碼
		EsbUtil3 esbUtil3 = new EsbUtil3(module, this.getEsbUrl(ESB_TYPE), this.getEsbID(ESB_TYPE), this.getEsbSeq());
        //head
		String txHeadXml = esbUtil3.getTxHead(txtId, null); 

        //body
		Map<String, String> txBody = new HashMap<String, String>();
		txBody.put("EviNum", potID);
		String txBodyXml = esbUtil3.getTxBody(txBody);
		//發送電文
		List<String> dnXmls = esbUtil3.send(txHeadXml + txBodyXml);

        List<CustAssetPotInfo> results = new ArrayList<CustAssetPotInfo>();
        CustAssetPotInfo asset = new CustAssetPotInfo();
        
        for(String dnXml : dnXmls) {
        	String hfmtid = esbUtil3.getTagValue(dnXml, "HFMTID");
			hfmtid = hfmtid == null ? "" : hfmtid.trim();
            
            /**
             * 下行電文會有2個Format的格式
             * Header.‧HFMTID =0001：奈米投契約明細查詢
             * Header.‧HFMTID =E001：錯誤訊息使用
             */
			String txBodyStr = esbUtil3.getTagValue(dnXml, "TxBody");
            if("0001".equals(hfmtid)) {
            	List<String> txRepeats = esbUtil3.getTagValueList(txBodyStr, "TxRepeat");
            	for (String txRepeat : txRepeats) {
	        		asset.setStarDate(esbUtil3.getTagClearValue(txRepeat, "StarDate"));
	        		asset.setYearLimit(Integer.parseInt(esbUtil3.getTagClearValue(txRepeat, "YearLimit")));
	        		asset.setStrategy(esbUtil3.getTagClearValue(txRepeat, "Strategy"));
	        		asset.setRiskPref(esbUtil3.getTagClearValue(txRepeat, "RiskPref"));
	        		asset.setType(esbUtil3.getTagClearValue(txRepeat, "Type"));
	        		asset.setTargetAmt(StringUtils.isBlank(esbUtil3.getTagClearValue(txRepeat, "TargetAmt")) ? BigDecimal.ZERO : new BigDecimal(esbUtil3.getTagClearValue(txRepeat, "TargetAmt")));
	        		asset.setAcct(esbUtil3.getTagClearValue(txRepeat, "Acct"));
	        		asset.setAmt(StringUtils.isBlank(esbUtil3.getTagClearValue(txRepeat, "Amt")) ? BigDecimal.ZERO : new BigDecimal(esbUtil3.getTagClearValue(txRepeat, "Amt")));
	        		asset.setChargeDate(esbUtil3.getTagClearValue(txRepeat, "ChargeDate"));
	        		asset.setNextChargeDate(esbUtil3.getTagClearValue(txRepeat, "NextChargeDate"));
	        		asset.setStatus(esbUtil3.getTagClearValue(txRepeat, "Status"));
	        		asset.setEviNum(esbUtil3.getTagClearValue(txRepeat, "EviNum"));
	        		asset.setCharge(StringUtils.isBlank(esbUtil3.getTagClearValue(txRepeat, "Charge")) ? BigDecimal.ZERO : new BigDecimal(esbUtil3.getTagClearValue(txRepeat, "Charge")));
	        		asset.setCoupon(esbUtil3.getTagClearValue(txRepeat, "Coupon"));
	        		asset.setIsNeedApprove(esbUtil3.getTagClearValue(txRepeat, "IsNeedApprove"));
	        		asset.setPlanName(esbUtil3.getTagClearValue(txRepeat, "PlanName"));
	        		asset.setTarget(esbUtil3.getTagClearValue(txRepeat, "Target"));
	        		asset.setExchange(esbUtil3.getTagClearValue(txRepeat, "Exchange"));
	        		asset.setDivType(esbUtil3.getTagClearValue(txRepeat, "DivType"));
	        		asset.setMaximumInvestmentAmount(StringUtils.isBlank(esbUtil3.getTagClearValue(txRepeat, "MaximumInvestmentAmount")) ? BigDecimal.ZERO : new BigDecimal(esbUtil3.getTagClearValue(txRepeat, "MaximumInvestmentAmount")));
	        		asset.setSelectedCards(esbUtil3.getTagClearValue(txRepeat, "SelectedCards"));
	        		asset.setCumulativeMultiple(esbUtil3.getTagClearValue(txRepeat, "CumulativeMultiple"));
	        		asset.setIsCumulativeSwitchOn(esbUtil3.getTagClearValue(txRepeat, "IsCumulativeSwitchOn"));
	        		asset.setCumulativeAmount(StringUtils.isBlank(esbUtil3.getTagClearValue(txRepeat, "CumulativeAmount")) ? BigDecimal.ZERO : new BigDecimal(esbUtil3.getTagClearValue(txRepeat, "CumulativeAmount")));
	        		results.add(asset);
	        		asset = new CustAssetPotInfo();
	        	}
            } else if("E001".equals(hfmtid)) {
            	String MSGID = esbUtil3.getTagClearValue(txBodyStr, "MSGID");
            	if(StringUtils.isNotBlank(MSGID)) {
            		String MSGTXT = esbUtil3.getTagClearValue(txBodyStr, "MSGTXT");
        			throw new JBranchException(String.format("%s:%s", MSGID, MSGTXT));
        		} 
            }
        }
        
        return results;       
	}
	
	public List<CustAssetPotInfo> getNanoAssetDetail_old(String potID) throws Exception {
        String htxtid = NMI002;

        //init util
        ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, htxtid);
        esbUtilInputVO.setModule(thisClaz+new Object(){}.getClass().getEnclosingMethod().getName());

        //head
        TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
        txHead.setDefaultTxHead();
        esbUtilInputVO.setTxHeadVO(txHead);

        //body
        NMI002InputVO nmi002InputVO = new NMI002InputVO();
        nmi002InputVO.setEviNum(potID);
        esbUtilInputVO.setNmi002InputVO(nmi002InputVO);

        //發送電文
        List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

        NMI002OutputVO nmi002OutputVO = new NMI002OutputVO();
        List<CustAssetPotInfo> results = new ArrayList<CustAssetPotInfo>();
        CustAssetPotInfo asset = new CustAssetPotInfo();
        
        for(ESBUtilOutputVO esbUtilOutputVO : vos) {
        	TxHeadVO headVO = esbUtilOutputVO.getTxHeadVO();

            String hfmtid = StringUtils.isBlank(headVO.getHFMTID()) ? "" : headVO.getHFMTID().trim();
            
            /**
             * 下行電文會有2個Format的格式
             * Header.‧HFMTID =0001：奈米投契約明細查詢
             * Header.‧HFMTID =E001：錯誤訊息使用
             */
            if("0001".equals(hfmtid)) {
	        	nmi002OutputVO = esbUtilOutputVO.getNmi002OutputVO();
	        	for(NMI002OutputVODetails detail : nmi002OutputVO.getDetails()) {
	        		asset.setStarDate(detail.getStarDate());
	        		asset.setYearLimit(Integer.parseInt(detail.getYearLimit()));
	        		asset.setStrategy(detail.getStrategy());
	        		asset.setRiskPref(detail.getRiskPref());
	        		asset.setType(detail.getType());
	        		asset.setTargetAmt(StringUtils.isBlank(detail.getTargetAmt()) ? BigDecimal.ZERO : new BigDecimal(detail.getTargetAmt()));
	        		asset.setAcct(detail.getAcct());
	        		asset.setAmt(StringUtils.isBlank(detail.getAmt()) ? BigDecimal.ZERO : new BigDecimal(detail.getAmt()));
	        		asset.setChargeDate(detail.getChargeDate());
	        		asset.setNextChargeDate(detail.getNextChargeDate());
	        		asset.setStatus(detail.getStatus());
	        		asset.setEviNum(detail.getEviNum());
	        		asset.setCharge(StringUtils.isBlank(detail.getCharge()) ? BigDecimal.ZERO : new BigDecimal(detail.getCharge()));
	        		asset.setCoupon(detail.getCoupon());
	        		asset.setIsNeedApprove(detail.getIsNeedApprove());
	        		asset.setPlanName(detail.getPlanName());
	        		asset.setTarget(detail.getTarget());
	        		asset.setExchange(detail.getExchange());
	        		asset.setDivType(detail.getDivType());
	        		asset.setMaximumInvestmentAmount(StringUtils.isBlank(detail.getMaximumInvestmentAmount()) ? BigDecimal.ZERO : new BigDecimal(detail.getMaximumInvestmentAmount()));
	        		asset.setSelectedCards(detail.getSelectedCards());
	        		asset.setCumulativeMultiple(detail.getCumulativeMultiple());
	        		asset.setIsCumulativeSwitchOn(detail.getIsCumulativeSwitchOn());
	        		asset.setCumulativeAmount(StringUtils.isBlank(detail.getCumulativeAmount()) ? BigDecimal.ZERO : new BigDecimal(detail.getCumulativeAmount()));
	        		results.add(asset);
	        		asset = new CustAssetPotInfo();
	        	}
            } else if("E001".equals(hfmtid)) {
            	if(StringUtils.isNotBlank(nmi002OutputVO.getMSGID())) {
        			throw new JBranchException(String.format("%s:%s", nmi002OutputVO.getMSGID(), nmi002OutputVO.getMSGTXT()));
        		} 
            }
        }
        
        return results;       
	}
	/**
	 * 取得電文NMI001_個人風險屬性、卡片標籤查詢
	 * @param custID 客戶ID	
	 * @return
	 * @throws Exception
	 */
	public List<NMI001> getNMI001(String custID) throws Exception {
		String htxtid = NMI001;
		
		String module = thisClaz+new Object(){}.getClass().getEnclosingMethod().getName();	// 模組代碼
		EsbUtil3 esbUtil3 = new EsbUtil3(module, this.getEsbUrl(ESB_TYPE), this.getEsbID(ESB_TYPE), this.getEsbSeq());
        //head
		String txHeadXml = esbUtil3.getTxHead(htxtid, null); 

        //body
		Map<String, String> txBody = new HashMap<String, String>();
		txBody.put("CustId", custID);
		txBody.put("CurAcctName", "0001");
		String txBodyXml = esbUtil3.getTxBody(txBody);
		//發送電文
		List<String> dnXmls = esbUtil3.send(txHeadXml + txBodyXml);

		List<NMI001> results = new ArrayList<NMI001>();
        
        for(String dnXml : dnXmls) {
        	String hfmtid = esbUtil3.getTagValue(dnXml, "HFMTID");
			hfmtid = hfmtid == null ? "" : hfmtid.trim();
            
            /**
             * 下行電文會有2個Format的格式
             * Header.‧HFMTID =0001：奈米投契約明細查詢
             * Header.‧HFMTID =E001：錯誤訊息使用
             */
			String txBodyStr = esbUtil3.getTagValue(dnXml, "TxBody");
            if("0001".equals(hfmtid)) {
            	List<String> txRepeats = esbUtil3.getTagValueList(txBodyStr, "TxRepeat");
				for (String txRepeat : txRepeats) {
					String EviNum = esbUtil3.getTagClearValue(txRepeat, "EviNum");
					if(StringUtils.isNotBlank(EviNum)){
						NMI001 nmi001 = new NMI001();
						nmi001.setEviNum(EviNum);
						nmi001.setCardStatus(esbUtil3.getTagClearValue(txRepeat, "CardStatus"));
						results.add(nmi001);
					}
				}
			} else if("E001".equals(hfmtid)) {
				String MSGID = esbUtil3.getTagClearValue(txBodyStr, "MSGID");
            	if(StringUtils.isNotBlank(MSGID)) {
            		String MSGTXT = esbUtil3.getTagClearValue(txBodyStr, "MSGTXT");
        			throw new JBranchException(String.format("%s:%s", MSGID, MSGTXT));
        		} 
			}
		}
		return results;       
	}
	
	public List<NMI001> getNMI001_old(String custID) throws Exception {
		String htxtid = NMI001;
		
		//init util
		ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, htxtid);
		esbUtilInputVO.setModule(thisClaz+new Object(){}.getClass().getEnclosingMethod().getName());
		
		//head
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
		txHead.setDefaultTxHead();
		esbUtilInputVO.setTxHeadVO(txHead);
		
		//body
		NMI001InputVO nmi001InputVO = new NMI001InputVO();
		nmi001InputVO.setCustId(custID);
		nmi001InputVO.setCurAcctName("0001");
		esbUtilInputVO.setNmi001InputVO(nmi001InputVO);
		
		//發送電文
		List<ESBUtilOutputVO> vos = send(esbUtilInputVO);
		
		NMI001OutputVO nmi001OutputVO = new NMI001OutputVO();
		List<NMI001> results = new ArrayList<NMI001>();
		NMI001 nmi001 = new NMI001();
		
		for(ESBUtilOutputVO esbUtilOutputVO : vos) {
			TxHeadVO headVO = esbUtilOutputVO.getTxHeadVO();
			String hfmtid = StringUtils.isBlank(headVO.getHFMTID()) ? "" : headVO.getHFMTID().trim();
			/**
			 * 下行電文會有2個Format的格式
			 * Header.‧HFMTID =0001：奈米投契約交易紀錄查詢
			 * Header.‧HFMTID =E001：錯誤訊息使用
			 */
			if("0001".equals(hfmtid)) {
				nmi001OutputVO = esbUtilOutputVO.getNmi001OutputVO();
				
				for (NMI001OutputVODetails detail : nmi001OutputVO.getDetails()) {
					if(StringUtils.isNotBlank(detail.getEviNum())){
						nmi001 = new NMI001();
						nmi001.setEviNum(detail.getEviNum());
						nmi001.setCardStatus(detail.getCardStatus());
						results.add(nmi001);
					}
				}
			} else if("E001".equals(hfmtid)) {
				if(StringUtils.isNotBlank(nmi001OutputVO.getMSGID())) {
					throw new JBranchException(String.format("%s:%s", nmi001OutputVO.getMSGID(), nmi001OutputVO.getMSGTXT()));
				} 
			}
		}
		return results;       
	}
	/**
	 * 取得電文NMI003_奈米投契約交易紀錄查詢
	 * @param potID 契約編號
	 * @return
	 * @throws Exception
	 */
	public List<NMI003> getNMI003(String potID) throws Exception {		
		//init util
		String txtId = NMI003;			// 電文代碼
		String module = thisClaz+new Object(){}.getClass().getEnclosingMethod().getName();	// 模組代碼
		EsbUtil3 esbUtil3 = new EsbUtil3(module, this.getEsbUrl(ESB_TYPE), this.getEsbID(ESB_TYPE), this.getEsbSeq());
        //head
		String txHeadXml = esbUtil3.getTxHead(txtId, null); 

        //body
		Map<String, String> txBody = new HashMap<String, String>();
		txBody.put("EviNum", potID);
		String txBodyXml = esbUtil3.getTxBody(txBody);
		//發送電文
		List<String> dnXmls = esbUtil3.send(txHeadXml + txBodyXml);
		
		NMI003OutputVO nmi003OutputVO = new NMI003OutputVO();
		List<NMI003> results = new ArrayList<NMI003>();
		NMI003 nmi003 = new NMI003();
		
        for(String dnXml : dnXmls) {
        	String hfmtid = esbUtil3.getTagValue(dnXml, "HFMTID");
			hfmtid = hfmtid == null ? "" : hfmtid.trim();
            
            /**
             * 下行電文會有2個Format的格式
             * Header.‧HFMTID =0001：奈米投契約明細查詢
             * Header.‧HFMTID =E001：錯誤訊息使用
             */
			String txBodyStr = esbUtil3.getTagValue(dnXml, "TxBody");
			if("0001".equals(hfmtid)) {
            	List<String> txRepeats = esbUtil3.getTagValueList(txBodyStr, "TxRepeat");
				for (String txRepeat : txRepeats) {
					nmi003.setType(esbUtil3.getTagClearValue(txRepeat, "Type"));
					nmi003.setModifyTime(esbUtil3.getTagClearValue(txRepeat, "ModifyTime"));
					results.add(nmi003);
					nmi003 = new NMI003();
				}
			} else if("E001".equals(hfmtid)) {
				if(StringUtils.isNotBlank(nmi003OutputVO.getMSGID())) {
					throw new JBranchException(String.format("%s:%s", nmi003OutputVO.getMSGID(), nmi003OutputVO.getMSGTXT()));
				} 
			}
		}
		return results;       
	}
	
	public List<NMI003> getNMI003_old(String potID) throws Exception {
		String htxtid = NMI003;
		
		//init util
		ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, htxtid);
		esbUtilInputVO.setModule(thisClaz+new Object(){}.getClass().getEnclosingMethod().getName());
		
		//head
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
		txHead.setDefaultTxHead();
		esbUtilInputVO.setTxHeadVO(txHead);
		
		//body
		NMI003InputVO nmi003InputVO = new NMI003InputVO();
		nmi003InputVO.setEviNum(potID);
		esbUtilInputVO.setNmi003InputVO(nmi003InputVO);
		
		//發送電文
		List<ESBUtilOutputVO> vos = send(esbUtilInputVO);
		
		NMI003OutputVO nmi003OutputVO = new NMI003OutputVO();
		List<NMI003> results = new ArrayList<NMI003>();
		NMI003 nmi003 = new NMI003();
		
		for(ESBUtilOutputVO esbUtilOutputVO : vos) {
			TxHeadVO headVO = esbUtilOutputVO.getTxHeadVO();
			String hfmtid = StringUtils.isBlank(headVO.getHFMTID()) ? "" : headVO.getHFMTID().trim();
			/**
			 * 下行電文會有2個Format的格式
			 * Header.‧HFMTID =0001：奈米投契約交易紀錄查詢
			 * Header.‧HFMTID =E001：錯誤訊息使用
			 */
			if("0001".equals(hfmtid)) {
				nmi003OutputVO = esbUtilOutputVO.getNmi003OutputVO();
				for(NMI003OutputVODetails detail : nmi003OutputVO.getDetails()) {
					nmi003.setType(detail.getType());
					nmi003.setModifyTime(detail.getModifyTime());
					results.add(nmi003);
					nmi003 = new NMI003();
				}
			} else if("E001".equals(hfmtid)) {
				if(StringUtils.isNotBlank(nmi003OutputVO.getMSGID())) {
					throw new JBranchException(String.format("%s:%s", nmi003OutputVO.getMSGID(), nmi003OutputVO.getMSGTXT()));
				} 
			}
		}
		return results;       
	}
	/***
	 * 契約是否仍有效(尚未結清終止)
	 * @param contractNo
	 * @return
	 * 	True：是
	 * 	False；否
	 * @throws JBranchException 
	 * @throws DAOException 
	 */
	private Boolean isContractActive(String contractNo) throws DAOException, JBranchException {
		DataAccessManager dam_obj = getDataAccessManager();
		QueryConditionIF qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		List<Map<String, Object>> chkList = new ArrayList<Map<String, Object>>();
		
		sb.append(" SELECT TMF10 FROM TBCRM_NMS060_DAY WHERE TMF01 = :contractNo ");	//有信託終止日TMF10，表示已終止
		qc.setObject("contractNo", contractNo);
		qc.setQueryString(sb.toString());
		chkList = dam_obj.exeQuery(qc);
		
		return ((CollectionUtils.isNotEmpty(chkList) && chkList.get(0).get("TMF10") != null) ? Boolean.FALSE : Boolean.TRUE);
	}
	
	/***
	 * 奈米投_邊刷邊投卡類，電文回傳多個代碼並以逗號隔開
	 * 須回傳各代碼中文名稱
	 * @param cardCodes
	 * @return
	 * @throws JBranchException
	 */
	private String getCardNames(String cardCodes) throws JBranchException {
		Map<String,Object> cardTypeMap = new XmlInfo().getVariable("CRM.NANO_SELECT_CARD", FormatHelper.FORMAT_3);	// 奈米投_邊刷邊投卡類
		String cardNames = "";
		if (StringUtils.isNotBlank(cardCodes)) {
			String codeAry[] = cardCodes.split(",");
			for(String code : codeAry) {
				String cardName = ObjectUtils.toString(cardTypeMap.get(code.trim())); //取卡片類型中文
				cardNames += (StringUtils.isBlank(cardNames) ? cardName : ", " + cardName);
			}			
		}
		return cardNames;
	}
	
}