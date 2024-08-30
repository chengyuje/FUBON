--**************************************************************************
--PROJECT_CODE> TBPMS
--DATE>2018/08/06
--AUTHOR> Kitty Chiang
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBPMS_IMM_SN_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
CHARACTERSET AL32UTF8
TRUNCATE
into table	TBPMS_IMM_SN_SG
--FIELDS TERMINATED BY X'09'
--FIELDS TERMINATED BY ',' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
BDH01 POSITION(1:12)   
,BDH02 POSITION(13:19)    
,BDH03 POSITION(20:32)   
,BDH04 POSITION(33:45)    
,BDH09 POSITION(46:53)   
,BDH10 POSITION(55:61)    
,BDH11 POSITION(74:80) "to_number(:BDH11/1000000000)"   
,BDH12 POSITION(81:81)      
,BD080_01 POSITION(82:86) "to_number(:BD080_01/1000)"     
,BD080_02 POSITION(87:131)      
,MTIME POSITION(132:137)    
,MDATE POSITION(138:145)    
,MUSR POSITION(146:155)     
,MPGM POSITION(156:165)     
,VERSION "0"
,CREATETIME sysdate
,CREATOR constant "TBPMS_IMM_SN_SG"
)