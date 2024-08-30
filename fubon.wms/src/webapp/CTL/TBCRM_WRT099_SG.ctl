--**************************************************************************
--PROJECT_CODE> TBCRM
--DATE>2017/01/09
--AUTHOR> Grace Liu
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBCRM_WRT099_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true,skip=1)
LOAD DATA
TRUNCATE       
into table TBCRM_WRT099_SG
FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
WRT01 POSITION(1:2)
,WRT02 POSITION(3:14)
,WRT03 POSITION(15:16)
,WRT04 POSITION(17:18)
,WRT05 POSITION(19:26)
,WRT06 POSITION(27:40)
,WRT07 POSITION(41:52)
,WRT08 POSITION(53:60)
,WRT09 POSITION(61:62)
,WRT10 POSITION(63:65)
,WRT11 POSITION(66:79)
,WRT12 POSITION(80:93)
,WRT13 POSITION(94:107)
,WRT14 POSITION(108:118)
,WRT15 POSITION(119:129)
,WRT16 POSITION(130:140)
,WRT17 POSITION(141:151)
,WRT18 POSITION(152:170)
,WRT19 POSITION(171:184) "TO_NUMBER(:WRT19/100)"
,WRT191 POSITION(185:185)
,WRT20 POSITION(186:204)
,WRT21 POSITION(205:206)
,WRT97 POSITION(207:218)
,WRT98 POSITION(219:233)
,WRT991 POSITION(234:236)
,WRT038 POSITION(237:237)
,WRT99 POSITION(238:257)
,VERSION "0"                                                                           
,CREATETIME sysdate                                                                    
,CREATOR constant "TBCRM_WRT099_SG"                                                
)                                                                                      
                                                                                       