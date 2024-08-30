--**************************************************************************
--PROJECT_CODE> TBCRM
--DATE>2017/01/20
--AUTHOR> Grace Liu
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBCRM_CUST_CON_NOTE_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
TRUNCATE       
into table TBCRM_CUST_CON_NOTE_SG
FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
( 
YEARMONTH
,PERSON_ID
,PROFIT_LST_Y
,PROFIT_LST_Y_SEG
,PROFIT_ONE_Y
,PROFIT_ONE_Y_SED
,LST_Y
,ONE_Y
,PROFIT_BEST_Y_SEG
,ONE_Y_FLOW_PCAS_TX
,VERSION "0"                                                                           
,CREATETIME sysdate                                                                    
,CREATOR constant "TBCRM_CUST_CON_NOTE_SG"                                                
)                                                                                      
                                                                                       