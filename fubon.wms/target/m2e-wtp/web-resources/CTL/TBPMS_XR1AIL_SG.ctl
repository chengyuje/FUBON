--**************************************************************************
--PROJECT_CODE> TBPMS
--DATE>2016/11/17
--AUTHOR> Grace Liu
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBPMS_XR1AIL_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
TRUNCATE
into table	TBPMS_XR1AIL_SG
--FIELDS TERMINATED BY X'09'
FIELDS TERMINATED BY ',' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
XR101 POSITION(1:8)
,XR102 POSITION(9:19)
,XR103 POSITION(20:21)
,XR104 POSITION(22:25)
,XR105 POSITION(26:29)
,XR107 POSITION(30:38)
,XR112 POSITION(39:52) "TO_NUMBER(:XR112/100)"
,XR116 POSITION(53:63) "TO_NUMBER(:XR116/100)"
,XR120 POSITION(64:74) "TO_NUMBER(:XR120/100)"
,XR133 POSITION(75:85) "TO_NUMBER(:XR133/100)"
,XR135 POSITION(86:94) "TO_NUMBER(:XR135/100)"
,XR137 POSITION(95:103)
,XR199 POSITION(104:133)
,VERSION "0"
,CREATETIME sysdate
,CREATOR constant "TBPMS_XR1AIL_SG"
)