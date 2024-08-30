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
EMP_ID POSITION(1:6) "TRIM(:EMP_ID)"    --���s   
,DETP_ID POSITION(7:10)									--���
,CUST_ID POSITION(11:21)								--�����Ҧr��
,EMP_ENG_NAME POSITION(22:51)           --�^��m�W
,JOB_RANK POSITION(52:52)								--¾��
,JOB_ID POSITION(53:56)									--¾�٥N��
,JOB_TITLE_ID POSITION(57:64)						--�H��¾�٥N��
,JOB_ONBOARD_DATE POSITION(65:71) 			--��¾���(����~7�X)
,NOTE_1 POSITION(72:74) 								--���O
,NOTE_2 POSITION(75:75)									--�N�X   
,PLATE POSITION(76:78)									--�O��
,PLATE_REAL POSITION(76:76)							--�O��(�Ĥ@�X)      
,VERSION "0"                                                                           
,CREATETIME sysdate                                                                    
,CREATOR constant "TBORG_MEMBER_PLATE_SG"                                                
)                                                                                      
                                                                        