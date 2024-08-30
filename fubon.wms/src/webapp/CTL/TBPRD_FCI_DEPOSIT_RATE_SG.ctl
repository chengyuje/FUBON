--**************************************************************************
--PROJECT_CODE> TBPRD
--DATE>2023/08/21
--AUTHOR> Cathy Tang
--PURPOSE>SI FCI Project
--**************************************************************************

--**************************************************************************
--TARGET>TBPRD_FCI_DEPOSIT_RATE_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
TRUNCATE
into table TBPRD_FCI_DEPOSIT_RATE_SG
--FIELDS TERMINATED BY X'09'
FIELDS TERMINATED BY ',' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
CURR_ID
,DATA_DATE "TO_DATE(:DATA_DATE,'YYYY/MM/DD')"
,MON_RATE_1 
,MON_RATE_2 
,MON_RATE_3
,MON_RATE_4
,MON_RATE_5 
,MON_RATE_6
,MON_RATE_7
,MON_RATE_8 
,MON_RATE_9
,MON_RATE_10
,MON_RATE_11
,MON_RATE_12
,VERSION "0"       
,CREATETIME SYSDATE       
,CREATOR constant "TBPRD_FCI_DEPOSIT_RATE_SG"       
)
       