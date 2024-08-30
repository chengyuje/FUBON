--**************************************************************************
--PROJECT_CODE> TBCAM
--DATE>2022/09/22
--AUTHOR> Sam Tu
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBPMS_REDEEM_AUTORETURN_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
TRUNCATE
into table TBPMS_REDEEM_AUTORETURN_SG
--FIELDS TERMINATED BY X'09'
FIELDS TERMINATED BY ',' -- OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
CUST_ID			POSITION(1:19)     "TRIM(:CUST_ID)"
,EMAIL			POSITION(20:87)    "TRIM(:EMAIL)"
,ERRORCODE			POSITION(88:90)   "TRIM(:ERRORCODE)"
,PROTOCOLDETAIL			POSITION(91:218)  "TRIM(:PROTOCOLDETAIL)"
,MAIL_STATUS          POSITION(219:230)  "TRIM(:MAIL_STATUS)"
,ID_TYPE          POSITION(231:232)  "TRIM(:ID_TYPE)"
,VERSION "0"                                                                           
,CREATETIME sysdate                                                                    
,CREATOR constant "TBPMS_REDEEM_AUTORETURN_SG"        
)       