--**************************************************************************
--PROJECT_CODE> TBCRM
--DATE>2017/01/18
--AUTHOR> Grace Liu
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBCRM_CUST_PROFEE_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
TRUNCATE       
into table TBCRM_CUST_PROFEE_SG
FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
( 
YEARMONTH,
PROD_SOURCE,
BRANCH_NBR,
PERSON_ID,
--AO_CODE,
PROD_CD,
TYPE_1,
TYPE_2,
TYPE_3,
TYPE_4,
TXN_CNT,
TXN_AMT,
TXN_FEE,
REAL_PROFIT,
PARTY_CLASS,
--SERVICE_DEGREE,
VERSION "0",
CREATOR constant "TBCRM_CUST_PROFEE_SG",
CREATETIME sysdate                                                                    
)                                                                                      
                                                                                       