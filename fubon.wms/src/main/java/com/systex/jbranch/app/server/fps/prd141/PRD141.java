package com.systex.jbranch.app.server.fps.prd141;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ibm.icu.text.SimpleDateFormat;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.commons.cbs.CBSService;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("prd141")
@Scope("request")
public class PRD141 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private CBSService cbsService;
	
	//基本資料
	public void getBondSnInfo (Object body, IPrimitiveMap header) throws JBranchException{
		PRD141InputVO inputVO = (PRD141InputVO) body;
		PRD141OutputVO outputVO = new PRD141OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT A.ISIN_CODE, A.CCY, A.INSTITION_OF_FLOTATION, A.INSTITION_OF_AVOUCH, ");
		sb.append(" A.FLO_CREDIT_RATING, A.AVO_CREDIT_RATING, A.DATE_OF_TXN, A.DATE_OF_FLOTATION,  ");
		sb.append(" A.DATE_OF_MATURITY, A.START_DATE_OF_BUYBACK, A.RISKCATE_ID, A.RATE_GUARANTEEPAY,  ");
		sb.append(" A.BASE_AMT_OF_PURCHASE, A.UNIT_AMT_OF_PURCHASE, A.BASE_AMT_OF_BUYBACK,  ");
		sb.append(" A.UNIT_AMT_OF_BUYBACK, A.FREQUENCY_OF_INTEST_PAY, A.CDSC, A.FIXED_RATE_DURATION,  ");
		sb.append(" A.FIXED_DIVIDEND_RATE, A.FLOATING_DIVIDEND_RATE, A.DURATION  ");
		sb.append(" FROM TBPRD_VPSN A  ");
		sb.append(" WHERE PROD_ID =:prd_id  ");
		
		qc.setObject("prd_id", inputVO.getPrd_id());
		qc.setQueryString(sb.toString());
		List<Map<String, Object>> list = dam.exeQuery(qc);
		outputVO.setResultList(list);
		
		this.sendRtnObject(outputVO);
	}
	
	//參考報價
	public void getBondPrice(Object body, IPrimitiveMap header) throws JBranchException{
		PRD141InputVO inputVO = (PRD141InputVO) body;
		PRD141OutputVO outputVO = new PRD141OutputVO();
		dam = this.getDataAccessManager();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		String sDate = sdf.format(inputVO.getsDate());
		String eDate = sdf.format(inputVO.geteDate());
		
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT A.NAV_DATE, A.NAV  ");
		sb.append(" FROM TBPRD_VPSN_NAV A  ");
		sb.append(" WHERE PROD_ID =:prd_id  ");
		qc.setObject("prd_id", inputVO.getPrd_id());
		
		sb.append(" AND A.NAV_DATE >= :sDate ");
		sb.append(" AND A.NAV_DATE <= :eDate ");
		sb.append(" Order by A.NAV_DATE  ");
		
		qc.setObject("sDate", sDate);
		qc.setObject("eDate", eDate);
		qc.setQueryString(sb.toString());
		List<Map<String, Object>> list = dam.exeQuery(qc);
		outputVO.setResultList(list);
		this.sendRtnObject(outputVO);
	}
	
	//配息情況
	public void getSnDividend (Object body, IPrimitiveMap header) throws JBranchException{
		PRD141InputVO inputVO = (PRD141InputVO) body;
		PRD141OutputVO outputVO = new PRD141OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT A.DIV_DATE, A.DIV_RATE,A.DIV_UNIT_AMT,A.TOT_DIV_RATE  ");
		sb.append(" FROM TBPRD_VPSN_DIV A  ");
		sb.append(" WHERE PROD_ID =:prd_id  ");
		qc.setObject("prd_id", inputVO.getPrd_id());
		qc.setQueryString(sb.toString());
		List<Map<String, Object>> list = dam.exeQuery(qc);
		outputVO.setResultList(list);
		this.sendRtnObject(outputVO);
		
	}
	
	//庫存明細
	public void getSnStocks(Object body, IPrimitiveMap header) throws JBranchException{
		PRD141InputVO inputVO = (PRD141InputVO) body;
		PRD141OutputVO outputVO = new PRD141OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT A.PROD_ID, A.AO_REGION, A.AO_AREA, A.AO_BRANCH, A.AO_CODE,  ");
		sb.append(" A.BAL_AMT_ORG, A.BAL_AMT_TWD, A.CUST_CNT  ");
		sb.append(" FROM TBPRD_VPSN_AO A  ");
		sb.append(" WHERE PROD_ID =:prd_id  ");
		qc.setObject("prd_id", inputVO.getPrd_id());
		qc.setQueryString(sb.toString());
		List<Map<String, Object>> list = dam.exeQuery(qc);
		outputVO.setResultList(list);
		this.sendRtnObject(outputVO);
	}
	
	
	//下載庫存明細.csv
		public void download (Object body, IPrimitiveMap header) throws JBranchException {
			PRD141InputVO inputVO = (PRD141InputVO) body;
			
			List<Map<String,String>> downloadList = inputVO.getDownloadList();
			List<Object[]> csvData = new ArrayList<Object[]>();

			String[] csvHeader = new String[] { "產品代號", "處別", "區別", "分行", "AO Code", "原幣庫存", "台幣庫存", "客戶數"};
			String[] csvMain = new String[] { "PROD_ID", "AO_REGION", "AO_AREA", "AO_BRANCH", "AO_CODE", "BAL_AMT_ORG", "BAL_AMT_TWD", "CUST_CNT"};

			if (downloadList.size() > 0) {
				for (Map<String, String> map : downloadList) {
					String[] records = new String[csvHeader.length];
					for (int i = 0; i < csvHeader.length; i++) {
						records[i] = checkIsNull(map, csvMain[i]);
					}

					csvData.add(records);
				}

				CSVUtil csv = new CSVUtil();

				// 設定表頭
				csv.setHeader(csvHeader);
				// 添加明細的List
				csv.addRecordList(csvData);

				// 執行產生csv并收到該檔的url
				String url = csv.generateCSV();

				// 將url送回FlexClient存檔
				notifyClientToDownloadFile(url, "商品庫存明細.csv");
			}

			sendRtnObject(null);
		}
		
		/**
		* 檢查Map取出欄位是否為Null
		*/
		private String checkIsNull(Map<String, String> map, String key) {

			if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && null != map.get(key)) {
				if("BRANCH_NAME".equals(key)){
					String bra_nbr = "";
					String branch_name = "";

					if(map.get("BRA_NBR") != null){
						bra_nbr = map.get("BRA_NBR").toString();
					}

					if(map.get("BRANCH_NAME") != null){
						branch_name = map.get("BRANCH_NAME").toString();
					}
					return bra_nbr + "-" + branch_name;

				} else {
					return String.valueOf(map.get(key));
				}
			} else {
				return "";
			}
		}
}
