--**************************************************************************
--PROJECT_CODE> TBIOT
--DATE>2021/03/05
--AUTHOR> Carley Chen
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBMTC_NMP2YFWK_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true,skip=1)
LOAD DATA
TRUNCATE
into table	TBMTC_NMP2YFWK_SG
WHEN (01) <> '#'
--FIELDS TERMINATED BY X'09'
FIELDS TERMINATED BY ',' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
CON_NO
,NMP2YFWK_STATUS
,NMP2YFWK_NO
,NMP2YFWK_ACC
,VERSION      "0"
,CREATOR      CONSTANT "TBMTC_NMP2YFWK_SG"
,CREATETIME   SYSDATE
,MODIFIER     CONSTANT "TBMTC_NMP2YFWK_SG"
,LASTUPDATE   SYSDATE
)
       