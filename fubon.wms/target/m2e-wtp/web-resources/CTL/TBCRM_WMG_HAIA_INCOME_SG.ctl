--**************************************************************************
--PROJECT_CODE> TBCRM
--DATE>
--AUTHOR>Ocean Lin
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBCRM_WMG_HAIA_INCOME_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
TRUNCATE
INTO TABLE TBCRM_WMG_HAIA_INCOME_SG
FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
 SNAP_YYYYMM  
,CUST_ID     
,PROD_TYPE   
,CUR_TYPE    
,REF_MK_VALUE
,REF_INVEST   
,REF_DIVIDEND
,REF_INCOME  
,REF_ROI
,VERSION   	constant"0"
,CREATETIME	sysdate
,CREATOR   	constant "TBCRM_WMG_HAIA_INCOME_SG"
,MODIFIER  	constant "TBCRM_WMG_HAIA_INCOME_SG"
,LASTUPDATE	sysdate
)