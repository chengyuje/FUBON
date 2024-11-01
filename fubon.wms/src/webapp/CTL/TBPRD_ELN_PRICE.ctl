--**************************************************************************
--PROJECT_CODE> TBELN
--DATE>2024/09/27
--AUTHOR> Carley Chen
--PURPOSE>ELN
--**************************************************************************

--**************************************************************************
--TARGET>TBPRD_ELN_PRICE
--TRUNCATE_BEFORE_INSERT>N
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true,skip=0)
LOAD DATA
--TRUNCATE
into table TBPRD_ELN_PRICE
--FIELDS TERMINATED BY X'09'
FIELDS TERMINATED BY ',' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
QUERY_ID
,QUERY_SEQ
,QUERY_BRANCH
,QUERY_EMP
,QUERY_DATE "TO_DATE(SUBSTR(:QUERY_DATE, 1, 16), 'YYYY/MM/DD HH24.MI.SS')"
,PRODUCT
,TENOR
,CURRENCY
,BBG_CODE1
,BBG_CODE2
,BBG_CODE3
,BBG_CODE4
,BBG_CODE5
,COUPON
,STRIKE
,KO_BARRIER
,KO_TYPE
,KI_BARRIER
,KI_TYPE
,UP_FRONT
,QUERY_TYPE
,REPRY_ANSWER
,REPRYCTP_CODE
,REPLY_TIME "TO_DATE(SUBSTR(:REPLY_TIME, 1, 19), 'YYYY-MM-DD HH24.MI.SS')"
,STATUS
,VERSION "0"       
,CREATETIME SYSDATE       
,CREATOR constant "TBPRD_ELN_PRICE.ctl"       
)
       