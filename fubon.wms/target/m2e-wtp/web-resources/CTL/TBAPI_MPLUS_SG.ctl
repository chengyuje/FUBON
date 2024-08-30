--**************************************************************************
--PROJECT_CODE> TBCRM
--DATE>2016/11/06
--AUTHOR> Grace Liu
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBAPI_MPLUS_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
CHARACTERSET AL32UTF8
TRUNCATE
into table	TBAPI_MPLUS_SG
--FIELDS TERMINATED BY X'09'
FIELDS TERMINATED BY ',' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
EMP_NAME
,EMP_NUMBER
,MPLUS_UID
,DEPARTMENT
,EMAIL
,STATUS
,EXTENSION
,VERSION "0"
,CREATETIME sysdate
,CREATOR constant "TBAPI_MPLUS_SG"
)