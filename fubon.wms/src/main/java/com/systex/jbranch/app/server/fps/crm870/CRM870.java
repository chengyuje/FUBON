package com.systex.jbranch.app.server.fps.crm870;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("crm870")
@Scope("request")
public class CRM870 extends FubonWmsBizLogic {
	
	private DataAccessManager dam = null;
	
	/* 取得庫存明細 前端入口 */
	public void getAST_INV_SEC (Object body, IPrimitiveMap header) throws JBranchException {
		
		CRM870InputVO inputVO = (CRM870InputVO) body;
		CRM870OutputVO outputVO = new CRM870OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		switch (inputVO.getTabSheet()) {
			case 2 : // 海外股票
			case 3 : // 海外債
			case 4 : // 境外結構型商品
				sb.append("SELECT M.DATA_DATE, ");
				sb.append("       M.SNAP_DATE, ");
				sb.append("       M.BRANCH_NBR, ");
				sb.append("       M.BRANCH_NAME, ");
				sb.append("       M.CUST_ID, ");
				sb.append("       M.ACCT_NBR, ");
				sb.append("       M.PRODUCT_CODE, ");
				sb.append("       M.PRODUCT_CODE_DESC, ");
				sb.append("       M.COUNTRY_CODE, ");
				sb.append("       M.COUNTRY_NAME, ");
				sb.append("       M.EXCHANGE_CODE, ");
				sb.append("       M.EXCHANGE_ABBR_NAME, ");
				sb.append("       M.UPPER_COMPANY_CODE, ");
				sb.append("       M.UPPER_COMPANY_ABBR_NAME,");
				sb.append("       M.PROD_ID, ");
				sb.append("       M.PROD_NAME, ");
				sb.append("       M.ISIN_CODE, ");
				sb.append("       M.CURRENCY, ");
				sb.append("       M.QTY, ");
				sb.append("       M.EXCHG_RATE_D, ");
				sb.append("       M.AVG_COST_PRICE, ");
				sb.append("       M.INVEST_AMT_FC, ");
				sb.append("       M.INVEST_AMT_TW, ");
				sb.append("       M.UNIT_PRICE, ");
				sb.append("       M.PRICE_DATE, ");
				sb.append("       M.AUM_FC, ");
				sb.append("       M.AUM_TW, ");
				sb.append("       M.DIVIDENT_AMT_FC, ");
				sb.append("       M.DIVIDENT_AMT_TW, ");
				sb.append("       M.BENEFIT_AMT1, ");
				sb.append("       M.BENEFIT_RATE1, ");
				sb.append("       M.BENEFIT_AMT2, ");
				sb.append("       M.BENEFIT_RATE2, ");
				sb.append("       M.BOND_COUPON_RATE, ");
				sb.append("       M.ISSUE_BROKER_NAME, ");
				sb.append("       M.MATURITY_DATE, ");
				sb.append("       M.RANK_LEVEL_MOODYS, ");
				sb.append("       M.RANK_LEVEL_SP, ");
				sb.append("       M.RANK_LEVEL_FITCH, ");
				sb.append("       M.RANK_LEVEL_OTHER, ");
				sb.append("       M.ACCT_FLAG, ");
				sb.append("       M.CUSTOMER_ID_MASK, ");
				sb.append("       M.ACCT_NBR_MASK, ");
				sb.append("       M.TRANS_SN, ");
				sb.append("       M.EXEC_DATE, ");
				sb.append("       M.VERSION, ");
				sb.append("       M.CREATETIME, ");
				sb.append("       M.CREATOR, ");
				sb.append("       M.MODIFIER, ");
				sb.append("       M.LASTUPDATE, ");
				sb.append("       M.PROD_ID AS STOCK_CODE ");
				
				switch (inputVO.getTabSheet()) {
					case 2 : // 海外股票
						sb.append("FROM TBCRM_AST_INV_SEC_STOCK M ");
						break;
					case 3 : // 海外債
						sb.append("FROM TBCRM_AST_INV_SEC_BOND M ");
						break;
					case 4 : // 結構型商品
						sb.append("FROM TBCRM_AST_INV_SEC_SN M ");
						break;
				}

				appendSecSql(sb);
				
				sb.append("WHERE 1 = 1 ");

				switch (inputVO.getTabSheet()) {
					case 2 : // 海外股票
						sb.append("AND M.DATA_DATE = (SELECT MAX(DATA_DATE) FROM TBCRM_AST_INV_SEC_STOCK) ");
						break;
					case 3 : // 海外債
						sb.append("AND M.DATA_DATE = (SELECT MAX(DATA_DATE) FROM TBCRM_AST_INV_SEC_BOND) ");
						break;
					case 4 : // 結構型商品
						sb.append("AND M.DATA_DATE = (SELECT MAX(DATA_DATE) FROM TBCRM_AST_INV_SEC_SN) ");
						break;
				}
				
				sb.append("AND M.CUST_ID = :custID ");
				
				switch (inputVO.getTabSheet()) {
					case 3 : // 海外債
						sb.append("ORDER BY M.PROD_NAME ");
						break;
					case 2 : // 海外股票
					case 4 : // 結構型商品
						sb.append("ORDER BY M.PROD_ID ");
						break;
				}
				
				break;
			case 5 : // 境內結構型商品
				sb.append("SELECT M.SNAP_DATE, ");
				sb.append("       M.CUSTOMER_ID, ");
				sb.append("       M.ACCT_NBR, ");
				sb.append("       M.STOCK_CODE, ");
				sb.append("       M.STOCK_NAME, ");
				sb.append("       M.STRATEGY, ");
				sb.append("       M.STOCK_TYPE, ");
				sb.append("       M.CURRENCY, ");
				sb.append("       M.TXN_DATE, ");
				sb.append("       M.SETTLE_DATE, ");
				sb.append("       M.MATURITY_DATE, ");
				sb.append("       M.DURATION, ");
				sb.append("       M.TARGET_CODE, ");
				sb.append("       M.TARGET_NAME, ");
				sb.append("       SUBSTR(M.TARGET_NAME, 0, 6) AS TARGET_NAME_6, ");
				sb.append("       M.UNIT_PRICE, ");
				sb.append("       M.TXN_FACE_VALUE, ");
				sb.append("       M.REM_FACE_VALUE, ");
				sb.append("       M.INVEST_COST, ");
				sb.append("       M.FACE_VALUE, ");
				sb.append("       M.DIVIDEND_AMT, ");
				sb.append("       M.REFER_PRICE, ");
				sb.append("       M.INS_DATE, ");
				sb.append("       M.AUM_TW ");
				sb.append("FROM TBCRM_AST_INV_SEC_DSN M ");
				
				appendSecDsnSql(sb);
				
				sb.append("WHERE 1 = 1 ");
				
				sb.append("AND M.SNAP_DATE = (SELECT MAX(SNAP_DATE) FROM TBCRM_AST_INV_SEC_DSN) ");
				sb.append("AND TRUNC(M.MATURITY_DATE) > TRUNC(SYSDATE) ");
				
				sb.append("AND M.CUSTOMER_ID = :custID ");
				
				sb.append("ORDER BY M.STOCK_CODE ");
				
				break;
		}
		
		queryCondition.setObject("custID", inputVO.getCustID());
		
		queryCondition.setQueryString(sb.toString());

		outputVO.setResultList(dam.exeQuery(queryCondition));
		
		this.sendRtnObject(outputVO);
	}
	
	/* 取得交易明細 前端入口 */
	public void getTxnList (Object body, IPrimitiveMap header) throws JBranchException {
		
		CRM870InputVO inputVO = (CRM870InputVO) body;
		CRM870OutputVO outputVO = new CRM870OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		// 往來記錄/成交明細
		switch (inputVO.getTabSheet()) {
			case 1 : // 全部
			case 2 : // 海外股票
			case 3 : // 海外債
			case 4 : // 境外結構型商品
				sb.append("SELECT M.DATA_DATE, ");                   // 資料日期             
				sb.append("       M.TXN_DATE, ");                    // 成交日期             
				sb.append("       M.SETTLE_DATE, ");                 // 交割日期             
				sb.append("       M.BRANCH_NBR, ");                  // 分公司代碼           
				sb.append("       M.BRANCH_NAME, ");                 // 分公司名稱           
				sb.append("       M.WARRANT_NBR, ");                 // 委託書編號           
				sb.append("       M.WARRANT_SEQ_NBR, ");             // 分單號碼             
				sb.append("       M.PRODUCT_CODE, ");                // 產品類別代碼         
				sb.append("       M.PRODUCT_CODE_DESC, ");           // 產品類別說明         
				sb.append("       M.CUST_ID, ");                     // 客戶ID(PER)(ID)      
				sb.append("       M.ACCT_NBR, ");                    // 客戶帳號(PER)(ACCT)  
				sb.append("       M.TRADING_TYPE_CODE, ");           // 交易類別代碼         
				sb.append("       M.TRADING_TYPE_CODE_DESC, ");      // 交易類別說明         
				sb.append("       M.COUNTRY_CODE, ");                // 國別代號             
				sb.append("       M.COUNTRY_NAME, ");                // 國別簡稱             
				sb.append("       M.EXCHANGE_CODE, ");               // 交易所代號           
				sb.append("       M.EXCHANGE_ABBR_NAME, ");          // 交易所簡稱           
				sb.append("       M.UPPER_COMPANY_CODE, ");          // 上手券商代號         
				sb.append("       M.UPPER_COMPANY_ABBR_NAME, ");     // 上手券商簡稱         
				sb.append("       M.PROD_ID, ");                     // 商品代號             
				sb.append("       M.PROD_NAME, ");                   // 商品名稱             
				sb.append("       M.ISIN_CODE, ");                   // ISIN_CODE            
				sb.append("       M.CURRENCY, ");                    // 幣別                 
				sb.append("       M.QTY, ");                         // 單位數/面額          
				sb.append("       M.UNIT_PRICE, ");                  // 成交價格             
				sb.append("       M.BOND_UPPER_PRICE, ");            // 債券上手價格         
				sb.append("       M.EXCHG_RATE_D, ");                // 匯率                 
				sb.append("       M.BOND_PRE_INTEREST, ");           // 債券前手息(原幣)     
				sb.append("       M.SETTLE_AMT_FC, ");               // 交割金額(原幣)       
				sb.append("       M.TXN_AMT_FC, ");                  // 交易量(原幣)         
				sb.append("       M.TXN_AMT_TW, ");                  // 交易量(台幣)         
				sb.append("       M.SERVICE_FEE_FC, ");              // 手續費收益(原幣)     
				sb.append("       M.SERVICE_FEE_TW, ");              // 手續費收益(台幣)     
				sb.append("       M.TXN_COST_RATE1, ");              // 交易成本費率1        
				sb.append("       M.TXN_COST_RATE2, ");              // 交易成本費率2        
				sb.append("       M.TXN_COST1, ");                   // 交易成本1(台幣)      
				sb.append("       M.TXN_COST2, ");                   // 交易成本2(台幣)      
				sb.append("       M.NET_FEE, ");                     // 扣成本後淨收入(台幣) 
				sb.append("       M.ACCT_FLAG, ");                   // 帳號註記(Y為設質帳號)
				sb.append("       M.CUSTOMER_ID_MASK, ");            // 客戶ID_MASK          
				sb.append("       M.ACCT_NBR_MASK, ");               // 客戶帳號_MASK        
				sb.append("       M.TRANS_SN, ");                    // TWSJobStreamNO       
				sb.append("       M.EXEC_DATE, ");                   // TWS執行日            
				sb.append("       M.VERSION, ");                     
				sb.append("       M.CREATETIME, ");
				sb.append("       M.CREATOR, ");
				sb.append("       M.MODIFIER, ");
				sb.append("       M.LASTUPDATE, ");
				sb.append("       M.PROD_ID AS STOCK_CODE ");

				switch (inputVO.getTabSheet()) {
					case 1 :
						sb.append("FROM ( ");
						sb.append("  SELECT M.* FROM TBCRM_AST_INV_SEC_STOCK_TXN M ");
						appendSecSql(sb);
						sb.append("  UNION ");
						sb.append("  SELECT M.* FROM TBCRM_AST_INV_SEC_BOND_TXN M ");
						appendSecSql(sb);
						sb.append("  UNION ");
						sb.append("  SELECT M.* FROM TBCRM_AST_INV_SEC_SN_TXN M ");
						appendSecSql(sb);
						sb.append("  UNION ");
						sb.append("  SELECT TRUNC(M.CREATETIME) AS DATA_DATE, ");          	
						sb.append("         M.TXN_DATE, ");                                	
						sb.append("         M.SETTLE_DATE, ");                             	
						sb.append("         NULL AS BRANCH_NBR, ");                      	
						sb.append("         NULL AS BRANCH_NAME, ");                     	
						sb.append("         M.TXN_WARRANT_NBR AS WARRANT_NBR, ");          	
						sb.append("         NULL AS WARRANT_SEQ_NBR, ");                 	
						sb.append("         'DSN' AS PRODUCT_CODE, ");                   	
						sb.append("         '境內結構型商品' AS PRODUCT_CODE_DESC, ");		
						sb.append("         M.CUSTOMER_ID AS CUST_ID, ");                  	
						sb.append("         M.ACCT_NBR, ");                                	
						sb.append("         M.TXN_TYPE AS TRADING_TYPE_CODE, ");           	
						sb.append("         M.TXN_TYPE_DESC AS TRADING_TYPE_CODE_DESC, "); 	
						sb.append("         NULL AS COUNTRY_CODE, ");                    	
						sb.append("         NULL AS COUNTRY_NAME, ");                    	
						sb.append("         NULL AS EXCHANGE_CODE, ");                   	
						sb.append("         NULL AS EXCHANGE_ABBR_NAME, ");              	
						sb.append("         NULL AS UPPER_COMPANY_CODE, ");              	
						sb.append("         NULL AS UPPER_COMPANY_ABBR_NAME, ");         	
						sb.append("         M.STOCK_CODE AS PROD_ID, ");                   	
						sb.append("         CAST(M.STOCK_NAME AS NVARCHAR2(300)) AS PROD_NAME, ");
						sb.append("         NULL AS ISIN_CODE, ");                  
						sb.append("         'TWD' AS CURRENCY, ");                  
						sb.append("         M.FACE_VALUE AS QTY, ");                  
						sb.append("         M.UNIT_PRICE, ");                         
						sb.append("         NULL AS TEMP, ");
						sb.append("         NULL AS EXCHG_RATE_D, ");               
						sb.append("         0 AS BOND_PRE_INTEREST, ");             
						sb.append("         M.SETTLE_AMT AS SETTLE_AMT_FC, ");        
						sb.append("         M.RECEIVABLES_AMT AS TXN_AMT_FC, ");                 
						sb.append("         NULL AS TXN_AMT_TW, ");                 
						sb.append("         NULL AS SERVICE_FEE_FC, ");             
						sb.append("         M.SERVICE_FEE AS SERVICE_FEE_TW, ");      
						sb.append("         NULL AS TXN_COST_RATE1, ");             
						sb.append("         NULL AS TXN_COST_RATE2, ");             
						sb.append("         NULL AS TXN_COST1, ");                  
						sb.append("         NULL AS TXN_COST2, ");                  
						sb.append("         NULL AS NET_FEE, ");                    
						sb.append("         NULL AS ACCT_FLAG, ");                  
						sb.append("         NULL AS CUSTOMER_ID_MASK, ");           
						sb.append("         NULL AS ACCT_NBR_MASK, ");              
						sb.append("         NULL AS TRANS_SN, ");                   
						sb.append("         NULL AS EXEC_DATE, ");                  
						sb.append("         M.VERSION, ");                     
						sb.append("         M.CREATETIME, ");
						sb.append("         M.CREATOR, ");
						sb.append("         M.MODIFIER, ");
						sb.append("         M.LASTUPDATE ");
						sb.append("  FROM TBCRM_AST_INV_SEC_DSN_TXN M ");
						appendSecDsnSql(sb);
						sb.append("  WHERE 1 = 1 ");
						sb.append("  AND EXTRACT_TYPE_DESC IS NULL ");
						sb.append(") M ");
						break;
					case 2 :
						sb.append("FROM TBCRM_AST_INV_SEC_STOCK_TXN M ");
						appendSecSql(sb);
						break;
					case 3 :
						sb.append("FROM TBCRM_AST_INV_SEC_BOND_TXN M ");
						appendSecSql(sb);
						break;
					case 4 :
						sb.append("FROM TBCRM_AST_INV_SEC_SN_TXN M ");
						appendSecSql(sb);
						break;
				}
				
				sb.append("WHERE 1 = 1 ");
				
				sb.append("AND M.CUST_ID = :custID ");		
									
				switch (inputVO.getRptType()) {
					case "DT_SEARCH": // 成交日期-起 ~ 成交日期-迄
						sb.append("AND TO_CHAR(M.TXN_DATE, 'yyyyMMdd') >= TO_CHAR(:sDate, 'yyyyMMdd') ");
						sb.append("AND TO_CHAR(M.TXN_DATE, 'yyyyMMdd') <= TO_CHAR(:eDate, 'yyyyMMdd') ");
						queryCondition.setObject("sDate", inputVO.getStartDate());			
						queryCondition.setObject("eDate", inputVO.getEndDate());	
						
						break;
					case "PRD_SEARCH":
						sb.append("AND PROD_ID = :prdID ");
						queryCondition.setObject("prdID", inputVO.getPrdID());			
						break;
				}
				
				sb.append("ORDER BY M.TXN_DATE DESC ");
				
				break;
			case 5 : // 境內結構型商品
				sb.append("SELECT DSN.SNAP_DATE, ");
				sb.append("       DSN.MATURITY_DATE, ");
				sb.append("       DSN.SETTLE_DATE, ");
				sb.append("       M.TXN_WARRANT_NBR, ");
				sb.append("       M.CUSTOMER_ID, ");
				sb.append("       M.ACCT_NBR, ");
				sb.append("       M.STOCK_CODE, ");
				sb.append("       M.STOCK_NAME, ");
				sb.append("       M.TXN_DATE, ");
				sb.append("       M.UNIT_PRICE, ");
				sb.append("       M.FEE_RATE, ");
				sb.append("       M.TXN_TYPE, ");
				sb.append("       M.TXN_TYPE_DESC, ");
				sb.append("       M.EXTRACT_TYPE, ");
				sb.append("       M.EXTRACT_TYPE_DESC, ");
				sb.append("       M.SETTLE_TYPE, ");
				sb.append("       M.SETTLE_TYPE_DESC, ");
				sb.append("       M.FACE_VALUE, ");
				sb.append("       M.SERVICE_FEE, ");
				sb.append("       M.RECEIVABLES_AMT, ");
				sb.append("       M.EXTRACT_WARRANT_NBR, ");
				sb.append("       M.WITHHOLDING_TAX, ");
				sb.append("       M.SETTLE_AMT, ");
				sb.append("       M.NBR_OF_SHARES, ");
				sb.append("       M.EXTRACT_COST, ");
				sb.append("       M.BENEFIT_AMT, ");
				sb.append("       M.PAY_OVERDUE_TAX_DATE, ");
				sb.append("       M.SELF_DEDUCTION, ");
				sb.append("       M.BROKER_NBR, ");
				sb.append("       M.BROKER_NAME, ");
				sb.append("       M.TDC_ACCT_NBR, ");
				sb.append("       M.STRATEGY, ");
				sb.append("       M.TXN_STATUS, ");
				sb.append("       M.TXN_STATUS_DESC, ");
				sb.append("       M.CONFIRMATION_REPLY_DATE, ");
				sb.append("       M.UPD_DATE, ");
				sb.append("       M.SETTLE_FLAG, ");
				sb.append("       M.SETTLE_STOCK_CODE, ");
				sb.append("       M.SETTLE_STOCK_NAME, ");
				sb.append("       M.SETTLE_NBR_OF_SHARES, ");
				sb.append("       M.CLOSING_PRICE, ");
				sb.append("       M.STRIKE_PRICE, ");
				sb.append("       M.SBL_DATE, ");
				sb.append("       M.SBL_TIME, ");
				sb.append("       M.TAX_AMT, ");
				sb.append("       M.INS_DATE, ");
				sb.append("       M.VERSION, ");
				sb.append("       M.CREATETIME, ");
				sb.append("       M.CREATOR, ");
				sb.append("       M.MODIFIER, ");
				sb.append("       M.LASTUPDATE ");
				sb.append("FROM TBCRM_AST_INV_SEC_DSN_TXN M ");
				sb.append("LEFT JOIN TBPRD_TFB_DOMESTIC_SN DSN ON M.STOCK_CODE = DSN.STOCK_CODE AND TRUNC(DSN.SNAP_DATE) = TRUNC((SELECT MAX(SNAP_DATE) FROM TBPRD_TFB_DOMESTIC_SN)) ");
				appendSecDsnSql(sb);
				sb.append("WHERE 1 = 1 ");
				
				sb.append("AND M.EXTRACT_TYPE_DESC IS NULL ");

				sb.append("AND M.CUSTOMER_ID = :custID ");

				switch (inputVO.getRptType()) {
					case "DT_SEARCH": // 成交日期-起 ~ 成交日期-迄
						sb.append("AND TO_CHAR(M.TXN_DATE, 'yyyyMMdd') >= TO_CHAR(:sDate, 'yyyyMMdd') ");
						sb.append("AND TO_CHAR(M.TXN_DATE, 'yyyyMMdd') <= TO_CHAR(:eDate, 'yyyyMMdd') ");
						queryCondition.setObject("sDate", inputVO.getStartDate());			
						queryCondition.setObject("eDate", inputVO.getEndDate());	
						
						break;
					case "PRD_SEARCH":
						sb.append("AND M.STOCK_CODE = :prdID ");
						queryCondition.setObject("prdID", inputVO.getPrdID());			
						break;
				}
				
				sb.append("ORDER BY M.TXN_DATE DESC ");
				
				break;
		}
		
		queryCondition.setObject("custID", inputVO.getCustID());		

		queryCondition.setQueryString(sb.toString());
		
		outputVO.setTxnList(dam.exeQuery(queryCondition));
		
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		// 到期交割明細
		switch (inputVO.getTabSheet()) {
			case 5 : // 境內結構型商品
				sb.append("SELECT DSN.SNAP_DATE, ");
				sb.append("       DSN.MATURITY_DATE, ");
				sb.append("       DSN.SETTLE_DATE, ");
				sb.append("       M.TXN_WARRANT_NBR, ");
				sb.append("       M.CUSTOMER_ID, ");
				sb.append("       M.ACCT_NBR, ");
				sb.append("       M.STOCK_CODE, ");
				sb.append("       M.STOCK_NAME, ");
				sb.append("       M.TXN_DATE, ");
				sb.append("       M.UNIT_PRICE, ");
				sb.append("       M.FEE_RATE, ");
				sb.append("       M.TXN_TYPE, ");
				sb.append("       M.TXN_TYPE_DESC, ");
				sb.append("       M.EXTRACT_TYPE, ");
				sb.append("       M.EXTRACT_TYPE_DESC, ");
				sb.append("       M.SETTLE_TYPE, ");
				sb.append("       M.SETTLE_TYPE_DESC, ");
				sb.append("       M.FACE_VALUE, ");
				sb.append("       M.SERVICE_FEE, ");
				sb.append("       M.RECEIVABLES_AMT, ");
				sb.append("       M.EXTRACT_WARRANT_NBR, ");
				sb.append("       M.WITHHOLDING_TAX, ");
				sb.append("       M.SETTLE_AMT, ");
				sb.append("       M.NBR_OF_SHARES, ");
				sb.append("       M.EXTRACT_COST, ");
				sb.append("       M.BENEFIT_AMT, ");
				sb.append("       M.PAY_OVERDUE_TAX_DATE, ");
				sb.append("       M.SELF_DEDUCTION, ");
				sb.append("       M.BROKER_NBR, ");
				sb.append("       M.BROKER_NAME, ");
				sb.append("       M.TDC_ACCT_NBR, ");
				sb.append("       M.STRATEGY, ");
				sb.append("       M.TXN_STATUS, ");
				sb.append("       M.TXN_STATUS_DESC, ");
				sb.append("       M.CONFIRMATION_REPLY_DATE, ");
				sb.append("       M.UPD_DATE, ");
				sb.append("       M.SETTLE_FLAG, ");
				sb.append("       M.SETTLE_STOCK_CODE, ");
				sb.append("       M.SETTLE_STOCK_NAME, ");
				sb.append("       M.SETTLE_NBR_OF_SHARES, ");
				sb.append("       M.CLOSING_PRICE, ");
				sb.append("       M.STRIKE_PRICE, ");
				sb.append("       M.SBL_DATE, ");
				sb.append("       M.SBL_TIME, ");
				sb.append("       M.TAX_AMT, ");
				sb.append("       M.INS_DATE, ");
				sb.append("       M.VERSION, ");
				sb.append("       M.CREATETIME, ");
				sb.append("       M.CREATOR, ");
				sb.append("       M.MODIFIER, ");
				sb.append("       M.LASTUPDATE ");
				sb.append("FROM TBCRM_AST_INV_SEC_DSN_TXN M ");
				sb.append("LEFT JOIN TBPRD_TFB_DOMESTIC_SN DSN ON M.STOCK_CODE = DSN.STOCK_CODE AND TRUNC(DSN.SNAP_DATE) = TRUNC((SELECT MAX(SNAP_DATE) FROM TBPRD_TFB_DOMESTIC_SN)) ");
				appendSecDsnSql(sb);
				sb.append("WHERE 1 = 1 ");
				
				sb.append("AND M.EXTRACT_TYPE_DESC IS NOT NULL ");

				sb.append("AND M.CUSTOMER_ID = :custID ");

				if (null != inputVO.getStartDate()) {   			// 到期交割日-起
					sb.append("AND TO_CHAR(M.SETTLE_DATE, 'yyyyMMdd') >= TO_CHAR(:sDate, 'yyyyMMdd') ");
					queryCondition.setObject("sDate", inputVO.getStartDate());			
				}
				
				if (null != inputVO.getEndDate()) {   				// 到期交割日-迄
					sb.append("AND TO_CHAR(M.SETTLE_DATE, 'yyyyMMdd') <= TO_CHAR(:eDate, 'yyyyMMdd') ");
					queryCondition.setObject("eDate", inputVO.getEndDate());			
				}
				
				if (StringUtils.isNotEmpty(inputVO.getPrdID())) {   // 產品代碼
					sb.append("AND M.STOCK_CODE = :prdID ");
					queryCondition.setObject("prdID", inputVO.getPrdID());			
				}
				
				sb.append("ORDER BY M.EXTRACT_TYPE_DESC DESC ");
				
				queryCondition.setObject("custID", inputVO.getCustID());		

				queryCondition.setQueryString(sb.toString());
				
				outputVO.setMaturityList(dam.exeQuery(queryCondition));
				
				break;
		}
		
		this.sendRtnObject(outputVO);
	}
	
	/* 取得配股配息 前端入口 */
	public void getDivList (Object body, IPrimitiveMap header) throws JBranchException {
		
		CRM870InputVO inputVO = (CRM870InputVO) body;
		CRM870OutputVO outputVO = new CRM870OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		switch (inputVO.getTabSheet()) {
			case 1 : // 全部 
			case 2 : // 海外股票
			case 3 : // 海外債
			case 4 : // 境外結構型商品
				sb.append("SELECT M.DATA_DATE, ");                // 資料日期
				sb.append("       M.ALLOCATE_DATE, ");            // 基準日期
				sb.append("       M.BRANCH_NBR, ");               // 分公司代碼
				sb.append("       M.BRANCH_NAME, ");              // 分公司名稱
				sb.append("       M.CUST_ID, ");                  // 客戶ID(PER)(ID)
				sb.append("       M.ACCT_NBR, ");                 // 客戶帳號(PER)(ACCT)
				sb.append("       M.PRODUCT_CODE, ");             // 產品類別代碼
				sb.append("       M.PRODUCT_CODE_DESC, ");        // 產品類別說明
				sb.append("       M.COUNTRY_CODE, ");             // 國別代號
				sb.append("       M.COUNTRY_NAME, ");             // 國別簡稱
				sb.append("       M.EXCHANGE_CODE, ");            // 交易所代號
				sb.append("       M.EXCHANGE_ABBR_NAME, ");       // 交易所簡稱
				sb.append("       M.UPPER_COMPANY_CODE, ");       // 上手券商代號
				sb.append("       M.UPPER_COMPANY_ABBR_NAME, ");  // 上手券商簡稱
				sb.append("       M.PROD_ID, ");                  // 商品代號
				sb.append("       M.PROD_NAME, ");                // 商品名稱
				sb.append("       M.ISIN_CODE, ");                // ISIN_CODE
				sb.append("       M.TYPE_CODE, ");                // 交易類別代碼
				sb.append("       M.TYPE_CODE_DESC, ");           // 交易類別說明
				sb.append("       M.STOCK_CURRENCY, ");           // 產品計價幣別
				sb.append("       M.DIVIDEND_FREQUENCY, ");       // 配息頻率
				sb.append("       M.QTY, ");                      // 除權股數/庫存面額
				sb.append("       M.DIVIDEND_RATE, ");            // 配息率(%)
				sb.append("       M.DIVIDEND_AMT, ");             // 現金股利/配息金額
				sb.append("       M.TAX_AMT, ");                  // 稅金
				sb.append("       M.ISSUE_FEE, ");                // 發行費
				sb.append("       M.TXN_CURRENCY, ");             // 交割幣別
				sb.append("       M.EXCHG_RATE_D, ");             // 匯率
				sb.append("       M.NET_AMT, ");                  // 應收付金額(交割幣)
				sb.append("       M.BOND_DIVIDEND_ACCU_CNT, ");   // 債券累計配息次數
				sb.append("       M.BOND_DIVIDEND_ACCU_AMT, ");   // 債券累計配息金額
				sb.append("       M.PAID_PAYMENT_AMT, ");         // 有償繳款金額
				sb.append("       M.FREE_ALLOTMENT_QTY, ");       // 無償配股股數
				sb.append("       M.PAID_ALLOTMENT_QTY, ");       // 有償配股股數
				sb.append("       M.STOCK_POSTING_DATE, ");       // 股票入帳日
				sb.append("       M.CASH_POSTING_DATE, ");        // 現金入帳日
				sb.append("       M.TO_BANK_ACCT_NBR, ");         // 入金銀行帳號(PER)(ACCT)
				sb.append("       M.ACCT_FLAG, ");                // 帳號註記(Y為設質帳號)
				sb.append("       M.CUSTOMER_ID_MASK, ");         // 客戶ID_MASK
				sb.append("       M.ACCT_NBR_MASK, ");            // 客戶帳號_MASK
				sb.append("       M.TRANS_SN, ");                 // TWSJobStreamNO
				sb.append("       M.EXEC_DATE, ");                // TWS執行日
				sb.append("       M.VERSION, ");
				sb.append("       M.CREATETIME, ");
				sb.append("       M.CREATOR, ");
				sb.append("       M.MODIFIER, ");
				sb.append("       M.LASTUPDATE, ");
				sb.append("       M.PROD_ID AS STOCK_CODE ");
				
				switch (inputVO.getTabSheet()) {
					case 1 :
						sb.append("FROM ( ");
						sb.append("  SELECT M.* FROM TBCRM_AST_INV_SEC_STOCK_DIV M ");
						appendSecSql(sb);
						sb.append("  UNION ");
						sb.append("  SELECT M.* FROM TBCRM_AST_INV_SEC_BOND_DIV M ");
						appendSecSql(sb);
						sb.append("  UNION ");
						sb.append("  SELECT M.* FROM TBCRM_AST_INV_SEC_SN_DIV M ");
						appendSecSql(sb);
						sb.append("  UNION ");
						sb.append("  SELECT TRUNC(M.CREATETIME) AS DATA_DATE, ");   	
						sb.append("         M.EVALUATE_DATE AS ALLOCATE_DATE, ");		
						sb.append("         NULL AS BRANCH_NBR, ");               	
						sb.append("         NULL AS BRANCH_NAME, ");              	
						sb.append("         M.CUSTOMER_ID AS CUST_ID, ");             
						sb.append("         M.ACCT_NBR, ");                           
						sb.append("         'DSN' AS PRODUCT_CODE, ");              
						sb.append("         '境內結構型商品' AS PRODUCT_CODE_DESC, "); 
						sb.append("         NULL AS COUNTRY_CODE, ");               
						sb.append("         NULL AS COUNTRY_NAME, ");               
						sb.append("         NULL AS EXCHANGE_CODE, ");            	
						sb.append("         NULL AS EXCHANGE_ABBR_NAME, ");       	
						sb.append("         NULL AS UPPER_COMPANY_CODE, ");       	
						sb.append("         NULL AS UPPER_COMPANY_ABBR_NAME, ");  	
						sb.append("         M.STOCK_CODE AS PROD_ID, ");          	
						sb.append("         CAST(M.STOCK_NAME AS NVARCHAR2(300)) AS PROD_NAME, ");
						sb.append("         NULL AS ISIN_CODE, ");                	
						sb.append("         M.STATUS AS TYPE_CODE, ");                
						sb.append("         M.STATUS_DESC AS TYPE_CODE_DESC, ");     	
						sb.append("         'TWD' AS STOCK_CURRENCY, ");           	
						sb.append("         NULL AS DIVIDEND_FREQUENCY, ");       	
						sb.append("         M.FACE_VALUE AS QTY, ");                	
						sb.append("         NULL AS DIVIDEND_RATE, ");            	
						sb.append("         M.DIVIDEND_AMT, ");             			
						sb.append("         NULL AS TAX_AMT, ");                  	
						sb.append("         NULL AS ISSUE_FEE, ");                	
						sb.append("         NULL AS TXN_CURRENCY, ");             	
						sb.append("         NULL AS EXCHG_RATE_D, ");             	
						sb.append("         M.DIVIDEND_AMT AS NET_AMT, ");           	
						sb.append("         NULL AS BOND_DIVIDEND_ACCU_CNT, ");   	
						sb.append("         NULL AS BOND_DIVIDEND_ACCU_AMT, ");   	
						sb.append("         0 AS PAID_PAYMENT_AMT, ");         		
						sb.append("         0 AS FREE_ALLOTMENT_QTY, ");       		
						sb.append("         NULL AS PAID_ALLOTMENT_QTY, ");       	
						sb.append("         NULL AS STOCK_POSTING_DATE, ");       	
						sb.append("         M.DIVIDEND_DATE AS CASH_POSTING_DATE, "); 
						sb.append("         M.BANK_ACCT_NBR AS TO_BANK_ACCT_NBR, ");	
						sb.append("         NULL AS ACCT_FLAG, ");                	
						sb.append("         NULL AS CUSTOMER_ID_MASK, ");         	
						sb.append("         NULL AS ACCT_NBR_MASK, ");            	
						sb.append("         NULL AS TRANS_SN, ");                 	
						sb.append("         NULL AS EXEC_DATE, ");                	
						sb.append("         M.VERSION, ");
						sb.append("         M.CREATETIME, ");
						sb.append("         M.CREATOR, ");
						sb.append("         M.MODIFIER, ");
						sb.append("         M.LASTUPDATE ");
						sb.append("  FROM TBCRM_AST_INV_SEC_DSN_DIV M ");
						appendSecDsnSql(sb);
						sb.append("  WHERE 1 = 1 ");
						sb.append("  AND DIVIDEND_AMT > 0 ");
						sb.append(") M ");
						break;
					case 2 :
						sb.append("FROM TBCRM_AST_INV_SEC_STOCK_DIV M ");
						appendSecSql(sb);
						break;
					case 3 :
						sb.append("FROM TBCRM_AST_INV_SEC_BOND_DIV M ");
						appendSecSql(sb);
						break;
					case 4 :
						sb.append("FROM TBCRM_AST_INV_SEC_SN_DIV M ");
						appendSecSql(sb);
						break;
				}
				
				sb.append("WHERE 1 = 1 ");
				
				sb.append("AND M.CUST_ID = :custID ");
				
				if (null != inputVO.getStartDate()) {   // 現金入帳日-起
					sb.append("AND TO_CHAR(M.CASH_POSTING_DATE, 'yyyyMMdd') >= TO_CHAR(:sDate, 'yyyyMMdd') ");
					queryCondition.setObject("sDate", inputVO.getStartDate());			
				}
				
				if (null != inputVO.getEndDate()) {   	// 現金入帳日-迄
					sb.append("AND TO_CHAR(M.CASH_POSTING_DATE, 'yyyyMMdd') <= TO_CHAR(:eDate, 'yyyyMMdd') ");
					queryCondition.setObject("eDate", inputVO.getEndDate());			
				}
				
				if (StringUtils.isNotEmpty(inputVO.getPrdID())) {   // 產品代碼
					sb.append("AND M.PROD_ID = :prdID ");
					queryCondition.setObject("prdID", inputVO.getPrdID());			
				}
				
				sb.append("ORDER BY M.CASH_POSTING_DATE DESC ");
				
				break;
			case 5 : // 境內結構型商品
				sb.append("SELECT M.CUSTOMER_ID, ");
				sb.append("       M.ACCT_NBR, ");
				sb.append("       M.STOCK_CODE, ");
				sb.append("       M.STOCK_NAME, ");
				sb.append("       M.SEQ_NO, ");
				sb.append("       M.OBSERVE_SDATE, ");
				sb.append("       M.OBSERVE_EDATE, ");
				sb.append("       M.EVALUATE_DATE, ");
				sb.append("       M.OUT_DATE, ");
				sb.append("       M.CONTRACT_RATE, ");
				sb.append("       M.FACE_VALUE, ");
				sb.append("       M.DIVIDEND_AMT, ");
				sb.append("       M.DIVIDEND_DATE, ");
				sb.append("       M.BANK_CODE, ");
				sb.append("       M.BANK_ACCT_NBR, ");
				sb.append("       M.STATUS, ");
				sb.append("       M.STATUS_DESC, ");
				sb.append("       M.CURRENCY, ");
				sb.append("       M.INS_DATE, ");
				sb.append("       M.VERSION, ");
				sb.append("       M.CREATETIME, ");
				sb.append("       M.CREATOR, ");
				sb.append("       M.MODIFIER, ");
				sb.append("       M.LASTUPDATE ");
				sb.append("FROM TBCRM_AST_INV_SEC_DSN_DIV M ");
				appendSecDsnSql(sb);
				sb.append("WHERE 1 = 1 ");
				
				sb.append("AND M.DIVIDEND_AMT > 0 ");
				
				sb.append("AND M.CUSTOMER_ID = :custID ");
				
				if (null != inputVO.getStartDate()) {   // 現金入帳日-起
					sb.append("AND TO_CHAR(M.DIVIDEND_DATE, 'yyyyMMdd') >= TO_CHAR(:sDate, 'yyyyMMdd') ");
					queryCondition.setObject("sDate", inputVO.getStartDate());			
				}
				
				if (null != inputVO.getEndDate()) {   	// 現金入帳日-迄
					sb.append("AND TO_CHAR(M.DIVIDEND_DATE, 'yyyyMMdd') <= TO_CHAR(:eDate, 'yyyyMMdd') ");
					queryCondition.setObject("eDate", inputVO.getEndDate());			
				}
				
				if (StringUtils.isNotEmpty(inputVO.getPrdID())) {   // 產品代碼
					sb.append("AND M.STOCK_CODE = :prdID ");
					queryCondition.setObject("prdID", inputVO.getPrdID());			
				}
				
				sb.append("ORDER BY M.DIVIDEND_DATE DESC ");
				
				break;
		}
		
		queryCondition.setObject("custID", inputVO.getCustID());			

		queryCondition.setQueryString(sb.toString());

		outputVO.setDivList(dam.exeQuery(queryCondition));
		
		this.sendRtnObject(outputVO);
	}
	
	/*
	 * #2326 : 共銷註記 = Y，才顯示。
	 */
	public void appendSecSql(StringBuffer sb) {
		sb.append("INNER JOIN ( ");
		sb.append("SELECT ");
		sb.append("ACCT_NBR, CROSS_SELLING_IND ");
		sb.append("FROM TBCRM_S_TFB_SVIP_ACCT ");
		sb.append("WHERE SNAP_DATE = (SELECT MAX(SNAP_DATE) FROM TBCRM_S_TFB_SVIP_ACCT) ");
		sb.append(") ACCT ");
		sb.append("ON M.ACCT_NBR = ACCT.ACCT_NBR ");
		sb.append("AND ACCT.CROSS_SELLING_IND = 'Y' ");
	}
	
	/*
	 * #2326 : 共銷註記 = Y，才顯示。
	 */
	public void appendSecDsnSql(StringBuffer sb) {
		sb.append("INNER JOIN ( ");
		sb.append("SELECT ");
		sb.append("DSN.CUSTOMER_ID, DSN.SALES_BRANCH_NBR, ACCT.CROSS_SELLING_IND, DSN.ACCT_NBR ");
		sb.append("FROM TBCRM_S_TFB_SVIP_ACCT_DSN DSN ");
		sb.append("     INNER JOIN TBCRM_S_TFB_SVIP_ACCT ACCT ");
		sb.append("     ON DSN.CUSTOMER_ID = ACCT.CUST_ID AND DSN.SALES_BRANCH_NBR = ACCT.BRANCH_NBR ");
		sb.append("     AND ACCT.CROSS_SELLING_IND = 'Y' ");
		sb.append("WHERE ACCT.SNAP_DATE = (SELECT MAX(SNAP_DATE) FROM TBCRM_S_TFB_SVIP_ACCT) ");
		sb.append(") S ");
		sb.append("ON M.CUSTOMER_ID = S.CUSTOMER_ID AND M.ACCT_NBR = S.ACCT_NBR ");
	}
	
	/*
	 * #2326 : 共銷註記 = Y，才顯示。
	 * For 資況表:CRM8502.java 呼叫。
	 */
	public void appendSecSql(StringBuilder sb) {
		sb.append("INNER JOIN ( ");
		sb.append("SELECT ");
		sb.append("ACCT_NBR, CROSS_SELLING_IND ");
		sb.append("FROM TBCRM_S_TFB_SVIP_ACCT ");
		sb.append("WHERE SNAP_DATE = (SELECT MAX(SNAP_DATE) FROM TBCRM_S_TFB_SVIP_ACCT) ");
		sb.append(") ACCT ");
		sb.append("ON M.ACCT_NBR = ACCT.ACCT_NBR ");
		sb.append("AND ACCT.CROSS_SELLING_IND = 'Y' ");
	}
	
	/*
	 * #2326 : 共銷註記 = Y，才顯示。
	 * For 資況表:CRM8502.java 呼叫。
	 */
	public void appendSecDsnSql(StringBuilder sb) {
		sb.append("INNER JOIN ( ");
		sb.append("SELECT ");
		sb.append("DSN.CUSTOMER_ID, DSN.SALES_BRANCH_NBR, ACCT.CROSS_SELLING_IND, DSN.ACCT_NBR ");
		sb.append("FROM TBCRM_S_TFB_SVIP_ACCT_DSN DSN ");
		sb.append("     INNER JOIN TBCRM_S_TFB_SVIP_ACCT ACCT ");
		sb.append("     ON DSN.CUSTOMER_ID = ACCT.CUST_ID AND DSN.SALES_BRANCH_NBR = ACCT.BRANCH_NBR ");
		sb.append("     AND ACCT.CROSS_SELLING_IND = 'Y' ");
		sb.append("WHERE ACCT.SNAP_DATE = (SELECT MAX(SNAP_DATE) FROM TBCRM_S_TFB_SVIP_ACCT) ");
		sb.append(") S ");
		sb.append("ON M.CUSTOMER_ID = S.CUSTOMER_ID AND M.ACCT_NBR = S.ACCT_NBR ");
	}
	
}