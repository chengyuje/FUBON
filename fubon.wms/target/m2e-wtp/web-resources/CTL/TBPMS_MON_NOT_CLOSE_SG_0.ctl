--**************************************************************************
--PROJECT_CODE> TBPMS
--DATE>2017/01/03
--AUTHOR> Grace Liu
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBPMS_MON_NOT_CLOSE_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true,skip=0)
LOAD DATA
TRUNCATE       
into table TBPMS_MON_NOT_CLOSE_SG
FIELDS TERMINATED BY ',' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
( 
APPLY_NO  			"TRIM(:APPLY_NO)"
,POLICY_NO          "TRIM(:POLICY_NO)"
,ID_DUP 			"NVL(:ID_DUP,' ')"  
,POLICY_SEQ			"TRIM(:POLICY_SEQ)"
,EFF_YY             "TRIM(:EFF_YY)"
,EFF_MM             "TRIM(:EFF_MM)"
,EFF_DD             "TRIM(:EFF_DD)"
,POLI_UNIT          "TRIM(:POLI_UNIT)"
,RECEIPT_TP         "TRIM(:RECEIPT_TP)"
,RECEIPT_NO         "TRIM(:RECEIPT_NO)"
,COMU_TRANS         "TRIM(:COMU_TRANS)"
,ITEM               "TRIM(:ITEM)"
,PREM_YEAR          "TRIM(:PREM_YEAR)"
,PL_CODE            "TRIM(:PL_CODE)"
,BONU_REMRK         "TRIM(:BONU_REMRK)"
,POLI_YEAR          "TRIM(:POLI_YEAR)"
,POLI_PERD          "TRIM(:POLI_PERD)"
,IS_CONSENT         "TRIM(:IS_CONSENT)"
,POLICY_STATUS 		"TRIM(:POLICY_STATUS)"
,ACPT_YY  			"TRIM(:ACPT_YY)"
,ACPT_MM            "TRIM(:ACPT_MM)"
,ACPT_DD            "TRIM(:ACPT_DD)"
,PREM               "TRIM(:PREM)"
,SA                 "TRIM(:SA)"
,OVER_PREM          "TRIM(:OVER_PREM)"
,COMIS              "TRIM(:COMIS)"
,OVER_COMU          "TRIM(:OVER_COMU)"
,SAVE_COMIS         "TRIM(:SAVE_COMIS)"
,CURRENCY           "TRIM(:CURRENCY)"
,DECL_ECHG          "TRIM(:DECL_ECHG)"
,EXCH_YY            "TRIM(:EXCH_YY)"
,EXCH_MM            "TRIM(:EXCH_MM)"
,EXCH_DD            "TRIM(:EXCH_DD)"
,PREMF              "TRIM(:PREMF)"
,OVER_PREMF         "TRIM(:OVER_PREMF)"
,EMPY_ID            "TRIM(:EMPY_ID)"
,COMU_RATE          "TRIM(:COMU_RATE)"
,AGENT_NBR          "TRIM(:AGENT_NBR)"
,SPEC_RATE          "TRIM(:SPEC_RATE)"
,RESOURCE_NO        "TRIM(:RESOURCE_NO)"
,GEN_YY             "TRIM(:GEN_YY)"
,GEN_MM             "TRIM(:GEN_MM)"
,GEN_DD             "TRIM(:GEN_DD)"
,MAIN_RIDR          "TRIM(:MAIN_RIDR)"
,MOP                "TRIM(:MOP)"
,MAIN_ITEM          "TRIM(:MAIN_ITEM)"
,MAIN_YEAR          "TRIM(:MAIN_YEAR)"
,EMPY_YN            "TRIM(:EMPY_YN)"
,DEPT_CODE          "TRIM(:DEPT_CODE)"
,GROUP_CODE         "TRIM(:GROUP_CODE)"
,PAY_KIND           "TRIM(:PAY_KIND)"
,PREM_RT_B          "TRIM(:PREM_RT_B)"
,PREM_RT_O          "TRIM(:PREM_RT_O)"
,NB_CHANGE          "TRIM(:NB_CHANGE)"
,SYSTEM_OP          "TRIM(:SYSTEM_OP)"
,AGE                "TRIM(:AGE)"
,RELA               "TRIM(:RELA)"
,CUR_PERD           "TRIM(:CUR_PERD)"
,PREM_KIND          "TRIM(:PREM_KIND)"
,FILLER_A           "TRIM(:FILLER_A)"
,FILLER_B           "TRIM(:FILLER_B)"
,FILLER_C           "TRIM(:FILLER_C)"
,FILLER_1           "TRIM(:FILLER_1)"
,FILLER_2           "TRIM(:FILLER_2)"
,FILLER_3           "TRIM(:FILLER_3)"
,FILLER_4           "TRIM(:FILLER_4)"
,FILLER_5           "TRIM(:FILLER_5)"
,FILLER_6           "TRIM(:FILLER_6)"
,FILLER_7           "TRIM(:FILLER_7)"
,FILLER_8           "TRIM(:FILLER_8)"
,FILLER_9           "TRIM(:FILLER_9)"
,FILLER_10          "TRIM(:FILLER_10)"
,ACCT_YY            "TRIM(:ACCT_YY)"
,ACCT_MM            "TRIM(:ACCT_MM)"
,ACCT_DD            "TRIM(:ACCT_DD)"
,HOLD_YY1           "TRIM(:HOLD_YY1)"
,HOLD_MM1           "TRIM(:HOLD_MM1)"
,HOLD_DD1           "TRIM(:HOLD_DD1)"
,HOLD_YY2           "TRIM(:HOLD_YY2)"
,HOLD_MM2           "TRIM(:HOLD_MM2)"
,HOLD_DD2           "TRIM(:HOLD_DD2)"
,HOLD_YY3           "TRIM(:HOLD_YY3)"
,HOLD_MM3           "TRIM(:HOLD_MM3)"
,HOLD_DD3           "TRIM(:HOLD_DD3)"
,VERSION 			"0"                                                                           
,CREATETIME 		sysdate                                                                    
,CREATOR 			constant "TBPMS_MON_NOT_CLOSE_SG"                                                
)                                                                                      
                                                                                       