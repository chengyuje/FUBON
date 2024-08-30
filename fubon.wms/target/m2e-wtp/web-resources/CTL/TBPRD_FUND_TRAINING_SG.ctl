--**************************************************************************
--PROJECT_CODE> TBPRD
--DATE>2016/10/04
--AUTHOR> Grace Liu
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBPRD_FUND_TRAINING_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
TRUNCATE
into table	TBPRD_FUND_TRAINING_SG
--FIELDS TERMINATED BY X'09'
FIELDS TERMINATED BY ',' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
CUST_ID   
,TRAGET_ID   
,REG_DATE "TO_DATE(:REG_DATE,'YYYY/MM/DD')"
,VERSION "0"
,CREATETIME sysdate
,CREATOR constant "TBPRD_FUND_TRAINING_SG"
)