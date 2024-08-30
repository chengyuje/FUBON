--**************************************************************************
--PROJECT_CODE> TBPRD
--DATE>2023/08/21
--AUTHOR> Cathy Tang
--PURPOSE>SI FCI Project
--**************************************************************************

--**************************************************************************
--TARGET>TBPRD_FCI_SPOT_DATE_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
TRUNCATE
into table TBPRD_FCI_SPOT_DATE_SG
--FIELDS TERMINATED BY X'09'
FIELDS TERMINATED BY ',' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
CURR_ID
,TRADE_DATE "TO_DATE(:TRADE_DATE,'YYYY/MM/DD')"
,VALUE_DATE "TO_DATE(:VALUE_DATE,'YYYY/MM/DD')"
,MON_DATE_1 "TO_DATE(:MON_DATE_1,'YYYY/MM/DD')"
,MON_DATE_2 "TO_DATE(:MON_DATE_2,'YYYY/MM/DD')"
,MON_DATE_3 "TO_DATE(:MON_DATE_3,'YYYY/MM/DD')"
,MON_DATE_4 "TO_DATE(:MON_DATE_4,'YYYY/MM/DD')"
,MON_DATE_5 "TO_DATE(:MON_DATE_5,'YYYY/MM/DD')"
,MON_DATE_6 "TO_DATE(:MON_DATE_6,'YYYY/MM/DD')"
,MON_DATE_7 "TO_DATE(:MON_DATE_7,'YYYY/MM/DD')"
,MON_DATE_8 "TO_DATE(:MON_DATE_8,'YYYY/MM/DD')"
,MON_DATE_9 "TO_DATE(:MON_DATE_9,'YYYY/MM/DD')"
,MON_DATE_10 "TO_DATE(:MON_DATE_10,'YYYY/MM/DD')"
,MON_DATE_11 "TO_DATE(:MON_DATE_11,'YYYY/MM/DD')"
,MON_DATE_12 "TO_DATE(:MON_DATE_12,'YYYY/MM/DD')"
,VERSION "0"       
,CREATETIME SYSDATE       
,CREATOR constant "TBPRD_FCI_SPOT_DATE_SG"       
)
       