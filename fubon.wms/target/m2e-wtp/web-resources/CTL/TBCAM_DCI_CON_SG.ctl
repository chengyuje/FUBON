--**************************************************************************
--PROJECT_CODE> TBCAM
--DATE> 2021/06/03
--AUTHOR> Ocean
--PURPOSE> ST Load
--**************************************************************************

--**************************************************************************
--TARGET> TBCAM_DCI_CON_SG_SG
--TRUNCATE_BEFORE_INSERT> Y
--**************************************************************************
--OPTIONS (ERRORS = 0)

OPTIONS (direct=true)
LOAD DATA
TRUNCATE
into table	TBCAM_DCI_CON_SG
--FIELDS TERMINATED BY X'09'
FIELDS TERMINATED BY ',' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
CUST_ID
,CUST_NAME
,TRAN_ID
,EXPIRY_DATE
,VERSION "0"
,CREATETIME sysdate
,CREATOR constant "TBCAM_DCI_CON_SG"
)