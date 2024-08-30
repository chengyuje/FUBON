package com.systex.jbranch.app.server.fps.fps200;

import com.google.gson.Gson;
import com.systex.jbranch.app.common.fps.table.*;
import com.systex.jbranch.app.server.fps.fps200.FPS200ApiUtils.FPSExt;
import com.systex.jbranch.app.server.fps.fps200.FPS200ApiUtils.FPSType;
import com.systex.jbranch.app.server.fps.fps210.FPS210;
import com.systex.jbranch.app.server.fps.fps210.FPS210InputVO;
import com.systex.jbranch.app.server.fps.fps220.FPS220;
import com.systex.jbranch.app.server.fps.fps220.FPS220InputVO;
import com.systex.jbranch.app.server.fps.fps230.FPS230;
import com.systex.jbranch.app.server.fps.fps230.FPS230InputVO;
import com.systex.jbranch.app.server.fps.fps230.FPS230ProdInputVO;
import com.systex.jbranch.app.server.fps.fps240.FPS240;
import com.systex.jbranch.app.server.fps.fps240.FPS240OutputVO;
import com.systex.jbranch.app.server.fps.fps250.FPS250;
import com.systex.jbranch.app.server.fps.fps250.FPS250InputVO;
import com.systex.jbranch.app.server.fps.fps324.FPS324;
import com.systex.jbranch.app.server.fps.fps324.FPS324InputVO;
import com.systex.jbranch.app.server.fps.fps324.FPS324PrdInputVO;
import com.systex.jbranch.app.server.fps.fps330.FPS330;
import com.systex.jbranch.app.server.fps.fps340.FPS340;
import com.systex.jbranch.app.server.fps.fps350.FPS350;
import com.systex.jbranch.app.server.fps.fps350.FPS350InputVO;
import com.systex.jbranch.app.server.fps.fps400.FPS400;
import com.systex.jbranch.app.server.fps.fps400.FPS400InputVO;
import com.systex.jbranch.app.server.fps.fps410.FPS410;
import com.systex.jbranch.app.server.fps.fpsjlb.FPSJLB;
import com.systex.jbranch.app.server.fps.fpsjlb.dao.FpsjlbDao;
import com.systex.jbranch.app.server.fps.fpsutils.FPSUtils;
import com.systex.jbranch.app.server.fps.fpsutils.FPSUtilsOutputVO;
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.WorkStation;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.ObjectUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.server.mail.FubonMail;
import com.systex.jbranch.platform.server.mail.FubonSendJavaMail;
import com.systex.jbranch.platform.util.IPrimitiveMap;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.xerces.impl.dv.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeUtility;
import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component("fps200api")
@Scope("request")
public class FPS200Api extends FPS200 {
	@Autowired
    @Qualifier("fps200")
    private FPS200 fps200;
    @Autowired
    @Qualifier("fps210")
    private FPS210 fps210;
    @Autowired
    @Qualifier("fps220")
    private FPS220 fps220;
    @Autowired
    @Qualifier("fps230")
    private FPS230 fps230;
    @Autowired
    @Qualifier("fps240")
    private FPS240 fps240;
    @Autowired
    @Qualifier("fps250")
    private FPS250 fps250;
    @Autowired
    @Qualifier("fps324")
    private FPS324 fps324;
    @Autowired
    @Qualifier("fps330")
    private FPS330 fps330;
    @Autowired
    @Qualifier("fps340")
    private FPS340 fps340;
    @Autowired
    @Qualifier("fps350")
    private FPS350 fps350;
    @Autowired
    @Qualifier("fps400")
    private FPS400 fps400;
    @Autowired
    @Qualifier("fps410")
    private FPS410 fps410;
    @Autowired
    @Qualifier("fpsjlb")
    private FPSJLB fpsjlb;
    @Autowired
    @Qualifier("fpsjlbDao")
    private FpsjlbDao fpsDao;
    @Autowired
    @Qualifier("fpsutils")
    private FPSUtils fpsUtils;

    private FPS200ApiProduct productFactory = new FPS200ApiProduct();

    /**
     * API- 2.8.1 getFinancialRefData 取得理財規劃參數
     * 
     * @param body
     * @param header
     * @throws JBranchException
     *             Exception
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void getFinancialRefData(Object body, IPrimitiveMap header) throws JBranchException, Exception{
        DataAccessManager dam = this.getDataAccessManager();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        Map<String, Object> inputMap = (Map<String, Object>) body;
        Map<String, Object> outputMap = new HashMap<String, Object>();

        // 有無庫存
        boolean hasStock = false;

        String custID = inputMap.get("custId").toString();
        String riskType = inputMap.get("riskLevel").toString();
        // set init key map for checkValidYear
        Map<String, String> keyMap = new HashMap<String, String>();
        keyMap.put("prodType", "PTYPE");
        keyMap.put("prodID", "PRD_ID");
        keyMap.put("targets", "TARGETS");

        if (StringUtils.isBlank(custID)) {
            throw new APException("沒有CustID");
        }

        // update cust loans (打電文更新TBFPS_CUST_LIS)
        Boolean canUpdate = false;
        TBFPS_CUST_LISTVO list_vo = (TBFPS_CUST_LISTVO) dam.findByPKey(TBFPS_CUST_LISTVO.TABLE_UID, custID);
        canUpdate = doCustLoan(dam, custID, list_vo) || canUpdate;
        // update cust ins (打電文更新TBFPS_CUST_LIS)
        canUpdate = doCustIns(dam, custID, list_vo) || canUpdate;
        if (canUpdate)
            dam.update(list_vo);

        // get params from fps210 (1, 2)
        List<Map<String, Object>> custAmtList = fps210.getCustAmt(dam, custID);		//從TBFPS_CUST_LIST取得最新資料（由上方打電文會立即更新）
        if (custAmtList.size() <= 0) {
            throw new Exception("fps210 getCustAmt 回傳為空");
        }
        Map<String, Object> custAmtMap = custAmtList.get(0);

        // 1. assets get params from fps210
        Map<String, Integer> assetsMap = new HashMap<String, Integer>();
        // set assets params
//        assetsMap.put("deposit", FPS200ApiMappingFactory.defaultIntValue(custAmtMap.get("DEPOSIT_AMT")));
//        assetsMap.put("insurence", FPS200ApiMappingFactory.defaultIntValue(custAmtMap.get("INS_1_AMT")));
//        assetsMap.put("income", FPS200ApiMappingFactory.defaultIntValue(custAmtMap.get("FIXED_INCOME_AMT")));
//        assetsMap.put("fund", FPS200ApiMappingFactory.defaultIntValue(custAmtMap.get("FUND_AMT")));
//        hasStock = (assetsMap.get("insurence") + assetsMap.get("income") + assetsMap.get("fund")) > 0;
        int depositAmt = FPS200ApiMappingFactory.defaultIntValue(custAmtMap.get("DEPOSIT_AMT"));
        int mfdProdAmt = FPS200ApiMappingFactory.defaultIntValue(custAmtMap.get("MFD_PROD_AMT"));
        int etfProdAmt = FPS200ApiMappingFactory.defaultIntValue(custAmtMap.get("ETF_PROD_AMT"));
        int insProdAmt = FPS200ApiMappingFactory.defaultIntValue(custAmtMap.get("INS_PROD_AMT"));
        int bondProdAmt = FPS200ApiMappingFactory.defaultIntValue(custAmtMap.get("BOND_PROD_AMT"));
        int snProdAmt = FPS200ApiMappingFactory.defaultIntValue(custAmtMap.get("SN_PROD_AMT"));
        int siProdAmt = FPS200ApiMappingFactory.defaultIntValue(custAmtMap.get("SI_PROD_AMT"));
        int nanoProdAmt = FPS200ApiMappingFactory.defaultIntValue(custAmtMap.get("NANO_PROD_AMT"));
        int invAmt = depositAmt + mfdProdAmt + etfProdAmt + bondProdAmt + snProdAmt + siProdAmt + nanoProdAmt;
        
        assetsMap.put("invAmt", invAmt);			// 帳上投資資產
        assetsMap.put("deposit", depositAmt);		// 存款
        assetsMap.put("mfdProdAmt", mfdProdAmt);	// 基金
        assetsMap.put("etfProdAmt", etfProdAmt);	// ETF
        assetsMap.put("insProdAmt", insProdAmt);	// 投資型保險
        assetsMap.put("bondProdAmt", bondProdAmt);	// 海外債
        assetsMap.put("snProdAmt", snProdAmt);		// SN
        assetsMap.put("siProdAmt", siProdAmt);		// SI	
        assetsMap.put("nanoProdAmt", nanoProdAmt);	// 奈米投
        
        assetsMap.put("sugCashPrePct", fps210.getCashPreparePct(dam, custID).intValue());	//現金準備建議上限
        
        hasStock = (assetsMap.get("mfdProdAmt") + assetsMap.get("etfProdAmt") + assetsMap.get("insProdAmt") +
        			assetsMap.get("bondProdAmt") + assetsMap.get("snProdAmt") + assetsMap.get("siProdAmt") + assetsMap.get("nanoProdAmt")) > 0;

        // 2. expenses get params from fps210
        // todo: 打電文拿新資料
        Map<String, Integer> expensesMap = new HashMap<String, Integer>();
        // set assets params
        expensesMap.put("houseLoans", FPS200ApiMappingFactory.defaultIntValue(custAmtMap.get("LN_YEAR_AMT_1")));
        expensesMap.put("creditLoans", FPS200ApiMappingFactory.defaultIntValue(custAmtMap.get("LN_YEAR_AMT_2")));
        expensesMap.put("eduLoans", FPS200ApiMappingFactory.defaultIntValue(custAmtMap.get("LN_YEAR_AMT_3")));
        expensesMap.put("lifeFee", FPS200ApiMappingFactory.defaultIntValue(custAmtMap.get("INS_YEAR_AMT_1")));		//預定保費支出(年)(台幣)-保障型保費
        expensesMap.put("dopositFee", FPS200ApiMappingFactory.defaultIntValue(custAmtMap.get("INS_YEAR_AMT_2")));	//預定保費支出(年)(台幣)-儲蓄型保費
        
        // get params from fps230 (3)
        List<Map<String, Object>> modelPortfolio = fps220.getModel(dam, riskType);

        // 3. assetRatio
        Map<String, Double> assetRatioMap = new HashMap<String, Double>();
        Map<String, Double> modelPctsMap = mergeList(modelPortfolio, "INV_PRD_TYPE", "INV_PERCENT");
        assetRatioMap.put("depositRatio", modelPctsMap.get("1"));
        assetRatioMap.put("incomeRatio", modelPctsMap.get("2"));
        assetRatioMap.put("fundRatio", modelPctsMap.get("3"));
        
        Map<String, Object> param =  new HashMap<String, Object>();
        param.put("hasIns", hasStock );
        param.put("custID", custID);
        param.put("riskType", riskType);
        BigDecimal[] stockBondsPct = fps210.getSuggestPct(dam, param);
        assetRatioMap.put("bondsRatio", stockBondsPct[0].doubleValue());
        assetRatioMap.put("stockRatio", stockBondsPct[1].doubleValue());
        
        // 4. products
        List<Map<String, Object>> products = new ArrayList<Map<String, Object>>();

        // 5. oldPortifolio 庫存from fps230
        List<Map<String, Object>> hisStockList = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> oldPortifolio = new ArrayList<Map<String, Object>>();
        if (hasStock) {
            hisStockList = fps230.getHisStock(dam, custID);
            for (Map<String, Object> data_map : hisStockList) {
            	if("SN".equals(data_map.get("PTYPE"))) {
            		if(data_map.get("TRUST_CURR") != null) {
            			BigDecimal hu100 = new BigDecimal(100);
//            			var value = (row.TRUST_CURR && row.TRUST_CURR === 'TWD') ? row.INV_AMT_TWD : row.INV_AMT;
//                		row.SML_SUBS_MINI_AMT_FOR = Math.ceil((row.SML_SUBS_MINI_AMT_FOR * row.NOW_AMT / value)/100)*100;
//                		row.PRD_UNIT = Math.ceil((row.PRD_UNIT * row.NOW_AMT / value)/100)*100;
            			BigDecimal value = "TWD".equals(data_map.get("TRUST_CURR").toString()) ? new BigDecimal(data_map.get("INV_AMT_TWD").toString()) : new BigDecimal(data_map.get("INV_AMT").toString());
            			BigDecimal miniAmt = new BigDecimal(data_map.get("SML_SUBS_MINI_AMT_FOR").toString());
            			BigDecimal nowAmt = new BigDecimal(data_map.get("NOW_AMT").toString());
            			miniAmt = miniAmt.multiply(nowAmt).divide(value).divide(hu100).setScale(0, BigDecimal.ROUND_CEILING).multiply(hu100);
            			data_map.put("SML_SUBS_MINI_AMT_FOR", miniAmt);
            			BigDecimal prdUnit = new BigDecimal(data_map.get("PRD_UNIT").toString());		
            			prdUnit = prdUnit.multiply(nowAmt).divide(value).divide(hu100).setScale(0, BigDecimal.ROUND_CEILING).multiply(hu100);
            			data_map.put("PRD_UNIT", prdUnit);
            		}
            	}
            	String dataDate = sdf.format(data_map.get("DATA_DATE"));
            	data_map.put("DATA_DATE", dataDate);
                oldPortifolio.add(productFactory.getMapFromDBValue(data_map, FPSType.INV));
            }
        }
        
        // 6. oldVolatility
        /// todo 算法
        Double oldVolatility = 0.0;
        try {
            List<Map<String, Object>> stockList = fps220.calStockVolaility(dam, custID);
            if (stockList.size() > 0) {
                List<Map<String, Object>> formatedStockLs = formatWeightMap(stockList);
                if (FPSUtils.checkValidYear(dam, stockList, 1, keyMap).length == 0) {
                    // 庫存波動率
                    BigDecimal stockVolatility = FPSUtils.getStandardDeviation(dam, formatedStockLs, 36, 12, false);
                    oldVolatility = stockVolatility == null ? 0.0 : stockVolatility.doubleValue();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 7. portfolioLimit
        Integer portfolioLimit = 0;
        List<Map<String, Object>> resultList = getOtherPara(dam);
        if (resultList.size() > 0) {
            portfolioLimit = Integer.parseInt(resultList.get(0).get("AVAILABLE_AMT").toString());
        }

        // dopositLimit
        Integer dopositLimit = resultList.size() > 0 ? Integer.parseInt(resultList.get(0)
                                                                                  .get("DEPOSIT_AUM")
                                                                                  .toString()) : 0;

        // 8. stockURL
        String stockURL = "assets/txn/CRM610/CRM610_MAIN.html";
        
        // 9. lastExpenses (最新一筆歷史規劃)
        List<Map<String, Object>> lastCashList = fps210.getCashParam(dam, custID);		
        Map<String, Object> getLastCash = new HashMap<String, Object>();
        Map<String, Object> lastCash = new HashMap<String, Object>();
        
        if (lastCashList.size() > 0)
            getLastCash = lastCashList.get(0);
        
        lastCash.put("houseLoans", FPS200ApiMappingFactory.defaultIntValue(getLastCash.get("LN_HOUSE_AMT")));
        lastCash.put("creditLoans", FPS200ApiMappingFactory.defaultIntValue(getLastCash.get("LN_CREDIT_AMT")));
        lastCash.put("eduLoans", FPS200ApiMappingFactory.defaultIntValue(getLastCash.get("LN_EDCUATION_AMT")));
        lastCash.put("house", FPS200ApiMappingFactory.defaultIntValue(getLastCash.get("BUY_HOUSE_AMT")));
        lastCash.put("car", FPS200ApiMappingFactory.defaultIntValue(getLastCash.get("BUY_CAR_AMT")));
        lastCash.put("travel", FPS200ApiMappingFactory.defaultIntValue(getLastCash.get("TRAVEL_AMT")));
        lastCash.put("study", FPS200ApiMappingFactory.defaultIntValue(getLastCash.get("OVERSEA_EDUCATION_AMT")));
        lastCash.put("other", FPS200ApiMappingFactory.defaultIntValue(getLastCash.get("OTHER_AMT")));
        lastCash.put("cash", FPS200ApiMappingFactory.defaultIntValue(getLastCash.get("CASH_YEAR_AMT")));
        lastCash.put("liveExp", FPS200ApiMappingFactory.defaultIntValue(getLastCash.get("LIVE_YEAR_AMT")));
        lastCash.put("loanExp", FPS200ApiMappingFactory.defaultIntValue(getLastCash.get("LOAN_EXPENSES")));
        lastCash.put("otherExp", FPS200ApiMappingFactory.defaultIntValue(getLastCash.get("OTHER_EXPENSES")));
        lastCash.put("cashPrepare", FPS200ApiMappingFactory.defaultIntValue(getLastCash.get("CASH_PREPARE")));
        // INS_POLICY_AMT 未來一年所需保險費用(保障型保費)
        // INS_SAV_AMT 未來一年所需保險費用(儲蓄型保費)
        lastCash.put("insPolicyAmt", FPS200ApiMappingFactory.defaultIntValue(getLastCash.get("INS_POLICY_AMT")));
        lastCash.put("insSavAmt", FPS200ApiMappingFactory.defaultIntValue(getLastCash.get("INS_SAV_AMT")));

        // final
        outputMap.put("assets", assetsMap);
        outputMap.put("expenses", expensesMap);
        outputMap.put("assetRatio", assetRatioMap);
        outputMap.put("products", products);
        outputMap.put("oldPortifolio", oldPortifolio);
        outputMap.put("oldVolatility", oldVolatility);
        outputMap.put("volatilityLimit", fps220.getRecoVolaility(dam, riskType));
        outputMap.put("portfolioLimit", portfolioLimit);
        outputMap.put("dopositLimit", dopositLimit);
        outputMap.put("stockURL", stockURL);
        outputMap.put("rateMap", fps220.getFxRate(dam, ""));
        outputMap.put("lastExpenses", lastCash);
        
        String prevBussinessDay = fps200.getPrevBussinessDay(dam);
        outputMap.put("prevBussinessDay", prevBussinessDay);	// 前一營業日

        this.sendRtnObject(outputMap);
    }

    /**
     * API- 2.8.2 checkAssetAllocation 檢核資產配置比例是否正確(固定收益)
     * 
     * @param body
     * @param header
     * @throws JBranchException
     *             Exception
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void checkAssetAllocation(Object body, IPrimitiveMap header) throws JBranchException, Exception{
        DataAccessManager dam = this.getDataAccessManager();
        Map<String, Object> inputMap = (Map<String, Object>) body;

        String custID = inputMap.get("custId").toString();
        String riskType = inputMap.get("riskLevel").toString();
        BigDecimal incomeAmt = new BigDecimal(inputMap.get("incomeAmt").toString());

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("isOK", "0");
        if (fps220.hasSISN(dam, custID) > 0)
            result.put("isOK", "1");
        else
            result.put("isOK", fps220.checkFixedLogic(dam, incomeAmt, custID, riskType) > 0 ? "1" : "0");

        this.sendRtnObject(result);
    }

    // merge function
    private Map<String, Double> mergeList(List<Map<String, Object>> ls, String mergekey, String valueKey){
        Map<String, Double> merged = new HashMap<String, Double>();
        for (Map<String, Object> itemMap : ls) {
            merged.put(itemMap.get(mergekey).toString(), merged.get(itemMap
                                                                           .get(mergekey)) == null ? ((BigDecimal) itemMap.get(valueKey)).doubleValue() : merged.get(itemMap.get(mergekey)) + ((BigDecimal) itemMap.get(valueKey)).doubleValue());
        }

        return merged;
    }

    /**
     * API- 2.3.3 addProduct 開啟新增商品/投資展望(more)URL
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void addProduct(Object body, IPrimitiveMap header) throws Exception{

    }

    /**
     * API- 2.3.4 getProductInfo 取得商品資訊
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void getProductInfo(Object body, IPrimitiveMap header) throws Exception{

    }

    /**
     * API- 2.3.5 forwardFile 轉寄規劃書
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void forwardFile(Object body, IPrimitiveMap header) throws Exception{
        DataAccessManager dam = this.getDataAccessManager();
        Map<String, Object> inputMap = (Map<String, Object>) body;

        forwardFile(inputMap, dam);

        this.sendRtnObject(null);
    }

    public void forwardFile(Map<String, Object> inputMap, DataAccessManager dam) throws Exception{
        String custID = inputMap.get("custId").toString();
        String planID = inputMap.get("planId").toString();
        int fileType = ((Double) inputMap.get("fileType")).intValue();
        BigDecimal seq_no = null;
        if (fileType == 2) {
            seq_no = new BigDecimal(inputMap.get("fileCode").toString());
        }
        String title = ObjectUtils.toString(inputMap.get("title"));
        // 理規規畫書
        List<Map<String, Object>> pdf_list = null;
        // 保規規畫書
        List<Map<String, Object>> ins_pdf_list = null;

        if (fileType == 1) {
            QueryConditionIF queryCondition = null;
            StringBuilder sb = new StringBuilder();
            queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
            sb.append(" select FILE_NAME, REPORT_FILE from TBINS_REPORT ")
              .append(" where PLAN_ID = :planID ")
              .append(" ORDER BY CREATETIME DESC ")
              .append(" FETCH FIRST 1 ROWS ONLY ");
            queryCondition.setObject("planID", planID);
            queryCondition.setQueryString(sb.toString());
            ins_pdf_list = dam.exeQuery(queryCondition);
            if (ins_pdf_list.size() == 0){
            	throw new APException("無PDF檔案");          	
            }
        } else if (fileType == 2) {
            // fixed by russle
            String sppType = planID.substring(0, 3);
            pdf_list = FPSUtils.getPDFFile(dam, planID, seq_no, sppType);

            if (pdf_list.size() == 0)
                throw new APException("無PDF檔案");
        }
        // get cust email
        QueryConditionIF queryCondition_mail = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        queryCondition_mail.setQueryString("select EMAIL from tbcrm_cust_contact where cust_id = :cust_id");
        queryCondition_mail.setObject("cust_id", custID);
        List<Map<String, String>> mail_data = dam.exeQuery(queryCondition_mail);
        if (mail_data.size() == 0)
            throw new APException("該客戶沒有Email");
        String mail_address = mail_data.get(0).get("EMAIL");
        if (isEmail(ObjectUtils.toString(mail_address)) == false)
            throw new APException("該客戶Email格式錯誤");
        List<Map<String, String>> mailList = new ArrayList<Map<String, String>>();
        Map<String, String> mailMap = new HashMap<String, String>();
        mailMap.put(FubonSendJavaMail.MAIL, mail_address);
        mailList.add(mailMap);
        // 取得寄件者email與地點名稱
        QueryConditionIF sender_mail = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        sender_mail.setQueryString(" SELECT PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'SYS.MAIL' AND PARAM_CODE = 'from' ");
        List<Map<String, String>> sender_mail_list = dam.exeQuery(sender_mail);
        String[] sender_mail_data = ObjectUtils.toString(sender_mail_list.get(0).get("PARAM_NAME")).split(",");
        // crm361 send
        FubonSendJavaMail sendMail = new FubonSendJavaMail();
        FubonMail mail = new FubonMail();
        Map<String, Object> annexData = new HashMap<String, Object>();
        mail.setLstMailTo(mailList);
        mail.setFromName(sender_mail_data[1]);
        mail.setFromMail(sender_mail_data[0]);
        
        if(StringUtils.isBlank(title)){
        	// 設定信件主旨
        	mail.setSubject("轉寄規劃書");
        	// 設定信件內容
        	mail.setContent("轉寄規劃書");        	
        }else{
        	// 設定信件主旨
        	mail.setSubject(title);
        	// 設定信件內容
        	mail.setContent(title); 
        }
        // 附件
        if (fileType == 1) {
            byte[] rep_data = ObjectUtil.blobToByteArr((Blob) ins_pdf_list.get(0).get("REPORT_FILE"));
            String fileName = ins_pdf_list.get(0).get("FILE_NAME").toString();
            annexData.put(MimeUtility.encodeText(fileName), rep_data);
        } else if (fileType == 2) {
            Blob file = (Blob) pdf_list.get(0).get("PLAN_PDF_FILE");
            int fileLength = (int) file.length();
            byte[] onversion = file.getBytes(1, fileLength);
            file.free();
            String fileName = pdf_list.get(0).get("FILE_NAME").toString();
            annexData.put(MimeUtility.encodeText(fileName), onversion);
        }
        // 寄出信件
        sendMail.sendMail(mail, annexData);
    }

    public static boolean isEmail(String email){
        Pattern emailPattern = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
        Matcher matcher = emailPattern.matcher(email);
        if (matcher.find())
            return true;
        return false;
    }

    /**
     * API- 2.3.6 downloadPlanFile下載規劃書
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void downloadPlanFile(Object body, IPrimitiveMap header) throws Exception{
        DataAccessManager dam = this.getDataAccessManager();
        Map<String, Object> inputMap = (Map<String, Object>) body;
        int fileType = ((Double) inputMap.get("fileType")).intValue();
        String planID = inputMap.get("planId").toString();
        BigDecimal seq_no = null;
        if (fileType == 2) {
            seq_no = new BigDecimal(inputMap.get("fileCode").toString());
        }

        Map<String, Object> outputMap = new HashMap<String, Object>();

        if (fileType == 1) {
            QueryConditionIF queryCondition = null;
            StringBuilder sb = new StringBuilder();
            queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
            sb.append(" select FILE_NAME, REPORT_FILE from TBINS_REPORT ")
              .append(" where PLAN_ID = :planID ")
              .append(" ORDER BY CREATETIME DESC ")
              .append(" FETCH FIRST 1 ROWS ONLY ");
            queryCondition.setObject("planID", planID);
            queryCondition.setQueryString(sb.toString());
            List<Map<String, Object>> blobList = dam.exeQuery(queryCondition);
            if(blobList.size() == 0){
            	throw new APException("無PDF檔案");
            }
            byte[] rep_data = ObjectUtil.blobToByteArr((Blob) blobList.get(0).get("REPORT_FILE"));

            outputMap.put("file", DatatypeConverter.printBase64Binary(rep_data));
        } else if (fileType == 2) {
            // fixed by russle
            String sppType = planID.substring(0, 3);
            List<Map<String, Object>> pdf_list = FPSUtils.getPDFFile(dam, planID, seq_no, sppType);

            if (pdf_list.size() == 0)
                throw new APException("無PDF檔案");

            Blob blob = (Blob) pdf_list.get(0).get("PLAN_PDF_FILE");
            int blobLength = (int) blob.length();
            byte[] blobAsBytes = blob.getBytes(1, blobLength);
            blob.free();

            outputMap.put("file", DatatypeConverter.printBase64Binary(blobAsBytes));
        }

        this.sendRtnObject(outputMap);
    }

    /**
     * API- 2.4.1 getFinancialPurposeRefData 取得理財規畫-特定目的參數
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void getFinancialPurposeRefData(Object body, IPrimitiveMap header) throws Exception{
        DataAccessManager dam = this.getDataAccessManager();
        // Map<String, Object> inputMap = (Map<String, Object>) body;

        // String custID = ObjectUtils.toString(inputMap.get("custId"));

        // TBFPS_OTHER_PARA
        QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT b.* FROM TBFPS_OTHER_PARA_HEAD a ");
        sql.append("LEFT JOIN TBFPS_OTHER_PARA b on a.PARAM_NO = b.PARAM_NO ");
        sql.append("WHERE a.STATUS = 'A' ");
        queryCondition.setQueryString(sql.toString());
        List<Map<String, Object>> list = dam.exeQuery(queryCondition);
        if (list.size() == 0)
            throw new APException("參數沒有設定");
        Map<String, Object> list_map = list.get(0);
        // FUND_TYPE
        queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        queryCondition.setQueryString("SELECT * FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'FPS.FUND_TYPE' ORDER BY PARAM_CODE");
        List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
        // MKT_TIER3
        queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        queryCondition.setQueryString("SELECT * FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'PRD.MKT_TIER3' ORDER BY PARAM_CODE");
        List<Map<String, Object>> list3 = dam.exeQuery(queryCondition);
        //
        LinkedHashMap<String, Object> outputMap = new LinkedHashMap<String, Object>();

        Map<String, Object> etuValue = new HashMap<String, Object>();
        List<Map<String, Object>> university = new ArrayList<Map<String, Object>>();
        for (int i = 1; i <= 3; i++) {
            Map<String, Object> university_map = new HashMap<String, Object>();
            university_map.put("type", i);
            university_map.put("tuition", list_map.get("UNIVERSITY_FEE_" + (i)));
            university_map.put("living", list_map.get("UNIVERSITY_COST_" + (i)));
            university_map.put("yesrs", 0);		// 於APP判斷
            university.add(university_map);
        }
        etuValue.put("university", university);
        List<Map<String, Object>> institute = new ArrayList<Map<String, Object>>();
        for (int i = 1; i <= 3; i++) {
            Map<String, Object> institute_map = new HashMap<String, Object>();
            institute_map.put("type", i);
            institute_map.put("tuition", list_map.get("GRADUATED_FEE_" + (i)));
            institute_map.put("living", list_map.get("GRADUATED_COST_" + (i)));
            institute_map.put("yesrs", 0);		// 於APP判斷
            institute.add(institute_map);
        }
        etuValue.put("institute", institute);
        List<Map<String, Object>> doctor = new ArrayList<Map<String, Object>>();
        for (int i = 1; i <= 3; i++) {
            Map<String, Object> doctor_map = new HashMap<String, Object>();
            doctor_map.put("type", i);
            doctor_map.put("tuition", list_map.get("DOCTORAL_FEE_" + (i)));
            doctor_map.put("living", list_map.get("DOCTORAL_COST_" + (i)));
            doctor_map.put("yesrs", 0);		// 於APP判斷
            doctor.add(doctor_map);
        }
        etuValue.put("doctor", doctor);
        outputMap.put("etuValue", etuValue);
        //
        List<Map<String, Object>> category = new ArrayList<Map<String, Object>>();
        for (Map<String, Object> map : list2) {
            Map<String, Object> re_map = new HashMap<String, Object>();
            re_map.put("categoryId", map.get("PARAM_CODE"));
            re_map.put("categoryName", map.get("PARAM_NAME"));
            category.add(re_map);
        }
        outputMap.put("category", category);
        //
        List<Map<String, Object>> market = new ArrayList<Map<String, Object>>();
        for (Map<String, Object> map : list3) {
            Map<String, Object> re_map = new HashMap<String, Object>();
            re_map.put("marketId", map.get("PARAM_CODE"));
            re_map.put("marketName", map.get("PARAM_NAME"));
            market.add(re_map);
        }
        outputMap.put("market", market);
        
        XmlInfo xmlInfo = new XmlInfo();
        Map<String, String> minBuyAmtMap1 = xmlInfo.doGetVariable("SOT.NF_MIN_BUY_AMT_1", FormatHelper.FORMAT_3);
        Map<String, String> minBuyAmtMap2 = xmlInfo.doGetVariable("SOT.NF_MIN_BUY_AMT_2", FormatHelper.FORMAT_3);
        //台幣申購門檻(單筆)
        outputMap.put("onceLimitNTD", FPS200ApiMappingFactory.defaultIntValue(minBuyAmtMap1.get("TWD_FOREIGN")));
        //台幣申購門檻(定額)
        outputMap.put("monthlyLimitNTD", FPS200ApiMappingFactory.defaultIntValue(minBuyAmtMap2.get("TWD")));

        this.sendRtnObject(outputMap);
    }

    /**
     * API- 2.4.2 getPortfolioByPurpose 取得特定目的投資建議組合
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void getPortfolioByPurpose(Object body, IPrimitiveMap header) throws Exception{
        DataAccessManager dam = this.getDataAccessManager();
        Map<String, Object> inputMap = (Map<String, Object>) body;
        WorkStation ws = DataManager.getWorkStation(uuid);
        String userID = ws.getUser().getUserID();

        String planID = ObjectUtils.toString(inputMap.get("planId"));
        String custID = inputMap.get("custId").toString();
        String sppType = inputMap.get("sppType").toString().substring(0, 1);
        String riskValue = inputMap.get("riskValue").toString();
        String custRiskValue = inputMap.get("custRiskValue") == null ? riskValue.substring(0, 2) : inputMap.get("custRiskValue").toString();
        String sppName = inputMap.get("sppName").toString();
        BigDecimal investPeriod = new BigDecimal(inputMap.get("investPeriod").toString());
        BigDecimal onceAmt = inputMap.get("onceAmt") == null ? new BigDecimal(0) : new BigDecimal(inputMap.get("onceAmt").toString());
        BigDecimal monthlyAmt = inputMap.get("monthlyAmt") == null ? new BigDecimal(0) : new BigDecimal(inputMap.get("monthlyAmt").toString());
        BigDecimal targetAmt = inputMap.get("targetAmt") == null ? new BigDecimal(0) : new BigDecimal(inputMap.get("targetAmt").toString());
        Map<String, Object> amountInfo = inputMap.get("amountInfo") == null ? null : (Map<String, Object>) inputMap.get("amountInfo");

        FPS324InputVO inputVO = new FPS324InputVO();
        inputVO.setCustId(custID);
        // sa:先寫死
        Map<String, String> mapping1 = new HashMap<String, String>();
        mapping1.put("1", "EDUCATION");
        mapping1.put("2", "RETIRE");
        mapping1.put("3", "BUY_HOUSE");
        mapping1.put("4", "BUY_CAR");
        mapping1.put("5", "MARRY");
        mapping1.put("6", "OV_EDU");
        mapping1.put("7", "TRAVEL");
        mapping1.put("8", "OTHER");
        inputVO.setSppType(mapping1.get(sppType));
        inputVO.setPlanName(sppName);
        inputVO.setINV_PERIOD(investPeriod.intValue());
        inputVO.setINV_AMT_ONETIME(onceAmt.intValue());
        inputVO.setINV_AMT_PER_MONTH(monthlyAmt.intValue());
        inputVO.setINV_AMT_TYPE(inputMap.get("onceAmt") != null ? "1" : inputMap.get("monthlyAmt") == null ? "1" : "2");
        inputVO.setRISK_ATTR(custRiskValue);
        inputVO.setVOL_RISK_ATTR(riskValue);
        inputVO.setINV_AMT_TARGET(targetAmt.intValue());
        inputVO.setRiskType(riskValue.substring(0, 2));
        // create
        if (StringUtils.isBlank(planID)) {
            // not same
            QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
            queryCondition.setQueryString("SELECT * FROM TBFPS_PORTFOLIO_PLAN_SPP_HEAD WHERE CUST_ID = :cust_id and SPP_TYPE = :spp_type and INV_PLAN_NAME = :plan_name");
            queryCondition.setObject("cust_id", custID);
            queryCondition.setObject("spp_type", mapping1.get(sppType));
            queryCondition.setObject("plan_name", sppName);
            List<Map<String, Object>> list = dam.exeQuery(queryCondition);
            if (list.size() > 0)
                throw new APException("該筆資料已存在");

            planID = fps324.create(inputVO, userID);
            inputVO.setPlanID(planID);
        }
        // update
        else {
            inputVO.setPlanID(planID);
            fps324.update(inputVO, userID);
            fps324.delHelp(planID);
        }
        // create or update
        if ("1".equals(sppType) && amountInfo != null) {	// 子女教育
            Map<String, Object> university = (Map<String, Object>) amountInfo.get("university");
            Map<String, Object> institute = (Map<String, Object>) amountInfo.get("institute");
            Map<String, Object> doctor = (Map<String, Object>) amountInfo.get("doctor");
            //
            inputVO.setUNIVERSITY(StringUtils.equals("1.0", university.get("type").toString()) ? "1" : 
            					  StringUtils.equals("2.0", university.get("type").toString()) ? "2" : 
            				      StringUtils.equals("3.0", university.get("type").toString()) ? "3" : null);
            inputVO.setUNIVERSITY_FEE_EDU(university.get("tuition") == null ? new BigDecimal(0) : new BigDecimal(university.get("tuition").toString()));
            inputVO.setUNIVERSITY_FEE_LIFE(university.get("living") == null ? new BigDecimal(0) : new BigDecimal(university.get("living").toString()));
            inputVO.setUNIVERSITY_YEAR(university.get("years") == null ? new BigDecimal(0) : new BigDecimal(university.get("years").toString()));
            //
            inputVO.setMASTER(StringUtils.equals("1.0", institute.get("type").toString()) ? "1" : 
            				  StringUtils.equals("2.0", institute.get("type").toString()) ? "2" : 
            				  StringUtils.equals("3.0", institute.get("type").toString()) ? "3" : null);
            inputVO.setMASTER_FEE_EDU(institute.get("tuition") == null ? new BigDecimal(0) : new BigDecimal(institute.get("tuition").toString()));
            inputVO.setMASTER_FEE_LIFE(institute.get("living") == null ? new BigDecimal(0) : new BigDecimal(institute.get("living").toString()));
            inputVO.setMASTER_YEAR(institute.get("years") == null ? new BigDecimal(0) : new BigDecimal(institute.get("years").toString()));
            //
            inputVO.setPHD(StringUtils.equals("1.0", doctor.get("type").toString()) ? "1" : 
            			   StringUtils.equals("2.0", doctor.get("type").toString()) ? "2" : 
            			   StringUtils.equals("3.0", doctor.get("type").toString()) ? "3" : null);
            inputVO.setPHD_FEE_EDU(doctor.get("tuition") == null ? new BigDecimal(0) : new BigDecimal(doctor.get("tuition").toString()));
            inputVO.setPHD_FEE_LIFE(doctor.get("living") == null ? new BigDecimal(0) : new BigDecimal(doctor.get("living").toString()));
            inputVO.setPHD_YEAR(doctor.get("years") == null ? new BigDecimal(0) : new BigDecimal(doctor.get("years").toString()));

            fps324.saveFPS321(inputVO);
            
        } else if ("2".equals(sppType) && amountInfo != null) {		// 退休
            inputVO.setRETIREMENT_AGE(amountInfo.get("retireAge") == null ? new BigDecimal(0) : new BigDecimal(amountInfo.get("retireAge").toString()));
            inputVO.setRETIRE_FEE(amountInfo.get("retireSpend") == null ? new BigDecimal(0) : new BigDecimal(amountInfo.get("retireSpend").toString()));
            inputVO.setHERITAGE(amountInfo.get("inheritAmt") == null ? new BigDecimal(0) : new BigDecimal(amountInfo.get("inheritAmt").toString()));
            
            if (amountInfo.get("socialInsurance") != null) {
            	Map<String, Object> socialInsurance = (Map<String, Object>) amountInfo.get("socialInsurance");
            	inputVO.setSOCIAL_INS_FEE_1(socialInsurance.get("monthly") == null ? new BigDecimal(0) : new BigDecimal(socialInsurance.get("monthly").toString()));
            	inputVO.setSOCIAL_INS_FEE_2(socialInsurance.get("once") == null ? new BigDecimal(0) : new BigDecimal(socialInsurance.get("once").toString()));            	
            }
            if (amountInfo.get("socialWelfare") != null) {
            	Map<String, Object> socialWelfare = (Map<String, Object>) amountInfo.get("socialWelfare");
            	inputVO.setSOCIAL_WELFARE_FEE_1(socialWelfare.get("monthly") == null ? new BigDecimal(0) : new BigDecimal(socialWelfare.get("monthly").toString()));
            	inputVO.setSOCIAL_WELFARE_FEE_2(socialWelfare.get("once") == null ? new BigDecimal(0) : new BigDecimal(socialWelfare.get("once").toString()));            	
            }
            if (amountInfo.get("businessInsurance") != null) {
            	Map<String, Object> businessInsurance = (Map<String, Object>) amountInfo.get("businessInsurance");
            	inputVO.setCOMM_INS_FEE_1(businessInsurance.get("monthly") == null ? new BigDecimal(0) : new BigDecimal(businessInsurance.get("monthly").toString()));
            	inputVO.setCOMM_INS_FEE_2(businessInsurance.get("once") == null ? new BigDecimal(0) : new BigDecimal(businessInsurance.get("once").toString()));            	
            }
            if (amountInfo.get("otherInsurance") != null) {
            	Map<String, Object> otherInsurance = (Map<String, Object>) amountInfo.get("otherInsurance");
            	inputVO.setOTHER_FEE_1(otherInsurance.get("monthly") == null ? new BigDecimal(0) : new BigDecimal(otherInsurance.get("monthly").toString()));
            	inputVO.setOTHER_FEE_2(otherInsurance.get("once") == null ? new BigDecimal(0) : new BigDecimal(otherInsurance.get("once").toString()));            	
            }
            fps324.saveFPS322(inputVO);
        }
        //
        Map<String, Object> outputMap = new HashMap<String, Object>();
        outputMap.put("planId", inputVO.getPlanID());

        // getMainQuery no need substring(0, 2) rechange
        inputVO.setRiskType(riskValue);
        List<Map<String, Object>> product = fps324.getMainQuery(inputVO);
        List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        for (Map<String, Object> map : product) {
            BigDecimal ntd = (onceAmt.add(monthlyAmt))
            						.multiply(new BigDecimal(ObjectUtils.toString(map.get("INV_PERCENT"))))
                                    .divide(new BigDecimal(100), BigDecimal.ROUND_HALF_UP);
            Map<String, Object> p = productFactory.getMapFromDBValue(map, FPSType.SPP);
            p.put("amountNTD", ntd);
            data.add(p);
        }
        outputMap.put("product", data);

        this.sendRtnObject(outputMap);
    }

    /**
     * API- 2.4.3 saveFinancialPurposePlan 儲存特定目的理財規劃資料及取得配置、模擬及績效表現 (與績效共用)
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void saveFinancialPurposePlan(Object body, IPrimitiveMap header) throws Exception{
        DataAccessManager dam = this.getDataAccessManager();
        Map<String, Object> inputMap = (Map<String, Object>) body;
        WorkStation ws = DataManager.getWorkStation(uuid);
        String userID = ws.getUser().getUserID();

        String planID = inputMap.get("planId").toString();
        Boolean isPerformance = new Boolean(inputMap.get("isPerformance").toString());
        String custID = inputMap.get("custId").toString();
        List<Map<String, Object>> old_product = (List<Map<String, Object>>) inputMap.get("product");
        List<Map<String, Object>> product = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> returnPrd = new ArrayList<Map<String, Object>>();
        // for 計算績效模擬＆歷史績效表現
        List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();

        TBFPS_PORTFOLIO_PLAN_SPP_HEADVO hvo = (TBFPS_PORTFOLIO_PLAN_SPP_HEADVO) dam.findByPKey(TBFPS_PORTFOLIO_PLAN_SPP_HEADVO.TABLE_UID, planID);
        if (hvo != null) {
            hvo.setCUST_ID(custID);
            dam.update(hvo);
        }
        if (fps324.deletePlanDetails(dam, planID)) {
            for (Map<String, Object> map : old_product) {
                FPS324PrdInputVO prd = new FPS324PrdInputVO();
                Map<String, Object> reMaped = new HashMap<String, Object>();
                Map<String, Object> returnMap = new HashMap<String, Object>();
                // for 計算績效模擬＆歷史績效表現
                Map<String, Object> retMap = new HashMap<String, Object>();
                
                String prdID = ObjectUtils.toString(map.get("productId"));
                String pType= ObjectUtils.toString(map.get("productType"));
                String trustCurr = ObjectUtils.toString(map.get("pCurrency"));
                BigDecimal amount = new BigDecimal(ObjectUtils.toString(map.get("amount")));
                BigDecimal amountNTD = new BigDecimal(ObjectUtils.toString(map.get("amountNTD")));
                BigDecimal ratio = new BigDecimal(ObjectUtils.toString(map.get("ratio")));
                String txnType = ObjectUtils.toString(map.get("txnType"));
                String invType = ObjectUtils.toString(map.get("invType"));
                String targets = ObjectUtils.toString(map.get("targets"));
                String certificateID = ObjectUtils.toString(map.get("certificateID"));
                
                if ("0.0".equals(ObjectUtils.toString(map.get("stockAmount")))) {
                	//不為庫存才可新增
                	prd.setSEQNO("".equals(ObjectUtils.toString(map.get("SEQNO"))) ? null : new BigDecimal(map.get("SEQNO").toString()));
                    prd.setPRD_ID(prdID);
                    prd.setPTYPE(pType);
                    prd.setRISK_TYPE(ObjectUtils.toString(map.get("riskLevel")));
                    prd.setCURRENCY_TYPE(ObjectUtils.toString(map.get("currCd")));
                    prd.setTRUST_CURR(trustCurr);
                    prd.setMARKET_CIS("0".equals(ObjectUtils.toString(map.get("CIS_3M"))) ? "B" : "1".equals(ObjectUtils.toString(map.get("CIS_3M"))) ? "H" : "S");
                    prd.setPURCHASE_ORG_AMT(amount);
                    prd.setPURCHASE_TWD_AMT(amountNTD);
                    prd.setPORTFOLIO_RATIO(ratio);
                    prd.setTXN_TYPE(txnType);
                    prd.setINV_TYPE(invType);
                    prd.setTARGETS(targets);
                    fps324.create(dam, prd, planID);
                }
                reMaped.put("PRD_ID", prdID);
                reMaped.put("PTYPE", pType);
                reMaped.put("TRUST_CURR", trustCurr);
                reMaped.put("PURCHASE_ORG_AMT", amount);
                reMaped.put("PURCHASE_TWD_AMT", amountNTD);
                reMaped.put("PORTFOLIO_RATIO", ratio);
                reMaped.put("TXN_TYPE", txnType);
                reMaped.put("INV_TYPE", invType);
                reMaped.put("TARGETS", targets);
                product.add(reMaped);
                
                returnMap.put("PRD_ID", prdID);
                returnMap.put("PRD_TYPE", pType);
                returnMap.put("TRUST_CURR", trustCurr);
                returnMap.put("PURCHASE_ORG_AMT", amount);
                returnMap.put("PURCHASE_TWD_AMT", amountNTD);
                returnMap.put("WEIGHT", ratio.divide(new BigDecimal(100)));
                returnMap.put("TXN_TYPE", txnType);
                returnMap.put("INV_TYPE", invType);
                returnMap.put("TARGETS", targets);
                returnPrd.add(returnMap);
                
                // PRD_ID, PTYPE, PURCHASE_TWD_AMT, TARGETS, INV_TYPE, ORDER_STATUS 
                retMap.put("PRD_ID", prdID);
                retMap.put("PTYPE", pType);
                retMap.put("PRD_TYPE", pType);		//計算共同區間用（每個地方變數取不同名字是想逼死誰!?)
                retMap.put("PURCHASE_TWD_AMT", amountNTD);
                retMap.put("TARGETS", targets);
                retMap.put("INV_TYPE", invType);
                retMap.put("ORDER_STATUS", certificateID != "" ? "Y" : null);
                ret.add(retMap);
            }
        }
        //
        Map<String, Object> outputMap = new HashMap<String, Object>();
        outputMap.put("planId", planID);

        // 績效模擬金額
//        List<Map<String, Object>> ret = getRet(dam, planID);
        try {
            if (hvo != null && ret.size() != 0) {
                // fps323 inquire
                // 前置作業準備
                Map<String, Object> afterPrepareDataMap = FPSUtils.beforeDoPortRtnSim(dam, ret, custID, planID);
                logger.info(new Gson().toJson(ret));
                logger.info("GET SPP_ID: " + planID);
                
                // 取得剩餘月份
                int remainingMonth = FPSUtils.getRemainingMonth(FPSUtils.getPurchasedDate(dam, planID), hvo.getINV_PERIOD().intValue());
                logger.info("GET REMAINING MONTH: " + remainingMonth);

                // 取得分組資料
                List<Map<String, Object>>[] purchasedListArray = (List<Map<String, Object>>[]) afterPrepareDataMap.get("purchasedListArray");

                // 取得單筆總投入 & 定額總投入
                BigDecimal[] purchasedValueArray = (BigDecimal[]) afterPrepareDataMap.get("purchasedValueArray");
                BigDecimal totalMoney = purchasedValueArray[0].add(purchasedValueArray[1].multiply(new BigDecimal(remainingMonth)));
                logger.info("GET SINGLE & PERIOD TOTAL VALUE: " + totalMoney);
                
                String[] invalid = FPSUtils.checkValidYear(dam, ret, 1, null);
                if (invalid.length == 0) {
                    GenericMap pt = fpsjlb.getPortRtnSim(purchasedListArray, purchasedValueArray, hvo.getINV_PERIOD().intValue(), 36, 120, remainingMonth);
//                    GenericMap pt = fpsjlb.getPortRtnSim(purchasedListArray, purchasedValueArray, inputVO.getYear(), 36, 120, remainingMonth);
                    logger.debug("FPS323 result: " + Arrays.deepToString((Double[]) pt.get("resultArray")));
                    logger.info("FPS323 result: " + Arrays.deepToString((Double[]) pt.get("resultArray")));
                    
                    ((Double[]) pt.get("resultArray"))[3] = totalMoney.doubleValue();
                    
                    for (Double oneD : (Double[]) pt.get("resultArray")) {
                        if (Double.isNaN(oneD)) {
                            outputMap.put("performance", new ArrayList());
                            return;
                        }
                    }
                    outputMap.put("performance", pt.get("resultArray"));
                } else {
                    outputMap.put("performance", new ArrayList());
                }
            } else {
                outputMap.put("performance", new ArrayList());
            }
        } catch (Exception e) {
            outputMap.put("performance", new ArrayList());
        }

        // fps340
        // set init key map for checkValidYear
        Map<String, String> keyMap = new HashMap<String, String>();
        keyMap.put("prodType", "PTYPE");
        keyMap.put("prodID", "PRD_ID");
        keyMap.put("targets", "TARGETS");

        List<Map<String, Object>> formatedStockLs = formatWeightMap(ret);
//        List<Map<String, Object>> old_hislist = FPSUtils.historyPerformance(dam, planID);
        if (product == null || product.size() == 0) {
        	List<Map<String, Object>> prdList = fps330.calVolatility(planID, dam);
        	if (FPSUtils.checkValidYear(dam, prdList, 1, null).length == 0) {
        		// 歷史年度平均報酬率
                BigDecimal yRate = FPSUtils.getYRate(dam, formatedStockLs, 120, 12);
                outputMap.put("avgORI", yRate == null ? 0 : yRate.doubleValue());
                // 波動率
                BigDecimal volatility = FPSUtils.getStandardDeviation(dam, formatedStockLs, 36, 12, false);
                outputMap.put("volatility", volatility == null ? 0 : volatility.doubleValue());
                // 歷史績效
        		List<Map<String, Object>> performanceHis = new ArrayList<Map<String, Object>>();
                List<Map<String, Object>> hisList = FPSUtils.getReturnAnnM(dam, prdList, 10, 1);
                
                for (Map<String, Object> map : hisList) {
            		Map<String, Object> new_map = new HashMap<String, Object>();
            		new_map.put("year", map.get("YEAR"));
            		new_map.put("ori", map.get("RETURN_ANN_M"));
            		performanceHis.add(new_map);
            	}
            	outputMap.put("performanceHis", performanceHis); 
        	} else {
        		outputMap.put("avgORI", 0);
                outputMap.put("volatility", 0);
                outputMap.put("performanceHis", new ArrayList<Float>());
        	}
        } else {
        	if (FPSUtils.checkValidYear(dam, returnPrd, 1, null).length == 0) {
        		// 歷史年度平均報酬率
                BigDecimal yRate = FPSUtils.getYRate(dam, formatedStockLs, 120, 12);
                outputMap.put("avgORI", yRate == null ? 0 : yRate.doubleValue());
                // 波動率
                BigDecimal volatility = FPSUtils.getStandardDeviation(dam, formatedStockLs, 36, 12, false);
                outputMap.put("volatility", volatility == null ? 0 : volatility.doubleValue());
                
                // 歷史績效
                List<Map<String, Object>> performanceHis = new ArrayList<Map<String, Object>>();
                List<Map<String, Object>> hisList = FPSUtils.getReturnAnnM(dam, returnPrd, 10, 1);
                if (hisList != null) {
                	for (Map<String, Object> map : hisList) {
                		Map<String, Object> new_map = new HashMap<String, Object>();
                		new_map.put("year", map.get("YEAR"));
                		new_map.put("ori", map.get("RETURN_ANN_M"));
                		performanceHis.add(new_map);
                	}
                	outputMap.put("performanceHis", performanceHis);                 	
                } else {
                	outputMap.put("avgORI", 0);
                    outputMap.put("volatility", 0);
                    outputMap.put("performanceHis", new ArrayList<Float>());
                }
        	} else {
        		outputMap.put("avgORI", 0);
                outputMap.put("volatility", 0);
                outputMap.put("performanceHis", new ArrayList<Float>());
        	}
        }
        
        // fps400 inquire
        if (isPerformance) {
            FPS400InputVO fps400vo = new FPS400InputVO();
            fps400vo.setCustID(custID);
            fps400vo.setPlanID(planID);

            List<Map<String, Object>> fps400_list = fps400.api_inquire(fps400vo);
            List<Map<String, Object>> summary = new ArrayList<Map<String, Object>>();
            for (Map<String, Object> map400 : fps400_list) {
                Map<String, Object> reMaped = new HashMap<String, Object>();
                reMaped.put("name", map400.get("INV_PLAN_NAME") == null ? "" : map400.get("INV_PLAN_NAME"));
                reMaped.put("goalFunds", map400.get("INV_AMT_TARGET") == null ? 0 : map400.get("INV_AMT_TARGET"));
                reMaped.put("investAmt", map400.get("INV_AMT_CURRENT") == null ? 0 : map400.get("INV_AMT_CURRENT"));
                reMaped.put("marketValue", map400.get("MARKET_VALUE") == null ? 0 : map400.get("MARKET_VALUE"));
                reMaped.put("returnRate", map400.get("RETURN_RATE") == null ? 0 : map400.get("RETURN_RATE"));
                reMaped.put("goalAmt", map400.get("AMT_TARGET") == null ? 0 : map400.get("AMT_TARGET"));
                reMaped.put("achievementRate", map400.get("HIT_RATE") == null ? 0 : map400.get("HIT_RATE"));
                reMaped.put("progressDesc", ObjectUtils.toString(map400.get("HIT_RATE_DESC")));

                summary.add(reMaped);
            }
            outputMap.put("summary", summary);
        } else {
            outputMap.put("summary", new ArrayList());
        }

        this.sendRtnObject(outputMap);
    }

    /**
     * API- 2.4.5 updateStatus 更新規劃書狀態(特定與非特定目的共用)
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void updateStatus(Object body, IPrimitiveMap header) throws Exception{
        DataAccessManager dam = this.getDataAccessManager();
        Map<String, Object> inputMap = (Map<String, Object>) body;

        String planID = inputMap.get("planId").toString();
        String custID = ObjectUtils.toString(inputMap.get("custId"));
        String status = inputMap.get("status").toString();

        TBFPS_PORTFOLIO_PLAN_INV_HEADVO invVO = (TBFPS_PORTFOLIO_PLAN_INV_HEADVO) dam.findByPKey(TBFPS_PORTFOLIO_PLAN_INV_HEADVO.TABLE_UID, planID);
        if (invVO != null) {
        	//全資產
        	invVO.setPLAN_STATUS("1".equals(status) ? "PRINT_REJECT" : "PRINT_THINK");
            dam.update(invVO);
        } else {
        	TBFPS_PORTFOLIO_PLAN_SPP_HEADVO sppVO = (TBFPS_PORTFOLIO_PLAN_SPP_HEADVO) dam.findByPKey(TBFPS_PORTFOLIO_PLAN_SPP_HEADVO.TABLE_UID, planID);
        	if (sppVO != null) {
        		//目標理財
        		sppVO.setPLAN_STATUS("1".equals(status) ? "PRINT_REJECT" : "PRINT_THINK");
                dam.update(sppVO);
            } else
            	throw new APException("ehl_01_common_009");
        }
        this.sendRtnObject(null);
    }

    /**
     * API- 2.4.6 queryHistoryFinancialPlan 查詢理財規劃歷史規劃(特定與非特定目的共用)
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void queryHistoryFinancialPlan(Object body, IPrimitiveMap header) throws Exception{
        DataAccessManager dam = this.getDataAccessManager();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Map<String, Object> inputMap = (Map<String, Object>) body;
        XmlInfo xmlInfo = new XmlInfo();
        Map<String, String> statusMap = xmlInfo.doGetVariable("FPS.VALID_FLAG", FormatHelper.FORMAT_3);

        Boolean isPurpose = new Boolean(inputMap.get("isPurpose").toString().equals("1"));
        String custID = inputMap.get("custId").toString();
        Date startDate = null;
        Date endDate = null;
        if (StringUtils.isNotBlank(ObjectUtils.toString(inputMap.get("startDate"))))
            startDate = sdf.parse(ObjectUtils.toString(inputMap.get("startDate")));
        if (StringUtils.isNotBlank(ObjectUtils.toString(inputMap.get("endDate"))))
            endDate = sdf.parse(ObjectUtils.toString(inputMap.get("endDate")));
        String status = ObjectUtils.toString(inputMap.get("status"));
        String isInvaild = ObjectUtils.toString(inputMap.get("isInvaild"));

        Map<String, Object> outputMap = new HashMap<String, Object>();
        outputMap.put("custId", custID);
        // fps350
        if (isPurpose) {
            FPS350InputVO fps350vo = new FPS350InputVO();
            fps350vo.setCustID(custID);
            fps350vo.setPlanStatus(status);
            fps350vo.setIsDisable(isInvaild);
            fps350vo.setSD(startDate);
            fps350vo.setED(endDate);

            List<Map<String, Object>> fps350_list = fps350.api_inquire(fps350vo);
            List<Map<String, Object>> portfolio = new ArrayList<Map<String, Object>>();
            for (Map<String, Object> map350 : fps350_list) {
                Map<String, Object> reMaped = new HashMap<String, Object>();
                reMaped.put("planId", FPS200ApiMappingFactory.defaultStringValue(map350.get("PLAN_ID")));
                reMaped.put("status", FPS200ApiMappingFactory.defaultStringValue(map350.get("PLAN_STATUS")));
                reMaped.put("planName", FPS200ApiMappingFactory.defaultStringValue(map350.get("INV_PLAN_NAME")));
                reMaped.put("sppType", FPS200ApiMappingFactory.defaultStringValue(getSppTypeText(ObjectUtils.toString(map350.get("SPP_TYPE")))));
                reMaped.put("isInvaild", "Y".equals(map350.get("VALID_FLAG")) ? false : true);
                reMaped.put("invaildDesc", FPS200ApiMappingFactory.defaultStringValue(statusMap.get(ObjectUtils.toString(map350.get("VALID_FLAG")))));
                reMaped.put("date", map350.get("CREATETIME"));
                reMaped.put("updateDate", map350.get("LASTUPDATE"));
                reMaped.put("braName", FPS200ApiMappingFactory.defaultStringValue(map350.get("BRANCH_NAME")));
                reMaped.put("empName", FPS200ApiMappingFactory.defaultStringValue(map350.get("EMP_NAME")));
                reMaped.put("tradeCount", FPS200ApiMappingFactory.defaultStringValue(map350.get("TRADECOUNT")));
                reMaped.put("step", FPS200ApiMappingFactory.defaultStringValue(map350.get("STEP_STAUS"), "0"));
                // file
                List<Map<String, Object>> file = new ArrayList<Map<String, Object>>();
                FPS350InputVO fps350filevo = new FPS350InputVO();
                fps350filevo.setPlanID(map350.get("PLAN_ID").toString());
                List<Map<String, Object>> file_list = fps350.api_inquireProposal(fps350filevo);
                for (Map<String, Object> file_map : file_list) {
                    Map<String, Object> reMaped2 = new HashMap<String, Object>();
                    reMaped2.put("date", file_map.get("BUILDTIME"));
                    if (!"無".equals(file_map.get("IS_EMAIL")) && !"無".equals(file_map.get("IS_PRINT")))
                        reMaped2.put("status", "已轉寄, 已列印");
                    else if (!"無".equals(file_map.get("IS_EMAIL")))
                        reMaped2.put("status", "已轉寄");
                    else if (!"無".equals(file_map.get("IS_PRINT")))
                        reMaped2.put("status", "已列印");
                    reMaped2.put("fileCode", file_map.get("SEQ_NO"));
                    file.add(reMaped2);
                }
                reMaped.put("file", file);
                portfolio.add(reMaped);
            }
            outputMap.put("portfolio", portfolio);
        }
        // fps250
        else {
            FPS250InputVO fps250vo = new FPS250InputVO();
            fps250vo.setCustID(custID);
            fps250vo.setPlanStatus(status);
            fps250vo.setDisable(isInvaild);
            fps250vo.setSD(startDate);
            fps250vo.setED(endDate);

            List<Map<String, Object>> fps250_list = fps250.getHisQuery(dam, fps250vo);
            List<Map<String, Object>> portfolio = new ArrayList<Map<String, Object>>();
            for (Map<String, Object> map250 : fps250_list) {
                Map<String, Object> reMaped = new HashMap<String, Object>();
                reMaped.put("planId", FPS200ApiMappingFactory.defaultStringValue(map250.get("PLAN_ID")));
                reMaped.put("status", FPS200ApiMappingFactory.defaultStringValue(map250.get("PLAN_STATUS")));
                reMaped.put("isInvaild", "Y".equals(map250.get("VALID_FLAG")) ? false : true);
                reMaped.put("invaildDesc", FPS200ApiMappingFactory.defaultStringValue(statusMap.get(ObjectUtils.toString(map250.get("VALID_FLAG")))));
                reMaped.put("date", map250.get("CREATETIME"));
                reMaped.put("updateDate", map250.get("LASTUPDATE"));
                reMaped.put("braName", FPS200ApiMappingFactory.defaultStringValue(map250.get("BRANCH_NAME")));
                reMaped.put("empName", FPS200ApiMappingFactory.defaultStringValue(map250.get("EMP_NAME")));
                reMaped.put("tradeCount", FPS200ApiMappingFactory.defaultStringValue(map250.get("COUNTINV")));
                reMaped.put("step", FPS200ApiMappingFactory.defaultStringValue(map250.get("PLAN_STEP"), "1"));
                // file
                List<Map<String, Object>> file = new ArrayList<Map<String, Object>>();
                FPS250InputVO fps250filevo = new FPS250InputVO();
                fps250filevo.setPlanID(map250.get("PLAN_ID").toString());
                List<Map<String, Object>> file_list = fps250.api_inquireProposal(fps250filevo);
                for (Map<String, Object> file_map : file_list) {
                    Map<String, Object> reMaped2 = new HashMap<String, Object>();
                    reMaped2.put("date", file_map.get("CREATETIME"));
                    if (((BigDecimal) file_map.get("IS_EMAIL")).compareTo(new BigDecimal(0)) > 0 && ((BigDecimal) file_map.get("IS_PRINT")).compareTo(new BigDecimal(0)) > 0)
                        reMaped2.put("status", "已轉寄, 已列印");
                    else if (((BigDecimal) file_map.get("IS_EMAIL")).compareTo(new BigDecimal(0)) > 0)
                        reMaped2.put("status", "已轉寄");
                    else if (((BigDecimal) file_map.get("IS_PRINT")).compareTo(new BigDecimal(0)) > 0)
                        reMaped2.put("status", "已列印");
                    else
                        reMaped2.put("status", "");
                    reMaped2.put("fileCode", file_map.get("SEQ_NO"));
                    file.add(reMaped2);
                }
                reMaped.put("file", file);
                portfolio.add(reMaped);
            }
            outputMap.put("portfolio", portfolio);
        }
        
        QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        queryCondition.setQueryString("SELECT PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'FPS.INS_AVERAGE' AND PARAM_CODE = :code");
        queryCondition.setObject("code", custID.substring(1, 2));
        List<Map<String, Object>> list = dam.exeQuery(queryCondition);
        if (list.size() > 0) {
        	outputMap.put("avgLife", new GenericMap(list.get(0)).getBigDecimal("PARAM_NAME"));            	
        } else {
        	outputMap.put("avgLife", new BigDecimal(0));
        }
        
        this.sendRtnObject(outputMap);
    }

    /**
     * API- 2.4.7 getFinancialPlanPurposeData 取得目標理財規劃資料
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void getFinancialPlanPurposeData(Object body, IPrimitiveMap header) throws Exception{
        DataAccessManager dam = this.getDataAccessManager();
        Map<String, Object> inputMap = (Map<String, Object>) body;

        String planID = inputMap.get("planId").toString();
        String custID = inputMap.get("custId").toString();
        String riskType = inputMap.get("riskLevel").toString();

        Map<String, Object> outputMap = new HashMap<String, Object>();
        outputMap.put("planId", planID);
        TBCRM_CUST_MASTVO cvo = (TBCRM_CUST_MASTVO) dam.findByPKey(TBCRM_CUST_MASTVO.TABLE_UID, custID);
        if (cvo == null)
            throw new APException("客戶不存在");
        TBFPS_PORTFOLIO_PLAN_SPP_HEADVO hvo = (TBFPS_PORTFOLIO_PLAN_SPP_HEADVO) dam.findByPKey(TBFPS_PORTFOLIO_PLAN_SPP_HEADVO.TABLE_UID, planID);
        if (hvo != null) {
            outputMap.put("sppType", getSppTypeText(hvo.getSPP_TYPE()));
            outputMap.put("sppName", hvo.getINV_PLAN_NAME());
            outputMap.put("investPeriod", hvo.getINV_PERIOD());
            outputMap.put("onceAmt", hvo.getINV_AMT_ONETIME().intValue());
            outputMap.put("monthlyAmt", hvo.getINV_AMT_PER_MONTH().intValue());
            outputMap.put("riskValue", hvo.getVOL_RISK_ATTR());
            outputMap.put("amount", hvo.getINV_AMT_TARGET().intValue());
        }
        Map<String, Object> amountInfo = new HashMap<String, Object>();

        if ("1".equals(outputMap.get("sppType"))) {		//教育
        	Map<String, Object> university = new HashMap<String, Object>();
        	Map<String, Object> institute = new HashMap<String, Object>();
        	Map<String, Object> doctor = new HashMap<String, Object>();
        	
            TBFPS_SPP_PLAN_HELP_EVO evo = (TBFPS_SPP_PLAN_HELP_EVO) dam.findByPKey(TBFPS_SPP_PLAN_HELP_EVO.TABLE_UID, planID);
            if (evo != null) {
            	university.put("type", evo.getUNIVERSITY() == null ? 0 : Integer.parseInt(evo.getUNIVERSITY()));
            	university.put("tuition", evo.getUNIVERSITY_FEE_EDU() == null ? 0 : evo.getUNIVERSITY_FEE_EDU());
            	university.put("living", evo.getUNIVERSITY_FEE_LIFE() == null ? 0 : evo.getUNIVERSITY_FEE_LIFE());
            	university.put("years", evo.getUNIVERSITY_YEAR() == null ? 0 : evo.getUNIVERSITY_YEAR());
            	institute.put("type", evo.getMASTER() == null ? 0 : Integer.parseInt(evo.getMASTER()));
            	institute.put("tuition", evo.getMASTER_FEE_EDU() == null ? 0 : evo.getMASTER_FEE_EDU());
            	institute.put("living", evo.getMASTER_FEE_LIFE() == null ? 0 : evo.getMASTER_FEE_LIFE());
            	institute.put("years", evo.getMASTER_YEAR() == null ? 0 : evo.getMASTER_YEAR());
            	doctor.put("type", evo.getPHD() == null ? 0 : Integer.parseInt(evo.getPHD()));
            	doctor.put("tuition", evo.getPHD_FEE_EDU() == null ? 0 : evo.getPHD_FEE_EDU());
            	doctor.put("living", evo.getPHD_FEE_LIFE() == null ? 0 : evo.getPHD_FEE_LIFE());
            	doctor.put("years", evo.getPHD_YEAR() == null ? 0 : evo.getPHD_YEAR());            	
            } else {
            	university.put("type", 0);
            	university.put("tuition", 0);
            	university.put("living", 0);
            	university.put("years", 0);
            	institute.put("type", 0);
            	institute.put("tuition", 0);
            	institute.put("living", 0);
            	institute.put("years", 0);
            	doctor.put("type", 0);
            	doctor.put("tuition", 0);
            	doctor.put("living", 0);
            	doctor.put("years", 0);
            }

            amountInfo.put("university", university);
            amountInfo.put("institute", institute);
            amountInfo.put("doctor", doctor);
            
        } else if ("2".equals(outputMap.get("sppType"))) {		//退休
            TBFPS_SPP_PLAN_HELP_RVO rvo = (TBFPS_SPP_PLAN_HELP_RVO) dam.findByPKey(TBFPS_SPP_PLAN_HELP_RVO.TABLE_UID, planID);
            amountInfo.put("retireAge", rvo == null ? 0 : (rvo.getRETIREMENT_AGE() == null ? 0 : rvo.getRETIREMENT_AGE()));
            
            QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
            queryCondition.setQueryString("SELECT PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'FPS.INS_AVERAGE' AND PARAM_CODE = :code");
            queryCondition.setObject("code", cvo.getGENDER());
            List<Map<String, Object>> list = dam.exeQuery(queryCondition);
            if (list.size() > 0)
            	amountInfo.put("avgLife", new GenericMap(list.get(0)).getBigDecimal("PARAM_NAME"));
            else
            	amountInfo.put("avgLife", new BigDecimal(0));
            
            amountInfo.put("retireSpend", rvo == null ? 0 : (rvo.getRETIRE_FEE() == null ? 0 : rvo.getRETIRE_FEE()));
            amountInfo.put("inheritAmt", rvo == null ? 0 : (rvo.getHERITAGE() == null ? 0 : rvo.getHERITAGE()));

            Map<String, Object> socialInsurance = new HashMap<String, Object>();
            socialInsurance.put("monthly", rvo == null ? 0 : (rvo.getSOCIAL_INS_FEE_1() == null ? 0 : rvo.getSOCIAL_INS_FEE_1()));
            socialInsurance.put("once", rvo == null ? 0 : (rvo.getSOCIAL_INS_FEE_2() == null ? 0 : rvo.getSOCIAL_INS_FEE_2()));
            amountInfo.put("socialInsurance", socialInsurance);

            Map<String, Object> socialWelfare = new HashMap<String, Object>();
            socialWelfare.put("monthly", rvo == null ? 0 : (rvo.getSOCIAL_WELFARE_FEE_1() == null ? 0 : rvo.getSOCIAL_WELFARE_FEE_1()));
            socialWelfare.put("once", rvo == null ? 0 : (rvo.getSOCIAL_WELFARE_FEE_2() == null ? 0 : rvo.getSOCIAL_WELFARE_FEE_2()));
            amountInfo.put("socialWelfare", socialWelfare);

            Map<String, Object> businessInsurance = new HashMap<String, Object>();
            businessInsurance.put("monthly", rvo == null ? 0 : (rvo.getCOMM_INS_FEE_1() == null ? 0 : rvo.getCOMM_INS_FEE_1()));
            businessInsurance.put("once", rvo == null ? 0 : (rvo.getCOMM_INS_FEE_2() == null ? 0 : rvo.getCOMM_INS_FEE_2()));
            amountInfo.put("businessInsurance", businessInsurance);

            Map<String, Object> otherInsurance = new HashMap<String, Object>();
            otherInsurance.put("monthly", rvo == null ? 0 : (rvo.getOTHER_FEE_1() == null ? 0 : rvo.getOTHER_FEE_1()));
            otherInsurance.put("once", rvo == null ? 0 : (rvo.getOTHER_FEE_2() == null ? 0 : rvo.getOTHER_FEE_2()));
            amountInfo.put("otherInsurance", otherInsurance);
        }
        
        setPurposeInfo(ObjectUtils.toString(outputMap.get("sppType")), amountInfo);
        outputMap.put("amountInfo", amountInfo);
        //
        Boolean isReco = false;
        FPS324InputVO inputVO = new FPS324InputVO();
        inputVO.setCustId(custID);
        inputVO.setPlanID(planID);
        inputVO.setRiskType(riskType);
        List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        
        List<Map<String, Object>> product = fps324.getHistory(inputVO);
        if (product.size() <= 0) {
        	inputVO.setRiskType(hvo.getVOL_RISK_ATTR());
        	inputVO.setINV_AMT_TYPE(hvo.getINV_AMT_ONETIME() != null ? "1" : "2");
            product = fps324.getMainQuery(inputVO);
            isReco = true;
        }
        for (Map<String, Object> map : product) {
            // todo
            Map<String, Object> data_map = new HashMap<String, Object>();

            data_map.put("productId", map.get("PRD_ID"));
            data_map.put("productName", map.get("PRD_CNAME"));
            data_map.put("invProductType", "3");
            data_map.put("productType", map.get("PTYPE"));
            data_map.put("categoryId", map.get("FUND_TYPE") == null ? "" : map.get("FUND_TYPE"));
            data_map.put("marketId", map.get("MF_MKT_CAT") == null ? "" : map.get("MF_MKT_CAT"));
            data_map.put("riskLevel", map.get("RISK_TYPE") == null ? "--" : map.get("RISK_TYPE"));
            data_map.put("isRecommand", map.get("MAIN_PRD"));
            data_map.put("currCd", map.get("CURRENCY_TYPE"));
            data_map.put("pCurrency", map.get("TRUST_CURR") == null ? "" : map.get("TRUST_CURR"));
            
            int outlook = 3;
            if ("B".equals(map.get("CIS_3M"))) {
            	outlook = 0;
            } else if ("H".equals(map.get("CIS_3M"))) {
            	outlook = 1;
            } else if ("S".equals(map.get("CIS_3M"))) {
            	outlook = 2;
            } else {
            	outlook = 3;
            }
            data_map.put("outlook", outlook);
            
            data_map.put("amount", map.get("PURCHASE_ORG_AMT") == null ? 0 : map.get("PURCHASE_ORG_AMT"));
//            BigDecimal ntd = hvo.getINV_AMT_ONETIME()
//                                .multiply(map.get("PORTFOLIO_RATIO") == null ? new BigDecimal(0) : new BigDecimal(ObjectUtils.toString(map.get("PORTFOLIO_RATIO"))))
//                                .divide(new BigDecimal(100), BigDecimal.ROUND_HALF_UP);
//            if (isReco)
//                map.put("PURCHASE_TWD_AMT", ntd);
            
            if (isReco) {
            	BigDecimal oneTime = hvo.getINV_AMT_ONETIME();
            	BigDecimal month = hvo.getINV_AMT_PER_MONTH();
            	BigDecimal amtNTD = new BigDecimal(0);
            	BigDecimal invPct = map.get("INV_PERCENT") == null ? new BigDecimal(0) : new BigDecimal(map.get("INV_PERCENT").toString());
            	if (oneTime != null && month != null) {
            		amtNTD = oneTime.add(month).multiply(invPct).divide(new BigDecimal(100));
            	} else if (oneTime != null) {
            		amtNTD = oneTime.multiply(invPct).divide(new BigDecimal(100));
            	} else {
            		amtNTD = month.multiply(invPct).divide(new BigDecimal(100));
            	}
            	data_map.put("amountNTD", amtNTD);
            	data_map.put("ratio", invPct);
            } else {
            	data_map.put("amountNTD", map.get("PURCHASE_TWD_AMT") == null ? new BigDecimal(0) : map.get("PURCHASE_TWD_AMT"));
            	data_map.put("ratio", map.get("PORTFOLIO_RATIO") == null ? new BigDecimal(0) : map.get("PORTFOLIO_RATIO"));
            }
            data_map.put("stockAmount", 0);
            data_map.put("stockAmountNTD", 0);
            data_map.put("onceLimit", map.get("GEN_SUBS_MINI_AMT_FOR") == null ? 0 : new BigDecimal(map.get("GEN_SUBS_MINI_AMT_FOR").toString()));
            data_map.put("monthlyLimit", map.get("SML_SUBS_MINI_AMT_FOR") == null ? 0 : new BigDecimal(map.get("SML_SUBS_MINI_AMT_FOR").toString()));
            data_map.put("onceProgressive", map.get("PRD_UNIT") == null ? 0 : new BigDecimal(map.get("PRD_UNIT").toString()));
            data_map.put("rate", map.get("EX_RATE") == null ? 0 : map.get("EX_RATE"));
            data_map.put("targets", map.get("TARGETS") == null ? "" : map.get("TARGETS"));
            data_map.put("categoryId", map.get("FUND_TYPE"));
            data_map.put("keyNo", map.get("KEY_NO") == null ? "" : map.get("KEY_NO"));
//            data_map.put("ratio", map.get("PORTFOLIO_RATIO") == null ? new BigDecimal(0) : map.get("PORTFOLIO_RATIO"));
            String stockBondType = map.get("STOCK_BOND_TYPE") == null ? "S" : map.get("STOCK_BOND_TYPE").toString();
            data_map.put("categoryName", "B".equals(stockBondType) ? "債券型" : "股票型");
            data_map.put("SEQNO", map.get("SEQNO") == null ? "" : map.get("SEQNO").toString());
            data_map.put("certificateID", map.get("CERTIFICATE_ID") == null ? "" : map.get("CERTIFICATE_ID"));
            data_map.put("marketName", map.get("NAME") == null ? "其他" : map.get("NAME"));
            data_map.put("invType", map.get("INV_TYPE") == null ? "1" : map.get("INV_TYPE"));
            data_map.put("txnType", map.get("TXN_TYPE") == null ? "" : map.get("TXN_TYPE"));
            
            Map<String, Object> rtnRateExtMap = new HashMap<String, Object>();
            rtnRateExtMap.put("rtnRate", 0);
            rtnRateExtMap.put("rtnRateTWD", 0);
            data_map.put("rtnRateExt", rtnRateExtMap);
            
            data_map.put("invAmt", 0);
            data_map.put("invAmtTwd", 0);
            data_map.put("rtnRateWd", 0);
            data_map.put("rtnRateWdTwd", 0);
            data_map.put("dataDate", "");
            
            data.add(data_map);
        }

        outputMap.put("product", data);

        // 績效模擬金額
        List<Map<String, Object>> ret = getRet(dam, planID);

        try {
            if (hvo != null && ret.size() != 0) {
                // fps323 inquire
                // 前置作業準備
                Map<String, Object> afterPrepareDataMap = FPSUtils.beforeDoPortRtnSim(dam, ret, custID, planID);

                // 取得剩餘月份
                int remainingMonth = FPSUtils.getRemainingMonth(FPSUtils.getPurchasedDate(dam, planID), hvo.getINV_PERIOD().intValue());
                logger.info("GET REMAINING MONTH: " + remainingMonth);

                // 取得分組資料
                List<Map<String, Object>>[] purchasedListArray = (List<Map<String, Object>>[]) afterPrepareDataMap.get("purchasedListArray");

                // 取得單筆總投入 & 定額總投入
                BigDecimal[] purchasedValueArray = (BigDecimal[]) afterPrepareDataMap.get("purchasedValueArray");
                BigDecimal totalMoney = purchasedValueArray[0].add(purchasedValueArray[1].multiply(new BigDecimal(remainingMonth)));

                String[] invalid = FPSUtils.checkValidYear(dam, ret, 1, null);
                if (invalid.length == 0) {
                    GenericMap pt = fpsjlb.getPortRtnSim(purchasedListArray, purchasedValueArray, hvo.getINV_PERIOD().intValue(), 12, 120, remainingMonth);
                    logger.debug("FPS323 result: " + Arrays.deepToString((Double[]) pt.get("resultArray")));
                    ((Double[]) pt.get("resultArray"))[3] = totalMoney.doubleValue();
                    for (Double oneD : (Double[]) pt.get("resultArray")) {
                        if (Double.isNaN(oneD)) {
                            outputMap.put("performance", new ArrayList());
                            return;
                        }
                    }
                    outputMap.put("performance", pt.get("resultArray"));
                } else {
                    outputMap.put("performance", new ArrayList());
                }
            } else {
                outputMap.put("performance", new ArrayList());
            }
        } catch (Exception e) {
            outputMap.put("performance", new ArrayList());
        }

        try {
            // fps340
            // set init key map for checkValidYear
            Map<String, String> keyMap = new HashMap<String, String>();
            keyMap.put("prodType", "PTYPE");
            keyMap.put("prodID", "PRD_ID");
            keyMap.put("targets", "TARGETS");

            List<Map<String, Object>> formatedStockLs = formatWeightMap(product);
            if (FPSUtils.checkValidYear(dam, product, 1, keyMap).length == 0) {
                // 歷史年度平均報酬率
                BigDecimal yRate = FPSUtils.getYRate(dam, formatedStockLs, 120, 12);
                outputMap.put("avgORI", yRate == null ? 0 : yRate.doubleValue());
                // 波動率
                BigDecimal volatility = FPSUtils.getStandardDeviation(dam, formatedStockLs, 36, 12, false);
                outputMap.put("volatility", volatility == null ? 0 : volatility.doubleValue());
                // 歷史績效
                List<Map<String, Object>> performanceHis = new ArrayList<Map<String, Object>>();
                List<Map<String, Object>> old_hislist = FPSUtils.historyPerformance(dam, planID);
                for (Map<String, Object> map : old_hislist) {
                    Map<String, Object> new_map = new HashMap<String, Object>();
                    new_map.put("year", map.get("YEAR"));
                    new_map.put("ori", map.get("RETURN_ANN_M"));
                    performanceHis.add(new_map);
                }
                outputMap.put("performanceHis", performanceHis);
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            e.printStackTrace();
            outputMap.put("avgORI", 0);
            outputMap.put("volatility", 0);
            outputMap.put("performanceHis", new ArrayList<Map<String, Object>>());
        }
        this.sendRtnObject(outputMap);
    }

    /**
     * API- 2.4.8 queryPerformance 查詢績效追蹤
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void queryPerformance(Object body, IPrimitiveMap header) throws Exception{
        DataAccessManager dam = this.getDataAccessManager();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Map<String, Object> inputMap = (Map<String, Object>) body;

        String custID = inputMap.get("custId").toString();
        Map<String, Object> outputMap = new HashMap<String, Object>();
        outputMap.put("custId", custID);

        FPS400InputVO fps400vo = new FPS400InputVO();
        fps400vo.setCustID(custID);
        List<Map<String, Object>> fps400_list = fps400.api_inquire(fps400vo);
        List<Map<String, Object>> portfolio = new ArrayList<Map<String, Object>>();

        for (Map<String, Object> map400 : fps400_list) {
            Map<String, Object> reMaped = new HashMap<String, Object>();
            reMaped.put("planId", map400.get("PLAN_ID"));
            reMaped.put("fTracking", "Y".equals(map400.get("TRACE_P_FLAG")));
            // fps400 有覆核
            reMaped.put("fTrackingReview", map400.get("REVIEW_P_FLAG") == null ? "" : map400.get("REVIEW_P_FLAG"));
            reMaped.put("pTracking", "Y".equals(map400.get("TRACE_V_FLAG")));
            // fps400 有覆核
            reMaped.put("pTrackingReview", map400.get("REVIEW_V_FLAG") == null ? "" : map400.get("REVIEW_V_FLAG"));
            
//            reMaped.put("achievementRateFlag", ObjectUtils.toString(map400.get("HIT_RATE_FLAG")));
//            reMaped.put("achievementRateDesc", ObjectUtils.toString(map400.get("HIT_RATE_DESC")));
            // 達成率重算
            BigDecimal hitRate = FPSUtils.getAchievementRate(new BigDecimal(ObjectUtils.toString(map400.get("MARKET_VALUE"))), new BigDecimal(ObjectUtils.toString(map400.get("AMT_TARGET"))));
            reMaped.put("achievementRate", hitRate);
//            reMaped.put("achievementRate", map400.get("HIT_RATE") == null ? 0 : map400.get("HIT_RATE"));
            if (hitRate.compareTo(new BigDecimal(ObjectUtils.toString(map400.get("SPP_ACHIVE_RATE_2")))) != -1) {
            	reMaped.put("achievementRateFlag", "1");
                reMaped.put("achievementRateDesc", "符合進度");
            } else if (hitRate.compareTo(new BigDecimal(ObjectUtils.toString(map400.get("SPP_ACHIVE_RATE_1")))) != -1) {
            	reMaped.put("achievementRateFlag", "0");
                reMaped.put("achievementRateDesc", "微幅落後");
            } else {
            	reMaped.put("achievementRateFlag", "-1");
                reMaped.put("achievementRateDesc", "落後");
            }
            
            reMaped.put("name", map400.get("INV_PLAN_NAME") == null ? "" : map400.get("INV_PLAN_NAME"));
            reMaped.put("goalFunds", map400.get("INV_AMT_TARGET") == null ? 0 : map400.get("INV_AMT_TARGET"));
            
            int invAmt = 0;
            if(map400.get("INV_AMT_CURRENT") != null) {
            	invAmt = new BigDecimal(map400.get("INV_AMT_CURRENT").toString()).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
            }
            reMaped.put("investAmt", invAmt);
//            reMaped.put("investAmt", map400.get("INV_AMT_CURRENT") == null ? 0 : map400.get("INV_AMT_CURRENT"));
            
            reMaped.put("marketValue", map400.get("MARKET_VALUE") == null ? 0 : map400.get("MARKET_VALUE"));
            reMaped.put("returnRate", map400.get("RETURN_RATE") == null ? 0 : map400.get("RETURN_RATE"));
            reMaped.put("goalAmt", map400.get("AMT_TARGET") == null ? 0 : map400.get("AMT_TARGET"));
            reMaped.put("sppType", getSppTypeText(ObjectUtils.toString(map400.get("SPP_TYPE"))));
            // file
            List<Map<String, Object>> file = new ArrayList<Map<String, Object>>();
            FPS400InputVO fps400filevo = new FPS400InputVO();
            fps400filevo.setPlanID(map400.get("PLAN_ID").toString());
            List<Map<String, Object>> file_list = fps400.api_inquireProposal(fps400filevo);
            for (Map<String, Object> file_map : file_list) {
                Map<String, Object> reMaped2 = new HashMap<String, Object>();
                reMaped2.put("date", file_map.get("BUILDTIME") == null ? 0 : file_map.get("BUILDTIME"));
                if (!"無".equals(file_map.get("IS_EMAIL")) && !"無".equals(file_map.get("IS_PRINT")))
                    reMaped2.put("status", "已轉寄, 已列印");
                else if (!"無".equals(file_map.get("IS_EMAIL")))
                    reMaped2.put("status", "已轉寄");
                else if (!"無".equals(file_map.get("IS_PRINT")))
                    reMaped2.put("status", "已列印");
                reMaped2.put("fileCode", file_map.get("SEQ_NO"));
                file.add(reMaped2);
            }
            reMaped.put("file", file);
            portfolio.add(reMaped);
        }
        outputMap.put("portfolio", portfolio);
        
        this.sendRtnObject(outputMap);
    }

    /**
     * API- 2.4.9 setTracking 設定追蹤
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void setTracking(Object body, IPrimitiveMap header) throws Exception{
        DataAccessManager dam = this.getDataAccessManager();
        Map<String, Object> inputMap = (Map<String, Object>) body;

        // String custID = inputMap.get("custId").toString();
        String planID = inputMap.get("planId").toString();
        String type = inputMap.get("type").toString();
        Boolean isTracking = new Boolean(inputMap.get("isTracking").toString());
        // fps400 有覆核
        Boolean isReview = new Boolean(ObjectUtils.toString(inputMap.get("isReview")));
        String review_type = ObjectUtils.toString(inputMap.get("review_type"));

        FPS400InputVO fps400vo = new FPS400InputVO();
        fps400vo.setPlanID(planID);
        fps400vo.setFlagType("F".equals(type) ? "tp" : "tv");
        fps400vo.setFlagYN(isTracking ? "Y" : "N");
        if (isReview) {
            fps400vo.setFlagYN(review_type);
            fps400vo.setAction("review");
        }

        fps400.api_setFlag(fps400vo);

        Map<String, Object> outputMap = new HashMap<String, Object>();
        this.sendRtnObject(outputMap);
    }

    /**
     * API- 2.4.10 getPerformanceData 取得績效追蹤資料
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void getPerformanceData(Object body, IPrimitiveMap header) throws Exception{
        DataAccessManager dam = this.getDataAccessManager();
        Map<String, Object> inputMap = (Map<String, Object>) body;

        String planID = inputMap.get("planId").toString();
        String custID = inputMap.get("custId").toString();

        Map<String, Object> outputMap = new HashMap<String, Object>();
        outputMap.put("planId", planID);

        TBCRM_CUST_MASTVO cvo = (TBCRM_CUST_MASTVO) dam.findByPKey(TBCRM_CUST_MASTVO.TABLE_UID, custID);
        if (cvo == null)
            throw new APException("客戶不存在");

        FPS400InputVO fps400vo = new FPS400InputVO();
        fps400vo.setCustID(custID);
        fps400vo.setPlanID(planID);
        List<Map<String, Object>> fps400_list = fps400.api_inquire(fps400vo);
        Map<String, Object> fps400_map = fps400_list.get(0);

        List<Map<String, Object>> settingList = fps410.getSetting(dam, planID, custID);
        Map<String, Object> settingMap = settingList.get(0);

        outputMap.put("sppType", getSppTypeText(ObjectUtils.toString(fps400_map.get("SPP_TYPE"))));
        outputMap.put("sppName", fps400_map.get("INV_PLAN_NAME"));
        outputMap.put("investPeriod", settingMap.get("INV_PERIOD"));
        int onceAmt = 0;
        if (settingMap.get("ONETIME_R") != null) {
        	BigDecimal onetimeR = (BigDecimal) settingMap.get("ONETIME_R");
        	onetimeR = onetimeR.setScale(0, BigDecimal.ROUND_HALF_UP);	//四捨五入至整數
        	onceAmt = onetimeR.intValue();
        }
        int monthlyAmt = 0;
        if (settingMap.get("PERMONTH_R") != null) {
        	BigDecimal permonthR = (BigDecimal) settingMap.get("PERMONTH_R");
        	permonthR = permonthR.setScale(0, BigDecimal.ROUND_HALF_UP);	//四捨五入至整數
        	monthlyAmt = permonthR.intValue();
        }
        outputMap.put("onceAmt", onceAmt);
        outputMap.put("monthlyAmt", monthlyAmt);
        outputMap.put("riskValue", fps400_map.get("VOL_RISK_ATTR"));
        outputMap.put("amount", settingMap.get("INV_AMT_TARGET"));
        //
        Map<String, Object> amountInfo = new HashMap<String, Object>();
        if ("1".equals(outputMap.get("sppType"))) {
            Map<String, Object> university = new HashMap<String, Object>();
            Map<String, Object> institute = new HashMap<String, Object>();
            Map<String, Object> doctor = new HashMap<String, Object>();

            TBFPS_SPP_PLAN_HELP_EVO evo = (TBFPS_SPP_PLAN_HELP_EVO) dam.findByPKey(TBFPS_SPP_PLAN_HELP_EVO.TABLE_UID, planID);
            // 1. 公立 2. 私立 3. 留學
            university.put("type", evo != null && evo.getUNIVERSITY() != null ? Integer.parseInt(evo.getUNIVERSITY().toString()) : 0);
            university.put("tuition", evo != null && evo.getUNIVERSITY_FEE_EDU() != null ? evo.getUNIVERSITY_FEE_EDU() : 0);
            university.put("living", evo != null && evo.getUNIVERSITY_FEE_LIFE() != null ? evo.getUNIVERSITY_FEE_LIFE() : 0);
            university.put("years", evo != null && evo.getUNIVERSITY_YEAR() != null ? evo.getUNIVERSITY_YEAR() : 0);
            
            institute.put("type", evo != null && evo.getMASTER() != null ? Integer.parseInt(evo.getMASTER().toString()) : 0);
            institute.put("tuition", evo != null && evo.getMASTER_FEE_EDU() != null ? evo.getMASTER_FEE_EDU() : 0);
            institute.put("living", evo != null && evo.getMASTER_FEE_LIFE() != null ? evo.getMASTER_FEE_LIFE() : 0);
            institute.put("years", evo != null && evo.getMASTER_YEAR() != null ? evo.getMASTER_YEAR() : 0);
            
            doctor.put("type", evo != null && evo.getPHD() != null ? Integer.parseInt(evo.getPHD().toString()) : 0);
            doctor.put("tuition", evo != null && evo.getPHD_FEE_EDU() != null ? evo.getPHD_FEE_EDU() : 0);
            doctor.put("living", evo != null && evo.getPHD_FEE_LIFE() != null ? evo.getPHD_FEE_LIFE() : 0);
            doctor.put("years", evo != null && evo.getPHD_YEAR() != null ? evo.getPHD_YEAR() : 0);

            amountInfo.put("university", university);
            amountInfo.put("institute", institute);
            amountInfo.put("doctor", doctor);
        } else if ("2".equals(outputMap.get("sppType"))) {
            TBFPS_SPP_PLAN_HELP_RVO rvo = (TBFPS_SPP_PLAN_HELP_RVO) dam.findByPKey(TBFPS_SPP_PLAN_HELP_RVO.TABLE_UID, planID);
            amountInfo.put("retireAge", rvo != null && rvo.getRETIREMENT_AGE() != null ? rvo.getRETIREMENT_AGE() : 0);
            QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
            queryCondition.setQueryString("SELECT PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'FPS.INS_AVERAGE' AND PARAM_CODE = :code");
            queryCondition.setObject("code", cvo.getGENDER());
            List<Map<String, Object>> list = dam.exeQuery(queryCondition);
            
            if (list.size() > 0)
                amountInfo.put("avgLife", new GenericMap(list.get(0)).getBigDecimal("PARAM_NAME"));
            else
                amountInfo.put("avgLife", new BigDecimal(0));
            
            amountInfo.put("retireSpend", rvo != null && rvo.getRETIRE_FEE() != null ? rvo.getRETIRE_FEE() : 0);
            amountInfo.put("inheritAmt", rvo != null && rvo.getHERITAGE() != null ? rvo.getHERITAGE() : 0);

            Map<String, Object> socialInsurance = new HashMap<String, Object>();
            socialInsurance.put("monthly", rvo != null && rvo.getSOCIAL_INS_FEE_1() != null ? rvo.getSOCIAL_INS_FEE_1() : 0);
            socialInsurance.put("once", rvo != null && rvo.getSOCIAL_INS_FEE_2() != null ? rvo.getSOCIAL_INS_FEE_2() : 0);
            amountInfo.put("socialInsurance", socialInsurance);

            Map<String, Object> socialWelfare = new HashMap<String, Object>();
            socialWelfare.put("monthly", rvo != null && rvo.getSOCIAL_WELFARE_FEE_1() != null ? rvo.getSOCIAL_WELFARE_FEE_1() : 0);
            socialWelfare.put("once", rvo != null && rvo.getSOCIAL_WELFARE_FEE_2() != null ? rvo.getSOCIAL_WELFARE_FEE_2() : 0);
            amountInfo.put("socialWelfare", socialWelfare);

            Map<String, Object> businessInsurance = new HashMap<String, Object>();
            businessInsurance.put("monthly", rvo != null && rvo.getCOMM_INS_FEE_1() != null ? rvo.getCOMM_INS_FEE_1() : 0);
            businessInsurance.put("once", rvo != null && rvo.getCOMM_INS_FEE_2() != null ? rvo.getCOMM_INS_FEE_2() : 0);
            amountInfo.put("businessInsurance", businessInsurance);

            Map<String, Object> otherInsurance = new HashMap<String, Object>();
            otherInsurance.put("monthly", rvo != null && rvo.getOTHER_FEE_1() != null ? rvo.getOTHER_FEE_1() : 0);
            otherInsurance.put("once", rvo != null && rvo.getOTHER_FEE_2() != null ? rvo.getOTHER_FEE_2() : 0);
            amountInfo.put("otherInsurance", otherInsurance);
        }

        setPurposeInfo(ObjectUtils.toString(outputMap.get("sppType")), amountInfo);
        outputMap.put("amountInfo", amountInfo);
        // history stock query
        List<Map<String, Object>> product = fps410.getReturnQuery(dam, planID, custID);
        /** 贖回門檻 (全資產 + 績效追蹤 共用)*/
        FPSUtils.mergeRedemption(dam, product);
        
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (Map<String, Object> data_map : product) {
            list.add(productFactory.getMapFromDBValue(data_map, FPSType.SPP, new FPSExt[] { FPSExt.rtnRateExt }));
        }
        outputMap.put("product", list);
        // performance query
        List<Map<String, Object>> performanceProduct = FPSUtils.getHistory(dam, planID, custID, "N");
        List<Map<String, Object>> performanceList = new ArrayList<Map<String, Object>>();
        for (Map<String, Object> data_map : performanceProduct) {
            performanceList.add(productFactory.getMapFromDBValue(data_map, FPSType.SPP));
        }
        outputMap.put("performanceProduct", performanceList);

        List<Map<String, Object>> achivedParamList = fps340.getAchivedParamList(dam, planID, custID);
        Map<String, Object> achivedParamMap = achivedParamList.get(0);

        outputMap.put("achievementRate", achivedParamMap.get("HIT_RATE"));
        outputMap.put("ori", achivedParamMap.get("RETURN_RATE"));
        outputMap.put("marketValue", achivedParamMap.get("MARKET_VALUE"));
        outputMap.put("targetGoal", achivedParamMap.get("AMT_TARGET"));
        outputMap.put("principal", achivedParamMap.get("INV_AMT_CURRENT"));

        this.sendRtnObject(outputMap);
    }

    /**
     * API- 2.4.11 queryHistoryTradeDetail 取得歷史交易明細
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void queryHistoryTradeDetail(Object body, IPrimitiveMap header) throws Exception{
        Map<String, Object> inputMap = (Map<String, Object>) body;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd");

        String custID = inputMap.get("custId").toString();
        String planID = inputMap.get("planId").toString();
        Date startDate = null;
        Date endDate = null;
        if (StringUtils.isNotBlank(ObjectUtils.toString(inputMap.get("startDate"))))
            startDate = sdf.parse(ObjectUtils.toString(inputMap.get("startDate")));
        if (StringUtils.isNotBlank(ObjectUtils.toString(inputMap.get("endDate"))))
            endDate = sdf.parse(ObjectUtils.toString(inputMap.get("endDate")));

        FPS400InputVO fps400VO = new FPS400InputVO();
        fps400VO.setCustID(custID);
        fps400VO.setPlanID(planID);
        fps400VO.setsDate(startDate);
        fps400VO.seteDate(endDate);
        fps400VO.setFlagYN("Y");
        List<Map<String, Object>> his_list = fps400.api_inquireHis(fps400VO);
        
        Map<String, Object> outputMap = new HashMap<String, Object>();
        outputMap.put("planId", planID);

        List<Map<String, Object>> history = new ArrayList<Map<String, Object>>();
        for (Map<String, Object> map : his_list) {
            Map<String, Object> data_map = new HashMap<String, Object>();
            data_map.put("date", sdf2.format(sdf2.parse(map.get("DATA_DATE").toString())));
            data_map.put("productId", map.get("PROD_ID"));
            data_map.put("productName", map.get("PROD_NAME"));
            data_map.put("currCd", map.get("VALU_CRCY_TYPE"));
            boolean isOrg = !"TWD".equals(ObjectUtils.toString(map.get("VALU_CRCY_TYPE")));
            data_map.put("investAmt", isOrg ? map.get("INV_COST_ORGD") : map.get("INV_COST_TWD"));
            data_map.put("redemptionAmt", isOrg ? map.get("REF_AMT_ORGD") : map.get("REF_AMT_TWD"));
            data_map.put("yield", isOrg ? map.get("TXN_DIVID_ORGD") : map.get("TXN_DIVID_TWD"));
            data_map.put("ratio", isOrg ? map.get("RATIO_ORGD") : map.get("RATIO_TWD"));
            history.add(data_map);
        }
        outputMap.put("history", history);

        this.sendRtnObject(outputMap);
    }

    /**
     * API- 2.8.3 saveFinancialPlan 儲存理財規劃資料
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void saveFinancialPlan(Object body, IPrimitiveMap header) throws Exception{
        DataAccessManager dam = this.getDataAccessManager();
        Map<String, Object> outputMap = new HashMap<String, Object>();
        Map<String, Object> inputMap = (Map<String, Object>) body;

        String custID = inputMap.get("custId").toString();
        String planID = ObjectUtils.toString(inputMap.get("planId"));
        String action = StringUtils.isBlank(planID) ? "create" : "update";
        String riskLevel = ObjectUtils.toString(inputMap.get("riskLevel"));
        Map<String, Object> planData = (Map<String, Object>) inputMap.get("planData");
        String step = planData.get("step").toString();
        String planStep = ObjectUtils.toString(planData.get("planStep"));
        Map<String, Object> step1_map = (Map<String, Object>) planData.get("step1");
        Map<String, Object> step2_map = (Map<String, Object>) planData.get("step2");
        List<Map<String, Object>> step3_list = (List<Map<String, Object>>) planData.get("step3");
        //
        if ("1".equals(step)) {
            Map<String, Object> step1_detail = (Map<String, Object>) step1_map.get("detail");

            // get FPS210 cust list for deposit and stock amounts
            List<Map<String, Object>> custAmtList = fps210.getCustAmt(dam, custID);
            if (custAmtList.size() <= 0) {
                throw new Exception("fps210 getCustAmt 回傳為空");
            }
            Map<String, Object> custAmtMap = custAmtList.get(0);

            FPS210InputVO fps210VO = new FPS210InputVO();
            try {
                fps210VO.setAction(action);
                fps210VO.setCustID(custID);
                fps210VO.setPlanID(planID);
                fps210VO.setRiskType(riskLevel);
                fps210VO.setCustRisk(riskLevel);
                fps210VO.setStep(planStep);

                fps210VO.setSowAmt(new BigDecimal(step1_map.get("outAmt").toString()));
                fps210VO.setCash(new BigDecimal(step1_map.get("cashPosition").toString()));
                fps210VO.setEssCash(new BigDecimal(step1_map.get("liveExp").toString()));
                fps210VO.setLoanExpenses(new BigDecimal(step1_map.get("loanExp").toString()));
                fps210VO.setOtherExpenses(new BigDecimal(step1_map.get("otherExp").toString())); 
                fps210VO.setInsPolicyAmt(new BigDecimal(step1_map.get("lifeFee").toString()));
                fps210VO.setInsSavAmt(new BigDecimal(step1_map.get("dopositFee").toString()));
                fps210VO.setCashPrepare(new BigDecimal(step1_map.get("cashPrepare").toString()));
//                fps210VO.setEssCash(new BigDecimal(step1_detail.get("life").toString()));
//                fps210VO.setEmeCsh(new BigDecimal(step1_detail.get("urgent").toString()));
                fps210VO.setHouseLoan(new BigDecimal(step1_detail.get("houseLoans").toString()));
                fps210VO.setCreditLoan(new BigDecimal(step1_detail.get("creditLoans").toString()));
                fps210VO.setStdLoan(new BigDecimal(step1_detail.get("eduLoans").toString()));
                fps210VO.setPayForHouse(new BigDecimal(step1_detail.get("house").toString()));
                fps210VO.setPayForCar(new BigDecimal(step1_detail.get("car").toString()));
                fps210VO.setTravel(new BigDecimal(step1_detail.get("travel").toString()));
                fps210VO.setStudy(new BigDecimal(step1_detail.get("study").toString()));
                fps210VO.setOther(new BigDecimal(step1_detail.get("other").toString()));

                fps210VO.setDeposit((BigDecimal) custAmtMap.get("DEPOSIT_AMT"));
                fps210VO.setAnnuityProd((BigDecimal) custAmtMap.get("INS_1_AMT"));
                fps210VO.setFixedProd((BigDecimal) custAmtMap.get("FIXED_INCOME_AMT"));
                fps210VO.setFundProd((BigDecimal) custAmtMap.get("FUND_AMT"));
                BigDecimal totalAmt = fps210VO.getDeposit()
                                              .add(fps210VO.getAnnuityProd())
                                              .add(fps210VO.getFixedProd())
                                              .add(fps210VO.getFundProd())
                                              .add(fps210VO.getSowAmt())
                                              .subtract(fps210VO.getCash())
                                              .subtract(fps210VO.getInsPolicyAmt())
                                              .subtract(fps210VO.getInsSavAmt());
                fps210VO.setPlanAmt(totalAmt);
                fps210VO.setPortfolio2Ratio(new BigDecimal(step2_map.get("bondsPct").toString()));
                fps210VO.setPortfolio3Ratio(new BigDecimal(step2_map.get("stockPct").toString()));
                
                fps210VO.setMfdProd((BigDecimal) custAmtMap.get("MFD_PROD_AMT"));
                fps210VO.setEtfProd((BigDecimal) custAmtMap.get("ETF_PROD_AMT"));
                fps210VO.setInsProd((BigDecimal) custAmtMap.get("INS_PROD_AMT"));
                fps210VO.setBondProd((BigDecimal) custAmtMap.get("BOND_PROD_AMT"));
                fps210VO.setSnProd((BigDecimal) custAmtMap.get("SN_PROD_AMT"));
                fps210VO.setSiProd((BigDecimal) custAmtMap.get("SI_PROD_AMT"));
                
            } catch (Exception e) {
                throw new APException("格式錯誤");
            }

            if ("create".equals(action))
                outputMap.put("planId", fps210.exeSave(fps210VO));
            else
                fps210.exeSave(fps210VO);
        } else if ("2".equals(step)) {
            FPS220InputVO fps220VO = new FPS220InputVO();
            try {
                fps220VO.setAction(action);
                fps220VO.setCustID(custID);
                fps220VO.setPlanID(planID);
                fps220VO.setRiskType(riskLevel);
                fps220VO.setStep(planStep);

                fps220VO.setDepositPct(new BigDecimal(step2_map.get("depositRatio").toString()));
                fps220VO.setFixedPct(new BigDecimal(step2_map.get("incomeRatio").toString()));
                fps220VO.setStockPct(new BigDecimal(step2_map.get("fundRatio").toString()));
                fps220VO.setDepositAmt(new BigDecimal(step2_map.get("dopositAmt").toString()));
                fps220VO.setFixedAmt(new BigDecimal(step2_map.get("incomeAmt").toString()));
                fps220VO.setStockAmt(new BigDecimal(step2_map.get("stockAmt").toString()));
            } catch (Exception e) {
                throw new APException("格式錯誤");
            }

            fps220.exeSave(fps220VO);
        } else if ("3".equals(step)) {
            FPS230InputVO fps230VO = new FPS230InputVO();
            try {
                fps230VO.setAction(action);
                fps230VO.setCustID(custID);
                fps230VO.setPlanID(planID);
                fps230VO.setRiskType(riskLevel);
                fps230VO.setStep(planStep);

                List<FPS230ProdInputVO> fps230_list = new ArrayList<FPS230ProdInputVO>();
                for (Map<String, Object> map : step3_list) {
                    FPS230ProdInputVO de_vo = new FPS230ProdInputVO();
                    String ptype = ObjectUtils.toString(map.get("productType"));
                    de_vo.setINV_PRD_TYPE(map.get("invProductType").toString());

                    // Juan 0524
                    if (map.get("invProductType").toString().equals("1")) {
                        if (ptype.equals("DOP")) {
                            de_vo.setINV_PRD_TYPE_2(map.get("pCurrency").toString().equals("TWD") ? "1" : "2");
                        }
                        if (ptype.equals("INS")) {
                            de_vo.setINV_PRD_TYPE_2("3");
                        }
                    } else {
                        de_vo.setINV_PRD_TYPE_2(null);
                    }

                    de_vo.setSEQNO("".equals(ObjectUtils.toString(map.get("SEQNO"))) ? null : new BigDecimal(map.get("SEQNO")
                                                                                                                .toString()));
                    de_vo.setPRD_TYPE(ptype.equals("DOP") ? null : ptype);
                    de_vo.setPRD_NAME(ObjectUtils.toString(map.get("productName")));
                    de_vo.setRISKCATE_ID(ObjectUtils.toString(map.get("riskLevel")));
                    de_vo.setPROD_CURR(ObjectUtils.toString(map.get("currCd")));
                    de_vo.setMARKET_CIS(ObjectUtils.toString(map.get("marketId")));
                    de_vo.setINV_TYPE(ObjectUtils.toString(map.get("investType")));
                    de_vo.setPRD_ID(ObjectUtils.toString(map.get("productId")));
                    de_vo.setTRUST_CURR(ObjectUtils.toString(map.get("pCurrency")));
                    de_vo.setINV_TYPE(ObjectUtils.toString(map.get("investType")));
                    de_vo.setPURCHASE_ORG_AMT(new BigDecimal(map.get("amount").toString()));
                    de_vo.setPURCHASE_TWD_AMT(new BigDecimal(map.get("amountNTD").toString()));
                    de_vo.setINVENTORY_ORG_AMT(new BigDecimal(map.get("stockAmount").toString()));
                    de_vo.setINVENTORY_TWD_AMT(new BigDecimal(map.get("stockAmountNTD").toString()));
                    de_vo.setPORTFOLIO_RATIO(new BigDecimal(map.get("ratio").toString()));
                    // de_vo.setLIMIT_ORG_AMT(new BigDecimal(map.get("limit").toString()));
                    if ("3".equals(map.get("invProductType")) && "INS".equals(map.get("productType")))
                        de_vo.setTargets(ObjectUtils.toString(map.get("targets")));
                    de_vo.setTXN_TYPE(ObjectUtils.toString(map.get("txnType")));

                    de_vo.setINV_TYPE(ObjectUtils.toString(map.get("invType")));
                    de_vo.setCERT_NBR(ObjectUtils.toString(map.get("certificateID")));
                    fps230_list.add(de_vo);
                }
                fps230VO.setProdList(fps230_list);
            } catch (Exception e) {
                e.printStackTrace();
                throw new APException("格式錯誤");
            }

            fps230.exeSave(fps230VO);
            // delete all plan details
            if (deletePlanDetails(dam, planID)) {
                for (FPS230ProdInputVO prod : fps230VO.getProdList()) {
                    BigDecimal invID = new BigDecimal(getSEQ(dam, "FPS_INV_DETAIL"));
                    fps230.create(prod, dam, planID, invID);
                }
            } else {
                throw new APException("ehl_01_common_007"); // 儲存錯誤: 更新失敗
            }
        } else if ("4".equals(step)) {
            FPS240OutputVO fps240_outputVO = new FPS240OutputVO();

            fps240_outputVO.setHeadList(fps210.getCustPlan(dam, planID));
            fps240_outputVO.setOutputList(fps230.hisQuery(dam, planID));
            fps240_outputVO.setFxRateList(getFxRate(dam, ""));
            // Juan need cust OBU isPro
            fps240_outputVO.setInitModelPortfolioList(fps230.modelQuery(dam, riskLevel, null, null, null));
            fps240_outputVO.setHasInvest(hasInvest(dam, planID));

            // set init key map for checkValidYear
            Map<String, String> keyMap = new HashMap<String, String>();
            keyMap.put("prodType", "PTYPE");
            keyMap.put("prodID", "PRD_ID");
            keyMap.put("targets", "TARGETS");
            // 庫存波動度
            if (fps240_outputVO.isHasInvest() && fps240_outputVO.getOutputList().size() > 0) {
                List<Map<String, Object>> stockList = fps220.calStockVolaility(dam, custID);
                if (stockList.size() > 0) {
                    List<Map<String, Object>> formatedStockLs = formatWeightMap(stockList);
                    if (FPSUtils.checkValidYear(dam, stockList, 1, keyMap).length == 0) {
                        // 庫存波動率
                        BigDecimal stockVolatility = FPSUtils.getStandardDeviation(dam, formatedStockLs, 36, 12, false);
                        fps240_outputVO.setStockVolatility(stockVolatility == null ? null : stockVolatility.doubleValue());
                    }
                }
            }
            // 波動度
            // 歷史績效
            try {
                FPSUtilsOutputVO invalidOutputVO = new FPSUtilsOutputVO();
                invalidOutputVO.setError(true);

                List<Map<String, Object>> prdList = fps240.getStockLsByPlanID(dam, planID);
                String[] prdArr = new String[prdList.size()];
                int i = 0;
                for (Map<String, Object> prd : prdList) {
                    prdArr[i++] = prd.get("PRD_ID").toString();
                }
                invalidOutputVO.setInvalidPrdID(FPSUtils.checkValidYear(dam, prdList, 3, null));
                if (invalidOutputVO.getInvalidPrdID().length == 0) {
                    fps240_outputVO.setHisPerformanceList(fpsjlb.getPortfolioVolatility(prdList));
                    fps240_outputVO.setYearRateList(FPSUtils.historyPerformance(dam, planID));
                } else {
                    throw new Exception();
                }
            } catch (Exception e) {
                e.printStackTrace();
                fps240_outputVO.setHisPerformanceList(null);
                fps240_outputVO.setYearRateList(null);
            }
            // 波動度參數
            fps240_outputVO.setRecoVolatility(fps220.getRecoVolaility(dam, riskLevel));

            outputMap.put("volatility", fps240_outputVO.getHisPerformanceList());
            outputMap.put("avgORI", fps240_outputVO.getYearRateList());
        }

        this.sendRtnObject(outputMap);
    }

    /**
     * API- 2.8.4 getPortfolio 取得的投資建議組合
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void getPortfolio(Object body, IPrimitiveMap header) throws Exception{
        DataAccessManager dam = this.getDataAccessManager();
        Map<String, Object> inputMap = (Map<String, Object>) body;

        String custID = inputMap.get("custId").toString();
        String riskType = inputMap.get("riskLevel").toString();
        BigDecimal fixedAmt = new BigDecimal(inputMap.get("incomeAmt").toString());
        String isPro = inputMap.get("isProfessional").toString();;
        String isOBU = inputMap.get("isOBU").toString();
        
        List<Map<String, Object>> fixedModel = null;
        try {
          fixedModel = fps230.fixedModelQuery(dam, custID, riskType, fixedAmt, isPro.equals("Y"), "B");
          if (fixedModel == null) {
//            errorList.add("SI/SN適配電文錯誤，無法配置推薦SI/SN商品。");
          }
        } catch (Exception e) {
//          errorList.add("SI/SN適配電文錯誤，無法配置推薦SI/SN商品。");
        }

        // portfolio 要分兩邊 A.類債券[BOND, SI, MFD, ETF] B.類股票[MFD, ETF, INS]
        List<Map<String, Object>> allModelPortfolio = fps230.modelQuery(dam, riskType, null, isOBU, isPro);
        Map<String, List<Map<String, Object>>> groupByStockBondTypeMap = fps230.groupByStockBondType(allModelPortfolio);
        
        // 類債券 (額外加工計算 比例)
        List<Map<String, Object>> modelPortfolioB = fps230.mergeModelWithFixed(groupByStockBondTypeMap.get("B"), fixedModel);
        fps230.reCalPct(modelPortfolioB, fixedAmt, dam);
        
        // 類股票
        List<Map<String, Object>> modelPortfolioS = fps230.mergeModelWithFixed(groupByStockBondTypeMap.get("S"), new ArrayList<Map<String, Object>>());
        List<Map<String, Object>> modelPortfolio = new ArrayList<Map<String, Object>>();
        modelPortfolio.addAll(modelPortfolioB);
        modelPortfolio.addAll(modelPortfolioS);
        
        // Juan need cust OBU isPro
//        List<Map<String, Object>> modelPortfolio = fps230.mergeModelWithFixed(fps230.modelQuery(dam, riskLevel, null, null, null), fps230.fixedModelQuery(dam, custID, riskLevel, incomeAmt, isPro, "S"));
        
        List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();

        for (Map<String, Object> map : modelPortfolio) {
            data.add(productFactory.getMapFromDBValue(map, FPSType.INV));
        }

        Map<String, Object> outputMap = new HashMap<String, Object>();
        outputMap.put("product", data);
        this.sendRtnObject(outputMap);
    }

    /**
     * API- 2.8.5 getFinancialPlanData 取得理財規劃資料
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void getFinancialPlanData(Object body, IPrimitiveMap header) throws Exception{
        DataAccessManager dam = this.getDataAccessManager();
        Map<String, Object> inputMap = (Map<String, Object>) body;

        String custID = inputMap.get("custId").toString();
        String planID = ObjectUtils.toString(inputMap.get("planId"));
        String riskType = ObjectUtils.toString(inputMap.get("riskLevel"));
        String hasIns = ObjectUtils.toString(inputMap.get("hasStock"));
        
        Map<String, Object> param =  new HashMap<String, Object>();
        param.put("hasIns", "Y".equals(hasIns) ? true : false );
        param.put("custID", custID);
        param.put("riskType", riskType);
        
        BigDecimal[] stockBondsPct = fps210.getSuggestPct(dam, param);
        //建議股票型比例
        Map<String,Object> suggestPctMap = fps230.getCustRisKPct(dam);
        //建議股票型上界值
        Map<String,Object> custRiskPctMap = fps240.getCustRisKPct(dam);
        
        Map<String, Object> outputMap = new HashMap<String, Object>();
        Map<String, Object> planData = new HashMap<String, Object>();

        // step 1 fps210
        Map<String, Object> map1;
        //boolean isPlan = StringUtils.isNotBlank(planID);
        if (StringUtils.isNotBlank(planID))
            map1 = fps210.getCustPlan(dam, planID).get(0);
        else
            map1 = fps210.getCustAmt(dam, custID).get(0);
        Map<String, Object> step1_map = new HashMap<String, Object>();
        Map<String, Object> step1_detail = new HashMap<String, Object>();

        step1_map.put("outAmt", FPS200ApiMappingFactory.defaultIntValue(map1.get("SOW_AMT")));
        step1_map.put("cashPosition", FPS200ApiMappingFactory.defaultIntValue(map1.get("CASH_YEAR_AMT")));
        step1_map.put("liveExp", FPS200ApiMappingFactory.defaultIntValue(map1.get("LIVE_YEAR_AMT")));
        step1_map.put("loanExp", FPS200ApiMappingFactory.defaultIntValue(map1.get("LOAN_EXPENSES")));
        step1_map.put("otherExp", FPS200ApiMappingFactory.defaultIntValue(map1.get("OTHER_EXPENSES")));
        step1_map.put("lifeFee", FPS200ApiMappingFactory.defaultIntValue(map1.get("INS_POLICY_AMT")));
        step1_map.put("dopositFee", FPS200ApiMappingFactory.defaultIntValue(map1.get("INS_SAV_AMT")));
        step1_map.put("cashPrepare", FPS200ApiMappingFactory.defaultIntValue(map1.get("CASH_PREPARE")));
//        step1_map.put("sugCashPrePct", fps210.getCashPreparePct(dam, custID).intValue());

        step1_detail.put("life", FPS200ApiMappingFactory.defaultIntValue(map1.get("LIVE_YEAR_AMT")));
        step1_detail.put("urgent", FPS200ApiMappingFactory.defaultIntValue(map1.get("PREPARE_YEAR_AMT")));
        step1_detail.put("houseLoans", FPS200ApiMappingFactory.defaultIntValue(map1.get("LN_HOUSE_AMT"), FPS200ApiMappingFactory.defaultIntValue(map1.get("LN_YEAR_AMT_1"))));
        step1_detail.put("creditLoans", FPS200ApiMappingFactory.defaultIntValue(map1.get("LN_CREDIT_AMT"), FPS200ApiMappingFactory.defaultIntValue(map1.get("LN_YEAR_AMT_2"))));
        step1_detail.put("eduLoans", FPS200ApiMappingFactory.defaultIntValue(map1.get("LN_EDCUATION_AMT"), FPS200ApiMappingFactory.defaultIntValue(map1.get("LN_YEAR_AMT_3"))));
        //step1_detail.put("houseLoans", FPS200ApiMappingFactory.defaultIntValue(isPlan ? map1.get("LN_HOUSE_AMT") : FPS200ApiMappingFactory.defaultIntValue(map1.get("LN_YEAR_AMT_1"))));
        //step1_detail.put("creditLoans", FPS200ApiMappingFactory.defaultIntValue(isPlan ? map1.get("LN_CREDIT_AMT") : FPS200ApiMappingFactory.defaultIntValue(map1.get("LN_YEAR_AMT_2"))));
        //step1_detail.put("eduLoans", FPS200ApiMappingFactory.defaultIntValue(isPlan ? map1.get("LN_EDCUATION_AMT") : FPS200ApiMappingFactory.defaultIntValue(map1.get("LN_YEAR_AMT_3"))));
        step1_detail.put("house", FPS200ApiMappingFactory.defaultIntValue(map1.get("BUY_HOUSE_AMT")));
        step1_detail.put("car", FPS200ApiMappingFactory.defaultIntValue(map1.get("BUY_CAR_AMT")));
        step1_detail.put("travel", FPS200ApiMappingFactory.defaultIntValue(map1.get("TRAVEL_AMT")));
        step1_detail.put("study", FPS200ApiMappingFactory.defaultIntValue(map1.get("OVERSEA_EDUCATION_AMT")));
        step1_detail.put("other", FPS200ApiMappingFactory.defaultIntValue(map1.get("OTHER_AMT")));

        step1_map.put("detail", step1_detail);
        planData.put("step1", step1_map);
        // step 2 fps220
        Map<String, Object> map2 = new HashMap<String, Object>();
        if (StringUtils.isNotBlank(planID))
            map2 = fps220.getHis(dam, planID).get(0);
        //
        Map<String, Object> step2_map = new HashMap<String, Object>();
        XmlInfo xmlInfo = new XmlInfo();
        Map<String, String> minBuyAmtMap1 = xmlInfo.doGetVariable("SOT.NF_MIN_BUY_AMT_1", FormatHelper.FORMAT_3);
        Map<String, String> minBuyAmtMap2 = xmlInfo.doGetVariable("SOT.NF_MIN_BUY_AMT_2", FormatHelper.FORMAT_3);
        
        step2_map.put("depositRatio", FPS200ApiMappingFactory.defaultDoubleValue(map2.get("PORTFOLIO1_RATIO")));
        step2_map.put("incomeRatio", FPS200ApiMappingFactory.defaultDoubleValue(map2.get("PORTFOLIO2_RATIO")));
        step2_map.put("fundRatio", FPS200ApiMappingFactory.defaultDoubleValue(map2.get("PORTFOLIO3_RATIO")));
        step2_map.put("dopositAmt", FPS200ApiMappingFactory.defaultIntValue(map2.get("PORTFOLIO1_AMT")));
        step2_map.put("incomeAmt", FPS200ApiMappingFactory.defaultIntValue(map2.get("PORTFOLIO2_AMT")));
        step2_map.put("stockAmt", FPS200ApiMappingFactory.defaultIntValue(map2.get("PORTFOLIO3_AMT")));
        //台幣申購門檻(單筆)
        step2_map.put("onceLimitNTD", FPS200ApiMappingFactory.defaultIntValue(minBuyAmtMap1.get("TWD")));
        //台幣申購門檻(定額)
        step2_map.put("monthlyLimitNTD", FPS200ApiMappingFactory.defaultIntValue(minBuyAmtMap2.get("TWD")));
        
        step2_map.put("bondsPct", FPS200ApiMappingFactory.defaultIntValue(stockBondsPct[0]));
        step2_map.put("stockPct", FPS200ApiMappingFactory.defaultIntValue(stockBondsPct[1]));
        if(stockBondsPct.length > 2) {
        	//再投資建議配置
        	step2_map.put("hasInsBonds", FPS200ApiMappingFactory.defaultIntValue(stockBondsPct[2]));
            step2_map.put("hasInsStock", FPS200ApiMappingFactory.defaultIntValue(stockBondsPct[3]));
        } else {
        	step2_map.put("hasInsBonds", 0);
            step2_map.put("hasInsStock", 0);
        }
        step2_map.put("suggestPct", FPS200ApiMappingFactory.defaultIntValue(suggestPctMap.get(riskType)));
        step2_map.put("stockUpLimit", FPS200ApiMappingFactory.defaultIntValue(custRiskPctMap.get(riskType)));
        
        planData.put("step2", step2_map);
        // step 3 fps230
        List<Map<String, Object>> list3 = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> data_list = new ArrayList<Map<String, Object>>();
        if (StringUtils.isNotBlank(planID) && StringUtils.isNotBlank(riskType)) {
            data_list = fps230.hisQuery(dam, planID);
            for (Map<String, Object> data_map : data_list) {
                data_map.put("NOW_AMT", BigDecimal.ZERO);
                data_map.put("NOW_AMT_TWD", BigDecimal.ZERO);
                list3.add(productFactory.getMapFromDBValue(data_map, FPSType.INV));
            }
        }
        planData.put("step3", list3);

        outputMap.put("planId", planID);
        outputMap.put("custId", custID);
        outputMap.put("planData", planData);

        //
        // set init key map for checkValidYear
        Map<String, String> keyMap = new HashMap<String, String>();
        keyMap.put("prodType", "PTYPE");
        keyMap.put("prodID", "PRD_ID");
        keyMap.put("targets", "TARGETS");

        // 基股類商品
        List<Map<String, Object>> prdList = fps240.rmBackItem(filterList(data_list, "3"));

        List<Map<String, Object>> formatedProdLs = formatWeightMap(prdList);

        // 1年
        if (FPSUtils.checkValidYear(dam, prdList, 1, keyMap).length == 0) {
            // 歷史年度平均報酬率
            BigDecimal yRate = FPSUtils.getYRate(dam, formatedProdLs, 120, 12);
            outputMap.put("avgORI", yRate == null ? 0 : yRate.doubleValue());
            // 波動率
            BigDecimal volatility = FPSUtils.getStandardDeviation(dam, formatedProdLs, 36, 12, false);
            outputMap.put("volatility", volatility == null ? 0 : volatility.doubleValue());
            // 有滿一年波動率
            BigDecimal FYvolatility = FPSUtils.getStandardDeviation(dam, formatedProdLs, 36, 12, true);
            outputMap.put("FYvolatility", FYvolatility == null ? 0 : FYvolatility.doubleValue());
        } else {
            outputMap.put("avgORI", 0);
            outputMap.put("volatility", 0);
            outputMap.put("FYvolatility", 0);
        }

        this.sendRtnObject(outputMap);
    }

    /**
     * API- 2.8.6 generateFinancialPlan 產生理財規劃書
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void generateFinancialPlan(Object body, IPrimitiveMap header) throws Exception{
        DataAccessManager dam = this.getDataAccessManager();
        Map<String, Object> inputMap = (Map<String, Object>) body;

        // String planID = ObjectUtils.toString(inputMap.get("planId"));
        //
        // List<Map<String, Object>> planData = fps210.getCustPlan(dam, planID);
        //
        // Map<String, Object> outputMap = new HashMap<String, Object>();
        // outputMap.put("planData", planData);
        // this.sendRtnObject(outputMap);
    }

    public void genPDFSafariUplodFile(Object body, IPrimitiveMap header) throws JBranchException, IOException{
        Map map = (Map) body;
        String tempFileName = ObjectUtils.toString(map.get("tempFileName"));
        String base64String = ObjectUtils.toString(map.get("base64"));
        String[] fileBase64 = base64String.split("base64,");

        if (base64String.equals("error") || fileBase64.length < 1) {
            throw new APException("產生 PDF 後上傳失敗! 請洽系統管理員");
        } else {
            String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
            FileOutputStream fos = new FileOutputStream(new File(tempPath, tempFileName).getAbsolutePath());
            fos.write(Base64.decode(fileBase64[1]));
            fos.close();
        }
        this.sendRtnObject(null);
    }

    private List<Map<String, Object>> getRet(DataAccessManager dam, String planID) throws DAOException,
            JBranchException{
        QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        StringBuffer sql = new StringBuffer();
//        sql.append("WITH BASE AS (SELECT PRD_ID, PTYPE, SUM(PURCHASE_TWD_AMT) PURCHASE_TWD_AMT, INV_TYPE ");
//        sql.append("FROM TBFPS_PORTFOLIO_PLAN_SPP ");
//        sql.append("WHERE PLAN_ID = :planID AND PURCHASE_TWD_AMT != 0 ");
//        sql.append("GROUP BY PRD_ID, PTYPE, INV_TYPE), ");
//        sql.append("BASE2 AS (SELECT SUM(PURCHASE_TWD_AMT) PURCHASE_TWD_AMT FROM BASE) ");
//        sql.append("SELECT BASE.PRD_ID, BASE.PTYPE AS PRD_TYPE, BASE.INV_TYPE, ");
//        sql.append("(BASE.PURCHASE_TWD_AMT / DECODE(BASE2.PURCHASE_TWD_AMT, 0, 1, BASE2.PURCHASE_TWD_AMT)) WEIGHT, BASE.PURCHASE_TWD_AMT FROM BASE, BASE2");
        sql.append("SELECT PRD_ID, PTYPE, PURCHASE_TWD_AMT, TARGETS, INV_TYPE, ORDER_STATUS ");
        sql.append("FROM TBFPS_PORTFOLIO_PLAN_SPP WHERE PLAN_ID = :planID ");
        queryCondition.setObject("planID", planID);
        queryCondition.setQueryString(sql.toString());
        return dam.exeQuery(queryCondition);
    }

    private void setPurposeInfo (String sppType, Map<String, Object> amountInfo){
    	
    	if ("2".equals(sppType) || "RETIRE".equals(sppType)) {
            Map<String, Object> university = new HashMap<String, Object>();
            Map<String, Object> institute = new HashMap<String, Object>();
            Map<String, Object> doctor = new HashMap<String, Object>();

            university.put("type", 0);
            university.put("tuition", 0);
            university.put("living", 0);
            university.put("years", 0);
            institute.put("type", 0);
            institute.put("tuition", 0);
            institute.put("living", 0);
            institute.put("years", 0);
            doctor.put("type", 0);
            doctor.put("tuition", 0);
            doctor.put("living", 0);
            doctor.put("years", 0);

            amountInfo.put("university", university);
            amountInfo.put("institute", institute);
            amountInfo.put("doctor", doctor);
        } else if ("1".equals(sppType) || "EDUCATION".equals(sppType)) {
            amountInfo.put("retireAge", 0);
            amountInfo.put("avgLife", 0);
            amountInfo.put("retireSpend", 0);
            amountInfo.put("inheritAmt", 0);

            Map<String, Object> socialInsurance = new HashMap<String, Object>();
            socialInsurance.put("monthly", 0);
            socialInsurance.put("once", 0);
            amountInfo.put("socialInsurance", socialInsurance);

            Map<String, Object> socialWelfare = new HashMap<String, Object>();
            socialWelfare.put("monthly", 0);
            socialWelfare.put("once", 0);
            amountInfo.put("socialWelfare", socialWelfare);

            Map<String, Object> businessInsurance = new HashMap<String, Object>();
            businessInsurance.put("monthly", 0);
            businessInsurance.put("once", 0);
            amountInfo.put("businessInsurance", businessInsurance);

            Map<String, Object> otherInsurance = new HashMap<String, Object>();
            otherInsurance.put("monthly", 0);
            otherInsurance.put("once", 0);
            amountInfo.put("otherInsurance", otherInsurance);
        } else {
        	Map<String, Object> university = new HashMap<String, Object>();
            Map<String, Object> institute = new HashMap<String, Object>();
            Map<String, Object> doctor = new HashMap<String, Object>();

            university.put("type", 0);
            university.put("tuition", 0);
            university.put("living", 0);
            university.put("years", 0);
            institute.put("type", 0);
            institute.put("tuition", 0);
            institute.put("living", 0);
            institute.put("years", 0);
            doctor.put("type", 0);
            doctor.put("tuition", 0);
            doctor.put("living", 0);
            doctor.put("years", 0);

            amountInfo.put("university", university);
            amountInfo.put("institute", institute);
            amountInfo.put("doctor", doctor);
            
            amountInfo.put("retireAge", 0);
            amountInfo.put("avgLife", 0);
            amountInfo.put("retireSpend", 0);
            amountInfo.put("inheritAmt", 0);

            Map<String, Object> socialInsurance = new HashMap<String, Object>();
            socialInsurance.put("monthly", 0);
            socialInsurance.put("once", 0);
            amountInfo.put("socialInsurance", socialInsurance);

            Map<String, Object> socialWelfare = new HashMap<String, Object>();
            socialWelfare.put("monthly", 0);
            socialWelfare.put("once", 0);
            amountInfo.put("socialWelfare", socialWelfare);

            Map<String, Object> businessInsurance = new HashMap<String, Object>();
            businessInsurance.put("monthly", 0);
            businessInsurance.put("once", 0);
            amountInfo.put("businessInsurance", businessInsurance);

            Map<String, Object> otherInsurance = new HashMap<String, Object>();
            otherInsurance.put("monthly", 0);
            otherInsurance.put("once", 0);
            amountInfo.put("otherInsurance", otherInsurance);
        }
    }

    private String getSppTypeText(String type){
        // sa:先寫死
        Map<String, String> mapping = new HashMap<String, String>();
        mapping.put("EDUCATION", "1");
        mapping.put("RETIRE", "2");
        mapping.put("BUY_HOUSE", "3");
        mapping.put("BUY_CAR", "4");
        mapping.put("MARRY", "5");
        mapping.put("OV_EDU", "6");
        mapping.put("TRAVEL", "7");
        mapping.put("OTHER", "8");
        return mapping.get(type);
    }
}
