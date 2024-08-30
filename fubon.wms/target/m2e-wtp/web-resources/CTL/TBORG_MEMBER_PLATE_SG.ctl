--**************************************************************************
--PROJECT_CODE> TBORG
--DATE>2017/06/09
--AUTHOR> BIBBY
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBORG_MEMBER_PLATE_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
TRUNCATE       
into table TBORG_MEMBER_PLATE_SG
FIELDS TERMINATED BY ',' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
( 
EMP_ID POSITION(1:6) "TRIM(:EMP_ID)"    --員編   
,DETP_ID POSITION(7:10)									--單位
,CUST_ID POSITION(11:21)								--身份證字號
,EMP_ENG_NAME POSITION(22:51)           --英文姓名
,JOB_RANK POSITION(52:52)								--職等
,JOB_ID POSITION(53:56)									--職稱代號
,JOB_TITLE_ID POSITION(57:64)						--人資職稱代號
,JOB_ONBOARD_DATE POSITION(65:71) 			--任職日期(民國年7碼)
,NOTE_1 POSITION(72:74) 								--註記
,NOTE_2 POSITION(75:75)									--代碼   
,PLATE POSITION(76:78)									--板塊
,PLATE_REAL POSITION(76:76)							--板塊(第一碼)      
,VERSION "0"                                                                           
,CREATETIME sysdate                                                                    
,CREATOR constant "TBORG_MEMBER_PLATE_SG"                                                
)                                                                                      
                                                                        