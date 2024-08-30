--**************************************************************************
--PROJECT_CODE> TBCRM
--DATE>2017/01/09
--AUTHOR> Grace Liu
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBCRM_CIF_W_SUM_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
TRUNCATE       
into table TBCRM_CIF_W_SUM_SG
FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
DATA_DAY
,CUST_ID
,AVG_AUM_AMT
,VERSION "0"                                                                           
,CREATETIME sysdate                                                                    
,CREATOR constant "TBCRM_CIF_W_SUM_SG"                                                
)                                                                                      
                                                                                       