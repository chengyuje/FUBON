--**************************************************************************
--PROJECT_CODE> TBJSB
--DATE>20220901
--AUTHOR> Betty
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBJSB_INS_PROD_MAIN_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true,skip=1) 
LOAD DATA
CHARACTERSET AL32UTF8
TRUNCATE       
into table TBJSB_INS_PROD_MAIN_SG
fields terminated by '|' --OPTIONALLY ENCLOSED BY '"'
trailing nullcols
           ( PRODUCTSERIALNUM    "CASE WHEN :PRODUCTSERIALNUM = 'NULL' THEN NULL ELSE RTRIM(:PRODUCTSERIALNUM) END",
             PRODUCTID    "CASE WHEN :PRODUCTID = 'NULL' THEN NULL ELSE RTRIM(:PRODUCTID) END",
             PRODUCTNAME    "CASE WHEN :PRODUCTNAME = 'NULL' THEN NULL ELSE RTRIM(:PRODUCTNAME) END",
             INSURANCECOSERIALNUM    "CASE WHEN :INSURANCECOSERIALNUM = 'NULL' THEN NULL ELSE RTRIM(:INSURANCECOSERIALNUM) END",
             COMMRATE    "CASE WHEN :COMMRATE = 'NULL' THEN NULL ELSE RTRIM(:COMMRATE) END",
             COMMRATE1    "CASE WHEN :COMMRATE1 = 'NULL' THEN NULL ELSE RTRIM(:COMMRATE1) END",
             COMMRATE2    "CASE WHEN :COMMRATE2 = 'NULL' THEN NULL ELSE RTRIM(:COMMRATE2) END",
             PRODUCTCATEGORY    "CASE WHEN :PRODUCTCATEGORY = 'NULL' THEN NULL ELSE RTRIM(:PRODUCTCATEGORY) END",
             CONTRACTID    "CASE WHEN :CONTRACTID = 'NULL' THEN NULL ELSE RTRIM(:CONTRACTID) END",
             MOF   "CASE WHEN :MOF = 'NULL' THEN NULL ELSE RTRIM(:MOF) END",
             PRODUCTVALIDFROM "CASE WHEN :PRODUCTVALIDFROM = 'NULL' THEN NULL ELSE TO_TIMESTAMP(:PRODUCTVALIDFROM,'YYYY-MM-DD HH24:MI:SS.FF3') END",
             PRODUCTVALIDTHRU "CASE WHEN :PRODUCTVALIDTHRU = 'NULL' THEN NULL ELSE TO_TIMESTAMP(:PRODUCTVALIDTHRU,'YYYY-MM-DD HH24:MI:SS.FF3') END",
             SUMASSUMEDTYPE    "CASE WHEN :SUMASSUMEDTYPE = 'NULL' THEN NULL ELSE RTRIM(:SUMASSUMEDTYPE) END",
             PREMIUMTABLE    "CASE WHEN :PREMIUMTABLE = 'NULL' THEN NULL ELSE RTRIM(:PREMIUMTABLE) END",
             BONUSBASETYPE    "CASE WHEN :BONUSBASETYPE = 'NULL' THEN NULL ELSE RTRIM(:BONUSBASETYPE) END",
             BONUSBASERATETYPE    "CASE WHEN :BONUSBASERATETYPE = 'NULL' THEN NULL ELSE RTRIM(:BONUSBASERATETYPE) END",
             CONTRACTBONUS    "CASE WHEN :CONTRACTBONUS = 'NULL' THEN NULL ELSE RTRIM(:CONTRACTBONUS) END",
             COMMRATEA    "CASE WHEN :COMMRATEA = 'NULL' THEN NULL ELSE RTRIM(:COMMRATEA) END",
             INSCOPRODUCTID    "CASE WHEN :INSCOPRODUCTID = 'NULL' THEN NULL ELSE RTRIM(:INSCOPRODUCTID) END",
             INSCOPRODUCTNAME    "CASE WHEN :INSCOPRODUCTNAME = 'NULL' THEN NULL ELSE RTRIM(:INSCOPRODUCTNAME) END",
             MEMO    "CASE WHEN :MEMO = 'NULL' THEN NULL ELSE RTRIM(:MEMO) END",
             COMMRATE_2    "CASE WHEN :COMMRATE_2 = 'NULL' THEN NULL ELSE RTRIM(:COMMRATE_2) END",
             COMMRATEA_2    "CASE WHEN :COMMRATEA_2 = 'NULL' THEN NULL ELSE RTRIM(:COMMRATEA_2) END",
             ADFEERATE    "CASE WHEN :ADFEERATE = 'NULL' THEN NULL ELSE RTRIM(:ADFEERATE) END",
             ADFEERATE1    "CASE WHEN :ADFEERATE1 = 'NULL' THEN NULL ELSE RTRIM(:ADFEERATE1) END",
             ADFEERATE2    "CASE WHEN :ADFEERATE2 = 'NULL' THEN NULL ELSE RTRIM(:ADFEERATE2) END",
             INSBASICCOM    "CASE WHEN :INSBASICCOM = 'NULL' THEN NULL ELSE RTRIM(:INSBASICCOM) END",
             CURRENCY1    "CASE WHEN :CURRENCY1 = 'NULL' THEN NULL ELSE RTRIM(:CURRENCY1) END",
             CURRENCYRATE    "CASE WHEN :CURRENCYRATE = 'NULL' THEN NULL ELSE RTRIM(:CURRENCYRATE) END",
             ADD_FLAG    "CASE WHEN :ADD_FLAG = 'NULL' THEN NULL ELSE RTRIM(:ADD_FLAG) END",
             A_FLAG    "CASE WHEN :A_FLAG = 'NULL' THEN NULL ELSE RTRIM(:A_FLAG) END",
             B_FLAG    "CASE WHEN :B_FLAG = 'NULL' THEN NULL ELSE RTRIM(:B_FLAG) END",
             C_FLAG    "CASE WHEN :C_FLAG = 'NULL' THEN NULL ELSE RTRIM(:C_FLAG) END",
             D_FLAG    "CASE WHEN :D_FLAG = 'NULL' THEN NULL ELSE RTRIM(:D_FLAG) END",
             E_FLAG    "CASE WHEN :E_FLAG = 'NULL' THEN NULL ELSE RTRIM(:E_FLAG) END",
             PRODUCTTYPE1    "CASE WHEN :PRODUCTTYPE1 = 'NULL' THEN NULL ELSE RTRIM(:PRODUCTTYPE1) END",
             COMMRATEC    "CASE WHEN :COMMRATEC = 'NULL' THEN NULL ELSE RTRIM(:COMMRATEC) END",
             ADD_1_START_MONTH    "CASE WHEN :ADD_1_START_MONTH = 'NULL' THEN NULL ELSE RTRIM(:ADD_1_START_MONTH) END",
             ADD_1_END_MONTH    "CASE WHEN :ADD_1_END_MONTH = 'NULL' THEN NULL ELSE RTRIM(:ADD_1_END_MONTH) END",
             ADD_1_COMMISSIONRATE    "CASE WHEN :ADD_1_COMMISSIONRATE = 'NULL' THEN NULL ELSE RTRIM(:ADD_1_COMMISSIONRATE) END",
             ADD_2_START_MONTH    "CASE WHEN :ADD_2_START_MONTH = 'NULL' THEN NULL ELSE RTRIM(:ADD_2_START_MONTH) END",
             ADD_2_END_MONTH    "CASE WHEN :ADD_2_END_MONTH = 'NULL' THEN NULL ELSE RTRIM(:ADD_2_END_MONTH) END",
             ADD_2_COMMISSIONRATE    "CASE WHEN :ADD_2_COMMISSIONRATE = 'NULL' THEN NULL ELSE RTRIM(:ADD_2_COMMISSIONRATE) END",
             ADD_3_START_MONTH    "CASE WHEN :ADD_3_START_MONTH = 'NULL' THEN NULL ELSE RTRIM(:ADD_3_START_MONTH) END",
             ADD_3_COMMISSIONRATE    "CASE WHEN :ADD_3_COMMISSIONRATE = 'NULL' THEN NULL ELSE RTRIM(:ADD_3_COMMISSIONRATE) END",
             CH_FLAG    "CASE WHEN :CH_FLAG = 'NULL' THEN NULL ELSE RTRIM(:CH_FLAG) END",
             ADD_3_END_MONTH    "CASE WHEN :ADD_3_END_MONTH = 'NULL' THEN NULL ELSE RTRIM(:ADD_3_END_MONTH) END",
             ADD_1_COMMRATEA    "CASE WHEN :ADD_1_COMMRATEA = 'NULL' THEN NULL ELSE RTRIM(:ADD_1_COMMRATEA) END",
             ADD_2_COMMRATEA    "CASE WHEN :ADD_2_COMMRATEA = 'NULL' THEN NULL ELSE RTRIM(:ADD_2_COMMRATEA) END",
             ADD_3_COMMRATEA    "CASE WHEN :ADD_3_COMMRATEA = 'NULL' THEN NULL ELSE RTRIM(:ADD_3_COMMRATEA) END",
             COMM1_FLAG    "CASE WHEN :COMM1_FLAG = 'NULL' THEN NULL ELSE RTRIM(:COMM1_FLAG) END",
             COMM2_FLAG    "CASE WHEN :COMM2_FLAG = 'NULL' THEN NULL ELSE RTRIM(:COMM2_FLAG) END",
             MAINT_DATE    "CASE WHEN :MAINT_DATE = 'NULL' THEN NULL ELSE TO_TIMESTAMP(:MAINT_DATE,'YYYY-MM-DD HH24:MI:SS.FF3') END",
             MAINT_USER    "CASE WHEN :MAINT_USER = 'NULL' THEN NULL ELSE RTRIM(:MAINT_USER) END",
             COMM1_ADD_FLAG    "CASE WHEN :COMM1_ADD_FLAG = 'NULL' THEN NULL ELSE RTRIM(:COMM1_ADD_FLAG) END",
             COMM2_ADD_FLAG    "CASE WHEN :COMM2_ADD_FLAG = 'NULL' THEN NULL ELSE RTRIM(:COMM2_ADD_FLAG) END",
             PRODUCTON_DT   "CASE WHEN :PRODUCTON_DT = 'NULL' THEN NULL ELSE TO_TIMESTAMP(:PRODUCTON_DT,'YYYY-MM-DD HH24:MI:SS.FF3') END",
             PRODUCTSHORTNAME    "CASE WHEN :PRODUCTSHORTNAME = 'NULL' THEN NULL ELSE RTRIM(:PRODUCTSHORTNAME) END",
             MAINSTAY_PRODUCT    "CASE WHEN :MAINSTAY_PRODUCT = 'NULL' THEN NULL ELSE RTRIM(:MAINSTAY_PRODUCT) END",
             ADDSUB_FLAG    "CASE WHEN :ADDSUB_FLAG = 'NULL' THEN NULL ELSE RTRIM(:ADDSUB_FLAG) END",
             INSCOPRODUCTID_1    "CASE WHEN :INSCOPRODUCTID_1 = 'NULL' THEN NULL ELSE RTRIM(:INSCOPRODUCTID_1) END",
             INSCOPRODUCTNAME_1    "CASE WHEN :INSCOPRODUCTNAME_1 = 'NULL' THEN NULL ELSE RTRIM(:INSCOPRODUCTNAME_1) END",
             INSCOPRODUCTID_2    "CASE WHEN :INSCOPRODUCTID_2 = 'NULL' THEN NULL ELSE RTRIM(:INSCOPRODUCTID_2) END",
             INSCOPRODUCTNAME_2    "CASE WHEN :INSCOPRODUCTNAME_2 = 'NULL' THEN NULL ELSE RTRIM(:INSCOPRODUCTNAME_2) END",
             INSCOPRODUCTID_3    "CASE WHEN :INSCOPRODUCTID_3 = 'NULL' THEN NULL ELSE RTRIM(:INSCOPRODUCTID_3) END",
             INSCOPRODUCTNAME_3    "CASE WHEN :INSCOPRODUCTNAME_3 = 'NULL' THEN NULL ELSE RTRIM(:INSCOPRODUCTNAME_3) END",
             CHECK_STATUS    "CASE WHEN :CHECK_STATUS = 'NULL' THEN NULL ELSE RTRIM(:CHECK_STATUS) END",
             CHECK_DATE    "CASE WHEN :CHECK_DATE = 'NULL' THEN NULL ELSE TO_TIMESTAMP(:CHECK_DATE,'YYYY-MM-DD HH24:MI:SS.FF3') END",
             CHECK_USER    "CASE WHEN :CHECK_USER = 'NULL' THEN NULL ELSE RTRIM(:CHECK_USER) END",
             CHANNEL    "CASE WHEN :CHANNEL = 'NULL' THEN NULL ELSE RTRIM(:CHANNEL) END",
             POLICY_TYPE    "CASE WHEN :POLICY_TYPE = 'NULL' THEN NULL ELSE RTRIM(:POLICY_TYPE) END",
             COMMRATEC_2    "CASE WHEN :COMMRATEC_2 = 'NULL' THEN NULL ELSE RTRIM(:COMMRATEC_2) END",
             PRODUCTRISK    "CASE WHEN :PRODUCTRISK = 'NULL' THEN NULL ELSE RTRIM(:PRODUCTRISK) END",
             NOBENIFIT    "CASE WHEN :NOBENIFIT = 'NULL' THEN NULL ELSE RTRIM(:NOBENIFIT) END",
             RISKRATE    "CASE WHEN :RISKRATE = 'NULL' THEN NULL ELSE RTRIM(:RISKRATE) END",
             NOOLDAGE   "CASE WHEN :NOOLDAGE = 'NULL' THEN NULL ELSE RTRIM(:NOOLDAGE) END"
           )
