--**************************************************************************
--PROJECT_CODE> TBCAM
--DATE>2016/09/30
--AUTHOR> Grace Liu
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBPRD_REST_DAY_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
TRUNCATE
into table	TBPRD_REST_DAY_SG
--FIELDS TERMINATED BY X'09'
--FIELDS TERMINATED BY ',' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
"010" POSITION(1:6)
,"020" POSITION(7:12)
,"030" POSITION(13:20)DATE "YYYY/MM/DD"
,"040" POSITION(21:62)
,"050" POSITION(63)
,"991" POSITION(64:77)
,"992" POSITION(78:85) DATE "YYYY/MM/DD"
,"993" POSITION(86:91)
,"994" POSITION(92:97)
,"995" POSITION(98:105)
,"996" POSITION(106:111)
,VERSION "0"
,CREATETIME sysdate
,CREATOR constant "TBPRD_REST_DAY_SG"
)