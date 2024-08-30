--**************************************************************************
--PROJECT_CODE> TBORG
--DATE>2017/08/11
--AUTHOR> Eli Huang
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBORG_PS_SA_INS_CLASS_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
LOAD DATA
TRUNCATE       
into table TBORG_PS_SA_INS_CLASS_SG
FIELDS TERMINATED BY ','  --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
( 
SEQ
,EMPID "substr(:EMPID,5,9)"
,TNDDATE SYSDATE                                             
)                                                                                      
                                                                                                                                                             