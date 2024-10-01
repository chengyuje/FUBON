--**************************************************************************
--PROJECT_CODE> TBELN
--DATE>2024/09/27
--AUTHOR> Carley Chen
--PURPOSE>ELN
--**************************************************************************

--**************************************************************************
--TARGET>TBPRD_ELN_LINK_PROD
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
SECURITY_TYPE
,BBG_CODE
,BBG_NAME
,AP_CODE
,VERSION "0"       
,CREATETIME SYSDATE       
,CREATOR constant "TBPRD_ELN_LINK_PROD.ctl"       
)
       