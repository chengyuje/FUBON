package com.systex.jbranch.app.server.fps.sot620;

import com.systex.jbranch.app.server.fps.sot701.CustHighNetWorthDataVO;
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
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.commons.PdfUtil;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 客戶高風險投資集中度查詢及試算
 * @date 2023/05/03
 * 
 */
@Component("sot620")
@Scope("request")
public class SOT620 extends FubonWmsBizLogic {

	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(SOT620.class);
	private SOT620InputVO sot620InputVO;
	private SOT620OutputVO sot620OutputVO;
	
	/***
	 * 取得客戶資料
	 * 1. 檢核是否為高資產客戶
	 * 2. 取得目前集中度結果
	 * 3. 取得客戶已委託高風險投資明細資訊
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void getCustData(Object body, IPrimitiveMap header) throws Exception {
		sot620InputVO = (SOT620InputVO) body;
		sot620OutputVO = new SOT620OutputVO();
		
		//檢核是否為高資產客戶
		SOT714 sot714 = (SOT714) PlatformContext.getBean("sot714");
		CustHighNetWorthDataVO hnwcData = sot714.getHNWCData(sot620InputVO.getCustID());
		if(hnwcData == null || !StringUtils.equals("Y", hnwcData.getValidHnwcYN())) {
			throw new APException("非高資產客戶無法執行試算");
		}
		
		//取得目前集中度結果
		sot620OutputVO.setCurrRateData(this.getCurrentCRate(sot620InputVO));
		//取得客戶已委託高風險投資明細資訊
		sot620OutputVO.setCentInvList(this.getCentInvData(sot620InputVO));
		
		sendRtnObject(sot620OutputVO);
	}
	
	/***
	 * 取得目前集中度結果
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void getCurrentCRate(Object body, IPrimitiveMap header) throws Exception {
		sot620InputVO = (SOT620InputVO) body;
		sot620OutputVO = new SOT620OutputVO();
		sot620OutputVO.setCurrRateData(this.getCurrentCRate(sot620InputVO));
		sendRtnObject(sot620OutputVO);
	}

	/***
	 * 取得目前集中度結果
	 * @param inputVO
	 * @return
	 * @throws Exception
	 */
	public WMSHACRDataVO getCurrentCRate(SOT620InputVO inputVO) throws Exception {
		SOT714 sot714 = (SOT714) PlatformContext.getBean("sot714");
		SOT714InputVO inputVO714 = new SOT714InputVO();
		inputVO714.setCustID(inputVO.getCustID());
		if(inputVO.getAddProdData() == null) {
			//沒有新增商品，取得目前集中度資訊
			inputVO714.setProdType("1");
			inputVO714.setBuyAmt(BigDecimal.ZERO);
		} else {
			//有新增商品，則集中度加上新增商品金額
			inputVO714.setProdType(ObjectUtils.toString(inputVO.getAddProdData().get("PROD_CAT")));
			inputVO714.setBuyAmt(new BigDecimal(ObjectUtils.toString(inputVO.getAddProdData().get("AMT_TWD"))));
		}
				
		//查詢客戶高資產集中度資料
		WMSHACRDataVO cRateData = sot714.getCentRateData(inputVO714);
		return cRateData;
	}
	
	/***
	 * 取得客戶已委託高風險投資明細資訊
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void getCentInvData(Object body, IPrimitiveMap header) throws Exception {
		sot620InputVO = (SOT620InputVO) body;
		sot620OutputVO = new SOT620OutputVO();
		sot620OutputVO.setCentInvList(this.getCentInvData(sot620InputVO));
		sendRtnObject(sot620OutputVO);
	}
	
	public List<CentInvDataVO> getCentInvData(SOT620InputVO inputVO) throws Exception {
		List<CentInvDataVO> dataList = new ArrayList<CentInvDataVO>();
		
		SOT714 sot714 = (SOT714) PlatformContext.getBean("sot714");
		for(int i=1; i<8; i++) {
			SOT714InputVO inputVO714 = new SOT714InputVO();
			inputVO714.setCustID(inputVO.getCustID());
			inputVO714.setProdType(Integer.toString(i));
			//查詢客戶已委託高風險投資明細資訊
			List<CentInvDataVO> list = sot714.getCentInvData(inputVO714);
			dataList.addAll(list);
		}
		
		return dataList;
	}
	
	/***
	 * 取得匯率資訊
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void getCurrencyRate(Object body, IPrimitiveMap header) throws Exception {
		sot620InputVO = (SOT620InputVO) body;
		sot620OutputVO = new SOT620OutputVO();
		sot620OutputVO.setCurrencyRate(this.getCurrencyRate(sot620InputVO));
		sendRtnObject(sot620OutputVO);
	}
	
	/***
	 * 取得匯率資訊
	 * @param inputVO
	 * @return
	 * @throws Exception
	 */
	public BigDecimal getCurrencyRate(SOT620InputVO inputVO) throws Exception {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append("select NVL(BUY_RATE, 1) AS BUY_RATE ");
		sb.append("  from TBPMS_IQ053 ");
		sb.append(" where CUR_COD = :currCode AND MTN_DATE = (SELECT MAX(MTN_DATE) FROM TBPMS_IQ053) ");
		queryCondition.setObject("currCode", inputVO.getCurrCode());
		
		queryCondition.setQueryString(sb.toString());
		List<Map<String, Object>> cList = dam.exeQuery(queryCondition);
		BigDecimal rate = new BigDecimal(1);
		if (CollectionUtils.isNotEmpty(cList)) {
			rate = (BigDecimal) cList.get(0).get("BUY_RATE");
		}

		return rate;
	}
	
	/***
	 * 列印高資產客戶投資產品集中度聲明書
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void printReport(Object body, IPrimitiveMap header) throws Exception {
		sot620InputVO = (SOT620InputVO) body;
		PRDFitInputVO prdInputVO = new PRDFitInputVO();
		
		prdInputVO.setHmshacrDataVO(sot620InputVO.getTrialRateData());
		prdInputVO.setCustId(sot620InputVO.getCustID());
		prdInputVO.setPrdId(ObjectUtils.toString(sot620InputVO.getAddProdData().get("PROD_ID")));
		prdInputVO.setPrdName(ObjectUtils.toString(sot620InputVO.getAddProdData().get("PROD_NAME")));
		//電文與下單/適配的商品種類需對應
		String pType = ObjectUtils.toString(sot620InputVO.getAddProdData().get("PROD_CAT"));
		prdInputVO.setPrdType(pType.matches("4|5|7") ? "3" : (pType.matches("1|2|3") ? "4" : "1"));
		
		List<String> urlList = new SotPdfContext(prdInputVO, "sot824").getSotPdfULst();
		String reportURL = PdfUtil.mergePDF(urlList, false);
		notifyClientViewDoc(reportURL, "pdf");
	}
	
	/***
	 * 新增商品須為高風險商品
	 * SI/SN不保本商品也可作集中度試算
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void checkHNWCBuy(Object body, IPrimitiveMap header) throws Exception {
		sot620InputVO = (SOT620InputVO) body;
		sot620OutputVO = new SOT620OutputVO();
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		if(sot620InputVO.getProdType().matches("1|2|4")) {
			//檢核SI/SN是否為不保本商品
			PRDFitInputVO pfitInputVO = new PRDFitInputVO();
			pfitInputVO.setPrdType(sot620InputVO.getProdType().matches("1") ? "4" : (sot620InputVO.getProdType().matches("2") ? "5" : "3"));
			pfitInputVO.setPrdId(sot620InputVO.getProdID());
			SOT712 sot712 = (SOT712) PlatformContext.getBean("sot712");
			boolean isRateGuaranteed = sot712.isRateGuaranteed(pfitInputVO); //true:保本  false:不保本
			
			//檢核是否為高風險商品
			sb.append("SELECT NVL(HNWC_BUY, 'N') AS HNWC_BUY FROM ");
			sb.append(sot620InputVO.getProdType().matches("1") ? "TBPRD_SI" : (sot620InputVO.getProdType().matches("2") ? "TBPRD_SN" : "TBPRD_BOND"));
			sb.append(" WHERE PRD_ID = :prodID ");
			queryCondition.setObject("prodID", sot620InputVO.getProdID());
			
			queryCondition.setQueryString(sb.toString());
			List<Map<String, Object>> pList = dam.exeQuery(queryCondition);
			//高風險商品或不保本商品，才可以做試算
			if(!(CollectionUtils.isNotEmpty(pList) && StringUtils.equals("Y", ObjectUtils.toString(pList.get(0).get("HNWC_BUY"))) || !isRateGuaranteed)) {
				throw new APException("此商品不為高風險商品");
			}
		} else if(sot620InputVO.getProdType().matches("6")) { //境外私募基金
			//檢核是否為高風險商品
			queryCondition.setObject("prodID", sot620InputVO.getProdID());
			queryCondition.setQueryString("SELECT NVL(OVS_PRIVATE_YN, 'N') AS HNWC_BUY FROM TBPRD_FUND WHERE PRD_ID = :prodID ");
			List<Map<String, Object>> pList = dam.exeQuery(queryCondition);
			//高風險商品，才可以做試算
			if(CollectionUtils.isEmpty(pList)) {
				throw new APException("查無此商品資料");
			} else if(!StringUtils.equals("Y", ObjectUtils.toString(pList.get(0).get("HNWC_BUY")))) {
				throw new APException("此商品不為高風險商品");
			}
		}
		
		sendRtnObject(sot620OutputVO);
	}
}
