--**************************************************************************
--PROJECT_CODE> TBPMS
--DATE> 2022/08/04
--AUTHOR> Ocean Lin
--PURPOSE> ST Load
--**************************************************************************

--**************************************************************************
--TARGET> TBPMS_WMG_SVIP_CASE_M_SG
--TRUNCATE_BEFORE_INSERT> Y
--**************************************************************************

--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
CHARACTERSET AL32UTF8
TRUNCATE
into table TBPMS_WMG_SVIP_CASE_M_SG
--FIELDS TERMINATED BY X'09'
FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
 SNAP_YYYYMM
,CUST_ID
,AGE
,CARE_TYPE
,REMARK1
,REMARK2
,REMARK3
,VERSION "0"       
,CREATETIME sysdate       
,CREATOR constant "TBPMS_WMG_SVIP_CASE_M_SG"    
,MODIFIER constant "TBPMS_WMG_SVIP_CASE_M_SG"
,LASTUPDATE sysdate  
)
       