--**************************************************************************
--PROJECT_CODE> TBCRM
--DATE>2017/01/12
--AUTHOR> Grace Liu
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBCRM_PS_SA_COM_NS_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
TRUNCATE
into table	TBCRM_PS_SA_COM_NS_SG
--FIELDS TERMINATED BY X'09'
FIELDS TERMINATED BY ',' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
SNAP_DATE "TO_DATE(:SNAP_DATE,'YYYYMMDD')"
,PARTY_ID
,DESCR20
,DESCR_LABEL
,VERSION "0"
,CREATETIME sysdate
,CREATOR constant "TBCRM_PS_SA_COM_NS_SG"
)