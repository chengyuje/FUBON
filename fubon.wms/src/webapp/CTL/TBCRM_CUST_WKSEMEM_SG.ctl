--**************************************************************************
--DATE>2024/12/10
--AUTHOR> Sam Tu
--**************************************************************************

--**************************************************************************
--TARGET>TBCRM_CUST_WKSEMEM_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
LOAD DATA
TRUNCATE       
into table TBCRM_CUST_WKSEMEM_SG
--FIELDS TERMINATED BY ','  --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
( 
SEM001 position(1: 12) "trim(:SEM001)"   
, SEM002 position(13: 23) "trim(:SEM002)"     
, VERSION "0"
, CREATETIME SYSDATE
, CREATOR constant "TBCRM_CUST_WKSEMEM_SG.ctl"
, MODIFIER constant "TBCRM_CUST_WKSEMEM_SG.ctl"
, LASTUPDATE SYSDATE                   
)                                                                                      
               