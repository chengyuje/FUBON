--**************************************************************************
--PROJECT_CODE> TBPMS
--DATE>2017/01/03
--AUTHOR> Grace Liu
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBPMS_DAY_CLOSE_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
TRUNCATE       
into table TBPMS_DAY_CLOSE_SG
--FIELDS TERMINATED BY WHITESPACE
FIELDS TERMINATED BY ',' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
( 
CESS_NO 
,WORK_YY 
,WORK_MM 
,UNIT_CODE
,POLICY_NO
,ID_DUP "NVL(:ID_DUP,' ')"   
,POLICY_SEQ
,ID_NO     
,INS_NAME
,APPL_ID 
,APPL_NAME
,EFF_YY   
,EFF_MM   
,EFF_DD    
,RECEIPT_TP
,RECEIPT_NO
,ITEM      
,PREM_YEAR 
,POLI_YEAR
,POLI_PERD
,PREM    
,COMIS   
,PREM_Y  
,COMIS_Y 
,REL_PREM
,AMOUNT   
,PRFM_COMU
,PRFM_RATE
,SAVE_COMIS
,SERV_RATE 
,EMPY_ID 
,ORIG_ID 
,COMU_YY 
,COMU_MM 
,COMU_DD 
,AGNT_NAME
,ORIG_NAME
,MOP      
,SA      
,END_YY  
,END_MM  
,END_DD  
,CHECK_YY
,CHECK_MM
,CHECK_DD
,RESOURCES 
,COMU_RATE
,CUR_PERD 
,PREMF   
,CURRENCY
,DECL_ECHG
,EMPY_YN  
,DEPT_CODE
,CNCT_STAT
,DISC_HISA
,PREM_KIND
,PRIZE    
,BT_AMT   
,VERSION "0"                                                                           
,CREATETIME sysdate                                                                    
,CREATOR constant "TBPMS_DAY_CLOSE_SG"                                                
)                                                                                      
                                                                                       