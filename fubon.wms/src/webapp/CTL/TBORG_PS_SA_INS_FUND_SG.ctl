--**************************************************************************
--PROJECT_CODE> TBORG
--DATE>2017/08/11
--AUTHOR> Eli Huang
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBORG_PS_SA_INS_FUND_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
OPTIONS (SKIP=1)
LOAD DATA
TRUNCATE       
into table TBORG_PS_SA_INS_FUND_SG
FIELDS TERMINATED BY ',' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
( 
CLASS
,FUNDID                                             
)                                                                                      
                                                                                       