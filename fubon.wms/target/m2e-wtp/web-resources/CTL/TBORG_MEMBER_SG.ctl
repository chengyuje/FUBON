--**************************************************************************
--PROJECT_CODE> TBCAM
--DATE>2016/05/23
--AUTHOR> Grace Liu
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBORG_MEMBER_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true,skip=1)
LOAD DATA
TRUNCATE
into table TBORG_MEMBER_SG
--FIELDS TERMINATED BY X'09'
FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
EFFDT "TO_DATE(:EFFDT,'YYYY/MM/DD AM HH:MI:ss')"
,CHANGEMARK 
,EMP_ID     
,EMPL_RCD   
,TW_PRER_NUM_BU 
,NAME_FORMAL    
,ENG_NAME       
,NAME_AC        
,BIRTHDATE "TO_DATE(:BIRTHDATE,'YYYY/MM/DD AM HH:MI:ss')"      
,SEX 
,SEX_DESCR        
,MAR_STATUS       
,MAR_STATUS_DESCR 
,TW_ORIG_RESD     
,TW_BLOOD_TYPE "TRIM(:TW_BLOOD_TYPE)"
,POSTAL           
,FULLADDRESS1     
,POSTAL2          
,FULLADDRESS2     
,EMAIL_ADDR       
,EMAIL_ADDR2      
,NATIONAL_ID      
,BGSETID    
,BGDEPTID         
,BGCODE           
,BGJOBDESCR       
,BUSETID          
,BUDEPTID         
,BUSINESS_UNIT2   
,BUCODE           
,BUHRGWCODE       
,BUJOBDESCR       
,TW_TITLE_NUM     
,TW_TITLE         
,LOCATION         
,LOCNAME          
,BUORGMANAGER     
,BGORGMANAGER     
,EXPECTED_END_DATE "TO_DATE(:EXPECTED_END_DATE,'YYYY/MM/DD AM HH:MI:ss')"
,JOINDTE "TO_DATE(:JOINDTE,'YYYY/MM/DD AM HH:MI:ss')"
,TERDTE "TO_DATE(:TERDTE,'YYYY/MM/DD AM HH:MI:ss')"
,TW_TITLE_CODE 
,SECOND_LAST_NAME 
,MIDDLE_NAME      
,JPM_CAT_ITEM_ID  
,JPM_CATDESCR     
,BANK_CODE        
,ACCOUNT_EC_ID    
,HR_STATUS        
,TW_TITLE_START_DT "TO_DATE(:TW_TITLE_START_DT,'YYYY/MM/DD AM HH:MI:ss')"
,PHONE       
,EXTENSION   
,VERSION    constant"0"
,CREATETIME sysdate
,CREATOR    constant "TBORG_MEMBER_SG"
,MODIFIER   constant "TBORG_MEMBER_SG"
,LASTUPDATE sysdate
,IDNO 
,HOMEPHONE
,CELLPHONE )