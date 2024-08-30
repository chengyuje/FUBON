--**************************************************************************
--PROJECT_CODE> TBCRM
--DATE>2017/01/18
--AUTHOR> Grace Liu
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBCRM_CUST_PROFEE_SAV_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
TRUNCATE       
into table TBCRM_CUST_PROFEE_SAV_SG
FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
( 
YYYYMM
,CUST_ID
,ACCT_NO
,ACCT_BRH_COD
,S_BRH_COD
,AO_CODE
,AO_LEVEL
,GL_ACCT_NO
,BIG_BLOCK
,AVG_BAL
,BALANCE
,INTEST
,AMT
,ID_FLG
,SOURCE_NAME
,SMALL_BLOCK
,INCOME_AMT
,EKE_AMT
,PROD_ID
,GL_CNAME
,CUR
,VERSION "0"                                                                           
,CREATETIME sysdate                                                                    
,CREATOR constant "TBCRM_CUST_PROFEE_SAV_SG"                                                
)                                                                                      
                                                                                       