--**************************************************************************
--PROJECT_CODE> TBPMS
--DATE> 2022/08/04
--AUTHOR> Ocean Lin
--PURPOSE> ST Load
--**************************************************************************

--**************************************************************************
--TARGET> TBPMS_EMP_DAILY_TXN_SUM_D_SG
--TRUNCATE_BEFORE_INSERT> Y
--**************************************************************************

--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
CHARACTERSET AL32UTF8
TRUNCATE
into table TBPMS_EMP_DAILY_TXN_SUM_D_SG
--FIELDS TERMINATED BY X'09'
FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
 SNAP_DATE "TO_DATE(:SNAP_DATE,'YYYYMMDD')"    
,TX_DATE   "TO_DATE(:TX_DATE,'YYYYMMDD')"  
,BUCODE        
,TW_PRER_NUM_BU
,NAME_FORMAL   
,TX_AMT        
,VERSION "0"       
,CREATETIME sysdate       
,CREATOR constant "TBPMS_EMP_DAILY_TXN_SUM_D_SG"    
,MODIFIER constant "TBPMS_EMP_DAILY_TXN_SUM_D_SG"
,LASTUPDATE sysdate  
)
       