--**************************************************************************
--PROJECT_CODE> TBCAM
--DATE>2016/10/26
--AUTHOR> Grace Liu
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBCAM_NFRAMMP5_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true,skip=1) 
LOAD DATA
TRUNCATE       
into table TBCAM_NFRAMMP5_SG
FIELDS TERMINATED BY ',' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
( 
RAM501 POSITION(1:1) "TRIM(:RAM501)"
,RAM502 POSITION(2:12) "TRIM(:RAM502)"
,RAM503 POSITION(13:15) "TRIM(:RAM503)"
,RAM504 POSITION(16:77) "TRIM(:RAM504)"
,RAM505 POSITION(78:99) "TRIM(:RAM505)"
,RAM506 POSITION(100:111) "TRIM(:RAM506)"
,RAM507 POSITION(112:119) "TRIM(:RAM507)"
,RAM508 POSITION(120:126) "TO_NUMBER(:RAM508/100)"
,RAM509 POSITION(127:127) "TRIM(:RAM509)"
,RAM510 POSITION(128:128) "TRIM(:RAM510)"
,RAM511 POSITION(129:135) "TO_NUMBER(:RAM511/100)"
,RAM512 POSITION(136:136) "TRIM(:RAM512)"
,RAM513 POSITION(137:186) "TRIM(:RAM513)"
,VERSION "0"                                                                           
,CREATETIME sysdate                                                                    
,CREATOR constant "TBCAM_NFRAMMP5_SG"                                                
)                                                                                      
                                                                                       