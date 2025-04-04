--**************************************************************************
--PROJECT_CODE> TBPMS
--DATE>2016/09/22
--AUTHOR> Grace Liu
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBPMS_BDS122_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
CHARACTERSET AL32UTF8
TRUNCATE
into table	TBPMS_BDS122_SG
--FIELDS TERMINATED BY X'09'
--FIELDS TERMINATED BY ',' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
BD122A POSITION(1:14)   
,BD122B POSITION(15:26)    
,BD122C POSITION(27)   
,BD122D POSITION(28:35)    
,BD122E POSITION(36:46)
,BD122F POSITION(47)   
,BD122G POSITION(48:62)
,BD122H POSITION(63:65)   
,BD122I POSITION(66:78) "to_number(:BD122I/100)"   
,BD122J POSITION(79:88)   
,BD122K POSITION(89)   
,BD122L POSITION(90:93)
,BD122M POSITION(94:101)    
,BD122N POSITION(102:105)   
,BD122O POSITION(106)
,BD122P POSITION(107:122)  
,BD122Q POSITION(123:135) "to_number(:BD122Q/1000)"    
,BD122X POSITION(136:175)
,BD122Y POSITION(176:215)
,BD122Z POSITION(216:255)
,MTIME POSITION(256:261)    
,MDATE POSITION(262:269)    
,MPGM POSITION(270:279)     
,MUSR POSITION(280:290)     
,VERSION "0"
,CREATETIME sysdate
,CREATOR constant "TBPMS_BDS122_SG"
)