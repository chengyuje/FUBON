--**************************************************************************
--PROJECT_CODE> TBCRM
--DATE>2017/09/20
--AUTHOR> Rick Chiang
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBCRM_FA_ACT_CUST_BRN_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
TRUNCATE     
into table TBCRM_FA_ACT_CUST_BRN_SG
FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
CUST_ID,
BRA_NBR,
DATA_YEARMONTH 
)                                               