--**************************************************************************
--PROJECT_CODE> TBPMS
--DATE>2017/04/13
--AUTHOR> Grace Liu
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBPMS_SDINVMP0_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
TRUNCATE
into table	TBPMS_SDINVMP0_SG
--FIELDS TERMINATED BY X'09'
FIELDS TERMINATED BY ',' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
SNAP_DATE POSITION(1:8) "TO_DATE(:SNAP_DATE,'YYYYMMDD')"
,SDPRD POSITION(9:18)
,IVBRH POSITION(19:21)
,IVRDTE POSITION(22:29) "TO_DATE(:IVRDTE,'YYYYMMDD')"
,IVID POSITION(30:40)
,IVCUCY POSITION(41:43)
,IVTDAC POSITION(44:55)
,IVTDNO POSITION(56:62)
,IVAMT2 POSITION(63:77) "TO_NUMBER(:IVAMT2/100)"
,VERSION "0"
,CREATETIME sysdate
,CREATOR constant "TBPMS_SDINVMP0_SG"
)