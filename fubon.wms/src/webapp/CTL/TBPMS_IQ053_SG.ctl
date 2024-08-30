--**************************************************************************
--PROJECT_CODE> TBPMS
--DATE>2016/08/25
--AUTHOR> Grace Liu
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBPMS_IQ053_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
CHARACTERSET AL32UTF8
TRUNCATE
into table	TBPMS_IQ053_SG
--FIELDS TERMINATED BY X'09'
--FIELDS TERMINATED BY ',' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
MTN_DATE POSITION(1:8) "TO_DATE(:MTN_DATE,'YYYYMMDD')"
,CUR_COD POSITION(9:11)
,BUY_RATE POSITION(12:17) "to_number(:BUY_RATE/10000)" 
,SEL_RATE POSITION(18:23) "to_number(:SEL_RATE/10000)"
,VERSION "0"
,CREATETIME sysdate
,CREATOR constant "TBPMS_IQ053_SG"
)