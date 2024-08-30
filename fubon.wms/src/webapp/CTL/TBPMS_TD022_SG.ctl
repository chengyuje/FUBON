--**************************************************************************
--PROJECT_CODE> TBPMS
--DATE>2016/10/30
--AUTHOR> Grace Liu
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBPMS_TD022_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
TRUNCATE       
into table TBPMS_TD022_SG
FIELDS TERMINATED BY ',' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
( 
BRH_COD POSITION(1:3)
,SUBJ POSITION(4:6)
,SERIAL POSITION(7:12)
,SLIP_NO POSITION(13:19)
,CUST_ID POSITION(20:30)
,PROD_ID POSITION(31:49)
,PROD_NAME POSITION(50:111)
,OPN_DTE POSITION(112:119)
,BK_VALUE POSITION(120:127)
,DUE_DTE POSITION(128:135)
,CLS_DTE POSITION(136:143)
,CUR POSITION(144:146)
,OPN_DPR_AMT POSITION(147:160) "TO_NUMBER(:OPN_DPR_AMT/100)"
,TX_BRH POSITION(161:163)
,ACT_BRH POSITION(164:166)
,CLASS POSITION(167:168)
,SUB_CLASS POSITION(169:170)
,HOLD_AMT POSITION(171:184) "TO_NUMBER(:HOLD_AMT/100)"
,TD_LN_AMT POSITION(185:198) "TO_NUMBER(:TD_LN_AMT/100)"
,CRD_APL_DTE POSITION(199:206)
,CRD_RLS_DTE POSITION(207:214)
,CRD_BRH POSITION(215:217)
,SLIP_EXCP POSITION(218:247)
,INC_CNT POSITION(248:252)
,TX_EMP_ID POSITION(253:258)
,AUT_EMP_ID POSITION(259:264)
,LST_MTN_DATE POSITION(265:272)
,LST_MTN_TIME POSITION(273:278)
,FILLERX POSITION(279:1021)
,VERSION "0"                                                                           
,CREATETIME sysdate                                                                    
,CREATOR constant "TBPMS_TD022_SG"                                                
)                                                                                      
                                                                                       