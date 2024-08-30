--**************************************************************************
--PROJECT_CODE> TBCRM
--DATE>
--AUTHOR>Ocean Lin
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBCRM_WMG_HAIA_CUR_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
TRUNCATE
INTO TABLE TBCRM_WMG_HAIA_CUR_SG
FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
 SNAP_YYYYMM  
,CUST_ID     
,CUR_TYPE    
,CUR_MK_VALUE
,CUR_PR 
,VERSION   	constant"0"
,CREATETIME	sysdate
,CREATOR   	constant "TBCRM_WMG_HAIA_CUR_SG"
,MODIFIER  	constant "TBCRM_WMG_HAIA_CUR_SG"
,LASTUPDATE	sysdate
)