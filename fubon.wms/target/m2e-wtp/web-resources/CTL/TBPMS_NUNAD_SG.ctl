--**************************************************************************
--PROJECT_CODE> TBPMS
--DATE>2016/08/26
--AUTHOR> Grace Liu
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBPMS_NUNAD_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
--CHARACTERSET AL32UTF8
TRUNCATE
into table	TBPMS_NUNAD_SG
--FIELDS TERMINATED BY X'09'
FIELDS TERMINATED BY ',' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
POLICY_NO
,SEQ
,ID_DUP "NVL(:ID_DUP,' ')"
,M_TYPE
,DTL_TYPE
,BRANCH_NBR
,BROKER_ID
,PROPOSER_ID
,INSURED_ID
,NOTE_DT
,CONTENT
,END_DT "TRIM(:END_DT)"
,NOTE_LINK
,NOTE_ID
,NOTE_NAME
,NOTE_NBR
,BROKER_NO
,NOTE_NO
,CASE_NO
,NOTE_CODE
,REPLY_DT
,VERSION "0"
,CREATETIME sysdate
,CREATOR constant "TBPMS_NUNAD_SG"
)