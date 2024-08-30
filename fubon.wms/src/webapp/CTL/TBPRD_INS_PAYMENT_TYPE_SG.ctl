--**************************************************************************
--PROJECT_CODE> TBPRD
--DATE>2017/08/10
--AUTHOR> Grace Liu
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBPRD_INS_PAYMENT_TYPE_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
TRUNCATE       
into table TBPRD_INS_PAYMENT_TYPE_SG
FIELDS TERMINATED BY ',' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
PRD_ID "TRIM(:PRD_ID)"
,INSPRD_ANNUAL
,PAY_TYPE
,PAY_RATE
,VERSION "0"                                                                           
,CREATETIME sysdate                                                                    
,CREATOR constant "TBPRD_INS_PAYMENT_TYPE_SG"                                                
)                                                                                                                                     