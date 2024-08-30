--**************************************************************************
--PROJECT_CODE> DATASTAGE_VIP
--DATE>2018/04/17
--AUTHOR> Eli Huang
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>DATASTAGE_VIP_TABLE_TW
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
LOAD DATA
TRUNCATE       
into table DATASTAGE_VIP_TABLE_TW
FIELDS TERMINATED BY ','  --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
( 
CUST_ID position(1: 11) "trim(:CUST_ID)"                       
)                                                                                      
                                                                                                                                                             