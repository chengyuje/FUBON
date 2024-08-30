--**************************************************************************
--PROJECT_CODE> TBPMS
--DATE>2016/10/28
--AUTHOR> Grace Liu
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBPMS_TD021_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
TRUNCATE       
into table TBPMS_TD021_SG
FIELDS TERMINATED BY ',' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
( 
BRH_COD POSITION(1:3)
,SUBJ POSITION(4:6)
,SERIAL POSITION(7:12)
,CUST_ID POSITION(13:23)
,OPN_DTE POSITION(24:31)
,CLS_DTE POSITION(32:39)
,NAME_CD POSITION(40:43)
,TEL_D_CD POSITION(44:47)
,TEL_N_CD POSITION(48:51)
,FAX_CD POSITION(52:55)
,ADDR_CD POSITION(56:59)
,TAX_CD POSITION(60:60)
,YY_F_TAX POSITION(61:64)
,ACNO_PB POSITION(65:76)
,ACNO_FS POSITION(77:88)
,ACT_EXCP POSITION(89:108)
,SLIP_CNT POSITION(109:115)
,UNFIN_CNT POSITION(116:120)
,TX_EMP_ID POSITION(121:126)
,AUT_EMP_ID POSITION(127:132)
,LST_MTN_DATE POSITION(133:140)
,LST_MTN_TIME POSITION(141:146)
,FILLERX POSITION(147:1024)
,VERSION "0"                                                                           
,CREATETIME sysdate                                                                    
,CREATOR constant "TBPMS_TD021_SG"                                                
)                                                                                      
                                                                                       