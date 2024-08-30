package com.systex.jbranch.app.server.fps.ins500;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.sql.Blob;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.rowset.serial.SerialException;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.ibm.icu.text.SimpleDateFormat;
import com.systex.jbranch.app.common.fps.table.TBINS_CON_MAINVO;
import com.systex.jbranch.app.common.fps.table.TBINS_REPORTVO;
import com.systex.jbranch.app.server.fps.ins.parse.WSMappingParserUtils;
import com.systex.jbranch.app.server.fps.ins810.FubonIns810;
import com.systex.jbranch.app.server.fps.ins810.INS810;
import com.systex.jbranch.app.server.fps.ins810.INS810InputVO;
import com.systex.jbranch.app.server.fps.ins810.INS810OutputVO;
import com.systex.jbranch.app.server.fps.ins810.PolicySuggestInputVO;
import com.systex.jbranch.app.server.fps.insjlb.FubonInsjlb;
import com.systex.jbranch.app.server.fps.insjlb.InsjlbUtils;
import com.systex.jbranch.app.server.fps.insjlb.vo.GetInsPremInputVO;
import com.systex.jbranch.app.server.fps.insjlb.vo.GetInsPremOutputVO;
import com.systex.jbranch.app.server.fps.insjlb.vo.GetOdItemListInputVO;
import com.systex.jbranch.app.server.fps.insjlb.vo.GetOldItemListOutputVO;
import com.systex.jbranch.comutil.collection.CollectionSearchUtils;
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.commons.PdfUtil;
import com.systex.jbranch.fubon.commons.ValidUtil;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.report.ReportIF;
import com.systex.jbranch.platform.common.report.factory.ReportFactory;
import com.systex.jbranch.platform.common.report.generator.ReportGeneratorIF;
import com.systex.jbranch.platform.common.report.reportdata.ReportData;
import com.systex.jbranch.platform.common.report.reportdata.ReportDataIF;
import com.systex.jbranch.platform.common.util.ObjectUtil;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

import java.util.Arrays;

/**
 * 保險商品組合
 * @author 1800020 Sen
 *
 */
@Component("ins500")
@Scope("request")
public class INS500 extends FubonWmsBizLogic {
	private Logger logger = LoggerFactory.getLogger(INS500.class);

	private DataAccessManager dam = null;
	
	//====================== 引用外部 Service ======================
	@Autowired @Qualifier("insjlb")
	private FubonInsjlb insjlb;
	
	public FubonInsjlb getInsjlb() {
		return insjlb;
	}

	public void setInsjlb(FubonInsjlb insjlb) {
		this.insjlb = insjlb;
	}
		
	@Autowired @Qualifier("ins810")
	private FubonIns810 ins810;
	
	public FubonIns810 getIns810() {
		return ins810;
	}

	public void setIns810(FubonIns810 ins810) {
		this.ins810 = ins810;
	}
			
	//====================== For FontEnd 的 Service ======================
	/**
	 * 執行查詢帶出客戶內外保單
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	@SuppressWarnings("rawtypes")
	public void query(Object body, IPrimitiveMap header) throws JBranchException {
		INS500InputVO inputVO = (INS500InputVO) body;
		INS500OutputVO outputVO = new INS500OutputVO();
		
		// 資料處理 & 回傳
		outputVO.setSavingsList(getSavingsList(inputVO));
		sendRtnObject(outputVO);
	}
	
	/**
	 * 取得第一年度末生存金
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	@SuppressWarnings("rawtypes")
	public void getFirstRepay(Object body, IPrimitiveMap header) throws JBranchException {
		INS500InputVO inputVO = (INS500InputVO) body;
		int age = getAge(inputVO.getBirth());
		inputVO.setAge(String.valueOf(age));
		List<Map<String, Object>> suggestDataList = inputVO.getSuggestList();
		finalResultDatasFontEnd(inputVO, suggestDataList);
		INS500OutputVO outputVO = new INS500OutputVO();
		outputVO.setReimbursementList(suggestDataList);
		sendRtnObject(new Object[]{age, outputVO});
	}
	
	/**
	 * 列印保險商品組合建議書
	 * @param body
	 * @param header
	 * @throws JBranchException 
	 * @throws IOException 
	 * @throws SQLException 
	 * @throws SerialException 
	 */
	@SuppressWarnings({ "unchecked", "static-access", "rawtypes" })
	public void printSuggestReport(Object body, IPrimitiveMap header) throws JBranchException, SerialException, SQLException, IOException {
		INS500InputVO inputVO = (INS500InputVO) body;
				
		//取得檔案與報表路徑
		Map<String, String> paramMap = returnFileNameAndPath(inputVO);
		
		// 列印儲存 + 取檔 + 新增報告書
		afterPrintSave(paramMap.get("reportURL"), inputVO.getCustId(), paramMap.get("fileName"),null);
		
		notifyClientToDownloadFile(paramMap.get("reportURL"), paramMap.get("fileName"));
	}
	
	/**
	 * 取得當前客戶的保險年齡
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void calCustPolicyAge(Object body, IPrimitiveMap header) throws JBranchException {
		INS500InputVO inputVO = (INS500InputVO) body;
		sendRtnObject(getAge(inputVO.getBirth()));
	}
	
	/**
	 * 取得保障年期
	 * @param body
	 * @param header
	 * @throws DAOException 
	 * @throws JBranchException
	 */
	public void getCoverageSel(Object body, IPrimitiveMap header) throws DAOException, JBranchException {
		INS500InputVO inputVO = (INS500InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sql = new StringBuilder();
		sql.append("select GUARANTEE_ANNUAL from TBPRD_INS where 1=1 and PRD_ID = :prdId and INSPRD_ANNUAL = :insprdAnnual ");
		queryCondition.setObject("prdId", inputVO.getPrdID());
		queryCondition.setObject("insprdAnnual", inputVO.getAnnual());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> guaranteeAList = dam.exeQuery(queryCondition);
		this.sendRtnObject(guaranteeAList.get(0).get("GUARANTEE_ANNUAL"));
	}
	
	//====================== For 資訊源調用 的 Method ======================
	/**
	 * 調用 資訊源取得 GetOdItemList
	 * @param inputVO
	 * @return
	 * @throws JBranchException
	 */
	private GetOldItemListOutputVO getOldItemListOutputVO(INS500InputVO inputVO) throws JBranchException {
		// 取得登入相關資料
		String LoginBraNbr = (String) getCommonVariable(SystemVariableConsts.LOGINBRH);
		ArrayList<String> loginAO = (ArrayList<String>) getUserVariable(FubonSystemVariableConsts.LOGIN_AOCODE_LIST);
		GetOdItemListInputVO getOldItemListInputVO = new GetOdItemListInputVO();
		
		// 調用 JLB - getOldItem
		getOldItemListInputVO.setCustId(inputVO.getCustId());
		getOldItemListInputVO.setPlanTypes("L,R");
		getOldItemListInputVO.setLoginBranch(LoginBraNbr);
		getOldItemListInputVO.setLoginAOCode(loginAO);
		return getInsjlb().getOldItemList(getOldItemListInputVO);
	}
	
	/**
	 * 調用 資訊源取得 GetInsPrem
	 * @param age
	 * @param birth
	 * @param suggestDataMap 產品來源
	 * @return
	 */
	private Object[] getInsPremResult(INS500InputVO inputVO500, Map<String, Object> suggestDataMap, int typeIndex) {
		Object[] resultObj = new Object[3];
		try{
			GetInsPremInputVO inputVO = new GetInsPremInputVO();
			List<Map<String, Object>> lstInsData = new ArrayList<Map<String, Object>>();
			Map<String, Object> lstInsDataMap = new HashMap<String, Object>();
			lstInsDataMap.put("CURR_CD", suggestDataMap.get("CURR_CD"));
			lstInsDataMap.put("PRD_KEYNO", suggestDataMap.get("prodKeyno" + typeIndex));
			lstInsDataMap.put("INSURED_AGE", inputVO500.getAge());
			lstInsDataMap.put("INSURED_BIRTHDAY", inputVO500.getBirth());
			lstInsDataMap.put("JOB_GRADE", "1");
			lstInsDataMap.put("PREMTERM", suggestDataMap.get("insPrdAnnualModel" + typeIndex)); // 繳費年期
			lstInsDataMap.put("INSYEARFEE", suggestDataMap.get("policyFee" + typeIndex)); // 保費
			lstInsDataMap.put("INSUREDAMT", suggestDataMap.get("assureAmt" + typeIndex)); // 保額
			
			// 先保留如果之後要減一年的話
			// mantis : 本功能 請調整 第1年 還本or生存金為 取 資訊源 第2年的數值.
			// 因為coverage01 為共用的因此在該支功能的 EFFECTED_DATE 做手腳
			// 模擬往前推一年，如此第一年的生存金是多少就是多少
			Calendar prevYear = Calendar.getInstance();
		    prevYear.add(Calendar.YEAR, -0); 
			lstInsDataMap.put("EFFECTED_DATE", prevYear.getTime());
			
			if(typeIndex == 3) {
				System.out.println("送資訊源前的lstInsDataMap " + new Gson().toJson(lstInsDataMap));
			}
			
			lstInsData.add(lstInsDataMap);
			inputVO.setFuncType("2");
			inputVO.setLstInsData(lstInsData);
			logger.info("送資訊源前的 VO " + new Gson().toJson(inputVO));
			GetInsPremOutputVO outputVO = getInsjlb().getInsPrem(inputVO);
			resultObj[0] = true;
			resultObj[1] = outputVO;
			resultObj[2] = outputVO.getLstExpression();
		} catch(Exception ex){
			ex.printStackTrace();
			logger.info("錯誤的資訊源組合來源 " + new Gson().toJson(suggestDataMap));
			logger.info("調用資訊源錯誤 " + suggestDataMap.get("PRD_ID") + " " + ex);
			resultObj[0] = false;
			resultObj[1] = "商品: " + suggestDataMap.get("PRD_ID") + " , " + ex.getMessage();
		}
		return resultObj;
	}
	
	//====================== For 回傳用 的 加工 Method (FontEnd Show)======================
	/**
	 * 處理 GetOdItemList 資訊源回來的資料 (For First Table)
	 * @param oldItemLList
	 * @return
	 * @throws JBranchException
	 */
	private List<Map<String,Object>> finalResultDatasFontEnd(List<Map<String,Object>> oldItemLList) throws JBranchException {
		// 資料整理 [只要有當年度生存金的資料，其餘不要]
		List<Map<String,Object>> oldItemLListNew = new ArrayList<Map<String,Object>>();
		List<String> prodKeys = new ArrayList<String>();
		if(oldItemLList != null && oldItemLList.size()>0) {
			for(Map<String, Object> oldItemLMap : oldItemLList) {
				if(!StringUtils.isEmpty(ObjectUtils.toString(oldItemLMap.get("REPAY")))) {
					prodKeys.add(String.valueOf(oldItemLMap.get("PROD_KEYNO")));
					oldItemLListNew.add(oldItemLMap);
				}
			}
		}
		prepareQueryResult(oldItemLListNew, findProdInfo(prodKeys));
		return oldItemLListNew;
	}
	
	/**
	 * 準備回傳的資料加工 (For First Table)
	 * @param oldItemLList 資訊源回來的資料
	 * @param prodInfoMap 產品資料
	 */
	private void prepareQueryResult(List<Map<String, Object>> oldItemLList, Map<String, Map<String, Object>> prodInfoMap) {
		if(oldItemLList == null){
			return;
		}
		for(Map<String, Object> oldItemL : oldItemLList) {
			Object listObj = prodInfoMap.get(oldItemL.get("PROD_KEYNO")).get("LIST_Y");
			String[] listY = null;
			if(listObj != null) {
				listY = ((String)listObj).split(",");
			}
			Map<String, String> premtermMap = InsjlbUtils.findSimilarIntVal(
					(String)prodInfoMap.get(oldItemL.get("PROD_KEYNO")).get("ITEM_Y"), 
					(String)oldItemL.get("PREMTERM"), ",", true);
			Object idx = premtermMap.get("idx");
			oldItemL.put("PREMTERM_NAME", listY[(Integer)idx]);
		}
	}
	
	/**
	 * 處理 GetInsPrem 資訊源回來的資料 (For Second Table)
	 * @param inputVO
	 * @param suggestDataList 所有列表資料
	 */
	private void finalResultDatasFontEnd(INS500InputVO inputVO, List<Map<String, Object>> suggestDataList) {
		for(Map<String, Object> suggestDataMap : suggestDataList) {
			Object[] resultObj = getInsPremResult(inputVO, suggestDataMap, 2);
			if((boolean)resultObj[0]) {
				GetInsPremOutputVO outputVO = (GetInsPremOutputVO)resultObj[1];
				// mantis : 本功能 請調整 第1年 還本or生存金為 取 資訊源 第2年的數值.
				BigDecimal repay = BigDecimal.ZERO;
		 		if(CollectionUtils.isNotEmpty(outputVO.getLstCoverAgePrem())) {
					for(Map<String, Object> agePremMap : (List<Map<String, Object>>)outputVO.getLstCoverAgePrem()) {
						if ((Short)agePremMap.get("THEYEAR") == 2 && agePremMap.get("REPAY") != null && ((BigDecimal)agePremMap.get("REPAY")).compareTo(BigDecimal.ZERO) == 1) {
							repay = (BigDecimal) agePremMap.get("REPAY");	
							break;
						}
					}
				}
				suggestDataMap.put("REPAY", repay);
				suggestDataMap.put("ERROR", null);
			} else {
				suggestDataMap.put("ERROR", resultObj[1]);
			}
		}
	}
	
	//====================== For 回傳用 的 加工 Method (Report Show)======================
	/**
	 * 準備報表要的資料 (For Report 既有)
	 * @param outputVO 資訊源回來的資料
	 * @param selectFirstTableDatas 有勾選的資料
	 * @return 所有商品的coverAgePrems
	 */
	private List<Object> finalResultDatas(GetOldItemListOutputVO outputVO, List<Object> selectFirstTableDatas) {
		List<Object> prodCoverAgePrems = new ArrayList<Object>();
		for(Map<String, Object> oldItemMap : (List<Map<String,Object>>)outputVO.getOldItemLlist()) {
			GenericMap gmp = new GenericMap(oldItemMap);
			if(selectFirstTableDatas.contains(gmp.get("PROD_ID"))) {
				prodCoverAgePrems.add(gmp.get("lstCoverAgePrem"));
			}
		}
		return prodCoverAgePrems;
	}
	
	/**
	 * 準備報表要的資料 (For Report 主推 or 推薦)
	 * @param resultObj 資訊源回來的資料
	 * @param selectSecondTableDatas 有勾選的資料
	 * @return 所有商品的coverAgePrems
	 * @throws JBranchException
	 */
	private List<Object>  finalResultDatas(List<Object[]> resultObjs) throws JBranchException {
		List<Object> prodCoverAgePrems = new ArrayList<Object>();
		for(Object[] resultObj : resultObjs) {
			if((boolean)resultObj[0]) {
				prodCoverAgePrems.add(((GetInsPremOutputVO)resultObj[1]).getLstCoverAgePrem());
			}
		}
		return prodCoverAgePrems;
	}

	/**
	 * 準備 & 合併 既有 + 主推
	 * @param inputVO
	 * @return
	 * @throws JBranchException
	 */
	@SuppressWarnings("unchecked")
	private List<Object>[] prepareCombianData(INS500InputVO inputVO) throws JBranchException {
		List<Object> firstTable = inputVO.getSelectPrdDatas().get(0); // 既有商品勾選的資料
		List<Object> secondTable = inputVO.getSelectPrdDatas().get(1); // 主推商品勾選的資料
		List<Object> thirdTable = inputVO.getSelectPrdDatas().get(2); // 推薦商品勾選的資料
		
		// 既有產品呼叫 getOldItemList
		List<Object> firstCoverAgePrem = finalResultDatas(getOldItemListOutputVO(inputVO), firstTable);
		System.out.println("既有 JSON：" + new Gson().toJson(firstCoverAgePrem)); // 既有的 JSON
		
		// 主推產品呼叫 getInsPrem
		List<Object[]> secondResults = new ArrayList<Object[]>();
		for(Object obj : secondTable) {
			Map<String, Object> resultMap = (Map<String, Object>) obj;
			secondResults.add(getInsPremResult(inputVO, resultMap, 2));
		}
		List<Object> secondCoverAgePrem = finalResultDatas(secondResults);
		System.out.println("主推 JSON：" + new Gson().toJson(secondCoverAgePrem)); // 主推的 JSON
		
		// 推薦產品呼叫 getInsPrem (只會有一筆)
		List<Object[]> thirdResults = new ArrayList<Object[]>();
		
		System.out.println("推薦清單：" + new Gson().toJson(thirdTable));
		
		for(Object obj : thirdTable) {
			Map<String, Object> resultMap = (Map<String, Object>) obj;
			thirdResults.add(getInsPremResult(inputVO, resultMap, 3));
		}
		List<Object> thirdCoverAgePrem = finalResultDatas(thirdResults);
		System.out.println("推薦 JSON：" + new Gson().toJson(thirdCoverAgePrem)); // 推薦的 JSON
		
		// 所有要合計的資料集
		List<Object>[] totalList = new ArrayList[]{new ArrayList<Object>(), new ArrayList<Object>(), new ArrayList<Object>()}; 
		totalList[0].addAll(firstCoverAgePrem); // 既有
		totalList[0].addAll(secondCoverAgePrem); // 主推
		totalList[1].addAll(thirdCoverAgePrem); // 推薦
		if(CollectionUtils.isNotEmpty(thirdResults)) {
			Object lst = ((Object[])thirdResults.get(0))[2];
//			Object lst = null;
			totalList[2].addAll((Collection<? extends Object>) (lst == null ? new ArrayList() : lst)); // 推薦的給付項目
			System.out.println();
		}
		return totalList;
	} 
	
	/**
	 * 組合加總既有 & 主推
	 * @param age
	 * @param firstTableResult
	 * @param secondTableResult
	 * 
	 * =================作法說明======================
	 * 取得所有的 lstCoverageprem List ==> List<List<Map>> 
	 * 外層 List 表示幾個產品，內層 List 表示每一年度 Map 資料 
	 * 根據 每年遞增加一判斷 Map裡是否存在 THEAGE 決定當前回圈是否繼續 
	 * 其中一個產品有就繼續，直到都沒有
	 * 判斷合計的 List<Map> 中是否存在 THEAGE
	 * 舉例
	 * A產品 36 歲 REPAY 50 PREM 100 放入 List<Map>
	 * 下一B產品 36 歲 確認 List<Map>是否存在了
	 * 有取出來合計，沒有建立新的 
	 * 然後就一直這樣循環直到最大的 THEAGE 結束  
	 *  
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> dataCombian(String age, List<Object> totalList, String[] columns) {
		// 變數定義
		final String finalAge = age; // 亙古不變的年齡
		boolean has = false; // 我是否該繼續執行? 其中一個產品有就繼續，直到都沒有
		
		// 報表要得資料集
		List<Map<String, Object>> reportDatas = new ArrayList<Map<String, Object>>(); 
		
		// 採用 do while 至少先跑一次在看要不要繼續
		do{
			has = false; // 每次重來，讓 for 重算
			
			// 每一年度的詳細資料
			Map<String, Object> reportMap = new HashMap<String, Object>();
			
			// 開始跑 [幾個產品就跑幾次，每次都會取出 lstCoveragePrem]
			for(Object coverAgePremObj : totalList) {
				// 當前年度 nowAge 與 下一年度 nextAge
				Short nowAge = Short.parseShort(age), nextAge = Short.parseShort(String.valueOf(Integer.parseInt(age) + 1));

				// 取得 當年度的 Map
				List<Map<String, Object>> currentPremList = (List<Map<String, Object>>) coverAgePremObj;
				Map<String, Object> currentMap = CollectionSearchUtils.findMapInColleciton(currentPremList, "THEAGE", nowAge); // for 取得不同商品相同保險年齡 Map
				Map<String, Object> nextMap = CollectionSearchUtils.findMapInColleciton(currentPremList, "THEAGE", nextAge); // 判斷是否有下一年齡
				has |= (nextMap!= null && nextMap.size() >0); // 只要一筆有迴圈就繼續[統計該商品下年度是否還有再取值]
				
				
				// 資料判斷重取運算或建立新一年度資料
				if(currentMap != null && currentMap.size() >0) {
					dataMapCalCulation(reportDatas, reportMap, currentMap, age, finalAge, columns);
				} else {
					continue;
				}
			}
			age = String.valueOf(Integer.parseInt(age) + 1);
		} while(has); // 如果產品還有下一年度 PREM 則繼續
		
		return reportDatas;
	}
	
	/**
	 * 對應資料 
	 * @param reportDatas 最後要給報表的 List
	 * @param reportMap 報表當年度的 Map
	 * @param currentMap 實際產品當年度的 Map
	 * @param age while 年度 (變動累加值)
	 * @param finalAge 一開始初始值年齡 (不變固定值)
	 * @param columns 所有要 SUM 的欄位
	 * 
	 * =================作法說明======================
	 * listSize 表示當前放了幾個年度了 假設 listSize = 1 表示目前放了第一年度
	 * nowSize 表示第幾年度 假設起始為 36 歲，當前看的是 36 歲 所以 nowSize = 0
	 * 如果listSize = 1 表示為一開始的 36 歲資料 所以 listSize >= nowSize + 1 
	 * 表示已存在 報表組合的 Map 拿出來直接進行 sum
	 * 否則建立一個新的年度 Map
	 * 
	 */
	private void dataMapCalCulation(List<Map<String, Object>> reportDatas, Map<String, Object> reportMap, Map<String, Object> currentMap,
			String age, String finalAge, String[] columns) {
		int listSize = reportDatas.size();
		int nowSize = Integer.parseInt(age) - Integer.parseInt(finalAge);
		boolean isExists = listSize >= nowSize +1; 
		
		// 存在 報表組合的 Map 拿出來直接進行 sum 否則使用一個新的年度 Map
		Map<String, Object> tempMap = isExists ? reportDatas.get(nowSize) : reportMap;
		tempMap.put("AGE", currentMap.get("THEAGE")); // 年齡
		calculationValue(tempMap, currentMap, columns);
		if(!isExists) {
			reportDatas.add(reportMap);
		}
	}
	
	/**
	 * 取出 Map 同一欄位的值並且進行 合計
	 * @param saveMap 實際存資料的 Map
	 * @param resourceMap 來源的 Map
	 * @param columns 所有欄位
	 * 
	 * 所有欄位運算作法一致，都是取出然後 add
	 */
	private void calculationValue(Map<String, Object> saveMap, Map<String, Object> resourceMap, String[] columns) {
		for(String column : columns) {
			BigDecimal value = BigDecimal.ZERO;
			GenericMap saveGmp = new GenericMap(saveMap);
			GenericMap resourceGmp = new GenericMap(resourceMap);
			value = saveGmp.getBigDecimal(column).add(resourceGmp.getBigDecimal(column));
			saveGmp.put(column, value);
		}
	}
	
	/**
	 * 產生報表要的 list
	 * @param repayList
	 * @param indemnificationList
	 * @return
	 */
	private List<Map<String, Object>> finalReportList(List<Map<String, Object>> repayList, List<Map<String, Object>> indemnificationList, List<Object> lstExpression, Map<String, Object> indemnificationDataMap) {
		Short starYear = 0, finalYear = 0;
		List<Map<String, Object>> finalList = new ArrayList<Map<String, Object>>();
		if(CollectionUtils.isNotEmpty(repayList) && CollectionUtils.isNotEmpty(indemnificationList)) {
			Short rS = ((Short)repayList.get(0).get("AGE")), iS = ((Short)indemnificationList.get(0).get("AGE"));
			Short rF = ((Short)repayList.get(repayList.size()-1).get("AGE")), iF = ((Short)indemnificationList.get(indemnificationList.size()-1).get("AGE"));
			starYear = rS < iS ? rS : iS; // 開始取其小
			finalYear = rF > iF ? rF : iF; // 結束取其大
			doFinalReportListCombine(repayList, indemnificationList, lstExpression, finalList, starYear, finalYear, indemnificationDataMap);
		} else if (CollectionUtils.isNotEmpty(repayList)) {
			Short rS = ((Short)repayList.get(0).get("AGE")), rF = ((Short)repayList.get(repayList.size()-1).get("AGE"));
			starYear = rS;
			finalYear = rF;
			doFinalReportListCombine(repayList, null, null, finalList, starYear, finalYear, null);
		} else if(CollectionUtils.isNotEmpty(indemnificationList)) {
			Short iS = ((Short)indemnificationList.get(0).get("AGE")), iF = ((Short)indemnificationList.get(indemnificationList.size()-1).get("AGE"));
			starYear = iS;
			finalYear = iF;
			doFinalReportListCombine(null, indemnificationList, lstExpression, finalList, starYear, finalYear, indemnificationDataMap);
		} 
		return finalList;
	}
	
	/**
	 * 實際執行合併兩邊資烙 還本 + 保障
	 * @param repayList
	 * @param indemnificationList
	 * @param finalList
	 * @param starYear
	 * @param finalYear
	 */
	private void doFinalReportListCombine(List<Map<String, Object>> repayList, List<Map<String, Object>> indemnificationList, List<Object> lstExpression, 
			List<Map<String, Object>> finalList, Short starYear, Short finalYear, Map<String, Object> indemnificationDataMap) {
		// 滿期金計算
		BigDecimal earned = null;
		Short year = null;
		Short age = null;
		if(MapUtils.isNotEmpty(indemnificationDataMap)) {
			GenericMap gMap = new GenericMap(indemnificationDataMap);
			// 保費總額 = 保費 (policyFee3) * 年期 (insPrdAnnualModel3) * 滿期金比 (earnedRatio)
			BigDecimal policy = gMap.getBigDecimal("policyFee3");
			BigDecimal insPrdAnnualModel = gMap.getBigDecimal("insPrdAnnualModel3");
			BigDecimal earnedRatio = gMap.getBigDecimal("earnedRatio");
			earned = policy.multiply(insPrdAnnualModel).multiply(earnedRatio);
			
			// 保額
			String unit = gMap.getNotNullStr("prdUnit3");
			BigDecimal assureAmt = gMap.getBigDecimal("assureAmt3");
			switch(unit) {
				case "1" : {
					break;
				}
				case "2" : {
					assureAmt = assureAmt.multiply(new BigDecimal(100));
					break;
				}
				case "3" : {
					assureAmt = assureAmt.multiply(new BigDecimal(1000));
					break;
				}
				case "4" : {
					assureAmt = assureAmt.multiply(new BigDecimal(10000));
					break;
				}
			}
			
			// 滿期金類型 (earnedCalWay) 1-取其大 (保費總額 VS 保額 * 單位), 2-保費總額
			int earnedCalWay = Integer.parseInt(gMap.getNotNullStr(("earnedCalWay")));
			
			// 滿期金計算
			if(earnedCalWay == 1) {
				earned = earned.compareTo(assureAmt) == 1 ? earned : assureAmt;
			} 
			
			// 滿期金年度
			year = Short.parseShort(ObjectUtils.toString(indemnificationDataMap.get("earnedYear")));
			Map<String, Object> sortNoMap = CollectionSearchUtils.findMapInColleciton(indemnificationList, "THEYEAR", new BigDecimal(year));
			if(MapUtils.isNotEmpty(sortNoMap)) {
				age = sortNoMap.get("AGE") != null ? (Short)sortNoMap.get("AGE") : null;
			}
		}
		
		// 每年重組
		for(Short i = starYear; i <= finalYear; i++) {
			Map<String, Object> finalReportMap = new HashMap<String, Object>();
			Map<String, Object> repayMap = repayList != null ? CollectionSearchUtils.findMapInColleciton(repayList, "AGE", i) : null;
			Map<String, Object> indemnificationMap = repayList != null ? CollectionSearchUtils.findMapInColleciton(indemnificationList, "AGE", i) : null;
			
			finalReportMap.put("AGE", i);
			
			if(i.equals(age)) {
				finalReportMap.put("EARNED", earned);
			}
			
			if(MapUtils.isNotEmpty(repayMap)) {
				for(Entry<String, Object> entry : repayMap.entrySet()) {
					finalReportMap.put(entry.getKey(), entry.getValue());
				}
			}
			
			if(MapUtils.isNotEmpty(indemnificationMap)) {
				for(Entry<String, Object> entry : indemnificationMap.entrySet()) {
					String key = entry.getKey();
					if(!"THEYEAR".equals(key)) {
						if("PREMIUM".equals(key)) key = entry.getKey() + "2";
						finalReportMap.put(key, entry.getValue());
					}
				}
			}
			
			if(CollectionUtils.isNotEmpty(lstExpression) && MapUtils.isNotEmpty(indemnificationMap)) {
				String[] sortNoColumn = new String[]{"10401_S", "30401_1S", "B0012_S", "70514_P"};
				for(String sortNo : sortNoColumn) {
					Map<String, Object> sortNoMap = CollectionSearchUtils.findMapInColleciton(lstExpression, "SORTNO", sortNo);
					finalReportMap.put(sortNo, MapUtils.isNotEmpty(sortNoMap) ? sortNoMap.get("DESCRIPTION") : null);
				}
			}
			finalList.add(finalReportMap);
		}
	}
	
	//======================== WS 調用 ========================
	/**
	 * 提供給 APP 使用的 API <br>
	 * <code> <b> 2.7.3 </b> 計算保費</code>
	 * 
	 * @param inputVO
	 *            :
	 * @return Object :
	 * @throws JBranchException
	 *             : <br>
	 *             <p>
	 *             <b>客製訊息如下 :</b>
	 *             <li>查無資料:</li>
	 *             </p>
	 */
	public void calculatePremium(Object body, IPrimitiveMap<Object> header)throws JBranchException {
		dam = this.getDataAccessManager();
		// 取得傳進來的資料
		GenericMap inputGmap = new GenericMap((Map) body);
		// 取得客戶ID
		String custId = inputGmap.getNotNullStr("custId");
		// 取得險種(商品)代碼
		String prdId = inputGmap.getNotNullStr("prdId");
		// 參數版號
		String paraNo = inputGmap.getNotNullStr("paraNo");
		// 保險商品Key
		String prdKeyNo = inputGmap.getNotNullStr("prdKeyNo");
		// 缺口數值
		float gapValue = 0;
		if(inputGmap.get("gapValue") != null){
			gapValue = ((Double) inputGmap.get("gapValue")).floatValue();			
		}
		// 保額
		float insuredamt = 0;
		if (inputGmap.get("insuredamt") != null) {
			insuredamt = ((Double) inputGmap.get("insuredamt")).floatValue();
		}
		// 疾病類型 4:重大疾病, A:癌症, B:長期看護
		String suggestType = inputGmap.getNotNullStr("suggestType");
		//是否換匯
		boolean chgExch = false;
		if(inputGmap.get("chgExch") != null){
			chgExch = inputGmap.get("chgExch");		
		}
		// 用來放計算結果的map
		Map outputMap = new HashMap();

		// 取得客戶資料
		List<Map<String, Object>> custDataList = getCustDataList(custId);

		// 計算保險年齡
		int age = getAge(new Date(((Timestamp) custDataList.get(0).get("BIRTH_DATE")).getTime()));

		// 取得計算費率用資料
		List<Map<String, Object>> calRateList = getCalRateList(prdKeyNo);

		// 求取費率、匯率與推薦商品所需的資料
		PolicySuggestInputVO inputVO = new PolicySuggestInputVO();
		gatherNeededInfo(inputVO, custDataList, calRateList, age, paraNo);

		// 求出費率與匯率
		BigDecimal premRate = getIns810().getPremRate(inputVO, dam);
		Map<String, BigDecimal> refExcRate = getIns810().queryRefExcRate();

		// 找出推薦的商品list
		List<Map<String, Object>> suggestPrdList = getIns810().getSuggestDataParser(inputVO, dam);
		System.out.println(suggestPrdList.get(0).get("KEY_NO"));
		Map<String, Object> suggestPrdMap = null;
		// 取得選取的推薦商品資料
		List<Map<String, Object>> suggestPrdForDiseaseList = new ArrayList<Map<String, Object>>();
		// 處理重大疾病
		if ("4".equals(suggestType) || "A".equals(suggestType)|| "B".equals(suggestType)) {
			for (Map<String, Object> map : suggestPrdList) {
				boolean sameKeyNo = false;
				String[] keyNos = ((String) map.get("KEY_NO")).split(",");
				for (String keyNo : keyNos) {
					if (prdKeyNo.equals(keyNo)) {
						sameKeyNo = true;
						break;
					}
				}
				if (sameKeyNo) {
					Map<String, Object> suggestPrdForDiseaseMap = CollectionSearchUtils.findMapInColleciton(suggestPrdList, "PRD_ID",prdId);
					suggestPrdForDiseaseList.add(suggestPrdForDiseaseMap);
				}
			}
			suggestPrdMap = CollectionSearchUtils.findMapInColleciton(suggestPrdForDiseaseList, "SUGGEST_TYPE",suggestType.charAt(0));
		} else {
			suggestPrdMap = CollectionSearchUtils.findMapInColleciton(suggestPrdList, "PRD_ID", prdId);
		}

		// 取得已選取年期的推薦商品資料
		List<Map<String, Object>> insprdAnnualList = (List<Map<String, Object>>) suggestPrdMap.get("insprd_annualList");
		Map<String, Object> selectedMap = CollectionSearchUtils.findMapInColleciton(insprdAnnualList, "KEY_NO", prdKeyNo);

		// 計算保費
		int unit = 1;
		String PRD_UNIT = ObjectUtils.toString(suggestPrdMap.get("PRD_UNIT"));
		String CVRG_RATIO = ObjectUtils.toString(selectedMap.get("CVRG_RATIO"));
		float POLICY_AMT_MIN = Float.parseFloat((String) selectedMap.get("POLICY_AMT_MIN"));  // 保額下限
		float POLICY_AMT_MAX = Float.parseFloat((String) selectedMap.get("POLICY_AMT_MAX"));  // 保額上限
		float POLICY_AMT_DISTANCE = Float.parseFloat((String) selectedMap.get("POLICY_AMT_DISTANCE"));  // 保額累加級距

		if ("2".equals(PRD_UNIT)) {
			unit = 100;
		} else if ("3".equals(PRD_UNIT)) {
			unit = 1000;
		} else if ("4".equals(PRD_UNIT)) {
			unit = 10000;
		}

		insuredamt = inputGmap.get("insuredamt") != null ? insuredamt : gapValue / (float) unit / Float.parseFloat(CVRG_RATIO);

		if (insuredamt < POLICY_AMT_MIN) {
			insuredamt = POLICY_AMT_MIN;
		} else if (insuredamt > POLICY_AMT_MAX) {
			insuredamt = POLICY_AMT_MAX;
		} else {
			insuredamt = (float) (POLICY_AMT_MIN + (Math.ceil((insuredamt - POLICY_AMT_MIN) / POLICY_AMT_DISTANCE) * POLICY_AMT_DISTANCE));
		}
		
		//原幣(台幣)的保額
		float origonalInsuredamt = insuredamt;
		
		//需要換匯(INS500需要；INS210只會推薦TWD商品、INS450則式會顯示幣別，故不需乘上匯率)
		if(chgExch){
			String CURR_CD = calRateList.get(0).get("CURR_CD").toString();
			float refExcRateVal;
			if (refExcRate.get(CURR_CD) != null) {
				refExcRateVal = refExcRate.get(CURR_CD).floatValue();
			} else {
				refExcRateVal = 1;
			}
			
			insuredamt = insuredamt * refExcRateVal;			
		}
		
		if(premRate == null){
			throw new APException("無此險種保費資訊");
		}
		
		//保費
		BigDecimal insyearfee = BigDecimal.valueOf(insuredamt).multiply(premRate);

		//第一年度末生存金
		BigDecimal aliveFee = null;
		
		//取得第一年度末生存金
		if(chgExch){
			//處理第一年度生存金用
			List<Map<String,Object>> suggestList = new ArrayList<Map<String,Object>>();
			//主推還本型商品
			List<Map<String,Object>> reimbursementList = getIns810().getSuggestDataParser(new PolicySuggestInputVO(null, getParaNo("8"), null,null), this.getDataAccessManager());
			
			Map<String,Object> dataMap = CollectionSearchUtils.findMapInColleciton(reimbursementList, "PRD_ID", prdId);
			
			Map<String,Object> suggestMap = new HashMap<String, Object>();
						
			suggestMap.put("CURR_CD", dataMap.get("CURR_CD"));
			suggestMap.put("prodKeyno2", dataMap.get("INSDATA_KEYNO"));
			suggestMap.put("insPrdAnnualModel2", selectedMap.get("paymentyearSel"));  // 繳費年期
			suggestMap.put("policyFee2", insyearfee);  // 保費
			suggestMap.put("assureAmt2", insuredamt);  // 保額
			
			suggestList.add(suggestMap);
			
			INS500InputVO input500VO = new INS500InputVO();
			input500VO.setAge(String.valueOf(age));
			input500VO.setBirth(new Date(((Timestamp) custDataList.get(0).get("BIRTH_DATE")).getTime()));
			
			finalResultDatasFontEnd(input500VO, suggestList);
			
			//第一年度末生存金
			aliveFee = (BigDecimal) suggestList.get(0).get("REPAY");
		}

		// prdId : 險種(商品)代碼
		outputMap.put("prdId", calRateList.get(0).get("PRD_ID").toString());
		// insuredamt : 保額
		outputMap.put("insuredamt", origonalInsuredamt);
		// inyearfee : 保費
		outputMap.put("insyearfee", insyearfee);
		if(chgExch){
			// aliveFee : 第一年度末生存金
			outputMap.put("aliveFee", aliveFee);			
		}

		this.sendRtnObject(outputMap);
	}
	
	/**
	 * 提供給 APP 使用的 API <br>
	 * <code> <b> 2.9.1 </b> 取得保險商品組合參數</code>
	 * 
	 * @param inputVO
	 *            :
	 * @return Object :
	 * @throws JBranchException
	 *             : <br>
	 *             <p>
	 *             <b>客製訊息如下 :</b>
	 *             <li>查無資料:</li>
	 *             </p>
	 */
	public void getInsuranceTiedSaleRefData(Object body, IPrimitiveMap<Object> header) throws JBranchException {
		dam = this.getDataAccessManager();
		// 取得傳進來的資料
		GenericMap inputGmap = new GenericMap((Map) body);
		// 取得客戶ID
		String custId = inputGmap.getNotNullStr("custId");
		// 用來放計算結果的map
		Map outputMap = new HashMap();
					
		//主推還本型商品
		List<Map<String,Object>> reimbursementList = getIns810().getSuggestDataParser(new PolicySuggestInputVO(null, getParaNo("8"), null,null), this.getDataAccessManager());
		//推薦保障型商品
		List<Map<String,Object>> protectionList = getIns810().getSuggestDataParser(new PolicySuggestInputVO(null, getParaNo("9"), null,null), this.getDataAccessManager());
		
		//主推&推薦全部的list
		List<Map<String, Object>> recommandList = new ArrayList<Map<String,Object>>();
		recommandList.addAll(getRecommandList(reimbursementList, 8));
		recommandList.addAll(getRecommandList(protectionList, 9));
		
		//主推還本型&推薦保障型商品的list
		outputMap.put("recommand", recommandList);
		

		List<Map<String, Object>> insuredList = new ArrayList<Map<String,Object>>();
			
		if(custId != null){
			if(ValidUtil.isValidIDorRCNumber(custId)){
				insuredList = getCustDataList(custId);
			}else{
				throw new APException("ehl_01_common_030");
			}
		}
		
		// 身分證字號
		outputMap.put("custId",custId);
		// 客戶姓名
		String custName = (String) insuredList.get(0).get("CUST_NAME");
		
		if(custName != null){
			if(custName.length() > 0){
				if(custId != ""){
					// 客戶姓名
					outputMap.put("custName",custName);		
				}
				
			}else{
				throw new APException("ehl_02_SOT_001");
			}
		}
		
		INS500InputVO inputVO = new INS500InputVO();
		inputVO.setCustId(custId);
		
		//取得既有儲蓄清單		
		List<Map<String, Object>> savingsList = getSavingsList(inputVO);
		//經過處理的既有儲蓄清單
		List<Map<String, Object>> newSavingsList = new ArrayList<Map<String,Object>>();
			
		WSMappingParserUtils.wsGetInsuranceTiedSaleRefDataMapping(savingsList, newSavingsList);
		
		//保單資料
		outputMap.put("policy",newSavingsList);
		//組合商品序號
		outputMap.put("conKeyno","CON" + ((INS810) PlatformContext.getBean("ins810")).doGetSeq("TBINS_SEQ"));
		
		this.sendRtnObject(outputMap);
	}
	
	/**
	 * 提供給 APP 使用的 API <br>
	 * <code> <b> 2.9.2 </b> 計算保額</code>
	 * 
	 * @param inputVO
	 *            :
	 * @return Object :
	 * @throws JBranchException
	 *             : <br>
	 *             <p>
	 *             <b>客製訊息如下 :</b>
	 *             <li>查無資料:</li>
	 *             </p>
	 */
	public void calculateInsuredamt(Object body, IPrimitiveMap<Object> header) throws JBranchException {
		dam = this.getDataAccessManager();
		// 取得傳進來的資料
		GenericMap inputGmap = new GenericMap((Map) body);
		// 取得客戶ID
		String custId = inputGmap.getNotNullStr("custId");
		// 取得險種(商品)代碼
		String prdId = inputGmap.getNotNullStr("prdId");
		// 參數版號
		String paraNo = inputGmap.getNotNullStr("paraNo");
		// 保險商品Key
		String prdKeyNo = inputGmap.getNotNullStr("prdKeyNo");
		// 保費
		float insyearfee = 0;
		if (inputGmap.get("insyearfee") != null) {
			if("0.0".equals(inputGmap.get("insyearfee").toString())) {
				throw new APException("當年度可領回生存金為 0 無法進行推薦商品操作");
			}
			insyearfee = ((Double) inputGmap.get("insyearfee")).floatValue();
		}else{
			throw new APException("請先計算當年度可領回生存金");
		}
		
		// 用來放計算結果的map
		Map outputMap = new HashMap();

		// 取得客戶資料
		List<Map<String, Object>> custDataList = getCustDataList(custId);

		// 計算保險年齡
		int age = getAge(new Date(((Timestamp) custDataList.get(0).get("BIRTH_DATE")).getTime()));

		// 取得計算費率用資料
		List<Map<String, Object>> calRateList = getCalRateList(prdKeyNo);

		// 求取費率、匯率與推薦商品所需的資料
		PolicySuggestInputVO inputVO = new PolicySuggestInputVO();
		gatherNeededInfo(inputVO, custDataList, calRateList, age, paraNo);

		// 求出費率與匯率
		BigDecimal premRate = getIns810().getPremRate(inputVO, dam);
		Map<String, BigDecimal> refExcRate = getIns810().queryRefExcRate();
		String CURR_CD = calRateList.get(0).get("CURR_CD").toString();
		float refExcRateVal;
		if (refExcRate.get(CURR_CD) != null) {
			refExcRateVal = refExcRate.get(CURR_CD).floatValue();
		} else {
			refExcRateVal = 1;
		}
		
		if(premRate == null){
			throw new APException("無此險種保費資訊");
		}

		// 找出推薦的商品list
		List<Map<String, Object>> suggestPrdList = getIns810().getSuggestDataParser(inputVO, dam);
		Map<String, Object> suggestPrdMap = CollectionSearchUtils.findMapInColleciton(suggestPrdList, "PRD_ID", prdId);
		
		// 取得已選取年期的推薦商品資料
		List<Map<String, Object>> insprdAnnualList = (List<Map<String, Object>>) suggestPrdMap.get("insprd_annualList");
		Map<String, Object> selectedMap = CollectionSearchUtils.findMapInColleciton(insprdAnnualList, "KEY_NO", prdKeyNo);
		
		// 計算保費
		String CVRG_RATIO = (String) selectedMap.get("CVRG_RATIO");
		float POLICY_AMT_MIN = Float.parseFloat((String) selectedMap.get("POLICY_AMT_MIN"));  // 保額下限
		float POLICY_AMT_MAX = Float.parseFloat((String) selectedMap.get("POLICY_AMT_MAX"));  // 保額上限
		float POLICY_AMT_DISTANCE = Float.parseFloat((String) selectedMap.get("POLICY_AMT_DISTANCE"));  // 保額累加級距
		
		//費率轉換
		float premRateFloat = premRate.floatValue();
		
		//與保額比較用
		float value = insyearfee/premRateFloat/refExcRateVal;
		
		//保額
		float insuredamt;
		
		if(value >= POLICY_AMT_MAX){
			insuredamt = POLICY_AMT_MAX;
			insyearfee = insuredamt*premRateFloat*refExcRateVal;
		}else if(value <= POLICY_AMT_MIN){
			insuredamt = POLICY_AMT_MIN;
			insyearfee = insuredamt*premRateFloat*refExcRateVal;
		}else{
			insuredamt = (float) Math.floor(((value-POLICY_AMT_MIN)/POLICY_AMT_DISTANCE)*POLICY_AMT_DISTANCE+POLICY_AMT_MIN);
			insyearfee = insuredamt*premRateFloat*refExcRateVal;
		}
		
		//保障年期
		String guaranteeAnnual = (String) selectedMap.get("GUARANTEE_ANNUAL");
		
		// prdKeyNo : 保險商品Key
		outputMap.put("prdKeyNo", prdKeyNo);
		// insuredamt : 保額
		outputMap.put("insuredamt", new BigDecimal(insuredamt));
		// insyearfee : 保費
		outputMap.put("insyearfee", new BigDecimal(insyearfee));
		//guaranteeAnnual : 保障年期
		outputMap.put("guaranteeAnnual", guaranteeAnnual);
		
		this.sendRtnObject(outputMap);		
	}
	
	/**
	 * 提供給 APP 使用的 API <br>
	 * <code> <b> 2.9.3 </b> 產生保險商品組合規劃書</code>
	 * 
	 * @param inputVO
	 *            :
	 * @return Object :
	 * @throws JBranchException
	 *             : <br>
	 *             <p>
	 *             <b>客製訊息如下 :</b>
	 *             <li>查無資料:</li>
	 *             </p>
	 * @throws IOException 
	 * @throws SQLException 
	 * @throws SerialException 
	 */
	public void generateTiedSalePlan(Object body, IPrimitiveMap<Object> header) throws JBranchException, SerialException, SQLException, IOException {
		dam = this.getDataAccessManager();
		// 取得傳進來的資料
		GenericMap inputGmap = new GenericMap((Map) body);
		// 取得客戶ID
		String custId = inputGmap.getNotNullStr("custId");
		// 組合商品序號
		String conKeyno = inputGmap.getNotNullStr("conKeyno");
		// 選取的既有儲蓄型商品險種代碼
		List<String> prdIdList = inputGmap.get("current");
		// 主推還本型&推薦保障型商品資料list
		List<Map<String, Object>> recommandList = inputGmap.get("recommand");
		//是否重新產生規畫書
		boolean isNew = inputGmap.get("isNew");
		//裝blob的list
		List<Map<String, Object>> blobList = new ArrayList<Map<String,Object>>();
		//取得商品組合序號與報表byte[]
		Map<String, Object> reportMap = null;
		//取得報表的byte[]
		byte[] reportData = null;
		// 用來放計算結果的map
		Map outputMap = new HashMap();
		
		INS500InputVO inputVO = new INS500InputVO();
		inputVO.setCustId(custId);
		
		// 取得客戶資料
		List<Map<String, Object>> custDataList = getCustDataList(custId);

		// 計算保險年齡
		int age = getAge(new Date(((Timestamp) custDataList.get(0).get("BIRTH_DATE")).getTime()));
		inputVO.setAge(String.valueOf(age));
		inputVO.setBirth(new Date(((Timestamp) custDataList.get(0).get("BIRTH_DATE")).getTime()));
			
		//主推還本型商品
		List<Map<String,Object>> reimbursementList = getIns810().getSuggestDataParser(new PolicySuggestInputVO(null, getParaNo("8"), null,null), this.getDataAccessManager());
		//推薦保障型商品
		List<Map<String,Object>> protectionList = getIns810().getSuggestDataParser(new PolicySuggestInputVO(null, getParaNo("9"), null,null), this.getDataAccessManager());
		
		//推薦保障型商品資料map
		Map<String, Object> protectionMap = CollectionSearchUtils.findMapInColleciton(recommandList, "type", (double)2);
		//主推還本型商品資料list(移除推薦保障型商品資料map)
		recommandList.remove(protectionMap);
		
		//推薦保障型商品資料list
		List<Map<String,Object>> protectionDataList = new ArrayList<Map<String,Object>>();
		protectionDataList.add(protectionMap);
		
		
		//選取並處理過的主推還本型商品
		List<Map<String,Object>> newReimbursementList = new ArrayList<Map<String,Object>>();
		//選取並處理過的推薦保障型商品
		List<Map<String,Object>> newProtectionList = new ArrayList<Map<String,Object>>();
		
		WSMappingParserUtils.dataProcess(reimbursementList, newReimbursementList, recommandList, 2);
		WSMappingParserUtils.dataProcess(protectionList, newProtectionList, protectionDataList, 3);
		
		//取得險種(商品)代碼的list
		List<Object> firstTable = new ArrayList<Object>();
		//取得選取並處理過的主推還本型商品的list
		List<Object> secondTable = new ArrayList<Object>();
		//取得選取並處理過的推薦保障型商品的list
		List<Object> thirdTable = new ArrayList<Object>();
		
		for(String prdId : prdIdList){
			firstTable.add(prdId);
		}
		
		secondTable.addAll(newReimbursementList);		
		thirdTable.addAll(newProtectionList);
		
		//取得所有選取的保險商品組合資料
		List<List<Object>> allDataList = new ArrayList<List<Object>>();
		allDataList.add(firstTable);
		allDataList.add(secondTable);
		allDataList.add(thirdTable);
		
		inputVO.setSelectPrdDatas(allDataList);
		
		//預防取舊規畫書卻為空的狀況
		String tableName = "TBINS_REPORT";
		String[] columns = {"REPORT_FILE"};
		Map<String,Object> conditions = new HashMap<String, Object>();
		conditions.put("PLAN_ID", conKeyno);
		blobList = getIns810().getDBList(dam, tableName, columns, conditions);
		
		if(false == isNew && CollectionUtils.isEmpty(blobList)){
			isNew = true;
		}
		
		if(isNew){
			//取得檔案與報表路徑
			Map<String, String> paramMap = returnFileNameAndPath(inputVO);
			//刪除保險商品主檔
			deleteConMain(conKeyno);
			//刪除既存報表
			deleteReport(conKeyno);			
			// 列印儲存 + 取檔 + 新增報告書
			reportMap = afterPrintSave(paramMap.get("reportURL"), inputVO.getCustId(), paramMap.get("fileName"),conKeyno);	
			
			reportData = (byte[]) reportMap.get("reportData");
		}else{
			
			reportData = ObjectUtil.blobToByteArr((Blob) blobList.get(0).get("REPORT_FILE"));
		}
		
		//fileCode : 每次產生報告書序號+1(與理規共用)
		outputMap.put("fileCode", conKeyno);
		//file : PDF檔案的byte[]，使用Base64加密
		outputMap.put("file", DatatypeConverter.printBase64Binary(reportData));
		
		this.sendRtnObject(outputMap);
		
	}
	
	//======================== WS 處理 ========================
	/**
	 * 取得paraNo
	 * @param paraType
	 * @return paraNoStr
	 */
	public String getParaNo(String paraType) throws DAOException, JBranchException{
		dam = this.getDataAccessManager();
		// GET PARA_NO
		List<Map<String, Object>>  list = getIns810().getPlanNo(new PolicySuggestInputVO(paraType.toString(), null, null,null), dam);
		BigDecimal paraNo = new BigDecimal(getIns810().getSingleMapValue(list, "PARA_NO"));
		String paraNoStr = ObjectUtils.toString(paraNo);
		return paraNoStr;
	}
	
	/**
	 * 取得處理過格式的推薦推薦商品清單
	 * @param suggestList
	 * @param type
	 * @throws JBranchException
	 * @throws DAOException
	 * @return newSuggestList
	 */
	private List<Map<String, Object>> getRecommandList(List<Map<String, Object>> suggestList, int type) throws DAOException, JBranchException{
		List<Map<String, Object>> newSuggestList = new ArrayList<Map<String, Object>>();
		
		//API轉換參數用
		int convertType = 0;
		
		switch(type){
			case 8:{
				convertType = 1;
				break;
			}
			case 9:{
				convertType = 2;
				break;
			}
		}
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> unitMap = xmlInfo.doGetVariable("INS.UNIT", FormatHelper.FORMAT_3);
		
		for(Map<String,Object> suggestMap : suggestList){
			Map<String, Object> newSuggestMap = new HashMap<String, Object>();
			
			newSuggestMap.put("type", convertType);
			newSuggestMap.put("prdId", suggestMap.get("PRD_ID"));
			newSuggestMap.put("insprdName", suggestMap.get("INSPRD_NAME"));
			newSuggestMap.put("unit", unitMap.get(suggestMap.get("PRD_UNIT")));
			newSuggestMap.put("currCd", suggestMap.get("CURR_CD"));
			newSuggestMap.put("paraNo", getParaNo(Integer.toString(type)));
			
			List<Map<String, Object>> prodInfoList = suggestMap.get("insprd_annualList") != null ? (List<Map<String, Object>>) suggestMap.get("insprd_annualList"): new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> newProdInfoList = new ArrayList<Map<String, Object>>();
			for(Map<String, Object> prodInfoMap : prodInfoList){
				Map<String, Object> newProdInfoMap = new HashMap<String, Object>();
				newProdInfoMap.put("paymentyearSel", prodInfoMap.get("DATA"));
				newProdInfoMap.put("guaranteeAnnual", prodInfoMap.get("GUARANTEE_ANNUAL"));
				newProdInfoMap.put("prdKeyNo", prodInfoMap.get("KEY_NO"));
				newProdInfoList.add(newProdInfoMap);
			}
			newSuggestMap.put("productInfo", newProdInfoList);
			
			newSuggestList.add(newSuggestMap);
		}
			
		return newSuggestList;	
	}
	
	/**
	 * 取得客戶資料
	 * @param custId
	 * @throws JBranchException
	 * @return custDataList
	 */
	public List<Map<String, Object>> getCustDataList(String custId) throws JBranchException {		
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT CUST_NAME, GENDER, BIRTH_DATE FROM TBCRM_CUST_MAST WHERE CUST_ID = :custId");
		qc.setObject("custId", custId);
		qc.setQueryString(sb.toString());
		List<Map<String, Object>> custDataList = dam.exeQuery(qc);
		return custDataList;		
	}
	
	/**
	 * 取得計算費率用資料
	 * @param prdKeyNo
	 * @throws JBranchException
	 * @return calRateList
	 */
	public List<Map<String, Object>> getCalRateList(String prdKeyNo) throws JBranchException {
		dam = this.getDataAccessManager();
		StringBuffer sb_rate = new StringBuffer();
		QueryConditionIF qc_rate = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb_rate.append("SELECT PRD_ID,INSPRD_ANNUAL,CURR_CD,PRD_UNIT ");
		sb_rate.append("FROM TBPRD_INS ");
		sb_rate.append("WHERE KEY_NO = :prdKeyNo ");
		qc_rate.setObject("prdKeyNo", prdKeyNo);
		qc_rate.setQueryString(sb_rate.toString());
		List<Map<String, Object>> calRateList = dam.exeQuery(qc_rate);
		return calRateList;
	}
	
	/**
	 * 求取費率、匯率與推薦商品所需的資料
	 * @param inputVO
	 * @param custDataList
	 * @param calRateList
	 * @param age
	 * @param paraNo
	 * @throws JBranchException
	 */
	public void gatherNeededInfo(PolicySuggestInputVO inputVO,List<Map<String, Object>> custDataList,List<Map<String, Object>> calRateList,int age,String paraNo) throws JBranchException {
		inputVO.setGender(custDataList.get(0).get("GENDER").toString());
		inputVO.setAge(age);
		inputVO.setInsPrdId(calRateList.get(0).get("PRD_ID").toString());
		inputVO.setCurrCD(calRateList.get(0).get("CURR_CD").toString());
		inputVO.setAnnual(calRateList.get(0).get("INSPRD_ANNUAL").toString());
		inputVO.setParaNo(paraNo);
	}
	
	/**
	 * 刪除報表
	 * @param String : conKeyno
	 * @throws JBranchException
	 * @throws SQLException 
	 * @throws SerialException 
	 */
	public void deleteReport(String conKeyno) throws JBranchException, SerialException, SQLException{
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		sb.append(" delete from TBINS_REPORT ");
		sb.append(" where PLAN_ID = :conKeyno ");
		qc.setObject("conKeyno", conKeyno);
		qc.setQueryString(sb.toString());
		dam.exeUpdate(qc);
	}
	
	/**
	 * 刪除保險商品主檔
	 * @param String : conKeyno
	 * @throws JBranchException
	 * @throws SQLException 
	 * @throws SerialException 
	 */
	public void deleteConMain(String conKeyno) throws JBranchException, SerialException, SQLException{
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		sb.append(" delete from TBINS_CON_MAIN ");
		sb.append(" where CON_KEYNO = :conKeyno ");
		qc.setObject("conKeyno", conKeyno);
		qc.setQueryString(sb.toString());
		dam.exeUpdate(qc);
	}
	
	//======================== 共用 調用 & 處理 ========================
	/**
	 * 取得既有儲蓄清單
	 * @param inputVO
	 * @return savingsList
	 */
	public List<Map<String, Object>> getSavingsList(INS500InputVO inputVO) throws DAOException, JBranchException{
			
		// 執行查詢帶出客戶內外保單
		GetOldItemListOutputVO getOldItemListOutputVO = getOldItemListOutputVO(inputVO);
		List<Map<String,Object>> oldItemLList = getOldItemListOutputVO.getOldItemLlist();
		// 錯誤訊息
		List lstLongTable = getOldItemListOutputVO.getLstLogTable();
		
//		if(lstLongTable != null){
//			throw new APException(new Gson().toJson(lstLongTable));
//		}

		// 既有儲蓄清單
		List<Map<String, Object>> savingsList = finalResultDatasFontEnd(oldItemLList);
		
		return savingsList;
	}
	
	/**
	 * 取得檔案與報表路徑
	 * @param inputVO
	 * @throws JBranchException
	 * @return paramMap
	 */
	public Map<String, String> returnFileNameAndPath(INS500InputVO inputVO) throws JBranchException{
		
		String age = String.valueOf(Integer.parseInt(inputVO.getAge()));
		String url = null, txnCode = "INS500", reportID = "R1";
				
		// 產出pdf
		ReportIF report = null;
		ReportFactory factory = new ReportFactory();
		ReportDataIF data = new ReportData();
		ReportGeneratorIF gen = factory.getGenerator(); 
		String now_date = new SimpleDateFormat("YYYYMMdd").format(new Date());
				
		// 調用資訊源後的所有資料
		List<Object>[] allData = prepareCombianData(inputVO);
		System.out.println("調用資訊源後的所有資料 " + new Gson().toJson(allData));
				
		// 還本的
		final String[] replayColumns = new String[]{"PREMIUM", "REPAY"}; // 所有要 重新 Get 運算在 Set的欄位
		List<Map<String, Object>> repayList = dataCombian(age, allData[0], replayColumns);
		System.out.println("列印-還本的 " + new Gson().toJson(repayList));

		// 保障的
		final String[] indemnificationColumn = new String[]{"PREMIUM","LIFE", "PA","THEYEAR"}; // 所有要 重新 Get 運算在 Set的欄位
		List<Map<String, Object>> indemnificationList = dataCombian(age, allData[1], indemnificationColumn);
		System.out.println("列印-保障的 " + new Gson().toJson(indemnificationList));
		
		// 保障的給付項目
		List<Object> lstExpression = allData[2];
		// 組合 還本 + 保障
		List<Map<String, Object>> finalReportList = finalReportList(repayList, indemnificationList, lstExpression, (Map<String, Object>)inputVO.getSelectPrdDatas().get(2).get(0));
		System.out.println("列印-還本 + 保障的給付項目 " + new Gson().toJson(finalReportList));
		
		data.addRecordList("Script Mult Data Set", finalReportList); 
		report = gen.generateReport(txnCode, reportID, data);
		url = report.getLocation();
				
		String reportURL = PdfUtil.mergePDF(Arrays.asList(new String[]{url}),true);
				
		String fileName = now_date + "保險商品組合報告書.pdf";
		
		//存放reportURL與fileName
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("reportURL", reportURL);
		paramMap.put("fileName", fileName);
		
		return paramMap;
	}
	
	//====================== 其他 Method ======================
	/**
	 * 儲存報表功能
	 * @param file 當前產生之後的報表
	 * @param custId 客戶 ID
	 * @throws DAOException
	 * @throws JBranchException
	 * @throws SerialException
	 * @throws SQLException
	 * @throws IOException 
	 */
	private Map<String, Object> afterPrintSave(String reportURL, String custId, String fileName, String conKeyno) throws DAOException, JBranchException, SerialException, SQLException, IOException {
		// 新增保險商品組合檔
		dam = getDataAccessManager();
		TBINS_CON_MAINVO mainVO = new TBINS_CON_MAINVO();
		if(conKeyno != null){
			mainVO.setCON_KEYNO(conKeyno);
		}else{			
			mainVO.setCON_KEYNO("CON" + ((INS810) PlatformContext.getBean("ins810")).doGetSeq("TBINS_SEQ"));
		}
		mainVO.setCUST_ID(custId);
		dam.create(mainVO);
		
		byte[] reportData = null;
		
		if(reportURL != null) {
			String sysPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
			File reportFile =  new File(sysPath , reportURL.replaceFirst("^temp.", ""));
			reportData = Files.readAllBytes(reportFile.toPath());
			// 新增保規報表檔
			TBINS_REPORTVO vo = new TBINS_REPORTVO();
			vo.setKEYNO(new BigDecimal(((INS810) PlatformContext.getBean("ins810")).doGetSeq("TBINS_REPORT_SEQ")));
			vo.setPLAN_ID(mainVO.getCON_KEYNO().toString());
			vo.setREPORT_FILE(ObjectUtil.byteArrToBlob(reportData));
			vo.setFILE_NAME(fileName);
			dam.create(vo);			
		}
		
		Map<String, Object> reportMap = new HashMap<String, Object>();
		reportMap.put("reportData", reportData);
		
		return reportMap;
		
	}

	/**
	 * 產品相關資訊
	 * @param prodKeys 所有的產品 key
	 * @return 哪個產品的 INFO
	 * @throws JBranchException
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Map<String, Object>> findProdInfo(List<String> prodKeys) throws JBranchException {
		Map<String, Map<String, Object>> prodInfoMap = new HashMap<String, Map<String, Object>>();
		if(prodKeys.size() > 0) {
			dam = this.getDataAccessManager();
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuilder sql = new StringBuilder();
			sql.append("select KEY_NO, ITEM_Y, LIST_Y from TBPRD_INSDATA_PROD_MAIN where KEY_NO in (:KEY_NO)");
			queryCondition.setObject("KEY_NO", prodKeys);
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> prodInfoList = dam.exeQuery(queryCondition);
			for(Map<String, Object> prodInfo : prodInfoList) {
				String keyNo = prodInfo.get("KEY_NO").toString();
				prodInfoMap.put(keyNo, prodInfo);
			}
		}
		return prodInfoMap;
	}
	
	/**
	 * 統一透過 INS810 取得年齡
	 * @param birthday 生日
	 * @return
	 * @throws JBranchException
	 */
	private int getAge(Date birthday) throws JBranchException {
		INS810 ins810 = (INS810) PlatformContext.getBean("ins810");
		INS810InputVO ins810inputVO = new INS810InputVO();
		INS810OutputVO ins810outputVO = new INS810OutputVO();
		ins810inputVO.setBirthday(birthday);
		ins810outputVO = ins810.getAge(ins810inputVO);
		return ins810outputVO.getAge();
	}
}
