--**************************************************************************
--PROJECT_CODE> WMS
--DATE> 2022/06/10
--AUTHOR> Cathy
--PURPOSE> WMS-CR-20220420-03
--**************************************************************************

--**************************************************************************
--SOURCE> WMS_MBILL_COMFM_C_RECV.TXT
--TARGET> TBPMS_MBILL_COMFM_C_RECV_SG
--TRUNCATE_BEFORE_INSERT> Y
--**************************************************************************
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
CHARACTERSET AL32UTF8
TRUNCATE
into table	TBPMS_MBILL_COMFM_C_RECV_SG
--FIELDS TERMINATED BY X'09'
FIELDS TERMINATED BY '|' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
FILE_MONTH
,BASE_DATE
,PRINT_DATE
,ID_REG_CODE
,BRANCH
,FIE_NAME
,FS_NAME
,RETURN_DATE
,VALID_SAMPLE
,CUSTOMER_RESPONSE_MATCH
,CUSTOMER_RESPONSE_VALID
,CONTACT_DATE
,REPLY_DATE
,ID
,VERSION      "0"
,CREATOR      CONSTANT "TBPMS_MBILL_COMFM_C_RECV_SG"
,CREATETIME   SYSDATE
,MODIFIER     CONSTANT "TBPMS_MBILL_COMFM_C_RECV_SG"
,LASTUPDATE   SYSDATE
)
       