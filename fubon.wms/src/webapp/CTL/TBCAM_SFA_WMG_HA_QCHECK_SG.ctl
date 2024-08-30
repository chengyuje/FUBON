--**************************************************************************
--PROJECT_CODE> TBCAM
--DATE>2023/09/01
--AUTHOR>Ocean Lin
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBCAM_SFA_WMG_HA_QCHECK_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
TRUNCATE
INTO TABLE TBCAM_SFA_WMG_HA_QCHECK_SG
FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
 SNAP_YYYYMM  
,CUST_ID      
,CUST_NAME    
,BOND_CCT     
,NPP_CCT      
,TOTAL_CCT    
,BOND_STD     
,NPP_STD      
,TOTAL_STD    
,OVER_STD_TYPE  
,OVER_STD_MES
,VERSION   	constant"0"
,CREATETIME	sysdate
,CREATOR   	constant "TBCAM_SFA_WMG_HA_QCHECK_SG"
,MODIFIER  	constant "TBCAM_SFA_WMG_HA_QCHECK_SG"
,LASTUPDATE	sysdate
)