--**************************************************************************
--PROJECT_CODE> TBPMS
--DATE> 2022/08/04
--AUTHOR> Ocean Lin
--PURPOSE> ST Load
--**************************************************************************

--**************************************************************************
--TARGET> TBPMS_EMP_DAILY_TXN_D_SG
--TRUNCATE_BEFORE_INSERT> Y
--**************************************************************************

--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
CHARACTERSET AL32UTF8
TRUNCATE
into table TBPMS_EMP_DAILY_TXN_D_SG
--FIELDS TERMINATED BY X'09'
FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
 SNAP_DATE "TO_DATE(:SNAP_DATE,'YYYYMMDD')"     
,ACT_DATE  "TO_DATE(:ACT_DATE,'YYYYMMDD')"    
,TX_DATE   "TO_DATE(:TX_DATE,'YYYYMMDD')"    
,DRCR          
,NATIONAL_ID   
,NAME_FORMAL   
,ACCT_NBR_ORI  
,CUST_NAME     
,RCV_ACCT_NO   
,TX_AMT        
,MEMO          
,REMK          
,JRNL_NO       
,TW_PRER_NUM_BU
,BUCODE        
,VERSION "0"       
,CREATETIME sysdate       
,CREATOR constant "TBPMS_EMP_DAILY_TXN_D_SG"    
,MODIFIER constant "TBPMS_EMP_DAILY_TXN_D_SG"
,LASTUPDATE sysdate  
)
       