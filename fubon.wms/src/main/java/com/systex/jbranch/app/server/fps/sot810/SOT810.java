package com.systex.jbranch.app.server.fps.sot810;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.sot712.PRDFitInputVO;
import com.systex.jbranch.app.server.fps.sot712.SotPdf;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.report.ReportIF;
import com.systex.jbranch.platform.common.report.factory.ReportFactory;
import com.systex.jbranch.platform.common.report.generator.ReportGeneratorIF;
import com.systex.jbranch.platform.common.report.reportdata.ReportData;
import com.systex.jbranch.platform.common.report.reportdata.ReportDataIF;
import com.systex.jbranch.platform.common.util.StringUtil;

import static org.apache.commons.lang.StringUtils.isNotBlank;

/**
 * MENU
 *
 * @author Lily
 * @date 2016/11/14
 * @spec null
 */
@Component("sot810")
@Scope("request")
public class SOT810 extends SotPdf {
    private DataAccessManager dam = null;
    private Logger logger = LoggerFactory.getLogger(SOT810.class);

    @Override
    public List<String> printReport() throws JBranchException {
        String url = null;
        String txnCode = "SOT810";
        String reportID = "R1";
        ReportIF report = null;
        List<String> url_list = new ArrayList<String>();

        ReportFactory factory = new ReportFactory();
        ReportDataIF data = new ReportData();
        ReportGeneratorIF gen = factory.getGenerator();  //產出pdf


        PRDFitInputVO inputVO = getInputVO();
        dam = this.getDataAccessManager();

        QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        try {
            StringBuffer sql = new StringBuffer();
            sql.append("select M.CUST_ID, M.CUST_NAME, M.REC_SEQ, M.BRANCH_NBR, M.LASTUPDATE as MLASTUPDATE, ")
                    .append("       D.*, to_char(D.REF_VAL_DATE,'yyyy/mm/dd') AS REFVALDATE, ")
                    .append("       VIEW_AO.BRA_NBR, VIEW_AO.BRANCH_NAME ")
                    .append("from TBSOT_SI_TRADE_D D ")
                    .append("join TBSOT_TRADE_MAIN M on M.TRADE_SEQ = D.TRADE_SEQ ")
                    .append("join VWORG_AO_INFO VIEW_AO on (VIEW_AO.EMP_ID = M.CREATOR and VIEW_AO.TYPE = '1') ") // 主 Code
                    .append("where D.TRADE_SEQ = :tradeSeq order by BATCH_SEQ ");
            queryCondition.setObject("tradeSeq", inputVO.getTradeSeq());
            queryCondition.setQueryString(sql.toString());
            List<Map<String, Object>> list = checkList(dam.exeQuery(queryCondition));
            if (list.size() > 0) {
                //批號
                Map<String, Object> tradeData = list.get(0);
                if (tradeData.get("BATCH_SEQ") != null && isNotBlank(tradeData.get("BATCH_SEQ").toString())) {
                    data.addParameter("BATCH_SEQ", tradeData.get("BATCH_SEQ").toString());
                }
                //投資人
                if (tradeData.get("CUST_NAME") != null && isNotBlank(tradeData.get("CUST_NAME").toString())) {
                    data.addParameter("CUST_NAME", tradeData.get("CUST_NAME").toString());
                }
                // 推薦分行代碼為「庫存收件行」
                data.addParameter("RECOMMAND_BRH_ID", null == inputVO.getIvBrh() ? tradeData.get("BRANCH_NBR") : inputVO.getIvBrh());

                // 中解受理分行為「理專歸屬行」
                String branchName = ObjectUtils.toString(tradeData.get("BRANCH_NAME"));
                data.addParameter("DEPT_ID", ObjectUtils.toString(tradeData.get("BRA_NBR"))
                        + (isNotBlank(branchName) ? " " : "") + branchName);

                //身分證號
                if (tradeData.get("CUST_ID") != null && isNotBlank(tradeData.get("CUST_ID").toString())) {
                    data.addParameter("CUST_ID", tradeData.get("CUST_ID").toString());
                }
                //商品代號
                if (tradeData.get("PROD_ID") != null && isNotBlank(tradeData.get("PROD_ID").toString())) {
                    data.addParameter("PROD_ID", tradeData.get("PROD_ID").toString());
                }
                if (tradeData.get("PROD_NAME") != null && isNotBlank(tradeData.get("PROD_NAME").toString())) {
                    data.addParameter("PROD_NAME", tradeData.get("PROD_NAME").toString());
                }
                //活存帳號
                if (tradeData.get("DEBIT_ACCT") != null && isNotBlank(tradeData.get("DEBIT_ACCT").toString())) {
                    data.addParameter("DEBIT_ACCT", tradeData.get("DEBIT_ACCT").toString());
                }
                //組合式商品帳號
                if (tradeData.get("PROD_ACCT") != null && isNotBlank(tradeData.get("PROD_ACCT").toString())) {
                    data.addParameter("PROD_ACCT", tradeData.get("PROD_ACCT").toString());
                }
                //商品原始承作本金
                String purchaseAmtToString = "";
                if (tradeData.get("PURCHASE_AMT") != null && isNotBlank(tradeData.get("PURCHASE_AMT").toString())) {
                    BigDecimal purchaseAmt = new BigDecimal(tradeData.get("PURCHASE_AMT").toString());
                    data.addParameter("PURCHASE_AMT", tradeData.get("PROD_CURR").toString() + "  " + DoNumberCurrencyToChineseCurrency(purchaseAmt));
                }
                //限價方式
                if (tradeData.get("ENTRUST_TYPE") != null && isNotBlank(tradeData.get("ENTRUST_TYPE").toString())) {
                    data.addParameter("ENTRUST_TYPE", tradeData.get("ENTRUST_TYPE").toString());
                }
                //收件編號
                if (tradeData.get("RECEIVED_NO") != null && isNotBlank(tradeData.get("RECEIVED_NO").toString())) {
                    data.addParameter("RECEIVED_NO", tradeData.get("RECEIVED_NO").toString());
                }
                //參考贖回報價
                if (tradeData.get("REF_VAL") != null && isNotBlank(tradeData.get("REF_VAL").toString())) {
                    data.addParameter("REF_VAL", tradeData.get("REF_VAL").toString());
                }
                //參考贖回報價日期
                if (tradeData.get("REFVALDATE") != null && isNotBlank(tradeData.get("REFVALDATE").toString())) {
                    data.addParameter("REF_VAL_DATE", tradeData.get("REFVALDATE").toString());
                }
                //委託贖回價格

                if (tradeData.get("ENTRUST_AMT") != null && isNotBlank(tradeData.get("ENTRUST_AMT").toString())) {
                    data.addParameter("ENTRUST_AMT", tradeData.get("ENTRUST_AMT").toString());

                }

                String getMonth = "";
                String getDate = "";
                java.sql.Timestamp ts = (java.sql.Timestamp) tradeData.get("MLASTUPDATE");
                getMonth = (ts.getMonth() + 1) + "";
                getDate = ts.getDate() + "";
                if (getMonth.length() < 2) {
                    getMonth = "0" + getMonth;
                }
                if (getDate.length() < 2) {
                    getDate = "0" + getDate;
                }
                data.addParameter("REPORT_DATE", "中華民國  " + (ts.getYear() - 11) + "  年  " + getMonth + "  月  " + getDate + "  日");

                report = gen.generateReport(txnCode, reportID, data);
                url = report.getLocation();
                url_list.add(url);

            }
            return url_list;

        } catch (Exception e) {
            logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
            throw new APException("系統發生錯誤請洽系統管理員");
        }


    }

    /**
     * Description 將數位金額轉換為中文金額
     * @param <p>BigDecimal bigdMoneyNumber 轉換前的數位金額</P>
     * @return String
        調用：myToChineseCurrency(\"101.89\")=\"壹佰零壹圓捌角玖分\"
        myToChineseCurrency(\"100.89\")=\"壹佰零捌角玖分\"
        myToChineseCurrency(\"100\")=\"壹佰圓整\"
     */
	public String DoNumberCurrencyToChineseCurrency(BigDecimal bigdMoneyNumber) {
        String strChineseCurrency = "";

        //中文金額單位陣列
        String[] straChineseUnit = new String[]{"分", "角", "圓", "拾", "佰", "仟", "萬", "拾", "佰", "仟", "億 ", "拾", "佰", "仟"};
        //中文數位字元陣列
        String[] straChineseNumber = new String[]{"零", "壹", "貳", "參", "肆", "伍", "陸", "柒", "捌", "玖"};
        //零數位標記
        boolean bZero = true;
        //中文金額單位下標
        int ChineseUnitIndex = 0;

        try {
            if (bigdMoneyNumber.intValue() == 0)
                return "零圓整";

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