package com.systex.jbranch.app.server.fps.eln100;

import java.text.SimpleDateFormat;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("eln100")
@Scope("request")
public class ELN100 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;

	public void inquire (Object body, IPrimitiveMap header) throws JBranchException {
		ELN100InputVO inputVO = (ELN100InputVO) body;
		ELN100OutputVO outputVO = new ELN100OutputVO();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT * FROM TBPRD_ELN_PRICE WHERE 1 = 1  ");
		
		// UF上限，產品天期1~6月超過2.5%、7~12月超過3%，不顯示。
		sb.append(" AND ((TENOR BETWEEN 1 AND 6 AND UP_FRONT <= 2.5) ");
		sb.append(" OR (TENOR BETWEEN 7 AND 12 AND UP_FRONT <= 3)) ");
		
		// 詢價起日 
		if (inputVO.getQuery_date_s() != null) {
			String sDate = sdf.format(inputVO.getQuery_date_s());
			sb.append(" AND TO_CHAR(QUERY_DATE, 'YYYY/MM/DD') >= :sDate ");
			qc.setObject("sDate", sDate);
		}
		// 詢價迄日
		if (inputVO.getQuery_date_e() != null) {
			String eDate = sdf.format(inputVO.getQuery_date_e());
			sb.append(" AND TO_CHAR(QUERY_DATE, 'YYYY/MM/DD') <= :eDate ");
			qc.setObject("eDate", eDate);
		}
		// 標的1
		if (StringUtils.isNotBlank(inputVO.getBbg_code1())) {
			sb.append(" AND (BBG_CODE1 = :bbg_code1 ");
			sb.append(" OR BBG_CODE2 = :bbg_code1 ");
			sb.append(" OR BBG_CODE3 = :bbg_code1 ");
			sb.append(" OR BBG_CODE4 = :bbg_code1 ");
			sb.append(" OR BBG_CODE5 = :bbg_code1) ");
			qc.setObject("bbg_code1", inputVO.getBbg_code1());
		}
		// 標的2
		if (StringUtils.isNotBlank(inputVO.getBbg_code2())) {
			sb.append(" AND (BBG_CODE1 = :bbg_code2 ");
			sb.append(" OR BBG_CODE2 = :bbg_code2 ");
			sb.append(" OR BBG_CODE3 = :bbg_code2 ");
			sb.append(" OR BBG_CODE4 = :bbg_code2 ");
			sb.append(" OR BBG_CODE5 = :bbg_code2) ");
			qc.setObject("bbg_code2", inputVO.getBbg_code2());
		}
		// 標的3
		if (StringUtils.isNotBlank(inputVO.getBbg_code3())) {
			sb.append(" AND (BBG_CODE1 = :bbg_code3 ");
			sb.append(" OR BBG_CODE2 = :bbg_code3 ");
			sb.append(" OR BBG_CODE3 = :bbg_code3 ");
			sb.append(" OR BBG_CODE4 = :bbg_code3 ");
			sb.append(" OR BBG_CODE5 = :bbg_code3) ");
			qc.setObject("bbg_code3", inputVO.getBbg_code3());
		}
		// 計價幣別
		if (StringUtils.isNotBlank(inputVO.getCurrency())) {
			sb.append(" AND CURRENCY = :currency ");
			qc.setObject("currency", inputVO.getCurrency());
		}
		// 承作天期
		if (inputVO.getTenor() != null) {
			sb.append(" AND TENOR = :tenor ");
			qc.setObject("tenor", inputVO.getTenor());
		}
		// KO 類型
		if (StringUtils.isNotBlank(inputVO.getKo_type())) {
			sb.append(" AND KO_TYPE = :ko_type ");
			qc.setObject("ko_type", inputVO.getKo_type());
		}
		// KI 類型
		if (StringUtils.isNotBlank(inputVO.getKi_type())) {
			sb.append(" AND KI_TYPE = :ki_type ");
			qc.setObject("ki_type", inputVO.getKi_type());
		}
		
		/**
		 *  點選單一條件，輸入數值區間(非必要翰入欄位):
		 *  1. 年化收益率	 ：查詢每日詢價明細資料庫中，收益率(年化%)	，限數值5以上。 
		 *  2. 執行價格	 ：查詢每日詢價明細資料庫中，執行價格(%)  	，限數值50以上。
		 *  3. KO 價格	 ：查詢每日詢價明細資料庫中，KO 價格(%)	，限數值80以上。
		 *  4. KI 價格	 ：查詢每日詢價明細資料庫中，KI 價格(%)	，若『KI類型』有選EKI、AKI，開放輸入，限數值50以上; 選None，則不可輸入數值。
		 *  5. 通路服務費率：查詢每日詢價明細資料庫中，UF(%)		，限數值0.5以上。
		 * **/
		if (inputVO.getType() != null && (StringUtils.isNotBlank(inputVO.getRate1()) || StringUtils.isNotBlank(inputVO.getRate2()))) {
			String colName = "";
			switch(inputVO.getType()){
			    case 1 :
			    	colName = "COUPON";
			    	break;
			    case 2 :
			    	colName = "STRIKE";
			    	break;
			    case 3 :
			    	colName = "KO_BARRIER";
			    	break;
			    case 4 :
			    	colName = "KI_BARRIER";
			    	break;
			    case 5 :
			    	colName = "UP_FRONT";
			    	break;
			}
			if (StringUtils.isNotBlank(inputVO.getRate1())) {
				sb.append(" AND " + colName + " >= :rate1 ");
				qc.setObject("rate1", inputVO.getRate1());
			}
			if (StringUtils.isNotBlank(inputVO.getRate2())) {
				sb.append(" AND " + colName + " <= :rate2 ");
				qc.setObject("rate2", inputVO.getRate2());
			}
		}
		
		// 排序方式，按詢價日新到舊排序
		sb.append(" ORDER BY QUERY_DATE DESC, QUERY_ID, QUERY_SEQ ");
		
		qc.setQueryString(sb.toString());
		outputVO.setResultList(dam.exeQuery(qc));
		this.sendRtnObject(outputVO);
	}
	
	public void getBBG (Object body, IPrimitiveMap header) throws JBranchException {
		ELN100InputVO inputVO = (ELN100InputVO) body;
		ELN100OutputVO outputVO = new ELN100OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT * FROM TBPRD_ELN_LINK_PROD WHERE 1 = 1");
		
		if (StringUtils.isNotBlank(inputVO.getBbg_code())) {
			sb.append(" AND BBG_CODE LIKE :bbg_code ");
			qc.setObject("bbg_code",  "%" + inputVO.getBbg_code() + "%");
		}
		
		if (StringUtils.isNotBlank(inputVO.getBbg_code1())) {
			sb.append(" AND BBG_CODE = :bbg_code ");
			qc.setObject("bbg_code",  inputVO.getBbg_code1());
		}
		
		qc.setQueryString(sb.toString());
		outputVO.setResultList(dam.exeQuery(qc));
		this.sendRtnObject(outputVO);
	}
	
}