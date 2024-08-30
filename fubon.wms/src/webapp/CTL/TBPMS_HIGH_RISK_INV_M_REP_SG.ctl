--**************************************************************************
--PROJECT_CODE> TBPMS
--DATE> 2022/08/04
--AUTHOR> Ocean Lin
--PURPOSE> ST Load
--**************************************************************************

--**************************************************************************
--TARGET> TBPMS_HIGH_RISK_INV_M_REP
--TRUNCATE_BEFORE_INSERT> Y
--**************************************************************************

--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
--CHARACTERSET AL32UTF8
TRUNCATE
into table TBPMS_HIGH_RISK_INV_M_REP_SG
--FIELDS TERMINATED BY X'09'
FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
 SNAP_YYYYMM
,AO_BRANCH_NBR
,AO_NAME
,AO_EMP_ID
,KIND_TYPE
,TX_AMT_TWD
,BAL_TWD_ME
,VERSION "0"       
,CREATETIME sysdate       
,CREATOR constant "TBPMS_HIGH_RISK_INV_M_REP_SG"     
,MODIFIER constant "TBPMS_HIGH_RISK_INV_M_REP_SG"
,LASTUPDATE sysdate  
)
       