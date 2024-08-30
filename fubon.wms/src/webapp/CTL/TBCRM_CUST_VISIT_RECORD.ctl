--**************************************************************************
--PROJECT_CODE> TBKYC
--DATE>2022/04/12
--AUTHOR> Sam Tu
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBCRM_CUST_VISIT_RECORD
--TRUNCATE_BEFORE_INSERT>N
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true,skip=0)
LOAD DATA
CHARACTERSET AL32UTF8
APPEND
--TRUNCATE
into table	TBCRM_CUST_VISIT_RECORD
--FIELDS TERMINATED BY X'09'
FIELDS TERMINATED BY '#!!#' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
VISIT_SEQ
,VISITOR_ROLE
,CUST_ID
,CMU_TYPE
,VISIT_MEMO CHAR(4000)
,VERSION
,CREATOR
,CREATETIME  "TO_TIMESTAMP(:CREATETIME,'YYYY-MM-DD HH24:MI:SS.FF')"
,MODIFIER 
,LASTUPDATE  "TO_TIMESTAMP(:LASTUPDATE,'YYYY-MM-DD HH24:MI:SS.FF')"
,VISIT_DT  "TO_DATE(:VISIT_DT,'YYYY-MM-DD HH24:MI:SS')"
,VISIT_CREPLY CHAR(4000)
)