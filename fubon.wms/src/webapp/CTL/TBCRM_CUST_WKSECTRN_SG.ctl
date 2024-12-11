--**************************************************************************
--DATE>2024/12/10
--AUTHOR> Sam Tu
--**************************************************************************

--**************************************************************************
--TARGET>TBCRM_CUST_WKSECTRN_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
LOAD DATA
TRUNCATE       
into table TBCRM_CUST_WKSECTRN_SG
--FIELDS TERMINATED BY ','  --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
( 
SEC001 position(1: 12) "trim(:SEC001)"
, SEC002 position(13: 19) "trim(:SEC002)"
, SEC003 position(20: 27) "trim(:SEC003)"
, SEC004 position(28: 29) "trim(:SEC004)"
, SEC005 position(30: 34) "trim(:SEC005)"
, SEC006 position(35: 46) "trim(:SEC006)"
, SEC007 position(47: 49) "trim(:SEC007)"
, SEC008 position(50: 67) "trim(:SEC008)"
, SEC0097 position(68: 75) "trim(:SEC0097)"
, SEC0098 position(76: 81) "trim(:SEC0098)"
, SEC0099 position(82: 91) "trim(:SEC0099)"
, VERSION "0"
, CREATETIME SYSDATE
, CREATOR constant "TBCRM_CUST_WKSECTRN_SG.ctl"
, MODIFIER constant "TBCRM_CUST_WKSECTRN_SG.ctl"
, LASTUPDATE SYSDATE                   
)                                                                                      
               