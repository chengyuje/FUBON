--**************************************************************************
--PROJECT_CODE> TBCRM
--DATE>2017/01/09
--AUTHOR> Grace Liu
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBCRM_TMRN27WK_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true,skip=1)
LOAD DATA
TRUNCATE       
into table TBCRM_TMRN27WK_SG
FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
( 
WKR2701 POSITION(1:4)     
,WKR2702 POSITION(5:12)
,WKR2703 POSITION(13:22)
,WKR2704 POSITION(23:25)
,WKR2705 POSITION(26:37)
,WKR2706 POSITION(38:51)
,WKR2706X POSITION(52:52)
,WKR2707 POSITION(53:57)
,WKR2708 POSITION(58:71)
,WKR2708X POSITION(72:72)
,WKR2709 POSITION(73:86)
,WKR2710 POSITION(87:126)
,WKR2711 POSITION(127:131)
,WKR2712 POSITION(132:153)
,WKR2713 POSITION(154:160)
,WKR2714 POSITION(161:190)
,WKR2715 POSITION(191:191)
,WKR2716 POSITION(192:197)
,WKR2717 POSITION(198:219)
,WKR2718 POSITION(220:233)
,WKR2719 POSITION(234:247)
,WKR2720 POSITION(248:261)
,WKR2721 POSITION(262:262)
,WKR2722 POSITION(263:274)
,WKR2723 POSITION(275:277)
,VERSION "0"                                                                           
,CREATETIME sysdate                                                                    
,CREATOR constant "TBCRM_TMRN27WK_SG"                                                
)                                                                                      
                                                                                       