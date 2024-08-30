--**************************************************************************
--PROJECT_CODE> TBCRM
--DATE>2016/10/20
--AUTHOR> Grace Liu
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBCRM_AST_INV_GOLD_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
TRUNCATE       
into table TBCRM_AST_INV_GOLD_SG
FIELDS TERMINATED BY ',' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
( 
DATA_DATE "TO_DATE(:DATA_DATE,'YYYY-MM-DD HH24:MI:ss')"	
,ACC_NBR
,BRA_NBR
,CUST_ID				
,STOCK_UNIT_G			
,STOCK_UNIT_O
,OPEN_DATE "TO_DATE(:OPEN_DATE,'YYYY-MM-DD HH24:MI:ss')"
,REF_PRICE_TWD		
,REF_PRICE_USD		
,REF_PRICE_TIME	"TO_DATE(:REF_PRICE_TIME,'YYYY-MM-DD HH24:MI:ss')"					
,REF_REAL_VALUE_TWD	
,REF_REAL_VALUE_USD				
,AVG_BUYIN_COST_TWD	
,AVG_BUYIN_COST_USD			
,REF_PL_TWD			
,REF_PL_USD			
,DED_DATE_TWD		
,DED_DATE_USD		
,PER_INV_AMT_TWD 	
,PER_INV_AMT_USD 	
,STATUS_TWD			
,STATUS_USD			
,VERSION "0"                                                                           
,CREATETIME sysdate                                                                    
,CREATOR constant "TBCRM_AST_INV_GOLD_SG"                                                
)                                                                                      
                                                                                       