--**************************************************************************
--PROJECT_CODE> TBIOT
--DATE>2016/10/04
--AUTHOR> Grace Liu
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBCAM_LOAN_CASE_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true,skip=1)
LOAD DATA
TRUNCATE
into table	TBCAM_LOAN_CASE_SG
--FIELDS TERMINATED BY X'09'
FIELDS TERMINATED BY ',' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
TXN_ID "NVL(:TXN_ID,' ')"
,CASEID
,CASE_PROPERTY
,REF_PROD
,REF_ORG_ID
,REF_ORG_DESC CHAR(1000)
,LOAN_ACCT_NO
,LOAN_CUST_ID
,LOAN_CUST_NAME
,AO_PERSON_ID
,AO_NAME
,SALES_PERSON_ID
,APPLY_DATE 
,APPLY_AMT
,PROCESS_DATE
,EXAM_RSLT
,EXAM_DTTM "TRIM(:EXAM_DTTM)"
,REJ_TYPE CHAR(1000)"TRIM(:REJ_TYPE)"
,APPROVE_AMT 
,APPROVE_DTTM
,APPROVE_RATE_STR CHAR(1000)
,HANDLE_CHARGE
,FUNDS_DTTM
,FUNDS_AMT
,FUND_ACCT_NO
,CREDIT_TYPE
,CLOSED_DATE
,VERSION "0"
,CREATETIME sysdate
,CREATOR constant "TBCAM_LOAN_CASE_SG"
)