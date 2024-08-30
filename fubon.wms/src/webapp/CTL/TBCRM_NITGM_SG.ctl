--**************************************************************************
--PROJECT_CODE> TBIOT
--DATE>2016/09/01
--AUTHOR> Grace Liu
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBCRM_NITGM_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
CHARACTERSET AL32UTF8
TRUNCATE
into table	TBCRM_NITGM_SG
--FIELDS TERMINATED BY X'09'
FIELDS TERMINATED BY ',' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
POLICY_NO
,POLICY_SEQ
,ID_DUP "NVL(:ID_DUP,' ')"
,INV_TARGET_NO
,TOTAL_UNITS
,ACC_TYPE
,INV_TARGET_CUR
,VERSION "0"       
,CREATETIME sysdate       
,CREATOR constant "TBCRM_NITGM_SG"       
)
       