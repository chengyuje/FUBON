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
OPTIONS (direct=true,skip=0)
LOAD DATA
TRUNCATE       
into table TBPMS_DAY_CLOSE_SG
--FIELDS TERMINATED BY WHITESPACE
FIELDS TERMINATED BY ',' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
( 
CESS_NO 			"TRIM(:CESS_NO)"
,WORK_YY            "TRIM(:WORK_YY)"
,WORK_MM            "TRIM(:WORK_MM)"
,UNIT_CODE          "TRIM(:UNIT_CODE)"
,POLICY_NO          "TRIM(:POLICY_NO)"
,ID_DUP 			"NVL(:ID_DUP,' ')"   
,POLICY_SEQ			"TRIM(:POLICY_SEQ)"
,ID_NO     			"TRIM(:ID_NO)"
,INS_NAME           "TRIM(:INS_NAME)"
,APPL_ID            "TRIM(:APPL_ID)"
,APPL_NAME          "TRIM(:APPL_NAME)"
,EFF_YY             "TRIM(:EFF_YY)"
,EFF_MM             "TRIM(:EFF_MM)"
,EFF_DD             "TRIM(:EFF_DD)"
,RECEIPT_TP         "TRIM(:RECEIPT_TP)"
,RECEIPT_NO         "TRIM(:RECEIPT_NO)"
,ITEM               "TRIM(:ITEM)"
,PREM_YEAR          "TRIM(:PREM_YEAR)"
,POLI_YEAR          "TRIM(:POLI_YEAR)"
,POLI_PERD          "TRIM(:POLI_PERD)"
,PREM               "TRIM(:PREM)"
,COMIS              "TRIM(:COMIS)"
,PREM_Y             "TRIM(:PREM_Y)"
,COMIS_Y            "TRIM(:COMIS_Y)"
,REL_PREM           "TRIM(:REL_PREM)"
,AMOUNT             "TRIM(:AMOUNT)"
,PRFM_COMU          "TRIM(:PRFM_COMU)"
,PRFM_RATE          "TRIM(:PRFM_RATE)"
,SAVE_COMIS         "TRIM(:SAVE_COMIS)"
,SERV_RATE          "TRIM(:SERV_RATE)"
,EMPY_ID            "TRIM(:EMPY_ID)"
,ORIG_ID            "TRIM(:ORIG_ID)"
,COMU_YY            "TRIM(:COMU_YY)"
,COMU_MM            "TRIM(:COMU_MM)"
,COMU_DD            "TRIM(:COMU_DD)"
,AGNT_NAME          "TRIM(:AGNT_NAME)"
,ORIG_NAME          "TRIM(:ORIG_NAME)"
,MOP                "TRIM(:MOP)"
,SA                 "TRIM(:SA)"
,END_YY             "TRIM(:END_YY)"
,END_MM             "TRIM(:END_MM)"
,END_DD             "TRIM(:END_DD)"
,CHECK_YY           "TRIM(:CHECK_YY)"
,CHECK_MM           "TRIM(:CHECK_MM)"
,CHECK_DD           "TRIM(:CHECK_DD)"
,RESOURCES          "TRIM(:RESOURCES)"
,COMU_RATE          "TRIM(:COMU_RATE)"
,CUR_PERD           "TRIM(:CUR_PERD)"
,PREMF              "TRIM(:PREMF)"
,CURRENCY           "TRIM(:CURRENCY)"
,DECL_ECHG          "TRIM(:DECL_ECHG)"
,EMPY_YN            "TRIM(:EMPY_YN)"
,DEPT_CODE          "TRIM(:DEPT_CODE)"
,CNCT_STAT          "TRIM(:CNCT_STAT)"
,DISC_HISA          "TRIM(:DISC_HISA)"
,PREM_KIND          "TRIM(:PREM_KIND)"
,PRIZE              "TRIM(:PRIZE)"
,BT_AMT             "TRIM(:BT_AMT)"
,IS_CONSENT         "TRIM(:IS_CONSENT)"
,VERSION "0"                                                  
,CREATETIME sysdate                                                                    
,CREATOR constant "TBPMS_DAY_CLOSE_SG"                                                
)                                                                                      
                                                                                       