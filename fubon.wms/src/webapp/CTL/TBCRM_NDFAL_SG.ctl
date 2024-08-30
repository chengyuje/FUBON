--**************************************************************************
--PROJECT_CODE> TBIOT
--DATE>2016/09/01
--AUTHOR> Grace Liu
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBCRM_NDFAL_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
TRUNCATE
into table	TBCRM_NDFAL_SG
--FIELDS TERMINATED BY X'09'
FIELDS TERMINATED BY ',' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
PHASE            
,ITEM_REMRK       
,POLICY_NO        
,POLICY_SEQ       
,ID_DUP "NVL(:ID_DUP,' ')"      
,BRANCH_NBR "TRIM(:BRANCH_NBR)"      
,RECRUIT_ID       
,PAY_TYPE "TRIM(:PAY_TYPE)"        
,PAY_TIME         
,STATEMENT_PREM   
,APPL_ID          
,PAY_WAY          
,DRAWEE_BANK      
,CREDIT_CARD_NUM  
,CREDIT_EXP_DATE "NVL(SUBSTR(:CREDIT_EXP_DATE,1,6),'')"  
,DEDUCT_DATE "TO_DATE(:DEDUCT_DATE,'YYYYMMDD')"      
,FAIL_REASON_CODE 
,FAIL_ZH_REASON   
,FAIL_TIMES       
,NEXT_DEDUCT_DATE "TO_DATE(:NEXT_DEDUCT_DATE,'YYYYMMDD')"
,FINAL_DEDUCT_DATE "TO_DATE(:FINAL_DEDUCT_DATE,'YYYYMMDD')"
,CANCEL_INSU_DATE "TO_DATE(:CANCEL_INSU_DATE,'YYYYMMDD')"
,PAYOR_ID         
,PAYOR_NAME       
,HOME_PHONE       
,COMPANY_PHONE    
,CELL_PHONE       
,VERSION "0"       
,CREATETIME sysdate       
,CREATOR constant "TBCRM_NDFAL_SG"       
)
       