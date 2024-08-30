--**************************************************************************
--PROJECT_CODE> TBPRD
--DATE>2017/08/01
--AUTHOR> Grace Liu
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBPRD_INSDATA_PROD_MAIN_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
TRUNCATE       
into table TBPRD_INSDATA_PROD_MAIN_SG
FIELDS TERMINATED BY ',' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
PRD_ID            
,PRD_NAME          
,COM_ID            
,IS_MAIN           
,IS_SALE           
,IS_WL             
,WL_TERM           
,INV_SDATE         
,INV_EDATE         
,CONTENT_FILENAME  
,CLAUSE_FILENAME   
,INVEST_TYPE       
,QID               
,ITEM_Y            
,ITEM_A            
,ITEM_X            
,ITEM_K            
,ITEM_P            
,ITEM_U            
,ITEM_O            
,LIST_Y            
,LIST_A            
,LIST_X            
,LIST_K            
,LIST_P            
,LIST_U            
,LIST_O            
,PROD_MODEL        
,IS_OVERSEA        
,CURR_CD           
,QUANTITY_STYLE    
,MENU_TYPE         
,MENU_ITEM         
,MENU_LIST
,INSDATA_LASTUPDATE "TO_DATE(:INSDATA_LASTUPDATE,'YYYY/MM/DD')"        
,TYPE_VALUE       
,TYPE_VALUE1       
,TYPE_VALUE2       
,COVERCACULUNIT    
,COVERCACULUNITDESC
,IFCHS        
,MENU_TITLE     
,VERSION "0"                                               
,CREATETIME sysdate                             
,CREATOR constant "TBPRD_INSDATA_PROD_MAIN_SG"  
)                                               