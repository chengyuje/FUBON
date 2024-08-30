--**************************************************************************
--PROJECT_CODE> TBCAM
--DATE>2021/12/20
--AUTHOR>Ocean Lin
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBCAM_LNM3130M_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
TRUNCATE
INTO TABLE TBCAM_LNM3130M_SG
FIELDS TERMINATED BY ';' OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
 DATA_DATE     
,USER_CUST_ID  
,USER_NAME     
,LN_ACNO       
,STR_DATE      
,LOAN_BAL      
,PENL_EDATE    
,NEXT_EFF_SDATE
,FRATE         
,NEXT_FRATE    
,NEXT_AMT      
,TEL_NO        
,EXT           
,CELL_NO       
,USER_EMAIL    
,CENTER_COD    
,VERSION   	constant"0"
,CREATETIME	sysdate
,CREATOR   	constant "TBCRM_PY2011O_SG"
,MODIFIER  	constant "TBCRM_PY2011O_SG"
,LASTUPDATE	sysdate
)