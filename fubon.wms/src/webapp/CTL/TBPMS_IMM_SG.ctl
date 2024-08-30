--**************************************************************************
--PROJECT_CODE> TBPMS
--DATE>2018/08/06
--AUTHOR> Kitty Chiang
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBPMS_IMM_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
CHARACTERSET AL32UTF8
TRUNCATE
into table	TBPMS_IMM_SG
--FIELDS TERMINATED BY X'09'
--FIELDS TERMINATED BY ',' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
BDEF1 POSITION(1:12)   
,BDEF2 POSITION(13:19)    
,BDEF3 POSITION(20:27)   
,BDEF4 POSITION(28:35)    
,BDEF5 POSITION(36:54) "to_number(:BDEF5/1000000000)"   
,BDEF6 POSITION(55:73) "to_number(:BDEF6/1000000000)"   
,BDEF7 POSITION(74:81) "to_number(:BDEF7/10000000)"   
,BDEF8 POSITION(82:89) "to_number(:BDEF8/10000000)"      
,BDEF9 POSITION(90:97) "to_number(:BDEF9/10000000)"     
,BDEFA POSITION(98:105) "to_number(:BDEFA/10000000)"      
,BDEFB POSITION(106:113) "to_number(:BDEFB/10000000)"      
,BDEFC POSITION(114:118)   
,BDEFD POSITION(119:137) "to_number(:BDEFD/1000000000)"       
,BDEFE POSITION(138:156) "to_number(:BDEFE/1000000000)"      
,BDEFF POSITION(157:164) "to_number(:BDEFF/10000000)"   
,BD056_01 POSITION(165:169)  
,BD056_02 POSITION(170:174) "to_number(:BD056_02/1000)"     
,BD056_03 POSITION(175:214)  
,MTIME POSITION(215:220)    
,MDATE POSITION(221:228)    
,MUSR POSITION(229:238)     
,MPGM POSITION(239:248)     
,VERSION "0"
,CREATETIME sysdate
,CREATOR constant "TBPMS_IMM_SG"
)