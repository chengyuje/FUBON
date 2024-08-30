--**************************************************************************
--PROJECT_CODE> TBCAM
--DATE>2016/10/13
--AUTHOR> Grace Liu
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBCAM_NFNOTICE_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
TRUNCATE
into table	TBCAM_NFNOTICE_SG
--FIELDS TERMINATED BY X'09'
FIELDS TERMINATED BY ',' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
BRANCH_NBR
,CUST_ID
,CUST_NAME
,LEAD_NAME
,END_DT "TRIM(:END_DT)"
,DESCRIPTION
,PRIOR_STATUS_CAT
,VERSION "0"
,CREATETIME sysdate
,CREATOR constant "TBCAM_NFNOTICE_SG"
)