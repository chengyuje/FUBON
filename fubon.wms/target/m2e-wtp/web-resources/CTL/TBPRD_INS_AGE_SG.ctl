--**************************************************************************
--PROJECT_CODE> TBPRD
--DATE>2017/08/10
--AUTHOR> Grace Liu
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBPRD_INS_AGE_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
TRUNCATE       
into table TBPRD_INS_AGE_SG
FIELDS TERMINATED BY ',' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
PRD_ID
,INSPRD_ANNUAL
,INSURED_OBJECT
,PAY_TYPE
,MIN_AGE
,MAX_AGE
,EXPIRED_AGE
,VERSION "0"                                                                           
,CREATETIME sysdate                                                                    
,CREATOR constant "TBPRD_INS_AGE_SG"                                                
)                                                                                                                                     