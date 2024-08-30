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
into table TBCRM_FC005_SG
FIELDS TERMINATED BY ',' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
( 
CUST_NO POSITION(1:11) "TRIM(:CUST_NO)"     
,ACT_TYP POSITION(12:15)
,BRH_COD POSITION(16:18)
,ACT_NO POSITION(19:38) "TRIM(:ACT_NO)"
,ACT_STS POSITION(39:39)
,NAME_COD POSITION(40:43)
,ADDR_COD POSITION(44:47)
,OPN_DATE POSITION(48:55) 
,CLS_DATE POSITION(56:63) 
,TELD_COD POSITION(64:67)
,TELN_COD POSITION(68:71)
,FAX_COD POSITION(72:75)
,CLASS POSITION(76:77)
,SUB_CLASS POSITION(78:79)
,TBR_BRH POSITION(80:82)
,TBR_ID FILLER --POSITION(83:93)
,TRANS_SN POSITION(94:108)
,TRANS_ROWS POSITION(109:123)
,TRANS_ACTION POSITION(124:124)
,ERROR_CHECK POSITION(125:125)
,VERSION "0"                                                                           
,CREATETIME sysdate                                                                    
,CREATOR constant "TBCRM_FC005_SG"                                                
)                                                                                      
                                                                                       