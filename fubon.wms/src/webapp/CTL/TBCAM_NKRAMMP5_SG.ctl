--**************************************************************************
--PROJECT_CODE> TBCAM
--DATE>2016/10/26
--AUTHOR> Grace Liu
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBCAM_NKRAMMP5_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true,skip=1) 
LOAD DATA
TRUNCATE       
into table TBCAM_NKRAMMP5_SG
FIELDS TERMINATED BY ',' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
( 
KAM501 POSITION(1:1)
,KAM502 POSITION(2:12)
,KAM503 POSITION(13:15)
,KAM504 POSITION(16:77)
,KAM505 POSITION(78:99)
,KAM506 POSITION(100:114)
,KAM507 POSITION(115:122)
,KAM508 POSITION(123:129) "TO_NUMBER(:KAM508/100)"
,KAM509 POSITION(130:130)
,KAM510 POSITION(131:131)
,KAM511 POSITION(132:138) "TO_NUMBER(:KAM511/100)"
,KAM512 POSITION(139:139)
,KAM513 POSITION(140:189)
,VERSION "0"                                                                           
,CREATETIME sysdate                                                                    
,CREATOR constant "TBCAM_NKRAMMP5_SG"                                                
)                                                                                      
                                                                                       