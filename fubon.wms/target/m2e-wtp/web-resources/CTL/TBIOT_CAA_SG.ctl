--**************************************************************************
--PROJECT_CODE> TBIOT
--DATE>2016/11/25
--AUTHOR> Grace Liu
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBIOT_CAA_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true,skip=1)
LOAD DATA
TRUNCATE
into table	TBIOT_CAA_SG
--FIELDS TERMINATED BY X'09'
FIELDS TERMINATED BY ',' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
KEYIN_DATE "TO_DATE(:KEYIN_DATE,'YYYY/MM/DD HH24:MI:ss')"
,OP_BATCH_NO			
,INS_ID				
,INSURED_NAME		
,BRANCH_NBR			
,BRANCH_NAME			
,INSPRD_ID			
,INSPRD_ANNUAL		
,PAY_TYPE			
,MOP2				
,SPECIAL_CONDITION	
,CURR_CD				
,REAL_PREMIUM		
,STATUS				
,REMARK				
,SUBMIT_DATE "TO_DATE(:SUBMIT_DATE,'YYYY/MM/DD HH24:MI:ss')"			
,DOC_TYPE 
,VERSION "0"
,CREATETIME sysdate
,CREATOR constant "TBIOT_CAA_SG"
)