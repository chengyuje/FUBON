--**************************************************************************
--PROJECT_CODE> TBPMS
--DATE>2016/11/17
--AUTHOR> Grace Liu
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBPMS_UMS200_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
TRUNCATE
into table	TBPMS_UMS200_SG
--FIELDS TERMINATED BY X'09'
FIELDS TERMINATED BY ',' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
UMG01 POSITION(1:12)
,UMG02 POSITION(13:54)
,UMG03 POSITION(55:64)
,UMG04 POSITION(65:72)
,UMG05 POSITION(73:80)
,UMG06 POSITION(81:88)
,UMG07 POSITION(89:89)
,UMG08 POSITION(90:90)
,UMG90 POSITION(91:190)
,UMG91 POSITION(191:196)
,UMG92 POSITION(197:204)
,UMG93 POSITION(205:214)
,UMG94 POSITION(215:224)
,VERSION "0"
,CREATETIME sysdate
,CREATOR constant "TBPMS_UMS200_SG"
)