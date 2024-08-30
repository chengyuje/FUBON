--**************************************************************************
--PROJECT_CODE> TBCRM
--DATE>2016/10/26
--AUTHOR> Grace Liu
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBCRM_FC008_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
TRUNCATE       
into table TBCRM_FC008_SG
FIELDS TERMINATED BY ',' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
( 
CUST_NO POSITION(1:11) "TRIM(:CUST_NO)"
,RESP_ID  POSITION(12:22)   
,TRANS_SN POSITION(23:37)
,TRANS_ROWS POSITION(38:52)
,TRANS_ACTION POSITION(53:53)
,ERROR_CHECK POSITION(54:54)
,VERSION "0"                                                                           
,CREATETIME sysdate                                                                    
,CREATOR constant "TBCRM_FC008_SG"                                                
)                                                                                      
                                                                                       