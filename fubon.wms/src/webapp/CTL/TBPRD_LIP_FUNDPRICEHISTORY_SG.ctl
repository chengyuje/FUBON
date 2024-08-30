--**************************************************************************
--PROJECT_CODE> TBPRD
--DATE>2016/10/31
--AUTHOR> Grace Liu
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBPRD_LIP_FUNDPRICEHISTORY_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true,skip=1)
LOAD DATA
TRUNCATE       
into table TBPRD_LIP_FUNDPRICEHISTORY_SG
FIELDS TERMINATED BY X'09'
--FIELDS TERMINATED BY ',' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
( 
LIPPERID
,DATADATE "TO_DATE(:DATADATE,'DD/MM/YYYY')"
,PRICECODE
,PRICESERIESCODE
,PRICELC
,PRICE
,ESTIMATE
,VERSION "0"                                                                           
,CREATETIME sysdate                                                                    
,CREATOR constant "TBPRD_LIP_FUNDPRICEHISTORY_SG"                                                
)                                                                                      
                                                                                       