--**************************************************************************
--PROJECT_CODE> TBKYC
--DATE>2020/12/03
--AUTHOR> Sam Tu
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBKYC_PSR_SUM_MKT_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
TRUNCATE       
into table TBKYC_PSR_SUM_MKT_SG
FIELDS TERMINATED BY '|' OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
( 
PSR01
,PSR02
,PSR03
,PSR04
,PSR05
,PSR06
,PSR07
,PSR08
,PSR09
,PSR10
,PSR11
,PSR12
,PSR13
,PSR14
,PSR15
,PSR16
,PSR17
,PSR18
,PSR19
,PSR20
,PSR21
,PSR22
,PSR23
,PSR24
,PSR25
,PSR26
,PSR27
,PSR28            
,VERSION "0"
,CREATOR "'CTLFILE_INSERT'"
,CREATETIME SYSDATE     
)                                                                                      
                                                                                       