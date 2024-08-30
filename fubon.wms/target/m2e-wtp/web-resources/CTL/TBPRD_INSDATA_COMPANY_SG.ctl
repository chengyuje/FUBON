--**************************************************************************
--PROJECT_CODE> TBPRD
--DATE>2017/08/01
--AUTHOR> Grace Liu
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBPRD_INSDATA_COMPANY_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
TRUNCATE       
into table TBPRD_INSDATA_COMPANY_SG
FIELDS TERMINATED BY ',' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
( 
COM_ID
,COM_NAME
,InsCompanyCode
,COM_TYPE
,InsCompanyShowOrder
,InsCompanyID
,COM_NAME_LONG
,VERSION "0"                                                                           
,CREATETIME sysdate                                                                    
,CREATOR constant "TBPRD_INSDATA_COMPANY_SG"                                                
)                                                                                      
                                                                                       