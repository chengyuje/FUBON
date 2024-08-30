--**************************************************************************
--PROJECT_CODE> TBPRD
--DATE>2023/09/11
--AUTHOR> Cathy Tang
--PURPOSE>SI FCI Project
--**************************************************************************

--**************************************************************************
--TARGET>TBSOT_FCI_INV_DETAIL_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true,skip=0)
LOAD DATA
TRUNCATE
into table TBSOT_FCI_INV_DETAIL_SG
--FIELDS TERMINATED BY X'09'
FIELDS TERMINATED BY ',' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
STATUS      
,BATCH_SEQ 	
,PROD_ID         
,TRADE_DATE "TO_DATE(:TRADE_DATE,'YYYY/MM/DD')" 
,VALUE_DATE "TO_DATE(:VALUE_DATE,'YYYY/MM/DD')"
,MON_PERIOD      
,PRD_PROFEE_RATE	
,PROD_CURR       
,PURCHASE_AMT    
,SPOT_DATE "TO_DATE(:SPOT_DATE,'YYYY/MM/DD')"		
,EXPIRE_DATE "TO_DATE(:EXPIRE_DATE,'YYYY/MM/DD')"
,RM_PROFEE      	
,MUREX_SEQ		
,EMP_ID			
,BRANCH_NBR	
,VERSION "0"       
,CREATETIME SYSDATE       
,CREATOR constant "TBSOT_FCI_INV_DETAIL_SG"       
)
       