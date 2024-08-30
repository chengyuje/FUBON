package com.systex.jbranch.app.server.fps.sot809;

import com.systex.jbranch.app.server.fps.sot712.PRDFitInputVO;
import com.systex.jbranch.app.server.fps.sot712.SotPdf;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.report.factory.ReportFactory;
import com.systex.jbranch.platform.common.report.generator.ReportGeneratorIF;
import com.systex.jbranch.platform.common.report.reportdata.ReportData;
import com.systex.jbranch.platform.common.report.reportdata.ReportDataIF;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * FCI商品契約書
 **/
@Component("sot809FCI")
@Scope("request")
public class SOT809FCI extends SotPdf {
    private DataAccessManager dam = null;

    @Override
    public List<String> printReport() throws Exception {
        PRDFitInputVO inputVO = getInputVO();
        ReportGeneratorIF generator = new ReportFactory().getGenerator();
        ReportDataIF data = new ReportData();
        List<String> urlList = new ArrayList<String>();
        dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		//FCI商品契約書
		urlList.add(getFCIContractReport( inputVO.getTradeSeq()));
		
		sql.append("SELECT DOC_FILE FROM TBPRD_FCI_DOC ");
		sql.append(" WHERE DOC_FILE IS NOT NULL ");	
		sql.append(" AND DOC_TYPE = '2' ");	
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> listFile = dam.exeQuery(queryCondition);
		
		//組合式商品申購交易文件檢核表
		//PM上傳文件
		if(CollectionUtils.isNotEmpty(listFile)) {
			for(Map<String, Object> map : listFile) {
				String filePath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
				String uuid = UUID.randomUUID().toString();
				Blob blob = (Blob) map.get("DOC_FILE");
				int blobLength = (int) blob.length();
				byte[] blobAsBytes = blob.getBytes(1, blobLength);

				File targetFile = new File(filePath, uuid);
				FileOutputStream fos = new FileOutputStream(targetFile);
				fos.write(blobAsBytes);
				fos.close();
				
				urlList.add("temp/" + uuid);
			}
		}
		
        return urlList;
    }
    
    private String getFCIContractReport(String tradeSeq) throws Exception {
    	String TXN_CODE = "SOT809";
        String REPORT_ID = "R2";	//FCI商品契約書
        
    	ReportGeneratorIF generator = new ReportFactory().getGenerator();
    	ReportDataIF data = new ReportData();
    	dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append("select B.*, A.CUST_ID, A.CUST_NAME, A.REC_SEQ, CASE WHEN A.IS_OBU = 'Y' THEN 'OBU' ELSE 'DBU' END AS ODBU, ");
		sql.append(" REPLACE(B.PROD_RISK_LV, 'P', 'C') AS CUST_RISK_ATR, ");
		sql.append(" C.PARAM_NAME AS PROD_CURR_NAME, E.PARAM_NAME AS CUST_RISK_NAME, ");
		sql.append(" TO_CHAR(B.TRADE_DATE, 'YYYY-MM-DD') AS TRADE_DATE_NAME, TO_CHAR(B.VALUE_DATE, 'YYYY-MM-DD') AS VALUE_DATE_NAME, ");
		sql.append(" TO_CHAR(B.EXPIRE_DATE, 'YYYY-MM-DD') AS EXPIRE_DATE_NAME, TO_CHAR(B.SPOT_DATE, 'YYYY-MM-DD') AS SPOT_DATE_NAME, ");
		sql.append(" F.PARAM_NAME AS REUTERS_PAGE, G.PARAM_NAME AS TARGET_PRICING_RMK, ");
		sql.append(" H.PARAM_NAME AS PRICING_BUSINESS_DATE, I.PARAM_NAME AS DELIVERY_BUSINESS_DATE ");
		sql.append(" from TBSOT_TRADE_MAIN A ");
		sql.append(" LEFT JOIN TBSOT_FCI_TRADE_D B ON B.TRADE_SEQ = A.TRADE_SEQ ");
		sql.append(" LEFT JOIN TBSYSPARAMETER C ON C.PARAM_TYPE = 'PRD.FCI_CURRENCY' AND C.PARAM_CODE = B.PROD_CURR ");
		sql.append(" LEFT JOIN TBSYSPARAMETER E ON E.PARAM_TYPE = 'KYC.RISK_TYPE' AND E.PARAM_CODE = REPLACE(B.PROD_RISK_LV, 'P', 'C') ");
		sql.append(" LEFT JOIN TBSYSPARAMETER F ON F.PARAM_TYPE = 'SOT.FCI_REUTERS_PAGE' AND F.PARAM_CODE = B.PROD_CURR ");
		sql.append(" LEFT JOIN TBSYSPARAMETER G ON G.PARAM_TYPE = 'SOT.FCI_TARGET_PRICING_RMK' AND G.PARAM_CODE = B.PROD_CURR ");
		sql.append(" LEFT JOIN TBSYSPARAMETER H ON H.PARAM_TYPE = 'SOT.FCI_PRICING_BUSINESS_DATE' AND H.PARAM_CODE = B.PROD_CURR ");
		sql.append(" LEFT JOIN TBSYSPARAMETER I ON I.PARAM_TYPE = 'SOT.FCI_DELIVERY_BUSINESS_DATE' AND I.PARAM_CODE = B.PROD_CURR ");
		sql.append(" where A.TRADE_SEQ = :trade_seq ");	
          
		queryCondition.setObject("trade_seq", tradeSeq);
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> dataList = dam.exeQuery(queryCondition);
		
        data.addParameter("PROD_ID", ObjectUtils.toString(dataList.get(0).get("PROD_ID")));
		data.addParameter("PROD_NAME", ObjectUtils.toString(dataList.get(0).get("PROD_NAME")));
		String prodCurr = ObjectUtils.toString(dataList.get(0).get("PROD_CURR"));
		data.addParameter("PROD_CURR", prodCurr);
		data.addParameter("PROD_CURR_CONVERT", StringUtils.equals("CNH", prodCurr) ? "CNY" : prodCurr);
		data.addParameter("TARGET_CURR_ID", ObjectUtils.toString(dataList.get(0).get("TARGET_CURR_ID")));
		data.addParameter("BATCH_SEQ", ObjectUtils.toString(dataList.get(0).get("BATCH_SEQ")));
		data.addParameter("PURCHASE_AMT", ObjectUtils.toString(dataList.get(0).get("PURCHASE_AMT")));
		data.addParameter("STRIKE_PRICE", ObjectUtils.toString(dataList.get(0).get("STRIKE_PRICE")));			//履約價
		data.addParameter("PRD_PROFEE_RATE", ObjectUtils.toString(dataList.get(0).get("PRD_PROFEE_RATE")));		//到期比價匯率大於等於履約價
		data.addParameter("LESS_PROFEE_RATE", ObjectUtils.toString(dataList.get(0).get("LESS_PROFEE_RATE")));	//到期比價匯率小於履約價
		data.addParameter("CUST_ID", ObjectUtils.toString(dataList.get(0).get("CUST_ID")));
		data.addParameter("CUST_NAME", ObjectUtils.toString(dataList.get(0).get("CUST_NAME")));
		data.addParameter("DEBIT_ACCT", ObjectUtils.toString(dataList.get(0).get("DEBIT_ACCT")));
		data.addParameter("PROD_ACCT", ObjectUtils.toString(dataList.get(0).get("PROD_ACCT")));
		data.addParameter("REC_SEQ", ObjectUtils.toString(dataList.get(0).get("REC_SEQ")));
		data.addParameter("REC_CODE", ObjectUtils.toString(dataList.get(0).get("REC_CODE")));
		data.addParameter("CUST_RISK_ATR", ObjectUtils.toString(dataList.get(0).get("CUST_RISK_ATR"))); //C值與產品風險等級一致即可
		data.addParameter("PROD_RISK_LV", ObjectUtils.toString(dataList.get(0).get("PROD_RISK_LV")));
		data.addParameter("PROD_MIN_BUY_AMT", ObjectUtils.toString(dataList.get(0).get("PROD_MIN_BUY_AMT")));	//最低申購面額
		data.addParameter("PROD_MIN_GRD_AMT", ObjectUtils.toString(dataList.get(0).get("PROD_MIN_GRD_AMT")));	//累計申購面額
		data.addParameter("ODBU", ObjectUtils.toString(dataList.get(0).get("ODBU")));							//OBU/DBU
		data.addParameter("PROD_CURR_NAME", ObjectUtils.toString(dataList.get(0).get("PROD_CURR_NAME")));		//幣別中文
		data.addParameter("TARGET_CURR_NAME", ObjectUtils.toString(dataList.get(0).get("TARGET_NAME")));		//連結標的
		data.addParameter("CUST_RISK_NAME", ObjectUtils.toString(dataList.get(0).get("CUST_RISK_NAME")));		//客戶風險屬性中文
		data.addParameter("TRADE_DATE_NAME", dateFormat(ObjectUtils.toString(dataList.get(0).get("TRADE_DATE_NAME"))));		//交易日
		data.addParameter("VALUE_DATE_NAME",dateFormat( ObjectUtils.toString(dataList.get(0).get("VALUE_DATE_NAME"))));		//扣款(起息)日
		data.addParameter("EXPIRE_DATE_NAME", dateFormat(ObjectUtils.toString(dataList.get(0).get("EXPIRE_DATE_NAME"))));	//到期(入帳)日
		data.addParameter("SPOT_DATE_NAME", dateFormat(ObjectUtils.toString(dataList.get(0).get("SPOT_DATE_NAME"))));		//比價日
		data.addParameter("REUTERS_PAGE", ObjectUtils.toString(dataList.get(0).get("REUTERS_PAGE")));			//FCI路透頁面
		data.addParameter("TARGET_PRICING_RMK", ObjectUtils.toString(dataList.get(0).get("TARGET_PRICING_RMK")));//FCI觀察指標定價
		data.addParameter("PRICING_BUSINESS_DATE", ObjectUtils.toString(dataList.get(0).get("PRICING_BUSINESS_DATE")));//定價營業日
		data.addParameter("DELIVERY_BUSINESS_DATE", ObjectUtils.toString(dataList.get(0).get("DELIVERY_BUSINESS_DATE")));//交割營業日
		String amtString = DoNumberCurrencyToChineseCurrency((BigDecimal)dataList.get(0).get("PURCHASE_AMT"));
		data.addParameter("PURCHASE_AMT_STR", amtString);//申購金額
		String authId = ObjectUtils.toString(dataList.get(0).get("AUTH_ID"));
		data.addParameter("AUTH_ID", StringUtils.isBlank(authId) ? "                    " : authId);
		
		String getMonth = "";
		String getDate = "";
		Date today = new Date();
		Timestamp ts = new Timestamp(today.getTime());
		getMonth = (ts.getMonth()+1)+"";
		getDate = ts.getDate()+"";
		if (getMonth.length() < 2){
			getMonth = "0"+getMonth;
		}
		if (getDate.length() < 2){
			getDate = "0"+getDate;
		}
		data.addParameter("REPORT_DATE", "中華民國  "+ (ts.getYear()-11) + "  年  " + getMonth + "  月  " + getDate+"  日");
		
		return generator.generateReport(TXN_CODE, REPORT_ID, data).getLocation();
    }
    
    private String dateFormat(String dateString) {
    	String mDate = "";
    	try {
    		String[] tokens = dateString.split("-");
    		mDate = tokens[0] + "年" + tokens[1] + "月" + tokens[2] + "日";
    	} catch(Exception e) {}
    	
    	return mDate;
    }
    
    /**
     * Description 將數位金額轉換為中文金額
     * @param <p>BigDecimal bigdMoneyNumber 轉換前的數位金額</P>
     * @return String
        	調用：	myToChineseCurrency(\"101.89\")=\"壹佰零壹元捌角玖分\"
        			myToChineseCurrency(\"100.89\")=\"壹佰零捌角玖分\"
        			myToChineseCurrency(\"100\")=\"壹佰元整\"
     */
	private String DoNumberCurrencyToChineseCurrency(BigDecimal bigdMoneyNumber) {
        String strChineseCurrency = "";

        //中文金額單位陣列
        String[] straChineseUnit = new String[]{"分", "角", "元", "拾", "佰", "仟", "萬", "拾", "佰", "仟", "億 ", "拾", "佰", "仟"};
        //中文數位字元陣列
        String[] straChineseNumber = new String[]{"零", "壹", "貳", "參", "肆", "伍", "陸", "柒", "捌", "玖"};
        //零數位標記
        boolean bZero = true;
        //中文金額單位下標
        int ChineseUnitIndex = 0;

        try {
            if (bigdMoneyNumber.intValue() == 0)
                return "零元整";

            //處理小數部分，四捨五入
            double doubMoneyNumber = Math.round(bigdMoneyNumber.doubleValue() * 100);

            //是否負數
            boolean bNegative = doubMoneyNumber < 0;

            //取絕對值
            doubMoneyNumber = Math.abs(doubMoneyNumber);

            //迴圈處理轉換操作
            while (doubMoneyNumber > 0) {
                //整的處理(無小數位)
                if (ChineseUnitIndex == 2 && strChineseCurrency.length() == 0)
                    strChineseCurrency = strChineseCurrency + "整";

                //非零數位的處理
                if (doubMoneyNumber % 10 > 0) {
                    strChineseCurrency = straChineseNumber[new BigDecimal(doubMoneyNumber % 10).intValue()] + straChineseUnit[ChineseUnitIndex] + strChineseCurrency;
                    bZero = false;
                }
                //零數位的處理
                else {
                    //元的處理(個位)
                    if (ChineseUnitIndex == 2) {
                        //段中有數位
                        if (doubMoneyNumber > 0) {
                            strChineseCurrency = straChineseUnit[ChineseUnitIndex] + strChineseCurrency;
	                        bZero = true;
                        }
                    }
                    //萬、億數位的處理
                    else if (ChineseUnitIndex == 6 || ChineseUnitIndex == 10) {
                        // 段中有數位
                        if (doubMoneyNumber % 10000 > 0)
							strChineseCurrency = straChineseUnit[ChineseUnitIndex] + strChineseCurrency;
					}

	                //前一數位非零的處理。
                	if (!bZero &&
                            // 再進行拾萬，拾億前位數時，不需要加零
                            !(ChineseUnitIndex+"").matches("6||10"))
                            strChineseCurrency = straChineseNumber[0] + strChineseCurrency;
                    bZero = true;
                }

                doubMoneyNumber = Math.floor(doubMoneyNumber / 10);
                ChineseUnitIndex++;
            }

            //負數的處理
            if (bNegative)
                strChineseCurrency = "負" + strChineseCurrency;
        } catch (Exception e) {

            e.printStackTrace();

            return "";
        }

        return strChineseCurrency;
    }
}
