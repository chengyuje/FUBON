--**************************************************************************
--PROJECT_CODE> WMS
--DATE> 2023/06/07
--AUTHOR> Cathy
--PURPOSE> WMS-CR-20230417-01
--**************************************************************************

--**************************************************************************
--SOURCE> TBPMS_CBILL_COMFM_C_RECV.TXT
--TARGET> TBPMS_CBILL_COMFM_C_RECV_SG
--TRUNCATE_BEFORE_INSERT> Y
--**************************************************************************
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
TRUNCATE
into table	TBPMS_CBILL_COMFM_C_RECV_SG
--FIELDS TERMINATED BY X'09'
FIELDS TERMINATED BY ',' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
CUST_ID
,STATEMENT_DATE
,SM_BRANCH_NBR
,STATEMENT_SEND_TYPE
,MATCH_YN
,VERSION      "0"
,CREATOR      CONSTANT "TBPMS_CBILL_COMFM_C_RECV_SG"
,CREATETIME   SYSDATE
,MODIFIER     CONSTANT "TBPMS_CBILL_COMFM_C_RECV_SG"
,LASTUPDATE   SYSDATE
)
       