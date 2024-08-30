--**************************************************************************
--PROJECT_CODE> TBPRD
--DATE>2016/12/15
--AUTHOR> Grace Liu
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBPRD_FUNDMFEE_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
TRUNCATE
into table	TBPRD_FUNDMFEE_SG
--FIELDS TERMINATED BY X'09'
FIELDS TERMINATED BY ',' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
PRD_ID
,M_FEE
,VERSION "0"
,CREATETIME sysdate
,CREATOR constant "TBPRD_FUNDMFEE_SG"
)