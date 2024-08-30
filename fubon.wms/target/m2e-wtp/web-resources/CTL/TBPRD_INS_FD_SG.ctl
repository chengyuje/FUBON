--**************************************************************************
--PROJECT_CODE> TBPRD
--DATE>2018/02/09
--AUTHOR> Grace Liu
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBPRD_INS_FD_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
TRUNCATE       
into table TBPRD_INS_FD_SG
FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
( 
fund_code
,CEFF_YY  
,Price   
,VERSION "0"                                                                           
,CREATETIME sysdate                                                                    
,CREATOR constant "TBPRD_INS_FD_SG"                                                
)                                                                                      
                                                                                       