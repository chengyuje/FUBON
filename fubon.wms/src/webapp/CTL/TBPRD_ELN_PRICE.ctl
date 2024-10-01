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
OPTIONS (direct=true,skip=1)
LOAD DATA
--TRUNCATE
into table TBPRD_ELN_LINK_PROD
--FIELDS TERMINATED BY X'09'
FIELDS TERMINATED BY ',' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
REQ_CASE_NO
,REQ_CASE_SEQ
,REQ_SUBJECT
,REQ_DEP
,REQ_PERSON
,REQ_TIME
,REQ_PRODUCT
,DURATION
,CURRENCY
,LINK_PROD_1
,LINK_PROD_2
,LINK_PROD_3
,LINK_PROD_4
,LINK_PROD_5
,ROI
,STRIKE_PRICE
,KO_PRICE
,KO_TYPE
,KI_PRICE
,KI_TYPE
,UF
,PRICE_WAY
,RETURN_PRICE
,PRE_PROD_BANK
,RES_PRICE_CASE_NO
,RESULT
,STATUS
,VERSION "0"       
,CREATETIME SYSDATE       
,CREATOR constant "TBPRD_ELN_PRICE.ctl"       
)
       