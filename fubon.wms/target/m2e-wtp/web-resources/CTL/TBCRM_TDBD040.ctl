--**************************************************************************
--PROJECT_CODE> TBKYC
--DATE>2022/06/13
--AUTHOR> Sam Tu
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBCRM_TDBD040
--TRUNCATE_BEFORE_INSERT>N
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true,skip=0)
LOAD DATA
CHARACTERSET AL32UTF8
APPEND
--TRUNCATE
into table	TBCRM_TDBD040
--FIELDS TERMINATED BY X'09'
FIELDS TERMINATED BY '#!!#' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
TX_BRH
,TX_DATE "TO_DATE(:TX_DATE,'YYYYMMDD')"
,ACNO_SA
,SLIP_NO
,CUST_ID
,CUST_NAME
,CLASS
,CUR
,OPN_DPR_AMT
,DPR_PRD_MM
,DPR_PRD_DD
,BK_VALUE "TO_DATE(:BK_VALUE,'YYYYMMDD')"
,DUE_DTE "TO_DATE(:DUE_DTE,'YYYYMMDD')"
,RMK
,INT
,PERSON_FLG
,VERSION
,CREATETIME sysdate
,CREATOR constant "JIHSUN_MANUAL"
,MODIFIER constant "JIHSUN_MANUAL"
,LASTUPDATE sysdate
)