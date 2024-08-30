--**************************************************************************
--PROJECT_CODE> TBCRM
--DATE>2019/03/05
--AUTHOR> Cathy Tang
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBCRM_LM5800Q2_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
--CHARACTERSET AL32UTF8
TRUNCATE
into table	TBCRM_LM5800Q2_SG
--FIELDS TERMINATED BY X'09'
FIELDS TERMINATED BY ';' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
CUST_NAME	
,CUST_NO		
,LM_ACNO		
,CUR_COD		
,ACNO_BAL	
,ENGINE_NO	
,STK_COD		
,TRUST_BAL	
,K_RATE		
,R_LN_AMT	
,L_LM_AMT	
,C_TYPE	
,VERSION "0"       
,CREATETIME sysdate       
,CREATOR constant "TBCRM_LM5800Q2_SG"     
,MODIFIER constant "TBCRM_LM5800Q2_SG"
,LASTUPDATE sysdate  
)
       