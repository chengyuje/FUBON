--**************************************************************************
--PROJECT_CODE> TBPRD
--DATE>2016/10/31
--AUTHOR> Grace Liu
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBPRD_LIP_FUND_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true,skip=1)
LOAD DATA
TRUNCATE       
into table TBPRD_LIP_FUND_SG
FIELDS TERMINATED BY X'09'
--FIELDS TERMINATED BY ',' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
( 
LIPPERID
,PRIMARYLIPPERID
,LAUNCHDATE "TO_DATE(:LAUNCHDATE,'DD/MM/YYYY')"
,LAUNCHCURRENCYCODE
,LAUNCHPRICE
,LOCALCURRENCYCODE
,DOMICILECODE
,GLOBALCLASSCODE
,LOCALCLASSCODE
,GLOBALCLASSBMLIPPERID
,LOCALCLASSBMLIPPERID
,TNADATE "TO_DATE(:TNADATE,'DD/MM/YYYY')"
,TNAVALUELC
,TNAVALUE
,MININVESTDATE "TO_DATE(:MININVESTDATE,'DD/MM/YYYY')"
,MININVESTINITLC
,MININVESTINIT
,MININVESTREGLC
,MININVESTREG
,MININVESTIRREGLC
,MININVESTIRREG
,MGTCHARGEDATE "TO_DATE(:MGTCHARGEDATE,'DD/MM/YYYY')"
,MGTCHARGEINITIAL
,MGTCHARGEANNUAL
,MGTCHARGEREDEMPTION
,PRICECODE
,PRICELC
,PRICE
,INCOMEDISTCODE
,DIVSPERYEAR
,YIELDDATE "TO_DATE(:YIELDDATE,'DD/MM/YYYY')"
,YIELDVALUE
,YIELDTAXCODE
,INCOMEYTM
,INCOME1Y
,PRICECODE2
,PRICELC2
,PRICE2
,VERSION "0"                                                                           
,CREATETIME sysdate                                                                    
,CREATOR constant "TBPRD_LIP_FUND_SG"                                                
)                                                                                      
                                                                                       