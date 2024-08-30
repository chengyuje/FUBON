--**************************************************************************
--PROJECT_CODE> TBPRD
--DATE>2016/12/07
--AUTHOR> Grace Liu
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBPRD_NRCOM4P_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
TRUNCATE
into table TBPRD_NRCOM4P_SG
--FIELDS TERMINATED BY X'09'
FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
COM004010 POSITION(1:2)
,COM004020 POSITION(3:4)
,COM004030 POSITION(5:54)
,COM004991 POSITION(55:68)
,COM004992 POSITION(69:76)
,COM004993 POSITION(77:82)
,COM004994 POSITION(83:96)
,COM004995 POSITION(97:104)
,COM004996 POSITION(105:110)
,VERSION "0"
,CREATETIME SYSDATE
,CREATOR constant "PABTH_BTORG003"
)       