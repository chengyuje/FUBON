--**************************************************************************
--PROJECT_CODE> TBCAM
--DATE>2020/11/10
--AUTHOR> Sam Tu
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBCAM_FAIL_BOND_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
TRUNCATE
into table	TBCAM_FAIL_BOND_SG
FIELDS TERMINATED BY ',' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
( 
 WRK01 POSITION(1:11) 
,WRK02 POSITION(12:19) 
,WRK03 POSITION(20:21)  
,WRK04 POSITION(22:33)
,WRK05 POSITION(34:53)      
,WRK06 POSITION(54:56) 
,WRK07 POSITION(57:65)     
,VERSION "0"
,CREATOR constant "BOND_FAIL_1"
,CREATETIME sysdate
,MODIFIER constant "BOND_FAIL_1"
,LASTUPDATE sysdate                                               
)                                  