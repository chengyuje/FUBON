--**************************************************************************
--PROJECT_CODE> TBPRD
--DATE>2016/10/14
--AUTHOR> Grace Liu
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBPRD_NATIONALITY_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
APPEND
into table	TBPRD_NATIONALITY_SG
--FIELDS TERMINATED BY X'09'
FIELDS TERMINATED BY ',' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
TYPE	POSITION(1:2)
,COUNTRY_ID	POSITION(3:5)
,PROD_ID	POSITION(6:9)
,COUNTRY_NAME	POSITION(10:329)
,SOU_TYPE constant"NF"
,VERSION "0"
,CREATETIME sysdate
,CREATOR constant "TBPRD_NATIONALITY_SG"
)