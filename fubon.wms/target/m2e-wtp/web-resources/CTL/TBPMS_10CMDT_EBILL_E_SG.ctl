--**************************************************************************
--PROJECT_CODE> WMS
--DATE> 2019/11/28
--AUTHOR> Steven
--PURPOSE> 銀行對帳單-客戶資料
--**************************************************************************

--**************************************************************************
--TARGET> TBPMS_10CMDT_EBILL_E_SG
--TRUNCATE_BEFORE_INSERT> Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (DIRECT=TRUE, SKIP=1)
LOAD DATA
CHARACTERSET AL32UTF8
TRUNCATE
INTO TABLE TBPMS_10CMDT_EBILL_E_SG
WHEN (01) <> '9' --除去最後一筆為9開頭資料
FIELDS TERMINATED BY '||' -- 以 || 分隔資料

-- 將欄位都去掉空白
TRAILING NULLCOLS
(   	CUST_NO  	"TRIM(:CUST_NO  )"
	, 	TYPE     	"TRIM(:TYPE     )"
	, 	CUST_ID  	"TRIM(:CUST_ID  )"
	, 	BRH_COD  	"TRIM(:BRH_COD  )"
	, 	ADDR_COD 	"TRIM(:ADDR_COD )"
	, 	RMK      	"TRIM(:RMK      )"
	, 	DATA_YM  	"TRIM(:DATA_YM  )"
	, 	BILL_BRH 	"TRIM(:BILL_BRH )"
	, 	ZIP_COD  	"TRIM(:ZIP_COD  )"
	, 	ADDR_1   	"TRIM(:ADDR_1   )"
	, 	ADDR_2   	"TRIM(:ADDR_2   )"
	, 	NAME     	"TRIM(:NAME     )"
	, 	TITLE    	"TRIM(:TITLE    )"
	, 	STR_DATE 	"TRIM(:STR_DATE )"
	, 	END_DATE 	"TRIM(:END_DATE )"
	, 	AO_PLACE 	"TRIM(:AO_PLACE )"
	, 	AO_NAME  	"TRIM(:AO_NAME  )"
	, 	BRH_TEL  	"TRIM(:BRH_TEL  )"
	, 	VIEW_01  	"TRIM(:VIEW_01  )"
	, 	VIEW_A01 	"TRIM(:VIEW_A01 )"
	, 	VIEW_A02 	"TRIM(:VIEW_A02 )"
	, 	VIEW_02  	"TRIM(:VIEW_02  )"
	, 	VIEW_B01 	"TRIM(:VIEW_B01 )"
	, 	VIEW_C01 	"TRIM(:VIEW_C01 )"
	, 	VIEW_D01 	"TRIM(:VIEW_D01 )"
	, 	VIEW_E01 	"TRIM(:VIEW_E01 )"
	, 	VIEW_F01 	"TRIM(:VIEW_F01 )"
	, 	VIEW_G01 	"TRIM(:VIEW_G01 )"
	, 	VIEW_O01 	"TRIM(:VIEW_O01 )"
	, 	VIEW_R01 	"TRIM(:VIEW_R01 )"
	, 	VIEW_Q01 	"TRIM(:VIEW_Q01 )"
	, 	VIEW_H   	"TRIM(:VIEW_H   )"
	, 	VIEW_I   	"TRIM(:VIEW_I   )"
	, 	VIEW_I01 	"TRIM(:VIEW_I01 )"
	, 	VIEW_I02 	"TRIM(:VIEW_I02 )"
	, 	VIEW_03  	"TRIM(:VIEW_03  )"
	, 	VIEW_K01 	"TRIM(:VIEW_K01 )"
	, 	VIEW_L01 	"TRIM(:VIEW_L01 )"
	, 	VIEW_K02 	"TRIM(:VIEW_K02 )"
	, 	VIEW_K03 	"TRIM(:VIEW_K03 )"
	, 	VIEW_K04 	"TRIM(:VIEW_K04 )"
	, 	VIEW_L02 	"TRIM(:VIEW_L02 )"
	, 	VIEW_M   	"TRIM(:VIEW_M   )"
	, 	VIEW_N   	"TRIM(:VIEW_N   )"
	, 	VIEW_P   	"TRIM(:VIEW_P   )"
	, 	VIEW_04  	"TRIM(:VIEW_04  )"
	, 	MSG      	"TRIM(:MSG      )"
	, 	AD       	"TRIM(:AD       )"
	, 	NOTE     	"TRIM(:NOTE     )"
	, 	CLASS    	"TRIM(:CLASS    )"
	, 	BLO_BRH  	"TRIM(:BLO_BRH  )"
	, 	SEQ_NO   	"TRIM(:SEQ_NO   )"
	, 	CUS_CLASS	"TRIM(:CUS_CLASS)"
	, 	FILLER   	"TRIM(:FILLER   )"
	, VERSION      "0"
	, CREATOR      CONSTANT "TBPMS_10CMDT_EBILL_E_SG"
	, CREATETIME   SYSDATE
	, MODIFIER     CONSTANT "TBPMS_10CMDT_EBILL_E_SG"
	, LASTUPDATE   SYSDATE  )
