package com.systex.jbranch.app.server.fps.sot802;

import com.systex.jbranch.app.server.fps.sot712.PRDFitInputVO;
import com.systex.jbranch.app.server.fps.sot712.SOT712;
import com.systex.jbranch.app.server.fps.sot712.SOT712InputVO;
import com.systex.jbranch.app.server.fps.sot712.SotPdf;
import com.systex.jbranch.common.xmlInfo.XMLInfo;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.report.ReportIF;
import com.systex.jbranch.platform.common.report.factory.ReportFactory;
import com.systex.jbranch.platform.common.report.generator.ReportGeneratorIF;
import com.systex.jbranch.platform.common.report.reportdata.ReportData;
import com.systex.jbranch.platform.common.report.reportdata.ReportDataIF;
import com.systex.jbranch.platform.common.util.PlatformContext;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***
 * 動態鎖利母基金加碼表單列印
 * @author 1800036
 *
 */
@Component("sot802dyna")
@Scope("request")
public class SOT802DYNA extends SotPdf{
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(SOT802DYNA.class);

	@Override
	public List<String> printReport() throws Exception {
		String txnCode = "SOT801";
		String reportID = "R4"; //母基金加碼與申購同表單
		
		PRDFitInputVO inputVO = getInputVO();
		List<String> url_list = new ArrayList<String>();
		XMLInfo XMLInfo = new XMLInfo();
        HashMap<String,BigDecimal> ex_map = XMLInfo.getExchangeRate();
        ReportGeneratorIF gen = new ReportFactory().getGenerator();
		ReportDataIF data = new ReportData();

		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		sql = new StringBuffer();
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql.append("SELECT M.CUST_ID, M.CUST_NAME, D.PROSPECTUS_TYPE as DPROSPECTUSTYPE, M.BRANCH_NBR AS BRA, M.LASTUPDATE AS MLASTUPDATE, M.REC_SEQ, D.*, ");
		sql.append(" CASE WHEN TO_CHAR(D.TRADE_DATE,'YYYYMMDD')=TO_CHAR(D.CREATETIME,'YYYYMMDD') THEN 'Y' ELSE 'N' END AS MEMO1, TO_CHAR(D.TRADE_DATE, 'YYYY/MM/DD') as TRA_DATE ");
		sql.append(" FROM TBSOT_NF_RAISE_AMT_DYNA D");
		sql.append(" INNER JOIN TBSOT_TRADE_MAIN M on M.TRADE_SEQ = D.TRADE_SEQ ");
		sql.append(" WHERE D.TRADE_SEQ = :trade_seq ");
		queryCondition.setObject("trade_seq", inputVO.getTradeSeq());
		queryCondition.setQueryString(sql.toString());

		List<Map<String, Object>> data_list = checkList(dam.exeQuery(queryCondition));
		Map<String, Object> data_map = data_list.get(0);
		
		data.addParameter("BATCH_SEQ", data_map.get("BATCH_SEQ").toString());
		data.addParameter("CUST", data_map.get("CUST_ID").toString() + "    " + data_map.get("CUST_NAME").toString());
		data.addParameter("CUST_ID", data_map.get("CUST_ID").toString());
		data.addParameter("CUST_NAME", data_map.get("CUST_NAME").toString());
		data.addParameter("TRUST_CURR_TYPE", StringUtils.equals(data_map.get("TRUST_CURR_TYPE").toString(),"Y") ? "外幣信託" : "臺幣信託");
		data.addParameter("TRUST_ACCT", data_map.get("TRUST_ACCT").toString());
		data.addParameter("DEBIT_ACCT", data_map.get("DEBIT_ACCT").toString());
		data.addParameter("CREDIT_ACCT", data_map.get("CREDIT_ACCT").toString());
		data.addParameter("CERTIFICATE_ID", data_map.get("CERTIFICATE_ID").toString());
		data.addParameter("R_PROD_ID", (String)data_map.get("PROD_ID"));
		data.addParameter("R_PROD_NAME", (String)data_map.get("PROD_NAME"));
		data.addParameter("R_PROD_RISK_LV", data_map.get("PROD_RISK_LV").toString());
		data.addParameter("R_TRUST_CURR", data_map.get("TRUST_CURR").toString());
		data.addParameter("R_PURCHASE_AMT", getBigDecimal(data_map.get("RAISE_AMT")));
		data.addParameter("R_FEE", getBigDecimal(data_map.get("FEE")));
		data.addParameter("R_P1_TOTAL1", new BigDecimal(data_map.get("RAISE_AMT").toString()).add(new BigDecimal(data_map.get("FEE").toString())));
		if(StringUtils.isNotBlank(ObjectUtils.toString(data_list.get(0).get("REC_SEQ")))) {
			data.addParameter("REC_SEQ","  錄音序號："+data_list.get(0).get("REC_SEQ").toString());
		}

		if(StringUtils.equals(data_map.get("MEMO1").toString(), "N")){
			data.addParameter("P1_MEMO1", "＊＊如生效日期遇颱風、地震等不可抗力因素致暫停營業，委託人同意順延至次一營業日辦理。");
		}

		String custID = data_map.get("CUST_ID").toString();

    	SOT712 sot712 = (SOT712) PlatformContext.getBean("sot712");
        SOT712InputVO  sot712InputVO = new SOT712InputVO();
        sot712InputVO.setCustID(custID);
		if(StringUtils.length(custID) > 8 && sot712.getCUST_AGE(sot712InputVO) < 18){
			data.addParameter("P1_MEMO2", "＊委託人未成年＊");
		}
		data.addParameter("P1_OPENBOOK1", data_map.get("DPROSPECTUSTYPE").toString().equals("2")? "■" :"□");
		data.addParameter("P1_OPENBOOK2", data_map.get("DPROSPECTUSTYPE").toString().equals("1")? "■" :"□");
		data.addParameter("AUTH_FLAG", "主管:_____________ 經辦:_____________ 驗印:_____________ 親見本人親簽無誤:_____________");
		//生效日期
		data.addParameter("P1_EFFECT_DATE", data_map.get("TRADE_DATE").toString()); 

		String getMonth = "";
		String getDate = "";
		java.sql.Timestamp ts = (java.sql.Timestamp) data_map.get("MLASTUPDATE");
		getMonth = (ts.getMonth()+1)+"";
		getDate = ts.getDate()+"";
		if (getMonth.length() < 2){
			getMonth = "0"+getMonth;
		}
		if (getDate.length() < 2){
			getDate = "0"+getDate;
		}

		if ("1".equals(data_map.get("TRADE_DATE_TYPE").toString())){
			data.addParameter("REPORT_DATE", "中華民國  "+ (ts.getYear()-11) + "  年  " + getMonth + "  月  " + getDate+"  日");
			data.addParameter("REPORT_TIME", "時間： "+ts.getHours()+"  時  "+ts.getMinutes()+"  分  ");
		}else{
			data.addParameter("REPORT_DATE", "中華民國        年     月     日");
			data.addParameter("REPORT_TIME", "時間：      時      分");
			if (StringUtils.isNotBlank(data_map.get("TRA_DATE").toString())) {
				data.addParameter("TRADE_DATE", "表單導入有效期限至　" + data_map.get("TRA_DATE").toString());
			}
		}

		data.addParameter("P1_RECOMMENDER", data_map.get("NARRATOR_NAME")); //解說理專姓名
		data.addParameter("P1_RECOMMENDER_ID", data_map.get("NARRATOR_ID")); //解說理專員編

		data.addParameter("PageFoot","第一聯:受理單位留存聯");
		String url = gen.generateReport(txnCode, reportID, data).getLocation();
		url_list.add(url);

		data.addParameter("PageFoot","第二聯:客戶收執聯");
		//經信託部提醒，客戶留存聯不用顯示表單有效期限 #0003117
		//邏輯 : 不管交易日期類型是否為預約或是即時，在客戶收執聯TRADE_DATE欄位加入空值
		data.addParameter("TRADE_DATE", "");
		url = gen.generateReport(txnCode, reportID, data).getLocation();
		url_list.add(url);
		
		return url_list;
	}

	private BigDecimal getBigDecimal(Object val){
		if(val == null){
			return new BigDecimal(0);
		}else{
			return new BigDecimal(val.toString());
		}
	}
}
