--**************************************************************************
--PROJECT_CODE> TBCRM
--DATE>2016/10/26
--AUTHOR> Grace Liu
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBCRM_FC042_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
TRUNCATE       
into table TBCRM_FC042_SG
FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
( 
CUST_NO POSITION(1:11)
,DB_APP_NO POSITION(12:16)
,MEMO POSITION(17:78)
,CUST_STS POSITION(79:178)
,GUAR_NAME POSITION(179:240)
,MTN_BRH POSITION(241:243)
,MTN_EMP POSITION(244:249)
,MTN_AUT POSITION(250:255)
,MTN_DATE POSITION(256:263)
,MTN_TIME POSITION(264:269)
,FILLER_01 POSITION(270:519)
,FILLER_02 POSITION(520:769)
,VERSION "0"                                                                           
,CREATETIME sysdate                                                                    
,CREATOR constant "TBCRM_FC042_SG"                                                
)                                                                                      
                                                                                       