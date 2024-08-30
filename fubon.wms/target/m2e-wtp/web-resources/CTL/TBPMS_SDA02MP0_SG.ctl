--**************************************************************************
--PROJECT_CODE> TBCRM
--DATE>2017/04/28
--AUTHOR> BIBBY
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBPMS_SDA02MP0_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
TRUNCATE       
into table TBPMS_SDA02MP0_SG
--FIELDS TERMINATED BY X'09'
--FIELDS TERMINATED BY ',' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
( 
 A02001	POSITION(1:10)
,A02002	POSITION(11:13)
,A02003	POSITION(14:21)
,A02004	POSITION(22:29)
,A02005	POSITION(30:30)
,A02006	POSITION(31:45)
,A02007	POSITION(46:55)
,A02008	POSITION(56:66)
,A02009	POSITION(67:74)
,A02010	POSITION(75:76)
,A02011	POSITION(77:77)
,A02012	POSITION(78:78)
,A02013	POSITION(79:86)
,A02014	POSITION(87:94)
,A02015	POSITION(95:101)
,A02016	POSITION(102:104)
,A02017	POSITION(105:154)
,VERSION "0"                                                                           
,CREATETIME sysdate                                                                    
,CREATOR constant "TBPMS_SDA01MP0_SG"   
)                                                                                      
             