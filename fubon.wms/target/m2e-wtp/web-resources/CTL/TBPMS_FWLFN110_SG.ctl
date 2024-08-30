--**************************************************************************
--PROJECT_CODE> TBPMS
--DATE>2017/05/23
--AUTHOR> NINA TSAO
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBPMS_FWLFN110_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true,skip=0) 
LOAD DATA
--CHARACTERSET UTF16
TRUNCATE       
into table TBPMS_FWLFN110_SG
FIELDS TERMINATED BY ',' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
( 
POLICY_SEQ,
ID_DUP,
ID_NO,
APPL_ID,
APPL_NAME,
UNIT_CODE,
SERVANT_ID,
SERVANT_NAME,
BRH_MGR_ID,
BRH_MGR_NAME,
INS_SALE_ID,
STAT,
STAT_DESC,
INS_CERT_TYPE,
VERSION "0",
CREATETIME SYSDATE,
CREATOR constant "TBPMS_FWLFN110_SG",
BRANCH_NBR "SUBSTR(:UNIT_CODE,LENGTH(TRIM(:UNIT_CODE))-2,3)"
)                                      
   
    