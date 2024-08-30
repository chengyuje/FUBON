--**************************************************************************
--PROJECT_CODE> TBSQM
--DATE>2018/07/30
--AUTHOR> Willis
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET> TBSQM_CSM_OPTIONS_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
OPTIONS (ERRORS = 0)
--OPTIONS (direct=true,skip=1)
LOAD DATA
CHARACTERSET AL32UTF8
TRUNCATE
into table	TBSQM_CSM_OPTIONS_SG
--FIELDS TERMINATED BY X'09'
FIELDS TERMINATED BY ',' 
OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
ITEM_MAPPING_ID
,UPDATE_DATE "TO_DATE(SUBSTR(REPLACE(:UPDATE_DATE,'T',' '),1,19),'YYYY-MM-DD HH24:MI:SS')"
,DESCRIPTION
,PRIORITY "TO_NUMBER(:PRIORITY)"
,QUESTION_MAPPING_ID
)