--**************************************************************************
--PROJECT_CODE> TBPRD
--DATE>2017/08/10
--AUTHOR> Grace Liu
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBPRD_INS_RATE_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
TRUNCATE       
into table TBPRD_INS_RATE_SG
FIELDS TERMINATED BY ',' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
PRD_ID "TRIM(:PRD_ID)"
,INSPRD_ANNUAL
,SEX
,AGE
,AI_CLASS
,HS_TYPE
,HS_PLAN
,SA_RANGE
,PUB_INSU
,UNIT
,PAY_TYPE
,PREM_RATE
,VERSION "0"                                                                           
,CREATETIME sysdate                                                                    
,CREATOR constant "TBPRD_INS_RATE_SG"                                                
)                                                                                                                                     