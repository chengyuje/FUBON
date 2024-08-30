--**************************************************************************
--PROJECT_CODE> TBCRM
--DATE>
--AUTHOR>Ocean Lin
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBCRM_WMG_HAIA_M_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
TRUNCATE
INTO TABLE TBCRM_WMG_HAIA_M_SG
FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
 SNAP_YYYYMM	 
,CUST_ID        
,CUST_KYC       
,RISK_VALUE	 
,REF_MK_VALUE   
,REF_ROI	     
,PROD_RISK_P1_PR
,PROD_RISK_P2_PR
,PROD_RISK_P3_PR
,PROD_RISK_P4_PR
,RISK_SUM       
,VALIDATE_YN
,VERSION   	constant"0"
,CREATETIME	sysdate
,CREATOR   	constant "TBCRM_WMG_HAIA_M_SG"
,MODIFIER  	constant "TBCRM_WMG_HAIA_M_SG"
,LASTUPDATE	sysdate
)