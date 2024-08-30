--**************************************************************************
--PROJECT_CODE> TBSQM
--DATE>2018/03/06
--AUTHOR> Kan
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET> TBSQM_CSM_MAIN_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true,skip=1)
LOAD DATA
CHARACTERSET AL32UTF8
TRUNCATE
into table	TBSQM_CSM_MAIN_SG
--FIELDS TERMINATED BY X'09'
FIELDS TERMINATED BY ',' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
DATA_DATE
,QTN_TYPE constant "WMS05"
,REGION_CENTER_ID
,BRANCH_AREA_ID
,BRANCH_NBR
,EMP_ID
,AO_CODE
,QUESTION_DESC
,CUST_NAME
,CUST_ID
,MOBILE_NO
,ANSWER
,RESP_DATE
,SEND_DATE
,CAMPAIGN_ID
--,STEP_ID
,SEND_QUANTITY
,VERSION "0"
,CREATETIME sysdate
,CREATOR constant "TBSQM_CSM_MAIN_SG_MSG"
)