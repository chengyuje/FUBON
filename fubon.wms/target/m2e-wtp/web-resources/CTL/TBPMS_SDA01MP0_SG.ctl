--**************************************************************************
--PROJECT_CODE> TBCRM
--DATE>2016/10/26
--AUTHOR> Grace Liu
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBCRM_FC005_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
TRUNCATE       
into table TBPMS_SDA01MP0_SG
--FIELDS TERMINATED BY X'09'
--FIELDS TERMINATED BY ',' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
( 
 A01001    POSITION(1:10)
,A01002    POSITION(11:18)
,A01003    POSITION(19:20)
,A01004    POSITION(21:29)
,A01005    POSITION(30:37)
,A01006    POSITION(38:44)
,A01007    POSITION(45:59)
,A01008    POSITION(60:64)
,A01009    POSITION(65:79)
,A01010    POSITION(80:84)
,A01011    POSITION(85:99)
,A01012    POSITION(100:104)
,A01013    POSITION(105:111)
,A01014    POSITION(112:126)
,A01015    POSITION(127:131)
,A01016    POSITION(132:146)
,A01017    POSITION(147:151)
,A01018    POSITION(152:166)
,A01019    POSITION(167:171)
,A01020    POSITION(172:178)
,A01021    POSITION(179:193)
,A01022    POSITION(194:198)
,A01023    POSITION(199:213)
,A01024    POSITION(214:218)
,A01025    POSITION(219:233)
,A01026    POSITION(234:238)
,A01027    POSITION(239:239)
,A01028    POSITION(240:319)
,A01029    POSITION(320:369)
,VERSION "0"                                                                           
,CREATETIME sysdate                                                                    
,CREATOR constant "TBPMS_SDA01MP0_SG"   
)                                                                                      
             