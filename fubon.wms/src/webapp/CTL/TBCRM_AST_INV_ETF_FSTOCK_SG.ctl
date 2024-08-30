--**************************************************************************
--PROJECT_CODE> TBCRM
--DATE>2018/01/24
--AUTHOR> Grace Liu
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBCRM_AST_INV_ETF_FSTOCK_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
TRUNCATE 
into table TBCRM_AST_INV_ETF_FSTOCK_SG
FIELDS TERMINATED BY ',' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
( 
INVEST_PROD_TYPE
,BRANCH_NBR
,AO_CODE
,NAME
,PERSON_ID
,CERTIFICATE_NBR
,PRODUCT_ID
,PRODUCT_NAME
,INVEST_TYPE
,DATA_DATE "TO_DATE(:DATA_DATE,'YYYY-MM-DD')"
,FUND_CURRENCY
,FUND_AMT
,FUND_AMT_TWD
,T_AMT
,DEL_AMT
,T_AMT_TWD
,TRAN_TYPE
,EXCHANGE_RATE
,VERSION "0"
,CREATOR constant "TBCRM_AST_INV_ETF_FSTOCK_SG"
,CREATETIME sysdate
,MODIFIER constant "TBCRM_AST_INV_ETF_FSTOCK_SG"
,LASTUPDATE sysdate                                               
)                                                                                      
                                                                                       