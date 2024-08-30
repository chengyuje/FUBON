--**************************************************************************
--PROJECT_CODE> TBCRM
--DATE>2017/08/13
--AUTHOR> Grace Liu
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBCRM_PS_SA_CUST_CIF_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
TRUNCATE       
into table TBCRM_PS_SA_CUST_CIF_SG
FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
PERSON_ID			
,BRANCH_NBR			
,TOTAL_ASSET_BAL		
,VIP_DEGREE			
,MON_ACCT_SAVING_AU	
,MON_ATERM_DEP_AUM	
,MON_ACCT_CHECK_AUM	
,MON_ASAVING_CUR_AU	
,MON_ACOMP_CUR_AUM	
,MON_TR_DEP_CUR_AUM	
,MON_ACCT_RP_AUM		
,MON_FUND_BD_TP_AUM	
,MON_FUND_OTHER_AUM	
,MON_STRUCTURE_AUM	
,MON_STOCK_FORE_AUM	
,MON_FUND_FORE_AUM	
,MON_INSURANCE_AUM	
,MON_ACCT_LOAN_AUM	
,ROW_LASTMANT_DTTM	"TO_DATE(:ROW_LASTMANT_DTTM,'YYYY-MM-DD HH24:MI:ss')"
,MON_TMS_AUM			
,MON_AUM_16			
,MON_AUM_17			
,MON_AUM_18			
,MON_AUM_19			
,MON_AUM_20			
,MON_AUM_21			
,MON_AUM_22			
,MON_AUM_23			
,MON_AUM_24			
,MON_AUM_25			
,MON_AUM_26			
,MON_AUM_27			
,MON_AUM_28			
,MON_AUM_29			
,MON_AUM_30			
,MON_AUM_31			
,MON_AUM_32			
,MON_AUM_33			
,MON_AUM_34			
,MON_AUM_35			
,MON_AUM_36			
,MON_AUM_37			
,MON_AUM_38			
,MON_AUM_39			
,MON_AUM_40							
,VERSION "0"                                               
,CREATETIME sysdate                             
,CREATOR constant "TBCRM_PS_SA_CUST_CIF_SG"  
)                                               