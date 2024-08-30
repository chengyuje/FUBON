--**************************************************************************
--PROJECT_CODE> TBPMS
--DATE>2016/11/01
--AUTHOR> Grace Liu
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBPMS_SC071_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
TRUNCATE       
into table TBPMS_SC071_SG
FIELDS TERMINATED BY ',' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
( 
BRH_COD POSITION(1:3)
,SUBJ POSITION(4:6)
,SERIAL POSITION(7:12)
,TX_DATE POSITION(13:20)
,SEQ_NO POSITION(21:29)
,TX_TIME POSITION(30:35)
,TX_BRH POSITION(36:38)
,TX_COD POSITION(39:44)
,TX_SRL POSITION(45:52)
,TX_MODE POSITION(53:53)
,DRCR POSITION(54:54)
,CUR POSITION(55:57)
,AMT POSITION(58:72) "TO_NUMBER(:AMT/100)"
,BAL POSITION(73:87) "TO_NUMBER(:BAL/100)"
,MEMO POSITION(88:95)
,RMK POSITION(96:111)
,ACT_DATE POSITION(112:119)
,TX_SRC POSITION(120:120)
,TX_TYPE POSITION(121:121)
,ACNO_AMT POSITION(122:136) "TO_NUMBER(:ACNO_AMT/100)"
,ACNO_SA POSITION(137:148)
,PRICE POSITION(149:163) "TO_NUMBER(:PRICE/100)"
,DISCOUNT POSITION(164:171) "TO_NUMBER(:DISCOUNT/100)"
,PRICE_BEF POSITION(172:186) "TO_NUMBER(:PRICE_BEF/100)"
,PRICE_DIF POSITION(187:201) "TO_NUMBER(:PRICE_DIF/100)"
,T_FEE POSITION(202:216) "TO_NUMBER(:T_FEE/100)"
,T_CNT POSITION(217:229)
,T_TYPE POSITION(230:230)
,EC_FLG POSITION(231:231)
,AVG_BEF POSITION(232:246) "TO_NUMBER(:AVG_BEF/100)"
,AVG_AFT POSITION(247:261) "TO_NUMBER(:AVG_AFT/100)"
,T_YIELD POSITION(262:276) "TO_NUMBER(:T_YIELD/100)"
,TO_BOT POSITION(277:277)
,TOTAL_DIFF POSITION(278:292) "TO_NUMBER(:TOTAL_DIFF/100)"
,SPV_NO POSITION(293:298)
,EMP_NO POSITION(299:304)
,RESER_MEMO POSITION(305:324)
,FILLERX POSITION(325:1024)
,VERSION "0"                                                                           
,CREATETIME sysdate                                                                    
,CREATOR constant "TBPMS_SC071_SG"                                                
)                                                                                      
                                                                                       