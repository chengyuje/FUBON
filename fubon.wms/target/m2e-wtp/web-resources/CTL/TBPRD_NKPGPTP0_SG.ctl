--**************************************************************************
--PROJECT_CODE> TBPRD_NKPGPTP0_SG
--DATE>2019/07/30
--AUTHOR> Kitty Chiang
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBPRD_NKPGPTP0_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
--CHARACTERSET AL32UTF8
TRUNCATE
into table	TBPRD_NKPGPTP0_SG
--FIELDS TERMINATED BY X'09'
--FIELDS TERMINATED BY ',' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
PGT00 POSITION(1:224)
,CREATETIME sysdate                                                                    
,CREATOR constant "TBPRD_NKPGPTP0_SG" 
)