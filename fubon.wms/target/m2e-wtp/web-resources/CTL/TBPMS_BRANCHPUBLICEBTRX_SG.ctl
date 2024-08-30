--**************************************************************************
--PROJECT_CODE> TBPMS
--DATE>2016/11/03
--AUTHOR> Grace Liu
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBPMS_BRANCHPUBLICEBTRX_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
TRUNCATE       
into table TBPMS_BRANCHPUBLICEBTRX_SG
FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
( 
DATA_DATE "TO_DATE(:DATA_DATE,'YYYY/MM/DD HH24:MI:ss')"
,PERSON_ID
,TRANNAME50
,BRANCH_NBR
,IP
,TRX_STATUS
,TRX_STATUS_D
,VERSION "0"                                                                           
,CREATETIME sysdate                                                                    
,CREATOR constant "TBPMS_BRANCHPUBLICEBTRX_SG"                                                
)                                                                                      
                                                                                       