--**************************************************************************
--PROJECT_CODE> TBCRM
--DATE>2016/11/06
--AUTHOR> Grace Liu
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBCRM_FcNoninvt_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
TRUNCATE
into table	TBCRM_FcNoninvt_SG
WHEN (15) = 'Y'
--FIELDS TERMINATED BY X'09'
FIELDS TERMINATED BY ',' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
CUST_ID POSITION(1:11)
,VERSION "0"
,CREATETIME sysdate
,CREATOR constant "TBCRM_FcNoninvt_SG"
)