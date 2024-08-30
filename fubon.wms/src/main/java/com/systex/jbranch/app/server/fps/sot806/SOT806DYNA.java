package com.systex.jbranch.app.server.fps.sot806;

import com.systex.jbranch.app.server.fps.sot712.PRDFitInputVO;
import com.systex.jbranch.app.server.fps.sot712.SOT712;
import com.systex.jbranch.app.server.fps.sot712.SOT712InputVO;
import com.systex.jbranch.app.server.fps.sot712.SotPdf;
import com.systex.jbranch.common.xmlInfo.XMLInfo;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
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
import java.util.*;



/**
 * 下單專區功能
 * @spec 動態鎖利事件變更表單列印
 */

@Component("sot806dyna")
@Scope("request")
public class SOT806DYNA extends SotPdf{
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(SOT806DYNA.class);
	
	List<String> url_list = new ArrayList<String>();

	@Override
	public List<String> printReport() throws Exception {
		PRDFitInputVO inputVO = getInputVO();

		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		sql = new StringBuffer();
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql.append("SELECT M.CUST_ID, M.CUST_NAME, M.BRANCH_NBR AS BRA, M.LASTUPDATE AS MLASTUPDATE, D.*, ");
		sql.append(" CASE WHEN TO_CHAR(D.TRADE_DATE,'YYYYMMDD')=TO_CHAR(D.CREATETIME,'YYYYMMDD') THEN 'Y' ELSE 'N' END AS MEMO1, TO_CHAR(D.TRADE_DATE, 'YYYY/MM/DD') as TRA_DATE ");
		sql.append(" FROM TBSOT_NF_CHANGE_DYNA D");
		sql.append(" INNER JOIN TBSOT_TRADE_MAIN M on M.TRADE_SEQ = D.TRADE_SEQ ");
		sql.append(" WHERE D.TRADE_SEQ = :trade_seq ");
		queryCondition.setObject("trade_seq", inputVO.getTradeSeq());
		queryCondition.setQueryString(sql.toString());

		List<Map<String, Object>> data_list = checkList(dam.exeQuery(queryCondition));
		Map<String, Object> data_map = data_list.get(0);
		for(int i=1; i<=4; i++) {
			//各變更事項有自己的批號，單獨印表
			if(i == 1 && StringUtils.isNotBlank(ObjectUtils.toString(data_map.get("BATCH_SEQ_STATUS")))) printUrl(data_map, i, ObjectUtils.toString(data_map.get("BATCH_SEQ_STATUS"))); //變更子基金扣款狀態
			if(i == 2 && StringUtils.isNotBlank(ObjectUtils.toString(data_map.get("BATCH_SEQ_AMOUNT")))) printUrl(data_map, i, ObjectUtils.toString(data_map.get("BATCH_SEQ_AMOUNT"))); //變更子基金轉換金額
			if(i == 3 && StringUtils.isNotBlank(ObjectUtils.toString(data_map.get("BATCH_SEQ_TRANSDATE")))) printUrl(data_map, i, ObjectUtils.toString(data_map.get("BATCH_SEQ_TRANSDATE"))); //變更每月扣款日期
			if(i == 4 && StringUtils.isNotBlank(ObjectUtils.toString(data_map.get("BATCH_SEQ_ADDPROD")))) printUrl(data_map, i, ObjectUtils.toString(data_map.get("BATCH_SEQ_ADDPROD"))); //新增子基金
		}
		
		return url_list;
	}
	
	private void printUrl(Map<String, Object> data_map, int transType, String batchSeq) throws JBranchException {
		String txnCode = "SOT804";
		String reportID = "R4"; //動態鎖利事件變更表單
		 ReportGeneratorIF gen = new ReportFactory().getGenerator();
		ReportDataIF data = new ReportData();
		
		data.addParameter("BATCH_SEQ", batchSeq);
		data.addParameter("CUST", data_map.get("CUST_ID").toString() + "    " + data_map.get("CUST_NAME").toString());
		data.addParameter("CUST_ID", data_map.get("CUST_ID").toString());
		data.addParameter("CUST_NAME", data_map.get("CUST_NAME").toString());
		data.addParameter("TRUST_ACCT", data_map.get("TRUST_ACCT").toString());
		data.addParameter("CREDIT_ACCT", data_map.get("CREDIT_ACCT").toString());
		
		if(transType == 1) {
			//變更事項_子基金扣款狀態
			int rIdx = 0;
			for(int ci=1; ci<=5; ci++) {
				String fStatus = ObjectUtils.toString(data_map.get("F_PROD_STATUS_C"+ Integer.toString(ci)));
				if(StringUtils.isNotBlank(fStatus)) {
					String rType = (rIdx == 0 ? "" : "_C" + Integer.toString(rIdx));
					data.addParameter("FUNC_CODE" + rType, fStatus.matches("Y") ? "E" : "F"); //E:暫停扣款 F:正常扣款
					data.addParameter("CERTIFICATE_ID" + rType, data_map.get("CERTIFICATE_ID").toString());
					data.addParameter("PROD_ID" + rType, (String)data_map.get("PROD_ID_C"+ Integer.toString(ci)));
					data.addParameter("PROD_NAME" + rType, (String)data_map.get("PROD_NAME_C"+ Integer.toString(ci)));
					rIdx++;
				}
			}
		} else if(transType == 2) {
			//變更事項_子基金轉換金額
			int rIdx = 0;
			for(int ci=1; ci<=5; ci++) {
				String fAmt = ObjectUtils.toString(data_map.get("F_PURCHASE_AMT_C"+ Integer.toString(ci)));
				if(StringUtils.isNotBlank(fAmt)) {
					String rType = (rIdx == 0 ? "" : "_C" + Integer.toString(rIdx));
					data.addParameter("FUNC_CODE" + rType, "C");
					data.addParameter("CERTIFICATE_ID" + rType, data_map.get("CERTIFICATE_ID").toString());
					data.addParameter("PROD_ID" + rType, (String)data_map.get("PROD_ID_C"+ Integer.toString(ci)));
					data.addParameter("PROD_NAME" + rType, (String)data_map.get("PROD_NAME_C"+ Integer.toString(ci)));
					data.addParameter("TRUST_CURR" + rType, (String)data_map.get("TRUST_CURR"));
					data.addParameter("AMT_TYPE" + rType, "$");
					data.addParameter("PURCHASE_AMT" + rType, getBigDecimal(data_map.get("F_PURCHASE_AMT_C"+ Integer.toString(ci))));
					rIdx++;
				}
			}
		} else if(transType == 3) {
			//變更事項_每月扣款日期
			data.addParameter("FUNC_CODE", "D");
			data.addParameter("CERTIFICATE_ID", data_map.get("CERTIFICATE_ID").toString());
			data.addParameter("PROD_ID", (String)data_map.get("PROD_ID"));
			data.addParameter("PROD_NAME", (String)data_map.get("PROD_NAME"));
			String transDate = "";
			if(StringUtils.equals(ObjectUtils.toString(data_map.get("F_TRANSFER_DATE_1")),"Y")) transDate = transDate + "1,";
			if(StringUtils.equals(ObjectUtils.toString(data_map.get("F_TRANSFER_DATE_4")),"Y")) transDate = transDate + "6,";
			if(StringUtils.equals(ObjectUtils.toString(data_map.get("F_TRANSFER_DATE_2")),"Y")) transDate = transDate + "11,";
			if(StringUtils.equals(ObjectUtils.toString(data_map.get("F_TRANSFER_DATE_5")),"Y")) transDate = transDate + "16,";
			if(StringUtils.equals(ObjectUtils.toString(data_map.get("F_TRANSFER_DATE_3")),"Y")) transDate = transDate + "21,";
			if(StringUtils.equals(ObjectUtils.toString(data_map.get("F_TRANSFER_DATE_6")),"Y")) transDate = transDate + "26,";
			data.addParameter("TRANSFER_DATE", transDate.subSequence(0, transDate.length()-1));
		} else if(transType == 4) {
			//增加子基金
			int rIdx = 0;
			for(int ci=1; ci<=5; ci++) {
				String fProd = ObjectUtils.toString(data_map.get("F_PROD_ID_C"+ Integer.toString(ci)));
				if(StringUtils.isNotBlank(fProd)) {
					String rType = (rIdx == 0 ? "" : "_C" + Integer.toString(rIdx));
					data.addParameter("FUNC_CODE" + rType, "AD");
					data.addParameter("CERTIFICATE_ID" + rType, data_map.get("CERTIFICATE_ID").toString());
					data.addParameter("PROD_ID" + rType, (String)data_map.get("F_PROD_ID_C"+ Integer.toString(ci)));
					data.addParameter("PROD_NAME" + rType, (String)data_map.get("F_PROD_NAME_C"+ Integer.toString(ci)));
					data.addParameter("PROD_RISK_LV" + rType, (String)data_map.get("F_PROD_RISK_LV_C"+ Integer.toString(ci)));
					data.addParameter("TRUST_CURR" + rType, (String)data_map.get("TRUST_CURR"));
					data.addParameter("AMT_TYPE" + rType, "$");
					data.addParameter("PURCHASE_AMT" + rType, getBigDecimal(data_map.get("F_ADDPROD_AMT_C"+ Integer.toString(ci))));
					rIdx++;
				}
			}
		}
		if(StringUtils.isNotBlank(ObjectUtils.toString(data_map.get("REC_SEQ")))) {
			data.addParameter("REC_SEQ","  錄音序號："+data_map.get("REC_SEQ").toString());
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
		data.addParameter("P1_OPENBOOK1", "□");
		data.addParameter("P1_OPENBOOK2", "□");
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
	}
	
	private BigDecimal getBigDecimal(Object val){
		if(val == null){
			return new BigDecimal(0);
		}else{
			return new BigDecimal(val.toString());
		}
	}
	
}
