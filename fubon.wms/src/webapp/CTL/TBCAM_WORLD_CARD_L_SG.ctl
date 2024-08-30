--**************************************************************************
--PROJECT_CODE> TBCAM
--DATE>2016/05/23
--AUTHOR> Grace Liu
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBCAM_WORLD_CARD_L_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
TRUNCATE
into table	TBCAM_WORLD_CARD_L_SG
--FIELDS TERMINATED BY X'09'
FIELDS TERMINATED BY ',' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
TYPE POSITION(1:2)
,EMAIL POSITION(3:70)
,ID POSITION(71:81)
,NAME POSITION(82:93)
,EMPID POSITION(94:104)
,CARD_TYPE POSITION(105:110)
,NAME_DISP POSITION(111:122)
,CARD_L4DIGS POSITION(123:144)
,MESSAGE POSITION(145:256)
,VERSION "0"
,CREATETIME sysdate
,CREATOR constant "TBCAM_WORLD_CARD_L_SG"
)