--**************************************************************************
--PROJECT_CODE> TBCAM
--DATE>2016/10/13
--AUTHOR> Grace Liu
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBCAM_PROF_INVESTOR_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
TRUNCATE
into table	TBCAM_PROF_INVESTOR_SG
--FIELDS TERMINATED BY X'09'
FIELDS TERMINATED BY ',' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
CUST_ID POSITION(1:11)
,EFF_DATE POSITION(12:19) "TO_DATE(:EFF_DATE,'YYYYMMDD')"
,VERSION "0"
,CREATETIME sysdate
,CREATOR constant "TBCAM_PROF_INVESTOR_SG"
)