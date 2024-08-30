--**************************************************************************
--PROJECT_CODE> TBCAM
--DATE>
--AUTHOR>Ocean Lin
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBCAM_WMG_HAIA_YLIST_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
TRUNCATE
INTO TABLE TBCAM_WMG_HAIA_YLIST_SG
FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
 SNAP_YYYYMM       
,CUST_NAME         
,CUST_ID           
,BRA_NBR           
,CUST_KYC          
,LOSS_WARN_VALUE   
,SEASON_END_REF_ROI
,VERSION   	constant"0"
,CREATETIME	sysdate
,CREATOR   	constant "TBCAM_WMG_HAIA_YLIST_SG"
,MODIFIER  	constant "TBCAM_WMG_HAIA_YLIST_SG"
,LASTUPDATE	sysdate
)