--**************************************************************************
--PROJECT_CODE> TBSQM
--DATE>2018/03/13
--AUTHOR> Kan
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET> TBSQM_CSM_QUESTION_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
OPTIONS (ERRORS = 0)
--OPTIONS (direct=true,skip=1)
LOAD DATA
CHARACTERSET AL32UTF8
TRUNCATE
into table	TBSQM_CSM_QUESTION_SG
--FIELDS TERMINATED BY X'09'
FIELDS TERMINATED BY ',' 
OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
QUESTION_MAPPING_ID
,UPDATE_DATE "TO_DATE(SUBSTR(REPLACE(:UPDATE_DATE,'T',' '),1,19),'YYYY-MM-DD HH24:MI:SS')" 
,DESCRIPTION
,PRIORITY "TO_NUMBER(:PRIORITY)"
,QUESTIONNAIRE_ID 
,QTN_TYPE
)