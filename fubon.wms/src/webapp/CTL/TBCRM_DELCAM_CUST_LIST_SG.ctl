--**************************************************************************
--PROJECT_CODE> TBCRM
--DATE>2017/04/17
--AUTHOR> Grace Liu
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBCRM_DELCAM_CUST_LIST_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
TRUNCATE       
into table TBCRM_DELCAM_CUST_LIST_SG
FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
( 
SNAP_DATE POSITION(1:8)
,SEQ_NO POSITION(9:21)
,BRH_COD POSITION(22:24)
,CONTACT_ADDRESS POSITION(25:124)
,BRH_OTH POSITION(125:127)
,POINTER_1 POSITION(128:137)
,POINTER_2 POSITION(138:147)
,ADDR_CITY POSITION(148:157)
,ADDR_AREA POSITION(158:167)
,KM_BRH_STR POSITION(168:177)
,KM_BRH_OTH POSITION(178:187)
,BRH_LST POSITION(188:190)
,KM_BRH_LST POSITION(191:200)
,BRH_SEC POSITION(201:203)
,KM_BRH_SEC POSITION(204:213)
,VERSION "0"                                                                           
,CREATETIME sysdate                                                                    
,CREATOR constant "TBCRM_DELCAM_CUST_LIST_SG"                                                
)                                                                                                                                          