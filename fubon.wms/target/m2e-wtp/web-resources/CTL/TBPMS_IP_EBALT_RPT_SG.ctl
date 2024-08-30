--**************************************************************************
--PROJECT_CODE> TBPMS
--DATE>2019/03/27
--AUTHOR> Cathy Tang
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBPMS_IP_EBALT_RPT_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
--CHARACTERSET AL32UTF8
TRUNCATE
into table	TBPMS_IP_EBALT_RPT_SG
--FIELDS TERMINATED BY X'09'
FIELDS TERMINATED BY ',' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
SNAP_DATE	
,CUST_ID		
,IP_ADDR		
,TXN_TIME "TO_DATE(:TXN_TIME,'YYYY/MM/DD HH24:MI:ss')"		
,TASK_NM
,VERSION "0"       
,CREATETIME sysdate       
,CREATOR constant "TBPMS_IP_EBALT_RPT_SG"     
,MODIFIER constant "TBPMS_IP_EBALT_RPT_SG"
,LASTUPDATE sysdate  
)
       