--**************************************************************************
--PROJECT_CODE> TBCRM
--DATE>2017/01/18
--AUTHOR> Grace Liu
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBCRM_NREMLMP0_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
TRUNCATE       
into table TBCRM_NREMLMP0_SG
FIELDS TERMINATED BY X'09'
--FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
( 
NRE001 POSITION(1:11)
,NRE002 POSITION(12:23)
,NRE003 POSITION(24:24)
,NRE004 POSITION(25:44)
,NRE0051 POSITION(45:50)
,NRE0052 POSITION(51:60)
,NRE0061 POSITION(61:61)
,NRE006 POSITION(62:67) "TO_NUMBER(:NRE006/100)"
,NRE0071 POSITION(68:68)
,NRE007 POSITION(69:74) "TO_NUMBER(:NRE007/100)"
,NRE008 POSITION(75:78)
,NRE009 POSITION(79:138)
,NRE010 POSITION(139:141)
,NRE011 POSITION(142:145)
,NRE012 POSITION(146:155)
,NRE013 POSITION(156:165)
,NRE014 POSITION(166:172)
,NRE015 POSITION(173:178)
,NRE016 POSITION(179:218)
,NRE017 POSITION(219:221)
,VERSION "0"                                                                           
,CREATETIME sysdate                                                                    
,CREATOR constant "TBCRM_NREMLMP0_SG"                                                
)                                                                                      
                                                                                       