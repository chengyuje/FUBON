--**************************************************************************
--PROJECT_CODE> TBPRD
--DATE>2023/09/11
--AUTHOR> Cathy Tang
--PURPOSE>SI FCI Project
--**************************************************************************

--**************************************************************************
--TARGET>TBSOT_FCI_PRODID_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true,skip=0)
LOAD DATA
TRUNCATE
into table TBSOT_FCI_PRODID_SG
--FIELDS TERMINATED BY X'09'
FIELDS TERMINATED BY ',' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
BATCH_SEQ
,PROD_ID
,VERSION "0"       
,CREATETIME SYSDATE       
,CREATOR constant "TBSOT_FCI_PRODID_SG"       
)
       