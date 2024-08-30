--**************************************************************************
--PROJECT_CODE> TBPMS
--DATE> 2022/08/04
--AUTHOR> Ocean Lin
--PURPOSE> ST Load
--**************************************************************************

--**************************************************************************
--TARGET> TBPMS_HIGH_RISK_INV_D_REP
--TRUNCATE_BEFORE_INSERT> Y
--**************************************************************************

--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
--CHARACTERSET AL32UTF8
TRUNCATE
into table TBPMS_HIGH_RISK_INV_D_REP_SG
--FIELDS TERMINATED BY X'09'
FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
 SNAP_YYYYMM
,TX_DATE "TO_DATE(SUBSTR(:TX_DATE, 0, 19), 'MM/DD/YYYY HH24:MI:SS')"
,AO_BRANCH_NBR
,AO_NAME
,AO_EMP_ID
,ACCT_NBR_ORI
,DRCR
,KIND_TYPE
,STCK_CSTDY_CD
,CUR
,TX_AMT_TWD
,JRNL_NO
,REMK
,MEMO
,VERSION "0"       
,CREATETIME sysdate       
,CREATOR constant "TBPMS_HIGH_RISK_INV_D_REP_SG"     
,MODIFIER constant "TBPMS_HIGH_RISK_INV_D_REP_SG"
,LASTUPDATE sysdate  
)
       