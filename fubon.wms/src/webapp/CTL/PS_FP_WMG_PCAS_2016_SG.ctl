--**************************************************************************
--PROJECT_CODE> PEOPSOFT
--DATE>2017/08/24
--AUTHOR> Grace Liu
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>PS_FP_WMG_PCAS_2016_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
TRUNCATE
into table	PS_FP_WMG_PCAS_2016_SG
FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
SNAP_YYYYMM
,PROD_TYP_1 
,PROD_TYP_2 
,CUST_ID    
,PROD_KEY   
,TXN_DT     
,TXN_AMT_ORI
,CURR       
,REF_EXCH_RT
,REF_EXCH_DT
,TXN_AMT_NTD
,TXN_TYP    
,VERSION "0"
,CREATETIME sysdate
,CREATOR constant "PS_FP_WMG_PCAS_2016_SG"
)