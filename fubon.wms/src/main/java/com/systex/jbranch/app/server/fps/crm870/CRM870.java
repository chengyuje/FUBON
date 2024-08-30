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
				sb.append("SELECT DATA_DATE, ");
				sb.append("       SNAP_DATE, ");
				sb.append("       BRANCH_NBR, ");
				sb.append("       BRANCH_NAME, ");
				sb.append("       CUST_ID, ");
				sb.append("       ACCT_NBR, ");
				sb.append("       PRODUCT_CODE, ");
				sb.append("       PRODUCT_CODE_DESC, ");
				sb.append("       COUNTRY_CODE, ");
				sb.append("       COUNTRY_NAME, ");
				sb.append("       EXCHANGE_CODE, ");
				sb.append("       EXCHANGE_ABBR_NAME, ");
				sb.append("       UPPER_COMPANY_CODE, ");
				sb.append("       UPPER_COMPANY_ABBR_NAME,");
				sb.append("       PROD_ID, ");
				sb.append("       PROD_NAME, ");
				sb.append("       ISIN_CODE, ");
				sb.append("       CURRENCY, ");
				sb.append("       QTY, ");
				sb.append("       EXCHG_RATE_D, ");
				sb.append("       AVG_COST_PRICE, ");
				sb.append("       INVEST_AMT_FC, ");
				sb.append("       INVEST_AMT_TW, ");
				sb.append("       UNIT_PRICE, ");
				sb.append("       PRICE_DATE, ");
				sb.append("       AUM_FC, ");
				sb.append("       AUM_TW, ");
				sb.append("       DIVIDENT_AMT_FC, ");
				sb.append("       DIVIDENT_AMT_TW, ");
				sb.append("       BENEFIT_AMT1, ");
				sb.append("       BENEFIT_RATE1, ");
				sb.append("       BENEFIT_AMT2, ");
				sb.append("       BENEFIT_RATE2, ");
				sb.append("       BOND_COUPON_RATE, ");
				sb.append("       ISSUE_BROKER_NAME, ");
				sb.append("       MATURITY_DATE, ");
				sb.append("       RANK_LEVEL_MOODYS, ");
				sb.append("       RANK_LEVEL_SP, ");
				sb.append("       RANK_LEVEL_FITCH, ");
				sb.append("       RANK_LEVEL_OTHER, ");
				sb.append("       ACCT_FLAG, ");
				sb.append("       CUSTOMER_ID_MASK, ");
				sb.append("       ACCT_NBR_MASK, ");
				sb.append("       TRANS_SN, ");
				sb.append("       EXEC_DATE, ");
				sb.append("       VERSION, ");
				sb.append("       CREATETIME, ");
				sb.append("       CREATOR, ");
				sb.append("       MODIFIER, ");
				sb.append("       LASTUPDATE, ");
				
				sb.append("       PROD_ID AS STOCK_CODE ");
				
				switch (inputVO.getTabSheet()) {
					case 2 : // 海外股票
						sb.append("FROM TBCRM_AST_INV_SEC_STOCK ");
						break;
					case 3 : // 海外債
						sb.append("FROM TBCRM_AST_INV_SEC_BOND ");
						break;
					case 4 : // 結構型商品
						sb.append("FROM TBCRM_AST_INV_SEC_SN ");
						break;
				}
				
				sb.append("WHERE 1 = 1 ");
				
				switch (inputVO.getTabSheet()) {
					case 2 : // 海外股票
						sb.append("AND DATA_DATE = (SELECT MAX(DATA_DATE) FROM TBCRM_AST_INV_SEC_STOCK) ");
						break;
					case 3 : // 海外債
						sb.append("AND DATA_DATE = (SELECT MAX(DATA_DATE) FROM TBCRM_AST_INV_SEC_BOND) ");
						break;
					case 4 : // 結構型商品
						sb.append("AND DATA_DATE = (SELECT MAX(DATA_DATE) FROM TBCRM_AST_INV_SEC_SN) ");
						break;
				}
				
				sb.append("AND CUST_ID = :custID ");
				
				switch (inputVO.getTabSheet()) {
					case 3 : // 海外債
						sb.append("ORDER BY PROD_NAME ");
						break;
					case 2 : // 海外股票
					case 4 : // 結構型商品
						sb.append("ORDER BY PROD_ID ");
						break;
				}
				
				break;
			case 5 : // 境內結構型商品
				sb.append("SELECT SNAP_DATE, ");
				sb.append("       CUSTOMER_ID, ");
				sb.append("       ACCT_NBR, ");
				sb.append("       STOCK_CODE, ");
				sb.append("       STOCK_NAME, ");
				sb.append("       STRATEGY, ");
				sb.append("       STOCK_TYPE, ");
				sb.append("       CURRENCY, ");
				sb.append("       TXN_DATE, ");
				sb.append("       SETTLE_DATE, ");
				sb.append("       MATURITY_DATE, ");
				sb.append("       DURATION, ");
				sb.append("       TARGET_CODE, ");
				sb.append("       TARGET_NAME, ");
				sb.append("       SUBSTR(TARGET_NAME, 0, 6) AS TARGET_NAME_6, ");
				sb.append("       UNIT_PRICE, ");
				sb.append("       TXN_FACE_VALUE, ");
				sb.append("       REM_FACE_VALUE, ");
				sb.append("       INVEST_COST, ");
				sb.append("       FACE_VALUE, ");
				sb.append("       DIVIDEND_AMT, ");
				sb.append("       REFER_PRICE, ");
				sb.append("       INS_DATE, ");
				sb.append("       AUM_TW ");
				sb.append("FROM TBCRM_AST_INV_SEC_DSN ");
				
				sb.append("WHERE 1 = 1 ");
				
				sb.append("AND SNAP_DATE = (SELECT MAX(SNAP_DATE) FROM TBCRM_AST_INV_SEC_DSN) ");
				sb.append("AND TRUNC(MATURITY_DATE) > TRUNC(SYSDATE) ");
				
				sb.append("AND CUSTOMER_ID = :custID ");
				
				sb.append("ORDER BY STOCK_CODE ");
				
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
				sb.append("SELECT DATA_DATE, ");                   // 資料日期             
				sb.append("       TXN_DATE, ");                    // 成交日期             
				sb.append("       SETTLE_DATE, ");                 // 交割日期             
				sb.append("       BRANCH_NBR, ");                  // 分公司代碼           
				sb.append("       BRANCH_NAME, ");                 // 分公司名稱           
				sb.append("       WARRANT_NBR, ");                 // 委託書編號           
				sb.append("       WARRANT_SEQ_NBR, ");             // 分單號碼             
				sb.append("       PRODUCT_CODE, ");                // 產品類別代碼         
				sb.append("       PRODUCT_CODE_DESC, ");           // 產品類別說明         
				sb.append("       CUST_ID, ");                     // 客戶ID(PER)(ID)      
				sb.append("       ACCT_NBR, ");                    // 客戶帳號(PER)(ACCT)  
				sb.append("       TRADING_TYPE_CODE, ");           // 交易類別代碼         
				sb.append("       TRADING_TYPE_CODE_DESC, ");      // 交易類別說明         
				sb.append("       COUNTRY_CODE, ");                // 國別代號             
				sb.append("       COUNTRY_NAME, ");                // 國別簡稱             
				sb.append("       EXCHANGE_CODE, ");               // 交易所代號           
				sb.append("       EXCHANGE_ABBR_NAME, ");          // 交易所簡稱           
				sb.append("       UPPER_COMPANY_CODE, ");          // 上手券商代號         
				sb.append("       UPPER_COMPANY_ABBR_NAME, ");     // 上手券商簡稱         
				sb.append("       PROD_ID, ");                     // 商品代號             
				sb.append("       PROD_NAME, ");                   // 商品名稱             
				sb.append("       ISIN_CODE, ");                   // ISIN_CODE            
				sb.append("       CURRENCY, ");                    // 幣別                 
				sb.append("       QTY, ");                         // 單位數/面額          
				sb.append("       UNIT_PRICE, ");                  // 成交價格             
				sb.append("       BOND_UPPER_PRICE, ");            // 債券上手價格         
				sb.append("       EXCHG_RATE_D, ");                // 匯率                 
				sb.append("       BOND_PRE_INTEREST, ");           // 債券前手息(原幣)     
				sb.append("       SETTLE_AMT_FC, ");               // 交割金額(原幣)       
				sb.append("       TXN_AMT_FC, ");                  // 交易量(原幣)         
				sb.append("       TXN_AMT_TW, ");                  // 交易量(台幣)         
				sb.append("       SERVICE_FEE_FC, ");              // 手續費收益(原幣)     
				sb.append("       SERVICE_FEE_TW, ");              // 手續費收益(台幣)     
				sb.append("       TXN_COST_RATE1, ");              // 交易成本費率1        
				sb.append("       TXN_COST_RATE2, ");              // 交易成本費率2        
				sb.append("       TXN_COST1, ");                   // 交易成本1(台幣)      
				sb.append("       TXN_COST2, ");                   // 交易成本2(台幣)      
				sb.append("       NET_FEE, ");                     // 扣成本後淨收入(台幣) 
				sb.append("       ACCT_FLAG, ");                   // 帳號註記(Y為設質帳號)
				sb.append("       CUSTOMER_ID_MASK, ");            // 客戶ID_MASK          
				sb.append("       ACCT_NBR_MASK, ");               // 客戶帳號_MASK        
				sb.append("       TRANS_SN, ");                    // TWSJobStreamNO       
				sb.append("       EXEC_DATE, ");                   // TWS執行日            
				sb.append("       VERSION, ");                     
				sb.append("       CREATETIME, ");
				sb.append("       CREATOR, ");
				sb.append("       MODIFIER, ");
				sb.append("       LASTUPDATE, ");
				
				sb.append("       PROD_ID AS STOCK_CODE ");
								
				switch (inputVO.getTabSheet()) {
					case 1 :
						sb.append("FROM ( ");
						sb.append("  SELECT * FROM TBCRM_AST_INV_SEC_STOCK_TXN ");
						sb.append("  UNION ");
						sb.append("  SELECT * FROM TBCRM_AST_INV_SEC_BOND_TXN ");
						sb.append("  UNION ");
						sb.append("  SELECT * FROM TBCRM_AST_INV_SEC_SN_TXN ");
						sb.append("  UNION ");
						sb.append("  SELECT TRUNC(CREATETIME) AS DATA_DATE, ");          	
						sb.append("         TXN_DATE, ");                                	
						sb.append("         SETTLE_DATE, ");                             	
						sb.append("         NULL AS BRANCH_NBR, ");                      	
						sb.append("         NULL AS BRANCH_NAME, ");                     	
						sb.append("         TXN_WARRANT_NBR AS WARRANT_NBR, ");          	
						sb.append("         NULL AS WARRANT_SEQ_NBR, ");                 	
						sb.append("         'DSN' AS PRODUCT_CODE, ");                   	
						sb.append("         '境內結構型商品' AS PRODUCT_CODE_DESC, ");		
						sb.append("         CUSTOMER_ID AS CUST_ID, ");                  	
						sb.append("         ACCT_NBR, ");                                	
						sb.append("         TXN_TYPE AS TRADING_TYPE_CODE, ");           	
						sb.append("         TXN_TYPE_DESC AS TRADING_TYPE_CODE_DESC, "); 	
						sb.append("         NULL AS COUNTRY_CODE, ");                    	
						sb.append("         NULL AS COUNTRY_NAME, ");                    	
						sb.append("         NULL AS EXCHANGE_CODE, ");                   	
						sb.append("         NULL AS EXCHANGE_ABBR_NAME, ");              	
						sb.append("         NULL AS UPPER_COMPANY_CODE, ");              	
						sb.append("         NULL AS UPPER_COMPANY_ABBR_NAME, ");         	
						sb.append("         STOCK_CODE AS PROD_ID, ");                   	
						sb.append("         CAST(STOCK_NAME AS NVARCHAR2(300)) AS PROD_NAME, ");
						sb.append("         NULL AS ISIN_CODE, ");                  
						sb.append("         'TWD' AS CURRENCY, ");                  
						sb.append("         FACE_VALUE AS QTY, ");                  
						sb.append("         UNIT_PRICE, ");                         
						sb.append("         NULL AS TEMP, ");
						sb.append("         NULL AS EXCHG_RATE_D, ");               
						sb.append("         0 AS BOND_PRE_INTEREST, ");             
						sb.append("         SETTLE_AMT AS SETTLE_AMT_FC, ");        
						sb.append("         RECEIVABLES_AMT AS TXN_AMT_FC, ");                 
						sb.append("         NULL AS TXN_AMT_TW, ");                 
						sb.append("         NULL AS SERVICE_FEE_FC, ");             
						sb.append("         SERVICE_FEE AS SERVICE_FEE_TW, ");      
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
						sb.append("         VERSION, ");                     
						sb.append("         CREATETIME, ");
						sb.append("         CREATOR, ");
						sb.append("         MODIFIER, ");
						sb.append("         LASTUPDATE ");
						sb.append("  FROM TBCRM_AST_INV_SEC_DSN_TXN ");
						sb.append("  WHERE 1 = 1 ");
						sb.append("  AND EXTRACT_TYPE_DESC IS NULL ");
						sb.append(") ");
						break;
					case 2 :
						sb.append("FROM TBCRM_AST_INV_SEC_STOCK_TXN ");
						break;
					case 3 :
						sb.append("FROM TBCRM_AST_INV_SEC_BOND_TXN ");
						break;
					case 4 :
						sb.append("FROM TBCRM_AST_INV_SEC_SN_TXN ");
						break;
				}
				
				sb.append("WHERE 1 = 1 ");
				
				sb.append("AND CUST_ID = :custID ");		
									
				switch (inputVO.getRptType()) {
					case "DT_SEARCH": // 成交日期-起 ~ 成交日期-迄
						sb.append("AND TO_CHAR(TXN_DATE, 'yyyyMMdd') >= TO_CHAR(:sDate, 'yyyyMMdd') ");
						sb.append("AND TO_CHAR(TXN_DATE, 'yyyyMMdd') <= TO_CHAR(:eDate, 'yyyyMMdd') ");
						queryCondition.setObject("sDate", inputVO.getStartDate());			
						queryCondition.setObject("eDate", inputVO.getEndDate());	
						
						break;
					case "PRD_SEARCH":
						sb.append("AND PROD_ID = :prdID ");
						queryCondition.setObject("prdID", inputVO.getPrdID());			
						break;
				}
				
				sb.append("ORDER BY TXN_DATE DESC ");
				
				break;
			case 5 : // 境內結構型商品
				sb.append("SELECT DSN.SNAP_DATE, ");
				sb.append("       DSN.MATURITY_DATE, ");
				sb.append("       DSN.SETTLE_DATE, ");
				sb.append("       TXN.TXN_WARRANT_NBR, ");
				sb.append("       TXN.CUSTOMER_ID, ");
				sb.append("       TXN.ACCT_NBR, ");
				sb.append("       TXN.STOCK_CODE, ");
				sb.append("       TXN.STOCK_NAME, ");
				sb.append("       TXN.TXN_DATE, ");
				sb.append("       TXN.UNIT_PRICE, ");
				sb.append("       TXN.FEE_RATE, ");
				sb.append("       TXN.TXN_TYPE, ");
				sb.append("       TXN.TXN_TYPE_DESC, ");
				sb.append("       TXN.EXTRACT_TYPE, ");
				sb.append("       TXN.EXTRACT_TYPE_DESC, ");
				sb.append("       TXN.SETTLE_TYPE, ");
				sb.append("       TXN.SETTLE_TYPE_DESC, ");
				sb.append("       TXN.FACE_VALUE, ");
				sb.append("       TXN.SERVICE_FEE, ");
				sb.append("       TXN.RECEIVABLES_AMT, ");
				sb.append("       TXN.EXTRACT_WARRANT_NBR, ");
				sb.append("       TXN.WITHHOLDING_TAX, ");
				sb.append("       TXN.SETTLE_AMT, ");
				sb.append("       TXN.NBR_OF_SHARES, ");
				sb.append("       TXN.EXTRACT_COST, ");
				sb.append("       TXN.BENEFIT_AMT, ");
				sb.append("       TXN.PAY_OVERDUE_TAX_DATE, ");
				sb.append("       TXN.SELF_DEDUCTION, ");
				sb.append("       TXN.BROKER_NBR, ");
				sb.append("       TXN.BROKER_NAME, ");
				sb.append("       TXN.TDC_ACCT_NBR, ");
				sb.append("       TXN.STRATEGY, ");
				sb.append("       TXN.TXN_STATUS, ");
				sb.append("       TXN.TXN_STATUS_DESC, ");
				sb.append("       TXN.CONFIRMATION_REPLY_DATE, ");
				sb.append("       TXN.UPD_DATE, ");
				sb.append("       TXN.SETTLE_FLAG, ");
				sb.append("       TXN.SETTLE_STOCK_CODE, ");
				sb.append("       TXN.SETTLE_STOCK_NAME, ");
				sb.append("       TXN.SETTLE_NBR_OF_SHARES, ");
				sb.append("       TXN.CLOSING_PRICE, ");
				sb.append("       TXN.STRIKE_PRICE, ");
				sb.append("       TXN.SBL_DATE, ");
				sb.append("       TXN.SBL_TIME, ");
				sb.append("       TXN.TAX_AMT, ");
				sb.append("       TXN.INS_DATE, ");
				sb.append("       TXN.VERSION, ");
				sb.append("       TXN.CREATETIME, ");
				sb.append("       TXN.CREATOR, ");
				sb.append("       TXN.MODIFIER, ");
				sb.append("       TXN.LASTUPDATE ");
				sb.append("FROM TBCRM_AST_INV_SEC_DSN_TXN TXN ");
				sb.append("LEFT JOIN TBPRD_TFB_DOMESTIC_SN DSN ON TXN.STOCK_CODE = DSN.STOCK_CODE AND TRUNC(DSN.SNAP_DATE) = TRUNC((SELECT MAX(SNAP_DATE) FROM TBPRD_TFB_DOMESTIC_SN)) ");
				sb.append("WHERE 1 = 1 ");
				
				sb.append("AND TXN.EXTRACT_TYPE_DESC IS NULL ");

				sb.append("AND TXN.CUSTOMER_ID = :custID ");

				switch (inputVO.getRptType()) {
					case "DT_SEARCH": // 成交日期-起 ~ 成交日期-迄
						sb.append("AND TO_CHAR(TXN.TXN_DATE, 'yyyyMMdd') >= TO_CHAR(:sDate, 'yyyyMMdd') ");
						sb.append("AND TO_CHAR(TXN.TXN_DATE, 'yyyyMMdd') <= TO_CHAR(:eDate, 'yyyyMMdd') ");
						queryCondition.setObject("sDate", inputVO.getStartDate());			
						queryCondition.setObject("eDate", inputVO.getEndDate());	
						
						break;
					case "PRD_SEARCH":
						sb.append("AND TXN.STOCK_CODE = :prdID ");
						queryCondition.setObject("prdID", inputVO.getPrdID());			
						break;
				}
				
				sb.append("ORDER BY TXN.TXN_DATE DESC ");
				
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
				sb.append("       TXN.TXN_WARRANT_NBR, ");
				sb.append("       TXN.CUSTOMER_ID, ");
				sb.append("       TXN.ACCT_NBR, ");
				sb.append("       TXN.STOCK_CODE, ");
				sb.append("       TXN.STOCK_NAME, ");
				sb.append("       TXN.TXN_DATE, ");
				sb.append("       TXN.UNIT_PRICE, ");
				sb.append("       TXN.FEE_RATE, ");
				sb.append("       TXN.TXN_TYPE, ");
				sb.append("       TXN.TXN_TYPE_DESC, ");
				sb.append("       TXN.EXTRACT_TYPE, ");
				sb.append("       TXN.EXTRACT_TYPE_DESC, ");
				sb.append("       TXN.SETTLE_TYPE, ");
				sb.append("       TXN.SETTLE_TYPE_DESC, ");
				sb.append("       TXN.FACE_VALUE, ");
				sb.append("       TXN.SERVICE_FEE, ");
				sb.append("       TXN.RECEIVABLES_AMT, ");
				sb.append("       TXN.EXTRACT_WARRANT_NBR, ");
				sb.append("       TXN.WITHHOLDING_TAX, ");
				sb.append("       TXN.SETTLE_AMT, ");
				sb.append("       TXN.NBR_OF_SHARES, ");
				sb.append("       TXN.EXTRACT_COST, ");
				sb.append("       TXN.BENEFIT_AMT, ");
				sb.append("       TXN.PAY_OVERDUE_TAX_DATE, ");
				sb.append("       TXN.SELF_DEDUCTION, ");
				sb.append("       TXN.BROKER_NBR, ");
				sb.append("       TXN.BROKER_NAME, ");
				sb.append("       TXN.TDC_ACCT_NBR, ");
				sb.append("       TXN.STRATEGY, ");
				sb.append("       TXN.TXN_STATUS, ");
				sb.append("       TXN.TXN_STATUS_DESC, ");
				sb.append("       TXN.CONFIRMATION_REPLY_DATE, ");
				sb.append("       TXN.UPD_DATE, ");
				sb.append("       TXN.SETTLE_FLAG, ");
				sb.append("       TXN.SETTLE_STOCK_CODE, ");
				sb.append("       TXN.SETTLE_STOCK_NAME, ");
				sb.append("       TXN.SETTLE_NBR_OF_SHARES, ");
				sb.append("       TXN.CLOSING_PRICE, ");
				sb.append("       TXN.STRIKE_PRICE, ");
				sb.append("       TXN.SBL_DATE, ");
				sb.append("       TXN.SBL_TIME, ");
				sb.append("       TXN.TAX_AMT, ");
				sb.append("       TXN.INS_DATE, ");
				sb.append("       TXN.VERSION, ");
				sb.append("       TXN.CREATETIME, ");
				sb.append("       TXN.CREATOR, ");
				sb.append("       TXN.MODIFIER, ");
				sb.append("       TXN.LASTUPDATE ");
				sb.append("FROM TBCRM_AST_INV_SEC_DSN_TXN TXN ");
				sb.append("LEFT JOIN TBPRD_TFB_DOMESTIC_SN DSN ON TXN.STOCK_CODE = DSN.STOCK_CODE AND TRUNC(DSN.SNAP_DATE) = TRUNC((SELECT MAX(SNAP_DATE) FROM TBPRD_TFB_DOMESTIC_SN)) ");
				sb.append("WHERE 1 = 1 ");
				
				sb.append("AND TXN.EXTRACT_TYPE_DESC IS NOT NULL ");

				sb.append("AND TXN.CUSTOMER_ID = :custID ");

				if (null != inputVO.getStartDate()) {   			// 到期交割日-起
					sb.append("AND TO_CHAR(TXN.SETTLE_DATE, 'yyyyMMdd') >= TO_CHAR(:sDate, 'yyyyMMdd') ");
					queryCondition.setObject("sDate", inputVO.getStartDate());			
				}
				
				if (null != inputVO.getEndDate()) {   				// 到期交割日-迄
					sb.append("AND TO_CHAR(TXN.SETTLE_DATE, 'yyyyMMdd') <= TO_CHAR(:eDate, 'yyyyMMdd') ");
					queryCondition.setObject("eDate", inputVO.getEndDate());			
				}
				
				if (StringUtils.isNotEmpty(inputVO.getPrdID())) {   // 產品代碼
					sb.append("AND TXN.STOCK_CODE = :prdID ");
					queryCondition.setObject("prdID", inputVO.getPrdID());			
				}
				
				sb.append("ORDER BY TXN.EXTRACT_TYPE_DESC DESC ");
				
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
				sb.append("SELECT DATA_DATE, ");                // 資料日期
				sb.append("       ALLOCATE_DATE, ");            // 基準日期
				sb.append("       BRANCH_NBR, ");               // 分公司代碼
				sb.append("       BRANCH_NAME, ");              // 分公司名稱
				sb.append("       CUST_ID, ");                  // 客戶ID(PER)(ID)
				sb.append("       ACCT_NBR, ");                 // 客戶帳號(PER)(ACCT)
				sb.append("       PRODUCT_CODE, ");             // 產品類別代碼
				sb.append("       PRODUCT_CODE_DESC, ");        // 產品類別說明
				sb.append("       COUNTRY_CODE, ");             // 國別代號
				sb.append("       COUNTRY_NAME, ");             // 國別簡稱
				sb.append("       EXCHANGE_CODE, ");            // 交易所代號
				sb.append("       EXCHANGE_ABBR_NAME, ");       // 交易所簡稱
				sb.append("       UPPER_COMPANY_CODE, ");       // 上手券商代號
				sb.append("       UPPER_COMPANY_ABBR_NAME, ");  // 上手券商簡稱
				sb.append("       PROD_ID, ");                  // 商品代號
				sb.append("       PROD_NAME, ");                // 商品名稱
				sb.append("       ISIN_CODE, ");                // ISIN_CODE
				sb.append("       TYPE_CODE, ");                // 交易類別代碼
				sb.append("       TYPE_CODE_DESC, ");           // 交易類別說明
				sb.append("       STOCK_CURRENCY, ");           // 產品計價幣別
				sb.append("       DIVIDEND_FREQUENCY, ");       // 配息頻率
				sb.append("       QTY, ");                      // 除權股數/庫存面額
				sb.append("       DIVIDEND_RATE, ");            // 配息率(%)
				sb.append("       DIVIDEND_AMT, ");             // 現金股利/配息金額
				sb.append("       TAX_AMT, ");                  // 稅金
				sb.append("       ISSUE_FEE, ");                // 發行費
				sb.append("       TXN_CURRENCY, ");             // 交割幣別
				sb.append("       EXCHG_RATE_D, ");             // 匯率
				sb.append("       NET_AMT, ");                  // 應收付金額(交割幣)
				sb.append("       BOND_DIVIDEND_ACCU_CNT, ");   // 債券累計配息次數
				sb.append("       BOND_DIVIDEND_ACCU_AMT, ");   // 債券累計配息金額
				sb.append("       PAID_PAYMENT_AMT, ");         // 有償繳款金額
				sb.append("       FREE_ALLOTMENT_QTY, ");       // 無償配股股數
				sb.append("       PAID_ALLOTMENT_QTY, ");       // 有償配股股數
				sb.append("       STOCK_POSTING_DATE, ");       // 股票入帳日
				sb.append("       CASH_POSTING_DATE, ");        // 現金入帳日
				sb.append("       TO_BANK_ACCT_NBR, ");         // 入金銀行帳號(PER)(ACCT)
				sb.append("       ACCT_FLAG, ");                // 帳號註記(Y為設質帳號)
				sb.append("       CUSTOMER_ID_MASK, ");         // 客戶ID_MASK
				sb.append("       ACCT_NBR_MASK, ");            // 客戶帳號_MASK
				sb.append("       TRANS_SN, ");                 // TWSJobStreamNO
				sb.append("       EXEC_DATE, ");                // TWS執行日
				sb.append("       VERSION, ");
				sb.append("       CREATETIME, ");
				sb.append("       CREATOR, ");
				sb.append("       MODIFIER, ");
				sb.append("       LASTUPDATE, ");
				
				sb.append("       PROD_ID AS STOCK_CODE ");
				
				switch (inputVO.getTabSheet()) {
					case 1 :
						sb.append("FROM ( ");
						sb.append("  SELECT * FROM TBCRM_AST_INV_SEC_STOCK_DIV ");
						sb.append("  UNION ");
						sb.append("  SELECT * FROM TBCRM_AST_INV_SEC_BOND_DIV ");
						sb.append("  UNION ");
						sb.append("  SELECT * FROM TBCRM_AST_INV_SEC_SN_DIV ");
						sb.append("  UNION ");
						sb.append("  SELECT TRUNC(CREATETIME) AS DATA_DATE, ");   	
						sb.append("         EVALUATE_DATE AS ALLOCATE_DATE, ");		
						sb.append("         NULL AS BRANCH_NBR, ");               	
						sb.append("         NULL AS BRANCH_NAME, ");              	
						sb.append("         CUSTOMER_ID AS CUST_ID, ");             
						sb.append("         ACCT_NBR, ");                           
						sb.append("         'DSN' AS PRODUCT_CODE, ");              
						sb.append("         '境內結構型商品' AS PRODUCT_CODE_DESC, "); 
						sb.append("         NULL AS COUNTRY_CODE, ");               
						sb.append("         NULL AS COUNTRY_NAME, ");               
						sb.append("         NULL AS EXCHANGE_CODE, ");            	
						sb.append("         NULL AS EXCHANGE_ABBR_NAME, ");       	
						sb.append("         NULL AS UPPER_COMPANY_CODE, ");       	
						sb.append("         NULL AS UPPER_COMPANY_ABBR_NAME, ");  	
						sb.append("         STOCK_CODE AS PROD_ID, ");          	
						sb.append("         CAST(STOCK_NAME AS NVARCHAR2(300)) AS PROD_NAME, ");
						sb.append("         NULL AS ISIN_CODE, ");                	
						sb.append("         STATUS AS TYPE_CODE, ");                
						sb.append("         STATUS_DESC AS TYPE_CODE_DESC, ");     	
						sb.append("         'TWD' AS STOCK_CURRENCY, ");           	
						sb.append("         NULL AS DIVIDEND_FREQUENCY, ");       	
						sb.append("         FACE_VALUE AS QTY, ");                	
						sb.append("         NULL AS DIVIDEND_RATE, ");            	
						sb.append("         DIVIDEND_AMT, ");             			
						sb.append("         NULL AS TAX_AMT, ");                  	
						sb.append("         NULL AS ISSUE_FEE, ");                	
						sb.append("         NULL AS TXN_CURRENCY, ");             	
						sb.append("         NULL AS EXCHG_RATE_D, ");             	
						sb.append("         DIVIDEND_AMT AS NET_AMT, ");           	
						sb.append("         NULL AS BOND_DIVIDEND_ACCU_CNT, ");   	
						sb.append("         NULL AS BOND_DIVIDEND_ACCU_AMT, ");   	
						sb.append("         0 AS PAID_PAYMENT_AMT, ");         		
						sb.append("         0 AS FREE_ALLOTMENT_QTY, ");       		
						sb.append("         NULL AS PAID_ALLOTMENT_QTY, ");       	
						sb.append("         NULL AS STOCK_POSTING_DATE, ");       	
						sb.append("         DIVIDEND_DATE AS CASH_POSTING_DATE, "); 
						sb.append("         BANK_ACCT_NBR AS TO_BANK_ACCT_NBR, ");	
						sb.append("         NULL AS ACCT_FLAG, ");                	
						sb.append("         NULL AS CUSTOMER_ID_MASK, ");         	
						sb.append("         NULL AS ACCT_NBR_MASK, ");            	
						sb.append("         NULL AS TRANS_SN, ");                 	
						sb.append("         NULL AS EXEC_DATE, ");                	
						sb.append("         VERSION, ");
						sb.append("         CREATETIME, ");
						sb.append("         CREATOR, ");
						sb.append("         MODIFIER, ");
						sb.append("         LASTUPDATE ");
						sb.append("  FROM TBCRM_AST_INV_SEC_DSN_DIV ");
						sb.append("  WHERE 1 = 1 ");
						sb.append("  AND DIVIDEND_AMT > 0 ");
						sb.append(") ");
						break;
					case 2 :
						sb.append("FROM TBCRM_AST_INV_SEC_STOCK_DIV ");
						break;
					case 3 :
						sb.append("FROM TBCRM_AST_INV_SEC_BOND_DIV ");
						break;
					case 4 :
						sb.append("FROM TBCRM_AST_INV_SEC_SN_DIV ");
						break;
				}
				
				sb.append("WHERE 1 = 1 ");
				
				sb.append("AND CUST_ID = :custID ");
				
				if (null != inputVO.getStartDate()) {   // 現金入帳日-起
					sb.append("AND TO_CHAR(CASH_POSTING_DATE, 'yyyyMMdd') >= TO_CHAR(:sDate, 'yyyyMMdd') ");
					queryCondition.setObject("sDate", inputVO.getStartDate());			
				}
				
				if (null != inputVO.getEndDate()) {   	// 現金入帳日-迄
					sb.append("AND TO_CHAR(CASH_POSTING_DATE, 'yyyyMMdd') <= TO_CHAR(:eDate, 'yyyyMMdd') ");
					queryCondition.setObject("eDate", inputVO.getEndDate());			
				}
				
				if (StringUtils.isNotEmpty(inputVO.getPrdID())) {   // 產品代碼
					sb.append("AND PROD_ID = :prdID ");
					queryCondition.setObject("prdID", inputVO.getPrdID());			
				}
				
				sb.append("ORDER BY CASH_POSTING_DATE DESC ");
				
				break;
			case 5 : // 境內結構型商品
				sb.append("SELECT CUSTOMER_ID, ");
				sb.append("       ACCT_NBR, ");
				sb.append("       STOCK_CODE, ");
				sb.append("       STOCK_NAME, ");
				sb.append("       SEQ_NO, ");
				sb.append("       OBSERVE_SDATE, ");
				sb.append("       OBSERVE_EDATE, ");
				sb.append("       EVALUATE_DATE, ");
				sb.append("       OUT_DATE, ");
				sb.append("       CONTRACT_RATE, ");
				sb.append("       FACE_VALUE, ");
				sb.append("       DIVIDEND_AMT, ");
				sb.append("       DIVIDEND_DATE, ");
				sb.append("       BANK_CODE, ");
				sb.append("       BANK_ACCT_NBR, ");
				sb.append("       STATUS, ");
				sb.append("       STATUS_DESC, ");
				sb.append("       CURRENCY, ");
				sb.append("       INS_DATE, ");
				sb.append("       VERSION, ");
				sb.append("       CREATETIME, ");
				sb.append("       CREATOR, ");
				sb.append("       MODIFIER, ");
				sb.append("       LASTUPDATE ");
				sb.append("FROM TBCRM_AST_INV_SEC_DSN_DIV ");
				sb.append("WHERE 1 = 1 ");
				
				sb.append("AND DIVIDEND_AMT > 0 ");
				
				sb.append("AND CUSTOMER_ID = :custID ");
				
				if (null != inputVO.getStartDate()) {   // 現金入帳日-起
					sb.append("AND TO_CHAR(DIVIDEND_DATE, 'yyyyMMdd') >= TO_CHAR(:sDate, 'yyyyMMdd') ");
					queryCondition.setObject("sDate", inputVO.getStartDate());			
				}
				
				if (null != inputVO.getEndDate()) {   	// 現金入帳日-迄
					sb.append("AND TO_CHAR(DIVIDEND_DATE, 'yyyyMMdd') <= TO_CHAR(:eDate, 'yyyyMMdd') ");
					queryCondition.setObject("eDate", inputVO.getEndDate());			
				}
				
				if (StringUtils.isNotEmpty(inputVO.getPrdID())) {   // 產品代碼
					sb.append("AND STOCK_CODE = :prdID ");
					queryCondition.setObject("prdID", inputVO.getPrdID());			
				}
				
				sb.append("ORDER BY DIVIDEND_DATE DESC ");
				
				break;
		}
		
		queryCondition.setObject("custID", inputVO.getCustID());			

		queryCondition.setQueryString(sb.toString());

		outputVO.setDivList(dam.exeQuery(queryCondition));
		
		this.sendRtnObject(outputVO);
	}
}